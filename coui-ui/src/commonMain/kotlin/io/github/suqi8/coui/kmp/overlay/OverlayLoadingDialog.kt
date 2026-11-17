// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.overlay

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.suqi8.coui.kmp.layout.DialogButtonBarAction
import io.github.suqi8.coui.kmp.layout.DialogDefaults
import io.github.suqi8.coui.kmp.layout.LoadingDialogContentLayout
import io.github.suqi8.coui.kmp.layout.LoadingDialogDefaults
import io.github.suqi8.coui.kmp.utils.COUIPopupUtils.Companion.DialogLayout

/**
 * A small always-centered loading dialog with a rotating spinner and an optional message,
 * matching the COUI rotating progress dialog (COUIRotatingDialogBuilder).
 *
 * @param show Whether the [OverlayLoadingDialog] is shown.
 * @param modifier The modifier to be applied to the [OverlayLoadingDialog].
 * @param text The optional message shown below the spinner.
 * @param textColor The color of the message text.
 * @param spinnerColor The color of the spinner.
 * @param backgroundColor The background color of the loading card.
 * @param enableWindowDim Whether to enable window dimming when the dialog is shown.
 * @param onDismissRequest Will be called when the user tries to dismiss the dialog by clicking
 *   outside or pressing the back button. When null (default), the dialog cannot be dismissed
 *   by the user.
 * @param onDismissFinished The callback when the [OverlayLoadingDialog] is completely dismissed.
 * @param renderInRootScaffold Whether to render the dialog in the root (outermost) Scaffold.
 * @param backgroundModifier A [Modifier] applied inside the card's corner clip, under the content
 *   and over [backgroundColor]. Use it to paint the COUI 17 blurred window background
 *   (`COUIRotatingDialogBuilder.setBlurBackgroundWindow`), e.g. with coui-blur's
 *   `Modifier.couiMaterialBlur(backdrop, MaterialBlurDefaults.dialog(isDark))`, and pass a
 *   transparent [backgroundColor] alongside it.
 * @param materialStroke Whether to draw the COUI 17 material stroke (edge lighting + inner shadow)
 *   `COUIAlertDialogRootLayout` adds once the window background is blurred. Needs runtime shaders.
 * @param title The optional title above the spinner (`COUIRotatingDialogBuilder.setDialogTitle`).
 * @param titleColor The color of [title].
 * @param cancelAction The optional cancel button (`COUIRotatingDialogBuilder.setCancelButton`).
 *   With it the dialog takes the cancelable form (`COUIAlertDialog.Rotating.Cancelable`): a wider
 *   card without the minimum height, the message closer to a one-button bar.
 */
@Composable
fun OverlayLoadingDialog(
    show: Boolean,
    modifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    text: String? = null,
    textColor: Color = LoadingDialogDefaults.textColor(),
    spinnerColor: Color = LoadingDialogDefaults.spinnerColor(),
    backgroundColor: Color = DialogDefaults.backgroundColor(),
    enableWindowDim: Boolean = true,
    onDismissRequest: (() -> Unit)? = null,
    onDismissFinished: (() -> Unit)? = null,
    renderInRootScaffold: Boolean = true,
    materialStroke: Boolean = false,
    title: String? = null,
    titleColor: Color = DialogDefaults.titleColor(),
    cancelAction: DialogButtonBarAction? = null,
) {
    LoadingDialogContentLayout(
        show = show,
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
        backgroundModifier = backgroundModifier,
        text = text,
        textColor = textColor,
        spinnerColor = spinnerColor,
        backgroundColor = backgroundColor,
        enableWindowDim = enableWindowDim,
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        materialStroke = materialStroke,
        title = title,
        titleColor = titleColor,
        cancelAction = cancelAction,
    )
}
