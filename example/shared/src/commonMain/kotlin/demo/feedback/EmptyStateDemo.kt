// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.feedback

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.EmptyState
import io.github.suqi8.coui.kmp.basic.EmptyStateSizeType
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_long_text
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.common_size
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_empty_state_height
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_empty_state_no_content
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_empty_state_no_results
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_empty_state_refresh
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_empty_state_refreshed
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_empty_state_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_empty_state_show_action
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_empty_state_show_illustration
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_empty_state_subtitle
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_empty_state_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_empty_state_try_another
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_empty_state_variant_default
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_empty_state_variant_small
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_long_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_long_title
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.stringResource

/** The demo page of `EmptyState`, listed in its category and found by the search. */
internal val EmptyStateDemo = Demo(
    id = "empty_state",
    apiName = "EmptyState",
    title = Res.string.demo_empty_state_title,
    category = DemoCategory.Feedback,
    page = DemoPage.Variants {
        playground { EmptyStatePlayground() }
        variant(key = "default", title = Res.string.demo_empty_state_variant_default) {
            var refreshes by rememberSaveable { mutableIntStateOf(0) }
            EmptyState(
                title = stringResource(Res.string.demo_empty_state_no_content),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp),
                subtitle = if (refreshes == 0) {
                    stringResource(Res.string.demo_empty_state_subtitle)
                } else {
                    stringResource(Res.string.demo_empty_state_refreshed, refreshes)
                },
                actionText = stringResource(Res.string.demo_empty_state_refresh),
                onActionClick = { refreshes++ },
                illustration = { EmptyIllustration() },
            )
        }
        variant(key = "small", title = Res.string.demo_empty_state_variant_small) {
            EmptyState(
                title = stringResource(Res.string.demo_empty_state_no_results),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                subtitle = stringResource(Res.string.demo_empty_state_try_another),
                sizeType = EmptyStateSizeType.Small,
                illustration = { EmptyIllustration() },
            )
        }
    },
    searchTerms = Res.string.demo_empty_state_search_terms,
)

@Composable
private fun ColumnScope.EmptyStatePlayground() {
    var sizeIndex by rememberSaveable { mutableIntStateOf(0) }
    var showSubtitle by rememberSaveable { mutableStateOf(true) }
    var showAction by rememberSaveable { mutableStateOf(true) }
    var showIllustration by rememberSaveable { mutableStateOf(true) }
    var longText by rememberSaveable { mutableStateOf(false) }
    var height by rememberSaveable { mutableFloatStateOf(360f) }
    var refreshes by rememberSaveable { mutableIntStateOf(0) }

    val title = stringResource(if (longText) Res.string.sample_long_title else Res.string.demo_empty_state_no_content)
    val subtitle = when {
        !showSubtitle -> null
        longText -> stringResource(Res.string.sample_long_summary)
        refreshes > 0 -> stringResource(Res.string.demo_empty_state_refreshed, refreshes)
        else -> stringResource(Res.string.demo_empty_state_subtitle)
    }
    val illustration: @Composable () -> Unit = { EmptyIllustration() }
    PlaygroundPreview(contentPadding = PaddingValues(0.dp)) {
        EmptyState(
            title = title,
            modifier = Modifier
                .fillMaxWidth()
                .height(height.dp),
            subtitle = subtitle,
            actionText = if (showAction) stringResource(Res.string.demo_empty_state_refresh) else null,
            onActionClick = if (showAction) {
                { refreshes++ }
            } else {
                null
            },
            sizeType = EmptyStateSizeType.entries[sizeIndex],
            illustration = if (showIllustration) illustration else null,
        )
    }
    val sizeTitle = stringResource(Res.string.common_size)
    val subtitleTitle = stringResource(Res.string.common_show_summary)
    val actionTitle = stringResource(Res.string.demo_empty_state_show_action)
    val illustrationTitle = stringResource(Res.string.demo_empty_state_show_illustration)
    val longTitle = stringResource(Res.string.common_long_text)
    val heightTitle = stringResource(Res.string.demo_empty_state_height)
    PlaygroundControls {
        item {
            OverlayDropdownPreference(
                items = EmptyStateSizeType.entries.map { it.name },
                selectedIndex = sizeIndex,
                title = sizeTitle,
                summary = "EmptyStateSizeType",
                onSelectedIndexChange = { sizeIndex = it },
            )
        }
        item {
            SliderPreference(
                value = height,
                onValueChange = { height = it },
                title = heightTitle,
                valueText = "${height.toInt()}dp",
                valueRange = 160f..520f,
                steps = 8,
            )
        }
        item {
            SwitchPreference(
                checked = showSubtitle,
                onCheckedChange = { showSubtitle = it },
                title = subtitleTitle,
                summary = "subtitle",
            )
        }
        item {
            SwitchPreference(
                checked = showAction,
                onCheckedChange = { showAction = it },
                title = actionTitle,
                summary = "actionText",
            )
        }
        item {
            SwitchPreference(
                checked = showIllustration,
                onCheckedChange = { showIllustration = it },
                title = illustrationTitle,
                summary = "illustration",
            )
        }
        item {
            SwitchPreference(checked = longText, onCheckedChange = { longText = it }, title = longTitle)
        }
    }
}

/** A placeholder illustration: an empty tray. */
@Composable
private fun EmptyIllustration(modifier: Modifier = Modifier) {
    val tray = COUITheme.colorScheme.secondaryContainer
    val line = COUITheme.colorScheme.labelTertiary
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        drawRoundRect(
            color = tray,
            topLeft = Offset(w * 0.3f, h * 0.3f),
            size = Size(w * 0.4f, h * 0.45f),
            cornerRadius = CornerRadius(h * 0.06f),
        )
        drawLine(line, Offset(w * 0.38f, h * 0.45f), Offset(w * 0.62f, h * 0.45f), strokeWidth = h * 0.025f)
        drawLine(line, Offset(w * 0.38f, h * 0.55f), Offset(w * 0.55f, h * 0.55f), strokeWidth = h * 0.025f)
    }
}
