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
import io.github.suqi8.coui.kmp.basic.DatePicker
import io.github.suqi8.coui.kmp.basic.DatePickerDefaults
import io.github.suqi8.coui.kmp.basic.DateValue
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.theme.COUITheme

@Composable
fun DatePickerDemo() {
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
            var date by remember { mutableStateOf(DateValue(year = 2026, month = 7, day = 26)) }
            var yearIgnorable by remember { mutableStateOf(false) }
            var disabled by remember { mutableStateOf(false) }
            Card(modifier = Modifier.fillMaxWidth()) {
                DatePicker(
                    value = date,
                    onValueChange = { date = it },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !disabled,
                    yearIgnorable = yearIgnorable,
                )
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Chip(
                    selected = yearIgnorable,
                    onClick = {
                        if (yearIgnorable && date.year == DatePickerDefaults.IgnoredYear) {
                            // 2028 is a leap year, so 29 February stays a valid date.
                            date = date.copy(year = 2028)
                        }
                        yearIgnorable = !yearIgnorable
                    },
                    label = "Optional year",
                )
                Chip(
                    selected = disabled,
                    onClick = { disabled = !disabled },
                    label = "Disabled",
                )
            }
            val year = if (date.year == DatePickerDefaults.IgnoredYear) "----" else date.year.toString()
            val month = date.month.toString().padStart(2, '0')
            val day = date.day.toString().padStart(2, '0')
            Text(
                text = "Selected: $year-$month-$day",
                color = COUITheme.colorScheme.labelSecondary,
                style = COUITheme.textStyles.bodyM,
            )
        }
    }
}
