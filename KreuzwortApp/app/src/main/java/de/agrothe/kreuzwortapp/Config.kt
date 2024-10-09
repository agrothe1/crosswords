package de.agrothe.kreuzwortapp

import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import kotlinx.css.Color

enum class PuzzleType{
    SCHWEDEN, SCHUETTEL
}

class PuzzleConfig(
    val ALLOW_DUPLICATES: Boolean,
    val DEFAULT_PUZZLE_DIMEN: Int,
    DEFAULT_PUZZLE_TYPE_STR: String
){
    val DEFAULT_PUZZLE_TYPE =
        PuzzleType.valueOf(DEFAULT_PUZZLE_TYPE_STR.uppercase())
}

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
    val IS_PLUS_VERSION: Boolean,

    val PORT: Int,
    val CERT_NAME: String,
    val CERT_DAYS_VALID: Long,
    val APP_URL: String,
    val DIMEN_PARAM_NAME: String,
    val TYPE_PARAM_NAME: String,
    val WEB_SOCK_ENDPOINT: String,
    val WEB_SOCK_URL: String,
    val WS_PING_PERIODS_SECS: Long,
    val SHARED_PREFS_NAME: String,
    val SHRD_PRFS_NUM_SLVD_GMES_CNT_KEY: String,
    val SHRD_PRFS_PUZZLE_HISTORY_KEY: String,
    val SHRD_PRFS_PUZZLE_HISTORY_SIZE: Int,
    val SHRD_PRFS_PUZZLE_DIMEN_KEY: String,
    val SHRD_PRFS_PUZZLE_TYPE: String,
    val PUZZLE_CACHE_MAX_SIZE: Int,
    val PUZZLE_CACHE_EXPIRATION_MINS: Long,
    val DIRCTN_IMG: String,
    val I18n: I18n,
    val CSS: Css,
    LEGND_ENTR_SUBST: String,
    val LEGND_ENTR_SUBST_REGEX: Regex = Regex(LEGND_ENTR_SUBST),
    val SHOW_INPUT_HINT: Boolean,
    val MAX_SYNMS: Int,
    val SYNMS_TOTAL_LNGTH_THRSHLD: Int,
    val HELP_PROBABILITY: Int,
    val PUZZLE_TYPE_PLACEHOLDER: String,
)

class I18n(
    val NEW_GAME: String,
    val SHOW_HELP: String,
    val HORIZONTAL: String,
    val VERTICAL: String,
    val PUZZLE_DIMEN_TMPLT: String,
    var SCHUETTEL: String,
    var SCHWEDEN: String,
    val PUZZLE_TYPES: Map<PuzzleType, String> =
        mapOf(PuzzleType.SCHWEDEN to SCHWEDEN,
            PuzzleType.SCHUETTEL to SCHUETTEL),
)

class Css(
    val PUZZLE_GRID: String,
    val CELL_GRID: String,
    val SHOW_HELP_BUTTON_ID: String,
    val NEW_GAME_BUTTON_ID: String,
    val PUZZLE_TYPE_RADIO_GROUP_NAME: String,
    val MENU_FIELD_SET_ENTRY: String,
    val NEW_GAME: String,
    val NEW_GAME_LABEL: String,
    val NUM_GAME: String,
    val NUM_GAME_ID: String,
    val GLASS_LAYER: String,
    val MENU_LAYER: String,
    val MENU_LAYER_NEXT_BUTTON: String,
    val MENU_LAYER_NEXT_BUTTON_ACTIVE: String,
    val LGND_GRID_HORIZ: String,
    var LGND_GRID_VERT: String,
    var FIELD_GRID: String,
    val GRID_TABLE: String,
    val GRID_TABLE_ROW: String,
    val GRID_TABLE_COL: String,
    val PUZZLE_CELL_GRID_IDX: String,
    val PUZZLE_CELL_CHAR_CONTAINER: String,
    val PUZZLE_CELL_CHAR: String,
    val PUZZLE_CELL_CHAR_SOLVED: String,
    val PUZZLE_CELL_CHAR_FINISHED: String,
    val PUZZLE_CELL_CHAR_ALL_FINISHED: String,
    val PUZZLE_CELL_IDX_NUM_HOR: String,
    val PUZZLE_CELL_IDX_NUM_VER: String,
    val PUZZLE_CELL_IDX_NUM_BKGND: String,
    val PUZZLE_LGND_IDX_NUM_HOR: String,
    val PUZZLE_LGND_IDX_NUM_VER: String,
    val PUZZLE_CELL_GRID_IDX_BACKGRD: String,
    val IDX_SLCT_ROT_EAST: String,
    val IDX_SLCT_ROT_WEST: String,
    val IDX_SLCT_ROT_SOUTH: String,
    val IDX_SLCT_ROT_NORTH: String,
    val TABLE_CELL_BACKGROUND: String,
    val LGND_TABLE: String,
    val LGND_TABLE_HEADER_HOR: String,
    val LGND_TABLE_HEADER_HOR_NTH: String,
    val LGND_TABLE_HEADER_VER: String,
    val LGND_TABLE_HEADER_VER_NTH: String,
    val LGND_ENTRIES_HOR: String,
    val LGND_ENTRIES_HOR_LAST: String,
    val LGND_ENTRIES_VER: String,
    val LGND_LAST_SFX: String,
    val LGND_ENTRIES_VER_LAST: String,
    val LGND_ENTRIES_SOLVED_SFX: String,
    val LGND_ID_SUFFX_ROW: String,
    val LGND_ID_SUFFX_COL: String,

    val COLOR_PALETTES: List<ColorPalletteConfig>,
    val TRANSITION_DURATION: Float,
    val ANIMATION_DURATION: Float,
    val ANIMATION_ITER_CNT: String,
    val ANIMATION_VARIATION_CNT: Int,
)

open class ColorPalletteConfig(
    GRID_BORDER: String,
    GRID_LINES: String,
    IDX_NUM: String,
    CELL_CHAR: String,
    CELL_CHAR_SOLVED: String,

    val GRID_BORDER_COLR: Color = GRID_BORDER.toColor(),
    val GRID_LINES_COLR: Color = GRID_LINES.toColor(),
    @Suppress("unused")
    val IDX_NUM_COLR: Color = IDX_NUM.toColor().darken(60),
    val CELL_CHAR_COLR: Color = CELL_CHAR.toColor().darken(20),
    val PUZZLE_CELL_CHAR_SOLVED: Color = CELL_CHAR_SOLVED.toColor(),
)
    {
        companion object{
            private fun String.toColor() = Color("#$this")
        }
    }

data class AppConfig(
    val dict: ReadDictConfig,
    val puzzle: PuzzleConfig,
    val webApp: WebAppConfig,
    @Suppress("ArrayInDataClass")
    val GENERATED_PUZZLES_DIR: Array<String>,
    val GENERATED_PUZZLE_SUFFX: String,
    val BUNDLE_KEY: String,
)

fun readConfig(): AppConfig =
    ConfigFactory.load().extract<AppConfig>()
