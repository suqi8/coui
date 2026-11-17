// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.dialogs

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import catalog.Demo
import catalog.DemoCategory
import catalog.DemoPage
import demo.framework.PlaygroundControls
import demo.framework.PlaygroundPreview
import demo.framework.playground
import demo.framework.variant
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.layout.DialogButtonBarAction
import io.github.suqi8.coui.kmp.layout.DialogDefaults
import io.github.suqi8.coui.kmp.layout.ListDialogItem
import io.github.suqi8.coui.kmp.overlay.OverlayListDialog
import io.github.suqi8.coui.kmp.overlay.OverlayMultiChoiceDialog
import io.github.suqi8.coui.kmp.overlay.OverlaySingleChoiceDialog
import io.github.suqi8.coui.kmp.shared.generated.resources.Res
import io.github.suqi8.coui.kmp.shared.generated.resources.common_cancel
import io.github.suqi8.coui.kmp.shared.generated.resources.common_confirm
import io.github.suqi8.coui.kmp.shared.generated.resources.common_count
import io.github.suqi8.coui.kmp.shared.generated.resources.common_show_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.common_style
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_dialog_window_dim
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_buttons
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_copy_link
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_delete
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_disabled_item
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_disabled_item_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_item_summaries
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_item_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_max_checked
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_max_checked_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_open_list
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_open_list_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_open_multi
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_open_multi_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_open_single
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_open_single_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_result_checked
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_result_clicked
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_result_none
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_result_selected
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_save_image
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_save_image_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_search_terms
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_share_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_show
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_sync_calendar
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_sync_contacts
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_sync_notes
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_sync_photos
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_sync_photos_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_sync_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_timeout_1m
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_timeout_30s
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_timeout_5m
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_timeout_never
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_timeout_never_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_timeout_summary
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_timeout_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_title
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_type_list
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_type_multi
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_type_single
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_variant_list
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_variant_multi
import io.github.suqi8.coui.kmp.shared.generated.resources.demo_list_dialog_variant_single
import io.github.suqi8.coui.kmp.shared.generated.resources.sample_option
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.window.WindowListDialog
import io.github.suqi8.coui.kmp.window.WindowMultiChoiceDialog
import io.github.suqi8.coui.kmp.window.WindowSingleChoiceDialog
import org.jetbrains.compose.resources.stringResource

/** The demo page of `OverlayListDialog`, listed in its category and found by the search. */
internal val ListDialogDemo = Demo(
    id = "list_dialog",
    apiName = "OverlayListDialog",
    title = Res.string.demo_list_dialog_title,
    category = DemoCategory.Dialogs,
    page = DemoPage.Variants {
        playground {
            ListDialogPlayground()
        }
        variant(key = "list", title = Res.string.demo_list_dialog_variant_list) {
            ShareListDialogVariant()
        }
        variant(key = "single", title = Res.string.demo_list_dialog_variant_single) {
            SingleChoiceDialogVariant()
        }
        variant(key = "multi", title = Res.string.demo_list_dialog_variant_multi) {
            MultiChoiceDialogVariant()
        }
    },
    keywords = listOf("OverlaySingleChoiceDialog", "OverlayMultiChoiceDialog"),
    searchTerms = Res.string.demo_list_dialog_search_terms,
    isNew = true,
)

private const val TYPE_LIST = 0
private const val TYPE_SINGLE = 1

/** The item counts the playground offers; numbers stay literals. */
private val ItemCounts = listOf("2", "3", "4", "5", "6")

/** The max checked counts of the multi-choice playground; 0 means no limit. */
private val MaxCheckedCounts = listOf("0", "1", "2", "3")

@Composable
private fun ColumnScope.ListDialogPlayground() {
    var hostIndex by rememberSaveable { mutableIntStateOf(0) }
    var material by rememberSaveable { mutableStateOf(true) }
    var typeIndex by rememberSaveable { mutableIntStateOf(TYPE_SINGLE) }
    var countIndex by rememberSaveable { mutableIntStateOf(2) }
    var showSummary by rememberSaveable { mutableStateOf(true) }
    var itemSummaries by rememberSaveable { mutableStateOf(false) }
    var disabledItem by rememberSaveable { mutableStateOf(false) }
    var buttons by rememberSaveable { mutableStateOf(true) }
    var maxCheckedIndex by rememberSaveable { mutableIntStateOf(0) }
    var windowDim by rememberSaveable { mutableStateOf(true) }
    var show by remember { mutableStateOf(false) }
    var selected by rememberSaveable { mutableIntStateOf(0) }
    var checked by remember { mutableStateOf(setOf(0)) }
    // The result is kept as an index; its text is resolved in composition.
    var lastClicked by rememberSaveable { mutableIntStateOf(-1) }
    val window = hostIndex == HOST_WINDOW
    val count = countIndex + 2

    val itemSummary = stringResource(Res.string.demo_list_dialog_item_summary)
    val optionTexts = List(count) { stringResource(Res.string.sample_option, it + 1) }
    val items = optionTexts.mapIndexed { index, text ->
        ListDialogItem(
            text = text,
            summary = if (itemSummaries) itemSummary else null,
            enabled = !(disabledItem && index == count - 1),
        )
    }
    val dismiss = { show = false }
    val cancel = stringResource(Res.string.common_cancel)
    val confirm = stringResource(Res.string.common_confirm)
    val negative = if (buttons) DialogButtonBarAction(text = cancel, onClick = dismiss) else null
    // The plain list closes on an item tap, so it only takes the cancel button.
    val positive = if (buttons && typeIndex != TYPE_LIST) DialogButtonBarAction(text = confirm, onClick = dismiss) else null
    val title = when (typeIndex) {
        TYPE_LIST -> stringResource(Res.string.demo_list_dialog_type_list)
        TYPE_SINGLE -> stringResource(Res.string.demo_list_dialog_type_single)
        else -> stringResource(Res.string.demo_list_dialog_type_multi)
    }
    val summary = if (showSummary) stringResource(Res.string.demo_list_dialog_summary) else null
    val blur = demoPanelBlur(enabled = material && !window)
    val checkedList = List(count) { it in checked }

    val result = when (typeIndex) {
        TYPE_LIST -> if (lastClicked in 0 until count) {
            stringResource(Res.string.demo_list_dialog_result_clicked, optionTexts[lastClicked])
        } else {
            stringResource(Res.string.demo_list_dialog_result_none)
        }

        TYPE_SINGLE -> stringResource(
            Res.string.demo_list_dialog_result_selected,
            optionTexts.getOrElse(selected) { "-" },
        )

        else -> stringResource(
            Res.string.demo_list_dialog_result_checked,
            optionTexts.filterIndexed { index, _ -> index in checked }.joinToString(),
        )
    }

    val typeItems = listOf(
        stringResource(Res.string.demo_list_dialog_type_list),
        stringResource(Res.string.demo_list_dialog_type_single),
        stringResource(Res.string.demo_list_dialog_type_multi),
    )

    PlaygroundPreview {
        DialogLaunchButton(text = stringResource(Res.string.demo_list_dialog_show), onClick = { show = true })
        val apiName = when (typeIndex) {
            TYPE_LIST -> "ListDialog"
            TYPE_SINGLE -> "SingleChoiceDialog"
            else -> "MultiChoiceDialog"
        }
        DialogDemoCaption(text = (if (window) "Window" else "Overlay") + apiName)
        DialogDemoCaption(text = result)
    }
    PlaygroundControls {
        dialogHostRow(selectedIndex = hostIndex, onSelectedIndexChange = { hostIndex = it })
        dialogMaterialRow(checked = material, onCheckedChange = { material = it }, enabled = !window)
        dialogDropdownRow(
            title = Res.string.common_style,
            items = typeItems,
            selectedIndex = typeIndex,
            onSelectedIndexChange = { typeIndex = it },
        )
        dialogDropdownRow(
            title = Res.string.common_count,
            items = ItemCounts,
            selectedIndex = countIndex,
            onSelectedIndexChange = { countIndex = it },
        )
        dialogSwitchRow(
            title = Res.string.common_show_summary,
            checked = showSummary,
            onCheckedChange = { showSummary = it },
        )
        dialogSwitchRow(
            title = Res.string.demo_list_dialog_item_summaries,
            checked = itemSummaries,
            onCheckedChange = { itemSummaries = it },
        )
        dialogSwitchRow(
            title = Res.string.demo_list_dialog_disabled_item,
            checked = disabledItem,
            onCheckedChange = { disabledItem = it },
            summary = Res.string.demo_list_dialog_disabled_item_summary,
        )
        dialogSwitchRow(
            title = Res.string.demo_list_dialog_buttons,
            checked = buttons,
            onCheckedChange = { buttons = it },
        )
        dialogDropdownRow(
            title = Res.string.demo_list_dialog_max_checked,
            items = MaxCheckedCounts,
            selectedIndex = maxCheckedIndex,
            onSelectedIndexChange = { maxCheckedIndex = it },
            summary = Res.string.demo_list_dialog_max_checked_summary,
            enabled = typeIndex != TYPE_LIST && typeIndex != TYPE_SINGLE,
        )
        dialogSwitchRow(
            title = Res.string.demo_dialog_window_dim,
            checked = windowDim,
            onCheckedChange = { windowDim = it },
        )
    }

    val backgroundModifier = blur?.backgroundModifier ?: Modifier
    val backgroundColor = blur?.backgroundColor ?: DialogDefaults.backgroundColor()
    val onItemClick: (Int) -> Unit = {
        lastClicked = it
        show = false
    }
    val onCheckedChange: (Int, Boolean) -> Unit = { index, value ->
        checked = if (value) checked + index else checked - index
    }
    when {
        typeIndex == TYPE_LIST && window -> WindowListDialog(
            show = show,
            items = items,
            onItemClick = onItemClick,
            title = title,
            summary = summary,
            negative = negative,
            enableWindowDim = windowDim,
            onDismissRequest = dismiss,
        )

        typeIndex == TYPE_LIST -> OverlayListDialog(
            show = show,
            items = items,
            onItemClick = onItemClick,
            backgroundModifier = backgroundModifier,
            title = title,
            summary = summary,
            negative = negative,
            backgroundColor = backgroundColor,
            enableWindowDim = windowDim,
            onDismissRequest = dismiss,
            materialStroke = blur != null,
        )

        typeIndex == TYPE_SINGLE && window -> WindowSingleChoiceDialog(
            show = show,
            items = items,
            selectedIndex = selected,
            onSelectedIndexChange = { selected = it },
            title = title,
            summary = summary,
            negative = negative,
            positive = positive,
            enableWindowDim = windowDim,
            onDismissRequest = dismiss,
        )

        typeIndex == TYPE_SINGLE -> OverlaySingleChoiceDialog(
            show = show,
            items = items,
            selectedIndex = selected,
            onSelectedIndexChange = { selected = it },
            backgroundModifier = backgroundModifier,
            title = title,
            summary = summary,
            negative = negative,
            positive = positive,
            backgroundColor = backgroundColor,
            enableWindowDim = windowDim,
            onDismissRequest = dismiss,
            materialStroke = blur != null,
        )

        window -> WindowMultiChoiceDialog(
            show = show,
            items = items,
            checked = checkedList,
            onCheckedChange = onCheckedChange,
            title = title,
            summary = summary,
            maxCheckedCount = maxCheckedIndex,
            negative = negative,
            positive = positive,
            enableWindowDim = windowDim,
            onDismissRequest = dismiss,
        )

        else -> OverlayMultiChoiceDialog(
            show = show,
            items = items,
            checked = checkedList,
            onCheckedChange = onCheckedChange,
            backgroundModifier = backgroundModifier,
            title = title,
            summary = summary,
            maxCheckedCount = maxCheckedIndex,
            negative = negative,
            positive = positive,
            backgroundColor = backgroundColor,
            enableWindowDim = windowDim,
            onDismissRequest = dismiss,
            materialStroke = blur != null,
        )
    }
}

/** The list dialog (COUIAlertDialogBuilder.setItems): items drawn as capsule buttons. */
@Composable
private fun ColumnScope.ShareListDialogVariant() {
    var show by remember { mutableStateOf(false) }
    var holdDown by remember { mutableStateOf(false) }
    CardListGroup {
        dialogLaunchRow(
            title = Res.string.demo_list_dialog_open_list,
            summary = Res.string.demo_list_dialog_open_list_summary,
            holdDown = holdDown,
            onClick = {
                show = true
                holdDown = true
            },
        )
    }
    val blur = demoPanelBlur(enabled = true)
    OverlayListDialog(
        show = show,
        title = stringResource(Res.string.demo_list_dialog_share_title),
        items = listOf(
            ListDialogItem(text = stringResource(Res.string.demo_list_dialog_copy_link)),
            ListDialogItem(
                text = stringResource(Res.string.demo_list_dialog_save_image),
                summary = stringResource(Res.string.demo_list_dialog_save_image_summary),
            ),
            ListDialogItem(
                text = stringResource(Res.string.demo_list_dialog_delete),
                textColor = COUITheme.colorScheme.error,
            ),
        ),
        onItemClick = { show = false },
        negative = DialogButtonBarAction(text = stringResource(Res.string.common_cancel), onClick = { show = false }),
        backgroundModifier = blur?.backgroundModifier ?: Modifier,
        backgroundColor = blur?.backgroundColor ?: DialogDefaults.backgroundColor(),
        onDismissRequest = { show = false },
        onDismissFinished = { holdDown = false },
        materialStroke = blur != null,
    )
}

/** The single-choice dialog (ChoiceListAdapter): rows ending in a radio button. */
@Composable
private fun ColumnScope.SingleChoiceDialogVariant() {
    var show by remember { mutableStateOf(false) }
    var holdDown by remember { mutableStateOf(false) }
    var selected by rememberSaveable { mutableIntStateOf(0) }
    CardListGroup {
        dialogLaunchRow(
            title = Res.string.demo_list_dialog_open_single,
            summary = Res.string.demo_list_dialog_open_single_summary,
            holdDown = holdDown,
            onClick = {
                show = true
                holdDown = true
            },
        )
    }
    val blur = demoPanelBlur(enabled = true)
    OverlaySingleChoiceDialog(
        show = show,
        title = stringResource(Res.string.demo_list_dialog_timeout_title),
        summary = stringResource(Res.string.demo_list_dialog_timeout_summary),
        items = listOf(
            ListDialogItem(text = stringResource(Res.string.demo_list_dialog_timeout_30s)),
            ListDialogItem(text = stringResource(Res.string.demo_list_dialog_timeout_1m)),
            ListDialogItem(text = stringResource(Res.string.demo_list_dialog_timeout_5m)),
            ListDialogItem(
                text = stringResource(Res.string.demo_list_dialog_timeout_never),
                summary = stringResource(Res.string.demo_list_dialog_timeout_never_summary),
            ),
        ),
        selectedIndex = selected,
        onSelectedIndexChange = { selected = it },
        negative = DialogButtonBarAction(text = stringResource(Res.string.common_cancel), onClick = { show = false }),
        positive = DialogButtonBarAction(text = stringResource(Res.string.common_confirm), onClick = { show = false }),
        backgroundModifier = blur?.backgroundModifier ?: Modifier,
        backgroundColor = blur?.backgroundColor ?: DialogDefaults.backgroundColor(),
        onDismissRequest = { show = false },
        onDismissFinished = { holdDown = false },
        materialStroke = blur != null,
    )
}

/** The multi-choice dialog: rows ending in a checkbox, at most two checked, one row disabled. */
@Composable
private fun ColumnScope.MultiChoiceDialogVariant() {
    var show by remember { mutableStateOf(false) }
    var holdDown by remember { mutableStateOf(false) }
    var checked by remember { mutableStateOf(listOf(true, false, false, false)) }
    CardListGroup {
        dialogLaunchRow(
            title = Res.string.demo_list_dialog_open_multi,
            summary = Res.string.demo_list_dialog_open_multi_summary,
            holdDown = holdDown,
            onClick = {
                show = true
                holdDown = true
            },
        )
    }
    val blur = demoPanelBlur(enabled = true)
    OverlayMultiChoiceDialog(
        show = show,
        title = stringResource(Res.string.demo_list_dialog_sync_title),
        items = listOf(
            ListDialogItem(text = stringResource(Res.string.demo_list_dialog_sync_contacts)),
            ListDialogItem(
                text = stringResource(Res.string.demo_list_dialog_sync_photos),
                summary = stringResource(Res.string.demo_list_dialog_sync_photos_summary),
            ),
            ListDialogItem(text = stringResource(Res.string.demo_list_dialog_sync_notes)),
            ListDialogItem(text = stringResource(Res.string.demo_list_dialog_sync_calendar), enabled = false),
        ),
        checked = checked,
        onCheckedChange = { index, value -> checked = checked.toMutableList().also { it[index] = value } },
        maxCheckedCount = 2,
        negative = DialogButtonBarAction(text = stringResource(Res.string.common_cancel), onClick = { show = false }),
        positive = DialogButtonBarAction(text = stringResource(Res.string.common_confirm), onClick = { show = false }),
        backgroundModifier = blur?.backgroundModifier ?: Modifier,
        backgroundColor = blur?.backgroundColor ?: DialogDefaults.backgroundColor(),
        onDismissRequest = { show = false },
        onDismissFinished = { holdDown = false },
        materialStroke = blur != null,
    )
}
