// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.layout

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.github.suqi8.coui.kmp.basic.ButtonColors
import io.github.suqi8.coui.kmp.basic.ButtonDefaults
import io.github.suqi8.coui.kmp.basic.CouiButton
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.basic.TextButtonColors
import io.github.suqi8.coui.kmp.effect.ContentButtonMaterialType
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * A single action in a [DialogButtonBar], addressed by role rather than position because
 * `COUIButtonBarLayout.resortButton` reorders the buttons when the bar stacks.
 *
 * @param text The button label.
 * @param enabled Whether the button is enabled.
 * @param recommended Whether this is the recommended action
 *   (`COUIAlertDialogBuilder.setPositiveButton(text, listener, isRecommend = true)`), which
 *   `COUIButtonBarLayout.applyRecommendStyle` promotes to a primary capsule: the theme-coloured
 *   opaque fill and white label. Mark at most one action of a bar; COUI keeps a single
 *   `mRecommendButtonId`.
 * @param onClick Invoked when the button is clicked.
 */
@Stable
data class DialogButtonBarAction(
    val text: String,
    val enabled: Boolean = true,
    val recommended: Boolean = false,
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
 * An action with [DialogButtonBarAction.recommended] takes the recommend tier
 * (`COUIButtonBarLayout.applyRecommendStyle`): `buttonType = PRIMARY` with the
 * [recommendedColors] fill and labels. The layout and order do not change. With [material] the
 * recommended button keeps the `drawableMaterialEffect` path, so its fill stays opaque (never
 * backdrop-blurred) and it gets the `CONTENT_CAPSULE_4` edge lighting and the
 * `TYPE_OPAQUE_MEDIUM_1` touch spotlight; the other buttons stay secondary.
 *
 * @param negative The negative (cancel) action. Hidden when null.
 * @param positive The positive (confirm) action. Hidden when null.
 * @param modifier The modifier to be applied to the bar.
 * @param neutral The neutral (third) action, drawn in the error color
 *   (`COUIAlertDialogBottomButtonSecondary.Last`). Hidden when null.
 * @param dynamicLayout Whether the bar may stack itself, like
 *   `COUIButtonBarLayout.setDynamicLayout`.
 * @param material Whether the buttons draw the COUI 17 material.
 *   `COUIButtonBarLayout.setBlurBackgroundWindow` switches the buttons to `FOLLOW_SYSTEM` when the
 *   dialog window is blurred (`FORCE_DISABLE` otherwise), so this defaults to whether the hosting
 *   dialog draws its material panel (`materialStroke`). The buttons then take the
 *   `drawableMaterialEffect` look: a translucent white fill
 *   ([DialogButtonBarDefaults.materialButtonColors]), the `CONTENT_CAPSULE_5` edge lighting +
 *   inner shadow, the `TYPE_OPAQUE_MEDIUM_2` touch spotlight and no press tint.
 * @param colors The [TextButtonColors] of the negative and positive buttons.
 * @param neutralColors The [TextButtonColors] of the neutral button.
 * @param recommendedColors The [TextButtonColors] of the recommended button, whatever its role.
 * @param textStyle The text style of the labels, also used to measure them for the flip decision.
 */
@Composable
fun DialogButtonBar(
    negative: DialogButtonBarAction?,
    positive: DialogButtonBarAction?,
    modifier: Modifier = Modifier,
    neutral: DialogButtonBarAction? = null,
    dynamicLayout: Boolean = true,
    material: Boolean = LocalDialogMaterial.current,
    colors: TextButtonColors = if (material) {
        DialogButtonBarDefaults.materialButtonColors()
    } else {
        DialogButtonBarDefaults.buttonColors()
    },
    neutralColors: TextButtonColors = if (material) {
        DialogButtonBarDefaults.materialNeutralButtonColors()
    } else {
        DialogButtonBarDefaults.neutralButtonColors()
    },
    recommendedColors: TextButtonColors = DialogButtonBarDefaults.recommendedButtonColors(),
    textStyle: TextStyle = COUITheme.textStyles.buttonL,
) {
    // COUIButtonBarLayout.onInterceptTouchEvent + getChildDrawingOrder: the cell touched last draws
    // above the others, so the rubber band and deformation of its button are never covered by a
    // neighbour, also while it springs back after the release.
    val touchedSlot = remember { mutableStateOf<BarSlot?>(null) }
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
                material = material,
                colors = colors,
                neutralColors = neutralColors,
                recommendedColors = recommendedColors,
                textStyle = textStyle,
                textMeasurer = textMeasurer,
                density = density,
                touchedSlot = touchedSlot,
            )
        } else {
            RowButtonBar(
                negative = negative,
                positive = positive,
                neutral = neutral,
                material = material,
                colors = colors,
                neutralColors = neutralColors,
                recommendedColors = recommendedColors,
                textStyle = textStyle,
                touchedSlot = touchedSlot,
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
    material: Boolean,
    colors: TextButtonColors,
    neutralColors: TextButtonColors,
    recommendedColors: TextButtonColors,
    textStyle: TextStyle,
    touchedSlot: MutableState<BarSlot?>,
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
                material = material,
                colors = when {
                    action.recommended -> recommendedColors
                    slot == BarSlot.Neutral -> neutralColors
                    else -> colors
                },
                minHeight = DialogDefaults.ButtonBarMinHeight,
                textStyle = textStyle,
                modifier = Modifier
                    .weight(1f)
                    .barCellDrawOrder(slot = slot, touchedSlot = touchedSlot)
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
    material: Boolean,
    colors: TextButtonColors,
    neutralColors: TextButtonColors,
    recommendedColors: TextButtonColors,
    textStyle: TextStyle,
    textMeasurer: TextMeasurer,
    density: Density,
    touchedSlot: MutableState<BarSlot?>,
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
                material = material,
                colors = when {
                    action.recommended -> recommendedColors
                    slot == BarSlot.Neutral -> neutralColors
                    else -> colors
                },
                minHeight = if (multiline) {
                    DialogButtonBarDefaults.StackedButtonMinHeightMultiline
                } else {
                    DialogButtonBarDefaults.StackedButtonMinHeight
                },
                textStyle = textStyle,
                modifier = Modifier
                    .fillMaxWidth()
                    .barCellDrawOrder(slot = slot, touchedSlot = touchedSlot)
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

/**
 * A `COUIAlertDialogBottomButtonSecondary` capsule: filled, press-scaled, corners at half height.
 * With [material] it is the `drawableMaterialEffect` button of a blurred dialog: primary for the
 * recommended action, secondary otherwise.
 */
@Composable
private fun BarButton(
    action: DialogButtonBarAction,
    material: Boolean,
    colors: TextButtonColors,
    minHeight: Dp,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    val mappedColors = remember(colors) {
        ButtonColors(
            color = colors.color,
            disabledColor = colors.disabledColor,
            contentColor = colors.textColor,
            disabledContentColor = colors.disabledTextColor,
        )
    }
    CouiButton(
        onClick = action.onClick,
        modifier = modifier,
        enabled = action.enabled,
        pressScaleEnabled = true,
        // drawableRadius = -1dp: the default capsule rounds the fill at half the measured height.
        cornerRadius = ButtonDefaults.CornerRadius,
        minWidth = 0.dp,
        minHeight = minHeight,
        colors = mappedColors,
        insideMargin = DialogDefaults.ButtonBarInsideMargin,
        // getBlurWindowButtonType: PRIMARY for the recommend id, SECONDARY for every other button.
        // The bar style keeps drawableMaterialEffect = true, so neither ever samples a backdrop.
        materialType = when {
            !material -> null
            action.recommended -> ContentButtonMaterialType.DrawablePrimary
            else -> ContentButtonMaterialType.DrawableSecondary
        },
        interactionSource = null,
        indication = null,
    ) {
        Text(
            text = action.text,
            style = textStyle,
            // COUIAlertDialogBottomButtonSecondary android:maxLines = 2; the bar measures label
            // wrapping itself and grows the button for multi-line labels.
            maxLines = DialogButtonBarDefaults.LabelMaxLines,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** The COUI button roles, kept apart because the two tiers order them differently. */
private enum class BarSlot { Neutral, Positive, Negative }

/**
 * Draws the cell of [slot] above its siblings once a touch lands in it, and keeps it there until
 * another cell is touched (`COUIButtonBarLayout.findTouchedButtonParentIndex` on `ACTION_DOWN` +
 * `getChildDrawingOrder`). The down is only observed, never consumed.
 */
private fun Modifier.barCellDrawOrder(
    slot: BarSlot,
    touchedSlot: MutableState<BarSlot?>,
): Modifier = this
    .zIndex(if (touchedSlot.value == slot) 1f else 0f)
    .pointerInput(slot, touchedSlot) {
        awaitEachGesture {
            awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            touchedSlot.value = slot
        }
    }

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
     * The max number of lines of a bar button label before it ellipsizes. COUI
     * `COUIAlertDialogBottomButtonSecondary` `android:maxLines`.
     */
    val LabelMaxLines = 2

    /**
     * The default [TextButtonColors] of the negative and positive buttons. COUI
     * `Widget.COUI.Button.Large.ButtonNew.Secondary`: `?couiColorContainer8` fill (also the
     * disabled fill) and `coui_btn_secondary_text_color` (`?couiColorLabelPrimary`) labels.
     */
    @Composable
    fun buttonColors(
        color: Color = COUITheme.colorScheme.dialogButton,
        disabledColor: Color = COUITheme.colorScheme.dialogButton,
        textColor: Color = COUITheme.colorScheme.labelPrimary,
        disabledTextColor: Color = COUITheme.colorScheme.disabledNeutral,
    ): TextButtonColors = remember(color, disabledColor, textColor, disabledTextColor) {
        TextButtonColors(
            color = color,
            disabledColor = disabledColor,
            textColor = textColor,
            disabledTextColor = disabledTextColor,
        )
    }

    /**
     * The [TextButtonColors] of the negative and positive buttons of a material dialog. COUI
     * `COUIButton.changeButtonAlphaColor` for a `drawableMaterialEffect` secondary button with the
     * material on: white at alpha 166 (light) or 51 (dark). The disabled fill stays
     * `?couiColorContainer8` (`disabledColor`, which `drawButtonBackground` draws when disabled).
     */
    @Composable
    fun materialButtonColors(
        color: Color = if (isDarkTheme()) MaterialFillDark else MaterialFillLight,
        disabledColor: Color = COUITheme.colorScheme.dialogButton,
        textColor: Color = COUITheme.colorScheme.labelPrimary,
        disabledTextColor: Color = COUITheme.colorScheme.disabledNeutral,
    ): TextButtonColors = remember(color, disabledColor, textColor, disabledTextColor) {
        TextButtonColors(
            color = color,
            disabledColor = disabledColor,
            textColor = textColor,
            disabledTextColor = disabledTextColor,
        )
    }

    /**
     * The [TextButtonColors] of the neutral button of a material dialog: the
     * [materialButtonColors] fill with the error labels of [neutralButtonColors].
     */
    @Composable
    fun materialNeutralButtonColors(
        color: Color = if (isDarkTheme()) MaterialFillDark else MaterialFillLight,
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

    /**
     * The [TextButtonColors] of the recommended button (`COUIButtonBarLayout.applyRecommendStyle`):
     * the `?couiColorContainerTheme` primary fill, which `changeButtonAlphaColor` keeps opaque in a
     * material dialog too, `coui_btn_default_text_color` labels (`?couiColorLabelOnColor`, white;
     * `?couiBtnDefaultTextColorDisable` when disabled) and the `?couiColorDisable` disabled fill
     * (the primary at 30% alpha).
     */
    @Composable
    fun recommendedButtonColors(
        color: Color = COUITheme.colorScheme.primary,
        disabledColor: Color = COUITheme.colorScheme.disable,
        textColor: Color = COUITheme.colorScheme.onPrimary,
        disabledTextColor: Color = COUITheme.colorScheme.disabledOnPrimaryButton,
    ): TextButtonColors = remember(color, disabledColor, textColor, disabledTextColor) {
        TextButtonColors(
            color = color,
            disabledColor = disabledColor,
            textColor = textColor,
            disabledTextColor = disabledTextColor,
        )
    }

    /** `Color.argb(166, 255, 255, 255)`, the light material fill. */
    private val MaterialFillLight = Color(0xA6FFFFFF)

    /** `Color.argb(51, 255, 255, 255)`, the dark material fill. */
    private val MaterialFillDark = Color(0x33FFFFFF)

    @Composable
    private fun isDarkTheme(): Boolean = COUITheme.colorScheme.isDark

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

/**
 * Whether the enclosing dialog draws its COUI 17 material panel (blurred window background +
 * material stroke), which `COUIButtonBarLayout.setBlurBackgroundWindow` forwards to its buttons.
 */
internal val LocalDialogMaterial = staticCompositionLocalOf { false }
