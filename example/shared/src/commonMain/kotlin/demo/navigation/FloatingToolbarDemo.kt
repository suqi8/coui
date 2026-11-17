// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.FloatingToolbar
import io.github.suqi8.coui.kmp.basic.FloatingToolbarGroupArrangement
import io.github.suqi8.coui.kmp.basic.FloatingToolbarItem
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.IconButton
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.basic.ToolbarPosition
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Delete
import io.github.suqi8.coui.kmp.icon.extended.Edit
import io.github.suqi8.coui.kmp.icon.extended.Link
import io.github.suqi8.coui.kmp.icon.extended.More
import io.github.suqi8.coui.kmp.icon.extended.Search
import io.github.suqi8.coui.kmp.icon.extended.Share
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_clicked
import io.github.suqi8.coui.kmp.shared.generated.resources.common_count
import io.github.suqi8.coui.kmp.shared.generated.resources.common_delete
import io.github.suqi8.coui.kmp.shared.generated.resources.common_done
import io.github.suqi8.coui.kmp.shared.generated.resources.common_edit
import io.github.suqi8.coui.kmp.shared.generated.resources.common_hide
import io.github.suqi8.coui.kmp.shared.generated.resources.common_horizontal
import io.github.suqi8.coui.kmp.shared.generated.resources.common_material
import io.github.suqi8.coui.kmp.shared.generated.resources.common_more
import io.github.suqi8.coui.kmp.shared.generated.resources.common_orientation
import io.github.suqi8.coui.kmp.shared.generated.resources.common_position
import io.github.suqi8.coui.kmp.shared.generated.resources.common_search
import io.github.suqi8.coui.kmp.shared.generated.resources.common_share
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show_divider
import io.github.suqi8.coui.kmp.shared.generated.resources.common_vertical
import io.github.suqi8.coui.kmp.shared.generated.resources.common_visible
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_toolbar_corner_radius
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_toolbar_item_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_toolbar_items_count
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_toolbar_none
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_toolbar_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_toolbar_variant_items
import io.github.suqi8.coui.kmp.theme.COUITheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import ui.AppPageList
import ui.AppPageScaffold

internal val FloatingToolbarDemo = Demo(
    id = "floating_toolbar",
    apiName = "FloatingToolbar",
    title = Res.string.demo_floating_toolbar_title,
    category = DemoCategory.Navigation,
    page = DemoPage.FullScreen { onBack -> FloatingToolbarDemoPage(onBack = onBack) },
    keywords = listOf("toolbar", "工具栏", "ToolbarPosition"),
    isNew = true,
)

/** The [ToolbarPosition] choices, matching [ToolbarPositionNames]. */
private val ToolbarPositions = listOf(
    ToolbarPosition.TopStart,
    ToolbarPosition.CenterStart,
    ToolbarPosition.BottomStart,
    ToolbarPosition.TopEnd,
    ToolbarPosition.CenterEnd,
    ToolbarPosition.BottomEnd,
    ToolbarPosition.TopCenter,
    ToolbarPosition.BottomCenter,
)

private val ToolbarPositionNames = listOf(
    "TopStart",
    "CenterStart",
    "BottomStart",
    "TopEnd",
    "CenterEnd",
    "BottomEnd",
    "TopCenter",
    "BottomCenter",
)

private val ToolbarDemoIcons: List<ImageVector> = listOf(COUIIcons.Edit, COUIIcons.Delete, COUIIcons.Share, COUIIcons.More)
private val ToolbarDemoLabels: List<StringResource> = listOf(
    Res.string.common_edit,
    Res.string.common_delete,
    Res.string.common_share,
    Res.string.common_more,
)
private val ToolbarDemoCountOptions = listOf("1", "2", "3", "4")

/**
 * The content floating toolbar in the page's floating toolbar slot, where the scaffold hands it
 * the page backdrop so its material blurs the page. The list below also shows the item toolbar.
 */
@Composable
private fun FloatingToolbarDemoPage(onBack: () -> Unit) {
    var visible by rememberSaveable { mutableStateOf(true) }
    var positionIndex by rememberSaveable { mutableIntStateOf(ToolbarPositions.lastIndex) }
    var vertical by rememberSaveable { mutableStateOf(false) }
    var countIndex by rememberSaveable { mutableIntStateOf(2) }
    var cornerRadius by rememberSaveable { mutableFloatStateOf(20f) }
    var material by rememberSaveable { mutableStateOf(true) }
    var showDivider by rememberSaveable { mutableStateOf(false) }
    var lastAction by rememberSaveable { mutableIntStateOf(-1) }

    AppPageScaffold(
        title = stringResource(Res.string.demo_floating_toolbar_title),
        subtitle = "FloatingToolbar",
        onBack = onBack,
        floatingToolbar = {
            // The toolbar runs its own COUI 17 show / hide springs, which keep its shadow whole.
            FloatingToolbar(
                visible = visible,
                materialEffect = material,
                cornerRadius = cornerRadius.dp,
                showDivider = showDivider,
            ) {
                // A size change without a clip or a fade: both would cut the icon buttons' shadows off.
                AnimatedContent(
                    targetState = vertical,
                    transitionSpec = { (EnterTransition.None togetherWith ExitTransition.None).using(SizeTransform(clip = false)) },
                ) { isVertical ->
                    val itemCount = countIndex + 1
                    val buttons = @Composable {
                        repeat(itemCount) { index ->
                            IconButton(onClick = { lastAction = index }) {
                                Icon(
                                    imageVector = ToolbarDemoIcons[index],
                                    contentDescription = stringResource(ToolbarDemoLabels[index]),
                                    tint = COUITheme.colorScheme.labelPrimary,
                                )
                            }
                        }
                    }
                    if (isVertical) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) { buttons() }
                    } else {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) { buttons() }
                    }
                }
            }
        },
        floatingToolbarPosition = ToolbarPositions[positionIndex],
    ) { scope ->
        AppPageList(scope = scope) {
            item(key = "last_action") {
                BasicComponent(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = stringResource(
                        Res.string.common_clicked,
                        if (lastAction >= 0) stringResource(ToolbarDemoLabels[lastAction]) else stringResource(Res.string.demo_floating_toolbar_none),
                    ),
                )
            }
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
                            items = ToolbarPositionNames,
                            selectedIndex = positionIndex,
                            title = stringResource(Res.string.common_position),
                            onSelectedIndexChange = { positionIndex = it },
                        )
                    }
                    item {
                        OverlayDropdownPreference(
                            items = listOf(
                                stringResource(Res.string.common_horizontal),
                                stringResource(Res.string.common_vertical),
                            ),
                            selectedIndex = if (vertical) 1 else 0,
                            title = stringResource(Res.string.common_orientation),
                            onSelectedIndexChange = { vertical = it == 1 },
                        )
                    }
                    item {
                        OverlayDropdownPreference(
                            items = ToolbarDemoCountOptions,
                            selectedIndex = countIndex,
                            title = stringResource(Res.string.common_count),
                            onSelectedIndexChange = { countIndex = it },
                        )
                    }
                    item {
                        SliderPreference(
                            value = cornerRadius,
                            onValueChange = { cornerRadius = it },
                            title = stringResource(Res.string.demo_floating_toolbar_corner_radius),
                            valueText = "${cornerRadius.toInt()}dp",
                            valueRange = 0f..32f,
                            steps = 31,
                        )
                    }
                    item {
                        SwitchPreference(
                            title = stringResource(Res.string.common_material),
                            checked = material,
                            onCheckedChange = { material = it },
                        )
                    }
                    item {
                        SwitchPreference(
                            title = stringResource(Res.string.common_show_divider),
                            checked = showDivider,
                            onCheckedChange = { showDivider = it },
                        )
                    }
                }
            }
            variant(key = "items_toolbar", title = Res.string.demo_floating_toolbar_variant_items) {
                ItemToolbarSample()
            }
            navigationFillerContent(rows = 12)
        }
    }
}

private val ItemToolbarIcons: List<ImageVector> = listOf(COUIIcons.Edit, COUIIcons.Delete, COUIIcons.Link, COUIIcons.Search)
private val ItemToolbarNames: List<StringResource> = listOf(
    Res.string.common_edit,
    Res.string.common_delete,
    Res.string.common_share,
    Res.string.common_search,
)

private val ItemToolbarDone = 100

/**
 * The item toolbar: the first group grows past what fits so the overflow item appears; the second
 * group is a single title item.
 */
@Composable
private fun ItemToolbarSample(modifier: Modifier = Modifier) {
    // The last clicked item: its index, ItemToolbarDone for the title item, -1 for none.
    var lastAction by rememberSaveable { mutableIntStateOf(-1) }
    var itemCount by rememberSaveable { mutableIntStateOf(3) }
    var packed by rememberSaveable { mutableStateOf(false) }
    var visible by rememberSaveable { mutableStateOf(true) }
    val names = ItemToolbarNames.map { stringResource(it) }
    val titles = List(itemCount) { index ->
        stringResource(Res.string.demo_floating_toolbar_item_title, names[index % names.size], index + 1)
    }
    val done = stringResource(Res.string.common_done)
    val items = List(itemCount) { index ->
        FloatingToolbarItem(
            onClick = { lastAction = index },
            icon = ItemToolbarIcons[index % ItemToolbarIcons.size],
            title = titles[index],
            showRedDot = index == 1,
            enabled = index != 2,
        )
    } + FloatingToolbarItem(onClick = { lastAction = ItemToolbarDone }, title = done, group = 1)
    val lastActionText = when (lastAction) {
        -1 -> "-"
        ItemToolbarDone -> done
        else -> titles.getOrElse(lastAction) { "-" }
    }
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(Res.string.common_clicked, lastActionText),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            color = COUITheme.colorScheme.labelSecondary,
        )
        FloatingToolbar(
            items = items,
            visible = visible,
            defaultWindowInsetsPadding = false,
            groupArrangement = if (packed) FloatingToolbarGroupArrangement.Packed else FloatingToolbarGroupArrangement.Spread,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TextButton(
                text = stringResource(Res.string.demo_floating_toolbar_items_count, itemCount),
                onClick = { itemCount = if (itemCount >= 9) 1 else itemCount + 2 },
                modifier = Modifier.weight(1f),
            )
            TextButton(
                text = if (packed) "Packed" else "Spread",
                onClick = { packed = !packed },
                modifier = Modifier.weight(1f),
            )
            TextButton(
                text = stringResource(if (visible) Res.string.common_hide else Res.string.common_show),
                onClick = { visible = !visible },
                modifier = Modifier.weight(1f),
            )
        }
    }
}
