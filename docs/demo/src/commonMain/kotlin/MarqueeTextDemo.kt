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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.MarqueeText
import io.github.suqi8.coui.kmp.theme.COUITheme

@Composable
fun MarqueeTextDemo() {
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
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    MarqueeText(
                        text = "Battery optimisation is limiting background activity for several apps, tap to review them",
                        style = COUITheme.textStyles.footnote1,
                        color = COUITheme.colorScheme.onSurfaceVariantSummary,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    MarqueeText(
                        text = "Short text stays still",
                        style = COUITheme.textStyles.footnote1,
                        color = COUITheme.colorScheme.onSurfaceVariantSummary,
                    )
                    MarqueeText(
                        text = "With the marquee disabled the same long text simply wraps onto the following lines",
                        enabled = false,
                        style = COUITheme.textStyles.footnote1,
                        color = COUITheme.colorScheme.onSurfaceVariantSummary,
                    )
                }
            }
        }
    }
}
