# AppInfoPreference

`AppInfoPreference` 是“关于”页顶部的应用信息头，对应 ColorOS 的 `COUIAppInfoPreference`。它是一个无背景的居中列：距顶部 42dp 的 60dp 应用图标（16dp 圆角），其下 8dp 为 20sp 中粗的应用名，再下 4dp 为 12sp 的版本号。

同时设置 `copyText` 与 `onCopyClick` 时，长按版本号会在其上方弹出小浮窗；点击浮窗会调用 `onCopyClick` 并关闭浮窗。COUI 在这一步复制版本号并弹出 Toast，请在 `onCopyClick` 中自行完成。

<div style="position: relative; height: 220px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=appInfoPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.preference.AppInfoPreference
import io.github.suqi8.coui.kmp.preference.AppInfoPreferenceDefaults
```

## 基本用法

```kotlin
AppInfoPreference(
    appName = "COUI Gallery",
    appVersion = "Version 17.0.0",
    icon = {
        Image(
            painter = painterResource(Res.drawable.app_icon),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
)
```

## 复制版本号

```kotlin
val clipboard = LocalClipboardManager.current

AppInfoPreference(
    appName = "COUI Gallery",
    appVersion = "Version 17.0.0",
    copyText = "Copy",
    onCopyClick = { clipboard.setText(AnnotatedString("17.0.0")) }
)
```

## 属性

### AppInfoPreference 属性

| 属性名           | 类型                      | 说明                               | 默认值                                              | 是否必须 |
| ---------------- | ------------------------- | ---------------------------------- | --------------------------------------------------- | -------- |
| appName          | String                    | 应用名                             | -                                                   | 是       |
| appVersion       | String                    | 版本号文字                         | -                                                   | 是       |
| modifier         | Modifier                  | 组件修饰符                         | Modifier                                            | 否       |
| icon             | @Composable (() -> Unit)? | 应用图标，自动定尺寸并裁剪圆角     | null                                                | 否       |
| iconCornerRadius | Dp                        | 图标裁剪圆角                       | AppInfoPreferenceDefaults.IconCornerRadius          | 否       |
| copyText         | String?                   | 复制浮窗文字；为空时不弹浮窗       | null                                                | 否       |
| onCopyClick      | (() -> Unit)?             | 复制浮窗回调；为空时不弹浮窗       | null                                                | 否       |
| colors           | AppInfoPreferenceColors   | 颜色配置                           | AppInfoPreferenceDefaults.appInfoPreferenceColors() | 否       |

### AppInfoPreferenceDefaults 对象

| 常量                       | 类型 | 默认值 | 说明                                     |
| -------------------------- | ---- | ------ | ---------------------------------------- |
| IconSize                   | Dp   | 60.dp  | about_app_icon 尺寸                      |
| IconCornerRadius           | Dp   | 16.dp  | about_app_icon couiBorderRadius          |
| IconTopMargin              | Dp   | 42.dp  | about_app_icon marginTop                 |
| NameTopMargin              | Dp   | 8.dp   | about_app_name marginTop                 |
| VersionTopMargin           | Dp   | 4.dp   | about_app_version marginTop              |
| BottomMargin               | Dp   | 8.dp   | 信息头下方留白                           |
| CopyPopupMinWidth          | Dp   | 76.dp  | coui_component_copy_window_width         |
| CopyPopupMinHeight         | Dp   | 48.dp  | coui_component_copy_window_height        |
| CopyPopupHorizontalPadding | Dp   | 24.dp  | coui_component_copy_text_padding_hor     |
| CopyPopupMarginBottom      | Dp   | 16.dp  | coui_component_copy_window_margin_bottom |
| CopyPopupCornerRadius      | Dp   | 12.dp  | 复制浮窗圆角                             |
| CopyPopupShadowElevation   | Dp   | 8.dp   | 复制浮窗阴影                             |

#### `appInfoPreferenceColors()` 工厂方法

| 参数                  | 类型  | 默认值                                    |
| --------------------- | ----- | ----------------------------------------- |
| nameColor             | Color | COUITheme.colorScheme.onSurface           |
| versionColor          | Color | COUITheme.colorScheme.onSurfaceSecondary  |
| copyPopupColor        | Color | COUITheme.colorScheme.backgroundTop       |
| copyPopupContentColor | Color | COUITheme.colorScheme.onSurface           |
