// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.popup

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.DropdownColors
import io.github.suqi8.coui.kmp.basic.DropdownEntry
import io.github.suqi8.coui.kmp.basic.ListPopupColumn
import io.github.suqi8.coui.kmp.basic.ListPopupDefaults
import io.github.suqi8.coui.kmp.basic.PopupPositionProvider
import io.github.suqi8.coui.kmp.basic.PreciseClickState
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.effect.LocalCOUIMaterialEffectEnabled
import io.github.suqi8.coui.kmp.theme.LocalDismissState
import io.github.suqi8.coui.kmp.window.WindowDialog
import io.github.suqi8.coui.kmp.window.WindowListPopup

/**
 * Window-layer dropdown popup for a single [DropdownEntry].
 *
 * Item clicks call [io.github.suqi8.coui.kmp.basic.DropdownItem.onClick], and [collapseOnSelection]
 * controls whether the popup is dismissed after a click.
 */
@Composable
fun WindowDropdownPopup(
    entry: DropdownEntry,
    show: Boolean,
    onDismiss: () -> Unit,
    onDismissFinished: () -> Unit,
    maxHeight: Dp?,
    dropdownColors: DropdownColors,
    backgroundModifier: Modifier = Modifier,
    collapseOnSelection: Boolean = true,
    preciseClickState: PreciseClickState? = null,
    backgroundColor: Color = ListPopupDefaults.backgroundColor(),
    materialBlur: Boolean = LocalCOUIMaterialEffectEnabled.current,
    materialEffect: Boolean = LocalCOUIMaterialEffectEnabled.current,
) {
    val entries = remember(entry) { listOf(entry) }
    WindowDropdownPopup(
        entries = entries,
        show = show,
        onDismiss = onDismiss,
        onDismissFinished = onDismissFinished,
        maxHeight = maxHeight,
        dropdownColors = dropdownColors,
        collapseOnSelection = collapseOnSelection,
        preciseClickState = preciseClickState,
        backgroundModifier = backgroundModifier,
        backgroundColor = backgroundColor,
        materialBlur = materialBlur,
        materialEffect = materialEffect,
    )
}

/**
 * Window-layer dropdown popup for one or more [DropdownEntry] groups.
 *
 * Groups are separated by dividers. Entries without selection state can be used as action menus.
 *
 * [backgroundModifier], [backgroundColor], [materialBlur] and [materialEffect] reach the popup
 * surface. The popup is blurred with the COUI 17 popup material by default when a
 * [io.github.suqi8.coui.kmp.effect.LocalPopupMaterialBlur] provider is in scope; `materialBlur =
 * false` keeps the opaque [backgroundColor]. Both flags default to [LocalCOUIMaterialEffectEnabled].
 */
@Composable
fun WindowDropdownPopup(
    entries: List<DropdownEntry>,
    show: Boolean,
    onDismiss: () -> Unit,
    onDismissFinished: () -> Unit,
    maxHeight: Dp?,
    dropdownColors: DropdownColors,
    backgroundModifier: Modifier = Modifier,
    collapseOnSelection: Boolean = entries.size <= 1,
    preciseClickState: PreciseClickState? = null,
    backgroundColor: Color = ListPopupDefaults.backgroundColor(),
    materialBlur: Boolean = LocalCOUIMaterialEffectEnabled.current,
    materialEffect: Boolean = LocalCOUIMaterialEffectEnabled.current,
) {
    val currentEntries by rememberUpdatedState(entries)
    val currentCollapseOnSelection by rememberUpdatedState(collapseOnSelection)
    WindowListPopup(
        show = show,
        alignment = PopupPositionProvider.Align.End,
        onDismissRequest = onDismiss,
        onDismissFinished = onDismissFinished,
        maxHeight = maxHeight,
        preciseClickState = preciseClickState,
        backgroundModifier = backgroundModifier,
        backgroundColor = backgroundColor,
        materialBlur = materialBlur,
        materialEffect = materialEffect,
    ) {
        val dismiss = LocalDismissState.current
        val currentDismiss by rememberUpdatedState(dismiss)
        val onItemClicked: (Int, Int) -> Unit = remember {
            { entryIdx, itemIdx ->
                currentEntries.getOrNull(entryIdx)?.let { entry ->
                    entry.items.getOrNull(itemIdx)?.onClick?.invoke()
                }
                if (currentCollapseOnSelection) {
                    currentDismiss?.invoke()
                }
            }
        }
        ListPopupColumn {
            DropdownEntriesPopupContent(
                entries = entries,
                dropdownColors = dropdownColors,
                onItemClick = onItemClicked,
            )
        }
    }
}

/**
 * Window-layer dialog for a [DropdownEntry].
 *
 * Item clicks call [io.github.suqi8.coui.kmp.basic.DropdownItem.onClick], and [collapseOnSelection]
 * controls whether the dialog is dismissed after a click.
 */
@Composable
fun WindowDropdownDialog(
    entry: DropdownEntry,
    title: String,
    dialogButtonString: String,
    show: Boolean,
    onDismiss: () -> Unit,
    onDismissFinished: () -> Unit,
    dropdownColors: DropdownColors,
    popupModifier: Modifier = Modifier,
    collapseOnSelection: Boolean = true,
) {
    val entries = remember(entry) { listOf(entry) }
    WindowDropdownDialog(
        entries = entries,
        title = title,
        dialogButtonString = dialogButtonString,
        show = show,
        onDismiss = onDismiss,
        onDismissFinished = onDismissFinished,
        dropdownColors = dropdownColors,
        popupModifier = popupModifier,
        collapseOnSelection = collapseOnSelection,
    )
}

/**
 * Window-layer dialog for one or more [DropdownEntry] groups.
 *
 * Groups are separated by dividers. Item clicks call [io.github.suqi8.coui.kmp.basic.DropdownItem.onClick], and [collapseOnSelection]
 * controls whether the dialog is dismissed after a click.
 */
@Composable
fun WindowDropdownDialog(
    entries: List<DropdownEntry>,
    title: String,
    dialogButtonString: String,
    show: Boolean,
    onDismiss: () -> Unit,
    onDismissFinished: () -> Unit,
    dropdownColors: DropdownColors,
    popupModifier: Modifier = Modifier,
    collapseOnSelection: Boolean = entries.size <= 1,
) {
    val hapticFeedback = LocalHapticFeedback.current
    val currentEntries by rememberUpdatedState(entries)
    val currentCollapseOnSelection by rememberUpdatedState(collapseOnSelection)
    val currentHapticFeedback by rememberUpdatedState(hapticFeedback)
    WindowDialog(
        show = show,
        modifier = popupModifier,
        title = title,
        onDismissRequest = onDismiss,
        onDismissFinished = onDismissFinished,
        insideMargin = DpSize(0.dp, 24.dp),
        // The list is the dialog's list panel: the weighted slot shrinks it before the button.
        customContent = {
            val dismiss = LocalDismissState.current
            val currentDismiss by rememberUpdatedState(dismiss)
            val onItemClicked: (Int, Int) -> Unit = remember {
                { entryIdx, itemIdx ->
                    currentHapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                    currentEntries.getOrNull(entryIdx)?.let { entry ->
                        entry.items.getOrNull(itemIdx)?.onClick?.invoke()
                    }
                    if (currentCollapseOnSelection) {
                        currentDismiss?.invoke()
                    }
                }
            }
            LazyColumn {
                dropdownEntriesDialogItems(
                    entries = entries,
                    dropdownColors = dropdownColors,
                    onItemClick = onItemClicked,
                )
            }
        },
        content = {
            val dismiss = LocalDismissState.current
            TextButton(
                modifier = Modifier
                    .padding(start = 24.dp, top = DropdownDialogButtonTopPadding, end = 24.dp)
                    .fillMaxWidth(),
                text = dialogButtonString,
                minHeight = 50.dp,
                onClick = { dismiss?.invoke() },
            )
        },
    )
}
