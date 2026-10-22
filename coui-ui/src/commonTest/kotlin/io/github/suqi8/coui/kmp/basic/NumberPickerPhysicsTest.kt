// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NumberPickerPhysicsTest {

    @Test
    fun splineFlingMatchesTheAndroidFormula() {
        // 5000 px/s x 1.6 on a 3.5x display (the reference ColorOS phone).
        val fling = numberPickerFling(8000f, 3.5f)
        assertTrue(abs(fling.distance - 2857.1f) < 1f, "distance was ${fling.distance}")
        assertEquals(1530, fling.durationMillis)

        val slow = numberPickerFling(1200f, 3.5f)
        assertTrue(abs(slow.distance - 106.0f) < 1f, "distance was ${slow.distance}")
        assertEquals(378, slow.durationMillis)
    }

    @Test
    fun longFlingsRoundDownToWholeRows() {
        assertEquals(18f, numberPickerQuantisedRows(2857f, 157.5f))
        // Shorter than one row: the fraction is kept for the snap to resolve.
        assertEquals(0.5f, numberPickerQuantisedRows(50f, 100f))
    }

    @Test
    fun valueChangesAfterNinetyFivePercentOfARow() {
        assertEquals(3, numberPickerHysteresisIndex(3, 3.9f))
        assertEquals(4, numberPickerHysteresisIndex(3, 3.96f))
        // Going back needs another 0.95 rows, so small jitter does not flip the value.
        assertEquals(4, numberPickerHysteresisIndex(4, 3.1f))
        assertEquals(3, numberPickerHysteresisIndex(4, 3.0f))
        // A fast frame can cross several rows at once.
        assertEquals(10, numberPickerHysteresisIndex(3, 10f))
        assertEquals(-2, numberPickerHysteresisIndex(3, -2f))
    }
}
