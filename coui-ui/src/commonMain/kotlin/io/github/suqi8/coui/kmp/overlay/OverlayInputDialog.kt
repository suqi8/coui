// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.overlay

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.suqi8.coui.kmp.basic.TextFieldColors
import io.github.suqi8.coui.kmp.basic.TextFieldDefaults
import io.github.suqi8.coui.kmp.layout.DialogButtonBarAction
import io.github.suqi8.coui.kmp.layout.DialogDefaults
import io.github.suqi8.coui.kmp.layout.InputDialogContentLayout

/**
 * A single-input dialog. Without [maxCount] it is
 * `COUIAlertDialog.SingleEdit`: a line-style edit text with quick delete, up to five lines and an
 * optional password toggle. With [maxCount] it is `COUIAlertDialog.SingleInput`: an
 * [io.github.suqi8.coui.kmp.basic.InputView] with an input counter. The field takes focus when the
 * dialog opens.
 *
 * @param show Whether the [OverlayInputDialog] is shown.
 * @param value The input text.
 * @param onValueChange Invoked when the input text changes.
 * @param modifier The modifier to be applied to the [OverlayInputDialog].
 * @param backgroundModifier A [Modifier] applied inside the panel's corner clip, under the content
 *   and over [backgroundColor], e.g. to paint the COUI 17 blurred window background.
 * @param title The title of the dialog.
 * @param titleColor The color of the title.
 * @param summary The message of the dialog.
 * @param summaryColor The color of the summary.
 * @param label The hint shown while the input is empty.
 * @param maxCount The max number of characters. When not null the dialog takes the SingleInput
 *   form with a counter.
 * @param showPasswordToggle Whether the field masks its text behind a visibility toggle.
 * @param keyboardOptions The keyboard options of the field.
 * @param textFieldColors The colors of the field.
 * @param negative The negative button of the bar. Hidden when null.
 * @param positive The positive button of the bar. Hidden when null.
 * @param neutral The neutral button of the bar. Hidden when null.
 * @param backgroundColor The background color of the dialog.
 * @param enableWindowDim Whether to enable window dimming when the dialog is shown.
 * @param onDismissRequest Invoked when the user taps outside or presses back.
 * @param onDismissFinished Invoked when the hide animation completes.
 * @param renderInRootScaffold Whether to render the dialog in the root (outermost) Scaffold.
 * @param materialStroke Whether to draw the COUI 17 material stroke of a blurred panel; the bar
 *   buttons then turn material too.
 */
@Composable
fun OverlayInputDialog(
    show: Boolean,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    title: String? = null,
    titleColor: Color = DialogDefaults.titleColor(),
    summary: String? = null,
    summaryColor: Color = DialogDefaults.summaryColor(),
    label: String = "",
    maxCount: Int? = null,
    showPasswordToggle: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    textFieldColors: TextFieldColors = TextFieldDefaults.textFieldColors(),
    negative: DialogButtonBarAction? = null,
    positive: DialogButtonBarAction? = null,
    neutral: DialogButtonBarAction? = null,
    backgroundColor: Color = DialogDefaults.backgroundColor(),
    enableWindowDim: Boolean = true,
    onDismissRequest: (() -> Unit)? = null,
    onDismissFinished: (() -> Unit)? = null,
    renderInRootScaffold: Boolean = true,
    materialStroke: Boolean = false,
) {
    InputDialogContentLayout(
        show = show,
        value = value,
        onValueChange = onValueChange,
        popupHost = { visible, hostContent ->
            OverlayDialogHost(visible = visible, renderInRootScaffold = renderInRootScaffold, content = hostContent)
        },
        modifier = modifier,
        backgroundModifier = backgroundModifier,
        title = title,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        label = label,
        maxCount = maxCount,
        showPasswordToggle = showPasswordToggle,
        keyboardOptions = keyboardOptions,
        textFieldColors = textFieldColors,
        negative = negative,
        positive = positive,
        neutral = neutral,
        backgroundColor = backgroundColor,
        enableWindowDim = enableWindowDim,
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        materialStroke = materialStroke,
    )
}
