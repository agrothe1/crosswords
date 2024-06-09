package de.agrothe.kreuzwortapp.web

import de.agrothe.crosswords.*
import de.agrothe.crosswords.web.HashCode
import de.agrothe.crosswords.web.WSData
import io.ktor.server.html.*
import kotlinx.css.*
import kotlinx.html.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Css.dirImg(
        pDirct: KeyDirct?,
        pIdx: Int, pRowIdx: Int, pColIdx: Int, pRot: Pair<String, String>,
        pDimen: Int, pConf: WebAppConfig, pHorizntl: Boolean,
        pParentCnt: FlowContent)
    {
        fun TR.idx() = td{
            +(if(pRot.first==IDX_SLCT_ROT_SOUTH)
                pColIdx else pRowIdx).inc().toString()}

        fun TD.idxImg() = img(
            classes=if(pDirct==KeyDirct.NORMAL) pRot.first else pRot.second,
            src=pConf.DIRCTN_IMG)

        pParentCnt.apply{
            span(if(pHorizntl) PUZZLE_CELL_IDX_NUM else PUZZLE_LGND_IDX_NUM){
                Pair(pDirct, pIdx).also{
                    table{tr{
                        if(it==Pair(KeyDirct.NORMAL, 0)
                                ||it==Pair(KeyDirct.REVERSED, pDimen-1)){
                            if(pHorizntl){
                                idx()
                                td{idxImg()}
                            }
                            else{
                                td{colSpan="2"
                                    table{
                                        tr{idx()}
                                        tr{td{idxImg()}}}
                                }
                            }
                        }else
                            td(classes=pRot.first){colSpan="2"; +Entities.nbsp}
                    }}
            }}
    }}

class GridCell(val pRowIdx: Int, val pColIdx: Int, val pChar: Char,
    val pWordAtX: DictSynmsOrnt?, val pWordAtY: DictSynmsOrnt?,
    val pDimen: Int, val pConf: WebAppConfig, val pHashCode: HashCode)
        : Template<FlowContent>
{
override fun FlowContent.apply(){
    with(pConf.CSS){
        table{
            fun idx(pDirct: KeyDirct?, pIdx: Int, pRot: Pair<String, String>){
                tr{td{
                    table{tr{td{
                        dirImg(pDirct, pIdx, pRowIdx, pColIdx, pRot, pDimen,
                            pConf, true, this)
                }}}}}
            }
            idx(pWordAtY?.ornt, pRowIdx,
                Pair(IDX_SLCT_ROT_SOUTH, IDX_SLCT_ROT_NORTH))
            tr{td{
                val iD="${pRowIdx}_${pColIdx}"
                val wsdata = Json.encodeToString(
                    WSData('%', pColIdx, pRowIdx, pHashCode))
                input(classes=PUZZLE_CELL_CHAR, type=InputType.text){
                    id=iD
                    maxLength="1"
                    placeholder=pChar.toString() // todo configurable
                    // todo does "new WS" reuse existing WS?
                    onClick="""
                        value=''
                        """.trimIndent()
                    onInput=""" 
                        if(value.length>1){value=value.charAt(0)}           
                        """.trimIndent()
                        // todo move to global function
                    onKeyUp="""
                        let ws=new WebSocket('${pConf.WEB_SOCK_ENDPOINT}')
                        ws.addEventListener("message",(ev)=>{
                            let e=document.getElementById('$iD')
                            if(ev.data=='true'){
                                e.disabled=true
                                e.className="$PUZZLE_CELL_CHAR_SOLVED"
                            }
                            else{e.className="$PUZZLE_CELL_CHAR"}
                        })
                        value=value.toUpperCase()
                        ws.onopen=(ev)=>{
                            ws.send('${wsdata}'.replace("%",value||" "))}
                        """.trimIndent()
                }
                //span(PUZZLE_CELL_CHAR){+pChar.toString()}
            }}
            idx(pWordAtX?.ornt, pColIdx,
                Pair(IDX_SLCT_ROT_WEST, IDX_SLCT_ROT_EAST))
        }
    }
}
}
