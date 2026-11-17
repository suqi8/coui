// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import io.github.suqi8.coui.kmp.basic.CompleteState
import io.github.suqi8.coui.kmp.basic.CompleteStateType
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.preference.ArrowPreference
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_long_text
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_complete_state_animate
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_complete_state_done
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_complete_state_failed
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_complete_state_order
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_complete_state_replay
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_complete_state_success
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_complete_state_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_complete_state_type
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_complete_state_variant_types
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_complete_state_waiting
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_long_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_long_title
import org.jetbrains.compose.resources.stringResource

internal val CompleteStateDemo = Demo(
    id = "complete_state",
    apiName = "CompleteState",
    title = Res.string.demo_complete_state_title,
    category = DemoCategory.Feedback,
    page = DemoPage.Variants {
        playground { CompleteStatePlayground() }
        variant(key = "types", title = Res.string.demo_complete_state_variant_types) {
            CompleteStateTypesContent()
        }
    },
    keywords = listOf("success", "完成", "成功", "失败", "result"),
)

private const val SAMPLE_ORDER_NUMBER = "2026103000001"

/** The sample title of a completion of [type]. */
@Composable
private fun completeTitle(type: CompleteStateType): String = stringResource(
    when (type) {
        CompleteStateType.None -> Res.string.demo_complete_state_done
        CompleteStateType.Success -> Res.string.demo_complete_state_success
        CompleteStateType.Failed -> Res.string.demo_complete_state_failed
        CompleteStateType.Waiting -> Res.string.demo_complete_state_waiting
    },
)

@Composable
private fun ColumnScope.CompleteStatePlayground() {
    var typeIndex by rememberSaveable { mutableIntStateOf(1) }
    var showSubtitle by rememberSaveable { mutableStateOf(true) }
    var animate by rememberSaveable { mutableStateOf(true) }
    var longText by rememberSaveable { mutableStateOf(false) }
    var replay by rememberSaveable { mutableIntStateOf(0) }

    val type = CompleteStateType.entries[typeIndex]
    val longTitle = stringResource(Res.string.sample_long_title)
    val title = if (longText) longTitle else completeTitle(type)
    val subtitle = when {
        !showSubtitle -> null
        longText -> stringResource(Res.string.sample_long_summary)
        else -> stringResource(Res.string.demo_complete_state_order, SAMPLE_ORDER_NUMBER)
    }
    PlaygroundPreview {
        CompleteState(
            title = title,
            modifier = Modifier.padding(vertical = 8.dp),
            subtitle = subtitle,
            type = type,
            animate = animate,
            replayKey = replay,
        )
    }
    val typeTitle = stringResource(Res.string.demo_complete_state_type)
    val subtitleTitle = stringResource(Res.string.common_show_summary)
    val animateTitle = stringResource(Res.string.demo_complete_state_animate)
    val longTextTitle = stringResource(Res.string.common_long_text)
    val replayTitle = stringResource(Res.string.demo_complete_state_replay)
    PlaygroundControls {
        item {
            OverlayDropdownPreference(
                items = CompleteStateType.entries.map { it.name },
                selectedIndex = typeIndex,
                title = typeTitle,
                summary = "CompleteStateType",
                onSelectedIndexChange = { typeIndex = it },
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
                checked = animate,
                onCheckedChange = { animate = it },
                title = animateTitle,
                summary = "animate",
            )
        }
        item {
            SwitchPreference(checked = longText, onCheckedChange = { longText = it }, title = longTextTitle)
        }
        item {
            ArrowPreference(
                title = replayTitle,
                summary = "replayKey",
                onClick = { replay++ },
                enabled = animate,
            )
        }
    }
}

/** One button per type; tapping the current type again replays its animation. */
@Composable
private fun CompleteStateTypesContent() {
    var type by rememberSaveable { mutableStateOf(CompleteStateType.Success) }
    var replay by rememberSaveable { mutableIntStateOf(0) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        CompleteState(
            title = completeTitle(type),
            subtitle = stringResource(Res.string.demo_complete_state_order, SAMPLE_ORDER_NUMBER),
            type = type,
            replayKey = replay,
        )
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CompleteStateType.entries.forEach { entry ->
                TextButton(
                    text = entry.name,
                    onClick = {
                        if (type == entry) replay++ else type = entry
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
