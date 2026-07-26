// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

package com.suqi8.coui.kmp.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.suqi8.coui.kmp.basic.ButtonDefaults
import com.suqi8.coui.kmp.basic.Checkbox
import com.suqi8.coui.kmp.basic.Text
import com.suqi8.coui.kmp.basic.TextButton
import com.suqi8.coui.kmp.theme.COUITheme

/**
 * Internal shared layout for the security dialog variants (COUISecurityAlertDialogBuilder,
 * `coui_security_alert_dialog_statement_or_checkbox.xml`): a regular alert dialog extended
 * with a statement paragraph (with an optional inline link), a "don't remind me again"
 * checkbox row and a two-button bar.
 *
 * @param show Whether the dialog is currently shown.
 * @param popupHost A composable that provides the dialog container (e.g., DialogLayout or Dialog).
 * @param onConfirm Invoked when the confirm button is clicked, with the current checkbox state.
 * @param onCancel Invoked when the cancel button is clicked, the user taps outside, or presses
 *   back (COUI reports the back key as the negative selection).
 * @param modifier The modifier to be applied to the dialog content.
 * @param title The title of the dialog.
 * @param summary The summary (message) of the dialog.
 * @param statement The statement paragraph. Hidden when null.
 * @param statementLinkText The substring of [statement] rendered as a tappable link.
 * @param onLinkClick Invoked when the statement link is clicked.
 * @param checkboxText The label of the checkbox row. Hidden when null.
 * @param initialChecked The initial checkbox state, re-applied each time the dialog is shown.
 * @param confirmText The label of the confirm (positive) button.
 * @param cancelText The label of the cancel (negative) button.
 * @param titleColor The color of the title.
 * @param summaryColor The color of the summary.
 * @param backgroundColor The background color of the dialog.
 * @param colors The [SecurityDialogColors] of the statement, link and checkbox texts.
 * @param enableWindowDim Whether to enable window dimming.
 * @param onDismissFinished Invoked when the hide animation completes.
 * @param topInset Optional top inset override forwarded to [DialogContentLayout].
 */
@Composable
internal fun SecurityDialogContentLayout(
    show: Boolean,
    popupHost: @Composable (visible: Boolean, content: @Composable () -> Unit) -> Unit,
    onConfirm: (dontRemind: Boolean) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    summary: String? = null,
    statement: String? = null,
    statementLinkText: String? = null,
    onLinkClick: (() -> Unit)? = null,
    checkboxText: String? = SecurityDialogDefaults.CheckboxText,
    initialChecked: Boolean = false,
    confirmText: String = SecurityDialogDefaults.ConfirmText,
    cancelText: String = SecurityDialogDefaults.CancelText,
    titleColor: Color = Color.Unspecified,
    summaryColor: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    colors: SecurityDialogColors = SecurityDialogDefaults.securityDialogColors(),
    enableWindowDim: Boolean = true,
    onDismissFinished: (() -> Unit)? = null,
    topInset: Dp? = null,
) {
    val checked = remember { mutableStateOf(initialChecked) }
    val currentInitialChecked by rememberUpdatedState(initialChecked)
    val currentOnConfirm by rememberUpdatedState(onConfirm)
    val currentOnCancel by rememberUpdatedState(onCancel)
    val currentOnLinkClick by rememberUpdatedState(onLinkClick)

    LaunchedEffect(show) {
        // COUISecurityAlertDialogBuilder.setChecked applies the initial state on each show.
        if (show) checked.value = currentInitialChecked
    }

    val requestCancel = remember { { currentOnCancel() } }

    DialogContentLayout(
        show = show,
        titleColor = titleColor,
        summaryColor = summaryColor,
        backgroundColor = backgroundColor,
        outsideMargin = DialogDefaults.outsideMargin,
        insideMargin = DialogDefaults.insideMargin,
        popupHost = popupHost,
        modifier = modifier,
        title = title,
        summary = summary,
        enableWindowDim = enableWindowDim,
        onDismissRequest = requestCancel,
        onDismissFinished = onDismissFinished,
        topInset = topInset,
    ) {
        if (statement != null) {
            val linkColor = colors.linkColor
            val statementText = remember(statement, statementLinkText, linkColor) {
                buildAnnotatedString {
                    val link = statementLinkText.takeUnless { it.isNullOrEmpty() }
                    val linkIndex = link?.let { statement.indexOf(it) } ?: -1
                    if (link == null || linkIndex < 0) {
                        append(statement)
                    } else {
                        append(statement.substring(0, linkIndex))
                        // COUIClickableSpan: couiColorLink at rest, 30% alpha while pressed
                        // (coui_clickable_text_color_pressed #4d2660f5), no underline.
                        val linkStyles = TextLinkStyles(
                            style = SpanStyle(color = linkColor),
                            pressedStyle = SpanStyle(color = linkColor.copy(alpha = linkColor.alpha * 0.3f)),
                        )
                        withLink(
                            LinkAnnotation.Clickable(tag = "statement-link", styles = linkStyles) {
                                currentOnLinkClick?.invoke()
                            },
                        ) {
                            append(link)
                        }
                        append(statement.substring(linkIndex + link.length))
                    }
                }
            }
            Text(
                // coui_security_alertdialog_statement: 12sp couiColorSecondNeutral, 10dp top /
                // 6dp bottom margins, lineSpacingMultiplier 1.2, start-aligned, 24dp side
                // margins (the dialog inside margin width).
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DialogDefaults.insideMargin.width)
                    .padding(top = StatementMarginTop, bottom = StatementMarginBottom),
                text = statementText,
                fontSize = StatementFontSize,
                textAlign = TextAlign.Start,
                lineHeight = StatementLineHeight,
                color = colors.statementColor,
            )
        }
        if (checkboxText != null) {
            val hapticFeedback = LocalHapticFeedback.current
            val currentHapticFeedback by rememberUpdatedState(hapticFeedback)
            Row(
                // COUICheckBox row starts 20dp from the panel edge
                // (coui_alert_dialog_checkbox_margin_left) so the 24dp drawable's inner ring
                // lines up with the 24dp text edge.
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = CheckboxRowMarginStart, end = DialogDefaults.insideMargin.width)
                    .toggleable(
                        value = checked.value,
                        interactionSource = null,
                        indication = null,
                        role = Role.Checkbox,
                        onValueChange = {
                            checked.value = it
                            currentHapticFeedback.performHapticFeedback(
                                if (it) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff,
                            )
                        },
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    state = ToggleableState(checked.value),
                    onClick = null,
                )
                Text(
                    // COUICheckBox text: 12sp couiColorSecondNeutral, 8dp from the drawable
                    // (coui_checkbox_margin_between_text_drawable).
                    modifier = Modifier.padding(start = CheckboxTextGap),
                    text = checkboxText,
                    fontSize = CheckboxFontSize,
                    color = colors.checkboxTextColor,
                )
            }
        }
        Row(
            // COUI horizontal button bar (COUIButtonBarLayout): spans the full panel width with
            // a 58dp min height (coui_alert_dialog_button_height); the gap above it comes from
            // the custom panel's 8dp bottom padding (coui_alert_dialog_customer_layout_padding_bottom)
            // and the panel bottom inset is carried by the buttons' own 22dp bottom padding.
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = ButtonBarMarginTop)
                .height(IntrinsicSize.Min),
        ) {
            TextButton(
                text = cancelText,
                onClick = requestCancel,
                modifier = Modifier.weight(1f).fillMaxHeight(),
                minHeight = ButtonBarMinHeight,
                insideMargin = DialogButtonInsideMargin,
                colors = ButtonDefaults.textButtonColorsBorderless(),
            )
            Box(
                // COUIAlertDialogBottomButtonDivider: 0.33dp couiColorDivider hairline inset
                // 12dp from the bar top and 21dp from the bar bottom.
                modifier = Modifier
                    .padding(top = ButtonDividerInsetTop, bottom = ButtonDividerInsetBottom)
                    .width(ButtonDividerWidth)
                    .fillMaxHeight()
                    .background(COUITheme.colorScheme.dividerLine),
            )
            TextButton(
                text = confirmText,
                onClick = remember { { currentOnConfirm(checked.value) } },
                modifier = Modifier.weight(1f).fillMaxHeight(),
                minHeight = ButtonBarMinHeight,
                insideMargin = DialogButtonInsideMargin,
                colors = ButtonDefaults.textButtonColorsBorderless(),
            )
        }
    }
}

object SecurityDialogDefaults {
    /** The default checkbox label. COUI `coui_security_alertdialog_checkbox_msg`. */
    val CheckboxText = "Don't remind me again"

    /** The default confirm (positive) button label. */
    val ConfirmText = "OK"

    /** The default cancel (negative) button label. */
    val CancelText = "Cancel"

    /**
     * The default [SecurityDialogColors]. Statement and checkbox use couiColorSecondNeutral
     * (secondary label) and the inline link uses couiColorLink (accent).
     */
    @Composable
    fun securityDialogColors(
        statementColor: Color = COUITheme.colorScheme.onBackgroundVariant,
        linkColor: Color = COUITheme.colorScheme.primary,
        checkboxTextColor: Color = COUITheme.colorScheme.onBackgroundVariant,
    ): SecurityDialogColors = remember(statementColor, linkColor, checkboxTextColor) {
        SecurityDialogColors(
            statementColor = statementColor,
            linkColor = linkColor,
            checkboxTextColor = checkboxTextColor,
        )
    }
}

@Immutable
data class SecurityDialogColors(
    val statementColor: Color,
    val linkColor: Color,
    val checkboxTextColor: Color,
)

/** COUI coui_security_alert_dialog_statement_text_size. */
private val StatementFontSize = 12.sp

/** COUI coui_security_alert_dialog_checkbox_text_size. */
private val CheckboxFontSize = 12.sp

/**
 * COUI statement lineSpacingMultiplier is 1.2; with sans-serif font metrics
 * (ascent + descent = 1.172 x size) that resolves to ~1.41 x fontSize.
 */
private val StatementLineHeight = 1.41f.em

/** COUI statement android:layout_marginTop. */
private val StatementMarginTop = 10.dp

/** COUI statement android:layout_marginBottom. */
private val StatementMarginBottom = 6.dp

/** COUI coui_alert_dialog_checkbox_margin_left. */
private val CheckboxRowMarginStart = 20.dp

/** COUI coui_checkbox_margin_between_text_drawable. */
private val CheckboxTextGap = 8.dp

/**
 * COUI coui_alert_dialog_customer_layout_padding_bottom: the custom panel (statement/checkbox)
 * pads 8dp before the horizontal button bar, which itself carries no top margin.
 */
private val ButtonBarMarginTop = 8.dp

/** COUI coui_alert_dialog_button_height: the min height of the horizontal button bar. */
private val ButtonBarMinHeight = 58.dp

/**
 * COUI dialog bar buttons pad 24dp horizontally (coui_alert_dialog_button_horizontal_padding)
 * and 12dp top / 22dp bottom (coui_bottom_alert_dialog_horizontal_button_padding_top/
 * bottom_extra_new), so the panel bottom inset is carried by the buttons themselves.
 */
private val DialogButtonInsideMargin = PaddingValues(start = 24.dp, top = 12.dp, end = 24.dp, bottom = 22.dp)

/** COUI coui_delete_alert_dialog_divider_height_verticalbutton: 0.33dp hairline. */
private val ButtonDividerWidth = 0.33.dp

/** COUI coui_bottom_alert_dialog_horizontal_button_padding_top_extra_new (divider top margin). */
private val ButtonDividerInsetTop = 12.dp

/** COUI coui_bottom_alert_dialog_horizontal_button_padding_bottom_extra_divider_new. */
private val ButtonDividerInsetBottom = 21.dp
