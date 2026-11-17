// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.ButtonDefaults
import io.github.suqi8.coui.kmp.basic.ButtonMaterial
import io.github.suqi8.coui.kmp.basic.LoadingButton
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_loading
import io.github.suqi8.coui.kmp.shared.generated.resources.common_long_text
import io.github.suqi8.coui.kmp.shared.generated.resources.common_material
import io.github.suqi8.coui.kmp.shared.generated.resources.common_style
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_type_primary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_type_secondary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_width
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_width_fill
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_width_wrap
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_loading_button_auto_finish
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_loading_button_auto_finish_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_loading_button_download
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_loading_button_label_long
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_loading_button_loading_text
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_loading_button_loading_text_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_loading_button_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_loading_button_sign_in
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_loading_button_signing_in
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_loading_button_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_loading_button_variant_forms
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

/** The demo page of `LoadingButton`, listed in its category and found by the search. */
internal val LoadingButtonDemo = Demo(
    id = "loading_button",
    apiName = "LoadingButton",
    title = Res.string.demo_loading_button_title,
    category = DemoCategory.Buttons,
    page = DemoPage.Variants { loadingButtonPage() },
    searchTerms = Res.string.demo_loading_button_search_terms,
)

/** How long a demo button stays loading after a click. */
private val LoadingMillis = 3000L

private fun LazyListScope.loadingButtonPage() {
    playground {
        var loading by rememberSaveable { mutableStateOf(false) }
        var autoFinish by rememberSaveable { mutableStateOf(true) }
        var showLoadingText by rememberSaveable { mutableStateOf(false) }
        var primary by rememberSaveable { mutableStateOf(true) }
        var fillWidth by rememberSaveable { mutableStateOf(false) }
        var material by rememberSaveable { mutableIntStateOf(ButtonMaterial.Auto.ordinal) }
        var enabled by rememberSaveable { mutableStateOf(true) }
        var longText by rememberSaveable { mutableStateOf(false) }

        LaunchedEffect(loading, autoFinish) {
            if (loading && autoFinish) {
                delay(LoadingMillis)
                loading = false
            }
        }
        PlaygroundPreview {
            LoadingButton(
                text = stringResource(if (longText) Res.string.demo_loading_button_label_long else Res.string.demo_loading_button_download),
                onClick = { loading = true },
                modifier = if (fillWidth) Modifier.fillMaxWidth() else Modifier,
                isLoading = loading,
                loadingText = if (showLoadingText) stringResource(Res.string.demo_loading_button_signing_in) else null,
                enabled = enabled,
                colors = if (primary) ButtonDefaults.textButtonColorsPrimary() else ButtonDefaults.textButtonColors(),
                material = ButtonMaterial.entries[material],
            )
        }
        PlaygroundControls {
            item {
                SwitchPreference(
                    title = stringResource(Res.string.common_loading),
                    summary = "isLoading",
                    checked = loading,
                    onCheckedChange = { loading = it },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.demo_loading_button_auto_finish),
                    summary = stringResource(Res.string.demo_loading_button_auto_finish_summary),
                    checked = autoFinish,
                    onCheckedChange = { autoFinish = it },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.demo_loading_button_loading_text),
                    summary = stringResource(Res.string.demo_loading_button_loading_text_summary),
                    checked = showLoadingText,
                    onCheckedChange = { showLoadingText = it },
                )
            }
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.common_style),
                    summary = "colors",
                    items = listOf(
                        stringResource(Res.string.demo_button_type_secondary),
                        stringResource(Res.string.demo_button_type_primary),
                    ),
                    selectedIndex = if (primary) 1 else 0,
                    onSelectedIndexChange = { primary = it == 1 },
                )
            }
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.demo_button_width),
                    items = listOf(
                        stringResource(Res.string.demo_button_width_wrap),
                        stringResource(Res.string.demo_button_width_fill),
                    ),
                    selectedIndex = if (fillWidth) 1 else 0,
                    onSelectedIndexChange = { fillWidth = it == 1 },
                )
            }
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.common_material),
                    summary = "material",
                    items = ButtonMaterial.entries.map { it.name },
                    selectedIndex = material,
                    onSelectedIndexChange = { material = it },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.common_enabled),
                    checked = enabled,
                    onCheckedChange = { enabled = it },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.common_long_text),
                    checked = longText,
                    onCheckedChange = { longText = it },
                )
            }
        }
    }
    // The two loading forms: animated dots in place of the label, and a loading label.
    variant(key = "forms", title = Res.string.demo_loading_button_variant_forms) {
        var dotsLoading by remember { mutableStateOf(false) }
        var textLoading by remember { mutableStateOf(false) }
        LaunchedEffect(dotsLoading) {
            if (dotsLoading) {
                delay(LoadingMillis)
                dotsLoading = false
            }
        }
        LaunchedEffect(textLoading) {
            if (textLoading) {
                delay(LoadingMillis)
                textLoading = false
            }
        }
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LoadingButton(
                text = stringResource(Res.string.demo_loading_button_download),
                onClick = { dotsLoading = true },
                modifier = Modifier.weight(1f),
                isLoading = dotsLoading,
                colors = ButtonDefaults.textButtonColorsPrimary(),
            )
            LoadingButton(
                text = stringResource(Res.string.demo_loading_button_sign_in),
                onClick = { textLoading = true },
                modifier = Modifier.weight(1f),
                isLoading = textLoading,
                loadingText = stringResource(Res.string.demo_loading_button_signing_in),
            )
        }
    }
}
