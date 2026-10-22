// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.util.lerp
import io.github.suqi8.coui.kmp.squircle.addSquircleRect
import io.github.suqi8.coui.kmp.squircle.isSquircleEnabled
import io.github.suqi8.coui.kmp.theme.COUIRoundCorner
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

/**
 * A [TabRow] with COUI style.
 *
 * A port of the ColorOS sliding tab layout (`COUITabLayout` + `COUISlidingTabStrip`): plain text
 * tabs over a 2dp track, with a 2dp neutral indicator as wide as the selected label (at least
 * 32dp) that slides between tabs. The selected label switches to the medium weight instantly while
 * the label colors crossfade with the indicator.
 *
 * Tabs are laid out like `COUISlidingTabStrip.onMeasure`: when the labels fit into the 360dp
 * content width they are spread with equal gaps, when they fit into the row they are centered with
 * [itemSpacing] between them, and otherwise the row scrolls with fading edges.
 *
 * @param tabs The text to be displayed in the [TabRow].
 * @param selectedTabIndex The selected tab index of the [TabRow].
 * @param onTabSelected The callback when a tab is selected.
 * @param modifier The modifier to be applied to the [TabRow].
 * @param colors The colors of the [TabRow], see [TabRowDefaults.underlineColors].
 * @param minWidth The minimum width of the label slot of a tab.
 * @param maxWidth The maximum width of the label slot of a tab; longer labels are ellipsized.
 *   [Dp.Unspecified] uses 70% of the row width like COUI, [Dp.Infinity] removes the limit.
 * @param height The height of the [TabRow].
 * @param cornerRadius The corner radius of the press mask of a tab.
 * @param itemSpacing The minimum spacing between two labels. [Dp.Unspecified] uses the COUI
 *   responsive value (20dp below a 600dp wide row, 24dp otherwise).
 * @param horizontalMargin The minimum margin before the first and after the last label.
 *   [Dp.Unspecified] uses the COUI responsive value (16dp, 24dp from 600dp, 40dp from 840dp).
 * @param contentAlignment The content alignment of the label in its tab.
 * @param listState The [LazyListState] to be used for the [TabRow].
 * @param interactionSource The [MutableInteractionSource] to be used for the [TabRow].
 * @param indication The press mask drawn over a tab, clipped to [cornerRadius].
 */
@Composable
fun TabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: TabRowColors = TabRowDefaults.underlineColors(),
    minWidth: Dp = TabRowDefaults.TabRowMinWidth,
    maxWidth: Dp = TabRowDefaults.TabRowMaxWidth,
    height: Dp = TabRowDefaults.TabRowHeight,
    cornerRadius: Dp = TabRowDefaults.TabRowCornerRadius,
    itemSpacing: Dp = Dp.Unspecified,
    horizontalMargin: Dp = Dp.Unspecified,
    contentAlignment: Alignment = Alignment.Center,
    listState: LazyListState? = null,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = LocalIndication.current,
) {
    val currentOnTabSelected by rememberUpdatedState(onTabSelected)
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val textMeasurer = rememberTextMeasurer()
    val labelStyle = COUITheme.textStyles.body2
    // COUITabLayout: sans-serif-medium for the selected tab, sans-serif for the others.
    val normalStyle = remember(labelStyle) { labelStyle.copy(fontWeight = FontWeight.Normal) }
    val selectedStyle = remember(labelStyle) { labelStyle.copy(fontWeight = FontWeight.Medium) }
    val trackColor = colors.backgroundColor(false)
    val indicatorColor = colors.backgroundColor(true)
    val normalColor = colors.contentColor(false)
    val selectedColor = colors.contentColor(true)
    val resolvedListState = listState ?: rememberLazyListState()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
            .height(height),
    ) {
        val widthPx = constraints.maxWidth
        val geometry = remember(
            tabs,
            selectedTabIndex,
            widthPx,
            minWidth,
            maxWidth,
            itemSpacing,
            horizontalMargin,
            contentAlignment,
            textMeasurer,
            normalStyle,
            selectedStyle,
            density,
        ) {
            with(density) {
                computeUnderlineTabGeometry(
                    tabs = tabs,
                    selectedTabIndex = selectedTabIndex,
                    widthPx = widthPx,
                    minWidth = minWidth,
                    maxWidth = maxWidth,
                    itemSpacing = itemSpacing,
                    horizontalMargin = horizontalMargin,
                    contentAlignment = contentAlignment,
                    measureLabel = { label, selected ->
                        textMeasurer.measure(
                            text = label,
                            style = if (selected) selectedStyle else normalStyle,
                            maxLines = 1,
                        ).size.width
                    },
                )
            }
        }
        val currentGeometry by rememberUpdatedState(geometry)

        // COUISlidingTabStrip.animateIndicatorToPosition: one animator drives the indicator edges
        // and the label color crossfade. The edges lerp from where the indicator was drawn.
        val progress = remember { Animatable(1f) }
        var targetIndex by remember { mutableIntStateOf(selectedTabIndex) }
        var previousIndex by remember { mutableIntStateOf(-1) }
        var fromLeft by remember { mutableFloatStateOf(0f) }
        var fromRight by remember { mutableFloatStateOf(0f) }

        val scrollOffset by remember(geometry, resolvedListState) {
            derivedStateOf {
                val firstIndex = resolvedListState.firstVisibleItemIndex
                (geometry.slotLefts.getOrElse(firstIndex) { 0 } + resolvedListState.firstVisibleItemScrollOffset).toFloat()
            }
        }

        // Keep the selected tab centered after a layout change (COUITabLayout.onLayout).
        LaunchedEffect(widthPx, tabs, resolvedListState) {
            val target = currentGeometry.scrollTargetFor(targetIndex)
            val item = currentGeometry.itemAt(target)
            if (item >= 0) resolvedListState.scrollToItem(item, target - currentGeometry.slotLefts[item])
        }

        LaunchedEffect(selectedTabIndex) {
            val geo = currentGeometry
            if (targetIndex == selectedTabIndex || geo.isEmpty) {
                targetIndex = selectedTabIndex
                progress.snapTo(1f)
                return@LaunchedEffect
            }
            val p = progress.value
            fromLeft = lerp(fromLeft, geo.indicatorLeft(targetIndex), p)
            fromRight = lerp(fromRight, geo.indicatorRight(targetIndex), p)
            // COUITabLayout.getIndicatorAnimTime.
            val duration = min(
                INDICATOR_MAX_DURATION,
                abs(selectedTabIndex - targetIndex) * INDICATOR_DURATION_PER_TAB + INDICATOR_BASE_DURATION,
            )
            previousIndex = targetIndex
            targetIndex = selectedTabIndex
            progress.snapTo(0f)
            // COUITabLayout.animateToTab: the scroll animator runs for 300ms on the same easing.
            launch {
                val target = geo.scrollTargetFor(selectedTabIndex)
                val delta = target - scrollOffset
                if (delta != 0f) {
                    resolvedListState.animateScrollBy(delta, tween(INDICATOR_MAX_DURATION, easing = IndicatorEasing))
                }
            }
            progress.animateTo(1f, tween(duration, easing = IndicatorEasing))
            previousIndex = -1
        }

        val labelColor: (Int, Boolean) -> Color = remember(normalColor, selectedColor) {
            { index, pressed ->
                val p = progress.value
                when {
                    // coui_tab_default_color: the pressed state uses the selected color.
                    pressed -> selectedColor

                    index == targetIndex -> if (p < 1f) lerp(normalColor, selectedColor, p) else selectedColor

                    index == previousIndex -> lerp(selectedColor, normalColor, p)

                    else -> normalColor
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    compositingStrategy = if (geometry.scrollable) CompositingStrategy.Offscreen else CompositingStrategy.Auto
                }
                .drawWithContent {
                    drawContent()
                    if (geometry.isEmpty) return@drawWithContent
                    val pad = geometry.padding.toFloat()
                    // COUITabLayout.dispatchDraw: the track is fixed to the viewport, inset 24dp.
                    val trackInset = TrackHorizontalPadding.toPx()
                    val trackHeight = TrackHeight.toPx()
                    if (trackColor.alpha > 0f && size.width > 2 * trackInset) {
                        drawRect(
                            color = trackColor,
                            topLeft = Offset(trackInset, size.height - trackHeight),
                            size = Size(size.width - 2 * trackInset, trackHeight),
                        )
                    }
                    // The indicator follows the label bounds, at least 32dp wide around their
                    // center (COUISlidingTabStrip.setIndicatorPosition), clipped to the viewport.
                    val p = progress.value
                    val index = targetIndex
                    val left = lerp(fromLeft, geometry.indicatorLeft(index), p)
                    val right = lerp(fromRight, geometry.indicatorRight(index), p)
                    val halfWidth = max(right - left, IndicatorMinWidth.toPx()) / 2f
                    val center = (left + right) / 2f
                    val scroll = scrollOffset
                    var start = max(pad + center - halfWidth - scroll, pad)
                    var end = min(pad + center + halfWidth - scroll, size.width - pad)
                    if (end > start) {
                        if (layoutDirection == LayoutDirection.Rtl) {
                            val mirroredStart = size.width - end
                            end = size.width - start
                            start = mirroredStart
                        }
                        val indicatorHeight = IndicatorHeight.toPx()
                        drawRect(
                            color = indicatorColor,
                            topLeft = Offset(start, size.height - indicatorHeight),
                            size = Size(end - start, indicatorHeight),
                        )
                    }
                    // HorizontalScrollView fading edges (58dp), scaled by the remaining scroll.
                    if (geometry.scrollable) {
                        val fadeLength = FadingEdgeLength.toPx()
                        val startStrength = (scroll / fadeLength).coerceIn(0f, 1f)
                        val endStrength = ((geometry.contentWidth - scroll - geometry.viewportWidth) / fadeLength).coerceIn(0f, 1f)
                        val rtl = layoutDirection == LayoutDirection.Rtl
                        val leftStrength = if (rtl) endStrength else startStrength
                        val rightStrength = if (rtl) startStrength else endStrength
                        if (leftStrength > 0f) {
                            drawRect(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color.Transparent, Color.Black),
                                    startX = pad,
                                    endX = pad + fadeLength * leftStrength,
                                ),
                                size = Size(pad + fadeLength * leftStrength, size.height),
                                blendMode = BlendMode.DstIn,
                            )
                        }
                        if (rightStrength > 0f) {
                            val fadeStart = size.width - pad - fadeLength * rightStrength
                            drawRect(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color.Black, Color.Transparent),
                                    startX = fadeStart,
                                    endX = size.width - pad,
                                ),
                                topLeft = Offset(fadeStart, 0f),
                                size = Size(size.width - fadeStart, size.height),
                                blendMode = BlendMode.DstIn,
                            )
                        }
                    }
                },
        ) {
            LazyRow(
                state = resolvedListState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = with(density) { geometry.padding.toDp() }),
                verticalAlignment = Alignment.CenterVertically,
                userScrollEnabled = geometry.scrollable,
                overscrollEffect = null,
            ) {
                itemsIndexed(tabs) { index, label ->
                    UnderlineTab(
                        text = label,
                        isSelected = selectedTabIndex == index,
                        onClick = { currentOnTabSelected.invoke(index) },
                        width = with(density) { geometry.slotWidths[index].toDp() },
                        labelPadding = with(density) { geometry.gapHalf.toDp() },
                        labelColor = { pressed -> labelColor(index, pressed) },
                        style = if (selectedTabIndex == index) selectedStyle else normalStyle,
                        cornerRadius = cornerRadius,
                        contentAlignment = contentAlignment,
                        interactionSource = interactionSource,
                        indication = indication,
                    )
                }
            }
        }
    }
}

@Composable
private fun UnderlineTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    width: Dp,
    labelPadding: Dp,
    labelColor: (Boolean) -> Color,
    style: TextStyle,
    cornerRadius: Dp,
    contentAlignment: Alignment,
    interactionSource: MutableInteractionSource?,
    indication: Indication?,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val hapticFeedback = LocalHapticFeedback.current
    val currentIsSelected by rememberUpdatedState(isSelected)
    // COUITabView.onTouchEvent: a haptic tick when a non-selected tab is touched down.
    LaunchedEffect(interactionSource, hapticFeedback) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Press && !currentIsSelected) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(width)
            // COUITabView: COUIMaskEffectDrawable over the whole tab, 8dp corners.
            .clip(RoundedCornerShape(cornerRadius))
            .selectable(
                selected = isSelected,
                interactionSource = interactionSource,
                indication = indication,
                role = Role.Tab,
                onClick = onClick,
            )
            .padding(horizontal = labelPadding),
        contentAlignment = contentAlignment,
    ) {
        Text(
            text = text,
            color = { labelColor(isPressed && !isSelected) },
            style = style,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * The horizontal layout of a [TabRow], in pixels.
 *
 * [slotLefts] and [slotWidths] describe each tab including the half gap on both sides, relative to
 * the scrolling content that starts after [padding]. [textLefts] and [textWidths] are the label
 * bounds in the same coordinates.
 */
@Immutable
private class UnderlineTabGeometry(
    val padding: Int,
    val gapHalf: Int,
    val slotLefts: List<Int>,
    val slotWidths: List<Int>,
    val textLefts: List<Int>,
    val textWidths: List<Int>,
    val viewportWidth: Int,
) {
    val isEmpty: Boolean get() = slotWidths.isEmpty()
    val contentWidth: Int = if (slotWidths.isEmpty()) 0 else slotLefts.last() + slotWidths.last()
    val scrollable: Boolean = contentWidth > viewportWidth

    fun indicatorLeft(index: Int): Float {
        if (isEmpty) return 0f
        return textLefts[index.coerceIn(0, textLefts.lastIndex)].toFloat()
    }

    fun indicatorRight(index: Int): Float {
        if (isEmpty) return 0f
        val i = index.coerceIn(0, textLefts.lastIndex)
        return (textLefts[i] + textWidths[i]).toFloat()
    }

    /** COUITabLayout.calculateScrollXForTab: center the tab in the viewport. */
    fun scrollTargetFor(index: Int): Int {
        if (isEmpty) return 0
        val i = index.coerceIn(0, slotWidths.lastIndex)
        val target = slotLefts[i] + slotWidths[i] / 2 - viewportWidth / 2
        return target.coerceIn(0, max(0, contentWidth - viewportWidth))
    }

    fun itemAt(offset: Int): Int {
        if (isEmpty) return -1
        for (i in slotLefts.indices.reversed()) {
            if (slotLefts[i] <= offset) return i
        }
        return 0
    }
}

/**
 * COUISlidingTabStrip.onMeasure in fixed mode: measure every label (selected one in the medium
 * weight), then spread them over the 360dp content width, center them with the minimum divider, or
 * let them overflow and scroll.
 */
private fun Density.computeUnderlineTabGeometry(
    tabs: List<String>,
    selectedTabIndex: Int,
    widthPx: Int,
    minWidth: Dp,
    maxWidth: Dp,
    itemSpacing: Dp,
    horizontalMargin: Dp,
    contentAlignment: Alignment,
    measureLabel: (String, Boolean) -> Int,
): UnderlineTabGeometry {
    val count = tabs.size
    if (count == 0) {
        return UnderlineTabGeometry(0, 0, emptyList(), emptyList(), emptyList(), emptyList(), 0)
    }
    val width = if (widthPx == Constraints.Infinity) 0 else widthPx
    val widthDp = width.toDp()
    val minDivider = if (itemSpacing.isSpecified) {
        itemSpacing.roundToPx()
    } else if (widthDp >= MediumWidthBreakpoint) {
        MediumTabSpacing.roundToPx()
    } else {
        SmallTabSpacing.roundToPx()
    }
    val minMargin = when {
        horizontalMargin.isSpecified -> horizontalMargin.roundToPx()
        widthDp >= LargeWidthBreakpoint -> LargeHorizontalMargin.roundToPx()
        widthDp >= MediumWidthBreakpoint -> MediumHorizontalMargin.roundToPx()
        else -> SmallHorizontalMargin.roundToPx()
    }
    // COUITabLayout.onMeasure: without couiTabMaxWidth a tab is at most 70% of the row.
    val maxTabPx = when {
        maxWidth == Dp.Unspecified -> (width * DEFAULT_TAB_MAX_WIDTH_RATIO).toInt()
        maxWidth == Dp.Infinity -> Int.MAX_VALUE
        else -> maxWidth.roundToPx()
    }
    val minTabPx = if (minWidth.isSpecified) minWidth.roundToPx() else 0
    val textWidths = tabs.mapIndexed { index, label -> min(measureLabel(label, index == selectedTabIndex), maxTabPx) }
    val tabWidths = textWidths.map { min(max(it, minTabPx), maxTabPx) }
    val sum = tabWidths.sum()
    val contentMinWidth = ContentMinWidth.roundToPx()
    val total = minMargin * 2 + sum + max(0, count - 1) * minDivider
    val gap: Int
    val padding: Int
    when {
        // measureSmallChild: equal gaps across the 360dp content width.
        total <= contentMinWidth -> {
            if (width >= contentMinWidth) {
                gap = max((contentMinWidth - sum) / (count + 1), minDivider)
                padding = (width - contentMinWidth + gap) / 2
            } else {
                gap = max((width - sum) / (count + 1), minDivider)
                padding = gap / 2
            }
        }

        // measureShortChild: centered with the minimum divider.
        total <= width -> {
            gap = minDivider
            padding = (width - sum - minDivider * count) / 2
        }

        // Overflow: scrolls, with the margin made up of padding plus half a divider.
        else -> {
            gap = minDivider
            padding = minMargin - minDivider / 2
        }
    }
    val gapHalf = gap / 2
    val resolvedPadding = max(0, padding)
    val slotLefts = ArrayList<Int>(count)
    val slotWidths = ArrayList<Int>(count)
    val textLefts = ArrayList<Int>(count)
    var left = 0
    for (i in 0 until count) {
        val slotWidth = tabWidths[i] + gapHalf * 2
        slotLefts.add(left)
        slotWidths.add(slotWidth)
        val alignOffset = contentAlignment.align(
            IntSize(textWidths[i], 0),
            IntSize(tabWidths[i], 0),
            LayoutDirection.Ltr,
        ).x
        textLefts.add(left + gapHalf + alignOffset)
        left += slotWidth
    }
    return UnderlineTabGeometry(
        padding = resolvedPadding,
        gapHalf = gapHalf,
        slotLefts = slotLefts,
        slotWidths = slotWidths,
        textLefts = textLefts,
        textWidths = textWidths,
        viewportWidth = max(0, width - resolvedPadding * 2),
    )
}

/**
 * A [TabRowWithContour] with COUI style.
 *
 * A port of the ColorOS 16 segmented control (COUISegmentButtonLayout, default `SegmentButton`
 * style). For the `SegmentButton.Tiny` style, pass [TabRowDefaults.TabRowWithContourTinyHeight],
 * [TabRowDefaults.TabRowWithContourTinyCornerRadius] and
 * [TabRowDefaults.TabRowWithContourTinyPadding].
 *
 * @param tabs The text to be displayed in the [TabRow].
 * @param selectedTabIndex The selected tab index of the [TabRow].
 * @param onTabSelected The callback when a tab is selected.
 * @param modifier The modifier to be applied to the [TabRow].
 * @param colors The colors of the [TabRow].
 * @param minWidth The minimum width of the tab in [TabRow].
 * @param maxWidth The maximum width of the tab in [TabRow].
 * @param height The height of the [TabRow].
 * @param cornerRadius The corner radius of the sliding indicator in [TabRow].
 * @param contourPadding The inset between the container capsule and the sliding indicator.
 * @param itemSpacing The spacing between tabs in [TabRow].
 * @param contentAlignment The content alignment of the tab in [TabRow].
 * @param listState The [LazyListState] to be used for the [TabRow].
 * @param interactionSource The [MutableInteractionSource] to be used for the [TabRow].
 * @param indication The [Indication] to be used for the [TabRow].
 */
@Composable
@NonRestartableComposable
fun TabRowWithContour(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: TabRowColors = TabRowDefaults.tabRowColors(),
    minWidth: Dp = TabRowDefaults.TabRowWithContourMinWidth,
    maxWidth: Dp = TabRowDefaults.TabRowWithContourMaxWidth,
    height: Dp = TabRowDefaults.TabRowWithContourHeight,
    cornerRadius: Dp = TabRowDefaults.TabRowWithContourCornerRadius,
    contourPadding: Dp = TabRowDefaults.TabRowWithContourPadding,
    itemSpacing: Dp = 0.dp,
    contentAlignment: Alignment = Alignment.Center,
    listState: LazyListState? = null,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
) {
    SegmentButtonRow(
        tabs = tabs,
        selectedTabIndex = selectedTabIndex,
        onTabSelected = onTabSelected,
        modifier = modifier,
        colors = colors,
        minWidth = minWidth,
        maxWidth = maxWidth,
        height = height,
        cornerRadius = cornerRadius,
        contourPadding = contourPadding,
        itemSpacing = itemSpacing,
        contentAlignment = contentAlignment,
        listState = listState,
        interactionSource = interactionSource,
        indication = indication,
    )
}

/**
 * Shared implementation of the COUI segment button, following COUISegmentButtonLayout.onDraw.
 */
@Composable
private fun SegmentButtonRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    colors: TabRowColors,
    minWidth: Dp,
    maxWidth: Dp,
    height: Dp,
    cornerRadius: Dp,
    contourPadding: Dp,
    itemSpacing: Dp,
    contentAlignment: Alignment,
    listState: LazyListState?,
    interactionSource: MutableInteractionSource?,
    indication: Indication?,
    modifier: Modifier = Modifier,
) {
    val currentOnTabSelected by rememberUpdatedState(onTabSelected)
    val isDarkTheme = COUITheme.colorScheme.background.luminance() < 0.5f
    val shadowColor = if (isDarkTheme) IndicatorShadowDark else IndicatorShadowLight
    val shadowPenumbraColor = remember(shadowColor) { shadowColor.copy(alpha = shadowColor.alpha * 0.5f) }
    val containerColor = colors.backgroundColor(false)
    val indicatorColor = colors.backgroundColor(true)
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val labelFontSize = COUITheme.textStyles.body2.fontSize
    // COUI measures segments with the selected appearance (couiTextButtonM: 14sp, medium).
    val measureStyle = remember(labelFontSize) { TextStyle(fontSize = labelFontSize, fontWeight = FontWeight.Medium) }
    val resolvedListState = listState ?: rememberLazyListState()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
            .height(height),
    ) {
        val viewportPx = with(density) { (this@BoxWithConstraints.maxWidth - contourPadding * 2).toPx() }
        val minWidthPx = with(density) { minWidth.roundToPx() }
        val maxWidthPx = if (maxWidth == Dp.Infinity) Int.MAX_VALUE else with(density) { maxWidth.roundToPx() }
        val spacingPx = with(density) { itemSpacing.roundToPx() }
        val segmentPaddingPx = with(density) { SegmentPaddingHorizontal.roundToPx() }

        // COUISegmentButtonLayout.calculateChildrenWidths: natural label widths distributed to fill the row.
        val segmentWidthsPx = remember(
            tabs,
            viewportPx,
            minWidthPx,
            maxWidthPx,
            spacingPx,
            segmentPaddingPx,
            textMeasurer,
            measureStyle,
        ) {
            val naturalWidths = tabs.map { label ->
                max(
                    textMeasurer.measure(text = label, style = measureStyle, maxLines = 1).size.width +
                        2 * segmentPaddingPx,
                    minWidthPx,
                )
            }
            val availablePx = viewportPx - spacingPx * max(0, tabs.size - 1)
            computeSegmentWidths(naturalWidths, availablePx, minWidthPx, maxWidthPx)
        }
        val segmentLeftsPx = remember(segmentWidthsPx, spacingPx) {
            buildList {
                var left = 0
                for (widthPx in segmentWidthsPx) {
                    add(left)
                    left += widthPx + spacingPx
                }
            }
        }

        // Initialized from the current selection so the first frame shows the indicator at rest.
        val indicatorCenterX = remember {
            val index = selectedTabIndex.coerceIn(0, max(0, segmentWidthsPx.lastIndex))
            Animatable(
                if (segmentWidthsPx.isEmpty()) 0f else segmentLeftsPx[index] + segmentWidthsPx[index] / 2f,
            )
        }
        val indicatorWidth = remember {
            val index = selectedTabIndex.coerceIn(0, max(0, segmentWidthsPx.lastIndex))
            Animatable(if (segmentWidthsPx.isEmpty()) 0f else segmentWidthsPx[index].toFloat())
        }
        val indicatorScale = remember { Animatable(1f) }
        var selectedSegmentPressed by remember { mutableStateOf(false) }
        var lastSettledSelectedTabIndex by remember(resolvedListState) { mutableIntStateOf(-1) }

        // COUI animateIndicatorToPosition: independent position and width springs.
        LaunchedEffect(selectedTabIndex, segmentWidthsPx, segmentLeftsPx) {
            if (segmentWidthsPx.isEmpty()) return@LaunchedEffect
            val index = selectedTabIndex.coerceIn(0, segmentWidthsPx.lastIndex)
            val targetWidth = segmentWidthsPx[index].toFloat()
            val targetCenter = segmentLeftsPx[index] + targetWidth / 2f
            if (lastSettledSelectedTabIndex < 0 || lastSettledSelectedTabIndex == selectedTabIndex) {
                indicatorCenterX.snapTo(targetCenter)
                indicatorWidth.snapTo(targetWidth)
            } else {
                launch { indicatorCenterX.animateTo(targetCenter, SegmentButtonSpring) }
                launch { indicatorWidth.animateTo(targetWidth, SegmentButtonSpring) }
            }
        }

        // Keep the selected tab visible when the row overflows and scrolls.
        LaunchedEffect(selectedTabIndex, viewportPx, resolvedListState, segmentWidthsPx) {
            if (segmentWidthsPx.isNotEmpty()) {
                val index = selectedTabIndex.coerceIn(0, segmentWidthsPx.lastIndex)
                val offsetPx = -((viewportPx - segmentWidthsPx[index]) / 2f).roundToInt()
                if (lastSettledSelectedTabIndex < 0 || lastSettledSelectedTabIndex == selectedTabIndex) {
                    resolvedListState.scrollToItem(index, offsetPx)
                } else {
                    resolvedListState.animateScrollToItem(index, offsetPx)
                }
            }
            lastSettledSelectedTabIndex = selectedTabIndex
        }

        // COUIPressFeedbackHelper: pressing the selected segment scales the indicator around its center.
        LaunchedEffect(selectedSegmentPressed, selectedTabIndex, segmentWidthsPx, height, contourPadding) {
            val target = if (selectedSegmentPressed && segmentWidthsPx.isNotEmpty()) {
                val index = selectedTabIndex.coerceIn(0, segmentWidthsPx.lastIndex)
                with(density) {
                    pressedScaleEndRatio(
                        Size(segmentWidthsPx[index].toFloat(), (height - contourPadding * 2).toPx()),
                    )
                }
            } else {
                1f
            }
            indicatorScale.animateTo(target, SegmentButtonSpring)
        }

        val scrollOffset by remember(segmentLeftsPx, resolvedListState) {
            derivedStateOf {
                val firstIndex = resolvedListState.firstVisibleItemIndex
                (segmentLeftsPx.getOrElse(firstIndex) { 0 } + resolvedListState.firstVisibleItemScrollOffset).toFloat()
            }
        }
        val onSelectedPressedChange: (Boolean) -> Unit = remember { { pressed -> selectedSegmentPressed = pressed } }
        val indicatorPath = remember { Path() }
        val squircleEnabled = isSquircleEnabled()

        // COUISegmentButtonLayout draws the container and the indicator as smooth round rects of
        // radius height / 2 without a weight (updateSmoothRoundPath): the COUI 17 soft capsule, or
        // default_g2_weight for a radius below half the height.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    // 1. Container capsule (COUISegmentButtonLayout.drawButtonBackground).
                    val containerRadius = min((cornerRadius + contourPadding).toPx(), size.height / 2f)
                    val containerPath = Path().apply {
                        addSquircleRect(
                            width = size.width,
                            height = size.height,
                            cornerRadius = containerRadius,
                            weight = COUIRoundCorner.UnspecifiedWeight,
                            squircleEnabled = squircleEnabled,
                        )
                    }
                    onDrawBehind {
                        if (containerColor.alpha > 0f) {
                            drawPath(path = containerPath, color = containerColor)
                        }
                        if (segmentWidthsPx.isEmpty()) return@onDrawBehind
                        val pad = contourPadding.toPx()
                        val scale = indicatorScale.value
                        val indicatorW = indicatorWidth.value * scale
                        val indicatorH = (size.height - 2f * pad) * scale
                        if (indicatorW <= 0f || indicatorH <= 0f) return@onDrawBehind
                        val centerX = pad + indicatorCenterX.value - scrollOffset
                        val centerY = size.height / 2f
                        val rect = Rect(
                            centerX - indicatorW / 2f,
                            centerY - indicatorH / 2f,
                            centerX + indicatorW / 2f,
                            centerY + indicatorH / 2f,
                        )
                        val radius = min(cornerRadius.toPx(), indicatorH / 2f)
                        indicatorPath.rewind()
                        indicatorPath.addSquircleRect(
                            rect = rect,
                            cornerRadius = radius,
                            weight = COUIRoundCorner.UnspecifiedWeight,
                            squircleEnabled = squircleEnabled,
                        )
                        clipPath(containerPath) {
                            // 2. Indicator shadow, clipped outside the indicator (Region.Op.DIFFERENCE);
                            // the 1dp blur is approximated with a half-alpha penumbra pass.
                            clipPath(indicatorPath, clipOp = ClipOp.Difference) {
                                val shadowDy = IndicatorShadowOffsetY.toPx()
                                val shadowBlur = IndicatorShadowBlurRadius.toPx()
                                drawRoundRect(
                                    color = shadowPenumbraColor,
                                    topLeft = Offset(rect.left - shadowBlur, rect.top + shadowDy - shadowBlur),
                                    size = Size(indicatorW + 2 * shadowBlur, indicatorH + 2 * shadowBlur),
                                    cornerRadius = CornerRadius(radius + shadowBlur),
                                )
                                translate(top = shadowDy) {
                                    drawPath(path = indicatorPath, color = shadowColor)
                                }
                            }
                            // 3. Indicator capsule fill.
                            drawPath(path = indicatorPath, color = indicatorColor)
                        }
                    }
                },
        ) {
            // 4. Labels, composed on top of the canvas.
            LazyRow(
                state = resolvedListState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contourPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(itemSpacing),
                overscrollEffect = null,
            ) {
                itemsIndexed(tabs) { index, label ->
                    SegmentTab(
                        text = label,
                        isSelected = selectedTabIndex == index,
                        onClick = { currentOnTabSelected.invoke(index) },
                        width = with(density) { segmentWidthsPx[index].toDp() },
                        color = colors.contentColor(selectedTabIndex == index),
                        fontSize = labelFontSize,
                        contentAlignment = contentAlignment,
                        interactionSource = interactionSource,
                        indication = indication,
                        onSelectedPressedChange = onSelectedPressedChange,
                    )
                }
            }
        }
    }
}

@Composable
private fun SegmentTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    width: Dp,
    color: Color,
    fontSize: TextUnit,
    contentAlignment: Alignment,
    interactionSource: MutableInteractionSource?,
    indication: Indication?,
    onSelectedPressedChange: (Boolean) -> Unit,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val currentOnSelectedPressedChange by rememberUpdatedState(onSelectedPressedChange)
    LaunchedEffect(isPressed, isSelected) {
        currentOnSelectedPressedChange(isPressed && isSelected)
    }
    // COUIPressFeedbackHelper on the segment label.
    val scaleProgress by animateFloatAsState(
        targetValue = if (isPressed) 1f else 0f,
        animationSpec = SegmentButtonSpring,
        label = "segmentPressScale",
    )
    // COUISegmentButtonLayout.setSelectedAt: color crossfades over 300ms, weight switches instantly.
    val textColor by animateColorAsState(
        targetValue = color,
        animationSpec = tween(TextColorAnimationDuration, easing = TextColorEasing),
        label = "segmentTextColor",
    )
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(width)
            .graphicsLayer {
                val scale = 1f - (1f - pressedScaleEndRatio(size)) * scaleProgress
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = indication,
                onClick = onClick,
            )
            .semantics {
                role = Role.Tab
                selected = isSelected
            }
            .padding(horizontal = SegmentPaddingHorizontal),
        contentAlignment = contentAlignment,
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            fontSize = fontSize,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * Resolve the widths of all segments (COUISegmentButtonLayout.onMeasure).
 *
 * When the row cannot give every segment [minWidthPx] the segments keep their natural widths and
 * the row scrolls instead.
 */
private fun computeSegmentWidths(
    naturalWidths: List<Int>,
    availablePx: Float,
    minWidthPx: Int,
    maxWidthPx: Int,
): List<Int> {
    val count = naturalWidths.size
    if (count == 0) return emptyList()
    if (availablePx <= 0f || availablePx / count < minWidthPx) {
        return naturalWidths.map { min(it, maxWidthPx) }
    }
    return distributeSegmentWidths(naturalWidths, availablePx).map { min(it, maxWidthPx) }
}

/**
 * Distribute the available width among segments (COUISegmentButtonLayout.calculateChildrenWidths):
 * narrow segments keep their natural width and the remaining space is split among the wider ones.
 */
private fun distributeSegmentWidths(
    naturalWidths: List<Int>,
    availablePx: Float,
): List<Int> {
    val count = naturalWidths.size
    val sortedIndices = naturalWidths.indices.sortedBy { naturalWidths[it] }
    val fairShare = availablePx / count
    val widths = FloatArray(count)
    var remaining = availablePx
    var share = fairShare
    var lockedIndex = -1
    var i = 0
    while (i < count) {
        val natural = naturalWidths[sortedIndices[i]].toFloat()
        if (natural < share) {
            widths[sortedIndices[i]] = natural
            remaining -= natural
            i++
        } else if (lockedIndex == i) {
            while (i < count) {
                widths[sortedIndices[i]] = share
                i++
            }
            remaining = 0f
        } else {
            share = remaining / (count - i)
            lockedIndex = i
        }
    }
    if (lockedIndex == -1) {
        return List(count) { fairShare.roundToInt() }
    }
    if (remaining > 0f) {
        for (j in 0 until count - 1) {
            widths[sortedIndices[j]] += remaining / (count - 1)
        }
    }
    return List(count) { widths[it].roundToInt() }
}

object TabRowDefaults {

    /**
     * The default height of the [TabRow]. `COUITabLayout` measures 48dp for text-only tabs.
     */
    val TabRowHeight = 48.dp

    /**
     * The default height of the [TabRowWithContour]. COUI `SegmentButton` style height (40dp).
     */
    val TabRowWithContourHeight = 40.dp

    /**
     * The height of the COUI `SegmentButton.Tiny` style (32dp), for use as [TabRowWithContour]'s
     * `height` together with [TabRowWithContourTinyPadding] and [TabRowWithContourTinyCornerRadius].
     */
    val TabRowWithContourTinyHeight = 32.dp

    /**
     * The default corner radius of the press mask of a tab in [TabRow]
     * (`COUITabView`'s `COUIMaskEffectDrawable`, 8dp).
     */
    val TabRowCornerRadius = 8.dp

    /**
     * The default corner radius of the sliding indicator in [TabRowWithContour]. Half the 32dp
     * indicator height (40dp container minus the 4dp inset on both sides).
     */
    val TabRowWithContourCornerRadius = 16.dp

    /**
     * The indicator corner radius of the COUI `SegmentButton.Tiny` style: (32dp - 2 * 2dp) / 2.
     */
    val TabRowWithContourTinyCornerRadius = 14.dp

    /**
     * The default inset between the container and the indicator in [TabRowWithContour].
     * COUI `SegmentButton` style `android:padding` (4dp).
     */
    val TabRowWithContourPadding = 4.dp

    /**
     * The contour inset of the COUI `SegmentButton.Tiny` style (`android:padding` 2dp).
     */
    val TabRowWithContourTinyPadding = 2.dp

    /**
     * The default minimum width of the label slot of a tab in [TabRow]. `COUITabLayout` has no
     * minimum, the indicator alone is at least 32dp wide.
     */
    val TabRowMinWidth = 0.dp

    /**
     * The default minimum width of the tab in [TabRowWithContour].
     * COUI `coui_segment_min_width` (52dp).
     */
    val TabRowWithContourMinWidth = 52.dp

    /**
     * The default maximum width of the label slot of a tab in [TabRow]. [Dp.Unspecified] means 70%
     * of the row width, which `COUITabLayout.onMeasure` uses when no maximum is requested.
     */
    val TabRowMaxWidth = Dp.Unspecified

    /**
     * The default maximum width of the tab in [TabRowWithContour]. COUI segments have no maximum width.
     */
    val TabRowWithContourMaxWidth = Dp.Infinity

    /**
     * The default colors for the [TabRow] (`COUITabLayoutBaseStyle`).
     *
     * @param trackColor The 2dp track under all tabs (`tablayout_indicator_background`).
     * @param indicatorColor The 2dp indicator under the selected tab (`couiColorPrimaryNeutral`).
     * @param contentColor The label color of unselected tabs (`couiColorSecondNeutral`).
     * @param selectedContentColor The label color of the selected and pressed tab
     *   (`couiColorPrimaryNeutral`).
     */
    @Composable
    fun underlineColors(
        trackColor: Color = if (COUITheme.colorScheme.background.luminance() < 0.5f) {
            TabTrackDark
        } else {
            TabTrackLight
        },
        indicatorColor: Color = COUITheme.colorScheme.onSurface,
        contentColor: Color = COUITheme.colorScheme.onSurfaceSecondary,
        selectedContentColor: Color = COUITheme.colorScheme.onSurface,
    ): TabRowColors = remember(trackColor, indicatorColor, contentColor, selectedContentColor) {
        TabRowColors(
            backgroundColor = trackColor,
            contentColor = contentColor,
            selectedBackgroundColor = indicatorColor,
            selectedContentColor = selectedContentColor,
        )
    }

    /**
     * The default colors for the [TabRowWithContour], following
     * COUISegmentButtonLayout: both label states default to `couiColorLabelPrimary` — the
     * selected state is distinguished by the indicator and the medium font weight.
     */
    @Composable
    fun tabRowColors(
        backgroundColor: Color = if (COUITheme.colorScheme.background.luminance() < 0.5f) {
            SegmentContainerDark
        } else {
            SegmentContainerLight
        },
        contentColor: Color = COUITheme.colorScheme.onSurface,
        selectedBackgroundColor: Color = if (COUITheme.colorScheme.background.luminance() < 0.5f) {
            SegmentIndicatorDark
        } else {
            SegmentIndicatorLight
        },
        selectedContentColor: Color = COUITheme.colorScheme.onSurface,
    ): TabRowColors = remember(backgroundColor, contentColor, selectedBackgroundColor, selectedContentColor) {
        TabRowColors(
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            selectedBackgroundColor = selectedBackgroundColor,
            selectedContentColor = selectedContentColor,
        )
    }
}

@Immutable
data class TabRowColors(
    private val backgroundColor: Color,
    private val contentColor: Color,
    private val selectedBackgroundColor: Color,
    private val selectedContentColor: Color,
) {
    @Stable
    internal fun backgroundColor(selected: Boolean): Color = if (selected) selectedBackgroundColor else backgroundColor

    @Stable
    internal fun contentColor(selected: Boolean): Color = if (selected) selectedContentColor else contentColor
}

/** `tablayout_indicator_background` in light themes (#0F000000). */
private val TabTrackLight = Color(0x0F000000)

/** `tablayout_indicator_background` in dark themes (#19FFFFFF). */
private val TabTrackDark = Color(0x19FFFFFF)

/** `tablayout_selected_indicator_height` (2dp), used for both the indicator and the track. */
private val IndicatorHeight = 2.dp

/** `couiTabIndicatorBackgroundHeight` (2dp). */
private val TrackHeight = 2.dp

/** `tablayout_background_padding` (24dp): the track inset on both sides. */
private val TrackHorizontalPadding = 24.dp

/** COUISlidingTabStrip.setIndicatorPosition keeps the indicator at least 32dp wide. */
private val IndicatorMinWidth = 32.dp

/** `tablayout_fade_edge_length` (58dp). */
private val FadingEdgeLength = 58.dp

/** `coui_tab_layout_content_min_width` (360dp). */
private val ContentMinWidth = 360.dp

/** `coui_tab_layout_small_tab_spacing` (20dp). */
private val SmallTabSpacing = 20.dp

/** `coui_tab_layout_medium_tab_spacing` (24dp). */
private val MediumTabSpacing = 24.dp

/** `coui_tab_layout_small_horizontal_margin` (16dp). */
private val SmallHorizontalMargin = 16.dp

/** `coui_tab_layout_medium_horizontal_margin` (24dp). */
private val MediumHorizontalMargin = 24.dp

/** `coui_tab_layout_large_horizontal_margin` (40dp). */
private val LargeHorizontalMargin = 40.dp

/** Window size class breakpoints, applied to the row width. */
private val MediumWidthBreakpoint = 600.dp
private val LargeWidthBreakpoint = 840.dp

/** COUITabLayout.onMeasure: the tab maximum width without `setRequestedTabMaxWidth`. */
private const val DEFAULT_TAB_MAX_WIDTH_RATIO = 0.7f

/** COUIEaseInterpolator: PathInterpolator(0.33f, 0f, 0.67f, 1f). */
private val IndicatorEasing = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

/** COUITabLayout.getIndicatorAnimTime: min(300, |delta| * 50 + 150) ms. */
private const val INDICATOR_MAX_DURATION = 300
private const val INDICATOR_DURATION_PER_TAB = 50
private const val INDICATOR_BASE_DURATION = 150

/**
 * COUISpringForce(response = 0.3f, bounce = 0f): a critically damped spring with stiffness
 * (2 * PI / 0.3)^2 ~= 438.65, used for the indicator springs and the press scale.
 */
private val SegmentButtonSpring = spring<Float>(dampingRatio = 1f, stiffness = 438.65f)

/** COUIMoveEaseInterpolator: PathInterpolator(0.3f, 0f, 0.1f, 1f). */
private val TextColorEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

/** COUISegmentButtonLayout text color animator duration (COLOR_ANIMATOR_TIME = 300ms). */
private val TextColorAnimationDuration = 300

/** COUI `coui_segment_btn_padding_horizontal` (12dp) — horizontal text padding of a segment. */
private val SegmentPaddingHorizontal = 12.dp

/** COUI `coui_segment_shadow_offset` (2dp) — the vertical offset of the indicator shadow. */
private val IndicatorShadowOffsetY = 2.dp

/** COUI `coui_segment_shadow_radius` (1dp) — the blur radius of the indicator shadow. */
private val IndicatorShadowBlurRadius = 1.dp

/** COUI `coui_segment_button_shadow_color` in light themes (#0D000000). */
private val IndicatorShadowLight = Color(0x0D000000)

/** COUI 17 `coui_segment_button_shadow_color` in dark themes (#14000000). */
private val IndicatorShadowDark = Color(0x14000000)

/** COUI `coui_color_segment_button_background` in light themes (#0F000000). */
private val SegmentContainerLight = Color(0x0F000000)

/** COUI `coui_color_segment_button_background` in dark themes (#1FFFFFFF). */
private val SegmentContainerDark = Color(0x1FFFFFFF)

/** COUI 17 `coui_color_segment_button_indicator` in light themes (#B2FFFFFF). */
private val SegmentIndicatorLight = Color(0xB2FFFFFF)

/** COUI 17 `coui_color_segment_button_indicator` in dark themes (#33FFFFFF). */
private val SegmentIndicatorDark = Color(0x33FFFFFF)

/** COUIInEaseInterpolator, used to map the segment area to the pressed end scale. */
private val PressedScaleEasing = CubicBezierEasing(0f, 0f, 0.1f, 1f)

/** COUIPressFeedbackHelper area bounds: coui_min_end_value_size (48dp) squared. */
private val PressedScaleMinAreaDp2 = 48f * 48f

/** COUIPressFeedbackHelper area bounds: coui_max_end_value_width x height (328dp x 220dp). */
private val PressedScaleMaxAreaDp2 = 328f * 220f

/** The pressed end scale of the smallest surfaces (COUIPressFeedbackHelper DEFAULT_SCALE_END_RATIO). */
private val PressedScaleMin = 0.92f

/** The pressed end scale of the largest surfaces (COUIPressFeedbackHelper MAX_SCALE_END_RATIO). */
private val PressedScaleMax = 0.98f

/**
 * The scale a segment of [size] shrinks to when fully pressed: [PressedScaleMin] for areas up to
 * 48 x 48dp, easing towards [PressedScaleMax] at 328 x 220dp
 * (COUIPressFeedbackHelper.getCardScaleRatio).
 */
private fun Density.pressedScaleEndRatio(size: Size): Float {
    val area = size.width.toDp().value * size.height.toDp().value
    return when {
        area <= PressedScaleMinAreaDp2 -> PressedScaleMin

        area >= PressedScaleMaxAreaDp2 -> PressedScaleMax

        else -> PressedScaleMin + (PressedScaleMax - PressedScaleMin) *
            PressedScaleEasing.transform((area - PressedScaleMinAreaDp2) / (PressedScaleMaxAreaDp2 - PressedScaleMinAreaDp2))
    }
}
