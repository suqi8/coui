// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.preference

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.BasicComponentColors
import io.github.suqi8.coui.kmp.basic.BasicComponentDefaults
import io.github.suqi8.coui.kmp.basic.CardListPosition
import io.github.suqi8.coui.kmp.basic.Checkbox
import io.github.suqi8.coui.kmp.basic.CheckboxColors
import io.github.suqi8.coui.kmp.basic.CheckboxDefaults
import io.github.suqi8.coui.kmp.basic.LocalCardListPosition
import io.github.suqi8.coui.kmp.layout.DialogButtonBar
import io.github.suqi8.coui.kmp.layout.DialogButtonBarAction
import io.github.suqi8.coui.kmp.overlay.OverlayDialog

/**
 * A multi-select list preference with a multi-choice selection dialog, mirroring
 * COUIMultiSelectListPreference and COUIMultiSelectListPreferenceDialogFragment.
 *
 * The row is a jump row (`Preference.COUI.DialogPreference`: the `coui_btn_next` arrow) that shows the
 * selected entries as trailing assignment text. A click opens the COUI multi-choice dialog
 * (`COUIAlertDialog.BottomAssignment` built with a `ChoiceListAdapter` of
 * `coui_select_dialog_multichoice` rows: label, optional summary and an end checkbox, a divider
 * between rows) with a cancel and a confirm button. Toggling rows only updates a pending selection;
 * the confirm button commits it, while the cancel button or an outside tap dismisses the dialog
 * without changes.
 *
 * @param entries The list of [ListPreferenceEntry] to choose from.
 * @param selectedIndices The indices of the selected entries.
 * @param onSelectedIndicesChange The callback with the new selection when the confirm button
 *   is clicked.
 * @param title The title of the [MultiSelectListPreference].
 * @param confirmButtonText The label of the confirm (positive) button of the dialog.
 * @param cancelButtonText The label of the cancel (negative) button of the dialog.
 * @param modifier The modifier to be applied to the [MultiSelectListPreference].
 * @param titleColor The color of the title.
 * @param summary The summary of the [MultiSelectListPreference].
 * @param summaryColor The color of the summary.
 * @param dialogTitle The title of the selection dialog. Defaults to [title].
 * @param colors The [ListPreferenceColors] used by the dialog rows.
 * @param checkboxColors The [CheckboxColors] of the row checkboxes.
 * @param startAction The [Composable] content on the start side of the [MultiSelectListPreference].
 * @param bottomAction The [Composable] content at the bottom of the [MultiSelectListPreference].
 * @param insideMargin The margin inside the [MultiSelectListPreference].
 * @param cardListPosition The row's position inside its card group, by default the one a
 *   [io.github.suqi8.coui.kmp.basic.CardListGroup] provides. Rounded outer edges receive extra padding.
 * @param enabled Whether the [MultiSelectListPreference] is clickable.
 * @param showValue Whether to show the selected entries as trailing assignment text.
 * @param renderInRootScaffold Whether to render the dialog in the root (outermost) Scaffold.
 * @param onExpandedChange The callback when the dialog is shown or dismissed.
 */
// The staged selection mirrors the public Set<Int> API; boxing is bounded by the entry count.
@Suppress("ktlint:compose:mutable-state-autoboxing")
@Composable
fun MultiSelectListPreference(
    entries: List<ListPreferenceEntry>,
    selectedIndices: Set<Int>,
    onSelectedIndicesChange: (Set<Int>) -> Unit,
    title: String,
    confirmButtonText: String,
    cancelButtonText: String,
    modifier: Modifier = Modifier,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    dialogTitle: String = title,
    colors: ListPreferenceColors = ListPreferenceDefaults.listPreferenceColors(),
    checkboxColors: CheckboxColors = CheckboxDefaults.checkboxColors(),
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
    val pendingSelection = remember { mutableStateOf(selectedIndices) }
    val currentSelectedIndices by rememberUpdatedState(selectedIndices)
    val currentOnSelectedIndicesChange by rememberUpdatedState(onSelectedIndicesChange)
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
                // COUIMultiSelectListPreferenceDialogFragment seeds the checkbox states from the
                // committed values every time the dialog opens.
                pendingSelection.value = currentSelectedIndices
                setExpanded(true)
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
            val selectedValueText = remember(entries, selectedIndices) {
                selectedIndices
                    .filter { it in entries.indices }
                    .sorted()
                    .joinToString(", ") { entries[it].text }
                    .ifBlank { null }
            }
            PreferenceValueEndActions(
                valueText = if (showValue) selectedValueText else null,
                enabled = actualEnabled,
            )
        },
        bottomAction = bottomAction,
        onClick = handleClick,
        role = Role.DropdownList,
        // No held press while the dialog shows: the COUI dialog locks no anchor state.
        enabled = actualEnabled,
    )

    if (hasEntries) {
        OverlayDialog(
            show = isDialogShown.value,
            title = dialogTitle,
            onDismissRequest = { setExpanded(false) },
            renderInRootScaffold = renderInRootScaffold,
            customContent = {
                PreferenceChoiceList {
                    entries.forEachIndexed { index, entry ->
                        val checked = index in pendingSelection.value
                        SelectPanelItem(
                            text = entry.text,
                            summary = entry.summary,
                            selected = checked,
                            checked = checked,
                            showDivider = index != entries.lastIndex,
                            colors = colors,
                            enabled = entry.enabled,
                            onClick = {
                                pendingSelection.value = if (checked) {
                                    pendingSelection.value - index
                                } else {
                                    pendingSelection.value + index
                                }
                            },
                        ) {
                            // The row handles the toggle (coui_select_dialog_multichoice: the
                            // COUICheckBox is neither clickable nor focusable).
                            Checkbox(
                                state = ToggleableState(checked),
                                onClick = null,
                                enabled = entry.enabled,
                                colors = checkboxColors,
                            )
                        }
                    }
                }
            },
        ) {
            DialogButtonBar(
                negative = DialogButtonBarAction(text = cancelButtonText, onClick = { setExpanded(false) }),
                positive = DialogButtonBarAction(
                    text = confirmButtonText,
                    onClick = {
                        currentOnSelectedIndicesChange(pendingSelection.value)
                        setExpanded(false)
                    },
                ),
            )
        }
    }
}
