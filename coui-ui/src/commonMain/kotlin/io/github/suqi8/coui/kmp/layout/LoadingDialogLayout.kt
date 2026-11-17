// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.ProgressIndicatorDefaults
import io.github.suqi8.coui.kmp.basic.RotatingProgressIndicator
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * Internal shared layout for the loading dialog variants (COUIRotatingDialogBuilder,
 * `coui_progress_dialog_rotating.xml`): a small centered card holding a rotating spinner
 * and an optional message below it. With a cancel action it takes the cancelable form
 * (`coui_cancelable_progress_dialog_rotating.xml`): an optional title, the spinner, the message
 * and a one-button bar.
 *
 * @param show Whether the dialog is currently shown.
 * @param popupHost A composable that provides the dialog container (e.g., DialogLayout or Dialog).
 * @param modifier The modifier to be applied to the dialog content.
 * @param text The optional message shown below the spinner.
 * @param textColor The color of the message text.
 * @param spinnerColor The color of the spinner.
 * @param backgroundColor The background color of the dialog card.
 * @param enableWindowDim Whether to enable window dimming.
 * @param onDismissRequest Invoked when the user taps outside or presses back. When null,
 *   the dialog cannot be dismissed by the user (matching a non-cancelable COUI dialog).
 * @param onDismissFinished Invoked when the hide animation completes.
 * @param topInset Optional top inset override forwarded to [DialogContentLayout].
 * @param backgroundModifier Applied inside the card's corner clip, below the content.
 * @param materialStroke Whether to draw the COUI 17 material stroke of a blurred card.
 * @param title The optional title above the spinner (`COUIRotatingDialogBuilder.setDialogTitle`).
 * @param titleColor The color of [title].
 * @param cancelAction The optional cancel button (`COUIRotatingDialogBuilder.setCancelButton`).
 *   With it the dialog takes the `COUIAlertDialog.Rotating.Cancelable` form.
 */
@Composable
internal fun LoadingDialogContentLayout(
    show: Boolean,
    popupHost: @Composable (visible: Boolean, content: @Composable () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    backgroundModifier: Modifier = Modifier,
    text: String? = null,
    textColor: Color = Color.Unspecified,
    spinnerColor: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    enableWindowDim: Boolean = true,
    onDismissRequest: (() -> Unit)? = null,
    onDismissFinished: (() -> Unit)? = null,
    topInset: Dp? = null,
    materialStroke: Boolean = false,
    title: String? = null,
    titleColor: Color = Color.Unspecified,
    cancelAction: DialogButtonBarAction? = null,
) {
    val cancelable = cancelAction != null
    val cardWidth = if (cancelable) LoadingDialogDefaults.CancelableCardWidth else LoadingDialogDefaults.CardWidth
    DialogContentLayout(
        show = show,
        titleColor = titleColor,
        summaryColor = Color.Unspecified,
        backgroundColor = backgroundColor,
        outsideMargin = DialogDefaults.outsideMargin,
        // The rotating layout manages its own margins; the card is not padded by the panel.
        insideMargin = DpSize(0.dp, 0.dp),
        popupHost = popupHost,
        modifier = modifier,
        backgroundModifier = backgroundModifier,
        title = null,
        summary = null,
        enableWindowDim = enableWindowDim,
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        // COUI rotating dialogs are always centered with the center-dialog transitions.
        forceCentered = true,
        maxWidth = cardWidth,
        cornerRadius = LoadingDialogDefaults.CornerRadius,
        topInset = topInset,
        materialStroke = materialStroke,
    ) {
        Column(
            modifier = Modifier
                .width(cardWidth)
                // coui_cancelable_progress_dialog_rotating drops the min height of the plain card.
                .heightIn(min = if (cancelable) 0.dp else LoadingDialogDefaults.CardMinHeight),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (title != null) {
                DialogTitle(
                    text = title,
                    color = titleColor,
                    modifier = Modifier
                        .padding(horizontal = DialogDefaults.insideMargin.width)
                        .padding(top = DialogDefaults.insideMargin.height, bottom = LoadingTitleMarginBottom),
                )
            }
            if (text != null || cancelable || title != null) {
                LoadingSpinner(
                    color = spinnerColor,
                    modifier = Modifier.padding(top = SpinnerMarginTop, bottom = SpinnerMarginBottom),
                )
                if (text != null) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = TextMarginHorizontal)
                            .padding(bottom = if (cancelable) TextMarginBottomCancelable else TextMarginBottom),
                        text = text,
                        // progress_tips: couiTextBodyM.
                        style = COUITheme.textStyles.bodyM,
                        textAlign = TextAlign.Center,
                        color = textColor,
                    )
                }
            } else {
                // Without a message the spinner simply centers inside the card.
                LoadingSpinner(color = spinnerColor)
            }
            if (cancelAction != null) {
                DialogButtonBar(
                    negative = cancelAction,
                    positive = null,
                )
            }
        }
    }
}

/** The `coui_rotating_loading.json` spinner at the rotating dialog size. */
@Composable
private fun LoadingSpinner(
    color: Color,
    modifier: Modifier = Modifier,
) {
    RotatingProgressIndicator(
        modifier = modifier,
        color = color,
        size = LoadingDialogDefaults.SpinnerSize,
        ringDiameter = LoadingDialogDefaults.SpinnerRingDiameter,
        strokeWidth = LoadingDialogDefaults.SpinnerStrokeWidth,
    )
}

object LoadingDialogDefaults {
    /**
     * The width of the loading card. COUI `coui_spinner_layout_width` (152dp).
     */
    val CardWidth = 152.dp

    /**
     * The width of the cancelable loading card. COUI `coui_cancelable_progress_dialog_rotating`
     * wraps its content, so the panel takes the `COUIAlertDialog.Rotating.Cancelable`
     * `contentMaxWidth`, `coui_loading_alert_dialog_max_width_cancelable` (180dp).
     */
    val CancelableCardWidth = 180.dp

    /** The minimum height of the loading card. COUI `coui_spinner_layout_min_height` (120dp). */
    val CardMinHeight = 120.dp

    /**
     * The corner radius of the loading card. `coui_rotate_alert_dialog_layout` is a
     * `COUIAlertDialogMaxLinearLayout` without `clip_radius_root`, so it takes the regular dialog
     * radius, `coui_round_corner_xxxxl_radius_large_16_1` (45dp) on COUI 17.
     */
    val CornerRadius = DialogDefaults.CornerRadius

    /**
     * The size of the rotating spinner. COUI `coui_spinner_loading_anim_width/height` (36dp).
     */
    val SpinnerSize = 36.dp

    /**
     * The ring diameter of the rotating spinner: the 78-unit ring of the 84-unit
     * `coui_rotating_loading.json` comp scaled to [SpinnerSize].
     */
    val SpinnerRingDiameter = 33.43.dp

    /**
     * The stroke width of the rotating spinner: the 6-unit stroke of the 84-unit
     * `coui_rotating_loading.json` comp scaled to [SpinnerSize].
     */
    val SpinnerStrokeWidth = 2.57.dp

    /**
     * The default color of the message below the spinner. COUI `progress_tips` uses
     * `?couiColorLabelSecondary`.
     */
    @Composable
    fun textColor() = COUITheme.colorScheme.labelSecondary

    /**
     * The default color of the spinner, the fixed stroke of `coui_rotating_loading.json`
     * (see [ProgressIndicatorDefaults.rotatingProgressIndicatorColor]).
     */
    @Composable
    fun spinnerColor() = ProgressIndicatorDefaults.rotatingProgressIndicatorColor()
}

/** COUI coui_spinner_margin_top. */
private val SpinnerMarginTop = 28.dp

/** COUI coui_spinner_margin_bottom. */
private val SpinnerMarginBottom = 12.dp

/** COUI coui_spinner_text_margin_bottom. */
private val TextMarginBottom = 28.dp

/** COUI coui_cancelable_progress_dialog_rotating: the message bottom margin above the button bar. */
private val TextMarginBottomCancelable = 16.dp

/** COUI coui_alert_dialog_title_margin_bottom (COUIAlertDialogTitleTemplateStyle). */
private val LoadingTitleMarginBottom = 6.dp

/** COUI coui_spinner_text_margin_horizontal. */
private val TextMarginHorizontal = 16.dp
