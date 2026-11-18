// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.anim.COUIEaseEasing
import io.github.suqi8.coui.kmp.anim.COUIMoveEaseEasing
import io.github.suqi8.coui.kmp.utils.CouiHapticEffect
import io.github.suqi8.coui.kmp.utils.rememberCouiHaptic
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

/**
 * The level (horizon) indicator of the camera viewfinder, the port of the camera's
 * `GradienterAssistView` that `AssistViewPresenter` lays over the preview when the "Level" setting
 * (`pref_assist_gradienter`) or the center indicator (`pref_assist_center_indicator`) is on.
 *
 * **Upright shooting.** A line a third of the frame's narrow side long sits in the middle, flanked by
 * two fixed 16dp ticks 4dp away from its ends. The line turns against [rollDegrees] so it stays
 * parallel to the real horizon while the ticks stay square with the frame. Within 1.2 degrees of
 * level the line snaps straight, grows over the ticks (which disappear), turns the accent color and
 * plays a haptic tick (at most once a second); after 725ms of staying level the indicator fades out
 * in 375ms and comes back at once when the device tilts again. It is only shown while the device is
 * nearly level: fully up to 5 degrees of roll and fading out by 11 degrees, and fading out while the
 * lens turns 30 to 40 degrees away from the horizon ([pitchDegrees]). Past 45 degrees of roll it
 * switches to the landscape orientation (ticks and line turned by 90 degrees).
 *
 * **Top-down shooting** (within 15 degrees of lying flat on both axes, left again past 20 degrees):
 * two 32dp crosses replace the line, a white target cross fixed in the middle and an accent cross
 * that moves toward the lower edge, up to 40dp away at 20 degrees of tilt. Within 0.4 degrees on both
 * axes, once the moving cross has settled within 1dp of the middle, only the accent cross remains,
 * centered. Shown up to 5 degrees from flat and fading out by 11 degrees.
 *
 * Every appearance waits 400ms (none right after the device was level) and fades in over 200ms; the
 * indicator fades out in 250ms when it leaves its range or switches between upright and top-down.
 * Each new reading glides there over 60ms. The bars and crosses carry a thin dark outline so they
 * read on any preview.
 *
 * The indicator draws over its own bounds; give it the viewfinder area, for example with
 * `Modifier.matchParentSize()`. Pass the same [state] to a [CameraGridOverlay] over the same area to
 * let the level line cut its gap into the grid lines, as the camera does.
 *
 * @param rollDegrees The tilt of the device's horizontal axis against the horizon, in degrees,
 *   positive when the device is turned clockwise (its right edge lower). Upright this is the rotation
 *   around the lens axis; lying flat it is the left/right tilt. Readings near +/-90 hold the device
 *   in landscape.
 * @param pitchDegrees The forward tilt of the device around its horizontal axis, in degrees: 0 with
 *   the lens facing the horizon, 90 lying flat screen-up (lens facing down), and past 90 tipped
 *   beyond flat with the top edge lower. Lying flat, the distance from 90 (or -90) is the forward/back
 *   tilt.
 * @param modifier The modifier to be applied to the indicator; it must give it a size.
 * @param state The state shared with a [CameraGridOverlay], see [rememberCameraLevelState].
 * @param showLevel Whether to show the level line and the top-down crosses (the camera's "Level").
 * @param showCenterMark Whether to show the small center ring (the camera's center indicator); it
 *   is always visible while on.
 * @param hapticFeedbackEnabled Whether reaching level plays a haptic tick.
 * @param colors The colors of the indicator, see [CameraLevelIndicatorDefaults.cameraLevelIndicatorColors].
 */
@Composable
fun CameraLevelIndicator(
    rollDegrees: Float,
    pitchDegrees: Float,
    modifier: Modifier = Modifier,
    state: CameraLevelState = rememberCameraLevelState(),
    showLevel: Boolean = true,
    showCenterMark: Boolean = false,
    hapticFeedbackEnabled: Boolean = true,
    colors: CameraLevelIndicatorColors = CameraLevelIndicatorDefaults.cameraLevelIndicatorColors(),
) {
    val haptic = rememberCouiHaptic()
    val currentHaptic by rememberUpdatedState(haptic)
    val currentHapticEnabled by rememberUpdatedState(hapticFeedbackEnabled)

    LaunchedEffect(state, rollDegrees, pitchDegrees) {
        state.update(levelWrapDegrees(rollDegrees), levelWrapDegrees(pitchDegrees))
    }
    DisposableEffect(state, showLevel) {
        state.levelActive = showLevel
        onDispose { state.levelActive = false }
    }
    LaunchedEffect(state, showLevel) {
        if (!showLevel) {
            state.visibility.snapTo(0f)
            return@LaunchedEffect
        }
        state.runVisibility {
            if (currentHapticEnabled) currentHaptic(CouiHapticEffect.Switch)
        }
    }
    LaunchedEffect(state) {
        snapshotFlow { !state.isTopDown && state.isLevel }.collectLatest { level ->
            state.gapProgress.animateTo(
                targetValue = if (level) 1f else 0f,
                animationSpec = tween(durationMillis = LEVEL_GAP_DURATION, easing = COUIMoveEaseEasing),
            )
        }
    }

    Spacer(
        modifier = modifier.drawWithCache {
            val cross = levelCrossPaths(CameraLevelIndicatorDefaults.CrossSize.toPx())
            onDrawBehind {
                if (showLevel) drawLevelIndicator(state, colors, cross)
                if (showCenterMark) drawLevelCenterMark(colors)
            }
        },
    )
}

/**
 * Creates and remembers a [CameraLevelState].
 */
@Composable
fun rememberCameraLevelState(): CameraLevelState = remember { CameraLevelState() }

/**
 * The state of a [CameraLevelIndicator]: the smoothed attitude, the fade and whether the device is
 * level. Share it with a [CameraGridOverlay] so the grid leaves a gap where the level line crosses
 * it (`GradienterAssistView` handing its gap path to `GuideLineView`).
 */
@Stable
class CameraLevelState internal constructor() {
    internal val roll = Animatable(0f)
    internal val pitch = Animatable(0f)
    internal val visibility = Animatable(0f)
    internal val gapProgress = Animatable(0f)
    internal var rollTarget by mutableFloatStateOf(0f)
    internal var pitchTarget by mutableFloatStateOf(0f)
    internal var quadrant by mutableIntStateOf(0)
    internal var topDownMode by mutableStateOf(false)
    internal var levelActive by mutableStateOf(false)
    private var initialized = false

    /** Whether the indicator is in its top-down mode (two crosses instead of the level line). */
    val isTopDown: Boolean get() = topDownMode

    /**
     * Whether the device is level: within 1.2 degrees upright, or within 0.4 degrees on both axes in
     * the top-down mode once the moving cross has settled.
     */
    val isLevel: Boolean by derivedStateOf {
        if (topDownMode) {
            val x = rollTarget
            val y = levelFlatTilt(pitchTarget)
            abs(x) < LEVEL_TOP_DOWN_THRESHOLD &&
                abs(y) < LEVEL_TOP_DOWN_THRESHOLD &&
                abs(roll.value - x) < LEVEL_CROSS_SETTLE &&
                abs(levelFlatTilt(pitch.value) - y) < LEVEL_CROSS_SETTLE
        } else {
            abs(rollTarget - QUARTER_TURN * quadrant) < LEVEL_UPRIGHT_THRESHOLD
        }
    }

    /** Whether the latest reading is inside the range the indicator is shown in. */
    private val inRange: Boolean
        get() = if (topDownMode) {
            hypot(rollTarget, levelFlatTilt(pitchTarget)) <= LEVEL_FADE_ZERO
        } else {
            !isLevel &&
                abs(rollTarget - QUARTER_TURN * quadrant) <= LEVEL_FADE_ZERO &&
                levelUprightTilt(pitchTarget) <= LEVEL_PITCH_FADE_ZERO
        }

    /**
     * The drawing alpha (`getDrawingAlphaRatio`): the attitude fade of the smoothed reading, capped
     * by the show/hide animation.
     */
    internal fun levelAlpha(): Float {
        val attitude = if (topDownMode) {
            levelRamp(hypot(roll.value, levelFlatTilt(pitch.value)), LEVEL_FADE_FULL, LEVEL_FADE_ZERO)
        } else {
            min(
                levelRamp(abs(roll.value - QUARTER_TURN * quadrant), LEVEL_FADE_FULL, LEVEL_FADE_ZERO),
                levelRamp(levelUprightTilt(pitch.value), LEVEL_PITCH_FADE_FULL, LEVEL_PITCH_FADE_ZERO),
            )
        }
        return min(visibility.value, attitude)
    }

    internal suspend fun update(newRoll: Float, newPitch: Float) {
        rollTarget = newRoll
        pitchTarget = newPitch
        val limit = if (topDownMode) LEVEL_TOP_DOWN_EXIT else LEVEL_TOP_DOWN_ENTER
        topDownMode = abs(newRoll) < limit && abs(levelFlatTilt(newPitch)) < limit
        if (abs(newRoll - QUARTER_TURN * quadrant) > LEVEL_QUADRANT_SWITCH) {
            quadrant = (newRoll / QUARTER_TURN).roundToInt()
        }
        if (!initialized) {
            initialized = true
            roll.snapTo(newRoll)
            pitch.snapTo(newPitch)
            return
        }
        coroutineScope {
            launch { roll.animateTo(newRoll, tween(LEVEL_SMOOTHING_DURATION, easing = LinearEasing)) }
            launch { pitch.animateTo(newPitch, tween(LEVEL_SMOOTHING_DURATION, easing = LinearEasing)) }
        }
    }

    /** The show/hide state machine of `GradienterAssistView.c` (states 0..5), driven by [isLevel] and [inRange]. */
    internal suspend fun runVisibility(onLevel: () -> Unit) {
        var shown = false
        var pending = true
        var wasLevel = false
        var lastTopDown = topDownMode
        var lastHaptic: TimeSource.Monotonic.ValueTimeMark? = null
        snapshotFlow { LevelPhase(topDownMode, isLevel, inRange) }.collectLatest { phase ->
            val previousLevel = wasLevel
            wasLevel = phase.level
            if (phase.topDown != lastTopDown) {
                lastTopDown = phase.topDown
                if (shown) {
                    shown = false
                    pending = false
                    fadeOut(LEVEL_SWITCH_FADE_OUT)
                }
            }
            when {
                !phase.topDown && phase.level -> {
                    if (!shown && pending) {
                        delay(LEVEL_SHOW_DELAY)
                        pending = false
                        shown = true
                    }
                    if (shown) {
                        val mark = lastHaptic
                        if (mark == null || mark.elapsedNow() >= LEVEL_HAPTIC_INTERVAL.milliseconds) {
                            lastHaptic = TimeSource.Monotonic.markNow()
                            onLevel()
                        }
                        fadeIn()
                        delay(LEVEL_HOLD_DURATION)
                        shown = false
                        fadeOut(LEVEL_HOLD_FADE_OUT)
                    }
                }

                phase.inRange -> {
                    if (!shown) {
                        pending = true
                        if (!previousLevel) delay(LEVEL_SHOW_DELAY)
                        pending = false
                        shown = true
                    }
                    fadeIn()
                }

                else -> {
                    pending = false
                    if (shown) {
                        shown = false
                        fadeOut(LEVEL_RANGE_FADE_OUT)
                    }
                }
            }
        }
    }

    private suspend fun fadeIn() {
        visibility.animateTo(1f, tween(LEVEL_FADE_IN, easing = COUIEaseEasing))
    }

    private suspend fun fadeOut(duration: Int) {
        visibility.snapTo(levelAlpha())
        visibility.animateTo(0f, tween(duration, easing = COUIEaseEasing))
    }

    /**
     * The gap the level line cuts into the grid lines (`GradienterAssistView.o`): a band
     * [CameraLevelIndicatorDefaults.GridGapHeight] high (scaled by the alpha) along the level line, widened
     * past the ticks while level. Null while nothing should be cut.
     */
    internal fun gridCutout(size: Size, density: Density, cutWhileTilted: Boolean): Path? {
        if (!levelActive || topDownMode) return null
        val alpha = levelAlpha()
        if (alpha <= 0f) return null
        val level = isLevel
        if (!level && !cutWhileTilted) return null
        return with(density) {
            val unit = min(size.width, size.height) / 3f
            val margin = CameraLevelIndicatorDefaults.TickMargin.toPx()
            val halfHeight = CameraLevelIndicatorDefaults.GridGapHeight.roundToPx() * alpha / 2f
            val center = Offset((size.width / 2f).toInt().toFloat(), (size.height / 2f).toInt().toFloat())
            val angle = if (level) -QUARTER_TURN * quadrant else -roll.value
            val path = Path()
            val lineHalf = (unit - margin).toInt() / 2f
            path.addLevelRotatedRect(
                Rect(center.x - lineHalf, center.y - halfHeight, center.x + lineHalf, center.y + halfHeight),
                angle,
                center,
            )
            val levelHalfHeight = halfHeight * gapProgress.value
            if (levelHalfHeight > 0f) {
                val levelHalf = (unit + 2f * margin).toInt() / 2f
                path.addLevelRotatedRect(
                    Rect(center.x - levelHalf, center.y - levelHalfHeight, center.x + levelHalf, center.y + levelHalfHeight),
                    angle,
                    center,
                )
            }
            path
        }
    }
}

/**
 * Default values of [CameraLevelIndicator], from the camera's `gradienter_*` dimensions.
 */
object CameraLevelIndicatorDefaults {
    /** The thickness of the level line and the ticks (`gradienter_level_line_width`, `gradienter_base_line_width`). */
    val LineThickness = 1.32.dp

    /** The length of each fixed tick (`gradienter_base_line_length`). */
    val TickLength = 16.dp

    /** The gap between the level line and each tick (`gradienter_base_line_level_line_margin`). */
    val TickMargin = 4.dp

    /** The dark outline inside the bars (`outer_stroke_width`). */
    val OutlineWidth = 0.33.dp

    /** The height of the gap the level line cuts into the grid lines (`gradienter_line_gap`). */
    val GridGapHeight = 4.33.dp

    /** The size of the top-down crosses (`ic_level_cross`). */
    val CrossSize = 32.dp

    /** How far the moving cross travels at 20 degrees of tilt (`gradienter_cross_max_offset`). */
    val CrossMaxOffset = 40.dp

    /** How close the moving cross must be to the middle to count as level (`gradienter_adjusted_threshold_in_distance`). */
    val CrossSettleDistance = 1.dp

    /** The outer diameter of the center ring (`gradienter_center_circle_diameter`). */
    val CenterMarkDiameter = 6.dp

    /** The diameter of the translucent center disc (`gradienter_center_circle_inner_diameter`). */
    val CenterMarkInnerDiameter = 4.66.dp

    /** The width of the center ring (`gradienter_center_circle_ring_width`). */
    val CenterMarkRingWidth = 0.67.dp

    /** The alpha of the center disc (the ring paint's alpha 51). */
    val CenterMarkFillAlpha = 0.2f

    /**
     * The fixed light-on-dark palette of the level: white line, ticks and target cross
     * (`gradienter_adjusting_color`, `gradienter_level_cross_adjusted_color`), the camera accent once
     * level (`couiColorPrimary`, `color_primary_light_hasselblad` #FB822A), the 30% black outline
     * (`outer_stroke_color` #4D000000) and a white center mark.
     */
    @Composable
    fun cameraLevelIndicatorColors(
        lineColor: Color = Color.White,
        levelColor: Color = Color(0xFFFB822A),
        targetCrossColor: Color = Color.White,
        outlineColor: Color = Color(0x4D000000),
        centerMarkColor: Color = Color.White,
    ): CameraLevelIndicatorColors = remember(lineColor, levelColor, targetCrossColor, outlineColor, centerMarkColor) {
        CameraLevelIndicatorColors(
            lineColor = lineColor,
            levelColor = levelColor,
            targetCrossColor = targetCrossColor,
            outlineColor = outlineColor,
            centerMarkColor = centerMarkColor,
        )
    }
}

/**
 * The colors of a [CameraLevelIndicator].
 *
 * @param lineColor The level line and the ticks while tilted.
 * @param levelColor The level line once level, and the moving top-down cross.
 * @param targetCrossColor The fixed top-down target cross.
 * @param outlineColor The thin outline around the bars and the crosses.
 * @param centerMarkColor The center ring and disc.
 */
@Immutable
data class CameraLevelIndicatorColors(
    val lineColor: Color,
    val levelColor: Color,
    val targetCrossColor: Color,
    val outlineColor: Color,
    val centerMarkColor: Color,
)

/** One reading of the show/hide inputs. */
private data class LevelPhase(val topDown: Boolean, val level: Boolean, val inRange: Boolean)

/** The two paths of `ic_level_cross`: the 1dp cross and its 0.33dp outline ring, scaled to [size]. */
private class LevelCrossPaths(val fill: Path, val outline: Path, val size: Float)

private fun levelCrossPaths(size: Float): LevelCrossPaths {
    val s = size / LEVEL_CROSS_VIEWPORT
    fun crossOf(start: Float, end: Float, near: Float, far: Float): Path = Path().apply {
        addRect(Rect(start * s, near * s, end * s, far * s))
        addRect(Rect(near * s, start * s, far * s, end * s))
    }
    // M16.5,15.5V1H15.5V15.5H1V16.5H15.5V31H16.5V16.5H31V15.5H16.5Z
    val fill = crossOf(start = 1f, end = 31f, near = 15.5f, far = 16.5f)
    // The same cross grown to 0.67..31.33 and 15.17..16.83, minus the cross itself.
    val outer = crossOf(start = 0.67f, end = 31.33f, near = 15.17f, far = 16.83f)
    return LevelCrossPaths(fill = fill, outline = Path.combine(PathOperation.Difference, outer, fill), size = size)
}

/** `GradienterAssistView.onDraw` for the level line, the ticks and the top-down crosses. */
private fun DrawScope.drawLevelIndicator(state: CameraLevelState, colors: CameraLevelIndicatorColors, cross: LevelCrossPaths) {
    val alpha = state.levelAlpha()
    if (alpha <= 0f) return
    val center = Offset((size.width / 2f).toInt().toFloat(), (size.height / 2f).toInt().toFloat())
    if (state.isTopDown) {
        if (state.isLevel) {
            drawLevelCross(cross, center, colors.levelColor, colors.outlineColor, alpha)
        } else {
            val reach = CameraLevelIndicatorDefaults.CrossMaxOffset.toPx().toInt()
            val dx = state.roll.value.coerceIn(-LEVEL_CROSS_MAX_TILT, LEVEL_CROSS_MAX_TILT) / LEVEL_CROSS_MAX_TILT * reach
            val dy = levelFlatTilt(state.pitch.value).coerceIn(-LEVEL_CROSS_MAX_TILT, LEVEL_CROSS_MAX_TILT) / LEVEL_CROSS_MAX_TILT * reach
            drawLevelCross(cross, Offset(center.x + dx, center.y - dy), colors.levelColor, colors.outlineColor, alpha)
            drawLevelCross(cross, center, colors.targetCrossColor, colors.outlineColor, alpha)
        }
        return
    }
    val unit = min(size.width, size.height) / 3f
    val margin = CameraLevelIndicatorDefaults.TickMargin.toPx()
    val tick = CameraLevelIndicatorDefaults.TickLength.toPx()
    val thickness = CameraLevelIndicatorDefaults.LineThickness.toPx().roundToInt().coerceAtLeast(1).toFloat()
    val outline = CameraLevelIndicatorDefaults.OutlineWidth.toPx().roundToInt().toFloat()
    val level = state.isLevel
    val baseAngle = -QUARTER_TURN * state.quadrant
    val top = center.y - thickness / 2f
    if (!level) {
        val half = (unit - margin).toInt() / 2f
        rotate(baseAngle, center) {
            drawLevelBar(center.x - half - margin - tick, top, tick, thickness, colors.lineColor, colors.outlineColor, outline, alpha)
            drawLevelBar(center.x + half + margin, top, tick, thickness, colors.lineColor, colors.outlineColor, outline, alpha)
        }
    }
    val length = if (level) (unit + margin + 2f * tick).toInt().toFloat() else (unit - margin).toInt().toFloat()
    rotate(if (level) baseAngle else -state.roll.value, center) {
        drawLevelBar(
            left = center.x - length / 2f,
            top = top,
            width = length,
            height = thickness,
            color = if (level) colors.levelColor else colors.lineColor,
            outlineColor = colors.outlineColor,
            outline = outline,
            alpha = alpha,
        )
    }
}

/** A bar filled with [color] and edged inside by [outline] px of [outlineColor] (the camera's three-pass bar). */
private fun DrawScope.drawLevelBar(
    left: Float,
    top: Float,
    width: Float,
    height: Float,
    color: Color,
    outlineColor: Color,
    outline: Float,
    alpha: Float,
) {
    val innerWidth = width - 2f * outline
    val innerHeight = height - 2f * outline
    if (outline <= 0f || innerWidth <= 0f || innerHeight <= 0f) {
        drawRect(color, Offset(left, top), Size(width, height), alpha = alpha)
        return
    }
    val edge = outlineColor.copy(alpha = outlineColor.alpha * alpha)
    drawRect(edge, Offset(left, top), Size(width, outline))
    drawRect(edge, Offset(left, top + height - outline), Size(width, outline))
    drawRect(edge, Offset(left, top + outline), Size(outline, innerHeight))
    drawRect(edge, Offset(left + width - outline, top + outline), Size(outline, innerHeight))
    drawRect(color, Offset(left + outline, top + outline), Size(innerWidth, innerHeight), alpha = alpha)
}

private fun DrawScope.drawLevelCross(cross: LevelCrossPaths, center: Offset, color: Color, outlineColor: Color, alpha: Float) {
    translate(center.x - cross.size / 2f, center.y - cross.size / 2f) {
        drawPath(cross.outline, outlineColor, alpha = alpha)
        drawPath(cross.fill, color, alpha = alpha)
    }
}

/** `GradienterAssistView.d`: a 20% white disc inside a white ring. */
private fun DrawScope.drawLevelCenterMark(colors: CameraLevelIndicatorColors) {
    val center = Offset((size.width / 2f).toInt().toFloat(), (size.height / 2f).toInt().toFloat())
    val ring = CameraLevelIndicatorDefaults.CenterMarkRingWidth.toPx()
    drawCircle(
        color = colors.centerMarkColor,
        radius = CameraLevelIndicatorDefaults.CenterMarkInnerDiameter.toPx() / 2f,
        center = center,
        alpha = CameraLevelIndicatorDefaults.CenterMarkFillAlpha,
    )
    drawCircle(
        color = colors.centerMarkColor,
        radius = CameraLevelIndicatorDefaults.CenterMarkDiameter.toPx() / 2f - ring / 2f,
        center = center,
        style = Stroke(width = ring),
    )
}

/** Adds [rect] turned by [degrees] (clockwise, like `DrawScope.rotate`) around [pivot] as a polygon. */
private fun Path.addLevelRotatedRect(rect: Rect, degrees: Float, pivot: Offset) {
    val radians = degrees * LEVEL_DEGREES_TO_RADIANS
    val c = cos(radians)
    val s = sin(radians)
    fun map(x: Float, y: Float): Offset {
        val dx = x - pivot.x
        val dy = y - pivot.y
        return Offset(pivot.x + dx * c - dy * s, pivot.y + dx * s + dy * c)
    }
    val p0 = map(rect.left, rect.top)
    val p1 = map(rect.right, rect.top)
    val p2 = map(rect.right, rect.bottom)
    val p3 = map(rect.left, rect.bottom)
    moveTo(p0.x, p0.y)
    lineTo(p1.x, p1.y)
    lineTo(p2.x, p2.y)
    lineTo(p3.x, p3.y)
    close()
}

private fun levelWrapDegrees(degrees: Float): Float {
    var value = degrees % 360f
    if (value > 180f) value -= 360f
    if (value <= -180f) value += 360f
    return value
}

/** The forward/back tilt from lying flat (screen up at 90, screen down at -90). */
private fun levelFlatTilt(pitch: Float): Float = if (pitch >= 0f) pitch - 90f else -90f - pitch

/** How far the lens turns away from the horizon, folded into 0..90. */
private fun levelUprightTilt(pitch: Float): Float {
    val value = abs(pitch)
    return if (value > 90f) 180f - value else value
}

/** 1 up to [full], 0 from [zero], linear in between (`GradienterAssistView.g`). */
private fun levelRamp(value: Float, full: Float, zero: Float): Float = when {
    value <= full -> 1f
    value >= zero -> 0f
    else -> (zero - value) / (zero - full)
}

private const val QUARTER_TURN = 90f
private const val LEVEL_DEGREES_TO_RADIANS = 0.017453292f

/** The `ic_level_cross` viewport. */
private const val LEVEL_CROSS_VIEWPORT = 32f

/** Upright level: `1.5707963 - |angle| < 1.2 && |roll| < 1.2` in `GradienterAssistView.l`. */
private const val LEVEL_UPRIGHT_THRESHOLD = 1.2f

/** Top-down level: `j(0.4f)` in `GradienterAssistView.l`. */
private const val LEVEL_TOP_DOWN_THRESHOLD = 0.4f

/** The tilt at which the moving cross reaches [CameraLevelIndicatorDefaults.CrossMaxOffset] (`/ 20.0f * k`). */
private const val LEVEL_CROSS_MAX_TILT = 20f

/** [CameraLevelIndicatorDefaults.CrossSettleDistance] in degrees: 1dp of the 40dp per 20 degrees. */
private const val LEVEL_CROSS_SETTLE = 0.5f

/** The top-down mode is entered within 15 degrees of flat and left past 20 (`j(15.0f)` / `j(20.0f)`). */
private const val LEVEL_TOP_DOWN_ENTER = 15f
private const val LEVEL_TOP_DOWN_EXIT = 20f

/** The portrait/landscape switch with a small hysteresis around 45 degrees (`|f0| - |f1| > -5`). */
private const val LEVEL_QUADRANT_SWITCH = 47.5f

/** Roll fade: fully shown up to 5 degrees, gone by 11 (`GradienterAssistView.g`). */
private const val LEVEL_FADE_FULL = 5f
private const val LEVEL_FADE_ZERO = 11f

/** Pitch fade: fully shown up to 30 degrees, gone by 40 (`GradienterAssistView.g`). */
private const val LEVEL_PITCH_FADE_FULL = 30f
private const val LEVEL_PITCH_FADE_ZERO = 40f

/** Each reading glides over 60ms (`GradienterAssistView.u`, a linear 0..100 animator). */
private const val LEVEL_SMOOTHING_DURATION = 60

/** The delay before showing (`l1.g(400L)`). */
private const val LEVEL_SHOW_DELAY = 400L

/** The fade-in (`GradienterAssistView.r`, 200ms on `COUIEaseInterpolator`). */
private const val LEVEL_FADE_IN = 200

/** How long the level line stays after reaching level (`m1.g(725L)`). */
private const val LEVEL_HOLD_DURATION = 725L

/** The fade-out after holding level (`s(375L)`). */
private const val LEVEL_HOLD_FADE_OUT = 375

/** The fade-out when leaving the range (`s(250L)`). */
private const val LEVEL_RANGE_FADE_OUT = 250

/** The fade-out when switching between upright and top-down (`s(250L)`). */
private const val LEVEL_SWITCH_FADE_OUT = 250

/** The gap widening while level (`c1`, 150ms on `c9.a.e` (0.3, 0, 0.1, 1)). */
private const val LEVEL_GAP_DURATION = 150

/** The minimum time between two level haptics (`1000 < now - Q`). */
private const val LEVEL_HAPTIC_INTERVAL = 1000
