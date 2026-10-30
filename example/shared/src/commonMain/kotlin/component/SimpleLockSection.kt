// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.SimpleLock
import io.github.suqi8.coui.kmp.basic.SimpleLockType
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.basic.rememberSimpleLockState
import kotlinx.coroutines.launch

fun LazyListScope.simpleLockSection() {
    item(key = "simpleLock") {
        SmallTitle(text = "SimpleLock")
        SimpleLockDemoCard(SimpleLockType.Four)
        SimpleLockDemoCard(SimpleLockType.Six)
    }
}

@Composable
private fun SimpleLockDemoCard(type: SimpleLockType) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
    ) {
        var count by remember { mutableIntStateOf(0) }
        val state = rememberSimpleLockState()
        val scope = rememberCoroutineScope()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            SimpleLock(filledCount = count, type = type, state = state)
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TextButton(text = "Add", onClick = { if (count < type.count) count++ }, modifier = Modifier.weight(1f))
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
