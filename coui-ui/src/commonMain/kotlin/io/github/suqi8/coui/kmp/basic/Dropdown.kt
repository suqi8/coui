// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlendModeColorFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.basic.ArrowUpDown
import io.github.suqi8.coui.kmp.icon.basic.Check
import io.github.suqi8.coui.kmp.squircle.drawSquircleRect
import io.github.suqi8.coui.kmp.squircle.isSquircleEnabled
import io.github.suqi8.coui.kmp.theme.COUITheme

@Composable
@NonRestartableComposable
fun RowScope.DropdownArrowEndAction(
    actionColor: Color,
    modifier: Modifier = Modifier,
) {
    val colorFilter = remember(actionColor) { ColorFilter.tint(actionColor) }
    Image(
        modifier = modifier
            .size(width = DropdownDefaults.ArrowSize.width, height = DropdownDefaults.ArrowSize.height)
            .align(Alignment.CenterVertically),
        imageVector = COUIIcons.Basic.ArrowUpDown,
        colorFilter = colorFilter,
        contentDescription = null,
    )
}

/**
 * The implementation of the dropdown.
 *
 * In popup mode the row follows COUI 17 `coui_popup_list_window_item.xml` as bound by
 * `MixedPopupListAdapter.VerticalViewHolder`: a 40dp minimum height with 8dp vertical and 20dp
 * horizontal padding, an optional 20dp icon, the title over an optional description, then — only
 * when needed — a trailing hint slot and a 24dp state icon (the check of a selected row, or the
 * sub-menu indicator). Press feedback is the list's shared mask, not a per-row highlight.
 *
 * @param item The item of the current option.
 * @param optionSize The size of the options.
 * @param isSelected Whether the option is selected.
 * @param index The index of the current option in the options.
 * @param dropdownColors The [DropdownColors] used to style the option row.
 * @param enabled Whether the option is clickable. Disabled rows ignore clicks and use the disabled text color.
 * @param dialogMode Whether the item is shown in dialog mode.
 * @param hasSubmenu When true, this row acts as a submenu trigger: the sub-menu indicator is shown
 *   instead of the selection check, and the row's accessibility role becomes [Role.Button].
 * @param submenuExpanded Whether the sub menu this row opens is showing. COUI then tints the
 *   indicator with the selected colour (`PopupListItem.groupState` 1).
 * @param reserveIconSlot Whether the row keeps the leading icon slot (20dp + 12dp) even when
 *   [item] has no icon, so its title lines up with the rows that have one. COUI 17
 *   `MixedPopupListAdapter.setItemList` raises this for every vertical row as soon as any item of
 *   the list has an icon (horizontal items included); pass the same value to all rows of one
 *   list. Popup mode only.
 * @param onSelectedIndexChange The callback invoked with [index] when the option is selected.
 */
@Composable
fun DropdownImpl(
    item: DropdownItem,
    optionSize: Int,
    isSelected: Boolean,
    index: Int,
    dropdownColors: DropdownColors = DropdownDefaults.dropdownColors(),
    enabled: Boolean = item.enabled,
    dialogMode: Boolean = false,
    hasSubmenu: Boolean = false,
    submenuExpanded: Boolean = false,
    reserveIconSlot: Boolean = false,
    onSelectedIndexChange: (Int) -> Unit,
) {
    if (dialogMode) {
        DialogDropdownRow(
            item = item,
            isSelected = isSelected,
            index = index,
            dropdownColors = dropdownColors,
            enabled = enabled,
            onSelectedIndexChange = onSelectedIndexChange,
        )
    } else {
        PopupDropdownRow(
            item = item,
            isSelected = isSelected,
            dropdownColors = dropdownColors,
            enabled = enabled,
            hasSubmenu = hasSubmenu,
            submenuExpanded = submenuExpanded,
            submenuHeader = false,
            reserveIconSlot = reserveIconSlot,
            onClick = { onSelectedIndexChange(index) },
        )
    }
}

/**
 * One COUI 17 popup row (`MixedPopupListAdapter.VerticalViewHolder.bind`).
 *
 * @param submenuHeader Whether this row is the copy of the parent item that heads a sub menu on a
 *   compact window (`PopupListItem.groupState` 2): its title turns medium and its indicator takes the
 *   selected colour (`setTitleForVertical` / `getTintColorByState`).
 * @param held Whether the list's shared mask stays locked on this row.
 * @param pressHandoff Whether this row inherits a fading press when it first appears.
 * @param reserveIconSlot Whether the leading icon slot is kept when [item] has no icon
 *   (`MixedPopupListAdapter.setIcon` shows the 20dp icon view on every row once any item has one).
 * @param onLongClick The action of a 650ms long press, or `null`.
 */
@Composable
internal fun PopupDropdownRow(
    item: DropdownItem,
    isSelected: Boolean,
    dropdownColors: DropdownColors,
    enabled: Boolean,
    hasSubmenu: Boolean,
    submenuExpanded: Boolean,
    submenuHeader: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    held: Boolean = false,
    pressHandoff: Boolean = false,
    reserveIconSlot: Boolean = false,
    onLongClick: (() -> Unit)? = null,
) {
    // COUI MixedPopupListAdapter.getTintColorByState: a disabled row takes the disabled state, an
    // enabled alert row (ITEM_TYPE_ALERT) couiColorError, everything else the tint selector, whose
    // selected and default states are both couiColorLabelPrimary in COUI 17.
    val titleColor = when {
        !enabled -> dropdownColors.disabledContentColor
        item.alert -> dropdownColors.alertContentColor
        isSelected -> dropdownColors.selectedContentColor
        else -> dropdownColors.contentColor
    }
    // COUI MixedPopupListAdapter.setDescription always paints couiColorLabelSecondary.
    val summaryColor = if (isSelected) dropdownColors.selectedSummaryColor else dropdownColors.summaryColor
    val backgroundColor = if (isSelected) dropdownColors.selectedContainerColor else dropdownColors.containerColor
    val backgroundColorState = rememberUpdatedState(backgroundColor)

    // COUI MixedPopupListAdapter.setHint hides the whole hint layout on a disabled row.
    val hint = item.hint?.takeIf { enabled }
    // setStateIcon: the 24dp state icon only exists for a sub-menu trigger, a custom state icon or a
    // checked row; the sub-menu indicator wins, then the custom icon, then the check.
    val customStateIcon = item.stateIcon
    val showStateIcon = hasSubmenu || customStateIcon != null || isSelected
    // getTintColorByState(stateIconTint, item, true) over coui_popup_list_window_item_status_icon_tint_selector:
    // selected (an open sub menu, a sub-menu header or a checked row) is couiColorLabelPrimary, the
    // default couiColorLabelTertiary; an alert row takes couiColorError.
    val stateColor = when {
        !enabled -> dropdownColors.disabledContentColor
        item.alert -> dropdownColors.alertContentColor
        hasSubmenu && (submenuExpanded || submenuHeader) -> dropdownColors.selectedIndicatorColor
        hasSubmenu -> dropdownColors.submenuIndicatorColor
        isSelected -> dropdownColors.selectedIndicatorColor
        else -> dropdownColors.submenuIndicatorColor
    }

    var titleSingleLine by remember { mutableStateOf(true) }
    val currentOnClick by rememberUpdatedState(onClick)
    val role = if (hasSubmenu) Role.Button else Role.RadioButton

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .drawBehind { drawRect(backgroundColorState.value) }
            .popupListItem(
                selected = isSelected,
                enabled = enabled,
                role = role,
                hasIcon = item.icon != null || reserveIconSlot,
                singleLine = titleSingleLine && item.summary == null,
                hasSubmenu = hasSubmenu,
                held = held,
                pressHandoff = pressHandoff,
                onLongClick = onLongClick,
                onClick = { currentOnClick() },
            )
            .then(PopupRowModifier),
    ) {
        val icon = item.icon
        if (icon != null) {
            icon(IconCellModifier)
        } else if (reserveIconSlot) {
            Spacer(IconCellModifier)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.text,
                modifier = TitleWidthModifier,
                style = popupRowTitleStyle(),
                // COUI coui_popup_list_window_item.xml textFontWeight=400; the sub-menu header turns
                // medium (MixedPopupListAdapter.setTitleForVertical, groupState 2 at index 0).
                fontWeight = if (submenuHeader) FontWeight.Medium else FontWeight.Normal,
                color = titleColor,
                // COUI setTitleForVertical: 3 lines without a description, 2 with one.
                maxLines = if (item.summary == null) {
                    DropdownDefaults.TitleMaxLines
                } else {
                    DropdownDefaults.TitleWithSummaryMaxLines
                },
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { titleSingleLine = it.lineCount <= 1 },
            )
            item.summary?.let {
                Text(
                    text = it,
                    modifier = TitleWidthModifier,
                    style = COUITheme.textStyles.bodyXS,
                    color = summaryColor,
                    maxLines = DropdownDefaults.SummaryMaxLines,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        // setGap: the 8dp title-end gap only exists next to a hint or a state icon, the 4dp
        // hint-end gap only between the two.
        if (hint != null || showStateIcon) Spacer(TitleEndGapModifier)
        if (hint != null) {
            Box(HintCellModifier, contentAlignment = Alignment.Center) { hint() }
            if (showStateIcon) Spacer(HintEndGapModifier)
        }
        if (showStateIcon) {
            val colorFilter = remember(stateColor) { BlendModeColorFilter(stateColor, BlendMode.SrcIn) }
            Image(
                modifier = StateIconModifier,
                imageVector = when {
                    hasSubmenu -> PopupMenuIcons.ExpandableIndicator
                    customStateIcon != null -> customStateIcon
                    else -> PopupMenuIcons.Check
                },
                colorFilter = colorFilter,
                contentDescription = null,
            )
        }
    }
}

/** The single-choice row of a dropdown shown as a dialog (COUI alert_dialog_singlechoice). */
@Composable
private fun DialogDropdownRow(
    item: DropdownItem,
    isSelected: Boolean,
    index: Int,
    dropdownColors: DropdownColors,
    enabled: Boolean,
    onSelectedIndexChange: (Int) -> Unit,
) {
    val backgroundColor = if (isSelected) {
        dropdownColors.selectedContainerColor
    } else {
        dropdownColors.containerColor
    }
    val backgroundColorState = rememberUpdatedState(backgroundColor)
    val checkColor = when {
        !isSelected -> Color.Transparent
        !enabled -> dropdownColors.disabledContentColor
        else -> dropdownColors.selectedIndicatorColor
    }
    val titleColor = when {
        !enabled -> dropdownColors.disabledContentColor
        item.alert -> dropdownColors.alertContentColor
        isSelected -> dropdownColors.selectedContentColor
        else -> dropdownColors.contentColor
    }
    val summaryColor = if (isSelected) dropdownColors.selectedSummaryColor else dropdownColors.summaryColor
    val currentOnSelectedIndexChange by rememberUpdatedState(onSelectedIndexChange)
    val hint = item.hint?.takeIf { enabled }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .drawBehind { drawRect(backgroundColorState.value) }
            .popupListItem(
                selected = isSelected,
                enabled = enabled,
                role = Role.RadioButton,
                hasIcon = item.icon != null,
                onClick = { currentOnSelectedIndexChange(index) },
            )
            .then(DialogRowModifier),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            item.icon?.let { it(DialogIconCellModifier) }
            Column {
                Text(
                    text = item.text,
                    style = COUITheme.textStyles.bodyXL,
                    // Dialog single-choice rows use medium (couiTextAppearanceHeadline6).
                    fontWeight = FontWeight.Medium,
                    color = titleColor,
                    maxLines = if (item.summary == null) {
                        DropdownDefaults.TitleMaxLines
                    } else {
                        DropdownDefaults.TitleWithSummaryMaxLines
                    },
                    overflow = TextOverflow.Ellipsis,
                )
                item.summary?.let {
                    Text(
                        text = it,
                        style = COUITheme.textStyles.bodyM,
                        color = summaryColor,
                        maxLines = DropdownDefaults.SummaryMaxLines,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
        if (hint != null) {
            Spacer(TitleEndGapModifier)
            Box(HintCellModifier, contentAlignment = Alignment.Center) { hint() }
        }
        val checkColorFilter = remember(checkColor) { BlendModeColorFilter(checkColor, BlendMode.SrcIn) }
        Image(
            modifier = if (hint != null) HintedCheckIconModifier else CheckIconBaseModifier,
            imageVector = COUIIcons.Basic.Check,
            colorFilter = checkColorFilter,
            contentDescription = null,
        )
    }
}

/**
 * A non-clickable group-title row shown inside a dropdown popup
 * (COUI 17 coui_popup_list_window_header_item.xml): 4dp above and 8dp below the title, 20dp at the
 * sides, no minimum height, `couiTextDescription` (12sp medium) in couiColorLabelSecondary, at most
 * two lines. The shared press mask passes over it without changing state.
 *
 * @param text The group title.
 * @param modifier The modifier applied to the header row.
 * @param color The title color.
 */
@Composable
fun DropdownHeaderItem(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = DropdownDefaults.dropdownColors().headerColor,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = DropdownDefaults.InsideHorizontalPadding,
                top = DropdownDefaults.PopupHeaderTopPadding,
                end = DropdownDefaults.InsideHorizontalPadding,
                bottom = DropdownDefaults.MiddleVerticalPadding,
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = text,
            style = COUITheme.textStyles.bodyXS,
            fontWeight = FontWeight.Medium,
            color = color,
            maxLines = DropdownDefaults.HeaderMaxLines,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * The divider COUI 17 draws between two groups of a popup menu (`MixedPopupListAdapter`
 * `DividerViewHolder`, item view type 4): a 12dp band (coui_popup_list_group_divider_total_height)
 * with a centred 0.5dp line (coui_popup_list_divider_height) inset 20dp at both sides
 * (coui_popup_list_default_divider_margin_horizontal), in couiColorDivider.
 *
 * @param modifier The modifier applied to the band.
 * @param color The line color.
 */
@Composable
fun DropdownGroupDivider(
    modifier: Modifier = Modifier,
    color: Color = DropdownDefaults.PopupGroupDividerColor,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(DropdownDefaults.PopupGroupDividerThickness)
            .drawBehind {
                val inset = DropdownDefaults.PopupGroupDividerMargin.toPx()
                val line = DropdownDefaults.PopupGroupDividerLineThickness.toPx()
                drawRect(
                    color = color,
                    topLeft = Offset(inset, (size.height - line) / 2f),
                    size = Size((size.width - 2 * inset).coerceAtLeast(0f), line),
                )
            },
    )
}

@Composable
@NonRestartableComposable
fun DropdownImpl(
    text: String,
    optionSize: Int,
    isSelected: Boolean,
    index: Int,
    dropdownColors: DropdownColors = DropdownDefaults.dropdownColors(),
    enabled: Boolean = true,
    dialogMode: Boolean = false,
    reserveIconSlot: Boolean = false,
    onSelectedIndexChange: (Int) -> Unit,
) {
    val item = remember(text, enabled) { DropdownItem(text = text, enabled = enabled) }
    DropdownImpl(
        item = item,
        optionSize = optionSize,
        isSelected = isSelected,
        index = index,
        dropdownColors = dropdownColors,
        enabled = enabled,
        dialogMode = dialogMode,
        reserveIconSlot = reserveIconSlot,
        onSelectedIndexChange = onSelectedIndexChange,
    )
}

/**
 * Colors used by dropdown option rows.
 *
 * @param contentColor The text color of an unselected option.
 * @param summaryColor The summary text color of an unselected option.
 * @param containerColor The background color of an unselected option.
 * @param selectedContentColor The text color of the selected option.
 * @param selectedSummaryColor The summary text color of the selected option.
 * @param selectedContainerColor The background color of the selected option.
 * @param selectedIndicatorColor The color of the selected indicator icon.
 * @param disabledContentColor The text color of a disabled option. Defaults to [contentColor] so
 *   existing constructor calls keep compiling; [DropdownDefaults.dropdownColors] supplies the
 *   COUI value.
 * @param alertContentColor The text color of an alert (destructive) option. Defaults to
 *   [contentColor], i.e. no alert tint unless a caller opts in.
 * @param headerColor The title color of a [DropdownHeaderItem] group header row. Defaults to
 *   [summaryColor], which already carries the same couiColorLabelSecondary value.
 * @param submenuIndicatorColor The color of the sub-menu indicator while its sub menu is closed
 *   (COUI coui_popup_list_window_item_status_icon_tint_selector default, couiColorLabelTertiary).
 *   Defaults to [disabledContentColor], which carries the same value.
 */
@Immutable
data class DropdownColors(
    val contentColor: Color,
    val summaryColor: Color,
    val containerColor: Color,
    val selectedContentColor: Color,
    val selectedSummaryColor: Color,
    val selectedContainerColor: Color,
    val selectedIndicatorColor: Color,
    val disabledContentColor: Color = contentColor,
    val alertContentColor: Color = contentColor,
    val headerColor: Color = summaryColor,
    val submenuIndicatorColor: Color = disabledContentColor,
)

/**
 * A group of dropdown items.
 *
 * A [DropdownEntry] represents one visual group in a dropdown menu.
 *
 * @param items Items shown in this dropdown group.
 * @param enabled Whether this group is enabled. When false, all items in this group are disabled;
 * when true, each item's [DropdownItem.enabled] value is still respected.
 * @param title Optional group header rendered above [items] as a non-clickable
 *   [DropdownHeaderItem] (COUI coui_popup_list_window_header_item.xml). Popup mode only; dialog
 *   mode has no COUI header presentation and ignores it.
 * @param horizontal Whether the group is the COUI 17 row of horizontal items
 *   ([DropdownHorizontalItems], `PopupListItem.MENU_ITEM_TYPE_HORIZONTAL`): up to five icons, or
 *   three icons with labels, side by side. Popup mode only; it is always followed by a divider.
 */
@Stable
data class DropdownEntry(
    val items: List<DropdownItem>,
    val enabled: Boolean = true,
    val title: String? = null,
    val horizontal: Boolean = false,
)

/**
 * An item shown inside a dropdown, spinner, or dropdown menu.
 *
 * @param text Text shown for the item.
 * @param enabled Whether the item can be clicked.
 * @param selected Whether the item is selected.
 * @param onClick Callback invoked when the item is clicked. Ignored when [children] is non-null
 *   and non-empty (the click is consumed by the cascading layer to expand the submenu).
 * @param icon Optional icon shown before [text].
 * @param summary Optional summary shown below [text].
 * @param children Optional submenu items. When non-null and non-empty, this item becomes a
 *   submenu trigger: cascading dropdown popups render a chevron and open a child popup on click,
 *   recursively. Stabilize this list (e.g. via `remember`) to avoid unnecessary recomposition.
 * @param hint Optional trailing hint slot rendered between the title block and the selection
 *   indicator (COUI popup_list_window_item_hint_layout). Suitable for a badge, a red dot, or a
 *   short count; it is width-capped at [DropdownDefaults.HintMaxWidth] and, matching COUI
 *   DefaultAdapter.setHint, hidden entirely while the row is disabled.
 * @param alert Whether this is an alert (destructive) item. Its title and state icon are tinted
 *   with [DropdownColors.alertContentColor] (COUI ITEM_TYPE_ALERT → couiColorError).
 * @param stateIcon Optional custom trailing state icon in the 24dp state slot
 *   (`PopupListItem.setStateIcon`). It is shown whether or not the item is [selected] and replaces
 *   the selection check; a submenu trigger keeps its indicator. It is tinted like the check
 *   (`forceTint` state bit): [DropdownColors.selectedIndicatorColor] while selected,
 *   [DropdownColors.submenuIndicatorColor] (couiColorLabelTertiary) otherwise. Popup mode only.
 */
@Stable
data class DropdownItem(
    val text: String,
    val enabled: Boolean = true,
    val selected: Boolean = false,
    val onClick: (() -> Unit)? = null,
    val icon: @Composable ((Modifier) -> Unit)? = null,
    val summary: String? = null,
    val children: List<DropdownItem>? = null,
    val hint: @Composable (() -> Unit)? = null,
    val alert: Boolean = false,
    val stateIcon: ImageVector? = null,
)

object DropdownDefaults {
    /** Minimum row height when the dropdown is shown in dialog mode (COUI alert_dialog_list_item_min_height). */
    val MinHeight: Dp = 50.dp

    /** Minimum row width when the dropdown is shown in dialog mode. */
    val MinWidth: Dp = 200.dp

    /**
     * Minimum height of each option row in popup mode, vertical padding included
     * (COUI coui_popup_list_window_item_min_height). COUI 17 applies it, and the 8dp vertical
     * padding, to every row alike (`MixedPopupListAdapter.configItemVerticalPadding`).
     */
    val PopupItemMinHeight: Dp = 40.dp

    /**
     * Size of the trailing state icon — the check of the selected option or the sub-menu indicator
     * (COUI coui_popup_list_window_checkbox_width / _height).
     */
    val CheckIconSize: Dp = 24.dp

    /**
     * Size of the up-down arrow rendered by [DropdownArrowEndAction]
     * (native box of COUI coui_pop_up_next_normal.xml, a 14x24dp vector).
     */
    val ArrowSize: DpSize = DpSize(width = 14.dp, height = 24.dp)

    /**
     * Size of the sub-menu indicator (COUI 17 coui_list_expandable_indicator, a 24dp vector in the
     * 24dp state-icon slot).
     */
    val ChevronSize: DpSize = DpSize(width = 24.dp, height = 24.dp)

    /** Size of the leading icon (COUI coui_popup_list_window_item.xml icon, a 20dp ImageView). */
    val IconMinSize: Dp = 20.dp

    /**
     * Maximum width of the title and description in popup mode
     * (COUI coui_popup_list_window_item_max_width, the maxWidth of both text views).
     */
    val MaxItemTextWidth: Dp = 248.dp

    /**
     * Horizontal padding of each row in popup mode (COUI coui_popup_list_window_item_icon_margin_left,
     * used as both paddingStart and paddingEnd in coui_popup_list_window_item.xml).
     */
    val InsideHorizontalPadding: Dp = 20.dp

    /** Horizontal padding of each row in dialog mode. */
    val DialogHorizontalPadding: Dp = 28.dp

    /**
     * Top/bottom padding of every row in popup mode and in dialog mode
     * (COUI coui_popup_list_window_item_padding_top_and_bottom).
     */
    val MiddleVerticalPadding: Dp = 8.dp

    /**
     * Top padding of a [DropdownHeaderItem] (COUI 17 coui_popup_list_window_header_item.xml
     * paddingTop); its bottom padding is [MiddleVerticalPadding].
     */
    val PopupHeaderTopPadding: Dp = 4.dp

    /** Color of the line inside the group divider (COUI `?couiColorDivider`, #1F000000 / #33FFFFFF). */
    val PopupGroupDividerColor: Color
        @Composable get() = COUITheme.colorScheme.divider

    /** Height of the group divider band (COUI coui_popup_list_group_divider_total_height). */
    val PopupGroupDividerThickness: Dp = 12.dp

    /** Thickness of the line inside the group divider band (COUI coui_popup_list_divider_height). */
    val PopupGroupDividerLineThickness: Dp = 0.5.dp

    /** Side inset of the line inside the group divider band (COUI coui_popup_list_default_divider_margin_horizontal). */
    val PopupGroupDividerMargin: Dp = 20.dp

    /** Size of an icon-only horizontal cell (coui_popup_list_window_horizontal_item_icon_only_width / _height). */
    val HorizontalIconCellSize: Dp = 40.dp

    /** Icon size of an icon-only horizontal cell (coui_popup_list_window_horizontal_item_icon_size_icon_only). */
    val HorizontalIconSize: Dp = 24.dp

    /** Menu-edge margin of icon-only horizontal cells (coui_popup_list_window_horizontal_item_icon_only_edge_margin). */
    val HorizontalIconEdgeMargin: Dp = 12.dp

    /** Spacing of icon-only horizontal cells (coui_popup_list_window_horizontal_item_icon_only_spacing_default). */
    val HorizontalIconSpacing: Dp = 8.dp

    /** Menu-edge margin of labelled horizontal cells (coui_popup_list_window_horizontal_item_text_edge_margin). */
    val HorizontalTextEdgeMargin: Dp = 8.dp

    /** Spacing of labelled horizontal cells (coui_popup_list_window_horizontal_item_text_spacing_default). */
    val HorizontalTextSpacing: Dp = 4.dp

    /** The spacing horizontal cells never shrink below (coui_popup_list_window_horizontal_item_spacing_min). */
    val HorizontalMinSpacing: Dp = 4.dp

    /** Minimum width of a labelled horizontal cell (coui_popup_list_window_horizontal_item_min_width_with_text). */
    val HorizontalTextMinWidth: Dp = 52.dp

    /** Minimum height of a labelled horizontal cell (coui_popup_list_window_horizontal_item_text_min_height). */
    val HorizontalTextMinHeight: Dp = 40.dp

    /** Padding of a labelled horizontal cell (coui_popup_list_window_horizontal_item_text_padding_*). */
    val HorizontalTextPadding: Dp = 6.dp

    /** Gap between the icon and the label of a horizontal cell (coui_popup_list_window_horizontal_item_icon_title_gap). */
    val HorizontalIconTitleGap: Dp = 4.dp

    /** Press-mask radius of an icon-only horizontal cell (coui_popup_list_window_horizontal_mask_radius_icon_only_16_1). */
    val HorizontalIconMaskRadius: Dp = 17.dp

    /** Press-mask radius of a labelled horizontal cell (coui_popup_list_window_horizontal_mask_radius_with_text_16_1). */
    val HorizontalTextMaskRadius: Dp = 23.dp

    /** Padding between the leading icon and the title text (COUI coui_popup_list_window_item_icon_margin_right). */
    val IconEndPadding: Dp = 12.dp

    /**
     * Gap between the title/summary block and the trailing hint or state icon
     * (COUI popup_list_window_item_title_end_gap, an 8dp Space in coui_popup_list_window_item.xml).
     */
    val CheckIconStartPadding: Dp = 8.dp

    /**
     * Maximum width of the trailing hint slot
     * (COUI popup_list_window_item_hint_layout couiMaxWidth in coui_popup_list_window_item.xml).
     */
    val HintMaxWidth: Dp = 40.dp

    /**
     * Padding between the trailing hint slot and the selection indicator
     * (COUI popup_list_window_item_hint_end_gap, a 4dp Space in coui_popup_list_window_item.xml).
     */
    val HintEndPadding: Dp = 4.dp

    /** Title line limit on a row without a summary (COUI MixedPopupListAdapter.setTitleForVertical). */
    val TitleMaxLines: Int = 3

    /** Title line limit on a row that also shows a summary (COUI MixedPopupListAdapter.setTitleForVertical). */
    val TitleWithSummaryMaxLines: Int = 2

    /** Summary line limit (COUI MixedPopupListAdapter.setDescription). */
    val SummaryMaxLines: Int = 2

    /** Group header line limit (COUI coui_popup_list_window_header_item.xml maxLines). */
    val HeaderMaxLines: Int = 2

    /**
     * Default popup row colors, matching COUI 17 coui_popup_list_window_item_tint_selector and
     * coui_popup_list_window_item_status_icon_tint_selector: titles in couiColorLabelPrimary whether
     * or not they are selected, couiColorLabelTertiary when disabled; the check and an open
     * sub menu's indicator in couiColorLabelPrimary, a closed indicator in couiColorLabelTertiary.
     * Summaries always use couiColorLabelSecondary, and row containers stay transparent — the popup
     * surface provides the fill.
     */
    @Composable
    fun dropdownColors(
        contentColor: Color = COUITheme.colorScheme.labelPrimary,
        summaryColor: Color = COUITheme.colorScheme.labelSecondary,
        containerColor: Color = Color.Transparent,
        selectedContentColor: Color = COUITheme.colorScheme.labelPrimary,
        selectedSummaryColor: Color = COUITheme.colorScheme.labelSecondary,
        selectedContainerColor: Color = Color.Transparent,
        selectedIndicatorColor: Color = COUITheme.colorScheme.labelPrimary,
        disabledContentColor: Color = COUITheme.colorScheme.disabledNeutral,
        alertContentColor: Color = COUITheme.colorScheme.error,
        headerColor: Color = COUITheme.colorScheme.labelSecondary,
        submenuIndicatorColor: Color = COUITheme.colorScheme.labelTertiary,
    ): DropdownColors = rememberDropdownColorsImpl(
        contentColor = contentColor,
        summaryColor = summaryColor,
        containerColor = containerColor,
        selectedContentColor = selectedContentColor,
        selectedSummaryColor = selectedSummaryColor,
        selectedContainerColor = selectedContainerColor,
        selectedIndicatorColor = selectedIndicatorColor,
        disabledContentColor = disabledContentColor,
        alertContentColor = alertContentColor,
        headerColor = headerColor,
        submenuIndicatorColor = submenuIndicatorColor,
    )

    @Composable
    fun dialogDropdownColors(
        contentColor: Color = COUITheme.colorScheme.labelPrimary,
        summaryColor: Color = COUITheme.colorScheme.labelSecondaryVariant,
        containerColor: Color = Color.Transparent,
        selectedContentColor: Color = COUITheme.colorScheme.primaryText,
        selectedSummaryColor: Color = COUITheme.colorScheme.primaryText,
        selectedContainerColor: Color = COUITheme.colorScheme.secondary,
        selectedIndicatorColor: Color = COUITheme.colorScheme.primaryText,
        disabledContentColor: Color = COUITheme.colorScheme.disabledNeutral,
        alertContentColor: Color = COUITheme.colorScheme.error,
        headerColor: Color = COUITheme.colorScheme.labelSecondary,
        submenuIndicatorColor: Color = COUITheme.colorScheme.labelTertiary,
    ): DropdownColors = rememberDropdownColorsImpl(
        contentColor = contentColor,
        summaryColor = summaryColor,
        containerColor = containerColor,
        selectedContentColor = selectedContentColor,
        selectedSummaryColor = selectedSummaryColor,
        selectedContainerColor = selectedContainerColor,
        selectedIndicatorColor = selectedIndicatorColor,
        disabledContentColor = disabledContentColor,
        alertContentColor = alertContentColor,
        headerColor = headerColor,
        submenuIndicatorColor = submenuIndicatorColor,
    )
}

@Composable
private fun rememberDropdownColorsImpl(
    contentColor: Color,
    summaryColor: Color,
    containerColor: Color,
    selectedContentColor: Color,
    selectedSummaryColor: Color,
    selectedContainerColor: Color,
    selectedIndicatorColor: Color,
    disabledContentColor: Color,
    alertContentColor: Color,
    headerColor: Color,
    submenuIndicatorColor: Color,
): DropdownColors = remember(
    contentColor,
    summaryColor,
    containerColor,
    selectedContentColor,
    selectedSummaryColor,
    selectedContainerColor,
    selectedIndicatorColor,
    disabledContentColor,
    alertContentColor,
    headerColor,
    submenuIndicatorColor,
) {
    DropdownColors(
        contentColor = contentColor,
        summaryColor = summaryColor,
        containerColor = containerColor,
        selectedContentColor = selectedContentColor,
        selectedSummaryColor = selectedSummaryColor,
        selectedContainerColor = selectedContainerColor,
        selectedIndicatorColor = selectedIndicatorColor,
        disabledContentColor = disabledContentColor,
        alertContentColor = alertContentColor,
        headerColor = headerColor,
        submenuIndicatorColor = submenuIndicatorColor,
    )
}

private val PopupRowModifier = Modifier
    .heightIn(min = DropdownDefaults.PopupItemMinHeight)
    .padding(
        horizontal = DropdownDefaults.InsideHorizontalPadding,
        vertical = DropdownDefaults.MiddleVerticalPadding,
    )

private val DialogRowModifier = Modifier
    .heightIn(min = DropdownDefaults.MinHeight)
    .widthIn(min = DropdownDefaults.MinWidth)
    .fillMaxWidth()
    .padding(horizontal = DropdownDefaults.DialogHorizontalPadding, vertical = DropdownDefaults.MiddleVerticalPadding)

private val TitleWidthModifier = Modifier.widthIn(max = DropdownDefaults.MaxItemTextWidth)

private val StateIconModifier = Modifier.size(DropdownDefaults.CheckIconSize)

private val CheckIconBaseModifier = Modifier
    .padding(start = DropdownDefaults.CheckIconStartPadding)
    .size(DropdownDefaults.CheckIconSize)

// With a hint present the 8dp title-end gap already sits before the hint, so the indicator only
// keeps the 4dp hint-end gap (COUI popup_list_window_item_hint_end_gap).
private val HintedCheckIconModifier = Modifier
    .padding(start = DropdownDefaults.HintEndPadding)
    .size(DropdownDefaults.CheckIconSize)

private val TitleEndGapModifier = Modifier.width(DropdownDefaults.CheckIconStartPadding)

private val HintEndGapModifier = Modifier.width(DropdownDefaults.HintEndPadding)

/**
 * The popup row title style: coui_popup_list_window_item.xml uses the legacy
 * `couiTextAppearanceBodyL` (16sp with the body_l line spacing), one step above the 15sp
 * `couiTextBodyL` that [io.github.suqi8.coui.kmp.theme.TextStyles.bodyL] maps.
 */
@Composable
@ReadOnlyComposable
private fun popupRowTitleStyle(): TextStyle {
    val bodyL = COUITheme.textStyles.bodyL
    return bodyL.copy(fontSize = PopupRowTitleFontSize, lineHeight = bodyL.lineHeight * (PopupRowTitleFontSize.value / bodyL.fontSize.value))
}

private val PopupRowTitleFontSize = 16.sp

private val HintCellModifier = Modifier.widthIn(max = DropdownDefaults.HintMaxWidth)

private val IconCellModifier = Modifier
    .padding(end = DropdownDefaults.IconEndPadding)
    .size(DropdownDefaults.IconMinSize)

private val DialogIconCellModifier = Modifier
    .sizeIn(minWidth = 24.dp, minHeight = 24.dp)
    .padding(end = DropdownDefaults.IconEndPadding)

/**
 * The row of horizontal items COUI 17 can put at the top of a popup menu
 * (`MixedPopupListAdapter.HorizontalViewHolder`, item type 4, laid out by a `GridLayoutManager`):
 * icon-only cells (up to five) or icon-and-label cells (up to three), each with its own rounded
 * press mask. Extra items are dropped, as `MixedPopupListAdapter.setItemList` does.
 *
 * Icon-only cells are 40dp tall with a 24dp icon, 12dp from the menu edges and 8dp apart; labelled
 * cells are at least 52dp wide and 40dp tall, 6dp padded, with a 20dp icon, a 4dp gap and a
 * two-line 12sp label, 8dp from the edges and 4dp apart. The grid has three columns, or as many as
 * there are icon-only cells up to five.
 *
 * Inside a [ListPopupColumn] the cells join the list's shared mask and drag selection like the
 * vertical rows (`COUITouchRecyclerView` treats item view type 0 as selectable): the mask covers the
 * whole cell — an icon-only one capped at 40dp — with a 17dp / 23dp weight-3 corner and slides on
 * the 0.2 response spring COUI uses for mixed lists. Outside a popup list each cell keeps its own
 * press mask.
 *
 * @param items The items; [DropdownItem.icon] draws each icon, [DropdownItem.text] the label (a
 *   blank text makes the row icon-only).
 * @param onItemClick Invoked with the index of a clicked enabled item.
 * @param modifier The modifier applied to the row.
 * @param enabled Whether the row reacts to input; each item's own [DropdownItem.enabled] still applies.
 * @param dropdownColors Colors of the labels.
 */
@Composable
fun DropdownHorizontalItems(
    items: List<DropdownItem>,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    dropdownColors: DropdownColors = DropdownDefaults.dropdownColors(),
) {
    val hasText = items.any { it.text.isNotBlank() }
    val limit = if (hasText) HORIZONTAL_MAX_WITH_TEXT else HORIZONTAL_MAX_ICON_ONLY
    val shown = items.take(limit)
    if (shown.isEmpty()) return
    // createMenuGridLayoutManager: 3 columns with labels, otherwise 3..5 by the item count.
    val columns = if (hasText) 3 else shown.size.coerceIn(3, 5)
    val edge = if (hasText) DropdownDefaults.HorizontalTextEdgeMargin else DropdownDefaults.HorizontalIconEdgeMargin
    val spacing = if (hasText) DropdownDefaults.HorizontalTextSpacing else DropdownDefaults.HorizontalIconSpacing
    val currentOnItemClick by rememberUpdatedState(onItemClick)
    val host = LocalPopupListGestureHost.current
    val handles = remember(shown.size) { List(shown.size) { PopupListItemHandle() } }
    shown.forEachIndexed { index, item ->
        val handle = handles[index]
        handle.horizontal = true
        handle.enabled = enabled && item.enabled
        handle.onClick = { currentOnItemClick(index) }
    }
    if (host != null) {
        DisposableEffect(host, handles) {
            handles.forEach(host::register)
            onDispose { handles.forEach(host::unregister) }
        }
    }
    val rowParentData = remember(handles) { PopupHorizontalRowParentData { handles } }

    Layout(
        modifier = modifier
            .fillMaxWidth()
            .then(if (host != null) rowParentData else Modifier),
        content = {
            shown.forEachIndexed { index, item ->
                DropdownHorizontalCell(
                    item = item,
                    hasText = hasText,
                    enabled = enabled && item.enabled,
                    dropdownColors = dropdownColors,
                    sharedMask = host != null,
                    onClick = { currentOnItemClick(index) },
                )
            }
        },
    ) { measurables, constraints ->
        val edgePx = edge.roundToPx()
        val minSpacing = DropdownDefaults.HorizontalMinSpacing.roundToPx()
        val defaultSpacing = spacing.roundToPx()
        val minCell = if (hasText) DropdownDefaults.HorizontalTextMinWidth.roundToPx() else DropdownDefaults.HorizontalIconCellSize.roundToPx()
        val naturalWidth = 2 * edgePx + columns * minCell + (columns - 1) * defaultSpacing
        val width = if (constraints.hasBoundedWidth) constraints.maxWidth else naturalWidth
        // configHorizontalItemLayout: shrink the spacing (not below 4dp) before the cells.
        val spacingPx = if (columns > 1) {
            ((width - 2 * edgePx - columns * minCell) / (columns - 1)).coerceIn(minSpacing, defaultSpacing)
        } else {
            0
        }
        val cellWidth = ((width - 2 * edgePx - (columns - 1) * spacingPx) / columns).coerceAtLeast(0)
        val placeables = measurables.map { it.measure(Constraints.fixedWidth(cellWidth)) }
        val height = placeables.maxOfOrNull { it.height } ?: 0
        val rowWidth = width.coerceAtLeast(constraints.minWidth)
        val maskRadius = (if (hasText) DropdownDefaults.HorizontalTextMaskRadius else DropdownDefaults.HorizontalIconMaskRadius).toPx()
        val maskCap = if (hasText) 0f else DropdownDefaults.HorizontalIconCellSize.toPx()
        placeables.forEachIndexed { index, placeable ->
            val start = edgePx + index * (cellWidth + spacingPx)
            // Physical bounds for the list's hit test; placeRelative mirrors under RTL.
            val left = if (layoutDirection == LayoutDirection.Rtl) rowWidth - start - cellWidth else start
            handles.getOrNull(index)?.apply {
                this.left = left.toFloat()
                right = (left + cellWidth).toFloat()
                localTop = 0f
                localBottom = placeable.height.toFloat()
                this.maskRadius = maskRadius
                maskHeightCap = maskCap
            }
        }
        layout(rowWidth, height) {
            placeables.forEachIndexed { index, placeable ->
                placeable.placeRelative(edgePx + index * (cellWidth + spacingPx), 0)
            }
        }
    }
}

@Composable
private fun DropdownHorizontalCell(
    item: DropdownItem,
    hasText: Boolean,
    enabled: Boolean,
    dropdownColors: DropdownColors,
    sharedMask: Boolean,
    onClick: () -> Unit,
) {
    val clickAction = onClick
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val press = remember { Animatable(0f) }
    LaunchedEffect(pressed) {
        // COUIMaskEffectDrawable: response 0.3 / bounce 0, touch-enter held to 0.7 before fading.
        if (pressed) {
            press.animateTo(1f, spring(dampingRatio = 1f, stiffness = 438.65f))
        } else {
            if (press.value in 0.001f..0.7f) press.animateTo(0.7f, spring(dampingRatio = 1f, stiffness = 438.65f))
            press.animateTo(0f, spring(dampingRatio = 1f, stiffness = 438.65f))
        }
    }
    val maskColor = COUITheme.colorScheme.hover
    val squircleEnabled = isSquircleEnabled()
    val labelColor = if (enabled) dropdownColors.contentColor else dropdownColors.disabledContentColor
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .heightIn(min = if (hasText) DropdownDefaults.HorizontalTextMinHeight else DropdownDefaults.HorizontalIconCellSize)
            .drawBehind {
                val progress = press.value
                if (progress <= 0f) return@drawBehind
                // configureHorizontalItemMaskPath (> 37): 17dp icon-only / 23dp labelled, weight 3.
                val radius = (if (hasText) DropdownDefaults.HorizontalTextMaskRadius else DropdownDefaults.HorizontalIconMaskRadius).toPx()
                val maskHeight = if (hasText) size.height else minOf(size.height, DropdownDefaults.HorizontalIconCellSize.toPx())
                drawSquircleRect(
                    color = maskColor,
                    cornerRadius = radius,
                    size = Size(size.width, maskHeight),
                    weight = 3f,
                    alpha = progress,
                    squircleEnabled = squircleEnabled,
                )
            }
            .then(
                if (sharedMask) {
                    // The list owns the press and the click; keep the accessibility action.
                    Modifier.semantics(mergeDescendants = true) {
                        role = Role.Button
                        if (enabled) {
                            onClick {
                                clickAction()
                                true
                            }
                        } else {
                            disabled()
                        }
                    }
                } else {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        enabled = enabled,
                        role = Role.Button,
                        onClickLabel = null,
                        onClick = onClick,
                    )
                },
            )
            .padding(if (hasText) DropdownDefaults.HorizontalTextPadding else 0.dp),
    ) {
        item.icon?.invoke(
            Modifier.size(if (hasText) DropdownDefaults.IconMinSize else DropdownDefaults.HorizontalIconSize),
        )
        if (hasText) {
            Text(
                text = item.text,
                modifier = Modifier.padding(top = DropdownDefaults.HorizontalIconTitleGap),
                style = COUITheme.textStyles.bodyXS,
                color = labelColor,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private const val HORIZONTAL_MAX_ICON_ONLY = 5
private const val HORIZONTAL_MAX_WITH_TEXT = 3
