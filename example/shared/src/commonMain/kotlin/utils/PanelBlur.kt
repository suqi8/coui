// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import app.LocalAppSettings
import io.github.suqi8.coui.kmp.blur.Backdrop
import io.github.suqi8.coui.kmp.blur.LayerBackdrop
import io.github.suqi8.coui.kmp.blur.MaterialBlendMode
import io.github.suqi8.coui.kmp.blur.MaterialBlurDefaults
import io.github.suqi8.coui.kmp.blur.MaterialBlurStyle
import io.github.suqi8.coui.kmp.blur.ViewBlurStyle
import io.github.suqi8.coui.kmp.blur.couiMaterialBlur
import io.github.suqi8.coui.kmp.blur.couiViewBlur
import io.github.suqi8.coui.kmp.blur.isRuntimeShaderSupported
import io.github.suqi8.coui.kmp.effect.LocalMaterialBackdrop
import io.github.suqi8.coui.kmp.effect.MaterialBackdropProvider
import io.github.suqi8.coui.kmp.effect.MaterialBackdropSpec
import io.github.suqi8.coui.kmp.effect.MaterialBlurBlendMode
import io.github.suqi8.coui.kmp.effect.MaterialBlurProvider
import io.github.suqi8.coui.kmp.effect.MaterialBlurSpec
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
    val backdrop = LocalPageBackdrop.current
    if (!LocalAppSettings.current.enableMaterialBlur || backdrop == null || !isRuntimeShaderSupported()) return null
    val isDark = COUITheme.colorScheme.isDark
    return PanelBlur(
        backgroundModifier = Modifier.couiMaterialBlur(backdrop = backdrop, style = style(isDark)),
        backgroundColor = Color.Transparent,
    )
}

/**
 * Bridges a coui-blur [Backdrop] to coui-ui's [LocalMaterialBackdrop], so buttons drawn over it get
 * the COUI 17 view backdrop blur ([couiViewBlur]). Returns null when the blur setting is off, no
 * backdrop is given or runtime shaders are unavailable; the buttons then keep their opaque fill,
 * COUI's own fallback.
 *
 * The backdrop must record the content behind the buttons, not the buttons themselves: provide the
 * result around siblings of the recorded layer. The page backdrop ([LocalPageBackdrop]) records
 * the whole page, so page content cannot use it; overlays reset the local to null on their own.
 *
 * @param backdrop The backdrop recording the content behind the buttons.
 */
@Composable
fun rememberMaterialBackdropProvider(backdrop: Backdrop?): MaterialBackdropProvider? {
    val active = LocalAppSettings.current.enableMaterialBlur && backdrop != null && isRuntimeShaderSupported()
    return remember(backdrop, active) {
        if (active && backdrop != null) {
            MaterialBackdropProvider { spec, shape ->
                Modifier.couiViewBlur(backdrop = backdrop, style = spec.toViewBlurStyle(), shape = shape)
            }
        } else {
            null
        }
    }
}

private fun MaterialBackdropSpec.toViewBlurStyle(): ViewBlurStyle = ViewBlurStyle(
    blurRadiusPx = blurRadiusPx,
    layer1 = layer1,
    mode1 = mode1,
    layer2 = layer2,
    mode2 = mode2,
)

/**
 * Bridges a coui-blur [Backdrop] to coui-ui's popup material blur
 * ([io.github.suqi8.coui.kmp.effect.LocalPopupMaterialBlur]), so every list popup, cascading menu
 * and dropdown called under it blurs the content behind it with [couiMaterialBlur], as ColorOS 17
 * menus do. Returns null when the blur setting is off, no backdrop is given or runtime shaders are
 * unavailable; the popups then keep their opaque surface.
 *
 * The popups render in the root scaffold's popup host (or their own window), outside the page
 * recording, so the page backdrop ([LocalPageBackdrop]) is the right one to hand them.
 *
 * @param backdrop The backdrop recording the page the menus open over.
 */
@Composable
fun rememberPopupMaterialBlur(backdrop: Backdrop?): MaterialBlurProvider? {
    val active = LocalAppSettings.current.enableMaterialBlur && backdrop != null && isRuntimeShaderSupported()
    return remember(backdrop, active) {
        if (active && backdrop != null) {
            MaterialBlurProvider { spec, shape ->
                Modifier.couiMaterialBlur(backdrop = backdrop, style = spec.toMaterialBlurStyle(), shape = shape)
            }
        } else {
            null
        }
    }
}

private fun MaterialBlurSpec.toMaterialBlurStyle(): MaterialBlurStyle = MaterialBlurStyle(
    blurRadiusPx = blurRadiusPx,
    mixColor = mixColor,
    blendColor = blendColor,
    blendMode = when (blendMode) {
        MaterialBlurBlendMode.Overlay -> MaterialBlendMode.Overlay
        MaterialBlurBlendMode.ColorDodge -> MaterialBlendMode.ColorDodge
        MaterialBlurBlendMode.GlowOverlay -> MaterialBlendMode.GlowOverlay
        MaterialBlurBlendMode.GlowColorDodge -> MaterialBlendMode.GlowColorDodge
    },
)
