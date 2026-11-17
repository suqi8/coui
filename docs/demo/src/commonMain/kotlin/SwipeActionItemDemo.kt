// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.BasicComponent
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.basic.SwipeAction
import io.github.suqi8.coui.kmp.basic.SwipeActionItem
import io.github.suqi8.coui.kmp.basic.SwipeActionMenuStyle
import io.github.suqi8.coui.kmp.basic.rememberSwipeActionItemState
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlinx.coroutines.launch

private val SwipeDemoTitles = listOf("Weekly report", "Team lunch", "Flight itinerary")

@Composable
fun SwipeActionItemDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val rows = remember { mutableStateListOf(*SwipeDemoTitles.toTypedArray()) }
            val scope = rememberCoroutineScope()
            val rowColor = COUITheme.colorScheme.cardBackground
            Card {
                rows.forEachIndexed { index, title ->
                    key(title) {
                        val state = rememberSwipeActionItemState()
                        SwipeActionItem(
                            actions = if (index < 2) {
                                listOf(SwipeAction(label = "Pin", color = COUITheme.colorScheme.primary))
                            } else {
                                emptyList()
                            },
                            onActionClick = { scope.launch { state.close() } },
                            state = state,
                            onDelete = { rows.remove(title) },
                            fastDeleteEnabled = index == 2,
                            deleteContentDescription = "Delete",
                            menuStyle = if (index == 1) SwipeActionMenuStyle.RoundRect else SwipeActionMenuStyle.Rect,
                        ) {
                            BasicComponent(
                                modifier = Modifier.background(rowColor),
                                title = title,
                                summary = when (index) {
                                    1 -> "Round rect actions"
                                    2 -> "Over-slide to delete"
                                    else -> "Swipe towards the start"
                                },
                            )
                        }
                    }
                }
                if (rows.size < SwipeDemoTitles.size) {
                    BasicComponent(
                        title = "Restore rows",
                        onClick = {
                            rows.clear()
                            rows.addAll(SwipeDemoTitles)
                        },
                    )
                }
            }
        }
    }
}
