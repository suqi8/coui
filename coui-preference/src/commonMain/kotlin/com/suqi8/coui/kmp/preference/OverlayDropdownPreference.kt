// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.preference

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.basic.BasicComponent
import com.suqi8.coui.kmp.basic.BasicComponentColors
import com.suqi8.coui.kmp.basic.BasicComponentDefaults
import com.suqi8.coui.kmp.basic.DropdownArrowEndAction
import com.suqi8.coui.kmp.basic.DropdownColors
import com.suqi8.coui.kmp.basic.DropdownDefaults
import com.suqi8.coui.kmp.basic.DropdownEntry
import com.suqi8.coui.kmp.basic.DropdownItem
import com.suqi8.coui.kmp.basic.Text
import com.suqi8.coui.kmp.popup.OverlayDropdownPopup
import com.suqi8.coui.kmp.theme.COUITheme

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
    maxHeight: Dp? = null,
    enabled: Boolean = true,
    showValue: Boolean = true,
    renderInRootScaffold: Boolean = true,
    collapseOnSelection: Boolean = true,
    onExpandedChange: ((Boolean) -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isDropdownExpanded = remember { mutableStateOf(false) }
    val isHoldDown = remember { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current
    val currentHapticFeedback by rememberUpdatedState(hapticFeedback)
    val currentOnExpandedChange = rememberUpdatedState(onExpandedChange)
    val setExpanded: (Boolean) -> Unit = remember {
        { expanded ->
            if (isDropdownExpanded.value != expanded) {
                isDropdownExpanded.value = expanded
                currentOnExpandedChange.value?.invoke(expanded)
            }
        }
    }

    val itemsNotEmpty = entry.items.isNotEmpty()
    val actualEnabled = enabled && itemsNotEmpty

    val actionColor = if (actualEnabled) {
        COUITheme.colorScheme.onSurfaceVariantActions
    } else {
        COUITheme.colorScheme.disabledOnSecondaryVariant
    }
    // COUI assignment/status text color (coui_preference_secondary_text_color):
    // couiColorSecondNeutral when enabled, couiColorLabelTertiary when disabled.
    val valueColor = if (actualEnabled) {
        COUITheme.colorScheme.onSurfaceSecondary
    } else {
        COUITheme.colorScheme.disabledOnSecondaryVariant
    }

    val handleClick = remember(actualEnabled) {
        {
            if (actualEnabled) {
                setExpanded(!isDropdownExpanded.value)
                if (isDropdownExpanded.value) {
                    isHoldDown.value = true
                    currentHapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
                }
            }
        }
    }

    BasicComponent(
        modifier = modifier,
        interactionSource = interactionSource,
        insideMargin = insideMargin,
        title = title,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        startAction = startAction,
        endActions = {
            if (showValue && itemsNotEmpty) {
                val text = entry.items.firstOrNull { it.selected }?.text
                if (!text.isNullOrEmpty()) {
                    Text(
                        text = text,
                        modifier = Modifier
                            // COUI assignment-in-right metrics: 162dp max width
                            // (coui_preference_status_text_max_width), 16dp min gap from the
                            // title block (support_preference_title_margin_end_in_right; 8dp
                            // here + the 8dp BasicComponent center-end spacer) and 4dp gap to
                            // the indicator (support_preference_image_margin_start).
                            .widthIn(max = 162.dp)
                            .padding(start = 8.dp, end = 4.dp)
                            .align(Alignment.CenterVertically)
                            .weight(1f, fill = false),
                        fontSize = COUITheme.textStyles.body2.fontSize,
                        color = valueColor,
                        textAlign = TextAlign.End,
                        overflow = TextOverflow.Ellipsis,
                    )
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
                    onDismissFinished = { isHoldDown.value = false },
                    maxHeight = maxHeight,
                    dropdownColors = dropdownColors,
                    renderInRootScaffold = renderInRootScaffold,
                    collapseOnSelection = collapseOnSelection,
                )
            }
        },
        bottomAction = bottomAction,
        onClick = handleClick,
        role = Role.DropdownList,
        holdDownState = isHoldDown.value,
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
    maxHeight: Dp? = null,
    enabled: Boolean = true,
    showValue: Boolean = true,
    renderInRootScaffold: Boolean = true,
    collapseOnSelection: Boolean = entries.size <= 1,
    onExpandedChange: ((Boolean) -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isDropdownExpanded = remember { mutableStateOf(false) }
    val isHoldDown = remember { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current
    val currentHapticFeedback by rememberUpdatedState(hapticFeedback)
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

    val actionColor = if (actualEnabled) {
        COUITheme.colorScheme.onSurfaceVariantActions
    } else {
        COUITheme.colorScheme.disabledOnSecondaryVariant
    }
    // COUI assignment/status text color (coui_preference_secondary_text_color):
    // couiColorSecondNeutral when enabled, couiColorLabelTertiary when disabled.
    val valueColor = if (actualEnabled) {
        COUITheme.colorScheme.onSurfaceSecondary
    } else {
        COUITheme.colorScheme.disabledOnSecondaryVariant
    }

    val handleClick = remember(actualEnabled) {
        {
            if (actualEnabled) {
                setExpanded(!isDropdownExpanded.value)
                if (isDropdownExpanded.value) {
                    isHoldDown.value = true
                    currentHapticFeedback.performHapticFeedback(HapticFeedbackType.ContextClick)
                }
            }
        }
    }

    BasicComponent(
        modifier = modifier,
        interactionSource = interactionSource,
        insideMargin = insideMargin,
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
                Text(
                    text = selectedValueText,
                    modifier = Modifier
                        // COUI assignment-in-right metrics: 162dp max width
                        // (coui_preference_status_text_max_width), 16dp min gap from the
                        // title block (support_preference_title_margin_end_in_right; 8dp
                        // here + the 8dp BasicComponent center-end spacer) and 4dp gap to
                        // the indicator (support_preference_image_margin_start).
                        .widthIn(max = 162.dp)
                        .padding(start = 8.dp, end = 4.dp)
                        .align(Alignment.CenterVertically)
                        .weight(1f, fill = false),
                    fontSize = COUITheme.textStyles.body2.fontSize,
                    color = valueColor,
                    textAlign = TextAlign.End,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            DropdownArrowEndAction(
                actionColor = actionColor,
            )
            if (hasEntries) {
                OverlayDropdownPopup(
                    entries = nonEmptyEntries,
                    show = isDropdownExpanded.value,
                    onDismiss = { setExpanded(false) },
                    onDismissFinished = { isHoldDown.value = false },
                    maxHeight = maxHeight,
                    dropdownColors = dropdownColors,
                    renderInRootScaffold = renderInRootScaffold,
                    collapseOnSelection = collapseOnSelection,
                )
            }
        },
        bottomAction = bottomAction,
        onClick = handleClick,
        role = Role.DropdownList,
        holdDownState = isHoldDown.value,
        enabled = actualEnabled,
    )
}
