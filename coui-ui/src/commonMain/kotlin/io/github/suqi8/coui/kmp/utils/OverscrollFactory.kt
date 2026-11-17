// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.utils

import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.OverscrollFactory
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.invalidatePlacement
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.WindowInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Velocity
import io.github.suqi8.coui.kmp.basic.LocalPullToRefreshState
import io.github.suqi8.coui.kmp.basic.PullToRefreshState
import io.github.suqi8.coui.kmp.basic.RefreshState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.round
import kotlin.math.sign

/**
 * A COUI implementation of [OverscrollFactory] that creates [COUIOverscrollEffect] instances,
 * the COUI 17 scroll-view overscroll shared with [overScrollVertical] and [overScrollHorizontal].
 *
 * Provide this as [androidx.compose.foundation.LocalOverscrollFactory] in
 * [io.github.suqi8.coui.kmp.theme.COUITheme] to automatically apply the COUI overscroll
 * effect to all Compose scrollable components.
 */
object COUIOverscrollFactory : OverscrollFactory {
    override fun createOverscrollEffect(): OverscrollEffect = COUIOverscrollEffect()

    override fun equals(other: Any?): Boolean = this === other

    override fun hashCode(): Int = 1
}

/**
 * A COUI [OverscrollEffect] with the overscroll of the COUI 17 scroll views (`COUIRecyclerView`,
 * `COUINestedScrollView`), shared with [overScrollVertical] and [overScrollHorizontal]:
 *
 * - a drag past the edge moves the content by `0.4 * (1 - |overscroll| / window size)` of the
 *   finger (`COUIPhysicalAnimationUtil.calcRealOverScrollDist`);
 * - a release while overscrolled springs back on the `SpringOverScroller` RK4 spring, tension
 *   multiple 2.15 vertically and 3.2 horizontally;
 * - a fling that runs into the edge carries its leftover speed past it on the same spring, its
 *   overshoot saturating at 37.31% of the window (`calcOverFlingDecelerateDist`);
 * - with [edgeHapticEnabled] (the default, like `couiRecyclerViewEnableVibrator` /
 *   `couiScrollViewEnableVibrator`) both a release while overscrolled and a fling hitting the edge
 *   play the scroll-edge vibration (`performHapticFeedback(307)`).
 *
 * The X and Y axes are handled independently. While a [io.github.suqi8.coui.kmp.basic.PullToRefresh]
 * is active the vertical axis is left to it.
 *
 * @param edgeHapticEnabled Whether to play the scroll-edge vibration.
 */
class COUIOverscrollEffect(
    private val edgeHapticEnabled: Boolean = true,
) : OverscrollEffect {
    private val offsetThreshold = 1f

    // Placement pixel-snaps via round(), so only re-place when the whole-pixel value changes.
    private var lastPlacedOffsetX = 0f
    private var lastPlacedOffsetY = 0f

    private val axisX = COUIOverScrollAxis(HorizontalSpringBackTensionMultiple) { value ->
        val rounded = round(value)
        if (rounded != lastPlacedOffsetX) {
            lastPlacedOffsetX = rounded
            invalidateNodePlacement?.invoke()
        }
    }
    private val axisY = COUIOverScrollAxis(VerticalSpringBackTensionMultiple) { value ->
        val rounded = round(value)
        if (rounded != lastPlacedOffsetY) {
            lastPlacedOffsetY = rounded
            invalidateNodePlacement?.invoke()
        }
    }

    internal val offsetX: Float
        get() = axisX.offset

    internal val offsetY: Float
        get() = axisY.offset

    // Injected by COUIOverscrollEffectNode when attached
    internal var invalidateNodePlacement: (() -> Unit)? = null

    internal var scrollRangeV = 0f
    internal var scrollRangeH = 0f

    private var animationJobX: Job? = null
    private var animationJobY: Job? = null

    // Injected by COUIOverscrollEffectNode when attached
    internal var launchAnimation: ((suspend CoroutineScope.() -> Unit) -> Job)? = null
    internal var getPullToRefreshState: (() -> PullToRefreshState?)? = null
    internal var getOverScrollState: (() -> OverScrollState?)? = null
    internal var performEdgeHaptic: (() -> Unit)? = null

    override val isInProgress: Boolean
        get() = abs(offsetX) > offsetThreshold || abs(offsetY) > offsetThreshold

    override val node: DelegatableNode = COUIOverscrollEffectNode(this)

    private fun shouldBypassForPullToRefreshY(): Boolean {
        val pts = getPullToRefreshState?.invoke() ?: return false
        return pts.refreshState != RefreshState.Idle
    }

    internal fun resetAll() {
        axisX.reset()
        axisY.reset()
        getOverScrollState?.invoke()?.let { state ->
            if (state.isOverScrollActive) state.isOverScrollActive = false
        }
    }

    // Reclaims a stale offset once the child can scroll again towards it (e.g. pagination); otherwise applyToFling swallows the next fling.
    private fun unwindStaleOffset(axis: COUIOverScrollAxis, consumedDelta: Float, range: Float) {
        if (abs(axis.offset) <= offsetThreshold || consumedDelta == 0f) return
        if (sign(consumedDelta) != sign(axis.offset)) return
        axis.dragBack(-consumedDelta, range)
    }

    private fun startSpringBack(axis: COUIOverScrollAxis, velocity: Float, overFling: Boolean, screenSize: Float): Job? {
        if (!overFling && abs(axis.offset) <= offsetThreshold) {
            axis.reset()
            return null
        }
        return launchAnimation?.invoke {
            axis.springBack(velocity = velocity, overFling = overFling, screenSize = screenSize)
            updateOverScrollState()
        }
    }

    private fun updateOverScrollState() {
        val state = getOverScrollState?.invoke() ?: return
        val isActive = isInProgress
        if (state.isOverScrollActive != isActive) {
            state.isOverScrollActive = isActive
        }
    }

    override fun applyToScroll(
        delta: Offset,
        source: NestedScrollSource,
        performScroll: (Offset) -> Offset,
    ): Offset {
        if (source != NestedScrollSource.UserInput) {
            val consumed = performScroll(delta)
            // A running spring settles to zero on its own; only reclaim when it was interrupted.
            if (animationJobY?.isActive != true) unwindStaleOffset(axisY, consumed.y, scrollRangeV)
            if (animationJobX?.isActive != true) unwindStaleOffset(axisX, consumed.x, scrollRangeH)
            updateOverScrollState()
            return consumed
        }

        val bypassY = shouldBypassForPullToRefreshY()

        // A drag takes over a running spring from where it is.
        if (!bypassY) animationJobY?.cancel()
        animationJobX?.cancel()

        // Pre-scroll: a drag back towards the content first undoes the overscroll.
        var performScrollDeltaY = delta.y
        var extraConsumedY = 0f
        if (!bypassY && abs(axisY.offset) > offsetThreshold && delta.y != 0f && sign(delta.y) != sign(axisY.offset)) {
            extraConsumedY = axisY.dragBack(delta.y, scrollRangeV)
            performScrollDeltaY = delta.y - extraConsumedY
        }
        var performScrollDeltaX = delta.x
        var extraConsumedX = 0f
        if (abs(axisX.offset) > offsetThreshold && delta.x != 0f && sign(delta.x) != sign(axisX.offset)) {
            extraConsumedX = axisX.dragBack(delta.x, scrollRangeH)
            performScrollDeltaX = delta.x - extraConsumedX
        }

        // Post-scroll: whatever the content cannot take pulls it past the edge.
        val adjustedDelta = Offset(performScrollDeltaX, performScrollDeltaY)
        val scrollConsumed = performScroll(adjustedDelta)
        val scrollRemaining = adjustedDelta - scrollConsumed

        if (!bypassY || animationJobY?.isActive != true) unwindStaleOffset(axisY, scrollConsumed.y, scrollRangeV)
        unwindStaleOffset(axisX, scrollConsumed.x, scrollRangeH)

        val absorbY = scrollRemaining.y != 0f && !bypassY
        if (absorbY) axisY.drag(scrollRemaining.y, scrollRangeV)
        if (scrollRemaining.x != 0f) axisX.drag(scrollRemaining.x, scrollRangeH)

        updateOverScrollState()

        // Total consumed = overscroll reduction + scroll consumed + overscroll-absorbed remainder
        return Offset(
            x = extraConsumedX + scrollConsumed.x + scrollRemaining.x,
            y = extraConsumedY + scrollConsumed.y + (if (absorbY) scrollRemaining.y else 0f),
        )
    }

    override suspend fun applyToFling(
        velocity: Velocity,
        performFling: suspend (Velocity) -> Velocity,
    ) {
        val isActiveY = abs(offsetY) > offsetThreshold
        val isActiveX = abs(offsetX) > offsetThreshold
        val bypassY = shouldBypassForPullToRefreshY() && !isActiveY

        animationJobY?.cancel()
        animationJobX?.cancel()

        // COUIRecyclerView.dispatchTouchEvent: a release while overscrolled springs back from rest
        // (performSpringBack) and vibrates.
        val releasedOverScrolled = (isActiveY && !bypassY) || isActiveX
        if (releasedOverScrolled && edgeHapticEnabled) performEdgeHaptic?.invoke()

        var performVelocity = velocity
        if (!bypassY && isActiveY) {
            animationJobY = startSpringBack(axisY, velocity = 0f, overFling = false, screenSize = scrollRangeV)
            // A fling back towards the content scrolls it (performOverScrollingReverseFling); one
            // further out is dropped.
            if (sign(velocity.y) == sign(offsetY)) performVelocity = Velocity(performVelocity.x, 0f)
        }
        if (isActiveX) {
            animationJobX = startSpringBack(axisX, velocity = 0f, overFling = false, screenSize = scrollRangeH)
            if (sign(velocity.x) == sign(offsetX)) performVelocity = Velocity(0f, performVelocity.y)
        }

        val consumed = performFling(performVelocity)
        val remaining = performVelocity - consumed

        // COUINestedScrollView.onOverScrolled: a fling reaching the edge carries on past it.
        var hitEdge = false
        if (!bypassY && !isActiveY && abs(remaining.y) > MIN_OVER_FLING_VELOCITY) {
            animationJobY = startSpringBack(axisY, velocity = remaining.y, overFling = true, screenSize = scrollRangeV)
            hitEdge = true
        }
        if (!isActiveX && abs(remaining.x) > MIN_OVER_FLING_VELOCITY) {
            animationJobX = startSpringBack(axisX, velocity = remaining.x, overFling = true, screenSize = scrollRangeH)
            hitEdge = true
        }
        if (hitEdge && !releasedOverScrolled && edgeHapticEnabled) performEdgeHaptic?.invoke()

        updateOverScrollState()
    }

    private companion object {
        /** Leftover fling speeds below this (px/s) stop at the edge. */
        const val MIN_OVER_FLING_VELOCITY = 1f
    }
}

private class COUIOverscrollEffectNode(
    val effect: COUIOverscrollEffect,
) : Modifier.Node(),
    CompositionLocalConsumerModifierNode,
    LayoutModifierNode {

    override fun onAttach() {
        super.onAttach()
        updateScrollRange()
        effect.launchAnimation = { block -> coroutineScope.launch(block = block) }
        effect.getPullToRefreshState = { currentValueOf(LocalPullToRefreshState) }
        effect.getOverScrollState = { currentValueOf(LocalOverScrollState) }
        effect.invalidateNodePlacement = { invalidatePlacement() }
        effect.performEdgeHaptic = { if (isAttached) performScrollEdgeHaptic() }
    }

    override fun onDetach() {
        super.onDetach()
        effect.launchAnimation = null
        effect.getPullToRefreshState = null
        effect.getOverScrollState = null
        effect.invalidateNodePlacement = null
        effect.performEdgeHaptic = null
        effect.resetAll()
    }

    private var cachedDensity: Density? = null
    private var cachedWindowInfo: WindowInfo? = null

    private fun updateScrollRange() {
        val density = currentValueOf(LocalDensity)
        val windowInfo = currentValueOf(LocalWindowInfo)
        if (density == cachedDensity && windowInfo == cachedWindowInfo) return
        cachedDensity = density
        cachedWindowInfo = windowInfo
        with(density) {
            effect.scrollRangeV = windowInfo.containerDpSize.height.toPx()
            effect.scrollRangeH = windowInfo.containerDpSize.width.toPx()
        }
    }

    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        updateScrollRange()
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            placeable.placeWithLayer(0, 0) {
                translationX = round(effect.offsetX)
                translationY = round(effect.offsetY)
                clip = true
            }
        }
    }
}
