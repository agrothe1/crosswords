package de.agrothe.crosswords.web

import io.ktor.http.*
import io.ktor.network.sockets.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.css.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger
import io.ktor.serialization.kotlinx.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

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
             call.respondHtmlTemplate(BodyTplt()) {
                 header{
                     +"Puzzler"
                 }
                 puzzle
             }
        }
        staticResources("/imgs", "imgs")
    }
}

@Serializable
data class WSData(
    val cellChar: Char, val inpChar: Char,
    val xPos: Int, val yPos: Int)

fun Application.configureSockets(){
    install(WebSockets){
        pingPeriod=Duration.ofSeconds(15)
        timeout=Duration.ofSeconds(15)
        maxFrameSize=Long.MAX_VALUE
        contentConverter=KotlinxWebsocketSerializationConverter(Json)
    }
    routing{
        webSocket("/verify"){
            try{
                for(frame in incoming){
                    val f = frame as? Frame.Text ?: continue
                    val wsData=
                        Json.decodeFromString<WSData>(f.readText())
                    println(wsData)
                }
            }catch(e: Exception){
                println(e.localizedMessage)
            }finally{
            }
        }
    }
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit){
    this.respondText(CSSBuilder().apply(builder).toString(),
        ContentType.Text.CSS)
}

