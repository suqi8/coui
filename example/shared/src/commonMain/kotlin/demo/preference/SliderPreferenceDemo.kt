// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.preference

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.SliderDefaults
import io.github.suqi8.coui.kmp.preference.RangeSliderPreference
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_material
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show_title
import io.github.suqi8.coui.kmp.shared.generated.resources.common_steps
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_pref_component
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slider_preference_custom_key_points
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slider_preference_custom_range_points
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slider_preference_disabled_key_points
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slider_preference_normal
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slider_preference_range
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slider_preference_range_key_points
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slider_preference_reverse
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slider_preference_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slider_preference_show_key_points
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slider_preference_show_value
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slider_preference_steps
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slider_preference_steps_key_points
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slider_preference_title
import org.jetbrains.compose.resources.stringResource

private val SliderTypes = listOf("SliderPreference", "RangeSliderPreference")
private val SliderSteps = listOf(0, 4, 9)

/** The demo page of `SliderPreference`, listed in its category and found by the search. */
internal val SliderPreferenceDemo = Demo(
    id = "slider_preference",
    apiName = "SliderPreference",
    title = Res.string.demo_slider_preference_title,
    category = DemoCategory.Preference,
    page = DemoPage.Variants {
        playground {
            val state = rememberPreferenceRowState(showSummary = false)
            var type by rememberSaveable { mutableIntStateOf(0) }
            var showTitle by rememberSaveable { mutableStateOf(true) }
            var showValue by rememberSaveable { mutableStateOf(true) }
            var stepsIndex by rememberSaveable { mutableIntStateOf(0) }
            var showKeyPoints by rememberSaveable { mutableStateOf(true) }
            var material by rememberSaveable { mutableStateOf(true) }
            var reverse by rememberSaveable { mutableStateOf(false) }
            var value by remember { mutableFloatStateOf(0.4f) }
            var range by remember { mutableStateOf(0.2f..0.8f) }
            val steps = SliderSteps[stepsIndex]
            val title = if (showTitle) state.title() else null
            val summary = state.summary()
            val startAction: (@Composable () -> Unit)? = if (state.showIcon) {
                { PreferenceStartIcon() }
            } else {
                null
            }
            val hapticEffect = if (steps > 0) SliderDefaults.SliderHapticEffect.Step else SliderDefaults.DefaultHapticEffect
            PreferencePreview {
                item {
                    if (type == 0) {
                        SliderPreference(
                            value = value,
                            onValueChange = { value = it },
                            title = title,
                            summary = summary,
                            startAction = startAction,
                            valueText = if (showValue) "${(value * 100).toInt()}%" else null,
                            enabled = state.enabled,
                            steps = steps,
                            reverseDirection = reverse,
                            hapticEffect = hapticEffect,
                            showKeyPoints = showKeyPoints,
                            material = material,
                        )
                    } else {
                        RangeSliderPreference(
                            value = range,
                            onValueChange = { range = it },
                            title = title,
                            summary = summary,
                            startAction = startAction,
                            valueText = if (showValue) {
                                "${(range.start * 100).toInt()}% - ${(range.endInclusive * 100).toInt()}%"
                            } else {
                                null
                            },
                            enabled = state.enabled,
                            steps = steps,
                            hapticEffect = hapticEffect,
                            showKeyPoints = showKeyPoints,
                        )
                    }
                }
            }
            PlaygroundControls {
                literalChoiceControl(Res.string.demo_pref_component, SliderTypes, type) { type = it }
                literalChoiceControl(Res.string.common_steps, SliderSteps.map { it.toString() }, stepsIndex) { stepsIndex = it }
                switchControl(Res.string.demo_slider_preference_show_key_points, showKeyPoints) { showKeyPoints = it }
                if (type == 0) {
                    switchControl(Res.string.common_material, material) { material = it }
                    switchControl(Res.string.demo_slider_preference_reverse, reverse) { reverse = it }
                }
                switchControl(Res.string.common_show_title, showTitle) { showTitle = it }
                switchControl(Res.string.demo_slider_preference_show_value, showValue) { showValue = it }
                preferenceRowControls(state, endText = false)
            }
        }
        variant(key = "slider", title = "SliderPreference") {
            SliderRows()
        }
        variant(key = "range", title = "RangeSliderPreference") {
            RangeSliderRows()
        }
    },
    searchTerms = Res.string.demo_slider_preference_search_terms,
)

/**
 * Single-thumb slider rows: continuous, stepped, with key points and disabled.
 */
@Composable
private fun SliderRows() {
    var normal by remember { mutableFloatStateOf(0.3f) }
    var stepped by remember { mutableFloatStateOf(100f) }
    var keyPointed by remember { mutableFloatStateOf(5f) }
    var custom by remember { mutableFloatStateOf(25f) }
    val customKeyPoints = remember { listOf(0f, 25f, 50f, 75f, 100f) }
    CardListGroup {
        item {
            SliderPreference(
                value = normal,
                onValueChange = { normal = it },
                title = stringResource(Res.string.demo_slider_preference_normal),
                valueText = "${(normal * 100).toInt()}%",
            )
        }
        item {
            SliderPreference(
                value = stepped,
                onValueChange = { stepped = it },
                title = stringResource(Res.string.demo_slider_preference_steps),
                valueText = "${stepped.toInt()}/200",
                valueRange = 0f..200f,
                steps = 199,
                hapticEffect = SliderDefaults.SliderHapticEffect.Step,
                showKeyPoints = false,
            )
        }
        item {
            SliderPreference(
                value = keyPointed,
                onValueChange = { keyPointed = it },
                title = stringResource(Res.string.demo_slider_preference_steps_key_points),
                valueText = "${keyPointed.toInt()}/8",
                valueRange = 0f..8f,
                steps = 7,
                hapticEffect = SliderDefaults.SliderHapticEffect.Step,
                showKeyPoints = true,
            )
        }
        item {
            SliderPreference(
                value = custom,
                onValueChange = { custom = it },
                title = stringResource(Res.string.demo_slider_preference_custom_key_points),
                valueText = "${custom.toInt()}%",
                valueRange = 0f..100f,
                hapticEffect = SliderDefaults.SliderHapticEffect.Step,
                showKeyPoints = true,
                keyPoints = customKeyPoints,
            )
        }
        item {
            SliderPreference(
                value = 0.7f,
                onValueChange = {},
                title = stringResource(Res.string.common_disabled),
                valueText = "70%",
                enabled = false,
            )
        }
        item {
            SliderPreference(
                value = 4f,
                onValueChange = {},
                title = stringResource(Res.string.demo_slider_preference_disabled_key_points),
                valueText = "4/8",
                valueRange = 0f..8f,
                steps = 7,
                showKeyPoints = true,
                enabled = false,
            )
        }
    }
}

/**
 * Two-thumb range slider rows.
 */
@Composable
private fun RangeSliderRows() {
    var range by remember { mutableStateOf(0.2f..0.8f) }
    var steppedRange by remember { mutableStateOf(2f..8f) }
    var customRange by remember { mutableStateOf(20f..80f) }
    val customKeyPoints = remember { listOf(0f, 20f, 40f, 60f, 80f, 100f) }
    CardListGroup {
        item {
            RangeSliderPreference(
                value = range,
                onValueChange = { range = it },
                title = stringResource(Res.string.demo_slider_preference_range),
                valueText = "${(range.start * 100).toInt()}% - ${(range.endInclusive * 100).toInt()}%",
            )
        }
        item {
            RangeSliderPreference(
                value = steppedRange,
                onValueChange = { steppedRange = it },
                title = stringResource(Res.string.demo_slider_preference_range_key_points),
                valueText = "${steppedRange.start.toInt()} - ${steppedRange.endInclusive.toInt()}",
                valueRange = 0f..8f,
                steps = 7,
                hapticEffect = SliderDefaults.SliderHapticEffect.Step,
                showKeyPoints = true,
            )
        }
        item {
            RangeSliderPreference(
                value = customRange,
                onValueChange = { customRange = it },
                title = stringResource(Res.string.demo_slider_preference_custom_range_points),
                valueText = "${customRange.start.toInt()}% - ${customRange.endInclusive.toInt()}%",
                valueRange = 0f..100f,
                hapticEffect = SliderDefaults.SliderHapticEffect.Step,
                showKeyPoints = true,
                keyPoints = customKeyPoints,
            )
        }
        item {
            RangeSliderPreference(
                value = 0.3f..0.7f,
                onValueChange = {},
                title = stringResource(Res.string.common_disabled),
                valueText = "30% - 70%",
                enabled = false,
            )
        }
    }
}
