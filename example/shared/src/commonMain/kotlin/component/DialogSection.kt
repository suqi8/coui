// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.ButtonDefaults
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.HorizontalDivider
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.layout.DialogDefaults
import io.github.suqi8.coui.kmp.overlay.OverlayDialog
import io.github.suqi8.coui.kmp.overlay.OverlayLoadingDialog
import io.github.suqi8.coui.kmp.overlay.OverlaySecurityDialog
import io.github.suqi8.coui.kmp.preference.ArrowPreference
import io.github.suqi8.coui.kmp.theme.COUITheme
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
 * COUI alert dialog button bar: two borderless primary-tinted text buttons split by a hairline
 * vertical divider (COUIButtonBarLayout horizontal layout, divider colored couiColorDivider).
 * Every metric comes from [DialogDefaults] so the bar matches COUIButtonBarLayout's measured
 * result: a 58dp min height, 24dp horizontal / 12dp top / 22dp bottom button paddings (so the
 * panel bottom inset is carried by the buttons), and a 1dp divider inset 17dp / 21dp.
 * Each button is a full-cell square-cornered rect with no press scale, matching
 * COUIAlertDialogBottomButtonNewNormal (drawableRadius=0dp, scaleEnable=false).
 */
@Composable
private fun DialogButtonBar(
    onNegative: () -> Unit,
    onPositive: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        TextButton(
            text = "Cancel",
            onClick = onNegative,
            modifier = Modifier.weight(1f).fillMaxHeight(),
            pressScaleEnabled = false,
            cornerRadius = 0.dp,
            minHeight = DialogDefaults.ButtonBarMinHeight,
            insideMargin = DialogDefaults.ButtonBarInsideMargin,
            colors = ButtonDefaults.textButtonColorsBorderless(),
        )
        Box(
            modifier = Modifier
                .padding(
                    top = DialogDefaults.ButtonBarDividerInsetTop,
                    bottom = DialogDefaults.ButtonBarDividerInsetBottom,
                )
                .width(DialogDefaults.ButtonBarDividerThickness)
                .fillMaxHeight()
                .background(COUITheme.colorScheme.dividerLine),
        )
        TextButton(
            text = "Confirm",
            onClick = onPositive,
            modifier = Modifier.weight(1f).fillMaxHeight(),
            pressScaleEnabled = false,
            cornerRadius = 0.dp,
            minHeight = DialogDefaults.ButtonBarMinHeight,
            insideMargin = DialogDefaults.ButtonBarInsideMargin,
            colors = ButtonDefaults.textButtonColorsBorderless(),
        )
    }
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
                onNegative = onDismissRequest,
                onPositive = onDismissRequest,
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
                onNegative = { dismissState?.invoke() },
                onPositive = { dismissState?.invoke() },
            )
        },
    )
}
