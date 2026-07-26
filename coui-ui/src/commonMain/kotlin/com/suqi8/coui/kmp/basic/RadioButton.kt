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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.theme.COUITheme
import com.suqi8.coui.kmp.utils.SinkFeedback
import com.suqi8.coui.kmp.utils.pressable

// COUI radio toggle easings. Every property animator in the decompiled AVDs shares one curve per
// direction: coui_btn_radio_off_to_on_animation.xml uses pathInterpolator "M0,0 c0.3,0 0.1,1 1,1"
// and coui_btn_radio_on_to_off_animation.xml uses "M0,0 c0.33,0 0.67,1 1,1".
private val CouiRadioSelectEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)
private val CouiRadioDeselectEasing = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

/**
 * A [RadioButton] component with COUI style.
 *
 * Visuals follow the COUI radio drawables (24dp viewport): unselected is an outline ring
 * (coui_btn_radio_off.xml), selected is a primary disc with a solid center dot stacked on top
 * (coui_btn_radio_on.xml). The selected state is two solid fills rather than a punched-out
 * annulus, so the center dot stays opaque over any background, including in dark mode. Toggling
 * replays the decompiled COUI AVD transitions (coui_btn_radio_off_to_on_animation.xml /
 * coui_btn_radio_on_to_off_animation.xml): the ring and disc cross-fade while pulsing
 * 1 -> 1.08 -> 1, and the center dot grows from / shrinks to the middle.
 *
 * @param selected Whether the [RadioButton] is currently selected.
 * @param onClick The callback to be called when the [RadioButton] is clicked. The caller is
 *   responsible for updating the state. If `null`, the [RadioButton] is not interactive.
 * @param modifier The modifier to be applied to the [RadioButton].
 * @param colors The [RadioButtonColors] of the [RadioButton].
 * @param enabled Whether the [RadioButton] is enabled.
 */
@Composable
fun RadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    colors: RadioButtonColors = RadioButtonDefaults.radioButtonColors(),
    enabled: Boolean = true,
) {
    val currentOnClickState = rememberUpdatedState(onClick)
    val hapticFeedback = LocalHapticFeedback.current
    val currentHapticFeedback by rememberUpdatedState(hapticFeedback)

    val transition = updateTransition(selected, label = "RadioButtonTransition")

    val color = colors.color(enabled)
    val unselectedColor = colors.unselectedColor(enabled)
    val centerColor = colors.centerColor(enabled)

    // _R_G_STROKE_PATH: the unselected ring fades out over 300ms when selecting and back in over
    // 267ms when deselecting.
    val ringAlphaState = transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                tween(durationMillis = 300, easing = CouiRadioSelectEasing)
            } else {
                tween(durationMillis = 267, easing = CouiRadioDeselectEasing)
            }
        },
        label = "RingAlpha",
    ) { if (it) 0f else 1f }

    // _R_G_OUT_CIRCLE_PATH: the primary disc fades in over 300ms and out over 200ms.
    val discAlphaState = transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                tween(durationMillis = 300, easing = CouiRadioSelectEasing)
            } else {
                tween(durationMillis = 200, easing = CouiRadioDeselectEasing)
            }
        },
        label = "DiscAlpha",
    ) { if (it) 1f else 0f }

    // _R_G_INNER_CIRCLE: the center dot scales up from the middle over 300ms and back down over
    // 267ms.
    val centerScaleState = transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                tween(durationMillis = 300, easing = CouiRadioSelectEasing)
            } else {
                tween(durationMillis = 267, easing = CouiRadioDeselectEasing)
            }
        },
        label = "CenterScale",
    ) { if (it) 1f else 0f }

    // _R_G_INNER_CIRCLE_PATH: the center dot fades in over 300ms and out over 200ms.
    val centerAlphaState = transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                tween(durationMillis = 300, easing = CouiRadioSelectEasing)
            } else {
                tween(durationMillis = 200, easing = CouiRadioDeselectEasing)
            }
        },
        label = "CenterAlpha",
    ) { if (it) 1f else 0f }

    // _R_G_STROKE / _R_G_OUT_CIRCLE group scale: both AVDs pulse the ring and disc
    // 1 -> 1.08 (150ms) -> 1 (150ms) on every toggle. A same-endpoint keyframe animation never
    // runs through updateTransition, so the pulse is driven by an Animatable on state changes
    // (same pattern as the Switch thumb squash). The inner circle group has no pulse.
    val bounceScale = remember { Animatable(1f) }
    var isFirstComposition by remember { mutableStateOf(true) }
    LaunchedEffect(selected) {
        if (isFirstComposition) {
            isFirstComposition = false
            return@LaunchedEffect
        }
        val easing = if (selected) CouiRadioSelectEasing else CouiRadioDeselectEasing
        bounceScale.snapTo(1f)
        bounceScale.animateTo(
            targetValue = 1f,
            animationSpec = keyframes {
                durationMillis = 300
                1f at 0 using easing
                1.08f at 150 using easing
            },
        )
    }

    val capsuleShape = CircleShape
    val sinkFeedback = remember { SinkFeedback(sinkAmount = 0.85f, animationSpec = spring(0.99f, 986.96f)) }

    val finalModifier = if (onClick != null) {
        Modifier.selectable(
            selected = selected,
            onClick = {
                currentOnClickState.value?.invoke()
                currentHapticFeedback.performHapticFeedback(
                    if (selected) HapticFeedbackType.ToggleOff else HapticFeedbackType.ToggleOn,
                )
            },
            enabled = enabled,
            role = Role.RadioButton,
            interactionSource = null,
            indication = null,
        )
    } else {
        Modifier.semantics {
            role = Role.RadioButton
            this.selected = selected
            if (!enabled) disabled()
        }
    }

    Box(
        modifier = modifier
            .wrapContentSize(Alignment.Center)
            // COUI radio drawables (coui_btn_radio_*.xml) have a 24dp intrinsic size.
            .requiredSize(24.dp)
            .pressable(
                interactionSource = remember { MutableInteractionSource() },
                indication = sinkFeedback,
                enabled = enabled,
                delay = null,
            )
            .clip(capsuleShape)
            .drawWithCache {
                // COUI radio geometry in a 24dp viewport: ring r=9.2 stroke=1.3
                // (coui_btn_radio_off.xml), disc r=10 and center dot r=6 (coui_btn_radio_on.xml).
                val ringRadius = size.minDimension * (9.2f / 24f)
                val ringStrokeWidth = size.minDimension * (1.3f / 24f)
                val discRadius = size.minDimension * (10f / 24f)
                val centerRadius = size.minDimension * (6f / 24f)
                onDrawBehind {
                    val bounce = bounceScale.value
                    val ringAlpha = ringAlphaState.value
                    if (ringAlpha > 0f) {
                        // The group scale pulse also scales the rendered stroke width, matching
                        // VectorDrawable's matrix-scaled strokes.
                        drawCircle(
                            color = unselectedColor,
                            radius = ringRadius * bounce,
                            style = Stroke(width = ringStrokeWidth * bounce),
                            alpha = ringAlpha,
                        )
                    }
                    val discAlpha = discAlphaState.value
                    if (discAlpha > 0f) {
                        drawCircle(
                            color = color,
                            radius = discRadius * bounce,
                            alpha = discAlpha,
                        )
                    }
                    val centerScale = centerScaleState.value
                    val centerAlpha = centerAlphaState.value
                    if (centerScale > 0f && centerAlpha > 0f) {
                        drawCircle(
                            color = centerColor,
                            radius = centerRadius * centerScale,
                            alpha = centerAlpha,
                        )
                    }
                }
            }
            .then(finalModifier),
    ) {}
}

object RadioButtonDefaults {
    @Composable
    fun radioButtonColors(
        selectedColor: Color = COUITheme.colorScheme.primary,
        disabledSelectedColor: Color = COUITheme.colorScheme.disabledPrimary,
        // couiColorLabelTertiary (coui_btn_radio_off.xml ring stroke).
        unselectedColor: Color = COUITheme.colorScheme.onSurfaceVariantActions,
        // couiColorQuarternaryNeutral (coui_btn_radio_off_disabled.xml ring stroke).
        disabledUnselectedColor: Color = COUITheme.colorScheme.disabledSecondaryVariant,
        // The COUI selected center is hardcoded #ffffff with no night variant
        // (coui_btn_radio_on.xml), so it stays white in dark mode.
        centerColor: Color = Color.White,
        // coui_btn_radio_color_on_disabled (#8affffff light / #29ffffff night), which is exactly
        // the disabledOnPrimaryButton pair (coui_btn_radio_on_disabled.xml center).
        disabledCenterColor: Color = COUITheme.colorScheme.disabledOnPrimaryButton,
    ): RadioButtonColors = remember(
        selectedColor,
        disabledSelectedColor,
        unselectedColor,
        disabledUnselectedColor,
        centerColor,
        disabledCenterColor,
    ) {
        RadioButtonColors(
            selectedColor = selectedColor,
            disabledSelectedColor = disabledSelectedColor,
            unselectedColor = unselectedColor,
            disabledUnselectedColor = disabledUnselectedColor,
            centerColor = centerColor,
            disabledCenterColor = disabledCenterColor,
        )
    }
}

@Immutable
data class RadioButtonColors(
    private val selectedColor: Color,
    private val disabledSelectedColor: Color,
    private val unselectedColor: Color,
    private val disabledUnselectedColor: Color,
    private val centerColor: Color,
    private val disabledCenterColor: Color,
) {
    @Stable
    internal fun color(enabled: Boolean): Color = if (enabled) selectedColor else disabledSelectedColor

    @Stable
    internal fun unselectedColor(enabled: Boolean): Color = if (enabled) unselectedColor else disabledUnselectedColor

    @Stable
    internal fun centerColor(enabled: Boolean): Color = if (enabled) centerColor else disabledCenterColor
}
