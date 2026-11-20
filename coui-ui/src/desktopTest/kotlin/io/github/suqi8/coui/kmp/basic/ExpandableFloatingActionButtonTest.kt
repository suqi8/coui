// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class ExpandableFloatingActionButtonTest {

    @Test
    fun theMenuIgnoresClicksWhileItMovesAndAnItemCollapsesIt() = runComposeUiTest {
        var expanded by mutableStateOf(false)
        var actions by mutableIntStateOf(0)
        setContent {
            COUITheme {
                Box(Modifier.padding(start = 200.dp, top = 400.dp)) {
                    ExpandableFloatingActionButton(
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        items = listOf(
                            FloatingActionButtonItem(icon = {}, onClick = { actions++ }, label = "Edit"),
                            FloatingActionButtonItem(icon = {}, onClick = {}, label = "Share"),
                        ),
                        modifier = Modifier.testTag("fab"),
                    ) {}
                }
            }
        }
        mainClock.autoAdvance = false
        mainClock.advanceTimeBy(1000)

        onNodeWithTag("fab").performClick()
        mainClock.advanceTimeBy(50)
        assertTrue(expanded, "a click on the main button opens the menu")

        // mCOUIFloatingButtonAnimationIsRun: the main button ignores clicks while the menu expands.
        onNodeWithTag("fab").performClick()
        mainClock.advanceTimeBy(50)
        assertTrue(expanded, "a click while the menu expands must be ignored")

        mainClock.advanceTimeBy(1000)
        onNodeWithText("Edit").performClick()
        mainClock.advanceTimeBy(50)
        assertEquals(1, actions, "the item's action runs")
        assertFalse(expanded, "an item click collapses the menu")
    }

    @Test
    fun anItemCanKeepTheMenuOpen() = runComposeUiTest {
        var expanded by mutableStateOf(true)
        var actions by mutableIntStateOf(0)
        setContent {
            COUITheme {
                Box(Modifier.padding(start = 200.dp, top = 400.dp)) {
                    ExpandableFloatingActionButton(
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        items = listOf(FloatingActionButtonItem(icon = {}, onClick = { actions++ }, label = "Edit")),
                        collapseOnItemClick = false,
                    ) {}
                }
            }
        }
        mainClock.autoAdvance = false
        mainClock.advanceTimeBy(1000)

        onNodeWithText("Edit").performClick()
        mainClock.advanceTimeBy(50)
        assertEquals(1, actions)
        assertTrue(expanded, "with collapseOnItemClick off the menu stays open")
    }
}
