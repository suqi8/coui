// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.utils

import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScrollModifierNode
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.invalidatePlacement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.WindowInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Velocity
import io.github.suqi8.coui.kmp.basic.LocalPullToRefreshState
import io.github.suqi8.coui.kmp.basic.PullToRefreshState
import io.github.suqi8.coui.kmp.basic.RefreshState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.round
import kotlin.math.sign

/**
 * @see overScrollOutOfBound
 */
@Stable
fun Modifier.overScrollVertical(
    nestedScrollToParent: Boolean = true,
    isEnabled: () -> Boolean = { platform() == Platform.Android || platform() == Platform.IOS },
): Modifier = overScrollOutOfBound(
    isVertical = true,
    nestedScrollToParent = nestedScrollToParent,
    isEnabled = isEnabled,
)

/**
 * @see overScrollOutOfBound
 */
@Stable
fun Modifier.overScrollHorizontal(
    nestedScrollToParent: Boolean = true,
    isEnabled: () -> Boolean = { platform() == Platform.Android || platform() == Platform.IOS },
): Modifier = overScrollOutOfBound(
    isVertical = false,
    nestedScrollToParent = nestedScrollToParent,
    isEnabled = isEnabled,
)

/**
 * Overscroll effect when scrolling to the boundary, with the physics of the COUI 17 scroll views
 * (see [COUIOverscrollEffect]): a drag past the edge moves the content by
 * `0.4 * (1 - |overscroll| / window size)` of the finger, a release springs it back on the
 * `SpringOverScroller` RK4 spring (tension multiple 2.15 vertically, 3.2 horizontally), and a fling
 * that runs into the edge carries on past it, its overshoot saturating at 37.31% of the window.
 * Unlike [COUIOverscrollEffect] it plays no edge vibration; add [scrollEndHaptic] for that.
 *
 * @param isVertical Whether the overscroll effect is vertical or horizontal.
 * @param nestedScrollToParent Whether to dispatch nested scroll events to parent.
 * @param isEnabled Whether the overscroll effect is enabled. Default is enabled on Android and iOS only.
 */
@Stable
fun Modifier.overScrollOutOfBound(
    isVertical: Boolean = true,
    nestedScrollToParent: Boolean = true,
    isEnabled: () -> Boolean = { platform() == Platform.Android || platform() == Platform.IOS },
): Modifier {
    if (!isEnabled()) return this

    return this
        .clipToBounds()
        .then(
            OverscrollElement(
                isVertical = isVertical,
                nestedScrollToParent = nestedScrollToParent,
            ),
        )
}

private data class OverscrollElement(
    val isVertical: Boolean,
    val nestedScrollToParent: Boolean,
) : ModifierNodeElement<OverscrollNode>() {
    override fun create(): OverscrollNode = OverscrollNode(
        isVertical = isVertical,
        nestedScrollToParent = nestedScrollToParent,
    )

    override fun update(node: OverscrollNode) {
        node.update(
            isVertical = isVertical,
            nestedScrollToParent = nestedScrollToParent,
        )
        node.invalidatePlacement()
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "overScrollOutOfBound"
        properties["isVertical"] = isVertical
        properties["nestedScrollToParent"] = nestedScrollToParent
    }
}

private class OverscrollNode(
    var isVertical: Boolean,
    var nestedScrollToParent: Boolean,
) : DelegatingNode(),
    CompositionLocalConsumerModifierNode,
    LayoutModifierNode,
    NestedScrollConnection {
    private val density: Density
        get() = currentValueOf(LocalDensity)

    private val windowInfo: WindowInfo
        get() = currentValueOf(LocalWindowInfo)

    private val overScrollState: OverScrollState
        get() = currentValueOf(LocalOverScrollState)

    private val pullToRefreshState: PullToRefreshState?
        get() = currentValueOf(LocalPullToRefreshState)

    private val dispatcher = NestedScrollDispatcher()
    private var animationJob: Job? = null
    private val offsetThreshold = 1f

    // Placement pixel-snaps via round(), so only re-place when the whole-pixel value changes.
    private var lastPlacedOffset = 0f

    private fun onOffsetChanged(value: Float) {
        val rounded = round(value)
        if (rounded != lastPlacedOffset) {
            lastPlacedOffset = rounded
            if (isAttached) invalidatePlacement()
        }
    }

    private val verticalAxis = COUIOverScrollAxis(VerticalSpringBackTensionMultiple, ::onOffsetChanged)
    private val horizontalAxis = COUIOverScrollAxis(HorizontalSpringBackTensionMultiple, ::onOffsetChanged)

    private val axis: COUIOverScrollAxis
        get() = if (isVertical) verticalAxis else horizontalAxis

    val offset: Float
        get() = axis.offset

    private var scrollRange: Float = 0f
    private var cachedScrollRangeDensity: Density? = null
    private var cachedScrollRangeWindowInfo: WindowInfo? = null

    override fun onAttach() {
        super.onAttach()
        updateScrollRange()
        delegate(nestedScrollModifierNode(this, dispatcher))
    }

    override fun onDetach() {
        super.onDetach()
        resetState()
    }

    fun update(
        isVertical: Boolean,
        nestedScrollToParent: Boolean,
    ) {
        var rangeChanged = false
        if (this.isVertical != isVertical) {
            rangeChanged = true
            animationJob?.cancel()
            resetState()
        }

        this.isVertical = isVertical
        this.nestedScrollToParent = nestedScrollToParent

        if (rangeChanged && isAttached) {
            cachedScrollRangeDensity = null
            updateScrollRange()
        }
    }

    private fun updateScrollRange() {
        val currentDensity = density
        val currentWindowInfo = windowInfo
        if (currentDensity == cachedScrollRangeDensity && currentWindowInfo == cachedScrollRangeWindowInfo) return
        cachedScrollRangeDensity = currentDensity
        cachedScrollRangeWindowInfo = currentWindowInfo
        scrollRange = with(currentDensity) {
            if (isVertical) {
                currentWindowInfo.containerDpSize.height.toPx()
            } else {
                currentWindowInfo.containerDpSize.width.toPx()
            }
        }
    }

    private fun resetState() {
        verticalAxis.reset()
        horizontalAxis.reset()
        if (isAttached) {
            overScrollState.isOverScrollActive = false
        }
    }

    private fun updateOverScrollState() {
        if (!isAttached) return
        val isActive = abs(offset) > offsetThreshold
        if (overScrollState.isOverScrollActive != isActive) {
            overScrollState.isOverScrollActive = isActive
        }
    }

    /**
     * Springs the content back to the edge, from rest or, with [overFling], carrying a fling that
     * reached the edge at [velocity] px/s past it.
     */
    private fun startSpringBack(velocity: Float, overFling: Boolean) {
        animationJob?.cancel()
        if (!overFling && abs(offset) <= offsetThreshold) {
            resetState()
            return
        }
        val springAxis = axis
        animationJob = coroutineScope.launch {
            springAxis.springBack(velocity = velocity, overFling = overFling, screenSize = scrollRange)
            updateOverScrollState()
        }
    }

    private fun shouldBypassForPullToRefresh(): Boolean {
        // When pull-to-refresh is active (not Idle), always bypass.
        return pullToRefreshState != null && pullToRefreshState?.refreshState != RefreshState.Idle && isVertical
    }

    /** Reclaims a stale offset once the child can scroll again towards it (e.g. pagination); otherwise [onPreFling] swallows the next fling. */
    private fun unwindStaleOffset(consumedDelta: Float) {
        if (abs(offset) <= offsetThreshold || consumedDelta == 0f) return
        if (sign(consumedDelta) != sign(offset)) return
        axis.dragBack(-consumedDelta, scrollRange)
    }

    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        updateScrollRange()
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            placeable.placeWithLayer(0, 0) {
                if (isVertical) {
                    translationY = round(offset)
                } else {
                    translationX = round(offset)
                }
                clip = true
            }
        }
    }

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        if (!isAttached) return Offset.Zero
        updateOverScrollState()

        if (shouldBypassForPullToRefresh() || source != NestedScrollSource.UserInput) {
            return dispatcher.dispatchPreScroll(available, source)
        }

        // A drag takes over a running spring from where it is.
        animationJob?.cancel()

        val parentConsumed = if (nestedScrollToParent) {
            dispatcher.dispatchPreScroll(available, source)
        } else {
            Offset.Zero
        }

        val realAvailable = available - parentConsumed
        val delta = if (isVertical) realAvailable.y else realAvailable.x

        if (abs(offset) <= offsetThreshold || delta == 0f || sign(delta) == sign(offset)) {
            return parentConsumed
        }

        // A drag back towards the content first undoes the overscroll.
        val consumed = axis.dragBack(delta, scrollRange)
        updateOverScrollState()
        return if (isVertical) {
            Offset(parentConsumed.x, consumed + parentConsumed.y)
        } else {
            Offset(consumed + parentConsumed.x, parentConsumed.y)
        }
    }

    override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
        if (!isAttached) return Offset.Zero
        updateOverScrollState()

        if (shouldBypassForPullToRefresh() || source != NestedScrollSource.UserInput) {
            // A running spring settles to zero on its own; only reclaim when it was interrupted.
            if (animationJob?.isActive != true) {
                unwindStaleOffset(if (isVertical) consumed.y else consumed.x)
            }
            return dispatcher.dispatchPostScroll(consumed, available, source)
        }

        animationJob?.cancel()
        unwindStaleOffset(if (isVertical) consumed.y else consumed.x)

        val parentConsumed = if (nestedScrollToParent) {
            dispatcher.dispatchPostScroll(consumed, available, source)
        } else {
            Offset.Zero
        }

        val realAvailable = available - parentConsumed
        val delta = if (isVertical) realAvailable.y else realAvailable.x

        axis.drag(delta, scrollRange)
        updateOverScrollState()
        return if (isVertical) Offset(parentConsumed.x, available.y) else Offset(available.x, parentConsumed.y)
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        if (!isAttached) return Velocity.Zero
        updateOverScrollState()

        if (shouldBypassForPullToRefresh() && !overScrollState.isOverScrollActive) {
            return dispatcher.dispatchPreFling(available)
        }

        animationJob?.cancel()

        val parentConsumed = if (nestedScrollToParent) {
            dispatcher.dispatchPreFling(available)
        } else {
            Velocity.Zero
        }

        val realAvailable = available - parentConsumed
        val velocity = if (isVertical) realAvailable.y else realAvailable.x

        if (abs(offset) > offsetThreshold) {
            // COUIRecyclerView: a release while overscrolled springs back from rest.
            startSpringBack(velocity = 0f, overFling = false)
            if (sign(velocity) != sign(offset)) {
                // A fling back towards the content scrolls it (performOverScrollingReverseFling).
                return parentConsumed
            }
            // A fling further out is dropped.
            return parentConsumed + if (isVertical) Velocity(0f, realAvailable.y) else Velocity(realAvailable.x, 0f)
        }

        return parentConsumed
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        if (!isAttached) return Velocity.Zero
        updateOverScrollState()

        if (shouldBypassForPullToRefresh() && !overScrollState.isOverScrollActive) {
            return dispatcher.dispatchPostFling(consumed, available)
        }

        val parentConsumed = if (nestedScrollToParent) {
            dispatcher.dispatchPostFling(consumed, available)
        } else {
            Velocity.Zero
        }

        val realAvailable = available - parentConsumed
        val velocity = if (isVertical) realAvailable.y else realAvailable.x

        if (animationJob?.isActive == true) return parentConsumed
        if (abs(offset) > offsetThreshold) {
            startSpringBack(velocity = 0f, overFling = false)
        } else if (abs(velocity) > MIN_OVER_FLING_VELOCITY) {
            // COUINestedScrollView.onOverScrolled: a fling reaching the edge carries on past it.
            startSpringBack(velocity = velocity, overFling = true)
        } else {
            resetState()
            return parentConsumed
        }

        return parentConsumed + if (isVertical) Velocity(0f, velocity) else Velocity(velocity, 0f)
    }

    private companion object {
        /** Leftover fling speeds below this (px/s) stop at the edge. */
        const val MIN_OVER_FLING_VELOCITY = 1f
    }
}

/**
 * OverScrollState is used to control the overscroll effect.
 *
 * @param isOverScrollActive Whether the overscroll effect is active.
 */
class OverScrollState {
    var isOverScrollActive by mutableStateOf(false)
        internal set
}

/**
 * [LocalOverScrollState] is used to provide the [OverScrollState] instance to the composition.
 *
 * @see OverScrollState
 */
val LocalOverScrollState = compositionLocalOf { OverScrollState() }
