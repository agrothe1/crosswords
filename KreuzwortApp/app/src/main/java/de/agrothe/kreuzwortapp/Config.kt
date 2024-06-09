package de.agrothe.crosswords

import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import kotlinx.css.Color

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
    var BIDIRECTIONAL: Boolean,

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
    val WEB_SOCK_ENDPOINT: String,
    val DIRCTN_IMG: String,
    val I18n: I18n,
    val CSS: Css,
    LEGND_ENTR_SUBST: String,
    val LEGND_ENTR_SUBST_REGEX: Regex = Regex(LEGND_ENTR_SUBST),
)

class I18n(
    val HORIZONTAL: String,
    val VERTICAL: String,
)

class Css(
    val PUZZLE_TABLE: String,
    val GRID_TABLE: String,
    val GRID_TABLE_ROW: String,
    val GRID_TABLE_COL: String,
    val PUZZLE_CELL_CHAR: String,
    val PUZZLE_CELL_CHAR_SOLVED: String,
    val PUZZLE_CELL_IDX_NUM: String,
    val PUZZLE_LGND_IDX_NUM: String,
    val IDX_SLCT_ROT_EAST: String,
    val IDX_SLCT_ROT_WEST: String,
    val IDX_SLCT_ROT_SOUTH: String,
    val IDX_SLCT_ROT_NORTH: String,
    val TABLE_CELL_BACKGROUND: String,
    val LEGEND_TABLE: String,
    val LEGEND_TABLE_HEADER: String,
    val LEGEND_TABLE_HEADER_NTH: String,
    val LEGEND_ENTRIES: String,

    val COLOR_PALETTES: List<ColorPalletteConfig>,
)

open class ColorPalletteConfig(
    GRID_BORDER: String,
    GRID_LINES: String,
    IDX_NUM: String,
    CELL_CHAR: String,
    CELL_CHAR_SOLVED: String,

    val GRID_BORDER_COLR: Color = GRID_BORDER.toColor(),
    val GRID_LINES_COLR: Color = GRID_LINES.toColor(),
    val IDX_NUM_COLR: Color = IDX_NUM.toColor().darken(60),
    val CELL_CHAR_COLR: Color = CELL_CHAR.toColor().darken(20),
    val PUZZLE_CELL_CHAR_SOLVED: Color = CELL_CHAR_SOLVED.toColor(),
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

