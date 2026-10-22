// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.utils

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

class COUILayoutGridTest {

    private fun assertDp(expected: Dp, actual: Dp) {
        assertTrue(abs(expected.value - actual.value) < 0.01f, "expected $expected, got $actual")
    }

    @Test
    fun compactWindowSpansTheWholeGrid() {
        // Four columns only: six columns fill the window minus the two 16dp margins.
        assertDp(328.dp, COUILayoutGrid.gridWidth(6, 360.dp, 800.dp))
    }

    @Test
    fun mediumWindowUsesSixOfEightColumns() {
        // (700 - 2 * 24 - 7 * 8) / 8 = 74.5 per column; 6 * 74.5 + 5 * 8 = 487.
        assertDp(487.dp, COUILayoutGrid.gridWidth(6, 700.dp, 1000.dp))
    }

    @Test
    fun expandedWindowUsesSixOfTwelveColumns() {
        // (1200 - 2 * 40 - 11 * 12) / 12 = 82.33 per column; 6 * 82.33 + 5 * 12 = 554.
        assertDp(554.dp, COUILayoutGrid.gridWidth(6, 1200.dp, 800.dp))
    }

    @Test
    fun tallPortraitExpandedWindowKeepsTheMediumGrid() {
        // ExpandedPortrait: (900 - 48 - 56) / 8 = 99.5 per column; 6 * 99.5 + 40 = 637.
        assertDp(637.dp, COUILayoutGrid.gridWidth(6, 900.dp, 1200.dp))
    }
}
