// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0
//
// This file is a general-purpose popup implementation.

package com.suqi8.coui.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
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
import kotlin.math.min

/**
 * A popup with a list of items.
 */
@Composable
fun ListPopup(
    show: MutableState<Boolean>,
    popupModifier: Modifier = Modifier,
    popupPositionProvider: PopupPositionProvider = ListPopupDefaults.DropdownPositionProvider,
    alignment: PopupPositionProvider.Align = PopupPositionProvider.Align.Right,
    enableWindowDim: Boolean = true,
    shadowElevation: Dp = 8.dp,
    onDismissRequest: (() -> Unit)? = null,
    maxHeight: Dp? = null,
    // COUI visual parameters are now the defaults
    minWidth: Dp = 178.dp,
    cornerRadius: Dp = 12.dp,
    verticalMargin: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    if (!show.value) return

    val windowSize = getWindowSize()
    var parentBounds by remember { mutableStateOf(IntRect.Zero) }

    val couiPositionProvider = remember(verticalMargin) {
        ListPopupDefaults.getCouiDropdownPositionProvider(verticalMargin)
    }

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

    val popupMargin by remember(windowSize, layoutDirection, density) {
        derivedStateOf {
            with(density) {
                IntRect(
                    left = couiPositionProvider.getMargins().calculateLeftPadding(layoutDirection).roundToPx(),
                    top = couiPositionProvider.getMargins().calculateTopPadding().roundToPx(),
                    right = couiPositionProvider.getMargins().calculateRightPadding(layoutDirection).roundToPx(),
                    bottom = couiPositionProvider.getMargins().calculateBottomPadding().roundToPx()
                )
            }
        }
    }

    val windowBounds by remember(windowSize, layoutDirection, displayCutout, statusBars, navigationBars, captionBar, density) {
        derivedStateOf {
            with(density) {
                IntRect(
                    left = displayCutout.calculateLeftPadding(layoutDirection).roundToPx(),
                    top = statusBars.calculateTopPadding().roundToPx(),
                    right = windowSize.width - displayCutout.calculateRightPadding(layoutDirection).roundToPx(),
                    bottom = windowSize.height - navigationBars.calculateBottomPadding().roundToPx()
                            - captionBar.calculateBottomPadding().roundToPx()
                )
            }
        }
    }

    // ... (TransformOrigin logic can be kept for smooth animations)

    PopupLayout(
        visible = show,
        enableWindowDim = enableWindowDim,
    ) {
        Box(
            modifier = popupModifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { onDismissRequest?.invoke() }
                    )
                }
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(
                        constraints.copy(
                            minWidth = minWidth.roundToPx().coerceAtMost(windowSize.width),
                            minHeight = 0,
                            maxHeight = maxHeight?.roundToPx()
                                ?: (windowBounds.height - popupMargin.top - popupMargin.bottom)
                        )
                    )
                    val measuredSize = IntSize(placeable.width, placeable.height)

                    val calculatedOffset = couiPositionProvider.calculatePosition(
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
            val shape = RoundedCornerShape(cornerRadius)
            Box(
                modifier = Modifier
                    .shadow(elevation = shadowElevation, shape = shape)
                    .background(COUITheme.colorScheme.surface, shape)
                    .graphicsLayer(clip = true, shape = shape)
            ) {
                content()
            }
        }
    }

    BackHandler(enabled = show.value) {
        onDismissRequest?.invoke()
    }
}


/**
 * A column that automatically aligns the width to the widest item
 */
@Composable
fun ListPopupColumn(
    content: @Composable () -> Unit
) {
    val scrollState = rememberScrollState()

    SubcomposeLayout(
        modifier = Modifier.verticalScroll(scrollState)
    ) { constraints ->
        val maxWidth = 232.dp.roundToPx() // From coui_popup_list_window_max_width
        val tempConstraints = constraints.copy(minWidth = 0, minHeight = 0, maxWidth = maxWidth)

        val listWidth = subcompose("couiPopupListMeasurer", content).maxOfOrNull {
            it.measure(tempConstraints).width
        } ?: 0

        val childConstraints = constraints.copy(minWidth = listWidth, maxWidth = listWidth, minHeight = 0)

        var listHeight = 0
        val placeables = subcompose("couiPopupListBuilder", content).map {
            val placeable = it.measure(childConstraints)
            listHeight += placeable.height
            placeable
        }
        layout(listWidth, min(constraints.maxHeight, listHeight)) {
            var currentY = 0
            placeables.forEach {
                it.place(0, currentY)
                currentY += it.height
            }
        }
    }
}


@Stable
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
        Left, Right
    }
}

object ListPopupDefaults {
    val DropdownPositionProvider = getCouiDropdownPositionProvider(8.dp)

    fun getCouiDropdownPositionProvider(verticalMargin: Dp) = object : PopupPositionProvider {
        override fun calculatePosition(
            anchorBounds: IntRect,
            windowBounds: IntRect,
            layoutDirection: LayoutDirection,
            popupContentSize: IntSize,
            popupMargin: IntRect,
            alignment: PopupPositionProvider.Align
        ): IntOffset {
            val offsetX = if (alignment == PopupPositionProvider.Align.Right) {
                anchorBounds.right - popupContentSize.width
            } else {
                anchorBounds.left
            }

            val spaceBelow = windowBounds.bottom - anchorBounds.bottom
            val spaceAbove = anchorBounds.top - windowBounds.top

            val offsetY = if (spaceBelow >= popupContentSize.height || spaceBelow >= spaceAbove) {
                anchorBounds.bottom + popupMargin.bottom
            } else {
                anchorBounds.top - popupContentSize.height - popupMargin.top
            }
            return IntOffset(
                x = offsetX.coerceIn(windowBounds.left, windowBounds.right - popupContentSize.width),
                y = offsetY.coerceIn(windowBounds.top, windowBounds.bottom - popupContentSize.height)
            )
        }

        override fun getMargins(): PaddingValues {
            return PaddingValues(vertical = verticalMargin)
        }
    }
}
