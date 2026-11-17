// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.RadioButton
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_count
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_horizontal
import io.github.suqi8.coui.kmp.shared.generated.resources.common_orientation
import io.github.suqi8.coui.kmp.shared.generated.resources.common_selected_value
import io.github.suqi8.coui.kmp.shared.generated.resources.common_vertical
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_radio_button_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_radio_button_show_labels
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_radio_button_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_radio_button_variant_states
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_option
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.stringResource

/** The demo page of `RadioButton`, listed in its category and found by the search. */
internal val RadioButtonDemo = Demo(
    id = "radio_button",
    apiName = "RadioButton",
    title = Res.string.demo_radio_button_title,
    category = DemoCategory.Selection,
    page = DemoPage.Variants { radioButtonPage() },
    searchTerms = Res.string.demo_radio_button_search_terms,
)

/** The option counts offered by the playground. */
private val OptionCounts = listOf(2, 3, 4, 5)

private fun LazyListScope.radioButtonPage() {
    playground {
        var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
        var countIndex by rememberSaveable { mutableIntStateOf(1) }
        var vertical by rememberSaveable { mutableStateOf(false) }
        var showLabels by rememberSaveable { mutableStateOf(true) }
        var enabled by rememberSaveable { mutableStateOf(true) }
        val count = OptionCounts[countIndex]

        PlaygroundPreview {
            RadioGroupPreview(
                count = count,
                selectedIndex = selectedIndex,
                onSelect = { selectedIndex = it },
                vertical = vertical,
                showLabels = showLabels,
                enabled = enabled,
            )
            ValueCaption(
                text = stringResource(
                    Res.string.common_selected_value,
                    stringResource(Res.string.sample_option, selectedIndex + 1),
                ),
            )
        }
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    items = OptionCounts.map { it.toString() },
                    selectedIndex = countIndex,
                    title = stringResource(Res.string.common_count),
                    onSelectedIndexChange = {
                        countIndex = it
                        if (selectedIndex >= OptionCounts[it]) selectedIndex = 0
                    },
                )
            }
            item {
                OverlayDropdownPreference(
                    items = listOf(
                        stringResource(Res.string.common_horizontal),
                        stringResource(Res.string.common_vertical),
                    ),
                    selectedIndex = if (vertical) 1 else 0,
                    title = stringResource(Res.string.common_orientation),
                    onSelectedIndexChange = { vertical = it == 1 },
                )
            }
            item {
                SwitchPreference(
                    checked = showLabels,
                    onCheckedChange = { showLabels = it },
                    title = stringResource(Res.string.demo_radio_button_show_labels),
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
    variant(key = "states", title = Res.string.demo_radio_button_variant_states) {
        RadioButtonStateRows()
    }
}

/**
 * A group of radio buttons, each optionally followed by its label.
 */
@Composable
private fun RadioGroupPreview(
    count: Int,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    vertical: Boolean,
    showLabels: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val options: @Composable (Int) -> Unit = { index ->
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = index == selectedIndex,
                onClick = { onSelect(index) },
                enabled = enabled,
            )
            if (showLabels) {
                Text(
                    text = stringResource(Res.string.sample_option, index + 1),
                    style = COUITheme.textStyles.bodyL,
                    color = if (enabled) COUITheme.colorScheme.labelPrimary else COUITheme.colorScheme.labelTertiary,
                )
            }
        }
    }
    if (vertical) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            repeat(count) { options(it) }
        }
    } else {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            repeat(count) { options(it) }
        }
    }
}

@Composable
private fun ColumnScope.RadioButtonStateRows() {
    var selectedIndex by remember { mutableIntStateOf(0) }

    // Standalone radio buttons show their own circular press highlight.
    LabeledControlsRow(label = stringResource(Res.string.common_enabled)) {
        RadioButton(
            selected = selectedIndex == 0,
            onClick = { selectedIndex = 0 },
        )
        RadioButton(
            selected = selectedIndex == 1,
            onClick = { selectedIndex = 1 },
        )
    }
    LabeledControlsRow(label = stringResource(Res.string.common_disabled)) {
        RadioButton(selected = false, onClick = null, enabled = false)
        RadioButton(selected = true, onClick = null, enabled = false)
    }
}
