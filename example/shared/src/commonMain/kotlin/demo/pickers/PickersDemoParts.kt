// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.pickers

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.DateValue
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * A centered secondary read-out line under a picker, e.g. the selected value.
 *
 * @param text The read-out.
 * @param modifier The modifier for the text.
 */
@Composable
internal fun PickerCaption(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
        style = COUITheme.textStyles.bodyS,
        color = COUITheme.colorScheme.labelSecondary,
        textAlign = TextAlign.Center,
    )
}

/** Two digits with a leading zero, e.g. `07`. */
internal fun Int.twoDigits(): String = toString().padStart(2, '0')

/** The ISO form of a date, e.g. `2026-07-26`; a debug read-out, not localized. */
internal fun DateValue.isoText(): String = "$year-${month.twoDigits()}-${day.twoDigits()}"
