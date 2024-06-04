package de.agrothe.crosswords.web

import de.agrothe.crosswords.Puzzle
import de.agrothe.crosswords.config
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
import kotlinx.css.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.jodah.expiringmap.ExpiringMap
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlinx.serialization.encodeToString

fun main(args: Array<String>){
    //io.ktor.server.netty.EngineMain.main(args)
    embeddedServer(Netty, port=8080){
        configureTemplating()
        configureSockets()
    }.start(wait = true)
}

fun Application.configureTemplating(){
    routing{
        get("/styles.css"){
            call.respondCss(CSS)
        }
        get("/puzzler"){
             call.respondHtmlTemplate(BodyTplt()){
                 header{
                     +"Puzzler"
                 }
                 puzzle
             }
        }
        staticResources("/imgs", "imgs")
    }
}

typealias HashCode = Int

@Serializable
data class WSData(
    val inpChar: Char, val xPos: Int, val yPos: Int, val hashCode: HashCode)

private val conf by lazy{config.webApp}

val puzzleCache: MutableMap<HashCode, Puzzle> = ExpiringMap.builder()
    .maxSize(10_000)
    .expiration(1, TimeUnit.HOURS)
    .build()

fun Application.configureSockets(){
    install(WebSockets){
        pingPeriod=Duration.ofSeconds(60) // todo config
        //timeout=Duration.ofSeconds(15)
        maxFrameSize=Long.MAX_VALUE
        contentConverter=KotlinxWebsocketSerializationConverter(Json)
    }
    routing{
        webSocket("/verify"){
            try{
                for(frame in incoming){
                    val f = frame as? Frame.Text ?: continue
                    val wsd=
                        Json.decodeFromString<WSData>(f.readText())
                    println(wsd)
                    puzzleCache.get(wsd.hashCode)?.let{
                        send(Frame.Text(
                            (it.get(wsd.yPos).get(wsd.xPos).equals(
                                wsd.inpChar, true)).toString()))
                    }
                }
            }
            catch(e: Exception){println(e.localizedMessage)}
        }
    }
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit){
    this.respondText(CSSBuilder().apply(builder).toString(),
        ContentType.Text.CSS)
}

