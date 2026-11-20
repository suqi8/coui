// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * A plain calendar date holder used by [DatePicker].
 *
 * Values are stored as simple integers so no date/time library is required.
 * Out-of-range values passed to [DatePicker] are coerced (e.g. day 31 in a
 * 30-day month is clamped to 30).
 *
 * @param year The calendar year (e.g. 2026).
 * @param month The month of the year, 1-based (1 = January .. 12 = December).
 * @param day The day of the month, 1-based.
 */
@Immutable
data class DateValue(
    val year: Int,
    val month: Int,
    val day: Int,
)

/**
 * A [DatePicker] component with COUI style.
 *
 * Three scroll wheels (year / month / day) arranged like the COUI date picker:
 * the columns are centered as a group, the day range follows the selected
 * year and month (leap years included), and the day is clamped when a shorter
 * month is selected. The date is kept within [minDate] .. [maxDate]: as in
 * COUIDatePicker.updateSpinners, while the selection sits in the first or last year (or month)
 * the month (or day) wheel only offers the reachable values and stops wrapping unless it still
 * covers the whole year (or month). Two hairlines frame the selected row at the center ± 24dp,
 * inset 16dp from both sides, and holding a row above or below the center steps the year and day
 * every 100ms and the month every 200ms. The wheels follow the locale's date field order
 * ([fieldOrder]), as COUIDatePicker.reorderSpinners does with the best "yyyyMMMMdd" pattern, and
 * the picker is at most [NumberPickerDefaults.PickersMaxWidth] wide. The columns stay centered as a
 * group, but the first and last wheel also cover the space left at either side, so a drag anywhere
 * across the picker moves them (COUIDatePicker.onMeasure: setNumberPickerPaddingLeft / Right).
 *
 * With [yearIgnorable] the year wheel gets a placeholder row after the last year (COUIDatePicker
 * couiYearIgnorable); selecting it reports [DatePickerDefaults.IgnoredYear] as the year, and the
 * day wheel then allows 29 February.
 *
 * @param value The currently selected date. Out-of-range fields are coerced.
 * @param onValueChange The callback invoked when the selected date changes.
 * @param modifier The modifier to be applied to the [DatePicker].
 * @param enabled Whether the [DatePicker] is enabled for user interaction.
 * @param yearIgnorable Whether the year can be left out (see above).
 * @param minDate The earliest selectable date (COUI default 1900-01-01).
 * @param maxDate The latest selectable date (COUI default 2100-12-31).
 * @param yearLabel Converts a year to its display string.
 * @param monthLabel Converts a month (1..12) to its display string.
 * @param dayLabel Converts a day of month to its display string.
 * @param fieldOrder The left-to-right order of the year, month and day wheels (mirrored in RTL).
 * @param yearColumnWidth The width of the year column.
 * @param monthColumnWidth The width of the month column.
 * @param dayColumnWidth The width of the day column.
 * @param colors The [NumberPickerColors] for the wheels.
 * @param dividerColor The color of the two hairlines around the selected row.
 * @param textStyle The [TextStyle] for the wheel items.
 * @param itemHeight The height of each wheel item.
 * @param insideMargin The padding around the wheel columns.
 */
@Composable
fun DatePicker(
    value: DateValue,
    onValueChange: (DateValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    yearIgnorable: Boolean = false,
    minDate: DateValue = DatePickerDefaults.MinDate,
    maxDate: DateValue = DatePickerDefaults.MaxDate,
    yearLabel: (Int) -> String = DatePickerDefaults.yearLabel(),
    monthLabel: (Int) -> String = DatePickerDefaults.monthLabel(),
    dayLabel: (Int) -> String = DatePickerDefaults.dayLabel(),
    fieldOrder: DateFieldOrder = DatePickerDefaults.localeFieldOrder(),
    yearColumnWidth: Dp = DatePickerDefaults.YearColumnWidth,
    monthColumnWidth: Dp = DatePickerDefaults.MonthColumnWidth,
    dayColumnWidth: Dp = DatePickerDefaults.DayColumnWidth,
    colors: NumberPickerColors = NumberPickerDefaults.colors(),
    dividerColor: Color = COUITheme.colorScheme.divider,
    textStyle: TextStyle = COUITheme.textStyles.headlineM,
    itemHeight: Dp = NumberPickerDefaults.ItemHeight,
    insideMargin: PaddingValues = DatePickerDefaults.InsideMargin,
) {
    val minEpoch = epochDayOf(minDate.normalized())
    val maxEpoch = epochDayOf(maxDate.normalized())
    require(minEpoch <= maxEpoch) { "minDate must not be after maxDate" }
    val min = dateOfEpochDay(minEpoch)
    val max = dateOfEpochDay(maxEpoch)

    val yearIgnored = yearIgnorable && value.year == DatePickerDefaults.IgnoredYear
    val date = if (yearIgnored) {
        DateValue(DatePickerDefaults.IgnoredYear, value.month, value.day).normalized()
    } else {
        // The year is pulled near the bounds first so the epoch-day math cannot overflow.
        val near = value.copy(year = value.year.coerceIn(min.year - 1, max.year + 1))
        dateOfEpochDay(epochDayOf(near.normalized()).coerceIn(minEpoch, maxEpoch))
    }
    val daysOfMonth = daysInMonth(dayCountYear(date.year), date.month)

    // COUIDatePicker.updateSpinners: the first / last year and month narrow the next wheel, which
    // only keeps wrapping while it still offers every value.
    val inMinYear = date.year == min.year
    val inMaxYear = date.year == max.year
    val monthRange = (if (inMinYear) min.month else 1)..(if (inMaxYear) max.month else 12)
    val inMinMonth = inMinYear && date.month == min.month
    val inMaxMonth = inMaxYear && date.month == max.month
    val dayRange = (if (inMinMonth) min.day else 1)..(if (inMaxMonth) max.day else daysOfMonth)
    val monthWraps = monthRange.first == 1 && monthRange.last == 12
    val dayWraps = dayRange.first == 1 && dayRange.last == daysOfMonth

    // Latest state readable from the remember-cached per-column callbacks below.
    val currentValue by rememberUpdatedState(date)
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val currentMinEpoch by rememberUpdatedState(minEpoch)
    val currentMaxEpoch by rememberUpdatedState(maxEpoch)

    // COUIDatePicker.setDate: a changed field is clamped back into minDate .. maxDate.
    val emit = remember {
        { target: DateValue ->
            val clamped = if (target.year == DatePickerDefaults.IgnoredYear) {
                target
            } else {
                dateOfEpochDay(epochDayOf(target).coerceIn(currentMinEpoch, currentMaxEpoch))
            }
            currentOnValueChange(clamped)
        }
    }
    val onYearChange = remember {
        { newYear: Int ->
            val current = currentValue
            val day = current.day.coerceAtMost(daysInMonth(dayCountYear(newYear), current.month))
            emit(DateValue(newYear, current.month, day))
        }
    }
    val onMonthChange = remember {
        { newMonth: Int ->
            val current = currentValue
            val day = current.day.coerceAtMost(daysInMonth(dayCountYear(current.year), newMonth))
            emit(DateValue(current.year, newMonth, day))
        }
    }
    val onDayChange = remember {
        { newDay: Int -> emit(currentValue.copy(day = newDay)) }
    }

    // COUIDatePicker.onMeasure: the first and last wheel take the space left at the sides
    // (numberPickerPaddingLeft / Right), so the whole width of the picker scrolls them.
    NumberPickerRow(
        modifier = modifier
            .widthIn(max = NumberPickerDefaults.PickersMaxWidth)
            .fillMaxWidth()
            .numberPickerDividers(dividerColor)
            .padding(insideMargin),
    ) {
        val fields = fieldOrder.fields
        fields.forEachIndexed { index, field ->
            val alignment = numberPickerColumnAlignment(index, fields.size)
            key(field) {
                when (field) {
                    DateField.Year -> NumberPickerWheel(
                        value = date.year,
                        onValueChange = onYearChange,
                        modifier = Modifier.width(yearColumnWidth),
                        enabled = enabled,
                        range = min.year..max.year,
                        label = yearLabel,
                        wrapAround = true,
                        ignorable = yearIgnorable,
                        longPressStepInterval = DatePickerDefaults.YearStepInterval,
                        colors = colors,
                        textStyle = textStyle,
                        itemHeight = itemHeight,
                        contentWidth = yearColumnWidth,
                        contentAlignment = alignment,
                    )

                    DateField.Month -> NumberPickerWheel(
                        value = date.month,
                        onValueChange = onMonthChange,
                        modifier = Modifier.width(monthColumnWidth),
                        enabled = enabled,
                        range = monthRange,
                        label = monthLabel,
                        wrapAround = monthWraps,
                        longPressStepInterval = DatePickerDefaults.MonthStepInterval,
                        colors = colors,
                        textStyle = textStyle,
                        itemHeight = itemHeight,
                        contentWidth = monthColumnWidth,
                        contentAlignment = alignment,
                    )

                    DateField.Day -> NumberPickerWheel(
                        value = date.day,
                        onValueChange = onDayChange,
                        modifier = Modifier.width(dayColumnWidth),
                        enabled = enabled,
                        range = dayRange,
                        label = dayLabel,
                        wrapAround = dayWraps,
                        longPressStepInterval = DatePickerDefaults.DayStepInterval,
                        colors = colors,
                        textStyle = textStyle,
                        itemHeight = itemHeight,
                        contentWidth = dayColumnWidth,
                        contentAlignment = alignment,
                    )
                }
            }
        }
    }
}

/**
 * Contains default values used by [DatePicker].
 */
object DatePickerDefaults {

    /** The earliest selectable date (COUIDatePicker beginYear 1900, January 1). */
    val MinDate: DateValue = DateValue(1900, 1, 1)

    /** The latest selectable date (COUIDatePicker endYear 2100, December 31). */
    val MaxDate: DateValue = DateValue(2100, 12, 31)

    /** The year of a date whose year was left out on an ignorable wheel (COUIDatePicker.IGNORED_YEAR). */
    val IgnoredYear: Int = NumberPickerDefaults.IgnoredValue

    /** Width of the year column (COUI coui_datepicker_width_year). */
    val YearColumnWidth = 76.dp

    /** Width of the month column (COUI coui_datepicker_width_month). */
    val MonthColumnWidth = 96.dp

    /** Width of the day column (COUI coui_number_picker_width_small). */
    val DayColumnWidth = 62.dp

    /** Padding around the wheel columns (COUI coui_number_picker_view_padding_vertical). */
    val InsideMargin = PaddingValues(vertical = 12.dp)

    /** Auto-repeat interval of the year wheel while a side row is held (COUIDatePicker). */
    val YearStepInterval: Long = 100L

    /** Auto-repeat interval of the month wheel while a side row is held (COUIDatePicker). */
    val MonthStepInterval: Long = 200L

    /** Auto-repeat interval of the day wheel while a side row is held (COUIDatePicker). */
    val DayStepInterval: Long = 100L

    /**
     * The date field order of the current locale, read from the platform's best "yyyyMMMMdd"
     * pattern (Android `DateFormat.getBestDateTimePattern`, the JDK long date pattern on desktop,
     * `NSDateFormatter.dateFormatFromTemplate` on Apple, `Intl.DateTimeFormat` on the web). Falls
     * back to [DateFieldOrder.YearMonthDay].
     */
    @Composable
    fun localeFieldOrder(): DateFieldOrder {
        val locale = Locale.current
        return remember(locale) { DateFieldOrder.fromPattern(platformDatePattern()) }
    }

    /**
     * The default year label of the current locale (COUIDatePicker.Format "YEAR"): the year
     * followed by coui_year ("2026年") in Chinese, the plain number otherwise.
     */
    @Composable
    fun yearLabel(): (Int) -> String {
        val chinese = isChineseLocale()
        return remember(chinese) { if (chinese) ChineseYearLabel else PlainNumberLabel }
    }

    /**
     * The default month label of the current locale (COUIDatePicker.Format "MONTH"): the
     * abbreviated month name, "Jan" in English and "1月" in Chinese.
     */
    @Composable
    fun monthLabel(): (Int) -> String {
        val symbols = rememberPickerSymbols()
        return remember(symbols) { { month: Int -> symbols.shortMonths[(month - 1).coerceIn(0, 11)] } }
    }

    /**
     * The default day label of the current locale (COUIDatePicker.Format "DAY"): the day followed
     * by coui_day ("5日") in Chinese, two zero-padded digits otherwise.
     */
    @Composable
    fun dayLabel(): (Int) -> String {
        val chinese = isChineseLocale()
        return remember(chinese) { if (chinese) ChineseDayLabel else TwoDigitDayLabel }
    }

    private val PlainNumberLabel: (Int) -> String = { it.toString() }
    private val ChineseYearLabel: (Int) -> String = { "${it}年" }
    private val TwoDigitDayLabel: (Int) -> String = { it.toString().padStart(2, '0') }
    private val ChineseDayLabel: (Int) -> String = { "${it}日" }
}

/** One wheel of a [DatePicker]. */
enum class DateField {
    Year,
    Month,
    Day,
}

/**
 * The left-to-right order of the [DatePicker] wheels.
 *
 * @property fields The wheels in display order.
 */
enum class DateFieldOrder(val fields: List<DateField>) {
    YearMonthDay(listOf(DateField.Year, DateField.Month, DateField.Day)),
    YearDayMonth(listOf(DateField.Year, DateField.Day, DateField.Month)),
    MonthDayYear(listOf(DateField.Month, DateField.Day, DateField.Year)),
    MonthYearDay(listOf(DateField.Month, DateField.Year, DateField.Day)),
    DayMonthYear(listOf(DateField.Day, DateField.Month, DateField.Year)),
    DayYearMonth(listOf(DateField.Day, DateField.Year, DateField.Month)),
    ;

    companion object {
        /**
         * Reads the order of the first `y`, `M` (or `L`) and `d` letters of a date pattern, skipping
         * quoted literals, the way COUIDatePicker.reorderSpinners walks the best pattern.
         */
        fun fromPattern(pattern: String): DateFieldOrder {
            val found = ArrayList<DateField>(3)
            var quoted = false
            for (char in pattern) {
                if (char == '\'') {
                    quoted = !quoted
                    continue
                }
                if (quoted) continue
                val field = when (char) {
                    'y', 'Y', 'u' -> DateField.Year
                    'M', 'L' -> DateField.Month
                    'd' -> DateField.Day
                    else -> null
                }
                if (field != null && field !in found) found += field
            }
            return entries.firstOrNull { it.fields == found } ?: YearMonthDay
        }
    }
}

private fun isLeapYear(year: Int): Boolean = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0

/** This date with the month kept in 1..12 and the day in its month. */
private fun DateValue.normalized(): DateValue {
    val safeMonth = month.coerceIn(1, 12)
    return DateValue(year, safeMonth, day.coerceIn(1, daysInMonth(dayCountYear(year), safeMonth)))
}

/**
 * The year whose month lengths apply: an ignored year counts as 2020, a leap year, as
 * COUIDatePicker.IncompleteDate does, so 29 February stays selectable.
 */
private fun dayCountYear(year: Int): Int = if (year == DatePickerDefaults.IgnoredYear) 2020 else year

internal fun daysInMonth(year: Int, month: Int): Int = when (month) {
    1, 3, 5, 7, 8, 10, 12 -> 31
    4, 6, 9, 11 -> 30
    2 -> if (isLeapYear(year)) 29 else 28
    else -> 31
}
