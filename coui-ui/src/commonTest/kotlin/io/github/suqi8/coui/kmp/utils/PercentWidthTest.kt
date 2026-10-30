// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.utils

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

class PercentWidthTest {

    private fun assertDp(expected: Dp, actual: Dp) {
        assertTrue(abs(expected.value - actual.value) < 0.01f, "expected $expected, got $actual")
    }

    @Test
    fun compactCardListInsetsByTheMarginMinusTheSpecialPadding() {
        // Four columns plus 2 * 16dp exceed 360 - 2 * 24: the width is capped and the paddings
        // fall back to 24 - 16 on both sides, so the content stays centred.
        val result = PercentWidthDefaults.resolve(360.dp, 360.dp, 800.dp, type = PercentWidthType.CardList)
        assertDp(312.dp, result.contentWidth)
        assertDp(8.dp, result.startPadding)
        assertDp(8.dp, result.endPadding)
    }

    @Test
    fun mediumDefaultSpansSixCentredColumns() {
        // (700 - 2 * 24 - 7 * 8) / 8 = 74.5 per column; 6 columns = 487, one column and gutter
        // after the margin on each side: 24 + 74.5 + 8 = 106.5.
        val result = PercentWidthDefaults.resolve(700.dp, 700.dp, 1000.dp)
        assertDp(487.dp, result.contentWidth)
        assertDp(106.5.dp, result.startPadding)
        assertDp(106.5.dp, result.endPadding)
    }
}
