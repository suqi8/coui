// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.DatePicker
import io.github.suqi8.coui.kmp.basic.DateTimePicker
import io.github.suqi8.coui.kmp.basic.DateTimeValue
import io.github.suqi8.coui.kmp.basic.DateValue
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.TimePicker
import io.github.suqi8.coui.kmp.basic.TimeValue

fun LazyListScope.dateTimePickerSection() {
    item(key = "datePicker") {
        SmallTitle(text = "DatePicker")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            var date by remember { mutableStateOf(DateValue(year = 2026, month = 7, day = 26)) }
            DatePicker(
                value = date,
                onValueChange = { date = it },
            )
        }
    }
    item(key = "timePicker24h") {
        SmallTitle(text = "TimePicker (24-hour)")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            var time by remember { mutableStateOf(TimeValue(hour = 16, minute = 30)) }
            TimePicker(
                value = time,
                onValueChange = { time = it },
                hourUnit = "h",
                minuteUnit = "min",
            )
        }
    }
    item(key = "timePicker12h") {
        SmallTitle(text = "TimePicker (12-hour)")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            var time by remember { mutableStateOf(TimeValue(hour = 16, minute = 30)) }
            TimePicker(
                value = time,
                onValueChange = { time = it },
                is24Hour = false,
            )
        }
    }
    item(key = "dateTimePicker") {
        SmallTitle(text = "DateTimePicker (12-hour)")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            var value by remember {
                mutableStateOf(DateTimeValue(DateValue(year = 2026, month = 10, day = 23), TimeValue(hour = 11, minute = 30)))
            }
            DateTimePicker(
                value = value,
                onValueChange = { value = it },
                is24Hour = false,
            )
        }
    }
}
