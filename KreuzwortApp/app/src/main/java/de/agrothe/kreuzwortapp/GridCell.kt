package de.agrothe.kreuzwortapp

import io.ktor.server.html.*
import kotlinx.css.*
import kotlinx.html.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val webAppConf = config.webApp
private val cssConf = webAppConf.CSS

fun getRowColIdx(pRowIdx: Int, pColIdx: Int) = "${pRowIdx}_${pColIdx}"
fun Int.lgndIdSuffxRow() = "${cssConf.LGND_ID_SUFFX_ROW}$this"
fun Int.lgndIdSuffxCol() = "${cssConf.LGND_ID_SUFFX_COL}$this"

fun Css.dirImg(
        pDirct: KeyDirct?,
        pIdx: Int, pRowIdx: Int, pColIdx: Int, pRot: Pair<String, String>,
        pDimen: Int, pHorizntl: Boolean, pParentCnt: FlowContent)
    {
        pParentCnt.apply{
            span(if(pHorizntl) PUZZLE_CELL_IDX_NUM else PUZZLE_LGND_IDX_NUM){
                Pair(pDirct, pIdx).also{
                    if(it==Pair(KeyDirct.NORMAL, 0)
                            || it==Pair(KeyDirct.REVERSED, pDimen-1)){
                        +(if(pRot.first==IDX_SLCT_ROT_SOUTH)
                            pColIdx else pRowIdx).inc().toString()
                        img(classes=if(pDirct==KeyDirct.NORMAL)
                                pRot.first else pRot.second,
                            src=webAppConf.DIRCTN_IMG)
                    } else span(classes=pRot.first){+Entities.nbsp}
            }}
    }}

class GridCell(val pRowIdx: Int, val pColIdx: Int, val pChar: Char,
    val pWordAtX: DictSynmsOrnt?, val pWordAtY: DictSynmsOrnt?,
    val pDimen: Int, val pHashCode: HashCode)
        : Template<FlowContent>
{
override fun FlowContent.apply(){
    with(cssConf){
        div(classes=CELL_GRID){
            fun idx(pDirct: KeyDirct?, pIdx: Int, pRot: Pair<String, String>){
                div(classes=PUZZLE_CELL_GRID_IDX){
                        dirImg(pDirct, pIdx, pRowIdx, pColIdx, pRot, pDimen,
                    true, this)
                }
            }
            idx(pWordAtY?.ornt, pRowIdx,
                Pair(IDX_SLCT_ROT_SOUTH, IDX_SLCT_ROT_NORTH))
            div(classes=PUZZLE_CELL_CHAR_CONTAINER){
                val iD = getRowColIdx(pRowIdx, pColIdx)
                val wsdata = Json.encodeToString(
                    WSDataToSrvr('%', pRowIdx, pColIdx, pHashCode))
                input(
                    classes=
                        if(puzzleCache.get(pHashCode)?.run{
                            puzzleInPlay.get(pRowIdx).get(pColIdx) == pChar
                        } == true)
                            PUZZLE_CELL_CHAR_SOLVED else PUZZLE_CELL_CHAR,
                    type=InputType.text){
                    id=iD
                    maxLength="1"
                    // todo make interactive
                    placeholder=if(webAppConf.SHOW_INPUT_HINT)
                        pChar.toString() else ""
                    // todo does "new WS" reuse existing WS?
                    onClick="""
                        value=''
                        """.trimIndent()
                    onInput=""" 
                        if(value.length>1){value=value.charAt(0)}           
                        """.trimIndent()
                        // todo move to global function
                    onKeyUp="""
                        let ws=new WebSocket('${webAppConf.WEB_SOCK_ENDPOINT}')
                        ws.addEventListener("message",(ev)=>{
                            let d=document
                            let elm=d.getElementById('$iD')
                            let rpl=JSON.parse(ev.data)
                            if(rpl.charSolved==true){
                                elm.disabled=true
                                elm.className="$PUZZLE_CELL_CHAR_SOLVED"}
                            if(rpl.rowSolved==true){
                                let e=d.getElementById('${pRowIdx.lgndIdSuffxRow()}')
                                e.className=e.className+'$LGND_ENTRIES_SOLVED_SUFFX'}
                            if(rpl.colSolved==true){
                                let e=d.getElementById('${pColIdx.lgndIdSuffxCol()}')
                                e.className=e.className+'$LGND_ENTRIES_SOLVED_SUFFX'}
                        })
                        value=value.toUpperCase()
                        ws.onopen=(ev)=>{
                            ws.send('${wsdata}'.replace("%",value||" "))}
                        """.trimIndent()
                }
            }
            idx(pWordAtX?.ornt, pColIdx,
                Pair(IDX_SLCT_ROT_WEST, IDX_SLCT_ROT_EAST))
        }
    }
}
}
