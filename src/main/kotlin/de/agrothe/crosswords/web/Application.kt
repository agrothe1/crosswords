package de.agrothe.crosswords.web

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.css.*

fun main(args: Array<String>){
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module(){
    configureTemplating()
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

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(),
        ContentType.Text.CSS)
}

