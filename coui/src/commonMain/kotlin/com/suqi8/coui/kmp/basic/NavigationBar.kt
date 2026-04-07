// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.basic

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mocharealm.gaze.capsule.ContinuousRoundedRectangle
import com.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Immutable
data class NavigationItem(
    val label: String,
    val icon: Painter,
    val enabled: Boolean = true
)

@Composable
fun NavigationBar(
    items: List<NavigationItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    maxVisibleItems: Int = 5,
    colors: NavigationColors = CouiNavigationDefaults.colors()
) {
    if (items.isEmpty()) return

    val visibleItems = items.take(maxVisibleItems.coerceAtLeast(1))
    val shape = remember(NavigationBarTokens.ContainerCornerRadius) {
        ContinuousRoundedRectangle(
            topStart = NavigationBarTokens.ContainerCornerRadius,
            topEnd = NavigationBarTokens.ContainerCornerRadius
        )
    }
    val border = remember(colors.containerBorderColor) {
        BorderStroke(NavigationBarTokens.ContainerBorderWidth, colors.containerBorderColor)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.containerColor)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(NavigationBarTokens.ContainerHeight),
            shape = shape,
            color = colors.containerColor,
            contentColor = colors.itemColors.contentColor(enabled = true, selected = false).value,
            border = border
        ) {
            Column {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(NavigationBarTokens.DividerHeight)
                        .background(colors.dividerColor)
                )
                Row(modifier = Modifier.fillMaxSize()) {
                    visibleItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            item = item,
                            isSelected = index == selectedIndex,
                            onClick = { onItemSelected(index) },
                            colors = colors.itemColors,
                        )
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.windowInsetsPadding(
                WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)
            )
        )
    }
}

@Composable
internal fun RowScope.NavigationBarItem(
    item: NavigationItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    colors: NavigationItemColors,
    modifier: Modifier = Modifier,
    iconSize: Dp = NavigationBarTokens.IconSize,
    iconTopPadding: Dp = NavigationBarTokens.IconTopPadding,
    labelTopSpacing: Dp = NavigationBarTokens.LabelTopSpacing,
    labelBottomPadding: Dp = NavigationBarTokens.LabelBottomPadding,
) {
    NavigationItemLayout(
        item = item,
        isSelected = isSelected,
        onClick = onClick,
        colors = colors,
        modifier = modifier
            .weight(1f)
            .fillMaxHeight(),
        iconSize = iconSize,
        topPadding = iconTopPadding,
        labelSpacing = labelTopSpacing,
        bottomPadding = labelBottomPadding,
        contentAlignment = Alignment.Center
    )
}

@Composable
internal fun NavigationRailItem(
    item: NavigationItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    colors: NavigationItemColors,
    modifier: Modifier = Modifier,
    iconSize: Dp = NavigationRailTokens.IconSize,
    itemHeight: Dp = NavigationRailTokens.ItemHeight,
    labelTopSpacing: Dp = NavigationRailTokens.LabelTopSpacing,
) {
    NavigationItemLayout(
        item = item,
        isSelected = isSelected,
        onClick = onClick,
        colors = colors,
        modifier = modifier
            .fillMaxWidth()
            .height(itemHeight),
        iconSize = iconSize,
        topPadding = 0.dp,
        labelSpacing = labelTopSpacing,
        bottomPadding = 0.dp,
        contentAlignment = Alignment.Center
    )
}

@Composable
private fun NavigationItemLayout(
    item: NavigationItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    colors: NavigationItemColors,
    modifier: Modifier,
    iconSize: Dp,
    topPadding: Dp,
    labelSpacing: Dp,
    bottomPadding: Dp,
    contentAlignment: Alignment,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val contentColor by colors.contentColor(enabled = item.enabled, selected = isSelected)

    Box(
        modifier = modifier.clickable(
            enabled = item.enabled,
            interactionSource = interactionSource,
            indication = rememberCouiMaskIndication(),
            onClick = onClick
        ),
        contentAlignment = contentAlignment
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = topPadding, bottom = bottomPadding)
        ) {
            Icon(
                painter = item.icon,
                contentDescription = item.label,
                tint = contentColor,
                modifier = Modifier.size(iconSize)
            )
            Spacer(modifier = Modifier.height(labelSpacing))
            Text(
                text = item.label,
                color = contentColor,
                fontSize = NavigationSharedTokens.LabelTextSize,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Immutable
private class CouiMaskIndication : Indication {
    fun create(interactionSource: InteractionSource): Modifier.Node {
        return CouiMaskIndicationNode(interactionSource)
    }
}

private class CouiMaskIndicationNode(
    private val interactionSource: InteractionSource
) : Modifier.Node(), DrawModifierNode {

    private val animatedProgress = androidx.compose.animation.core.Animatable(0f)
    private var pressPosition: Offset = Offset.Zero

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collectLatest { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> {
                        pressPosition = interaction.pressPosition
                        animatedProgress.snapTo(0f)
                        animatedProgress.animateTo(
                            1f,
                            animationSpec = androidx.compose.animation.core.tween(100)
                        )
                    }
                    is PressInteraction.Release, is PressInteraction.Cancel -> {
                        animatedProgress.animateTo(
                            0f,
                            animationSpec = androidx.compose.animation.core.tween(200)
                        )
                    }
                }
            }
        }
    }

    override fun ContentDrawScope.draw() {
        val radius = 8.dp.toPx() * animatedProgress.value
        val alpha = animatedProgress.value * 0.1f
        drawContent()
        if (animatedProgress.value > 0f) {
            drawCircle(
                color = Color.Black.copy(alpha = alpha),
                radius = radius,
                center = pressPosition
            )
        }
    }
}

@Composable
fun rememberCouiMaskIndication(): Indication = remember { CouiMaskIndication() }

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
    val containerBorderColor: Color,
    val itemColors: NavigationItemColors
)

object CouiNavigationDefaults {
    @Composable
    fun itemColors(
        selectedColor: Color = COUITheme.colorScheme.onSurface,
        unselectedColor: Color = COUITheme.colorScheme.onSurfaceSecondary,
        disabledColor: Color = COUITheme.colorScheme.disabledOnSurface
    ): NavigationItemColors = NavigationItemColors(selectedColor, unselectedColor, disabledColor)

    @Composable
    fun colors(
        containerColor: Color = if (COUITheme.colorScheme.isDark) Color(0xFF1F1F1F) else Color(0xFFFAFAFA),
        dividerColor: Color = COUITheme.colorScheme.dividerLine,
        containerBorderColor: Color = COUITheme.colorScheme.outline.copy(alpha = if (COUITheme.colorScheme.isDark) 0.24f else 0.12f),
        itemColors: NavigationItemColors = itemColors()
    ): NavigationColors = NavigationColors(containerColor, dividerColor, containerBorderColor, itemColors)
}

internal object NavigationSharedTokens {
    val LabelTextSize = 10.sp
}

private object NavigationBarTokens {
    val ContainerHeight = 56.dp
    val ContainerCornerRadius = 18.dp
    val ContainerBorderWidth = 0.5.dp
    val DividerHeight = 0.33.dp
    val IconTopPadding = 8.dp
    val IconSize = 24.dp
    val LabelTopSpacing = 2.dp
    val LabelBottomPadding = 10.dp
}

internal object NavigationRailTokens {
    val ContainerWidth = 80.dp
    val DividerWidth = 0.33.dp
    val TopPadding = 12.dp
    val ItemHeight = 64.dp
    val IconSize = 24.dp
    val LabelTopSpacing = 4.dp
}
