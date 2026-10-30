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
import io.github.suqi8.coui.kmp.basic.CalendarPicker
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.DateValue
import io.github.suqi8.coui.kmp.basic.SmallTitle

fun LazyListScope.calendarPickerSection() {
    item(key = "calendarPicker") {
        SmallTitle(text = "CalendarPicker")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            var date by remember { mutableStateOf(DateValue(year = 2026, month = 10, day = 30)) }
            CalendarPicker(
                value = date,
                onValueChange = { date = it },
            )
        }
    }
    item(key = "calendarPickerRange") {
        SmallTitle(text = "CalendarPicker (Monday first, limited range)")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            var date by remember { mutableStateOf(DateValue(year = 2026, month = 10, day = 12)) }
            CalendarPicker(
                value = date,
                onValueChange = { date = it },
                minDate = DateValue(year = 2026, month = 9, day = 10),
                maxDate = DateValue(year = 2026, month = 12, day = 20),
                firstDayOfWeek = 2,
            )
        }
    }
}
