## COUI

**COUI** is a shared UI library for [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/).  
It provides a modern, elegant design system inspired by MIUI, adapted and extended for multiple platforms.

Now Supported: **Android** / **Desktop(JVM)** / **iOS** / **WasmJs** / **Js** / **macOS(Native)**.

> This library is experimental, and APIs may change at any time without prior notice.

[![License](https://img.shields.io/github/license/yourname/coui)](LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/top.yourname.coui.kmp/coui)](https://search.maven.org/search?q=g:top.yourname.coui.kmp)

---

### 🌐 Web Example

[JsCanvas Demo](https://suqi8.github.io/coui-jsCanvas/) / [WasmJs Demo](https://suqi8.github.io/coui-wasmJs/)

---

### ⚙️ Start

```Kotlin DSL
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.suqi8.coui.kmp:coui:<version>")
            // Other dependencies...
        }
        // Other sourceSets...
    }
    // Other configurations...
}
````

---

### 🧩 Usage

```kotlin
@Composable
fun App() {
    COUITheme(
        colors = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    ) {
        Scaffold(
            topBar = { /* TopBar */ },
            bottomBar = { /* BottomBar */ },
            floatingActionButton = { /* FloatingActionButton */ },
        ) {
            // Other Content...
        }
    }
}
```

---

### 🖼️ Screenshot

![screenshot](https://github.com/suqi8/coui/blob/main/assets/screenshot.webp?raw=true)

---

### 🙏 Acknowledgement

COUI is based on [**Miuix**](https://github.com/compose-miuix-ui/miuix),
a Compose Multiplatform UI library created by **Yukonga** and contributors.
This project modifies and extends Miuix for enhanced cross-platform visual experience and COUI design language.

```
Original project: https://github.com/compose-miuix-ui/miuix
License: Apache License 2.0
```

---

### 🪶 Notes

* COUI keeps compatibility with Miuix components and theming system.
* Some API names and design tokens are refactored to match COUI style.
* Future updates may introduce breaking changes before 1.0 release.

```
