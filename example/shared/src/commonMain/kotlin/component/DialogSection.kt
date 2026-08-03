// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.HorizontalDivider
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.layout.DialogButtonBar
import io.github.suqi8.coui.kmp.layout.DialogButtonBarAction
import io.github.suqi8.coui.kmp.overlay.OverlayDialog
import io.github.suqi8.coui.kmp.overlay.OverlayLoadingDialog
import io.github.suqi8.coui.kmp.overlay.OverlaySecurityDialog
import io.github.suqi8.coui.kmp.preference.ArrowPreference
import io.github.suqi8.coui.kmp.theme.LocalDismissState
import io.github.suqi8.coui.kmp.window.WindowDialog
import kotlinx.coroutines.delay

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

        SmallTitle(text = "Dialog")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            ArrowPreference(
                title = "Dialog (O)",
                summary = "Click to show an OverlayDialog",
                onClick = {
                    showSuperDialog = true
                    superDialogHoldDown = true
                },
                holdDownState = superDialogHoldDown,
            )
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            ArrowPreference(
                title = "Dialog (W)",
                summary = "Click to show a WindowDialog",
                onClick = {
                    showWindowDialog = true
                    windowDialogHoldDown = true
                },
                holdDownState = windowDialogHoldDown,
            )
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            ArrowPreference(
                title = "Loading Dialog (O)",
                summary = "Click to show an OverlayLoadingDialog",
                onClick = {
                    showLoadingDialog = true
                    loadingDialogHoldDown = true
                },
                holdDownState = loadingDialogHoldDown,
            )
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            ArrowPreference(
                title = "Stacked Button Bar (O)",
                summary = "Long labels flip the bar to a vertical stack",
                onClick = {
                    showStackedDialog = true
                    stackedDialogHoldDown = true
                },
                holdDownState = stackedDialogHoldDown,
            )
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
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

        // Auto-dismiss the loading demo after a short delay, like a finished task would.
        LaunchedEffect(showLoadingDialog) {
            if (showLoadingDialog) {
                delay(3000)
                showLoadingDialog = false
            }
        }
        OverlayLoadingDialog(
            show = showLoadingDialog,
            text = "Loading...",
            onDismissRequest = { showLoadingDialog = false },
            onDismissFinished = { loadingDialogHoldDown = false },
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
            onDismissFinished = { securityDialogHoldDown = false },
        )
    }
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
    OverlayDialog(
        show = show,
        title = "Delete Backup",
        summary = "The button labels below do not fit side by side, so the bar stacks them.",
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        content = {
            DialogButtonBar(
                negative = DialogButtonBarAction(text = "Not Now", onClick = onDismissRequest),
                positive = DialogButtonBarAction(
                    text = "Delete Backup And Local Copies",
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
    OverlayDialog(
        show = show,
        title = "Dialog (O)",
        summary = "A dialog component inside COUIPopupHost.",
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
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
