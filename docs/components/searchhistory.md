# SearchHistory

`SearchHistory` reproduces ColorOS's search history panel (`COUISearchHistoryView` + `COUIFlowLayout`): a title bar with a clear button, and the history records as capsule chips flowing into lines that can be folded behind an expand button.

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=searchHistory" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.SearchHistory
import io.github.suqi8.coui.kmp.basic.SearchHistoryDefaults
```

## Basic Usage

```kotlin
val history = remember { mutableStateListOf("Wallpaper", "Dark mode", "Bluetooth") }

SearchHistory(
    title = "Search history",
    items = history,
    onItemClick = { query -> search(query) },
    onClear = { history.clear() },
    maxLinesFolded = 2,
    clearContentDescription = "Clear search history",
)
```

The panel draws nothing while `items` is empty.

## Folding

With `maxLinesFolded`, records past that many lines are folded: a round expand button takes the end of the last folded line (replacing its last record if it does not fit). Expanding shows up to `maxLinesExpanded` lines, and a fold button closes the last line. `expandable = false` keeps the panel at `maxLinesFolded` lines without a button.

## Properties

### SearchHistory

| Property                | Type                | Description                                    | Default Value                               | Required |
| ----------------------- | ------------------- | ---------------------------------------------- | ------------------------------------------- | -------- |
| title                   | String              | Panel title                                    | -                                           | Yes      |
| items                   | List\<String\>      | History records in display order               | -                                           | Yes      |
| onItemClick             | (String) -> Unit    | Called with the clicked record                 | -                                           | Yes      |
| onClear                 | () -> Unit          | Called as the clear animation starts           | -                                           | Yes      |
| modifier                | Modifier            | Modifier applied to the panel                  | Modifier                                    | No       |
| expandable              | Boolean             | Whether folded records can be expanded         | true                                        | No       |
| maxLinesFolded          | Int                 | Lines shown while folded                       | SearchHistoryDefaults.MaxLinesFolded        | No       |
| maxLinesExpanded        | Int                 | Lines shown while expanded                     | SearchHistoryDefaults.MaxLinesExpanded      | No       |
| clearContentDescription | String?             | Accessibility label of the clear button        | null                                        | No       |
| colors                  | SearchHistoryColors | Panel colors                                   | SearchHistoryDefaults.searchHistoryColors() | No       |
| insideMargin            | PaddingValues       | Padding around the panel content               | SearchHistoryDefaults.insideMargin()        | No       |

### SearchHistoryDefaults

| Constant              | Type          | Default Value                                 | COUI Source                                          |
| --------------------- | ------------- | --------------------------------------------- | ---------------------------------------------------- |
| MaxLinesFolded        | Int           | Int.MAX_VALUE                                 | maxRowFolded default                                 |
| MaxLinesExpanded      | Int           | 8                                             | DEFAULT_MAX_ROW_UNFOLDED                             |
| HorizontalPadding     | Dp            | 16.dp                                         | coui_component_search_history_padding_start / _end   |
| HorizontalPaddingWide | Dp            | 20.dp                                         | values-w600dp padding_start / _end                   |
| WideWindowWidth       | Dp            | 600.dp                                        | the values-w600dp qualifier                          |
| TopPadding            | Dp            | 16.dp                                         | coui_component_search_history_padding_top            |
| BottomPadding         | Dp            | 32.dp                                         | coui_component_search_history_padding_bottom         |
| TitleVerticalPadding  | Dp            | 7.dp                                          | coui_component_search_history_title_margin_vertical  |
| ClearIconSize         | Dp            | 18.dp                                         | coui_component_search_history_delete_icon_size       |
| ClearPressRadius      | Dp            | 24.dp                                         | coui_component_icon_bg ripple radius                 |
| FlowTopMargin         | Dp            | 8.dp                                          | coui_component_search_history_flow_margin_top        |
| ItemSpacing           | Dp            | 8.dp                                          | coui_component_search_history_flow_item_spacing      |
| LineSpacing           | Dp            | 8.dp                                          | coui_component_search_history_flow_line_spacing      |
| ChipHeight            | Dp            | 28.dp                                         | coui_chip_suggest_style_height                       |
| ChipMinWidth          | Dp            | 52.dp                                         | coui_chip_default_min_width                          |
| ChipMaxWidth          | Dp            | 300.dp                                        | coui_chip_default_max_width                          |
| ChipTextMaxWidth      | Dp            | 200.dp                                        | coui_chip_default_max_text_width                     |
| ChipHorizontalPadding | Dp            | 12.dp                                         | coui_chip_suggest_style_chip_horizontal_padding      |
| ToggleIconSize        | Dp            | 16.dp                                         | coui_component_expand_arrow_drop_down                |

| Method                | Type                | Description                                     |
| --------------------- | ------------------- | ----------------------------------------------- |
| titleTextStyle()      | TextStyle           | subtitle, couiTextAppearanceButton 14sp medium  |
| chipTextStyle()       | TextStyle           | bodyXS, couiTextBodyXS 12sp                     |
| searchHistoryColors() | SearchHistoryColors | Creates the panel colors                        |

### `searchHistoryColors()` factory

| Parameter         | Type  | Default                                  | COUI Source              |
| ----------------- | ----- | ---------------------------------------- | ------------------------ |
| titleColor        | Color | COUITheme.colorScheme.labelPrimary       | couiColorPrimaryNeutral  |
| clearIconColor    | Color | COUITheme.colorScheme.labelSecondary | couiColorSecondNeutral   |
| clearPressColor   | Color | COUITheme.colorScheme.press              | couiColorPress           |
| chipColor         | Color | COUITheme.colorScheme.container8         | couiColorContainer8      |
| chipContentColor  | Color | COUITheme.colorScheme.labelPrimary          | couiColorLabelPrimary    |
| toggleButtonColor | Color | COUITheme.colorScheme.container8         | couiColorContainer8      |
| toggleIconColor   | Color | COUITheme.colorScheme.labelSecondary | couiColorSecondNeutral   |

## Behavior

- Records are 28dp capsules (12sp text, 12dp horizontal padding) laid out like `COUIFlowLayout`: a record joins the line while it fits with 8dp gaps; a record wider than the panel takes a line of its own.
- Expanding switches to the expanded height at once, fades the extra records and the fold button in over 400ms after 100ms and fades the expand button out over 250ms. Folding fades them out over 300ms and then shrinks the panel. All fades use COUIMoveEaseInterpolator; invisible records cannot be clicked.
- Records press like COUI chips (press scale + couiColorPress tint); the expand and fold buttons scale to 0.9 over 200ms and back over 340ms.
- Clicking the clear button calls `onClear` right away and fades the whole panel out over 300ms; the cleared records stay on screen during the fade, so `onClear` can empty `items` immediately (COUI `setOnDeleteClickListener`).
- `SearchHistoryDefaults.insideMargin()` pads the sides by 16dp, or 20dp once the window is at least 600dp wide (COUI `values-w600dp`).
