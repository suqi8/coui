// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import io.github.suqi8.coui.kmp.interfaces.ExperimentalScrollBarApi
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.abs

/**
 * Defines how to scroll the scrollable component and how to display a scrollbar for it.
 */
@ExperimentalScrollBarApi
interface ScrollBarAdapter {
    val scrollOffset: Double
    val contentSize: Double
    val viewportSize: Double
    suspend fun scrollTo(scrollOffset: Double)
}

@OptIn(ExperimentalScrollBarApi::class)
internal val ScrollBarAdapter.maxScrollOffset: Double
    get() = (contentSize - viewportSize).coerceAtLeast(0.0)

/**
 * Create and [remember] [ScrollBarAdapter] for [ScrollState].
 */
@ExperimentalScrollBarApi
@Composable
fun rememberScrollBarAdapter(
    scrollState: ScrollState,
): ScrollBarAdapter = remember(scrollState) {
    ScrollStateAdapter(scrollState)
}

/**
 * Create and [remember] [ScrollBarAdapter] for [LazyListState].
 */
@ExperimentalScrollBarApi
@Composable
fun rememberScrollBarAdapter(
    scrollState: LazyListState,
): ScrollBarAdapter = remember(scrollState) {
    LazyListAdapter(scrollState)
}

/**
 * Create and [remember] [ScrollBarAdapter] for [LazyGridState].
 */
@ExperimentalScrollBarApi
@Composable
fun rememberScrollBarAdapter(
    scrollState: LazyGridState,
): ScrollBarAdapter = remember(scrollState) {
    LazyGridAdapter(scrollState)
}

/**
 * Colors for the scrollbar component.
 *
 * @param thumbColor The color of the scrollbar thumb. [Color.Unspecified] uses theme default.
 * @param trackColor The color of the scrollbar track. [Color.Unspecified] hides the track.
 */
@ExperimentalScrollBarApi
@Immutable
data class ScrollBarColors(
    val thumbColor: Color,
    val trackColor: Color,
)

/**
 * A vertical scrollbar.
 *
 * @param adapter [ScrollBarAdapter] that communicates with the scrollable component.
 * @param modifier The modifier to apply to this layout.
 * @param reverseLayout Reverse the direction of scrolling and layout.
 * @param trackPadding Padding applied to the track to skip content padding areas.
 * @param colors The colors of the scrollbar.
 * @param thumbWidth The width of the thumb.
 * @param cornerRadius The corner radius. [Dp.Unspecified] defaults to half of [thumbWidth].
 * @param thumbMinLength The minimum length of the thumb rect; the visible thumb is drawn inside it,
 *   [ScrollBarDefaults.ThumbMarginStart] from its start and [ScrollBarDefaults.ThumbMarginEnd] from its end.
 * @param endPadding The padding from the end edge.
 */
@ExperimentalScrollBarApi
@Composable
fun VerticalScrollBar(
    adapter: ScrollBarAdapter,
    modifier: Modifier = Modifier,
    reverseLayout: Boolean = false,
    trackPadding: PaddingValues = PaddingValues(0.dp),
    colors: ScrollBarColors = ScrollBarDefaults.scrollBarColors(),
    thumbWidth: Dp = ScrollBarDefaults.ThumbWidth,
    cornerRadius: Dp = ScrollBarDefaults.CornerRadius,
    thumbMinLength: Dp = ScrollBarDefaults.ThumbMinLength,
    endPadding: Dp = ScrollBarDefaults.EndPadding,
) {
    ScrollBar(
        adapter = adapter,
        modifier = modifier,
        isVertical = true,
        reverseLayout = reverseLayout,
        trackPadding = trackPadding,
        colors = colors,
        thumbWidth = thumbWidth,
        cornerRadius = cornerRadius,
        thumbMinLength = thumbMinLength,
        endPadding = endPadding,
    )
}

/**
 * A horizontal scrollbar.
 *
 * @param adapter [ScrollBarAdapter] that communicates with the scrollable component.
 * @param modifier The modifier to apply to this layout.
 * @param reverseLayout Reverse the direction of scrolling and layout.
 * @param trackPadding Padding applied to the track to skip content padding areas.
 * @param colors The colors of the scrollbar.
 * @param thumbWidth The width of the thumb.
 * @param cornerRadius The corner radius. [Dp.Unspecified] defaults to half of [thumbWidth].
 * @param thumbMinLength The minimum length of the thumb rect; the visible thumb is drawn inside it,
 *   [ScrollBarDefaults.ThumbMarginStart] from its start and [ScrollBarDefaults.ThumbMarginEnd] from its end.
 * @param endPadding The padding from the end edge.
 */
@ExperimentalScrollBarApi
@Composable
fun HorizontalScrollBar(
    adapter: ScrollBarAdapter,
    modifier: Modifier = Modifier,
    reverseLayout: Boolean = false,
    trackPadding: PaddingValues = PaddingValues(0.dp),
    colors: ScrollBarColors = ScrollBarDefaults.scrollBarColors(),
    thumbWidth: Dp = ScrollBarDefaults.ThumbWidth,
    cornerRadius: Dp = ScrollBarDefaults.CornerRadius,
    thumbMinLength: Dp = ScrollBarDefaults.ThumbMinLength,
    endPadding: Dp = ScrollBarDefaults.EndPadding,
) {
    ScrollBar(
        adapter = adapter,
        modifier = modifier,
        isVertical = false,
        reverseLayout = reverseLayout,
        trackPadding = trackPadding,
        colors = colors,
        thumbWidth = thumbWidth,
        cornerRadius = cornerRadius,
        thumbMinLength = thumbMinLength,
        endPadding = endPadding,
    )
}

/**
 * Default values of the COUI 17 scrollbar (`COUIScrollBar`).
 *
 * The thumb is a `ThumbMinLength`-or-longer rect riding the full track; it is drawn inset by
 * [ThumbMarginStart] / [ThumbMarginEnd] along the track (COUI `coui_scrollbar_margin_top` /
 * `_bottom`) and by [EndPadding] (pressed: [DragEndPadding]) across it, so the visible thumb is
 * [ThumbWidth] (pressed: [DragThumbWidth]) wide and at least 24dp long. It appears at once when
 * the content scrolls, stays for [FadeDelayMillis] after the last scroll and then fades out
 * linearly over [FadeDurationMillis].
 */
@ExperimentalScrollBarApi
object ScrollBarDefaults {
    /** The visible thumb width: the 14dp `coui_scrollbar_wight` rect minus two 5.5dp insets. */
    val ThumbWidth: Dp = 3.dp

    /** The padding from the end edge (COUI coui_scrollbar_drawable_default_inset). */
    val EndPadding: Dp = 5.5.dp

    /** The minimum length of the thumb rect, margins included (COUI coui_scrollbar_min_height). */
    val ThumbMinLength: Dp = 48.dp

    /** The inset of the visible thumb from the start of its rect (COUI coui_scrollbar_margin_top). */
    val ThumbMarginStart: Dp = 8.dp

    /** The inset of the visible thumb from the end of its rect (COUI coui_scrollbar_margin_bottom). */
    val ThumbMarginEnd: Dp = 16.dp

    val CornerRadius: Dp = Dp.Unspecified

    /** How long the thumb stays after the last scroll or drag (COUIScrollBar.SCROLLER_FADE_TIMEOUT). */
    val FadeDelayMillis = 2000

    /**
     * How long the thumb stays when the scrollbar first appears: `initialAwakenScrollBars` waits
     * four times the platform scroll default delay (4 x 300ms).
     */
    val InitialFadeDelayMillis = 1200

    /** The linear fade-out (ViewConfiguration.getScrollBarFadeDuration, 250ms on AOSP). */
    val FadeDurationMillis = 250

    /** The width of the strip that can start a thumb drag (COUI coui_scrollbar_wight). */
    val TouchTargetWidth: Dp = 14.dp

    /** The visible thumb width while dragged: the 14dp rect minus two 4dp pressed insets. */
    val DragThumbWidth: Dp = 6.dp

    /**
     * The padding from the end edge while dragging (COUI coui_scrollbar_drawable_pressed_inset).
     */
    val DragEndPadding: Dp = 4.dp

    /**
     * Creates [ScrollBarColors]. A [Color.Unspecified] thumb resolves to `COUITheme.colorScheme.dragHandle`
     * (#29000000 / #40FFFFFF, the same values as COUI `coui_scrollbar_color`), used both at rest and
     * while dragged.
     */
    fun scrollBarColors(
        thumbColor: Color = Color.Unspecified,
        trackColor: Color = Color.Unspecified,
    ): ScrollBarColors = ScrollBarColors(
        thumbColor = thumbColor,
        trackColor = trackColor,
    )
}

@OptIn(ExperimentalScrollBarApi::class)
@Composable
private fun ScrollBar(
    adapter: ScrollBarAdapter,
    isVertical: Boolean,
    modifier: Modifier = Modifier,
    reverseLayout: Boolean = false,
    trackPadding: PaddingValues = PaddingValues(0.dp),
    colors: ScrollBarColors = ScrollBarDefaults.scrollBarColors(),
    thumbWidth: Dp = ScrollBarDefaults.ThumbWidth,
    cornerRadius: Dp = ScrollBarDefaults.CornerRadius,
    thumbMinLength: Dp = ScrollBarDefaults.ThumbMinLength,
    endPadding: Dp = ScrollBarDefaults.EndPadding,
) {
    val density = LocalDensity.current
    // COUIScrollBar puts the vertical thumb on the left in RTL (mIsRTL).
    val rtl = isVertical && LocalLayoutDirection.current == LayoutDirection.Rtl
    // COUI draws the normal and the pressed thumb in the same coui_scrollbar_color.
    val thumbColor = colors.thumbColor.takeOrElse { COUITheme.colorScheme.dragHandle }

    val minThumbPx = with(density) { thumbMinLength.toPx() }
    val coroutineScope = rememberCoroutineScope()

    val beforeTrackPaddingPx = with(density) {
        if (isVertical) {
            trackPadding.calculateTopPadding()
                .toPx()
        } else {
            trackPadding.calculateLeftPadding(androidx.compose.ui.unit.LayoutDirection.Ltr).toPx()
        }
    }
    val afterTrackPaddingPx = with(density) {
        if (isVertical) {
            trackPadding.calculateBottomPadding()
                .toPx()
        } else {
            trackPadding.calculateRightPadding(androidx.compose.ui.unit.LayoutDirection.Ltr).toPx()
        }
    }
    val totalTrackPaddingPx = beforeTrackPaddingPx + afterTrackPaddingPx

    var containerSize by remember { mutableIntStateOf(0) }

    val sliderAdapter = remember(adapter, minThumbPx, reverseLayout, isVertical, coroutineScope) {
        SliderAdapter(adapter, 0, minThumbPx, reverseLayout, isVertical, coroutineScope)
    }
    LaunchedEffect(containerSize, totalTrackPaddingPx) {
        sliderAdapter.trackSize = (containerSize - totalTrackPaddingPx.fastRoundToInt()).coerceAtLeast(0)
    }

    // A mouse over the thumb strip (not the whole bar, which may span the content).
    var isHovered by remember { mutableStateOf(false) }
    var opacity by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    val isHighlighted = isHovered || isDragging
    val fadeOut = remember { ScrollBarFadeOut(coroutineScope) { opacity = it } }

    // COUI swaps the StateListDrawable state, so the pressed thumb widens (and its end inset
    // shrinks from 5.5dp to 4dp) at once, without an enter or exit animation.
    val dragEndPaddingRaw = endPadding - (ScrollBarDefaults.EndPadding - ScrollBarDefaults.DragEndPadding)
    val dragEndPadding = if (dragEndPaddingRaw < 0.dp) 0.dp else dragEndPaddingRaw
    val currentThumbWidth = if (isHighlighted) ScrollBarDefaults.DragThumbWidth else thumbWidth
    val currentEndPadding = if (isHighlighted) dragEndPadding else endPadding

    // COUIScrollBar.onAttachedToWindow: the scrollbar flashes once when it appears.
    LaunchedEffect(sliderAdapter) {
        snapshotFlow { sliderAdapter.trackSize > 0 && sliderAdapter.thumbSize < sliderAdapter.trackSize }
            .first { it }
        if (!isHighlighted) {
            opacity = 1f
            fadeOut.schedule(ScrollBarDefaults.InitialFadeDelayMillis)
        }
    }

    LaunchedEffect(isHighlighted) {
        if (isHighlighted) {
            // Dragging keeps the thumb fully opaque and cancels the pending fade.
            fadeOut.cancel()
            opacity = 1f
        } else if (opacity > 0f) {
            // On release COUI calls awakenScrollBars(), restarting the 2000ms timeout.
            fadeOut.schedule(ScrollBarDefaults.FadeDelayMillis)
        }
    }

    LaunchedEffect(adapter) {
        snapshotFlow { adapter.scrollOffset }
            .drop(1)
            .collect {
                // Showing is instant: the scroll cache goes straight to ON with alpha 255.
                opacity = 1f
                if (!isHighlighted) fadeOut.schedule(ScrollBarDefaults.FadeDelayMillis)
            }
    }

    val measurePolicy = if (isVertical) {
        verticalMeasurePolicy(
            setContainerSize = { containerSize = it },
            density = density,
            endPadding = endPadding,
            thumbWidth = thumbWidth,
        )
    } else {
        horizontalMeasurePolicy(
            setContainerSize = { containerSize = it },
            density = density,
            endPadding = endPadding,
            thumbWidth = thumbWidth,
        )
    }

    Layout(
        content = {},
        modifier = modifier
            // COUIScrollBar lives inside the scrolled view and only takes a touch that lands on the
            // shown thumb (onInterceptTouchEvent); the content below keeps every other touch.
            .scrollBarPointerInput(isVertical, sliderAdapter, beforeTrackPaddingPx, rtl) {
                val touchTargetPx = ScrollBarDefaults.TouchTargetWidth.toPx()
                fun inStrip(position: Offset): Boolean = when {
                    !isVertical -> position.y >= size.height - touchTargetPx
                    rtl -> position.x <= touchTargetPx
                    else -> position.x >= size.width - touchTargetPx
                }

                coroutineScope {
                    launch {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent(PointerEventPass.Initial)
                                isHovered = event.type != PointerEventType.Exit &&
                                    event.changes.any { it.type == PointerType.Mouse && inStrip(it.position) }
                            }
                        }
                    }

                    // The Initial pass reaches the bar before the content below it, so a thumb drag
                    // is consumed before the content can start scrolling with it.
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)

                        // COUIScrollBar only grabs the thumb while it is shown (scroll cache not OFF).
                        if (opacity <= 0f || !inStrip(down.position)) return@awaitEachGesture

                        // The hit area is the whole thumb rect, its 8dp / 16dp margins included.
                        val thumbRange = sliderAdapter.thumbPixelRange
                        val touchPos = if (isVertical) down.position.y else down.position.x
                        val adjustedTouchPos = touchPos - beforeTrackPaddingPx
                        if (adjustedTouchPos < thumbRange.first || adjustedTouchPos > thumbRange.last) {
                            return@awaitEachGesture
                        }

                        isDragging = true
                        opacity = 1f
                        fadeOut.cancel()
                        down.consume()

                        sliderAdapter.onDragStarted()

                        try {
                            while (true) {
                                val event = awaitPointerEvent(PointerEventPass.Initial)
                                val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                if (!change.pressed) {
                                    change.consume()
                                    break
                                }
                                val delta = change.positionChange()
                                if (delta != Offset.Zero) sliderAdapter.onDragDelta(delta)
                                change.consume()
                            }
                        } finally {
                            isDragging = false
                        }
                    }
                }
            }
            .drawBehind {
                if (containerSize == 0 || opacity <= 0f) return@drawBehind
                if (sliderAdapter.thumbSize >= sliderAdapter.trackSize) return@drawBehind

                val thumbWidthPx = currentThumbWidth.toPx()
                val endPaddingPx = currentEndPadding.toPx()
                val cornerRadiusPx = if (cornerRadius == Dp.Unspecified) thumbWidthPx / 2 else cornerRadius.toPx()
                val cr = CornerRadius(cornerRadiusPx)
                val thumbX = if (rtl) endPaddingPx else size.width - thumbWidthPx - endPaddingPx

                if (colors.trackColor != Color.Unspecified) {
                    val trackColor = colors.trackColor.copy(alpha = colors.trackColor.alpha * opacity)
                    if (isVertical) {
                        drawRoundRect(
                            color = trackColor,
                            topLeft = Offset(thumbX, beforeTrackPaddingPx),
                            size = Size(thumbWidthPx, size.height - totalTrackPaddingPx),
                            cornerRadius = cr,
                        )
                    } else {
                        drawRoundRect(
                            color = trackColor,
                            topLeft = Offset(beforeTrackPaddingPx, size.height - thumbWidthPx - endPaddingPx),
                            size = Size(size.width - totalTrackPaddingPx, thumbWidthPx),
                            cornerRadius = cr,
                        )
                    }
                }

                // COUI has no length animation: the rect follows the extent directly, and the
                // visible thumb sits inside it, 8dp from its start and 16dp from its end.
                val marginStartPx = ScrollBarDefaults.ThumbMarginStart.toPx()
                val marginEndPx = ScrollBarDefaults.ThumbMarginEnd.toPx()
                val rectLength = sliderAdapter.thumbSize.toFloat()
                val thumbLength = (rectLength - marginStartPx - marginEndPx).coerceAtLeast(0f)
                if (thumbLength <= 0f) return@drawBehind
                val thumbOffset = beforeTrackPaddingPx + sliderAdapter.position.toFloat() + marginStartPx
                val color = thumbColor.copy(alpha = thumbColor.alpha * opacity)

                if (isVertical) {
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(thumbX, thumbOffset),
                        size = Size(thumbWidthPx, thumbLength),
                        cornerRadius = cr,
                    )
                } else {
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(thumbOffset, size.height - thumbWidthPx - endPaddingPx),
                        size = Size(thumbLength, thumbWidthPx),
                        cornerRadius = cr,
                    )
                }
            },
        measurePolicy = measurePolicy,
    )
}

/**
 * The COUIScrollBar fade: after a delay the thumb alpha runs linearly from opaque to transparent
 * over [ScrollBarDefaults.FadeDurationMillis] (the scroll cache keyframe interpolator).
 */
@OptIn(ExperimentalScrollBarApi::class)
private class ScrollBarFadeOut(
    private val scope: CoroutineScope,
    private val setOpacity: (Float) -> Unit,
) {
    private var job: Job? = null

    fun schedule(delayMillis: Int) {
        job?.cancel()
        job = scope.launch {
            delay(delayMillis.toLong())
            animate(
                initialValue = 1f,
                targetValue = 0f,
                animationSpec = tween(ScrollBarDefaults.FadeDurationMillis, easing = LinearEasing),
            ) { value, _ ->
                setOpacity(value)
            }
        }
    }

    fun cancel() {
        job?.cancel()
        job = null
    }
}

// A bar given a wider minimum (Modifier.matchParentSize over the content) fills it and keeps its
// thumb at the end edge, instead of being centred by the layout at its own strip width.
private fun verticalMeasurePolicy(
    setContainerSize: (Int) -> Unit,
    density: androidx.compose.ui.unit.Density,
    endPadding: Dp,
    thumbWidth: Dp,
) = androidx.compose.ui.layout.MeasurePolicy { _, constraints ->
    setContainerSize(constraints.maxHeight)
    val width = with(density) {
        (thumbWidth + endPadding * 2).roundToPx()
    }.coerceIn(constraints.minWidth, constraints.maxWidth)
    layout(width, constraints.maxHeight) {}
}

private fun horizontalMeasurePolicy(
    setContainerSize: (Int) -> Unit,
    density: androidx.compose.ui.unit.Density,
    endPadding: Dp,
    thumbWidth: Dp,
) = androidx.compose.ui.layout.MeasurePolicy { _, constraints ->
    setContainerSize(constraints.maxWidth)
    val height = with(density) {
        (thumbWidth + endPadding * 2).roundToPx()
    }.coerceIn(constraints.minHeight, constraints.maxHeight)
    layout(constraints.maxWidth, height) {}
}

/**
 * A `Modifier.pointerInput` whose events also reach the siblings below the bar. The bar is laid
 * over the content it scrolls, and Compose stops hit testing at the first sibling with pointer
 * input, so a plain `pointerInput` would keep every touch in the bar's strip from scrolling the
 * content. Like `pointerInput`, [block] restarts when a key changes.
 */
private fun Modifier.scrollBarPointerInput(
    key1: Any?,
    key2: Any?,
    key3: Any?,
    key4: Any?,
    block: suspend PointerInputScope.() -> Unit,
): Modifier = this then ScrollBarPointerInputElement(key1, key2, key3, key4, block)

private class ScrollBarPointerInputElement(
    private val key1: Any?,
    private val key2: Any?,
    private val key3: Any?,
    private val key4: Any?,
    private val block: suspend PointerInputScope.() -> Unit,
) : ModifierNodeElement<ScrollBarPointerInputNode>() {
    override fun create(): ScrollBarPointerInputNode = ScrollBarPointerInputNode(block)

    override fun update(node: ScrollBarPointerInputNode) {
        node.update(block)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ScrollBarPointerInputElement) return false
        return key1 == other.key1 && key2 == other.key2 && key3 == other.key3 && key4 == other.key4
    }

    override fun hashCode(): Int {
        var result = key1?.hashCode() ?: 0
        result = 31 * result + (key2?.hashCode() ?: 0)
        result = 31 * result + (key3?.hashCode() ?: 0)
        result = 31 * result + (key4?.hashCode() ?: 0)
        return result
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "scrollBarPointerInput"
        properties["key1"] = key1
        properties["key2"] = key2
        properties["key3"] = key3
        properties["key4"] = key4
    }
}

private class ScrollBarPointerInputNode(
    private var block: suspend PointerInputScope.() -> Unit,
) : DelegatingNode(),
    PointerInputModifierNode {
    private val pointerInputNode = delegate(SuspendingPointerInputModifierNode { block(this) })

    fun update(block: suspend PointerInputScope.() -> Unit) {
        this.block = block
        pointerInputNode.resetPointerInputHandler()
    }

    override fun onPointerEvent(pointerEvent: PointerEvent, pass: PointerEventPass, bounds: IntSize) {
        pointerInputNode.onPointerEvent(pointerEvent, pass, bounds)
    }

    override fun onCancelPointerInput() {
        pointerInputNode.onCancelPointerInput()
    }

    override fun onDensityChange() {
        pointerInputNode.onDensityChange()
    }

    override fun onViewConfigurationChange() {
        pointerInputNode.onViewConfigurationChange()
    }

    override fun sharePointerInputWithSiblings(): Boolean = true
}

@OptIn(ExperimentalScrollBarApi::class)
@Stable
private class SliderAdapter(
    val adapter: ScrollBarAdapter,
    trackSize: Int,
    private val minThumbSize: Float,
    private val reverseLayout: Boolean,
    private val isVertical: Boolean,
    private val coroutineScope: CoroutineScope,
) {
    // State-backed so a draw that ran before the track was measured is redone once it is.
    var trackSize by mutableIntStateOf(trackSize)

    private val contentSize get() = adapter.contentSize

    private val visiblePart: Double
        get() {
            val cs = contentSize
            return if (cs == 0.0) 1.0 else (adapter.viewportSize / cs).coerceAtMost(1.0)
        }

    val thumbSize: Double
        get() = (trackSize * visiblePart).coerceAtLeast(minThumbSize.toDouble())

    private val scrollScale: Double
        get() {
            val extraBar = trackSize - thumbSize
            val extraContent = adapter.maxScrollOffset
            return if (extraContent == 0.0) 1.0 else extraBar / extraContent
        }

    private val rawPosition: Double
        get() = scrollScale * adapter.scrollOffset

    val position: Double
        get() = if (reverseLayout) trackSize - thumbSize - rawPosition else rawPosition

    val bounds get() = position..position + thumbSize

    private var unscrolledDragDistance = 0.0
    private val dragMutex = Mutex()

    fun onDragStarted() {
        unscrolledDragDistance = 0.0
    }

    fun onDragDelta(offset: Offset) {
        coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
            dragMutex.withLock {
                val dragDelta = if (isVertical) offset.y else offset.x
                val maxPosition = adapter.maxScrollOffset * scrollScale
                val currentPosition = position
                val targetPosition =
                    (currentPosition + dragDelta + unscrolledDragDistance).coerceIn(0.0, maxPosition)
                val sliderDelta = targetPosition - currentPosition

                setPosition(currentPosition + sliderDelta)
                unscrolledDragDistance += dragDelta - sliderDelta
            }
        }
    }

    private suspend fun setPosition(value: Double) {
        val rawPosition = if (reverseLayout) trackSize - thumbSize - value else value
        adapter.scrollTo(rawPosition / scrollScale)
    }
}

private val SliderAdapter.thumbPixelRange: IntRange
    get() {
        val start = position.fastRoundToInt()
        val endExclusive = start + thumbSize.fastRoundToInt()
        return start until endExclusive
    }

@OptIn(ExperimentalScrollBarApi::class)
private class ScrollStateAdapter(
    private val scrollState: ScrollState,
) : ScrollBarAdapter {

    override val scrollOffset: Double get() = scrollState.value.toDouble()

    override val contentSize: Double get() = scrollState.maxValue + viewportSize

    override val viewportSize: Double get() = scrollState.viewportSize.toDouble()

    override suspend fun scrollTo(scrollOffset: Double) {
        scrollState.scrollTo(scrollOffset.fastRoundToInt())
    }
}

@OptIn(ExperimentalScrollBarApi::class)
private abstract class LazyLineContentAdapter : ScrollBarAdapter {

    data class VisibleLine(val index: Int, val offset: Int)

    abstract fun firstVisibleLine(): VisibleLine?
    abstract fun totalLineCount(): Int
    abstract fun contentPadding(): Int
    abstract suspend fun snapToLine(lineIndex: Int, scrollOffset: Int)
    abstract suspend fun scrollBy(value: Float)
    abstract fun averageVisibleLineSize(): Double
    abstract val lineSpacing: Int

    private val averageVisibleLineSize: Double
        get() = if (totalLineCount() == 0) 0.0 else averageVisibleLineSize()

    private val averageVisibleLineSizeWithSpacing: Double
        get() = averageVisibleLineSize + lineSpacing

    override val scrollOffset: Double
        get() {
            val firstVisibleLine = firstVisibleLine()
            return if (firstVisibleLine == null) {
                0.0
            } else {
                firstVisibleLine.index * averageVisibleLineSizeWithSpacing - firstVisibleLine.offset
            }
        }

    override val contentSize: Double
        get() {
            val totalLineCount = totalLineCount()
            return averageVisibleLineSize * totalLineCount +
                lineSpacing * (totalLineCount - 1).coerceAtLeast(0) +
                contentPadding()
        }

    override suspend fun scrollTo(scrollOffset: Double) {
        val distance = scrollOffset - this.scrollOffset
        if (abs(distance) <= viewportSize) {
            scrollBy(distance.toFloat())
        } else {
            snapTo(scrollOffset)
        }
    }

    private suspend fun snapTo(scrollOffset: Double) {
        val scrollOffsetCoerced = scrollOffset.coerceIn(0.0, maxScrollOffset)
        val avgWithSpacing = averageVisibleLineSizeWithSpacing
        if (avgWithSpacing <= 0.0) return

        val index = (scrollOffsetCoerced / avgWithSpacing)
            .toInt()
            .coerceAtLeast(0)
            .coerceAtMost(totalLineCount() - 1)

        val offset = (scrollOffsetCoerced - index * avgWithSpacing)
            .toInt()
            .coerceAtLeast(0)

        snapToLine(lineIndex = index, scrollOffset = offset)
    }
}

private class LazyListAdapter(
    private val scrollState: LazyListState,
) : LazyLineContentAdapter() {

    override val viewportSize: Double
        get() = with(scrollState.layoutInfo) {
            if (orientation == Orientation.Vertical) viewportSize.height else viewportSize.width
        }.toDouble()

    override val lineSpacing: Int get() = scrollState.layoutInfo.mainAxisItemSpacing

    private fun firstFloatingVisibleItemIndex(): Int? = with(scrollState.layoutInfo.visibleItemsInfo) {
        when (size) {
            0 -> null

            1 -> 0

            else -> {
                val first = this[0]
                val second = this[1]
                if ((first.index < second.index - 1) ||
                    (first.offset + first.size + lineSpacing > second.offset)
                ) {
                    1
                } else {
                    0
                }
            }
        }
    }

    override fun firstVisibleLine(): VisibleLine? {
        val idx = firstFloatingVisibleItemIndex() ?: return null
        val item = scrollState.layoutInfo.visibleItemsInfo[idx]
        return VisibleLine(index = item.index, offset = item.offset)
    }

    override fun totalLineCount(): Int = scrollState.layoutInfo.totalItemsCount

    override fun contentPadding(): Int = with(scrollState.layoutInfo) {
        beforeContentPadding + afterContentPadding
    }

    override suspend fun snapToLine(lineIndex: Int, scrollOffset: Int) {
        scrollState.scrollToItem(lineIndex, scrollOffset)
    }

    override suspend fun scrollBy(value: Float) {
        scrollState.scrollBy(value)
    }

    override fun averageVisibleLineSize(): Double {
        return with(scrollState.layoutInfo.visibleItemsInfo) {
            val firstIdx = firstFloatingVisibleItemIndex() ?: return@with 0.0
            val first = this[firstIdx]
            val last = last()
            val count = size - firstIdx
            (last.offset + last.size - first.offset - (count - 1) * lineSpacing).toDouble() / count
        }
    }
}

private class LazyGridAdapter(
    private val scrollState: LazyGridState,
) : LazyLineContentAdapter() {

    private val isVertical = scrollState.layoutInfo.orientation == Orientation.Vertical

    override val viewportSize: Double
        get() = with(scrollState.layoutInfo) {
            if (orientation == Orientation.Vertical) viewportSize.height else viewportSize.width
        }.toDouble()

    override val lineSpacing: Int get() = scrollState.layoutInfo.mainAxisItemSpacing

    private val unknownLine: Int
        get() = with(LazyGridItemInfo) {
            if (isVertical) UnknownRow else UnknownColumn
        }

    private fun LazyGridItemInfo.line(): Int = if (isVertical) row else column

    private fun LazyGridItemInfo.mainAxisSize(): Int = if (isVertical) size.height else size.width

    private fun LazyGridItemInfo.mainAxisOffset(): Int = if (isVertical) offset.y else offset.x

    private fun lineOfIndex(index: Int) = index / scrollState.slotsPerLine

    private fun indexOfFirstInLine(line: Int) = line * scrollState.slotsPerLine

    override fun firstVisibleLine(): VisibleLine? {
        val items = scrollState.layoutInfo.visibleItemsInfo
        val firstItem = items.firstOrNull { it.line() != unknownLine } ?: return null
        return VisibleLine(index = firstItem.line(), offset = firstItem.mainAxisOffset())
    }

    override fun totalLineCount(): Int {
        val itemCount = scrollState.layoutInfo.totalItemsCount
        return if (itemCount == 0) 0 else lineOfIndex(itemCount - 1) + 1
    }

    override fun contentPadding(): Int = with(scrollState.layoutInfo) {
        beforeContentPadding + afterContentPadding
    }

    override suspend fun snapToLine(lineIndex: Int, scrollOffset: Int) {
        scrollState.scrollToItem(
            index = indexOfFirstInLine(lineIndex),
            scrollOffset = scrollOffset,
        )
    }

    override suspend fun scrollBy(value: Float) {
        scrollState.scrollBy(value)
    }

    override fun averageVisibleLineSize(): Double {
        val items = scrollState.layoutInfo.visibleItemsInfo
        val firstKnownIdx = items.indexOfFirst { it.line() != unknownLine }
        if (firstKnownIdx == -1) return 0.0
        val realItems = items.subList(firstKnownIdx, items.size)

        val lastLine = realItems.last().line()
        val lastLineSize = realItems.asReversed().asSequence()
            .takeWhile { it.line() == lastLine }
            .maxOf { it.mainAxisSize() }

        val first = realItems.first()
        val last = realItems.last()
        val lineCount = last.line() - first.line() + 1
        val spacingSum = (lineCount - 1) * lineSpacing
        return (last.mainAxisOffset() + lastLineSize - first.mainAxisOffset() - spacingSum).toDouble() / lineCount
    }
}

private val LazyGridState.slotsPerLine: Int
    get() = with(layoutInfo) {
        var count = 0
        for (item in visibleItemsInfo) {
            val index = if (orientation == Orientation.Vertical) item.column else item.row
            if (index == -1) break
            if (count == index) count += 1 else break
        }
        count.coerceAtLeast(1)
    }
