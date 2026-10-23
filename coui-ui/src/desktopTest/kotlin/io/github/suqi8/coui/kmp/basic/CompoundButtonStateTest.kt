// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class CompoundButtonStateTest {

    /** The pixel at the centre of a 24dp control placed at 16dp, offset by ([dx], [dy]) dp. */
    private fun ComposeUiTest.sample(dx: Float = 0f, dy: Float = 0f): Color {
        val map = onRoot().captureToImage().toPixelMap()
        return map[((16 + 12 + dx) * density.density).toInt(), ((16 + 12 + dy) * density.density).toInt()]
    }

    private fun ComposeUiTest.host(content: @Composable () -> Unit) {
        setContent {
            COUITheme {
                Box(Modifier.background(Color.White).padding(16.dp)) { content() }
            }
        }
        mainClock.autoAdvance = false
    }

    /** Returns the sampled pixel two frames after [change] and once settled. */
    private fun ComposeUiTest.afterChange(dx: Float, change: () -> Unit): Pair<Color, Color> {
        mainClock.advanceTimeBy(1000)
        change()
        mainClock.advanceTimeByFrame()
        mainClock.advanceTimeByFrame()
        val early = sample(dx)
        mainClock.advanceTimeBy(1000)
        return early to sample(dx)
    }

    @Test
    fun disabledCheckboxJumpsToChecked() = runComposeUiTest {
        var state by mutableStateOf(ToggleableState.Off)
        host { Checkbox(state = state, onClick = null, enabled = false) }
        // A point on the checked fill, away from the checkmark.
        val (early, settled) = afterChange(dx = -7f) { state = ToggleableState.On }
        assertEquals(settled, early, "COUI has no disabled off -> on transition")
    }

    @Test
    fun enabledCheckboxStillAnimates() = runComposeUiTest {
        var state by mutableStateOf(ToggleableState.Off)
        host { Checkbox(state = state, onClick = null) }
        val (early, settled) = afterChange(dx = -7f) { state = ToggleableState.On }
        assertTrue(early != settled, "the enabled checkbox replays the AVD: $early vs $settled")
    }

    @Test
    fun disabledRadioButtonJumpsToSelected() = runComposeUiTest {
        var selected by mutableStateOf(false)
        host { RadioButton(selected = selected, onClick = null, enabled = false) }
        // Between the 6dp centre dot and the 10dp disc edge.
        val (early, settled) = afterChange(dx = 8f) { selected = true }
        assertEquals(settled, early, "coui_btn_radio.xml has no disabled transitions")
    }

    @Test
    fun focusedChipDrawsItsOutlineOutside() = runComposeUiTest {
        val focusRequester = FocusRequester()
        setContent {
            COUITheme {
                Box(Modifier.background(Color.White).padding(16.dp)) {
                    Chip(selected = false, onClick = {}, label = "", modifier = Modifier.focusRequester(focusRequester))
                }
            }
        }
        mainClock.autoAdvance = false
        mainClock.advanceTimeBy(500)
        // 1.5dp above the chip's top edge, in the middle of the 52dp chip.
        fun ringPixel(): Color {
            val map = onRoot().captureToImage().toPixelMap()
            return map[((16 + 26) * density.density).toInt(), ((16 - 1.5f) * density.density).toInt()]
        }
        val rest = ringPixel()
        runOnUiThread { focusRequester.requestFocus() }
        mainClock.advanceTimeBy(1000)
        val focused = ringPixel()
        assertEquals(Color.White, rest)
        assertTrue(focused != rest, "a focused chip must draw its 3dp focus outline: $focused")
    }
}
