// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * A [SmallTitle] with COUI style.
 *
 * Typography follows the COUI category title (`Widget.COUI.List.Category.Title` ->
 * `couiTextAppearanceSmallButton`): 12sp, sans-serif-medium, line spacing multiplier 1.0.
 * The style is overridden locally instead of changing the shared `subtitle` text style,
 * which is used by other components.
 *
 * @param text The text to be displayed in the [SmallTitle].
 * @param modifier The modifier to be applied to the [SmallTitle].
 * @param textColor The color of the [SmallTitle].
 * @param insideMargin The margin inside the [SmallTitle].
 */
@Composable
@NonRestartableComposable
fun SmallTitle(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = COUITheme.colorScheme.onBackgroundVariant,
    insideMargin: PaddingValues = SmallTitleDefaults.InsideMargin,
) {
    Text(
        modifier =
        modifier
            .padding(insideMargin)
            .heightIn(min = SmallTitleDefaults.MinHeight)
            .wrapContentHeight(),
        text = text,
        // COUI couiTextAppearanceSmallButton: 12sp sans-serif-medium.
        style =
        COUITheme.textStyles.subtitle.copy(
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
        ),
        color = textColor,
    )
}

/** Contains default values used by [SmallTitle]. */
object SmallTitleDefaults {
    /**
     * The default inside margin of the [SmallTitle].
     *
     * COUI category title metrics (com.android.settings ground truth):
     * horizontal 32dp = 16dp page margin + 16dp card content inset
     * (support_preference_category_layout_title_margin_start_large), vertical 8dp
     * (support_preference_category_layout_title_margin_end_new, applied as both the
     * top and bottom margin of the category title). The remaining 16dp card-to-title
     * gap comes from the spacing between cards (coui_preference_category_margintop_large),
     * so stack `Card` blocks with 16dp vertical spacing to reproduce the COUI rhythm:
     * card -> 16dp + 8dp -> title line -> 8dp -> next card.
     */
    val InsideMargin = PaddingValues(32.dp, 8.dp)

    /**
     * The default minimum text height of the [SmallTitle].
     *
     * COUI `Widget.COUI.List.Category.Title` sets `android:minHeight` to
     * `coui_preference_category_text_height` (16dp) with `center_vertical` gravity.
     */
    val MinHeight = 16.dp
}
