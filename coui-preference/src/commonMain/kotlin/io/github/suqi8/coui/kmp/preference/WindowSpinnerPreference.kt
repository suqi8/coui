// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.preference

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.BasicComponentColors
import io.github.suqi8.coui.kmp.basic.BasicComponentDefaults
import io.github.suqi8.coui.kmp.basic.CardListPosition
import io.github.suqi8.coui.kmp.basic.DropdownArrowEndAction
import io.github.suqi8.coui.kmp.basic.DropdownColors
import io.github.suqi8.coui.kmp.basic.DropdownDefaults
import io.github.suqi8.coui.kmp.basic.DropdownEntry
import io.github.suqi8.coui.kmp.basic.DropdownItem
import io.github.suqi8.coui.kmp.basic.LocalCardListPosition
import io.github.suqi8.coui.kmp.basic.preciseClickAnchor
import io.github.suqi8.coui.kmp.basic.rememberPreciseClickState
import io.github.suqi8.coui.kmp.popup.DropdownValueText
import io.github.suqi8.coui.kmp.popup.PopupAnchorHoverLock
import io.github.suqi8.coui.kmp.popup.WindowDropdownDialog
import io.github.suqi8.coui.kmp.popup.WindowDropdownPopup
import io.github.suqi8.coui.kmp.popup.dropdownArrowColor
import io.github.suqi8.coui.kmp.popup.dropdownValueColor
import io.github.suqi8.coui.kmp.popup.rememberClickSelectMenuOpener

/**
 * A spinner component with COUI style, rendered at window level without `Scaffold`. (Popup Mode)
 *
 * @param items The list of [DropdownItem] to be shown in the [WindowSpinnerPreference].
 * @param selectedIndex The index of the selected item in the [WindowSpinnerPreference].
 * @param title The title of the [WindowSpinnerPreference].
 * @param modifier The [Modifier] to be applied to the [WindowSpinnerPreference].
 * @param titleColor The color of the title of the [WindowSpinnerPreference].
 * @param summary The summary of the [WindowSpinnerPreference].
 * @param summaryColor The color of the summary of the [WindowSpinnerPreference].
 * @param spinnerColors The [DropdownColors] of the [WindowSpinnerPreference].
 * @param startAction The [Composable] content on the start side of the [WindowSpinnerPreference].
 * @param bottomAction The [Composable] content at the bottom of the [WindowSpinnerPreference].
 * @param insideMargin The [PaddingValues] to be applied inside the [WindowSpinnerPreference].
 * @param cardListPosition The row's position inside its card group, by default the one a
 *   [io.github.suqi8.coui.kmp.basic.CardListGroup] provides. Rounded outer edges receive extra padding.
 * @param maxHeight The maximum height of the dropdown popup.
 * @param enabled Whether the [WindowSpinnerPreference] is enabled.
 * @param showValue Whether to show the value of the [WindowSpinnerPreference].
 * @param onExpandedChange The callback to be invoked when the expanded state of the [WindowSpinnerPreference] changes.
 * @param onSelectedIndexChange The callback to be invoked when the selected index of the [WindowSpinnerPreference] is changed.
 */
@Composable
fun WindowSpinnerPreference(
    items: List<DropdownItem>,
    selectedIndex: Int,
    title: String,
    modifier: Modifier = Modifier,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    spinnerColors: DropdownColors = DropdownDefaults.dropdownColors(),
    startAction: @Composable (() -> Unit)? = null,
    bottomAction: (@Composable () -> Unit)? = null,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    cardListPosition: CardListPosition = LocalCardListPosition.current,
    maxHeight: Dp? = null,
    enabled: Boolean = true,
    showValue: Boolean = true,
    onExpandedChange: ((Boolean) -> Unit)? = null,
    onSelectedIndexChange: ((Int) -> Unit)? = null,
) {
    val entry = remember(items, selectedIndex, onSelectedIndexChange) {
        DropdownEntry(
            items = items.mapIndexed { index, item ->
                item.copy(
                    selected = index == selectedIndex,
                    onClick = {
                        onSelectedIndexChange?.invoke(index)
                        item.onClick?.invoke()
                    },
                )
            },
        )
    }
    WindowSpinnerPreference(
        entry = entry,
        title = title,
        modifier = modifier,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        spinnerColors = spinnerColors,
        startAction = startAction,
        bottomAction = bottomAction,
        insideMargin = insideMargin,
        cardListPosition = cardListPosition,
        maxHeight = maxHeight,
        enabled = enabled,
        showValue = showValue,
        collapseOnSelection = true,
        onExpandedChange = onExpandedChange,
    )
}

@Composable
fun WindowSpinnerPreference(
    entry: DropdownEntry,
    title: String,
    modifier: Modifier = Modifier,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    spinnerColors: DropdownColors = DropdownDefaults.dropdownColors(),
    startAction: @Composable (() -> Unit)? = null,
    bottomAction: (@Composable () -> Unit)? = null,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    cardListPosition: CardListPosition = LocalCardListPosition.current,
    maxHeight: Dp? = null,
    enabled: Boolean = true,
    showValue: Boolean = true,
    collapseOnSelection: Boolean = true,
    onExpandedChange: ((Boolean) -> Unit)? = null,
) {
    val entries = remember(entry) { listOf(entry) }
    WindowSpinnerPreference(
        entries = entries,
        title = title,
        modifier = modifier,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        spinnerColors = spinnerColors,
        startAction = startAction,
        bottomAction = bottomAction,
        insideMargin = insideMargin,
        cardListPosition = cardListPosition,
        maxHeight = maxHeight,
        enabled = enabled,
        showValue = showValue,
        collapseOnSelection = collapseOnSelection,
        onExpandedChange = onExpandedChange,
    )
}

@Composable
fun WindowSpinnerPreference(
    entries: List<DropdownEntry>,
    title: String,
    modifier: Modifier = Modifier,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    spinnerColors: DropdownColors = DropdownDefaults.dropdownColors(),
    startAction: @Composable (() -> Unit)? = null,
    bottomAction: (@Composable () -> Unit)? = null,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    cardListPosition: CardListPosition = LocalCardListPosition.current,
    maxHeight: Dp? = null,
    enabled: Boolean = true,
    showValue: Boolean = true,
    collapseOnSelection: Boolean = entries.size <= 1,
    onExpandedChange: ((Boolean) -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isDropdownExpanded = rememberSaveable { mutableStateOf(false) }
    // COUI wires the dropdown preference row through PreciseClickHelper, so the menu opens at
    // the finger rather than centred on the row.
    val preciseClickState = rememberPreciseClickState()
    val currentOnExpandedChange = rememberUpdatedState(onExpandedChange)
    val setExpanded: (Boolean) -> Unit = remember {
        { expanded ->
            if (isDropdownExpanded.value != expanded) {
                isDropdownExpanded.value = expanded
                currentOnExpandedChange.value?.invoke(expanded)
            }
        }
    }
    // COUIClickSelectMenu: a showing soft keyboard is hidden before the menu opens. Opening plays
    // no haptic (COUIPopupListWindow.show, PreciseClickHelper and COUIMenuPreference have none).
    val openMenu = rememberClickSelectMenuOpener { setExpanded(true) }

    val nonEmptyEntries = entries.filter { it.items.isNotEmpty() }
    val hasEntries = nonEmptyEntries.isNotEmpty()
    val actualEnabled = enabled && hasEntries

    val actionColor = dropdownArrowColor(actualEnabled)
    val valueColor = dropdownValueColor(actualEnabled)

    val handleClick = remember(actualEnabled, openMenu) {
        {
            if (actualEnabled) {
                if (isDropdownExpanded.value) setExpanded(false) else openMenu()
            }
        }
    }

    // COUIPopupListWindow.setAnchorHoveredState: the row's hover layer stays locked while the menu
    // is showing and is released as soon as it starts to leave.
    PopupAnchorHoverLock(locked = isDropdownExpanded.value, interactionSource = interactionSource)
    BasicComponent(
        modifier = modifier.preciseClickAnchor(preciseClickState),
        interactionSource = interactionSource,
        insideMargin = insideMargin,
        cardListPosition = cardListPosition,
        title = title,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        startAction = startAction,
        endActions = {
            val selectedValueText = nonEmptyEntries
                .asSequence()
                .flatMap { group -> group.items }
                .filter { it.selected }
                .map { it.text }
                .filter { it.isNotBlank() }
                .joinToString("\n")
                .ifBlank { null }
            if (showValue && hasEntries && !selectedValueText.isNullOrBlank()) {
                DropdownValueText(text = selectedValueText, color = valueColor)
            }
            DropdownArrowEndAction(
                actionColor = actionColor,
            )
            if (hasEntries) {
                WindowDropdownPopup(
                    entries = nonEmptyEntries,
                    show = isDropdownExpanded.value,
                    onDismiss = { setExpanded(false) },
                    // PreciseClickHelper falls back to the anchor without a fresh touch point (TalkBack):
                    // forget this one once the menu has gone.
                    onDismissFinished = { preciseClickState.clear() },
                    maxHeight = maxHeight,
                    dropdownColors = spinnerColors,
                    collapseOnSelection = collapseOnSelection,
                    preciseClickState = preciseClickState,
                )
            }
        },
        bottomAction = bottomAction,
        onClick = handleClick,
        role = Role.DropdownList,
        enabled = actualEnabled,
    )
}

/**
 * A [WindowSpinnerPreference] component with COUI style, show Spinner as dialog, rendered at window level without `Scaffold`. (Dialog Mode)
 *
 * @param items the list of [DropdownItem] to be shown in the [WindowSpinnerPreference].
 * @param selectedIndex the index of the selected item in the [WindowSpinnerPreference].
 * @param title the title of the [WindowSpinnerPreference].
 * @param dialogButtonString the string of the button in the dialog.
 * @param modifier the [Modifier] to be applied to the [WindowSpinnerPreference].
 * @param popupModifier the [Modifier] to be applied to the popup of the [WindowSpinnerPreference].
 * @param titleColor the color of the title of the [WindowSpinnerPreference].
 * @param summary the summary of the [WindowSpinnerPreference].
 * @param summaryColor the color of the summary of the [WindowSpinnerPreference].
 * @param startAction the action to be shown at the start side of the [WindowSpinnerPreference].
 * @param bottomAction The [Composable] content at the bottom of the [WindowSpinnerPreference].
 * @param insideMargin the [PaddingValues] to be applied inside the [WindowSpinnerPreference].
 * @param cardListPosition The row's position inside its card group, by default the one a
 *   [io.github.suqi8.coui.kmp.basic.CardListGroup] provides. Rounded outer edges receive extra padding.
 * @param enabled whether the [WindowSpinnerPreference] is enabled.
 * @param showValue whether to show the value of the [WindowSpinnerPreference].
 * @param onExpandedChange the callback to be invoked when the expanded state of the [WindowSpinnerPreference] changes.
 * @param onSelectedIndexChange the callback to be invoked when the selected index of the [WindowSpinnerPreference] is changed.
 */
@Composable
fun WindowSpinnerPreference(
    items: List<DropdownItem>,
    selectedIndex: Int,
    title: String,
    dialogButtonString: String,
    modifier: Modifier = Modifier,
    popupModifier: Modifier = Modifier,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    spinnerColors: DropdownColors = DropdownDefaults.dialogDropdownColors(),
    startAction: @Composable (() -> Unit)? = null,
    bottomAction: (@Composable () -> Unit)? = null,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    cardListPosition: CardListPosition = LocalCardListPosition.current,
    enabled: Boolean = true,
    showValue: Boolean = true,
    collapseOnSelection: Boolean = true,
    onExpandedChange: ((Boolean) -> Unit)? = null,
    onSelectedIndexChange: ((Int) -> Unit)? = null,
) {
    val entry = remember(items, selectedIndex, onSelectedIndexChange) {
        DropdownEntry(
            items = items.mapIndexed { index, item ->
                item.copy(
                    selected = index == selectedIndex,
                    onClick = {
                        onSelectedIndexChange?.invoke(index)
                        item.onClick?.invoke()
                    },
                )
            },
        )
    }
    WindowSpinnerPreference(
        entry = entry,
        title = title,
        dialogButtonString = dialogButtonString,
        modifier = modifier,
        popupModifier = popupModifier,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        spinnerColors = spinnerColors,
        startAction = startAction,
        bottomAction = bottomAction,
        insideMargin = insideMargin,
        cardListPosition = cardListPosition,
        enabled = enabled,
        showValue = showValue,
        collapseOnSelection = collapseOnSelection,
        onExpandedChange = onExpandedChange,
    )
}

@Composable
fun WindowSpinnerPreference(
    entry: DropdownEntry,
    title: String,
    dialogButtonString: String,
    modifier: Modifier = Modifier,
    popupModifier: Modifier = Modifier,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    spinnerColors: DropdownColors = DropdownDefaults.dialogDropdownColors(),
    startAction: @Composable (() -> Unit)? = null,
    bottomAction: (@Composable () -> Unit)? = null,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    cardListPosition: CardListPosition = LocalCardListPosition.current,
    enabled: Boolean = true,
    showValue: Boolean = true,
    collapseOnSelection: Boolean = true,
    onExpandedChange: ((Boolean) -> Unit)? = null,
) {
    val entries = remember(entry) { listOf(entry) }
    WindowSpinnerPreference(
        entries = entries,
        title = title,
        dialogButtonString = dialogButtonString,
        modifier = modifier,
        popupModifier = popupModifier,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        spinnerColors = spinnerColors,
        startAction = startAction,
        bottomAction = bottomAction,
        insideMargin = insideMargin,
        cardListPosition = cardListPosition,
        enabled = enabled,
        showValue = showValue,
        collapseOnSelection = collapseOnSelection,
        onExpandedChange = onExpandedChange,
    )
}

@Composable
fun WindowSpinnerPreference(
    entries: List<DropdownEntry>,
    title: String,
    dialogButtonString: String,
    modifier: Modifier = Modifier,
    popupModifier: Modifier = Modifier,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    spinnerColors: DropdownColors = DropdownDefaults.dialogDropdownColors(),
    startAction: @Composable (() -> Unit)? = null,
    bottomAction: (@Composable () -> Unit)? = null,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    cardListPosition: CardListPosition = LocalCardListPosition.current,
    enabled: Boolean = true,
    showValue: Boolean = true,
    collapseOnSelection: Boolean = entries.size <= 1,
    onExpandedChange: ((Boolean) -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isDropdownExpanded = remember { mutableStateOf(false) }
    val currentOnExpandedChange = rememberUpdatedState(onExpandedChange)
    val setExpanded: (Boolean) -> Unit = remember {
        { expanded ->
            if (isDropdownExpanded.value != expanded) {
                isDropdownExpanded.value = expanded
                currentOnExpandedChange.value?.invoke(expanded)
            }
        }
    }

    val nonEmptyEntries = entries.filter { it.items.isNotEmpty() }
    val hasEntries = nonEmptyEntries.isNotEmpty()
    val actualEnabled = enabled && hasEntries

    val actionColor = dropdownArrowColor(actualEnabled)
    val valueColor = dropdownValueColor(actualEnabled)

    // The row of a COUI list dialog (COUIListPreference) plays its normal press and release: a
    // dialog locks no anchor state and opening it plays no haptic.
    val handleClick = remember(actualEnabled) {
        {
            if (actualEnabled) {
                setExpanded(!isDropdownExpanded.value)
            }
        }
    }

    BasicComponent(
        modifier = modifier,
        interactionSource = interactionSource,
        insideMargin = insideMargin,
        cardListPosition = cardListPosition,
        title = title,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        startAction = startAction,
        endActions = {
            val selectedValueText = nonEmptyEntries
                .asSequence()
                .flatMap { group -> group.items }
                .filter { it.selected }
                .map { it.text }
                .filter { it.isNotBlank() }
                .joinToString("\n")
                .ifBlank { null }
            if (showValue && hasEntries && !selectedValueText.isNullOrBlank()) {
                DropdownValueText(text = selectedValueText, color = valueColor)
            }
            DropdownArrowEndAction(
                actionColor = actionColor,
            )
            if (hasEntries) {
                WindowDropdownDialog(
                    entries = nonEmptyEntries,
                    title = title,
                    dialogButtonString = dialogButtonString,
                    show = isDropdownExpanded.value,
                    onDismiss = { setExpanded(false) },
                    onDismissFinished = {},
                    dropdownColors = spinnerColors,
                    popupModifier = popupModifier,
                    collapseOnSelection = collapseOnSelection,
                )
            }
        },
        bottomAction = bottomAction,
        onClick = handleClick,
        role = Role.DropdownList,
        enabled = actualEnabled,
    )
}
