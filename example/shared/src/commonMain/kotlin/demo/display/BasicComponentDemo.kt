// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.display

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.LocalDemoSnackbarHostState
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Info
import io.github.suqi8.coui.kmp.icon.extended.Settings
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_long_text
import io.github.suqi8.coui.kmp.shared.generated.resources.common_selected
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_basic_component_badge_dot
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_basic_component_badge_none
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_basic_component_clickable
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_basic_component_clicked
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_basic_component_end
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_basic_component_end_1
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_basic_component_end_2
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_basic_component_end_badge
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_basic_component_end_content
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_basic_component_end_icon
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_basic_component_red_dot
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_basic_component_red_dot_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_basic_component_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_basic_component_selectable
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_basic_component_selected_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_basic_component_start
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_basic_component_start_badge
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_basic_component_start_badge_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_basic_component_start_content
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_basic_component_tap_to_select
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_basic_component_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_basic_component_variant_actions
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_basic_component_variant_selection
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_long_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_long_title
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_settings
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_title
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

/** The demo page of `BasicComponent`, listed in its category and found by the search. */
internal val BasicComponentDemo = Demo(
    id = "basic_component",
    apiName = "BasicComponent",
    title = Res.string.demo_basic_component_title,
    category = DemoCategory.Display,
    page = DemoPage.Variants { basicComponentPage() },
    searchTerms = Res.string.demo_basic_component_search_terms,
)

/** The end red dot choices of the playground: hidden, a plain dot and two numbers. */
private val EndBadgeCounts = listOf<Int?>(null, 0, 3, 99)

private fun LazyListScope.basicComponentPage() {
    playground {
        var showSummary by rememberSaveable { mutableStateOf(true) }
        var longText by rememberSaveable { mutableStateOf(false) }
        var showStart by rememberSaveable { mutableStateOf(true) }
        var showEnd by rememberSaveable { mutableStateOf(true) }
        var showEndIcon by rememberSaveable { mutableStateOf(false) }
        var startBadge by rememberSaveable { mutableStateOf(false) }
        var endBadgeIndex by rememberSaveable { mutableIntStateOf(0) }
        var enabled by rememberSaveable { mutableStateOf(true) }
        var clickable by rememberSaveable { mutableStateOf(true) }
        var selected by rememberSaveable { mutableStateOf(false) }

        val snackbarHostState = LocalDemoSnackbarHostState.current
        val scope = rememberCoroutineScope()
        val endColor = if (enabled) COUITheme.colorScheme.labelTertiary else COUITheme.colorScheme.disabledNeutral

        PlaygroundPreview(contentPadding = PaddingValues(0.dp)) {
            val startAction: (@Composable () -> Unit)? = if (showStart) {
                { Icon(imageVector = COUIIcons.Settings, contentDescription = null) }
            } else {
                null
            }
            val endActions: (@Composable RowScope.() -> Unit)? = if (showEnd) {
                {
                    Text(
                        text = stringResource(Res.string.demo_basic_component_end),
                        color = endColor,
                        style = COUITheme.textStyles.bodyM,
                    )
                }
            } else {
                null
            }
            val endIcon: (@Composable () -> Unit)? = if (showEndIcon) {
                { Icon(imageVector = COUIIcons.Info, contentDescription = null) }
            } else {
                null
            }
            val onClick: (() -> Unit)? = if (clickable) {
                {
                    scope.launch {
                        snackbarHostState.showSnackbar(getString(Res.string.demo_basic_component_clicked))
                    }
                }
            } else {
                null
            }
            BasicComponent(
                title = stringResource(if (longText) Res.string.sample_long_title else Res.string.sample_title),
                summary = if (showSummary) {
                    stringResource(if (longText) Res.string.sample_long_summary else Res.string.sample_summary)
                } else {
                    null
                },
                startAction = startAction,
                endActions = endActions,
                onClick = onClick,
                enabled = enabled,
                selected = selected,
                startBadge = startBadge,
                endBadgeCount = EndBadgeCounts[endBadgeIndex],
                endIcon = endIcon,
            )
        }
        PlaygroundControls {
            item {
                SwitchPreference(
                    title = stringResource(Res.string.common_show_summary),
                    checked = showSummary,
                    onCheckedChange = { showSummary = it },
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
                SwitchPreference(
                    title = stringResource(Res.string.demo_basic_component_start_content),
                    summary = "startAction",
                    checked = showStart,
                    onCheckedChange = { showStart = it },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.demo_basic_component_end_content),
                    summary = "endActions",
                    checked = showEnd,
                    onCheckedChange = { showEnd = it },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.demo_basic_component_end_icon),
                    summary = "endIcon",
                    checked = showEndIcon,
                    onCheckedChange = { showEndIcon = it },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.demo_basic_component_start_badge),
                    summary = "startBadge",
                    checked = startBadge,
                    onCheckedChange = { startBadge = it },
                )
            }
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.demo_basic_component_end_badge),
                    summary = "endBadgeCount",
                    items = listOf(
                        stringResource(Res.string.demo_basic_component_badge_none),
                        stringResource(Res.string.demo_basic_component_badge_dot),
                        "3",
                        "99",
                    ),
                    selectedIndex = endBadgeIndex,
                    onSelectedIndexChange = { endBadgeIndex = it },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.demo_basic_component_clickable),
                    summary = "onClick",
                    checked = clickable,
                    onCheckedChange = { clickable = it },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.common_selected),
                    summary = "selected",
                    checked = selected,
                    onCheckedChange = { selected = it },
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
    variant(key = "actions", title = Res.string.demo_basic_component_variant_actions) {
        CardListGroup {
            item { ActionsRow(enabled = true) }
            item { ActionsRow(enabled = false) }
        }
    }
    variant(key = "selection", title = Res.string.demo_basic_component_variant_selection) {
        CardListGroup {
            item {
                var selected by remember { mutableStateOf(false) }
                BasicComponent(
                    title = stringResource(Res.string.demo_basic_component_selectable),
                    summary = stringResource(
                        if (selected) Res.string.demo_basic_component_selected_summary else Res.string.demo_basic_component_tap_to_select,
                    ),
                    endBadgeCount = 3,
                    selected = selected,
                    onClick = { selected = !selected },
                )
            }
            item {
                BasicComponent(
                    title = stringResource(Res.string.demo_basic_component_red_dot),
                    summary = stringResource(Res.string.demo_basic_component_red_dot_summary),
                    endBadgeCount = 0,
                    onClick = {},
                )
            }
            item {
                BasicComponent(
                    title = stringResource(Res.string.sample_settings),
                    summary = stringResource(Res.string.demo_basic_component_start_badge_summary),
                    startAction = { Icon(imageVector = COUIIcons.Settings, contentDescription = null) },
                    startBadge = true,
                    onClick = {},
                )
            }
        }
    }
}

/** A row with text on both sides, as in the original demo; the disabled one dims its end texts. */
@Composable
private fun ActionsRow(enabled: Boolean) {
    val sideColor = if (enabled) COUITheme.colorScheme.labelTertiary else COUITheme.colorScheme.disabledNeutral
    BasicComponent(
        title = stringResource(if (enabled) Res.string.sample_title else Res.string.common_disabled),
        summary = stringResource(Res.string.sample_summary),
        startAction = {
            Text(
                text = stringResource(Res.string.demo_basic_component_start),
                color = if (enabled) COUITheme.colorScheme.labelPrimary else COUITheme.colorScheme.disabledNeutral,
            )
        },
        endActions = {
            Text(
                text = stringResource(Res.string.demo_basic_component_end_1),
                fontSize = COUITheme.textStyles.bodyM.fontSize,
                color = sideColor,
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(Res.string.demo_basic_component_end_2),
                fontSize = COUITheme.textStyles.bodyM.fontSize,
                color = sideColor,
            )
        },
        enabled = enabled,
    )
}
