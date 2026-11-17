// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.layout

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.NavigationEventTransitionState
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import io.github.suqi8.coui.kmp.basic.CardListPosition
import io.github.suqi8.coui.kmp.basic.LocalCardListPosition
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.effect.LocalMaterialBackdrop
import io.github.suqi8.coui.kmp.effect.LocalPopupMaterialBlur
import io.github.suqi8.coui.kmp.effect.MaterialStrokePresets
import io.github.suqi8.coui.kmp.effect.materialStroke
import io.github.suqi8.coui.kmp.squircle.squircleSurface
import io.github.suqi8.coui.kmp.theme.COUIRoundCorner
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.LocalDismissState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.PI
import kotlin.math.abs

/**
 * Internal shared layout logic for [OverlayBottomSheet] and [WindowBottomSheet].
 *
 * Shape and behavior follow COUI 17 `COUIBottomSheetDialog` / `COUIBottomSheetBehavior` /
 * `COUIPanelPercentFrameLayout`:
 * - On windows narrower than [BottomSheetDefaults.LargeScreenWidthThreshold] the panel is anchored
 *   to the bottom edge and slides in. From that width on (ratio 2.0) it becomes a floating panel
 *   centered between the top inset and [BottomSheetDefaults.LargeScreenMarginBottom], with all four
 *   corners rounded, entering with a scale 0.8 -> 1 + alpha spring. A handle panel always stays
 *   bottom-anchored (`COUIPanelPercentFrameLayout.getRatio` returns 1 for it).
 * - The whole panel is draggable (`mGlobalDrag = true`); scrollable content drags it only once it is
 *   scrolled to the top.
 *
 * @param show Whether the bottom sheet is currently shown.
 * @param backgroundColor The background color of the bottom sheet.
 * @param cornerRadius The corner radius of the panel (top corners; all corners when floating).
 * @param sheetMaxWidth Additional maximum-width cap applied on top of the COUI responsive
 *   grid width and the 540dp panel cap.
 * @param outsideMargin The margin outside the bottom sheet.
 * @param insideMargin The margin inside the bottom sheet.
 * @param dragHandleColor The color of the drag handle.
 * @param popupHost A composable that provides the container (e.g., DialogLayout or Dialog).
 *   It receives the visibility state and the inner content composable.
 * @param modifier The modifier to be applied to the bottom sheet content.
 * @param title Optional title to display at the top of the bottom sheet.
 * @param startAction Optional [Composable] to display on the start side of the title.
 * @param endAction Optional [Composable] to display on the end side of the title.
 * @param enableWindowDim Whether to dim the window behind the bottom sheet.
 * @param onDismissRequest The callback when the user tries to dismiss the bottom sheet.
 * @param onDismissFinished Invoked when the hide animation completes; not invoked if the hide
 *   is cancelled mid-flight (e.g., by [show] toggling back to true).
 * @param defaultWindowInsetsPadding Whether to apply default window insets padding.
 * @param showDragHandle Whether to show the drag handle bar.
 * @param handlePanel Whether the panel is a COUI handle panel (always shows the drag handle, fills
 *   the maximum height and rests in collapsed / half-expanded / expanded states).
 * @param showInMaxHeight Whether the panel takes the maximum height instead of wrapping its content.
 * @param allowDismiss Whether to allow dismissing the sheet via drag, back gesture or outside taps.
 * @param dismissOnClickOutside Whether tapping outside the panel dismisses it.
 * @param draggable Whether the panel follows drag gestures.
 * @param enablePullUp Whether the panel can be pulled above its resting position.
 * @param enableNestedScroll Whether to enable nested scrolling for the content.
 * @param topInset Optional top inset override. If null, calculated from window insets.
 * @param backgroundModifier Applied inside the sheet's corner clip, below the content.
 * @param materialStroke Whether to draw the COUI 17 material stroke of a blurred panel.
 * @param showHeader Whether to reserve the drag handle area at the top of the panel. COUI statement
 *   dialogs remove it entirely (`drag_layout` GONE).
 * @param content The content of the bottom sheet.
 */
@Suppress("ktlint:compose:modifier-not-used-at-root")
@Composable
internal fun BottomSheetContentLayout(
    show: Boolean,
    backgroundColor: Color,
    cornerRadius: Dp,
    sheetMaxWidth: Dp,
    outsideMargin: DpSize,
    insideMargin: DpSize,
    dragHandleColor: Color,
    popupHost: @Composable (visible: Boolean, content: @Composable () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    title: String? = null,
    startAction: @Composable (() -> Unit)? = null,
    endAction: @Composable (() -> Unit)? = null,
    enableWindowDim: Boolean = true,
    onDismissRequest: (() -> Unit)? = null,
    onDismissFinished: (() -> Unit)? = null,
    defaultWindowInsetsPadding: Boolean = true,
    showDragHandle: Boolean = false,
    handlePanel: Boolean = false,
    showInMaxHeight: Boolean = false,
    allowDismiss: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    draggable: Boolean = true,
    enablePullUp: Boolean = true,
    enableNestedScroll: Boolean = true,
    topInset: Dp? = null,
    materialStroke: Boolean = false,
    showHeader: Boolean = true,
    content: @Composable () -> Unit,
) {
    val animationProgress = remember { Animatable(0f, visibilityThreshold = 0.0001f) }
    val fadeProgress = remember { Animatable(0f, visibilityThreshold = 0.0001f) }
    val dragOffsetY = remember { Animatable(0f) }
    val currentOnDismissFinished by rememberUpdatedState(onDismissFinished)
    val internalVisible = remember { mutableStateOf(false) }
    val density = LocalDensity.current
    val windowSize = LocalWindowInfo.current.containerDpSize
    val sheetHeightPx = remember { mutableIntStateOf(0) }
    val restState = remember { mutableIntStateOf(PanelState.Expanded) }
    val resolvedTopInset = topInset ?: systemTopInset()
    val centered = !handlePanel && windowSize.width >= BottomSheetDefaults.LargeScreenWidthThreshold
    val geometry = SheetGeometry(
        centered = centered,
        handlePanel = handlePanel,
        landscape = windowSize.width > windowSize.height,
        windowHeightPx = with(density) { windowSize.height.toPx() },
        topInsetPx = with(density) { resolvedTopInset.toPx() },
        bottomMarginPx = if (centered) with(density) { BottomSheetDefaults.LargeScreenMarginBottom.toPx() } else 0f,
        peekPx = with(density) { PanelPeekHeight.toPx() },
        pullUpMaxPx = with(density) { PullUpMaxOffset.toPx() },
    )
    val currentGeometry by rememberUpdatedState(geometry)

    LaunchedEffect(show) {
        // COUI samples the panel/container heights when the animation starts; mirror that
        // with a one-shot read of the latest geometry.
        val g = currentGeometry
        if (show) {
            internalVisible.value = true
            if (g.centered) {
                // COUIBottomSheetDialog.isFadeInCenter: scale 0.8 -> 1 and alpha 0 -> 1, response 0.25.
                dragOffsetY.snapTo(0f)
                restState.intValue = PanelState.Expanded
                coroutineScope {
                    launch { fadeProgress.animateTo(1f, panelSpring(CenterShowResponse)) }
                    animationProgress.animateTo(1f, panelSpring(CenterShowResponse))
                }
            } else {
                fadeProgress.snapTo(1f)
                // COUIBottomSheetDialog.initBehavior: a handle panel opens half-expanded in portrait
                // and collapsed in landscape; every other panel opens expanded.
                val initialState = when {
                    !g.handlePanel -> PanelState.Expanded
                    g.landscape -> PanelState.Collapsed
                    else -> PanelState.HalfExpanded
                }
                val heightPx = if (g.handlePanel) g.maxHeightPx else sheetHeightPx.intValue.toFloat()
                val initialOffset = g.offsetFor(initialState, heightPx)
                restState.intValue = initialState
                dragOffsetY.snapTo(initialOffset)
                animationProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = couiPanelSlideSpring(
                        entering = true,
                        travelPx = if (heightPx > 0f) heightPx - initialOffset else g.windowHeightPx,
                        containerPx = g.windowHeightPx,
                        peekPx = g.peekPx,
                    ),
                )
            }
        } else {
            if (!internalVisible.value) return@LaunchedEffect
            if (g.centered) {
                // Exit: alpha response 0.18, scale response 0.25.
                coroutineScope {
                    launch { fadeProgress.animateTo(0f, panelSpring(CenterHideAlphaResponse)) }
                    animationProgress.animateTo(0f, panelSpring(CenterShowResponse))
                }
            } else {
                // A panel already dragged off-screen still runs this so the mask fades out.
                val heightPx = sheetHeightPx.intValue.toFloat()
                val visiblePx = if (heightPx > 0f) (heightPx - dragOffsetY.value).coerceAtLeast(0f) else g.windowHeightPx
                animationProgress.animateTo(
                    targetValue = 0f,
                    animationSpec = couiPanelSlideSpring(
                        entering = false,
                        travelPx = visiblePx,
                        containerPx = g.windowHeightPx,
                        peekPx = g.peekPx,
                    ),
                )
            }
            internalVisible.value = false
            currentOnDismissFinished?.invoke()
        }
    }

    if (!show && !internalVisible.value) return

    val coroutineScope = rememberCoroutineScope()
    val dimAlpha = remember { mutableFloatStateOf(1f) }
    val dragSnapChannel = remember { Channel<Float>(capacity = Channel.CONFLATED) }
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)

    val requestDismiss: () -> Unit = remember {
        { currentOnDismissRequest?.invoke() }
    }

    val restOffset: () -> Float = {
        currentGeometry.offsetFor(restState.intValue, sheetHeightPx.intValue.toFloat())
    }

    val resetGesture: suspend () -> Unit = {
        dragOffsetY.animateTo(restOffset(), animationSpec = tween(durationMillis = 150))
        animate(dimAlpha.floatValue, 1f, animationSpec = tween(durationMillis = 150)) { value, _ ->
            dimAlpha.floatValue = value
        }
    }

    LaunchedEffect(dragOffsetY) {
        for (target in dragSnapChannel) dragOffsetY.snapTo(target)
    }

    popupHost(internalVisible.value) {
        val navigationEventState = rememberNavigationEventState(currentInfo = NavigationEventInfo.None)
        NavigationBackHandler(
            state = navigationEventState,
            isBackEnabled = show,
            onBackCancelled = { coroutineScope.launch { resetGesture() } },
            onBackCompleted = {
                if (allowDismiss) {
                    val g = currentGeometry
                    if (g.centered) {
                        requestDismiss()
                    } else {
                        coroutineScope.launch {
                            animateDismissOffScreen(
                                dragOffsetY = dragOffsetY,
                                travelPx = g.travelPx(sheetHeightPx.intValue.toFloat()),
                                windowHeightPx = g.windowHeightPx,
                                peekPx = g.peekPx,
                                dimAlpha = if (g.handlePanel) null else dimAlpha,
                            ) {
                                requestDismiss()
                            }
                        }
                    }
                } else {
                    coroutineScope.launch { resetGesture() }
                }
            },
        )

        LaunchedEffect(allowDismiss) {
            // Collect inside a single coroutine so the per-frame `transitionState` ticks during a
            // back gesture do not cancel/relaunch the LaunchedEffect on every progress update.
            snapshotFlow { navigationEventState.transitionState }
                .collect { transitionState ->
                    if (
                        transitionState is NavigationEventTransitionState.InProgress &&
                        transitionState.direction == NavigationEventTransitionState.TRANSITIONING_BACK
                    ) {
                        val maxOffset = if (sheetHeightPx.intValue > 0) sheetHeightPx.intValue.toFloat() else 500f
                        val offset = transitionState.latestEvent.progress * maxOffset
                        val finalOffset = if (!allowDismiss) offset * 0.1f else offset
                        dragSnapChannel.trySend(restOffset() + finalOffset)
                        if (allowDismiss && !currentGeometry.handlePanel) {
                            dimAlpha.floatValue = 1f - transitionState.latestEvent.progress
                        }
                    }
                }
        }

        if (enableWindowDim) {
            // COUI drives the mask alpha with the same spring as the panel slide (or the alpha
            // spring of a floating panel), which multiplying by the progress reproduces here.
            val baseColor = COUITheme.colorScheme.mask
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        val progress = if (centered) fadeProgress.value else animationProgress.value
                        drawRect(baseColor.copy(alpha = baseColor.alpha * dimAlpha.floatValue * progress))
                    },
            )
        }

        val outsideDismissible = allowDismiss && dismissOnClickOutside
        Box(
            modifier = Modifier
                .fillMaxSize()
                // Key on the flags so the gesture coroutine restarts with the latest closure
                // when the consumer toggles them; otherwise the in-flight detector keeps the stale value.
                .pointerInput(outsideDismissible) {
                    detectTapGestures(
                        onTap = {
                            if (outsideDismissible) {
                                requestDismiss()
                            }
                        },
                    )
                }
                .semantics {
                    if (outsideDismissible) {
                        onClick(label = "Dismiss") {
                            requestDismiss()
                            true
                        }
                    }
                }
                .then(
                    if (centered) {
                        Modifier.padding(top = resolvedTopInset, bottom = BottomSheetDefaults.LargeScreenMarginBottom)
                    } else {
                        Modifier
                    },
                ),
            contentAlignment = if (centered) Alignment.Center else Alignment.BottomCenter,
        ) {
            val sheetModifier = modifier.graphicsLayer {
                val offset = dragOffsetY.value
                if (centered) {
                    val scale = CenterScaleStart + (1f - CenterScaleStart) * animationProgress.value
                    scaleX = scale
                    scaleY = scale
                    alpha = fadeProgress.value
                    translationY = offset
                } else {
                    val currentHeight = sheetHeightPx.intValue.toFloat()
                    val baseOffset = if (currentHeight > 0) currentHeight else geometry.windowHeightPx
                    translationY = baseOffset * (1f - animationProgress.value) + offset
                }
            }

            BottomSheetContent(
                title = title,
                backgroundColor = backgroundColor,
                cornerRadius = cornerRadius,
                sheetMaxWidth = sheetMaxWidth,
                outsideMargin = outsideMargin,
                insideMargin = insideMargin,
                defaultWindowInsetsPadding = defaultWindowInsetsPadding,
                dragHandleColor = dragHandleColor,
                geometry = geometry,
                topInset = resolvedTopInset,
                showDragHandle = showDragHandle || handlePanel,
                showInMaxHeight = showInMaxHeight || handlePanel,
                showHeader = showHeader,
                allowDismiss = allowDismiss,
                draggable = draggable,
                enablePullUp = enablePullUp,
                enableNestedScroll = enableNestedScroll,
                sheetHeightPx = sheetHeightPx,
                restState = restState,
                dragOffsetY = dragOffsetY,
                dimAlpha = dimAlpha,
                dragSnapChannel = dragSnapChannel,
                onDismissRequest = {
                    if (allowDismiss) {
                        requestDismiss()
                    }
                },
                modifier = sheetModifier,
                backgroundModifier = backgroundModifier,
                materialStroke = materialStroke,
                startAction = startAction?.let { action ->
                    { CompositionLocalProvider(LocalDismissState provides requestDismiss, LocalMaterialBackdrop provides null, LocalPopupMaterialBlur provides null, LocalCardListPosition provides CardListPosition.None) { action() } }
                },
                endAction = endAction?.let { action ->
                    { CompositionLocalProvider(LocalDismissState provides requestDismiss, LocalMaterialBackdrop provides null, LocalPopupMaterialBlur provides null, LocalCardListPosition provides CardListPosition.None) { action() } }
                },
                content = {
                    // The page backdrop lies behind the sheet; buttons on it keep their fill.
                    CompositionLocalProvider(
                        LocalDismissState provides requestDismiss,
                        LocalMaterialBackdrop provides null,
                        LocalPopupMaterialBlur provides null,
                        LocalCardListPosition provides CardListPosition.None,
                    ) {
                        content()
                    }
                },
            )
        }
    }
}

@Suppress("ktlint:compose:modifier-not-used-at-root")
@Composable
private fun BottomSheetContent(
    title: String?,
    backgroundColor: Color,
    cornerRadius: Dp,
    sheetMaxWidth: Dp,
    outsideMargin: DpSize,
    insideMargin: DpSize,
    defaultWindowInsetsPadding: Boolean,
    dragHandleColor: Color,
    geometry: SheetGeometry,
    topInset: Dp,
    showDragHandle: Boolean,
    showInMaxHeight: Boolean,
    showHeader: Boolean,
    allowDismiss: Boolean,
    draggable: Boolean,
    enablePullUp: Boolean,
    enableNestedScroll: Boolean,
    sheetHeightPx: MutableIntState,
    restState: MutableIntState,
    dragOffsetY: Animatable<Float, AnimationVector1D>,
    dimAlpha: MutableFloatState,
    dragSnapChannel: Channel<Float>,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    materialStroke: Boolean = false,
    startAction: @Composable (() -> Unit)? = null,
    endAction: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    // Read through a State so the gesture closures below always see the latest window geometry
    // without being rebuilt (an IME resize must not recreate the handlers mid-gesture).
    val currentGeometry by rememberUpdatedState(geometry)
    val coroutineScope = rememberCoroutineScope()

    val settlingJob = remember { mutableStateOf<Job?>(null) }
    val isSettling = remember { mutableStateOf(false) }
    // Sign of the last nested scroll step (COUIBottomSheetBehavior.lastNestedScrollDy, inverted:
    // positive = finger moving down).
    val lastNestedDelta = remember { mutableFloatStateOf(0f) }
    // State the panel rested in when the current drag began (COUIBottomSheetBehavior.mPressDownState).
    val pressDownState = remember { mutableIntStateOf(PanelState.Expanded) }

    val cancelSettling: () -> Unit = {
        if (isSettling.value) {
            settlingJob.value?.cancel()
            isSettling.value = false
        }
    }

    val calculateNewOffset = remember(allowDismiss, enablePullUp) {
        { current: Float, delta: Float ->
            val maxPullUp = if (enablePullUp) {
                currentGeometry.maxPullUpPx(sheetHeightPx.intValue.toFloat())
            } else {
                0f
            }
            when {
                // Pulling above the resting position: COUIBottomSheetDialog grows the panel by
                // 0.2 of the finger travel, capped at coui_panel_pull_up_max_offset and the space
                // left above the panel.
                delta < 0f && current <= 0f -> minOf(current, maxOf(current + delta * PullUpFriction, -maxPullUp))

                delta < 0f -> {
                    val next = current + delta
                    if (next >= 0f) next else maxOf(next * PullUpFriction, -maxPullUp)
                }

                current < 0f -> (current + delta * PullUpFriction).coerceAtMost(0f)

                !allowDismiss -> (current + delta * NotDismissibleDamping).coerceAtLeast(0f)

                else -> current + delta
            }
        }
    }

    val updateDimAlpha = remember(allowDismiss) {
        { offset: Float ->
            val g = currentGeometry
            // COUIBottomSheetBehavior.calculatePanelOutsideAlpha normalizes by the parent height;
            // a handle panel keeps its mask still (setIsNeedOutsideViewAnim(false)).
            dimAlpha.floatValue = if (g.handlePanel || !allowDismiss || offset <= 0f || g.windowHeightPx <= 0f) {
                1f
            } else {
                1f - (offset / g.windowHeightPx).coerceIn(0f, 1f)
            }
        }
    }

    val performSettle: (velocity: Float, nested: Boolean) -> Unit = remember(allowDismiss) {
        { velocity, nested ->
            settlingJob.value?.cancel()
            isSettling.value = true
            settlingJob.value = coroutineScope.launch {
                val g = currentGeometry
                val heightPx = if (g.handlePanel) g.maxHeightPx else sheetHeightPx.intValue.toFloat()
                val currentOffset = dragOffsetY.value
                try {
                    if (currentOffset < 0f) {
                        // COUIBottomSheetDialog pull-up release: Rebound spring (bounciness 6, speed 42).
                        dragOffsetY.animateTo(
                            targetValue = 0f,
                            animationSpec = PullUpReboundSpring,
                            initialVelocity = velocity,
                        )
                        dimAlpha.floatValue = 1f
                        return@launch
                    }
                    val target = resolveSettleTarget(
                        geometry = g,
                        heightPx = heightPx,
                        offset = currentOffset,
                        velocity = velocity,
                        nested = nested,
                        lastNestedDelta = lastNestedDelta.floatValue,
                        pressDownState = pressDownState.intValue,
                        allowDismiss = allowDismiss,
                    )
                    if (target == PanelState.Hidden) {
                        val travelPx = g.travelPx(heightPx)
                        if (currentOffset >= travelPx) {
                            onDismissRequest()
                        } else {
                            animateDismissOffScreen(
                                dragOffsetY = dragOffsetY,
                                travelPx = travelPx,
                                windowHeightPx = g.windowHeightPx,
                                peekPx = g.peekPx,
                                dimAlpha = if (g.handlePanel) null else dimAlpha,
                                velocity = velocity,
                                draggedOut = true,
                            ) {
                                onDismissRequest()
                            }
                        }
                    } else {
                        restState.intValue = target
                        val effectiveVelocity = if (!allowDismiss && velocity > 0) 0f else velocity
                        // COUIBottomSheetBehavior.startSettleRunnable to a resting state:
                        // COUISpringForce response 0.4, bounce 0.
                        dragOffsetY.animateTo(
                            targetValue = g.offsetFor(target, heightPx),
                            animationSpec = panelSpring(SettleResponse),
                            initialVelocity = effectiveVelocity,
                        ) {
                            updateDimAlpha(value)
                        }
                    }
                } catch (_: CancellationException) {
                    // Animation is interrupted
                } finally {
                    isSettling.value = false
                }
            }
        }
    }

    // Nested scroll: the content scrolls first; once it reaches the top, a further downward drag
    // moves the panel (COUIBottomSheetBehavior.onNestedPreScroll, touch input only).
    val nestedScrollConnection = remember(enableNestedScroll, allowDismiss, draggable) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (!enableNestedScroll || !draggable || source != NestedScrollSource.UserInput) return Offset.Zero
                cancelSettling()

                val delta = available.y
                // If the sheet is offset, prioritize restoring its position
                if (delta < 0 && dragOffsetY.value > 0) {
                    val newOffset = (dragOffsetY.value + delta).coerceAtLeast(0f)
                    val consumedY = dragOffsetY.value - newOffset
                    if (consumedY != 0f) {
                        lastNestedDelta.floatValue = delta
                        dragSnapChannel.trySend(newOffset)
                        updateDimAlpha(newOffset)
                        return Offset(0f, consumedY * -1f)
                    }
                }
                return Offset.Zero
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                if (!enableNestedScroll || !draggable || source != NestedScrollSource.UserInput) return Offset.Zero

                val delta = available.y
                if (delta > 0) {
                    cancelSettling()
                    if (dragOffsetY.value == restOffsetOf(restState, currentGeometry, sheetHeightPx)) {
                        pressDownState.intValue = restState.intValue
                    }
                    lastNestedDelta.floatValue = delta
                    val newOffset = calculateNewOffset(dragOffsetY.value, delta)
                    dragSnapChannel.trySend(newOffset)
                    updateDimAlpha(newOffset)

                    // Dismiss immediately if dragged beyond window height
                    if (newOffset > currentGeometry.windowHeightPx) {
                        performSettle(0f, true)
                    }
                    return available
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                if (!enableNestedScroll || isSettling.value) return Velocity.Zero

                // Take over fling if the sheet is away from its resting position
                if (dragOffsetY.value != restOffsetOf(restState, currentGeometry, sheetHeightPx)) {
                    performSettle(available.y, true)
                    return available
                }
                return Velocity.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                if (!enableNestedScroll || isSettling.value) return Velocity.Zero

                if (dragOffsetY.value != restOffsetOf(restState, currentGeometry, sheetHeightPx)) {
                    performSettle(available.y, true)
                    return available
                }
                return super.onPostFling(consumed, available)
            }
        }
    }

    // Whole-panel drag (COUIBottomSheetBehavior mGlobalDrag = true).
    val onDrag: (Float) -> Unit = remember(allowDismiss, enablePullUp) {
        { delta ->
            val newOffset = calculateNewOffset(dragOffsetY.value, delta)
            dragSnapChannel.trySend(newOffset)
            updateDimAlpha(newOffset)
        }
    }
    val onDragStarted: () -> Unit = {
        cancelSettling()
        pressDownState.intValue = restState.intValue
    }
    val onDragStopped: (Float) -> Unit = { velocity -> performSettle(velocity, false) }

    BottomSheetColumn(
        title = title,
        startAction = startAction,
        endAction = endAction,
        backgroundColor = backgroundColor,
        cornerRadius = cornerRadius,
        sheetMaxWidth = sheetMaxWidth,
        outsideMargin = outsideMargin,
        insideMargin = insideMargin,
        defaultWindowInsetsPadding = defaultWindowInsetsPadding,
        dragHandleColor = dragHandleColor,
        centered = geometry.centered,
        topInset = topInset,
        showDragHandle = showDragHandle,
        showInMaxHeight = showInMaxHeight,
        showHeader = showHeader,
        draggable = draggable,
        enableNestedScroll = enableNestedScroll,
        sheetHeightPx = sheetHeightPx,
        dragOffsetY = dragOffsetY,
        nestedScrollConnection = nestedScrollConnection,
        onDrag = onDrag,
        onDragStarted = onDragStarted,
        onDragStopped = onDragStopped,
        modifier = modifier,
        backgroundModifier = backgroundModifier,
        materialStroke = materialStroke,
        content = content,
    )
}

private fun restOffsetOf(restState: MutableIntState, geometry: SheetGeometry, sheetHeightPx: MutableIntState): Float {
    val heightPx = if (geometry.handlePanel) geometry.maxHeightPx else sheetHeightPx.intValue.toFloat()
    return geometry.offsetFor(restState.intValue, heightPx)
}

/**
 * Resolves where a released panel settles, following `COUIBottomSheetBehavior.onViewReleased`
 * (direct drag) and `onStopNestedScroll` (content drag). Velocities are in px/s, positive downward.
 */
private fun resolveSettleTarget(
    geometry: SheetGeometry,
    heightPx: Float,
    offset: Float,
    velocity: Float,
    nested: Boolean,
    lastNestedDelta: Float,
    pressDownState: Int,
    allowDismiss: Boolean,
): Int {
    val travelPx = geometry.travelPx(heightPx)
    if (!geometry.handlePanel) {
        if (!allowDismiss) return PanelState.Expanded
        // skipCollapsed panels: shouldHide() is always true.
        val hide = if (nested) {
            lastNestedDelta >= 0f
        } else {
            velocity >= 0f && (velocity > HideVelocityThreshold || offset > travelPx / 2f)
        }
        return if (hide) PanelState.Hidden else PanelState.Expanded
    }
    val half = geometry.halfOffset(heightPx)
    val collapsed = geometry.collapsedOffset(heightPx)
    val shouldHide = allowDismiss &&
        offset >= collapsed &&
        abs(offset + velocity * 0.1f - collapsed) / geometry.peekPx > HideThreshold
    val nearest = when {
        offset < half -> if (offset < abs(offset - collapsed)) PanelState.Expanded else PanelState.HalfExpanded
        abs(offset - half) < abs(offset - collapsed) -> PanelState.HalfExpanded
        else -> PanelState.Collapsed
    }
    val inBetween = offset > half && offset < collapsed
    val downward = if (!(pressDownState == PanelState.HalfExpanded && inBetween) &&
        abs(offset - half) < abs(offset - collapsed)
    ) {
        PanelState.HalfExpanded
    } else {
        PanelState.Collapsed
    }
    val upward = if (offset > half) PanelState.HalfExpanded else PanelState.Expanded
    if (nested) {
        return when {
            lastNestedDelta < 0f -> upward
            shouldHide -> PanelState.Hidden
            lastNestedDelta == 0f -> nearest
            else -> downward
        }
    }
    return when {
        velocity < 0f -> upward

        shouldHide -> when {
            velocity > HideVelocityThreshold || offset > travelPx / 2f -> PanelState.Hidden
            abs(offset) < abs(offset - half) -> PanelState.Expanded
            else -> PanelState.HalfExpanded
        }

        velocity == 0f -> nearest

        else -> downward
    }
}

// showInMaxHeight is fixed for a sheet's lifetime, so the two content() branches never swap at runtime.
@Suppress("ktlint:compose:modifier-not-used-at-root", "ktlint:compose:content-slot-reused")
@Composable
private fun BottomSheetColumn(
    title: String?,
    startAction: (@Composable () -> Unit)?,
    endAction: (@Composable () -> Unit)?,
    backgroundColor: Color,
    cornerRadius: Dp,
    sheetMaxWidth: Dp,
    outsideMargin: DpSize,
    insideMargin: DpSize,
    defaultWindowInsetsPadding: Boolean,
    dragHandleColor: Color,
    centered: Boolean,
    topInset: Dp,
    showDragHandle: Boolean,
    showInMaxHeight: Boolean,
    showHeader: Boolean,
    draggable: Boolean,
    enableNestedScroll: Boolean,
    sheetHeightPx: MutableIntState,
    dragOffsetY: Animatable<Float, AnimationVector1D>,
    nestedScrollConnection: NestedScrollConnection,
    onDrag: (Float) -> Unit,
    onDragStarted: () -> Unit,
    onDragStopped: (velocity: Float) -> Unit,
    modifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    materialStroke: Boolean = false,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val imeInsets = WindowInsets.ime
    // COUI grid width, the 540dp panel cap and `sheetMaxWidth` as an additional caller-supplied cap.
    val windowSize = LocalWindowInfo.current.containerDpSize
    val effectiveMaxWidth = remember(windowSize, sheetMaxWidth) {
        min(min(sheetMaxWidth, couiPanelMaxWidth(windowSize)), BottomSheetDefaults.PanelMaxWidth)
    }
    val maxHeight = if (centered) largeScreenPanelMaxHeight(windowSize, topInset) else windowSize.height - topInset
    // COUIPanelPercentFrameLayout.updatePath: a floating panel rounds all four corners.
    val bottomCornerRadius = if (centered) cornerRadius else 0.dp
    val currentOnDrag by rememberUpdatedState(onDrag)
    val currentOnDragStarted by rememberUpdatedState(onDragStarted)
    val currentOnDragStopped by rememberUpdatedState(onDragStopped)

    Column(
        modifier = modifier
            .pointerInput(Unit) { detectTapGestures { /* Consume click */ } }
            .then(if (enableNestedScroll) Modifier.nestedScroll(nestedScrollConnection) else Modifier)
            .draggable(
                state = rememberDraggableState { delta -> currentOnDrag(delta) },
                orientation = Orientation.Vertical,
                enabled = draggable,
                onDragStarted = { currentOnDragStarted() },
                onDragStopped = { velocity -> currentOnDragStopped(velocity) },
            )
            .widthIn(max = effectiveMaxWidth)
            .fillMaxWidth()
            .then(
                if (showInMaxHeight) {
                    Modifier.height(maxHeight)
                } else {
                    Modifier
                        .wrapContentHeight()
                        .heightIn(max = maxHeight)
                },
            )
            .onGloballyPositioned { coordinates ->
                // Only capture the natural sheet height while the IME is closed; otherwise the
                // bottom inset added by imePadding would inflate sheetHeightPx, shifting the
                // dim threshold and causing translationY jumps mid-animation.
                if (imeInsets.getBottom(density) == 0) {
                    sheetHeightPx.intValue = coordinates.size.height
                }
            }
            .then(if (defaultWindowInsetsPadding) Modifier.imePadding() else Modifier)
            .padding(horizontal = outsideMargin.width)
            .drawBehind {
                // Pull-up stretch: the panel is translated up by the pull distance and its
                // surface is extended below its bottom edge, so it reads as growing upward.
                val pullUp = -dragOffsetY.value
                if (pullUp <= 0f) return@drawBehind
                if (centered) {
                    val radius = bottomCornerRadius.toPx()
                    drawRect(
                        color = backgroundColor,
                        topLeft = Offset(0f, size.height - radius),
                        size = Size(size.width, pullUp),
                    )
                    drawRoundRect(
                        color = backgroundColor,
                        topLeft = Offset(0f, size.height + pullUp - 2f * radius),
                        size = Size(size.width, 2f * radius),
                        cornerRadius = CornerRadius(radius, radius),
                    )
                } else {
                    drawRect(
                        color = backgroundColor,
                        topLeft = Offset(0f, size.height - 1f),
                        size = Size(size.width, pullUp + 1f),
                    )
                }
            }
            .squircleSurface(
                color = backgroundColor,
                topStart = cornerRadius,
                topEnd = cornerRadius,
                bottomEnd = bottomCornerRadius,
                bottomStart = bottomCornerRadius,
                weight = COUIRoundCorner.Large.Weight,
            )
            // Inside the corner clip: a backdrop blur provider paints here, under the content.
            .then(backgroundModifier)
            // COUIBottomSheetDialog's shadow edge, following the panel corners.
            .materialStroke(
                light = MaterialStrokePresets.SheetLight,
                dark = MaterialStrokePresets.SheetDark,
                topCornerRadius = cornerRadius,
                bottomCornerRadius = bottomCornerRadius,
                enabled = materialStroke,
            )
            .padding(horizontal = insideMargin.width)
            .padding(bottom = insideMargin.height),
    ) {
        if (showHeader) {
            if (showDragHandle) {
                DragHandleArea(dragHandleColor = dragHandleColor)
            } else {
                // COUIBottomSheetDialog.hideDragView: the drag layout stays as an invisible
                // coui_panel_drag_view_hide_height spacer.
                Spacer(modifier = Modifier.height(BottomSheetDefaults.DragHandleHiddenHeight))
            }
        }

        if (title != null || startAction != null || endAction != null) {
            TitleAndActionsRow(
                title = title,
                startAction = startAction,
                endAction = endAction,
            )
        }

        if (showInMaxHeight) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                content()
            }
        } else {
            content()
        }
    }
}

/** Easing curve matching COUIEaseInterpolator (cubic-bezier 0.33, 0, 0.67, 1). */
private val CouiEaseEasing = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

/**
 * Peek height of a collapsed handle panel, also used to normalize the panel travel fraction.
 * COUI `coui_panel_default_peek_height_in_gesture` (gesture navigation; the three-button
 * `coui_panel_default_peek_height` 66dp is not distinguished).
 */
private val PanelPeekHeight = 82.dp

/** COUI `coui_panel_pull_up_max_offset`: the furthest a panel can be pulled above its rest. */
private val PullUpMaxOffset = 28.dp

/** COUIBottomSheetDialog pull-up friction: the panel grows by `dy * 0.19999999`. */
private val PullUpFriction = 0.2f

/** Resistance of a non-dismissible panel dragged downward. */
private val NotDismissibleDamping = 0.1f

/** COUIBottomSheetBehavior.onViewReleased: downward fling speed (px/s) that hides the panel. */
private val HideVelocityThreshold = 500f

/** COUIBottomSheetBehavior.HIDE_THRESHOLD, applied to the peek height by `shouldHide`. */
private val HideThreshold = 0.5f

/** COUIBottomSheetBehavior.halfExpandedRatio. */
private val HalfExpandedRatio = 0.5f

/** COUIBottomSheetBehavior settle spring response (bounce 0). */
private val SettleResponse = 0.4f

/** COUIBottomSheetDialog.DEFAULT_CENTER_SHOW_SPRING_RESPONSE: floating panel scale/alpha response. */
private val CenterShowResponse = 0.25f

/** COUIBottomSheetDialog: floating panel alpha response when leaving. */
private val CenterHideAlphaResponse = 0.18f

/** COUIBottomSheetDialog.DIALOG_SHOW_SCALE_START. */
private val CenterScaleStart = 0.8f

/**
 * COUIBottomSheetDialog pull-up rebound: Rebound `SpringConfig.fromBouncinessAndSpeed(6, 42)`,
 * i.e. origami tension 246.9 / friction 14.0 -> stiffness 979, damping ratio 0.69.
 */
private val PullUpReboundSpring: SpringSpec<Float> = spring(dampingRatio = 0.69f, stiffness = 979f)

/** Resting states of a COUI panel (COUIBottomSheetBehavior STATE_* values). */
private object PanelState {
    val Expanded = 0
    val HalfExpanded = 1
    val Collapsed = 2
    val Hidden = 3
}

/**
 * Window geometry snapshot shared by the gesture and animation code. Offsets are measured
 * downward from the expanded resting position of the panel.
 */
private class SheetGeometry(
    val centered: Boolean,
    val handlePanel: Boolean,
    val landscape: Boolean,
    val windowHeightPx: Float,
    val topInsetPx: Float,
    val bottomMarginPx: Float,
    val peekPx: Float,
    val pullUpMaxPx: Float,
) {
    /** Height of a panel laid out at the maximum height in the bottom-anchored form. */
    val maxHeightPx: Float get() = (windowHeightPx - topInsetPx).coerceAtLeast(0f)

    /** Distance from the resting top edge of the panel to the window bottom. */
    fun travelPx(heightPx: Float): Float {
        if (heightPx <= 0f) return windowHeightPx
        if (!centered) return heightPx
        return windowHeightPx - topInsetPx - topSpacePx(heightPx)
    }

    /** Free space between the top inset and the resting top edge of the panel. */
    fun topSpacePx(heightPx: Float): Float = if (centered) {
        ((windowHeightPx - topInsetPx - bottomMarginPx - heightPx) / 2f).coerceAtLeast(0f)
    } else {
        (windowHeightPx - topInsetPx - heightPx).coerceAtLeast(0f)
    }

    /** COUIBottomSheetDialog: pull-up is capped by the max offset and the space above the panel. */
    fun maxPullUpPx(heightPx: Float): Float = if (heightPx <= 0f) 0f else minOf(pullUpMaxPx, topSpacePx(heightPx))

    /** COUIBottomSheetBehavior.calculateHalfExpandedOffset: half of the parent stays visible. */
    fun halfOffset(heightPx: Float): Float = (heightPx - windowHeightPx * HalfExpandedRatio).coerceAtLeast(0f)

    /** Only the peek height stays visible. */
    fun collapsedOffset(heightPx: Float): Float = (heightPx - peekPx).coerceAtLeast(0f)

    fun offsetFor(state: Int, heightPx: Float): Float = when (state) {
        PanelState.HalfExpanded -> halfOffset(heightPx)
        PanelState.Collapsed -> collapsedOffset(heightPx)
        else -> 0f
    }
}

/** Status bar / caption bar / display cutout top inset, whichever is largest. */
@Composable
private fun systemTopInset(): Dp {
    val statusBars = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val captionBar = WindowInsets.captionBar.asPaddingValues().calculateTopPadding()
    val displayCutout = WindowInsets.displayCutout.asPaddingValues().calculateTopPadding()
    return maxOf(statusBars, captionBar, displayCutout)
}

/** A COUISpringForce with bounce 0: damping ratio 1, stiffness `(2 * PI / response)^2`. */
private fun <T> panelSpring(response: Float): SpringSpec<T> {
    val stiffness = ((2.0 * PI / response) * (2.0 * PI / response)).toFloat()
    return spring(dampingRatio = 1f, stiffness = stiffness)
}

/**
 * Spring spec of the COUI panel slide (COUIBottomSheetDialog.getTranslationResponse): bounce 0,
 * response interpolated between 0.25s and 0.45s (enter) / 0.18s and 0.37s (exit) by the travel
 * fraction `(travel - peek) / (container - peek)`.
 */
private fun <T> couiPanelSlideSpring(
    entering: Boolean,
    travelPx: Float,
    containerPx: Float,
    peekPx: Float,
): SpringSpec<T> {
    val start = if (entering) 0.25f else 0.18f
    val end = if (entering) 0.45f else 0.37f
    val denominator = containerPx - peekPx
    val fraction = if (denominator > 0f) ((travelPx - peekPx) / denominator).coerceIn(0f, 1f) else 1f
    return panelSpring(start + (end - start) * fraction)
}

/**
 * Maximum height of a floating panel (COUIPanelMultiWindowUtils.getPanelMaxHeight): the window
 * height less coui_panel_min_padding_top and the bottom margin, the centering area, and
 * coui_panel_max_height (800dp on sw600dp, 2000dp otherwise).
 */
private fun largeScreenPanelMaxHeight(windowSize: DpSize, topInset: Dp): Dp {
    val area = windowSize.height - topInset - BottomSheetDefaults.LargeScreenMarginBottom
    val visible = windowSize.height - LargeScreenMinPaddingTop - BottomSheetDefaults.LargeScreenMarginBottom
    val cap = if (min(windowSize.width, windowSize.height) >= 600.dp) LargeScreenMaxHeightSw600 else LargeScreenMaxHeight
    return minOf(area, visible, cap).coerceAtLeast(0.dp)
}

/** COUI `coui_panel_min_padding_top`. */
private val LargeScreenMinPaddingTop = 40.dp

/** COUI `coui_panel_max_height` in values-sw600dp. */
private val LargeScreenMaxHeightSw600 = 800.dp

/** COUI `coui_panel_max_height`. */
private val LargeScreenMaxHeight = 2000.dp

/**
 * Maximum panel width from the COUI responsive layout grid: full width on compact windows
 * (< 600dp), otherwise a 6-column span of the 8-column (margin 24dp, gutter 8dp) or
 * 12-column (margin 40dp, gutter 12dp) grid, chosen by the COUI window size classes.
 */
private fun couiPanelMaxWidth(windowSize: DpSize): Dp {
    val width = windowSize.width.value
    val height = windowSize.height.value
    if (width < 600f) return Dp.Infinity
    val eightColumns = width < 840f || height < 480f || (height >= 900f && height > width && width < 960f)
    return if (eightColumns) {
        ((width - 2f * 24f - 7f * 8f) / 8f * 6f + 5f * 8f).dp
    } else {
        ((width - 2f * 40f - 11f * 12f) / 12f * 6f + 5f * 12f).dp
    }
}

private fun CoroutineScope.animateHandlePressDown(pressAlpha: Animatable<Float, *>) {
    // COUIPanelPressHelper.startAnim: bgAlpha 0 -> 1, 200ms, COUIEaseInterpolator.
    launch { pressAlpha.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 200, easing = CouiEaseEasing)) }
}

private fun CoroutineScope.animateHandlePressRelease(pressAlpha: Animatable<Float, *>) {
    // COUIPanelPressHelper.endAnim: bgAlpha -> 0, 200ms, COUIEaseInterpolator.
    launch { pressAlpha.animateTo(targetValue = 0f, animationSpec = tween(durationMillis = 200, easing = CouiEaseEasing)) }
}

/**
 * Drag handle strip at the top of the sheet, matching coui_panel_view_layout.xml: `drag_layout`
 * (coui_panel_drag_view_shadow_margin_top 4dp + coui_panel_drag_view_height 12dp) holding the
 * 36x4dp capsule 8dp below its top, and a 70x20dp press background (`tv_drag_press_bg`) aligned
 * to its top that fades in while the handle is touched. The press background overflows the strip
 * downward, as the `clipChildren="false"` layout lets it. The strip does not consume touches, so
 * the whole-panel drag still runs from it.
 */
@Composable
private fun DragHandleArea(
    dragHandleColor: Color,
) {
    val coroutineScope = rememberCoroutineScope()
    val pressAlpha = remember { Animatable(0f) }
    // COUI coui_pannel_press_shadow_bg is tinted with ?couiColorPress.
    val pressColor = COUITheme.colorScheme.press

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(BottomSheetDefaults.DragHandleShownHeight)
            .pointerHoverIcon(PointerIcon.Hand)
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    coroutineScope.animateHandlePressDown(pressAlpha)
                    do {
                        val event = awaitPointerEvent()
                    } while (event.changes.any { it.pressed })
                    coroutineScope.animateHandlePressRelease(pressAlpha)
                }
            }
            .drawBehind {
                val top = DragHandleMarginTop.toPx()
                val pressWidth = DragHandlePressWidth.toPx()
                val pressHeight = DragHandlePressHeight.toPx()
                val pressRadius = DragHandlePressCornerRadius.toPx()
                drawRoundRect(
                    color = pressColor.copy(alpha = pressColor.alpha * pressAlpha.value),
                    topLeft = Offset((size.width - pressWidth) / 2f, top),
                    size = Size(pressWidth, pressHeight),
                    cornerRadius = CornerRadius(pressRadius, pressRadius),
                )
                val barWidth = DragHandleBarWidth.toPx()
                val barHeight = DragHandleBarHeight.toPx()
                val barRadius = DragHandleBarCornerRadius.toPx()
                drawRoundRect(
                    color = dragHandleColor,
                    topLeft = Offset((size.width - barWidth) / 2f, top + DragHandleBarPaddingTop.toPx()),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(barRadius, barRadius),
                )
            },
    )
}

/** Panel toolbar metrics (Widget.COUI.Toolbar.Panel). */
private val TitleRowMinHeight = 52.dp
private val TitleRowPaddingStart = 8.dp
private val TitleRowPaddingEnd = 11.dp

/** COUI `coui_panel_drag_view_shadow_margin_top`. */
private val DragHandleMarginTop = 4.dp

/** COUI `coui_panel_drag_view_padding_vertical` (top padding of the capsule image). */
private val DragHandleBarPaddingTop = 8.dp

/** COUI `coui_panel_drag_view` drawable: 36x4dp, corner radius 2dp. */
private val DragHandleBarWidth = 36.dp
private val DragHandleBarHeight = 4.dp
private val DragHandleBarCornerRadius = 2.dp

/** COUI `coui_panel_drag_view_shadow_width` / `_height` and `coui_pannel_press_shadow_bg` radius. */
private val DragHandlePressWidth = 70.dp
private val DragHandlePressHeight = 20.dp
private val DragHandlePressCornerRadius = 16.dp

@Composable
private fun TitleAndActionsRow(
    title: String?,
    startAction: (@Composable () -> Unit)?,
    endAction: (@Composable () -> Unit)?,
) {
    // Widget.COUI.Toolbar.Panel: minHeight toolbar_min_height 52dp, no vertical padding,
    // toolbar_normal_menu_padding_left / _right 8dp / 11dp, buttons centered vertically.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = TitleRowMinHeight)
            .padding(start = TitleRowPaddingStart, end = TitleRowPaddingEnd),
    ) {
        // Start action (e.g. close button)
        Box(modifier = Modifier.align(Alignment.CenterStart)) {
            startAction?.invoke()
        }

        // Title text
        title?.let {
            Text(
                text = it,
                modifier = Modifier.align(Alignment.Center),
                fontSize = COUITheme.textStyles.headlineS.fontSize,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = COUITheme.colorScheme.labelPrimary,
            )
        }

        // End action (e.g. submit button)
        Box(modifier = Modifier.align(Alignment.CenterEnd)) {
            endAction?.invoke()
        }
    }
}

/**
 * Animate the sheet off-screen, updating the dim alpha along the way (parent-height normalized,
 * like COUIBottomSheetBehavior.calculatePanelOutsideAlpha; skipped when [dimAlpha] is null).
 * Dismisses as soon as the sheet leaves the viewport (offset >= [travelPx]).
 */
private suspend fun CoroutineScope.animateDismissOffScreen(
    dragOffsetY: Animatable<Float, AnimationVector1D>,
    travelPx: Float,
    windowHeightPx: Float,
    peekPx: Float,
    dimAlpha: MutableFloatState?,
    velocity: Float = 0f,
    draggedOut: Boolean = false,
    onDismiss: () -> Unit,
) {
    val visible = (travelPx - dragOffsetY.value).coerceAtLeast(0f)
    val spec: SpringSpec<Float> = if (draggedOut) {
        // COUIBottomSheetBehavior.startSettleRunnable(STATE_HIDDEN) after a drag: response
        // 0.18 + 0.19 * (the part of the sheet still on screen / parent height), bounce 0.
        val fraction = if (windowHeightPx > 0f) (visible / windowHeightPx).coerceIn(0f, 1f) else 0f
        panelSpring(0.18f + 0.19f * fraction)
    } else {
        couiPanelSlideSpring(
            entering = false,
            travelPx = visible,
            containerPx = windowHeightPx,
            peekPx = peekPx,
        )
    }
    val settleJob = launch {
        dragOffsetY.animateTo(
            targetValue = maxOf(windowHeightPx, travelPx),
            animationSpec = spec,
            initialVelocity = velocity,
        ) {
            if (dimAlpha != null && windowHeightPx > 0f) {
                dimAlpha.floatValue = 1f - (value / windowHeightPx).coerceIn(0f, 1f)
            }
        }
    }
    snapshotFlow { dragOffsetY.value }
        .first { travelPx > 0 && it >= travelPx }
    settleJob.cancel()
    onDismiss()
}

object BottomSheetDefaults {

    /**
     * The default background color of the bottom sheet. COUI tints the panel with
     * `couiColorSurface` ([io.github.suqi8.coui.kmp.theme.Colors.surface]).
     */
    @Composable
    fun backgroundColor() = COUITheme.colorScheme.surface

    /**
     * The default color of the drag handle. COUI `DefaultBottomSheetDialog` tints the
     * `coui_panel_drag_view` capsule with `panelDragViewTintColor = ?couiColorControls`.
     */
    @Composable
    fun dragHandleColor() = COUITheme.colorScheme.dragHandle

    /**
     * The default corner radius of the bottom sheet. COUI 17 `COUIBottomSheetDialog` takes
     * `coui_round_corner_xxxxl_radius_large_16_1` (45dp) once the Oplus OS version is above 37
     * (`couiRoundCornerXXXXL` before), drawn by the squircle surface as a weight-3.0 smooth round
     * rect ([COUIRoundCorner.Large.Weight]). A bottom-anchored panel rounds the top corners only
     * (the outline COUI extends off-screen); a floating panel rounds all four.
     */
    val cornerRadius = 45.dp

    /**
     * The default maximum width cap of the bottom sheet. Unbounded by default: the effective
     * width is governed by the COUI responsive layout grid (full width on compact windows,
     * a 6-column grid span on medium/expanded windows) and [PanelMaxWidth]. Pass a finite value
     * to cap it further.
     */
    val maxWidth = Dp.Infinity

    /**
     * Hard width cap of the panel (COUI `coui_panel_max_width`): COUIPanelPercentFrameLayout
     * re-measures a wider panel at exactly this width.
     */
    val PanelMaxWidth = 540.dp

    /**
     * Window width from which the panel floats centered instead of being anchored to the bottom
     * edge (COUIPanelMultiWindowUtils SMALLEST_SCREEN_WIDTH_DP_THRESHOLD, panel ratio 2.0).
     */
    val LargeScreenWidthThreshold = 600.dp

    /**
     * Bottom margin of a floating panel (COUI `coui_bottom_sheet_margin_bottom_default`). COUI
     * subtracts the navigation bar from it inside a layout that already excludes the bar, so the
     * panel keeps this distance from the window bottom.
     */
    val LargeScreenMarginBottom = 64.dp

    /**
     * Height of the drag handle area while the handle is shown: coui_panel_drag_view_shadow_margin_top
     * 4dp + coui_panel_drag_view_height 12dp.
     */
    val DragHandleShownHeight = 16.dp

    /**
     * Height kept above the content while the handle is hidden (COUI
     * `coui_panel_drag_view_hide_height`, COUIBottomSheetDialog.setHideDragViewHeight).
     */
    val DragHandleHiddenHeight = 10.dp

    /**
     * The default margin outside the bottom sheet.
     */
    val outsideMargin = DpSize(0.dp, 0.dp)

    /**
     * The default margin inside the bottom sheet. COUI panels add no padding of their own
     * (design_bottom_sheet_dialog.xml, coui_panel_view_layout.xml, coui_panel_layout.xml); the
     * content sets its own margins.
     */
    val insideMargin = DpSize(0.dp, 0.dp)
}
