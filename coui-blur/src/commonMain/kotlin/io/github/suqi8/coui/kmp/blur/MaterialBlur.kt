// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.blur

import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape

/**
 * How [MaterialBlurStyle.blendColor] is blended over the blurred backdrop, after the
 * `OplusBlurParam` material blend modes a COUI window background blur passes to the renderer.
 */
enum class MaterialBlendMode {
    /** `OplusBlurParam.BLUR_BLEND_MODE_OVERLAY` (2), the dark-scheme mode. */
    Overlay,

    /** `OplusBlurParam.BLUR_BLEND_MODE_COLORDODGE` (3), the light-scheme mode. */
    ColorDodge,
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
 * Paints the COUI 17 material blur behind this node: the [backdrop] blurred by
 * [MaterialBlurStyle.blurRadiusPx], then the renderer's material blend applied to the result.
 *
 * The blend follows the material shader in the ColorOS 17 `libhwui.so` (`.rodata`): the mix color
 * is lerped in first (`inColor.rgb = mix(inColor.rgb, uMixColor.rgb, uMixColor.w)`), then the blend
 * color is blended with `overlay(s, d)` or `colorDodge(s, d) = d / (1 - s)` and lerped in by its
 * alpha (`inColor.rgb = mix(inColor.rgb, blendColor, uBlendColor.w)`). The native colour dodge is
 * not clamped before that last lerp, which this port keeps.
 *
 * Draws nothing where runtime shaders are unavailable; callers should then keep the opaque
 * `?couiColorSurface` fill COUI falls back to (`COUIAlertDialogOverlayHelper.BLUR_DEGRADED`).
 *
 * @param backdrop The [Backdrop] providing the content behind the surface.
 * @param style The [MaterialBlurStyle], e.g. [MaterialBlurDefaults.dialog].
 * @param shape The shape the blur is clipped to. A surface that already clips its content (such as
 *   the coui-ui dialog and bottom sheet `backgroundModifier` slot) can keep [RectangleShape].
 * @param fraction Scales the blur radius and both color alphas, as
 *   `COUIBottomSheetDialog.updateBlurAlphaParams` does while the panel mask fades. 1 is full strength.
 * @param enabled Whether the blur is drawn.
 */
fun Modifier.couiMaterialBlur(
    backdrop: Backdrop,
    style: MaterialBlurStyle,
    shape: Shape = RectangleShape,
    fraction: Float = 1f,
    enabled: Boolean = true,
): Modifier = this.drawBackdrop(
    backdrop = backdrop,
    shape = { shape },
    effects = {
        val strength = fraction.coerceIn(0f, 1f)
        val radiusPx = (style.blurRadiusPx * strength).coerceIn(0f, BlurDefaults.MaxBlurRadius * density)
        if (radiusPx > 0f) blur(radiusPx, radiusPx)
        runtimeShaderEffect(
            key = "COUIMaterialBlend",
            shaderString = MATERIAL_BLEND_SHADER,
            uniformShaderName = "child",
        ) {
            val mix = style.mixColor
            val blend = style.blendColor
            setFloatUniform("uMixColor", mix.red, mix.green, mix.blue, mix.alpha * strength)
            setFloatUniform("uBlendColor", blend.red, blend.green, blend.blue, blend.alpha * strength)
            setFloatUniform("uColorDodge", if (style.blendMode == MaterialBlendMode.ColorDodge) 1f else 0f)
        }
    },
    enabled = enabled,
)

private const val MATERIAL_BLEND_SHADER = """
uniform shader child;
uniform float4 uMixColor;
uniform float4 uBlendColor;
uniform float uColorDodge;

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

half4 main(float2 coord) {
    half4 src = child.eval(coord);
    float a = float(src.a);
    if (a <= 0.0) {
        return src;
    }
    float3 inColor = float3(src.rgb) / a;
    inColor = mix(inColor, uMixColor.rgb, uMixColor.a);
    float3 blendColor = uColorDodge > 0.5
        ? colorDodge(uBlendColor.rgb, inColor)
        : overlay(uBlendColor.rgb, inColor);
    inColor = mix(inColor, blendColor, uBlendColor.a);
    return half4(half3(clamp(inColor, 0.0, 1.0) * a), half(a));
}
"""
