package de.agrothe.crosswords.web

import de.agrothe.crosswords.*
import io.ktor.server.html.*
import kotlinx.html.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Css.dirImg(
        pDirct: KeyDirct?,
        pIdx: Int, pRowIdx: Int, pColIdx: Int, pRot: Pair<String, String>,
        pDimen: Int, pConf: WebAppConfig, pParentCnt: FlowContent)
    {
        pParentCnt.apply {
            span(PUZZLE_CELL_IDX_NUM){
                Pair(pDirct, pIdx).also{
                    table{tr{
                        if(it==Pair(KeyDirct.NORMAL, 0)
                            ||it==Pair(KeyDirct.REVERSED, pDimen-1)){
                            td{
                                +(if(pRot.first==IDX_SLCT_ROT_SOUTH)
                                    pColIdx else pRowIdx).inc().toString()
                            }
                            td{
                                img(classes=
                                    if(pDirct==KeyDirct.NORMAL)
                                        pRot.first else pRot.second,
                                    src=pConf.DIRCTN_IMG)
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
                            pConf, this)
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
                    onKeyUp="""
                let ws=new WebSocket('${pConf.WEB_SOCK_ENDPOINT}')
                ws.addEventListener("message",(ev)=>{
                    let elm=document.getElementById('$iD')
                    if(ev.data=='true'){
                        elm.disabled=true
                        elm.className="$PUZZLE_CELL_CHAR_SOLVED"
                    }
                    else{elm.className="$PUZZLE_CELL_CHAR"}
                })
                value=value.toUpperCase()
                ws.onopen=(event)=>{
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
