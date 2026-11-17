// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import io.github.suqi8.coui.kmp.basic.InstallLoadProgress
import io.github.suqi8.coui.kmp.basic.InstallLoadState
import io.github.suqi8.coui.kmp.basic.InstallLoadStyle
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_progress
import io.github.suqi8.coui.kmp.shared.generated.resources.common_retry
import io.github.suqi8.coui.kmp.shared.generated.resources.common_style
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_install_load_progress_auto_advance
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_install_load_progress_download
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_install_load_progress_get
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_install_load_progress_interactive_hint
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_install_load_progress_resume
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_install_load_progress_state
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_install_load_progress_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_install_load_progress_variant_interactive
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_install_load_progress_variant_states
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

internal val InstallLoadProgressDemo = Demo(
    id = "install_load_progress",
    apiName = "InstallLoadProgress",
    title = Res.string.demo_install_load_progress_title,
    category = DemoCategory.Feedback,
    page = DemoPage.Variants {
        playground { InstallLoadProgressPlayground() }
        variant(key = "interactive", title = Res.string.demo_install_load_progress_variant_interactive) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(Res.string.demo_install_load_progress_interactive_hint),
                    color = COUITheme.colorScheme.labelSecondaryVariant,
                    style = COUITheme.textStyles.bodyM,
                )
                InteractiveInstallLoadProgress(style = InstallLoadStyle.Small)
                InteractiveInstallLoadProgress(style = InstallLoadStyle.Large)
                InteractiveInstallLoadProgress(style = InstallLoadStyle.Circle)
            }
        }
        variant(key = "states", title = Res.string.demo_install_load_progress_variant_states) {
            val get = stringResource(Res.string.demo_install_load_progress_get)
            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                InstallLoadProgress(text = get, onClick = {})
                InstallLoadProgress(text = "40%", onClick = {}, state = InstallLoadState.Paused, progress = 0.4f)
                InstallLoadProgress(text = stringResource(Res.string.common_retry), onClick = {}, state = InstallLoadState.Failed)
                InstallLoadProgress(text = get, onClick = {}, enabled = false)
            }
        }
    },
    keywords = listOf("download", "下载", "安装", "install"),
)

/** The button label COUI apps show for [state] at [progress]. */
@Composable
private fun installLabel(state: InstallLoadState, progress: Float): String = when (state) {
    InstallLoadState.Idle -> stringResource(Res.string.demo_install_load_progress_download)
    InstallLoadState.Loading -> "${(progress * 100).toInt()}%"
    InstallLoadState.Paused -> stringResource(Res.string.demo_install_load_progress_resume)
    InstallLoadState.Failed -> stringResource(Res.string.common_retry)
}

@Composable
private fun ColumnScope.InstallLoadProgressPlayground() {
    var styleIndex by rememberSaveable { mutableIntStateOf(0) }
    var stateIndex by rememberSaveable { mutableIntStateOf(0) }
    var progress by rememberSaveable { mutableFloatStateOf(0.3f) }
    var enabled by rememberSaveable { mutableStateOf(true) }
    var autoAdvance by rememberSaveable { mutableStateOf(true) }

    val state = InstallLoadState.entries[stateIndex]
    LaunchedEffect(state, autoAdvance) {
        while (autoAdvance && state == InstallLoadState.Loading) {
            delay(400)
            progress = (progress + 0.05f).coerceAtMost(1f)
            if (progress >= 1f) {
                stateIndex = InstallLoadState.Idle.ordinal
                progress = 0f
            }
        }
    }
    PlaygroundPreview {
        Box(
            modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp),
            contentAlignment = Alignment.Center,
        ) {
            InstallLoadProgress(
                text = installLabel(state, progress),
                onClick = {
                    stateIndex = if (state == InstallLoadState.Loading) {
                        InstallLoadState.Paused.ordinal
                    } else {
                        InstallLoadState.Loading.ordinal
                    }
                },
                state = state,
                progress = progress,
                enabled = enabled,
                style = InstallLoadStyle.entries[styleIndex],
            )
        }
    }
    val styleTitle = stringResource(Res.string.common_style)
    val stateTitle = stringResource(Res.string.demo_install_load_progress_state)
    val progressTitle = stringResource(Res.string.common_progress)
    val enabledTitle = stringResource(Res.string.common_enabled)
    val autoTitle = stringResource(Res.string.demo_install_load_progress_auto_advance)
    PlaygroundControls {
        item {
            OverlayDropdownPreference(
                items = InstallLoadStyle.entries.map { it.name },
                selectedIndex = styleIndex,
                title = styleTitle,
                summary = "InstallLoadStyle",
                onSelectedIndexChange = { styleIndex = it },
            )
        }
        item {
            OverlayDropdownPreference(
                items = InstallLoadState.entries.map { it.name },
                selectedIndex = stateIndex,
                title = stateTitle,
                summary = "InstallLoadState",
                onSelectedIndexChange = { stateIndex = it },
            )
        }
        item {
            SliderPreference(
                value = progress,
                onValueChange = { progress = it },
                title = progressTitle,
                valueText = "${(progress * 100).toInt()}%",
            )
        }
        item {
            SwitchPreference(checked = enabled, onCheckedChange = { enabled = it }, title = enabledTitle)
        }
        item {
            SwitchPreference(checked = autoAdvance, onCheckedChange = { autoAdvance = it }, title = autoTitle)
        }
    }
}

/** Idle -> Loading (progress ticks up) -> tap pauses / resumes -> Idle when complete. */
@Composable
private fun InteractiveInstallLoadProgress(
    style: InstallLoadStyle,
    modifier: Modifier = Modifier,
) {
    var state by rememberSaveable { mutableStateOf(InstallLoadState.Idle) }
    var progress by rememberSaveable { mutableFloatStateOf(0f) }
    LaunchedEffect(state) {
        while (state == InstallLoadState.Loading) {
            delay(400)
            progress = (progress + 0.1f).coerceAtMost(1f)
            if (progress >= 1f) {
                state = InstallLoadState.Idle
                progress = 0f
            }
        }
    }
    InstallLoadProgress(
        text = installLabel(state, progress),
        onClick = {
            state = if (state == InstallLoadState.Loading) InstallLoadState.Paused else InstallLoadState.Loading
        },
        modifier = modifier,
        state = state,
        progress = progress,
        style = style,
    )
}
