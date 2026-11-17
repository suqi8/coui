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
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.WindowInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.overlay.OverlayCascadingListPopup
import io.github.suqi8.coui.kmp.theme.COUITheme
import io.github.suqi8.coui.kmp.theme.darkColorScheme
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * The first frames of a sub menu opening. On a compact window the sub menu's surface starts as the
 * anchor-row band (`SmallScreenAnimationController.configSubMenuAnimationPropertiesForSubMenuEnter`,
 * `setOverrideOutline`) and must never be measured at the full panel size for a frame, which used to
 * flash a whole panel over the main menu. The main menu's last row has to stay readable while the
 * band is still small, and beside a larger window's sub menu.
 */
@OptIn(ExperimentalTestApi::class)
class PopupExpandTest {

    private fun expand(compact: Boolean, dark: Boolean) = runComposeUiTest {
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
        // The main menu below the anchor row: View, the group divider and Delete.
        val view = onNodeWithText("View", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val delete = onNodeWithText("Delete", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val region = with(density) {
            IntRect(view.left.roundToPx(), view.top.roundToPx(), (view.left + 120.dp).roundToPx(), delete.bottom.roundToPx())
        }
        val before = onRoot().captureToImage().toPixelMap()
        fun change(): Float {
            val now = onRoot().captureToImage().toPixelMap()
            var sum = 0f
            for (x in region.left until region.right) {
                for (y in region.top until region.bottom) {
                    val a = before[x, y]
                    val b = now[x, y]
                    sum += abs(a.red - b.red) + abs(a.green - b.green) + abs(a.blue - b.blue)
                }
            }
            return sum / (3f * region.width * region.height)
        }
        mainClock.autoAdvance = false
        onNodeWithText("More", useUnmergedTree = true).performClick()
        // The sub menu is first drawn on frame 2: it is the anchor-row band, so nothing below it may
        // change yet; on frame 3 only the main menu has started to shrink (a surface measured at the
        // full panel size changes the region by about 0.04 at once).
        repeat(4) { frame ->
            val change = change()
            val limit = if (frame < 3) 0.01f else 0.03f
            assertTrue(change < limit, "frame $frame: the main menu below the anchor row changed by $change")
            mainClock.advanceTimeByFrame()
        }
    }

    @Test
    fun compactLightOpensFromTheAnchorBand() = expand(compact = true, dark = false)

    @Test
    fun compactDarkOpensFromTheAnchorBand() = expand(compact = true, dark = true)

    @Test
    fun largerWindowDarkOpensBesideTheMainMenu() = expand(compact = false, dark = true)
}
