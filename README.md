## COUI

A ColorOS-styled UI library for Compose Multiplatform, based on [Miuix](https://github.com/compose-miuix-ui/miuix).

> Components are calibrated against ColorOS 16 (COUI) design: colors, metrics, motion curves and press feedback are transcribed from the real system implementation.
>
> This library is experimental. APIs may change without notice.

[![Kotlin](https://img.shields.io/badge/kotlin-2.4.10-7F52FF)](https://kotlinlang.org/)
[![Compose Multiplatform](https://img.shields.io/badge/compose-1.11.1-4285F4)](https://kotlinlang.org/compose-multiplatform/)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.suqi8.coui.kmp/coui-ui)](https://central.sonatype.com/search?q=g:io.github.suqi8.coui.kmp)
[![License](https://img.shields.io/github/license/suqi8/coui)](LICENSE)

### Supported Platforms

![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![iOS](https://img.shields.io/badge/iOS-Native-white?logo=apple)
![macOS](https://img.shields.io/badge/macOS-Native-white?logo=apple)
![Desktop](https://img.shields.io/badge/Desktop-JVM-007396?logo=openjdk)
![JsCanvas](https://img.shields.io/badge/Web-JsCanvas-F7DF1E?logo=javascript&logoColor=white)
![WasmJs](https://img.shields.io/badge/Web-WasmJs-654FF0?logo=webassembly&logoColor=white)

### Documentation & Demo

[![Docs](https://img.shields.io/badge/Docs-VitePress-3EAF7C?logo=vitepress&logoColor=white)](https://suqi8.github.io/coui/)
[![WasmJs Demo](https://img.shields.io/badge/Demo-WasmJs-654FF0?logo=webassembly&logoColor=white)](https://suqi8.github.io/coui/example/)
[![Other](https://img.shields.io/badge/Demo-Other-white?logo=githubactions&logoColor=white)](https://github.com/suqi8/coui/actions/workflows/example.yml)

### Modules

| Module                 | Description                                                                                             |
| ---------------------- | ------------------------------------------------------------------------------------------------------- |
| `coui-ui`              | Core UI component library (automatically includes `coui-core`)                                          |
| `coui-preference`      | Preference components (SwitchPreference, CheckboxPreference, etc.), depends on `coui-ui`                |
| `coui-icons`           | Extended icon library, can be used independently or with `coui-ui` (automatically includes `coui-core`) |
| `coui-blur`            | Blur effect library, can be used independently                                                          |
| `coui-navigation3-ui`  | Navigation3 UI library, can be used independently                                                       |

### Getting Started

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.suqi8.coui.kmp:coui-ui:1.1.0")
            // Optional: Add coui-preference for preference components
            implementation("io.github.suqi8.coui.kmp:coui-preference:1.1.0")
            // Optional: Add coui-icons for more icons
            implementation("io.github.suqi8.coui.kmp:coui-icons:1.1.0")
            // Optional: Add coui-blur for blur effects
            implementation("io.github.suqi8.coui.kmp:coui-blur:1.1.0")
            // Optional: Add coui-navigation3-ui for navigation3 support
            implementation("io.github.suqi8.coui.kmp:coui-navigation3-ui:1.1.0")
            // Other dependencies...
        }
        // Other sourceSets...
    }
    // Other configurations...
}
```

### Usage

- Provide a color scheme via `COUITheme(colors = ...)`, e.g., `lightColorScheme()` or `darkColorScheme()`.

```kotlin
@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val colors = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    return COUITheme(
        colors = colors,
        content = content
    )
}
```

- Use `ThemeController` to manage modes and enable Monet dynamic colors. Pass `keyColor` to set a custom seed color.

```kotlin
@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val controller = remember {
        ThemeController(
            ColorSchemeMode.MonetSystem,
            keyColor = Color(0xFF3482FF)
        )
    }
    return COUITheme(
        controller = controller,
        content = content
    )
}
```

### Credits

COUI is a fork of [Miuix](https://github.com/compose-miuix-ui/miuix) by YuKongA and the
compose-miuix-ui contributors, rethemed to the ColorOS 16 design language. Both projects are
licensed under Apache-2.0.
