// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.width
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class ChipTest {

    @Test
    fun disablingSwitchesTheColorsAtOnce() = runComposeUiTest {
        var enabled by mutableStateOf(true)
        setContent {
            COUITheme {
                Box(Modifier.padding(16.dp)) {
                    Chip(selected = true, onClick = {}, label = "", enabled = enabled)
                }
            }
        }
        mainClock.autoAdvance = false
        // A point on the capsule fill, inside its 12dp start padding.
        val x = ((16 + 6) * density.density).toInt()
        val y = ((16 + 16) * density.density).toInt()
        val before = onRoot().captureToImage().toPixelMap()[x, y]

        enabled = false
        mainClock.advanceTimeByFrame()
        mainClock.advanceTimeByFrame()
        val firstFrames = onRoot().captureToImage().toPixelMap()[x, y]
        mainClock.advanceTimeBy(2000)
        val settled = onRoot().captureToImage().toPixelMap()[x, y]

        assertTrue(before != settled, "the disabled fill must differ from the enabled one")
        assertEquals(settled, firstFrames, "COUIChipDrawable assigns the disabled colors without animating")
    }

    @Test
    fun hidingTheCloseIconShrinksTheChipOnASpring() = runComposeUiTest {
        var closable by mutableStateOf(true)
        setContent {
            COUITheme {
                Box(Modifier.padding(16.dp)) {
                    SuggestionChip(
                        onClick = {},
                        // Long enough to stay wider than the 52dp minimum without the close icon.
                        label = "Search history",
                        onClose = if (closable) ({}) else null,
                        modifier = Modifier.testTag("chip"),
                    )
                }
            }
        }
        mainClock.autoAdvance = false
        mainClock.advanceTimeBy(500)
        fun width() = onNodeWithTag("chip").getUnclippedBoundsInRoot().width.value
        val shown = width()

        closable = false
        mainClock.advanceTimeBy(64)
        val early = width()
        mainClock.advanceTimeBy(1000)
        val settled = width()

        // COUIChipDrawable.CloseIconAnimation: the 8 + 12 + 8dp slot gives way to the 12dp end padding.
        assertEquals(16f, shown - settled, 1f)
        assertTrue(early < shown && early > settled, "the chip must shrink gradually: $shown -> $early -> $settled")
    }
}
