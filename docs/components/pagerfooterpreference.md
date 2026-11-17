# PagerFooterPreference

`PagerFooterPreference` is the description shown at the bottom of a settings page, below the last card, mirroring COUI's `COUIPagerFooterPreference` (layout `coui_pager_footer_preference`). The description may hold clickable links in the COUI link style, which is what COUI's `couiEnalbeClickSpan` turns on.

<div style="position: relative; height: 200px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=pagerFooterPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.appendLink
import io.github.suqi8.coui.kmp.preference.PagerFooterPreference
```

## Basic Usage

```kotlin
PagerFooterPreference(
    summary = "Updates download over WLAN only."
)
```

### With a Link

```kotlin
PagerFooterPreference(
    summary = buildAnnotatedString {
        append("Updates download over WLAN only. ")
        appendLink("Data usage", onClick = { /* open the data usage page */ })
    }
)
```

### Without the Extra Bottom Margin

By default the footer keeps a 24dp bottom margin (`support_preference_footer_preference_margin_bottom`). Turn it off when something follows the footer:

```kotlin
PagerFooterPreference(
    summary = "Updates download over WLAN only.",
    withExtraMarginBottom = false
)
```

## Properties

### PagerFooterPreference Properties

| Property Name         | Type                     | Description                                         | Default Value                              | Required |
| --------------------- | ------------------------ | --------------------------------------------------- | ------------------------------------------ | -------- |
| summary               | AnnotatedString / String | Description text (the AnnotatedString overload may hold links) | -                               | Yes      |
| modifier              | Modifier                 | Component modifier                                  | Modifier                                   | No       |
| title                 | String?                  | Optional title                                      | null                                       | No       |
| withExtraMarginBottom | Boolean                  | Whether the 24dp bottom margin is kept              | true                                       | No       |
| titleColor            | Color                    | Title color                                         | PagerFooterPreferenceDefaults.TitleColor   | No       |
| summaryColor          | Color                    | Non-link description color                          | PagerFooterPreferenceDefaults.SummaryColor | No       |
| linkColors            | LinkTextColors           | Link colors (AnnotatedString overload only)         | LinkTextDefaults.linkTextColors()          | No       |
| insideMargin          | PaddingValues            | Padding around the text                             | PagerFooterPreferenceDefaults.InsideMargin | No       |

### PagerFooterPreferenceDefaults Object

| Property Name | Type          | Description                                    | Value                                    |
| ------------- | ------------- | ---------------------------------------------- | ---------------------------------------- |
| MarginBottom  | Dp            | Space below the footer when `withExtraMarginBottom` | 24.dp                               |
| InsideMargin  | PaddingValues | Padding around the text                        | horizontal = 32.dp, vertical = 8.dp      |
| TitleColor    | Color         | Title color                                    | COUITheme.colorScheme.labelPrimary       |
| SummaryColor  | Color         | Description color                              | COUITheme.colorScheme.labelSecondary |
