// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.effect

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import io.github.suqi8.coui.kmp.shader.RuntimeShader

/**
 * COUI 17 touch spotlight, ported from `com.coui.appcompat.spotlight` (`SpotLight.java` AGSL,
 * `COUISpotLightEffect`, `COUISpotLightEffectDrawable`).
 *
 * The light sits under the finger: it glows up on press, follows the pointer, and on release
 * spreads out while it fades. At rest nothing is drawn. Every length is in raw pixels, as in the
 * source.
 */
internal object SpotLightShader {

    /** `SpotLight.AGSL_SOURCE`. */
    val Source: String = """
        uniform vec4 u_lightPosAndHeight;
        uniform vec4 u_lightColorAndAmbient;
        uniform vec4 u_lightingParams;

        float luminance(vec3 c) { return dot(c, vec3(0.299, 0.587, 0.114)); }

        float max3(vec3 v) { return max(max(v.r, v.g), v.b); }

        float noise(vec2 p) {
            return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
        }

        float softLight(float A, float B) {
            float l = B - (1.0 - 2.0 * A) * B * (1.0 - B);
            float h = B + (2.0 * A - 1.0) * (sqrt(B) - B);
            return mix(l, h, step(0.5, A));
        }

        float dither(vec2 coord, float ditherN) {
            float n1 = fract(sin(dot(coord, vec2(12.9898, 78.233))) * 43758.5453);
            float n2 = fract(sin(dot(coord + vec2(1.0), vec2(12.9898, 78.233))) * 43758.5453);
            return (n1 + n2 - 1.0) * ditherN / 255.0;
        }

        half4 main(float2 fragCoord) {
            float2 lightP = u_lightPosAndHeight.xy;
            float radius = u_lightPosAndHeight.z;
            float intensity = u_lightPosAndHeight.w;
            vec3 lightCol = u_lightColorAndAmbient.rgb;
            float ambientStr = u_lightColorAndAmbient.w;
            float diffuseStr = u_lightingParams.x;
            float lightH = u_lightingParams.y;
            float noise_exp = u_lightingParams.z;
            float ditherN = u_lightingParams.w;
            const float lightIntensity = 1.5;

            vec2 delta = fragCoord - lightP;
            vec3 L = vec3(delta, lightH);
            float dist = length(L);
            L = normalize(L);
            float diff = max(L.z, 0.0);
            vec3 lighting = (ambientStr + diffuseStr * diff) * lightCol;
            if (dist >= radius) {
                return half4(0.0);
            }
            float att = smoothstep(1.0, 0.0, dist / radius);
            lighting *= att * lightIntensity * intensity;
            float alpha = clamp(luminance(lighting), 0.0, 1.0);

            if (noise_exp > 0.001) {
                vec2 uv_pos = fragCoord * 9.259e-4;
                float n = clamp((noise(uv_pos) - 0.5) * noise_exp + 0.5, 0.0, 1.0);
                float mask = max3(lighting);
                float fadeAlpha = clamp(softLight(n, mask), 0.0, 1.0);
                lighting += vec3(clamp(abs(fadeAlpha - mask), 0.0, 1.0));
            }
            if (ditherN > 0.001) {
                float d = dither(fragCoord, ditherN);
                lighting += vec3(d);
                alpha = clamp(alpha + d, 0.0, 1.0);
            }
            return half4(lighting, alpha);
        }
    """.trimIndent()

    // The parameters below are shared by every [SpotLightType] this library uses
    // (`COUISpotLightEffect` static initializer); only the end intensity and blend mode differ.

    /** Start radius (100px). */
    val StartRadius = 100f

    /** End radius (500px). */
    val EndRadius = 500f

    /** Light height (200px). */
    val LightHeight = 200f

    /** Ambient strength. */
    val Ambient = 0.4f

    /** Diffuse strength. */
    val Diffuse = 0.4f

    /** Grain amount. */
    val Noise = 0.08f

    /**
     * Light radius at glow [progress]. While extinguishing, `COUISpotLightEffect` adds
     * `2 * endRadius * (1 - progress)` so the light spreads out as it fades.
     */
    fun radius(progress: Float, extinguishing: Boolean): Float {
        val base = StartRadius + (EndRadius - StartRadius) * progress
        return if (extinguishing) base + 2f * EndRadius * (1f - progress) else base
    }

    fun setUniforms(
        shader: RuntimeShader,
        lightX: Float,
        lightY: Float,
        radius: Float,
        intensity: Float,
    ) {
        shader.setFloatUniform("u_lightPosAndHeight", lightX, lightY, radius, intensity)
        shader.setFloatUniform("u_lightColorAndAmbient", 1f, 1f, 1f, Ambient)
        shader.setFloatUniform("u_lightingParams", Diffuse, LightHeight, Noise, 0f)
    }
}

/**
 * The `COUISpotLightEffect.SpotLightType` presets this library draws. All of them share the
 * [SpotLightShader] radii, light height, ambient / diffuse strength and noise; they differ in the
 * end intensity and the blend mode, per light / dark theme (`COUISpotLightEffect` static
 * initializer, `SpotLightParams(blendMode, startIntensity, endIntensity, ...)`).
 */
internal enum class SpotLightType(
    private val lightIntensity: Float,
    private val lightBlendMode: BlendMode,
    private val darkIntensity: Float,
    private val darkBlendMode: BlendMode,
) {
    /** `TYPE_OPAQUE_SMALL_2`: the top-bar buttons. 1.0 OVERLAY light, 0.15 PLUS dark. */
    OpaqueSmall2(1f, BlendMode.Overlay, 0.15f, BlendMode.Plus),

    /** `TYPE_OPAQUE_MEDIUM_1`: primary buttons and `COUISeekBar`. 0.6 light, 0.8 dark, both OVERLAY. */
    OpaqueMedium1(0.6f, BlendMode.Overlay, 0.8f, BlendMode.Overlay),

    /** `TYPE_OPAQUE_MEDIUM_2`: secondary buttons. 1.0 OVERLAY light, 0.15 PLUS dark. */
    OpaqueMedium2(1f, BlendMode.Overlay, 0.15f, BlendMode.Plus),

    /** `TYPE_TRANSPARENT_MEDIUM_1`: transparent buttons. 0.5 light, 0.3 dark, both OVERLAY. */
    TransparentMedium1(0.5f, BlendMode.Overlay, 0.3f, BlendMode.Overlay),
    ;

    /** The intensity the light glows up to. */
    fun endIntensity(isDark: Boolean): Float = if (isDark) darkIntensity else lightIntensity

    /** The blend mode the light is drawn with over the content beneath it. */
    fun blendMode(isDark: Boolean): BlendMode = if (isDark) darkBlendMode else lightBlendMode
}

/** `COUISpotLightEffect` glow / follow / extinguish, driven by the pointer. */
internal class SpotLightState {
    val progress = Animatable(0f)
    val position = Animatable(Offset.Zero, Offset.VectorConverter)
    var extinguishing by mutableStateOf(false)
        private set

    suspend fun glow(at: Offset) {
        extinguishing = false
        position.snapTo(at)
        // glow(): response 0.15, bounce 0.
        progress.animateTo(1f, spring(dampingRatio = 1f, stiffness = 1754.6f))
    }

    suspend fun follow(to: Offset) {
        // Pointer follow: response 0.35, bounce 0.15.
        position.animateTo(to, spring(dampingRatio = 0.85f, stiffness = 322.27f))
    }

    suspend fun extinguish() {
        extinguishing = true
        // extinguish(): response 0.4, bounce 0.
        progress.animateTo(0f, spring(dampingRatio = 1f, stiffness = 246.74f))
    }
}
