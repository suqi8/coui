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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Scaffold
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.layout.StatementPrivacyItem
import io.github.suqi8.coui.kmp.overlay.OverlayIndividualStatementDialog
import io.github.suqi8.coui.kmp.theme.COUITheme

@Composable
fun IndividualStatementDialogDemo() {
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
                var showDialog by remember { mutableStateOf(false) }
                var result by remember { mutableStateOf("") }
                val privacyItems = remember {
                    listOf(
                        StatementPrivacyItem(title = "Personalized suggestions", summary = "Use your usage data to tailor suggestions"),
                        StatementPrivacyItem(title = "Crash reports", summary = "Send anonymous crash logs"),
                    )
                }

                TextButton(
                    text = "Show an individual statement",
                    onClick = { showDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                )
                if (result.isNotEmpty()) {
                    Text(text = result, color = COUITheme.colorScheme.labelSecondary)
                }

                OverlayIndividualStatementDialog(
                    show = showDialog,
                    onConfirm = { checked ->
                        result = "Enabled: " + checked.joinToString { it.title }
                        showDialog = false
                    },
                    onExit = {
                        result = "Exited"
                        showDialog = false
                    },
                    title = "Optional features",
                    statement = AnnotatedString("Choose the optional features to turn on. You can change them later in Settings."),
                    privacyItems = privacyItems,
                    onDismissRequest = { showDialog = false },
                )
            }
        }
    }
}
