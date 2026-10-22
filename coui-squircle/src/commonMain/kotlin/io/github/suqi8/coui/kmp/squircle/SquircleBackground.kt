// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.squircle

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import io.github.suqi8.coui.kmp.shader.RuntimeShader
import io.github.suqi8.coui.kmp.shader.asComposeShader
import io.github.suqi8.coui.kmp.shader.isRuntimeShaderSupported
import io.github.suqi8.coui.kmp.squircle.internal.SoftCapsule
import io.github.suqi8.coui.kmp.squircle.internal.SuperellipseCorner
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Squircle solid background — fill-only, descendants are NOT clipped.
 *
 * Falls back to `Modifier.background(color, RoundedCornerShape(cornerRadius))`
 * when runtime shaders are unavailable (Android < API 33).
 *
 * @param color The fill [Color] of the squircle background.
 * @param cornerRadius The radius applied uniformly to all four corners.
 * @param weight The superellipse exponent of the corners (Oplus smooth round rect weight): 2.0 is a
 *   circular arc, larger values square the corners off. Clamped to
 *   [SquircleDefaults.WeightMin]..[SquircleDefaults.WeightMax]; a weight below 2 draws the Oplus soft
 *   capsule blend when the rect is fully rounded and 2 otherwise (see [SquircleDefaults]).
 */
@Composable
fun Modifier.squircleBackground(
    color: Color,
    cornerRadius: Dp,
    weight: Float = SquircleDefaults.Weight,
): Modifier = squircleBackground(color, cornerRadius, cornerRadius, cornerRadius, cornerRadius, weight)

/**
 * Per-corner variant of [squircleBackground]. Ordering matches [RoundedCornerShape].
 *
 * @param color The fill [Color] of the squircle background.
 * @param topStart The corner radius of the top-start corner (flipped by `LocalLayoutDirection`).
 * @param topEnd The corner radius of the top-end corner (flipped by `LocalLayoutDirection`).
 * @param bottomEnd The corner radius of the bottom-end corner (flipped by `LocalLayoutDirection`).
 * @param bottomStart The corner radius of the bottom-start corner (flipped by `LocalLayoutDirection`).
 * @param weight The superellipse exponent of the corners, clamped to
 *   [SquircleDefaults.WeightMin]..[SquircleDefaults.WeightMax].
 */
@Composable
fun Modifier.squircleBackground(
    color: Color,
    topStart: Dp,
    topEnd: Dp,
    bottomEnd: Dp,
    bottomStart: Dp,
    weight: Float = SquircleDefaults.Weight,
): Modifier {
    val brush = rememberSquircleBrush(color, topStart, topEnd, bottomEnd, bottomStart, weight)
        ?: return this.background(
            color = color,
            shape = RoundedCornerShape(topStart, topEnd, bottomEnd, bottomStart),
        )
    return this.background(brush = brush, shape = RectangleShape)
}

/**
 * Clips the subtree to the squircle silhouette.
 *
 * Falls back to `Modifier.clip(RoundedCornerShape(cornerRadius))` when
 * runtime shaders are unavailable.
 *
 * @param cornerRadius The radius applied uniformly to all four corners.
 * @param weight The superellipse exponent of the corners, clamped to
 *   [SquircleDefaults.WeightMin]..[SquircleDefaults.WeightMax].
 */
@Composable
fun Modifier.squircleClip(
    cornerRadius: Dp,
    weight: Float = SquircleDefaults.Weight,
): Modifier = squircleClip(cornerRadius, cornerRadius, cornerRadius, cornerRadius, weight)

/**
 * Per-corner variant of [squircleClip].
 *
 * @param topStart The corner radius of the top-start corner (flipped by `LocalLayoutDirection`).
 * @param topEnd The corner radius of the top-end corner (flipped by `LocalLayoutDirection`).
 * @param bottomEnd The corner radius of the bottom-end corner (flipped by `LocalLayoutDirection`).
 * @param bottomStart The corner radius of the bottom-start corner (flipped by `LocalLayoutDirection`).
 * @param weight The superellipse exponent of the corners, clamped to
 *   [SquircleDefaults.WeightMin]..[SquircleDefaults.WeightMax].
 */
@Composable
fun Modifier.squircleClip(
    topStart: Dp,
    topEnd: Dp,
    bottomEnd: Dp,
    bottomStart: Dp,
    weight: Float = SquircleDefaults.Weight,
): Modifier {
    val brush = rememberSquircleBrush(Color.White, topStart, topEnd, bottomEnd, bottomStart, weight)
        ?: return this.clip(RoundedCornerShape(topStart, topEnd, bottomEnd, bottomStart))
    return this.squircleShaderMask(brush, fillColor = null)
}

/**
 * Fill + clip behind a squircle silhouette — drop-in replacement for
 * `.clip(RoundedCornerShape(r)).background(color)` with a squircle outline.
 *
 * @param color The fill [Color] drawn behind the clipped content.
 * @param cornerRadius The radius applied uniformly to all four corners.
 * @param weight The superellipse exponent of the corners, clamped to
 *   [SquircleDefaults.WeightMin]..[SquircleDefaults.WeightMax].
 */
@Composable
fun Modifier.squircleSurface(
    color: Color,
    cornerRadius: Dp,
    weight: Float = SquircleDefaults.Weight,
): Modifier = squircleSurface(color, cornerRadius, cornerRadius, cornerRadius, cornerRadius, weight)

/**
 * Per-corner variant of [squircleSurface].
 *
 * @param color The fill [Color] drawn behind the clipped content.
 * @param topStart The corner radius of the top-start corner (flipped by `LocalLayoutDirection`).
 * @param topEnd The corner radius of the top-end corner (flipped by `LocalLayoutDirection`).
 * @param bottomEnd The corner radius of the bottom-end corner (flipped by `LocalLayoutDirection`).
 * @param bottomStart The corner radius of the bottom-start corner (flipped by `LocalLayoutDirection`).
 * @param weight The superellipse exponent of the corners, clamped to
 *   [SquircleDefaults.WeightMin]..[SquircleDefaults.WeightMax].
 */
@Composable
fun Modifier.squircleSurface(
    color: Color,
    topStart: Dp,
    topEnd: Dp,
    bottomEnd: Dp,
    bottomStart: Dp,
    weight: Float = SquircleDefaults.Weight,
): Modifier {
    val brush = rememberSquircleBrush(Color.White, topStart, topEnd, bottomEnd, bottomStart, weight)
        ?: return this
            .clip(RoundedCornerShape(topStart, topEnd, bottomEnd, bottomStart))
            .background(color)
    return this.squircleShaderMask(brush, fillColor = color)
}

/**
 * Absolute-positioning variant of [squircleBackground]. Corner parameters are physical
 * (clockwise from top-left) and are NOT flipped by `LocalLayoutDirection` — mirrors
 * `AbsoluteRoundedCornerShape`. Reach for this when corners are anchored to physical sides
 * regardless of layout direction (e.g. transition reveals tied to a swipe edge).
 *
 * @param color The fill [Color] of the squircle background.
 * @param topLeft The physical top-left corner radius, never flipped by `LocalLayoutDirection`.
 * @param topRight The physical top-right corner radius, never flipped by `LocalLayoutDirection`.
 * @param bottomRight The physical bottom-right corner radius, never flipped by `LocalLayoutDirection`.
 * @param bottomLeft The physical bottom-left corner radius, never flipped by `LocalLayoutDirection`.
 * @param weight The superellipse exponent of the corners, clamped to
 *   [SquircleDefaults.WeightMin]..[SquircleDefaults.WeightMax].
 */
@Composable
fun Modifier.absoluteSquircleBackground(
    color: Color,
    topLeft: Dp,
    topRight: Dp,
    bottomRight: Dp,
    bottomLeft: Dp,
    weight: Float = SquircleDefaults.Weight,
): Modifier {
    val brush = rememberSquircleBrush(color, topLeft, topRight, bottomRight, bottomLeft, weight)
        ?: return this.background(
            color = color,
            shape = AbsoluteRoundedCornerShape(topLeft, topRight, bottomRight, bottomLeft),
        )
    return this.background(brush = brush, shape = RectangleShape)
}

/**
 * Absolute-positioning variant of [squircleClip]. See [absoluteSquircleBackground] for semantics.
 *
 * @param topLeft The physical top-left corner radius, never flipped by `LocalLayoutDirection`.
 * @param topRight The physical top-right corner radius, never flipped by `LocalLayoutDirection`.
 * @param bottomRight The physical bottom-right corner radius, never flipped by `LocalLayoutDirection`.
 * @param bottomLeft The physical bottom-left corner radius, never flipped by `LocalLayoutDirection`.
 * @param weight The superellipse exponent of the corners, clamped to
 *   [SquircleDefaults.WeightMin]..[SquircleDefaults.WeightMax].
 */
@Composable
fun Modifier.absoluteSquircleClip(
    topLeft: Dp,
    topRight: Dp,
    bottomRight: Dp,
    bottomLeft: Dp,
    weight: Float = SquircleDefaults.Weight,
): Modifier {
    val brush = rememberSquircleBrush(Color.White, topLeft, topRight, bottomRight, bottomLeft, weight)
        ?: return this.clip(AbsoluteRoundedCornerShape(topLeft, topRight, bottomRight, bottomLeft))
    return this.squircleShaderMask(brush, fillColor = null)
}

/**
 * Absolute-positioning variant of [squircleSurface]. See [absoluteSquircleBackground] for semantics.
 *
 * @param color The fill [Color] drawn behind the clipped content.
 * @param topLeft The physical top-left corner radius, never flipped by `LocalLayoutDirection`.
 * @param topRight The physical top-right corner radius, never flipped by `LocalLayoutDirection`.
 * @param bottomRight The physical bottom-right corner radius, never flipped by `LocalLayoutDirection`.
 * @param bottomLeft The physical bottom-left corner radius, never flipped by `LocalLayoutDirection`.
 * @param weight The superellipse exponent of the corners, clamped to
 *   [SquircleDefaults.WeightMin]..[SquircleDefaults.WeightMax].
 */
@Composable
fun Modifier.absoluteSquircleSurface(
    color: Color,
    topLeft: Dp,
    topRight: Dp,
    bottomRight: Dp,
    bottomLeft: Dp,
    weight: Float = SquircleDefaults.Weight,
): Modifier {
    val brush = rememberSquircleBrush(Color.White, topLeft, topRight, bottomRight, bottomLeft, weight)
        ?: return this
            .clip(AbsoluteRoundedCornerShape(topLeft, topRight, bottomRight, bottomLeft))
            .background(color)
    return this.squircleShaderMask(brush, fillColor = color)
}

/**
 * Shared squircle clip/surface mask for [squircleClip] / [squircleSurface] and their absolute
 * variants. Up to [MAX_OFFSCREEN_PX] the node composites through one [CompositingStrategy.Offscreen]
 * layer (cheap to cache and re-blit while scrolling) and only the corners are carved; past that a
 * full-node offscreen would overflow the GPU max texture and render transparent, so the content is
 * recorded and replayed per corner band instead — offscreen work tracks the corners, not the height.
 */
private fun Modifier.squircleShaderMask(
    brush: SquircleShaderBrush,
    fillColor: Color?,
): Modifier = this
    .graphicsLayer {
        compositingStrategy =
            if (maxOf(size.width, size.height) <= MAX_OFFSCREEN_PX) {
                CompositingStrategy.Offscreen
            } else {
                CompositingStrategy.Auto
            }
    }
    .drawWithCache {
        val layerPaint = Paint()
        val contentLayer = obtainGraphicsLayer()
        onDrawWithContent {
            if (maxOf(size.width, size.height) <= MAX_OFFSCREEN_PX) {
                drawSquircleFast(brush, fillColor)
            } else {
                contentLayer.record { this@onDrawWithContent.drawContent() }
                drawSquircleBanded(brush, fillColor, layerPaint, contentLayer)
            }
        }
    }

// Fast path: the enclosing Offscreen layer holds fill + content, so DstIn carves straight against it.
private fun ContentDrawScope.drawSquircleFast(
    brush: SquircleShaderBrush,
    fillColor: Color?,
) {
    if (fillColor != null) drawRect(fillColor)
    drawContent()
    maskSquircleCorners(brush, top = true, bottom = true)
}

// Oversize path: replay recorded content per corner band so no single offscreen exceeds the texture
// max; the straight middle draws with no offscreen at all.
private fun ContentDrawScope.drawSquircleBanded(
    brush: SquircleShaderBrush,
    fillColor: Color?,
    layerPaint: Paint,
    contentLayer: GraphicsLayer,
) {
    val width = size.width
    val height = size.height
    val canvas = drawContext.canvas
    val topBand = brush.topBandHeightPx(size)
    val bottomBand = brush.bottomBandHeightPx(size)

    // One offscreen slice: fill + content + its corner pair(s). [clip] bounds a banded slice.
    fun maskedBand(top: Float, bottom: Float, maskTop: Boolean, maskBottom: Boolean, clip: Boolean) {
        canvas.saveLayer(Rect(0f, top, width, bottom), layerPaint)
        if (clip) canvas.clipRect(0f, top, width, bottom)
        if (fillColor != null) drawRect(fillColor)
        drawLayer(contentLayer)
        maskSquircleCorners(brush, top = maskTop, bottom = maskBottom)
        canvas.restore()
    }

    // No straight middle: one offscreen for the whole (no-taller-than-wide) node.
    if (height <= topBand + bottomBand) {
        maskedBand(0f, height, maskTop = true, maskBottom = true, clip = false)
        return
    }

    // Offscreen only the corner bands; replay the straight middle directly. Whole-pixel edges tile.
    val midTop = ceil(topBand)
    val midBottom = floor(height - bottomBand)
    if (topBand > 0f) maskedBand(0f, midTop, maskTop = true, maskBottom = false, clip = true)
    clipRect(top = midTop, bottom = midBottom) {
        if (fillColor != null) drawRect(fillColor)
        drawLayer(contentLayer)
    }
    if (bottomBand > 0f) maskedBand(midBottom, height, maskTop = false, maskBottom = true, clip = true)
}

// Carve the corner regions (the shader is opaque elsewhere). Must run inside an offscreen already
// holding fill + content, so DstIn has an alpha channel to carve against.
private fun ContentDrawScope.maskSquircleCorners(
    brush: SquircleShaderBrush,
    top: Boolean,
    bottom: Boolean,
) {
    val width = size.width
    val height = size.height
    val canvas = drawContext.canvas
    fun corner(index: Int, alignRight: Boolean, alignBottom: Boolean) {
        val w = brush.cornerWidthPx(index, size)
        val h = brush.cornerHeightPx(index, size)
        if (w <= 0f || h <= 0f) return
        val left = if (alignRight) width - w else 0f
        val topPx = if (alignBottom) height - h else 0f
        canvas.save()
        canvas.clipRect(left, topPx, left + w, topPx + h)
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
        canvas.restore()
    }
    if (top) {
        corner(0, alignRight = false, alignBottom = false)
        corner(1, alignRight = true, alignBottom = false)
    }
    if (bottom) {
        corner(2, alignRight = true, alignBottom = true)
        corner(3, alignRight = false, alignBottom = true)
    }
}

@Composable
private fun rememberSquircleBrush(
    color: Color,
    topStart: Dp,
    topEnd: Dp,
    bottomEnd: Dp,
    bottomStart: Dp,
    weight: Float,
): SquircleShaderBrush? {
    if (!LocalSquircleEnabled.current || !isRuntimeShaderSupported()) return null
    val density = LocalDensity.current
    val clampedWeight = SuperellipseCorner.clampWeight(weight)
    val radii = remember(topStart, topEnd, bottomEnd, bottomStart, density) {
        with(density) {
            floatArrayOf(
                topStart.toPx().coerceAtLeast(0f),
                topEnd.toPx().coerceAtLeast(0f),
                bottomEnd.toPx().coerceAtLeast(0f),
                bottomStart.toPx().coerceAtLeast(0f),
            )
        }
    }
    return remember(color, radii, clampedWeight) {
        SquircleShaderBrush(color, radii, clampedWeight)
    }
}

/**
 * Fills the squircle silhouette with [color] through [SQUIRCLE_SHADER], an analytic signed distance
 * field of the Oplus smooth round rect: per corner `lpNorm(q, weight) - r`, the same Lp-norm the
 * libhwui G2 shader evaluates, so no distance-field texture is baked or sampled. A [weight] below 2
 * on a fully rounded rect switches to the G2 soft capsule blend (see [SoftCapsule]).
 */
internal class SquircleShaderBrush(
    private val color: Color,
    private val cornerRadiiPx: FloatArray,
    private val weight: Float,
) : ShaderBrush() {
    private val runtimeShader = RuntimeShader(SQUIRCLE_SHADER)
    private val effectiveRadii = FloatArray(4)

    override fun createShader(size: Size): Shader {
        for (i in 0..3) {
            effectiveRadii[i] = effectiveCornerPx(i, size)
        }
        runtimeShader.setColorUniform("color", color)
        runtimeShader.setFloatUniform("size", size.width, size.height)
        runtimeShader.setFloatUniform("radii", effectiveRadii)
        runtimeShader.setFloatUniform("weight", SuperellipseCorner.exponent(weight))
        runtimeShader.setFloatUniform("capsuleMix", capsuleMix(size))
        return runtimeShader.asComposeShader()
    }

    /**
     * The soft capsule blend factor for [size], or -1 when the superellipse corners apply: a weight
     * below 2 only counts on a fully rounded rect, as in `OplusPathSmooth.addSmoothRoundRect`.
     */
    fun capsuleMix(size: Size): Float {
        if (weight >= 2f) return -1f
        if (!SoftCapsule.isFullyRounded(cornerRadiiPx, size.width, size.height)) return -1f
        if (minOf(size.width, size.height) * 0.5f < SoftCapsule.MIN_RADIUS_PX) return -1f
        return SoftCapsule.capsuleMix(weight)
    }

    // Corner index order: 0 = topStart/topLeft, 1 = topEnd/topRight, 2 = bottomEnd/bottomRight, 3 = bottomStart/bottomLeft.
    fun topBandHeightPx(size: Size): Float = maxOf(cornerHeightPx(0, size), cornerHeightPx(1, size))

    fun bottomBandHeightPx(size: Size): Float = maxOf(cornerHeightPx(2, size), cornerHeightPx(3, size))

    // Corner radius the shader uses at [index].
    fun effectiveCornerPx(index: Int, size: Size): Float = SuperellipseCorner.effectiveRadius(cornerRadiiPx[index], size.width, size.height)

    // Width of the region the shader carves at corner [index]: the corner square, or for the soft
    // capsule the end cap plus the softened junction along a horizontal long axis.
    fun cornerWidthPx(index: Int, size: Size): Float {
        if (capsuleMix(size) < 0f) return effectiveCornerPx(index, size)
        val halfShort = minOf(size.width, size.height) * 0.5f
        return if (size.width > size.height) minOf(size.width * 0.5f, SoftCapsule.junctionReach(halfShort)) else size.width * 0.5f
    }

    // Height of the region the shader carves at corner [index]; see [cornerWidthPx].
    fun cornerHeightPx(index: Int, size: Size): Float {
        if (capsuleMix(size) < 0f) return effectiveCornerPx(index, size)
        val halfShort = minOf(size.width, size.height) * 0.5f
        return if (size.height > size.width) minOf(size.height * 0.5f, SoftCapsule.junctionReach(halfShort)) else size.height * 0.5f
    }
}

// Max node dimension composited in one offscreen layer. 2048 px is the texture-size floor guaranteed
// by GL ES 3.0 / WebGL2 / Metal, so it never overflows the GPU max texture; larger nodes go banded.
private const val MAX_OFFSCREEN_PX = 2048f

/**
 * The superellipse corner SDF. Inside a corner square, `q` is the offset from the corner center
 * towards the vertex and the boundary is `lpNorm(q, weight) == r` (Oplus `g2RoundRectCommonFill`
 * with `superN = weight`). The Lp-norm excess is divided by the norm's gradient length, turning it
 * into a Euclidean distance near the boundary, so the one-pixel anti-aliasing ramp keeps its width
 * along the whole curve.
 *
 * With `capsuleMix >= 0` (a weight below 2 on a fully rounded rect) the whole rect is evaluated as
 * the G2 `superN < 2` branch instead: `mix(sdRoundRect(n = 2), sdCapsule, capsuleMix)`, where
 * `sdCapsule` is the libhwui soft capsule (radius half the shorter side, smooth-max junction with
 * `kMax` 26.6 and junction bands 70 / 60 scaled by `r * 0.0125`, one pixel `kAbs`).
 */
internal const val SQUIRCLE_SHADER = """
uniform float2 size;
uniform float4 radii;
uniform float weight;
uniform float capsuleMix;
layout(color) uniform half4 color;

half4 main(float2 coord) {
    if (capsuleMix >= 0.0) {
        float2 halfSize = size * 0.5;
        float2 p = coord - halfSize;
        bool vertical = halfSize.x < halfSize.y;
        float2 hs = vertical ? halfSize.yx : halfSize;
        float2 pl = vertical ? p.yx : p;
        float rc = hs.y;
        float a = max(hs.x - rc, 0.0);
        float2 qc = sqrt(pl * pl + float2(1.0));
        float scale = rc * 0.0125;
        float kMax = max(26.6 * scale, 1e-4);
        float xBand = max(70.0 * scale, 1e-3);
        float yBand = max(60.0 * scale, 1e-3);
        float sc = qc.x - a;
        float xSoft = 0.5 * (sc + sqrt(sc * sc + kMax * kMax));
        float wx = 1.0 - smoothstep(0.0, xBand, abs(sc));
        float wy = smoothstep(rc - yBand, rc, qc.y);
        float xc = mix(max(sc, 0.0), xSoft, clamp(wx * wy, 0.0, 1.0));
        float dCapsule = length(float2(xc, qc.y)) - rc;
        float2 qr = float2(abs(pl.x) - a, abs(pl.y));
        float dRound = length(max(qr, float2(0.0))) + min(max(qr.x, qr.y), 0.0) - rc;
        float dc = mix(dRound, dCapsule, capsuleMix);
        return color * half(smoothstep(0.5, -0.5, dc));
    }
    float dxL = coord.x;
    float dxR = size.x - coord.x;
    float dyT = coord.y;
    float dyB = size.y - coord.y;
    bool left = dxL < dxR;
    bool top = dyT < dyB;
    float dx = left ? dxL : dxR;
    float dy = top ? dyT : dyB;
    // vec4 swizzle: AGSL forbids dynamic indexing. Channel order: x=topStart, y=topEnd, z=bottomEnd, w=bottomStart.
    float r = top ? (left ? radii.x : radii.y) : (left ? radii.w : radii.z);
    if (dx >= r || dy >= r) {
        return color;
    }
    float2 q = float2(r - dx, r - dy);
    float m = max(q.x, q.y);
    float2 u = q / m;
    float2 lu = log2(max(u, float2(1e-8)));
    float s = exp2(lu.x * weight) + exp2(lu.y * weight);
    float ls = log2(s);
    float norm = m * exp2(ls / weight);
    // |grad lpNorm| = |u^(n-1)| * s^((1-n)/n), between 2^(1/n-1/2) and 1.
    float2 grad = exp2(lu * (weight - 1.0)) * exp2(ls * (1.0 - weight) / weight);
    float dist = (norm - r) / max(length(grad), 0.5);
    return color * half(smoothstep(0.5, -0.5, dist));
}
"""
