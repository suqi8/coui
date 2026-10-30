// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.annotation.IntRange
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import io.github.suqi8.coui.kmp.effect.SpotLightShader
import io.github.suqi8.coui.kmp.effect.SpotLightState
import io.github.suqi8.coui.kmp.effect.SpotLightType
import io.github.suqi8.coui.kmp.effect.TouchMotionState
import io.github.suqi8.coui.kmp.shader.RuntimeShader
import io.github.suqi8.coui.kmp.shader.asBrush
import io.github.suqi8.coui.kmp.shader.isRuntimeShaderSupported
import io.github.suqi8.coui.kmp.squircle.addSquircleRect
import io.github.suqi8.coui.kmp.squircle.drawSquircleRect
import io.github.suqi8.coui.kmp.squircle.isSquircleEnabled
import io.github.suqi8.coui.kmp.theme.COUIRoundCorner
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * A [Slider] component with COUI style.
 *
 * The interaction follows COUI 17 `COUISeekBar`: on touch down the track grows to 1.4x of its
 * height and the thumb to 8dp (with a slight bounce), a tap springs the progress to the touched
 * position, a fast release keeps the progress gliding (`couiSeekBarPhysicsEnable`, continuous
 * sliders only), and running into the maximum flashes a white highlight over the progress
 * (`GlitterEffectRenderer`). With [material] (the default) the pressed thumb turns to glass and
 * a touch spotlight follows the finger, the effects ColorOS 17 adds with its system material
 * switches on, as they are on real devices.
 *
 * Like `COUISeekBarCore.onMeasure`, the slider lays out at 1.4x of its track [height] (28dp by
 * default), so the pressed track fits inside its bounds; touches still register within Compose's
 * minimum touch target around it. While a finger drags, the whole slider follows it horizontally on
 * the COUI 17 touch-motion rubber band (`TouchMotionEffectRenderer`: up to 28dp, curve ratio 0.05)
 * and springs back on release.
 *
 * @param value The current value of the [Slider]. If outside of [valueRange] provided, value will be coerced to this range.
 * @param onValueChange The callback to be called when the value changes.
 * @param modifier The modifier to be applied to the [Slider].
 * @param enabled Whether the [Slider] is enabled.
 * @param valueRange Range of values that this slider can take. The passed [value] will be coerced to this range.
 * @param steps If positive, specifies the amount of discrete allowable values between the endpoints of [valueRange].
 *   For example, a range from 0 to 10 with 4 [steps] allows 4 values evenly distributed between 0 and 10 (i.e., 2, 4, 6, 8).
 *   If [steps] is 0, the slider will behave continuously and allow any value from the range. Must not be negative.
 * @param onValueChangeFinished Called when value change has ended. This should not be used to update the slider value
 *   (use [onValueChange] instead), but rather to know when the user has completed selecting a new value by ending a
 *   drag, a fling or a click.
 * @param reverseDirection Controls the direction of this slider. When false (default), slider increases from left to right.
 *   When true, slider increases from right to left (useful for RTL layouts or custom direction requirements).
 * @param height The rest height of the [Slider] track. While pressed, the inactive track
 *   enlarges to 1.4x of this height; the slider reserves that height in its layout.
 * @param colors The [SliderColors] of the [Slider].
 * @param hapticEffect The haptic effect of the [Slider].
 * @param showKeyPoints Whether to show the key points (step indicators) on the slider. Only works when [keyPoints] is not null.
 * @param keyPoints Custom key point values to display on the slider. If null, uses step positions from [steps] parameter.
 *   Values should be within [valueRange]. For example, for a range of 0f..100f, you might specify listOf(0f, 25f, 50f, 75f, 100f).
 * @param magnetThreshold The magnetic snap threshold as a fraction (0.0 to 1.0). When the slider value is within this
 *   distance from a key point, it will snap to that point. Default is 0.02 (2%). Only applies when [keyPoints] is set.
 * @param material Whether to draw the COUI 17 material effects: the glass thumb while pressed
 *   (`TexturedThumbRenderer`) and the `TYPE_OPAQUE_MEDIUM_1` touch spotlight. `true` by default:
 *   `COUISeekBar` follows the system spotlight switch (`onMaterialStateChange`), which is on on
 *   ColorOS 17 devices. Pass `false` to opt out. The spotlight needs runtime shaders (Android 13+
 *   and every Skia target) and is skipped elsewhere.
 */
@Composable
fun Slider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0) steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    reverseDirection: Boolean = false,
    height: Dp = SliderDefaults.MinHeight,
    colors: SliderColors = SliderDefaults.sliderColors(),
    hapticEffect: SliderDefaults.SliderHapticEffect = SliderDefaults.DefaultHapticEffect,
    showKeyPoints: Boolean = false,
    keyPoints: List<Float>? = null,
    magnetThreshold: Float = 0.02f,
    material: Boolean = true,
) {
    require(steps >= 0) { "steps should be >= 0" }
    require(valueRange.start < valueRange.endInclusive) { "valueRange start should be less than end" }

    val hapticFeedback = LocalHapticFeedback.current
    val layoutDirection = LocalLayoutDirection.current
    val effectiveReverseDirection = if (layoutDirection == LayoutDirection.Rtl) !reverseDirection else reverseDirection
    val onValueChangeState by rememberUpdatedState(onValueChange)
    val onValueChangeFinishedState by rememberUpdatedState(onValueChangeFinished)
    var dragOffset by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var isHoveringThumb by remember { mutableStateOf(false) }
    var layoutWidth by remember { mutableIntStateOf(0) }
    var containerHeight by remember { mutableIntStateOf(0) }
    // The geometry (cap inset, thumb hit radius) follows the rest track height; the layout
    // itself is TrackEnlargeScale times taller, like COUISeekBarCore.onMeasure.
    val trackHeight by rememberUpdatedState(with(LocalDensity.current) { height.roundToPx() })
    val hapticState = remember { SliderHapticState() }
    val interactionSource = remember { MutableInteractionSource() }
    val scope = rememberCoroutineScope()
    val motion = remember { SliderMotionState() }
    // TouchMotionEffectRenderer: only the rubber-band translation reaches the view (translateX).
    val touchMotion = remember { TouchMotionState().apply { deformEnabled = false } }
    LaunchedEffect(enabled) {
        if (!enabled) touchMotion.translationX.animateTo(0f)
    }

    val coercedValue = value.coerceIn(valueRange.start, valueRange.endInclusive)

    val animatedValueState = animateFloatAsState(
        targetValue = coercedValue,
        animationSpec = if (isDragging || motion.isFlinging) FollowHandAnimationSpec else ClickAnimationSpec,
    )
    val thumbEnlarged = motion.isPressed || isDragging || isHoveringThumb
    val thumbScaleState = animateFloatAsState(
        targetValue = if (thumbEnlarged) ThumbPressedScale else 1f,
        animationSpec = if (thumbEnlarged) ThumbEnlargeAnimationSpec else ThumbShrinkAnimationSpec,
    )
    val trackGrowState = rememberTrackEnlargeFraction(motion.isPressed || isDragging)
    val glassPressed = material && motion.isPressed
    LaunchedEffect(glassPressed, material) {
        if (material) motion.animateGlass(glassPressed) else motion.resetGlass()
    }
    val spotLightShader = remember(material) {
        if (material && isRuntimeShaderSupported()) RuntimeShader(SpotLightShader.Source) else null
    }
    val isDark = COUITheme.colorScheme.background.luminance() < 0.5f

    val stepFractions = remember(steps) { stepsToTickFractions(steps) }

    val keyPointFractions = remember(keyPoints, stepFractions, valueRange, showKeyPoints) {
        computeKeyPointFractions(keyPoints, stepFractions, valueRange, showKeyPoints)
    }

    val allKeyPointFractions = remember(keyPoints, stepFractions, valueRange) {
        computeAllKeyPointFractions(keyPoints, stepFractions, valueRange)
    }

    val fractionToValue = remember(valueRange, steps, stepFractions, allKeyPointFractions, magnetThreshold) {
        { fraction: Float ->
            resolveValueFromFraction(
                fraction = fraction,
                valueRange = valueRange,
                steps = steps,
                allKeyPointFractions = allKeyPointFractions,
                magnetThreshold = magnetThreshold,
            )
        }
    }

    // Read by the long-lived gesture and fling coroutines.
    val currentValue by rememberUpdatedState(coercedValue)
    val currentValueRange by rememberUpdatedState(valueRange)
    val currentReverse by rememberUpdatedState(effectiveReverseDirection)
    val currentFractionToValue by rememberUpdatedState(fractionToValue)
    val spotLightEnabled by rememberUpdatedState(spotLightShader != null)

    val reportValue: (Float) -> Unit = { newValue ->
        onValueChangeState(newValue)
        hapticState.handleHapticFeedback(
            newValue,
            valueRange,
            hapticEffect,
            hapticFeedback,
            allKeyPointFractions,
            hasCustomKeyPoints = keyPoints != null,
        )
    }

    val currentLayoutWidth = layoutWidth
    val currentTrackHeight = trackHeight

    Box(
        modifier = Modifier
            // TouchMotionEffectRenderer: the whole seek bar follows the finger on the rubber band.
            .graphicsLayer { translationX = touchMotion.translationX.value }
            .then(modifier)
            .heightIn(min = height * TrackEnlargeScale)
            .then(
                if (enabled) {
                    Modifier
                        .onSizeChanged {
                            layoutWidth = it.width
                            containerHeight = it.height
                            touchMotion.width = it.width.toFloat()
                            touchMotion.height = it.height.toFloat()
                        }
                        .pointerInput(effectiveReverseDirection, valueRange) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    val change = event.changes.last()

                                    if (event.type == PointerEventType.Exit ||
                                        event.type == PointerEventType.Release ||
                                        change.type != PointerType.Mouse
                                    ) {
                                        isHoveringThumb = false
                                        continue
                                    }

                                    val capInset = currentTrackHeight * TrackCapInsetRatio
                                    val availableWidth = (currentLayoutWidth - 2f * capInset).coerceAtLeast(0f)
                                    val hitRadius = capInset

                                    val position = change.position
                                    val fraction = (animatedValueState.value - valueRange.start) / (valueRange.endInclusive - valueRange.start)
                                    val effectiveFraction = if (effectiveReverseDirection) 1f - fraction else fraction
                                    val thumbX = capInset + effectiveFraction * availableWidth

                                    val isOver = abs(position.x - thumbX) <= hitRadius
                                    if (isHoveringThumb != isOver) {
                                        isHoveringThumb = isOver
                                    }
                                }
                            }
                        }
                        .pointerInput(Unit) {
                            detectSliderPress(
                                orientation = Orientation.Horizontal,
                                onPress = { position, uptimeMillis ->
                                    if (motion.stopFling()) onValueChangeFinishedState?.invoke()
                                    motion.press(position, uptimeMillis)
                                    with(touchMotion) { down(position) }
                                    if (spotLightEnabled) scope.launch { motion.spotLight.glow(position) }
                                },
                                onMove = { position, uptimeMillis ->
                                    motion.velocityTracker.addPosition(uptimeMillis, position)
                                    with(touchMotion) { move(position, scope) }
                                    if (spotLightEnabled) scope.launch { motion.spotLight.follow(position) }
                                },
                                onRelease = { tapPosition ->
                                    motion.isPressed = false
                                    val velocity = motion.velocityTracker.calculateVelocity()
                                    with(touchMotion) { release(velocity.x, velocity.y, scope) }
                                    scope.launch { motion.spotLight.extinguish() }
                                    if (tapPosition != null) {
                                        // COUISeekBarAdvanced.startClickTransitionAnim: the progress
                                        // springs to the tapped position.
                                        val visualFraction = horizontalVisualFraction(tapPosition.x, layoutWidth, trackHeight)
                                        val fractionForValue = if (currentReverse) 1f - visualFraction else visualFraction
                                        onValueChangeState(currentFractionToValue(fractionForValue))
                                        onValueChangeFinishedState?.invoke()
                                    }
                                },
                            )
                        }
                        .hoverable(
                            interactionSource = interactionSource,
                            enabled = enabled,
                        )
                        .draggable(
                            orientation = Orientation.Horizontal,
                            state = rememberDraggableState { dragAmount ->
                                dragOffset += dragAmount
                                val visualFraction = horizontalVisualFraction(dragOffset, layoutWidth, trackHeight)
                                val fractionForValue = if (effectiveReverseDirection) 1f - visualFraction else visualFraction
                                val calculatedValue = fractionToValue(fractionForValue)
                                reportValue(calculatedValue)
                                motion.onTrackTouch(
                                    fraction = valueToFraction(calculatedValue, valueRange),
                                    reverseDirection = effectiveReverseDirection,
                                    scope = scope,
                                )
                            },
                            onDragStarted = { offset ->
                                isDragging = true
                                dragOffset = offset.x
                                val visualFraction = horizontalVisualFraction(offset.x, layoutWidth, trackHeight)
                                val fractionForValue = if (effectiveReverseDirection) 1f - visualFraction else visualFraction
                                val calculatedValue = fractionToValue(fractionForValue)
                                onValueChangeState(calculatedValue)
                                hapticState.reset(calculatedValue)
                            },
                            onDragStopped = { velocity ->
                                isDragging = false
                                val capInset = trackHeight * TrackCapInsetRatio
                                val trackLength = layoutWidth - 2f * capInset
                                // COUISectionSeekBar turns the release fling off (couiSeekBarPhysicsEnable = false).
                                if (steps == 0 && trackLength > 0f && abs(velocity) >= FlingMinVelocity) {
                                    motion.flingJob = scope.launch {
                                        motion.isFlinging = true
                                        try {
                                            flingSliderTrack(
                                                startFraction = valueToFraction(currentValue, currentValueRange),
                                                velocity = velocity.coerceIn(-FlingMaxVelocity, FlingMaxVelocity),
                                                trackLength = trackLength,
                                                reverseDirection = currentReverse,
                                            ) { visualFraction, trackVelocity ->
                                                val fractionForValue = if (currentReverse) 1f - visualFraction else visualFraction
                                                val newValue = currentFractionToValue(fractionForValue)
                                                reportValue(newValue)
                                                motion.onFlingStep(valueToFraction(newValue, currentValueRange), trackVelocity, scope)
                                            }
                                            onValueChangeFinishedState?.invoke()
                                        } finally {
                                            motion.isFlinging = false
                                        }
                                    }
                                } else {
                                    onValueChangeFinishedState?.invoke()
                                }
                            },
                        )
                        .indication(interactionSource, null)
                } else {
                    Modifier
                },
            )
            .semantics {
                progressBarRangeInfo = ProgressBarRangeInfo(
                    coercedValue,
                    valueRange.start..valueRange.endInclusive,
                    if (steps > 0) steps else 0,
                )
                setProgress { target ->
                    val clamped = target.coerceIn(valueRange.start, valueRange.endInclusive)
                    onValueChangeState(clamped)
                    true
                }
            },
        contentAlignment = Alignment.CenterStart,
    ) {
        SliderTrack(
            backgroundColor = colors.backgroundColor(enabled),
            foregroundColor = colors.foregroundColor(enabled),
            thumbColor = colors.thumbColor(enabled),
            thumbShadowColor = colors.thumbShadowColor(),
            keyPointColor = colors.keyPointColor(enabled),
            keyPointForegroundColor = colors.keyPointForegroundColor(enabled),
            valueProvider = { animatedValueState.value },
            valueRange = valueRange,
            trackGrowProvider = { trackGrowState.value },
            isVertical = false,
            enabled = enabled,
            showKeyPoints = showKeyPoints,
            stepFractions = keyPointFractions,
            thumbScaleProvider = { thumbScaleState.value },
            reverseDirection = effectiveReverseDirection,
            modifier = Modifier.fillMaxWidth().height(height),
            effects = SliderTrackEffects(
                motion = motion,
                glass = material,
                spotLightShader = spotLightShader,
                isDark = isDark,
                containerHeight = containerHeight,
            ),
        )
    }
}

/**
 * A vertical [Slider] component with COUI style.
 *
 * @param value The current value of the [Slider]. If outside of [valueRange] provided, value will be coerced to this range.
 * @param onValueChange The callback to be called when the value changes.
 * @param modifier The modifier to be applied to the [Slider].
 * @param enabled Whether the [Slider] is enabled.
 * @param valueRange Range of values that this slider can take. The passed [value] will be coerced to this range.
 * @param steps If positive, specifies the amount of discrete allowable values between the endpoints of [valueRange].
 * @param onValueChangeFinished Called when value change has ended.
 * @param reverseDirection Controls the direction of this slider. When false (default), slider increases from bottom to top.
 *   When true, slider increases from top to bottom.
 * @param width The rest width of the vertical [Slider] track. While pressed, the inactive track
 *   enlarges to 1.4x of this width and is drawn beyond the layout bounds, following COUI behavior.
 * @param colors The [SliderColors] of the [Slider].
 * @param effect Whether to show the effect of the [Slider].
 * @param hapticEffect The haptic effect of the [Slider].
 * @param showKeyPoints Whether to show the key points (step indicators) on the slider. Only works when [keyPoints] is not null.
 * @param keyPoints Custom key point values to display on the slider. If null, uses step positions from [steps] parameter.
 *   Values should be within [valueRange].
 * @param magnetThreshold The magnetic snap threshold as a fraction (0.0 to 1.0). When the slider value is within this
 *   distance from a key point, it will snap to that point. Default is 0.02 (2%). Only applies when [keyPoints] is set.
 */
@Composable
fun VerticalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0) steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    reverseDirection: Boolean = false,
    width: Dp = SliderDefaults.MinHeight,
    colors: SliderColors = SliderDefaults.sliderColors(),
    effect: Boolean = false,
    hapticEffect: SliderDefaults.SliderHapticEffect = SliderDefaults.DefaultHapticEffect,
    showKeyPoints: Boolean = false,
    keyPoints: List<Float>? = null,
    magnetThreshold: Float = 0.02f,
) {
    require(steps >= 0) { "steps should be >= 0" }
    require(valueRange.start < valueRange.endInclusive) { "valueRange start should be less than end" }

    val hapticFeedback = LocalHapticFeedback.current
    val onValueChangeState by rememberUpdatedState(onValueChange)
    val onValueChangeFinishedState by rememberUpdatedState(onValueChangeFinished)
    var dragOffset by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }
    var isHoveringThumb by remember { mutableStateOf(false) }
    val hapticState = remember { SliderHapticState() }
    val interactionSource = remember { MutableInteractionSource() }
    var layoutWidth by remember { mutableIntStateOf(0) }
    var layoutHeight by remember { mutableIntStateOf(0) }

    val coercedValue = value.coerceIn(valueRange.start, valueRange.endInclusive)

    val animatedValueState = animateFloatAsState(
        targetValue = coercedValue,
        animationSpec = if (isDragging) FollowHandAnimationSpec else ClickAnimationSpec,
    )
    val thumbEnlarged = isPressed || isDragging || isHoveringThumb
    val thumbScaleState = animateFloatAsState(
        targetValue = if (thumbEnlarged) ThumbPressedScale else 1f,
        animationSpec = if (thumbEnlarged) ThumbEnlargeAnimationSpec else ThumbShrinkAnimationSpec,
    )
    val trackGrowState = rememberTrackEnlargeFraction(isPressed || isDragging)

    val stepFractions = remember(steps) { stepsToTickFractions(steps) }

    val keyPointFractions = remember(keyPoints, stepFractions, valueRange, showKeyPoints) {
        computeKeyPointFractions(keyPoints, stepFractions, valueRange, showKeyPoints)
    }

    val allKeyPointFractions = remember(keyPoints, stepFractions, valueRange) {
        computeAllKeyPointFractions(keyPoints, stepFractions, valueRange)
    }

    val fractionToValueVertical = remember(valueRange, steps, stepFractions, allKeyPointFractions, magnetThreshold) {
        { fraction: Float ->
            resolveValueFromFraction(
                fraction = fraction,
                valueRange = valueRange,
                steps = steps,
                allKeyPointFractions = allKeyPointFractions,
                magnetThreshold = magnetThreshold,
            )
        }
    }

    val currentReverse by rememberUpdatedState(reverseDirection)
    val currentFractionToValue by rememberUpdatedState(fractionToValueVertical)

    val currentLayoutWidth = layoutWidth
    val currentLayoutHeight = layoutHeight

    Box(
        modifier = modifier
            .then(
                if (enabled) {
                    Modifier
                        .onSizeChanged {
                            layoutWidth = it.width
                            layoutHeight = it.height
                        }
                        .pointerInput(reverseDirection, valueRange) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    val change = event.changes.last()

                                    if (event.type == PointerEventType.Exit ||
                                        event.type == PointerEventType.Release ||
                                        change.type != PointerType.Mouse
                                    ) {
                                        isHoveringThumb = false
                                        continue
                                    }

                                    val capInset = currentLayoutWidth * TrackCapInsetRatio
                                    val availableHeight = (currentLayoutHeight - 2f * capInset).coerceAtLeast(0f)
                                    val hitRadius = capInset

                                    val position = change.position
                                    val fraction =
                                        (animatedValueState.value - valueRange.start) / (valueRange.endInclusive - valueRange.start)
                                    val effectiveFraction = if (reverseDirection) fraction else 1f - fraction
                                    val thumbY = capInset + effectiveFraction * availableHeight

                                    val isOver = abs(position.y - thumbY) <= hitRadius
                                    if (isHoveringThumb != isOver) {
                                        isHoveringThumb = isOver
                                    }
                                }
                            }
                        }
                        .pointerInput(Unit) {
                            detectSliderPress(
                                orientation = Orientation.Vertical,
                                onPress = { _, _ -> isPressed = true },
                                onMove = { _, _ -> },
                                onRelease = { tapPosition ->
                                    isPressed = false
                                    if (tapPosition != null) {
                                        val visualFraction = verticalVisualFraction(tapPosition.y, layoutHeight, layoutWidth)
                                        val fractionForValue = if (currentReverse) visualFraction else 1f - visualFraction
                                        onValueChangeState(currentFractionToValue(fractionForValue))
                                        onValueChangeFinishedState?.invoke()
                                    }
                                },
                            )
                        }
                        .draggable(
                            orientation = Orientation.Vertical,
                            state = rememberDraggableState { dragAmount ->
                                dragOffset += dragAmount
                                val visualFraction = verticalVisualFraction(dragOffset, layoutHeight, layoutWidth)
                                val fractionForValue = if (reverseDirection) visualFraction else 1f - visualFraction
                                val calculatedValue = fractionToValueVertical(fractionForValue)
                                onValueChangeState(calculatedValue)
                                hapticState.handleHapticFeedback(
                                    calculatedValue,
                                    valueRange,
                                    hapticEffect,
                                    hapticFeedback,
                                    allKeyPointFractions,
                                    hasCustomKeyPoints = keyPoints != null,
                                )
                            },
                            onDragStarted = { offset ->
                                isDragging = true
                                dragOffset = offset.y
                                val visualFraction = verticalVisualFraction(offset.y, layoutHeight, layoutWidth)
                                val fractionForValue = if (reverseDirection) visualFraction else 1f - visualFraction
                                val calculatedValue = fractionToValueVertical(fractionForValue)
                                onValueChangeState(calculatedValue)
                                hapticState.reset(calculatedValue)
                            },
                            onDragStopped = {
                                isDragging = false
                                onValueChangeFinishedState?.invoke()
                            },
                        )
                        .indication(interactionSource, null)
                } else {
                    Modifier
                },
            )
            .semantics {
                progressBarRangeInfo = ProgressBarRangeInfo(
                    coercedValue,
                    valueRange.start..valueRange.endInclusive,
                    if (steps > 0) steps else 0,
                )
                setProgress { target ->
                    val clamped = target.coerceIn(valueRange.start, valueRange.endInclusive)
                    onValueChangeState(clamped)
                    true
                }
            },
        contentAlignment = Alignment.BottomCenter,
    ) {
        SliderTrack(
            backgroundColor = colors.backgroundColor(enabled),
            foregroundColor = colors.foregroundColor(enabled),
            thumbColor = colors.thumbColor(enabled),
            thumbShadowColor = colors.thumbShadowColor(),
            keyPointColor = colors.keyPointColor(enabled),
            keyPointForegroundColor = colors.keyPointForegroundColor(enabled),
            valueProvider = { animatedValueState.value },
            valueRange = valueRange,
            trackGrowProvider = { trackGrowState.value },
            isVertical = true,
            enabled = enabled,
            showKeyPoints = showKeyPoints,
            stepFractions = keyPointFractions,
            thumbScaleProvider = { thumbScaleState.value },
            reverseDirection = reverseDirection,
            modifier = Modifier.width(width).fillMaxHeight(),
        )
    }
}

/**
 * A [RangeSlider] component with COUI style.
 *
 * Range Sliders expand upon [Slider] using the same concepts but allow the user to select 2 values.
 * The two values are still bounded by the value range but they also cannot cross each other.
 *
 * @param value Current values of the RangeSlider. If either value is outside of [valueRange] provided, it will be coerced to this range.
 * @param onValueChange Lambda in which values should be updated.
 * @param modifier The modifier to be applied to the [RangeSlider].
 * @param enabled Whether the [RangeSlider] is enabled.
 * @param valueRange Range of values that Range Slider values can take. Passed [value] will be coerced to this range.
 * @param steps If positive, specifies the amount of discrete allowable values between the endpoints of [valueRange].
 * @param onValueChangeFinished Lambda to be invoked when value change has ended.
 * @param height The rest height of the [RangeSlider] track. While pressed, the inactive track
 *   enlarges to 1.4x of this height; the range slider reserves that height in its layout.
 * @param colors The [SliderColors] of the [RangeSlider].
 * @param hapticEffect The haptic effect of the [RangeSlider].
 * @param showKeyPoints Whether to show the key points (step indicators) on the slider. Only works when [keyPoints] is not null.
 * @param keyPoints Custom key point values to display on the slider. If null, uses step positions from [steps] parameter.
 *   Values should be within [valueRange].
 * @param magnetThreshold The magnetic snap threshold as a fraction (0.0 to 1.0). When the slider value is within this
 *   distance from a key point, it will snap to that point. Default is 0.02 (2%). Only applies when [keyPoints] is set.
 */
@Composable
fun RangeSlider(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0) steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    height: Dp = SliderDefaults.MinHeight,
    colors: SliderColors = SliderDefaults.sliderColors(),
    hapticEffect: SliderDefaults.SliderHapticEffect = SliderDefaults.DefaultHapticEffect,
    showKeyPoints: Boolean = false,
    keyPoints: List<Float>? = null,
    magnetThreshold: Float = 0.02f,
) {
    require(steps >= 0) { "steps should be >= 0" }
    require(valueRange.start < valueRange.endInclusive) { "valueRange start should be less than end" }

    val hapticFeedback = LocalHapticFeedback.current
    val layoutDirection = LocalLayoutDirection.current
    val isRtl = layoutDirection == LayoutDirection.Rtl
    val onValueChangeState by rememberUpdatedState(onValueChange)
    val onValueChangeFinishedState by rememberUpdatedState(onValueChangeFinished)
    var startDragOffset by remember { mutableFloatStateOf(0f) }
    var endDragOffset by remember { mutableFloatStateOf(0f) }
    var isDraggingStart by remember { mutableStateOf(false) }
    var isDraggingEnd by remember { mutableStateOf(false) }
    var isHoveringStartThumb by remember { mutableStateOf(false) }
    var isHoveringEndThumb by remember { mutableStateOf(false) }
    val isDragging by remember { derivedStateOf { isDraggingStart || isDraggingEnd } }
    val hapticState = remember { RangeSliderHapticState() }
    val interactionSource = remember { MutableInteractionSource() }
    var lastDraggedIsStart by remember { mutableStateOf(true) }
    var layoutWidth by remember { mutableIntStateOf(0) }
    // The geometry (cap inset, thumb hit radius) follows the rest track height; the layout
    // itself is TrackEnlargeScale times taller, like COUISeekBarCore.onMeasure.
    val trackHeight by rememberUpdatedState(with(LocalDensity.current) { height.roundToPx() })
    var isPressed by remember { mutableStateOf(false) }

    var currentStartValue by remember { mutableFloatStateOf(value.start) }
    var currentEndValue by remember { mutableFloatStateOf(value.endInclusive) }

    if (!isDragging) {
        currentStartValue = value.start
        currentEndValue = value.endInclusive
    }

    val coercedStart = currentStartValue.coerceIn(valueRange.start, valueRange.endInclusive)
    val coercedEnd = currentEndValue.coerceIn(valueRange.start, valueRange.endInclusive)

    val progressAnimationSpec = if (isDragging) FollowHandAnimationSpec else ClickAnimationSpec
    val animatedStartValueState = animateFloatAsState(coercedStart, progressAnimationSpec)
    val animatedEndValueState = animateFloatAsState(coercedEnd, progressAnimationSpec)
    val startThumbEnlarged = isDraggingStart || isPressed || isHoveringStartThumb
    val startThumbScaleState = animateFloatAsState(
        targetValue = if (startThumbEnlarged) ThumbPressedScale else 1f,
        animationSpec = if (startThumbEnlarged) ThumbEnlargeAnimationSpec else ThumbShrinkAnimationSpec,
    )
    val endThumbEnlarged = isDraggingEnd || isPressed || isHoveringEndThumb
    val endThumbScaleState = animateFloatAsState(
        targetValue = if (endThumbEnlarged) ThumbPressedScale else 1f,
        animationSpec = if (endThumbEnlarged) ThumbEnlargeAnimationSpec else ThumbShrinkAnimationSpec,
    )
    val trackGrowState = rememberTrackEnlargeFraction(isPressed || isDragging)

    val stepFractions = remember(steps) { stepsToTickFractions(steps) }

    val keyPointFractions = remember(keyPoints, stepFractions, valueRange, showKeyPoints) {
        computeKeyPointFractions(keyPoints, stepFractions, valueRange, showKeyPoints)
    }

    val allKeyPointFractions = remember(keyPoints, stepFractions, valueRange) {
        computeAllKeyPointFractions(keyPoints, stepFractions, valueRange)
    }

    val fractionToValueRange = remember(valueRange, steps, stepFractions, allKeyPointFractions, magnetThreshold) {
        { fraction: Float ->
            resolveValueFromFraction(
                fraction = fraction,
                valueRange = valueRange,
                steps = steps,
                allKeyPointFractions = allKeyPointFractions,
                magnetThreshold = magnetThreshold,
            )
        }
    }

    val currentLayoutWidth = layoutWidth
    val currentTrackHeight = trackHeight

    Box(
        modifier = modifier
            .heightIn(min = height * TrackEnlargeScale)
            .then(
                if (enabled) {
                    Modifier
                        .onSizeChanged {
                            layoutWidth = it.width
                        }
                        .pointerInput(isRtl, valueRange) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    val change = event.changes.last()

                                    if (event.type == PointerEventType.Exit ||
                                        event.type == PointerEventType.Release ||
                                        change.type != PointerType.Mouse
                                    ) {
                                        isHoveringStartThumb = false
                                        isHoveringEndThumb = false
                                        continue
                                    }

                                    val capInset = currentTrackHeight * TrackCapInsetRatio
                                    val availableWidth = (currentLayoutWidth - 2f * capInset).coerceAtLeast(0f)
                                    val hitRadius = capInset

                                    val position = change.position
                                    val startFraction = (animatedStartValueState.value - valueRange.start) / (valueRange.endInclusive - valueRange.start)
                                    val endFraction = (animatedEndValueState.value - valueRange.start) / (valueRange.endInclusive - valueRange.start)
                                    val effectiveStartFraction = if (isRtl) 1f - startFraction else startFraction
                                    val effectiveEndFraction = if (isRtl) 1f - endFraction else endFraction
                                    val startThumbX = capInset + effectiveStartFraction * availableWidth
                                    val endThumbX = capInset + effectiveEndFraction * availableWidth

                                    val isOverStart = abs(position.x - startThumbX) <= hitRadius
                                    val isOverEnd = abs(position.x - endThumbX) <= hitRadius

                                    if (isHoveringStartThumb != isOverStart) {
                                        isHoveringStartThumb = isOverStart
                                    }
                                    if (isHoveringEndThumb != isOverEnd) {
                                        isHoveringEndThumb = isOverEnd
                                    }
                                }
                            }
                        }
                        .pointerInput(Unit) {
                            detectSliderPress(
                                orientation = Orientation.Horizontal,
                                onPress = { _, _ -> isPressed = true },
                                onMove = { _, _ -> },
                                onRelease = { isPressed = false },
                            )
                        }
                        .hoverable(interactionSource = interactionSource, enabled = enabled)
                        .draggable(
                            orientation = Orientation.Horizontal,
                            state = rememberDraggableState { dragAmount ->
                                if (isDraggingStart) {
                                    lastDraggedIsStart = true
                                    val tentativeStartOffset = startDragOffset + dragAmount
                                    val visualFractionStart = horizontalVisualFraction(tentativeStartOffset, layoutWidth, trackHeight)
                                    val fractionForValue = if (isRtl) 1f - visualFractionStart else visualFractionStart
                                    val newStart = fractionToValueRange(fractionForValue).coerceAtMost(currentEndValue)
                                    val crossCondition = if (isRtl) dragAmount < 0f else dragAmount > 0f

                                    if (newStart >= currentEndValue && crossCondition && currentStartValue == currentEndValue) {
                                        isDraggingStart = false
                                        isDraggingEnd = true

                                        endDragOffset = tentativeStartOffset
                                        hapticState.resetEnd(currentEndValue)
                                        hapticState.inheritEndKeyPoint()

                                        val visualFractionEnd = horizontalVisualFraction(endDragOffset, layoutWidth, trackHeight)
                                        val fractionForValueEnd = if (isRtl) 1f - visualFractionEnd else visualFractionEnd
                                        val newEnd = fractionToValueRange(fractionForValueEnd).coerceAtLeast(currentStartValue)
                                        currentEndValue = newEnd
                                        onValueChangeState(currentStartValue..newEnd)
                                        hapticState.handleEndHapticFeedback(
                                            newEnd,
                                            valueRange,
                                            hapticEffect,
                                            hapticFeedback,
                                            allKeyPointFractions,
                                            hasCustomKeyPoints = keyPoints != null,
                                        )
                                    } else {
                                        startDragOffset = tentativeStartOffset
                                        currentStartValue = newStart
                                        onValueChangeState(newStart..currentEndValue)
                                        hapticState.handleStartHapticFeedback(
                                            newStart,
                                            valueRange,
                                            hapticEffect,
                                            hapticFeedback,
                                            allKeyPointFractions,
                                            hasCustomKeyPoints = keyPoints != null,
                                        )
                                    }
                                } else if (isDraggingEnd) {
                                    lastDraggedIsStart = false
                                    val tentativeEndOffset = endDragOffset + dragAmount
                                    val visualFractionEnd = horizontalVisualFraction(tentativeEndOffset, layoutWidth, trackHeight)
                                    val fractionForValue = if (isRtl) 1f - visualFractionEnd else visualFractionEnd
                                    val newEnd = fractionToValueRange(fractionForValue).coerceAtLeast(currentStartValue)
                                    val crossCondition = if (isRtl) dragAmount > 0f else dragAmount < 0f

                                    if (newEnd <= currentStartValue && crossCondition && currentStartValue == currentEndValue) {
                                        isDraggingEnd = false
                                        isDraggingStart = true
                                        startDragOffset = tentativeEndOffset
                                        hapticState.resetStart(currentStartValue)
                                        hapticState.inheritStartKeyPoint()

                                        val visualFractionStart = horizontalVisualFraction(startDragOffset, layoutWidth, trackHeight)
                                        val fractionForValueStart = if (isRtl) 1f - visualFractionStart else visualFractionStart
                                        val newStart = fractionToValueRange(fractionForValueStart).coerceAtMost(currentEndValue)
                                        currentStartValue = newStart
                                        onValueChangeState(newStart..currentEndValue)
                                        hapticState.handleStartHapticFeedback(
                                            newStart,
                                            valueRange,
                                            hapticEffect,
                                            hapticFeedback,
                                            allKeyPointFractions,
                                            hasCustomKeyPoints = keyPoints != null,
                                        )
                                    } else {
                                        endDragOffset = tentativeEndOffset
                                        currentEndValue = newEnd
                                        onValueChangeState(currentStartValue..newEnd)
                                        hapticState.handleEndHapticFeedback(
                                            newEnd,
                                            valueRange,
                                            hapticEffect,
                                            hapticFeedback,
                                            allKeyPointFractions,
                                            hasCustomKeyPoints = keyPoints != null,
                                        )
                                    }
                                }
                            },
                            onDragStarted = { offset ->
                                val capInset = trackHeight * TrackCapInsetRatio
                                val availableWidth = (layoutWidth - 2f * capInset).coerceAtLeast(0f)
                                val startFraction =
                                    (currentStartValue - valueRange.start) / (valueRange.endInclusive - valueRange.start)
                                val endFraction =
                                    (currentEndValue - valueRange.start) / (valueRange.endInclusive - valueRange.start)
                                val effectiveStartFraction = if (isRtl) 1f - startFraction else startFraction
                                val effectiveEndFraction = if (isRtl) 1f - endFraction else endFraction
                                val startPos = capInset + effectiveStartFraction * availableWidth
                                val endPos = capInset + effectiveEndFraction * availableWidth

                                val hitRadius = capInset
                                val isOnStartThumb = abs(offset.x - startPos) <= hitRadius
                                val isOnEndThumb = abs(offset.x - endPos) <= hitRadius

                                when {
                                    isOnStartThumb && !isOnEndThumb -> {
                                        isDraggingStart = true
                                        startDragOffset = offset.x
                                        hapticState.resetStart(coercedStart)
                                    }

                                    !isOnStartThumb && isOnEndThumb -> {
                                        isDraggingEnd = true
                                        endDragOffset = offset.x
                                        hapticState.resetEnd(coercedEnd)
                                    }

                                    isOnStartThumb && isOnEndThumb -> {
                                        if (lastDraggedIsStart) {
                                            isDraggingStart = true
                                            startDragOffset = offset.x
                                            hapticState.resetStart(coercedStart)
                                        } else {
                                            isDraggingEnd = true
                                            endDragOffset = offset.x
                                            hapticState.resetEnd(coercedEnd)
                                        }
                                    }

                                    else -> {
                                        val diffStart = abs(offset.x - startPos)
                                        val diffEnd = abs(offset.x - endPos)
                                        if (diffStart <= diffEnd) {
                                            isDraggingStart = true
                                            startDragOffset = offset.x
                                            hapticState.resetStart(coercedStart)
                                        } else {
                                            isDraggingEnd = true
                                            endDragOffset = offset.x
                                            hapticState.resetEnd(coercedEnd)
                                        }
                                    }
                                }
                            },
                            onDragStopped = {
                                isDraggingStart = false
                                isDraggingEnd = false
                                onValueChangeFinishedState?.invoke()
                            },
                        )
                        .indication(interactionSource, null)
                } else {
                    Modifier
                },
            )
            .semantics {
                stateDescription = "$coercedStart-$coercedEnd"
            },
        contentAlignment = Alignment.CenterStart,
    ) {
        RangeSliderTrack(
            backgroundColor = colors.backgroundColor(enabled),
            foregroundColor = colors.foregroundColor(enabled),
            thumbColor = colors.thumbColor(enabled),
            thumbShadowColor = colors.thumbShadowColor(),
            keyPointColor = colors.keyPointColor(enabled),
            keyPointForegroundColor = colors.keyPointForegroundColor(enabled),
            valueStartProvider = { animatedStartValueState.value },
            valueEndProvider = { animatedEndValueState.value },
            startThumbScaleProvider = { startThumbScaleState.value },
            endThumbScaleProvider = { endThumbScaleState.value },
            valueRange = valueRange,
            trackGrowProvider = { trackGrowState.value },
            enabled = enabled,
            showKeyPoints = showKeyPoints,
            stepFractions = keyPointFractions,
            isRtl = isRtl,
            modifier = Modifier.fillMaxWidth().height(height),
        )
    }
}

/**
 * The effect layers [SliderTrack] draws for a horizontal [Slider].
 *
 * @param motion The press, fling and effect animation state.
 * @param glass Whether the pressed thumb turns to glass (`TexturedThumbRenderer`).
 * @param spotLightShader The `TYPE_OPAQUE_MEDIUM_1` touch spotlight shader, or null when it is not drawn.
 * @param isDark Whether the theme is dark, selecting the spotlight preset.
 * @param containerHeight The height of the box the pointer positions are measured in, in pixels.
 */
@Stable
private data class SliderTrackEffects(
    val motion: SliderMotionState,
    val glass: Boolean,
    val spotLightShader: RuntimeShader?,
    val isDark: Boolean,
    val containerHeight: Int,
)

/**
 * Internal slider track renderer, following the COUISeekBarCore.onDraw rendering model: the
 * background capsule, the progress capsule, the key points, the thumb, then the glitter and the
 * touch spotlight on top.
 */
@Composable
private fun SliderTrack(
    backgroundColor: Color,
    foregroundColor: Color,
    thumbColor: Color,
    thumbShadowColor: Color,
    keyPointColor: Color,
    keyPointForegroundColor: Color,
    valueProvider: () -> Float,
    valueRange: ClosedFloatingPointRange<Float>,
    trackGrowProvider: () -> Float,
    isVertical: Boolean,
    enabled: Boolean,
    showKeyPoints: Boolean,
    stepFractions: FloatArray,
    thumbScaleProvider: () -> Float,
    reverseDirection: Boolean,
    modifier: Modifier = Modifier,
    effects: SliderTrackEffects? = null,
) {
    // COUISeekBarCore draws the background and the progress as smooth round rects of radius
    // height / 2 without a weight (drawInactiveTrack / StandardProgressRenderer): the soft capsule.
    val squircleEnabled = isSquircleEnabled()
    Canvas(modifier = modifier) {
        val barHeight = size.height
        val barWidth = size.width
        val value = valueProvider()
        val thumbScale = thumbScaleProvider()
        val trackGrowFraction = trackGrowProvider()
        val fraction = (value - valueRange.start) / (valueRange.endInclusive - valueRange.start)

        if (isVertical) {
            val trackWidth = barWidth
            val backgroundWidth = lerp(trackWidth, trackWidth * TrackEnlargeScale, trackGrowFraction)
            val capInset = trackWidth * TrackCapInsetRatio
            val availableHeight = (barHeight - 2f * capInset).coerceAtLeast(0f)
            val effectiveFraction = if (reverseDirection) fraction else (1f - fraction)
            val thumbCenterY = capInset + effectiveFraction * availableHeight
            val centerX = barWidth / 2f

            drawSquircleRect(
                color = backgroundColor,
                cornerRadius = backgroundWidth / 2f,
                topLeft = Offset(centerX - backgroundWidth / 2f, capInset - backgroundWidth / 2f),
                size = Size(backgroundWidth, availableHeight + backgroundWidth),
                weight = COUIRoundCorner.UnspecifiedWeight,
                squircleEnabled = squircleEnabled,
            )

            val halfTrack = trackWidth / 2f
            val progressTop = if (reverseDirection) capInset - halfTrack else thumbCenterY - halfTrack
            val progressBottom = if (reverseDirection) thumbCenterY + halfTrack else barHeight - capInset + halfTrack
            drawRoundRect(
                color = foregroundColor,
                topLeft = Offset(centerX - halfTrack, progressTop),
                size = Size(trackWidth, (progressBottom - progressTop).coerceAtLeast(0f)),
                cornerRadius = CornerRadius(halfTrack),
            )

            if (showKeyPoints && stepFractions.isNotEmpty()) {
                val keyPointRadius = SliderDefaults.KeyPointRadius.toPx()
                for (i in stepFractions.indices) {
                    val stepFraction = stepFractions[i]
                    val effectiveStep = if (reverseDirection) stepFraction else (1f - stepFraction)
                    val y = capInset + effectiveStep * availableHeight
                    val isSelected = if (reverseDirection) y <= thumbCenterY else y >= thumbCenterY
                    val kpColor = if (isSelected) keyPointForegroundColor else keyPointColor
                    drawCircle(
                        color = kpColor,
                        radius = keyPointRadius,
                        center = Offset(centerX, y),
                    )
                }
            }
            drawThumbWithShadow(
                center = Offset(centerX, thumbCenterY),
                radius = trackWidth * ThumbRadiusRatio * thumbScale,
                thumbColor = thumbColor,
                shadowColor = thumbShadowColor,
                shadowEnabled = enabled,
            )
        } else {
            val trackHeight = barHeight
            val backgroundHeight = lerp(trackHeight, trackHeight * TrackEnlargeScale, trackGrowFraction)
            val capInset = trackHeight * TrackCapInsetRatio
            val availableWidth = (barWidth - 2f * capInset).coerceAtLeast(0f)
            val effectiveFraction = if (reverseDirection) 1f - fraction else fraction
            val thumbCenterX = capInset + effectiveFraction * availableWidth
            val centerY = barHeight / 2f
            val backgroundTopLeft = Offset(capInset - backgroundHeight / 2f, centerY - backgroundHeight / 2f)
            val backgroundSize = Size(availableWidth + backgroundHeight, backgroundHeight)

            drawSquircleRect(
                color = backgroundColor,
                cornerRadius = backgroundHeight / 2f,
                topLeft = backgroundTopLeft,
                size = backgroundSize,
                weight = COUIRoundCorner.UnspecifiedWeight,
                squircleEnabled = squircleEnabled,
            )

            val halfTrack = trackHeight / 2f
            val progressLeft = if (reverseDirection) thumbCenterX - halfTrack else capInset - halfTrack
            val progressRight = if (reverseDirection) barWidth - capInset + halfTrack else thumbCenterX + halfTrack
            val progressSize = Size((progressRight - progressLeft).coerceAtLeast(0f), trackHeight)
            drawSquircleRect(
                color = foregroundColor,
                cornerRadius = halfTrack,
                topLeft = Offset(progressLeft, centerY - halfTrack),
                size = progressSize,
                weight = COUIRoundCorner.UnspecifiedWeight,
                squircleEnabled = squircleEnabled,
            )

            if (showKeyPoints && stepFractions.isNotEmpty()) {
                val keyPointRadius = SliderDefaults.KeyPointRadius.toPx()
                for (i in stepFractions.indices) {
                    val stepFraction = stepFractions[i]
                    val effectiveStep = if (reverseDirection) 1f - stepFraction else stepFraction
                    val x = capInset + effectiveStep * availableWidth
                    val isSelected = if (reverseDirection) x >= thumbCenterX else x <= thumbCenterX
                    val kpColor = if (isSelected) keyPointForegroundColor else keyPointColor
                    drawCircle(
                        color = kpColor,
                        radius = keyPointRadius,
                        center = Offset(x, centerY),
                    )
                }
            }

            val thumbCenter = Offset(thumbCenterX, centerY)
            val thumbRadius = trackHeight * ThumbRadiusRatio * thumbScale
            val textureAlpha = if (effects != null && effects.glass) effects.motion.texture.value else 0f
            if (textureAlpha > 0f) {
                drawGlassThumb(
                    center = thumbCenter,
                    radius = thumbRadius,
                    textureAlpha = textureAlpha,
                    solidAlpha = effects!!.motion.solidThumb.value,
                )
            } else {
                drawThumbWithShadow(
                    center = thumbCenter,
                    radius = thumbRadius,
                    thumbColor = thumbColor,
                    shadowColor = thumbShadowColor,
                    shadowEnabled = enabled,
                )
            }

            if (effects == null) return@Canvas

            // GlitterEffectRenderer: a white gradient over the progress, brightest at the end the
            // progress ran into.
            val glitterAlpha = sliderGlitterAlpha(effects.motion.glitter.value)
            if (glitterAlpha > 0f && progressSize.width > 0f) {
                drawSquircleRect(
                    brush = Brush.horizontalGradient(
                        colors = if (reverseDirection) GlitterColorsReversed else GlitterColors,
                        startX = progressLeft,
                        endX = progressRight,
                    ),
                    cornerRadius = halfTrack,
                    topLeft = Offset(progressLeft, centerY - halfTrack),
                    size = progressSize,
                    weight = COUIRoundCorner.UnspecifiedWeight,
                    alpha = glitterAlpha,
                    squircleEnabled = squircleEnabled,
                )
            }

            // SpotLightEffectRenderer: DrawPhase.OVERLAY, clipped to the background capsule.
            val spotLightShader = effects.spotLightShader
            val glow = effects.motion.spotLight.progress.value
            if (spotLightShader != null && glow > 0f) {
                val light = effects.motion.spotLight.position.value
                val pointerOffsetY = (effects.containerHeight - barHeight) / 2f
                SpotLightShader.setUniforms(
                    shader = spotLightShader,
                    lightX = light.x,
                    lightY = light.y - pointerOffsetY,
                    radius = SpotLightShader.radius(glow, effects.motion.spotLight.extinguishing),
                    intensity = SpotLightType.OpaqueMedium1.endIntensity(effects.isDark) * glow,
                )
                val clip = Path().apply {
                    addSquircleRect(
                        rect = Rect(backgroundTopLeft, backgroundSize),
                        cornerRadius = backgroundHeight / 2f,
                        weight = COUIRoundCorner.UnspecifiedWeight,
                        squircleEnabled = squircleEnabled,
                    )
                }
                clipPath(clip) {
                    drawRect(
                        brush = spotLightShader.asBrush(),
                        topLeft = backgroundTopLeft,
                        size = backgroundSize,
                        blendMode = SpotLightType.OpaqueMedium1.blendMode(effects.isDark),
                    )
                }
            }
        }
    }
}

/**
 * Internal range slider track renderer, sharing the COUISeekBar rendering model with [SliderTrack].
 */
@Composable
private fun RangeSliderTrack(
    backgroundColor: Color,
    foregroundColor: Color,
    thumbColor: Color,
    thumbShadowColor: Color,
    keyPointColor: Color,
    keyPointForegroundColor: Color,
    valueStartProvider: () -> Float,
    valueEndProvider: () -> Float,
    startThumbScaleProvider: () -> Float,
    endThumbScaleProvider: () -> Float,
    valueRange: ClosedFloatingPointRange<Float>,
    trackGrowProvider: () -> Float,
    enabled: Boolean,
    showKeyPoints: Boolean,
    stepFractions: FloatArray,
    isRtl: Boolean,
    modifier: Modifier = Modifier,
) {
    val squircleEnabled = isSquircleEnabled()
    Canvas(modifier = modifier) {
        val barHeight = size.height
        val barWidth = size.width
        val valueStart = valueStartProvider()
        val valueEnd = valueEndProvider()
        val startThumbScale = startThumbScaleProvider()
        val endThumbScale = endThumbScaleProvider()
        val startFraction = (valueStart - valueRange.start) / (valueRange.endInclusive - valueRange.start)
        val endFraction = (valueEnd - valueRange.start) / (valueRange.endInclusive - valueRange.start)
        val trackHeight = barHeight
        val backgroundHeight = lerp(trackHeight, trackHeight * TrackEnlargeScale, trackGrowProvider())
        val capInset = trackHeight * TrackCapInsetRatio
        val availableWidth = (barWidth - 2f * capInset).coerceAtLeast(0f)
        val effectiveStartFraction = if (isRtl) 1f - startFraction else startFraction
        val effectiveEndFraction = if (isRtl) 1f - endFraction else endFraction
        val startX = capInset + effectiveStartFraction * availableWidth
        val endX = capInset + effectiveEndFraction * availableWidth

        val centerY = barHeight / 2f

        drawSquircleRect(
            color = backgroundColor,
            cornerRadius = backgroundHeight / 2f,
            topLeft = Offset(capInset - backgroundHeight / 2f, centerY - backgroundHeight / 2f),
            size = Size(availableWidth + backgroundHeight, backgroundHeight),
            weight = COUIRoundCorner.UnspecifiedWeight,
            squircleEnabled = squircleEnabled,
        )

        val halfTrack = trackHeight / 2f
        val rangeLeft = min(startX, endX)
        val rangeRight = max(startX, endX)
        drawSquircleRect(
            color = foregroundColor,
            cornerRadius = halfTrack,
            topLeft = Offset(rangeLeft - halfTrack, centerY - halfTrack),
            size = Size(rangeRight - rangeLeft + trackHeight, trackHeight),
            weight = COUIRoundCorner.UnspecifiedWeight,
            squircleEnabled = squircleEnabled,
        )

        if (showKeyPoints && stepFractions.isNotEmpty()) {
            val keyPointRadius = SliderDefaults.KeyPointRadius.toPx()
            for (i in stepFractions.indices) {
                val stepFraction = stepFractions[i]
                val effectiveStep = if (isRtl) 1f - stepFraction else stepFraction
                val x = capInset + effectiveStep * availableWidth
                val isSelected = x in rangeLeft..rangeRight
                val kpColor = if (isSelected) keyPointForegroundColor else keyPointColor
                drawCircle(
                    color = kpColor,
                    radius = keyPointRadius,
                    center = Offset(x, centerY),
                )
            }
        }

        drawThumbWithShadow(
            center = Offset(startX, centerY),
            radius = trackHeight * ThumbRadiusRatio * startThumbScale,
            thumbColor = thumbColor,
            shadowColor = thumbShadowColor,
            shadowEnabled = enabled,
        )
        drawThumbWithShadow(
            center = Offset(endX, centerY),
            radius = trackHeight * ThumbRadiusRatio * endThumbScale,
            thumbColor = thumbColor,
            shadowColor = thumbShadowColor,
            shadowEnabled = enabled,
        )
    }
}

/**
 * Observes, without consuming, each touch gesture on a slider as `COUISeekBarCore.onTouchEvent`
 * sees it: [onPress] on touch down, [onMove] for every move, and [onRelease] on lift or when a
 * cross-axis move hands the gesture to a scrolling parent. [onRelease] receives the lift position
 * when the pointer never left the touch slop (a tap), or null otherwise.
 */
internal suspend fun PointerInputScope.detectSliderPress(
    orientation: Orientation,
    onPress: (position: Offset, uptimeMillis: Long) -> Unit,
    onMove: (position: Offset, uptimeMillis: Long) -> Unit,
    onRelease: (tapPosition: Offset?) -> Unit,
) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        onPress(down.position, down.uptimeMillis)
        var withinSlop = true
        var tapPosition: Offset? = null
        while (true) {
            val change = awaitPointerEvent().changes.firstOrNull { it.id == down.id } ?: break
            if (change.changedToUpIgnoreConsumed()) {
                if (withinSlop) tapPosition = change.position
                break
            }
            onMove(change.position, change.uptimeMillis)
            if (withinSlop) {
                val delta = change.position - down.position
                if (delta.getDistance() > viewConfiguration.touchSlop) {
                    withinSlop = false
                    val crossAxis = if (orientation == Orientation.Horizontal) {
                        abs(delta.y) > abs(delta.x)
                    } else {
                        abs(delta.x) > abs(delta.y)
                    }
                    if (crossAxis) break
                }
            }
        }
        onRelease(tapPosition)
    }
}

/**
 * `PhysicsAnimationHandler` release fling: the progress glides on from [startFraction] at
 * [velocity] pixels per second, slowed by the `FlingBehavior` linear damping and stopped at either
 * end of the [trackLength] pixel track. [onStep] receives the visual fraction and velocity of
 * every frame.
 */
internal suspend fun flingSliderTrack(
    startFraction: Float,
    velocity: Float,
    trackLength: Float,
    reverseDirection: Boolean,
    onStep: (visualFraction: Float, velocity: Float) -> Unit,
) {
    val startVisual = if (reverseDirection) 1f - startFraction else startFraction
    val track = Animatable(startVisual * trackLength)
    track.updateBounds(0f, trackLength)
    track.animateDecay(velocity, FlingDecayAnimationSpec) {
        onStep(this.value / trackLength, this.velocity)
    }
}

/**
 * The press, fling and effect animation state of a [Slider], after `COUISeekBarAdvanced`.
 */
@Stable
private class SliderMotionState {
    /** Whether a pointer is down on the slider (`ACTION_DOWN` until `ACTION_UP` / `ACTION_CANCEL`). */
    var isPressed by mutableStateOf(false)

    /** Whether a release fling is moving the progress. */
    var isFlinging by mutableStateOf(false)

    var flingJob: Job? = null

    /** Tracks the pointer for the touch glitter, like `PhysicsAnimationHandler.velocityTracker`. */
    val velocityTracker = VelocityTracker()

    /** `GlitterEffectRenderer` spring value, the input of [sliderGlitterAlpha]. */
    val glitter = Animatable(0f)

    /** `TexturedThumbRenderer.mTextureAlpha`: the glass layers. */
    val texture = Animatable(0f)

    /** `TexturedThumbRenderer.mLayerCAlpha`: the solid white disc over the glass. */
    val solidThumb = Animatable(0f)

    val spotLight = SpotLightState()

    /** `SeekBarState.isBumpingEdges`: the glitter fires once per run into the maximum. */
    private var bumpingEdge = false
    private var lastFraction = 0f

    fun press(position: Offset, uptimeMillis: Long) {
        isPressed = true
        bumpingEdge = false
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

    /**
     * `COUISeekBarAdvanced.executeTouchGlitterEffectAnim`: dragging into the maximum while still
     * moving towards it flashes the glitter at the pointer speed.
     */
    fun onTrackTouch(fraction: Float, reverseDirection: Boolean, scope: CoroutineScope) {
        lastFraction = fraction
        if (fraction < 1f) {
            bumpingEdge = false
            return
        }
        if (bumpingEdge) return
        val velocityX = velocityTracker.calculateVelocity().x.coerceIn(-FlingMaxVelocity, FlingMaxVelocity)
        val towardsMax = if (reverseDirection) velocityX < 0f else velocityX > 0f
        if (towardsMax) startGlitter(scope, abs(velocityX))
    }

    /** `COUISeekBarAdvanced.executeFlingGlitterEffectAnim`: a fling running into the maximum. */
    fun onFlingStep(fraction: Float, velocity: Float, scope: CoroutineScope) {
        if (fraction >= 1f && lastFraction < 1f && !bumpingEdge) {
            startGlitter(scope, min(abs(velocity), FlingMaxVelocity))
        }
        lastFraction = fraction
    }

    private fun startGlitter(scope: CoroutineScope, velocity: Float) {
        bumpingEdge = true
        // GlitterEffectRenderer.startAnimation: spring back to 0, kicked by the speed.
        scope.launch { glitter.animateTo(0f, GlitterAnimationSpec, initialVelocity = velocity) }
    }

    /** `TexturedThumbRenderer.onPressDown` / `onPressUp`. */
    suspend fun animateGlass(pressed: Boolean) = coroutineScope {
        if (pressed) {
            launch { texture.animateTo(1f, GlassTextureAnimationSpec) }
            launch {
                solidThumb.snapTo(1f)
                solidThumb.animateTo(0f, GlassSolidFadeOutAnimationSpec)
            }
        } else {
            launch { texture.animateTo(0f, GlassTextureAnimationSpec) }
            launch { solidThumb.animateTo(1f, GlassSolidFadeInAnimationSpec) }
        }
    }

    /** `TexturedThumbRenderer.setEnabled(false)`. */
    suspend fun resetGlass() {
        texture.snapTo(0f)
        solidThumb.snapTo(0f)
    }
}

/**
 * The background enlarge animation of `COUISeekBarCore` (`touchAnim` / `releaseAnim`): the track
 * grows on touch down and shrinks on release. A release that comes before the track has grown
 * halfway (`getMinEnlargeHeight`) waits until it has, so a quick tap still shows the feedback.
 */
@Stable
private class TrackEnlargeState(private val scope: CoroutineScope) {
    /** 0 at rest, 1 fully enlarged. */
    val fraction = Animatable(0f)
    private var job: Job? = null

    fun update(enlarged: Boolean) {
        val pendingRelease = !enlarged &&
            fraction.isRunning &&
            fraction.targetValue == 1f &&
            fraction.value < TrackMinEnlargeFraction
        if (pendingRelease) {
            // Leave the running enlarge alone until it is halfway, then reverse it from its
            // current speed.
            job = scope.launch {
                snapshotFlow { fraction.value }.first { it >= TrackMinEnlargeFraction }
                fraction.animateTo(0f, TrackEnlargeAnimationSpec)
            }
            return
        }
        // Cancelling an animation resets the Animatable's velocity, so read it first.
        val velocity = fraction.velocity
        job?.cancel()
        job = scope.launch {
            fraction.animateTo(if (enlarged) 1f else 0f, TrackEnlargeAnimationSpec, velocity)
        }
    }
}

/**
 * Remembers the track enlarge fraction of a slider whose track is [enlarged] while pressed.
 */
@Composable
private fun rememberTrackEnlargeFraction(enlarged: Boolean): State<Float> {
    val scope = rememberCoroutineScope()
    val state = remember(scope) { TrackEnlargeState(scope) }
    LaunchedEffect(enlarged) { state.update(enlarged) }
    return state.fraction.asState()
}

/**
 * Manages haptic feedback state for the slider.
 */
@Stable
internal class SliderHapticState {
    private var edgeFeedbackTriggered: Boolean = false
    private var lastStep: Float = 0f
    private var isAtKeyPoint: Boolean = false

    fun reset(currentValue: Float) {
        edgeFeedbackTriggered = false
        lastStep = currentValue
        isAtKeyPoint = false
    }

    fun handleHapticFeedback(
        currentValue: Float,
        valueRange: ClosedFloatingPointRange<Float>,
        hapticEffect: SliderDefaults.SliderHapticEffect,
        hapticFeedback: HapticFeedback,
        keyPointFractions: FloatArray = floatArrayOf(),
        hasCustomKeyPoints: Boolean = false,
    ) {
        if (hapticEffect == SliderDefaults.SliderHapticEffect.None) return

        handleEdgeHaptic(currentValue, valueRange, hapticFeedback)

        if (hapticEffect == SliderDefaults.SliderHapticEffect.Step) {
            handleStepHaptic(currentValue, valueRange, hapticFeedback, keyPointFractions, hasCustomKeyPoints)
        }
    }

    private fun handleEdgeHaptic(
        currentValue: Float,
        valueRange: ClosedFloatingPointRange<Float>,
        hapticFeedback: HapticFeedback,
    ) {
        val isAtEdge = currentValue == valueRange.start || currentValue == valueRange.endInclusive
        if (isAtEdge && !edgeFeedbackTriggered) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
            edgeFeedbackTriggered = true
        } else if (!isAtEdge) {
            edgeFeedbackTriggered = false
        }
    }

    private fun handleStepHaptic(
        currentValue: Float,
        valueRange: ClosedFloatingPointRange<Float>,
        hapticFeedback: HapticFeedback,
        keyPointFractions: FloatArray,
        hasCustomKeyPoints: Boolean,
    ) {
        val isNotAtEdge = currentValue != valueRange.start && currentValue != valueRange.endInclusive

        if (hasCustomKeyPoints && keyPointFractions.isNotEmpty()) {
            handleKeyPointHaptic(currentValue, valueRange, hapticFeedback, keyPointFractions, isNotAtEdge)
        } else if (currentValue != lastStep && isNotAtEdge) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            lastStep = currentValue
        }
    }

    private fun handleKeyPointHaptic(
        currentValue: Float,
        valueRange: ClosedFloatingPointRange<Float>,
        hapticFeedback: HapticFeedback,
        keyPointFractions: FloatArray,
        isNotAtEdge: Boolean,
    ) {
        val fraction = (currentValue - valueRange.start) / (valueRange.endInclusive - valueRange.start)
        val threshold = 0.005f

        var nearestDist = Float.MAX_VALUE
        for (i in keyPointFractions.indices) {
            val dist = abs(keyPointFractions[i] - fraction)
            if (dist < nearestDist) nearestDist = dist
        }
        val currentlyAtKeyPoint = nearestDist < threshold

        if (currentlyAtKeyPoint && !isAtKeyPoint && isNotAtEdge) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }

        isAtKeyPoint = currentlyAtKeyPoint
    }
}

/**
 * Manages haptic feedback state for the range slider.
 */
@Stable
internal class RangeSliderHapticState {
    private var startEdgeFeedbackTriggered: Boolean = false
    private var endEdgeFeedbackTriggered: Boolean = false
    private var startLastStep: Float = 0f
    private var endLastStep: Float = 0f
    private var startIsAtKeyPoint: Boolean = false
    private var endIsAtKeyPoint: Boolean = false

    fun resetStart(currentValue: Float) {
        startEdgeFeedbackTriggered = false
        startLastStep = currentValue
        startIsAtKeyPoint = false
    }

    fun resetEnd(currentValue: Float) {
        endEdgeFeedbackTriggered = false
        endLastStep = currentValue
        endIsAtKeyPoint = false
    }

    fun inheritStartKeyPoint() {
        startIsAtKeyPoint = endIsAtKeyPoint
    }

    fun inheritEndKeyPoint() {
        endIsAtKeyPoint = startIsAtKeyPoint
    }

    fun handleStartHapticFeedback(
        currentValue: Float,
        valueRange: ClosedFloatingPointRange<Float>,
        hapticEffect: SliderDefaults.SliderHapticEffect,
        hapticFeedback: HapticFeedback,
        keyPointFractions: FloatArray = floatArrayOf(),
        hasCustomKeyPoints: Boolean = false,
    ) {
        handleHapticFeedbackInternal(
            currentValue = currentValue,
            valueRange = valueRange,
            hapticEffect = hapticEffect,
            hapticFeedback = hapticFeedback,
            keyPointFractions = keyPointFractions,
            edgeFeedbackTriggered = startEdgeFeedbackTriggered,
            lastStep = startLastStep,
            isAtKeyPoint = startIsAtKeyPoint,
            isStartEdge = true,
            hasCustomKeyPoints = hasCustomKeyPoints,
            onEdgeFeedbackUpdate = { startEdgeFeedbackTriggered = it },
            onLastStepUpdate = { startLastStep = it },
            onKeyPointUpdate = { startIsAtKeyPoint = it },
        )
    }

    fun handleEndHapticFeedback(
        currentValue: Float,
        valueRange: ClosedFloatingPointRange<Float>,
        hapticEffect: SliderDefaults.SliderHapticEffect,
        hapticFeedback: HapticFeedback,
        keyPointFractions: FloatArray = floatArrayOf(),
        hasCustomKeyPoints: Boolean = false,
    ) {
        handleHapticFeedbackInternal(
            currentValue = currentValue,
            valueRange = valueRange,
            hapticEffect = hapticEffect,
            hapticFeedback = hapticFeedback,
            keyPointFractions = keyPointFractions,
            edgeFeedbackTriggered = endEdgeFeedbackTriggered,
            lastStep = endLastStep,
            isAtKeyPoint = endIsAtKeyPoint,
            isStartEdge = false,
            hasCustomKeyPoints = hasCustomKeyPoints,
            onEdgeFeedbackUpdate = { endEdgeFeedbackTriggered = it },
            onLastStepUpdate = { endLastStep = it },
            onKeyPointUpdate = { endIsAtKeyPoint = it },
        )
    }

    private fun handleHapticFeedbackInternal(
        currentValue: Float,
        valueRange: ClosedFloatingPointRange<Float>,
        hapticEffect: SliderDefaults.SliderHapticEffect,
        hapticFeedback: HapticFeedback,
        keyPointFractions: FloatArray,
        edgeFeedbackTriggered: Boolean,
        lastStep: Float,
        isAtKeyPoint: Boolean,
        isStartEdge: Boolean,
        hasCustomKeyPoints: Boolean,
        onEdgeFeedbackUpdate: (Boolean) -> Unit,
        onLastStepUpdate: (Float) -> Unit,
        onKeyPointUpdate: (Boolean) -> Unit,
    ) {
        if (hapticEffect == SliderDefaults.SliderHapticEffect.None) return

        val targetEdge = if (isStartEdge) valueRange.start else valueRange.endInclusive
        val isAtEdge = currentValue == targetEdge

        if (isAtEdge && !edgeFeedbackTriggered) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
            onEdgeFeedbackUpdate(true)
        } else if (!isAtEdge) {
            onEdgeFeedbackUpdate(false)
        }

        if (hapticEffect == SliderDefaults.SliderHapticEffect.Step) {
            val isNotAtEdge = currentValue != targetEdge

            if (hasCustomKeyPoints && keyPointFractions.isNotEmpty()) {
                val fraction = (currentValue - valueRange.start) / (valueRange.endInclusive - valueRange.start)
                val threshold = 0.005f

                var nearestDist = Float.MAX_VALUE
                for (i in keyPointFractions.indices) {
                    val dist = abs(keyPointFractions[i] - fraction)
                    if (dist < nearestDist) nearestDist = dist
                }
                val currentlyAtKeyPoint = nearestDist < threshold

                if (currentlyAtKeyPoint && !isAtKeyPoint && isNotAtEdge) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }

                onKeyPointUpdate(currentlyAtKeyPoint)
            } else if (currentValue != lastStep && isNotAtEdge) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onLastStepUpdate(currentValue)
            }
        }
    }
}

private fun stepsToTickFractions(steps: Int): FloatArray = if (steps == 0) floatArrayOf() else FloatArray(steps + 2) { it.toFloat() / (steps + 1) }

private fun resolveValueFromFraction(
    fraction: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    allKeyPointFractions: FloatArray,
    magnetThreshold: Float,
): Float {
    val f = fraction.coerceIn(0f, 1f)
    val base = lerp(valueRange.start, valueRange.endInclusive, f)
    return when {
        steps > 0 -> {
            val stepCount = steps + 1
            val start = valueRange.start.toDouble()
            val end = valueRange.endInclusive.toDouble()
            val stepIndex = (f * stepCount).roundToInt().coerceIn(0, stepCount)
            (start + (end - start) * stepIndex / stepCount).toFloat()
        }

        allKeyPointFractions.isNotEmpty() -> {
            var closest = allKeyPointFractions[0]
            var bestDist = abs(closest - f)
            for (i in 1 until allKeyPointFractions.size) {
                val cand = allKeyPointFractions[i]
                val dist = abs(cand - f)
                if (dist < bestDist) {
                    bestDist = dist
                    closest = cand
                }
            }
            if (bestDist < magnetThreshold) {
                lerp(valueRange.start, valueRange.endInclusive, closest)
            } else {
                base
            }
        }

        else -> base
    }
}

/** The inactive track thickness multiplier while pressed (COUISeekBarCore DEFAULT_BACKGROUND_ENLARGE_SCALE). */
private val TrackEnlargeScale = 1.4f

/** Cap center inset as a fraction of track thickness (COUISeekBar mPaddingHorizontal). */
private val TrackCapInsetRatio = TrackEnlargeScale / 2f

/** COUISeekBar: coui_seekbar_thumb_radius (6dp) over track height (20dp). */
private val ThumbRadiusRatio = 0.3f

/** COUISeekBar: coui_seekbar_thumb_max_radius (8dp) over coui_seekbar_thumb_radius (6dp). */
private val ThumbPressedScale = 8f / 6f

// COUISpringForce maps response r and bounce b to stiffness (2π / r)² and damping ratio 1 - b.

/** COUISeekBarCore height spring (HEIGHT_SPRING_RESPONSE 0.5, bounce 0): the track enlarge and release. */
private val TrackEnlargeAnimationSpec = spring<Float>(dampingRatio = 1f, stiffness = 157.91f)

/** COUISeekBarCore.getMinEnlargeHeight: half of the enlarge must show before the track shrinks back. */
private val TrackMinEnlargeFraction = 0.5f

/** StandardAnimationHandler thumb radius spring growing: response 0.5, bounce 0.3. */
private val ThumbEnlargeAnimationSpec = spring<Float>(dampingRatio = 0.7f, stiffness = 157.91f)

/** StandardAnimationHandler thumb radius spring shrinking: response 0.5, bounce 0. */
private val ThumbShrinkAnimationSpec = spring<Float>(dampingRatio = 1f, stiffness = 157.91f)

/** ClickAnimationHandler: the progress springs to a tapped position (response 0.3, bounce 0). */
private val ClickAnimationSpec = spring<Float>(dampingRatio = 1f, stiffness = 438.65f)

/** FlexibleFollowHandHandler / drag transition: the progress follows the finger (response 0.15, bounce 0). */
private val FollowHandAnimationSpec = spring<Float>(dampingRatio = 1f, stiffness = 1754.6f)

/** PhysicsAnimationHandler.PHYSICAL_VELOCITY_LIMIT: slower releases do not fling (px/s). */
private val FlingMinVelocity = 100f

/** PhysicsAnimationHandler.MAX_VELOCITY (px/s). */
private val FlingMaxVelocity = 8000f

/**
 * PhysicsAnimationHandler FlingBehavior linear damping 15/s. [exponentialDecay] decays at
 * 4.2/s per unit of friction multiplier.
 */
private val FlingDecayAnimationSpec = exponentialDecay<Float>(frictionMultiplier = 15f / 4.2f)

/** GlitterEffectRenderer spring: response 0.6, bounce 0. */
private val GlitterAnimationSpec = spring<Float>(dampingRatio = 1f, stiffness = 109.66f)

/** coui_seekbar_glitter_effect_min_color (#00FFFFFF) to coui_seekbar_glitter_effect_max_color (#A0FFFFFF). */
private val GlitterColors = listOf(Color(0x00FFFFFF), Color(0xA0FFFFFF))
private val GlitterColorsReversed = GlitterColors.reversed()

/** TexturedThumbRenderer texture spring: response 0.3, bounce 0. */
private val GlassTextureAnimationSpec = spring<Float>(dampingRatio = 1f, stiffness = 438.65f)

/** TexturedThumbRenderer LAYER_C_SPRING_RESPONSE_IN (0.5): the solid disc fades out on press. */
private val GlassSolidFadeOutAnimationSpec = spring<Float>(dampingRatio = 1f, stiffness = 157.91f)

/** TexturedThumbRenderer LAYER_C_SPRING_RESPONSE_OUT (0.3): the solid disc returns on release. */
private val GlassSolidFadeInAnimationSpec = spring<Float>(dampingRatio = 1f, stiffness = 438.65f)

/** TexturedThumbRenderer layer A: white at alpha 140 at the top, 230 from 20% above the bottom. */
private val GlassGradientTop = Color(0x8CFFFFFF)
private val GlassGradientBottom = Color(0xE6FFFFFF)

/** TexturedThumbRenderer layer B fill. */
private val GlassFill = Color(0x77FFFFFF)

/** TexturedThumbRenderer layer B rim: a 10px stroke on the edge whose white shadow (radius 5px) glows inwards. */
private val GlassRimHalfStroke = 5f

/** The Android blur sigma of the 5px rim shadow radius (`0.57735 * radius + 0.5`). */
private val GlassRimSigma = 0.57735f * 5f + 0.5f

/** How far inside the edge the rim glow is drawn, in pixels. */
private val GlassRimReach = GlassRimHalfStroke + 3f * GlassRimSigma

/** COUISeekBar style: couiSeekBarThumbShadowSize = 4dp (shadow blur radius). */
private val ThumbShadowRadius = 4.dp

/** COUISeekBar: coui_seekbar_shadow_offset_y = 2dp. */
private val ThumbShadowOffsetY = 2.dp

/**
 * `GlitterEffectRenderer.computeGlitterEffectAlpha`: the glitter opacity for the spring value
 * [x], `1 - e^(-ln(85) / 360 * x)`.
 */
internal fun sliderGlitterAlpha(x: Float): Float {
    if (x <= 0f) return 0f
    return (1f - exp(-ln(85f) / 360f * x)).coerceIn(0f, 1f)
}

/**
 * The opacity of the glass thumb's rim glow [inset] pixels inside the thumb edge: the shadow of a
 * 10px stroke centred on the edge, blurred with the Android sigma of a 5px shadow radius.
 */
internal fun sliderGlassRimAlpha(inset: Float): Float {
    val outer = gaussianCdf((GlassRimHalfStroke - inset) / GlassRimSigma)
    val inner = gaussianCdf((-GlassRimHalfStroke - inset) / GlassRimSigma)
    return (outer - inner).coerceIn(0f, 1f)
}

/** The standard normal cumulative distribution, after Abramowitz and Stegun 7.1.26. */
private fun gaussianCdf(x: Float): Float {
    val z = abs(x) / sqrt(2f)
    val t = 1f / (1f + 0.3275911f * z)
    val poly = t * (0.25482959f + t * (-0.28449672f + t * (1.4214138f + t * (-1.4531521f + t * 1.0614054f))))
    val erf = 1f - poly * exp(-z * z)
    return if (x >= 0f) 0.5f * (1f + erf) else 0.5f * (1f - erf)
}

/**
 * Draws the slider thumb as a circle with a soft drop shadow (COUISeekBar.drawThumb);
 * the shadow is skipped when the slider is disabled.
 */
internal fun DrawScope.drawThumbWithShadow(
    center: Offset,
    radius: Float,
    thumbColor: Color,
    shadowColor: Color,
    shadowEnabled: Boolean,
) {
    if (shadowEnabled && radius > 0f) {
        val shadowRadius = radius + ThumbShadowRadius.toPx()
        val shadowCenter = Offset(center.x, center.y + ThumbShadowOffsetY.toPx())
        drawCircle(
            brush = Brush.radialGradient(
                0f to shadowColor,
                radius / shadowRadius to shadowColor,
                1f to shadowColor.copy(alpha = 0f),
                center = shadowCenter,
                radius = shadowRadius,
            ),
            radius = shadowRadius,
            center = shadowCenter,
        )
    }
    drawCircle(color = thumbColor, radius = radius, center = center)
}

/**
 * Draws the pressed glass thumb of `TexturedThumbRenderer`: a vertical white gradient (layer A)
 * and a translucent white disc with an inner rim glow (layer B), composited at [textureAlpha],
 * then the solid white disc (layer C) at [solidAlpha]. Lengths are raw pixels, as in the source.
 */
private fun DrawScope.drawGlassThumb(
    center: Offset,
    radius: Float,
    textureAlpha: Float,
    solidAlpha: Float,
) {
    if (radius <= 0f) return
    drawIntoCanvas { canvas ->
        canvas.saveLayer(Rect(center, radius), Paint().apply { alpha = textureAlpha.coerceIn(0f, 1f) })
    }
    drawCircle(
        brush = Brush.verticalGradient(
            0f to GlassGradientTop,
            0.8f to GlassGradientBottom,
            1f to GlassGradientBottom,
            startY = center.y - radius,
            endY = center.y + radius,
        ),
        radius = radius,
        center = center,
    )
    drawCircle(color = GlassFill, radius = radius, center = center)
    drawCircle(brush = glassRimBrush(center, radius), radius = radius, center = center)
    drawIntoCanvas { canvas -> canvas.restore() }
    if (solidAlpha > 0f) {
        drawCircle(color = Color.White, radius = radius, center = center, alpha = solidAlpha.coerceIn(0f, 1f))
    }
}

/** A radial white gradient sampling [sliderGlassRimAlpha] over the outer [GlassRimReach] pixels. */
private fun glassRimBrush(center: Offset, radius: Float): Brush {
    val innerRadius = (radius - GlassRimReach).coerceAtLeast(0f)
    val samples = 8
    val stops = ArrayList<Pair<Float, Color>>(samples + 2)
    if (innerRadius > 0f) stops += 0f to Color.White.copy(alpha = sliderGlassRimAlpha(radius))
    for (i in 0..samples) {
        val distance = innerRadius + (radius - innerRadius) * i / samples
        stops += distance / radius to Color.White.copy(alpha = sliderGlassRimAlpha(radius - distance))
    }
    return Brush.radialGradient(*stops.toTypedArray(), center = center, radius = radius)
}

private fun horizontalVisualFraction(offsetX: Float, sizeWidth: Int, sizeHeight: Int): Float {
    val capInset = sizeHeight * TrackCapInsetRatio
    val availableWidth = (sizeWidth - 2f * capInset).coerceAtLeast(0f)
    return if (availableWidth == 0f) 0f else ((offsetX - capInset) / availableWidth).coerceIn(0f, 1f)
}

private fun verticalVisualFraction(offsetY: Float, sizeHeight: Int, sizeWidth: Int): Float {
    val capInset = sizeWidth * TrackCapInsetRatio
    val availableHeight = (sizeHeight - 2f * capInset).coerceAtLeast(0f)
    return if (availableHeight == 0f) 0f else ((offsetY - capInset) / availableHeight).coerceIn(0f, 1f)
}

/** The position of [value] within [valueRange], from 0 to 1. */
private fun valueToFraction(value: Float, valueRange: ClosedFloatingPointRange<Float>): Float = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)

/**
 * Converts point values to normalized fractions within the value range.
 */
private fun pointsToFractions(
    points: List<Float>,
    valueRange: ClosedFloatingPointRange<Float>,
): FloatArray = points.map { point ->
    ((point - valueRange.start) / (valueRange.endInclusive - valueRange.start))
        .coerceIn(0f, 1f)
}.toFloatArray()

/**
 * Computes key point fractions for slider display.
 * Filters out points too close to edges.
 */
private fun computeKeyPointFractions(
    keyPoints: List<Float>?,
    stepFractions: FloatArray,
    valueRange: ClosedFloatingPointRange<Float>,
    showKeyPoints: Boolean,
): FloatArray = when {
    keyPoints != null -> pointsToFractions(keyPoints, valueRange)
    showKeyPoints -> stepFractions
    else -> floatArrayOf()
}

/**
 * Computes all key point fractions including edge points.
 * Used for haptic feedback and magnetic snapping.
 */
private fun computeAllKeyPointFractions(
    keyPoints: List<Float>?,
    stepFractions: FloatArray,
    valueRange: ClosedFloatingPointRange<Float>,
): FloatArray = when {
    keyPoints != null -> pointsToFractions(keyPoints, valueRange)
    stepFractions.isNotEmpty() -> stepFractions
    else -> floatArrayOf()
}

object SliderDefaults {
    /**
     * The minimum height of the [Slider] and [RangeSlider].
     */
    val MinHeight = 20.dp

    /**
     * The radius of the key points on the [Slider] and [RangeSlider].
     */
    val KeyPointRadius = 3.0.dp

    /**
     * The type of haptic feedback to be used for the slider.
     */
    enum class SliderHapticEffect {
        /** No haptic feedback. */
        None,

        /** Haptic feedback at 0% and 100%. */
        Edge,

        /** Haptic feedback at steps. */
        Step,
    }

    /**
     * The default haptic effect of the [Slider] and [RangeSlider].
     */
    val DefaultHapticEffect = SliderHapticEffect.Edge

    /**
     * The colors of the [Slider], [VerticalSlider] and [RangeSlider], after COUI 17
     * `COUISeekBar` / `COUISectionSeekBar`.
     *
     * @param foregroundColor The progress color (`?couiColorContainerTheme`).
     * @param disabledForegroundColor The progress color when disabled (`?couiColorDisable`).
     * @param backgroundColor The track color (`?couiColorContainer12`).
     * @param disabledBackgroundColor The track color when disabled; COUI keeps the enabled track color.
     * @param thumbColor The thumb color.
     * @param disabledThumbColor The thumb color when disabled (`coui_seekbar_thumb_disable_color`).
     * @param keyPointColor The key points outside the progress (`?couiColorLabelTertiary`).
     * @param keyPointForegroundColor The key points on the progress (`?couiColorLabelOnColor`).
     * @param disabledKeyPointColor The key points outside the progress when disabled
     *   (`coui_seekbar_inactive_mark_disable_color`).
     * @param disabledKeyPointForegroundColor The key points on the progress when disabled
     *   (`coui_seekbar_active_mark_disable_color`).
     * @param thumbShadowColor The thumb drop shadow (`coui_seekbar_thumb_shadow_color`), drawn only when enabled.
     */
    @Composable
    fun sliderColors(
        foregroundColor: Color = COUITheme.colorScheme.primary,
        disabledForegroundColor: Color = COUITheme.colorScheme.disabledPrimarySlider,
        backgroundColor: Color = COUITheme.colorScheme.sliderBackground,
        disabledBackgroundColor: Color = COUITheme.colorScheme.sliderBackground,
        thumbColor: Color = COUITheme.colorScheme.onPrimary,
        disabledThumbColor: Color = COUITheme.colorScheme.disabledSliderThumb,
        keyPointColor: Color = COUITheme.colorScheme.sliderKeyPoint,
        keyPointForegroundColor: Color = COUITheme.colorScheme.sliderKeyPointForeground,
        disabledKeyPointColor: Color = COUITheme.colorScheme.disabledSliderKeyPoint,
        disabledKeyPointForegroundColor: Color = COUITheme.colorScheme.disabledSliderThumb,
        thumbShadowColor: Color = COUITheme.colorScheme.sliderThumbShadow,
    ): SliderColors = remember(
        foregroundColor,
        disabledForegroundColor,
        backgroundColor,
        disabledBackgroundColor,
        thumbColor,
        disabledThumbColor,
        keyPointColor,
        keyPointForegroundColor,
        disabledKeyPointColor,
        disabledKeyPointForegroundColor,
        thumbShadowColor,
    ) {
        SliderColors(
            foregroundColor = foregroundColor,
            disabledForegroundColor = disabledForegroundColor,
            backgroundColor = backgroundColor,
            disabledBackgroundColor = disabledBackgroundColor,
            thumbColor = thumbColor,
            disabledThumbColor = disabledThumbColor,
            keyPointColor = keyPointColor,
            keyPointForegroundColor = keyPointForegroundColor,
            disabledKeyPointColor = disabledKeyPointColor,
            disabledKeyPointForegroundColor = disabledKeyPointForegroundColor,
            thumbShadowColor = thumbShadowColor,
        )
    }
}

@Immutable
data class SliderColors(
    private val foregroundColor: Color,
    private val disabledForegroundColor: Color,
    private val backgroundColor: Color,
    private val disabledBackgroundColor: Color,
    private val thumbColor: Color,
    private val disabledThumbColor: Color,
    private val keyPointColor: Color,
    private val keyPointForegroundColor: Color,
    private val disabledKeyPointColor: Color,
    private val disabledKeyPointForegroundColor: Color,
    private val thumbShadowColor: Color,
) {
    @Stable
    internal fun foregroundColor(enabled: Boolean): Color = if (enabled) foregroundColor else disabledForegroundColor

    @Stable
    internal fun backgroundColor(enabled: Boolean): Color = if (enabled) backgroundColor else disabledBackgroundColor

    @Stable
    internal fun thumbColor(enabled: Boolean): Color = if (enabled) thumbColor else disabledThumbColor

    @Stable
    internal fun keyPointColor(enabled: Boolean): Color = if (enabled) keyPointColor else disabledKeyPointColor

    @Stable
    internal fun keyPointForegroundColor(enabled: Boolean): Color = if (enabled) keyPointForegroundColor else disabledKeyPointForegroundColor

    @Stable
    internal fun thumbShadowColor(): Color = thumbShadowColor
}
