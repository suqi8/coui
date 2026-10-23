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
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
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
import io.github.suqi8.coui.kmp.basic.DropdownColors
import io.github.suqi8.coui.kmp.basic.DropdownEntry
import io.github.suqi8.coui.kmp.basic.DropdownGroupDivider
import io.github.suqi8.coui.kmp.basic.DropdownHeaderItem
import io.github.suqi8.coui.kmp.basic.DropdownHorizontalItems
import io.github.suqi8.coui.kmp.basic.DropdownItem
import io.github.suqi8.coui.kmp.basic.ListPopupColumn
import io.github.suqi8.coui.kmp.basic.ListPopupDefaults
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
import io.github.suqi8.coui.kmp.basic.consumePopupTouches
import io.github.suqi8.coui.kmp.basic.popupDropShadow
import io.github.suqi8.coui.kmp.basic.popupGravityPathFor
import io.github.suqi8.coui.kmp.basic.popupMaterialEffect
import io.github.suqi8.coui.kmp.basic.popupSpotLightInput
import io.github.suqi8.coui.kmp.basic.rememberListPopupLayoutInfo
import io.github.suqi8.coui.kmp.basic.rememberPopupSpotLight
import io.github.suqi8.coui.kmp.basic.rememberPopupSurface
import io.github.suqi8.coui.kmp.effect.LocalMaterialBackdrop
import io.github.suqi8.coui.kmp.effect.LocalPopupMaterialBlur
import io.github.suqi8.coui.kmp.effect.couiTouchMotion
import io.github.suqi8.coui.kmp.squircle.addSquircleRect
import io.github.suqi8.coui.kmp.squircle.isSquircleEnabled
import io.github.suqi8.coui.kmp.squircle.squircleSurface
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.LocalDismissState
import io.github.suqi8.coui.kmp.utils.COUILayoutGrid
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
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
)

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
    materialBlur: Boolean = true,
    materialEffect: Boolean = true,
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
            displayedItem = item
            backExpandProgress.snapTo(0f)
            gestureRelay.subEntered = false
            subProgress.animateTo(1f, SubMenuSpring)
            // onSubMenuEntered: from now on the main menu's finger may steer the sub menu.
            gestureRelay.subEntered = true
        } else if (displayedItem != null) {
            gestureRelay.subEntered = false
            subProgress.animateTo(0f, SubMenuSpring)
            displayedItem = null
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
            LocalPopupGestureRelay provides gestureRelay,
        ) {
            val dimColor = COUITheme.colorScheme.windowDimming
            val surfaceColor = surface.color
            val shape = remember { RoundedCornerShape(CascadingPopupCornerRadius) }
            val squircleEnabled = isSquircleEnabled()
            val touchSlop = LocalViewConfiguration.current.touchSlop
            val subSpotLight = rememberPopupSpotLight()

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
                                    val geometry = geometryHolder.submenu
                                    val p = if (geometry != null) sub().coerceIn(0f, 1f) else 0f
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
                                )
                                .popupDropShadow(shape)
                                .squircleSurface(color = surfaceColor, cornerRadius = CascadingPopupCornerRadius, weight = PopupCornerWeight)
                                .then(surface.backgroundModifier)
                                // COUI turns the main menu spotlight off while a sub menu is open.
                                .popupMaterialEffect(
                                    enabled = surface.materialEffect,
                                    spotLightSuppressed = { displayedItem != null },
                                )
                                .consumePopupTouches()
                                .onGloballyPositioned { geometryHolder.mainCoordinates = it },
                        ) {
                            Box(
                                modifier = Modifier.graphicsLayer {
                                    val geometry = geometryHolder.submenu
                                    val p = if (geometry != null) sub().coerceIn(0f, 1f) else 0f
                                    alpha = lerp(1f, geometry?.mainContentAlpha ?: 1f, p)
                                },
                            ) {
                                CascadingMainList(
                                    entries = entries,
                                    expandedItem = expandedItem,
                                    holdExpandedRow = !compact,
                                    dropdownColors = dropdownColors,
                                    onExpand = { expandedItem = it },
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
                            if (expandedItem != null || displayedItem != null) {
                                // While the sub menu is open the main menu stops taking rows:
                                // a tap collapses the sub menu, and a drag that wanders the main
                                // menu collapses it after 500ms (COUIPopupMenuRootView).
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .mainAreaCollapse(touchSlop = touchSlop, onCollapse = requestCollapse),
                                )
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
                                            val geometry = geometryHolder.submenu ?: return@graphicsLayer
                                            val p = sub().coerceIn(0f, 1f)
                                            if (geometry.compact) {
                                                val travel = lerp(geometry.subStartTranslationY, 0f, p)
                                                val minTravel = geometryHolder.availableTopOffset
                                                translationY = max(minTravel, travel)
                                            } else {
                                                // The fade sits on the surface below, so the
                                                // shadow is not cut off by an offscreen layer.
                                                scaleX = p
                                                scaleY = p
                                                transformOrigin = geometry.subPivot
                                            }
                                        }
                                        .couiTouchMotion(interactionSource = null, pressScaleEnabled = false)
                                        .popupSpotLightInput(subSpotLight, enabled = surface.materialEffect)
                                        .consumePopupTouches(),
                                ) {
                                    // The surface follows the RoundFrameLayout override outline: on
                                    // a compact window it grows from the anchor row band, carrying
                                    // its shadow, fill and blur with it.
                                    // The shadow fades in with the band, like the override outline
                                    // alpha (setOverrideOutline(..., progress)); a full sub menu
                                    // fades its shadow by the same progress, outside the layer
                                    // that fades the surface and rows.
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .cascadeBand(geometryHolder, sub)
                                            .popupDropShadow(
                                                shape = shape,
                                                alpha = {
                                                    if (geometryHolder.submenu != null) sub().coerceIn(0f, 1f) else 1f
                                                },
                                            ),
                                    )
                                    Box(
                                        modifier = Modifier.graphicsLayer {
                                            val geometry = geometryHolder.submenu
                                            alpha = if (geometry != null && !geometry.compact) sub().coerceIn(0f, 1f) else 1f
                                        },
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .matchParentSize()
                                                .cascadeBand(geometryHolder, sub)
                                                .squircleSurface(color = surfaceColor, cornerRadius = CascadingPopupCornerRadius, weight = PopupCornerWeight)
                                                .then(surface.backgroundModifier)
                                                .popupMaterialEffect(enabled = surface.materialEffect, spotLight = subSpotLight, trackTouches = false),
                                        )
                                        Box(modifier = Modifier.cascadeContentClip(geometryHolder, sub, squircleEnabled)) {
                                            CascadingSubList(
                                                parent = shown,
                                                showHeader = compact,
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

                    // COUI caps the sub menu so the main menu keeps a 12dp strip on view.
                    val subMeasurable = measurables.firstOrNull { it.layoutId == SLOT_SUB }
                    val subPlaceable = subMeasurable?.measure(
                        Constraints.fixedWidth(measuredMain.width).copy(
                            maxHeight = (availableHeight - SubMenuMinGapToTop.roundToPx()).coerceAtLeast(0),
                        ),
                    )
                    val anchorLocal = displayedItem?.let { anchorRows[it] }
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

/** Geometry shared between the cascade's measure pass and its layer / draw blocks. */
private class CascadeGeometryHolder {
    var mainCoordinates: LayoutCoordinates? = null
    var mainRectLocal: Rect? = null
    var gravityPath: PopupGravityPath = PopupGravityPath.None
    var submenu: SubmenuGeometry? = null

    /** `clampSubMenuTranslationY`: the sub menu never travels above the available top. */
    var availableTopOffset: Float = Float.NEGATIVE_INFINITY
}

/** The visible band of a compact sub menu at [progress], as (top, height) in its own pixels. */
private fun subMenuBand(geometry: SubmenuGeometry?, progress: Float, height: Float): Pair<Float, Float> {
    if (geometry == null || !geometry.compact) return 0f to height
    val p = progress.coerceIn(0f, 1f)
    val top = lerp(geometry.clipTopStart, 0f, p)
    val bandHeight = lerp(geometry.clipHeightStart, height, p).coerceAtMost(height - top)
    return top to bandHeight.coerceAtLeast(0f)
}

/** Sizes and places the sub menu surface on its band (`RoundFrameLayout.setOverrideOutline`). */
private fun Modifier.cascadeBand(geometryHolder: CascadeGeometryHolder, progress: () -> Float): Modifier = layout { measurable, constraints ->
    val (top, height) = subMenuBand(geometryHolder.submenu, progress(), constraints.maxHeight.toFloat())
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
        val (top, height) = subMenuBand(geometryHolder.submenu, progress(), size.height)
        if (height <= 0f) return@onDrawWithContent
        path.rewind()
        path.addSquircleRect(Rect(0f, top, size.width, top + height), cornerRadius, PopupCornerWeight, squircleEnabled)
        clipPath(path) { this@onDrawWithContent.drawContent() }
    }
}

/**
 * Collapses the sub menu from a touch on the shrunken main menu: a tap collapses at once
 * (`mCancelSubMenuEnterAndStartExit`), a drag past the touch slop collapses after
 * [MAIN_AREA_DISMISS_DELAY_MILLIS] unless the finger lifts first (`mMainAreaDismissRunnable`).
 */
private fun Modifier.mainAreaCollapse(touchSlop: Float, onCollapse: () -> Unit): Modifier = pointerInput(Unit) {
    coroutineScope {
        awaitPointerEventScope {
            while (true) {
                val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                down.consume()
                var moved = false
                var pending: Job? = null
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    val change = event.changes.firstOrNull { it.id == down.id } ?: break
                    change.consume()
                    if (!change.pressed) {
                        pending?.cancel()
                        if (!moved) onCollapse()
                        break
                    }
                    val delta = change.position - down.position
                    if (!moved && (abs(delta.x) > touchSlop || abs(delta.y) > touchSlop)) {
                        moved = true
                        pending = launch {
                            delay(MAIN_AREA_DISMISS_DELAY_MILLIS)
                            onCollapse()
                        }
                    }
                }
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
 * The sub menu list. On a compact window it starts with a copy of the parent item
 * (`PopupListItem.groupState` 2: medium title, tinted indicator) whose tap collapses the sub menu
 * (`performSubMenuHeader`); larger windows list the children only.
 */
@Composable
private fun CascadingSubList(
    parent: DropdownItem,
    showHeader: Boolean,
    dropdownColors: DropdownColors,
    onHeaderClick: () -> Unit,
    onLeafSelected: (DropdownItem) -> Unit,
) {
    CompositionLocalProvider(LocalPopupListRole provides PopupListRole.Sub) {
        CascadingSubColumn(parent, showHeader, dropdownColors, onHeaderClick, onLeafSelected)
    }
}

@Composable
private fun CascadingSubColumn(
    parent: DropdownItem,
    showHeader: Boolean,
    dropdownColors: DropdownColors,
    onHeaderClick: () -> Unit,
    onLeafSelected: (DropdownItem) -> Unit,
) {
    val children = parent.children.orEmpty()
    ListPopupColumn {
        if (showHeader) {
            PopupDropdownRow(
                item = parent,
                isSelected = parent.selected,
                dropdownColors = dropdownColors,
                enabled = parent.enabled,
                hasSubmenu = true,
                submenuExpanded = true,
                submenuHeader = true,
                pressHandoff = true,
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
                    onClick = { onLeafSelected(child) },
                )
            }
        }
    }
}

/** The smallest list that shows group dividers (COUI `COUIPopupListWindow.GROUP_MIN_ITEMS`). */
private const val GROUP_MIN_ITEMS = 3

private var deeperChildrenWarned = false
