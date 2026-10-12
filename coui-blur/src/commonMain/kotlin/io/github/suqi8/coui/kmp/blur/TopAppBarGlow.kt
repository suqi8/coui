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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.effect.COUIMaterialEffect.materialGlow

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
 * - Backdrop filter: `OplusRenderEffect.createGradientBlurEffect(30, 0, true, 3)` — the radii are
 *   **raw pixels**, not dp (`AppBarBlurParamsExtKt` passes 30f straight through), so the caller
 *   converts the 30px token with `30f / density`. The SkSL ramps the radius down the overlay
 *   (`ratio = lerp(start, end, y/H)`), applies the speed exponent (`ratio = 1 - pow(1 - ratio, 3)`,
 *   i.e. `1 - (y/H)^3`), then quantises to `radius = int(15 * ratio)` — so the bottom ~4.7% of the
 *   overlay is pixel-sharp and fully unblurred, not merely faint.
 * - Alpha gradient base (`GradientBackgroundDrawable`, direction bottom→top): light 3 stops
 *   positions [0, 0.4, 1] × alphas [0, 0.7, 1]; dark 2 stops positions [0, 1] × alphas [0, 0.4]
 *   (GRADIENT_DARK_ALPHA).
 * - Blend layers (`COUIMaterialBlurEffect`, TYPE_TOP_BAR): light `#66333333` COLOR_DODGE +
 *   `#99F5F5F5` LUMINOSITY; dark `#CC262626` SRC_OVER + `#B37F7F7F` OVERLAY.
 *
 * @param backdrop The [Backdrop] providing the scrolled content to blur (e.g. `rememberLayerBackdrop`
 *   on the page content).
 * @param scrollOffsetPx The top-app-bar scroll offset in pixels (a positive value that grows as the
 *   bar collapses, e.g. `abs(scrollBehavior.state.heightOffset)`). The glow alpha follows COUI 17:
 *   `min(1, scrollOffsetPx / 30dp)`, so the frosting is fully revealed after only a 30dp scroll.
 * @param isDark Whether the dark scheme is active, to pick the dark blend layers and alpha stops.
 * @param baseColor The opaque base color of the gradient sheet. Defaults to the COUI page surface
 *   (`couiColorBackgroundWithCard`); callers usually pass the same color as the app bar.
 * @param modifier The [Modifier] to be applied to the overlay.
 * @param blurRadiusPx The backdrop blur radius in raw pixels at the top edge. COUI 17
 *   `AppBarBlurParamsExtKt` uses `createGradientBlurEffect(30f, 0f, true, 3f)`: the radius ramps
 *   from [blurRadiusPx] at the top to 0 at the bottom, quantised to `int(15 * (1 - (y/H)^3))` — so
 *   the bottom ~4.7% of the overlay is fully sharp.
 * @param shape The shape of the overlay region.
 * @param enabled Whether the condensed-light glow is active. **Off by default** — the caller must
 *   opt in, since the glow is a heavy frosting treatment COUI applies only in specific scenarios.
 * @param content Optional foreground content drawn on top of the glow (usually none).
 */
@Composable
fun BoxScope.TopAppBarGlow(
    backdrop: Backdrop,
    scrollOffsetPx: Float,
    isDark: Boolean,
    baseColor: Color,
    modifier: Modifier = Modifier,
    blurRadiusPx: Float = TopAppBarGlowDefaults.BlurRadiusPx,
    shape: Shape = RectangleShape,
    enabled: Boolean = false,
    content: @Composable () -> Unit = {},
) {
    // COUI 17 AppBarBlurOverlayBehavior.computeOverlayAlpha: alpha = min(1, scrollPx / 30dp).
    val thresholdPx = with(LocalDensity.current) { TopAppBarGlowDefaults.AlphaScrollThreshold.toPx() }
    val alpha = (scrollOffsetPx / thresholdPx).coerceIn(0f, 1f)
    if (!enabled || alpha <= 0f) {
        return
    }

    // Translucent vertical gradient base, drawn bottom→top (position 0 at the bottom edge).
    // COUI 17 GradientBlurConfig: light positions [0, 0.4, 1] × alphas [0, 0.7, 1];
    // dark positions [0, 1] × alphas [0, GRADIENT_DARK_ALPHA=0.4].
    val gradient = remember(baseColor, isDark) {
        if (isDark) {
            Brush.verticalGradient(
                0f to baseColor.copy(alpha = 0f),
                1f to baseColor.copy(alpha = 0.4f),
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
            // The 30px token is raw pixels (AppBarBlurParamsExtKt passes 30f straight to the
            // RenderEffect), so convert it against the display density before handing the dp-based
            // modifier its radius.
            blurRadius = (blurRadiusPx / LocalDensity.current.density).coerceAtLeast(0f),
            gradient = TopAppBarGlowDefaults.Gradient,
            colors = colors,
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .matchParentSize()
            .then(blurModifier)
            .materialGlow(
                baseColor = baseColor,
                scrollOffsetPx = scrollOffsetPx,
                isDark = isDark,
                enabled = enabled,
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
     * The backdrop blur radius in raw pixels at the overlay's top edge. COUI 17
     * `AppBarBlurParamsExtKt` default: `AppBarBlurParams(30, 0, 3, ...)`.
     */
    val BlurRadiusPx: Float = 30f

    /**
     * The scroll distance over which the glow fades in. COUI 17
     * `AppBarBlurOverlayBehavior.ALPHA_SCROLL_THRESHOLD_DP` (30dp).
     */
    val AlphaScrollThreshold = 30.dp

    /**
     * The progressive ramp matching the native gradient blur: full radius at the top, zero at the
     * bottom, shaped by the native `speed = 3f` exponent (`ratio = 1 - pow(1 - ratio, 3)`, applied
     * to `ratio = 1 - y/H`, which reduces to `1 - (y/H)^3`).
     */
    val Gradient: ProgressiveBlur = ProgressiveBlur(
        angle = 90f,
        startFraction = 0f,
        endFraction = 1f,
        curve = 3f,
    )
}
