// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.overlay

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
 * (`COUINavigationPopupMenu`), rendered in the [io.github.suqi8.coui.kmp.basic.Scaffold] popup host.
 *
 * Call it inside the layout that wraps the navigation bar: that layout is the anchor. The menu
 * lines up its end edge 16dp in from the anchor's end edge and opens 8dp above the bar (below it
 * when there is room), at least 208dp wide and at most 324dp tall. Each row shows an item's icon and
 * title; the [DropdownItem.selected] item is marked as the current destination and disabled items
 * are greyed out. A click on an enabled row dismisses the menu and then runs [DropdownItem.onClick],
 * as COUI dismisses the popup before performing the navigation item's click.
 *
 * The menu uses the COUI 17 list popup surface, rows and motion; COUI's own `ListPopupWindow` with
 * the legacy `coui_navigation_popup_bg` nine-patch is not reproduced.
 *
 * @param show Whether the menu is shown.
 * @param items The destinations, in order.
 * @param onDismissRequest Called when the menu should close: a row click, a tap outside or back.
 * @param popupModifier The modifier applied to the popup content area.
 * @param onDismissFinished Called once the exit animation has finished.
 * @param dropdownColors The colors of the rows.
 * @param minWidth The minimum menu width.
 * @param maxHeight The maximum menu height.
 * @param renderInRootScaffold Whether to render the menu in the root (outermost) Scaffold.
 * @param materialBlur Whether the menu blurs what lies behind it with the COUI 17 popup material,
 *   when a blur provider is in scope.
 */
@Composable
fun OverlayNavigationPopupMenu(
    show: Boolean,
    items: List<DropdownItem>,
    onDismissRequest: () -> Unit,
    popupModifier: Modifier = Modifier,
    onDismissFinished: (() -> Unit)? = null,
    dropdownColors: DropdownColors = DropdownDefaults.dropdownColors(),
    minWidth: Dp = NavigationPopupMenuDefaults.MinWidth,
    maxHeight: Dp = NavigationPopupMenuDefaults.MaxHeight,
    renderInRootScaffold: Boolean = true,
    materialBlur: Boolean = true,
) {
    OverlayListPopup(
        show = show,
        popupModifier = popupModifier,
        popupPositionProvider = NavigationPopupMenuDefaults.PositionProvider,
        alignment = PopupPositionProvider.Align.End,
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        maxHeight = maxHeight,
        minWidth = minWidth,
        renderInRootScaffold = renderInRootScaffold,
        materialBlur = materialBlur,
    ) {
        NavigationPopupMenuContent(
            items = items,
            dropdownColors = dropdownColors,
            onDismissRequest = onDismissRequest,
        )
    }
}
