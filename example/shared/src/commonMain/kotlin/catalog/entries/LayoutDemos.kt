// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package catalog.entries

import catalog.Demo
import catalog.DemoCategory
import demo.layout.ExpandableGroupDemo
import demo.layout.ListDetailLayoutDemo
import demo.layout.ResponsiveGridDemo
import demo.layout.SidePaneLayoutDemo
import demo.layout.SpringChainDemo
import demo.layout.SwipeActionItemDemo

/**
 * The demos of [DemoCategory.Layout], in list order.
 */
internal val LayoutDemos: List<Demo> = listOf(
    ListDetailLayoutDemo,
    SidePaneLayoutDemo,
    ResponsiveGridDemo,
    ExpandableGroupDemo,
    SwipeActionItemDemo,
    SpringChainDemo,
)
