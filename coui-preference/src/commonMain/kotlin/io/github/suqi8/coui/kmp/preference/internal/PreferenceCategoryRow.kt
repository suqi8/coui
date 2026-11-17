// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.preference.internal

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Badge
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.preference.LocalPreferenceCategoryTitleType
import io.github.suqi8.coui.kmp.preference.PreferenceCategoryDefaults
import io.github.suqi8.coui.kmp.preference.PreferenceCategoryTitleType

/**
 * The shared COUIPreferenceCategory title row (coui_preference_category_layout.xml): the category
 * title on the start side, an optional title icon or text badge right after it, and an end-aligned
 * widget frame.
 *
 * The title follows [titleType] (see [PreferenceCategoryDefaults.titleTextStyle],
 * [PreferenceCategoryDefaults.titleMinHeight] and [PreferenceCategoryDefaults.titleVerticalMargin]).
 * The title keeps 8dp to its end (coui_category_title_pading_end_with_reddot_default; 4dp / 8dp before
 * a title icon, coui_category_title_margin_end_with_icon_small / _large) and the widget frame 8dp to its
 * start (support_preference_category_layout_widgetlayout_margin_horizontal).
 *
 * [titleType] is provided to [widget] through [LocalPreferenceCategoryTitleType].
 */
@Composable
internal fun PreferenceCategoryRow(
    title: String,
    titleColor: Color,
    insideMargin: PaddingValues,
    modifier: Modifier = Modifier,
    titleType: PreferenceCategoryTitleType = PreferenceCategoryTitleType.Small,
    titleIcon: (@Composable () -> Unit)? = null,
    titleBadge: String? = null,
    widget: (@Composable RowScope.() -> Unit)? = null,
) {
    // COUIPreferenceCategory.initStartRect: the text red dot is hidden while a title icon shows.
    val showBadge = titleIcon == null && !titleBadge.isNullOrEmpty()
    CompositionLocalProvider(LocalPreferenceCategoryTitleType provides titleType) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(insideMargin),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .padding(vertical = PreferenceCategoryDefaults.titleVerticalMargin(titleType))
                        .heightIn(min = PreferenceCategoryDefaults.titleMinHeight(titleType))
                        .wrapContentHeight()
                        .semantics { heading() },
                    style = PreferenceCategoryDefaults.titleTextStyle(titleType),
                    color = titleColor,
                )
                if (titleIcon != null) {
                    Spacer(Modifier.width(PreferenceCategoryDefaults.titleIconSpacing(titleType)))
                    titleIcon()
                } else if (showBadge) {
                    Spacer(Modifier.width(PreferenceCategoryDefaults.TitleBadgeSpacing))
                    Badge(text = titleBadge.orEmpty())
                }
            }
            if (widget != null) {
                // Title end margin 8dp (only without an icon or badge) + widget frame start margin 8dp.
                val gap = if (titleIcon != null || showBadge) WidgetFrameMarginStart else TitleMarginEnd + WidgetFrameMarginStart
                Spacer(Modifier.width(gap))
                widget()
            }
        }
    }
}

/** The end margin of a plain title (coui_category_title_pading_end_with_reddot_default). */
private val TitleMarginEnd = 8.dp

/** The start margin of the widget frame (support_preference_category_layout_widgetlayout_margin_horizontal). */
private val WidgetFrameMarginStart = 8.dp
