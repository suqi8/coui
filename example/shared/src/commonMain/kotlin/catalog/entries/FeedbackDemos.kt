// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package catalog.entries

import catalog.Demo
import catalog.DemoCategory
import demo.feedback.CompProgressIndicatorDemo
import demo.feedback.CompleteStateDemo
import demo.feedback.CustomSnackbarDemo
import demo.feedback.EmptyStateDemo
import demo.feedback.InstallLoadProgressDemo
import demo.feedback.ProgressIndicatorDemo
import demo.feedback.PullToRefreshDemo
import demo.feedback.SnackbarDemo

/**
 * The demos of [DemoCategory.Feedback], in list order.
 */
internal val FeedbackDemos: List<Demo> = listOf(
    SnackbarDemo,
    CustomSnackbarDemo,
    ProgressIndicatorDemo,
    CompProgressIndicatorDemo,
    InstallLoadProgressDemo,
    PullToRefreshDemo,
    EmptyStateDemo,
    CompleteStateDemo,
)
