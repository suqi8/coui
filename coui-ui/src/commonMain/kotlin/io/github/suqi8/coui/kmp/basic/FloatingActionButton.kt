// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.effect.FrameworkEdgeBlendMode
import io.github.suqi8.coui.kmp.effect.LocalMaterialBackdrop
import io.github.suqi8.coui.kmp.effect.MaterialBackdropSpec
import io.github.suqi8.coui.kmp.effect.ShadowEdgePresets
import io.github.suqi8.coui.kmp.effect.ShadowEdgeShader
import io.github.suqi8.coui.kmp.effect.StrokeEffectParams
import io.github.suqi8.coui.kmp.effect.couiTouchMotion
import io.github.suqi8.coui.kmp.effect.splitFrameworkStroke
import io.github.suqi8.coui.kmp.shader.RuntimeShader
import io.github.suqi8.coui.kmp.shader.asBrush
import io.github.suqi8.coui.kmp.shader.isRuntimeShaderSupported
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.LocalContentColor
import io.github.suqi8.coui.kmp.utils.CouiHapticEffect
import io.github.suqi8.coui.kmp.utils.rememberCouiHaptic
import kotlin.math.min

/**
 * A [FloatingActionButton] component with COUI style, after the main button of COUI 17
 * `COUIFloatingButton`.
 *
 * - **Touch**: the `COUITouchMotionAnimator` the FAB wrapper is driven by, with press scale,
 *   rubber-band drag-follow and drag deformation all on (a 56dp button presses to ~0.91 and
 *   springs back under-damped). A touch down plays the OPLUS 302 haptic (`fabNeedVibrate`).
 * - **Shadow**: the OPlus light-source elevation `ShadowUtils.setElevationToFloatingActionButton`
 *   requests: a soft glow in the button's own colour (see [FloatingActionButtonDefaults]).
 * - **Material**: with [material] on (the ColorOS 17 default, the system material switches), the
 *   `TYPE_FRAMEWORK_CIRCLE_2` edge lighting + inner shadow is drawn over the fill and the
 *   `COUIMaskEffectDrawable` press mask is turned off; when a [LocalMaterialBackdrop] provider is
 *   present the fill is replaced by the `TYPE_FRAMEWORK_FAB` backdrop blur. With [material] off the
 *   opaque fill takes the `couiColorPress` mask while pressed.
 * - **Disabled**: the fill turns to [disabledContainerColor], touches are ignored and the material
 *   switches to its disabled presets.
 *
 * @param onClick The callback when the [FloatingActionButton] is clicked.
 * @param modifier The modifier to be applied to the [FloatingActionButton].
 * @param enabled Whether the [FloatingActionButton] is enabled.
 * @param material Whether the COUI 17 material (edge lighting and, with a backdrop provider, the
 *   backdrop blur) is drawn, as on ColorOS 17 with its system material switches on.
 * @param hapticFeedbackEnabled Whether a touch down plays the press haptic (COUI `fabNeedVibrate`).
 * @param shape The shape of the [FloatingActionButton].
 * @param containerColor The color of the [FloatingActionButton].
 * @param disabledContainerColor The color of the [FloatingActionButton] when disabled.
 * @param contentColor The [LocalContentColor] provided to [content] (COUI draws the icon in
 *   `couiColorLabelOnColor`).
 * @param shadowElevation The OPlus shadow elevation of the [FloatingActionButton]; 0dp removes the
 *   shadow (COUI `fabNeedElevation = false`).
 * @param shadowColor The color of the shadow glow. Defaults to [containerColor] at the COUI shadow
 *   alpha.
 * @param minWidth The minimum width of the [FloatingActionButton].
 * @param minHeight The minimum height of the [FloatingActionButton].
 * @param interactionSource The [MutableInteractionSource] to be used for the [FloatingActionButton].
 * @param content The [Composable] content of the [FloatingActionButton].
 */
@Composable
fun FloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    material: Boolean = true,
    hapticFeedbackEnabled: Boolean = true,
    shape: Shape = CircleShape,
    containerColor: Color = COUITheme.colorScheme.primary,
    disabledContainerColor: Color = FloatingActionButtonDefaults.disabledContainerColor(),
    contentColor: Color = COUITheme.colorScheme.onPrimary,
    shadowElevation: Dp = FloatingActionButtonDefaults.ShadowElevation,
    shadowColor: Color = containerColor.copy(alpha = FloatingActionButtonDefaults.ShadowAlpha),
    minWidth: Dp = FloatingActionButtonDefaults.MinWidth,
    minHeight: Dp = FloatingActionButtonDefaults.MinHeight,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val isDark = COUITheme.colorScheme.background.luminance() < 0.5f
    val themeFill = COUITheme.colorScheme.primary

    // The material layer needs runtime shaders; without them COUI keeps the plain mask.
    val shadersSupported = remember { isRuntimeShaderSupported() }
    val materialActive = material && shadersSupported

    // COUIFloatingButton.ensureBlurEffect: the filtered backdrop replaces the fill when a backdrop
    // is available; otherwise the opaque fill of the non-blur path stays.
    val backdropProvider = LocalMaterialBackdrop.current
    val backdropModifier = if (materialActive && backdropProvider != null) {
        val spec = remember(isDark, enabled, containerColor, disabledContainerColor, themeFill) {
            fabBackdropSpec(
                isDark = isDark,
                enabled = enabled,
                fill = containerColor,
                disabledFill = disabledContainerColor,
                themeFill = containerColor == themeFill,
            )
        }
        remember(backdropProvider, spec, shape) { backdropProvider.materialBackdrop(spec, shape) }
    } else {
        null
    }
    val fillColor = when {
        backdropModifier != null -> Color.Transparent
        enabled -> containerColor
        else -> disabledContainerColor
    }

    // OPlus light-source shadow: the spot offset depends on where the button sits below the light.
    val centerYInWindow = remember { mutableFloatStateOf(Float.NaN) }
    val shadowModifier = if (shadowElevation > 0.dp) {
        Modifier.dropShadow(shape) {
            val elevation = shadowElevation.toPx()
            val centerY = centerYInWindow.floatValue.takeUnless { it.isNaN() } ?: 0f
            radius = FloatingActionButtonDefaults.shadowPenumbra(shadowElevation).toPx()
            offset = Offset(
                x = 0f,
                y = fabSpotShadowOffsetY(
                    centerY = centerY,
                    elevation = elevation,
                    lightY = FloatingActionButtonDefaults.ShadowLightY.toPx(),
                    lightZ = FloatingActionButtonDefaults.ShadowLightZ.toPx(),
                ),
            )
            color = shadowColor
        }
    } else {
        Modifier
    }

    val haptic = rememberCouiHaptic()
    val currentHaptic by rememberUpdatedState(haptic)
    val hapticModifier = if (enabled && hapticFeedbackEnabled) {
        // COUIFloatingButton.animatePress: performHapticFeedback(302) on ACTION_DOWN.
        Modifier.pointerInput(Unit) {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false)
                currentHaptic(CouiHapticEffect.Switch)
            }
        }
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .onGloballyPositioned { centerYInWindow.floatValue = it.positionInWindow().y + it.size.height / 2f }
            // The wrapper carries the shadow, the material and the button, and the touch motion
            // scales and moves them all together.
            .couiTouchMotion(interactionSource = interactionSource, enabled = enabled)
            .then(shadowModifier)
            .then(backdropModifier ?: Modifier)
            .clip(shape)
            .background(fillColor)
            .then(if (materialActive) Modifier.fabMaterialStroke(isDark, enabled) else Modifier)
            .then(hapticModifier)
            .clickable(
                interactionSource = interactionSource,
                // COUIFloatingButton.ensureBlurEffect turns the press mask off with the material.
                indication = if (materialActive) null else LocalIndication.current,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .defaultMinSize(minWidth = minWidth, minHeight = minHeight),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor, content = content)
    }
}

/**
 * The COUI 17 `TYPE_FRAMEWORK_CIRCLE_2` stroke (`TYPE_FRAMEWORK_CIRCLE_DISABLED_2` when disabled):
 * circle edge and inner-shadow styles, corner weight 2.0 (`OplusMaterialCornerParams(r, 2.0f)`).
 * Drawn over the fill and below the icon.
 */
@Composable
private fun Modifier.fabMaterialStroke(isDark: Boolean, enabled: Boolean): Modifier {
    val shader = remember { RuntimeShader(ShadowEdgeShader.Source) }
    // The edge-light pass of the framework stroke (see FrameworkEdgeBlendMode).
    val edgePass = remember { RuntimeShader(ShadowEdgeShader.Source) }
    val params = fabStrokeParams(isDark, enabled)
    return this.drawWithCache {
        val radius = min(size.width, size.height) / 2f
        for (pass in listOf(shader, edgePass)) {
            pass.setFloatUniform(
                "u_commonArray",
                ShadowEdgeShader.commonArray(size.width, size.height, radius, weight = FabCornerWeight),
            )
            pass.setFloatUniform("u_shadow", 1f)
            pass.setFloatUniform(
                "u_shadowArray",
                ShadowEdgeShader.shadowArray(
                    spec = ShadowEdgePresets.CircleShadow,
                    width = size.width,
                    height = size.height,
                    fadeWidthScale = params.shadowFadeScale,
                    fadeAlphaInScale = params.shadowFadeIn,
                    fadeAlphaOutScale = params.shadowFadeOut,
                    density = density,
                ),
            )
            pass.setFloatUniform("u_edge", 1f)
            pass.setFloatUniform(
                "u_edgeArray",
                ShadowEdgeShader.edgeArray(
                    spec = ShadowEdgePresets.CircleEdge,
                    width = size.width,
                    height = size.height,
                    lineWidth = params.edgeWidth.dp.toPx(),
                    lineAlpha = params.edgeAlpha,
                    lineAngle = params.edgeAngle,
                    density = density,
                ),
            )
        }
        splitFrameworkStroke(shadowPass = shader, edgePass = edgePass)
        val shadowBrush = shader.asBrush()
        val edgeBrush = edgePass.asBrush()
        onDrawWithContent {
            // The edge shader carries its own SDF mask.
            drawRect(shadowBrush)
            drawRect(edgeBrush, blendMode = FrameworkEdgeBlendMode)
            drawContent()
        }
    }
}

/** `COUIMaterialStrokeEffect.FRAMEWORK_CIRCLE_PARAMS_2[_DISABLED]_{LIGHT,DARK}`. */
internal fun fabStrokeParams(isDark: Boolean, enabled: Boolean): StrokeEffectParams = when {
    enabled -> FabStroke
    isDark -> FabStrokeDisabledDark
    else -> FabStrokeDisabledLight
}

/**
 * The `COUIMaterialThemeBlurEffect` view blur of the FAB (10px):
 * - theme colour enabled (`FRAMEWORK_FAB_PARAMS`): the fill re-alpha'd to 229 (light) / 230 (dark)
 *   SRC_OVER, then `#1AFFFFFF` (light) / `#19FFFFFF` (dark) OVERLAY;
 * - theme colour disabled (`FRAMEWORK_DISABLED_FAB`): the fill at alpha 77, then `#19FFFFFF`
 *   OVERLAY;
 * - a custom colour (`getNonThemeFabColor`): the colour (or its disabled colour) at full alpha,
 *   then the same second layer.
 */
internal fun fabBackdropSpec(
    isDark: Boolean,
    enabled: Boolean,
    fill: Color,
    disabledFill: Color,
    themeFill: Boolean,
): MaterialBackdropSpec {
    val layer2 = if (enabled && !isDark) Color(0x1AFFFFFF) else Color(0x19FFFFFF)
    val layer1 = when {
        !themeFill -> if (enabled) fill else disabledFill
        enabled -> fill.copy(alpha = (if (isDark) 230f else 229f) / 255f)
        else -> fill.copy(alpha = 77f / 255f)
    }
    return MaterialBackdropSpec(
        blurRadiusPx = 10f,
        layer1 = layer1,
        mode1 = BlendMode.SrcOver,
        layer2 = layer2,
        mode2 = BlendMode.Overlay,
    )
}

/**
 * The vertical offset of an Android spot shadow cast by an element at [elevation] whose centre
 * sits at [centerY], lit by a point light at ([lightY], [lightZ]): the outline is projected away
 * from the light by `elevation * (centerY - lightY) / (lightZ - elevation)`. All values share one
 * unit.
 */
internal fun fabSpotShadowOffsetY(centerY: Float, elevation: Float, lightY: Float, lightZ: Float): Float {
    if (elevation <= 0f || lightZ <= elevation) return 0f
    return elevation * (centerY - lightY) / (lightZ - elevation)
}

private val FabCornerWeight = 2f

private val FabStroke = StrokeEffectParams(0.25f, 2.5f, 0f, 0.3f, 1f, 1f)
private val FabStrokeDisabledLight = StrokeEffectParams(0.075f, 2.5f, 0f, 0.09f, 0.3f, 2f)
private val FabStrokeDisabledDark = StrokeEffectParams(0.12f, 2.5f, 0f, 0.09f, 0.3f, 2f)

/** Contains default values used by [FloatingActionButton]. */
object FloatingActionButtonDefaults {
    /** The default minimum width of the [FloatingActionButton] (COUI coui_floating_button_size). */
    val MinWidth = 56.dp

    /** The default minimum height of the [FloatingActionButton] (COUI coui_floating_button_size). */
    val MinHeight = 56.dp

    /** The fixed press scale used before the COUI 17 touch motion. No longer read. */
    @Deprecated("The button now presses with the COUI 17 touch motion, whose target depends on its size.")
    val PressedScale = 0.92f

    /** The OPlus shadow elevation of the [FloatingActionButton] (COUI coui_float_btn_shadow_elevation). */
    val ShadowElevation = 60.dp

    /** The alpha of the shadow colour, applied to the button colour (COUI coui_shadow_color_float_btn, 86/255). */
    val ShadowAlpha = 86f / 255f

    /** The y of the shadow light source, above the window top (COUI coui_float_btn_shadow_light_y). */
    val ShadowLightY = (-800).dp

    /** The height of the shadow light source (COUI coui_float_btn_shadow_light_z). */
    val ShadowLightZ = 10000.dp

    /** The radius of the shadow light source (COUI coui_float_btn_shadow_light_r). */
    val ShadowLightRadius = 2000.dp

    /**
     * The blur of the spot shadow for [elevation]: the penumbra width of a light of
     * [ShadowLightRadius] at [ShadowLightZ], `radius * elevation / (z - elevation)` (~12dp at 60dp).
     */
    fun shadowPenumbra(elevation: Dp): Dp {
        if (elevation <= 0.dp || ShadowLightZ <= elevation) return 0.dp
        return ShadowLightRadius * (elevation / (ShadowLightZ - elevation))
    }

    /**
     * The disabled fill: `couiColorDisable` composited over `coui_floating_button_disabled_base_color`
     * (#4D0080FF over #FFFFFF = #B2D9FF light, #4D1A8CFF over #4D4D4D = #3E6083 dark).
     */
    @Composable
    fun disabledContainerColor(): Color {
        val scheme = COUITheme.colorScheme
        val isDark = scheme.background.luminance() < 0.5f
        return scheme.disabledPrimary.compositeOver(if (isDark) DisabledBaseDark else DisabledBaseLight)
    }

    private val DisabledBaseLight = Color(0xFFFFFFFF)
    private val DisabledBaseDark = Color(0xFF4D4D4D)
}
