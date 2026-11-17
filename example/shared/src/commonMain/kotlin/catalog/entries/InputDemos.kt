// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package catalog.entries

import catalog.Demo
import catalog.DemoCategory
import demo.input.CodeTextFieldDemo
import demo.input.InputViewDemo
import demo.input.LockPatternViewDemo
import demo.input.LockScreenPasswordInputDemo
import demo.input.NumericKeyboardDemo
import demo.input.SearchBarDemo
import demo.input.SearchHistoryDemo
import demo.input.SimpleLockDemo
import demo.input.TabWithSearchLayoutDemo
import demo.input.TextFieldDemo
import demo.input.ToolbarSearchDemo

/**
 * The demos of [DemoCategory.Input], in list order.
 */
internal val InputDemos: List<Demo> = listOf(
    TextFieldDemo,
    InputViewDemo,
    CodeTextFieldDemo,
    SearchBarDemo,
    SearchHistoryDemo,
    ToolbarSearchDemo,
    TabWithSearchLayoutDemo,
    NumericKeyboardDemo,
    LockScreenPasswordInputDemo,
    SimpleLockDemo,
    LockPatternViewDemo,
)
