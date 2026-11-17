// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.CardListGroup
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.Text
import io.github.suqi8.coui.kmp.theme.COUITheme

fun LazyListScope.basicComponentSection() {
    item(key = "basicComponent") {
        SmallTitle(text = "Basic Component")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            CardListGroup {
                item {
                    BasicComponent(
                        title = "Title",
                        summary = "Summary",
                        startAction = {
                            Text(
                                text = "Start",
                            )
                        },
                        endActions = {
                            Text(
                                text = "End1",
                                fontSize = COUITheme.textStyles.bodyM.fontSize,
                                color = COUITheme.colorScheme.labelTertiary,
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "End2",
                                fontSize = COUITheme.textStyles.bodyM.fontSize,
                                color = COUITheme.colorScheme.labelTertiary,
                            )
                        },
                        enabled = true,
                    )
                }
                item {
                    BasicComponent(
                        title = "Title",
                        summary = "Summary",
                        startAction = {
                            Text(
                                text = "Start",
                                color = COUITheme.colorScheme.disabledNeutral,
                            )
                        },
                        endActions = {
                            Text(
                                text = "End1",
                                fontSize = COUITheme.textStyles.bodyM.fontSize,
                                color = COUITheme.colorScheme.disabledNeutral,
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "End2",
                                fontSize = COUITheme.textStyles.bodyM.fontSize,
                                color = COUITheme.colorScheme.disabledNeutral,
                            )
                        },
                        enabled = false,
                    )
                }
                item {
                    var selected by remember { mutableStateOf(false) }
                    BasicComponent(
                        title = "Selectable",
                        summary = if (selected) "Selected: the dividers around this row hide" else "Tap to select",
                        endBadgeCount = 3,
                        selected = selected,
                        onClick = { selected = !selected },
                    )
                }
                item {
                    BasicComponent(
                        title = "Red dot",
                        summary = "A plain end red dot",
                        endBadgeCount = 0,
                        onClick = {},
                    )
                }
            }
        }
    }
}
