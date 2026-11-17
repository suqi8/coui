// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.ExpandableFloatingActionButton
import io.github.suqi8.coui.kmp.basic.FabPosition
import io.github.suqi8.coui.kmp.basic.FloatingActionButton
import io.github.suqi8.coui.kmp.basic.FloatingActionButtonDefaults
import io.github.suqi8.coui.kmp.basic.FloatingActionButtonItem
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.SnackbarHostState
import io.github.suqi8.coui.kmp.basic.rememberFloatingActionButtonScrollState
import io.github.suqi8.coui.kmp.effect.LocalCOUIMaterialEffectEnabled
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Add
import io.github.suqi8.coui.kmp.icon.extended.Edit
import io.github.suqi8.coui.kmp.icon.extended.Scan
import io.github.suqi8.coui.kmp.icon.extended.Share
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_add
import io.github.suqi8.coui.kmp.shared.generated.resources.common_center
import io.github.suqi8.coui.kmp.shared.generated.resources.common_color
import io.github.suqi8.coui.kmp.shared.generated.resources.common_default
import io.github.suqi8.coui.kmp.shared.generated.resources.common_edit
import io.github.suqi8.coui.kmp.shared.generated.resources.common_enabled
import io.github.suqi8.coui.kmp.shared.generated.resources.common_end
import io.github.suqi8.coui.kmp.shared.generated.resources.common_material
import io.github.suqi8.coui.kmp.shared.generated.resources.common_position
import io.github.suqi8.coui.kmp.shared.generated.resources.common_share
import io.github.suqi8.coui.kmp.shared.generated.resources.common_size
import io.github.suqi8.coui.kmp.shared.generated.resources.common_start
import io.github.suqi8.coui.kmp.shared.generated.resources.common_visible
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_action_button_clicked
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_action_button_color_primary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_action_button_color_secondary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_action_button_color_translucent
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_action_button_hide_on_scroll
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_action_button_hide_on_scroll_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_action_button_last_action
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_action_button_none
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_action_button_position_end_overlay
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_action_button_scan
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_action_button_shadow
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_action_button_size_tiny
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_action_button_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_action_button_type
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_action_button_type_expandable
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_action_button_type_single
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_action_button_variant_backdrop
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_action_button_variant_scroll
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_floating_action_button_variant_styles
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_item
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import ui.AppPageList
import ui.AppPageScaffold

internal val FloatingActionButtonDemo = Demo(
    id = "floating_action_button",
    apiName = "FloatingActionButton",
    title = Res.string.demo_floating_action_button_title,
    category = DemoCategory.Buttons,
    page = DemoPage.FullScreen { onBack -> FloatingActionButtonPage(onBack = onBack) },
    keywords = listOf("fab", "ExpandableFloatingActionButton", "speed dial", "悬浮", "展开"),
)

/** The [FabPosition] choices of the position parameter, in dropdown order. */
private val FabPositions = listOf(FabPosition.Start, FabPosition.Center, FabPosition.End, FabPosition.EndOverlay)

private const val POSITION_END = 2

private const val COLOR_PRIMARY = 0
private const val COLOR_SECONDARY = 1
private const val COLOR_TRANSLUCENT = 2

private const val NO_ACTION = -1

/** The fill, disabled fill and content colour of a floating action button colour preset. */
@Immutable
private data class FabColors(
    val container: Color,
    val disabledContainer: Color,
    val content: Color,
)

/**
 * The floating action button demo: the button lives in the page scaffold's real
 * `floatingActionButton` slot, the parameters are in the page list below.
 */
@Composable
private fun FloatingActionButtonPage(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val materialDefault = LocalCOUIMaterialEffectEnabled.current
    var visible by rememberSaveable { mutableStateOf(true) }
    var positionIndex by rememberSaveable { mutableIntStateOf(POSITION_END) }
    var expandable by rememberSaveable { mutableStateOf(false) }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var colorIndex by rememberSaveable { mutableIntStateOf(COLOR_PRIMARY) }
    var tiny by rememberSaveable { mutableStateOf(false) }
    var shadow by rememberSaveable { mutableStateOf(true) }
    var material by rememberSaveable { mutableStateOf(materialDefault) }
    var enabled by rememberSaveable { mutableStateOf(true) }
    var hideOnScroll by rememberSaveable { mutableStateOf(false) }
    var lastAction by rememberSaveable { mutableIntStateOf(NO_ACTION) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberFloatingActionButtonScrollState()
    val shown = visible && (!hideOnScroll || scrollState.visible)
    val colors = fabColorsOf(colorIndex)

    val actionLabels = listOf(
        stringResource(Res.string.common_edit),
        stringResource(Res.string.common_share),
        stringResource(Res.string.demo_floating_action_button_scan),
    )
    val items = remember(actionLabels) {
        listOf(
            FloatingActionButtonItem(
                icon = { Icon(imageVector = COUIIcons.Edit, contentDescription = null) },
                onClick = {
                    lastAction = 0
                    expanded = false
                },
                label = actionLabels[0],
            ),
            FloatingActionButtonItem(
                icon = { Icon(imageVector = COUIIcons.Share, contentDescription = null) },
                onClick = {
                    lastAction = 1
                    expanded = false
                },
                label = actionLabels[1],
            ),
            // A bare child button without a label.
            FloatingActionButtonItem(
                icon = { Icon(imageVector = COUIIcons.Scan, contentDescription = actionLabels[2]) },
                onClick = {
                    lastAction = 2
                    expanded = false
                },
            ),
        )
    }

    AppPageScaffold(
        title = stringResource(Res.string.demo_floating_action_button_title),
        modifier = modifier,
        subtitle = "FloatingActionButton",
        onBack = onBack,
        snackbarHostState = snackbarHostState,
        floatingActionButton = {
            val addLabel = stringResource(Res.string.common_add)
            if (expandable) {
                ExpandableFloatingActionButton(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    items = items,
                    enabled = enabled,
                    visible = shown,
                    material = material,
                    containerColor = colors.container,
                    disabledContainerColor = colors.disabledContainer,
                    contentColor = colors.content,
                ) {
                    Icon(imageVector = COUIIcons.Add, contentDescription = addLabel)
                }
            } else {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(getString(Res.string.demo_floating_action_button_clicked))
                        }
                    },
                    enabled = enabled,
                    visible = shown,
                    material = material,
                    containerColor = colors.container,
                    disabledContainerColor = colors.disabledContainer,
                    contentColor = colors.content,
                    shadowElevation = if (shadow) FloatingActionButtonDefaults.ShadowElevation else 0.dp,
                    minWidth = if (tiny) FloatingActionButtonDefaults.TinySize else FloatingActionButtonDefaults.MinWidth,
                    minHeight = if (tiny) FloatingActionButtonDefaults.TinySize else FloatingActionButtonDefaults.MinHeight,
                ) {
                    Icon(imageVector = COUIIcons.Add, contentDescription = addLabel)
                }
            }
        },
        floatingActionButtonPosition = FabPositions[positionIndex],
    ) { pageScope ->
        AppPageList(
            scope = pageScope,
            modifier = Modifier.nestedScroll(scrollState.nestedScrollConnection),
        ) {
            item(key = "parameters") {
                PlaygroundControls {
                    item {
                        SwitchPreference(
                            title = stringResource(Res.string.common_visible),
                            summary = "visible",
                            checked = visible,
                            onCheckedChange = { visible = it },
                        )
                    }
                    item {
                        OverlayDropdownPreference(
                            title = stringResource(Res.string.common_position),
                            summary = "floatingActionButtonPosition",
                            items = listOf(
                                stringResource(Res.string.common_start),
                                stringResource(Res.string.common_center),
                                stringResource(Res.string.common_end),
                                stringResource(Res.string.demo_floating_action_button_position_end_overlay),
                            ),
                            selectedIndex = positionIndex,
                            onSelectedIndexChange = { positionIndex = it },
                        )
                    }
                    item {
                        OverlayDropdownPreference(
                            title = stringResource(Res.string.demo_floating_action_button_type),
                            summary = if (expandable) "ExpandableFloatingActionButton" else "FloatingActionButton",
                            items = listOf(
                                stringResource(Res.string.demo_floating_action_button_type_single),
                                stringResource(Res.string.demo_floating_action_button_type_expandable),
                            ),
                            selectedIndex = if (expandable) 1 else 0,
                            onSelectedIndexChange = {
                                expandable = it == 1
                                expanded = false
                            },
                        )
                    }
                    item {
                        OverlayDropdownPreference(
                            title = stringResource(Res.string.common_color),
                            summary = "containerColor",
                            items = listOf(
                                stringResource(Res.string.demo_floating_action_button_color_primary),
                                stringResource(Res.string.demo_floating_action_button_color_secondary),
                                stringResource(Res.string.demo_floating_action_button_color_translucent),
                            ),
                            selectedIndex = colorIndex,
                            onSelectedIndexChange = { colorIndex = it },
                        )
                    }
                    item {
                        OverlayDropdownPreference(
                            title = stringResource(Res.string.common_size),
                            summary = "minWidth / minHeight",
                            items = listOf(
                                stringResource(Res.string.common_default),
                                stringResource(Res.string.demo_floating_action_button_size_tiny),
                            ),
                            selectedIndex = if (tiny) 1 else 0,
                            onSelectedIndexChange = { tiny = it == 1 },
                            enabled = !expandable,
                        )
                    }
                    item {
                        SwitchPreference(
                            title = stringResource(Res.string.demo_floating_action_button_shadow),
                            summary = "shadowElevation",
                            checked = shadow,
                            onCheckedChange = { shadow = it },
                            enabled = !expandable,
                        )
                    }
                    item {
                        SwitchPreference(
                            title = stringResource(Res.string.common_material),
                            summary = "material",
                            checked = material,
                            onCheckedChange = { material = it },
                        )
                    }
                    item {
                        SwitchPreference(
                            title = stringResource(Res.string.common_enabled),
                            checked = enabled,
                            onCheckedChange = { enabled = it },
                        )
                    }
                    item {
                        SwitchPreference(
                            title = stringResource(Res.string.demo_floating_action_button_hide_on_scroll),
                            summary = stringResource(Res.string.demo_floating_action_button_hide_on_scroll_summary),
                            checked = hideOnScroll,
                            onCheckedChange = { hideOnScroll = it },
                        )
                    }
                    item {
                        BasicComponent(
                            title = stringResource(Res.string.demo_floating_action_button_last_action),
                            summary = if (lastAction == NO_ACTION) {
                                stringResource(Res.string.demo_floating_action_button_none)
                            } else {
                                actionLabels[lastAction]
                            },
                            enabled = expandable,
                        )
                    }
                }
            }
            item(key = "styles") {
                OpenDemoSection(title = stringResource(Res.string.demo_floating_action_button_variant_styles)) {
                    FabStyles()
                }
            }
            // The FAB takes the TYPE_FRAMEWORK_FAB backdrop blur in place of its fill.
            item(key = "backdrop") {
                OpenDemoSection(title = stringResource(Res.string.demo_floating_action_button_variant_backdrop)) {
                    ButtonStage(
                        background = StageBackground.Photo,
                        cornerRadius = 16.dp,
                        contentPadding = PaddingValues(24.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            FloatingActionButton(onClick = {}, shadowElevation = 0.dp) {
                                Icon(imageVector = COUIIcons.Add, contentDescription = stringResource(Res.string.common_add))
                            }
                            FloatingActionButton(
                                onClick = {},
                                containerColor = FloatingActionButtonDefaults.translateContainerColor(),
                                disabledContainerColor = FloatingActionButtonDefaults.translateContainerColor(),
                                contentColor = COUITheme.colorScheme.onTransparentButton,
                                shadowElevation = 0.dp,
                            ) {
                                Icon(imageVector = COUIIcons.Add, contentDescription = stringResource(Res.string.common_add))
                            }
                        }
                    }
                }
            }
            // A long list to scroll, for the hide-on-scroll behaviour.
            variant(key = "scroll", title = Res.string.demo_floating_action_button_variant_scroll) {
                CardListGroup {
                    repeat(SCROLL_ROWS) { index ->
                        item(key = index) {
                            BasicComponent(title = stringResource(Res.string.sample_item, index + 1))
                        }
                    }
                }
            }
        }
    }
}

/** The number of rows in the scroll section. */
private const val SCROLL_ROWS = 20

/** The COUI 17 floating action button: material on and off, disabled, and the secondary tiny preset. */
@Composable
private fun FabStyles(modifier: Modifier = Modifier) {
    val addLabel = stringResource(Res.string.common_add)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 40.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FloatingActionButton(onClick = {}, material = true) {
            Icon(imageVector = COUIIcons.Add, contentDescription = addLabel)
        }
        FloatingActionButton(onClick = {}, material = false) {
            Icon(imageVector = COUIIcons.Add, contentDescription = addLabel)
        }
        FloatingActionButton(onClick = {}, enabled = false) {
            Icon(imageVector = COUIIcons.Add, contentDescription = addLabel)
        }
        // The secondary preset at the tiny size; the preset styles draw no shadow.
        FloatingActionButton(
            onClick = {},
            containerColor = FloatingActionButtonDefaults.secondaryContainerColor(),
            disabledContainerColor = FloatingActionButtonDefaults.secondaryDisabledContainerColor(),
            contentColor = COUITheme.colorScheme.labelPrimary,
            shadowElevation = 0.dp,
            minWidth = FloatingActionButtonDefaults.TinySize,
            minHeight = FloatingActionButtonDefaults.TinySize,
        ) {
            Icon(imageVector = COUIIcons.Add, contentDescription = addLabel)
        }
    }
}

/** The colours of a playground colour preset. */
@Composable
private fun fabColorsOf(preset: Int): FabColors = when (preset) {
    COLOR_SECONDARY -> FabColors(
        container = FloatingActionButtonDefaults.secondaryContainerColor(),
        disabledContainer = FloatingActionButtonDefaults.secondaryDisabledContainerColor(),
        content = COUITheme.colorScheme.labelPrimary,
    )

    COLOR_TRANSLUCENT -> FabColors(
        container = FloatingActionButtonDefaults.translateContainerColor(),
        disabledContainer = FloatingActionButtonDefaults.translateContainerColor(),
        content = COUITheme.colorScheme.onTransparentButton,
    )

    else -> FabColors(
        container = COUITheme.colorScheme.primary,
        disabledContainer = FloatingActionButtonDefaults.disabledContainerColor(),
        content = COUITheme.colorScheme.onPrimary,
    )
}
