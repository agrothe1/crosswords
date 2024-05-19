package de.agrothe.crosswords.web

import de.agrothe.crosswords.config
import kotlinx.css.*
import kotlinx.css.properties.*

private val confCss=config.webApp.CSS

val CSS = fun CSSBuilder.(){
    with(confCss){
        val colors = COLOR_PALETTES.random()

        rule("body"){
            backgroundColor=Color.white
            margin(5.vh)
        }
        rule("h1"){
            color=Color.blue
            fontSize=5.vh
        }
        rule(".${GRID_TABLE}"){
            fontFamily="Monospace"
            border="0.6vh solid ${colors.GRID_BORDER_COLR}"
            borderRadius=2.vh
            borderCollapse=BorderCollapse.collapse
        }
        rule(".${GRID_TABLE_COL}"){
            border="0.4vh solid ${colors.GRID_LINES_COLR}"
        }
        rule(".${TABLE_CELL_BACKGROUND}1"){
            backgroundColor=Color.floralWhite.lighten((0..3).random())
        }
        rule(".${TABLE_CELL_BACKGROUND}2"){
            backgroundColor=Color.antiqueWhite.lighten((5..7).random())
        }
        rule(".${PUZZLE_CELL_IDX_NUM}"){
            fontSize=3.0.vh
            fontWeight=FontWeight.bolder
            lineHeight=LineHeight("0.5vh")
            color=colors.IDX_NUM_COLR
        }
        rule(".${PUZZLE_CELL_CHAR}"){
            fontSize=10.vh
            lineHeight=LineHeight("4vh")
            color=colors.CELL_CHAR_COLR
        }
        rule(".${IDX_SLCT_ROT_SOUTH}"){
            height=2.5.vh
            transform.rotate(0.grad)
        }
        rule(".${IDX_SLCT_ROT_EAST}"){
            height=2.5.vh
            transform.rotate(100.grad)
        }
        rule(".${IDX_SLCT_ROT_NORTH}"){
            height=2.5.vh
            transform.rotate(200.grad)
        }
        rule(".${IDX_SLCT_ROT_WEST}"){
            height=2.5.vh
            transform.rotate(300.grad)
        }
    }
}
