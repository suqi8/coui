# AppInfoPreference

`AppInfoPreference` is the application header of an "About" page, mirroring ColorOS's `COUIAppInfoPreference`. It is a centered column without a background: a 60dp app icon with 16dp corners 42dp from the top, the 20sp medium app name 8dp below it, and the 12sp version 4dp below that.

When `copyText` and `onCopyClick` are both set, a long press on the version opens a small popup above it; tapping the popup calls `onCopyClick` and closes it. COUI copies the version and shows a toast at that point; do both in `onCopyClick`.

<div style="position: relative; height: 220px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=appInfoPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.preference.AppInfoPreference
import io.github.suqi8.coui.kmp.preference.AppInfoPreferenceDefaults
```

## Basic Usage

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

## Copy the Version

```kotlin
val clipboard = LocalClipboardManager.current

AppInfoPreference(
    appName = "COUI Gallery",
    appVersion = "Version 17.0.0",
    copyText = "Copy",
    onCopyClick = { clipboard.setText(AnnotatedString("17.0.0")) }
)
```

## Properties

### AppInfoPreference Properties

| Property Name    | Type                      | Description                                   | Default Value                                       | Required |
| ---------------- | ------------------------- | --------------------------------------------- | --------------------------------------------------- | -------- |
| appName          | String                    | Application name                              | -                                                   | Yes      |
| appVersion       | String                    | Version text                                  | -                                                   | Yes      |
| modifier         | Modifier                  | Component modifier                            | Modifier                                            | No       |
| icon             | @Composable (() -> Unit)? | Application icon, sized and clipped           | null                                                | No       |
| iconCornerRadius | Dp                        | Icon clip radius                              | AppInfoPreferenceDefaults.IconCornerRadius          | No       |
| copyText         | String?                   | Copy popup label; no popup without it         | null                                                | No       |
| onCopyClick      | (() -> Unit)?             | Copy popup callback; no popup without it      | null                                                | No       |
| colors           | AppInfoPreferenceColors   | Header colors                                 | AppInfoPreferenceDefaults.appInfoPreferenceColors() | No       |

### AppInfoPreferenceDefaults Object

| Constant                   | Type | Default Value | Description                                    |
| -------------------------- | ---- | ------------- | ---------------------------------------------- |
| IconSize                   | Dp   | 60.dp         | about_app_icon size                            |
| IconCornerRadius           | Dp   | 16.dp         | about_app_icon couiBorderRadius                |
| IconTopMargin              | Dp   | 42.dp         | about_app_icon marginTop                       |
| NameTopMargin              | Dp   | 8.dp          | about_app_name marginTop                       |
| VersionTopMargin           | Dp   | 4.dp          | about_app_version marginTop                    |
| BottomMargin               | Dp   | 8.dp          | Space below the header                         |
| CopyPopupMinWidth          | Dp   | 76.dp         | coui_component_copy_window_width               |
| CopyPopupMinHeight         | Dp   | 48.dp         | coui_component_copy_window_height              |
| CopyPopupHorizontalPadding | Dp   | 24.dp         | coui_component_copy_text_padding_hor           |
| CopyPopupMarginBottom      | Dp   | 16.dp         | coui_component_copy_window_margin_bottom       |
| CopyPopupCornerRadius      | Dp   | 12.dp         | Copy popup corner radius                       |
| CopyPopupShadowElevation   | Dp   | 8.dp          | Copy popup shadow                              |

#### `appInfoPreferenceColors()` factory

| Parameter             | Type  | Default                                   |
| --------------------- | ----- | ----------------------------------------- |
| nameColor             | Color | COUITheme.colorScheme.onSurface           |
| versionColor          | Color | COUITheme.colorScheme.onSurfaceSecondary  |
| copyPopupColor        | Color | COUITheme.colorScheme.backgroundTop       |
| copyPopupContentColor | Color | COUITheme.colorScheme.onSurface           |
