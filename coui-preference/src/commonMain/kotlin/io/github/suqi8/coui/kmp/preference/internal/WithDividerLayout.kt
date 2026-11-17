// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.preference.internal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.VerticalDivider

/**
 * The two-part row of the COUI "with divider" preferences (`COUICustomListSelectedLinearLayout` with
 * `couiPreferenceWithDividerItem`): [main] (`main_layout`, weight 1) takes the width the trailing
 * area leaves, and [trailing] (`switch_layout` / `check_box_layout`, wrap_content x match_parent)
 * is stretched to the height of [main].
 *
 * The outer row itself swallows touches without pressing (`onTouchEvent` returns `true` when
 * `mWithDividerItem`), so only [main] shows the row press feedback.
 */
@Composable
internal fun WithDividerLayout(
    main: @Composable () -> Unit,
    trailing: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Layout(
        modifier = modifier,
        content = {
            Box(Modifier.layoutId(MAIN_ID)) { main() }
            // Propagates the fixed size below, so the trailing area fills the full row height.
            Box(Modifier.layoutId(TRAILING_ID), propagateMinConstraints = true) { trailing() }
        },
    ) { measurables, constraints ->
        val mainMeasurable = measurables.first { it.layoutId == MAIN_ID }
        val trailingMeasurable = measurables.first { it.layoutId == TRAILING_ID }

        // The trailing area is a fixed divider + control; its intrinsic width is cheap to query.
        val trailingWidth = trailingMeasurable.maxIntrinsicWidth(Constraints.Infinity).coerceAtMost(constraints.maxWidth)
        val mainMaxWidth = if (constraints.hasBoundedWidth) {
            (constraints.maxWidth - trailingWidth).coerceAtLeast(0)
        } else {
            Constraints.Infinity
        }
        val mainPlaceable = mainMeasurable.measure(
            constraints.copy(minWidth = 0, maxWidth = mainMaxWidth),
        )
        val height = mainPlaceable.height
        val trailingPlaceable = trailingMeasurable.measure(Constraints.fixed(trailingWidth, height))
        val width = if (constraints.hasBoundedWidth) {
            constraints.maxWidth
        } else {
            mainPlaceable.width + trailingWidth
        }
        layout(width, height) {
            mainPlaceable.placeRelative(0, 0)
            trailingPlaceable.placeRelative(width - trailingWidth, 0)
        }
    }
}

/**
 * The divider leading the trailing area: a 1dp x 22dp `?couiColorDivider` line followed by a 16dp gap
 * to the control (`coui_switch_with_divider_preference` / `coui_check_box_divider_preference`).
 */
@Composable
internal fun WithDividerSeparator(color: Color) {
    VerticalDivider(
        modifier = Modifier
            .padding(end = WithDividerDefaults.DividerMarginEnd)
            .height(WithDividerDefaults.DividerHeight),
        thickness = WithDividerDefaults.DividerWidth,
        color = color,
    )
}

/** Shared padding of the trailing area: `support_preference_title_padding_end` minus the 16dp card inset. */
internal fun Modifier.withDividerTrailingPadding(): Modifier = padding(end = WithDividerDefaults.TrailingPaddingEnd)

internal object WithDividerDefaults {
    /** preference_divider_width (the switch layout hardcodes the same 1dp). */
    val DividerWidth: Dp = 1.dp

    /** preference_divider_height (the switch layout hardcodes the same 22dp). */
    val DividerHeight: Dp = 22.dp

    /** android_preference_switch_margin_left / preference_divider_margin_end. */
    val DividerMarginEnd: Dp = 16.dp

    /** support_preference_title_padding_end 32dp, less the 16dp coui_preference_card_margin_horizontal. */
    val TrailingPaddingEnd: Dp = 16.dp
}

private const val MAIN_ID = "main"
private const val TRAILING_ID = "trailing"
