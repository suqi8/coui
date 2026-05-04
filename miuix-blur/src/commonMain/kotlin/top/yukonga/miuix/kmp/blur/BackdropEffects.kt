// Copyright 2026, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package top.yukonga.miuix.kmp.blur

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import top.yukonga.miuix.kmp.blur.internal.GAUSSIAN_KERNEL_REACH
import top.yukonga.miuix.kmp.blur.internal.GAUSSIAN_RADIUS_TO_SIGMA
import top.yukonga.miuix.kmp.blur.internal.MI_BLEND_MODE_SHADER
import top.yukonga.miuix.kmp.blur.internal.chain
import top.yukonga.miuix.kmp.blur.internal.computeDownScaleParams
import top.yukonga.miuix.kmp.blur.internal.createGaussianBlurEffect

/** Maximum number of blend layers supported by [blendColors]. Extra entries are dropped. */
private const val MAX_BLEND_LAYERS = 8

/**
 * Chains a separable Gaussian blur into the scope's [BackdropEffectScope.renderEffect],
 * adjusts [BackdropEffectScope.padding] to cover the kernel reach, and updates
 * [BackdropEffectScope.downscaleFactor]. Non-positive radii skip that axis.
 *
 * Typical use:
 * ```
 * Modifier.drawBackdrop(backdrop, shape = { shape }, effects = {
 *     gaussianBlur(20f * density)
 * })
 * ```
 *
 * @param radiusX Horizontal blur radius in pixels.
 * @param radiusY Vertical blur radius in pixels. Defaults to [radiusX] for isotropic blur.
 */
fun BackdropEffectScope.gaussianBlur(radiusX: Float, radiusY: Float = radiusX) {
    if (!isRuntimeShaderSupported()) return

    // Pre-compute downscale factor to set padding before creating the effect.
    val sigmaMax = maxOf(radiusX, radiusY) * GAUSSIAN_RADIUS_TO_SIGMA
    val sf = computeDownScaleParams(sigmaMax).downScale

    // Padding covers the blur kernel's maximum sampling reach in original
    // pixel space. Keeps recording dimensions stable across radius changes
    // within the same downscale level.
    val kernelPadding = (GAUSSIAN_KERNEL_REACH * sf).toFloat()
    if (kernelPadding > padding) {
        padding = kernelPadding
    }

    val paddedSize = Size(size.width + padding * 2f, size.height + padding * 2f)
    val result = createGaussianBlurEffect(radiusX, radiusY, paddedSize, this) ?: return

    downscaleFactor = result.downscaleFactor
    renderEffect = renderEffect?.chain(result.renderEffect) ?: result.renderEffect
}

/**
 * Registers a noise dither pass with the given [coefficient]. Non-positive values are ignored.
 * Noise is applied at full resolution after upscaling so each screen pixel gets independent
 * dithering, which prevents banding visible at low blur radii.
 */
fun BackdropEffectScope.noiseDither(coefficient: Float) {
    if (coefficient <= 0f) return
    noiseCoefficient = coefficient
}

/**
 * Chains all blend color layers from [colors] as a single runtime shader pass. Up to
 * [MAX_BLEND_LAYERS] entries are honored. Brightness and saturation in [colors] are
 * folded into the blend shader's uniforms (separate from any [colorControls] you may
 * have already chained).
 */
fun BackdropEffectScope.blendColors(colors: BlurColors) {
    if (colors.blendColors.isEmpty()) return
    if (!isRuntimeShaderSupported()) return

    val layers = colors.blendColors.take(MAX_BLEND_LAYERS)

    runtimeShaderEffect(
        key = "MiBlendModes",
        shaderString = MI_BLEND_MODE_SHADER,
        uniformShaderName = "child",
    ) {
        setFloatUniform("layerCount", layers.size.toFloat())

        // Pack blend modes as float array (Skiko lacks IntArray uniform support)
        val modes = FloatArray(MAX_BLEND_LAYERS)
        for (i in layers.indices) {
            modes[i] = layers[i].mode.value.toFloat()
        }
        setFloatUniform("blendModes", modes)

        // Pack colors as flat float array with premultiplied alpha
        // (array-indexed setColorUniform is not supported on Android/Skiko)
        val colorData = FloatArray(MAX_BLEND_LAYERS * 4)
        for (i in layers.indices) {
            val c = layers[i].color.convert(ColorSpaces.Srgb)
            val a = c.alpha
            colorData[i * 4] = c.red * a
            colorData[i * 4 + 1] = c.green * a
            colorData[i * 4 + 2] = c.blue * a
            colorData[i * 4 + 3] = a
        }
        setFloatUniform("layerColors", colorData)
        setFloatUniform("uSaturation", colors.saturation)
        setFloatUniform("uBrightness", colors.brightness)
        setFloatUniform("uLuminanceAmount", 0f)
        setFloatUniform("uLuminanceValues", 0f, 0f, 0f, 0f)
    }
}

/**
 * Convenience: chains a single blend color layer with the given [mode].
 *
 * Each call adds an independent shader pass; for batch-blending many layers
 * (≥ 3) prefer a single [blendColors] call with a packed [BlurColors] instead.
 */
fun BackdropEffectScope.blendColor(color: Color, mode: BlurBlendMode = BlurBlendMode.SrcOver) {
    blendColors(BlurColors(blendColors = listOf(BlendColorEntry(color, mode))))
}

/**
 * Runs the standard texture-blur preset chain inside a custom [drawBackdrop] effect block.
 *
 * Equivalent to what [Modifier.textureBlur] applies internally:
 * 1. [noiseDither] for anti-banding
 * 2. [colorControls] for brightness/contrast/saturation in linear (gamma 2.2) space
 * 3. [gaussianBlur] with the given radii in dp (multiplied by [BackdropEffectScope.density])
 * 4. [blendColors] for the layered tinting
 *
 * Use this to compose the standard preset with additional custom effects:
 * ```
 * Modifier.drawBackdrop(backdrop, shape = { shape }, effects = {
 *     textureBlurEffect(blurRadius = 30f, colors = colors)
 *     // ...then chain your own effect on top
 * })
 * ```
 *
 * @param blurRadiusX Horizontal blur radius in dp.
 * @param blurRadiusY Vertical blur radius in dp. Defaults to [blurRadiusX].
 * @param noiseCoefficient Noise dithering coefficient. 0 disables noise.
 * @param colors Color adjustments and blend layers applied after blur.
 */
fun BackdropEffectScope.textureBlurEffect(
    blurRadiusX: Float,
    blurRadiusY: Float = blurRadiusX,
    noiseCoefficient: Float = BlurDefaults.NoiseCoefficient,
    colors: BlurColors = BlurColors(),
) {
    val clampedX = blurRadiusX.coerceIn(0f, BlurDefaults.MaxBlurRadius)
    val clampedY = blurRadiusY.coerceIn(0f, BlurDefaults.MaxBlurRadius)
    noiseDither(noiseCoefficient)
    colorControls(colors.brightness, colors.contrast, colors.saturation)
    gaussianBlur(clampedX * density, clampedY * density)
    blendColors(colors)
}
