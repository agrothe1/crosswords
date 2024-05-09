package de.agrothe.crosswords.web

import kotlinx.css.*
import kotlinx.css.properties.*

val CSS = fun CSSBuilder.(){
    rule("body"){
        backgroundColor=Color.white
        margin(5.vh)
    }
    rule("h1.page-title"){
        color=Color.blue
        fontSize=50.vh
    }
    rule("table"){
        color=Color.black
        fontSize=10.vh
        //border="1.vh solid black"
        borderCollapse=BorderCollapse.collapse
    }
    rule("td"){
        border="0.3vh solid black"
    }
    rule("td"){
        textAlign=TextAlign.center
        fontFamily="Monospace"
    }
    rule(".idx"){
        fontSize=3.0.vh
        fontFamily="Monospace"
        fontWeight=FontWeight.bolder
        fontStyle=FontStyle.italic
        color=rgb(233, 30, 99)
        zIndex=1
        position=Position.absolute
        justifyContent=JustifyContent.spaceBetween
    }
    rule(".idx>img"){
        height=3.0.vh
        position=Position.relative
    }
    rule(".idx>img.idxImageRotateLeft"){
        transform.rotate(100.grad)
    }
    rule(".idx>img.idxImageRotateUp"){
        transform.rotate(200.grad)
    }
    rule(".idx>img.idxImageRotateRight"){
        transform.rotate(300.grad)
    }
}
