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
        val cellTmplt = TemplatePlaceholder<GridCell>()
        with(confCss){
            table(GRID_TABLE){
                tbody{
                    puzzle.forEachIndexed{rowIdx, row->
                        tr(GRID_TABLE_ROW){
                            row.forEachIndexed{colIdx, char->
                                td(GRID_TABLE_COL +' '+
                                        TABLE_CELL_BACKGROUND
                                            +setOf(1,2).random()){
                                    insert(GridCell(rowIdx, colIdx, char,
                                        entries.get(
                                            puzzle.getStringAt(Axis.X, rowIdx)),
                                        entries.get(
                                            puzzle.getStringAt(Axis.Y, colIdx)),
                                        dimen, conf), cellTmplt)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
