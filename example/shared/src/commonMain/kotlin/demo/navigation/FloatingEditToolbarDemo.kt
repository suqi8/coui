// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.navigation

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import io.github.suqi8.coui.kmp.basic.FloatingEditToolbar
import io.github.suqi8.coui.kmp.basic.FloatingEditToolbarItem
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Copy
import io.github.suqi8.coui.kmp.icon.extended.Delete
import io.github.suqi8.coui.kmp.icon.extended.Favorites
import io.github.suqi8.coui.kmp.icon.extended.Share
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_copy
import io.github.suqi8.coui.kmp.shared.generated.resources.common_count
import io.github.suqi8.coui.kmp.shared.generated.resources.common_delete
import io.github.suqi8.coui.kmp.shared.generated.resources.common_material
import io.github.suqi8.coui.kmp.shared.generated.resources.common_share
import io.github.suqi8.coui.kmp.shared.generated.resources.common_visible
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_edit_toolbar_disabled_item
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_edit_toolbar_favorite
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_edit_toolbar_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_edit_toolbar_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_edit_toolbar_variant_compact
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_edit_toolbar_variant_wide
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation_badges
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation_image_background
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation_image_background_summary
import org.jetbrains.compose.resources.stringResource

/** The demo page of `FloatingEditToolbar`, listed in its category and found by the search. */
internal val FloatingEditToolbarDemo = Demo(
    id = "floating_edit_toolbar",
    apiName = "FloatingEditToolbar",
    title = Res.string.demo_floating_edit_toolbar_title,
    category = DemoCategory.Navigation,
    page = DemoPage.Variants {
        playground {
            var visible by rememberSaveable { mutableStateOf(true) }
            var countIndex by rememberSaveable { mutableIntStateOf(3) }
            var badges by rememberSaveable { mutableStateOf(true) }
            var disabledItem by rememberSaveable { mutableStateOf(true) }
            var material by rememberSaveable { mutableStateOf(true) }
            var imageBackground by rememberSaveable { mutableStateOf(true) }
            PlaygroundPreview {
                ImageBackdropBox(enabled = imageBackground) {
                    EditToolbarSample(
                        visible = visible,
                        itemCount = countIndex + 1,
                        badges = badges,
                        disabledItem = disabledItem,
                        material = material,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            PlaygroundControls {
                item {
                    SwitchPreference(
                        title = stringResource(Res.string.common_visible),
                        checked = visible,
                        onCheckedChange = { visible = it },
                    )
                }
                item {
                    OverlayDropdownPreference(
                        items = EditToolbarCountOptions,
                        selectedIndex = countIndex,
                        title = stringResource(Res.string.common_count),
                        onSelectedIndexChange = { countIndex = it },
                    )
                }
                item {
                    SwitchPreference(
                        title = stringResource(Res.string.demo_navigation_badges),
                        checked = badges,
                        onCheckedChange = { badges = it },
                    )
                }
                item {
                    SwitchPreference(
                        title = stringResource(Res.string.demo_floating_edit_toolbar_disabled_item),
                        checked = disabledItem,
                        onCheckedChange = { disabledItem = it },
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
                        title = stringResource(Res.string.demo_navigation_image_background),
                        summary = stringResource(Res.string.demo_navigation_image_background_summary),
                        checked = imageBackground,
                        onCheckedChange = { imageBackground = it },
                    )
                }
            }
        }
        variant(key = "compact", title = Res.string.demo_floating_edit_toolbar_variant_compact) {
            EditToolbarSample(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
        }
        variant(key = "wide", title = Res.string.demo_floating_edit_toolbar_variant_wide) {
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()).padding(vertical = 8.dp)) {
                EditToolbarSample(modifier = Modifier.width(720.dp))
            }
        }
    },
    searchTerms = Res.string.demo_floating_edit_toolbar_search_terms,
)

private val EditToolbarCountOptions = listOf("1", "2", "3", "4")

/**
 * The edit toolbar of the demos: share, copy (red dot), favourite (disabled) and delete (number
 * badge). Below 600dp of width the items put their icons over the labels, from there beside them.
 */
@Composable
private fun EditToolbarSample(
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    itemCount: Int = 4,
    badges: Boolean = true,
    disabledItem: Boolean = true,
    material: Boolean = true,
) {
    FloatingEditToolbar(
        modifier = modifier,
        visible = visible,
        materialEffect = material,
        defaultWindowInsetsPadding = false,
    ) {
        FloatingEditToolbarItem(onClick = {}, icon = COUIIcons.Share, label = stringResource(Res.string.common_share))
        if (itemCount >= 2) {
            FloatingEditToolbarItem(
                onClick = {},
                icon = COUIIcons.Copy,
                label = stringResource(Res.string.common_copy),
                badge = if (badges) 0 else null,
            )
        }
        if (itemCount >= 3) {
            FloatingEditToolbarItem(
                onClick = {},
                icon = COUIIcons.Favorites,
                label = stringResource(Res.string.demo_floating_edit_toolbar_favorite),
                enabled = !disabledItem,
            )
        }
        if (itemCount >= 4) {
            FloatingEditToolbarItem(
                onClick = {},
                icon = COUIIcons.Delete,
                label = stringResource(Res.string.common_delete),
                badge = if (badges) 3 else null,
            )
        }
    }
}
