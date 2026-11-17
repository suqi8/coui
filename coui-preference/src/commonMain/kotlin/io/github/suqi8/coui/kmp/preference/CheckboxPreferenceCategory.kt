// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.preference

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Checkbox
import io.github.suqi8.coui.kmp.basic.CheckboxColors
import io.github.suqi8.coui.kmp.basic.CheckboxDefaults
import io.github.suqi8.coui.kmp.preference.internal.PreferenceCategoryRow

/**
 * A category title with a trailing select-all checkbox, mirroring COUICheckBoxPreferenceCategory.
 *
 * The checkbox is a tri-state COUICheckBox: [ToggleableState.Indeterminate] reads as "some items
 * selected". Tapping it follows COUICheckBox.toggle: a fully checked box turns
 * [ToggleableState.Off], any other state turns [ToggleableState.On]; the new state is delivered to
 * [onStateChange] (COUICheckBox.OnStateChangeListener). Like the original, the title itself is not
 * clickable (COUICheckBoxPreferenceCategory ignores setWidgetLayoutClickListener).
 *
 * @param title The category title.
 * @param state The [ToggleableState] of the select-all checkbox.
 * @param onStateChange The callback with the toggled state when the checkbox is clicked.
 * @param modifier The modifier to be applied to the [CheckboxPreferenceCategory].
 * @param enabled Whether the checkbox is enabled.
 * @param titleType The size of the title, see [PreferenceCategory]. The checkbox does not change
 *   with it.
 * @param marginStartType The horizontal margin type, which picks the default [insideMargin].
 * @param titleColor The color of the title.
 * @param checkboxColors The [CheckboxColors] of the checkbox.
 * @param insideMargin The horizontal margin of the row. The end margin is measured to the
 *   checkbox itself.
 */
@Composable
fun CheckboxPreferenceCategory(
    title: String,
    state: ToggleableState,
    onStateChange: (ToggleableState) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    titleType: PreferenceCategoryTitleType = PreferenceCategoryTitleType.Small,
    marginStartType: PreferenceCategoryMarginStartType = PreferenceCategoryMarginStartType.Large,
    titleColor: Color = CheckboxPreferenceCategoryDefaults.titleColor(titleType),
    checkboxColors: CheckboxColors = CheckboxDefaults.checkboxColors(),
    insideMargin: PaddingValues = CheckboxPreferenceCategoryDefaults.insideMargin(marginStartType),
) {
    val currentOnStateChange by rememberUpdatedState(onStateChange)
    val currentState by rememberUpdatedState(state)
    PreferenceCategoryRow(
        title = title,
        titleColor = titleColor,
        insideMargin = insideMargin,
        modifier = modifier,
        titleType = titleType,
    ) {
        Checkbox(
            state = state,
            onClick = {
                // COUICheckBox.toggle: setState(mState >= ALL_CHECKED ? UNCHECKED : ALL_CHECKED).
                val next = if (currentState == ToggleableState.On) ToggleableState.Off else ToggleableState.On
                currentOnStateChange(next)
            },
            modifier = Modifier.padding(vertical = CheckboxPreferenceCategoryDefaults.CheckboxVerticalPadding),
            colors = checkboxColors,
            enabled = enabled,
        )
    }
}

/** Contains default values used by [CheckboxPreferenceCategory]. */
object CheckboxPreferenceCategoryDefaults {

    /**
     * The horizontal margin of the row for [PreferenceCategoryMarginStartType.Large]. Start: the title
     * margin (support_preference_category_layout_title_margin_start_large, 32dp). End: the widget frame
     * margin (support_preference_category_layout_title_margin_end_large, 16dp) plus the checkbox
     * paddingEnd (coui_preference_category_widget_layout_padding_end, 16dp).
     */
    val InsideMargin = PaddingValues(horizontal = 32.dp)

    /**
     * The horizontal margin of the row for [PreferenceCategoryMarginStartType.Small]. Start: the title
     * margin (support_preference_category_layout_title_margin_start_small, 16dp). End: the checkbox
     * paddingEnd (coui_preference_category_widget_layout_padding_end, 16dp); the widget frame keeps no
     * extra margin.
     */
    val InsideMarginSmall = PaddingValues(horizontal = 16.dp)

    /** The horizontal margin of the row for [marginStartType]. */
    fun insideMargin(
        marginStartType: PreferenceCategoryMarginStartType = PreferenceCategoryMarginStartType.Large,
    ): PaddingValues = if (marginStartType == PreferenceCategoryMarginStartType.Small) InsideMarginSmall else InsideMargin

    /**
     * The space above and below the checkbox: paddingTop / paddingBottom 5dp plus
     * coui_preference_category_widget_layout_margin_vertical 4dp
     * (coui_preference_category_widget_layout_checkbox.xml).
     */
    val CheckboxVerticalPadding = 9.dp

    /**
     * The default title color: couiColorSecondNeutral for a small title, couiColorPrimaryNeutral for
     * medium and large titles (see [PreferenceCategoryDefaults.titleColor]).
     */
    @Composable
    fun titleColor(
        titleType: PreferenceCategoryTitleType = PreferenceCategoryTitleType.Small,
    ): Color = PreferenceCategoryDefaults.titleColor(titleType = titleType)
}
