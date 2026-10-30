// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos

/**
 * One dot of a [LockPatternView], in a 3 x 3 grid (`COUILockPatternView.Cell`).
 *
 * @param row The row, 0 to 2.
 * @param column The column, 0 to 2.
 */
@Immutable
data class LockPatternCell(val row: Int, val column: Int) {
    init {
        require(row in 0..2) { "row must be in range 0-2" }
        require(column in 0..2) { "column must be in range 0-2" }
    }

    /** The 1-based dot number used by accessibility and [patternToString] (`row * 3 + column + 1`). */
    val number: Int get() = row * 3 + column + 1
}

/** How a [LockPatternView] colors the current pattern (`COUILockPatternView.DisplayMode`). */
enum class LockPatternDisplayMode {
    /** The pattern being drawn, or a correct one: path and selected dots in the success color. */
    Correct,

    /** Replays the pattern, one dot every 700ms (`MILLIS_PER_CIRCLE_ANIMATING`). */
    Animate,

    /** A wrong pattern: error color, and the path and selected dots flash out over 1s. */
    Wrong,
}

/**
 * The state of a [LockPatternView]: the cells of the current pattern, the display mode and the
 * per-dot animations. Create it with [rememberLockPatternState].
 */
@Stable
class LockPatternState internal constructor() {
    internal val cells = mutableStateListOf<LockPatternCell>()

    /** The cells of the current pattern, in drawing order. */
    val pattern: List<LockPatternCell> get() = cells.toList()

    /** The current [LockPatternDisplayMode]. */
    var displayMode by mutableStateOf(LockPatternDisplayMode.Correct)
        private set

    internal val selected = Array(3) { BooleanArray(3) }
    internal var selectionVersion by mutableIntStateOf(0)
    internal var inProgress by mutableStateOf(false)
    internal var progress by mutableStateOf(Offset.Unspecified)
    internal var pathAlpha by mutableFloatStateOf(1f)
    internal var animateElapsed by mutableLongStateOf(0L)
    internal var wrongToken by mutableIntStateOf(0)
    internal var wrongHaptic = false
    internal var wrongJob: Job? = null
    internal var pendingClear = false
    internal var scope: CoroutineScope? = null

    internal val dots = Array(9) { LockPatternDotState() }

    internal fun dot(cell: LockPatternCell) = dots[cell.row * 3 + cell.column]

    /**
     * Sets the display mode (`setDisplayMode`). [LockPatternDisplayMode.Wrong] plays the error
     * flash and, when [hapticFeedback] is set and the pattern has more than one dot, a reject
     * haptic. [LockPatternDisplayMode.Animate] needs a non-empty pattern.
     */
    fun setDisplayMode(mode: LockPatternDisplayMode, hapticFeedback: Boolean = true) {
        if (mode == LockPatternDisplayMode.Animate) {
            check(cells.isNotEmpty()) { "you must have a pattern to animate if you want to set the display mode to animate" }
            animateElapsed = 0L
        }
        displayMode = mode
        if (mode == LockPatternDisplayMode.Wrong) {
            wrongHaptic = hapticFeedback && cells.size > 1
            wrongToken++
        }
    }

    /** Replaces the pattern and sets [mode] (`setPattern`). */
    fun setPattern(mode: LockPatternDisplayMode, pattern: List<LockPatternCell>) {
        cells.clear()
        cells.addAll(pattern)
        clearLookup()
        pattern.forEach { selected[it.row][it.column] = true }
        selectionVersion++
        setDisplayMode(mode)
    }

    /**
     * Clears the pattern (`clearPattern`). While the wrong flash is running the pattern is only
     * cleared once it ends.
     */
    fun clearPattern() {
        if (wrongJob?.isActive == true) {
            pendingClear = true
        } else {
            reset()
        }
    }

    /**
     * Plays the COUI entrance (`getEnterAnim`): each dot rises 258dp
     * (`color_lock_pattern_view_max_translate_y`) over 500ms (`COUIInEaseInterpolator`) and fades
     * in over 167ms (`COUIEaseInterpolator`) after 166ms, dot `i` delayed by `16ms * i`.
     */
    suspend fun animateEnter() {
        val scope = scope ?: return
        val jobs = dots.mapIndexed { index, dot ->
            dot.enterAlpha.snapTo(0f)
            dot.enterTranslate.snapTo(1f)
            val stagger = index * ENTER_STAGGER_MS
            listOf(
                scope.launch {
                    dot.enterAlpha.animateTo(1f, tween(ENTER_ALPHA_MS, ENTER_ALPHA_DELAY_MS + stagger, CouiEase))
                },
                scope.launch {
                    dot.enterTranslate.animateTo(0f, tween(ENTER_TRANSLATE_MS, stagger, CouiInEase))
                },
            )
        }
        jobs.flatten().forEach { it.join() }
    }

    internal fun clearLookup() {
        for (r in 0..2) for (c in 0..2) selected[r][c] = false
    }

    internal fun reset() {
        dots.forEach { it.innerOverride = -1f }
        cells.clear()
        clearLookup()
        selectionVersion++
        displayMode = LockPatternDisplayMode.Correct
    }
}

/** The per-dot animation values of [LockPatternState] (`COUILockPatternView.CellState`). */
internal class LockPatternDotState {
    val innerAlpha = Animatable(0f)
    val outerScale = Animatable(1f)
    val outerProgress = Animatable(1f)
    val lineEnd = Animatable(Offset.Unspecified, Offset.VectorConverter)
    var lineJob: Job? = null
    var hideDot by mutableStateOf(false)

    /** The inner alpha forced by the wrong flash (`innerCircleAlpha = pathAlpha`), or -1. */
    var innerOverride by mutableFloatStateOf(-1f)
    val enterAlpha = Animatable(1f)
    val enterTranslate = Animatable(0f)
}

/** Creates and remembers a [LockPatternState]. */
@Composable
fun rememberLockPatternState(): LockPatternState = remember { LockPatternState() }

/**
 * The COUI 3 x 3 pattern lock (`COUILockPatternView`).
 *
 * Touching a dot starts a pattern; dragging over dots adds them (a dot skipped on a straight
 * line between two dots is added too), each one with a haptic tick, a 460ms ripple that grows to
 * 7x and fades, its glowing inner circle fading in over 230ms and the path segment snapping to
 * its center over 100ms. The path is drawn while dragging, from the last dot to the finger.
 * Releasing reports the pattern through [onPatternDetected]; mark it wrong with
 * [LockPatternState.setDisplayMode] and [LockPatternDisplayMode.Wrong] to color it red and play
 * the 1s error flash, then [LockPatternState.clearPattern].
 *
 * The view measures 312 x 280dp by default (`coui_lock_pattern_view_width / _height`) with
 * 21 x 5dp padding; a [modifier] size overrides it.
 *
 * @param onPatternDetected Called with the pattern when the finger is lifted.
 * @param modifier The modifier applied to the view.
 * @param state The [LockPatternState] holding the pattern.
 * @param enabled Whether touches are accepted (`enableInput` / `disableInput`).
 * @param inStealthMode Whether the path and the dot animations are hidden while drawing.
 * @param hapticFeedbackEnabled Whether each added dot and a wrong pattern vibrate.
 * @param colors The [LockPatternViewColors].
 * @param onPatternStart Called when a pattern starts.
 * @param onPatternCellAdded Called with the pattern each time a dot is added.
 * @param onPatternCleared Called when a pattern in progress is cancelled.
 */
@Composable
fun LockPatternView(
    onPatternDetected: (List<LockPatternCell>) -> Unit,
    modifier: Modifier = Modifier,
    state: LockPatternState = rememberLockPatternState(),
    enabled: Boolean = true,
    inStealthMode: Boolean = false,
    hapticFeedbackEnabled: Boolean = true,
    colors: LockPatternViewColors = LockPatternViewDefaults.colors(),
    onPatternStart: () -> Unit = {},
    onPatternCellAdded: (List<LockPatternCell>) -> Unit = {},
    onPatternCleared: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    state.scope = scope
    val haptic = LocalHapticFeedback.current
    val currentHaptic by rememberUpdatedState(haptic)
    val currentHapticEnabled by rememberUpdatedState(hapticFeedbackEnabled)
    val currentStealth by rememberUpdatedState(inStealthMode)
    val currentOnDetected by rememberUpdatedState(onPatternDetected)
    val currentOnStart by rememberUpdatedState(onPatternStart)
    val currentOnAdded by rememberUpdatedState(onPatternCellAdded)
    val currentOnCleared by rememberUpdatedState(onPatternCleared)

    // The error flash (startWrongAnimator): keyframes over 1s on the default
    // AccelerateDecelerateInterpolator, driving the path and the selected inner circles.
    LaunchedEffect(state, state.wrongToken) {
        if (state.wrongToken == 0) return@LaunchedEffect
        if (state.wrongHaptic && currentHapticEnabled) currentHaptic.performHapticFeedback(HapticFeedbackType.Reject)
        state.wrongJob = currentCoroutineContext()[Job]
        val progress = Animatable(0f)
        try {
            progress.animateTo(1f, tween(WRONG_DURATION_MS, easing = AccelerateDecelerateEasing)) {
                val alpha = wrongKeyframes(value)
                state.pathAlpha = alpha
                state.cells.forEach { cell ->
                    val dot = state.dot(cell)
                    dot.innerOverride = alpha
                    dot.hideDot = alpha > 0.1f
                }
            }
        } finally {
            state.dots.forEach { it.hideDot = false }
            if (state.pendingClear) {
                state.pendingClear = false
                state.reset()
            }
        }
    }

    // Animate mode replays the pattern against the frame clock (onDraw's elapsedRealtime math).
    val animating = state.displayMode == LockPatternDisplayMode.Animate
    LaunchedEffect(state, animating) {
        if (!animating) return@LaunchedEffect
        val start = withFrameMillis { it }
        while (true) {
            withFrameMillis { state.animateElapsed = it - start }
        }
    }

    val innerBrush = remember { Brush.radialGradient(colorStops = InnerCircleStops, center = Offset.Zero, radius = 1f) }

    Canvas(
        modifier = modifier
            .size(LockPatternViewDefaults.Width, LockPatternViewDefaults.Height)
            .pointerInput(state, enabled) {
                if (!enabled) return@pointerInput
                val padH = LockPatternViewDefaults.HorizontalPadding.toPx()
                val padV = LockPatternViewDefaults.VerticalPadding.toPx()
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val grid = PatternGrid(size.width.toFloat(), size.height.toFloat(), padH, padV)
                    val touch = PatternTouch(
                        state = state,
                        grid = grid,
                        scope = scope,
                        stealth = currentStealth,
                        onHit = { if (currentHapticEnabled) currentHaptic.performHapticFeedback(HapticFeedbackType.SegmentTick) },
                        onStart = { currentOnStart() },
                        onAdded = { currentOnAdded(it) },
                    )
                    down.consume()
                    touch.down(down.position)
                    var cancelled = false
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id } ?: event.changes.first()
                        if (!change.pressed) {
                            change.consume()
                            break
                        }
                        if (change.isConsumed) {
                            cancelled = true
                            break
                        }
                        touch.move(change)
                        change.consume()
                    }
                    if (cancelled) {
                        if (state.inProgress) {
                            state.inProgress = false
                            state.reset()
                            currentOnCleared()
                        }
                    } else if (state.cells.isNotEmpty()) {
                        state.inProgress = false
                        state.dots.forEach { dot ->
                            dot.lineJob?.cancel()
                            scope.launch { dot.lineEnd.snapTo(Offset.Unspecified) }
                        }
                        currentOnDetected(state.pattern)
                    }
                }
            },
    ) {
        val grid = PatternGrid(
            size.width,
            size.height,
            LockPatternViewDefaults.HorizontalPadding.toPx(),
            LockPatternViewDefaults.VerticalPadding.toPx(),
        )
        val mode = state.displayMode
        val currentColor = if (mode == LockPatternDisplayMode.Wrong) colors.errorColor else colors.successColor
        val cells = state.cells
        state.selectionVersion

        // Animate: only the first `spot` cells count as drawn, and the path end runs from the
        // last one towards the next (onDraw, 700ms per circle).
        var lookup: Array<BooleanArray> = state.selected
        var progress = state.progress
        if (mode == LockPatternDisplayMode.Animate && cells.isNotEmpty()) {
            val period = (cells.size + 1) * MILLIS_PER_CIRCLE
            val elapsed = (state.animateElapsed % period).toInt()
            val spot = elapsed / MILLIS_PER_CIRCLE
            lookup = Array(3) { BooleanArray(3) }
            for (i in 0 until spot.coerceAtMost(cells.size)) lookup[cells[i].row][cells[i].column] = true
            if (spot in 1 until cells.size) {
                val fraction = (elapsed % MILLIS_PER_CIRCLE) / MILLIS_PER_CIRCLE.toFloat()
                val from = cells[spot - 1]
                val to = cells[spot]
                val fromX = grid.centerX(from.column)
                val fromY = grid.centerY(from.row)
                progress = Offset(
                    fromX + (grid.centerX(to.column) - fromX) * fraction,
                    fromY + (grid.centerY(to.row) - fromY) * fraction,
                )
            }
        }

        if (!inStealthMode) {
            val path = Path()
            var lastX = 0f
            var lastY = 0f
            var anyCircles = false
            for (i in cells.indices) {
                val cell = cells[i]
                if (!lookup[cell.row][cell.column]) break
                anyCircles = true
                lastX = grid.centerX(cell.column)
                lastY = grid.centerY(cell.row)
                if (i == 0) {
                    path.moveTo(lastX, lastY)
                } else {
                    val end = state.dot(cell).lineEnd.value
                    if (end.isSpecified()) path.lineTo(end.x, end.y) else path.lineTo(lastX, lastY)
                }
            }
            if ((state.inProgress || mode == LockPatternDisplayMode.Animate) && anyCircles && progress.isSpecified()) {
                path.moveTo(lastX, lastY)
                path.lineTo(progress.x, progress.y)
            }
            drawPath(
                path = path,
                color = currentColor,
                alpha = state.pathAlpha.coerceIn(0f, 1f) * currentColor.alpha,
                style = Stroke(
                    width = LockPatternViewDefaults.PathWidth.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
            )
        }

        val maxTranslate = LockPatternViewDefaults.EnterTranslateY.toPx()
        val dotRadius = LockPatternViewDefaults.DotSize.toPx() / 2f
        val innerRadius = INNER_CIRCLE_RADIUS_DP.dp.toPx()
        val outerRadius = OUTER_CIRCLE_RADIUS_DP.dp.toPx()
        val tint = ColorFilter.tint(currentColor.copy(alpha = 1f))
        for (row in 0..2) {
            for (column in 0..2) {
                val dot = state.dots[row * 3 + column]
                val cx = grid.centerX(column)
                val cy = grid.centerY(row) + dot.enterTranslate.value * maxTranslate
                if (lookup[row][column]) {
                    val inner = if (dot.innerOverride >= 0f) dot.innerOverride else dot.innerAlpha.value
                    val innerAlpha = inner.coerceIn(0f, 1f) * currentColor.alpha
                    if (innerAlpha > 0f) {
                        translate(cx, cy) {
                            scale(innerRadius, innerRadius, pivot = Offset.Zero) {
                                drawCircle(brush = innerBrush, radius = 1f, center = Offset.Zero, alpha = innerAlpha, colorFilter = tint)
                            }
                        }
                    }
                    val outerAlpha = outerAlpha(dot.outerProgress.value, colors.outerCircleMaxAlpha) * currentColor.alpha
                    if (outerAlpha > 0f) {
                        drawCircle(
                            color = currentColor.copy(alpha = 1f),
                            radius = outerRadius * dot.outerScale.value,
                            center = Offset(cx, cy),
                            alpha = outerAlpha,
                        )
                    }
                }
                if (!dot.hideDot) {
                    drawCircle(
                        color = colors.dotColor,
                        radius = dotRadius,
                        center = Offset(cx, cy),
                        alpha = dot.enterAlpha.value.coerceIn(0f, 1f),
                    )
                }
            }
        }
    }
}

/** The grid geometry (`onSizeChanged`, `getCenterXForColumn`, `getRowHit`). */
private class PatternGrid(width: Float, height: Float, private val padH: Float, private val padV: Float) {
    val squareWidth = (width - padH * 2f) / 3f
    val squareHeight = (height - padV * 2f) / 3f

    fun centerX(column: Int) = padH + column * squareWidth + squareWidth / 2f

    fun centerY(row: Int) = padV + row * squareHeight + squareHeight / 2f

    fun rowHit(y: Float): Int {
        val hit = squareHeight * HIT_FACTOR
        val top = padV + (squareHeight - hit) / 2f
        for (i in 0..2) {
            val start = top + i * squareHeight
            if (y >= start && y <= start + hit) return i
        }
        return -1
    }

    fun columnHit(x: Float): Int {
        val hit = squareWidth * HIT_FACTOR
        val left = padH + (squareWidth - hit) / 2f
        for (i in 0..2) {
            val start = left + i * squareWidth
            if (x >= start && x <= start + hit) return i
        }
        return -1
    }
}

/** `handleActionDown` / `handleActionMove` / `detectAndAddHit`. */
private class PatternTouch(
    val state: LockPatternState,
    val grid: PatternGrid,
    val scope: CoroutineScope,
    val stealth: Boolean,
    val onHit: () -> Unit,
    val onStart: () -> Unit,
    val onAdded: (List<LockPatternCell>) -> Unit,
) {
    fun down(position: Offset) {
        state.wrongJob?.cancel()
        state.pendingClear = false
        state.pathAlpha = 1f
        state.reset()
        state.dots.forEach { dot ->
            dot.hideDot = false
            dot.innerOverride = -1f
        }
        val hit = detectAndAddHit(position)
        if (hit != null) {
            state.inProgress = true
            onStart()
        } else if (state.inProgress) {
            state.inProgress = false
        }
        state.progress = position
    }

    fun move(change: PointerInputChange) {
        val points = change.historical.map { it.position } + change.position
        points.forEach { point ->
            val hit = detectAndAddHit(point)
            if (hit != null && state.cells.size == 1) {
                state.inProgress = true
                onStart()
            }
        }
        state.progress = change.position
    }

    private fun detectAndAddHit(position: Offset): LockPatternCell? {
        val row = grid.rowHit(position.y)
        if (row < 0) return null
        val column = grid.columnHit(position.x)
        if (column < 0 || state.selected[row][column]) return null
        val cell = LockPatternCell(row, column)
        val last = state.cells.lastOrNull()
        if (last != null) {
            val dRow = cell.row - last.row
            val dColumn = cell.column - last.column
            var midRow = last.row
            var midColumn = last.column
            if (abs(dRow) == 2 && abs(dColumn) != 1) midRow = last.row + if (dRow > 0) 1 else -1
            if (abs(dColumn) == 2 && abs(dRow) != 1) midColumn = last.column + if (dColumn > 0) 1 else -1
            if (!state.selected[midRow][midColumn]) addCell(LockPatternCell(midRow, midColumn))
        }
        addCell(cell)
        onHit()
        return cell
    }

    private fun addCell(cell: LockPatternCell) {
        state.selected[cell.row][cell.column] = true
        state.cells.add(cell)
        state.selectionVersion++
        if (!stealth) startCellActivated(cell)
        onAdded(state.pattern)
    }

    /** `startCellActivatedAnimation`: outer ripple, inner fade-in and the line-end snap. */
    private fun startCellActivated(cell: LockPatternCell) {
        val dot = state.dot(cell)
        scope.launch {
            dot.outerScale.snapTo(1f)
            dot.outerScale.animateTo(OUTER_MAX_SCALE, tween(OUTER_DURATION_MS, easing = CouiInEase))
        }
        scope.launch {
            dot.outerProgress.snapTo(0f)
            dot.outerProgress.animateTo(1f, tween(OUTER_DURATION_MS, easing = CouiInEase))
        }
        scope.launch {
            dot.innerOverride = -1f
            dot.innerAlpha.snapTo(0f)
            dot.innerAlpha.animateTo(1f, tween(INNER_DURATION_MS, easing = CouiEase))
        }
        val from = state.progress
        val to = Offset(grid.centerX(cell.column), grid.centerY(cell.row))
        dot.lineJob?.cancel()
        dot.lineJob = scope.launch {
            if (from.isSpecified()) {
                dot.lineEnd.snapTo(from)
                dot.lineEnd.animateTo(to, tween(LINE_DURATION_MS, easing = FastOutSlowInEasing))
            }
            dot.lineEnd.snapTo(Offset.Unspecified)
        }
    }
}

/** Contains the default values used by [LockPatternView]. */
object LockPatternViewDefaults {
    /** The default width (`coui_lock_pattern_view_width`). */
    val Width: Dp = 312.dp

    /** The default height (`coui_lock_pattern_view_height`). */
    val Height: Dp = 280.dp

    /** The horizontal padding (`coui_lock_pattern_view_horizontal_padding`). */
    val HorizontalPadding: Dp = 21.dp

    /** The vertical padding (`coui_lock_pattern_view_vertical_padding`). */
    val VerticalPadding: Dp = 5.dp

    /** The diameter of a resting dot (`lock_pattern_dot_size`). */
    val DotSize: Dp = 10.dp

    /** The width of the path (`lock_pattern_dot_line_width`). */
    val PathWidth: Dp = 2.dp

    /** The rise of the entrance animation (`color_lock_pattern_view_max_translate_y`). */
    val EnterTranslateY: Dp = 258.dp

    /**
     * The default [LockPatternViewColors] (`Widget.COUI.COUILockPatternView` and its `.Dark`
     * variant): dots `coui_lock_pattern_dot_color` #4D000000 / #66FFFFFF, success
     * `coui_lock_pattern_success_color` #000000 / #FFFFFF, error `coui_lock_pattern_error_color`
     * #EA3447, ripple peak `coui_lock_pattern_outer_circle_max_alpha` 0.1 / 0.2.
     */
    @Composable
    fun colors(
        dotColor: Color = if (isDark()) DotDark else DotLight,
        successColor: Color = if (isDark()) Color.White else Color.Black,
        errorColor: Color = ErrorColor,
        outerCircleMaxAlpha: Float = if (isDark()) 0.2f else 0.1f,
    ): LockPatternViewColors = remember(dotColor, successColor, errorColor, outerCircleMaxAlpha) {
        LockPatternViewColors(dotColor, successColor, errorColor, outerCircleMaxAlpha)
    }

    @Composable
    private fun isDark(): Boolean = COUITheme.colorScheme.background.luminance() < 0.5f
}

/**
 * Colors used by a [LockPatternView].
 *
 * @param dotColor The resting dots (`couiRegularColor`).
 * @param successColor The path and selected dots while drawing or correct (`couiSuccessColor`;
 *   COUI recolors the path with it, so `couiPathColor` has no visible effect).
 * @param errorColor The path and selected dots of a wrong pattern (`couiErrorColor`).
 * @param outerCircleMaxAlpha The peak alpha of the ripple of an added dot (`couiOuterCircleMaxAlpha`).
 */
@Immutable
data class LockPatternViewColors(
    val dotColor: Color,
    val successColor: Color,
    val errorColor: Color,
    val outerCircleMaxAlpha: Float,
)

/** Serializes a pattern as its 1-based dot numbers (`COUILockPatternUtils.patternToString`). */
fun patternToString(pattern: List<LockPatternCell>): String = pattern.joinToString(separator = "") { it.number.toString() }

/** Parses [patternToString] output (`COUILockPatternUtils.stringToPattern`). */
fun stringToPattern(string: String): List<LockPatternCell> = string.mapNotNull { char ->
    val number = char.digitToIntOrNull() ?: return@mapNotNull null
    if (number !in 1..9) null else LockPatternCell((number - 1) / 3, (number - 1) % 3)
}

private fun Offset.isSpecified(): Boolean = this != Offset.Unspecified

/** `pathAlpha` keyframes of `startWrongAnimator`: 1, .35, 1, .15, .5, 0 at fifths. */
private fun wrongKeyframes(fraction: Float): Float {
    val values = WrongKeyframeValues
    val position = (fraction.coerceIn(0f, 1f) * 5f)
    val index = position.toInt().coerceAtMost(4)
    val local = position - index
    return values[index] + (values[index + 1] - values[index]) * local
}

/** The outer ripple alpha keyframes (0, max at 0.5, 0) of `startOuterAnimation`. */
private fun outerAlpha(progress: Float, max: Float): Float {
    if (progress >= 1f) return 0f
    return if (progress < 0.5f) max * progress * 2f else max * (1f - progress) * 2f
}

private val WrongKeyframeValues = floatArrayOf(1f, 0.35f, 1f, 0.15f, 0.5f, 0f)

/**
 * `coui_lock_pattern_inner_circle.png` (90px at xxhdpi = 30dp): a 7.2dp disc fading from 98% at
 * the center to 69% and back to 79% at its edge, wrapped in a 20% glow fading out by 15dp.
 * Stops are fractions of the 15dp radius.
 */
private val InnerCircleStops = arrayOf(
    0f to Color.White.copy(alpha = 0.98f),
    0.133f to Color.White.copy(alpha = 0.886f),
    0.267f to Color.White.copy(alpha = 0.749f),
    0.356f to Color.White.copy(alpha = 0.69f),
    0.444f to Color.White.copy(alpha = 0.776f),
    0.478f to Color.White.copy(alpha = 0.788f),
    0.489f to Color.White.copy(alpha = 0.188f),
    0.533f to Color.White.copy(alpha = 0.204f),
    0.622f to Color.White.copy(alpha = 0.165f),
    0.711f to Color.White.copy(alpha = 0.114f),
    0.8f to Color.White.copy(alpha = 0.039f),
    0.889f to Color.White.copy(alpha = 0.008f),
    1f to Color.Transparent,
)

/** The radius of the inner circle drawable (half of its 30dp intrinsic size). */
private const val INNER_CIRCLE_RADIUS_DP = 15f

/** `coui_lock_pattern_outer_circle.png`: an 11.5px (xxhdpi) disc, scaled up to 7x. */
private const val OUTER_CIRCLE_RADIUS_DP = 3.83f

private const val OUTER_MAX_SCALE = 7f
private const val OUTER_DURATION_MS = 460
private const val INNER_DURATION_MS = 230
private const val LINE_DURATION_MS = 100
private const val WRONG_DURATION_MS = 1000
private const val MILLIS_PER_CIRCLE = 700
private const val HIT_FACTOR = 0.6f
private const val ENTER_ALPHA_MS = 167
private const val ENTER_ALPHA_DELAY_MS = 166
private const val ENTER_TRANSLATE_MS = 500
private const val ENTER_STAGGER_MS = 16

private val DotLight = Color(0x4D000000)
private val DotDark = Color(0x66FFFFFF)
private val ErrorColor = Color(0xFFEA3447)

/** `COUIEaseInterpolator`: cubic-bezier(0.33, 0, 0.67, 1). */
private val CouiEase = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

/** `COUIInEaseInterpolator`: cubic-bezier(0, 0, 0.1, 1). */
private val CouiInEase = CubicBezierEasing(0f, 0f, 0.1f, 1f)

/** `ValueAnimator`'s default `AccelerateDecelerateInterpolator`. */
private val AccelerateDecelerateEasing = Easing { (cos((it + 1f) * PI.toFloat()) / 2f) + 0.5f }
