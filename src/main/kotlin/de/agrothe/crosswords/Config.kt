package de.agrothe.crosswords

import io.github.oshai.kotlinlogging.KotlinLogging
import com.typesafe.config.ConfigFactory
import io.github.config4k.extract

private val logger = KotlinLogging.logger{}
data class DictConfig(
    val DICT_FILE_NAME: String,
    val NEGATIVE_LIST_FILE_NAME: String,
    val COMMENT_CHAR: String,
    val TEST_MARKER: String,
    val SKIP_TEST_LINES: Boolean,
    val LEGAL_CHARS: Regex,
    val TO_UPPER_CASE: Boolean,
    val SUBST_CHARS: Boolean,
    val CHAR_SUBSTS: List<Pair<String, String>>
)
data class AppConfig(
    val DICT_CONFIG: DictConfig,
)

fun readConfig(): AppConfig {
    data class ReadDictConfig (
        val dictFileName: String,
        val negativeListFileName: String,
        val commentChar: String,
        val testMarker: String,
        val skipTestLines: Boolean,
        val legalChars: String, // RegEx
        val toUpperCase: Boolean,
        val substChars: Boolean,
        val charSubts: List<List<String>>,
    )
    data class ReadAppConf(
        val dict: ReadDictConfig,
    )

    val readAppConf = ConfigFactory.load().extract<ReadAppConf>()
    val readDictConf = readAppConf.dict

    return readDictConf.run {
        AppConfig(
            DICT_CONFIG = DictConfig(
                DICT_FILE_NAME = dictFileName,
                NEGATIVE_LIST_FILE_NAME = negativeListFileName,
                COMMENT_CHAR = commentChar,
                TEST_MARKER = testMarker,
                SKIP_TEST_LINES = skipTestLines,
                LEGAL_CHARS = Regex(legalChars),
                TO_UPPER_CASE = toUpperCase,
                SUBST_CHARS = substChars,
                CHAR_SUBSTS = charSubts.map {Pair(it.first(), it.last())}
        )
    )}
}

fun main() {
    val c = readConfig()
    logger.debug{c}
}


