package de.agrothe.crosswords

import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
data class AppConfig(
    val DICT_FILE_NAME: String,
    val NEGATIVE_LIST_FILE_NAME: String,
    val LEGAL_CHARS: Regex,
    val TO_UPPER_CASE: Boolean,
    val SUBST_CHARS: Boolean,
    val CHAR_SUBSTS: List<Pair<String, String>>
)
fun readConfig(): AppConfig {
    data class LocalAppConfig (
        val dictFileName: String,
        val negativeListFileName: String,
        val legalChars: String, // RegEx
        val toUpperCase: Boolean,
        val substChars: Boolean,
        val charSubts: List<List<String>>
    )

    val conf = ConfigFactory.load().extract<LocalAppConfig>()
    return AppConfig (
        DICT_FILE_NAME = conf.dictFileName,
        NEGATIVE_LIST_FILE_NAME = conf.negativeListFileName,
        LEGAL_CHARS = Regex(conf.legalChars),
        TO_UPPER_CASE = conf.toUpperCase,
        SUBST_CHARS = conf.substChars,
        CHAR_SUBSTS = conf.charSubts.map {Pair(it.first(), it.last())}
    )
}

fun main() {
    val c = readConfig()
    println(c)
}


