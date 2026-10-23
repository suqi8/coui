// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.triStateToggleable
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
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * A [Checkbox] component with COUI style, supporting three states: On, Off, and Indeterminate.
 *
 * Like `COUICheckBox`, the box itself never scales on press: an interactive checkbox shows a 40dp
 * circular press highlight (`checkbox_ripple_bg_radius` 20dp, couiColorPress) behind the 24dp
 * drawable. Pass `onClick = null` when a surrounding row handles the click, as the COUI checkbox
 * preference does, so only the row shows its press state.
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

    // Fill alpha timeline from the COUI AVDs.
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

    // Checkmark trim: trimPathEnd runs 0 <-> 1 over the full 300ms.
    val checkFractionState = transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300, easing = CouiCheckboxEasing) },
        label = "CheckFraction",
    ) { if (it == ToggleableState.On) 1f else 0f }

    // Indeterminate bar reveal from the part AVD clip-path morphs.
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

    // COUI crossfades layers instead of morphing colors.
    val ringColor = colors.uncheckedBackgroundColor(enabled)
    val fillColor = colors.checkedBackgroundColor(enabled)
    val markColor = colors.checkedForegroundColor(enabled)

    val checkPath = remember { Path() }
    val pressColor = COUITheme.colorScheme.press
    val pressIndication = remember(pressColor) { CircularPressIndication(CheckboxPressRadius, pressColor) }

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
            indication = pressIndication,
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
            .requiredSize(24.dp)
            // Before the drawing so the press highlight sits behind the box, like a view background.
            .then(finalModifier)
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
            },
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
    /**
     * The default checkbox colors (COUI `coui_btn_check_*.xml`).
     *
     * - The checkmark / indeterminate bar is white, and `coui_btn_check_inner_color_on_disabled`
     *   #8AFFFFFF / #29FFFFFF when disabled.
     * - The unchecked ring is couiColorLabelTertiary #42000000 / #4DFFFFFF, and
     *   couiColorQuarternaryNeutral #14000000 / #26FFFFFF when disabled (the same values as
     *   couiColorHover, hence the `hover` slot).
     * - The checked fill is couiColorPrimary, at 30% (`fillAlpha` 0.3) when disabled.
     */
    @Composable
    fun checkboxColors(
        checkedForegroundColor: Color = COUITheme.colorScheme.onPrimary,
        uncheckedForegroundColor: Color = COUITheme.colorScheme.onSurfaceVariantActions,
        disabledCheckedForegroundColor: Color = COUITheme.colorScheme.disabledOnPrimaryButton,
        disabledUncheckedForegroundColor: Color = COUITheme.colorScheme.hover,
        checkedBackgroundColor: Color = COUITheme.colorScheme.primary,
        uncheckedBackgroundColor: Color = COUITheme.colorScheme.onSurfaceVariantActions,
        disabledCheckedBackgroundColor: Color = COUITheme.colorScheme.disabledPrimary,
        disabledUncheckedBackgroundColor: Color = COUITheme.colorScheme.hover,
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

// COUI checkbox geometry, expressed as fractions of the 24dp drawable viewport (coui_btn_check_*.xml).
private val ViewportSize = 24f

// Checked state (coui_btn_check_on_normal.xml): 18x18 rounded square at inset 3, corner radius 4.
private val FillInsetFraction = 3f / ViewportSize
private val FillSizeFraction = 18f / ViewportSize
private val FillCornerRadiusFraction = 4f / ViewportSize

// Unchecked state (coui_btn_check_off_normal.xml): 1.3 stroke on the 16.4x16.4 rounded rect at inset 3.8.
private val RingInsetFraction = 3.8f / ViewportSize
private val RingSizeFraction = 16.4f / ViewportSize
private val RingCornerRadiusFraction = 3.2f / ViewportSize
private val RingStrokeWidthFraction = 1.3f / ViewportSize

// Checkmark stroke of the COUI check AVD ($coui_checkbox_unselected_to_selected__0.xml): the 72-unit
// polyline (-14.12,-1) -> (-3.44,9.69) -> (16.97,-10.91) around (36, 36.764), 3.9 wide, divided by 3.
private val CheckStrokeWidthFraction = 1.3f / ViewportSize
private val CheckStartPoint = Offset(7.2933f / ViewportSize, 11.9213f / ViewportSize)
private val CheckMiddlePoint = Offset(10.8533f / ViewportSize, 15.4847f / ViewportSize)
private val CheckEndPoint = Offset(17.6567f / ViewportSize, 8.618f / ViewportSize)

// Indeterminate state (coui_btn_part_check_on_normal.xml): 10x1.6 centered bar revealed from the left.
private val BarStartFraction = 7f / ViewportSize
private val BarLengthFraction = 10f / ViewportSize
private val BarThicknessFraction = 1.6f / ViewportSize

// Shared easing of the COUI checkbox AVDs: cubic-bezier(0.3, 0, 0.1, 1).
private val CouiCheckboxEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

// Every COUI checkbox AVD pops all layers 1 -> 1.08 -> 1 (150ms up + 150ms down).
private val PopPeakScale = 1.08f
private val PopHalfDurationMillis = 150

// COUICheckBox press ripple: circle mask of checkbox_ripple_bg_radius.
private val CheckboxPressRadius = 20.dp
