// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import io.github.suqi8.coui.kmp.shader.RuntimeShader
import io.github.suqi8.coui.kmp.shader.asBrush
import kotlin.math.abs
import kotlin.math.max

/**
 * Links a [SearchBar] to the toolbar search entry it expands out of, after COUI 17
 * `COUISearchBar.showInToolBar`. Provided by [ToolbarFloatingSearchBar].
 *
 * @property progress the icon expansion progress, `mIconExpandCenterAnimationRange / 1000`: 0 is
 *   the bar folded into the toolbar icon, 1 the bar at its edit-state size and place.
 * @property sourceCenterX the horizontal centre of the toolbar search icon in root pixels, `NaN`
 *   until it has been measured.
 * @property interactive whether the bar takes touches. A collapsing bar sits behind the toolbar
 *   children in COUI (`updateLayerInToolBar(false)`), so its capsule and buttons stay still.
 */
@Stable
internal class SearchBarIconExpand(
    val progress: () -> Float,
    val sourceCenterX: () -> Float,
    val interactive: () -> Boolean,
)

internal val LocalSearchBarIconExpand = staticCompositionLocalOf<SearchBarIconExpand?> { null }

/**
 * What the [InputField] inside a [SearchBar] tells the bar: whether its capsule wears the glass
 * material (`COUISearchBar.setBallChain` only chains blurred material views) and its fallback fill.
 */
@Stable
internal class SearchBarCapsuleInfo {
    var material by mutableStateOf(false)
    var fallbackColor by mutableStateOf(Color.Unspecified)
}

internal val LocalSearchBarCapsuleInfo = staticCompositionLocalOf<SearchBarCapsuleInfo?> { null }

/** Measured positions a [SearchBar] needs at draw time; written during layout. */
@Stable
internal class SearchBarGeometry {
    /** The row's content origin in root pixels. */
    var rowRootX by mutableFloatStateOf(Float.NaN)

    /** The input slot within the row (capsule plus its vertical margin). */
    var inputRect = Rect.Zero

    /** The action button's untransformed bounds within its slot. */
    var actionLocalRect = Rect.Zero

    /** The action slot's origin within the row. */
    var actionSlotOrigin = Offset.Zero

    /** The action button's untransformed bounds within the row. */
    val actionRect: Rect get() = actionLocalRect.translate(actionSlotOrigin)
}

/**
 * The cancel-button transform during a toolbar icon expansion, `COUISearchBar.funBtnAnimateIconExpand`:
 * hidden up to 5%, then scale `initScale + (1 - initScale) * p` and alpha `p`.
 */
internal fun iconExpandActionScale(progress: Float): Float = if (progress <= IconExpandActionHiddenProgress) {
    0f
} else {
    IconExpandActionInitScale + (1f - IconExpandActionInitScale) * progress
}

/** See [iconExpandActionScale]. */
internal fun iconExpandActionAlpha(progress: Float): Float = if (progress <= IconExpandActionHiddenProgress) 0f else progress.coerceIn(0f, 1f)

/**
 * The capsule's visible width during a toolbar icon expansion: `SearchBarBackgroundView
 * .updateIconExpandClippingWidth(h, W, max(h, h + (W - h) * p))`, a circle of the capsule height
 * growing into the full capsule.
 */
internal fun iconExpandCapsuleWidth(height: Float, fullWidth: Float, progress: Float): Float = max(height, height + (fullWidth - height) * progress)

/**
 * The capsule blur during a toolbar icon expansion, `updateBlurWithAnimationProgress(background,
 * 1 - |(1 - p) - 0.5| * 2)` scaled by 30: in raw pixels, 0 at both ends and 30 half way.
 */
internal fun iconExpandCapsuleBlurPx(progress: Float): Float = max(1f - abs((1f - progress) - 0.5f) * 2f, 0f) * IconExpandBlurPx

/** The cancel-button blur during a toolbar icon expansion: `30 * (1 - p)` raw pixels. */
internal fun iconExpandActionBlurPx(progress: Float): Float = max(1f - progress, 0f) * IconExpandBlurPx

/**
 * `IconExpandToEditParamsHigh.functionalButtonInitScale` and
 * `IconCollapseToNormalParamsHigh.functionalButtonEndScale`, both 0.6.
 */
private val IconExpandActionInitScale = 0.6f

/** `funBtnAnimateIconExpand`: the button stays collapsed and transparent up to this progress. */
private val IconExpandActionHiddenProgress = 0.05f

/** `updateBlurWithAnimationProgress`: the blur radius scale, in raw pixels. */
private val IconExpandBlurPx = 30f

/**
 * The metaball merge `COUISearchBar.setBallChain` asks the framework for between the search capsule
 * (ball 1) and the functional button (ball 2), `OplusMaterialMetaBallParams(index, MODE_X, 4dp,
 * 1.5, 2.0)`. The framework renders it in libhwui's `getRoundedMetaballSDF`; this keeps its core,
 * `blendPair`'s polynomial smooth minimum over the two rounded-rect distances at
 * `BLEND_STRENGTH` (25px), and draws only the neck the merge adds outside both shapes, since each
 * shape already draws its own material. The per-pair precompute that modulates the strength and
 * stretches the shapes toward each other (`pre[24]`) is native code that is not recoverable from
 * the shader text, so the strength stays constant here.
 */
internal object SearchBarMetaballShader {

    /** libhwui `BLEND_STRENGTH`, in raw pixels. */
    val BlendStrengthPx = 25f

    val Source: String = """
        uniform float4 u_rect1;
        uniform float4 u_rect2;
        uniform float u_k;
        uniform half4 u_color;

        float sdCapsuleRect(float2 p, float4 r) {
            float2 c = (r.xy + r.zw) * 0.5;
            float2 hs = (r.zw - r.xy) * 0.5;
            float radius = min(hs.x, hs.y);
            float2 q = abs(p - c) - hs + radius;
            return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - radius;
        }

        half4 main(float2 p) {
            float d1 = sdCapsuleRect(p, u_rect1);
            float d2 = sdCapsuleRect(p, u_rect2);
            float h = clamp(0.5 + 0.5 * (d1 - d2) / u_k, 0.0, 1.0);
            float blended = mix(d1, d2, h) - u_k * h * (1.0 - h);
            float neck = clamp(0.5 - blended, 0.0, 1.0) * clamp(min(d1, d2) + 0.5, 0.0, 1.0);
            return u_color * half(neck);
        }
    """.trimIndent()

    /**
     * Draws the neck between [capsule] and [action] (both in this scope's pixels) at [alpha], as the
     * glass filters when [filters] is non-null (pairs of colour and blend mode, drawn with one
     * shader each), else as a flat [fallback] fill.
     */
    fun DrawScope.drawNeck(
        shaders: List<RuntimeShader>,
        capsule: Rect,
        action: Rect,
        alpha: Float,
        filters: List<Pair<Color, BlendMode>>?,
        fallback: Color,
    ) {
        if (alpha <= 0f || capsule.isEmpty || action.isEmpty) return
        val layers = filters ?: listOf(fallback to BlendMode.SrcOver)
        layers.forEachIndexed { index, (color, blendMode) ->
            val shader = shaders.getOrNull(index) ?: return
            shader.setFloatUniform("u_rect1", capsule.left, capsule.top, capsule.right, capsule.bottom)
            shader.setFloatUniform("u_rect2", action.left, action.top, action.right, action.bottom)
            shader.setFloatUniform("u_k", BlendStrengthPx)
            val a = color.alpha * alpha
            shader.setFloatUniform("u_color", color.red * a, color.green * a, color.blue * a, a)
            drawRect(brush = shader.asBrush(), blendMode = blendMode)
        }
    }
}
