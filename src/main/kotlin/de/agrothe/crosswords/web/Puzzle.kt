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
                                insert(CellTmplt(rowIdx, colIdx,
                                    char, puzzle, dictEntryRow,
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
        val pDimen: Int): Template<FlowContent>
{
    override fun FlowContent.apply(){
        with(confCss){
            table{
                tr{td{
                    table{tr{td{
                        span(PUZZLE_CELL_IDX_NUM){
                            wordAtY?.ornt.let{yOrnt->
                                if((yOrnt==KeyDirct.NORMAL && pRowIdx==0)
                                    || (yOrnt==KeyDirct.REVERSED
                                        && pRowIdx==pDimen-1))
                                    span{
                                        +pColIdx.inc().toString()
                                        img(classes=
                                            if(yOrnt==KeyDirct.NORMAL)
                                                IDX_SLCT_ROT_SOUTH
                                            else IDX_SLCT_ROT_NORTH,
                                                src=conf.DIRCTN_IMG)
                                    }
                                    else {+Entities.nbsp}
                            }
                        }
                    }}}
                }}
                tr{
                    td{
                        //colSpan="2"
                        //rowSpan="2"
                        span(confCss.PUZZLE_CELL_CHAR){+char.toString()}
                    }
                }
                tr{
                    td{}
                    td{}
                }
        }}
    }
}
        /*
            if(showRowIdx) {
                img(src = rowImg) {
                    classes = setOf(confCss.GRID_TABLE_CELL_TOP)
                }
                span {

                }
            }
                        .let{showCol->
                        wordAtX?.ornt.let{ornt-> Pair(showCol,
                            when(pColIdx){
                                0->         Pair(
                                                if(ornt==KeyDirct.NORMAL)
                                                    pRowIdx else null,
                                                IDX_SLCT_ROT_EAST)
                                pDimen-1->  Pair(
                                                if(ornt==KeyDirct.REVERSED)
                                                    null else pRowIdx,
                                                IDX_SLCT_ROT_WEST)
                                else->      Pair(null, "")
                            })
                        }}
                    span()
                        span(PUZZLE_CELL_IDX_NUM){+col.first}
                        span(){+col.second}
                        //img(classes="."+col.first, src=conf.DIRCTN_IMG)
                        span(PUZZLE_CELL_CHAR){+char.toString()}
                    span(PUZZLE_CELL_IDX_NUM){+row.first}
                        span(){+row.second}
                        //img(classes="."+row.second, src=conf.DIRCTN_IMG)
                }}
                   when{
                        col.first->when{
                            col.second == IDX_SLCT_ROT_SOUTH->{
                                span()
                                span(PUZZLE_CELL_IDX_NUM){
                                    +pColIdx.inc().toString()}
                                img(classes=col.second, src=conf.DIRCTN_IMG)
                                span()
                                    span(PUZZLE_CELL_CHAR){+char.toString()}
                                span()
                                span();span();span()
                            }
                            else->(1..9).forEach{span()}
                        }
                        else->(1..9).forEach{span()}
                    }
                    if(col.first && col.second == IDX_SLCT_ROT_SOUTH){ // show
                        span()
                        span(PUZZLE_CELL_IDX_NUM){
                            +pColIdx.inc().toString()}
                        img(classes=col.second, src=conf.DIRCTN_IMG)
                        span(PUZZLE_CELL_CHAR){+char.toString()}
                        span()
                    }
                    else{span(); span()}
        */
