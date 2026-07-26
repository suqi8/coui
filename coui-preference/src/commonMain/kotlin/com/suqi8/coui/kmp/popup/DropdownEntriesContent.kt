// Copyright 2026, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.popup

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import com.suqi8.coui.kmp.basic.DropdownColors
import com.suqi8.coui.kmp.basic.DropdownDefaults
import com.suqi8.coui.kmp.basic.DropdownEntry
import com.suqi8.coui.kmp.basic.DropdownImpl
import com.suqi8.coui.kmp.basic.HorizontalDivider

/**
 * Renders a list of [DropdownEntry] groups inside a popup container. Computes popup-global
 * first/last per row internally so only the very first and very last rows of the entire popup
 * receive the larger first/last padding; group boundaries fall back to the middle padding.
 */
@Composable
internal fun DropdownEntriesPopupContent(
    entries: List<DropdownEntry>,
    dropdownColors: DropdownColors,
    onItemClick: (entryIdx: Int, itemIdx: Int) -> Unit,
) {
    val lastEntryIdx = entries.lastIndex
    entries.forEachIndexed { entryIdx, entry ->
        val lastItemIdx = entry.items.lastIndex
        val isFirstEntry = entryIdx == 0
        val isLastEntry = entryIdx == lastEntryIdx
        entry.items.forEachIndexed { itemIdx, option ->
            key(entryIdx, itemIdx) {
                DropdownImpl(
                    item = option,
                    optionSize = entry.items.size,
                    isSelected = option.selected,
                    index = itemIdx,
                    dropdownColors = dropdownColors,
                    enabled = entry.enabled && option.enabled,
                    isFirst = isFirstEntry && itemIdx == 0,
                    isLast = isLastEntry && itemIdx == lastItemIdx,
                    onSelectedIndexChange = { idx -> onItemClick(entryIdx, idx) },
                )
            }
        }
        if (entryIdx != lastEntryIdx) {
            // COUI popup group divider: a full-bleed 4dp band
            // (coui_popup_list_group_divider_height) tinted with
            // coui_popup_list_group_divider_color (#14000000 in both light and dark —
            // the resource has no values-night override), no extra margins.
            HorizontalDivider(
                thickness = DropdownDefaults.PopupGroupDividerThickness,
                color = DropdownDefaults.PopupGroupDividerColor,
            )
        }
    }
}

/**
 * Adds [DropdownEntry] groups to a [LazyListScope] for use inside a dialog. Dialog mode uses
 * uniform vertical padding regardless of position, so popup-global first/last is intentionally
 * not propagated.
 */
internal fun LazyListScope.dropdownEntriesDialogItems(
    entries: List<DropdownEntry>,
    dropdownColors: DropdownColors,
    onItemClick: (entryIdx: Int, itemIdx: Int) -> Unit,
) {
    val lastEntryIdx = entries.lastIndex
    entries.forEachIndexed { entryIdx, entry ->
        items(entry.items.size, key = { itemIdx -> "$entryIdx-$itemIdx" }) { itemIdx ->
            val item = entry.items[itemIdx]
            DropdownImpl(
                item = item,
                optionSize = entry.items.size,
                isSelected = item.selected,
                index = itemIdx,
                dropdownColors = dropdownColors,
                enabled = entry.enabled && item.enabled,
                dialogMode = true,
                onSelectedIndexChange = { idx -> onItemClick(entryIdx, idx) },
            )
        }
        if (entryIdx != lastEntryIdx) {
            item(key = "divider-$entryIdx") {
                // Same COUI group divider treatment as the popup variant.
                HorizontalDivider(
                    thickness = DropdownDefaults.PopupGroupDividerThickness,
                    color = DropdownDefaults.PopupGroupDividerColor,
                )
            }
        }
    }
}
