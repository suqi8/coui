// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.shader.isRenderEffectSupported
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.min

/*
 * The camera's edge light, `ap.c` InnerShadowEffect: a rounded rect grown by a stretch is stroked
 * with a transparent paint whose `setShadowLayer(blur, dx, dy, white @ alpha)` shadow, blended with
 * OVERLAY or PLUS, bleeds into the view that clips to its outline. The result is a thin light rim
 * just inside the edge, brighter where the shadow is pushed in by its offset.
 */

/**
 * One `InnerShadowEffect.a` layer. [blurRadiusPx] is raw pixels, as the camera passes it to
 * `setShadowLayer`; the other lengths are the camera's dimensions.
 */
@Immutable
internal class CameraInnerShadowLayer(
    val blendMode: BlendMode,
    val alpha: Float,
    val strokeWidth: Dp,
    val blurRadiusPx: Float,
    val color: Color,
    val dx: Dp,
    val dy: Dp,
)

/** An `InnerShadowEffectFactory` preset: the outline stretch and its layers. */
@Immutable
internal class CameraInnerShadowSpec(
    val stretchX: Dp,
    val stretchY: Dp,
    val layers: List<CameraInnerShadowLayer>,
)

/** The `InnerShadowEffectFactory.a(type)` presets. */
internal object CameraInnerShadows {
    /**
     * Type 1 (`ap.a`), the dark panels (`BottomEffectContainer` background): stretch
     * `bottom_menu_dark_color_shadow_effect_stretch_dx` 0.3333dp; OVERLAY white 0.3 (1dp stroke, 3px
     * blur, dy 0.6667dp) and PLUS white 0.05 (0.6667dp stroke, 1px blur, dy -0.6667dp).
     */
    val Panel = CameraInnerShadowSpec(
        stretchX = 0.3333.dp,
        stretchY = 0.3333.dp,
        layers = listOf(
            CameraInnerShadowLayer(BlendMode.Overlay, 0.3f, 1.dp, 3f, Color.White, 0.dp, 0.6667.dp),
            CameraInnerShadowLayer(BlendMode.Plus, 0.05f, 0.6667.dp, 1f, Color.White, 0.dp, (-0.6667).dp),
        ),
    )

    /**
     * Type 2 (`ap.f`), an unselected round menu button (`BottomMenuIcon`): stretch 1.3333dp; PLUS
     * white 0.5 (1.3333dp stroke, 3px blur, dx -0.3333dp, dy 1.3333dp), 0.25 (1.6667dp, 3px, dy -1dp)
     * and 0.1 (7.3333dp, 30px, dy -4.6667dp).
     */
    val MenuIcon = CameraInnerShadowSpec(
        stretchX = 1.3333.dp,
        stretchY = 1.3333.dp,
        layers = listOf(
            CameraInnerShadowLayer(BlendMode.Plus, 0.5f, 1.3333.dp, 3f, Color.White, (-0.3333).dp, 1.3333.dp),
            CameraInnerShadowLayer(BlendMode.Plus, 0.25f, 1.6667.dp, 3f, Color.White, 0.dp, (-1).dp),
            CameraInnerShadowLayer(BlendMode.Plus, 0.1f, 7.3333.dp, 30f, Color.White, 0.dp, (-4.6667).dp),
        ),
    )

    /**
     * Type 3 (`ap.e`), a selected round menu button: no stretch; OVERLAY #FFD39E (-11362) 0.35
     * (7.6667dp stroke, 20px blur, dy -1.3333dp) and 1.0 (0.6667dp, 3px, dy 0).
     */
    val MenuIconSelected = CameraInnerShadowSpec(
        stretchX = 0.dp,
        stretchY = 0.dp,
        layers = listOf(
            CameraInnerShadowLayer(BlendMode.Overlay, 0.35f, 7.6667.dp, 20f, Color(0xFFFFD39E), 0.dp, (-1.3333).dp),
            CameraInnerShadowLayer(BlendMode.Overlay, 1f, 0.6667.dp, 3f, Color(0xFFFFD39E), 0.dp, 0.dp),
        ),
    )

    /**
     * Type 4 (`ap.g`), the shutter: stretch 0.6667 x 1.3333dp; OVERLAY white 0.9 (1.3333dp stroke,
     * 10px blur, dy -1.6667dp).
     */
    val Shutter = CameraInnerShadowSpec(
        stretchX = 0.6667.dp,
        stretchY = 1.3333.dp,
        layers = listOf(
            CameraInnerShadowLayer(BlendMode.Overlay, 0.9f, 1.3333.dp, 10f, Color.White, 0.dp, (-1.6667).dp),
        ),
    )
}

/**
 * Draws a [CameraInnerShadowSpec] behind the content, clipped to a rounded rect of [cornerRadius]
 * filling the element (coerced to a capsule). Needs render effects (Android 12+ and every Skia
 * target); elsewhere it is skipped, like the camera on devices without `g1.l()` support.
 */
internal fun Modifier.cameraInnerShadow(
    spec: CameraInnerShadowSpec,
    cornerRadius: Dp,
    enabled: Boolean = true,
): Modifier = if (!enabled || !isRenderEffectSupported()) {
    this
} else {
    drawWithCache {
        val radius = min(cornerRadius.toPx(), min(size.width, size.height) / 2f)
        val sx = spec.stretchX.toPx()
        val sy = spec.stretchY.toPx()
        val clip = Path().apply {
            addRoundRect(RoundRect(0f, 0f, size.width, size.height, CornerRadius(radius)))
        }
        val stretched = Path().apply {
            addRoundRect(RoundRect(-sx, -sy, size.width + sx, size.height + sy, CornerRadius(radius)))
        }
        val layers = spec.layers.map { layer ->
            val stroke = layer.strokeWidth.toPx()
            val dx = layer.dx.toPx()
            val dy = layer.dy.toPx()
            val pad = ceil(layer.blurRadiusPx * 2f + stroke + sx + sy + abs(dx) + abs(dy))
            obtainGraphicsLayer().apply {
                renderEffect = BlurEffect(layer.blurRadiusPx, layer.blurRadiusPx, TileMode.Decal)
                blendMode = layer.blendMode
                record(size = IntSize(ceil(size.width + pad * 2f).toInt(), ceil(size.height + pad * 2f).toInt())) {
                    translate(pad + dx, pad + dy) {
                        drawPath(path = stretched, color = layer.color.copy(alpha = layer.alpha), style = Stroke(width = stroke))
                    }
                }
            } to pad
        }
        onDrawBehind {
            clipPath(clip) {
                layers.forEach { (graphicsLayer, pad) ->
                    translate(-pad, -pad) { drawLayer(graphicsLayer) }
                }
            }
        }
    }
}
