package de.agrothe.kreuzwortapp

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.css.*
import kotlinx.css.properties.*

private val logger by lazy{ KotlinLogging.logger{}}

private val confCss=config.webApp.CSS

// no support for these attributes in kotlin.css
const val CELL_CHAR_FONT_FAMILY="monospace,sans-serif"
const val CONTENT_STYLE="transform-origin:top left"
const val NEW_GAME_BUTTON_STYLE =
    "writing-mode:vertical-lr;text-orientation:upright"
const val NEW_GAME_DIALOG_STYLE =
    "box-shadow:0 4px 8px 0 rgba(0,0,0,0.2),0 6px 20px 0 rgba(0,0,0,0.19)"
const val CLR_PLCH = "%COLOR"
const val PUZZLE_CELL_GRID_IDX_BACKGRD_STYLE_TPLT =
    "radial-gradient(circle at center,${CLR_PLCH} 0%,transparent 400%)"
const val SIMPLE_KEYBOARD_CLASS_NAME="simple-keyboard"

val CSS = fun(pDimen: Int, pWidth: Int, pHght: Int) = CSSBuilder().apply{
    fun String.cls()=".$this"
    fun Float.decrPercnt(pPrct: Float, pUnit: String)=
        toFloat().run{LinearDimension("${this-this/100*pPrct}$pUnit")}

    logger.debug{"CSS params dimen:'$pDimen' width:'$pWidth' hght:'$pHght'"}

    with(confCss){
        val colors=COLOR_PALETTES.random()
        val gridBorderColor=colors.GRID_BORDER_COLR
        val gridLineColor=colors.GRID_LINES_COLR
        val puzzleCellFocusedColor=gridLineColor.darken(20)
            .saturate(20)
        val tableCellBackgroundColor1=Color.floralWhite.lighten((1..3)
            .random())//.changeAlpha((84..90).random()*0.01)
        val tableCellBackgroundColor2=Color.antiqueWhite.lighten((5..7)
            .random())//.changeAlpha((91..94).random()*0.01)
        val NEW_GAME_BORDER_STYLE =
            "0.2vh groove ${gridBorderColor.darken(20)}"
        val PUZZLE_CELL_GRID_IDX_BACKGRD_STYLE1 =
            PUZZLE_CELL_GRID_IDX_BACKGRD_STYLE_TPLT
                .replace(CLR_PLCH, tableCellBackgroundColor1.toString())
        val PUZZLE_CELL_GRID_IDX_BACKGRD_STYLE2 =
            PUZZLE_CELL_GRID_IDX_BACKGRD_STYLE_TPLT
                .replace(CLR_PLCH, tableCellBackgroundColor2.toString())
        val PUZZLE_CELL_FOCUSED_BORDER_WIDTH=LinearDimension("0.7vh")

        rule("html, body"){
            height=100.pct
            width=100.pct
            margin="0"
            overflow=Overflow.hidden
        }
        fun cellChar(pSel: String, pColr: Color)=rule(pSel.cls()){
            position=Position.absolute
            top=50.pct
            left=50.pct
            transform.translate(-50.pct, -50.pct)
            color=pColr
            backgroundColor=Color.transparent
            borderStyle=BorderStyle.none
            padding="0"
            fontFamily=CELL_CHAR_FONT_FAMILY
            (100f/pDimen).decrPercnt(
                    PUZZLE_CELL_CHAR_DECR_HGHT_PERC, "vw").let{
                fontSize=it
                maxWidth=it
                maxHeight=it
                //lineHeight=LineHeight("$it")
            }
            textAlign=TextAlign.center
            transition("color", TRANSITION_DURATION.s,
                Timing("cubic-bezier(0.4, 0, 0.2, 1)"), 0.s)
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
        cellChar(PUZZLE_CELL_CHAR, colors.CELL_CHAR_COLR)
        cellChar(PUZZLE_CELL_CHAR_SOLVED, colors.PUZZLE_CELL_CHAR_SOLVED)
        cellChar(PUZZLE_CELL_CHAR_FINISHED, colors.PUZZLE_CELL_CHAR_SOLVED)
        rule(PUZZLE_CELL_CHAR_ALL_FINISHED.cls()){
            animation(name=
                "${PUZZLE_CELL_CHAR_ALL_FINISHED}${(1..ANIMATION_VARIATION_CNT)
                    .random()}",
                duration=ANIMATION_DURATION.s,
                iterationCount=IterationCount.infinite)
        }
        LinearDimension("4vw").let{hght->
            rule(IDX_SLCT_ROT_SOUTH.cls()){
                height=hght
            }
            rule(IDX_SLCT_ROT_EAST.cls()){
                height=hght
            }
            rule(IDX_SLCT_ROT_NORTH.cls()){
                height=hght
            }
            rule(IDX_SLCT_ROT_WEST.cls()){
                height=hght
        }}
        //media("only screen and (orientation: portrait)"){
            rule(PUZZLE_GRID.cls()){
                display=Display.grid
            }
            rule(LGND_GRID_HORIZ.cls()){
                alignSelf=Align.selfStart
            }
            rule(LGND_GRID_VERT.cls()){
                alignSelf=Align.selfStart
                marginLeft=LinearDimension.auto
                marginRight=LinearDimension("0")
            }
            rule(NEW_GAME.cls()){
                nextButton()
                border="none"
                marginLeft=0.5.vh
                marginRight=1.0.vh
            }
            rule(FIELD_GRID.cls()){
                padding="0.2vh"
            }
            rule(LGND_TABLE.cls()){
                fontSize=2.5.vh
                paddingTop=0.5.vh
                fontWeight=FontWeight.w700
            }
            fun lgndTableHdr(pSel: String, pTop: LinearDimension, pColor: Color)
                    = rule(pSel.cls()){
                paddingTop=pTop
                textAlign=TextAlign.left
                textDecoration=
                    TextDecoration(setOf(TextDecorationLine.underline))
                color=pColor.darken(65)
            }
            lgndTableHdr(LGND_TABLE_HEADER_HOR, 0.vh, gridBorderColor)
            lgndTableHdr(LGND_TABLE_HEADER_HOR_NTH, 2.vh, gridBorderColor)
            lgndTableHdr(LGND_TABLE_HEADER_VER, 0.vh, gridLineColor)
            lgndTableHdr(LGND_TABLE_HEADER_VER_NTH, 2.vh, gridLineColor)
            fun cellIdxNumBkgnd(pHeight: String) =
                    rule(PUZZLE_CELL_IDX_NUM_BKGND.cls()){
                borderRadius=LinearDimension(pHeight)
            }
            fun cellIdxNum(pSel: String, pLineHeight: String,
                    pColor: Color) = rule(pSel.cls()){
                "${pLineHeight}cqh".let{lHght->
                    LinearDimension(lHght).let{Hght->
                        lineHeight=LineHeight(lHght)
                        height=Hght
                        fontSize=Hght
                        fontWeight=FontWeight.w800
                        color=pColor.darken(50)
                        zIndex=1
                        opacity=0.8
            }}}
            cellIdxNumBkgnd("6vh")
            cellIdxNum(PUZZLE_CELL_IDX_NUM_HOR, "3",
                gridLineColor)
            cellIdxNum(PUZZLE_CELL_IDX_NUM_VER, "3",
                gridBorderColor)
            cellIdxNum(PUZZLE_LGND_IDX_NUM_HOR, "2.4",
                gridLineColor)
            cellIdxNum(PUZZLE_LGND_IDX_NUM_VER, "2.4",
                gridBorderColor)
            LinearDimension("2.8cqh").let{hght->
                rule(IDX_SLCT_ROT_SOUTH.cls()){
                    height=hght
                }
                rule(IDX_SLCT_ROT_EAST.cls()){
                    height=hght
                }
                rule(IDX_SLCT_ROT_NORTH.cls()){
                    height=hght
                }
                rule(IDX_SLCT_ROT_WEST.cls()){
                    height=hght
                }}
        //}
        //media("only screen and (orientation: landscape)"){
        media("(orientation: Xlandscape) and (max-width: 812px)" +
                " and (min-aspect-ratio: 16/9)"){ // todo
            rule(PUZZLE_GRID.cls()){
                display=Display.grid
                gridTemplateColumns=GridTemplateColumns(
                    LinearDimension("45fr"), LinearDimension("45fr"),
                    LinearDimension("88fr"), LinearDimension("1fr"))
                gridTemplateRows=GridTemplateRows(
                    LinearDimension("4fr"), LinearDimension("2fr"))
                overflow=Overflow.auto
            }
            rule(LGND_GRID_HORIZ.cls()){
                //display=Display.grid
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
                overflowY=Overflow.auto
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
                borderWidth=0.3.vh // lgndBorderWidth -> todo constant
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
                textDecoration=
                    TextDecoration(setOf(TextDecorationLine.underline))
                color=pColor.darken(65)
            }
            lgndTableHdr(LGND_TABLE_HEADER_HOR, 0.vh, gridBorderColor)
            lgndTableHdr(LGND_TABLE_HEADER_HOR_NTH, 2.vh, gridBorderColor)
            lgndTableHdr(LGND_TABLE_HEADER_VER, 0.vh, gridLineColor)
            lgndTableHdr(LGND_TABLE_HEADER_VER_NTH, 2.vh, gridLineColor)
            fun cellIdxNum(pSel: String, pLineHeight: String,
                    pHeight: String, pColor: Color) = rule(pSel.cls()){
                lineHeight=LineHeight(pLineHeight)
                height=LinearDimension(pHeight)
                fontSize=LinearDimension(pLineHeight)
                fontWeight=FontWeight.w800
                color=pColor.darken(60)
                opacity=0.8
            }
            cellIdxNum(PUZZLE_CELL_IDX_NUM_HOR, "5vh", "5vh",
                gridLineColor)
            cellIdxNum(PUZZLE_CELL_IDX_NUM_VER, "5vh", "5vh",
                gridBorderColor)
            cellIdxNum(
                PUZZLE_LGND_IDX_NUM_HOR, "3.8vh","4.4vh",
                gridLineColor)
            cellIdxNum(
                PUZZLE_LGND_IDX_NUM_VER, "3.8vh","4.4vh",
                gridBorderColor)
            rule(PUZZLE_CELL_IDX_NUM_BKGND.cls()){
                borderRadius=LinearDimension("8vh")
            }
            LinearDimension("4vh").let{hght->
                rule(IDX_SLCT_ROT_SOUTH.cls()){
                    height=hght
                }
                rule(IDX_SLCT_ROT_EAST.cls()){
                    height=hght
                }
                rule(IDX_SLCT_ROT_NORTH.cls()){
                    height=hght
                }
                rule(IDX_SLCT_ROT_WEST.cls()){
                    height=hght
                }}
        }
        rule(LGND_TABLE.cls()){
            fontFamily="sans-serif"
            margin="auto"
            fontWeight=FontWeight.normal
            color=colors.CELL_CHAR_COLR
            wordWrap=WordWrap.breakWord
        }
        rule(CELL_GRID.cls()){
            position=Position.relative
            display=Display.grid
            gridTemplateColumns=GridTemplateColumns(
                LinearDimension("3fr"))
            gridTemplateRows=GridTemplateRows(
                LinearDimension("1fr"), LinearDimension("2fr"),
                LinearDimension("1fr"))
        }
        rule(GRID_TABLE.cls()){
            fontFamily="sans-serif"
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
            backgroundColor=tableCellBackgroundColor1
        }
        rule((TABLE_CELL_BACKGROUND+"2").cls()){
            backgroundColor=tableCellBackgroundColor2
        }
        rule("::placeholder"){
            color=gridLineColor.desaturate(25)
            fontStyle=FontStyle.italic
        }
        rule(PUZZLE_CELL_GRID_IDX.cls()){
            paddingTop=0.4.vh
            paddingLeft=0.4.vh
            zIndex=1
        }
        rule(PUZZLE_CELL_GRID_IDX_BACKGRD+"1".cls()){
            background=PUZZLE_CELL_GRID_IDX_BACKGRD_STYLE1
        }
        rule(PUZZLE_CELL_GRID_IDX_BACKGRD+"2".cls()){
            background=PUZZLE_CELL_GRID_IDX_BACKGRD_STYLE2
        }
        rule("td:has(${PUZZLE_CELL_FOCUSED.cls()})"){
            borderStyle=BorderStyle.solid
            borderColor=puzzleCellFocusedColor
            borderWidth=PUZZLE_CELL_FOCUSED_BORDER_WIDTH
            //paddingTop=PUZZLE_CELL_FOCUSED_BORDER_WIDTH
        }
        rule(PUZZLE_CELL_FOCUSED.cls()){
            /* too annoying, makes cell char jump todo: fix? */
            /*
            animation(name="puzzleCellFocused",
                duration=PUZZLE_CELL_FOCUSED_ANIM_DURTN.s,
                iterationCount=IterationCount.infinite, timing=Timing.easeIn)
             */
        }
        rule(PUZZLE_CELL_CHAR_CONTAINER.cls()){
            textAlign=TextAlign.center
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
            zIndex=2
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
            fontSize=3.vh
            lineHeight=LineHeight("5.2vh")
            color=gridBorderColor.darken(60)
            alignContent=Align.center
            padding="2.0vh"
            textAlign=TextAlign.center
            backgroundColor=Color.white
        }
        fun StyledElement.menuLayerNextButton(){apply{
            newGameBoder()
            nextButton()
            fontSize=3.vh
            borderColor=gridLineColor
            borderWidth=0.5.vh
            padding="0.9vh"
            margin="1vh"
            whiteSpace=WhiteSpace.nowrap
        }}
        rule(MENU_LAYER_NEXT_BUTTON.cls()){
            menuLayerNextButton()
        }
        rule(MENU_LAYER_NEXT_BUTTON_ACTIVE.cls()){
            menuLayerNextButton()
            borderWidth=0.7.vh
            fontWeight=FontWeight.w900
        }
        rule(MENU_FIELD_SET_ENTRY.cls()){
            position=Position.relative
            borderStyle=BorderStyle.none
            margin="0"
            padding="0"
            lineHeight=LineHeight("4.0vh")
            textAlign=TextAlign.left
        }
        rule("""${MENU_FIELD_SET_ENTRY.cls()}
                | input[type="radio"]""".trimMargin()){
            appearance=Appearance.none
            newGameBoder()
            nextButton()
            borderRadius=50.pct
            width=1.9.vh
            height=width
        }
        rule("""${MENU_FIELD_SET_ENTRY.cls()}
                | input[type="radio"]:checked""".trimMargin()){
            borderWidth=0.5.vh
        }
        rule(IDX_SLCT_ROT_SOUTH.cls()){
            transform.rotate(0.grad)
        }
        rule(IDX_SLCT_ROT_EAST.cls()){
            transform.rotate(100.grad)
        }
        rule(IDX_SLCT_ROT_NORTH.cls()){
            transform.rotate(200.grad)
        }
        rule(IDX_SLCT_ROT_WEST.cls()){
            transform.rotate(300.grad)
        }
        rule(".simple-keyboard.hg-theme-default.${KEYBORD_THEME_NAME}"){
            color=colors.CELL_CHAR_COLR
            fontWeight=FontWeight.w700
            fontFamily=CELL_CHAR_FONT_FAMILY
        }
        rule(KEYBORD_HIDDEN.cls()){
            animation(name="keyboardFadeOut", duration=KEYBORD_ANIM_DURATION.ms,
                fillMode=FillMode.forwards, timing=Timing.ease)
        }
    }
}
