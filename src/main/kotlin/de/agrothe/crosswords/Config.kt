package de.agrothe.crosswords

import io.github.oshai.kotlinlogging.KotlinLogging
import com.typesafe.config.ConfigFactory
import io.github.config4k.extract

private val logger by lazy{KotlinLogging.logger{}}

open class DictConfig(
    val LEGAL_KEY_CHARS: Regex,
    val CHAR_SUBSTS: List<Pair<String, String>>,
    val ENTRY_DELIMITER: Char,
    val COMMENT_SEP: Char,
    val NEGATIVES_REGEXPRS: List<Regex>
)
class ReadDictConfig(
    val DICT_FILE_NAME: String,
    val TEST_MARKER: String,
    val SKIP_TEST_LINES: Boolean,
    val TO_UPPER_CASE: Boolean,
    val SUBST_CHARS: Boolean,
    NEGATIVES_LIST: Set<String>,
    ENTRY_DELIMITER_CHAR: String,
    LEGAL_KEY_CHARS_PATT: String,
    COMMENT_SEP_CHAR: String,
    CHAR_SUBSTS_LIST: List<List<String>>,
) :
    DictConfig(
        LEGAL_KEY_CHARS = Regex(LEGAL_KEY_CHARS_PATT),
        CHAR_SUBSTS = CHAR_SUBSTS_LIST.map{Pair(it.first(), it.last())},
        ENTRY_DELIMITER = ENTRY_DELIMITER_CHAR.toCharArray().first(),
        COMMENT_SEP = COMMENT_SEP_CHAR.toCharArray().first(),
        NEGATIVES_REGEXPRS = NEGATIVES_LIST.map{Regex(it)}
    )

data class AppConfig(
    val dict: ReadDictConfig,
)

fun readConfig(): AppConfig =
    ConfigFactory.load().extract<AppConfig>()

fun main() {
    fun Regex.matchLine(pLine: String, pGroupName: String): ArrayList<String?>
    {
        val matchRes = find(pLine)
        val group = (matchRes?.groups)?.get(pGroupName)
        val res = arrayListOf(group?.value)
        var next = matchRes?.next()
        while(next != null){
            res += next.groups[pGroupName]?.value
            next = next.next()
            println("XXX $next")
        }
        return res
    }
}
