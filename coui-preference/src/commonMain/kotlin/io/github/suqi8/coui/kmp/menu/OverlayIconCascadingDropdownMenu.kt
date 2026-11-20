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
import io.github.suqi8.coui.kmp.overlay.OverlayCascadingListPopup

/**
 * An [IconButton] wrapper that opens an [OverlayCascadingListPopup] for a single [DropdownEntry].
 *
 * Items whose [io.github.suqi8.coui.kmp.basic.DropdownItem.children] is non-empty become submenu
 * triggers; cascading depth is limited to 2.
 */
@Composable
fun OverlayIconCascadingDropdownMenu(
    entry: DropdownEntry,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    maxHeight: Dp? = null,
    dropdownColors: DropdownColors = DropdownDefaults.dropdownColors(),
    renderInRootScaffold: Boolean = true,
    collapseOnSelection: Boolean = true,
    onExpandedChange: ((Boolean) -> Unit)? = null,
    backgroundColor: Color = Color.Unspecified,
    cornerRadius: Dp = IconButtonDefaults.CornerRadius,
    minHeight: Dp = IconButtonDefaults.MinHeight,
    minWidth: Dp = IconButtonDefaults.MinWidth,
    content: @Composable () -> Unit,
) {
    val entries = remember(entry) { listOf(entry) }
    OverlayIconCascadingDropdownMenu(
        entries = entries,
        modifier = modifier,
        enabled = enabled,
        maxHeight = maxHeight,
        dropdownColors = dropdownColors,
        renderInRootScaffold = renderInRootScaffold,
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
 * An [IconButton] wrapper that opens an [OverlayCascadingListPopup] for one or more
 * [DropdownEntry] groups. Items whose [io.github.suqi8.coui.kmp.basic.DropdownItem.children] is
 * non-empty become submenu triggers; cascading depth is limited to 2.
 *
 * Like the COUI 17 toolbar overflow button (`COUIActionMenuView` → `COUIPopupListWindow.show`), the
 * button plays its normal press and release and is not held while the menu is showing:
 * `COUIPopupListWindow.setAnchorHoveredState` only locks list-row anchors.
 */
@Composable
fun OverlayIconCascadingDropdownMenu(
    entries: List<DropdownEntry>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    maxHeight: Dp? = null,
    dropdownColors: DropdownColors = DropdownDefaults.dropdownColors(),
    renderInRootScaffold: Boolean = true,
    collapseOnSelection: Boolean = true,
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
            OverlayCascadingListPopup(
                show = isDropdownExpanded.value,
                entries = nonEmptyEntries,
                onDismissRequest = { setExpanded(false) },
                maxHeight = maxHeight,
                dropdownColors = dropdownColors,
                renderInRootScaffold = renderInRootScaffold,
                collapseOnSelection = collapseOnSelection,
            )
        }
    }
}
