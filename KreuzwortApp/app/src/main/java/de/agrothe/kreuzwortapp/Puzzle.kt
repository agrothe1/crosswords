package de.agrothe.kreuzwortapp

import io.ktor.server.html.*
import kotlinx.html.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val confCss=confWeb.CSS

class BodyTplt(val pNumSolvedGames: Int): Template<HTML>{
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
                href="/styles.css"
                type="text/css"
            }
            link{
                rel="stylesheet"
                href="/css/anim.css"
                type="text/css"
            }
            /*
            link{
                href="/Callbacks.js"
                type="text/javascript"
            }
             */
        }
        body{
            /*
            h1{
                insert(header)
            }
             */
            insert(PuzzleTplt(pNumSolvedGames), puzzle)
            script{unsafe{raw(scripts)}}
            //script(src="js/Callbacks.js"){}
        }}
}

class PuzzleTplt(val pNumSolvedGames: Int): Template<FlowContent>{
    val dimen = 4 // todo
    private val entries = dict.entries
        .map{(key, values)
            ->Pair(key.uppercase(), values)}.toMap()

    private val puzzle: Puzzle =
        restoredGame?.puzzleInPlay
            ?: getRandom(dimen)!!.map{row->row.map{it.uppercaseChar()}
                .toCharArray()}.toTypedArray()

    init{
        if(restoredGame==null)
            puzzleCache.put(puzzle.hashCode(),
                PuzzleCacheEntry(puzzle, emptyPuzzle(dimen)))
        else
            restoredGame?.apply{
                puzzleCache.put(puzzleGenerated.hashCode(),
                    PuzzleCacheEntry(puzzleGenerated, puzzleInPlay))
                    // todo mark solved
            }
    }

    override fun FlowContent.apply(){
        val gridTmplt = TemplatePlaceholder<PuzzleGrid>()
        with(confCss){
            fun TR.legendIdx(pRowIdx: Int, pColIdx: Int, pSynms: DictSynmsOrnt?,
                    pIdxSelct: Pair<String, String>) =
                pSynms?.ornt.let{ornt->
                    td(classes=if(ornt == KeyDirct.NORMAL)
                            PUZZLE_CELL_IDX_NUM_HOR
                            else PUZZLE_CELL_IDX_NUM_VER)
                        {
                            dirImg(ornt,
                                if(ornt == KeyDirct.NORMAL) 0 else dimen-1,
                                pRowIdx, pColIdx, pIdxSelct, dimen,
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
            fun TD.legendEntries(pSynms: DictSynms?, pId: String,
                    pLast: Boolean, pHoriz: Boolean) =
                table(classes=
                    (if(pHoriz)LGND_ENTRIES_HOR else LGND_ENTRIES_VER)
                        +if(pLast) " ${
                            if(pHoriz)LGND_ENTRIES_HOR_LAST 
                            else LGND_ENTRIES_VER_LAST}"
                        else "")
                    {
                        id=pId
                        pSynms?.shuffled()?.take(confWeb.MAX_SYNMS)
                            ?.foldIndexed(mutableListOf<String>()){
                                idx, acc, syn->
                                    if(idx==0 || acc.sumOf{it.length}+syn.length
                                            < confWeb.SYNMS_TOTAL_LNGTH_THRSHLD)
                                        acc.add(syn); acc}
                            ?.forEach{legendEntry(it)}
                    }
            fun BUTTON.gameButton(pLabel: String, pShowImg: Boolean = false) =
                table{tr{td(classes=NUM_GAME){
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
                div(classes=PUZZLE_GRID){
                    button(classes=NEW_GAME){
                        id=SHOW_HELP_BUTTON_ID
                        hidden=false
                        val wsdata = Json.encodeToString(
                            WSDataToSrvr(showHelp=true,
                                hashCode=puzzle.hashCode()))
                        onClick="showHelp('$wsdata')"
                        gameButton(confWeb.I18n.SHOW_HELP)
                    }
                    button(classes=NEW_GAME){
                        id=NEW_GAME_BUTTON_ID
                        hidden=true
                        val wsdata = Json.encodeToString(
                            WSDataToSrvr(newGame=true))
                        onClick=
                """ 
                    let ws=new WebSocket('${webAppConf.WEB_SOCK_URL}')
                    ws.onopen=(ev)=>{ws.send('${wsdata}')}
                """.trimIndent()
                        gameButton(confWeb.I18n.NEW_GAME, true)
                    }
                    div(classes=LGND_GRID_HORIZ){
                        table(classes=LGND_TABLE){
                            tr{
                                th(classes=LGND_TABLE_HEADER_HOR)
                                    {colSpan="2"; +confWeb.I18n.HORIZONTAL}
                            }
                            puzzle.forEachIndexed{rowIdx, _->
                                tr{
                                    entries[puzzle.getStringAt(Axis.X, rowIdx)]
                                        ?.let{
                                            legendIdx(rowIdx, 0, it,
                                                Pair(IDX_SLCT_ROT_WEST,
                                                    IDX_SLCT_ROT_EAST)
                                                )
                                            td{legendEntries(it.synms,
                                                rowIdx.lgndIdSuffxRow(),
                                                rowIdx==dimen-1,
                                                false)}
                            }}}
                        }
                    }
                    div(classes=FIELD_GRID){
                        insert(PuzzleGrid(entries, puzzle, dimen, confWeb),
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
                                    entries[puzzle.getStringAt(Axis.Y, colIdx)]
                                        ?.let{
                                            legendIdx(0, colIdx, it,
                                                Pair(IDX_SLCT_ROT_SOUTH,
                                                    IDX_SLCT_ROT_NORTH))
                                            td{legendEntries(it.synms,
                                                colIdx.lgndIdSuffxCol(),
                                                colIdx==dimen-1,
                                                true)}
                            }}}
                        }
                    }
                }
            }
        }
    }
}

// todo separate
class PuzzleGrid(val pEntries: DictEntry, val puzzle: Puzzle, val pDimen: Int,
        val pConf: WebAppConfig): Template<FlowContent>{
    override fun FlowContent.apply(){
        val cellTmplt = TemplatePlaceholder<GridCell>()
        with(pConf.CSS){
            table(GRID_TABLE){
                tbody{
                    puzzle.forEachIndexed{rowIdx, row->
                        tr(GRID_TABLE_ROW){
                            row.forEachIndexed{colIdx, char->
                                td(GRID_TABLE_COL +' '+
                                        TABLE_CELL_BACKGROUND
                                            +setOf(1,2).random()){
                                    insert(GridCell(rowIdx, colIdx, char,
                                        pEntries[
                                            puzzle.getStringAt(Axis.X, rowIdx)],
                                        pEntries[
                                            puzzle.getStringAt(Axis.Y, colIdx)],
                                        pDimen, puzzle.hashCode()),
                                            cellTmplt)
                                }
                            }
            }}}}
        }
    }
}

val scripts="""
    function showNewButton(pDoc){
        pDoc.getElementById('${confCss.SHOW_HELP_BUTTON_ID}')
            .style.display='none'
        pDoc.getElementById('${confCss.NEW_GAME_BUTTON_ID}')
            .style.display='block'
    }
    function checkCellInput(pValue, pWSData, pRowIdx, pColIdx, pRowId, pColId){
        var d=document
        var ws=new WebSocket('${webAppConf.WEB_SOCK_URL}')
        ws.addEventListener("message",(ev)=>{
            function rowColSolved(pId, pSel){
                var l=d.getElementById(pId)
                l.className=l.className+"${confCss.LGND_ENTRIES_SOLVED_SFX}"
                d.querySelectorAll(pSel).forEach(e=>{
                    e.disabled=true
                    e.className='${confCss.PUZZLE_CELL_CHAR_SOLVED}'
                })
            }
            var rpl=JSON.parse(ev.data)
            if(rpl.rowSolved===true){
                rowColSolved(pRowId, '[id^="'+pRowIdx+'_"]')
            }
            if(rpl.colSolved===true){
                rowColSolved(pColId, '[id$="_'+pColIdx+'"]')
            }
            if(rpl.puzzleSolved===true){
                showNewButton(d)
                d.querySelectorAll('.${confCss.PUZZLE_CELL_CHAR_SOLVED}')
                    .forEach((e)=>{
                        e.classList.remove('${confCss.PUZZLE_CELL_CHAR_SOLVED}') 
                        e.classList.add('${confCss.PUZZLE_CELL_CHAR_FINISHED}')})
            }
        })
        ws.onopen=(ev)=>{ws.send(pWSData.replace("%",pValue||" "))}
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
""".lines().map{it.trimStart().trimEnd()}.joinToString("\n")
