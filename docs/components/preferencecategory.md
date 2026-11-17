# PreferenceCategory

`PreferenceCategory` is a category title mirroring ColorOS's `COUIPreferenceCategory`. It covers the three title sizes, the two start margin types, an icon or a text red dot next to the title, and an end-aligned widget. `SmallTitle` is its small, title-only shortcut; `CheckboxPreferenceCategory` and `LoadingPreferenceCategory` cover the checkbox and loading widgets.

<div style="position: relative; height: 420px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=preferenceCategory" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.preference.PreferenceCategory
import io.github.suqi8.coui.kmp.preference.PreferenceCategoryDefaults
import io.github.suqi8.coui.kmp.preference.PreferenceCategoryMarginStartType
import io.github.suqi8.coui.kmp.preference.PreferenceCategoryTitleType
```

## Basic Usage

Place the category above the card it titles:

```kotlin
PreferenceCategory(
    title = "Network",
    titleType = PreferenceCategoryTitleType.Medium,
    endWidget = {
        PreferenceCategoryDefaults.TextButton(text = "Edit", onClick = { /* ... */ })
    }
)
Card(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp)) {
    // rows
}
```

## Title Types

| Type   | Size | Min height | Above / below | Color                                         |
| ------ | ---- | ---------- | ------------- | --------------------------------------------- |
| Small  | 12sp | 16dp       | 8dp / 8dp     | couiColorSecondNeutral (PrimaryNeutral with a title icon) |
| Medium | 14sp | 20dp       | 8dp / 8dp     | couiColorPrimaryNeutral                       |
| Large  | 16sp | 22dp       | 7dp / 7dp     | couiColorPrimaryNeutral                       |

All sizes are sans-serif-medium. Next to a Medium or Large title, the text widgets (text button, text with icon, loading text) grow to 14sp and to a 28dp minimum height.

## Margin Types

| Type  | Title start | Widget frame end margin | Widget content end |
| ----- | ----------- | ----------------------- | ------------------ |
| Large | 32dp        | 16dp                    | 32dp               |
| Small | 16dp        | 0dp                     | 16dp               |

The ready-made widgets keep 16dp (`WidgetContentEndPadding`) between their content and the widget frame end, so their content lines up with the checkbox and loading widgets. Add the same padding to a custom widget. Without an `endWidget` the title keeps the start margin at its end too.

Spacing convention: the row only carries the title's own vertical margin, not the COUI 16dp item top margin. Cards bring that space with their own `padding(bottom = 16.dp)`, which adds up to the COUI 24dp from a card bottom to the title text. `TopMarginLarge` / `TopMarginSmall` are available for a category that does not follow such a card.

## End Widgets

| Widget                                     | COUI layout                                    | Details                                                                  |
| ------------------------------------------ | ---------------------------------------------- | ------------------------------------------------------------------------ |
| `PreferenceCategoryDefaults.TextButton`    | `coui_preference_category_widget_layout_textbutton` | couiColorPrimary label, at most two lines, 12dp / 4dp capsule press area, 6dp above and below |
| `PreferenceCategoryDefaults.SingleIcon`    | `coui_preference_category_widget_layout_singleicon` | Icon padded 8dp / 4dp, 16dp circular press mask when clickable    |
| `PreferenceCategoryDefaults.TextWithIcon`  | `coui_preference_category_widget_layout_textwithicon` | Text (two lines) + icon 4dp apart, padded by half its height; text couiColorPrimaryNeutral when clickable, otherwise couiColorSecondNeutral |

## Properties

### PreferenceCategory Properties

| Property Name   | Type                                 | Description                                         | Default Value                                       | Required |
| --------------- | ------------------------------------ | --------------------------------------------------- | --------------------------------------------------- | -------- |
| title           | String                               | Category title                                      | -                                                   | Yes      |
| modifier        | Modifier                             | Component modifier                                  | Modifier                                            | No       |
| titleType       | PreferenceCategoryTitleType          | Title size                                          | PreferenceCategoryTitleType.Small                   | No       |
| marginStartType | PreferenceCategoryMarginStartType    | Horizontal margin type                              | PreferenceCategoryMarginStartType.Large             | No       |
| titleBadge      | String?                              | Text red dot after the title; hidden with a title icon | null                                             | No       |
| titleColor      | Color                                | Title color; Unspecified picks the COUI color       | Color.Unspecified                                   | No       |
| insideMargin    | PaddingValues                        | Row padding: title start / widget frame end         | PreferenceCategoryDefaults.insideMargin(marginStartType) | No  |
| titleIcon       | (@Composable () -> Unit)?            | Icon after the title                                | null                                                | No       |
| endWidget       | (@Composable RowScope.() -> Unit)?   | Widget at the end of the row                        | null                                                | No       |

### PreferenceCategoryDefaults Object

| Name                               | Type          | Default Value | Description                                                         |
| ---------------------------------- | ------------- | ------------- | ------------------------------------------------------------------- |
| TitleMarginStartLarge / Small      | Dp            | 32.dp / 16.dp | Title start margin of each margin type                              |
| WidgetFrameMarginEndLarge          | Dp            | 16.dp         | Extra widget frame end margin of the Large type                     |
| WidgetContentEndPadding            | Dp            | 16.dp         | Space between widget content and the widget frame end               |
| TopMarginLarge / TopMarginSmall    | Dp            | 16.dp / 12.dp | COUI item top margins (not applied by default)                      |
| TitleMinHeightSmall / Medium / Large | Dp          | 16 / 20 / 22.dp | Title minimum heights                                             |
| TitleVerticalMargin / TitleVerticalMarginLarge | Dp | 8.dp / 7.dp | Space above and below the title                                    |
| TitleIconSpacing / TitleIconSpacingLarge | Dp      | 4.dp / 8.dp   | Gap between the title and its icon                                  |
| TitleBadgeSpacing                  | Dp            | 8.dp          | Gap between the title and the badge                                 |
| IconPressRadius                    | Dp            | 16.dp         | Circular press mask radius of icons                                 |
| WidgetMinHeightWhenTitleIsNotSmall | Dp            | 28.dp         | Text widget minimum height next to Medium / Large titles            |
| TextButtonMarginEnd / TextButtonMarginVertical | Dp | 4.dp / 6.dp | Text button margins                                               |
| TextButtonPadding                  | PaddingValues | 12.dp / 4.dp  | Text button press area padding                                      |
| SingleIconMarginEnd                | Dp            | 8.dp          | Single icon end margin                                              |
| SingleIconPadding                  | PaddingValues | 8.dp / 4.dp   | Single icon padding                                                 |
| TextWithIconSpacing                | Dp            | 4.dp          | Gap between text and icon                                           |
| TextWithIconVerticalPadding        | Dp            | 4.dp          | Vertical press padding of a clickable text with icon                |
| insideMargin(marginStartType)      | PaddingValues | -             | Default row padding                                                 |
| titleColor(titleType, hasTitleIcon) | Color        | -             | labelSecondary (Small without icon) / labelPrimary                |
| titleTextStyle(titleType) / widgetTextStyle(titleType) | TextStyle | - | Title and widget text styles                               |
| TitleIcon / TextButton / SingleIcon / TextWithIcon | @Composable | - | Ready-made title icon and widgets                               |

## Advanced Usage

### Title Icon

```kotlin
PreferenceCategory(
    title = "Shortcuts",
    titleIcon = {
        PreferenceCategoryDefaults.TitleIcon(
            imageVector = COUIIcons.Info,
            contentDescription = "About shortcuts",
            onClick = { showHelp = true }
        )
    }
)
```

### Text With Icon, Small Margin Type

```kotlin
PreferenceCategory(
    title = "Nearby devices",
    marginStartType = PreferenceCategoryMarginStartType.Small,
    endWidget = {
        PreferenceCategoryDefaults.TextWithIcon(
            text = "Refresh",
            icon = COUIIcons.Refresh,
            onClick = { refresh() }
        )
    }
)
```
