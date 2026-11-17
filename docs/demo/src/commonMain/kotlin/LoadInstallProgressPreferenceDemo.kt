// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.InstallLoadState
import io.github.suqi8.coui.kmp.preference.LoadInstallProgressPreference
import kotlinx.coroutines.delay

@Composable
fun LoadInstallProgressPreferenceDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var state by remember { mutableStateOf(InstallLoadState.Idle) }
            var progress by remember { mutableFloatStateOf(0f) }
            LaunchedEffect(state) {
                while (state == InstallLoadState.Loading && progress < 1f) {
                    delay(120)
                    progress = (progress + 0.04f).coerceAtMost(1f)
                }
                if (progress >= 1f) {
                    state = InstallLoadState.Idle
                    progress = 0f
                }
            }
            val label = when (state) {
                InstallLoadState.Idle -> "Install"
                InstallLoadState.Loading -> "${(progress * 100).toInt()}%"
                InstallLoadState.Paused -> "Resume"
                InstallLoadState.Failed -> "Retry"
            }
            Card {
                CardListGroup {
                    item {
                        LoadInstallProgressPreference(
                            title = "Weather",
                            summary = "18.6 MB",
                            progressText = label,
                            state = state,
                            progress = progress,
                            onProgressClick = {
                                state = if (state == InstallLoadState.Loading) InstallLoadState.Paused else InstallLoadState.Loading
                            },
                        )
                    }
                    item {
                        LoadInstallProgressPreference(
                            title = "Calculator",
                            summary = "Download failed",
                            progressText = "Retry",
                            state = InstallLoadState.Failed,
                            onProgressClick = {},
                        )
                    }
                    item {
                        LoadInstallProgressPreference(
                            title = "Disabled",
                            progressText = "Install",
                            onProgressClick = {},
                            enabled = false,
                        )
                    }
                }
            }
        }
    }
}
