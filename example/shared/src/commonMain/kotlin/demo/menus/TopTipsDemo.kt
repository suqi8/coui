// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.menus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.basic.TopTips
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.Search
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_long_text
import io.github.suqi8.coui.kmp.shared.generated.resources.common_retry
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show_icon
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show_title
import io.github.suqi8.coui.kmp.shared.generated.resources.common_visible
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_top_tips_action_count
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_top_tips_action_update
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_top_tips_backup_text
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_top_tips_closable
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_top_tips_closable_text
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_top_tips_long_icon_text
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_top_tips_marquee
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_top_tips_marquee_text
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_top_tips_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_top_tips_show_action
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_top_tips_show_again
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_top_tips_text
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_top_tips_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_top_tips_title_text
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_top_tips_turn_on
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_top_tips_variant_examples
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_long_text
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.stringResource

/** The demo page of `TopTips`, listed in its category and found by the search. */
internal val TopTipsDemo = Demo(
    id = "top_tips",
    apiName = "TopTips",
    title = Res.string.demo_top_tips_title,
    category = DemoCategory.Menus,
    page = DemoPage.Variants {
        playground { TopTipsPlayground() }
        variant(key = "examples", title = Res.string.demo_top_tips_variant_examples) {
            TopTipsExamples()
        }
    },
    searchTerms = Res.string.demo_top_tips_search_terms,
)

@Composable
private fun ColumnScope.TopTipsPlayground() {
    var visible by rememberSaveable { mutableStateOf(true) }
    var showTitle by rememberSaveable { mutableStateOf(false) }
    var showIcon by rememberSaveable { mutableStateOf(true) }
    var showAction by rememberSaveable { mutableStateOf(true) }
    var closable by rememberSaveable { mutableStateOf(true) }
    var marquee by rememberSaveable { mutableStateOf(false) }
    var longText by rememberSaveable { mutableStateOf(false) }
    var actionCount by rememberSaveable { mutableIntStateOf(0) }

    val searchIcon: @Composable () -> Unit = { Icon(imageVector = COUIIcons.Basic.Search, contentDescription = null) }
    PlaygroundPreview {
        TopTips(
            text = stringResource(if (longText) Res.string.sample_long_text else Res.string.demo_top_tips_text),
            modifier = Modifier.fillMaxWidth(),
            visible = visible,
            title = if (showTitle) stringResource(Res.string.demo_top_tips_title_text) else null,
            startIcon = if (showIcon) searchIcon else null,
            actionText = if (showAction) stringResource(Res.string.demo_top_tips_action_update) else null,
            onAction = { actionCount++ },
            onClose = if (closable) {
                { visible = false }
            } else {
                null
            },
            marquee = marquee,
        )
        Text(
            text = stringResource(Res.string.demo_top_tips_action_count, actionCount),
            modifier = Modifier.padding(top = 12.dp),
            color = COUITheme.colorScheme.labelSecondaryVariant,
            style = COUITheme.textStyles.bodyM,
        )
    }
    val visibleTitle = stringResource(Res.string.common_visible)
    val titleTitle = stringResource(Res.string.common_show_title)
    val iconTitle = stringResource(Res.string.common_show_icon)
    val actionTitle = stringResource(Res.string.demo_top_tips_show_action)
    val closableTitle = stringResource(Res.string.demo_top_tips_closable)
    val marqueeTitle = stringResource(Res.string.demo_top_tips_marquee)
    val longTitle = stringResource(Res.string.common_long_text)
    PlaygroundControls {
        item {
            SwitchPreference(
                checked = visible,
                onCheckedChange = { visible = it },
                title = visibleTitle,
                summary = "visible",
            )
        }
        item {
            SwitchPreference(
                checked = showTitle,
                onCheckedChange = { showTitle = it },
                title = titleTitle,
                summary = "title",
            )
        }
        item {
            SwitchPreference(
                checked = showIcon,
                onCheckedChange = { showIcon = it },
                title = iconTitle,
                summary = "startIcon",
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
                checked = closable,
                onCheckedChange = { closable = it },
                title = closableTitle,
                summary = "onClose",
            )
        }
        item {
            SwitchPreference(
                checked = marquee,
                onCheckedChange = { marquee = it },
                title = marqueeTitle,
                summary = "marquee",
            )
        }
        item {
            SwitchPreference(checked = longText, onCheckedChange = { longText = it }, title = longTitle)
        }
    }
}

/**
 * A closable tip with an icon, a tip with an action, a long tip whose action moves below the
 * text, a titled tip and a marquee tip.
 */
@Composable
private fun TopTipsExamples() {
    var closableVisible by rememberSaveable { mutableStateOf(true) }
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TopTips(
            text = stringResource(Res.string.demo_top_tips_closable_text),
            modifier = Modifier.fillMaxWidth(),
            visible = closableVisible,
            startIcon = { Icon(imageVector = COUIIcons.Basic.Search, contentDescription = null) },
            onClose = { closableVisible = false },
        )
        TopTips(
            text = stringResource(Res.string.demo_top_tips_text),
            modifier = Modifier.fillMaxWidth(),
            actionText = stringResource(Res.string.demo_top_tips_action_update),
            onAction = {},
        )
        TopTips(
            text = stringResource(Res.string.demo_top_tips_long_icon_text),
            modifier = Modifier.fillMaxWidth(),
            startIcon = { Icon(imageVector = COUIIcons.Basic.Search, contentDescription = null) },
            actionText = stringResource(Res.string.demo_top_tips_turn_on),
            onAction = {},
            onClose = {},
        )
        TopTips(
            text = stringResource(Res.string.demo_top_tips_backup_text),
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(Res.string.demo_top_tips_title_text),
            startIcon = { Icon(imageVector = COUIIcons.Basic.Search, contentDescription = null) },
            actionText = stringResource(Res.string.common_retry),
            onAction = {},
            onClose = {},
        )
        TopTips(
            text = stringResource(Res.string.demo_top_tips_marquee_text),
            modifier = Modifier.fillMaxWidth(),
            marquee = true,
            onClose = {},
        )
        if (!closableVisible) {
            TextButton(
                text = stringResource(Res.string.demo_top_tips_show_again),
                onClick = { closableVisible = true },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
