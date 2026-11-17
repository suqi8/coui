// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.layout

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.Button
import io.github.suqi8.coui.kmp.basic.ButtonDefaults
import io.github.suqi8.coui.kmp.basic.CardListPosition
import io.github.suqi8.coui.kmp.basic.Checkbox
import io.github.suqi8.coui.kmp.basic.FullPageStatementDefaults
import io.github.suqi8.coui.kmp.basic.HorizontalDivider
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.verticalFadingEdges
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * One row of the icon list a user statement dialog can show under the app name
 * (COUIUserStatementDialog.COUIUserStatementListItem, coui_component_statement_list_item.xml).
 *
 * @param title The row title.
 * @param summary The text below the title. Hidden when null or empty.
 * @param icon The 36dp icon at the start of the row. Hidden when null.
 */
@Stable
data class UserStatementListItem(
    val title: String,
    val summary: String? = null,
    val icon: Painter? = null,
)

/**
 * One optional function a user can opt into in an individual statement dialog
 * (COUIIndividualStatementDialog `PrivacyItem`, shown as a COUICheckBoxItemView row).
 *
 * @param title The row title.
 * @param summary The text below the title. Hidden when null.
 */
@Stable
data class StatementPrivacyItem(
    val title: String,
    val summary: String? = null,
)

/** Default values used by the user statement dialogs. */
object UserStatementDialogDefaults {

    /** Default confirm label (COUI `coui_allow_text`). */
    val ConfirmText = "Agree and continue"

    /** Default exit label (COUI `coui_reject_text`). */
    val ExitText = "Exit"
}

/** Default values used by the individual statement dialogs. */
object IndividualStatementDialogDefaults {

    /** Default confirm label (COUI `coui_allow_text`). */
    val ConfirmText = "Agree and continue"

    /** Default exit label (COUI `coui_reject_text`). */
    val ExitText = "Exit"
}

/** Start padding of the user statement content (coui_component_statement_panel_start_padding). */
private val UserPanelStartPadding = 24.dp

/**
 * End padding of the user statement content: coui_component_statement_panel_end_padding 6dp
 * around the scrollbar plus coui_component_statement_panel_content_item_end_padding 18dp.
 */
private val UserPanelEndPadding = 6.dp
private val UserContentEndPadding = 18.dp

/** Top padding of the logo block (coui_component_statement_margin_top_min). */
private val UserLogoPaddingTop = 40.dp

/**
 * Top padding of the logo block when the dialog shows only a message
 * (coui_component_statement_margin_top_small_screen_max / _big_screen_max).
 */
private val UserLogoPaddingTopCompact = 110.dp
private val UserLogoPaddingTopExpanded = 80.dp

/** Logo size and corner (coui_component_statement_logo_width / height, couiRoundCornerXL). */
private val UserLogoSize = 80.dp
private val UserLogoCornerRadius = 20.dp

/** Gap above the title (coui_component_statement_logo_subtitle_margin). */
private val UserTitlePaddingTop = 16.dp

/** Gap above the app name (coui_component_statement_logo_name_margin_top). */
private val UserAppNameMarginTop = 4.dp

/** App name size (`couiTextAppearanceHeadline2`, 28sp medium). */
private val UserAppNameFontSize = 28.sp

/** Gap above the message (coui_component_statement_logo_message_margin_top). */
private val UserMessagePaddingTop = 8.dp

/** Gap above the list or the custom content (coui_component_statement_custom_padding_top). */
private val UserCustomPaddingTop = 16.dp

/** Statement / protocol text size and spacing (coui_component_statement_protocol_text_size, x1.2). */
private val UserStatementFontSize = 10.sp
private val UserStatementLineHeight = 1.2.em

/** Margins around the statement scroller (coui_component_statement_scroll_text_margin_top / _bottom). */
private val UserStatementMarginTop = 16.dp
private val UserStatementMarginBottom = 12.dp

/**
 * Maximum height of the statement scroller: coui_component_statement_scroll_text_height_normal,
 * or _height_max on compact windows when only a message is shown.
 */
private val UserStatementMaxHeight = 80.dp
private val UserStatementMaxHeightMessageOnly = 150.dp

/** Button margins (coui_horizontal_single_btn_margin, coui_component_statement_confirm/cancel_margin_top). */
private val StatementButtonMarginHorizontal = 32.dp
private val StatementButtonMarginTop = 16.dp

/** Bottom margin of the last button (coui_component_bottom_sheet_margin). */
private val StatementBottomMargin = 24.dp

/** List row metrics (coui_component_statement_list_*). */
private val ListItemMarginHorizontal = 16.dp
private val ListItemIconSize = 36.dp
private val ListItemTextMarginStart = 16.dp
private val ListItemTextPaddingVertical = 10.dp
private val ListItemSummaryMarginTop = 4.dp
private val ListItemTitleFontSize = 16.sp

/** Individual statement title margins (COUIComponentStatementTitleStyle). */
private val IndividualTitleMarginTop = 18.dp
private val IndividualTitleMarginBottom = 12.dp
private val IndividualHorizontalPadding = 24.dp

/** Statement scroller max height (scrollViewMaxHeight 384dp). */
private val IndividualScrollMaxHeight = 384.dp

/** coui_component_individual_padding_top / _bottom, and their _with_checkbox variants. */
private val IndividualPaddingTop = 12.dp
private val IndividualPaddingTopWithItems = 4.dp
private val IndividualPaddingBottom = 16.dp
private val IndividualPaddingBottomWithItems = 12.dp

/** Privacy row margins (coui_component_layout_privacy_checkbox.xml). */
private val PrivacyRowPaddingStart = 24.dp
private val PrivacyRowPaddingEnd = 20.dp
private val PrivacyRowPaddingVertical = 10.dp

/** Confirm button widths (coui_component_statement_button_width / _large_button_width). */
private val IndividualButtonWidthCompact = 220.dp
private val IndividualButtonWidth = 280.dp

/** Confirm button vertical margins in coui_component_full_page_function_privacy.xml. */
private val IndividualButtonMarginVertical = 16.dp

/** Side-by-side buttons in small landscape windows (small_land_button_layout). */
private val SmallLandButtonPaddingHorizontal = 24.dp
private val SmallLandButtonSpacing = 8.dp

/** Smallest window width below which the statement dialogs use their small-screen forms. */
private val StatementSmallScreenWidth = 480.dp

/** Window width from which COUIResponsiveUtils no longer treats the window as small. */
private val StatementCompactWidth = 600.dp

/**
 * Content of a user statement dialog (coui_component_full_page_statement_with_protocol.xml with
 * coui_component_statement_content_item.xml): a scrollable header (logo, title, app name and either
 * a message, an icon list or custom content), the statement + protocol text in its own short
 * scroller, then a confirm button and an exit button.
 */
@Composable
internal fun UserStatementDialogContent(
    onConfirm: () -> Unit,
    onExit: () -> Unit,
    logo: Painter?,
    title: String?,
    appName: String?,
    appMessage: String?,
    listItems: List<UserStatementListItem>,
    onListItemClick: ((index: Int) -> Unit)?,
    statement: AnnotatedString?,
    protocolText: AnnotatedString?,
    confirmText: String,
    exitText: String,
    customContent: (@Composable () -> Unit)?,
) {
    val windowWidth = LocalWindowInfo.current.containerDpSize.width
    val compact = windowWidth < StatementCompactWidth
    val messageOnly = customContent == null && listItems.isEmpty()
    val headerScrollState = rememberScrollState()
    val statementScrollState = rememberScrollState()
    // COUIUserStatementDialog appends the protocol text to the statement.
    val statementText = remember(statement, protocolText) {
        if (statement == null && protocolText == null) {
            null
        } else {
            buildAnnotatedString {
                statement?.let { append(it) }
                protocolText?.let { append(it) }
            }
        }
    }
    val secondaryColor = COUITheme.colorScheme.labelSecondary
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(start = UserPanelStartPadding, end = UserPanelEndPadding + UserContentEndPadding),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalFadingEdges(headerScrollState, FullPageStatementDefaults.ScrollFadeLength)
                    .verticalScroll(headerScrollState)
                    .padding(
                        top = when {
                            !messageOnly -> UserLogoPaddingTop
                            compact -> UserLogoPaddingTopCompact
                            else -> UserLogoPaddingTopExpanded
                        },
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (logo != null && customContent == null) {
                    Image(
                        painter = logo,
                        contentDescription = null,
                        modifier = Modifier
                            .size(UserLogoSize)
                            .clip(RoundedCornerShape(UserLogoCornerRadius)),
                        contentScale = ContentScale.Crop,
                    )
                }
                if (title != null) {
                    Text(
                        text = title,
                        modifier = Modifier.padding(top = if (customContent == null) UserTitlePaddingTop else 0.dp),
                        style = COUITheme.textStyles.headlineXS,
                        color = COUITheme.colorScheme.labelPrimary,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (appName != null) {
                    Text(
                        text = appName,
                        modifier = Modifier.padding(top = UserAppNameMarginTop),
                        style = COUITheme.textStyles.displayXS,
                        fontSize = UserAppNameFontSize,
                        fontWeight = FontWeight.Medium,
                        color = COUITheme.colorScheme.labelPrimary,
                        textAlign = TextAlign.Center,
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = if (messageOnly) UserMessagePaddingTop else UserCustomPaddingTop),
                ) {
                    when {
                        customContent != null -> customContent()

                        listItems.isNotEmpty() -> Column(modifier = Modifier.fillMaxWidth()) {
                            listItems.forEachIndexed { index, item ->
                                UserStatementListRow(
                                    item = item,
                                    onClick = onListItemClick?.let { click -> { click(index) } },
                                )
                            }
                        }

                        appMessage != null -> Text(
                            text = appMessage,
                            modifier = Modifier.fillMaxWidth(),
                            style = COUITheme.textStyles.bodyS,
                            color = secondaryColor,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
            if (statementText != null && statementText.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .padding(top = UserStatementMarginTop, bottom = UserStatementMarginBottom)
                        .fillMaxWidth()
                        .heightIn(
                            max = if (messageOnly && compact) UserStatementMaxHeightMessageOnly else UserStatementMaxHeight,
                        )
                        .verticalFadingEdges(statementScrollState, FullPageStatementDefaults.ScrollFadeLength)
                        .verticalScroll(statementScrollState),
                ) {
                    Text(
                        text = statementText,
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = UserStatementFontSize,
                        lineHeight = UserStatementLineHeight,
                        color = secondaryColor,
                    )
                }
            }
        }
        StatementDialogButton(
            text = confirmText,
            onClick = onConfirm,
            primary = true,
            modifier = Modifier
                .padding(top = StatementButtonMarginTop)
                .padding(horizontal = StatementButtonMarginHorizontal)
                .fillMaxWidth(),
        )
        StatementDialogButton(
            text = exitText,
            onClick = onExit,
            primary = false,
            modifier = Modifier
                .padding(top = StatementButtonMarginTop, bottom = StatementBottomMargin)
                .padding(horizontal = StatementButtonMarginHorizontal)
                .fillMaxWidth(),
        )
    }
}

@Composable
private fun UserStatementListRow(
    item: UserStatementListItem,
    onClick: (() -> Unit)?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier)
            .padding(horizontal = ListItemMarginHorizontal),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (item.icon != null) {
            Image(
                painter = item.icon,
                contentDescription = null,
                modifier = Modifier.size(ListItemIconSize),
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = ListItemTextMarginStart)
                .padding(vertical = ListItemTextPaddingVertical),
        ) {
            Text(
                text = item.title,
                fontSize = ListItemTitleFontSize,
                fontWeight = FontWeight.Medium,
                color = COUITheme.colorScheme.labelPrimary,
            )
            if (!item.summary.isNullOrEmpty()) {
                Text(
                    text = item.summary,
                    modifier = Modifier.padding(top = ListItemSummaryMarginTop),
                    style = COUITheme.textStyles.bodyM,
                    color = COUITheme.colorScheme.labelSecondary,
                )
            }
        }
    }
}

/**
 * Content of an individual statement dialog (coui_component_full_page_function_privacy.xml): a
 * centered title, the statement text plus optional opt-in rows in a scroller of at most 384dp, a
 * confirm button that stays disabled until a row is checked, and an exit text button. Small
 * landscape windows show exit and confirm side by side.
 */
@Composable
internal fun IndividualStatementDialogContent(
    show: Boolean,
    onConfirm: (checkedItems: List<StatementPrivacyItem>) -> Unit,
    onExit: () -> Unit,
    title: String?,
    statement: AnnotatedString?,
    privacyItems: List<StatementPrivacyItem>,
    confirmText: String,
    exitText: String,
) {
    val windowSize = LocalWindowInfo.current.containerDpSize
    val smallScreen = min(windowSize.width, windowSize.height) < StatementSmallScreenWidth
    val smallLand = smallScreen && windowSize.width > windowSize.height
    val checked = remember(privacyItems) { mutableStateListOf<Boolean>().apply { repeat(privacyItems.size) { add(false) } } }
    LaunchedEffect(show, privacyItems) {
        if (show) {
            for (i in checked.indices) checked[i] = false
        }
    }
    val hasItems = privacyItems.isNotEmpty()
    // COUIIndividualStatementDialog.resetBottomButton: with opt-in rows, confirm needs one checked.
    val confirmEnabled = !hasItems || checked.any { it }
    val confirm = { onConfirm(privacyItems.filterIndexed { index, _ -> checked.getOrElse(index) { false } }) }
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxWidth()) {
        if (title != null) {
            Text(
                text = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = IndividualTitleMarginTop, bottom = IndividualTitleMarginBottom)
                    .padding(horizontal = IndividualHorizontalPadding),
                style = COUITheme.textStyles.headlineS,
                color = COUITheme.colorScheme.labelPrimary,
                textAlign = TextAlign.Center,
            )
        }
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .fillMaxWidth()
                .heightIn(max = IndividualScrollMaxHeight)
                .verticalFadingEdges(scrollState, FullPageStatementDefaults.ScrollFadeLength)
                .verticalScroll(scrollState)
                .padding(top = if (hasItems) IndividualPaddingTopWithItems else IndividualPaddingTop),
        ) {
            if (statement != null) {
                Text(
                    text = statement,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = IndividualHorizontalPadding)
                        .padding(bottom = if (hasItems) IndividualPaddingBottomWithItems else IndividualPaddingBottom),
                    // ORIGIN_STATEMENT_TEXT_SIZE 14sp, 12sp once opt-in rows are added.
                    style = if (hasItems) COUITheme.textStyles.bodyXS else COUITheme.textStyles.bodyM,
                    color = COUITheme.colorScheme.labelSecondary,
                )
            }
            privacyItems.forEachIndexed { index, item ->
                val isChecked = checked.getOrElse(index) { false }
                val toggle: () -> Unit = {
                    if (index < checked.size) {
                        checked[index] = !checked[index]
                    }
                }
                BasicComponent(
                    title = item.title,
                    summary = item.summary,
                    endActions = {
                        Checkbox(
                            state = ToggleableState(isChecked),
                            onClick = null,
                        )
                    },
                    insideMargin = PaddingValues(
                        start = PrivacyRowPaddingStart,
                        end = PrivacyRowPaddingEnd,
                        top = PrivacyRowPaddingVertical,
                        bottom = PrivacyRowPaddingVertical,
                    ),
                    cardListPosition = CardListPosition.None,
                    onClick = toggle,
                    role = Role.Checkbox,
                )
                if (index != privacyItems.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = PrivacyRowPaddingStart, end = PrivacyRowPaddingEnd),
                    )
                }
            }
        }
        if (smallLand) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = StatementBottomMargin)
                    .padding(horizontal = SmallLandButtonPaddingHorizontal),
                horizontalArrangement = Arrangement.spacedBy(SmallLandButtonSpacing),
            ) {
                StatementDialogButton(
                    text = exitText,
                    onClick = onExit,
                    primary = false,
                    modifier = Modifier.weight(1f),
                )
                StatementDialogButton(
                    text = confirmText,
                    onClick = confirm,
                    primary = true,
                    enabled = confirmEnabled,
                    modifier = Modifier.weight(1f),
                )
            }
        } else {
            StatementDialogButton(
                text = confirmText,
                onClick = confirm,
                primary = true,
                enabled = confirmEnabled,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = IndividualButtonMarginVertical)
                    .width(if (smallScreen) IndividualButtonWidthCompact else IndividualButtonWidth),
            )
            Text(
                text = exitText,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = StatementBottomMargin)
                    .clickable(role = Role.Button, onClick = onExit),
                style = COUITheme.textStyles.buttonL,
                color = COUITheme.colorScheme.primary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/** A statement action button whose label wraps to two lines at most (`android:maxLines="2"`). */
@Composable
private fun StatementDialogButton(
    text: String,
    onClick: () -> Unit,
    primary: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = if (primary) ButtonDefaults.buttonColorsPrimary() else ButtonDefaults.buttonColors(),
    ) {
        Text(
            text = text,
            style = COUITheme.textStyles.buttonL,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
