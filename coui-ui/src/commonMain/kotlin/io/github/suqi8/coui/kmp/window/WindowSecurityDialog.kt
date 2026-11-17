// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.window

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import io.github.suqi8.coui.kmp.layout.DialogDefaults
import io.github.suqi8.coui.kmp.layout.SecurityDialogColors
import io.github.suqi8.coui.kmp.layout.SecurityDialogContentLayout
import io.github.suqi8.coui.kmp.layout.SecurityDialogDefaults
import io.github.suqi8.coui.kmp.utils.RemovePlatformDialogDefaultEffects
import io.github.suqi8.coui.kmp.utils.platformDialogProperties

/**
 * A security statement dialog rendered at window level without `Scaffold`, matching the COUI
 * security alert dialog (COUISecurityAlertDialogBuilder): a regular dialog extended with a
 * statement paragraph (with an optional tappable link), a "don't remind me again" checkbox row
 * and a cancel / confirm button bar.
 *
 * @param show Whether the [WindowSecurityDialog] is shown.
 * @param onConfirm Invoked when the confirm button is clicked, with the current checkbox state.
 * @param onCancel Invoked when the cancel button is clicked or the user presses the back button.
 *   A tap outside the panel does nothing, as COUI makes the dialog non-cancelable once a selection
 *   listener is set (`COUISecurityAlertDialogBuilder.configureDefaultPredictiveBack`).
 * @param modifier The modifier to be applied to the [WindowSecurityDialog].
 * @param title The title of the dialog.
 * @param summary The summary (message) of the dialog.
 * @param statement The statement paragraph shown below the summary. Hidden when null.
 * @param statementLinkText The substring of [statement] rendered as a tappable link.
 * @param onLinkClick Invoked when the statement link is clicked.
 * @param checkboxText The label of the checkbox row. Hidden when null.
 * @param initialChecked The initial checkbox state, re-applied each time the dialog is shown.
 * @param onCheckedChange Invoked each time the checkbox is toggled, like the
 *   `onSelected(0, isChecked)` report of `COUISecurityAlertDialogBuilder`.
 * @param confirmText The label of the confirm (positive) button.
 * @param cancelText The label of the cancel (negative) button.
 * @param titleColor The color of the title.
 * @param summaryColor The color of the summary.
 * @param backgroundColor The background color of the dialog.
 * @param colors The [SecurityDialogColors] of the statement, link and checkbox texts.
 * @param enableWindowDim Whether to enable window dimming when the dialog is shown.
 * @param onDismissFinished The callback when the [WindowSecurityDialog] is completely dismissed.
 * @param backgroundModifier A [Modifier] applied inside the panel's corner clip, under the content
 *   and over [backgroundColor]. Use it to paint the COUI 17 blurred window background
 *   (`COUIAlertDialogBuilder.setBlurBackgroundDrawable`) and pass a transparent [backgroundColor]
 *   alongside it.
 * @param materialStroke Whether to draw the COUI 17 material stroke (edge lighting + inner shadow)
 *   `COUIAlertDialogRootLayout` adds once the window background is blurred. Needs runtime shaders.
 *   The cancel / confirm buttons then turn material too
 *   (`COUIButtonBarLayout.setBlurBackgroundWindow`).
 */
@Composable
fun WindowSecurityDialog(
    show: Boolean,
    onConfirm: (dontRemind: Boolean) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    title: String? = null,
    summary: String? = null,
    statement: String? = null,
    statementLinkText: String? = null,
    onLinkClick: (() -> Unit)? = null,
    checkboxText: String? = SecurityDialogDefaults.CheckboxText,
    initialChecked: Boolean = false,
    onCheckedChange: ((checked: Boolean) -> Unit)? = null,
    confirmText: String = SecurityDialogDefaults.ConfirmText,
    cancelText: String = SecurityDialogDefaults.CancelText,
    titleColor: Color = DialogDefaults.titleColor(),
    summaryColor: Color = DialogDefaults.summaryColor(),
    backgroundColor: Color = DialogDefaults.backgroundColor(),
    colors: SecurityDialogColors = SecurityDialogDefaults.securityDialogColors(),
    enableWindowDim: Boolean = true,
    onDismissFinished: (() -> Unit)? = null,
    materialStroke: Boolean = false,
) {
    val statusBarsPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val captionBarPadding = WindowInsets.captionBar.asPaddingValues().calculateTopPadding()
    val displayCutoutPadding = WindowInsets.displayCutout.asPaddingValues().calculateTopPadding()
    val safeTopInset = remember(statusBarsPadding, captionBarPadding, displayCutoutPadding) {
        maxOf(statusBarsPadding, captionBarPadding, displayCutoutPadding)
    }

    val currentOnCancel = rememberUpdatedState(onCancel)

    SecurityDialogContentLayout(
        show = show,
        popupHost = { visible, hostContent ->
            if (visible) {
                Dialog(
                    onDismissRequest = { currentOnCancel.value.invoke() },
                    properties = platformDialogProperties(),
                ) {
                    RemovePlatformDialogDefaultEffects()
                    hostContent()
                }
            }
        },
        onConfirm = onConfirm,
        onCancel = onCancel,
        modifier = modifier,
        backgroundModifier = backgroundModifier,
        title = title,
        summary = summary,
        statement = statement,
        statementLinkText = statementLinkText,
        onLinkClick = onLinkClick,
        checkboxText = checkboxText,
        initialChecked = initialChecked,
        onCheckedChange = onCheckedChange,
        confirmText = confirmText,
        cancelText = cancelText,
        titleColor = titleColor,
        summaryColor = summaryColor,
        backgroundColor = backgroundColor,
        colors = colors,
        enableWindowDim = enableWindowDim,
        onDismissFinished = onDismissFinished,
        topInset = safeTopInset,
        materialStroke = materialStroke,
    )
}
