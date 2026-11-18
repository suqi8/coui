// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package catalog.entries

import catalog.Demo
import catalog.DemoCategory
import demo.selection.CameraLensBarDemo
import demo.selection.CameraZoomBarDemo
import demo.selection.CheckboxDemo
import demo.selection.ChipDemo
import demo.selection.ChipGroupDemo
import demo.selection.IntentSliderDemo
import demo.selection.RadioButtonDemo
import demo.selection.SliderDemo
import demo.selection.SwitchDemo

/**
 * The demos of [DemoCategory.Selection], in list order.
 */
internal val SelectionDemos: List<Demo> = listOf(
    CheckboxDemo,
    RadioButtonDemo,
    SwitchDemo,
    SliderDemo,
    IntentSliderDemo,
    CameraLensBarDemo,
    CameraZoomBarDemo,
    ChipDemo,
    ChipGroupDemo,
)
