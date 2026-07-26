# RecommendedPreference

`RecommendedPreference` is a standalone rounded card that recommends related settings, mirroring ColorOS's `COUIRecommendedPreference` (the "You might be looking for:" card at the bottom of ColorOS settings pages). The card is a 12dp rounded container (COUI `couiRoundCornerM`) filled with `couiColorContainer4`, holding a small secondary header followed by one tappable text row per item. Rows draw the standard COUI press overlay while pressed.

Nothing is rendered when `items` is empty, matching `COUIRecommendedPreference.setData()` hiding the preference for an empty list.

## Import

```kotlin
import io.github.suqi8.coui.kmp.preference.RecommendedPreference
import io.github.suqi8.coui.kmp.preference.RecommendedItem
import io.github.suqi8.coui.kmp.preference.RecommendedPreferenceDefaults
```

## Basic Usage

Build the item list inside a `remember` block to keep it stable across recompositions. Place the card like any other card block, e.g. with 16dp horizontal outer margins:

```kotlin
val items = remember {
    listOf(
        RecommendedItem(text = "Display & brightness", onClick = { /* navigate */ }),
        RecommendedItem(text = "Wallpapers & style", onClick = { /* navigate */ }),
        RecommendedItem(text = "Battery", onClick = { /* navigate */ })
    )
}

RecommendedPreference(
    title = "You might be looking for:",
    items = items,
    modifier = Modifier.padding(horizontal = 16.dp)
)
```

## Properties

### RecommendedPreference Properties

| Property Name | Type                         | Description                                    | Default Value                                              | Required |
| ------------- | ---------------------------- | ---------------------------------------------- | ---------------------------------------------------------- | -------- |
| title         | String                       | Header text of the card                        | -                                                          | Yes      |
| items         | List\<RecommendedItem\>      | Recommended entries; empty renders nothing     | -                                                          | Yes      |
| modifier      | Modifier                     | Component modifier                             | Modifier                                                   | No       |
| cornerRadius  | Dp                           | Corner radius of the card                      | RecommendedPreferenceDefaults.CornerRadius                 | No       |
| colors        | RecommendedPreferenceColors  | Card color configuration                       | RecommendedPreferenceDefaults.recommendedPreferenceColors() | No      |
| insideMargin  | PaddingValues                | Horizontal margin between card edge and texts  | RecommendedPreferenceDefaults.InsideMargin                 | No       |

### RecommendedItem Properties

| Property Name | Type          | Description                                            | Default Value | Required |
| ------------- | ------------- | ------------------------------------------------------ | ------------- | -------- |
| text          | String        | Row text                                               | -             | Yes      |
| onClick       | (() -> Unit)? | Callback when the row is clicked; null renders an inert row | null     | No       |

### RecommendedPreferenceDefaults Object

| Constant            | Type          | Default Value                       | Description                                                    |
| ------------------- | ------------- | ----------------------------------- | -------------------------------------------------------------- |
| CornerRadius        | Dp            | 12.dp                               | COUI couiRoundCornerM                                          |
| InsideMargin        | PaddingValues | PaddingValues(horizontal = 16.dp)   | In-card text inset (COUI 32dp minus the 16dp card margin)      |
| HeaderTopPadding    | Dp            | 16.dp                               | COUI recommended_recyclerView_padding_top                      |
| HeaderBottomSpacing | Dp            | 8.dp                                | COUI recommended_preference_list_item_head_bottom_margin       |
| HeaderMinHeight     | Dp            | 20.dp                               | COUI recommended_preference_list_item_height_head              |
| ItemVerticalPadding | Dp            | 10.dp                               | COUI recommended_preference_list_item_common_margin_vertical   |
| BottomPadding       | Dp            | 8.dp                                | COUI recommended_recyclerView_padding_bottom                   |

#### `recommendedPreferenceColors()` factory

| Parameter      | Type  | Default                                                                     |
| -------------- | ----- | --------------------------------------------------------------------------- |
| containerColor | Color | couiColorContainer4 (#0A000000 light / #14FFFFFF dark, theme-aware)         |
| titleColor     | Color | COUITheme.colorScheme.onSurfaceSecondary                                   |
| itemColor      | Color | COUITheme.colorScheme.onSurface                                            |

## Advanced Usage

### Custom Colors

```kotlin
RecommendedPreference(
    title = "You might be looking for:",
    items = items,
    colors = RecommendedPreferenceDefaults.recommendedPreferenceColors(
        containerColor = COUITheme.colorScheme.surfaceContainer,
        itemColor = COUITheme.colorScheme.primary
    )
)
```

### At the Bottom of a Settings Page

```kotlin
LazyColumn {
    // ... other preference cards ...
    item {
        RecommendedPreference(
            title = "You might be looking for:",
            items = items,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        )
    }
}
```
