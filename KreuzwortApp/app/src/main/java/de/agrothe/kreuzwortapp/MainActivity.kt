package de.agrothe.kreuzwortapp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.AssetManager
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView
import androidx.activity.ComponentActivity
import de.agrothe.crosswords.web.BodyTplt
import de.agrothe.crosswords.web.CSS
import de.agrothe.crosswords.web.configureSockets
import de.agrothe.kreuzwortapp.ui.theme.KreuzwortAppTheme
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import kotlinx.coroutines.*
import kotlinx.css.CSSBuilder
import java.lang.Thread.sleep
import java.time.Duration
import java.util.concurrent.TimeUnit

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

        WebView(this).let{
            setContentView(it)
            @SuppressLint("SetJavaScriptEnabled")
            it.settings.javaScriptEnabled=true
            it.loadUrl("http://localhost:8080/puzzler")
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
    }
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit){
    this.respondText(CSSBuilder().apply(builder).toString(),
        ContentType.Text.CSS)
}