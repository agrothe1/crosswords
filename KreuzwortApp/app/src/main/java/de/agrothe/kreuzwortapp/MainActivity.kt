package de.agrothe.kreuzwortapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import android.net.http.SslError
import android.os.Bundle
import android.webkit.*
import android.webkit.WebView.setWebContentsDebuggingEnabled
import androidx.activity.ComponentActivity
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.network.tls.certificates.*
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
import java.security.PublicKey
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
        var result=puzzleGenerated.contentDeepHashCode()
        result=31*result+puzzleInPlay.contentDeepHashCode()
        result=31*result+dimen
        return result
    }
}

lateinit var sharedPrefs: SharedPreferences

fun readSolvedGamesCnt(): Int =
    sharedPrefs.getInt(confWeb.SHRD_PRFS_NUM_SLVD_GMES_CNT_KEY, 0)

fun saveSolvedGamesCnt(pCnt: Int) =
    with(sharedPrefs.edit()){
        putInt(confWeb.SHRD_PRFS_NUM_SLVD_GMES_CNT_KEY, pCnt)
        apply() // persist changes
    }

fun readPuzzleType(): PuzzleType =
    sharedPrefs.getString(confWeb.SHRD_PRFS_PUZZLE_TYPE,
            PuzzleType.SCHWEDEN.name)
        ?.let{PuzzleType.valueOf(it)} ?: PuzzleType.SCHWEDEN


fun savePuzzleType(pType: PuzzleType) =
    with(sharedPrefs.edit()){
        putString(confWeb.SHRD_PRFS_PUZZLE_TYPE, pType.name)
        apply() // persist changes
    }

@Serializable
data class GameHistoryEntry(
    val puzzleId: String,
    val timeStamp: Long)
{
    constructor(pPuzzleId: String) :
        this(pPuzzleId, System.currentTimeMillis())
}

fun readPuzzleHistory(): List<GameHistoryEntry> =
    try{
        sharedPrefs.getStringSet(confWeb.SHRD_PRFS_PUZZLE_HISTORY_KEY,
                mutableSetOf<String>())
            ?.map{Json.decodeFromString<GameHistoryEntry>(it)}
            ?.sortedBy{it.timeStamp}
            ?.mapIndexed{idx, elm->
                logger.debug{"read from history $idx: '$elm'"}
                elm}
            .orEmpty()
    }
    catch(e: Exception){
        logger.error{"failed 'readPuzzleHistory' '$e'"}
        emptyList()
    }

fun savePuzzleHistory(pId: String) =
    try{
        with(sharedPrefs.edit()){
            putStringSet(confWeb.SHRD_PRFS_PUZZLE_HISTORY_KEY,
                readPuzzleHistory()
                    .take(confWeb.SHRD_PRFS_PUZZLE_HISTORY_SIZE-1)
                    .toMutableList()
                    .apply{add(0, GameHistoryEntry(pId))}
                    .map{Json.encodeToString(it)}
                    .toMutableSet()
                    .apply{forEachIndexed{idx, elm->
                        logger.debug{"saved to history $idx: '$elm'"}}
                    }
            )
            apply() // persist changes
        }
    }
    catch(e: Exception){
        logger.error{"failed 'savePuzzleHistory' '$e'"}
    }

fun readPuzzleDimen(): Int =
    sharedPrefs.getInt(confWeb.SHRD_PRFS_PUZZLE_DIMEN_KEY,
        config.puzzle.DEFAULT_PUZZLE_DIMEN)

fun savePuzzleDimen(pCnt: Int) = // todo
    with(sharedPrefs.edit()){
        putInt(confWeb.SHRD_PRFS_PUZZLE_DIMEN_KEY, pCnt)
        apply() // persist changes
    }

var restoredGame: Game? = null

class MainActivity : ComponentActivity(){
    private lateinit var server: NettyApplicationEngine

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        appAssets=assets

        logger.info{"web server starting..."}
        var myPubKey: PublicKey? = null
        confWeb.CERT_NAME.let{certName->
            certName.toCharArray().let{certNameArr->
                server=embeddedServer(Netty,
                    environment=applicationEngineEnvironment{
                        sslConnector(
                            keyStore=buildKeyStore{
                                certificate(certName){
                                    password=certName
                                    domains=listOf("localhost")
                                    daysValid=confWeb.CERT_DAYS_VALID
                                }}.apply{
                                    myPubKey=getCertificate(certName).publicKey},
                            keyAlias=certName,
                            keyStorePassword={certNameArr},
                            privateKeyPassword={certNameArr}
                        )
                        {port=confWeb.PORT}
                    })
                .apply{
                    application.run{
                        configureTemplating()
                        configureSockets()
                    }
                }
                .start(wait=false)}}
        logger.info{"...web server started"}

        if(savedInstanceState!=null){
            //restoredGame = todo
            savedInstanceState.getString(conf.BUNDLE_KEY)?.run{
                logger.debug{"onCreate: $this"}
                //Json.decodeFromString<Game>(this) todo
            }
            logger.debug{
                "restored saved game: '${restoredGame?.puzzleInPlay}'"}
        }

        sharedPrefs=getSharedPreferences(
            conf.webApp.SHARED_PREFS_NAME, Context.MODE_PRIVATE)

        webViewReference = WeakReference(
            WebView(this).apply{
                webViewClient=SslWebView(myPubKey)
                setContentView(this)
                setWebContentsDebuggingEnabled(true)
                with(settings){
                    @SuppressLint("SetJavaScriptEnabled")
                    javaScriptEnabled=true
                    loadWithOverviewMode=true
                    useWideViewPort=true
                    layoutAlgorithm=WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
                }

                val displayMetrics = resources.displayMetrics
                val dpHeight = displayMetrics.heightPixels /
                    displayMetrics.density
                val dpWidth = displayMetrics.widthPixels /
                    displayMetrics.density
                logger.debug{"dpHeight: $dpHeight, dpWidth: $dpWidth"}

                loadUrl(String.format(confWeb.APP_URL, readPuzzleDimen(),
                    readPuzzleType()))
            })
    }

    override fun onDestroy(){
        server.stop(100, 200)
        super.onDestroy()
    }

    /*override*/ fun XonSaveInstanceState(outState: Bundle){ // todo
        super.onSaveInstanceState(outState)
        with(puzzleCache.toSortedMap().entries){
            if(isNotEmpty()){first().value.let{first->
                outState.putString(conf.BUNDLE_KEY,
                    Json.encodeToString(
                        Game(first.puzzleGenerated,
                            first.puzzleInPlay, 4) // todo dimen
                    ).apply{
                        logger.debug{"onSaveInstanceState: save game: $this"}}
                )
            }}
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle){
        super.onRestoreInstanceState(savedInstanceState)
        logger.debug{"onRestoreInstanceState: $this"}
        savedInstanceState.getString(conf.BUNDLE_KEY)?.run{
            logger.debug{"onRestoreInstanceState: $this"}
            //restoredGame = Json.decodeFromString<Game>(this) // todo
        }
    }

    companion object{
        lateinit var appAssets: AssetManager
    }
}

typealias HashCode = Int

@Serializable
data class WSDataToSrvr(
    val inpChar: Char='#', val xPos: Int=-1, val yPos: Int=-1,
    val hashCode: HashCode=-1,
    val newGame: Boolean=false, val dimen: Int=-1,
    val showHelp: Boolean=false,
    val puzzleType: String="NOT SET")

@Serializable
data class WSDataFromSrvrPlcHldrs(
    val selctr: String, val plcHldr: Char,
)

@Suppress("ArrayInDataClass")
@Serializable
data class WSDataFromSrvr(
    val rowSolved: Boolean=false, val colSolved: Boolean=false,
    val puzzleSolved: Boolean=false,
    val showPlaceholders: Array<WSDataFromSrvrPlcHldrs>?=null,
    val puzzleType: PuzzleType=PuzzleType.SCHWEDEN)

data class PuzzleCacheEntry(
    val puzzleGenerated: Puzzle,
    var puzzleInPlay: Puzzle
){
    override fun equals(other: Any?): Boolean{
        if(this===other) return true
        if(javaClass!=other?.javaClass) return false
        other as PuzzleCacheEntry
        if(!puzzleGenerated.contentDeepEquals(other.puzzleGenerated))
            return false
        if(!puzzleInPlay.contentDeepEquals(other.puzzleInPlay)) return false
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
        maxFrameSize=Long.MAX_VALUE
        contentConverter=KotlinxWebsocketSerializationConverter(Json)
    }

    fun WSDataToSrvr.getHelp() =
        WSDataFromSrvr(showPlaceholders=puzzleCache[hashCode]?.run{
            puzzleGenerated.run{
                diff(puzzleInPlay).shuffled().run{
                    take(size/confWeb.HELP_PROBABILITY+1).map{
                            (rowIdx, colIdx, chr)->
                        WSDataFromSrvrPlcHldrs(
                        "[id=\"${rowIdx}_${colIdx}\"]", chr)
        }}}}?.toTypedArray())

    routing{
        webSocket(confWeb.WEB_SOCK_ENDPOINT){
            try{
                for(frame in incoming){
                    val f = frame as? Frame.Text ?: continue
                    Json.decodeFromString<WSDataToSrvr>(f.readText()).run{
                        logger.debug{"ws data recvd: '$this'"}
                        if(showHelp){
                            send(Frame.Text(Json.encodeToString(getHelp())))
                        }else if(newGame){
                            savePuzzleDimen(dimen)
                            savePuzzleType(PuzzleType.valueOf(puzzleType))
                            webViewReference.get()?.apply{post{
                                with(String.format(confWeb.APP_URL,
                                    dimen, puzzleType)){
                                        logger.debug{
                                            "ws data post load url: '$this'"}
                                        loadUrl(this)
                            }}}}
                        else{
                            puzzleCache[hashCode]?.run{
                                logger.debug{"inpChar: '$inpChar'"}
                                puzzleInPlay[xPos][yPos]=inpChar
                                if(puzzleGenerated[xPos][yPos]
                                    .equals(inpChar, true)){
                                (puzzleGenerated.getStringAt(Axis.X, xPos)==
                                    puzzleInPlay.getStringAt(Axis.X, xPos))
                                        .let{correctRow->
                                (puzzleGenerated.getStringAt(Axis.Y, yPos)==
                                    puzzleInPlay.getStringAt(Axis.Y, yPos))
                            .let{correctCol->
                                send(Frame.Text(Json.encodeToString(
                                    WSDataFromSrvr(correctRow,
                                        correctCol, (correctRow && correctCol
                                            && puzzleGenerated
                                                .sameContent(puzzleInPlay))
                                                    .apply{
                                            if(this) readSolvedGamesCnt().run{
                                                saveSolvedGamesCnt(this+1)
                                                    }}
                                    )
                                )))
                        }}}}}
                    }
                }
            }
            catch(e: Exception){
                logger.debug{"failed on incoming WS: '${e.message}'"}
            }
        }
    }
}

fun Application.configureTemplating(){
    routing{
        get("/styles.css"){
            call.respondCss(CSS)
        }
        get("/puzzler"){
            call.request.queryParameters.let{params->
                (params[confWeb.DIMEN_PARAM_NAME]?.toInt()
                        ?: conf.puzzle.DEFAULT_PUZZLE_DIMEN).let{dimen->
                    (params[confWeb.TYPE_PARAM_NAME]
                        ?: conf.puzzle.DEFAULT_PUZZLE_TYPE.name).let{type->
                logger.debug{"dimenParamName:'$dimen', typeParamName: '$type'"}
                call.respondHtmlTemplate(
                    BodyTplt(readSolvedGamesCnt(), dimen,
                        readPuzzleHistory().map{it.puzzleId},
                        PuzzleType.valueOf(type.uppercase()))
                ){
                    puzzle
                    // dph 866.2857 x dpw 411.42856 = 2,10 dp
                    /* 1080x2400 2,22 */
                }
            }}}
        }
        staticResources("/css", "/css")
        staticResources("/imgs", "/imgs")
    }
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit){
    this.respondText(CSSBuilder().apply(builder).toString(),
        ContentType.Text.CSS)
}

class SslWebView(private val pubKey: PublicKey?): WebViewClient(){
    @SuppressLint("WebViewClientOnReceivedSslError")
    override
    fun onReceivedSslError(pView: WebView?, pHandler: SslErrorHandler,
            pError: SslError?){
        logger.info{"onReceivedSslError: $pError"}
        with(pHandler){
            try{
                pError?.certificate?.x509Certificate?.verify(pubKey)
                logger.info{"onReceivedSslError: verified key ok"}
                proceed()
            }
            catch (e: Exception){
                logger.error{"onReceivedSslError, unverified key: $e"}
            }
            cancel()
        }
    }

    override
    fun onReceivedError(pView: WebView?,
            pRequest: WebResourceRequest?, pError: WebResourceError?){
        logger.error{"onReceivedError: ${pError?.description}"}
        super.onReceivedError(pView, pRequest, pError)
    }

    override
    fun onReceivedClientCertRequest(pView: WebView?,
        pRequest: ClientCertRequest)
    {
        logger.info{"onReceivedClientCertRequest: $pRequest"}
        super.onReceivedClientCertRequest(pView, pRequest)
    }
}
