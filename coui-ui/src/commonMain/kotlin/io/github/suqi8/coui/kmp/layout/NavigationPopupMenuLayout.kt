// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.layout

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.DropdownColors
import io.github.suqi8.coui.kmp.basic.DropdownItem
import io.github.suqi8.coui.kmp.basic.ListPopupColumn
import io.github.suqi8.coui.kmp.basic.PopupPositionProvider
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.popupListItem
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.math.max

/**
 * The rows of a navigation popup menu (`COUINavigationPopupMenu.MenuAdapter`): one row per item,
 * its icon and title. A click on an enabled row dismisses the menu first and then runs the item's
 * action (`onItemClick`: `dismiss()`, then `performClick()`).
 */
@Composable
internal fun NavigationPopupMenuContent(
    items: List<DropdownItem>,
    dropdownColors: DropdownColors,
    onDismissRequest: () -> Unit,
) {
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)
    ListPopupColumn {
        items.forEachIndexed { index, item ->
            key(index) {
                NavigationPopupMenuRow(
                    item = item,
                    dropdownColors = dropdownColors,
                    onClick = {
                        currentOnDismissRequest()
                        item.onClick?.invoke()
                    },
                )
            }
        }
    }
}

/**
 * One row of `COUINavigationPopupMenu` (coui_navigation_popup_item.xml): 12dp above and below, the
 * icon at its own size 16dp from the start, then the title 16dp from the icon and the end, 16sp
 * bold (`coui_navigation_popup_text_size`, `textStyle="bold"`). The adapter never marks the checked
 * item, so no selection check is drawn.
 */
@Composable
private fun NavigationPopupMenuRow(
    item: DropdownItem,
    dropdownColors: DropdownColors,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .popupListItem(
                selected = item.selected,
                enabled = item.enabled,
                role = Role.Button,
                hasIcon = item.icon != null,
                onClick = onClick,
            )
            .padding(vertical = NavigationPopupMenuDefaults.ItemVerticalPadding),
    ) {
        item.icon?.invoke(Modifier.padding(start = NavigationPopupMenuDefaults.ItemHorizontalPadding))
        Text(
            text = item.text,
            modifier = Modifier.padding(horizontal = NavigationPopupMenuDefaults.ItemHorizontalPadding),
            style = COUITheme.textStyles.bodyXL,
            fontWeight = FontWeight.Bold,
            color = if (item.enabled) dropdownColors.contentColor else dropdownColors.disabledContentColor,
        )
    }
}

/** Contains default values used by the navigation popup menus. */
object NavigationPopupMenuDefaults {
    /** The top and bottom padding of a row (coui_navigation_popup_item.xml paddingTop / paddingBottom). */
    val ItemVerticalPadding = 12.dp

    /**
     * The start margin of the icon and the horizontal margins of the title
     * (coui_navigation_popup_horizontal_margin, coui_navigation_popup_item.xml).
     */
    val ItemHorizontalPadding = 16.dp

    /**
     * The minimum menu width (coui_navigation_popup_item_min_width). COUI widens the menu to the
     * widest title plus coui_navigation_popup_item_used_space (72dp); here the popup column measures
     * its rows within the COUI 17 popup width range.
     */
    val MinWidth = 208.dp

    /** The maximum menu height (coui_navigation_popup_max_height); taller menus scroll. */
    val MaxHeight = 324.dp

    /** The inset of the menu's end edge from the anchor's end edge (coui_navigation_popup_horizontal_margin). */
    val HorizontalMargin = 16.dp

    /** The gap between the anchor and the menu (coui_navigation_popup_vertical_margin). */
    val VerticalMargin = 8.dp

    /**
     * The placement of `COUINavigationPopupMenu`'s `ListPopupWindow`: drop-down gravity
     * `END | BOTTOM` with a horizontal offset of -[HorizontalMargin] and a vertical gap of
     * [VerticalMargin]. The menu's end edge sits [HorizontalMargin] in from the anchor's end edge; it
     * opens below the anchor when it fits and above it otherwise, which for a navigation bar at the
     * bottom of the window is always above. It is kept inside the window's safe area.
     */
    val PositionProvider: PopupPositionProvider = object : PopupPositionProvider {
        private val margins = PaddingValues(horizontal = HorizontalMargin, vertical = VerticalMargin)

        override fun calculatePosition(
            anchorBounds: IntRect,
            windowBounds: IntRect,
            layoutDirection: LayoutDirection,
            popupContentSize: IntSize,
            popupMargin: IntRect,
            alignment: PopupPositionProvider.Align,
        ): IntOffset {
            val width = popupContentSize.width
            val height = popupContentSize.height
            val rawX = if (layoutDirection == LayoutDirection.Ltr) {
                anchorBounds.right - popupMargin.right - width
            } else {
                anchorBounds.left + popupMargin.left
            }
            val x = rawX.coerceIn(windowBounds.left, max(windowBounds.left, windowBounds.right - width))
            val below = anchorBounds.bottom + popupMargin.bottom
            val above = anchorBounds.top - popupMargin.top - height
            val y = when {
                below + height <= windowBounds.bottom -> below
                above >= windowBounds.top -> above
                else -> max(windowBounds.top, windowBounds.bottom - height)
            }
            return IntOffset(x, y)
        }

        override fun getMargins(): PaddingValues = margins
    }
}
