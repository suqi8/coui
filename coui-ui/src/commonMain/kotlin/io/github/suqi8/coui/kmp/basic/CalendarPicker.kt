// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.launch

/**
 * A [CalendarPicker] component with COUI style, mirroring ColorOS's calendar picker family
 * (COUICalendarDayPickerView, COUIDateMonthView, COUICalendarYearView and
 * coui_calendar_picker_material.xml).
 *
 * A header row holds the previous / next month buttons and the month-year label with an expand
 * arrow. The body is a horizontally paged month grid: a weekday row and up to six week rows of
 * 40dp cells (five-week months add 8dp to every row so the grid keeps its height, as
 * COUIDateMonthView does). Days of the adjacent months fill the first and last rows and can be
 * tapped too. The selected day is a filled primary circle that grows from 80% while fading in
 * (280ms), the previously selected day of the same month fades out (150ms), and today is ringed
 * in the primary color. Tapping the month-year label rotates the arrow and swaps the grid for a
 * [DatePicker] wheel (COUICalendarYearView hosts a COUIDatePicker), cross-fading 280ms in / 150ms out.
 *
 * @param value The selected date. Dates outside [minDate] .. [maxDate] are clamped.
 * @param onValueChange The callback invoked when a day is tapped or the wheel moves.
 * @param modifier The modifier to be applied to the [CalendarPicker].
 * @param minDate The earliest selectable date (COUI default 1900-01-01).
 * @param maxDate The latest selectable date (COUI default 2100-12-31).
 * @param today The date ringed as today, or null for none. Defaults to the system date.
 * @param firstDayOfWeek The first column of the grid, 1 = Sunday .. 7 = Saturday.
 * @param monthYearLabel Converts a year and month (1..12) to the header label.
 * @param weekdayLabel Converts a day of week (1 = Sunday .. 7 = Saturday) to its column label.
 * @param dayLabel Converts a day of month to its cell label.
 * @param colors The [CalendarPickerColors] of the [CalendarPicker].
 * @param headerTextStyle The [TextStyle] of the month-year label.
 * @param weekdayTextStyle The [TextStyle] of the weekday labels.
 * @param dayTextStyle The [TextStyle] of the day labels.
 * @param insideMargin The padding around the whole picker.
 */
@Composable
fun CalendarPicker(
    value: DateValue,
    onValueChange: (DateValue) -> Unit,
    modifier: Modifier = Modifier,
    minDate: DateValue = CalendarPickerDefaults.MinDate,
    maxDate: DateValue = CalendarPickerDefaults.MaxDate,
    today: DateValue? = CalendarPickerDefaults.today(),
    firstDayOfWeek: Int = CalendarPickerDefaults.FirstDayOfWeek,
    monthYearLabel: (year: Int, month: Int) -> String = CalendarPickerDefaults.MonthYearLabel,
    weekdayLabel: (dayOfWeek: Int) -> String = CalendarPickerDefaults.WeekdayLabel,
    dayLabel: (Int) -> String = CalendarPickerDefaults.DayLabel,
    colors: CalendarPickerColors = CalendarPickerDefaults.calendarPickerColors(),
    headerTextStyle: TextStyle = COUITheme.textStyles.headline2,
    weekdayTextStyle: TextStyle = COUITheme.textStyles.subtitle,
    dayTextStyle: TextStyle = COUITheme.textStyles.headline2,
    insideMargin: PaddingValues = CalendarPickerDefaults.InsideMargin,
) {
    val minEpoch = civilToEpochDay(minDate.year, minDate.month, minDate.day)
    val maxEpoch = civilToEpochDay(maxDate.year, maxDate.month, maxDate.day)
    require(minEpoch <= maxEpoch) { "maxDate must be >= minDate" }
    require(firstDayOfWeek in 1..7) { "firstDayOfWeek must be in 1..7 (1 = Sunday)" }
    val safeMonth = value.month.coerceIn(1, 12)
    val selectedEpoch = civilToEpochDay(value.year, safeMonth, value.day.coerceIn(1, daysInMonth(value.year, safeMonth)))
        .coerceIn(minEpoch, maxEpoch)
    val selected = remember(selectedEpoch) { epochDayToCivil(selectedEpoch) }

    // COUICalendarDayPickerView: one pager page per month from minDate to maxDate.
    val pageCount = monthIndex(maxDate.year, maxDate.month) - monthIndex(minDate.year, minDate.month) + 1
    val firstMonthIndex = monthIndex(minDate.year, minDate.month)
    val selectedPage = (monthIndex(selected.year, selected.month) - firstMonthIndex).coerceIn(0, pageCount - 1)
    val pagerState = rememberPagerState(initialPage = selectedPage) { pageCount }
    val scope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }

    // Follow the selection to its month, but only while the grid is shown: the pager is not
    // composed behind the wheel, and collapsing jumps it to the selected month instead.
    LaunchedEffect(selectedPage, expanded) {
        if (!expanded && pagerState.currentPage != selectedPage) {
            pagerState.animateScrollToPage(selectedPage, animationSpec = tween(PAGE_SCROLL_DURATION_MILLIS, easing = CalendarPageEasing))
        }
    }

    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val currentMinEpoch by rememberUpdatedState(minEpoch)
    val currentMaxEpoch by rememberUpdatedState(maxEpoch)
    val onDaySelected = remember {
        { date: DateValue ->
            val epoch = civilToEpochDay(date.year, date.month, date.day)
            if (epoch in currentMinEpoch..currentMaxEpoch) currentOnValueChange(date)
        }
    }

    // While the wheel is shown the header follows the wheel, not the hidden pager.
    val displayedMonth = if (expanded) monthIndex(selected.year, selected.month) else firstMonthIndex + pagerState.currentPage
    Column(modifier = modifier.padding(insideMargin)) {
        CalendarPickerHeader(
            label = monthYearLabel(displayedMonth / 12, displayedMonth % 12 + 1),
            expanded = expanded,
            canGoBack = !expanded && pagerState.currentPage > 0,
            canGoForward = !expanded && pagerState.currentPage < pageCount - 1,
            // From the target page, so repeated taps during the scroll keep stepping.
            onBack = {
                scope.launch {
                    pagerState.animateScrollToPage(
                        (pagerState.targetPage - 1).coerceAtLeast(0),
                        animationSpec = tween(PAGE_SCROLL_DURATION_MILLIS, easing = CalendarPageEasing),
                    )
                }
            },
            onForward = {
                scope.launch {
                    pagerState.animateScrollToPage(
                        (pagerState.targetPage + 1).coerceAtMost(pageCount - 1),
                        animationSpec = tween(PAGE_SCROLL_DURATION_MILLIS, easing = CalendarPageEasing),
                    )
                }
            },
            onToggleExpanded = {
                if (expanded) pagerState.requestScrollToPage(selectedPage)
                expanded = !expanded
            },
            colors = colors,
            textStyle = headerTextStyle,
        )
        AnimatedContent(
            targetState = expanded,
            transitionSpec = {
                // coui_calendar_picker_fade_in_material / _fade_out_material.
                fadeIn(tween(FADE_IN_DURATION_MILLIS, easing = LinearEasing)) togetherWith
                    fadeOut(tween(FADE_OUT_DURATION_MILLIS, easing = CalendarFadeOutEasing))
            },
            contentAlignment = Alignment.Center,
        ) { showWheel ->
            if (showWheel) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(CalendarPickerDefaults.MonthHeight),
                    contentAlignment = Alignment.Center,
                ) {
                    // COUICalendarYearView: a COUIDatePicker pulled up by layout_marginTop -36dp.
                    DatePicker(
                        value = selected,
                        onValueChange = { date ->
                            val epoch = civilToEpochDay(date.year, date.month, date.day).coerceIn(currentMinEpoch, currentMaxEpoch)
                            currentOnValueChange(epochDayToCivil(epoch))
                        },
                        modifier = Modifier.offset(y = CalendarPickerDefaults.YearViewOffset),
                        yearRange = minDate.year..maxDate.year,
                    )
                }
            } else {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth(),
                    key = { it },
                ) { page ->
                    val month = firstMonthIndex + page
                    CalendarMonth(
                        year = month / 12,
                        month = month % 12 + 1,
                        selected = selected,
                        today = today,
                        minEpoch = minEpoch,
                        maxEpoch = maxEpoch,
                        firstDayOfWeek = firstDayOfWeek,
                        weekdayLabel = weekdayLabel,
                        dayLabel = dayLabel,
                        colors = colors,
                        weekdayTextStyle = weekdayTextStyle,
                        dayTextStyle = dayTextStyle,
                        onDaySelected = onDaySelected,
                    )
                }
            }
        }
    }
}

/** coui_calendar_picker_header_material.xml: previous button, month-year label + arrow, next button. */
@Composable
private fun CalendarPickerHeader(
    label: String,
    expanded: Boolean,
    canGoBack: Boolean,
    canGoForward: Boolean,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onToggleExpanded: () -> Unit,
    colors: CalendarPickerColors,
    textStyle: TextStyle,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(CalendarPickerDefaults.HeaderHeight)
            .padding(horizontal = CalendarPickerDefaults.HeaderPaddingHorizontal),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        CalendarChevronButton(
            forward = false,
            visible = canGoBack,
            color = colors.headerColor,
            onClick = onBack,
        )
        // COUIRotateView: 180 degree turn over 400ms with PathInterpolator(0.133, 0, 0.3, 1).
        val arrowRotation by animateFloatAsState(
            targetValue = if (expanded) 180f else 0f,
            animationSpec = tween(ARROW_ROTATE_DURATION_MILLIS, easing = ArrowRotateEasing),
        )
        val arrowColor by animateColorAsState(
            targetValue = if (expanded) colors.expandedArrowColor else colors.arrowColor,
            animationSpec = tween(ARROW_ROTATE_DURATION_MILLIS, easing = ArrowRotateEasing),
        )
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .clickable(role = Role.Button, onClick = onToggleExpanded),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                color = colors.headerColor,
                style = textStyle,
                maxLines = 2,
            )
            Canvas(
                modifier = Modifier
                    .size(CalendarPickerDefaults.ArrowSize)
                    .rotate(arrowRotation),
            ) {
                scale(size.width / 16f, size.height / 16f, pivot = Offset.Zero) {
                    drawPath(path = ArrowPath, color = arrowColor)
                }
            }
        }
        CalendarChevronButton(
            forward = true,
            visible = canGoForward,
            color = colors.headerColor,
            onClick = onForward,
        )
    }
}

/** The 24dp coui_ic_chevron_start / _end button with 4dp padding; invisible at the first / last page. */
@Composable
private fun CalendarChevronButton(
    forward: Boolean,
    visible: Boolean,
    color: Color,
    onClick: () -> Unit,
) {
    val mirrored = forward != (LocalLayoutDirection.current == LayoutDirection.Rtl)
    Box(
        modifier = Modifier
            .alpha(if (visible) 1f else 0f)
            .clip(CircleShape)
            .clickable(enabled = visible, role = Role.Button, onClick = onClick)
            .semantics { contentDescription = if (forward) "Next month" else "Previous month" }
            .padding(CalendarPickerDefaults.ChevronPadding)
            .size(CalendarPickerDefaults.ChevronSize),
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            // coui_ic_chevron_start; the end chevron is the same vector mirrored (autoMirrored).
            scale(scaleX = if (mirrored) -1f else 1f, scaleY = 1f) {
                scale(size.width / 24f, size.height / 24f, pivot = Offset.Zero) {
                    drawPath(
                        path = ChevronPath,
                        color = color,
                        style = Stroke(width = 1.4f, cap = StrokeCap.Round),
                    )
                }
            }
        }
    }
}

/** One COUIDateMonthView page: the weekday row and the day grid. */
@Composable
private fun CalendarMonth(
    year: Int,
    month: Int,
    selected: DateValue,
    today: DateValue?,
    minEpoch: Long,
    maxEpoch: Long,
    firstDayOfWeek: Int,
    weekdayLabel: (Int) -> String,
    dayLabel: (Int) -> String,
    colors: CalendarPickerColors,
    weekdayTextStyle: TextStyle,
    dayTextStyle: TextStyle,
    onDaySelected: (DateValue) -> Unit,
) {
    val daysOfMonth = daysInMonth(year, month)
    // COUIDateMonthView.w(): the number of leading cells before day 1.
    val leading = (dayOfWeek(year, month, 1) - firstDayOfWeek).mod(7)
    val sixRows = leading + daysOfMonth > 35
    val selectedDay = if (selected.year == year && selected.month == month) selected.day else null
    val todayDay = if (today != null && today.year == year && today.month == month) today.day else null
    val firstEpoch = civilToEpochDay(year, month, 1)
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    // COUIDateMonthView.q(): the new selection grows 0.8 -> 1 and fades in over 280ms; the old
    // selection of the same month fades out over 150ms.
    val appear = remember { Animatable(1f) }
    val fade = remember { Animatable(0f) }
    val previous = remember { SelectionHolder(selectedDay) }
    LaunchedEffect(selectedDay) {
        val old = previous.day
        previous.day = selectedDay
        if (old == selectedDay || selectedDay == null) return@LaunchedEffect
        previous.fadingDay = old
        launch {
            appear.snapTo(0f)
            appear.animateTo(1f, tween(SELECT_DURATION_MILLIS, easing = CalendarPageEasing))
        }
        if (old != null) {
            launch {
                fade.snapTo(1f)
                fade.animateTo(0f, tween(DESELECT_DURATION_MILLIS, easing = CalendarFadeOutEasing))
            }
        }
    }
    var pressedCell by remember { mutableStateOf<Int?>(null) }
    val currentOnDaySelected by rememberUpdatedState(onDaySelected)
    val textMeasurer = rememberTextMeasurer(cacheSize = 48)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(CalendarPickerDefaults.MonthHeight)
            .padding(horizontal = CalendarPickerDefaults.MonthPaddingHorizontal)
            .pointerInput(year, month, leading, sixRows, isRtl) {
                fun cellAt(offset: Offset): Int? {
                    val cell = size.width / 7f
                    val top = CalendarPickerDefaults.WeekdayHeight.toPx()
                    if (offset.x < 0f || offset.x >= cell * 7f || offset.y < top || offset.y >= size.height) return null
                    val rowHeight = rowHeightPx(sixRows)
                    var column = (offset.x / cell).toInt().coerceIn(0, 6)
                    if (isRtl) column = 6 - column
                    val row = ((offset.y - top) / rowHeight).toInt()
                    if (row >= (if (sixRows) 6 else 5)) return null
                    return column + row * 7
                }
                detectTapGestures(
                    onPress = { offset ->
                        pressedCell = cellAt(offset)
                        tryAwaitRelease()
                        pressedCell = null
                    },
                    onTap = { offset ->
                        val cell = cellAt(offset) ?: return@detectTapGestures
                        // COUIDateMonthView.J(): cells before day 1 / after the last day pick
                        // the adjacent month's day.
                        currentOnDaySelected(epochDayToCivil(firstEpoch + cell - leading))
                    },
                )
            },
    ) {
        val cell = size.width / 7f
        val weekdayHeight = CalendarPickerDefaults.WeekdayHeight.toPx()
        val rowHeight = rowHeightPx(sixRows)
        val circleRadius = CalendarPickerDefaults.DayCircleDiameter.toPx() / 2f
        fun columnCenter(column: Int): Float {
            val x = cell * column + cell / 2f
            return if (isRtl) size.width - x else x
        }

        // Weekday row (COUIDateMonthView.s()).
        for (column in 0 until 7) {
            val dayOfWeek = (firstDayOfWeek - 1 + column) % 7 + 1
            val layout = textMeasurer.measure(weekdayLabel(dayOfWeek), weekdayTextStyle)
            drawText(
                textLayoutResult = layout,
                color = colors.weekdayColor,
                topLeft = Offset(columnCenter(column) - layout.size.width / 2f, weekdayHeight / 2f - layout.size.height / 2f),
            )
        }

        val cellCount = if (sixRows) 42 else 35
        for (index in 0 until cellCount) {
            val column = index % 7
            val row = index / 7
            val center = Offset(columnCenter(column), weekdayHeight + rowHeight * row + rowHeight / 2f)
            val epoch = firstEpoch + index - leading
            val inMonth = index >= leading && index < leading + daysOfMonth
            val day = if (inMonth) index - leading + 1 else epochDayToCivil(epoch).day
            val enabled = epoch in minEpoch..maxEpoch
            val isSelected = inMonth && day == selectedDay
            val isPressed = pressedCell == index
            var textColor = when {
                !inMonth || !enabled -> colors.disabledDayColor
                else -> colors.dayColor
            }
            if (isSelected) {
                val progress = appear.value
                val baseAlpha = if (isPressed) SELECTED_PRESSED_ALPHA else 1f
                drawCircle(
                    color = colors.selectedColor.copy(alpha = colors.selectedColor.alpha * baseAlpha * progress),
                    radius = circleRadius * (0.8f + 0.2f * progress),
                    center = center,
                )
                textColor = colors.onSelectedColor
            } else if (isPressed) {
                if (inMonth && enabled) drawCircle(color = colors.pressedColor, radius = circleRadius, center = center)
            } else if (inMonth && day == previous.fadingDay && fade.value > 0f && enabled) {
                drawCircle(
                    color = colors.selectedColor.copy(alpha = colors.selectedColor.alpha * fade.value),
                    radius = circleRadius,
                    center = center,
                )
            }
            if (inMonth && day == todayDay && !isSelected) {
                textColor = colors.todayColor
                drawCircle(
                    color = colors.todayColor,
                    radius = circleRadius,
                    center = center,
                    style = Stroke(width = CalendarPickerDefaults.TodayStrokeWidth.toPx()),
                )
            }
            val layout = textMeasurer.measure(dayLabel(day), dayTextStyle)
            drawText(
                textLayoutResult = layout,
                color = textColor,
                topLeft = Offset(center.x - layout.size.width / 2f, center.y - layout.size.height / 2f),
            )
        }
    }
}

/** Remembers the last selected day and the day whose circle is fading out. */
private class SelectionHolder(var day: Int?) {
    var fadingDay: Int? = null
}

/** Row height in px: 40dp, plus calendar_picker_day_min_col_padding (8dp) for five-week months. */
private fun Density.rowHeightPx(sixRows: Boolean): Float = (CalendarPickerDefaults.DayHeight + if (sixRows) 0.dp else CalendarPickerDefaults.FiveRowExtraHeight).toPx()

/** Months since year 0 of a year and 1-based month. */
private fun monthIndex(year: Int, month: Int): Int = year * 12 + month - 1

/** Contains default values used by [CalendarPicker]. */
object CalendarPickerDefaults {

    /** The earliest selectable date when none is given (COUICalendarDayPickerView: 1900-01-01). */
    val MinDate: DateValue = DateValue(1900, 1, 1)

    /** The latest selectable date when none is given (COUICalendarDayPickerView: 2100-12-31). */
    val MaxDate: DateValue = DateValue(2100, 12, 31)

    /** The first column of the grid: Sunday (java.util.Calendar.SUNDAY). */
    val FirstDayOfWeek: Int = 1

    /** Padding around the picker (coui_calendar_picker_material.xml layout_marginVertical 16dp). */
    val InsideMargin = PaddingValues(vertical = 16.dp)

    /** Height of the header row (coui_calendar_picker_header_material.xml 32dp). */
    val HeaderHeight = 32.dp

    /** Horizontal padding of the header row (paddingStart / paddingEnd 24dp). */
    val HeaderPaddingHorizontal = 24.dp

    /** Size of the chevron icons (coui_ic_chevron_start / _end, minWidth 24dp). */
    val ChevronSize = 24.dp

    /** Padding of the chevron buttons (android:padding 4dp). */
    val ChevronPadding = 4.dp

    /** Size of the expand arrow (calendar_picker_title_icon_size). */
    val ArrowSize = 16.dp

    /** Horizontal padding of the month grid (calendar_picker_padding_horizontal). */
    val MonthPaddingHorizontal = 16.dp

    /** Height of the weekday row (calendar_picker_day_of_week_height). */
    val WeekdayHeight = 40.dp

    /** Height of a week row (calendar_picker_day_height). */
    val DayHeight = 40.dp

    /** Extra height of every row of a five-week month (calendar_picker_day_min_col_padding). */
    val FiveRowExtraHeight = 8.dp

    /** Height of a month page: the weekday row and six 40dp week rows (COUIDateMonthView.onMeasure). */
    val MonthHeight = 280.dp

    /** Diameter of the selection, press and today circles (calendar_picker_current_day_radius, used as a diameter). */
    val DayCircleDiameter = 32.dp

    /** Stroke width of the today ring (calendar_picker_current_day_stroke_radius). */
    val TodayStrokeWidth = 1.8.dp

    /** Vertical offset of the expanded wheel: half of COUICalendarYearView's -36dp top margin. */
    val YearViewOffset = (-18).dp

    /** The system date, read once (the date ringed as today). */
    @Composable
    fun today(): DateValue = remember { platformToday() }

    /** Default header label: the English "MMMMy" pattern, e.g. "October 2026". */
    val MonthYearLabel: (Int, Int) -> String = { year, month -> "${MonthNames[month - 1]} $year" }

    /** Default weekday label: the shortest English names (DateUtils.LENGTH_SHORTEST), S M T W T F S. */
    val WeekdayLabel: (Int) -> String = { ShortestWeekdays[it - 1] }

    /** Default day label: the plain day number. */
    val DayLabel: (Int) -> String = { it.toString() }

    private val MonthNames = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December",
    )

    private val ShortestWeekdays = listOf("S", "M", "T", "W", "T", "F", "S")

    /**
     * The default [CalendarPickerColors]: couiColorPrimaryNeutral header and days,
     * couiColorSecondNeutral collapsed arrow (couiColorPrimary when expanded), couiColorHintNeutral
     * weekdays, couiColorDisabledNeutral adjacent / out-of-range days, couiColorPrimary selection
     * and today ring, couiColorOnPrimary selected text and the press highlight.
     */
    @Composable
    fun calendarPickerColors(
        headerColor: Color = COUITheme.colorScheme.onSurface,
        arrowColor: Color = COUITheme.colorScheme.onSurfaceSecondary,
        expandedArrowColor: Color = COUITheme.colorScheme.primary,
        weekdayColor: Color = COUITheme.colorScheme.onSurfaceContainerHigh,
        dayColor: Color = COUITheme.colorScheme.onSurface,
        disabledDayColor: Color = COUITheme.colorScheme.disabledOnSurface,
        selectedColor: Color = COUITheme.colorScheme.primary,
        onSelectedColor: Color = COUITheme.colorScheme.onPrimary,
        todayColor: Color = COUITheme.colorScheme.primary,
        pressedColor: Color = COUITheme.colorScheme.press,
    ): CalendarPickerColors = remember(
        headerColor,
        arrowColor,
        expandedArrowColor,
        weekdayColor,
        dayColor,
        disabledDayColor,
        selectedColor,
        onSelectedColor,
        todayColor,
        pressedColor,
    ) {
        CalendarPickerColors(
            headerColor = headerColor,
            arrowColor = arrowColor,
            expandedArrowColor = expandedArrowColor,
            weekdayColor = weekdayColor,
            dayColor = dayColor,
            disabledDayColor = disabledDayColor,
            selectedColor = selectedColor,
            onSelectedColor = onSelectedColor,
            todayColor = todayColor,
            pressedColor = pressedColor,
        )
    }
}

/**
 * Colors used by a [CalendarPicker].
 *
 * @param headerColor The color of the month-year label and the chevrons.
 * @param arrowColor The color of the collapsed expand arrow.
 * @param expandedArrowColor The color of the expanded arrow.
 * @param weekdayColor The color of the weekday labels.
 * @param dayColor The color of the days of the shown month.
 * @param disabledDayColor The color of adjacent-month and out-of-range days.
 * @param selectedColor The fill of the selected day.
 * @param onSelectedColor The label color of the selected day.
 * @param todayColor The ring and label color of today.
 * @param pressedColor The highlight of a pressed day.
 */
@Immutable
data class CalendarPickerColors(
    val headerColor: Color,
    val arrowColor: Color,
    val expandedArrowColor: Color,
    val weekdayColor: Color,
    val dayColor: Color,
    val disabledDayColor: Color,
    val selectedColor: Color,
    val onSelectedColor: Color,
    val todayColor: Color,
    val pressedColor: Color,
)

/** ViewPager page scroll duration (c1.a scroller, 300ms). */
private const val PAGE_SCROLL_DURATION_MILLIS = 300

/** coui_calendar_picker_fade_in_material duration. */
private const val FADE_IN_DURATION_MILLIS = 280

/** coui_calendar_picker_fade_out_material duration. */
private const val FADE_OUT_DURATION_MILLIS = 150

/** COUIDateMonthView selection animator duration. */
private const val SELECT_DURATION_MILLIS = 280

/** COUIDateMonthView deselection animator duration. */
private const val DESELECT_DURATION_MILLIS = 150

/** COUIRotateView duration. */
private const val ARROW_ROTATE_DURATION_MILLIS = 400

/** COUIDateMonthView.setDaySelectorColor: a pressed selected day is drawn at alpha 176. */
private const val SELECTED_PRESSED_ALPHA = 176f / 255f

/** The page scroller and selection interpolator (COUI w0.f: PathInterpolator(0.3, 0, 0.1, 1)). */
private val CalendarPageEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

/** The fade-out interpolator (coui_ease / w0.c: PathInterpolator(0.33, 0, 0.67, 1)). */
private val CalendarFadeOutEasing = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

/** COUIRotateView interpolator (0.133, 0, 0.3, 1). */
private val ArrowRotateEasing = CubicBezierEasing(0.133f, 0f, 0.3f, 1f)

/** coui_ic_chevron_start in its 24x24 viewport. */
private val ChevronPath = Path().apply {
    moveTo(13.84f, 5.636f)
    lineTo(7.829f, 11.646f)
    cubicTo(7.634f, 11.842f, 7.634f, 12.158f, 7.829f, 12.354f)
    lineTo(13.84f, 18.364f)
}

/** coui_calendar_arrow_collapsed (a rounded down triangle) in its 16x16 viewport. */
private val ArrowPath = Path().apply {
    moveTo(4.1f, 5.5f)
    lineTo(11.9f, 5.5f)
    quadraticTo(12.5f, 5.5f, 12.5f, 6.1f)
    quadraticTo(12.5f, 6.3f, 12.37f, 6.48f)
    lineTo(8.58f, 11.22f)
    quadraticTo(8.03f, 11.72f, 7.47f, 11.22f)
    lineTo(3.63f, 6.48f)
    quadraticTo(3.37f, 5.95f, 3.72f, 5.63f)
    quadraticTo(3.9f, 5.5f, 4.1f, 5.5f)
    close()
}
