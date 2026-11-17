# LoadingPreferenceCategory

`LoadingPreferenceCategory` is a category title with a trailing loading spinner, mirroring ColorOS's `COUILoadingPreferenceCategory` (for example "Available devices" while a scan is running). While loading, the row shows the small 16dp COUI rotating spinner, optionally preceded by a loading text (12sp medium, at most two lines, 4dp before the spinner). When loading ends, `endContent` replaces the spinner; without it the widget disappears. The title uses the COUI category metrics: 12sp medium, 8dp above and below, 32dp from the start edge, and the widget ends 32dp from the end edge.

<div style="position: relative; height: 240px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=loadingPreferenceCategory" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.preference.LoadingPreferenceCategory
import io.github.suqi8.coui.kmp.preference.LoadingPreferenceCategoryDefaults
```

## Basic Usage

Place the category above the card it titles, in place of a `SmallTitle`:

```kotlin
var scanning by remember { mutableStateOf(true) }

LoadingPreferenceCategory(
    title = "Available devices",
    loading = scanning,
    loadingText = "Searching"
)
Card(modifier = Modifier.padding(horizontal = 16.dp)) {
    // device rows
}
```

## Loading States

| COUI LoadingType              | Parameters                                   |
| ----------------------------- | -------------------------------------------- |
| LOADING                       | `loading = true`                             |
| INVISIBLE                     | `loading = false`, `endContent = null`       |
| AFTER_LOADING / BEFORE_LOADING | `loading = false` with `endContent`         |

`PAUSE` (the spinner frozen on its current frame) is not available: the rotating spinner has no paused frame.

## Properties

### LoadingPreferenceCategory Properties

| Property Name    | Type                                | Description                                   | Default Value                                         | Required |
| ---------------- | ----------------------------------- | --------------------------------------------- | ----------------------------------------------------- | -------- |
| title            | String                              | Category title                                | -                                                     | Yes      |
| modifier         | Modifier                            | Component modifier                            | Modifier                                              | No       |
| loading          | Boolean                             | Whether the spinner is shown                  | true                                                  | No       |
| loadingText      | String?                             | Text before the spinner                       | null                                                  | No       |
| titleType        | PreferenceCategoryTitleType         | Title size (Small / Medium / Large), see [PreferenceCategory](./preferencecategory); next to Medium / Large the loading text is 14sp and the widget at least 28dp tall | PreferenceCategoryTitleType.Small | No |
| marginStartType  | PreferenceCategoryMarginStartType   | Horizontal margin type (Large 32dp / Small 16dp) | PreferenceCategoryMarginStartType.Large            | No       |
| titleColor       | Color                               | Title color                                   | LoadingPreferenceCategoryDefaults.titleColor(titleType) | No     |
| loadingTextColor | Color                               | Loading text color                            | LoadingPreferenceCategoryDefaults.loadingTextColor()  | No       |
| indicatorColor   | Color                               | Spinner color                                 | ProgressIndicatorDefaults.rotatingProgressIndicatorColor() | No  |
| insideMargin     | PaddingValues                       | Horizontal margin of the row                  | LoadingPreferenceCategoryDefaults.insideMargin(marginStartType) | No |
| endContent       | @Composable (RowScope.() -> Unit)?  | Content shown at the end when not loading     | null                                                  | No       |

### LoadingPreferenceCategoryDefaults Object

| Name               | Type          | Default Value                          | Description                                                     |
| ------------------ | ------------- | -------------------------------------- | --------------------------------------------------------------- |
| InsideMargin       | PaddingValues | PaddingValues(horizontal = 32.dp)      | Large margin type: title start margin / widget end inset        |
| InsideMarginSmall  | PaddingValues | PaddingValues(horizontal = 16.dp)      | Small margin type: title start margin / widget end inset        |
| insideMargin(marginStartType) | PaddingValues | InsideMargin / InsideMarginSmall | Margin for the given margin type                           |
| LoadingTextSpacing | Dp            | 4.dp                                   | Gap between the loading text and the spinner                    |
| titleColor(titleType) | Color      | labelSecondary (Small) / labelPrimary | COUI couiColorSecondNeutral (Small) / couiColorPrimaryNeutral (Medium, Large) |
| loadingTextColor() | Color         | COUITheme.colorScheme.labelPrimary        | COUI couiColorPrimaryNeutral                                    |

## Advanced Usage

### Action After Loading

```kotlin
LoadingPreferenceCategory(
    title = "Available devices",
    loading = scanning,
    endContent = {
        Text(
            text = "Refresh",
            modifier = Modifier.clickable { scanning = true },
            style = COUITheme.textStyles.bodyXS.copy(fontWeight = FontWeight.Medium),
            color = COUITheme.colorScheme.primary
        )
    }
)
```
