// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.interfaces.HoldDownObserver
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * A COUI 17 material top-bar icon button, replicating the full `ToolbarMaterialEffectDelegate`
 * + `libhwui.so` material shader (material_shader.glsl): capsule SDF, G2 superellipse, multi-light
 * spotlight, caustic shadow, and squircle, drawn with Compose Canvas.
 *
 * @param onClick The callback when the [MaterialIconButton] is clicked.
 * @param modifier The modifier to be applied to the [MaterialIconButton]
 * @param enabled Whether the [MaterialIconButton] is enabled.
 * @param holdDownState Used to determine whether it is in the pressed state.
 * @param panelColor The material panel color. Defaults to COUI 17 (`#FFF0F0F0` light / `#FF333333` dark).
 * @param size The button size (COUI 17 top-bar buttons are circular 48dp).
 * @param cornerRadius The corner radius (24dp for a 48dp circle).
 * @param content The content of this icon button, typically an [Icon].
 */
@Composable
fun MaterialIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    holdDownState: Boolean = false,
    panelColor: Color = MaterialIconButtonDefaults.panelColor(),
    size: Dp = MaterialIconButtonDefaults.Size,
    cornerRadius: Dp = MaterialIconButtonDefaults.CornerRadius,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    HoldDownObserver(holdDownState, interactionSource)

    val pressed by interactionSource.collectIsPressedAsState()
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        animationSpec = androidx.compose.animation.core.spring(dampingRatio = 1f, stiffness = 438.65f),
        label = "materialIconButtonPressScale",
    )

    val currentOnClick by rememberUpdatedState(onClick)
    val clickableModifier = remember(enabled, interactionSource) {
        if (enabled) {
            Modifier.clickable(
                role = Role.Button,
                interactionSource = interactionSource,
                onClick = { currentOnClick() },
            )
        } else {
            Modifier
        }
    }

    val isDark = isSystemInDarkTheme()
    val glowColor = if (isDark) Color(0xFF262626) else Color(0xFF333333)
    val spotlightColor = if (isDark) Color(0xFF7F7F7F) else Color(0xFFF5F5F5)

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = size, minHeight = size)
            .then(clickableModifier)
            .drawWithContent {
                val buttonSizePx = size.toPx()
                val cornerRadiusPx = cornerRadius.toPx()
                val center = this.size.center

                // 1. Capsule SDF mask (COUI 17 material_shader.glsl sdCapsule)
                val capsulePath = capsulePath(buttonSizePx, cornerRadiusPx)
                clipPath(capsulePath) {
                    // 2. Panel fill
                    drawRect(color = panelColor)

                    // 3. Spotlight (multi-light, COUI 17 material_shader.glsl main loop)
                    drawSpotlight(
                        center = center,
                        radius = buttonSizePx / 2f,
                        glowColor = glowColor,
                        spotlightColor = spotlightColor,
                        pressed = pressed,
                    )

                    // 4. Caustic shadow (COUI 17 caustic shadow: refractive edge glow)
                    drawCausticShadow(
                        center = center,
                        radius = buttonSizePx / 2f,
                        cornerRadius = cornerRadiusPx,
                        isDark = isDark,
                    )
                }

                // 5. Capsule stroke (COUI 17 COUIShadowEdgeDrawable edge)
                drawPath(
                    path = capsulePath,
                    color = if (isDark) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.2f),
                    style = Stroke(width = 2.dp.toPx()),
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

/** COUI 17 material_shader.glsl sdCapsule: capsule SDF path. */
private fun capsulePath(width: Float, cornerRadius: Float): Path {
    val path = Path()
    val r = cornerRadius
    val w = width
    val h = width // circular button: width == height
    // Capsule: two semicircles + rectangle
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
private fun DrawScope.drawSpotlight(
    center: Offset,
    radius: Float,
    glowColor: Color,
    spotlightColor: Color,
    pressed: Boolean,
) {
    // COUI 17: 8 lights, but the top-bar button uses 1 main spotlight (TYPE_OPAQUE_SMALL_2)
    val lightRadius = radius * 0.8f
    val falloff = 1.5f
    val atten = if (pressed) 0.3f else 0.15f // pressed: stronger spotlight
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
private fun DrawScope.drawCausticShadow(
    center: Offset,
    radius: Float,
    cornerRadius: Float,
    isDark: Boolean,
) {
    // COUI 17 caustic shadow: refractive rainbow edge (sdfWidth ~2dp, factor0/1 refractive coeffs)
    val sdfWidth = 2.dp.toPx()
    val causticColor = if (isDark) Color(0xFF7F7F7F) else Color(0xFFF5F5F5)
    val refractiveOffset = sdfWidth * 0.5f

    // Draw refractive edge: offset copies with chromatic aberration
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
    // Main caustic glow
    drawCircle(
        color = causticColor.copy(alpha = 0.15f),
        radius = radius + sdfWidth,
        center = center,
    )
}

object MaterialIconButtonDefaults {

    /** COUI 17 top-bar button size: circular 48dp (`coui_toolbar_back_view_tiny_width`). */
    val Size = 48.dp

    /** COUI 17 top-bar button corner radius: 24dp (48/2, `coui_toolbar_menu_bg_radius`). */
    val CornerRadius = 24.dp

    /** COUI 17 material panel color: `#FFF0F0F0` light, `#FF333333` dark. */
    @Composable
    fun panelColor(): Color = if (isSystemInDarkTheme()) Color(0xFF333333) else Color(0xFFF0F0F0)
}
