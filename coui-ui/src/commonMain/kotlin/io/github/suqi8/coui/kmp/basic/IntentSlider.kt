// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
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
import io.github.suqi8.coui.kmp.effect.ConicLightMode
import io.github.suqi8.coui.kmp.effect.ConicLightShader
import io.github.suqi8.coui.kmp.effect.ConicLightState
import io.github.suqi8.coui.kmp.effect.LocalMaterialBackdrop
import io.github.suqi8.coui.kmp.effect.MaterialBackdropSpec
import io.github.suqi8.coui.kmp.effect.TouchMotionState
import io.github.suqi8.coui.kmp.shader.RuntimeShader
import io.github.suqi8.coui.kmp.shader.asBrush
import io.github.suqi8.coui.kmp.shader.isRuntimeShaderSupported
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.utils.rememberCouiHaptic
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
 * [playbackState] drives the playback visuals (`COUIIntentSeekBar.resolveVisualState`): while
 * [IntentSliderPlaybackState.LoadingInitial] a white light sweeps across the whole bar instead of
 * the progress, while [IntentSliderPlaybackState.LoadingBuffering] it sweeps from the progress to the
 * end (`LoadingEffectRenderer`). With [showThumb] a round thumb pops out while the bar is touched or
 * [IntentSliderPlaybackState.Paused] and disappears again when playback goes on
 * (`ThumbStateAnimationHandler`).
 *
 * With [followSystemTheme] off the bar takes the fixed white palette COUI uses over video and
 * artwork, and, when a [LocalMaterialBackdrop] provider is present, the COUI 17 blur-blend
 * background in place of its translucent fill (`BlurBlendEffectRenderer`). Over media, [conicLight]
 * turns on the COUI 17 conic light progress style (`IntentConicProgressRenderer` +
 * `ConicLightEffectRenderer`): the progress takes [IntentSliderColors.conicLightColor] with a white
 * gradient at its end, and a cone of light trails the progress end while playing or dragging.
 *
 * The bar fills the available width and is at least [IntentSliderDefaults.MinHeight] high.
 *
 * @param value The current value. Values outside [valueRange] are coerced into it.
 * @param onValueChange Called with the new value while the user drags, taps or flings the bar, and
 *   with the start of [valueRange] when a [loadingDragReturn] springs the progress back.
 * @param modifier The modifier to be applied to the bar.
 * @param enabled Whether the bar accepts input.
 * @param showThumb Whether to draw the thumb while the bar is touched or paused
 *   (`couiIntentSeekBarShowThumb`, off by default).
 * @param followThumb Whether a drag moves the progress to the finger (`couiSeekBarIsFollowThumb`).
 *   Off by default: the progress moves by the finger's travel from wherever it was.
 * @param followSystemTheme Whether the colors follow the theme (`couiIntentSeekBarFollowSystemTheme`,
 *   the default). When off, [colors] defaults to [IntentSliderDefaults.fixedColors], the
 *   [materialBackdrop] blur may replace the background fill and [conicLight] may apply.
 * @param materialBackdrop Whether the COUI 17 blur-blend background (`BlurBlendEffectRenderer`) may
 *   replace the background fill. COUI enables it only when the bar does not follow the theme and
 *   uses no custom colors, so it only applies with [followSystemTheme] off, and only when a
 *   [LocalMaterialBackdrop] provider is present; pass `false` when you supply custom [colors].
 * @param conicLight Whether to use the COUI 17 conic light progress style
 *   (`couiIntentSeekBarConicLight`, off by default). Like `isConicLightProgressStyle`, it only applies
 *   with [followSystemTheme] off. The light itself needs runtime shaders (Android 13+ and every Skia
 *   target) and is skipped elsewhere; the progress style is drawn everywhere.
 * @param loadingDragReturn Whether a drag that starts while
 *   [IntentSliderPlaybackState.LoadingInitial] shows the progress and, on release, springs it back to
 *   the start of [valueRange] (`setLoadingDragReturnEnabled`, `LoadingDragReturnHandler`).
 * @param playbackState The playback state (`couiIntentSeekBarPlaybackState`).
 * @param valueRange The range of values the bar can take.
 * @param secondaryValue The secondary (buffered) value, drawn under the progress; null hides it
 *   (`couiIntentSeekBarShowSecondaryProgress`).
 * @param onValueChangeFinished Called when the user has finished changing the value: at the end of a
 *   drag, a fling or a tap.
 * @param onLoadingDragReturnComplete Called when a [loadingDragReturn] spring has brought the
 *   progress back to the start (`OnLoadingDragReturnCompleteListener`).
 * @param size The track and thumb sizes ([IntentSliderSize], `couiIntentSeekBarSize`).
 * @param horizontalPadding The inset of the track from both ends of the bar
 *   (`couiSeekBarProgressPaddingHorizontal`); the thumb and the rubber band reach into it.
 * @param colors The [IntentSliderColors] of the bar.
 * @param hapticEffect The haptic feedback of the bar (`VibrationFeedbackHandler`, on by default
 *   through `couiSeekBarEnableVibrator`); see [Slider].
 */
@Composable
fun IntentSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showThumb: Boolean = false,
    followThumb: Boolean = false,
    followSystemTheme: Boolean = true,
    materialBackdrop: Boolean = true,
    conicLight: Boolean = false,
    loadingDragReturn: Boolean = false,
    playbackState: IntentSliderPlaybackState = IntentSliderPlaybackState.Idle,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    secondaryValue: Float? = null,
    onValueChangeFinished: (() -> Unit)? = null,
    onLoadingDragReturnComplete: (() -> Unit)? = null,
    size: IntentSliderSize = IntentSliderSize.Default,
    horizontalPadding: Dp = IntentSliderDefaults.HorizontalPadding,
    colors: IntentSliderColors = if (followSystemTheme) IntentSliderDefaults.intentSliderColors() else IntentSliderDefaults.fixedColors(),
    hapticEffect: SliderDefaults.SliderHapticEffect = SliderDefaults.SliderHapticEffect.Step,
) {
    require(valueRange.start < valueRange.endInclusive) { "valueRange start should be less than end" }

    val density = LocalDensity.current
    val couiHaptic = rememberCouiHaptic()
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val scope = rememberCoroutineScope()
    val onValueChangeState by rememberUpdatedState(onValueChange)
    val onValueChangeFinishedState by rememberUpdatedState(onValueChangeFinished)
    val onLoadingDragReturnCompleteState by rememberUpdatedState(onLoadingDragReturnComplete)

    val coercedValue = value.coerceIn(valueRange.start, valueRange.endInclusive)
    val state = remember(scope) { IntentSliderState(scope, coercedValue) }
    val feedback = remember { SeekBarFeedbackState() }
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
    val paused = playbackState == IntentSliderPlaybackState.Paused
    LaunchedEffect(touched, paused, showThumb, thumbRadiusPx, thumbMaxRadiusPx) {
        state.animateThumb(touched, paused, showThumb, thumbRadiusPx, thumbMaxRadiusPx)
    }

    // COUIIntentSeekBar.resolveVisualState.
    val visualState = resolveIntentVisualState(
        dragging = state.isDragging,
        loadingReturnInProgress = state.draggingDuringLoading || state.loadingReturning,
        playbackState = playbackState,
    )
    LaunchedEffect(visualState) { state.loading.onVisualStateChanged(visualState) }

    // isConicLightProgressStyle: couiIntentSeekBarConicLight && !followSystemTheme; the system
    // material switches it also checks (isConicLightCapabilitySupported) are on on ColorOS 17.
    val conicStyle = conicLight && !followSystemTheme
    val conicMode = if (conicStyle) {
        when (visualState) {
            IntentVisualState.PlayingConeLight -> ConicLightMode.Playing
            IntentVisualState.DraggingConeLight -> ConicLightMode.Dragging
            else -> ConicLightMode.Off
        }
    } else {
        ConicLightMode.Off
    }
    LaunchedEffect(conicStyle, conicMode) {
        if (conicStyle) {
            state.conic.transitionTo(conicMode, size.conicPlayW0, size.conicDragW0, size.conicDragHeight)
        } else {
            // refreshConicLightActivation / setFollowSystemTheme(true): resetToOff.
            state.conic.resetToOff()
        }
    }
    val conicShader = remember(conicStyle) {
        if (conicStyle && isRuntimeShaderSupported()) RuntimeShader(ConicLightShader.Source) else null
    }
    // IntentConicProgressRenderer: the white gradient at the progress end fades in while playing or
    // dragging (startGradientAlphaAnim, response 0.5, bounce 0).
    val playing = playbackState == IntentSliderPlaybackState.Playing
    val gradientVisible = conicStyle && (playing || state.isDragging)
    LaunchedEffect(gradientVisible) {
        state.gradientAlpha.animateTo(if (gradientVisible) 1f else 0f, IntentGradientAlphaAnimationSpec)
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
    val currentLoadingDragReturn by rememberUpdatedState(loadingDragReturn)
    val currentPlaybackState by rememberUpdatedState(playbackState)

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
        feedback.perform(
            fraction = valueToIntentFraction(newValue, currentValueRange),
            sections = 0,
            dragging = state.isDragging,
            effect = currentHapticEffect,
            haptic = couiHaptic,
        )
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
                                    // COUIIntentSeekBar.onTouchEvent ACTION_DOWN: a new touch cancels a
                                    // running drag return and, while loading, starts a loading drag.
                                    state.cancelLoadingReturn()
                                    if (currentLoadingDragReturn && currentPlaybackState == IntentSliderPlaybackState.LoadingInitial) {
                                        state.draggingDuringLoading = true
                                    }
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
                                    // A touch that never dragged leaves the loading drag mode again.
                                    if (!state.isDragging) state.draggingDuringLoading = false
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
                                feedback.reset(valueToIntentFraction(currentValue, currentValueRange), sections = 0)
                                if (currentFollowThumb) {
                                    report(fractionToValue(positionToFraction(offset.x)), IntentFollowHandAnimationSpec)
                                }
                            },
                            onDragStopped = { velocity ->
                                state.isDragging = false
                                val length = state.width - 2f * currentPadding
                                if (state.draggingDuringLoading) {
                                    // LoadingDragReturnHandler.startReturnAnimation: no fling, the
                                    // progress springs back to the start.
                                    state.draggingDuringLoading = false
                                    onValueChangeFinishedState?.invoke()
                                    val start = currentValueRange.start
                                    state.startLoadingReturn(
                                        start = start,
                                        alreadyAtStart = currentValue <= start,
                                        latestValue = { currentValue },
                                        onReturn = {
                                            state.pendingSpec = null
                                            onValueChangeState(start)
                                        },
                                        onComplete = { onLoadingDragReturnCompleteState?.invoke() },
                                    )
                                } else if (length > 0f && abs(velocity) >= INTENT_FLING_MIN_VELOCITY) {
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
            visualState = visualState,
            drawBackground = backdropModifier == null,
            enabled = enabled,
            isRtl = isRtl,
            playing = playing,
            conicStyle = conicStyle,
            conicShader = conicShader,
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
 * Draws the bar like `COUISeekBarCore.onDraw`: `COUIIntentSeekBar.drawBackground` (skipped when the
 * blur-blend backdrop replaces it), `drawActiveTrack` (the loading sweeps, the secondary and the
 * primary progress clipped to the capsule, then the unclipped thumb, `IntentThumbRenderer`), then
 * the `POST_DRAW` effects (`ConicLightEffectRenderer`).
 */
@Composable
private fun IntentSliderTrack(
    state: IntentSliderState,
    valueRange: ClosedFloatingPointRange<Float>,
    secondaryValue: Float?,
    colors: IntentSliderColors,
    visualState: IntentVisualState,
    drawBackground: Boolean,
    enabled: Boolean,
    isRtl: Boolean,
    playing: Boolean,
    conicStyle: Boolean,
    conicShader: RuntimeShader?,
    trackHeightPx: Float,
    maxTrackHeightPx: Float,
    paddingPx: Float,
    modifier: Modifier = Modifier,
) {
    val clip = remember { Path() }
    // The gradients are drawn in a unit-wide space scaled to their length, so their shaders never
    // change with the geometry and are created once.
    val loadingBrush = remember { FixedShaderBrush(loadingSweepShader()) }
    val conicGradientBrush = remember(isRtl) { FixedShaderBrush(conicGradientShader(isRtl)) }
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

        // LoadingEffectParams.gradientLengthRatio: the sweep is 0.6 of the track long.
        val sweepLength = LOADING_GRADIENT_LENGTH_RATIO * length

        // LoadingEffectRenderer.drawInitialLoading: the sweep crosses the whole track.
        fun drawInitialSweep() {
            val travel = (length + sweepLength) * state.loading.initial.value.coerceIn(0f, 1f)
            val start = if (isRtl) right - travel else left - sweepLength + travel
            drawUnitGradient(loadingBrush, start, sweepLength, top, height, radius)
        }

        val fraction = valueToIntentFraction(state.visualValue.value, valueRange)
        val progressWidth = fraction * length
        val progressEnd = if (isRtl) right - progressWidth else left + progressWidth
        clip.reset()
        clip.addRoundRect(RoundRect(left, top, right, top + height, corner))

        if (visualState == IntentVisualState.LoadingFull) {
            // drawActiveTrack LOADING_FULL: only the sweep, no progress and no thumb.
            clipPath(clip) { drawInitialSweep() }
        } else {
            clipPath(clip) {
                if (visualState == IntentVisualState.LoadingPartial) {
                    // LoadingEffectRenderer.drawBufferingLoading: the sweep runs from the progress
                    // end to the track end, under the progress; no secondary progress.
                    val remaining = length - progressWidth
                    val travel = (remaining + sweepLength) * state.loading.buffering.value.coerceIn(0f, 1f)
                    val start = if (isRtl) progressEnd - travel - sweepLength else progressEnd + travel - sweepLength
                    drawUnitGradient(loadingBrush, start, sweepLength, top, height, radius)
                } else if (secondaryValue != null) {
                    // SecondaryProgressRenderer.
                    drawFill(colors.secondaryProgressColor, valueToIntentFraction(secondaryValue, valueRange))
                }
                if (conicStyle) {
                    // IntentConicProgressRenderer.drawConicStyleProgress: the progress in the conic
                    // light color, then a white gradient over its end, a fifth of the track while
                    // playing and a tenth while dragging (or the whole progress near the start).
                    drawFill(colors.conicLightColor, fraction)
                    val gradientLength = when {
                        playing -> if (fraction > CONIC_GRADIENT_PLAY_THRESHOLD) length / CONIC_GRADIENT_PLAY_DIVISOR else progressWidth
                        state.isDragging -> if (fraction > CONIC_GRADIENT_DRAG_THRESHOLD) length / CONIC_GRADIENT_DRAG_DIVISOR else progressWidth
                        else -> 0f
                    }
                    if (gradientLength > 0f) state.lastGradientLength = gradientLength
                    val drawnLength = if (gradientLength > 0f) gradientLength else state.lastGradientLength
                    val gradientAlpha = state.gradientAlpha.value
                    if (progressWidth > 0f && drawnLength > 0f && gradientAlpha > 0f) {
                        val start = if (isRtl) progressEnd else progressEnd - drawnLength
                        drawUnitGradient(conicGradientBrush, start, drawnLength, top, height, radius, gradientAlpha)
                    }
                } else {
                    // StandardProgressRenderer.
                    drawFill(colors.progressColor, fraction)
                }
            }
            // getMInitialTailActive: a stopped initial sweep finishes its pass over the track.
            if (state.loading.initialTailActive) clipPath(clip) { drawInitialSweep() }

            val thumbRadius = state.thumbRadius.value
            if (thumbRadius > 0f) {
                drawThumbWithShadow(
                    center = Offset(progressEnd, centerY),
                    radius = thumbRadius,
                    thumbColor = colors.thumbColor,
                    shadowColor = colors.thumbShadowColor,
                    shadowEnabled = enabled,
                )
            }
        }

        // ConicLightEffectRenderer.drawEffect (DrawPhase.POST_DRAW): a 450px high rect reaching
        // 300px past both track ends, centered on the bar, with BlendMode.SCREEN. A View draws
        // inside its own bounds, so the light is clipped to the bar.
        if (conicShader != null && state.conic.isVisible) {
            val conic = state.conic
            val lightWidth = length + 2f * ConicLightShader.HORIZONTAL_PADDING
            // The thumb position from the left end of the track (thumbPosition - progressStart).
            val process = (progressEnd - left) / length
            val heightScale = ConicLightShader.heightScale(process)
            ConicLightShader.setUniforms(
                shader = conicShader,
                width = lightWidth,
                process = process,
                trackLength = length,
                height = conic.baseHeight * heightScale,
                heightFalloff = conic.baseHeightFalloff * heightScale,
                alpha1 = conic.alpha1,
                alpha2 = conic.alpha2,
                w0 = conic.w0,
                coneK = conic.coneK,
                secondConeK = conic.secondConeK,
                isRtl = isRtl,
                color = colors.conicLightColor,
            )
            clipRect(0f, 0f, size.width, size.height) {
                translate(left - ConicLightShader.HORIZONTAL_PADDING, centerY - ConicLightShader.LIGHT_HEIGHT / 2f) {
                    drawRect(
                        brush = conicShader.asBrush(),
                        size = Size(lightWidth, ConicLightShader.LIGHT_HEIGHT),
                        blendMode = BlendMode.Screen,
                    )
                }
            }
        }
    }
}

/**
 * Draws a horizontal gradient capsule from [start] over [length] pixels: the unit-wide [brush] is
 * scaled to the length, and the corner radius is scaled back so the ends stay round.
 */
private fun DrawScope.drawUnitGradient(
    brush: Brush,
    start: Float,
    length: Float,
    top: Float,
    height: Float,
    radius: Float,
    alpha: Float = 1f,
) {
    if (length <= 0f) return
    withTransform({
        translate(left = start)
        scale(scaleX = length, scaleY = 1f, pivot = Offset.Zero)
    }) {
        drawRoundRect(
            brush = brush,
            topLeft = Offset(0f, top),
            size = Size(1f, height),
            cornerRadius = CornerRadius(radius / length, radius),
            alpha = alpha,
        )
    }
}

/** A [ShaderBrush] that hands out one shader whatever the size it is drawn at. */
private class FixedShaderBrush(private val shader: Shader) : ShaderBrush() {
    override fun createShader(size: Size): Shader = shader
}

/**
 * `LoadingEffectRenderer.ensureGradient`: transparent white, white at `gradientMidAlpha` 0.6 and
 * transparent white again at 0, 0.5 and 1 of a unit length.
 */
private fun loadingSweepShader(): Shader = LinearGradientShader(
    from = Offset.Zero,
    to = Offset(1f, 0f),
    colors = listOf(Color.White.copy(alpha = 0f), Color.White.copy(alpha = LOADING_GRADIENT_MID_ALPHA), Color.White.copy(alpha = 0f)),
    colorStops = listOf(0f, 0.5f, 1f),
)

/**
 * `IntentConicProgressRenderer.mCachedBGradient`: `colorBStart` #00FFFFFF to `colorBEnd` #FFFFFFFF
 * towards the progress end, reversed in RTL, over a unit length.
 */
private fun conicGradientShader(isRtl: Boolean): Shader {
    val transparent = Color.White.copy(alpha = 0f)
    return LinearGradientShader(
        from = Offset.Zero,
        to = Offset(1f, 0f),
        colors = if (isRtl) listOf(Color.White, transparent) else listOf(transparent, Color.White),
    )
}

/** `COUIIntentSeekBar.VisualState`. */
private enum class IntentVisualState {
    Static,
    PlayingConeLight,
    DraggingConeLight,
    LoadingFull,
    LoadingPartial,
}

/**
 * `COUIIntentSeekBar.resolveVisualState`. The programmatic-seek and hold-to-speed-up cone light
 * overrides (`collapseConeLightForSeek`, `setHoldToSpeedUpConeLightActive`) are not ported.
 */
private fun resolveIntentVisualState(
    dragging: Boolean,
    loadingReturnInProgress: Boolean,
    playbackState: IntentSliderPlaybackState,
): IntentVisualState = when {
    dragging && (playbackState == IntentSliderPlaybackState.Playing || playbackState == IntentSliderPlaybackState.LoadingBuffering) -> IntentVisualState.DraggingConeLight

    loadingReturnInProgress -> IntentVisualState.Static

    else -> when (playbackState) {
        IntentSliderPlaybackState.Idle, IntentSliderPlaybackState.Paused -> IntentVisualState.Static
        IntentSliderPlaybackState.LoadingInitial -> IntentVisualState.LoadingFull
        IntentSliderPlaybackState.Playing -> IntentVisualState.PlayingConeLight
        IntentSliderPlaybackState.LoadingBuffering -> IntentVisualState.LoadingPartial
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

    /** `LoadingDragReturnHandler.isDraggingDuringLoading`. */
    var draggingDuringLoading by mutableStateOf(false)

    /** `LoadingDragReturnHandler.isReturning`. */
    var loadingReturning by mutableStateOf(false)
        private set

    /** `ConicLightEffectRenderer`. */
    val conic = ConicLightState()

    /** `LoadingEffectRenderer`. */
    val loading = IntentLoadingState(scope)

    /** `IntentConicProgressRenderer.mGradientAlpha`. */
    val gradientAlpha = Animatable(0f)

    /** `IntentConicProgressRenderer.mLastGradientLenB`: the gradient length kept while it fades out. */
    var lastGradientLength = 0f

    val velocityTracker = VelocityTracker()
    var width = 0f
    private var thumbInitialized = false
    var dragStartX = 0f
    var dragDelta = 0f
    var dragStartFraction = 0f
    var flingJob: Job? = null
    private var enlargeJob: Job? = null
    private var returnJob: Job? = null

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
        // The drag return owns the drawn value until it settles.
        if (loadingReturning) return
        val spec = pendingSpec
        if (spec == null || (isDragging && spec !== IntentFollowHandAnimationSpec)) {
            visualValue.snapTo(value)
        } else {
            visualValue.animateTo(value, spec)
            if (!isDragging) pendingSpec = null
        }
    }

    /** `LoadingDragReturnHandler.cancel`. */
    fun cancelLoadingReturn() {
        returnJob?.cancel()
        returnJob = null
        loadingReturning = false
        draggingDuringLoading = false
    }

    /**
     * `LoadingDragReturnHandler.startReturnAnimation`: the progress springs back to [start]
     * (response 0.3, bounce 0), then [onComplete] runs (`onReturnAnimComplete`). A progress already
     * at the start completes at once.
     */
    fun startLoadingReturn(
        start: Float,
        alreadyAtStart: Boolean,
        latestValue: () -> Float,
        onReturn: () -> Unit,
        onComplete: () -> Unit,
    ) {
        stopFling()
        returnJob?.cancel()
        if (alreadyAtStart) {
            onComplete()
            return
        }
        loadingReturning = true
        onReturn()
        returnJob = scope.launch {
            try {
                visualValue.animateTo(start, IntentLoadingReturnAnimationSpec)
            } finally {
                loadingReturning = false
            }
            // Draw whatever the caller made of the value meanwhile.
            visualValue.snapTo(latestValue())
            onComplete()
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

/**
 * `LoadingEffectRenderer`: the initial-loading and buffering sweeps, each a spring from 0 to 1
 * restarted whenever it settles (`onInitialSpringAnimEnd`, `startBuffering` end listener). Leaving
 * the initial loading lets the running sweep finish its pass (`mInitialTailActive`) unless it is
 * nearly done or a drag takes over (`stopInitialLoading`).
 */
@Stable
private class IntentLoadingState(private val scope: CoroutineScope) {
    /** `mInitialProgress`. */
    val initial = Animatable(0f)

    /** `mBufferingProgress`. */
    val buffering = Animatable(0f)

    /** `mInitialTailActive`. */
    var initialTailActive by mutableStateOf(false)
        private set

    private var lastVisualState = IntentVisualState.Static
    private var initialJob: Job? = null
    private var bufferingJob: Job? = null

    /** `COUIIntentSeekBar.onVisualStateChanged`. */
    fun onVisualStateChanged(visualState: IntentVisualState) {
        val old = lastVisualState
        if (old == visualState) return
        lastVisualState = visualState
        when (old) {
            IntentVisualState.LoadingFull -> stopInitial(immediate = visualState == IntentVisualState.DraggingConeLight)
            IntentVisualState.LoadingPartial -> stopBuffering()
            else -> Unit
        }
        when (visualState) {
            IntentVisualState.LoadingFull -> startInitial()
            IntentVisualState.LoadingPartial -> startBuffering()
            else -> Unit
        }
    }

    /** `startInitialLoading`: a finishing tail starts over from the beginning. */
    private fun startInitial() {
        if (initialJob?.isActive == true && !initialTailActive) return
        initialJob?.cancel()
        initialTailActive = false
        initialJob = scope.launch { loop(initial) }
    }

    /** `stopInitialLoading`. */
    private fun stopInitial(immediate: Boolean) {
        val velocity = initial.velocity
        initialJob?.cancel()
        if (immediate || initial.value >= TAIL_SKIP_PROGRESS) {
            initialTailActive = false
            initialJob = scope.launch { initial.snapTo(0f) }
            return
        }
        initialTailActive = true
        initialJob = scope.launch {
            initial.animateTo(1f, IntentLoadingAnimationSpec, velocity)
            initialTailActive = false
            initial.snapTo(0f)
        }
    }

    /** `startBuffering`: a running sweep keeps going. */
    private fun startBuffering() {
        if (bufferingJob?.isActive == true) return
        bufferingJob = scope.launch { loop(buffering) }
    }

    /** `stopBuffering`. */
    private fun stopBuffering() {
        bufferingJob?.cancel()
        bufferingJob = scope.launch { buffering.snapTo(0f) }
    }

    private suspend fun loop(progress: Animatable<Float, AnimationVector1D>) {
        while (true) {
            progress.snapTo(0f)
            progress.animateTo(1f, IntentLoadingAnimationSpec)
        }
    }
}

private fun valueToIntentFraction(value: Float, valueRange: ClosedFloatingPointRange<Float>): Float = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)

/** `ConicLightEffectRenderer.getPlayW0`: 0.008, small 0.002, large 0.012. */
private val IntentSliderSize.conicPlayW0: Float
    get() = when (this) {
        IntentSliderSize.Default -> 0.008f
        IntentSliderSize.Small -> 0.002f
        IntentSliderSize.Large -> 0.012f
    }

/** `ConicLightEffectRenderer.getDragW0`: 0.014, small and large 0.03. */
private val IntentSliderSize.conicDragW0: Float
    get() = if (this == IntentSliderSize.Default) 0.014f else 0.03f

/** `ConicLightEffectRenderer.getDragHeight`: 0.2, small and large 0.4. */
private val IntentSliderSize.conicDragHeight: Float
    get() = if (this == IntentSliderSize.Default) 0.2f else 0.4f

/** `COUIIntentSeekBar.MIN_ENLARGE_HEIGHT_DP`. */
private val IntentMinEnlargeHeight = 2.dp

private const val ENLARGE_EPSILON = 0.001f

/** `LoadingEffectParams.gradientLengthRatio`. */
private const val LOADING_GRADIENT_LENGTH_RATIO = 0.6f

/** `LoadingEffectParams.gradientMidAlpha`. */
private const val LOADING_GRADIENT_MID_ALPHA = 0.6f

/** `LoadingEffectRenderer.TAIL_SKIP_PROGRESS_EPSILON`. */
private const val TAIL_SKIP_PROGRESS = 0.99f

/** `IntentConicProgressRenderer.drawConicStyleProgress`: while playing past 0.2 the gradient is a fifth of the track. */
private const val CONIC_GRADIENT_PLAY_THRESHOLD = 0.2f
private const val CONIC_GRADIENT_PLAY_DIVISOR = 5f

/** `IntentConicProgressRenderer.drawConicStyleProgress`: while dragging past 0.1 the gradient is a tenth of the track. */
private const val CONIC_GRADIENT_DRAG_THRESHOLD = 0.1f
private const val CONIC_GRADIENT_DRAG_DIVISOR = 10f

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

/** `LoadingDragReturnHandler.SPRING_RESPONSE` 0.3, bounce 0. */
private val IntentLoadingReturnAnimationSpec = spring<Float>(dampingRatio = 1f, stiffness = 438.65f)

/** `COUIIntentSeekBarParams` initial-loading / buffering spring: response 0.7, bounce 0. */
private val IntentLoadingAnimationSpec = spring(dampingRatio = 1f, stiffness = 80.57f, visibilityThreshold = 0.0001f)

/** `IntentConicProgressRenderer.GRADIENT_ALPHA_SPRING_RESPONSE` 0.5, bounce 0. */
private val IntentGradientAlphaAnimationSpec = spring(dampingRatio = 1f, stiffness = 157.91f, visibilityThreshold = 0.001f)

private const val INTENT_FLING_MIN_VELOCITY = 100f

/** `COUISeekBarAdvanced.MAX_VELOCITY`. */
private const val INTENT_FLING_MAX_VELOCITY = 8000f

/** The playback state of an [IntentSlider] (`com.coui.appcompat.seekbar.PlaybackState`). */
enum class IntentSliderPlaybackState {
    /** `IDLE`: the plain bar. */
    Idle,

    /** `LOADING_INITIAL`: a light sweeps across the whole bar instead of the progress. */
    LoadingInitial,

    /** `PLAYING`: with the conic light style, the light trails the progress end. */
    Playing,

    /** `PAUSED`: the thumb (with `showThumb`) stays out at its rest radius. */
    Paused,

    /** `LOADING_BUFFERING`: a light sweeps from the progress end to the end of the bar. */
    LoadingBuffering,
}

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

    /** `COUIIntentSeekBar.DEFAULT_CONIC_LIGHT_COLOR` (-493674, #F87796). */
    val ConicLightColor = Color(0xFFF87796)

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
        progressColor: Color = COUITheme.colorScheme.labelPrimary,
        secondaryProgressColor: Color = COUITheme.colorScheme.quarternaryNeutral,
        thumbColor: Color = COUITheme.colorScheme.labelPrimary.copy(alpha = 1f),
        thumbShadowColor: Color = COUITheme.colorScheme.sliderThumbShadow,
        conicLightColor: Color = ConicLightColor,
    ): IntentSliderColors = remember(backgroundColor, progressColor, secondaryProgressColor, thumbColor, thumbShadowColor, conicLightColor) {
        IntentSliderColors(
            backgroundColor = backgroundColor,
            progressColor = progressColor,
            secondaryProgressColor = secondaryProgressColor,
            thumbColor = thumbColor,
            thumbShadowColor = thumbShadowColor,
            conicLightColor = conicLightColor,
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
        conicLightColor: Color = ConicLightColor,
    ): IntentSliderColors = remember(backgroundColor, progressColor, secondaryProgressColor, thumbColor, thumbShadowColor, conicLightColor) {
        IntentSliderColors(
            backgroundColor = backgroundColor,
            progressColor = progressColor,
            secondaryProgressColor = secondaryProgressColor,
            thumbColor = thumbColor,
            thumbShadowColor = thumbShadowColor,
            conicLightColor = conicLightColor,
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
 * @param conicLightColor The progress and the light of the conic light style
 *   (`couiIntentSeekBarConicLightColor`, `IntentConicProgressRenderer.colorA`).
 */
@Immutable
data class IntentSliderColors(
    val backgroundColor: Color,
    val progressColor: Color,
    val secondaryProgressColor: Color,
    val thumbColor: Color,
    val thumbShadowColor: Color,
    val conicLightColor: Color = IntentSliderDefaults.ConicLightColor,
)
