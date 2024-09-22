package de.agrothe.kreuzwortapp

import de.agrothe.kreuzwortapp.Axis.*
import de.agrothe.kreuzwortapp.MainActivity.Companion.appAssets
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.File
import java.io.InputStream
import java.lang.System.currentTimeMillis
import kotlin.io.path.*

private val logger by lazy{KotlinLogging.logger{}}

typealias Puzzle = Array<CharArray>
typealias Keys = Collection<DictWord>
typealias Pos = Int

val config by lazy{readConfig()}

val dict by lazy {Dict(config.dict)}

enum class Axis{
    X, Y
}

fun Puzzle.sameContent(pOther: Puzzle): Boolean =
    zip(pOther).all{it.first.asList().equals(it.second.asList())}

fun Pos.advance(pAxis: Axis): Pair<Axis, Pos> =
    when(pAxis){
        X->Pair(Y, this)
        Y->Pair(X, this+1)
    }

fun Puzzle.put(pAxis: Axis, pPos: Pos, pStr: String): Puzzle =
    foldIndexed(this) // clone/copy Puzzle
            {idx, arr, line->arr[idx]=line.copyOf(); arr}
        .also{newPuzzle->
            for((idx, c) in pStr.withIndex()){
                when(pAxis){
                    X-> newPuzzle[pPos][idx] = c
                    Y-> newPuzzle[idx][pPos] = c
                }
            }
        }

fun Puzzle.getStringAt(pAxis: Axis, pPos: Pos): String =
    when(pAxis){
        X-> String(this[pPos])
        Y-> this.fold(StringBuilder())
            {sb, row->sb.append(row[pPos])}.toString()
    }

fun Keys.getMatchingKeys(pPattern: String): Collection<DictWord> =
    Regex(pPattern, RegexOption.IGNORE_CASE).let{regex->
        mapNotNull{key->
            if(key.matches(regex)) key else null
        }
    }

fun Puzzle.print(){
    this.forEach{row->row.forEach{print(it)}; print('\n')}
    println()
}

fun Puzzle.diff(pOther: Puzzle) =
    zip(pOther).mapIndexed{rowIdx, row->
        row.first.zip(row.second).mapIndexedNotNull{colIdx, col->
            if(col.first==col.second) null
            else Triple(rowIdx, colIdx, col.first)
    }}.flatten()

fun Puzzle?.fillGrid(pAxis: Axis, pPos: Pos, pDimen: Int,
        pUsedWords: MutableSet<String>, pKeys: Keys): Puzzle? =
    this?.let{puzzle->
        if(pPos == pDimen) return this // solved
        fun Axis.getMatches(): Collection<String> =
            pKeys.getMatchingKeys(puzzle.getStringAt(pAxis=this, pPos))
                .minus(pUsedWords)

        pPos.advance(pAxis).also{(nextAxis, nextPos)->
            pAxis.getMatches().shuffled()
                .forEach{word->
                    if(!config.puzzle.ALLOW_DUPLICATES)
                        pUsedWords.addAll(setOf(word, word.reversed()))
                    logger.debug{"$nextAxis $nextPos: $word"}
                    puzzle.copyOf().put(pAxis, pPos, word)
                        .fillGrid(nextAxis, nextPos, pDimen,
                                HashSet(pUsedWords), pKeys)
                            ?.run{return@fillGrid this}
                }
        }
        return null
    }

fun Puzzle.save(pFile: File): Puzzle =
    apply{pFile.writeText(this.fold(StringBuilder()){acc, row->
        acc.append(row.joinToString("", postfix="\n"))}
            .toString().removeSuffix("\n"))}

fun Puzzle.save(): Puzzle =
    save(File(Path("",
        *config.GENERATED_PUZZLES_DIR.plus(
            arrayOf(this.size.toString(), currentTimeMillis().toString()
                +config.GENERATED_PUZZLE_SUFFX))
        ).toAbsolutePath().createParentDirectories().toString()))

fun InputStream.readPuzzle(): Puzzle =
    reader().readLines().map{it.toCharArray()}.toTypedArray()

fun listDimens(): List<String>? =
    appAssets.list(
        Path("", *config.GENERATED_PUZZLES_DIR).toString())?.map{
            dir->dir.substringAfterLast('.')
        }

fun getRandom(pDimen: Int, pExcludes: Collection<String>?): Puzzle? =
//  File(Path("", *config.GENERATED_PUZZLES_DIR.plus(pDimen.toString()))
//        .listDirectoryEntries().random().toString()).run{
    Path("",
            *config.GENERATED_PUZZLES_DIR.plus(pDimen.toString())).toString()
        .let{path->
            appAssets.list(path)
                ?.filterNot{(pExcludes!=null && pExcludes.contains(it)).apply{
                    if(this==true) logger.info{"excluded puzzle: $it"}
                }}
                ?.random()?.let{fname->
                    savePuzzleHistory(fname)
                    appAssets.open(Path(path, fname).toString()).let{
                        logger.info{"reading puzzle: $fname"}
                        it.readPuzzle()
                    }
                }
        }?.randomMirror()

fun emptyPuzzle(pDimen: Int): Puzzle =
    Array(pDimen){_->CharArray(pDimen){_-> '.'}}

fun Puzzle.randomMirror(): Puzzle{
    fun revrt() = setOf(true, false).random()
    fun String.revert(pRvrt: Boolean) = if(pRvrt) reversed() else this
    val newPuzz = emptyPuzzle(size)
    Pair(revrt(), revrt()).let{
        forEachIndexed{idx, _->
            newPuzz.put(X, idx, getStringAt(X, idx).run{revert(it.first)})}
        forEachIndexed{idx, _->
            newPuzz.put(Y, idx, newPuzz.getStringAt(Y, idx)
                .run{revert(it.second)})}
    }
    return newPuzz
}

fun generate(pDimen: Int): Puzzle? =
    emptyPuzzle(pDimen)
        .fillGrid(X, 0, pDimen, hashSetOf(),
            dict.words.filter{it.length == pDimen})

fun main(){
    val numPuzzls = 10
    val dimen = 5

    (1..numPuzzls).forEach{
        generate(dimen)?.run{
            println("--> solution: $it")
            print()
            save()
        }
    }
}


