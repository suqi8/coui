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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.SimpleLock
import io.github.suqi8.coui.kmp.basic.SimpleLockType
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.basic.rememberSimpleLockState
import kotlinx.coroutines.launch

@Composable
fun SimpleLockDemo() {
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
            var count by remember { mutableIntStateOf(0) }
            val state = rememberSimpleLockState()
            val scope = rememberCoroutineScope()
            Card(modifier = Modifier.fillMaxWidth()) {
                SimpleLock(
                    filledCount = count,
                    type = SimpleLockType.Six,
                    state = state,
                    modifier = Modifier.padding(top = 24.dp),
                )
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TextButton(text = "Add", onClick = { if (count < 6) count++ }, modifier = Modifier.weight(1f))
                    TextButton(text = "Delete", onClick = { if (count > 0) count-- }, modifier = Modifier.weight(1f))
                    TextButton(text = "Clear", onClick = { count = 0 }, modifier = Modifier.weight(1f))
                    TextButton(
                        text = "Fail",
                        onClick = {
                            scope.launch {
                                state.playFailure()
                                count = 0
                            }
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}
