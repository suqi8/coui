// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.effect

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.util.lerp
import io.github.suqi8.coui.kmp.shader.RuntimeShader
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * COUI 17 conic light of the media progress bar, ported from `com.coui.appcompat.seekbar.shader`
 * (`ConicLightShaderParams.AGSL_CODE`, `ConicLightPaintHost`, `ConicLightPaintHostDrawer`) and
 * `seekbar/effect/ConicLightEffectRenderer`.
 *
 * Two horizontal light cones trail behind the thumb (towards the start of the bar) and spread
 * vertically as they go back; ahead of the thumb nothing is drawn. The light is drawn over the whole
 * bar with `BlendMode.SCREEN` (`ConicLightPaintHost`). Every length is in raw pixels, as in the
 * source.
 */
internal object ConicLightShader {

    /**
     * `ConicLightShaderParams.AGSL_CODE`, with the comments translated and two guards against
     * undefined math that the source leaves to the GPU: `pow(0, 0)` while the height falloff
     * animates from 0, and the `0 / 0` blend percentage where neither cone reaches.
     */
    val Source: String = """
        uniform float2 u_resolution;
        uniform float3 u_startColor;
        uniform float3 u_endColor;
        // [process, rectWidth, height, heightFalloff]
        uniform float4 u_progressHeightParams;
        // [density, alpha, anti, threshold]
        uniform float4 u_basicParams;
        // [alpha1, alpha2, w0, spotRadius]
        uniform float4 u_alphaParams;
        // [coneK, secondConeK, axialFalloff, radialFalloff]
        uniform float4 u_coneParams;
        // [lightBlendMode, isRtl, reserved, reserved]
        uniform float4 u_miscParams;

        const float TO_POINT = 0.003921;

        float3 blendPlusLighter(float3 base, float3 blend) {
            return min(float3(1.0), base + blend);
        }

        float4 getBloom(float2 q, float coneK, float alpha1) {
            float u_height = u_progressHeightParams.z;
            float u_heightFalloff = u_progressHeightParams.w;
            float u_alpha = u_basicParams.y;
            float w0 = u_alphaParams.z;
            float u_spotRadius = u_alphaParams.w;
            float radialFalloff = u_coneParams.w;
            float axialFalloff = u_coneParams.z;

            float xp = max(-q.x, 0.0);
            float feather = u_spotRadius;
            float w0_px = w0 * feather;

            // Cone width.
            float wCone = coneK * xp + w0_px;
            float w = mix(w0_px, wCone, step(0.0, xp));

            // Edge mask.
            float abs_y = abs(q.y);
            float d = abs_y - w;
            float edgeMask = 1.0 - smoothstep(0.0, feather * 2.0, d);

            // Radial falloff.
            float rNorm = abs_y / (w + 1e-4);
            float radial = exp(-radialFalloff * rNorm * rNorm);

            // Axial falloff, normalized by the spot radius.
            float xpNorm = xp / feather;
            float axial = exp(-axialFalloff * xpNorm);

            // Height falloff.
            float maxHeight = feather * u_height;
            float heightFactor = clamp(xp / (maxHeight + 1e-4), 0.0, 1.0);
            float heightAttenuation = 1.0 - pow(max(heightFactor, 1e-6), u_heightFalloff);
            float cond = step(maxHeight, xp);
            float overDistance = (xp - maxHeight) / feather * cond;
            heightAttenuation *= exp(-overDistance * 3.0);

            // Intensity.
            float intensity = edgeMask * radial * heightAttenuation * axial;
            intensity *= smoothstep(0.0, 0.03 * feather, xp);

            float3 glowColor = mix(u_startColor, u_endColor, heightFactor) * TO_POINT;
            return float4(glowColor * intensity, intensity * alpha1 * u_alpha);
        }

        float max3(float3 v) {
            return max(max(v.r, v.g), v.b);
        }

        // For additive blending: keeps the light energy while guaranteeing rgb <= alpha.
        float4 premulForAdditiveBlend(float4 c) {
            c.rgb = clamp(c.rgb, 0.0, 1.0);
            c.a = clamp(max(c.a, max3(c.rgb)), 0.0, 1.0);
            return c;
        }

        half4 main(float2 fragCoord) {
            float u_process = u_progressHeightParams.x;
            float u_rectWidth = u_progressHeightParams.y;
            float u_alpha1 = u_alphaParams.x;
            float u_alpha2 = u_alphaParams.y;
            float coneK = u_coneParams.x;
            float second_coneK = u_coneParams.y;
            float is_RTL = u_miscParams.y;

            // Pixel position relative to the light source.
            float2 pixelPos = fragCoord - u_resolution * 0.5;
            float spotX = (u_process - 0.5) * u_rectWidth;
            float2 q = pixelPos - float2(spotX, 0.0);

            // RTL mirrors the cone.
            q.x *= 1.0 - 2.0 * is_RTL;

            float4 blend_color = float4(0.0);
            float4 base = getBloom(q, coneK, u_alpha1);
            float4 blend = getBloom(q, second_coneK, u_alpha2);
            blend_color.rgb = blendPlusLighter(base.rgb, blend.rgb);
            blend_color.a = base.a * (1.0 - blend.a) + blend.a;

            float percent = abs(blend.a - base.a) / max(blend_color.a, 1e-6);
            float mix_bg = step(blend.a, base.a) * percent;
            float mix_fg = step(base.a, blend.a) * percent;
            blend_color.rgb = mix(blend_color.rgb, base.rgb, mix_bg);
            blend_color.rgb = mix(blend_color.rgb, blend.rgb, mix_fg);

            float4 final_color = mix(blend_color, float4(0.0), step(0.0, q.x));
            return half4(premulForAdditiveBlend(final_color));
        }
    """.trimIndent()

    /** `ConicLightEffectRenderer.CONIC_LIGHT_HEIGHT`: the height of the light rect (450px). */
    const val LIGHT_HEIGHT = 450f

    /** `ConicLightEffectRenderer.CONIC_LIGHT_HORIZONTAL_PADDING`: the light rect reaches 300px past both track ends. */
    const val HORIZONTAL_PADDING = 300f

    /** `ConicLightPaintHostDrawer.DEFAULT_SPOT_RADIUS` (500px). */
    private const val SPOT_RADIUS = 500f

    /** `ConicLightPaintHostDrawer`: axial falloff 2. */
    private const val AXIAL_FALLOFF = 2f

    /** `ConicLightPaintHostDrawer`: radial falloff 1. */
    private const val RADIAL_FALLOFF = 1f

    /** `ConicLightShaderParams.density` (unused by the shader). */
    private const val DENSITY = 3f

    /** `ConicLightPaintHostDrawer`: alpha 1. */
    private const val ALPHA = 1f

    /** `ConicLightShaderParams.anti` (unused by the shader). */
    private const val ANTI = 2f

    /** `ConicLightPaintHostDrawer`: threshold 0.2 (unused by the shader). */
    private const val THRESHOLD = 0.2f

    /** `ConicLightPaintHostDrawer`: light blend mode 1 (unused by the shader). */
    private const val LIGHT_BLEND_MODE = 1f

    /** `ConicLightEffectRenderer.mBezierThreshold`: below this process the cone height shrinks. */
    private const val BEZIER_THRESHOLD = 0.2f

    /** `ConicLightEffectRenderer.THRESHOLD_EPSILON`. */
    private const val THRESHOLD_EPSILON = 0.01f

    /**
     * `ConicLightEffectRenderer.computeModulatedHeightParams`: near the start of the bar the height
     * and its falloff scale down with the process. The source eases with `cubicBezierY(x, 0, 0, 1, 1)`,
     * whose x and y polynomials are equal, so the easing is the identity.
     */
    fun heightScale(process: Float): Float = if (process > BEZIER_THRESHOLD) 1f else (process / (BEZIER_THRESHOLD + THRESHOLD_EPSILON)).coerceIn(0f, 1f)

    /**
     * Sets every uniform for one frame (`ConicLightShaderParams.flushIfDirty`), clamped like the
     * `ConicLightShaderParams` setters.
     *
     * @param width The width of the light rect: the track length plus [HORIZONTAL_PADDING] on both sides.
     * @param process The thumb position along the track, from the left end, 0..1.
     * @param trackLength The track length (`rectWidth`).
     */
    fun setUniforms(
        shader: RuntimeShader,
        width: Float,
        process: Float,
        trackLength: Float,
        height: Float,
        heightFalloff: Float,
        alpha1: Float,
        alpha2: Float,
        w0: Float,
        coneK: Float,
        secondConeK: Float,
        isRtl: Boolean,
        color: Color,
    ) {
        val red = (color.red * 255f).coerceIn(0f, 255f)
        val green = (color.green * 255f).coerceIn(0f, 255f)
        val blue = (color.blue * 255f).coerceIn(0f, 255f)
        shader.setFloatUniform("u_resolution", width, LIGHT_HEIGHT)
        shader.setFloatUniform("u_startColor", red, green, blue)
        shader.setFloatUniform("u_endColor", red, green, blue)
        shader.setFloatUniform(
            "u_progressHeightParams",
            process,
            trackLength.coerceAtLeast(1f),
            height.coerceIn(0f, 2f),
            heightFalloff.coerceIn(0f, 5f),
        )
        shader.setFloatUniform("u_basicParams", DENSITY, ALPHA, ANTI, THRESHOLD)
        shader.setFloatUniform(
            "u_alphaParams",
            alpha1.coerceIn(0f, 1f),
            alpha2.coerceIn(0f, 1f),
            w0.coerceIn(0f, 0.1f),
            SPOT_RADIUS,
        )
        shader.setFloatUniform(
            "u_coneParams",
            coneK.coerceIn(0f, 1f),
            secondConeK.coerceIn(0f, 1f),
            AXIAL_FALLOFF,
            RADIAL_FALLOFF,
        )
        shader.setFloatUniform("u_miscParams", LIGHT_BLEND_MODE, if (isRtl) 1f else 0f, 0f, 0f)
    }
}

/** `ConicLightEffectRenderer.ConicLightMode`. */
internal enum class ConicLightMode {
    Off,
    Playing,
    Dragging,
}

/**
 * The shape and alpha springs of `ConicLightEffectRenderer`: each transition captures the current
 * parameters and springs them to the targets of the new [ConicLightMode] (`captureAnimFromValues`,
 * `startEnterPlayingAnim`, `startEnterDraggingAnim`, `startFadeOutAnim`). Leaving a lit mode for
 * [ConicLightMode.Off] keeps drawing until the fade-out settles (`mTransitionDrawing`).
 *
 * The parameters are read in the draw phase; only the two fractions are snapshot state.
 */
@Stable
internal class ConicLightState {
    private val shapeFraction = Animatable(1f)
    private val alphaFraction = Animatable(1f)

    // [baseHeight, baseHeightFalloff, coneK, secondConeK, w0]
    private val shapeFrom = FloatArray(SHAPE_COUNT)
    private val shapeTo = floatArrayOf(OFF_HEIGHT, OFF_HEIGHT_FALLOFF, OFF_CONE_K, OFF_CONE_K, OFF_W0)

    // [alpha1, alpha2]
    private val alphaFrom = FloatArray(ALPHA_COUNT)
    private val alphaTo = floatArrayOf(OFF_ALPHA, OFF_ALPHA)

    /** `mCurrentMode`. */
    var mode by mutableStateOf(ConicLightMode.Off)
        private set

    /** `mTransitionDrawing`: the fade-out to [ConicLightMode.Off] is still running. */
    private var transitionDrawing by mutableStateOf(false)

    /** `drawEffect`: the light is drawn while lit or fading out. */
    val isVisible: Boolean get() = mode != ConicLightMode.Off || transitionDrawing

    val baseHeight: Float get() = shape(0)
    val baseHeightFalloff: Float get() = shape(1)
    val coneK: Float get() = shape(2)
    val secondConeK: Float get() = shape(3)
    val w0: Float get() = shape(4)
    val alpha1: Float get() = alpha(0)
    val alpha2: Float get() = alpha(1)

    private fun shape(index: Int): Float = lerp(shapeFrom[index], shapeTo[index], shapeFraction.value.coerceIn(0f, 1f))

    private fun alpha(index: Int): Float = lerp(alphaFrom[index], alphaTo[index], alphaFraction.value.coerceIn(0f, 1f))

    /**
     * `ConicLightEffectRenderer.transitionTo`.
     *
     * @param playW0 The playing cone width (`getPlayW0`, per bar size).
     * @param dragW0 The dragging cone width (`getDragW0`, per bar size).
     * @param dragHeight The dragging cone height (`getDragHeight`, per bar size).
     */
    suspend fun transitionTo(target: ConicLightMode, playW0: Float, dragW0: Float, dragHeight: Float) {
        val wasLit = mode != ConicLightMode.Off
        mode = target
        when (target) {
            ConicLightMode.Playing -> {
                transitionDrawing = false
                animate(
                    shape = floatArrayOf(PLAY_HEIGHT, PLAY_HEIGHT_FALLOFF, PLAY_CONE_K, PLAY_CONE_K, playW0),
                    alpha1 = PLAY_ALPHA1,
                    alpha2 = PLAY_ALPHA2,
                    shapeSpec = EnterPlayShapeSpec,
                    alphaSpec = EnterPlayAlphaSpec,
                )
            }

            ConicLightMode.Dragging -> {
                transitionDrawing = false
                animate(
                    shape = floatArrayOf(dragHeight, DRAG_HEIGHT_FALLOFF, DRAG_CONE_K, DRAG_CONE_K, dragW0),
                    alpha1 = DRAG_ALPHA,
                    alpha2 = DRAG_ALPHA,
                    shapeSpec = EnterDragSpec,
                    alphaSpec = EnterDragSpec,
                )
            }

            ConicLightMode.Off -> {
                if (!wasLit) return
                transitionDrawing = true
                try {
                    animate(
                        shape = floatArrayOf(OFF_HEIGHT, OFF_HEIGHT_FALLOFF, OFF_CONE_K, OFF_CONE_K, OFF_W0),
                        alpha1 = OFF_ALPHA,
                        alpha2 = OFF_ALPHA,
                        shapeSpec = FadeOutSpec,
                        alphaSpec = FadeOutSpec,
                    )
                } finally {
                    if (mode == ConicLightMode.Off) transitionDrawing = false
                }
            }
        }
    }

    /** `ConicLightEffectRenderer.resetToOff`: drops the light at once. */
    suspend fun resetToOff() {
        mode = ConicLightMode.Off
        transitionDrawing = false
        shapeTo[0] = OFF_HEIGHT
        shapeTo[1] = OFF_HEIGHT_FALLOFF
        shapeTo[2] = OFF_CONE_K
        shapeTo[3] = OFF_CONE_K
        shapeTo[4] = OFF_W0
        alphaTo[0] = OFF_ALPHA
        alphaTo[1] = OFF_ALPHA
        shapeFraction.snapTo(1f)
        alphaFraction.snapTo(1f)
    }

    private suspend fun animate(
        shape: FloatArray,
        alpha1: Float,
        alpha2: Float,
        shapeSpec: AnimationSpec<Float>,
        alphaSpec: AnimationSpec<Float>,
    ) {
        // captureAnimFromValues.
        for (i in 0 until SHAPE_COUNT) shapeFrom[i] = shape(i)
        for (i in 0 until ALPHA_COUNT) alphaFrom[i] = alpha(i)
        shape.copyInto(shapeTo)
        alphaTo[0] = alpha1
        alphaTo[1] = alpha2
        shapeFraction.snapTo(0f)
        alphaFraction.snapTo(0f)
        coroutineScope {
            launch { shapeFraction.animateTo(1f, shapeSpec) }
            launch { alphaFraction.animateTo(1f, alphaSpec) }
        }
    }

    private companion object {
        const val SHAPE_COUNT = 5
        const val ALPHA_COUNT = 2

        const val OFF_HEIGHT = 0.1f
        const val OFF_HEIGHT_FALLOFF = 0f
        const val OFF_CONE_K = 0f
        const val OFF_W0 = 0f
        const val OFF_ALPHA = 0f

        const val PLAY_HEIGHT = 0.6f
        const val PLAY_HEIGHT_FALLOFF = 0.6f
        const val PLAY_CONE_K = 0.2f
        const val PLAY_ALPHA1 = 1f
        const val PLAY_ALPHA2 = 0.6f

        const val DRAG_HEIGHT_FALLOFF = 2f
        const val DRAG_CONE_K = 0f
        const val DRAG_ALPHA = 0.3f

        /** `ENTER_PLAY_SHAPE_RESPONSE` 0.9, bounce 0. */
        val EnterPlayShapeSpec = spring(dampingRatio = 1f, stiffness = 48.74f, visibilityThreshold = 0.0001f)

        /** `ENTER_PLAY_ALPHA_RESPONSE` 0.3, bounce 0. */
        val EnterPlayAlphaSpec = spring(dampingRatio = 1f, stiffness = 438.65f, visibilityThreshold = 0.0001f)

        /** `ENTER_DRAG_RESPONSE` 0.4, bounce 0. */
        val EnterDragSpec = spring(dampingRatio = 1f, stiffness = 246.74f, visibilityThreshold = 0.0001f)

        /** `FADEOUT_RESPONSE` 0.4, bounce 0. */
        val FadeOutSpec = spring(dampingRatio = 1f, stiffness = 246.74f, visibilityThreshold = 0.0001f)
    }
}
