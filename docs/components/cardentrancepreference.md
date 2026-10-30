# CardEntrancePreference

`CardEntrancePreference` is a grid of entrance cards, mirroring a run of ColorOS `COUICardEntrancePreference` items in their two-span grid. A small card (`CardEntranceType.Small`) takes half a row, with the icon above the title and summary; a large card (`CardEntranceType.Large`) takes a full row, with the icon beside the texts. Each card is its own rounded tile (12dp small / 16dp large corners) and scales down while pressed. Small cards in a row share its height; the columns are 8dp apart, a small row starts 8dp and a large card 16dp below the previous row.

<div style="position: relative; height: 330px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=cardEntrancePreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.preference.CardEntranceItem
import io.github.suqi8.coui.kmp.preference.CardEntrancePreference
import io.github.suqi8.coui.kmp.preference.CardEntranceType
```

## Basic Usage

```kotlin
val items = remember {
    listOf(
        CardEntranceItem(title = "Privacy", summary = "Permissions", icon = { PrivacyIcon() }, onClick = { /* open */ }),
        CardEntranceItem(title = "Location", summary = "On", statusOn = true, icon = { LocationIcon() }, onClick = { /* open */ }),
        CardEntranceItem(
            title = "Backup & restore",
            summary = "Last backup: yesterday",
            type = CardEntranceType.Large,
            icon = { BackupIcon() },
            onClick = { /* open */ }
        )
    )
}

CardEntrancePreference(
    items = items,
    modifier = Modifier.padding(horizontal = 16.dp)
)
```

`statusOn` draws the summary in the accent color, like an "On" status. A small card left alone before a large card keeps half the row empty.

## Properties

### CardEntrancePreference Properties

| Property Name | Type                          | Description              | Default Value                                               | Required |
| ------------- | ----------------------------- | ------------------------ | ----------------------------------------------------------- | -------- |
| items         | List&lt;CardEntranceItem&gt;  | Cards in display order   | -                                                           | Yes      |
| modifier      | Modifier                      | Component modifier       | Modifier                                                    | No       |
| colors        | CardEntrancePreferenceColors  | Card colors              | CardEntrancePreferenceDefaults.cardEntrancePreferenceColors() | No     |

### CardEntranceItem Properties

| Property Name | Type                       | Description                                   | Default Value          |
| ------------- | -------------------------- | --------------------------------------------- | ---------------------- |
| title         | String                     | Card title                                    | -                      |
| summary       | String?                    | Summary below the title                       | null                   |
| type          | CardEntranceType           | Small (half row) or Large (full row)          | CardEntranceType.Small |
| icon          | @Composable (() -> Unit)?  | Card icon                                     | null                   |
| statusOn      | Boolean                    | Draw the summary in the accent color          | false                  |
| enabled       | Boolean                    | Whether the card can be clicked               | true                   |
| onClick       | (() -> Unit)?              | Click callback; null renders an inert card    | null                   |

### CardEntrancePreferenceDefaults Object

| Constant              | Type          | Default Value                                                  | Description                                          |
| --------------------- | ------------- | -------------------------------------------------------------- | ---------------------------------------------------- |
| SmallCornerRadius     | Dp            | 12.dp                                                          | coui_component_card_entrance_small_bg                |
| LargeCornerRadius     | Dp            | 16.dp                                                          | coui_component_card_entrance_large_bg                |
| SmallInsideMargin     | PaddingValues | PaddingValues(16.dp)                                           | Small card padding                                   |
| LargeInsideMargin     | PaddingValues | PaddingValues(start = 16.dp, end = 12.dp, top = 20.dp, bottom = 20.dp) | Large card padding                          |
| SmallTitleTopMargin   | Dp            | 12.dp                                                          | Icon to title (small)                                |
| SmallSummaryTopMargin | Dp            | 4.dp                                                           | Title to summary (small)                             |
| LargeIconSpacing      | Dp            | 16.dp                                                          | Icon to texts (large)                                |
| LargeSummaryTopMargin | Dp            | 3.dp                                                           | Title to summary (large)                             |
| ColumnSpacing         | Dp            | 8.dp                                                           | 2 x …_small_horizontal_margin_inner                  |
| SmallRowSpacing       | Dp            | 8.dp                                                           | …_small_top_margin_other                             |
| LargeRowSpacing       | Dp            | 16.dp                                                          | …_large_top_margin                                   |

#### `cardEntrancePreferenceColors()` factory

| Parameter            | Type  | Default                                            |
| -------------------- | ----- | -------------------------------------------------- |
| containerColor       | Color | COUITheme.colorScheme.surfaceContainer             |
| titleColor           | Color | COUITheme.colorScheme.onSurface                    |
| summaryColor         | Color | COUITheme.colorScheme.onSurfaceSecondary           |
| statusOnSummaryColor | Color | COUITheme.colorScheme.primary                      |
| disabledColor        | Color | COUITheme.colorScheme.disabledOnSecondaryVariant   |
