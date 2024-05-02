package de.agrothe.crosswords

import java.io.File

typealias DictKey = String
typealias DictValues = Collection<String>
typealias DictEntry = Map<DictKey, DictValues>

const val ANY_CHR = '.'

class Dict(conf: ReadDictConfig){
    val entries: DictEntry by lazy {readDictFile(conf)}
    val keys: Collection<DictKey> by lazy {entries.keys}

    companion object{
        fun String.getPattern(srcPos: Int, destPos: Int, destLen: Int)
                : String =
            let{src->StringBuilder().apply{repeat(destLen){append(ANY_CHR)}}
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
            Pair(entry.substituteChars(), synms)
        }
        // do not include keys w/ illegal chars
        .filter{(entry, _)->LEGAL_KEY_CHARS.matches(entry)}
        .map{(entry, synms)->
            Pair(if(TO_UPPER_CASE) entry.uppercase() else entry, synms)}
        .groupBy{(entry, _)->entry}
        .map{(entry, synms)->
            Pair(entry, synms.flatMap{synm->synm.second}.toSet())}
        .flatMap{entry->if(BIDECTIONAL)
                setOf(entry, Pair(entry.first.reversed(), entry.second))
            else setOf(entry)
        }
        .toMap()
