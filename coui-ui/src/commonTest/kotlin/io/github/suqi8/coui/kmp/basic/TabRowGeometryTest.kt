// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TabRowGeometryTest {

    private val density = Density(1f)

    private fun geometry(
        specs: List<TabContentSpec>,
        width: Int,
        windowHeight: Dp = 800.dp,
        maxWidth: Dp = Dp.Unspecified,
    ) = with(density) {
        computeUnderlineTabGeometry(
            specs = specs,
            widthPx = width,
            heightPx = 48,
            windowHeight = windowHeight,
            minWidth = 0.dp,
            maxWidth = maxWidth,
            itemSpacing = Dp.Unspecified,
            horizontalMargin = Dp.Unspecified,
            contentAlignment = Alignment.Center,
        )
    }

    private fun text(width: Int) = TabContentSpec(textWidth = width, iconWidth = 0, badgeWidth = 0)

    @Test
    fun fewTabsSpreadOverTheContentWidth() {
        // measureSmallChild: gap = (360 - 90) / 4 = 67, padding = 67 / 2 = 33.
        val geo = geometry(List(3) { text(30) }, width = 360)
        assertEquals(33, geo.padding)
        assertEquals(listOf(96, 96, 96), geo.slotWidths)
        assertEquals(33f, geo.indicatorLeft(0))
        assertEquals(63f, geo.indicatorRight(0))
        assertFalse(geo.scrollable)
    }

    @Test
    fun overflowingTabsScrollAndCenterTheTarget() {
        // padding = 16 - 20 / 2 = 6, slots 60 + 20.
        val geo = geometry(List(10) { text(60) }, width = 360)
        assertEquals(6, geo.padding)
        assertTrue(geo.scrollable)
        assertEquals(800 - 348, geo.scrollRange)
        // calculateScrollXForTab(5, 0): 40 - 174 + 400.
        assertEquals(266f, geo.scrollTargetFor(5f))
        // Half way to tab 6 adds half of both slot widths.
        assertEquals(266f + 40f, geo.scrollTargetFor(5.5f))
        assertEquals(0f, geo.scrollTargetFor(0f))
        assertEquals(452f, geo.scrollTargetFor(9f))
    }

    @Test
    fun mediumRowsUseTheWiderDividerAndMargin() {
        // 700dp row: medium divider 24dp; 700dp x 1000dp window: medium margin 24dp.
        val geo = geometry(List(12) { text(60) }, width = 700, windowHeight = 1000.dp)
        assertEquals(24 - 12, geo.padding)
        assertEquals(84, geo.slotWidths[0])
    }

    @Test
    fun expandedWindowUsesTheLargeMargin() {
        val geo = geometry(List(14) { text(60) }, width = 900, windowHeight = 700.dp)
        assertEquals(40 - 12, geo.padding)
    }

    @Test
    fun theRedDotSqueezesAnOverlongLabel() {
        val geo = geometry(
            listOf(TabContentSpec(textWidth = 200, iconWidth = 0, badgeWidth = 8), text(20)),
            width = 360,
            maxWidth = 100.dp,
        )
        assertEquals(92, geo.tabs[0].textWidth)
        assertEquals(92f, geo.indicatorRight(0) - geo.indicatorLeft(0))
    }

    @Test
    fun iconTabsKeepTheIndicatorUnderTheLabel() {
        val geo = geometry(listOf(TabContentSpec(textWidth = 40, iconWidth = 24, badgeWidth = 0), text(40)), width = 360)
        val tab = geo.tabs[0]
        assertEquals(geo.indicatorLeft(0), (geo.slotLefts[0] + tab.contentStart + 24).toFloat())
    }
}
