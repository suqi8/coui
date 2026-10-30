// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.util.lerp
import io.github.suqi8.coui.kmp.effect.LocalMaterialBackdrop
import io.github.suqi8.coui.kmp.effect.MaterialBackdropSpec
import io.github.suqi8.coui.kmp.effect.TouchMotionState
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * A slim media progress bar with COUI style, the port of COUI 17 `COUIIntentSeekBar`.
 *
 * At rest the bar is a thin capsule ([size] sets 2, 4 or 6dp) that grows on touch down
 * (`COUISeekBarCore.touchAnim`, response 0.5 / bounce 0) to its pressed height and shrinks back on
 * release. The progress fills the capsule from the start, and an optional [secondaryValue] (the
 * buffered position) is drawn underneath it, both clipped to the capsule
 * (`COUIIntentSeekBar.drawActiveTrack`).
 *
 * Unlike [Slider], dragging moves the progress by the finger's travel instead of jumping to it
 * (`couiSeekBarIsFollowThumb = false`, move type 2); pass [followThumb] to make the progress chase
 * the finger instead. A tap springs the progress to the tapped position
 * (`COUISeekBarAdvanced.startClickTransitionAnim`), a fast release keeps it gliding
 * (`PhysicsAnimationHandler`), and while a finger drags the whole bar follows it horizontally on the
 * COUI 17 touch-motion rubber band (`TouchMotionEffectRenderer`, translateX only).
 *
 * With [showThumb] a round thumb pops out while the bar is touched or [paused] and disappears again
 * when playback goes on (`ThumbStateAnimationHandler`).
 *
 * With [followSystemTheme] off the bar takes the fixed white palette COUI uses over video and
 * artwork, and, when a [LocalMaterialBackdrop] provider is present, the COUI 17 blur-blend
 * background in place of its translucent fill (`BlurBlendEffectRenderer`).
 *
 * The bar fills the available width and is at least [IntentSliderDefaults.MinHeight] high.
 *
 * @param value The current value. Values outside [valueRange] are coerced into it.
 * @param onValueChange Called with the new value while the user drags, taps or flings the bar.
 * @param modifier The modifier to be applied to the bar.
 * @param enabled Whether the bar accepts input.
 * @param showThumb Whether to draw the thumb while the bar is touched or [paused]
 *   (`couiIntentSeekBarShowThumb`, off by default).
 * @param paused Whether the media is paused (`PlaybackState.PAUSED`): the thumb stays out at its
 *   rest radius instead of disappearing after a touch. Only has an effect with [showThumb].
 * @param followThumb Whether a drag moves the progress to the finger (`couiSeekBarIsFollowThumb`).
 *   Off by default: the progress moves by the finger's travel from wherever it was.
 * @param followSystemTheme Whether the colors follow the theme (`couiIntentSeekBarFollowSystemTheme`,
 *   the default). When off, [colors] defaults to [IntentSliderDefaults.fixedColors] and the
 *   [materialBackdrop] blur may replace the background fill.
 * @param materialBackdrop Whether the COUI 17 blur-blend background (`BlurBlendEffectRenderer`) may
 *   replace the background fill. COUI enables it only when the bar does not follow the theme and
 *   uses no custom colors, so it only applies with [followSystemTheme] off, and only when a
 *   [LocalMaterialBackdrop] provider is present; pass `false` when you supply custom [colors].
 * @param valueRange The range of values the bar can take.
 * @param secondaryValue The secondary (buffered) value, drawn under the progress; null hides it
 *   (`couiIntentSeekBarShowSecondaryProgress`).
 * @param onValueChangeFinished Called when the user has finished changing the value: at the end of a
 *   drag, a fling or a tap.
 * @param size The track and thumb sizes ([IntentSliderSize], `couiIntentSeekBarSize`).
 * @param horizontalPadding The inset of the track from both ends of the bar
 *   (`couiSeekBarProgressPaddingHorizontal`); the thumb and the rubber band reach into it.
 * @param colors The [IntentSliderColors] of the bar.
 * @param hapticEffect The haptic effect of the bar (`VibrationFeedbackHandler`).
 */
@Composable
fun IntentSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showThumb: Boolean = false,
    paused: Boolean = false,
    followThumb: Boolean = false,
    followSystemTheme: Boolean = true,
    materialBackdrop: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    secondaryValue: Float? = null,
    onValueChangeFinished: (() -> Unit)? = null,
    size: IntentSliderSize = IntentSliderSize.Default,
    horizontalPadding: Dp = IntentSliderDefaults.HorizontalPadding,
    colors: IntentSliderColors = if (followSystemTheme) IntentSliderDefaults.intentSliderColors() else IntentSliderDefaults.fixedColors(),
    hapticEffect: SliderDefaults.SliderHapticEffect = SliderDefaults.DefaultHapticEffect,
) {
    require(valueRange.start < valueRange.endInclusive) { "valueRange start should be less than end" }

    val density = LocalDensity.current
    val hapticFeedback = LocalHapticFeedback.current
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val scope = rememberCoroutineScope()
    val onValueChangeState by rememberUpdatedState(onValueChange)
    val onValueChangeFinishedState by rememberUpdatedState(onValueChangeFinished)

    val coercedValue = value.coerceIn(valueRange.start, valueRange.endInclusive)
    val state = remember(scope) { IntentSliderState(scope, coercedValue) }
    val hapticState = remember { SliderHapticState() }
    // TouchMotionEffectRenderer: only the rubber-band translation reaches the view (translateX).
    val touchMotion = remember { TouchMotionState().apply { deformEnabled = false } }
    LaunchedEffect(enabled) {
        if (!enabled) touchMotion.translationX.animateTo(0f)
    }

    // COUISeekBarCore.setProgress draws a programmatic value right away (media playback updates
    // every frame); only the gestures pick a spring for the values they report.
    LaunchedEffect(coercedValue) { state.showValue(coercedValue) }

    val trackHeightPx = with(density) { size.trackHeight.toPx() }
    val maxTrackHeightPx = with(density) { size.maxTrackHeight.toPx() }
    val thumbRadiusPx = with(density) { size.thumbRadius.toPx() }
    val thumbMaxRadiusPx = with(density) { size.thumbMaxRadius.toPx() }
    val paddingPx = with(density) { horizontalPadding.toPx() }

    // COUIIntentSeekBar.getMinEnlargeHeight: a release waits until the track has grown by 2dp.
    val minEnlargeFraction = if (maxTrackHeightPx > trackHeightPx) {
        (with(density) { IntentMinEnlargeHeight.toPx() } / (maxTrackHeightPx - trackHeightPx)).coerceIn(0f, 1f)
    } else {
        0f
    }
    val touched = state.isPressed || state.isDragging
    LaunchedEffect(touched) { state.updateEnlarge(touched, minEnlargeFraction) }

    // ThumbStateAnimationHandler + StandardAnimationHandler.startThumbScaleAnimation.
    LaunchedEffect(touched, paused, showThumb, thumbRadiusPx, thumbMaxRadiusPx) {
        state.animateThumb(touched, paused, showThumb, thumbRadiusPx, thumbMaxRadiusPx)
    }

    // BlurBlendEffectRenderer.shouldEnableBlurBlend: !followSystemTheme && !hasCustomUserColors.
    val backdropProvider = if (!followSystemTheme && materialBackdrop) LocalMaterialBackdrop.current else null
    val backdropModifier = if (backdropProvider != null) {
        remember(backdropProvider) { backdropProvider.materialBackdrop(IntentSliderDefaults.BackdropSpec, CircleShape) }
    } else {
        null
    }

    // Read by the long-lived gesture and fling coroutines.
    val currentValue by rememberUpdatedState(coercedValue)
    val currentValueRange by rememberUpdatedState(valueRange)
    val currentRtl by rememberUpdatedState(isRtl)
    val currentFollowThumb by rememberUpdatedState(followThumb)
    val currentPadding by rememberUpdatedState(paddingPx)
    val currentHapticEffect by rememberUpdatedState(hapticEffect)

    fun fractionToValue(fraction: Float): Float {
        val range = currentValueRange
        return range.start + fraction.coerceIn(0f, 1f) * (range.endInclusive - range.start)
    }

    fun positionToFraction(x: Float): Float {
        val length = state.width - 2f * currentPadding
        if (length <= 0f) return 0f
        val fraction = ((x - currentPadding) / length).coerceIn(0f, 1f)
        return if (currentRtl) 1f - fraction else fraction
    }

    fun report(newValue: Float, spec: AnimationSpec<Float>?) {
        // An unchanged value draws nothing, so its spring must not linger for the next update.
        state.pendingSpec = if (newValue != currentValue) spec else null
        onValueChangeState(newValue)
        hapticState.handleHapticFeedback(newValue, currentValueRange, currentHapticEffect, hapticFeedback)
    }

    val minHeight = max(
        max(size.maxTrackHeight, if (showThumb) size.thumbMaxRadius * 2 else 0.dp),
        IntentSliderDefaults.MinHeight,
    )

    Box(
        modifier = Modifier
            // TouchMotionEffectRenderer: the whole bar follows the finger on the rubber band.
            .graphicsLayer { translationX = touchMotion.translationX.value }
            .then(modifier)
            .heightIn(min = minHeight)
            .onSizeChanged {
                state.width = it.width.toFloat()
                touchMotion.width = it.width.toFloat()
                touchMotion.height = it.height.toFloat()
            }
            .then(
                if (enabled) {
                    Modifier
                        .pointerInput(Unit) {
                            detectSliderPress(
                                orientation = Orientation.Horizontal,
                                onPress = { position, uptimeMillis ->
                                    if (state.stopFling()) onValueChangeFinishedState?.invoke()
                                    state.press(position, uptimeMillis)
                                    with(touchMotion) { down(position) }
                                },
                                onMove = { position, uptimeMillis ->
                                    state.velocityTracker.addPosition(uptimeMillis, position)
                                    with(touchMotion) { move(position, scope) }
                                },
                                onRelease = { tapPosition ->
                                    state.isPressed = false
                                    val velocity = state.velocityTracker.calculateVelocity()
                                    with(touchMotion) { release(velocity.x, velocity.y, scope) }
                                    if (tapPosition != null) {
                                        // COUISeekBarAdvanced.startClickTransitionAnim.
                                        report(fractionToValue(positionToFraction(tapPosition.x)), IntentClickAnimationSpec)
                                        onValueChangeFinishedState?.invoke()
                                    }
                                },
                            )
                        }
                        .draggable(
                            orientation = Orientation.Horizontal,
                            state = rememberDraggableState { delta ->
                                state.dragDelta += delta
                                val newValue = if (currentFollowThumb) {
                                    fractionToValue(positionToFraction(state.dragStartX + state.dragDelta))
                                } else {
                                    // COUISeekBarCore.trackTouchEvent: move type 2 moves the progress
                                    // by the travel since the drag started.
                                    val length = (state.width - 2f * currentPadding).coerceAtLeast(1f)
                                    val travel = state.dragDelta / length
                                    fractionToValue(state.dragStartFraction + if (currentRtl) -travel else travel)
                                }
                                report(newValue, if (currentFollowThumb) IntentFollowHandAnimationSpec else null)
                            },
                            onDragStarted = { offset ->
                                state.isDragging = true
                                state.dragStartX = offset.x
                                state.dragDelta = 0f
                                state.dragStartFraction = valueToIntentFraction(currentValue, currentValueRange)
                                hapticState.reset(currentValue)
                                if (currentFollowThumb) {
                                    report(fractionToValue(positionToFraction(offset.x)), IntentFollowHandAnimationSpec)
                                }
                            },
                            onDragStopped = { velocity ->
                                state.isDragging = false
                                val length = state.width - 2f * currentPadding
                                if (length > 0f && abs(velocity) >= INTENT_FLING_MIN_VELOCITY) {
                                    // COUISeekBarAdvanced.flingBehaviorAfterEndDrag.
                                    state.flingJob = scope.launch {
                                        flingSliderTrack(
                                            startFraction = valueToIntentFraction(currentValue, currentValueRange),
                                            velocity = velocity.coerceIn(-INTENT_FLING_MAX_VELOCITY, INTENT_FLING_MAX_VELOCITY),
                                            trackLength = length,
                                            reverseDirection = currentRtl,
                                        ) { visualFraction, _ ->
                                            report(fractionToValue(if (currentRtl) 1f - visualFraction else visualFraction), null)
                                        }
                                        onValueChangeFinishedState?.invoke()
                                    }
                                } else {
                                    onValueChangeFinishedState?.invoke()
                                }
                            },
                        )
                } else {
                    Modifier
                },
            )
            .semantics {
                progressBarRangeInfo = ProgressBarRangeInfo(coercedValue, valueRange.start..valueRange.endInclusive, 0)
                if (enabled) {
                    setProgress { target ->
                        onValueChangeState(target.coerceIn(valueRange.start, valueRange.endInclusive))
                        true
                    }
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        if (backdropModifier != null) {
            // BlurBlendEffectRenderer.updateBaseParams: the backdrop follows the background rect
            // and its radius while the track grows.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding)
                    .layout { measurable, constraints ->
                        val height = lerp(trackHeightPx, maxTrackHeightPx, state.enlarge.value).toInt()
                        val width = constraints.maxWidth
                        val placeable = measurable.measure(Constraints.fixed(width, height))
                        layout(width, height) { placeable.place(0, 0) }
                    }
                    .then(backdropModifier),
            )
        }
        IntentSliderTrack(
            state = state,
            valueRange = valueRange,
            secondaryValue = secondaryValue,
            colors = colors,
            drawBackground = backdropModifier == null,
            enabled = enabled,
            isRtl = isRtl,
            trackHeightPx = trackHeightPx,
            maxTrackHeightPx = maxTrackHeightPx,
            paddingPx = paddingPx,
            // Like a match_parent COUIIntentSeekBar, the bar takes the whole available width.
            modifier = Modifier
                .fillMaxWidth()
                .height(minHeight),
        )
    }
}

/**
 * Draws the bar like `COUIIntentSeekBar.drawBackground` + `drawActiveTrack`: the background capsule
 * (skipped when the blur-blend backdrop replaces it), the secondary and the primary progress clipped
 * to the capsule, then the unclipped thumb (`IntentThumbRenderer`).
 */
@Composable
private fun IntentSliderTrack(
    state: IntentSliderState,
    valueRange: ClosedFloatingPointRange<Float>,
    secondaryValue: Float?,
    colors: IntentSliderColors,
    drawBackground: Boolean,
    enabled: Boolean,
    isRtl: Boolean,
    trackHeightPx: Float,
    maxTrackHeightPx: Float,
    paddingPx: Float,
    modifier: Modifier = Modifier,
) {
    val clip = remember { Path() }
    Canvas(modifier = modifier) {
        val left = paddingPx
        val right = size.width - paddingPx
        val length = right - left
        if (length <= 0f) return@Canvas
        // COUISeekBarCore.onHeightSpringUpdate; getProgressRectHeight is the background height.
        val height = lerp(trackHeightPx, maxTrackHeightPx, state.enlarge.value)
        val radius = height / 2f
        val centerY = size.height / 2f
        val top = centerY - radius
        val corner = CornerRadius(radius)

        if (drawBackground) {
            drawRoundRect(
                color = colors.backgroundColor,
                topLeft = Offset(left, top),
                size = Size(length, height),
                cornerRadius = corner,
            )
        }

        fun drawFill(color: Color, fraction: Float) {
            if (fraction <= 0f) return
            val width = fraction * length
            drawRoundRect(
                color = color,
                topLeft = Offset(if (isRtl) right - width else left, top),
                size = Size(width, height),
                cornerRadius = corner,
            )
        }

        val fraction = valueToIntentFraction(state.visualValue.value, valueRange)
        clip.reset()
        clip.addRoundRect(RoundRect(left, top, right, top + height, corner))
        clipPath(clip) {
            // SecondaryProgressRenderer, then the progress (StandardProgressRenderer).
            if (secondaryValue != null) drawFill(colors.secondaryProgressColor, valueToIntentFraction(secondaryValue, valueRange))
            drawFill(colors.progressColor, fraction)
        }

        val thumbRadius = state.thumbRadius.value
        if (thumbRadius > 0f) {
            val thumbX = if (isRtl) right - fraction * length else left + fraction * length
            drawThumbWithShadow(
                center = Offset(thumbX, centerY),
                radius = thumbRadius,
                thumbColor = colors.thumbColor,
                shadowColor = colors.thumbShadowColor,
                shadowEnabled = enabled,
            )
        }
    }
}

/**
 * The touch, animation and gesture state of an [IntentSlider], after `COUIIntentSeekBar` and its
 * `COUISeekBarCore` / `COUISeekBarAdvanced` bases.
 */
@Stable
private class IntentSliderState(private val scope: CoroutineScope, initialValue: Float) {
    /** Whether a pointer is down on the bar (`ACTION_DOWN` until `ACTION_UP` / `ACTION_CANCEL`). */
    var isPressed by mutableStateOf(false)

    /** Whether a drag past the touch slop is moving the progress (`mStartDragging`). */
    var isDragging by mutableStateOf(false)

    /** The drawn value: snapped for programmatic updates, sprung for taps and finger-follow drags. */
    val visualValue = Animatable(initialValue)

    /** The spring the next reported value is drawn with, or null to draw it right away. */
    var pendingSpec: AnimationSpec<Float>? = null

    /** `mHeightSpringAnim` / 100: 0 at the rest height, 1 at the pressed height. */
    val enlarge = Animatable(0f)

    /** Whether the enlarge spring has started shrinking (`ThumbStateAnimationHandler.checkPendingShrink`). */
    var shrinking by mutableStateOf(true)
        private set

    /** `SeekBarState.curThumbRadius` in pixels. */
    val thumbRadius = Animatable(0f)

    val velocityTracker = VelocityTracker()
    var width = 0f
    private var thumbInitialized = false
    var dragStartX = 0f
    var dragDelta = 0f
    var dragStartFraction = 0f
    var flingJob: Job? = null
    private var enlargeJob: Job? = null

    fun press(position: Offset, uptimeMillis: Long) {
        isPressed = true
        velocityTracker.resetTracking()
        velocityTracker.addPosition(uptimeMillis, position)
    }

    /** Stops a running fling; returns whether one was running. */
    fun stopFling(): Boolean {
        val job = flingJob ?: return false
        flingJob = null
        if (!job.isActive) return false
        job.cancel()
        return true
    }

    suspend fun showValue(value: Float) {
        val spec = pendingSpec
        if (spec == null || (isDragging && spec !== IntentFollowHandAnimationSpec)) {
            visualValue.snapTo(value)
        } else {
            visualValue.animateTo(value, spec)
            if (!isDragging) pendingSpec = null
        }
    }

    /**
     * `COUISeekBarCore.touchAnim` / `releaseAnim`: a release that comes before the track has grown
     * by [minFraction] waits until it has (`mPendingRelease`), so a quick tap still shows the
     * feedback.
     */
    fun updateEnlarge(enlarged: Boolean, minFraction: Float) {
        val pendingRelease = !enlarged &&
            enlarge.isRunning &&
            enlarge.targetValue == 1f &&
            enlarge.value < minFraction
        if (pendingRelease) {
            enlargeJob = scope.launch {
                snapshotFlow { enlarge.value }.first { it >= minFraction - ENLARGE_EPSILON }
                shrinking = true
                enlarge.animateTo(0f, IntentEnlargeAnimationSpec)
            }
            return
        }
        // Cancelling an animation resets the Animatable's velocity, so read it first.
        val velocity = enlarge.velocity
        enlargeJob?.cancel()
        shrinking = !enlarged
        enlargeJob = scope.launch {
            enlarge.animateTo(if (enlarged) 1f else 0f, IntentEnlargeAnimationSpec, velocity)
        }
    }

    /**
     * The thumb radius: grows to [maxRadius] on touch (`startThumbScaleAnimation(true)`), and after
     * a release, once the track starts shrinking (`releaseThumbScaleAnim` pending shrink), settles
     * on [restRadius] when [paused] or disappears (`ThumbStateAnimationHandler.startDisappear`).
     */
    suspend fun animateThumb(touched: Boolean, paused: Boolean, showThumb: Boolean, restRadius: Float, maxRadius: Float) {
        if (!showThumb) {
            thumbRadius.snapTo(0f)
            return
        }
        if (!thumbInitialized) {
            // applyThumbSize: a bar that starts paused shows its thumb at the rest radius at once.
            thumbInitialized = true
            if (!touched) {
                thumbRadius.snapTo(if (paused) restRadius else 0f)
                return
            }
        }
        if (touched) {
            thumbRadius.animateTo(maxRadius, IntentThumbAppearAnimationSpec)
            return
        }
        snapshotFlow { shrinking || enlarge.value == 0f }.first { it }
        when {
            !paused -> thumbRadius.animateTo(0f, IntentThumbDisappearAnimationSpec)

            // startAppear: a thumb coming out bounces.
            thumbRadius.value < restRadius -> thumbRadius.animateTo(restRadius, IntentThumbAppearAnimationSpec)

            else -> thumbRadius.animateTo(restRadius, IntentThumbShrinkAnimationSpec)
        }
    }
}

private fun valueToIntentFraction(value: Float, valueRange: ClosedFloatingPointRange<Float>): Float = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)

/** `COUIIntentSeekBar.MIN_ENLARGE_HEIGHT_DP`. */
private val IntentMinEnlargeHeight = 2.dp

private const val ENLARGE_EPSILON = 0.001f

/** `COUISeekBarCore.initEnlargeAnim`: response 0.5, bounce 0. */
private val IntentEnlargeAnimationSpec = spring<Float>(dampingRatio = 1f, stiffness = 157.91f)

/** `ThumbStateAnimationHandler.startAppear` / thumb enlarge: response 0.5, bounce 0.3. */
private val IntentThumbAppearAnimationSpec = spring<Float>(dampingRatio = 0.7f, stiffness = 157.91f)

/** `StandardAnimationHandler.startThumbScaleAnimation(false)`: response 0.5, bounce 0. */
private val IntentThumbShrinkAnimationSpec = spring<Float>(dampingRatio = 1f, stiffness = 157.91f)

/** `ThumbStateAnimationHandler.startDisappear`: response 0.4, bounce 0. */
private val IntentThumbDisappearAnimationSpec = spring<Float>(dampingRatio = 1f, stiffness = 246.74f)

/** `ClickAnimationHandler`: response 0.3, bounce 0. */
private val IntentClickAnimationSpec = spring<Float>(dampingRatio = 1f, stiffness = 438.65f)

/** `COUISeekBarAdvanced.DRAG_TRANSITION_SPRING_RESPONSE` 0.15, bounce 0. */
private val IntentFollowHandAnimationSpec = spring<Float>(dampingRatio = 1f, stiffness = 1754.6f)

private const val INTENT_FLING_MIN_VELOCITY = 100f

/** `COUISeekBarAdvanced.MAX_VELOCITY`. */
private const val INTENT_FLING_MAX_VELOCITY = 8000f

/**
 * The [IntentSlider] sizes of `IntentSeekBarSize` (`couiIntentSeekBarSize`), from the
 * `coui_seekbar_intent_*` dimensions.
 *
 * @property trackHeight The rest track height (`coui_seekbar_intent_height_*`).
 * @property maxTrackHeight The pressed track height (`coui_seekbar_intent_max_background_height_*`).
 * @property thumbRadius The rest thumb radius (`coui_seekbar_intent_thumb_radius_*`).
 * @property thumbMaxRadius The pressed thumb radius (`coui_seekbar_intent_thumb_max_radius_*`).
 */
enum class IntentSliderSize(
    val trackHeight: Dp,
    val maxTrackHeight: Dp,
    val thumbRadius: Dp,
    val thumbMaxRadius: Dp,
) {
    /** `IntentSeekBarSize.DEFAULT`: 4dp growing to 6dp, thumb 4dp / 5dp. */
    Default(4.dp, 6.dp, 4.dp, 5.dp),

    /** `IntentSeekBarSize.SMALL`: 2dp growing to 10dp, thumb 3dp / 8dp. */
    Small(2.dp, 10.dp, 3.dp, 8.dp),

    /** `IntentSeekBarSize.LARGE`: 6dp growing to 10dp, thumb 5dp / 8dp. */
    Large(6.dp, 10.dp, 5.dp, 8.dp),
}

object IntentSliderDefaults {
    /** `coui_seekbar_progress_padding_horizontal`: the track inset from both ends. */
    val HorizontalPadding = 14.dp

    /** `COUIIntentSeekBar.MIN_TOUCH_TARGET_HEIGHT_DP`: the bar is never shorter than this. */
    val MinHeight = 24.dp

    /**
     * `BlurBlendEffectRenderer.setupBlurBlend`: the background blurred by 30px (`TileMode.MIRROR`),
     * then `#66333333` LIGHTEN and `#4DFFFFFF` SOFT_LIGHT over it.
     */
    val BackdropSpec = MaterialBackdropSpec(
        blurRadiusPx = 30f,
        layer1 = Color(0x66333333),
        mode1 = BlendMode.Lighten,
        layer2 = Color(0x4DFFFFFF),
        mode2 = BlendMode.Softlight,
    )

    /**
     * The colors that follow the theme (`couiIntentSeekBarFollowSystemTheme = true`): the background
     * and the secondary progress in couiColorLabelQuaternary (#14000000 / #26FFFFFF), the progress in
     * couiColorLabelPrimary (#E6000000 / #E6FFFFFF) and the thumb in
     * `coui_seekbar_intent_thumb_color` (#FF000000 / #FFFFFFFF).
     */
    @Composable
    fun intentSliderColors(
        backgroundColor: Color = COUITheme.colorScheme.quarternaryNeutral,
        progressColor: Color = COUITheme.colorScheme.onBackground,
        secondaryProgressColor: Color = COUITheme.colorScheme.quarternaryNeutral,
        thumbColor: Color = COUITheme.colorScheme.onBackground.copy(alpha = 1f),
        thumbShadowColor: Color = COUITheme.colorScheme.sliderThumbShadow,
    ): IntentSliderColors = remember(backgroundColor, progressColor, secondaryProgressColor, thumbColor, thumbShadowColor) {
        IntentSliderColors(
            backgroundColor = backgroundColor,
            progressColor = progressColor,
            secondaryProgressColor = secondaryProgressColor,
            thumbColor = thumbColor,
            thumbShadowColor = thumbShadowColor,
        )
    }

    /**
     * The fixed colors for media content (`couiIntentSeekBarFollowSystemTheme = false`,
     * `COUIIntentSeekBar.getFixedColors`): white at `coui_seekbar_intent_background_color_fixed`
     * #4DFFFFFF, `coui_seekbar_intent_progress_color_fixed` #8AFFFFFF,
     * `coui_seekbar_intent_secondary_progress_color_fixed` #29FFFFFF and
     * `coui_seekbar_intent_thumb_color_fixed` #FFFFFFFF, the same in light and dark themes.
     */
    @Composable
    fun fixedColors(
        backgroundColor: Color = Color(0x4DFFFFFF),
        progressColor: Color = Color(0x8AFFFFFF),
        secondaryProgressColor: Color = Color(0x29FFFFFF),
        thumbColor: Color = Color(0xFFFFFFFF),
        thumbShadowColor: Color = COUITheme.colorScheme.sliderThumbShadow,
    ): IntentSliderColors = remember(backgroundColor, progressColor, secondaryProgressColor, thumbColor, thumbShadowColor) {
        IntentSliderColors(
            backgroundColor = backgroundColor,
            progressColor = progressColor,
            secondaryProgressColor = secondaryProgressColor,
            thumbColor = thumbColor,
            thumbShadowColor = thumbShadowColor,
        )
    }
}

/**
 * The colors of an [IntentSlider].
 *
 * @param backgroundColor The background capsule, the unplayed part of the track.
 * @param progressColor The progress, from the start to the current value.
 * @param secondaryProgressColor The secondary (buffered) progress drawn under the progress.
 * @param thumbColor The thumb.
 * @param thumbShadowColor The thumb shadow (`coui_seekbar_thumb_shadow_color`).
 */
@Immutable
data class IntentSliderColors(
    val backgroundColor: Color,
    val progressColor: Color,
    val secondaryProgressColor: Color,
    val thumbColor: Color,
    val thumbShadowColor: Color,
)
