package de.agrothe.crosswords.web

import de.agrothe.crosswords.*
import de.agrothe.kreuzwortapp.web.GridCell
import de.agrothe.kreuzwortapp.web.dirImg
import io.ktor.server.html.*
import kotlinx.css.td
import kotlinx.html.*

private val conf by lazy{config.webApp}
private val confCss=conf.CSS

class BodyTplt: Template<HTML>{
    val header = Placeholder<FlowContent>()
    val puzzle = TemplatePlaceholder<PuzzleTplt>()
    override fun HTML.apply(){
        lang="de"
        head{
            meta{
                charset="utf-8"
                name="viewport"
                content="height=device-height"
            }
            link{
                rel="stylesheet"
                href="/styles.css"
                type="text/css"
            }
        }
        body{
            /*
            h1{
                insert(header)
            }
             */
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
        getRandom(dimen)!!.map{row->row.map{it.uppercaseChar()}.toCharArray()}
            .toTypedArray()

    init{
        puzzleCache.put(puzzle.hashCode(),
            PuzzleCacheEntry(puzzle, emptyPuzzle(dimen)))
    }

    override fun FlowContent.apply(){
        val gridTmplt = TemplatePlaceholder<PuzzleGrid>()
        with(confCss){
            fun TR.legendIdx(pRowIdx: Int, pColIdx: Int, pSynms: DictSynmsOrnt?,
                    pIdxSelct: Pair<String, String>) =
                td(classes=PUZZLE_CELL_IDX_NUM){
                    pSynms?.ornt.let{ornt->
                        dirImg(ornt,
                            if(ornt == KeyDirct.NORMAL) 0 else dimen-1,
                            pRowIdx, pColIdx, pIdxSelct, dimen, conf,
                            false, this)
                }}
            fun TABLE.legendEntry(pSynm: String) =
                tr{
                    td{b{+Entities.middot}}
                    conf.LEGND_ENTR_SUBST_REGEX
                        .replace(pSynm, "").also{
                            td{+it}
                    }
                }
            fun TD.legendEntries(pSynms: DictSynms?, pId: String) =
                table(classes=LGND_ENTRIES){
                    id=pId
                    // todo max synms -> config
                    pSynms?.shuffled()?.take(2)?.forEach{legendEntry(it)}
            }

            div{
                div(classes=PUZZLE_GRID){
                    div(classes=LGND_GRID_HORIZ){
                        table(classes=LGND_TABLE){
                            tr{
                                th(classes=LGND_TABLE_HEADER)
                                    {colSpan="2"; +conf.I18n.HORIZONTAL}
                            }
                            puzzle.forEachIndexed{rowIdx, _->
                                tr{
                                    entries[puzzle.getStringAt(Axis.X, rowIdx)]
                                        ?.let{
                                            legendIdx(rowIdx, 0, it,
                                                Pair(IDX_SLCT_ROT_WEST,
                                                    IDX_SLCT_ROT_EAST))
                                            td{legendEntries(it.synms,
                                                rowIdx.lgndIdSuffxRow())}
                            }}}
                        }
                    }
                    div(classes=FIELD_GRID){
                        insert(PuzzleGrid(entries, puzzle, dimen, conf),
                            gridTmplt)
                    }
                    div(classes=LGND_GRID_VERT){
                        table(classes=LGND_TABLE){
                            tr{
                                th(classes=LGND_TABLE_HEADER)
                                    {colSpan="2"; +conf.I18n.VERTICAL}
                            }
                            puzzle.forEachIndexed{colIdx, _->
                                tr{
                                    entries[puzzle.getStringAt(Axis.Y, colIdx)]
                                        ?.let{
                                            legendIdx(0, colIdx, it,
                                                Pair(IDX_SLCT_ROT_SOUTH,
                                                    IDX_SLCT_ROT_NORTH))
                                            td{legendEntries(it.synms,
                                                colIdx.lgndIdSuffxCol())}
                            }}}
                        }
                    }
                }
            }
        }
    }
}

// todo separate
class PuzzleGrid(val pEntries: DictEntry, val puzzle: Puzzle, val pDimen: Int,
        val pConf: WebAppConfig): Template<FlowContent>{
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
