// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.window

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import io.github.suqi8.coui.kmp.layout.ChoiceDialogContentLayout
import io.github.suqi8.coui.kmp.layout.DialogButtonBarAction
import io.github.suqi8.coui.kmp.layout.DialogDefaults
import io.github.suqi8.coui.kmp.layout.ListDialogColors
import io.github.suqi8.coui.kmp.layout.ListDialogContentLayout
import io.github.suqi8.coui.kmp.layout.ListDialogDefaults
import io.github.suqi8.coui.kmp.layout.ListDialogItem
import io.github.suqi8.coui.kmp.layout.toggleMultiChoice
import io.github.suqi8.coui.kmp.utils.RemovePlatformDialogDefaultEffects
import io.github.suqi8.coui.kmp.utils.WindowNavigationEventScope
import io.github.suqi8.coui.kmp.utils.platformDialogProperties

/**
 * A list dialog rendered at window level without `Scaffold` (`COUIAlertDialogBuilder.setItems`,
 * `COUIAlertDialog.List.Bottom`): every item is a full-width capsule button with an optional summary
 * below it, followed by an optional button bar.
 *
 * @param show Whether the [WindowListDialog] is shown.
 * @param items The items. [ListDialogItem.textColor] tints a single label.
 * @param onItemClick Invoked with the index of the clicked item. The dialog does not hide by
 *   itself; set [show] to false to close it.
 * @param modifier The modifier to be applied to the [WindowListDialog].
 * @param backgroundModifier A [Modifier] applied inside the panel's corner clip, under the content
 *   and over [backgroundColor], e.g. to paint the COUI 17 blurred window background.
 * @param title The title of the dialog.
 * @param titleColor The color of the title.
 * @param summary The message of the dialog, capped at [ListDialogDefaults.SummaryMaxHeight].
 * @param summaryColor The color of the summary.
 * @param negative The negative button of the bar below the list. Hidden when null.
 * @param positive The positive button of the bar below the list. Hidden when null.
 * @param neutral The neutral button of the bar below the list. Hidden when null.
 * @param colors The [ListDialogColors] of the item summaries.
 * @param backgroundColor The background color of the dialog.
 * @param enableWindowDim Whether to enable window dimming when the dialog is shown.
 * @param onDismissRequest Invoked when the user taps outside or presses back.
 * @param onDismissFinished Invoked when the hide animation completes.
 * @param materialStroke Whether to draw the COUI 17 material stroke of a blurred panel; the item
 *   and bar buttons then turn material too.
 */
@Composable
fun WindowListDialog(
    show: Boolean,
    items: List<ListDialogItem>,
    onItemClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    title: String? = null,
    titleColor: Color = DialogDefaults.titleColor(),
    summary: String? = null,
    summaryColor: Color = DialogDefaults.summaryColor(),
    negative: DialogButtonBarAction? = null,
    positive: DialogButtonBarAction? = null,
    neutral: DialogButtonBarAction? = null,
    colors: ListDialogColors = ListDialogDefaults.listDialogColors(),
    backgroundColor: Color = DialogDefaults.backgroundColor(),
    enableWindowDim: Boolean = true,
    onDismissRequest: (() -> Unit)? = null,
    onDismissFinished: (() -> Unit)? = null,
    materialStroke: Boolean = false,
) {
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)
    ListDialogContentLayout(
        show = show,
        items = items,
        onItemClick = onItemClick,
        popupHost = { visible, hostContent ->
            WindowDialogHost(visible = visible, onDismissRequest = { currentOnDismissRequest?.invoke() }, content = hostContent)
        },
        modifier = modifier,
        backgroundModifier = backgroundModifier,
        title = title,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        negative = negative,
        positive = positive,
        neutral = neutral,
        colors = colors,
        backgroundColor = backgroundColor,
        enableWindowDim = enableWindowDim,
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        topInset = windowDialogTopInset(),
        materialStroke = materialStroke,
    )
}

/**
 * A single-choice dialog rendered at window level without `Scaffold`
 * (`COUIAlertDialogBuilder.setSingleChoiceItems` with `ChoiceListAdapter`): rows of a label, an
 * optional summary and icon, and an end radio button.
 *
 * @param show Whether the [WindowSingleChoiceDialog] is shown.
 * @param items The rows. A disabled [ListDialogItem] ignores clicks.
 * @param selectedIndex The index of the selected row, or -1 for none.
 * @param onSelectedIndexChange Invoked with the index of the clicked row. The dialog does not hide
 *   by itself, as COUI keeps a single-choice dialog open until a button closes it.
 * @param modifier The modifier to be applied to the [WindowSingleChoiceDialog].
 * @param backgroundModifier A [Modifier] applied inside the panel's corner clip, under the content
 *   and over [backgroundColor], e.g. to paint the COUI 17 blurred window background.
 * @param title The title of the dialog.
 * @param titleColor The color of the title.
 * @param summary The message of the dialog, capped at [ListDialogDefaults.SummaryMaxHeight].
 * @param summaryColor The color of the summary.
 * @param negative The negative button of the bar below the list. Hidden when null.
 * @param positive The positive button of the bar below the list. Hidden when null.
 * @param neutral The neutral button of the bar below the list. Hidden when null.
 * @param colors The [ListDialogColors] of the rows.
 * @param backgroundColor The background color of the dialog.
 * @param enableWindowDim Whether to enable window dimming when the dialog is shown.
 * @param onDismissRequest Invoked when the user taps outside or presses back.
 * @param onDismissFinished Invoked when the hide animation completes.
 * @param materialStroke Whether to draw the COUI 17 material stroke of a blurred panel.
 */
@Composable
fun WindowSingleChoiceDialog(
    show: Boolean,
    items: List<ListDialogItem>,
    selectedIndex: Int,
    onSelectedIndexChange: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    title: String? = null,
    titleColor: Color = DialogDefaults.titleColor(),
    summary: String? = null,
    summaryColor: Color = DialogDefaults.summaryColor(),
    negative: DialogButtonBarAction? = null,
    positive: DialogButtonBarAction? = null,
    neutral: DialogButtonBarAction? = null,
    colors: ListDialogColors = ListDialogDefaults.listDialogColors(),
    backgroundColor: Color = DialogDefaults.backgroundColor(),
    enableWindowDim: Boolean = true,
    onDismissRequest: (() -> Unit)? = null,
    onDismissFinished: (() -> Unit)? = null,
    materialStroke: Boolean = false,
) {
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)
    ChoiceDialogContentLayout(
        show = show,
        items = items,
        checked = { it == selectedIndex },
        multiChoice = false,
        onItemClick = onSelectedIndexChange,
        popupHost = { visible, hostContent ->
            WindowDialogHost(visible = visible, onDismissRequest = { currentOnDismissRequest?.invoke() }, content = hostContent)
        },
        modifier = modifier,
        backgroundModifier = backgroundModifier,
        title = title,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        negative = negative,
        positive = positive,
        neutral = neutral,
        colors = colors,
        backgroundColor = backgroundColor,
        enableWindowDim = enableWindowDim,
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        topInset = windowDialogTopInset(),
        materialStroke = materialStroke,
    )
}

/**
 * A multi-choice dialog rendered at window level without `Scaffold`
 * (`COUIAlertDialogBuilder.setMultiChoiceItems` with `ChoiceListAdapter`): rows of a label, an
 * optional summary and icon, and an end checkbox.
 *
 * @param show Whether the [WindowMultiChoiceDialog] is shown.
 * @param items The rows. A disabled [ListDialogItem] ignores clicks.
 * @param checked Whether each row is checked, index-aligned with [items].
 * @param onCheckedChange Invoked with the index and the new checked state of a toggled row.
 * @param modifier The modifier to be applied to the [WindowMultiChoiceDialog].
 * @param backgroundModifier A [Modifier] applied inside the panel's corner clip, under the content
 *   and over [backgroundColor], e.g. to paint the COUI 17 blurred window background.
 * @param title The title of the dialog.
 * @param titleColor The color of the title.
 * @param summary The message of the dialog, capped at [ListDialogDefaults.SummaryMaxHeight].
 * @param summaryColor The color of the summary.
 * @param maxCheckedCount The max number of checked rows, or 0 for no limit
 *   (`ChoiceListAdapter` `mMaxCheckedNum`). A row past the limit stays unchecked.
 * @param onMaxCheckedReached Invoked instead of [onCheckedChange] when a row past
 *   [maxCheckedCount] is clicked (`ChoiceListAdapter.MaxCheckedListener.maxCheckedNotice`).
 * @param negative The negative button of the bar below the list. Hidden when null.
 * @param positive The positive button of the bar below the list. Hidden when null.
 * @param neutral The neutral button of the bar below the list. Hidden when null.
 * @param colors The [ListDialogColors] of the rows.
 * @param backgroundColor The background color of the dialog.
 * @param enableWindowDim Whether to enable window dimming when the dialog is shown.
 * @param onDismissRequest Invoked when the user taps outside or presses back.
 * @param onDismissFinished Invoked when the hide animation completes.
 * @param materialStroke Whether to draw the COUI 17 material stroke of a blurred panel.
 */
@Composable
fun WindowMultiChoiceDialog(
    show: Boolean,
    items: List<ListDialogItem>,
    checked: List<Boolean>,
    onCheckedChange: (index: Int, checked: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    title: String? = null,
    titleColor: Color = DialogDefaults.titleColor(),
    summary: String? = null,
    summaryColor: Color = DialogDefaults.summaryColor(),
    maxCheckedCount: Int = 0,
    onMaxCheckedReached: (() -> Unit)? = null,
    negative: DialogButtonBarAction? = null,
    positive: DialogButtonBarAction? = null,
    neutral: DialogButtonBarAction? = null,
    colors: ListDialogColors = ListDialogDefaults.listDialogColors(),
    backgroundColor: Color = DialogDefaults.backgroundColor(),
    enableWindowDim: Boolean = true,
    onDismissRequest: (() -> Unit)? = null,
    onDismissFinished: (() -> Unit)? = null,
    materialStroke: Boolean = false,
) {
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)
    val currentChecked by rememberUpdatedState(checked)
    val currentOnCheckedChange by rememberUpdatedState(onCheckedChange)
    val currentOnMaxCheckedReached by rememberUpdatedState(onMaxCheckedReached)
    ChoiceDialogContentLayout(
        show = show,
        items = items,
        checked = { checked.getOrElse(it) { false } },
        multiChoice = true,
        onItemClick = { index ->
            toggleMultiChoice(
                checked = currentChecked,
                index = index,
                maxCheckedCount = maxCheckedCount,
                onCheckedChange = currentOnCheckedChange,
                onMaxCheckedReached = currentOnMaxCheckedReached,
            )
        },
        popupHost = { visible, hostContent ->
            WindowDialogHost(visible = visible, onDismissRequest = { currentOnDismissRequest?.invoke() }, content = hostContent)
        },
        modifier = modifier,
        backgroundModifier = backgroundModifier,
        title = title,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        negative = negative,
        positive = positive,
        neutral = neutral,
        colors = colors,
        backgroundColor = backgroundColor,
        enableWindowDim = enableWindowDim,
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        topInset = windowDialogTopInset(),
        materialStroke = materialStroke,
    )
}

/** The platform-window container shared by the window dialogs; the dialog layout animates itself. */
@Composable
internal fun WindowDialogHost(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (visible) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = platformDialogProperties(),
        ) {
            RemovePlatformDialogDefaultEffects()
            WindowNavigationEventScope {
                content()
            }
        }
    }
}

/** The top safe inset of a window dialog: the tallest of the status bar, caption bar and cutout. */
@Composable
internal fun windowDialogTopInset(): Dp {
    val statusBarsPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val captionBarPadding = WindowInsets.captionBar.asPaddingValues().calculateTopPadding()
    val displayCutoutPadding = WindowInsets.displayCutout.asPaddingValues().calculateTopPadding()
    return remember(statusBarsPadding, captionBarPadding, displayCutoutPadding) {
        maxOf(statusBarsPadding, captionBarPadding, displayCutoutPadding)
    }
}
