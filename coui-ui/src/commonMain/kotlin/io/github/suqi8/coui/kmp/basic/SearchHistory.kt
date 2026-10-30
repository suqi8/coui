// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.effect.couiPressScale
import io.github.suqi8.coui.kmp.squircle.squircleSurface
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A [SearchHistory] component with COUI style.
 *
 * The search history panel of ColorOS's COUISearchHistoryView: a title bar with a clear button,
 * and the history records as capsule chips flowing into lines (COUIFlowLayout). With
 * [maxLinesFolded], the records past that many lines are folded behind a round expand button
 * that takes the end of the last folded line (replacing its last chip if it does not fit).
 * Expanding shows up to [maxLinesExpanded] lines at once and fades the extra records in (400ms,
 * 100ms delay) while the expand button fades out (250ms); a fold button then closes the last
 * line, and folding fades the extra records out (300ms) before the panel shrinks back.
 * Clicking the clear button fades the whole panel out over 300ms before [onClear] is called.
 * The panel draws nothing while [items] is empty.
 *
 * @param title The title of the panel.
 * @param items The history records, in display order.
 * @param onItemClick Called with the clicked record.
 * @param onClear Called after the clear animation when the clear button is clicked.
 * @param modifier The modifier to be applied to the [SearchHistory].
 * @param expandable Whether folded records can be expanded (COUISearchHistoryView expandable).
 *   When `false` the panel never shows more than [maxLinesFolded] lines.
 * @param maxLinesFolded The number of lines shown while folded (COUI maxRowFolded, unlimited by
 *   default).
 * @param maxLinesExpanded The number of lines shown while expanded (COUISearchHistoryView sets
 *   COUIFlowLayout maxRowUnfolded to 8).
 * @param clearContentDescription The accessibility label of the clear button.
 * @param colors The [SearchHistoryColors] of the panel.
 * @param insideMargin The padding around the panel content.
 */
@Composable
fun SearchHistory(
    title: String,
    items: List<String>,
    onItemClick: (item: String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    expandable: Boolean = true,
    maxLinesFolded: Int = SearchHistoryDefaults.MaxLinesFolded,
    maxLinesExpanded: Int = SearchHistoryDefaults.MaxLinesExpanded,
    clearContentDescription: String? = null,
    colors: SearchHistoryColors = SearchHistoryDefaults.searchHistoryColors(),
    insideMargin: PaddingValues = SearchHistoryDefaults.InsideMargin,
) {
    if (items.isEmpty()) return
    val scope = rememberCoroutineScope()
    val currentOnClear by rememberUpdatedState(onClear)
    val panelAlpha = remember { Animatable(1f) }

    Column(
        modifier = modifier
            .graphicsLayer { alpha = panelAlpha.value }
            .padding(insideMargin),
    ) {
        Row(
            modifier = Modifier.padding(vertical = SearchHistoryDefaults.TitleVerticalPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                color = colors.titleColor,
                style = SearchHistoryDefaults.titleTextStyle(),
                modifier = Modifier.weight(1f),
            )
            val clearIndication = remember(colors.clearPressColor) {
                CircularPressIndication(SearchHistoryDefaults.ClearPressRadius, colors.clearPressColor)
            }
            Icon(
                imageVector = HistoryDeleteIcon,
                contentDescription = clearContentDescription,
                tint = colors.clearIconColor,
                modifier = Modifier
                    .size(SearchHistoryDefaults.ClearIconSize)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = clearIndication,
                        role = Role.Button,
                    ) {
                        if (panelAlpha.isRunning) return@clickable
                        scope.launch {
                            // COUISearchHistoryView.doDeleteAnimation: alpha 1 -> 0, 300ms.
                            panelAlpha.animateTo(0f, tween(CLEAR_DURATION_MILLIS, easing = MoveEase))
                            currentOnClear()
                            panelAlpha.snapTo(1f)
                        }
                    },
            )
        }
        SearchHistoryFlow(
            items = items,
            onItemClick = onItemClick,
            expandable = expandable,
            maxLinesFolded = maxLinesFolded,
            maxLinesExpanded = maxLinesExpanded,
            colors = colors,
            modifier = Modifier.padding(top = SearchHistoryDefaults.FlowTopMargin),
        )
    }
}

/** COUIFlowLayout: the record chips, the expand button and the fold button laid out in lines. */
@Composable
private fun SearchHistoryFlow(
    items: List<String>,
    onItemClick: (String) -> Unit,
    expandable: Boolean,
    maxLinesFolded: Int,
    maxLinesExpanded: Int,
    colors: SearchHistoryColors,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    // COUIFlowLayout.isExpand: the line limit and height in use.
    var isExpand by rememberSaveable { mutableStateOf(false) }
    // The alpha of the records past the folded lines and of the fold button.
    val hiddenAlpha = remember { Animatable(if (isExpand) 1f else 0f) }
    // The alpha of the expand button.
    val expandAlpha = remember { Animatable(if (isExpand) 0f else 1f) }
    var animation by remember { mutableStateOf<Job?>(null) }
    val itemSpacing = SearchHistoryDefaults.ItemSpacing
    val lineSpacing = SearchHistoryDefaults.LineSpacing

    val onExpand: () -> Unit = {
        animation?.cancel()
        // doExpandAnimation: the expand button animator sets isExpand on start.
        isExpand = true
        animation = scope.launch {
            launch { expandAlpha.animateTo(0f, tween(EXPAND_BUTTON_FADE_MILLIS, easing = MoveEase)) }
            launch {
                delay(HIDDEN_APPEAR_DELAY_MILLIS)
                hiddenAlpha.animateTo(1f, tween(HIDDEN_APPEAR_MILLIS, easing = MoveEase))
            }
        }
    }
    val onFold: () -> Unit = {
        animation?.cancel()
        animation = scope.launch {
            val fadeIn = launch { expandAlpha.animateTo(1f, tween(HIDDEN_DISAPPEAR_MILLIS, easing = MoveEase)) }
            val fadeOut = launch { hiddenAlpha.animateTo(0f, tween(HIDDEN_DISAPPEAR_MILLIS, easing = MoveEase)) }
            fadeIn.join()
            fadeOut.join()
            // doFoldAnimation: isExpand is cleared when the animation ends.
            isExpand = false
        }
    }

    Layout(
        modifier = modifier,
        content = {
            items.forEach { item ->
                HistoryChip(text = item, colors = colors, onClick = { onItemClick(item) })
            }
            FoldToggleButton(icon = ExpandArrowIcon, colors = colors, onClick = onExpand)
            FoldToggleButton(icon = FoldArrowIcon, colors = colors, onClick = onFold)
        },
    ) { measurables, constraints ->
        val maxWidth = constraints.maxWidth
        val chipConstraints = Constraints(maxWidth = maxWidth)
        // Count from the measurables, so a list mutated after composition cannot misindex them.
        val count = measurables.size - 2
        val chips = List(count) { measurables[it].measure(chipConstraints) }
        val expandButton = measurables[count].measure(chipConstraints)
        val foldButton = measurables[count + 1].measure(chipConstraints)
        val spacing = itemSpacing.roundToPx()
        val lineGap = lineSpacing.roundToPx()
        val unfolded = if (expandable) maxLinesExpanded else maxLinesFolded
        val maxRows = if (isExpand) unfolded else maxLinesFolded

        fun widthOf(id: Int): Int = when (id) {
            EXPAND_ID -> expandButton.width
            FOLD_ID -> foldButton.width
            else -> chips[id].width
        }

        fun heightOf(id: Int): Int = when (id) {
            EXPAND_ID -> expandButton.height
            FOLD_ID -> foldButton.height
            else -> chips[id].height
        }

        fun usedWidth(line: List<Int>): Int {
            var used = 0
            line.forEachIndexed { index, id -> used += if (index == 0) minOf(widthOf(id), maxWidth) else widthOf(id) + spacing }
            return used
        }

        fun canAdd(line: List<Int>, id: Int): Boolean = line.isEmpty() || usedWidth(line) + spacing + widthOf(id) <= maxWidth

        // COUIFlowLayout.fillChildInLines.
        val lines = ArrayList<MutableList<Int>>()
        lines.add(ArrayList())
        for (i in 0 until count) {
            val line = lines.last()
            if (canAdd(line, i)) {
                line += i
            } else if (lines.size >= maxRows) {
                break
            } else {
                lines.add(arrayListOf(i))
            }
        }
        // COUIFlowLayout.isNeedExpandButton: do all records take more than maxLinesFolded lines?
        var rows = 0
        var rowWidth = 0
        var rowItems = 0
        for (i in 0 until count) {
            val w = chips[i].width
            if (rowItems == 0) {
                rowWidth = minOf(w, maxWidth)
                rows++
            } else if (rowWidth + w + spacing > maxWidth) {
                rows++
                rowWidth = minOf(w, maxWidth)
                rowItems = 0
            } else {
                rowWidth += w + spacing
            }
            rowItems++
        }
        val needButton = expandable && rows > maxLinesFolded && lines.size >= maxLinesFolded
        // COUIFlowLayout.ensureAndExpandButton.
        var removedChip = -1
        var removedChipLine = -1
        if (needButton) {
            val foldLine = lines[maxLinesFolded - 1]
            val last = foldLine.lastOrNull()
            if (last != null && !canAdd(foldLine, FOLD_ID)) {
                foldLine.removeAt(foldLine.size - 1)
                removedChip = last
                removedChipLine = maxLinesFolded - 1
            }
            foldLine += EXPAND_ID
            if (isExpand) {
                val lastLine = lines.last()
                val lastView = lastLine.lastOrNull()
                if (lastView != null && !canAdd(lastLine, EXPAND_ID)) lastLine.removeAt(lastLine.size - 1)
                lastLine += FOLD_ID
            }
        }
        val lineHeights = lines.map { line -> line.maxOfOrNull { heightOf(it) } ?: 0 }

        fun linesHeight(n: Int): Int {
            val shown = minOf(n, lines.size)
            if (shown <= 0) return 0
            var h = 0
            for (i in 0 until shown) h += lineHeights[i]
            return h + lineGap * (shown - 1)
        }
        val height = if (isExpand) linesHeight(lines.size) else linesHeight(maxLinesFolded)

        // Unbounded width (e.g. a horizontally scrolling parent) keeps every record on one line.
        val width = if (constraints.hasBoundedWidth) maxWidth else lines.maxOf { usedWidth(it) }
        layout(constraints.constrainWidth(width), constraints.constrainHeight(height)) {
            val hidden = hiddenAlpha.value
            val expandButtonAlpha = expandAlpha.value
            var y = 0
            lines.forEachIndexed { lineIndex, line ->
                // COUIFlowLayout.drawChild skips children below the container height.
                if (y >= height) return@forEachIndexed
                var x = 0
                line.forEach { id ->
                    val placeable = when (id) {
                        EXPAND_ID -> expandButton
                        FOLD_ID -> foldButton
                        else -> chips[id]
                    }
                    val alpha = when {
                        id == EXPAND_ID -> expandButtonAlpha
                        id == FOLD_ID -> hidden
                        lineIndex >= maxLinesFolded -> hidden
                        else -> 1f
                    }
                    // updateAlphaAndVisibility: a child at alpha 0 is INVISIBLE and not clickable.
                    if (alpha > ALPHA_EPSILON) placeable.placeRelativeWithLayer(x, y) { this.alpha = alpha }
                    if (id == EXPAND_ID && removedChip >= 0 && lineIndex == removedChipLine && hidden > ALPHA_EPSILON) {
                        // Line.layoutRemovedView: the chip the expand button displaced sits in its place.
                        chips[removedChip].placeRelativeWithLayer(x, y) { this.alpha = hidden }
                    }
                    x += placeable.width + spacing
                }
                y += lineHeights[lineIndex] + lineGap
            }
        }
    }
}

/** One record: a COUIChip in Widget.COUI.Chip.Suggestion style. */
@Composable
private fun HistoryChip(
    text: String,
    colors: SearchHistoryColors,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressProgress by animateFloatAsState(if (pressed) 1f else 0f, ChipPressSpring)
    val pressColor = COUITheme.colorScheme.press
    val fill = if (pressProgress > 0f) {
        pressColor.copy(alpha = pressColor.alpha * pressProgress).compositeOver(colors.chipColor)
    } else {
        colors.chipColor
    }
    Box(
        modifier = Modifier
            .couiPressScale(pressed = pressed)
            .widthIn(max = SearchHistoryDefaults.ChipMaxWidth)
            .squircleSurface(color = fill, cornerRadius = SearchHistoryDefaults.ChipHeight / 2)
            .clickable(interactionSource = interactionSource, indication = null, role = Role.Button, onClick = onClick)
            .defaultMinSize(minWidth = SearchHistoryDefaults.ChipMinWidth, minHeight = SearchHistoryDefaults.ChipHeight)
            .padding(horizontal = SearchHistoryDefaults.ChipHorizontalPadding),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = colors.chipContentColor,
            style = SearchHistoryDefaults.chipTextStyle(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = SearchHistoryDefaults.ChipTextMaxWidth),
        )
    }
}

/**
 * The expand / fold button: a COUIPressFeedbackImageView, i.e. a chip-height circle around a
 * 16dp arrow that scales to 0.9 over 200ms when pressed and back over 340ms on release.
 */
@Composable
private fun FoldToggleButton(
    icon: ImageVector,
    colors: SearchHistoryColors,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale = remember { Animatable(1f) }
    LaunchedEffect(pressed) {
        if (pressed) {
            scale.animateTo(TOGGLE_PRESSED_SCALE, tween(TOGGLE_PRESS_MILLIS, easing = MoveEase))
        } else if (scale.value < 1f) {
            scale.animateTo(1f, tween(TOGGLE_RELEASE_MILLIS, easing = MoveEase))
        }
    }
    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .size(SearchHistoryDefaults.ChipHeight)
            .squircleSurface(color = colors.toggleButtonColor, cornerRadius = SearchHistoryDefaults.ChipHeight / 2)
            .clickable(interactionSource = interactionSource, indication = null, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.toggleIconColor,
            modifier = Modifier.size(SearchHistoryDefaults.ToggleIconSize),
        )
    }
}

/** Contains default values used by [SearchHistory]. */
object SearchHistoryDefaults {
    /** Lines shown while folded (COUI maxRowFolded default Integer.MAX_VALUE). */
    val MaxLinesFolded: Int = Int.MAX_VALUE

    /** Lines shown while expanded (COUISearchHistoryView DEFAULT_MAX_ROW_UNFOLDED). */
    val MaxLinesExpanded: Int = 8

    /**
     * The padding around the panel (COUI coui_component_search_history_padding_start / _end
     * 16dp, _top 16dp, _bottom 32dp).
     */
    val InsideMargin = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 32.dp)

    /** The vertical padding of the title bar (COUI coui_component_search_history_title_margin_vertical). */
    val TitleVerticalPadding = 7.dp

    /** The size of the clear icon (COUI coui_component_search_history_delete_icon_size). */
    val ClearIconSize = 18.dp

    /** The radius of the clear button press highlight (COUI coui_component_icon_bg ripple radius). */
    val ClearPressRadius = 24.dp

    /** The gap between the title bar and the records (COUI coui_component_search_history_flow_margin_top). */
    val FlowTopMargin = 8.dp

    /** The horizontal gap between records (COUI coui_component_search_history_flow_item_spacing). */
    val ItemSpacing = 8.dp

    /** The vertical gap between lines (COUI coui_component_search_history_flow_line_spacing). */
    val LineSpacing = 8.dp

    /** The height of a record and of the expand button (COUI coui_chip_suggest_style_height). */
    val ChipHeight = 28.dp

    /** The minimum width of a record (COUI coui_chip_default_min_width). */
    val ChipMinWidth = 52.dp

    /** The maximum width of a record (COUI coui_chip_default_max_width). */
    val ChipMaxWidth = 300.dp

    /** The maximum width of a record label (COUI coui_chip_default_max_text_width). */
    val ChipTextMaxWidth = 200.dp

    /** The horizontal padding of a record (COUI coui_chip_suggest_style_chip_horizontal_padding). */
    val ChipHorizontalPadding = 12.dp

    /** The size of the expand / fold arrow (COUI coui_component_expand_arrow_drop_down, 16dp). */
    val ToggleIconSize: Dp = 16.dp

    /** The title text style (COUI couiTextAppearanceButton: 14sp medium). */
    @Composable
    fun titleTextStyle(): TextStyle = COUITheme.textStyles.subtitle

    /** The record text style (COUI Widget.COUI.Chip textAppearance couiTextBodyXS: 12sp). */
    @Composable
    fun chipTextStyle(): TextStyle = COUITheme.textStyles.footnote2

    /**
     * The default [SearchHistoryColors]: title couiColorPrimaryNeutral, clear icon and arrows
     * couiColorSecondNeutral, record and expand button fills couiColorContainer8 with
     * couiColorLabelPrimary labels, and the couiColorPress highlight of the clear button.
     */
    @Composable
    fun searchHistoryColors(
        titleColor: Color = COUITheme.colorScheme.onBackground,
        clearIconColor: Color = COUITheme.colorScheme.onSurfaceSecondary,
        clearPressColor: Color = COUITheme.colorScheme.press,
        chipColor: Color = COUITheme.colorScheme.container8,
        chipContentColor: Color = COUITheme.colorScheme.onSurface,
        toggleButtonColor: Color = COUITheme.colorScheme.container8,
        toggleIconColor: Color = COUITheme.colorScheme.onSurfaceSecondary,
    ): SearchHistoryColors = remember(
        titleColor,
        clearIconColor,
        clearPressColor,
        chipColor,
        chipContentColor,
        toggleButtonColor,
        toggleIconColor,
    ) {
        SearchHistoryColors(
            titleColor = titleColor,
            clearIconColor = clearIconColor,
            clearPressColor = clearPressColor,
            chipColor = chipColor,
            chipContentColor = chipContentColor,
            toggleButtonColor = toggleButtonColor,
            toggleIconColor = toggleIconColor,
        )
    }
}

/**
 * Colors used by a [SearchHistory].
 *
 * @param titleColor The color of the title.
 * @param clearIconColor The color of the clear icon.
 * @param clearPressColor The press highlight of the clear button.
 * @param chipColor The fill of a record.
 * @param chipContentColor The label color of a record.
 * @param toggleButtonColor The fill of the expand and fold buttons.
 * @param toggleIconColor The arrow color of the expand and fold buttons.
 */
@Immutable
data class SearchHistoryColors(
    val titleColor: Color,
    val clearIconColor: Color,
    val clearPressColor: Color,
    val chipColor: Color,
    val chipContentColor: Color,
    val toggleButtonColor: Color,
    val toggleIconColor: Color,
)

/** COUI coui_component_search_history_delete.xml (18dp). */
private val HistoryDeleteIcon: ImageVector by lazy {
    ImageVector.Builder("HistoryDelete", 18.dp, 18.dp, 18f, 18f).apply {
        addPath(
            pathData = addPathNodes(
                "M6.781,2.001C6.985,1.388 7.559,0.975 8.204,0.975H9.799C10.444,0.975 11.018,1.388 11.222,2.001L11.63,3.225" +
                    "H14.247H14.255H15.189C15.479,3.225 15.714,3.46 15.714,3.75C15.714,4.04 15.479,4.275 15.189,4.275H14.747" +
                    "L14.08,15.613C14.033,16.406 13.376,17.025 12.582,17.025H5.421C4.627,17.025 3.97,16.406 3.923,15.613" +
                    "L3.257,4.275H2.814C2.524,4.275 2.289,4.04 2.289,3.75C2.289,3.46 2.524,3.225 2.814,3.225H3.748H3.756" +
                    "H6.373L6.781,2.001ZM10.226,2.333L10.523,3.225H7.48L7.777,2.333C7.839,2.149 8.011,2.025 8.204,2.025" +
                    "H9.799C9.993,2.025 10.165,2.149 10.226,2.333ZM4.308,4.275H6.023H6.752H11.252H11.98H13.695L13.031,15.551" +
                    "C13.017,15.789 12.821,15.975 12.582,15.975H5.421C5.183,15.975 4.986,15.789 4.972,15.551L4.308,4.275Z" +
                    "M7.089,7.483C7.079,7.193 6.837,6.966 6.547,6.975C6.257,6.985 6.03,7.227 6.039,7.517L6.228,13.329" +
                    "C6.237,13.619 6.479,13.847 6.769,13.837C7.059,13.828 7.286,13.585 7.277,13.295L7.089,7.483Z" +
                    "M11.965,7.517C11.974,7.227 11.747,6.985 11.457,6.975C11.167,6.966 10.925,7.193 10.915,7.483" +
                    "L10.727,13.295C10.717,13.585 10.945,13.828 11.235,13.837C11.524,13.847 11.767,13.619 11.776,13.329" +
                    "L11.965,7.517ZM9.002,6.975C9.292,6.975 9.527,7.21 9.527,7.5V13.312C9.527,13.602 9.292,13.837 9.002,13.837" +
                    "C8.712,13.837 8.477,13.602 8.477,13.312V7.5C8.477,7.21 8.712,6.975 9.002,6.975Z",
            ),
            pathFillType = PathFillType.EvenOdd,
            fill = SolidColor(Color.Black),
        )
    }.build()
}

/** COUI coui_component_expand_arrow_drop_down.xml (16dp, 0.933 stroke). */
private val ExpandArrowIcon: ImageVector by lazy { toggleArrow("ExpandArrow", "M4,6L8,10L12,6") }

/** COUI coui_component_expand_arrow_drop_up.xml (16dp, 0.933 stroke). */
private val FoldArrowIcon: ImageVector by lazy { toggleArrow("FoldArrow", "M4,10L8,6L12,10") }

private fun toggleArrow(name: String, pathData: String): ImageVector = ImageVector.Builder(name, 16.dp, 16.dp, 16f, 16f).apply {
    addPath(
        pathData = addPathNodes(pathData),
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 0.933333f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    )
}.build()

/** COUIChip press tint spring: COUISpringForce(response 0.3, bounce 0). */
private val ChipPressSpring = spring<Float>(dampingRatio = 1f, stiffness = 438.65f)

/** COUIMoveEaseInterpolator. */
private val MoveEase = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

private const val EXPAND_ID = -1
private const val FOLD_ID = -2

/** COUIFlowLayout.almostEquals tolerance for an invisible child. */
private const val ALPHA_EPSILON = 0.001f

/** COUIFlowLayout EXPAND_BUTTON_APPEAR_DURATION. */
private const val EXPAND_BUTTON_FADE_MILLIS = 250

/** COUIFlowLayout HIDDEN_VIEWS_APPEAR_DURATION. */
private const val HIDDEN_APPEAR_MILLIS = 400

/** COUIFlowLayout HIDDEN_VIEWS_APPEAR_DELAY. */
private const val HIDDEN_APPEAR_DELAY_MILLIS = 100L

/** COUIFlowLayout HIDDEN_VIEWS_DISAPPEAR_DURATION. */
private const val HIDDEN_DISAPPEAR_MILLIS = 300

/** COUISearchHistoryView SEARCH_VIEW_DISAPPEAR_DURATION. */
private const val CLEAR_DURATION_MILLIS = 300

/** COUIPressFeedbackImageView DEFAULT_SCALE_MIN_VALUE. */
private const val TOGGLE_PRESSED_SCALE = 0.9f

/** COUIPressFeedbackImageView PRESS_FEEDBACK_ANIMATION_DURATION. */
private const val TOGGLE_PRESS_MILLIS = 200

/** COUIPressFeedbackImageView RELEASE_FEEDBACK_ANIMATION_DURATION. */
private const val TOGGLE_RELEASE_MILLIS = 340
