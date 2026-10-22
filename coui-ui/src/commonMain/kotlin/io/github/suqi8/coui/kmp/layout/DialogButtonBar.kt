// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.layout

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.TextButton
import io.github.suqi8.coui.kmp.basic.TextButtonColors
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * A single action in a [DialogButtonBar], addressed by role rather than position because
 * `COUIButtonBarLayout.resortButton` reorders the buttons when the bar stacks.
 *
 * @param text The button label.
 * @param enabled Whether the button is enabled.
 * @param onClick Invoked when the button is clicked.
 */
@Stable
data class DialogButtonBarAction(
    val text: String,
    val enabled: Boolean = true,
    val onClick: () -> Unit,
)

/**
 * A COUI 17 dialog button bar (`COUIButtonBarLayout` around three `COUILoadingButton`s styled
 * `COUIAlertDialogBottomButtonSecondary`): filled capsule buttons that automatically flip between a
 * side-by-side row and a vertical stack.
 *
 * `COUIButtonBarLayout.onMeasure` keeps the bar horizontal only when there are exactly two buttons
 * and every label fits its cell; in every other case it stacks. In both tiers each button sits in
 * a cell padded 16dp from the panel edges with 8dp between buttons
 * (`coui_alert_dialog_button_parent_padding_*`), and the bottom-most cell carries the 16dp panel
 * bottom inset. See [DialogButtonBarDefaults] for the metrics.
 *
 * The recommend-button tier (`setRecommendButtonId`, a primary-filled button) is not implemented;
 * this bar always behaves as `mRecommendButtonId == NO_RECOMMEND_ID`.
 *
 * @param negative The negative (cancel) action. Hidden when null.
 * @param positive The positive (confirm) action. Hidden when null.
 * @param modifier The modifier to be applied to the bar.
 * @param neutral The neutral (third) action, drawn in the error color
 *   (`COUIAlertDialogBottomButtonSecondary.Last`). Hidden when null.
 * @param dynamicLayout Whether the bar may stack itself, like
 *   `COUIButtonBarLayout.setDynamicLayout`.
 * @param colors The [TextButtonColors] of the negative and positive buttons.
 * @param neutralColors The [TextButtonColors] of the neutral button.
 * @param textStyle The text style of the labels, also used to measure them for the flip decision.
 */
@Composable
fun DialogButtonBar(
    negative: DialogButtonBarAction?,
    positive: DialogButtonBarAction?,
    modifier: Modifier = Modifier,
    neutral: DialogButtonBarAction? = null,
    dynamicLayout: Boolean = true,
    colors: TextButtonColors = DialogButtonBarDefaults.buttonColors(),
    neutralColors: TextButtonColors = DialogButtonBarDefaults.neutralButtonColors(),
    textStyle: TextStyle = COUITheme.textStyles.button,
) {
    if (negative == null && positive == null && neutral == null) return

    BoxWithConstraints(modifier) {
        val textMeasurer = rememberTextMeasurer()
        val density = LocalDensity.current
        // COUI clamps the measured bar width to coui_dialog_max_width before deciding.
        val barWidth = minOf(maxWidth, DialogDefaults.MaxWidth)
        val buttonCount = listOfNotNull(negative, positive, neutral).size

        val stacked = remember(
            barWidth,
            buttonCount,
            dynamicLayout,
            textStyle,
            density,
            negative?.text,
            positive?.text,
            neutral?.text,
        ) {
            dynamicLayout &&
                (
                    buttonCount != 2 ||
                        !labelsFit(
                            labels = listOfNotNull(positive?.text, negative?.text, neutral?.text),
                            availableWidth = rowLabelWidth(barWidth, buttonCount),
                            textStyle = textStyle,
                            textMeasurer = textMeasurer,
                            density = density,
                        )
                    )
        }

        if (stacked) {
            StackedButtonBar(
                negative = negative,
                positive = positive,
                neutral = neutral,
                barWidth = barWidth,
                colors = colors,
                neutralColors = neutralColors,
                textStyle = textStyle,
                textMeasurer = textMeasurer,
                density = density,
            )
        } else {
            RowButtonBar(
                negative = negative,
                positive = positive,
                neutral = neutral,
                colors = colors,
                neutralColors = neutralColors,
                textStyle = textStyle,
            )
        }
    }
}

/**
 * The label width a row cell leaves, after `COUIButtonBarLayout.getHorizontalButtonContentWidth`:
 * the bar width split evenly between the buttons, minus each button's two horizontal paddings and
 * the cell's edge and central paddings.
 */
private fun rowLabelWidth(barWidth: Dp, buttonCount: Int): Dp = if (buttonCount <= 0) {
    0.dp
} else {
    barWidth / buttonCount -
        DialogButtonBarDefaults.ButtonHorizontalPadding * 2 -
        DialogButtonBarDefaults.CellPaddingEdge -
        DialogButtonBarDefaults.CellPaddingCentral
}

/**
 * Whether every label fits [availableWidth] on one line, inverting
 * `COUIButtonBarLayout.wouldButtonTextExceedWidth`, which compares the raw single-line paint width.
 */
private fun labelsFit(
    labels: List<String>,
    availableWidth: Dp,
    textStyle: TextStyle,
    textMeasurer: TextMeasurer,
    density: Density,
): Boolean {
    val availablePx = with(density) { availableWidth.roundToPx() }
    if (availablePx <= 0) return true
    return labels.all { label ->
        textMeasurer.measure(text = label, style = textStyle, softWrap = false, maxLines = 1).size.width <= availablePx
    }
}

/**
 * The horizontal tier of `COUIButtonBarLayout` (`setButtonsHorizontal` + `setHorButtonsParentPadding`):
 * two equal-weight cells ordered negative / neutral / positive, the first padded
 * (16, 4, 4, 16) and the second (4, 4, 16, 16).
 */
@Composable
private fun RowButtonBar(
    negative: DialogButtonBarAction?,
    positive: DialogButtonBarAction?,
    neutral: DialogButtonBarAction?,
    colors: TextButtonColors,
    neutralColors: TextButtonColors,
    textStyle: TextStyle,
) {
    // COUI resortButton brings negative, then neutral, then positive to the front in a row.
    val slots = listOfNotNull(
        negative?.let { BarSlot.Negative to it },
        neutral?.let { BarSlot.Neutral to it },
        positive?.let { BarSlot.Positive to it },
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = DialogDefaults.ButtonBarMinHeight),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        slots.forEachIndexed { index, (slot, action) ->
            val isFirst = index == 0
            val isLast = index == slots.lastIndex
            BarButton(
                action = action,
                colors = if (slot == BarSlot.Neutral) neutralColors else colors,
                minHeight = DialogDefaults.ButtonBarMinHeight,
                textStyle = textStyle,
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        start = if (isFirst) DialogButtonBarDefaults.CellPaddingEdge else DialogButtonBarDefaults.CellPaddingCentral,
                        top = DialogButtonBarDefaults.CellPaddingTop,
                        end = if (isLast) DialogButtonBarDefaults.CellPaddingEdge else DialogButtonBarDefaults.CellPaddingCentral,
                        bottom = DialogButtonBarDefaults.CellPaddingBottom,
                    ),
            )
        }
    }
}

/**
 * The vertical tier of `COUIButtonBarLayout` (`setButtonsVertical` + `resetVerButsPadding` +
 * `setVerButtonsParentPadding`): full-width buttons ordered neutral / positive / negative, each
 * cell padded 16dp horizontally and 4dp vertically, with the bottom-most cell carrying the 16dp
 * panel bottom inset. A label that would wrap raises its button to the multiline min height.
 */
@Composable
private fun StackedButtonBar(
    negative: DialogButtonBarAction?,
    positive: DialogButtonBarAction?,
    neutral: DialogButtonBarAction?,
    barWidth: Dp,
    colors: TextButtonColors,
    neutralColors: TextButtonColors,
    textStyle: TextStyle,
    textMeasurer: TextMeasurer,
    density: Density,
) {
    // COUI resortButton brings neutral, then positive, then negative to the front when stacking.
    val slots = listOfNotNull(
        neutral?.let { BarSlot.Neutral to it },
        positive?.let { BarSlot.Positive to it },
        negative?.let { BarSlot.Negative to it },
    )
    // COUIButtonBarLayout.isVerButtonTextMultiline: the bar width minus both edge paddings and both
    // button paddings.
    val labelWidth = barWidth -
        DialogButtonBarDefaults.CellPaddingEdge * 2 -
        DialogButtonBarDefaults.ButtonHorizontalPadding * 2
    Column(modifier = Modifier.fillMaxWidth()) {
        slots.forEachIndexed { index, (slot, action) ->
            val isBottom = index == slots.lastIndex
            val multiline = remember(action.text, labelWidth, textStyle, density) {
                !labelsFit(listOf(action.text), labelWidth, textStyle, textMeasurer, density)
            }
            BarButton(
                action = action,
                colors = if (slot == BarSlot.Neutral) neutralColors else colors,
                minHeight = if (multiline) {
                    DialogButtonBarDefaults.StackedButtonMinHeightMultiline
                } else {
                    DialogButtonBarDefaults.StackedButtonMinHeight
                },
                textStyle = textStyle,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = DialogButtonBarDefaults.CellPaddingEdge,
                    )
                    .padding(
                        top = DialogButtonBarDefaults.CellPaddingTop,
                        bottom = if (isBottom) DialogButtonBarDefaults.CellPaddingBottom else DialogButtonBarDefaults.CellPaddingTop,
                    ),
            )
        }
    }
}

/** A `COUIAlertDialogBottomButtonSecondary` capsule: filled, press-scaled, corners at half height. */
@Composable
private fun BarButton(
    action: DialogButtonBarAction,
    colors: TextButtonColors,
    minHeight: Dp,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    TextButton(
        text = action.text,
        onClick = action.onClick,
        modifier = modifier,
        enabled = action.enabled,
        // drawableRadius = -1dp: the default capsule rounds the fill at half the measured height.
        minWidth = 0.dp,
        minHeight = minHeight,
        colors = colors,
        insideMargin = DialogDefaults.ButtonBarInsideMargin,
        textStyle = textStyle,
        // The bar measures label wrapping itself and grows the button for multi-line labels.
        maxLines = Int.MAX_VALUE,
    )
}

/** The COUI button roles, kept apart because the two tiers order them differently. */
private enum class BarSlot { Neutral, Positive, Negative }

object DialogButtonBarDefaults {
    /**
     * The horizontal padding of a bar button, in both tiers. COUI
     * `coui_alert_dialog_button_horizontal_padding`; also the padding
     * `COUIButtonBarLayout.getHorizontalButtonContentWidth` subtracts from each cell.
     */
    val ButtonHorizontalPadding = 12.dp

    /**
     * The padding between a button cell and the panel's side edge. COUI
     * `coui_alert_dialog_button_parent_padding_horizontal`.
     */
    val CellPaddingEdge = 16.dp

    /**
     * The padding on the inner side of a row cell; two of them make the 8dp gap between the
     * buttons. COUI `coui_alert_dialog_button_parent_padding_horizontal_inside`.
     */
    val CellPaddingCentral = 4.dp

    /**
     * The top padding of every button cell, and the bottom padding of a stacked cell that is not the
     * bottom-most one. COUI `coui_alert_dialog_button_parent_padding_vertical`.
     */
    val CellPaddingTop = 4.dp

    /**
     * The bottom padding of the bottom-most cell (every cell of a row), which carries the panel
     * bottom inset. COUI `coui_alert_dialog_button_parent_padding_bottom`.
     */
    val CellPaddingBottom = 16.dp

    /**
     * The min height of a stacked bar button. COUI `coui_alert_dialog_vertical_button_min_height`
     * (`mVerButMinHeightNormal` and `mVerButMinHeightBottom` are the same value on COUI 17).
     */
    val StackedButtonMinHeight = 44.dp

    /**
     * The min height of a stacked bar button whose label wraps. COUI
     * `coui_alert_dialog_button_min_height_multiline`.
     */
    val StackedButtonMinHeightMultiline = 56.dp

    /**
     * The default [TextButtonColors] of the negative and positive buttons. COUI
     * `Widget.COUI.Button.Large.ButtonNew.Secondary`: `?couiColorContainer8` fill (also the
     * disabled fill) and `coui_btn_secondary_text_color` (`?couiColorLabelPrimary`) labels.
     */
    @Composable
    fun buttonColors(
        color: Color = COUITheme.colorScheme.dialogButton,
        disabledColor: Color = COUITheme.colorScheme.dialogButton,
        textColor: Color = COUITheme.colorScheme.onSecondaryVariant,
        disabledTextColor: Color = COUITheme.colorScheme.disabledOnSecondaryVariant,
    ): TextButtonColors = remember(color, disabledColor, textColor, disabledTextColor) {
        TextButtonColors(
            color = color,
            disabledColor = disabledColor,
            textColor = textColor,
            disabledTextColor = disabledTextColor,
        )
    }

    /**
     * The default [TextButtonColors] of the neutral button. COUI
     * `COUIAlertDialogBottomButtonSecondary.Last`: the secondary fill with `coui_btn_error_text_color`
     * labels (`?couiColorError`, and `?couiColorErrorDisabled`, the error color at 30% alpha, when
     * disabled).
     */
    @Composable
    fun neutralButtonColors(
        color: Color = COUITheme.colorScheme.dialogButton,
        disabledColor: Color = COUITheme.colorScheme.dialogButton,
        textColor: Color = COUITheme.colorScheme.error,
        disabledTextColor: Color = COUITheme.colorScheme.error.copy(alpha = 0.3f),
    ): TextButtonColors = remember(color, disabledColor, textColor, disabledTextColor) {
        TextButtonColors(
            color = color,
            disabledColor = disabledColor,
            textColor = textColor,
            disabledTextColor = disabledTextColor,
        )
    }
}
