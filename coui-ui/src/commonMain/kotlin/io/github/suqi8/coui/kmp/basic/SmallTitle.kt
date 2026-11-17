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
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * A [SmallTitle] with COUI style.
 *
 * Typography follows the COUI category title (couiTextAppearanceSmallButton: 12sp sans-serif-medium).
 *
 * This is the small, title-only form of COUIPreferenceCategory. For medium / large titles, the 16dp
 * start margin type, a title icon or badge, or an end widget (text button, single icon, text with
 * icon), use `PreferenceCategory` from the coui-preference module.
 *
 * Spacing convention: only the title's own 8dp vertical margin is applied, not the COUI item top margin
 * (coui_preference_category_margintop_large, 16dp). Cards are expected to bring that space with their
 * own `padding(bottom = 16.dp)`, which adds up to the COUI 24dp from a card bottom to the title text.
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
    textColor: Color = COUITheme.colorScheme.labelSecondary,
    insideMargin: PaddingValues = SmallTitleDefaults.InsideMargin,
) {
    Text(
        modifier = modifier
            .padding(insideMargin)
            .heightIn(min = SmallTitleDefaults.MinHeight)
            .wrapContentHeight(),
        text = text,
        // COUI couiTextAppearanceSmallButton: 12sp sans-serif-medium, coui_spacing_multiplier_button_s 1.143.
        style = COUITheme.textStyles.bodyXS.copy(fontWeight = FontWeight.Medium),
        color = textColor,
    )
}

/** Contains default values used by [SmallTitle]. */
object SmallTitleDefaults {
    /**
     * The default inside margin of the [SmallTitle] (COUI category title metrics:
     * 16dp page margin + 16dp card content inset horizontally, 8dp vertically).
     */
    val InsideMargin = PaddingValues(32.dp, 8.dp)

    /**
     * The inside margin for the COUI small title margin type
     * (support_preference_category_layout_title_margin_start_small, 16dp horizontally; 8dp vertically),
     * for titles over content that has no card inset.
     */
    val InsideMarginSmall = PaddingValues(16.dp, 8.dp)

    /**
     * The default minimum text height of the [SmallTitle] (COUI coui_preference_category_text_height).
     */
    val MinHeight = 16.dp
}
