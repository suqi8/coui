// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.Sidebar
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * The open state of a [SidePaneLayout], animated like `COUISidePaneLayout`: 483ms on the COUI
 * move-ease curve (`COUIMoveEaseInterpolator`, cubic-bezier 0.3, 0, 0.1, 1).
 *
 * @param initialOpen Whether the pane starts open (`setDefaultShowPane`, true by default).
 */
@Stable
class SidePaneState(initialOpen: Boolean) {

    private val animatable = Animatable(if (initialOpen) 1f else 0f)

    /** Whether the pane is open or opening (`COUISidePaneLayout.isOpen()`). */
    var isOpen: Boolean by mutableStateOf(initialOpen)
        private set

    /** How far the pane is open, from 0 (closed) to 1 (open): the slide offset. */
    val progress: Float get() = animatable.value

    /** Whether the pane is moving. */
    val isAnimating: Boolean get() = animatable.isRunning

    /** Opens the pane with the COUI animation (`COUISidePaneLayout.openPane()`). */
    suspend fun open() = animateTo(true)

    /** Closes the pane with the COUI animation (`COUISidePaneLayout.closePane()`). */
    suspend fun close() = animateTo(false)

    /** Opens a closed pane and closes an open one, as the toggle icon does. */
    suspend fun toggle() = animateTo(!isOpen)

    /** Opens or closes the pane without animating. */
    suspend fun snapTo(open: Boolean) {
        isOpen = open
        animatable.snapTo(if (open) 1f else 0f)
    }

    private suspend fun animateTo(open: Boolean) {
        isOpen = open
        val target = if (open) 1f else 0f
        // The animators restart from the current slide offset (setCurrentFraction), so an
        // interrupted move only takes the remaining share of the duration.
        val duration = (SidePaneLayoutDefaults.ANIMATION_DURATION_MILLIS * abs(target - animatable.value)).roundToInt()
        animatable.animateTo(target, tween(duration, easing = SidePaneLayoutDefaults.AnimationEasing))
    }

    companion object {
        /** Saves whether the pane is open. */
        val Saver: Saver<SidePaneState, Boolean> = Saver(save = { it.isOpen }, restore = { SidePaneState(it) })
    }
}

/**
 * Creates and remembers a [SidePaneState].
 *
 * @param initialOpen Whether the pane starts open.
 */
@Composable
fun rememberSidePaneState(initialOpen: Boolean = true): SidePaneState = rememberSaveable(saver = SidePaneState.Saver) { SidePaneState(initialOpen) }

/**
 * A side pane + content split for tablets, foldables and desktop windows, ported from
 * `COUISidePaneLayout`.
 *
 * The [pane] is [paneWidth] wide on the start side and slides out of view as it closes. In split
 * mode (`COUISidePaneUtils.isSplitMode`: at least 600dp wide in a window at least 480dp tall) the
 * [content] fills the rest and grows as the pane closes. Narrower, the content keeps the full
 * width and is pushed aside by the pane; with [coverStyle] the pane covers the content instead,
 * under a mask that fades in with the pane, and touching the content closes the pane.
 *
 * A 48dp toggle icon (`coui_sliding_icon_layout`) sits 45dp from the top and 12dp from the start
 * edge and opens or closes the pane.
 *
 * @param pane The side pane.
 * @param modifier The modifier to be applied to the layout.
 * @param state The open state.
 * @param coverStyle `setCoverStyle`: the pane covers the content rather than resizing it.
 * @param alwaysShowMask `setAlwaysShowMask`: draw the mask over the content even without
 *   [coverStyle], and swallow touches on the content while the pane is open.
 * @param touchContentEnabled `setTouchContentEnable`: in [coverStyle], touching the content
 *   closes the pane.
 * @param showToggleIcon `setCreateIcon`: whether the toggle icon is shown.
 * @param paneWidth `firstPaneWidth` / `expandPaneWidth` (`coui_sliding_pane_width`).
 * @param maskColor The mask color at full opening (`coui_color_mask`).
 * @param toggleIconColor The toggle icon tint (`couiColorLabelPrimary`).
 * @param content The main content.
 */
@Composable
fun SidePaneLayout(
    pane: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    state: SidePaneState = rememberSidePaneState(),
    coverStyle: Boolean = false,
    alwaysShowMask: Boolean = false,
    touchContentEnabled: Boolean = true,
    showToggleIcon: Boolean = true,
    paneWidth: Dp = SidePaneLayoutDefaults.PaneWidth,
    maskColor: Color = SidePaneLayoutDefaults.maskColor,
    toggleIconColor: Color = COUITheme.colorScheme.onBackground,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val windowHeight = with(density) { LocalWindowInfo.current.containerSize.height.toDp() }
    val maskActive = state.isOpen && (alwaysShowMask || (coverStyle && touchContentEnabled))
    val maskModifier = if (maskActive) {
        Modifier.pointerInput(alwaysShowMask, state) {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                // onInterceptTouchEvent: the mask swallows the touch; a cover pane also closes.
                down.consume()
                if (!alwaysShowMask) scope.launch { state.close() }
            }
        }
    } else {
        Modifier
    }
    val drawMask = coverStyle || alwaysShowMask

    Layout(
        contents = listOf(
            content,
            {
                // drawChild / onInterceptTouchEvent: the mask covers what lies beyond the pane's
                // end edge (the layout places it there), so touches on the pane never reach it.
                Box(
                    Modifier
                        .then(maskModifier)
                        .drawBehind {
                            val fraction = state.progress
                            if (drawMask && fraction > 0f) {
                                drawRect(maskColor.copy(alpha = maskColor.alpha * fraction))
                            }
                        },
                )
            },
            pane,
            {
                if (showToggleIcon) {
                    IconButton(
                        onClick = { scope.launch { state.toggle() } },
                        material = false,
                        cornerRadius = SidePaneLayoutDefaults.ToggleIconSize / 2,
                        minWidth = SidePaneLayoutDefaults.ToggleIconSize,
                        minHeight = SidePaneLayoutDefaults.ToggleIconSize,
                    ) {
                        Icon(
                            imageVector = COUIIcons.Basic.Sidebar,
                            contentDescription = "Shrink and expand button",
                            modifier = Modifier.size(SidePaneLayoutDefaults.ToggleIconGlyphSize),
                            tint = toggleIconColor,
                        )
                    }
                }
            },
        ),
        modifier = modifier.clipToBounds(),
    ) { (contentMeasurables, maskMeasurables, paneMeasurables, iconMeasurables), constraints ->
        val width = if (constraints.hasBoundedWidth) constraints.maxWidth else constraints.minWidth
        val fraction = state.progress
        val paneWidthPx = paneWidth.roundToPx().coerceAtMost(width)
        val split = SidePaneLayoutDefaults.isSplitMode(width.toDp(), windowHeight)
        val childHeight = { measured: Int -> if (constraints.hasBoundedHeight) constraints.maxHeight else measured }
        val heightConstraints = if (constraints.hasBoundedHeight) {
            Constraints(minHeight = constraints.maxHeight, maxHeight = constraints.maxHeight)
        } else {
            Constraints()
        }

        val paneOffset = (paneWidthPx * fraction).roundToInt()
        val contentWidth = if (coverStyle || !split) width else width - paneOffset
        val contentX = if (coverStyle) 0 else paneOffset
        val contentPlaceables = contentMeasurables.map {
            it.measure(heightConstraints.copy(minWidth = contentWidth, maxWidth = contentWidth))
        }
        val panePlaceables = paneMeasurables.map {
            it.measure(heightConstraints.copy(minWidth = paneWidthPx, maxWidth = paneWidthPx))
        }
        val height = childHeight(
            maxOf(contentPlaceables.maxOfOrNull { it.height } ?: 0, panePlaceables.maxOfOrNull { it.height } ?: 0),
        )
        val maskPlaceables = maskMeasurables.map { it.measure(Constraints.fixed(width - paneOffset, height)) }
        val iconPlaceables = iconMeasurables.map { it.measure(Constraints()) }

        layout(width, height) {
            contentPlaceables.forEach { it.placeRelative(contentX, 0) }
            maskPlaceables.forEach { it.placeRelative(paneOffset, 0) }
            // The pane slides out by its width as it closes (translationX = -width * (1 - offset)).
            panePlaceables.forEach { it.placeRelative(paneOffset - paneWidthPx, 0) }
            iconPlaceables.forEach {
                it.placeRelative(
                    SidePaneLayoutDefaults.ToggleIconMarginStart.roundToPx(),
                    SidePaneLayoutDefaults.ToggleIconMarginTop.roundToPx(),
                )
            }
        }
    }
}

/** Default values of [SidePaneLayout]. */
object SidePaneLayoutDefaults {

    /** `COUISidePaneLayout`'s pane animators: 483ms. */
    const val ANIMATION_DURATION_MILLIS: Int = 483

    /** `COUIMoveEaseInterpolator`: cubic-bezier(0.3, 0, 0.1, 1). */
    val AnimationEasing: CubicBezierEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

    /** The full-length pane animation. */
    val AnimationSpec: AnimationSpec<Float> = tween(ANIMATION_DURATION_MILLIS, easing = AnimationEasing)

    /** `coui_sliding_pane_width`. */
    val PaneWidth: Dp = 224.dp

    /** `coui_side_pane_layout_icon_size`. */
    val ToggleIconSize: Dp = 48.dp

    /** The icon inside the toggle button: the 48dp button minus `coui_side_pane_layout_icon_padding` (12dp) per side. */
    val ToggleIconGlyphSize: Dp = 24.dp

    /** `coui_side_pane_layout_icon_margin_top`. */
    val ToggleIconMarginTop: Dp = 45.dp

    /** `coui_side_pane_layout_icon_margin_start`. */
    val ToggleIconMarginStart: Dp = 12.dp

    /** `coui_color_mask` (#33000000 light, #99000000 dark): the window dimming color. */
    val maskColor: Color
        @Composable get() = COUITheme.colorScheme.windowDimming

    /**
     * `COUISidePaneUtils.isSplitMode`: the pane and the content sit side by side when the layout
     * is at least 600dp wide and the window at least 480dp tall.
     */
    fun isSplitMode(width: Dp, windowHeight: Dp): Boolean = width >= 600.dp && windowHeight >= 480.dp
}
