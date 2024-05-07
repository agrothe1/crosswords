package de.agrothe.crosswords.web

import io.ktor.http.*import kotlinx.css.*
import kotlinx.html.InputType

val CSS = fun CSSBuilder.(){
    rule("body"){
        backgroundColor=Color.white
        margin(10.px)
    }
    rule("h1.page-title"){
        color=Color.blue
        fontSize=1.5.em
    }
    rule("table"){
        color=Color.black
        fontSize=2.em
        border="2px solid black"
        borderCollapse=BorderCollapse.collapse
    }
    rule("tr, td"){
        border="1px solid black"
    }
    rule("td"){
        textAlign=TextAlign.center
        fontFamily="Monospace"
    }
    rule(".idx"){
        fontSize=0.4.em
        fontFamily="Monospace"
        fontWeight=FontWeight.bolder
        fontStyle=FontStyle.italic
        color=rgb(233, 30, 99)
        zIndex=1
        position=Position.absolute
    }
    rule(".idx"){
        backgroundImage=Image("url(imgs/triangle-outline.svg)")
        backgroundSize="cover"

    }
}
