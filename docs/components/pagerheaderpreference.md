# PagerHeaderPreference

`PagerHeaderPreference` is the description shown at the top of a settings page, above the first card, mirroring COUI's `COUIPagerHeaderPreference` (layout `coui_pager_header_preference`). It shows an optional title above a 12sp secondary-color description on the page background.

<div style="position: relative; height: 220px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=pagerHeaderPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.preference.PagerHeaderPreference
```

## Basic Usage

```kotlin
LazyColumn {
    item {
        PagerHeaderPreference(
            summary = "Limits background activity and visual effects to extend battery life."
        )
    }
    item {
        Card(modifier = Modifier.padding(horizontal = 16.dp)) {
            SwitchPreference(checked = on, onCheckedChange = { on = it }, title = "Battery saver")
        }
    }
}
```

The default 32dp horizontal padding lines the text up with the text inside 16dp-inset cards. The header keeps a 12dp top margin (`support_preference_header_preference_margin_top`).

### With a Title

```kotlin
PagerHeaderPreference(
    title = "Battery saver",
    summary = "Limits background activity and visual effects to extend battery life."
)
```

## Properties

### PagerHeaderPreference Properties

| Property Name | Type          | Description                    | Default Value                              | Required |
| ------------- | ------------- | ------------------------------ | ------------------------------------------ | -------- |
| summary       | String        | Description text               | -                                          | Yes      |
| modifier      | Modifier      | Component modifier             | Modifier                                   | No       |
| title         | String?       | Optional title                 | null                                       | No       |
| titleColor    | Color         | Title color                    | PagerHeaderPreferenceDefaults.TitleColor   | No       |
| summaryColor  | Color         | Description color              | PagerHeaderPreferenceDefaults.SummaryColor | No       |
| insideMargin  | PaddingValues | Padding around the text        | PagerHeaderPreferenceDefaults.InsideMargin | No       |

### PagerHeaderPreferenceDefaults Object

| Property Name | Type          | Description                                            | Value                                   |
| ------------- | ------------- | ------------------------------------------------------ | --------------------------------------- |
| MarginTop     | Dp            | Space above the header                                 | 12.dp                                   |
| InsideMargin  | PaddingValues | Padding around the text                                | horizontal = 32.dp, vertical = 8.dp     |
| TitleColor    | Color         | Title color                                            | COUITheme.colorScheme.labelPrimary      |
| SummaryColor  | Color         | Description color (`coui_preference_secondary_text_color`) | COUITheme.colorScheme.labelSecondary |
