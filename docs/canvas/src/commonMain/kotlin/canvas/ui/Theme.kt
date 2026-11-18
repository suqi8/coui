// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package canvas.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import canvas.model.DocTheme
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.ColorSchemeMode
import io.github.suqi8.coui.kmp.theme.ThemeController

/** Key colours offered in the theme panel; null is the stock COUI blue. */
val KeyColors: List<String?> = listOf(null, "#2F74FF", "#00A86B", "#FF6A00", "#E5376B", "#7B61FF", "#00A3B4", "#8A6A4F")

/** `#RRGGBB` (or `RRGGBB`) to a colour, or null when it does not parse. */
fun parseHex(hex: String?): Color? {
    val h = hex?.trim()?.removePrefix("#") ?: return null
    if (h.length != 6) return null
    val v = h.toLongOrNull(16) ?: return null
    return Color(0xFF000000 or v)
}

/** The COUI theme a design asks for: light or dark, with its key colour. */
@Composable
fun DesignTheme(theme: DocTheme, content: @Composable () -> Unit) {
    val key = parseHex(theme.keyColor)
    val controller = remember(theme.dark, key) {
        ThemeController(colorSchemeMode = if (theme.dark) ColorSchemeMode.Dark else ColorSchemeMode.Light, keyColor = key)
    }
    COUITheme(controller = controller, content = content)
}
