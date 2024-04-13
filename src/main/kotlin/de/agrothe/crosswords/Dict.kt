package de.agrothe.crosswords

import java.io.File

typealias DictKey = String
typealias DictValues = Collection<String>
typealias DictEntry = Map<DictKey, DictValues>

class Dict(conf: ReadDictConfig){
    val entries: DictEntry by lazy {readDictFile(conf)}

    fun getMatches(pattern: Regex): Collection<DictKey> =
        entries.keys.mapNotNull{entry->
            if(pattern.matches(entry)) entry
            else null
        }

    fun getRandomMatch(pattern: Regex):  DictKey =
        getMatches(pattern).random()


    companion object{
        fun String.getPattern(srcPos: Int, destPos: Int, destLen: Int)
                : String =
            let{src->StringBuilder().apply{repeat(destLen){append(".")}}
                .apply{setCharAt(destPos, src[srcPos])}.toString()}
    }
}

fun readDictFile(pConf: ReadDictConfig) =
   File(pConf.DICT_FILE_NAME).useLines{lines->
       lines.parseDict(pConf)
   }

fun Sequence<String>.parseDict(pConf: ReadDictConfig) =
    pConf.parseDict(this)

/*
a;b;c -> a(b,c) b(a,c) c(a,b)
 */
fun ReadDictConfig.parseDict(pDict: Sequence<String>) =
    pDict
        .filterNot{SKIP_TEST_LINES && it.contains(TEST_MARKER)}
        // strip tailing comments
        .map{it.substringBefore(COMMENT_SEP)}
        .filter{it.isNotBlank()}
        .map{line->
            line.split(ENTRY_DELIMITER)
                // trim all entries
                .map{it.trim()}
                // remove empty entries
                .filter{it.isNotBlank()}
                // remove entries contained in negative list
                .filterNot{NEGATIVES_REGEXPR.matches(it)}
                // no duplicate elements per line
                .toSet()
        }
        // remove single value entries
        .filter{it.count() > 1}
        // build entrySet permutations
        // Set(a,b,c) -> Set(Pair(a,b),Pair(b,c), Pair(c,a))
        .flatMap{entry->entry.map{Pair(it, entry.minus(it))}}
        .map{(entry, synms)->
            fun String.substituteChars(): String =
                if(SUBST_CHARS) CHAR_SUBSTS.fold(this)
                    {acc, subst->acc.replace(subst.first, subst.second)}
                else this
            Pair(entry.substituteChars(), synms)
        }
        // do not include keys w/ illegal chars
        .filter{(entry, _)->LEGAL_KEY_CHARS.matches(entry)}
        .map{(entry, synms)->
            Pair(if(TO_UPPER_CASE) entry.uppercase() else entry, synms)}
        .groupBy{(entry, _)->entry}
        .map{(entry, synms)->
            Pair(entry,
                synms.fold(arrayListOf<String>())
                    {acc, synm->acc.addAll(synm.second); acc}.toSet())}
        .toMap()
