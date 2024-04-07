package de.agrothe.crosswords

import java.io.File

class Dict(conf: ReadDictConfig){
    val dict: List<Pair<String, Collection<String>>> = readDictFile(conf)

    fun getMatches(pattern: String): List<Pair<String, Collection<String>>> =
        dict.mapNotNull{entry ->
            if (Regex(pattern, RegexOption.IGNORE_CASE).matches(entry.first))
                entry
            else null
        }

    companion object{
        fun String.getPattern(
                recvPos: Int, otherPos: Int, otherLen: Int) : String =
            let{recv -> StringBuilder().apply{repeat(otherLen){append(".")}}
                .apply{setCharAt(otherPos, recv[recvPos])}.toString()}
    }
}

// Kurs
// Works, Weiterb ..., Wechsel, Dvisen, Reisepl, Route, Konzept,
// Linie, Parcours
private
fun readDictFile(pConfig: ReadDictConfig):
        List<Pair<String, Collection<String>>>{

    return File(pConfig.DICT_FILE_NAME).useLines{lines->
        pConfig.run{
            lines
                .filterNot{SKIP_TEST_LINES && it.contains(TEST_MARKER)}
                // strip tailing comments
                .map{it.substringBefore(COMMENT_CHAR)}
                .map{it.split(';')}
                .filterNot{it.count() < 2}
                .filter{LEGAL_KEY_CHARS.matches(it.first())}
                .filter{line->line.drop(1).all{it.isNotBlank()}}
                .filterNot{NEGATIVES_LIST.contains(it.first())}
                .map{line->Pair(line.first().trim(),
                    line.drop(1).map{it.trim()})}

                .toList() // file will be closed hereafter
                .map{keyValue->
                    fun String.adjustChars(): String =
                        this.run{
                            if(SUBST_CHARS)
                                CHAR_SUBSTS.fold(this){acc, subst->
                                    acc.replace(subst.first, subst.second)
                                }
                            else this
                        }
                        .run{if(TO_UPPER_CASE)this.uppercase()
                            else this
                        }
                    Pair(keyValue.first.adjustChars(), keyValue.second)
                }
                // put entries w/ same key into one group
                .groupBy{it.first}
                .map{keyValue->
                    Pair(keyValue.key,
                        keyValue.value.fold(arrayListOf<String>())
                            {acc, elem->acc.addAll(elem.second); acc}
                                .toSet())
                }
        }
    }
}