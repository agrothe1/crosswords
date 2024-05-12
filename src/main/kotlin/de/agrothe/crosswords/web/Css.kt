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
    rule(".${confCss.GRID_TABLE}"){
        border="0.5vh solid blue"
    }
    rule(".${confCss.GRID_TABLE} td"){
        fontFamily="Monospace"
        borderCollapse=BorderCollapse.collapse
    }
    rule(".${confCss.GRID_TABLE_CELL}"){
        fontSize=3.0.vh
        textAlign=TextAlign.end
        display=Display.grid
        gridTemplateColumns=GridTemplateColumns(1.fr, 1.fr)
        gridTemplateRows=GridTemplateRows(3.0.vh, 10.vh)
    }
    rule(".${confCss.PUZZLE_CELL_CHAR}"){
        fontSize=10.vh
        gridColumnStart=GridColumnStart("1")
        gridColumnEnd=GridColumnEnd("3")
    }
    rule(".${confCss.IDX_SLCT_ROT_SOUTH}"){
        height=2.5.vh
        transform.rotate(0.grad)
    }
    rule(".${confCss.IDX_SLCT_ROT_EAST}"){
        height=2.5.vh
        transform.rotate(100.grad)
    }
    rule(".${confCss.IDX_SLCT_ROT_NORTH}"){
        height=2.5.vh
        transform.rotate(200.grad)
    }
    rule(".${confCss.IDX_SLCT_ROT_WEST}"){
        height=2.5.vh
        transform.rotate(300.grad)
    }
}
