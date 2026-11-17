// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.preference

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.BasicComponentColors
import io.github.suqi8.coui.kmp.basic.BasicComponentDefaults
import io.github.suqi8.coui.kmp.basic.CardListPosition
import io.github.suqi8.coui.kmp.basic.LocalCardListPosition
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextField
import io.github.suqi8.coui.kmp.basic.TextFieldColors
import io.github.suqi8.coui.kmp.basic.TextFieldDefaults
import io.github.suqi8.coui.kmp.basic.TextFieldMode
import io.github.suqi8.coui.kmp.layout.DialogButtonBar
import io.github.suqi8.coui.kmp.layout.DialogButtonBarAction
import io.github.suqi8.coui.kmp.overlay.OverlayDialog
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * A preference row that edits its text value in an input dialog, mirroring
 * COUIEditTextPreference and COUIEditTextPreferenceDialogFragment.
 *
 * The row is a jump row (Preference.COUI.DialogPreference.EditTextPreference: coui_btn_next arrow)
 * that shows the current value as trailing assignment text. A click opens an [OverlayDialog]
 * (`COUIAlertDialog.BottomAssignment`) with [dialogTitle], an optional [dialogMessage], a
 * single-line underlined field (coui_preference_dialog_edittext.xml: `couiEditTextLineHintDisableStyle`,
 * 24dp horizontal margins, 14dp bottom margin, 34dp min height, quick delete on) that takes the
 * focus at once, and a cancel / confirm button bar. The confirm button commits the edited text
 * through [onValueChange]; it is disabled while the field is empty unless [supportEmptyInput]
 * (couiSupportEmptyInput) is set. Cancel or an outside tap dismiss without changes.
 *
 * @param value The current text value.
 * @param onValueChange The callback with the edited text when the dialog is confirmed.
 * @param title The title of the [EditTextPreference].
 * @param positiveButtonText The label of the confirm button (COUI positiveButtonText, dialog_ok).
 * @param negativeButtonText The label of the cancel button (COUI negativeButtonText, dialog_cancel).
 * @param modifier The modifier to be applied to the [EditTextPreference].
 * @param titleColor The color of the title.
 * @param summary The summary of the [EditTextPreference].
 * @param summaryColor The color of the summary.
 * @param dialogTitle The title of the input dialog. Defaults to [title].
 * @param dialogMessage The optional message of the input dialog.
 * @param hint The hint of the dialog field, shown while it is empty.
 * @param showValue Whether to show [value] as trailing assignment text on the row.
 * @param supportEmptyInput Whether the confirm button stays enabled for an empty field.
 * @param textFieldColors The [TextFieldColors] of the dialog field.
 * @param keyboardOptions The keyboard options of the dialog field.
 * @param startAction The [Composable] content on the start side of the [EditTextPreference].
 * @param bottomAction The [Composable] content at the bottom of the [EditTextPreference].
 * @param insideMargin The margin inside the [EditTextPreference].
 * @param cardListPosition The row's position inside its card group, by default the one a
 *   [io.github.suqi8.coui.kmp.basic.CardListGroup] provides. Rounded outer edges receive extra padding.
 * @param enabled Whether the [EditTextPreference] is clickable.
 * @param renderInRootScaffold Whether to render the dialog in the root (outermost) Scaffold.
 * @param onExpandedChange The callback when the dialog is shown or dismissed.
 */
@Composable
fun EditTextPreference(
    value: String,
    onValueChange: (String) -> Unit,
    title: String,
    positiveButtonText: String,
    negativeButtonText: String,
    modifier: Modifier = Modifier,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    dialogTitle: String? = title,
    dialogMessage: String? = null,
    hint: String = "",
    showValue: Boolean = true,
    supportEmptyInput: Boolean = false,
    textFieldColors: TextFieldColors = TextFieldDefaults.textFieldColors(),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    startAction: @Composable (() -> Unit)? = null,
    bottomAction: (@Composable () -> Unit)? = null,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    cardListPosition: CardListPosition = LocalCardListPosition.current,
    enabled: Boolean = true,
    renderInRootScaffold: Boolean = true,
    onExpandedChange: ((Boolean) -> Unit)? = null,
) {
    val isDialogShown = remember { mutableStateOf(false) }
    val isHoldDown = remember { mutableStateOf(false) }
    val draft = remember { mutableStateOf(value) }
    val currentValue by rememberUpdatedState(value)
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val currentOnExpandedChange = rememberUpdatedState(onExpandedChange)
    val setExpanded: (Boolean) -> Unit = remember {
        { expanded ->
            if (isDialogShown.value != expanded) {
                isDialogShown.value = expanded
                currentOnExpandedChange.value?.invoke(expanded)
            }
        }
    }
    val handleClick = remember(enabled) {
        {
            if (enabled) {
                // EditTextPreferenceDialogFragmentCompat seeds the field with the current text.
                draft.value = currentValue
                isHoldDown.value = true
                setExpanded(true)
            }
        }
    }

    ArrowPreference(
        title = title,
        modifier = modifier,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        startAction = startAction,
        endActions = {
            if (showValue && value.isNotEmpty()) {
                Text(
                    text = value,
                    modifier = Modifier.weight(1f, fill = false),
                    style = COUITheme.textStyles.bodyS,
                    color = if (enabled) {
                        COUITheme.colorScheme.labelSecondary
                    } else {
                        COUITheme.colorScheme.disabledNeutral
                    },
                    textAlign = TextAlign.End,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            }
        },
        bottomAction = bottomAction,
        insideMargin = insideMargin,
        cardListPosition = cardListPosition,
        onClick = handleClick,
        holdDownState = isHoldDown.value,
        enabled = enabled,
    )

    OverlayDialog(
        show = isDialogShown.value,
        title = dialogTitle,
        summary = dialogMessage,
        onDismissRequest = { setExpanded(false) },
        onDismissFinished = { isHoldDown.value = false },
        renderInRootScaffold = renderInRootScaffold,
        // The field is the dialog's custom view (COUIEditTextPreferenceDialogFragment sets it
        // through the builder's setView), so it goes into the custom panel slot.
        customContent = {
            EditTextPreferenceDialogField(
                text = draft.value,
                onTextChange = { draft.value = it },
                hint = hint,
                textFieldColors = textFieldColors,
                keyboardOptions = keyboardOptions,
            )
        },
    ) {
        DialogButtonBar(
            negative = DialogButtonBarAction(text = negativeButtonText, onClick = { setExpanded(false) }),
            positive = DialogButtonBarAction(
                text = positiveButtonText,
                // COUIEditTextPreferenceDialogFragment: afterTextChanged disables BUTTON_POSITIVE
                // for an empty field unless couiSupportEmptyInput.
                enabled = supportEmptyInput || draft.value.isNotEmpty(),
                onClick = {
                    currentOnValueChange(draft.value)
                    setExpanded(false)
                },
            ),
        )
    }
}

@Composable
private fun EditTextPreferenceDialogField(
    text: String,
    onTextChange: (String) -> Unit,
    hint: String,
    textFieldColors: TextFieldColors,
    keyboardOptions: KeyboardOptions,
) {
    val focusRequester = remember { FocusRequester() }
    // COUIEditTextPreferenceDialogFragment.onResume: requestFocus + SOFT_INPUT_STATE_VISIBLE.
    LaunchedEffect(focusRequester) {
        runCatching { focusRequester.requestFocus() }
    }
    TextField(
        value = text,
        onValueChange = onTextChange,
        modifier = Modifier
            .padding(horizontal = EditTextPreferenceDefaults.FieldHorizontalMargin)
            .padding(bottom = EditTextPreferenceDefaults.FieldBottomMargin)
            .fillMaxWidth()
            .focusRequester(focusRequester),
        backgroundMode = TextFieldMode.Line,
        // The dialog layout overrides the Widget.COUI.EditText 62dp minHeight.
        minHeight = EditTextPreferenceDefaults.FieldMinHeight,
        colors = textFieldColors,
        label = hint,
        showClearButton = true,
        keyboardOptions = keyboardOptions,
        singleLine = true,
    )
}

object EditTextPreferenceDefaults {

    /**
     * The horizontal margin of the dialog field
     * (COUI support_preference_dialog_edittext_margin_horizontal).
     */
    val FieldHorizontalMargin = 24.dp

    /** The bottom margin of the dialog field (COUI coui_edittextdialog_marginbottom). */
    val FieldBottomMargin = 14.dp

    /** The minimum height of the dialog field (COUI support_preference_dialog_edittext_min_height). */
    val FieldMinHeight = 34.dp
}
