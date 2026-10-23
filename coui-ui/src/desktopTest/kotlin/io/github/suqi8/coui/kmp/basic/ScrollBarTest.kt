// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.interfaces.ExperimentalScrollBarApi
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class, ExperimentalScrollBarApi::class)
class ScrollBarTest {

    private fun ComposeUiTest.setUp(state: ScrollState) {
        setContent {
            COUITheme {
                Box(Modifier.size(200.dp, 300.dp).background(Color.White)) {
                    Column(Modifier.fillMaxSize().verticalScroll(state)) {
                        Spacer(Modifier.height(2000.dp))
                    }
                    VerticalScrollBar(
                        adapter = rememberScrollBarAdapter(state),
                        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    )
                }
            }
        }
        mainClock.autoAdvance = false
    }

    /** The pixel at ([xDp], [yDp]) of the 200 x 300dp box. */
    private fun ComposeUiTest.pixel(xDp: Float, yDp: Float): Color {
        val map = onRoot().captureToImage().toPixelMap()
        return map[(xDp * density.density).toInt(), (yDp * density.density).toInt()]
    }

    // The 3dp thumb sits 5.5dp from the end: its centre line is 200 - 5.5 - 1.5 = 193dp.
    private fun ComposeUiTest.thumbPixel(yDp: Float = 20f) = pixel(193f, yDp)

    @Test
    fun thumbFlashesWhenItFirstAppears() = runComposeUiTest {
        setUp(ScrollState(0))
        mainClock.advanceTimeBy(1000)
        val shown = thumbPixel()
        mainClock.advanceTimeBy(600)
        val hidden = thumbPixel()
        assertTrue(shown != hidden, "the thumb must show for 1200ms and fade over 250ms: $shown vs $hidden")
        assertEquals(Color.White, hidden)
    }

    @Test
    fun thumbHidesTwoSecondsAfterTheLastScroll() = runComposeUiTest {
        val state = ScrollState(0)
        setUp(state)
        mainClock.advanceTimeBy(5000)
        val rest = thumbPixel()

        runOnUiThread { state.dispatchRawDelta(10f) }
        mainClock.advanceTimeBy(1900)
        val beforeTimeout = thumbPixel()
        mainClock.advanceTimeBy(400)
        val afterFade = thumbPixel()

        assertTrue(beforeTimeout != rest, "the thumb must still show 1900ms after the scroll")
        assertEquals(rest, afterFade, "the thumb must be gone 2000 + 250ms after the scroll")
    }

    @Test
    fun thumbIsInsetFromItsRect() = runComposeUiTest {
        setUp(ScrollState(0))
        mainClock.advanceTimeBy(500)
        // The 48dp rect at the top of the track draws the thumb from 8dp to 32dp.
        assertEquals(Color.White, thumbPixel(yDp = 4f), "8dp start margin")
        assertTrue(thumbPixel(yDp = 20f) != Color.White, "visible thumb")
        assertEquals(Color.White, thumbPixel(yDp = 40f), "16dp end margin")
    }
}
