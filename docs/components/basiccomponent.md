# BasicComponent

`BasicComponent` is a foundational standard component in COUI. It provides customizable content areas on the start and end sides, along with a title and summary, making it suitable for building list items, settings items, and other UI elements.

This project builds upon it to provide some extended components, enabling developers to quickly create UI components that conform to design specifications. See the usage of [Extended Components](../components/#extended-components) for details.

<div style="position: relative; height: 350px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=basicComponent" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.BasicComponent
```

## Basic Usage

The BasicComponent can be used to display title and summary information:

```kotlin
BasicComponent(
    title = "Setting Item Title",
    summary = "This is the description text for the setting item"
)
```

## Component Variants

### Clickable Component

```kotlin
BasicComponent(
    title = "Wi-Fi",
    summary = "Connected to HOME-WiFi",
    onClick = { /* Handle click event */ }
)
```

### With Start Icon

```kotlin
BasicComponent(
    title = "Nickname",
    summary = "A brief introduction",
    startAction = {
        Icon(
            imageVector = COUIIcons.Contacts,
            contentDescription = "Avatar Icon",
            tint = COUITheme.colorScheme.labelPrimary
        )
    },
    onClick = { /* Handle click event */ }
)
```

### With End Actions

```kotlin
var isFlightMode by remember { mutableStateOf(false) }

BasicComponent(
    title = "Flight Mode",
    endActions = {
        Switch(
            checked = isFlightMode,
            onCheckedChange = { isFlightMode = it }
        )
    }
)
```

### Custom Content Variant

In addition to the title and summary variant, BasicComponent also provides an overload that accepts custom content. This is useful when you want full control over the layout inside the component while still reusing the container and interactions:

```kotlin
BasicComponent(
    startAction = {
        Icon(
            imageVector = COUIIcons.Contacts,
            contentDescription = "Avatar Icon",
            tint = COUITheme.colorScheme.labelPrimary
        )
    },
    endActions = {
        Text("Details")
    }
) {
    Text(
        text = "Custom Title",
        style = COUITheme.textStyles.headlineXS
    )
    Text(
        text = "Custom content description",
        style = COUITheme.textStyles.bodyS
    )
}
```

## Component States

### Disabled State

```kotlin
BasicComponent(
    title = "Mobile Network",
    summary = "SIM card not inserted",
    enabled = false
)
```

## Properties

### BasicComponent Properties (title and summary variant)

| Property Name     | Type                               | Description                                           | Default Value                         | Required |
| ----------------- | ---------------------------------- | ----------------------------------------------------- | ------------------------------------- | -------- |
| modifier          | Modifier                           | Modifier for the component                            | Modifier                              | No       |
| title             | String?                            | Title of the component                                | null                                  | No       |
| titleColor        | BasicComponentColors               | Title color configuration                             | BasicComponentDefaults.titleColor()   | No       |
| summary           | String?                            | Summary of the component                              | null                                  | No       |
| summaryColor      | BasicComponentColors               | Summary color configuration                           | BasicComponentDefaults.summaryColor() | No       |
| startAction       | @Composable (() -> Unit)?          | Composable content on the start side of the component | null                                  | No       |
| endActions        | @Composable (RowScope.() -> Unit)? | Composable content on the end side of the component   | null                                  | No       |
| bottomAction      | @Composable (() -> Unit)?          | Composable content at the bottom of the component     | null                                  | No       |
| insideMargin      | PaddingValues                      | Internal padding of the component                     | BasicComponentDefaults.InsideMargin   | No       |
| cardListPosition  | CardListPosition                   | Row position inside its card group ([CardListGroup](./card.md#cardlistgroup)); rounded outer edges gain extra padding | LocalCardListPosition.current (None outside a CardListGroup) | No       |
| onClick           | (() -> Unit)?                      | Callback triggered when the component is clicked      | null                                  | No       |
| onClickLabel      | String?                            | Accessibility label describing the click action       | null                                  | No       |
| role              | Role?                              | Semantic role for accessibility services              | null                                  | No       |
| holdDownState     | Boolean                            | Whether the component is in the pressed state         | false                                 | No       |
| enabled           | Boolean                            | Whether the component is enabled                      | true                                  | No       |
| selected          | Boolean                            | Whether the row is selected: the press layer stays on and, inside a CardListGroup, the dividers above and below the row hide | false | No       |
| startBadge        | Boolean                            | Whether a red dot sits on the top end corner of startAction | false | No       |
| endBadgeCount     | Int?                               | Red dot before the end content: null hidden, 0 a plain dot, > 0 a number | null | No       |
| endIcon           | @Composable (() -> Unit)?          | 16dp icon right before endActions                     | null                                  | No       |
| interactionSource | MutableInteractionSource?          | Interaction source of the component                   | null                                  | No       |

### BasicComponent Properties (custom content variant)

| Property Name     | Type                               | Description                                           | Default Value                       | Required |
| ----------------- | ---------------------------------- | ----------------------------------------------------- | ----------------------------------- | -------- |
| modifier          | Modifier                           | Modifier for the component                            | Modifier                            | No       |
| startAction       | @Composable (() -> Unit)?          | Composable content on the start side of the component | null                                | No       |
| endActions        | @Composable (RowScope.() -> Unit)? | Composable content on the end side of the component   | null                                | No       |
| bottomAction      | @Composable (() -> Unit)?          | Composable content at the bottom of the component     | null                                | No       |
| insideMargin      | PaddingValues                      | Internal padding of the component                     | BasicComponentDefaults.InsideMargin | No       |
| cardListPosition  | CardListPosition                   | Row position inside its card group ([CardListGroup](./card.md#cardlistgroup)); rounded outer edges gain extra padding | LocalCardListPosition.current (None outside a CardListGroup) | No       |
| onClick           | (() -> Unit)?                      | Callback triggered when the component is clicked      | null                                | No       |
| onClickLabel      | String?                            | Accessibility label describing the click action       | null                                | No       |
| role              | Role?                              | Semantic role for accessibility services              | null                                | No       |
| holdDownState     | Boolean                            | Whether the component is in the pressed state         | false                               | No       |
| enabled           | Boolean                            | Whether the component is enabled                      | true                                | No       |
| selected          | Boolean                            | Whether the row is selected: the press layer stays on and, inside a CardListGroup, the dividers above and below the row hide | false | No       |
| startBadge        | Boolean                            | Whether a red dot sits on the top end corner of startAction | false | No       |
| endBadgeCount     | Int?                               | Red dot before the end content: null hidden, 0 a plain dot, > 0 a number | null | No       |
| endIcon           | @Composable (() -> Unit)?          | 16dp icon right before endActions                     | null                                  | No       |
| interactionSource | MutableInteractionSource?          | Interaction source of the component                   | null                                | No       |
| content           | @Composable ColumnScope.() -> Unit | Composable content of the component                   | -                                   | Yes      |

### BasicComponentDefaults Object

The BasicComponentDefaults object provides default values and color configurations for the BasicComponent.

#### BasicComponentDefaults Constants

| Constant Name | Type          | Description                       | Default Value        |
| ------------- | ------------- | --------------------------------- | -------------------- |
| MinHeight     | Dp            | Minimum row height, before card-group position padding | 48.dp |
| InsideMargin  | PaddingValues | Internal padding of the component | PaddingValues(horizontal = 16.dp, vertical = 10.dp) |
| StartActionSpacing | Dp | Gap between startAction and the title (coui_preference_icon_margin_right) | 16.dp |
| EndActionsSpacing | Dp | Minimum gap between the title and the end content (support_preference_title_margin_end_in_right) | 16.dp |
| EndLowPriorityMinWidth | Dp | Width the end content keeps when the title is long; below it the end content always gets its full width | 80.dp |
| EndDecorationSpacing | Dp | Gap after the end red dot / end icon | 4.dp |
| EndIconSize | Dp | Size of endIcon | 16.dp |
| StartBadgeInset | Dp | Inset of the start red dot from the startAction corner | 2.dp |
| StartActionMinTopMargin | Dp | Minimum distance from the row top to top-aligned start content | 8.dp |

`startActionVerticalMargin(heightDp, lineCount)` returns the COUI vertical margin of the start content: 24dp content 12dp (20dp with two lines of text), 32dp 10dp / 16dp, 36dp 12dp / 14dp, 50dp 8dp, other sizes 14dp. The row grows to fit the start content plus these margins; once the title and summary exceed two lines, the start content is aligned with the center of the first two lines instead. The start content keeps 16dp to the title, so do not pad your icons yourself.

#### BasicComponentDefaults Methods

| Method Name    | Type                 | Description                         |
| -------------- | -------------------- | ----------------------------------- |
| titleColor()   | BasicComponentColors | Creates title color configuration (`labelPrimary`, disabled `disabledNeutral`) |
| summaryColor() | BasicComponentColors | Creates summary color configuration (`labelSecondary`, COUI `couiColorSecondNeutral`; disabled `disabledNeutral`) |

The title uses `textStyles.headlineXS` (16sp Medium, COUI `TextAppearance.COUI.List.Title`). The summary uses
`textStyles.bodyS` (13sp, COUI `TextAppearance.COUI.Preference.Summary`) and sits directly below the
title: COUI 17 removed the 2dp `layout_marginTop` the summary had in COUI 16.

### BasicComponentColors Class

Used to configure the color states of the component.

| Property Name | Type  | Description             |
| ------------- | ----- | ----------------------- |
| color         | Color | Color in normal state   |
| disabledColor | Color | Color in disabled state |

## Advanced Usage

### Complex Layout Component

```kotlin
BasicComponent(
    title = "Volume",
    summary = "Media Volume: 70%",
    startAction = {
        Icon(
            imageVector = COUIIcons.Play,
            contentDescription = "Volume Icon",
            tint = COUITheme.colorScheme.labelPrimary
        )
    },
    endActions = {
        IconButton(onClick = { /* Decrease volume */ }) {
            Icon(
                imageVector = COUIIcons.Remove,
                contentDescription = "Decrease Volume",
                tint = COUITheme.colorScheme.labelPrimary
            )
        }
        Text("70%")
        IconButton(onClick = { /* Increase volume */ }) {
            Icon(
                imageVector = COUIIcons.Add,
                contentDescription = "Increase Volume",
                tint = COUITheme.colorScheme.labelPrimary
            )
        }
    }
)
```

### Custom Style Component

```kotlin
BasicComponent(
    title = "Custom Component",
    summary = "Using custom colors",
    titleColor = BasicComponentColors(
        color = Color.Blue,
        disabledColor = Color.Gray
    ),
    summaryColor = BasicComponentColors(
        color = Color.DarkGray,
        disabledColor = Color.LightGray
    ),
    insideMargin = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    onClick = { /* Handle option click */ }
)
```

### Usage in a List

```kotlin
val options = listOf("Option 1", "Option 2", "Option 3", "Option 4")

Column {
    options.forEach { option ->
        BasicComponent(
            title = option,
            onClick = { /* Handle option click */ }
        )
    }
}
```

### Usage in a Card Group

Inside a `Card`, wrap the rows in a `CardListGroup` so each row gets its card list position (the
2dp head / tail padding on the rounded edges). Rows outside a group keep `CardListPosition.None`,
and an explicit `cardListPosition` always wins. The group also draws the row dividers, aligned with
each row's title and hidden around `selected` rows, so do not add `HorizontalDivider`s between them.
See [CardListGroup](./cardlistgroup.md).

```kotlin
Card {
    CardListGroup {
        options.forEach { option ->
            item(key = option) {
                BasicComponent(
                    title = option,
                    onClick = { /* Handle option click */ }
                )
            }
        }
    }
}
```
