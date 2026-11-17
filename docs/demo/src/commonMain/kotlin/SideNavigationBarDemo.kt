// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.BasicComponentDefaults
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.IconButton
import io.github.suqi8.coui.kmp.basic.SideNavigationBar
import io.github.suqi8.coui.kmp.basic.SideNavigationBarDefaults
import io.github.suqi8.coui.kmp.basic.SideNavigationBarMode
import io.github.suqi8.coui.kmp.basic.SideNavigationBarState
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.rememberSideNavigationBarState
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.Sidebar
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.launch

private val SideNavigationPages = listOf("Home", "Themes", "Wallpapers", "Fonts", "Icons", "Me")

@Composable
fun SideNavigationBarDemo() {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        // Start open only where the drawer docks; as an overlay it would cover the content.
        val docked = SideNavigationBarDefaults.drawerMode(maxWidth, maxHeight) == SideNavigationBarMode.Docked
        SideNavigationBarDemoContent(rememberSideNavigationBarState(initialOpen = docked))
    }
}

@Composable
private fun SideNavigationBarDemoContent(state: SideNavigationBarState) {
    val scope = rememberCoroutineScope()
    var selected by remember { mutableIntStateOf(0) }
    SideNavigationBar(
        modifier = Modifier
            .fillMaxSize()
            .background(demoBackground()),
        state = state,
        drawerContent = {
            Column(Modifier.fillMaxSize().padding(top = 16.dp)) {
                SideNavigationPages.forEachIndexed { index, page ->
                    BasicComponent(
                        title = page,
                        titleColor = if (index == selected) {
                            BasicComponentDefaults.titleColor(color = COUITheme.colorScheme.primary)
                        } else {
                            BasicComponentDefaults.titleColor()
                        },
                        onClick = {
                            selected = index
                            if (state.mode == SideNavigationBarMode.Overlay) scope.launch { state.close() }
                        },
                    )
                }
            }
        },
    ) {
        Column(Modifier.fillMaxSize().padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { scope.launch { state.toggle() } }) {
                    Icon(imageVector = COUIIcons.Basic.Sidebar, contentDescription = "Toggle navigation")
                }
                Text(
                    text = SideNavigationPages[selected],
                    modifier = Modifier.padding(start = 8.dp),
                    style = COUITheme.textStyles.headlineM,
                )
            }
            Text(
                text = when (state.mode) {
                    SideNavigationBarMode.Docked -> "Docked: the content makes room for the drawer."
                    SideNavigationBarMode.Overlay -> "Overlay: the drawer slides over the content; tap the mask to close it."
                },
                modifier = Modifier.padding(start = 4.dp, top = 12.dp),
                style = COUITheme.textStyles.bodyM,
                color = COUITheme.colorScheme.labelSecondary,
            )
        }
    }
}
