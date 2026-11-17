// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.layout

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.NavigationEventTransitionState
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import io.github.suqi8.coui.kmp.basic.CardListPosition
import io.github.suqi8.coui.kmp.basic.DropdownColors
import io.github.suqi8.coui.kmp.basic.DropdownEntry
import io.github.suqi8.coui.kmp.basic.DropdownGroupDivider
import io.github.suqi8.coui.kmp.basic.DropdownHeaderItem
import io.github.suqi8.coui.kmp.basic.DropdownHorizontalItems
import io.github.suqi8.coui.kmp.basic.DropdownItem
import io.github.suqi8.coui.kmp.basic.ListPopupColumn
import io.github.suqi8.coui.kmp.basic.ListPopupDefaults
import io.github.suqi8.coui.kmp.basic.LocalCardListPosition
import io.github.suqi8.coui.kmp.basic.LocalPopupGestureRelay
import io.github.suqi8.coui.kmp.basic.LocalPopupListRole
import io.github.suqi8.coui.kmp.basic.PopupCornerRadius
import io.github.suqi8.coui.kmp.basic.PopupCornerWeight
import io.github.suqi8.coui.kmp.basic.PopupDropdownRow
import io.github.suqi8.coui.kmp.basic.PopupGestureRelay
import io.github.suqi8.coui.kmp.basic.PopupGravityPath
import io.github.suqi8.coui.kmp.basic.PopupListRole
import io.github.suqi8.coui.kmp.basic.PopupListVerticalPadding
import io.github.suqi8.coui.kmp.basic.PopupPositionProvider
import io.github.suqi8.coui.kmp.basic.PopupRelayTarget
import io.github.suqi8.coui.kmp.basic.PopupSpotLight
import io.github.suqi8.coui.kmp.basic.consumePopupTouches
import io.github.suqi8.coui.kmp.basic.popupDropShadow
import io.github.suqi8.coui.kmp.basic.popupGravityPathFor
import io.github.suqi8.coui.kmp.basic.popupMaterialEffect
import io.github.suqi8.coui.kmp.basic.popupPanelLightInput
import io.github.suqi8.coui.kmp.basic.rememberListPopupLayoutInfo
import io.github.suqi8.coui.kmp.basic.rememberPopupPanelLight
import io.github.suqi8.coui.kmp.basic.rememberPopupSurface
import io.github.suqi8.coui.kmp.effect.LocalCOUIMaterialEffectEnabled
import io.github.suqi8.coui.kmp.effect.LocalMaterialBackdrop
import io.github.suqi8.coui.kmp.effect.LocalPopupMaterialBlur
import io.github.suqi8.coui.kmp.effect.TouchMotionState
import io.github.suqi8.coui.kmp.effect.couiTouchMotion
import io.github.suqi8.coui.kmp.squircle.addSquircleRect
import io.github.suqi8.coui.kmp.squircle.isSquircleEnabled
import io.github.suqi8.coui.kmp.squircle.squircleSurface
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.LocalDismissState
import io.github.suqi8.coui.kmp.utils.COUILayoutGrid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.min

/** The cascading menus share the RoundFrameLayout container corner ([PopupCornerRadius], 28dp). */
internal val CascadingPopupCornerRadius = PopupCornerRadius

/**
 * Vertical overlap between the anchor row's top edge and the sub menu's top edge on a compact
 * window (COUI coui_popup_list_window_vertical_overlap_between_main_and_sub_menu).
 */
internal val MainSubMenuVerticalOverlap = 14.dp

/**
 * Horizontal overlap between the main and the sub menu on a larger window
 * (COUI coui_popup_list_window_horizontal_overlap_between_main_and_sub_menu).
 */
internal val MainSubMenuHorizontalOverlap = 14.dp

/**
 * Minimum gap kept between the main menu top and the sub menu top, also taken off the sub menu's
 * maximum height (COUI coui_popup_list_window_min_gap_to_top).
 */
internal val SubMenuMinGapToTop = 12.dp

/** How far the main menu shrinks while a sub menu is open (`PopupMenuLocateHelper.MAIN_MENU_SHRINK_RATIO`). */
private const val MAIN_MENU_SHRINK_RATIO = 0.9f

/** Main menu list alpha under a sub menu on a compact window (`SmallScreenAnimationController.DEFAULT_MAIN_MENU_ALPHA_PERCENT`). */
private const val COMPACT_MAIN_CONTENT_ALPHA = 0.3f

/** Main menu list alpha under a sub menu on a larger window (`DefaultScreenAnimationController.DEFAULT_MAIN_MENU_ALPHA_PERCENT`). */
private const val DEFAULT_MAIN_CONTENT_ALPHA = 0.7f

/** How long a drag inside the main menu waits before closing the sub menu (`COUIPopupMenuRootView.MAIN_AREA_DISMISS_DELAY_MS`). */
private const val MAIN_AREA_DISMISS_DELAY_MILLIS = 500L

/** A blurred sub menu below this alpha is hidden (`BasePopupMenuAnimationController.SUB_MENU_BLUR_VISIBILITY_MIN_ALPHA`). */
private const val SUB_MENU_BLUR_VISIBILITY_MIN_ALPHA = 0.1f

/**
 * The sub menu transition (`MenuTransitionUnit` / `SubMenuTransitionUnit`:
 * `COUISpringForce` bounce 0, response 0.35, in both directions).
 */
private val SubMenuSpring: SpringSpec<Float> = spring(
    dampingRatio = 1f,
    stiffness = (2f * PI.toFloat() / 0.35f).let { it * it },
    visibilityThreshold = 0.0005f,
)

/**
 * Where a COUI 17 sub menu goes and how the main menu yields to it, resolved once both menus are
 * measured (`PopupMenuLocateHelper` main-menu relocate + sub-menu locate rules, and the two
 * animation controllers). All rects are in window pixels.
 *
 * @property compact Whether the window is compact (`SmallScreenAnimationController`): the sub menu
 *   covers the main menu; otherwise it opens beside it (`DefaultScreenAnimationController`).
 * @property subRect The resting rect of the sub menu.
 * @property mainPivot The pivot the main menu shrinks about, as fractions of its size.
 * @property mainScale The main menu scale once the sub menu is open.
 * @property mainTranslationY The main menu's vertical shift once the sub menu is open.
 * @property mainContentAlpha The main menu list alpha once the sub menu is open.
 * @property subStartTranslationY The compact sub menu's initial shift, which puts its header on the
 *   anchor row.
 * @property clipTopStart The compact sub menu's initial clip top (its list inset).
 * @property clipHeightStart The compact sub menu's initial clip height (the anchor row).
 * @property subPivot The pivot of the larger window's sub menu scale, as fractions of its size.
 * @property anchorDepth How far the anchor row's top sits below the main menu's top (compact).
 */
@Immutable
internal data class SubmenuGeometry(
    val compact: Boolean,
    val subRect: IntRect,
    val mainPivot: TransformOrigin,
    val mainScale: Float,
    val mainTranslationY: Float,
    val mainContentAlpha: Float,
    val subStartTranslationY: Float,
    val clipTopStart: Float,
    val clipHeightStart: Float,
    val subPivot: TransformOrigin,
    val anchorDepth: Float = 0f,
) {
    /**
     * The compact sub menu's translation start while it closes at [progress]
     * (`SmallScreenAnimationController.onSubMenuProgressUpdate`, run while the progress falls): the
     * anchor row is re-read where it is drawn this frame — the main menu scaled about its top by the
     * `MenuTransitionUnit` at the same progress, its translation removed — so the band closes onto
     * the row's current place instead of its unscaled one.
     */
    fun collapseStartTranslationY(progress: Float): Float {
        val mainScaleNow = 1f + (mainScale - 1f) * progress.coerceIn(0f, 1f)
        return subStartTranslationY - (1f - mainScaleNow) * anchorDepth
    }
}

/**
 * Resolves the [SubmenuGeometry] of a sub menu of [subSize] opened from [anchorRow] in a main menu
 * at [main], whose own anchor is [popupAnchor], inside [available].
 */
internal fun computeSubmenuGeometry(
    main: IntRect,
    anchorRow: IntRect,
    popupAnchor: IntRect,
    subSize: IntSize,
    available: IntRect,
    compact: Boolean,
    layoutDirection: LayoutDirection,
    verticalOverlap: Int,
    horizontalOverlap: Int,
    minGap: Int,
    listInset: Int,
): SubmenuGeometry {
    val relocatedWidth = (main.width * MAIN_MENU_SHRINK_RATIO).toInt()
    val relocatedHeight = (main.height * MAIN_MENU_SHRINK_RATIO).toInt()
    if (compact) {
        // mMainMenuRelocateRule, small screen: shrink towards the side the anchor sits on.
        val offsetX = when {
            popupAnchor.center.x < main.center.x - 1 -> 0
            popupAnchor.center.x > main.center.x + 1 -> main.width - relocatedWidth
            else -> (main.width - relocatedWidth) / 2
        }
        val offsetY = if (main.top + minGap + subSize.height < available.bottom) {
            0
        } else {
            available.bottom - subSize.height - minGap - main.top
        }
        val relocatedLeft = main.left + offsetX
        val relocatedTop = main.top + offsetY
        // getSubMenuAnchorTopAfterMainMenuRelocated.
        val ratio = if (main.height > 0) relocatedHeight.toFloat() / main.height else 1f
        val anchorTop = (relocatedTop + ratio * (anchorRow.top - main.top)).toInt()
        val subTop = if (anchorTop - verticalOverlap + subSize.height < available.bottom) {
            anchorTop - verticalOverlap
        } else {
            available.bottom - subSize.height
        }
        val subRect = IntRect(main.left, subTop, main.left + subSize.width, subTop + subSize.height)
        val pivotX = when {
            main.left == relocatedLeft -> 0f
            main.right == relocatedLeft + relocatedWidth -> 1f
            else -> 0.5f
        }
        // computeMainMenuTargetTranslationY, clamped to the available top.
        val translationY = if (main.top + minGap <= subTop) 0f else (subTop - minGap - main.top).toFloat()
        return SubmenuGeometry(
            compact = true,
            subRect = subRect,
            mainPivot = TransformOrigin(pivotX, 0f),
            mainScale = relocatedWidth.toFloat() / main.width.coerceAtLeast(1),
            mainTranslationY = max((available.top - main.top).toFloat(), translationY),
            mainContentAlpha = COMPACT_MAIN_CONTENT_ALPHA,
            subStartTranslationY = (anchorRow.top - subTop - listInset).toFloat(),
            clipTopStart = listInset.toFloat(),
            clipHeightStart = min(anchorRow.height, main.bottom - anchorRow.top).toFloat(),
            subPivot = TransformOrigin.Center,
            anchorDepth = (anchorRow.top - main.top).toFloat(),
        )
    }
    // Larger windows: the main menu shrinks about its centre and the sub menu opens beside it.
    val relocatedLeft = main.center.x - relocatedWidth / 2
    val relocatedRight = relocatedLeft + relocatedWidth
    val subLeft = if (layoutDirection == LayoutDirection.Rtl) {
        if (relocatedRight - horizontalOverlap + subSize.width < available.right) {
            relocatedRight - horizontalOverlap
        } else {
            relocatedLeft + horizontalOverlap - subSize.width
        }
    } else if (relocatedLeft + horizontalOverlap - subSize.width > available.left) {
        relocatedLeft + horizontalOverlap - subSize.width
    } else {
        relocatedRight - horizontalOverlap
    }
    val subTop = if (anchorRow.top + subSize.height < available.bottom) {
        anchorRow.top
    } else {
        available.bottom - subSize.height
    }
    val subRect = IntRect(subLeft, subTop, subLeft + subSize.width, subTop + subSize.height)
    // getSubMenuEnterPivotX / Y: the edge facing the main menu, at the anchor row's centre.
    val pivotX = if (subRect.left > main.left) 0f else 1f
    val pivotY = (anchorRow.center.y - subRect.top).toFloat() / subSize.height.coerceAtLeast(1)
    return SubmenuGeometry(
        compact = false,
        subRect = subRect,
        mainPivot = TransformOrigin.Center,
        mainScale = MAIN_MENU_SHRINK_RATIO,
        mainTranslationY = 0f,
        mainContentAlpha = DEFAULT_MAIN_CONTENT_ALPHA,
        subStartTranslationY = 0f,
        clipTopStart = 0f,
        clipHeightStart = subSize.height.toFloat(),
        subPivot = TransformOrigin(pivotX, pivotY.coerceIn(0f, 1f)),
    )
}

/**
 * Internal shared layout for cascading list popups, after COUI 17 `COUIPopupListWindow` with a sub
 * menu (`COUIPopupMenuRootView` + `SmallScreenAnimationController` /
 * `DefaultScreenAnimationController`). Cascading depth is limited to 2.
 */
// backgroundModifier paints both the main and the sub menu surface.
@Suppress("ktlint:compose:modifier-reused-check")
@Composable
internal fun CascadingListPopupLayout(
    show: Boolean,
    popupHost: @Composable (visible: Boolean, content: @Composable () -> Unit) -> Unit,
    entries: List<DropdownEntry>,
    onDismissRequest: () -> Unit,
    dropdownColors: DropdownColors,
    popupModifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    onDismissFinished: (() -> Unit)? = null,
    popupPositionProvider: PopupPositionProvider = ListPopupDefaults.DropdownPositionProvider,
    alignment: PopupPositionProvider.Align = PopupPositionProvider.Align.End,
    enableWindowDim: Boolean = false,
    maxHeight: Dp? = null,
    minWidth: Dp = ListPopupDefaults.MinWidth,
    collapseOnSelection: Boolean = true,
    backgroundColor: Color = ListPopupDefaults.backgroundColor(),
    materialBlur: Boolean = LocalCOUIMaterialEffectEnabled.current,
    materialEffect: Boolean = LocalCOUIMaterialEffectEnabled.current,
) {
    // Resolved at the call site, where dialogs and sheets can switch the popup blur off.
    val surface = rememberPopupSurface(
        materialBlur = materialBlur,
        backgroundColor = backgroundColor,
        materialEffect = materialEffect,
        backgroundModifier = backgroundModifier,
    )
    val internalVisible = remember { mutableStateOf(false) }
    val pathProgress = remember { Animatable(0f) }
    val scaleProgress = remember { Animatable(0f) }
    val alphaProgress = remember { Animatable(0f) }
    val dimProgress = remember { Animatable(0f) }
    // Two tracks: a depth-0 gesture may start while the depth-1 collapse is still settling.
    val backEnterProgress = remember { Animatable(0f) }
    val backExpandProgress = remember { Animatable(0f) }

    var expandedItem by remember { mutableStateOf<DropdownItem?>(null) }
    // Outlives [expandedItem] until the collapse spring settles so the sub menu stays composed.
    var displayedItem by remember { mutableStateOf<DropdownItem?>(null) }
    val subProgress = remember { Animatable(0f) }

    val currentOnDismiss by rememberUpdatedState(onDismissRequest)
    val currentOnDismissFinished by rememberUpdatedState(onDismissFinished)
    val coroutineScope = rememberCoroutineScope()
    var mainSize by remember { mutableStateOf(IntSize.Zero) }
    var hostPositionInWindow by remember { mutableStateOf(IntOffset.Zero) }
    var hostMeasured by remember { mutableStateOf(false) }

    LaunchedEffect(show) {
        if (show) {
            internalVisible.value = true
            backEnterProgress.snapTo(0f)
            backExpandProgress.snapTo(0f)
            if (displayedItem != null) {
                // startMainMenuEnter with a sub menu still up (reopened mid-dismiss): force-hide it.
                subProgress.snapTo(0f)
                expandedItem = null
                displayedItem = null
            }
            if (enableWindowDim) launch { dimProgress.animateTo(1f, ListPopupDefaults.DimEnterAnimationSpec) }
            snapshotFlow { mainSize to hostMeasured }.first { (size, ready) -> size != IntSize.Zero && ready }
            launch { pathProgress.animateTo(1f, ListPopupDefaults.PathEnterAnimationSpec) }
            launch { scaleProgress.animateTo(1f, ListPopupDefaults.ScaleEnterAnimationSpec) }
            launch { alphaProgress.animateTo(1f, ListPopupDefaults.OpacityEnterAnimationSpec) }
        } else {
            if (!internalVisible.value) return@LaunchedEffect
            // COUI freezes an open sub menu and lets it leave with the main menu.
            subProgress.stop()
            if (enableWindowDim) launch { dimProgress.animateTo(0f, ListPopupDefaults.DimExitAnimationSpec) }
            listOf(
                launch { pathProgress.animateTo(0f, ListPopupDefaults.PathExitAnimationSpec) },
                launch { scaleProgress.animateTo(0f, ListPopupDefaults.ScaleExitAnimationSpec) },
                launch { alphaProgress.animateTo(0f, ListPopupDefaults.OpacityExitAnimationSpec) },
            ).joinAll()
            pathProgress.snapTo(0f)
            scaleProgress.snapTo(0f)
            alphaProgress.snapTo(0f)
            dimProgress.snapTo(0f)
            backEnterProgress.snapTo(0f)
            backExpandProgress.snapTo(0f)
            subProgress.snapTo(0f)
            expandedItem = null
            displayedItem = null
            internalVisible.value = false
            currentOnDismissFinished?.invoke()
        }
    }

    // Carries a long press that opened the sub menu on into it (forwardTouchToSubMenuIfNeeded).
    val gestureRelay = remember { PopupGestureRelay() }

    LaunchedEffect(expandedItem) {
        val item = expandedItem
        if (item != null) {
            if (displayedItem != null && displayedItem !== item) {
                // dispatchMainMenuMixedItemClick: another sub menu is up, hide it without animation.
                subProgress.snapTo(0f)
            }
            displayedItem = item
            backExpandProgress.snapTo(0f)
            gestureRelay.subEntered = false
            gestureRelay.subInteractive = true
            subProgress.animateTo(1f, SubMenuSpring)
            // onSubMenuEntered: from now on the main menu's finger may steer the sub menu.
            gestureRelay.subEntered = true
        } else if (displayedItem != null) {
            // onSubMenuStartToExit: the sub menu stops taking touches, its press mask fades
            // (clearSharedMask(true)) and a relayed finger is let go.
            gestureRelay.subEntered = false
            gestureRelay.subInteractive = false
            gestureRelay.subHost?.let { host -> host.scope?.let(host::cancel) }
            gestureRelay.subPanel?.cancel()
            subProgress.animateTo(0f, SubMenuSpring)
            displayedItem = null
            gestureRelay.subInteractive = true
            backExpandProgress.snapTo(0f)
        }
    }

    if (!show && !internalVisible.value) return

    var parentBounds by remember { mutableStateOf(IntRect.Zero) }
    Spacer(
        modifier = Modifier.onGloballyPositioned { childCoordinates ->
            childCoordinates.parentLayoutCoordinates?.let { parent ->
                val pos = parent.positionInWindow()
                parentBounds = IntRect(
                    left = pos.x.toInt(),
                    top = pos.y.toInt(),
                    right = pos.x.toInt() + parent.size.width,
                    bottom = pos.y.toInt() + parent.size.height,
                )
            }
        },
    )
    if (parentBounds == IntRect.Zero) return

    val layoutInfo = rememberListPopupLayoutInfo(
        alignment = alignment,
        popupPositionProvider = popupPositionProvider,
        parentBounds = parentBounds,
        popupContentSize = mainSize,
    )
    val layoutDirection = LocalLayoutDirection.current
    val density = LocalDensity.current
    val containerSize = LocalWindowInfo.current.containerSize
    // COUIPopupMenuRootView.setDomain picks the small-screen controller on a compact-width window.
    val compact = with(density) {
        COUILayoutGrid.screenTier(containerSize.width.toDp(), containerSize.height.toDp()) == COUILayoutGrid.ScreenTier.Small
    }

    // Anchor rows relative to the main menu, as COUI setSubmenuAnchor walks the view tree.
    val anchorRows = remember { mutableStateMapOf<DropdownItem, IntRect>() }
    val geometryHolder = remember { CascadeGeometryHolder() }

    val requestCollapse: () -> Unit = remember { { expandedItem = null } }

    popupHost(internalVisible.value) {
        val backState = rememberNavigationEventState(currentInfo = NavigationEventInfo.None)
        NavigationBackHandler(
            state = backState,
            isBackEnabled = show,
            onBackCancelled = {
                coroutineScope.launch {
                    if (expandedItem != null) {
                        backExpandProgress.animateTo(0f, SubMenuSpring)
                    } else {
                        backEnterProgress.animateTo(0f, ListPopupDefaults.ResetAnimationSpec)
                    }
                }
            },
            // Mirror outside-tap: depth > 0 collapses the sub menu, depth 0 dismisses the popup.
            onBackCompleted = {
                if (expandedItem != null) expandedItem = null else currentOnDismiss()
            },
        )

        LaunchedEffect(Unit) {
            snapshotFlow { backState.transitionState }
                .collect { transitionState ->
                    if (
                        transitionState is NavigationEventTransitionState.InProgress &&
                        transitionState.direction == NavigationEventTransitionState.TRANSITIONING_BACK
                    ) {
                        val progress = transitionState.latestEvent.progress
                        if (expandedItem != null) backExpandProgress.snapTo(progress) else backEnterProgress.snapTo(progress)
                    }
                }
        }

        CompositionLocalProvider(
            LocalDismissState provides { currentOnDismiss() },
            LocalMaterialBackdrop provides null,
            LocalPopupMaterialBlur provides null,
            LocalCardListPosition provides CardListPosition.None,
            LocalPopupGestureRelay provides gestureRelay,
        ) {
            val dimColor = COUITheme.colorScheme.mask
            val surfaceColor = surface.color
            val shape = remember { RoundedCornerShape(CascadingPopupCornerRadius) }
            val squircleEnabled = isSquircleEnabled()
            val touchSlop = LocalViewConfiguration.current.touchSlop
            // Per-show state, composed inside the host: a spring launched here is cancelled when
            // the host leaves at the end of the exit, so state kept outside it would freeze
            // mid-spring and greet the next show with a displaced menu or a lit spotlight.
            // The main menu's touch motion, which an open sub menu follows (ParentInfluenceUnit).
            val mainTouchMotion = remember { TouchMotionState() }
            // The sub menu's own touch motion, also driven by a relayed finger.
            val subTouchMotion = remember { TouchMotionState() }
            val mainSpotLight = rememberPopupPanelLight()
            val subSpotLight = rememberPopupPanelLight()
            val subRequested = expandedItem != null
            LaunchedEffect(subRequested) {
                if (subRequested) {
                    // onSubMenuStartToEnter: the main menu's light goes out and stays dark.
                    mainSpotLight.extinguish()
                } else {
                    // onSubMenuStartToExit: a closing sub menu drops its light and its touches.
                    subSpotLight.extinguish()
                }
            }
            // The sub panel as the relayed finger sees it (forwardTouchInsideSubMenuBounds): its
            // spotlight and its touch motion get the synthetic DOWN / MOVE / UP / CANCEL.
            val panelScope = rememberCoroutineScope()
            val subPanel = remember(subSpotLight, subTouchMotion, panelScope, density) {
                CascadeSubPanel(geometryHolder, subSpotLight, subTouchMotion, panelScope, density) { surface.materialEffect }
            }
            DisposableEffect(gestureRelay, subPanel) {
                gestureRelay.subPanel = subPanel
                onDispose { if (gestureRelay.subPanel === subPanel) gestureRelay.subPanel = null }
            }

            val enterScale = { scaleProgress.value * (1f - backEnterProgress.value) }
            val enterAlpha = { alphaProgress.value * (1f - backEnterProgress.value) }
            val sub = { subProgress.value * (1f - backExpandProgress.value) }

            Box(modifier = Modifier.fillMaxSize()) {
                if (enableWindowDim) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer { alpha = dimProgress.value * (1f - backEnterProgress.value) }
                            .background(dimColor),
                    )
                }
                Layout(
                    modifier = popupModifier
                        .fillMaxSize()
                        .onGloballyPositioned { coords ->
                            val pos = coords.positionInWindow()
                            hostPositionInWindow = IntOffset(pos.x.toInt(), pos.y.toInt())
                            hostMeasured = true
                        }
                        // COUIPopupMenuRootView.handleMainAreaDelayedDismissOnTouch and the main
                        // root's click while a sub menu is up; observes, consumes nothing.
                        .mainAreaDismiss(
                            geometryHolder = geometryHolder,
                            touchSlop = touchSlop,
                            active = { expandedItem != null },
                            entered = { gestureRelay.subEntered },
                            onCollapse = requestCollapse,
                        )
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                if (expandedItem != null) expandedItem = null else currentOnDismiss()
                            })
                        }
                        // The enter / exit motion moves both menus together about the main menu
                        // centre, so an open sub menu leaves with the main menu (startMainMenuExit).
                        .graphicsLayer {
                            val main = geometryHolder.mainRectLocal
                            if (main != null && size.width > 0f && size.height > 0f) {
                                val scale = enterScale().coerceAtLeast(0f)
                                scaleX = scale
                                scaleY = scale
                                alpha = enterAlpha().coerceIn(0f, 1f)
                                val travel = geometryHolder.gravityPath.pointAt(
                                    pathProgress.value * (1f - backEnterProgress.value),
                                )
                                translationX = travel.x
                                translationY = travel.y
                                transformOrigin = TransformOrigin(
                                    main.center.x / size.width,
                                    main.center.y / size.height,
                                )
                            } else {
                                alpha = 0f
                            }
                        },
                    content = {
                        // Main menu.
                        Box(
                            modifier = Modifier
                                .layoutId(SLOT_MAIN)
                                .graphicsLayer {
                                    // Read the progress before anything that may return early, so
                                    // this block always re-runs with the MenuTransitionUnit.
                                    val progress = sub().coerceIn(0f, 1f)
                                    val geometry = geometryHolder.submenu
                                    val p = if (geometry != null) progress else 0f
                                    val scale = lerp(1f, geometry?.mainScale ?: 1f, p)
                                    scaleX = scale
                                    scaleY = scale
                                    translationY = lerp(0f, geometry?.mainTranslationY ?: 0f, p)
                                    transformOrigin = geometry?.mainPivot ?: TransformOrigin.Center
                                }
                                // The main menu's touch motion pauses while a sub menu is open
                                // (BasePopupMenuAnimationController.makeAnimator).
                                .couiTouchMotion(
                                    interactionSource = null,
                                    enabled = displayedItem == null,
                                    pressScaleEnabled = false,
                                    state = mainTouchMotion,
                                )
                                .popupDropShadow(shape)
                                .squircleSurface(color = surfaceColor, cornerRadius = CascadingPopupCornerRadius, weight = PopupCornerWeight)
                                .then(surface.backgroundModifier)
                                // COUI turns the main menu spotlight off while a sub menu is open
                                // (setSpotLightTouchEnabled(false) until onSubMenuExited).
                                .popupMaterialEffect(
                                    enabled = surface.materialEffect,
                                    spotLight = mainSpotLight,
                                    spotLightSuppressed = { expandedItem != null || displayedItem != null },
                                )
                                .consumePopupTouches()
                                .onGloballyPositioned { geometryHolder.mainCoordinates = it },
                        ) {
                            Box(
                                modifier = Modifier.graphicsLayer {
                                    val progress = sub().coerceIn(0f, 1f)
                                    val geometry = geometryHolder.submenu
                                    val p = if (geometry != null) progress else 0f
                                    alpha = lerp(1f, geometry?.mainContentAlpha ?: 1f, p)
                                },
                            ) {
                                CascadingMainList(
                                    entries = entries,
                                    expandedItem = expandedItem,
                                    holdExpandedRow = !compact,
                                    dropdownColors = dropdownColors,
                                    onExpand = { item ->
                                        // mTransferredPressProgress: the press the compact header inherits.
                                        gestureRelay.transferredPress = gestureRelay.mainHost?.mask?.press ?: 0f
                                        expandedItem = item
                                    },
                                    onLeafSelected = { item ->
                                        item.onClick?.invoke()
                                        if (collapseOnSelection) currentOnDismiss()
                                    },
                                    onAnchorRow = { item, coordinates ->
                                        val main = geometryHolder.mainCoordinates
                                        if (main != null && main.isAttached && coordinates.isAttached && displayedItem == null) {
                                            val offset = main.localPositionOf(coordinates, Offset.Zero)
                                            anchorRows[item] = IntRect(
                                                left = offset.x.toInt(),
                                                top = offset.y.toInt(),
                                                right = offset.x.toInt() + coordinates.size.width,
                                                bottom = offset.y.toInt() + coordinates.size.height,
                                            )
                                        }
                                    },
                                )
                            }
                            if (expandedItem != null) {
                                // From onSubMenuStartToEnter until onSubMenuStartToExit the main
                                // list takes no new touches (allowListViewDispatchTouchEvent(false))
                                // and the main root is a click target that closes the sub menu;
                                // mainAreaDismiss on the root runs that click.
                                Box(modifier = Modifier.matchParentSize().blockPopupTouches())
                            }
                        }

                        // Sub menu.
                        val shown = displayedItem
                        if (shown != null) {
                            key(shown) {
                                Box(
                                    modifier = Modifier
                                        .layoutId(SLOT_SUB)
                                        .graphicsLayer {
                                            val p = sub().coerceIn(0f, 1f)
                                            val geometry = geometryHolder.submenu ?: return@graphicsLayer
                                            // ParentInfluenceUnit: the sub menu takes on the main
                                            // menu's touch motion (updateFromParent), which springs
                                            // home once the sub menu opens (resetTouchOnly(true)).
                                            // Units add their translations and scale deltas
                                            // (TouchMotionCollector.put).
                                            val parentTranslationX = mainTouchMotion.translationX.value
                                            val parentTranslationY = mainTouchMotion.translationY.value
                                            val parentScaleX = mainTouchMotion.scaleX.value
                                            val parentScaleY = mainTouchMotion.scaleY.value
                                            translationX = parentTranslationX
                                            if (geometry.compact) {
                                                // SubMenuTransitionUnit: lerp(startY, 0, p). Opening
                                                // starts from the anchor row's unscaled place;
                                                // closing re-reads it every frame where the
                                                // shrunken main menu draws it.
                                                val start = if (expandedItem == null) {
                                                    geometry.collapseStartTranslationY(p)
                                                } else {
                                                    geometry.subStartTranslationY
                                                }
                                                val travel = lerp(start, 0f, p) + parentTranslationY
                                                // clampSubMenuTranslationY applies to the summed sink.
                                                translationY = max(geometryHolder.availableTopOffset, travel)
                                                scaleX = parentScaleX
                                                scaleY = parentScaleY
                                            } else {
                                                // The fade sits on the surface below, so the
                                                // shadow is not cut off by an offscreen layer.
                                                translationY = parentTranslationY
                                                scaleX = (p + parentScaleX - 1f).coerceAtLeast(0f)
                                                scaleY = (p + parentScaleY - 1f).coerceAtLeast(0f)
                                                transformOrigin = geometry.subPivot
                                            }
                                        }
                                        .onGloballyPositioned { geometryHolder.subMotionCoordinates = it }
                                        // The sub menu's touch motion stops (and springs home) once
                                        // it starts to leave (startSubMenuExit → resetTouchOnly).
                                        .couiTouchMotion(
                                            interactionSource = null,
                                            enabled = expandedItem != null,
                                            pressScaleEnabled = false,
                                            state = subTouchMotion,
                                        )
                                        .onGloballyPositioned { geometryHolder.subPanelCoordinates = it }
                                        .popupPanelLightInput(
                                            spotLight = subSpotLight,
                                            enabled = surface.materialEffect,
                                            suppressed = { expandedItem == null },
                                        )
                                        .consumePopupTouches(),
                                ) {
                                    // The surface follows the RoundFrameLayout override outline: on
                                    // a compact window it grows from the anchor row band, carrying
                                    // its shadow, fill and blur with it.
                                    // The shadow fades in with the band, like the override outline
                                    // alpha (setOverrideOutline(..., progress)); a full sub menu
                                    // fades its shadow by the same progress, outside the layer
                                    // that fades the surface and rows.
                                    val subAlpha = {
                                        val geometry = geometryHolder.submenu
                                        val p = sub().coerceIn(0f, 1f)
                                        when {
                                            geometry == null -> 1f

                                            geometry.compact -> 1f

                                            // applySubMenuVisibilityForBlurAlpha: INVISIBLE at ≤ 10%.
                                            surface.blurred && p <= SUB_MENU_BLUR_VISIBILITY_MIN_ALPHA -> 0f

                                            else -> p
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .cascadeBand(geometryHolder, sub)
                                            .popupDropShadow(
                                                shape = shape,
                                                alpha = {
                                                    val geometry = geometryHolder.submenu
                                                    if (geometry == null) {
                                                        1f
                                                    } else if (geometry.compact) {
                                                        sub().coerceIn(0f, 1f)
                                                    } else {
                                                        subAlpha()
                                                    }
                                                },
                                            ),
                                    )
                                    Box(modifier = Modifier.graphicsLayer { alpha = subAlpha() }) {
                                        // The background (fill, blur) takes the override
                                        // outline's bounds (setOverrideOutline → background
                                        // setBounds), so it shrinks with the band.
                                        Box(
                                            modifier = Modifier
                                                .matchParentSize()
                                                .cascadeBand(geometryHolder, sub)
                                                .squircleSurface(color = surfaceColor, cornerRadius = CascadingPopupCornerRadius, weight = PopupCornerWeight)
                                                .then(surface.backgroundModifier),
                                        )
                                        // The shadow-edge drawable and the spotlight keep the
                                        // whole panel's bounds (RoundFrameLayout.onSizeChanged
                                        // setBounds(0, 0, w, h)) and are only clipped by the
                                        // outline, so the band's own edges carry no stroke: the
                                        // panel's stroke sits outside a shrunken band.
                                        Box(
                                            modifier = Modifier
                                                .matchParentSize()
                                                .cascadeContentClip(geometryHolder, sub, squircleEnabled)
                                                .popupMaterialEffect(
                                                    enabled = surface.materialEffect,
                                                    spotLight = subSpotLight,
                                                    trackTouches = false,
                                                ),
                                        )
                                        Box(modifier = Modifier.cascadeContentClip(geometryHolder, sub, squircleEnabled)) {
                                            CascadingSubList(
                                                parent = shown,
                                                showHeader = compact,
                                                headerActive = expandedItem != null,
                                                dropdownColors = dropdownColors,
                                                onHeaderClick = requestCollapse,
                                                onLeafSelected = { item ->
                                                    item.onClick?.invoke()
                                                    if (collapseOnSelection) currentOnDismiss()
                                                },
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    },
                ) { measurables, constraints ->
                    val windowBounds = layoutInfo.windowBounds
                    val popupMargin = layoutInfo.popupMargin
                    val host = hostPositionInWindow
                    val minHeightPx = ListPopupDefaults.MinPopupHeight.roundToPx()
                    val availableHeight = windowBounds.height.coerceAtLeast(minHeightPx)
                    val availableWidth = (windowBounds.width - popupMargin.left - popupMargin.right).coerceAtLeast(0)

                    val mainMeasurable = measurables.first { it.layoutId == SLOT_MAIN }
                    val mainPlaceable = mainMeasurable.measure(
                        Constraints(
                            minWidth = minWidth.roundToPx().coerceAtMost(availableWidth),
                            maxWidth = availableWidth,
                            minHeight = 0,
                            maxHeight = maxHeight?.roundToPx()?.coerceIn(minHeightPx, availableHeight) ?: availableHeight,
                        ),
                    )
                    val measuredMain = IntSize(mainPlaceable.width, mainPlaceable.height)
                    if (mainSize != measuredMain) mainSize = measuredMain
                    val mainOffset = popupPositionProvider.calculatePosition(
                        parentBounds,
                        windowBounds,
                        layoutDirection,
                        measuredMain,
                        popupMargin,
                        alignment,
                    )
                    val mainRect = IntRect(mainOffset, measuredMain)
                    geometryHolder.mainRectLocal = Rect(
                        (mainRect.left - host.x).toFloat(),
                        (mainRect.top - host.y).toFloat(),
                        (mainRect.right - host.x).toFloat(),
                        (mainRect.bottom - host.y).toFloat(),
                    )
                    geometryHolder.gravityPath = popupGravityPathFor(parentBounds, mainOffset, measuredMain, density.density)

                    // The band only depends on the main menu and the anchor row, so it is known
                    // before the sub menu is measured: its surface must never be measured at the
                    // full panel size for a frame (the opening frame would flash a full panel).
                    val anchorLocal = displayedItem?.let { anchorRows[it] }
                    geometryHolder.setBand(
                        compact = compact,
                        clipTop = PopupListVerticalPadding.roundToPx().toFloat(),
                        clipHeight = anchorLocal?.let { min(it.height, measuredMain.height - it.top) }?.toFloat() ?: 0f,
                    )
                    // COUI caps the sub menu so the main menu keeps a 12dp strip on view.
                    val subMeasurable = measurables.firstOrNull { it.layoutId == SLOT_SUB }
                    val subPlaceable = subMeasurable?.measure(
                        Constraints.fixedWidth(measuredMain.width).copy(
                            maxHeight = (availableHeight - SubMenuMinGapToTop.roundToPx()).coerceAtLeast(0),
                        ),
                    )
                    geometryHolder.submenu = if (subPlaceable != null && anchorLocal != null) {
                        computeSubmenuGeometry(
                            main = mainRect,
                            anchorRow = anchorLocal.translate(mainRect.left, mainRect.top),
                            popupAnchor = parentBounds,
                            subSize = IntSize(subPlaceable.width, subPlaceable.height),
                            available = windowBounds,
                            compact = compact,
                            layoutDirection = layoutDirection,
                            verticalOverlap = MainSubMenuVerticalOverlap.roundToPx(),
                            horizontalOverlap = MainSubMenuHorizontalOverlap.roundToPx(),
                            minGap = SubMenuMinGapToTop.roundToPx(),
                            listInset = PopupListVerticalPadding.roundToPx(),
                        ).also { geometryHolder.availableTopOffset = (windowBounds.top - it.subRect.top).toFloat() }
                    } else {
                        null
                    }
                    geometryHolder.subRectLocal = geometryHolder.submenu?.subRect?.let { r ->
                        Rect(
                            (r.left - host.x).toFloat(),
                            (r.top - host.y).toFloat(),
                            (r.right - host.x).toFloat(),
                            (r.bottom - host.y).toFloat(),
                        )
                    }

                    layout(constraints.maxWidth, constraints.maxHeight) {
                        mainPlaceable.place(mainRect.left - host.x, mainRect.top - host.y)
                        val geometry = geometryHolder.submenu
                        if (subPlaceable != null && geometry != null) {
                            subPlaceable.place(geometry.subRect.left - host.x, geometry.subRect.top - host.y, zIndex = 1f)
                        }
                    }
                }
            }
        }
    }
}

private const val SLOT_MAIN = "main"
private const val SLOT_SUB = "sub"

/** Geometry shared between the cascade's measure pass and its layer / draw / input blocks. */
private class CascadeGeometryHolder {
    var mainCoordinates: LayoutCoordinates? = null
    var mainRectLocal: Rect? = null
    var gravityPath: PopupGravityPath = PopupGravityPath.None

    /**
     * Snapshot state: the layer blocks of both menus read it, and a block that last ran before the
     * sub menu existed must re-run once the measure pass has computed it (otherwise the main menu
     * never shrinks and snaps back when the sub menu leaves).
     */
    var submenu: SubmenuGeometry? by mutableStateOf(null)

    /** The sub menu's resting rect in the host's space (`PopupMenuDomain.mSubMenu`). */
    var subRectLocal: Rect? = null

    /** The sub panel where its spotlight reads touches (inside its touch-motion layer). */
    var subPanelCoordinates: LayoutCoordinates? = null

    /** The sub panel where its touch motion reads touches (outside its touch-motion layer). */
    var subMotionCoordinates: LayoutCoordinates? = null

    /** `clampSubMenuTranslationY`: the sub menu never travels above the available top. */
    var availableTopOffset: Float = Float.NEGATIVE_INFINITY

    // The compact band's start (configSubMenuAnimationPropertiesForSubMenuEnter), set by the
    // measure pass before it measures the sub menu.
    private var bandCompact = false
    private var bandClipTop = 0f
    private var bandClipHeight = 0f

    fun setBand(compact: Boolean, clipTop: Float, clipHeight: Float) {
        bandCompact = compact
        bandClipTop = clipTop
        bandClipHeight = clipHeight
    }

    /** The top of the compact sub menu's band at [progress], in its own pixels (0 elsewhere). */
    fun bandTop(progress: Float): Float {
        if (!bandCompact) return 0f
        return lerp(bandClipTop, 0f, progress.coerceIn(0f, 1f))
    }

    /** The height of the compact sub menu's band at [progress] in a panel [height] tall. */
    fun bandHeight(progress: Float, height: Float): Float {
        if (!bandCompact) return height
        val top = bandTop(progress)
        return lerp(bandClipHeight, height, progress.coerceIn(0f, 1f)).coerceIn(0f, (height - top).coerceAtLeast(0f))
    }

    /** Whether ([x], [y]) in the host's space lies on the main menu and not on the sub menu. */
    fun isInMainOnly(x: Float, y: Float): Boolean = isInMain(x, y) && !isInSub(x, y)

    fun isInMain(x: Float, y: Float): Boolean {
        val main = mainRectLocal ?: return false
        return x >= main.left && x <= main.right && y >= main.top && y <= main.bottom
    }

    private fun isInSub(x: Float, y: Float): Boolean {
        val sub = subRectLocal ?: return false
        return x >= sub.left && x <= sub.right && y >= sub.top && y <= sub.bottom
    }
}

/** Sizes and places the sub menu surface on its band (`RoundFrameLayout.setOverrideOutline`). */
private fun Modifier.cascadeBand(geometryHolder: CascadeGeometryHolder, progress: () -> Float): Modifier = layout { measurable, constraints ->
    val p = progress()
    val top = geometryHolder.bandTop(p)
    val height = geometryHolder.bandHeight(p, constraints.maxHeight.toFloat())
    val placeable = measurable.measure(Constraints.fixed(constraints.maxWidth, height.toInt()))
    layout(constraints.maxWidth, constraints.maxHeight) { placeable.place(0, top.toInt()) }
}

/** Clips the sub menu rows to the same band. */
private fun Modifier.cascadeContentClip(
    geometryHolder: CascadeGeometryHolder,
    progress: () -> Float,
    squircleEnabled: Boolean,
): Modifier = drawWithCache {
    val path = Path()
    val cornerRadius = CascadingPopupCornerRadius.toPx()
    onDrawWithContent {
        val p = progress()
        val top = geometryHolder.bandTop(p)
        val height = geometryHolder.bandHeight(p, size.height)
        if (height <= 0f) return@onDrawWithContent
        path.rewind()
        path.addSquircleRect(size.width, height, cornerRadius, PopupCornerWeight, squircleEnabled)
        path.translate(Offset(0f, top))
        clipPath(path) { this@onDrawWithContent.drawContent() }
    }
}

/**
 * The sub panel as a relayed finger reaches it (`COUIPopupMenuRootView`
 * `forwardTouchInsideSubMenuBounds`): the synthetic events land in the sub `RoundFrameLayout`,
 * whose spotlight (`COUISpotLightEffectDrawable.onTouchEvent`) and touch motion
 * (`makeSubMenuAnimator`) react as to a real finger.
 */
private class CascadeSubPanel(
    private val geometryHolder: CascadeGeometryHolder,
    private val spotLight: PopupSpotLight,
    private val touchMotion: TouchMotionState,
    private val scope: CoroutineScope,
    private val density: Density,
    private val lightEnabled: () -> Boolean,
) : PopupRelayTarget {
    private val tracker = VelocityTracker()
    private var tracking = false

    private fun lightPoint(window: Offset): Offset? = geometryHolder.subPanelCoordinates?.takeIf { it.isAttached }?.windowToLocal(window)

    private fun motionPoint(window: Offset): Offset? = geometryHolder.subMotionCoordinates?.takeIf { it.isAttached }?.windowToLocal(window)

    override fun down(windowPosition: Offset, uptimeMillis: Long) {
        if (lightEnabled()) lightPoint(windowPosition)?.let(spotLight::onDown)
        val point = motionPoint(windowPosition) ?: return
        tracking = true
        tracker.resetTracking()
        tracker.addPosition(uptimeMillis, point)
        touchMotion.touching = true
        with(touchMotion) { density.down(point) }
    }

    override fun move(windowPosition: Offset, uptimeMillis: Long) {
        if (lightEnabled()) lightPoint(windowPosition)?.let(spotLight::onMove)
        if (!tracking) return
        val point = motionPoint(windowPosition) ?: return
        tracker.addPosition(uptimeMillis, point)
        with(touchMotion) { density.move(point, scope) }
    }

    override fun up(windowPosition: Offset, uptimeMillis: Long) {
        if (lightEnabled()) lightPoint(windowPosition)?.let(spotLight::onUp)
        if (!tracking) return
        motionPoint(windowPosition)?.let { tracker.addPosition(uptimeMillis, it) }
        val velocity = tracker.calculateVelocity()
        release(velocity.x, velocity.y)
    }

    override fun cancel() {
        spotLight.extinguish()
        if (tracking) release(0f, 0f)
    }

    private fun release(velocityX: Float, velocityY: Float) {
        tracking = false
        touchMotion.touching = false
        with(touchMotion) { density.release(velocityX, velocityY, scope) }
    }
}

/** Makes a node hit-testable so the touches it covers never reach the siblings below it. */
private fun Modifier.blockPopupTouches(): Modifier = pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) awaitPointerEvent(PointerEventPass.Initial)
    }
}

/**
 * `COUIPopupMenuRootView` while a sub menu is up, at the root so it sees every gesture (one that
 * starts on the sub menu included):
 * - The main root's click (`mCancelSubMenuEnterAndStartExit`, set from `onSubMenuStartToEnter`): a
 *   gesture that goes down on the main menu outside the sub menu and lifts still on the main menu
 *   closes the sub menu at once.
 * - `handleMainAreaDelayedDismissOnTouch`, once the sub menu has entered: a finger that goes down on
 *   the main menu (outside the sub menu) and moves past the touch slop, or that slides onto it from
 *   elsewhere, closes the sub menu after [MAIN_AREA_DISMISS_DELAY_MILLIS] unless it leaves the main
 *   menu or lifts first.
 *
 * A gesture already running when the sub menu opened (the long press that opened it) is left alone:
 * its finger keeps drag-selecting on the main menu (a deliberate deviation, see `ListPopup.kt`).
 * Observes in the initial pass and consumes nothing.
 */
private fun Modifier.mainAreaDismiss(
    geometryHolder: CascadeGeometryHolder,
    touchSlop: Float,
    active: () -> Boolean,
    entered: () -> Boolean,
    onCollapse: () -> Unit,
): Modifier = pointerInput(Unit) {
    coroutineScope {
        awaitPointerEventScope {
            while (true) {
                val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                if (!active()) {
                    do {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                    } while (event.changes.any { it.pressed })
                    continue
                }
                var click = geometryHolder.isInMainOnly(down.position.x, down.position.y)
                var tracking = click && entered()
                var origin = down.position
                var pending: Job? = null
                fun post() {
                    if (pending == null) {
                        pending = launch {
                            delay(MAIN_AREA_DISMISS_DELAY_MILLIS)
                            if (entered()) onCollapse()
                        }
                    }
                }
                fun cancelPending() {
                    pending?.cancel()
                    pending = null
                }
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    val change = event.changes.firstOrNull { it.id == down.id } ?: break
                    val position = change.position
                    if (!change.pressed) {
                        cancelPending()
                        if (click && active() && geometryHolder.isInMain(position.x, position.y)) onCollapse()
                        break
                    }
                    if (click && !geometryHolder.isInMain(position.x, position.y)) click = false
                    if (!entered()) continue
                    val inside = geometryHolder.isInMainOnly(position.x, position.y)
                    if (tracking) {
                        if (!inside) {
                            cancelPending()
                            tracking = false
                        } else if ((position - origin).getDistance() > touchSlop) {
                            post()
                        }
                    } else if (inside) {
                        tracking = true
                        origin = position
                        post()
                    }
                }
                cancelPending()
            }
        }
    }
}

/** The main menu list: every group, with a sub-menu row tinted while its sub menu is open. */
@Composable
private fun CascadingMainList(
    entries: List<DropdownEntry>,
    expandedItem: DropdownItem?,
    holdExpandedRow: Boolean,
    dropdownColors: DropdownColors,
    onExpand: (DropdownItem) -> Unit,
    onLeafSelected: (DropdownItem) -> Unit,
    onAnchorRow: (DropdownItem, LayoutCoordinates) -> Unit,
) {
    CompositionLocalProvider(LocalPopupListRole provides PopupListRole.Main) {
        CascadingMainColumn(
            entries = entries,
            expandedItem = expandedItem,
            holdExpandedRow = holdExpandedRow,
            dropdownColors = dropdownColors,
            onExpand = onExpand,
            onLeafSelected = onLeafSelected,
            onAnchorRow = onAnchorRow,
        )
    }
}

@Composable
private fun CascadingMainColumn(
    entries: List<DropdownEntry>,
    expandedItem: DropdownItem?,
    holdExpandedRow: Boolean,
    dropdownColors: DropdownColors,
    onExpand: (DropdownItem) -> Unit,
    onLeafSelected: (DropdownItem) -> Unit,
    onAnchorRow: (DropdownItem, LayoutCoordinates) -> Unit,
) {
    val showGroupDividers = entries.sumOf { it.items.size } >= GROUP_MIN_ITEMS
    // MixedPopupListAdapter.setItemList raises the icon flag over the whole list, horizontal items included.
    val reserveIconSlot = remember(entries) { entries.any { entry -> entry.items.any { it.icon != null } } }
    ListPopupColumn {
        val lastEntryIdx = entries.lastIndex
        entries.forEachIndexed { entryIndex, entry ->
            if (entry.horizontal) {
                key("horizontal", entryIndex) {
                    DropdownHorizontalItems(
                        items = entry.items,
                        onItemClick = { index -> entry.items.getOrNull(index)?.let(onLeafSelected) },
                        enabled = entry.enabled,
                        dropdownColors = dropdownColors,
                    )
                }
                if (entryIndex != lastEntryIdx) key("divider", entryIndex) { DropdownGroupDivider() }
                return@forEachIndexed
            }
            entry.title?.let { title ->
                key("header", entryIndex) { DropdownHeaderItem(text = title, color = dropdownColors.headerColor) }
            }
            entry.items.forEachIndexed { itemIndex, item ->
                key(entryIndex, itemIndex) {
                    val children = item.children?.takeIf { it.isNotEmpty() }
                    if (children != null && !deeperChildrenWarned && children.any { !it.children.isNullOrEmpty() }) {
                        deeperChildrenWarned = true
                        println("[CascadingListPopup] Cascading depth is limited to 2; deeper children are ignored.")
                    }
                    PopupDropdownRow(
                        item = item,
                        isSelected = item.selected,
                        dropdownColors = dropdownColors,
                        enabled = entry.enabled && item.enabled,
                        hasSubmenu = children != null,
                        submenuExpanded = expandedItem === item,
                        submenuHeader = false,
                        held = holdExpandedRow && expandedItem === item,
                        reserveIconSlot = reserveIconSlot,
                        onClick = { if (children != null) onExpand(item) else onLeafSelected(item) },
                        onLongClick = if (children != null) ({ onExpand(item) }) else null,
                        modifier = Modifier.onGloballyPositioned { onAnchorRow(item, it) },
                    )
                }
            }
            if (entryIndex != lastEntryIdx && showGroupDividers) {
                key("divider", entryIndex) { DropdownGroupDivider() }
            }
        }
    }
}

/**
 * The sub menu list. On a compact window it starts with a copy of the parent item whose tap
 * collapses the sub menu (`performSubMenuHeader`), shown with a medium title and a tinted indicator
 * while [headerActive] (`PopupListItem.groupState` 2); larger windows list the children only.
 */
@Composable
private fun CascadingSubList(
    parent: DropdownItem,
    showHeader: Boolean,
    headerActive: Boolean,
    dropdownColors: DropdownColors,
    onHeaderClick: () -> Unit,
    onLeafSelected: (DropdownItem) -> Unit,
) {
    CompositionLocalProvider(LocalPopupListRole provides PopupListRole.Sub) {
        CascadingSubColumn(parent, showHeader, headerActive, dropdownColors, onHeaderClick, onLeafSelected)
    }
}

@Composable
private fun CascadingSubColumn(
    parent: DropdownItem,
    showHeader: Boolean,
    headerActive: Boolean,
    dropdownColors: DropdownColors,
    onHeaderClick: () -> Unit,
    onLeafSelected: (DropdownItem) -> Unit,
) {
    val children = parent.children.orEmpty()
    // The sub menu's own adapter list (the header copy of the parent on a compact window, then the
    // children) decides its icon flag, independently of the main menu.
    val reserveIconSlot = remember(parent, showHeader) {
        (showHeader && parent.icon != null) || children.any { it.icon != null }
    }
    ListPopupColumn {
        if (showHeader) {
            PopupDropdownRow(
                item = parent,
                isSelected = parent.selected,
                dropdownColors = dropdownColors,
                enabled = parent.enabled,
                hasSubmenu = true,
                // PopupListItem.groupState 2 only from onSubMenuStartToEnter to
                // onSubMenuStartToExit: the collapse runs with the plain title and indicator, so
                // the band closes onto an identical main row.
                submenuExpanded = headerActive,
                submenuHeader = headerActive,
                pressHandoff = true,
                reserveIconSlot = reserveIconSlot,
                onClick = onHeaderClick,
                onLongClick = onHeaderClick,
            )
        }
        children.forEachIndexed { index, child ->
            key(index) {
                PopupDropdownRow(
                    item = child,
                    isSelected = child.selected,
                    dropdownColors = dropdownColors,
                    enabled = child.enabled,
                    hasSubmenu = false,
                    submenuExpanded = false,
                    submenuHeader = false,
                    reserveIconSlot = reserveIconSlot,
                    onClick = { onLeafSelected(child) },
                )
            }
        }
    }
}

/** The smallest list that shows group dividers (COUI `COUIPopupListWindow.GROUP_MIN_ITEMS`). */
private const val GROUP_MIN_ITEMS = 3

private var deeperChildrenWarned = false
