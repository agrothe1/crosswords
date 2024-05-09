package de.agrothe.crosswords.web

import de.agrothe.crosswords.*
import io.ktor.server.html.*
import kotlinx.html.*

private val conf=config.webApp

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
    val entries = dict.entries
        .map{(key, values)
            ->Pair(key.uppercase(), values)}.toMap()
    private val puzzle: Puzzle =
        getRandom(dimen).map{row->row.map{it.uppercaseChar()}.toCharArray()}
            .toTypedArray()

    override fun FlowContent.apply(){
        val indxTmplt = TemplatePlaceholder<IndxTmplt>()
        table{
            tbody{
                puzzle.forEachIndexed{rowIdx, row->
                    tr{
                        row.forEachIndexed{colIdx, char->
                            td{
                                insert(IndxTmplt(rowIdx, colIdx,
                                    puzzle, entries, dimen), indxTmplt)
                                +"$char"
                            }
                        }
                    }
                }
            }
        }
    }
}

class IndxTmplt(val pRowIdx: Int, val pColIdx: Int,
        val pPuzzle: Puzzle, val pEntries: DictEntry,
        val pDimen: Int): Template<FlowContent>{
    override fun FlowContent.apply(){
        fun getOrntAndClss(pAxis: Axis) =
            pEntries[pPuzzle.getStringAt(pAxis, pColIdx)]?.let{entry->
                Pair(entry.ornt,
                    if(pAxis == Axis.Y)
                            when(entry.ornt){
                                KeyDirct.NORMAL->"idxImageRotateDown"
                                else->"idxImageRotateUp"}
                        else
                            when(entry.ornt){
                                KeyDirct.NORMAL->"idxImageRotateLeft"
                                else->"idxImageRotateRight"},
                    )
            }
        span("idx"){
            getOrntAndClss(Axis.Y)?.let{(ornt, drct)->
                when(pRowIdx){
                    0->if(ornt == KeyDirct.NORMAL){
                        +"${pColIdx+1}"
                        img(classes=drct, src=conf.DIRCTN_IMG)
                    }
                    (pDimen-1)->if(ornt == KeyDirct.REVERSED){
                        +"${pColIdx+1}"
                        img(classes=drct, src=conf.DIRCTN_IMG)
                    }
                }
            }
        }
    }
}



