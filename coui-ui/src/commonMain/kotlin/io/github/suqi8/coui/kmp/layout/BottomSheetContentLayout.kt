// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.layout

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.VectorizedFiniteAnimationSpec
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import kotlinx.coroutines.withTimeoutOrNull
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
 * - The slide covers the part of the panel that is on screen (`getTranslationDistance`) and starts
 *   once the panel is laid out. A show or hide in flight is turned around from where the panel is
 *   drawn, keeping its spring and speed (`reversalAnimation`).
 * - The whole panel is draggable (`mGlobalDrag = true`); scrollable content drags it only once it is
 *   scrolled to the top. A released panel settles with a bounce-0 spring started at the release
 *   speed, an overshoot past the resting state shown at 0.4 (`panelHeightVerticalMoving`). A panel
 *   that cannot be dismissed follows the finger down to its collapsed (peek) offset and may rest there.
 * - Released to close, the panel slides off while the mask fades on its own spring
 *   (`dismissWithAlphaAnim`), and the keyboard is hidden as soon as the panel starts to leave.
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
 *   When false, a drag down stops at the collapsed (peek) offset.
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
    val panel = remember { BottomSheetPanelState() }
    val currentOnDismissFinished by rememberUpdatedState(onDismissFinished)
    val currentShow by rememberUpdatedState(show)
    val density = LocalDensity.current
    val windowSize = LocalWindowInfo.current.containerDpSize
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
        // COUI samples the panel/container heights when the animation starts; mirror that with a
        // one-shot read of the latest geometry.
        if (show) {
            panel.animateShow(currentGeometry)
        } else if (panel.visible.value) {
            panel.animateHide(currentGeometry)
            panel.visible.value = false
            // The next show measures the panel afresh before it slides in.
            panel.heightPx.intValue = 0
            currentOnDismissFinished?.invoke()
        }
    }

    if (!show && !panel.visible.value) return

    val scope = rememberCoroutineScope()
    val dragSnapChannel = remember { Channel<Float>(capacity = Channel.CONFLATED) }
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)

    val requestDismiss: () -> Unit = remember {
        { currentOnDismissRequest?.invoke() }
    }

    val resetGesture: suspend () -> Unit = {
        coroutineScope {
            launch { panel.animateDimTo(1f, SettleResponse) }
            panel.dragOffset.animateTo(panel.restOffset(currentGeometry), panelSpring(SettleResponse))
        }
    }

    // COUI always dismisses here; the panel returns to rest if the caller keeps the sheet shown.
    val requestDismissFromGesture: () -> Unit = {
        scope.launch {
            requestDismiss()
            repeat(DismissConfirmFrames) { withFrameNanos { } }
            if (currentShow) resetGesture()
        }
    }

    LaunchedEffect(panel) {
        for (target in dragSnapChannel) {
            panel.dragOffset.snapTo(target)
            if (panel.pendingOffset == target) panel.pendingOffset = Float.NaN
        }
    }

    popupHost(panel.visible.value) {
        // COUIBottomSheetDialog.dismiss(boolean) hides the keyboard as soon as the panel starts to leave.
        val keyboardController = LocalSoftwareKeyboardController.current
        val imeInsets = WindowInsets.ime
        val hostDensity = LocalDensity.current
        LaunchedEffect(show) {
            if (!show && imeInsets.getBottom(hostDensity) > 0) keyboardController?.hide()
        }

        val navigationEventState = rememberNavigationEventState(currentInfo = NavigationEventInfo.None)
        NavigationBackHandler(
            state = navigationEventState,
            isBackEnabled = show,
            onBackCancelled = { scope.launch { resetGesture() } },
            onBackCompleted = {
                if (allowDismiss) {
                    // COUIBottomSheetDialog.onBackPressed -> cancel(): the regular hide animation,
                    // continued from where the back gesture left the panel.
                    requestDismissFromGesture()
                } else {
                    scope.launch { resetGesture() }
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
                        val measuredHeight = panel.heightPx.intValue
                        val maxOffset = if (measuredHeight > 0) measuredHeight.toFloat() else 500f
                        val offset = transitionState.latestEvent.progress * maxOffset
                        val finalOffset = if (!allowDismiss) offset * 0.1f else offset
                        dragSnapChannel.trySend(panel.restOffset(currentGeometry) + finalOffset)
                        if (allowDismiss && !currentGeometry.handlePanel) {
                            panel.dimAlpha.floatValue = 1f - transitionState.latestEvent.progress
                        }
                    }
                }
        }

        if (enableWindowDim) {
            // COUI drives the mask alpha with the same spring as the panel slide (or the alpha
            // spring of a floating panel); after a drag-to-dismiss it fades on its own spring.
            val baseColor = COUITheme.colorScheme.mask
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        val progress = when {
                            panel.maskFadingOut.value -> 1f
                            centered -> panel.fade.value
                            else -> panel.progress.value
                        }
                        drawRect(baseColor.copy(alpha = baseColor.alpha * panel.dimAlpha.floatValue * progress))
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
                val offset = panel.dragOffset.value
                if (centered) {
                    val scale = CenterScaleStart + (1f - CenterScaleStart) * panel.progress.value
                    scaleX = scale
                    scaleY = scale
                    alpha = panel.fade.value
                    translationY = offset
                } else {
                    // COUIBottomSheetDialog.translateUpdate: the slide covers the visible part of the panel.
                    translationY = panel.slideTravel.floatValue * (1f - panel.progress.value) + offset
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
                panel = panel,
                dragSnapChannel = dragSnapChannel,
                isShown = { currentShow },
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
    panel: BottomSheetPanelState,
    dragSnapChannel: Channel<Float>,
    isShown: () -> Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    materialStroke: Boolean = false,
    startAction: @Composable (() -> Unit)? = null,
    endAction: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    // Read through States so the gesture closures below always see the latest values without being
    // rebuilt (an IME resize must not recreate the handlers mid-gesture).
    val currentGeometry by rememberUpdatedState(geometry)
    val currentIsShown by rememberUpdatedState(isShown)
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)
    val minFlingVelocityPx = with(LocalDensity.current) { MinFlingVelocity.toPx() }
    val currentMinFlingVelocityPx by rememberUpdatedState(minFlingVelocityPx)
    val keyboardController = LocalSoftwareKeyboardController.current
    val currentKeyboardController by rememberUpdatedState(keyboardController)
    val scope = rememberCoroutineScope()

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
        if (panel.maskFadingOut.value) {
            // A panel grabbed again on its way out: the mask follows the drag once more.
            panel.maskJob?.cancel()
            panel.maskFadingOut.value = false
        }
    }

    val updateDimAlpha: (Float) -> Unit = remember {
        { offset: Float ->
            val g = currentGeometry
            // COUIBottomSheetBehavior.calculatePanelOutsideAlpha normalizes by the parent height;
            // a handle panel keeps its mask still (setIsNeedOutsideViewAnim(false)).
            panel.dimAlpha.floatValue = if (g.handlePanel || offset <= 0f || g.containerPx <= 0f) {
                1f
            } else {
                1f - (offset / g.containerPx).coerceIn(0f, 1f)
            }
        }
    }

    // Hands a gesture offset to the panel. Gestures compute from the latest value handed over, so
    // steps that arrive before the snap lands are not lost.
    val moveTo: (Float) -> Unit = remember {
        { offset: Float ->
            panel.pendingOffset = offset
            dragSnapChannel.trySend(offset)
            updateDimAlpha(offset)
        }
    }

    val calculateNewOffset = remember(allowDismiss, enablePullUp) {
        { current: Float, delta: Float ->
            val g = currentGeometry
            val heightPx = g.panelHeightPx(panel.heightPx.intValue)
            val maxPullUp = if (enablePullUp) g.maxPullUpPx(heightPx) else 0f
            val next = when {
                // Pulling above the resting position: COUIBottomSheetDialog grows the panel by 0.2
                // of the finger travel, capped at coui_panel_pull_up_max_offset and the space left
                // above the panel. Moving back down follows the finger again.
                delta < 0f && current <= 0f -> minOf(current, maxOf(current + delta * PullUpFriction, -maxPullUp))

                delta < 0f -> {
                    val moved = current + delta
                    if (moved >= 0f) moved else maxOf(moved * PullUpFriction, -maxPullUp)
                }

                else -> current + delta
            }
            // COUIBottomSheetBehavior.clampViewPositionVertical: a panel that cannot be hidden stops
            // at its collapsed offset.
            if (!allowDismiss && next > current) {
                minOf(next, maxOf(current, g.collapsedOffset(heightPx)))
            } else {
                next
            }
        }
    }

    val performSettle: (velocity: Float, nested: Boolean) -> Unit = remember(allowDismiss) {
        { velocity, nested ->
            settlingJob.value?.cancel()
            isSettling.value = true
            settlingJob.value = scope.launch {
                val g = currentGeometry
                val heightPx = g.panelHeightPx(panel.heightPx.intValue)
                val currentOffset = panel.dragOffset.value
                try {
                    if (currentOffset < 0f) {
                        // COUIBottomSheetDialog pull-up release: Rebound spring (bounciness 6, speed 42).
                        panel.dragOffset.animateTo(
                            targetValue = 0f,
                            animationSpec = PullUpReboundSpring,
                            initialVelocity = velocity,
                        )
                        panel.dimAlpha.floatValue = 1f
                        return@launch
                    }
                    // COUIViewDragHelper.releaseViewForPointerUp: a release slower than the minimum
                    // fling speed counts as still.
                    val releaseVelocity = if (!nested && abs(velocity) < currentMinFlingVelocityPx) 0f else velocity
                    val target = resolveSettleTarget(
                        geometry = g,
                        heightPx = heightPx,
                        offset = currentOffset,
                        velocity = releaseVelocity,
                        nested = nested,
                        lastNestedDelta = lastNestedDelta.floatValue,
                        pressDownState = pressDownState.intValue,
                        allowDismiss = allowDismiss,
                    )
                    if (target == PanelState.Hidden) {
                        // COUIBottomSheetBehavior.startSettlingAnimation(STATE_HIDDEN): the keyboard
                        // goes (mCanHideKeyboard) and the dialog fades the mask out on its own spring
                        // right away (PullUpToDismissPanelListener -> dismissWithAlphaAnim).
                        currentKeyboardController?.hide()
                        val travelPx = g.travelPx(heightPx)
                        val visiblePx = (travelPx - currentOffset).coerceAtLeast(0f)
                        panel.fadeOutMask(
                            scope = this,
                            response = if (g.centered) {
                                CenterHideAlphaResponse
                            } else {
                                couiPanelResponse(entering = false, travelPx = visiblePx, geometry = g)
                            },
                        )
                        if (currentOffset < travelPx) {
                            slidePanelOffWindow(
                                dragOffsetY = panel.dragOffset,
                                travelPx = travelPx,
                                // COUI settles a hidden panel to the root view height, the top inset
                                // below the window bottom.
                                targetPx = travelPx + maxOf(g.topInsetPx, 1f),
                                containerPx = g.containerPx,
                                velocity = velocity,
                            )
                        }
                        currentOnDismissRequest()
                        // COUI always dismisses here; put the panel back if the caller keeps it shown.
                        repeat(DismissConfirmFrames) { withFrameNanos { } }
                        if (currentIsShown()) {
                            panel.maskJob?.cancel()
                            panel.maskFadingOut.value = false
                            coroutineScope {
                                launch { panel.animateDimTo(1f, SettleResponse) }
                                panel.dragOffset.animateTo(panel.restOffset(g), panelSpring(SettleResponse))
                            }
                        }
                    } else {
                        panel.restState.intValue = target
                        val targetOffset = g.offsetFor(target, heightPx)
                        if (abs(targetOffset - currentOffset) < 0.5f) {
                            // COUIViewDragHelper.settleCapturedViewAt: nothing to settle at rest.
                            panel.dragOffset.snapTo(targetOffset)
                        } else {
                            // COUIBottomSheetBehavior.startSettleRunnable: COUISpringForce response 0.4,
                            // bounce 0, started at the release speed (mYVelocity); an overshoot past the
                            // resting state in the release direction is damped.
                            panel.dragOffset.animateTo(
                                targetValue = targetOffset,
                                animationSpec = BottomSheetSettleSpec(
                                    spring = panelSpring(SettleResponse),
                                    dampUpward = target != PanelState.Collapsed,
                                    dampDownward = target != PanelState.Expanded,
                                ),
                                initialVelocity = velocity,
                            ) {
                                updateDimAlpha(value)
                            }
                        }
                        updateDimAlpha(targetOffset)
                    }
                } catch (_: CancellationException) {
                    // Interrupted by a new gesture or by the sheet hiding.
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
                val current = panel.currentOffset()
                // An upward scroll first brings a lowered panel back up to its expanded position.
                if (delta < 0f && current > 0f) {
                    val newOffset = (current + delta).coerceAtLeast(0f)
                    val consumedY = current - newOffset
                    if (consumedY != 0f) {
                        lastNestedDelta.floatValue = delta
                        moveTo(newOffset)
                        return Offset(0f, -consumedY)
                    }
                }
                return Offset.Zero
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                if (!enableNestedScroll || !draggable || source != NestedScrollSource.UserInput) return Offset.Zero

                val delta = available.y
                if (delta <= 0f) return Offset.Zero
                cancelSettling()
                val g = currentGeometry
                val current = panel.currentOffset()
                if (current == panel.restOffset(g)) {
                    pressDownState.intValue = panel.restState.intValue
                }
                lastNestedDelta.floatValue = delta
                if (!allowDismiss) {
                    // A panel that cannot be hidden stops at its collapsed offset; the rest of the
                    // step goes back to the content.
                    val limit = g.collapsedOffset(g.panelHeightPx(panel.heightPx.intValue))
                    if (current + delta > limit) {
                        val newOffset = maxOf(current, limit)
                        moveTo(newOffset)
                        return Offset(0f, newOffset - current)
                    }
                }
                // A single scroll step beyond 100px moves the panel half as far.
                val step = if (delta > NestedFastStepPx) delta * 0.5f else delta
                val newOffset = calculateNewOffset(current, step)
                moveTo(newOffset)

                // Dismiss immediately if dragged beyond window height
                if (newOffset > g.windowHeightPx) {
                    performSettle(0f, true)
                }
                return available
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                if (!enableNestedScroll || isSettling.value) return Velocity.Zero

                // Take over fling if the sheet is away from its resting position
                if (panel.currentOffset() != panel.restOffset(currentGeometry)) {
                    performSettle(available.y, true)
                    return available
                }
                return Velocity.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                if (!enableNestedScroll || isSettling.value) return Velocity.Zero

                if (panel.currentOffset() != panel.restOffset(currentGeometry)) {
                    performSettle(available.y, true)
                    return available
                }
                return super.onPostFling(consumed, available)
            }
        }
    }

    // Whole-panel drag (COUIBottomSheetBehavior mGlobalDrag = true).
    val onDrag: (Float) -> Unit = remember(calculateNewOffset) {
        { delta -> moveTo(calculateNewOffset(panel.currentOffset(), delta)) }
    }
    val onDragStarted: () -> Unit = {
        cancelSettling()
        pressDownState.intValue = panel.restState.intValue
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
        panel = panel,
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

/**
 * Resolves where a released panel settles, following `COUIBottomSheetBehavior.onViewReleased`
 * (direct drag) and `onStopNestedScroll` (content drag). Velocities are in px/s, positive downward.
 * Regular panels and landscape handle panels fit their content (`fitToContents`): no half-expanded
 * state. A panel that cannot be hidden (`hideable = false`) rests collapsed or expanded instead.
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
    val collapsed = geometry.collapsedOffset(heightPx)
    val fitToContents = !geometry.handlePanel || geometry.landscape
    val half = geometry.halfOffset(heightPx)
    val shouldHide = when {
        !allowDismiss -> false

        // A regular panel skips the collapsed state (skipCollapsed): shouldHide() is always true.
        !geometry.handlePanel -> true

        else -> offset >= collapsed && abs(offset + velocity * 0.1f - collapsed) / geometry.peekPx > HideThreshold
    }
    val nearest = when {
        fitToContents -> if (abs(offset) < abs(offset - collapsed)) PanelState.Expanded else PanelState.Collapsed
        offset < half -> if (offset < abs(offset - collapsed)) PanelState.Expanded else PanelState.HalfExpanded
        abs(offset - half) < abs(offset - collapsed) -> PanelState.HalfExpanded
        else -> PanelState.Collapsed
    }
    val inBetween = offset > half && offset < collapsed
    val downward = if (
        !fitToContents &&
        !(pressDownState == PanelState.HalfExpanded && inBetween) &&
        abs(offset - half) < abs(offset - collapsed)
    ) {
        PanelState.HalfExpanded
    } else {
        PanelState.Collapsed
    }
    val upward = if (!fitToContents && offset > half) PanelState.HalfExpanded else PanelState.Expanded
    val target = if (nested) {
        when {
            lastNestedDelta < 0f -> upward
            shouldHide -> PanelState.Hidden
            lastNestedDelta == 0f -> nearest
            else -> downward
        }
    } else {
        when {
            velocity < 0f -> upward

            shouldHide -> when {
                velocity > HideVelocityThreshold || offset > travelPx / 2f -> PanelState.Hidden
                fitToContents || abs(offset) < abs(offset - half) -> PanelState.Expanded
                else -> PanelState.HalfExpanded
            }

            velocity == 0f -> nearest

            else -> downward
        }
    }
    // A panel no taller than its peek height has nothing to collapse.
    return if (target == PanelState.Collapsed && collapsed <= 0f) PanelState.Expanded else target
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
    panel: BottomSheetPanelState,
    nestedScrollConnection: NestedScrollConnection,
    onDrag: (Float) -> Unit,
    onDragStarted: () -> Unit,
    onDragStopped: (velocity: Float) -> Unit,
    modifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    materialStroke: Boolean = false,
    content: @Composable () -> Unit,
) {
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
            // The outer height includes the IME lift: it is how far the panel travels to leave the
            // window (COUIBottomSheetDialog.getTranslationDistance uses the screen bottom while the
            // keyboard accepts text).
            .onSizeChanged { size -> panel.heightPx.intValue = size.height }
            .then(if (defaultWindowInsetsPadding) Modifier.imePadding() else Modifier)
            .padding(horizontal = outsideMargin.width)
            .drawBehind {
                // Pull-up stretch: the panel is translated up by the pull distance and its
                // surface is extended below its bottom edge, so it reads as growing upward.
                val pullUp = -panel.dragOffset.value
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
 * Android `ViewConfiguration.getScaledMinimumFlingVelocity` (50dp/s): COUIViewDragHelper.clampMag
 * reports slower releases as still.
 */
private val MinFlingVelocity = 50.dp

/** COUIBottomSheetBehavior.onNestedPreScroll: a downward scroll step beyond this (px) moves the panel half as far. */
private val NestedFastStepPx = 100f

/** COUIBottomSheetBehavior.setShakeHandMovingDirection: release speed (px/s) that gives the settle a direction. */
private val ShakeHandVelocity = 100f

/** COUIBottomSheetBehavior.SHAKE_HAND_MOVING_FACTOR: share of a settle overshoot that is shown. */
private val ShakeHandFactor = 0.4f

/** Frames given to the caller to hide the sheet after a dismiss request before the panel goes back. */
private val DismissConfirmFrames = 2

/** Upper bound for the first layout of a shown panel; the slide then starts with the window height. */
private val SheetMeasureTimeoutMillis = 400L

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
    /** Height of the panel's parent (the COUI coordinator: the window below the top inset). */
    val containerPx: Float get() = (windowHeightPx - topInsetPx).coerceAtLeast(0f)

    /** Height of a panel laid out at the maximum height in the bottom-anchored form. */
    val maxHeightPx: Float get() = containerPx

    /** Height the gestures work with: a handle panel always fills the maximum height. */
    fun panelHeightPx(measuredPx: Int): Float = if (handlePanel) maxHeightPx else measuredPx.toFloat()

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

    /** COUIBottomSheetBehavior.calculateCollapsedOffset: only the peek height stays visible. */
    fun collapsedOffset(heightPx: Float): Float = (travelPx(heightPx) - peekPx).coerceAtLeast(0f)

    fun offsetFor(state: Int, heightPx: Float): Float = when (state) {
        PanelState.HalfExpanded -> halfOffset(heightPx)
        PanelState.Collapsed -> collapsedOffset(heightPx)
        else -> 0f
    }
}

/**
 * Motion state of one panel, shared by the show / hide effect, the gestures and the draw code. The
 * snapshot-backed fields are read in graphics layers and draw blocks; the plain fields are
 * bookkeeping that is never read during composition.
 */
private class BottomSheetPanelState {
    /** Slide progress of a bottom-anchored panel, scale progress of a floating one (0 hidden, 1 shown). */
    val progress = Animatable(0f, visibilityThreshold = 0.0001f)

    /** Alpha progress of a floating panel. */
    val fade = Animatable(0f, visibilityThreshold = 0.0001f)

    /** Offset below the expanded resting position, driven by drags, settles and the back gesture. */
    val dragOffset = Animatable(0f)

    /** COUIBottomSheetDialog.getTranslationDistance: how far the slide moves the panel from progress 0 to 1. */
    val slideTravel = mutableFloatStateOf(0f)

    /** Gesture-driven factor of the mask alpha (drag position, back gesture, drag-to-dismiss fade). */
    val dimAlpha = mutableFloatStateOf(1f)

    /** Set once a drag or fling hides the panel: the mask then fades on its own spring. */
    val maskFadingOut = mutableStateOf(false)

    /** Height of the laid-out panel, IME lift included; 0 until the first layout of a show. */
    val heightPx = mutableIntStateOf(0)

    val restState = mutableIntStateOf(PanelState.Expanded)

    /** Whether the panel is composed: from the start of a show until its hide animation ends. */
    val visible = mutableStateOf(false)

    /** The mask fade a drag-to-dismiss started. */
    var maskJob: Job? = null

    /** Gesture offset handed to the panel but not applied yet; NaN when there is none. */
    var pendingOffset = Float.NaN

    private var slideVelocity = 0f
    private var slideResponse = 0f
    private var fadeVelocity = 0f
    private var fadeResponse = 0f

    fun currentOffset(): Float = if (pendingOffset.isNaN()) dragOffset.value else pendingOffset

    fun restOffset(geometry: SheetGeometry): Float = geometry.offsetFor(restState.intValue, geometry.panelHeightPx(heightPx.intValue))

    suspend fun animateDimTo(target: Float, response: Float) {
        animate(dimAlpha.floatValue, target, animationSpec = panelSpring(response)) { value, _ ->
            dimAlpha.floatValue = value
        }
    }

    /** COUIBottomSheetDialog.dismissWithAlphaAnim: the mask fades from where the drag left it. */
    fun fadeOutMask(scope: CoroutineScope, response: Float) {
        maskJob?.cancel()
        maskFadingOut.value = true
        maskJob = scope.launch { animateDimTo(0f, response) }
    }

    suspend fun animateShow(geometry: SheetGeometry): Unit = coroutineScope {
        val wasVisible = visible.value
        maskJob?.cancel()
        maskFadingOut.value = false
        if (wasVisible) {
            if (dimAlpha.floatValue < 1f) launch { animateDimTo(1f, SettleResponse) }
        } else {
            progress.snapTo(0f)
            fade.snapTo(0f)
            dragOffset.snapTo(0f)
            dimAlpha.floatValue = 1f
            slideVelocity = 0f
            fadeVelocity = 0f
            pendingOffset = Float.NaN
            // Parks a bottom-anchored panel below the window until its first layout reports its height.
            slideTravel.floatValue = geometry.windowHeightPx
        }
        visible.value = true
        if (geometry.centered) {
            // COUIBottomSheetDialog.isFadeInCenter: scale 0.8 -> 1 and alpha 0 -> 1, response 0.25.
            restState.intValue = PanelState.Expanded
            if (dragOffset.value != 0f) launch { dragOffset.animateTo(0f, panelSpring(CenterShowResponse)) }
            launch { animateFade(1f, CenterShowResponse) }
            animateProgress(1f, CenterShowResponse)
        } else {
            fade.snapTo(1f)
            // COUIBottomSheetDialog.initBehavior: a handle panel opens half-expanded in portrait
            // and collapsed in landscape; every other panel opens expanded.
            val initialState = when {
                !geometry.handlePanel -> PanelState.Expanded
                geometry.landscape -> PanelState.Collapsed
                else -> PanelState.HalfExpanded
            }
            // COUIBottomSheetDialog starts the slide on the first pre-draw, once the panel is measured.
            val measured = if (geometry.handlePanel) geometry.maxHeightPx else awaitHeight()
            val initialOffset = geometry.offsetFor(initialState, measured)
            val travel = if (measured > 0f) (measured - initialOffset).coerceAtLeast(0f) else geometry.windowHeightPx
            restState.intValue = initialState
            rebaseSlide(travel = travel, offset = initialOffset)
            animateProgress(1f, couiPanelResponse(entering = true, travelPx = travel, geometry = geometry))
        }
    }

    suspend fun animateHide(geometry: SheetGeometry) {
        if (geometry.centered) {
            // Exit: alpha response 0.18, scale response 0.25.
            coroutineScope {
                launch { animateFade(0f, CenterHideAlphaResponse) }
                animateProgress(0f, CenterShowResponse)
            }
        } else {
            // Freeze a settle in flight so the slide covers exactly what is still on screen; a panel
            // already dragged off the window keeps still while the mask fades.
            dragOffset.stop()
            val measured = geometry.panelHeightPx(heightPx.intValue)
            val offset = dragOffset.value
            val travel = if (measured > 0f) (measured - offset).coerceAtLeast(0f) else geometry.windowHeightPx
            rebaseSlide(travel = travel, offset = offset)
            animateProgress(0f, couiPanelResponse(entering = false, travelPx = travel, geometry = geometry))
        }
    }

    /** COUIBottomSheetDialog.reversalAnimation: a slide turned around mid-flight keeps its spring and speed. */
    private suspend fun animateProgress(target: Float, freshResponse: Float) {
        val response = if (slideVelocity != 0f) slideResponse else freshResponse
        slideResponse = response
        progress.animateTo(target, panelSpring(response), initialVelocity = slideVelocity) {
            slideVelocity = velocity
        }
        slideVelocity = 0f
    }

    private suspend fun animateFade(target: Float, freshResponse: Float) {
        val response = if (fadeVelocity != 0f) fadeResponse else freshResponse
        fadeResponse = response
        fade.animateTo(target, panelSpring(response), initialVelocity = fadeVelocity) {
            fadeVelocity = velocity
        }
        fadeVelocity = 0f
    }

    /**
     * Re-expresses where the panel is drawn for a new slide [travel] and resting [offset], so a
     * retargeted slide starts from the panel's current position at the same speed.
     */
    private suspend fun rebaseSlide(travel: Float, offset: Float) {
        val oldTravel = slideTravel.floatValue
        val shift = oldTravel * (1f - progress.value) + dragOffset.value - offset
        val start = if (travel > 0f) (1f - shift / travel).coerceIn(0f, 1f) else progress.value
        slideVelocity = if (oldTravel > 0f && travel > 0f) slideVelocity * oldTravel / travel else 0f
        slideTravel.floatValue = travel
        dragOffset.snapTo(offset)
        progress.snapTo(start)
    }

    private suspend fun awaitHeight(): Float {
        withTimeoutOrNull(SheetMeasureTimeoutMillis) {
            snapshotFlow { heightPx.intValue }.first { it > 0 }
        }
        return heightPx.intValue.toFloat()
    }
}

/**
 * Settle spring of a released panel. COUIBottomSheetBehavior.panelHeightVerticalMoving shows the part
 * of the spring that overshoots the resting state in the release direction at [ShakeHandFactor]:
 * upward past an expanded or half-expanded rest, downward past a collapsed or half-expanded one.
 */
private class BottomSheetSettleSpec(
    private val spring: SpringSpec<Float>,
    private val dampUpward: Boolean,
    private val dampDownward: Boolean,
) : FiniteAnimationSpec<Float> {
    override fun <V : AnimationVector> vectorize(converter: TwoWayConverter<Float, V>): VectorizedFiniteAnimationSpec<V> = BottomSheetSettleVectorizedSpec(spring.vectorize(converter), dampUpward, dampDownward)
}

private class BottomSheetSettleVectorizedSpec<V : AnimationVector>(
    private val spring: VectorizedFiniteAnimationSpec<V>,
    private val dampUpward: Boolean,
    private val dampDownward: Boolean,
) : VectorizedFiniteAnimationSpec<V> {
    override fun getValueFromNanos(playTimeNanos: Long, initialValue: V, targetValue: V, initialVelocity: V): V {
        val raw = spring.getValueFromNanos(playTimeNanos, initialValue, targetValue, initialVelocity).firstValue()
        val factor = overshootFactor(raw, initialValue, targetValue, initialVelocity)
        val target = targetValue.firstValue()
        return vectorOf(if (factor == 1f) raw else target + (raw - target) * factor)
    }

    override fun getVelocityFromNanos(playTimeNanos: Long, initialValue: V, targetValue: V, initialVelocity: V): V {
        val velocity = spring.getVelocityFromNanos(playTimeNanos, initialValue, targetValue, initialVelocity).firstValue()
        val raw = spring.getValueFromNanos(playTimeNanos, initialValue, targetValue, initialVelocity).firstValue()
        return vectorOf(velocity * overshootFactor(raw, initialValue, targetValue, initialVelocity))
    }

    override fun getDurationNanos(initialValue: V, targetValue: V, initialVelocity: V): Long = spring.getDurationNanos(initialValue, targetValue, initialVelocity)

    private fun overshootFactor(value: Float, initialValue: V, targetValue: V, initialVelocity: V): Float {
        val start = initialValue.firstValue()
        val target = targetValue.firstValue()
        val releaseVelocity = initialVelocity.firstValue()
        val upward = dampUpward && releaseVelocity < -ShakeHandVelocity && start > target && value < target
        val downward = dampDownward && releaseVelocity > ShakeHandVelocity && start < target && value > target
        return if (upward || downward) ShakeHandFactor else 1f
    }
}

private fun AnimationVector.firstValue(): Float = (this as AnimationVector1D).value

@Suppress("UNCHECKED_CAST")
private fun <V : AnimationVector> vectorOf(value: Float): V = AnimationVector1D(value) as V

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
 * COUIBottomSheetDialog.getTranslationResponse: bounce-0 slide response interpolated between 0.25s
 * and 0.45s (enter) / 0.18s and 0.37s (exit) by the travel fraction
 * `(travel - peek) / (coordinator height - peek)`.
 */
private fun couiPanelResponse(entering: Boolean, travelPx: Float, geometry: SheetGeometry): Float {
    val start = if (entering) 0.25f else 0.18f
    val end = if (entering) 0.45f else 0.37f
    val denominator = geometry.containerPx - geometry.peekPx
    val fraction = if (denominator > 0f) ((travelPx - geometry.peekPx) / denominator).coerceIn(0f, 1f) else 1f
    return start + (end - start) * fraction
}

/**
 * Slides a released panel off the window (COUIBottomSheetBehavior.startSettleRunnable to
 * STATE_HIDDEN: response `0.18 + 0.19 * visible / parent height`, bounce 0, started at the release
 * speed) and returns as soon as the panel has left the window.
 */
private suspend fun slidePanelOffWindow(
    dragOffsetY: Animatable<Float, AnimationVector1D>,
    travelPx: Float,
    targetPx: Float,
    containerPx: Float,
    velocity: Float,
) {
    coroutineScope {
        val visible = (travelPx - dragOffsetY.value).coerceAtLeast(0f)
        val fraction = if (containerPx > 0f) (visible / containerPx).coerceIn(0f, 1f) else 0f
        val slide = launch {
            dragOffsetY.animateTo(
                targetValue = targetPx,
                animationSpec = panelSpring(0.18f + 0.19f * fraction),
                initialVelocity = velocity,
            )
        }
        snapshotFlow { dragOffsetY.value }.first { it >= travelPx }
        slide.cancel()
    }
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
 * (coui_panel_drag_view_shadow_margin_top 4dp + coui_panel_drag_view_height 12dp, 70dp wide) holding
 * the 36x4dp capsule 8dp below its top, and a 70x20dp press background (`tv_drag_press_bg`) aligned
 * to its top that fades in while the handle is touched. The drag layout clips the press background
 * to its 12dp height, and only a touch inside it lights it (COUIPanelContentLayout.setDragViewPressAnim).
 * The strip does not consume touches, so the whole-panel drag still runs from it.
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
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val halfWidth = DragHandlePressWidth.toPx() / 2f
                    val centerX = size.width / 2f
                    val top = DragHandleMarginTop.toPx()
                    val onHandle = down.position.x >= centerX - halfWidth &&
                        down.position.x <= centerX + halfWidth &&
                        down.position.y >= top &&
                        down.position.y <= top + DragHandleLayoutHeight.toPx()
                    if (!onHandle) return@awaitEachGesture
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
                val pressLeft = (size.width - pressWidth) / 2f
                val pressValue = pressAlpha.value
                if (pressValue > 0f) {
                    val pressRadius = DragHandlePressCornerRadius.toPx()
                    clipRect(
                        left = pressLeft,
                        top = top,
                        right = pressLeft + pressWidth,
                        bottom = top + DragHandleLayoutHeight.toPx(),
                    ) {
                        drawRoundRect(
                            color = pressColor.copy(alpha = pressColor.alpha * pressValue),
                            topLeft = Offset(pressLeft, top),
                            size = Size(pressWidth, DragHandlePressHeight.toPx()),
                            cornerRadius = CornerRadius(pressRadius, pressRadius),
                        )
                    }
                }
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

/** COUIToolbar `supportTitlePaddingTop` / `supportTitlePaddingBottom` of Widget.COUI.Toolbar.Panel. */
private val TitlePaddingVertical = 12.dp

/** COUI `coui_panel_drag_view_shadow_margin_top`. */
private val DragHandleMarginTop = 4.dp

/** COUI `coui_panel_drag_view_height`: the drag layout, which clips the press background. */
private val DragHandleLayoutHeight = 12.dp

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
    // toolbar_normal_menu_padding_left / _right 8dp / 11dp, buttons centered vertically. The title
    // (COUIToolbar.ensureTitleTextView) is one ellipsized line of couiTextHeadlineS, padded 12dp
    // above and below, centered and kept clear of both buttons.
    Layout(
        content = {
            Box { startAction?.invoke() }
            Box { endAction?.invoke() }
            Box {
                if (title != null) {
                    Text(
                        text = title,
                        modifier = Modifier.padding(vertical = TitlePaddingVertical),
                        color = COUITheme.colorScheme.labelPrimary,
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = COUITheme.textStyles.headlineS,
                    )
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = TitleRowMinHeight)
            .padding(start = TitleRowPaddingStart, end = TitleRowPaddingEnd),
    ) { measurables, constraints ->
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val startPlaceable = measurables[0].measure(loose)
        val endPlaceable = measurables[1].measure(loose)
        val width = if (constraints.hasBoundedWidth) constraints.maxWidth else startPlaceable.width + endPlaceable.width
        val side = maxOf(startPlaceable.width, endPlaceable.width)
        val titlePlaceable = measurables[2].measure(loose.copy(maxWidth = (width - 2 * side).coerceAtLeast(0)))
        val height = maxOf(constraints.minHeight, startPlaceable.height, endPlaceable.height, titlePlaceable.height)
        layout(width, height) {
            startPlaceable.placeRelative(0, (height - startPlaceable.height) / 2)
            endPlaceable.placeRelative(width - endPlaceable.width, (height - endPlaceable.height) / 2)
            titlePlaceable.placeRelative((width - titlePlaceable.width) / 2, (height - titlePlaceable.height) / 2)
        }
    }
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
