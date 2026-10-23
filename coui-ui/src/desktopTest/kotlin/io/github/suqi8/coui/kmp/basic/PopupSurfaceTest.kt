// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import io.github.suqi8.coui.kmp.effect.LocalPopupMaterialBlur
import io.github.suqi8.coui.kmp.effect.MaterialBlurProvider
import io.github.suqi8.coui.kmp.effect.MaterialBlurSpec
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class PopupSurfaceTest {

    private val opaque = Color(0xFFFAFAFA)

    @Test
    fun aProviderBlursThePopupByDefault() = runComposeUiTest {
        var requested: MaterialBlurSpec? = null
        var surface: PopupSurface? = null
        val provider = MaterialBlurProvider { spec, _ ->
            requested = spec
            Modifier
        }
        setContent {
            COUITheme {
                CompositionLocalProvider(LocalPopupMaterialBlur provides provider) {
                    surface = rememberPopupSurface(true, opaque, materialEffect = true, backgroundModifier = Modifier)
                }
            }
        }
        waitForIdle()
        assertEquals(ListPopupDefaults.materialBlurSpec(isDark = false), requested)
        assertEquals(160f, requested?.blurRadiusPx)
        assertEquals(Color.Transparent, surface?.color)
        assertTrue(surface?.materialEffect == true)
    }

    @Test
    fun optingOutOrMissingSupportKeepsTheOpaqueSurface() = runComposeUiTest {
        var optedOut: PopupSurface? = null
        var unsupported: PopupSurface? = null
        var noProvider: PopupSurface? = null
        setContent {
            COUITheme {
                CompositionLocalProvider(LocalPopupMaterialBlur provides MaterialBlurProvider { _, _ -> Modifier }) {
                    optedOut = rememberPopupSurface(false, opaque, materialEffect = true, backgroundModifier = Modifier)
                }
                CompositionLocalProvider(LocalPopupMaterialBlur provides MaterialBlurProvider { _, _ -> null }) {
                    unsupported = rememberPopupSurface(true, opaque, materialEffect = true, backgroundModifier = Modifier)
                }
                noProvider = rememberPopupSurface(true, opaque, materialEffect = true, backgroundModifier = Modifier)
            }
        }
        waitForIdle()
        for (surface in listOf(optedOut, unsupported, noProvider)) {
            assertEquals(opaque, surface?.color)
            assertFalse(surface?.materialEffect == true, "no material effects on an opaque surface")
        }
    }

    @Test
    fun aCustomTranslucentBlurKeepsItsMaterialEffects() = runComposeUiTest {
        var surface: PopupSurface? = null
        setContent {
            COUITheme {
                surface = rememberPopupSurface(true, Color.Transparent, materialEffect = true, backgroundModifier = Modifier)
            }
        }
        waitForIdle()
        assertTrue(surface?.materialEffect == true)
    }
}
