// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
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
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.Stepper
import io.github.suqi8.coui.kmp.basic.StepperDefaults
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Settings
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_disabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_steps
import io.github.suqi8.coui.kmp.shared.generated.resources.common_value_format
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_stepper_badge_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_stepper_basic
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_stepper_basic_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_stepper_hold
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_stepper_hold_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_stepper_max
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_stepper_min
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_stepper_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_stepper_variant_badge
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_stepper_variant_rows
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_notifications
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_settings
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.stringResource

internal val StepperDemo = Demo(
    id = "stepper",
    apiName = "Stepper",
    title = Res.string.demo_stepper_title,
    category = DemoCategory.Buttons,
    page = DemoPage.Variants { stepperPage() },
    keywords = listOf("计数", "counter", "number", "plus", "minus"),
)

/** The minimum value choices, the COUI default first. */
private val MinValues = listOf(StepperDefaults.MinValue, 0, 1)

/** The maximum value choices, the COUI default last. */
private val MaxValues = listOf(10, 100, 1000, StepperDefaults.MaxValue)

/** The step choices. */
private val Steps = listOf(1, 5, 10, 111)

private fun LazyListScope.stepperPage() {
    playground {
        var value by rememberSaveable { mutableIntStateOf(2) }
        var minIndex by rememberSaveable { mutableIntStateOf(1) }
        var maxIndex by rememberSaveable { mutableIntStateOf(0) }
        var stepIndex by rememberSaveable { mutableIntStateOf(0) }
        var enabled by rememberSaveable { mutableStateOf(true) }

        val min = MinValues[minIndex]
        val max = MaxValues[maxIndex]
        val shown = value.coerceIn(min, max)
        PlaygroundPreview {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(Res.string.common_value_format, shown.toString()),
                    color = COUITheme.colorScheme.labelSecondaryVariant,
                    style = COUITheme.textStyles.bodyM,
                )
                Stepper(
                    value = shown,
                    onValueChange = { value = it },
                    minValue = min,
                    maxValue = max,
                    step = Steps[stepIndex],
                    enabled = enabled,
                )
            }
        }
        PlaygroundControls {
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.demo_stepper_min),
                    summary = "minValue",
                    items = MinValues.map { it.toString() },
                    selectedIndex = minIndex,
                    onSelectedIndexChange = { minIndex = it },
                )
            }
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.demo_stepper_max),
                    summary = "maxValue",
                    items = MaxValues.map { it.toString() },
                    selectedIndex = maxIndex,
                    onSelectedIndexChange = { maxIndex = it },
                )
            }
            item {
                OverlayDropdownPreference(
                    title = stringResource(Res.string.common_steps),
                    summary = "step",
                    items = Steps.map { it.toString() },
                    selectedIndex = stepIndex,
                    onSelectedIndexChange = { stepIndex = it },
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
    variant(key = "rows", title = Res.string.demo_stepper_variant_rows) {
        CardListGroup {
            item {
                var basicValue by remember { mutableIntStateOf(2) }
                BasicComponent(
                    title = stringResource(Res.string.demo_stepper_basic),
                    summary = stringResource(Res.string.demo_stepper_basic_summary),
                    endActions = {
                        Stepper(
                            value = basicValue,
                            onValueChange = { basicValue = it },
                            minValue = 0,
                            maxValue = 10,
                        )
                    },
                )
            }
            // The COUI default range (-999..9999): hold a button to repeat every 100ms.
            item {
                var wideValue by remember { mutableIntStateOf(0) }
                BasicComponent(
                    title = stringResource(Res.string.demo_stepper_hold),
                    summary = stringResource(Res.string.demo_stepper_hold_summary),
                    endActions = {
                        Stepper(
                            value = wideValue,
                            onValueChange = { wideValue = it },
                        )
                    },
                )
            }
            item {
                BasicComponent(
                    title = stringResource(Res.string.common_disabled),
                    endActions = {
                        Stepper(
                            value = 3,
                            onValueChange = {},
                            enabled = false,
                        )
                    },
                    enabled = false,
                )
            }
        }
    }
    // A stepper driving a badge count with a large step.
    variant(key = "badge", title = Res.string.demo_stepper_variant_badge) {
        var badgeCount by remember { mutableIntStateOf(8) }
        BasicComponent(
            title = stringResource(Res.string.sample_notifications),
            summary = stringResource(Res.string.demo_stepper_badge_summary),
            startAction = {
                BadgeBox(
                    badge = { Badge(count = badgeCount) },
                    overhang = BadgeDefaults.CountOverhang,
                ) {
                    Icon(
                        imageVector = COUIIcons.Settings,
                        contentDescription = stringResource(Res.string.sample_settings),
                        modifier = Modifier.size(32.dp),
                    )
                }
            },
            endActions = {
                Stepper(
                    value = badgeCount,
                    onValueChange = { badgeCount = it },
                    minValue = 1,
                    maxValue = 1000,
                    step = 111,
                )
            },
        )
    }
}
