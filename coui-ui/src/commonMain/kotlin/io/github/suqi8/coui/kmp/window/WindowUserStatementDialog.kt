// Copyright 2026, compose-coui-ui contributors
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.suqi8.coui.kmp.basic.FullPageStatementDefaults
import io.github.suqi8.coui.kmp.layout.BottomSheetContentLayout
import io.github.suqi8.coui.kmp.layout.BottomSheetDefaults
import io.github.suqi8.coui.kmp.layout.UserStatementDialogContent
import io.github.suqi8.coui.kmp.layout.UserStatementDialogDefaults
import io.github.suqi8.coui.kmp.layout.UserStatementListItem
import io.github.suqi8.coui.kmp.theme.LocalDismissState
import io.github.suqi8.coui.kmp.utils.RemovePlatformDialogDefaultEffects
import io.github.suqi8.coui.kmp.utils.WindowNavigationEventScope
import io.github.suqi8.coui.kmp.utils.platformDialogProperties

/**
 * A user statement panel matching COUI `COUIUserStatementDialog`, rendered at window level without
 * `Scaffold`: a bottom sheet laid out at the maximum height with no drag handle, showing the app
 * logo, a title, the app name and either a message, an icon list or custom content, then the
 * statement text (with the agreement links of [protocolText] appended) in a short scroller, and a
 * confirm button above an exit button. The panel cannot be dragged and taps outside it are ignored
 * (`setCanceledOnTouchOutside(false)`).
 *
 * @param show Whether the [WindowUserStatementDialog] is shown.
 * @param onConfirm Invoked when the confirm button is clicked.
 * @param onExit Invoked when the exit button is clicked.
 * @param modifier The modifier to be applied to the panel.
 * @param backgroundModifier A [Modifier] applied inside the panel's corner clip, under the content
 *   and over [backgroundColor] (see [WindowBottomSheet]).
 * @param logo The 80dp app logo (`setLogoDrawable`). Hidden when null or when [customContent] is set.
 * @param title The line above the app name (`setTitleText`, 16sp medium, single line).
 * @param appName The app name (`setAppName`, 28sp medium).
 * @param appMessage The centered message below the app name (`setAppMessage`), shown when there is
 *   neither [listItems] nor [customContent].
 * @param listItems The icon list shown below the app name (`setListItems`).
 * @param onListItemClick Invoked with the index of a tapped [listItems] row.
 * @param statement The statement text (`setStatement`). Use [androidx.compose.ui.text.LinkAnnotation]s
 *   styled with [FullPageStatementDefaults.linkStyles] for links.
 * @param protocolText The agreement text appended to [statement] (`setProtocolText`).
 * @param confirmText The confirm button label (`setBottomButtonText`).
 * @param exitText The exit button label (`setExitButtonText`).
 * @param backgroundColor The background color of the panel.
 * @param enableWindowDim Whether to dim the window behind the panel.
 * @param onDismissRequest Invoked when the user presses back. When null, back is ignored.
 * @param onDismissFinished Invoked when the panel is completely dismissed.
 * @param materialStroke Whether to draw the COUI 17 material stroke of a blurred panel.
 * @param customContent Content replacing the logo, message and list (`setCustomView`).
 */
@Composable
fun WindowUserStatementDialog(
    show: Boolean,
    onConfirm: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    logo: Painter? = null,
    title: String? = null,
    appName: String? = null,
    appMessage: String? = null,
    listItems: List<UserStatementListItem> = emptyList(),
    onListItemClick: ((index: Int) -> Unit)? = null,
    statement: AnnotatedString? = null,
    protocolText: AnnotatedString? = null,
    confirmText: String = UserStatementDialogDefaults.ConfirmText,
    exitText: String = UserStatementDialogDefaults.ExitText,
    backgroundColor: Color = BottomSheetDefaults.backgroundColor(),
    enableWindowDim: Boolean = true,
    onDismissRequest: (() -> Unit)? = null,
    onDismissFinished: (() -> Unit)? = null,
    materialStroke: Boolean = false,
    customContent: (@Composable () -> Unit)? = null,
) {
    val statusBarsPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val captionBarPadding = WindowInsets.captionBar.asPaddingValues().calculateTopPadding()
    val displayCutoutPadding = WindowInsets.displayCutout.asPaddingValues().calculateTopPadding()
    val safeTopInset = remember(statusBarsPadding, captionBarPadding, displayCutoutPadding) {
        maxOf(statusBarsPadding, captionBarPadding, displayCutoutPadding)
    }
    val currentOnDismissRequest = rememberUpdatedState(onDismissRequest)

    BottomSheetContentLayout(
        show = show,
        backgroundColor = backgroundColor,
        cornerRadius = BottomSheetDefaults.cornerRadius,
        sheetMaxWidth = BottomSheetDefaults.maxWidth,
        outsideMargin = BottomSheetDefaults.outsideMargin,
        insideMargin = DpSize(0.dp, 0.dp),
        dragHandleColor = BottomSheetDefaults.dragHandleColor(),
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
        backgroundModifier = backgroundModifier,
        enableWindowDim = enableWindowDim,
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        showInMaxHeight = true,
        allowDismiss = onDismissRequest != null,
        dismissOnClickOutside = false,
        draggable = false,
        enablePullUp = false,
        topInset = safeTopInset,
        materialStroke = materialStroke,
        showHeader = false,
    ) {
        CompositionLocalProvider(
            LocalDismissState provides { currentOnDismissRequest.value?.invoke() },
        ) {
            UserStatementDialogContent(
                onConfirm = onConfirm,
                onExit = onExit,
                logo = logo,
                title = title,
                appName = appName,
                appMessage = appMessage,
                listItems = listItems,
                onListItemClick = onListItemClick,
                statement = statement,
                protocolText = protocolText,
                confirmText = confirmText,
                exitText = exitText,
                customContent = customContent,
            )
        }
    }
}
