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
   [![Maven Central](https://img.shields.io/maven-central/v/com.suqi8.coui.kmp/coui-ui)](https://search.maven.org/search?q=g:com.suqi8.coui.kmp)

3. Add dependencies to your project's `build.gradle.kts`:

Miuix is composed of several modules that can be used independently:

| Module | Description |
|---|---|
| `coui-ui` | Core UI component library (automatically includes `coui-core`) |
| `coui-preference` | Preference components (SwitchPreference, CheckboxPreference, etc.), depends on `coui-ui` |
| `coui-icons` | Extended icon library, can be used independently or together with `coui-ui` (automatically includes `coui-core`) |
| `coui-blur` | Blur effect library, can be used independently |
| `coui-navigation3-ui` | Navigation3 UI library, can be used independently |

- For Compose Multiplatform projects:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("com.suqi8.coui.kmp:coui-ui:<version>")
            // Optional: Add coui-preference for preference components
            implementation("com.suqi8.coui.kmp:coui-preference:<version>")
            // Optional: Add coui-icons for more icons
            implementation("com.suqi8.coui.kmp:coui-icons:<version>")
            // Optional: Add coui-blur for blur effects
            implementation("com.suqi8.coui.kmp:coui-blur:<version>")
            // Optional: Add coui-navigation3-ui for Navigation3 support
            implementation("com.suqi8.coui.kmp:coui-navigation3-ui:<version>")
        }
    }
}
```

- For Android Compose projects:

```kotlin
dependencies {
    implementation("com.suqi8.coui.kmp:coui-ui-android:<version>")
    // Optional: Add coui-preference for preference components
    implementation("com.suqi8.coui.kmp:coui-preference-android:<version>")
    // Optional: Add coui-icons for more icons
    implementation("com.suqi8.coui.kmp:coui-icons-android:<version>")
    // Optional: Add coui-blur for blur effects (requires minSdk 33)
    implementation("com.suqi8.coui.kmp:coui-blur-android:<version>")
    // Optional: Add coui-navigation3-ui for Navigation3 support
    implementation("com.suqi8.coui.kmp:coui-navigation3-ui-android:<version>")
}
```

- For other projects, add platform-specific dependencies as needed:

```kotlin
implementation("com.suqi8.coui.kmp:coui-ui-iosarm64:<version>")
implementation("com.suqi8.coui.kmp:coui-ui-iossimulatorarm64:<version>")
implementation("com.suqi8.coui.kmp:coui-ui-macosarm64:<version>")
implementation("com.suqi8.coui.kmp:coui-ui-desktop:<version>")
implementation("com.suqi8.coui.kmp:coui-ui-wasmjs:<version>")
implementation("com.suqi8.coui.kmp:coui-ui-js:<version>")
// Optional: Add coui-preference
implementation("com.suqi8.coui.kmp:coui-preference-iosarm64:<version>")
implementation("com.suqi8.coui.kmp:coui-preference-iossimulatorarm64:<version>")
implementation("com.suqi8.coui.kmp:coui-preference-macosarm64:<version>")
implementation("com.suqi8.coui.kmp:coui-preference-desktop:<version>")
implementation("com.suqi8.coui.kmp:coui-preference-wasmjs:<version>")
implementation("com.suqi8.coui.kmp:coui-preference-js:<version>")
// Optional: Add coui-blur
implementation("com.suqi8.coui.kmp:coui-blur-iosarm64:<version>")
implementation("com.suqi8.coui.kmp:coui-blur-iossimulatorarm64:<version>")
implementation("com.suqi8.coui.kmp:coui-blur-macosarm64:<version>")
implementation("com.suqi8.coui.kmp:coui-blur-desktop:<version>")
implementation("com.suqi8.coui.kmp:coui-blur-wasmjs:<version>")
implementation("com.suqi8.coui.kmp:coui-blur-js:<version>")
// Optional: Add coui-navigation3-ui
implementation("com.suqi8.coui.kmp:coui-navigation3-ui-iosarm64:<version>")
implementation("com.suqi8.coui.kmp:coui-navigation3-ui-iossimulatorarm64:<version>")
implementation("com.suqi8.coui.kmp:coui-navigation3-ui-macosarm64:<version>")
implementation("com.suqi8.coui.kmp:coui-navigation3-ui-desktop:<version>")
implementation("com.suqi8.coui.kmp:coui-navigation3-ui-wasmjs:<version>")
implementation("com.suqi8.coui.kmp:coui-navigation3-ui-js:<version>")
// Optional: Add coui-icons
implementation("com.suqi8.coui.kmp:coui-icons-iosarm64:<version>")
implementation("com.suqi8.coui.kmp:coui-icons-iossimulatorarm64:<version>")
implementation("com.suqi8.coui.kmp:coui-icons-macosarm64:<version>")
implementation("com.suqi8.coui.kmp:coui-icons-desktop:<version>")
implementation("com.suqi8.coui.kmp:coui-icons-wasmjs:<version>")
implementation("com.suqi8.coui.kmp:coui-icons-js:<version>")
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

- View the [API Documentation](/miuix/dokka/index.html){target="_blank"},
  generated using Dokka, which contains detailed information about all APIs.
