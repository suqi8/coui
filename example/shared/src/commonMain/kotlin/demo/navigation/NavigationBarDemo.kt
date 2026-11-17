// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import io.github.suqi8.coui.kmp.basic.NavigationBar
import io.github.suqi8.coui.kmp.basic.NavigationBarDefaults
import io.github.suqi8.coui.kmp.basic.NavigationBarDisplayMode
import io.github.suqi8.coui.kmp.basic.NavigationBarItem
import io.github.suqi8.coui.kmp.blur.BlendColorEntry
import io.github.suqi8.coui.kmp.blur.BlurDefaults
import io.github.suqi8.coui.kmp.blur.textureBlur
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_count
import io.github.suqi8.coui.kmp.shared.generated.resources.common_mode
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show_divider
import io.github.suqi8.coui.kmp.shared.generated.resources.common_visible
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation_badges
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation_bar_blur
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation_bar_blur_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_navigation_bar_title
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.stringResource
import ui.AppPageList
import ui.AppPageScaffold
import utils.LocalPageBackdrop

internal val NavigationBarDemo = Demo(
    id = "navigation_bar",
    apiName = "NavigationBar",
    title = Res.string.demo_navigation_bar_title,
    category = DemoCategory.Navigation,
    page = DemoPage.FullScreen { onBack -> NavigationBarDemoPage(onBack = onBack) },
    keywords = listOf("tab", "底栏", "bottom bar", "tab bar"),
)

private val NavigationBarDemoModes = NavigationBarDisplayMode.entries
private val NavigationBarDemoModeNames = NavigationBarDemoModes.map { it.name }

/**
 * The classic COUI tab bar in the page's bottom bar slot, over a page that scrolls under it.
 */
@Composable
private fun NavigationBarDemoPage(onBack: () -> Unit) {
    var visible by rememberSaveable { mutableStateOf(true) }
    var modeIndex by rememberSaveable { mutableIntStateOf(0) }
    var countIndex by rememberSaveable { mutableIntStateOf(2) }
    var badges by rememberSaveable { mutableStateOf(true) }
    var showDivider by rememberSaveable { mutableStateOf(true) }
    var blur by rememberSaveable { mutableStateOf(true) }
    var selected by rememberSaveable { mutableIntStateOf(0) }
    val itemCount = countIndex + NavigationDemoMinCount

    AppPageScaffold(
        title = stringResource(Res.string.demo_navigation_bar_title),
        subtitle = "NavigationBar",
        onBack = onBack,
        bottomBar = {
            DemoNavigationBar(
                selected = selected.coerceAtMost(itemCount - 1),
                onSelect = { selected = it },
                itemCount = itemCount,
                visible = visible,
                mode = NavigationBarDemoModes[modeIndex],
                badges = badges,
                showDivider = showDivider,
                blur = blur,
            )
        },
    ) { scope ->
        AppPageList(scope = scope) {
            item(key = "controls") {
                PlaygroundControls {
                    item {
                        SwitchPreference(
                            title = stringResource(Res.string.common_visible),
                            checked = visible,
                            onCheckedChange = { visible = it },
                        )
                    }
                    item {
                        OverlayDropdownPreference(
                            items = NavigationBarDemoModeNames,
                            selectedIndex = modeIndex,
                            title = stringResource(Res.string.common_mode),
                            onSelectedIndexChange = { modeIndex = it },
                        )
                    }
                    item {
                        OverlayDropdownPreference(
                            items = NavigationDemoCountOptions,
                            selectedIndex = countIndex,
                            title = stringResource(Res.string.common_count),
                            onSelectedIndexChange = { countIndex = it },
                        )
                    }
                    item {
                        SwitchPreference(
                            title = stringResource(Res.string.demo_navigation_badges),
                            checked = badges,
                            onCheckedChange = { badges = it },
                        )
                    }
                    item {
                        SwitchPreference(
                            title = stringResource(Res.string.common_show_divider),
                            checked = showDivider,
                            onCheckedChange = { showDivider = it },
                        )
                    }
                    item {
                        SwitchPreference(
                            title = stringResource(Res.string.demo_navigation_bar_blur),
                            summary = stringResource(Res.string.demo_navigation_bar_blur_summary),
                            checked = blur,
                            onCheckedChange = { blur = it },
                        )
                    }
                }
            }
            navigationFillerContent()
        }
    }
}

/**
 * The tab bar of the demo. With [blur] on and a page backdrop provided (the material setting is on
 * and runtime shaders exist), the bar turns see-through over a 25px blur of the page tinted with
 * the page colour at 80%; otherwise it keeps COUI's opaque `?couiColorBar`.
 */
@Composable
private fun DemoNavigationBar(
    selected: Int,
    onSelect: (Int) -> Unit,
    itemCount: Int,
    visible: Boolean,
    mode: NavigationBarDisplayMode,
    badges: Boolean,
    showDivider: Boolean,
    blur: Boolean,
    modifier: Modifier = Modifier,
) {
    val backdrop = LocalPageBackdrop.current
    val blurActive = blur && backdrop != null
    val barColor = if (blurActive) Color.Transparent else NavigationBarDefaults.color()
    val blendColors = listOf(BlendColorEntry(color = COUITheme.colorScheme.backgroundWithCard.copy(alpha = 0.8f)))
    val blurColors = BlurDefaults.blurColors(blendColors = blendColors)
    // The bar shrinks its own layout while it hides, so the blurred box follows it.
    Box(
        modifier = modifier
            .then(
                if (blur && backdrop != null) {
                    Modifier.textureBlur(
                        backdrop = backdrop,
                        shape = RectangleShape,
                        blurRadius = 25f,
                        colors = blurColors,
                    )
                } else {
                    Modifier
                },
            )
            .background(barColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {},
            ),
    ) {
        NavigationBar(
            visible = visible,
            color = barColor,
            showDivider = showDivider,
            mode = mode,
        ) {
            repeat(itemCount) { index ->
                NavigationBarItem(
                    selected = selected == index,
                    onClick = { onSelect(index) },
                    icon = NavigationDemoIcons[index],
                    label = stringResource(NavigationDemoLabels[index]),
                    badge = when {
                        !badges -> null
                        index == 1 -> 0
                        index == 3 -> 8
                        else -> null
                    },
                )
            }
        }
    }
}
