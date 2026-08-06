// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot

/**
 * Creates a [ToolbarSearchState] that survives configuration changes.
 *
 * @param initiallyExpanded whether the search bar starts out expanded.
 */
@Composable
fun rememberToolbarSearchState(initiallyExpanded: Boolean = false): ToolbarSearchState = rememberSaveable(saver = ToolbarSearchState.Saver) { ToolbarSearchState(initiallyExpanded) }

/**
 * The state driving a search bar that floats up into the toolbar when it expands.
 *
 * COUI models this with `COUISearchBar.setAtFrontToolBar`: the bar keeps its place in the list
 * until it expands, then travels up to where the toolbar sits while the toolbar's own children
 * fade away underneath it. The travel distance is not a design token — `COUISearchBar` measures
 * it at runtime as `mStartY - mExtraY`, the bar's own top minus a caller-supplied inset — so this
 * state measures the same two positions instead of hardcoding a distance.
 *
 * @param initiallyExpanded whether the search bar starts out expanded.
 */
@Stable
class ToolbarSearchState(initiallyExpanded: Boolean = false) {
    /** Whether the search bar is expanded and floated up into the toolbar. */
    var expanded: Boolean by mutableStateOf(initiallyExpanded)
        internal set

    /** The search bar's resting top, in root coordinates. COUI `mStartY`. */
    private var restingTopPx by mutableFloatStateOf(Float.NaN)

    /** The search bar's own height, used to align it against the anchor's bottom edge. */
    private var barHeightPx by mutableFloatStateOf(Float.NaN)

    /**
     * The anchor's bottom, in root coordinates. Aligning against the bottom rather than the top
     * keeps the landing spot correct whatever status-bar or display-cutout inset the toolbar
     * carries above its content.
     */
    private var anchorBottomPx by mutableFloatStateOf(Float.NaN)

    /**
     * How far the bar must travel to reach the toolbar, in pixels. Zero until every input has been
     * measured, so the first frame never jumps.
     */
    internal val floatDistancePx: Float
        get() = if (restingTopPx.isNaN() || anchorBottomPx.isNaN() || barHeightPx.isNaN()) {
            0f
        } else {
            (restingTopPx - (anchorBottomPx - barHeightPx)).coerceAtLeast(0f)
        }

    internal fun onBarPositioned(topPx: Float, heightPx: Float) {
        // Only the collapsed geometry is a valid reference. Once expanded the bar reports where
        // the animation has moved it, and its height grows to include the results it now hosts —
        // feeding either back in would corrupt the distance being animated over.
        if (!expanded) {
            restingTopPx = topPx
            barHeightPx = heightPx
        }
    }

    internal fun onAnchorPositioned(bottomPx: Float) {
        anchorBottomPx = bottomPx
    }

    /** Expands the search bar, floating it up into the toolbar. */
    fun expand() {
        expanded = true
    }

    /** Collapses the search bar, returning it to its place in the content. */
    fun collapse() {
        expanded = false
    }

    companion object {
        /** The default [Saver] for [ToolbarSearchState]. */
        val Saver: Saver<ToolbarSearchState, Boolean> = Saver(
            save = { it.expanded },
            restore = { ToolbarSearchState(it) },
        )
    }
}

/**
 * Hosts the toolbar content that a [ToolbarSearchState]-driven search bar floats over.
 *
 * Place this where the toolbar belongs and pass the same [state] to [ToolbarSearchAnchor] and
 * [ToolbarFloatingSearchBar]. On expand the toolbar's children fade out over 150ms, matching
 * `COUISearchBar.showInToolBar`, which drives `setToolBarAlpha` from 1 to 0 over that duration.
 * COUI does not translate the toolbar content, so neither does this.
 *
 * @param state the [ToolbarSearchState] shared with the floating search bar.
 * @param modifier the [Modifier] to be applied to the anchor.
 * @param content the toolbar content that fades out while the search bar is expanded.
 */
@Composable
fun ToolbarSearchAnchor(
    state: ToolbarSearchState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val alpha by animateFloatAsState(
        targetValue = if (state.expanded) 0f else 1f,
        // COUISearchBar.showInToolBar: a bare 150ms ValueAnimator with no interpolator set, so it
        // runs on the platform default rather than MoveEase.
        animationSpec = tween(durationMillis = ToolbarContentFadeDurationMillis),
        label = "ToolbarSearchAnchorAlpha",
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned {
                state.onAnchorPositioned(it.positionInRoot().y + it.size.height)
            },
    ) {
        Box(modifier = Modifier.graphicsLayer { this.alpha = alpha }) {
            content()
        }
    }
}

/**
 * A search bar that floats up into a [ToolbarSearchAnchor] when it expands.
 *
 * The bar occupies its natural place in the content while collapsed. On expand it translates up by
 * the measured distance to the anchor over 450ms on the COUIMoveEaseInterpolator curve, matching
 * `COUISearchBar`'s offset animator. The translation is a draw-time offset, so the content below
 * keeps its layout and does not reflow as the bar travels.
 *
 * @param state the [ToolbarSearchState] shared with the [ToolbarSearchAnchor].
 * @param modifier the [Modifier] to be applied to the search bar.
 * @param content the search bar itself, typically a [SearchBar].
 */
@Composable
fun ToolbarFloatingSearchBar(
    state: ToolbarSearchState,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val distance = state.floatDistancePx
    val progress by animateFloatAsState(
        targetValue = if (state.expanded) 1f else 0f,
        // COUISearchBar offset animator: 450ms on COUIMoveEaseInterpolator.
        animationSpec = tween(durationMillis = FloatDurationMillis, easing = MoveEasing),
        label = "ToolbarFloatingSearchBarProgress",
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                // Report the resting top, undoing the translation currently applied.
                state.onBarPositioned(
                    topPx = coordinates.positionInRoot().y + distance * progress,
                    heightPx = coordinates.size.height.toFloat(),
                )
            }
            .graphicsLayer { translationY = -distance * progress },
        content = content,
    )
}

/** COUIMoveEaseInterpolator: PathInterpolator(0.3f, 0f, 0.1f, 1f). */
private val MoveEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

/** COUISearchBar DEFAULT_SEARCH_VIEW_OFFSET_CHANGE_DURATION (450ms). */
private val FloatDurationMillis = 450

/** COUISearchBar.showInToolBar toolbar alpha animator (150ms). */
private val ToolbarContentFadeDurationMillis = 150
