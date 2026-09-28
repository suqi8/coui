// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.blur

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape

/**
 * The "condensed light" (凝光) glow overlay for a COUI 17 top app bar.
 *
 * Two layers composite over the content scrolled beneath the bar:
 * 1. A backdrop blur (radius [blurRadius], strongest at the top and fading to pixel-sharp at the
 *    bottom via a progressive gradient), tinted by the two condensed-light blend layers.
 * 2. A translucent vertical gradient base ([baseColor] at the COUI alpha stops), drawn bottom→top
 *    so the bar reads as a frosted sheet that thickens toward its own top edge.
 *
 * The whole overlay is revealed by [alpha], which the caller drives from the top-app-bar collapse
 * fraction (COUI reveals it over a 30dp scroll: `alpha = min(1, scroll / 30dp)`).
 *
 * COUI 17 evidence (settings 17.0.0, `com.coui.appcompat.toolbar`):
 * - Blur radius: the dialog/background blur radius token `coui_list_dialog_background_blur_radius`
 *   (240dp) — the COUI frosted-sheet blur strength, not the small Material effect radius.
 * - Alpha gradient base (`GradientBackgroundDrawable`, direction bottom→top): light 3 stops
 *   positions [0, 0.4, 1] × alphas [0, 0.7, 1]; dark 2 stops positions [0, 0.6] × alphas [0, 1]
 *   (NORMAL_BLUR_DARK_ALPHA ramp).
 * - Blend layers (`COUIMaterialBlurEffect`, TYPE_TOP_BAR): light `#66333333` COLOR_DODGE +
 *   `#99F5F5F5` LUMINOSITY; dark `#CC262626` SRC_OVER + `#B37F7F7F` OVERLAY.
 *
 * @param backdrop The [Backdrop] providing the scrolled content to blur (e.g. `rememberLayerBackdrop`
 *   on the page content).
 * @param collapseFraction The top-app-bar collapse fraction in `[0, 1]`: 0 = expanded (overlay
 *   hidden), 1 = collapsed (overlay fully revealed). COUI maps scroll→alpha as min(1, scroll/30dp).
 * @param isDark Whether the dark scheme is active, to pick the dark blend layers and alpha stops.
 * @param baseColor The opaque base color of the gradient sheet. Defaults to the COUI page surface
 *   (`couiColorBackgroundWithCard`); callers usually pass the same color as the app bar.
 * @param modifier The [Modifier] to be applied to the overlay.
 * @param blurRadius The blur radius in dp at full strength. COUI `coui_list_dialog_background_blur_radius` (240dp).
 * @param shape The shape of the overlay region.
 * @param content Optional foreground content drawn on top of the glow (usually none).
 */
@Composable
fun BoxScope.TopAppBarGlow(
    backdrop: Backdrop,
    collapseFraction: Float,
    isDark: Boolean,
    baseColor: Color,
    modifier: Modifier = Modifier,
    blurRadius: Float = TopAppBarGlowDefaults.BlurRadius,
    shape: Shape = RectangleShape,
    content: @Composable () -> Unit = {},
) {
    val alpha = collapseFraction.coerceIn(0f, 1f)
    if (alpha <= 0f) {
        return
    }

    // Translucent vertical gradient base, drawn bottom→top (position 0 at the bottom edge).
    val gradient = remember(baseColor, isDark) {
        if (isDark) {
            Brush.verticalGradient(
                0f to baseColor.copy(alpha = 0f),
                0.6f to baseColor.copy(alpha = 1f),
            )
        } else {
            Brush.verticalGradient(
                0f to baseColor.copy(alpha = 0f),
                0.4f to baseColor.copy(alpha = 0.7f),
                1f to baseColor.copy(alpha = 1f),
            )
        }
    }

    val blurSupported = isRuntimeShaderSupported()
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

    val blurModifier = if (blurSupported) {
        Modifier.progressiveTextureBlur(
            backdrop = backdrop,
            shape = shape,
            blurRadius = blurRadius,
            gradient = ProgressiveBlur.Top,
            colors = colors,
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .matchParentSize()
            .alpha(alpha)
            .then(blurModifier)
            .background(gradient),
    ) {
        content()
    }
}

/**
 * Defaults for [TopAppBarGlow].
 */
object TopAppBarGlowDefaults {

    /**
     * The blur radius in dp at full strength. COUI `coui_list_dialog_background_blur_radius` (240dp).
     */
    val BlurRadius: Float = 240f
}
