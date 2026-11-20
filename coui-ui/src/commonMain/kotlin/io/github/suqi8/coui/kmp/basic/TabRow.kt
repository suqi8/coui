// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.DragInteraction
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChangedIgnoreConsumed
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.util.lerp
import io.github.suqi8.coui.kmp.effect.couiPressScale
import io.github.suqi8.coui.kmp.squircle.addSquircleRect
import io.github.suqi8.coui.kmp.squircle.isSquircleEnabled
import io.github.suqi8.coui.kmp.theme.COUIRoundCorner
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.utils.COUILayoutGrid
import io.github.suqi8.coui.kmp.utils.COUIPhysicalAnimation
import io.github.suqi8.coui.kmp.utils.COUIReboundOverScroller
import io.github.suqi8.coui.kmp.utils.CouiHapticEffect
import io.github.suqi8.coui.kmp.utils.rememberCouiHaptic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.jvm.JvmName
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * A tab of a [TabRow].
 *
 * @param text The label of the tab. It may be empty for an icon-only tab.
 * @param icon An optional 24dp icon shown before the label (`COUITab.setIcon`). A row with both an
 *   icon and a label grows to 72dp.
 * @param badge An optional red dot after the label (`COUITab` hint red dot): `null` hides it, `0`
 *   shows a plain dot and a positive value shows that number, see [Badge].
 * @param enabled Whether the tab can be selected (`COUITabLayout.enableTab`).
 */
@Immutable
data class TabRowItem(
    val text: String,
    val icon: ImageVector? = null,
    val badge: Int? = null,
    val enabled: Boolean = true,
)

/**
 * A [TabRow] with COUI style.
 *
 * A port of the ColorOS sliding tab layout (`COUITabLayout` + `COUISlidingTabStrip`): text tabs over
 * a 2dp track, with a 2dp neutral indicator as wide as the selected label (at least 32dp) that
 * slides between tabs. The selected label switches to the medium weight instantly while the label
 * colors crossfade with the indicator.
 *
 * Tabs are laid out like `COUISlidingTabStrip.onMeasure`: when the labels fit into the 360dp
 * content width they are spread with equal gaps, when they fit into the row they are centered with
 * [itemSpacing] between them, and otherwise the row scrolls with fading edges and the
 * `COUIHorizontalScrollView` rubber-band overscroll.
 *
 * A tab reacts on touch down (`COUITabView.onTouchEvent`), even inside a scrolling page: its press
 * mask starts at once and touching a tab that is not selected vibrates. The row itself stays
 * silent, as `COUITabLayoutBaseStyle` turns the layout's own haptics off: no tick when a pager
 * swipe changes the tab and none at the scroll edges.
 *
 * @param tabs The text to be displayed in the [TabRow].
 * @param selectedTabIndex The selected tab index of the [TabRow].
 * @param onTabSelected The callback when a tab is selected.
 * @param modifier The modifier to be applied to the [TabRow].
 * @param enabled Whether the tabs can be selected; a disabled row greys out its labels and
 *   indicator (`COUITabLayout.setEnabled`).
 * @param colors The colors of the [TabRow], see [TabRowDefaults.underlineColors].
 * @param minWidth The minimum width of the content of a tab.
 * @param maxWidth The maximum width of the content of a tab; longer labels are ellipsized.
 *   [Dp.Unspecified] uses 70% of the row width like COUI, [Dp.Infinity] removes the limit.
 * @param height The height of the [TabRow]. [Dp.Unspecified] uses the COUI height: 48dp, or 72dp
 *   when a tab has both an icon and a label.
 * @param cornerRadius The corner radius of the press mask of a tab.
 * @param itemSpacing The minimum spacing between two tabs. [Dp.Unspecified] uses the COUI
 *   responsive value (20dp on small screens, 24dp otherwise).
 * @param horizontalMargin The minimum margin before the first and after the last tab.
 *   [Dp.Unspecified] uses the COUI responsive value (16dp, 24dp or 40dp by screen size).
 * @param contentAlignment The alignment of the tab content in its tab.
 * @param listState The [LazyListState] to be used for the [TabRow].
 * @param interactionSource The [MutableInteractionSource] to be used for the [TabRow].
 * @param indication The press mask drawn over a tab, clipped to [cornerRadius].
 * @param pagerState An optional [PagerState] the row follows while the user drags the pager: the
 *   indicator, the label colors and the scroll position track the page offset like
 *   `COUITabLayout.setScrollPosition`.
 */
@Composable
fun TabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: TabRowColors = TabRowDefaults.underlineColors(),
    minWidth: Dp = TabRowDefaults.TabRowMinWidth,
    maxWidth: Dp = TabRowDefaults.TabRowMaxWidth,
    height: Dp = Dp.Unspecified,
    cornerRadius: Dp = TabRowDefaults.TabRowCornerRadius,
    itemSpacing: Dp = Dp.Unspecified,
    horizontalMargin: Dp = Dp.Unspecified,
    contentAlignment: Alignment = Alignment.Center,
    listState: LazyListState? = null,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = LocalIndication.current,
    pagerState: PagerState? = null,
) {
    val items = remember(tabs) { tabs.map { TabRowItem(text = it) } }
    TabRow(
        items = items,
        selectedTabIndex = selectedTabIndex,
        onTabSelected = onTabSelected,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        minWidth = minWidth,
        maxWidth = maxWidth,
        height = height,
        cornerRadius = cornerRadius,
        itemSpacing = itemSpacing,
        horizontalMargin = horizontalMargin,
        contentAlignment = contentAlignment,
        listState = listState,
        interactionSource = interactionSource,
        indication = indication,
        pagerState = pagerState,
    )
}

/**
 * A [TabRow] with COUI style whose tabs may carry an icon, a red dot badge or be disabled.
 *
 * See the [TabRow] overload taking `tabs` for the layout and the motion.
 *
 * @param items The tabs to be displayed in the [TabRow].
 * @param selectedTabIndex The selected tab index of the [TabRow].
 * @param onTabSelected The callback when a tab is selected.
 * @param modifier The modifier to be applied to the [TabRow].
 * @param enabled Whether the tabs can be selected; a disabled row greys out its labels and
 *   indicator (`COUITabLayout.setEnabled`).
 * @param colors The colors of the [TabRow], see [TabRowDefaults.underlineColors].
 * @param minWidth The minimum width of the content of a tab.
 * @param maxWidth The maximum width of the content of a tab; longer labels are ellipsized.
 *   [Dp.Unspecified] uses 70% of the row width like COUI, [Dp.Infinity] removes the limit.
 * @param height The height of the [TabRow]. [Dp.Unspecified] uses the COUI height: 48dp, or 72dp
 *   when a tab has both an icon and a label.
 * @param cornerRadius The corner radius of the press mask of a tab.
 * @param itemSpacing The minimum spacing between two tabs. [Dp.Unspecified] uses the COUI
 *   responsive value (20dp on small screens, 24dp otherwise).
 * @param horizontalMargin The minimum margin before the first and after the last tab.
 *   [Dp.Unspecified] uses the COUI responsive value (16dp, 24dp or 40dp by screen size).
 * @param contentAlignment The alignment of the tab content in its tab.
 * @param listState The [LazyListState] to be used for the [TabRow].
 * @param interactionSource The [MutableInteractionSource] to be used for the [TabRow].
 * @param indication The press mask drawn over a tab, clipped to [cornerRadius].
 * @param pagerState An optional [PagerState] the row follows while the user drags the pager.
 */
@Composable
@JvmName("TabRowItems")
fun TabRow(
    items: List<TabRowItem>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: TabRowColors = TabRowDefaults.underlineColors(),
    minWidth: Dp = TabRowDefaults.TabRowMinWidth,
    maxWidth: Dp = TabRowDefaults.TabRowMaxWidth,
    height: Dp = Dp.Unspecified,
    cornerRadius: Dp = TabRowDefaults.TabRowCornerRadius,
    itemSpacing: Dp = Dp.Unspecified,
    horizontalMargin: Dp = Dp.Unspecified,
    contentAlignment: Alignment = Alignment.Center,
    listState: LazyListState? = null,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = LocalIndication.current,
    pagerState: PagerState? = null,
) {
    val currentOnTabSelected by rememberUpdatedState(onTabSelected)
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val windowInfo = LocalWindowInfo.current
    val textMeasurer = rememberTextMeasurer()
    val labelStyle = COUITheme.textStyles.bodyM
    // COUITabLayout: sans-serif-medium for the selected tab, sans-serif for the others.
    val normalStyle = remember(labelStyle) { labelStyle.copy(fontWeight = FontWeight.Normal) }
    val selectedStyle = remember(labelStyle) { labelStyle.copy(fontWeight = FontWeight.Medium) }
    val trackColor = colors.backgroundColor(false)
    val indicatorColor = if (enabled) colors.backgroundColor(true) else colors.disabledIndicatorColor()
    val normalColor = colors.contentColor(false)
    val selectedColor = colors.contentColor(true)
    val disabledColor = colors.disabledContentColor()
    val resolvedListState = listState ?: rememberLazyListState()
    val resolvedHeight = if (height.isSpecified) {
        height
    } else if (items.any { it.icon != null && it.text.isNotEmpty() }) {
        TabRowDefaults.TabRowIconHeight
    } else {
        TabRowDefaults.TabRowHeight
    }

    // COUITabLayout.TabLayoutOnPageChangeListener: only a drag of the pager drives the row; a
    // programmatic page change after a tab click leaves the click animation in charge.
    var pagerDragging by remember { mutableStateOf(false) }
    var pagerSettledIndex by remember { mutableIntStateOf(selectedTabIndex) }
    // COUITabLayout.mSelectedPosition: the page the label crossfade of a drag starts from. It moves
    // to every page the drag passes, so a drag across several pages crossfades each pair in turn.
    var pagerColorIndex by remember { mutableIntStateOf(selectedTabIndex) }
    if (pagerState != null) {
        LaunchedEffect(pagerState) {
            pagerState.interactionSource.interactions.collect { interaction ->
                if (interaction is DragInteraction.Start) {
                    if (!pagerDragging) {
                        pagerSettledIndex = pagerState.currentPage
                        pagerColorIndex = pagerState.currentPage
                    }
                    pagerDragging = true
                }
            }
        }
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.isScrollInProgress }.collect { scrolling ->
                if (!scrolling) {
                    pagerDragging = false
                    pagerSettledIndex = pagerState.currentPage
                }
            }
        }
    }
    // COUITabView.setSelected switches the typeface once the pager settles.
    val weightIndex = if (pagerDragging) pagerSettledIndex else selectedTabIndex

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
            .height(resolvedHeight),
    ) {
        val widthPx = constraints.maxWidth
        val heightPx = constraints.maxHeight
        val windowHeight = with(density) { windowInfo.containerSize.height.toDp() }
        val geometry = remember(
            items,
            weightIndex,
            widthPx,
            heightPx,
            windowHeight,
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
                val specs = items.mapIndexed { index, item ->
                    TabContentSpec(
                        textWidth = if (item.text.isEmpty()) {
                            0
                        } else {
                            textMeasurer.measure(
                                text = item.text,
                                style = if (index == weightIndex) selectedStyle else normalStyle,
                                maxLines = 1,
                            ).size.width
                        },
                        iconWidth = if (item.icon != null) TabIconSize.roundToPx() else 0,
                        badgeWidth = item.badge?.let { (tabBadgeWidth(it) + BadgeHorizontalOffset).roundToPx() } ?: 0,
                    )
                }
                computeUnderlineTabGeometry(
                    specs = specs,
                    widthPx = widthPx,
                    heightPx = heightPx,
                    windowHeight = windowHeight,
                    minWidth = minWidth,
                    maxWidth = maxWidth,
                    itemSpacing = itemSpacing,
                    horizontalMargin = horizontalMargin,
                    contentAlignment = contentAlignment,
                )
            }
        }
        val currentGeometry by rememberUpdatedState(geometry)
        val scope = rememberCoroutineScope()
        // COUITabLayoutBaseStyle sets android:hapticFeedbackEnabled="false" on the tab layout, which
        // silences the COUIHorizontalScrollView edge vibration (performHapticFeedback(307)), so
        // strip.onEdge stays empty. Only the tabs themselves vibrate.
        val strip = remember { TabStripScroller() }
        strip.range = geometry.scrollRange.toFloat()
        strip.canScroll = geometry.scrollable
        strip.maxOverScroll = windowInfo.containerSize.width

        // COUISlidingTabStrip.animateIndicatorToPosition: one animator drives the indicator edges
        // and the label color crossfade. The edges lerp from where the indicator was drawn.
        val progress = remember { Animatable(1f) }
        var targetIndex by remember { mutableIntStateOf(selectedTabIndex) }
        var previousIndex by remember { mutableIntStateOf(-1) }
        var fromLeft by remember { mutableFloatStateOf(0f) }
        var fromRight by remember { mutableFloatStateOf(0f) }
        val pagerPosition by remember(pagerState) {
            derivedStateOf {
                if (pagerState != null && pagerDragging) {
                    pagerState.currentPage + pagerState.currentPageOffsetFraction
                } else {
                    Float.NaN
                }
            }
        }

        // The in-range part of the scroll lives in the lazy list; the overscroll translates it.
        val listScroll by remember(geometry, resolvedListState) {
            derivedStateOf {
                val firstIndex = resolvedListState.firstVisibleItemIndex
                (geometry.slotLefts.getOrElse(firstIndex) { 0 } + resolvedListState.firstVisibleItemScrollOffset).toFloat()
            }
        }
        val overScroll by remember(strip) {
            derivedStateOf { strip.value - strip.value.coerceIn(0f, strip.range) }
        }
        LaunchedEffect(strip, resolvedListState) {
            snapshotFlow { strip.value.coerceIn(0f, strip.range).roundToInt() }.collect { target ->
                val geo = currentGeometry
                val item = geo.itemAt(target)
                if (item >= 0) resolvedListState.scrollToItem(item, target - geo.slotLefts[item])
            }
        }

        // Keep the selected tab centered after a layout change (COUITabLayout.onLayout).
        LaunchedEffect(widthPx, items) {
            strip.stop()
            strip.value = currentGeometry.scrollTargetFor(targetIndex.toFloat())
        }

        // COUITabLayout.setScrollPosition: a dragged pager scrolls the row with it.
        LaunchedEffect(strip) {
            var lastOffset = Float.NaN
            snapshotFlow { pagerPosition }.collect { position ->
                if (position.isNaN()) {
                    lastOffset = Float.NaN
                    return@collect
                }
                // setSelectedTabView(int, float): an offset that lands on a page or wraps by more
                // than half a page (the drag passed a page) restarts the crossfade from there.
                val offset = position - floor(position)
                if (offset == 0f || (!lastOffset.isNaN() && abs(offset - lastOffset) > 0.5f)) {
                    pagerColorIndex = position.roundToInt().coerceIn(0, max(0, currentGeometry.lastIndex))
                }
                lastOffset = offset
                strip.stop()
                strip.value = currentGeometry.scrollTargetFor(position)
            }
        }

        LaunchedEffect(selectedTabIndex) {
            val geo = currentGeometry
            if (targetIndex == selectedTabIndex || geo.isEmpty) {
                targetIndex = selectedTabIndex
                progress.snapTo(1f)
                return@LaunchedEffect
            }
            if (pagerDragging) {
                // COUITabLayout.selectTab(tab, false): the indicator already follows the pager. Its
                // performHapticFeedback(302) is silenced by the style's hapticFeedbackEnabled="false".
                targetIndex = selectedTabIndex
                previousIndex = -1
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
            strip.animateTo(scope, geo.scrollTargetFor(selectedTabIndex.toFloat()), tween(INDICATOR_MAX_DURATION, easing = IndicatorEasing))
            progress.animateTo(1f, tween(duration, easing = IndicatorEasing))
            previousIndex = -1
        }

        val labelColor: (Int, Boolean) -> Color = remember(normalColor, selectedColor, disabledColor, enabled, items) {
            { index, pressed ->
                val position = pagerPosition
                when {
                    !enabled || items.getOrNull(index)?.enabled == false -> disabledColor

                    // coui_tab_default_color: the pressed state uses the selected color.
                    pressed -> selectedColor

                    // COUITabLayout.setSelectedTabView(position, offset): the label nearest the page
                    // offset crossfades with the last page passed over the second half of the way.
                    !position.isNaN() -> {
                        val settled = pagerColorIndex
                        val nearest = position.roundToInt()
                        val t = (2f * abs(position - settled) - 1f).coerceIn(0f, 1f)
                        when {
                            nearest == settled -> if (index == settled) selectedColor else normalColor
                            index == nearest -> lerp(normalColor, selectedColor, t)
                            index == settled -> lerp(selectedColor, normalColor, t)
                            else -> normalColor
                        }
                    }

                    else -> {
                        val p = progress.value
                        when (index) {
                            targetIndex -> if (p < 1f) lerp(normalColor, selectedColor, p) else selectedColor
                            previousIndex -> lerp(selectedColor, normalColor, p)
                            else -> normalColor
                        }
                    }
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
                    val rtl = layoutDirection == LayoutDirection.Rtl
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
                    val position = pagerPosition
                    val left: Float
                    val right: Float
                    if (!position.isNaN()) {
                        // COUISlidingTabStrip.updateIndicatorPosition with a selection offset.
                        val page = floor(position).toInt().coerceIn(0, geometry.lastIndex)
                        val next = (page + 1).coerceAtMost(geometry.lastIndex)
                        val offset = (position - page).coerceIn(0f, 1f)
                        left = lerp(geometry.indicatorLeft(page), geometry.indicatorLeft(next), offset)
                        right = lerp(geometry.indicatorRight(page), geometry.indicatorRight(next), offset)
                    } else {
                        val p = progress.value
                        left = lerp(fromLeft, geometry.indicatorLeft(targetIndex), p)
                        right = lerp(fromRight, geometry.indicatorRight(targetIndex), p)
                    }
                    val halfWidth = max(right - left, IndicatorMinWidth.toPx()) / 2f
                    val center = (left + right) / 2f
                    val scroll = listScroll + overScroll
                    var start = max(pad + center - halfWidth - scroll, pad)
                    var end = min(pad + center + halfWidth - scroll, size.width - pad)
                    if (end > start) {
                        if (rtl) {
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
            val dragState = rememberDraggableState { delta ->
                strip.drag(if (layoutDirection == LayoutDirection.Rtl) delta else -delta)
            }
            LazyRow(
                state = resolvedListState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = with(density) { geometry.padding.toDp() })
                    .clipToBounds()
                    .pointerInput(strip) {
                        // COUIHorizontalScrollView.onTouchEvent: a touch stops a running fling or
                        // spring-back, and lifting without a drag springs an overscroll back.
                        awaitEachGesture {
                            awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                            strip.touchDown()
                            do {
                                val event = awaitPointerEvent(PointerEventPass.Initial)
                            } while (event.changes.any { it.pressed })
                            if (!strip.dragging) strip.releaseWithoutDrag(scope)
                        }
                    }
                    .draggable(
                        state = dragState,
                        orientation = Orientation.Horizontal,
                        enabled = geometry.scrollable,
                        onDragStarted = { strip.dragStarted() },
                        onDragStopped = { velocity ->
                            strip.release(scope, if (layoutDirection == LayoutDirection.Rtl) -velocity else velocity, with(density) { MinimumFlingVelocity.toPx() })
                        },
                    )
                    .graphicsLayer {
                        translationX = if (layoutDirection == LayoutDirection.Rtl) overScroll else -overScroll
                    },
                verticalAlignment = Alignment.CenterVertically,
                userScrollEnabled = false,
                overscrollEffect = null,
            ) {
                itemsIndexed(items) { index, item ->
                    val tab = geometry.tabs[index]
                    UnderlineTab(
                        item = item,
                        isSelected = selectedTabIndex == index,
                        enabled = enabled && item.enabled,
                        onClick = { currentOnTabSelected.invoke(index) },
                        width = with(density) { geometry.slotWidths[index].toDp() },
                        tab = tab,
                        labelColor = { pressed -> labelColor(index, pressed) },
                        style = if (weightIndex == index) selectedStyle else normalStyle,
                        cornerRadius = cornerRadius,
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
    item: TabRowItem,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    width: Dp,
    tab: TabSlot,
    labelColor: (Boolean) -> Color,
    style: TextStyle,
    cornerRadius: Dp,
    interactionSource: MutableInteractionSource?,
    indication: Indication?,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val performHaptic = rememberCouiHaptic()
    Layout(
        modifier = Modifier
            .fillMaxHeight()
            .width(width)
            // COUITabView: COUIMaskEffectDrawable over the whole tab, 8dp corners.
            .clip(RoundedCornerShape(cornerRadius))
            // COUITabView.onTouchEvent: ACTION_DOWN marks the mask touched at once, even inside a
            // scrolling page, and vibrates (302) when the tab is not the selected one.
            .tabTouchDownPress(interactionSource = interactionSource, enabled = enabled) {
                if (!isSelected) performHaptic(CouiHapticEffect.Switch)
            }
            .selectable(
                selected = isSelected,
                interactionSource = interactionSource,
                indication = indication,
                enabled = enabled,
                role = Role.Tab,
                onClick = onClick,
            ),
        content = {
            Text(
                text = item.text,
                color = { labelColor(isPressed && !isSelected) },
                style = style,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (item.icon != null) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    modifier = Modifier.size(TabIconSize),
                    tint = labelColor(isPressed && !isSelected),
                )
            }
            if (item.badge != null) {
                Badge(count = item.badge)
            }
        },
    ) { measurables, constraints ->
        // COUITabView is a horizontal LinearLayout: [icon][label][red dot], centered as a block.
        val text = measurables[0].measure(Constraints.fixedWidth(tab.textWidth).copy(minHeight = 0, maxHeight = constraints.maxHeight))
        var next = 1
        val icon = if (item.icon != null) measurables[next++].measure(Constraints()) else null
        val badge = if (item.badge != null) measurables[next].measure(Constraints()) else null
        val height = constraints.maxHeight
        layout(constraints.maxWidth, height) {
            var x = tab.contentStart
            if (icon != null) {
                // coui_tab_layout_icon: 8dp bottom margin when the tab also has a label.
                val bottomMargin = if (item.text.isNotEmpty()) TabIconBottomMargin.roundToPx() else 0
                icon.placeRelative(x, (height - icon.height - bottomMargin) / 2)
                x += tab.iconWidth
            }
            text.placeRelative(x, (height - text.height) / 2)
            x += tab.textWidth
            if (badge != null) {
                // COUISlidingTabStrip.measureChildWithRedDot: top gravity, 2dp start margin, 5dp
                // (dot) or 1dp (number) top margin.
                val top = if ((item.badge ?: 0) > 0) BadgeTopOffsetNumber else BadgeTopOffsetDot
                badge.placeRelative(x + BadgeHorizontalOffset.roundToPx(), top.roundToPx())
            }
        }
    }
}

/**
 * Starts a press of [interactionSource] the moment a pointer goes down, as the COUI views do on
 * `ACTION_DOWN` (`COUITabView.onTouchEvent` marks its mask touched, `COUIChip.handleActionDown`
 * starts its press scale and mask, `COUISegmentButtonLayout.dispatchTouchEvent` presses the
 * touched segment), instead of after the tap timeout Compose's clickable waits for inside a
 * scrolling parent. The press is released when the pointer lifts and cancelled when a parent
 * scroller takes the gesture (`ACTION_CANCEL`). [onDown] runs with the down.
 *
 * The clickable of the element keeps emitting its own, possibly delayed, press into the same
 * source; the COUI indications and press-state readers only ask whether a press is active, so the
 * two overlap harmlessly.
 */
@Composable
internal fun Modifier.tabTouchDownPress(
    interactionSource: MutableInteractionSource,
    enabled: Boolean,
    onDown: () -> Unit = {},
): Modifier {
    val currentOnDown by rememberUpdatedState(onDown)
    return pointerInput(interactionSource, enabled) {
        if (!enabled) return@pointerInput
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            val press = PressInteraction.Press(down.position)
            interactionSource.tryEmit(press)
            currentOnDown()
            var end: PressInteraction = PressInteraction.Cancel(press)
            try {
                while (true) {
                    val change = awaitPointerEvent(PointerEventPass.Final).changes.firstOrNull { it.id == down.id } ?: break
                    if (!change.pressed) {
                        end = PressInteraction.Release(press)
                        break
                    }
                    if (change.isConsumed && change.positionChangedIgnoreConsumed()) break
                }
            } finally {
                interactionSource.tryEmit(end)
            }
        }
    }
}

/** The measured content of one tab: label, icon and red dot (with its 2dp margin) widths, px. */
internal class TabContentSpec(val textWidth: Int, val iconWidth: Int, val badgeWidth: Int)

/**
 * One tab of the layout, in px: [contentStart] is the start of the icon/label/dot block inside
 * the tab, [iconWidth] and [textWidth] the widths it lays out.
 */
@Immutable
internal class TabSlot(
    val contentStart: Int,
    val iconWidth: Int,
    val textWidth: Int,
)

/**
 * The horizontal layout of a [TabRow], in pixels.
 *
 * [slotLefts] and [slotWidths] describe each tab including the half gap on both sides, relative to
 * the scrolling content that starts after [padding]. The indicator spans are in the same
 * coordinates.
 */
@Immutable
internal class UnderlineTabGeometry(
    val padding: Int,
    val slotLefts: List<Int>,
    val slotWidths: List<Int>,
    val tabs: List<TabSlot>,
    private val indicatorLefts: List<Int>,
    private val indicatorRights: List<Int>,
    val viewportWidth: Int,
) {
    val isEmpty: Boolean get() = slotWidths.isEmpty()
    val lastIndex: Int get() = slotWidths.lastIndex
    val contentWidth: Int = if (slotWidths.isEmpty()) 0 else slotLefts.last() + slotWidths.last()
    val scrollRange: Int = max(0, contentWidth - viewportWidth)
    val scrollable: Boolean = contentWidth > viewportWidth

    fun indicatorLeft(index: Int): Float = if (isEmpty) 0f else indicatorLefts[index.coerceIn(0, lastIndex)].toFloat()

    fun indicatorRight(index: Int): Float = if (isEmpty) 0f else indicatorRights[index.coerceIn(0, lastIndex)].toFloat()

    /**
     * COUITabLayout.calculateScrollXForTab: center the tab at [position] (a tab index plus an
     * offset towards the next tab) in the viewport, clamped like `HorizontalScrollView.scrollTo`.
     */
    fun scrollTargetFor(position: Float): Float {
        if (isEmpty) return 0f
        val index = floor(position).toInt().coerceIn(0, lastIndex)
        val offset = (position - index).coerceIn(0f, 1f)
        val nextWidth = if (index < lastIndex) slotWidths[index + 1] else 0
        val target = slotWidths[index] / 2 - viewportWidth / 2 + slotLefts[index] +
            ((slotWidths[index] + nextWidth) * 0.5f * offset).toInt()
        return target.coerceIn(0, scrollRange).toFloat()
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
 * COUISlidingTabStrip.onMeasure in fixed mode: measure every tab, then spread them over the 360dp
 * content width, center them with the minimum divider, or let them overflow and scroll.
 *
 * The responsive margin follows `parseMinMargin` (row width against the window height) and the
 * divider `parseMinDivider` (row width against the row height).
 */
internal fun Density.computeUnderlineTabGeometry(
    specs: List<TabContentSpec>,
    widthPx: Int,
    heightPx: Int,
    windowHeight: Dp,
    minWidth: Dp,
    maxWidth: Dp,
    itemSpacing: Dp,
    horizontalMargin: Dp,
    contentAlignment: Alignment,
): UnderlineTabGeometry {
    val count = specs.size
    if (count == 0) {
        return UnderlineTabGeometry(0, emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), 0)
    }
    val width = if (widthPx == Constraints.Infinity) 0 else widthPx
    val widthDp = width.toDp()
    val heightDp = if (heightPx == Constraints.Infinity) 0.dp else heightPx.toDp()
    val minDivider = if (itemSpacing.isSpecified) {
        itemSpacing.roundToPx()
    } else if (COUILayoutGrid.screenTier(widthDp, heightDp) == COUILayoutGrid.ScreenTier.Small) {
        SmallTabSpacing.roundToPx()
    } else {
        MediumTabSpacing.roundToPx()
    }
    val minMargin = if (horizontalMargin.isSpecified) {
        horizontalMargin.roundToPx()
    } else {
        when (COUILayoutGrid.screenTier(widthDp, windowHeight)) {
            COUILayoutGrid.ScreenTier.Small -> SmallHorizontalMargin.roundToPx()
            COUILayoutGrid.ScreenTier.Medium -> MediumHorizontalMargin.roundToPx()
            COUILayoutGrid.ScreenTier.Large -> LargeHorizontalMargin.roundToPx()
        }
    }
    // COUITabLayout.onMeasure: without couiTabMaxWidth a tab is at most 70% of the row.
    val maxTabPx = when {
        maxWidth == Dp.Unspecified -> (width * DEFAULT_TAB_MAX_WIDTH_RATIO).toInt()
        maxWidth == Dp.Infinity -> Int.MAX_VALUE
        else -> maxWidth.roundToPx()
    }
    val minTabPx = if (minWidth.isSpecified) minWidth.roundToPx() else 0
    // measureChildWithRedDot: the label gives way to the icon and the red dot past the maximum.
    val textWidths = specs.map { spec ->
        val content = spec.iconWidth + spec.textWidth + spec.badgeWidth
        if (content <= maxTabPx) spec.textWidth else max(0, maxTabPx - spec.iconWidth - spec.badgeWidth)
    }
    val contentWidths = specs.mapIndexed { i, spec -> spec.iconWidth + textWidths[i] + spec.badgeWidth }
    val tabWidths = contentWidths.map { max(it, minTabPx) }
    val sum = tabWidths.sum()
    val contentMinWidth = ContentMinWidth.roundToPx()
    val total = minMargin * 2 + sum + (count - 1) * minDivider
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
    val tabs = ArrayList<TabSlot>(count)
    val indicatorLefts = ArrayList<Int>(count)
    val indicatorRights = ArrayList<Int>(count)
    var left = 0
    for (i in 0 until count) {
        val slotWidth = tabWidths[i] + gapHalf * 2
        val alignOffset = contentAlignment.align(
            IntSize(contentWidths[i], 0),
            IntSize(tabWidths[i], 0),
            LayoutDirection.Ltr,
        ).x
        val contentStart = gapHalf + alignOffset
        slotLefts.add(left)
        slotWidths.add(slotWidth)
        tabs.add(TabSlot(contentStart = contentStart, iconWidth = specs[i].iconWidth, textWidth = textWidths[i]))
        // updateIndicatorPosition: the label bounds (coui_tablayout_indicator_padding is 0dp); an
        // icon-only tab falls back to its content.
        if (textWidths[i] > 0) {
            val textLeft = left + contentStart + specs[i].iconWidth
            indicatorLefts.add(textLeft)
            indicatorRights.add(textLeft + textWidths[i])
        } else {
            indicatorLefts.add(left + contentStart)
            indicatorRights.add(left + contentStart + contentWidths[i])
        }
        left += slotWidth
    }
    return UnderlineTabGeometry(
        padding = resolvedPadding,
        slotLefts = slotLefts,
        slotWidths = slotWidths,
        tabs = tabs,
        indicatorLefts = indicatorLefts,
        indicatorRights = indicatorRights,
        viewportWidth = max(0, width - resolvedPadding * 2),
    )
}

/**
 * The scroll position of the tab strip and its `COUIHorizontalScrollView` motion: rubber-band
 * drags past the edges, [COUIReboundOverScroller] flings that overshoot and spring back, and
 * spring-backs on release.
 */
@Stable
private class TabStripScroller {
    /** The scroll position, px; negative or past [range] while overscrolled. */
    var value by mutableFloatStateOf(0f)
    var range = 0f
    var canScroll = false

    /** `mOverscrollDistance` / `mOverflingDistance`: the screen width, px. */
    var maxOverScroll = 1
    var onEdge: () -> Unit = {}
    var dragging = false
        private set

    private val scroller = COUIReboundOverScroller(springBackTensionMultiple = HORIZONTAL_SPRING_BACK_TENSION_MULTIPLE)
    private var flingStrictSpan = false
    private var job: Job? = null
    private var pendingDrag = 0f

    fun stop() {
        job?.cancel()
        job = null
        scroller.setAtRest()
    }

    fun touchDown() {
        stop()
        flingStrictSpan = false
        dragging = false
    }

    fun dragStarted() {
        stop()
        dragging = true
        pendingDrag = 0f
    }

    /** ACTION_MOVE: [delta] is in scroll direction, px. */
    fun drag(delta: Float) {
        pendingDrag += delta
        var d = pendingDrag.toInt()
        if (d == 0) return
        pendingDrag -= d
        val rangeInt = range.toInt()
        val position = value.roundToInt()
        if (position < 0) {
            d = COUIPhysicalAnimation.calcRealOverScrollDist(d, position, maxOverScroll)
        } else if (position > rangeInt) {
            d = COUIPhysicalAnimation.calcRealOverScrollDist(d, position - rangeInt, maxOverScroll)
        }
        overScrolled(position + d)
    }

    /** ACTION_UP after a drag; [velocity] is the finger velocity, px/s, in screen direction. */
    fun release(scope: CoroutineScope, velocity: Float, minimumVelocity: Float) {
        dragging = false
        val xVelocity = velocity.toInt()
        val position = value.roundToInt()
        val rangeInt = range.toInt()
        if (abs(xVelocity) <= minimumVelocity) {
            springBack(scope)
        } else if (position < 0) {
            if (xVelocity > -FLING_SCROLL_THRESHOLD) {
                scroller.velocity = -xVelocity.toDouble()
                springBack(scope)
            } else {
                fling(scope, -xVelocity)
            }
        } else if (position > rangeInt) {
            if (xVelocity < FLING_SCROLL_THRESHOLD) {
                scroller.velocity = -xVelocity.toDouble()
                springBack(scope)
            } else {
                fling(scope, -xVelocity)
            }
        } else if (position in 1 until rangeInt) {
            fling(scope, -xVelocity)
        }
        if (position < 0 || position > rangeInt) onEdge()
    }

    fun releaseWithoutDrag(scope: CoroutineScope) {
        springBack(scope)
    }

    /** The tab-click scroll animator (`COUITabLayout.ensureScrollAnimator`). */
    fun animateTo(scope: CoroutineScope, target: Float, spec: AnimationSpec<Float>) {
        stop()
        if (value == target) return
        job = scope.launch {
            animate(value, target, animationSpec = spec) { current, _ -> value = current }
        }
    }

    private fun springBack(scope: CoroutineScope) {
        if (scroller.springBack(value.roundToInt(), 0, range.toInt())) run(scope)
    }

    private fun fling(scope: CoroutineScope, velocity: Int) {
        flingStrictSpan = true
        job?.cancel()
        job = scope.launch {
            withFrameNanos { nanos ->
                scroller.fling(value.roundToInt(), velocity, nanos / 1_000_000L, FLING_FRAME_INTERVAL_SEC)
            }
            loop()
        }
    }

    private fun run(scope: CoroutineScope) {
        job?.cancel()
        job = scope.launch { loop() }
    }

    /** COUIHorizontalScrollView.computeScroll. */
    private suspend fun loop() {
        var lastNanos = -1L
        while (true) {
            val running = withFrameNanos { nanos ->
                val delta = if (lastNanos < 0L) 0f else (nanos - lastNanos) / 1_000_000_000f
                lastNanos = nanos
                val more = scroller.update(nanos / 1_000_000L, delta)
                overScrolled(scroller.position.roundToInt())
                more
            }
            if (!running) {
                scroller.setAtRest()
                flingStrictSpan = false
                break
            }
        }
    }

    /** COUIHorizontalScrollView.onOverScrolled. */
    private fun overScrolled(target: Int) {
        val rangeInt = range.toInt()
        var x = target
        if ((x < 0 || x > rangeInt) && flingStrictSpan) {
            val edge = if (x >= rangeInt) rangeInt else 0
            x = COUIPhysicalAnimation.calcOverFlingDecelerateDist(edge, x - edge, maxOverScroll)
        }
        // OVER_SCROLL_IF_CONTENT_SCROLLS: no overscroll unless the tabs overflow.
        if (!canScroll) x = x.coerceIn(0, rangeInt)
        val current = value.roundToInt()
        if (current >= 0 && x < 0 && flingStrictSpan) {
            onEdge()
            scroller.notifyEdgeReached(x, 0, 0, 0)
        }
        if (current <= rangeInt && x > rangeInt && flingStrictSpan) {
            onEdge()
            scroller.notifyEdgeReached(x, rangeInt, 0, rangeInt)
        }
        value = x.toFloat()
    }
}

/** The red dot width COUIHintRedDot measures for [badge] (see [Badge]). */
private fun tabBadgeWidth(badge: Int): Dp = when {
    badge <= 0 -> BadgeDefaults.DotDiameter
    badge < 10 -> BadgeDefaults.SmallWidth
    badge < 100 -> BadgeDefaults.MediumWidth
    badge < 1000 -> BadgeDefaults.LargeWidth
    else -> BadgeDefaults.MediumWidth
}

/**
 * A [TabRowWithContour] with COUI style.
 *
 * A port of the ColorOS segmented control (COUISegmentButtonLayout, default `SegmentButton`
 * style) that scrolls when its segments overflow. For the `SegmentButton.Tiny` style, pass
 * [TabRowDefaults.TabRowWithContourTinyHeight], [TabRowDefaults.TabRowWithContourTinyCornerRadius]
 * and [TabRowDefaults.TabRowWithContourTinyPadding].
 *
 * The indicator moves on the COUI 17 segment button springs, as in [SegmentButton]: position
 * (response 0.4, bounce 0.1), width (response 0.6 → 0.4 and bounce 0.5 → 0.1 by how different the
 * two widths are) and a height dip to 90%. A touched segment shrinks with the COUI 17 press scale
 * from the touch down, and pressing the selected segment presses the indicator in. All labels use
 * `couiTextButtonM`. Use [SegmentButton] for the drag-to-switch indicator and the COUI 17 material.
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
 * Shared implementation of the COUI segment button, following `COUISegmentButtonLayout.onDraw`.
 *
 * The indicator runs on the [SegmentButton] springs ([SegmentButtonState]): position response 0.4 /
 * bounce 0.1, a width spring started at 1000 px/s whose response and bounce ease by how different
 * the two widths are, and a height that dips towards 90% and springs back. Pressing a segment
 * shrinks it with the COUI 17 press scale from the touch down; pressing the selected one also
 * presses the indicator in. Every label uses `couiTextButtonM` (`refreshSegmentTextConfig`).
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
    val isDarkTheme = COUITheme.colorScheme.isDark
    // coui_segment_button_shadow_color / _end_color, as the segment button draws them.
    val segmentColors = SegmentButtonDefaults.segmentButtonColors()
    val shadowColor = segmentColors.indicatorShadowColor
    val shadowEndColor = segmentColors.indicatorShadowEndColor
    val containerColor = colors.backgroundColor(false)
    val indicatorColor = colors.backgroundColor(true)
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    // refreshSegmentTextConfig: couiTextButtonM (14sp, medium) on every segment, selected or not.
    val labelStyle = SegmentButtonDefaults.textStyle()
    val resolvedListState = listState ?: rememberLazyListState()
    val state = remember { SegmentButtonState() }
    val scope = rememberCoroutineScope()

    // selectSegmentAt: the indicator follows the hoisted selection on the segment button springs.
    SideEffect {
        if (selectedTabIndex != state.paramIndex) {
            state.paramIndex = selectedTabIndex
            state.selected = selectedTabIndex
            if (state.indicatorTarget != selectedTabIndex) state.animateTo(selectedTabIndex, scope)
        }
    }

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
        val paddingPx = with(density) { contourPadding.roundToPx() }

        // COUISegmentButtonLayout.calculateChildrenWidths: natural label widths distributed to fill the row.
        val segmentWidthsPx = remember(
            tabs,
            viewportPx,
            minWidthPx,
            maxWidthPx,
            spacingPx,
            segmentPaddingPx,
            textMeasurer,
            labelStyle,
        ) {
            val naturalWidths = tabs.map { label ->
                max(
                    textMeasurer.measure(text = label, style = labelStyle, maxLines = 1).size.width +
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
        // The segment bounds along the whole (possibly scrolling) track, from its start edge.
        val segmentStarts = remember(segmentLeftsPx, paddingPx) {
            FloatArray(segmentLeftsPx.size) { (paddingPx + segmentLeftsPx[it]).toFloat() }
        }
        val segmentEnds = remember(segmentStarts, segmentWidthsPx) {
            FloatArray(segmentStarts.size) { segmentStarts[it] + segmentWidthsPx[it] }
        }
        val trackWidth = if (segmentEnds.isEmpty()) 0f else segmentEnds.last() + paddingPx
        var lastSettledSelectedTabIndex by remember(resolvedListState) { mutableIntStateOf(-1) }

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

        // The indicator's COUIPressFeedbackHelper: pressing the selected segment presses it in.
        var selectedSegmentPressed by remember { mutableStateOf(false) }
        LaunchedEffect(selectedSegmentPressed) {
            state.pressIndicator(selectedSegmentPressed, scope)
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
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    val measureDensity: Density = this
                    layout(placeable.width, placeable.height) {
                        // onLayout / onSizeChanged: a new layout snaps the indicator onto the selection.
                        state.onLayout(
                            density = measureDensity,
                            lefts = segmentStarts,
                            rights = segmentEnds,
                            width = trackWidth,
                            height = placeable.height.toFloat(),
                            padding = paddingPx.toFloat(),
                            paddingDp = contourPadding,
                        )
                        placeable.place(0, 0)
                    }
                }
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
                    val shadowOffset = IndicatorShadowOffsetY.toPx()
                    val radiusLimit = cornerRadius.toPx()
                    onDrawBehind {
                        if (containerColor.alpha > 0f) {
                            drawPath(path = containerPath, color = containerColor)
                        }
                        // updateIndicatorRect, along the track; the segments scroll under the row.
                        val track = state.indicatorRect(trackWidth)
                        if (track.width <= 0f || track.height <= 0f) return@onDrawBehind
                        val scroll = scrollOffset
                        val rect = if (layoutDirection == LayoutDirection.Rtl) {
                            Rect(size.width - track.right + scroll, track.top, size.width - track.left + scroll, track.bottom)
                        } else {
                            Rect(track.left - scroll, track.top, track.right - scroll, track.bottom)
                        }
                        val radius = min(radiusLimit, rect.height / 2f)
                        indicatorPath.rewind()
                        indicatorPath.addSquircleRect(
                            rect = rect,
                            cornerRadius = radius,
                            weight = COUIRoundCorner.UnspecifiedWeight,
                            squircleEnabled = squircleEnabled,
                        )
                        clipPath(containerPath) {
                            // 2. drawShadowWithGradient, then 3. the indicator capsule.
                            drawSegmentIndicatorShadow(
                                rect = rect,
                                radius = radius,
                                indicatorPath = indicatorPath,
                                bottom = size.height,
                                offset = shadowOffset,
                                shadowColor = shadowColor,
                                shadowEndColor = shadowEndColor,
                                isDark = isDarkTheme,
                            )
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
                        style = labelStyle,
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
    style: TextStyle,
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
    // COUISegmentButtonLayout.setSelectedAt: the label colour crossfades over 300ms.
    val textColor by animateColorAsState(
        targetValue = color,
        animationSpec = tween(TextColorAnimationDuration, easing = TextColorEasing),
        label = "segmentTextColor",
    )
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(width)
            // executeScaleAnimatorAt: the segment's COUIPressFeedbackHelper, the COUI 17 press scale.
            .couiPressScale(pressed = isPressed)
            // dispatchTouchEvent presses the touched segment on ACTION_DOWN, inside a scroller too.
            .tabTouchDownPress(interactionSource = interactionSource, enabled = true)
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
            style = style,
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
     * The height of a [TabRow] whose tabs have both an icon and a label
     * (`COUITabLayout.getDefaultHeight`, 72dp).
     */
    val TabRowIconHeight = 72.dp

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
     * @param disabledContentColor The label color of disabled tabs (`couiColorDisabledNeutral`).
     * @param disabledIndicatorColor The indicator color of a disabled row
     *   (`couiTabIndicatorDisableColor`).
     */
    @Composable
    fun underlineColors(
        trackColor: Color = if (COUITheme.colorScheme.isDark) {
            TabTrackDark
        } else {
            TabTrackLight
        },
        indicatorColor: Color = COUITheme.colorScheme.labelPrimary,
        contentColor: Color = COUITheme.colorScheme.labelSecondary,
        selectedContentColor: Color = COUITheme.colorScheme.labelPrimary,
        disabledContentColor: Color = COUITheme.colorScheme.disabledNeutral,
        disabledIndicatorColor: Color = TabIndicatorDisabled,
    ): TabRowColors = remember(
        trackColor,
        indicatorColor,
        contentColor,
        selectedContentColor,
        disabledContentColor,
        disabledIndicatorColor,
    ) {
        TabRowColors(
            backgroundColor = trackColor,
            contentColor = contentColor,
            selectedBackgroundColor = indicatorColor,
            selectedContentColor = selectedContentColor,
            disabledContentColor = disabledContentColor,
            disabledSelectedBackgroundColor = disabledIndicatorColor,
        )
    }

    /**
     * The default colors for the [TabRowWithContour], following
     * COUISegmentButtonLayout: both label states default to `couiColorLabelPrimary` and every label
     * uses the medium `couiTextButtonM`, so the selected segment is told by the indicator alone.
     */
    @Composable
    fun tabRowColors(
        backgroundColor: Color = if (COUITheme.colorScheme.isDark) {
            SegmentContainerDark
        } else {
            SegmentContainerLight
        },
        contentColor: Color = COUITheme.colorScheme.labelPrimary,
        selectedBackgroundColor: Color = if (COUITheme.colorScheme.isDark) {
            SegmentIndicatorDark
        } else {
            SegmentIndicatorLight
        },
        selectedContentColor: Color = COUITheme.colorScheme.labelPrimary,
    ): TabRowColors = remember(backgroundColor, contentColor, selectedBackgroundColor, selectedContentColor) {
        TabRowColors(
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            selectedBackgroundColor = selectedBackgroundColor,
            selectedContentColor = selectedContentColor,
        )
    }
}

/**
 * The colors of a [TabRow] or [TabRowWithContour].
 *
 * @param backgroundColor The container ([TabRowWithContour]) or the track ([TabRow]).
 * @param contentColor The label color of unselected tabs.
 * @param selectedBackgroundColor The sliding indicator.
 * @param selectedContentColor The label color of the selected tab.
 * @param disabledContentColor The label color of disabled tabs ([TabRow] only); unspecified falls
 *   back to [contentColor].
 * @param disabledSelectedBackgroundColor The indicator of a disabled row ([TabRow] only);
 *   unspecified falls back to [selectedBackgroundColor].
 */
@Immutable
data class TabRowColors(
    private val backgroundColor: Color,
    private val contentColor: Color,
    private val selectedBackgroundColor: Color,
    private val selectedContentColor: Color,
    private val disabledContentColor: Color = Color.Unspecified,
    private val disabledSelectedBackgroundColor: Color = Color.Unspecified,
) {
    @Stable
    internal fun backgroundColor(selected: Boolean): Color = if (selected) selectedBackgroundColor else backgroundColor

    @Stable
    internal fun contentColor(selected: Boolean): Color = if (selected) selectedContentColor else contentColor

    @Stable
    internal fun disabledContentColor(): Color = disabledContentColor.takeOrElse { contentColor }

    @Stable
    internal fun disabledIndicatorColor(): Color = disabledSelectedBackgroundColor.takeOrElse { selectedBackgroundColor }
}

/** `tablayout_indicator_background` in light themes (#0F000000). */
private val TabTrackLight = Color(0x0F000000)

/** `tablayout_indicator_background` in dark themes (#19FFFFFF). */
private val TabTrackDark = Color(0x19FFFFFF)

/** `couiTabIndicatorDisableColor` (#FFD9D9D9, no night variant). */
private val TabIndicatorDisabled = Color(0xFFD9D9D9)

/** `coui_tab_layout_icon`: a 24dp icon with an 8dp bottom margin under a label. */
private val TabIconSize = 24.dp
private val TabIconBottomMargin = 8.dp

/** `coui_dot_horizontal_offset` (2dp): the red dot's start margin after the label. */
private val BadgeHorizontalOffset = 2.dp

/** `coui_dot_vertical_offset_only_red` (5dp) / `coui_dot_vertical_offset_number_red` (1dp). */
private val BadgeTopOffsetDot = 5.dp
private val BadgeTopOffsetNumber = 1.dp

/** `ViewConfiguration.MINIMUM_FLING_VELOCITY` (50dp/s). */
private val MinimumFlingVelocity = 50.dp

/** COUIHorizontalScrollView: `HORIZONTAL_SPRING_BACK_TENSION_MULTIPLE`. */
private const val HORIZONTAL_SPRING_BACK_TENSION_MULTIPLE = 3.2f

/** COUIHorizontalScrollView: `FLING_SCROLL_THRESHOLD` (px/s) that turns a release into a fling. */
private const val FLING_SCROLL_THRESHOLD = 1500

/**
 * The frame interval the fling simulation runs at; the original reads the display's from the
 * Choreographer, ColorOS phones run at 120Hz.
 */
private const val FLING_FRAME_INTERVAL_SEC = 1f / 120f

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

/** COUITabLayout.onMeasure: the tab maximum width without `setRequestedTabMaxWidth`. */
private const val DEFAULT_TAB_MAX_WIDTH_RATIO = 0.7f

/** COUIEaseInterpolator: PathInterpolator(0.33f, 0f, 0.67f, 1f). */
private val IndicatorEasing = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

/** COUITabLayout.getIndicatorAnimTime: min(300, |delta| * 50 + 150) ms. */
private const val INDICATOR_MAX_DURATION = 300
private const val INDICATOR_DURATION_PER_TAB = 50
private const val INDICATOR_BASE_DURATION = 150

/** COUIMoveEaseInterpolator: PathInterpolator(0.3f, 0f, 0.1f, 1f). */
private val TextColorEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

/** COUISegmentButtonLayout text color animator duration (COLOR_ANIMATOR_TIME = 300ms). */
private val TextColorAnimationDuration = 300

/** COUI `coui_segment_btn_padding_horizontal` (12dp) — horizontal text padding of a segment. */
private val SegmentPaddingHorizontal = 12.dp

/** COUI `coui_segment_shadow_offset` (2dp) — the vertical offset of the indicator shadow. */
private val IndicatorShadowOffsetY = 2.dp

/** COUI `coui_color_segment_button_background` in light themes (#0F000000). */
private val SegmentContainerLight = Color(0x0F000000)

/** COUI `coui_color_segment_button_background` in dark themes (#1FFFFFFF). */
private val SegmentContainerDark = Color(0x1FFFFFFF)

/** COUI 17 `coui_color_segment_button_indicator` in light themes (#B2FFFFFF). */
private val SegmentIndicatorLight = Color(0xB2FFFFFF)

/** COUI 17 `coui_color_segment_button_indicator` in dark themes (#33FFFFFF). */
private val SegmentIndicatorDark = Color(0x33FFFFFF)
