// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package catalog.entries

import catalog.Demo
import catalog.DemoCategory
import demo.pickers.CalendarPickerDemo
import demo.pickers.ColorPickerDemo
import demo.pickers.DateTimePickerDemo
import demo.pickers.LunarDatePickerDemo
import demo.pickers.NumberPickerDemo

/**
 * The demos of [DemoCategory.Pickers], in list order.
 */
internal val PickersDemos: List<Demo> = listOf(
    NumberPickerDemo,
    DateTimePickerDemo,
    LunarDatePickerDemo,
    CalendarPickerDemo,
    ColorPickerDemo,
)
