// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.suqi8.coui.kmp.effect.FrameworkEdgeBlendMode
import io.github.suqi8.coui.kmp.effect.LocalCOUIMaterialEffectEnabled
import io.github.suqi8.coui.kmp.effect.LocalMaterialBackdrop
import io.github.suqi8.coui.kmp.effect.MaterialBackdropSpec
import io.github.suqi8.coui.kmp.effect.ShadowEdgeShader
import io.github.suqi8.coui.kmp.effect.SpotLightShader
import io.github.suqi8.coui.kmp.effect.SpotLightState
import io.github.suqi8.coui.kmp.effect.SpotLightType
import io.github.suqi8.coui.kmp.effect.StrokeEffectParams
import io.github.suqi8.coui.kmp.effect.TouchMotion
import io.github.suqi8.coui.kmp.effect.couiTouchMotion
import io.github.suqi8.coui.kmp.effect.splitFrameworkStroke
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.More
import io.github.suqi8.coui.kmp.shader.RuntimeShader
import io.github.suqi8.coui.kmp.shader.asBrush
import io.github.suqi8.coui.kmp.shader.isRuntimeShaderSupported
import io.github.suqi8.coui.kmp.squircle.SquircleShape
import io.github.suqi8.coui.kmp.squircle.addSquircleRect
import io.github.suqi8.coui.kmp.squircle.isSquircleEnabled
import io.github.suqi8.coui.kmp.squircle.squircleBackground
import io.github.suqi8.coui.kmp.theme.COUIRoundCorner
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.LocalDismissState
import io.github.suqi8.coui.kmp.utils.graphicsLayerWithOverflow
import io.github.suqi8.coui.kmp.utils.layerOverflow
import io.github.suqi8.coui.kmp.window.WindowListPopup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * A [FloatingToolbar] capsule around free [content], arranged either horizontally or vertically:
 * one `COUIBottomFloatingToolbarMenuView` holding custom views (`COUIBottomFloatingToolbarItem`
 * `setCustomView`). The actual placement on screen is handled by the parent, typically Scaffold.
 * For the COUI toolbar of items, groups, red dots and an overflow menu, use the [FloatingToolbar]
 * overload that takes a list of [FloatingToolbarItem]s.
 *
 * @param modifier The modifier to be applied to the [FloatingToolbar].
 * @param visible Whether the toolbar is shown. Changes animate with the COUI 17
 *   `COUIBottomFloatingToolbar` menu view springs: it scales in about its centre (response 0.5,
 *   bounce 0.4) and scales out to nothing (response 0.25, bounce 0), shadow included. A hidden
 *   toolbar keeps its place in the layout but takes no touches and has no semantics.
 * @param materialEffect Whether the COUI 17 bottom-bar material is used. ColorOS 17 draws the
 *   toolbar with `TYPE_FRAMEWORK_BOTTOM_BAR` (a 25px backdrop blur, see
 *   [FloatingToolbarDefaults.backdropSpec]) over a transparent background
 *   (`COUIBottomFloatingToolbarMenuView.updateBlurEffect`). coui-ui has no backdrop capture of its
 *   own: the blur is drawn only when a [LocalMaterialBackdrop] provider is present, and then it
 *   replaces [color]. Without one the toolbar keeps [color], COUI's non-blur fallback. The material
 *   also draws the `TYPE_FRAMEWORK_CAPSULE_5` edge lighting and inner shadow
 *   (`updateStrokeEffect`) and the `TYPE_BOTTOM_NAVIGATION` touch spotlight under the finger
 *   (`initTouchEffectDrawable`), both above the fill and below [content]; these need runtime
 *   shaders (Android 13+ and every Skia target) and are skipped elsewhere. Defaults to
 *   [LocalCOUIMaterialEffectEnabled].
 * @param color Background color of the [FloatingToolbar]: COUI's non-blur fallback
 *   `coui_tab_navigation_view_bg` (`?couiColorBar`) by default.
 * @param cornerRadius Corner radius of the [FloatingToolbar].
 * @param outSidePadding Padding outside the [FloatingToolbar]. The default keeps 16dp on every side,
 *   which centres a 44dp capsule in COUI's 76dp toolbar height.
 * @param shadowElevation The blur radius of the drop shadow, the COUI 17 caustic shadow
 *   (`setCausticShadowEffect`, elevation 24dp at alpha 45); 0 disables it.
 * @param showDivider Whether to show the divider line around the [FloatingToolbar].
 * @param content The [Composable] content of the [FloatingToolbar].
 */
@Composable
fun FloatingToolbar(
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    materialEffect: Boolean = LocalCOUIMaterialEffectEnabled.current,
    color: Color = FloatingToolbarDefaults.defaultColor(),
    cornerRadius: Dp = FloatingToolbarDefaults.CornerRadius,
    outSidePadding: PaddingValues = FloatingToolbarDefaults.OutSidePadding,
    shadowElevation: Dp = FloatingToolbarDefaults.ShadowElevation,
    showDivider: Boolean = false,
    content: @Composable () -> Unit,
) {
    FloatingToolbarCapsule(
        visible = visible,
        materialEffect = materialEffect,
        color = color,
        cornerRadius = cornerRadius,
        outSidePadding = outSidePadding,
        shadowElevation = shadowElevation,
        showDivider = showDivider,
        modifier = modifier,
        content = content,
    )
}

/**
 * The capsule of both [FloatingToolbar]s (`COUIBottomFloatingToolbarMenuView`): the attach /
 * detach scale springs, the material, the shadow, and a hidden capsule's touch and semantics
 * blocking.
 *
 * @param visible Whether the capsule is shown.
 * @param materialEffect Whether the COUI 17 bottom-bar material is drawn.
 * @param color The fill without the material blur.
 * @param cornerRadius The corner radius.
 * @param outSidePadding The padding outside the capsule.
 * @param shadowElevation The blur radius of the drop shadow; 0 disables it.
 * @param showDivider Whether a divider line runs around the capsule.
 * @param modifier The modifier applied outside the capsule.
 * @param cornerWeight The corner weight of the fill, the backdrop and the edge light.
 *   [COUIRoundCorner.UnspecifiedWeight] resolves it per outline, as `addSmoothRoundRect` without a
 *   weight does.
 * @param enterFromHidden Whether the capsule starts hidden and scales in when first shown: a group
 *   the item toolbar inserts (`createMenuView` starts it at scale 0).
 * @param onHidden Called each time the capsule finishes scaling out (`handleMenuHide`).
 * @param content The content of the capsule.
 */
@Composable
internal fun FloatingToolbarCapsule(
    visible: Boolean,
    materialEffect: Boolean,
    color: Color,
    cornerRadius: Dp,
    outSidePadding: PaddingValues,
    shadowElevation: Dp,
    showDivider: Boolean,
    modifier: Modifier = Modifier,
    cornerWeight: Float = COUIRoundCorner.UnspecifiedWeight,
    enterFromHidden: Boolean = false,
    onHidden: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val shape = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }
    val dividerColor = COUITheme.colorScheme.divider

    // MenuViewAnimatorImpl.updateAttachState: the attach / detach scale spring, restarted from the
    // current scale, about the view centre. There is no fade.
    val scale = remember { Animatable(if (visible && !enterFromHidden) 1f else 0f) }
    val currentOnHidden by rememberUpdatedState(onHidden)
    LaunchedEffect(visible) {
        scale.animateTo(
            targetValue = if (visible) 1f else 0f,
            animationSpec = if (visible) FloatingToolbarAttachSpring else FloatingToolbarDetachSpring,
        )
        if (!visible) currentOnHidden?.invoke()
    }

    val dividerMod = if (showDivider) {
        Modifier
            .squircleBackground(color = dividerColor, cornerRadius = cornerRadius, weight = cornerWeight)
            .padding(0.75.dp)
    } else {
        Modifier
    }
    val hasShadow = shadowElevation > 0.dp
    val shadow = remember(shadowElevation) { floatingNavigationBarShadow(shadowElevation) }
    val shadowMod = if (hasShadow) Modifier.dropShadow(shape = shape, shadow = shadow) else Modifier

    // updateBlurEffect: with the material the backdrop blur is the fill and the background turns
    // transparent; without a backdrop the opaque fill stays.
    val backdropProvider = if (materialEffect) LocalMaterialBackdrop.current else null
    val isDark = COUITheme.colorScheme.isDark
    val backdropModifier = if (backdropProvider != null) {
        val spec = FloatingToolbarDefaults.backdropSpec(isDark)
        val backdropShape = remember(cornerRadius, cornerWeight) { SquircleShape(cornerRadius, cornerWeight) }
        remember(backdropProvider, spec, backdropShape) { backdropProvider.materialBackdrop(spec, backdropShape) }
    } else {
        null
    }

    // The stroke and spotlight of the material (COUIBottomFloatingToolbarMenuView.onDraw).
    val shaders = remember(materialEffect) {
        if (materialEffect && isRuntimeShaderSupported()) {
            FloatingToolbarShaders(
                shadowPass = RuntimeShader(ShadowEdgeShader.Source),
                edgePass = RuntimeShader(ShadowEdgeShader.Source),
                spotLight = RuntimeShader(SpotLightShader.Source),
            )
        } else {
            null
        }
    }
    val spotLight = rememberPopupSpotLight()
    val squircleEnabled = isSquircleEnabled()
    val materialModifier = if (shaders != null) {
        Modifier
            // dispatchTouchEvent -> COUISpotLightEffectDrawable.onTouchEvent, observed only.
            .popupSpotLightInput(spotLight, enabled = visible)
            .floatingToolbarMaterial(shaders, spotLight, cornerRadius, cornerWeight, isDark, squircleEnabled)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .padding(outSidePadding)
            // The layer reaches past the toolbar, so a fade or render effect added to it never cuts
            // the shadow off.
            .graphicsLayerWithOverflow(if (hasShadow) shadow.layerOverflow() else 0.dp) {
                val s = scale.value.coerceAtLeast(0f)
                scaleX = s
                scaleY = s
            }
            .then(
                if (visible) {
                    // COUIBottomFloatingToolbarMenuView.onTouchEvent returns true: touches on the
                    // capsule never fall through to what lies under it.
                    Modifier.pointerInput(Unit) {
                        detectTapGestures { /* Consume click */ }
                    }
                } else {
                    // A hidden toolbar is INVISIBLE: no touches, nothing for accessibility.
                    Modifier
                        .clearAndSetSemantics { }
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    awaitPointerEvent(PointerEventPass.Initial).changes.forEach { it.consume() }
                                }
                            }
                        }
                },
            )
            .then(dividerMod)
            .then(shadowMod)
            .then(backdropModifier ?: Modifier)
            .squircleBackground(
                color = if (backdropModifier != null) Color.Transparent else color,
                cornerRadius = cornerRadius,
                weight = cornerWeight,
            )
            // No offscreen layer lies between the fill (or backdrop) above and this draw, so the
            // overlaid edge light lands on them rather than on an empty layer.
            .then(materialModifier),
    ) {
        content()
    }
}

/** The shaders of the toolbar material: the two framework stroke passes and the spotlight. */
private class FloatingToolbarShaders(
    val shadowPass: RuntimeShader,
    val edgePass: RuntimeShader,
    val spotLight: RuntimeShader,
)

/**
 * `COUIBottomFloatingToolbarMenuView.onDraw` with the default framework material
 * (`mIsDrawableMaterialEffect` false): the `TYPE_BOTTOM_NAVIGATION` spotlight clipped to the
 * toolbar outline (`drawTouchEffectDrawable`, `canvas.clipPath(mBackgroundPath)`), then the
 * `TYPE_FRAMEWORK_CAPSULE_5` stroke the renderer adds (`updateStrokeEffect`): the inner shadow
 * source-over and the edge light with [FrameworkEdgeBlendMode]. The items are drawn above both.
 * The outline and stroke uniforms are rebuilt only when the size changes.
 */
private fun Modifier.floatingToolbarMaterial(
    shaders: FloatingToolbarShaders,
    spotLight: SpotLightState,
    cornerRadius: Dp,
    cornerWeight: Float,
    isDark: Boolean,
    squircleEnabled: Boolean,
): Modifier = drawWithCache {
    val radius = min(cornerRadius.toPx(), min(size.width, size.height) / 2f)
    val stroke = if (isDark) FloatingToolbarStrokeDark else FloatingToolbarStrokeLight
    configureFloatingNavigationBarEdge(shaders.shadowPass, size.width, size.height, radius, stroke)
    configureFloatingNavigationBarEdge(shaders.edgePass, size.width, size.height, radius, stroke)
    // The stroke has to trace the fill's outline. COUI always rounds the toolbar fully (radius half
    // the height, computeMaterialCornerWeight 1.0, 2.0 for a lone item), but [cornerRadius] may be
    // smaller: then the fill is a G2 rounded rect, and a weight-1.0 stroke would still draw the full
    // capsule (the shader's capsule ignores the corner radius), a round rim light inside
    // square-ish corners.
    val weight = when {
        cornerWeight == COUIRoundCorner.UnspecifiedWeight -> floatingToolbarCornerWeight(radius, size.width, size.height, squircleEnabled)
        !squircleEnabled -> COUIRoundCorner.CircularWeight
        else -> cornerWeight
    }
    val common = ShadowEdgeShader.commonArray(size.width, size.height, radius, weight)
    shaders.shadowPass.setFloatUniform("u_commonArray", common)
    shaders.edgePass.setFloatUniform("u_commonArray", common)
    splitFrameworkStroke(shadowPass = shaders.shadowPass, edgePass = shaders.edgePass)
    val shadowBrush = shaders.shadowPass.asBrush()
    val edgeBrush = shaders.edgePass.asBrush()
    val outline = Path().apply {
        addSquircleRect(size.width, size.height, radius, cornerWeight, squircleEnabled)
    }
    // TYPE_BOTTOM_NAVIGATION resolves to the TYPE_OPAQUE_MEDIUM_2 params (COUISpotLightEffect.getParamsByType).
    val type = SpotLightType.OpaqueMedium2
    onDrawWithContent {
        val glow = spotLight.progress.value
        if (glow > 0f) {
            val light = spotLight.position.value
            SpotLightShader.setUniforms(
                shader = shaders.spotLight,
                lightX = light.x,
                lightY = light.y,
                radius = SpotLightShader.radius(glow, spotLight.extinguishing, type.endRadius),
                intensity = type.endIntensity(isDark) * glow,
            )
            val spotBrush = shaders.spotLight.asBrush()
            clipPath(outline) {
                drawRect(brush = spotBrush, blendMode = type.blendMode(isDark))
            }
        }
        // The edge shader carries its own SDF mask.
        drawRect(shadowBrush)
        drawRect(edgeBrush, blendMode = FrameworkEdgeBlendMode)
        drawContent()
    }
}

/**
 * The corner weight the toolbar fill resolves to for a `width x height` rect of corner [radius]
 * ([COUIRoundCorner.UnspecifiedWeight] in [squircleBackground]): the soft capsule
 * ([COUIRoundCorner.CapsuleWeight]) when fully rounded, `default_g2_weight`
 * ([COUIRoundCorner.DefaultG2Weight]) otherwise, and plain circular corners
 * ([COUIRoundCorner.CircularWeight]) when squircles are off and the fill falls back to
 * `RoundedCornerShape`.
 */
internal fun floatingToolbarCornerWeight(radius: Float, width: Float, height: Float, squircleEnabled: Boolean): Float = when {
    !squircleEnabled -> COUIRoundCorner.CircularWeight
    radius >= min(width, height) / 2f -> COUIRoundCorner.CapsuleWeight
    else -> COUIRoundCorner.DefaultG2Weight
}

/** `COUIMaterialStrokeEffect.FRAMEWORK_CAPSULE_PARAMS_5_LIGHT` (`TYPE_FRAMEWORK_CAPSULE_5`). */
private val FloatingToolbarStrokeLight = StrokeEffectParams(0.2f, 2.3f, 0f, 0.1f, 1f, 1f)

/** `COUIMaterialStrokeEffect.FRAMEWORK_CAPSULE_PARAMS_5_DARK`. */
private val FloatingToolbarStrokeDark = StrokeEffectParams(0.8f, 2.5f, 0f, 0.03f, 1f, 1f)

/** `MenuViewAnimatorImpl` attach spring: response 0.5, bounce 0.4. */
private val FloatingToolbarAttachSpring = TouchMotion.spring(response = 0.5f, bounce = 0.4f, visibilityThreshold = 0.001f)

/** `MenuViewAnimatorImpl` detach spring (`HIDE_SCALE_SPRING_*`): response 0.25, bounce 0. */
private val FloatingToolbarDetachSpring = TouchMotion.spring(response = 0.25f, bounce = 0f, visibilityThreshold = 0.001f)

/** `MenuViewAnimatorImpl.mHorizontalAnimation`: a capsule moving to its new place, response 0.5, bounce 0.4. */
private val FloatingToolbarHorizontalSpring = TouchMotion.spring(response = 0.5f, bounce = 0.4f, visibilityThreshold = 0.5f)

/** `ItemViewAnimatorImpl.mTransitionAnimation`: the items scaling in and out, response 0.5, bounce 0.4. */
private val FloatingToolbarItemSpring = TouchMotion.spring(response = 0.5f, bounce = 0.4f, visibilityThreshold = 0.0001f)

/** `ItemViewAnimatorImpl.mGapScaleAnimator`: 200ms linear, out to the pending gap and back. */
private val FloatingToolbarGapSpec = tween<Float>(durationMillis = 200, easing = LinearEasing)

/** `GAP_MAXIMUM_EXTRA_SCALE_RANGE` / `DEFAULT_ANIMATION_RANGE`: the gaps breathe by 75% before the items move. */
private const val FLOATING_TOOLBAR_MAX_GAP_SCALE = 0.75f

object FloatingToolbarDefaults {

    /**
     * Default corner radius of the [FloatingToolbar]: a capsule. COUI 17
     * `COUIBottomFloatingToolbarMenuView` derives it as half the content height
     * (`coui_floating_toolbar_menu_view_height` 44dp -> 22dp) and calls `addSmoothRoundRect`
     * without a weight: a fully rounded toolbar is the weight-1.0 Oplus soft capsule, any smaller
     * radius takes `default_g2_weight` 2.45 ([COUIRoundCorner.UnspecifiedWeight]).
     */
    val CornerRadius = 22.dp

    /**
     * Default color of the [FloatingToolbar]: without the material blur ColorOS 17 fills the
     * capsule with `coui_tab_navigation_view_bg` (`?couiColorBar`,
     * `COUIBottomFloatingToolbarMenuView.updateBlurEffect`), see [FloatingNavigationBarDefaults.color].
     */
    @Composable
    fun defaultColor(): Color = FloatingNavigationBarDefaults.color()

    /**
     * The blur radius of the drop shadow: the caustic shadow of `setCausticShadowEffect`
     * (`ShadowUtils.setElevationToView(this, 7)`, `SHADOW_CAUSTIC_DOWN`: elevation 24dp, alpha 45),
     * the same as [FloatingNavigationBarDefaults.ShadowElevation].
     */
    val ShadowElevation = FloatingNavigationBarDefaults.ShadowElevation

    /**
     * The COUI 17 material of the toolbar, `COUIMaterialBlurEffect.TYPE_FRAMEWORK_BOTTOM_BAR`, the
     * same bottom-bar material as [FloatingNavigationBarDefaults.backdropSpec].
     *
     * @param isDark Whether the dark-scheme layers are used.
     */
    fun backdropSpec(isDark: Boolean): MaterialBackdropSpec = FloatingNavigationBarDefaults.backdropSpec(isDark)

    /**
     * Default padding outside the content [FloatingToolbar]: 16dp on every side, the compact side
     * margin (coui_floating_toolbar_group_margin_small_screen) and the space that centres the 44dp
     * capsule (coui_floating_toolbar_menu_view_height) in the 76dp toolbar
     * (coui_floating_toolbar_min_height).
     */
    val OutSidePadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)

    /**
     * The least height of the item toolbar, its capsules centred in it
     * (coui_floating_toolbar_min_height).
     */
    val MinHeight = 76.dp

    /** Below this toolbar width the compact side margin applies (`COUIResponsiveUtils.isSmallScreen`). */
    val CompactWidthBreakpoint = 600.dp

    /** From this toolbar width the expanded side margin applies (`COUIResponsiveUtils.isLargeScreen`). */
    val ExpandedWidthBreakpoint = 840.dp

    /** The side margin on compact toolbars (coui_floating_toolbar_group_margin_small_screen). */
    val HorizontalOutSidePaddingCompact = 16.dp

    /** The side margin on medium toolbars (coui_floating_toolbar_group_margin_medium_screen). */
    val HorizontalOutSidePaddingMedium = 24.dp

    /** The side margin on expanded toolbars (coui_floating_toolbar_group_margin_large_screen). */
    val HorizontalOutSidePaddingExpanded = 40.dp

    /** The least gap between two groups (coui_floating_toolbar_min_group_space). */
    val MinGroupSpacing = 16.dp

    /**
     * The width the side groups keep when three groups share the toolbar
     * (coui_floating_toolbar_item_width, `mMinGroupWidth`).
     */
    val MinGroupWidth = 44.dp

    /** The capsule height (coui_floating_toolbar_menu_view_height). */
    val CapsuleHeight = 44.dp

    /**
     * The capsule's padding beside its first and last item when it holds more than one item
     * (coui_floating_toolbar_menu_view_item_bg_inset_horizontal); a single item fills the capsule.
     */
    val CapsuleInnerPadding = 6.dp

    /** The gap between items (coui_floating_toolbar_item_view_space). */
    val ItemSpacing = 4.dp

    /** The least item width, and the width of an icon item (coui_floating_toolbar_item_width). */
    val ItemMinWidth = 44.dp

    /** The item height (coui_floating_toolbar_item_height). */
    val ItemHeight = 40.dp

    /** The icon size (coui_floating_toolbar_item_icon_width / _height). */
    val IconSize = 24.dp

    /** The least width of a title item (coui_floating_toolbar_item_text_min_width). */
    val TitleMinWidth = 52.dp

    /** The most width of a title item (coui_floating_toolbar_item_text_max_width). */
    val TitleMaxWidth = 200.dp

    /** The padding beside a title (coui_floating_toolbar_item_text_padding_horizontal). */
    val TitleHorizontalPadding = 16.dp

    /** The title text size: `setTextSize(COMPLEX_UNIT_SP, 16)`, sans-serif-medium. */
    val TitleFontSize = 16.sp

    /** The red dot diameter (coui_floating_toolbar_red_dot_radius). */
    val RedDotDiameter = 6.dp

    /**
     * How far the red dot's centre lies from the item's top end corner, both ways
     * (coui_floating_toolbar_red_dot_offset).
     */
    val RedDotOffset = 12.dp

    /**
     * How many items a group shows before the rest go to the overflow menu, unless the last item
     * fits in its place (`COUIBottomFloatingToolbarMenuView.onMeasure`).
     */
    val MaxVisibleItems = 6

    /** How many groups the toolbar shows (`MAX_GROUP`); later groups are dropped. */
    val MaxGroups = 3

    /** The accessibility label of the overflow item (`floating_toolbar_item_more`). */
    val OverflowContentDescription = "More"

    /**
     * The side margin COUI gives a toolbar [width] wide (`COUIBottomFloatingToolbar.changePadding`).
     *
     * @param width The width the toolbar is laid out at.
     */
    fun horizontalOutSidePadding(width: Dp): Dp = when {
        width < CompactWidthBreakpoint -> HorizontalOutSidePaddingCompact
        width < ExpandedWidthBreakpoint -> HorizontalOutSidePaddingMedium
        else -> HorizontalOutSidePaddingExpanded
    }

    /**
     * The item content colors (`COUIBottomFloatingToolbarItemView.resolveToolbarDefaultTitleColorStateList`):
     * `?couiColorLabelPrimary` when enabled, `?couiColorLabelTertiary` when disabled.
     *
     * @param contentColor The icon and title color of an enabled item.
     * @param disabledContentColor The icon and title color of a disabled item.
     */
    @Composable
    fun itemColors(
        contentColor: Color = COUITheme.colorScheme.labelPrimary,
        disabledContentColor: Color = COUITheme.colorScheme.labelTertiary,
    ): FloatingToolbarItemColors = remember(contentColor, disabledContentColor) {
        FloatingToolbarItemColors(
            contentColor = contentColor,
            disabledContentColor = disabledContentColor,
        )
    }
}

/**
 * An item of the item [FloatingToolbar] (`COUIBottomFloatingToolbarItem`, drawn by
 * `COUIBottomFloatingToolbarItemView`). An item with an [icon] shows only the icon; an item
 * without one shows its [title].
 *
 * @param onClick The callback when the item is tapped, from the toolbar or from the overflow menu.
 * @param icon The icon of the item, tinted with the item's content color. When null, [title] is shown.
 * @param title The title of the item: the text of a title item, the row of the item in the overflow
 *   menu and, without a [contentDescription], its accessibility label.
 * @param contentDescription The accessibility label of the item (`setDescription`).
 * @param group The group of the item (`setGroupId`). Each group is a capsule of its own; groups are
 *   ordered by this value.
 * @param enabled Whether the item is enabled. A disabled item ignores taps and hides its red dot.
 * @param showRedDot Whether the item shows a red dot at its top end corner (`setShowRedDot`).
 * @param key The identity of the item (`setId`), which the toolbar follows when the list changes
 *   to animate the items that come and go. Unique within its group. Null identifies the item by its
 *   [icon], [title] and [contentDescription].
 */
@Stable
data class FloatingToolbarItem(
    val onClick: () -> Unit,
    val icon: ImageVector? = null,
    val title: String = "",
    val contentDescription: String? = null,
    val group: Int = 0,
    val enabled: Boolean = true,
    val showRedDot: Boolean = false,
    val key: Any? = null,
)

/**
 * How an item [FloatingToolbar] places its groups (`setGroupAlignStyle`).
 */
enum class FloatingToolbarGroupArrangement {
    /**
     * `SPREAD`: one group in the middle; two groups at the start and end edges; three groups at the
     * edges and in the middle, the middle one pushed aside when it would come closer than
     * [FloatingToolbarDefaults.MinGroupSpacing] to a side group.
     */
    Spread,

    /** `PACKED`: the groups side by side, [FloatingToolbarDefaults.MinGroupSpacing] apart, centred together. */
    Packed,
}

/**
 * The content colors of an item [FloatingToolbar].
 *
 * @param contentColor The icon and title color of an enabled item.
 * @param disabledContentColor The icon and title color of a disabled item.
 */
@Immutable
data class FloatingToolbarItemColors(
    val contentColor: Color,
    val disabledContentColor: Color,
) {
    @Stable
    internal fun contentColor(enabled: Boolean): Color = if (enabled) contentColor else disabledContentColor
}

/**
 * The COUI 17 floating toolbar of items (`COUIBottomFloatingToolbar`): a full-width bar, at least
 * 76dp high, holding up to three capsules, one per [FloatingToolbarItem.group].
 *
 * - **Layout**: the bar keeps 16dp from its sides below 600dp, 24dp below 840dp and 40dp from there
 *   (`changePadding`), and centres its capsules vertically. Two groups each get up to half the
 *   width, the narrower one leaving the rest to the wider; with three, the middle group keeps room
 *   for two 44dp side groups (`onMeasureMenuView*`). [groupArrangement] places them.
 * - **Capsules**: 44dp high; the items sit 4dp apart with 6dp of capsule beside the first and last
 *   (none for a single item). An icon item is 44 x 40dp around a 24dp icon; a title item is
 *   52-200dp wide with 16dp beside its 16sp medium title. A group shows at most six items; when
 *   more items, or items that do not fit, follow, the last place goes to an overflow item that
 *   opens a menu of the rest, 8dp above the capsule (`COUIBottomFloatingToolbarMenuView.onMeasure`,
 *   `coui_popup_list_window_gap_to_navigation_view`).
 * - **Material and touch**: each capsule is a content [FloatingToolbar] (blur from a
 *   [LocalMaterialBackdrop] provider, `TYPE_FRAMEWORK_CAPSULE_5` stroke, touch spotlight, caustic
 *   shadow; a lone item is drawn on a circle, `computeMaterialCornerWeight` 2.0) and takes the
 *   `COUITouchMotionAnimator` press motion as a whole; the items have no press mask of their own
 *   (`shouldStripPressedForContainerTouch`).
 * - **Item changes** (`ItemViewAnimatorImpl`): when the items a capsule shows change (items added,
 *   removed, or moved into or out of the overflow item), the gaps first breathe for 200ms (they
 *   close up by 75% when the capsule is about to grow, open up by 75% when it is about to shrink),
 *   then the new items scale and fade in about their centres while the leaving ones scale and fade
 *   out, on a response 0.5 / bounce 0.4 spring, and the capsule's width follows them. A change undone
 *   while it runs plays backwards. Items are told apart by [FloatingToolbarItem.key].
 * - **Groups**: a group that appears scales in (response 0.5, bounce 0.4) and one that goes away
 *   scales out (response 0.25, bounce 0) where it stood, while the capsules that have to move
 *   spring to their new places (`MenuViewAnimatorImpl`, response 0.5, bounce 0.4). [visible] scales
 *   every capsule in or out. A change of the toolbar's width applies at once (`setScreenSwitch`).
 *
 * @param items The items, in order. Items of the same [FloatingToolbarItem.group] form a capsule.
 * @param modifier The modifier applied to the toolbar (the full-width area holding the capsules).
 * @param visible Whether the capsules are shown.
 * @param materialEffect Whether the COUI 17 material is drawn on the capsules. Defaults to
 *   [LocalCOUIMaterialEffectEnabled].
 * @param defaultWindowInsetsPadding Whether to keep the capsules above the navigation and caption bars.
 * @param groupArrangement How the groups are placed.
 * @param color The capsule fill without the material blur.
 * @param shadowElevation The blur radius of the capsules' drop shadow; 0 disables it.
 * @param colors The item content colors.
 * @param overflowContentDescription The accessibility label of the overflow item.
 */
@Composable
fun FloatingToolbar(
    items: List<FloatingToolbarItem>,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    materialEffect: Boolean = LocalCOUIMaterialEffectEnabled.current,
    defaultWindowInsetsPadding: Boolean = true,
    groupArrangement: FloatingToolbarGroupArrangement = FloatingToolbarGroupArrangement.Spread,
    color: Color = FloatingToolbarDefaults.defaultColor(),
    shadowElevation: Dp = FloatingToolbarDefaults.ShadowElevation,
    colors: FloatingToolbarItemColors = FloatingToolbarDefaults.itemColors(),
    overflowContentDescription: String = FloatingToolbarDefaults.OverflowContentDescription,
) {
    // handleExtremeScene: sorted by group id, at most MAX_GROUP groups.
    val groups = remember(items) {
        items.groupBy { it.group }.toList().sortedBy { it.first }.take(FloatingToolbarDefaults.MaxGroups)
    }
    val scope = rememberCoroutineScope()
    val groupsState = remember { FloatingToolbarGroupsState() }
    // A group the items no longer hold stays composed while it scales out (removeMenuGroup).
    val slots = groupsState.slots(groups, animate = visible)
    SideEffect { groupsState.commit(groups, animate = visible) }
    val navigationBars = WindowInsets.navigationBars
    val captionBar = WindowInsets.captionBar
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val sidePadding = FloatingToolbarDefaults.horizontalOutSidePadding(maxWidth)
        val toolbarWidth = constraints.maxWidth
        Layout(
            content = {
                slots.forEach { slot ->
                    key(slot.id) {
                        val shown = visible && !slot.removed
                        FloatingToolbarCapsule(
                            visible = shown,
                            materialEffect = materialEffect,
                            color = color,
                            cornerRadius = FloatingToolbarDefaults.CornerRadius,
                            outSidePadding = PaddingValues(0.dp),
                            shadowElevation = shadowElevation,
                            showDivider = false,
                            // COUIBottomFloatingToolbarMenuView.dispatchTouchEvent drives its
                            // COUITouchMotionAnimator whatever its items' states.
                            modifier = Modifier.couiTouchMotion(interactionSource = null, enabled = shown),
                            // computeMaterialCornerWeight: the material draws a lone item view on a
                            // circle; the non-blur fill keeps the plain smooth round rect.
                            cornerWeight = if (materialEffect && slot.items.size == 1) {
                                COUIRoundCorner.CircularWeight
                            } else {
                                COUIRoundCorner.UnspecifiedWeight
                            },
                            enterFromHidden = slot.inserted,
                            onHidden = if (slot.removed) {
                                { groupsState.drop(slot.id) }
                            } else {
                                null
                            },
                        ) {
                            FloatingToolbarGroup(
                                items = slot.items,
                                colors = colors,
                                overflowContentDescription = overflowContentDescription,
                                toolbarWidth = toolbarWidth,
                                interactive = shown,
                            )
                        }
                    }
                }
            },
        ) { measurables, constraints ->
            val width = constraints.maxWidth
            val side = sidePadding.roundToPx()
            val available = max(0, width - side * 2)
            val groupSpace = FloatingToolbarDefaults.MinGroupSpacing.roundToPx()
            val liveIndices = slots.indices.filter { !slots[it].removed }
            val measured = measureFloatingToolbarGroups(
                measurables = liveIndices.map { measurables[it] },
                available = available,
                groupSpace = groupSpace,
                minGroupWidth = FloatingToolbarDefaults.MinGroupWidth.roundToPx(),
            )
            // A group on its way out keeps the width it was given.
            val leaving = slots.indices.filter { slots[it].removed }.map { index ->
                val allotted = groupsState.allottedWidth(slots[index].id) ?: available
                index to measurables[index].measure(Constraints(maxWidth = max(0, allotted)))
            }
            val tallest = max(measured.maxOfOrNull { it.placeable.height } ?: 0, leaving.maxOfOrNull { it.second.height } ?: 0)
            val bodyHeight = max(FloatingToolbarDefaults.MinHeight.roundToPx(), tallest)
            val bottomInset = if (defaultWindowInsetsPadding) navigationBars.getBottom(this) + captionBar.getBottom(this) else 0
            val xs = floatingToolbarGroupPositions(
                widths = measured.map { it.placeable.width },
                start = side,
                available = available,
                groupSpace = groupSpace,
                arrangement = groupArrangement,
            )
            layout(width, bodyHeight + bottomInset) {
                val liveIds = liveIndices.map { slots[it].id }
                val moved = groupsState.beginPlacement(liveIds, width, animate = visible)
                measured.forEachIndexed { index, group ->
                    val x = groupsState.placeGroup(liveIds[index], xs[index], group.allotted, moved, scope)
                    // COUIBottomFloatingToolbar.layoutMenuView: centred in the toolbar height.
                    group.placeable.placeRelative(x, (bodyHeight - group.placeable.height) / 2)
                }
                leaving.forEach { (index, placeable) ->
                    val x = groupsState.drawnX(slots[index].id) ?: (side + (available - placeable.width) / 2)
                    placeable.placeRelative(x, (bodyHeight - placeable.height) / 2)
                }
            }
        }
    }
}

/** A measured capsule and the most width it was allowed. */
private class FloatingToolbarMeasuredGroup(val placeable: Placeable, val allotted: Int)

/**
 * `COUIBottomFloatingToolbar.onMeasure`: the capsule widths for one, two or three groups. The
 * natural widths come from the intrinsics, so each capsule is measured once.
 */
private fun measureFloatingToolbarGroups(
    measurables: List<Measurable>,
    available: Int,
    groupSpace: Int,
    minGroupWidth: Int,
): List<FloatingToolbarMeasuredGroup> {
    fun natural(measurable: Measurable): Int = measurable.maxIntrinsicWidth(Constraints.Infinity)

    // onMeasureMenuViewTwoMenu: each up to half; when the narrower fits in its half, the wider may
    // take what it leaves (fillRest) or stays at half.
    fun twoWidths(first: Measurable, second: Measurable, total: Int, fillRest: Boolean): Pair<Int, Int> {
        val limit = max(0, total)
        val a = min(natural(first), limit)
        val b = min(natural(second), limit)
        val half = limit / 2
        val narrow = min(a, b)
        if (narrow > half) return half to half
        val wideLimit = if (fillRest) limit - narrow else half
        return if (a == narrow) a to min(b, wideLimit) else min(a, wideLimit) to b
    }

    fun measureGroup(measurable: Measurable, maxWidth: Int): FloatingToolbarMeasuredGroup {
        val allotted = max(0, maxWidth)
        return FloatingToolbarMeasuredGroup(measurable.measure(Constraints(maxWidth = allotted)), allotted)
    }
    return when (measurables.size) {
        0 -> emptyList()

        1 -> listOf(measureGroup(measurables[0], available))

        2 -> {
            val (a, b) = twoWidths(measurables[0], measurables[1], available - groupSpace, fillRest = true)
            listOf(measureGroup(measurables[0], a), measureGroup(measurables[1], b))
        }

        else -> {
            // onMeasureMenuViewThreeMenu: the middle group first, keeping room for two side groups.
            val middle = measureGroup(measurables[1], available - (groupSpace + minGroupWidth) * 2)
            val (a, c) = twoWidths(measurables[0], measurables[2], available - middle.placeable.width - groupSpace * 2, fillRest = false)
            listOf(measureGroup(measurables[0], a), middle, measureGroup(measurables[2], c))
        }
    }
}

/**
 * `COUIBottomFloatingToolbar.onLayoutMenuView*`: the start x of each capsule in left-to-right
 * terms (mirrored by `placeRelative` for right-to-left layouts, as COUI swaps the outer groups).
 */
internal fun floatingToolbarGroupPositions(
    widths: List<Int>,
    start: Int,
    available: Int,
    groupSpace: Int,
    arrangement: FloatingToolbarGroupArrangement,
): List<Int> {
    if (widths.isEmpty()) return emptyList()
    val end = start + available
    if (widths.size == 1) return listOf(max(start, start + (available - widths[0]) / 2))
    if (arrangement == FloatingToolbarGroupArrangement.Packed) {
        val total = widths.sum() + groupSpace * (widths.size - 1)
        var x = if (widths.size == 2) max(start, start + (available - total) / 2) else start + (available - total) / 2
        return widths.map { w ->
            val position = x
            x += w + groupSpace
            position
        }
    }
    if (widths.size == 2) return listOf(start, end - widths[1])
    // Three groups: the middle one centred when it clears both side groups by the group space,
    // otherwise pushed against the side group it would overlap.
    val right = end - widths[2]
    val minMiddle = start + widths[0] + groupSpace
    val maxMiddleEnd = right - groupSpace
    val centred = start + (available - widths[1]) / 2
    val middle = when {
        centred >= minMiddle && centred + widths[1] <= maxMiddleEnd -> centred
        centred >= minMiddle -> maxMiddleEnd - widths[1]
        else -> minMiddle
    }
    return listOf(start, middle, right)
}

/** One capsule of the item [FloatingToolbar]: a group of items, possibly on its way in or out. */
private class FloatingToolbarGroupSlot(
    val id: Int,
    val items: List<FloatingToolbarItem>,
    val removed: Boolean,
    val inserted: Boolean,
)

/**
 * The groups of an item [FloatingToolbar] across changes of its items: the groups that leave
 * (kept until they scaled out, `startMenuViewTransition`), the ones that arrive, and where each
 * capsule was last drawn, for the horizontal spring of `MenuViewAnimatorImpl.doHorizontalAnim`.
 */
private class FloatingToolbarGroupsState {
    private var initialized = false
    private var committedIds: List<Int> = emptyList()
    private var lastItems: Map<Int, List<FloatingToolbarItem>> = emptyMap()
    private val removing = mutableStateMapOf<Int, List<FloatingToolbarItem>>()
    private var lastLiveIds: List<Int>? = null
    private var lastWidth = -1
    private val drawn = HashMap<Int, Float>()
    private val allotted = HashMap<Int, Int>()
    private val offsets = HashMap<Int, Animatable<Float, AnimationVector1D>>()

    /** The capsules to compose: the live groups and, when [animate], the ones scaling out. */
    fun slots(groups: List<Pair<Int, List<FloatingToolbarItem>>>, animate: Boolean): List<FloatingToolbarGroupSlot> {
        val liveIds = groups.map { it.first }
        val result = ArrayList<FloatingToolbarGroupSlot>(groups.size + removing.size)
        groups.forEach { (id, groupItems) ->
            // createMenuView: a group inserted after the first layout starts at scale 0.
            val inserted = initialized && id !in committedIds && !removing.containsKey(id)
            result += FloatingToolbarGroupSlot(id, groupItems, removed = false, inserted = inserted)
        }
        if (animate) {
            val leavingIds = LinkedHashSet<Int>()
            committedIds.forEach { if (it !in liveIds) leavingIds += it }
            removing.keys.forEach { if (it !in liveIds) leavingIds += it }
            leavingIds.forEach { id ->
                val groupItems = removing[id] ?: lastItems[id]
                if (groupItems != null) result += FloatingToolbarGroupSlot(id, groupItems, removed = true, inserted = false)
            }
        }
        result.sortBy { it.id }
        return result
    }

    /** Records the composed groups (a side effect of a successful composition). */
    fun commit(groups: List<Pair<Int, List<FloatingToolbarItem>>>, animate: Boolean) {
        val liveIds = groups.map { it.first }
        if (animate) {
            committedIds.forEach { id ->
                if (id !in liveIds && !removing.containsKey(id)) lastItems[id]?.let { removing[id] = it }
            }
        } else if (removing.isNotEmpty()) {
            removing.clear()
        }
        liveIds.forEach { if (removing.containsKey(it)) removing.remove(it) }
        committedIds = liveIds
        lastItems = groups.toMap()
        initialized = true
    }

    /** Forgets a group that finished scaling out. */
    fun drop(id: Int) {
        if (removing.containsKey(id)) removing.remove(id)
    }

    fun allottedWidth(id: Int): Int? = allotted[id]

    fun drawnX(id: Int): Int? = drawn[id]?.roundToInt()

    /**
     * Starts a placement pass of the live groups [liveIds] in a toolbar [width] wide; returns
     * whether their capsules spring to their new places (a group came or went, with the width
     * unchanged: `updateRealBounds` with `mDoAnimation`).
     */
    fun beginPlacement(liveIds: List<Int>, width: Int, animate: Boolean): Boolean {
        val previous = lastLiveIds
        val moved = previous != null && previous != liveIds && width == lastWidth && animate
        lastLiveIds = liveIds
        lastWidth = width
        if (offsets.size > liveIds.size + removing.size) offsets.keys.retainAll(liveIds.toSet())
        return moved
    }

    /** The x to place group [id] at: its laid-out [target] plus the spring that carries it there. */
    fun placeGroup(id: Int, target: Int, allottedWidth: Int, moved: Boolean, scope: CoroutineScope): Int {
        allotted[id] = allottedWidth
        if (moved) {
            val previous = drawn[id]
            if (previous != null && abs(previous - target) >= 0.5f) {
                val offset = Animatable(previous - target, visibilityThreshold = 0.5f)
                offsets[id] = offset
                scope.launch { offset.animateTo(0f, FloatingToolbarHorizontalSpring) }
            }
        }
        val x = target + (offsets[id]?.value ?: 0f)
        drawn[id] = x
        return x.roundToInt()
    }
}

/** The overflow item's place in a capsule's key list. */
private object FloatingToolbarOverflowKey

/** What a [FloatingToolbarItem] without a key is told apart by. */
private data class FloatingToolbarItemLook(val icon: ImageVector?, val title: String, val contentDescription: String?)

/** The identity of an item in its capsule: its key (or look) and its rank among items that share it. */
private data class FloatingToolbarItemIdentity(val base: Any, val rank: Int)

/** The identities of [items], in order. */
private fun floatingToolbarItemKeys(items: List<FloatingToolbarItem>): List<Any> {
    val seen = HashMap<Any, Int>()
    return items.map { item ->
        val base: Any = item.key ?: FloatingToolbarItemLook(item.icon, item.title, item.contentDescription)
        val rank = seen[base] ?: 0
        seen[base] = rank + 1
        FloatingToolbarItemIdentity(base, rank)
    }
}

/** An item a capsule composes: a live one, or one the items dropped that is still scaling out. */
private class FloatingToolbarItemEntry(val key: Any, val item: FloatingToolbarItem, val removed: Boolean)

/**
 * `COUIBottomFloatingToolbarMenuView.onMeasure`: how many leading items a capsule shows for
 * [available] pixels between its insets; when that is fewer than all of them, the overflow item
 * follows. At most [maxVisible] items show unless the last one fits in its place.
 */
internal fun floatingToolbarShownCount(
    widths: IntArray,
    overflowWidth: Int,
    spacing: Int,
    available: Long,
    maxVisible: Int,
): Int {
    var remaining = available
    var shown = 0
    for (index in widths.indices) {
        val space = if (shown == 0) 0 else spacing
        val isLast = index == widths.lastIndex
        val width = widths[index].toLong()
        if (!isLast && width + spacing + space + overflowWidth <= remaining && shown < maxVisible) {
            remaining -= width + space
            shown++
        } else if (!isLast || width + space > remaining) {
            return index
        } else {
            remaining -= width + space
            shown++
        }
    }
    return widths.size
}

private const val FLOATING_TOOLBAR_ITEM_APPEARING = 1
private const val FLOATING_TOOLBAR_ITEM_STAYING = 0
private const val FLOATING_TOOLBAR_ITEM_DISAPPEARING = -1

private const val FLOATING_TOOLBAR_PHASE_IDLE = 0
private const val FLOATING_TOOLBAR_PHASE_PENDING = 1
private const val FLOATING_TOOLBAR_PHASE_RUNNING = 2

/**
 * `ItemViewAnimatorImpl`: the item change of one capsule. The measure pass hands it the items the
 * capsule should show ([update]); it answers with the layout of the change while it runs.
 *
 * A change first runs the pending phase (`STATE_PENDING`): the gaps between the items shown so far
 * scale by 75% over 200ms, closing up when the capsule will grow and opening up when it will shrink,
 * while the arriving items wait at scale 0. Then (`STATE_RUNNING`) the arriving items scale and fade
 * in and the leaving ones out on a response 0.5 / bounce 0.4 spring, as the gaps return over 200ms.
 */
private class FloatingToolbarItemsAnimator(private val scope: CoroutineScope) {
    /** The keys the capsule shows at rest (`mChildList`). */
    var committed: List<Any> = emptyList()
        private set

    /** The inset beside the committed keys (`mMenuViewItemBgInsetHorizontal`). */
    var committedInset = 0
        private set

    var phase = FLOATING_TOOLBAR_PHASE_IDLE
        private set

    /** `mAnimatingViews`: the new keys with the leaving ones after the last key they followed. */
    var animating: List<Any> = emptyList()
        private set

    /** `mAnimatingState`: appearing, staying or disappearing. */
    var roles: Map<Any, Int> = emptyMap()
        private set
    var appearingFirst: Any? = null
        private set
    var appearingLast: Any? = null
        private set
    var disappearingFirst: Any? = null
        private set
    var disappearingLast: Any? = null
        private set

    /** `mItemCountIdle`: the menu's child count when the change began. */
    var idleCount = 0
        private set
    var insetFrom = 0
        private set
    var insetTo = 0
        private set

    /** `mIsGapEnlarging`: whether the pending gaps open up (the capsule shrinks). */
    var gapEnlarging = false
        private set
    var gapScale = Animatable(0f)
        private set
    var progress = Animatable(0f)
        private set

    private var target: List<Any>? = null
    private var lastToolbarWidth = Int.MIN_VALUE
    private var fromKeys: List<Any> = emptyList()
    private var toKeys: List<Any> = emptyList()
    private var rollingBack = false
    private var job: Job? = null
    private var lastItems: Map<Any, FloatingToolbarItem> = emptyMap()
    private val settleCount = mutableIntStateOf(0)

    /** Bumped each time the capsule settles; read by composition to drop the items a change kept. */
    val settled: Int
        get() = settleCount.intValue

    /** [phase], read through the settle counter so a measure pass runs again once the capsule settles. */
    val phaseNow: Int
        get() = if (settleCount.intValue >= 0) phase else FLOATING_TOOLBAR_PHASE_IDLE

    /** Whether [key] is among the items the capsule is showing or about to show. */
    fun isTargetShown(key: Any): Boolean = target?.contains(key) == true

    private fun keysOnScreen(): Collection<Any> = if (phase == FLOATING_TOOLBAR_PHASE_IDLE) committed else animating

    /** The items to compose: [items] (identified by [liveKeys]) and the dropped ones still on screen. */
    fun entries(items: List<FloatingToolbarItem>, liveKeys: List<Any>): List<FloatingToolbarItemEntry> {
        val live = liveKeys.toHashSet()
        val result = ArrayList<FloatingToolbarItemEntry>(items.size + 1)
        items.forEachIndexed { index, item -> result += FloatingToolbarItemEntry(liveKeys[index], item, removed = false) }
        keysOnScreen().forEach { key ->
            if (key !in live && key != FloatingToolbarOverflowKey) {
                lastItems[key]?.let { result += FloatingToolbarItemEntry(key, it, removed = true) }
            }
        }
        return result
    }

    /** Keeps the data of the composed items, for the ones the next item list drops. */
    fun rememberItems(entries: List<FloatingToolbarItemEntry>) {
        lastItems = entries.associate { it.key to it.item }
    }

    /**
     * Hands the animator the keys the capsule should show, from a measure pass.
     *
     * @param newTarget The keys to show, the overflow key last when present.
     * @param inset The inset beside them.
     * @param growing Whether the capsule is wider with them (`onMenuViewWidthChanged`).
     * @param toolbarWidth The width of the whole toolbar; a change of it applies at once.
     * @param animate Whether changes animate (false while the capsule is hidden).
     */
    fun update(newTarget: List<Any>, inset: Int, growing: Boolean, toolbarWidth: Int, animate: Boolean) {
        val previous = target
        target = newTarget
        val widthChanged = toolbarWidth != lastToolbarWidth
        lastToolbarWidth = toolbarWidth
        if (previous == null || !animate || widthChanged) {
            // The first layout, a hidden capsule or a resized toolbar (setScreenSwitch): no animation.
            if (phase != FLOATING_TOOLBAR_PHASE_IDLE || committed != newTarget || committedInset != inset) snapTo(newTarget, inset)
            return
        }
        if (newTarget == previous) return
        if (phase != FLOATING_TOOLBAR_PHASE_IDLE) {
            when (newTarget) {
                // rollbackCheck: going back to where the change started reverses it.
                fromKeys -> if (!rollingBack) runTransition(forward = false)

                toKeys -> if (rollingBack) runTransition(forward = true)

                else -> {
                    // A third list while a change runs: land the change, then start the new one.
                    if (rollingBack) snapTo(fromKeys, insetFrom) else snapTo(toKeys, insetTo)
                    start(newTarget, inset, growing)
                }
            }
            return
        }
        start(newTarget, inset, growing)
    }

    private fun snapTo(keys: List<Any>, inset: Int) {
        job?.cancel()
        job = null
        val dropsKeys = keysOnScreen().any { it !in keys }
        phase = FLOATING_TOOLBAR_PHASE_IDLE
        rollingBack = false
        committed = keys
        committedInset = inset
        animating = emptyList()
        if (dropsKeys) scope.launch { settleCount.intValue++ }
    }

    /** `setItemList` / `setItemData`: builds the change from the committed keys to [to]. */
    private fun start(to: List<Any>, inset: Int, growing: Boolean) {
        val from = committed
        val merged = ArrayList<Any>(to.size + from.size)
        merged.addAll(to)
        val roleMap = HashMap<Any, Int>(to.size + from.size)
        to.forEach { roleMap[it] = FLOATING_TOOLBAR_ITEM_APPEARING }
        // A leaving key goes right after the last key it followed that stays.
        val leaving = ArrayList<Pair<Any, Any?>>()
        var lastStaying: Any? = null
        for (key in from) {
            if (roleMap.containsKey(key)) {
                roleMap[key] = FLOATING_TOOLBAR_ITEM_STAYING
                lastStaying = key
            } else {
                leaving += key to lastStaying
            }
        }
        for (index in leaving.indices.reversed()) {
            val (key, after) = leaving[index]
            merged.add(if (after == null) 0 else merged.indexOf(after) + 1, key)
            roleMap[key] = FLOATING_TOOLBAR_ITEM_DISAPPEARING
        }
        // The overflow item always closes the row.
        if (merged.remove(FloatingToolbarOverflowKey)) merged.add(FloatingToolbarOverflowKey)
        // The ends that change: their outer gaps open or close with the progress.
        var dFirst: Any? = from.firstOrNull()
        var dLast: Any? = from.lastOrNull()
        var aFirst: Any? = to.firstOrNull()
        var aLast: Any? = to.lastOrNull()
        if (dFirst == aFirst) {
            dFirst = null
            aFirst = null
        }
        if (dLast == aLast) {
            dLast = null
            aLast = null
        }
        appearingFirst = aFirst
        appearingLast = aLast
        disappearingFirst = dFirst
        disappearingLast = dLast
        animating = merged
        roles = roleMap
        // The menu already holds the new children when setItemData counts them.
        idleCount = to.size
        fromKeys = from
        toKeys = to
        insetFrom = committedInset
        insetTo = inset
        gapEnlarging = !growing
        phase = FLOATING_TOOLBAR_PHASE_PENDING
        gapScale = Animatable(0f)
        progress = Animatable(0f)
        runTransition(forward = true)
    }

    private fun runTransition(forward: Boolean) {
        job?.cancel()
        rollingBack = !forward
        val gap = gapScale
        val prog = progress
        job = scope.launch {
            if (phase == FLOATING_TOOLBAR_PHASE_PENDING) {
                if (!forward) {
                    // Undone before the items moved: the gaps settle back.
                    gap.animateTo(0f, FloatingToolbarGapSpec)
                    settle(fromKeys, insetFrom)
                    return@launch
                }
                gap.animateTo(FLOATING_TOOLBAR_MAX_GAP_SCALE, FloatingToolbarGapSpec)
                phase = FLOATING_TOOLBAR_PHASE_RUNNING
            }
            coroutineScope {
                // gapScaleAnimBack runs alongside the transition spring.
                launch { gap.animateTo(0f, FloatingToolbarGapSpec) }
                prog.animateTo(if (forward) 1f else 0f, FloatingToolbarItemSpring)
            }
            if (forward) settle(toKeys, insetTo) else settle(fromKeys, insetFrom)
        }
    }

    private fun settle(keys: List<Any>, inset: Int) {
        phase = FLOATING_TOOLBAR_PHASE_IDLE
        rollingBack = false
        committed = keys
        committedInset = inset
        animating = emptyList()
        job = null
        settleCount.intValue++
    }
}

/** What a group's measure pass decided about its overflow, read when the overflow menu opens. */
private class FloatingToolbarOverflowHolder {
    var hiddenItems: List<FloatingToolbarItem> = emptyList()
}

/**
 * The items of one capsule (`COUIBottomFloatingToolbarMenuView`): the visible items, then the
 * overflow item when some do not fit, animating as they change ([FloatingToolbarItemsAnimator]).
 */
@Composable
private fun FloatingToolbarGroup(
    items: List<FloatingToolbarItem>,
    colors: FloatingToolbarItemColors,
    overflowContentDescription: String,
    toolbarWidth: Int,
    interactive: Boolean,
) {
    val scope = rememberCoroutineScope()
    val animator = remember { FloatingToolbarItemsAnimator(scope) }
    val holder = remember { FloatingToolbarOverflowHolder() }
    var showOverflow by remember { mutableStateOf(false) }
    val liveKeys = remember(items) { floatingToolbarItemKeys(items) }
    val settled = animator.settled
    // Items the list dropped stay composed while they scale out of the capsule.
    val entries = remember(items, liveKeys, settled) { animator.entries(items, liveKeys) }
    SideEffect { animator.rememberItems(entries) }
    val measurePolicy = remember(entries, liveKeys, toolbarWidth, interactive) {
        FloatingToolbarGroupMeasurePolicy(
            entries = entries,
            liveKeys = liveKeys,
            items = items,
            animator = animator,
            holder = holder,
            toolbarWidth = toolbarWidth,
            animate = interactive,
        )
    }
    Layout(
        content = {
            entries.forEach { entry ->
                key(entry.key) {
                    val item = entry.item
                    FloatingToolbarItemView(
                        // A leaving item, or one moving into the overflow menu, no longer clicks.
                        onClick = { if (animator.isTargetShown(entry.key)) item.onClick() },
                        icon = item.icon,
                        title = item.title,
                        contentDescription = item.contentDescription,
                        enabled = item.enabled,
                        showRedDot = item.showRedDot,
                        colors = colors,
                        interactive = interactive && !entry.removed,
                    )
                }
            }
            // The anchor spans the capsule height, so the menu keeps
            // coui_popup_list_window_gap_to_navigation_view (8dp) from the capsule itself.
            Box(
                modifier = Modifier.height(FloatingToolbarDefaults.CapsuleHeight),
                contentAlignment = Alignment.Center,
            ) {
                // createOverflowItem: an icon item showing coui_floating_toolbar_more.
                FloatingToolbarItemView(
                    onClick = { if (animator.isTargetShown(FloatingToolbarOverflowKey)) showOverflow = true },
                    icon = COUIIcons.Basic.More,
                    title = "",
                    contentDescription = overflowContentDescription,
                    enabled = true,
                    showRedDot = false,
                    colors = colors,
                    interactive = interactive,
                )
                WindowListPopup(
                    show = showOverflow,
                    alignment = PopupPositionProvider.Align.End,
                    onDismissRequest = { showOverflow = false },
                ) {
                    val dismiss = LocalDismissState.current
                    val hidden = holder.hiddenItems
                    ListPopupColumn {
                        hidden.forEachIndexed { index, item ->
                            DropdownImpl(
                                text = item.title.ifEmpty { item.contentDescription.orEmpty() },
                                optionSize = hidden.size,
                                isSelected = false,
                                index = index,
                                enabled = item.enabled,
                                onSelectedIndexChange = {
                                    item.onClick()
                                    dismiss?.invoke()
                                },
                            )
                        }
                    }
                }
            }
        },
        measurePolicy = measurePolicy,
    )
}

/**
 * The measure policy of a capsule's items. The intrinsic widths give the capsule's width at rest
 * for all of its items; the measure pass picks the items that fit, hands them to the [animator]
 * and lays out what it draws: the items at rest, or the change in progress
 * (`ItemViewAnimatorImpl.updateAllAnimatingViews`) with the capsule spanning the items it draws
 * (`updateAndDrawBackgroundRect`).
 */
private class FloatingToolbarGroupMeasurePolicy(
    private val entries: List<FloatingToolbarItemEntry>,
    private val liveKeys: List<Any>,
    private val items: List<FloatingToolbarItem>,
    private val animator: FloatingToolbarItemsAnimator,
    private val holder: FloatingToolbarOverflowHolder,
    private val toolbarWidth: Int,
    private val animate: Boolean,
) : MeasurePolicy {

    private fun keyAt(index: Int): Any = if (index < entries.size) entries[index].key else FloatingToolbarOverflowKey

    override fun MeasureScope.measure(measurables: List<Measurable>, constraints: Constraints): MeasureResult {
        val spacing = FloatingToolbarDefaults.ItemSpacing.roundToPx()
        // computeMenuViewItemBgInsetHorizontal: no capsule beside a single item.
        val inset = if (liveKeys.size > 1) FloatingToolbarDefaults.CapsuleInnerPadding.roundToPx() else 0
        val placeables = HashMap<Any, Placeable>(measurables.size)
        measurables.forEachIndexed { index, measurable -> placeables[keyAt(index)] = measurable.measure(Constraints()) }
        val overflowWidth = placeables[FloatingToolbarOverflowKey]?.width ?: 0
        val available = if (constraints.hasBoundedWidth) (constraints.maxWidth - inset * 2).toLong() else Long.MAX_VALUE / 4
        val shown = floatingToolbarShownCount(
            widths = IntArray(liveKeys.size) { placeables[liveKeys[it]]?.width ?: 0 },
            overflowWidth = overflowWidth,
            spacing = spacing,
            available = available,
            maxVisible = FloatingToolbarDefaults.MaxVisibleItems,
        )
        val overflowShown = shown < liveKeys.size
        val target = if (overflowShown) liveKeys.subList(0, shown) + FloatingToolbarOverflowKey else liveKeys
        val hidden = if (overflowShown) items.subList(shown, items.size).toList() else emptyList()

        fun widthOf(keys: List<Any>, keysInset: Int): Int {
            if (keys.isEmpty()) return 0
            return keysInset * 2 + keys.sumOf { placeables[it]?.width ?: 0 } + spacing * (keys.size - 1)
        }
        animator.update(
            newTarget = target,
            inset = inset,
            growing = widthOf(target, inset) >= widthOf(animator.committed, animator.committedInset),
            toolbarWidth = toolbarWidth,
            animate = animate,
        )
        val height = max(FloatingToolbarDefaults.CapsuleHeight.roundToPx(), placeables.values.maxOfOrNull { it.height } ?: 0)
        if (animator.phaseNow == FLOATING_TOOLBAR_PHASE_IDLE) {
            val keys = animator.committed.filter { placeables.containsKey(it) }
            val keysInset = animator.committedInset
            return layout(widthOf(keys, keysInset).coerceAtMost(constraints.maxWidth), height) {
                // Written on placement only, so the intrinsic passes of the toolbar leave it alone.
                holder.hiddenItems = hidden
                var x = keysInset
                keys.forEach { key ->
                    val placeable = placeables.getValue(key)
                    placeable.placeRelative(x, (height - placeable.height) / 2)
                    x += placeable.width + spacing
                }
            }
        }
        return layoutChange(placeables, spacing, height, constraints, hidden)
    }

    /**
     * `updateAllAnimatingViews`: each key in order takes its scaled width and half a gap on either
     * side, scaled with it; the outer gap of an end that changes opens or closes with the
     * progress; a leaving key past its end (the spring's overshoot) pulls its neighbours in five
     * times as far. The capsule then spans the first and last key (`updateAndDrawBackgroundRect`).
     * Computed left to right and mirrored by `placeRelative`, as COUI walks right-to-left layouts
     * from the end.
     */
    private fun MeasureScope.layoutChange(
        placeables: Map<Any, Placeable>,
        spacing: Int,
        height: Int,
        constraints: Constraints,
        hidden: List<FloatingToolbarItem>,
    ): MeasureResult {
        val keys = animator.animating
        val count = keys.size
        if (count == 0) {
            return layout(0, height) { holder.hiddenItems = hidden }
        }
        val running = animator.phaseNow == FLOATING_TOOLBAR_PHASE_RUNNING
        val progress = if (running) animator.progress.value else 0f
        val clamped = progress.coerceIn(0f, 1f)
        val gapScale = (if (animator.gapEnlarging) 1f else -1f) * animator.gapScale.value
        val gap = spacing * (1f + gapScale)
        // setProgress: the inset follows the progress while the change runs.
        val inset = if (running) {
            (animator.insetFrom + (animator.insetTo - animator.insetFrom) * clamped).roundToInt()
        } else {
            animator.insetFrom
        }
        val xs = FloatArray(count)
        val scales = FloatArray(count)
        var x = inset - spacing * gapScale * (animator.idleCount - 1) / 2f
        for (index in 0 until count) {
            val key = keys[index]
            val width = placeables[key]?.width ?: 0
            val role = animator.roles[key] ?: FLOATING_TOOLBAR_ITEM_STAYING
            // handleItemState: arriving keys wait at scale 0 until the change runs.
            val state: Float
            val scale: Float
            if (!running) {
                state = 1f
                scale = if (role == FLOATING_TOOLBAR_ITEM_APPEARING) 0f else 1f
            } else if (role == FLOATING_TOOLBAR_ITEM_STAYING) {
                state = 1f
                scale = 1f
            } else {
                state = if (role == FLOATING_TOOLBAR_ITEM_APPEARING) progress else 1f - progress
                scale = max(0f, state)
            }
            var leftGap = scale * gap / 2f
            var rightGap = leftGap
            if (state < 0f) {
                leftGap = 5f * state * gap
                rightGap = leftGap
            }
            when (key) {
                animator.appearingFirst -> leftGap *= 1f - clamped
                animator.disappearingFirst -> leftGap *= clamped
                animator.appearingLast -> rightGap *= 1f - clamped
                animator.disappearingLast -> rightGap *= clamped
            }
            x += leftGap
            if (index == 0) x -= leftGap
            var viewX = x - width * (1f - scale) / 2f
            x += width * scale + rightGap
            if (index == count - 1 && state < 0f) viewX += rightGap
            xs[index] = viewX
            scales[index] = scale
        }
        val firstWidth = placeables[keys[0]]?.width ?: 0
        val lastWidth = placeables[keys[count - 1]]?.width ?: 0
        val lastScale = scales[count - 1]
        val left = xs[0] + firstWidth * max(0f, 1f - scales[0]) / 2f - inset
        val right = if (lastScale <= 0f) {
            xs[count - 1] + lastWidth * (1f - lastScale) / 2f + inset
        } else {
            xs[count - 1] + lastWidth - lastWidth * (1f - lastScale) / 2f + inset
        }
        val width = ceil(right - left).toInt().coerceIn(0, constraints.maxWidth)
        return layout(width, height) {
            holder.hiddenItems = hidden
            for (index in 0 until count) {
                val placeable = placeables[keys[index]] ?: continue
                val scale = scales[index]
                placeable.placeRelativeWithLayer((xs[index] - left).roundToInt(), (height - placeable.height) / 2) {
                    scaleX = scale
                    scaleY = scale
                    alpha = scale.coerceIn(0f, 1f)
                }
            }
        }
    }

    /** The capsule's width at rest with all of its items: the toolbar shares its width by it. */
    private fun IntrinsicMeasureScope.naturalWidth(measurables: List<IntrinsicMeasurable>, height: Int): Int {
        val spacing = FloatingToolbarDefaults.ItemSpacing.roundToPx()
        val inset = if (liveKeys.size > 1) FloatingToolbarDefaults.CapsuleInnerPadding.roundToPx() else 0
        // The live items come first in the content, the overflow item last.
        val widths = IntArray(liveKeys.size) { measurables.getOrNull(it)?.maxIntrinsicWidth(height) ?: 0 }
        val overflowWidth = measurables.lastOrNull()?.maxIntrinsicWidth(height) ?: 0
        val shown = floatingToolbarShownCount(widths, overflowWidth, spacing, Long.MAX_VALUE / 4, FloatingToolbarDefaults.MaxVisibleItems)
        var content = 0
        for (index in 0 until shown) content += widths[index]
        var count = shown
        if (shown < liveKeys.size) {
            content += overflowWidth
            count++
        }
        if (count == 0) return 0
        return content + spacing * (count - 1) + inset * 2
    }

    private fun IntrinsicMeasureScope.naturalHeight(measurables: List<IntrinsicMeasurable>, width: Int): Int = max(
        FloatingToolbarDefaults.CapsuleHeight.roundToPx(),
        measurables.maxOfOrNull { it.maxIntrinsicHeight(width) } ?: 0,
    )

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(measurables: List<IntrinsicMeasurable>, height: Int): Int = naturalWidth(measurables, height)

    override fun IntrinsicMeasureScope.minIntrinsicWidth(measurables: List<IntrinsicMeasurable>, height: Int): Int = naturalWidth(measurables, height)

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(measurables: List<IntrinsicMeasurable>, width: Int): Int = naturalHeight(measurables, width)

    override fun IntrinsicMeasureScope.minIntrinsicHeight(measurables: List<IntrinsicMeasurable>, width: Int): Int = naturalHeight(measurables, width)
}

/**
 * One item view of an item [FloatingToolbar] (`COUIBottomFloatingToolbarItemView`, default style):
 * the icon, or the title when there is none, and the red dot.
 */
@Composable
private fun FloatingToolbarItemView(
    onClick: () -> Unit,
    icon: ImageVector?,
    title: String,
    contentDescription: String?,
    enabled: Boolean,
    showRedDot: Boolean,
    colors: FloatingToolbarItemColors,
    interactive: Boolean,
) {
    val contentColor = colors.contentColor(enabled)
    val label = contentDescription ?: title
    Layout(
        modifier = if (interactive) {
            Modifier
                .clickable(
                    interactionSource = null,
                    indication = null,
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick,
                )
                .semantics(mergeDescendants = true) {
                    if (label.isNotEmpty()) this.contentDescription = label
                }
        } else {
            Modifier.clearAndSetSemantics { }
        },
        content = {
            if (icon != null) {
                Image(
                    imageVector = icon,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(contentColor),
                )
            } else {
                Text(
                    text = title,
                    modifier = Modifier.padding(horizontal = FloatingToolbarDefaults.TitleHorizontalPadding),
                    color = contentColor,
                    fontSize = FloatingToolbarDefaults.TitleFontSize,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            // setEnabled(false) hides the red dot.
            if (showRedDot && enabled) Badge(dotDiameter = FloatingToolbarDefaults.RedDotDiameter)
        },
    ) { measurables, _ ->
        val itemHeight = FloatingToolbarDefaults.ItemHeight.roundToPx()
        val minWidth = FloatingToolbarDefaults.ItemMinWidth.roundToPx()
        val content = if (icon != null) {
            val size = FloatingToolbarDefaults.IconSize.roundToPx()
            measurables[0].measure(Constraints.fixed(size, size))
        } else {
            measurables[0].measure(
                Constraints(
                    minWidth = FloatingToolbarDefaults.TitleMinWidth.roundToPx(),
                    maxWidth = FloatingToolbarDefaults.TitleMaxWidth.roundToPx(),
                    maxHeight = itemHeight,
                ),
            )
        }
        val dot = measurables.getOrNull(1)?.measure(Constraints())
        val width = max(minWidth, content.width)
        layout(width, itemHeight) {
            content.placeRelative((width - content.width) / 2, (itemHeight - content.height) / 2)
            if (dot != null) {
                // layoutRedDot: centred coui_floating_toolbar_red_dot_offset in from the top end corner.
                val offset = FloatingToolbarDefaults.RedDotOffset.roundToPx()
                dot.placeRelative(width - offset - dot.width / 2, offset - dot.height / 2)
            }
        }
    }
}
