package de.agrothe.crosswords

import java.io.File

class Dict(conf: ReadDictConfig){
    private val entries = readDictFile(conf)

    fun getMatches(pattern: Regex) =
        entries.mapNotNull{entry->
            if(pattern.matches(entry.first)) entry
            else null
        }

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
        .map{line->
            line.split(ENTRY_DELIMITER)
                // trim all entries
                .map{it.trim()}
                // remove empty entries
                .filter{it.isNotBlank()}
                // no duplicate elements per line
                .toSet()
        }
        // remove single value entries
        .filter{it.count() > 1}
        .toList() // todo file will be closed hereafter
        // build entrySet permutations
        // Set(a,b,c) -> Set(Pair(a,b),Pair(b,c), Pair(c,a))
        //.map{it.first().run{Pair(this, it.minus(this))}} todo
        .flatMap{entry->entry.map{Pair(it, entry.minus(it))}}
        .map{(entry, synms)->
            fun String.substituteChars(): String =
                if(SUBST_CHARS) CHAR_SUBSTS.fold(this)
                    {acc, subst->acc.replace(subst.first, subst.second)}
                else this
            Pair(entry.substituteChars(), synms)
        }
        //.filter{it.first.isEmpty()} todo remove ?
        // do not include keys w/ illegal chars
        .filter{(entry, _)->LEGAL_KEY_CHARS.matches(entry)}
        .filter{(entry, _)->NEGATIVES_REGEXPRS.all{!it.matches(entry)}}
        .map{(entry, synms)->
            Pair(if(TO_UPPER_CASE) entry.uppercase() else entry, synms)}
        .groupBy{(entry, _)->entry}
        .map{(entry, synms)->
            Pair(entry,
                synms.fold(arrayListOf<String>())
                    {acc, synm->acc.addAll(synm.second); acc}.toSet())}
