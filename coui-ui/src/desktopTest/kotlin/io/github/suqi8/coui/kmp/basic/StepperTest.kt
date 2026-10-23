// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class StepperTest {

    @Test
    fun stepClampsToTheRange() {
        assertEquals(10, stepperNext(9, 5, -999, 10))
        assertEquals(-999, stepperNext(-998, -5, -999, 9999))
        assertEquals(Int.MAX_VALUE, stepperNext(Int.MAX_VALUE - 1, 5, 0, Int.MAX_VALUE))
    }

    @Test
    fun indicatorReservesTheLengthOfTheMaximum() {
        assertEquals(4, stepperIndicatorChars(9999))
        assertEquals(2, stepperIndicatorChars(10))
        assertEquals(2, stepperIndicatorChars(-5))
    }

    @Test
    fun tapStepsOnceOnFingerUp() = runComposeUiTest {
        var value by mutableIntStateOf(0)
        setContent {
            COUITheme {
                Stepper(value = value, onValueChange = { value = it })
            }
        }
        mainClock.autoAdvance = false
        onNodeWithContentDescription("+").performTouchInput { down(center) }
        mainClock.advanceTimeBy(50)
        assertEquals(0, value, "a tap steps on finger up, not down")
        onNodeWithContentDescription("+").performTouchInput { up() }
        mainClock.advanceTimeBy(50)
        assertEquals(1, value)
    }

    @Test
    fun holdingRepeatsEvery100msUntilTheLimit() = runComposeUiTest {
        var value by mutableIntStateOf(0)
        var timeout = 0L
        setContent {
            COUITheme {
                timeout = LocalViewConfiguration.current.longPressTimeoutMillis
                Stepper(value = value, onValueChange = { value = it }, maxValue = 8)
            }
        }
        mainClock.autoAdvance = false
        onNodeWithContentDescription("+").performTouchInput { down(center) }
        mainClock.advanceTimeBy(timeout - 16)
        assertEquals(0, value, "nothing happens before the long-press timeout")
        mainClock.advanceTimeBy(32)
        assertEquals(1, value, "the long press steps at once")
        mainClock.advanceTimeBy(100)
        assertEquals(2, value)
        mainClock.advanceTimeBy(300)
        assertEquals(5, value)
        mainClock.advanceTimeBy(2000)
        assertEquals(8, value, "the repeat stops at the limit")
        onNodeWithContentDescription("+").performTouchInput { up() }
        mainClock.advanceTimeBy(500)
        assertEquals(8, value, "a long press does not also tap")
    }

    @Test
    fun widthDoesNotDependOnTheValue() = runComposeUiTest {
        var value by mutableIntStateOf(0)
        setContent {
            COUITheme {
                Box(Modifier.padding(8.dp)) {
                    Stepper(value = value, onValueChange = { value = it }, modifier = Modifier.testTag("stepper"))
                }
            }
        }
        val narrow = onNodeWithTag("stepper").getBoundsInRoot().right
        value = 9999
        waitForIdle()
        val wide = onNodeWithTag("stepper").getBoundsInRoot().right
        assertEquals(narrow, wide)
        assertTrue(narrow > 0.dp)
    }
}
