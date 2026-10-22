// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import io.github.suqi8.coui.kmp.effect.TouchMotion
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Creates a [ToolbarSearchState] that survives configuration changes.
 *
 * @param initiallyExpanded whether the search bar starts out expanded.
 */
@Composable
fun rememberToolbarSearchState(initiallyExpanded: Boolean = false): ToolbarSearchState = rememberSaveable(saver = ToolbarSearchState.Saver) { ToolbarSearchState(initiallyExpanded) }

/**
 * The state of a search bar that lives in the toolbar and expands out of the toolbar's search icon,
 * after COUI 17 `COUISearchBar.showInToolBar` / `hideInToolBar`.
 *
 * On expand the bar starts as a circle centred on the search icon, slides home while widening into
 * the full capsule, and the cancel button flies out of the icon, the two merging like metaballs;
 * the toolbar's own children (not its background) fade and shrink away underneath. Collapsing
 * plays it back. Two springs
 * drive it, both from `IconExpandToEditParamsHigh` / `IconCollapseToNormalParamsHigh`:
 * - the icon expansion (`mIconExpandAnimation`): response 0.55 / bounce 0.2 out, 0.5 / 0.2 back;
 * - the toolbar children (`mToolbarIconSpringAnimation`): response 0.4 / bounce 0.3 out, scaling to
 *   0.8, and 0.5 / 0.2 back, growing from 0.5.
 *
 * @param initiallyExpanded whether the search bar starts out expanded.
 */
@Stable
class ToolbarSearchState(initiallyExpanded: Boolean = false) {
    /** Whether the search bar is expanded in the toolbar. */
    var expanded: Boolean by mutableStateOf(initiallyExpanded)
        internal set

    /** `mIconExpandCenterAnimationRange / 1000`. */
    internal val iconExpandProgress = Animatable(if (initiallyExpanded) 1f else 0f)

    /** `mToolbarIconSpringValue / 1000`: 0 shows the toolbar children, 1 hides them. */
    internal val toolbarProgress = Animatable(if (initiallyExpanded) 1f else 0f)

    /** `mToolbarIconScaleEnd`: the scale the toolbar children shrink to (out) or grow from (back). */
    internal var toolbarScaleEnd by mutableFloatStateOf(ExpandToolbarEndScale)

    /** The toolbar search icon's bounds in root pixels, [Rect.Zero] until measured. */
    internal var entryBoundsInRoot by mutableStateOf(Rect.Zero)

    /** The toolbar search icon's horizontal centre in root pixels, `NaN` until measured. */
    internal val entryCenterXPx: Float
        get() = if (entryBoundsInRoot.isEmpty) Float.NaN else entryBoundsInRoot.center.x

    /**
     * Whether the bar is on screen: expanded, or still folding back into the icon. COUI keeps the
     * bar `GONE` otherwise.
     */
    internal val barVisible: Boolean
        get() = expanded || iconExpandProgress.value != 0f

    /** `setToolBarAlpha(1 - q)`. */
    internal fun toolbarAlpha(): Float = (1f - toolbarProgress.value).coerceIn(0f, 1f)

    /**
     * The alpha for the toolbar's title while the search bar is out, `COUISearchBar.setToolBarAlpha`
     * applied to the title view. Pass it to the bar's title colour (for example
     * `titleColor = color.copy(alpha = color.alpha * state.toolbarTitleAlpha)`); a large title is
     * not part of the toolbar row and stays as it is.
     */
    val toolbarTitleAlpha: Float
        get() = toolbarAlpha()

    /** `setToolBarScale(1 - q)`: `end + (1 - end) * (1 - q)`. */
    internal fun toolbarScale(): Float {
        val end = toolbarScaleEnd
        return end + (1f - end) * (1f - toolbarProgress.value)
    }

    /** Expands the search bar out of the toolbar's search icon. */
    fun expand() {
        expanded = true
    }

    /** Collapses the search bar back into the toolbar's search icon. */
    fun collapse() {
        expanded = false
    }

    /**
     * `COUISearchBar.onToolbarSearchIconDownWhileInvisible`: a touch on the (still hidden) search icon
     * in the last 5% of a collapse completes the collapse at once, so the icon is back to take it.
     */
    internal suspend fun onEntryDownWhileInvisible() {
        if (expanded || !iconExpandProgress.isRunning || iconExpandProgress.value > InterruptCollapseProgress) return
        // mIconExpandAnimation.cancelComplete(), whose end listener cancels the toolbar icon spring.
        iconExpandProgress.snapTo(0f)
        toolbarProgress.stop()
    }

    internal suspend fun animateTo(expanded: Boolean) = coroutineScope {
        if (expanded) {
            // setExpandData + configureToolbarIconSpringForExpand.
            toolbarScaleEnd = ExpandToolbarEndScale
            launch { iconExpandProgress.animateTo(1f, ExpandSpring) }
            launch { toolbarProgress.animateTo(1f, ExpandToolbarSpring) }
        } else {
            // setCollapseData + configureToolbarIconSpringForCollapse.
            toolbarScaleEnd = CollapseToolbarInitScale
            launch { iconExpandProgress.animateTo(0f, CollapseSpring) }
            launch { toolbarProgress.animateTo(0f, CollapseToolbarSpring) }
        }
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
 * Hosts the toolbar content the search bar expands over.
 *
 * COUI fades and shrinks the toolbar's children, never the toolbar itself, so the content is drawn
 * as it is: apply [toolbarSearchItem] to the navigation and menu icons, [toolbarSearchEntry] to the
 * search icon, and [ToolbarSearchState.toolbarTitleAlpha] to the title colour.
 *
 * @param state the [ToolbarSearchState] shared with the [ToolbarFloatingSearchBar].
 * @param modifier the [Modifier] to be applied to the anchor.
 * @param content the toolbar content.
 */
@Composable
fun ToolbarSearchAnchor(
    state: ToolbarSearchState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        content()
    }
}

/**
 * Marks the toolbar's search icon, the [ToolbarFloatingSearchBar] grows out of. It reports the
 * icon's bounds, hides the icon while the bar is out (`setToolbarSearchIconAlpha(0)`, restored once
 * the bar has folded back), shrinks with the other toolbar icons, and a touch on it in the last 5%
 * of a collapse completes the collapse at once (`onToolbarSearchIconDownWhileInvisible`); the click
 * that follows expands the bar again, as the icon's own action does.
 *
 * @param state the [ToolbarSearchState] driving the search bar.
 */
fun Modifier.toolbarSearchEntry(state: ToolbarSearchState): Modifier = this
    .onGloballyPositioned { state.entryBoundsInRoot = it.boundsInRoot() }
    .pointerInput(state) {
        coroutineScope {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                launch { state.onEntryDownWhileInvisible() }
            }
        }
    }
    .graphicsLayer {
        val scale = state.toolbarScale()
        scaleX = scale
        scaleY = scale
        alpha = if (state.barVisible) 0f else state.toolbarAlpha()
    }

/**
 * Fades and shrinks a toolbar icon (navigation or menu) while the search bar is out, after
 * `COUISearchBar.setToolBarAlpha` and `applyMenuViewScale` / `applyToolbarIconExternalScale`.
 *
 * @param state the [ToolbarSearchState] driving the search bar.
 */
fun Modifier.toolbarSearchItem(state: ToolbarSearchState): Modifier = this.graphicsLayer {
    val scale = state.toolbarScale()
    scaleX = scale
    scaleY = scale
    alpha = state.toolbarAlpha()
}

/**
 * The search bar that expands out of the toolbar's search icon.
 *
 * Place it over the toolbar row, in the same position COUI adds `COUISearchBar` to the toolbar
 * (full width, below the status bar), and mark the search icon with [toolbarSearchEntry]. It
 * shows nothing while the bar is folded away. Inside, a [SearchBar] with an [InputField] and a
 * [CancelButton] runs the COUI 17 icon expansion: the capsule starts as a circle on the icon,
 * slides home and widens while its text fades in and it blurs most half way, and the cancel button
 * flies out of the icon from 0.6 scale, merging with the capsule like a metaball.
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
    LaunchedEffect(state, state.expanded) {
        state.animateTo(state.expanded)
    }
    val iconExpand = remember(state) {
        SearchBarIconExpand(
            progress = { state.iconExpandProgress.value },
            sourceCenterX = { state.entryCenterXPx },
            interactive = { state.expanded },
        )
    }
    var barOrigin by remember { mutableStateOf(Offset.Zero) }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned { barOrigin = it.positionInRoot() }
            // COUI drops the collapsing bar behind the toolbar children (updateLayerInToolBar(false)),
            // so the bar ignores touches and the (hidden) search icon takes a touch at its spot: the
            // down may complete a late collapse (onToolbarSearchIconDownWhileInvisible) and the
            // click that follows runs the icon's action, showInToolBar, which expands the bar again.
            // Here the bar stays on top, so it swallows the gesture and plays the icon's part.
            .pointerInput(state) {
                coroutineScope {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                        if (state.expanded) return@awaitEachGesture
                        down.consume()
                        val onEntry = state.entryBoundsInRoot.contains(barOrigin + down.position)
                        if (onEntry) launch { state.onEntryDownWhileInvisible() }
                        var clicked = false
                        while (true) {
                            val change = awaitPointerEvent(PointerEventPass.Initial).changes.firstOrNull { it.id == down.id } ?: break
                            change.consume()
                            if (!change.pressed) {
                                clicked = onEntry && state.entryBoundsInRoot.contains(barOrigin + change.position)
                                break
                            }
                        }
                        if (clicked) state.expand()
                    }
                }
            },
    ) {
        if (state.barVisible) {
            CompositionLocalProvider(LocalSearchBarIconExpand provides iconExpand) {
                content()
            }
        }
    }
}

/** `IconExpandToEditParamsHigh` bounce 0.2 / response 0.55. */
private val ExpandSpring = TouchMotion.spring(response = 0.55f, bounce = 0.2f, visibilityThreshold = 0.001f)

/** `IconCollapseToNormalParamsHigh` bounce 0.2 / response 0.5. */
private val CollapseSpring = TouchMotion.spring(response = 0.5f, bounce = 0.2f, visibilityThreshold = 0.001f)

/** `IconExpandToEditParamsHigh` toolbar icon bounce 0.3 / response 0.4. */
private val ExpandToolbarSpring = TouchMotion.spring(response = 0.4f, bounce = 0.3f, visibilityThreshold = 0.001f)

/** `IconCollapseToNormalParamsHigh` toolbar icon bounce 0.2 / response 0.5. */
private val CollapseToolbarSpring = TouchMotion.spring(response = 0.5f, bounce = 0.2f, visibilityThreshold = 0.001f)

/** `IconExpandToEditParamsHigh.toolbarEndScale`. */
private val ExpandToolbarEndScale = 0.8f

/** `onToolbarSearchIconDownWhileInvisible`: the collapse progress below which a touch completes it. */
private val InterruptCollapseProgress = 0.05f

/** `IconCollapseToNormalParamsHigh.toolbarInitScale`. */
private val CollapseToolbarInitScale = 0.5f
