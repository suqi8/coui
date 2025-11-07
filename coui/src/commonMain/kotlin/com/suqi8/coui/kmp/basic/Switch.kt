// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.basic

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.theme.COUITheme

/**
 * A [Switch] component with Miuix style.
 *
 * @param checked The checked state of the [Switch].
 * @param onCheckedChange The callback to be called when the state of the [Switch] changes.
 * @param modifier The modifier to be applied to the [Switch].
 * @param colors The [SwitchColors] of the [Switch].
 * @param enabled Whether the [Switch] is enabled.
 */
@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    colors: SwitchColors = SwitchDefaults.switchColors(),
    enabled: Boolean = true
) {
    val trackWidth = 38.dp
    val trackHeight = 24.dp
    val thumbSize = 18.dp
    val thumbMargin = 3.dp

    val isPressed by rememberUpdatedState(false)
    val toggleDuration = 383
    val scaleUpDuration = 133
    val toggleEasing = CubicBezierEasing(0.3f, 0.0f, 0.1f, 1.0f)

    val transition = updateTransition(checked, label = "SwitchTransition")

    val trackColor by animateColorAsState(
        targetValue = if (checked) colors.checkedTrackColor(enabled) else colors.uncheckedTrackColor(enabled),
        animationSpec = tween(durationMillis = 450),
        label = "TrackColor"
    )

    val thumbMoveRange = trackWidth - thumbSize - (thumbMargin * 2)
    val thumbOffset by transition.animateDp(
        transitionSpec = { tween(durationMillis = toggleDuration, easing = toggleEasing) },
        label = "ThumbOffset"
    ) { if (it) thumbMoveRange else 0.dp }

    val toggleScaleX by transition.animateFloat(
        transitionSpec = {
            keyframes {
                durationMillis = toggleDuration
                1.0f at 0 using toggleEasing
                1.3f at scaleUpDuration using toggleEasing
                1.0f at toggleDuration
            }
        },
        label = "ThumbScaleX"
    ) { 1f }

    val pressScale = remember { Animatable(1f) }
    LaunchedEffect(isPressed) {
        pressScale.animateTo(
            if (isPressed) 0.9f else 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    val finalScaleX = toggleScaleX * pressScale.value
    val finalScaleY = pressScale.value

    val interactionSource = remember { MutableInteractionSource() }

    val toggleableModifier = if (onCheckedChange != null) {
        Modifier.toggleable(
            value = checked,
            onValueChange = onCheckedChange,
            enabled = enabled,
            role = Role.Switch,
            interactionSource = interactionSource,
            indication = null
        )
    } else Modifier

    Box(
        modifier = modifier
            .then(toggleableModifier)
            .wrapContentSize(Alignment.Center)
            .requiredSize(trackWidth, trackHeight)
            .clip(RoundedCornerShape(50))
            .background(trackColor)
            .padding(thumbMargin)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = thumbOffset)
                .requiredSize(thumbSize)
                .graphicsLayer {
                    scaleX = finalScaleX
                    scaleY = finalScaleY
                    shadowElevation = 2.dp.toPx()
                    shape = CircleShape
                    clip = true
                }
                .background(
                    if (checked) colors.checkedThumbColor(enabled)
                    else colors.uncheckedThumbColor(enabled)
                )
        )
    }
}

object SwitchDefaults {

    /**
     * The default colors for the [Switch].
     */
    @Composable
    fun switchColors(
        checkedThumbColor: Color = COUITheme.colorScheme.onPrimary,
        uncheckedThumbColor: Color = COUITheme.colorScheme.onSecondary,
        disabledCheckedThumbColor: Color = COUITheme.colorScheme.disabledOnPrimary,
        disabledUncheckedThumbColor: Color = COUITheme.colorScheme.disabledOnSecondary,
        checkedTrackColor: Color = COUITheme.colorScheme.primary,
        uncheckedTrackColor: Color = COUITheme.colorScheme.secondary,
        disabledCheckedTrackColor: Color = COUITheme.colorScheme.disabledPrimary,
        disabledUncheckedTrackColor: Color = COUITheme.colorScheme.disabledSecondary
    ): SwitchColors = SwitchColors(
        checkedThumbColor = checkedThumbColor,
        uncheckedThumbColor = uncheckedThumbColor,
        disabledCheckedThumbColor = disabledCheckedThumbColor,
        disabledUncheckedThumbColor = disabledUncheckedThumbColor,
        checkedTrackColor = checkedTrackColor,
        uncheckedTrackColor = uncheckedTrackColor,
        disabledCheckedTrackColor = disabledCheckedTrackColor,
        disabledUncheckedTrackColor = disabledUncheckedTrackColor
    )
}

@Immutable
class SwitchColors(
    private val checkedThumbColor: Color,
    private val uncheckedThumbColor: Color,
    private val disabledCheckedThumbColor: Color,
    private val disabledUncheckedThumbColor: Color,
    private val checkedTrackColor: Color,
    private val uncheckedTrackColor: Color,
    private val disabledCheckedTrackColor: Color,
    private val disabledUncheckedTrackColor: Color
) {
    @Stable
    internal fun checkedThumbColor(enabled: Boolean): Color =
        if (enabled) checkedThumbColor else disabledCheckedThumbColor

    @Stable
    internal fun uncheckedThumbColor(enabled: Boolean): Color =
        if (enabled) uncheckedThumbColor else disabledUncheckedThumbColor

    @Stable
    internal fun checkedTrackColor(enabled: Boolean): Color =
        if (enabled) checkedTrackColor else disabledCheckedTrackColor

    @Stable
    internal fun uncheckedTrackColor(enabled: Boolean): Color =
        if (enabled) uncheckedTrackColor else disabledUncheckedTrackColor
}
