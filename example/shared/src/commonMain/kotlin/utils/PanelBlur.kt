// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package utils

import LocalAppState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import io.github.suqi8.coui.kmp.blur.LayerBackdrop
import io.github.suqi8.coui.kmp.blur.MaterialBlurDefaults
import io.github.suqi8.coui.kmp.blur.MaterialBlurStyle
import io.github.suqi8.coui.kmp.blur.couiMaterialBlur
import io.github.suqi8.coui.kmp.blur.isRuntimeShaderSupported
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * The backdrop recording the current page content, provided by the app scaffold so overlays
 * (dialogs, bottom sheets) can blur what lies behind them.
 */
val LocalPageBackdrop = staticCompositionLocalOf<LayerBackdrop?> { null }

/**
 * The COUI 17 blurred panel material of a dialog or bottom sheet.
 *
 * @param backgroundModifier The material blur for the panel's `backgroundModifier` slot.
 * @param backgroundColor The panel fill; transparent, since the blur paints the panel.
 */
@Immutable
class PanelBlur(
    val backgroundModifier: Modifier,
    val backgroundColor: Color,
)

/**
 * Returns the COUI 17 material blur for a panel, or null when the blur setting is off, no page
 * backdrop is provided or runtime shaders are unavailable (COUI then keeps the opaque surface).
 *
 * @param style Picks the [MaterialBlurStyle] for the current scheme, e.g. [MaterialBlurDefaults.dialog].
 */
@Composable
fun panelBlur(style: (isDark: Boolean) -> MaterialBlurStyle): PanelBlur? {
    val appState = LocalAppState.current
    val backdrop = LocalPageBackdrop.current
    if (!appState.enableBlur || backdrop == null || !isRuntimeShaderSupported()) return null
    val isDark = COUITheme.colorScheme.background.luminance() < 0.5f
    return PanelBlur(
        backgroundModifier = Modifier.couiMaterialBlur(backdrop = backdrop, style = style(isDark)),
        backgroundColor = Color.Transparent,
    )
}
