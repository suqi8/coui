// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package top.yukonga.miuix.kmp.basic

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.theme.LocalColors
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.MiuixTheme.isDynamicColor

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
    enabled: Boolean = true,
) {
    val currentOnCheckedChange by rememberUpdatedState(onCheckedChange)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isDragged by interactionSource.collectIsDraggedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()

    val hapticFeedback = LocalHapticFeedback.current
    val currentHapticFeedback by rememberUpdatedState(hapticFeedback)

    val capsuleShape = CircleShape
    val thumbOffsetSpringSpec = remember { spring<Dp>(dampingRatio = 0.7f, stiffness = 987f) }
    val thumbSquashSpringSpec = remember { spring<Float>(dampingRatio = 0.6f, stiffness = 987f) }

    // COUI switch is tap-driven: the thumb only animates between the off/on positions on toggle,
    // it never tracks the finger (COUISwitch.onTouchEvent has no ACTION_MOVE).
    val thumbOffsetState = animateDpAsState(
        targetValue = if (checked) SwitchDefaults.ThumbEndOffset else SwitchDefaults.ThumbStartOffset,
        animationSpec = thumbOffsetSpringSpec,
    )

    // COUI has no uniform press-scale on the thumb. The only thumb deform is a brief horizontal
    // squash-stretch (scaleX -> 1.3) during the off<->on toggle slide.
    val thumbSquashState = animateFloatAsState(
        targetValue = if (enabled && (isPressed || isDragged)) 1.3f else 1f,
        animationSpec = thumbSquashSpringSpec,
    )

    val thumbColorState = animateColorAsState(
        if (checked) colors.checkedThumbColor(enabled) else colors.uncheckedThumbColor(enabled),
    )

    val backgroundColorState = animateColorAsState(
        if (checked) colors.checkedTrackColor(enabled) else colors.uncheckedTrackColor(enabled),
        animationSpec = spring(dampingRatio = 0.99f, stiffness = 438.6f),
    )

    // COUI press/hover feedback is a translucent black overlay on the track (coui_color_press /
    // coui_color_hover), not a thumb scale.
    val pressOverlayState = animateColorAsState(
        when {
            !enabled -> Color.Transparent
            isPressed || isDragged -> Color.Black.copy(alpha = 0.12f)
            isHovered -> Color.Black.copy(alpha = 0.078f)
            else -> Color.Transparent
        },
    )

    val hasCallback = onCheckedChange != null
    val toggleableModifier = if (hasCallback) {
        remember(checked, enabled, interactionSource) {
            Modifier.toggleable(
                value = checked,
                onValueChange = { v ->
                    currentOnCheckedChange?.invoke(v)
                    currentHapticFeedback.performHapticFeedback(
                        if (v) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff,
                    )
                },
                enabled = enabled,
                role = Role.Switch,
                interactionSource = interactionSource,
            )
        }
    } else {
        Modifier.semantics {
            role = Role.Switch
            toggleableState = ToggleableState(checked)
            if (!enabled) disabled()
        }
    }

    Box(
        modifier = modifier
            .wrapContentSize(Alignment.Center)
            .size(SwitchDefaults.TrackWidth, SwitchDefaults.TrackHeight)
            .clip(capsuleShape)
            .drawBehind {
                drawRect(backgroundColorState.value)
                drawRect(pressOverlayState.value)
            }
            .hoverable(
                interactionSource = interactionSource,
                enabled = enabled,
            )
            .then(toggleableModifier),
    ) {
        Box(
            modifier = Modifier
                .size(SwitchDefaults.ThumbSize)
                .align(Alignment.CenterStart)
                .offset {
                    IntOffset(thumbOffsetState.value.roundToPx(), 0)
                }
                .graphicsLayer {
                    scaleX = thumbSquashState.value
                }
                .drawBehind {
                    drawCircle(color = thumbColorState.value)
                },
        )
    }
}

object SwitchDefaults {

    /** The track width of the [Switch] (COUISwitch bar_width). */
    val TrackWidth: Dp = 38.dp

    /** The track height of the [Switch] (COUISwitch bar_height). */
    val TrackHeight: Dp = 24.dp

    /** The diameter of the [Switch] thumb (COUISwitch outer_circle_width). */
    val ThumbSize: Dp = 18.dp

    /** The inset of the thumb from the track edge when fully on one side (COUISwitch circle_padding). */
    private val ThumbMargin: Dp = 3.dp

    /** The thumb offset in the unchecked (off) position. */
    val ThumbStartOffset: Dp = ThumbMargin

    /** The thumb offset in the checked (on) position. */
    val ThumbEndOffset: Dp = TrackWidth - ThumbSize - ThumbMargin

    /** The distance the thumb travels between off and on. */
    val Travel: Dp = ThumbEndOffset - ThumbStartOffset

    /**
     * The default colors for the [Switch].
     */
    @Composable
    fun switchColors(
        checkedThumbColor: Color = if (isDynamicColor) LocalColors.current.onPrimary else MiuixTheme.colorScheme.onPrimary,
        uncheckedThumbColor: Color = if (isDynamicColor) LocalColors.current.onSurface.copy(0.38f) else MiuixTheme.colorScheme.onSecondary,
        disabledCheckedThumbColor: Color = if (isDynamicColor) LocalColors.current.surface else MiuixTheme.colorScheme.disabledOnPrimary,
        disabledUncheckedThumbColor: Color = MiuixTheme.colorScheme.disabledOnSecondary,
        checkedTrackColor: Color = MiuixTheme.colorScheme.primary,
        uncheckedTrackColor: Color = MiuixTheme.colorScheme.secondary,
        disabledCheckedTrackColor: Color = MiuixTheme.colorScheme.disabledPrimary,
        disabledUncheckedTrackColor: Color = MiuixTheme.colorScheme.disabledSecondary,
    ): SwitchColors = remember(
        checkedThumbColor,
        uncheckedThumbColor,
        disabledCheckedThumbColor,
        disabledUncheckedThumbColor,
        checkedTrackColor,
        uncheckedTrackColor,
        disabledCheckedTrackColor,
        disabledUncheckedTrackColor,
    ) {
        SwitchColors(
            checkedThumbColor = checkedThumbColor,
            uncheckedThumbColor = uncheckedThumbColor,
            disabledCheckedThumbColor = disabledCheckedThumbColor,
            disabledUncheckedThumbColor = disabledUncheckedThumbColor,
            checkedTrackColor = checkedTrackColor,
            uncheckedTrackColor = uncheckedTrackColor,
            disabledCheckedTrackColor = disabledCheckedTrackColor,
            disabledUncheckedTrackColor = disabledUncheckedTrackColor,
        )
    }
}

@Immutable
data class SwitchColors(
    private val checkedThumbColor: Color,
    private val uncheckedThumbColor: Color,
    private val disabledCheckedThumbColor: Color,
    private val disabledUncheckedThumbColor: Color,
    private val checkedTrackColor: Color,
    private val uncheckedTrackColor: Color,
    private val disabledCheckedTrackColor: Color,
    private val disabledUncheckedTrackColor: Color,
) {
    @Stable
    internal fun checkedThumbColor(enabled: Boolean): Color = if (enabled) checkedThumbColor else disabledCheckedThumbColor

    @Stable
    internal fun uncheckedThumbColor(enabled: Boolean): Color = if (enabled) uncheckedThumbColor else disabledUncheckedThumbColor

    @Stable
    internal fun checkedTrackColor(enabled: Boolean): Color = if (enabled) checkedTrackColor else disabledCheckedTrackColor

    @Stable
    internal fun uncheckedTrackColor(enabled: Boolean): Color = if (enabled) uncheckedTrackColor else disabledUncheckedTrackColor
}
