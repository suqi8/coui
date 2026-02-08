// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.basic

import androidx.annotation.IntRange
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.suqi8.coui.kmp.theme.COUITheme
import kotlin.math.abs

/**
 * A [Slider] component with COUI style, restored pixel-perfectly from ColorOS XML resources.
 *
 * Dimensions extracted from XML:
 * - Track Height: 20dp
 * - Thumb Radius: 6dp (Normal) -> 8dp (Pressed)
 * - Padding Horizontal: 14dp
 *
 * Colors mapped to [COUITheme.colorScheme]:
 * - Progress: [COUITheme.colorScheme.primary] (Default active color)
 * - Background: [Colors.seekbarBackground]
 * - Thumb: [Colors.seekbarThumb]
 * - Tick Mark: [Colors.seekbarTickMark]
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
    height: Dp = SliderDefaults.TrackHeight,
    colors: SliderColors = SliderDefaults.sliderColors(),
    hapticEffect: SliderDefaults.SliderHapticEffect = SliderDefaults.DefaultHapticEffect,
    showKeyPoints: Boolean = false,
    keyPoints: List<Float>? = null,
    magnetThreshold: Float = 0.03f
) {
    val hapticFeedback = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }

    var isDragging by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }

    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val currentOnValueChangeFinished by rememberUpdatedState(onValueChangeFinished)
    val currentValueRange by rememberUpdatedState(valueRange)
    val currentSteps by rememberUpdatedState(steps)
    val currentKeyPoints by rememberUpdatedState(keyPoints)
    val currentValue by rememberUpdatedState(value)

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> isPressed = true
                is PressInteraction.Release, is PressInteraction.Cancel -> isPressed = false
            }
        }
    }

    val targetTrackHeight = if (isDragging || isPressed) height * 1.4f else height
    val animatedTrackHeight by animateDpAsState(
        targetValue = targetTrackHeight,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy),
        label = "TrackHeightAnimation"
    )

    val targetThumbRadius =
        if (isDragging || isPressed) SliderDefaults.ThumbPressedRadius else SliderDefaults.ThumbRadius
    val animatedThumbRadius by animateDpAsState(
        targetValue = targetThumbRadius,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy),
        label = "ThumbRadiusAnimation"
    )

    val coercedValue = value.coerceIn(valueRange.start, valueRange.endInclusive)
    val stepFractions = remember(steps) { stepsToTickFractions(steps) }
    val allKeyPointFractions = remember(keyPoints, stepFractions, valueRange) {
        computeAllKeyPointFractions(keyPoints, stepFractions, valueRange)
    }

    val hapticState = remember { SliderHapticState() }

    fun calculateValueFromFraction(visualFraction: Float): Float {
        val fractionForValue = if (reverseDirection) 1f - visualFraction else visualFraction
        val f = fractionForValue.coerceIn(0f, 1f)
        val newValue = lerp(currentValueRange.start, currentValueRange.endInclusive, f)

        return when {
            currentSteps > 0 -> snapValueToTick(
                newValue,
                stepFractions,
                currentValueRange.start,
                currentValueRange.endInclusive
            )

            allKeyPointFractions.isNotEmpty() -> {
                val closestKeyPoint = allKeyPointFractions.minByOrNull { abs(it - f) }
                if (closestKeyPoint != null && abs(f - closestKeyPoint) < magnetThreshold) {
                    lerp(currentValueRange.start, currentValueRange.endInclusive, closestKeyPoint)
                } else newValue
            }

            else -> newValue
        }
    }

    Box(
        modifier = modifier
            .height(SliderDefaults.TouchTargetHeight)
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .then(
                if (enabled) {
                    Modifier
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragStart = { offset ->
                                    isDragging = true
                                    isPressed = true
                                    val width = size.width
                                    val visualFraction = (offset.x / width).coerceIn(0f, 1f)

                                    val calculatedValue = calculateValueFromFraction(visualFraction)
                                    currentOnValueChange(calculatedValue)
                                    hapticState.reset(calculatedValue)
                                },
                                onHorizontalDrag = { change, _ ->
                                    val width = size.width
                                    val visualFraction = (change.position.x / width).coerceIn(0f, 1f)

                                    val calculatedValue = calculateValueFromFraction(visualFraction)

                                    if (calculatedValue != currentValue) {
                                        currentOnValueChange(calculatedValue)
                                        hapticState.handleHapticFeedback(
                                            calculatedValue,
                                            currentValueRange,
                                            hapticEffect,
                                            hapticFeedback,
                                            allKeyPointFractions,
                                            currentKeyPoints != null
                                        )
                                    }
                                },
                                onDragEnd = {
                                    isDragging = false
                                    isPressed = false
                                    currentOnValueChangeFinished?.invoke()
                                },
                                onDragCancel = {
                                    isDragging = false
                                    isPressed = false
                                    currentOnValueChangeFinished?.invoke()
                                }
                            )
                        }
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = { offset ->
                                    isPressed = true
                                    val width = size.width
                                    val visualFraction = (offset.x / width).coerceIn(0f, 1f)
                                    val calculatedValue = calculateValueFromFraction(visualFraction)
                                    currentOnValueChange(calculatedValue)
                                    tryAwaitRelease()
                                    isPressed = false
                                    currentOnValueChangeFinished?.invoke()
                                }
                            )
                        }
                        .indication(interactionSource, LocalIndication.current)
                } else Modifier
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        SliderTrack(
            modifier = Modifier.fillMaxWidth(),
            backgroundHeight = animatedTrackHeight,
            progressHeight = height,
            currentThumbRadius = animatedThumbRadius,
            backgroundColor = colors.backgroundColor(),
            foregroundColor = colors.foregroundColor(enabled),
            thumbColor = colors.thumbColor(),
            thumbShadowColor = colors.thumbShadowColor(),
            value = coercedValue,
            valueRange = valueRange,
            reverseDirection = reverseDirection,
            showKeyPoints = showKeyPoints,
            keyPointsFractions = allKeyPointFractions,
            keyPointColor = colors.keyPointColor(),
            isDragging = isDragging
        )
    }
}

/**
 * A Vertical [Slider] component with COUI style.
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
    width: Dp = SliderDefaults.TrackHeight,
    colors: SliderColors = SliderDefaults.sliderColors(),
    hapticEffect: SliderDefaults.SliderHapticEffect = SliderDefaults.DefaultHapticEffect,
    showKeyPoints: Boolean = false,
    keyPoints: List<Float>? = null,
    magnetThreshold: Float = 0.03f
) {
    val hapticFeedback = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }

    var isDragging by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }

    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val currentOnValueChangeFinished by rememberUpdatedState(onValueChangeFinished)
    val currentValueRange by rememberUpdatedState(valueRange)
    val currentSteps by rememberUpdatedState(steps)
    val currentKeyPoints by rememberUpdatedState(keyPoints)
    val currentValue by rememberUpdatedState(value)

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> isPressed = true
                is PressInteraction.Release, is PressInteraction.Cancel -> isPressed = false
            }
        }
    }

    val targetTrackWidth = if (isDragging || isPressed) width * 1.4f else width
    val animatedTrackWidth by animateDpAsState(
        targetValue = targetTrackWidth,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy),
        label = "TrackWidthAnimation"
    )

    val targetThumbRadius =
        if (isDragging || isPressed) SliderDefaults.ThumbPressedRadius else SliderDefaults.ThumbRadius
    val animatedThumbRadius by animateDpAsState(
        targetValue = targetThumbRadius,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy),
        label = "ThumbRadiusAnimation"
    )

    val coercedValue = value.coerceIn(valueRange.start, valueRange.endInclusive)
    val stepFractions = remember(steps) { stepsToTickFractions(steps) }
    val allKeyPointFractions = remember(keyPoints, stepFractions, valueRange) {
        computeAllKeyPointFractions(keyPoints, stepFractions, valueRange)
    }

    val hapticState = remember { SliderHapticState() }

    fun calculateValueFromFraction(visualFraction: Float): Float {
        val fractionForValue = if (reverseDirection) visualFraction else 1f - visualFraction
        val f = fractionForValue.coerceIn(0f, 1f)
        val newValue = lerp(currentValueRange.start, currentValueRange.endInclusive, f)
        return when {
            currentSteps > 0 -> snapValueToTick(
                newValue,
                stepFractions,
                currentValueRange.start,
                currentValueRange.endInclusive
            )

            allKeyPointFractions.isNotEmpty() -> {
                val closestKeyPoint = allKeyPointFractions.minByOrNull { abs(it - f) }
                if (closestKeyPoint != null && abs(f - closestKeyPoint) < magnetThreshold) {
                    lerp(currentValueRange.start, currentValueRange.endInclusive, closestKeyPoint)
                } else newValue
            }

            else -> newValue
        }
    }

    Box(
        modifier = modifier
            .width(SliderDefaults.TouchTargetHeight)
            .fillMaxHeight()
            .padding(vertical = 14.dp)
            .then(
                if (enabled) {
                    Modifier
                        .pointerInput(Unit) {
                            detectVerticalDragGestures(
                                onDragStart = { offset ->
                                    isDragging = true
                                    isPressed = true
                                    val totalHeight = size.height
                                    val visualFraction = (offset.y / totalHeight).coerceIn(0f, 1f)

                                    val calculatedValue = calculateValueFromFraction(visualFraction)
                                    currentOnValueChange(calculatedValue)
                                    hapticState.reset(calculatedValue)
                                },
                                onVerticalDrag = { change, _ ->
                                    val totalHeight = size.height
                                    val visualFraction = (change.position.y / totalHeight).coerceIn(0f, 1f)

                                    val calculatedValue = calculateValueFromFraction(visualFraction)

                                    if (calculatedValue != currentValue) {
                                        currentOnValueChange(calculatedValue)
                                        hapticState.handleHapticFeedback(
                                            calculatedValue,
                                            currentValueRange,
                                            hapticEffect,
                                            hapticFeedback,
                                            allKeyPointFractions,
                                            currentKeyPoints != null
                                        )
                                    }
                                },
                                onDragEnd = {
                                    isDragging = false
                                    isPressed = false
                                    currentOnValueChangeFinished?.invoke()
                                },
                                onDragCancel = {
                                    isDragging = false
                                    isPressed = false
                                    currentOnValueChangeFinished?.invoke()
                                }
                            )
                        }
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = { offset ->
                                    isPressed = true
                                    val totalHeight = size.height
                                    val visualFraction = (offset.y / totalHeight).coerceIn(0f, 1f)
                                    val calculatedValue = calculateValueFromFraction(visualFraction)
                                    currentOnValueChange(calculatedValue)
                                    tryAwaitRelease()
                                    isPressed = false
                                    currentOnValueChangeFinished?.invoke()
                                }
                            )
                        }
                        .indication(interactionSource, LocalIndication.current)
                } else Modifier
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        VerticalSliderTrack(
            modifier = Modifier.fillMaxHeight(),
            backgroundWidth = animatedTrackWidth,
            progressWidth = width,
            currentThumbRadius = animatedThumbRadius,
            backgroundColor = colors.backgroundColor(),
            foregroundColor = colors.foregroundColor(enabled),
            thumbColor = colors.thumbColor(),
            thumbShadowColor = colors.thumbShadowColor(),
            value = coercedValue,
            valueRange = valueRange,
            reverseDirection = reverseDirection,
            showKeyPoints = showKeyPoints,
            keyPointsFractions = allKeyPointFractions,
            keyPointColor = colors.keyPointColor(),
            isDragging = isDragging
        )
    }
}

/**
 * A [RangeSlider] component with COUI style.
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
    height: Dp = SliderDefaults.TrackHeight,
    colors: SliderColors = SliderDefaults.sliderColors(),
    hapticEffect: SliderDefaults.SliderHapticEffect = SliderDefaults.DefaultHapticEffect,
    showKeyPoints: Boolean = false,
    keyPoints: List<Float>? = null,
    magnetThreshold: Float = 0.03f
) {
    val hapticFeedback = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }

    var isDragging by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }
    var draggingThumb by remember { mutableStateOf(0) }

    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val currentOnValueChangeFinished by rememberUpdatedState(onValueChangeFinished)
    val currentValueRange by rememberUpdatedState(valueRange)
    val currentSteps by rememberUpdatedState(steps)
    val currentKeyPoints by rememberUpdatedState(keyPoints)
    val currentValue by rememberUpdatedState(value)

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> isPressed = true
                is PressInteraction.Release, is PressInteraction.Cancel -> isPressed = false
            }
        }
    }

    val targetHeight = if (isDragging || isPressed) height * 1.4f else height
    val animatedTrackHeight by animateDpAsState(
        targetValue = targetHeight,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy),
        label = "RangeTrackHeightAnimation"
    )

    val targetThumbRadius =
        if (isDragging || isPressed) SliderDefaults.ThumbPressedRadius else SliderDefaults.ThumbRadius
    val animatedThumbRadius by animateDpAsState(
        targetValue = targetThumbRadius,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy),
        label = "RangeThumbRadiusAnimation"
    )

    val stepFractions = remember(steps) { stepsToTickFractions(steps) }
    val allKeyPointFractions = remember(keyPoints, stepFractions, valueRange) {
        computeAllKeyPointFractions(keyPoints, stepFractions, valueRange)
    }
    val hapticState = remember { SliderHapticState() }

    fun calculateValueFromFraction(visualFraction: Float): Float {
        val f = visualFraction.coerceIn(0f, 1f)
        val newValue = lerp(currentValueRange.start, currentValueRange.endInclusive, f)
        return when {
            currentSteps > 0 -> snapValueToTick(
                newValue,
                stepFractions,
                currentValueRange.start,
                currentValueRange.endInclusive
            )

            allKeyPointFractions.isNotEmpty() -> {
                val closestKeyPoint = allKeyPointFractions.minByOrNull { abs(it - f) }
                if (closestKeyPoint != null && abs(f - closestKeyPoint) < magnetThreshold) {
                    lerp(currentValueRange.start, currentValueRange.endInclusive, closestKeyPoint)
                } else newValue
            }

            else -> newValue
        }
    }

    Box(
        modifier = modifier
            .height(SliderDefaults.TouchTargetHeight)
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .then(
                if (enabled) {
                    Modifier
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragStart = { offset ->
                                    isDragging = true
                                    isPressed = true
                                    val width = size.width
                                    val currentStartFrac =
                                        (currentValue.start - currentValueRange.start) / (currentValueRange.endInclusive - currentValueRange.start)
                                    val currentEndFrac =
                                        (currentValue.endInclusive - currentValueRange.start) / (currentValueRange.endInclusive - currentValueRange.start)

                                    val startPx = currentStartFrac * width
                                    val endPx = currentEndFrac * width

                                    val distStart = abs(offset.x - startPx)
                                    val distEnd = abs(offset.x - endPx)

                                    draggingThumb = if (distStart < distEnd) 1 else 2

                                    val visualFraction = (offset.x / width).coerceIn(0f, 1f)
                                    val calculatedValue = calculateValueFromFraction(visualFraction)

                                    val newRange = if (draggingThumb == 1) {
                                        calculatedValue.coerceAtMost(currentValue.endInclusive)..currentValue.endInclusive
                                    } else {
                                        currentValue.start..calculatedValue.coerceAtLeast(currentValue.start)
                                    }

                                    currentOnValueChange(newRange)
                                },
                                onHorizontalDrag = { change, _ ->
                                    val width = size.width
                                    val visualFraction = (change.position.x / width).coerceIn(0f, 1f)
                                    val calculatedValue = calculateValueFromFraction(visualFraction)

                                    val newRange = if (draggingThumb == 1) {
                                        val safeStart = calculatedValue.coerceAtMost(currentValue.endInclusive)
                                        safeStart..currentValue.endInclusive
                                    } else {
                                        val safeEnd = calculatedValue.coerceAtLeast(currentValue.start)
                                        currentValue.start..safeEnd
                                    }

                                    if (newRange != currentValue) {
                                        currentOnValueChange(newRange)
                                        hapticState.handleHapticFeedback(
                                            calculatedValue,
                                            currentValueRange,
                                            hapticEffect,
                                            hapticFeedback,
                                            allKeyPointFractions,
                                            currentKeyPoints != null
                                        )
                                    }
                                },
                                onDragEnd = {
                                    isDragging = false
                                    isPressed = false
                                    draggingThumb = 0
                                    currentOnValueChangeFinished?.invoke()
                                },
                                onDragCancel = {
                                    isDragging = false
                                    isPressed = false
                                    draggingThumb = 0
                                    currentOnValueChangeFinished?.invoke()
                                }
                            )
                        }
                        .indication(interactionSource, LocalIndication.current)
                } else Modifier
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        RangeSliderTrack(
            modifier = Modifier.fillMaxWidth(),
            backgroundHeight = animatedTrackHeight,
            progressHeight = height,
            currentThumbRadius = animatedThumbRadius,
            backgroundColor = colors.backgroundColor(),
            foregroundColor = colors.foregroundColor(enabled),
            thumbColor = colors.thumbColor(),
            thumbShadowColor = colors.thumbShadowColor(),
            value = value,
            valueRange = valueRange,
            showKeyPoints = showKeyPoints,
            keyPointsFractions = allKeyPointFractions,
            keyPointColor = colors.keyPointColor(),
            isDragging = isDragging
        )
    }
}


@Composable
private fun SliderTrack(
    modifier: Modifier,
    backgroundHeight: Dp,
    progressHeight: Dp,
    currentThumbRadius: Dp,
    backgroundColor: Color,
    foregroundColor: Color,
    thumbColor: Color,
    thumbShadowColor: Color,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    reverseDirection: Boolean,
    showKeyPoints: Boolean,
    keyPointsFractions: FloatArray,
    keyPointColor: Color,
    isDragging: Boolean
) {
    val animatedValue by animateFloatAsState(
        targetValue = value,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = if (isDragging) Spring.StiffnessMedium else Spring.StiffnessLow
        ),
        label = "SliderValueAnimation"
    )

    Canvas(modifier = modifier.height(SliderDefaults.TouchTargetHeight)) {
        val width = size.width
        val trackHeightPx = backgroundHeight.toPx()
        val progressHeightPx = progressHeight.toPx()
        val centerY = size.height / 2f

        val backgroundCornerRadius = trackHeightPx / 2f
        val progressCornerRadius = progressHeightPx / 2f

        drawRoundRect(
            color = backgroundColor,
            topLeft = Offset(-backgroundCornerRadius, centerY - backgroundCornerRadius),
            size = Size(width + 2 * backgroundCornerRadius, trackHeightPx),
            cornerRadius = CornerRadius(backgroundCornerRadius, backgroundCornerRadius)
        )

        val fraction = (animatedValue - valueRange.start) / (valueRange.endInclusive - valueRange.start)
        val visualFraction = if (reverseDirection) 1f - fraction else fraction

        if (showKeyPoints && keyPointsFractions.isNotEmpty()) {
            val dotRadius = 2.dp.toPx()
            keyPointsFractions.forEach { fractionPoint ->
                val visualPointFraction = if (reverseDirection) 1f - fractionPoint else fractionPoint
                val cx = width * visualPointFraction
                drawCircle(
                    color = keyPointColor,
                    radius = dotRadius,
                    center = Offset(cx, centerY)
                )
            }
        }

        val progressEndPx = width * visualFraction

        if (reverseDirection) {
            drawRoundRect(
                color = foregroundColor,
                topLeft = Offset(progressEndPx - progressCornerRadius, centerY - progressCornerRadius),
                size = Size((width - progressEndPx) + 2 * progressCornerRadius, progressHeightPx),
                cornerRadius = CornerRadius(progressCornerRadius, progressCornerRadius)
            )
        } else {
            drawRoundRect(
                color = foregroundColor,
                topLeft = Offset(-progressCornerRadius, centerY - progressCornerRadius),
                size = Size(progressEndPx + 2 * progressCornerRadius, progressHeightPx),
                cornerRadius = CornerRadius(progressCornerRadius, progressCornerRadius)
            )
        }

        val thumbCx = if (reverseDirection) width - progressEndPx else progressEndPx
        val thumbRadiusPx = currentThumbRadius.toPx()

        drawCircle(
            color = thumbShadowColor,
            radius = thumbRadiusPx,
            center = Offset(thumbCx, centerY + 2.dp.toPx())
        )

        drawCircle(
            color = thumbColor,
            radius = thumbRadiusPx,
            center = Offset(thumbCx, centerY)
        )
    }
}

@Composable
private fun VerticalSliderTrack(
    modifier: Modifier,
    backgroundWidth: Dp,
    progressWidth: Dp,
    currentThumbRadius: Dp,
    backgroundColor: Color,
    foregroundColor: Color,
    thumbColor: Color,
    thumbShadowColor: Color,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    reverseDirection: Boolean,
    showKeyPoints: Boolean,
    keyPointsFractions: FloatArray,
    keyPointColor: Color,
    isDragging: Boolean
) {
    val animatedValue by animateFloatAsState(
        targetValue = value,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = if (isDragging) Spring.StiffnessMedium else Spring.StiffnessLow
        ),
        label = "VerticalSliderValueAnimation"
    )

    Canvas(modifier = modifier.width(SliderDefaults.TouchTargetHeight)) {
        val height = size.height
        val trackWidthPx = backgroundWidth.toPx()
        val progressWidthPx = progressWidth.toPx()
        val centerX = size.width / 2f

        val backgroundCornerRadius = trackWidthPx / 2f
        val progressCornerRadius = progressWidthPx / 2f

        drawRoundRect(
            color = backgroundColor,
            topLeft = Offset(centerX - backgroundCornerRadius, -backgroundCornerRadius),
            size = Size(trackWidthPx, height + 2 * backgroundCornerRadius),
            cornerRadius = CornerRadius(backgroundCornerRadius, backgroundCornerRadius)
        )

        val fraction = (animatedValue - valueRange.start) / (valueRange.endInclusive - valueRange.start)
        val visualFraction = if (reverseDirection) fraction else 1f - fraction

        if (showKeyPoints && keyPointsFractions.isNotEmpty()) {
            val dotRadius = 2.dp.toPx()
            keyPointsFractions.forEach { fractionPoint ->
                val vPointFrac = if (reverseDirection) fractionPoint else 1f - fractionPoint
                val cy = height * vPointFrac
                drawCircle(
                    color = keyPointColor,
                    radius = dotRadius,
                    center = Offset(centerX, cy)
                )
            }
        }

        val progressEndPx = height * visualFraction

        if (reverseDirection) {
            drawRoundRect(
                color = foregroundColor,
                topLeft = Offset(centerX - progressCornerRadius, -progressCornerRadius),
                size = Size(progressWidthPx, progressEndPx + 2 * progressCornerRadius),
                cornerRadius = CornerRadius(progressCornerRadius, progressCornerRadius)
            )
        } else {
            drawRoundRect(
                color = foregroundColor,
                topLeft = Offset(centerX - progressCornerRadius, progressEndPx - progressCornerRadius),
                size = Size(progressWidthPx, (height - progressEndPx) + 2 * progressCornerRadius),
                cornerRadius = CornerRadius(progressCornerRadius, progressCornerRadius)
            )
        }

        val thumbCy = progressEndPx
        val thumbRadiusPx = currentThumbRadius.toPx()

        drawCircle(
            color = thumbShadowColor,
            radius = thumbRadiusPx,
            center = Offset(centerX, thumbCy + 2.dp.toPx())
        )

        drawCircle(
            color = thumbColor,
            radius = thumbRadiusPx,
            center = Offset(centerX, thumbCy)
        )
    }
}


@Composable
private fun RangeSliderTrack(
    modifier: Modifier,
    backgroundHeight: Dp,
    progressHeight: Dp,
    currentThumbRadius: Dp,
    backgroundColor: Color,
    foregroundColor: Color,
    thumbColor: Color,
    thumbShadowColor: Color,
    value: ClosedFloatingPointRange<Float>,
    valueRange: ClosedFloatingPointRange<Float>,
    showKeyPoints: Boolean,
    keyPointsFractions: FloatArray,
    keyPointColor: Color,
    isDragging: Boolean
) {
    val animatedStart by animateFloatAsState(
        targetValue = value.start,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = if (isDragging) Spring.StiffnessMedium else Spring.StiffnessLow
        ), label = "RangeStart"
    )
    val animatedEnd by animateFloatAsState(
        targetValue = value.endInclusive,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = if (isDragging) Spring.StiffnessMedium else Spring.StiffnessLow
        ), label = "RangeEnd"
    )

    Canvas(modifier = modifier.height(SliderDefaults.TouchTargetHeight)) {
        val width = size.width
        val trackHeightPx = backgroundHeight.toPx()
        val progressHeightPx = progressHeight.toPx()
        val centerY = size.height / 2f

        val backgroundCornerRadius = trackHeightPx / 2f
        val progressCornerRadius = progressHeightPx / 2f

        drawRoundRect(
            color = backgroundColor,
            topLeft = Offset(-backgroundCornerRadius, centerY - backgroundCornerRadius),
            size = Size(width + 2 * backgroundCornerRadius, trackHeightPx),
            cornerRadius = CornerRadius(backgroundCornerRadius, backgroundCornerRadius)
        )

        val startFraction = (animatedStart - valueRange.start) / (valueRange.endInclusive - valueRange.start)
        val endFraction = (animatedEnd - valueRange.start) / (valueRange.endInclusive - valueRange.start)

        if (showKeyPoints && keyPointsFractions.isNotEmpty()) {
            val dotRadius = 2.dp.toPx()
            keyPointsFractions.forEach { fractionPoint ->
                val cx = width * fractionPoint
                drawCircle(
                    color = keyPointColor,
                    radius = dotRadius,
                    center = Offset(cx, centerY)
                )
            }
        }

        val startPx = width * startFraction
        val endPx = width * endFraction
        val rangeWidth = endPx - startPx

        drawRoundRect(
            color = foregroundColor,
            topLeft = Offset(startPx - progressCornerRadius, centerY - progressCornerRadius),
            size = Size(rangeWidth + 2 * progressCornerRadius, progressHeightPx),
            cornerRadius = CornerRadius(progressCornerRadius, progressCornerRadius)
        )

        val thumbRadiusPx = currentThumbRadius.toPx()

        drawCircle(color = thumbShadowColor, radius = thumbRadiusPx, center = Offset(startPx, centerY + 2.dp.toPx()))
        drawCircle(color = thumbColor, radius = thumbRadiusPx, center = Offset(startPx, centerY))

        drawCircle(color = thumbShadowColor, radius = thumbRadiusPx, center = Offset(endPx, centerY + 2.dp.toPx()))
        drawCircle(color = thumbColor, radius = thumbRadiusPx, center = Offset(endPx, centerY))
    }
}

object SliderDefaults {
    val TrackHeight = 20.dp
    val TouchTargetHeight = 48.dp
    val ThumbRadius = 6.dp
    val ThumbPressedRadius = 8.dp

    enum class SliderHapticEffect {
        None, Edge, Step
    }

    val DefaultHapticEffect = SliderHapticEffect.Step

    @Composable
    fun sliderColors(
        foregroundColor: Color = COUITheme.colorScheme.primary,
        backgroundColor: Color = COUITheme.colorScheme.seekbarBackground,
        disabledForegroundColor: Color = COUITheme.colorScheme.disabledPrimarySlider,
        disabledBackgroundColor: Color = COUITheme.colorScheme.disabledPrimarySlider,
        thumbColor: Color = COUITheme.colorScheme.seekbarThumb,
        thumbShadowColor: Color = Color(0x1A000000),
        keyPointColor: Color = COUITheme.colorScheme.seekbarTickMark
    ): SliderColors = SliderColors(
        foregroundColor = foregroundColor,
        backgroundColor = backgroundColor,
        disabledForegroundColor = disabledForegroundColor,
        disabledBackgroundColor = disabledBackgroundColor,
        thumbColor = thumbColor,
        thumbShadowColor = thumbShadowColor,
        keyPointColor = keyPointColor
    )
}

@Immutable
class SliderColors(
    private val foregroundColor: Color,
    private val backgroundColor: Color,
    private val disabledForegroundColor: Color,
    private val disabledBackgroundColor: Color,
    private val thumbColor: Color,
    private val thumbShadowColor: Color,
    private val keyPointColor: Color
) {
    @Stable
    internal fun foregroundColor(enabled: Boolean): Color =
        if (enabled) foregroundColor else disabledForegroundColor

    @Stable
    internal fun backgroundColor(): Color = backgroundColor

    @Stable
    internal fun thumbColor(): Color = thumbColor

    @Stable
    internal fun thumbShadowColor(): Color = thumbShadowColor

    @Stable
    internal fun keyPointColor(): Color = keyPointColor

    @Stable
    internal fun keyPointForegroundColor(): Color = Color.White
}

internal class SliderHapticState {
    private var lastStep: Float = 0f
    private var isAtKeyPoint: Boolean = false

    fun reset(currentValue: Float) {
        lastStep = currentValue
        isAtKeyPoint = false
    }

    fun handleHapticFeedback(
        currentValue: Float,
        valueRange: ClosedFloatingPointRange<Float>,
        hapticEffect: SliderDefaults.SliderHapticEffect,
        hapticFeedback: HapticFeedback,
        keyPointFractions: FloatArray = floatArrayOf(),
        hasCustomKeyPoints: Boolean = false
    ) {
        if (hapticEffect == SliderDefaults.SliderHapticEffect.None) return

        if (currentValue == valueRange.start || currentValue == valueRange.endInclusive) {
            if (currentValue != lastStep) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        }

        if (hapticEffect == SliderDefaults.SliderHapticEffect.Step) {
            val isNotAtEdge = currentValue != valueRange.start && currentValue != valueRange.endInclusive

            if (hasCustomKeyPoints || keyPointFractions.isNotEmpty()) {
                val fraction = (currentValue - valueRange.start) / (valueRange.endInclusive - valueRange.start)
                val hitKeyPoint = keyPointFractions.any { abs(it - fraction) < 0.001f }

                if (hitKeyPoint && !isAtKeyPoint && isNotAtEdge) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
                isAtKeyPoint = hitKeyPoint
            }
        }
        lastStep = currentValue
    }
}

private fun stepsToTickFractions(steps: Int): FloatArray {
    return if (steps == 0) floatArrayOf() else FloatArray(steps + 2) { it.toFloat() / (steps + 1) }
}

private fun snapValueToTick(
    current: Float,
    tickFractions: FloatArray,
    minPx: Float,
    maxPx: Float
): Float {
    return tickFractions
        .minByOrNull { abs(lerp(minPx, maxPx, it) - current) }
        ?.run { lerp(minPx, maxPx, this) }
        ?: current
}

private fun pointsToFractions(
    points: List<Float>,
    valueRange: ClosedFloatingPointRange<Float>
): FloatArray {
    return points.map { point ->
        ((point - valueRange.start) / (valueRange.endInclusive - valueRange.start))
            .coerceIn(0f, 1f)
    }.toFloatArray()
}

private fun computeAllKeyPointFractions(
    keyPoints: List<Float>?,
    stepFractions: FloatArray,
    valueRange: ClosedFloatingPointRange<Float>
): FloatArray {
    return when {
        keyPoints != null -> pointsToFractions(keyPoints, valueRange)
        stepFractions.isNotEmpty() -> stepFractions
        else -> floatArrayOf()
    }
}
