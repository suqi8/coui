// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.basic

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mocharealm.gaze.capsule.ContinuousRoundedRectangle
import com.suqi8.coui.kmp.theme.COUITheme
import kotlin.math.roundToInt

@Composable
fun SegmentedControl(
    items: List<String>,
    selectedIndex: Int,
    onSelectionChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    style: SegmentedControlStyle = SegmentedControlStyle.Regular,
    colors: SegmentedControlColors = SegmentedControlDefaults.segmentedControlColors(),
    minSegmentWidth: Dp = SegmentedControlDefaults.MinSegmentWidth,
    enabled: Boolean = true,
) {
    CouiSegmentedRow(
        items = items,
        selectedIndex = selectedIndex,
        onSelectionChange = onSelectionChange,
        modifier = modifier,
        colors = colors,
        enabled = enabled,
        metrics = SegmentedControlDefaults.metrics(
            style = style,
            minSegmentWidth = minSegmentWidth,
        ),
    )
}

@Composable
internal fun CouiSegmentedRow(
    items: List<String>,
    selectedIndex: Int,
    onSelectionChange: ((Int) -> Unit)?,
    modifier: Modifier = Modifier,
    colors: SegmentedControlColors,
    enabled: Boolean = true,
    metrics: SegmentedControlMetrics,
) {
    if (items.isEmpty()) return

    val clampedSelectedIndex = selectedIndex.coerceIn(0, items.lastIndex)
    val currentOnSelectionChange by rememberUpdatedState(onSelectionChange)
    val containerShape = remember(metrics.height, metrics.outerPadding, metrics.cornerRadius) {
        ContinuousRoundedRectangle(metrics.cornerRadius ?: metrics.height / 2)
    }
    val itemShape = remember(metrics.height, metrics.outerPadding, metrics.itemCornerRadius) {
        ContinuousRoundedRectangle(metrics.itemCornerRadius ?: (metrics.height - metrics.outerPadding * 2) / 2)
    }
    val indicatorShape = remember(metrics.height, metrics.outerPadding, metrics.indicatorCornerRadius) {
        ContinuousRoundedRectangle(
            metrics.indicatorCornerRadius ?: (metrics.height - metrics.outerPadding * 2) / 2
        )
    }

    val useContentWidth = metrics.sizeToContent && !metrics.fillMaxWidth
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    BoxWithConstraints(
        modifier = modifier
            .then(if (metrics.fillMaxWidth) Modifier.fillMaxWidth() else Modifier)
            .height(metrics.height)
            .clip(containerShape)
            .then(
                if (metrics.border != null) {
                    Modifier
                } else {
                    Modifier
                }
            )
    ) {
        val viewportWidth = (maxWidth - metrics.outerPadding * 2).coerceAtLeast(0.dp)
        val fallbackSegmentWidth = remember(
            maxWidth,
            items.size,
            metrics.minSegmentWidth,
            metrics.maxSegmentWidth,
            metrics.fillMaxWidth,
        ) {
            calculateSegmentWidth(
                itemCount = items.size,
                availableWidth = viewportWidth,
                minSegmentWidth = metrics.minSegmentWidth,
                maxSegmentWidth = metrics.maxSegmentWidth,
                fillAvailableWidth = metrics.fillMaxWidth,
            )
        }
        val itemWidthsPx = remember(
            density,
            textMeasurer,
            fallbackSegmentWidth,
            metrics.minSegmentWidth,
            metrics.maxSegmentWidth,
            metrics.horizontalItemPadding,
            metrics.itemHorizontalInset,
            metrics.textStyle,
            items,
            useContentWidth,
        ) {
            List(items.size) { index ->
                if (!useContentWidth) {
                    with(density) { fallbackSegmentWidth.roundToPx() }
                } else {
                    val normalTextWidth = textMeasurer.measure(
                        text = AnnotatedString(items[index]),
                        style = metrics.textStyle.merge(TextStyle(fontWeight = FontWeight.Normal)),
                    ).size.width
                    val selectedTextWidth = textMeasurer.measure(
                        text = AnnotatedString(items[index]),
                        style = metrics.textStyle.merge(TextStyle(fontWeight = FontWeight.Medium)),
                    ).size.width
                    val horizontalPaddingPx = with(density) {
                        ((metrics.horizontalItemPadding + metrics.itemHorizontalInset) * 2).roundToPx()
                    }
                    val widthBufferPx = with(density) { 8.dp.roundToPx() }
                    val measuredPx = maxOf(normalTextWidth, selectedTextWidth) + horizontalPaddingPx + widthBufferPx
                    val minWidthPx = with(density) { metrics.minSegmentWidth.roundToPx() }
                    val maxWidthPx = metrics.maxSegmentWidth?.let { with(density) { it.roundToPx() } }
                    if (maxWidthPx != null) {
                        measuredPx.coerceIn(minWidthPx, maxWidthPx)
                    } else {
                        measuredPx.coerceAtLeast(minWidthPx)
                    }
                }
            }
        }
        val itemWidths = remember(itemWidthsPx, density) {
            itemWidthsPx.map { with(density) { it.toDp() } }
        }
        val trackWidth = remember(viewportWidth, itemWidthsPx, metrics.fillMaxWidth, density) {
            val contentWidth = with(density) { itemWidthsPx.sum().toDp() }
            if (metrics.fillMaxWidth) maxOf(viewportWidth, contentWidth) else contentWidth
        }
        val selectedItemWidth = with(density) {
            itemWidthsPx.getOrElse(clampedSelectedIndex) { fallbackSegmentWidth.roundToPx() }.toDp()
        }
        val selectedItemOffset = with(density) {
            itemWidthsPx.take(clampedSelectedIndex).sum().toDp()
        }
        val indicatorOffset by animateDpAsState(
            targetValue = selectedItemOffset,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        val indicatorWidth by animateDpAsState(
            targetValue = selectedItemWidth,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        val indicatorBackgroundHorizontalPadding = metrics.indicatorBackgroundHorizontalPadding
            ?.coerceAtMost(selectedItemWidth / 2)
            ?: 0.dp
        val indicatorHorizontalPadding = metrics.indicatorHorizontalPadding
            ?.coerceAtMost(selectedItemWidth / 2)
            ?: 0.dp
        val indicatorBackgroundOffset by animateDpAsState(
            targetValue = selectedItemOffset + indicatorBackgroundHorizontalPadding,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        val indicatorBackgroundWidth by animateDpAsState(
            targetValue = (selectedItemWidth - indicatorBackgroundHorizontalPadding * 2).coerceAtLeast(0.dp),
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        val actualIndicatorOffset by animateDpAsState(
            targetValue = selectedItemOffset + indicatorHorizontalPadding,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        val actualIndicatorWidth by animateDpAsState(
            targetValue = (selectedItemWidth - indicatorHorizontalPadding * 2).coerceAtLeast(0.dp),
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        val scrollState = rememberScrollState()
        val canScroll = trackWidth > viewportWidth

        LaunchedEffect(clampedSelectedIndex, selectedItemOffset, selectedItemWidth, viewportWidth, trackWidth, canScroll) {
            if (!canScroll) return@LaunchedEffect
            val targetOffset = selectedItemOffset - (viewportWidth - selectedItemWidth) / 2
            val maxOffset = (trackWidth - viewportWidth).coerceAtLeast(0.dp)
            val clampedOffset = targetOffset.coerceIn(0.dp, maxOffset)
            scrollState.animateScrollTo(with(density) { clampedOffset.roundToPx() })
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = containerShape,
            color = colors.containerColor(enabled),
            border = metrics.border,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(metrics.outerPadding)
                    .horizontalScroll(scrollState, enabled = canScroll)
            ) {
                Box(
                    modifier = Modifier
                        .width(trackWidth)
                        .fillMaxHeight()
                ) {
                    if (metrics.indicatorBackgroundHeight != null || metrics.indicatorBackgroundHorizontalPadding != null) {
                        Surface(
                            modifier = Modifier
                                .padding(start = indicatorBackgroundOffset)
                                .then(
                                    if (metrics.indicatorBackgroundAtBottom && metrics.indicatorBackgroundHeight != null) {
                                        Modifier
                                            .align(Alignment.BottomStart)
                                            .height(metrics.indicatorBackgroundHeight)
                                    } else {
                                        Modifier.fillMaxHeight()
                                    }
                                )
                                .width(indicatorBackgroundWidth),
                            shape = indicatorShape,
                            color = colors.indicatorBackgroundColor(enabled = enabled),
                        ) {}
                    }

                    Surface(
                        modifier = Modifier
                            .padding(start = actualIndicatorOffset)
                            .then(
                                if (metrics.indicatorAtBottom && metrics.indicatorHeight != null) {
                                    Modifier
                                        .align(Alignment.BottomStart)
                                        .height(metrics.indicatorHeight)
                                } else {
                                    Modifier.fillMaxHeight()
                                }
                            )
                            .width(actualIndicatorWidth),
                        shape = indicatorShape,
                        color = colors.indicatorColor(enabled = enabled),
                        shadowElevation = metrics.indicatorShadowElevation,
                    ) {}

                    Row(
                        modifier = Modifier
                            .then(
                                if (useContentWidth) {
                                    Modifier.fillMaxHeight()
                                } else {
                                    Modifier
                                        .width(trackWidth)
                                        .fillMaxHeight()
                                }
                            )
                    ) {
                        items.forEachIndexed { index, item ->
                            SegmentedControlItem(
                                text = item,
                                selected = index == clampedSelectedIndex,
                                enabled = enabled,
                                onClick = {
                                    if (index != clampedSelectedIndex) {
                                        currentOnSelectionChange?.invoke(index)
                                    }
                                },
                                modifier = if (useContentWidth) {
                                    Modifier
                                        .fillMaxHeight()
                                        .width(itemWidths[index])
                                } else {
                                    Modifier
                                        .fillMaxHeight()
                                        .width(fallbackSegmentWidth)
                                },
                                colors = colors,
                                metrics = metrics,
                                itemShape = itemShape,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SegmentedControlItem(
    text: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
    colors: SegmentedControlColors,
    metrics: SegmentedControlMetrics,
    itemShape: ContinuousRoundedRectangle,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val textColor by animateColorAsState(
        targetValue = colors.contentColor(
            selected = selected,
            enabled = enabled,
            pressed = pressed,
        ),
        animationSpec = tween(durationMillis = SegmentedControlDefaults.TextColorAnimationDurationMillis)
    )

    Box(
        modifier = modifier
            .padding(horizontal = metrics.itemHorizontalInset)
            .clip(itemShape)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .semantics {
                role = Role.Tab
                this.selected = selected
            }
            .padding(horizontal = metrics.horizontalItemPadding),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            style = metrics.textStyle,
            fontWeight = if (selected || pressed) FontWeight.Medium else FontWeight.Normal,
        )
    }
}

private fun calculateSegmentWidth(
    itemCount: Int,
    availableWidth: Dp,
    minSegmentWidth: Dp,
    maxSegmentWidth: Dp?,
    fillAvailableWidth: Boolean,
): Dp {
    if (itemCount <= 0) return minSegmentWidth
    if (availableWidth <= 0.dp) return minSegmentWidth

    val idealWidth = availableWidth / itemCount
    if (maxSegmentWidth == null) return idealWidth.coerceAtLeast(minSegmentWidth)

    return when {
        fillAvailableWidth -> idealWidth.coerceIn(minSegmentWidth, maxSegmentWidth)
        itemCount <= 4 -> idealWidth.coerceAtLeast(minSegmentWidth)
        else -> idealWidth.coerceIn(minSegmentWidth, maxSegmentWidth)
    }
}

sealed interface SegmentedControlStyle {
    data object Regular : SegmentedControlStyle
    data object Compact : SegmentedControlStyle
}

@Immutable
internal data class SegmentedControlMetrics(
    val height: Dp,
    val outerPadding: Dp,
    val horizontalItemPadding: Dp,
    val minSegmentWidth: Dp,
    val maxSegmentWidth: Dp? = null,
    val indicatorShadowElevation: Dp,
    val textStyle: TextStyle,
    val border: BorderStroke? = null,
    val fillMaxWidth: Boolean = true,
    val sizeToContent: Boolean = false,
    val cornerRadius: Dp? = null,
    val indicatorCornerRadius: Dp? = null,
    val itemCornerRadius: Dp? = null,
    val indicatorHeight: Dp? = null,
    val indicatorAtBottom: Boolean = false,
    val indicatorHorizontalPadding: Dp? = null,
    val indicatorBackgroundHeight: Dp? = null,
    val indicatorBackgroundAtBottom: Boolean = false,
    val indicatorBackgroundHorizontalPadding: Dp? = null,
    val itemHorizontalInset: Dp = 0.dp,
)

object SegmentedControlDefaults {
    val RegularHeight = 40.dp
    val CompactHeight = 32.dp
    val RegularOuterPadding = 4.dp
    val CompactOuterPadding = 2.dp
    val HorizontalItemPadding = 12.dp
    val CompactHorizontalItemPadding = 10.dp
    val MinSegmentWidth = 52.dp
    val IndicatorShadowElevation = 4.dp
    const val TextColorAnimationDurationMillis = 220

    @Composable
    fun segmentedControlColors(
        containerColor: Color = COUITheme.colorScheme.background,
        indicatorColor: Color = COUITheme.colorScheme.surface,
        indicatorBackgroundColor: Color? = null,
        contentColor: Color = COUITheme.colorScheme.onSurfaceVariantSummary,
        selectedContentColor: Color = COUITheme.colorScheme.onSurface,
        pressedContentColor: Color = COUITheme.colorScheme.onSurfaceSecondary,
        pressedSelectedContentColor: Color = COUITheme.colorScheme.onSurface,
        disabledContainerColor: Color = COUITheme.colorScheme.background.copy(alpha = 0.72f),
        disabledIndicatorColor: Color = COUITheme.colorScheme.surface.copy(alpha = 0.8f),
        disabledIndicatorBackgroundColor: Color? = indicatorBackgroundColor,
        disabledContentColor: Color = COUITheme.colorScheme.onSurfaceVariantSummary.copy(alpha = 0.45f),
        disabledSelectedContentColor: Color = COUITheme.colorScheme.onSurface.copy(alpha = 0.45f),
    ): SegmentedControlColors = SegmentedControlColors(
        containerColor = containerColor,
        indicatorColor = indicatorColor,
        indicatorBackgroundColor = indicatorBackgroundColor,
        contentColor = contentColor,
        selectedContentColor = selectedContentColor,
        pressedContentColor = pressedContentColor,
        pressedSelectedContentColor = pressedSelectedContentColor,
        disabledContainerColor = disabledContainerColor,
        disabledIndicatorColor = disabledIndicatorColor,
        disabledIndicatorBackgroundColor = disabledIndicatorBackgroundColor,
        disabledContentColor = disabledContentColor,
        disabledSelectedContentColor = disabledSelectedContentColor,
    )

    @Stable
    fun height(style: SegmentedControlStyle): Dp = when (style) {
        SegmentedControlStyle.Regular -> RegularHeight
        SegmentedControlStyle.Compact -> CompactHeight
    }

    @Stable
    fun outerPadding(style: SegmentedControlStyle): Dp = when (style) {
        SegmentedControlStyle.Regular -> RegularOuterPadding
        SegmentedControlStyle.Compact -> CompactOuterPadding
    }

    @Composable
    internal fun metrics(
        style: SegmentedControlStyle,
        minSegmentWidth: Dp,
    ): SegmentedControlMetrics = SegmentedControlMetrics(
        height = height(style),
        outerPadding = outerPadding(style),
        horizontalItemPadding = if (style == SegmentedControlStyle.Compact) {
            CompactHorizontalItemPadding
        } else {
            HorizontalItemPadding
        },
        minSegmentWidth = minSegmentWidth,
        indicatorShadowElevation = IndicatorShadowElevation,
        textStyle = if (style == SegmentedControlStyle.Compact) {
            COUITheme.textStyles.body
        } else {
            COUITheme.textStyles.headline
        },
    )
}

@Immutable
class SegmentedControlColors(
    private val containerColor: Color,
    private val indicatorColor: Color,
    private val indicatorBackgroundColor: Color? = null,
    private val contentColor: Color,
    private val selectedContentColor: Color,
    private val pressedContentColor: Color,
    private val pressedSelectedContentColor: Color,
    private val disabledContainerColor: Color,
    private val disabledIndicatorColor: Color,
    private val disabledIndicatorBackgroundColor: Color? = indicatorBackgroundColor,
    private val disabledContentColor: Color,
    private val disabledSelectedContentColor: Color,
) {
    @Stable
    internal fun containerColor(enabled: Boolean): Color =
        if (enabled) containerColor else disabledContainerColor

    @Stable
    internal fun indicatorColor(enabled: Boolean): Color =
        if (enabled) indicatorColor else disabledIndicatorColor

    @Stable
    internal fun indicatorBackgroundColor(enabled: Boolean): Color =
        if (enabled) {
            indicatorBackgroundColor ?: Color.Transparent
        } else {
            disabledIndicatorBackgroundColor ?: indicatorBackgroundColor ?: Color.Transparent
        }

    @Stable
    internal fun contentColor(selected: Boolean, enabled: Boolean, pressed: Boolean): Color = when {
        enabled && pressed && selected -> pressedSelectedContentColor
        enabled && pressed -> pressedContentColor
        enabled && selected -> selectedContentColor
        enabled -> contentColor
        selected -> disabledSelectedContentColor
        else -> disabledContentColor
    }
}
