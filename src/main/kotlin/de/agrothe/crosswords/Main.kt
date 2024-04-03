package de.agrothe.crosswords

import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger{}

private val config = readConfig()
private val dict = Dict(config.dict).dict

fun main() {
    dict.take(50).forEach {logger.debug{it}}

    fun getMatches(patt: String): List<Pair<String, List<String>>> =
        dict.mapNotNull{entry ->
            if (Regex(patt, RegexOption.IGNORE_CASE).matches(entry.first))
                entry
            else null
        }

    fun xgetPattern(other: String, otherPos: Int, pos: Int, len: Int): String =
        StringBuilder((1..len).fold(""){ acc, _ -> acc + "." })
            .apply{this.setCharAt(pos, other[otherPos]) }.toString()

    getMatches(".ruckm....l").forEach{
        logger.debug{ ".ruckm....l '$it'\n"}
    }

    fun String.getPattern(pos: Int, otherPos: Int, otherLen: Int): String =
        let{rec -> StringBuilder().apply{repeat(otherLen){append(".")}}
            .apply{setCharAt(pos, rec[otherPos])}.toString()}


    /*
    getMatches(getPattern("Baum", 1, 0, 5))
        .forEach{logger.debug{it}}
*/
}




