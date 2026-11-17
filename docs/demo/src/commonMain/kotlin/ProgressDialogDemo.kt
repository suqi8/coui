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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.Scaffold
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.layout.DialogButtonBarAction
import io.github.suqi8.coui.kmp.overlay.OverlayProgressDialog
import kotlinx.coroutines.delay

@Composable
fun ProgressDialogDemo() {
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
                var progress by remember { mutableFloatStateOf(0f) }
                LaunchedEffect(showDialog) {
                    if (showDialog) {
                        progress = 0f
                        while (progress < 1f) {
                            delay(60)
                            progress = (progress + 0.02f).coerceAtMost(1f)
                        }
                        showDialog = false
                    }
                }
                Card {
                    TextButton(text = "Show a progress dialog", onClick = { showDialog = true })
                }
                OverlayProgressDialog(
                    show = showDialog,
                    progress = progress,
                    title = "Downloading",
                    cancelAction = DialogButtonBarAction(text = "Cancel", onClick = { showDialog = false }),
                    onDismissRequest = { showDialog = false },
                )
            }
        }
    }
}
