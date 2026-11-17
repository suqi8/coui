// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.window

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.suqi8.coui.kmp.basic.ProgressIndicatorColors
import io.github.suqi8.coui.kmp.layout.DialogButtonBarAction
import io.github.suqi8.coui.kmp.layout.DialogDefaults
import io.github.suqi8.coui.kmp.layout.ProgressDialogContentLayout
import io.github.suqi8.coui.kmp.layout.ProgressDialogDefaults

/**
 * A horizontal progress dialog (`COUIAlertDialog.Progress`, `coui_progress_dialog_horizontal.xml`)
 * rendered at window level without `Scaffold`: a title and an optional message above a centered
 * 200dp x 4dp progress bar. With [cancelAction] it is `COUIAlertDialog.Progress.Cancelable`, closed
 * by a one-button bar.
 *
 * @param show Whether the [WindowProgressDialog] is shown.
 * @param progress The progress, between 0 and 1.
 * @param modifier The modifier to be applied to the [WindowProgressDialog].
 * @param backgroundModifier A [Modifier] applied inside the panel's corner clip, under the content
 *   and over [backgroundColor], e.g. to paint the COUI 17 blurred window background.
 * @param title The title of the dialog.
 * @param titleColor The color of the title.
 * @param summary The message of the dialog.
 * @param summaryColor The color of the summary.
 * @param cancelAction The optional cancel button. Hidden when null.
 * @param progressColors The colors of the progress bar.
 * @param backgroundColor The background color of the dialog.
 * @param enableWindowDim Whether to enable window dimming when the dialog is shown.
 * @param onDismissRequest Invoked when the user taps outside or presses back.
 * @param onDismissFinished Invoked when the hide animation completes.
 * @param materialStroke Whether to draw the COUI 17 material stroke of a blurred panel; the bar
 *   buttons then turn material too.
 */
@Composable
fun WindowProgressDialog(
    show: Boolean,
    progress: Float,
    modifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    title: String? = null,
    titleColor: Color = DialogDefaults.titleColor(),
    summary: String? = null,
    summaryColor: Color = DialogDefaults.summaryColor(),
    cancelAction: DialogButtonBarAction? = null,
    progressColors: ProgressIndicatorColors = ProgressDialogDefaults.progressColors(),
    backgroundColor: Color = DialogDefaults.backgroundColor(),
    enableWindowDim: Boolean = true,
    onDismissRequest: (() -> Unit)? = null,
    onDismissFinished: (() -> Unit)? = null,
    materialStroke: Boolean = false,
) {
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)
    ProgressDialogContentLayout(
        show = show,
        progress = progress,
        popupHost = { visible, hostContent ->
            WindowDialogHost(visible = visible, onDismissRequest = { currentOnDismissRequest?.invoke() }, content = hostContent)
        },
        modifier = modifier,
        backgroundModifier = backgroundModifier,
        title = title,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        cancelAction = cancelAction,
        progressColors = progressColors,
        backgroundColor = backgroundColor,
        enableWindowDim = enableWindowDim,
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        topInset = windowDialogTopInset(),
        materialStroke = materialStroke,
    )
}
