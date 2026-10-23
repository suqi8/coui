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
}
