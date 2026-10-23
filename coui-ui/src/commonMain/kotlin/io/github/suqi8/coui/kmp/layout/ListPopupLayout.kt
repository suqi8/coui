// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.layout

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.NavigationEventTransitionState
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import io.github.suqi8.coui.kmp.basic.ListPopupContent
import io.github.suqi8.coui.kmp.basic.ListPopupDefaults
import io.github.suqi8.coui.kmp.basic.PopupGravityPath
import io.github.suqi8.coui.kmp.basic.PopupPositionProvider
import io.github.suqi8.coui.kmp.basic.PreciseClickState
import io.github.suqi8.coui.kmp.basic.popupGravityPathFor
import io.github.suqi8.coui.kmp.basic.rememberListPopupLayoutInfo
import io.github.suqi8.coui.kmp.effect.LocalMaterialBackdrop
import io.github.suqi8.coui.kmp.overlay.OverlayListPopup
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.LocalDismissState
import io.github.suqi8.coui.kmp.window.WindowListPopup
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

/**
 * Internal shared layout logic for [OverlayListPopup] and [WindowListPopup].
 *
 * @param show Whether the popup is currently shown.
 * @param popupHost A composable that provides the popup container (e.g., PopupLayout or Dialog).
 *   It receives an internal "mounted" state (true from the moment the enter animation starts
 *   until the exit animation finishes) and the inner content composable. Hosts that
 *   conditionally render their container (e.g., a `Dialog`) should respect this flag and
 *   should NOT animate enter/exit themselves — entry/exit transitions are driven internally.
 * @param popupModifier The modifier to be applied to the popup content area.
 * @param popupPositionProvider The [PopupPositionProvider] for positioning.
 * @param alignment The alignment of the popup.
 * @param enableWindowDim Whether to render and animate the window-dim layer. The outside-tap
 *   dismiss gesture is preserved regardless of this flag.
 * @param onDismissRequest The callback when the popup is dismissed.
 * @param onDismissFinished Invoked when the hide animation completes; not invoked if the hide
 *   is cancelled mid-flight (e.g., by [show] toggling back to true).
 * @param maxHeight The maximum height of the popup.
 * @param minWidth The minimum width of the popup.
 * @param preciseClickState When non-null and carrying a recorded touch point, the anchor is
 *   collapsed to that point so the popup opens at the finger (COUI `PreciseClickHelper` plus
 *   `PopupMenuLocateHelper.setAnchor`). A null state, or one with no point recorded, keeps the
 *   anchor-centred placement.
 * @param backgroundColor The popup surface fill (couiColorBackgroundTop). Pass a transparent color
 *   alongside a blurring [backgroundModifier].
 * @param backgroundModifier A [Modifier] applied inside the popup surface clip, under the rows: the
 *   slot for the COUI 17 blurred background, e.g. coui-blur's
 *   `Modifier.couiMaterialBlur(backdrop, MaterialBlurDefaults.popup(isDark))`. COUI leaves popup blur
 *   off unless an app asks for it (`COUIPopupListWindow.setUseBackgroundBlur`).
 * @param materialEffect Whether to draw the material effects of a blurred COUI 17 popup: the
 *   shadow-edge stroke and the touch spotlight (`RoundFrameLayout.initMaterialEffects`).
 * @param content The content of the popup.
 */
@Composable
internal fun ListPopupLayout(
    show: Boolean,
    popupHost: @Composable (visible: Boolean, content: @Composable () -> Unit) -> Unit,
    popupModifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    popupPositionProvider: PopupPositionProvider = ListPopupDefaults.DropdownPositionProvider,
    alignment: PopupPositionProvider.Align = PopupPositionProvider.Align.Start,
    enableWindowDim: Boolean = true,
    onDismissRequest: (() -> Unit)? = null,
    onDismissFinished: (() -> Unit)? = null,
    maxHeight: Dp? = null,
    minWidth: Dp = ListPopupDefaults.MinWidth,
    preciseClickState: PreciseClickState? = null,
    backgroundColor: Color = ListPopupDefaults.backgroundColor(),
    materialEffect: Boolean = false,
    content: @Composable () -> Unit,
) {
    // COUI 17 PopupMotionAnimateKitController drives three waves: the path progress along the
    // gravity Bezier, the scale and the opacity, each on its own spring.
    val pathProgress = remember { Animatable(0f) }
    val scaleProgress = remember { Animatable(0f) }
    val alphaProgress = remember { Animatable(0f) }
    val dimProgress = remember { Animatable(0f) }
    val backProgress = remember { Animatable(0f) }
    val currentOnDismiss by rememberUpdatedState(onDismissRequest)
    val currentOnDismissFinished by rememberUpdatedState(onDismissFinished)
    val coroutineScope = rememberCoroutineScope()
    val internalVisible = remember { mutableStateOf(false) }
    var popupContentSize by remember { mutableStateOf(IntSize.Zero) }
    var hostPositionInWindow by remember { mutableStateOf(Offset.Zero) }

    LaunchedEffect(show) {
        if (show) {
            internalVisible.value = true
            backProgress.snapTo(0f)
            if (enableWindowDim) {
                launch { dimProgress.animateTo(1f, ListPopupDefaults.DimEnterAnimationSpec) }
            }
            // The path needs the measured menu, as COUI configures it from the located domain.
            snapshotFlow { popupContentSize }.first { it != IntSize.Zero }
            launch { pathProgress.animateTo(1f, ListPopupDefaults.PathEnterAnimationSpec) }
            launch { scaleProgress.animateTo(1f, ListPopupDefaults.ScaleEnterAnimationSpec) }
            launch { alphaProgress.animateTo(1f, ListPopupDefaults.OpacityEnterAnimationSpec) }
        } else {
            if (!internalVisible.value) return@LaunchedEffect
            if (enableWindowDim) {
                launch { dimProgress.animateTo(0f, ListPopupDefaults.DimExitAnimationSpec) }
            }
            // COUI WaveEndAggregator only reports the dismissal once every wave has ended.
            listOf(
                launch { pathProgress.animateTo(0f, ListPopupDefaults.PathExitAnimationSpec) },
                launch { scaleProgress.animateTo(0f, ListPopupDefaults.ScaleExitAnimationSpec) },
                launch { alphaProgress.animateTo(0f, ListPopupDefaults.OpacityExitAnimationSpec) },
            ).joinAll()
            // Force-settle remaining tracks so the next enter starts from a clean zero baseline.
            pathProgress.snapTo(0f)
            scaleProgress.snapTo(0f)
            alphaProgress.snapTo(0f)
            dimProgress.snapTo(0f)
            backProgress.snapTo(0f)
            internalVisible.value = false
            currentOnDismissFinished?.invoke()
        }
    }

    if (!show && !internalVisible.value) return

    var parentBounds by remember { mutableStateOf(IntRect.Zero) }

    Spacer(
        modifier = Modifier
            .onGloballyPositioned { childCoordinates ->
                childCoordinates.parentLayoutCoordinates?.let { parentLayoutCoordinates ->
                    val positionInWindow = parentLayoutCoordinates.positionInWindow()
                    parentBounds = IntRect(
                        left = positionInWindow.x.toInt(),
                        top = positionInWindow.y.toInt(),
                        right = positionInWindow.x.toInt() + parentLayoutCoordinates.size.width,
                        bottom = positionInWindow.y.toInt() + parentLayoutCoordinates.size.height,
                    )
                }
            },
    )

    if (parentBounds == IntRect.Zero) return

    // COUI PopupMenuLocateHelper.setAnchor collapses the anchor to the recorded touch point, so both
    // the horizontal centring and the below/above test run against that point. The point is already
    // in window coordinates, the same space parentBounds uses.
    val touchPoint = preciseClickState?.touchPoint ?: Offset.Unspecified
    val anchorBounds = if (touchPoint.isSpecified) {
        val x = touchPoint.x.toInt()
        val y = touchPoint.y.toInt()
        IntRect(left = x, top = y, right = x, bottom = y)
    } else {
        parentBounds
    }

    val layoutInfo = rememberListPopupLayoutInfo(
        alignment = alignment,
        popupPositionProvider = popupPositionProvider,
        parentBounds = anchorBounds,
        popupContentSize = popupContentSize,
    )

    // COUI PopupMenuDomain.computeMainMenuPopupPathAnchorOffset: the menu centre starts on the
    // anchor's edge and travels the gravity Bezier to its resting place.
    val layoutDirection = LocalLayoutDirection.current
    val density = LocalDensity.current
    val gravityPath = remember(anchorBounds, popupContentSize, layoutInfo, popupPositionProvider, alignment, layoutDirection, density) {
        if (popupContentSize == IntSize.Zero) {
            PopupGravityPath.None
        } else {
            val menuOffset = popupPositionProvider.calculatePosition(
                anchorBounds,
                layoutInfo.windowBounds,
                layoutDirection,
                popupContentSize,
                layoutInfo.popupMargin,
                alignment,
            )
            popupGravityPathFor(anchorBounds, menuOffset, popupContentSize, density.density)
        }
    }

    val requestDismiss: () -> Unit = remember {
        { currentOnDismiss?.invoke() }
    }

    popupHost(internalVisible.value) {
        val navigationEventState = rememberNavigationEventState(currentInfo = NavigationEventInfo.None)
        NavigationBackHandler(
            state = navigationEventState,
            isBackEnabled = show,
            onBackCancelled = {
                coroutineScope.launch {
                    backProgress.animateTo(0f, ListPopupDefaults.ResetAnimationSpec)
                }
            },
            onBackCompleted = { requestDismiss() },
        )

        LaunchedEffect(Unit) {
            // Collect inside a single coroutine so the per-frame `transitionState` ticks during a
            // back gesture do not cancel/relaunch the LaunchedEffect on every progress update.
            snapshotFlow { navigationEventState.transitionState }
                .collect { transitionState ->
                    if (
                        transitionState is NavigationEventTransitionState.InProgress &&
                        transitionState.direction == NavigationEventTransitionState.TRANSITIONING_BACK
                    ) {
                        backProgress.snapTo(transitionState.latestEvent.progress)
                    }
                }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            if (enableWindowDim) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = dimProgress.value * (1f - backProgress.value)
                        }
                        .background(COUITheme.colorScheme.windowDimming),
                )
            }
            Box(
                modifier = popupModifier
                    .fillMaxSize()
                    .onGloballyPositioned { coordinates ->
                        hostPositionInWindow = coordinates.positionInWindow()
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { requestDismiss() },
                        )
                    }
                    .layout { measurable, constraints ->
                        val windowBounds = layoutInfo.windowBounds
                        val popupMargin = layoutInfo.popupMargin
                        val minHeightPx = ListPopupDefaults.MinPopupHeight.roundToPx()
                        // COUI PopupMenuLocateHelper.prepareShowMainMenu caps the menu at the
                        // available rect (the window minus every barrier, which windowBounds
                        // carries); ListPopupColumn then shows only the rows that fit whole.
                        val availableHeight = windowBounds.height.coerceAtLeast(minHeightPx)
                        val availableWidth = (windowBounds.width - popupMargin.left - popupMargin.right)
                            .coerceAtLeast(0)
                            .coerceAtMost(constraints.maxWidth)
                        val placeable = measurable.measure(
                            constraints.copy(
                                maxHeight = maxHeight?.roundToPx()?.coerceIn(minHeightPx, availableHeight)
                                    ?: availableHeight,
                                minHeight = 0,
                                maxWidth = availableWidth,
                                minWidth = minWidth.roundToPx().coerceAtMost(availableWidth),
                            ),
                        )
                        val measuredSize = IntSize(placeable.width, placeable.height)

                        val calculatedOffset = popupPositionProvider.calculatePosition(
                            anchorBounds,
                            windowBounds,
                            layoutDirection,
                            measuredSize,
                            popupMargin,
                            alignment,
                        )

                        val adjustedOffset = IntOffset(
                            x = calculatedOffset.x - hostPositionInWindow.x.toInt(),
                            y = calculatedOffset.y - hostPositionInWindow.y.toInt(),
                        )

                        layout(constraints.maxWidth, constraints.maxHeight) {
                            placeable.place(adjustedOffset)
                        }
                    },
            ) {
                ListPopupContent(
                    popupContentSize = popupContentSize,
                    onPopupContentSizeChange = { popupContentSize = it },
                    fractionProgress = { scaleProgress.value * (1f - backProgress.value) },
                    alphaProgress = { alphaProgress.value * (1f - backProgress.value) },
                    popupLayoutPosition = layoutInfo.popupLayoutPosition,
                    localTransformOrigin = layoutInfo.localTransformOrigin,
                    translation = { gravityPath.pointAt(pathProgress.value * (1f - backProgress.value)) },
                    backgroundColor = backgroundColor,
                    backgroundModifier = backgroundModifier,
                    materialEffect = materialEffect,
                    content = {
                        CompositionLocalProvider(
                            LocalDismissState provides requestDismiss,
                            LocalMaterialBackdrop provides null,
                        ) {
                            content()
                        }
                    },
                )
            }
        }
    }
}
