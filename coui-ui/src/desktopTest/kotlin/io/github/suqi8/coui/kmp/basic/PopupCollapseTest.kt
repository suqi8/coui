// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.WindowInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.click
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.overlay.OverlayCascadingListPopup
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.darkColorScheme
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The main menu while a sub menu opens and closes: its rows (and their sub-menu indicators) follow
 * the `MenuTransitionUnit` progress frame by frame (`SmallScreenAnimationController` /
 * `DefaultScreenAnimationController` `animateToSubMenuEnterState` / `animateToSubMenuExitState`),
 * shrinking while the sub menu is open and never jumping when it starts to close.
 */
@OptIn(ExperimentalTestApi::class)
class PopupCollapseTest {

    private fun openAndClose(compact: Boolean, dark: Boolean) = runComposeUiTest {
        val entries = listOf(
            DropdownEntry(
                items = listOf(
                    DropdownItem(text = "New folder"),
                    DropdownItem(text = "More", children = listOf("Alpha", "Beta", "Gamma").map { DropdownItem(text = it) }),
                    DropdownItem(text = "View"),
                ),
            ),
            DropdownEntry(items = listOf(DropdownItem(text = "Delete"))),
        )
        setContent {
            val real = LocalWindowInfo.current
            val info = remember {
                object : WindowInfo {
                    override val isWindowFocused: Boolean get() = real.isWindowFocused
                    override val containerSize: IntSize get() = if (compact) IntSize(400, 760) else real.containerSize
                }
            }
            CompositionLocalProvider(LocalWindowInfo provides info) {
                COUITheme(colors = if (dark) darkColorScheme() else COUITheme.colorScheme) {
                    Scaffold {
                        var show by remember { mutableStateOf(false) }
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
                            Box(Modifier.padding(start = 100.dp)) {
                                TextButton(
                                    text = "Anchor",
                                    onClick = { show = true },
                                    modifier = Modifier.testTag("anchor").padding(top = 16.dp),
                                )
                                OverlayCascadingListPopup(
                                    show = show,
                                    entries = entries,
                                    onDismissRequest = { show = false },
                                    // The touch light would light the whole region; measure the surfaces only.
                                    materialEffect = false,
                                )
                            }
                        }
                    }
                }
            }
        }
        onNodeWithTag("anchor").performClick()
        mainClock.advanceTimeBy(1500)
        waitForIdle()
        mainClock.autoAdvance = false
        // The main menu's "View" row: its bounds carry the main menu's layer transform.
        fun view() = onNodeWithText("View", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val rest = view()
        val track = mutableListOf(rest)
        onNodeWithText("More", useUnmergedTree = true).performClick()
        repeat(40) {
            mainClock.advanceTimeByFrame()
            track += view()
        }
        val open = track.last()
        // Collapse with a tap outside the popup.
        onRoot().performTouchInput { click(Offset(5f, 700f)) }
        repeat(40) {
            mainClock.advanceTimeByFrame()
            track += view()
        }
        // The row's origin moves as the main menu shrinks about its pivot.
        assertTrue(
            abs((open.left - rest.left).value) > 2f || abs((open.top - rest.top).value) > 2f,
            "the main menu shrinks under an open sub menu: $rest -> $open",
        )
        val jumps = track.zipWithNext { a, b -> maxOf(abs((b.top - a.top).value), abs((b.left - a.left).value)) }
        assertTrue(jumps.max() < 3f, "a frame jumps ${jumps.max()}dp: $jumps")
        assertEquals(rest, track.last())
    }

    @Test
    fun compactMainMenuFollowsTheSubMenu() = openAndClose(compact = true, dark = false)

    @Test
    fun largerWindowMainMenuFollowsTheSubMenu() = openAndClose(compact = false, dark = true)
}
