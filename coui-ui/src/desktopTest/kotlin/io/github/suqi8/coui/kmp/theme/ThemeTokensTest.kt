// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ThemeTokensTest {

    @Test
    fun schemesCarryTheDarkFlag() {
        assertFalse(lightColorScheme().isDark)
        assertTrue(darkColorScheme().isDark)
        assertTrue(darkColorScheme().copy().isDark)
        assertTrue(colorsFromSeed(Color(0xFF0080FF), ThemeColorSpec.Spec2021, ThemePaletteStyle.TonalSpot, dark = true).isDark)
    }

    @Test
    fun singleColorRuleMatchesCouiOverlay() {
        // COUIOverlay.Theme.Single.First: couiSingleFirst* (values/colors.xml 551-555, values-night 167-168).
        val derived = COUIThemeColor.fromColor(Color(0xFFFF6436))
        assertEquals(COUIThemeColors.SingleFirst.light.argb(), derived.light.argb())
        assertEquals(COUIThemeColors.SingleFirst.dark.argb(), derived.dark.argb())
    }

    @Test
    fun themeColorMovesEveryDependentSlot() {
        val green = lightColorScheme(COUIThemeColors.Green)
        assertEquals(Color(0xFF00BD13), green.primary)
        assertEquals(Color(0x4D00BD13), green.disable)
        assertEquals(Color(0x2600BD13), green.secondary)
        assertEquals(Color(0xFF00990F), green.primaryText)
        assertEquals(Color(0xFF00990F), green.primaryTextOnPopup)
        assertEquals(Color(0x6600990F), green.focusOutline)
        val dark = darkColorScheme(COUIThemeColors.Green)
        assertEquals(Color(0xFF24B232), dark.primary)
        assertEquals(Color(0x3324B232), dark.focus)
        // The neutrals stay.
        assertEquals(darkColorScheme().backgroundWithCard, dark.backgroundWithCard)
    }

    @Test
    fun blueFamilyIsTheDefaultScheme() {
        val light = lightColorScheme()
        val themed = light.withThemeColor(COUIThemeColors.Blue)
        assertEquals(light.primary, themed.primary)
        assertEquals(light.focusOutline, themed.focusOutline)
        assertEquals(light.secondary, themed.secondary)
        val dark = darkColorScheme()
        val darkThemed = dark.withThemeColor(COUIThemeColors.Blue)
        assertEquals(dark.primaryText, darkThemed.primaryText)
        assertEquals(dark.focusOutline, darkThemed.focusOutline)
    }

    @Test
    fun monetKeepsTranslucentLabels() {
        val colors = colorsFromSeed(Color(0xFF6750A4), ThemeColorSpec.Spec2021, ThemePaletteStyle.TonalSpot, dark = false)
        assertEquals(0x42, (colors.disabledNeutral.alpha * 255f + 0.5f).toInt())
        assertEquals(0x8A, (colors.labelSecondary.alpha * 255f + 0.5f).toInt())
        assertEquals(0x26, (colors.secondary.alpha * 255f + 0.5f).toInt())
        assertEquals(0x4D, (colors.disable.alpha * 255f + 0.5f).toInt())
    }

    @Test
    fun fontScaleCapsMatchCouiChangeTextUtil() {
        assertEquals(1.3f, COUIFontScale.suitableFontScale(1.3f, COUIFontScale.G1))
        assertEquals(1.0f, COUIFontScale.suitableFontScale(1.1f, COUIFontScale.G2))
        assertEquals(1.15f, COUIFontScale.suitableFontScale(1.6f, COUIFontScale.G2))
        assertEquals(1.35f, COUIFontScale.suitableFontScale(1.7f, COUIFontScale.G3))
        assertEquals(1.35f, COUIFontScale.suitableFontScale(1.6f, COUIFontScale.G4))
        assertEquals(1.2f, COUIFontScale.suitableFontScale(1.2f, COUIFontScale.G5))
    }

    private fun COUIThemeColorTokens.argb(): List<Int> = listOf(
        primary, containerTheme, containerThemeHalftone, secondary, disable, focus, focusOutline,
        labelTheme, primaryText, primaryTextOnPopup, textColorHighlight,
    ).map { it.toArgb() }
}
