package de.agrothe.kreuzwortapp

import kotlinx.css.*
import kotlinx.css.properties.*

private val confCss=config.webApp.CSS

// no support for these attributes in kotlin.css
const val NEW_GAME_BUTTON_STYLE =
    "writing-mode:vertical-lr;text-orientation:upright"
const val NEW_GAME_DIALOG_STYLE =
    "box-shadow:0 4px 8px 0 rgba(0,0,0,0.2),0 6px 20px 0 rgba(0,0,0,0.19)"

val CSS = fun CSSBuilder.(){
    fun String.cls()=".$this"

    with(confCss){
        val colors=COLOR_PALETTES.random()
        val gridBorderColor=colors.GRID_BORDER_COLR
        val gridLineColor=colors.GRID_LINES_COLR

        val NEW_GAME_BORDER_STYLE=
            "0.2vh groove ${gridBorderColor.darken(20)}"

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
            padding="0.2vh"
            lineHeight=LineHeight("2.0vh")
            fontWeight=FontWeight.w700
            color=gridBorderColor.darken(40)
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
                border="none"
                marginLeft=0.5.vh
                marginRight=1.0.vh
            }
            rule(FIELD_GRID.cls()){
                gridColumnStart=GridColumnStart("1")
                gridColumnEnd=GridColumnEnd("4")
                gridRowStart=GridRowStart("2")
                gridRowEnd=GridRowEnd("3")
                padding="0.2vh"
            }
            fun lgndEntries(pSel: String, pTextDecoLine: TextDecorationLine,
                    pBorderBottomStyle: BorderStyle, pColor: Color)
                        = rule(pSel.cls()){
                borderWidth=0.3.vh
                borderColor=pColor
                borderStyle=BorderStyle.none
                borderBottomStyle=pBorderBottomStyle
                textDecoration=TextDecoration(setOf(pTextDecoLine))
                lineHeight=LineHeight("2.6vh")
                hyphens=Hyphens.auto
            }
            fun lgndEntriesDir(pClass: String, pColor: Color){
                lgndEntries(pClass,
                    TextDecorationLine.unset, BorderStyle.dashed, pColor)
                lgndEntries("${pClass}${LGND_ENTRIES_SOLVED_SFX}",
                    TextDecorationLine.lineThrough, BorderStyle.dashed, pColor)
                lgndEntries("${pClass}${LGND_LAST_SFX}",
                    TextDecorationLine.unset, BorderStyle.none, pColor)
                lgndEntries(
                    "${pClass}${LGND_LAST_SFX}${LGND_ENTRIES_SOLVED_SFX}",
                    TextDecorationLine.lineThrough, BorderStyle.none, pColor)
            }
            lgndEntriesDir(LGND_ENTRIES_HOR, gridLineColor.darken(70))
            lgndEntriesDir(LGND_ENTRIES_VER, gridBorderColor.darken(70))
            rule(LGND_TABLE.cls()){
                fontSize=2.5.vh
                paddingTop=2.vh
                fontWeight=FontWeight.w700
            }
            fun lgndTableHdr(pSel: String, pTop: LinearDimension, pColor: Color)
                    = rule(pSel.cls()){
                paddingTop=pTop
                textAlign=TextAlign.left
                textDecoration=TextDecoration(setOf(TextDecorationLine.underline))
                color=pColor.darken(65)
            }
            lgndTableHdr(LGND_TABLE_HEADER_HOR, 0.vh, gridBorderColor)
            lgndTableHdr(LGND_TABLE_HEADER_HOR_NTH, 2.vh, gridBorderColor)
            lgndTableHdr(LGND_TABLE_HEADER_VER, 0.vh, gridLineColor)
            lgndTableHdr(LGND_TABLE_HEADER_VER_NTH, 2.vh, gridLineColor)
            fun cellIdxNum(pSel: String, pLineHeight: String,
                    pColor: Color) = rule(pSel.cls()){
                lineHeight=LineHeight(pLineHeight)
                height=LinearDimension(pLineHeight)
                fontSize=LinearDimension(pLineHeight)
                fontWeight=FontWeight.w800
                color=pColor.darken(50)
            }
            cellIdxNum(PUZZLE_CELL_IDX_NUM_HOR, "3vh",
                gridLineColor)
            cellIdxNum(PUZZLE_CELL_IDX_NUM_VER, "3vh",
                gridBorderColor)
            cellIdxNum(PUZZLE_LGND_IDX_NUM_HOR, "2.4vh",
                gridLineColor)
            cellIdxNum(PUZZLE_LGND_IDX_NUM_VER, "2.4vh",
                gridBorderColor)
            val IDX_SCT_ROT_HGHT = 1.9.vh
            rule(IDX_SLCT_ROT_SOUTH.cls()){
                height=IDX_SCT_ROT_HGHT
                transform.rotate(0.grad)
            }
            rule(IDX_SLCT_ROT_EAST.cls()){
                height=IDX_SCT_ROT_HGHT
                transform.rotate(100.grad)
            }
            rule(IDX_SLCT_ROT_NORTH.cls()){
                height=IDX_SCT_ROT_HGHT
                transform.rotate(200.grad)
            }
            rule(IDX_SLCT_ROT_WEST.cls()){
                height=IDX_SCT_ROT_HGHT
                transform.rotate(300.grad)
            }
            fun cellChar(pSel: String, pColr: Color,
                    pIterCnt: String="0")= rule(pSel.cls()){
                fontSize=7.0.vh
                color=pColr
                backgroundColor=Color.transparent
                borderStyle=BorderStyle.none
                lineHeight=LineHeight("3vh")
                padding="0"
                maxWidth=1.em
                maxHeight=1.em
                textAlign=TextAlign.center
                transition("color", TRANSITION_DURATION.s,
                    Timing("cubic-bezier(0.4, 0, 0.2, 1)"), 0.s)
                animation(name="${PUZZLE_CELL_CHAR_FINISHED}${(1..3).random()}",
                    duration=ANIMATION_DURATION.s,
                    iterationCount=IterationCount(pIterCnt))
            }
            cellChar(PUZZLE_CELL_CHAR, colors.CELL_CHAR_COLR)
            cellChar(PUZZLE_CELL_CHAR_SOLVED, colors.PUZZLE_CELL_CHAR_SOLVED)
            cellChar(PUZZLE_CELL_CHAR_FINISHED,
                colors.PUZZLE_CELL_CHAR_SOLVED, ANIMATION_ITER_CNT)
        }
        media("only screen and (orientation: landscape)"){
            rule(PUZZLE_GRID.cls()){
                display=Display.grid
                gridTemplateColumns=GridTemplateColumns(
                    LinearDimension("45fr"), LinearDimension("45fr"),
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
                border="none"
                marginRight=3.4.vh
                fontSize=4.6.vh
            }
            fun lgndEntries(pSel: String, pTextDecoLine: TextDecorationLine,
                    pBorderBottomStyle: BorderStyle, pColor: Color)
                        = rule(pSel.cls()){
                borderWidth=0.3.vh
                borderColor=pColor
                borderStyle=BorderStyle.none
                borderBottomStyle=pBorderBottomStyle
                textDecoration=TextDecoration(setOf(pTextDecoLine))
                lineHeight=LineHeight("5.1vh")
                hyphens=Hyphens.auto
            }
            fun lgndEntriesDir(pClass: String, pColor: Color){
                lgndEntries(pClass,
                    TextDecorationLine.unset, BorderStyle.dashed, pColor)
                lgndEntries("${pClass}${LGND_ENTRIES_SOLVED_SFX}",
                    TextDecorationLine.lineThrough, BorderStyle.dashed, pColor)
                lgndEntries("${pClass}${LGND_LAST_SFX}",
                    TextDecorationLine.unset, BorderStyle.none, pColor)
                lgndEntries(
                    "${pClass}${LGND_LAST_SFX}${LGND_ENTRIES_SOLVED_SFX}",
                    TextDecorationLine.lineThrough, BorderStyle.none, pColor)
            }
            lgndEntriesDir(LGND_ENTRIES_HOR, gridLineColor.darken(70))
            lgndEntriesDir(LGND_ENTRIES_VER, gridBorderColor.darken(70))
            rule(LGND_TABLE.cls()){
                fontSize=5.0.vh
                paddingTop=2.vh
                fontWeight=FontWeight.w700
            }
            fun lgndTableHdr(pSel: String, pTop: LinearDimension, pColor: Color)
                    = rule(pSel.cls()){
                paddingTop=pTop
                textAlign=TextAlign.left
                textDecoration=TextDecoration(setOf(TextDecorationLine.underline))
                color=pColor.darken(65)
            }
            lgndTableHdr(LGND_TABLE_HEADER_HOR, 0.vh, gridBorderColor)
            lgndTableHdr(LGND_TABLE_HEADER_HOR_NTH, 2.vh, gridBorderColor)
            lgndTableHdr(LGND_TABLE_HEADER_VER, 0.vh, gridLineColor)
            lgndTableHdr(LGND_TABLE_HEADER_VER_NTH, 2.vh, gridLineColor)
            fun cellIdxNum(pSel: String, pLineHeight: String, pHeight: String,
                    pColor: Color) = rule(pSel.cls()){
                lineHeight=LineHeight(pLineHeight)
                height=LinearDimension(pHeight)
                fontSize=LinearDimension(pLineHeight)
                fontWeight=FontWeight.bolder
                color=pColor.darken(60)
            }
            cellIdxNum(PUZZLE_CELL_IDX_NUM_HOR, "5vh", "5vh",
                gridLineColor)
            cellIdxNum(PUZZLE_CELL_IDX_NUM_VER, "5vh", "5vh",
                gridBorderColor)
            cellIdxNum(PUZZLE_LGND_IDX_NUM_HOR, "3.8vh","4.4vh",
                gridLineColor)
            cellIdxNum(PUZZLE_LGND_IDX_NUM_VER, "3.8vh","4.4vh",
                gridBorderColor)
            val IDX_SCT_ROT_HGHT = 3.4.vh
            rule(IDX_SLCT_ROT_SOUTH.cls()){
                height=IDX_SCT_ROT_HGHT
                transform.rotate(0.grad)
            }
            rule(IDX_SLCT_ROT_EAST.cls()){
                height=IDX_SCT_ROT_HGHT
                transform.rotate(100.grad)
            }
            rule(IDX_SLCT_ROT_NORTH.cls()){
                height=IDX_SCT_ROT_HGHT
                transform.rotate(200.grad)
            }
            rule(IDX_SLCT_ROT_WEST.cls()){
                height=IDX_SCT_ROT_HGHT
                transform.rotate(300.grad)
            }
            fun cellChar(pSel: String, pColr: Color,
                    pIterCnt: String="0")= rule(pSel.cls()){
                fontSize=10.0.vh
                color=pColr
                backgroundColor=Color.transparent
                borderStyle=BorderStyle.none
                lineHeight=LineHeight("4vh")
                padding="0"
                maxWidth=1.em
                maxHeight=1.em
                textAlign=TextAlign.center
                transition("color", TRANSITION_DURATION.s,
                    Timing("cubic-bezier(0.4, 0, 0.2, 1)"), 0.s)
                animation(name="${PUZZLE_CELL_CHAR_FINISHED}${(1..3).random()}",
                    duration=ANIMATION_DURATION.s,
                    iterationCount=IterationCount(pIterCnt))
            }
            cellChar(PUZZLE_CELL_CHAR, colors.CELL_CHAR_COLR)
            cellChar(PUZZLE_CELL_CHAR_SOLVED, colors.PUZZLE_CELL_CHAR_SOLVED)
            cellChar(PUZZLE_CELL_CHAR_FINISHED,
                colors.PUZZLE_CELL_CHAR_SOLVED, ANIMATION_ITER_CNT)
        }
        rule(LGND_TABLE.cls()){
            fontFamily="sans-serif"
            margin="auto"
            fontWeight=FontWeight.normal
            color=colors.CELL_CHAR_COLR
            wordWrap=WordWrap.breakWord
        }
        rule(CELL_GRID.cls()){
            display=Display.grid
            gridTemplateColumns=GridTemplateColumns(
                LinearDimension("3fr"))
            gridTemplateRows=GridTemplateRows(
                LinearDimension("1fr"), LinearDimension("2fr"),
                LinearDimension("1fr"))
        }
        rule(GRID_TABLE.cls()){
            fontFamily="monospace"
            borderWidth=0.6.vh
            borderStyle=BorderStyle.solid
            borderColor=gridBorderColor
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
            borderColor=gridLineColor
        }
        rule((TABLE_CELL_BACKGROUND+"1").cls()){
            backgroundColor=Color.floralWhite.lighten((1..3).random())
                //.changeAlpha((84..90).random()*0.01)
        }
        /*
        rule((TABLE_CELL_BACKGROUND+"1").cls()
                +" "+ PUZZLE_CELL_CHAR_SOLVED.cls()){
            backgroundColor=Color.floralWhite.lighten((1..3).random())
                .changeAlpha((84..90).random()*0.01)
        }
         */
        rule((TABLE_CELL_BACKGROUND+"2").cls()){
            backgroundColor=Color.antiqueWhite.lighten((5..7).random())
                //.changeAlpha((91..94).random()*0.01)
        }
        rule("::placeholder"){
            color=gridLineColor.desaturate(25)
            fontStyle=FontStyle.italic
        }
        rule(PUZZLE_CELL_GRID_IDX.cls()){
            paddingTop=0.4.vh
            paddingLeft=0.4.vh
        }
        rule(PUZZLE_CELL_CHAR_CONTAINER.cls()){
            textAlign=TextAlign.center
        }
        rule((PUZZLE_CELL_CHAR+":focus-within").cls()){
            backgroundColor=Color.white
        }
        rule(NUM_GAME.cls()){
            margin="auto"
            padding="0.2vh"
            paddingBottom=LinearDimension("1.5vh")
            color=gridLineColor.darken(40)
            fontWeight=FontWeight.lighter
            fontStyle=FontStyle.italic
            top=LinearDimension("0")
        }
        fun StyledElement.newGameBoder(){apply{
            border=NEW_GAME_BORDER_STYLE
            borderRadius=0.8.vh
        }}
        rule(NEW_GAME_LABEL.cls()){
            padding="0.4vh"
            newGameBoder()
        }
        rule(GLASS_LAYER.cls()){
            display=Display.none
            zIndex=1
            position=Position.fixed
            top=0.px
            left=0.px
            width=100.pct
            height=100.pct
            alignItems=Align.center
            justifyItems=JustifyItems.center
            alignContent=Align.center
            justifyContent=JustifyContent.center
        }
        rule(MENU_LAYER.cls()){
            newGameBoder()
            borderWidth=0.4.vh
            margin="none"
            fontSize=2.7.vh
            lineHeight=LineHeight("5.0vh")
            color=gridBorderColor.darken(60)
            alignContent=Align.center
            width=87.pct
            padding="1.0vh"
            textAlign=TextAlign.center
            backgroundColor=Color.white
        }
        fun StyledElement.menuLayerNextButton(){apply{
            newGameBoder()
            nextButton()
            borderColor=gridLineColor
            padding="0.5vh"
            whiteSpace=WhiteSpace.nowrap
        }}
        rule(MENU_LAYER_NEXT_BUTTON.cls()){
            menuLayerNextButton()
        }
        rule(MENU_LAYER_NEXT_BUTTON_ACTIVE.cls()){
            menuLayerNextButton()
            borderWidth=0.5.vh
            fontWeight=FontWeight.w900
        }
    }
}
