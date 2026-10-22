// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.effect

import androidx.compose.runtime.Immutable
import kotlin.math.max

/**
 * COUI 17 edge lighting + inner shadow, ported from `com.coui.appcompat.shadowedge`.
 *
 * `ToolbarMaterialEffectDelegate` hands a top-bar button's stroke to
 * `OplusMaterialUtil.setEdgeParams/setShadowParams`; `ShadowEdge.java` is the AGSL twin of that
 * renderer, with the same `u_edgeArray` / `u_shadowArray` layout. [Source] is its `NEW_G2` corner
 * variant (`setCornerType(2)`, the one the toolbar requests), and [edgeArray] / [shadowArray]
 * reproduce `GradientStrokeStyleMapper.toEdgeArray16` and `InnerShadowStyleMapper.toShadowArray16`.
 */
internal object ShadowEdgeShader {

    /** `ShadowEdge.buildSource(CornerType.NEW_G2)`, with its unused declarations dropped. */
    val Source: String = """
        uniform float u_commonArray[12];
        uniform float u_shadow;
        uniform float u_shadowArray[16];
        uniform float u_edge;
        uniform float u_edgeArray[16];

        const float EPS = 1e-6;
        const float EFFECT_PARAM_THRESHOLD = 0.001;

        struct CommonDataRect {
            vec2 halfResolution;
            vec2 pos;
            vec2 halfSize;
            float coner;
            float weight;
            float preCalcMinC;
        };

        float getQuadrantValue(vec2 pos, vec4 values) {
            vec2 s = step(vec2(0.0), pos);
            float valueTop = mix(values.x, values.y, s.x);
            float valueBot = mix(values.w, values.z, s.x);
            return mix(valueTop, valueBot, s.y);
        }

        float noise(vec2 p) {
            return fract(sin(dot(p.yx, vec2(79.1214, 78.233))) * 43760.5453);
        }

        float softLight(float A, float B) {
            float l = B - (1. - 2. * A) * B * (1. - B);
            float h = B + (2. * A - 1.) * (sqrt(B) - B);
            return mix(l, h, step(.5, A));
        }

        const float DEFAULT_TRANSITION_PIXELS = 4.0;
        const float kAbs = 1.0;
        const float kMax = 26.6;
        const float junctionXBand = 70.0;
        const float junctionYBand = 60.0;

        float smoothAbs(float x, float k) {
            return sqrt(x * x + k * k);
        }

        float smoothMaxSqrt(float a, float b, float k) {
            float d = a - b;
            return 0.5 * (a + b + sqrt(d * d + k * k));
        }

        float sdCapsule(vec2 pLocal, vec2 halfSize, inout float zoneCap, inout float transitionPixels) {
            bool isVertical = halfSize.x < halfSize.y;
            vec2 hs = isVertical ? halfSize.yx : halfSize;
            vec2 pl = isVertical ? pLocal.yx : pLocal;

            vec2 q = vec2(smoothAbs(pl.x, kAbs), smoothAbs(pl.y, kAbs));
            float r = hs.y;
            float a = max(hs.x - r, 0.0);

            zoneCap = abs(pl.x) - a;

            float scale = r * 0.0125;
            float kMaxEff = kMax * scale;
            float xBandEff = junctionXBand * scale;
            float yBandEff = junctionYBand * scale;

            transitionPixels = max(DEFAULT_TRANSITION_PIXELS, xBandEff);

            float s = q.x - a;
            float xHard = max(s, 0.0);
            float xSoft = (kMaxEff > 0.0) ? smoothMaxSqrt(s, 0.0, kMaxEff) : xHard;
            float wx = 1.0 - smoothstep(0.0, max(xBandEff, 1e-3), abs(s));
            float wy = smoothstep(r - max(yBandEff, 1e-3), r, q.y);
            float w = clamp(wx * wy, 0.0, 1.0);
            float x = mix(xHard, xSoft, w);
            return length(vec2(x, q.y)) - r;
        }

        float powFast(float x, float n) {
            x = max(x, 1e-8);
            return exp2(log2(x) * n);
        }

        float lpNorm(vec2 v, float n) {
            v = abs(v);
            float m = max(v.x, v.y);
            if (m < 1e-8) return 0.0;
            vec2 u = v / m;
            float s = powFast(u.x, n) + powFast(u.y, n);
            return m * exp2(log2(max(s, EPS)) / n);
        }

        float lpNorm4(vec2 v) {
            v = abs(v);
            vec2 v2 = v * v;
            vec2 v4 = v2 * v2;
            return sqrt(sqrt(max(v4.x + v4.y, 0.0)));
        }

        float sdRoundRect(vec2 p, vec2 halfSize, float r, float n, inout vec2 q) {
            q = abs(p) - halfSize + vec2(r);
            float nClamped = max(n, 2.0);
            float dCorner = (abs(nClamped - 4.0) < 1e-3) ? lpNorm4(max(q, 0.0)) : lpNorm(max(q, 0.0), nClamped);
            if (q.x > 0.0 && q.y > 0.0) return dCorner - r;
            else return max(q.x, q.y) - r;
        }

        float smoothSDF_D(vec2 p, vec2 b, float corner, float weight, inout float aaFactor) {
            float rectSdf = 0.0;
            float d_corner_zone = 0.0;
            vec2 q_rect = vec2(0.0, 0.0);
            float transitionPixels = DEFAULT_TRANSITION_PIXELS;

            if (weight < 2.0) {
                float zoneCap = 0.0;
                if (weight > 1.005) {
                    float t = smoothstep(2.0, 1.0, weight);
                    float d_G2 = sdRoundRect(p, b, corner, 2.0, q_rect);
                    float zone_G2 = min(q_rect.x, q_rect.y);
                    float d_Capsule = sdCapsule(p, b, zoneCap, transitionPixels);
                    rectSdf = mix(d_G2, d_Capsule, t);
                    d_corner_zone = mix(zone_G2, zoneCap, t);
                } else {
                    rectSdf = sdCapsule(p, b, zoneCap, transitionPixels);
                    d_corner_zone = zoneCap;
                }
            } else {
                rectSdf = sdRoundRect(p, b, corner, weight, q_rect);
                d_corner_zone = min(q_rect.x, q_rect.y);
            }
            aaFactor = smoothstep(-transitionPixels - 2.0, -transitionPixels * 0.2, d_corner_zone);
            return rectSdf;
        }

        vec4 blendShadowColor(CommonDataRect rectData, vec4 color, float d, vec2 fragCoord) {
            vec3 uFadeColor = vec3(u_shadowArray[0], u_shadowArray[1], u_shadowArray[2]);
            vec3 uFadeAlpha = vec3(u_shadowArray[3], u_shadowArray[4], u_shadowArray[5]);
            float uFadeIn = u_shadowArray[6];
            vec2 uFadeOffset = vec2(u_shadowArray[7], u_shadowArray[8]);
            vec2 uFadeScale = vec2(u_shadowArray[9], u_shadowArray[10]);
            float uFadeInPara1 = u_shadowArray[11];
            float uFadeInPara2 = u_shadowArray[12];
            float uFadeOut = u_shadowArray[13];
            float uFadeOutPara1 = u_shadowArray[14];
            float uFadeOutPara2 = u_shadowArray[15];

            vec2 fadeConer = vec2(0.0);
            fadeConer.y = min(rectData.preCalcMinC, max(uFadeOut * 1.2, rectData.coner));
            float fadeAlpha = 0.0;

            if (any(greaterThan(uFadeAlpha.xy, vec2(EFFECT_PARAM_THRESHOLD)))) {
                vec2 pos_new = rectData.pos - uFadeOffset;
                fadeConer.x = getQuadrantValue(pos_new, vec4(u_commonArray[6], u_commonArray[7], u_commonArray[8], u_commonArray[9]));
                float fadeLine = uFadeIn * -1.0;
                float fadeSdf = fadeLine;
                vec2 halfSizeFade = rectData.halfSize * uFadeScale;
                fadeConer.x = min(min(halfSizeFade.x, halfSizeFade.y), max(uFadeIn * 1.2, fadeConer.x));
                if (rectData.weight < 2.0 || any(greaterThan(abs(pos_new), halfSizeFade - vec2(fadeConer.x)))) {
                    float aaFactor = 1.0;
                    fadeSdf = smoothSDF_D(pos_new, halfSizeFade, fadeConer.x, rectData.weight, aaFactor);
                    float alpha = smoothstep(fadeLine, 0.0, fadeSdf);
                    if (uFadeInPara2 > EFFECT_PARAM_THRESHOLD) {
                        alpha = pow(alpha, uFadeInPara1) * uFadeInPara2 + (1.0 - uFadeInPara2) * alpha;
                    }
                    alpha = alpha * (uFadeAlpha.x - uFadeAlpha.y) + uFadeAlpha.y;
                    fadeAlpha = alpha;
                } else {
                    fadeAlpha = uFadeAlpha.y;
                }
            }

            if (uFadeAlpha.z > EFFECT_PARAM_THRESHOLD) {
                float fadeLine = uFadeOut * -1.0;
                float fadeSdf = fadeLine;
                if (rectData.weight < 2.0 || any(greaterThan(abs(rectData.pos), rectData.halfSize - vec2(fadeConer.y)))) {
                    if (abs(fadeConer.y - rectData.coner) < EFFECT_PARAM_THRESHOLD) {
                        fadeSdf = d;
                    } else {
                        float aaFactor = 1.0;
                        fadeSdf = smoothSDF_D(rectData.pos, rectData.halfSize, fadeConer.y, rectData.weight, aaFactor);
                    }
                    float alpha = smoothstep(fadeLine, 0.0, fadeSdf);
                    if (uFadeOutPara2 > EFFECT_PARAM_THRESHOLD) {
                        alpha = pow(alpha, uFadeOutPara1) * uFadeOutPara2 + (1.0 - uFadeOutPara2) * alpha;
                    }
                    alpha *= uFadeAlpha.z;
                    fadeAlpha = mix(fadeAlpha, 1.0, alpha);
                }
            }

            vec2 uv_pos = fragCoord.xy * 0.013;
            float n = clamp((noise(uv_pos) - .5) * 0.01 + .5, 0., 1.);
            fadeAlpha = clamp(softLight(n, fadeAlpha), 0.0, 1.0);

            vec4 fadeColor = vec4(uFadeColor, fadeAlpha);
            fadeColor.rgb *= fadeColor.a;

            vec4 reColor = vec4(0.0);
            reColor.rgb = color.rgb * (1.0 - fadeColor.a) + fadeColor.rgb;
            reColor.a = color.a * (1.0 - fadeColor.a) + fadeColor.a;
            return reColor;
        }

        float getDis(vec2 pos, vec2 halfsize) {
            vec2 rect = abs(pos - halfsize);
            return rect.x + rect.y;
        }

        vec4 blendEdge(CommonDataRect rectData, vec4 color, float d) {
            vec3 uRectLineColor = vec3(u_edgeArray[0], u_edgeArray[1], u_edgeArray[2]);
            vec2 rectLineNear = vec2(u_edgeArray[3], u_edgeArray[4]);
            vec2 rectLineFar = vec2(u_edgeArray[5], u_edgeArray[6]);
            vec2 uRectLineAlpha = vec2(u_edgeArray[7], u_edgeArray[8]);
            vec4 rectLineFade = vec4(u_edgeArray[9], u_edgeArray[10], u_edgeArray[11], u_edgeArray[12]);
            float uAngle = u_edgeArray[13];
            float uRectLinePara1 = u_edgeArray[14];
            float uRectLinePara2 = u_edgeArray[15];

            if (d < -max(rectLineNear.y, rectLineFar.y)) {
                return color;
            }

            float distanceAll = (rectData.halfSize.x + rectData.halfSize.y) * 2.0;
            vec2 halfSize = rectData.halfSize;

            float ratio = mod(uAngle + rectData.halfSize.x / distanceAll * 0.5 + 0.5, 1.0);
            vec2 dir_pos = normalize(rectData.pos);
            vec2 dirAbs_pos = abs(dir_pos);
            vec2 pos_n = min(vec2(dirAbs_pos.xy * halfSize.yx / max(vec2(EFFECT_PARAM_THRESHOLD), dirAbs_pos.yx)), halfSize) * sign(dir_pos);
            vec2 halfSize_symobol = vec2(-halfSize.y, halfSize.x);
            float flag = mix(-1.0, 1.0, step(ratio, 0.5)) * dot(halfSize_symobol, pos_n);
            float dis_point = distanceAll * min(ratio, 1.0 - ratio) * 2.0;
            float dis_pos = getDis(pos_n, halfSize);
            float distance_near = mix(dis_point + dis_pos, abs(dis_point - dis_pos), step(0., flag));
            distance_near = min(2. * distanceAll - distance_near, distance_near);
            float lineNearAlpha = uRectLineAlpha.x * smoothstep(rectLineFade.y * distanceAll, rectLineFade.x * distanceAll, distance_near);
            float lineFarAlpha = uRectLineAlpha.y * smoothstep(rectLineFade.w * distanceAll, rectLineFade.z * distanceAll, distanceAll - distance_near);

            float lineNearSdf = smoothstep(-rectLineNear.y, -rectLineNear.x, d);
            float lineFarSdf = smoothstep(-rectLineFar.y, -rectLineFar.x, d);

            if (uRectLinePara2 > EFFECT_PARAM_THRESHOLD) {
                lineNearSdf = pow(lineNearSdf, uRectLinePara1) * uRectLinePara2 + (1.0 - uRectLinePara2) * lineNearSdf;
                lineFarSdf = pow(lineFarSdf, uRectLinePara1) * uRectLinePara2 + (1.0 - uRectLinePara2) * lineFarSdf;
            }

            lineNearAlpha *= lineNearSdf;
            lineFarAlpha *= lineFarSdf;

            float lineAlpha = clamp(lineNearAlpha + lineFarAlpha, 0., 1.);

            vec4 rectLineColor = vec4(uRectLineColor, lineAlpha);
            rectLineColor.rgb *= rectLineColor.a;

            vec4 reColor = vec4(0.0);
            reColor.rgb = color.rgb * (1.0 - rectLineColor.a) + rectLineColor.rgb;
            reColor.a = color.a * (1.0 - rectLineColor.a) + rectLineColor.a;
            return reColor;
        }

        vec4 main(vec2 fragCoord) {
            CommonDataRect rectData;
            rectData.halfResolution = vec2(u_commonArray[0], u_commonArray[1]) * 0.5;
            rectData.pos = vec2(fragCoord.x, u_commonArray[1] - fragCoord.y) - vec2(u_commonArray[2], u_commonArray[3]) - rectData.halfResolution;
            rectData.halfSize = vec2(u_commonArray[4], u_commonArray[5]) * 0.5;

            vec2 s = step(vec2(0.0), rectData.pos);
            float rTop = mix(u_commonArray[6], u_commonArray[7], s.x);
            float rBot = mix(u_commonArray[9], u_commonArray[8], s.x);
            rectData.coner = mix(rTop, rBot, s.y);

            rectData.weight = u_commonArray[10];
            rectData.preCalcMinC = min(rectData.halfSize.x, rectData.halfSize.y);

            float aaFactor = 1.0;
            float d = smoothSDF_D(rectData.pos, rectData.halfSize, rectData.coner, rectData.weight, aaFactor);

            float aa = mix(EPS, 1.0, aaFactor);
            float shape = 1.0 - smoothstep(-aa, aa, d);
            if (shape < EFFECT_PARAM_THRESHOLD) {
                return vec4(0.0);
            }

            vec4 color = vec4(0.0);
            if (u_shadow > 0.5) {
                color = blendShadowColor(rectData, color, d, fragCoord);
            }
            if (u_edge > 0.5) {
                color = blendEdge(rectData, color, d);
            }
            return color * shape;
        }
    """.trimIndent()

    /**
     * `ShadowEdgeShader.mCommonUniform` for a shape filling the whole draw area: resolution and size
     * are both the bounds (`COUIShadowEdgeDrawable.onBoundsChange`), the position offset is zero, and
     * all four corners share [cornerRadius]. [weight] is 2 for a circle and 1 for a capsule
     * (`ToolbarMaterialEffectDelegate`: `setWeight(isCapsule() ? 1.0f : 2.0f)`).
     */
    fun commonArray(width: Float, height: Float, cornerRadius: Float, weight: Float): FloatArray = floatArrayOf(
        width,
        height,
        0f,
        0f,
        width,
        height,
        cornerRadius,
        cornerRadius,
        cornerRadius,
        cornerRadius,
        weight,
        1f,
    )

    /**
     * [commonArray] with the top and bottom corner pairs set apart, after
     * `ShadowEdgeShader.setCornerRadius(lt, rt, lb, rb)` (stored as lb, rb, rt, lt). A COUI panel
     * passes its radius to the top corners only and zero to the bottom ones.
     */
    fun commonArray(
        width: Float,
        height: Float,
        topCornerRadius: Float,
        bottomCornerRadius: Float,
        weight: Float,
    ): FloatArray = floatArrayOf(
        width,
        height,
        0f,
        0f,
        width,
        height,
        bottomCornerRadius,
        bottomCornerRadius,
        topCornerRadius,
        topCornerRadius,
        weight,
        1f,
    )

    /** `GradientStrokeStyleMapper.toEdgeArray16`: [lineWidth] is in pixels. */
    fun edgeArray(
        spec: GradientStrokeSpec,
        width: Float,
        height: Float,
        lineWidth: Float,
        lineAlpha: Float,
        lineAngle: Float,
        density: Float,
    ): FloatArray {
        val unit = max(1f, density) * 0.34f
        val unitWidth = max(1f, width) / unit
        val unitHeight = max(1f, height) / unit
        val sizeWidth = max(spec.rectLineSize[0], 1f)
        val sizeHeight = max(spec.rectLineSize[1], 1f)
        val specWidth = max(
            max(spec.rectLineNear[0] + spec.rectLineNear[1], spec.rectLineFar[0] + spec.rectLineFar[1]),
            1.0e-4f,
        )
        val widthScale = (if (!lineWidth.isNaN() && lineWidth > 0f) lineWidth else specWidth) / specWidth

        val out = FloatArray(16)
        out[0] = spec.rectLineColor[0]
        out[1] = spec.rectLineColor[1]
        out[2] = spec.rectLineColor[2]
        out[3] = max(spec.rectLineNear[0] * widthScale, 2f)
        out[4] = max(spec.rectLineNear[1] * widthScale, 2f)
        out[5] = max(spec.rectLineFar[0] * widthScale, 2f)
        out[6] = max(spec.rectLineFar[1] * widthScale, 2f)
        out[7] = spec.rectLineAlpha[0] * lineAlpha
        out[8] = spec.rectLineAlpha[1] * lineAlpha
        rectLineFadeForSize(
            spec.rectLineFade[0],
            spec.rectLineFade[1],
            sizeWidth,
            sizeHeight,
            unitWidth,
            unitHeight,
            out,
            9,
            10,
        )
        rectLineFadeForSize(
            spec.rectLineFade[2],
            spec.rectLineFade[3],
            sizeWidth,
            sizeHeight,
            unitWidth,
            unitHeight,
            out,
            11,
            12,
        )
        out[4] += out[3]
        out[6] += out[5]
        out[10] += out[9]
        out[12] += out[11]
        out[13] = lineAngle
        out[14] = spec.rectLinePara1
        out[15] = spec.rectLinePara2
        return out
    }

    /** `InnerShadowStyleMapper.toShadowArray16`. */
    fun shadowArray(
        spec: InnerShadowSpec,
        width: Float,
        height: Float,
        fadeWidthScale: Float,
        fadeAlphaInScale: Float,
        fadeAlphaOutScale: Float,
        density: Float,
    ): FloatArray {
        val unit = max(1f, density) * 0.34f
        val unitWidth = max(1f, width) / unit
        val unitHeight = max(1f, height) / unit

        val out = FloatArray(16)
        out[0] = spec.fadeColor[0]
        out[1] = spec.fadeColor[1]
        out[2] = spec.fadeColor[2]
        out[3] = spec.fadeAlpha[0] * fadeAlphaInScale
        out[4] = spec.fadeAlpha[1] * fadeAlphaInScale
        out[5] = spec.fadeAlpha[2] * fadeAlphaOutScale
        out[6] = spec.fadeIn * fadeWidthScale * unit
        out[7] = spec.fadeOffset[0] * unit
        out[8] = spec.fadeOffset[1] * unit
        out[9] = spec.fadeSize[0] * (spec.fadeScale[0] - 1f) / unitWidth + 1f
        out[10] = spec.fadeSize[1] * (spec.fadeScale[1] - 1f) / unitHeight + 1f
        out[11] = spec.fadeInPara1
        out[12] = spec.fadeInPara2
        out[13] = spec.fadeOut * fadeWidthScale * unit
        out[14] = spec.fadeOutPara1
        out[15] = spec.fadeOutPara2
        return out
    }

    /** `GradientStrokeStyleKt.rectLineFadeForSize`. */
    private fun rectLineFadeForSize(
        fadeStart: Float,
        fadeLength: Float,
        sizeWidth: Float,
        sizeHeight: Float,
        unitWidth: Float,
        unitHeight: Float,
        out: FloatArray,
        startIndex: Int,
        lengthIndex: Int,
    ) {
        val specPerimeter = (sizeHeight + sizeWidth) * 2f
        val perimeter = (unitHeight + unitWidth) * 2f
        val fadeTotal = fadeStart + fadeLength
        val fadeEnd = fadeTotal * specPerimeter
        if (fadeEnd > sizeWidth) {
            val length = fadeLength * specPerimeter
            out[startIndex] = (unitWidth + (fadeEnd - sizeWidth) - length) / perimeter
            out[lengthIndex] = length / perimeter
            return
        }
        if (fadeTotal <= 0f) {
            out[startIndex] = fadeStart
            out[lengthIndex] = fadeLength
        } else {
            val scaled = (unitWidth - (sizeWidth - fadeEnd)) / perimeter
            out[startIndex] = fadeStart * scaled / fadeTotal
            out[lengthIndex] = scaled * fadeLength / fadeTotal
        }
    }
}

/** `GradientStrokeStyleSpec`: the edge-lighting preset, in 0.34dp design units. */
internal class GradientStrokeSpec(
    val rectLineSize: FloatArray,
    val rectLineColor: FloatArray,
    val rectLineNear: FloatArray,
    val rectLineFar: FloatArray,
    val rectLineAlpha: FloatArray,
    val rectLineFade: FloatArray,
    val rectLinePara1: Float,
    val rectLinePara2: Float,
)

/** `InnerShadowStyleSpec`: the inner-shadow preset, in 0.34dp design units. */
internal class InnerShadowSpec(
    val fadeSize: FloatArray,
    val fadeColor: FloatArray,
    val fadeAlpha: FloatArray,
    val fadeOffset: FloatArray,
    val fadeScale: FloatArray,
    val fadeIn: Float,
    val fadeInPara1: Float,
    val fadeInPara2: Float,
    val fadeOut: Float,
    val fadeOutPara1: Float,
    val fadeOutPara2: Float,
)

/**
 * `COUIMaterialStrokeEffect.StrokeEffectParams`: the per-component scalars layered over the
 * presets. [edgeWidth] is in dp (`getEdgeWidth` applies `TypedValue.applyDimension(DIP, …)`).
 */
@Immutable
internal class StrokeEffectParams(
    val edgeAlpha: Float,
    val edgeWidth: Float,
    val edgeAngle: Float,
    val shadowFadeIn: Float,
    val shadowFadeOut: Float,
    val shadowFadeScale: Float,
)

/**
 * The presets a top-bar button resolves to. Capsules take style 0 and circles style 1
 * (`setEdgeStyle(isCapsule() ? EDGE_STYLE_0 : EDGE_STYLE_1)`); the light and dark presets of both
 * styles are identical, so the scheme only changes [StrokeEffectParams].
 */
internal object ShadowEdgePresets {

    /** `GradientStrokeStylePresets.EDGE_STYLE_LIGHT_0` / `EDGE_STYLE_DARK_0`. */
    val CapsuleEdge = GradientStrokeSpec(
        rectLineSize = floatArrayOf(400f, 100f),
        rectLineColor = floatArrayOf(1f, 1f, 1f),
        rectLineNear = floatArrayOf(2f, 6f),
        rectLineFar = floatArrayOf(2f, 3f),
        rectLineAlpha = floatArrayOf(0.7f, 1f),
        rectLineFade = floatArrayOf(0.2f, 0.4f, 0.15f, 0.35f),
        rectLinePara1 = 4f,
        rectLinePara2 = 0.8f,
    )

    /** `GradientStrokeStylePresets.EDGE_STYLE_LIGHT_1` / `EDGE_STYLE_DARK_1`. */
    val CircleEdge = GradientStrokeSpec(
        rectLineSize = floatArrayOf(160f, 160f),
        rectLineColor = floatArrayOf(1f, 1f, 1f),
        rectLineNear = floatArrayOf(2f, 6f),
        rectLineFar = floatArrayOf(2f, 3f),
        rectLineAlpha = floatArrayOf(0.7f, 1f),
        rectLineFade = floatArrayOf(0.06f, 0.4f, 0.01f, 0.28f),
        rectLinePara1 = 4f,
        rectLinePara2 = 0.8f,
    )

    /** `InnerShadowStylePresets.SHADOW_STYLE_LIGHT_0` / `SHADOW_STYLE_DARK_0`. */
    val CapsuleShadow = InnerShadowSpec(
        fadeSize = floatArrayOf(400f, 100f),
        fadeColor = floatArrayOf(1f, 1f, 1f),
        fadeAlpha = floatArrayOf(1f, 0f, 0f),
        fadeOffset = floatArrayOf(0f, 20f),
        fadeScale = floatArrayOf(1.3f, 1.15f),
        fadeIn = 120f,
        fadeInPara1 = 4f,
        fadeInPara2 = 0.9f,
        fadeOut = 20f,
        fadeOutPara1 = 4f,
        fadeOutPara2 = 0.7f,
    )

    /** `InnerShadowStylePresets.SHADOW_STYLE_LIGHT_1` / `SHADOW_STYLE_DARK_1`. */
    val CircleShadow = InnerShadowSpec(
        fadeSize = floatArrayOf(160f, 160f),
        fadeColor = floatArrayOf(1f, 1f, 1f),
        fadeAlpha = floatArrayOf(0.4f, 0f, 0f),
        fadeOffset = floatArrayOf(0f, 20f),
        fadeScale = floatArrayOf(1.4f, 1.35f),
        fadeIn = 140f,
        fadeInPara1 = 4f,
        fadeInPara2 = 0.9f,
        fadeOut = 20f,
        fadeOutPara1 = 4f,
        fadeOutPara2 = 0.7f,
    )

    /**
     * `GradientStrokeStylePresets.EDGE_STYLE_LIGHT_2` / `EDGE_STYLE_DARK_2`, the edge style the
     * blurred alert dialog (`COUIAlertDialogRootLayout`) and bottom sheet (`COUIBottomSheetDialog`)
     * request. The light and dark presets are identical.
     */
    val PanelEdge = GradientStrokeSpec(
        rectLineSize = floatArrayOf(600f, 200f),
        rectLineColor = floatArrayOf(1f, 1f, 1f),
        rectLineNear = floatArrayOf(2f, 8f),
        rectLineFar = floatArrayOf(2f, 2f),
        rectLineAlpha = floatArrayOf(0.7f, 0.6f),
        rectLineFade = floatArrayOf(0.18f, 0.35f, 0.25f, 0.25f),
        rectLinePara1 = 4f,
        rectLinePara2 = 0.8f,
    )

    /**
     * `InnerShadowStylePresets.SHADOW_STYLE_LIGHT_2` / `SHADOW_STYLE_DARK_2`, the inner shadow of the
     * blurred bottom sheet. The light and dark presets are identical.
     */
    val PanelShadow = InnerShadowSpec(
        fadeSize = floatArrayOf(600f, 600f),
        fadeColor = floatArrayOf(1f, 1f, 1f),
        fadeAlpha = floatArrayOf(0.6f, 0f, 0f),
        fadeOffset = floatArrayOf(0f, 10f),
        fadeScale = floatArrayOf(1.1f, 1.07f),
        fadeIn = 86f,
        fadeInPara1 = 4f,
        fadeInPara2 = 0.7f,
        fadeOut = 1f,
        fadeOutPara1 = 1f,
        fadeOutPara2 = 0f,
    )

    /** `FRAMEWORK_CIRCLE_PARAMS_1_LIGHT` (`TYPE_FRAMEWORK_CIRCLE_1`). */
    val CircleLight = StrokeEffectParams(0.2f, 1.9f, 0f, 0.15f, 1f, 1f)

    /** `FRAMEWORK_CIRCLE_PARAMS_1_DARK`. */
    val CircleDark = StrokeEffectParams(0.6f, 1.9f, 0f, 0.1f, 1f, 1f)

    /** `FRAMEWORK_CAPSULE_PARAMS_1_LIGHT` (`TYPE_FRAMEWORK_CAPSULE_1`). */
    val CapsuleLight = StrokeEffectParams(0.2f, 2.2f, 0f, 0.1f, 1f, 1.15f)

    /** `FRAMEWORK_CAPSULE_PARAMS_1_DARK`. */
    val CapsuleDark = StrokeEffectParams(0.6f, 2.2f, 0f, 0.01f, 1f, 1f)

    /** `CONTENT_CAPSULE_PARAMS_1_LIGHT`, the primary `COUIButton` stroke. */
    val ContentCapsule1Light = StrokeEffectParams(0.3f, 2.8f, 0f, 0.1f, 1f, 1f)

    /** `CONTENT_CAPSULE_PARAMS_1_DARK`. */
    val ContentCapsule1Dark = StrokeEffectParams(0.26f, 2.8f, 0f, 0.08f, 1f, 0.8f)

    /** `CONTENT_CAPSULE_PARAMS_1_DISABLED_LIGHT`. */
    val ContentCapsule1DisabledLight = StrokeEffectParams(0.12f, 2.8f, 0f, 0.03f, 0.3f, 1f)

    /** `CONTENT_CAPSULE_PARAMS_1_DISABLED_DARK`. */
    val ContentCapsule1DisabledDark = StrokeEffectParams(0.18f, 2.8f, 0f, 0.024f, 0.3f, 0.8f)

    /** `CONTENT_CAPSULE_PARAMS_2_LIGHT`, the secondary `COUIButton` stroke. */
    val ContentCapsule2Light = StrokeEffectParams(0.5f, 2.8f, 0f, 0.1f, 1f, 1f)

    /** `CONTENT_CAPSULE_PARAMS_2_DARK`. */
    val ContentCapsule2Dark = StrokeEffectParams(0.5f, 2.8f, 0f, 0.05f, 1f, 1f)

    /** `CONTENT_CAPSULE_PARAMS_2_DISABLED_LIGHT`. */
    val ContentCapsule2DisabledLight = StrokeEffectParams(0.15f, 2.8f, 0f, 0.03f, 0.3f, 1f)

    /** `CONTENT_CAPSULE_PARAMS_2_DISABLED_DARK`. */
    val ContentCapsule2DisabledDark = StrokeEffectParams(0.15f, 2.8f, 0f, 0.015f, 0.3f, 1f)

    /** `CONTENT_CAPSULE_PARAMS_3_LIGHT` / `_DARK` (identical), the transparent `COUIButton` stroke. */
    val ContentCapsule3 = StrokeEffectParams(0.5f, 2.8f, 0f, 0.05f, 1f, 1f)

    /** `CONTENT_CAPSULE_PARAMS_3_DISABLED_LIGHT` / `_DARK` (identical). */
    val ContentCapsule3Disabled = StrokeEffectParams(0.15f, 2.8f, 0f, 0.015f, 0.3f, 1f)
}
