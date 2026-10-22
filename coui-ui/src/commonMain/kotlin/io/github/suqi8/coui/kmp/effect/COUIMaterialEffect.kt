// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.effect

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.isSupported
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.shader.RuntimeShader
import io.github.suqi8.coui.kmp.shader.asBrush
import io.github.suqi8.coui.kmp.shader.isRuntimeShaderSupported
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.min

/**
 * COUI 17 top-bar button material, after `ToolbarMaterialEffectDelegate` (default path:
 * `mIsDrawableMaterialEffect = false`, material type 0).
 */
object COUIMaterialEffect {

    /**
     * Gives a component the COUI 17 top-bar button material:
     * - **Fill**: the view background is transparent and `setViewBlurEffect(TYPE_FRAMEWORK_TOP_BAR)`
     *   filters whatever lies behind it with a 0px blur and two colour filters (light `#66333333`
     *   COLOR_DODGE + `#99F5F5F5` LUMINOSITY, dark `#CC262626` SRC_OVER + `#B37F7F7F` OVERLAY). The
     *   filters are drawn here as blended layers over the content beneath. Without those blend
     *   modes the button falls back to the opaque `?couiColorBar`.
     * - **Shadow**: `ShadowUtils` level 6 (16dp elevation, alpha 30/255, light 160dp above).
     * - **Edge lighting + inner shadow**: [ShadowEdgeShader].
     * - **Touch spotlight**: [SpotLightShader], following the finger.
     * - **Press**: the `COUIMaskEffectDrawable` press mask and the `PressPhysicsEngine` scale.
     *
     * The edge lighting and the spotlight need runtime shaders (Android 13+ and every Skia target),
     * as `COUIShadowEdgeDrawable` and `COUISpotLightEffectDrawable` do; elsewhere they are skipped.
     *
     * @param interactionSource The interaction source driving the press state.
     * @param cornerRadius The corner radius; half the height for a circle or capsule.
     * @param enabled Whether the material is drawn.
     */
    @Composable
    fun Modifier.materialPanel(
        interactionSource: MutableInteractionSource,
        cornerRadius: Dp = 20.dp,
        enabled: Boolean = true,
    ): Modifier {
        if (!enabled) return this

        val isDark = COUITheme.colorScheme.background.luminance() < 0.5f
        val pressed by interactionSource.collectIsPressedAsState()

        // COUIPressAnimatorUnit: press spring response 0.25 / bounce 0, release (jelly hand-up)
        // response 0.5 / bounce 0.6. stiffness = (2pi / response)^2, damping ratio = 1 - bounce.
        val pressFraction = remember { Animatable(0f) }
        LaunchedEffect(pressed) {
            pressFraction.animateTo(
                targetValue = if (pressed) 1f else 0f,
                animationSpec = if (pressed) {
                    spring(dampingRatio = 1f, stiffness = 631.65f)
                } else {
                    spring(dampingRatio = 0.4f, stiffness = 157.91f)
                },
            )
        }
        // COUIMaskEffectDrawable press layer: ?couiColorPress on a response 0.3 / bounce 0 spring.
        val maskProgress = remember { Animatable(0f) }
        LaunchedEffect(pressed) {
            maskProgress.animateTo(
                targetValue = if (pressed) 1f else 0f,
                animationSpec = spring(dampingRatio = 1f, stiffness = 438.65f),
            )
        }

        val shaders = remember {
            if (isRuntimeShaderSupported()) {
                RuntimeShader(ShadowEdgeShader.Source) to RuntimeShader(SpotLightShader.Source)
            } else {
                null
            }
        }
        val spotlight = remember { SpotlightState() }
        val scope = rememberCoroutineScope()

        val shape = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }
        val blendSupported = remember { BlendMode.ColorDodge.isSupported() && BlendMode.Luminosity.isSupported() }
        val pressMaskColor = if (isDark) PressMaskDark else PressMaskLight
        val fallbackFill = if (isDark) BarFallbackDark else BarFallbackLight

        return this
            // The press scale lifts the whole button (shadow, material, edge and icon) together.
            .graphicsLayer {
                val target = pressTargetScale(size.width / density, size.height / density)
                val scale = 1f + (target - 1f) * pressFraction.value
                scaleX = scale
                scaleY = scale
            }
            .dropShadow(shape = shape, shadow = MaterialShadow)
            .then(
                if (shaders != null) {
                    Modifier.pointerInput(Unit) {
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            scope.launch { spotlight.glow(down.position) }
                            while (true) {
                                val change = awaitPointerEvent().changes.firstOrNull { it.id == down.id } ?: break
                                if (change.changedToUp() || !change.pressed) break
                                scope.launch { spotlight.follow(change.position) }
                            }
                            scope.launch { spotlight.extinguish() }
                        }
                    }
                } else {
                    Modifier
                },
            )
            .drawWithCache {
                val radius = min(cornerRadius.toPx(), min(size.width, size.height) / 2f)
                val outline = Path().apply { addRoundRect(RoundRect(size.toRect(), CornerRadius(radius))) }
                val edgeBrush = shaders?.first?.let { shader ->
                    configureShadowEdge(shader, size.width, size.height, radius, isDark)
                    shader.asBrush()
                }

                onDrawWithContent {
                    clipPath(outline) {
                        if (blendSupported) {
                            if (isDark) {
                                drawRect(FilterDark1, blendMode = BlendMode.SrcOver)
                                drawRect(FilterDark2, blendMode = BlendMode.Overlay)
                            } else {
                                drawRect(FilterLight1, blendMode = BlendMode.ColorDodge)
                                drawRect(FilterLight2, blendMode = BlendMode.Luminosity)
                            }
                        } else {
                            drawRect(fallbackFill)
                        }

                        val glow = spotlight.progress.value
                        val spotShader = shaders?.second
                        if (spotShader != null && glow > 0f) {
                            val light = spotlight.position.value
                            SpotLightShader.setUniforms(
                                shader = spotShader,
                                lightX = light.x,
                                lightY = light.y,
                                radius = SpotLightShader.radius(glow, spotlight.extinguishing),
                                intensity = SpotLightShader.endIntensity(isDark) * glow,
                            )
                            drawRect(
                                brush = spotShader.asBrush(),
                                blendMode = if (isDark) BlendMode.Plus else BlendMode.Overlay,
                            )
                        }

                        val mask = maskProgress.value
                        if (mask > 0f) drawRect(pressMaskColor, alpha = mask.coerceIn(0f, 1f))
                    }
                    // The edge shader carries its own SDF mask.
                    if (edgeBrush != null) drawRect(edgeBrush)
                    drawContent()
                }
            }
    }

    /**
     * `PressPhysicsEngine.calculateTargetScaleBreakdown`: the press target scale for a
     * [width] x [height] dp view. `t = (clamp(wh, 20², 150²) - 20²) / (150² - 20²)` and
     * `scale = 0.85 + B(t) * 0.12` with `B = cubic-bezier(0, 0, 0, 1)`; a 40dp circle lands on
     * ~0.889.
     */
    fun pressTargetScale(width: Float, height: Float): Float {
        val minArea = 20f * 20f
        val maxArea = 150f * 150f
        val t = ((width * height).coerceIn(minArea, maxArea) - minArea) / (maxArea - minArea)
        return 0.85f + bezier0001(t) * 0.12f
    }

    /** `cubic-bezier(0, 0, 0, 1)` evaluated at x = [t]. */
    private fun bezier0001(t: Float): Float {
        // x(u) = u^3 and y(u) = 3u^2 - 2u^3 for control points (0,0) and (0,1).
        var lo = 0f
        var hi = 1f
        repeat(24) {
            val mid = (lo + hi) / 2f
            if (mid * mid * mid < t) lo = mid else hi = mid
        }
        val u = (lo + hi) / 2f
        return 3f * u * u - 2f * u * u * u
    }

    /**
     * Feeds [ShadowEdgeShader] the uniforms `ToolbarMaterialEffectDelegate` gives a button of
     * [width] x [height] pixels: edge and inner shadow both on, style 0 + `CAPSULE_1` params for a
     * capsule, style 1 + `CIRCLE_1` params for a circle (`isCapsule()` = width != height).
     */
    private fun Density.configureShadowEdge(
        shader: RuntimeShader,
        width: Float,
        height: Float,
        cornerRadius: Float,
        isDark: Boolean,
    ) {
        val isCapsule = abs(width - height) >= 0.5f
        val params = when {
            isCapsule && isDark -> ShadowEdgePresets.CapsuleDark
            isCapsule -> ShadowEdgePresets.CapsuleLight
            isDark -> ShadowEdgePresets.CircleDark
            else -> ShadowEdgePresets.CircleLight
        }
        shader.setFloatUniform(
            "u_commonArray",
            ShadowEdgeShader.commonArray(width, height, cornerRadius, weight = if (isCapsule) 1f else 2f),
        )
        shader.setFloatUniform("u_shadow", 1f)
        shader.setFloatUniform(
            "u_shadowArray",
            ShadowEdgeShader.shadowArray(
                spec = if (isCapsule) ShadowEdgePresets.CapsuleShadow else ShadowEdgePresets.CircleShadow,
                width = width,
                height = height,
                fadeWidthScale = params.shadowFadeScale,
                fadeAlphaInScale = params.shadowFadeIn,
                fadeAlphaOutScale = params.shadowFadeOut,
                density = density,
            ),
        )
        shader.setFloatUniform("u_edge", 1f)
        shader.setFloatUniform(
            "u_edgeArray",
            ShadowEdgeShader.edgeArray(
                spec = if (isCapsule) ShadowEdgePresets.CapsuleEdge else ShadowEdgePresets.CircleEdge,
                width = width,
                height = height,
                lineWidth = params.edgeWidth.dp.toPx(),
                lineAlpha = params.edgeAlpha,
                lineAngle = params.edgeAngle,
                density = density,
            ),
        )
    }

    /** `COUISpotLightEffect` glow / follow / extinguish, driven by the pointer. */
    private class SpotlightState {
        val progress = Animatable(0f)
        val position = Animatable(Offset.Zero, Offset.VectorConverter)
        var extinguishing by mutableStateOf(false)
            private set

        suspend fun glow(at: Offset) {
            extinguishing = false
            position.snapTo(at)
            // glow(): response 0.15, bounce 0.
            progress.animateTo(1f, spring(dampingRatio = 1f, stiffness = 1754.6f))
        }

        suspend fun follow(to: Offset) {
            // Pointer follow: response 0.35, bounce 0.15.
            position.animateTo(to, spring(dampingRatio = 0.85f, stiffness = 322.27f))
        }

        suspend fun extinguish() {
            extinguishing = true
            // extinguish(): response 0.4, bounce 0.
            progress.animateTo(0f, spring(dampingRatio = 1f, stiffness = 246.74f))
        }
    }

    /** `ShadowUtils` level 6: 16dp elevation at alpha 30/255, lit from 160dp above (r 800dp). */
    private val MaterialShadow = Shadow(
        radius = 16.dp,
        color = Color.Black,
        offset = DpOffset(0.dp, 3.2.dp),
        alpha = 30f / 255f,
    )

    // TYPE_FRAMEWORK_TOP_BAR colour filters (COUIMaterialBlurEffect).
    private val FilterLight1 = Color(0x66333333)
    private val FilterLight2 = Color(0x99F5F5F5)
    private val FilterDark1 = Color(0xCC262626)
    private val FilterDark2 = Color(0xB37F7F7F)

    // ?couiColorBar, the non-blur fallback (ToolbarMaterialEffectResolver.getToolbarBackgroundColor).
    private val BarFallbackLight = Color(0xFFFAFAFA)
    private val BarFallbackDark = Color(0xFF1E1E1E)

    // coui_color_press.
    private val PressMaskLight = Color(0x1F000000)
    private val PressMaskDark = Color(0x33FFFFFF)
}
