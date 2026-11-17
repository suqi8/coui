// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.selection

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.Switch
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_checked
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_loading
import io.github.suqi8.coui.kmp.shared.generated.resources.common_off
import io.github.suqi8.coui.kmp.shared.generated.resources.common_on
import io.github.suqi8.coui.kmp.shared.generated.resources.common_value_format
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_switch_loading_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_switch_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_switch_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_switch_variant_states
import org.jetbrains.compose.resources.stringResource

/** The demo page of `Switch`, listed in its category and found by the search. */
internal val SwitchDemo = Demo(
    id = "switch",
    apiName = "Switch",
    title = Res.string.demo_switch_title,
    category = DemoCategory.Selection,
    page = DemoPage.Variants { switchPage() },
    searchTerms = Res.string.demo_switch_search_terms,
)

private fun LazyListScope.switchPage() {
    playground {
        var checked by rememberSaveable { mutableStateOf(true) }
        var enabled by rememberSaveable { mutableStateOf(true) }
        var loading by rememberSaveable { mutableStateOf(false) }

        PlaygroundPreview {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Switch(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    enabled = enabled,
                    isLoading = loading,
                )
            }
            ValueCaption(
                text = stringResource(
                    Res.string.common_value_format,
                    stringResource(if (checked) Res.string.common_on else Res.string.common_off),
                ),
            )
        }
        PlaygroundControls {
            item {
                SwitchPreference(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    title = stringResource(Res.string.common_checked),
                )
            }
            item {
                SwitchPreference(
                    checked = loading,
                    onCheckedChange = { loading = it },
                    title = stringResource(Res.string.common_loading),
                    summary = stringResource(Res.string.demo_switch_loading_summary),
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
    variant(key = "states", title = Res.string.demo_switch_variant_states) {
        SwitchStateRows()
    }
}

@Composable
private fun ColumnScope.SwitchStateRows() {
    var off by remember { mutableStateOf(false) }
    var on by remember { mutableStateOf(true) }
    var loadingChecked by remember { mutableStateOf(true) }

    LabeledControlsRow(label = stringResource(Res.string.common_enabled)) {
        Switch(checked = off, onCheckedChange = { off = it })
        Switch(checked = on, onCheckedChange = { on = it })
    }
    LabeledControlsRow(label = stringResource(Res.string.common_disabled)) {
        Switch(checked = false, onCheckedChange = {}, enabled = false)
        Switch(checked = true, onCheckedChange = {}, enabled = false)
    }
    LabeledControlsRow(label = stringResource(Res.string.common_loading)) {
        Switch(checked = loadingChecked, onCheckedChange = { loadingChecked = it }, isLoading = true)
    }
}
