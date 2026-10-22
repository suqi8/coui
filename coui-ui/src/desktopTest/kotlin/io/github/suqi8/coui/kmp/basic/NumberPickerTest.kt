// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.click
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class NumberPickerTest {

    @Test
    fun tappingTheSideRowsStepsByOne() = runComposeUiTest {
        var value by mutableIntStateOf(5)
        setContent {
            COUITheme {
                NumberPicker(
                    value = value,
                    onValueChange = { value = it },
                    modifier = Modifier.width(80.dp).testTag("picker"),
                    range = 0..10,
                )
            }
        }
        // Rows are 45dp: the bottom row steps forward, the top row steps back.
        onNodeWithTag("picker").performTouchInput { click(Offset(centerX, bottom - 10f)) }
        waitForIdle()
        assertEquals(6, value)

        onNodeWithTag("picker").performTouchInput { click(Offset(centerX, top + 10f)) }
        waitForIdle()
        assertEquals(5, value)

        // Tapping the selected row does nothing.
        onNodeWithTag("picker").performTouchInput { click(center) }
        waitForIdle()
        assertEquals(5, value)
    }

    @Test
    fun draggingReportsEveryValueOnTheWay() = runComposeUiTest {
        val seen = mutableListOf<Int>()
        setContent {
            COUITheme {
                var value by remember { mutableIntStateOf(0) }
                NumberPicker(
                    value = value,
                    onValueChange = {
                        seen += it
                        value = it
                    },
                    modifier = Modifier.width(80.dp).testTag("picker"),
                    range = 0..10,
                )
            }
        }
        // Drag up by three rows slowly so no fling starts.
        onNodeWithTag("picker").performTouchInput {
            down(center)
            val row = 45.dp.toPx()
            repeat(60) { moveBy(Offset(0f, -row * 3f / 60f), delayMillis = 50) }
            up()
        }
        waitForIdle()
        assertEquals(listOf(1, 2, 3), seen)
    }

    @Test
    fun nonWrappingWheelStopsAtTheEnds() = runComposeUiTest {
        var value by mutableIntStateOf(9)
        setContent {
            COUITheme {
                NumberPicker(
                    value = value,
                    onValueChange = { value = it },
                    modifier = Modifier.width(80.dp).testTag("picker"),
                    range = 0..10,
                )
            }
        }
        onNodeWithTag("picker").performTouchInput { swipeUp(durationMillis = 100) }
        waitForIdle()
        assertTrue(value == 10, "value was $value")
    }
}
