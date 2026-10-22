// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.shader.RuntimeShader
import io.github.suqi8.coui.kmp.shader.asBrush
import io.github.suqi8.coui.kmp.shader.isRuntimeShaderSupported
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.math.min

/**
 * The scalars a COUI 17 surface feeds `COUIShadowEdgeDrawable` once its window background is
 * blurred: `setLineWidth` (dp here, px in COUI), `setLineAlpha`, `setLineAngle`,
 * `setFadeWidthScale` and `setFadeAlpha(in, out)`, layered over an edge and a shadow preset.
 */
@Immutable
internal class MaterialStrokeParams(
    val edge: GradientStrokeSpec,
    val shadow: InnerShadowSpec,
    val lineWidth: Dp,
    val lineAlpha: Float,
    val lineAngle: Float,
    val fadeWidthScale: Float,
    val fadeAlphaIn: Float,
    val fadeAlphaOut: Float,
)

/** The stroke presets of the blurred COUI 17 dialog and bottom sheet. */
internal object MaterialStrokePresets {

    /**
     * `COUIAlertDialogRootLayout.applyShadowEdgeParams` (light): `EDGE_STYLE_2`, `SHADOW_STYLE_0`
     * and the `DialogShadowEdgeParamsExtKt` defaults lineWidth 4, lineAlpha 0.6, lineAngle 0,
     * fadeWidthScale 1, fadeAlpha (0.4, 0).
     */
    val DialogLight = MaterialStrokeParams(
        edge = ShadowEdgePresets.PanelEdge,
        shadow = ShadowEdgePresets.CapsuleShadow,
        lineWidth = 4.dp,
        lineAlpha = 0.6f,
        lineAngle = 0f,
        fadeWidthScale = 1f,
        fadeAlphaIn = 0.4f,
        fadeAlphaOut = 0f,
    )

    /** The dark twin of [DialogLight]: lineWidth 2, lineAlpha 0.3, fadeAlpha (0.03, 0). */
    val DialogDark = MaterialStrokeParams(
        edge = ShadowEdgePresets.PanelEdge,
        shadow = ShadowEdgePresets.CapsuleShadow,
        lineWidth = 2.dp,
        lineAlpha = 0.3f,
        lineAngle = 0f,
        fadeWidthScale = 1f,
        fadeAlphaIn = 0.03f,
        fadeAlphaOut = 0f,
    )

    /**
     * `COUIBottomSheetDialog.applyShadowEdgeParams` (light): `EDGE_STYLE_2`, `SHADOW_STYLE_2`,
     * `coui_panel_shadow_edge_width_light` (4dp), lineAlpha 0.8, fadeAlpha (0.4, 0).
     */
    val SheetLight = MaterialStrokeParams(
        edge = ShadowEdgePresets.PanelEdge,
        shadow = ShadowEdgePresets.PanelShadow,
        lineWidth = 4.dp,
        lineAlpha = 0.8f,
        lineAngle = 0f,
        fadeWidthScale = 1f,
        fadeAlphaIn = 0.4f,
        fadeAlphaOut = 0f,
    )

    /**
     * The dark twin of [SheetLight]: `coui_panel_shadow_edge_width_dark` (2dp), lineAlpha 0.15,
     * fadeAlpha (0.1, 0).
     */
    val SheetDark = MaterialStrokeParams(
        edge = ShadowEdgePresets.PanelEdge,
        shadow = ShadowEdgePresets.PanelShadow,
        lineWidth = 2.dp,
        lineAlpha = 0.15f,
        lineAngle = 0f,
        fadeWidthScale = 1f,
        fadeAlphaIn = 0.1f,
        fadeAlphaOut = 0f,
    )

    /** `weight_16_1_large`, the corner weight both surfaces hand the drawable on COUI 17. */
    val CornerWeight = 3f
}

/**
 * Draws the COUI 17 material stroke (edge lighting + inner shadow) of a blurred dialog or bottom
 * sheet behind the content, through [ShadowEdgeShader] with the `NEW_G2` corner type both COUI
 * surfaces request (`setCornerType(2)`). The shader carries its own SDF mask, so the stroke needs
 * no clip. Needs runtime shaders (Android 13+ and every Skia target); elsewhere it is skipped,
 * like `COUIShadowEdgeDrawable`.
 *
 * @param light The params used in the light scheme.
 * @param dark The params used in the dark scheme.
 * @param topCornerRadius The radius of the two top corners.
 * @param bottomCornerRadius The radius of the two bottom corners.
 * @param enabled Whether the stroke is drawn.
 */
@Composable
internal fun Modifier.materialStroke(
    light: MaterialStrokeParams,
    dark: MaterialStrokeParams,
    topCornerRadius: Dp,
    bottomCornerRadius: Dp,
    enabled: Boolean = true,
): Modifier {
    if (!enabled) return this
    val shader = remember { if (isRuntimeShaderSupported()) RuntimeShader(ShadowEdgeShader.Source) else null }
        ?: return this
    val isDark = COUITheme.colorScheme.background.luminance() < 0.5f
    val params = if (isDark) dark else light
    return this.drawWithCache {
        val maxRadius = min(size.width, size.height) / 2f
        shader.setFloatUniform(
            "u_commonArray",
            ShadowEdgeShader.commonArray(
                width = size.width,
                height = size.height,
                topCornerRadius = topCornerRadius.toPx().coerceAtMost(maxRadius),
                bottomCornerRadius = bottomCornerRadius.toPx().coerceAtMost(maxRadius),
                weight = MaterialStrokePresets.CornerWeight,
            ),
        )
        shader.setFloatUniform("u_shadow", 1f)
        shader.setFloatUniform(
            "u_shadowArray",
            ShadowEdgeShader.shadowArray(
                spec = params.shadow,
                width = size.width,
                height = size.height,
                fadeWidthScale = params.fadeWidthScale,
                fadeAlphaInScale = params.fadeAlphaIn,
                fadeAlphaOutScale = params.fadeAlphaOut,
                density = density,
            ),
        )
        shader.setFloatUniform("u_edge", 1f)
        shader.setFloatUniform(
            "u_edgeArray",
            ShadowEdgeShader.edgeArray(
                spec = params.edge,
                width = size.width,
                height = size.height,
                lineWidth = params.lineWidth.toPx(),
                lineAlpha = params.lineAlpha,
                lineAngle = params.lineAngle,
                density = density,
            ),
        )
        val brush = shader.asBrush()
        onDrawBehind { drawRect(brush) }
    }
}
