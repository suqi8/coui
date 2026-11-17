// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
@file:OptIn(ExperimentalWasmJsInterop::class)

package canvas.web

import canvas.ui.CanvasPlatform
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.js.ExperimentalWasmJsInterop

@JsFun("(text) => { try { navigator.clipboard.writeText(text); } catch (e) { console.warn(e); } }")
private external fun copyJs(text: String)

@JsFun(
    """
    (name, text, mime) => {
        const url = URL.createObjectURL(new Blob([text], { type: mime }));
        const a = document.createElement('a');
        a.href = url;
        a.download = name;
        document.body.appendChild(a);
        a.click();
        a.remove();
        setTimeout(() => URL.revokeObjectURL(url), 0);
    }
    """,
)
private external fun downloadJs(name: String, text: String, mime: String)

@JsFun(
    """
    (accept, onText) => {
        const input = document.createElement('input');
        input.type = 'file';
        input.accept = accept;
        input.onchange = () => {
            const file = input.files && input.files[0];
            if (file) file.text().then(onText);
        };
        input.click();
    }
    """,
)
private external fun openFileJs(accept: String, onText: (String) -> Unit)

@JsFun("(key) => { try { return localStorage.getItem(key); } catch (e) { return null; } }")
private external fun loadJs(key: String): String?

@JsFun("(key, value) => { try { localStorage.setItem(key, value); } catch (e) { } }")
private external fun saveJs(key: String, value: String)

@JsFun("() => location.href.split('#')[0]")
private external fun baseUrlJs(): String

@JsFun("() => location.hash.replace(/^#/, '')")
private external fun hashJs(): String

@JsFun("() => history.replaceState(null, '', location.pathname + location.search)")
private external fun clearHashJs()

@JsFun("(cb) => window.addEventListener('hashchange', () => cb())")
private external fun onHashChangeJs(onChange: () -> Unit)

@JsFun("() => (navigator.language || '').toLowerCase().startsWith('zh')")
private external fun prefersChineseJs(): Boolean

@JsFun("() => !!(window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches)")
private external fun prefersDarkJs(): Boolean

@JsFun("(url) => window.open(url, '_blank', 'noopener')")
private external fun openUrlJs(url: String)

@JsFun(
    """
    (text, onOk, onErr) => {
        try {
            const stream = new Blob([new TextEncoder().encode(text)]).stream().pipeThrough(new CompressionStream('deflate-raw'));
            new Response(stream).arrayBuffer().then((buf) => {
                const bytes = new Uint8Array(buf);
                let bin = '';
                for (let i = 0; i < bytes.length; i += 0x8000) bin += String.fromCharCode.apply(null, bytes.subarray(i, i + 0x8000));
                onOk(btoa(bin).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, ''));
            }).catch((e) => onErr(String(e)));
        } catch (e) { onErr(String(e)); }
    }
    """,
)
private external fun deflateJs(text: String, onOk: (String) -> Unit, onErr: (String) -> Unit)

@JsFun(
    """
    (data, onOk, onErr) => {
        try {
            const b64 = data.replace(/-/g, '+').replace(/_/g, '/') + '='.repeat((4 - (data.length % 4)) % 4);
            const bin = atob(b64);
            const bytes = new Uint8Array(bin.length);
            for (let i = 0; i < bin.length; i++) bytes[i] = bin.charCodeAt(i);
            const stream = new Blob([bytes]).stream().pipeThrough(new DecompressionStream('deflate-raw'));
            new Response(stream).text().then(onOk).catch((e) => onErr(String(e)));
        } catch (e) { onErr(String(e)); }
    }
    """,
)
private external fun inflateJs(data: String, onOk: (String) -> Unit, onErr: (String) -> Unit)

/** The browser behind the editor. */
class BrowserPlatform : CanvasPlatform {
    override fun copy(text: String) = copyJs(text)

    override fun download(fileName: String, text: String, mime: String) = downloadJs(fileName, text, mime)

    override fun openFile(accept: String, onText: (String) -> Unit) = openFileJs(accept, onText)

    override fun load(key: String): String? = loadJs(key)

    override fun save(key: String, value: String) = saveJs(key, value)

    override val baseUrl: String get() = baseUrlJs()

    override val hash: String get() = hashJs()

    override fun clearHash() = clearHashJs()

    override fun onHashChange(onChange: () -> Unit) = onHashChangeJs(onChange)

    override suspend fun deflate(text: String): String = suspendCancellableCoroutine { cont ->
        deflateJs(text, { if (cont.isActive) cont.resume(it) }, { if (cont.isActive) cont.resume("") })
    }

    override suspend fun inflate(data: String): String? = suspendCancellableCoroutine { cont ->
        inflateJs(data, { if (cont.isActive) cont.resume(it) }, { if (cont.isActive) cont.resume(null) })
    }

    override val prefersChinese: Boolean get() = prefersChineseJs()

    override val prefersDark: Boolean get() = prefersDarkJs()

    override fun openUrl(url: String) = openUrlJs(url)
}
