// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.LocalContentColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * An integer whose digits roll like a drum when it changes, the port of the camera's
 * `RollingNumView` (the burst-shot counter) and `PaletteRollingNumView` (the palette and filter
 * strength values).
 *
 * Every digit sits in a cell as wide as the style's "0", so the number keeps its width while it
 * counts. When the value changes, each digit place whose digit changed rolls in 200ms on the curve
 * (0.6, 0, 0.3, 1) (`c9.a.k`): the old digit slides away by [rollDistance] while it tilts back 45° and
 * fades out, and the new digit slides in from the opposite side, tilting from 45° to flat and fading
 * in. Rising values roll upwards, falling values downwards. A place that changes again mid-roll
 * restarts from the digit it was rolling to. When the number of digits or the sign changes, the
 * whole number rolls at once instead (`PaletteRollingNumView`'s whole-value roll).
 *
 * Like the camera, the ones digit does not roll by default: it is the digit that changes on every
 * step, so it switches at once and only the carries (tens, hundreds, ...) roll. Set [rollOnesDigit]
 * to roll every place.
 *
 * @param value The number to show.
 * @param modifier The modifier to be applied to the number.
 * @param rollOnesDigit Whether the ones digit rolls too; the camera switches it at once.
 * @param minDigits The minimum number of digits, padded with leading zeros (the burst counter shows
 *   "05" with 2).
 * @param color The digit color. If [Color.Unspecified] and [style] has no color, [LocalContentColor].
 * @param style The text style of the digits.
 * @param rollDistance How far a digit travels while it rolls. [Dp.Unspecified] uses the font size of
 *   [style]; the camera uses [RollingNumberDefaults.BurstRollDistance] for its 14dp burst counter and
 *   [RollingNumberDefaults.PaletteRollDistance] for its 10dp palette values.
 */
@Composable
fun RollingNumber(
    value: Int,
    modifier: Modifier = Modifier,
    rollOnesDigit: Boolean = false,
    minDigits: Int = 1,
    color: Color = Color.Unspecified,
    style: TextStyle = COUITheme.textStyles.bodyXL,
    rollDistance: Dp = Dp.Unspecified,
) {
    val textColor = color.takeOrElse { style.color.takeOrElse { LocalContentColor.current } }
    val digitStyle = remember(style, textColor) { style.merge(TextStyle(color = textColor, textAlign = TextAlign.Center)) }
    val padding = minDigits.coerceIn(1, MAX_PLACES)
    val scope = rememberCoroutineScope()
    val state = remember { RollingNumberState(value, padding) }
    LaunchedEffect(value, padding, rollOnesDigit) {
        state.update(value, padding, rollOnesDigit, scope)
    }

    val density = LocalDensity.current
    val measurer = rememberTextMeasurer()
    val cell = remember(digitStyle, density) {
        val zero = measurer.measure("0", digitStyle).size
        val minus = measurer.measure("-", digitStyle).size
        RollingCellSize(
            digitWidth = with(density) { zero.width.toDp() },
            signWidth = with(density) { minus.width.toDp() },
            height = with(density) { zero.height.toDp() },
        )
    }
    val distancePx = with(density) {
        when {
            rollDistance.isSpecified -> rollDistance.toPx()
            digitStyle.fontSize.isSp -> digitStyle.fontSize.toPx()
            else -> cell.height.toPx()
        }
    }

    // Digits always read from the most significant place to the ones, whatever the layout direction.
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Box(
            modifier = modifier.clearAndSetSemantics { contentDescription = formatRolling(value, padding) },
            contentAlignment = Alignment.Center,
        ) {
            val whole = state.wholeProgress.value
            if (whole < 1f) {
                val direction = state.wholeDirection
                RollingRow(
                    text = formatRolling(state.wholeFrom, state.padding),
                    style = digitStyle,
                    cell = cell,
                    modifier = Modifier.rollingLayer(-direction * whole, distancePx, cell.height),
                )
                RollingRow(
                    text = formatRolling(state.target, state.padding),
                    style = digitStyle,
                    cell = cell,
                    modifier = Modifier.rollingLayer(direction * (1f - whole), distancePx, cell.height),
                )
            } else {
                RollingPlaces(state = state, style = digitStyle, cell = cell, distancePx = distancePx)
            }
        }
    }
}

/** The number at rest or with per-place rolls: the sign, then one cell per digit place. */
@Composable
private fun RollingPlaces(
    state: RollingNumberState,
    style: TextStyle,
    cell: RollingCellSize,
    distancePx: Float,
) {
    val target = state.target
    val digits = abs(target.toLong()).toString().padStart(state.padding, '0')
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (target < 0) RollingGlyph("-", style, Modifier.size(cell.signWidth, cell.height))
        digits.forEachIndexed { index, char ->
            val place = digits.length - 1 - index
            val roll = state.places[place]
            val progress = roll.progress.value
            Box(
                modifier = Modifier.size(cell.digitWidth, cell.height),
                contentAlignment = Alignment.Center,
            ) {
                if (progress < 1f) {
                    RollingGlyph(
                        text = roll.from.toString(),
                        style = style,
                        modifier = Modifier.rollingLayer(-roll.direction * progress, distancePx, cell.height),
                    )
                    RollingGlyph(
                        text = char.toString(),
                        style = style,
                        modifier = Modifier.rollingLayer(roll.direction * (1f - progress), distancePx, cell.height),
                    )
                } else {
                    RollingGlyph(char.toString(), style)
                }
            }
        }
    }
}

/** A whole number in fixed cells, used while the whole value rolls. */
@Composable
private fun RollingRow(
    text: String,
    style: TextStyle,
    cell: RollingCellSize,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        text.forEach { char ->
            val width = if (char == '-') cell.signWidth else cell.digitWidth
            RollingGlyph(char.toString(), style, Modifier.size(width, cell.height))
        }
    }
}

@Composable
private fun RollingGlyph(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        BasicText(text = text, style = style, softWrap = false, maxLines = 1)
    }
}

/**
 * Places a rolling glyph at [offset] of a roll (-1 fully gone upwards, 0 at rest, 1 waiting below),
 * the `RollingNumView.f` / `g` transform: the glyph is shifted by `offset * distance`, tilted about
 * the row's center line by `offset * 45°` (`Camera.rotateX`) and faded by `1 - |offset|`.
 */
private fun Modifier.rollingLayer(offset: Float, distancePx: Float, height: Dp): Modifier = graphicsLayer {
    val shift = offset * distancePx
    val heightPx = height.toPx()
    translationY = shift
    rotationX = offset * RollingNumberDefaults.RollAngle
    alpha = (1f - abs(offset)).coerceIn(0f, 1f)
    // Rotate about the row's center line, not the shifted glyph's own center.
    transformOrigin = TransformOrigin(0.5f, if (heightPx > 0f) 0.5f - shift / heightPx else 0.5f)
}

/** The measured cell sizes of a [RollingNumber]. */
private data class RollingCellSize(val digitWidth: Dp, val signWidth: Dp, val height: Dp)

/** One digit place of a [RollingNumber]: the digit it rolls from and the roll progress. */
@Stable
private class RollingPlace {
    var from by mutableIntStateOf(0)
    var direction by mutableIntStateOf(1)
    val progress = Animatable(1f)
}

/** The rolls of a [RollingNumber]. */
@Stable
private class RollingNumberState(initial: Int, initialPadding: Int) {
    var target by mutableIntStateOf(initial)
    var padding by mutableIntStateOf(initialPadding)
    var wholeFrom by mutableIntStateOf(initial)
    var wholeDirection by mutableIntStateOf(1)
    val wholeProgress = Animatable(1f)
    val places = List(MAX_PLACES) { RollingPlace() }

    fun update(value: Int, newPadding: Int, rollOnesDigit: Boolean, scope: CoroutineScope) {
        val old = target
        val oldText = formatRolling(old, padding)
        val newText = formatRolling(value, newPadding)
        padding = newPadding
        if (old == value) return
        val direction = if (value >= old) 1 else -1
        target = value
        if (oldText.length != newText.length || (old < 0) != (value < 0)) {
            wholeFrom = old
            wholeDirection = direction
            places.forEach { place -> scope.launch { place.progress.snapTo(1f) } }
            scope.launch {
                wholeProgress.snapTo(0f)
                wholeProgress.animateTo(1f, tween(RollingNumberDefaults.RollDurationMillis, easing = ROLL_EASING))
            }
            return
        }
        val oldDigits = abs(old.toLong())
        val newDigits = abs(value.toLong())
        var divisor = 1L
        for (place in 0 until newText.trimStart('-').length) {
            val from = ((oldDigits / divisor) % 10).toInt()
            val to = ((newDigits / divisor) % 10).toInt()
            divisor *= 10
            if (from == to || (place == 0 && !rollOnesDigit)) continue
            val roll = places[place]
            roll.from = from
            roll.direction = direction
            scope.launch {
                roll.progress.snapTo(0f)
                roll.progress.animateTo(1f, tween(RollingNumberDefaults.RollDurationMillis, easing = ROLL_EASING))
            }
        }
    }
}

private fun formatRolling(value: Int, padding: Int): String {
    val digits = abs(value.toLong()).toString().padStart(padding, '0')
    return if (value < 0) "-$digits" else digits
}

/** Digit places of an [Int] (`Int.MIN_VALUE` has 10 digits). */
private const val MAX_PLACES = 10

/** `c9.a.k`, the roll curve of `RollingNumView` and `PaletteRollingNumView`. */
private val ROLL_EASING = CubicBezierEasing(0.6f, 0f, 0.3f, 1f)

/**
 * Default values of [RollingNumber], from the camera's `RollingNumView` / `PaletteRollingNumView`
 * and the dimensions of their two uses.
 */
object RollingNumberDefaults {
    /** The roll duration of one digit place (`RollingNumView.j`). */
    val RollDurationMillis: Int = 200

    /** The tilt of a digit at either end of its roll (`Camera.rotateX(45)`). */
    val RollAngle: Float = 45f

    /** The burst-shot counter digit size (`burst_num_child_text_view_size`). */
    val BurstTextSize: Dp = 14.dp

    /** The burst-shot counter roll distance (`burst_num_view_rolling_transY`). */
    val BurstRollDistance: Dp = 13.dp

    /** The burst-shot counter badge (`burst.xml`: a 32dp circle of 50% black). */
    val BurstBadgeSize: Dp = 32.dp

    /** The palette value digit size (`palette_value_panel_text_size`). */
    val PaletteTextSize: Dp = 10.dp

    /** The palette value roll distance (`palette_value_rolling_transY`). */
    val PaletteRollDistance: Dp = 12.dp
}
