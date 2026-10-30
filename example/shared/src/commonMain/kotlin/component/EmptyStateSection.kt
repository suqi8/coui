// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.EmptyState
import io.github.suqi8.coui.kmp.basic.EmptyStateSizeType
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.theme.COUITheme

fun LazyListScope.emptyStateSection() {
    item(key = "emptyState") {
        SmallTitle(text = "EmptyState")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            var refreshes by remember { mutableIntStateOf(0) }
            EmptyState(
                title = "No content",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp),
                subtitle = if (refreshes == 0) "Nothing has been added yet" else "Refreshed $refreshes times, still empty",
                actionText = "Refresh",
                onActionClick = { refreshes++ },
                illustration = { EmptyIllustration() },
            )
        }
    }
    item(key = "emptyStateSmall") {
        SmallTitle(text = "EmptyState (small, text only)")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            EmptyState(
                title = "No search results",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                subtitle = "Try another keyword",
                sizeType = EmptyStateSizeType.Small,
                illustration = { EmptyIllustration() },
            )
        }
    }
}

/** A placeholder illustration: an empty tray. */
@Composable
private fun EmptyIllustration() {
    val tray = COUITheme.colorScheme.secondaryContainer
    val line = COUITheme.colorScheme.onSurfaceContainerHigh
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        drawRoundRect(
            color = tray,
            topLeft = Offset(w * 0.3f, h * 0.3f),
            size = Size(w * 0.4f, h * 0.45f),
            cornerRadius = CornerRadius(h * 0.06f),
        )
        drawLine(line, Offset(w * 0.38f, h * 0.45f), Offset(w * 0.62f, h * 0.45f), strokeWidth = h * 0.025f)
        drawLine(line, Offset(w * 0.38f, h * 0.55f), Offset(w * 0.55f, h * 0.55f), strokeWidth = h * 0.025f)
    }
}
