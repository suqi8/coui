// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.darkColorScheme
import io.github.suqi8.coui.kmp.theme.lightColorScheme
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class FloatingActionButtonTest {

    @Test
    fun disabledFillMatchesCoui() = runComposeUiTest {
        var light = Color.Unspecified
        var dark = Color.Unspecified
        setContent {
            COUITheme(colors = lightColorScheme()) { light = FloatingActionButtonDefaults.disabledContainerColor() }
            COUITheme(colors = darkColorScheme()) { dark = FloatingActionButtonDefaults.disabledContainerColor() }
        }
        waitForIdle()
        assertClose(0xFFB2D9FF.toInt(), light.toArgb())
        assertClose(0xFF3E6083.toInt(), dark.toArgb())
    }

    @Test
    fun shadowGeometryFollowsTheOplusLight() {
        // 60dp at 10000dp under a 2000dp light: a ~12dp penumbra.
        val penumbra = FloatingActionButtonDefaults.shadowPenumbra(60.dp).value
        assertTrue(abs(penumbra - 12.07f) < 0.01f, "penumbra $penumbra")
        // A button centred 700dp down, lit from 800dp above the window: ~9dp down.
        val offset = fabSpotShadowOffsetY(centerY = 700f, elevation = 60f, lightY = -800f, lightZ = 10000f)
        assertTrue(abs(offset - 9.054f) < 0.01f, "offset $offset")
        assertEquals(0f, fabSpotShadowOffsetY(700f, 0f, -800f, 10000f))
    }

    @Test
    fun backdropPresetsMatchCoui() {
        val blue = Color(0xFF0080FF)
        val light = fabBackdropSpec(isDark = false, enabled = true, fill = blue, disabledFill = Color.Gray, themeFill = true)
        assertEquals(10f, light.blurRadiusPx)
        assertEquals(229, (light.layer1.alpha * 255f + 0.5f).toInt())
        assertEquals(Color(0x1AFFFFFF), light.layer2)
        assertEquals(BlendMode.Overlay, light.mode2)
        val dark = fabBackdropSpec(isDark = true, enabled = true, fill = blue, disabledFill = Color.Gray, themeFill = true)
        assertEquals(230, (dark.layer1.alpha * 255f + 0.5f).toInt())
        assertEquals(Color(0x19FFFFFF), dark.layer2)
        val disabled = fabBackdropSpec(isDark = false, enabled = false, fill = blue, disabledFill = Color.Gray, themeFill = true)
        assertEquals(77, (disabled.layer1.alpha * 255f + 0.5f).toInt())
        val custom = fabBackdropSpec(isDark = false, enabled = false, fill = Color.Red, disabledFill = Color.Gray, themeFill = false)
        assertEquals(Color.Gray, custom.layer1)
    }

    @Test
    fun strokePresetsMatchCoui() {
        assertEquals(0.25f, fabStrokeParams(isDark = false, enabled = true).edgeAlpha)
        assertEquals(2.5f, fabStrokeParams(isDark = true, enabled = true).edgeWidth)
        assertEquals(0.075f, fabStrokeParams(isDark = false, enabled = false).edgeAlpha)
        assertEquals(0.12f, fabStrokeParams(isDark = true, enabled = false).edgeAlpha)
        assertEquals(2f, fabStrokeParams(isDark = true, enabled = false).shadowFadeScale)
    }

    @Test
    fun disabledButtonIgnoresClicks() = runComposeUiTest {
        var clicks by mutableIntStateOf(0)
        var enabled by mutableIntStateOf(1)
        setContent {
            COUITheme {
                Box(Modifier.padding(80.dp)) {
                    FloatingActionButton(
                        onClick = { clicks++ },
                        modifier = Modifier.testTag("fab"),
                        enabled = enabled == 1,
                    ) {}
                }
            }
        }
        onNodeWithTag("fab").performClick()
        waitForIdle()
        assertEquals(1, clicks)
        enabled = 0
        waitForIdle()
        onNodeWithTag("fab").performClick()
        waitForIdle()
        assertEquals(1, clicks)
    }

    private fun assertClose(expected: Int, actual: Int) {
        for (shift in intArrayOf(16, 8, 0)) {
            val e = (expected shr shift) and 0xFF
            val a = (actual shr shift) and 0xFF
            assertTrue(abs(e - a) <= 1, "expected #${expected.toUInt().toString(16)}, got #${actual.toUInt().toString(16)}")
        }
    }
}
