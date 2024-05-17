package de.agrothe.crosswords

import io.github.oshai.kotlinlogging.KotlinLogging
import com.typesafe.config.ConfigFactory
import io.github.config4k.extract

private val logger by lazy{KotlinLogging.logger{}}

class PuzzleConfig(
    val ALLOW_DUPLICATES: Boolean,
)

open class DictConfig(
    val LEGAL_KEY_CHARS: Regex,
    val CHAR_SUBSTS: List<Pair<String, String>>,
    val ENTRY_DELIMITER: Char,
    val COMMENT_SEP: Char,
)

class ReadDictConfig(
    val DICT_FILE_NAME: String,
    val TEST_MARKER: String,
    val SKIP_TEST_LINES: Boolean,
    val TO_UPPER_CASE: Boolean,
    private val SUBST_CHARS: Boolean,
    var BIDECTIONAL: Boolean,

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
    )
    {
        val NEGATIVES_REGEXPR: Regex =
            Regex(NEGATIVES_LIST.fold(StringBuilder())
                {acc, entry->acc.append("|\\b$entry\\b")}
                    .deleteCharAt(0).toString())

        fun String.substituteChars(): String =
           if(SUBST_CHARS) CHAR_SUBSTS.fold(this)
               {acc, subst->acc.replace(subst.first, subst.second)}
           else this
    }

class WebAppConfig(
    val DIRCTN_IMG: String,
    val CSS: Css,
)

class Css(
    val GRID_TABLE: String,
    val GRID_TABLE_ROW: String,
    val GRID_TABLE_COL: String,
    val PUZZLE_CELL_CHAR: String,
    val PUZZLE_CELL_IDX_NUM: String,
    val IDX_SLCT_ROT_EAST: String,
    val IDX_SLCT_ROT_WEST: String,
    val IDX_SLCT_ROT_SOUTH: String,
    val IDX_SLCT_ROT_NORTH: String,
)

data class AppConfig(
    val dict: ReadDictConfig,
    val puzzle: PuzzleConfig,
    val webApp: WebAppConfig,
    val GENERATED_PUZZLES_DIR: Array<String>,
    val GENERATED_PUZZLE_SUFFX: String,
)

fun readConfig(): AppConfig =
    ConfigFactory.load().extract<AppConfig>()

