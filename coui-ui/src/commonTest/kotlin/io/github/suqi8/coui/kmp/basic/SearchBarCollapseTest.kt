// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Checks the collapse arithmetic against the values decompiled from
 * `COUISearchBar.setSearchViewAnimateHeightPercent`.
 */
class SearchBarCollapseTest {
    private val normal = Color(0xFFEEEEEE)
    private val divider = Color(0xFF888888)

    private fun geometry(percent: Float) = searchBarCollapseGeometry(
        percent = percent,
        normalColor = normal,
        dividerColor = divider,
    )

    // --- capsule height: max(1px, 40dp * (p - 0.3) / 0.7) ---

    @Test
    fun capsuleHeight_isRestingHeightWhenExpanded() {
        assertEquals(40.dp.value, geometry(1f).capsuleHeight.value, 1e-3f)
    }

    @Test
    fun capsuleHeight_reachesTheFloorAtThirtyPercent() {
        // clampSearchViewHeight hits zero at 0.3, so the floor takes over from there down.
        assertEquals(1.dp.value, geometry(0.3f).capsuleHeight.value, 1e-3f)
        assertEquals(1.dp.value, geometry(0.15f).capsuleHeight.value, 1e-3f)
        assertEquals(1.dp.value, geometry(0f).capsuleHeight.value, 1e-3f)
    }

    @Test
    fun capsuleHeight_isHalfwayAtSixtyFivePercent() {
        // (0.65 - 0.3) / 0.7 = 0.5
        assertEquals(20.dp.value, geometry(0.65f).capsuleHeight.value, 1e-3f)
    }

    @Test
    fun capsuleHeight_matchesTheRampAtTheFadeBoundary() {
        // At the phase boundary the content has just finished fading: 40dp * (0.75 - 0.3) / 0.7.
        assertEquals(25.714f, geometry(0.75f).capsuleHeight.value, 1e-2f)
    }

    // --- content alpha: (p - 0.75) * 4 ---

    @Test
    fun contentAlpha_isOpaqueWhenExpanded() {
        assertEquals(1f, geometry(1f).contentAlpha, 1e-6f)
    }

    @Test
    fun contentAlpha_isGoneFromSeventyFivePercentDown() {
        assertEquals(0f, geometry(0.75f).contentAlpha, 1e-6f)
        assertEquals(0f, geometry(0.5f).contentAlpha, 1e-6f)
        assertEquals(0f, geometry(0f).contentAlpha, 1e-6f)
    }

    @Test
    fun contentAlpha_isHalfwayAcrossTheFadeBand() {
        assertEquals(0.5f, geometry(0.875f).contentAlpha, 1e-6f)
    }

    @Test
    fun contentAlpha_isClampedForCallersFeedingAGraphicsLayer() {
        // COUI passes this unclamped and lets the platform clamp at draw time.
        assertTrue(geometry(1.5f).contentAlpha <= 1f)
        assertTrue(geometry(-0.5f).contentAlpha >= 0f)
    }

    // --- capsule widening: extra = padding * (1 - min(1, p / 0.3)) ---

    @Test
    fun extraWidth_isZeroWhenExpanded() {
        assertEquals(0f, geometry(1f).extraHorizontalWidth.value, 1e-3f)
    }

    @Test
    fun extraWidth_finishesGrowingAtThirtyPercent() {
        // clampMarginValue saturates at 0.3, so there is no extra width above it.
        assertEquals(0f, geometry(0.3f).extraHorizontalWidth.value, 1e-3f)
        assertEquals(0f, geometry(0.5f).extraHorizontalWidth.value, 1e-3f)
    }

    @Test
    fun extraWidth_isFullyGrownWhenCollapsed() {
        val padding = SearchBarDefaults.LeadingIconStartPadding
        assertEquals(padding.value, geometry(0f).extraHorizontalWidth.value, 1e-3f)
        // Halfway through the margin ramp.
        assertEquals(padding.value / 2f, geometry(0.15f).extraHorizontalWidth.value, 1e-3f)
    }

    // --- capsule color: lerp(divider, normal, min(1, p / 0.3)) ---

    @Test
    fun capsuleColor_isNormalUntilTheMarginRampEnds() {
        assertEquals(normal, geometry(1f).capsuleColor)
        assertEquals(normal, geometry(0.3f).capsuleColor)
    }

    @Test
    fun capsuleColor_becomesTheDividerColorWhenFlattened() {
        assertEquals(divider, geometry(0f).capsuleColor)
    }

    // --- translationY: max(0, 26dp * (1 - p)) ---

    @Test
    fun translationY_isZeroWhenExpanded() {
        assertEquals(0f, geometry(1f).translationY.value, 1e-3f)
    }

    @Test
    fun translationY_isHalfTheBarHeightWhenFullyCollapsed() {
        assertEquals(26.dp.value, geometry(0f).translationY.value, 1e-3f)
    }

    @Test
    fun translationY_neverGoesNegative() {
        assertTrue(geometry(1.5f).translationY.value >= 0f)
    }

    // --- scroll conversion: 1 - clamp01((scroll - 10dp) / 58dp) ---

    @Test
    fun collapsePercent_staysExpandedThroughTheSlack() {
        assertEquals(1f, searchBarCollapsePercent(0.dp), 1e-6f)
        assertEquals(1f, searchBarCollapsePercent(10.dp), 1e-6f)
    }

    @Test
    fun collapsePercent_isFullyCollapsedPastTheRange() {
        // 10dp slack + 58dp range = 68dp, the 52dp bar plus its 16dp gap.
        assertEquals(0f, searchBarCollapsePercent(68.dp), 1e-6f)
        assertEquals(0f, searchBarCollapsePercent(200.dp), 1e-6f)
    }

    @Test
    fun collapsePercent_isLinearAcrossTheRange() {
        assertEquals(0.5f, searchBarCollapsePercent(39.dp), 1e-6f)
    }
}
