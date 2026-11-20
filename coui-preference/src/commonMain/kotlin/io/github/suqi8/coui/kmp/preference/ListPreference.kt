// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.preference

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.BasicComponentColors
import io.github.suqi8.coui.kmp.basic.BasicComponentDefaults
import io.github.suqi8.coui.kmp.basic.CardListPosition
import io.github.suqi8.coui.kmp.basic.HorizontalDivider
import io.github.suqi8.coui.kmp.basic.LocalCardListPosition
import io.github.suqi8.coui.kmp.basic.RadioButton
import io.github.suqi8.coui.kmp.basic.RadioButtonDefaults
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.layout.DialogButtonBar
import io.github.suqi8.coui.kmp.layout.DialogButtonBarAction
import io.github.suqi8.coui.kmp.layout.DialogDefaults
import io.github.suqi8.coui.kmp.layout.ListDialogDefaults
import io.github.suqi8.coui.kmp.overlay.OverlayDialog
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * An entry of a [ListPreference] / [MultiSelectListPreference] selection dialog.
 *
 * @param text The text of the entry.
 * @param summary The optional summary shown below the text.
 * @param enabled Whether the entry can be selected.
 */
@Immutable
data class ListPreferenceEntry(
    val text: String,
    val summary: String? = null,
    val enabled: Boolean = true,
)

/**
 * A list preference with a single-choice selection dialog, mirroring COUIListPreference and
 * COUIListPreferenceDialogFragment.
 *
 * The row is a jump row (`Preference.COUI.DialogPreference`: the `coui_btn_next` arrow) that shows the
 * selected entry as trailing assignment text. A click opens the COUI single-choice dialog
 * (`COUIAlertDialog.BottomAssignment` built with a `ChoiceListAdapter` of
 * `coui_select_dialog_singlechoice` rows: label, optional summary and an end radio button, a divider
 * between rows) with a cancel button. Tapping an entry commits it and dismisses the dialog right away
 * (the radio of the dialog keeps the selection it opened with); the cancel button or an outside tap
 * dismisses without changes.
 *
 * @param entries The list of [ListPreferenceEntry] to choose from.
 * @param selectedIndex The index of the selected entry, or -1 when nothing is selected.
 * @param onSelectedIndexChange The callback when an entry is selected in the dialog.
 * @param title The title of the [ListPreference].
 * @param cancelButtonText The label of the cancel button of the dialog (COUI dialog_cancel).
 * @param modifier The modifier to be applied to the [ListPreference].
 * @param titleColor The color of the title.
 * @param summary The summary of the [ListPreference].
 * @param summaryColor The color of the summary.
 * @param dialogTitle The title of the selection dialog. Defaults to [title].
 * @param colors The [ListPreferenceColors] used by the dialog rows.
 * @param startAction The [Composable] content on the start side of the [ListPreference].
 * @param bottomAction The [Composable] content at the bottom of the [ListPreference].
 * @param insideMargin The margin inside the [ListPreference].
 * @param cardListPosition The row's position inside its card group, by default the one a
 *   [io.github.suqi8.coui.kmp.basic.CardListGroup] provides. Rounded outer edges receive extra padding.
 * @param enabled Whether the [ListPreference] is clickable.
 * @param showValue Whether to show the selected entry as trailing assignment text.
 * @param renderInRootScaffold Whether to render the dialog in the root (outermost) Scaffold.
 * @param onExpandedChange The callback when the dialog is shown or dismissed.
 */
@Composable
fun ListPreference(
    entries: List<ListPreferenceEntry>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    title: String,
    cancelButtonText: String,
    modifier: Modifier = Modifier,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    dialogTitle: String = title,
    colors: ListPreferenceColors = ListPreferenceDefaults.listPreferenceColors(),
    startAction: @Composable (() -> Unit)? = null,
    bottomAction: (@Composable () -> Unit)? = null,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    cardListPosition: CardListPosition = LocalCardListPosition.current,
    enabled: Boolean = true,
    showValue: Boolean = true,
    renderInRootScaffold: Boolean = true,
    onExpandedChange: ((Boolean) -> Unit)? = null,
) {
    val isDialogShown = remember { mutableStateOf(false) }
    val isHoldDown = remember { mutableStateOf(false) }
    // COUIListPreferenceDialogFragment checks the radio of the value the dialog opened with and
    // dismisses on a click without moving it.
    val dialogSelectedIndex = remember { mutableIntStateOf(selectedIndex) }
    val currentSelectedIndex by rememberUpdatedState(selectedIndex)
    val currentOnSelectedIndexChange by rememberUpdatedState(onSelectedIndexChange)
    val currentOnExpandedChange = rememberUpdatedState(onExpandedChange)
    val setExpanded: (Boolean) -> Unit = remember {
        { expanded ->
            if (isDialogShown.value != expanded) {
                isDialogShown.value = expanded
                currentOnExpandedChange.value?.invoke(expanded)
            }
        }
    }

    val hasEntries = entries.isNotEmpty()
    val actualEnabled = enabled && hasEntries

    val handleClick = remember(actualEnabled) {
        {
            if (actualEnabled) {
                dialogSelectedIndex.intValue = currentSelectedIndex
                setExpanded(true)
                isHoldDown.value = true
            }
        }
    }

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
            val selectedValueText = entries.getOrNull(selectedIndex)?.text
            PreferenceValueEndActions(
                valueText = if (showValue) selectedValueText else null,
                enabled = actualEnabled,
            )
        },
        bottomAction = bottomAction,
        onClick = handleClick,
        role = Role.DropdownList,
        holdDownState = isHoldDown.value,
        enabled = actualEnabled,
    )

    if (hasEntries) {
        OverlayDialog(
            show = isDialogShown.value,
            title = dialogTitle,
            onDismissRequest = { setExpanded(false) },
            onDismissFinished = { isHoldDown.value = false },
            renderInRootScaffold = renderInRootScaffold,
            customContent = {
                PreferenceChoiceList {
                    val radioColors = RadioButtonDefaults.radioButtonColors(
                        selectedColor = colors.selectedIndicatorColor,
                        disabledSelectedColor = colors.disabledSelectedIndicatorColor,
                    )
                    entries.forEachIndexed { index, entry ->
                        val selected = index == dialogSelectedIndex.intValue
                        SelectPanelItem(
                            text = entry.text,
                            summary = entry.summary,
                            selected = selected,
                            checked = null,
                            showDivider = index != entries.lastIndex,
                            colors = colors,
                            enabled = entry.enabled,
                            onClick = {
                                // COUIListPreferenceDialogFragment: commit the clicked entry, then dismiss.
                                currentOnSelectedIndexChange(index)
                                setExpanded(false)
                            },
                        ) {
                            RadioButton(
                                selected = selected,
                                onClick = null,
                                enabled = entry.enabled,
                                colors = radioColors,
                            )
                        }
                    }
                }
            },
        ) {
            DialogButtonBar(
                negative = DialogButtonBarAction(text = cancelButtonText, onClick = { setExpanded(false) }),
                positive = null,
            )
        }
    }
}

/**
 * The trailing assignment text and the jump arrow of a [ListPreference] /
 * [MultiSelectListPreference] row (`Preference.COUI.DialogPreference`:
 * coui_preference_assignment_in_right with the coui_preference_widget_jump widget).
 */
@Composable
internal fun RowScope.PreferenceValueEndActions(
    valueText: String?,
    enabled: Boolean,
) {
    val valueColor = if (enabled) {
        COUITheme.colorScheme.labelSecondary
    } else {
        COUITheme.colorScheme.disabledNeutral
    }
    if (!valueText.isNullOrBlank()) {
        Text(
            text = valueText,
            modifier = Modifier
                // BasicComponent keeps 16dp to the title (support_preference_title_margin_end_in_right);
                // the jump arrow keeps support_preference_image_margin_start 4dp.
                .padding(end = 4.dp)
                .weight(1f, fill = false),
            fontSize = COUITheme.textStyles.bodyS.fontSize,
            color = valueColor,
            textAlign = TextAlign.End,
            // TextAppearance.COUI.List.Assignment.End: maxLines 2, ellipsize end.
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
    JumpArrowEndAction(enabled = enabled)
}

/**
 * The list panel of a preference choice dialog: the rows scroll inside the dialog's custom panel,
 * which with its own bottom padding adds up to the `alert_dialog_list_item_padding_bottom` COUI puts
 * below an adapter list (`COUIAlertDialogBuilder.initListPanel`).
 */
@Composable
internal fun PreferenceChoiceList(
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                bottom = (ListDialogDefaults.ChoiceListPaddingBottom - DialogDefaults.CustomContentPaddingBottom)
                    .coerceAtLeast(0.dp),
            )
            .verticalScroll(rememberScrollState()),
        content = content,
    )
}

/**
 * A row of a preference choice dialog (`ChoiceListAdapter` with `coui_select_dialog_singlechoice` /
 * `coui_select_dialog_multichoice`): the whole row is the touch target and spans the dialog width,
 * padded 24dp at both ends; the label and the optional summary on the start side, the selection
 * control at the end, both 10dp from the row edges, and a hairline divider inset by the row padding
 * below every row but the last. A disabled row swallows touches without pressing (`ChoiceListAdapter`
 * installs a touch listener that consumes them) and shows the disabled colors.
 *
 * @param checked When null the row uses single-choice semantics ([Role.RadioButton]);
 *   otherwise it uses toggleable checkbox semantics with this checked value.
 */
@Composable
internal fun SelectPanelItem(
    text: String,
    summary: String?,
    selected: Boolean,
    checked: Boolean?,
    showDivider: Boolean,
    colors: ListPreferenceColors,
    enabled: Boolean,
    onClick: () -> Unit,
    endContent: @Composable () -> Unit,
) {
    val currentOnClick by rememberUpdatedState(onClick)
    val horizontalPadding = ListPreferenceDefaults.PanelInsideMargin.width
    Column(modifier = Modifier.fillMaxWidth()) {
        val interactionModifier = if (checked == null) {
            Modifier.selectable(
                selected = selected,
                enabled = enabled,
                role = Role.RadioButton,
                onClick = { currentOnClick() },
            )
        } else {
            Modifier.toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Checkbox,
                onValueChange = { currentOnClick() },
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = ListPreferenceDefaults.PanelItemMinHeight)
                .then(interactionModifier)
                .padding(horizontal = horizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = ListPreferenceDefaults.PanelItemIndicatorSpacing)
                    .padding(vertical = ListPreferenceDefaults.PanelItemVerticalPadding),
            ) {
                // ?couiTextAppearanceHeadline6 in coui_bottom_alert_dialog_button_text_color_selector.
                Text(
                    text = text,
                    style = COUITheme.textStyles.headlineXS,
                    color = colors.itemTextColor(enabled),
                )
                summary?.let {
                    // ?couiTextAppearanceBody in coui_bottom_alert_dialog_summary_text_color_selector.
                    Text(
                        text = it,
                        modifier = Modifier.padding(top = ListPreferenceDefaults.PanelItemSummarySpacing),
                        style = COUITheme.textStyles.bodyM,
                        color = colors.itemSummaryColor(enabled),
                    )
                }
            }
            Box(
                modifier = Modifier.padding(vertical = ListPreferenceDefaults.PanelItemVerticalPadding),
                contentAlignment = Alignment.Center,
            ) {
                endContent()
            }
        }
        if (showDivider) {
            HorizontalDivider(modifier = Modifier.padding(horizontal = horizontalPadding))
        }
    }
}

object ListPreferenceDefaults {

    /**
     * The horizontal padding of a dialog row (COUI alert_dialog_single_list_padding_left /
     * alert_dialog_single_list_padding_right); the row itself spans the dialog width.
     */
    val PanelInsideMargin = DpSize(24.dp, 0.dp)

    /** The minimum height of a dialog row (COUI coui_delete_alert_dialog_button_height). */
    val PanelItemMinHeight = 48.dp

    /**
     * The vertical margin of the texts and of the selection control of a dialog row
     * (COUI alert_dialog_single_list_padding_vertical).
     */
    val PanelItemVerticalPadding = 10.dp

    /**
     * The gap between the text block and the selection control of a dialog row
     * (COUI coui_dialog_layout_margin_horizontal).
     */
    val PanelItemIndicatorSpacing = 16.dp

    /**
     * The spacing between the text and the summary of a dialog row
     * (COUI coui_alert_dialog_content_panel_padding_top).
     */
    val PanelItemSummarySpacing = 2.dp

    /** The size of the selection control of a dialog row, matching the COUI 24dp selection widgets. */
    val CheckIconSize = 24.dp

    /**
     * The gap below the last row of a single-choice list (COUI
     * alert_dialog_single_list_last_item_padding_bottom). The selection dialog does not read it: its
     * [DialogButtonBar] keeps the COUI 17 bar spacing.
     */
    val ButtonBarTopPadding = 6.dp

    /**
     * A 12dp padding below a custom button bar. The selection dialog does not read it: its
     * [DialogButtonBar] keeps the COUI 17 bar spacing.
     */
    val ButtonBarBottomPadding = 12.dp

    /**
     * The default [ListPreferenceColors] of the selection dialog rows.
     */
    @Composable
    fun listPreferenceColors(
        itemTextColor: Color = COUITheme.colorScheme.labelPrimary,
        disabledItemTextColor: Color = COUITheme.colorScheme.disabledNeutral,
        itemSummaryColor: Color = COUITheme.colorScheme.labelSecondary,
        disabledItemSummaryColor: Color = COUITheme.colorScheme.disabledNeutral,
        selectedIndicatorColor: Color = COUITheme.colorScheme.primary,
        disabledSelectedIndicatorColor: Color = COUITheme.colorScheme.disable,
    ): ListPreferenceColors = remember(
        itemTextColor,
        disabledItemTextColor,
        itemSummaryColor,
        disabledItemSummaryColor,
        selectedIndicatorColor,
        disabledSelectedIndicatorColor,
    ) {
        ListPreferenceColors(
            itemTextColor = itemTextColor,
            disabledItemTextColor = disabledItemTextColor,
            itemSummaryColor = itemSummaryColor,
            disabledItemSummaryColor = disabledItemSummaryColor,
            selectedIndicatorColor = selectedIndicatorColor,
            disabledSelectedIndicatorColor = disabledSelectedIndicatorColor,
        )
    }
}

/**
 * Colors used by the selection dialog rows of a [ListPreference] / [MultiSelectListPreference].
 *
 * @param itemTextColor The color of a row label.
 * @param disabledItemTextColor The color of a disabled row label.
 * @param itemSummaryColor The color of a row summary.
 * @param disabledItemSummaryColor The color of a disabled row summary.
 * @param selectedIndicatorColor The color of the checked radio button of a [ListPreference] row.
 * @param disabledSelectedIndicatorColor The color of the checked radio button of a disabled row.
 */
@Immutable
data class ListPreferenceColors(
    val itemTextColor: Color,
    val disabledItemTextColor: Color,
    val itemSummaryColor: Color,
    val disabledItemSummaryColor: Color,
    val selectedIndicatorColor: Color,
    val disabledSelectedIndicatorColor: Color,
) {
    @Stable
    internal fun itemTextColor(enabled: Boolean): Color = if (enabled) itemTextColor else disabledItemTextColor

    @Stable
    internal fun itemSummaryColor(enabled: Boolean): Color = if (enabled) itemSummaryColor else disabledItemSummaryColor
}
