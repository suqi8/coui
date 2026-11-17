// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.suqi8.coui.kmp.effect.FrameworkEdgeBlendMode
import io.github.suqi8.coui.kmp.effect.LocalCOUIMaterialEffectEnabled
import io.github.suqi8.coui.kmp.effect.LocalMaterialBackdrop
import io.github.suqi8.coui.kmp.effect.MaterialBackdropSpec
import io.github.suqi8.coui.kmp.effect.ShadowEdgePresets
import io.github.suqi8.coui.kmp.effect.ShadowEdgeShader
import io.github.suqi8.coui.kmp.effect.SpotLightShader
import io.github.suqi8.coui.kmp.effect.SpotLightState
import io.github.suqi8.coui.kmp.effect.SpotLightType
import io.github.suqi8.coui.kmp.effect.StrokeEffectParams
import io.github.suqi8.coui.kmp.effect.couiFocusStroke
import io.github.suqi8.coui.kmp.effect.couiTouchMotion
import io.github.suqi8.coui.kmp.effect.splitFrameworkStroke
import io.github.suqi8.coui.kmp.shader.RuntimeShader
import io.github.suqi8.coui.kmp.shader.asBrush
import io.github.suqi8.coui.kmp.shader.isRuntimeShaderSupported
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.LocalContentColor
import io.github.suqi8.coui.kmp.utils.CouiHapticEffect
import io.github.suqi8.coui.kmp.utils.graphicsLayerWithOverflow
import io.github.suqi8.coui.kmp.utils.rememberCouiHaptic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
 *   present the fill is replaced by the `TYPE_FRAMEWORK_FAB` backdrop blur. A touch lights the
 *   `TYPE_OPAQUE_SMALL_1` spotlight under the finger, over the fill and the icon and clipped to the
 *   button (`FloatingButtonWrapper`). With [material] off the opaque fill takes the `couiColorPress`
 *   mask while pressed.
 * - **Disabled**: the fill turns to [disabledContainerColor], touches are ignored and the material
 *   switches to its disabled presets.
 * - **Show / hide**: toggling [visible] plays `animationFloatingButtonEnlarge` /
 *   `animationFloatingButtonShrink` (alpha 0 <-> 1, scale 0.6 <-> 1 over 350ms on
 *   COUIMoveEaseInterpolator); once hidden the button leaves the layout (`View.GONE`). Drive it
 *   from [rememberFloatingActionButtonScrollState] for the COUI hide-on-scroll behavior.
 *
 * The COUI color presets are in [FloatingActionButtonDefaults]: the theme primary (`Main`), the
 * neutral [FloatingActionButtonDefaults.secondaryContainerColor] (`Secondary`) and the translucent
 * [FloatingActionButtonDefaults.translateContainerColor] (`Translate`), each at the 56dp or the
 * 48dp [FloatingActionButtonDefaults.TinySize] size. The `FloatingButton.Icon.*` styles carrying
 * those presets also turn the shadow off (`fabNeedElevation = false`): pass `shadowElevation = 0.dp`
 * with them.
 *
 * @param onClick The callback when the [FloatingActionButton] is clicked.
 * @param modifier The modifier to be applied to the [FloatingActionButton].
 * @param enabled Whether the [FloatingActionButton] is enabled.
 * @param visible Whether the [FloatingActionButton] is shown. Changes play the COUI enlarge / shrink
 *   animation; a hidden button ignores touches.
 * @param material Whether the COUI 17 material (edge lighting, touch spotlight and, with a backdrop
 *   provider, the backdrop blur) is drawn, as on ColorOS 17 with its system material switches on.
 *   Defaults to [LocalCOUIMaterialEffectEnabled].
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
    visible: Boolean = true,
    material: Boolean = LocalCOUIMaterialEffectEnabled.current,
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
    // animationFloatingButtonEnlarge / animationFloatingButtonShrink: 1 = shown, 0 = hidden.
    val shown = remember { Animatable(if (visible) 1f else 0f) }
    LaunchedEffect(visible) {
        shown.animateTo(if (visible) 1f else 0f, tween(FAB_SHOW_HIDE_MILLIS, easing = FabCollapseEasing))
    }
    // Derived so that the running animation only redraws the layer instead of recomposing.
    val gone by remember { derivedStateOf { shown.value == 0f && shown.targetValue == 0f } }
    val atRest by remember { derivedStateOf { shown.value == 1f } }
    if (!visible && gone) return
    val showHideModifier = if (!atRest) {
        Modifier.graphicsLayerWithOverflow(FabItemLayerOverflow) {
            val progress = shown.value
            alpha = progress
            val scale = FAB_HIDDEN_SCALE + (1f - FAB_HIDDEN_SCALE) * progress
            scaleX = scale
            scaleY = scale
        }
    } else {
        Modifier
    }
    // A hiding button keeps its colors but ignores touches.
    val interactive = enabled && visible

    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val isDark = COUITheme.colorScheme.isDark
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

    // FloatingButtonWrapper: the TYPE_OPAQUE_SMALL_1 touch spotlight, on while the material
    // spotlight switch is on and the button is enabled (setSpotLightEnabled / setEnabled).
    val spotLightActive = materialActive && enabled
    val spotLight = rememberPopupSpotLight()
    val spotLightShader = remember(materialActive) { if (materialActive) RuntimeShader(SpotLightShader.Source) else null }

    val haptic = rememberCouiHaptic()
    val currentHaptic by rememberUpdatedState(haptic)
    val hapticModifier = if (interactive && hapticFeedbackEnabled) {
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
            .then(showHideModifier)
            .onGloballyPositioned { centerYInWindow.floatValue = it.positionInWindow().y + it.size.height / 2f }
            // The wrapper carries the shadow, the material and the button, and the touch motion
            // scales and moves them all together.
            .couiTouchMotion(interactionSource = interactionSource, enabled = interactive)
            // COUIFloatingButton's TYPE_OUTER COUIStrokeDrawable, a capsule around the main button.
            .couiFocusStroke(interactionSource = interactionSource, shape = shape, enabled = interactive)
            .then(shadowModifier)
            .then(backdropModifier ?: Modifier)
            .clip(shape)
            .then(
                if (spotLightActive && spotLightShader != null) {
                    Modifier
                        .popupSpotLightInput(spotLight, enabled = true)
                        .fabSpotLight(spotLightShader, spotLight, isDark)
                } else {
                    Modifier
                },
            )
            .background(fillColor)
            .then(if (materialActive) Modifier.fabMaterialStroke(isDark, enabled) else Modifier)
            .then(hapticModifier)
            .clickable(
                interactionSource = interactionSource,
                // COUIFloatingButton.ensureBlurEffect turns the press mask off with the material.
                indication = if (materialActive) null else LocalIndication.current,
                enabled = interactive,
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

/**
 * The `TYPE_OPAQUE_SMALL_1` touch spotlight of `FloatingButtonWrapper.draw`: drawn after the
 * button and its icon, clipped to the button outline (the oval `mDrawPath`; here the clip of the
 * shape this modifier follows). Only reads animation state while drawing, so it allocates nothing
 * per frame beyond the shader brush.
 */
private fun Modifier.fabSpotLight(shader: RuntimeShader, spotLight: SpotLightState, isDark: Boolean): Modifier = drawWithContent {
    drawContent()
    val glow = spotLight.progress.value
    if (glow <= 0f) return@drawWithContent
    val light = spotLight.position.value
    val type = SpotLightType.OpaqueSmall1
    SpotLightShader.setUniforms(
        shader = shader,
        lightX = light.x,
        lightY = light.y,
        radius = SpotLightShader.radius(glow, spotLight.extinguishing, type.endRadius),
        intensity = type.endIntensity(isDark) * glow,
    )
    drawRect(brush = shader.asBrush(), blendMode = type.blendMode(isDark))
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

    /**
     * The rotation of the [ExpandableFloatingActionButton] main button content while expanded
     * (COUIFloatingButton `DEFAULT_ROTATE_ANGLE`).
     */
    val ExpandedRotation = 45f

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
        return scheme.disable.compositeOver(if (scheme.isDark) DisabledBaseDark else DisabledBaseLight)
    }

    private val DisabledBaseLight = Color(0xFFFFFFFF)
    private val DisabledBaseDark = Color(0xFF4D4D4D)

    /** The size of the tiny tier (COUI `FloatingButton.Icon.Tiny` `fabButtonSize`). */
    val TinySize = 48.dp

    /**
     * How long a button hidden by [rememberFloatingActionButtonScrollState] waits before it shows
     * itself again (COUIFloatingButton `DELAY_TIME_NO_ACTION_SLIDE_OUT`, `AutoDismissRunnable`).
     */
    val AutoShowDelayMillis = 5000L

    /**
     * The fill of the secondary preset (`FloatingButton.Icon.*.Secondary`,
     * `coui_floating_button_secondary_button_color_selector`: `coui_button_neutral_color`
     * #0F000000 / #4CFFFFFF).
     */
    @Composable
    fun secondaryContainerColor(): Color = COUITheme.colorScheme.neutralButton

    /**
     * The disabled fill of the secondary preset: couiColorPressBackground (#14000000 /
     * #26FFFFFF).
     */
    @Composable
    fun secondaryDisabledContainerColor(): Color = COUITheme.colorScheme.pressBackground

    /**
     * The fill of the translucent preset laid over images (`FloatingButton.Icon.*.Translate`,
     * `coui_floating_button_translate_button_color_selector`: #33FFFFFF in every state).
     */
    @Composable
    fun translateContainerColor(): Color = COUITheme.colorScheme.transparentButton
}

/**
 * The hide-on-scroll state of a [FloatingActionButton], after COUI
 * `COUIFloatingButton.ScrollViewBehavior`: a scroll step of more than 10px towards the end of the
 * content hides the button (`animationFloatingButtonSlideOut`, the shrink animation), a step of more
 * than 10px back towards the start shows it, and a hidden button shows itself again
 * [autoShowDelayMillis] after it was hidden (`AutoDismissRunnable`).
 *
 * Attach [nestedScrollConnection] to the scrolling container's parent with `Modifier.nestedScroll`
 * and pass [visible] to the button.
 */
@Stable
class FloatingActionButtonScrollState internal constructor(
    private val scope: CoroutineScope,
    private val autoShowDelayMillis: Long,
) {
    /** Whether the button should be shown. */
    var visible: Boolean by mutableStateOf(true)
        private set

    private var autoShowJob: Job? = null

    /** The connection that hides and shows the button while the content scrolls. */
    val nestedScrollConnection: NestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            when {
                available.y < -FAB_SCROLL_THRESHOLD_PX -> hide()
                available.y > FAB_SCROLL_THRESHOLD_PX -> show()
            }
            return Offset.Zero
        }
    }

    /** Shows the button (`COUIFloatingButton.show`). */
    fun show() {
        autoShowJob?.cancel()
        autoShowJob = null
        visible = true
    }

    /** Hides the button (`COUIFloatingButton.hide`), and schedules it to show again when enabled. */
    fun hide() {
        visible = false
        autoShowJob?.cancel()
        autoShowJob = if (autoShowDelayMillis > 0) {
            scope.launch {
                delay(autoShowDelayMillis)
                visible = true
            }
        } else {
            null
        }
    }
}

/**
 * Creates and remembers a [FloatingActionButtonScrollState].
 *
 * @param autoShowDelayMillis How long a hidden button waits before it shows itself again; 0 or less
 *   keeps it hidden until the content scrolls back (`setAutoSlideInDisable`).
 */
@Composable
fun rememberFloatingActionButtonScrollState(
    autoShowDelayMillis: Long = FloatingActionButtonDefaults.AutoShowDelayMillis,
): FloatingActionButtonScrollState {
    val scope = rememberCoroutineScope()
    return remember(scope, autoShowDelayMillis) { FloatingActionButtonScrollState(scope, autoShowDelayMillis) }
}

/**
 * One child action of an [ExpandableFloatingActionButton], after COUI `COUIFloatingButtonItem`:
 * a child floating action button with an optional label capsule before it.
 *
 * @param icon The icon of the child button, drawn in its content color.
 * @param onClick Called when the child button or its label is clicked (COUI `onActionSelected`).
 *   The menu stays open, as in COUI; collapse it from here when needed.
 * @param label The text of the label capsule, or null for a bare child button.
 * @param enabled Whether the child button and its label are enabled.
 * @param containerColor The fill of the child button; [Color.Unspecified] takes the theme primary
 *   color (COUI `couiColorPrimary`).
 * @param labelContainerColor The fill of the label capsule; [Color.Unspecified] takes the theme
 *   primary color (COUI `couiColorPrimary`).
 * @param labelColor The label text color; [Color.Unspecified] takes white
 *   (`coui_floating_button_label_text_color_enable`).
 */
@Stable
class FloatingActionButtonItem(
    val icon: @Composable () -> Unit,
    val onClick: () -> Unit,
    val label: String? = null,
    val enabled: Boolean = true,
    val containerColor: Color = Color.Unspecified,
    val labelContainerColor: Color = Color.Unspecified,
    val labelColor: Color = Color.Unspecified,
)

/**
 * An expandable floating action button (speed dial) with COUI style, after COUI 17
 * `COUIFloatingButton` with `COUIFloatingButtonItem` / `COUIFloatingButtonLabel` children.
 *
 * Clicking the main button toggles [expanded]. While expanded the [items] stand in a column above
 * the main button, [items] index 0 nearest to it: 32dp above the main button
 * (`coui_floating_button_item_first_bottom_margin`) and 16dp apart
 * (`coui_floating_button_item_normal_bottom_margin`), each child button 56dp
 * (`coui_floating_button_size`) with its label capsule 12dp before it
 * (`coui_floating_button_fab_side_margin`).
 *
 * - **Expand** (`animationFloatingButtonMenuExpand`): the farthest item starts first, each next one
 *   50ms later. An item springs up from the main button (stiffness 500, damping ratio 0.8) while its
 *   child button and label scale from 0.6 and the child button fades in over 300ms and the label
 *   over 350ms, all on `PathInterpolator(0.25, 0.1, 0.25, 1)`; the label grows from its top end
 *   corner.
 * - **Collapse** (`animationFloatingButtonMenuClose` / `narrowFloatingButton`): the nearest item
 *   starts first, each next one 50ms later; it slides back into the main button over 300ms on
 *   COUIMoveEaseInterpolator while scaling to 0.6 and fading out, the label over 200ms from its
 *   bottom end corner.
 * - **Main button**: the content rotates by [rotation] (45 degrees) over 300ms
 *   (`rotateForward` / `rotateBackward`).
 * - **Material**: the main and child buttons take the [FloatingActionButton] material
 *   (`TYPE_FRAMEWORK_FAB` blur, `TYPE_FRAMEWORK_CIRCLE_2` stroke); the labels take the primary
 *   button material (`TYPE_CONTENT_PRIMARY_BUTTON` blur, `TYPE_CONTENT_CAPSULE_1` stroke) through
 *   [Button].
 *
 * The component measures as the main button alone and places the items above it, outside its own
 * bounds, the way the collapsed COUI menu keeps its labels hidden behind the main button: in a
 * [Scaffold] `floatingActionButton` slot the main button keeps its place (with any [FabPosition])
 * and the snack bar does not jump when the menu opens. Leave room above the button and do not clip
 * its parent there. Like COUI, the menu has no scrim and does not close on an outside touch; an
 * item click is ignored while the menu is collapsing.
 *
 * @param expanded Whether the items are shown.
 * @param onExpandedChange Called with the new expanded state when the main button is clicked.
 * @param items The child actions, index 0 nearest to the main button.
 * @param modifier The modifier to be applied to the [ExpandableFloatingActionButton].
 * @param enabled Whether the main button is enabled.
 * @param visible Whether the main button is shown, see [FloatingActionButton]. Hiding an expanded
 *   menu also collapses it through [onExpandedChange] (`ScrollViewBehavior` closes the menu as it
 *   slides the button out).
 * @param material Whether the buttons draw the COUI 17 material, see [FloatingActionButton].
 * @param hapticFeedbackEnabled Whether a touch down on a button plays the press haptic.
 * @param containerColor The fill of the main button.
 * @param disabledContainerColor The fill of the main button when disabled.
 * @param contentColor The content color of the main button.
 * @param rotation The rotation of the main button content while expanded, in degrees.
 * @param content The content of the main button, typically an add icon.
 */
@Composable
fun ExpandableFloatingActionButton(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    items: List<FloatingActionButtonItem>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    visible: Boolean = true,
    material: Boolean = LocalCOUIMaterialEffectEnabled.current,
    hapticFeedbackEnabled: Boolean = true,
    containerColor: Color = COUITheme.colorScheme.primary,
    disabledContainerColor: Color = FloatingActionButtonDefaults.disabledContainerColor(),
    contentColor: Color = COUITheme.colorScheme.onPrimary,
    rotation: Float = FloatingActionButtonDefaults.ExpandedRotation,
    content: @Composable () -> Unit,
) {
    // The items stay composed until the last one has slid back into the main button.
    var itemsComposed by remember { mutableStateOf(expanded) }
    LaunchedEffect(expanded, items.size) {
        if (expanded) {
            itemsComposed = true
        } else {
            delay((items.size - 1).coerceAtLeast(0) * FAB_ITEM_STAGGER_MILLIS + FAB_MENU_DURATION_MILLIS)
            itemsComposed = false
        }
    }
    val contentRotation by animateFloatAsState(
        targetValue = if (expanded) rotation else 0f,
        animationSpec = tween(durationMillis = FAB_MENU_DURATION_MILLIS.toInt(), easing = FabExpandEasing),
        label = "fabRotation",
    )
    val currentOnExpandedChange by rememberUpdatedState(onExpandedChange)
    val currentExpanded by rememberUpdatedState(expanded)
    LaunchedEffect(visible) {
        if (!visible && currentExpanded) currentOnExpandedChange(false)
    }

    Layout(
        content = {
            // Composed first so they draw below the main button they slide out of.
            if (expanded || itemsComposed) {
                for (index in items.indices) {
                    ExpandableFabItem(
                        item = items[index],
                        index = index,
                        count = items.size,
                        expanded = expanded,
                        material = material,
                        hapticFeedbackEnabled = hapticFeedbackEnabled,
                    )
                }
            }
            // Wrapped so the layout always has a main measurable, also once the button is hidden.
            Box {
                FloatingActionButton(
                    onClick = { currentOnExpandedChange(!currentExpanded) },
                    enabled = enabled,
                    visible = visible,
                    material = material,
                    hapticFeedbackEnabled = hapticFeedbackEnabled,
                    containerColor = containerColor,
                    disabledContainerColor = disabledContainerColor,
                    contentColor = contentColor,
                ) {
                    Box(
                        modifier = Modifier.graphicsLayer { rotationZ = contentRotation },
                        contentAlignment = Alignment.Center,
                    ) {
                        content()
                    }
                }
            }
        },
        modifier = modifier,
    ) { measurables, constraints ->
        // The main button is measured on its own with loose constraints so it keeps its 56dp
        // circle whatever the items take: the items never share its height and a stretching
        // parent (fillMaxSize, a tight slot) cannot flatten or widen it.
        val main = measurables.last().measure(constraints.copy(minWidth = 0, minHeight = 0))
        // The items may be wider than the main button: only the available width bounds them.
        val itemConstraints = Constraints(maxWidth = constraints.maxWidth)
        val itemPlaceables = List(measurables.size - 1) { measurables[it].measure(itemConstraints) }
        val width = main.width.coerceIn(constraints.minWidth, constraints.maxWidth)
        val height = main.height.coerceIn(constraints.minHeight, constraints.maxHeight)
        layout(width, height) {
            // The main button sits in the bottom end corner of a larger forced size.
            val mainX = width - main.width
            val mainY = height - main.height
            // Item 0 right above the main button, each next one above the previous; all end
            // aligned with the main button (placeRelative mirrors this in RTL).
            var bottom = mainY
            itemPlaceables.forEach { placeable ->
                bottom -= placeable.height
                placeable.placeRelative(mainX + main.width - placeable.width, bottom)
            }
            main.placeRelative(mainX, mainY)
        }
    }
}

/** One label + child button row of an [ExpandableFloatingActionButton] (`COUIFloatingButtonLabel`). */
@Composable
private fun ExpandableFabItem(
    item: FloatingActionButtonItem,
    index: Int,
    count: Int,
    expanded: Boolean,
    material: Boolean,
    hapticFeedbackEnabled: Boolean,
) {
    val density = LocalDensity.current
    // getTotalLabelHeight: 88dp for the first item, 72dp more for each next one.
    val collapsedOffset = with(density) { (FabFirstItemTravel + FabItemTravel * index).toPx() }
    val translation = remember { Animatable(collapsedOffset) }
    val scale = remember { Animatable(FAB_ITEM_COLLAPSED_SCALE) }
    val buttonAlpha = remember { Animatable(0f) }
    val labelAlpha = remember { Animatable(0f) }
    var growing by remember { mutableStateOf(true) }
    LaunchedEffect(expanded, collapsedOffset) {
        if (expanded) {
            growing = true
            delay((count - 1 - index) * FAB_ITEM_STAGGER_MILLIS)
            launch { translation.animateTo(0f, FabItemSpring) }
            launch { labelAlpha.animateTo(1f, tween(FAB_LABEL_FADE_IN_MILLIS, easing = FabExpandEasing)) }
            launch { buttonAlpha.animateTo(1f, tween(FAB_MENU_DURATION_MILLIS.toInt(), easing = FabExpandEasing)) }
            scale.animateTo(1f, tween(FAB_MENU_DURATION_MILLIS.toInt(), easing = FabExpandEasing))
        } else {
            growing = false
            delay(index * FAB_ITEM_STAGGER_MILLIS)
            launch { translation.animateTo(collapsedOffset, tween(FAB_MENU_DURATION_MILLIS.toInt(), easing = FabCollapseEasing)) }
            launch { labelAlpha.animateTo(0f, tween(FAB_LABEL_FADE_OUT_MILLIS, easing = FabExpandEasing)) }
            launch { buttonAlpha.animateTo(0f, tween(FAB_MENU_DURATION_MILLIS.toInt(), easing = FabCollapseEasing)) }
            scale.animateTo(FAB_ITEM_COLLAPSED_SCALE, tween(FAB_MENU_DURATION_MILLIS.toInt(), easing = FabCollapseEasing))
        }
    }

    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val primary = COUITheme.colorScheme.primary
    val isDark = COUITheme.colorScheme.isDark
    val buttonColor = item.containerColor.takeOrElse { primary }
    val labelFill = item.labelContainerColor.takeOrElse { primary }
    val labelText = item.labelColor.takeOrElse { Color.White }
    // COUIFloatingButton drops the action listener while the menu closes: a click on an item that
    // is sliding back into the main button does nothing.
    val currentExpanded by rememberUpdatedState(expanded)
    val currentItem by rememberUpdatedState(item)
    val onItemClick = remember { { if (currentExpanded) currentItem.onClick() } }

    Row(
        modifier = Modifier
            .graphicsLayer { translationY = translation.value }
            .padding(bottom = if (index == 0) FabFirstItemBottomMargin else FabItemBottomMargin),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (!item.label.isNullOrEmpty()) {
            Button(
                onClick = onItemClick,
                modifier = Modifier
                    // The fade renders offscreen at the layer bounds: the overflow keeps the
                    // shadow from being cut off while the label fades.
                    .graphicsLayerWithOverflow(FabLabelLayerOverflow) {
                        scaleX = scale.value
                        scaleY = scale.value
                        alpha = labelAlpha.value
                        // The label grows from its top end corner and shrinks to its bottom end one.
                        transformOrigin = TransformOrigin(if (isRtl) 0f else 1f, if (growing) 0f else 1f)
                    }
                    .dropShadow(CircleShape) {
                        radius = FloatingActionButtonDefaults.shadowPenumbra(FabLabelShadowElevation).toPx()
                        offset = Offset(0f, FabLabelShadowOffsetY.toPx())
                        color = labelFill.copy(alpha = FloatingActionButtonDefaults.ShadowAlpha)
                    },
                enabled = item.enabled,
                minWidth = 0.dp,
                minHeight = 0.dp,
                colors = ButtonDefaults.buttonColorsPrimary(
                    color = labelFill,
                    disabledColor = labelFill.copy(alpha = labelFill.alpha * FAB_LABEL_DISABLED_ALPHA),
                    contentColor = labelText,
                    disabledContentColor = if (isDark) FabLabelDisabledTextColorDark else FabLabelDisabledTextColorLight,
                ),
                insideMargin = FabLabelInsideMargin,
                material = if (material) ButtonMaterial.Primary else ButtonMaterial.None,
            ) {
                Text(
                    text = item.label,
                    fontSize = FabLabelTextSize,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = FabLabelMaxWidth),
                )
            }
            Spacer(modifier = Modifier.width(FabLabelSpacing))
        }
        FloatingActionButton(
            onClick = onItemClick,
            modifier = Modifier.graphicsLayerWithOverflow(FabItemLayerOverflow) {
                scaleX = scale.value
                scaleY = scale.value
                alpha = buttonAlpha.value
            },
            enabled = item.enabled,
            material = material,
            hapticFeedbackEnabled = hapticFeedbackEnabled,
            containerColor = buttonColor,
        ) {
            item.icon()
        }
    }
}

/** COUIFloatingButton `ACTION_ANIM_DELAY`: the stagger between two items. */
private const val FAB_ITEM_STAGGER_MILLIS = 50L

/** The expand / collapse duration of an item and the main button rotation. */
private const val FAB_MENU_DURATION_MILLIS = 300L

/** COUIFloatingButton `DEFAULT_ENLARGE_ANIMATION_DURATION`: the label fade-in. */
private const val FAB_LABEL_FADE_IN_MILLIS = 350

/** `narrowFloatingButton`: the label fade-out. */
private const val FAB_LABEL_FADE_OUT_MILLIS = 200

/** COUIFloatingButton `DEFAULT_SCALE_ANIMATION_MIN_VALUE`. */
private const val FAB_ITEM_COLLAPSED_SCALE = 0.6f

/** The label fill alpha when disabled. */
private const val FAB_LABEL_DISABLED_ALPHA = 0.3f

/** COUIFloatingButton `DEFAULT_ENLARGE_ANIMATION_DURATION`: the show / hide animation. */
private const val FAB_SHOW_HIDE_MILLIS = 350

/** `animationFloatingButtonShrink`: the scale of a hidden button. */
private const val FAB_HIDDEN_SCALE = 0.6f

/** `ScrollViewBehavior.behaviorAnimate`: a scroll step must exceed 10px to show or hide the button. */
private const val FAB_SCROLL_THRESHOLD_PX = 10f

/** `PathInterpolator(0.25, 0.1, 0.25, 1)`: expand, label and rotation curve. */
private val FabExpandEasing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1f)

/** COUIMoveEaseInterpolator: the collapse curve. */
private val FabCollapseEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

/** The item rise spring: stiffness 500, damping ratio 0.8. */
private val FabItemSpring = spring<Float>(dampingRatio = 0.8f, stiffness = 500f)

/** `getTotalLabelHeight` for the first item: 56dp button + 32dp margin. */
private val FabFirstItemTravel = 88.dp

/** `getTotalLabelHeight` step: 56dp button + 16dp margin. */
private val FabItemTravel = 72.dp

/** COUI coui_floating_button_item_first_bottom_margin. */
private val FabFirstItemBottomMargin = 32.dp

/** COUI coui_floating_button_item_normal_bottom_margin. */
private val FabItemBottomMargin = 16.dp

/** COUI coui_floating_button_fab_side_margin. */
private val FabLabelSpacing = 12.dp

/** COUI coui_floating_button_item_label_label_text_margin_start/end/top/bottom. */
private val FabLabelInsideMargin = PaddingValues(horizontal = 8.dp, vertical = 4.dp)

/** COUI coui_floating_button_item_label_label_text_size. */
private val FabLabelTextSize = 12.sp

/** COUI coui_floating_button_item_label_label_text_max_width. */
private val FabLabelMaxWidth = 220.dp

/** COUI coui_floating_button_label_text_color_disable. */
private val FabLabelDisabledTextColorLight = Color(0x26000000)

/** COUI coui_floating_button_label_text_color_disable (values-night). */
private val FabLabelDisabledTextColorDark = Color(0x26FFFFFF)

/** The label elevation: `setElevationToFloatingActionButton(label, support_shadow_size_level_three)`. */
private val FabLabelShadowElevation = 12.dp

/** The downward offset of the label shadow. */
private val FabLabelShadowOffsetY = 2.dp

/**
 * How far the child button shadow reaches past the button: the ~12dp penumbra of the 60dp
 * elevation over its Gaussian reach, plus the light-source offset (about 10dp at the screen bottom).
 */
private val FabItemLayerOverflow = 32.dp

/** How far the label shadow reaches past the label: the ~2.4dp penumbra plus the 2dp offset. */
private val FabLabelLayerOverflow = 8.dp
