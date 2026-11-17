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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.Stepper

@Composable
fun StepperDemo() {
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
            var copies by remember { mutableIntStateOf(1) }
            var volume by remember { mutableIntStateOf(50) }

            Card {
                CardListGroup {
                    item {
                        BasicComponent(
                            title = "Copies",
                            summary = "1 to 10",
                            endActions = {
                                Stepper(
                                    value = copies,
                                    onValueChange = { copies = it },
                                    minValue = 1,
                                    maxValue = 10,
                                )
                            },
                        )
                    }
                    item {
                        BasicComponent(
                            title = "Volume",
                            summary = "Steps of 5",
                            endActions = {
                                Stepper(
                                    value = volume,
                                    onValueChange = { volume = it },
                                    minValue = 0,
                                    maxValue = 100,
                                    step = 5,
                                )
                            },
                        )
                    }
                    item {
                        BasicComponent(
                            title = "Disabled",
                            enabled = false,
                            endActions = {
                                Stepper(
                                    value = 3,
                                    onValueChange = {},
                                    minValue = 0,
                                    maxValue = 10,
                                    enabled = false,
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}
