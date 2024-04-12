package de.agrothe.crosswords

import de.agrothe.crosswords.Dict.Companion.getPattern
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger by lazy { KotlinLogging.logger{} }

private val config = readConfig()
private val dict = Dict(config.dict)

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

    val matches = dict.getMatches(regex)

    logger.debug{"matches"}
    matches.forEach{logger.debug{"\t$it"}}
    logger.debug{"XXX"}
}





