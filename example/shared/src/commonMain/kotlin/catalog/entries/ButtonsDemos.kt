// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package catalog.entries

import catalog.Demo
import catalog.DemoCategory
import demo.buttons.ButtonDemo
import demo.buttons.CameraShutterButtonDemo
import demo.buttons.FloatingActionButtonDemo
import demo.buttons.LoadingButtonDemo
import demo.buttons.SegmentButtonDemo
import demo.buttons.StepperDemo
import demo.buttons.TwoTextButtonDemo

/**
 * The demos of [DemoCategory.Buttons], in list order.
 */
internal val ButtonsDemos: List<Demo> = listOf(
    ButtonDemo,
    FloatingActionButtonDemo,
    LoadingButtonDemo,
    StepperDemo,
    TwoTextButtonDemo,
    SegmentButtonDemo,
    CameraShutterButtonDemo,
)
