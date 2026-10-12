// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.effect

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * COUI 17 unified material effect, replicating `COUIMaterialEffect` + `ToolbarMaterialEffectDelegate`
 * + `libhwui.so` material shader (material_shader.glsl) in Compose.
 *
 * All components (icon buttons, top app bar glow, segmented controls, cards) share this single
 * pipeline: panel + stroke + shadow + spotlight + caustic + press animation, instead of each
 * implementing its own.
 */
object COUIMaterialEffect {

    /**
     * Applies the COUI 17 material panel to a component: material fill + edge lighting + inner
     * shadow + spotlight + press scale, drawn with Compose Canvas.
     *
     * `ToolbarMaterialEffectDelegate` builds a top-bar button out of a **transparent** view
     * background (`ColorDrawable(0)`) plus a backdrop filter — a backing blur with two colour
     * layers (`#66333333` COLOR_DODGE + `#99F5F5F5` LUMINOSITY light, `#CC262626` SRC_OVER +
     * `#B37F7F7F` OVERLAY dark). Compose has no backdrop filter on a plain `Modifier`, so the two
     * colour layers are approximated by [panelColor]: a light translucent wash, which reads the
     * same as the source's colour-dodge over a light bar.
     *
     * @param panelColor The material fill colour. Defaults to the COUI 17 top-bar material wash
     *   ([materialColor]), not the `#FFF0F0F0` blur-window panel.
     * @param size The component size (e.g. 48dp for a top-bar icon button).
     * @param cornerRadius The corner radius (e.g. 24dp for a 48dp circle).
     * @param interactionSource The interaction source driving the press state.
     * @param enabled Whether the material effect is active.
     */
    @Composable
    fun Modifier.materialPanel(
        interactionSource: MutableInteractionSource,
        panelColor: Color = materialColor(),
        size: Dp = 48.dp,
        cornerRadius: Dp = 24.dp,
        enabled: Boolean = true,
    ): Modifier {
        if (!enabled) return this

        val pressed by interactionSource.collectIsPressedAsState()
        val isDark = isSystemInDarkTheme()
        // PressScaleTuning: t = (clamp(wh, 20dp^2, 150dp^2) - 20) / 130, scale = 0.85 + B(t) * 0.12
        // with B = cubic-bezier(0, 0, 0, 1). A 48dp circle lands on ~0.900.
        val pressScale = remember(size) { pressTargetScale(size) }
        val scale by animateFloatAsState(
            targetValue = if (pressed) pressScale else 1f,
            // COUIPressAnimatorUnit press spring: bounce 0, response 0.25
            // -> stiffness = (2pi / 0.25)^2 = 631.65, damping ratio 1.
            animationSpec = spring(dampingRatio = 1f, stiffness = 631.65f),
            label = "materialPanelPressScale",
        )
        val spotlightColor = if (isDark) Color(0xFF7F7F7F) else Color(0xFFF5F5F5)

        return this
            .drawWithContent {
                val sizePx = size.toPx()
                val cornerRadiusPx = cornerRadius.toPx()
                val center = this.size.center

                // 1. Capsule SDF mask (material_shader.glsl sdCapsule)
                val capsulePath = capsulePath(sizePx, cornerRadiusPx)
                clipPath(capsulePath) {
                    // 2. Material fill
                    drawRect(color = panelColor)

                    // 3. Spotlight (SpotLightShader TYPE_OPAQUE_SMALL_2)
                    drawSpotlight(
                        center = center,
                        radius = sizePx / 2f,
                        glowColor = spotlightColor,
                        spotlightColor = spotlightColor,
                        pressed = pressed,
                    )

                    // 4. Inner shadow: the white inner glow of ShadowEdge.java's blendShadow
                    // (a light rim along the bottom inner edge).
                    drawInnerGlow(capsulePath, sizePx, isDark)
                }

                // 5. Edge lighting (ShadowEdge.java blendEdge): two inner bands running around the
                // perimeter, brightest at one light point and fading toward the opposite side.
                drawEdgeLighting(capsulePath, sizePx, isDark)
            }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    }

    /**
     * COUI 17 top-bar material fill: the effect of `COUIMaterialBlurEffect`'s two colour layers over
     * a transparent button on the bar — a translucent light wash (light) or a translucent dark
     * wash (dark). Not the `#FFF0F0F0` blur-window panel, which only applies inside dialogs.
     */
    @Composable
    fun materialColor(): Color = if (isSystemInDarkTheme()) Color(0x33FFFFFF) else Color(0x1FFFFFFF)

    /**
     * COUI 17 `PressPhysicsEngine.calculateTargetScaleBreakdown`: the press target scale for a
     * view of [size] (square), from the 20dp²–150dp² area mapping and the `cubic-bezier(0,0,0,1)`
     * shaping curve. A 48dp circle returns ~0.900.
     */
    fun pressTargetScale(size: Dp): Float {
        val area = size.value * size.value
        val t = ((area - 20f) / 130f).coerceIn(0f, 1f)
        return 0.85f + bezier0001(t) * 0.12f
    }

    /** Cubic bezier (0,0,0,1) evaluated as y at parameter [t], matching COUI's press interpolator. */
    private fun bezier0001(t: Float): Float {
        var lo = 0f
        var hi = 1f
        repeat(24) {
            val mid = (lo + hi) / 2f
            if (cubicBezierX(mid, 0f, 0f, 1f) < t) lo = mid else hi = mid
        }
        return cubicBezierY((lo + hi) / 2f, 0f, 0f, 1f)
    }

    private fun cubicBezierX(t: Float, x1: Float, x2: Float, x3: Float): Float {
        val u = 1f - t
        return 3f * u * u * t * x1 + 3f * u * t * t * x2 + t * t * t * x3
    }

    private fun cubicBezierY(t: Float, y1: Float, y2: Float, y3: Float): Float {
        val u = 1f - t
        return 3f * u * u * t * y1 + 3f * u * t * t * y2 + t * t * t * y3
    }

    /** COUI 17 material_shader.glsl sdCapsule: capsule SDF path. */
    fun capsulePath(width: Float, cornerRadius: Float): Path {
        val path = Path()
        val r = cornerRadius
        val w = width
        val h = width // circular button: width == height
        path.moveTo(r, 0f)
        path.lineTo(w - r, 0f)
        path.arcTo(
            rect = androidx.compose.ui.geometry.Rect(w - 2 * r, 0f, w, 2 * r),
            startAngleDegrees = -90f,
            sweepAngleDegrees = 180f,
            forceMoveTo = false,
        )
        path.lineTo(r, h)
        path.arcTo(
            rect = androidx.compose.ui.geometry.Rect(0f, h - 2 * r, 2 * r, h),
            startAngleDegrees = 90f,
            sweepAngleDegrees = 180f,
            forceMoveTo = false,
        )
        path.close()
        return path
    }

    /**
     * COUI 17 edge lighting (`ShadowEdge.java` `blendEdge` + `GradientStrokeStylePresets`): the
     * stroke is **not** a uniform ring. It is two inner bands whose alpha sweeps around the
     * perimeter — brightest near one light point, fading toward the opposite side — which is what
     * makes a COUI button look lit rather than outlined.
     *
     * The light point sits at the bottom-centre (`ratio = frac(angle + hw / (4(hw + hh)) + 0.5)`
     * with `angle = 0`), so the bottom edge carries the bright near band and the top edge the faint
     * far band. Both bands are approximated here by sweeping a `sweepGradient` from the bottom
     * around the shape.
     *
     * @param path The capsule path to stroke.
     * @param sizePx The button size in pixels.
     * @param isDark Whether the dark scheme is active (higher edge alpha: 0.6 vs 0.2).
     */
    fun DrawScope.drawEdgeLighting(path: Path, sizePx: Float, isDark: Boolean) {
        // StrokeEffectParams CIRCLE_1: edgeAlpha 0.2 light / 0.6 dark, edgeWidthDp 1.9.
        val edgeAlpha = if (isDark) 0.6f else 0.2f
        val lineWidthPx = 1.9.dp.toPx()
        // rectLineFade preset (circle / EDGE_STYLE_LIGHT_1): near band alpha 0.7, far band 1.0,
        // fade fractions (0.06, 0.4, 0.01, 0.28).
        val nearAlpha = 0.7f * edgeAlpha
        val farAlpha = 1.0f * edgeAlpha
        val center = Offset(sizePx / 2f, sizePx / 2f)
        val radius = sizePx / 2f

        // Near band: bright at the bottom light point, sweeping to the fade end 40% of the way round.
        drawPath(
            path = path,
            brush = Brush.sweepGradient(
                0.0f to Color.White.copy(alpha = farAlpha * 0.01f),
                0.25f to Color.White.copy(alpha = farAlpha * 0.28f),
                0.5f to Color.White.copy(alpha = nearAlpha),
                0.75f to Color.White.copy(alpha = farAlpha * 0.28f),
                1.0f to Color.White.copy(alpha = farAlpha * 0.01f),
                center = center,
            ),
            style = Stroke(width = lineWidthPx),
        )
        // Far band: a second, narrower ring slightly inside the first, alpha 1.0 at the lit side.
        drawPath(
            path = path,
            brush = Brush.sweepGradient(
                0.0f to Color.White.copy(alpha = 0f),
                0.5f to Color.White.copy(alpha = edgeAlpha * 0.4f),
                1.0f to Color.White.copy(alpha = 0f),
                center = center,
            ),
            style = Stroke(width = lineWidthPx * 0.6f),
        )
    }

    /**
     * COUI 17 inner shadow (`InnerShadowStyleMapper` + `ShadowEdge.java` `blendShadow`): a white
     * inner glow shaped as `pow(smoothstep(-fadeIn, 0, sdf), 4) * 0.9 + 0.1 * alpha`, weighted so it
     * reads along the **bottom inner edge** (the shadow is offset upward). Peak alpha is 0.06 light
     * / 0.04 dark for the circle preset.
     */
    fun DrawScope.drawInnerGlow(path: Path, sizePx: Float, isDark: Boolean) {
        val peak = if (isDark) 0.04f else 0.06f
        val innerSize = sizePx * 0.9f
        val inset = (sizePx - innerSize) / 2f
        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                0f to Color.White.copy(alpha = peak * 0.2f),
                0.65f to Color.White.copy(alpha = peak * 0.35f),
                1f to Color.White.copy(alpha = peak),
                startY = inset,
                endY = sizePx,
            ),
            style = Stroke(width = (sizePx - innerSize) / 2f),
            alpha = 1f,
        )
    }

    /** COUI 17 material_shader.glsl main loop: multi-light spotlight. */
    fun DrawScope.drawSpotlight(
        center: Offset,
        radius: Float,
        glowColor: Color,
        spotlightColor: Color,
        pressed: Boolean,
    ) {
        val lightRadius = radius * 0.8f
        val atten = if (pressed) 0.3f else 0.15f
        val brush = Brush.radialGradient(
            colors = listOf(
                spotlightColor.copy(alpha = atten),
                spotlightColor.copy(alpha = 0f),
            ),
            center = center,
            radius = lightRadius,
        )
        drawCircle(brush = brush, radius = lightRadius, center = center)
    }

    /** COUI 17 caustic shadow: refractive edge glow (sdfWidth + factor0/1 + speed). */
    fun DrawScope.drawCausticShadow(
        center: Offset,
        radius: Float,
        cornerRadius: Float,
        isDark: Boolean,
    ) {
        val sdfWidth = 2.dp.toPx()
        val causticColor = if (isDark) Color(0xFF7F7F7F) else Color(0xFFF5F5F5)
        val refractiveOffset = sdfWidth * 0.5f

        val offsets = listOf(
            Offset(-refractiveOffset, 0f) to Color.Red.copy(alpha = 0.1f),
            Offset(refractiveOffset, 0f) to Color.Cyan.copy(alpha = 0.1f),
            Offset(0f, -refractiveOffset) to Color.Green.copy(alpha = 0.1f),
            Offset(0f, refractiveOffset) to Color.Magenta.copy(alpha = 0.1f),
        )
        offsets.forEach { (offset, color) ->
            drawCircle(
                color = color,
                radius = radius + sdfWidth,
                center = center + offset,
            )
        }
        drawCircle(
            color = causticColor.copy(alpha = 0.15f),
            radius = radius + sdfWidth,
            center = center,
        )
    }

    /**
     * COUI 17 condensed-light glow overlay for a top app bar, replicating `AppBarBlurHelper`
     * + `COUIMaterialBlurEffect` in Compose: a translucent vertical gradient base (bottom→top)
     * plus the condensed-light blend layers, revealed by the scroll offset.
     *
     * @param baseColor The opaque base color of the gradient sheet (e.g. the app bar surface).
     * @param scrollOffsetPx The top-app-bar scroll offset in pixels (grows as the bar collapses).
     *   The glow alpha follows COUI 17: `min(1, scrollOffsetPx / 30dp)`.
     * @param isDark Whether the dark scheme is active, to pick the dark blend layers and alpha stops.
     * @param enabled Whether the glow is active.
     */
    @Composable
    fun Modifier.materialGlow(
        baseColor: Color,
        scrollOffsetPx: Float,
        isDark: Boolean,
        enabled: Boolean = true,
    ): Modifier {
        if (!enabled) return this

        val thresholdPx = with(androidx.compose.ui.platform.LocalDensity.current) { 30.dp.toPx() }
        val alpha = (scrollOffsetPx / thresholdPx).coerceIn(0f, 1f)
        if (alpha <= 0f) return this

        val gradient = remember(baseColor, isDark) {
            if (isDark) {
                Brush.verticalGradient(
                    0f to baseColor.copy(alpha = 0f),
                    1f to baseColor.copy(alpha = 0.4f),
                )
            } else {
                Brush.verticalGradient(
                    0f to baseColor.copy(alpha = 0f),
                    0.4f to baseColor.copy(alpha = 0.7f),
                    1f to baseColor.copy(alpha = 1f),
                )
            }
        }

        return this
            .drawWithContent {
                drawRect(brush = gradient)
            }
            .graphicsLayer {
                this.alpha = alpha
            }
    }
}
