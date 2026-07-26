// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.theme.COUITheme
import com.suqi8.coui.kmp.utils.SinkFeedback
import com.suqi8.coui.kmp.utils.pressable

// COUI checkbox geometry, expressed as fractions of the 24dp drawable viewport
// (decompiled coui_btn_check_*.xml / coui_btn_part_check_*.xml).
private val ViewportSize = 24f

// Checked state (coui_btn_check_on_normal.xml): 18x18 rounded square at inset 3,
// outer corner radius 4, filled with couiColorPrimary.
private val FillInsetFraction = 3f / ViewportSize
private val FillSizeFraction = 18f / ViewportSize
private val FillCornerRadiusFraction = 4f / ViewportSize

// Unchecked state (coui_btn_check_off_normal.xml): 1.3 stroke whose centerline is the
// 16.4x16.4 rounded rect at inset 3.8 with corner radius 3.2. The centerline is inset in
// the asset itself, so the outline is never clipped by the shape clip.
private val RingInsetFraction = 3.8f / ViewportSize
private val RingSizeFraction = 16.4f / ViewportSize
private val RingCornerRadiusFraction = 3.2f / ViewportSize
private val RingStrokeWidthFraction = 1.3f / ViewportSize

// Checkmark centerline derived from the filled outline in coui_btn_check_on_normal.xml
// (M10.831,14.318 L17.033,8 L18,8.985 ... L7,12.386 L7.967,11.401): both ends are the
// flat cap midpoints, the elbow is the intersection of the two arm centerlines, and the
// outline thickness is 1.38 with butt caps and a rounded outer elbow.
private val CheckStrokeWidthFraction = 1.38f / ViewportSize
private val CheckStartPoint = Offset(7.4835f / ViewportSize, 11.8935f / ViewportSize)
private val CheckMiddlePoint = Offset(10.831f / ViewportSize, 15.3035f / ViewportSize)
private val CheckEndPoint = Offset(17.5165f / ViewportSize, 8.4925f / ViewportSize)

// Indeterminate state (coui_btn_part_check_on_normal.xml): 10x1.6 bar (M7,11.866h10v1.6)
// centered in the square, revealed from the left in the part AVDs.
private val BarStartFraction = 7f / ViewportSize
private val BarLengthFraction = 10f / ViewportSize
private val BarThicknessFraction = 1.6f / ViewportSize

// Shared easing of the COUI checkbox AVDs ($coui_checkbox_*): cubic-bezier(0.3, 0, 0.1, 1).
private val CouiCheckboxEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

// Every COUI checkbox AVD pops all layers 1 -> 1.08 -> 1 (150ms up + 150ms down).
private val PopPeakScale = 1.08f
private val PopHalfDurationMillis = 150

// Clip proportional to the drawn square: corner radius 4 on the 18-wide square, scaled
// up to the 24dp layout box (24 * 4 / 18). Drawing stays inside the square, so this only
// bounds press-feedback overdraw with a matching corner geometry.
private val CheckboxClipShape = RoundedCornerShape((ViewportSize * 4f / 18f).dp)

/**
 * A [Checkbox] component with COUI style, supporting three states: On, Off, and Indeterminate.
 *
 * Visuals follow the COUI checkbox: a rounded square that is outlined when unchecked,
 * filled with a trimmed checkmark when checked, and filled with a centered bar when
 * indeterminate.
 *
 * @param state The current [ToggleableState] of the [Checkbox].
 * @param onClick The callback to be called when the [Checkbox] is clicked. The caller is
 *   responsible for updating the state. If `null`, the [Checkbox] is not interactive.
 * @param modifier The modifier to be applied to the [Checkbox].
 * @param colors The [CheckboxColors] of the [Checkbox].
 * @param enabled Whether the [Checkbox] is enabled.
 */
@Composable
fun Checkbox(
    state: ToggleableState,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    colors: CheckboxColors = CheckboxDefaults.checkboxColors(),
    enabled: Boolean = true,
) {
    val currentOnClickState = rememberUpdatedState(onClick)
    val hapticFeedback = LocalHapticFeedback.current
    val currentHapticFeedback by rememberUpdatedState(hapticFeedback)

    val transition = updateTransition(state, label = "CheckboxTransition")

    // Fill alpha timeline from the COUI AVDs: fades in over the first 150ms when leaving
    // Off, holds for 150ms and fades out over the last 150ms when returning to Off, and
    // stays opaque between On and Indeterminate.
    val fillFractionState = transition.animateFloat(
        transitionSpec = {
            if (targetState != ToggleableState.Off) {
                tween(durationMillis = 150, easing = CouiCheckboxEasing)
            } else {
                keyframes {
                    durationMillis = 300
                    1f at 150 using CouiCheckboxEasing
                }
            }
        },
        label = "FillFraction",
    ) { if (it != ToggleableState.Off) 1f else 0f }

    // Checkmark trim: trimPathEnd runs 0 <-> 1 over the full 300ms in every COUI
    // transition that involves the checkmark; trimPathStart stays 0.
    val checkFractionState = transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300, easing = CouiCheckboxEasing) },
        label = "CheckFraction",
    ) { if (it == ToggleableState.On) 1f else 0f }

    // Indeterminate bar reveal from the part AVD clip-path morphs: grows over 300ms from
    // Off, holds 100ms then grows over 200ms from On, retracts over 200ms towards On and
    // over 300ms towards Off.
    val barFractionState = transition.animateFloat(
        transitionSpec = {
            when {
                targetState == ToggleableState.Indeterminate && initialState == ToggleableState.On ->
                    keyframes {
                        durationMillis = 300
                        0f at 100 using CouiCheckboxEasing
                    }

                targetState == ToggleableState.On -> tween(durationMillis = 200, easing = CouiCheckboxEasing)

                else -> tween(durationMillis = 300, easing = CouiCheckboxEasing)
            }
        },
        label = "BarFraction",
    ) { if (it == ToggleableState.Indeterminate) 1f else 0f }

    // Center pop played by every COUI checkbox AVD on each state change.
    val popScale = remember { Animatable(1f) }
    var skipInitialPop by remember { mutableStateOf(true) }
    LaunchedEffect(state) {
        if (skipInitialPop) {
            skipInitialPop = false
            return@LaunchedEffect
        }
        popScale.snapTo(1f)
        popScale.animateTo(PopPeakScale, tween(PopHalfDurationMillis, easing = CouiCheckboxEasing))
        popScale.animateTo(1f, tween(PopHalfDurationMillis, easing = CouiCheckboxEasing))
    }

    // COUI keeps each layer's own color and crossfades layers instead of morphing colors:
    // the outline keeps the unchecked color while the fill, checkmark and bar keep the
    // checked colors throughout every transition.
    val ringColor = colors.uncheckedBackgroundColor(enabled)
    val fillColor = colors.checkedBackgroundColor(enabled)
    val markColor = colors.checkedForegroundColor(enabled)

    val checkPath = remember { Path() }
    val sinkFeedback = remember { SinkFeedback(sinkAmount = 0.85f, animationSpec = spring(0.99f, 986.96f)) }

    val finalModifier = if (onClick != null) {
        Modifier.triStateToggleable(
            state = state,
            onClick = {
                currentOnClickState.value?.invoke()
                currentHapticFeedback.performHapticFeedback(
                    when (state) {
                        ToggleableState.Off -> HapticFeedbackType.ToggleOn
                        ToggleableState.On -> HapticFeedbackType.ToggleOff
                        ToggleableState.Indeterminate -> HapticFeedbackType.SegmentTick
                    },
                )
            },
            enabled = enabled,
            role = Role.Checkbox,
            interactionSource = null,
        )
    } else {
        Modifier.semantics {
            role = Role.Checkbox
            toggleableState = state
            if (!enabled) disabled()
        }
    }

    Box(
        modifier = modifier
            .wrapContentSize(Alignment.Center)
            // COUI checkbox drawables (coui_btn_check_*.xml) have a 24dp intrinsic size.
            .requiredSize(24.dp)
            .pressable(
                interactionSource = remember { MutableInteractionSource() },
                indication = sinkFeedback,
                enabled = enabled,
                delay = null,
            )
            .clip(CheckboxClipShape)
            .drawWithCache {
                val d = size.minDimension

                val fillTopLeft = Offset(FillInsetFraction * d, FillInsetFraction * d)
                val fillSize = Size(FillSizeFraction * d, FillSizeFraction * d)
                val fillCornerRadius = CornerRadius(FillCornerRadiusFraction * d)

                val ringTopLeft = Offset(RingInsetFraction * d, RingInsetFraction * d)
                val ringSize = Size(RingSizeFraction * d, RingSizeFraction * d)
                val ringCornerRadius = CornerRadius(RingCornerRadiusFraction * d)
                val ringStroke = Stroke(width = RingStrokeWidthFraction * d)

                val checkCache = CheckmarkCache(
                    startPoint = Offset(CheckStartPoint.x * d, CheckStartPoint.y * d),
                    middlePoint = Offset(CheckMiddlePoint.x * d, CheckMiddlePoint.y * d),
                    endPoint = Offset(CheckEndPoint.x * d, CheckEndPoint.y * d),
                )
                val checkStroke = Stroke(
                    width = CheckStrokeWidthFraction * d,
                    cap = StrokeCap.Butt,
                    join = StrokeJoin.Round,
                )

                val barTopLeft = Offset(BarStartFraction * d, (d - BarThicknessFraction * d) / 2f)
                val barFullLength = BarLengthFraction * d
                val barThickness = BarThicknessFraction * d

                onDrawBehind {
                    scale(scale = popScale.value) {
                        val fillFraction = fillFractionState.value
                        if (fillFraction < 1f) {
                            // Unchecked outline sits beneath the fill, matching the COUI
                            // AVD layer stack; the opaque fill fully covers it.
                            drawRoundRect(
                                color = ringColor,
                                topLeft = ringTopLeft,
                                size = ringSize,
                                cornerRadius = ringCornerRadius,
                                style = ringStroke,
                            )
                        }
                        if (fillFraction > 0f) {
                            drawRoundRect(
                                color = fillColor,
                                topLeft = fillTopLeft,
                                size = fillSize,
                                cornerRadius = fillCornerRadius,
                                alpha = fillFraction,
                            )
                        }
                        drawTrimmedCheckmark(
                            color = markColor,
                            fraction = checkFractionState.value,
                            path = checkPath,
                            cache = checkCache,
                            stroke = checkStroke,
                        )
                        val barFraction = barFractionState.value
                        if (barFraction > 0f) {
                            drawRect(
                                color = markColor,
                                topLeft = barTopLeft,
                                size = Size(barFullLength * barFraction, barThickness),
                            )
                        }
                    }
                }
            }
            .then(finalModifier),
    ) {}
}

private class CheckmarkCache(
    val startPoint: Offset,
    val middlePoint: Offset,
    val endPoint: Offset,
) {
    val firstSegmentLength = (middlePoint - startPoint).getDistance()
    val secondSegmentLength = (endPoint - middlePoint).getDistance()
    val totalLength = firstSegmentLength + secondSegmentLength
}

private fun DrawScope.drawTrimmedCheckmark(
    color: Color,
    fraction: Float,
    path: Path,
    cache: CheckmarkCache,
    stroke: Stroke,
) {
    val endDistance = cache.totalLength * fraction
    if (endDistance <= 0f) return

    path.rewind()
    path.moveTo(cache.startPoint.x, cache.startPoint.y)
    if (endDistance <= cache.firstSegmentLength) {
        val t = endDistance / cache.firstSegmentLength
        path.lineTo(
            cache.startPoint.x + (cache.middlePoint.x - cache.startPoint.x) * t,
            cache.startPoint.y + (cache.middlePoint.y - cache.startPoint.y) * t,
        )
    } else {
        path.lineTo(cache.middlePoint.x, cache.middlePoint.y)
        val t = ((endDistance - cache.firstSegmentLength) / cache.secondSegmentLength).coerceAtMost(1f)
        path.lineTo(
            cache.middlePoint.x + (cache.endPoint.x - cache.middlePoint.x) * t,
            cache.middlePoint.y + (cache.endPoint.y - cache.middlePoint.y) * t,
        )
    }

    drawPath(
        path = path,
        color = color,
        style = stroke,
    )
}

object CheckboxDefaults {
    @Composable
    fun checkboxColors(
        checkedForegroundColor: Color = COUITheme.colorScheme.onPrimary,
        uncheckedForegroundColor: Color = COUITheme.colorScheme.secondary,
        disabledCheckedForegroundColor: Color = COUITheme.colorScheme.disabledOnPrimary,
        disabledUncheckedForegroundColor: Color = COUITheme.colorScheme.disabledOnPrimary,
        checkedBackgroundColor: Color = COUITheme.colorScheme.primary,
        uncheckedBackgroundColor: Color = COUITheme.colorScheme.secondary,
        disabledCheckedBackgroundColor: Color = COUITheme.colorScheme.disabledPrimary,
        disabledUncheckedBackgroundColor: Color = COUITheme.colorScheme.disabledSecondary,
    ): CheckboxColors = remember(
        checkedForegroundColor,
        uncheckedForegroundColor,
        disabledCheckedForegroundColor,
        disabledUncheckedForegroundColor,
        checkedBackgroundColor,
        uncheckedBackgroundColor,
        disabledCheckedBackgroundColor,
        disabledUncheckedBackgroundColor,
    ) {
        CheckboxColors(
            checkedForegroundColor = checkedForegroundColor,
            uncheckedForegroundColor = uncheckedForegroundColor,
            disabledCheckedForegroundColor = disabledCheckedForegroundColor,
            disabledUncheckedForegroundColor = disabledUncheckedForegroundColor,
            checkedBackgroundColor = checkedBackgroundColor,
            uncheckedBackgroundColor = uncheckedBackgroundColor,
            disabledCheckedBackgroundColor = disabledCheckedBackgroundColor,
            disabledUncheckedBackgroundColor = disabledUncheckedBackgroundColor,
        )
    }
}

@Immutable
data class CheckboxColors(
    private val checkedForegroundColor: Color,
    private val uncheckedForegroundColor: Color,
    private val disabledCheckedForegroundColor: Color,
    private val disabledUncheckedForegroundColor: Color,
    private val checkedBackgroundColor: Color,
    private val uncheckedBackgroundColor: Color,
    private val disabledCheckedBackgroundColor: Color,
    private val disabledUncheckedBackgroundColor: Color,
) {
    internal fun checkedForegroundColor(enabled: Boolean): Color = if (enabled) checkedForegroundColor else disabledCheckedForegroundColor

    internal fun uncheckedForegroundColor(enabled: Boolean): Color = if (enabled) uncheckedForegroundColor else disabledUncheckedForegroundColor

    internal fun checkedBackgroundColor(enabled: Boolean): Color = if (enabled) checkedBackgroundColor else disabledCheckedBackgroundColor

    internal fun uncheckedBackgroundColor(enabled: Boolean): Color = if (enabled) uncheckedBackgroundColor else disabledUncheckedBackgroundColor
}
