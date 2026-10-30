// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.EmptyState
import io.github.suqi8.coui.kmp.theme.COUITheme

@Composable
fun EmptyStateDemo() {
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
            var refreshes by remember { mutableIntStateOf(0) }
            Card {
                EmptyState(
                    title = "No content",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp),
                    subtitle = if (refreshes == 0) "Nothing has been added yet" else "Refreshed $refreshes times",
                    actionText = "Refresh",
                    onActionClick = { refreshes++ },
                    illustration = {
                        val tray = COUITheme.colorScheme.secondaryContainer
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawRoundRect(
                                color = tray,
                                topLeft = Offset(size.width * 0.3f, size.height * 0.3f),
                                size = Size(size.width * 0.4f, size.height * 0.45f),
                                cornerRadius = CornerRadius(size.height * 0.06f),
                            )
                        }
                    },
                )
            }
        }
    }
}
