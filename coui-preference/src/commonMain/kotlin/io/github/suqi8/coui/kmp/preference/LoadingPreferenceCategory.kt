// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.preference

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.ProgressIndicatorDefaults
import io.github.suqi8.coui.kmp.basic.RotatingProgressIndicator
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.preference.internal.PreferenceCategoryRow
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * A category title with a trailing loading spinner, mirroring COUILoadingPreferenceCategory
 * (for example "Available devices" while a scan is running).
 *
 * While [loading] is `true` the row shows the small COUI rotating spinner
 * (coui_preference_category_widget_layout_loading.xml: COUICompProgressIndicator
 * `small_animation`, 16 x 16dp), optionally preceded by [loadingText] (at most two lines, 4dp before
 * the spinner). When loading ends, [endContent] replaces the spinner, which covers the original's
 * `coui_loading_after_layout` / `coui_loading_before_layout` widget layouts; without [endContent]
 * the widget simply disappears (LoadingType.INVISIBLE).
 *
 * @param title The category title.
 * @param modifier The modifier to be applied to the [LoadingPreferenceCategory].
 * @param loading Whether the spinner is shown (LoadingType.LOADING).
 * @param loadingText The optional text before the spinner (COUI text_in_loading).
 * @param titleType The size of the title, see [PreferenceCategory]. Next to a medium or large title
 *   [loadingText] grows to 14sp and the loading widget to a 28dp minimum height
 *   (COUIPreferenceCategory.initEndRect).
 * @param marginStartType The horizontal margin type, which picks the default [insideMargin].
 * @param titleColor The color of the title.
 * @param loadingTextColor The color of [loadingText].
 * @param indicatorColor The color of the spinner.
 * @param insideMargin The horizontal margin of the row. The end margin is measured to the spinner.
 * @param endContent The content shown at the end of the row while [loading] is `false`.
 */
@Composable
fun LoadingPreferenceCategory(
    title: String,
    modifier: Modifier = Modifier,
    loading: Boolean = true,
    loadingText: String? = null,
    titleType: PreferenceCategoryTitleType = PreferenceCategoryTitleType.Small,
    marginStartType: PreferenceCategoryMarginStartType = PreferenceCategoryMarginStartType.Large,
    titleColor: Color = LoadingPreferenceCategoryDefaults.titleColor(titleType),
    loadingTextColor: Color = LoadingPreferenceCategoryDefaults.loadingTextColor(),
    indicatorColor: Color = ProgressIndicatorDefaults.rotatingProgressIndicatorColor(),
    insideMargin: PaddingValues = LoadingPreferenceCategoryDefaults.insideMargin(marginStartType),
    endContent: (@Composable RowScope.() -> Unit)? = null,
) {
    PreferenceCategoryRow(
        title = title,
        titleColor = titleColor,
        insideMargin = insideMargin,
        modifier = modifier,
        titleType = titleType,
    ) {
        if (loading) {
            Row(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .heightIn(min = PreferenceCategoryDefaults.widgetMinHeight(titleType)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!loadingText.isNullOrEmpty()) {
                    Text(
                        text = loadingText,
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .padding(end = LoadingPreferenceCategoryDefaults.LoadingTextSpacing),
                        // COUI couiTextButtonS: 12sp sans-serif-medium, 14sp next to a medium / large title.
                        style = PreferenceCategoryDefaults.widgetTextStyle(titleType),
                        color = loadingTextColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                RotatingProgressIndicator(
                    color = indicatorColor,
                    size = ProgressIndicatorDefaults.SmallRotatingProgressIndicatorSize,
                    ringDiameter = ProgressIndicatorDefaults.SmallRotatingProgressIndicatorRingDiameter,
                    strokeWidth = ProgressIndicatorDefaults.SmallRotatingProgressIndicatorStrokeWidth,
                )
            }
        } else if (endContent != null) {
            Row(
                modifier = Modifier.weight(1f, fill = false),
                verticalAlignment = Alignment.CenterVertically,
                content = endContent,
            )
        }
    }
}

/** Contains default values used by [LoadingPreferenceCategory]. */
object LoadingPreferenceCategoryDefaults {

    /**
     * The horizontal margin of the row. Start: the title margin
     * (support_preference_category_layout_title_margin_start_large, 32dp). End: the loading widget
     * ends at the same 32dp, because COUIHalfHeightHorizontalPaddingLinearLayout pads it by half its
     * height and moves the rest of fixPaddingEnd (coui_preference_category_widget_layout_padding_end,
     * 16dp) into its end margin on top of support_preference_category_layout_title_margin_end_large
     * (16dp).
     */
    val InsideMargin = PaddingValues(horizontal = 32.dp)

    /**
     * The horizontal margin of the row for [PreferenceCategoryMarginStartType.Small]: the title starts
     * 16dp in (support_preference_category_layout_title_margin_start_small) and, without the extra
     * widget frame margin, the spinner ends at the 16dp fixPaddingEnd.
     */
    val InsideMarginSmall = PaddingValues(horizontal = 16.dp)

    /** The gap between the loading text and the spinner (COUI text_in_loading layout_marginEnd). */
    val LoadingTextSpacing = 4.dp

    /** The horizontal margin of the row for [marginStartType] ([InsideMargin] for the large type). */
    fun insideMargin(
        marginStartType: PreferenceCategoryMarginStartType = PreferenceCategoryMarginStartType.Large,
    ): PaddingValues = if (marginStartType == PreferenceCategoryMarginStartType.Small) InsideMarginSmall else InsideMargin

    /**
     * The default title color: couiColorSecondNeutral for a small title, couiColorPrimaryNeutral for
     * medium and large titles (see [PreferenceCategoryDefaults.titleColor]).
     */
    @Composable
    fun titleColor(
        titleType: PreferenceCategoryTitleType = PreferenceCategoryTitleType.Small,
    ): Color = PreferenceCategoryDefaults.titleColor(titleType = titleType)

    /**
     * The default loading text color (COUI text_in_loading ?couiColorPrimaryNeutral; the category's
     * rightTextfixSecondaryColor override never reaches this text view).
     */
    @Composable
    fun loadingTextColor(): Color = COUITheme.colorScheme.labelPrimary
}
