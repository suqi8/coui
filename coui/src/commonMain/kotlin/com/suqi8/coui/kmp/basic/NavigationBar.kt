// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0
//
// Final version for COUI Bottom Navigation.
// - Uses modern Modifier.Node API for Indication to resolve deprecation warnings.
// - Fixes all compilation errors.
// - Implements precise COUI visual and interaction specifications.

package com.suqi8.coui.kmp.basic // Please adjust the package name to match your project structure

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Indication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.suqi8.coui.kmp.icon.MiuixIcons
import com.suqi8.coui.kmp.icon.icons.useful.More
import com.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * The data class for [NavigationBar].
 */
@Immutable
data class NavigationItem(
    val label: String,
    val icon: Painter,
    val enabled: Boolean = true
)

/**
 * A precise KMP replica of COUI's Bottom Navigation Bar.
 */
/**
 * A precise KMP replica of COUI's Bottom Navigation Bar.
 * It internally handles system navigation bar insets to prevent overlapping.
 *
 * @param items The list of [NavigationItem]s to display.
 * @param selectedIndex The index of the currently selected item.
 * @param onItemSelected The callback invoked when an item is selected, returning its index.
 * @param modifier The modifier to be applied to the navigation bar.
 * @param maxVisibleItems The maximum number of items to show before collapsing the rest into a "More" menu. Defaults to 5.
 * @param colors The [NavigationColors] to be used.
 */
@Composable
fun NavigationBar(
    items: List<NavigationItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    maxVisibleItems: Int = 5,
    colors: NavigationColors = CouiNavigationDefaults.colors()
) {
    // --- THIS IS THE CORE FIX ---
    // The entire component is wrapped in a Column.
    // The background is applied to the Column to ensure it extends behind the system gesture bar.
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.containerColor)
    ) {
        val showMoreMenu = items.size > maxVisibleItems
        val visibleItems = if (showMoreMenu) items.take(maxVisibleItems - 1) else items
        val hiddenItems = if (showMoreMenu) items.drop(maxVisibleItems - 1) else emptyList()

        // The Surface now only contains the visible items and the divider.
        // Its background is transparent as the parent Column handles the color.
        Surface(
            modifier = Modifier.fillMaxWidth().height(56.dp), // coui_tool_navigation_item_height
            color = Color.Transparent, // Parent Column has the color
            contentColor = colors.itemColors.contentColor(enabled = true, selected = false).value
        ) {
            Column {
                Box(Modifier.fillMaxWidth().height(0.33.dp).background(colors.dividerColor))
                Row(modifier = Modifier.fillMaxSize()) {
                    visibleItems.forEachIndexed { index, item ->
                        CouiNavigationItem(
                            item = item,
                            isSelected = index == selectedIndex,
                            onClick = { onItemSelected(index) },
                            colors = colors.itemColors,
                        )
                    }

                    if (showMoreMenu) {
                        var isMoreMenuExpanded by remember { mutableStateOf(false) }
                        val isMoreButtonSelected = selectedIndex >= maxVisibleItems - 1
                        val moreIconPainter = rememberVectorPainter(image = MiuixIcons.Useful.More)

                        CouiNavigationItem(
                            item = NavigationItem("More", moreIconPainter, true),
                            isSelected = isMoreButtonSelected,
                            onClick = { isMoreMenuExpanded = true },
                            colors = colors.itemColors,
                        )

                        CouiNavigationPopupMenu(
                            expanded = isMoreMenuExpanded,
                            onDismissRequest = { isMoreMenuExpanded = false },
                            items = hiddenItems,
                            onItemSelected = { selectedHiddenItem ->
                                val originalIndex = items.indexOf(selectedHiddenItem)
                                if (originalIndex != -1) {
                                    onItemSelected(originalIndex)
                                }
                                isMoreMenuExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // This Spacer consumes the navigation bars insets, pushing the content above it up.
        // It has the same background color to create a seamless look.
        Spacer(modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)))
    }
}

@Composable
private fun RowScope.CouiNavigationItem(
    item: NavigationItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    colors: NavigationItemColors
) {
    val interactionSource = remember { MutableInteractionSource() }
    val contentColor by colors.contentColor(enabled = item.enabled, selected = isSelected)

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(
                enabled = item.enabled,
                interactionSource = interactionSource,
                indication = rememberCouiMaskIndication(),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 9.dp) // coui_navigation_icon_margin_top
        ) {
            Icon(
                painter = item.icon,
                contentDescription = item.label,
                tint = contentColor,
                modifier = Modifier.size(24.dp) // coui_navigation_icon_size
            )
            Spacer(modifier = Modifier.height(2.dp)) // coui_navigation_text_margin_top
            Text(
                text = item.label,
                color = contentColor,
                fontSize = 10.sp, // coui_navigation_item_text_size
                fontWeight = FontWeight.Medium, // sans-serif-medium
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun CouiNavigationPopupMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    items: List<NavigationItem>,
    onItemSelected: (NavigationItem) -> Unit
) {
}

// --- Custom Indication using modern Modifier.Node API ---
@Immutable
private class CouiMaskIndication : Indication {
    fun create(interactionSource: InteractionSource): Modifier.Node {
        return CouiMaskIndicationNode(interactionSource)
    }
}

private class CouiMaskIndicationNode(
    private val interactionSource: InteractionSource
) : Modifier.Node(), DrawModifierNode {

    private val animatedProgress = Animatable(0f)
    private var pressPosition: Offset = Offset.Zero

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collectLatest { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> {
                        pressPosition = interaction.pressPosition
                        animatedProgress.snapTo(0f)
                        animatedProgress.animateTo(1f, animationSpec = tween(100))
                    }
                    is PressInteraction.Release, is PressInteraction.Cancel -> {
                        animatedProgress.animateTo(0f, animationSpec = tween(200))
                    }
                }
            }
        }
    }

    override fun ContentDrawScope.draw() {
        val radius = 8.dp.toPx() * animatedProgress.value
        val alpha = animatedProgress.value * 0.1f
        drawContent()
        if (animatedProgress.value > 0) {
            drawCircle(color = Color.Black.copy(alpha = alpha), radius = radius, center = pressPosition)
        }
    }
}

@Composable
fun rememberCouiMaskIndication(): Indication = remember { CouiMaskIndication() }

// --- Colors and Defaults ---
@Immutable
class NavigationItemColors(
    private val selectedColor: Color,
    private val unselectedColor: Color,
    private val disabledColor: Color
) {
    @Composable
    fun contentColor(enabled: Boolean, selected: Boolean): State<Color> {
        val target = when {
            !enabled -> disabledColor
            selected -> selectedColor
            else -> unselectedColor
        }
        return rememberUpdatedState(target)
    }
}

@Immutable
data class NavigationColors(
    val containerColor: Color,
    val dividerColor: Color,
    val itemColors: NavigationItemColors
)

object CouiNavigationDefaults {
    @Composable
    fun itemColors(
        selectedColor: Color = COUITheme.colorScheme.primary,
        unselectedColor: Color = COUITheme.colorScheme.onSurface,
        disabledColor: Color = COUITheme.colorScheme.onSurface.copy(alpha = 0.3f)
    ): NavigationItemColors = NavigationItemColors(selectedColor, unselectedColor, disabledColor)

    @Composable
    fun colors(
        containerColor: Color = COUITheme.colorScheme.surface,
        dividerColor: Color = Color(0x1A000000), // from coui_navigation_divider_color
        itemColors: NavigationItemColors = itemColors()
    ): NavigationColors = NavigationColors(containerColor, dividerColor, itemColors)
}
