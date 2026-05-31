// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package top.yukonga.miuix.kmp.basic

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
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
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.SinkFeedback
import top.yukonga.miuix.kmp.utils.pressable

/**
 * A [RadioButton] component with Miuix style.
 *
 * Displays a checkmark indicator when selected, matching the miuix-classic SingleChoicePreference
 * visual style. When unselected, no indicator is shown.
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

    val checkAlphaState = transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                tween(durationMillis = 10, easing = FastOutSlowInEasing)
            } else {
                tween(durationMillis = 150, easing = FastOutSlowInEasing)
            }
        },
        label = "CheckAlpha",
    ) { if (it) 1f else 0f }

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
            .requiredSize(26.dp)
            .pressable(
                interactionSource = remember { MutableInteractionSource() },
                indication = sinkFeedback,
                enabled = enabled,
                delay = null,
            )
            .clip(capsuleShape)
            .drawWithCache {
                onDrawBehind {
                    // COUI radio button: an outer ring always present, plus a filled inner dot
                    // when selected (instead of miuix-classic's checkmark).
                    val ringStrokeWidth = size.width * 0.09f
                    val selected = checkAlphaState.value
                    // Outer ring: unselected color blends to selected color.
                    drawCircle(
                        color = lerp(unselectedColor, color, selected),
                        radius = (size.minDimension - ringStrokeWidth) / 2f,
                        style = Stroke(width = ringStrokeWidth),
                    )
                    // Inner dot: scales/fades in when selected.
                    if (selected > 0f) {
                        drawCircle(
                            color = color,
                            radius = size.minDimension * 0.28f * selected,
                            alpha = selected,
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
        selectedColor: Color = MiuixTheme.colorScheme.primary,
        disabledSelectedColor: Color = MiuixTheme.colorScheme.disabledPrimary,
        unselectedColor: Color = MiuixTheme.colorScheme.secondaryContainerVariant,
        disabledUnselectedColor: Color = MiuixTheme.colorScheme.disabledSecondary,
    ): RadioButtonColors = remember(
        selectedColor,
        disabledSelectedColor,
        unselectedColor,
        disabledUnselectedColor,
    ) {
        RadioButtonColors(
            selectedColor = selectedColor,
            disabledSelectedColor = disabledSelectedColor,
            unselectedColor = unselectedColor,
            disabledUnselectedColor = disabledUnselectedColor,
        )
    }
}

@Immutable
data class RadioButtonColors(
    private val selectedColor: Color,
    private val disabledSelectedColor: Color,
    private val unselectedColor: Color,
    private val disabledUnselectedColor: Color,
) {
    internal fun color(enabled: Boolean): Color = if (enabled) selectedColor else disabledSelectedColor

    @Stable
    internal fun unselectedColor(enabled: Boolean): Color = if (enabled) unselectedColor else disabledUnselectedColor
}
