// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.BasicComponentDefaults
import io.github.suqi8.coui.kmp.basic.SidePaneLayout
import io.github.suqi8.coui.kmp.basic.SidePaneLayoutDefaults
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.rememberSidePaneState
import io.github.suqi8.coui.kmp.theme.COUITheme

private val SidePaneFolders = listOf("All notes", "Recently edited", "Favourites", "Shared", "Recently deleted")

@Composable
fun SidePaneLayoutDemo() {
    val windowHeight = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.height.toDp() }
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(demoBackground()),
    ) {
        val split = SidePaneLayoutDefaults.isSplitMode(maxWidth, windowHeight)
        val state = rememberSidePaneState(initialOpen = split)
        var selected by remember { mutableIntStateOf(0) }
        SidePaneLayout(
            state = state,
            coverStyle = !split,
            pane = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(COUITheme.colorScheme.surfaceContainer)
                        .padding(top = 104.dp),
                ) {
                    SidePaneFolders.forEachIndexed { index, folder ->
                        BasicComponent(
                            title = folder,
                            titleColor = if (index == selected) {
                                BasicComponentDefaults.titleColor(color = COUITheme.colorScheme.primary)
                            } else {
                                BasicComponentDefaults.titleColor()
                            },
                            onClick = { selected = index },
                        )
                    }
                }
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(COUITheme.colorScheme.surface)
                    .padding(start = 72.dp, top = 56.dp, end = 16.dp),
            ) {
                Text(text = SidePaneFolders[selected], style = COUITheme.textStyles.title3)
                Text(
                    text = if (split) "Split mode: the content resizes with the pane." else "Compact: the pane slides over the content.",
                    modifier = Modifier.padding(top = 8.dp),
                    style = COUITheme.textStyles.body2,
                    color = COUITheme.colorScheme.onSurfaceSecondary,
                )
            }
        }
    }
}
