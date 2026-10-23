// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.preference

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.BasicComponentColors
import io.github.suqi8.coui.kmp.basic.BasicComponentDefaults
import io.github.suqi8.coui.kmp.basic.CardListPosition
import io.github.suqi8.coui.kmp.basic.Checkbox
import io.github.suqi8.coui.kmp.basic.CheckboxColors
import io.github.suqi8.coui.kmp.basic.CheckboxDefaults
import io.github.suqi8.coui.kmp.basic.LocalCardListPosition
import io.github.suqi8.coui.kmp.preference.internal.StartActionSlot
import io.github.suqi8.coui.kmp.utils.CouiHapticEffect
import io.github.suqi8.coui.kmp.utils.rememberCouiHaptic

/**
 * A checkbox with a title and a summary.
 *
 * As in `COUICheckBoxPreference`, the whole row is the touch target: the checkbox is drawn without
 * its own press highlight, and toggling plays the COUI 302 haptic.
 *
 * @param title The title of the [CheckboxPreference].
 * @param checked The checked state of the [CheckboxPreference].
 * @param onCheckedChange The callback when the checked state of the [CheckboxPreference] is changed.
 * @param modifier The modifier to be applied to the [CheckboxPreference].
 * @param titleColor The color of the title.
 * @param summary The summary of the [CheckboxPreference].
 * @param summaryColor The color of the summary.
 * @param checkboxColors The [CheckboxColors] of the [CheckboxPreference].
 * @param startAction The [Composable] content on the start side of the [CheckboxPreference].
 * @param endActions The [Composable] content on the end side of the [CheckboxPreference].
 * @param checkboxLocation The location of checkbox, [CheckboxLocation.Start] or [CheckboxLocation.End].
 * @param bottomAction The [Composable] content at the bottom of the [CheckboxPreference].
 * @param insideMargin The margin inside the [CheckboxPreference].
 * @param cardListPosition The row's position inside its card group, by default the one a
 *   [io.github.suqi8.coui.kmp.basic.CardListGroup] provides. Rounded outer edges receive extra padding.
 * @param holdDownState Used to determine whether it is in the pressed state.
 * @param enabled Whether the [CheckboxPreference] is clickable.
 */
@Composable
@NonRestartableComposable
fun CheckboxPreference(
    title: String,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    checkboxColors: CheckboxColors = CheckboxDefaults.checkboxColors(),
    startAction: @Composable (() -> Unit)? = null,
    endActions: @Composable (RowScope.() -> Unit)? = null,
    checkboxLocation: CheckboxLocation = CheckboxLocation.Start,
    bottomAction: (@Composable () -> Unit)? = null,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    cardListPosition: CardListPosition = LocalCardListPosition.current,
    holdDownState: Boolean = false,
    enabled: Boolean = true,
) {
    val currentOnCheckedChange by rememberUpdatedState(onCheckedChange)
    val haptic = rememberCouiHaptic()

    BasicComponent(
        modifier = modifier,
        insideMargin = insideMargin,
        cardListPosition = cardListPosition,
        title = title,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        startAction = if (checkboxLocation == CheckboxLocation.Start || startAction != null) {
            {
                Row {
                    if (checkboxLocation == CheckboxLocation.Start) {
                        CheckboxPreferenceStartAction(
                            checked = checked,
                            enabled = enabled,
                            checkboxColors = checkboxColors,
                        )
                    }

                    startAction?.let {
                        StartActionSlot(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .weight(1f, fill = false),
                            endSpacing = 5.dp,
                            content = it,
                        )
                    }
                }
            }
        } else {
            null
        },
        endActions = {
            endActions?.let {
                Row(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .align(Alignment.CenterVertically)
                        .weight(1f, fill = false),
                ) {
                    it()
                }
            }

            if (checkboxLocation == CheckboxLocation.End) {
                CheckboxPreferenceEndAction(
                    checked = checked,
                    enabled = enabled,
                    checkboxColors = checkboxColors,
                )
            }
        },
        bottomAction = bottomAction,
        onClick = {
            currentOnCheckedChange.takeIf { enabled }?.let {
                haptic(CouiHapticEffect.Switch)
                it(!checked)
            }
        },
        role = Role.Checkbox,
        holdDownState = holdDownState,
        enabled = enabled,
    )
}

// COUI preference_checkbox.xml: the COUICheckBox is not clickable; the row handles the press.
@Composable
private fun CheckboxPreferenceStartAction(
    checked: Boolean,
    enabled: Boolean,
    checkboxColors: CheckboxColors,
) {
    Checkbox(
        modifier = Modifier
            .padding(end = 5.dp),
        state = ToggleableState(checked),
        onClick = null,
        enabled = enabled,
        colors = checkboxColors,
    )
}

@Composable
private fun CheckboxPreferenceEndAction(
    checked: Boolean,
    enabled: Boolean,
    checkboxColors: CheckboxColors,
) {
    Checkbox(
        state = ToggleableState(checked),
        onClick = null,
        enabled = enabled,
        colors = checkboxColors,
    )
}

enum class CheckboxLocation {
    Start,
    End,
}
