// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.preference

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * An invisible placeholder that closes a settings list (COUI `COUIBottomPreference`, layout
 * `coui_preference_bottom`: an invisible full-width `FrameLayout` whose height
 * `COUIBottomPreference.onBindViewHolder` sets to `mPlaceholderHeight`).
 *
 * Put it as the last item of a scrolling settings page to keep the last card off the bottom edge.
 *
 * @param modifier The modifier to be applied to the [BottomPreference].
 * @param placeholderHeight The height of the placeholder (COUI `placeholderHeight`).
 */
@Composable
@NonRestartableComposable
fun BottomPreference(
    modifier: Modifier = Modifier,
    placeholderHeight: Dp = BottomPreferenceDefaults.PlaceholderHeight,
) {
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(placeholderHeight),
    )
}

object BottomPreferenceDefaults {

    /**
     * The default placeholder height (COUI support_preference_foot_preference_padding_bottom =
     * coui_list_to_ex_bottom_padding).
     */
    val PlaceholderHeight: Dp = 32.dp
}
