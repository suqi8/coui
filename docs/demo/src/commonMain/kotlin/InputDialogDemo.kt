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
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.Scaffold
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.layout.DialogButtonBarAction
import io.github.suqi8.coui.kmp.overlay.OverlayInputDialog

@Composable
fun InputDialogDemo() {
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
                var showEdit by remember { mutableStateOf(false) }
                var showCounted by remember { mutableStateOf(false) }
                var name by remember { mutableStateOf("") }
                var note by remember { mutableStateOf("") }
                Card {
                    TextButton(text = "Show an input dialog", onClick = { showEdit = true })
                    TextButton(text = "Show an input dialog with a counter", onClick = { showCounted = true })
                }
                OverlayInputDialog(
                    show = showEdit,
                    value = name,
                    onValueChange = { name = it },
                    title = "Rename",
                    label = "Enter a name",
                    negative = DialogButtonBarAction(text = "Cancel", onClick = { showEdit = false }),
                    positive = DialogButtonBarAction(text = "Save", enabled = name.isNotEmpty(), onClick = { showEdit = false }),
                    onDismissRequest = { showEdit = false },
                )
                OverlayInputDialog(
                    show = showCounted,
                    value = note,
                    onValueChange = { note = it },
                    title = "Add a note",
                    label = "Note",
                    maxCount = 30,
                    negative = DialogButtonBarAction(text = "Cancel", onClick = { showCounted = false }),
                    positive = DialogButtonBarAction(text = "Save", onClick = { showCounted = false }),
                    onDismissRequest = { showCounted = false },
                )
            }
        }
    }
}
