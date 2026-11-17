// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.LinearProgressIndicator
import io.github.suqi8.coui.kmp.basic.ProgressIndicatorColors
import io.github.suqi8.coui.kmp.basic.ProgressIndicatorDefaults
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * Internal shared layout of the horizontal progress dialogs (`COUIAlertDialog.Progress` /
 * `Progress.Cancelable`, `coui_progress_dialog_horizontal.xml`): a regular alert dialog whose custom
 * view is a centered 200dp x 4dp progress bar.
 */
@Composable
internal fun ProgressDialogContentLayout(
    show: Boolean,
    progress: Float,
    popupHost: @Composable (visible: Boolean, content: @Composable () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    title: String? = null,
    titleColor: Color = Color.Unspecified,
    summary: String? = null,
    summaryColor: Color = Color.Unspecified,
    cancelAction: DialogButtonBarAction? = null,
    progressColors: ProgressIndicatorColors = ProgressDialogDefaults.progressColors(),
    backgroundColor: Color = Color.Unspecified,
    enableWindowDim: Boolean = true,
    onDismissRequest: (() -> Unit)? = null,
    onDismissFinished: (() -> Unit)? = null,
    topInset: Dp? = null,
    materialStroke: Boolean = false,
) {
    DialogContentLayout(
        show = show,
        titleColor = titleColor,
        summaryColor = summaryColor,
        backgroundColor = backgroundColor,
        outsideMargin = DialogDefaults.outsideMargin,
        insideMargin = DialogDefaults.insideMargin,
        popupHost = popupHost,
        modifier = modifier,
        backgroundModifier = backgroundModifier,
        title = title,
        summary = summary,
        enableWindowDim = enableWindowDim,
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        topInset = topInset,
        materialStroke = materialStroke,
        customContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = ProgressDialogDefaults.PaddingTop, bottom = ProgressDialogDefaults.PaddingBottom),
                contentAlignment = Alignment.Center,
            ) {
                LinearProgressIndicator(
                    modifier = Modifier.width(ProgressDialogDefaults.ProgressWidth),
                    progress = progress.coerceIn(0f, 1f),
                    colors = progressColors,
                    height = ProgressDialogDefaults.ProgressHeight,
                )
            }
        },
    ) {
        DialogButtonBar(negative = cancelAction, positive = null)
    }
}

object ProgressDialogDefaults {
    /** The width of the progress bar. COUI `coui_loading_dialog_progress_width`. */
    val ProgressWidth = 200.dp

    /** The height of the progress bar. COUI `coui_loading_dialog_progress_height`. */
    val ProgressHeight = 4.dp

    /** The padding above the progress bar. COUI `coui_progress_dialog_padding_top`. */
    val PaddingTop = 16.dp

    /**
     * The padding below the progress bar. Both progress layouts use
     * `coui_progress_cancelable_dialog_padding_bottom`.
     */
    val PaddingBottom = 16.dp

    /**
     * The default [ProgressIndicatorColors] of the progress bar: the regular progress colors over
     * the `?couiColorDivider` track the layout sets (`couiHorizontalProgressBarBackgroundColor`).
     */
    @Composable
    fun progressColors(): ProgressIndicatorColors = ProgressIndicatorDefaults.progressIndicatorColors(
        backgroundColor = COUITheme.colorScheme.divider,
    )
}
