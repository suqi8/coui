// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.BasicComponentDefaults
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.IconButton
import io.github.suqi8.coui.kmp.basic.SideNavigationBar
import io.github.suqi8.coui.kmp.basic.SideNavigationBarMode
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.rememberSideNavigationBarState
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.Sidebar
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.launch

private val SideNavigationDemoPages = listOf("Home", "Themes", "Wallpapers", "Fonts", "Icons", "Me")

fun LazyListScope.sideNavigationBarSection() {
    item(key = "sideNavigationBar") {
        SmallTitle(text = "SideNavigationBar")
        var parentChildHierarchy by remember { mutableStateOf(false) }
        var editMode by remember { mutableStateOf(false) }
        Card(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp),
        ) {
            SideNavigationBarDemo(parentChildHierarchy = parentChildHierarchy, editMode = editMode)
        }
        Card(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp),
        ) {
            SwitchPreference(
                title = "Parent-child hierarchy",
                summary = "Only dock on large layouts",
                checked = parentChildHierarchy,
                onCheckedChange = { parentChildHierarchy = it },
            )
            SwitchPreference(
                title = "Edit mode",
                summary = "Masks the content beside a docked drawer",
                checked = editMode,
                onCheckedChange = { editMode = it },
            )
        }
    }
}

@Composable
private fun SideNavigationBarDemo(parentChildHierarchy: Boolean, editMode: Boolean) {
    val state = rememberSideNavigationBarState(initialOpen = true)
    val scope = rememberCoroutineScope()
    var selected by remember { mutableIntStateOf(0) }
    SideNavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp),
        state = state,
        parentChildHierarchy = parentChildHierarchy,
        editMode = editMode,
        drawerContent = {
            Column(Modifier.fillMaxSize().padding(top = 16.dp)) {
                SideNavigationDemoPages.forEachIndexed { index, page ->
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(COUITheme.colorScheme.surface)
                .padding(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { scope.launch { state.toggle() } }) {
                    Icon(imageVector = COUIIcons.Basic.Sidebar, contentDescription = "Toggle navigation")
                }
                Text(
                    text = SideNavigationDemoPages[selected],
                    modifier = Modifier.padding(start = 8.dp),
                    style = COUITheme.textStyles.title3,
                )
            }
            Text(
                text = when (state.mode) {
                    SideNavigationBarMode.Docked -> "Docked: the content makes room for the drawer."
                    SideNavigationBarMode.Overlay -> "Overlay: the drawer slides over the content; tap the mask to close it."
                },
                modifier = Modifier.padding(start = 4.dp, top = 12.dp),
                style = COUITheme.textStyles.body2,
                color = COUITheme.colorScheme.onSurfaceSecondary,
            )
        }
    }
}
