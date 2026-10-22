// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class IconButtonPressTest {

    @Test
    fun holdingTheButtonChangesItsMaterial() = runComposeUiTest {
        setContent {
            COUITheme {
                Box(Modifier.padding(32.dp)) {
                    IconButton(onClick = {}, modifier = Modifier.testTag("button")) {}
                }
            }
        }
        mainClock.autoAdvance = false
        val rest = onRoot().captureToImage().toPixelMap()

        onNodeWithTag("button").performTouchInput { down(center) }
        mainClock.advanceTimeBy(1000)
        val held = onRoot().captureToImage().toPixelMap()

        // The button's centre: 32dp padding + half of the 40dp button, at the test density.
        val c = ((32 + 20) * density.density).toInt()
        assertTrue(rest[c, c] != held[c, c], "pressing must change the button's material: ${rest[c, c]} vs ${held[c, c]}")
    }
}
