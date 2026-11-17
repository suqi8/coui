// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.preference

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.preference.SlideSelectPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_arrow_preference_clickable
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slide_select_preference_extra_large
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slide_select_preference_font_size
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slide_select_preference_large
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slide_select_preference_long_selection
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slide_select_preference_long_selection_text
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slide_select_preference_never
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slide_select_preference_no_selection
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slide_select_preference_screen_timeout
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slide_select_preference_show_selection
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slide_select_preference_standard
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slide_select_preference_tap_next
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slide_select_preference_timeout_minutes
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slide_select_preference_timeout_seconds
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slide_select_preference_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_slide_select_preference_variant_examples
import org.jetbrains.compose.resources.stringResource

internal val SlideSelectPreferenceDemo = Demo(
    id = "slide_select_preference",
    apiName = "SlideSelectPreference",
    title = Res.string.demo_slide_select_preference_title,
    category = DemoCategory.Preference,
    page = DemoPage.Variants {
        playground {
            val state = rememberPreferenceRowState(showSummary = false)
            var showSelection by rememberSaveable { mutableStateOf(true) }
            var longSelection by rememberSaveable { mutableStateOf(false) }
            var clickable by rememberSaveable { mutableStateOf(true) }
            var selected by rememberSaveable { mutableIntStateOf(0) }
            val values = fontSizeValues()
            val selectionText = when {
                !showSelection -> null
                longSelection -> stringResource(Res.string.demo_slide_select_preference_long_selection_text)
                else -> values[selected]
            }
            PreferencePreview {
                item {
                    SlideSelectPreference(
                        title = state.title(),
                        selectionText = selectionText,
                        onClick = if (clickable) {
                            { selected = (selected + 1) % values.size }
                        } else {
                            null
                        },
                        summary = state.summary(Res.string.demo_slide_select_preference_tap_next),
                        startAction = if (state.showIcon) {
                            { PreferenceStartIcon() }
                        } else {
                            null
                        },
                        endActions = if (state.showEndText) {
                            { PreferenceEndText(enabled = state.enabled) }
                        } else {
                            null
                        },
                        enabled = state.enabled,
                    )
                }
            }
            PlaygroundControls {
                switchControl(Res.string.demo_slide_select_preference_show_selection, showSelection) { showSelection = it }
                if (showSelection) {
                    switchControl(Res.string.demo_slide_select_preference_long_selection, longSelection) { longSelection = it }
                }
                switchControl(Res.string.demo_arrow_preference_clickable, clickable) { clickable = it }
                preferenceRowControls(state)
            }
        }
        variant(key = "examples", title = Res.string.demo_slide_select_preference_variant_examples) {
            SlideSelectExamples()
        }
    },
    keywords = listOf("设置", "slide", "滑动选择", "切换"),
    isNew = true,
)

@Composable
private fun fontSizeValues(): List<String> = listOf(
    stringResource(Res.string.demo_slide_select_preference_standard),
    stringResource(Res.string.demo_slide_select_preference_large),
    stringResource(Res.string.demo_slide_select_preference_extra_large),
)

/**
 * Rows that step through their values on each tap, one without a value and a disabled one.
 */
@Composable
private fun SlideSelectExamples() {
    var fontSize by rememberSaveable { mutableIntStateOf(0) }
    var timeout by rememberSaveable { mutableIntStateOf(1) }
    val fontSizes = fontSizeValues()
    val timeouts = listOf(
        stringResource(Res.string.demo_slide_select_preference_timeout_seconds, 30),
        stringResource(Res.string.demo_slide_select_preference_timeout_minutes, 1),
        stringResource(Res.string.demo_slide_select_preference_timeout_minutes, 5),
        stringResource(Res.string.demo_slide_select_preference_never),
    )
    CardListGroup {
        item {
            SlideSelectPreference(
                title = stringResource(Res.string.demo_slide_select_preference_font_size),
                selectionText = fontSizes[fontSize],
                summary = stringResource(Res.string.demo_slide_select_preference_tap_next),
                onClick = { fontSize = (fontSize + 1) % fontSizes.size },
            )
        }
        item {
            SlideSelectPreference(
                title = stringResource(Res.string.demo_slide_select_preference_screen_timeout),
                selectionText = timeouts[timeout],
                onClick = { timeout = (timeout + 1) % timeouts.size },
            )
        }
        item {
            SlideSelectPreference(
                title = stringResource(Res.string.demo_slide_select_preference_no_selection),
                onClick = {},
            )
        }
        item {
            SlideSelectPreference(
                title = stringResource(Res.string.common_disabled),
                selectionText = fontSizes[0],
                enabled = false,
            )
        }
    }
}
