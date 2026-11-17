// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import io.github.suqi8.coui.kmp.basic.SegmentButton
import io.github.suqi8.coui.kmp.basic.SegmentButtonDefaults
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_count
import io.github.suqi8.coui.kmp.shared.generated.resources.common_default
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_long_text
import io.github.suqi8.coui.kmp.shared.generated.resources.common_material
import io.github.suqi8.coui.kmp.shared.generated.resources.common_off
import io.github.suqi8.coui.kmp.shared.generated.resources.common_on
import io.github.suqi8.coui.kmp.shared.generated.resources.common_search
import io.github.suqi8.coui.kmp.shared.generated.resources.common_size
import io.github.suqi8.coui.kmp.shared.generated.resources.common_small
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_background
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_width
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_width_fill
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_button_width_wrap
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_segment_button_all
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_segment_button_allowed
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_segment_button_day
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_segment_button_drag_animation
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_segment_button_favorites
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_segment_button_grid
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_segment_button_haptic
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_segment_button_list
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_segment_button_material_backdrop
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_segment_button_month
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_segment_button_not_allowed
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_segment_button_recent
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_segment_button_responsive_width
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_segment_button_responsive_width_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_segment_button_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_segment_button_shared
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_segment_button_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_segment_button_variant_backdrop
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_segment_button_variant_forms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_segment_button_week
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_albums
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_long_title
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_option
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_photos
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.stringResource

/** The demo page of `SegmentButton`, listed in its category and found by the search. */
internal val SegmentButtonDemo = Demo(
    id = "segment_button",
    apiName = "SegmentButton",
    title = Res.string.demo_segment_button_title,
    category = DemoCategory.Buttons,
    page = DemoPage.Variants { segmentButtonPage() },
    searchTerms = Res.string.demo_segment_button_search_terms,
)

/** The segment count choices of the playground. */
private val SegmentCounts = listOf(2, 3, 4, 5)

private fun LazyListScope.segmentButtonPage() {
    playground {
        var countIndex by rememberSaveable { mutableIntStateOf(1) }
        var selected by rememberSaveable { mutableIntStateOf(0) }
        var longText by rememberSaveable { mutableStateOf(false) }
        var fillWidth by rememberSaveable { mutableStateOf(false) }
        var small by rememberSaveable { mutableStateOf(false) }
        var materialEffect by rememberSaveable { mutableStateOf(false) }
        var backgroundIndex by rememberSaveable { mutableIntStateOf(StageBackground.Plain.ordinal) }
        var dragAnimation by rememberSaveable { mutableStateOf(true) }
        var haptic by rememberSaveable { mutableStateOf(true) }
        var responsiveWidth by rememberSaveable { mutableStateOf(true) }
        var enabled by rememberSaveable { mutableStateOf(true) }

        val count = SegmentCounts[countIndex]
        val longLabel = stringResource(Res.string.sample_long_title)
        val items = List(count) { index ->
            if (longText && index == 0) longLabel else stringResource(Res.string.sample_option, index + 1)
        }
        PlaygroundPreview(contentPadding = PaddingValues(0.dp)) {
            ButtonStage(background = StageBackground.entries[backgroundIndex]) {
                SegmentButton(
                    items = items,
                    selectedIndex = selected.coerceAtMost(count - 1),
                    onSelectedIndexChange = { selected = it },
                    modifier = if (fillWidth) Modifier.fillMaxWidth() else Modifier,
                    enabled = enabled,
                    materialEffect = materialEffect,
                    dragAnimationEnabled = dragAnimation,
                    hapticFeedbackEnabled = haptic,
                    responsiveWidthEnabled = responsiveWidth,
                    height = if (small) SegmentButtonDefaults.HeightSmall else SegmentButtonDefaults.Height,
                    padding = if (small) SegmentButtonDefaults.PaddingSmall else SegmentButtonDefaults.Padding,
                )
            }
        }
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.common_count),
                    summary = "items",
                    items = SegmentCounts.map { it.toString() },
                    selectedIndex = countIndex,
                    onSelectedIndexChange = { countIndex = it },
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
                    title = stringResource(Res.string.common_size),
                    summary = "height / padding",
                    items = listOf(stringResource(Res.string.common_default), stringResource(Res.string.common_small)),
                    selectedIndex = if (small) 1 else 0,
                    onSelectedIndexChange = { small = it == 1 },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.common_material),
                    summary = "materialEffect",
                    checked = materialEffect,
                    onCheckedChange = { materialEffect = it },
                )
            }
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.demo_button_background),
                    items = StageBackground.entries.map { stringResource(it.label) },
                    selectedIndex = backgroundIndex,
                    onSelectedIndexChange = { backgroundIndex = it },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.demo_segment_button_drag_animation),
                    summary = "dragAnimationEnabled",
                    checked = dragAnimation,
                    onCheckedChange = { dragAnimation = it },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.demo_segment_button_haptic),
                    summary = "hapticFeedbackEnabled",
                    checked = haptic,
                    onCheckedChange = { haptic = it },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.demo_segment_button_responsive_width),
                    summary = stringResource(Res.string.demo_segment_button_responsive_width_summary),
                    checked = responsiveWidth,
                    onCheckedChange = { responsiveWidth = it },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.common_enabled),
                    checked = enabled,
                    onCheckedChange = { enabled = it },
                )
            }
        }
    }
    variant(key = "forms", title = Res.string.demo_segment_button_variant_forms) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Wraps its labels.
            var wrapIndex by remember { mutableIntStateOf(0) }
            SegmentButton(
                items = listOf(
                    stringResource(Res.string.demo_segment_button_day),
                    stringResource(Res.string.demo_segment_button_week),
                    stringResource(Res.string.demo_segment_button_month),
                ),
                selectedIndex = wrapIndex,
                onSelectedIndexChange = { wrapIndex = it },
            )
            // Fills the width.
            var fillIndex by remember { mutableIntStateOf(1) }
            SegmentButton(
                items = listOf(
                    stringResource(Res.string.demo_segment_button_all),
                    stringResource(Res.string.demo_segment_button_allowed),
                    stringResource(Res.string.demo_segment_button_not_allowed),
                ),
                selectedIndex = fillIndex,
                onSelectedIndexChange = { fillIndex = it },
                modifier = Modifier.fillMaxWidth(),
            )
            // The small size.
            var smallIndex by remember { mutableIntStateOf(0) }
            SegmentButton(
                items = listOf(
                    stringResource(Res.string.demo_segment_button_list),
                    stringResource(Res.string.demo_segment_button_grid),
                ),
                selectedIndex = smallIndex,
                onSelectedIndexChange = { smallIndex = it },
                height = SegmentButtonDefaults.HeightSmall,
                padding = SegmentButtonDefaults.PaddingSmall,
            )
            // The COUI 17 material on the card surface.
            var materialIndex by remember { mutableIntStateOf(0) }
            SegmentButton(
                items = listOf(
                    stringResource(Res.string.sample_photos),
                    stringResource(Res.string.sample_albums),
                    stringResource(Res.string.common_search),
                ),
                selectedIndex = materialIndex,
                onSelectedIndexChange = { materialIndex = it },
                modifier = Modifier.fillMaxWidth(),
                materialEffect = true,
            )
            // Disabled: drawn unchanged, touches dropped.
            DisabledSegmentButton()
        }
    }
    // A material segment button over an image, blurring it through a backdrop recorded from it.
    variant(key = "backdrop", title = Res.string.demo_segment_button_variant_backdrop) {
        ButtonStage(
            background = StageBackground.Photo,
            modifier = Modifier.padding(16.dp),
            cornerRadius = 16.dp,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(Res.string.demo_segment_button_material_backdrop),
                    color = COUITheme.colorScheme.labelPrimary,
                    style = COUITheme.textStyles.bodyS,
                )
                var index by remember { mutableIntStateOf(0) }
                SegmentButton(
                    items = listOf(
                        stringResource(Res.string.demo_segment_button_recent),
                        stringResource(Res.string.demo_segment_button_favorites),
                        stringResource(Res.string.demo_segment_button_shared),
                    ),
                    selectedIndex = index,
                    onSelectedIndexChange = { index = it },
                    modifier = Modifier.fillMaxWidth(),
                    materialEffect = true,
                )
            }
        }
    }
}

/** A disabled on / off segment button with its label. */
@Composable
private fun DisabledSegmentButton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SegmentButton(
            items = listOf(stringResource(Res.string.common_on), stringResource(Res.string.common_off)),
            selectedIndex = 0,
            onSelectedIndexChange = {},
            enabled = false,
        )
        Text(
            text = stringResource(Res.string.common_disabled),
            color = COUITheme.colorScheme.labelSecondaryVariant,
            style = COUITheme.textStyles.bodyS,
        )
    }
}
