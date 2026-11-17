// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChangedIgnoreConsumed
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import io.github.suqi8.coui.kmp.effect.couiFocusStroke
import io.github.suqi8.coui.kmp.effect.couiPressScale
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.utils.CouiHapticEffect
import io.github.suqi8.coui.kmp.utils.rememberCouiHaptic
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * A [Stepper] component with COUI style, after COUI 17 `COUIStepperView`.
 *
 * The minus and plus buttons are flat 32dp circles in `couiColorContainer8` with the
 * `COUIMaskEffectDrawable` press mask and the plain `COUIPressFeedbackHelper` press scale (no
 * material, rubber band or deformation). A tap steps once on finger up; holding a button steps
 * once after the long-press timeout and then every [StepperDefaults.RepeatIntervalMillis] until
 * the finger lifts or the limit is reached (`LongPressProxy`). Every step plays the OPLUS 308
 * haptic. A button turns its glyph to the disabled colour at its limit; the value itself never
 * dims. The value area has a fixed width, sized for [maxValue], so the row does not change width
 * as the value changes.
 *
 * @param value The current value of the [Stepper].
 * @param onValueChange The callback invoked with the new value when the user steps up or down.
 * @param modifier The modifier to be applied to the [Stepper].
 * @param minValue The minimum value the [Stepper] can reach (COUI default and hard limit -999).
 * @param maxValue The maximum value the [Stepper] can reach (COUI default and hard limit 9999).
 * @param step The increment/decrement applied on each step.
 * @param enabled Whether the [Stepper] is enabled.
 * @param colors The [StepperColors] of the [Stepper].
 */
@Composable
fun Stepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minValue: Int = StepperDefaults.MinValue,
    maxValue: Int = StepperDefaults.MaxValue,
    step: Int = 1,
    enabled: Boolean = true,
    colors: StepperColors = StepperDefaults.stepperColors(),
) {
    val currentValue by rememberUpdatedState(value)
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val currentMin by rememberUpdatedState(minValue)
    val currentMax by rememberUpdatedState(maxValue)
    val currentStep by rememberUpdatedState(step)
    val couiHaptic = rememberCouiHaptic()
    val currentHaptic by rememberUpdatedState(couiHaptic)

    val canDecrease = enabled && value > minValue
    val canIncrease = enabled && value < maxValue

    val textStyle = StepperDefaults.textStyle()
    val indicatorWidth = rememberStepperIndicatorWidth(maxValue, textStyle)

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
            onStep = {
                val current = currentValue
                val next = stepperNext(current, -currentStep, currentMin, currentMax)
                if (next != current) {
                    currentHaptic(CouiHapticEffect.Strength)
                    currentOnValueChange(next)
                }
            },
        )
        Box(
            modifier = Modifier.width(indicatorWidth),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = value.toString(),
                // COUIStepperView.update() only toggles the two buttons, never the value text.
                color = colors.contentColor,
                style = textStyle,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
        StepperButton(
            symbol = StepperSymbol.Plus,
            enabled = canIncrease,
            buttonColor = colors.buttonColor,
            contentColor = colors.contentColor(canIncrease),
            onStep = {
                val current = currentValue
                val next = stepperNext(current, currentStep, currentMin, currentMax)
                if (next != current) {
                    currentHaptic(CouiHapticEffect.Strength)
                    currentOnValueChange(next)
                }
            },
        )
    }
}

/** The value one [delta] away from [value], clamped to [min]..[max] (`ObservableStep.setStep`). */
internal fun stepperNext(value: Int, delta: Int, min: Int, max: Int): Int = (value.toLong() + delta).coerceIn(min.toLong(), max.toLong()).toInt()

/**
 * The number of characters `COUIStepperView.onMeasure` reserves: the length of
 * `String.valueOf(max)`, the sign included.
 */
internal fun stepperIndicatorChars(maxValue: Int): Int = maxValue.toString().length

/**
 * The fixed width of the value area: the widest digit repeated [stepperIndicatorChars] times
 * (`COUIStepperView.onMeasure` + `getNumForMaxWidth`), never below
 * [StepperDefaults.IndicatorMinWidth].
 */
@Composable
private fun rememberStepperIndicatorWidth(maxValue: Int, textStyle: TextStyle): Dp {
    val measurer = rememberTextMeasurer(cacheSize = 12)
    val density = LocalDensity.current
    return remember(maxValue, textStyle, density, measurer) {
        var widestDigit = 1
        var widest = 0
        for (digit in 0..9) {
            val width = measurer.measure(digit.toString(), textStyle, maxLines = 1).size.width
            if (width > widest) {
                widest = width
                widestDigit = digit
            }
        }
        val sample = widestDigit.toString().repeat(stepperIndicatorChars(maxValue))
        val width = with(density) { measurer.measure(sample, textStyle, maxLines = 1).size.width.toDp() }
        max(width, StepperDefaults.IndicatorMinWidth)
    }
}

private enum class StepperSymbol { Minus, Plus }

@Composable
private fun StepperButton(
    symbol: StepperSymbol,
    enabled: Boolean,
    buttonColor: Color,
    contentColor: Color,
    onStep: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val currentOnStep by rememberUpdatedState(onStep)
    val currentEnabled by rememberUpdatedState(enabled)
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .semantics {
                role = Role.Button
                contentDescription = if (symbol == StepperSymbol.Plus) "+" else "-"
                if (!enabled) disabled()
                onClick {
                    if (currentEnabled) currentOnStep()
                    currentEnabled
                }
            }
            .onKeyEvent { event ->
                val confirm = event.key == Key.Enter || event.key == Key.NumPadEnter || event.key == Key.DirectionCenter
                if (confirm && event.type == KeyEventType.KeyUp && currentEnabled) {
                    currentOnStep()
                    true
                } else {
                    false
                }
            }
            .focusable(enabled = enabled, interactionSource = interactionSource)
            // COUIStateEffectDrawable.enableScaleEffect -> COUIPressFeedbackHelper: press scale only.
            .couiPressScale(pressed = pressed, enabled = enabled)
            .size(StepperDefaults.ButtonSize)
            // COUIStepperView: a TYPE_OUTER COUIStrokeDrawable around each circular button.
            .couiFocusStroke(interactionSource = interactionSource, shape = CircleShape, enabled = enabled)
            .clip(CircleShape)
            .background(buttonColor)
            .hoverable(interactionSource, enabled)
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val press = PressInteraction.Press(down.position)
                    interactionSource.tryEmit(press)
                    var longPressed = false
                    val longPressTimeout = viewConfiguration.longPressTimeoutMillis
                    // GestureDetector.onLongPress: step at once, then every 100ms while enabled.
                    val repeat: Job = scope.launch {
                        delay(longPressTimeout)
                        longPressed = true
                        while (isActive && currentEnabled) {
                            currentOnStep()
                            delay(StepperDefaults.RepeatIntervalMillis)
                        }
                    }
                    var inTapRegion = true
                    var tapped = false
                    try {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Final)
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                            if (change.changedToUp()) {
                                tapped = inTapRegion && !longPressed && !change.isConsumed
                                if (tapped) change.consume()
                                break
                            }
                            if (change.positionChangedIgnoreConsumed() && change.isConsumed) {
                                // A scrolling parent took the gesture: ACTION_CANCEL.
                                break
                            }
                            if ((change.position - down.position).getDistance() > viewConfiguration.touchSlop) {
                                inTapRegion = false
                                if (!longPressed) repeat.cancel()
                            }
                        }
                    } finally {
                        repeat.cancel()
                        interactionSource.tryEmit(if (tapped) PressInteraction.Release(press) else PressInteraction.Cancel(press))
                    }
                    // LongPressProxy.onSingleTapUp: a tap steps on finger up.
                    if (tapped) currentOnStep()
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        // The COUIMaskEffectDrawable press / hover mask is a background layer, under the glyph.
        Box(Modifier.matchParentSize().indication(interactionSource, LocalIndication.current))
        // COUI ic_minus_sign / ic_plus_sign: a 1.6dp round-cap stroke spanning 15.2dp
        // (8.4..23.6) inside the 32dp button, drawn directly to avoid an icon dependency.
        Canvas(modifier = Modifier.size(StepperDefaults.GlyphSize)) {
            val strokeWidth = StepperDefaults.GlyphStrokeWidth.toPx()
            val cx = size.width / 2f
            val cy = size.height / 2f
            val half = (size.minDimension - strokeWidth) / 2f
            drawLine(
                color = contentColor,
                start = Offset(cx - half, cy),
                end = Offset(cx + half, cy),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round,
            )
            if (symbol == StepperSymbol.Plus) {
                drawLine(
                    color = contentColor,
                    start = Offset(cx, cy - half),
                    end = Offset(cx, cy + half),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round,
                )
            }
        }
    }
}

/** Contains default values used by [Stepper]. */
object StepperDefaults {
    /** The diameter of each stepper button (COUI stepper_button_size). */
    val ButtonSize = 32.dp

    /** The visual extent of the +/- glyph inside each button (COUI ic_minus_sign / ic_plus_sign span 8.4..23.6dp). */
    val GlyphSize = 15.2.dp

    /** The stroke width of the +/- glyph (COUI ic_minus_sign / ic_plus_sign strokeWidth). */
    val GlyphStrokeWidth = 1.6.dp

    /** The spacing between buttons and the indicator (COUI indicator horizontal margin). */
    val Spacing = 12.dp

    /** The minimum width reserved for the value indicator (COUI indicator minWidth). */
    val IndicatorMinWidth = 44.dp

    /** The default minimum value (COUI ObservableStep.MIN_VALUE, also its hard limit). */
    val MinValue = -999

    /** The default maximum value (COUI ObservableStep.MAX_VALUE, also its hard limit). */
    val MaxValue = 9999

    /** The interval between repeated steps while a button is held (COUI LongPressProxy, 100ms). */
    val RepeatIntervalMillis = 100L

    /** The default text style of the value indicator (COUIStepperViewTextDefStyle: 18sp, sans-serif-medium). */
    @Composable
    fun textStyle(): TextStyle = COUITheme.textStyles.headlineS.copy(fontWeight = FontWeight.Medium)

    /**
     * The default colors for the [Stepper]: `couiColorContainer8` buttons, `couiColorPrimaryNeutral`
     * glyphs and value, and `couiColorDivider` glyphs at a limit (ic_*_sign_disable).
     */
    @Composable
    fun stepperColors(
        buttonColor: Color = COUITheme.colorScheme.dialogButton,
        contentColor: Color = COUITheme.colorScheme.labelPrimary,
        disabledContentColor: Color = COUITheme.colorScheme.divider,
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
 * @param contentColor The color of the glyphs when enabled, and of the value.
 * @param disabledContentColor The color of a glyph whose button is disabled.
 */
@Immutable
data class StepperColors(
    val buttonColor: Color,
    val contentColor: Color,
    val disabledContentColor: Color,
) {
    @Stable
    internal fun contentColor(enabled: Boolean): Color = if (enabled) contentColor else disabledContentColor
}
