# NavigationRail

`NavigationRail` is a side navigation component in COUI, suitable for wide screens. It offers different display modes (icon only, text only, icon and text, icon with selected label).

The header keeps the top of the rail. The items spread over the middle of it as in `COUINavigationRailMenuView`: one gap before each item and one after the last, each `max((rail height x 0.4 - item heights) / (item count + 1), 8dp)`, the whole group centred vertically. The group never rides up under the header, and the rail scrolls when the items do not fit.

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=navigationRail" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.NavigationRail
import io.github.suqi8.coui.kmp.basic.NavigationRailItem
import io.github.suqi8.coui.kmp.basic.NavigationRailDisplayMode
```

## Basic Usage

The NavigationRail component can be used to create side navigation menus:

```kotlin
var selectedIndex by remember { mutableStateOf(0) }
val items = listOf("Home", "Profile", "Settings")
val icons = listOf(COUIIcons.VerticalSplit, COUIIcons.Contacts, COUIIcons.Settings)

Row {
    NavigationRail {
        items.forEachIndexed { index, label ->
            NavigationRailItem(
                selected = selectedIndex == index,
                onClick = { selectedIndex = index },
                icon = icons[index],
                label = label
            )
        }
    }
    // Content area
}
```

## Component States

### Selected State

`NavigationRailItem` automatically handles the visual style of the selected item, highlighting the icon and text. Labels always use medium font weight (`sans-serif-medium`).

## Properties

### NavigationRail Properties

| Property Name              | Type                                   | Description                                   | Default Value                     | Required |
| -------------------------- | -------------------------------------- | --------------------------------------------- | --------------------------------- | -------- |
| modifier                   | Modifier                               | Modifier applied to the rail                  | Modifier                          | No       |
| header                     | @Composable (ColumnScope.() -> Unit)?  | Header content (e.g. FAB or Logo)             | null                              | No       |
| color                      | Color                                  | Background color of the rail                  | COUITheme.colorScheme.backgroundWithCard    | No       |
| showDivider                | Boolean                                | Show divider line between rail and content    | true                              | No       |
| defaultWindowInsetsPadding | Boolean                                | Apply default window insets padding           | true                              | No       |
| minWidth                   | Dp                                     | Minimum width of the rail                     | NavigationRailDefaults.MinWidth   | No       |
| mode                       | NavigationRailDisplayMode                  | Display mode for items                        | NavigationRailDisplayMode.IconAndText | No       |
| content                    | @Composable () -> Unit                 | The items of the rail; each root composable is one item at the rail's full width | - | Yes |

### NavigationRailItem Properties

| Property Name | Type        | Description                      | Default Value | Required |
| ------------- | ----------- | -------------------------------- | ------------- | -------- |
| selected      | Boolean     | Whether the item is selected     | -             | Yes      |
| onClick       | () -> Unit  | Callback when the item is clicked| -             | Yes      |
| icon          | ImageVector | Icon of the item                 | -             | Yes      |
| label         | String      | Label of the item                | -             | Yes      |
| modifier      | Modifier    | Modifier applied to the item     | Modifier      | No       |
| enabled       | Boolean     | Whether the item is enabled      | true          | No       |

### NavigationRailDefaults Object

The NavigationRailDefaults object provides default values for NavigationRail and NavigationRailItem components.

#### Constants

| Constant Name          | Type     | Description                                | Default Value |
| ---------------------- | -------- | ------------------------------------------ | ------------- |
| MinWidth               | Dp       | Minimum width of the rail                  | 72.dp         |
| VerticalPadding        | Dp       | Distance of the header from the top        | 24.dp         |
| HeaderSpacing          | Dp       | Least spacing between header and first item | 24.dp        |
| MinItemSpacing         | Dp       | Least gap before each item (`coui_navigation_rail_root_view_divider_height`) | 8.dp |
| ItemAreaFraction       | Float    | Share of the rail height the items spread over | 0.4f      |
| IconSize               | Dp       | Icon size                                  | 24.dp         |
| IconTextSpacing        | Dp       | Spacing between icon and text              | 2.dp          |
| ItemVerticalPadding    | Dp       | Space above the icon and below the label (`coui_navigation_margin_vertical`) | 12.dp |
| LabelFontSize          | Dp       | Label text size, in dp (does not follow the font scale) | 10.dp |
| TextOnlyFontSize       | TextUnit | Font size in TextOnly mode                 | 14.sp         |
| TextOnlyVerticalPadding| Dp       | Vertical padding in TextOnly mode          | 4.dp          |
| SelectedPressedAlpha   | Float    | Alpha for selected pressed item            | 0.5f          |
| UnselectedPressedAlpha | Float    | Alpha for unselected pressed item          | 0.6f          |
| UnselectedAlpha        | Float    | Alpha for unselected item                  | 0.4f          |

### NavigationRailDisplayMode

| Value                 | Description                                       |
| --------------------- | ------------------------------------------------- |
| IconAndText           | Display both icon and text.                       |
| IconOnly              | Display only icon.                                |
| TextOnly              | Display only text.                                |
| IconWithSelectedLabel | Display icon always, and text only when selected. |

The display mode set on `NavigationRail` is delivered to its items through the `LocalNavigationRailDisplayMode` composition local.

## Advanced Usage

### Display Modes

```kotlin
NavigationRail(
    // IconAndText (default) / IconOnly / TextOnly / IconWithSelectedLabel
    mode = NavigationRailDisplayMode.IconWithSelectedLabel
) {
    // ... items ...
}
```

### With Header

```kotlin
NavigationRail(
    header = {
        // Usually a FloatingActionButton or a logo
        FloatingActionButton(
            onClick = { /* Handle click */ }
        ) {
            Icon(
                imageVector = COUIIcons.Add,
                contentDescription = "Add"
            )
        }
    }
) {
    // ... items ...
}
```

### Custom Color and Width

```kotlin
NavigationRail(
    color = COUITheme.colorScheme.backgroundElevated,
    minWidth = 96.dp
) {
    // ... items ...
}
```

### Without Divider

```kotlin
NavigationRail(
    showDivider = false
) {
    // ... items ...
}
```
