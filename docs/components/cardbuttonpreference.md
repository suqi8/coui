# CardButtonPreference

`CardButtonPreference` is a card-style button with an icon above a one-line title, mirroring ColorOS's `COUICardButtonPreference`. Each tile is a 16dp-corner card (`couiColorCardBackground`) padded 8dp horizontally and 12dp vertically, with the title 4dp below the icon. The title is 12sp and drops to 10sp when it does not fit on one line, then ellipsizes. Pressing scales the tile down instead of tinting it. `CardButtonPreferenceGroup` lays tiles out in the COUI `CardButtonDecoration` grid: columns 8dp apart, rows 16dp apart, tiles in a row sharing its height.

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=cardButtonPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.preference.CardButtonItem
import io.github.suqi8.coui.kmp.preference.CardButtonPreference
import io.github.suqi8.coui.kmp.preference.CardButtonPreferenceDefaults
import io.github.suqi8.coui.kmp.preference.CardButtonPreferenceGroup
```

## Basic Usage

```kotlin
val items = remember {
    listOf(
        CardButtonItem("Scan", icon = { Icon(COUIIcons.Scan, null) }, onClick = { /* scan */ }),
        CardButtonItem("Photos", icon = { Icon(COUIIcons.Photos, null) }, onClick = { /* photos */ }),
        CardButtonItem("Music", icon = { Icon(COUIIcons.Music, null) }, onClick = { /* music */ }),
        CardButtonItem("Backup", icon = { Icon(COUIIcons.Backup, null) }, onClick = { /* backup */ })
    )
}

CardButtonPreferenceGroup(
    items = items,
    modifier = Modifier.padding(horizontal = 16.dp)
)
```

A single tile can also be placed directly:

```kotlin
CardButtonPreference(
    title = "Scan",
    onClick = { /* scan */ },
    icon = { Icon(COUIIcons.Scan, null) }
)
```

## Properties

### CardButtonPreference Properties

| Property Name | Type                         | Description                                 | Default Value                                            | Required |
| ------------- | ---------------------------- | ------------------------------------------- | -------------------------------------------------------- | -------- |
| title         | String                       | Title of the tile                           | -                                                        | Yes      |
| onClick       | (() -> Unit)?                | Callback when clicked; null renders inert   | -                                                        | Yes      |
| modifier      | Modifier                     | Component modifier                          | Modifier                                                 | No       |
| enabled       | Boolean                      | Whether the tile can be clicked             | true                                                     | No       |
| cornerRadius  | Dp                           | Corner radius of the tile                   | CardButtonPreferenceDefaults.CornerRadius                | No       |
| insideMargin  | PaddingValues                | Padding inside the tile                     | CardButtonPreferenceDefaults.InsideMargin                | No       |
| colors        | CardButtonPreferenceColors   | Colors of the tile                          | CardButtonPreferenceDefaults.cardButtonPreferenceColors() | No      |
| icon          | @Composable (() -> Unit)?    | Icon above the title                        | null                                                     | No       |

### CardButtonPreferenceGroup Properties

| Property Name | Type                        | Description                    | Default Value                                            | Required |
| ------------- | --------------------------- | ------------------------------ | -------------------------------------------------------- | -------- |
| items         | List\<CardButtonItem>       | Tiles in display order         | -                                                        | Yes      |
| modifier      | Modifier                    | Component modifier             | Modifier                                                 | No       |
| columns       | Int                         | Number of columns              | CardButtonPreferenceDefaults.Columns                     | No       |
| colors        | CardButtonPreferenceColors  | Colors of the tiles            | CardButtonPreferenceDefaults.cardButtonPreferenceColors() | No      |

### CardButtonItem

| Field   | Type                      | Description                                | Default |
| ------- | ------------------------- | ------------------------------------------ | ------- |
| title   | String                    | Title of the tile                          | -       |
| icon    | @Composable (() -> Unit)? | Icon above the title                       | null    |
| enabled | Boolean                   | Whether the tile can be clicked            | true    |
| onClick | (() -> Unit)?             | Callback when clicked; null renders inert  | null    |

### CardButtonPreferenceDefaults Object

| Name               | Type          | Default Value                                    | Description                                                    |
| ------------------ | ------------- | ------------------------------------------------ | -------------------------------------------------------------- |
| CornerRadius       | Dp            | 16.dp                                            | coui_component_card_button_bg corners                          |
| InsideMargin       | PaddingValues | PaddingValues(horizontal = 8.dp, vertical = 12.dp) | Card button layout padding                                   |
| TitleTopMargin     | Dp            | 4.dp                                             | Gap between icon and title                                     |
| TitleFontSize      | TextUnit      | 12.sp                                            | CARD_BUTTON_TEXT_SIZE_NORMAL                                   |
| SmallTitleFontSize | TextUnit      | 10.sp                                            | CARD_BUTTON_TEXT_SIZE_SMALL (used when 12sp overflows)         |
| ColumnSpacing      | Dp            | 8.dp                                             | 2 x coui_component_card_button_horizontal_margin_inner         |
| RowSpacing         | Dp            | 16.dp                                            | coui_component_card_button_first_top_margin                    |
| Columns            | Int           | 4                                                | Default column count (COUI leaves it to the host grid)         |

#### `cardButtonPreferenceColors()` factory

| Parameter          | Type  | Default                                             |
| ------------------ | ----- | --------------------------------------------------- |
| containerColor     | Color | COUITheme.colorScheme.cardBackground              |
| titleColor         | Color | COUITheme.colorScheme.labelPrimary                     |
| disabledTitleColor | Color | COUITheme.colorScheme.disabledNeutral    |

### CardButtonPreferenceColors Class

| Property           | Type  | Description                      |
| ------------------ | ----- | -------------------------------- |
| containerColor     | Color | Fill of the tile                 |
| titleColor         | Color | Title color                      |
| disabledTitleColor | Color | Title color of a disabled tile   |
