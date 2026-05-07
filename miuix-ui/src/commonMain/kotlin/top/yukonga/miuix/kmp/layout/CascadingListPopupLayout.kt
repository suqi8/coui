// Copyright 2026, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package top.yukonga.miuix.kmp.layout

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.anim.folmeSpring
import top.yukonga.miuix.kmp.basic.DropdownColors
import top.yukonga.miuix.kmp.basic.DropdownDefaults
import top.yukonga.miuix.kmp.basic.DropdownEntry
import top.yukonga.miuix.kmp.basic.DropdownImpl
import top.yukonga.miuix.kmp.basic.DropdownItem
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.ListPopupDefaults
import top.yukonga.miuix.kmp.basic.ListPopupLayoutInfo
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.basic.rememberListPopupLayoutInfo
import top.yukonga.miuix.kmp.theme.LocalDismissState
import top.yukonga.miuix.kmp.theme.MiuixTheme

internal val CascadingPopupCornerRadius = 16.dp

private const val MAIN_SPRING_DAMPING = 0.95f
private const val EXPAND_SPRING_DAMPING = 0.99f
private const val EXPAND_SPRING_RESPONSE = 0.45f
private const val COLLAPSE_SPRING_RESPONSE = 0.2f
private const val ARROW_EXPAND_SPRING_RESPONSE = 0.2f
private const val ARROW_COLLAPSE_SPRING_RESPONSE = 0.3f
private const val PRIMARY_SHRUNK_SCALE = 0.95f
internal const val ENTER_SCALE_FROM = 0.15f
internal const val ENTER_SCALE_RANGE = 1f - ENTER_SCALE_FROM

/** Internal shared layout for cascading list popups. Cascading depth is limited to 2. */
@Composable
internal fun CascadingListPopupLayout(
    show: Boolean,
    popupHost: @Composable (visible: Boolean, content: @Composable () -> Unit) -> Unit,
    entries: List<DropdownEntry>,
    onDismissRequest: () -> Unit,
    dropdownColors: DropdownColors,
    popupModifier: Modifier = Modifier,
    onDismissFinished: (() -> Unit)? = null,
    popupPositionProvider: PopupPositionProvider = ListPopupDefaults.DropdownPositionProvider,
    alignment: PopupPositionProvider.Align = PopupPositionProvider.Align.End,
    enableWindowDim: Boolean = true,
    maxHeight: Dp? = null,
    minWidth: Dp = 200.dp,
    collapseOnSelection: Boolean = true,
) {
    val internalVisible = remember { mutableStateOf(false) }
    val enterFraction = remember { Animatable(0f) }
    val enterAlpha = remember { Animatable(0f) }
    val dimProgress = remember { Animatable(0f) }

    var expandedItem by remember { mutableStateOf<DropdownItem?>(null) }
    // displayedItem stays non-null through the collapse animation so the secondary surface
    // remains subcomposed even after expandedItem flips to null.
    var displayedItem by remember { mutableStateOf<DropdownItem?>(null) }
    val expandFraction = remember { Animatable(0f) }
    val primaryScale = remember { Animatable(1f) }
    val maskAlpha = remember { Animatable(0f) }
    val arrowRotation = remember { Animatable(0f) }

    val layoutDirection = LocalLayoutDirection.current
    val arrowEndDeg = remember(layoutDirection) {
        if (layoutDirection == LayoutDirection.Ltr) -90f else 90f
    }
    val haptic = LocalHapticFeedback.current

    val currentOnDismiss by rememberUpdatedState(onDismissRequest)
    val currentOnDismissFinished by rememberUpdatedState(onDismissFinished)

    LaunchedEffect(show) {
        if (show) {
            internalVisible.value = true
            haptic.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
            launch { enterFraction.animateTo(1f, ListPopupDefaults.FractionAnimationSpec) }
            launch { enterAlpha.animateTo(1f, ListPopupDefaults.AlphaEnterAnimationSpec) }
            launch { dimProgress.animateTo(1f, ListPopupDefaults.DimEnterAnimationSpec) }
        } else {
            if (!internalVisible.value) return@LaunchedEffect
            expandedItem = null
            launch { enterFraction.animateTo(0f, ListPopupDefaults.FractionAnimationSpec) }
            launch { dimProgress.animateTo(0f, ListPopupDefaults.DimExitAnimationSpec) }
            enterAlpha.animateTo(0f, ListPopupDefaults.AlphaExitAnimationSpec)
            enterFraction.stop()
            dimProgress.stop()
            internalVisible.value = false
            currentOnDismissFinished?.invoke()
        }
    }

    LaunchedEffect(expandedItem) {
        val item = expandedItem
        if (item != null) displayedItem = item
        val target = if (item != null) 1f else 0f
        val mainSpec = if (target == 1f) {
            folmeSpring(EXPAND_SPRING_DAMPING, EXPAND_SPRING_RESPONSE)
        } else {
            folmeSpring<Float>(MAIN_SPRING_DAMPING, COLLAPSE_SPRING_RESPONSE)
        }
        launch {
            expandFraction.animateTo(target, mainSpec)
            // Clear displayedItem the moment the main spring settles; otherwise the slower
            // arrowRotation collapse would keep the secondary subcomposed past visibility.
            if (target == 0f) displayedItem = null
        }
        launch {
            primaryScale.animateTo(
                if (target == 1f) PRIMARY_SHRUNK_SCALE else 1f,
                mainSpec,
            )
        }
        launch { maskAlpha.animateTo(target, mainSpec) }
        launch {
            val arrowResponse = if (target == 1f) {
                ARROW_EXPAND_SPRING_RESPONSE
            } else {
                ARROW_COLLAPSE_SPRING_RESPONSE
            }
            arrowRotation.animateTo(
                target * arrowEndDeg,
                folmeSpring(MAIN_SPRING_DAMPING, arrowResponse),
            )
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

    var primarySize by remember { mutableStateOf(IntSize.Zero) }
    val layoutInfo = rememberListPopupLayoutInfo(
        alignment = alignment,
        popupPositionProvider = popupPositionProvider,
        parentBounds = parentBounds,
        popupContentSize = primarySize,
    )

    val anchorBoundsByItem = remember { mutableStateMapOf<DropdownItem, IntRect>() }

    var hostPositionInWindow by remember { mutableStateOf(IntOffset.Zero) }

    popupHost(internalVisible.value) {
        val backState = rememberNavigationEventState(currentInfo = NavigationEventInfo.None)
        NavigationBackHandler(
            state = backState,
            isBackEnabled = show,
            onBackCompleted = {
                if (expandedItem != null) {
                    expandedItem = null
                } else {
                    currentOnDismiss()
                }
            },
        )

        CompositionLocalProvider(LocalDismissState provides { currentOnDismiss() }) {
            val dimColor = MiuixTheme.colorScheme.windowDimming
            val maskColor = remember(dimColor) {
                dimColor.copy(alpha = (dimColor.alpha * 0.5f).coerceIn(0f, 1f))
            }
            val surfaceColor = MiuixTheme.colorScheme.surfaceContainer

            Box(modifier = Modifier.fillMaxSize()) {
                if (enableWindowDim) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer { alpha = dimProgress.value }
                            .background(dimColor),
                    )
                }
                Box(
                    modifier = popupModifier
                        .fillMaxSize()
                        .onGloballyPositioned { coords ->
                            val pos = coords.positionInWindow()
                            hostPositionInWindow = IntOffset(pos.x.toInt(), pos.y.toInt())
                        }
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = { currentOnDismiss() })
                        },
                ) {
                    CascadingMorphSubLayout(
                        entries = entries,
                        expandedItem = expandedItem,
                        displayedItem = displayedItem,
                        onExpand = { expandedItem = it },
                        onCollapseSecondary = { expandedItem = null },
                        onLeafSelected = { item ->
                            item.onClick?.invoke()
                            if (collapseOnSelection) {
                                expandedItem = null
                                currentOnDismiss()
                            }
                        },
                        getAnchorBounds = { anchorBoundsByItem[it] },
                        setAnchorBounds = { item, bounds ->
                            // Freeze the anchor while a secondary surface is on screen so the
                            // primary's scale animation doesn't drift the secondary's position
                            // through onGloballyPositioned.
                            if (displayedItem == null) {
                                anchorBoundsByItem[item] = bounds
                            }
                        },
                        onPrimarySizeChange = { primarySize = it },
                        windowBounds = layoutInfo.windowBounds,
                        popupMargin = layoutInfo.popupMargin,
                        parentBounds = parentBounds,
                        popupPositionProvider = popupPositionProvider,
                        alignment = alignment,
                        hostOriginInWindow = { hostPositionInWindow },
                        maxHeight = maxHeight,
                        minWidth = minWidth,
                        dropdownColors = dropdownColors,
                        enterFraction = { enterFraction.value },
                        enterAlpha = { enterAlpha.value },
                        primaryScale = { primaryScale.value },
                        maskAlpha = { maskAlpha.value },
                        expandFraction = { expandFraction.value },
                        arrowRotation = { arrowRotation.value },
                        layoutInfo = layoutInfo,
                        layoutDirection = layoutDirection,
                        surfaceColor = surfaceColor,
                        maskColor = maskColor,
                    )
                }
            }
        }
    }
}

private const val SLOT_PRIMARY = "primary"
private const val SLOT_SECONDARY = "secondary"

@Suppress("LongParameterList")
@Composable
private fun CascadingMorphSubLayout(
    entries: List<DropdownEntry>,
    expandedItem: DropdownItem?,
    displayedItem: DropdownItem?,
    onExpand: (DropdownItem) -> Unit,
    onCollapseSecondary: () -> Unit,
    onLeafSelected: (DropdownItem) -> Unit,
    getAnchorBounds: (DropdownItem) -> IntRect?,
    setAnchorBounds: (DropdownItem, IntRect) -> Unit,
    onPrimarySizeChange: (IntSize) -> Unit,
    windowBounds: IntRect,
    popupMargin: IntRect,
    parentBounds: IntRect,
    popupPositionProvider: PopupPositionProvider,
    alignment: PopupPositionProvider.Align,
    hostOriginInWindow: () -> IntOffset,
    maxHeight: Dp?,
    minWidth: Dp,
    dropdownColors: DropdownColors,
    enterFraction: () -> Float,
    enterAlpha: () -> Float,
    primaryScale: () -> Float,
    maskAlpha: () -> Float,
    expandFraction: () -> Float,
    arrowRotation: () -> Float,
    layoutInfo: ListPopupLayoutInfo,
    layoutDirection: LayoutDirection,
    surfaceColor: Color,
    maskColor: Color,
) {
    SubcomposeLayout(modifier = Modifier.fillMaxSize()) { constraints ->
        val resolvedMaxHeightPx = maxHeight?.roundToPx()?.coerceAtLeast(50.dp.roundToPx())
            ?: (windowBounds.height - popupMargin.top - popupMargin.bottom)
                .coerceAtLeast(50.dp.roundToPx())
        val minWidthPx = minWidth.roundToPx().coerceAtMost(constraints.maxWidth)
        val primaryConstraints = Constraints(
            minWidth = minWidthPx,
            maxWidth = constraints.maxWidth,
            minHeight = 0,
            maxHeight = resolvedMaxHeightPx.coerceAtMost(constraints.maxHeight),
        )

        // Secondary subcomposition tracks [displayedItem] (kept non-null until the spring
        // collapse animation finishes) so the morphing surface stays alive throughout the
        // collapse. The primary's mask/chevron, in contrast, react to [expandedItem] —
        // see CascadingPrimaryContent below — so user input feels instant.
        val activeExpanded = displayedItem
        val anchorRect = activeExpanded?.let { getAnchorBounds(it) }
        val (anchorPaddingTopPx, anchorPaddingBottomPx) = if (activeExpanded != null) {
            computeAnchorPaddingPx(activeExpanded, entries)
        } else {
            0 to 0
        }

        val primaryPlaceables = subcompose(SLOT_PRIMARY) {
            CascadingPrimaryContent(
                entries = entries,
                expandedItem = expandedItem,
                onExpand = onExpand,
                onLeafSelected = onLeafSelected,
                onAnchorBounds = setAnchorBounds,
                enterFraction = enterFraction,
                enterAlpha = enterAlpha,
                popupLayoutPosition = layoutInfo.popupLayoutPosition,
                transformOrigin = layoutInfo.localTransformOrigin,
                surfaceColor = surfaceColor,
                dropdownColors = dropdownColors,
                primaryScale = primaryScale,
                maskAlpha = maskAlpha,
                maskColor = maskColor,
                onCollapseSecondary = onCollapseSecondary,
            )
        }.map { it.measure(primaryConstraints) }

        val primaryWidth = primaryPlaceables.maxOfOrZero { it.width }
        val primaryHeight = primaryPlaceables.sumOfOrZero { it.height }
        val primaryMeasuredSize = IntSize(primaryWidth, primaryHeight)
        onPrimarySizeChange(primaryMeasuredSize)

        val primaryOffsetWindow = if (primaryMeasuredSize == IntSize.Zero) {
            IntOffset.Zero
        } else {
            popupPositionProvider.calculatePosition(
                parentBounds,
                windowBounds,
                layoutDirection,
                primaryMeasuredSize,
                popupMargin,
                alignment,
            )
        }

        // Probe the secondary's natural size, derive the union rect (primary ∪ secondary),
        // then measure the real secondary at union size so its content can slide from the
        // anchor row to its final position inside a fixed-size container.
        val probeConstraints = primaryConstraints.copy(
            minWidth = primaryWidth.coerceAtMost(primaryConstraints.maxWidth),
            maxHeight = resolvedMaxHeightPx.coerceAtMost(constraints.maxHeight),
        )

        data class SecondaryLayout(
            val placeables: List<Placeable>,
            val unionRect: IntRect,
        )

        val secondaryLayout: SecondaryLayout? =
            if (activeExpanded != null && anchorRect != null) {
                val measured = subcompose(SLOT_SECONDARY + "_probe") {
                    Box(propagateMinConstraints = true) {
                        SecondaryProbe(
                            triggerItem = activeExpanded,
                            children = activeExpanded.children.orEmpty(),
                            dropdownColors = dropdownColors,
                        )
                    }
                }.firstOrNull()?.measure(probeConstraints)
                val secondarySize = if (measured != null) {
                    IntSize(measured.width, measured.height)
                } else {
                    IntSize.Zero
                }
                if (secondarySize.width > 0 && secondarySize.height > 0 && primaryMeasuredSize != IntSize.Zero) {
                    val secondaryRect = computeSecondaryRect(
                        anchor = anchorRect,
                        secondarySize = secondarySize,
                        windowBounds = windowBounds,
                        layoutDirection = layoutDirection,
                    )
                    val primaryRect = IntRect(
                        left = primaryOffsetWindow.x,
                        top = primaryOffsetWindow.y,
                        right = primaryOffsetWindow.x + primaryMeasuredSize.width,
                        bottom = primaryOffsetWindow.y + primaryMeasuredSize.height,
                    )
                    val unionRect = unionOf(primaryRect, secondaryRect)
                    val secondaryLocalInUnion = IntRect(
                        left = secondaryRect.left - unionRect.left,
                        top = secondaryRect.top - unionRect.top,
                        right = secondaryRect.right - unionRect.left,
                        bottom = secondaryRect.bottom - unionRect.top,
                    )
                    val anchorLocalInUnion = IntRect(
                        left = anchorRect.left - unionRect.left,
                        top = anchorRect.top - unionRect.top,
                        right = anchorRect.right - unionRect.left,
                        bottom = anchorRect.bottom - unionRect.top,
                    )
                    val unionConstraints = Constraints.fixed(unionRect.width, unionRect.height)
                    val placeables = subcompose(SLOT_SECONDARY) {
                        CascadingSecondaryContent(
                            triggerItem = activeExpanded,
                            unionSize = IntSize(unionRect.width, unionRect.height),
                            secondaryLocalInUnion = secondaryLocalInUnion,
                            anchorLocalInUnion = anchorLocalInUnion,
                            anchorPaddingTopPx = anchorPaddingTopPx,
                            anchorPaddingBottomPx = anchorPaddingBottomPx,
                            secondaryContentMaxHeight = resolvedMaxHeightPx,
                            enterFraction = enterFraction,
                            enterAlpha = enterAlpha,
                            popupLayoutPosition = layoutInfo.popupLayoutPosition,
                            transformOrigin = layoutInfo.localTransformOrigin,
                            expandFraction = expandFraction,
                            arrowRotation = arrowRotation,
                            onCollapseSecondary = onCollapseSecondary,
                            onLeafSelected = onLeafSelected,
                            dropdownColors = dropdownColors,
                            surfaceColor = surfaceColor,
                        )
                    }.map { it.measure(unionConstraints) }
                    SecondaryLayout(placeables, unionRect)
                } else {
                    null
                }
            } else {
                null
            }

        val host = hostOriginInWindow()
        layout(constraints.maxWidth, constraints.maxHeight) {
            val primaryDxLocal = primaryOffsetWindow.x - host.x
            val primaryDyLocal = primaryOffsetWindow.y - host.y
            var py = primaryDyLocal
            primaryPlaceables.forEach { placeable ->
                placeable.place(primaryDxLocal, py)
                py += placeable.height
            }
            secondaryLayout?.let { (placeables, unionRect) ->
                val ux = unionRect.left - host.x
                val uy = unionRect.top - host.y
                placeables.forEach { it.place(ux, uy) }
            }
        }
    }
}

private fun unionOf(a: IntRect, b: IntRect): IntRect = IntRect(
    left = minOf(a.left, b.left),
    top = minOf(a.top, b.top),
    right = maxOf(a.right, b.right),
    bottom = maxOf(a.bottom, b.bottom),
)

/** First-pass measurement-only stand-in for [CascadingSecondaryContent], used to obtain the
 *  natural secondary size before its window rect (and the union) can be derived. */
@Composable
private fun SecondaryProbe(
    triggerItem: DropdownItem,
    children: List<DropdownItem>,
    dropdownColors: DropdownColors,
) {
    Box {
        ListPopupColumn {
            MorphHeaderRow(
                triggerItem = triggerItem,
                arrowRotation = { 0f },
                expandFraction = { 1f },
                anchorHeightPx = 0,
                anchorPaddingTopPx = 0,
                dropdownColors = dropdownColors,
                onClick = {},
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                thickness = 1.dp,
            )
            children.forEachIndexed { index, child ->
                key(child) {
                    Box(propagateMinConstraints = true) {
                        DropdownImpl(
                            item = child,
                            optionSize = children.size,
                            isSelected = false,
                            index = index,
                            dropdownColors = dropdownColors,
                            enabled = child.enabled,
                            hasSubmenu = false,
                            onSelectedIndexChange = {},
                        )
                    }
                }
            }
        }
    }
}

internal fun computeSecondaryRect(
    anchor: IntRect,
    secondarySize: IntSize,
    windowBounds: IntRect,
    layoutDirection: LayoutDirection,
): IntRect {
    val ltr = layoutDirection == LayoutDirection.Ltr
    // Leading-edge align (LTR: anchor.left, RTL: anchor.right); fall back to the opposite
    // edge if the trailing side overflows the window, then clamp.
    val left = if (ltr) {
        val leftAligned = anchor.left
        when {
            leftAligned + secondarySize.width <= windowBounds.right -> leftAligned

            anchor.right - secondarySize.width >= windowBounds.left ->
                windowBounds.right - secondarySize.width

            else -> windowBounds.left
        }
    } else {
        val rightAligned = anchor.right - secondarySize.width
        when {
            rightAligned >= windowBounds.left -> rightAligned
            anchor.left + secondarySize.width <= windowBounds.right -> anchor.left
            else -> (windowBounds.right - secondarySize.width).coerceAtLeast(windowBounds.left)
        }
    }
    val top = when {
        anchor.top + secondarySize.height <= windowBounds.bottom -> anchor.top
        anchor.bottom - secondarySize.height >= windowBounds.top -> anchor.bottom - secondarySize.height
        else -> (windowBounds.bottom - secondarySize.height).coerceAtLeast(windowBounds.top)
    }
    return IntRect(
        left = left,
        top = top,
        right = left + secondarySize.width,
        bottom = top + secondarySize.height,
    )
}

/** Trigger row's vertical paddings (first/last vs middle), used by [MorphHeaderRow] to
 *  interpolate its own padding during expansion. */
private fun Density.computeAnchorPaddingPx(
    item: DropdownItem,
    entries: List<DropdownEntry>,
): Pair<Int, Int> {
    entries.forEach { entry ->
        val idx = entry.items.indexOf(item)
        if (idx != -1) {
            val size = entry.items.size
            val top = if (idx == 0) {
                DropdownDefaults.FirstLastVerticalPadding.roundToPx()
            } else {
                DropdownDefaults.MiddleVerticalPadding.roundToPx()
            }
            val bottom = if (idx == size - 1) {
                DropdownDefaults.FirstLastVerticalPadding.roundToPx()
            } else {
                DropdownDefaults.MiddleVerticalPadding.roundToPx()
            }
            return top to bottom
        }
    }
    return 0 to 0
}

private inline fun <T> List<T>.maxOfOrZero(selector: (T) -> Int): Int = if (isEmpty()) 0 else maxOf(selector)

private inline fun <T> List<T>.sumOfOrZero(selector: (T) -> Int): Int = if (isEmpty()) 0 else sumOf(selector)
