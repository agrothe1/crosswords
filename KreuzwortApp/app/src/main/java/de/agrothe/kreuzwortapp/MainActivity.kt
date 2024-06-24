package de.agrothe.kreuzwortapp

import android.annotation.SuppressLint
import android.content.res.AssetManager
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebView.setWebContentsDebuggingEnabled
import androidx.activity.ComponentActivity
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.css.CSSBuilder
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.jodah.expiringmap.ExpiringMap
import java.lang.ref.WeakReference
import java.time.Duration
import java.util.concurrent.TimeUnit

private val logger by lazy{KotlinLogging.logger{}}

val conf by lazy{config}
val confWeb by lazy{conf.webApp}

private lateinit
var webViewReference: WeakReference<WebView>

@Serializable
data class Game(
    val puzzleGenerated: Puzzle,
    val puzzleInPlay: Puzzle,
    val dimen: Int,
)
{
    override fun equals(other: Any?): Boolean{
        if(this===other) return true
        if(javaClass!=other?.javaClass) return false
        other as Game
        if(!puzzleGenerated.contentDeepEquals(other.puzzleGenerated))
            return false
        if(!puzzleInPlay.contentDeepEquals(other.puzzleInPlay)) return false
        if(dimen!=other.dimen) return false
        return true
    }
    override fun hashCode(): Int{
        var result= puzzleGenerated.contentDeepHashCode()
        result= 31*result + puzzleInPlay.contentDeepHashCode()
        result= 31*result + dimen
        return result
    }
}

var restoredGame: Game? = null

class MainActivity : ComponentActivity(){
    private lateinit var server: NettyApplicationEngine

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        appAssets=assets

        logger.info{"web server starting..."}
        server=embeddedServer(Netty, port=confWeb.PORT){
            configureTemplating()
            configureSockets()
        }.start(wait=false)
        logger.info{"...web server started"}

        if(savedInstanceState!=null){
            //restoredGame =
                savedInstanceState.getString(conf.BUNDLE_KEY)?.apply{
                    logger.debug{"onCreate: $this"}}
            logger.debug{
                "restored saved game: '${restoredGame?.puzzleInPlay}'"}
        }

        webViewReference = WeakReference(
            WebView(this).apply{
                setContentView(this)
                setWebContentsDebuggingEnabled(true)
                with(settings){
                    @SuppressLint("SetJavaScriptEnabled")
                    javaScriptEnabled=true
                    loadWithOverviewMode=true
                    useWideViewPort=true
                    layoutAlgorithm=WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
                }
                loadUrl(confWeb.APP_URL)
            })
    }

    override fun onDestroy(){
        server.stop(100, 200)
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle){
        with(puzzleCache.toSortedMap().entries){
            if(isNotEmpty()){first().value.let{first->
                outState.putString(conf.BUNDLE_KEY,
                    Json.encodeToString(
                        Game(first.puzzleGenerated,
                            first.puzzleInPlay, 4)
                    ).apply{
                        logger.debug{"onSaveInstanceState: save game: $this"}}
                )
            }}
        }
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle){
        logger.debug{"onRestoreInstanceState: $this"}
        savedInstanceState.getString(conf.BUNDLE_KEY)?.run{
            logger.debug{"onRestoreInstanceState: $this"}
            restoredGame = Json.decodeFromString<Game>(this)
        }
        super.onRestoreInstanceState(savedInstanceState)
    }

    companion object{
        lateinit var appAssets: AssetManager
    }
}

typealias HashCode = Int

@Serializable
data class WSDataToSrvr(
    val inpChar: Char='#', val xPos: Int=0, val yPos: Int=0,
    val hashCode: HashCode=0, val newGame: Boolean=false)

@Serializable
data class WSDataFromSrvr(
    val charSolved: Boolean, val rowSolved: Boolean, val colSolved: Boolean)

data class PuzzleCacheEntry(
    val puzzleGenerated: Puzzle,
    var puzzleInPlay: Puzzle
){
    override fun equals(other: Any?): Boolean{
        if(this===other) return true
        if(javaClass!=other?.javaClass) return false
        other as PuzzleCacheEntry
        if (!puzzleGenerated.contentDeepEquals(other.puzzleGenerated)) return false
        if (!puzzleInPlay.contentDeepEquals(other.puzzleInPlay)) return false
        return true
    }
    override fun hashCode(): Int {
        var result= puzzleGenerated.contentDeepHashCode()
        result = 31 * result + puzzleInPlay.contentDeepHashCode()
        return result
    }
}

val puzzleCache: ExpiringMap<HashCode, PuzzleCacheEntry> = ExpiringMap.builder()
    .maxSize(confWeb.PUZZLE_CACHE_MAX_SIZE)
    .expiration(confWeb.PUZZLE_CACHE_EXPIRATION_MINS, TimeUnit.MINUTES)
    .build()

fun Application.configureSockets(){
    install(WebSockets){
        pingPeriod=Duration.ofSeconds(confWeb.WS_PING_PERIODS_SECS)
        //timeout=Duration.ofSeconds(15)
        maxFrameSize=Long.MAX_VALUE
        contentConverter=KotlinxWebsocketSerializationConverter(Json)
    }
    routing{
        webSocket(confWeb.WEB_SOCK_ENDPOINT){
            try{
                for(frame in incoming){
                    val f = frame as? Frame.Text ?: continue
                    val wsd=
                        Json.decodeFromString<WSDataToSrvr>(f.readText())
                    logger.debug{"ws data recvd: '$wsd'"}
                    if(wsd.newGame){
                        webViewReference.get()?.apply{post{
                            loadUrl(confWeb.APP_URL)
                        }}
                    }else{
                        puzzleCache[wsd.hashCode]?.let{wsd.apply{
                            it.puzzleGenerated[xPos][yPos]
                                .equals(inpChar, true)
                                    .let{correctChar->
                                        if(correctChar){
                                            it.puzzleInPlay[xPos][yPos]=inpChar
                                        }
                                        send(Frame.Text(
                                            Json.encodeToString(WSDataFromSrvr(
                                                correctChar,
                        it.puzzleGenerated.getStringAt(Axis.X, xPos)
                            .equals(it.puzzleInPlay.getStringAt(Axis.X, xPos)),
                        it.puzzleGenerated.getStringAt(Axis.Y, yPos)
                            .equals(it.puzzleInPlay.getStringAt(Axis.Y, yPos))
                                    ))))
                        }}}
                    }
                }
            }
            catch(e: Exception){println(e.localizedMessage)}
        }
    }
}

fun Application.configureTemplating(){
    routing{
        get("/styles.css"){
            call.respondCss(CSS)
        }
        get("/puzzler"){
             call.respondHtmlTemplate(BodyTplt()){
                 /*
                 header{
                     +"Puzzler"
                 }
                  */
                 puzzle
             }
        }
        staticResources("/imgs", "/imgs")
    }
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit){
    this.respondText(CSSBuilder().apply(builder).toString(),
        ContentType.Text.CSS)
}