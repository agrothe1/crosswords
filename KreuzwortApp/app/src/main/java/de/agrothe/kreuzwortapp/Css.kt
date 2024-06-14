package de.agrothe.kreuzwortapp

import kotlinx.css.*
import kotlinx.css.properties.*

private val confCss=config.webApp.CSS

val CSS = fun CSSBuilder.(){
    fun String.cls() = ".$this"

    with(confCss){
        val colors = COLOR_PALETTES.random()

        rule("html, body"){
            backgroundColor=Color.white
            //backgroundImage=
            //height=100.pct
            //width=100.pct
            //padding="5.vh"
            //boxSizing=BoxSizing.borderBox
        }
        rule("h1"){
            color=Color.blue
            fontSize=5.vh
        }
        rule(PUZZLE_GRID.cls()){
            display=Display.grid
            gridTemplateColumns=GridTemplateColumns(
                LinearDimension("1fr"), LinearDimension("1fr"))
            gridTemplateRows=GridTemplateRows(
                LinearDimension("1fr"), LinearDimension("5fr"))
        }
        rule(LGND_GRID_HORIZ.cls()){
            gridColumnStart=GridColumnStart("1")
            gridColumnEnd=GridColumnEnd("2")
            gridRowStart=GridRowStart("1")
            gridRowEnd=GridRowEnd("2")
        }
        rule(LGND_GRID_VERT.cls()){
            gridColumnStart=GridColumnStart("2")
            gridColumnEnd=GridColumnEnd("3")
            gridRowStart=GridRowStart("1")
            gridRowEnd=GridRowEnd("2")
            marginLeft=LinearDimension.auto
            marginRight=LinearDimension("0")
        }
        rule(FIELD_GRID.cls()){
            gridColumnStart=GridColumnStart("1")
            gridColumnEnd=GridColumnEnd("3")
            gridRowStart=GridRowStart("2")
            gridRowEnd=GridRowEnd("3")
            paddingTop=0.7.vh
        }
        rule(LGND_TABLE.cls()){
            fontFamily="sans-serif"
            fontSize=2.6.vh
            margin="auto"
            paddingTop=1.vh
            paddingRight=1.vh
            fontWeight=FontWeight.normal
            color=colors.CELL_CHAR_COLR
            wordWrap=WordWrap.breakWord
        }
        fun lgndTableHdr(pSel: String, pTop: LinearDimension)
                = rule(pSel.cls()){
            textAlign=TextAlign.left
            textDecoration=TextDecoration(setOf(TextDecorationLine.underline))
            paddingTop=pTop
        }
        lgndTableHdr(LGND_TABLE_HEADER, 0.vh)
        lgndTableHdr(LGND_TABLE_HEADER_NTH, 2.vh)
        fun lgndEntries(pSel: String,
            pTextDecoLine: TextDecorationLine, pBorderBottomStyle: BorderStyle)
                = rule(pSel.cls()){
            borderWidth=0.4.vh
            borderColor=colors.GRID_BORDER_COLR.darken(30)
            borderStyle=BorderStyle.none
            borderBottomStyle=pBorderBottomStyle
            textDecoration=TextDecoration(setOf(pTextDecoLine))
            hyphens=Hyphens.auto
        }
        lgndEntries(LGND_ENTRIES,
            TextDecorationLine.unset, BorderStyle.dashed)
        lgndEntries(LGND_ENTRIES+LGND_ENTRIES_SOLVED_SUFFX,
            TextDecorationLine.lineThrough, BorderStyle.dashed)
        lgndEntries(LGND_ENTRIES_LAST,
            TextDecorationLine.unset, BorderStyle.none)
        lgndEntries(LGND_ENTRIES_LAST+LGND_ENTRIES_SOLVED_SUFFX,
            TextDecorationLine.lineThrough, BorderStyle.none)
        rule(GRID_TABLE.cls()){
            fontFamily="monospace"
            borderWidth=0.6.vh
            borderStyle=BorderStyle.solid
            borderColor=colors.GRID_BORDER_COLR
            borderRadius=2.vh
            borderCollapse=BorderCollapse.collapse
            margin="auto"
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
        fun cellIdxNum(pSel: String, pLineHeight: LineHeight)
                = rule(pSel.cls()){
            height=2.9.vh
            fontSize=2.9.vh
            fontWeight=FontWeight.bolder
            lineHeight=pLineHeight
            color=colors.IDX_NUM_COLR
        }
        cellIdxNum(PUZZLE_CELL_IDX_NUM, LineHeight("0.5vh"))
        cellIdxNum(PUZZLE_LGND_IDX_NUM, LineHeight("2.1vh"))
        fun cellChar(pSel: String, pColr: Color)= rule(pSel.cls()){
            fontSize=8.0.vh
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
