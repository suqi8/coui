// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.layout

import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import kotlin.test.Test
import kotlin.test.assertEquals

class CascadingGeometryTest {

    private val available = IntRect(left = 48, top = 200, right = 1200, bottom = 2500)
    private val main = IntRect(left = 500, top = 400, right = 1100, bottom = 1100)
    private val anchorRow = IntRect(left = 500, top = 700, right = 1100, bottom = 840)

    private fun geometry(compact: Boolean, popupAnchor: IntRect, sub: IntSize = IntSize(600, 600)) = computeSubmenuGeometry(
        main = main,
        anchorRow = anchorRow,
        popupAnchor = popupAnchor,
        subSize = sub,
        available = available,
        compact = compact,
        layoutDirection = LayoutDirection.Ltr,
        verticalOverlap = 49,
        horizontalOverlap = 49,
        minGap = 42,
        listInset = 28,
    )

    @Test
    fun compactSubMenuCoversTheMainMenuAboveTheAnchorRow() {
        // Anchor on the right of the menu centre: the main menu shrinks towards its right edge.
        val g = geometry(compact = true, popupAnchor = IntRect(1000, 300, 1100, 380))
        assertEquals(true, g.compact)
        assertEquals(main.left, g.subRect.left)
        assertEquals(main.width, g.subRect.width)
        // Relocated main: 540 wide, right aligned, same top; anchor top after relocation:
        // 400 + 0.9 * 300 = 670, minus the 14dp overlap.
        assertEquals(670 - 49, g.subRect.top)
        assertEquals(TransformOrigin(1f, 0f), g.mainPivot)
        assertEquals(0.9f, g.mainScale, 0.001f)
        // The sub menu starts with its header (after the list inset) on the anchor row.
        assertEquals((anchorRow.top - g.subRect.top - 28).toFloat(), g.subStartTranslationY)
        assertEquals(28f, g.clipTopStart)
        assertEquals(anchorRow.height.toFloat(), g.clipHeightStart)
        assertEquals(0.3f, g.mainContentAlpha)
    }

    @Test
    fun compactCollapseClosesOntoTheRowWhereTheShrunkMainMenuDrawsIt() {
        val g = geometry(compact = true, popupAnchor = IntRect(1000, 300, 1100, 380))
        // SmallScreenAnimationController.onSubMenuProgressUpdate: the start is re-read from the
        // anchor row of the main menu scaled about its top at the same progress.
        val depth = (anchorRow.top - main.top).toFloat()
        assertEquals(depth, g.anchorDepth)
        assertEquals(g.subStartTranslationY, g.collapseStartTranslationY(0f), 0.001f)
        assertEquals(g.subStartTranslationY - (1f - g.mainScale) * depth, g.collapseStartTranslationY(1f), 0.001f)
        assertEquals(g.subStartTranslationY - (1f - g.mainScale) * depth / 2f, g.collapseStartTranslationY(0.5f), 0.001f)
    }

    @Test
    fun compactMainMenuMovesUpToKeepTheGap() {
        // main.top + 12dp gap lies below the sub top, so the main menu moves up.
        val g = geometry(compact = true, popupAnchor = IntRect(780, 300, 820, 380))
        assertEquals(TransformOrigin(0.5f, 0f), g.mainPivot)
        val expected = (g.subRect.top - 42 - main.top).toFloat()
        assertEquals(if (main.top + 42 <= g.subRect.top) 0f else expected, g.mainTranslationY)
    }

    @Test
    fun largerWindowsOpenTheSubMenuBesideTheMainMenu() {
        val g = geometry(compact = false, popupAnchor = IntRect(1000, 300, 1100, 380), sub = IntSize(600, 400))
        assertEquals(false, g.compact)
        // Relocated main 530..1070: no room on the left (530 + 49 - 600 < 48), so the sub opens
        // on the right, overlapping 14dp.
        assertEquals(1070 - 49, g.subRect.left)
        assertEquals(anchorRow.top, g.subRect.top)
        assertEquals(TransformOrigin.Center, g.mainPivot)
        assertEquals(0.7f, g.mainContentAlpha)
        // Pivot on the edge facing the main menu, at the anchor row centre.
        assertEquals(0f, g.subPivot.pivotFractionX)
        assertEquals((anchorRow.center.y - g.subRect.top) / 400f, g.subPivot.pivotFractionY, 0.001f)
    }

    @Test
    fun largerWindowsPreferTheLeftSideInLtr() {
        val g = computeSubmenuGeometry(
            main = IntRect(1000, 400, 1600, 1100),
            anchorRow = IntRect(1000, 700, 1600, 840),
            popupAnchor = IntRect(1500, 300, 1600, 380),
            subSize = IntSize(600, 400),
            available = IntRect(48, 200, 2000, 2500),
            compact = false,
            layoutDirection = LayoutDirection.Ltr,
            verticalOverlap = 49,
            horizontalOverlap = 49,
            minGap = 42,
            listInset = 28,
        )
        // Relocated left = 1300 - 270 = 1030; 1030 + 49 - 600 = 479 clears the barrier.
        assertEquals(479, g.subRect.left)
        assertEquals(1f, g.subPivot.pivotFractionX)
    }
}
