// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChangedIgnoreConsumed
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import io.github.suqi8.coui.kmp.effect.FrameworkEdgeBlendMode
import io.github.suqi8.coui.kmp.effect.LocalCOUIMaterialEffectEnabled
import io.github.suqi8.coui.kmp.effect.LocalMaterialBackdrop
import io.github.suqi8.coui.kmp.effect.MaterialBackdropSpec
import io.github.suqi8.coui.kmp.effect.ShadowEdgeShader
import io.github.suqi8.coui.kmp.effect.SpotLightShader
import io.github.suqi8.coui.kmp.effect.SpotLightState
import io.github.suqi8.coui.kmp.effect.SpotLightType
import io.github.suqi8.coui.kmp.effect.TouchMotion
import io.github.suqi8.coui.kmp.effect.couiTouchMotion
import io.github.suqi8.coui.kmp.effect.splitFrameworkStroke
import io.github.suqi8.coui.kmp.shader.RuntimeShader
import io.github.suqi8.coui.kmp.shader.asBrush
import io.github.suqi8.coui.kmp.shader.isRuntimeShaderSupported
import io.github.suqi8.coui.kmp.squircle.SquircleShape
import io.github.suqi8.coui.kmp.squircle.isSquircleEnabled
import io.github.suqi8.coui.kmp.squircle.squircleSurface
import io.github.suqi8.coui.kmp.theme.COUIRoundCorner
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

/**
 * The COUI 17 floating edit toolbar (`COUIBottomFloatingEditToolbar`): the bar of edit actions a
 * list shows at the bottom while items are selected, a capsule of [FloatingEditToolbarItem]s
 * centred in an 88dp container above the bottom inset.
 *
 * - **Layout**: the toolbar follows the width it is laid out at (`COUIResponsiveUtils`). Below 600dp
 *   it keeps 16dp from its sides and draws a 56dp capsule whose items stack a 24dp icon over a 10dp
 *   label (70dp each, 148dp for a single item; `STYLE_EDIT_TOOLBAR_VERTICAL`). From 600dp it keeps
 *   24dp (40dp from 840dp) and draws a 44dp capsule whose items put the icon beside a 14dp label of
 *   up to two lines (98dp each, 152dp for a single item; `STYLE_EDIT_TOOLBAR_HORIZONTAL`). Items that
 *   do not fit shrink equally (`BaseBottomFloatingMenuView.onMeasure`); there is no overflow menu.
 * - **Touch**: a tap clicks the item it started and ended on, unless the finger slid off that item
 *   by more than the touch slop on the way (`View.onTouchEvent`). The capsule takes the
 *   `COUITouchMotionAnimator` press scale, rubber band and deformation and the touch spotlight;
 *   items have no press mask of their own (`shouldStripPressedForContainerTouch`). With every item
 *   disabled the capsule gives no touch feedback (`isMenuTouchFeedbackEnabled`).
 * - **Material**: the same framework bottom-bar material as [FloatingNavigationBar]
 *   (`BaseBottomFloatingMenuView`): the backdrop blur from a [LocalMaterialBackdrop] provider (which
 *   then replaces [color]), the `TYPE_FRAMEWORK_CAPSULE_3` edge lighting and inner shadow and the
 *   `TYPE_OPAQUE_MEDIUM_2` spotlight. The backdrop is drawn inside the capsule's offscreen layer,
 *   below the edge light, so the light is overlaid on the blur and not on whatever lies under the
 *   bar. Without a provider the capsule keeps COUI's opaque `?couiColorBar`.
 * - **Show / hide**: [visible] slides the capsule out below the toolbar's bottom edge and back on a
 *   spring (response 0.38, bounce 0.25), with no fade (`showMenuView` / `hideMenuView`). A hidden
 *   toolbar keeps its space but takes no touches and has no semantics.
 *
 * @param modifier The modifier applied to the toolbar's container (the full-width area holding the
 *   capsule).
 * @param backgroundModifier Applied inside the capsule clip, above [color] and below the items: the
 *   place for a custom background.
 * @param visible Whether the capsule is shown.
 * @param materialEffect Whether the COUI 17 material is drawn on the capsule: the edge lighting and
 *   inner shadow, the touch spotlight and, with a [LocalMaterialBackdrop] provider, the backdrop blur.
 *   Defaults to [LocalCOUIMaterialEffectEnabled].
 * @param defaultWindowInsetsPadding Whether to keep the capsule above the navigation and caption bars.
 * @param color The capsule fill. Defaults to COUI's opaque `?couiColorBar`; replaced by the backdrop
 *   blur when a [LocalMaterialBackdrop] provider is present and [materialEffect] is on.
 * @param shadowElevation The blur radius of the capsule's drop shadow; 0 disables it.
 * @param content The [FloatingEditToolbarItem]s, placed directly inside.
 */
@Composable
fun FloatingEditToolbar(
    modifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    visible: Boolean = true,
    materialEffect: Boolean = LocalCOUIMaterialEffectEnabled.current,
    defaultWindowInsetsPadding: Boolean = true,
    color: Color = FloatingEditToolbarDefaults.color(),
    shadowElevation: Dp = FloatingEditToolbarDefaults.ShadowElevation,
    content: @Composable () -> Unit,
) {
    // showMenuView / hideMenuView: the translation springs between 0 and the hidden distance. The
    // spring runs on the hidden fraction, which the placement scales by the current distance.
    val hidden = remember { Animatable(if (visible) 0f else 1f, visibilityThreshold = 0.0001f) }
    LaunchedEffect(visible) {
        hidden.animateTo(
            targetValue = if (visible) 0f else 1f,
            animationSpec = TouchMotion.spring(response = 0.38f, bounce = 0.25f, visibilityThreshold = 0.0001f),
        )
    }
    val navigationBars = WindowInsets.navigationBars
    val captionBar = WindowInsets.captionBar

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        // COUIBottomFloatingEditToolbar.onMeasure: the screen class of the toolbar's own width
        // picks the side padding and the item style.
        val width = maxWidth
        val horizontalStyle = width >= FloatingEditToolbarDefaults.CompactWidthBreakpoint
        val sidePadding = when {
            !horizontalStyle -> FloatingEditToolbarDefaults.HorizontalOutSidePaddingCompact
            width < FloatingEditToolbarDefaults.ExpandedWidthBreakpoint -> FloatingEditToolbarDefaults.HorizontalOutSidePaddingMedium
            else -> FloatingEditToolbarDefaults.HorizontalOutSidePaddingExpanded
        }
        Layout(
            content = {
                FloatingEditToolbarCapsule(
                    visible = visible,
                    horizontalStyle = horizontalStyle,
                    materialEffect = materialEffect,
                    color = color,
                    shadowElevation = shadowElevation,
                    backgroundModifier = backgroundModifier,
                    content = content,
                )
            },
        ) { measurables, constraints ->
            val available = constraints.maxWidth
            val side = sidePadding.roundToPx()
            val capsule = measurables.first().measure(Constraints(maxWidth = max(0, available - side * 2)))
            val bottomInset = if (defaultWindowInsetsPadding) navigationBars.getBottom(this) + captionBar.getBottom(this) else 0
            // coui_bottom_floating_edit_toolbar_min_height_without_padding_vertical: the capsule is
            // centred in at least 88dp.
            val bodyHeight = max(capsule.height, FloatingEditToolbarDefaults.MinHeight.roundToPx())
            val height = bodyHeight + bottomInset
            val top = (bodyHeight - capsule.height) / 2
            // getMenuViewHiddenTranslationY: the capsule top moves to the container's bottom edge.
            val hiddenDistance = (height - top).toFloat()
            layout(available, height) {
                capsule.placeRelativeWithLayer(side + (available - side * 2 - capsule.width) / 2, top) {
                    translationY = hidden.value * hiddenDistance
                }
            }
        }
    }
}

@Composable
private fun FloatingEditToolbarCapsule(
    visible: Boolean,
    horizontalStyle: Boolean,
    materialEffect: Boolean,
    color: Color,
    shadowElevation: Dp,
    backgroundModifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val state = remember { FloatingEditToolbarState() }
    val scope = rememberCoroutineScope()
    val isDark = COUITheme.colorScheme.isDark
    val cornerRadius = FloatingEditToolbarDefaults.CornerRadius
    val shape = remember { RoundedCornerShape(cornerRadius) }
    // BaseBottomFloatingMenuView.updateBlurEffect: with the material the backdrop blur is the fill
    // and the background turns transparent; without a backdrop the opaque fill stays.
    val backdropProvider = if (materialEffect) LocalMaterialBackdrop.current else null
    val fill = if (backdropProvider != null) Color.Transparent else color
    val backdropModifier = if (backdropProvider != null) {
        val spec = FloatingEditToolbarDefaults.backdropSpec(isDark)
        val backdropShape = remember { SquircleShape(cornerRadius, COUIRoundCorner.UnspecifiedWeight) }
        remember(backdropProvider, spec, backdropShape) { backdropProvider.materialBackdrop(spec, backdropShape) }
    } else {
        Modifier
    }
    val shaders = remember(materialEffect) {
        if (materialEffect && isRuntimeShaderSupported()) {
            RuntimeShader(ShadowEdgeShader.Source) to RuntimeShader(SpotLightShader.Source)
        } else {
            null
        }
    }
    // The edge-light pass of the framework stroke (see FrameworkEdgeBlendMode).
    val edgePass = remember(shaders) { shaders?.let { RuntimeShader(ShadowEdgeShader.Source) } }
    val spotlight = remember { SpotLightState() }
    val stroke = if (isDark) FloatingNavigationBarStrokeDark else FloatingNavigationBarStrokeLight
    val spotType = SpotLightType.OpaqueMedium2
    val squircleEnabled = isSquircleEnabled()
    // isMenuTouchFeedbackEnabled: no items, or at least one enabled item.
    val feedback = visible && state.touchFeedbackEnabled

    Layout(
        content = {
            Box(
                modifier = Modifier
                    .squircleSurface(color = fill, cornerRadius = cornerRadius, weight = COUIRoundCorner.UnspecifiedWeight)
                    // Inside the surface's offscreen layer: the edge light below is overlaid on what
                    // lies beneath it in this layer, so the blurred backdrop has to be drawn here.
                    .then(backdropModifier)
                    .then(backgroundModifier)
                    .then(
                        if (shaders != null && edgePass != null) {
                            Modifier.drawWithCache {
                                val radius = min(cornerRadius.toPx(), min(size.width, size.height) / 2f)
                                configureFloatingNavigationBarEdge(shaders.first, size.width, size.height, radius, stroke)
                                configureFloatingNavigationBarEdge(edgePass, size.width, size.height, radius, stroke)
                                // The stroke traces the same outline as the fill (the soft capsule,
                                // or the circular capsule of the fallback fill with squircles off).
                                val common = ShadowEdgeShader.commonArray(
                                    size.width,
                                    size.height,
                                    radius,
                                    floatingToolbarCornerWeight(radius, size.width, size.height, squircleEnabled),
                                )
                                shaders.first.setFloatUniform("u_commonArray", common)
                                edgePass.setFloatUniform("u_commonArray", common)
                                splitFrameworkStroke(shadowPass = shaders.first, edgePass = edgePass)
                                val shadowBrush = shaders.first.asBrush()
                                val edgeBrush = edgePass.asBrush()
                                onDrawWithContent {
                                    // BaseBottomFloatingMenuView.onDraw: the spotlight and the stroke
                                    // above the background, below the items.
                                    val glow = spotlight.progress.value
                                    if (glow > 0f) {
                                        val light = spotlight.position.value
                                        SpotLightShader.setUniforms(
                                            shader = shaders.second,
                                            lightX = light.x,
                                            lightY = light.y,
                                            radius = SpotLightShader.radius(glow, spotlight.extinguishing),
                                            intensity = spotType.endIntensity(isDark) * glow,
                                        )
                                        drawRect(brush = shaders.second.asBrush(), blendMode = spotType.blendMode(isDark))
                                    }
                                    drawRect(shadowBrush)
                                    drawRect(edgeBrush, blendMode = FrameworkEdgeBlendMode)
                                    drawContent()
                                }
                            }
                        } else {
                            Modifier
                        },
                    ),
            )
            CompositionLocalProvider(LocalFloatingEditToolbarHorizontalStyle provides horizontalStyle) {
                content()
            }
        },
        modifier = Modifier
            .couiTouchMotion(interactionSource = null, enabled = feedback)
            .then(if (shadowElevation > 0.dp) Modifier.dropShadow(shape = shape, shadow = floatingNavigationBarShadow(shadowElevation)) else Modifier)
            .then(
                if (visible) {
                    Modifier.pointerInput(state, shaders != null) {
                        val spot = shaders != null
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            val glow = spot && state.touchFeedbackEnabled
                            if (glow) scope.launch { spotlight.glow(down.position) }
                            val downItem = state.itemAt(down.position)
                            // View.onTouchEvent: a finger that slides out of the item by more than
                            // the touch slop unpresses it for good, even if it comes back.
                            val downBounds = state.boundsAt(down.position)?.inflate(viewConfiguration.touchSlop)
                            var upPosition: Offset? = null
                            var cancelled = false
                            var slipped = false
                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                // A scrolling parent took the gesture (ACTION_CANCEL): no click.
                                if (change.isConsumed && change.positionChangedIgnoreConsumed()) {
                                    cancelled = true
                                    break
                                }
                                if (!change.pressed) {
                                    upPosition = change.position
                                    break
                                }
                                if (downBounds != null && !downBounds.contains(change.position)) slipped = true
                                if (glow && change.position != change.previousPosition) {
                                    val position = change.position
                                    scope.launch { spotlight.follow(position) }
                                }
                            }
                            if (glow) scope.launch { spotlight.extinguish() }
                            // The item views click themselves: the item the finger went down on,
                            // when it lifts on it without having slid off it.
                            if (!cancelled && !slipped && upPosition != null) {
                                state.itemAt(upPosition)?.takeIf { it === downItem && it.enabled }?.onClick?.invoke()
                            }
                        }
                    }
                } else {
                    // A hidden toolbar is out of reach: no touches, nothing for accessibility.
                    Modifier.clearAndSetSemantics { }
                },
            ),
    ) { measurables, constraints ->
        val itemMeasurables = measurables.drop(1)
        val itemCount = itemMeasurables.size
        // COUIBottomFloatingEditToolbarMenuView.setHorizontalStyle / onMeasure.
        val itemMaxWidth = when {
            itemCount == 1 && horizontalStyle -> FloatingEditToolbarDefaults.SingleItemWidthHorizontal
            itemCount == 1 -> FloatingEditToolbarDefaults.SingleItemWidthVertical
            horizontalStyle -> FloatingEditToolbarDefaults.ItemMaxWidthHorizontal
            else -> FloatingEditToolbarDefaults.ItemMaxWidthVertical
        }.roundToPx()
        val innerPadding = (
            if (horizontalStyle) FloatingEditToolbarDefaults.InnerPaddingHorizontal else FloatingEditToolbarDefaults.InnerPaddingVertical
            ).roundToPx()
        val geometry = floatingNavigationBarLayout(
            itemCount = itemCount,
            maxWidth = constraints.maxWidth,
            itemMaxWidth = itemMaxWidth,
            innerPadding = innerPadding,
            itemSpacing = FloatingEditToolbarDefaults.ItemSpacing.roundToPx(),
        )
        val height = (if (horizontalStyle) FloatingEditToolbarDefaults.HeightMediumLarge else FloatingEditToolbarDefaults.HeightCompact).roundToPx()
        val itemHeight = min(
            (if (horizontalStyle) FloatingEditToolbarDefaults.ItemHeightHorizontal else FloatingEditToolbarDefaults.ItemHeightVertical).roundToPx(),
            height,
        )
        val width = geometry.width
        val background = measurables[0].measure(Constraints.fixed(max(width, 0), height))
        val handles = itemMeasurables.map { it.layoutId as? FloatingEditToolbarItemHandle }
        if (state.items != handles) state.items = handles
        val placeables = itemMeasurables.map { it.measure(Constraints.fixed(geometry.itemWidth, itemHeight)) }
        val rtl = layoutDirection == LayoutDirection.Rtl
        layout(width, height) {
            background.place(0, 0)
            val top = (height - itemHeight) / 2
            val bounds = ArrayList<Rect>(placeables.size)
            placeables.forEachIndexed { index, placeable ->
                // BaseBottomFloatingMenuView.onLayout: from the start padding, item spacing apart.
                val x = geometry.itemStart(index)
                placeable.placeRelative(x, top)
                val left = if (rtl) width - x - geometry.itemWidth else x
                bounds += Rect(left.toFloat(), top.toFloat(), (left + geometry.itemWidth).toFloat(), (top + itemHeight).toFloat())
            }
            state.itemBounds = bounds
        }
    }
}

/**
 * An item of a [FloatingEditToolbar] (`COUIBottomFloatingEditToolbarItem`, drawn by
 * `COUIBottomFloatingToolbarItemView`): an [icon] and a [label], tinted with
 * [FloatingEditToolbarColors.contentColor], or [FloatingEditToolbarColors.disabledContentColor]
 * while disabled.
 *
 * On compact toolbars the 24dp icon sits 4dp below the item top with the 10dp medium label 2dp
 * below it, on one line (`STYLE_EDIT_TOOLBAR_VERTICAL`). On wider toolbars the icon and the 14dp
 * medium label (up to two lines) sit side by side 4dp apart, centred in the item
 * (`STYLE_EDIT_TOOLBAR_HORIZONTAL`). The label sizes are in dp: they do not follow the font scale.
 *
 * Put the items directly in the [FloatingEditToolbar] content: the toolbar lays them out and
 * delivers their taps.
 *
 * @param onClick The callback when the item is tapped.
 * @param icon The icon of the item, tinted with the item's content color (an unspecified color
 *   leaves it untinted).
 * @param label The label of the item, also its accessibility label.
 * @param modifier The modifier to be applied to the item.
 * @param enabled Whether the item is enabled. A disabled item ignores taps and hides its badge.
 * @param badge The red dot on the icon (`setRedDot`): `null` for none, 0 for a plain dot at the
 *   icon's top end corner, a positive number for a number badge 4dp above the icon.
 * @param colors The content colors of the item.
 */
@Composable
fun FloatingEditToolbarItem(
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    badge: Int? = null,
    colors: FloatingEditToolbarColors = FloatingEditToolbarDefaults.itemColors(),
) {
    val horizontalStyle = LocalFloatingEditToolbarHorizontalStyle.current
    val handle = remember { FloatingEditToolbarItemHandle() }
    val currentOnClick by rememberUpdatedState(onClick)
    SideEffect {
        handle.enabled = enabled
        handle.onClick = onClick
    }
    val contentColor = colors.contentColor(enabled)
    val density = LocalDensity.current
    val labelStyle = remember(density, horizontalStyle) {
        if (horizontalStyle) {
            // applyEditToolbarHorizontalStyle: couiTextButtonM at dp2px(14), start-aligned, two lines.
            TextStyle(
                fontSize = with(density) { FloatingEditToolbarDefaults.LabelTextSizeHorizontal.toSp() },
                fontWeight = FontWeight.Medium,
                lineHeight = (BUTTON_M_LINE_SPACING * FONT_METRICS_HEIGHT_EM).em,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Proportional,
                    trim = LineHeightStyle.Trim.None,
                ),
                textAlign = TextAlign.Start,
            )
        } else {
            // applyEditToolbarVerticalStyle: dp2px(10) sans-serif-medium, centred, one line.
            TextStyle(
                fontSize = with(density) { FloatingEditToolbarDefaults.LabelTextSizeVertical.toSp() },
                fontWeight = FontWeight.Medium,
                lineHeight = FONT_METRICS_HEIGHT_EM.em,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Proportional,
                    trim = LineHeightStyle.Trim.None,
                ),
                textAlign = TextAlign.Center,
            )
        }
    }
    val interactionSource = remember { MutableInteractionSource() }
    val showBadge = badge != null && enabled
    Layout(
        modifier = Modifier
            .layoutId(handle)
            .then(modifier)
            .focusable(enabled = enabled, interactionSource = interactionSource)
            .onKeyEvent { event ->
                val activate = event.key == Key.Enter || event.key == Key.NumPadEnter || event.key == Key.Spacebar
                if (enabled && activate && event.type == KeyEventType.KeyUp) {
                    currentOnClick()
                    true
                } else {
                    false
                }
            }
            .semantics(mergeDescendants = true) {
                role = Role.Button
                onClick {
                    if (enabled) currentOnClick()
                    enabled
                }
                if (!enabled) disabled()
            },
        content = {
            Image(
                imageVector = icon,
                contentDescription = null,
                colorFilter = if (contentColor.isSpecified) ColorFilter.tint(contentColor) else null,
            )
            Text(
                text = label,
                modifier = if (horizontalStyle) {
                    Modifier
                } else {
                    // The font padding Android's TextView adds above the ascent (includeFontPadding).
                    Modifier.padding(top = with(density) { (FloatingEditToolbarDefaults.LabelTextSizeVertical.toPx() * FONT_PADDING_TOP_EM).toDp() })
                },
                color = contentColor,
                style = labelStyle,
                maxLines = if (horizontalStyle) 2 else 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (showBadge) Badge(count = badge)
        },
    ) { measurables, constraints ->
        val width = if (constraints.hasBoundedWidth) constraints.maxWidth else FloatingEditToolbarDefaults.ItemMaxWidthVertical.roundToPx()
        val height = if (constraints.hasBoundedHeight) constraints.maxHeight else FloatingEditToolbarDefaults.ItemHeightVertical.roundToPx()
        val iconSize = FloatingEditToolbarDefaults.IconSize.roundToPx()
        val iconPlaceable = measurables[0].measure(Constraints.fixed(iconSize, iconSize))
        val gap = FloatingEditToolbarDefaults.IconLabelSpacingHorizontal.roundToPx()
        val titlePlaceable = if (horizontalStyle) {
            // onMeasure, STYLE_EDIT_TOOLBAR_HORIZONTAL: AT_MOST the width left beside the icon.
            measurables[1].measure(Constraints(maxWidth = max(0, width - iconSize - gap)))
        } else {
            measurables[1].measure(Constraints(minWidth = width, maxWidth = width))
        }
        val badgePlaceable = measurables.getOrNull(2)?.measure(Constraints())
        layout(width, height) {
            val iconLeft: Int
            val iconTop: Int
            if (horizontalStyle) {
                // layoutEditToolbarHorizontal: icon, gap and label centred as a group.
                iconLeft = (width - (iconSize + gap + titlePlaceable.width)) / 2
                iconTop = (height - iconSize) / 2
                titlePlaceable.placeRelative(iconLeft + iconSize + gap, (height - titlePlaceable.height) / 2)
            } else {
                // layoutEditToolbarVertical: the icon below the top margin, the label under it.
                iconLeft = (width - iconSize) / 2
                iconTop = FloatingEditToolbarDefaults.IconTopPadding.roundToPx()
                titlePlaceable.placeRelative(0, iconTop + iconSize + FloatingEditToolbarDefaults.LabelTopSpacing.roundToPx())
            }
            iconPlaceable.placeRelative(iconLeft, iconTop)
            if (badgePlaceable != null) {
                // layoutRedDotRelativeToIcon.
                val count = badge ?: 0
                val iconRight = iconLeft + iconSize
                if (count <= 0) {
                    badgePlaceable.placeRelative(iconRight - badgePlaceable.width, iconTop)
                } else {
                    val overflow = when {
                        count < 10 -> FloatingNavigationBarDefaults.BadgeEndOverflowOneDigit
                        count < 100 -> FloatingNavigationBarDefaults.BadgeEndOverflowTwoDigits
                        else -> FloatingNavigationBarDefaults.BadgeEndOverflowThreeDigits
                    }.roundToPx()
                    badgePlaceable.placeRelative(
                        iconRight + overflow - badgePlaceable.width,
                        iconTop - FloatingNavigationBarDefaults.BadgeTopOverflow.roundToPx(),
                    )
                }
            }
        }
    }
}

/**
 * The content colors of a [FloatingEditToolbarItem].
 *
 * @param contentColor The icon and label color of an enabled item.
 * @param disabledContentColor The icon and label color of a disabled item.
 */
@Immutable
data class FloatingEditToolbarColors(
    val contentColor: Color,
    val disabledContentColor: Color,
) {
    @Stable
    internal fun contentColor(enabled: Boolean): Color = if (enabled) contentColor else disabledContentColor
}

/** Contains default values used by [FloatingEditToolbar] and [FloatingEditToolbarItem]. */
object FloatingEditToolbarDefaults {
    /**
     * The corner radius: [COUIRoundCorner.Full]. `BaseBottomFloatingMenuView.updateSmoothRoundPath`
     * takes half the capsule height, drawn as the soft capsule.
     */
    val CornerRadius = COUIRoundCorner.Full

    /** The minimum height the capsule is centred in (coui_bottom_floating_edit_toolbar_min_height_without_padding_vertical). */
    val MinHeight = 88.dp

    /** Below this toolbar width the compact (vertical item) style applies (`WindowWidthSizeClass.Compact`). */
    val CompactWidthBreakpoint = 600.dp

    /** From this toolbar width the expanded side padding applies (`WindowWidthSizeClass.Expanded`). */
    val ExpandedWidthBreakpoint = 840.dp

    /** The side padding on compact toolbars (coui_bottom_floating_edit_toolbar_padding_horizontal_small_screen). */
    val HorizontalOutSidePaddingCompact = 16.dp

    /** The side padding on medium toolbars (coui_bottom_floating_edit_toolbar_padding_horizontal_medium_screen). */
    val HorizontalOutSidePaddingMedium = 24.dp

    /** The side padding on expanded toolbars (coui_bottom_floating_edit_toolbar_padding_horizontal_large_screen). */
    val HorizontalOutSidePaddingExpanded = 40.dp

    /** The capsule height on compact toolbars (coui_bottom_floating_navigation_bar_height). */
    val HeightCompact = 56.dp

    /** The capsule height on wider toolbars (coui_bottom_floating_edit_toolbar_menu_height_medium_large). */
    val HeightMediumLarge = 44.dp

    /** The capsule's inner padding in the compact style (coui_bottom_floating_edit_toolbar_inner_padding_vertical). */
    val InnerPaddingVertical = 12.dp

    /** The capsule's inner padding in the wide style (coui_bottom_floating_edit_toolbar_inner_padding_horizontal). */
    val InnerPaddingHorizontal = 10.dp

    /** The spacing between items (coui_bottom_floating_navigation_bar_item_space). */
    val ItemSpacing = 8.dp

    /** The item width in the compact style (coui_bottom_floating_edit_toolbar_item_max_width_vertical). */
    val ItemMaxWidthVertical = 70.dp

    /** The item width in the wide style (coui_bottom_floating_edit_toolbar_item_max_width_horizontal). */
    val ItemMaxWidthHorizontal = 98.dp

    /** The width of a single item in the compact style (coui_bottom_floating_edit_toolbar_single_item_width_vertical). */
    val SingleItemWidthVertical = 148.dp

    /** The width of a single item in the wide style (coui_bottom_floating_edit_toolbar_single_item_width_horizontal). */
    val SingleItemWidthHorizontal = 152.dp

    /** The item height in the compact style (coui_bottom_floating_navigation_bar_item_height). */
    val ItemHeightVertical = 48.dp

    /** The item height in the wide style (coui_bottom_floating_edit_toolbar_item_height_horizontal). */
    val ItemHeightHorizontal = 40.dp

    /** The icon size (coui_floating_toolbar_item_icon_width / _height). */
    val IconSize = 24.dp

    /** The icon's distance from the item top in the compact style (coui_bottom_floating_navigation_bar_item_icon_top_margin). */
    val IconTopPadding = 4.dp

    /** The gap between icon and label in the compact style (..._item_icon_title_gap_vertical). */
    val LabelTopSpacing = 2.dp

    /** The gap between icon and label in the wide style (..._item_icon_title_gap_horizontal). */
    val IconLabelSpacingHorizontal = 4.dp

    /** The label text size in the compact style: `dp2px(10)` (`applyEditToolbarVerticalStyle`). */
    val LabelTextSizeVertical = 10.dp

    /** The label text size in the wide style: `dp2px(14)` (`applyEditToolbarHorizontalStyle`). */
    val LabelTextSizeHorizontal = 14.dp

    /** The blur radius of the drop shadow, the caustic shadow of [FloatingNavigationBarDefaults.ShadowElevation]. */
    val ShadowElevation = FloatingNavigationBarDefaults.ShadowElevation

    /** The opaque capsule fill, COUI's non-blur fallback `?couiColorBar` (see [FloatingNavigationBarDefaults.color]). */
    @Composable
    fun color(): Color = FloatingNavigationBarDefaults.color()

    /**
     * The COUI 17 bottom-bar material, `TYPE_FRAMEWORK_BOTTOM_BAR`, the same material as
     * [FloatingNavigationBarDefaults.backdropSpec].
     *
     * @param isDark Whether the dark scheme is active.
     */
    fun backdropSpec(isDark: Boolean): MaterialBackdropSpec = FloatingNavigationBarDefaults.backdropSpec(isDark)

    /**
     * The item content colors (`COUIBottomFloatingToolbarItemView.resolveToolbarDefaultTitleColorStateList`):
     * `?couiColorLabelPrimary` when enabled, `?couiColorLabelTertiary` when disabled.
     *
     * @param contentColor The icon and label color of an enabled item.
     * @param disabledContentColor The icon and label color of a disabled item.
     */
    @Composable
    fun itemColors(
        contentColor: Color = COUITheme.colorScheme.labelPrimary,
        disabledContentColor: Color = COUITheme.colorScheme.labelTertiary,
    ): FloatingEditToolbarColors = remember(contentColor, disabledContentColor) {
        FloatingEditToolbarColors(
            contentColor = contentColor,
            disabledContentColor = disabledContentColor,
        )
    }
}

/** What the [FloatingEditToolbar] knows about one of its [FloatingEditToolbarItem]s. */
@Stable
private class FloatingEditToolbarItemHandle {
    var enabled by mutableStateOf(true)
    var onClick: () -> Unit = {}
}

/** The item registry of one [FloatingEditToolbar] capsule. */
@Stable
private class FloatingEditToolbarState {
    /** The items in layout order, written by the measure pass. */
    var items by mutableStateOf<List<FloatingEditToolbarItemHandle?>>(emptyList())

    /** The item bounds in the capsule's coordinates, written by the placement pass. */
    var itemBounds: List<Rect> = emptyList()

    /** `isMenuTouchFeedbackEnabled`: no items, or at least one of them enabled. */
    val touchFeedbackEnabled: Boolean
        get() = items.isEmpty() || items.any { it?.enabled != false }

    /** `findChildViewUnder`: the item whose bounds contain [position], if any. */
    fun itemAt(position: Offset): FloatingEditToolbarItemHandle? {
        val index = indexAt(position)
        return if (index >= 0) items.getOrNull(index) else null
    }

    /** The bounds of the item under [position], if any. */
    fun boundsAt(position: Offset): Rect? = itemBounds.getOrNull(indexAt(position))

    private fun indexAt(position: Offset): Int {
        val bounds = itemBounds
        for (index in bounds.indices) {
            val rect = bounds[index]
            if (position.x >= rect.left && position.x <= rect.right && position.y >= rect.top && position.y <= rect.bottom) {
                return index
            }
        }
        return -1
    }
}

/** Whether the items are laid out in the wide (`STYLE_EDIT_TOOLBAR_HORIZONTAL`) style. */
private val LocalFloatingEditToolbarHorizontalStyle = staticCompositionLocalOf { false }

/** An unspaced TextView line: the font metrics height, 1.1719em for Roboto. */
private const val FONT_METRICS_HEIGHT_EM = 1.1719f

/** `couiTextButtonM` line spacing (`values-v35` coui_spacing_multiplier_button_m). */
private const val BUTTON_M_LINE_SPACING = 1.2245f

/**
 * The Roboto font padding Android's `includeFontPadding` adds above the ascent, in em:
 * `(yMax 2163 - ascender 1900) / 2048`.
 */
private const val FONT_PADDING_TOP_EM = (2163f - 1900f) / 2048f
