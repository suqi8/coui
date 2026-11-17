// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package catalog.entries

import catalog.Demo
import catalog.DemoCategory
import demo.menus.ContextMenuDemo
import demo.menus.DropdownMenuDemo
import demo.menus.ListPopupDemo
import demo.menus.NavigationPopupMenuDemo
import demo.menus.SpinnerDemo
import demo.menus.TooltipDemo
import demo.menus.TopTipsDemo

/**
 * The demos of [DemoCategory.Menus], in list order.
 */
internal val MenusDemos: List<Demo> = listOf(
    DropdownMenuDemo,
    ListPopupDemo,
    ContextMenuDemo,
    SpinnerDemo,
    NavigationPopupMenuDemo,
    TooltipDemo,
    TopTipsDemo,
)
