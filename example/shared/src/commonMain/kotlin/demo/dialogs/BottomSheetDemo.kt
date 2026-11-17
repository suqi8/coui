// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.dialogs

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardDefaults
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.basic.IconButton
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.TextField
import io.github.suqi8.coui.kmp.blur.MaterialBlurDefaults
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Close
import io.github.suqi8.coui.kmp.icon.extended.Ok
import io.github.suqi8.coui.kmp.layout.BottomSheetDefaults
import io.github.suqi8.coui.kmp.overlay.OverlayBottomSheet
import io.github.suqi8.coui.kmp.preference.ArrowPreference
import io.github.suqi8.coui.kmp.preference.OverlayDropdownPreference
import io.github.suqi8.coui.kmp.preference.SliderPreference
import io.github.suqi8.coui.kmp.preference.SwitchPreference
import io.github.suqi8.coui.kmp.preference.WindowDropdownPreference
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_cancel
import io.github.suqi8.coui.kmp.shared.generated.resources.common_confirm
import io.github.suqi8.coui.kmp.shared.generated.resources.common_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_bottom_sheet_actions
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_bottom_sheet_actions_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_bottom_sheet_allow_dismiss
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_bottom_sheet_allow_dismiss_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_bottom_sheet_behavior
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_bottom_sheet_dismiss_outside
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_bottom_sheet_drag_handle
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_bottom_sheet_draggable
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_bottom_sheet_draggable_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_bottom_sheet_dropdown
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_bottom_sheet_handle_panel
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_bottom_sheet_handle_panel_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_bottom_sheet_long_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_bottom_sheet_max_height
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_bottom_sheet_max_height_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_bottom_sheet_nested_scroll
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_bottom_sheet_nested_scroll_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_bottom_sheet_open_overlay
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_bottom_sheet_open_overlay_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_bottom_sheet_open_window
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_bottom_sheet_open_window_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_bottom_sheet_pull_up
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_bottom_sheet_pull_up_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_bottom_sheet_rows
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_bottom_sheet_short_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_bottom_sheet_show
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_bottom_sheet_slider
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_bottom_sheet_switch
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_bottom_sheet_text_field
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_bottom_sheet_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_bottom_sheet_variant_hosts
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_window_dim
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialogs_length_long
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialogs_length_none
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialogs_length_short
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_item
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_option
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.utils.overScrollVertical
import io.github.suqi8.coui.kmp.utils.scrollEndHaptic
import io.github.suqi8.coui.kmp.window.WindowBottomSheet
import org.jetbrains.compose.resources.stringResource

internal val BottomSheetDemo = Demo(
    id = "bottom_sheet",
    apiName = "OverlayBottomSheet",
    title = Res.string.demo_bottom_sheet_title,
    category = DemoCategory.Dialogs,
    page = DemoPage.Variants {
        playground {
            BottomSheetPlayground()
        }
        variant(key = "hosts", title = Res.string.demo_bottom_sheet_variant_hosts) {
            BottomSheetsVariant()
        }
    },
    keywords = listOf("panel", "WindowBottomSheet", "sheet", "面板", "半屏"),
)

/** The content row counts the playground offers; numbers stay literals. */
private val RowCounts = listOf(3, 8, 20)

@Composable
private fun ColumnScope.BottomSheetPlayground() {
    var hostIndex by rememberSaveable { mutableIntStateOf(0) }
    var material by rememberSaveable { mutableStateOf(true) }
    var titleIndex by rememberSaveable { mutableIntStateOf(1) }
    var actions by rememberSaveable { mutableStateOf(true) }
    var rowsIndex by rememberSaveable { mutableIntStateOf(1) }
    var dragHandle by rememberSaveable { mutableStateOf(false) }
    var handlePanel by rememberSaveable { mutableStateOf(false) }
    var draggable by rememberSaveable { mutableStateOf(true) }
    var allowDismiss by rememberSaveable { mutableStateOf(true) }
    var dismissOutside by rememberSaveable { mutableStateOf(true) }
    var nestedScroll by rememberSaveable { mutableStateOf(true) }
    var pullUp by rememberSaveable { mutableStateOf(true) }
    var maxHeight by rememberSaveable { mutableStateOf(false) }
    var windowDim by rememberSaveable { mutableStateOf(true) }
    var show by remember { mutableStateOf(false) }
    val window = hostIndex == HOST_WINDOW

    val lengthItems = listOf(
        stringResource(Res.string.demo_dialogs_length_none),
        stringResource(Res.string.demo_dialogs_length_short),
        stringResource(Res.string.demo_dialogs_length_long),
    )
    val title = when (titleIndex) {
        0 -> null
        1 -> stringResource(Res.string.demo_bottom_sheet_short_title)
        else -> stringResource(Res.string.demo_bottom_sheet_long_title)
    }
    val rowCount = RowCounts[rowsIndex]
    val dismiss = { show = false }
    val blur = demoPanelBlur(enabled = material && !window, style = MaterialBlurDefaults::bottomSheet)
    val closeAction: @Composable () -> Unit = { SheetActionButton(close = true, onClick = dismiss) }
    val confirmAction: @Composable () -> Unit = { SheetActionButton(close = false, onClick = dismiss) }
    val startAction = if (actions) closeAction else null
    val endAction = if (actions) confirmAction else null

    PlaygroundPreview {
        DialogLaunchButton(text = stringResource(Res.string.demo_bottom_sheet_show), onClick = { show = true })
        DialogDemoCaption(text = if (window) "WindowBottomSheet" else "OverlayBottomSheet")
    }
    PlaygroundControls {
        dialogHostRow(selectedIndex = hostIndex, onSelectedIndexChange = { hostIndex = it })
        dialogMaterialRow(checked = material, onCheckedChange = { material = it }, enabled = !window)
        dialogDropdownRow(
            title = Res.string.common_title,
            items = lengthItems,
            selectedIndex = titleIndex,
            onSelectedIndexChange = { titleIndex = it },
        )
        dialogSwitchRow(
            title = Res.string.demo_bottom_sheet_actions,
            checked = actions,
            onCheckedChange = { actions = it },
            summary = Res.string.demo_bottom_sheet_actions_summary,
        )
        dialogDropdownRow(
            title = Res.string.demo_bottom_sheet_rows,
            items = RowCounts.map { it.toString() },
            selectedIndex = rowsIndex,
            onSelectedIndexChange = { rowsIndex = it },
        )
        dialogSwitchRow(
            title = Res.string.demo_bottom_sheet_drag_handle,
            checked = dragHandle,
            onCheckedChange = { dragHandle = it },
        )
        dialogSwitchRow(
            title = Res.string.demo_bottom_sheet_handle_panel,
            checked = handlePanel,
            onCheckedChange = { handlePanel = it },
            summary = Res.string.demo_bottom_sheet_handle_panel_summary,
        )
        dialogSwitchRow(
            title = Res.string.demo_bottom_sheet_draggable,
            checked = draggable,
            onCheckedChange = { draggable = it },
            summary = Res.string.demo_bottom_sheet_draggable_summary,
        )
        dialogSwitchRow(
            title = Res.string.demo_bottom_sheet_allow_dismiss,
            checked = allowDismiss,
            onCheckedChange = { allowDismiss = it },
            summary = Res.string.demo_bottom_sheet_allow_dismiss_summary,
        )
        dialogSwitchRow(
            title = Res.string.demo_bottom_sheet_dismiss_outside,
            checked = dismissOutside,
            onCheckedChange = { dismissOutside = it },
            enabled = allowDismiss,
        )
        dialogSwitchRow(
            title = Res.string.demo_bottom_sheet_nested_scroll,
            checked = nestedScroll,
            onCheckedChange = { nestedScroll = it },
            summary = Res.string.demo_bottom_sheet_nested_scroll_summary,
        )
        dialogSwitchRow(
            title = Res.string.demo_bottom_sheet_pull_up,
            checked = pullUp,
            onCheckedChange = { pullUp = it },
            summary = Res.string.demo_bottom_sheet_pull_up_summary,
        )
        dialogSwitchRow(
            title = Res.string.demo_bottom_sheet_max_height,
            checked = maxHeight,
            onCheckedChange = { maxHeight = it },
            summary = Res.string.demo_bottom_sheet_max_height_summary,
        )
        dialogSwitchRow(
            title = Res.string.demo_dialog_window_dim,
            checked = windowDim,
            onCheckedChange = { windowDim = it },
        )
    }

    if (window) {
        WindowBottomSheet(
            show = show,
            title = title,
            startAction = startAction,
            endAction = endAction,
            enableWindowDim = windowDim,
            onDismissRequest = dismiss,
            showDragHandle = dragHandle,
            handlePanel = handlePanel,
            showInMaxHeight = maxHeight,
            allowDismiss = allowDismiss,
            dismissOnClickOutside = dismissOutside,
            draggable = draggable,
            enablePullUp = pullUp,
            enableNestedScroll = nestedScroll,
        ) {
            SheetRowsContent(rowCount = rowCount)
        }
    } else {
        OverlayBottomSheet(
            show = show,
            backgroundModifier = blur?.backgroundModifier ?: Modifier,
            title = title,
            startAction = startAction,
            endAction = endAction,
            backgroundColor = blur?.backgroundColor ?: BottomSheetDefaults.backgroundColor(),
            enableWindowDim = windowDim,
            onDismissRequest = dismiss,
            showDragHandle = dragHandle,
            handlePanel = handlePanel,
            showInMaxHeight = maxHeight,
            allowDismiss = allowDismiss,
            dismissOnClickOutside = dismissOutside,
            draggable = draggable,
            enablePullUp = pullUp,
            enableNestedScroll = nestedScroll,
            materialStroke = blur != null,
        ) {
            SheetRowsContent(rowCount = rowCount)
        }
    }
}

/** A close (×) or confirm (✓) icon button for the sheet title bar. */
@Composable
private fun SheetActionButton(
    close: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = if (close) COUIIcons.Close else COUIIcons.Ok,
            contentDescription = stringResource(if (close) Res.string.common_cancel else Res.string.common_confirm),
            tint = COUITheme.colorScheme.labelPrimary,
        )
    }
}

/** The bottom padding that keeps the last sheet row clear of the navigation and caption bars. */
@Composable
private fun SheetBottomSpacer(modifier: Modifier = Modifier) {
    Spacer(
        modifier.padding(
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() +
                WindowInsets.captionBar.asPaddingValues().calculateBottomPadding(),
        ),
    )
}

/** The playground sheet content: a card of [rowCount] rows, scrolling once it outgrows the panel. */
@Composable
private fun SheetRowsContent(
    rowCount: Int,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .scrollEndHaptic()
            .overScrollVertical(),
        // COUI panels add no padding of their own; the content sets its margins.
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        item {
            Card(
                modifier = Modifier.padding(bottom = 12.dp),
                colors = CardDefaults.defaultColors(color = COUITheme.colorScheme.secondaryContainer),
            ) {
                CardListGroup {
                    repeat(rowCount) { index ->
                        item {
                            ArrowPreference(title = stringResource(Res.string.sample_item, index + 1))
                        }
                    }
                }
            }
            SheetBottomSpacer()
        }
    }
}

/**
 * The two original sheets: their behaviour switches live inside the panel and apply while it is
 * open, next to a slider, a text field and a dropdown showing ordinary controls in a sheet.
 */
@Composable
private fun ColumnScope.BottomSheetsVariant() {
    var showOverlay by remember { mutableStateOf(false) }
    var overlayHoldDown by remember { mutableStateOf(false) }
    var showWindow by remember { mutableStateOf(false) }
    var windowHoldDown by remember { mutableStateOf(false) }
    var dropdownIndex by remember { mutableIntStateOf(0) }
    var switchChecked by remember { mutableStateOf(true) }
    CardListGroup {
        dialogLaunchRow(
            title = Res.string.demo_bottom_sheet_open_overlay,
            summary = Res.string.demo_bottom_sheet_open_overlay_summary,
            holdDown = overlayHoldDown,
            onClick = {
                showOverlay = true
                overlayHoldDown = true
            },
        )
        dialogLaunchRow(
            title = Res.string.demo_bottom_sheet_open_window,
            summary = Res.string.demo_bottom_sheet_open_window_summary,
            holdDown = windowHoldDown,
            onClick = {
                showWindow = true
                windowHoldDown = true
            },
        )
    }
    OverlaySheetDemo(
        show = showOverlay,
        onDismissRequest = { showOverlay = false },
        onDismissFinished = { overlayHoldDown = false },
        dropdownIndex = dropdownIndex,
        onDropdownIndexChange = { dropdownIndex = it },
        switchChecked = switchChecked,
        onSwitchCheckedChange = { switchChecked = it },
    )
    WindowSheetDemo(
        show = showWindow,
        onDismissRequest = { showWindow = false },
        onDismissFinished = { windowHoldDown = false },
        dropdownIndex = dropdownIndex,
        onDropdownIndexChange = { dropdownIndex = it },
        switchChecked = switchChecked,
        onSwitchCheckedChange = { switchChecked = it },
    )
}

@Composable
private fun OverlaySheetDemo(
    show: Boolean,
    onDismissRequest: () -> Unit,
    onDismissFinished: () -> Unit,
    dropdownIndex: Int,
    onDropdownIndexChange: (Int) -> Unit,
    switchChecked: Boolean,
    onSwitchCheckedChange: (Boolean) -> Unit,
) {
    var allowDismiss by remember { mutableStateOf(true) }
    var nestedScroll by remember { mutableStateOf(true) }
    var dragHandle by remember { mutableStateOf(false) }
    var handlePanel by remember { mutableStateOf(false) }
    var draggable by remember { mutableStateOf(true) }
    // COUI 17 blurs the window behind the panel when the material blur is on; the page backdrop
    // stands in for that window here.
    val blur = demoPanelBlur(enabled = true, style = MaterialBlurDefaults::bottomSheet)
    OverlayBottomSheet(
        show = show,
        backgroundModifier = blur?.backgroundModifier ?: Modifier,
        title = stringResource(Res.string.demo_bottom_sheet_open_overlay),
        startAction = { SheetActionButton(close = true, onClick = onDismissRequest) },
        endAction = { SheetActionButton(close = false, onClick = onDismissRequest) },
        backgroundColor = blur?.backgroundColor ?: BottomSheetDefaults.backgroundColor(),
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        showDragHandle = dragHandle,
        handlePanel = handlePanel,
        allowDismiss = allowDismiss,
        draggable = draggable,
        enableNestedScroll = nestedScroll,
        materialStroke = blur != null,
    ) {
        SheetControlsContent(
            allowDismiss = allowDismiss,
            onAllowDismissChange = { allowDismiss = it },
            nestedScroll = nestedScroll,
            onNestedScrollChange = { nestedScroll = it },
            draggable = draggable,
            onDraggableChange = { draggable = it },
            dragHandle = dragHandle,
            onDragHandleChange = { dragHandle = it },
            handlePanel = handlePanel,
            onHandlePanelChange = { handlePanel = it },
            dropdownIndex = dropdownIndex,
            onDropdownIndexChange = onDropdownIndexChange,
            switchChecked = switchChecked,
            onSwitchCheckedChange = onSwitchCheckedChange,
            windowDropdown = false,
        )
    }
}

@Composable
private fun WindowSheetDemo(
    show: Boolean,
    onDismissRequest: () -> Unit,
    onDismissFinished: () -> Unit,
    dropdownIndex: Int,
    onDropdownIndexChange: (Int) -> Unit,
    switchChecked: Boolean,
    onSwitchCheckedChange: (Boolean) -> Unit,
) {
    var allowDismiss by remember { mutableStateOf(true) }
    var nestedScroll by remember { mutableStateOf(true) }
    WindowBottomSheet(
        show = show,
        title = stringResource(Res.string.demo_bottom_sheet_open_window),
        startAction = { SheetActionButton(close = true, onClick = onDismissRequest) },
        endAction = { SheetActionButton(close = false, onClick = onDismissRequest) },
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        allowDismiss = allowDismiss,
        enableNestedScroll = nestedScroll,
    ) {
        SheetControlsContent(
            allowDismiss = allowDismiss,
            onAllowDismissChange = { allowDismiss = it },
            nestedScroll = nestedScroll,
            onNestedScrollChange = { nestedScroll = it },
            draggable = null,
            onDraggableChange = {},
            dragHandle = null,
            onDragHandleChange = {},
            handlePanel = null,
            onHandlePanelChange = {},
            dropdownIndex = dropdownIndex,
            onDropdownIndexChange = onDropdownIndexChange,
            switchChecked = switchChecked,
            onSwitchCheckedChange = onSwitchCheckedChange,
            windowDropdown = true,
        )
    }
}

/**
 * The content of the original sheets. A null behaviour value hides its switch (the Window sheet
 * only offers dismissal and nested scrolling).
 */
@Composable
private fun SheetControlsContent(
    allowDismiss: Boolean,
    onAllowDismissChange: (Boolean) -> Unit,
    nestedScroll: Boolean,
    onNestedScrollChange: (Boolean) -> Unit,
    draggable: Boolean?,
    onDraggableChange: (Boolean) -> Unit,
    dragHandle: Boolean?,
    onDragHandleChange: (Boolean) -> Unit,
    handlePanel: Boolean?,
    onHandlePanelChange: (Boolean) -> Unit,
    dropdownIndex: Int,
    onDropdownIndexChange: (Int) -> Unit,
    switchChecked: Boolean,
    onSwitchCheckedChange: (Boolean) -> Unit,
    windowDropdown: Boolean,
    modifier: Modifier = Modifier,
) {
    val behaviorTitle = stringResource(Res.string.demo_bottom_sheet_behavior)
    val allowDismissTitle = stringResource(Res.string.demo_bottom_sheet_allow_dismiss)
    val allowDismissSummary = stringResource(Res.string.demo_bottom_sheet_allow_dismiss_summary)
    val nestedScrollTitle = stringResource(Res.string.demo_bottom_sheet_nested_scroll)
    val nestedScrollSummary = stringResource(Res.string.demo_bottom_sheet_nested_scroll_summary)
    val draggableTitle = stringResource(Res.string.demo_bottom_sheet_draggable)
    val draggableSummary = stringResource(Res.string.demo_bottom_sheet_draggable_summary)
    val dragHandleTitle = stringResource(Res.string.demo_bottom_sheet_drag_handle)
    val handlePanelTitle = stringResource(Res.string.demo_bottom_sheet_handle_panel)
    val handlePanelSummary = stringResource(Res.string.demo_bottom_sheet_handle_panel_summary)
    val dropdownTitle = stringResource(Res.string.demo_bottom_sheet_dropdown)
    val switchTitle = stringResource(Res.string.demo_bottom_sheet_switch)
    val options = listOf(
        stringResource(Res.string.sample_option, 1),
        stringResource(Res.string.sample_option, 2),
    )
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .scrollEndHaptic()
            .overScrollVertical(),
        // COUI panels add no padding of their own; the content sets its margins.
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        item {
            SmallTitle(
                text = behaviorTitle,
                insideMargin = PaddingValues(16.dp, 8.dp),
            )
            Card(
                modifier = Modifier.padding(bottom = 12.dp),
                colors = CardDefaults.defaultColors(color = COUITheme.colorScheme.secondaryContainer),
            ) {
                CardListGroup {
                    item {
                        SwitchPreference(
                            title = allowDismissTitle,
                            summary = allowDismissSummary,
                            checked = allowDismiss,
                            onCheckedChange = onAllowDismissChange,
                        )
                    }
                    item {
                        SwitchPreference(
                            title = nestedScrollTitle,
                            summary = nestedScrollSummary,
                            checked = nestedScroll,
                            onCheckedChange = onNestedScrollChange,
                        )
                    }
                    if (draggable != null) {
                        item {
                            SwitchPreference(
                                title = draggableTitle,
                                summary = draggableSummary,
                                checked = draggable,
                                onCheckedChange = onDraggableChange,
                            )
                        }
                    }
                    if (dragHandle != null) {
                        item {
                            SwitchPreference(
                                title = dragHandleTitle,
                                checked = dragHandle,
                                onCheckedChange = onDragHandleChange,
                            )
                        }
                    }
                    if (handlePanel != null) {
                        item {
                            SwitchPreference(
                                title = handlePanelTitle,
                                summary = handlePanelSummary,
                                checked = handlePanel,
                                onCheckedChange = onHandlePanelChange,
                            )
                        }
                    }
                }
            }
        }
        item {
            var sliderValue by remember { mutableFloatStateOf(0.5f) }
            SliderPreference(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                modifier = Modifier.padding(bottom = 12.dp),
                title = stringResource(Res.string.demo_bottom_sheet_slider),
            )
            var textFieldValue by remember { mutableStateOf("") }
            TextField(
                value = textFieldValue,
                onValueChange = { textFieldValue = it },
                label = stringResource(Res.string.demo_bottom_sheet_text_field),
                modifier = Modifier.padding(bottom = 12.dp),
            )
            Card(
                modifier = Modifier.padding(bottom = 12.dp),
                colors = CardDefaults.defaultColors(color = COUITheme.colorScheme.secondaryContainer),
            ) {
                CardListGroup {
                    item {
                        if (windowDropdown) {
                            WindowDropdownPreference(
                                items = options,
                                selectedIndex = dropdownIndex,
                                title = dropdownTitle,
                                onSelectedIndexChange = onDropdownIndexChange,
                            )
                        } else {
                            OverlayDropdownPreference(
                                items = options,
                                selectedIndex = dropdownIndex,
                                title = dropdownTitle,
                                onSelectedIndexChange = onDropdownIndexChange,
                            )
                        }
                    }
                    item {
                        SwitchPreference(
                            title = switchTitle,
                            checked = switchChecked,
                            onCheckedChange = onSwitchCheckedChange,
                        )
                    }
                }
            }
            SheetBottomSpacer()
        }
    }
}
