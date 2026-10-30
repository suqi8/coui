// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.DateFieldOrder
import io.github.suqi8.coui.kmp.basic.DateValue
import io.github.suqi8.coui.kmp.basic.LunarCalendar
import io.github.suqi8.coui.kmp.basic.LunarDatePicker
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.theme.COUITheme

fun LazyListScope.lunarDatePickerSection() {
    item(key = "lunarDatePicker") {
        SmallTitle(text = "LunarDatePicker")
        Card(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp),
        ) {
            // 2025-07-25 is the first day of the leap sixth lunar month.
            var date by remember { mutableStateOf(DateValue(year = 2025, month = 7, day = 25)) }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LunarDatePicker(
                    value = date,
                    onValueChange = { date = it },
                    fieldOrder = DateFieldOrder.YearMonthDay,
                )
                Text(
                    text = "${LunarCalendar.format(date)}  (${date.year}-${date.month}-${date.day})",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    color = COUITheme.colorScheme.onSurfaceSecondary,
                    style = COUITheme.textStyles.footnote1,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
    item(key = "lunarDatePickerDisabled") {
        SmallTitle(text = "LunarDatePicker (disabled)")
        Card(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp),
        ) {
            LunarDatePicker(
                value = DateValue(year = 2026, month = 2, day = 17),
                onValueChange = {},
                enabled = false,
                fieldOrder = DateFieldOrder.YearMonthDay,
            )
        }
    }
}
