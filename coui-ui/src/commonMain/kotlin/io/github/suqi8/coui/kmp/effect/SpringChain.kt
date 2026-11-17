// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.effect

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * The edge a spring chain is pulled from, after COUI `COUIGridSpringChain` `BACK_TO_TOP` /
 * `BACK_TO_LEFT` ([Start]) and `BACK_TO_BOTTOM` / `BACK_TO_RIGHT` ([End]).
 */
enum class SpringChainEdge {
    /** The first row of the list sits at this edge (COUI `BACK_TO_TOP` / `BACK_TO_LEFT`). */
    Start,

    /** The last row of the list sits at this edge (COUI `BACK_TO_BOTTOM` / `BACK_TO_RIGHT`). */
    End,
}

/**
 * Computes the share of the pull distance one chain step contributes, after COUI
 * `COUIGridSpringChain.TransCalculator.getTrans(index, distance, direction)`.
 *
 * With accumulation on (COUI `enableAutoAcc`, the default) the translation of the row at chain index
 * `c` is the sum of [getTrans] over the steps `0..c`, so rows further from the pulled edge move further
 * and the list fans out.
 */
fun interface SpringChainTransCalculator {
    /**
     * Returns the translation step for [index].
     *
     * @param index The chain index of the step, 0 at the pulled edge.
     * @param distance The damped pull distance in pixels, positive when pulled away from [edge].
     * @param edge The edge being pulled.
     */
    fun getTrans(index: Int, distance: Float, edge: SpringChainEdge): Float
}

/**
 * Defaults of the spring chain, ported from COUI `COUIGridSpringChain` / `COUISpringChain`.
 */
object SpringChainDefaults {
    /** The Origami tension of the control spring (COUIGridSpringChain `MAIN_TENSION`). */
    val MainTension = 150f

    /** The Origami friction of the control spring (COUIGridSpringChain `MAIN_FRICTION`). */
    val MainFriction = 10f

    /** The Origami tension of the attached springs (COUIGridSpringChain `ATTACHMENT_TENSION`). */
    val AttachmentTension = 150f

    /** The Origami friction of the attached springs (COUIGridSpringChain `ATTACHMENT_FRICTION`). */
    val AttachmentFriction = 10f

    /** The control spring tension of a bare `COUISpringChain.create(SpringSystem)` (`DEFAULT_MAIN_TENSION`). */
    val BaseMainTension = 40f

    /** The control spring friction of a bare `COUISpringChain` (`DEFAULT_MAIN_FRICTION`). */
    val BaseMainFriction = 6f

    /** The attached spring tension of a bare `COUISpringChain` (`DEFAULT_ATTACHMENT_TENSION`). */
    val BaseAttachmentTension = 70f

    /** The attached spring friction of a bare `COUISpringChain` (`DEFAULT_ATTACHMENT_FRICTION`). */
    val BaseAttachmentFriction = 10f

    /** The number of rows the chain tracks (COUIGridSpringChain `MAX_Y` / `MAX_X`). */
    val MaxRows = 50

    /** The per-step share of the pull distance (COUIGridSpringChain `DefaultTransCalculator`). */
    val TransFactor = 0.1f

    /**
     * The default step calculator: [TransFactor] of the pull distance per step
     * (COUIGridSpringChain `DefaultTransCalculator`, installed for both edges by `COUIChainDecorator`).
     */
    val TransCalculator: SpringChainTransCalculator = SpringChainTransCalculator { _, distance, _ -> distance * TransFactor }
}

/**
 * Creates and remembers a [SpringChainState] for a [LazyListState]: the row of an item is its index.
 *
 * @param lazyListState The state of the list the chain is attached to.
 * @param mainTension The Origami tension of the control spring at the pulled edge.
 * @param mainFriction The Origami friction of the control spring.
 * @param attachmentTension The Origami tension of the springs that follow it.
 * @param attachmentFriction The Origami friction of the springs that follow it.
 * @param maxRows The number of rows from the pulled edge the chain moves; rows further away stay put,
 *   as COUI refuses to add items past `MAX_Y`.
 * @param transCalculator The per-step translation, see [SpringChainTransCalculator].
 * @param accumulate Whether a row moves by the sum of the steps up to its index (COUI `enableAutoAcc`)
 *   rather than by its own step only.
 */
@Composable
fun rememberSpringChainState(
    lazyListState: LazyListState,
    mainTension: Float = SpringChainDefaults.MainTension,
    mainFriction: Float = SpringChainDefaults.MainFriction,
    attachmentTension: Float = SpringChainDefaults.AttachmentTension,
    attachmentFriction: Float = SpringChainDefaults.AttachmentFriction,
    maxRows: Int = SpringChainDefaults.MaxRows,
    transCalculator: SpringChainTransCalculator = SpringChainDefaults.TransCalculator,
    accumulate: Boolean = true,
): SpringChainState {
    val scope = rememberCoroutineScope()
    val layoutDirection = LocalLayoutDirection.current
    val state = remember(lazyListState, scope) {
        SpringChainState(
            scrollableState = lazyListState,
            scope = scope,
            target = object : SpringChainTarget {
                override fun isVertical(): Boolean = lazyListState.layoutInfo.orientation == Orientation.Vertical

                override fun isReverseLayout(): Boolean = lazyListState.layoutInfo.reverseLayout

                override fun lastRowIndex(): Int = lazyListState.layoutInfo.totalItemsCount - 1

                override fun viewportLength(): Int {
                    val size = lazyListState.layoutInfo.viewportSize
                    return if (isVertical()) size.height else size.width
                }
            },
        )
    }
    SideEffect {
        state.configure(
            mainTension = mainTension,
            mainFriction = mainFriction,
            attachmentTension = attachmentTension,
            attachmentFriction = attachmentFriction,
            maxRows = maxRows,
            transCalculator = transCalculator,
            accumulate = accumulate,
            layoutDirection = layoutDirection,
        )
    }
    return state
}

/**
 * Creates and remembers a [SpringChainState] for a [LazyGridState]. Pass the row (column for a
 * horizontal grid) of each item to [springChainItem]; with fixed spans it is `index / columns`.
 *
 * See the [LazyListState] overload for the parameters.
 */
@Composable
fun rememberSpringChainState(
    lazyGridState: LazyGridState,
    mainTension: Float = SpringChainDefaults.MainTension,
    mainFriction: Float = SpringChainDefaults.MainFriction,
    attachmentTension: Float = SpringChainDefaults.AttachmentTension,
    attachmentFriction: Float = SpringChainDefaults.AttachmentFriction,
    maxRows: Int = SpringChainDefaults.MaxRows,
    transCalculator: SpringChainTransCalculator = SpringChainDefaults.TransCalculator,
    accumulate: Boolean = true,
): SpringChainState {
    val scope = rememberCoroutineScope()
    val layoutDirection = LocalLayoutDirection.current
    val state = remember(lazyGridState, scope) {
        SpringChainState(
            scrollableState = lazyGridState,
            scope = scope,
            target = object : SpringChainTarget {
                override fun isVertical(): Boolean = lazyGridState.layoutInfo.orientation == Orientation.Vertical

                override fun isReverseLayout(): Boolean = lazyGridState.layoutInfo.reverseLayout

                override fun lastRowIndex(): Int {
                    // At the end edge the last visible item is the last item of the grid.
                    val info = lazyGridState.layoutInfo
                    val last = info.visibleItemsInfo.lastOrNull() ?: return -1
                    return if (info.orientation == Orientation.Vertical) last.row else last.column
                }

                override fun viewportLength(): Int {
                    val size = lazyGridState.layoutInfo.viewportSize
                    return if (isVertical()) size.height else size.width
                }
            },
        )
    }
    SideEffect {
        state.configure(
            mainTension = mainTension,
            mainFriction = mainFriction,
            attachmentTension = attachmentTension,
            attachmentFriction = attachmentFriction,
            maxRows = maxRows,
            transCalculator = transCalculator,
            accumulate = accumulate,
            layoutDirection = layoutDirection,
        )
    }
    return state
}

/**
 * Attaches the spring chain to a lazy list or grid: put it on the `LazyColumn` / `LazyRow` /
 * `LazyVerticalGrid` itself and give every item [springChainItem].
 *
 * The chain only reacts to touch drags, as COUI's `COUIGridSpringChainScrollView.onEdgeSpringEvent`
 * reads raw `MotionEvent`s: a fling or a mouse wheel that reaches the edge leaves it at rest.
 */
fun Modifier.springChain(state: SpringChainState): Modifier = this
    .pointerInput(state) {
        awaitPointerEventScope {
            var pointers = 0
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                var pressed = 0
                val changes = event.changes
                for (i in changes.indices) {
                    if (changes[i].pressed) pressed++
                }
                if (pointers == 0 && pressed > 0) {
                    state.onTouchDown()
                } else if (pointers > 0 && pressed == 0) {
                    state.onTouchUp()
                } else if (pressed != pointers) {
                    state.onPointerCountChanged()
                }
                pointers = pressed
            }
        }
    }
    .nestedScroll(state.nestedScrollConnection)

/**
 * Moves an item with the spring chain of [state].
 *
 * @param state The [SpringChainState] of the list.
 * @param row The row of the item along the scroll axis: the item index in a list, the row (column
 *   for a horizontal grid) in a grid.
 */
fun Modifier.springChainItem(state: SpringChainState, row: Int): Modifier = graphicsLayer {
    val translation = state.translationOf(row)
    if (state.isVertical) translationY = translation else translationX = translation
}

/** What the chain needs to know about the list it follows. */
internal interface SpringChainTarget {
    fun isVertical(): Boolean

    fun isReverseLayout(): Boolean

    fun lastRowIndex(): Int

    fun viewportLength(): Int
}

/**
 * The state of a COUI edge spring chain, after `COUIGridSpringChain` driven by
 * `COUIGridSpringChainScrollView`.
 *
 * - **Pull**: while a finger drags a list that cannot scroll further, the drag is damped by
 *   `getFormalDistance` (each move closes `1 - |distance| / (2 x viewport)` of the gap to the raw
 *   finger travel) and every row from the pulled edge is translated by the accumulated
 *   [SpringChainTransCalculator] steps, so the rows fan out from the edge.
 * - **Rebound**: on release a chain of Rebound springs (`COUISpringChain`) returns the rows: the
 *   control spring at the pulled edge animates to 0 on the main config, and every other spring
 *   chases the current value of its neighbour towards the edge on the attachment config. The springs
 *   are integrated with Rebound's fixed 1ms RK4 step (`Spring.advance`, frame delta capped at 64ms,
 *   rest at 0.1px and 0.1px/s), so the follow delay and overshoot match the original.
 * - **Interrupt**: touching the list while the chain is moving freezes every row in place
 *   (`releaseSpring`); the frozen offsets stay until release, then rebound from where they were.
 *
 * Per-frame work runs on preallocated arrays and allocates nothing.
 */
@Stable
class SpringChainState internal constructor(
    private val scrollableState: ScrollableState,
    private val scope: CoroutineScope,
    private val target: SpringChainTarget,
) {
    private var mainTension = tensionFromOrigami(SpringChainDefaults.MainTension.toDouble())
    private var mainFriction = frictionFromOrigami(SpringChainDefaults.MainFriction.toDouble())
    private var attachmentTension = tensionFromOrigami(SpringChainDefaults.AttachmentTension.toDouble())
    private var attachmentFriction = frictionFromOrigami(SpringChainDefaults.AttachmentFriction.toDouble())
    private var transCalculator: SpringChainTransCalculator = SpringChainDefaults.TransCalculator
    private var accumulate = true
    private var rtl = false

    // Chain arrays, indexed by the rank of a row from the edge the chain was last pulled from.
    private var capacity = SpringChainDefaults.MaxRows
    private var current = FloatArray(capacity)
    private var last = FloatArray(capacity)
    private var position = DoubleArray(capacity)
    private var velocity = DoubleArray(capacity)
    private var previousPosition = DoubleArray(capacity)
    private var previousVelocity = DoubleArray(capacity)
    private var tempPosition = DoubleArray(capacity)
    private var tempVelocity = DoubleArray(capacity)
    private var endValue = DoubleArray(capacity)
    private var startValue = DoubleArray(capacity)
    private var accumulator = DoubleArray(capacity)
    private var wasAtRest = BooleanArray(capacity)
    private var active = BooleanArray(capacity)

    /** Bumped after every change of [current] so the item layers redraw. */
    private var frame by mutableIntStateOf(0)

    /** The edge the arrays are ranked from; null until the first pull. */
    private var arrayEdge: SpringChainEdge? = null

    /** The last row of the list when the arrays were ranked from [SpringChainEdge.End]. */
    private var arrayLastRow = 0

    /** The number of tracked rows (COUI `currentMaxY`, capped at the capacity). */
    private var rows = 0

    /** -1 or 1: the screen sign of a distance pulled away from the start edge. */
    private var axisSign = 1f

    /** COUI `yDirection` / `xDirection`: the edge whose translations are applied, null for -1. */
    private var appliedEdge: SpringChainEdge? = null

    /** COUI `lastMoveDirection`, initially `BACK_TO_BOTTOM`. */
    private var lastMoveEdge = SpringChainEdge.End

    /** COUI `lastReboundDirection`, null for 0. */
    private var lastReboundEdge: SpringChainEdge? = null

    private var controlIndex = -1
    private var reboundJob: Job? = null

    // COUIGridSpringChainScrollView touch tracking, in start-edge-positive axis pixels.
    private var touching = false
    private var rawDistance = 0f
    private var curDistance = 0f
    private var hasReleasedSpring = false
    private var pulling = false

    /** Whether the list scrolls vertically, read by [springChainItem]. */
    var isVertical: Boolean = true
        private set

    /** Whether the rebound springs are moving (COUI `isSpringSystemRunning() != 0`). */
    val isRunning: Boolean get() = reboundJob?.isActive == true

    /**
     * The translation of the item on [row] along the scroll axis, in pixels. Reading it inside a
     * layer or draw block subscribes that block to every chain frame.
     */
    fun translationOf(row: Int): Float {
        // Subscribe the reader to chain frames.
        frame
        val edge = arrayEdge ?: return 0f
        val rank = if (edge == SpringChainEdge.Start) row else arrayLastRow - row
        return if (rank in 0 until rows) current[rank] * axisSign else 0f
    }

    internal fun configure(
        mainTension: Float,
        mainFriction: Float,
        attachmentTension: Float,
        attachmentFriction: Float,
        maxRows: Int,
        transCalculator: SpringChainTransCalculator,
        accumulate: Boolean,
        layoutDirection: LayoutDirection,
    ) {
        this.mainTension = tensionFromOrigami(mainTension.toDouble())
        this.mainFriction = frictionFromOrigami(mainFriction.toDouble())
        this.attachmentTension = tensionFromOrigami(attachmentTension.toDouble())
        this.attachmentFriction = frictionFromOrigami(attachmentFriction.toDouble())
        this.transCalculator = transCalculator
        this.accumulate = accumulate
        this.rtl = layoutDirection == LayoutDirection.Rtl
        this.isVertical = target.isVertical()
        val newCapacity = maxRows.coerceAtLeast(1)
        if (newCapacity != capacity && !isRunning) resize(newCapacity)
    }

    private fun resize(newCapacity: Int) {
        capacity = newCapacity
        current = FloatArray(newCapacity)
        last = FloatArray(newCapacity)
        position = DoubleArray(newCapacity)
        velocity = DoubleArray(newCapacity)
        previousPosition = DoubleArray(newCapacity)
        previousVelocity = DoubleArray(newCapacity)
        tempPosition = DoubleArray(newCapacity)
        tempVelocity = DoubleArray(newCapacity)
        endValue = DoubleArray(newCapacity)
        startValue = DoubleArray(newCapacity)
        accumulator = DoubleArray(newCapacity)
        wasAtRest = BooleanArray(newCapacity)
        active = BooleanArray(newCapacity)
        rows = 0
        arrayEdge = null
        frame++
    }

    internal val nestedScrollConnection: NestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (!touching || source != NestedScrollSource.UserInput) return Offset.Zero
            val vertical = target.isVertical()
            isVertical = vertical
            val screenDelta = if (vertical) available.y else available.x
            if (screenDelta == 0f) return Offset.Zero
            val reverse = target.isReverseLayout() != (!vertical && rtl)
            val sign = if (reverse) -1f else 1f
            // ACTION_MOVE: the raw finger travel since downY, damped by getFormalDistance.
            rawDistance += screenDelta * sign
            curDistance = formalDistance(rawDistance, curDistance)
            val consumed = when {
                curDistance > 0f && !scrollableState.canScrollBackward -> {
                    axisSign = sign
                    updateMoveTranslation(curDistance, SpringChainEdge.Start)
                    pulling = true
                    true
                }

                curDistance < 0f && !scrollableState.canScrollForward -> {
                    axisSign = sign
                    updateMoveTranslation(curDistance, SpringChainEdge.End)
                    pulling = true
                    true
                }

                else -> {
                    // downY = y: the pull restarts from here.
                    rawDistance = 0f
                    if (hasReleasedSpring || pulling) {
                        // COUI only resets here after an interrupted rebound; a pull that crosses
                        // back over the edge is settled too so no rows are left offset.
                        updateMoveTranslation(0f, null)
                    }
                    val wasPulling = pulling
                    pulling = false
                    wasPulling
                }
            }
            return if (consumed) available else Offset.Zero
        }
    }

    /** COUI ACTION_DOWN. */
    internal fun onTouchDown() {
        touching = true
        rawDistance = 0f
        curDistance = 0f
        pulling = false
        hasReleasedSpring = isRunning
        if (hasReleasedSpring) releaseSpring()
    }

    /** COUI ACTION_POINTER_DOWN / ACTION_POINTER_UP: the next move inherits the current distance. */
    internal fun onPointerCountChanged() {
        rawDistance = curDistance
    }

    /** COUI ACTION_UP / ACTION_CANCEL. */
    internal fun onTouchUp() {
        if (!touching) return
        touching = false
        curDistance = formalDistance(rawDistance, curDistance)
        when {
            curDistance > 0f && !scrollableState.canScrollBackward && pulling -> startRebound(SpringChainEdge.Start)
            curDistance < 0f && !scrollableState.canScrollForward && pulling -> startRebound(SpringChainEdge.End)
            hasReleasedSpring -> startRebound(null)
        }
        pulling = false
    }

    /** COUIGridSpringChainScrollView.getFormalDistance. */
    private fun formalDistance(distance: Float, lastDistance: Float): Float {
        if (lastDistance * distance <= 0f) return distance
        val half = target.viewportLength() * MAX_DISTANCE_RATIO
        val lastTranslation = if (rows > 0) abs(last[0]) else 0f
        val limit = ((half - lastTranslation) / MAX_DISTANCE_FACTOR)
            .coerceAtLeast(0f)
            .coerceAtMost(half / MAX_DISTANCE_FACTOR)
        val factor = (1f - abs(lastDistance / limit)).coerceIn(0f, 1f)
        return factor * (distance - lastDistance) + lastDistance
    }

    /** COUIGridSpringChain.updateMoveTranslation: row translation = steps + frozen offset. */
    private fun updateMoveTranslation(distance: Float, edge: SpringChainEdge?) {
        val moveEdge = edge ?: arrayEdge ?: lastMoveEdge
        if (edge != null) lastMoveEdge = edge
        if (arrayEdge != moveEdge) {
            // The frozen offsets belong to the other edge's ranking; start this edge from rest.
            arrayEdge = moveEdge
            last.fill(0f)
        }
        val lastRow = target.lastRowIndex()
        if (moveEdge == SpringChainEdge.End) arrayLastRow = lastRow
        rows = (lastRow + 1).coerceIn(0, capacity)
        appliedEdge = moveEdge
        // calculateTranslation: the end edge ranks its rows from currentMaxY - index, one step more
        // than the start edge, and accumulates getTrans over 0..calIndex.
        val offset = if (moveEdge == SpringChainEdge.End) 1 else 0
        val calculator = transCalculator
        var sum = 0f
        var step = 0
        for (rank in 0 until rows) {
            val calIndex = rank + offset
            val translation = if (accumulate) {
                while (step <= calIndex) {
                    sum += calculator.getTrans(step, distance, moveEdge)
                    step++
                }
                sum
            } else {
                calculator.getTrans(calIndex, distance, moveEdge)
            }
            current[rank] = translation + last[rank]
        }
        frame++
    }

    /** COUIGridSpringChain.releaseSpring: freeze every spring at its current value. */
    private fun releaseSpring() {
        reboundJob?.cancel()
        reboundJob = null
        for (i in 0 until rows) {
            val value = position[i]
            onSpringUpdate(i)
            endValue[i] = value
            tempPosition[i] = value
            velocity[i] = 0.0
            startValue[i] = value
            active[i] = false
        }
        appliedEdge = null
        frame++
    }

    /** COUIGridSpringChain.startRebound. */
    private fun startRebound(edge: SpringChainEdge?) {
        val reboundEdge = arrayEdge ?: return
        if (edge == null && lastReboundEdge == null) return
        lastReboundEdge = reboundEdge
        appliedEdge = reboundEdge
        if (rows == 0) return
        for (i in 0 until rows) {
            // setCurrentValue(value, false) + setVelocity(0).
            val value = current[i].toDouble()
            position[i] = value
            startValue[i] = value
            velocity[i] = 0.0
            tempPosition[i] = value
            tempVelocity[i] = 0.0
            accumulator[i] = 0.0
            wasAtRest[i] = false
            active[i] = true
            last[i] = current[i]
        }
        // The control spring is the first row from the top with a value >= 0, else the last row.
        var control = -1
        if (reboundEdge == SpringChainEdge.Start) {
            for (i in 0 until rows) {
                if (position[i] >= 0.0) {
                    control = i
                    break
                }
            }
            if (control == -1) control = rows - 1
        } else {
            for (i in rows - 1 downTo 0) {
                if (position[i] >= 0.0) {
                    control = i
                    break
                }
            }
            if (control == -1) control = 0
        }
        controlIndex = control
        // The attached springs start chasing their neighbour towards the control spring.
        for (i in 0 until rows) {
            endValue[i] = when {
                i == control -> 0.0
                i < control -> position[i + 1]
                else -> position[i - 1]
            }
        }
        frame++
        reboundJob?.cancel()
        reboundJob = scope.launch {
            var lastFrame = withFrameNanos { it }
            while (true) {
                val now = withFrameNanos { it }
                val deltaMs = (now - lastFrame) / 1_000_000.0
                lastFrame = now
                if (!advanceSystem(deltaMs)) break
            }
        }
    }

    /**
     * BaseSpringSystem.advance: steps every active spring in Rebound's activation order (top to
     * bottom), dropping those at rest. Returns whether any spring is still active.
     */
    private fun advanceSystem(deltaMs: Double): Boolean {
        var anyActive = false
        val fromTop = arrayEdge != SpringChainEdge.End
        for (n in 0 until rows) {
            val i = if (fromTop) n else rows - 1 - n
            if (!active[i]) continue
            if (isAtRest(i)) {
                active[i] = false
                continue
            }
            advanceSpring(i, deltaMs / 1000.0)
            anyActive = true
        }
        frame++
        return anyActive || hasActiveSpring()
    }

    private fun hasActiveSpring(): Boolean {
        for (i in 0 until rows) {
            if (active[i] && !isAtRest(i)) return true
        }
        return false
    }

    private fun tension(i: Int): Double = if (i == controlIndex) mainTension else attachmentTension

    private fun friction(i: Int): Double = if (i == controlIndex) mainFriction else attachmentFriction

    /** Spring.isAtRest with COUISpringChain's 0.1 rest thresholds. */
    private fun isAtRest(i: Int): Boolean = abs(velocity[i]) <= REST_THRESHOLD &&
        (abs(endValue[i] - position[i]) <= REST_THRESHOLD || tension(i) == 0.0)

    /** Rebound Spring.advance, ported literally including its RK4 first-stage quirk. */
    private fun advanceSpring(i: Int, deltaSeconds: Double) {
        val atRest = isAtRest(i)
        if (atRest && wasAtRest[i]) return
        accumulator[i] += if (deltaSeconds <= MAX_DELTA_TIME_SEC) deltaSeconds else MAX_DELTA_TIME_SEC
        val tension = tension(i)
        val friction = friction(i)
        var pos = position[i]
        var vel = velocity[i]
        var tempPos = tempPosition[i]
        var tempVel = tempVelocity[i]
        var acc: Double
        while (true) {
            acc = accumulator[i]
            if (acc < SOLVER_TIMESTEP_SEC) break
            val remaining = acc - SOLVER_TIMESTEP_SEC
            accumulator[i] = remaining
            if (remaining < SOLVER_TIMESTEP_SEC) {
                previousPosition[i] = pos
                previousVelocity[i] = vel
            }
            val end = endValue[i]
            val aAcceleration = ((end - tempPos) * tension) - (friction * vel)
            val bPosition = (vel * SOLVER_TIMESTEP_SEC * 0.5) + pos
            val bVelocity = vel + (aAcceleration * SOLVER_TIMESTEP_SEC * 0.5)
            val bAcceleration = ((end - bPosition) * tension) - (friction * bVelocity)
            val cPosition = pos + (bVelocity * SOLVER_TIMESTEP_SEC * 0.5)
            val cVelocity = vel + (bAcceleration * SOLVER_TIMESTEP_SEC * 0.5)
            val cAcceleration = ((end - cPosition) * tension) - (friction * cVelocity)
            val dPosition = pos + (cVelocity * SOLVER_TIMESTEP_SEC)
            val dVelocity = vel + (cAcceleration * SOLVER_TIMESTEP_SEC)
            val dAcceleration = ((end - dPosition) * tension) - (friction * dVelocity)
            pos += (vel + ((bVelocity + cVelocity) * 2.0) + dVelocity) * ONE_SIXTH * SOLVER_TIMESTEP_SEC
            vel += (aAcceleration + ((bAcceleration + cAcceleration) * 2.0) + dAcceleration) * ONE_SIXTH * SOLVER_TIMESTEP_SEC
            tempPos = dPosition
            tempVel = dVelocity
        }
        tempPosition[i] = tempPos
        tempVelocity[i] = tempVel
        position[i] = pos
        velocity[i] = vel
        if (acc > 0.0) {
            // interpolate(alpha): blend with the state one solver step back.
            val alpha = acc / SOLVER_TIMESTEP_SEC
            position[i] = position[i] * alpha + previousPosition[i] * (1.0 - alpha)
            velocity[i] = velocity[i] * alpha + previousVelocity[i] * (1.0 - alpha)
        }
        var nowAtRest = atRest
        if (isAtRest(i)) {
            if (tension > 0.0) {
                startValue[i] = endValue[i]
                position[i] = endValue[i]
            } else {
                endValue[i] = position[i]
                startValue[i] = position[i]
            }
            velocity[i] = 0.0
            nowAtRest = true
        }
        if (wasAtRest[i]) wasAtRest[i] = false
        if (nowAtRest) wasAtRest[i] = true
        onSpringUpdate(i)
    }

    /**
     * COUISpringChain.onSpringUpdate + GridSpringListener: the neighbour away from the control spring
     * chases this spring, then the row takes the spring value.
     */
    private fun onSpringUpdate(i: Int) {
        val control = controlIndex
        val value = position[i]
        if (control >= 0) {
            val next: Int
            val previous: Int
            when {
                i == control -> {
                    previous = i - 1
                    next = i + 1
                }

                i < control -> {
                    previous = i - 1
                    next = -1
                }

                else -> {
                    previous = -1
                    next = i + 1
                }
            }
            if (next in 0 until rows) setEndValue(next, value)
            if (previous in 0 until rows) setEndValue(previous, value)
        }
        if (appliedEdge != null) {
            val translation = value.toFloat()
            current[i] = translation
            last[i] = translation
        }
    }

    /** Spring.setEndValue. */
    private fun setEndValue(i: Int, value: Double) {
        if (endValue[i] != value || !isAtRest(i)) {
            startValue[i] = position[i]
            endValue[i] = value
            active[i] = true
        }
    }

    private companion object {
        const val MAX_DISTANCE_RATIO = 0.5f
        const val MAX_DISTANCE_FACTOR = 0.25f
        const val REST_THRESHOLD = 0.1
        const val MAX_DELTA_TIME_SEC = 0.064
        const val SOLVER_TIMESTEP_SEC = 0.001
        const val ONE_SIXTH = 0.16666666666666666

        /** OrigamiValueConverter.tensionFromOrigamiValue. */
        fun tensionFromOrigami(value: Double): Double = if (value == 0.0) 0.0 else ((value - 30.0) * 3.62) + 194.0

        /** OrigamiValueConverter.frictionFromOrigamiValue. */
        fun frictionFromOrigami(value: Double): Double = if (value == 0.0) 0.0 else ((value - 8.0) * 3.0) + 25.0
    }
}
