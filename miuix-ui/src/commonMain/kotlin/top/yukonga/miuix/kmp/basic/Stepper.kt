// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package top.yukonga.miuix.kmp.basic

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.CouiHapticEffect
import top.yukonga.miuix.kmp.utils.rememberCouiHaptic

/**
 * A [Stepper] component with COUI style.
 *
 * A stepper lets the user adjust an integer [value] in fixed [step] increments using a minus and a
 * plus button, mirroring COUI's COUIStepperView (32dp circular buttons, OPLUS step haptic).
 *
 * @param value The current value of the [Stepper].
 * @param onValueChange The callback invoked with the new value when the user steps up or down.
 * @param modifier The modifier to be applied to the [Stepper].
 * @param minValue The minimum value the [Stepper] can reach.
 * @param maxValue The maximum value the [Stepper] can reach.
 * @param step The increment/decrement applied on each button press.
 * @param enabled Whether the [Stepper] is enabled.
 * @param colors The [StepperColors] of the [Stepper].
 */
@Composable
fun Stepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minValue: Int = Int.MIN_VALUE,
    maxValue: Int = Int.MAX_VALUE,
    step: Int = 1,
    enabled: Boolean = true,
    colors: StepperColors = StepperDefaults.stepperColors(),
) {
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val couiHaptic = rememberCouiHaptic()
    val currentHaptic by rememberUpdatedState(couiHaptic)

    val canDecrease = enabled && value > minValue
    val canIncrease = enabled && value < maxValue

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(StepperDefaults.Spacing),
    ) {
        StepperButton(
            symbol = StepperSymbol.Minus,
            enabled = canDecrease,
            buttonColor = colors.buttonColor,
            contentColor = colors.contentColor(canDecrease),
            onClick = {
                val next = (value - step).coerceAtLeast(minValue)
                if (next != value) {
                    currentOnValueChange(next)
                    currentHaptic(CouiHapticEffect.Strength)
                }
            },
        )
        Box(
            modifier = Modifier.widthIn(min = StepperDefaults.IndicatorMinWidth),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = value.toString(),
                color = colors.contentColor(enabled),
                style = StepperDefaults.textStyle(),
                textAlign = TextAlign.Center,
            )
        }
        StepperButton(
            symbol = StepperSymbol.Plus,
            enabled = canIncrease,
            buttonColor = colors.buttonColor,
            contentColor = colors.contentColor(canIncrease),
            onClick = {
                val next = (value + step).coerceAtMost(maxValue)
                if (next != value) {
                    currentOnValueChange(next)
                    currentHaptic(CouiHapticEffect.Strength)
                }
            },
        )
    }
}

private enum class StepperSymbol { Minus, Plus }

@Composable
private fun StepperButton(
    symbol: StepperSymbol,
    enabled: Boolean,
    buttonColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    IconButton(
        onClick = onClick,
        enabled = enabled,
        backgroundColor = buttonColor,
        cornerRadius = StepperDefaults.ButtonSize / 2,
        minWidth = StepperDefaults.ButtonSize,
        minHeight = StepperDefaults.ButtonSize,
    ) {
        Box(
            modifier = Modifier.size(StepperDefaults.ButtonSize),
            contentAlignment = Alignment.Center,
        ) {
            // Draw the minus/plus glyph with simple strokes so no icon dependency is needed.
            androidx.compose.foundation.Canvas(
                modifier = Modifier.size(StepperDefaults.GlyphSize),
            ) {
                val strokeW = size.minDimension * 0.12f
                val cx = size.width / 2f
                val cy = size.height / 2f
                val half = size.minDimension * 0.32f
                drawLine(
                    color = contentColor,
                    start = androidx.compose.ui.geometry.Offset(cx - half, cy),
                    end = androidx.compose.ui.geometry.Offset(cx + half, cy),
                    strokeWidth = strokeW,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round,
                )
                if (symbol == StepperSymbol.Plus) {
                    drawLine(
                        color = contentColor,
                        start = androidx.compose.ui.geometry.Offset(cx, cy - half),
                        end = androidx.compose.ui.geometry.Offset(cx, cy + half),
                        strokeWidth = strokeW,
                        cap = androidx.compose.ui.graphics.StrokeCap.Round,
                    )
                }
            }
        }
    }
}

/** Contains default values used by [Stepper]. */
object StepperDefaults {
    /** The diameter of each stepper button (COUI stepper_button_size). */
    val ButtonSize: Dp = 32.dp

    /** The size of the +/- glyph inside each button. */
    val GlyphSize: Dp = 18.dp

    /** The spacing between buttons and the indicator. */
    val Spacing: Dp = 4.dp

    /** The minimum width reserved for the value indicator. */
    val IndicatorMinWidth: Dp = 40.dp

    /** The default text style of the value indicator. */
    @Composable
    fun textStyle(): TextStyle = MiuixTheme.textStyles.title4

    /** The default colors for the [Stepper]. */
    @Composable
    fun stepperColors(
        buttonColor: Color = MiuixTheme.colorScheme.secondaryContainer,
        contentColor: Color = MiuixTheme.colorScheme.onSurface,
        disabledContentColor: Color = MiuixTheme.colorScheme.disabledOnSurface,
    ): StepperColors = remember(buttonColor, contentColor, disabledContentColor) {
        StepperColors(
            buttonColor = buttonColor,
            contentColor = contentColor,
            disabledContentColor = disabledContentColor,
        )
    }
}

/**
 * Colors used by a [Stepper].
 *
 * @param buttonColor The circular background color of the +/- buttons.
 * @param contentColor The color of the glyphs and the value when enabled.
 * @param disabledContentColor The color of the glyphs and the value when disabled.
 */
@androidx.compose.runtime.Immutable
data class StepperColors(
    val buttonColor: Color,
    val contentColor: Color,
    val disabledContentColor: Color,
) {
    @androidx.compose.runtime.Stable
    internal fun contentColor(enabled: Boolean): Color = if (enabled) contentColor else disabledContentColor
}
