// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package catalog.entries

import catalog.Demo
import catalog.DemoCategory
import demo.navigation.AlphabetIndexBarDemo
import demo.navigation.BreadcrumbBarDemo
import demo.navigation.CameraModeBarDemo
import demo.navigation.ChipTabDemo
import demo.navigation.FloatingAccessoryNavigationBarDemo
import demo.navigation.FloatingEditToolbarDemo
import demo.navigation.FloatingNavigationBarDemo
import demo.navigation.FloatingToolbarDemo
import demo.navigation.Navigation3TransitionDemo
import demo.navigation.NavigationBarDemo
import demo.navigation.NavigationRailDemo
import demo.navigation.SideNavigationBarDemo
import demo.navigation.TabRowDemo
import demo.navigation.TopAppBarDemo

/**
 * The demos of [DemoCategory.Navigation], in list order.
 */
internal val NavigationDemos: List<Demo> = listOf(
    TopAppBarDemo,
    NavigationBarDemo,
    FloatingNavigationBarDemo,
    FloatingAccessoryNavigationBarDemo,
    NavigationRailDemo,
    SideNavigationBarDemo,
    FloatingToolbarDemo,
    FloatingEditToolbarDemo,
    TabRowDemo,
    ChipTabDemo,
    BreadcrumbBarDemo,
    AlphabetIndexBarDemo,
    CameraModeBarDemo,
    Navigation3TransitionDemo,
)
