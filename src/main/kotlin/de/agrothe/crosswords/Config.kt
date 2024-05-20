package de.agrothe.crosswords

import io.github.oshai.kotlinlogging.KotlinLogging
import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import kotlinx.css.Color

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
    val TABLE_CELL_BACKGROUND: String,
    val TABLE_CELL_BACKGROUND1: String,
    val TABLE_CELL_BACKGROUND2: String,

    val COLOR_PALETTES: List<ColorPalletteConfig>,
)

open class ColorPalletteConfig(
    GRID_BORDER: String,
    GRID_LINES: String,
    IDX_NUM: String,
    CELL_CHAR: String,

    val GRID_BORDER_COLR: Color = GRID_BORDER.toColor(),
    val GRID_LINES_COLR: Color = GRID_LINES.toColor(),
    val IDX_NUM_COLR: Color = IDX_NUM.toColor().darken(60),
    val CELL_CHAR_COLR: Color = CELL_CHAR.toColor().darken(20),
)
    {
        companion object{
            private fun String.toColor() = Color('#'+this)
        }
    }

data class AppConfig(
    val dict: ReadDictConfig,
    val puzzle: PuzzleConfig,
    val webApp: WebAppConfig,
    val GENERATED_PUZZLES_DIR: Array<String>,
    val GENERATED_PUZZLE_SUFFX: String,
)

fun readConfig(): AppConfig =
    ConfigFactory.load().extract<AppConfig>()

