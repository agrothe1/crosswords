package de.agrothe.kreuzwortapp

import java.io.File

//import de.agrothe.kreuzwortapp.MainActivity.Companion.appAssets

typealias DictWord = String
typealias DictSynms = Collection<String>
typealias DictEntry = Map<DictWord, DictSynmsOrnt>

class DictSynmsOrnt(
    val synms: DictSynms,
    val ornt: KeyDirct
)
{
    fun reversed() =
        DictSynmsOrnt(synms,
            when(ornt){
               KeyDirct.NORMAL->KeyDirct.REVERSED
               KeyDirct.REVERSED->KeyDirct.NORMAL
            })
}

enum class KeyDirct{
    NORMAL, REVERSED
}

class Dict(conf: ReadDictConfig){
    val entries: DictEntry by lazy {readDictFile(conf)}
    val words: Collection<DictWord> by lazy {entries.keys}
}

fun readDictFile(pConf: ReadDictConfig): DictEntry =
   File(pConf.DICT_FILE_NAME).useLines{line-> // todo generify
    //appAssets.open(pConf.DICT_FILE_NAME).reader().useLines{line->
       line.parseDict(pConf)
   }

fun Sequence<String>.parseDict(pConf: ReadDictConfig): DictEntry =
    pConf.parseDict(this)

/*
a;b;c -> a(b,c) b(a,c) c(a,b)
 */
fun ReadDictConfig.parseDict(pDict: Sequence<String>): DictEntry =
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
            Pair(entry, DictSynmsOrnt(synms.flatMap{synm->synm.second}.toSet(),
                KeyDirct.NORMAL))}
        .flatMap{entry->
            if(BIDIRECTIONAL)
                setOf(entry,
                    Pair(entry.first.reversed(), entry.second.reversed()))
            else setOf(entry)
        }
        .associate{it}
