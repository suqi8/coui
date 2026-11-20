// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.sp
import io.github.suqi8.coui.kmp.theme.COUIFontScale
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class ButtonLabelStyleTest {

    @Test
    fun labelsFollowTheCouiFontScaleSteps() = runComposeUiTest {
        val base = TextStyle(fontSize = 16.sp, lineHeight = 20.sp)
        var capped = base
        var below = base
        var descLarge = base
        var descSmall = base
        setContent {
            // COUIChangeTextUtil.adaptFontSize(G4): a 1.6 font scale renders at 1.35.
            CompositionLocalProvider(LocalDensity provides Density(1f, fontScale = 1.6f)) {
                capped = buttonLabelStyle(base)
                descLarge = buttonLabelStyle(base, COUIFontScale.G2)
            }
            // Below the cap the style is untouched.
            CompositionLocalProvider(LocalDensity provides Density(1f, fontScale = 1.15f)) {
                below = buttonLabelStyle(base)
            }
            // G2 renders at 1.0 below a 1.15 font scale, so a small font scale grows back.
            CompositionLocalProvider(LocalDensity provides Density(1f, fontScale = 0.9f)) {
                descSmall = buttonLabelStyle(base, COUIFontScale.G2)
            }
        }
        waitForIdle()
        assertEquals(16f * 1.35f / 1.6f, capped.fontSize.value, 0.001f)
        assertEquals(20f * 1.35f / 1.6f, capped.lineHeight.value, 0.001f)
        assertEquals(base, below)
        assertEquals(16f * 1.15f / 1.6f, descLarge.fontSize.value, 0.001f)
        assertEquals(16f / 0.9f, descSmall.fontSize.value, 0.001f)
    }
}
