// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.popup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.DropdownColors
import io.github.suqi8.coui.kmp.basic.DropdownDefaults
import io.github.suqi8.coui.kmp.basic.DropdownEntry
import io.github.suqi8.coui.kmp.basic.DropdownGroupDivider
import io.github.suqi8.coui.kmp.basic.DropdownHeaderItem
import io.github.suqi8.coui.kmp.basic.DropdownHorizontalItems
import io.github.suqi8.coui.kmp.basic.DropdownImpl
import io.github.suqi8.coui.kmp.basic.RadioButton
import io.github.suqi8.coui.kmp.basic.RadioButtonDefaults
import io.github.suqi8.coui.kmp.preference.ListPreferenceColors
import io.github.suqi8.coui.kmp.preference.ListPreferenceDefaults
import io.github.suqi8.coui.kmp.preference.PreferenceChoiceList
import io.github.suqi8.coui.kmp.preference.SelectPanelItem
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * Renders a list of [DropdownEntry] groups inside a popup container.
 *
 * A group that declares a [DropdownEntry.title] gets a non-clickable [DropdownHeaderItem] above its
 * items. Groups are separated by a [DropdownGroupDivider] — but, like COUI 17
 * `COUIPopupListWindow.setItemListInternal`, only when the menu holds at least
 * [POPUP_GROUP_MIN_ITEMS] items; a shorter menu shows its groups back to back.
 *
 * As soon as any item of the menu has an icon, every vertical row keeps the leading icon slot so
 * the titles line up (`MixedPopupListAdapter.setItemList` raises its icon flag over the whole list,
 * horizontal items included).
 */
@Composable
internal fun DropdownEntriesPopupContent(
    entries: List<DropdownEntry>,
    dropdownColors: DropdownColors,
    onItemClick: (entryIdx: Int, itemIdx: Int) -> Unit,
) {
    val lastEntryIdx = entries.lastIndex
    val showGroupDividers = entries.sumOf { it.items.size } >= POPUP_GROUP_MIN_ITEMS
    val reserveIconSlot = remember(entries) { entries.any { entry -> entry.items.any { it.icon != null } } }
    entries.forEachIndexed { entryIdx, entry ->
        if (entry.horizontal) {
            key(entryIdx, "horizontal") {
                DropdownHorizontalItems(
                    items = entry.items,
                    onItemClick = { itemIdx -> onItemClick(entryIdx, itemIdx) },
                    enabled = entry.enabled,
                    dropdownColors = dropdownColors,
                )
            }
            // MixedPopupListAdapter always follows the horizontal row with a group divider.
            if (entryIdx != lastEntryIdx) key(entryIdx, "divider") { DropdownGroupDivider() }
            return@forEachIndexed
        }
        val header = entry.title
        if (header != null) {
            key(entryIdx, "header") {
                DropdownHeaderItem(
                    text = header,
                    color = dropdownColors.headerColor,
                )
            }
        }
        entry.items.forEachIndexed { itemIdx, option ->
            key(entryIdx, itemIdx) {
                DropdownImpl(
                    item = option,
                    optionSize = entry.items.size,
                    isSelected = option.selected,
                    index = itemIdx,
                    dropdownColors = dropdownColors,
                    enabled = entry.enabled && option.enabled,
                    reserveIconSlot = reserveIconSlot,
                    onSelectedIndexChange = { idx -> onItemClick(entryIdx, idx) },
                )
            }
        }
        if (entryIdx != lastEntryIdx && showGroupDividers) {
            key(entryIdx, "divider") {
                DropdownGroupDivider()
            }
        }
    }
}

/** The smallest menu that shows group dividers (COUI `COUIPopupListWindow.GROUP_MIN_ITEMS`). */
internal const val POPUP_GROUP_MIN_ITEMS = 3

/**
 * The list panel of a dropdown dialog: the COUI 17 single-choice dialog of a `COUIListPreference`
 * (`COUIListPreferenceDialogFragment` builds a `ChoiceListAdapter` of `coui_select_dialog_singlechoice`
 * rows: an optional icon, the label over an optional summary, an end radio button and a hairline
 * between rows), drawn with the rows of [io.github.suqi8.coui.kmp.preference.ListPreference].
 *
 * Groups follow one another and a [DropdownEntry.title] adds its header row (the popup header,
 * aligned with the 24dp row text); horizontal groups have no dialog form and are left out. Only
 * the text, summary, icon and enabled state of a [io.github.suqi8.coui.kmp.basic.DropdownItem]
 * apply here: a COUI list dialog has no hint, alert or state icon.
 *
 * While [freezeSelection] is set the radios keep the selection the dialog opened with, as
 * `COUIListPreferenceDialogFragment` commits a click and dismisses without moving the check;
 * otherwise they follow [io.github.suqi8.coui.kmp.basic.DropdownItem.selected].
 *
 * @param entries The groups.
 * @param show Whether the dialog is showing; its rising edge records the opening selection.
 * @param freezeSelection Whether the radios keep the opening selection.
 * @param dropdownColors The row colors: [DropdownColors.contentColor] and
 *   [DropdownColors.summaryColor] for the texts, [DropdownColors.disabledContentColor] for disabled
 *   rows, [DropdownColors.selectedIndicatorColor] for a checked radio and
 *   [DropdownColors.headerColor] for group headers.
 * @param onItemClick Invoked with the group and item index of a clicked enabled row.
 */
@Composable
internal fun DropdownChoiceDialogList(
    entries: List<DropdownEntry>,
    show: Boolean,
    freezeSelection: Boolean,
    dropdownColors: DropdownColors,
    onItemClick: (entryIdx: Int, itemIdx: Int) -> Unit,
) {
    // COUIRadioButton keeps couiColorDisable for a checked disabled radio.
    val disabledSelectedColor = COUITheme.colorScheme.disable
    val colors = remember(dropdownColors, disabledSelectedColor) {
        ListPreferenceColors(
            itemTextColor = dropdownColors.contentColor,
            disabledItemTextColor = dropdownColors.disabledContentColor,
            itemSummaryColor = dropdownColors.summaryColor,
            disabledItemSummaryColor = dropdownColors.disabledContentColor,
            selectedIndicatorColor = dropdownColors.selectedIndicatorColor,
            disabledSelectedIndicatorColor = disabledSelectedColor,
        )
    }
    val radioColors = RadioButtonDefaults.radioButtonColors(
        selectedColor = colors.selectedIndicatorColor,
        disabledSelectedColor = colors.disabledSelectedIndicatorColor,
    )
    val openedSelection = remember { DropdownDialogOpenedSelection() }
    openedSelection.update(show, entries)
    val lastEntryIdx = entries.indexOfLast { !it.horizontal && it.items.isNotEmpty() }

    PreferenceChoiceList {
        entries.forEachIndexed { entryIdx, entry ->
            if (entry.horizontal) return@forEachIndexed
            entry.title?.let { header ->
                key(entryIdx, "header") {
                    DropdownHeaderItem(
                        text = header,
                        modifier = DialogHeaderModifier,
                        color = dropdownColors.headerColor,
                    )
                }
            }
            entry.items.forEachIndexed { itemIdx, item ->
                key(entryIdx, itemIdx) {
                    val enabled = entry.enabled && item.enabled
                    val selected = if (freezeSelection) openedSelection.isSelected(entryIdx, itemIdx) else item.selected
                    val icon = item.icon
                    val iconContent: (@Composable () -> Unit)? = if (icon != null) {
                        { Box(DialogIconModifier, contentAlignment = Alignment.Center) { icon(Modifier) } }
                    } else {
                        null
                    }
                    SelectPanelItem(
                        text = item.text,
                        summary = item.summary,
                        selected = selected,
                        checked = null,
                        showDivider = !(entryIdx == lastEntryIdx && itemIdx == entry.items.lastIndex),
                        colors = colors,
                        enabled = enabled,
                        onClick = { onItemClick(entryIdx, itemIdx) },
                        icon = iconContent,
                    ) {
                        RadioButton(
                            selected = selected,
                            onClick = null,
                            enabled = enabled,
                            colors = radioColors,
                        )
                    }
                }
            }
        }
    }
}

/**
 * The selection a dropdown dialog opened with. It is recorded when the dialog starts to show and
 * kept through its exit, so a click that commits and dismisses does not move the radio.
 */
private class DropdownDialogOpenedSelection {
    private var showing = false
    private var selected: List<List<Boolean>> = emptyList()

    fun update(show: Boolean, entries: List<DropdownEntry>) {
        if (show && !showing) selected = entries.map { entry -> entry.items.map { it.selected } }
        showing = show
    }

    fun isSelected(entryIdx: Int, itemIdx: Int): Boolean = selected.getOrNull(entryIdx)?.getOrNull(itemIdx) == true
}

/** A group header lines up with the row text, which the dialog rows pad by 24dp. */
private val DialogHeaderModifier = Modifier.padding(
    horizontal = ListPreferenceDefaults.PanelInsideMargin.width - DropdownDefaults.InsideHorizontalPadding,
)

/**
 * The row icon of `coui_select_dialog_singlechoice.xml` (`alertdialog_choice_icon`): 16dp before the
 * texts (coui_dialog_layout_margin_horizontal) and 20dp above and below
 * (alert_dialog_single_list_icon_padding_vertical).
 */
private val DialogIconModifier = Modifier.padding(end = 16.dp, top = 20.dp, bottom = 20.dp)
