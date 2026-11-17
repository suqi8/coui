// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import io.github.suqi8.coui.kmp.basic.Chip
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TimePicker
import io.github.suqi8.coui.kmp.basic.TimeValue
import io.github.suqi8.coui.kmp.theme.COUITheme

@Composable
fun TimePickerDemo() {
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
            var time by remember { mutableStateOf(TimeValue(hour = 16, minute = 30)) }
            var twelveHour by remember { mutableStateOf(false) }
            var showUnits by remember { mutableStateOf(false) }
            var disabled by remember { mutableStateOf(false) }
            Card(modifier = Modifier.fillMaxWidth()) {
                TimePicker(
                    value = time,
                    onValueChange = { time = it },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !disabled,
                    is24Hour = !twelveHour,
                    hourUnit = if (showUnits) "h" else "",
                    minuteUnit = if (showUnits) "min" else "",
                )
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Chip(
                    selected = twelveHour,
                    onClick = { twelveHour = !twelveHour },
                    label = "12-hour",
                )
                Chip(
                    selected = showUnits,
                    onClick = { showUnits = !showUnits },
                    label = "Units",
                )
                Chip(
                    selected = disabled,
                    onClick = { disabled = !disabled },
                    label = "Disabled",
                )
            }
            Text(
                text = "Selected: ${time.hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}",
                color = COUITheme.colorScheme.labelSecondary,
                style = COUITheme.textStyles.bodyM,
            )
        }
    }
}
