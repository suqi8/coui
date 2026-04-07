package com.suqi8.coui.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.suqi8.coui.kmp.theme.COUITheme
import com.suqi8.coui.kmp.utils.BackHandler
import com.suqi8.coui.kmp.utils.MiuixPopupUtils.Companion.PopupLayout
import com.suqi8.coui.kmp.utils.getWindowSize
import kotlin.math.roundToInt

@Composable
fun ListPopup(
    show: MutableState<Boolean>,
    popupModifier: Modifier = Modifier,
    popupPositionProvider: PopupPositionProvider = ListPopupDefaults.DropdownPositionProvider,
    alignment: PopupPositionProvider.Align = PopupPositionProvider.Align.Right,
    enableWindowDim: Boolean = false,
    shadowElevation: Dp = 8.dp,
    onDismissRequest: (() -> Unit)? = null,
    maxHeight: Dp? = null,
    minWidth: Dp = 178.dp,
    cornerRadius: Dp = 12.dp,
    content: @Composable () -> Unit
) {
    if (!show.value) return

    val windowSize by rememberUpdatedState(getWindowSize())
    var parentBounds by remember { mutableStateOf(IntRect.Zero) }

    Layout(
        modifier = Modifier
            .onGloballyPositioned { childCoordinates ->
                childCoordinates.parentLayoutCoordinates?.let { parentLayoutCoordinates ->
                    val positionInWindow = parentLayoutCoordinates.positionInWindow()
                    parentBounds = IntRect(
                        left = positionInWindow.x.toInt(),
                        top = positionInWindow.y.toInt(),
                        right = positionInWindow.x.toInt() + parentLayoutCoordinates.size.width,
                        bottom = positionInWindow.y.toInt() + parentLayoutCoordinates.size.height
                    )
                }
            }
    ) { _, _ -> layout(0, 0) {} }
    if (parentBounds == IntRect.Zero) return

    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val displayCutout = WindowInsets.displayCutout.asPaddingValues()
    val statusBars = WindowInsets.statusBars.asPaddingValues()
    val navigationBars = WindowInsets.navigationBars.asPaddingValues()
    val captionBar = WindowInsets.captionBar.asPaddingValues()

    val popupMargin = remember(windowSize, density) {
        with(density) {
            IntRect(
                left = popupPositionProvider.getMargins().calculateLeftPadding(layoutDirection).roundToPx(),
                top = popupPositionProvider.getMargins().calculateTopPadding().roundToPx(),
                right = popupPositionProvider.getMargins().calculateRightPadding(layoutDirection).roundToPx(),
                bottom = popupPositionProvider.getMargins().calculateBottomPadding().roundToPx()
            )
        }
    }

    val windowBounds = remember(windowSize, density) {
        with(density) {
            IntRect(
                left = displayCutout.calculateLeftPadding(layoutDirection).roundToPx(),
                top = statusBars.calculateTopPadding().roundToPx(),
                right = windowSize.width - displayCutout.calculateRightPadding(layoutDirection).roundToPx(),
                bottom = windowSize.height - navigationBars.calculateBottomPadding()
                    .roundToPx() - captionBar.calculateBottomPadding().roundToPx()
            )
        }
    }

    val transformOrigin = remember(windowSize, alignment, density) {
        val xInWindow = when (alignment) {
            PopupPositionProvider.Align.Right,
            PopupPositionProvider.Align.TopRight,
            PopupPositionProvider.Align.BottomRight -> parentBounds.right - popupMargin.right - with(density) { 64.dp.roundToPx() }

            else -> parentBounds.left + popupMargin.left + with(density) { 64.dp.roundToPx() }
        }
        val yInWindow = parentBounds.top + parentBounds.height / 2 - with(density) { 56.dp.roundToPx() }
        safeTransformOrigin(
            xInWindow / windowSize.width.toFloat(),
            yInWindow / windowSize.height.toFloat()
        )
    }

    PopupLayout(
        visible = show,
        enableWindowDim = enableWindowDim,
        transformOrigin = { transformOrigin },
    ) {
        val shape = remember { RoundedCornerShape(cornerRadius) }
        val elevationPx = with(density) { shadowElevation.toPx() }

        Box(
            modifier = popupModifier
                .pointerInput(onDismissRequest) {
                    detectTapGestures(
                        onTap = { onDismissRequest?.invoke() }
                    )
                }
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(
                        constraints.copy(
                            minWidth = if (minWidth.roundToPx() <= windowSize.width) minWidth.roundToPx() else windowSize.width,
                            minHeight = if (50.dp.roundToPx() <= windowSize.height) 50.dp.roundToPx() else windowSize.height,
                            maxHeight = maxHeight?.roundToPx()?.coerceAtLeast(50.dp.roundToPx())
                                ?: (windowBounds.height - popupMargin.top - popupMargin.bottom).coerceAtLeast(
                                    50.dp.roundToPx()
                                ),
                            maxWidth = if (minWidth.roundToPx() <= windowSize.width) windowSize.width else minWidth.roundToPx()
                        )
                    )
                    val measuredSize = IntSize(placeable.width, placeable.height)

                    val calculatedOffset = popupPositionProvider.calculatePosition(
                        parentBounds,
                        windowBounds,
                        layoutDirection,
                        measuredSize,
                        popupMargin,
                        alignment
                    )

                    layout(constraints.maxWidth, constraints.maxHeight) {
                        placeable.place(calculatedOffset)
                    }
                }
        ) {
            Box(
                modifier = Modifier
                    .graphicsLayer(
                        clip = true,
                        shape = shape,
                        shadowElevation = elevationPx
                    )
                    .background(COUITheme.colorScheme.surface)
            ) {
                content()
            }
        }
    }

    BackHandler(enabled = show.value) {
        onDismissRequest?.invoke()
    }
}

@Composable
fun ListPopupColumn(
    minWidth: Dp = 178.dp,
    maxWidth: Dp = 288.dp,
    onDragHover: (index: Int) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    onPressedIndexChange: (index: Int) -> Unit,
    onTap: (index: Int) -> Unit,
    content: @Composable () -> Unit
) {
    val scrollState = rememberScrollState()
    val currentContent by rememberUpdatedState(content)
    val itemBoundaries = remember { mutableListOf<IntRange>() }
    val hapticFeedback = LocalHapticFeedback.current

    val pressAndTapDetector = Modifier.pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                val down = awaitFirstDown(requireUnconsumed = false)
                val index = findIndex(down.position.y, scrollState.value, itemBoundaries)

                if (index == -1) continue

                onPressedIndexChange(index)

                val up = waitForUpOrCancellation()

                if (up != null) {
                    onTap(index)
                }

                onPressedIndexChange(-1)
            }
        }
    }

    val longPressDragDetector = Modifier.pointerInput(Unit) {
        detectDragGesturesAfterLongPress(
            onDragStart = { offset ->
                val index = findIndex(offset.y, scrollState.value, itemBoundaries)
                if (index != -1) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onPressedIndexChange(-1)
                    onDragHover(index)
                }
            },
            onDrag = { change, _ ->
                val index = findIndex(change.position.y, scrollState.value, itemBoundaries)
                onDragHover(index)
            },
            onDragEnd = { onDragEnd() },
            onDragCancel = { onDragCancel() }
        )
    }

    SubcomposeLayout(
        modifier = Modifier
            .verticalScroll(scrollState)
            .then(longPressDragDetector)
            .then(pressAndTapDetector)
    ) { constraints ->
        var listHeight = 0

        val minWidthPx = minWidth.roundToPx()
        val maxWidthPx = maxWidth.roundToPx()
        val tempConstraints = constraints.copy(
            minWidth = minWidthPx,
            maxWidth = maxWidthPx,
            minHeight = 0
        )

        val listWidth = subcompose("couiPopupListFake", currentContent).map {
            it.measure(tempConstraints)
        }.maxOfOrNull { it.width }?.coerceIn(minWidthPx, maxWidthPx) ?: minWidthPx

        val childConstraints = constraints.copy(minWidth = listWidth, maxWidth = listWidth, minHeight = 0)

        itemBoundaries.clear()
        var currentY = 0

        val placeables = subcompose("couiPopupListReal", currentContent).map {
            val placeable = it.measure(childConstraints)

            itemBoundaries.add(currentY until (currentY + placeable.height))
            currentY += placeable.height

            listHeight += placeable.height
            placeable
        }

        layout(listWidth, listHeight) {
            var yPosition = 0
            placeables.forEach {
                it.place(0, yPosition)
                yPosition += it.height
            }
        }
    }
}

private fun findIndex(y: Float, scrollY: Int, boundaries: List<IntRange>): Int {
    val yInList = y.roundToInt() + scrollY
    return boundaries.indexOfFirst { yInList in it }
}

interface PopupPositionProvider {
    fun calculatePosition(
        anchorBounds: IntRect,
        windowBounds: IntRect,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
        popupMargin: IntRect,
        alignment: Align
    ): IntOffset

    fun getMargins(): PaddingValues

    enum class Align {
        Left,
        Right,
        TopLeft,
        TopRight,
        BottomLeft,
        BottomRight
    }
}

object ListPopupDefaults {
    val DropdownPositionProvider = object : PopupPositionProvider {
        override fun calculatePosition(
            anchorBounds: IntRect,
            windowBounds: IntRect,
            layoutDirection: LayoutDirection,
            popupContentSize: IntSize,
            popupMargin: IntRect,
            alignment: PopupPositionProvider.Align
        ): IntOffset {
            val offsetX = if (alignment == PopupPositionProvider.Align.Right) {
                anchorBounds.right - popupContentSize.width - popupMargin.right
            } else {
                anchorBounds.left + popupMargin.left
            }
            val offsetY = if (windowBounds.bottom - anchorBounds.bottom > popupContentSize.height) {
                anchorBounds.bottom + popupMargin.bottom
            } else if (anchorBounds.top - windowBounds.top > popupContentSize.height) {
                anchorBounds.top - popupContentSize.height - popupMargin.top
            } else {
                anchorBounds.top + anchorBounds.height / 2 - popupContentSize.height / 2
            }
            return IntOffset(
                x = offsetX.coerceIn(
                    windowBounds.left,
                    (windowBounds.right - popupContentSize.width - popupMargin.right).coerceAtLeast(windowBounds.left)
                ),
                y = offsetY.coerceIn(
                    (windowBounds.top + popupMargin.top).coerceAtMost(windowBounds.bottom - popupContentSize.height - popupMargin.bottom),
                    windowBounds.bottom - popupContentSize.height - popupMargin.bottom
                )
            )
        }

        override fun getMargins(): PaddingValues {
            return PaddingValues(horizontal = 0.dp, vertical = 8.dp)
        }
    }
}

fun safeTransformOrigin(x: Float, y: Float): TransformOrigin {
    val safeX = if (x.isNaN() || x < 0f) 0f else x
    val safeY = if (y.isNaN() || y < 0f) 0f else y
    return TransformOrigin(safeX, safeY)
}