// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package demo.selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.theme.COUITheme

/**
 * A row of a variant card: a label at the start and the demoed controls at the end.
 *
 * @param label The row label.
 * @param modifier The modifier for the row.
 * @param content The controls, spaced 12dp apart.
 */
@Composable
internal fun LabeledControlsRow(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
            style = COUITheme.textStyles.bodyXL,
            color = COUITheme.colorScheme.labelPrimary,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}

/**
 * A block of a variant card: a small secondary label above full-width content.
 *
 * @param label The block label.
 * @param modifier The modifier for the block.
 * @param content The demoed content.
 */
@Composable
internal fun LabeledBlock(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = COUITheme.textStyles.bodyS,
            color = COUITheme.colorScheme.labelSecondary,
        )
        content()
    }
}

/**
 * A centered secondary read-out line, e.g. the value under a preview.
 *
 * @param text The read-out.
 * @param modifier The modifier for the text.
 */
@Composable
internal fun ValueCaption(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        style = COUITheme.textStyles.bodyS,
        color = COUITheme.colorScheme.labelSecondary,
        textAlign = TextAlign.Center,
    )
}
