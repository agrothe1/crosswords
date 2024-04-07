package de.agrothe.crosswords

import io.github.oshai.kotlinlogging.KotlinLogging
import com.typesafe.config.ConfigFactory
import io.github.config4k.extract

private val logger by lazy { KotlinLogging.logger{} }

open class DictConfig(
    val LEGAL_KEY_CHARS: Regex,
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
    LEGAL_KEY_CHARS_PATT: String,
    CHAR_SUBSTS_LIST: List<List<String>>,
) :
    DictConfig(
        LEGAL_KEY_CHARS = Regex(LEGAL_KEY_CHARS_PATT),
        CHAR_SUBSTS = CHAR_SUBSTS_LIST.map {Pair(it.first(), it.last())}
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
/*
    val dict = readConfig().dict
    val patt = dict.LINE_PATT
    val key = patt.matchLine("a; b; c ;d  # X", dict.KEY_GRP_NAME)
    val values = patt.matchLine("a;b;c;d", dict.VALUE_GRP_NAME)
    println("stop '$key'")
    values.forEach{println("\t$it")}
*/

/*
false:
    "" " "
    ";" ";;" ";;;" "a;" ";a" ";;a" "a;;" ";a;b"
    " ;" "; " " ;;" ";; " "; ;" " ; ; "
true:
    "a;b" "a;b;c" "a;b;c;d"
    " a ; b " " a ; b ; c" "a ; b ; c; d  # X"
    "a;b;c # " "a;b# c" "a;b# c#"
*/
}
