// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import io.github.suqi8.coui.kmp.basic.InstallLoadProgress
import io.github.suqi8.coui.kmp.basic.InstallLoadState
import io.github.suqi8.coui.kmp.basic.InstallLoadStyle
import kotlinx.coroutines.delay

@Composable
fun InstallLoadProgressDemo() {
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
            InteractiveInstallLoadProgress(style = InstallLoadStyle.Small)
            InteractiveInstallLoadProgress(style = InstallLoadStyle.Large)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                InstallLoadProgress(text = "Get", onClick = {})
                InstallLoadProgress(text = "40%", onClick = {}, state = InstallLoadState.Paused, progress = 0.4f)
                InstallLoadProgress(text = "Retry", onClick = {}, state = InstallLoadState.Failed)
                InstallLoadProgress(text = "Get", onClick = {}, enabled = false)
            }
        }
    }
}

@Composable
private fun InteractiveInstallLoadProgress(style: InstallLoadStyle) {
    var state by remember { mutableStateOf(InstallLoadState.Idle) }
    var progress by remember { mutableFloatStateOf(0f) }
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
        text = when (state) {
            InstallLoadState.Idle -> "Download"
            InstallLoadState.Paused -> "Resume"
            else -> "${(progress * 100).toInt()}%"
        },
        onClick = {
            state = if (state == InstallLoadState.Loading) InstallLoadState.Paused else InstallLoadState.Loading
        },
        state = state,
        progress = progress,
        style = style,
    )
}
