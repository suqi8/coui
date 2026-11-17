// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.blur

import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import io.github.suqi8.coui.kmp.blur.internal.BLUR_RADIUS_TO_SIGMA
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * How [MaterialBlurStyle.blendColor] is blended over the blurred backdrop, after the
 * `OplusBlurParam` material blend modes a COUI window background blur passes to the renderer.
 */
enum class MaterialBlendMode {
    /** `OplusBlurParam.BLUR_BLEND_MODE_OVERLAY` (2), the dark-scheme mode. */
    Overlay,

    /** `OplusBlurParam.BLUR_BLEND_MODE_COLORDODGE` (3), the light-scheme mode. */
    ColorDodge,

    /**
     * `OplusBlurParam.BLUR_BLEND_MODE_GLOW_OVERLAY` (4): the mix color is lerped in as a
     * luminosity blend (libhwui `luminosity(uMixColor.rgb, inColor.rgb)`) before the overlay blend.
     * The COUI 17 popup menu uses it in both schemes.
     */
    GlowOverlay,

    /** `OplusBlurParam.BLUR_BLEND_MODE_GLOW_COLORDODGE` (5): the luminosity mix, then color dodge. */
    GlowColorDodge,
}

/**
 * The parameters of a COUI 17 material (window background) blur: `COUIBackgroundBlurBuilder`
 * hands `OplusBlurParam.setMaterialParams(mode, blendColor, mixColor)` and a blur radius to the
 * window's `BackgroundBlurDrawable`.
 *
 * @param blurRadiusPx The blur radius in raw pixels, as COUI passes it to
 *   `ViewRootManager.setBlurRadius` (an integer resource, not a dimension).
 * @param mixColor The color mixed over the blurred backdrop first, weighted by its alpha.
 * @param blendColor The color then blended with [blendMode], weighted by its alpha.
 * @param blendMode How [blendColor] is blended.
 */
@Immutable
data class MaterialBlurStyle(
    val blurRadiusPx: Float,
    val mixColor: Color,
    val blendColor: Color,
    val blendMode: MaterialBlendMode,
)

/**
 * The COUI 17 material blur presets.
 */
object MaterialBlurDefaults {

    /** `coui_dialog_blur_radius` (200), the alert dialog blur radius in raw pixels. */
    val DialogBlurRadiusPx: Float = 200f

    /** `coui_bottomdialog_blur_radius` (400), the bottom sheet blur radius in raw pixels. */
    val BottomSheetBlurRadiusPx: Float = 400f

    /**
     * The alert dialog blur (`COUIAlertDialogBuilder.operateBlur`): radius `coui_dialog_blur_radius`,
     * mode 2 (overlay) in the dark scheme and 3 (color dodge) in the light one, mix colors
     * `coui_dialog_list_mix_blur_light` `#DC909090` / `_dark` `#8C777777` and blend colors
     * `coui_dialog_list_blend_blur_light` `#509F9F9F` / `_dark` `#B2292929`.
     *
     * @param isDark Whether the dark scheme is active.
     */
    fun dialog(isDark: Boolean): MaterialBlurStyle = if (isDark) DialogDark else DialogLight

    /**
     * The bottom sheet blur (`COUIBottomSheetDialog.initBlurParams` + `applyBlurParams`): radius
     * `coui_bottomdialog_blur_radius`, the same mode switch as [dialog], mix colors
     * `coui_bottomdialog_list_mix_blur_light` `#DCC8C8C8` / `_dark` `#A4515151` and blend colors
     * `coui_bottomdialog_list_blend_blur_light` `#376E6E6E` / `_dark` `#B30F0F0F`.
     *
     * @param isDark Whether the dark scheme is active.
     */
    fun bottomSheet(isDark: Boolean): MaterialBlurStyle = if (isDark) BottomSheetDark else BottomSheetLight

    /** `RoundFrameLayout.DEFAULT_BLUR_RADIUS_PX` (160), the popup menu blur radius in raw pixels. */
    val PopupBlurRadiusPx: Float = 160f

    /**
     * The popup menu blur (`RoundFrameLayout.applyBlurColorParams`): radius [PopupBlurRadiusPx].
     * - SDR branch: mode 4 ([MaterialBlendMode.GlowOverlay]) in both schemes, mix colors
     *   `coui_popup_list_mix_blur_light` `#99CDCDCD` / `_dark` `#99262626` and blend colors
     *   `coui_popup_list_blend_blur_light` `#BFEEEEEE` / `_dark` `#99262626`.
     * - HDR branch (`RoundFrameLayout.useHdrBlurParams`, `Display.getHdrSdrRatio() > 1`): mode 2
     *   (overlay) in the dark scheme and 3 (color dodge) in the light one, mix colors
     *   `coui_popup_list_mix_blur_light_hdr` `#DC7A7A7A` / `_dark_hdr` `#50262626` and blend colors
     *   `coui_popup_list_blend_blur_light_hdr` `#C8868686` / `_dark_hdr` `#990F0F0F`.
     *
     * @param isDark Whether the dark scheme is active.
     * @param hdr Whether the display currently shows HDR content (COUI reads the display's
     *   HDR/SDR ratio); the caller decides, since Compose has no common HDR signal.
     */
    fun popup(isDark: Boolean, hdr: Boolean = false): MaterialBlurStyle = when {
        hdr -> if (isDark) PopupHdrDark else PopupHdrLight
        else -> if (isDark) PopupDark else PopupLight
    }

    /**
     * The dither strength [couiMaterialBlur] applies by default. The ColorOS 17 Kawase material
     * blur ends with a `KawaseBlur_Dither` pass (`libhwui.so` shader: `ditherRange = float2(-0.005,
     * 0.005)`, `originColor.rgb += mixValue * ditherAlpha`), i.e. at most ±0.005 per channel, which
     * this coefficient reproduces through [noiseDither]. `ditherAlpha` is taken as 1.
     */
    val NoiseCoefficient: Float = 0.005f

    private val PopupHdrLight = MaterialBlurStyle(
        blurRadiusPx = PopupBlurRadiusPx,
        mixColor = Color(0xDC7A7A7A),
        blendColor = Color(0xC8868686),
        blendMode = MaterialBlendMode.ColorDodge,
    )

    private val PopupHdrDark = MaterialBlurStyle(
        blurRadiusPx = PopupBlurRadiusPx,
        mixColor = Color(0x50262626),
        blendColor = Color(0x990F0F0F),
        blendMode = MaterialBlendMode.Overlay,
    )

    private val PopupLight = MaterialBlurStyle(
        blurRadiusPx = PopupBlurRadiusPx,
        mixColor = Color(0x99CDCDCD),
        blendColor = Color(0xBFEEEEEE),
        blendMode = MaterialBlendMode.GlowOverlay,
    )

    private val PopupDark = MaterialBlurStyle(
        blurRadiusPx = PopupBlurRadiusPx,
        mixColor = Color(0x99262626),
        blendColor = Color(0x99262626),
        blendMode = MaterialBlendMode.GlowOverlay,
    )

    private val DialogLight = MaterialBlurStyle(
        blurRadiusPx = DialogBlurRadiusPx,
        mixColor = Color(0xDC909090),
        blendColor = Color(0x509F9F9F),
        blendMode = MaterialBlendMode.ColorDodge,
    )

    private val DialogDark = MaterialBlurStyle(
        blurRadiusPx = DialogBlurRadiusPx,
        mixColor = Color(0x8C777777),
        blendColor = Color(0xB2292929),
        blendMode = MaterialBlendMode.Overlay,
    )

    private val BottomSheetLight = MaterialBlurStyle(
        blurRadiusPx = BottomSheetBlurRadiusPx,
        mixColor = Color(0xDCC8C8C8),
        blendColor = Color(0x376E6E6E),
        blendMode = MaterialBlendMode.ColorDodge,
    )

    private val BottomSheetDark = MaterialBlurStyle(
        blurRadiusPx = BottomSheetBlurRadiusPx,
        mixColor = Color(0xA4515151),
        blendColor = Color(0xB30F0F0F),
        blendMode = MaterialBlendMode.Overlay,
    )
}

/**
 * Converts a COUI raw-pixel material (window background) blur radius into the radius [blur] takes,
 * so both produce the same spread.
 *
 * The ColorOS 17 renderer blurs window backgrounds with a Kawase filter, not a Gaussian:
 * `OplusKawaseBlurImageFilter` in `libhwui.so` keeps the AOSP `KawaseBlurFilter` pass schedule (its
 * `numberOfPasses` / `radiusByPasses` symbols) and its 5-tap pass shader (`c` plus four diagonal
 * taps at `±in_blurOffset`, `* 0.2`). With `tmp = r / 2`, `passes = min(4, ceil(tmp))` and
 * `step = tmp / passes`, the pass offsets are `step, 1·step, 2·step, …, (passes − 1)·step` in
 * full-resolution pixels, and each pass adds a per-axis variance of `0.8 · offset²`. The chain thus
 * spreads like a Gaussian of `sigma² = 0.8 · step² · (1 + Σ i²)`, about `0.433 · r` from 8px up
 * (the resampling filters add only a few px² and are ignored). The result is that sigma divided by
 * the `0.45` radius-to-sigma factor of [blur]. A radius of 0 stays 0.
 *
 * @param radiusPx The COUI radius in raw pixels, e.g. [MaterialBlurStyle.blurRadiusPx].
 */
fun couiMaterialBlurRadius(radiusPx: Float): Float {
    if (radiusPx <= 0f) return 0f
    val tmpRadius = radiusPx / 2f
    val passes = ceil(tmpRadius).toInt().coerceIn(1, KAWASE_MAX_PASSES)
    val step = tmpRadius / passes
    // Offsets: step (first pass), then i * step for i in 1 until passes.
    var offsetSquares = 1f
    for (i in 1 until passes) offsetSquares += (i * i).toFloat()
    val sigma = step * sqrt(KAWASE_PASS_VARIANCE * offsetSquares)
    return sigma / BLUR_RADIUS_TO_SIGMA
}

/** AOSP `BlurFilter::kMaxPasses`, the Kawase pass cap. */
private const val KAWASE_MAX_PASSES = 4

/** Per-axis variance of one 5-tap Kawase pass in units of its offset²: four of five taps at ±d. */
private const val KAWASE_PASS_VARIANCE = 0.8f

/**
 * Paints the COUI 17 material blur behind this node: the [backdrop] blurred by
 * [MaterialBlurStyle.blurRadiusPx] (converted with [couiMaterialBlurRadius] to match the native
 * Kawase filter), then the renderer's material blend applied to the result, then a light dither.
 *
 * The blend follows the material shader in the ColorOS 17 `libhwui.so` (`.rodata`): the mix color
 * is lerped in first (`inColor.rgb = mix(inColor.rgb, uMixColor.rgb, uMixColor.w * ratio)`, or the
 * luminosity of the mix color for the glow modes), then the blend color is blended with
 * `overlay(s, d)` or `colorDodge(s, d) = d / (1 - s)` and lerped in by its alpha
 * (`inColor.rgb = mix(inColor.rgb, blendColor.rgb, uBlendColor.w * ratio)`). A uniform window blur
 * has `ratio = 1`. The native colour dodge is not clamped before that last lerp, which this port
 * keeps. The native shader runs on the opaque window content; this port unpremultiplies first so a
 * translucent backdrop pixel gets the same result scaled by its alpha.
 *
 * Draws nothing where runtime shaders are unavailable; callers should then keep the opaque
 * `?couiColorSurface` fill COUI falls back to (`COUIAlertDialogOverlayHelper.BLUR_DEGRADED`).
 *
 * @param backdrop The [Backdrop] providing the content behind the surface.
 * @param style The [MaterialBlurStyle], e.g. [MaterialBlurDefaults.dialog].
 * @param shape The shape the blur is clipped to. A surface that already clips its content (such as
 *   the coui-ui dialog and bottom sheet `backgroundModifier` slot) can keep [RectangleShape].
 * @param fraction Scales the blur radius and both color alphas, as
 *   `COUIBottomSheetDialog.updateBlurAlphaParams` does while the panel mask fades (the scaled radius
 *   is rounded to whole pixels, `Math.round(mBlurRadius * f)`). 1 is full strength.
 * @param enabled Whether the blur is drawn.
 * @param noiseCoefficient The dither strength, see [MaterialBlurDefaults.NoiseCoefficient]. 0
 *   disables the dither.
 */
fun Modifier.couiMaterialBlur(
    backdrop: Backdrop,
    style: MaterialBlurStyle,
    shape: Shape = RectangleShape,
    fraction: Float = 1f,
    enabled: Boolean = true,
    noiseCoefficient: Float = MaterialBlurDefaults.NoiseCoefficient,
): Modifier = this.drawBackdrop(
    backdrop = backdrop,
    shape = { shape },
    effects = {
        val strength = fraction.coerceIn(0f, 1f)
        val couiRadiusPx = (style.blurRadiusPx * strength).roundToInt().toFloat()
        val radiusPx = couiMaterialBlurRadius(couiRadiusPx).coerceIn(0f, BlurDefaults.MaxBlurRadius * density)
        // A zero radius takes the native `KawaseBlur_NoBlur_ReturnSource` path: no blur, no dither.
        if (radiusPx > 0f) {
            noiseDither(noiseCoefficient)
            blur(radiusPx, radiusPx)
        }
        runtimeShaderEffect(
            key = "COUIMaterialBlend",
            shaderString = MATERIAL_BLEND_SHADER,
            uniformShaderName = "child",
        ) {
            val mix = style.mixColor
            val blend = style.blendColor
            setFloatUniform("uMixColor", mix.red, mix.green, mix.blue, mix.alpha * strength)
            setFloatUniform("uBlendColor", blend.red, blend.green, blend.blue, blend.alpha * strength)
            val dodge = style.blendMode == MaterialBlendMode.ColorDodge || style.blendMode == MaterialBlendMode.GlowColorDodge
            val glow = style.blendMode == MaterialBlendMode.GlowOverlay || style.blendMode == MaterialBlendMode.GlowColorDodge
            setFloatUniform("uColorDodge", if (dodge) 1f else 0f)
            setFloatUniform("uLuminosity", if (glow) 1f else 0f)
        }
    },
    enabled = enabled,
)

private const val MATERIAL_BLEND_SHADER = """
uniform shader child;
uniform float4 uMixColor;
uniform float4 uBlendColor;
uniform float uColorDodge;
uniform float uLuminosity;

float overlay(float s, float d) {
    return (d < 0.5) ? 2.0 * s * d : 1.0 - 2.0 * (1.0 - s) * (1.0 - d);
}

float3 overlay(float3 s, float3 d) {
    return float3(overlay(s.x, d.x), overlay(s.y, d.y), overlay(s.z, d.z));
}

float colorDodge(float s, float d) {
    return d / max(1.0 - s, 1e-4);
}

float3 colorDodge(float3 s, float3 d) {
    return float3(colorDodge(s.x, d.x), colorDodge(s.y, d.y), colorDodge(s.z, d.z));
}

// libhwui `luminosity(s, d)`: d moved to the luminance of s, clipped around that luminance.
float3 luminosity(float3 s, float3 d) {
    float dLum = dot(d, float3(0.3, 0.59, 0.11));
    float sLum = dot(s, float3(0.3, 0.59, 0.11));
    float3 c = d + (sLum - dLum);
    float minC = min(min(c.x, c.y), c.z);
    float maxC = max(max(c.x, c.y), c.z);
    if (minC < 0.0) {
        return sLum + ((c - sLum) * sLum) / (sLum - minC);
    } else if (maxC > 1.0) {
        return sLum + ((c - sLum) * (1.0 - sLum)) / (maxC - sLum);
    }
    return c;
}

half4 main(float2 coord) {
    half4 src = child.eval(coord);
    float a = float(src.a);
    if (a <= 0.0) {
        return src;
    }
    float3 inColor = float3(src.rgb) / a;
    float3 mixed = uLuminosity > 0.5 ? luminosity(uMixColor.rgb, inColor) : uMixColor.rgb;
    inColor = mix(inColor, mixed, uMixColor.a);
    float3 blendColor = uColorDodge > 0.5
        ? colorDodge(uBlendColor.rgb, inColor)
        : overlay(uBlendColor.rgb, inColor);
    inColor = mix(inColor, blendColor, uBlendColor.a);
    return half4(half3(clamp(inColor, 0.0, 1.0) * a), half(a));
}
"""
