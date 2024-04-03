package de.agrothe.crosswords

import java.io.File

class Dict(conf: ReadDictConfig) {
    val dict: List<Pair<String, List<String>>> = readDictFile(conf)
}

// Kurs
// Works, Weiterb ..., Wechsel, Dvisen, Reisepl, Route, Konzept,
// Linie, Parcours
private
fun readDictFile(pConfig: ReadDictConfig): List<Pair<String, List<String>>> {
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