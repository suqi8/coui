// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.RangeSlider
import io.github.suqi8.coui.kmp.basic.Slider
import io.github.suqi8.coui.kmp.basic.SliderDefaults
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.VerticalSlider
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_custom
import io.github.suqi8.coui.kmp.shared.generated.resources.common_default
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_material
import io.github.suqi8.coui.kmp.shared.generated.resources.common_steps
import io.github.suqi8.coui.kmp.shared.generated.resources.common_value_format
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slider_component
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slider_continuous
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slider_custom_key_points
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slider_disabled_key_points
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slider_haptic
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slider_key_points
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slider_material_off
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slider_material_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slider_reverse
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slider_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slider_sections
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slider_show_key_points
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slider_start_from_middle
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slider_steps_key_points
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slider_title
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

/** The demo page of `Slider`, listed in its category and found by the search. */
internal val SliderDemo = Demo(
    id = "slider",
    apiName = "Slider",
    title = Res.string.demo_slider_title,
    category = DemoCategory.Selection,
    page = DemoPage.Variants { sliderPage() },
    keywords = listOf("RangeSlider", "VerticalSlider"),
    searchTerms = Res.string.demo_slider_search_terms,
)

/** The components of the playground, by API name. */
private val SliderTypes = listOf("Slider", "RangeSlider", "VerticalSlider")

private val SliderModifier = Modifier
    .fillMaxWidth()
    .padding(horizontal = 16.dp, vertical = 8.dp)

private fun percent(value: Float): String = "${(value * 100).roundToInt()}%"

private fun LazyListScope.sliderPage() {
    playground {
        var typeIndex by rememberSaveable { mutableIntStateOf(0) }
        var value by rememberSaveable { mutableFloatStateOf(0.4f) }
        var rangeStart by rememberSaveable { mutableFloatStateOf(0.2f) }
        var rangeEnd by rememberSaveable { mutableFloatStateOf(0.8f) }
        var steps by rememberSaveable { mutableIntStateOf(0) }
        var showKeyPoints by rememberSaveable { mutableStateOf(true) }
        var material by rememberSaveable { mutableStateOf(true) }
        var startFromMiddle by rememberSaveable { mutableStateOf(false) }
        var reverse by rememberSaveable { mutableStateOf(false) }
        var hapticIndex by rememberSaveable { mutableIntStateOf(SliderDefaults.SliderHapticEffect.Step.ordinal) }
        var enabled by rememberSaveable { mutableStateOf(true) }
        val haptic = SliderDefaults.SliderHapticEffect.entries[hapticIndex]

        PlaygroundPreview {
            when (typeIndex) {
                0 -> {
                    Slider(
                        value = value,
                        onValueChange = { value = it },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = enabled,
                        steps = steps,
                        reverseDirection = reverse,
                        startFromMiddle = startFromMiddle && steps == 0,
                        hapticEffect = haptic,
                        showKeyPoints = showKeyPoints,
                        material = material,
                    )
                    ValueCaption(text = stringResource(Res.string.common_value_format, percent(value)))
                }

                1 -> {
                    RangeSlider(
                        value = rangeStart..rangeEnd,
                        onValueChange = {
                            rangeStart = it.start
                            rangeEnd = it.endInclusive
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = enabled,
                        steps = steps,
                        hapticEffect = haptic,
                        showKeyPoints = showKeyPoints,
                    )
                    ValueCaption(
                        text = stringResource(Res.string.common_value_format, "${percent(rangeStart)} - ${percent(rangeEnd)}"),
                    )
                }

                else -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        VerticalSlider(
                            value = value,
                            onValueChange = { value = it },
                            modifier = Modifier.height(200.dp),
                            enabled = enabled,
                            steps = steps,
                            reverseDirection = reverse,
                            text = percent(value),
                            hapticEffect = haptic,
                            showKeyPoints = showKeyPoints,
                        )
                    }
                }
            }
        }
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    items = SliderTypes,
                    selectedIndex = typeIndex,
                    title = stringResource(Res.string.demo_slider_component),
                    onSelectedIndexChange = { typeIndex = it },
                )
            }
            item {
                SliderPreference(
                    value = steps.toFloat(),
                    onValueChange = { steps = it.roundToInt() },
                    title = stringResource(Res.string.common_steps),
                    valueText = steps.toString(),
                    valueRange = 0f..10f,
                    steps = 9,
                )
            }
            item {
                SwitchPreference(
                    checked = showKeyPoints,
                    onCheckedChange = { showKeyPoints = it },
                    title = stringResource(Res.string.demo_slider_show_key_points),
                )
            }
            item {
                SwitchPreference(
                    checked = material,
                    onCheckedChange = { material = it },
                    title = stringResource(Res.string.common_material),
                    summary = stringResource(Res.string.demo_slider_material_summary),
                    enabled = typeIndex == 0,
                )
            }
            item {
                SwitchPreference(
                    checked = startFromMiddle,
                    onCheckedChange = { startFromMiddle = it },
                    title = stringResource(Res.string.demo_slider_start_from_middle),
                    enabled = typeIndex == 0 && steps == 0,
                )
            }
            item {
                SwitchPreference(
                    checked = reverse,
                    onCheckedChange = { reverse = it },
                    title = stringResource(Res.string.demo_slider_reverse),
                    enabled = typeIndex != 1,
                )
            }
            item {
                OverlayDropdownPreference(
                    items = SliderDefaults.SliderHapticEffect.entries.map { it.name },
                    selectedIndex = hapticIndex,
                    title = stringResource(Res.string.demo_slider_haptic),
                    onSelectedIndexChange = { hapticIndex = it },
                )
            }
            item {
                SwitchPreference(
                    checked = enabled,
                    onCheckedChange = { enabled = it },
                    title = stringResource(Res.string.common_enabled),
                )
            }
        }
    }
    variant(key = "slider", title = "Slider") {
        SliderVariants()
    }
    variant(key = "rangeSlider", title = "RangeSlider") {
        RangeSliderVariants()
    }
    variant(key = "verticalSlider", title = "VerticalSlider") {
        VerticalSliderVariants()
    }
}

@Composable
private fun ColumnScope.SliderVariants() {
    var continuous by remember { mutableFloatStateOf(0.3f) }
    LabeledBlock(label = "${stringResource(Res.string.demo_slider_continuous)}  ${percent(continuous)}") {
        Slider(value = continuous, onValueChange = { continuous = it }, modifier = SliderModifier)
    }
    // COUISectionSeekBar: section marks, anchored drag and section snapping.
    var sections by remember { mutableFloatStateOf(2f) }
    LabeledBlock(label = "${stringResource(Res.string.demo_slider_sections)}  ${sections.roundToInt()}/5") {
        Slider(
            value = sections,
            onValueChange = { sections = it },
            modifier = SliderModifier,
            valueRange = 0f..5f,
            steps = 4,
        )
    }
    var customPoints by remember { mutableFloatStateOf(25f) }
    LabeledBlock(label = "${stringResource(Res.string.demo_slider_custom_key_points)}  ${customPoints.roundToInt()}%") {
        Slider(
            value = customPoints,
            onValueChange = { customPoints = it },
            modifier = SliderModifier,
            valueRange = 0f..100f,
            showKeyPoints = true,
            keyPoints = listOf(0f, 25f, 50f, 75f, 100f),
        )
    }
    var middle by remember { mutableFloatStateOf(0f) }
    LabeledBlock(label = "${stringResource(Res.string.demo_slider_start_from_middle)}  ${middle.roundToInt()}") {
        Slider(
            value = middle,
            onValueChange = { middle = it },
            modifier = SliderModifier,
            valueRange = -50f..50f,
            startFromMiddle = true,
        )
    }
    var noMaterial by remember { mutableFloatStateOf(0.5f) }
    LabeledBlock(label = stringResource(Res.string.demo_slider_material_off)) {
        Slider(
            value = noMaterial,
            onValueChange = { noMaterial = it },
            modifier = SliderModifier,
            material = false,
        )
    }
    LabeledBlock(label = stringResource(Res.string.common_disabled)) {
        Slider(value = 0.7f, onValueChange = {}, modifier = SliderModifier, enabled = false)
    }
    LabeledBlock(label = stringResource(Res.string.demo_slider_disabled_key_points)) {
        Slider(
            value = 4f,
            onValueChange = {},
            modifier = SliderModifier,
            enabled = false,
            valueRange = 0f..8f,
            steps = 7,
        )
    }
}

@Composable
private fun ColumnScope.RangeSliderVariants() {
    var continuous by remember { mutableStateOf(0.2f..0.8f) }
    LabeledBlock(
        label = "${stringResource(Res.string.demo_slider_continuous)}  ${percent(continuous.start)} - ${percent(continuous.endInclusive)}",
    ) {
        RangeSlider(value = continuous, onValueChange = { continuous = it }, modifier = SliderModifier)
    }
    var stepped by remember { mutableStateOf(2f..6f) }
    LabeledBlock(
        label = "${stringResource(Res.string.demo_slider_steps_key_points)}  " +
            "${stepped.start.roundToInt()} - ${stepped.endInclusive.roundToInt()}",
    ) {
        RangeSlider(
            value = stepped,
            onValueChange = { stepped = it },
            modifier = SliderModifier,
            valueRange = 0f..8f,
            steps = 7,
            hapticEffect = SliderDefaults.SliderHapticEffect.Step,
            showKeyPoints = true,
        )
    }
    var customPoints by remember { mutableStateOf(20f..80f) }
    LabeledBlock(
        label = "${stringResource(Res.string.demo_slider_custom_key_points)}  " +
            "${customPoints.start.roundToInt()}% - ${customPoints.endInclusive.roundToInt()}%",
    ) {
        RangeSlider(
            value = customPoints,
            onValueChange = { customPoints = it },
            modifier = SliderModifier,
            valueRange = 0f..100f,
            hapticEffect = SliderDefaults.SliderHapticEffect.Step,
            showKeyPoints = true,
            keyPoints = listOf(0f, 20f, 40f, 60f, 80f, 100f),
        )
    }
    LabeledBlock(label = stringResource(Res.string.common_disabled)) {
        RangeSlider(value = 0.3f..0.7f, onValueChange = {}, modifier = SliderModifier, enabled = false)
    }
}

@Composable
private fun VerticalSliderVariants() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        var normal by remember { mutableFloatStateOf(0.3f) }
        VerticalSliderColumn(
            label = stringResource(Res.string.common_default),
            valueText = percent(normal),
            modifier = Modifier.weight(1f),
        ) {
            VerticalSlider(
                value = normal,
                onValueChange = { normal = it },
                modifier = Modifier.height(160.dp),
                text = "${(normal * 100).roundToInt()}",
            )
        }
        var stepped by remember { mutableFloatStateOf(5f) }
        VerticalSliderColumn(
            label = stringResource(Res.string.common_steps),
            valueText = "${stepped.roundToInt()}/6",
            modifier = Modifier.weight(1f),
        ) {
            VerticalSlider(
                value = stepped,
                onValueChange = { stepped = it },
                modifier = Modifier.height(160.dp),
                valueRange = 0f..6f,
                steps = 5,
            )
        }
        var points by remember { mutableFloatStateOf(5f) }
        VerticalSliderColumn(
            label = stringResource(Res.string.demo_slider_key_points),
            valueText = "${points.roundToInt()}/6",
            modifier = Modifier.weight(1f),
        ) {
            VerticalSlider(
                value = points,
                onValueChange = { points = it },
                modifier = Modifier.height(160.dp),
                valueRange = 0f..6f,
                steps = 5,
                showKeyPoints = true,
            )
        }
        var custom by remember { mutableFloatStateOf(50f) }
        VerticalSliderColumn(
            label = stringResource(Res.string.common_custom),
            valueText = "${custom.roundToInt()}%",
            modifier = Modifier.weight(1f),
        ) {
            VerticalSlider(
                value = custom,
                onValueChange = { custom = it },
                modifier = Modifier.height(160.dp),
                valueRange = 0f..100f,
                showKeyPoints = true,
                keyPoints = listOf(0f, 25f, 50f, 75f, 100f),
            )
        }
        VerticalSliderColumn(
            label = stringResource(Res.string.common_disabled),
            valueText = percent(0.7f),
            modifier = Modifier.weight(1f),
        ) {
            VerticalSlider(
                value = 0.7f,
                onValueChange = {},
                modifier = Modifier.height(160.dp),
                enabled = false,
            )
        }
    }
}

@Composable
private fun VerticalSliderColumn(
    label: String,
    valueText: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        content()
        Text(
            text = "$label\n$valueText",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            style = COUITheme.textStyles.bodyXS,
            color = COUITheme.colorScheme.labelSecondary,
            textAlign = TextAlign.Center,
        )
    }
}
