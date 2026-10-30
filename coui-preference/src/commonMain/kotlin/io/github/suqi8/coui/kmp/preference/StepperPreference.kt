// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.preference

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.BasicComponentColors
import io.github.suqi8.coui.kmp.basic.BasicComponentDefaults
import io.github.suqi8.coui.kmp.basic.CardListPosition
import io.github.suqi8.coui.kmp.basic.LocalCardListPosition
import io.github.suqi8.coui.kmp.basic.Stepper
import io.github.suqi8.coui.kmp.basic.StepperColors
import io.github.suqi8.coui.kmp.basic.StepperDefaults

/**
 * A preference row with a [Stepper] at the end, mirroring COUIStepperPreference.
 *
 * COUI keeps the plain COUIPreference layout and only sets `android:widgetLayout` to
 * coui_preference_widget_stepper_view.xml, a `COUIStepperView` in `couiStepperViewStyle`; the
 * preference forwards couiMinimum (default -999), couiMaximum (default 9999), couiDefStep and
 * couiUnit (the step, default 1) to it. The stepper fires the change listener only when the value
 * really changes, which [Stepper] already guarantees.
 *
 * In coui_preference.xml only the text block is padded vertically (PreferenceContent); the
 * `widget_frame` is not, so the 32dp stepper fits a 48dp row without growing it. The stepper may
 * therefore extend into the vertical [insideMargin], keeping the row as tall as a plain
 * [ArrowPreference] or [SwitchPreference].
 *
 * @param value The current value of the stepper.
 * @param onValueChange The callback with the new value when the user steps up or down.
 * @param title The title of the [StepperPreference].
 * @param modifier The modifier to be applied to the [StepperPreference].
 * @param titleColor The color of the title.
 * @param summary The summary of the [StepperPreference].
 * @param summaryColor The color of the summary.
 * @param minValue The minimum value of the stepper (COUI couiMinimum).
 * @param maxValue The maximum value of the stepper (COUI couiMaximum).
 * @param step The increment/decrement of each step (COUI couiUnit).
 * @param stepperColors The [StepperColors] of the stepper.
 * @param startAction The [Composable] content on the start side of the [StepperPreference].
 * @param bottomAction The [Composable] content at the bottom of the [StepperPreference].
 * @param insideMargin The margin inside the [StepperPreference].
 * @param cardListPosition The row's position inside its card group, by default the one a
 *   [io.github.suqi8.coui.kmp.basic.CardListGroup] provides. Rounded outer edges receive extra padding.
 * @param enabled Whether the [StepperPreference] and its stepper are enabled.
 */
@Composable
@NonRestartableComposable
fun StepperPreference(
    value: Int,
    onValueChange: (Int) -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    minValue: Int = StepperDefaults.MinValue,
    maxValue: Int = StepperDefaults.MaxValue,
    step: Int = 1,
    stepperColors: StepperColors = StepperDefaults.stepperColors(),
    startAction: @Composable (() -> Unit)? = null,
    bottomAction: (@Composable () -> Unit)? = null,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    cardListPosition: CardListPosition = LocalCardListPosition.current,
    enabled: Boolean = true,
) {
    BasicComponent(
        modifier = modifier,
        insideMargin = insideMargin,
        cardListPosition = cardListPosition,
        title = title,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        startAction = startAction,
        endActions = {
            Stepper(
                value = value,
                onValueChange = onValueChange,
                modifier = remember(insideMargin) { Modifier.overlapVerticalPadding(insideMargin) },
                minValue = minValue,
                maxValue = maxValue,
                step = step,
                enabled = enabled,
                colors = stepperColors,
            )
        },
        bottomAction = bottomAction,
        enabled = enabled,
    )
}

/**
 * Lets the widget reach into the row's vertical [padding] like an unpadded COUI `widget_frame`: it
 * reports its height minus that padding (never below zero) and is drawn centered on the reported
 * box, so the row is as tall as its text block and padding, or as the widget when that is taller.
 */
private fun Modifier.overlapVerticalPadding(padding: PaddingValues): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints.copy(minHeight = 0))
    val overlap = (padding.calculateTopPadding() + padding.calculateBottomPadding()).roundToPx()
    val height = (placeable.height - overlap).coerceAtLeast(0)
    layout(placeable.width, height) {
        placeable.place(0, (height - placeable.height) / 2)
    }
}
