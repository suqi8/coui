// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package io.github.suqi8.coui.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.theme.COUITheme
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class TooltipTest {

    @Test
    fun tooltipShowsAboveTheAnchorAndTheCloseIconDismissesIt() = runComposeUiTest {
        var closed = false
        setContent {
            COUITheme {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    TooltipBox(
                        state = rememberTooltipState(initialIsVisible = true),
                        tooltip = { Tooltip(text = "Hello tooltip", onCloseClick = { closed = true }) },
                    ) {
                        Box(Modifier.size(48.dp).testTag("anchor"))
                    }
                }
            }
        }
        waitForIdle()
        val anchor = onNodeWithTag("anchor").fetchSemanticsNode().boundsInWindow
        val text = onNodeWithText("Hello tooltip").fetchSemanticsNode().boundsInWindow
        assertTrue(text.bottom <= anchor.top, "tooltip text $text must sit above the anchor $anchor")
        assertTrue(text.left < anchor.center.x, "tooltip $text must straddle the anchor centre ${anchor.center}")

        onNodeWithContentDescription(tooltipCloseDescription(Locale.current)).performClick()
        waitForIdle()
        assertTrue(closed, "the close icon must call onCloseClick")
        onNodeWithText("Hello tooltip").assertDoesNotExist()
    }

    @Test
    fun belowPlacementPutsTheTooltipUnderTheAnchor() = runComposeUiTest {
        setContent {
            COUITheme {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    TooltipBox(
                        state = rememberTooltipState(initialIsVisible = true),
                        tooltip = { Tooltip(text = "Below", mode = TooltipMode.Info) },
                        placement = TooltipAnchorPosition.Below,
                    ) {
                        Box(Modifier.size(48.dp).testTag("anchor"))
                    }
                }
            }
        }
        waitForIdle()
        val anchor = onNodeWithTag("anchor").fetchSemanticsNode().boundsInWindow
        val text = onNodeWithText("Below").fetchSemanticsNode().boundsInWindow
        assertTrue(text.top >= anchor.bottom, "tooltip text $text must sit below the anchor $anchor")
        onNodeWithContentDescription(tooltipCloseDescription(Locale.current)).assertDoesNotExist()
    }

    @Test
    fun exitIsCutAt320Milliseconds() = runComposeUiTest {
        val state = TooltipStateHolder()
        setContent {
            COUITheme {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    state.value = rememberTooltipState(initialIsVisible = true)
                    TooltipBox(state = state.value!!, tooltip = { Tooltip(text = "Exit", mode = TooltipMode.Info) }) {
                        Box(Modifier.size(48.dp))
                    }
                }
            }
        }
        waitForIdle()
        mainClock.autoAdvance = false
        runOnIdle { state.value!!.dismiss() }
        mainClock.advanceTimeBy(250)
        onNodeWithText("Exit").assertExists()
        assertTrue(state.value!!.isVisible)
        mainClock.advanceTimeBy(150)
        onNodeWithText("Exit").assertDoesNotExist()
        assertFalse(state.value!!.isVisible)
    }

    @Test
    fun secondDismissDuringTheExitRemovesTheTooltipAtOnce() = runComposeUiTest {
        val state = TooltipStateHolder()
        setContent {
            COUITheme {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    state.value = rememberTooltipState(initialIsVisible = true)
                    TooltipBox(state = state.value!!, tooltip = { Tooltip(text = "Twice", mode = TooltipMode.Info) }) {
                        Box(Modifier.size(48.dp))
                    }
                }
            }
        }
        waitForIdle()
        mainClock.autoAdvance = false
        runOnIdle { state.value!!.dismiss() }
        mainClock.advanceTimeBy(50)
        onNodeWithText("Twice").assertExists()
        runOnIdle { state.value!!.dismiss() }
        mainClock.advanceTimeBy(32)
        onNodeWithText("Twice").assertDoesNotExist()
    }

    @Test
    fun removedAnchorDismissesTheTooltipImmediately() = runComposeUiTest {
        val state = TooltipStateHolder()
        var anchorShown by mutableStateOf(true)
        setContent {
            COUITheme {
                state.value = rememberTooltipState(initialIsVisible = true)
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (anchorShown) {
                        TooltipBox(state = state.value!!, tooltip = { Tooltip(text = "Gone", mode = TooltipMode.Info) }) {
                            Box(Modifier.size(48.dp))
                        }
                    }
                }
            }
        }
        waitForIdle()
        onNodeWithText("Gone").assertExists()
        mainClock.autoAdvance = false
        anchorShown = false
        mainClock.advanceTimeByFrame()
        onNodeWithText("Gone").assertDoesNotExist()
        assertFalse(state.value!!.isVisible)
    }

    @Test
    fun zeroSizeAnchorDismissesTheTooltipImmediately() = runComposeUiTest {
        val state = TooltipStateHolder()
        var anchorSize by mutableStateOf(48.dp)
        setContent {
            COUITheme {
                state.value = rememberTooltipState(initialIsVisible = true)
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    TooltipBox(state = state.value!!, tooltip = { Tooltip(text = "Shrunk", mode = TooltipMode.Info) }) {
                        Box(Modifier.size(anchorSize))
                    }
                }
            }
        }
        waitForIdle()
        onNodeWithText("Shrunk").assertExists()
        anchorSize = 0.dp
        waitForIdle()
        onNodeWithText("Shrunk").assertDoesNotExist()
        assertFalse(state.value!!.isVisible)
    }

    @Test
    fun sideFallbackMeasuresLazyContent() = runComposeUiTest {
        setContent {
            COUITheme {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                    TooltipBox(
                        state = rememberTooltipState(initialIsVisible = true),
                        placement = TooltipAnchorPosition.End,
                        tooltip = {
                            TooltipBubble(style = TooltipStyle.Default, mode = TooltipMode.Info) {
                                LazyColumn {
                                    items(3) { Text(text = "Lazy $it") }
                                }
                            }
                        },
                    ) {
                        Box(Modifier.size(48.dp).testTag("anchor"))
                    }
                }
            }
        }
        waitForIdle()
        val anchor = onNodeWithTag("anchor").fetchSemanticsNode().boundsInWindow
        val last = onNodeWithText("Lazy 2").fetchSemanticsNode().boundsInWindow
        // The End tooltip would reach the window bottom, so COUI shows it above the anchor instead.
        assertTrue(last.bottom <= anchor.top, "the fallback tooltip $last must sit above the anchor $anchor")
    }

    @Test
    fun toolbarAnchorsUseTheToolbarSpacing() = runComposeUiTest {
        setContent {
            COUITheme {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    ProvideTooltipAnchorContainer(TooltipAnchorType.Toolbar) {
                        TooltipBox(
                            state = rememberTooltipState(initialIsVisible = true),
                            placement = TooltipAnchorPosition.Below,
                            tooltip = { Tooltip(text = "Toolbar", mode = TooltipMode.Info) },
                        ) {
                            Box(Modifier.size(48.dp).testTag("anchor"))
                        }
                    }
                }
            }
        }
        waitForIdle()
        val anchor = onNodeWithTag("anchor").fetchSemanticsNode().boundsInWindow
        val text = onNodeWithText("Toolbar").fetchSemanticsNode().boundsInWindow
        // Window top = anchor bottom - 10.1dp, then 13dp inset + 12dp margin to the text.
        val gap = text.top - anchor.bottom
        assertTrue(gap > 12f && gap < 18f, "toolbar tooltip text should start ~14.9dp below the anchor, was $gap")
    }

    @Test
    fun closeDescriptionIsLocalised() {
        assertEquals("Close this tip", tooltipCloseDescription(Locale("en-US")))
        assertEquals("关闭提示", tooltipCloseDescription(Locale("zh-CN")))
        assertEquals("關閉提示", tooltipCloseDescription(Locale("zh-TW")))
        assertEquals("關閉提示", tooltipCloseDescription(Locale("zh-Hant-HK")))
        assertEquals("Cerrar esta sugerencia", tooltipCloseDescription(Locale("es-MX")))
        assertEquals("Cerrar este consejo", tooltipCloseDescription(Locale("es-ES")))
        assertEquals("Close this tip", tooltipCloseDescription(Locale("pt-PT")))
        assertEquals("Fechar dica", tooltipCloseDescription(Locale("pt-BR")))
    }

    private class TooltipStateHolder {
        var value: TooltipState? = null
    }
}
