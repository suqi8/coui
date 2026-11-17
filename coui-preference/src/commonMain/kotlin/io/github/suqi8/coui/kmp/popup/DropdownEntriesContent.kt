// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.popup

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import io.github.suqi8.coui.kmp.basic.DropdownColors
import io.github.suqi8.coui.kmp.basic.DropdownEntry
import io.github.suqi8.coui.kmp.basic.DropdownGroupDivider
import io.github.suqi8.coui.kmp.basic.DropdownHeaderItem
import io.github.suqi8.coui.kmp.basic.DropdownHorizontalItems
import io.github.suqi8.coui.kmp.basic.DropdownImpl

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
 * Adds [DropdownEntry] groups to a [LazyListScope] for use inside a dialog. Dialog mode uses
 * uniform vertical padding regardless of position, so popup-global first/last is intentionally
 * not propagated. A [DropdownEntry.title] still renders a header row so grouped dialogs stay
 * labelled, but COUI has no dialog header metrics to calibrate against — it reuses the popup one.
 */
internal fun LazyListScope.dropdownEntriesDialogItems(
    entries: List<DropdownEntry>,
    dropdownColors: DropdownColors,
    onItemClick: (entryIdx: Int, itemIdx: Int) -> Unit,
) {
    val lastEntryIdx = entries.lastIndex
    entries.forEachIndexed { entryIdx, entry ->
        // Dialog mode has no horizontal row; its items are left out there.
        if (entry.horizontal) return@forEachIndexed
        entry.title?.let { header ->
            item(key = "header-$entryIdx") {
                DropdownHeaderItem(text = header, color = dropdownColors.headerColor)
            }
        }
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
                DropdownGroupDivider()
            }
        }
    }
}
