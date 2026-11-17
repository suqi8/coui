// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.CardListGroupScope
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.blur.MaterialBlurDefaults
import io.github.suqi8.coui.kmp.layout.DialogButtonBar
import io.github.suqi8.coui.kmp.layout.DialogButtonBarAction
import io.github.suqi8.coui.kmp.layout.DialogDefaults
import io.github.suqi8.coui.kmp.layout.ListDialogItem
import io.github.suqi8.coui.kmp.overlay.OverlayDialog
import io.github.suqi8.coui.kmp.overlay.OverlayInputDialog
import io.github.suqi8.coui.kmp.overlay.OverlayListDialog
import io.github.suqi8.coui.kmp.overlay.OverlayLoadingDialog
import io.github.suqi8.coui.kmp.overlay.OverlayMultiChoiceDialog
import io.github.suqi8.coui.kmp.overlay.OverlayProgressDialog
import io.github.suqi8.coui.kmp.overlay.OverlaySecurityDialog
import io.github.suqi8.coui.kmp.overlay.OverlaySingleChoiceDialog
import io.github.suqi8.coui.kmp.preference.ArrowPreference
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.LocalDismissState
import io.github.suqi8.coui.kmp.window.WindowDialog
import kotlinx.coroutines.delay
import utils.panelBlur

fun LazyListScope.dialogSection() {
    item(key = "dialog") {
        var showSuperDialog by remember { mutableStateOf(false) }
        var showWindowDialog by remember { mutableStateOf(false) }
        var superDialogHoldDown by remember { mutableStateOf(false) }
        var windowDialogHoldDown by remember { mutableStateOf(false) }
        var showLoadingDialog by remember { mutableStateOf(false) }
        var loadingDialogHoldDown by remember { mutableStateOf(false) }
        var showSecurityDialog by remember { mutableStateOf(false) }
        var securityDialogHoldDown by remember { mutableStateOf(false) }
        var showStackedDialog by remember { mutableStateOf(false) }
        var stackedDialogHoldDown by remember { mutableStateOf(false) }
        var showRecommendDialog by remember { mutableStateOf(false) }
        var recommendDialogHoldDown by remember { mutableStateOf(false) }
        var showCancelableLoading by remember { mutableStateOf(false) }
        var cancelableLoadingHoldDown by remember { mutableStateOf(false) }
        var showListDialog by remember { mutableStateOf(false) }
        var listDialogHoldDown by remember { mutableStateOf(false) }
        var showSingleChoiceDialog by remember { mutableStateOf(false) }
        var singleChoiceDialogHoldDown by remember { mutableStateOf(false) }
        var showMultiChoiceDialog by remember { mutableStateOf(false) }
        var multiChoiceDialogHoldDown by remember { mutableStateOf(false) }
        var showProgressDialog by remember { mutableStateOf(false) }
        var progressDialogHoldDown by remember { mutableStateOf(false) }
        var showInputDialog by remember { mutableStateOf(false) }
        var inputDialogHoldDown by remember { mutableStateOf(false) }
        var showLongDialog by remember { mutableStateOf(false) }
        var longDialogHoldDown by remember { mutableStateOf(false) }

        SmallTitle(text = "Dialog")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            CardListGroup {
                item {
                    ArrowPreference(
                        title = "Dialog (O)",
                        summary = "Click to show an OverlayDialog",
                        onClick = {
                            showSuperDialog = true
                            superDialogHoldDown = true
                        },
                        holdDownState = superDialogHoldDown,
                    )
                }
                item {
                    ArrowPreference(
                        title = "Dialog (W)",
                        summary = "Click to show a WindowDialog",
                        onClick = {
                            showWindowDialog = true
                            windowDialogHoldDown = true
                        },
                        holdDownState = windowDialogHoldDown,
                    )
                }
                item {
                    ArrowPreference(
                        title = "Loading Dialog (O)",
                        summary = "Click to show an OverlayLoadingDialog",
                        onClick = {
                            showLoadingDialog = true
                            loadingDialogHoldDown = true
                        },
                        holdDownState = loadingDialogHoldDown,
                    )
                }
                item {
                    ArrowPreference(
                        title = "Stacked Button Bar (O)",
                        summary = "Long labels flip the bar to a vertical stack",
                        onClick = {
                            showStackedDialog = true
                            stackedDialogHoldDown = true
                        },
                        holdDownState = stackedDialogHoldDown,
                    )
                }
                item {
                    ArrowPreference(
                        title = "Recommended Button (O)",
                        summary = "The recommended action becomes a primary capsule",
                        onClick = {
                            showRecommendDialog = true
                            recommendDialogHoldDown = true
                        },
                        holdDownState = recommendDialogHoldDown,
                    )
                }
                item {
                    ArrowPreference(
                        title = "Security Dialog (O)",
                        summary = "Click to show an OverlaySecurityDialog",
                        onClick = {
                            showSecurityDialog = true
                            securityDialogHoldDown = true
                        },
                        holdDownState = securityDialogHoldDown,
                    )
                }
                dialogDemoEntry(
                    title = "Cancelable Loading Dialog (O)",
                    summary = "Title, spinner, message and a cancel button",
                    holdDown = cancelableLoadingHoldDown,
                    onClick = {
                        showCancelableLoading = true
                        cancelableLoadingHoldDown = true
                    },
                )
                dialogDemoEntry(
                    title = "List Dialog (O)",
                    summary = "Items drawn as capsule buttons",
                    holdDown = listDialogHoldDown,
                    onClick = {
                        showListDialog = true
                        listDialogHoldDown = true
                    },
                )
                dialogDemoEntry(
                    title = "Single-Choice Dialog (O)",
                    summary = "Rows ending in a radio button",
                    holdDown = singleChoiceDialogHoldDown,
                    onClick = {
                        showSingleChoiceDialog = true
                        singleChoiceDialogHoldDown = true
                    },
                )
                dialogDemoEntry(
                    title = "Multi-Choice Dialog (O)",
                    summary = "Rows ending in a checkbox, at most two checked",
                    holdDown = multiChoiceDialogHoldDown,
                    onClick = {
                        showMultiChoiceDialog = true
                        multiChoiceDialogHoldDown = true
                    },
                )
                dialogDemoEntry(
                    title = "Progress Dialog (O)",
                    summary = "A 200dp horizontal progress bar",
                    holdDown = progressDialogHoldDown,
                    onClick = {
                        showProgressDialog = true
                        progressDialogHoldDown = true
                    },
                )
                dialogDemoEntry(
                    title = "Input Dialog (O)",
                    summary = "A single line-style edit text",
                    holdDown = inputDialogHoldDown,
                    onClick = {
                        showInputDialog = true
                        inputDialogHoldDown = true
                    },
                )
                dialogDemoEntry(
                    title = "Long Message Dialog (O)",
                    summary = "The message scrolls, the button bar stays in view",
                    holdDown = longDialogHoldDown,
                    onClick = {
                        showLongDialog = true
                        longDialogHoldDown = true
                    },
                )
            }
        }

        SuperDialogDemo(
            show = showSuperDialog,
            onDismissRequest = { showSuperDialog = false },
            onDismissFinished = { superDialogHoldDown = false },
        )
        WindowDialogDemo(
            show = showWindowDialog,
            onDismissRequest = { showWindowDialog = false },
            onDismissFinished = { windowDialogHoldDown = false },
        )
        StackedButtonBarDialogDemo(
            show = showStackedDialog,
            onDismissRequest = { showStackedDialog = false },
            onDismissFinished = { stackedDialogHoldDown = false },
        )
        RecommendButtonDialogDemo(
            show = showRecommendDialog,
            onDismissRequest = { showRecommendDialog = false },
            onDismissFinished = { recommendDialogHoldDown = false },
        )

        // Auto-dismiss the loading demo after a short delay, like a finished task would.
        LaunchedEffect(showLoadingDialog) {
            if (showLoadingDialog) {
                delay(3000)
                showLoadingDialog = false
            }
        }
        // Both builders sit on COUIAlertDialogBuilder, so they take the same blurred window and
        // material stroke as the alert dialogs above.
        val dialogBlur = panelBlur(MaterialBlurDefaults::dialog)
        OverlayLoadingDialog(
            show = showLoadingDialog,
            backgroundModifier = dialogBlur?.backgroundModifier ?: Modifier,
            text = "Loading...",
            backgroundColor = dialogBlur?.backgroundColor ?: DialogDefaults.backgroundColor(),
            onDismissRequest = { showLoadingDialog = false },
            onDismissFinished = { loadingDialogHoldDown = false },
            materialStroke = dialogBlur != null,
        )

        OverlaySecurityDialog(
            show = showSecurityDialog,
            title = "Security Notice",
            summary = "This feature needs to connect to the network.",
            statement = "Tap and view Privacy Policy for more information.",
            statementLinkText = "Privacy Policy",
            onLinkClick = { /* Open the privacy policy */ },
            onConfirm = { _ -> showSecurityDialog = false },
            onCancel = { showSecurityDialog = false },
            backgroundModifier = dialogBlur?.backgroundModifier ?: Modifier,
            backgroundColor = dialogBlur?.backgroundColor ?: DialogDefaults.backgroundColor(),
            onDismissFinished = { securityDialogHoldDown = false },
            materialStroke = dialogBlur != null,
        )

        OverlayLoadingDialog(
            show = showCancelableLoading,
            title = "Downloading",
            text = "Please wait...",
            cancelAction = DialogButtonBarAction(text = "Cancel", onClick = { showCancelableLoading = false }),
            backgroundModifier = dialogBlur?.backgroundModifier ?: Modifier,
            backgroundColor = dialogBlur?.backgroundColor ?: DialogDefaults.backgroundColor(),
            onDismissFinished = { cancelableLoadingHoldDown = false },
            materialStroke = dialogBlur != null,
        )
        ListDialogsDemo(
            showList = showListDialog,
            showSingle = showSingleChoiceDialog,
            showMulti = showMultiChoiceDialog,
            onListDismiss = { showListDialog = false },
            onSingleDismiss = { showSingleChoiceDialog = false },
            onMultiDismiss = { showMultiChoiceDialog = false },
            onListFinished = { listDialogHoldDown = false },
            onSingleFinished = { singleChoiceDialogHoldDown = false },
            onMultiFinished = { multiChoiceDialogHoldDown = false },
        )
        ProgressDialogDemo(
            show = showProgressDialog,
            onDismissRequest = { showProgressDialog = false },
            onDismissFinished = { progressDialogHoldDown = false },
        )
        InputDialogDemo(
            show = showInputDialog,
            onDismissRequest = { showInputDialog = false },
            onDismissFinished = { inputDialogHoldDown = false },
        )
        LongMessageDialogDemo(
            show = showLongDialog,
            onDismissRequest = { showLongDialog = false },
            onDismissFinished = { longDialogHoldDown = false },
        )
    }
}

/** One arrow row of the dialog card. */
private fun CardListGroupScope.dialogDemoEntry(
    title: String,
    summary: String,
    holdDown: Boolean,
    onClick: () -> Unit,
) {
    item {
        ArrowPreference(
            title = title,
            summary = summary,
            onClick = onClick,
            holdDownState = holdDown,
        )
    }
}

/** The list, single-choice and multi-choice dialogs (COUIAlertDialogBuilder.setItems / ChoiceListAdapter). */
@Composable
private fun ListDialogsDemo(
    showList: Boolean,
    showSingle: Boolean,
    showMulti: Boolean,
    onListDismiss: () -> Unit,
    onSingleDismiss: () -> Unit,
    onMultiDismiss: () -> Unit,
    onListFinished: () -> Unit,
    onSingleFinished: () -> Unit,
    onMultiFinished: () -> Unit,
) {
    val blur = panelBlur(MaterialBlurDefaults::dialog)
    var selected by remember { mutableIntStateOf(0) }
    var checked by remember { mutableStateOf(listOf(true, false, false, false)) }
    OverlayListDialog(
        show = showList,
        title = "Share to",
        items = listOf(
            ListDialogItem(text = "Copy link"),
            ListDialogItem(text = "Save image", summary = "Saved to the gallery"),
            ListDialogItem(text = "Delete", textColor = COUITheme.colorScheme.error),
        ),
        onItemClick = { onListDismiss() },
        negative = DialogButtonBarAction(text = "Cancel", onClick = onListDismiss),
        backgroundModifier = blur?.backgroundModifier ?: Modifier,
        backgroundColor = blur?.backgroundColor ?: DialogDefaults.backgroundColor(),
        onDismissRequest = onListDismiss,
        onDismissFinished = onListFinished,
        materialStroke = blur != null,
    )
    OverlaySingleChoiceDialog(
        show = showSingle,
        title = "Screen timeout",
        summary = "The screen turns off after this period of inactivity.",
        items = listOf(
            ListDialogItem(text = "30 seconds"),
            ListDialogItem(text = "1 minute"),
            ListDialogItem(text = "5 minutes"),
            ListDialogItem(text = "Never", summary = "Uses more power"),
        ),
        selectedIndex = selected,
        onSelectedIndexChange = { selected = it },
        negative = DialogButtonBarAction(text = "Cancel", onClick = onSingleDismiss),
        positive = DialogButtonBarAction(text = "OK", onClick = onSingleDismiss),
        backgroundModifier = blur?.backgroundModifier ?: Modifier,
        backgroundColor = blur?.backgroundColor ?: DialogDefaults.backgroundColor(),
        onDismissRequest = onSingleDismiss,
        onDismissFinished = onSingleFinished,
        materialStroke = blur != null,
    )
    OverlayMultiChoiceDialog(
        show = showMulti,
        title = "Sync items",
        items = listOf(
            ListDialogItem(text = "Contacts"),
            ListDialogItem(text = "Photos", summary = "Wi-Fi only"),
            ListDialogItem(text = "Notes"),
            ListDialogItem(text = "Calendar", enabled = false),
        ),
        checked = checked,
        onCheckedChange = { index, value -> checked = checked.toMutableList().also { it[index] = value } },
        maxCheckedCount = 2,
        negative = DialogButtonBarAction(text = "Cancel", onClick = onMultiDismiss),
        positive = DialogButtonBarAction(text = "OK", onClick = onMultiDismiss),
        backgroundModifier = blur?.backgroundModifier ?: Modifier,
        backgroundColor = blur?.backgroundColor ?: DialogDefaults.backgroundColor(),
        onDismissRequest = onMultiDismiss,
        onDismissFinished = onMultiFinished,
        materialStroke = blur != null,
    )
}

/** The horizontal progress dialog (COUIAlertDialog.Progress.Cancelable), filled by a fake task. */
@Composable
private fun ProgressDialogDemo(
    show: Boolean,
    onDismissRequest: () -> Unit,
    onDismissFinished: () -> Unit,
) {
    var progress by remember { mutableFloatStateOf(0f) }
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)
    LaunchedEffect(show) {
        if (show) {
            progress = 0f
            while (progress < 1f) {
                delay(60)
                progress = (progress + 0.02f).coerceAtMost(1f)
            }
            currentOnDismissRequest()
        }
    }
    val blur = panelBlur(MaterialBlurDefaults::dialog)
    OverlayProgressDialog(
        show = show,
        progress = progress,
        title = "Downloading",
        cancelAction = DialogButtonBarAction(text = "Cancel", onClick = onDismissRequest),
        backgroundModifier = blur?.backgroundModifier ?: Modifier,
        backgroundColor = blur?.backgroundColor ?: DialogDefaults.backgroundColor(),
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        materialStroke = blur != null,
    )
}

/** The single-edit input dialog (COUIAlertDialog.SingleEdit). */
@Composable
private fun InputDialogDemo(
    show: Boolean,
    onDismissRequest: () -> Unit,
    onDismissFinished: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    val blur = panelBlur(MaterialBlurDefaults::dialog)
    OverlayInputDialog(
        show = show,
        value = name,
        onValueChange = { name = it },
        title = "Rename",
        label = "Enter a name",
        negative = DialogButtonBarAction(text = "Cancel", onClick = onDismissRequest),
        positive = DialogButtonBarAction(text = "Save", enabled = name.isNotEmpty(), onClick = onDismissRequest),
        backgroundModifier = blur?.backgroundModifier ?: Modifier,
        backgroundColor = blur?.backgroundColor ?: DialogDefaults.backgroundColor(),
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        materialStroke = blur != null,
    )
}

/** A message taller than the panel: it scrolls while the button bar stays in view. */
@Composable
private fun LongMessageDialogDemo(
    show: Boolean,
    onDismissRequest: () -> Unit,
    onDismissFinished: () -> Unit,
) {
    val blur = panelBlur(MaterialBlurDefaults::dialog)
    val message = remember {
        (1..40).joinToString(separator = " ") { "Line $it of a long message that keeps going." }
    }
    OverlayDialog(
        show = show,
        title = "Terms of Service",
        summary = message,
        backgroundColor = blur?.backgroundColor ?: DialogDefaults.backgroundColor(),
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        backgroundModifier = blur?.backgroundModifier ?: Modifier,
        materialStroke = blur != null,
        content = {
            DialogButtonBar(
                negative = DialogButtonBarAction(text = "Disagree", onClick = onDismissRequest),
                positive = DialogButtonBarAction(text = "Agree", onClick = onDismissRequest),
            )
        },
    )
}

/**
 * Shows the automatic vertical stacking of [DialogButtonBar]: the labels are wide enough that
 * COUIButtonBarLayout's needSetButVertical check fails, so the whole bar flips to a stack.
 */
@Composable
private fun StackedButtonBarDialogDemo(
    show: Boolean,
    onDismissRequest: () -> Unit,
    onDismissFinished: () -> Unit,
) {
    val blur = panelBlur(MaterialBlurDefaults::dialog)
    OverlayDialog(
        show = show,
        title = "Delete Backup",
        summary = "The button labels below do not fit side by side, so the bar stacks them. " +
            "The neutral action takes the error color.",
        backgroundColor = blur?.backgroundColor ?: DialogDefaults.backgroundColor(),
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        backgroundModifier = blur?.backgroundModifier ?: Modifier,
        materialStroke = blur != null,
        content = {
            DialogButtonBar(
                negative = DialogButtonBarAction(text = "Not Now", onClick = onDismissRequest),
                positive = DialogButtonBarAction(
                    text = "Delete Backup And Local Copies",
                    onClick = onDismissRequest,
                ),
                neutral = DialogButtonBarAction(text = "Delete Everything", onClick = onDismissRequest),
            )
        },
    )
}

/**
 * Shows the recommend tier of [DialogButtonBar] (COUIAlertDialogBuilder `isRecommend = true`): the
 * recommended action is a primary capsule while the other one stays secondary.
 */
@Composable
private fun RecommendButtonDialogDemo(
    show: Boolean,
    onDismissRequest: () -> Unit,
    onDismissFinished: () -> Unit,
) {
    val blur = panelBlur(MaterialBlurDefaults::dialog)
    OverlayDialog(
        show = show,
        title = "Turn On Backup",
        summary = "The recommended action is drawn as a primary capsule; the other buttons stay secondary.",
        backgroundColor = blur?.backgroundColor ?: DialogDefaults.backgroundColor(),
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        backgroundModifier = blur?.backgroundModifier ?: Modifier,
        materialStroke = blur != null,
        content = {
            DialogButtonBar(
                negative = DialogButtonBarAction(text = "Not Now", onClick = onDismissRequest),
                positive = DialogButtonBarAction(
                    text = "Turn On",
                    recommended = true,
                    onClick = onDismissRequest,
                ),
            )
        },
    )
}

@Composable
private fun SuperDialogDemo(
    show: Boolean,
    onDismissRequest: () -> Unit,
    onDismissFinished: () -> Unit,
) {
    // COUI 17 blurs the window behind the dialog when the material blur is on; the page backdrop
    // stands in for that window here.
    val blur = panelBlur(MaterialBlurDefaults::dialog)
    OverlayDialog(
        show = show,
        title = "Dialog (O)",
        summary = "A dialog component inside COUIPopupHost.",
        backgroundColor = blur?.backgroundColor ?: DialogDefaults.backgroundColor(),
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        backgroundModifier = blur?.backgroundModifier ?: Modifier,
        materialStroke = blur != null,
        content = {
            DialogButtonBar(
                negative = DialogButtonBarAction(text = "Cancel", onClick = onDismissRequest),
                positive = DialogButtonBarAction(text = "Confirm", onClick = onDismissRequest),
            )
        },
    )
}

@Composable
private fun WindowDialogDemo(
    show: Boolean,
    onDismissRequest: () -> Unit,
    onDismissFinished: () -> Unit,
) {
    WindowDialog(
        show = show,
        title = "Dialog (W)",
        summary = "A window-level dialog, no COUIPopupHost required.",
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        content = {
            val dismissState = LocalDismissState.current
            DialogButtonBar(
                negative = DialogButtonBarAction(text = "Cancel", onClick = { dismissState?.invoke() }),
                positive = DialogButtonBarAction(text = "Confirm", onClick = { dismissState?.invoke() }),
            )
        },
    )
}
