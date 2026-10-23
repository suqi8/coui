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
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.BasicComponentColors
import io.github.suqi8.coui.kmp.basic.BasicComponentDefaults
import io.github.suqi8.coui.kmp.basic.CardListPosition
import io.github.suqi8.coui.kmp.basic.RadioButton
import io.github.suqi8.coui.kmp.basic.RadioButtonColors
import io.github.suqi8.coui.kmp.basic.RadioButtonDefaults
import io.github.suqi8.coui.kmp.preference.internal.StartActionSlot

/**
 * A radio button with a title and a summary.
 *
 * As in the COUI radio and mark preferences, the whole row is the touch target: the radio button
 * is not clickable itself and shows no press highlight of its own.
 *
 * @param title The title of the [RadioButtonPreference].
 * @param selected The selected state of the [RadioButtonPreference].
 * @param onClick The callback when the [RadioButtonPreference] is clicked.
 * @param modifier The modifier to be applied to the [RadioButtonPreference].
 * @param titleColor The color of the title.
 * @param summary The summary of the [RadioButtonPreference].
 * @param summaryColor The color of the summary.
 * @param radioButtonColors The [RadioButtonColors] of the [RadioButtonPreference].
 * @param startAction The [Composable] content on the start side of the [RadioButtonPreference].
 * @param endActions The [Composable] content on the end side of the [RadioButtonPreference].
 * @param radioButtonLocation The location of radio button, [RadioButtonLocation.Start] or [RadioButtonLocation.End].
 * @param bottomAction The [Composable] content at the bottom of the [RadioButtonPreference].
 * @param insideMargin The margin inside the [RadioButtonPreference].
 * @param cardListPosition The row's position inside its card group. Rounded outer edges receive extra padding.
 * @param holdDownState Used to determine whether it is in the pressed state.
 * @param enabled Whether the [RadioButtonPreference] is clickable.
 */
@Composable
@NonRestartableComposable
fun RadioButtonPreference(
    title: String,
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    radioButtonColors: RadioButtonColors = RadioButtonDefaults.radioButtonColors(),
    startAction: @Composable (() -> Unit)? = null,
    endActions: @Composable (RowScope.() -> Unit)? = null,
    radioButtonLocation: RadioButtonLocation = RadioButtonLocation.Start,
    bottomAction: (@Composable () -> Unit)? = null,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    cardListPosition: CardListPosition = CardListPosition.None,
    holdDownState: Boolean = false,
    enabled: Boolean = true,
) {
    val currentOnClick by rememberUpdatedState(onClick)

    BasicComponent(
        modifier = modifier,
        insideMargin = insideMargin,
        cardListPosition = cardListPosition,
        title = title,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        startAction = if (radioButtonLocation == RadioButtonLocation.Start || startAction != null) {
            {
                Row {
                    if (radioButtonLocation == RadioButtonLocation.Start) {
                        RadioButtonPreferenceStartAction(
                            selected = selected,
                            enabled = enabled,
                            radioButtonColors = radioButtonColors,
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

            if (radioButtonLocation == RadioButtonLocation.End) {
                RadioButtonPreferenceEndAction(
                    selected = selected,
                    enabled = enabled,
                    radioButtonColors = radioButtonColors,
                )
            }
        },
        bottomAction = bottomAction,
        onClick = {
            currentOnClick.takeIf { enabled }?.invoke()
        },
        role = Role.RadioButton,
        holdDownState = holdDownState,
        enabled = enabled,
    )
}

// COUI coui_preference_widget_mark.xml: the radio is not clickable; the row handles the press.
@Composable
private fun RadioButtonPreferenceStartAction(
    selected: Boolean,
    enabled: Boolean,
    radioButtonColors: RadioButtonColors,
) {
    RadioButton(
        modifier = Modifier
            .padding(end = 5.dp),
        selected = selected,
        onClick = null,
        enabled = enabled,
        colors = radioButtonColors,
    )
}

@Composable
private fun RadioButtonPreferenceEndAction(
    selected: Boolean,
    enabled: Boolean,
    radioButtonColors: RadioButtonColors,
) {
    RadioButton(
        selected = selected,
        onClick = null,
        enabled = enabled,
        colors = radioButtonColors,
    )
}

enum class RadioButtonLocation {
    Start,
    End,
}
