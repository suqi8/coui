// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.preference

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.preference.RadioButtonLocation
import io.github.suqi8.coui.kmp.preference.RadioButtonPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_count
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_end
import io.github.suqi8.coui.kmp.shared.generated.resources.common_position
import io.github.suqi8.coui.kmp.shared.generated.resources.common_selected_value
import io.github.suqi8.coui.kmp.shared.generated.resources.common_start
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_radio_button_preference_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_radio_button_preference_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_radio_button_preference_variant_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_radio_button_preference_variant_single
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_long_title
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_option
import org.jetbrains.compose.resources.stringResource

private val RadioCounts = listOf("2", "3", "4", "5")

/** The demo page of `RadioButtonPreference`, listed in its category and found by the search. */
internal val RadioButtonPreferenceDemo = Demo(
    id = "radio_button_preference",
    apiName = "RadioButtonPreference",
    title = Res.string.demo_radio_button_preference_title,
    category = DemoCategory.Preference,
    page = DemoPage.Variants {
        playground {
            val state = rememberPreferenceRowState(showSummary = false)
            var location by rememberSaveable { mutableIntStateOf(0) }
            var countIndex by rememberSaveable { mutableIntStateOf(1) }
            var selected by rememberSaveable { mutableIntStateOf(0) }
            val count = countIndex + 2
            val summary = state.summary()
            val longTitle = stringResource(Res.string.sample_long_title)
            val startAction: (@Composable () -> Unit)? = if (state.showIcon) {
                { PreferenceStartIcon() }
            } else {
                null
            }
            val titles = List(count) { index ->
                if (state.longTitle && index == 0) longTitle else stringResource(Res.string.sample_option, index + 1)
            }
            PreferencePreview {
                titles.forEachIndexed { index, title ->
                    item(key = index) {
                        RadioButtonPreference(
                            title = title,
                            selected = selected == index,
                            onClick = { selected = index },
                            summary = summary,
                            startAction = startAction,
                            endActions = { if (state.showEndText) PreferenceEndText(enabled = state.enabled) },
                            radioButtonLocation = if (location == 0) RadioButtonLocation.Start else RadioButtonLocation.End,
                            enabled = state.enabled,
                        )
                    }
                }
            }
            PlaygroundControls {
                choiceControl(
                    Res.string.common_position,
                    listOf(Res.string.common_start, Res.string.common_end),
                    location,
                ) { location = it }
                literalChoiceControl(Res.string.common_count, RadioCounts, countIndex) {
                    countIndex = it
                    if (selected >= it + 2) selected = 0
                }
                preferenceRowControls(state)
            }
        }
        variant(key = "single", title = Res.string.demo_radio_button_preference_variant_single) {
            SingleChoiceRows()
        }
        variant(key = "disabled", title = Res.string.demo_radio_button_preference_variant_disabled) {
            CardListGroup {
                item {
                    RadioButtonPreference(
                        title = stringResource(Res.string.common_disabled),
                        selected = true,
                        onClick = {},
                        enabled = false,
                    )
                }
                item {
                    RadioButtonPreference(
                        title = stringResource(Res.string.common_disabled),
                        selected = false,
                        onClick = {},
                        radioButtonLocation = RadioButtonLocation.End,
                        enabled = false,
                    )
                }
            }
        }
    },
    searchTerms = Res.string.demo_radio_button_preference_search_terms,
)

/**
 * Three options of which exactly one is selected; each summary reads out its state.
 */
@Composable
private fun SingleChoiceRows() {
    var selected by rememberSaveable { mutableIntStateOf(0) }
    CardListGroup {
        repeat(3) { index ->
            item(key = index) {
                RadioButtonPreference(
                    title = stringResource(Res.string.sample_option, index + 1),
                    selected = selected == index,
                    onClick = { selected = index },
                    summary = stringResource(Res.string.common_selected_value, (selected == index).toString()),
                )
            }
        }
    }
}
