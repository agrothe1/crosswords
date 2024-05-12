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
        table(confCss.GRID_TABLE){
            tbody{
                puzzle.forEachIndexed{rowIdx, row->
                    val dictEntryRow= entries.get(
                        puzzle.getStringAt(Axis.X, rowIdx))
                    tr{
                        row.forEachIndexed{colIdx, char->
                            td{
                                insert(CellTmplt(rowIdx, colIdx, char, puzzle,
                                    dictEntryRow,
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

class CellTmplt(val pRowIdx: Int, val pColIdx: Int, val char: Char,
        val pPuzzle: Puzzle,
        val wordAtX: DictSynmsOrnt?, val wordAtY: DictSynmsOrnt?,
        val pDimen: Int): Template<FlowContent>{
    override fun FlowContent.apply(){
        span(confCss.GRID_TABLE_CELL){
            wordAtY?.ornt.let{ornt->
                when(pRowIdx){
                    0->         ornt==KeyDirct.NORMAL
                    pDimen-1->  ornt==KeyDirct.REVERSED
                    else->      false
                }.let{show->
                    if(show){
                        span(confCss.PUZZLE_CELL_IDX_NUM){
                            +pColIdx.inc().toString()}
                        img(classes=
                                if(ornt==KeyDirct.NORMAL)
                                    confCss.IDX_SLCT_ROT_SOUTH
                                else confCss.IDX_SLCT_ROT_NORTH,
                            src=conf.DIRCTN_IMG)
                    }
                    else{span(); span()}
                }
            }
            span(confCss.PUZZLE_CELL_CHAR){+char.toString()}
        }
    }
}



