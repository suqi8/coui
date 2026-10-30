// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.utils.COUILayoutGrid
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * How a [SideNavigationBar] shows its drawer (`COUISideNavigationBar.getDrawerMode()`).
 */
enum class SideNavigationBarMode {
    /** Mode 0: the drawer slides over the content under a mask, like a modal drawer. */
    Overlay,

    /** Mode 1: the drawer sits beside the content, which makes room for it, without a mask. */
    Docked,
}

/**
 * The open state of a [SideNavigationBar].
 *
 * The drawer moves like a `DrawerLayout` settle without fling velocity: 512ms
 * (`ViewDragHelper.computeSettleDuration`, twice the 256ms base for a full-width move) on the
 * quintic ease-out `ViewDragHelper` interpolator.
 *
 * @param initialOpen Whether the drawer starts open.
 */
@Stable
class SideNavigationBarState(initialOpen: Boolean = false) {

    private val animatable = Animatable(if (initialOpen) 1f else 0f)

    /** Whether the drawer is open or opening. */
    var isOpen: Boolean by mutableStateOf(initialOpen)
        private set

    /** The current drawer mode, resolved from the bar's size by the last layout. */
    var mode: SideNavigationBarMode by mutableStateOf(SideNavigationBarMode.Overlay)
        internal set

    /** How far the drawer is open, from 0 to 1 (`onDrawerSlide` offset). */
    val progress: Float get() = animatable.value

    /**
     * The open state the user chose while docked (`mImplicitDrawerState`): -1 unknown, 0 closed,
     * 1 open. Docked windows restore it after a size change.
     */
    internal var implicitDockedState: Int = -1

    private var modeInitialized = false

    /** Opens the drawer (`openDrawer`). */
    suspend fun open() = animateTo(true)

    /** Closes the drawer (`closeDrawer`). */
    suspend fun close() = animateTo(false)

    /** Opens a closed drawer and closes an open one. */
    suspend fun toggle() = animateTo(!isOpen)

    /** Opens or closes the drawer without animating. */
    suspend fun snapTo(open: Boolean) {
        updateOpen(open)
        animatable.snapTo(if (open) 1f else 0f)
    }

    private fun updateOpen(open: Boolean) {
        isOpen = open
        if (mode == SideNavigationBarMode.Docked) implicitDockedState = if (open) 1 else 0
    }

    private suspend fun animateTo(open: Boolean) {
        updateOpen(open)
        val target = if (open) 1f else 0f
        val distance = abs(target - animatable.value)
        if (distance == 0f) return
        // computeAxisDuration: (distance / range + 1) * 256ms, at most 600ms.
        val duration = min(((distance + 1f) * SETTLE_BASE_DURATION_MILLIS).roundToInt(), SETTLE_MAX_DURATION_MILLIS)
        animatable.animateTo(target, tween(duration, easing = SideNavigationBarDefaults.SettleEasing))
    }

    internal suspend fun applyMode(newMode: SideNavigationBarMode, editMode: Boolean) {
        // The first layout only adopts the mode (mFirstLayout skips the open / close rules).
        if (modeInitialized && mode != newMode) {
            // COUISideNavigationBar.updateResponsiveUI on a window size class change.
            if (newMode == SideNavigationBarMode.Overlay) {
                if (!editMode && implicitDockedState != 1 && isOpen) {
                    isOpen = false
                    animatable.snapTo(0f)
                }
            } else if (editMode) {
                implicitDockedState = if (isOpen) 1 else 0
            } else if (implicitDockedState == 0 && isOpen) {
                isOpen = false
                animatable.snapTo(0f)
            } else if (implicitDockedState == 1 && !isOpen) {
                isOpen = true
                animatable.snapTo(1f)
            }
        }
        mode = newMode
        modeInitialized = true
        // setDrawerMode: entering the docked mode the first time adopts the current state.
        if (newMode == SideNavigationBarMode.Docked && implicitDockedState == -1) {
            implicitDockedState = if (isOpen) 1 else 0
        }
    }

    companion object {
        private const val SETTLE_BASE_DURATION_MILLIS = 256
        private const val SETTLE_MAX_DURATION_MILLIS = 600

        /** Saves whether the drawer is open. */
        val Saver: Saver<SideNavigationBarState, Boolean> = Saver(
            save = { it.isOpen },
            restore = { SideNavigationBarState(it) },
        )
    }
}

/**
 * Creates and remembers a [SideNavigationBarState].
 *
 * @param initialOpen Whether the drawer starts open.
 */
@Composable
fun rememberSideNavigationBarState(initialOpen: Boolean = false): SideNavigationBarState = rememberSaveable(saver = SideNavigationBarState.Saver) { SideNavigationBarState(initialOpen) }

/**
 * A responsive side navigation drawer ported from `COUISideNavigationBar` (a `DrawerLayout`).
 *
 * The drawer mode follows the bar's size (`COUIResponsiveUtils` screen tiers: small = 1,
 * medium = 2, large = 3) against the content weight (1, or 2 with [parentChildHierarchy]):
 * when the window outweighs the content the drawer is [SideNavigationBarMode.Docked], otherwise
 * [SideNavigationBarMode.Overlay]. Docked, the drawer is `min(280dp, 38.2% of the width)` wide
 * and the content shrinks beside it; as an overlay it is 300dp wide, slides over the content
 * under the `coui_color_mask` scrim, and a tap on the scrim closes it. A 0.33dp divider follows
 * the drawer's end edge in both modes. The drawer cannot be dragged, as COUI locks the
 * `DrawerLayout` swipe.
 *
 * When the window class changes, an overlay drawer the user did not open while docked is
 * closed, and a docked drawer restores the open state the user last chose while docked.
 *
 * @param drawerContent The drawer content.
 * @param modifier The modifier to be applied to the bar.
 * @param state The drawer state.
 * @param parentChildHierarchy `isParentChildHierarchy`: the content is itself a list-detail
 *   split, so the drawer only docks on large windows.
 * @param editMode `setIsInEditState`: in docked mode the content is masked and does not take touches.
 * @param editModeMask `setHandlerEditModeMask`: whether [editMode] draws the mask.
 * @param drawerColor The drawer background.
 * @param maskColor The scrim / edit mask color (`coui_color_mask`).
 * @param dividerColor The drawer divider color (`couiColorDivider`).
 * @param content The main content.
 */
@Composable
fun SideNavigationBar(
    drawerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    state: SideNavigationBarState = rememberSideNavigationBarState(),
    parentChildHierarchy: Boolean = false,
    editMode: Boolean = false,
    editModeMask: Boolean = true,
    drawerColor: Color = COUITheme.colorScheme.surface,
    maskColor: Color = SideNavigationBarDefaults.maskColor,
    dividerColor: Color = DividerDefaults.DividerColor,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val windowSize = LocalWindowInfo.current.containerSize
    val currentEditMode by rememberUpdatedState(editMode)
    var measuredMode by remember { mutableStateOf<SideNavigationBarMode?>(null) }
    LaunchedEffect(measuredMode, state) {
        val mode = measuredMode ?: return@LaunchedEffect
        state.applyMode(mode, currentEditMode)
    }
    val docked = state.mode == SideNavigationBarMode.Docked
    // setIsInEditState animates the scrim colour in docked mode (default ValueAnimator: 300ms).
    val editMaskFraction by animateFloatAsState(
        targetValue = if (editMode && editModeMask && docked) 1f else 0f,
        animationSpec = tween(SideNavigationBarDefaults.EDIT_MASK_DURATION_MILLIS, easing = FastOutSlowInEasing),
        label = "SideNavigationBarEditMask",
    )
    val scrimActive = if (docked) editMode else state.isOpen
    val scrimModifier = if (scrimActive) {
        Modifier.pointerInput(docked, editMode, state) {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                down.consume()
                // onTouchEvent: a tap outside an overlay drawer closes it, unless editing.
                if (!docked && !editMode) scope.launch { state.close() }
            }
        }
    } else {
        Modifier
    }

    Layout(
        contents = listOf(
            content,
            {
                Box(
                    Modifier
                        .then(scrimModifier)
                        .drawBehind {
                            val alpha = if (state.mode == SideNavigationBarMode.Docked) editMaskFraction else state.progress
                            if (alpha > 0f) drawRect(maskColor.copy(alpha = maskColor.alpha * alpha))
                        },
                )
            },
            { Box(Modifier.background(drawerColor)) { drawerContent() } },
            { Box(Modifier.background(dividerColor)) },
        ),
        modifier = modifier
            .clipToBounds()
            .onSizeChanged { size ->
                measuredMode = with(density) {
                    SideNavigationBarDefaults.drawerMode(size.width.toDp(), size.height.toDp(), parentChildHierarchy)
                }
            },
    ) { (contentMeasurables, scrimMeasurables, drawerMeasurables, dividerMeasurables), constraints ->
        val width = if (constraints.hasBoundedWidth) constraints.maxWidth else constraints.minWidth
        val height = if (constraints.hasBoundedHeight) constraints.maxHeight else windowSize.height
        val widthDp = width.toDp()
        val heightDp = height.toDp()
        val mode = SideNavigationBarDefaults.drawerMode(widthDp, heightDp, parentChildHierarchy)
        val drawerWidth = SideNavigationBarDefaults.drawerWidth(widthDp, heightDp, mode).roundToPx().coerceAtMost(width)
        val drawerOffset = (drawerWidth * state.progress).roundToInt()
        // Docked, the content makes room for the drawer (the onDrawerSlide offset); as an
        // overlay the drawer covers it.
        val contentX = if (mode == SideNavigationBarMode.Docked) drawerOffset else 0
        val contentWidth = width - contentX
        val contentPlaceables = contentMeasurables.map { it.measure(Constraints.fixed(contentWidth, height)) }
        // DrawerLayout clips the scrim to the area beside the drawer, and only a touch there
        // closes it: the scrim starts at the drawer's end edge in both modes.
        val scrimPlaceables = scrimMeasurables.map { it.measure(Constraints.fixed(width - drawerOffset, height)) }
        val drawerPlaceables = drawerMeasurables.map { it.measure(Constraints.fixed(drawerWidth, height)) }
        val dividerWidth = max(1, SideNavigationBarDefaults.DividerThickness.roundToPx())
        val dividerPlaceables = dividerMeasurables.map { it.measure(Constraints.fixed(dividerWidth, height)) }

        layout(width, height) {
            contentPlaceables.forEach { it.placeRelative(contentX, 0) }
            scrimPlaceables.forEach { it.placeRelative(drawerOffset, 0) }
            drawerPlaceables.forEach { it.placeRelative(drawerOffset - drawerWidth, 0) }
            // drawChild: the divider is translated to the drawer's end edge; a closed drawer
            // leaves no line at the start edge.
            if (drawerOffset > 0) dividerPlaceables.forEach { it.placeRelative(drawerOffset, 0) }
        }
    }
}

/** Default values of [SideNavigationBar]. */
object SideNavigationBarDefaults {

    /** `coui_side_navigation_bar_min_width`: the overlay (and small-screen) drawer width. */
    val OverlayDrawerWidth: Dp = 300.dp

    /** `coui_side_navigation_bar_max_width`: the docked drawer's maximum width. */
    val DockedDrawerMaxWidth: Dp = 280.dp

    /** The docked drawer's share of the bar width (`calculateDrawerWidth`: 0.382). */
    val DockedDrawerWidthFraction: Float = 0.382f

    /** `coui_side_navigation_drawer_divider_width`. */
    val DividerThickness: Dp = 0.33.dp

    /** The edit-mode mask fade: the default `ValueAnimator` duration. */
    const val EDIT_MASK_DURATION_MILLIS: Int = 300

    /** `ViewDragHelper`'s settle interpolator: `(t - 1)^5 + 1`. */
    val SettleEasing: Easing = Easing { t ->
        val u = t - 1f
        u * u * u * u * u + 1f
    }

    /** `coui_color_mask` (#33000000 light, #99000000 dark): the window dimming color. */
    val maskColor: Color
        @Composable get() = COUITheme.colorScheme.windowDimming

    /**
     * The drawer mode of a bar [width] x [height]: docked when the screen tier weight (small 1,
     * medium 2, large 3) is greater than the content weight (1, or 2 for a parent-child hierarchy).
     */
    fun drawerMode(width: Dp, height: Dp, parentChildHierarchy: Boolean = false): SideNavigationBarMode {
        val windowWeight = when (COUILayoutGrid.screenTier(width, height)) {
            COUILayoutGrid.ScreenTier.Small -> 1
            COUILayoutGrid.ScreenTier.Medium -> 2
            COUILayoutGrid.ScreenTier.Large -> 3
        }
        val contentWeight = if (parentChildHierarchy) 2 else 1
        return if (windowWeight - contentWeight > 0) SideNavigationBarMode.Docked else SideNavigationBarMode.Overlay
    }

    /**
     * `calculateDrawerWidth`: 300dp on a small screen or as an overlay, otherwise
     * `min(280dp, width * 0.382)`.
     */
    fun drawerWidth(width: Dp, height: Dp, mode: SideNavigationBarMode): Dp = if (
        mode == SideNavigationBarMode.Overlay ||
        COUILayoutGrid.screenTier(width, height) == COUILayoutGrid.ScreenTier.Small
    ) {
        OverlayDrawerWidth
    } else {
        min(DockedDrawerMaxWidth.value, width.value * DockedDrawerWidthFraction).dp
    }
}
