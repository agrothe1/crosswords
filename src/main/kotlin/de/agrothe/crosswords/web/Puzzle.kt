package de.agrothe.crosswords.web

import de.agrothe.crosswords.*
import io.ktor.server.html.*
import kotlinx.html.*

private val conf by lazy{config.webApp}
private val confCss=conf.CSS

class BodyTplt: Template<HTML>{
    val header = Placeholder<FlowContent>()
    val puzzle = TemplatePlaceholder<PuzzleTplt>()
    override fun HTML.apply(){
        head{
            meta("viewport",
            "width=device-width, initial-scale=1.0")
            link(rel = "stylesheet", href="/styles.css", type = "text/css")
        }
        body{
            h1{
                insert(header)
            }
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
        getRandom(dimen).map{row->row.map{it.uppercaseChar()}.toCharArray()}
            .toTypedArray()

    override fun FlowContent.apply(){
        val cellTmplt = TemplatePlaceholder<CellTmplt>()
        with(confCss){
            table(GRID_TABLE){
                tbody{
                    puzzle.forEachIndexed{rowIdx, row->
                        tr(GRID_TABLE_ROW){
                            row.forEachIndexed{colIdx, char->
                                td(GRID_TABLE_COL +' '+
                                        TABLE_CELL_BACKGROUND
                                            +setOf(1,2).random()){
                                    insert(CellTmplt(rowIdx, colIdx,
                                        char, puzzle,
                                        entries.get(
                                            puzzle.getStringAt(Axis.X, rowIdx)),
                                        entries.get(
                                            puzzle.getStringAt(Axis.Y, colIdx)),
                                        dimen), cellTmplt)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

class CellTmplt(val pRowIdx: Int, val pColIdx: Int, val pChar: Char,
        val pPuzzle: Puzzle,
        val pWordAtX: DictSynmsOrnt?, val pWordAtY: DictSynmsOrnt?,
        val pDimen: Int): Template<FlowContent>
{
    override fun FlowContent.apply(){
        with(confCss){
            table{
                fun idx(pDirct: KeyDirct?, pIdx: Int,
                        pRot: Pair<String, String>){
                    tr{td{
                        table{tr{td{
                            span(PUZZLE_CELL_IDX_NUM){
                                Pair(pDirct, pIdx).also{
                                    if(it==Pair(KeyDirct.NORMAL, 0)
                                        ||it==Pair(KeyDirct.REVERSED, pDimen-1))
                                    span{
                                        if(pRot.first==IDX_SLCT_ROT_SOUTH)
                                            +pColIdx.inc().toString()
                                        else +pRowIdx.inc().toString()
                                        img(classes=
                                            if(pDirct==KeyDirct.NORMAL)
                                                pRot.first else pRot.second,
                                            src=conf.DIRCTN_IMG)
                                    }
                                    else {+Entities.nbsp}
                            }}
                    }}}}}
                }
                idx(pWordAtY?.ornt, pRowIdx,
                    Pair(IDX_SLCT_ROT_SOUTH, IDX_SLCT_ROT_NORTH))
                tr{td{
                    span(PUZZLE_CELL_CHAR){+pChar.toString()}
                }}
                idx(pWordAtX?.ornt, pColIdx,
                    Pair(IDX_SLCT_ROT_WEST, IDX_SLCT_ROT_EAST))
            }
        }
    }
}
