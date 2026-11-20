// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.effect

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.isSupported
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
import io.github.suqi8.coui.kmp.utils.layerOverflow
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.min

/**
 * The colour a [COUIMaterialEffect.materialPanel] lays down under its filters while an ancestor
 * layer renders it offscreen (a blur or a fade), read in the draw phase.
 *
 * The filters are colour-dodge and luminosity blends of whatever lies beneath the panel. An
 * offscreen layer starts out transparent, so inside one they blend with nothing and the glass turns
 * a flat grey. COUI's view blur keeps sampling the window behind a blurred or faded view; laying
 * down the page colour the panel sits on keeps the glass looking as it does at rest. The provider
 * returns [Color.Unspecified] while the panel is drawn in place, so the real backdrop shows.
 */
internal val LocalMaterialOffscreenBase = staticCompositionLocalOf<(() -> Color)?> { null }

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
     * - **Hover**: the `?couiColorHover` hover mask of the `COUIMaskEffectDrawable` (type 1). Its
     *   press layer only follows `setTouchEntered`, which the COUI 17 toolbar never calls
     *   (`ToolbarMaterialEffectDelegate.handleTouch` feeds the spotlight and the touch animator
     *   only), so a press shows the touch motion and the spotlight, no press mask.
     * - **Touch motion**: the `COUIViewTouchAnimator` press scale, rubber-band drag-follow
     *   translation and area-conserving drag deformation, with velocity-seeded release springs
     *   (see [couiTouchMotion]).
     *
     * The edge lighting and the spotlight need runtime shaders (Android 13+ and every Skia target),
     * as `COUIShadowEdgeDrawable` and `COUISpotLightEffectDrawable` do; elsewhere they are skipped.
     *
     * @param interactionSource The interaction source driving the press state.
     * @param cornerRadius The corner radius; half the height for a circle or capsule.
     * @param enabled Whether the material is drawn. Defaults to [LocalCOUIMaterialEffectEnabled].
     * @param motionEnabled Whether the touch motion runs. Pass `false` for a disabled button.
     * @param spotLightSuppressed Read when a touch would light or move the spotlight; while it
     *   returns `true` the spotlight stays dark. A gesture handler running later in the same pass
     *   (for example one that spots a child button consuming the down) can still turn it off for
     *   that touch, as `SearchBarBackgroundView` only feeds its spotlight touches that drive the
     *   capsule.
     * @param touchMotionSuppressed When non-null, read once the touch down has passed every handler;
     *   a gesture for which it returns `true` never starts the touch motion. `null` keeps the
     *   existing behaviour.
     */
    @Composable
    fun Modifier.materialPanel(
        interactionSource: MutableInteractionSource,
        cornerRadius: Dp = 20.dp,
        enabled: Boolean = LocalCOUIMaterialEffectEnabled.current,
        motionEnabled: Boolean = true,
        spotLightSuppressed: () -> Boolean = { false },
        touchMotionSuppressed: (() -> Boolean)? = null,
    ): Modifier {
        if (!enabled) return this
        val currentSpotLightSuppressed by rememberUpdatedState(spotLightSuppressed)

        val isDark = COUITheme.colorScheme.isDark
        // ?couiColorBar, the non-blur fallback (ToolbarMaterialEffectResolver.getToolbarBackgroundColor).
        val fallbackFill = COUITheme.colorScheme.bar
        val hovered by interactionSource.collectIsHoveredAsState()
        val offscreenBase = LocalMaterialOffscreenBase.current

        // The COUIMaskEffectDrawable hover layer: ?couiColorHover on a response 0.3 / bounce 0 spring.
        val hoverProgress = remember { Animatable(0f) }
        LaunchedEffect(hovered, motionEnabled) {
            hoverProgress.animateTo(
                targetValue = if (hovered && motionEnabled) 1f else 0f,
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
        // The edge-light pass of the framework stroke (see FrameworkEdgeBlendMode).
        val edgePass = remember { if (isRuntimeShaderSupported()) RuntimeShader(ShadowEdgeShader.Source) else null }
        val spotlight = remember { SpotLightState() }
        val scope = rememberCoroutineScope()

        val shape = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }
        val blendSupported = remember { BlendMode.ColorDodge.isSupported() && BlendMode.Luminosity.isSupported() }
        val hoverMaskColor = if (isDark) HoverMaskDark else HoverMaskLight

        return this
            // The touch motion moves the whole button (shadow, material, edge and icon) together.
            .couiTouchMotion(
                interactionSource = interactionSource,
                enabled = motionEnabled,
                touchSuppressed = touchMotionSuppressed,
            )
            .dropShadow(shape = shape, shadow = MaterialShadow)
            .then(
                if (shaders != null) {
                    Modifier.pointerInput(Unit) {
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            // Launched, so the check runs after every handler has seen the down.
                            scope.launch { if (!currentSpotLightSuppressed()) spotlight.glow(down.position) }
                            while (true) {
                                val change = awaitPointerEvent().changes.firstOrNull { it.id == down.id } ?: break
                                if (change.changedToUp() || !change.pressed) break
                                scope.launch { if (!currentSpotLightSuppressed()) spotlight.follow(change.position) }
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
                val shadowPass = shaders?.first
                var shadowBrush: Brush? = null
                var edgeBrush: Brush? = null
                if (shadowPass != null && edgePass != null) {
                    configureShadowEdge(shadowPass, size.width, size.height, radius, isDark)
                    configureShadowEdge(edgePass, size.width, size.height, radius, isDark)
                    splitFrameworkStroke(shadowPass = shadowPass, edgePass = edgePass)
                    shadowBrush = shadowPass.asBrush()
                    edgeBrush = edgePass.asBrush()
                }

                onDrawWithContent {
                    clipPath(outline) {
                        // Inside an offscreen ancestor layer the filters need the page beneath.
                        val base = offscreenBase?.invoke() ?: Color.Unspecified
                        if (base.isSpecified) drawRect(base)
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
                                intensity = SpotLightType.OpaqueSmall2.endIntensity(isDark) * glow,
                            )
                            drawRect(
                                brush = spotShader.asBrush(),
                                blendMode = SpotLightType.OpaqueSmall2.blendMode(isDark),
                            )
                        }

                        val hover = hoverProgress.value
                        if (hover > 0f) drawRect(hoverMaskColor, alpha = hover.coerceIn(0f, 1f))
                    }
                    // The edge shader carries its own SDF mask. The inner shadow is drawn
                    // source-over, the edge light overlaid on what lies beneath it.
                    if (shadowBrush != null) drawRect(shadowBrush)
                    if (edgeBrush != null) drawRect(edgeBrush, blendMode = FrameworkEdgeBlendMode)
                    drawContent()
                }
            }
    }

    /**
     * `PressCalc.calculateUniformScaleBreakdown` with the default `PressParams`: the press target
     * scale of a [width] x [height] dp view, shared by `COUITouchMotionAnimator` users such as
     * `COUIButton` and the top-bar buttons.
     *
     * - Area channel: `t = (clamp(wh, 20², 150²) - 20²) / (150² - 20²)`,
     *   `areaScale = 0.85 + B(t) * (0.97 - 0.85)`.
     * - Aspect-ratio channel: `ar = max(w / h, h / w)`. Up to `ar = 2` the result is `areaScale`;
     *   beyond it `arScale = 0.85 + B(clamp((ar - 2) / (15 - 2), 0, 1)) * (1 - 0.85)` and the
     *   result is the mean of both channels.
     *
     * `B = cubic-bezier(0, 0, 0, 1)` and the result is clamped to `[0.4, 1]`. A 40dp circle lands
     * on ~0.889, a 132 x 44dp button on ~0.920 and a 296 x 44dp button on ~0.966.
     */
    fun pressTargetScale(width: Float, height: Float): Float {
        val minArea = 20f * 20f
        val maxArea = 150f * 150f
        val t = ((width * height).coerceIn(minArea, maxArea) - minArea) / (maxArea - minArea)
        val areaScale = 0.85f + bezier0001(t) * (0.97f - 0.85f)
        val aspectRatio = pressAspectRatio(width, height)
        val scale = if (aspectRatio <= PressAspectRatioThreshold) {
            areaScale
        } else {
            val arT = ((aspectRatio - PressAspectRatioThreshold) / (15f - PressAspectRatioThreshold)).coerceIn(0f, 1f)
            val arScale = 0.85f + bezier0001(arT) * (1f - 0.85f)
            (areaScale + arScale) / 2f
        }
        return scale.coerceIn(0.4f, 1f)
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
        configureShadowEdge(shader, width, height, cornerRadius, weight = if (isCapsule) 1f else 2f, isCapsule, params)
    }

    /**
     * Feeds [ShadowEdgeShader] a stroke effect of [params] on a [width] x [height] pixel shape:
     * edge style 0 and shadow style 0 for a capsule, style 1 for a circle.
     */
    private fun Density.configureShadowEdge(
        shader: RuntimeShader,
        width: Float,
        height: Float,
        cornerRadius: Float,
        weight: Float,
        isCapsule: Boolean,
        params: StrokeEffectParams,
    ) {
        shader.setFloatUniform(
            "u_commonArray",
            ShadowEdgeShader.commonArray(width, height, cornerRadius, weight = weight),
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

    /**
     * The COUI 17 content-button material layer that `COUIButton` adds over its solid fill when
     * the system material switches are on (`useMaterialEffect` = FOLLOW_SYSTEM): the
     * `COUIMaterialStrokeEffect` `CONTENT_CAPSULE_*` edge lighting + inner shadow through
     * [ShadowEdgeShader], and the `COUISpotLightEffect` touch spotlight through [SpotLightShader].
     * The framework background blur that replaces the fill of a non-drawable button
     * (`TYPE_CONTENT_*_BUTTON`, see [ContentButtonMaterialType.backdropSpec]) needs a backdrop and
     * is not drawn here; the caller draws it through [LocalMaterialBackdrop] or keeps its fill.
     *
     * Draws inside the button's clip, above the fill and below the content. Needs runtime shaders
     * (Android 13+ and every Skia target); elsewhere it is skipped. Pointer events are only
     * observed, never consumed.
     *
     * @param type The button type selecting the presets.
     * @param enabled Whether the button is enabled; a disabled button takes the `_DISABLED`
     *   stroke presets and no spotlight.
     * @param cornerRadius The explicit corner radius, or [Dp.Unspecified] for a capsule at half
     *   the height (`drawableRadius = -1`).
     */
    @Composable
    internal fun Modifier.contentButtonMaterial(
        type: ContentButtonMaterialType,
        enabled: Boolean,
        cornerRadius: Dp,
    ): Modifier {
        val shaders = remember {
            if (isRuntimeShaderSupported()) {
                RuntimeShader(ShadowEdgeShader.Source) to RuntimeShader(SpotLightShader.Source)
            } else {
                null
            }
        } ?: return this
        // The edge-light pass of a framework stroke (non-drawable types, see FrameworkEdgeBlendMode).
        val edgePass = remember { RuntimeShader(ShadowEdgeShader.Source) }
        val isDark = COUITheme.colorScheme.isDark
        val spotlight = remember { SpotLightState() }
        val scope = rememberCoroutineScope()
        val stroke = type.stroke(isDark, enabled)
        val spotIntensity = type.spotLight.endIntensity(isDark)
        val spotBlend = type.spotLight.blendMode(isDark)

        return this
            .then(
                if (enabled) {
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
                val maxRadius = min(size.width, size.height) / 2f
                val isCapsule = abs(size.width - size.height) >= 0.5f
                // COUIButton stroke weight: 1.0 for a height-derived capsule, 2.0 for a circle,
                // 3.0 (weight_16_1_large) for an explicit radius; always 2.0 for the drawable
                // material (initShadowEdgeParamsWithoutBlur).
                val radius: Float
                val weight: Float
                if (type.drawable) {
                    radius = if (cornerRadius == Dp.Unspecified) maxRadius else min(cornerRadius.toPx(), maxRadius)
                    weight = 2f
                } else if (cornerRadius == Dp.Unspecified) {
                    radius = maxRadius
                    weight = if (isCapsule) 1f else 2f
                } else {
                    radius = min(cornerRadius.toPx(), maxRadius)
                    weight = 3f
                }
                configureShadowEdge(shaders.first, size.width, size.height, radius, weight, isCapsule, stroke)
                // The drawable material (CONTENT_CAPSULE_4..6) is the AGSL COUIShadowEdgeDrawable,
                // drawn source-over in one pass; the others are framework strokes.
                val framework = !type.drawable
                if (framework) {
                    configureShadowEdge(edgePass, size.width, size.height, radius, weight, isCapsule, stroke)
                    splitFrameworkStroke(shadowPass = shaders.first, edgePass = edgePass)
                }
                val shadowBrush = shaders.first.asBrush()
                val edgeBrush = if (framework) edgePass.asBrush() else null

                onDrawWithContent {
                    val glow = spotlight.progress.value
                    if (glow > 0f) {
                        val light = spotlight.position.value
                        SpotLightShader.setUniforms(
                            shader = shaders.second,
                            lightX = light.x,
                            lightY = light.y,
                            radius = SpotLightShader.radius(glow, spotlight.extinguishing),
                            intensity = spotIntensity * glow,
                        )
                        drawRect(brush = shaders.second.asBrush(), blendMode = spotBlend)
                    }
                    // The edge shader carries its own SDF mask.
                    drawRect(shadowBrush)
                    if (edgeBrush != null) drawRect(edgeBrush, blendMode = FrameworkEdgeBlendMode)
                    drawContent()
                }
            }
    }

    /** `ShadowUtils` level 6: 16dp elevation at alpha 30/255, lit from 160dp above (r 800dp). */
    private val MaterialShadow = Shadow(
        radius = 16.dp,
        color = Color.Black,
        offset = DpOffset(0.dp, 3.2.dp),
        alpha = 30f / 255f,
    )

    /**
     * How far a [materialPanel] shadow reaches past the panel. A layer that fades, scales or blurs
     * a material panel grows its bounds by this much (`graphicsLayerWithOverflow`), or the shadow
     * is cut off while the layer renders offscreen.
     */
    internal val ShadowOverflow: Dp
        get() = MaterialShadow.layerOverflow()

    // TYPE_FRAMEWORK_TOP_BAR colour filters (COUIMaterialBlurEffect).
    private val FilterLight1 = Color(0x66333333)
    private val FilterLight2 = Color(0x99F5F5F5)
    private val FilterDark1 = Color(0xCC262626)
    private val FilterDark2 = Color(0xB37F7F7F)

    // coui_color_hover.
    internal val HoverMaskLight = Color(0x14000000)
    internal val HoverMaskDark = Color(0x26FFFFFF)
}

/**
 * The top-bar material presets (`COUIToolbar.setMaterialEffectType`, `supportMaterialType`) and the
 * stroke presets `ToolbarMaterialEffectResolver` picks for them. `COUIMaterialEffect.materialPanel`
 * draws [Default].
 */
internal enum class TopBarMaterialLevel {
    /** Preset 0: `TYPE_FRAMEWORK_TOP_BAR` fill, `CAPSULE_1` / `CIRCLE_1` stroke. */
    Default,

    /** Preset 1 (immersive): `TYPE_FRAMEWORK_TOP_BAR1` fill, `CAPSULE_7` / `CIRCLE_3` stroke. */
    Immersive,

    /** Preset 2: `TYPE_FRAMEWORK_TOP_BAR_BLUR` fill, `CAPSULE_1` / `CIRCLE_1` stroke. */
    DefaultBlur,
    ;

    /** `ToolbarMaterialEffectResolver.getCapsuleStrokeEffectType` / `getCircleStrokeEffectType`. */
    fun stroke(isCapsule: Boolean, isDark: Boolean): StrokeEffectParams = when {
        this == Immersive -> if (isCapsule) ShadowEdgePresets.Capsule7 else ShadowEdgePresets.Circle3
        isCapsule -> if (isDark) ShadowEdgePresets.CapsuleDark else ShadowEdgePresets.CapsuleLight
        else -> if (isDark) ShadowEdgePresets.CircleDark else ShadowEdgePresets.CircleLight
    }

    /**
     * `ToolbarMaterialEffectResolver.getCapsuleStrokeEffectTypeInWindow` /
     * `getCircleStrokeEffectTypeInWindow`: the AGSL `COUIShadowEdgeDrawable` stroke
     * `ToolbarMaterialEffectDelegate.applyBlurWindowBackground` draws, source-over, when the button
     * sits in a blurred window. `CAPSULE_9` / `CIRCLE_5` for [Immersive], `CAPSULE_8` / `CIRCLE_4`
     * otherwise.
     */
    fun strokeInWindow(isCapsule: Boolean, isDark: Boolean): StrokeEffectParams = when {
        this == Immersive -> if (isCapsule) ShadowEdgePresets.Capsule9 else ShadowEdgePresets.Circle5
        isCapsule -> if (isDark) ShadowEdgePresets.Capsule8Dark else ShadowEdgePresets.Capsule8Light
        else -> if (isDark) ShadowEdgePresets.Circle4Dark else ShadowEdgePresets.Circle4Light
    }

    /**
     * The opaque fill `applyBlurWindowBackground` lays under [strokeInWindow]
     * (`BLUR_WINDOW_VIEW_BG_COLOR_LIGHT` `#FFF0F0F0`, `_DARK` `#FF333333`, `_IMMERSIVE` `#26FFFFFF`).
     */
    fun windowFill(isDark: Boolean): Color = when {
        this == Immersive -> WindowFillImmersive
        isDark -> WindowFillDark
        else -> WindowFillLight
    }

    private companion object {
        val WindowFillLight = Color(0xFFF0F0F0)
        val WindowFillDark = Color(0xFF333333)
        val WindowFillImmersive = Color(0x26FFFFFF)
    }
}

/**
 * The drawable-material panel strokes of the bottom bars and the segment button, drawn by the AGSL
 * `COUIShadowEdgeDrawable` only after the widget's drawable-material setter is called
 * (`mIsDrawableMaterialEffect` / `mDrawMaterialEffect` start `false`).
 */
internal object DrawableMaterialStrokes {
    /** `BaseBottomFloatingMenuView` (floating navigation bar): `TYPE_FRAMEWORK_CAPSULE_12`. */
    fun navigationBar(isDark: Boolean): StrokeEffectParams = if (isDark) ShadowEdgePresets.Capsule12Dark else ShadowEdgePresets.Capsule12Light

    /** `COUIBottomFloatingToolbarMenuView` (floating toolbar): `TYPE_FRAMEWORK_CAPSULE_13`. */
    fun toolbar(isDark: Boolean): StrokeEffectParams = if (isDark) ShadowEdgePresets.Capsule13Dark else ShadowEdgePresets.Capsule13Light

    /** `COUISegmentButtonLayout` background: `TYPE_FRAMEWORK_CAPSULE_11`. */
    fun segmentButton(isDark: Boolean): StrokeEffectParams = if (isDark) ShadowEdgePresets.Capsule11Dark else ShadowEdgePresets.Capsule11Light
}

/**
 * The `COUIButton` types that carry a material layer (`buttonType` 1, 2 and 3), in the
 * framework-blur variant and in the `drawableMaterialEffect` variant that draws its own panel
 * (the dialog buttons once `COUIButtonBarLayout.setBlurBackgroundWindow(true)` switches them to
 * `FOLLOW_SYSTEM`).
 */
internal enum class ContentButtonMaterialType(
    /** The touch spotlight preset. */
    val spotLight: SpotLightType,
    /**
     * Whether this is the `drawableMaterialEffect` variant (`initShadowEdgeParamsWithoutBlur`:
     * `CONTENT_CAPSULE_4/5/6`, stroke weight 2).
     */
    val drawable: Boolean = false,
) {
    /** `buttonType = primary`: `CONTENT_CAPSULE_1` stroke, `TYPE_OPAQUE_MEDIUM_1` spotlight. */
    Primary(SpotLightType.OpaqueMedium1),

    /** `buttonType = secondary`: `CONTENT_CAPSULE_2` stroke, `TYPE_OPAQUE_MEDIUM_2` spotlight. */
    Secondary(SpotLightType.OpaqueMedium2),

    /** `buttonType = transparent`: `CONTENT_CAPSULE_3` stroke, `TYPE_TRANSPARENT_MEDIUM_1` spotlight. */
    Transparent(SpotLightType.TransparentMedium1),

    /** Drawable-material primary: `CONTENT_CAPSULE_4` stroke, `TYPE_OPAQUE_MEDIUM_1` spotlight. */
    DrawablePrimary(SpotLightType.OpaqueMedium1, drawable = true),

    /** Drawable-material secondary: `CONTENT_CAPSULE_5` stroke, `TYPE_OPAQUE_MEDIUM_2` spotlight. */
    DrawableSecondary(SpotLightType.OpaqueMedium2, drawable = true),

    /** Drawable-material transparent: `CONTENT_CAPSULE_6` stroke, `TYPE_TRANSPARENT_MEDIUM_1` spotlight. */
    DrawableTransparent(SpotLightType.TransparentMedium1, drawable = true),
    ;

    /**
     * The `COUIButton.applyBlurEffect` view blur preset that replaces the fill of a non-drawable
     * button while the system material blur is on, or null for the drawable variants, which
     * never blur (`setViewBlurEffect(TYPE_NO_EFFECT)`).
     *
     * - [Primary]: `COUIMaterialThemeBlurEffect` `TYPE_CONTENT_PRIMARY_BUTTON`, 10px,
     *   `?couiColorContainerTheme` ([fill]) re-alpha'd to 229 (light) / 230 (dark) SRC_OVER, then
     *   `#00FFFFFF` (light) / `#19FFFFFF` (dark) OVERLAY; disabled
     *   (`TYPE_CONTENT_DISABLED_PRIMARY_BUTTON`) the fill at alpha 77, then `#19FFFFFF` OVERLAY.
     * - [Secondary]: `TYPE_CONTENT_SECONDARY_BUTTON`, 25px, `#CCFFFFFF` (light) / `#33FFFFFF`
     *   (dark) SRC_OVER, then a no-op `#00FFFFFF` SRC_OVER.
     * - [Transparent]: `TYPE_CONTENT_TRANSPARENT_BUTTON`, 25px, `#3F333333` OVERLAY, then
     *   `#3FFFFFFF` LUMINOSITY, in both schemes.
     *
     * @param isDark Whether the dark scheme is active.
     * @param enabled Whether the button is enabled.
     * @param fill The button's theme fill, the colour the primary preset re-alphas.
     */
    fun backdropSpec(isDark: Boolean, enabled: Boolean, fill: Color): MaterialBackdropSpec? = when (this) {
        Primary -> if (enabled) {
            MaterialBackdropSpec(
                blurRadiusPx = PrimaryBlurRadiusPx,
                layer1 = fill.copy(alpha = (if (isDark) 230f else 229f) / 255f),
                mode1 = BlendMode.SrcOver,
                layer2 = if (isDark) Color(0x19FFFFFF) else Color(0x00FFFFFF),
                mode2 = BlendMode.Overlay,
            )
        } else {
            MaterialBackdropSpec(
                blurRadiusPx = PrimaryBlurRadiusPx,
                layer1 = fill.copy(alpha = 77f / 255f),
                mode1 = BlendMode.SrcOver,
                layer2 = Color(0x19FFFFFF),
                mode2 = BlendMode.Overlay,
            )
        }

        Secondary -> if (isDark) SecondaryBackdropDark else SecondaryBackdropLight

        Transparent -> TransparentBackdrop

        DrawablePrimary, DrawableSecondary, DrawableTransparent -> null
    }

    /** `COUIMaterialStrokeEffect.CONTENT_CAPSULE_PARAMS_{1,2,3}[_DISABLED]_{LIGHT,DARK}`. */
    fun stroke(isDark: Boolean, enabled: Boolean): StrokeEffectParams = when (this) {
        Primary -> when {
            enabled && isDark -> ShadowEdgePresets.ContentCapsule1Dark
            enabled -> ShadowEdgePresets.ContentCapsule1Light
            isDark -> ShadowEdgePresets.ContentCapsule1DisabledDark
            else -> ShadowEdgePresets.ContentCapsule1DisabledLight
        }

        Secondary -> when {
            enabled && isDark -> ShadowEdgePresets.ContentCapsule2Dark
            enabled -> ShadowEdgePresets.ContentCapsule2Light
            isDark -> ShadowEdgePresets.ContentCapsule2DisabledDark
            else -> ShadowEdgePresets.ContentCapsule2DisabledLight
        }

        Transparent -> if (enabled) ShadowEdgePresets.ContentCapsule3 else ShadowEdgePresets.ContentCapsule3Disabled

        DrawablePrimary -> when {
            enabled && isDark -> ShadowEdgePresets.ContentCapsule4Dark
            enabled -> ShadowEdgePresets.ContentCapsule4Light
            isDark -> ShadowEdgePresets.ContentCapsule4DisabledDark
            else -> ShadowEdgePresets.ContentCapsule4DisabledLight
        }

        DrawableSecondary -> when {
            enabled && isDark -> ShadowEdgePresets.ContentCapsule5Dark
            enabled -> ShadowEdgePresets.ContentCapsule5Light
            isDark -> ShadowEdgePresets.ContentCapsule5DisabledDark
            else -> ShadowEdgePresets.ContentCapsule5DisabledLight
        }

        DrawableTransparent -> when {
            enabled && isDark -> ShadowEdgePresets.ContentCapsule6Dark
            enabled -> ShadowEdgePresets.ContentCapsule6Light
            isDark -> ShadowEdgePresets.ContentCapsule6DisabledDark
            else -> ShadowEdgePresets.ContentCapsule6DisabledLight
        }
    }

    private companion object {
        /** The primary button (and FAB) blur radius, 10 raw pixels. */
        val PrimaryBlurRadiusPx = 10f

        /** The secondary and transparent button blur radius, 25 raw pixels. */
        val ContentBlurRadiusPx = 25f

        val SecondaryBackdropLight = MaterialBackdropSpec(
            blurRadiusPx = ContentBlurRadiusPx,
            layer1 = Color(0xCCFFFFFF),
            mode1 = BlendMode.SrcOver,
            layer2 = Color(0x00FFFFFF),
            mode2 = BlendMode.SrcOver,
        )

        val SecondaryBackdropDark = MaterialBackdropSpec(
            blurRadiusPx = ContentBlurRadiusPx,
            layer1 = Color(0x33FFFFFF),
            mode1 = BlendMode.SrcOver,
            layer2 = Color(0x00FFFFFF),
            mode2 = BlendMode.SrcOver,
        )

        val TransparentBackdrop = MaterialBackdropSpec(
            blurRadiusPx = ContentBlurRadiusPx,
            layer1 = Color(0x3F333333),
            mode1 = BlendMode.Overlay,
            layer2 = Color(0x3FFFFFFF),
            mode2 = BlendMode.Luminosity,
        )
    }
}
