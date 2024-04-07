package de.agrothe.crosswords

import de.agrothe.crosswords.Dict.Companion.getPattern
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger by lazy { KotlinLogging.logger{} }

private val config = readConfig()
private val dict = Dict(config.dict)

fun main() {
    dict.dict.take(50).forEach {logger.debug{it}}

    dict.getMatches(".ruckm....l").forEach{
        logger.debug{ ".ruckm....l '$it'\n"}
    }

    dict.getMatches("Haushalt".getPattern(1, 0, 5))
        .forEach{logger.debug{it}}
}




