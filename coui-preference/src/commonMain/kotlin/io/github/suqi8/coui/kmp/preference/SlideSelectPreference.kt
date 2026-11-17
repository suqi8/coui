// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.preference

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.BasicComponentColors
import io.github.suqi8.coui.kmp.basic.BasicComponentDefaults
import io.github.suqi8.coui.kmp.basic.CardListPosition
import io.github.suqi8.coui.kmp.basic.LocalCardListPosition
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * A row that shows the current selection as trailing status text followed by the COUI up/down
 * select indicator (COUI `COUISlideSelectPreference`, style `Preference.COUI.COUISelectPreference`,
 * widget `coui_preference_widget_select`).
 *
 * The official class adds no click behaviour of its own (it extends `COUIPreference`): the host
 * decides what a tap does, typically opening a list popup or a picker and then updating
 * [selectionText]. The status text (`coui_statusText_select`) is hidden when [selectionText] is
 * null or empty; the indicator (`coui_preference_widget_jump`, `coui_btn_select`) is always shown
 * and is not exposed to accessibility services.
 *
 * @param title The title of the [SlideSelectPreference].
 * @param modifier The modifier to be applied to the [SlideSelectPreference].
 * @param selectionText The current selection shown before the indicator (`setSelectionText`);
 *   null or empty hides it.
 * @param onClick The callback when the [SlideSelectPreference] is clicked.
 * @param titleColor The color of the title.
 * @param summary The summary of the [SlideSelectPreference].
 * @param summaryColor The color of the summary.
 * @param colors The [SlideSelectPreferenceColors] of the status text and the indicator.
 * @param startAction The [Composable] content on the start side of the [SlideSelectPreference].
 * @param endActions The [Composable] content on the end side, before the status text.
 * @param bottomAction The [Composable] content at the bottom of the [SlideSelectPreference].
 * @param insideMargin The margin inside the [SlideSelectPreference].
 * @param cardListPosition The row's position inside its card group, by default the one a
 *   [io.github.suqi8.coui.kmp.basic.CardListGroup] provides. Rounded outer edges receive extra padding.
 * @param holdDownState Used to determine whether it is in the pressed state, e.g. while the
 *   popup it opened is shown.
 * @param enabled Whether the [SlideSelectPreference] is clickable.
 */
@Composable
fun SlideSelectPreference(
    title: String,
    modifier: Modifier = Modifier,
    selectionText: String? = null,
    onClick: (() -> Unit)? = null,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    colors: SlideSelectPreferenceColors = SlideSelectPreferenceDefaults.slideSelectPreferenceColors(),
    startAction: @Composable (() -> Unit)? = null,
    endActions: @Composable (RowScope.() -> Unit)? = null,
    bottomAction: (@Composable () -> Unit)? = null,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    cardListPosition: CardListPosition = LocalCardListPosition.current,
    holdDownState: Boolean = false,
    enabled: Boolean = true,
) {
    BasicComponent(
        modifier = modifier,
        insideMargin = insideMargin,
        cardListPosition = cardListPosition,
        title = title,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        startAction = startAction,
        endActions = {
            endActions?.let {
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    it()
                }
            }
            // coui_preference_widget_select: the status text, then the indicator.
            val hasText = !selectionText.isNullOrEmpty()
            if (!selectionText.isNullOrEmpty()) {
                Text(
                    text = selectionText,
                    modifier = Modifier
                        .padding(start = SlideSelectPreferenceDefaults.SelectionTextPaddingStart)
                        .widthIn(max = SlideSelectPreferenceDefaults.SelectionTextMaxWidth)
                        .align(Alignment.CenterVertically)
                        .weight(1f, fill = false),
                    style = COUITheme.textStyles.bodyM,
                    color = colors.selectionTextColor(enabled),
                    textAlign = TextAlign.End,
                )
            }
            SlideSelectIndicator(
                color = colors.indicatorColor(enabled),
                modifier = Modifier
                    .padding(start = if (hasText) SlideSelectPreferenceDefaults.IndicatorMarginStart else 0.dp)
                    .align(Alignment.CenterVertically),
            )
        },
        bottomAction = bottomAction,
        onClick = onClick,
        holdDownState = holdDownState,
        enabled = enabled,
    )
}

/**
 * The `coui_btn_select` indicator: two filled triangles, the upper one pointing up and the lower one
 * pointing down, traced from the 72px xxhdpi bitmaps (24dp box, 8.67dp wide, 5.67dp tall each,
 * 2.67dp apart). The drawing is symmetric, so it needs no RTL mirroring.
 */
@Composable
private fun SlideSelectIndicator(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.size(SlideSelectPreferenceDefaults.IndicatorSize)) {
        val unit = size.minDimension / 24f
        val up = Path().apply {
            moveTo(12f * unit, 5f * unit)
            lineTo(16.33f * unit, 10.67f * unit)
            lineTo(7.67f * unit, 10.67f * unit)
            close()
        }
        val down = Path().apply {
            moveTo(7.67f * unit, 13.33f * unit)
            lineTo(16.33f * unit, 13.33f * unit)
            lineTo(12f * unit, 19f * unit)
            close()
        }
        drawPath(path = up, color = color)
        drawPath(path = down, color = color)
    }
}

object SlideSelectPreferenceDefaults {
    /**
     * Start padding of the status text: `support_preference_widget_focus_jump_margin_start` (24dp),
     * less the 16dp gap [BasicComponent] already keeps between the title and its end actions.
     */
    val SelectionTextPaddingStart: Dp = 8.dp

    /** Maximum width of the status text (`coui_preference_status_text_max_width`). */
    val SelectionTextMaxWidth: Dp = 162.dp

    /**
     * Gap between the status text and the indicator (`support_preference_image_padding_start`). Without
     * status text the indicator follows the title at the [BasicComponent] end-action gap instead.
     */
    val IndicatorMarginStart: Dp = 16.dp

    /** Size of the indicator (`coui_btn_select_*.png`, 72px at xxhdpi). */
    val IndicatorSize: Dp = 24.dp

    /**
     * The default colors: the status text uses `coui_preference_secondary_text_color`
     * (`?couiColorSecondNeutral`, `?couiColorLabelTertiary` when disabled); the indicator bitmap is a
     * neutral tertiary grey whose disabled state has half the opacity of the normal one.
     */
    @Composable
    fun slideSelectPreferenceColors(
        selectionTextColor: Color = COUITheme.colorScheme.labelSecondary,
        disabledSelectionTextColor: Color = COUITheme.colorScheme.labelTertiary,
        indicatorColor: Color = COUITheme.colorScheme.labelTertiary,
        disabledIndicatorColor: Color = indicatorColor.copy(alpha = indicatorColor.alpha * 0.5f),
    ): SlideSelectPreferenceColors = remember(
        selectionTextColor,
        disabledSelectionTextColor,
        indicatorColor,
        disabledIndicatorColor,
    ) {
        SlideSelectPreferenceColors(
            selectionTextColor = selectionTextColor,
            disabledSelectionTextColor = disabledSelectionTextColor,
            indicatorColor = indicatorColor,
            disabledIndicatorColor = disabledIndicatorColor,
        )
    }
}

@Immutable
data class SlideSelectPreferenceColors(
    private val selectionTextColor: Color,
    private val disabledSelectionTextColor: Color,
    private val indicatorColor: Color,
    private val disabledIndicatorColor: Color,
) {
    @Stable
    internal fun selectionTextColor(enabled: Boolean): Color = if (enabled) selectionTextColor else disabledSelectionTextColor

    @Stable
    internal fun indicatorColor(enabled: Boolean): Color = if (enabled) indicatorColor else disabledIndicatorColor
}
