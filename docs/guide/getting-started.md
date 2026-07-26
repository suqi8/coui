# Getting Started

Supported platforms: **Android** / **Desktop (JVM)** / **iOS** / **WasmJs** / **Js** / **macOS (Native)**

::: warning
This library is experimental, and APIs may change in future versions without notice.
:::

## Adding Dependencies

To use Miuix in your project, follow these steps to add dependencies:

### Gradle (Kotlin DSL)

1. Add the following to the root `settings.gradle.kts` file (usually already included):

```kotlin
repositories {
    mavenCentral()
}
```

2. Check the latest version on Maven Central:
   [![Maven Central](https://img.shields.io/maven-central/v/com.suqi8.coui.kmp/miuix-ui)](https://search.maven.org/search?q=g:com.suqi8.coui.kmp)

3. Add dependencies to your project's `build.gradle.kts`:

Miuix is composed of several modules that can be used independently:

| Module             | Description                                          |
| ------------------ | ---------------------------------------------------- |
| `miuix-ui`         | Core UI component library                            |
| `miuix-preference` | Preference components library, depends on `miuix-ui` |
| `miuix-icons`      | Extended icon library, can be used independently     |
| `miuix-blur`       | Blur effect library, can be used independently       |
| `miuix-squircle`   | Squircle shapes library, can be used independently   |
| `coui-nav`        | Navigation library, can be used independently        |
| `miuix-shader`     | Low-level runtime shader / render effect abstraction |

- For Compose Multiplatform projects:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.suqi8.coui.kmp:miuix-ui:<version>")
            // Optional: Add miuix-preference for preference components
            implementation("com.suqi8.coui.kmp:miuix-preference:<version>")
            // Optional: Add miuix-icons for more icons
            implementation("com.suqi8.coui.kmp:miuix-icons:<version>")
            // Optional: Add miuix-blur for blur effects
            implementation("com.suqi8.coui.kmp:miuix-blur:<version>")
            // Optional: Add miuix-squircle for squircle (smooth rounded corner) shapes
            implementation("com.suqi8.coui.kmp:miuix-squircle:<version>")
            // Optional: Add coui-nav for navigation
            implementation("com.suqi8.coui.kmp:coui-nav:<version>")
        }
    }
}
```

- For Android Compose projects:

```kotlin
dependencies {
    implementation("com.suqi8.coui.kmp:miuix-ui-android:<version>")
    // Optional: Add miuix-preference for preference components
    implementation("com.suqi8.coui.kmp:miuix-preference-android:<version>")
    // Optional: Add miuix-icons for more icons
    implementation("com.suqi8.coui.kmp:miuix-icons-android:<version>")
    // Optional: Add miuix-blur for blur effects (requires minSdk 33)
    implementation("com.suqi8.coui.kmp:miuix-blur-android:<version>")
    // Optional: Add miuix-squircle for squircle (smooth rounded corner) shapes
    implementation("com.suqi8.coui.kmp:miuix-squircle-android:<version>")
    // Optional: Add coui-nav for navigation
    implementation("com.suqi8.coui.kmp:coui-nav-android:<version>")
}
```

- For other projects, add platform-specific dependencies as needed:

```kotlin
implementation("com.suqi8.coui.kmp:miuix-ui-iosarm64:<version>")
implementation("com.suqi8.coui.kmp:miuix-ui-iossimulatorarm64:<version>")
implementation("com.suqi8.coui.kmp:miuix-ui-macosarm64:<version>")
implementation("com.suqi8.coui.kmp:miuix-ui-desktop:<version>")
implementation("com.suqi8.coui.kmp:miuix-ui-wasmjs:<version>")
implementation("com.suqi8.coui.kmp:miuix-ui-js:<version>")
// Optional: Add miuix-preference
implementation("com.suqi8.coui.kmp:miuix-preference-iosarm64:<version>")
implementation("com.suqi8.coui.kmp:miuix-preference-iossimulatorarm64:<version>")
implementation("com.suqi8.coui.kmp:miuix-preference-macosarm64:<version>")
implementation("com.suqi8.coui.kmp:miuix-preference-desktop:<version>")
implementation("com.suqi8.coui.kmp:miuix-preference-wasmjs:<version>")
implementation("com.suqi8.coui.kmp:miuix-preference-js:<version>")
// Optional: Add miuix-blur
implementation("com.suqi8.coui.kmp:miuix-blur-iosarm64:<version>")
implementation("com.suqi8.coui.kmp:miuix-blur-iossimulatorarm64:<version>")
implementation("com.suqi8.coui.kmp:miuix-blur-macosarm64:<version>")
implementation("com.suqi8.coui.kmp:miuix-blur-desktop:<version>")
implementation("com.suqi8.coui.kmp:miuix-blur-wasmjs:<version>")
implementation("com.suqi8.coui.kmp:miuix-blur-js:<version>")
// Optional: Add coui-nav
implementation("com.suqi8.coui.kmp:coui-nav-iosarm64:<version>")
implementation("com.suqi8.coui.kmp:coui-nav-iossimulatorarm64:<version>")
implementation("com.suqi8.coui.kmp:coui-nav-macosarm64:<version>")
implementation("com.suqi8.coui.kmp:coui-nav-desktop:<version>")
implementation("com.suqi8.coui.kmp:coui-nav-wasmjs:<version>")
implementation("com.suqi8.coui.kmp:coui-nav-js:<version>")
// Optional: Add miuix-icons
implementation("com.suqi8.coui.kmp:miuix-icons-iosarm64:<version>")
implementation("com.suqi8.coui.kmp:miuix-icons-iossimulatorarm64:<version>")
implementation("com.suqi8.coui.kmp:miuix-icons-macosarm64:<version>")
implementation("com.suqi8.coui.kmp:miuix-icons-desktop:<version>")
implementation("com.suqi8.coui.kmp:miuix-icons-wasmjs:<version>")
implementation("com.suqi8.coui.kmp:miuix-icons-js:<version>")
// Optional: Add miuix-squircle
implementation("com.suqi8.coui.kmp:miuix-squircle-iosarm64:<version>")
implementation("com.suqi8.coui.kmp:miuix-squircle-iossimulatorarm64:<version>")
implementation("com.suqi8.coui.kmp:miuix-squircle-macosarm64:<version>")
implementation("com.suqi8.coui.kmp:miuix-squircle-desktop:<version>")
implementation("com.suqi8.coui.kmp:miuix-squircle-wasmjs:<version>")
implementation("com.suqi8.coui.kmp:miuix-squircle-js:<version>")
```

## Basic Usage

### Applying the Miuix Theme

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

### Using the Miuix Scaffold

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

- View the [API Documentation](/miuix/dokka/index.html){target="\_blank"},
  generated using Dokka, which contains detailed information about all APIs.
