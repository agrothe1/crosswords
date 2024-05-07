package de.agrothe.crosswords.web

import de.agrothe.crosswords.*
import io.ktor.server.html.*
import kotlinx.html.*

class BodyTplt: Template<HTML>{
    val header = Placeholder<FlowContent>()
    val puzzle = TemplatePlaceholder<PuzzleTplt>()
    override fun HTML.apply(){
        head{
            link(rel = "stylesheet", href = "/styles.css", type = "text/css")
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
    private val puzzle: Array<CharArray> =
        getRandom(dimen).map{row->row.map{it.uppercaseChar()}.toCharArray()}
            .toTypedArray()

    override fun FlowContent.apply(){
        val index = TemplatePlaceholder<IdxTplt>()
        table{
            tbody{
                puzzle.forEachIndexed{rowIdx, row->
                    tr{
                        row.forEachIndexed{colIdx, col->
                            td{
                                insert(IdxTplt(rowIdx, colIdx,
                                    puzzle, entries, dimen), index)
                                +"$col"
                            }
                        }
                    }
                }
            }
        }
    }
}

class IdxTplt(val rowIdx: Int, val colIdx: Int,
        val puzzle: Array<CharArray>, val entries: DictEntry, val pDimen: Int)
            : Template<FlowContent>{
    override fun FlowContent.apply(){
        span("idx"){
            if((rowIdx == 0 &&
                entries[puzzle.getStringAt(Axis.Y, colIdx)]
                    ?.ornt == KeyDirct.NORMAL)
                ||(rowIdx == pDimen-1 &&
                    entries[puzzle.getStringAt(Axis.Y, colIdx)]
                        ?.ornt == KeyDirct.REVERSED))
                   +"${colIdx+1}"
            }
       }
}



