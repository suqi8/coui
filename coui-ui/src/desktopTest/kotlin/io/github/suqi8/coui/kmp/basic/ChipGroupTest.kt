// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class ChipGroupTest {

    @Test
    fun collapsingFadesTheHiddenChipsOutBeforeDroppingThem() = runComposeUiTest {
        val state = ChipGroupState()
        setContent {
            COUITheme {
                Box(Modifier.width(200.dp)) {
                    ChipGroup(state = state, collapsable = true, collapsedMaxRows = 1) {
                        repeat(8) { index ->
                            Chip(
                                selected = false,
                                onClick = {},
                                label = "Chip $index",
                                modifier = Modifier.testTag("chip$index"),
                            )
                        }
                    }
                }
            }
        }
        mainClock.autoAdvance = false
        mainClock.advanceTimeBy(500)
        // The fourth chip sits on the second row, which a one-row collapse hides.
        onNodeWithTag("chip3").assertIsDisplayed()

        state.collapsed = true
        mainClock.advanceTimeBy(32)
        // COUIChip.ChipGroupAnimatorImpl fades a hidden chip out where it was.
        onNodeWithTag("chip3").assertIsDisplayed()

        mainClock.advanceTimeBy(1000)
        val placed = onAllNodesWithTag("chip3").fetchSemanticsNodes().any { it.layoutInfo.isPlaced }
        assertTrue(!placed, "a chip that has faded out must leave the layout")
    }
}
