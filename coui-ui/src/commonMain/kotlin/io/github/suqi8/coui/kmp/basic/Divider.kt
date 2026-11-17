// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.math.roundToInt

/**
 * A divider is a thin line that groups content in lists and layouts.
 *
 * Rows inside a [CardListGroup] get their COUI dividers drawn by the group, aligned with each row's
 * title; use this composable only for dividers outside such groups.
 *
 * @param modifier the [Modifier] to be applied to this divider line.
 * @param thickness thickness of this divider line, rounded to whole pixels and never thinner than one
 *   pixel (COUIRecyclerView draws `max(1px, round(coui_list_divider_height))`). Using [Dp.Hairline]
 *   will produce a single pixel divider regardless of screen density.
 * @param color color of this divider line.
 */
@Composable
@NonRestartableComposable
fun HorizontalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = DividerDefaults.Thickness,
    color: Color = DividerDefaults.DividerColor,
) = Canvas(modifier.fillMaxWidth().height(thickness)) {
    val stroke = dividerStrokePx(thickness.toPx())
    drawLine(
        color = color,
        strokeWidth = stroke,
        start = Offset(0f, stroke / 2),
        end = Offset(size.width, stroke / 2),
    )
}

/**
 * A divider is a thin line that groups content in lists and layouts.
 *
 * @param modifier the [Modifier] to be applied to this divider line.
 * @param thickness thickness of this divider line, rounded to whole pixels and never thinner than one
 *   pixel (COUIRecyclerView draws `max(1px, round(coui_list_divider_height))`). Using [Dp.Hairline]
 *   will produce a single pixel divider regardless of screen density.
 * @param color color of this divider line.
 */
@Composable
@NonRestartableComposable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = DividerDefaults.Thickness,
    color: Color = DividerDefaults.DividerColor,
) = Canvas(modifier.fillMaxHeight().width(thickness)) {
    val stroke = dividerStrokePx(thickness.toPx())
    drawLine(
        color = color,
        strokeWidth = stroke,
        start = Offset(stroke / 2, 0f),
        end = Offset(stroke / 2, size.height),
    )
}

object DividerDefaults {

    /**
     * Default thickness of the divider line (COUI coui_list_divider_height).
     */
    val Thickness = 0.33.dp

    /**
     * Default color of the divider line.
     */
    val DividerColor @Composable get() = COUITheme.colorScheme.divider

    /**
     * The end inset of a row divider inside a card (COUI coui_preference_divider_default_horizontal_padding
     * 32dp, less the 16dp card margin). [CardListGroup] uses it for the end of its row dividers.
     */
    val CardInset = 16.dp
}

/** COUI divider stroke: [thicknessPx] rounded to whole pixels, with a one-pixel floor. */
private fun dividerStrokePx(thicknessPx: Float): Float = thicknessPx.roundToInt().coerceAtLeast(1).toFloat()
