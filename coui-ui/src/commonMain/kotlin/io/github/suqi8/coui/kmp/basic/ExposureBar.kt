// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitHorizontalTouchSlopOrCancellation
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.suqi8.coui.kmp.effect.MaterialBlurBlendMode
import io.github.suqi8.coui.kmp.effect.MaterialBlurProvider
import io.github.suqi8.coui.kmp.effect.MaterialBlurSpec
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.Reset
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.utils.CouiHapticEffect
import io.github.suqi8.coui.kmp.utils.rememberCouiHaptic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * The exposure compensation (EV) panel of the camera, the port of the camera's global EV bar
 * (`FocusExposureUIManager` building a `BottomEffectContainer` around an `HSScaleBar` and a
 * `ResetImageView`).
 *
 * A dark capsule ([ExposureBarDefaults.Width] x [ExposureBarDefaults.Height], 32dp corners) holds a
 * horizontal [ScaleBar] inset by 64dp on both sides, one tick per entry of [values] and a major tick
 * every third entry, and a round reset button at its start. Dragging the ruler moves the ticks under
 * the fixed center indicator; the value over the indicator always shows the nearest entry. The reset
 * button dims to 30% while [selectedIndex] is already [defaultIndex] and, when tapped, spins once
 * and springs the ruler back to [defaultIndex].
 *
 * The capsule is the camera's material blur when [materialBlur] is given (80dp blur, glow overlay of
 * a 15% grey over a 60% mix of 30% grey), and otherwise the camera's non-blur fill: an opaque
 * near-black capsule with a hairline white border. Both the capsule and the reset button carry the
 * camera's edge light (`InnerShadowEffect`): a thin light rim just inside their edges. The panel
 * always uses its fixed light-on-dark palette, since it sits over the camera preview.
 *
 * @param selectedIndex The index of the selected entry of [values].
 * @param onSelectedIndexChange Called with the new index while the ruler is dragged, flung, settles,
 *   or is reset.
 * @param modifier The modifier to be applied to the panel.
 * @param enabled Whether the ruler and the reset button accept input.
 * @param values The labels of the ruler entries, from the lowest to the highest compensation.
 *   Defaults to the camera's -2.0..+2.0 EV in 1/3 steps.
 * @param defaultIndex The entry the reset button returns to; the middle entry (0.0) by default.
 * @param showResetButton Whether to show the reset button.
 * @param materialBlur Paints the camera material blur behind the capsule. Pass a provider recording
 *   the content under the panel (for example the camera preview); null keeps the opaque fill. The
 *   recorded content must not contain the panel itself.
 * @param colors The colors of the panel, see [ExposureBarDefaults.exposureBarColors].
 */
@Composable
fun ExposureBar(
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    values: List<String> = ExposureBarDefaults.Values,
    defaultIndex: Int = values.size / 2,
    showResetButton: Boolean = true,
    materialBlur: MaterialBlurProvider? = null,
    colors: ExposureBarColors = ExposureBarDefaults.exposureBarColors(),
) {
    val shape = remember { RoundedCornerShape(ExposureBarDefaults.CornerRadius) }
    val density = LocalDensity.current
    val blurSpec = remember(density, colors) {
        MaterialBlurSpec(
            blurRadiusPx = with(density) { ExposureBarDefaults.BlurRadius.toPx() },
            mixColor = colors.blurMixColor,
            blendColor = colors.blurBlendColor,
            blendMode = MaterialBlurBlendMode.GlowOverlay,
        )
    }
    val blurModifier = materialBlur?.materialBlur(blurSpec, shape)
    val background = blurModifier ?: Modifier
        .background(colors.containerColor, shape)
        .border(ExposureBarDefaults.BorderWidth, colors.borderColor, shape)
    Box(
        modifier = modifier
            .width(ExposureBarDefaults.Width)
            .height(ExposureBarDefaults.Height)
            .then(background)
            .cameraInnerShadow(CameraInnerShadows.Panel, ExposureBarDefaults.CornerRadius),
    ) {
        ScaleBar(
            labels = values,
            selectedIndex = selectedIndex,
            onSelectedIndexChange = onSelectedIndexChange,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = ExposureBarDefaults.ScaleBarHorizontalMargin),
            enabled = enabled,
            majorTickInterval = ExposureBarDefaults.MajorTickInterval,
            valueTextSize = ExposureBarDefaults.ValueTextSize,
            colors = colors.scaleBarColors,
        )
        if (showResetButton) {
            ExposureResetButton(
                resetEnabled = enabled && selectedIndex != defaultIndex,
                onReset = { onSelectedIndexChange(defaultIndex) },
                colors = colors,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = ExposureBarDefaults.ResetButtonMarginStart, top = ExposureBarDefaults.ResetButtonMarginTop),
            )
        }
    }
}

/**
 * The reset button (`ResetImageView`): a 32dp disc of 10% white with the reset arrow, at 30% alpha
 * while there is nothing to reset (`setResetEnable`, 200ms), spinning a full turn in 300ms when
 * tapped.
 */
@Composable
private fun ExposureResetButton(
    resetEnabled: Boolean,
    onReset: () -> Unit,
    colors: ExposureBarColors,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val rotation = remember { Animatable(0f) }
    val alpha by animateFloatAsState(
        targetValue = if (resetEnabled) 1f else ExposureBarDefaults.ResetDisabledAlpha,
        animationSpec = tween(durationMillis = 200),
    )
    val currentOnReset by rememberUpdatedState(onReset)
    Box(
        modifier = modifier
            .size(ExposureBarDefaults.ResetButtonSize)
            .graphicsLayer { this.alpha = alpha }
            .background(colors.resetButtonColor, CircleShape)
            .cameraInnerShadow(CameraInnerShadows.MenuIcon, ExposureBarDefaults.ResetButtonSize / 2)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = resetEnabled,
                role = Role.Button,
            ) {
                scope.launch {
                    rotation.snapTo(0f)
                    rotation.animateTo(360f, tween(durationMillis = 300, easing = AccelerateDecelerateEasing))
                }
                currentOnReset()
            },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = COUIIcons.Basic.Reset,
            contentDescription = null,
            modifier = Modifier
                .size(ExposureBarDefaults.ResetIconSize)
                .graphicsLayer { rotationZ = rotation.value },
            tint = colors.resetIconColor,
        )
    }
}

/**
 * A horizontal ruler picker, the port of the camera's `HSScaleBar` in its expanded state: one tick
 * per entry of [labels], [majorTickInterval] entries per major tick, a fixed indicator in the middle
 * and the label of the entry nearest to it drawn above.
 *
 * Dragging moves the ticks with the finger; the index reported while dragging flips once a tick
 * passes the indicator (within half a tick width). Past either end the ruler stretches on a rubber
 * band (5% of the finger travel, the ticks spreading by half of it) and springs back in 350ms when
 * released. A release faster than 500px/s keeps the ruler gliding (400ms, or 600ms for rulers of
 * more than 30 entries), after which it settles on the nearest entry. Ticks fade out over the outer
 * 49dp at both ends. When [selectedIndex] changes from outside, the ruler glides to it in 300ms.
 *
 * The ruler fills the width it is given and is [ScaleBarDefaults.Height] high by default; its ticks
 * are drawn relative to its vertical center.
 *
 * @param labels The labels of the entries, from start to end. Must not be empty.
 * @param selectedIndex The index of the selected entry.
 * @param onSelectedIndexChange Called with the new index while the ruler is dragged, flung or settles.
 * @param modifier The modifier to be applied to the ruler.
 * @param enabled Whether the ruler accepts drags.
 * @param showIndicator Whether to draw the center indicator.
 * @param majorTickInterval How many entries a major tick spans (`setCountBetweenBigScale`); the
 *   first entry is always major.
 * @param valueSuffix A suffix appended to the label drawn over the indicator.
 * @param valueTextSize The size of the label over the indicator, in dp like the camera's dimension
 *   (not scaled with the font scale).
 * @param colors The colors of the ruler, see [ScaleBarDefaults.scaleBarColors].
 */
@Composable
fun ScaleBar(
    labels: List<String>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showIndicator: Boolean = true,
    majorTickInterval: Int = ScaleBarDefaults.MajorTickInterval,
    valueSuffix: String = "",
    valueTextSize: Dp = ScaleBarDefaults.ValueTextSize,
    colors: ScaleBarColors = ScaleBarDefaults.scaleBarColors(),
) {
    require(labels.isNotEmpty()) { "ScaleBar needs at least one label" }
    val count = labels.size
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val haptic = rememberCouiHaptic()
    val scope = rememberCoroutineScope()
    val textMeasurer = rememberTextMeasurer()

    val tickWidthPx = with(density) { ScaleBarDefaults.TickWidth.toPx() }
    val tickGap = if (count > ScaleBarDefaults.DenseCount) ScaleBarDefaults.SmallTickGap else ScaleBarDefaults.MediumTickGap
    val intervalPx = with(density) { ScaleBarDefaults.TickWidth.toPx() + tickGap.toPx() }

    val state = remember { ScaleBarState(selectedIndex.coerceIn(0, count - 1)) }
    state.count = count
    state.interval = intervalPx
    state.tolerance = floor(tickWidthPx / 2f)

    val currentOnChange by rememberUpdatedState(onSelectedIndexChange)
    val currentHaptic by rememberUpdatedState(haptic)
    state.onIndexChange = { index, fromUser ->
        if (fromUser) currentHaptic(CouiHapticEffect.Strength)
        currentOnChange(index)
    }

    LaunchedEffect(selectedIndex, count, intervalPx) {
        val target = selectedIndex.coerceIn(0, count - 1)
        if (state.syncedCount != count || state.syncedInterval != intervalPx || !state.laidOut) {
            state.jumpTo(target)
        } else if (target != state.reportedIndex && !state.dragging) {
            state.animateTo(scope, target)
        }
    }

    val valueStyle = remember(valueTextSize, density, colors.valueColor) {
        TextStyle(
            color = colors.valueColor,
            fontSize = (valueTextSize.value / density.fontScale).sp,
            fontWeight = FontWeight.Medium,
            fontFeatureSettings = "tnum",
        )
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(ScaleBarDefaults.Height)
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
            .semantics {
                stateDescription = labels[state.nearestIndex()] + valueSuffix
                progressBarRangeInfo = ProgressBarRangeInfo(
                    current = state.nearestIndex().toFloat(),
                    range = 0f..(count - 1).coerceAtLeast(1).toFloat(),
                    steps = (count - 2).coerceAtLeast(0),
                )
                if (enabled) {
                    setProgress { target ->
                        val index = target.roundToInt().coerceIn(0, count - 1)
                        currentOnChange(index)
                        true
                    }
                }
            }
            .pointerInput(enabled, layoutDirection) {
                if (!enabled) return@pointerInput
                val sign = if (layoutDirection == LayoutDirection.Rtl) -1f else 1f
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    state.cancelAnimations()
                    state.dragging = true
                    val tracker = VelocityTracker()
                    tracker.addPosition(down.uptimeMillis, down.position)
                    // Like HSScaleBar, only a mostly horizontal move drags the ruler.
                    val start = awaitHorizontalTouchSlopOrCancellation(down.id) { change, over ->
                        change.consume()
                        tracker.addPosition(change.uptimeMillis, change.position)
                        state.move(sign * over, fromUser = true)
                    }
                    if (start == null) {
                        state.dragging = false
                        state.release(scope, 0f)
                        return@awaitEachGesture
                    }
                    horizontalDrag(start.id) { change ->
                        tracker.addPosition(change.uptimeMillis, change.position)
                        state.move(sign * change.positionChange().x, fromUser = true)
                        change.consume()
                    }
                    state.dragging = false
                    state.release(scope, sign * tracker.calculateVelocity().x)
                }
            },
    ) {
        state.laidOut = true
        val w = size.width
        val center = w / 2f
        val midY = size.height / 2f
        val rtl = layoutDirection == LayoutDirection.Rtl
        val halfTick = floor(tickWidthPx / 2f).coerceAtLeast(0.5f)
        val fade = ScaleBarDefaults.FadeWidth.toPx()
        val tickTop = midY + ScaleBarDefaults.TickTop.toPx()
        val tickBottom = midY + ScaleBarDefaults.TickBottom.toPx()
        val spread = state.overscroll * 0.5f

        for (i in 0 until count) {
            val offset = if (spread > 0f) (intervalPx + spread) * i else i * intervalPx + (count - 1 - i) * spread
            val ltrX = center + state.overscroll + state.scroll + offset
            val x = if (rtl) w - ltrX else ltrX
            if (x < -halfTick || x > w + halfTick) continue
            val edgeAlpha = when {
                x < fade -> x / fade
                x > w - fade -> (w - x) / fade
                else -> 1f
            }.coerceIn(0f, 1f)
            if (edgeAlpha <= 0f) continue
            val base = if (i % majorTickInterval == 0) colors.majorTickColor else colors.minorTickColor
            drawRect(
                color = base.copy(alpha = base.alpha * edgeAlpha),
                topLeft = Offset(x - halfTick, tickTop),
                size = Size(halfTick * 2f, tickBottom - tickTop),
            )
        }

        if (showIndicator) {
            val top = midY + ScaleBarDefaults.IndicatorTop.toPx()
            val bottom = midY + ScaleBarDefaults.IndicatorBottom.toPx()
            val topLeft = Offset(center - halfTick, top)
            val rectSize = Size(halfTick * 2f, bottom - top)
            drawRect(color = Color.Black, topLeft = topLeft, size = rectSize, blendMode = BlendMode.Clear)
            drawRect(color = colors.indicatorColor, topLeft = topLeft, size = rectSize)
        }

        val text = labels[state.nearestIndex()] + valueSuffix
        val layout = textMeasurer.measure(text, valueStyle)
        // The camera centers the digits' ink (getTextBounds) in a 20dp band whose top sits 25dp above
        // the middle; the ink of tabular digits is about 0.7 em high.
        val inkHeight = valueStyle.fontSize.toPx() * DIGIT_INK_RATIO
        val bandTop = midY + ScaleBarDefaults.ValueBandTop.toPx()
        val baseline = bandTop + (ScaleBarDefaults.ValueBandHeight.toPx() - inkHeight) / 2f + inkHeight
        drawText(
            textLayoutResult = layout,
            topLeft = Offset(center - layout.size.width / 2f, baseline - layout.firstBaseline),
        )
    }
}

/** The ink height of tabular digits relative to the font size, used to center the value. */
private const val DIGIT_INK_RATIO = 0.7f

/** `AccelerateDecelerateInterpolator`, the default `ValueAnimator` interpolator. */
private val AccelerateDecelerateEasing = Easing { t -> (cos((t + 1f) * kotlin.math.PI.toFloat()) / 2f) + 0.5f }

/**
 * The scroll model of [ScaleBar], ported from `HSScaleBar.z` (move), `I` (nearest), `q` (index) and
 * the fling runnable `HSScaleBar.d`. [scroll] is the position of the first tick relative to the
 * center, [overscroll] the rubber-band offset (`R`). Every animation runs in [job], which a new
 * touch cancels.
 */
@Stable
private class ScaleBarState(initialIndex: Int) {
    var scroll by mutableFloatStateOf(0f)
    var overscroll by mutableFloatStateOf(0f)
    var count = 1
    var syncedCount = -1
    var syncedInterval = 0f
    var interval = 1f
    var tolerance = 0f
    var dragging = false
    var laidOut = false
    var reportedIndex = initialIndex
    var onIndexChange: (Int, Boolean) -> Unit = { _, _ -> }

    private var direction = 1
    private var job: Job? = null

    private val maxOverscroll get() = interval * 30f
    private val minScroll get() = -(count - 1) * interval

    /** The entry nearest to the indicator, the one whose label is shown (`getNearestTick`). */
    fun nearestIndex(): Int = (-scroll / interval).roundToInt().coerceIn(0, count - 1)

    fun jumpTo(index: Int) {
        cancelAnimations()
        overscroll = 0f
        scroll = -index * interval
        reportedIndex = index
        syncedCount = count
        syncedInterval = interval
    }

    fun cancelAnimations() {
        job?.cancel()
        job = null
    }

    /** `HSScaleBar.q`: the tick at or before the indicator, or the next one within half a tick. */
    private fun indexAt(distance: Float): Int {
        val floorIndex = (distance / interval).toInt()
        val floorPos = floorIndex * interval
        val nextPos = (floorIndex + 1) * interval
        val index = when {
            distance >= max(floorPos - tolerance, 0f) && distance <= floorPos + tolerance -> floorIndex
            distance >= nextPos - tolerance && distance <= nextPos + tolerance -> floorIndex + 1
            else -> floorIndex
        }
        return index.coerceIn(0, count - 1)
    }

    private fun report(index: Int, fromUser: Boolean) {
        if (index != reportedIndex) {
            reportedIndex = index
            onIndexChange(index, fromUser)
        }
    }

    /** `HSScaleBar.z`: moves the ruler by [delta], stretching past either end. */
    fun move(delta: Float, fromUser: Boolean) {
        if (delta == 0f) return
        val dir = if (delta > 0f) 1 else -1
        if (direction != dir && overscroll != 0f) {
            overscroll = if (dir == 1) min(overscroll + delta, 0f) else max(overscroll + delta, 0f)
            return
        }
        direction = dir
        var s = scroll + delta
        if (s >= 0f) {
            overscroll = when {
                s > maxOverscroll -> maxOverscroll
                overscroll == 0f -> s * 0.05f
                else -> overscroll + delta * 0.05f
            }
            s = 0f
        } else if (s <= minScroll) {
            val beyond = s - minScroll
            overscroll = when {
                beyond < -maxOverscroll -> -maxOverscroll
                overscroll == 0f -> beyond * 0.05f
                else -> overscroll + delta * 0.05f
            }
            s = minScroll
        }
        scroll = s
        report(indexAt(-scroll), fromUser)
    }

    /** `HSScaleBar.F`: settles on the nearest entry. */
    private fun settle(fromUser: Boolean) {
        val index = nearestIndex()
        scroll = -index * interval
        report(index, fromUser)
    }

    /** Handles a release at [velocity] px/s (`HSScaleBar.onTouchEvent` ACTION_UP and `t`). */
    fun release(scope: CoroutineScope, velocity: Float) {
        if (overscroll != 0f) {
            launch(scope) { reboundAndSettle() }
            return
        }
        if (abs(velocity) < FLING_THRESHOLD) {
            settle(fromUser = true)
            return
        }
        // HSScaleBar.d: rulers of up to 30 entries glide for 400ms on (0, 0, 0.58, 1) over 30% of
        // the release speed, denser ones for 600ms on (0.3, 0.1, 0, 1) over 15%.
        val dense = count > ScaleBarDefaults.DenseCount
        val duration = if (dense) 600 else 400
        val easing = if (dense) CubicBezierEasing(0.3f, 0.1f, 0f, 1f) else CubicBezierEasing(0f, 0f, 0.58f, 1f)
        val distance = duration * velocity / 1000f * (if (dense) 0.15f else 0.3f)
        launch(scope) {
            var last = 0f
            var bounced = false
            runTween(duration, easing) { fraction ->
                move((fraction - last) * distance, fromUser = true)
                last = fraction
                bounced = overscroll != 0f
                !bounced
            }
            if (bounced) reboundAndSettle() else settle(fromUser = true)
        }
    }

    /** `HSScaleBar.O`: springs the rubber band back in 350ms, then settles. */
    private suspend fun reboundAndSettle() {
        val from = overscroll
        runTween(350, MoveEaseEasing) { fraction ->
            overscroll = from * (1f - fraction)
            true
        }
        overscroll = 0f
        settle(fromUser = true)
    }

    /** `HSScaleBar.N`: glides to [index] in 300ms. */
    fun animateTo(scope: CoroutineScope, index: Int) {
        val from = scroll
        val to = -index * interval
        reportedIndex = index
        launch(scope) {
            overscroll = 0f
            runTween(300, MoveEaseEasing) { fraction ->
                scroll = from + (to - from) * fraction
                true
            }
            scroll = to
        }
    }

    private fun launch(scope: CoroutineScope, block: suspend () -> Unit) {
        cancelAnimations()
        job = scope.launch { block() }
    }

    /** Runs [onFrame] with the eased fraction every frame for [duration] ms, until it returns false. */
    private suspend fun runTween(duration: Int, easing: Easing, onFrame: (Float) -> Boolean) {
        val start = withFrameMillis { it }
        while (true) {
            val elapsed = withFrameMillis { it } - start
            val fraction = (elapsed / duration.toFloat()).coerceAtMost(1f)
            if (!onFrame(easing.transform(fraction)) || fraction >= 1f) return
        }
    }

    private companion object {
        /** `HSScaleBar.t`: the release speed (px/s) from which the ruler keeps gliding. */
        const val FLING_THRESHOLD = 500f

        /** `COUIMoveEaseInterpolator` (0.3, 0, 0.1, 1). */
        val MoveEaseEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)
    }
}

/**
 * Default values of [ScaleBar], from the camera's `hs_professional_mode_scale_bar_*` dimensions.
 */
object ScaleBarDefaults {
    /** The ruler height (`global_ev_bar_container_height`). */
    val Height = 60.dp

    /** The tick width (`hs_professional_mode_scale_bar_scale_width`). */
    val TickWidth = 1.33.dp

    /** The gap between ticks of rulers up to [DenseCount] entries (`..._medium_scale_margin`). */
    val MediumTickGap = 14.dp

    /** The gap between ticks of rulers above [DenseCount] entries (`..._small_scale_margin`). */
    val SmallTickGap = 6.dp

    /** The entry count above which the ruler packs its ticks and glides longer (`HSScaleBar.S`). */
    val DenseCount = 30

    /** The width over which the ticks fade out at both ends (`..._scale_bar_side_width`). */
    val FadeWidth = 49.dp

    /** The tick top, relative to the vertical center (`..._start_y` after `HSScaleBar.o`). */
    val TickTop = 6.dp

    /** The tick bottom, relative to the vertical center (`..._end_y` after `HSScaleBar.o`). */
    val TickBottom = 18.dp

    /** The indicator top, relative to the vertical center (`..._center_start_y`). */
    val IndicatorTop = 0.dp

    /** The indicator bottom, relative to the vertical center (`..._center_end_y`). */
    val IndicatorBottom = 24.dp

    /** The top of the band the value is centered in, relative to the vertical center. */
    val ValueBandTop = (-25).dp

    /** The height of the band the value is centered in (`..._center_tick_text_height`). */
    val ValueBandHeight = 20.dp

    /** The size of the value over the indicator (`hs_professional_mode_center_tick_text_size`). */
    val ValueTextSize = 18.dp

    /** The default entries per major tick (`HSScaleBar.T0`). */
    val MajorTickInterval = 2

    /**
     * The fixed light-on-dark palette of the camera rulers: white major ticks
     * (`professional_mode_scale_bar_big_scale_color`), 30% white minor ticks, a white value and the
     * camera accent indicator (`couiColorPrimary`, `color_primary_light_hasselblad` #FB822A).
     */
    @Composable
    fun scaleBarColors(
        majorTickColor: Color = Color.White,
        minorTickColor: Color = Color(0x4DFFFFFF),
        indicatorColor: Color = Color(0xFFFB822A),
        valueColor: Color = Color.White,
    ): ScaleBarColors = remember(majorTickColor, minorTickColor, indicatorColor, valueColor) {
        ScaleBarColors(
            majorTickColor = majorTickColor,
            minorTickColor = minorTickColor,
            indicatorColor = indicatorColor,
            valueColor = valueColor,
        )
    }
}

/**
 * The colors of a [ScaleBar].
 *
 * @param majorTickColor The major ticks.
 * @param minorTickColor The minor ticks.
 * @param indicatorColor The center indicator.
 * @param valueColor The label over the indicator.
 */
@Immutable
data class ScaleBarColors(
    val majorTickColor: Color,
    val minorTickColor: Color,
    val indicatorColor: Color,
    val valueColor: Color,
)

/**
 * Default values of [ExposureBar], from the camera's `global_ev_bar_*` and bottom menu dimensions.
 */
object ExposureBarDefaults {
    /** The camera's global EV entries: -2.0..+2.0 EV in 1/3 steps (`a.b.f`). */
    val Values: List<String> = listOf(
        "-2.0", "-1.7", "-1.3", "-1.0", "-0.7", "-0.3", "0.0",
        "+0.3", "+0.7", "+1.0", "+1.3", "+1.7", "+2.0",
    )

    /** The wider -3.0..+3.0 EV entries the camera uses in its professional modes (`a.b.g`). */
    val WideValues: List<String> = listOf(
        "-3.0", "-2.7", "-2.3", "-2.0", "-1.7", "-1.3", "-1.0", "-0.7", "-0.3", "0.0",
        "+0.3", "+0.7", "+1.0", "+1.3", "+1.7", "+2.0", "+2.3", "+2.7", "+3.0",
    )

    /** The panel width (`bottom_effect_menu_width`). */
    val Width = 336.dp

    /** The panel height (`bottom_effect_menu_global_ev_height`). */
    val Height = 60.dp

    /** The capsule corner radius (`setting_corner_radius`). */
    val CornerRadius = 32.dp

    /** The material blur radius (`setting_blur_radius`). */
    val BlurRadius = 80.dp

    /** The border of the non-blur fill (`bottom_menu_container_border_width`). */
    val BorderWidth = 0.67.dp

    /** The ruler inset at both sides (`blur_seek_bar_narrow_margin_horizontal`). */
    val ScaleBarHorizontalMargin = 64.dp

    /** Entries per major tick (`setCountBetweenBigScale(3)`): a major tick every whole EV. */
    val MajorTickInterval = 3

    /** The size of the value over the indicator (`global_ev_scale_bar_center_text_size`). */
    val ValueTextSize = 14.dp

    /** The reset button size (`global_ev_bar_reset_button_size`). */
    val ResetButtonSize = 32.dp

    /** The reset icon size (icon_reset_common). */
    val ResetIconSize = 24.dp

    /** The reset button start margin (`global_ev_bar_reset_button_margin_left`). */
    val ResetButtonMarginStart = 14.dp

    /** The reset button top margin (`global_ev_bar_reset_button_margin_top`). */
    val ResetButtonMarginTop = 14.dp

    /** The reset button alpha while there is nothing to reset (`ResetImageView.setResetEnable`). */
    val ResetDisabledAlpha = 0.3f

    /**
     * The fixed camera palette: the non-blur fill `color_bottom_menu_bg_color_no_blur` #141414 with a
     * `color_white_with_10_percent_transparency` border, the material blur colors of
     * `BlurBackgroundImpl` (dark, progress 0), the reset disc `color_menu_grey_bg` #1AFFFFFF and the
     * ruler of [ScaleBarDefaults.scaleBarColors].
     */
    @Composable
    fun exposureBarColors(
        containerColor: Color = Color(0xFF141414),
        borderColor: Color = Color(0x1AFFFFFF),
        blurMixColor: Color = Color(red = 0.302f, green = 0.302f, blue = 0.302f, alpha = 0.6f),
        blurBlendColor: Color = Color(red = 0.15f, green = 0.15f, blue = 0.15f, alpha = 0.15f),
        resetButtonColor: Color = Color(0x1AFFFFFF),
        resetIconColor: Color = Color.White,
        scaleBarColors: ScaleBarColors = ScaleBarDefaults.scaleBarColors(),
    ): ExposureBarColors = remember(
        containerColor,
        borderColor,
        blurMixColor,
        blurBlendColor,
        resetButtonColor,
        resetIconColor,
        scaleBarColors,
    ) {
        ExposureBarColors(
            containerColor = containerColor,
            borderColor = borderColor,
            blurMixColor = blurMixColor,
            blurBlendColor = blurBlendColor,
            resetButtonColor = resetButtonColor,
            resetIconColor = resetIconColor,
            scaleBarColors = scaleBarColors,
        )
    }
}

/**
 * The colors of an [ExposureBar].
 *
 * @param containerColor The capsule fill without material blur.
 * @param borderColor The capsule border without material blur.
 * @param blurMixColor The material blur mix color.
 * @param blurBlendColor The material blur glow-overlay color.
 * @param resetButtonColor The reset button disc.
 * @param resetIconColor The reset arrow.
 * @param scaleBarColors The ruler colors.
 */
@Immutable
data class ExposureBarColors(
    val containerColor: Color,
    val borderColor: Color,
    val blurMixColor: Color,
    val blurBlendColor: Color,
    val resetButtonColor: Color,
    val resetIconColor: Color,
    val scaleBarColors: ScaleBarColors,
)
