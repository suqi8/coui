// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.ui.Alignment
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
    fun flingCountsWholeRowsFromTheReportedValue() {
        // Dragged 0.6 rows up, then flung up two rows: COUI counts from the reported row, not from
        // the finger, so the part of a row already dragged is dropped.
        assertEquals(12f, numberPickerFlingTarget(reported = 10, position = 10.6f, lastMoveRows = -0.06f, velocity = -3000f, rows = 2f))
        // The same downwards.
        assertEquals(8f, numberPickerFlingTarget(reported = 10, position = 9.4f, lastMoveRows = 0.06f, velocity = 3000f, rows = 2f))
        // A flick shorter than a row keeps its fraction; the snap afterwards picks the row.
        assertEquals(10.7f, numberPickerFlingTarget(reported = 10, position = 10.6f, lastMoveRows = -0.06f, velocity = -3000f, rows = 0.7f), 1e-4f)
        // A downward last move that carries the offset past a whole row adds that row.
        assertEquals(7f, numberPickerFlingTarget(reported = 10, position = 9.1f, lastMoveRows = 0.2f, velocity = 3000f, rows = 2f))
    }

    @Test
    fun outerWheelsDrawOnTheirInnerSide() {
        assertEquals(Alignment.End, numberPickerColumnAlignment(index = 0, count = 3))
        assertEquals(Alignment.CenterHorizontally, numberPickerColumnAlignment(index = 1, count = 3))
        assertEquals(Alignment.Start, numberPickerColumnAlignment(index = 2, count = 3))
        assertEquals(Alignment.CenterHorizontally, numberPickerColumnAlignment(index = 0, count = 1))
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

    @Test
    fun hapticIntensityFollowsTheCrispRichTapRange() {
        // VibrateUtils crisp level: intensity 50 at rest, 100 at the max fling velocity.
        assertEquals(0.5f, numberPickerHapticIntensity(0f, 5000f))
        assertEquals(0.75f, numberPickerHapticIntensity(2500f, 5000f))
        assertEquals(1f, numberPickerHapticIntensity(9000f, 5000f))
    }

    @Test
    fun epochDaysRoundTrip() {
        assertEquals(0, epochDayOf(DateValue(1970, 1, 1)))
        assertEquals(DateValue(2026, 10, 23), dateOfEpochDay(epochDayOf(DateValue(2026, 10, 23))))
        assertEquals(DateValue(2024, 2, 29), dateOfEpochDay(epochDayOf(DateValue(2024, 3, 1)) - 1))
        assertEquals(DateValue(1900, 1, 1), dateOfEpochDay(epochDayOf(DateValue(1900, 1, 1))))
        // 2026-10-23 is a Friday; 1970-01-01 was a Thursday.
        assertEquals(5, DateValue(2026, 10, 23).dayOfWeek)
        assertEquals(4, DateValue(1970, 1, 1).dayOfWeek)
    }

    @Test
    fun dateFieldOrderFollowsThePattern() {
        assertEquals(DateFieldOrder.MonthDayYear, DateFieldOrder.fromPattern("MMMM dd, yyyy"))
        assertEquals(DateFieldOrder.YearMonthDay, DateFieldOrder.fromPattern("yyyy年MMMMdd日"))
        assertEquals(DateFieldOrder.DayMonthYear, DateFieldOrder.fromPattern("dd. MMMM yyyy"))
        // Quoted literals are skipped.
        assertEquals(DateFieldOrder.DayMonthYear, DateFieldOrder.fromPattern("d 'de' MMMM 'de' y"))
        assertEquals(DateFieldOrder.MonthDayYear, DateFieldOrder.fromPattern("Mdy"))
        assertEquals(DateFieldOrder.YearMonthDay, DateFieldOrder.fromPattern(""))
    }
}
