package de.agrothe.kreuzwortapp

import android.annotation.SuppressLint
import android.content.res.AssetManager
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebView.setWebContentsDebuggingEnabled
import androidx.activity.ComponentActivity
import de.agrothe.crosswords.web.BodyTplt
import de.agrothe.kreuzwortapp.web.CSS
import de.agrothe.crosswords.web.configureSockets
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.css.CSSBuilder

private val logger by lazy{KotlinLogging.logger{}}

class MainActivity : ComponentActivity(){
    private lateinit var server: NettyApplicationEngine

override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        appAssets=assets

        logger.info{"web server starting..."}
        server=embeddedServer(Netty, port=8080){
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
            loadUrl("http://localhost:8080/puzzler")
        }
    }

    override fun onDestroy(){
        server.stop(0, 0)
        super.onDestroy()
    }

    companion object{
        lateinit var appAssets: AssetManager
    }
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
        staticResources("/imgs", "/imgs")
    }
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit){
    this.respondText(CSSBuilder().apply(builder).toString(),
        ContentType.Text.CSS)
}