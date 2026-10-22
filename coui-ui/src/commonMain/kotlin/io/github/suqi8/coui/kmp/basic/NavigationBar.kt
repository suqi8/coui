// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import io.github.suqi8.coui.kmp.effect.MaterialBackdropSpec
import io.github.suqi8.coui.kmp.effect.ShadowEdgePresets
import io.github.suqi8.coui.kmp.effect.ShadowEdgeShader
import io.github.suqi8.coui.kmp.effect.SpotLightShader
import io.github.suqi8.coui.kmp.effect.SpotLightState
import io.github.suqi8.coui.kmp.effect.SpotLightType
import io.github.suqi8.coui.kmp.effect.StrokeEffectParams
import io.github.suqi8.coui.kmp.effect.TouchMotion
import io.github.suqi8.coui.kmp.effect.couiTouchMotion
import io.github.suqi8.coui.kmp.shader.RuntimeShader
import io.github.suqi8.coui.kmp.shader.asBrush
import io.github.suqi8.coui.kmp.shader.isRuntimeShaderSupported
import io.github.suqi8.coui.kmp.squircle.squircleBackground
import io.github.suqi8.coui.kmp.squircle.squircleSurface
import io.github.suqi8.coui.kmp.theme.COUIRoundCorner
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.utils.Platform
import io.github.suqi8.coui.kmp.utils.platform
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

/**
 * A [NavigationBar] that with 2 to 5 items.
 *
 * @param modifier The modifier to be applied to the [NavigationBar].
 * @param color The color of the [NavigationBar]. COUI `coui_tab_navigation_view_bg` (`?couiColorBar`).
 * @param showDivider Whether to show the divider line between the [NavigationBar] and the content.
 * @param defaultWindowInsetsPadding whether to apply default window insets padding to the [NavigationBar].
 * @param mode The mode for displaying items in the [NavigationBar]. It can show icons, text or both.
 * @param content The content of the [NavigationBar], usually [NavigationBarItem]s.
 */
@Composable
fun NavigationBar(
    modifier: Modifier = Modifier,
    color: Color = NavigationBarDefaults.color(),
    showDivider: Boolean = true,
    defaultWindowInsetsPadding: Boolean = true,
    mode: NavigationBarDisplayMode = NavigationBarDisplayMode.IconAndText,
    content: @Composable RowScope.() -> Unit,
) {
    val captionBarPaddings = WindowInsets.captionBar.only(WindowInsetsSides.Bottom).asPaddingValues()
    val captionBarBottomPaddingValue = captionBarPaddings.calculateBottomPadding()

    val animatedCaptionBarHeight by animateDpAsState(
        targetValue = if (captionBarBottomPaddingValue > 0.dp) captionBarBottomPaddingValue else 0.dp,
        animationSpec = tween(durationMillis = 300),
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color),
    ) {
        if (showDivider) {
            // COUINavigationView.addCompatibilityTopDivider: ?couiColorDivider, 0.33dp
            // (coui_navigation_shadow_height); HorizontalDivider defaults already match.
            HorizontalDivider()
        }
        Row(
            // COUINavigationMenuView.onMeasure: 12dp edge padding, items divide the rest equally.
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = NavigationBarDefaults.HorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CompositionLocalProvider(LocalNavigationBarDisplayMode provides mode) {
                content()
            }
        }
        if (defaultWindowInsetsPadding) {
            val navigationBarsPadding = if (platform() != Platform.IOS) {
                WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            } else {
                20.dp
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .layout { measurable, constraints ->
                        val totalHeight = (navigationBarsPadding + animatedCaptionBarHeight).roundToPx()
                        val fixedConstraints = constraints.copy(minHeight = totalHeight, maxHeight = totalHeight)
                        val placeable = measurable.measure(fixedConstraints)
                        layout(placeable.width, totalHeight) {
                            placeable.placeRelative(0, 0)
                        }
                    }
                    .pointerInput(Unit) { detectTapGestures { /* Do nothing to consume the click */ } },
            )
        }
    }
}

/**
 * A [NavigationBarItem] that is suitable for [NavigationBar].
 *
 * @param selected Whether the item is selected.
 * @param onClick The callback when the item is clicked.
 * @param icon The icon of the item.
 * @param label The label of the item.
 * @param modifier The modifier to be applied to the [NavigationBarItem].
 * @param enabled Whether the item is enabled.
 */
@Composable
fun RowScope.NavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val colorScheme = COUITheme.colorScheme

    // COUI coui_navigation_tab_color selector: no pressed entry, switches instantly (not animated).
    val labelColor = when {
        !enabled -> colorScheme.disabledOnSurface
        selected -> colorScheme.onSurfaceContainer
        else -> colorScheme.onSurfaceSecondary
    }
    // COUI tab icon selector: selected variant shown for state_selected OR state_pressed,
    // cross-faded over 180ms.
    val iconTargetTint = when {
        !enabled -> colorScheme.disabledOnSurface
        selected || isPressed -> colorScheme.onSurfaceContainer
        else -> colorScheme.onSurfaceSecondary
    }
    val iconTint by animateColorAsState(
        targetValue = iconTargetTint,
        animationSpec = tween(durationMillis = NavigationBarDefaults.IconFadeDurationMillis, easing = LinearEasing),
        label = "navigationItemIconTint",
    )
    // COUI labels use sans-serif-medium in all states (coui_navigation_item_layout.xml); tab items
    // have no press mask, ripple or selection motion.
    val fontWeight = FontWeight.Medium
    val mode = LocalNavigationBarDisplayMode.current
    // coui_navigation_item_text_size / _large_text_size are dp: the labels ignore the font scale.
    val density = LocalDensity.current
    val labelFontSize = with(density) { NavigationBarDefaults.LabelFontSize.toSp() }
    val textFontSize = with(density) { NavigationBarDefaults.TextFontSize.toSp() }

    Box(
        modifier = modifier
            .height(NavigationBarDefaults.ItemHeight)
            .weight(1f)
            .selectable(
                selected = selected,
                onClick = onClick,
                enabled = enabled,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = null,
            )
            .padding(horizontal = NavigationBarDefaults.ItemHorizontalPadding),
    ) {
        when (mode) {
            NavigationBarDisplayMode.IconAndText -> {
                // COUINavigationItemView.topToBottom(): icon pinned to the top, label to the bottom.
                Image(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = NavigationBarDefaults.IconTopPadding)
                        .size(NavigationBarDefaults.IconSize),
                    imageVector = icon,
                    contentDescription = label,
                    colorFilter = ColorFilter.tint(iconTint),
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = NavigationBarDefaults.LabelBottomPadding),
                    text = label,
                    color = labelColor,
                    textAlign = TextAlign.Center,
                    fontSize = labelFontSize,
                    fontWeight = fontWeight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            NavigationBarDisplayMode.IconWithSelectedLabel -> {
                // Extension mode: the icon slides towards the cell center while the label fades.
                val centeredIconPadding = (NavigationBarDefaults.ItemHeight - NavigationBarDefaults.IconSize) / 2
                val iconTopPadding by animateDpAsState(
                    targetValue = if (selected) NavigationBarDefaults.IconTopPadding else centeredIconPadding,
                    animationSpec = tween(durationMillis = 300),
                    label = "iconTopPadding",
                )
                val textAlpha by animateFloatAsState(
                    targetValue = if (selected) 1f else 0f,
                    animationSpec = tween(durationMillis = 300),
                    label = "textAlpha",
                )

                Image(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = iconTopPadding)
                        .size(NavigationBarDefaults.IconSize),
                    imageVector = icon,
                    contentDescription = label,
                    colorFilter = ColorFilter.tint(iconTint),
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = NavigationBarDefaults.LabelBottomPadding)
                        .graphicsLayer { alpha = textAlpha },
                    text = label,
                    color = labelColor,
                    textAlign = TextAlign.Center,
                    fontSize = labelFontSize,
                    fontWeight = fontWeight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            NavigationBarDisplayMode.TextOnly -> {
                // COUI horizontal (large-screen) items center the label vertically.
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = label,
                    color = labelColor,
                    textAlign = TextAlign.Center,
                    fontSize = textFontSize,
                    fontWeight = fontWeight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            NavigationBarDisplayMode.IconOnly -> {
                // labelVisibilityMode UNLABELED centers the icon container (gravity CENTER).
                Image(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(NavigationBarDefaults.IconSize),
                    imageVector = icon,
                    contentDescription = label,
                    colorFilter = ColorFilter.tint(iconTint),
                )
            }
        }
    }
}

/**
 * The COUI 17 floating navigation bar (`COUIBottomFloatingNavigationBar`): a 56dp capsule of 2 to 5
 * [FloatingNavigationBarItem]s, each 70 x 48dp with a 24dp icon over a 10dp label, floating 16dp
 * above the bottom inset.
 *
 * - **Selection**: tapping an item selects it, and sliding a finger across the capsule selects the
 *   item under it as it goes (the selection follows the finger); the item the finger ends on is
 *   committed with its `onClick` when the finger lifts, wherever it lifts
 *   (`COUIBottomFloatingNavigationBarMenuView.onMenuTouchMove` / `dispatchTouchEvent`). There is no
 *   indicator and no haptic feedback: the selected item simply switches to the selected colors.
 * - **Touch motion**: the whole capsule takes the `COUITouchMotionAnimator` press scale, rubber-band
 *   drag-follow and drag deformation (`BaseBottomFloatingMenuView.initTouchMotionAnim`); the items
 *   have no press mask of their own.
 * - **Material**: COUI 17 draws the capsule with the framework bottom-bar material
 *   (`TYPE_FRAMEWORK_BOTTOM_BAR`, a 25px backdrop blur, see [FloatingNavigationBarDefaults.backdropSpec]),
 *   the `TYPE_FRAMEWORK_CAPSULE_3` edge lighting and inner shadow, and a `TYPE_OPAQUE_MEDIUM_2` touch
 *   spotlight. coui-ui has no backdrop capture of its own: pass the blur through [backgroundModifier]
 *   together with a transparent [color]. Without it the capsule keeps COUI's opaque `?couiColorBar`
 *   fallback. The edge lighting and spotlight need runtime shaders (Android 13+ and every Skia
 *   target) and are skipped elsewhere.
 * - **Show / hide**: [visible] runs the COUI 17 springs: alpha (response 0.2), scale 0.8 <-> 1
 *   (response 0.35, bounce 0.35) and, with [materialEffect], a 30px foreground blur (response 0.3)
 *   (`doMenuViewShowOrHideAnim`). A hidden bar keeps its space but takes no touches.
 *
 * @param modifier The modifier applied to the bar's container (the full-width area holding the capsule).
 * @param backgroundModifier Applied inside the capsule clip, above [color] and below the items: the
 *   place for a backdrop blur such as [FloatingNavigationBarDefaults.backdropSpec].
 * @param visible Whether the capsule is shown. Changes animate with the COUI 17 show / hide springs.
 * @param materialEffect Whether the COUI 17 material is drawn on the capsule: the edge lighting and
 *   inner shadow, the touch spotlight and the hide blur. The fill itself is [color] + [backgroundModifier].
 * @param showDivider Whether to draw a thin divider ring around the capsule (not part of COUI 17).
 * @param defaultWindowInsetsPadding Whether to keep the capsule above the navigation and caption bars.
 * @param color The capsule fill. Defaults to COUI's opaque `?couiColorBar`; pass [Color.Transparent]
 *   when [backgroundModifier] paints a backdrop blur.
 * @param cornerRadius The corner radius. The default [FloatingNavigationBarDefaults.CornerRadius]
 *   rounds the bar fully into the COUI 17 soft capsule; a smaller radius is drawn at the ColorOS
 *   default smoothing weight.
 * @param horizontalAlignment The alignment of the capsule within the bar's width.
 * @param horizontalOutSidePadding The minimum distance between the capsule and the bar's sides. When
 *   the items do not fit, they shrink equally.
 * @param shadowElevation The blur radius of the capsule's drop shadow; 0 disables it.
 * @param content The content of the [FloatingNavigationBar], usually [FloatingNavigationBarItem]s.
 */
@Composable
fun FloatingNavigationBar(
    modifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    visible: Boolean = true,
    materialEffect: Boolean = true,
    showDivider: Boolean = false,
    defaultWindowInsetsPadding: Boolean = true,
    color: Color = FloatingNavigationBarDefaults.color(),
    cornerRadius: Dp = FloatingNavigationBarDefaults.CornerRadius,
    horizontalAlignment: Alignment.Horizontal = CenterHorizontally,
    horizontalOutSidePadding: Dp = FloatingNavigationBarDefaults.HorizontalOutSidePadding,
    shadowElevation: Dp = FloatingNavigationBarDefaults.ShadowElevation,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (defaultWindowInsetsPadding) {
                    Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
                        .windowInsetsPadding(WindowInsets.captionBar.only(WindowInsetsSides.Bottom))
                } else {
                    Modifier
                },
            )
            // COUIBottomFloatingNavigationBar: the capsule is centred in a container at least 88dp
            // tall (16dp above and below), with 16dp side padding on compact screens.
            .padding(
                horizontal = horizontalOutSidePadding,
                vertical = FloatingNavigationBarDefaults.VerticalOutSidePadding,
            ),
        horizontalAlignment = horizontalAlignment,
    ) {
        FloatingNavigationBarCapsule(
            visible = visible,
            materialEffect = materialEffect,
            showDivider = showDivider,
            color = color,
            backgroundModifier = backgroundModifier,
            cornerRadius = cornerRadius,
            shadowElevation = shadowElevation,
            content = content,
        )
    }
}

@Composable
private fun FloatingNavigationBarCapsule(
    visible: Boolean,
    materialEffect: Boolean,
    showDivider: Boolean,
    color: Color,
    cornerRadius: Dp,
    shadowElevation: Dp,
    backgroundModifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val state = remember { FloatingNavigationBarState() }
    val scope = rememberCoroutineScope()
    val isDark = COUITheme.colorScheme.background.luminance() < 0.5f
    val shape = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }
    val dividerColor = COUITheme.colorScheme.dividerLine

    // doMenuViewShowOrHideAnim, melt-ball (COUI 17) springs.
    val alpha = remember { Animatable(if (visible) 1f else 0f) }
    val scale = remember { Animatable(if (visible) 1f else FloatingNavigationBarDefaults.HiddenScale) }
    val blur = remember { Animatable(0f) }
    LaunchedEffect(visible, materialEffect) {
        launch {
            alpha.animateTo(
                targetValue = if (visible) 1f else 0f,
                animationSpec = TouchMotion.spring(response = 0.2f, bounce = 0f, visibilityThreshold = 0.001f),
            )
        }
        launch {
            scale.animateTo(
                targetValue = if (visible) 1f else FloatingNavigationBarDefaults.HiddenScale,
                animationSpec = TouchMotion.spring(response = 0.35f, bounce = 0.35f, visibilityThreshold = 0.001f),
            )
        }
        // The foreground blur only runs with the material (isBlurEffectEnable).
        if (materialEffect) {
            launch {
                blur.animateTo(
                    targetValue = if (visible) 0f else FloatingNavigationBarDefaults.HiddenBlurRadiusPx,
                    animationSpec = TouchMotion.spring(response = 0.3f, bounce = 0f, visibilityThreshold = 0.01f),
                )
            }
        } else {
            blur.snapTo(0f)
        }
    }

    val shaders = remember(materialEffect) {
        if (materialEffect && isRuntimeShaderSupported()) {
            RuntimeShader(ShadowEdgeShader.Source) to RuntimeShader(SpotLightShader.Source)
        } else {
            null
        }
    }
    val spotlight = remember { SpotLightState() }
    val stroke = if (isDark) FloatingNavigationBarStrokeDark else FloatingNavigationBarStrokeLight
    val spotType = SpotLightType.OpaqueMedium2

    Layout(
        content = {
            CompositionLocalProvider(LocalFloatingNavigationBarState provides state) {
                content()
            }
        },
        modifier = Modifier
            .graphicsLayer {
                this.alpha = alpha.value
                scaleX = scale.value
                scaleY = scale.value
                val radius = blur.value
                renderEffect = if (radius > 0.01f) BlurEffect(radius, radius, TileMode.Clamp) else null
            }
            // A hidden capsule is INVISIBLE: no touches, nothing for accessibility.
            .then(if (visible) Modifier.couiTouchMotion(interactionSource = null) else Modifier.clearAndSetSemantics { })
            .then(if (shadowElevation > 0.dp) Modifier.dropShadow(shape = shape, shadow = floatingNavigationBarShadow(shadowElevation)) else Modifier)
            .then(
                if (showDivider) {
                    Modifier
                        .squircleBackground(color = dividerColor, cornerRadius = cornerRadius, weight = COUIRoundCorner.UnspecifiedWeight)
                        .padding(0.75.dp)
                } else {
                    Modifier
                },
            )
            .squircleSurface(color = color, cornerRadius = cornerRadius, weight = COUIRoundCorner.UnspecifiedWeight)
            .then(backgroundModifier)
            .then(
                if (shaders != null) {
                    Modifier.drawWithCache {
                        val radius = min(cornerRadius.toPx(), min(size.width, size.height) / 2f)
                        configureFloatingNavigationBarEdge(shaders.first, size.width, size.height, radius, stroke)
                        val edgeBrush = shaders.first.asBrush()
                        onDrawWithContent {
                            // BaseBottomFloatingMenuView.onDraw: the spotlight, clipped to the capsule,
                            // and the stroke are drawn above the background and below the items.
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
                            // The edge shader carries its own SDF mask.
                            drawRect(edgeBrush)
                            drawContent()
                        }
                    }
                } else {
                    Modifier
                },
            )
            .then(
                if (visible) {
                    Modifier.pointerInput(state, shaders != null) {
                        val spot = shaders != null
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            if (spot) scope.launch { spotlight.glow(down.position) }
                            val downItem = state.itemAt(down.position)
                            var slideItem: FloatingNavigationItemHandle? = null
                            var upPosition: Offset? = null
                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                if (!change.pressed) {
                                    upPosition = change.position
                                    break
                                }
                                if (change.position != change.previousPosition) {
                                    val position = change.position
                                    if (spot) scope.launch { spotlight.follow(position) }
                                    val inBounds = position.x in 0f..size.width.toFloat() &&
                                        position.y in 0f..size.height.toFloat()
                                    if (inBounds) {
                                        val item = state.itemAt(position)
                                        if (item != null && item.enabled && item !== state.currentSelection()) {
                                            state.slideTarget = item
                                            slideItem = item
                                        }
                                    }
                                }
                            }
                            if (spot) scope.launch { spotlight.extinguish() }
                            // A slide commits the item it ended on, wherever the finger lifts (and on
                            // cancel); a plain tap clicks the item under the finger.
                            val target = slideItem ?: upPosition?.let { position ->
                                state.itemAt(position)?.takeIf { it === downItem && it.enabled }
                            }
                            target?.onClick?.invoke()
                            state.slideTarget = null
                        }
                    }
                } else {
                    Modifier
                },
            )
            .selectableGroup(),
    ) { measurables, constraints ->
        val geometry = floatingNavigationBarLayout(
            itemCount = measurables.size,
            maxWidth = constraints.maxWidth,
            itemMaxWidth = FloatingNavigationBarDefaults.ItemMaxWidth.roundToPx(),
            innerPadding = FloatingNavigationBarDefaults.HorizontalPadding.roundToPx(),
            itemSpacing = FloatingNavigationBarDefaults.ItemSpacing.roundToPx(),
        )
        val height = FloatingNavigationBarDefaults.Height.roundToPx()
            .coerceIn(constraints.minHeight, max(constraints.minHeight, constraints.maxHeight))
        val itemHeight = min(FloatingNavigationBarDefaults.ItemHeight.roundToPx(), height)
        val placeables = measurables.map { it.measure(Constraints.fixed(geometry.itemWidth, itemHeight)) }
        state.items = measurables.map { it.layoutId as? FloatingNavigationItemHandle }
        val width = geometry.width.coerceIn(constraints.minWidth, max(constraints.minWidth, constraints.maxWidth))
        val rtl = layoutDirection == LayoutDirection.Rtl
        layout(width, height) {
            val top = (height - itemHeight) / 2
            val bounds = ArrayList<Rect>(placeables.size)
            placeables.forEachIndexed { index, placeable ->
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
 * A [FloatingNavigationBarItem] that is suitable for [FloatingNavigationBar]: a 24dp [icon] over a
 * 10dp medium [label] (COUI `COUIBottomFloatingToolbarItemView`, `STYLE_NAVI_VERTICAL`). The icon and
 * label switch instantly between [FloatingNavigationBarColors.selectedColor] and
 * [FloatingNavigationBarColors.unselectedColor]; there is no press mask (COUI strips the pressed state
 * of navigation items, the capsule's touch motion is the press feedback).
 *
 * Put the items directly in the [FloatingNavigationBar] content: the bar lays them out and drives
 * their taps and slide-to-select gestures.
 *
 * @param selected Whether the item is selected.
 * @param onClick The callback when the item is tapped, or when a slide across the bar ends on it.
 * @param icon The icon of the item, tinted with the item's content color.
 * @param label The label of the item.
 * @param modifier The modifier to be applied to the [FloatingNavigationBarItem].
 * @param enabled Whether the item is enabled. A disabled item ignores taps and slides.
 * @param colors The content colors of the item.
 */
@Composable
fun FloatingNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: FloatingNavigationBarColors = FloatingNavigationBarDefaults.itemColors(),
) {
    val barState = LocalFloatingNavigationBarState.current
    val handle = remember { FloatingNavigationItemHandle() }
    val currentOnClick by rememberUpdatedState(onClick)
    SideEffect {
        handle.selected = selected
        handle.enabled = enabled
        handle.onClick = onClick
    }
    // While a slide is in progress the bar's slide target is the selected item.
    val slideTarget = barState?.slideTarget
    val shownSelected = if (slideTarget != null) slideTarget === handle else selected
    val contentColor = colors.contentColor(enabled = enabled, selected = shownSelected)

    val density = LocalDensity.current
    val labelStyle = remember(density) {
        TextStyle(
            fontSize = with(density) { FloatingNavigationBarDefaults.LabelTextSize.toSp() },
            fontWeight = FontWeight.Medium,
            // An unspaced TextView line: the font metrics height, 1.1719em for Roboto.
            lineHeight = 1.1719f.em,
            lineHeightStyle = LineHeightStyle(
                alignment = LineHeightStyle.Alignment.Proportional,
                trim = LineHeightStyle.Trim.None,
            ),
            textAlign = TextAlign.Center,
        )
    }
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .layoutId(handle)
            .then(modifier)
            .size(FloatingNavigationBarDefaults.ItemMaxWidth, FloatingNavigationBarDefaults.ItemHeight)
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
                role = Role.Tab
                this.selected = shownSelected
                onClick {
                    if (enabled) currentOnClick()
                    enabled
                }
                if (!enabled) disabled()
            },
    ) {
        Image(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = FloatingNavigationBarDefaults.IconTopPadding)
                .size(FloatingNavigationBarDefaults.IconSize),
            imageVector = icon,
            contentDescription = null,
            colorFilter = ColorFilter.tint(contentColor),
        )
        Text(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(
                    top = FloatingNavigationBarDefaults.IconTopPadding +
                        FloatingNavigationBarDefaults.IconSize +
                        FloatingNavigationBarDefaults.LabelTopSpacing,
                ),
            text = label,
            color = contentColor,
            style = labelStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * The content colors of a [FloatingNavigationBarItem].
 *
 * @param selectedColor The icon and label color of the selected item.
 * @param unselectedColor The icon and label color of an unselected item.
 * @param disabledColor The icon and label color of a disabled item.
 */
@Immutable
data class FloatingNavigationBarColors(
    val selectedColor: Color,
    val unselectedColor: Color,
    val disabledColor: Color,
) {
    @Stable
    internal fun contentColor(enabled: Boolean, selected: Boolean): Color = when {
        !enabled -> disabledColor
        selected -> selectedColor
        else -> unselectedColor
    }
}

/** Contains default values used by [NavigationBar] and [NavigationBarItem]. */
object NavigationBarDefaults {
    /** The item cell height (COUI coui_tool_navigation_item_height). */
    val ItemHeight = 56.dp

    /** The horizontal padding of the item row (COUI coui_tool_navigation_edge_item_padding). */
    val HorizontalPadding = 12.dp

    /** The horizontal padding inside each item cell (COUI coui_navigation_item_half_gap). */
    val ItemHorizontalPadding = 2.dp

    /** The icon size (COUI coui_navigation_icon_size). */
    val IconSize = 24.dp

    /** The top padding of the icon (COUI coui_navigation_icon_margin_top). */
    val IconTopPadding = 9.dp

    /** The bottom padding of the label (COUINavigationItemView.topToBottom(): 9dp - 2dp). */
    val LabelBottomPadding = 7.dp

    /**
     * The label text size (COUI coui_navigation_item_text_size). COUI declares it in dp, so the label
     * does not follow the font scale.
     */
    val LabelFontSize = 10.dp

    /** The text-only text size (COUI coui_navigation_item_large_text_size), in dp like [LabelFontSize]. */
    val TextFontSize = 14.dp

    /** The icon cross-fade duration (COUI tab icon selector enter/exitFadeDuration, 180ms). */
    val IconFadeDurationMillis = 180

    /**
     * The bar background: COUI `coui_tab_navigation_view_bg` = `?couiColorBar`, `#FAFAFA` in the light
     * scheme and `#1E1E1E` in the dark one.
     */
    @Composable
    fun color(): Color = if (COUITheme.colorScheme.background.luminance() < 0.5f) BarColorDark else BarColorLight
}

/** Contains default values used by [FloatingNavigationBar] and [FloatingNavigationBarItem]. */
object FloatingNavigationBarDefaults {
    /**
     * The default corner radius: [COUIRoundCorner.Full], which rounds the bar fully at any height.
     * COUI 17 `BaseBottomFloatingMenuView.updateSmoothRoundPath` takes half the content height and
     * calls `addSmoothRoundRect` without a weight, which draws the soft capsule.
     */
    val CornerRadius = COUIRoundCorner.Full

    /**
     * The minimum horizontal distance between the capsule and the bar's sides
     * (coui_bottom_floating_navigation_bar_padding_horizontal).
     */
    val HorizontalOutSidePadding = 16.dp

    /**
     * The space above and below the capsule: the 56dp capsule centred in the 88dp
     * coui_bottom_floating_navigation_bar_min_height_without_padding_vertical container.
     */
    val VerticalOutSidePadding = 16.dp

    /**
     * The blur radius of the drop shadow: the COUI 17 caustic shadow
     * (`ShadowUtils.SHADOW_CAUSTIC_DOWN`, elevation 24dp, alpha 45, light 320dp above at 1600dp).
     * The caustic light layer is not reproduced.
     */
    val ShadowElevation = 24.dp

    /** The capsule height (coui_bottom_floating_navigation_bar_height). */
    val Height = 56.dp

    /** The horizontal padding inside the capsule (coui_bottom_floating_navigation_bar_item_padding). */
    val HorizontalPadding = 12.dp

    /** The spacing between items (coui_bottom_floating_navigation_bar_item_space). */
    val ItemSpacing = 8.dp

    /**
     * The item width (coui_bottom_floating_navigation_bar_item_max_width). When the items do not fit,
     * they shrink equally (`BaseBottomFloatingMenuView.onMeasure`).
     */
    val ItemMaxWidth = 70.dp

    /** The item height (coui_bottom_floating_navigation_bar_item_height), centred in the capsule. */
    val ItemHeight = 48.dp

    /** The icon size (coui_floating_toolbar_item_icon_width / _height). */
    val IconSize = 24.dp

    /** The icon's distance from the item top (coui_bottom_floating_navigation_bar_item_icon_top_margin). */
    val IconTopPadding = 4.dp

    /** The gap between icon and label (coui_bottom_floating_navigation_bar_item_icon_title_gap_vertical). */
    val LabelTopSpacing = 2.dp

    /**
     * The label text size: `dp2px(10)` (`COUIBottomFloatingToolbarItemView.applyNaviVerticalStyle`),
     * so the label does not follow the font scale.
     */
    val LabelTextSize = 10.dp

    /** The capsule scale when hidden (`MENU_VIEW_MIN_SCALE`). */
    val HiddenScale = 0.8f

    /** The capsule's own blur radius in raw pixels when hidden (`MENU_VIEW_MAX_BLUR_RADIUS`). */
    val HiddenBlurRadiusPx = 30f

    /**
     * The opaque capsule fill: COUI's non-blur fallback `coui_tab_navigation_view_bg` = `?couiColorBar`,
     * `#FAFAFA` in the light scheme and `#1E1E1E` in the dark one.
     */
    @Composable
    fun color(): Color = if (COUITheme.colorScheme.background.luminance() < 0.5f) BarColorDark else BarColorLight

    /**
     * The COUI 17 bottom-bar material, `COUIMaterialBlurEffect.TYPE_FRAMEWORK_BOTTOM_BAR`: a 25px
     * backdrop blur, then `#B3666666` COLOR_DODGE + `#99F5F5F5` LUMINOSITY in the light scheme and
     * `#80666666` OVERLAY + `#B3262626` LUMINOSITY in the dark one. Draw it through
     * [FloatingNavigationBar]'s `backgroundModifier` (for example with a
     * [io.github.suqi8.coui.kmp.effect.MaterialBackdropProvider]) and pass a transparent `color`.
     *
     * @param isDark Whether the dark scheme is active.
     */
    fun backdropSpec(isDark: Boolean): MaterialBackdropSpec = if (isDark) BackdropDark else BackdropLight

    /**
     * The item content colors (`COUIBottomFloatingToolbarItemView.resolveBottomNavDefaultTitleColorStateList`):
     * selected `?couiColorLabelPrimary` (`#E6000000` / `#E6FFFFFF`), unselected and pressed
     * `?couiColorLabelPrimaryVariant` (`#B8000000` / `#B8FFFFFF`), disabled `?couiColorLabelTertiary`
     * (`#42000000` / `#4DFFFFFF`).
     *
     * @param selectedColor The icon and label color of the selected item.
     * @param unselectedColor The icon and label color of an unselected item.
     * @param disabledColor The icon and label color of a disabled item.
     */
    @Composable
    fun itemColors(
        selectedColor: Color = COUITheme.colorScheme.onSurfaceContainer,
        unselectedColor: Color = COUITheme.colorScheme.onSurfaceContainer.copy(alpha = LABEL_PRIMARY_VARIANT_ALPHA),
        disabledColor: Color = COUITheme.colorScheme.disabledOnSurface,
    ): FloatingNavigationBarColors = remember(selectedColor, unselectedColor, disabledColor) {
        FloatingNavigationBarColors(
            selectedColor = selectedColor,
            unselectedColor = unselectedColor,
            disabledColor = disabledColor,
        )
    }

    /** `?couiColorLabelPrimaryVariant` alpha, `0xB8`. */
    private const val LABEL_PRIMARY_VARIANT_ALPHA = 0xB8 / 255f

    private val BackdropLight = MaterialBackdropSpec(
        blurRadiusPx = 25f,
        layer1 = Color(0xB3666666),
        mode1 = BlendMode.ColorDodge,
        layer2 = Color(0x99F5F5F5),
        mode2 = BlendMode.Luminosity,
    )

    private val BackdropDark = MaterialBackdropSpec(
        blurRadiusPx = 25f,
        layer1 = Color(0x80666666),
        mode1 = BlendMode.Overlay,
        layer2 = Color(0xB3262626),
        mode2 = BlendMode.Luminosity,
    )
}

/**
 * Defines the display mode for items in a NavigationBar.
 *
 * This controls whether to show both icon and text, icon only, or text only.
 */
enum class NavigationBarDisplayMode {
    /** Show both icon and text. */
    IconAndText,

    /** Show icon only. */
    IconOnly,

    /** Show text only. */
    TextOnly,

    /** Show icon always, show text only when selected. */
    IconWithSelectedLabel,
}

/**
 * A composition local to control the display mode for items in a NavigationBar.
 */
val LocalNavigationBarDisplayMode = compositionLocalOf { NavigationBarDisplayMode.IconAndText }

/**
 * The data class for [NavigationBar].
 *
 * @param label The label of the item.
 * @param icon The icon of the item.
 */
@Immutable
data class NavigationItem(
    val label: String,
    val icon: ImageVector,
)

// ?couiColorBar.
private val BarColorLight = Color(0xFFFAFAFA)
private val BarColorDark = Color(0xFF1E1E1E)

// COUIMaterialStrokeEffect FRAMEWORK_CAPSULE_PARAMS_3_LIGHT / _DARK (TYPE_FRAMEWORK_CAPSULE_3).
private val FloatingNavigationBarStrokeLight = StrokeEffectParams(0.25f, 2.5f, 0f, 0.1f, 1f, 1f)
private val FloatingNavigationBarStrokeDark = StrokeEffectParams(0.8f, 2.5f, 0f, 0.03f, 1f, 1f)

/**
 * The caustic shadow approximated as a drop shadow of [radius] at alpha 45/255, offset down by
 * `radius x 320 / 1600` (the light sits 320dp above the capsule at a 1600dp radius).
 */
private fun floatingNavigationBarShadow(radius: Dp): Shadow = Shadow(
    radius = radius,
    color = Color.Black,
    offset = DpOffset(0.dp, radius * 0.2f),
    alpha = 45f / 255f,
)

/**
 * Feeds [ShadowEdgeShader] the `TYPE_FRAMEWORK_CAPSULE_3` stroke of the capsule: edge and inner
 * shadow on, capsule style, weight 1.0 (`setCornerType(2).setWeight(1.0f)`).
 */
private fun Density.configureFloatingNavigationBarEdge(
    shader: RuntimeShader,
    width: Float,
    height: Float,
    cornerRadius: Float,
    params: StrokeEffectParams,
) {
    shader.setFloatUniform("u_commonArray", ShadowEdgeShader.commonArray(width, height, cornerRadius, weight = 1f))
    shader.setFloatUniform("u_shadow", 1f)
    shader.setFloatUniform(
        "u_shadowArray",
        ShadowEdgeShader.shadowArray(
            spec = ShadowEdgePresets.CapsuleShadow,
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
            spec = ShadowEdgePresets.CapsuleEdge,
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
 * The horizontal geometry of a [FloatingNavigationBar] capsule (`BaseBottomFloatingMenuView.onMeasure`
 * / `onLayout`), in pixels.
 *
 * @property itemWidth The width every item gets.
 * @property width The capsule width.
 */
@Immutable
internal class FloatingNavigationBarLayout(
    val itemWidth: Int,
    val width: Int,
    private val innerPadding: Int,
    private val itemSpacing: Int,
) {
    /** The start offset of item [index], before any right-to-left mirroring. */
    fun itemStart(index: Int): Int = innerPadding + index * (itemWidth + itemSpacing)
}

/**
 * `BaseBottomFloatingMenuView.onMeasure`: each item is [itemMaxWidth] wide when all of them fit into
 * [maxWidth] with the [innerPadding] on both ends and [itemSpacing] between them; otherwise the
 * items shrink equally to fill [maxWidth].
 */
internal fun floatingNavigationBarLayout(
    itemCount: Int,
    maxWidth: Int,
    itemMaxWidth: Int,
    innerPadding: Int,
    itemSpacing: Int,
): FloatingNavigationBarLayout {
    if (itemCount <= 0) return FloatingNavigationBarLayout(0, 0, innerPadding, itemSpacing)
    val gaps = (itemCount - 1) * itemSpacing
    val full = itemMaxWidth * itemCount + innerPadding * 2 + gaps
    return if (maxWidth == Constraints.Infinity || full <= maxWidth) {
        FloatingNavigationBarLayout(itemMaxWidth, full, innerPadding, itemSpacing)
    } else {
        val itemWidth = ((maxWidth - innerPadding * 2 - gaps) / itemCount).coerceAtLeast(0)
        FloatingNavigationBarLayout(itemWidth, itemWidth * itemCount + innerPadding * 2 + gaps, innerPadding, itemSpacing)
    }
}

/** What the [FloatingNavigationBar] knows about one of its [FloatingNavigationBarItem]s. */
internal class FloatingNavigationItemHandle {
    var selected: Boolean = false
    var enabled: Boolean = true
    var onClick: () -> Unit = {}
}

/** The item registry and slide-to-select state of one [FloatingNavigationBar]. */
@Stable
internal class FloatingNavigationBarState {
    /** The items in layout order, written by the measure pass. */
    var items: List<FloatingNavigationItemHandle?> = emptyList()

    /** The item bounds in the capsule's coordinates, written by the placement pass. */
    var itemBounds: List<Rect> = emptyList()

    /**
     * The item a slide has moved the selection to, shown as selected instead of the caller's
     * selection until the finger lifts (`COUIBottomFloatingNavigationBarMenuView.setSelectedItem`).
     */
    var slideTarget by mutableStateOf<FloatingNavigationItemHandle?>(null)

    /** `findChildViewUnder`: the item whose bounds contain [position], if any. */
    fun itemAt(position: Offset): FloatingNavigationItemHandle? {
        val bounds = itemBounds
        for (index in bounds.indices) {
            val rect = bounds[index]
            if (position.x >= rect.left && position.x <= rect.right && position.y >= rect.top && position.y <= rect.bottom) {
                return items.getOrNull(index)
            }
        }
        return null
    }

    /** The item currently shown as selected. */
    fun currentSelection(): FloatingNavigationItemHandle? = slideTarget ?: items.firstOrNull { it?.selected == true }
}

private val LocalFloatingNavigationBarState = staticCompositionLocalOf<FloatingNavigationBarState?> { null }
