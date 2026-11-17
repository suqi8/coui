// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import io.github.suqi8.coui.kmp.basic.CompProgressIndicator
import io.github.suqi8.coui.kmp.basic.CompProgressIndicatorType
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_loading
import io.github.suqi8.coui.kmp.shared.generated.resources.common_long_text
import io.github.suqi8.coui.kmp.shared.generated.resources.common_style
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_comp_progress_indicator_long_text
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_comp_progress_indicator_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_comp_progress_indicator_show_text
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_comp_progress_indicator_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_comp_progress_indicator_variant_all
import org.jetbrains.compose.resources.stringResource

/** The demo page of `CompProgressIndicator`, listed in its category and found by the search. */
internal val CompProgressIndicatorDemo = Demo(
    id = "comp_progress_indicator",
    apiName = "CompProgressIndicator",
    title = Res.string.demo_comp_progress_indicator_title,
    category = DemoCategory.Feedback,
    page = DemoPage.Variants {
        playground { CompProgressIndicatorPlayground() }
        variant(key = "all_types", title = Res.string.demo_comp_progress_indicator_variant_all) {
            val loading = stringResource(Res.string.common_loading)
            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                CompProgressIndicator(type = CompProgressIndicatorType.Large)
                CompProgressIndicator(type = CompProgressIndicatorType.Small)
                CompProgressIndicator(type = CompProgressIndicatorType.SmallWithTextHorizontal, text = loading)
                CompProgressIndicator(type = CompProgressIndicatorType.LargeWithTextVertical, text = loading)
                CompProgressIndicator(type = CompProgressIndicatorType.SmallWithTextVertical, text = loading)
            }
        }
    },
    searchTerms = Res.string.demo_comp_progress_indicator_search_terms,
)

/** The types that lay out a text label beside or below the spinner. */
private val TypesWithText = setOf(
    CompProgressIndicatorType.SmallWithTextHorizontal,
    CompProgressIndicatorType.LargeWithTextVertical,
    CompProgressIndicatorType.SmallWithTextVertical,
)

@Composable
private fun ColumnScope.CompProgressIndicatorPlayground() {
    var typeIndex by rememberSaveable { mutableIntStateOf(3) }
    var showText by rememberSaveable { mutableStateOf(true) }
    var longText by rememberSaveable { mutableStateOf(false) }

    val type = CompProgressIndicatorType.entries[typeIndex]
    val text = when {
        !showText || type !in TypesWithText -> null
        longText -> stringResource(Res.string.demo_comp_progress_indicator_long_text)
        else -> stringResource(Res.string.common_loading)
    }
    PlaygroundPreview {
        Box(
            modifier = Modifier.fillMaxWidth().heightIn(min = 96.dp),
            contentAlignment = Alignment.Center,
        ) {
            CompProgressIndicator(type = type, text = text)
        }
    }
    val typeTitle = stringResource(Res.string.common_style)
    val showTextTitle = stringResource(Res.string.demo_comp_progress_indicator_show_text)
    val longTitle = stringResource(Res.string.common_long_text)
    PlaygroundControls {
        item {
            OverlayDropdownPreference(
                items = CompProgressIndicatorType.entries.map { it.name },
                selectedIndex = typeIndex,
                title = typeTitle,
                summary = "CompProgressIndicatorType",
                onSelectedIndexChange = { typeIndex = it },
            )
        }
        item {
            SwitchPreference(
                checked = showText,
                onCheckedChange = { showText = it },
                title = showTextTitle,
                summary = "text",
                enabled = type in TypesWithText,
            )
        }
        item {
            SwitchPreference(
                checked = longText,
                onCheckedChange = { longText = it },
                title = longTitle,
                enabled = type in TypesWithText && showText,
            )
        }
    }
}
