// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.window

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import io.github.suqi8.coui.kmp.basic.DropdownColors
import io.github.suqi8.coui.kmp.basic.DropdownDefaults
import io.github.suqi8.coui.kmp.basic.DropdownItem
import io.github.suqi8.coui.kmp.basic.PopupPositionProvider
import io.github.suqi8.coui.kmp.layout.NavigationPopupMenuContent
import io.github.suqi8.coui.kmp.layout.NavigationPopupMenuDefaults

/**
 * A popup menu of navigation destinations anchored to a bottom navigation bar
 * (`COUINavigationPopupMenu`), rendered at window level: no Scaffold is required.
 *
 * See [io.github.suqi8.coui.kmp.overlay.OverlayNavigationPopupMenu] for the behaviour; both
 * variants share it. Call it inside the layout that wraps the navigation bar.
 *
 * @param show Whether the menu is shown.
 * @param items The destinations, in order.
 * @param onDismissRequest Called when the menu should close: a row click, a tap outside or back.
 * @param popupModifier The modifier applied to the popup content area.
 * @param onDismissFinished Called once the exit animation has finished.
 * @param dropdownColors The colors of the rows.
 * @param minWidth The minimum menu width.
 * @param maxHeight The maximum menu height.
 * @param materialBlur Whether the menu blurs what lies behind it with the COUI 17 popup material,
 *   when a blur provider is in scope.
 */
@Composable
fun WindowNavigationPopupMenu(
    show: Boolean,
    items: List<DropdownItem>,
    onDismissRequest: () -> Unit,
    popupModifier: Modifier = Modifier,
    onDismissFinished: (() -> Unit)? = null,
    dropdownColors: DropdownColors = DropdownDefaults.dropdownColors(),
    minWidth: Dp = NavigationPopupMenuDefaults.MinWidth,
    maxHeight: Dp = NavigationPopupMenuDefaults.MaxHeight,
    materialBlur: Boolean = true,
) {
    WindowListPopup(
        show = show,
        popupModifier = popupModifier,
        popupPositionProvider = NavigationPopupMenuDefaults.PositionProvider,
        alignment = PopupPositionProvider.Align.End,
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        maxHeight = maxHeight,
        minWidth = minWidth,
        materialBlur = materialBlur,
    ) {
        NavigationPopupMenuContent(
            items = items,
            dropdownColors = dropdownColors,
            onDismissRequest = onDismissRequest,
        )
    }
}
