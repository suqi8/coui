// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
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
import io.github.suqi8.coui.kmp.basic.BreadcrumbBar
import io.github.suqi8.coui.kmp.basic.BreadcrumbItem
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.joinToPath
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_breadcrumb_bar_current_path
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_breadcrumb_bar_depth
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_breadcrumb_bar_full_path
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_breadcrumb_bar_internal_storage
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_breadcrumb_bar_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_breadcrumb_bar_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_breadcrumb_bar_variant_card
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_breadcrumb_bar_variant_page
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.stringResource

/** The demo page of `BreadcrumbBar`, listed in its category and found by the search. */
internal val BreadcrumbBarDemo = Demo(
    id = "breadcrumb_bar",
    apiName = "BreadcrumbBar",
    title = Res.string.demo_breadcrumb_bar_title,
    category = DemoCategory.Navigation,
    page = DemoPage.Variants {
        playground {
            var depthIndex by rememberSaveable { mutableIntStateOf(BreadcrumbDepthOptions.lastIndex) }
            var enabled by rememberSaveable { mutableStateOf(true) }
            var highlightIndex by rememberSaveable { mutableIntStateOf(Int.MAX_VALUE) }
            val items = rememberBreadcrumbItems().take(depthIndex + 1)
            val shownHighlight = highlightIndex.coerceAtMost(items.lastIndex)
            PlaygroundPreview(contentPadding = PaddingValues(vertical = 16.dp)) {
                BreadcrumbBar(
                    items = items,
                    onItemClick = { index -> highlightIndex = index },
                    modifier = Modifier.fillMaxWidth(),
                    highlightIndex = shownHighlight,
                    enabled = enabled,
                )
                BreadcrumbPathText(
                    text = stringResource(Res.string.demo_breadcrumb_bar_current_path, items.subList(0, shownHighlight + 1).joinToPath()),
                    modifier = Modifier.padding(horizontal = 16.dp).padding(top = 12.dp),
                )
            }
            PlaygroundControls {
                item {
                    OverlayDropdownPreference(
                        items = BreadcrumbDepthOptions,
                        selectedIndex = depthIndex,
                        title = stringResource(Res.string.demo_breadcrumb_bar_depth),
                        onSelectedIndexChange = {
                            depthIndex = it
                            highlightIndex = Int.MAX_VALUE
                        },
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
        variant(key = "page", title = Res.string.demo_breadcrumb_bar_variant_page, inCard = false) {
            val items = rememberBreadcrumbItems()
            var highlightIndex by rememberSaveable { mutableIntStateOf(items.lastIndex) }
            BreadcrumbBar(
                items = items,
                onItemClick = { index -> highlightIndex = index },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                highlightIndex = highlightIndex,
            )
        }
        variant(key = "card", title = Res.string.demo_breadcrumb_bar_variant_card) {
            val items = rememberBreadcrumbItems()
            var highlightIndex by rememberSaveable { mutableIntStateOf(items.lastIndex) }
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                BreadcrumbBar(
                    items = items,
                    onItemClick = { index -> highlightIndex = index },
                    modifier = Modifier.fillMaxWidth(),
                    highlightIndex = highlightIndex,
                )
                BreadcrumbPathText(text = stringResource(Res.string.demo_breadcrumb_bar_full_path, items.joinToPath()))
                BreadcrumbPathText(
                    text = stringResource(Res.string.demo_breadcrumb_bar_current_path, items.subList(0, highlightIndex + 1).joinToPath()),
                )
            }
        }
        variant(key = "disabled", title = Res.string.common_disabled) {
            BreadcrumbBar(
                items = rememberBreadcrumbItems(),
                onItemClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = false,
            )
        }
    },
    searchTerms = Res.string.demo_breadcrumb_bar_search_terms,
)

private val BreadcrumbDepthOptions = listOf("1", "2", "3", "4", "5")

/** A file manager path: the localized storage root, then folder names. */
@Composable
private fun rememberBreadcrumbItems(): List<BreadcrumbItem> {
    val storage = stringResource(Res.string.demo_breadcrumb_bar_internal_storage)
    return remember(storage) {
        listOf(
            BreadcrumbItem(path = "/storage/emulated/0", text = storage),
            BreadcrumbItem(path = "DataBackup"),
            BreadcrumbItem(path = "apps"),
            BreadcrumbItem(path = "com.tencent.mobileqq"),
            BreadcrumbItem(path = "user_0"),
        )
    }
}

@Composable
private fun BreadcrumbPathText(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier,
        color = COUITheme.colorScheme.labelSecondaryVariant,
    )
}
