package de.agrothe.crosswords

import de.agrothe.crosswords.Dict.Companion.getPattern
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.time.measureTime

private val logger by lazy { KotlinLogging.logger{} }

private val config = readConfig()
private val dict by lazy {Dict(config.dict)}

fun main() {
    //dict.dict.keys.take(50).forEach {logger.debug{it}}

    /*
    dict.getMatches(".ruckm....l").forEach{
        logger.debug{".ruckm....l '$it'"}
    }
     */

    val word = "Bier"
    val (srcPos, destPos, destLen) = listOf(1, 1, 4)
    val pattern = word.getPattern(srcPos, destPos, destLen)
    val regex = Regex(pattern)

    logger.debug{
        "$word (src:$srcPos, dest:$destPos, len:$destLen): '$pattern'"}

    var matches: List<Pair<String, Set<String>>>
    var timeTaken = measureTime{
        matches = dict.getMatches(regex)
    }
    println("COLD dict search time: $timeTaken")

    timeTaken = measureTime{
        matches = dict.getMatches(regex)
    }
    println("HOT dict search time: $timeTaken")

    logger.debug{"matches"}
    logger.debug{"XXX"}
    matches.forEach{logger.debug{"\t$it"}}
}





