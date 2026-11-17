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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.Slider
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TwoTextButton
import io.github.suqi8.coui.kmp.basic.TwoTextButtonDefaults
import io.github.suqi8.coui.kmp.theme.COUITheme

@Composable
fun TwoTextButtonDemo() {
    var progress by remember { mutableFloatStateOf(0f) }
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
                    Text(
                        text = "Page offset ${(progress * 100).toInt()}%",
                        color = COUITheme.colorScheme.labelSecondaryVariant,
                    )
                    Slider(
                        value = progress,
                        onValueChange = { progress = it },
                    )
                    TwoTextButton(
                        startText = "Next",
                        nextText = "Get started",
                        startTextAlpha = { TwoTextButtonDefaults.outgoingTextAlpha(progress) },
                        nextTextAlpha = { TwoTextButtonDefaults.incomingTextAlpha(progress) },
                        onClick = { progress = if (progress < 0.5f) 1f else 0f },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
