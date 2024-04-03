package de.agrothe.crosswords

import io.github.oshai.kotlinlogging.KotlinLogging
import com.typesafe.config.ConfigFactory
import io.github.config4k.extract

private val logger = KotlinLogging.logger{}

open class DictConfig(
    val LEGAL_CHARS: Regex,
    val CHAR_SUBSTS: List<Pair<String, String>>,
)
class ReadDictConfig(
    val DICT_FILE_NAME: String,
    val NEGATIVE_LIST_FILE_NAME: String,
    val COMMENT_CHAR: String,
    val TEST_MARKER: String,
    val SKIP_TEST_LINES: Boolean,
    val TO_UPPER_CASE: Boolean,
    val SUBST_CHARS: Boolean,
    LEGAL_CHARS_PATTERN: String,
    CHAR_SUBSTS_LIST: List<List<String>>,
) :
    DictConfig(
        LEGAL_CHARS = Regex(LEGAL_CHARS_PATTERN) ,
        CHAR_SUBSTS = CHAR_SUBSTS_LIST.map {Pair(it.first(), it.last())}
)

data class AppConfig(
    val dict: ReadDictConfig,
)

fun readConfig(): AppConfig =
    ConfigFactory.load().extract<AppConfig>()

fun main() {
    val c = readConfig()
    logger.debug{c}
}


