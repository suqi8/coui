// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Scaffold
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.overlay.OverlaySecurityDialog
import io.github.suqi8.coui.kmp.theme.COUITheme

@Composable
fun SecurityDialogDemo() {
    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(demoBackground()),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                Modifier
                    .padding(16.dp)
                    .widthIn(max = 600.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                var showFull by remember { mutableStateOf(false) }
                var showPlain by remember { mutableStateOf(false) }
                var result by remember { mutableStateOf("") }

                TextButton(
                    text = "Statement and checkbox",
                    onClick = { showFull = true },
                    modifier = Modifier.fillMaxWidth(),
                )
                TextButton(
                    text = "Without statement or checkbox",
                    onClick = { showPlain = true },
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = result.ifEmpty { "Open a dialog and make a choice" },
                    color = COUITheme.colorScheme.labelSecondary,
                )

                OverlaySecurityDialog(
                    show = showFull,
                    onConfirm = { dontAskAgain ->
                        result = if (dontAskAgain) "Confirmed, won't ask again" else "Confirmed"
                        showFull = false
                    },
                    onCancel = {
                        result = "Canceled"
                        showFull = false
                    },
                    title = "Security notice",
                    summary = "This feature needs to connect to the network.",
                    statement = "Tap and view Privacy Policy for more information.",
                    statementLinkText = "Privacy Policy",
                    onLinkClick = { result = "Opened the Privacy Policy" },
                )
                OverlaySecurityDialog(
                    show = showPlain,
                    onConfirm = {
                        result = "Feature enabled"
                        showPlain = false
                    },
                    onCancel = {
                        result = "Canceled"
                        showPlain = false
                    },
                    title = "Enable feature",
                    summary = "Do you want to enable this feature?",
                    checkboxText = null,
                    confirmText = "Enable",
                )
            }
        }
    }
}
