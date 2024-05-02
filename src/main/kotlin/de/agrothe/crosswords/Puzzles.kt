package de.agrothe.crosswords

import de.agrothe.crosswords.Axis.X
import de.agrothe.crosswords.Axis.Y
import de.agrothe.crosswords.Pos.Companion.advance
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.random.Random

private val logger by lazy{KotlinLogging.logger{}}

private val dict by lazy {Dict(readConfig().dict)}
private val keys by lazy {dict.keys}

typealias Puzzle = Array<CharArray>

enum class Axis{
    X, Y
}

class Pos(val row: Int, val col: Int){
    override fun toString() = "$row,$col"

    companion object{
        fun Pos.advance(pAxis: Axis): Pair<Axis, Pos> =
            when(pAxis){
                X->Pair(Y, this)
                Y->Pair(X, Pos(this.row+1, this.col+1))
            }
        }
}

fun Puzzle.put(pAxis: Axis, pPos: Pos, pStr: String): Puzzle =
    foldIndexed(copyOf()) // clone/copy Puzzle
            {idx, arr, line->arr[idx]=line.copyOf(); arr}
        .also{newPuzzle->
            for((idx, c) in pStr.withIndex()){
                when(pAxis){
                    X->newPuzzle[pPos.row][idx] = c
                    Y->newPuzzle[idx][pPos.col] = c
                }
            }
    }

fun Puzzle.getStringAt(pAxis: Axis, pPos: Pos): String =
    when(pAxis){
        X -> String(this[pPos.row])
        Y -> pPos.col.let{col->this.fold(StringBuilder())
            {sb, row->sb.append(row[col])}}.toString()
    }

fun getMatchingKeys(pPattern: Regex): Collection<DictKey> =
    keys.mapNotNull{entry->
        if(pPattern.matches(entry)) entry else null
    }

fun Puzzle.print(){
    this.forEach{row->row.forEach{print(it)}; print('\n')}
    println()
}

fun Puzzle?.fillGrid(pAxis: Axis, pPos: Pos, pDimen: Int,
        pUsedWords: Set<String>): Puzzle?{
    if(pPos.row == pDimen || pPos.col == pDimen) return this // solved
    this?.let{puzzle->
        //puzzle.print()
        fun Axis.getMatches(): Collection<String> =
            getMatchingKeys(Regex(puzzle.getStringAt(pAxis=this, pPos),
                    RegexOption.IGNORE_CASE))
                .minus(pUsedWords)

        pUsedWords.toMutableSet().let{usedWords->
            pPos.advance(pAxis).let{(nextAxis, nextPos)->
                pAxis.getMatches()
                    .shuffled()
                    .forEach{word->
                        usedWords.add(word)
                        println("$nextAxis $nextPos: $word")
                        put(pAxis, pPos, word)
                            .fillGrid(nextAxis, nextPos, pDimen,
                                    usedWords.toSet())
                                ?.run{return@fillGrid this}
                    }
            }
        }
    }
    return null
}

fun emptyPuzzle(pDimen: Int) =
    Array(pDimen){_->CharArray(pDimen){_-> '.'}}

fun main(){ // Saum Baum
    val dimen = 4
    val puzzle = emptyPuzzle(dimen)
    puzzle.fillGrid(Axis.X, Pos(0, 0), dimen, setOf())
        ?.print()
}


