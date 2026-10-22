// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.effect.LocalMaterialBackdrop
import io.github.suqi8.coui.kmp.effect.MaterialBackdropProvider
import io.github.suqi8.coui.kmp.effect.MaterialBackdropSpec
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class ButtonBackdropTest {

    @Test
    fun secondaryButtonDrawsTheBackdropInsteadOfItsFill() = runComposeUiTest {
        val specs = mutableListOf<MaterialBackdropSpec>()
        // A stand-in backdrop: a flat marker colour where the real provider would blur.
        val provider = MaterialBackdropProvider { spec, _ ->
            specs += spec
            Modifier.background(Marker)
        }
        setContent {
            COUITheme {
                CompositionLocalProvider(LocalMaterialBackdrop provides provider) {
                    Box(Modifier.padding(32.dp)) {
                        TextButton(text = "", onClick = {})
                    }
                }
            }
        }
        val spec = specs.last()
        assertEquals(25f, spec.blurRadiusPx)
        assertEquals(Color(0xCCFFFFFF), spec.layer1)
        assertEquals(BlendMode.SrcOver, spec.mode1)

        // The button centre shows the backdrop (under the faint material inner shadow), not the
        // opaque #EBEBEB fill.
        val pixels = onRoot().captureToImage().toPixelMap()
        val x = ((32 + 66) * density.density).toInt()
        val y = ((32 + 22) * density.density).toInt()
        val centre = pixels[x, y]
        assertTrue(centre.green < 0.1f && centre.red > 0.9f, "the centre must show the backdrop, got $centre")
    }

    @Test
    fun primaryButtonTintsTheBackdropWithItsFill() = runComposeUiTest {
        val specs = mutableListOf<MaterialBackdropSpec>()
        val provider = MaterialBackdropProvider { spec, _ ->
            specs += spec
            Modifier
        }
        var primary = Color.Unspecified
        setContent {
            COUITheme {
                primary = COUITheme.colorScheme.primary
                CompositionLocalProvider(LocalMaterialBackdrop provides provider) {
                    TextButton(text = "", onClick = {}, colors = ButtonDefaults.textButtonColorsPrimary())
                    TextButton(
                        text = "",
                        onClick = {},
                        enabled = false,
                        colors = ButtonDefaults.textButtonColorsPrimary(),
                    )
                }
            }
        }
        val enabled = specs.first { it.layer1.alpha > 0.5f }
        assertEquals(10f, enabled.blurRadiusPx)
        assertEquals(primary.copy(alpha = 229f / 255f), enabled.layer1)
        val disabled = specs.first { it.layer1.alpha < 0.5f }
        assertEquals(primary.copy(alpha = 77f / 255f), disabled.layer1)
        assertEquals(BlendMode.Overlay, disabled.mode2)
    }

    @Test
    fun optedOutButtonsKeepTheirFill() = runComposeUiTest {
        var calls = 0
        val provider = MaterialBackdropProvider { _, _ ->
            calls++
            Modifier
        }
        setContent {
            COUITheme {
                CompositionLocalProvider(LocalMaterialBackdrop provides provider) {
                    TextButton(text = "", onClick = {}, materialBackdrop = false)
                    TextButton(text = "", onClick = {}, material = ButtonMaterial.None)
                    // Borderless buttons have no buttonType and never blur.
                    TextButton(text = "", onClick = {}, colors = ButtonDefaults.textButtonColorsBorderless())
                }
            }
        }
        waitForIdle()
        assertTrue(calls == 0, "no button may request the backdrop, got $calls calls")
    }

    private companion object {
        val Marker = Color(0xFFFF00FF)
    }
}
