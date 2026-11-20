// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.FloatingAccessoryNavigationBar
import io.github.suqi8.coui.kmp.basic.FloatingNavigationBarAccessory
import io.github.suqi8.coui.kmp.basic.FloatingNavigationBarItem
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Add
import io.github.suqi8.coui.kmp.icon.extended.Close
import io.github.suqi8.coui.kmp.icon.extended.Contacts
import io.github.suqi8.coui.kmp.icon.extended.Music
import io.github.suqi8.coui.kmp.icon.extended.Photos
import io.github.suqi8.coui.kmp.icon.extended.Search
import io.github.suqi8.coui.kmp.icon.extended.Settings
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_close
import io.github.suqi8.coui.kmp.shared.generated.resources.common_count
import io.github.suqi8.coui.kmp.shared.generated.resources.common_material
import io.github.suqi8.coui.kmp.shared.generated.resources.common_search
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show_divider
import io.github.suqi8.coui.kmp.shared.generated.resources.common_visible
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_accessory_navigation_bar_end
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_accessory_navigation_bar_end_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_accessory_navigation_bar_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_accessory_navigation_bar_start
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_accessory_navigation_bar_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_accessory_navigation_bar_variant_plain
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation_create
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation_image_background
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation_image_background_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_music
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_photos
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_profile
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_settings
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/** The demo page of `FloatingAccessoryNavigationBar`, listed in its category and found by the search. */
internal val FloatingAccessoryNavigationBarDemo = Demo(
    id = "floating_accessory_navigation_bar",
    apiName = "FloatingAccessoryNavigationBar",
    title = Res.string.demo_floating_accessory_navigation_bar_title,
    category = DemoCategory.Navigation,
    page = DemoPage.Variants {
        playground {
            var selected by rememberSaveable { mutableIntStateOf(0) }
            var countIndex by rememberSaveable { mutableIntStateOf(1) }
            var capsuleShown by rememberSaveable { mutableStateOf(true) }
            var startShown by rememberSaveable { mutableStateOf(true) }
            var endShown by rememberSaveable { mutableStateOf(true) }
            var searching by rememberSaveable { mutableStateOf(false) }
            var material by rememberSaveable { mutableStateOf(true) }
            var showDivider by rememberSaveable { mutableStateOf(false) }
            var imageBackground by rememberSaveable { mutableStateOf(true) }
            val itemCount = countIndex + AccessoryDemoMinCount
            PlaygroundPreview {
                ImageBackdropBox(enabled = imageBackground) {
                    FloatingAccessoryNavigationBar(
                        visible = capsuleShown,
                        startAccessoryVisible = startShown,
                        endAccessoryVisible = endShown,
                        materialEffect = material,
                        showDivider = showDivider,
                        defaultWindowInsetsPadding = false,
                        startAccessory = {
                            FloatingNavigationBarAccessory(
                                onClick = { selected = 0 },
                                icon = COUIIcons.Add,
                                contentDescription = stringResource(Res.string.demo_navigation_create),
                                materialEffect = material,
                            )
                        },
                        endAccessory = {
                            // Clicking swaps the icon with the COUI 17 icon-change animation.
                            FloatingNavigationBarAccessory(
                                onClick = { searching = !searching },
                                icon = if (searching) COUIIcons.Close else COUIIcons.Search,
                                contentDescription = stringResource(if (searching) Res.string.common_close else Res.string.common_search),
                                materialEffect = material,
                            )
                        },
                    ) {
                        repeat(itemCount) { index ->
                            FloatingNavigationBarItem(
                                selected = selected.coerceAtMost(itemCount - 1) == index,
                                onClick = { selected = index },
                                icon = AccessoryDemoIcons[index],
                                label = stringResource(AccessoryDemoLabels[index]),
                            )
                        }
                    }
                }
            }
            PlaygroundControls {
                item {
                    // The capsule alone: the COUI 17 show / hide springs, the accessories stay.
                    SwitchPreference(
                        title = stringResource(Res.string.common_visible),
                        checked = capsuleShown,
                        onCheckedChange = { capsuleShown = it },
                    )
                }
                item {
                    SwitchPreference(
                        title = stringResource(Res.string.demo_floating_accessory_navigation_bar_start),
                        checked = startShown,
                        onCheckedChange = { startShown = it },
                    )
                }
                item {
                    SwitchPreference(
                        title = stringResource(Res.string.demo_floating_accessory_navigation_bar_end),
                        summary = stringResource(Res.string.demo_floating_accessory_navigation_bar_end_summary),
                        checked = endShown,
                        onCheckedChange = { endShown = it },
                    )
                }
                item {
                    OverlayDropdownPreference(
                        items = AccessoryDemoCountOptions,
                        selectedIndex = countIndex,
                        title = stringResource(Res.string.common_count),
                        onSelectedIndexChange = { countIndex = it },
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
                    SwitchPreference(
                        title = stringResource(Res.string.common_show_divider),
                        checked = showDivider,
                        onCheckedChange = { showDivider = it },
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
        variant(key = "plain", title = Res.string.demo_floating_accessory_navigation_bar_variant_plain) {
            var selected by rememberSaveable { mutableIntStateOf(0) }
            // Without accessories the bar is the plain capsule.
            FloatingAccessoryNavigationBar(defaultWindowInsetsPadding = false) {
                repeat(3) { index ->
                    FloatingNavigationBarItem(
                        selected = selected == index,
                        onClick = { selected = index },
                        icon = AccessoryDemoIcons[index],
                        label = stringResource(AccessoryDemoLabels[index]),
                    )
                }
            }
        }
    },
    searchTerms = Res.string.demo_floating_accessory_navigation_bar_search_terms,
)

private val AccessoryDemoIcons: List<ImageVector> = listOf(
    COUIIcons.Photos,
    COUIIcons.Music,
    COUIIcons.Contacts,
    COUIIcons.Settings,
)

private val AccessoryDemoLabels: List<StringResource> = listOf(
    Res.string.sample_photos,
    Res.string.sample_music,
    Res.string.sample_profile,
    Res.string.sample_settings,
)

private val AccessoryDemoMinCount = 2
private val AccessoryDemoCountOptions = listOf("2", "3", "4")
