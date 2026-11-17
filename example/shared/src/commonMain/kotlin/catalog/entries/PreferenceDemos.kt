// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package catalog.entries

import catalog.Demo
import catalog.DemoCategory
import demo.preference.ArrowPreferenceDemo
import demo.preference.ButtonPreferenceDemo
import demo.preference.CardPreferenceDemo
import demo.preference.CheckboxPreferenceDemo
import demo.preference.DropdownPreferenceDemo
import demo.preference.InputPreferenceDemo
import demo.preference.ListPreferenceDemo
import demo.preference.MarkPreferenceDemo
import demo.preference.PagerPreferenceDemo
import demo.preference.PreferenceCategoryDemo
import demo.preference.RadioButtonPreferenceDemo
import demo.preference.SlideSelectPreferenceDemo
import demo.preference.SliderPreferenceDemo
import demo.preference.StatusPreferenceDemo
import demo.preference.SwitchPreferenceDemo

/**
 * The demos of [DemoCategory.Preference], in list order.
 */
internal val PreferenceDemos: List<Demo> = listOf(
    ArrowPreferenceDemo,
    SwitchPreferenceDemo,
    CheckboxPreferenceDemo,
    RadioButtonPreferenceDemo,
    SliderPreferenceDemo,
    PreferenceCategoryDemo,
    ListPreferenceDemo,
    SlideSelectPreferenceDemo,
    MarkPreferenceDemo,
    ButtonPreferenceDemo,
    InputPreferenceDemo,
    CardPreferenceDemo,
    StatusPreferenceDemo,
    PagerPreferenceDemo,
    DropdownPreferenceDemo,
)
