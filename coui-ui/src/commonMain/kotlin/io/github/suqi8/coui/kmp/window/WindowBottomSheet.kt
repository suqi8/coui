// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.window

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.Dialog
import io.github.suqi8.coui.kmp.layout.BottomSheetContentLayout
import io.github.suqi8.coui.kmp.layout.BottomSheetDefaults
import io.github.suqi8.coui.kmp.theme.LocalDismissState
import io.github.suqi8.coui.kmp.utils.RemovePlatformDialogDefaultEffects
import io.github.suqi8.coui.kmp.utils.WindowNavigationEventScope
import io.github.suqi8.coui.kmp.utils.platformDialogProperties

/**
 * A COUI panel (`COUIBottomSheetDialog`) rendered at window level without `Scaffold`. On windows
 * narrower than [BottomSheetDefaults.LargeScreenWidthThreshold] it slides up from the bottom edge;
 * from that width on it floats centered (all corners rounded, scale + fade entrance). The whole
 * panel can be dragged; scrollable content drags it once scrolled to the top.
 *
 * Use [LocalDismissState] inside `content` to request dismissal from inner composables.
 *
 * @param show Whether the [WindowBottomSheet] is shown.
 * @param modifier The modifier to be applied to the [WindowBottomSheet].
 * @param title Optional title to display at the top of the [WindowBottomSheet].
 * @param startAction Optional [Composable] to display on the start side of the title (e.g. a close button).
 * @param endAction Optional [Composable] to display on the end side of the title (e.g. a submit button).
 * @param backgroundColor The background color of the [WindowBottomSheet].
 * @param enableWindowDim Whether to dim the window behind the [WindowBottomSheet].
 * @param cornerRadius The corner radius of the [WindowBottomSheet].
 * @param sheetMaxWidth The maximum width of the [WindowBottomSheet], on top of the COUI grid width
 *   and the [BottomSheetDefaults.PanelMaxWidth] cap.
 * @param onDismissRequest Will called when the user tries to dismiss the Dialog by clicking outside or pressing the back button.
 * @param onDismissFinished The callback when the [WindowBottomSheet] is completely dismissed.
 * @param outsideMargin The margin outside the [WindowBottomSheet].
 * @param insideMargin The margin inside the [WindowBottomSheet]. COUI panels have none; the content
 *   sets its own margins.
 * @param defaultWindowInsetsPadding Whether to apply default window insets padding.
 * @param dragHandleColor The color of the drag handle at the top.
 * @param showDragHandle Whether to show the drag handle. Hidden by default, as COUI only shows it on
 *   handle panels; a 10dp gap is kept at the top either way.
 * @param handlePanel Whether this is a COUI handle panel (`couiIsHandlePanel`): it shows the drag
 *   handle, fills the maximum height and rests half-expanded (portrait) or collapsed to the peek
 *   height (landscape), settling between collapsed, half-expanded and expanded after a drag. It stays
 *   bottom-anchored on large screens.
 * @param showInMaxHeight Whether the panel takes the maximum height instead of wrapping its content
 *   (`setIsShowInMaxHeight`). The content then fills the space below the title.
 * @param allowDismiss Whether to allow dismissing the sheet via drag, back gesture or outside taps.
 *   When false, a drag down follows the finger only until the 82dp peek height is left and the panel
 *   then rests collapsed or springs back, as COUI does for a non-cancelable panel.
 * @param dismissOnClickOutside Whether tapping outside the panel dismisses it
 *   (`setCanceledOnTouchOutside`). Only applies while [allowDismiss] is true.
 * @param draggable Whether the panel follows drag gestures (`setDraggable`).
 * @param enablePullUp Whether the panel can be pulled up to 28dp above its resting position with
 *   resistance (`setCanPullUp`).
 * @param enableNestedScroll Whether to enable nested scrolling for the content.
 * @param backgroundModifier A [Modifier] applied inside the sheet's corner clip, under the content
 *   and over [backgroundColor]. Use it to paint the COUI 17 blurred panel background, e.g. with
 *   coui-blur's `Modifier.couiMaterialBlur(backdrop, MaterialBlurDefaults.bottomSheet(isDark))`, and
 *   pass a transparent [backgroundColor] alongside it.
 * @param materialStroke Whether to draw the COUI 17 material stroke (edge lighting + inner shadow)
 *   `COUIBottomSheetDialog` adds once the panel background is blurred. Needs runtime shaders.
 * @param content The [Composable] content of the [WindowBottomSheet].
 */
@Composable
fun WindowBottomSheet(
    show: Boolean,
    modifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    title: String? = null,
    startAction: @Composable (() -> Unit)? = null,
    endAction: @Composable (() -> Unit)? = null,
    backgroundColor: Color = BottomSheetDefaults.backgroundColor(),
    enableWindowDim: Boolean = true,
    cornerRadius: Dp = BottomSheetDefaults.cornerRadius,
    sheetMaxWidth: Dp = BottomSheetDefaults.maxWidth,
    onDismissRequest: (() -> Unit)? = null,
    onDismissFinished: (() -> Unit)? = null,
    outsideMargin: DpSize = BottomSheetDefaults.outsideMargin,
    insideMargin: DpSize = BottomSheetDefaults.insideMargin,
    defaultWindowInsetsPadding: Boolean = true,
    dragHandleColor: Color = BottomSheetDefaults.dragHandleColor(),
    showDragHandle: Boolean = false,
    handlePanel: Boolean = false,
    showInMaxHeight: Boolean = false,
    allowDismiss: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    draggable: Boolean = true,
    enablePullUp: Boolean = true,
    enableNestedScroll: Boolean = true,
    materialStroke: Boolean = false,
    content: @Composable () -> Unit,
) {
    val statusBarsPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val captionBarPadding = WindowInsets.captionBar.asPaddingValues().calculateTopPadding()
    val displayCutoutPadding = WindowInsets.displayCutout.asPaddingValues().calculateTopPadding()
    val safeTopInset = remember(statusBarsPadding, captionBarPadding, displayCutoutPadding) {
        maxOf(statusBarsPadding, captionBarPadding, displayCutoutPadding)
    }

    val currentOnDismissRequest = rememberUpdatedState(onDismissRequest)

    BottomSheetContentLayout(
        show = show,
        backgroundColor = backgroundColor,
        cornerRadius = cornerRadius,
        sheetMaxWidth = sheetMaxWidth,
        outsideMargin = outsideMargin,
        insideMargin = insideMargin,
        dragHandleColor = dragHandleColor,
        popupHost = { visible, hostContent ->
            if (visible) {
                Dialog(
                    onDismissRequest = {
                        if (allowDismiss) {
                            currentOnDismissRequest.value?.invoke()
                        }
                    },
                    properties = platformDialogProperties(),
                ) {
                    RemovePlatformDialogDefaultEffects()
                    WindowNavigationEventScope {
                        hostContent()
                    }
                }
            }
        },
        modifier = modifier,
        title = title,
        startAction = startAction,
        endAction = endAction,
        enableWindowDim = enableWindowDim,
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        defaultWindowInsetsPadding = defaultWindowInsetsPadding,
        showDragHandle = showDragHandle,
        handlePanel = handlePanel,
        showInMaxHeight = showInMaxHeight,
        allowDismiss = allowDismiss,
        dismissOnClickOutside = dismissOnClickOutside,
        draggable = draggable,
        enablePullUp = enablePullUp,
        enableNestedScroll = enableNestedScroll,
        topInset = safeTopInset,
        backgroundModifier = backgroundModifier,
        materialStroke = materialStroke,
        content = {
            CompositionLocalProvider(
                LocalDismissState provides {
                    currentOnDismissRequest.value?.invoke()
                },
            ) {
                content()
            }
        },
    )
}
