// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package catalog.entries

import catalog.Demo
import catalog.DemoCategory
import demo.display.BadgeDemo
import demo.display.BannerDemo
import demo.display.BasicComponentDemo
import demo.display.CardDemo
import demo.display.MarqueeTextDemo
import demo.display.PageIndicatorDemo
import demo.display.TagViewDemo

/**
 * The demos of [DemoCategory.Display], in list order.
 */
internal val DisplayDemos: List<Demo> = listOf(
    BasicComponentDemo,
    CardDemo,
    BadgeDemo,
    TagViewDemo,
    MarqueeTextDemo,
    BannerDemo,
    PageIndicatorDemo,
)
