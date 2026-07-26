# Getting Started

Supported platforms: **Android** / **Desktop (JVM)** / **iOS** / **WasmJs** / **Js** / **macOS (Native)**

::: warning
This library is experimental, and APIs may change in future versions without notice.
:::

## Adding Dependencies

To use COUI in your project, follow these steps to add dependencies:

### Gradle (Kotlin DSL)

1. Add the following to the root `settings.gradle.kts` file (usually already included):

```kotlin
repositories {
    mavenCentral()
}
```

2. Check the latest version on Maven Central:
   [![Maven Central](https://img.shields.io/maven-central/v/io.github.suqi8.coui.kmp/coui-ui)](https://search.maven.org/search?q=g:io.github.suqi8.coui.kmp)

3. Add dependencies to your project's `build.gradle.kts`:

COUI is composed of several modules that can be used independently:

| Module             | Description                                          |
| ------------------ | ---------------------------------------------------- |
| `coui-ui`         | Core UI component library                            |
| `coui-preference` | Preference components library, depends on `coui-ui` |
| `coui-icons`      | Extended icon library, can be used independently     |
| `coui-blur`       | Blur effect library, can be used independently       |
| `coui-squircle`   | Squircle shapes library, can be used independently   |
| `coui-nav`        | Navigation library, can be used independently        |
| `coui-shader`     | Low-level runtime shader / render effect abstraction |

- For Compose Multiplatform projects:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.suqi8.coui.kmp:coui-ui:<version>")
            // Optional: Add coui-preference for preference components
            implementation("io.github.suqi8.coui.kmp:coui-preference:<version>")
            // Optional: Add coui-icons for more icons
            implementation("io.github.suqi8.coui.kmp:coui-icons:<version>")
            // Optional: Add coui-blur for blur effects
            implementation("io.github.suqi8.coui.kmp:coui-blur:<version>")
            // Optional: Add coui-squircle for squircle (smooth rounded corner) shapes
            implementation("io.github.suqi8.coui.kmp:coui-squircle:<version>")
            // Optional: Add coui-nav for navigation
            implementation("io.github.suqi8.coui.kmp:coui-nav:<version>")
        }
    }
}
```

- For Android Compose projects:

```kotlin
dependencies {
    implementation("io.github.suqi8.coui.kmp:coui-ui-android:<version>")
    // Optional: Add coui-preference for preference components
    implementation("io.github.suqi8.coui.kmp:coui-preference-android:<version>")
    // Optional: Add coui-icons for more icons
    implementation("io.github.suqi8.coui.kmp:coui-icons-android:<version>")
    // Optional: Add coui-blur for blur effects (requires minSdk 33)
    implementation("io.github.suqi8.coui.kmp:coui-blur-android:<version>")
    // Optional: Add coui-squircle for squircle (smooth rounded corner) shapes
    implementation("io.github.suqi8.coui.kmp:coui-squircle-android:<version>")
    // Optional: Add coui-nav for navigation
    implementation("io.github.suqi8.coui.kmp:coui-nav-android:<version>")
}
```

- For other projects, add platform-specific dependencies as needed:

```kotlin
implementation("io.github.suqi8.coui.kmp:coui-ui-iosarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-ui-iossimulatorarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-ui-macosarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-ui-desktop:<version>")
implementation("io.github.suqi8.coui.kmp:coui-ui-wasmjs:<version>")
implementation("io.github.suqi8.coui.kmp:coui-ui-js:<version>")
// Optional: Add coui-preference
implementation("io.github.suqi8.coui.kmp:coui-preference-iosarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-preference-iossimulatorarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-preference-macosarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-preference-desktop:<version>")
implementation("io.github.suqi8.coui.kmp:coui-preference-wasmjs:<version>")
implementation("io.github.suqi8.coui.kmp:coui-preference-js:<version>")
// Optional: Add coui-blur
implementation("io.github.suqi8.coui.kmp:coui-blur-iosarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-blur-iossimulatorarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-blur-macosarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-blur-desktop:<version>")
implementation("io.github.suqi8.coui.kmp:coui-blur-wasmjs:<version>")
implementation("io.github.suqi8.coui.kmp:coui-blur-js:<version>")
// Optional: Add coui-nav
implementation("io.github.suqi8.coui.kmp:coui-nav-iosarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-nav-iossimulatorarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-nav-macosarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-nav-desktop:<version>")
implementation("io.github.suqi8.coui.kmp:coui-nav-wasmjs:<version>")
implementation("io.github.suqi8.coui.kmp:coui-nav-js:<version>")
// Optional: Add coui-icons
implementation("io.github.suqi8.coui.kmp:coui-icons-iosarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-icons-iossimulatorarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-icons-macosarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-icons-desktop:<version>")
implementation("io.github.suqi8.coui.kmp:coui-icons-wasmjs:<version>")
implementation("io.github.suqi8.coui.kmp:coui-icons-js:<version>")
// Optional: Add coui-squircle
implementation("io.github.suqi8.coui.kmp:coui-squircle-iosarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-squircle-iossimulatorarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-squircle-macosarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-squircle-desktop:<version>")
implementation("io.github.suqi8.coui.kmp:coui-squircle-wasmjs:<version>")
implementation("io.github.suqi8.coui.kmp:coui-squircle-js:<version>")
```

## Basic Usage

### Applying the COUI Theme

```kotlin
@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    // Available modes: System, Light, Dark, MonetSystem, MonetLight, MonetDark
    val controller = remember { ThemeController(ColorSchemeMode.System) }
    return COUITheme(
        controller = controller,
        content = content
    )
}
```

### Using the COUI Scaffold

```kotlin
Scaffold(
    topBar = {
        // TopBar
    },
    bottomBar = {
        // BottomBar
    },
    floatingActionButton = {
        // FloatingActionButton
    },
    floatingToolbar = {
        // FloatingToolbar
    }
) {
    // Content...
}
```

::: warning
The Scaffold component provides a suitable container for cross-platform popup windows.
Components such as `OverlayDialog`, `OverlayDropdownPreference`, `OverlaySpinnerPreference`, and `OverlayListPopup` are
all implemented based on this and therefore need to be wrapped by this component.
:::

## API Documentation

- View the [API Documentation](/coui/dokka/index.html){target="\_blank"},
  generated using Dokka, which contains detailed information about all APIs.
