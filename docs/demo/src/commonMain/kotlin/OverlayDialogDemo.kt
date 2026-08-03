// Copyright 2025, compose-coui-ui contributors
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
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.Scaffold
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.layout.DialogButtonBar
import io.github.suqi8.coui.kmp.layout.DialogButtonBarAction
import io.github.suqi8.coui.kmp.overlay.OverlayDialog

@Composable
fun OverlayDialogDemo() {
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
                Card {
                    TextButton(
                        text = "Show an OverlayDialog",
                        onClick = { showDialog = true },
                    )
                    OverlayDialog(
                        title = "OverlayDialog Title",
                        summary = "This is a basic dialog example that can contain various content.",
                        show = showDialog,
                        onDismissRequest = { showDialog = false },
                    ) {
                        // COUI button bar: it flips itself to a vertical stack when the labels
                        // no longer fit side by side (COUIButtonBarLayout).
                        DialogButtonBar(
                            negative = DialogButtonBarAction(text = "Cancel", onClick = { showDialog = false }),
                            positive = DialogButtonBarAction(text = "Confirm", onClick = { showDialog = false }),
                        )
                    }
                }
            }
        }
    }
}
