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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Scaffold
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.layout.DialogButtonBarAction
import io.github.suqi8.coui.kmp.overlay.OverlayLoadingDialog
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.delay

/** The loading dialog forms the demo can open. */
private enum class LoadingDialogDemoVariant(val label: String) {
    SpinnerOnly("Spinner only"),
    WithMessage("With a message"),
    Cancelable("Cancelable with a title"),
}

@Composable
fun LoadingDialogDemo() {
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
                var variant by remember { mutableStateOf(LoadingDialogDemoVariant.SpinnerOnly) }
                LaunchedEffect(showDialog) {
                    // Stand-in for real work: the dialog hides itself once the "task" is done.
                    if (showDialog) {
                        delay(if (variant == LoadingDialogDemoVariant.Cancelable) 5000L else 2000L)
                        showDialog = false
                    }
                }

                LoadingDialogDemoVariant.entries.forEach { item ->
                    TextButton(
                        text = item.label,
                        onClick = {
                            variant = item
                            showDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Text(
                    text = "The dialog closes itself when the task ends",
                    color = COUITheme.colorScheme.labelSecondary,
                )

                val cancelable = variant == LoadingDialogDemoVariant.Cancelable
                OverlayLoadingDialog(
                    show = showDialog,
                    text = when (variant) {
                        LoadingDialogDemoVariant.SpinnerOnly -> null
                        LoadingDialogDemoVariant.WithMessage -> "Loading..."
                        LoadingDialogDemoVariant.Cancelable -> "Please wait..."
                    },
                    title = if (cancelable) "Downloading" else null,
                    cancelAction = if (cancelable) {
                        DialogButtonBarAction(text = "Cancel", onClick = { showDialog = false })
                    } else {
                        null
                    },
                )
            }
        }
    }
}
