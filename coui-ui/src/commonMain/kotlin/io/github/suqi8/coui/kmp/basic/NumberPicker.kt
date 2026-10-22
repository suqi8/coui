// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.utils.CouiHapticEffect
import io.github.suqi8.coui.kmp.utils.rememberCouiScaledHaptic
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.roundToInt
import kotlin.math.sign
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

/**
 * A [NumberPicker] component with COUI style.
 *
 * A vertical scroll wheel that shows a range of values with the selected one centered, matching
 * COUINumberPicker:
 * - every row is drawn twice and hard-clipped at the center band (center ± 24dp): the part
 *   inside the band uses [selectedTextSize] and the selected color, the part outside uses
 *   [unselectedTextSize] and the unselected color. There is no gradual blend.
 * - a fling uses the Android spline at 1.6x the release velocity (clamped to 750..5000 px/s),
 *   lands on a whole row and finishes with a 300ms snap. Android uses these raw px/s values like
 *   COUI; other platforms treat them as px on a 3.5x reference phone and scale them by the actual
 *   density, so a flick feels the same.
 * - [onValueChange] fires on every value change while the wheel moves, not only when it settles.
 *   Each change also plays a haptic tick whose intensity follows the wheel speed (50..100% up to
 *   5000 px/s, at most one every 25ms, like COUI's VibrateUtils) and, on Android with touch
 *   sounds enabled, the COUI click sound.
 * - tapping the row above or below the center steps by one; holding it repeats the step every
 *   [longPressStepInterval] milliseconds.
 *
 * Text sizes are in dp, not sp, so they do not follow the system font scale (like COUI).
 *
 * @param value The current selected value. If outside [range], it will be coerced into the range.
 * @param onValueChange The callback invoked whenever the selected value changes.
 * @param modifier The modifier to be applied to the [NumberPicker].
 * @param enabled Whether the [NumberPicker] is enabled for user interaction.
 * @param range The range of selectable values.
 * @param label A function that converts a value to its display string.
 * @param unit A unit label drawn after the selected value (COUI setUnitText); empty to hide.
 * @param visibleItemCount The number of visible items. Must be odd and at least 3.
 * @param wrapAround Whether the picker wraps around from the last item to the first (infinite
 *   scrolling). As in COUI, wrapping only applies when the range has more items than
 *   [visibleItemCount].
 * @param longPressStepInterval The interval, in milliseconds, between auto-repeated steps while a
 *   row above or below the center is held.
 * @param colors The [NumberPickerColors] for this [NumberPicker].
 * @param textStyle The base [TextStyle] for the items. Its font size is replaced by
 *   [selectedTextSize] and [unselectedTextSize]; a missing font weight defaults to Medium.
 * @param selectedTextSize The text size inside the center band.
 * @param unselectedTextSize The text size outside the center band.
 * @param itemHeight The height of each item in the picker.
 */
@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    range: IntRange = 0..10,
    label: (Int) -> String = { it.toString() },
    unit: String = "",
    visibleItemCount: Int = 3,
    wrapAround: Boolean = false,
    longPressStepInterval: Long = NumberPickerDefaults.LongPressStepInterval,
    colors: NumberPickerColors = NumberPickerDefaults.colors(),
    textStyle: TextStyle = COUITheme.textStyles.title3,
    selectedTextSize: Dp = NumberPickerDefaults.SelectedTextSize,
    unselectedTextSize: Dp = NumberPickerDefaults.UnselectedTextSize,
    itemHeight: Dp = NumberPickerDefaults.ItemHeight,
) {
    require(visibleItemCount % 2 == 1 && visibleItemCount >= 3) {
        "visibleItemCount must be odd and at least 3, but was $visibleItemCount"
    }
    require(range.first <= range.last) {
        "range must not be empty"
    }

    val itemCount = range.last - range.first + 1
    val coercedValue = value.coerceIn(range)
    val currentIndex = coercedValue - range.first
    // COUINumberPicker.updateWrapSelectorWheel: max - min >= rows and wrapping preferred.
    val canWrap = wrapAround && itemCount > visibleItemCount

    val performHaptic = rememberCouiScaledHaptic()
    val playClick = rememberNumberPickerClickSound()
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    // Wheel position in rows: the row at the center is `position`. Unbounded while wrapping;
    // normalised back into 0 until itemCount whenever the wheel comes to rest.
    val position = remember { Animatable(currentIndex.toFloat()) }
    // Index last reported to the caller (not wrapped), advanced with COUI's 0.95-row hysteresis.
    val tracker = remember { NumberPickerTracker(currentIndex) }
    var pressed by remember { mutableStateOf(false) }
    var animationJob by remember { mutableStateOf<Job?>(null) }

    val latestOnValueChange by rememberUpdatedState(onValueChange)
    val latestPerformHaptic by rememberUpdatedState(performHaptic)
    val latestPlayClick by rememberUpdatedState(playClick)
    val velocityScale = numberPickerVelocityScale(density.density)
    val minFlingVelocity = NumberPickerMinFlingVelocity * velocityScale
    val maxFlingVelocity = NumberPickerMaxFlingVelocity * velocityScale
    val latestMaxFlingVelocity by rememberUpdatedState(maxFlingVelocity)
    val latestRowPx by rememberUpdatedState(with(density) { itemHeight.toPx() })
    val latestRangeStart by rememberUpdatedState(range.first)
    val latestItemCount by rememberUpdatedState(itemCount)
    val latestCanWrap by rememberUpdatedState(canWrap)
    val latestStepInterval by rememberUpdatedState(longPressStepInterval)

    fun wrapIndex(index: Int): Int = if (latestCanWrap) {
        index.mod(latestItemCount)
    } else {
        index.coerceIn(0, latestItemCount - 1)
    }

    fun clampPosition(target: Float): Float = if (latestCanWrap) target else target.coerceIn(0f, (latestItemCount - 1).toFloat())

    // Follow external value / range changes while the wheel is at rest.
    LaunchedEffect(currentIndex, itemCount, canWrap) {
        if (!pressed && animationJob?.isActive != true && (wrapIndex(tracker.reported) != currentIndex || position.value != currentIndex.toFloat())) {
            tracker.rebase(currentIndex) { position.snapTo(currentIndex.toFloat()) }
        }
    }

    // COUINumberPicker.scrollBy: the value changes once the wheel has moved 0.95 of a row, and every
    // change notifies the listener and plays the feedback (setValueInternal -> performFeedback).
    LaunchedEffect(Unit) {
        snapshotFlow { position.value }.collect {
            val now = TimeSource.Monotonic.markNow()
            val current = position.value
            tracker.trackSpeed(current, now, latestRowPx, latestMaxFlingVelocity)
            if (tracker.rebasing) return@collect
            val next = numberPickerHysteresisIndex(tracker.reported, current)
            if (next != tracker.reported) {
                tracker.reported = next
                if (tracker.takeHapticSlot(now)) {
                    latestPerformHaptic(
                        CouiHapticEffect.Strength,
                        numberPickerHapticIntensity(tracker.speed, latestMaxFlingVelocity),
                    )
                }
                latestPlayClick()
                latestOnValueChange(latestRangeStart + wrapIndex(next))
            }
        }
    }

    // Snap to the nearest row (COUI mAdjustScroller: 300ms, PathInterpolator(0, 0, 0.4, 1)), then
    // normalise the position so it stays small while wrapping.
    suspend fun settle() {
        val target = clampPosition(position.value.roundToInt().toFloat())
        if (position.value != target) {
            position.animateTo(target, tween(NumberPickerSnapDurationMillis, easing = NumberPickerSnapEasing))
        }
        val index = wrapIndex(position.value.roundToInt())
        if (position.value != index.toFloat() || tracker.reported != index) {
            tracker.rebase(index) { position.snapTo(index.toFloat()) }
        }
    }

    fun launchAnimation(block: suspend () -> Unit) {
        animationJob?.cancel()
        animationJob = scope.launch { block() }
    }

    // COUINumberPicker.changeValueByOne: one row over 300ms on the fling scroller, then adjust.
    fun step(direction: Int) {
        val from = (if (animationJob?.isActive == true) position.targetValue else position.value).roundToInt()
        val target = clampPosition((from + direction).toFloat())
        launchAnimation {
            position.animateTo(target, tween(NumberPickerSnapDurationMillis, easing = NumberPickerFlingEasing))
            settle()
        }
    }

    fun fling(velocity: Float, rowPx: Float, from: Float) {
        val fling = numberPickerFling(velocity, density.density)
        val rows = numberPickerQuantisedRows(fling.distance, rowPx)
        // A downward finger (positive velocity) reveals the previous values.
        val target = clampPosition((from - sign(velocity) * rows).roundToInt().toFloat())
        launchAnimation {
            if (position.value != target) {
                position.animateTo(target, tween(fling.durationMillis.coerceAtLeast(1), easing = NumberPickerFlingEasing))
            }
            settle()
        }
    }

    val textMeasurer = rememberTextMeasurer(cacheSize = (visibleItemCount + 3) * 2 + 2)
    val baseStyle = remember(textStyle) {
        // Rows are centered on the font metrics, so the theme line height is dropped.
        textStyle.copy(
            fontWeight = textStyle.fontWeight ?: FontWeight.Medium,
            lineHeight = TextUnit.Unspecified,
        )
    }
    val selectedStyle = remember(baseStyle, selectedTextSize, density) {
        baseStyle.copy(fontSize = with(density) { selectedTextSize.toSp() })
    }
    val unselectedStyle = remember(baseStyle, unselectedTextSize, density) {
        baseStyle.copy(fontSize = with(density) { unselectedTextSize.toSp() })
    }
    // COUI draws the unit with Typeface.DEFAULT (regular weight) at 14dp.
    val unitStyle = remember(density) {
        TextStyle(fontSize = with(density) { NumberPickerDefaults.UnitTextSize.toSp() }, fontWeight = FontWeight.Normal)
    }
    // COUI centers every row on the default typeface's top/bottom metrics at the unselected size.
    val fontCenter = remember(unselectedTextSize, density) {
        val (top, bottom) = numberPickerFontTopBottom(with(density) { unselectedTextSize.toPx() })
        (top + bottom) / 2f
    }
    val selectedColor = colors.selectedTextColor(enabled)
    val unselectedColor = colors.unselectedTextColor(enabled)
    val latestLabel by rememberUpdatedState(label)

    val displayValue = label(coercedValue)
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(itemHeight * visibleItemCount)
            .clipToBounds()
            .semantics {
                contentDescription = "$displayValue, ${range.first} - ${range.last}"
            }
            .then(
                if (enabled) {
                    Modifier.pointerInput(visibleItemCount, density) {
                        val touchSlop = viewConfiguration.touchSlop
                        val longPressTimeout = viewConfiguration.longPressTimeoutMillis
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            val rowPx = size.height.toFloat() / visibleItemCount
                            val centerY = size.height / 2f
                            pressed = true
                            // A touch while the wheel moves only stops it (COUI onInterceptTouchEvent).
                            val wasMoving = animationJob?.isActive == true
                            animationJob?.cancel()
                            val holdDirection = when {
                                wasMoving -> 0
                                down.position.y < centerY - rowPx / 2f -> -1
                                down.position.y > centerY + rowPx / 2f -> 1
                                else -> 0
                            }
                            val holdJob = if (holdDirection != 0) {
                                scope.launch {
                                    delay(longPressTimeout)
                                    while (true) {
                                        step(holdDirection)
                                        delay(latestStepInterval)
                                    }
                                }
                            } else {
                                null
                            }

                            val tracker = VelocityTracker()
                            tracker.addPosition(down.uptimeMillis, down.position)
                            var dragging = false
                            var dragPosition = position.value
                            var lastY = down.position.y
                            var upY = down.position.y
                            var upTime = down.uptimeMillis
                            var released = false
                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                tracker.addPosition(change.uptimeMillis, change.position)
                                if (!change.pressed) {
                                    released = true
                                    upY = change.position.y
                                    upTime = change.uptimeMillis
                                    break
                                }
                                if (!dragging && abs(change.position.y - down.position.y) > touchSlop) {
                                    dragging = true
                                    holdJob?.cancel()
                                    animationJob?.cancel()
                                    dragPosition = position.value
                                } else if (dragging) {
                                    dragPosition = clampPosition(dragPosition - (change.position.y - lastY) / rowPx)
                                    val target = dragPosition
                                    scope.launch { position.snapTo(target) }
                                }
                                if (dragging) change.consume()
                                lastY = change.position.y
                            }
                            holdJob?.cancel()
                            pressed = false

                            val velocity = tracker.calculateVelocity().y
                                .coerceIn(-maxFlingVelocity, maxFlingVelocity)
                            val stepping = animationJob?.isActive == true
                            when {
                                released && abs(velocity) > minFlingVelocity ->
                                    fling(velocity * NumberPickerFlingVelocityRatio, rowPx, if (dragging) dragPosition else position.value)

                                !released ||
                                    abs(upY - down.position.y) > touchSlop ||
                                    upTime - down.uptimeMillis >= longPressTimeout ->
                                    if (!stepping) launchAnimation { settle() }

                                else -> {
                                    // Tap: the row above steps back, the row below steps forward.
                                    val row = floor(upY / rowPx).toInt() - visibleItemCount / 2
                                    if (row != 0) step(sign(row.toFloat()).toInt()) else launchAnimation { settle() }
                                }
                            }
                        }
                    }
                } else {
                    Modifier
                },
            )
            .drawBehind {
                drawNumberPickerRows(
                    textMeasurer = textMeasurer,
                    position = position.value,
                    visibleItemCount = visibleItemCount,
                    itemCount = itemCount,
                    wrap = canWrap,
                    labelOf = { latestLabel(range.first + it) },
                    unit = unit,
                    selectedStyle = selectedStyle,
                    unselectedStyle = unselectedStyle,
                    unitStyle = unitStyle,
                    fontCenter = fontCenter,
                    selectedColor = selectedColor,
                    unselectedColor = unselectedColor,
                )
            },
    )
}

private fun DrawScope.drawNumberPickerRows(
    textMeasurer: TextMeasurer,
    position: Float,
    visibleItemCount: Int,
    itemCount: Int,
    wrap: Boolean,
    labelOf: (Int) -> String,
    unit: String,
    selectedStyle: TextStyle,
    unselectedStyle: TextStyle,
    unitStyle: TextStyle,
    fontCenter: Float,
    selectedColor: Color,
    unselectedColor: Color,
) {
    val rowPx = size.height / visibleItemCount
    val centerY = size.height / 2f
    val bandHalf = NumberPickerBandHalfHeight.toPx()
    val bandTop = centerY - bandHalf
    val bandBottom = centerY + bandHalf
    val half = visibleItemCount / 2
    val first = floor(position).toInt() - half - 1
    val last = ceil(position).toInt() + half + 1

    for (row in first..last) {
        val index = if (wrap) {
            row.mod(itemCount)
        } else {
            if (row !in 0 until itemCount) continue
            row
        }
        val text = labelOf(index)
        val normal = measureRow(textMeasurer, text, unselectedStyle)
        val focus = measureRow(textMeasurer, text, selectedStyle)
        // Both passes share one baseline: (2 * rowTop + rowHeight - top - bottom) / 2 with the
        // default typeface metrics at the normal size, as in COUINumberPicker.onDraw.
        val rowCenter = centerY + (row - position) * rowPx
        val baseline = rowCenter - fontCenter

        clipRect(top = bandTop, bottom = bandBottom, clipOp = ClipOp.Difference) {
            drawText(normal, color = unselectedColor, topLeft = Offset(rowTextX(normal), baseline - normal.firstBaseline))
        }
        clipRect(top = bandTop, bottom = bandBottom, clipOp = ClipOp.Intersect) {
            drawText(focus, color = selectedColor, topLeft = Offset(rowTextX(focus), baseline - focus.firstBaseline))
        }
    }

    if (unit.isNotEmpty()) {
        // The unit sits on the resting center row's baseline minus 2dp, after an 18dp value slot
        // plus a 4dp margin (coui_number_picker_text_width / _text_margin_start).
        val baseline = centerY - fontCenter - NumberPickerUnitMarginBottom.toPx()
        val unitLayout = measureRow(textMeasurer, unit, unitStyle)
        val x = size.width / 2f + (NumberPickerSelectedValueWidth / 2 + NumberPickerUnitMarginStart).toPx()
        drawText(unitLayout, color = selectedColor, topLeft = Offset(x, baseline - unitLayout.firstBaseline))
    }
}

private fun measureRow(textMeasurer: TextMeasurer, text: String, style: TextStyle): TextLayoutResult = textMeasurer.measure(text = text, style = style, softWrap = false, maxLines = 1)

// COUI centers the text, or left-aligns it when it is wider than the wheel.
private fun DrawScope.rowTextX(layout: TextLayoutResult): Float = if (layout.size.width >= size.width) 0f else (size.width - layout.size.width) / 2f

/**
 * Tracks the index reported to the caller. [rebase] moves the wheel without notifying, so the
 * hysteresis collector never sees the reported index and the position out of step.
 */
private class NumberPickerTracker(var reported: Int) {
    var rebasing = false
        private set

    /** Wheel speed in px/s, clamped to the max fling velocity (COUI mScrollerVelocity). */
    var speed = 0f
        private set
    private var lastPosition = Float.NaN
    private var lastMark: TimeSource.Monotonic.ValueTimeMark? = null
    private var lastHapticMark: TimeSource.Monotonic.ValueTimeMark? = null

    fun trackSpeed(position: Float, now: TimeSource.Monotonic.ValueTimeMark, rowPx: Float, maxVelocity: Float) {
        val previous = lastMark
        if (previous != null && !lastPosition.isNaN()) {
            val seconds = (now - previous).inWholeMicroseconds / 1_000_000f
            if (seconds > 0f) speed = (abs(position - lastPosition) * rowPx / seconds).coerceAtMost(maxVelocity)
        }
        lastPosition = position
        lastMark = now
    }

    /** VibrateUtils.filterVibrator: at most one haptic every 25ms. */
    fun takeHapticSlot(now: TimeSource.Monotonic.ValueTimeMark): Boolean {
        val previous = lastHapticMark
        if (previous != null && (now - previous) < NumberPickerHapticInterval) return false
        lastHapticMark = now
        return true
    }

    suspend fun rebase(index: Int, move: suspend () -> Unit) {
        rebasing = true
        try {
            move()
        } finally {
            reported = index
            rebasing = false
        }
    }
}

/**
 * COUI "crisp" picker vibration (VibrateUtils.setLinearMotorVibratorStrength, level 0): the RichTap
 * intensity runs from 50 to 100 as the speed goes from 0 to the max fling velocity. Returned as a
 * fraction of full strength.
 */
internal fun numberPickerHapticIntensity(speed: Float, maxVelocity: Float): Float {
    if (maxVelocity <= 0f) return NumberPickerHapticMinIntensity / 100f
    val intensity = (speed / maxVelocity) * (NumberPickerHapticMaxIntensity - NumberPickerHapticMinIntensity) + NumberPickerHapticMinIntensity
    return intensity.coerceIn(NumberPickerHapticMinIntensity, NumberPickerHapticMaxIntensity) / 100f
}

private val NumberPickerHapticMinIntensity = 50f
private val NumberPickerHapticMaxIntensity = 100f
private val NumberPickerHapticInterval = 25.milliseconds

/** The spline fling distance (px) and duration (ms) COUINumberPicker uses for a release velocity. */
internal data class NumberPickerFling(val distance: Float, val durationMillis: Int)

/**
 * COUINumberPicker.getSplineFlingDistance / getSplineFlingDuration with the AOSP scroll friction,
 * `ppi = density * 160` and `physicalCoeff = ppi * 386.0878 * 0.84`; the duration is stretched
 * by 1.5 as in COUINumberPicker.fling.
 */
internal fun numberPickerFling(velocity: Float, density: Float): NumberPickerFling {
    val physicalCoeff = density * 160f * 386.0878f * 0.84f
    val decelerationRate = (ln(0.78) / ln(0.9))
    val splineDeceleration = ln(abs(velocity) * 0.35 / (NumberPickerScrollFriction * physicalCoeff))
    val distance = NumberPickerScrollFriction * physicalCoeff *
        exp(decelerationRate / (decelerationRate - 1.0) * splineDeceleration)
    val duration = exp(splineDeceleration / (decelerationRate - 1.0)) * 1000.0
    return NumberPickerFling(distance.toFloat(), (duration * 1.5).toInt())
}

/**
 * COUINumberPicker.fling rounds a distance longer than one row down to whole rows; a shorter one
 * keeps its fraction (and the following snap decides the row).
 */
internal fun numberPickerQuantisedRows(distance: Float, rowPx: Float): Float {
    if (rowPx <= 0f) return 0f
    val rows = distance / rowPx
    return if (rows > 1f) floor(rows) else rows
}

/** Advances [reported] towards [position] once the wheel has moved 0.95 of a row past it. */
internal fun numberPickerHysteresisIndex(reported: Int, position: Float): Int {
    var index = reported
    while (position > index + NumberPickerValueChangeThreshold) index++
    while (position < index - NumberPickerValueChangeThreshold) index--
    return index
}

private val NumberPickerValueChangeThreshold = 0.95f
private val NumberPickerScrollFriction = 0.015f

// COUINumberPicker: mMinimumFlingVelocity / mMaximumFlingVelocity are raw px/s, not dp-scaled.
internal val NumberPickerMinFlingVelocity = 750f
internal val NumberPickerMaxFlingVelocity = 5000f

// COUINumberPicker.getDampRatio: min(1.8, 1.6).
internal val NumberPickerFlingVelocityRatio = 1.6f
private val NumberPickerSnapDurationMillis = 300

// COUINumberPicker FLING_INTERPOLATOR (adjust scroller) and SLOW_FLING_INTERPOLATOR (fling scroller).
private val NumberPickerSnapEasing = CubicBezierEasing(0f, 0f, 0.4f, 1f)
private val NumberPickerFlingEasing = CubicBezierEasing(0f, 0.23f, 0.1f, 1f)

// coui_selected_background_radius: the center band is 2 x 24dp tall.
private val NumberPickerBandHalfHeight = 24.dp
private val NumberPickerSelectedValueWidth = 18.dp
private val NumberPickerUnitMarginStart = 4.dp
private val NumberPickerUnitMarginBottom = 2.dp

/**
 * Contains default values used by [NumberPicker].
 */
object NumberPickerDefaults {

    /** Row height inside the COUI date and time pickers (135dp / 3 rows). */
    val ItemHeight = 45.dp

    /** Text size inside the center band (coui_numberpicker_textSize_big). */
    val SelectedTextSize = 20.dp

    /** Text size outside the center band (coui_numberpicker_textSize_small). */
    val UnselectedTextSize = 16.dp

    /** Text size of the unit label (coui_numberpicker_unit_textSize). */
    val UnitTextSize = 14.dp

    /** Interval between auto-repeated steps while a side row is held (COUINumberPicker default). */
    val LongPressStepInterval: Long = 300L

    /**
     * Creates the default [NumberPickerColors] for a [NumberPicker].
     *
     * COUI does not change the picker colors when it is disabled, so the disabled colors default
     * to the enabled ones.
     *
     * @param selectedTextColor The color for the selected (center) item text.
     * @param unselectedTextColor The color for unselected item text.
     * @param disabledSelectedTextColor The color for the selected item text when disabled.
     * @param disabledUnselectedTextColor The color for unselected item text when disabled.
     */
    @Composable
    fun colors(
        selectedTextColor: Color = COUITheme.colorScheme.onSurface,
        unselectedTextColor: Color = COUITheme.colorScheme.onSurfaceVariantActions,
        disabledSelectedTextColor: Color = selectedTextColor,
        disabledUnselectedTextColor: Color = unselectedTextColor,
    ): NumberPickerColors = remember(
        selectedTextColor,
        unselectedTextColor,
        disabledSelectedTextColor,
        disabledUnselectedTextColor,
    ) {
        NumberPickerColors(
            selectedTextColor = selectedTextColor,
            unselectedTextColor = unselectedTextColor,
            disabledSelectedTextColor = disabledSelectedTextColor,
            disabledUnselectedTextColor = disabledUnselectedTextColor,
        )
    }
}

/**
 * The colors for a [NumberPicker].
 *
 * @see NumberPickerDefaults.colors
 */
@Immutable
data class NumberPickerColors(
    private val selectedTextColor: Color,
    private val unselectedTextColor: Color,
    private val disabledSelectedTextColor: Color,
    private val disabledUnselectedTextColor: Color,
) {

    @Stable
    internal fun selectedTextColor(enabled: Boolean): Color = if (enabled) selectedTextColor else disabledSelectedTextColor

    @Stable
    internal fun unselectedTextColor(enabled: Boolean): Color = if (enabled) unselectedTextColor else disabledUnselectedTextColor
}

/**
 * Draws the two COUI picker hairlines at the center ± 24dp, inset 16dp from both sides
 * (COUIDatePicker / COUITimeLimitPicker.dispatchDraw), `max(0.33dp, 1px)` thick.
 */
internal fun Modifier.numberPickerDividers(color: Color, inset: Dp = NumberPickerDividerInset): Modifier = drawBehind {
    val thickness = NumberPickerDividerThickness.toPx().toInt().coerceAtLeast(1).toFloat()
    val insetPx = inset.toPx().toInt().toFloat()
    val radius = NumberPickerBandHalfHeight.toPx().toInt()
    val center = size.height / 2f
    val top = (center - radius).toInt().toFloat()
    val bottom = (center + radius).toInt().toFloat()
    val width = size.width - insetPx * 2
    if (width <= 0f) return@drawBehind
    drawRect(color, topLeft = Offset(insetPx, top), size = Size(width, thickness))
    drawRect(color, topLeft = Offset(insetPx, bottom), size = Size(width, thickness))
}

private val NumberPickerDividerThickness = 0.33.dp
internal val NumberPickerDividerInset = 16.dp
