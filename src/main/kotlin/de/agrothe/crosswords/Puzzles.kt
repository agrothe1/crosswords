package de.agrothe.crosswords

import de.agrothe.crosswords.Axis.Companion.other
import de.agrothe.crosswords.Dict.Companion.getPattern
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger by lazy{KotlinLogging.logger{}}

private val dict by lazy {Dict(readConfig().dict)}
private val keys by lazy {dict.keys}

typealias Puzzle = Array<CharArray>

enum class Axis{
    X, Y;

    companion object{
        fun Axis.other(): Axis =
            when(this){
                X->Y
                Y->X
            }
    }
}

class Pos(val row: Int, val col: Int){
    fun incr(axis: Axis) =
        when(axis){
            Axis.X->Pos(row, col+1)
            Axis.Y->Pos(row+1, col)
        }
}

fun Puzzle.put(axis: Axis, pos: Pos, str: String): Puzzle{
    with(this.clone())
    {
        for((idx, c) in str.withIndex()){
             when(axis){
                 Axis.X->this[pos.row][idx] = c
                 Axis.Y->this[idx][pos.col] = c
             }
        }
        return this
    }
}

fun Puzzle.getStringAt(axis: Axis, pos: Pos): String =
    when(axis){
        Axis.X -> String(this[pos.row])
        Axis.Y -> pos.col.let{col->this.fold(StringBuilder())
            {sb, row->sb.append(row[col])}.toString()}
    }

fun getMatchingKeys(pattern: Regex): Collection<DictKey> =
    keys.mapNotNull{entry->
        if(pattern.matches(entry)) entry else null
    }

fun Puzzle.print(){
    this.forEach{row->row.forEach{print(it)}; print('\n')}
    println()
}

fun Puzzle.fillGrid(dir: Axis, pos: Pos, dimen: Int,
        usedWords: Set<String>): Puzzle?{
    let{puzzle->
        fun Axis.getMatches(idx: Int, strPos: Int): Collection<String> =
            getMatchingKeys(
                Regex(puzzle.getStringAt(axis=this, Pos(idx, strPos))
                    .getPattern(srcPos=0, destPos=0, destLen=dimen)))
            .minus(usedWords)

        val myUsedWords = usedWords.toMutableSet()
        dir.getMatches(pos.row, pos.col).shuffled()
            .forEach{word->
                puzzle.put(dir, Pos(0, pos.col), word)
                puzzle.print()
                dir.other().let{newDir->
                    puzzle.fillGrid(newDir,
                        pos, dimen, myUsedWords.apply{add(word)}.toSet())
            }
            /*
            puzzle.fillGrid(dir.other(), row+1, col+1, dimen,
                usedWords.run{plus(word)})
             */
        }
        return@fillGrid null
    }
}

fun emptyPuzzle(dimen: Int) =
    Array(dimen){_->CharArray(dimen){_-> '.'}}

fun main(){ // Saum Baum
    val dimen = 4
    val puzzle = emptyPuzzle(dimen)
    puzzle.fillGrid(Axis.X, Pos(0, 0), dimen, setOf())
}

