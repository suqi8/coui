// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ListPopupGeometryTest {

    private val available = IntRect(left = 16, top = 100, right = 1064, bottom = 2200)
    private val menu = IntSize(width = 600, height = 500)

    @Test
    fun menuOpensBelowTheAnchorCentredOnIt() {
        val anchor = IntRect(left = 200, top = 300, right = 600, bottom = 400)
        val offset = locateDropdownPopup(anchor, available, menu, gapAbove = 28, gapBelow = 28)
        assertEquals(IntOffset(x = 100, y = 428), offset)
    }

    @Test
    fun menuFlipsAboveWhenBelowHasNoRoom() {
        val anchor = IntRect(left = 200, top = 1900, right = 600, bottom = 2000)
        val offset = locateDropdownPopup(anchor, available, menu, gapAbove = 28, gapBelow = 28)
        assertEquals(1900 - 28 - 500, offset.y)
    }

    @Test
    fun menuFallsBackToTheAvailableTopWhenNeitherSideFits() {
        val tall = IntSize(width = 600, height = 1500)
        val anchor = IntRect(left = 200, top = 1000, right = 600, bottom = 1100)
        val offset = locateDropdownPopup(anchor, available, tall, gapAbove = 28, gapBelow = 28)
        // PopupMenuLocateHelper starts from mAvailableBounds.top, it does not centre on the anchor.
        assertEquals(available.top, offset.y)
    }

    @Test
    fun menuIsPushedInsideTheBarriers() {
        val anchor = IntRect(left = 1000, top = 300, right = 1060, bottom = 400)
        val offset = locateDropdownPopup(anchor, available, menu, gapAbove = 28, gapBelow = 28)
        assertEquals(available.right - menu.width, offset.x)
    }

    @Test
    fun pointAnchorDropsTheGap() {
        val point = IntRect(left = 500, top = 600, right = 500, bottom = 600)
        val offset = locateDropdownPopup(point, available, menu, gapAbove = 0, gapBelow = 0)
        assertEquals(IntOffset(x = 200, y = 600), offset)
    }

    @Test
    fun widthClampsBetweenMinAndMax() {
        assertEquals(168, resolvePopupListWidth(widest = 100, minWidth = 168, maxWidth = 256, parentMin = 0, parentMax = 1000))
        assertEquals(256, resolvePopupListWidth(widest = 400, minWidth = 168, maxWidth = 256, parentMin = 0, parentMax = 1000))
        assertEquals(200, resolvePopupListWidth(widest = 200, minWidth = 168, maxWidth = 256, parentMin = 0, parentMax = 1000))
        // A narrow window still wins over the menu minimum.
        assertEquals(150, resolvePopupListWidth(widest = 100, minWidth = 168, maxWidth = 256, parentMin = 0, parentMax = 150))
    }

    @Test
    fun viewportStopsAtTheLastWholeRow() {
        val rows = listOf(40, 40, 40, 40)
        assertEquals(16 + 160, snapPopupListHeight(rows, verticalInsets = 16, maxHeight = 1000))
        assertEquals(16 + 80, snapPopupListHeight(rows, verticalInsets = 16, maxHeight = 130))
    }

    @Test
    fun gravityPathRunsFromTheAnchorToTheMenuCentre() {
        val path = PopupGravityPath.from(ax = -200f, ay = -400f)
        val start = path.pointAt(0f)
        val end = path.pointAt(1f)
        assertEquals(-200f, start.x, 0.01f)
        assertEquals(-400f, start.y, 0.01f)
        assertEquals(0f, end.x, 0.01f)
        assertEquals(0f, end.y, 0.01f)
    }

    @Test
    fun straightDropHasNoGravityArc() {
        val path = PopupGravityPath.from(ax = 0f, ay = -400f)
        val mid = path.pointAt(0.5f)
        assertEquals(0f, mid.x, 0.01f)
    }

    @Test
    fun obliqueTravelSagsDownTheScreen() {
        // The menu opens below and to the right of its anchor: the chord runs from the anchor
        // (up-left) to the centre; the gravity normal points down the screen.
        val path = PopupGravityPath.from(ax = -300f, ay = -300f)
        val mid = path.pointAt(0.5f)
        val chordMidY = -150f
        assertTrue(mid.y > chordMidY, "the arc must bulge downwards: $mid")
        // 300/300 is the steepest ratio; at 424px the distance factor saturates to the full 130px arc.
        assertTrue(abs(mid.y - chordMidY) > 30f, "gravity too weak: $mid")
    }

    @Test
    fun singleLineRowsGetACapsuleMask() {
        val row = PopupListItemHandle().apply {
            top = 10f
            bottom = 50f
            singleLine = true
        }
        val geometry = popupMaskGeometry(row, width = 300f, horizontalMargin = 8f, cornerRadius = 28f)
        assertEquals(8f, geometry.left)
        assertEquals(292f, geometry.right)
        assertEquals(20f, geometry.radius)
        assertEquals(1f, geometry.weight)
    }

    @Test
    fun multiLineRowsKeepTheFixedMaskRadius() {
        val row = PopupListItemHandle().apply {
            top = 0f
            bottom = 80f
            singleLine = false
        }
        val geometry = popupMaskGeometry(row, width = 300f, horizontalMargin = 8f, cornerRadius = 28f)
        assertEquals(28f, geometry.radius)
        assertEquals(3f, geometry.weight)
    }

    @Test
    fun horizontalBarrierFollowsTheGridMargin() {
        assertEquals(16.dp, ListPopupDefaults.horizontalBarrier(widthDp = 411f, heightDp = 900f))
        assertEquals(24.dp, ListPopupDefaults.horizontalBarrier(widthDp = 700f, heightDp = 900f))
        assertEquals(40.dp, ListPopupDefaults.horizontalBarrier(widthDp = 1280f, heightDp = 800f))
    }
}
