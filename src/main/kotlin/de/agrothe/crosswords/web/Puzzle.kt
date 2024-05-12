package de.agrothe.crosswords.web

import de.agrothe.crosswords.*
import io.ktor.server.html.*
import kotlinx.css.span
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
        table("gridTable"){
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
        /*
        fun getOrntAndClss(pAxis: Axis) =
            pEntries[pPuzzle.getStringAt(pAxis, pColIdx)]?.let{entry->
                Pair(entry.ornt,
                    if(pAxis == Axis.X)
                        when(entry.ornt){
                            KeyDirct.NORMAL->confCss.IDX_SLCT_ROT_NORTH
                            else->confCss.IDX_SLCT_ROT_SOUTH}
                    else
                        when(entry.ornt){
                            KeyDirct.NORMAL->confCss.IDX_SLCT_ROT_EAST
                            else->confCss.IDX_SLCT_ROT_WEST}
                    )
            }
         */

        span("gridTableCell"){
            wordAtY?.ornt.let{ornt->
                when(pRowIdx){
                    0->         ornt==KeyDirct.NORMAL
                    pDimen-1->  ornt==KeyDirct.REVERSED
                    else->      false
                }.let{show->
                    if(show){
                        span("puzzleCellIdxNum"){
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
            span("puzzleCellChar"){+char.toString()}
        }
    }
    /*
        span("idx"){
            getOrntAndClss(Axis.Y)?.let{(ornt, imgDirct)->
                when(pRowIdx){
                    0->if(ornt == KeyDirct.NORMAL){
                        +"${pColIdx+1}"
                        //img(classes=imgDirct, src=conf.DIRCTN_IMG)
                    }
                    (pDimen-1)->if(ornt == KeyDirct.REVERSED){
                        +"${pColIdx+1}"
                        //img(classes=imgDirct, src=conf.DIRCTN_IMG)
                    }
                }
            }
        }
*/
}



