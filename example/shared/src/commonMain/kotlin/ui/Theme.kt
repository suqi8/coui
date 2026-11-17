// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.ColorSchemeMode
import io.github.suqi8.coui.kmp.theme.ThemeColorSpec
import io.github.suqi8.coui.kmp.theme.ThemeController
import io.github.suqi8.coui.kmp.theme.ThemePaletteStyle

/**
 * The colour modes the example app offers, in the order the settings page lists them.
 *
 * @property isMonet Whether the mode derives its scheme from a key colour.
 */
enum class ColorMode(val isMonet: Boolean) {
    System(false),
    Light(false),
    Dark(false),
    MonetSystem(true),
    MonetLight(true),
    MonetDark(true),
    ;

    /**
     * Whether this mode renders dark, given whether the system is dark.
     */
    fun isDark(systemDark: Boolean): Boolean = when (this) {
        Light, MonetLight -> false
        Dark, MonetDark -> true
        System, MonetSystem -> systemDark
    }
}

val LocalColorMode = compositionLocalOf { ColorMode.System }

@Composable
fun AppTheme(
    colorMode: ColorMode = ColorMode.System,
    keyColor: Color? = null,
    paletteStyle: Int = 0,
    colorSpec: Int = 0,
    content: @Composable () -> Unit,
) {
    val spec = ThemeColorSpec.entries.getOrNull(colorSpec) ?: ThemeColorSpec.Spec2021
    val style = ThemePaletteStyle.entries.getOrNull(paletteStyle) ?: ThemePaletteStyle.Content
    val controller = remember(colorMode, keyColor, spec, style) {
        when (colorMode) {
            ColorMode.System -> ThemeController(ColorSchemeMode.System)
            ColorMode.Light -> ThemeController(ColorSchemeMode.Light)
            ColorMode.Dark -> ThemeController(ColorSchemeMode.Dark)
            ColorMode.MonetSystem -> ThemeController(ColorSchemeMode.MonetSystem, keyColor = keyColor, colorSpec = spec, paletteStyle = style)
            ColorMode.MonetLight -> ThemeController(ColorSchemeMode.MonetLight, keyColor = keyColor, colorSpec = spec, paletteStyle = style)
            ColorMode.MonetDark -> ThemeController(ColorSchemeMode.MonetDark, keyColor = keyColor, colorSpec = spec, paletteStyle = style)
        }
    }
    CompositionLocalProvider(
        LocalColorMode provides colorMode,
    ) {
        COUITheme(
            controller = controller,
            content = content,
        )
    }
}

@Composable
fun isInDarkTheme(): Boolean = LocalColorMode.current.isDark(isSystemInDarkTheme())

/**
 * The key colours offered for the Monet modes. Index 0 of the settings list is "default" (no key
 * colour), so entry `i` here is settings index `i + 1`.
 */
val KeyColors: List<Color> = listOf(
    Color(0xFF3482FF),
    Color(0xFF36D167),
    Color(0xFF7C4DFF),
    Color(0xFFFFB21D),
    Color(0xFFFF5722),
    Color(0xFFE91E63),
    Color(0xFF00BCD4),
)

fun keyColorFor(index: Int): Color? = if (index <= 0) null else KeyColors.getOrNull(index - 1)
