// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isFinite
import androidx.compose.ui.unit.sp
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.math.max

/**
 * A [NavigationRail] that is suitable for wide screens.
 *
 * The [header] keeps the top of the rail. The items are spread over the middle of the rail as
 * `COUINavigationRailMenuView` does: one gap before each item and one after the last, each
 * `max((rail height x 0.4 - item heights) / (item count + 1), 8dp)`, the whole group centred
 * vertically (`COUINavigationRailView.onLayout`). The group never rides up under the header, and
 * the rail scrolls when the items do not fit.
 *
 * @param modifier The modifier to be applied to the [NavigationRail].
 * @param header The header of the [NavigationRail], usually a [FloatingActionButton] or a logo.
 * @param color The color of the [NavigationRail].
 * @param showDivider Whether to show the divider line between the [NavigationRail] and the content.
 * @param defaultWindowInsetsPadding whether to apply default window insets padding to the [NavigationRail].
 * @param minWidth The minimum width of the [NavigationRail].
 * @param mode The mode for displaying items in the [NavigationRail]. It can show icons, text or both.
 * @param content The content of the [NavigationRail], usually [NavigationRailItem]s. Each root
 *   composable is laid out as one item, at the rail's full width.
 */
@Composable
fun NavigationRail(
    modifier: Modifier = Modifier,
    header: @Composable (ColumnScope.() -> Unit)? = null,
    color: Color = COUITheme.colorScheme.backgroundWithCard,
    showDivider: Boolean = true,
    defaultWindowInsetsPadding: Boolean = true,
    minWidth: Dp = NavigationRailDefaults.MinWidth,
    mode: NavigationRailDisplayMode = NavigationRailDisplayMode.IconAndText,
    content: @Composable () -> Unit,
) {
    // The background and the divider run edge to edge, under the status bar; only the rail's own
    // content keeps clear of the system bars and the cutout.
    Row(
        modifier = modifier
            .fillMaxHeight()
            .background(color),
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .then(
                    if (defaultWindowInsetsPadding) {
                        Modifier
                            .windowInsetsPadding(WindowInsets.statusBars.only(WindowInsetsSides.Vertical))
                            .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Start))
                            .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Start))
                    } else {
                        Modifier
                    },
                )
                .width(minWidth)
                .fillMaxHeight(),
        ) {
            val railHeight = maxHeight
            Layout(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                content = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        if (header != null) header()
                    }
                    CompositionLocalProvider(LocalNavigationRailDisplayMode provides mode) {
                        content()
                    }
                },
            ) { measurables, constraints ->
                val width = constraints.maxWidth
                val childConstraints = Constraints(minWidth = width, maxWidth = width)
                val headerPlaceable = measurables.first().measure(childConstraints)
                val itemPlaceables = measurables.drop(1).map { it.measure(childConstraints) }
                val height = if (railHeight.isFinite) railHeight.roundToPx() else 0
                val gap = navigationRailItemGap(
                    railHeight = height,
                    itemHeights = itemPlaceables.sumOf { it.height },
                    itemCount = itemPlaceables.size,
                    minGap = NavigationRailDefaults.MinItemSpacing.roundToPx(),
                )
                val menuHeight = itemPlaceables.sumOf { it.height } + gap * (itemPlaceables.size + 1)
                // The header keeps the top of the rail; the menu never rides up under it.
                val headerBottom = if (headerPlaceable.height > 0) {
                    NavigationRailDefaults.VerticalPadding.roundToPx() + headerPlaceable.height +
                        NavigationRailDefaults.HeaderSpacing.roundToPx()
                } else {
                    0
                }
                // COUINavigationRailView.onLayout: the menu is centred in the rail.
                val menuTop = max((height - menuHeight) / 2, headerBottom)
                val layoutHeight = max(height, menuTop + menuHeight)
                layout(width, layoutHeight) {
                    if (headerPlaceable.height > 0) {
                        headerPlaceable.placeRelative(0, NavigationRailDefaults.VerticalPadding.roundToPx())
                    }
                    // COUINavigationRailMenuView.onLayout: each item follows one gap below the last.
                    var y = menuTop
                    itemPlaceables.forEach { placeable ->
                        y += gap
                        placeable.placeRelative(0, y)
                        y += placeable.height
                    }
                }
            }
        }
        if (showDivider) {
            VerticalDivider()
        }
    }
}

/**
 * A [NavigationRailItem] that is suitable for [NavigationRail].
 *
 * @param selected Whether the item is selected.
 * @param onClick The callback when the item is clicked.
 * @param icon The icon of the item.
 * @param label The label of the item.
 * @param modifier The modifier to be applied to the [NavigationRailItem].
 * @param enabled Whether the item is enabled.
 */
@Composable
fun NavigationRailItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val onSurfaceContainerColor = COUITheme.colorScheme.labelPrimary
    val tint = when {
        isPressed -> if (selected) {
            onSurfaceContainerColor.copy(alpha = NavigationRailDefaults.SelectedPressedAlpha)
        } else {
            onSurfaceContainerColor.copy(alpha = NavigationRailDefaults.UnselectedPressedAlpha)
        }

        selected -> onSurfaceContainerColor

        else -> onSurfaceContainerColor.copy(NavigationRailDefaults.UnselectedAlpha)
    }
    // coui_navigation_rail_item_layout: the labels are sans-serif-medium in every state.
    val fontWeight = FontWeight.Medium
    val mode = LocalNavigationRailDisplayMode.current
    // coui_side_navigation_rail_item_text_size is dp (setTextSize(COMPLEX_UNIT_PX)): the label
    // ignores the font scale.
    val labelFontSize = with(LocalDensity.current) { NavigationRailDefaults.LabelFontSize.toSp() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                enabled = enabled,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = null,
            )
            .padding(vertical = NavigationRailDefaults.ItemVerticalPadding)
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        when (mode) {
            NavigationRailDisplayMode.IconAndText -> {
                Image(
                    modifier = Modifier.size(NavigationRailDefaults.IconSize),
                    imageVector = icon,
                    contentDescription = label,
                    colorFilter = ColorFilter.tint(tint),
                )
                Spacer(modifier = Modifier.height(NavigationRailDefaults.IconTextSpacing))
                Text(
                    text = label,
                    color = tint,
                    textAlign = TextAlign.Center,
                    fontSize = labelFontSize,
                    fontWeight = fontWeight,
                )
            }

            NavigationRailDisplayMode.IconWithSelectedLabel -> {
                Image(
                    modifier = Modifier.size(NavigationRailDefaults.IconSize),
                    imageVector = icon,
                    contentDescription = label,
                    colorFilter = ColorFilter.tint(tint),
                )
                if (selected) {
                    Spacer(modifier = Modifier.height(NavigationRailDefaults.IconTextSpacing))
                    Text(
                        text = label,
                        color = tint,
                        textAlign = TextAlign.Center,
                        fontSize = labelFontSize,
                        fontWeight = fontWeight,
                    )
                }
            }

            NavigationRailDisplayMode.TextOnly -> {
                Text(
                    modifier = Modifier.padding(vertical = NavigationRailDefaults.TextOnlyVerticalPadding),
                    text = label,
                    color = tint,
                    textAlign = TextAlign.Center,
                    fontSize = NavigationRailDefaults.TextOnlyFontSize,
                    fontWeight = fontWeight,
                )
            }

            else -> {
                Image(
                    modifier = Modifier.size(NavigationRailDefaults.IconSize),
                    imageVector = icon,
                    contentDescription = label,
                    colorFilter = ColorFilter.tint(tint),
                )
            }
        }
    }
}

/** Contains default values used by [NavigationRail] and [NavigationRailItem]. */
object NavigationRailDefaults {
    /** The default minimum width of the [NavigationRail] (COUI coui_navigation_rail_min_width). */
    val MinWidth = 72.dp

    /** The distance of the header from the top of the [NavigationRail]. */
    val VerticalPadding = 24.dp

    /** The least spacing between the header and the first item. */
    val HeaderSpacing = 24.dp

    /**
     * The least gap before each item (coui_navigation_rail_root_view_divider_height). The gap is
     * `max((rail height x 0.4 - item heights) / (item count + 1), MinItemSpacing)`
     * (`COUINavigationRailMenuView.getDividerHeight`), so the items fill the middle 40% of the rail.
     */
    val MinItemSpacing = 8.dp

    /** The share of the rail height the items and their gaps spread over (`getDividerHeight`). */
    val ItemAreaFraction = 0.4f

    /** The default icon size (COUI coui_side_navigation_icon_size). */
    val IconSize = 24.dp

    /** The default spacing between icon and text (COUI coui_navigation_rail_text_margin_top). */
    val IconTextSpacing = 2.dp

    /** The space above the icon and below the label of each item (coui_navigation_margin_vertical). */
    val ItemVerticalPadding = 12.dp

    /**
     * The label text size (coui_side_navigation_rail_item_text_size). COUI declares it in dp, so the
     * label does not follow the font scale.
     */
    val LabelFontSize = 10.dp

    /** The font size in [NavigationRailDisplayMode.TextOnly] mode. */
    val TextOnlyFontSize = 14.sp

    /** The vertical padding in [NavigationRailDisplayMode.TextOnly] mode. */
    val TextOnlyVerticalPadding = 4.dp

    /** The alpha value for the selected item when pressed. */
    val SelectedPressedAlpha = 0.5f

    /** The alpha value for an unselected item when pressed. */
    val UnselectedPressedAlpha = 0.6f

    /** The alpha value for an unselected item. */
    val UnselectedAlpha = 0.4f
}

/**
 * Defines the display mode for items in a [NavigationRail].
 *
 * This controls whether to show both icon and text, icon only, text only,
 * or icon with text only when selected.
 */
enum class NavigationRailDisplayMode {
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
 * A composition local to control the display mode for items in a [NavigationRail].
 */
val LocalNavigationRailDisplayMode = compositionLocalOf { NavigationRailDisplayMode.IconAndText }

/**
 * `COUINavigationRailMenuView.getDividerHeight`: the gap before each of [itemCount] items whose
 * heights add up to [itemHeights], in a rail [railHeight] high.
 */
internal fun navigationRailItemGap(railHeight: Int, itemHeights: Int, itemCount: Int, minGap: Int): Int {
    val spread = ((railHeight * NavigationRailDefaults.ItemAreaFraction - itemHeights) / (itemCount + 1)).toInt()
    return max(spread, minGap)
}
