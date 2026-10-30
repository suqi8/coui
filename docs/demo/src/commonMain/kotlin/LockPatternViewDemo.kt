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
import io.github.suqi8.coui.kmp.basic.LockPatternDisplayMode
import io.github.suqi8.coui.kmp.basic.LockPatternView
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.patternToString
import io.github.suqi8.coui.kmp.basic.rememberLockPatternState

@Composable
fun LockPatternViewDemo() {
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
            val state = rememberLockPatternState()
            var message by remember { mutableStateOf("Draw an L (1-4-7-8-9)") }
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = message)
                    LockPatternView(
                        state = state,
                        onPatternStart = { message = "Release when done" },
                        onPatternDetected = { pattern ->
                            val code = patternToString(pattern)
                            if (code == "14789") {
                                message = "Correct"
                                state.clearPattern()
                            } else {
                                message = "Wrong pattern ($code)"
                                state.setDisplayMode(LockPatternDisplayMode.Wrong)
                                state.clearPattern()
                            }
                        },
                    )
                }
            }
        }
    }
}
