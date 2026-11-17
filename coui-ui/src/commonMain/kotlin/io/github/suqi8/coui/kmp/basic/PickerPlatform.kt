// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.intl.Locale

/**
 * Returns a callback that plays the COUI picker tick (`coui_numberpicker_click.ogg`) once.
 *
 * Android plays it through a SoundPool on a background thread, only while the system touch
 * sounds setting is on (COUISoundLoadUtil); every other platform is silent.
 */
@Composable
internal expect fun rememberNumberPickerClickSound(): () -> Unit

/**
 * Scale applied to COUINumberPicker's raw px/s velocity constants (750 / 5000 px/s).
 *
 * Android uses them unscaled, exactly like COUI. Other platforms treat them as px at the
 * reference ColorOS phone density ([NumberPickerReferenceDensity]) and scale them by the actual
 * density, so a flick feels the same as on that phone.
 */
internal expect fun numberPickerVelocityScale(density: Float): Float

/** Density of the reference ColorOS phone (3.5x) the non-Android velocity scale is based on. */
internal val NumberPickerReferenceDensity = 3.5f

/**
 * The default typeface's `FontMetrics.top` and `bottom` (px, top negative) at [textSizePx].
 *
 * COUINumberPicker reads these from a default-typeface Paint at the unselected text size and
 * centers every row on them, so the rows sit about 0.8dp higher than an ascent/descent center.
 */
internal expect fun numberPickerFontTopBottom(textSizePx: Float): FloatArray

/**
 * The locale's best "yyyyMMMMdd" date pattern (COUIDatePicker.reorderSpinners), used to order the
 * year, month and day wheels. Empty when the platform cannot tell.
 */
internal expect fun platformDatePattern(): String

/** Today's date in the system time zone. */
internal expect fun platformToday(): DateValue

/**
 * The locale's best "hm" time pattern (COUITimeLimitPicker / COUITimePicker.reorderSpinners), used
 * to place the AM/PM wheel. Empty when the platform cannot tell.
 */
internal expect fun platformTimePattern(): String

/** The default-locale names and labels the pickers show, read from the platform in one go. */
internal expect fun platformPickerSymbols(): PickerSymbols

/**
 * A formatter for [format] in the default locale. Each call may build a platform formatter, so
 * callers cache the results (see [cachedLabel]).
 */
internal expect fun platformDateFormatter(format: PickerDateFormat): (DateValue) -> String

/**
 * Localized texts of the default locale used by the picker defaults.
 *
 * @property shortMonths The 12 abbreviated month names, January first, as COUIDatePicker formats
 *   the month wheel (`DateUtils.formatDateTime` with abbreviated month and no day or year).
 * @property shortWeekdays The 7 abbreviated weekday names ("EEE"), Sunday first.
 * @property shortestWeekdays The 7 shortest weekday names (`DateUtils.LENGTH_SHORTEST`), Sunday first.
 * @property am The AM label (COUI coui_time_picker_ampm).
 * @property pm The PM label (COUI coui_time_picker_ampm).
 * @property today The "Today" label (COUI coui_time_picker_today).
 */
internal class PickerSymbols(
    val shortMonths: List<String>,
    val shortWeekdays: List<String>,
    val shortestWeekdays: List<String>,
    val am: String,
    val pm: String,
    val today: String,
)

/** The date formats the picker defaults need, as ICU skeletons. */
internal enum class PickerDateFormat(val skeleton: String) {
    /** The calendar header (COUICalendarPickerDelegate: best "MMMMy"). */
    MonthYear("yMMMM"),

    /** The COUITimePicker date wheel outside Chinese (`DateUtils.formatDateTime` flags 524314). */
    WeekdayMonthDay("MMMEd"),

    /** A full date for accessibility (`DateUtils.formatDateTime` flags 22). */
    FullDate("yMMMMEEEEd"),
}

/** The picker symbols of the current locale, read once per locale. */
@Composable
internal fun rememberPickerSymbols(): PickerSymbols {
    val locale = Locale.current
    return remember(locale) { platformPickerSymbols() }
}

/** Whether the current locale writes Chinese, which COUI pickers special-case (`Locale.getLanguage() == "zh"`). */
@Composable
internal fun isChineseLocale(): Boolean = Locale.current.language == "zh"

/**
 * A date formatter for [format] in the current locale, recreated when the locale changes and
 * memoising its recent results, since wheel and grid labels are asked for on every frame.
 */
@Composable
internal fun rememberDateFormatter(format: PickerDateFormat): (DateValue) -> String {
    val locale = Locale.current
    return remember(locale, format) { cachedLabel(platformDateFormatter(format)) }
}

/**
 * Wraps [compute] in a small insertion-ordered cache. Labels are requested from draw passes on
 * the main thread only, so the map needs no synchronisation.
 */
internal fun <K, V> cachedLabel(compute: (K) -> V): (K) -> V {
    val cache = LinkedHashMap<K, V>()
    return { key ->
        cache.getOrPut(key) {
            if (cache.size >= LabelCacheSize) cache.remove(cache.keys.first())
            compute(key)
        }
    }
}

private val LabelCacheSize = 64

/**
 * Whether a time pattern puts the day period before the hour, as COUITimeLimitPicker checks with
 * `getBestDateTimePattern(locale, "hm").startsWith("a")`. Quoted literals are skipped.
 */
internal fun amPmBeforeHour(pattern: String): Boolean {
    var quoted = false
    for (char in pattern) {
        if (char == '\'') {
            quoted = !quoted
            continue
        }
        if (quoted) continue
        when (char) {
            'a', 'b', 'B' -> return true
            'h', 'H', 'k', 'K' -> return false
        }
    }
    return false
}

/** The "Today" label for platforms without a relative date formatter. */
internal fun fallbackTodayLabel(language: String): String = when (language) {
    "zh" -> "今天"
    "ja" -> "今日"
    "ko" -> "오늘"
    else -> "Today"
}
