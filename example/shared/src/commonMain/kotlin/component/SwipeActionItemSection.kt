// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.SmallTitle
import io.github.suqi8.coui.kmp.basic.SwipeAction
import io.github.suqi8.coui.kmp.basic.SwipeActionItem
import io.github.suqi8.coui.kmp.basic.rememberSwipeActionItemState
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.launch

private val SwipeDemoRows = listOf("Weekly report", "Team lunch", "Flight itinerary", "Invoice 2041", "Design review")

fun LazyListScope.swipeActionItemSection() {
    item(key = "swipeActionItem") {
        SmallTitle(text = "SwipeActionItem")
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
        ) {
            SwipeActionItemDemo()
        }
    }
}

@Composable
private fun SwipeActionItemDemo() {
    val rows = remember { mutableStateListOf(*SwipeDemoRows.toTypedArray()) }
    val scope = rememberCoroutineScope()
    val rowColor = COUITheme.colorScheme.surfaceContainer
    rows.forEachIndexed { index, title ->
        // Key by title so a deleted row's state does not move to its neighbour.
        key(title) {
            val state = rememberSwipeActionItemState()
            val actions = when (index % 3) {
                0 -> listOf(SwipeAction(label = "Pin", color = COUITheme.colorScheme.primary))

                1 -> listOf(
                    SwipeAction(label = "Mark", color = COUITheme.colorScheme.primary),
                    SwipeAction(label = "Move", color = COUITheme.colorScheme.onSurfaceSecondary),
                )

                else -> emptyList()
            }
            SwipeActionItem(
                actions = actions,
                onActionClick = { scope.launch { state.close() } },
                state = state,
                onDelete = { rows.remove(title) },
                fastDeleteEnabled = index % 3 == 2,
                deleteContentDescription = "Delete",
            ) {
                BasicComponent(
                    modifier = Modifier.background(rowColor),
                    title = title,
                    summary = if (index % 3 == 2) "Over-slide to delete" else "Swipe towards the start for actions",
                )
            }
        }
    }
    if (rows.size < SwipeDemoRows.size) {
        BasicComponent(
            title = "Restore rows",
            onClick = {
                rows.clear()
                rows.addAll(SwipeDemoRows)
            },
        )
    }
    SwipeActionItem(
        actions = emptyList(),
        onActionClick = {},
        enabled = false,
        onDelete = {},
    ) {
        BasicComponent(
            modifier = Modifier.background(rowColor),
            title = "Disabled",
            summary = "Swiping is off",
        )
    }
}
