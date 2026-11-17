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
import io.github.suqi8.coui.kmp.layout.DialogContentLayout
import io.github.suqi8.coui.kmp.layout.DialogDefaults
import io.github.suqi8.coui.kmp.theme.LocalDismissState
import io.github.suqi8.coui.kmp.utils.RemovePlatformDialogDefaultEffects
import io.github.suqi8.coui.kmp.utils.WindowNavigationEventScope
import io.github.suqi8.coui.kmp.utils.platformDialogProperties

/**
 * A dialog with a title, a summary, and other contents, rendered at window level without `Scaffold`.
 *
 * Use [LocalDismissState] inside `content` to request dismissal from inner composables.
 *
 * @param show Whether the [WindowDialog] is shown.
 * @param modifier The modifier to be applied to the [WindowDialog].
 * @param title The title of the [WindowDialog].
 * @param titleColor The color of the title.
 * @param summary The summary of the [WindowDialog].
 * @param summaryColor The color of the summary.
 * @param backgroundColor The background color of the [WindowDialog].
 * @param enableWindowDim Whether to enable window dimming when the [WindowDialog] is shown.
 * @param onDismissRequest Will called when the user tries to dismiss the Dialog by clicking outside or pressing the back button.
 * @param onDismissFinished The callback when the [WindowDialog] is completely dismissed.
 * @param outsideMargin The margin outside the [WindowDialog].
 * @param insideMargin The margin inside the [WindowDialog].
 * @param defaultWindowInsetsPadding Whether to apply default window insets padding to the [WindowDialog].
 * @param maxWidth The maximum width of the [WindowDialog].
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
 * @param content The [Composable] content of the [WindowDialog], typically a
 *   [io.github.suqi8.coui.kmp.layout.DialogButtonBar].
 */
@Composable
fun WindowDialog(
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
    maxWidth: Dp = DialogDefaults.MaxWidth,
    largeScreen: Boolean? = null,
    cornerRadius: Dp? = null,
    materialStroke: Boolean = false,
    customContent: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val statusBarsPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val captionBarPadding = WindowInsets.captionBar.asPaddingValues().calculateTopPadding()
    val displayCutoutPadding = WindowInsets.displayCutout.asPaddingValues().calculateTopPadding()
    val safeTopInset = remember(statusBarsPadding, captionBarPadding, displayCutoutPadding) {
        maxOf(statusBarsPadding, captionBarPadding, displayCutoutPadding)
    }

    val currentOnDismissRequest = rememberUpdatedState(onDismissRequest)

    DialogContentLayout(
        show = show,
        titleColor = titleColor,
        summaryColor = summaryColor,
        backgroundColor = backgroundColor,
        outsideMargin = outsideMargin,
        insideMargin = insideMargin,
        popupHost = { visible, hostContent ->
            if (visible) {
                Dialog(
                    onDismissRequest = { currentOnDismissRequest.value?.invoke() },
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
        summary = summary,
        enableWindowDim = enableWindowDim,
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        defaultWindowInsetsPadding = defaultWindowInsetsPadding,
        topInset = safeTopInset,
        maxWidth = maxWidth,
        largeScreen = largeScreen,
        cornerRadius = cornerRadius,
        backgroundModifier = backgroundModifier,
        materialStroke = materialStroke,
        customContent = customContent,
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
