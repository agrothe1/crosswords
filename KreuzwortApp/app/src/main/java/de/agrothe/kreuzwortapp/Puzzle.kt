package de.agrothe.kreuzwortapp

import io.ktor.server.html.*
import kotlinx.html.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val confCss=confWeb.CSS

class BodyTplt(val pNumSolvedGames: Int, val pDimen: Int,
        val pExcluded: Collection<String>? = null, val pPuzzleType: PuzzleType,
        val pWidth: Int, val pHght: Int): Template<HTML>{
    val header = Placeholder<FlowContent>()
    val puzzle = TemplatePlaceholder<PuzzleTplt>()
    override fun HTML.apply(){
        lang="de"
        head{
            meta{
                charset="utf-8"
                name="viewport"
                content="height=device-height,width=device-width" // todo initial-scale ?
            }
            link{
                rel="stylesheet"
                href="/styles.css"+String.format(confWeb.APP_URL_PARAMS,
                    pDimen, pPuzzleType.name, pWidth, pHght)
                type="text/css"
            }
            link{
                rel="stylesheet"
                href="/css/anim.css"
                type="text/css"
            }
            link{
                rel="stylesheet"
                href="/kbd/keyboard.css"
                type="text/css"
            }
            script(src="kbd/keyboard.js"){}
        }
        body{
            /*
            h1{
                insert(header)
            }
             */
            insert(PuzzleTplt(pNumSolvedGames, pDimen, pExcluded,
                pPuzzleType, pWidth, pHght), puzzle)
            script{unsafe{raw(scripts)}}
        }}
}

class PuzzleTplt(private val pNumSolvedGames: Int, val pDimen: Int,
        pExcludedPuzzleNames: Collection<String>?, val pPuzzleType: PuzzleType,
        val pWidth: Int, val pHght: Int): Template<FlowContent>{
    private val entries = dict.entries
        .map{(key, values)
            ->Pair(key.uppercase(), values)}.toMap()

    private val puzzle: Puzzle =
        restoredGame?.puzzleInPlay
            ?: getRandom(pDimen, pExcludedPuzzleNames)!!
                .map{row->row.map{it.uppercaseChar()}
                .toCharArray()}.toTypedArray()

    init{
        if(restoredGame==null)
            puzzleCache[puzzle.hashCode()]=
                PuzzleCacheEntry(puzzle, emptyPuzzle(pDimen))
        else
            restoredGame?.apply{
                puzzleCache[puzzleGenerated.hashCode()]=
                    PuzzleCacheEntry(puzzleGenerated, puzzleInPlay)
                // todo mark solved
            }
    }

    override fun FlowContent.apply(){
        val gridTmplt = TemplatePlaceholder<PuzzleGrid>()
        with(confCss){
            fun TR.legendIdx(pRowIdx: Int, pColIdx: Int, pSynms: DictSynmsOrnt?,
                    pIdxSelct: Pair<String, String>) =
                pSynms?.ornt.let{ornt->
                    td{dirImg(ornt,
                        if(ornt == KeyDirct.NORMAL) 0 else pDimen-1,
                        pRowIdx, pColIdx, pIdxSelct, pDimen,
                        false, this)
                    }}
            fun TABLE.legendEntry(pSynm: String) =
                tr{
                    td{b{+Entities.middot}}
                    confWeb.LEGND_ENTR_SUBST_REGEX
                        .replace(pSynm, "").also{
                            td{+it}
                    }
                }
            fun TD.legendEntries(pWord: CharArray, pSynms: DictSynmsOrnt,
                    pId: String, pLast: Boolean, pHoriz: Boolean) =
                table(classes=
                    (if(pHoriz)LGND_ENTRIES_HOR else LGND_ENTRIES_VER)
                        +if(pLast) " ${
                            if(pHoriz)LGND_ENTRIES_HOR_LAST 
                            else LGND_ENTRIES_VER_LAST}"
                        else "")
                    {
                        id=pId
                        (if(pPuzzleType==PuzzleType.SCHWEDEN)
                            pSynms.synms.shuffled().take(confWeb.MAX_SYNMS)
                        else listOf(pWord.toList()
                                .shuffled().joinToString("")))
                            .foldIndexed(mutableListOf<String>()){
                                idx, acc, syn->
                                    if(idx==0 || acc.sumOf{it.length}+syn.length
                                            < confWeb.SYNMS_TOTAL_LNGTH_THRSHLD)
                                        acc.add(syn); acc}
                            .forEach{legendEntry(it)}
                    }

            fun BUTTON.gameButton(pLabel: String, pShowImg: Boolean = false) =
                table{
                    tr{td(classes=NUM_GAME){
                        style=NEW_GAME_BUTTON_STYLE
                        id=NUM_GAME_ID
                        +pNumSolvedGames.toString()
                    }}
                tr{td(classes=NEW_GAME_LABEL){
                    style=NEW_GAME_BUTTON_STYLE
                    +pLabel
                    if(pShowImg)
                        img(classes=IDX_SLCT_ROT_WEST,
                            src=webAppConf.DIRCTN_IMG)
                }}}

            div{
                id="content"
                div(classes=PUZZLE_GRID){
                    button(classes=NEW_GAME){
                        id=SHOW_HELP_BUTTON_ID
                        hidden=false
                        val wsdata=Json.encodeToString(
                            WSDataToSrvr(
                                showHelp=true, dimen=pDimen,
                                hashCode=puzzle.hashCode()
                            )
                        )
                        onClick="showHelp('$wsdata')"
                        gameButton(confWeb.I18n.SHOW_HELP)
                    }
                    button(classes=NEW_GAME){
                        id=NEW_GAME_BUTTON_ID
                        hidden=true
                        val wsdata=Json.encodeToString(
                            WSDataToSrvr(newGame=true, dimen=pDimen,
                                puzzleType=pPuzzleType.name)
                        )
                        onClick=
                            """
                    if(${confWeb.IS_PLUS_VERSION})
                        document.getElementById('$GLASS_LAYER')
                            .style.display='grid'
                    else{
                        let ws=new WebSocket('${webAppConf.WEB_SOCK_URL}')
                        ws.onopen=(ev)=>{ws.send('${wsdata}')}
                    }
                """.trimIndent()
                        gameButton(confWeb.I18n.NEW_GAME, true)
                    }
                    div(classes=LGND_GRID_HORIZ){
                        table(classes=LGND_TABLE){
                            tr{
                                th(classes=LGND_TABLE_HEADER_HOR)
                                    {colSpan="2"; +confWeb.I18n.HORIZONTAL}
                            }
                            puzzle.forEachIndexed{rowIdx, horWord->
                                tr{
                                    entries[puzzle.getStringAt(Axis.X, rowIdx)]
                                        ?.let{synms->
                                            legendIdx(rowIdx, 0, synms,
                                                Pair(IDX_SLCT_ROT_WEST,
                                                    IDX_SLCT_ROT_EAST)
                                                )
                                            td{legendEntries(horWord, synms,
                                                rowIdx.lgndIdSuffxRow(),
                                                rowIdx==pDimen-1,
                                                false)}
                            }}}
                        }
                    }
                    div(classes=FIELD_GRID){
                        insert(PuzzleGrid(entries, puzzle, pDimen, confWeb),
                            gridTmplt)
                    }
                    div(classes=LGND_GRID_VERT){
                        table(classes=LGND_TABLE){
                            tr{
                                th(classes=LGND_TABLE_HEADER_VER)
                                    {colSpan="2"; +confWeb.I18n.VERTICAL}
                            }
                            puzzle.forEachIndexed{colIdx, _->
                                tr{
                                    puzzle.getStringAt(Axis.Y, colIdx)
                                        .let{vertWord->
                                    entries[puzzle.getStringAt(Axis.Y, colIdx)]
                                        ?.let{synms->
                                            legendIdx(0, colIdx, synms,
                                                Pair(IDX_SLCT_ROT_SOUTH,
                                                    IDX_SLCT_ROT_NORTH))
                                            td{legendEntries(
                                                vertWord.toCharArray(),
                                                synms, colIdx.lgndIdSuffxCol(),
                                                colIdx==pDimen-1,
                                                true)}
                            }}}}
                        }
                    }
                    div(classes=GLASS_LAYER){
                        id=GLASS_LAYER
                        menu(confCss)
                    }
                    button(classes=NEW_GAME){
                        id=NEW_GAME_BUTTON_ID
                        hidden=true
                        val wsdata=Json.encodeToString(
                            WSDataToSrvr(newGame=true, dimen=pDimen)
                        )
                        onClick=
                            """
                    let ws=new WebSocket('${webAppConf.WEB_SOCK_URL}')
                    ws.onopen=(ev)=>{ws.send('${wsdata}')}
                """.trimIndent()
                        gameButton(confWeb.I18n.NEW_GAME, true)
                    }
                    div(classes=SIMPLE_KEYBOARD_CLASS_NAME){
                        id=SIMPLE_KEYBOARD_ID}
                }
            }
        }
    }
    fun DIV.menu(pCss: Css){
        table(classes=pCss.MENU_LAYER){
            style=NEW_GAME_DIALOG_STYLE
            tr{td{
                table{
                    //legend(classes="menuFieldSetLegend"){+"Rätsel"}
                    pCss.PUZZLE_TYPE_RADIO_GROUP_NAME.let{grpName->
                        PuzzleType.entries.forEachIndexed{pIdx, pType->
                            tr(classes=confCss.MENU_FIELD_SET_ENTRY){
                                td{pType.name.let{typeName->
                                    input(type=InputType.radio){
                                        id=grpName+pIdx
                                        name=grpName
                                        checked=typeName==pPuzzleType.name
                                        value=typeName
                                }}}
                                td{label{
                                    htmlFor=grpName+pIdx
                                    +confWeb.I18n.PUZZLE_TYPES.getOrDefault(
                                        pType, pType.toString())
                                }}
                        }}
                    }
                }}
            }
            listDimens()?.forEach{dimen->
                tr{td{
                    button(classes=
                        if(pDimen==dimen.toInt())
                            pCss.MENU_LAYER_NEXT_BUTTON_ACTIVE
                        else pCss.MENU_LAYER_NEXT_BUTTON){
                        val wsdata=Json.encodeToString(WSDataToSrvr(
                            newGame=true, dimen=dimen.toInt(),
                            puzzleType=webAppConf.PUZZLE_TYPE_PLACEHOLDER,
                            width=pWidth, height=pHght))
                        onClick=
                            """newGame('$wsdata')""".trimIndent()
                        +String.format(
                            confWeb.I18n.PUZZLE_DIMEN_TMPLT, dimen, dimen)
                    }
        }}}}
    }
}

// todo separate
class PuzzleGrid(val pEntries: DictEntry, val puzzle: Puzzle, val pDimen: Int,
        val pConf: WebAppConfig): Template<FlowContent>{
    override fun FlowContent.apply(){
        val cellTmplt = TemplatePlaceholder<GridCell>()
        with(pConf.CSS){
            table(GRID_TABLE){
                id=GRID_TABLE
                tbody{
                    puzzle.forEachIndexed{rowIdx, row->
                        tr(GRID_TABLE_ROW){
                            row.forEachIndexed{colIdx, char->
                                (TABLE_CELL_BACKGROUND+setOf(1,2).random())
                                    .let{bckgdColor->
                                td(GRID_TABLE_COL +' '+ bckgdColor){
                                    insert(GridCell(rowIdx, colIdx, char,
                                        pEntries[
                                            puzzle.getStringAt(Axis.X, rowIdx)],
                                        pEntries[
                                            puzzle.getStringAt(Axis.Y, colIdx)],
                                        pDimen, puzzle.hashCode(), bckgdColor),
                                            cellTmplt)
                                }}
                            }
            }}}}
        }
    }
}

@Suppress("SimplifiableCallChain")
val scripts="""
    let Keyboard=window.SimpleKeyboard.default
    let keyboard=new Keyboard({
        theme:"hg-theme-default hg-layout-default ${confCss.KEYBORD_THEME_NAME}",
        onChange:input=>onChange(input),
        maxLength:1,
        layout:{
            default:[
                "Q W E R T Z U I O P",
                "A S D F G H J K L",
                "Y X C V B N M {backspace}",
            ],
            landscape:[
                "A B C D E F G H I J K L M N",
                "O P Q R S T U V W X Y Z {backspace}",
            ]
        },
        display:{"{backspace}": "⌫"},
    })
    function keyboardStatus(pShow, pFx){
        var kbd=document.getElementById('${confCss.SIMPLE_KEYBOARD_ID}')
        function kbdDispl(pDisp){kbd.style.display=pDisp}
        var displ
        if(pShow){ displ='block'
            kbd.classList.remove('${confCss.KEYBORD_HIDDEN}')
            kbd.classList.add('${confCss.KEYBORD_SHOWN}')}
        else{ displ='none'
            kbd.classList.remove('${confCss.KEYBORD_SHOWN}')
            kbd.classList.add('${confCss.KEYBORD_HIDDEN}')}
        if(pFx) setTimeout(()=>{kbdDispl(displ)},
            ${confCss.KEYBORD_ANIM_DURATION})
        else kbdDispl(displ)
    }
    keyboardStatus(true, false)
    function removeFocusedStyle(pDoc){
        pDoc.querySelectorAll('[class^="${confCss.PUZZLE_CELL_CHAR}"]')
            .forEach((e)=>{e.classList
                .remove('${confCss.PUZZLE_CELL_FOCUSED}')})
    }
    function onInputFocus(pWSData){
        var d=document
        removeFocusedStyle(d)
        keyboard.clearInput()
        var wsdata=JSON.parse(pWSData)
        var inp=d.getElementById(wsdata.xPos+"_"+wsdata.yPos)
        inp.value=''
        inp.classList.add('${confCss.PUZZLE_CELL_FOCUSED}')
        keyboard.setOptions({WSData: wsdata})
    }
    function onChange(pInput){
        var d=document
        var wsdata=keyboard.options.WSData
        var e=d.getElementById(wsdata.xPos+'_'+wsdata.yPos)
        e.value=pInput.toUpperCase().replace(/[^A-Z]/,'')
        checkCellInput(pInput, wsdata, d)
    }
    function checkCellInput(pValue, pWSData, pDoc){
        var ws=new WebSocket('${webAppConf.WEB_SOCK_URL}')
        ws.addEventListener("message",(ev)=>{
            function rowColSolved(pId, pSel){
                removeFocusedStyle(pDoc)
                var l=pDoc.getElementById(pId)
                l.className=l.className+"${confCss.LGND_ENTRIES_SOLVED_SFX}"
                pDoc.querySelectorAll(pSel).forEach(e=>{
                    e.disabled=true
                    e.className='${confCss.PUZZLE_CELL_CHAR_SOLVED}'
                })
            }
            var rpl=JSON.parse(ev.data)
            if(rpl.rowSolved===true){
                var xPos=pWSData.xPos
                rowColSolved('${cssConf.LGND_ID_SUFFX_ROW}'+xPos,
                    '[id^="'+xPos+'_"]')
            }
            if(rpl.colSolved===true){
                var yPos=pWSData.yPos
                rowColSolved('${cssConf.LGND_ID_SUFFX_COL}'+yPos,
                    '[id$="_'+yPos+'"]')
            }
            if(rpl.puzzleSolved===true){
                keyboardStatus(false, true)
                showNewButton(pDoc)
                pDoc.querySelectorAll('.${confCss.PUZZLE_CELL_CHAR_SOLVED}')
                    .forEach((e)=>{e.classList.add(
                        '${confCss.PUZZLE_CELL_CHAR_FINISHED}')})
                pDoc.querySelectorAll('.${confCss.PUZZLE_CELL_CHAR_CONTAINER}')
                    .forEach((e)=>{e.classList.add(
                        '${confCss.PUZZLE_CELL_CHAR_ALL_FINISHED}')})
            }
        })
        pWSData.inpChar=pValue||" "
        ws.onopen=(ev)=>{ws.send(JSON.stringify(pWSData))}
    }
    function newGame(pWSData){
        function getGameType(){
            for(let t of document.getElementsByName(
                '${confCss.PUZZLE_TYPE_RADIO_GROUP_NAME}')){
                    if(t.checked){return t.value}
            }
            return '${PuzzleType.SCHWEDEN.name}'
        }
        let ws=new WebSocket('${webAppConf.WEB_SOCK_URL}')
        ws.onopen=(ev)=>{ws.send(pWSData.replace(
            "${webAppConf.PUZZLE_TYPE_PLACEHOLDER}", getGameType()))}
    }
    function showNewButton(pDoc){
        pDoc.getElementById('${confCss.SHOW_HELP_BUTTON_ID}')
            .style.display='none'
        pDoc.getElementById('${confCss.NEW_GAME_BUTTON_ID}')
            .style.display='block'
    }
    function showHelp(pWSData){                  
        var d=document
        showNewButton(d)
        let ws=new WebSocket('${webAppConf.WEB_SOCK_URL}')
        ws.addEventListener("message",(ev)=>{
            JSON.parse(ev.data).showPlaceholders.forEach(
                function(p){
                    var c=d.querySelector(p.selctr)
                    c.placeholder=p.plcHldr
                    if(c.value!=p.plcHldr){c.value=''}
                }
            )
        })
        ws.onopen=(ev)=>{ws.send(pWSData)}
    }
    function adjustFontSize(){
        let d=document
        let t=d.getElementById('${confCss.GRID_TABLE}')
        let dim=t.rows[0].cells.length
        let mw=Math.trunc(t.clientWidth/dim)+'px'
        let mh=Math.trunc(t.clientHeight/dim)
        let fs=mh-(mh/100*${confCss.PUZZLE_CELL_CHAR_DECR_HGHT_PERC})+'px'
        mh=mh+'px'
        d.querySelectorAll('.${confCss.PUZZLE_CELL_CHAR}')
            .forEach(c=>{
                let s=c.style
                s.fontSize=fs;s.maxWidth=mw;s.maxHeight=mh
            })
    }
    //window.addEventListener('resize', adjustFontSize)
    window.addEventListener('load', adjustFontSize)
""".lines().map{it.trimStart().trimEnd()}.joinToString("\n")
