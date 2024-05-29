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
            "width=device-width, height=device-height, initial-scale=1.0")
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

    init{
        puzzleCache.put(puzzle.hashCode(), puzzle)
    }

    override fun FlowContent.apply(){
        val gridTmplt = TemplatePlaceholder<PuzzleGrid>()
        with(confCss){
            table{
                fun legendEntry(pSynm: String){
                    tr{
                        td{b{+Entities.middot}}
                        td{+pSynm}
                    }}
                tbody(classes=PUZZLE_TABLE){
                tr{
                    td(){
                        table(classes=LEGEND_TABLE){
                            tr{
                                th(classes=LEGEND_TABLE_HEADER)
                                    {colSpan="2"; +"Waagerecht"}//todo
                            }
                            puzzle.forEachIndexed{rowIdx, _->
                                tr{
                                    val synms= entries[
                                        puzzle.getStringAt(Axis.X, rowIdx)]
                                    td(classes=PUZZLE_CELL_IDX_NUM){
                                        synms?.ornt.let{ornt->
                                            dirImg(ornt,
                                                if(ornt == KeyDirct.NORMAL) 0
                                                    else dimen-1,
                                                rowIdx, 0,
                                                Pair(IDX_SLCT_ROT_WEST,
                                                    IDX_SLCT_ROT_EAST),
                                                dimen, conf, this)
                                        }}
                                    td{table{
                                        synms?.synms?.shuffled()?.take(2)
                                        ?.forEach{legendEntry(it)}

                                    }}
                                }}
                            tr{
                                th(classes=LEGEND_TABLE_HEADER)
                                    {colSpan="2"; +"Senkrecht"}//todo
                            }
                            puzzle.forEachIndexed{colIdx, _->
                                tr{
                                    val synms= entries[
                                        puzzle.getStringAt(Axis.Y, colIdx)]
                                    td(classes=PUZZLE_CELL_IDX_NUM){
                                        synms?.ornt.let{ornt->
                                            dirImg(ornt,
                                                if(ornt == KeyDirct.NORMAL) 0
                                                    else dimen-1,
                                        0, colIdx,
                                                Pair(IDX_SLCT_ROT_SOUTH,
                                                    IDX_SLCT_ROT_NORTH),
                                                dimen, conf,this)
                                    }}
                                    td{table{
                                        synms?.synms?.shuffled()?.take(2)
                                            ?.forEach{legendEntry(it)}
                                    }}
                                }}
                        }
                    }
                    td{
                        insert(PuzzleGrid(entries, puzzle, dimen, conf),
                            gridTmplt)
                    }
                }}
            }
        }
    }
}

// todo separate
class PuzzleGrid(val pEntries: DictEntry, val puzzle: Puzzle, val pDimen: Int,
    val pConf: WebAppConfig)
        : Template<FlowContent> {
    override fun FlowContent.apply(){
        val cellTmplt = TemplatePlaceholder<GridCell>()
        with(pConf.CSS){
            table(GRID_TABLE){
                tbody{
                    puzzle.forEachIndexed{rowIdx, row->
                        tr(GRID_TABLE_ROW){
                            row.forEachIndexed{colIdx, char->
                                td(GRID_TABLE_COL +' '+
                                        TABLE_CELL_BACKGROUND
                                            +setOf(1,2).random()){
                                    insert(GridCell(rowIdx, colIdx, char,
                                        pEntries[
                                            puzzle.getStringAt(Axis.X, rowIdx)],
                                        pEntries[
                                            puzzle.getStringAt(Axis.Y, colIdx)],
                                        pDimen, pConf, puzzle.hashCode()),
                                        cellTmplt)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
