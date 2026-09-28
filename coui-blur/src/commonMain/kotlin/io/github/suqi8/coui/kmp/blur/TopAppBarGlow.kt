// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.blur

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * The "condensed light" (凝光) glow overlay for a COUI 17 top app bar.
 *
 * It composites, over the content scrolled beneath the bar, a progressive backdrop blur that is
 * strongest at the top edge and fades to pixel-sharp at the bottom, plus the two condensed-light
 * blend layers COUI applies to a top bar. The whole overlay is revealed by [alpha], which the
 * caller drives from the top-app-bar collapse fraction (COUI reveals it over a 30dp scroll).
 *
 * COUI 17 evidence (settings 17.0.0, `com.coui.appcompat.toolbar`):
 * - Progressive blur: `AppBarBlurParams` gradient blur, strong at the top and zero at the bottom.
 * - Blend layers (`COUIMaterialBlurEffect`, TYPE_TOP_BAR): light `#66333333` COLOR_DODGE +
 *   `#99F5F5F5` LUMINOSITY; dark `#CC262626` SRC_OVER + `#B37F7F7F` OVERLAY.
 *
 * @param backdrop The [Backdrop] providing the scrolled content to blur (e.g. `rememberLayerBackdrop`
 *   on the page content).
 * @param collapseFraction The top-app-bar collapse fraction in `[0, 1]`: 0 = expanded (overlay
 *   hidden), 1 = collapsed (overlay fully revealed). COUI maps scroll→alpha as min(1, scroll/30dp).
 * @param modifier The [Modifier] to be applied to the overlay.
 * @param isDark Whether the dark scheme is active, to pick the dark blend layers.
 * @param blurRadius The blur radius in dp at full strength (top edge). COUI Material
 *   `TYPE_DEFAULT_BLUR` = 20dp.
 * @param shape The shape of the overlay region.
 * @param content Optional foreground content drawn on top of the glow (usually none).
 */
@Composable
fun BoxScope.TopAppBarGlow(
    backdrop: Backdrop,
    collapseFraction: Float,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    blurRadius: Float = TopAppBarGlowDefaults.BlurRadius,
    shape: Shape = RectangleShape,
    content: @Composable () -> Unit = {},
) {
    val alpha = collapseFraction.coerceIn(0f, 1f)
    if (alpha <= 0f || !isRuntimeShaderSupported()) {
        return
    }
    val colors = remember(isDark) {
        if (isDark) {
            BlurColors(
                blendColors = listOf(
                    BlendColorEntry(Color(0xCC262626), BlurBlendMode.SrcOver),
                    BlendColorEntry(Color(0xB37F7F7F), BlurBlendMode.Overlay),
                ),
            )
        } else {
            BlurColors(
                blendColors = listOf(
                    BlendColorEntry(Color(0x66333333), BlurBlendMode.ColorDodge),
                    BlendColorEntry(Color(0x99F5F5F5), BlurBlendMode.Luminosity),
                ),
            )
        }
    }
    Box(
        modifier = modifier
            .matchParentSize()
            .alpha(alpha)
            .progressiveTextureBlur(
                backdrop = backdrop,
                shape = shape,
                blurRadius = blurRadius,
                gradient = ProgressiveBlur.Top,
                colors = colors,
            ),
    ) {
        content()
    }
}

/**
 * Defaults for [TopAppBarGlow].
 */
object TopAppBarGlowDefaults {

    /**
     * The blur radius in dp at full strength (top edge). COUI Material `TYPE_DEFAULT_BLUR`.
     */
    val BlurRadius: Float = 20f
}
