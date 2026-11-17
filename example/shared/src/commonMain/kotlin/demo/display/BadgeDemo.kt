// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import io.github.suqi8.coui.kmp.basic.Badge
import io.github.suqi8.coui.kmp.basic.BadgeBox
import io.github.suqi8.coui.kmp.basic.BadgeDefaults
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.Stepper
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Settings
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_count
import io.github.suqi8.coui.kmp.shared.generated.resources.common_text
import io.github.suqi8.coui.kmp.shared.generated.resources.common_visible
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_badge_form
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_badge_form_dot
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_badge_form_number
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_badge_form_text
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_badge_navigation
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_badge_navigation_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_badge_stroke
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_badge_stroke_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_badge_text_hot
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_badge_text_new
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_badge_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_badge_variant_badge_box
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_badge_variant_forms
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_settings
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

internal val BadgeDemo = Demo(
    id = "badge",
    apiName = "Badge",
    title = Res.string.demo_badge_title,
    category = DemoCategory.Display,
    page = DemoPage.Variants { badgePage() },
    keywords = listOf("红点", "dot", "BadgeBox", "count", "数字"),
)

/** The playground count choices: one, two and three digits, and the 1000+ ellipsis. */
private val BadgeCounts = listOf(5, 66, 666, 1000)

/** The playground text choices. */
private val BadgeTexts: List<StringResource> = listOf(Res.string.demo_badge_text_new, Res.string.demo_badge_text_hot)

private const val FORM_DOT = 0
private const val FORM_NUMBER = 1
private const val FORM_TEXT = 2

private fun LazyListScope.badgePage() {
    playground {
        var form by rememberSaveable { mutableIntStateOf(FORM_NUMBER) }
        var countIndex by rememberSaveable { mutableIntStateOf(1) }
        var textIndex by rememberSaveable { mutableIntStateOf(0) }
        var visible by rememberSaveable { mutableStateOf(true) }
        var stroke by rememberSaveable { mutableStateOf(false) }
        var navigation by rememberSaveable { mutableStateOf(false) }

        val text = stringResource(BadgeTexts[textIndex])
        PlaygroundPreview {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PlaygroundBadge(
                    form = form,
                    count = BadgeCounts[countIndex],
                    text = text,
                    visible = visible,
                    stroke = stroke,
                    navigation = navigation,
                )
                BadgeBox(
                    badge = {
                        PlaygroundBadge(
                            form = form,
                            count = BadgeCounts[countIndex],
                            text = text,
                            visible = visible,
                            stroke = stroke,
                            navigation = navigation,
                        )
                    },
                    overhang = if (form == FORM_DOT) BadgeDefaults.DotOverhang else BadgeDefaults.CountOverhang,
                ) {
                    Icon(
                        imageVector = COUIIcons.Settings,
                        contentDescription = stringResource(Res.string.sample_settings),
                        modifier = Modifier.size(32.dp),
                    )
                }
            }
        }
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.demo_badge_form),
                    items = listOf(
                        stringResource(Res.string.demo_badge_form_dot),
                        stringResource(Res.string.demo_badge_form_number),
                        stringResource(Res.string.demo_badge_form_text),
                    ),
                    selectedIndex = form,
                    onSelectedIndexChange = { form = it },
                )
            }
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.common_count),
                    summary = "count",
                    items = BadgeCounts.map { it.toString() },
                    selectedIndex = countIndex,
                    onSelectedIndexChange = { countIndex = it },
                    enabled = form == FORM_NUMBER,
                )
            }
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.common_text),
                    summary = "text",
                    items = BadgeTexts.map { stringResource(it) },
                    selectedIndex = textIndex,
                    onSelectedIndexChange = { textIndex = it },
                    enabled = form == FORM_TEXT,
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.common_visible),
                    summary = "visible",
                    checked = visible,
                    onCheckedChange = { visible = it },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.demo_badge_stroke),
                    summary = stringResource(Res.string.demo_badge_stroke_summary),
                    checked = stroke,
                    onCheckedChange = { stroke = it },
                )
            }
            item {
                SwitchPreference(
                    title = stringResource(Res.string.demo_badge_navigation),
                    summary = stringResource(Res.string.demo_badge_navigation_summary),
                    checked = navigation,
                    onCheckedChange = { navigation = it },
                    enabled = form != FORM_DOT,
                )
            }
        }
    }
    variant(key = "forms", title = Res.string.demo_badge_variant_forms) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Badge()
            Badge(count = 5)
            Badge(count = 66)
            Badge(count = 666)
            Badge(count = 1000)
            Badge(stroke = true)
            Badge(count = 99, stroke = true)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Badge(text = stringResource(Res.string.demo_badge_text_new))
            Badge(text = stringResource(Res.string.demo_badge_text_hot))
            Badge(count = 5, navigation = true)
            Badge(count = 66, navigation = true)
            Badge(count = 666, navigation = true)
        }
    }
    variant(key = "badge_box", title = Res.string.demo_badge_variant_badge_box) {
        var badgeCount by remember { mutableIntStateOf(8) }
        val settings = stringResource(Res.string.sample_settings)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                BadgeBox(badge = { Badge() }) {
                    Icon(
                        imageVector = COUIIcons.Settings,
                        contentDescription = settings,
                        modifier = Modifier.size(32.dp),
                    )
                }
                BadgeBox(
                    badge = { Badge(count = badgeCount) },
                    overhang = BadgeDefaults.CountOverhang,
                ) {
                    Icon(
                        imageVector = COUIIcons.Settings,
                        contentDescription = settings,
                        modifier = Modifier.size(32.dp),
                    )
                }
            }
            Stepper(
                value = badgeCount,
                onValueChange = { badgeCount = it },
                minValue = 1,
                maxValue = 1000,
                step = 111,
            )
        }
    }
}

/** The playground badge in the chosen form. */
@Composable
private fun PlaygroundBadge(
    form: Int,
    count: Int,
    text: String,
    visible: Boolean,
    stroke: Boolean,
    navigation: Boolean,
) {
    when (form) {
        FORM_DOT -> Badge(visible = visible, stroke = stroke)
        FORM_NUMBER -> Badge(count = count, visible = visible, stroke = stroke, navigation = navigation)
        else -> Badge(text = text, visible = visible, stroke = stroke, navigation = navigation)
    }
}
