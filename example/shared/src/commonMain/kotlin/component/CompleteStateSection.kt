// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CompleteState
import io.github.suqi8.coui.kmp.basic.CompleteStateType
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.TextButton

fun LazyListScope.completeStateSection() {
    item(key = "completeState") {
        SmallTitle(text = "CompleteState")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            var type by remember { mutableStateOf(CompleteStateType.Success) }
            var replay by remember { mutableIntStateOf(0) }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                CompleteState(
                    title = when (type) {
                        CompleteStateType.Success -> "Payment successful"
                        CompleteStateType.Failed -> "Payment failed"
                        CompleteStateType.Waiting -> "Waiting for confirmation"
                    },
                    subtitle = "Order No. 2026103000001",
                    type = type,
                    replayKey = replay,
                )
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    CompleteStateType.entries.forEach { entry ->
                        TextButton(
                            text = entry.name,
                            onClick = {
                                if (type == entry) replay++ else type = entry
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}
