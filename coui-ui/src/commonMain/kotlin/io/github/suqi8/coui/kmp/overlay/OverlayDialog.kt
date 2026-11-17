// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.overlay

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import io.github.suqi8.coui.kmp.layout.DialogContentLayout
import io.github.suqi8.coui.kmp.layout.DialogDefaults
import io.github.suqi8.coui.kmp.utils.COUIPopupUtils.Companion.DialogLayout

/**
 * A dialog with a title, a summary, and other contents.
 *
 * @param show Whether the [OverlayDialog] is shown.
 * @param modifier The modifier to be applied to the [OverlayDialog].
 * @param title The title of the [OverlayDialog].
 * @param titleColor The color of the title.
 * @param summary The summary of the [OverlayDialog].
 * @param summaryColor The color of the summary.
 * @param backgroundColor The background color of the [OverlayDialog].
 * @param enableWindowDim Whether to enable window dimming when the [OverlayDialog] is shown.
 * @param onDismissRequest Will called when the user tries to dismiss the Dialog by clicking outside or pressing the back button.
 * @param onDismissFinished The callback when the [OverlayDialog] is completely dismissed.
 * @param outsideMargin The margin outside the [OverlayDialog].
 * @param insideMargin The margin for the built-in texts: width is the horizontal padding of
 *   the title and summary, height is the top padding above the title. The [content] slot is
 *   left unpadded so COUI-style button bars can span the full panel width.
 * @param defaultWindowInsetsPadding Whether to apply default window insets padding to the [OverlayDialog].
 * @param renderInRootScaffold Whether to render the dialog in the root (outermost) Scaffold.
 *   When true (default), the dialog covers the full screen. When false, it renders within the
 *   current Scaffold's bounds.
 * @param maxWidth The maximum width of the [OverlayDialog].
 * @param largeScreen Optional override for the large-screen presentation (centered scale/fade
 *   instead of bottom slide-in). If null, detected from the window size.
 * @param cornerRadius Optional corner radius override. If null, [DialogDefaults.CornerRadius]
 *   is used.
 * @param backgroundModifier A [Modifier] applied inside the panel's corner clip, under the content
 *   and over [backgroundColor]. Use it to paint the COUI 17 blurred window background, e.g. with
 *   coui-blur's `Modifier.couiMaterialBlur(backdrop, MaterialBlurDefaults.dialog(isDark))`, and pass
 *   a transparent [backgroundColor] alongside it.
 * @param materialStroke Whether to draw the COUI 17 material stroke (edge lighting + inner shadow)
 *   `COUIAlertDialogRootLayout` adds once the window background is blurred. Needs runtime shaders.
 *   It also marks the window as blurred for a [io.github.suqi8.coui.kmp.layout.DialogButtonBar]
 *   inside, whose buttons then turn material (`COUIButtonBarLayout.setBlurBackgroundWindow`).
 * @param customContent The custom view slot between the summary and [content] (COUI `customPanel`,
 *   `COUIAlertDialogBuilder.setView`), padded by [DialogDefaults.CustomContentPaddingBottom] below.
 *   Put custom views here rather than in [content]: with it the summary drops its bottom padding to
 *   4dp and a title-only dialog skips the extra 16dp below the title, as COUI's
 *   `initContentSpacing` does. Like the summary it shrinks and must scroll by itself when the panel
 *   runs out of height, so [content] (the button bar) stays in view.
 * @param content The [Composable] content of the [OverlayDialog], typically a
 *   [io.github.suqi8.coui.kmp.layout.DialogButtonBar].
 */
@Composable
fun OverlayDialog(
    show: Boolean,
    modifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    title: String? = null,
    titleColor: Color = DialogDefaults.titleColor(),
    summary: String? = null,
    summaryColor: Color = DialogDefaults.summaryColor(),
    backgroundColor: Color = DialogDefaults.backgroundColor(),
    enableWindowDim: Boolean = true,
    onDismissRequest: (() -> Unit)? = null,
    onDismissFinished: (() -> Unit)? = null,
    outsideMargin: DpSize = DialogDefaults.outsideMargin,
    insideMargin: DpSize = DialogDefaults.insideMargin,
    defaultWindowInsetsPadding: Boolean = true,
    renderInRootScaffold: Boolean = true,
    maxWidth: Dp = DialogDefaults.MaxWidth,
    largeScreen: Boolean? = null,
    cornerRadius: Dp? = null,
    materialStroke: Boolean = false,
    customContent: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    DialogContentLayout(
        show = show,
        titleColor = titleColor,
        summaryColor = summaryColor,
        backgroundColor = backgroundColor,
        outsideMargin = outsideMargin,
        insideMargin = insideMargin,
        popupHost = { visible, hostContent ->
            val visibleState = remember { mutableStateOf(false) }
            visibleState.value = visible
            DialogLayout(
                visible = visibleState,
                enableWindowDim = false,
                enterTransition = EnterTransition.None,
                exitTransition = ExitTransition.None,
                enableAutoLargeScreen = false,
                renderInRootScaffold = renderInRootScaffold,
            ) {
                hostContent()
            }
        },
        modifier = modifier,
        title = title,
        summary = summary,
        enableWindowDim = enableWindowDim,
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        defaultWindowInsetsPadding = defaultWindowInsetsPadding,
        maxWidth = maxWidth,
        largeScreen = largeScreen,
        cornerRadius = cornerRadius,
        backgroundModifier = backgroundModifier,
        materialStroke = materialStroke,
        customContent = customContent,
        content = content,
    )
}
