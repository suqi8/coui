# 快速开始

当前支持的平台: **Android** / **Desktop(JVM)** / **iOS** / **WasmJs** / **Js** / **macOS(Native)**

::: warning 注意
此库处于实验阶段，API 可能会在未来版本中变更而不另行通知
:::

## 添加依赖

要在您的项目中使用 COUI，请按照以下步骤添加依赖：

### Gradle (Kotlin DSL)

1. 在根目录的 settings.gradle.kts 添加（正常情况应已包含）：

```kotlin
repositories {
    mavenCentral()
}
```

2. 检查 Maven Central 当前最新版本：
   [![Maven Central](https://img.shields.io/maven-central/v/io.github.suqi8.coui.kmp/coui-ui)](https://search.maven.org/search?q=g:io.github.suqi8.coui.kmp)

3. 在项目的 build.gradle.kts 中添加依赖：

COUI 由多个可独立使用的模块组成：

| 模块 | 说明 |
|---|---|
| `coui-ui` | 核心 UI 组件库（自动包含 `coui-core`） |
| `coui-preference` | Preference 组件（SwitchPreference、CheckboxPreference 等），依赖 `coui-ui` |
| `coui-icons` | 扩展图标库，可独立使用，也可与 `coui-ui` 同时使用（自动包含 `coui-core`） |
| `coui-blur` | 模糊效果库，可独立使用 |
| `coui-squircle` | 平滑圆角形状，可独立使用（已由 `coui-ui` 传递包含） |
| `coui-shader` | 底层运行时着色器 / 渲染效果抽象，已由 `coui-blur` / `coui-squircle` 传递包含 |
| `coui-nav` | 自包含导航库，可独立使用 |

- 在 Compose Multiplatform 项目目录的 build.gradle.kts 中：

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.suqi8.coui.kmp:coui-ui:<version>")
            // 可选：添加 coui-preference 以获取 Preference 组件
            implementation("io.github.suqi8.coui.kmp:coui-preference:<version>")
            // 可选：添加 coui-icons 以获取更多图标
            implementation("io.github.suqi8.coui.kmp:coui-icons:<version>")
            // 可选：添加 coui-blur 以获取模糊效果
            implementation("io.github.suqi8.coui.kmp:coui-blur:<version>")
            // 可选：添加 coui-squircle 以获取平滑圆角形状
            implementation("io.github.suqi8.coui.kmp:coui-squircle:<version>")
            // 可选：添加 coui-nav 以获取导航支持
            implementation("io.github.suqi8.coui.kmp:coui-nav:<version>")
        }
    }
}
```

- 在 Android Compose 项目目录的 build.gradle.kts 中：

```kotlin
dependencies {
    implementation("io.github.suqi8.coui.kmp:coui-ui-android:<version>")
    // 可选：添加 coui-preference 以获取 Preference 组件
    implementation("io.github.suqi8.coui.kmp:coui-preference-android:<version>")
    // 可选：添加 coui-icons 以获取更多图标
    implementation("io.github.suqi8.coui.kmp:coui-icons-android:<version>")
    // 可选：添加 coui-blur 以获取模糊效果（需要 minSdk 33）
    implementation("io.github.suqi8.coui.kmp:coui-blur-android:<version>")
    // 可选：添加 coui-squircle 以获取平滑圆角形状
    implementation("io.github.suqi8.coui.kmp:coui-squircle-android:<version>")
    // 可选：添加 coui-nav 以获取导航支持
    implementation("io.github.suqi8.coui.kmp:coui-nav-android:<version>")
}
```

- 在其他常规项目中使用，则只需要根据需要添加对应平台后缀的依赖即可：

```kotlin
implementation("io.github.suqi8.coui.kmp:coui-ui-iosarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-ui-iossimulatorarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-ui-macosarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-ui-desktop:<version>")
implementation("io.github.suqi8.coui.kmp:coui-ui-wasmjs:<version>")
implementation("io.github.suqi8.coui.kmp:coui-ui-js:<version>")
// 可选：添加 coui-preference
implementation("io.github.suqi8.coui.kmp:coui-preference-iosarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-preference-iossimulatorarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-preference-macosarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-preference-desktop:<version>")
implementation("io.github.suqi8.coui.kmp:coui-preference-wasmjs:<version>")
implementation("io.github.suqi8.coui.kmp:coui-preference-js:<version>")
// 可选：添加 coui-blur
implementation("io.github.suqi8.coui.kmp:coui-blur-iosarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-blur-iossimulatorarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-blur-macosarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-blur-desktop:<version>")
implementation("io.github.suqi8.coui.kmp:coui-blur-wasmjs:<version>")
implementation("io.github.suqi8.coui.kmp:coui-blur-js:<version>")
// 可选：添加 coui-nav
implementation("io.github.suqi8.coui.kmp:coui-nav-iosarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-nav-iossimulatorarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-nav-macosarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-nav-desktop:<version>")
implementation("io.github.suqi8.coui.kmp:coui-nav-wasmjs:<version>")
implementation("io.github.suqi8.coui.kmp:coui-nav-js:<version>")
// 可选：添加 coui-icons
implementation("io.github.suqi8.coui.kmp:coui-icons-iosarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-icons-iossimulatorarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-icons-macosarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-icons-desktop:<version>")
implementation("io.github.suqi8.coui.kmp:coui-icons-wasmjs:<version>")
implementation("io.github.suqi8.coui.kmp:coui-icons-js:<version>")
// 可选：添加 coui-squircle
implementation("io.github.suqi8.coui.kmp:coui-squircle-iosarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-squircle-iossimulatorarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-squircle-macosarm64:<version>")
implementation("io.github.suqi8.coui.kmp:coui-squircle-desktop:<version>")
implementation("io.github.suqi8.coui.kmp:coui-squircle-wasmjs:<version>")
implementation("io.github.suqi8.coui.kmp:coui-squircle-js:<version>")
```

## 基本用法

### 应用 COUI 主题

```kotlin
@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    // 可用模式: System, Light, Dark, MonetSystem, MonetLight, MonetDark
    val controller = remember { ThemeController(ColorSchemeMode.System) }
    return COUITheme(
        controller = controller,
        content = content
    )
}
```

### 使用 COUI 脚手架

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

::: warning 注意
Scaffold 组件为跨平台提供了一个合适的弹出窗口的容器。`OverlayDialog`、`OverlayDropdownPreference`、`OverlaySpinnerPreference`、
`OverlayListPopup` 等组件都基于此实现弹出窗口，因此都需要被该组件包裹。
:::

## API 文档

- 查看 [API 文档](/coui/dokka/index.html){target="_blank"}，此文档使用 Dokka 生成，包含了所有 API
  的详细信息。
