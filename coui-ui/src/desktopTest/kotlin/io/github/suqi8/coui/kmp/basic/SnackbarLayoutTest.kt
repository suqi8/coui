// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class SnackbarLayoutTest {

    private fun data(message: String, action: String?) = object : SnackbarData {
        override val visuals = SnackbarVisuals(message, action, withDismissAction = false, duration = SnackbarDuration.Indefinite)
        override suspend fun dismiss() = Unit
        override suspend fun performAction() = Unit
    }

    @Test
    fun shortMessageKeepsTheActionOnTheSameLine() = runComposeUiTest {
        setContent {
            COUITheme {
                Box(Modifier.width(360.dp)) { Snackbar(data("Saved", "Undo")) }
            }
        }
        val message = onNodeWithText("Saved").getUnclippedBoundsInRoot()
        val action = onNodeWithText("Undo").getUnclippedBoundsInRoot()
        assertTrue(action.left >= message.right, "action must follow the message: $message / $action")
        assertTrue(action.top < message.bottom, "action must share the message line: $message / $action")
    }

    @Test
    fun wrappingMessageMovesTheActionBelowAndToTheEnd() = runComposeUiTest {
        val text = "The file has been moved to the recycle bin and will be deleted permanently after 30 days"
        setContent {
            COUITheme {
                Box(Modifier.width(360.dp)) { Snackbar(data(text, "Undo")) }
            }
        }
        val message = onNodeWithText(text).getUnclippedBoundsInRoot()
        val action = onNodeWithText("Undo").getUnclippedBoundsInRoot()
        assertTrue(action.top >= message.bottom, "action must sit below the message: $message / $action")
        // 360dp box - 16dp outer margin - 4dp action end margin.
        assertTrue(action.right > 330.dp && action.right <= 340.dp, "action must hug the end: $action")
    }
}
