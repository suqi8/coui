// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.popup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import io.github.suqi8.coui.kmp.basic.DropdownColors
import io.github.suqi8.coui.kmp.basic.DropdownEntry
import io.github.suqi8.coui.kmp.basic.ListPopupColumn
import io.github.suqi8.coui.kmp.basic.ListPopupDefaults
import io.github.suqi8.coui.kmp.basic.PopupPositionProvider
import io.github.suqi8.coui.kmp.basic.PreciseClickState
import io.github.suqi8.coui.kmp.effect.LocalCOUIMaterialEffectEnabled
import io.github.suqi8.coui.kmp.layout.DialogButtonBar
import io.github.suqi8.coui.kmp.layout.DialogButtonBarAction
import io.github.suqi8.coui.kmp.overlay.OverlayDialog
import io.github.suqi8.coui.kmp.overlay.OverlayListPopup

/**
 * Overlay dropdown popup for a single [DropdownEntry].
 *
 * Item clicks call [io.github.suqi8.coui.kmp.basic.DropdownItem.onClick], and [collapseOnSelection]
 * controls whether the popup is dismissed after a click.
 */
@Composable
fun OverlayDropdownPopup(
    entry: DropdownEntry,
    show: Boolean,
    onDismiss: () -> Unit,
    onDismissFinished: () -> Unit,
    maxHeight: Dp?,
    dropdownColors: DropdownColors,
    renderInRootScaffold: Boolean,
    backgroundModifier: Modifier = Modifier,
    collapseOnSelection: Boolean = true,
    preciseClickState: PreciseClickState? = null,
    backgroundColor: Color = ListPopupDefaults.backgroundColor(),
    materialBlur: Boolean = LocalCOUIMaterialEffectEnabled.current,
    materialEffect: Boolean = LocalCOUIMaterialEffectEnabled.current,
) {
    val entries = remember(entry) { listOf(entry) }
    OverlayDropdownPopup(
        entries = entries,
        show = show,
        onDismiss = onDismiss,
        onDismissFinished = onDismissFinished,
        maxHeight = maxHeight,
        dropdownColors = dropdownColors,
        renderInRootScaffold = renderInRootScaffold,
        collapseOnSelection = collapseOnSelection,
        preciseClickState = preciseClickState,
        backgroundModifier = backgroundModifier,
        backgroundColor = backgroundColor,
        materialBlur = materialBlur,
        materialEffect = materialEffect,
    )
}

/**
 * Overlay dropdown popup for one or more [DropdownEntry] groups.
 *
 * Groups are separated by dividers. Entries without selection state can be used as action menus.
 *
 * [backgroundModifier], [backgroundColor], [materialBlur] and [materialEffect] reach the popup
 * surface. The popup is blurred with the COUI 17 popup material by default when a
 * [io.github.suqi8.coui.kmp.effect.LocalPopupMaterialBlur] provider is in scope; `materialBlur =
 * false` keeps the opaque [backgroundColor]. Both flags default to [LocalCOUIMaterialEffectEnabled].
 */
@Composable
fun OverlayDropdownPopup(
    entries: List<DropdownEntry>,
    show: Boolean,
    onDismiss: () -> Unit,
    onDismissFinished: () -> Unit,
    maxHeight: Dp?,
    dropdownColors: DropdownColors,
    renderInRootScaffold: Boolean,
    backgroundModifier: Modifier = Modifier,
    collapseOnSelection: Boolean = entries.size <= 1,
    preciseClickState: PreciseClickState? = null,
    backgroundColor: Color = ListPopupDefaults.backgroundColor(),
    materialBlur: Boolean = LocalCOUIMaterialEffectEnabled.current,
    materialEffect: Boolean = LocalCOUIMaterialEffectEnabled.current,
) {
    val currentEntries by rememberUpdatedState(entries)
    val currentCollapseOnSelection by rememberUpdatedState(collapseOnSelection)
    val currentOnDismiss by rememberUpdatedState(onDismiss)
    val onItemClicked: (Int, Int) -> Unit = remember {
        { entryIdx, itemIdx ->
            currentEntries.getOrNull(entryIdx)?.let { entry ->
                entry.items.getOrNull(itemIdx)?.onClick?.invoke()
            }
            if (currentCollapseOnSelection) {
                currentOnDismiss()
            }
        }
    }
    OverlayListPopup(
        show = show,
        alignment = PopupPositionProvider.Align.End,
        onDismissRequest = onDismiss,
        onDismissFinished = onDismissFinished,
        maxHeight = maxHeight,
        renderInRootScaffold = renderInRootScaffold,
        preciseClickState = preciseClickState,
        backgroundModifier = backgroundModifier,
        backgroundColor = backgroundColor,
        materialBlur = materialBlur,
        materialEffect = materialEffect,
    ) {
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
 * Overlay dialog for a [DropdownEntry].
 *
 * Item clicks call [io.github.suqi8.coui.kmp.basic.DropdownItem.onClick], and [collapseOnSelection]
 * controls whether the dialog is dismissed after a click.
 */
@Composable
fun OverlayDropdownDialog(
    entry: DropdownEntry,
    title: String,
    dialogButtonString: String,
    show: Boolean,
    onDismiss: () -> Unit,
    onDismissFinished: () -> Unit,
    dropdownColors: DropdownColors,
    popupModifier: Modifier = Modifier,
    renderInRootScaffold: Boolean = true,
    collapseOnSelection: Boolean = true,
) {
    val entries = remember(entry) { listOf(entry) }
    OverlayDropdownDialog(
        entries = entries,
        title = title,
        dialogButtonString = dialogButtonString,
        show = show,
        onDismiss = onDismiss,
        onDismissFinished = onDismissFinished,
        dropdownColors = dropdownColors,
        popupModifier = popupModifier,
        renderInRootScaffold = renderInRootScaffold,
        collapseOnSelection = collapseOnSelection,
    )
}

/**
 * Overlay dialog for one or more [DropdownEntry] groups: the COUI 17 single-choice dialog of a
 * `COUIListPreference`, the one [io.github.suqi8.coui.kmp.preference.ListPreference] opens
 * (`COUIListPreferenceDialogFragment`: `coui_select_dialog_singlechoice` rows with an end radio
 * button and a hairline between rows, and a single [dialogButtonString] button in the COUI 17
 * capsule button bar).
 *
 * Groups follow one another, a [DropdownEntry.title] adds a header row. Item clicks call
 * [io.github.suqi8.coui.kmp.basic.DropdownItem.onClick], and [collapseOnSelection] controls whether
 * the dialog is dismissed after a click; when it is, the radios keep the selection the dialog
 * opened with while it leaves, as COUI does. A click plays no haptic.
 *
 * @param dropdownColors The row colors: the label (`contentColor`), the summary (`summaryColor`), a
 *   disabled row (`disabledContentColor`), a checked radio (`selectedIndicatorColor`) and the group
 *   headers (`headerColor`). [io.github.suqi8.coui.kmp.basic.DropdownDefaults.dialogDropdownColors]
 *   carries the COUI values.
 */
@Composable
fun OverlayDropdownDialog(
    entries: List<DropdownEntry>,
    title: String,
    dialogButtonString: String,
    show: Boolean,
    onDismiss: () -> Unit,
    onDismissFinished: () -> Unit,
    dropdownColors: DropdownColors,
    popupModifier: Modifier = Modifier,
    renderInRootScaffold: Boolean = true,
    collapseOnSelection: Boolean = entries.size <= 1,
) {
    val currentEntries by rememberUpdatedState(entries)
    val currentCollapseOnSelection by rememberUpdatedState(collapseOnSelection)
    val currentOnDismiss by rememberUpdatedState(onDismiss)
    val onItemClicked: (Int, Int) -> Unit = remember {
        { entryIdx, itemIdx ->
            currentEntries.getOrNull(entryIdx)?.let { entry ->
                entry.items.getOrNull(itemIdx)?.onClick?.invoke()
            }
            if (currentCollapseOnSelection) {
                currentOnDismiss()
            }
        }
    }
    OverlayDialog(
        show = show,
        modifier = popupModifier,
        title = title,
        onDismissRequest = onDismiss,
        onDismissFinished = onDismissFinished,
        renderInRootScaffold = renderInRootScaffold,
        customContent = {
            DropdownChoiceDialogList(
                entries = entries,
                show = show,
                freezeSelection = collapseOnSelection,
                dropdownColors = dropdownColors,
                onItemClick = onItemClicked,
            )
        },
    ) {
        // COUIListPreferenceDialogFragment: only a negative button, in the COUI 17 button bar.
        DialogButtonBar(
            negative = DialogButtonBarAction(text = dialogButtonString, onClick = { currentOnDismiss() }),
            positive = null,
        )
    }
}
