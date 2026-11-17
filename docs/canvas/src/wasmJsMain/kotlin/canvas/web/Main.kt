// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(ExperimentalWasmJsInterop::class)

package canvas.web

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.window.ComposeViewport
import canvas.ui.CanvasApp
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.js.ExperimentalWasmJsInterop

/** MiSans with its CJK subsets, the font the COUI example site uses. */
private const val FONT_CSS = "https://cdn-font.hyperos.mi.com/font/css?family=MiSans_VF:VF:Chinese_Simplify&display=swap"

@JsFun("() => { const el = document.getElementById('loading'); if (el) { el.classList.add('hidden'); setTimeout(() => el.remove(), 400); } }")
private external fun hideLoading()

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(viewportContainerId = "canvasApp") {
        val platform = remember { BrowserPlatform() }
        val resolver = LocalFontFamilyResolver.current
        val scope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            // Chinese labels need the CJK font: wait for it a little, then show the editor anyway
            // while the remaining subsets keep loading.
            val fonts = scope.launch { preloadWebFonts(queryParam("cssUrl").ifBlank { FONT_CSS }, resolver) }
            withTimeoutOrNull(8_000) { fonts.join() }
            hideLoading()
        }
        CanvasApp(platform)
    }
}
