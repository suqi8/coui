// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.utils.Platform
import io.github.suqi8.coui.kmp.utils.platform
import kotlin.math.roundToInt

/**
 * A [NavigationBar] that with 2 to 5 items.
 *
 * Toggling [visible] runs the `COUINavigationView` show / hide translation: hiding pulls the bar
 * down by its own height over 200ms (`COUIOutEaseInterpolator`) while its layout height shrinks to
 * nothing (the negative bottom margin of `mHideTranslateAnimation`); showing reverses it over 250ms
 * (`COUIInEaseInterpolator`).
 *
 * @param modifier The modifier to be applied to the [NavigationBar].
 * @param visible Whether the bar is shown. Changes animate with the COUI show / hide translation.
 * @param color The color of the [NavigationBar]. COUI `coui_tab_navigation_view_bg` (`?couiColorBar`).
 * @param showDivider Whether to show the divider line between the [NavigationBar] and the content.
 * @param defaultWindowInsetsPadding whether to apply default window insets padding to the [NavigationBar].
 * @param mode The mode for displaying items in the [NavigationBar]. It can show icons, text or both.
 * @param content The content of the [NavigationBar], usually [NavigationBarItem]s.
 */
@Composable
fun NavigationBar(
    modifier: Modifier = Modifier,
    visible: Boolean = true,
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

    // COUINavigationView mHideTranslateAnimation / mShowTranslateAnimation: the hidden fraction.
    val hidden = remember { Animatable(if (visible) 0f else 1f) }
    LaunchedEffect(visible) {
        hidden.animateTo(
            targetValue = if (visible) 0f else 1f,
            animationSpec = if (visible) {
                tween(durationMillis = NavigationBarDefaults.ShowDurationMillis, easing = NavigationBarShowEasing)
            } else {
                tween(durationMillis = NavigationBarDefaults.HideDurationMillis, easing = NavigationBarHideEasing)
            },
        )
    }

    Column(
        modifier = modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                // bottomMargin = -fraction * height: the layout shrinks and the bar slides down.
                val height = (placeable.height * (1f - hidden.value)).roundToInt()
                layout(placeable.width, height) { placeable.placeRelative(0, 0) }
            }
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
 * @param badge The red dot on the icon (`COUINavigationView.setTipsView`): `null` for none, 0 for a
 *   plain 6dp dot, a positive number for a number badge. Not shown in
 *   [NavigationBarDisplayMode.TextOnly], which has no icon to anchor it.
 */
@Composable
fun RowScope.NavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    badge: Int? = null,
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
                NavigationBarItemIcon(
                    icon = icon,
                    tint = iconTint,
                    label = label,
                    badge = badge.takeIf { enabled },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = NavigationBarDefaults.IconTopPadding),
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

                NavigationBarItemIcon(
                    icon = icon,
                    tint = iconTint,
                    label = label,
                    badge = badge.takeIf { enabled },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = iconTopPadding),
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
                NavigationBarItemIcon(
                    icon = icon,
                    tint = iconTint,
                    label = label,
                    badge = badge.takeIf { enabled },
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }
}

/**
 * The classic item icon with its red dot, `COUINavigationItemView.layoutRedDot`: the dot sits on the
 * icon's top end corner, pushed out by `coui_navigation_red_dot_offset` (1dp) both ways; a number
 * badge's end edge lies `coui_navigation_red_dot_with_number_horizontal_offset` (8dp, -2dp under 10,
 * +1dp from 100 to 999) past the icon's end and its top `..._vertical_offset` (5dp) above the icon.
 */
@Composable
private fun NavigationBarItemIcon(
    icon: ImageVector,
    tint: Color,
    label: String,
    badge: Int?,
    modifier: Modifier = Modifier,
) {
    Layout(
        modifier = modifier,
        content = {
            Image(
                modifier = Modifier.size(NavigationBarDefaults.IconSize),
                imageVector = icon,
                contentDescription = label,
                colorFilter = ColorFilter.tint(tint),
            )
            if (badge != null) Badge(count = badge)
        },
    ) { measurables, constraints ->
        val iconPlaceable = measurables[0].measure(constraints)
        val badgePlaceable = measurables.getOrNull(1)?.measure(Constraints())
        layout(iconPlaceable.width, iconPlaceable.height) {
            iconPlaceable.placeRelative(0, 0)
            if (badgePlaceable != null && badge != null) {
                val iconEnd = iconPlaceable.width
                if (badge <= 0) {
                    val offset = NavigationBarDefaults.BadgeDotOffset.roundToPx()
                    badgePlaceable.placeRelative(iconEnd - badgePlaceable.width + offset, -offset)
                } else {
                    val horizontal = NavigationBarDefaults.BadgeNumberHorizontalOffset + when {
                        badge < 10 -> (-2).dp
                        badge in 100..999 -> 1.dp
                        else -> 0.dp
                    }
                    badgePlaceable.placeRelative(
                        iconEnd - badgePlaceable.width + horizontal.roundToPx(),
                        -NavigationBarDefaults.BadgeNumberVerticalOffset.roundToPx(),
                    )
                }
            }
        }
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

    /** The hide translation duration (`DEFAULT_HIDE_TRANSLATE_ANIMATION_DURATION`). */
    val HideDurationMillis = 200

    /** The show translation duration (`DEFAULT_SHOW_TRANSLATE_ANIMATION_DURATION`). */
    val ShowDurationMillis = 250

    /** How far a red dot sticks out of the icon's top end corner (coui_navigation_red_dot_offset). */
    val BadgeDotOffset = 1.dp

    /**
     * How far a number badge's end edge lies past the icon's end
     * (coui_navigation_red_dot_with_number_horizontal_offset), before the digit-count correction.
     */
    val BadgeNumberHorizontalOffset = 8.dp

    /** How far a number badge rises above the icon (coui_navigation_red_dot_with_number_vertical_offset). */
    val BadgeNumberVerticalOffset = 5.dp

    /**
     * The bar background: COUI `coui_tab_navigation_view_bg` = `?couiColorBar`, `#FAFAFA` in the light
     * scheme and `#1E1E1E` in the dark one.
     */
    @Composable
    fun color(): Color = if (COUITheme.colorScheme.background.luminance() < 0.5f) BarColorDark else BarColorLight
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
internal val BarColorLight = Color(0xFFFAFAFA)
internal val BarColorDark = Color(0xFF1E1E1E)

/** COUIInEaseInterpolator, the show translation. */
private val NavigationBarShowEasing = CubicBezierEasing(0f, 0f, 0.1f, 1f)

/** COUIOutEaseInterpolator, the hide translation. */
private val NavigationBarHideEasing = CubicBezierEasing(0.3f, 0f, 1f, 1f)
