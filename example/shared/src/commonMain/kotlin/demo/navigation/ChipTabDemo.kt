// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.navigation

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import io.github.suqi8.coui.kmp.basic.ChipTab
import io.github.suqi8.coui.kmp.basic.ChipTabMaterialType
import io.github.suqi8.coui.kmp.basic.ChipTabRow
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.Check
import io.github.suqi8.coui.kmp.icon.basic.Search
import io.github.suqi8.coui.kmp.icon.basic.Sidebar
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_count
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_material
import io.github.suqi8.coui.kmp.shared.generated.resources.common_search
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_tab_all
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_tab_archives
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_tab_categories
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_tab_disabled_selected
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_tab_documents
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_tab_downloads
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_tab_leading_icon
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_tab_material_type
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_tab_no_material
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_tab_others
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_tab_starred
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_tab_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_tab_variant_backdrop
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_tab_variant_leading_icon
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_tab_variant_scrolling
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_chip_tab_variant_single
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation_image_background
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation_image_background_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_music
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_photos
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_videos
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

internal val ChipTabDemo = Demo(
    id = "chip_tab",
    apiName = "ChipTab",
    title = Res.string.demo_chip_tab_title,
    category = DemoCategory.Navigation,
    page = DemoPage.Variants {
        playground {
            var countIndex by rememberSaveable { mutableIntStateOf(ChipTabDemoTabs.size - ChipTabDemoMinCount) }
            var leadingIcon by rememberSaveable { mutableStateOf(false) }
            var enabled by rememberSaveable { mutableStateOf(true) }
            var material by rememberSaveable { mutableStateOf(true) }
            var materialTypeIndex by rememberSaveable { mutableIntStateOf(0) }
            var imageBackground by rememberSaveable { mutableStateOf(true) }
            var selected by rememberSaveable { mutableIntStateOf(0) }
            val count = countIndex + ChipTabDemoMinCount
            val tabs = ChipTabDemoTabs.take(count).map { stringResource(it) }
            val categories = stringResource(Res.string.demo_chip_tab_categories)
            PlaygroundPreview(contentPadding = PaddingValues(0.dp)) {
                ImageBackdropBox(enabled = imageBackground) {
                    ChipTabRow(
                        tabs = tabs,
                        selectedTabIndex = selected.coerceAtMost(count - 1),
                        onTabSelected = { selected = it },
                        modifier = Modifier.padding(vertical = 16.dp),
                        enabled = enabled,
                        materialEffect = material,
                        materialType = ChipTabMaterialTypes[materialTypeIndex],
                        onLeadingIconClick = { selected = 0 },
                        leadingIcon = if (leadingIcon) {
                            {
                                Icon(
                                    imageVector = COUIIcons.Basic.Sidebar,
                                    contentDescription = categories,
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                        } else {
                            null
                        },
                    )
                }
            }
            PlaygroundControls {
                item {
                    OverlayDropdownPreference(
                        items = ChipTabCountOptions,
                        selectedIndex = countIndex,
                        title = stringResource(Res.string.common_count),
                        onSelectedIndexChange = { countIndex = it },
                    )
                }
                item {
                    SwitchPreference(
                        title = stringResource(Res.string.demo_chip_tab_leading_icon),
                        checked = leadingIcon,
                        onCheckedChange = { leadingIcon = it },
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
                        title = stringResource(Res.string.common_material),
                        checked = material,
                        onCheckedChange = { material = it },
                    )
                }
                item {
                    OverlayDropdownPreference(
                        items = ChipTabMaterialTypeNames,
                        selectedIndex = materialTypeIndex,
                        title = stringResource(Res.string.demo_chip_tab_material_type),
                        onSelectedIndexChange = { materialTypeIndex = it },
                        enabled = material,
                    )
                }
                item {
                    SwitchPreference(
                        title = stringResource(Res.string.demo_navigation_image_background),
                        summary = stringResource(Res.string.demo_navigation_image_background_summary),
                        checked = imageBackground,
                        onCheckedChange = { imageBackground = it },
                    )
                }
            }
        }
        variant(key = "scrolling", title = Res.string.demo_chip_tab_variant_scrolling) {
            var selected by rememberSaveable { mutableIntStateOf(0) }
            ChipTabRow(
                tabs = ChipTabDemoTabs.map { stringResource(it) },
                selectedTabIndex = selected,
                onTabSelected = { selected = it },
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }
        variant(key = "leading_icon", title = Res.string.demo_chip_tab_variant_leading_icon) {
            var selected by rememberSaveable { mutableIntStateOf(1) }
            val categories = stringResource(Res.string.demo_chip_tab_categories)
            ChipTabRow(
                tabs = ChipTabDemoTabs.map { stringResource(it) },
                selectedTabIndex = selected,
                onTabSelected = { selected = it },
                modifier = Modifier.padding(vertical = 8.dp),
                onLeadingIconClick = { selected = 0 },
                leadingIcon = {
                    Icon(
                        imageVector = COUIIcons.Basic.Sidebar,
                        contentDescription = categories,
                        modifier = Modifier.size(20.dp),
                    )
                },
            )
        }
        variant(key = "single", title = Res.string.demo_chip_tab_variant_single) {
            SingleChipTabsSample()
        }
        // The COUI 17 chip tab material over a recorded image: the backdrop replaces the fill with
        // the blurred (or, for the no-blur type, filtered) and tinted image.
        variant(key = "backdrop", title = Res.string.demo_chip_tab_variant_backdrop, inCard = false) {
            var selectedBlur by rememberSaveable { mutableIntStateOf(0) }
            var selectedNoBlur by rememberSaveable { mutableIntStateOf(2) }
            val tabs = ChipTabDemoTabs.map { stringResource(it) }
            ImageBackdropBox(enabled = true, modifier = Modifier.padding(horizontal = 16.dp)) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    ChipTabRow(
                        tabs = tabs,
                        selectedTabIndex = selectedBlur,
                        onTabSelected = { selectedBlur = it },
                    )
                    ChipTabRow(
                        tabs = tabs,
                        selectedTabIndex = selectedNoBlur,
                        onTabSelected = { selectedNoBlur = it },
                        materialType = ChipTabMaterialType.NoBlur,
                    )
                }
            }
        }
    },
    keywords = listOf("tab", "chip", "胶囊", "筛选"),
)

private val ChipTabDemoTabs: List<StringResource> = listOf(
    Res.string.demo_chip_tab_all,
    Res.string.sample_photos,
    Res.string.sample_videos,
    Res.string.demo_chip_tab_documents,
    Res.string.sample_music,
    Res.string.demo_chip_tab_downloads,
    Res.string.demo_chip_tab_archives,
    Res.string.demo_chip_tab_others,
)

private val ChipTabDemoMinCount = 2
private val ChipTabCountOptions = (ChipTabDemoMinCount..ChipTabDemoTabs.size).map { it.toString() }
private val ChipTabMaterialTypes = ChipTabMaterialType.entries
private val ChipTabMaterialTypeNames = ChipTabMaterialTypes.map { it.name }

/** Single chip tabs: with an icon, without the material, icon only and disabled. */
@Composable
private fun SingleChipTabsSample(modifier: Modifier = Modifier) {
    var withIcon by rememberSaveable { mutableStateOf(true) }
    var plain by rememberSaveable { mutableStateOf(false) }
    var iconOnly by rememberSaveable { mutableStateOf(false) }
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ChipTab(
                selected = withIcon,
                onClick = { withIcon = !withIcon },
                label = stringResource(Res.string.demo_chip_tab_starred),
                icon = {
                    Icon(
                        imageVector = COUIIcons.Basic.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                },
            )
            ChipTab(
                selected = plain,
                onClick = { plain = !plain },
                label = stringResource(Res.string.demo_chip_tab_no_material),
                materialEffect = false,
            )
            ChipTab(
                selected = iconOnly,
                onClick = { iconOnly = !iconOnly },
                icon = {
                    Icon(
                        imageVector = COUIIcons.Basic.Search,
                        contentDescription = stringResource(Res.string.common_search),
                        modifier = Modifier.size(16.dp),
                    )
                },
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ChipTab(
                selected = false,
                onClick = {},
                label = stringResource(Res.string.common_disabled),
                enabled = false,
            )
            ChipTab(
                selected = true,
                onClick = {},
                label = stringResource(Res.string.demo_chip_tab_disabled_selected),
                enabled = false,
            )
        }
    }
}
