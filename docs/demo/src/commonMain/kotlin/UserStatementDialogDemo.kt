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
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.FullPageStatementDefaults
import io.github.suqi8.coui.kmp.basic.Scaffold
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.layout.UserStatementListItem
import io.github.suqi8.coui.kmp.overlay.OverlayUserStatementDialog
import io.github.suqi8.coui.kmp.theme.COUITheme

@Composable
fun UserStatementDialogDemo() {
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
                var showMessage by remember { mutableStateOf(false) }
                var showList by remember { mutableStateOf(false) }
                val linkStyles = FullPageStatementDefaults.linkStyles()
                val statement = remember(linkStyles) {
                    buildAnnotatedString {
                        append("Continuing means you agree to the ")
                        withLink(LinkAnnotation.Clickable(tag = "agreement", styles = linkStyles) {}) {
                            append("User Agreement")
                        }
                        append(" and the ")
                        withLink(LinkAnnotation.Clickable(tag = "privacy", styles = linkStyles) {}) {
                            append("Privacy Policy")
                        }
                        append(".")
                    }
                }
                val logo = ColorPainter(COUITheme.colorScheme.primary)

                TextButton(
                    text = "User statement with a message",
                    onClick = { showMessage = true },
                    modifier = Modifier.fillMaxWidth(),
                )
                TextButton(
                    text = "User statement with a list",
                    onClick = { showList = true },
                    modifier = Modifier.fillMaxWidth(),
                )

                OverlayUserStatementDialog(
                    show = showMessage,
                    onConfirm = { showMessage = false },
                    onExit = { showMessage = false },
                    logo = logo,
                    title = "Welcome to",
                    appName = "Notes",
                    appMessage = "Write, organize and sync your notes everywhere.",
                    statement = statement,
                    onDismissRequest = { showMessage = false },
                )
                OverlayUserStatementDialog(
                    show = showList,
                    onConfirm = { showList = false },
                    onExit = { showList = false },
                    logo = logo,
                    title = "Welcome to",
                    appName = "Notes",
                    listItems = listOf(
                        UserStatementListItem(title = "Network", summary = "Sync your notes across devices", icon = logo),
                        UserStatementListItem(title = "Storage", summary = "Save attachments locally", icon = logo),
                    ),
                    statement = statement,
                    onDismissRequest = { showList = false },
                )
            }
        }
    }
}
