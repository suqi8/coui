// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0

// Loading screen with real progress: the WebAssembly modules are counted by the bytes that actually
// arrive (three quarters of the bar), then the app reports its font subsets. The corner log lists
// every step with its timing and any failure. The app script named by data-app is loaded only after
// the browser passed the WebAssembly GC and WebGL 2 checks.
// The app calls window.__couiLoading.fontProgress(done, total) and window.__couiLoading.hide().
(function () {
    var config = document.currentScript ? document.currentScript.dataset : {};
    var APP_SCRIPT = config.app;
    var EXPECTED_MODULES = Number(config.modules) || 2;
    var GUESS_BYTES = Number(config.guessBytes) || 8e6;
    var CODE_SHARE = 0.75, LOG_LINES = 16;
    var zh = /^zh\b/i.test(navigator.language || '');
    function tr(en, cn) { return zh ? cn : en; }

    var files = {}, fonts = null, shown = 0, started = Date.now();
    var screen0 = document.getElementById('loading');
    var bar = document.getElementById('loading-bar'), text = document.getElementById('loading-text');
    var logBox = document.getElementById('loading-log'), logRows = {};
    function mb(n) { return (n / 1048576).toFixed(1); }
    function since(t) { return ((Date.now() - t) / 1000).toFixed(1) + 's'; }
    function fileName(url) { return String(url).split('?')[0].split('/').pop() || String(url); }
    function reason(error) { return (error && (error.message || error.name)) || String(error); }

    // One row per step; a keyed step rewrites its own row.
    function log(key, message, failed) {
        if (!logBox || !document.getElementById('loading')) return;
        var row = key && logRows[key];
        if (!row) {
            row = document.createElement('div');
            row.className = 'loading-log-row';
            // A row keeps the time its step began; the message says how long it took.
            row.dataset.at = since(started);
            logBox.appendChild(row);
            if (key) logRows[key] = row;
            while (logBox.children.length > LOG_LINES) logBox.removeChild(logBox.firstChild);
        }
        row.classList.toggle('failed', !!failed);
        row.textContent = row.dataset.at.padStart(6, ' ') + '  ' + message;
    }

    function finishDownload(url) {
        var entry = files[url];
        if (!entry || entry.done) return;
        entry.done = true;
        entry.total = entry.loaded = Math.max(entry.loaded, 1);
        var timing = window.performance && performance.getEntriesByName && performance.getEntriesByName(url)[0];
        var cached = timing && timing.transferSize === 0 && timing.decodedBodySize > 0;
        log('wasm-' + url, tr('Downloaded ', '下载 ') + fileName(url) + ' ' + mb(entry.loaded) + ' MB' +
            tr(' in ', '，用时 ') + since(entry.at) + (cached ? tr(' (browser cache)', '（浏览器缓存）') : ''));
        render();
    }

    function render() {
        var loaded = 0, total = 0, count = 0;
        for (var k in files) { var f = files[k]; loaded += Math.min(f.loaded, f.total); total += f.total; count++; }
        total += Math.max(0, EXPECTED_MODULES - count) * GUESS_BYTES;
        var code = total ? loaded / total : 0, value, label;
        if (fonts) {
            value = CODE_SHARE + (1 - CODE_SHARE) * (fonts.total ? fonts.done / fonts.total : 1);
            label = tr('Loading fonts ', '正在加载字体 ') + fonts.done + ' / ' + fonts.total;
        } else if (count >= EXPECTED_MODULES && loaded >= total) {
            value = CODE_SHARE;
            label = tr('Starting…', '正在启动…');
        } else {
            value = CODE_SHARE * code;
            label = count ? tr('Downloading ', '正在下载 ') + mb(loaded) + ' / ' + mb(total) + ' MB' : tr('Loading…', '正在加载…');
        }
        // The bar never moves backwards, even when a later module's size replaces the guess.
        shown = Math.max(shown, Math.min(1, value));
        if (bar) bar.style.width = (shown * 100).toFixed(1) + '%';
        if (text) text.textContent = label;
    }

    window.__couiLoading = {
        log: log,
        fontProgress: function (done, total) {
            if (!fonts) log('ui', tr('Loading the CJK font', '正在加载中文字体'));
            fonts = { done: done, total: total };
            log('fonts', tr('Fonts ', '字体 ') + done + ' / ' + total + (done >= total ? tr(', done', '，完成') : ''));
            render();
        },
        hide: function () {
            if (!screen0) return;
            screen0.classList.add('hidden');
            setTimeout(function () { screen0.remove(); }, 300);
        },
    };

    log('', tr('Loading ', '开始加载 ') + location.host + location.pathname);
    (function () {
        var nav = navigator, parts = [];
        var brands = nav.userAgentData && nav.userAgentData.brands;
        var brand = brands && brands.filter(function (b) { return !/Not.?A.?Brand|Chromium/i.test(b.brand); })[0];
        if (brand) parts.push(brand.brand + ' ' + brand.version);
        if (nav.hardwareConcurrency) parts.push(nav.hardwareConcurrency + tr(' cores', ' 核'));
        if (nav.deviceMemory) parts.push(nav.deviceMemory + tr(' GB memory', ' GB 内存'));
        var net = nav.connection;
        if (net && net.effectiveType) parts.push(tr('network ', '网络 ') + net.effectiveType + (net.downlink ? ' ~' + net.downlink + ' Mbps' : ''));
        parts.push(window.screen.width + 'x' + window.screen.height + ' @' + (window.devicePixelRatio || 1) + 'x');
        log('', tr('Environment: ', '环境：') + parts.join(' · '));
    })();
    window.addEventListener('error', function (e) {
        var target = e.target;
        // A stylesheet that fails (such as an optional @import) does not stop the app.
        if (target && target.tagName === 'LINK') return;
        if (target && target !== window && (target.src || target.href)) log('', tr('Failed to load ', '资源加载失败 ') + fileName(target.src || target.href), true);
        else log('', tr('Script error ', '脚本错误 ') + (e.message || e), true);
    }, true);
    window.addEventListener('unhandledrejection', function (e) { log('', tr('Unhandled error ', '未处理的错误 ') + reason(e.reason), true); });

    // Streaming compilation overlaps the download, so only the moment a module is ready is logged.
    if (window.WebAssembly) ['instantiateStreaming', 'compileStreaming', 'instantiate'].forEach(function (name) {
        var original = WebAssembly[name];
        if (typeof original !== 'function') return;
        WebAssembly[name] = function (source) {
            var t = Date.now();
            // The streaming calls take the fetch's response, which tells which module this is.
            var sourceUrl = Promise.resolve(source).then(function (r) { return (r && r.url) || ''; }, function () { return ''; });
            return original.apply(this, arguments).then(function (result) {
                sourceUrl.then(function (url) {
                    if (url) finishDownload(url);
                    log('', tr('WebAssembly module ready', 'WebAssembly 模块就绪') + (url ? ' ' + fileName(url) : '') +
                        tr(', download and compile took ', '，下载加编译共 ') + since(t));
                });
                return result;
            }, function (error) {
                log('', tr('WebAssembly module failed to start ', 'WebAssembly 模块初始化失败 ') + reason(error), true);
                throw error;
            });
        };
    });

    if (window.fetch && window.ReadableStream && window.Response) {
        var fetch0 = window.fetch;
        window.fetch = function (input, init) {
            var url = typeof input === 'string' ? input : (input && input.url) || '';
            // Keyed by the absolute address, which is what a response's url reports later.
            try { url = new URL(url, document.baseURI).href; } catch (e) {}
            var promise = fetch0.apply(this, arguments);
            var name = fileName(url), t = Date.now(), key = 'wasm-' + url;
            if (!/\.wasm(\?|$)/.test(url)) {
                // Font subsets have their own counter; other requests show only when they fail.
                return promise.then(function (res) {
                    if (!res.ok) log('', tr('Request failed ', '请求失败 ') + res.status + ' ' + name, true);
                    return res;
                }, function (error) {
                    log('', tr('Request failed ', '请求失败 ') + name + ' ' + reason(error), true);
                    throw error;
                });
            }
            log(key, tr('Downloading ', '下载 ') + name + '…');
            return promise.then(function (res) {
                if (!res.ok || !res.body) {
                    if (!res.ok) log(key, tr('Download failed ', '下载失败 ') + res.status + ' ' + name, true);
                    return res;
                }
                var entry = files[url] = { loaded: 0, total: Number(res.headers.get('Content-Length')) || GUESS_BYTES, at: t };
                var encoded = res.headers.get('Content-Encoding');
                var reader = res.body.getReader();
                var stream = new ReadableStream({
                    pull: function (controller) {
                        return reader.read().then(function (r) {
                            if (r.done) {
                                entry.loaded = entry.total = Math.max(entry.loaded, encoded ? entry.loaded : entry.total);
                                finishDownload(url);
                                controller.close();
                                return;
                            }
                            entry.loaded += r.value.byteLength;
                            // A compressed body counts more bytes than its Content-Length; show only what arrived then.
                            if (!entry.done) log(key, tr('Downloading ', '下载 ') + name + ' ' + mb(entry.loaded) + (encoded ? '' : ' / ' + mb(entry.total)) + ' MB');
                            // A compressed response's length counts compressed bytes; grow the total instead of overflowing.
                            if (entry.loaded > entry.total) entry.total = entry.loaded;
                            render();
                            controller.enqueue(r.value);
                        }, function (error) {
                            log(key, tr('Download interrupted ', '下载中断 ') + name + ' ' + reason(error), true);
                            throw error;
                        });
                    },
                    cancel: function (why) { return reader.cancel(why); },
                });
                render();
                return new Response(stream, { status: res.status, statusText: res.statusText, headers: res.headers });
            }, function (error) {
                log(key, tr('Download failed ', '下载失败 ') + name + ' ' + reason(error), true);
                throw error;
            });
        };
    }

    // The app needs WebAssembly GC and WebGL 2 (Chrome / Edge 119+, Firefox 120+, Safari 18.2+). Say
    // why instead of leaving the bar stuck. The GC probe is the module the runtime builds first.
    var gc = false;
    try {
        gc = !!window.WebAssembly && WebAssembly.validate(new Uint8Array([0,97,115,109,1,0,0,0,1,11,2,94,119,1,96,2,100,0,127,1,127,3,2,1,1,7,11,1,7,97,49,54,95,103,101,116,0,0,10,11,1,9,0,32,0,32,1,251,13,0,11,0,19,4,110,97,109,101,4,12,1,0,9,97,114,114,97,121,95,105,49,54]));
    } catch (e) {}
    // Everything is drawn through WebGL 2 (Skia). Chrome turns it off when hardware acceleration is
    // disabled or after its GPU process crashes; without it start-up stalls.
    var gl = false;
    try {
        var probe = document.createElement('canvas').getContext('webgl2');
        gl = !!probe;
        var lose = probe && probe.getExtension('WEBGL_lose_context');
        if (lose) lose.loseContext();
    } catch (e) {}
    var problem = !gc
        ? [tr('This browser does not support WebAssembly GC', '此浏览器内核不支持 WebAssembly GC'),
            tr('Your browser is too old. Use Chrome / Edge 119+, Firefox 120+ or Safari 18.2+.', '浏览器版本过旧，请使用 Chrome / Edge 119+、Firefox 120+ 或 Safari 18.2+')]
        : !gl
            ? [tr('WebGL 2 is unavailable (hardware acceleration off or the GPU process crashed)', 'WebGL 2 不可用（硬件加速关闭或显卡进程异常）'),
                tr('Graphics acceleration is unavailable. Turn on "Use graphics acceleration" in the browser settings and restart it.', '浏览器图形加速不可用，请在浏览器设置里开启"使用图形加速"后重启浏览器')]
            : null;
    if (problem) {
        log('', problem[0], true);
        if (text) {
            text.textContent = problem[1];
            var retry = document.createElement('a');
            retry.href = '#';
            retry.className = 'loading-action';
            retry.textContent = tr('Retry', '重试');
            retry.onclick = function () { location.reload(); return false; };
            text.appendChild(document.createElement('br'));
            text.appendChild(retry);
        }
        return;
    }
    render();
    if (!APP_SCRIPT) return;
    var script = document.createElement('script');
    script.src = APP_SCRIPT;
    script.onload = function () { log('', tr('Script loaded, preparing the interface', '脚本已载入，正在准备界面')); };
    document.body.appendChild(script);
})();
