// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.preference

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
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
import io.github.suqi8.coui.kmp.popup.OverlayDropdownPopup
import io.github.suqi8.coui.kmp.popup.PopupAnchorHoverLock
import io.github.suqi8.coui.kmp.popup.dropdownArrowColor
import io.github.suqi8.coui.kmp.popup.dropdownValueColor
import io.github.suqi8.coui.kmp.popup.rememberClickSelectMenuOpener

/**
 * A dropdown with a title and a summary.
 *
 * @param items The options of the [OverlayDropdownPreference].
 * @param selectedIndex The index of the selected option.
 * @param title The title of the [OverlayDropdownPreference].
 * @param modifier The modifier to be applied to the [OverlayDropdownPreference].
 * @param titleColor The color of the title.
 * @param summary The summary of the [OverlayDropdownPreference].
 * @param summaryColor The color of the summary.
 * @param dropdownColors The [DropdownColors] of the [OverlayDropdownPreference].
 * @param startAction The [Composable] content on the start side of the [OverlayDropdownPreference].
 * @param bottomAction The [Composable] content at the bottom of the [OverlayDropdownPreference].
 * @param insideMargin The margin inside the [OverlayDropdownPreference].
 * @param cardListPosition The row's position inside its card group, by default the one a
 *   [io.github.suqi8.coui.kmp.basic.CardListGroup] provides. Rounded outer edges receive extra padding.
 * @param maxHeight The maximum height of the [OverlayListPopup].
 * @param enabled Whether the [OverlayDropdownPreference] is enabled.
 * @param showValue Whether to show the selected value of the [OverlayDropdownPreference].
 * @param renderInRootScaffold Whether to render the popup in the root (outermost) Scaffold.
 *   When true (default), the popup covers the full screen. When false, it renders within the
 *   current Scaffold's bounds with position compensation.
 * @param onExpandedChange The callback to be invoked when the expanded state of the [OverlayDropdownPreference] changes.
 * @param onSelectedIndexChange The callback when the selected index of the [OverlayDropdownPreference] is changed.
 */
@Composable
fun OverlayDropdownPreference(
    items: List<String>,
    selectedIndex: Int,
    title: String,
    modifier: Modifier = Modifier,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    dropdownColors: DropdownColors = DropdownDefaults.dropdownColors(),
    startAction: @Composable (() -> Unit)? = null,
    bottomAction: (@Composable () -> Unit)? = null,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    cardListPosition: CardListPosition = LocalCardListPosition.current,
    maxHeight: Dp? = null,
    enabled: Boolean = true,
    showValue: Boolean = true,
    renderInRootScaffold: Boolean = true,
    onExpandedChange: ((Boolean) -> Unit)? = null,
    onSelectedIndexChange: ((Int) -> Unit)? = null,
) {
    val entry = remember(
        items,
        selectedIndex,
        onSelectedIndexChange,
    ) {
        DropdownEntry(
            items.mapIndexed { index, item ->
                DropdownItem(
                    text = item,
                    selected = index == selectedIndex,
                    onClick = { onSelectedIndexChange?.invoke(index) },
                )
            },
        )
    }
    OverlayDropdownPreference(
        entry = entry,
        title = title,
        modifier = modifier,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        dropdownColors = dropdownColors,
        startAction = startAction,
        bottomAction = bottomAction,
        insideMargin = insideMargin,
        cardListPosition = cardListPosition,
        maxHeight = maxHeight,
        enabled = enabled,
        showValue = showValue,
        renderInRootScaffold = renderInRootScaffold,
        collapseOnSelection = true,
        onExpandedChange = onExpandedChange,
    )
}

@Composable
fun OverlayDropdownPreference(
    entry: DropdownEntry,
    title: String,
    modifier: Modifier = Modifier,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    dropdownColors: DropdownColors = DropdownDefaults.dropdownColors(),
    startAction: @Composable (() -> Unit)? = null,
    bottomAction: (@Composable () -> Unit)? = null,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    cardListPosition: CardListPosition = LocalCardListPosition.current,
    maxHeight: Dp? = null,
    enabled: Boolean = true,
    showValue: Boolean = true,
    renderInRootScaffold: Boolean = true,
    collapseOnSelection: Boolean = true,
    onExpandedChange: ((Boolean) -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isDropdownExpanded = remember { mutableStateOf(false) }
    // COUI wires the dropdown preference row through PreciseClickHelper, so the menu opens at the
    // finger rather than centred on the row.
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
    // COUIClickSelectMenu: a showing soft keyboard is hidden before the menu opens.
    val openMenu = rememberClickSelectMenuOpener { setExpanded(true) }

    val itemsNotEmpty = entry.items.isNotEmpty()
    val actualEnabled = enabled && itemsNotEmpty

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
            if (showValue && itemsNotEmpty) {
                val text = entry.items.firstOrNull { it.selected }?.text
                if (!text.isNullOrEmpty()) {
                    DropdownValueText(text = text, color = valueColor)
                }
            }
            DropdownArrowEndAction(
                actionColor = actionColor,
            )
            if (itemsNotEmpty) {
                OverlayDropdownPopup(
                    entry = entry,
                    show = isDropdownExpanded.value,
                    onDismiss = { setExpanded(false) },
                    // PreciseClickHelper falls back to the anchor without a fresh touch point (TalkBack):
                    // forget this one once the menu has gone.
                    onDismissFinished = { preciseClickState.clear() },
                    maxHeight = maxHeight,
                    dropdownColors = dropdownColors,
                    renderInRootScaffold = renderInRootScaffold,
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

@Composable
fun OverlayDropdownPreference(
    entries: List<DropdownEntry>,
    title: String,
    modifier: Modifier = Modifier,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    dropdownColors: DropdownColors = DropdownDefaults.dropdownColors(),
    startAction: @Composable (() -> Unit)? = null,
    bottomAction: (@Composable () -> Unit)? = null,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    cardListPosition: CardListPosition = LocalCardListPosition.current,
    maxHeight: Dp? = null,
    enabled: Boolean = true,
    showValue: Boolean = true,
    renderInRootScaffold: Boolean = true,
    collapseOnSelection: Boolean = entries.size <= 1,
    onExpandedChange: ((Boolean) -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isDropdownExpanded = remember { mutableStateOf(false) }
    // COUI wires the dropdown preference row through PreciseClickHelper, so the menu opens at the
    // finger rather than centred on the row.
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
    // COUIClickSelectMenu: a showing soft keyboard is hidden before the menu opens.
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
                OverlayDropdownPopup(
                    entries = nonEmptyEntries,
                    show = isDropdownExpanded.value,
                    onDismiss = { setExpanded(false) },
                    // PreciseClickHelper falls back to the anchor without a fresh touch point (TalkBack):
                    // forget this one once the menu has gone.
                    onDismissFinished = { preciseClickState.clear() },
                    maxHeight = maxHeight,
                    dropdownColors = dropdownColors,
                    renderInRootScaffold = renderInRootScaffold,
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
