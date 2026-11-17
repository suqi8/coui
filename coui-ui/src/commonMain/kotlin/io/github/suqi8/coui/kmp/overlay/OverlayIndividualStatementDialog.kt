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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.FullPageStatementDefaults
import io.github.suqi8.coui.kmp.layout.BottomSheetContentLayout
import io.github.suqi8.coui.kmp.layout.BottomSheetDefaults
import io.github.suqi8.coui.kmp.layout.IndividualStatementDialogContent
import io.github.suqi8.coui.kmp.layout.IndividualStatementDialogDefaults
import io.github.suqi8.coui.kmp.layout.StatementPrivacyItem
import io.github.suqi8.coui.kmp.utils.COUIPopupUtils.Companion.DialogLayout

/**
 * A function privacy statement panel matching COUI `COUIIndividualStatementDialog`: a bottom sheet
 * with no drag handle holding a centered title, the statement text and optional opt-in rows (title,
 * summary and a checkbox; the whole row toggles it) in a scroller of at most 384dp, a confirm
 * button and an exit text button. With opt-in rows the confirm button stays disabled until at least
 * one row is checked, and the statement text shrinks from 14sp to 12sp. Small landscape windows
 * (smallest width < 480dp) show exit and confirm side by side. The panel cannot be dragged and taps
 * outside it are ignored (`setCanceledOnTouchOutside(false)`). Checks are cleared each time the panel
 * is shown.
 *
 * @param show Whether the [OverlayIndividualStatementDialog] is shown.
 * @param onConfirm Invoked when the confirm button is clicked, with the checked [privacyItems]
 *   (`OnButtonClickListener.onBottomButtonClick`).
 * @param onExit Invoked when the exit button is clicked.
 * @param modifier The modifier to be applied to the panel.
 * @param backgroundModifier A [Modifier] applied inside the panel's corner clip, under the content
 *   and over [backgroundColor] (see [OverlayBottomSheet]).
 * @param title The centered title (`setTitleText`, 18sp medium).
 * @param statement The statement text (`setStatement`). Use [androidx.compose.ui.text.LinkAnnotation]s
 *   styled with [FullPageStatementDefaults.linkStyles] for links.
 * @param privacyItems The opt-in rows (`addPrivacyList`).
 * @param confirmText The confirm button label (`setBottomButtonText`).
 * @param exitText The exit button label (`setExitButtonText`).
 * @param backgroundColor The background color of the panel.
 * @param enableWindowDim Whether to dim the window behind the panel.
 * @param onDismissRequest Invoked when the user presses back. When null, back is ignored.
 * @param onDismissFinished Invoked when the panel is completely dismissed.
 * @param renderInRootScaffold Whether to render the panel in the root (outermost) Scaffold.
 * @param materialStroke Whether to draw the COUI 17 material stroke of a blurred panel.
 */
@Composable
fun OverlayIndividualStatementDialog(
    show: Boolean,
    onConfirm: (checkedItems: List<StatementPrivacyItem>) -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    title: String? = null,
    statement: AnnotatedString? = null,
    privacyItems: List<StatementPrivacyItem> = emptyList(),
    confirmText: String = IndividualStatementDialogDefaults.ConfirmText,
    exitText: String = IndividualStatementDialogDefaults.ExitText,
    backgroundColor: Color = BottomSheetDefaults.backgroundColor(),
    enableWindowDim: Boolean = true,
    onDismissRequest: (() -> Unit)? = null,
    onDismissFinished: (() -> Unit)? = null,
    renderInRootScaffold: Boolean = true,
    materialStroke: Boolean = false,
) {
    BottomSheetContentLayout(
        show = show,
        backgroundColor = backgroundColor,
        cornerRadius = BottomSheetDefaults.cornerRadius,
        sheetMaxWidth = BottomSheetDefaults.maxWidth,
        outsideMargin = BottomSheetDefaults.outsideMargin,
        insideMargin = DpSize(0.dp, 0.dp),
        dragHandleColor = BottomSheetDefaults.dragHandleColor(),
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
        enableWindowDim = enableWindowDim,
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        allowDismiss = onDismissRequest != null,
        dismissOnClickOutside = false,
        draggable = false,
        enablePullUp = false,
        materialStroke = materialStroke,
        showHeader = false,
    ) {
        IndividualStatementDialogContent(
            show = show,
            onConfirm = onConfirm,
            onExit = onExit,
            title = title,
            statement = statement,
            privacyItems = privacyItems,
            confirmText = confirmText,
            exitText = exitText,
        )
    }
}
