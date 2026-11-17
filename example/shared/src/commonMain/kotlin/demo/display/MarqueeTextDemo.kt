// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import io.github.suqi8.coui.kmp.basic.MarqueeText
import io.github.suqi8.coui.kmp.basic.MarqueeTextDefaults
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_long_text
import io.github.suqi8.coui.kmp.shared.generated.resources.common_style
import io.github.suqi8.coui.kmp.shared.generated.resources.common_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_marquee_text_body
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_marquee_text_caption
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_marquee_text_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_marquee_text_enabled_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_marquee_text_fading_edge
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_marquee_text_initial_delay
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_marquee_text_long
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_marquee_text_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_marquee_text_short
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_marquee_text_spacing
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_marquee_text_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_marquee_text_variant_states
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_marquee_text_velocity
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.stringResource

/** The demo page of `MarqueeText`, listed in its category and found by the search. */
internal val MarqueeTextDemo = Demo(
    id = "marquee_text",
    apiName = "MarqueeText",
    title = Res.string.demo_marquee_text_title,
    category = DemoCategory.Display,
    page = DemoPage.Variants { marqueeTextPage() },
    searchTerms = Res.string.demo_marquee_text_search_terms,
)

private fun LazyListScope.marqueeTextPage() {
    playground {
        var enabled by rememberSaveable { mutableStateOf(true) }
        var longText by rememberSaveable { mutableStateOf(true) }
        var styleIndex by rememberSaveable { mutableIntStateOf(0) }
        var velocity by rememberSaveable { mutableFloatStateOf(MarqueeTextDefaults.Velocity.value) }
        var initialDelay by rememberSaveable { mutableFloatStateOf(MarqueeTextDefaults.InitialDelayMillis.toFloat()) }
        var spacing by rememberSaveable { mutableFloatStateOf(MarqueeTextDefaults.Spacing.value) }
        var fadingEdge by rememberSaveable { mutableFloatStateOf(MarqueeTextDefaults.FadingEdgeLength.value) }

        PlaygroundPreview {
            // Restarts the scroll from its start position when a timing parameter changes.
            key(initialDelay.toInt()) {
                MarqueeText(
                    text = stringResource(if (longText) Res.string.demo_marquee_text_long else Res.string.demo_marquee_text_short),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = enabled,
                    style = when (styleIndex) {
                        0 -> COUITheme.textStyles.bodyS
                        1 -> COUITheme.textStyles.bodyXL
                        else -> COUITheme.textStyles.headlineXS
                    },
                    color = if (styleIndex == 0) COUITheme.colorScheme.labelSecondaryVariant else COUITheme.colorScheme.labelPrimary,
                    velocity = velocity.dp,
                    initialDelayMillis = initialDelay.toInt(),
                    spacing = spacing.dp,
                    fadingEdgeLength = fadingEdge.dp,
                )
            }
        }
        PlaygroundControls {
            item {
                SwitchPreference(
                    title = stringResource(Res.string.common_enabled),
                    summary = stringResource(Res.string.demo_marquee_text_enabled_summary),
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
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.common_style),
                    summary = "style",
                    items = listOf(
                        stringResource(Res.string.demo_marquee_text_caption),
                        stringResource(Res.string.demo_marquee_text_body),
                        stringResource(Res.string.common_title),
                    ),
                    selectedIndex = styleIndex,
                    onSelectedIndexChange = { styleIndex = it },
                )
            }
            item {
                SliderPreference(
                    value = velocity,
                    onValueChange = { velocity = it },
                    title = stringResource(Res.string.demo_marquee_text_velocity),
                    valueText = "${velocity.toInt()} dp/s",
                    valueRange = 10f..120f,
                    steps = 10,
                    showKeyPoints = false,
                )
            }
            item {
                SliderPreference(
                    value = initialDelay,
                    onValueChange = { initialDelay = it },
                    title = stringResource(Res.string.demo_marquee_text_initial_delay),
                    valueText = "${initialDelay.toInt()} ms",
                    valueRange = 0f..3000f,
                    steps = 5,
                    showKeyPoints = false,
                )
            }
            item {
                SliderPreference(
                    value = spacing,
                    onValueChange = { spacing = it },
                    title = stringResource(Res.string.demo_marquee_text_spacing),
                    valueText = "${spacing.toInt()} dp",
                    valueRange = 20f..200f,
                    steps = 8,
                    showKeyPoints = false,
                )
            }
            item {
                SliderPreference(
                    value = fadingEdge,
                    onValueChange = { fadingEdge = it },
                    title = stringResource(Res.string.demo_marquee_text_fading_edge),
                    valueText = "${fadingEdge.toInt()} dp",
                    valueRange = 0f..60f,
                    steps = 11,
                    showKeyPoints = false,
                )
            }
        }
    }
    variant(key = "states", title = Res.string.demo_marquee_text_variant_states) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MarqueeText(
                text = stringResource(Res.string.demo_marquee_text_long),
                style = COUITheme.textStyles.bodyS,
                color = COUITheme.colorScheme.labelSecondaryVariant,
                modifier = Modifier.fillMaxWidth(),
            )
            MarqueeText(
                text = stringResource(Res.string.demo_marquee_text_short),
                style = COUITheme.textStyles.bodyS,
                color = COUITheme.colorScheme.labelSecondaryVariant,
            )
            MarqueeText(
                text = stringResource(Res.string.demo_marquee_text_disabled),
                enabled = false,
                style = COUITheme.textStyles.bodyS,
                color = COUITheme.colorScheme.labelSecondaryVariant,
            )
        }
    }
}
