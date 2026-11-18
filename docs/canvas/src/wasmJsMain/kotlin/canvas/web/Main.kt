// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(ExperimentalWasmJsInterop::class)

package canvas.web

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.window.ComposeViewport
import canvas.ui.CanvasApp
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.js.ExperimentalWasmJsInterop

/** MiSans with its CJK subsets, the font the COUI example site uses. */
private const val FONT_CSS = "https://cdn-font.hyperos.mi.com/font/css?family=MiSans_VF:VF:Chinese_Simplify&display=swap"

/**
 * How long the loading screen waits for the CJK subsets before showing the editor anyway; text laid out
 * before its glyphs arrive keeps showing boxes until it is laid out again.
 */
private const val FONT_WAIT_MS = 45_000L

@JsFun("(done, total) => { const el = document.querySelector('#loading .sub'); if (el && total > 0) el.textContent = 'Loading fonts ' + done + ' / ' + total + '…'; }")
private external fun setFontProgress(done: Int, total: Int)

@JsFun("() => { const el = document.getElementById('loading'); if (el) { el.classList.add('hidden'); setTimeout(() => el.remove(), 400); } }")
private external fun hideLoading()

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(viewportContainerId = "canvasApp") {
        val platform = remember { BrowserPlatform() }
        val resolver = LocalFontFamilyResolver.current
        val scope = rememberCoroutineScope()
        var fontEpoch by remember { mutableIntStateOf(0) }
        LaunchedEffect(Unit) {
            val fonts = scope.launch {
                preloadWebFonts(queryParam("cssUrl").ifBlank { FONT_CSS }, resolver, ::setFontProgress)
            }
            withTimeoutOrNull(FONT_WAIT_MS) { fonts.join() }
            // The editor composed behind the loading screen before the glyphs arrived; lay it out
            // again with them. The design lives in local storage, so remounting loses nothing.
            fontEpoch++
            hideLoading()
            if (fonts.isActive) {
                fonts.join()
                fontEpoch++
            }
        }
        key(fontEpoch) { CanvasApp(platform) }
    }
}
