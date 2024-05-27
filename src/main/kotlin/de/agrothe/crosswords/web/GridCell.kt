package de.agrothe.crosswords.web

import de.agrothe.crosswords.*
import io.ktor.server.html.*
import kotlinx.html.*
import kotlinx.serialization.StringFormat
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Formatter

fun Css.dirImg(
        pDirct: KeyDirct?,
        pIdx: Int, pRowIdx: Int, pColIdx: Int, pRot: Pair<String, String>,
        pDimen: Int, pConf: WebAppConfig, pParentCnt: FlowContent)
    {
        pParentCnt.apply {
            span(PUZZLE_CELL_IDX_NUM){
                Pair(pDirct, pIdx).also{
                    if(it==Pair(KeyDirct.NORMAL, 0)
                        || it==Pair(KeyDirct.REVERSED, pDimen-1))
                        table{tr{td{
                                +(if(pRot.first==IDX_SLCT_ROT_SOUTH)
                                    pColIdx else pRowIdx).inc().toString()
                            };td{
                                img(classes=
                                    if(pDirct==KeyDirct.NORMAL)
                                        pRot.first else pRot.second,
                                    src=pConf.DIRCTN_IMG)
                        }}}
                    else +Entities.nbsp
            }}
    }}

class GridCell(val pRowIdx: Int, val pColIdx: Int, val pChar: Char,
    val pWordAtX: DictSynmsOrnt?, val pWordAtY: DictSynmsOrnt?,
    val pDimen: Int, val pConf: WebAppConfig): Template<FlowContent>
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
                val wsdata = Json.encodeToString(
                    WSData(pChar, '%', pColIdx, pRowIdx))
                input(classes=PUZZLE_CELL_CHAR, type=InputType.text){
                    maxLength="1"
                    placeholder=pChar.toString() // todo configurable
                    // localhost:8080/verify: todo refactor to gloabal setting
                    onKeyUp="""
                        let s=new WebSocket("ws://localhost:8080/verify")
                        value=value.toUpperCase()
                        s.onopen=(event)=>{s.send(
                            '${wsdata}'.replace("%", value||" ") 
                        )}
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
