// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TooltipPlacementTest {

    // 3x density: 13dp inset = 39px, 16dp margin = 48px, gaps 16px (above / below) and 17px (side).
    private val metrics = TooltipMetrics.of(3f, 13f, 13f, 13f, 13f)
    private val viewport = IntSize(1080, 2400)
    private val window = IntSize(400, 150)

    @Test
    fun metricsFollowTheCouiDimens() {
        assertEquals(39, metrics.insetLeft)
        assertEquals(48, metrics.screenMargin)
        assertEquals(16, metrics.overflowAbove) // 17.1 + (-0.87) as getDimensionPixelSize rounds them
        assertEquals(16, metrics.overflowBelow) // 17.1 + (-1.2)
        assertEquals(17, metrics.overflowSide)
    }

    @Test
    fun aboveIsCentredOnTheAnchorWithTheArrowPointingDown() {
        val anchor = IntRect(500, 1000, 600, 1100)
        val placement = calculateTooltipPlacement(anchor, viewport, window, TooltipSide.Above, metrics, showArrow = true)
        assertEquals(TooltipSide.Above, placement.side)
        assertEquals(IntOffset(350, 1000 - 150 - 16), placement.position)
        assertEquals(TooltipArrowDirection.Down, placement.arrow)
        // The middle arrow (60px wide) is centred on the anchor centre.
        assertEquals(550f - 350f - 30f, placement.arrowTopLeft.x)
        // Arrow tip = window bottom - (inset - arrow height + embed): about 8.6dp above the anchor.
        val arrowHeight = TooltipArrowDirection.Down.size(3f).height
        val tipY = placement.position.y + placement.arrowTopLeft.y + arrowHeight
        val gapDp = (anchor.top - tipY) / 3f
        assertTrue(abs(gapDp - 8.56f) < 0.3f, "arrow tip gap was ${gapDp}dp")
        assertEquals(0.5f, placement.pivot.pivotFractionX)
        assertEquals(1f, placement.pivot.pivotFractionY)
    }

    @Test
    fun aboveMovesBelowWhenThereIsNoRoom() {
        val anchor = IntRect(500, 100, 600, 200)
        val placement = calculateTooltipPlacement(anchor, viewport, window, TooltipSide.Above, metrics, showArrow = true)
        assertEquals(TooltipSide.Below, placement.side)
        assertEquals(200 + 16, placement.position.y)
        assertEquals(TooltipArrowDirection.Up, placement.arrow)
        assertEquals(0f, placement.pivot.pivotFractionY)
        // Arrow base overlaps the bubble by the 0.33dp embed distance.
        val arrowHeight = TooltipArrowDirection.Up.size(3f).height
        assertTrue(abs(placement.arrowTopLeft.y + arrowHeight - (39f + 0.99f)) < 0.01f)
    }

    @Test
    fun neitherSideFitsTakesTheLargerSideAndShrinks() {
        val anchor = IntRect(500, 900, 600, 1000)
        val (side, cap) = tooltipVerticalSide(TooltipSide.Above, anchor, viewport, 2000)
        assertEquals(TooltipSide.Below, side)
        assertEquals(1400, cap)
        val (fits, noCap) = tooltipVerticalSide(TooltipSide.Below, anchor, viewport, 300)
        assertEquals(TooltipSide.Below, fits)
        assertNull(noCap)
    }

    @Test
    fun screenMarginClampsTheWindow() {
        val left = calculateTooltipPlacement(IntRect(0, 1000, 60, 1100), viewport, window, TooltipSide.Above, metrics, true)
        // Visible bubble keeps 16dp: window x = margin - inset.
        assertEquals(48 - 39, left.position.x)
        val right = calculateTooltipPlacement(IntRect(1020, 1000, 1080, 1100), viewport, window, TooltipSide.Above, metrics, true)
        assertEquals(1080 - (400 - 39 + 48), right.position.x)
    }

    @Test
    fun edgeArrowsTakeOverNearTheCorners() {
        val left = calculateTooltipPlacement(IntRect(0, 1000, 60, 1100), viewport, window, TooltipSide.Above, metrics, true)
        assertEquals(TooltipArrowDirection.DownLeft, left.arrow)
        // max(R 27 + inset 39 + edge extra 6, anchor centre 30)
        assertEquals(72f, left.arrowTopLeft.x)

        val right = calculateTooltipPlacement(IntRect(1020, 1000, 1080, 1100), viewport, window, TooltipSide.Above, metrics, true)
        assertEquals(TooltipArrowDirection.DownRight, right.arrow)
        val edgeWidth = TooltipArrowDirection.DownRight.size(3f).width
        assertEquals(400f - 72f - edgeWidth, right.arrowTopLeft.x)

        val below = calculateTooltipPlacement(IntRect(0, 100, 60, 200), viewport, window, TooltipSide.Below, metrics, true)
        assertEquals(TooltipArrowDirection.UpLeft, below.arrow)
    }

    @Test
    fun sidePlacementIsVerticallyCentredAndNeverFlips() {
        val anchor = IntRect(100, 1000, 200, 1100)
        val tall = IntSize(400, 300)
        val right = calculateTooltipPlacement(anchor, viewport, tall, TooltipSide.Right, metrics, true)
        assertEquals(TooltipSide.Right, right.side)
        assertEquals(IntOffset(200 + 17, 1050 - 150), right.position)
        assertEquals(TooltipArrowDirection.Left, right.arrow)
        assertEquals(0f, right.pivot.pivotFractionX)
        assertEquals(0.5f, right.pivot.pivotFractionY)

        // Not enough room on the left: still on the left, clamped by the screen margin.
        val left = calculateTooltipPlacement(anchor, viewport, tall, TooltipSide.Left, metrics, true)
        assertEquals(TooltipSide.Left, left.side)
        assertEquals(48 - 39, left.position.x)
        assertEquals(TooltipArrowDirection.Right, left.arrow)
        assertEquals(1f, left.pivot.pivotFractionX)
    }

    @Test
    fun sideArrowSwitchesToTheEdgeArrowNearTheTop() {
        val anchor = IntRect(100, 0, 200, 40)
        val right = calculateTooltipPlacement(anchor, viewport, window, TooltipSide.Right, metrics, true)
        assertEquals(TooltipArrowDirection.LeftUp, right.arrow)
        assertEquals(39f + 27f, right.arrowTopLeft.y)
    }

    @Test
    fun sideWidthIsLimitedToTheRoomBesideTheAnchor() {
        val anchor = IntRect(100, 1000, 200, 1100)
        val styleMax = 328 * 3
        assertEquals(1080 - 200 - 17 + 39 - 48, tooltipMaxWindowWidth(TooltipSide.Right, anchor, viewport, metrics, styleMax))
        assertEquals(100 - 17 - (48 - 39), tooltipMaxWindowWidth(TooltipSide.Left, anchor, viewport, metrics, styleMax))
        assertEquals(1062, tooltipMaxWindowWidth(TooltipSide.Above, anchor, viewport, metrics, styleMax))
    }

    @Test
    fun sideFallsBackToAboveNearTheBottom() {
        assertFalse(tooltipSideFallsBackToVertical(IntRect(100, 2300, 200, 2400), viewport, metrics, 150))
        assertTrue(tooltipSideFallsBackToVertical(IntRect(100, 2340, 200, 2420), viewport, metrics, 150))
    }

    @Test
    fun startEndFollowTheLayoutDirection() {
        assertEquals(TooltipSide.Left, TooltipAnchorPosition.Start.toTooltipSide(LayoutDirection.Ltr))
        assertEquals(TooltipSide.Right, TooltipAnchorPosition.Start.toTooltipSide(LayoutDirection.Rtl))
        assertEquals(TooltipSide.Left, TooltipAnchorPosition.End.toTooltipSide(LayoutDirection.Rtl))
    }

    @Test
    fun offsetMovesTheWindowButTheArrowKeepsPointingAtTheAnchor() {
        val anchor = IntRect(500, 1000, 600, 1100)
        val base = calculateTooltipPlacement(anchor, viewport, window, TooltipSide.Above, metrics, true)
        val moved = calculateTooltipPlacement(anchor, viewport, window, TooltipSide.Above, metrics, true, offset = IntOffset(30, -10))
        assertEquals(base.position + IntOffset(30, -10), moved.position)
        assertEquals(base.position.x + base.arrowTopLeft.x, moved.position.x + moved.arrowTopLeft.x)

        val rtl = calculateTooltipPlacement(
            anchor,
            viewport,
            window,
            TooltipSide.Above,
            metrics,
            true,
            offset = IntOffset(30, 0),
            layoutDirection = LayoutDirection.Rtl,
        )
        assertEquals(base.position.x - 30, rtl.position.x)

        val follows = calculateTooltipPlacement(anchor, viewport, window, TooltipSide.Above, metrics, true, IntOffset(30, 0), arrowFollowsOffset = true)
        assertEquals(base.arrowTopLeft.x, follows.arrowTopLeft.x)
    }

    @Test
    fun toolbarSpacingPullsTheWindowIntoTheAnchor() {
        val anchor = IntRect(500, 100, 600, 200)
        val toolbar = TooltipAnchorSpacing.toolbar(3f)
        assertEquals(-30, toolbar.overflow) // -10.1dp
        val below = calculateTooltipPlacement(anchor, viewport, window, TooltipSide.Below, metrics, true, spacing = toolbar)
        assertEquals(200 - 30, below.position.y)
        // The toolbar spacing also counts toward the room below the anchor.
        val (side, _) = tooltipVerticalSide(TooltipSide.Below, IntRect(500, 2230, 600, 2260), viewport, 165, toolbar)
        assertEquals(TooltipSide.Below, side)
        val (normalSide, _) = tooltipVerticalSide(TooltipSide.Below, IntRect(500, 2230, 600, 2260), viewport, 165)
        assertEquals(TooltipSide.Above, normalSide)
    }

    @Test
    fun navigationSpacingAddsTheOffsetFromTheBarTop() {
        // Bar top 2200, anchor top 2212: overflow = -3.3dp (-10px) + (2200 - 2212).
        val navigation = TooltipAnchorSpacing.navigation(3f, containerTop = 2200, anchorTop = 2212)
        assertEquals(-10 - 12, navigation.overflow)
        assertEquals(-10, navigation.spaceBelowSpacing)
        val anchor = IntRect(500, 2212, 600, 2350)
        val above = calculateTooltipPlacement(anchor, viewport, window, TooltipSide.Above, metrics, true, spacing = navigation)
        assertEquals(2212 - 150 + 22, above.position.y)
        // Side tooltips use the same overflow.
        assertEquals(1080 - 600 + 22 + 39 - 48, tooltipMaxWindowWidth(TooltipSide.Right, anchor, viewport, metrics, 984, navigation))
    }

    @Test
    fun noArrowWhenHidden() {
        val placement = calculateTooltipPlacement(IntRect(500, 1000, 600, 1100), viewport, window, TooltipSide.Above, metrics, showArrow = false)
        assertNull(placement.arrow)
    }

    @Test
    fun arrowPathsPointTheRightWay() {
        fun Path.hits(rect: Rect): Boolean {
            val probe = Path().apply { addRect(rect) }
            return !Path.combine(PathOperation.Intersect, this, probe).getBounds().isEmpty
        }
        val up = TooltipArrowDirection.Up.path(1f, Offset.Zero)
        assertTrue(up.hits(Rect(9.5f, 0f, 10.5f, 0.5f)), "up tip at the top centre")
        assertFalse(up.hits(Rect(0f, 0f, 3f, 3f)))

        val down = TooltipArrowDirection.Down.path(1f, Offset.Zero)
        assertTrue(down.hits(Rect(9.5f, 9.6f, 10.5f, 10.1f)), "down tip at the bottom centre")

        val right = TooltipArrowDirection.Right.path(1f, Offset.Zero)
        val bounds = right.getBounds()
        assertTrue(abs(bounds.width - 10.1f) < 0.05f && abs(bounds.height - 20f) < 0.05f, "right arrow is 10.1 x 20: $bounds")
        assertTrue(right.hits(Rect(9.6f, 9.5f, 10.1f, 10.5f)), "right tip at the right centre")

        // The edge arrow keeps its straight flank on the corner side, with the tip 2dp from it.
        val upLeft = TooltipArrowDirection.UpLeft.path(1f, Offset.Zero)
        assertTrue(upLeft.hits(Rect(1.5f, 0f, 2.5f, 0.5f)), "up-left tip near the left flank")
        val upRight = TooltipArrowDirection.UpRight.path(1f, Offset.Zero)
        assertTrue(upRight.hits(Rect(13.8f, 0f, 14.8f, 0.5f)), "up-right tip near the right flank")
        val rightUp = TooltipArrowDirection.RightUp.path(1f, Offset.Zero)
        assertTrue(rightUp.hits(Rect(9.8f, 1.5f, 10.29f, 2.5f)), "right-up tip near the top flank")
        val leftDown = TooltipArrowDirection.LeftDown.path(1f, Offset.Zero)
        assertTrue(leftDown.hits(Rect(0f, 13.8f, 0.5f, 14.8f)), "left-down tip near the bottom flank")
    }
}
