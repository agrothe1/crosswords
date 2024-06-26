package de.agrothe.kreuzwortapp

import kotlinx.css.*
import kotlinx.css.properties.*

private val confCss=config.webApp.CSS

val newGameButtonStyle = // no support for these attributes in kotlin.css
    "writing-mode:vertical-lr;text-orientation:upright"

val CSS = fun CSSBuilder.(){
    fun String.cls() = ".$this"

    with(confCss){
        val colors = COLOR_PALETTES.random()
        val gridBorderColor = colors.GRID_BORDER_COLR.darken(40)

        rule("html, body"){
            //backgroundColor=Color.transparent // todo
            minHeight=100.vh
            maxHeight=100.vw
            height=100.vh
            width=100.vw
            marginTop=0.vh
            marginBottom=0.vh
            marginLeft=0.vh
            marginRight=0.vh
            paddingLeft=LinearDimension("0.2vh")
            paddingRight=LinearDimension("0.2vh")
        }
        rule("h1"){
            color=Color.blue
            fontSize=0.5.vh
        }
        fun StyledElement.nextButton(){apply{
            fontFamily="sans-serif"
            fontSize=2.6.vh
            margin="auto"
            padding(LinearDimension("0.2vh"))
            lineHeight=LineHeight("2.0vh")
            fontWeight=FontWeight.w700
            color=gridBorderColor
            border="0.3vh dotted $gridBorderColor"
            borderRadius=0.8.vh
            background="none"
        }}
        media("only screen and (orientation: portrait)"){
            rule(PUZZLE_GRID.cls()){
                display=Display.grid
                gridTemplateColumns=GridTemplateColumns(
                    LinearDimension("4fr"), LinearDimension("4fr"),
                    LinearDimension("1fr"))
                gridTemplateRows=GridTemplateRows(
                    LinearDimension("8fr"), LinearDimension("14fr"))
            }
            rule(LGND_GRID_HORIZ.cls()){
                gridRowStart=GridRowStart("1")
                gridColumnStart=GridColumnStart("1")
                gridColumnEnd=GridColumnEnd("2")
            }
            rule(LGND_GRID_VERT.cls()){
                gridRowStart=GridRowStart("1")
                gridColumnStart=GridColumnStart("2")
                gridColumnEnd=GridColumnEnd("3")
                marginLeft=LinearDimension.auto
                marginRight=LinearDimension("0")
            }
            rule(NEW_GAME.cls()){
                gridRowStart=GridRowStart("1")
                gridColumnStart=GridColumnStart("3")
                nextButton()
                marginLeft=0.5.vh
                marginRight=1.0.vh
            }
            rule(FIELD_GRID.cls()){
                gridColumnStart=GridColumnStart("1")
                gridColumnEnd=GridColumnEnd("4")
                gridRowStart=GridRowStart("2")
                gridRowEnd=GridRowEnd("3")
                paddingTop=0.7.vh
            }
        }
        media("only screen and (orientation: landscape)"){
            rule(PUZZLE_GRID.cls()){
                display=Display.grid
                gridTemplateColumns=GridTemplateColumns(
                    LinearDimension("20fr"), LinearDimension("20fr"),
                    LinearDimension("88fr"), LinearDimension("1fr"))
                gridTemplateRows=GridTemplateRows(LinearDimension("1fr"))
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
                marginRight=LinearDimension("0")
            }
            rule(FIELD_GRID.cls()){
                gridColumnStart=GridColumnStart("3")
                gridColumnEnd=GridColumnEnd("4")
                gridRowStart=GridRowStart("1")
                gridRowEnd=GridRowEnd("2")
                paddingTop=0.7.vh
            }
            rule(NEW_GAME.cls()){
                gridColumnStart=GridColumnStart("4")
                gridColumnStart=GridColumnStart("5")
                gridRowStart=GridRowStart("1")
                gridRowEnd=GridRowEnd("2")
                nextButton()
                //marginRight=3.0.vh
            }
        }
        rule(CELL_GRID.cls()){
            display=Display.grid
            gridTemplateColumns=GridTemplateColumns(
                LinearDimension("3fr"))
            gridTemplateRows=GridTemplateRows(
                LinearDimension("1fr"), LinearDimension("2fr"),
                LinearDimension("1fr"))
        }
        rule(LGND_TABLE.cls()){
            fontFamily="sans-serif"
            fontSize=2.6.vh
            margin="auto"
            paddingTop=1.vh
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
            borderWidth=0.3.vh
            borderColor=colors.GRID_BORDER_COLR.darken(30)
            borderStyle=BorderStyle.none
            borderBottomStyle=pBorderBottomStyle
            textDecoration=TextDecoration(setOf(pTextDecoLine))
            lineHeight=LineHeight("2.7vh")
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
            marginLeft=LinearDimension("auto")
            marginRight=LinearDimension("auto")
            width=98.pct
            //backgroundImage=Image("url(imgs/AGRLogoGS.svg)") // todo
            //backgroundPosition="center"
            //filter="grayscale(80%)"
        }
        rule(GRID_TABLE_COL.cls()){
            borderWidth=0.4.vh
            borderStyle=BorderStyle.solid
            borderColor=colors.GRID_LINES_COLR
        }
        rule((TABLE_CELL_BACKGROUND+"1").cls()){
            backgroundColor=Color.floralWhite.lighten((1..3).random())
                //.changeAlpha((84..90).random()*0.01)
        }
        /*
        rule((TABLE_CELL_BACKGROUND+"1").cls()
                +" "+PUZZLE_CELL_CHAR_SOLVED.cls()){
            backgroundColor=Color.floralWhite.lighten((1..3).random())
                .changeAlpha((84..90).random()*0.01)
        }
         */
        rule((TABLE_CELL_BACKGROUND+"2").cls()){
            backgroundColor=Color.antiqueWhite.lighten((5..7).random())
                //.changeAlpha((91..94).random()*0.01)
        }
        rule(PUZZLE_CELL_GRID_IDX.cls()){
            paddingTop=0.4.vh
            paddingLeft=0.4.vh
        }
        fun cellIdxNum(pSel: String, pLineHeight: LineHeight)
                = rule(pSel.cls()){
            height=2.9.vh
            fontSize=2.9.vh
            fontWeight=FontWeight.bolder
            lineHeight=pLineHeight
            textAlign=TextAlign.center
            color=colors.IDX_NUM_COLR
        }
        cellIdxNum(PUZZLE_CELL_IDX_NUM, LineHeight("3vh"))
        cellIdxNum(PUZZLE_LGND_IDX_NUM, LineHeight("2.1vh"))
        rule(PUZZLE_CELL_CHAR_CONTAINER.cls()){
            textAlign=TextAlign.center
        }
        fun cellChar(pSel: String, pColr: Color)= rule(pSel.cls()){
            fontSize=7.5.vh
            color=pColr
            backgroundColor=Color.transparent
            borderStyle=BorderStyle.none
            lineHeight=LineHeight("4vh")
            padding="0"
            maxWidth=1.em
            maxHeight=1.em
            textAlign=TextAlign.center
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
