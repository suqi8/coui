// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
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
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_large
import io.github.suqi8.coui.kmp.shared.generated.resources.common_medium
import io.github.suqi8.coui.kmp.shared.generated.resources.common_small
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_navigation_bar_docked
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_navigation_bar_edit_mask
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_navigation_bar_edit_mode
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_navigation_bar_edit_mode_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_navigation_bar_height
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_navigation_bar_overlay
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_navigation_bar_page_fonts
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_navigation_bar_page_icons
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_navigation_bar_page_themes
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_navigation_bar_page_wallpapers
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_navigation_bar_parent_child
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_navigation_bar_parent_child_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_navigation_bar_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_side_navigation_bar_toggle
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_home
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_profile
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

internal val SideNavigationBarDemo = Demo(
    id = "side_navigation_bar",
    apiName = "SideNavigationBar",
    title = Res.string.demo_side_navigation_bar_title,
    category = DemoCategory.Navigation,
    page = DemoPage.Variants {
        playground {
            var parentChildHierarchy by rememberSaveable { mutableStateOf(false) }
            var editMode by rememberSaveable { mutableStateOf(false) }
            var editModeMask by rememberSaveable { mutableStateOf(true) }
            var heightIndex by rememberSaveable { mutableIntStateOf(1) }
            PlaygroundPreview(contentPadding = PaddingValues(0.dp)) {
                SideNavigationBarSample(
                    parentChildHierarchy = parentChildHierarchy,
                    editMode = editMode,
                    editModeMask = editModeMask,
                    height = SideNavigationDemoHeights[heightIndex],
                )
            }
            PlaygroundControls {
                item {
                    SwitchPreference(
                        title = stringResource(Res.string.demo_side_navigation_bar_parent_child),
                        summary = stringResource(Res.string.demo_side_navigation_bar_parent_child_summary),
                        checked = parentChildHierarchy,
                        onCheckedChange = { parentChildHierarchy = it },
                    )
                }
                item {
                    SwitchPreference(
                        title = stringResource(Res.string.demo_side_navigation_bar_edit_mode),
                        summary = stringResource(Res.string.demo_side_navigation_bar_edit_mode_summary),
                        checked = editMode,
                        onCheckedChange = { editMode = it },
                    )
                }
                item {
                    SwitchPreference(
                        title = stringResource(Res.string.demo_side_navigation_bar_edit_mask),
                        checked = editModeMask,
                        onCheckedChange = { editModeMask = it },
                    )
                }
                item {
                    OverlayDropdownPreference(
                        items = listOf(
                            stringResource(Res.string.common_small),
                            stringResource(Res.string.common_medium),
                            stringResource(Res.string.common_large),
                        ),
                        selectedIndex = heightIndex,
                        title = stringResource(Res.string.demo_side_navigation_bar_height),
                        onSelectedIndexChange = { heightIndex = it },
                    )
                }
            }
        }
    },
    keywords = listOf("sidebar", "drawer", "抽屉", "侧边栏"),
)

private val SideNavigationDemoHeights: List<Dp> = listOf(320.dp, 420.dp, 560.dp)

private val SideNavigationDemoPages: List<StringResource> = listOf(
    Res.string.sample_home,
    Res.string.demo_side_navigation_bar_page_themes,
    Res.string.demo_side_navigation_bar_page_wallpapers,
    Res.string.demo_side_navigation_bar_page_fonts,
    Res.string.demo_side_navigation_bar_page_icons,
    Res.string.sample_profile,
)

@Composable
private fun SideNavigationBarSample(
    parentChildHierarchy: Boolean,
    editMode: Boolean,
    editModeMask: Boolean,
    height: Dp,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
    ) {
        // Start open only where the drawer docks; as an overlay it would cover the whole card.
        val docked = SideNavigationBarDefaults.drawerMode(maxWidth, maxHeight, parentChildHierarchy) == SideNavigationBarMode.Docked
        val state = rememberSideNavigationBarState(initialOpen = docked)
        SideNavigationBarSampleContent(
            state = state,
            parentChildHierarchy = parentChildHierarchy,
            editMode = editMode,
            editModeMask = editModeMask,
        )
    }
}

@Composable
private fun SideNavigationBarSampleContent(
    state: SideNavigationBarState,
    parentChildHierarchy: Boolean,
    editMode: Boolean,
    editModeMask: Boolean,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    var selected by rememberSaveable { mutableIntStateOf(0) }
    SideNavigationBar(
        modifier = modifier.fillMaxSize(),
        state = state,
        parentChildHierarchy = parentChildHierarchy,
        editMode = editMode,
        editModeMask = editModeMask,
        drawerContent = {
            Column(Modifier.fillMaxSize().padding(top = 16.dp)) {
                SideNavigationDemoPages.forEachIndexed { index, page ->
                    BasicComponent(
                        title = stringResource(page),
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
                .background(COUITheme.colorScheme.backgroundWithCard)
                .padding(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { scope.launch { state.toggle() } }) {
                    Icon(
                        imageVector = COUIIcons.Basic.Sidebar,
                        contentDescription = stringResource(Res.string.demo_side_navigation_bar_toggle),
                    )
                }
                Text(
                    text = stringResource(SideNavigationDemoPages[selected]),
                    modifier = Modifier.padding(start = 8.dp),
                    style = COUITheme.textStyles.headlineM,
                )
            }
            Text(
                text = stringResource(
                    when (state.mode) {
                        SideNavigationBarMode.Docked -> Res.string.demo_side_navigation_bar_docked
                        SideNavigationBarMode.Overlay -> Res.string.demo_side_navigation_bar_overlay
                    },
                ),
                modifier = Modifier.padding(start = 4.dp, top = 12.dp),
                style = COUITheme.textStyles.bodyM,
                color = COUITheme.colorScheme.labelSecondary,
            )
        }
    }
}
