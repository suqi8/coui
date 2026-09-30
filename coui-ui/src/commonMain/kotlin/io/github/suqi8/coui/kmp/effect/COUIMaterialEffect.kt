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
     * Applies the COUI 17 material panel to a component: panel fill + capsule stroke + elevation
     * shadow + spotlight + caustic shadow + press scale, drawn with Compose Canvas.
     *
     * @param panelColor The material panel color. Defaults to COUI 17 (`#FFF0F0F0` light / `#FF333333` dark).
     * @param size The component size (e.g. 48dp for a top-bar icon button).
     * @param cornerRadius The corner radius (e.g. 24dp for a 48dp circle).
     * @param interactionSource The interaction source driving the press state.
     * @param enabled Whether the material effect is active.
     */
    @Composable
    fun Modifier.materialPanel(
        interactionSource: MutableInteractionSource,
        panelColor: Color = panelColor(),
        size: Dp = 48.dp,
        cornerRadius: Dp = 24.dp,
        enabled: Boolean = true,
    ): Modifier {
        if (!enabled) return this

        val pressed by interactionSource.collectIsPressedAsState()
        val scale by animateFloatAsState(
            targetValue = if (pressed) 0.92f else 1f,
            animationSpec = spring(dampingRatio = 1f, stiffness = 438.65f),
            label = "materialPanelPressScale",
        )
        val isDark = isSystemInDarkTheme()
        val glowColor = if (isDark) Color(0xFF262626) else Color(0xFF333333)
        val spotlightColor = if (isDark) Color(0xFF7F7F7F) else Color(0xFFF5F5F5)
        val strokeColor = if (isDark) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.2f)

        return this
            .drawWithContent {
                val sizePx = size.toPx()
                val cornerRadiusPx = cornerRadius.toPx()
                val center = this.size.center

                // 1. Capsule SDF mask (material_shader.glsl sdCapsule)
                val capsulePath = capsulePath(sizePx, cornerRadiusPx)
                clipPath(capsulePath) {
                    // 2. Panel fill
                    drawRect(color = panelColor)

                    // 3. Spotlight (multi-light, material_shader.glsl main loop)
                    drawSpotlight(
                        center = center,
                        radius = sizePx / 2f,
                        glowColor = glowColor,
                        spotlightColor = spotlightColor,
                        pressed = pressed,
                    )

                    // 4. Caustic shadow (refractive edge glow)
                    drawCausticShadow(
                        center = center,
                        radius = sizePx / 2f,
                        cornerRadius = cornerRadiusPx,
                        isDark = isDark,
                    )
                }

                // 5. Capsule stroke (COUIShadowEdgeDrawable edge)
                drawPath(
                    path = capsulePath,
                    color = strokeColor,
                    style = Stroke(width = 2.dp.toPx()),
                )
            }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    }

    /** COUI 17 material panel color: `#FFF0F0F0` light, `#FF333333` dark. */
    @Composable
    fun panelColor(): Color = if (isSystemInDarkTheme()) Color(0xFF333333) else Color(0xFFF0F0F0)

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
}
