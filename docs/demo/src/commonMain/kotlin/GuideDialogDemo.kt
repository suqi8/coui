// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Scaffold
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.layout.GuideDialogDefaults
import io.github.suqi8.coui.kmp.layout.GuideDialogPage
import io.github.suqi8.coui.kmp.overlay.OverlayGuideDialog
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.window.WindowGuideDialog

@Composable
fun GuideDialogDemo() {
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
                var showOverlay by remember { mutableStateOf(false) }
                var showWindow by remember { mutableStateOf(false) }
                val primary = COUITheme.colorScheme.primary
                val pages = remember(primary) {
                    listOf(
                        GuideDialogPage(
                            title = "Swipe to switch",
                            description = "Swipe left or right on the bottom bar to switch between recent apps.",
                            image = ColorPainter(primary.copy(alpha = 0.25f)),
                        ),
                        GuideDialogPage(
                            title = "Quick reply",
                            description = "Pull down a notification to reply without leaving the current app.",
                            image = ColorPainter(primary.copy(alpha = 0.4f)),
                        ),
                        GuideDialogPage(
                            title = "All set",
                            description = "You can find these tips again in Settings.",
                            image = ColorPainter(primary.copy(alpha = 0.55f)),
                        ),
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(text = "OverlayGuideDialog", onClick = { showOverlay = true }, minWidth = 0.dp)
                    TextButton(text = "WindowGuideDialog", onClick = { showWindow = true }, minWidth = 0.dp)
                }
                OverlayGuideDialog(
                    show = showOverlay,
                    pages = pages,
                    onStart = { showOverlay = false },
                    onDismissRequest = { showOverlay = false },
                    imageHeight = GuideDialogDefaults.ImageHeightSmall,
                )
                WindowGuideDialog(
                    show = showWindow,
                    pages = pages,
                    onStart = { showWindow = false },
                    onDismissRequest = { showWindow = false },
                    imageHeight = GuideDialogDefaults.ImageHeightSmall,
                )
            }
        }
    }
}
