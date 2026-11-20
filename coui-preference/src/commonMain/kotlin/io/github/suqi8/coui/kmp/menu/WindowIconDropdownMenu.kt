// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import io.github.suqi8.coui.kmp.basic.DropdownColors
import io.github.suqi8.coui.kmp.basic.DropdownDefaults
import io.github.suqi8.coui.kmp.basic.DropdownEntry
import io.github.suqi8.coui.kmp.basic.IconButton
import io.github.suqi8.coui.kmp.basic.IconButtonDefaults
import io.github.suqi8.coui.kmp.popup.WindowDropdownPopup

/**
 * An [IconButton] wrapper that opens a [WindowDropdownPopup] for a single [DropdownEntry].
 */
@Composable
fun WindowIconDropdownMenu(
    entry: DropdownEntry,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    maxHeight: Dp? = null,
    dropdownColors: DropdownColors = DropdownDefaults.dropdownColors(),
    collapseOnSelection: Boolean = true,
    onExpandedChange: ((Boolean) -> Unit)? = null,
    backgroundColor: Color = Color.Unspecified,
    cornerRadius: Dp = IconButtonDefaults.CornerRadius,
    minHeight: Dp = IconButtonDefaults.MinHeight,
    minWidth: Dp = IconButtonDefaults.MinWidth,
    content: @Composable () -> Unit,
) {
    val entries = remember(entry) { listOf(entry) }
    WindowIconDropdownMenu(
        entries = entries,
        modifier = modifier,
        enabled = enabled,
        maxHeight = maxHeight,
        dropdownColors = dropdownColors,
        collapseOnSelection = collapseOnSelection,
        onExpandedChange = onExpandedChange,
        backgroundColor = backgroundColor,
        cornerRadius = cornerRadius,
        minHeight = minHeight,
        minWidth = minWidth,
        content = content,
    )
}

/**
 * An [IconButton] wrapper that opens a [WindowDropdownPopup] for one or more [DropdownEntry] groups.
 *
 * Like the COUI 17 toolbar overflow button (`COUIActionMenuView` → `COUIPopupListWindow.show`), the
 * button plays its normal press and release and is not held while the menu is showing:
 * `COUIPopupListWindow.setAnchorHoveredState` only locks list-row anchors.
 */
@Composable
fun WindowIconDropdownMenu(
    entries: List<DropdownEntry>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    maxHeight: Dp? = null,
    dropdownColors: DropdownColors = DropdownDefaults.dropdownColors(),
    collapseOnSelection: Boolean = entries.size <= 1,
    onExpandedChange: ((Boolean) -> Unit)? = null,
    backgroundColor: Color = Color.Unspecified,
    cornerRadius: Dp = IconButtonDefaults.CornerRadius,
    minHeight: Dp = IconButtonDefaults.MinHeight,
    minWidth: Dp = IconButtonDefaults.MinWidth,
    content: @Composable () -> Unit,
) {
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
    val actualEnabled = enabled && nonEmptyEntries.isNotEmpty()
    val handleClick = remember(actualEnabled) {
        {
            if (actualEnabled) {
                setExpanded(!isDropdownExpanded.value)
            }
        }
    }

    Box(modifier = modifier) {
        IconButton(
            onClick = handleClick,
            enabled = actualEnabled,
            backgroundColor = backgroundColor,
            cornerRadius = cornerRadius,
            minHeight = minHeight,
            minWidth = minWidth,
            content = content,
        )
        if (nonEmptyEntries.isNotEmpty()) {
            WindowDropdownPopup(
                entries = nonEmptyEntries,
                show = isDropdownExpanded.value,
                onDismiss = { setExpanded(false) },
                onDismissFinished = {},
                maxHeight = maxHeight,
                dropdownColors = dropdownColors,
                collapseOnSelection = collapseOnSelection,
            )
        }
    }
}
