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
import io.github.suqi8.coui.kmp.layout.DialogButtonBar
import io.github.suqi8.coui.kmp.layout.DialogButtonBarAction
import io.github.suqi8.coui.kmp.overlay.OverlayDialog
import io.github.suqi8.coui.kmp.theme.COUITheme

/** The button bar layouts the demo can open, each in its own dialog. */
private enum class DialogButtonBarDemoVariant(val label: String, val summary: String) {
    TwoButtons("Two buttons", "Two short labels stay side by side."),
    LongLabels("Long labels", "A label too wide for its cell flips the whole bar to a vertical stack."),
    ThreeButtons("Three buttons", "The neutral action is drawn in the error color. Three buttons always stack."),
    Recommended("Recommended action", "The recommended action becomes a primary capsule."),
    Disabled("Disabled action", "A disabled action keeps its place but cannot be tapped."),
}

/** A bar action that reports its own label when tapped. */
private fun barAction(
    text: String,
    onClick: (String) -> Unit,
    enabled: Boolean = true,
    recommended: Boolean = false,
): DialogButtonBarAction = DialogButtonBarAction(
    text = text,
    enabled = enabled,
    recommended = recommended,
    onClick = { onClick(text) },
)

@Composable
fun DialogButtonBarDemo() {
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
                var variant by remember { mutableStateOf(DialogButtonBarDemoVariant.TwoButtons) }
                var lastTapped by remember { mutableStateOf("") }

                DialogButtonBarDemoVariant.entries.forEach { item ->
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
                    text = if (lastTapped.isEmpty()) "Open a dialog and tap a button" else "Tapped: $lastTapped",
                    color = COUITheme.colorScheme.labelSecondary,
                )

                OverlayDialog(
                    show = showDialog,
                    title = variant.label,
                    summary = variant.summary,
                    onDismissRequest = { showDialog = false },
                ) {
                    val onTap: (String) -> Unit = { label ->
                        lastTapped = label
                        showDialog = false
                    }
                    when (variant) {
                        DialogButtonBarDemoVariant.TwoButtons -> DialogButtonBar(
                            negative = barAction("Cancel", onTap),
                            positive = barAction("Confirm", onTap),
                        )

                        DialogButtonBarDemoVariant.LongLabels -> DialogButtonBar(
                            negative = barAction("Not Now", onTap),
                            positive = barAction("Delete Backup And Local Copies", onTap),
                        )

                        DialogButtonBarDemoVariant.ThreeButtons -> DialogButtonBar(
                            negative = barAction("Cancel", onTap),
                            positive = barAction("Save", onTap),
                            neutral = barAction("Discard", onTap),
                        )

                        DialogButtonBarDemoVariant.Recommended -> DialogButtonBar(
                            negative = barAction("Not Now", onTap),
                            positive = barAction("Turn On", onTap, recommended = true),
                        )

                        DialogButtonBarDemoVariant.Disabled -> DialogButtonBar(
                            negative = barAction("Cancel", onTap),
                            positive = barAction("Confirm", onTap, enabled = false),
                        )
                    }
                }
            }
        }
    }
}
