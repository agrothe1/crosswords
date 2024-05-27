package de.agrothe.crosswords.web

import de.agrothe.crosswords.config
import kotlinx.css.*
import kotlinx.css.properties.*

private val confCss=config.webApp.CSS

val CSS = fun CSSBuilder.(){
    fun String.cls() = ".$this"

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
        rule(PUZZLE_TABLE.cls()){
            verticalAlign=VerticalAlign.top
        }
        rule(LEGEND_TABLE.cls()){
            fontFamily="sans-serif"
            fontSize=3.3.vh
            fontWeight=FontWeight.normal
            //whiteSpace=WhiteSpace.nowrap
            wordWrap=WordWrap.breakWord
        }
        rule(LEGEND_TABLE_HEADER.cls()){
            textAlign = TextAlign.left
            textDecoration=TextDecoration(setOf(TextDecorationLine.underline))
        }
        rule(GRID_TABLE.cls()){
            fontFamily="monospace"
            borderWidth=0.6.vh
            borderStyle=BorderStyle.solid
            borderColor=colors.GRID_BORDER_COLR
            borderRadius=2.vh
            borderCollapse=BorderCollapse.collapse
        }
        rule(GRID_TABLE_COL.cls()){
            borderWidth=0.4.vh
            borderStyle=BorderStyle.solid
            borderColor=colors.GRID_LINES_COLR
        }
        rule((TABLE_CELL_BACKGROUND+"1").cls()){
            backgroundColor=Color.floralWhite.lighten((1..3).random())
        }
        rule((TABLE_CELL_BACKGROUND+"2").cls()){
            backgroundColor=Color.antiqueWhite.lighten((5..7).random())
        }
        rule(PUZZLE_CELL_IDX_NUM.cls()){
            height=3.0.vh
            fontSize=3.0.vh
            fontWeight=FontWeight.bolder
            lineHeight=LineHeight("0.5vh")
            color=colors.IDX_NUM_COLR
        }
        rule(PUZZLE_CELL_CHAR.cls()){
            fontSize=10.vh
            color=colors.CELL_CHAR_COLR
            backgroundColor=Color.transparent
            borderStyle=BorderStyle.none
            lineHeight=LineHeight("4vh")
            padding="0"
            maxWidth=1.em
            textAlign=TextAlign.center
            maxHeight=1.em
        }
        rule(IDX_SLCT_ROT_SOUTH.cls()){
            height=2.5.vh
            transform.rotate(0.grad)
        }
        rule(IDX_SLCT_ROT_EAST.cls()){
            height=2.5.vh
            transform.rotate(100.grad)
        }
        rule(IDX_SLCT_ROT_NORTH.cls()){
            height=2.5.vh
            transform.rotate(200.grad)
        }
        rule(IDX_SLCT_ROT_WEST.cls()){
            height=2.5.vh
            transform.rotate(300.grad)
        }
    }
}
