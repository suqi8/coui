// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.BasicComponentDefaults
import io.github.suqi8.coui.kmp.basic.SidePaneLayout
import io.github.suqi8.coui.kmp.basic.SidePaneLayoutDefaults
import io.github.suqi8.coui.kmp.basic.SidePaneState
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.rememberSidePaneState
import io.github.suqi8.coui.kmp.preference.ArrowPreference
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_layout_simulated_width
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_layout_simulated_width_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_pane_layout_always_show_mask
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_pane_layout_always_show_mask_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_pane_layout_compact
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_pane_layout_cover_style
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_pane_layout_cover_style_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_pane_layout_folder_all
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_pane_layout_folder_deleted
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_pane_layout_folder_favourites
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_pane_layout_folder_recent
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_pane_layout_folder_shared
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_pane_layout_pane_closed
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_pane_layout_pane_open
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_pane_layout_pane_width
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_pane_layout_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_pane_layout_show_toggle_icon
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_pane_layout_split
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_pane_layout_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_pane_layout_toggle
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_pane_layout_touch_content
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_pane_layout_touch_content_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_pane_layout_variant_default
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

/** The demo page of `SidePaneLayout`, listed in its category and found by the search. */
internal val SidePaneLayoutDemo = Demo(
    id = "side_pane_layout",
    apiName = "SidePaneLayout",
    title = Res.string.demo_side_pane_layout_title,
    category = DemoCategory.Layout,
    page = DemoPage.Variants {
        sidePaneLayoutPlayground()
        variant(key = "default", title = Res.string.demo_side_pane_layout_variant_default) {
            val windowHeight = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.height.toDp() }
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PreviewHeight),
            ) {
                // Split on wide layouts (>= 600dp in a window >= 480dp tall); narrower, the pane
                // starts closed and covers the content under the mask.
                val split = SidePaneLayoutDefaults.isSplitMode(maxWidth, windowHeight)
                SidePaneSample(
                    state = rememberSidePaneState(initialOpen = split),
                    coverStyle = !split,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    },
    searchTerms = Res.string.demo_side_pane_layout_search_terms,
)

private val PreviewHeight = 420.dp

private val PaneWidths = listOf(180.dp, SidePaneLayoutDefaults.PaneWidth, 280.dp)

private fun LazyListScope.sidePaneLayoutPlayground() {
    playground {
        // 840dp by default, so the split shows on a phone too.
        var widthIndex by rememberSaveable { mutableIntStateOf(3) }
        var coverStyle by rememberSaveable { mutableStateOf(false) }
        var alwaysShowMask by rememberSaveable { mutableStateOf(false) }
        var touchContentEnabled by rememberSaveable { mutableStateOf(true) }
        var showToggleIcon by rememberSaveable { mutableStateOf(true) }
        var paneWidthIndex by rememberSaveable { mutableIntStateOf(1) }
        val state = rememberSidePaneState(initialOpen = true)
        val scope = rememberCoroutineScope()

        PlaygroundPreview(contentPadding = PaddingValues(vertical = 16.dp)) {
            SimulatedWidthBox(simulatedWidth = SimulatedWidths[widthIndex], height = PreviewHeight) {
                SidePaneSample(
                    state = state,
                    coverStyle = coverStyle,
                    modifier = Modifier.fillMaxSize(),
                    alwaysShowMask = alwaysShowMask,
                    touchContentEnabled = touchContentEnabled,
                    showToggleIcon = showToggleIcon,
                    paneWidth = PaneWidths[paneWidthIndex],
                )
            }
        }

        val widthLabels = simulatedWidthLabels()
        val widthTitle = stringResource(Res.string.demo_layout_simulated_width)
        val widthSummary = stringResource(Res.string.demo_layout_simulated_width_summary)
        val toggleTitle = stringResource(Res.string.demo_side_pane_layout_toggle)
        val toggleSummary = stringResource(
            if (state.isOpen) Res.string.demo_side_pane_layout_pane_open else Res.string.demo_side_pane_layout_pane_closed,
        )
        val coverTitle = stringResource(Res.string.demo_side_pane_layout_cover_style)
        val coverSummary = stringResource(Res.string.demo_side_pane_layout_cover_style_summary)
        val maskTitle = stringResource(Res.string.demo_side_pane_layout_always_show_mask)
        val maskSummary = stringResource(Res.string.demo_side_pane_layout_always_show_mask_summary)
        val touchTitle = stringResource(Res.string.demo_side_pane_layout_touch_content)
        val touchSummary = stringResource(Res.string.demo_side_pane_layout_touch_content_summary)
        val iconTitle = stringResource(Res.string.demo_side_pane_layout_show_toggle_icon)
        val paneWidthTitle = stringResource(Res.string.demo_side_pane_layout_pane_width)
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    items = widthLabels,
                    selectedIndex = widthIndex,
                    title = widthTitle,
                    summary = widthSummary,
                    onSelectedIndexChange = { widthIndex = it },
                )
            }
            item {
                ArrowPreference(
                    title = toggleTitle,
                    summary = toggleSummary,
                    onClick = { scope.launch { state.toggle() } },
                )
            }
            item {
                SwitchPreference(
                    checked = coverStyle,
                    onCheckedChange = { coverStyle = it },
                    title = coverTitle,
                    summary = coverSummary,
                )
            }
            item {
                SwitchPreference(
                    checked = alwaysShowMask,
                    onCheckedChange = { alwaysShowMask = it },
                    title = maskTitle,
                    summary = maskSummary,
                )
            }
            item {
                SwitchPreference(
                    checked = touchContentEnabled,
                    onCheckedChange = { touchContentEnabled = it },
                    title = touchTitle,
                    summary = touchSummary,
                )
            }
            item {
                SwitchPreference(
                    checked = showToggleIcon,
                    onCheckedChange = { showToggleIcon = it },
                    title = iconTitle,
                )
            }
            item {
                OverlayDropdownPreference(
                    items = PaneWidths.map { "${it.value.roundToInt()}dp" },
                    selectedIndex = paneWidthIndex,
                    title = paneWidthTitle,
                    onSelectedIndexChange = { paneWidthIndex = it },
                )
            }
        }
    }
}

/** The sample notes folders shown in the pane. */
@Composable
private fun folderNames(): List<String> = listOf(
    stringResource(Res.string.demo_side_pane_layout_folder_all),
    stringResource(Res.string.demo_side_pane_layout_folder_recent),
    stringResource(Res.string.demo_side_pane_layout_folder_favourites),
    stringResource(Res.string.demo_side_pane_layout_folder_shared),
    stringResource(Res.string.demo_side_pane_layout_folder_deleted),
)

@Composable
private fun SidePaneSample(
    state: SidePaneState,
    coverStyle: Boolean,
    modifier: Modifier = Modifier,
    alwaysShowMask: Boolean = false,
    touchContentEnabled: Boolean = true,
    showToggleIcon: Boolean = true,
    paneWidth: Dp = SidePaneLayoutDefaults.PaneWidth,
) {
    val windowHeight = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.height.toDp() }
    val folders = folderNames()
    var selected by rememberSaveable { mutableIntStateOf(0) }
    BoxWithConstraints(modifier = modifier) {
        val split = SidePaneLayoutDefaults.isSplitMode(maxWidth, windowHeight)
        SidePaneLayout(
            pane = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(COUITheme.colorScheme.cardBackground)
                        .padding(top = 104.dp),
                ) {
                    folders.forEachIndexed { index, folder ->
                        BasicComponent(
                            title = folder,
                            titleColor = if (index == selected) {
                                BasicComponentDefaults.titleColor(color = COUITheme.colorScheme.primary)
                            } else {
                                BasicComponentDefaults.titleColor()
                            },
                            onClick = { selected = index },
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxSize(),
            state = state,
            coverStyle = coverStyle,
            alwaysShowMask = alwaysShowMask,
            touchContentEnabled = touchContentEnabled,
            showToggleIcon = showToggleIcon,
            paneWidth = paneWidth,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(COUITheme.colorScheme.backgroundWithCard)
                    // coui_side_pane_layout_toolbar_margin_start: the title clears the toggle icon.
                    .padding(start = 72.dp, top = 56.dp, end = 16.dp),
            ) {
                Text(
                    text = folders[selected],
                    style = COUITheme.textStyles.headlineM,
                )
                Text(
                    text = stringResource(
                        if (split) Res.string.demo_side_pane_layout_split else Res.string.demo_side_pane_layout_compact,
                    ),
                    modifier = Modifier.padding(top = 8.dp),
                    style = COUITheme.textStyles.bodyM,
                    color = COUITheme.colorScheme.labelSecondary,
                )
            }
        }
    }
}
