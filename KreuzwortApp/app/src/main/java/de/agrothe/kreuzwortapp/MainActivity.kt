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
import java.time.Duration
import java.util.concurrent.TimeUnit

private val logger by lazy{KotlinLogging.logger{}}

val conf by lazy{config.webApp}

class MainActivity : ComponentActivity(){
    private lateinit var server: NettyApplicationEngine

override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        appAssets=assets

        logger.info{"web server starting..."}
        server=embeddedServer(Netty, port=conf.PORT){
            configureTemplating()
            configureSockets()
        }.start(wait=false)
        logger.info{"...web server started"}

        WebView(this).run{
            setContentView(this)
            setWebContentsDebuggingEnabled(true)
            with(settings){
                @SuppressLint("SetJavaScriptEnabled")
                javaScriptEnabled=true
                loadWithOverviewMode=true
                useWideViewPort=true
                layoutAlgorithm=WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
            }
            loadUrl(conf.APP_URL)
        }
    }

    override fun onDestroy(){
        server.stop(100, 200)
        super.onDestroy()
    }

    companion object{
        lateinit var appAssets: AssetManager
    }
}

typealias HashCode = Int

@Serializable
data class WSDataToSrvr(
    val inpChar: Char, val xPos: Int, val yPos: Int,
    val hashCode: HashCode)

@Serializable
data class WSDataFromSrvr(
    val charSolved: Boolean, val rowSolved: Boolean, val colSolved: Boolean)

data class PuzzleCacheEntry(
    val puzzleGenerated: Puzzle,
    var puzzleInPlay: Puzzle
)

val puzzleCache: MutableMap<HashCode, PuzzleCacheEntry> = ExpiringMap.builder()
    .maxSize(conf.PUZZLE_CACHE_MAX_SIZE)
    .expiration(conf.PUZZLE_CACHE_EXPIRATION_MINS, TimeUnit.MINUTES)
    .build()

fun Application.configureSockets(){
    install(WebSockets){
        pingPeriod=Duration.ofSeconds(conf.WS_PING_PERIODS_SECS)
        //timeout=Duration.ofSeconds(15)
        maxFrameSize=Long.MAX_VALUE
        contentConverter=KotlinxWebsocketSerializationConverter(Json)
    }
    routing{
        webSocket(conf.WEB_SOCK_ENDPOINT){
            try{
                for(frame in incoming){
                    val f = frame as? Frame.Text ?: continue
                    val wsd=
                        Json.decodeFromString<WSDataToSrvr>(f.readText())
                    logger.debug{"ws data recvd: '$wsd'"}
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