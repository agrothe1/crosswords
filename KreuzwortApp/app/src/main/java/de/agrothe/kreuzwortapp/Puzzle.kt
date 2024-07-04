package de.agrothe.kreuzwortapp

import io.ktor.server.html.*
import kotlinx.html.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val confCss=confWeb.CSS

class BodyTplt: Template<HTML>{
    val header = Placeholder<FlowContent>()
    val puzzle = TemplatePlaceholder<PuzzleTplt>()
    override fun HTML.apply(){
        lang="de"
        head{
            meta{
                charset="utf-8"
                name="viewport"
                content="height=device-height,width=device-width"
            }
            link{
                rel="stylesheet"
                href="/styles.css"
                type="text/css"
            }
        }
        body{
            /*
            h1{
                insert(header)
            }
             */
            insert(PuzzleTplt(), puzzle)
        }
    }
}

class PuzzleTplt: Template<FlowContent>{
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

            div{
                div(classes=PUZZLE_GRID){
                    button(classes=NEW_GAME){
                        id=SHOW_HELP_BUTTON_ID
                        hidden=false
                        val wsdata = Json.encodeToString(
                            WSDataToSrvr(showHelp=true,
                                hashCode=puzzle.hashCode()))
                        style=NEW_GAME_BUTTON_STYLE
                        onClick=
                    """
                        let d=document
                        d.getElementById('$SHOW_HELP_BUTTON_ID')
                            .style.display='none'
                        d.getElementById('$NEW_GAME_BUTTON_ID')
                            .style.display='block'
                        let ws=new WebSocket('${webAppConf.WEB_SOCK_ENDPOINT}')
                        ws.addEventListener("message",(ev)=>{ 
                            JSON.parse(ev.data).showPlaceholders.forEach(
                                function(p){d.querySelector(p.selctr)
                                    .placeholder=p.plcHldr})})
                        ws.onopen=(ev)=>{ws.send('${wsdata}')}
                    """.trimIndent()
                            +confWeb.I18n.SHOW_HELP
                    }
                    /*
                    d.querySelectorAll('[id^="${pRowIdx}_"]').forEach(e=>{
                     */
                    button(classes=NEW_GAME){
                        id=NEW_GAME_BUTTON_ID
                        hidden=true
                        val wsdata = Json.encodeToString(
                            WSDataToSrvr(newGame=true))
                        style=NEW_GAME_BUTTON_STYLE
                        onClick=
                """ 
                    let ws=new WebSocket('${webAppConf.WEB_SOCK_ENDPOINT}')
                    ws.onopen=(ev)=>{ws.send('${wsdata}')}
                """.trimIndent()
                        +confWeb.I18n.NEW_GAME
                        img(classes=IDX_SLCT_ROT_WEST,
                            src=webAppConf.DIRCTN_IMG)
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
                        }
                    }
                }
            }
        }
    }
}
