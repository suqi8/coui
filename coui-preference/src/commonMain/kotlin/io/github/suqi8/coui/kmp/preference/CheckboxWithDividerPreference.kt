// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.preference

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.BasicComponentColors
import io.github.suqi8.coui.kmp.basic.BasicComponentDefaults
import io.github.suqi8.coui.kmp.basic.CardListPosition
import io.github.suqi8.coui.kmp.basic.Checkbox
import io.github.suqi8.coui.kmp.basic.CheckboxColors
import io.github.suqi8.coui.kmp.basic.CheckboxDefaults
import io.github.suqi8.coui.kmp.basic.DividerDefaults
import io.github.suqi8.coui.kmp.basic.LocalCardListPosition
import io.github.suqi8.coui.kmp.preference.internal.WithDividerLayout
import io.github.suqi8.coui.kmp.preference.internal.WithDividerSeparator
import io.github.suqi8.coui.kmp.preference.internal.withDividerTrailingPadding

/**
 * A row split into a clickable main area and an independent checkbox, separated by a vertical divider
 * (COUI `COUICheckBoxWithDividerPreference`, layout `coui_check_box_divider_preference`).
 *
 * - The main area is a [BasicComponent] (`main_layout`): only it shows the row press feedback and
 *   receives the card-group padding, and a tap on it runs [onClick]
 *   (`OnMainLayoutClickListener.onMainLayoutClick`).
 * - The trailing area (`check_box_layout`) is as tall as the main area and is the checkbox's touch
 *   target: the `COUICheckBox` itself is not clickable, and a tap anywhere in the area toggles it
 *   (`check_box_layout` click -> `CheckBoxPreference.onClick`). The area draws no press feedback.
 *   COUI plays no haptic here: the class extends the androidx `CheckBoxPreference`, not
 *   `COUICheckBoxPreference` (whose touch listener plays the 302 haptic).
 *
 * @param checked The checked state of the checkbox.
 * @param onCheckedChange The callback when the checkbox is toggled.
 * @param title The title of the [CheckboxWithDividerPreference].
 * @param modifier The modifier to be applied to the [CheckboxWithDividerPreference].
 * @param onClick The callback when the main area is clicked; `null` keeps the main area non-clickable.
 * @param titleColor The color of the title.
 * @param summary The summary of the [CheckboxWithDividerPreference].
 * @param summaryColor The color of the summary.
 * @param startAction The [Composable] content on the start side of the main area.
 * @param endActions The [Composable] content on the end side of the main area, before the divider.
 * @param bottomAction The [Composable] content at the bottom of the main area.
 * @param checkboxColors The [CheckboxColors] of the checkbox.
 * @param dividerColor The color of the vertical divider (`?couiColorDivider`).
 * @param insideMargin The margin inside the main area (`support_preference_title_padding_start` less
 *   the card inset, `coui_checkbox_with_divider_title_padding_end`).
 * @param cardListPosition The row's position inside its card group, by default the one a
 *   [io.github.suqi8.coui.kmp.basic.CardListGroup] provides. Rounded outer edges receive extra padding.
 * @param holdDownState Used to determine whether the main area is in the pressed state.
 * @param enabled Whether the main area and the checkbox are enabled.
 */
@Composable
fun CheckboxWithDividerPreference(
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
    checkboxColors: CheckboxColors = CheckboxDefaults.checkboxColors(),
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
            CheckboxWithDividerTrailing(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
                checkboxColors = checkboxColors,
                dividerColor = dividerColor,
            )
        },
    )
}

@Composable
private fun CheckboxWithDividerTrailing(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean,
    checkboxColors: CheckboxColors,
    dividerColor: Color,
) {
    Row(
        modifier = Modifier
            .toggleable(
                value = checked,
                interactionSource = null,
                indication = null,
                enabled = enabled,
                role = Role.Checkbox,
                onValueChange = onCheckedChange,
            )
            .withDividerTrailingPadding(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WithDividerSeparator(color = dividerColor)
        // coui_check_box_divider_preference: the COUICheckBox is not clickable or focusable.
        Checkbox(
            state = ToggleableState(checked),
            onClick = null,
            colors = checkboxColors,
            enabled = enabled,
        )
    }
}
