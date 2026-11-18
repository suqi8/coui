// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(ExperimentalWasmJsInterop::class)

package canvas.web

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.window.ComposeViewport
import canvas.ui.CanvasApp
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.js.ExperimentalWasmJsInterop

/** MiSans with its CJK subsets, the font the COUI example site uses. */
private const val FONT_CSS = "https://cdn-font.hyperos.mi.com/font/css?family=MiSans_VF:VF:Chinese_Simplify&display=swap"

/** How long the loading screen waits for the CJK subsets before showing the editor anyway. */
private const val FONT_WAIT_MS = 45_000L

@JsFun("(done, total) => { if (window.__couiLoading) window.__couiLoading.fontProgress(done, total); }")
private external fun setFontProgress(done: Int, total: Int)

@JsFun("() => { if (window.__couiLoading) window.__couiLoading.hide(); }")
private external fun hideLoading()

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(viewportContainerId = "canvasApp") {
        val platform = remember { BrowserPlatform() }
        val resolver = LocalFontFamilyResolver.current
        val scope = rememberCoroutineScope()
        // The editor's labels are plain strings, so it is composed only once the CJK subsets are in:
        // text measured before its glyphs arrive keeps the narrow width of the missing-glyph boxes,
        // and Skia's paragraph cache hands that layout back even to a remounted tree.
        var fontsReady by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            val fonts = scope.launch {
                preloadWebFonts(queryParam("cssUrl").ifBlank { FONT_CSS }, resolver, ::setFontProgress)
            }
            withTimeoutOrNull(FONT_WAIT_MS) { fonts.join() }
            fontsReady = true
            // Let the editor paint its first frame under the loading screen before revealing it. A
            // background tab gets no animation frames, so the wait is capped.
            withTimeoutOrNull(500) {
                withFrameNanos {}
                withFrameNanos {}
            }
            hideLoading()
        }
        if (fontsReady) CanvasApp(platform)
    }
}
