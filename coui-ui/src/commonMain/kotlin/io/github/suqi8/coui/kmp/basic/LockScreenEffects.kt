// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Stable
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.shadow.ShadowContext
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.shader.isRenderEffectSupported
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.min

/*
 * The lock-screen "glass" effects shared by NumericKeyboard (press effect `pressLight`) and
 * LockScreenPasswordInput (desktop scenes mode). Both COUI widgets ship their own copy of the same
 * two helpers: `com.coui.appcompat.lockview.InnerShadowHelper` / `LightEffectHelper` and
 * `com.coui.appcompat.input.InnerShadowHelper` / `LightEffectHelper` (identical code), fed with
 * the same constants and the same white-on-wallpaper colors.
 */

/**
 * One `InnerShadowHelper.addInnerShadowLayer` call: a transparent stroke of [strokeWidth] along
 * the outline whose `setShadowLayer(blurRadius, 0, dy, color)` shadow is kept inside the outline.
 * Every length is in raw pixels, as in COUI (`INNER_SHADOW_*` constants, not dp-scaled).
 */
internal class LockInnerShadowLayer(
    val blurRadius: Float,
    val dy: Float,
    val strokeWidth: Float,
    val color: Color,
)

/**
 * The two inner-shadow layers of `COUINumericKeyboard.initInnerShadowBitmap` and
 * `COUILockScreenPwdInputLayout/View.initInnerShadowBitmap`: (32px blur, dy -8px, 20px stroke,
 * `coui_*_upper_inner_shadow_color` #5CFFFFFF) and (8px blur, dy 2px, 12px stroke,
 * `coui_*_lower_inner_shadow_color` #33FFFFFF).
 */
internal val LockInnerShadowLayers = listOf(
    LockInnerShadowLayer(blurRadius = 32f, dy = -8f, strokeWidth = 20f, color = Color(0x5CFFFFFF)),
    LockInnerShadowLayer(blurRadius = 8f, dy = 2f, strokeWidth = 12f, color = Color(0x33FFFFFF)),
)

/** `coui_numeric_keyboard_border_color` / `coui_input_lock_screen_border_color`. */
internal val LockBorderColor = Color(0x0DFFFFFF)

/**
 * `coui_keyboard_button_border_width` / `coui_input_lock_screen_border_width` (0.6dp). COUI strokes
 * twice this width and clips to the outline, so 0.6dp stays visible inside.
 */
internal const val LOCK_BORDER_WIDTH_DP = 0.6f

/** `coui_*_outer_gradient_color_1..3`, at `GRADIENT_OUTER_STOP_1..3` (0.3 / 0.6 / 0.8). */
private val OuterGradientStops = arrayOf(
    0f to Color.Transparent,
    0.3f to Color(0x80FFFFFF),
    0.6f to Color(0x33FFFFFF),
    0.8f to Color(0x0DFFFFFF),
    1f to Color.Transparent,
)

/** `coui_*_inner_gradient_color_1..2`, at `GRADIENT_INNER_STOP_1` (0.7) and 1. */
private val InnerGradientStops = arrayOf(
    0f to Color.Transparent,
    0.7f to Color(0x8FFFFFFF),
    1f to Color(0xE6FFFFFF),
)

/**
 * A recorded [LockInnerShadowLayer], drawn at its outline's top left minus [pad] pixels:
 * - [layer]: the stroke recorded into a blurred [GraphicsLayer] padded by [pad] on every side, so
 *   the blur of the outer half of the stroke still reaches inside;
 * - [painter] (where render effects are unavailable, Android before 12, on which a
 *   [BlurEffect] is silently dropped and the bare stroke would show as a hard ring): a drop shadow
 *   of the stroke band, drawn at [painterSize] with [pad] the half stroke width.
 */
internal class RecordedInnerShadow(
    val layer: GraphicsLayer?,
    val painter: Painter?,
    val painterSize: Size,
    val pad: Float,
)

/**
 * Records [LockInnerShadowLayers] for an outline of [size] built by [outline] (at the origin).
 * Call from `drawWithCache` so the blur is only recorded again when the size changes.
 *
 * [outline] must be a capsule (a circle included): the blur-mask fallback builds the stroke band
 * of a capsule itself.
 */
internal fun CacheDrawScope.recordLockInnerShadow(
    size: Size,
    shadowContext: ShadowContext,
    outline: (Size) -> Path,
): List<RecordedInnerShadow> {
    if (!isRenderEffectSupported()) return LockInnerShadowLayers.map { recordFallbackInnerShadow(size, shadowContext, it) }
    val path = outline(size)
    return LockInnerShadowLayers.map { spec ->
        val pad = ceil(spec.blurRadius * 2f + spec.strokeWidth + abs(spec.dy))
        val layer = obtainGraphicsLayer()
        layer.renderEffect = BlurEffect(spec.blurRadius, spec.blurRadius, TileMode.Decal)
        layer.record(size = IntSize(ceil(size.width + pad * 2f).toInt(), ceil(size.height + pad * 2f).toInt())) {
            translate(pad, pad + spec.dy) {
                drawPath(path = path, color = spec.color, style = Stroke(width = spec.strokeWidth))
            }
        }
        RecordedInnerShadow(layer = layer, painter = null, painterSize = Size.Zero, pad = pad)
    }
}

/**
 * The blur-mask fallback of one [LockInnerShadowLayer]: COUI's `setShadowLayer` on a transparent
 * stroke is exactly the blurred, offset stroke band, which is what a drop shadow of the band shape
 * draws (Compose renders it with a blur mask filter on every API level, and converts the radius to
 * a sigma the same way `setShadowLayer` does).
 */
private fun CacheDrawScope.recordFallbackInnerShadow(
    size: Size,
    shadowContext: ShadowContext,
    spec: LockInnerShadowLayer,
): RecordedInnerShadow {
    val shadow = Shadow(
        radius = spec.blurRadius.toDp(),
        color = spec.color,
        offset = DpOffset(0.dp, spec.dy.toDp()),
    )
    return RecordedInnerShadow(
        layer = null,
        painter = shadowContext.createDropShadowPainter(CapsuleStrokeBand(spec.strokeWidth), shadow),
        painterSize = Size(size.width + spec.strokeWidth, size.height + spec.strokeWidth),
        pad = spec.strokeWidth / 2f,
    )
}

/**
 * The band a [strokeWidth] stroke along a capsule covers, in a frame grown by half the stroke on
 * every side: the frame capsule minus the capsule inset by the whole stroke.
 */
private data class CapsuleStrokeBand(val strokeWidth: Float) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path().apply {
            fillType = PathFillType.EvenOdd
            addRoundRect(capsule(Rect(Offset.Zero, size)))
            if (size.width > strokeWidth * 2f && size.height > strokeWidth * 2f) {
                addRoundRect(capsule(Rect(strokeWidth, strokeWidth, size.width - strokeWidth, size.height - strokeWidth)))
            }
        }
        return Outline.Generic(path)
    }
}

private fun capsule(rect: Rect): RoundRect = RoundRect(rect, CornerRadius(min(rect.width, rect.height) / 2f))

/**
 * Draws recorded inner shadows inside [clip] (the outline at its final position and scale),
 * translated to [topLeft] and scaled by [scale] around it like COUI's `mInnerShadowMatrix`
 * (`postScale(scale)` then `postTranslate`), with [alpha] as the bitmap paint alpha.
 */
internal fun DrawScope.drawLockInnerShadow(
    shadows: List<RecordedInnerShadow>,
    clip: Path,
    topLeft: Offset,
    scale: Float = 1f,
    alpha: Float = 1f,
) {
    if (alpha <= 0f) return
    clipPath(clip) {
        shadows.forEach { shadow ->
            translate(topLeft.x - shadow.pad * scale, topLeft.y - shadow.pad * scale) {
                scale(scale, scale, pivot = Offset.Zero) {
                    if (shadow.layer != null) {
                        shadow.layer.alpha = alpha
                        drawLayer(shadow.layer)
                    } else if (shadow.painter != null) {
                        with(shadow.painter) { draw(shadow.painterSize, alpha = alpha) }
                    }
                }
            }
        }
    }
}

/**
 * The COUI inner border: a [LockBorderColor] stroke of twice [LOCK_BORDER_WIDTH_DP] along [path],
 * clipped to it.
 */
internal fun DrawScope.drawLockInnerBorder(path: Path, color: Color = LockBorderColor, alpha: Float = 1f, blendMode: BlendMode = BlendMode.SrcOver) {
    if (alpha <= 0f) return
    clipPath(path) {
        drawPath(
            path = path,
            color = color,
            alpha = alpha,
            style = Stroke(width = LOCK_BORDER_WIDTH_DP.dp.toPx() * 2f),
            blendMode = blendMode,
        )
    }
}

/**
 * `LightEffectHelper.drawLightEffect`, with [center] as the light origin:
 * - the outer light fills [lightPath] with a radial gradient of [lightRadius] scaled by
 *   `0.6 + 0.4 * outerAlpha` (`mOuterLightRadiusStart = 0.6 * radius`), at [outerAlpha];
 * - the inner light fills the pressed circle ([innerRadius] x [scale]) with a ring gradient, at
 *   [innerAlpha].
 * Both use `BlendMode.LIGHTEN`.
 */
internal fun DrawScope.drawLockLightEffect(
    center: Offset,
    lightPath: Path,
    lightRadius: Float,
    innerRadius: Float,
    scale: Float,
    innerAlpha: Float,
    outerAlpha: Float,
) {
    if (outerAlpha > 0f && lightRadius > 0f) {
        val radius = lightRadius * (0.6f + 0.4f * outerAlpha)
        drawPath(
            path = lightPath,
            brush = Brush.radialGradient(colorStops = OuterGradientStops, center = center, radius = radius),
            alpha = outerAlpha.coerceIn(0f, 1f),
            blendMode = BlendMode.Lighten,
        )
    }
    val innerScaled = innerRadius * scale
    if (innerAlpha > 0f && innerScaled > 0f) {
        drawCircle(
            brush = Brush.radialGradient(colorStops = InnerGradientStops, center = center, radius = innerScaled),
            radius = innerScaled,
            center = center,
            alpha = innerAlpha.coerceIn(0f, 1f),
            blendMode = BlendMode.Lighten,
        )
    }
}

/**
 * The `LightEffectHelper` springs, as fractions of COUI's 0..255 alpha: the inner light runs on
 * response 0.3 both ways; the outer light on 0.3 while pressing and 0.6 when released. Bounce 0
 * (damping ratio 1), stiffness `(2π / response)²`.
 */
@Stable
internal class LockLightEffectState {
    val inner = Animatable(0f)
    val outer = Animatable(0f)

    suspend fun animate(pressed: Boolean) = coroutineScope {
        val target = if (pressed) 1f else 0f
        launch { inner.animateTo(target, LightSpringFast) }
        launch { outer.animateTo(target, if (pressed) LightSpringFast else LightSpringSlow) }
    }
}

private val LightSpringFast = spring<Float>(dampingRatio = 1f, stiffness = 438.65f)
private val LightSpringSlow = spring<Float>(dampingRatio = 1f, stiffness = 109.66f)
