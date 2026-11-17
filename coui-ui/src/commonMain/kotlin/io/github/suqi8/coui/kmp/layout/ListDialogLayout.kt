// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.layout

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import io.github.suqi8.coui.kmp.basic.ButtonColors
import io.github.suqi8.coui.kmp.basic.ButtonDefaults
import io.github.suqi8.coui.kmp.basic.Checkbox
import io.github.suqi8.coui.kmp.basic.CouiButton
import io.github.suqi8.coui.kmp.basic.HorizontalDivider
import io.github.suqi8.coui.kmp.basic.RadioButton
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.effect.ContentButtonMaterialType
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * An item of a list, single-choice or multi-choice dialog.
 *
 * @param text The item label.
 * @param summary The optional secondary line (`COUIAlertDialogBuilder.setSummaryItems` /
 *   the `ChoiceListAdapter` summaries).
 * @param textColor The label color of a list dialog item (the `textColor` array of
 *   `COUIAlertDialogBuilder.setItems`). [Color.Unspecified] keeps the default. Ignored by the
 *   choice dialogs.
 * @param icon The optional start icon of a choice dialog row (`ChoiceListAdapter.setIcons`).
 *   Ignored by the list dialog.
 * @param enabled Whether the item can be clicked (`ChoiceListAdapter` disable status).
 */
@Immutable
data class ListDialogItem(
    val text: String,
    val summary: String? = null,
    val textColor: Color = Color.Unspecified,
    val icon: ImageVector? = null,
    val enabled: Boolean = true,
)

/**
 * Internal shared layout of the list dialogs (`COUIAlertDialogBuilder.setItems` with the
 * `COUIAlertDialog.List.Bottom` style, `SummaryAdapter` + `coui_alert_dialog_summary_item`): every
 * item is a full-width `COUIAlertDialogBottomButtonSecondary` capsule with an optional summary
 * below it.
 */
@Composable
internal fun ListDialogContentLayout(
    show: Boolean,
    items: List<ListDialogItem>,
    onItemClick: (index: Int) -> Unit,
    popupHost: @Composable (visible: Boolean, content: @Composable () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    title: String? = null,
    titleColor: Color = Color.Unspecified,
    summary: String? = null,
    summaryColor: Color = Color.Unspecified,
    negative: DialogButtonBarAction? = null,
    positive: DialogButtonBarAction? = null,
    neutral: DialogButtonBarAction? = null,
    colors: ListDialogColors = ListDialogDefaults.listDialogColors(),
    backgroundColor: Color = Color.Unspecified,
    enableWindowDim: Boolean = true,
    onDismissRequest: (() -> Unit)? = null,
    onDismissFinished: (() -> Unit)? = null,
    topInset: Dp? = null,
    materialStroke: Boolean = false,
) {
    val currentOnItemClick by rememberUpdatedState(onItemClick)
    // SummaryAdapter.resetPadding: the first item takes the bottom inset as its top margin when no
    // title or message sits above it, and the last one when no button bar follows.
    val isTop = title == null && summary == null
    val isBottom = negative == null && positive == null && neutral == null
    DialogContentLayout(
        show = show,
        titleColor = titleColor,
        summaryColor = summaryColor,
        backgroundColor = backgroundColor,
        outsideMargin = DialogDefaults.outsideMargin,
        insideMargin = DialogDefaults.insideMargin,
        popupHost = popupHost,
        modifier = modifier,
        backgroundModifier = backgroundModifier,
        title = title,
        summary = summary,
        enableWindowDim = enableWindowDim,
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        topInset = topInset,
        materialStroke = materialStroke,
        customContentPaddingBottom = 0.dp,
        summaryMaxHeight = ListDialogDefaults.SummaryMaxHeight,
        customContent = {
            val material = LocalDialogMaterial.current
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = ListDialogDefaults.ListPanelMarginTop)
                    .verticalScroll(rememberScrollState()),
            ) {
                items.forEachIndexed { index, item ->
                    ListDialogButtonItem(
                        item = item,
                        material = material,
                        colors = colors,
                        onClick = { currentOnItemClick(index) },
                        modifier = Modifier.padding(
                            top = if (index == 0 && isTop) {
                                DialogButtonBarDefaults.CellPaddingBottom
                            } else {
                                DialogButtonBarDefaults.CellPaddingTop
                            },
                            bottom = if (index == items.lastIndex && isBottom) {
                                DialogButtonBarDefaults.CellPaddingBottom
                            } else {
                                DialogButtonBarDefaults.CellPaddingTop
                            },
                        ),
                    )
                }
            }
        },
    ) {
        DialogButtonBar(negative = negative, positive = positive, neutral = neutral)
    }
}

/** One `coui_alert_dialog_summary_item`: a capsule button and its optional summary. */
@Composable
private fun ListDialogButtonItem(
    item: ListDialogItem,
    material: Boolean,
    colors: ListDialogColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val buttonColors = if (material) {
        DialogButtonBarDefaults.materialButtonColors()
    } else {
        DialogButtonBarDefaults.buttonColors()
    }
    val contentColor = if (item.textColor.isSpecified) item.textColor else buttonColors.textColor
    val mappedColors = remember(buttonColors, contentColor) {
        ButtonColors(
            color = buttonColors.color,
            disabledColor = buttonColors.disabledColor,
            contentColor = contentColor,
            disabledContentColor = buttonColors.disabledTextColor,
        )
    }
    Column(modifier = modifier.fillMaxWidth()) {
        CouiButton(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DialogButtonBarDefaults.CellPaddingEdge),
            enabled = item.enabled,
            pressScaleEnabled = true,
            cornerRadius = ButtonDefaults.CornerRadius,
            minWidth = 0.dp,
            minHeight = DialogButtonBarDefaults.StackedButtonMinHeight,
            colors = mappedColors,
            insideMargin = DialogDefaults.ButtonBarInsideMargin,
            // SummaryAdapter.applyBlurEffect: SECONDARY material in a blurred window.
            materialType = if (material) ContentButtonMaterialType.DrawableSecondary else null,
            interactionSource = null,
            indication = null,
        ) {
            Text(
                text = item.text,
                style = COUITheme.textStyles.buttonL,
                maxLines = DialogButtonBarDefaults.LabelMaxLines,
                overflow = TextOverflow.Ellipsis,
            )
        }
        item.summary?.let {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = it,
                fontSize = ListDialogDefaults.ItemSummaryFontSize,
                lineHeight = ListDialogDefaults.ItemSummaryLineHeight,
                textAlign = TextAlign.Center,
                color = colors.summaryColor(item.enabled),
            )
        }
    }
}

/**
 * Internal shared layout of the single- and multi-choice dialogs
 * (`COUIAlertDialogBuilder.setSingleChoiceItems` / `setMultiChoiceItems` with `ChoiceListAdapter`
 * and `coui_select_dialog_singlechoice` / `coui_select_dialog_multichoice`).
 *
 * @param checked Whether each row is selected / checked.
 * @param multiChoice Whether the rows end in a checkbox instead of a radio button.
 * @param onItemClick Invoked when an enabled row is clicked.
 */
@Composable
internal fun ChoiceDialogContentLayout(
    show: Boolean,
    items: List<ListDialogItem>,
    checked: (index: Int) -> Boolean,
    multiChoice: Boolean,
    onItemClick: (index: Int) -> Unit,
    popupHost: @Composable (visible: Boolean, content: @Composable () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    title: String? = null,
    titleColor: Color = Color.Unspecified,
    summary: String? = null,
    summaryColor: Color = Color.Unspecified,
    negative: DialogButtonBarAction? = null,
    positive: DialogButtonBarAction? = null,
    neutral: DialogButtonBarAction? = null,
    colors: ListDialogColors = ListDialogDefaults.listDialogColors(),
    backgroundColor: Color = Color.Unspecified,
    enableWindowDim: Boolean = true,
    onDismissRequest: (() -> Unit)? = null,
    onDismissFinished: (() -> Unit)? = null,
    topInset: Dp? = null,
    materialStroke: Boolean = false,
) {
    val currentOnItemClick by rememberUpdatedState(onItemClick)
    DialogContentLayout(
        show = show,
        titleColor = titleColor,
        summaryColor = summaryColor,
        backgroundColor = backgroundColor,
        outsideMargin = DialogDefaults.outsideMargin,
        insideMargin = DialogDefaults.insideMargin,
        popupHost = popupHost,
        modifier = modifier,
        backgroundModifier = backgroundModifier,
        title = title,
        summary = summary,
        enableWindowDim = enableWindowDim,
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        topInset = topInset,
        materialStroke = materialStroke,
        // COUIAlertDialogBuilder.initListPanel: an adapter list pads the listPanel by
        // alert_dialog_list_item_padding_bottom.
        customContentPaddingBottom = ListDialogDefaults.ChoiceListPaddingBottom,
        summaryMaxHeight = ListDialogDefaults.SummaryMaxHeight,
        customContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                items.forEachIndexed { index, item ->
                    ChoiceDialogRow(
                        item = item,
                        checked = checked(index),
                        multiChoice = multiChoice,
                        showDivider = index != items.lastIndex,
                        colors = colors,
                        onClick = { currentOnItemClick(index) },
                    )
                }
            }
        },
    ) {
        DialogButtonBar(negative = negative, positive = positive, neutral = neutral)
    }
}

/**
 * The multi-choice click rule of `ChoiceListAdapter`: a checked row unchecks; an unchecked row
 * checks unless [maxCheckedCount] (> 0) rows are already checked, in which case
 * [onMaxCheckedReached] fires instead.
 */
internal fun toggleMultiChoice(
    checked: List<Boolean>,
    index: Int,
    maxCheckedCount: Int,
    onCheckedChange: (index: Int, checked: Boolean) -> Unit,
    onMaxCheckedReached: (() -> Unit)?,
) {
    if (checked.getOrElse(index) { false }) {
        onCheckedChange(index, false)
    } else if (maxCheckedCount <= 0 || checked.count { it } < maxCheckedCount) {
        onCheckedChange(index, true)
    } else {
        onMaxCheckedReached?.invoke()
    }
}

/**
 * One `ChoiceListAdapter` row: an optional icon, the label and summary, and an end radio button or
 * checkbox, the whole row clickable.
 */
@Composable
private fun ChoiceDialogRow(
    item: ListDialogItem,
    checked: Boolean,
    multiChoice: Boolean,
    showDivider: Boolean,
    colors: ListDialogColors,
    onClick: () -> Unit,
) {
    val currentOnClick by rememberUpdatedState(onClick)
    val hapticFeedback = LocalHapticFeedback.current
    val currentHapticFeedback by rememberUpdatedState(hapticFeedback)
    val interactionModifier = if (multiChoice) {
        Modifier.toggleable(
            value = checked,
            enabled = item.enabled,
            role = Role.Checkbox,
            onValueChange = {
                currentHapticFeedback.performHapticFeedback(
                    if (it) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff,
                )
                currentOnClick()
            },
        )
    } else {
        Modifier.selectable(
            selected = checked,
            enabled = item.enabled,
            role = Role.RadioButton,
            onClick = {
                if (!checked) currentHapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOn)
                currentOnClick()
            },
        )
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = ListDialogDefaults.ChoiceItemMinHeight)
                .then(interactionModifier)
                .padding(horizontal = ListDialogDefaults.ChoiceItemPaddingHorizontal),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            item.icon?.let { icon ->
                Image(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = ListDialogDefaults.ChoiceItemIconMarginEnd)
                        .padding(vertical = ListDialogDefaults.ChoiceItemIconPaddingVertical),
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = ListDialogDefaults.ChoiceItemTextMarginEnd)
                    .padding(vertical = ListDialogDefaults.ChoiceItemPaddingVertical),
            ) {
                Text(
                    text = item.text,
                    style = COUITheme.textStyles.headlineXS,
                    color = colors.textColor(item.enabled),
                )
                item.summary?.let {
                    Text(
                        modifier = Modifier.padding(top = ListDialogDefaults.ChoiceItemSummarySpacing),
                        text = it,
                        style = COUITheme.textStyles.bodyM,
                        color = colors.summaryColor(item.enabled),
                    )
                }
            }
            if (multiChoice) {
                Checkbox(
                    state = ToggleableState(checked),
                    onClick = null,
                    modifier = Modifier.padding(vertical = ListDialogDefaults.ChoiceItemPaddingVertical),
                    enabled = item.enabled,
                )
            } else {
                RadioButton(
                    selected = checked,
                    onClick = null,
                    modifier = Modifier.padding(vertical = ListDialogDefaults.ChoiceItemPaddingVertical),
                    enabled = item.enabled,
                )
            }
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = ListDialogDefaults.ChoiceItemPaddingHorizontal),
                color = colors.dividerColor,
            )
        }
    }
}

object ListDialogDefaults {
    /**
     * The top margin of the list of a list dialog. COUI `coui_list_dialog_list_panel_margin_top` on
     * the `listPanel` of `coui_center_list_alert_dialog_layout`.
     */
    val ListPanelMarginTop = 16.dp

    /**
     * The max height of the message above a list. COUI
     * `coui_alert_dialog_builder_content_max_height_with_adapter`, which
     * `COUIAlertDialogBuilder.initListPanel` sets on the message scroll view of an adapter dialog.
     */
    val SummaryMaxHeight = 60.dp

    /** The summary text size below a list dialog item. COUI `coui_alert_dialog_summary_text_size`. */
    val ItemSummaryFontSize = 12.sp

    /** The summary line height below a list dialog item: `android:lineSpacingMultiplier` 1.15. */
    val ItemSummaryLineHeight = 1.35.em

    /** The min height of a choice row. COUI `coui_delete_alert_dialog_button_height`. */
    val ChoiceItemMinHeight = 48.dp

    /**
     * The horizontal padding of a choice row. COUI `alert_dialog_single_list_padding_left` /
     * `alert_dialog_single_list_padding_right`.
     */
    val ChoiceItemPaddingHorizontal = 24.dp

    /**
     * The vertical margin of the text block and the end control of a choice row. COUI
     * `alert_dialog_single_list_padding_vertical`.
     */
    val ChoiceItemPaddingVertical = 10.dp

    /**
     * The gap between the text block and the end control of a choice row. COUI
     * `coui_dialog_layout_margin_horizontal`.
     */
    val ChoiceItemTextMarginEnd = 16.dp

    /** The gap between the icon and the text of a choice row. COUI `coui_dialog_layout_margin_horizontal`. */
    val ChoiceItemIconMarginEnd = 16.dp

    /** The vertical margin of the icon of a choice row. COUI `alert_dialog_single_list_icon_padding_vertical`. */
    val ChoiceItemIconPaddingVertical = 20.dp

    /** The gap between the label and the summary of a choice row. COUI `coui_alert_dialog_content_panel_padding_top`. */
    val ChoiceItemSummarySpacing = 2.dp

    /** The bottom padding of a choice list. COUI `alert_dialog_list_item_padding_bottom`. */
    val ChoiceListPaddingBottom = 16.dp

    /**
     * The default [ListDialogColors]. The choice labels use
     * `coui_bottom_alert_dialog_button_text_color_selector` (`?couiColorPrimaryNeutral`), the
     * summaries `coui_bottom_alert_dialog_summary_text_color_selector` /
     * `coui_alert_dialog_summary_item` (`?couiColorSecondNeutral`), both `?couiColorDisabledNeutral`
     * when disabled; the row dividers `?couiColorDivider`.
     */
    @Composable
    fun listDialogColors(
        textColor: Color = COUITheme.colorScheme.labelPrimary,
        disabledTextColor: Color = COUITheme.colorScheme.disabledNeutral,
        summaryColor: Color = COUITheme.colorScheme.labelSecondary,
        disabledSummaryColor: Color = COUITheme.colorScheme.disabledNeutral,
        dividerColor: Color = COUITheme.colorScheme.divider,
    ): ListDialogColors = remember(textColor, disabledTextColor, summaryColor, disabledSummaryColor, dividerColor) {
        ListDialogColors(
            textColor = textColor,
            disabledTextColor = disabledTextColor,
            summaryColor = summaryColor,
            disabledSummaryColor = disabledSummaryColor,
            dividerColor = dividerColor,
        )
    }
}

@Immutable
data class ListDialogColors(
    val textColor: Color,
    val disabledTextColor: Color,
    val summaryColor: Color,
    val disabledSummaryColor: Color,
    val dividerColor: Color,
) {
    @Stable
    internal fun textColor(enabled: Boolean): Color = if (enabled) textColor else disabledTextColor

    @Stable
    internal fun summaryColor(enabled: Boolean): Color = if (enabled) summaryColor else disabledSummaryColor
}
