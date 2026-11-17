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
import androidx.compose.runtime.mutableIntStateOf
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
import io.github.suqi8.coui.kmp.layout.ListDialogItem
import io.github.suqi8.coui.kmp.overlay.OverlayListDialog
import io.github.suqi8.coui.kmp.overlay.OverlayMultiChoiceDialog
import io.github.suqi8.coui.kmp.overlay.OverlaySingleChoiceDialog
import io.github.suqi8.coui.kmp.theme.COUITheme

@Composable
fun ListDialogDemo() {
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
                var showList by remember { mutableStateOf(false) }
                var showSingle by remember { mutableStateOf(false) }
                var showMulti by remember { mutableStateOf(false) }
                var selected by remember { mutableIntStateOf(0) }
                var checked by remember { mutableStateOf(listOf(true, false, false)) }
                Card {
                    TextButton(text = "Show a list dialog", onClick = { showList = true })
                    TextButton(text = "Show a single-choice dialog", onClick = { showSingle = true })
                    TextButton(text = "Show a multi-choice dialog", onClick = { showMulti = true })
                }
                OverlayListDialog(
                    show = showList,
                    title = "Share to",
                    items = listOf(
                        ListDialogItem(text = "Copy link"),
                        ListDialogItem(text = "Save image", summary = "Saved to the gallery"),
                        ListDialogItem(text = "Delete", textColor = COUITheme.colorScheme.error),
                    ),
                    onItemClick = { showList = false },
                    negative = DialogButtonBarAction(text = "Cancel", onClick = { showList = false }),
                    onDismissRequest = { showList = false },
                )
                OverlaySingleChoiceDialog(
                    show = showSingle,
                    title = "Screen timeout",
                    items = listOf(
                        ListDialogItem(text = "30 seconds"),
                        ListDialogItem(text = "1 minute"),
                        ListDialogItem(text = "Never", summary = "Uses more power"),
                    ),
                    selectedIndex = selected,
                    onSelectedIndexChange = { selected = it },
                    negative = DialogButtonBarAction(text = "Cancel", onClick = { showSingle = false }),
                    positive = DialogButtonBarAction(text = "OK", onClick = { showSingle = false }),
                    onDismissRequest = { showSingle = false },
                )
                OverlayMultiChoiceDialog(
                    show = showMulti,
                    title = "Sync items",
                    items = listOf(
                        ListDialogItem(text = "Contacts"),
                        ListDialogItem(text = "Photos"),
                        ListDialogItem(text = "Notes", enabled = false),
                    ),
                    checked = checked,
                    onCheckedChange = { index, value ->
                        checked = checked.toMutableList().also { it[index] = value }
                    },
                    negative = DialogButtonBarAction(text = "Cancel", onClick = { showMulti = false }),
                    positive = DialogButtonBarAction(text = "OK", onClick = { showMulti = false }),
                    onDismissRequest = { showMulti = false },
                )
            }
        }
    }
}
