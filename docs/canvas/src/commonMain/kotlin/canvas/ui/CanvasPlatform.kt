// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package canvas.ui

/** What the editor needs from the browser. */
interface CanvasPlatform {
    /** Copies [text] to the clipboard. */
    fun copy(text: String)

    /** Hands [text] to the browser as a file download. */
    fun download(fileName: String, text: String, mime: String = "text/plain")

    /** Lets the person pick a file and returns its text through [onText]. */
    fun openFile(accept: String, onText: (String) -> Unit)

    fun load(key: String): String?

    fun save(key: String, value: String)

    /** The address of the app without the hash. */
    val baseUrl: String

    /** The current location hash, without `#`. */
    val hash: String

    /** Calls [onChange] whenever the location hash changes (a link pasted into this tab). */
    fun onHashChange(onChange: () -> Unit)

    /** Clears the hash without reloading (after a shared design was opened). */
    fun clearHash()

    /** `base64url(deflate-raw(text))`. */
    suspend fun deflate(text: String): String

    /** The inverse of [deflate]; null when the data is broken. */
    suspend fun inflate(data: String): String?

    /** The browser's preferred language starts with `zh`. */
    val prefersChinese: Boolean

    /** The system is in dark mode. */
    val prefersDark: Boolean

    /** Opens [url] in a new browser tab. */
    fun openUrl(url: String)
}
