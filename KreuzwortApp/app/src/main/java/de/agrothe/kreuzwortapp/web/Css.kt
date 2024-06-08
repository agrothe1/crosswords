package de.agrothe.crosswords.web

import de.agrothe.crosswords.config
import kotlinx.css.*
import kotlinx.css.properties.*

private val confCss=config.webApp.CSS

val CSS = fun CSSBuilder.(){
    fun String.cls() = ".$this"

    with(confCss){
        val colors = COLOR_PALETTES.random()

        rule("html, body"){
            backgroundColor=Color.white
            //height=100.pct
            //width=100.pct
            //padding="5.vh"
            //boxSizing=BoxSizing.borderBox
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
            paddingRight=1.vh
            fontWeight=FontWeight.normal
            color=colors.CELL_CHAR_COLR
            wordWrap=WordWrap.breakWord
        }
        fun lgndTableHdr(pSel: String, pTop: LinearDimension)
                = rule(pSel.cls()){
            textAlign = TextAlign.left
            textDecoration=TextDecoration(setOf(TextDecorationLine.underline))
            paddingTop=pTop
        }
        lgndTableHdr(LEGEND_TABLE_HEADER, 0.vh)
        lgndTableHdr(LEGEND_TABLE_HEADER_NTH, 2.vh)
        rule(LEGEND_ENTRIES.cls()){
            borderWidth=0.4.vh
            borderColor=colors.GRID_BORDER_COLR.darken(30)
            borderStyle=BorderStyle.none
            borderBottomStyle=BorderStyle.dashed
            hyphens=Hyphens.auto
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
        fun cellChar(pSel: String, pColr: Color) = rule(pSel.cls()){
            fontSize=10.vh
            color=pColr
            backgroundColor=Color.transparent
            borderStyle=BorderStyle.none
            lineHeight=LineHeight("4vh")
            padding="0"
            maxWidth=1.em
            textAlign=TextAlign.center
            maxHeight=1.em
            transition("all", 1.s,
                Timing("cubic-bezier(0.4, 0, 0.2, 1)"), 0.s)
        }
        cellChar(PUZZLE_CELL_CHAR, colors.CELL_CHAR_COLR)
        cellChar(PUZZLE_CELL_CHAR_SOLVED, colors.PUZZLE_CELL_CHAR_SOLVED)
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
