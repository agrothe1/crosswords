package de.agrothe.crosswords.web

import de.agrothe.crosswords.config
import kotlinx.css.*
import kotlinx.css.properties.*

private val confCss=config.webApp.CSS

val CSS = fun CSSBuilder.(){
    rule("body"){
        backgroundColor=Color.white
        margin(5.vh)
    }
    rule("h1"){
        color=Color.blue
        fontSize=5.vh
    }
    rule(".gridTable"){
        border="0.5vh solid blue"
        //borderCollapse=BorderCollapse.collapse
    }
    rule(".gridTable td"){
        //border="0.3vh solid black"
        fontFamily="Monospace"
        borderCollapse=BorderCollapse.collapse
    }
    rule(".gridTable td"){
        borderStyle=BorderStyle.none
        //fontSize=3.0.vh
        //fontFamily="Monospace"
        //fontWeight=FontWeight.bolder
        //fontStyle=FontStyle.italic
        //color=rgb(233, 30, 99)
        //zIndex=1
    }
    rule(".puzzleRow"){
        //gridAutoRows= GridAutoRows.auto
        //gridTemplateAreas=GridTemplateAreas("idx . dir")
        //gridTemplateAreas=GridTemplateAreas("lett")
        //gridTemplateColumns=GridTemplateColumns(1.0.fr, 1.0.fr, 1.0.fr)
        //gridTemplateRows=GridTemplateRows()
    }
    rule(".gridTableCell"){
        display=Display.grid
        gridTemplateColumns=GridTemplateColumns(1.fr, 1.fr)
        gridTemplateRows=GridTemplateRows(3.0.vh, 10.vh)
    }
    rule(".puzzleCellIdxNum"){
        fontSize=3.0.vh
    }
    rule(".puzzleCellChar"){
        fontSize=10.vh
        gridColumnStart=GridColumnStart("1")
        gridColumnEnd=GridColumnEnd("3")
    }
    //"gridTableCell"
    rule(".gridTableCell"){
        fontSize=3.0.vh
        textAlign=TextAlign.end
    }
    rule(".idxImageRotateSouth"){
        height=2.5.vh
        transform.rotate(0.grad)
    }
    rule("idxImageRotateEast"){
        height=2.5.vh
        transform.rotate(100.grad)
    }
    rule(".idxImageRotateNorth"){
        transform.rotate(200.grad)
        height=2.5.vh
    }
    rule(".idxImageRotateWest"){
        height=2.5.vh
        transform.rotate(300.grad)
    }
}
