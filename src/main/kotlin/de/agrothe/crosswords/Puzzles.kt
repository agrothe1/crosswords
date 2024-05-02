package de.agrothe.crosswords

import de.agrothe.crosswords.Axis.X
import de.agrothe.crosswords.Axis.Y
import de.agrothe.crosswords.Pos.Companion.advance
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger by lazy{KotlinLogging.logger{}}

private val config by lazy{
    readConfig()
}
private val dict by lazy {
    Dict(config.dict)
}
private val keys by lazy {dict.keys}
private val puzzleConf by lazy {config.puzzle}

typealias Puzzle = Array<CharArray>

enum class Axis{
    X, Y
}

class Pos(val pos: Int){
    override fun toString() = "$pos"

    companion object{
        fun Pos.advance(pAxis: Axis): Pair<Axis, Pos> =
            when(pAxis){
                X->Pair(Y, this)
                Y->Pair(X, Pos(pos+1))
            }
        }
}

fun Puzzle.put(pAxis: Axis, pPos: Pos, pStr: String): Puzzle =
    foldIndexed(copyOf()) // clone/copy Puzzle
            {idx, arr, line->arr[idx]=line.copyOf(); arr}
        .also{newPuzzle->
            for((idx, c) in pStr.withIndex()){
                when(pAxis){
                    X->newPuzzle[pPos.pos][idx] = c
                    Y->newPuzzle[idx][pPos.pos] = c
                }
            }
        }

fun Puzzle.getStringAt(pAxis: Axis, pPos: Pos): String =
    when(pAxis){
        X -> String(this[pPos.pos])
        Y -> pPos.pos.let{col->this.fold(StringBuilder())
            {sb, row->sb.append(row[col])}}.toString()
    }

fun getMatchingKeys(pPattern: String): Collection<DictKey> =
    Regex(pPattern, RegexOption.IGNORE_CASE).let{regex->
        keys.mapNotNull{entry->
            if(entry.matches(regex)) entry else null
        }
    }

fun Puzzle.print(){
    this.forEach{row->row.forEach{print(it)}; print('\n')}
    println()
}

fun Puzzle?.fillGrid(pAxis: Axis, pPos: Pos, pDimen: Int,
        pUsedWords: MutableSet<String>): Puzzle?{
    if(pPos.pos == pDimen) return this // solved
    this?.also{puzzle->
        //puzzle.print()
        fun Axis.getMatches(): Collection<String> =
            getMatchingKeys(puzzle.getStringAt(pAxis=this, pPos))
                .minus(pUsedWords)

        pPos.advance(pAxis).also{(nextAxis, nextPos)->
            pAxis.getMatches()
                .shuffled()
                .forEach{word->
                    pUsedWords.addAll(if(puzzleConf.ALLOW_DUPLICATES)
                        setOf(word) else setOf(word, word.reversed()))
                    logger.debug{"$nextAxis $nextPos: $word"}
                    puzzle.put(pAxis, pPos, word)
                        .fillGrid(nextAxis, nextPos, pDimen,
                                HashSet(pUsedWords))
                            ?.run{return@fillGrid this}
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
    puzzle.fillGrid(X, Pos(0), dimen, mutableSetOf())
        ?.print()
}


