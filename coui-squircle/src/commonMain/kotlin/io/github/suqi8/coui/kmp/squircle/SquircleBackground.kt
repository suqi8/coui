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
 *   [SquircleDefaults.WeightMin]..[SquircleDefaults.WeightMax].
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

// Carve the corner squares (the shader is opaque elsewhere). Must run inside an offscreen already
// holding fill + content, so DstIn has an alpha channel to carve against.
private fun ContentDrawScope.maskSquircleCorners(
    brush: SquircleShaderBrush,
    top: Boolean,
    bottom: Boolean,
) {
    val width = size.width
    val height = size.height
    val canvas = drawContext.canvas
    fun corner(left: Float, topPx: Float, side: Float) {
        if (side <= 0f) return
        canvas.save()
        canvas.clipRect(left, topPx, left + side, topPx + side)
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
        canvas.restore()
    }
    if (top) {
        corner(0f, 0f, brush.effectiveCornerPx(0, size))
        val tr = brush.effectiveCornerPx(1, size)
        corner(width - tr, 0f, tr)
    }
    if (bottom) {
        val br = brush.effectiveCornerPx(2, size)
        corner(width - br, height - br, br)
        val bl = brush.effectiveCornerPx(3, size)
        corner(0f, height - bl, bl)
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
    val exponent = SuperellipseCorner.clampWeight(weight)
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
    return remember(color, radii, exponent) {
        SquircleShaderBrush(color, radii, exponent)
    }
}

/**
 * Fills the squircle silhouette with [color] through [SQUIRCLE_SHADER], an analytic signed distance
 * field of the Oplus smooth round rect: per corner `lpNorm(q, weight) - r`, the same Lp-norm the
 * libhwui G2 shader evaluates, so no distance-field texture is baked or sampled.
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
        runtimeShader.setFloatUniform("weight", weight)
        return runtimeShader.asComposeShader()
    }

    // Corner index order: 0 = topStart/topLeft, 1 = topEnd/topRight, 2 = bottomEnd/bottomRight, 3 = bottomStart/bottomLeft.
    fun topBandHeightPx(size: Size): Float = maxOf(effectiveCornerPx(0, size), effectiveCornerPx(1, size))

    fun bottomBandHeightPx(size: Size): Float = maxOf(effectiveCornerPx(2, size), effectiveCornerPx(3, size))

    // Corner radius the shader uses at [index]: the square of that side is the only area it carves.
    fun effectiveCornerPx(index: Int, size: Size): Float = SuperellipseCorner.effectiveRadius(cornerRadiiPx[index], size.width, size.height)
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
 */
internal const val SQUIRCLE_SHADER = """
uniform float2 size;
uniform float4 radii;
uniform float weight;
layout(color) uniform half4 color;

half4 main(float2 coord) {
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
