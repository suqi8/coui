// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
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
import kotlin.math.max
import kotlin.math.min

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
    val shape = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }
    val dividerColor = COUITheme.colorScheme.divider

    // MenuViewAnimatorImpl.updateAttachState: the attach / detach scale spring, restarted from the
    // current scale, about the view centre. There is no fade.
    val scale = remember { Animatable(if (visible) 1f else 0f) }
    LaunchedEffect(visible) {
        scale.animateTo(
            targetValue = if (visible) 1f else 0f,
            animationSpec = if (visible) {
                TouchMotion.spring(response = 0.5f, bounce = 0.4f, visibilityThreshold = 0.001f)
            } else {
                TouchMotion.spring(response = 0.25f, bounce = 0f, visibilityThreshold = 0.001f)
            },
        )
    }

    val dividerMod = if (showDivider) {
        Modifier
            .squircleBackground(color = dividerColor, cornerRadius = cornerRadius, weight = COUIRoundCorner.UnspecifiedWeight)
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
        val backdropShape = remember(cornerRadius) { SquircleShape(cornerRadius, COUIRoundCorner.UnspecifiedWeight) }
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
            .floatingToolbarMaterial(shaders, spotLight, cornerRadius, isDark, squircleEnabled)
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
                weight = COUIRoundCorner.UnspecifiedWeight,
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
    isDark: Boolean,
    squircleEnabled: Boolean,
): Modifier = drawWithCache {
    val radius = min(cornerRadius.toPx(), min(size.width, size.height) / 2f)
    val stroke = if (isDark) FloatingToolbarStrokeDark else FloatingToolbarStrokeLight
    configureFloatingNavigationBarEdge(shaders.shadowPass, size.width, size.height, radius, stroke)
    configureFloatingNavigationBarEdge(shaders.edgePass, size.width, size.height, radius, stroke)
    // The stroke has to trace the fill's outline. COUI always rounds the toolbar fully (radius half
    // the height, computeMaterialCornerWeight 1.0), but [cornerRadius] may be smaller: then the fill
    // is a G2 rounded rect, and a weight-1.0 stroke would still draw the full capsule (the shader's
    // capsule ignores the corner radius), a round rim light inside square-ish corners.
    val common = ShadowEdgeShader.commonArray(size.width, size.height, radius, floatingToolbarCornerWeight(radius, size.width, size.height, squircleEnabled))
    shaders.shadowPass.setFloatUniform("u_commonArray", common)
    shaders.edgePass.setFloatUniform("u_commonArray", common)
    splitFrameworkStroke(shadowPass = shaders.shadowPass, edgePass = shaders.edgePass)
    val shadowBrush = shaders.shadowPass.asBrush()
    val edgeBrush = shaders.edgePass.asBrush()
    val outline = Path().apply {
        addSquircleRect(size.width, size.height, radius, COUIRoundCorner.UnspecifiedWeight, squircleEnabled)
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
        disabledContentColor: Color = COUITheme.colorScheme.disabledNeutral,
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
 *   opens a menu of the rest (`COUIBottomFloatingToolbarMenuView.onMeasure`).
 * - **Material and motion**: each capsule is a content [FloatingToolbar] (blur from a
 *   [LocalMaterialBackdrop] provider, `TYPE_FRAMEWORK_CAPSULE_5` stroke, touch spotlight, caustic
 *   shadow, the attach / detach scale springs on [visible]) and takes the `COUITouchMotionAnimator`
 *   press motion as a whole; the items have no press mask of their own
 *   (`shouldStripPressedForContainerTouch`).
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
    val navigationBars = WindowInsets.navigationBars
    val captionBar = WindowInsets.captionBar
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val sidePadding = FloatingToolbarDefaults.horizontalOutSidePadding(maxWidth)
        Layout(
            content = {
                groups.forEach { (groupId, groupItems) ->
                    key(groupId) {
                        FloatingToolbar(
                            modifier = Modifier.couiTouchMotion(
                                interactionSource = null,
                                enabled = visible && groupItems.any { it.enabled },
                            ),
                            visible = visible,
                            materialEffect = materialEffect,
                            color = color,
                            cornerRadius = FloatingToolbarDefaults.CornerRadius,
                            outSidePadding = PaddingValues(0.dp),
                            shadowElevation = shadowElevation,
                        ) {
                            FloatingToolbarGroup(
                                items = groupItems,
                                colors = colors,
                                overflowContentDescription = overflowContentDescription,
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
            val placeables = measureFloatingToolbarGroups(
                measurables = measurables,
                available = available,
                groupSpace = groupSpace,
                minGroupWidth = FloatingToolbarDefaults.MinGroupWidth.roundToPx(),
            )
            val bodyHeight = max(FloatingToolbarDefaults.MinHeight.roundToPx(), placeables.maxOfOrNull { it.height } ?: 0)
            val bottomInset = if (defaultWindowInsetsPadding) navigationBars.getBottom(this) + captionBar.getBottom(this) else 0
            val xs = floatingToolbarGroupPositions(
                widths = placeables.map { it.width },
                start = side,
                available = available,
                groupSpace = groupSpace,
                arrangement = groupArrangement,
            )
            layout(width, bodyHeight + bottomInset) {
                placeables.forEachIndexed { index, placeable ->
                    // COUIBottomFloatingToolbar.layoutMenuView: centred in the toolbar height.
                    placeable.placeRelative(xs[index], (bodyHeight - placeable.height) / 2)
                }
            }
        }
    }
}

/**
 * `COUIBottomFloatingToolbar.onMeasure`: the capsule widths for one, two or three groups. The
 * natural widths come from the intrinsics, so each capsule is measured once.
 */
private fun measureFloatingToolbarGroups(
    measurables: List<Measurable>,
    available: Int,
    groupSpace: Int,
    minGroupWidth: Int,
): List<Placeable> {
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

    fun loose(maxWidth: Int) = Constraints(maxWidth = max(0, maxWidth))
    return when (measurables.size) {
        0 -> emptyList()

        1 -> listOf(measurables[0].measure(loose(available)))

        2 -> {
            val (a, b) = twoWidths(measurables[0], measurables[1], available - groupSpace, fillRest = true)
            listOf(measurables[0].measure(loose(a)), measurables[1].measure(loose(b)))
        }

        else -> {
            // onMeasureMenuViewThreeMenu: the middle group first, keeping room for two side groups.
            val middle = measurables[1].measure(loose(available - (groupSpace + minGroupWidth) * 2))
            val (a, c) = twoWidths(measurables[0], measurables[2], available - middle.width - groupSpace * 2, fillRest = false)
            listOf(measurables[0].measure(loose(a)), middle, measurables[2].measure(loose(c)))
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

/** What a group's measure pass decided about its overflow, read when the overflow menu opens. */
private class FloatingToolbarOverflowHolder {
    var hiddenItems: List<FloatingToolbarItem> = emptyList()
}

/**
 * The items of one capsule (`COUIBottomFloatingToolbarMenuView`): the visible items, then the
 * overflow item when some do not fit.
 */
@Composable
private fun FloatingToolbarGroup(
    items: List<FloatingToolbarItem>,
    colors: FloatingToolbarItemColors,
    overflowContentDescription: String,
) {
    val holder = remember { FloatingToolbarOverflowHolder() }
    var showOverflow by remember { mutableStateOf(false) }
    Layout(
        content = {
            items.forEach { item ->
                FloatingToolbarItemView(
                    onClick = item.onClick,
                    icon = item.icon,
                    title = item.title,
                    contentDescription = item.contentDescription,
                    enabled = item.enabled,
                    showRedDot = item.showRedDot,
                    colors = colors,
                )
            }
            Box {
                // createOverflowItem: an icon item showing coui_floating_toolbar_more.
                FloatingToolbarItemView(
                    onClick = { showOverflow = true },
                    icon = COUIIcons.Basic.More,
                    title = "",
                    contentDescription = overflowContentDescription,
                    enabled = true,
                    showRedDot = false,
                    colors = colors,
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
    ) { measurables, constraints ->
        val count = items.size
        val spacing = FloatingToolbarDefaults.ItemSpacing.roundToPx()
        // computeMenuViewItemBgInsetHorizontal: no capsule beside a single item.
        val inset = if (count > 1) FloatingToolbarDefaults.CapsuleInnerPadding.roundToPx() else 0
        val itemPlaceables = measurables.take(count).map { it.measure(Constraints()) }
        val overflowPlaceable = measurables[count].measure(Constraints())
        val overflowWidth = overflowPlaceable.width
        var remaining = if (constraints.hasBoundedWidth) (constraints.maxWidth - inset * 2).toLong() else Long.MAX_VALUE / 4
        val shown = ArrayList<Placeable>(count + 1)
        var hiddenFrom = count
        for (index in 0 until count) {
            val placeable = itemPlaceables[index]
            val space = if (shown.isEmpty()) 0 else spacing
            val isLast = index == count - 1
            val width = placeable.width.toLong()
            if (!isLast && width + spacing + space + overflowWidth <= remaining && shown.size < FloatingToolbarDefaults.MaxVisibleItems) {
                remaining -= width + space
                shown += placeable
            } else if (!isLast || width + space > remaining) {
                remaining -= overflowWidth + space
                shown += overflowPlaceable
                hiddenFrom = index
                break
            } else {
                remaining -= width + space
                shown += placeable
            }
        }
        val contentWidth = shown.sumOf { it.width } + spacing * max(0, shown.size - 1)
        val width = contentWidth + inset * 2
        val height = max(FloatingToolbarDefaults.CapsuleHeight.roundToPx(), shown.maxOfOrNull { it.height } ?: 0)
        layout(width, height) {
            // Written on placement only, so the intrinsic passes of the toolbar leave it alone.
            holder.hiddenItems = if (hiddenFrom < count) items.subList(hiddenFrom, count).toList() else emptyList()
            var x = inset
            shown.forEach { placeable ->
                placeable.placeRelative(x, (height - placeable.height) / 2)
                x += placeable.width + spacing
            }
        }
    }
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
) {
    val contentColor = colors.contentColor(enabled)
    val label = contentDescription ?: title
    Layout(
        modifier = Modifier
            .clickable(
                interactionSource = null,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .semantics(mergeDescendants = true) {
                if (label.isNotEmpty()) this.contentDescription = label
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
