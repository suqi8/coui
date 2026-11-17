// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.utils.CouiHapticEffect
import io.github.suqi8.coui.kmp.utils.rememberCouiHaptic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * An [AlphabetIndexBar] component with COUI style.
 *
 * The side letter index of contact and app lists, mirroring ColorOS's COUITouchSearchView: an
 * optional favorites star followed by one 16dp row per key (A to Z and `#` by default), drawn
 * in 12dp medium couiColorSecondNeutral and centered in a 39dp wide column. Touching or
 * dragging along the bar selects the key under the finger, highlights it with
 * couiColorPrimaryTextOnPopup, reports it through [onKeySelected], ticks a haptic and shows a
 * 48dp bubble with the key 8.5dp beside the bar. The bubble grows from 0.8 and fades in over
 * 350ms, and hides again (to 0.8, transparent, 300ms) one second after the finger lifts.
 *
 * Keys outside [availableKeys] cannot be selected: touching one selects the closest available
 * key above it, like COUITouchSearchView's `hasValue` keys. When the height is too small for
 * every key, the bar collapses runs of keys into `·` dots following COUITouchSearchView's dot
 * levels; dragging over a dot walks through the keys it hides. Below the smallest level the bar
 * draws nothing.
 *
 * The bubble is placed outside the bar's bounds (COUI uses a PopupWindow), so put the bar above
 * the list it indexes (for example as the last child of a `Box`) and keep any clipping ancestor,
 * such as a `Card`, large enough for the bubble and its shadow.
 *
 * @param onKeySelected Called with the newly selected key while touching the bar, including
 *   [AlphabetIndexBarDefaults.CollectKey] for the star.
 * @param modifier The modifier to be applied to the [AlphabetIndexBar].
 * @param selectedKey The key to highlight while the bar is not touched, typically the section of
 *   the first visible list item, or `null` to leave the highlight to touches alone. Like
 *   COUITouchSearchView.updateMoveTouchBarText it moves the highlight without a haptic or the
 *   bubble, follows a key hidden behind a dot to the row that holds it, and is ignored while a
 *   finger is on the bar; after the finger lifts the touched key stays highlighted until
 *   [selectedKey] changes.
 * @param keys The index keys, top to bottom (COUI normal_touchsearch_keys without the star).
 * @param availableKeys The keys that can be selected, or `null` for all of them.
 * @param showCollectKey Whether the favorites star leads the keys (COUI couiFirstIsCharacter false).
 * @param hapticFeedbackEnabled Whether moving to another key ticks a haptic.
 * @param colors The [AlphabetIndexBarColors] of the bar and its bubble.
 */
@Composable
fun AlphabetIndexBar(
    onKeySelected: (key: String) -> Unit,
    modifier: Modifier = Modifier,
    selectedKey: String? = null,
    keys: List<String> = AlphabetIndexBarDefaults.Keys,
    availableKeys: Set<String>? = null,
    showCollectKey: Boolean = true,
    hapticFeedbackEnabled: Boolean = true,
    colors: AlphabetIndexBarColors = AlphabetIndexBarDefaults.alphabetIndexBarColors(),
) {
    val allKeys = remember(keys, showCollectKey) {
        if (showCollectKey) listOf(AlphabetIndexBarDefaults.CollectKey) + keys else keys
    }
    val hasValue = remember(allKeys, availableKeys) {
        BooleanArray(allKeys.size) { availableKeys == null || allKeys[it] in availableKeys }
    }
    val currentOnKeySelected by rememberUpdatedState(onKeySelected)
    // COUITouchSearchView.performFeedback: GRANULAR_SHORT_VIBRATE_SYNC (308), falling back to 302.
    val haptic by rememberUpdatedState(rememberCouiHaptic())
    val currentHapticEnabled by rememberUpdatedState(hapticFeedbackEnabled)
    val scope = rememberCoroutineScope()
    val textMeasurer = rememberTextMeasurer(cacheSize = allKeys.size + 2)
    val state = remember { IndexBarState(selectedKey) }
    val starPainter = rememberVectorPainter(CollectStarIcon)

    // COUITouchSearchView.updateMoveTouchBarText: ignored while touching.
    LaunchedEffect(selectedKey) {
        if (!state.inTouch) state.externalKey = selectedKey
    }

    Layout(
        modifier = modifier
            .semantics {
                customActions = allKeys.filterIndexed { index, _ -> hasValue[index] }.map { key ->
                    CustomAccessibilityAction(key) {
                        currentOnKeySelected(key)
                        true
                    }
                }
            }
            .pointerInput(allKeys, hasValue, showCollectKey) {
                fun handle(rows: IndexRows, y: Float, isMove: Boolean) {
                    val hit = rows.select(y, hasValue) ?: return
                    val key = allKeys[hit.originalIndex]
                    if (key == state.displayKey) return
                    state.displayKey = key
                    if (hit.rowIndex != state.selectedRow && currentHapticEnabled) {
                        haptic(CouiHapticEffect.Strength)
                    }
                    state.selectedRow = hit.rowIndex
                    val row = rows.rows[hit.rowIndex]
                    state.bubbleText = key
                    state.bubbleIsCollect = showCollectKey && hit.originalIndex == 0
                    state.bubbleCenterY = (row.top + row.bottom) / 2f
                    currentOnKeySelected(key)
                    state.showBubble(scope, snapAlpha = isMove)
                }
                awaitEachGesture {
                    val down = awaitFirstDown()
                    down.consume()
                    // The rows only depend on the height, so build them once per gesture.
                    val rows = buildIndexRows(allKeys.size, showCollectKey, size.height.toFloat(), this)
                    // Touches take over from selectedKey: start from the row it highlights.
                    state.selectedRow = state.highlightedRow(rows, allKeys, hasValue)
                    state.externalKey = null
                    state.inTouch = true
                    state.resetForTouch(scope)
                    try {
                        handle(rows, down.position.y, isMove = false)
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                            if (!change.pressed) {
                                change.consume()
                                break
                            }
                            change.consume()
                            handle(rows, change.position.y, isMove = true)
                        }
                    } finally {
                        state.inTouch = false
                        state.displayKey = ""
                        state.hideBubbleLater(scope)
                    }
                }
            }
            .drawWithCache {
                val rows = buildIndexRows(allKeys.size, showCollectKey, size.height, this)
                val keyStyle = TextStyle(fontSize = AlphabetIndexBarDefaults.KeyTextSize.toSp(), fontWeight = FontWeight.Medium)
                val dotStyle = TextStyle(fontSize = AlphabetIndexBarDefaults.DotTextSize.toSp(), fontWeight = FontWeight.Medium)
                val layouts: List<TextLayoutResult?> = rows.rows.map { row ->
                    when {
                        row.isCollect -> null
                        row.isDot -> textMeasurer.measure(DOT_TEXT, dotStyle, softWrap = false, maxLines = 1)
                        else -> textMeasurer.measure(allKeys[row.originalIndex], keyStyle, softWrap = false, maxLines = 1)
                    }
                }
                val starSize = AlphabetIndexBarDefaults.CollectIconSize.toPx()
                val starFilter = ColorFilter.tint(colors.keyColor)
                val selectedStarFilter = ColorFilter.tint(colors.selectedKeyColor)
                onDrawBehind {
                    val selected = state.highlightedRow(rows, allKeys, hasValue)
                    rows.rows.forEachIndexed { index, row ->
                        val isSelected = index == selected
                        val centerY = (row.top + row.bottom) / 2f
                        if (row.isCollect) {
                            translate(left = (size.width - starSize) / 2f, top = row.top) {
                                with(starPainter) {
                                    draw(
                                        size = Size(starSize, starSize),
                                        colorFilter = if (isSelected) selectedStarFilter else starFilter,
                                    )
                                }
                            }
                        } else {
                            val layout = layouts[index] ?: return@forEachIndexed
                            val color = when {
                                isSelected -> colors.selectedKeyColor
                                row.isDot -> colors.dotColor
                                else -> colors.keyColor
                            }
                            drawText(
                                textLayoutResult = layout,
                                color = color,
                                topLeft = Offset((size.width - layout.size.width) / 2f, centerY - layout.size.height / 2f),
                            )
                        }
                    }
                }
            },
        content = {
            IndexBubble(
                text = state.bubbleText,
                isCollect = state.bubbleIsCollect,
                colors = colors,
            )
        },
    ) { measurables, constraints ->
        val width = constraints.constrainWidth(AlphabetIndexBarDefaults.Width.roundToPx())
        val neededHeight = buildIndexRows(allKeys.size, showCollectKey, Float.MAX_VALUE, this).fullHeight
        val height = constraints.constrainHeight(neededHeight.roundToInt())
        val bubbleSize = AlphabetIndexBarDefaults.BubbleSize.roundToPx()
        val bubble = measurables.first().measure(Constraints.fixed(bubbleSize, bubbleSize))
        layout(width, height) {
            val x = -(AlphabetIndexBarDefaults.BubbleGap.roundToPx() + bubbleSize)
            val y = (state.bubbleCenterY - bubbleSize / 2f).roundToInt()
            bubble.placeRelativeWithLayer(x, y) {
                alpha = state.bubbleAlpha.value
                scaleX = state.bubbleScale.value
                scaleY = state.bubbleScale.value
            }
        }
    }
}

@Composable
private fun IndexBubble(
    text: String,
    isCollect: Boolean,
    colors: AlphabetIndexBarColors,
) {
    // COUITouchSearchView sizes the bubble key with COUIChangeTextUtil.getSuitableFontSize level 4:
    // the font scale is capped at 1.35 so large fonts stay inside the 48dp bubble.
    val fontScale = LocalDensity.current.fontScale
    val baseStyle = COUITheme.textStyles.headlineM
    val textStyle = remember(baseStyle, fontScale) {
        baseStyle.copy(fontSize = baseStyle.fontSize * (min(fontScale, BUBBLE_MAX_FONT_SCALE) / fontScale))
    }
    Box(
        modifier = Modifier
            .dropShadow(CircleShape, BubbleShadow)
            .background(colors.bubbleColor, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (isCollect) {
            Icon(
                imageVector = CollectStarPopupIcon,
                contentDescription = null,
                tint = colors.bubbleContentColor,
            )
        } else {
            Text(
                text = text,
                color = colors.bubbleContentColor,
                style = textStyle,
                maxLines = 1,
            )
        }
    }
}

/** The touch and bubble state of an [AlphabetIndexBar]. */
@Stable
private class IndexBarState(initialExternalKey: String?) {
    var selectedRow by mutableIntStateOf(-1)

    /** The selectedKey to highlight, or `null` once a touch has taken over. */
    var externalKey by mutableStateOf(initialExternalKey)

    /** COUITouchSearchView mInTouching. */
    var inTouch = false

    /**
     * The highlighted row: [externalKey] located like COUITouchSearchView.updateMoveTouchBarText
     * (getWillDisplayY, then getKeyIndices / getKeyIndicesWithDots), or the last touched row.
     */
    fun highlightedRow(rows: IndexRows, allKeys: List<String>, hasValue: BooleanArray): Int {
        val key = externalKey ?: return selectedRow
        val y = rows.displayY(key, allKeys) ?: return selectedRow
        return rows.select(y, hasValue)?.rowIndex ?: selectedRow
    }
    var bubbleText by mutableStateOf("")
    var bubbleIsCollect by mutableStateOf(false)
    var bubbleCenterY by mutableFloatStateOf(0f)

    /** COUITouchSearchView mDisplayKey, cleared when the finger lifts. */
    var displayKey = ""
    val bubbleAlpha = Animatable(0f)
    val bubbleScale = Animatable(BUBBLE_MIN_SCALE)
    private var appearJob: Job? = null
    private var hideJob: Job? = null

    /** ACTION_DOWN: cancel the pending dismissal and any bubble animation, then reset it. */
    fun resetForTouch(scope: CoroutineScope) {
        hideJob?.cancel()
        appearJob?.cancel()
        scope.launch {
            bubbleAlpha.snapTo(0f)
            bubbleScale.snapTo(BUBBLE_MIN_SCALE)
        }
    }

    /** COUITouchSearchView.onKeyChanged + startFirstAnimationToShow. */
    fun showBubble(scope: CoroutineScope, snapAlpha: Boolean) {
        hideJob?.cancel()
        if (snapAlpha) scope.launch { bubbleAlpha.snapTo(1f) }
        if (appearJob?.isActive == true) return
        appearJob = scope.launch {
            launch { bubbleAlpha.animateTo(1f, tween(BUBBLE_APPEAR_MILLIS, easing = MoveEase)) }
            launch { bubbleScale.animateTo(1f, tween(BUBBLE_APPEAR_MILLIS, easing = MoveEase)) }
        }
    }

    /** COUITouchSearchView.startFirstAnimationToDismiss: one second later, fade and shrink. */
    fun hideBubbleLater(scope: CoroutineScope) {
        hideJob?.cancel()
        hideJob = scope.launch {
            delay(BUBBLE_DISMISS_DELAY_MILLIS)
            appearJob?.cancel()
            launch { bubbleAlpha.animateTo(0f, tween(BUBBLE_DISAPPEAR_MILLIS, easing = MoveEase)) }
            launch { bubbleScale.animateTo(BUBBLE_MIN_SCALE, tween(BUBBLE_DISAPPEAR_MILLIS, easing = MoveEase)) }
        }
    }
}

/** One drawn row: a key, the star, or a dot hiding [hidden] keys. */
private class IndexRow(
    val originalIndex: Int,
    val isCollect: Boolean,
    val isDot: Boolean,
    val hidden: IntArray,
    val top: Float,
    val bottom: Float,
)

private class IndexHit(val rowIndex: Int, val originalIndex: Int)

private class IndexRows(val rows: List<IndexRow>, val compressed: Boolean, val fullHeight: Float) {
    /**
     * COUITouchSearchView.getWillDisplayY: the center of [key]'s row, or for a key hidden behind a
     * dot, its slot within the dot; `null` when the key is not drawn.
     */
    fun displayY(key: String, allKeys: List<String>): Float? {
        rows.forEach { row ->
            if (row.isDot) {
                val count = row.hidden.size
                val step = (row.bottom - row.top) / count.coerceAtLeast(1)
                for (j in 0 until count) {
                    if (allKeys[row.hidden[j]] == key) return min(row.top + step * (j + 1), row.bottom)
                }
            } else if (allKeys[row.originalIndex] == key) {
                return (row.top + row.bottom) / 2f
            }
        }
        return null
    }

    /** COUITouchSearchView.getKeyIndices / getKeyIndicesWithDots. */
    fun select(y: Float, hasValue: BooleanArray): IndexHit? {
        if (rows.isEmpty()) return null
        if (!compressed) {
            if ((rows.size == 1 || rows[1].top > y) && hasValue[rows[0].originalIndex]) return IndexHit(0, rows[0].originalIndex)
            val last = rows.size - 1
            if (y > rows[last].top && hasValue[rows[last].originalIndex]) return IndexHit(last, rows[last].originalIndex)
            var valued = -1
            rows.forEachIndexed { index, row ->
                if (hasValue[row.originalIndex]) valued = index
                if (y <= row.bottom && valued != -1) return IndexHit(valued, rows[valued].originalIndex)
            }
            return null
        }
        var rowIndex = -1
        var original = -1
        rows.forEachIndexed { index, row ->
            if (row.isDot) {
                val count = row.hidden.size
                val step = (row.bottom - row.top) / count.coerceAtLeast(1)
                val target = (ceil((y - row.top) / step).toInt() - 1).coerceAtMost(count - 1).coerceAtLeast(0)
                for (j in 0 until count) {
                    if (hasValue[row.hidden[j]]) {
                        rowIndex = index
                        original = row.hidden[j]
                    }
                    if (j >= target && rowIndex != -1) break
                }
            } else if (hasValue[row.originalIndex]) {
                rowIndex = index
                original = row.originalIndex
            }
            if (y <= row.bottom && rowIndex != -1) return IndexHit(rowIndex, original)
            if (index < rows.size - 1 && y > row.bottom && y < rows[index + 1].top) return null
        }
        return null
    }
}

/** COUITouchSearchView.LetterLimitLevelInfo. */
private class LetterLimitLevel(
    val dotLevel: Int,
    val dotSize: Int,
    val showLetterSize: Int,
    val limitHeight: Float,
    val replenishIndex: Int,
)

/**
 * COUITouchSearchView.initHeightRangeSpec + updateKeys: lays the keys out from the top padding,
 * collapsing them into dots when [height] cannot hold them all.
 */
private fun buildIndexRows(
    keyCount: Int,
    showCollectKey: Boolean,
    height: Float,
    density: Density,
): IndexRows {
    with(density) {
        val padding = AlphabetIndexBarDefaults.VerticalPadding.toPx()
        val rowHeight = AlphabetIndexBarDefaults.RowHeight.toPx()
        val collectHeight = if (showCollectKey) AlphabetIndexBarDefaults.CollectIconSize.toPx() else 0f
        val letters = if (showCollectKey) keyCount - 1 else keyCount
        val levels = letterLimitLevels(letters, collectHeight, rowHeight)
        val defaultLimit = levels.firstOrNull()?.limitHeight ?: 0f
        val fullHeight = defaultLimit + padding * 2f
        val available = height - padding * 2f
        val rows = ArrayList<IndexRow>(keyCount)
        var top = padding
        val start = if (showCollectKey) 1 else 0
        if (defaultLimit <= available) {
            if (showCollectKey) {
                rows += IndexRow(0, isCollect = true, isDot = false, hidden = EmptyHidden, top = top, bottom = top + collectHeight)
                top += collectHeight
            }
            for (i in start until keyCount) {
                rows += IndexRow(i, isCollect = false, isDot = false, hidden = EmptyHidden, top = top, bottom = top + rowHeight)
                top += rowHeight
            }
            return IndexRows(rows, compressed = false, fullHeight = fullHeight)
        }
        val level = levels.firstOrNull { available >= it.limitHeight }
            ?: return IndexRows(emptyList(), compressed = true, fullHeight = fullHeight)
        if (showCollectKey) {
            rows += IndexRow(0, isCollect = true, isDot = false, hidden = EmptyHidden, top = top, bottom = top + collectHeight)
            top += collectHeight
        }
        var lettersSinceDot = 0
        var dotIndex = 0
        var i = start
        while (i < keyCount) {
            if (lettersSinceDot == 0 || dotIndex >= level.dotSize) {
                rows += IndexRow(i, isCollect = false, isDot = false, hidden = EmptyHidden, top = top, bottom = top + rowHeight)
                lettersSinceDot++
            } else {
                val count = letterSizeFromDot(level, dotIndex, letters).coerceAtLeast(1)
                dotIndex++
                lettersSinceDot = if (level.dotLevel >= 5) lettersSinceDot + 1 else 0
                val end = (i + count).coerceAtMost(keyCount)
                rows += IndexRow(i, isCollect = false, isDot = true, hidden = IntArray(end - i) { i + it }, top = top, bottom = top + rowHeight)
                i = end - 1
            }
            top += rowHeight
            i++
        }
        return IndexRows(rows, compressed = true, fullHeight = fullHeight)
    }
}

/** COUITouchSearchView.initHeightRangeSpec over getShowLettersSize, levels 0 to 5. */
private fun letterLimitLevels(letters: Int, collectHeight: Float, letterHeight: Float): List<LetterLimitLevel> {
    val result = ArrayList<LetterLimitLevel>(6)
    for (level in 0 until 6) {
        val info = showLettersSize(letters, collectHeight, letterHeight, level)
        if (info == null) {
            showLettersSize(letters, collectHeight, letterHeight, 5)?.let { result += it }
            return result
        }
        result += info
    }
    return result
}

/** COUITouchSearchView.getShowLettersSize; the trailing `#` counts as the last symbol. */
private fun showLettersSize(letters: Int, collectHeight: Float, letterHeight: Float, level: Int): LetterLimitLevel? {
    val lastSymbol = 1
    val counted = letters - lastSymbol
    if (counted < MIN_LETTERS_FOR_DOTS && level != 0) return null
    val d = counted - 1
    fun checked(dotSize: Int, show: Int, replenish: Int): LetterLimitLevel? = if (show - lastSymbol < MIN_LETTERS_FOR_DOTS) null else LetterLimitLevel(level, dotSize, show, letterHeight * show + collectHeight, replenish)
    return when (level) {
        0 -> LetterLimitLevel(0, 0, letters, letterHeight * letters + collectHeight, -1)

        1 -> {
            val removed = if (d % 2 == 0) 2 else 1
            val show = letters - removed
            checked((show - lastSymbol - 1) / 2, show, removed)
        }

        2, 3, 4 -> {
            val group = level + 1
            val remainder = d % group
            val dots = d / group
            checked(dots, letters - dots * level + dots - remainder, remainder)
        }

        5 -> checked(6, lastSymbol + MIN_LETTERS_FOR_DOTS, (d - 1) % 6)

        else -> null
    }
}

/** COUITouchSearchView.getLetterSizeFromDot: how many keys the dot at [dotIndex] hides. */
private fun letterSizeFromDot(level: LetterLimitLevel, dotIndex: Int, letters: Int): Int {
    val dotSize = level.dotSize
    if (dotSize <= 0) return 0
    val total = letters - level.showLetterSize + dotSize
    val base = total / dotSize
    return when (total % dotSize) {
        0 -> base
        1 -> if (dotIndex == dotSize - 1) base + 1 else base
        else -> if (dotIndex >= dotSize - level.replenishIndex) base + 1 else base
    }
}

/** Contains default values used by [AlphabetIndexBar]. */
object AlphabetIndexBarDefaults {
    /** The key reported for the favorites star (COUI star_character). */
    val CollectKey: String = "*"

    /** The default keys (COUI normal_touchsearch_keys after the star: A to Z, then `#`). */
    val Keys: List<String> = ('A'..'Z').map { it.toString() } + "#"

    /** The width of the bar (COUI coui_touchsearch_background_width). */
    val Width = 39.dp

    /** The height of one key row: coui_touchsearch_each_item_height 14dp + item_spacing 2dp. */
    val RowHeight = 16.dp

    /** The top and bottom padding (COUI coui_touchsearch_padding_top / _bottom). */
    val VerticalPadding = 16.dp

    /** The size of the favorites star (COUI coui_touchsearch_collect_normal, 12dp). */
    val CollectIconSize = 12.dp

    /** The key text size, in dp so it ignores font scaling (COUI coui_touchsearch_key_textsize). */
    val KeyTextSize = 12.dp

    /** The dot text size (COUI coui_touchsearch_key_dot_textsize). */
    val DotTextSize = 20.dp

    /** The bubble diameter (COUI coui_touchsearch_popup_first_default_width / _height). */
    val BubbleSize = 48.dp

    /**
     * The gap between the bubble and the bar: the popup window sits 0.5dp
     * (coui_touchsearch_popupwin_right_margin) plus 8dp (coui_touchsearch_touch_end_gap) away.
     */
    val BubbleGap: Dp = 8.5.dp

    /**
     * The default [AlphabetIndexBarColors]: keys on coui_touchsearchview_text_color
     * (couiColorSecondNeutral, pressed couiColorPrimaryTextOnPopup), dots on
     * coui_touchsearchview_dot_color (couiColorControls), and the bubble filled with
     * couiColorBackgroundTop (coui_touch_search_popup_bg) around a couiColorPrimaryNeutral key.
     */
    @Composable
    fun alphabetIndexBarColors(
        keyColor: Color = COUITheme.colorScheme.labelSecondary,
        selectedKeyColor: Color = COUITheme.colorScheme.primaryTextOnPopup,
        dotColor: Color = COUITheme.colorScheme.dragHandle,
        bubbleColor: Color = COUITheme.colorScheme.backgroundTop,
        bubbleContentColor: Color = COUITheme.colorScheme.labelPrimary,
    ): AlphabetIndexBarColors = remember(keyColor, selectedKeyColor, dotColor, bubbleColor, bubbleContentColor) {
        AlphabetIndexBarColors(
            keyColor = keyColor,
            selectedKeyColor = selectedKeyColor,
            dotColor = dotColor,
            bubbleColor = bubbleColor,
            bubbleContentColor = bubbleContentColor,
        )
    }
}

/**
 * Colors used by an [AlphabetIndexBar].
 *
 * @param keyColor The color of the keys and the star.
 * @param selectedKeyColor The color of the selected key, star or dot.
 * @param dotColor The color of the dots that stand for hidden keys.
 * @param bubbleColor The fill of the bubble.
 * @param bubbleContentColor The color of the key or star inside the bubble.
 */
@Immutable
data class AlphabetIndexBarColors(
    val keyColor: Color,
    val selectedKeyColor: Color,
    val dotColor: Color,
    val bubbleColor: Color,
    val bubbleContentColor: Color,
)

/** The star of COUI coui_touchsearch_collect_normal.xml (12dp, 0.7 stroke). */
private val CollectStarIcon: ImageVector by lazy {
    ImageVector.Builder("IndexCollect", 12.dp, 12.dp, 12f, 12f).apply {
        addPath(
            pathData = addPathNodes(
                "M5.762,1.232C5.837,1.001 6.163,1.001 6.238,1.232L7.179,4.127C7.212,4.23 7.308,4.3 7.416,4.3H10.461" +
                    "C10.704,4.3 10.804,4.61 10.608,4.752L8.145,6.542C8.057,6.606 8.021,6.719 8.054,6.822L8.995,9.718" +
                    "C9.07,9.948 8.806,10.139 8.61,9.997L6.147,8.207C6.059,8.144 5.941,8.144 5.853,8.207L3.39,9.997" +
                    "C3.194,10.139 2.93,9.948 3.005,9.718L3.946,6.822C3.979,6.719 3.943,6.606 3.855,6.542L1.392,4.752" +
                    "C1.196,4.61 1.296,4.3 1.539,4.3H4.584C4.692,4.3 4.788,4.23 4.821,4.127L5.762,1.232Z",
            ),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 0.7f,
            strokeLineCap = StrokeCap.Round,
        )
    }.build()
}

/** The star of COUI coui_touchsearch_collect_popup.xml (24dp, 1.4 stroke). */
private val CollectStarPopupIcon: ImageVector by lazy {
    ImageVector.Builder("IndexCollectPopup", 24.dp, 24.dp, 24f, 24f).apply {
        addPath(
            pathData = addPathNodes(
                "M11.524,2.463C11.674,2.002 12.326,2.002 12.476,2.463L14.357,8.255C14.424,8.461 14.616,8.6 14.833,8.6" +
                    "H20.923C21.407,8.6 21.608,9.22 21.217,9.505L16.29,13.084C16.115,13.212 16.041,13.437 16.108,13.643" +
                    "L17.99,19.435C18.14,19.896 17.612,20.279 17.221,19.994L12.294,16.415C12.119,16.287 11.881,16.287 11.706,16.415" +
                    "L6.779,19.994C6.387,20.279 5.86,19.896 6.01,19.435L7.892,13.643C7.959,13.437 7.885,13.212 7.71,13.084" +
                    "L2.783,9.505C2.391,9.22 2.593,8.6 3.077,8.6H9.167C9.384,8.6 9.576,8.461 9.643,8.255L11.524,2.463Z",
            ),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.4f,
            strokeLineCap = StrokeCap.Round,
        )
    }.build()
}

/**
 * The bubble shadow, approximating ShadowUtils.setElevationToView with
 * support_shadow_size_level_five (30dp) and the #88000000 outline spot color.
 */
private val BubbleShadow = Shadow(
    radius = 24.dp,
    color = Color.Black,
    offset = DpOffset(0.dp, 8.dp),
    alpha = 0.1f,
)

private val EmptyHidden = IntArray(0)

/** COUI coui_touchsearch_dot. */
private const val DOT_TEXT = "·"

/** COUITouchSearchView FIXED_MIN_LETTER_LENGTH_FOR_DISPLAY. */
private const val MIN_LETTERS_FOR_DOTS = 8

/** COUIChangeTextUtil.SCALE_LEVEL[3], the font scale cap of getSuitableFontSize level 4. */
private const val BUBBLE_MAX_FONT_SCALE = 1.35f

/** COUITouchSearchView SCALE_MIN. */
private const val BUBBLE_MIN_SCALE = 0.8f

/** COUITouchSearchView POPUP_WINDOW_APPEAR_DURATION. */
private const val BUBBLE_APPEAR_MILLIS = 350

/** COUITouchSearchView POPUP_WINDOW_DISAPPEAR_DURATION. */
private const val BUBBLE_DISAPPEAR_MILLIS = 300

/** COUITouchSearchView SEC_WINDOW_SHOW_DELAY_DURATION, the dismissal delay after lifting. */
private const val BUBBLE_DISMISS_DELAY_MILLIS = 1000L

/** COUIMoveEaseInterpolator. */
private val MoveEase = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)
