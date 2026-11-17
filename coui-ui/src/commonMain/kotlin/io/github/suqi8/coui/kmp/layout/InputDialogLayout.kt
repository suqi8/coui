// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.layout

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.InputView
import io.github.suqi8.coui.kmp.basic.TextField
import io.github.suqi8.coui.kmp.basic.TextFieldColors
import io.github.suqi8.coui.kmp.basic.TextFieldDefaults
import io.github.suqi8.coui.kmp.basic.TextFieldMode

/**
 * Internal shared layout of the input dialogs. Without [maxCount] it is `COUIAlertDialog.SingleEdit`
 * (`coui_single_edit_bottom_alert_dialog_layout`): a line-style edit text with quick delete, up to
 * five lines and an optional password toggle. With [maxCount] it is `COUIAlertDialog.SingleInput`
 * (`coui_single_inputview_bottom_alert_dialog_layout`): a `COUIInputView` with an input counter.
 * Both are `BottomAssignment` dialogs, whose custom panel carries no extra bottom padding.
 */
@Composable
internal fun InputDialogContentLayout(
    show: Boolean,
    value: String,
    onValueChange: (String) -> Unit,
    popupHost: @Composable (visible: Boolean, content: @Composable () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    title: String? = null,
    titleColor: Color = Color.Unspecified,
    summary: String? = null,
    summaryColor: Color = Color.Unspecified,
    label: String = "",
    maxCount: Int? = null,
    showPasswordToggle: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    textFieldColors: TextFieldColors = TextFieldDefaults.textFieldColors(),
    negative: DialogButtonBarAction? = null,
    positive: DialogButtonBarAction? = null,
    neutral: DialogButtonBarAction? = null,
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
        customContentPaddingBottom = 0.dp,
        customContent = {
            val focusRequester = remember { FocusRequester() }
            // Animation.COUI.Dialog.AutoShowKeyboard: the input dialogs open with the keyboard up.
            LaunchedEffect(focusRequester) {
                runCatching { focusRequester.requestFocus() }
            }
            if (maxCount == null) {
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .padding(horizontal = InputDialogDefaults.FieldMarginHorizontal)
                        .padding(vertical = InputDialogDefaults.EditMarginVertical)
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    backgroundMode = TextFieldMode.Line,
                    colors = textFieldColors,
                    label = label,
                    showClearButton = true,
                    showPasswordToggle = showPasswordToggle,
                    keyboardOptions = keyboardOptions,
                    maxLines = InputDialogDefaults.MaxLines,
                )
            } else {
                InputView(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .padding(horizontal = InputDialogDefaults.FieldMarginHorizontal)
                        .padding(bottom = InputDialogDefaults.InputMarginBottom)
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    label = label,
                    showCount = true,
                    maxCount = maxCount,
                    showPasswordToggle = showPasswordToggle,
                    textFieldColors = textFieldColors,
                    keyboardOptions = keyboardOptions,
                    singleLine = false,
                    maxLines = InputDialogDefaults.MaxLines,
                )
            }
        },
    ) {
        DialogButtonBar(negative = negative, positive = positive, neutral = neutral)
    }
}

object InputDialogDefaults {
    /**
     * The horizontal margin of the input field. COUI `common_margin` (SingleEdit) and the 24dp
     * `layout_marginStart/End` of the SingleInput `COUIInputView`.
     */
    val FieldMarginHorizontal = 24.dp

    /** The vertical margin around the SingleEdit field (its root `layout_marginTop/Bottom`). */
    val EditMarginVertical = 8.dp

    /** The bottom margin below the SingleInput `COUIInputView`. */
    val InputMarginBottom = 18.dp

    /** The max number of lines of the input (`android:maxLines` of both layouts). */
    val MaxLines = 5
}
