// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package catalog.entries

import catalog.Demo
import catalog.DemoCategory
import demo.dialogs.BottomSheetDemo
import demo.dialogs.DialogDemo
import demo.dialogs.FullPageStatementDemo
import demo.dialogs.GuideDialogDemo
import demo.dialogs.InputDialogDemo
import demo.dialogs.ListDialogDemo
import demo.dialogs.ProgressDialogDemo
import demo.dialogs.SecurityDialogDemo
import demo.dialogs.StatementDialogDemo

/**
 * The demos of [DemoCategory.Dialogs], in list order.
 */
internal val DialogsDemos: List<Demo> = listOf(
    DialogDemo,
    ListDialogDemo,
    ProgressDialogDemo,
    InputDialogDemo,
    SecurityDialogDemo,
    GuideDialogDemo,
    StatementDialogDemo,
    FullPageStatementDemo,
    BottomSheetDemo,
)
