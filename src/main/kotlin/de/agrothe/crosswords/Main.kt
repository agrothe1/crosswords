package de.agrothe.crosswords

import java.io.File
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger{}
private val config = readConfig()

fun main() {
    // Kurs
    // Works, Weiterb ..., Wechsel, Dvisen, Reisepl, Route, Konzept,
    // Linie, Parcours
    val dict: List<Pair<String, List<String>>> =
        readDictFile(config.DICT_CONFIG)

    dict.take(50).forEach {logger.debug {it}}

    fun getMatches(patt: String): List<Pair<String, List<String>>> =
        dict.mapNotNull {entry ->
            if(Regex(patt, RegexOption.IGNORE_CASE).matches(entry.first))
                entry
            else null
        }

    fun getPattern(other: String, otherPos: Int, pos: Int, len: Int): String =
        StringBuilder((1..len).fold("") {acc, _ -> acc + "."})
            .apply {this.setCharAt(pos, other[otherPos])}.toString()

    getMatches(".ruckm....l").forEach {
        logger.debug{".ruckm....l '$it'\n"}
    }
    getMatches(getPattern("KRssne", 2, 3, 5))
        .forEach{logger.debug{it}}
}

fun readDictFile(pConfig: DictConfig): List<Pair<String, List<String>>> {
    val negatives = File(pConfig.NEGATIVE_LIST_FILE_NAME).readLines().toSet()

    return File(pConfig.DICT_FILE_NAME).useLines{lines ->
        pConfig.run{
            lines
                // strip tailing comments
                .map{it.substringBefore(COMMENT_CHAR)}
                .filterNot{SKIP_TEST_LINES && it.contains(TEST_MARKER)}
                .map{it.split(';')}
                .filterNot{it.size < 2}
                .mapNotNull{line ->
                    line.first().trim().let{first ->
                        if (LEGAL_CHARS.matches(first))
                            Pair(first, line.drop(1).map {it.trim()})
                        else null
                    }
                }
                .filter{it.second.all{it.isNotBlank()}}
                .toList() // file will be closed hereafter
                .filterNot{negatives.contains(it.first)}
                .map{keyValue ->
                    fun String.adjustChars(): String =
                        this.run{
                            if (SUBST_CHARS)
                                CHAR_SUBSTS.fold(this){acc, subst ->
                                    acc.replace(subst.first, subst.second)
                                }
                            else this
                        }
                        .run{if(TO_UPPER_CASE) this.uppercase()
                                else this
                        }
                    Pair(keyValue.first.adjustChars(), keyValue.second)
                }
                // put entries w/ same key into one group
                .groupBy{it.first}
                .map{keyValue ->
                    Pair(keyValue.key,
                        keyValue.value.fold(arrayListOf<String>(),
                            {acc, elem -> acc.addAll(elem.second); acc}
                        ))
                }
        }
    }
}


