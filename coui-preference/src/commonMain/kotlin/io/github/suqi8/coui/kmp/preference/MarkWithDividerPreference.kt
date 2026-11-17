// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.preference

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.BasicComponentColors
import io.github.suqi8.coui.kmp.basic.BasicComponentDefaults
import io.github.suqi8.coui.kmp.basic.CardListPosition
import io.github.suqi8.coui.kmp.basic.DividerDefaults
import io.github.suqi8.coui.kmp.basic.LocalCardListPosition
import io.github.suqi8.coui.kmp.basic.RadioButton
import io.github.suqi8.coui.kmp.basic.RadioButtonColors
import io.github.suqi8.coui.kmp.basic.RadioButtonDefaults
import io.github.suqi8.coui.kmp.preference.internal.WithDividerLayout
import io.github.suqi8.coui.kmp.preference.internal.WithDividerSeparator
import io.github.suqi8.coui.kmp.preference.internal.withDividerTrailingPadding

/**
 * A row split into a clickable main area and an independent selection mark, separated by a vertical
 * divider (COUI `COUIMarkWithDividerPreference`, layout `coui_mark_with_divider_preference`, style
 * `Preference.COUI.COUIRadioWithDividerPreference`): tapping the title opens a detail page while the
 * mark selects the row in place.
 *
 * - The main area is a [BasicComponent] (`main_layout`, a `ListSelectedItemLayout` with
 *   `coui_mark_with_divider_title_padding_start` 32dp / `_end` 16dp): only it shows the row press
 *   feedback and receives the card-group padding, and a tap on it runs [onClick]
 *   (`OnMainLayoutClickListener.onMainLayoutClick`).
 * - The trailing area (`radio_layout`, wrap_content x match_parent) is as tall as the main area and
 *   is the mark's touch target: the `coui_tail_mark` radio button is neither clickable nor
 *   focusable, and a tap anywhere in the area runs [onCheckedChange] with the inverted state
 *   (`radio_layout` click -> `TwoStatePreference.onClick`). It draws no press feedback and plays no
 *   haptic: `COUIMarkPreference` extends the androidx `CheckBoxPreference` without a touch haptic,
 *   and the area disables its click sound (`soundEffectsEnabled="false"`).
 *
 * Rows are usually used as a single-select group: the caller moves the mark to the row whose mark
 * area was tapped and typically ignores the `false` callback of an already selected row.
 *
 * @param checked Whether this row currently carries the mark.
 * @param onCheckedChange The callback when the mark area is tapped, with the inverted [checked] state.
 * @param title The title of the [MarkWithDividerPreference].
 * @param modifier The modifier to be applied to the [MarkWithDividerPreference].
 * @param onClick The callback when the main area is clicked; `null` keeps the main area non-clickable.
 * @param titleColor The color of the title.
 * @param summary The summary of the [MarkWithDividerPreference].
 * @param summaryColor The color of the summary.
 * @param startAction The [Composable] content on the start side of the main area.
 * @param endActions The [Composable] content on the end side of the main area, before the divider.
 * @param bottomAction The [Composable] content at the bottom of the main area.
 * @param markColors The [RadioButtonColors] of the mark (`coui_btn_check_mark`).
 * @param dividerColor The color of the vertical divider (`?couiColorDivider`).
 * @param insideMargin The margin inside the main area (`coui_mark_with_divider_title_padding_start`
 *   / `_end`, less the card inset at the start).
 * @param cardListPosition The row's position inside its card group, by default the one a
 *   [io.github.suqi8.coui.kmp.basic.CardListGroup] provides. Rounded outer edges receive extra padding.
 * @param holdDownState Used to determine whether the main area is in the pressed state.
 * @param enabled Whether the main area and the mark are enabled.
 */
@Composable
fun MarkWithDividerPreference(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    startAction: @Composable (() -> Unit)? = null,
    endActions: @Composable (RowScope.() -> Unit)? = null,
    bottomAction: (@Composable () -> Unit)? = null,
    markColors: RadioButtonColors = RadioButtonDefaults.radioButtonColors(),
    dividerColor: Color = DividerDefaults.DividerColor,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    cardListPosition: CardListPosition = LocalCardListPosition.current,
    holdDownState: Boolean = false,
    enabled: Boolean = true,
) {
    WithDividerLayout(
        modifier = modifier,
        main = {
            BasicComponent(
                insideMargin = insideMargin,
                cardListPosition = cardListPosition,
                title = title,
                titleColor = titleColor,
                summary = summary,
                summaryColor = summaryColor,
                startAction = startAction,
                endActions = endActions,
                bottomAction = bottomAction,
                onClick = onClick,
                holdDownState = holdDownState,
                enabled = enabled,
            )
        },
        trailing = {
            MarkWithDividerTrailing(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
                markColors = markColors,
                dividerColor = dividerColor,
            )
        },
    )
}

@Composable
private fun MarkWithDividerTrailing(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean,
    markColors: RadioButtonColors,
    dividerColor: Color,
) {
    val currentChecked by rememberUpdatedState(checked)
    val currentOnCheckedChange by rememberUpdatedState(onCheckedChange)
    Row(
        modifier = Modifier
            .selectable(
                selected = checked,
                interactionSource = null,
                indication = null,
                enabled = enabled,
                role = Role.RadioButton,
                onClick = { currentOnCheckedChange(!currentChecked) },
            )
            .withDividerTrailingPadding(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WithDividerSeparator(color = dividerColor)
        // coui_mark_with_divider_preference: the coui_tail_mark radio button is not clickable or
        // focusable; its end margin (support_preference_title_padding_end) is the trailing padding.
        RadioButton(
            selected = checked,
            onClick = null,
            enabled = enabled,
            colors = markColors,
        )
    }
}
