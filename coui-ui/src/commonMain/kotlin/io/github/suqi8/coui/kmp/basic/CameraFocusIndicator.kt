// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * The tap-to-focus indicator of the camera with its vertical exposure slider, the port of the
 * camera's `FocusIndicatorRotateLayout` (the focus UI inflated from `focus_exposure_indicator`) and
 * its `ExposureSeekbarContainer`.
 *
 * The indicator draws over the area given by [modifier] (typically `Modifier.fillMaxSize()` over the
 * camera preview) and stays invisible until [CameraFocusIndicatorState.focusAt] places it, which a
 * tap does when [tapToFocus] is on. It then shows:
 *
 * - The focus box: four white 1.5dp corner brackets of 20dp in a 70dp square
 *   (`ic_indicator_focus`), each with a 30% black hairline outline. It appears scaling from 1.8x to
 *   1x in 165ms on (0.3, 0, 1, 1) while fading in over 310ms on (0.33, 0, 0.67, 1), then blinks once
 *   (alpha 1 -> 0.6 -> 1 in 300ms).
 * - The exposure slider 55dp beside it (on the end side, or on the start side when the box is
 *   within 82dp of the end edge): a sun (a 5dp disc with eight 1.57dp rays) on a 119.6dp vertical
 *   track. The sun's core fills up like a moon phase and its rays turn a full circle across the
 *   range. Dragging vertically anywhere over the indicator area moves it: 70% of the range per
 *   area height, upwards is brighter. Once dragged, the track line appears (split 11.7dp around the
 *   sun) together with the AE/AF lock button 88dp above the slider center (below it when the box is
 *   within 103dp of the top edge).
 * - When locked, the brackets pulse (scale 1 -> 1.3 -> 0.9 -> 1 in 500ms) and the brackets, the
 *   sun, the line and the padlock turn [CameraFocusIndicatorColors.lockedColor] in 300ms, both on
 *   (0.33, 0, 0.67, 1); the padlock closes its shackle in 500ms. Unlocking turns them white again at
 *   once.
 *
 * With [autoHide] the indicator follows the camera's `FocusStateManager` timers: 3s after a focus it
 * fades out (from 55% to 0 in 200ms on (0.3, 0, 0.1, 1)); after an exposure drag or a lock toggle it
 * dims to 55% after 1.5s (300ms on (0.58, 0, 0.69, 1)) and fades out 4s later. A locked indicator
 * stays, dimmed. Dragging again restores it at once.
 *
 * The indicator always uses its fixed light-on-dark camera palette.
 *
 * @param state The state holding the position, the exposure and the lock, see
 *   [rememberCameraFocusIndicatorState].
 * @param modifier The modifier to be applied to the indicator area. It must be given a size.
 * @param onFocus Called with the tap position when a tap places the indicator.
 * @param onExposureChange Called with the new exposure (0..1, 0.5 is no compensation) whenever it
 *   changes, including the reset to 0.5 on a new focus.
 * @param onLockedChange Called whenever the AE/AF lock changes.
 * @param enabled Whether the indicator reacts to taps and drags.
 * @param tapToFocus Whether a tap places the indicator ([CameraFocusIndicatorState.focusAt]).
 * @param autoHide Whether the indicator fades out on its own, see above.
 * @param showLockButton Whether the AE/AF lock button appears once the exposure is adjusted.
 * @param label An optional label under the focus box (`focus_indicator_text`, 12dp white with a
 *   soft shadow, at most two lines), for example a focus or lock hint.
 * @param colors The colors of the indicator, see [CameraFocusIndicatorDefaults.colors].
 */
@Composable
fun CameraFocusIndicator(
    state: CameraFocusIndicatorState,
    modifier: Modifier = Modifier,
    onFocus: (Offset) -> Unit = {},
    onExposureChange: (Float) -> Unit = {},
    onLockedChange: (Boolean) -> Unit = {},
    enabled: Boolean = true,
    tapToFocus: Boolean = true,
    autoHide: Boolean = true,
    showLockButton: Boolean = true,
    label: String? = null,
    colors: CameraFocusIndicatorColors = CameraFocusIndicatorDefaults.colors(),
) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val bracketPath = remember { PathParser().parsePathString(FOCUS_BRACKET_PATH).toPath() }
    val sunPath = remember { Path() }
    val padlockPath = remember { Path() }
    val padlockFrame = remember { FloatArray(FocusPadlockFrames[0].size) }
    val layerPaint = remember { Paint() }

    val currentOnFocus by rememberUpdatedState(onFocus)
    val currentOnExposureChange by rememberUpdatedState(onExposureChange)
    val currentOnLockedChange by rememberUpdatedState(onLockedChange)

    LaunchedEffect(state) {
        snapshotFlow { state.exposure }.drop(1).collect { currentOnExposureChange(it) }
    }
    LaunchedEffect(state) {
        snapshotFlow { state.isLocked }.drop(1).collect { currentOnLockedChange(it) }
    }
    LaunchedEffect(state, state.phaseKey) { state.runPhase() }
    LaunchedEffect(state, state.isLocked) { state.runLock() }
    LaunchedEffect(state, state.timerKey, autoHide) { state.runTimer(autoHide) }

    val labelStyle = remember(density, colors.labelColor, colors.labelShadowColor) {
        TextStyle(
            color = colors.labelColor,
            fontSize = (CameraFocusIndicatorDefaults.LabelTextSize.value / density.fontScale).sp,
            shadow = Shadow(
                color = colors.labelShadowColor,
                offset = Offset.Zero,
                blurRadius = with(density) { CameraFocusIndicatorDefaults.LabelShadowRadius.toPx() },
            ),
            textAlign = TextAlign.Center,
        )
    }

    Canvas(
        modifier = modifier
            .graphicsLayer { alpha = state.alpha.value }
            .semantics {
                if (state.isVisible) {
                    progressBarRangeInfo = ProgressBarRangeInfo(current = state.exposure, range = 0f..1f)
                    if (enabled) {
                        setProgress { target ->
                            state.adjustExposure(target - state.exposure)
                            true
                        }
                    }
                }
            }
            .pointerInput(state, enabled, tapToFocus, showLockButton) {
                if (!enabled) return@pointerInput
                val dragStart = FocusDragStartThreshold.toPx()
                val moveStep = FocusDragStepThreshold.toPx()
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val layout = state.layout(size.toSize(), this)
                    val lockHit = showLockButton && state.isVisible && state.isExposureActive && layout != null &&
                        layout.lockButton.contains(down.position)
                    var last = down.position
                    var dragging = false
                    var multiTouch = false
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.changes.count { it.pressed } > 1) multiTouch = true
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        if (!change.pressed) {
                            val travel = (change.position - down.position).getDistance()
                            if (!dragging && !multiTouch && travel <= viewConfiguration.touchSlop) {
                                if (lockHit) {
                                    state.lock(!state.isLocked)
                                } else if (tapToFocus) {
                                    state.focusAt(change.position)
                                    currentOnFocus(change.position)
                                }
                            }
                            break
                        }
                        if (multiTouch || !state.isVisible || size.height <= 0) continue
                        // FocusExposurePresenter: the exposure follows a mostly vertical move (|dy| >
                        // 0.58 |dx|) once the finger left the down point by 24px; upwards is brighter.
                        val dy = last.y - change.position.y
                        val dx = last.x - change.position.x
                        if (!dragging && abs(dy) > dragStart) dragging = true
                        if (dragging && abs(dy) > abs(dx) * VERTICAL_DRAG_RATIO && abs(dy) > moveStep) {
                            state.adjustExposure(dy / size.height * CameraFocusIndicatorDefaults.DragSensitivity)
                            last = change.position
                            change.consume()
                        }
                    }
                }
            },
    ) {
        val layout = state.layout(size, this) ?: return@Canvas
        val center = layout.center
        val tint = state.tint.value
        val color = lerp(colors.indicatorColor, colors.lockedColor, tint)
        withTransform({ scale(state.scale.value, state.scale.value, pivot = center) }) {
            drawFocusBrackets(
                path = bracketPath,
                center = center,
                pulse = focusPulseScale(state.lockPulse.value),
                alpha = focusBlinkAlpha(state.blink.value),
                color = color,
                outlineColor = colors.outlineColor,
                layerPaint = layerPaint,
            )
            if (!label.isNullOrEmpty()) {
                val result = textMeasurer.measure(
                    text = label,
                    style = labelStyle,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    constraints = Constraints(maxWidth = CameraFocusIndicatorDefaults.LabelMaxWidth.roundToPx()),
                )
                drawText(
                    textLayoutResult = result,
                    topLeft = Offset(
                        center.x - result.size.width / 2f,
                        center.y + CameraFocusIndicatorDefaults.IndicatorSize.toPx() / 2f +
                            CameraFocusIndicatorDefaults.LabelMarginTop.toPx(),
                    ),
                )
            }
            drawExposureSlider(
                path = sunPath,
                sliderX = layout.sliderX,
                centerY = center.y,
                exposure = state.exposure,
                showLine = state.isExposureActive,
                color = color,
                outlineColor = colors.outlineColor,
            )
            if (showLockButton && state.isExposureActive) {
                drawPadlock(
                    path = padlockPath,
                    frame = padlockFrame,
                    progress = state.padlock.value,
                    center = layout.lockButton.center,
                    color = color,
                    outlineColor = colors.outlineColor,
                )
            }
        }
    }
}

/**
 * Creates and remembers a [CameraFocusIndicatorState].
 */
@Composable
fun rememberCameraFocusIndicatorState(): CameraFocusIndicatorState = remember { CameraFocusIndicatorState() }

/**
 * The state of a [CameraFocusIndicator]: where it is, the exposure of its slider, the AE/AF lock
 * and its animations. Create it with [rememberCameraFocusIndicatorState]; the indicator must be
 * composed for the animations and the auto-hide timers to run.
 */
@Stable
class CameraFocusIndicatorState {
    /** The requested center of the focus box in the indicator's coordinates (px), or null before the first focus. */
    var focusPosition: Offset? by mutableStateOf(null)
        private set

    /** The exposure of the slider, 0 (darkest) to 1 (brightest); 0.5 is no compensation. */
    var exposure: Float by mutableFloatStateOf(CameraFocusIndicatorDefaults.DefaultExposure)
        private set

    /** Whether AE/AF is locked. */
    var isLocked: Boolean by mutableStateOf(false)
        private set

    /** Whether the exposure was adjusted since the last focus, which shows the track line and the lock button. */
    var isExposureActive: Boolean by mutableStateOf(false)
        private set

    /** Whether the indicator is shown (placed and not faded out). */
    val isVisible: Boolean get() = focusPosition != null && phase != CameraFocusPhase.Hidden

    internal var phase by mutableStateOf(CameraFocusPhase.Hidden)
    internal var phaseKey by mutableIntStateOf(0)
    internal var timerKey by mutableIntStateOf(0)
    private var timerAfterInteraction = false

    internal val alpha = Animatable(0f)
    internal val scale = Animatable(1f)
    internal val blink = Animatable(1f)
    internal val lockPulse = Animatable(1f)
    internal val tint = Animatable(0f)
    internal val padlock = Animatable(0f)

    /**
     * Places the indicator at [position] (its requested center, kept 45dp inside the edges) with the
     * appear animation, like a tap on the preview: the exposure returns to 0.5, the lock is released
     * and the track line and the lock button hide until the next drag.
     */
    fun focusAt(position: Offset) {
        focusPosition = position
        exposure = CameraFocusIndicatorDefaults.DefaultExposure
        isLocked = false
        isExposureActive = false
        command(CameraFocusPhase.Appear)
        restartTimer(afterInteraction = false)
    }

    /**
     * Moves the exposure by [delta] (in 0..1 units) like a drag: shows the track line and the lock
     * button, restores a dimmed indicator and restarts the dim timer. Ignored while hidden.
     */
    fun adjustExposure(delta: Float) {
        if (!isVisible) return
        exposure = (exposure + delta).coerceIn(0f, 1f)
        isExposureActive = true
        restore()
        restartTimer(afterInteraction = true)
    }

    /** Sets the exposure without the drag side effects, for example from another control. */
    fun updateExposure(value: Float) {
        exposure = value.coerceIn(0f, 1f)
    }

    /** Locks or releases AE/AF, like the lock button. Ignored before the first focus. */
    fun lock(locked: Boolean) {
        if (focusPosition == null || locked == isLocked) return
        isLocked = locked
        restore()
        restartTimer(afterInteraction = true)
    }

    /** Fades the indicator out now (55% to 0 in 200ms). */
    fun hide() {
        if (isVisible) command(CameraFocusPhase.FadeOut)
    }

    private fun restore() {
        if (phase == CameraFocusPhase.Dimmed || phase == CameraFocusPhase.FadeOut) command(CameraFocusPhase.Restore)
    }

    private fun command(target: CameraFocusPhase) {
        phase = target
        phaseKey++
    }

    private fun restartTimer(afterInteraction: Boolean) {
        timerAfterInteraction = afterInteraction
        timerKey++
    }

    /** The geometry for an indicator area of [size], or null before the first focus. */
    internal fun layout(size: Size, density: Density): CameraFocusLayout? {
        val position = focusPosition ?: return null
        with(density) {
            val margin = CameraFocusIndicatorDefaults.IndicatorSize.toPx() / 2f + CameraFocusIndicatorDefaults.EdgeMargin.toPx()
            val center = Offset(position.x.clampInside(margin, size.width), position.y.clampInside(margin, size.height))
            val startSide = center.x >= size.width - CameraFocusIndicatorDefaults.SliderFlipDistance.toPx()
            val sliderX = center.x + CameraFocusIndicatorDefaults.SliderOffset.toPx() * (if (startSide) -1f else 1f)
            val lockBelow = center.y <= CameraFocusIndicatorDefaults.LockButtonFlipDistance.toPx()
            val lockY = center.y + CameraFocusIndicatorDefaults.LockButtonOffset.toPx() * (if (lockBelow) 1f else -1f)
            val half = CameraFocusIndicatorDefaults.LockButtonTouchSize.toPx() / 2f
            return CameraFocusLayout(center, sliderX, Rect(sliderX - half, lockY - half, sliderX + half, lockY + half))
        }
    }

    internal suspend fun runPhase() {
        when (phase) {
            CameraFocusPhase.Appear -> coroutineScope {
                // FocusIndicatorRotateLayout.s: scale 1.8 -> 1 (165ms, COUI f) with alpha 0 -> 1
                // (310ms, COUI b); the end of the fade blinks the focus image (g, 300ms).
                alpha.snapTo(0f)
                scale.snapTo(CameraFocusIndicatorDefaults.AppearScale)
                blink.snapTo(1f)
                launch { scale.animateTo(1f, tween(165, easing = FocusAppearScaleEasing)) }
                alpha.animateTo(1f, tween(310, easing = FocusStandardEasing))
                phase = CameraFocusPhase.Shown
                blink.snapTo(0f)
                blink.animateTo(1f, tween(300, easing = FocusStandardEasing))
            }

            CameraFocusPhase.Restore -> {
                // The drag handler sets the layout visible and its alpha to 1 at once.
                scale.snapTo(1f)
                blink.snapTo(1f)
                alpha.snapTo(1f)
                phase = CameraFocusPhase.Shown
            }

            CameraFocusPhase.Dimmed -> {
                // FocusExposurePresenter.i: alpha to 0.55 in 300ms on c9.a.i (0.58, 0, 0.69, 1).
                alpha.animateTo(CameraFocusIndicatorDefaults.DimmedAlpha, tween(300, easing = FocusDimEasing))
            }

            CameraFocusPhase.FadeOut -> {
                // FocusExposurePresenter.k: alpha 0.55 -> 0 in 200ms on COUI e (0.3, 0, 0.1, 1).
                if (alpha.value > CameraFocusIndicatorDefaults.DimmedAlpha) alpha.snapTo(CameraFocusIndicatorDefaults.DimmedAlpha)
                alpha.animateTo(0f, tween(200, easing = FocusMoveEaseEasing))
                phase = CameraFocusPhase.Hidden
            }

            CameraFocusPhase.Shown, CameraFocusPhase.Hidden -> Unit
        }
    }

    internal suspend fun runLock() {
        if (isLocked) {
            coroutineScope {
                // FocusIndicatorRotateLayout.v(true): the focus image pulses (500ms) and turns the
                // accent (300ms), both on COUI b; the seekbar tints the same way
                // (ExposureSeekbarContainer.setLockedState) and the lock icon plays forward (30 frames).
                launch {
                    lockPulse.snapTo(0f)
                    lockPulse.animateTo(1f, tween(500, easing = FocusStandardEasing))
                }
                launch {
                    tint.snapTo(0f)
                    tint.animateTo(1f, tween(300, easing = FocusStandardEasing))
                }
                padlock.animateTo(1f, tween(((1f - padlock.value) * PADLOCK_DURATION).toInt(), easing = LinearEasing))
            }
        } else {
            lockPulse.snapTo(1f)
            tint.snapTo(0f)
            if (isExposureActive) {
                padlock.animateTo(0f, tween((padlock.value * PADLOCK_DURATION).toInt(), easing = LinearEasing))
            } else {
                padlock.snapTo(0f)
            }
        }
    }

    internal suspend fun runTimer(autoHide: Boolean) {
        if (!isVisible) return
        // FocusStateManager: MSG 6 (reset) 3s after a focus; after an interaction MSG 7 (dim) after
        // 1.5s, MSG 8 1s later, then MSG 6 3s after that. A locked AE/AF skips the reset.
        if (timerAfterInteraction) {
            delay(CameraFocusIndicatorDefaults.DimDelayMillis)
            if (phase == CameraFocusPhase.Shown) command(CameraFocusPhase.Dimmed)
            delay(CameraFocusIndicatorDefaults.DimmedHideDelayMillis)
        } else {
            delay(CameraFocusIndicatorDefaults.AutoHideDelayMillis)
        }
        if (autoHide && !isLocked) hide()
    }
}

/** The visual phases of a [CameraFocusIndicatorState]. */
internal enum class CameraFocusPhase { Hidden, Appear, Shown, Restore, Dimmed, FadeOut }

/** The geometry of a placed [CameraFocusIndicator], in px. */
@Immutable
internal class CameraFocusLayout(
    val center: Offset,
    val sliderX: Float,
    val lockButton: Rect,
)

/**
 * Default values of [CameraFocusIndicator], from the camera's `focus_*` and `exposure_*`
 * dimensions and the `exposure_bar_lottie_anim` / `exposure_lock_lottie_anim` compositions.
 */
object CameraFocusIndicatorDefaults {
    /** The focus box size (`focus_indicator_width`, `ic_indicator_focus` drawn fitXY). */
    val IndicatorSize = 70.dp

    /** The minimum distance between the focus box and the area edges (`exposure_indicator_min_margin`). */
    val EdgeMargin = 10.dp

    /**
     * The distance from the box center to the slider center: half of `exposure_bar_container_width`
     * (164dp) minus `exposure_indicator_horizontal_margin` (12dp) and half of the 30dp seekbar wrapper.
     */
    val SliderOffset = 55.dp

    /** The distance to the end edge within which the slider moves to the start side (half of 164dp). */
    val SliderFlipDistance = 82.dp

    /** The travel of the sun: 369 of the 432 composition units of the 140dp `exposure_seekbar`. */
    val SliderTravel = 119.6.dp

    /** The track line width (1.8 composition units x3 at 0.324dp). */
    val SliderLineWidth = 1.76.dp

    /** The gap between the sun center and either part of the track line (36 composition units). */
    val SliderLineGap = 11.7.dp

    /** The sun disc radius. */
    val SunRadius = 5.03.dp

    /** The radius of the sun's hollow core at the darkest exposure. */
    val SunCoreRadius = 3.48.dp

    /** The inner end of the sun rays. */
    val SunRayInnerRadius = 6.81.dp

    /** The outer end of the sun rays. */
    val SunRayOuterRadius = 9.35.dp

    /** The sun ray width (`focus_common_stroke_width`). */
    val SunRayWidth = 1.57.dp

    /** The hairline 30% black outline around the white shapes (0.33 units in the camera assets). */
    val OutlineWidth = 0.33.dp

    /** The touch size of the lock button (`exposure_bar_toggle_touch_size`). */
    val LockButtonTouchSize = 30.dp

    /** The lock icon size (`exposure_bar_toggle_size`). */
    val LockIconSize = 24.dp

    /** The vertical distance from the box center to the lock button (half of 206dp minus 15dp). */
    val LockButtonOffset = 88.dp

    /** The distance to the top edge within which the lock button moves below the slider (half of 206dp). */
    val LockButtonFlipDistance = 103.dp

    /** The label size (`PreviewContentTextStyle`, not scaled with the font scale). */
    val LabelTextSize = 12.dp

    /** The label shadow radius (`focus_exposure_indicator_shadow_radius`). */
    val LabelShadowRadius = 2.dp

    /** The gap between the focus box and the label. */
    val LabelMarginTop = 0.dp

    /** The width the label wraps within (`focus_indicator_container_width`). */
    val LabelMaxWidth = 120.dp

    /** The scale the indicator appears from. */
    val AppearScale = 1.8f

    /** The alpha of a dimmed indicator. */
    val DimmedAlpha = 0.55f

    /** The exposure of a new focus (`ExposureSeekbarContainer` progress 50). */
    val DefaultExposure = 0.5f

    /** The part of the exposure range one drag over the whole area height covers (damping 0.35 x 200%). */
    val DragSensitivity = 0.7f

    /** The time after a focus before the indicator fades out. */
    val AutoHideDelayMillis = 3000L

    /** The time after an exposure drag or a lock toggle before the indicator dims. */
    val DimDelayMillis = 1500L

    /** The time a dimmed indicator stays before it fades out. */
    val DimmedHideDelayMillis = 4000L

    /**
     * The fixed light-on-dark camera palette: white shapes with a 30% black outline (the `#000000`
     * 0.3 alpha band of `ic_indicator_focus` and the lottie border layers), the camera accent when
     * locked (`couiColorPrimary`, `color_primary_light_hasselblad` #FB822A) and a white label with
     * a `color_black_with_20_percent_transparency` shadow.
     */
    @Composable
    fun colors(
        indicatorColor: Color = Color.White,
        lockedColor: Color = Color(0xFFFB822A),
        outlineColor: Color = Color(0x4D000000),
        labelColor: Color = Color.White,
        labelShadowColor: Color = Color(0x33000000),
    ): CameraFocusIndicatorColors = remember(indicatorColor, lockedColor, outlineColor, labelColor, labelShadowColor) {
        CameraFocusIndicatorColors(
            indicatorColor = indicatorColor,
            lockedColor = lockedColor,
            outlineColor = outlineColor,
            labelColor = labelColor,
            labelShadowColor = labelShadowColor,
        )
    }
}

/**
 * The colors of a [CameraFocusIndicator].
 *
 * @param indicatorColor The brackets, the sun, the track line and the padlock.
 * @param lockedColor The same shapes while AE/AF is locked.
 * @param outlineColor The hairline outline around the shapes.
 * @param labelColor The label under the focus box.
 * @param labelShadowColor The label shadow.
 */
@Immutable
data class CameraFocusIndicatorColors(
    val indicatorColor: Color,
    val lockedColor: Color,
    val outlineColor: Color,
    val labelColor: Color,
    val labelShadowColor: Color,
)

private fun Float.clampInside(margin: Float, extent: Float): Float = if (extent - margin < margin) extent / 2f else coerceIn(margin, extent - margin)

/** `g`: the focus image alpha keyframes 1, 0.6, 1 over the blink fraction. */
private fun focusBlinkAlpha(fraction: Float): Float = if (fraction < 0.5f) 1f - 0.8f * fraction else 0.6f + 0.8f * (fraction - 0.5f)

/** `v(true)`: the focus image scale keyframes 1, 1.3, 0.9, 1 over the pulse fraction. */
private fun focusPulseScale(fraction: Float): Float = when {
    fraction < 1f / 3f -> 1f + 0.9f * fraction
    fraction < 2f / 3f -> 1.3f - 1.2f * (fraction - 1f / 3f)
    else -> 0.9f + 0.3f * (fraction - 2f / 3f)
}

/** Draws `ic_indicator_focus` (71 x 72 viewport) stretched over the 70dp box around [center]. */
private fun DrawScope.drawFocusBrackets(
    path: Path,
    center: Offset,
    pulse: Float,
    alpha: Float,
    color: Color,
    outlineColor: Color,
    layerPaint: Paint,
) {
    val box = CameraFocusIndicatorDefaults.IndicatorSize.toPx()
    val left = center.x - box / 2f
    val top = center.y - box / 2f
    val layered = alpha < 1f
    if (layered) {
        layerPaint.alpha = alpha
        drawContext.canvas.saveLayer(Rect(left - box, top - box, left + box * 2f, top + box * 2f), layerPaint)
    }
    withTransform({
        scale(pulse, pulse, pivot = center)
        translate(left, top)
        scale(box / FOCUS_BRACKET_VIEWPORT_WIDTH, box / FOCUS_BRACKET_VIEWPORT_HEIGHT, pivot = Offset.Zero)
    }) {
        // The drawable's 30% black band runs 0.33 units outside the white brackets.
        drawPath(path, outlineColor, style = Stroke(width = FOCUS_BRACKET_OUTLINE * 2f))
        drawPath(path, color)
    }
    if (layered) drawContext.canvas.restore()
}

/**
 * Draws `exposure_bar_lottie_anim` at [exposure]: the sun (`*Path_seek`) and, when [showLine], the
 * track line (`*Path_seekbar`).
 */
private fun DrawScope.drawExposureSlider(
    path: Path,
    sliderX: Float,
    centerY: Float,
    exposure: Float,
    showLine: Boolean,
    color: Color,
    outlineColor: Color,
) {
    val travel = CameraFocusIndicatorDefaults.SliderTravel.toPx()
    val sunY = centerY + (0.5f - exposure) * travel
    val outline = Stroke(width = CameraFocusIndicatorDefaults.OutlineWidth.toPx())
    if (showLine) {
        val lineWidth = CameraFocusIndicatorDefaults.SliderLineWidth.toPx()
        val gap = CameraFocusIndicatorDefaults.SliderLineGap.toPx()
        val trackTop = centerY - travel / 2f
        val trackBottom = centerY + travel / 2f
        for ((from, to) in listOf(trackTop to sunY - gap, sunY + gap to trackBottom)) {
            if (to - from <= 0f) continue
            val topLeft = Offset(sliderX - lineWidth / 2f, from)
            val rectSize = Size(lineWidth, to - from)
            drawRect(color, topLeft, rectSize)
            drawRect(outlineColor, topLeft, rectSize, style = outline)
        }
    }

    // The disc minus its core: a moon-phase cut bounded by the right half circle and a half ellipse
    // whose horizontal radius runs from -r (full core) through 0 (half) to r (no core).
    val sun = Offset(sliderX, sunY)
    val radius = CameraFocusIndicatorDefaults.SunRadius.toPx()
    val core = CameraFocusIndicatorDefaults.SunCoreRadius.toPx()
    path.rewind()
    path.fillType = PathFillType.EvenOdd
    path.addOval(Rect(sun, radius))
    if (exposure < 1f) {
        val terminator = core * (2f * exposure - 1f)
        path.moveTo(sun.x, sun.y - core)
        path.arcTo(Rect(sun, core), -90f, 180f, false)
        if (abs(terminator) < 0.5f) {
            path.lineTo(sun.x, sun.y - core)
        } else {
            val ellipse = Rect(sun.x - abs(terminator), sun.y - core, sun.x + abs(terminator), sun.y + core)
            path.arcTo(ellipse, 90f, if (terminator < 0f) 180f else -180f, false)
        }
        path.close()
    }
    drawPath(path, color)
    drawPath(path, outlineColor, style = outline)

    // Eight rays turning a full circle across the range.
    val rayWidth = CameraFocusIndicatorDefaults.SunRayWidth.toPx()
    val inner = CameraFocusIndicatorDefaults.SunRayInnerRadius.toPx()
    val outer = CameraFocusIndicatorDefaults.SunRayOuterRadius.toPx()
    for (i in 0 until SUN_RAY_COUNT) {
        rotate(degrees = exposure * 360f + i * (360f / SUN_RAY_COUNT), pivot = sun) {
            val topLeft = Offset(sun.x - rayWidth / 2f, sun.y - outer)
            val rectSize = Size(rayWidth, outer - inner)
            drawRect(color, topLeft, rectSize)
            drawRect(outlineColor, topLeft, rectSize, style = outline)
        }
    }
}

/**
 * Draws `exposure_lock_lottie_anim` at [progress] (0 open, 1 closed) in a 24dp box around
 * [center]: one unit of the lottie shapes is 1dp.
 */
private fun DrawScope.drawPadlock(
    path: Path,
    frame: FloatArray,
    progress: Float,
    center: Offset,
    color: Color,
    outlineColor: Color,
) {
    interpolatePadlock(progress * PADLOCK_FRAME_COUNT, frame)
    val shift = frame[PADLOCK_BODY_TOP_INDEX * 6 + 1] - PADLOCK_BODY_TOP
    val count = frame.size / 6
    path.rewind()
    path.fillType = PathFillType.EvenOdd
    path.moveTo(frame[0], frame[1])
    for (i in 0 until count) {
        val j = (i + 1) % count
        val a = i * 6
        val b = j * 6
        path.cubicTo(
            frame[a] + frame[a + 4],
            frame[a + 1] + frame[a + 5],
            frame[b] + frame[b + 2],
            frame[b + 1] + frame[b + 3],
            frame[b],
            frame[b + 1],
        )
    }
    path.close()
    // The hollow body and the ring keyhole (paths 4, 1 and 2 of the fill layer).
    path.addRect(Rect(-4.45f, 0.27f + shift, 7.95f, 8.29f + shift))
    path.addOval(Rect(Offset(1.75f, 4.28f + shift), 2.99f))
    path.addOval(Rect(Offset(1.75f, 4.28f + shift), 1.39f))
    val unit = 1.dp.toPx()
    withTransform({
        translate(center.x + PADLOCK_ORIGIN_X * unit, center.y + PADLOCK_ORIGIN_Y * unit)
        scale(unit, unit, pivot = Offset.Zero)
    }) {
        drawPath(path, color)
        drawPath(path, outlineColor, style = Stroke(width = CameraFocusIndicatorDefaults.OutlineWidth.value))
    }
}

/** Fills [out] with the padlock outline at lottie [frame] (0..30), easing each keyframe segment. */
private fun interpolatePadlock(frame: Float, out: FloatArray) {
    val last = FocusPadlockTimes.lastIndex
    if (frame <= 0f) {
        FocusPadlockFrames[0].copyInto(out)
        return
    }
    if (frame >= FocusPadlockTimes[last]) {
        FocusPadlockFrames[last].copyInto(out)
        return
    }
    var k = 0
    while (k < last - 1 && frame >= FocusPadlockTimes[k + 1]) k++
    val span = FocusPadlockTimes[k + 1] - FocusPadlockTimes[k]
    val t = FocusPadlockEasings[k].transform(((frame - FocusPadlockTimes[k]) / span).coerceIn(0f, 1f))
    val from = FocusPadlockFrames[k]
    val to = FocusPadlockFrames[k + 1]
    for (i in out.indices) out[i] = from[i] + (to[i] - from[i]) * t
}

/** The 24px start of an exposure drag at the camera's 3.5x density. */
private val FocusDragStartThreshold = 6.86.dp

/** The 2px minimum step of an exposure drag at the camera's 3.5x density. */
private val FocusDragStepThreshold = 0.57.dp

/** The ratio |dy| / |dx| above which a move counts as vertical (`FocusExposurePresenter`). */
private const val VERTICAL_DRAG_RATIO = 0.58f

private const val SUN_RAY_COUNT = 8

/** The white brackets of `ic_indicator_focus`. */
private const val FOCUS_BRACKET_PATH =
    "M20.5,1H2H0.5V2.5V21H2V2.5H20.5V1ZM20.5,69.5H2V51H0.5V69.5V71H2H20.5V69.5ZM50.5,71V69.5H69V51H70.5V69.5V71H69H50.5ZM50.5,2.5V1H69H70.5V2.5V21H69V2.5H50.5Z"
private const val FOCUS_BRACKET_VIEWPORT_WIDTH = 71f
private const val FOCUS_BRACKET_VIEWPORT_HEIGHT = 72f
private const val FOCUS_BRACKET_OUTLINE = 0.33f

/** The 30 frames of the lock lottie at 60fps. */
private const val PADLOCK_FRAME_COUNT = 30f
private const val PADLOCK_DURATION = 500f

/** The fill layer position (30.749, 36.281) of the 72-unit composition in a 24dp box, from the box center, in dp. */
private const val PADLOCK_ORIGIN_X = -1.75f
private const val PADLOCK_ORIGIN_Y = 0.09f

/** The vertex whose y tracks the body top, which drops 0.15 units in the last frames. */
private const val PADLOCK_BODY_TOP_INDEX = 12
private const val PADLOCK_BODY_TOP = -1.33f

/** COUI b, `COUIEaseInterpolator` (0.33, 0, 0.67, 1). */
private val FocusStandardEasing = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

/** COUI f (0.3, 0, 1, 1), the appear scale. */
private val FocusAppearScaleEasing = CubicBezierEasing(0.3f, 0f, 1f, 1f)

/** COUI e, `COUIMoveEaseInterpolator` (0.3, 0, 0.1, 1). */
private val FocusMoveEaseEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

/** `c9.a.i` (0.58, 0, 0.69, 1), the dim. */
private val FocusDimEasing = CubicBezierEasing(0.58f, 0f, 0.69f, 1f)

/** The padlock keyframes kept from `exposure_lock_lottie_anim` (frames 0, 14, 20, 26, 30). */
private val FocusPadlockTimes = floatArrayOf(0f, 14f, 20f, 26f, 30f)

/** The easing of each padlock segment, from the keyframes' out / in tangents. */
private val FocusPadlockEasings: List<Easing> = listOf(
    CubicBezierEasing(0.33f, 0f, 0.67f, 1f),
    CubicBezierEasing(0.167f, 0f, 0.833f, 1f),
    CubicBezierEasing(0.167f, 0f, 0.67f, 1f),
    CubicBezierEasing(0.33f, 0f, 0.67f, 1f),
)

/**
 * The shackle-and-body outline of the lock lottie's fill layer at each kept keyframe: per vertex
 * x, y, in-tangent x, y, out-tangent x, y, in dp around the layer origin.
 */
private val FocusPadlockFrames: List<FloatArray> = listOf(
    // frame 0: open, the shackle swung to the start
    floatArrayOf(
        -3.31f, -7.58f, 0.5f, 0.45f, -0.5f, -0.45f,
        -5.25f, -8.29f, 0.74f, 0f, -0.74f, 0f,
        -7.19f, -7.58f, 0.5f, -0.45f, -0.5f, 0.45f,
        -7.95f, -5.94f, 0f, -0.59f, 0f, 0f,
        -7.95f, -4.37f, 0f, 0f, 0f, 0f,
        -9.55f, -4.37f, 0f, 0f, 0f, 0f,
        -9.55f, -5.94f, 0f, 0f, 0f, -1.08f,
        -8.26f, -8.77f, -0.81f, 0.73f, 0.81f, -0.73f,
        -5.25f, -9.89f, -1.11f, 0f, 1.11f, 0f,
        -2.24f, -8.77f, -0.81f, -0.73f, 0.81f, 0.73f,
        -0.95f, -5.94f, 0f, -1.08f, 0f, 0f,
        -0.95f, -1.33f, 0f, 0f, 0f, 0f,
        9.55f, -1.33f, 0f, 0f, 0f, 0f,
        9.55f, 8.22f, 0f, 0f, 0f, 0.44f,
        9.06f, 9.4f, 0.31f, -0.31f, -0.31f, 0.31f,
        7.88f, 9.89f, 0.44f, 0f, 0f, 0f,
        -4.38f, 9.89f, 0f, 0f, -0.44f, 0f,
        -5.56f, 9.4f, 0.31f, 0.31f, -0.31f, -0.31f,
        -6.05f, 8.22f, 0f, 0.44f, 0f, 0f,
        -6.05f, -1.33f, 0f, 0f, 0f, 0f,
        -2.55f, -1.33f, 0f, 0f, 0f, 0f,
        -2.55f, -5.94f, 0f, 0f, 0f, -0.59f,
    ),
    // frame 14: lifted
    floatArrayOf(
        -3.31f, -8.28f, 0.5f, 0.45f, -0.5f, -0.45f,
        -5.25f, -9f, 0.74f, 0f, -0.74f, 0f,
        -7.19f, -8.28f, 0.5f, -0.45f, -0.5f, 0.45f,
        -7.95f, -6.64f, 0f, -0.59f, 0f, 0f,
        -7.95f, -5.07f, 0f, 0f, 0f, 0f,
        -9.55f, -5.07f, 0f, 0f, 0f, 0f,
        -9.55f, -6.64f, 0f, 0f, 0f, -1.08f,
        -8.26f, -9.47f, -0.81f, 0.73f, 0.81f, -0.73f,
        -5.25f, -10.6f, -1.11f, 0f, 1.11f, 0f,
        -2.24f, -9.47f, -0.81f, -0.73f, 0.81f, 0.73f,
        -0.95f, -6.12f, 0f, -1.08f, 0f, 0f,
        -0.95f, -1.33f, 0f, 0f, 0f, 0f,
        9.55f, -1.33f, 0f, 0f, 0f, 0f,
        9.55f, 8.22f, 0f, 0f, 0f, 0.44f,
        9.06f, 9.4f, 0.31f, -0.31f, -0.31f, 0.31f,
        7.88f, 9.89f, 0.44f, 0f, 0f, 0f,
        -4.38f, 9.89f, 0f, 0f, -0.44f, 0f,
        -5.56f, 9.4f, 0.31f, 0.31f, -0.31f, -0.31f,
        -6.05f, 8.22f, 0f, 0.44f, 0f, 0f,
        -6.05f, -1.33f, 0f, 0f, 0f, 0f,
        -2.55f, -1.33f, 0f, 0f, 0f, 0f,
        -2.55f, -6.12f, 0f, 0f, 0f, -0.59f,
    ),
    // frame 20: edge-on, mid swing
    floatArrayOf(
        -2.31f, -8.94f, -0.23f, 0.55f, -0.01f, -0.04f,
        -1.73f, -9.79f, -0.34f, 0.01f, 0.25f, 0.01f,
        -1.11f, -8.93f, -0.13f, -0.64f, 0.13f, 0.58f,
        -0.94f, -6.13f, 0f, -0.97f, 0f, 0f,
        -0.95f, -3.2f, 0f, 0f, 0f, 0f,
        -2.55f, -3.2f, 0f, 0f, 0f, 0f,
        -2.55f, -6.42f, 0f, 0f, 0.01f, -0.97f,
        -2.31f, -8.95f, -0.2f, 0.52f, 0.19f, -0.59f,
        -1.73f, -9.78f, -0.15f, 0f, 0.28f, -0.01f,
        -1.09f, -8.82f, -0.11f, -0.68f, 0.1f, 0.6f,
        -0.95f, -6.12f, 0f, -1.05f, 0f, 0f,
        -0.95f, -1.33f, 0f, 0f, 0f, 0f,
        9.55f, -1.33f, 0f, 0f, 0f, 0f,
        9.55f, 8.22f, 0f, 0f, 0f, 0.44f,
        9.06f, 9.4f, 0.31f, -0.31f, -0.31f, 0.31f,
        7.88f, 9.89f, 0.44f, 0f, 0f, 0f,
        -4.38f, 9.89f, 0f, 0f, -0.44f, 0f,
        -5.56f, 9.4f, 0.31f, 0.31f, -0.31f, -0.31f,
        -6.05f, 8.22f, 0f, 0.44f, 0f, 0f,
        -6.05f, -1.33f, 0f, 0f, 0f, 0f,
        -2.55f, -1.33f, 0f, 0f, 0f, 0f,
        -2.55f, -6.12f, 0f, 0f, 0.01f, -0.93f,
    ),
    // frame 26: over the body, still lifted
    floatArrayOf(
        -1.73f, -8.86f, -0.43f, 0.51f, 0.77f, -0.92f,
        1.79f, -10.58f, -1.42f, -0.01f, 1.21f, 0.01f,
        4.93f, -9.36f, -0.77f, -0.83f, 0.63f, 0.6f,
        6.03f, -6.69f, 0.01f, -1.28f, 0f, 0f,
        6.04f, -1.33f, 0f, 0f, 0f, 0f,
        4.44f, -1.33f, 0f, 0f, 0f, 0f,
        4.45f, -6.19f, 0f, 0f, 0.03f, -0.86f,
        3.8f, -8.36f, 0.41f, 0.31f, -0.43f, -0.46f,
        1.9f, -9.01f, 0.81f, 0f, -0.56f, -0.02f,
        -0.32f, -8.05f, 0.59f, -0.81f, -0.32f, 0.44f,
        -0.95f, -6.12f, 0.01f, -0.86f, 0f, 0f,
        -0.95f, -1.33f, 0f, 0f, 0f, 0f,
        9.55f, -1.33f, 0f, 0f, 0f, 0f,
        9.55f, 8.22f, 0f, 0f, 0f, 0.44f,
        9.06f, 9.4f, 0.31f, -0.31f, -0.31f, 0.31f,
        7.88f, 9.89f, 0.44f, 0f, 0f, 0f,
        -4.38f, 9.89f, 0f, 0f, -0.44f, 0f,
        -5.56f, 9.4f, 0.31f, 0.31f, -0.31f, -0.31f,
        -6.05f, 8.22f, 0f, 0.44f, 0f, 0f,
        -6.05f, -1.33f, 0f, 0f, 0f, 0f,
        -2.55f, -1.33f, 0f, 0f, 0f, 0f,
        -2.55f, -6.12f, 0f, 0f, 0.01f, -1.26f,
    ),
    // frame 30: closed
    floatArrayOf(
        -1.73f, -8.11f, -0.43f, 0.51f, 0.77f, -0.92f,
        1.79f, -9.93f, -1.42f, -0.01f, 1.21f, 0.01f,
        4.93f, -8.61f, -0.77f, -0.83f, 0.63f, 0.6f,
        6.03f, -5.94f, -0.03f, -1.12f, 0f, 0f,
        6.04f, -1.48f, 0f, 0f, 0f, 0f,
        4.44f, -1.48f, 0f, 0f, 0f, 0f,
        4.45f, -5.44f, 0f, 0f, 0.03f, -0.86f,
        3.8f, -7.61f, 0.41f, 0.31f, -0.43f, -0.46f,
        1.9f, -8.26f, 0.81f, 0f, -0.56f, -0.02f,
        -0.32f, -7.3f, 0.59f, -0.81f, -0.32f, 0.44f,
        -0.95f, -5.37f, 0.01f, -0.86f, 0f, 0f,
        -0.95f, -1.48f, 0f, 0f, 0f, 0f,
        9.55f, -1.48f, 0f, 0f, 0f, 0f,
        9.55f, 8.07f, 0f, 0f, 0f, 0.44f,
        9.06f, 9.26f, 0.31f, -0.31f, -0.31f, 0.31f,
        7.88f, 9.75f, 0.44f, 0f, 0f, 0f,
        -4.38f, 9.75f, 0f, 0f, -0.44f, 0f,
        -5.56f, 9.26f, 0.31f, 0.31f, -0.31f, -0.31f,
        -6.05f, 8.07f, 0f, 0.44f, 0f, 0f,
        -6.05f, -1.48f, 0f, 0f, 0f, 0f,
        -2.55f, -1.48f, 0f, 0f, 0f, 0f,
        -2.55f, -5.37f, 0f, 0f, 0.01f, -1.26f,
    ),
)
