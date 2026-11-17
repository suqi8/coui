// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.blur

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

/**
 * The "condensed light" (凝光) glow overlay for a COUI 17 top app bar.
 *
 * Two layers composite over the content scrolled beneath the bar:
 * 1. A backdrop blur (radius [blurRadius], strongest at the top and fading to pixel-sharp at the
 *    bottom via a progressive gradient).
 * 2. A translucent vertical gradient base ([baseColor] at the COUI alpha stops), transparent at the
 *    bottom edge and thickening toward the top, so the band dissolves into the content below it.
 *
 * The whole overlay fades in with the scroll: `alpha = min(1, scrollOffsetPx / 30dp)`.
 *
 * COUI 17 evidence (settings 17.0.0, `com.coui.appcompat.toolbar`):
 * - Backdrop filter: `OplusRenderEffect.createGradientBlurEffect(30, 0, true, 3)` — the radii are
 *   **raw pixels**, not dp (`AppBarBlurParamsExtKt` passes 30f straight through), so the caller
 *   converts the 30px token with `30f / density`. The libhwui SkSL ramps the radius down the
 *   overlay (`ratio = in_ratios.x + (in_ratios.y - in_ratios.x) * (coord.y / in_size)`) and applies
 *   the speed exponent (`ratio = 1.0 - pow(1.0 - ratio, in_speed)`, i.e. `1 - (y/H)^3`); the
 *   progressive blur used here follows the same `1 - (y/H)^3` falloff. The native shader then
 *   truncates the radius to whole pixels (`int(float(in_blurRadius) * ratio)`, sharp at
 *   `radius <= 1`) and adds dither noise; this port ramps the radius continuously and adds no noise.
 * - Alpha gradient base (`GradientBackgroundDrawable`: `LinearGradient(0, height, 0, 0)`, so
 *   position 0 is the **bottom** edge): light 3 stops positions [0, 0.4, 1] × alphas [0, 0.7, 1];
 *   dark 2 stops positions [0, 1] × alphas [0, 0.4] (GRADIENT_DARK_ALPHA).
 * - No colour blend: the default `GradientBlurConfig()` passes blendMode / blendC / mixC = 0.
 * - No-blur fallback (`AppBarBlurHelper.applyGradientBlurBackgroundAppBar` when blur is off):
 *   the gradient and the render effect are cleared and the app bar gets its opaque background
 *   back. Where runtime shaders are unavailable this overlay does the same: it paints [baseColor]
 *   opaque over the bar, leaves the [TopAppBarGlowDefaults.ExtraHeight] strip below it empty, and
 *   ignores the scroll-driven alpha.
 *
 * @param backdrop The [Backdrop] providing the scrolled content to blur (e.g. `rememberLayerBackdrop`
 *   on the page content).
 * @param scrollOffsetPx The top-app-bar scroll offset in pixels (a positive value that grows as the
 *   bar collapses, e.g. `abs(scrollBehavior.state.heightOffset)`). The glow alpha follows COUI 17:
 *   `min(1, scrollOffsetPx / 30dp)`, so the frosting is fully revealed after only a 30dp scroll.
 * @param isDark Whether the dark scheme is active, to pick the dark alpha stops.
 * @param baseColor The opaque base color of the gradient sheet, usually the COUI page surface
 *   (`couiColorBackgroundWithCard`), the same color as the app bar.
 * @param modifier The [Modifier] to be applied to the overlay.
 * @param blurRadiusPx The backdrop blur radius in raw pixels at the top edge. COUI 17
 *   `AppBarBlurParamsExtKt` uses `createGradientBlurEffect(30f, 0f, true, 3f)`: the radius ramps
 *   from [blurRadiusPx] at the top to 0 at the bottom as `blurRadiusPx * (1 - (y/H)^3)`.
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
    if (!enabled) {
        return
    }
    val overlayModifier = if (!isRuntimeShaderSupported()) {
        // AppBarBlurHelper.applyGradientBlurBackgroundAppBar without blur: clear the gradient and
        // restore the bar's opaque background, so unblurred content never shows through the bar.
        Modifier
            .padding(bottom = TopAppBarGlowDefaults.ExtraHeight)
            .background(color = baseColor, shape = shape)
    } else {
        glowOverlayModifier(backdrop, scrollOffsetPx, isDark, baseColor, blurRadiusPx, shape) ?: return
    }
    Box(modifier = modifier.matchParentSize().then(overlayModifier)) {
        content()
    }
}

/**
 * The blurred, gradient-tinted overlay of [TopAppBarGlow], or null while the scroll offset keeps
 * it fully transparent.
 */
@Composable
private fun glowOverlayModifier(
    backdrop: Backdrop,
    scrollOffsetPx: Float,
    isDark: Boolean,
    baseColor: Color,
    blurRadiusPx: Float,
    shape: Shape,
): Modifier? {
    // COUI 17 AppBarBlurOverlayBehavior.computeOverlayAlpha: alpha = min(1, scrollPx / 30dp).
    val thresholdPx = with(LocalDensity.current) { TopAppBarGlowDefaults.AlphaScrollThreshold.toPx() }
    val alpha = (scrollOffsetPx / thresholdPx).coerceIn(0f, 1f)
    if (alpha <= 0f) {
        return null
    }

    // GradientBackgroundDrawable draws LinearGradient(0, height, 0, 0): its position 0 is the bottom
    // edge, so the stops are mirrored onto Compose's top-to-bottom axis. The sheet is densest at the
    // top and fully transparent where it meets the content.
    val gradient = remember(baseColor, isDark) {
        if (isDark) {
            Brush.verticalGradient(
                0f to baseColor.copy(alpha = 0.4f),
                1f to baseColor.copy(alpha = 0f),
            )
        } else {
            Brush.verticalGradient(
                0f to baseColor.copy(alpha = 1f),
                0.6f to baseColor.copy(alpha = 0.7f),
                1f to baseColor.copy(alpha = 0f),
            )
        }
    }

    val blurModifier = Modifier.progressiveTextureBlur(
        backdrop = backdrop,
        shape = shape,
        // The 30px token is raw pixels (AppBarBlurParamsExtKt passes 30f straight to the
        // RenderEffect), so convert it against the display density before handing the dp-based
        // modifier its radius.
        blurRadius = (blurRadiusPx / LocalDensity.current.density).coerceAtLeast(0f),
        gradient = TopAppBarGlowDefaults.Gradient,
        // The default GradientBlurConfig() passes blendMode = blendC = mixC = 0: no colour blend.
        colors = NoBlend,
    )

    // AppBarBlurOverlayBehavior sets the alpha on the whole overlay view, so the blur and the
    // gradient sheet fade in together.
    return Modifier
        .graphicsLayer { this.alpha = alpha }
        .then(blurModifier)
        .drawBehind { drawRect(brush = gradient) }
}

private val NoBlend = BlurColors()

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
     * How far the overlay runs past the bottom of the app bar. COUI 17
     * `AppBarBlurOverlayView.computeExtraHeightPx` with `scrollDrivenAlphaEnabled` (24dp). The host
     * sizes the overlay as the whole bar plus this strip (`TopAppBar` does); the no-blur fallback
     * leaves the strip empty.
     */
    val ExtraHeight = 24.dp

    /**
     * The progressive ramp matching the native gradient blur: full radius at the top, zero at the
     * bottom, shaped by the native `speed = 3f` exponent (`ratio = 1 - pow(1 - ratio, 3)`, applied
     * to `ratio = 1 - y/H`, which reduces to `1 - (y/H)^3`; the progressive blur evaluates
     * `1 - raw^curve` on the linear band position `raw = y/H`).
     */
    val Gradient: ProgressiveBlur = ProgressiveBlur(
        angle = 90f,
        startFraction = 0f,
        endFraction = 1f,
        curve = 3f,
    )
}
