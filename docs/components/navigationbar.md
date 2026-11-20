# NavigationBar

`NavigationBar` is a bottom navigation bar component in COUI, used to create navigation menus fixed at the bottom of applications. It supports 2 to 5 navigation items, offering different display modes (icon only, text only, icon and text, icon with selected label).

For the COUI 17 floating capsule bar, see [FloatingNavigationBar](./floatingnavigationbar).

These components are typically used in conjunction with the `Scaffold` component to maintain consistent layout and behavior across different pages in the application.

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=navigationBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.NavigationBar
import io.github.suqi8.coui.kmp.basic.NavigationBarItem
import io.github.suqi8.coui.kmp.basic.NavigationBarDisplayMode
import io.github.suqi8.coui.kmp.basic.NavigationItem
```

## Basic Usage

### NavigationBar

The NavigationBar component can be used to create bottom navigation menus fixed to the bottom:

```kotlin
var selectedIndex by remember { mutableStateOf(0) }
val items = listOf("Home", "Profile", "Settings")
val icons = listOf(COUIIcons.VerticalSplit, COUIIcons.Contacts, COUIIcons.Settings)

Scaffold(
    bottomBar = {
        NavigationBar {
            items.forEachIndexed { index, label ->
                NavigationBarItem(
                    selected = selectedIndex == index,
                    onClick = { selectedIndex = index },
                    icon = icons[index],
                    label = label
                )
            }
        }
    }
)
```

## Component States

### Selected State

`NavigationBarItem` follows the COUI tab navigation state model: the selected item shows the icon and label at full label color, unselected items are dimmed, and the icon cross-fades between the two states over 180ms while the label color switches instantly. Labels always use medium font weight; pressing an unselected item previews the selected icon color.

### Hover and Focus

A pointer hovering an item, or keyboard focus on it, fades in COUI's hover / focus mask behind the icon and the label: an 8dp rounded rectangle over the whole cell (`COUINavigationItemView.configStateEffectBackground`, `?couiColorHover` / `?couiColorFocus`, each on a response 0.3 spring). A tap shows no mask, as on COUI tab bars.

### Switching Menus

The `NavigationBar(targetState) { state -> ... }` overload builds its items from a state and switches them the way `COUINavigationView.switchToMenu` does: the items on show fade out over 100ms (linear), then the items of the new state rise from the bar's bottom edge over 350ms while fading in over the first 100ms (both `COUIInEaseInterpolator`). The bar, its background and its divider stay in place.

### Wide Windows

The bar follows the window as COUI's resource qualifiers do. On windows at least 600dp wide (`values-w600dp`, `is_big_layout`), `IconAndText` items put the icon beside a 14dp label, 8dp apart, centred as a group (`COUINavigationItemView.leftToRight`), and the row keeps 44dp from the edges; landscape windows also take the 44dp edge padding (`values-land`).

## Properties

### NavigationBar Properties

| Property Name              | Type                     | Description                         | Default Value                         | Required |
| -------------------------- | ------------------------ | ----------------------------------- | ------------------------------------- | -------- |
| modifier                   | Modifier                 | Modifier applied to the nav bar     | Modifier                              | No       |
| visible                    | Boolean                  | Whether the bar is shown; hiding slides it down by its own height over 200ms (COUIOutEase) while its layout shrinks, showing reverses it over 250ms (COUIInEase) | true | No |
| color                      | Color                    | Background color of the nav bar (`?couiColorBar`) | NavigationBarDefaults.color() | No       |
| showDivider                | Boolean                  | Show top divider line or not        | true                                  | No       |
| defaultWindowInsetsPadding | Boolean                  | Apply default window insets padding | true                                  | No       |
| mode                       | NavigationBarDisplayMode    | Display mode for items              | NavigationBarDisplayMode.IconAndText     | No       |
| content                    | @Composable RowScope.()  | The content of the nav bar          | -                                     | Yes      |

The state-driven overload takes the same properties plus:

| Property Name | Type                        | Description                                                                 | Default Value | Required |
| ------------- | --------------------------- | --------------------------------------------------------------------------- | ------------- | -------- |
| targetState   | T                           | The state the items are built from; a change plays the `switchToMenu` animation | -         | Yes      |
| content       | @Composable RowScope.(T)    | The items for a state; receives the state on show, which trails `targetState` while the old items fade out | - | Yes |

### NavigationBarItem Properties

| Property Name | Type          | Description                      | Default Value | Required |
| ------------- | ------------- | -------------------------------- | ------------- | -------- |
| selected      | Boolean       | Whether the item is selected     | -             | Yes      |
| onClick       | () -> Unit    | Callback when the item is clicked| -             | Yes      |
| icon          | ImageVector   | Icon of the item                 | -             | Yes      |
| label         | String        | Label of the item                | -             | Yes      |
| modifier      | Modifier      | Modifier applied to the item     | Modifier      | No       |
| enabled       | Boolean       | Whether the item is enabled      | true          | No       |
| badge         | Int?          | Red dot on the icon: `null` none, `0` a 6dp dot (1dp past the icon's top end corner), a positive number a number badge (end edge 8dp past the icon, 5dp above it). Not shown in `TextOnly` | null | No |

### NavigationBarDefaults Object

The NavigationBarDefaults object provides default values for NavigationBar and NavigationBarItem components.

#### Constants

| Constant Name          | Type     | Description                                  | Default Value |
| ---------------------- | -------- | -------------------------------------------- | ------------- |
| ItemHeight             | Dp       | Item cell height                             | 56.dp         |
| HorizontalPadding      | Dp       | Horizontal padding at both edges of the bar  | 12.dp         |
| HorizontalPaddingWide  | Dp       | Edge padding on wide or landscape windows    | 44.dp         |
| WideLayoutBreakpoint   | Dp       | Window width from which items lay out side by side | 600.dp  |
| IconLabelSpacingWide   | Dp       | Gap between icon and label in the wide layout | 8.dp         |
| LabelFontSizeWide      | Dp       | Label text size in the wide layout, in dp    | 14.dp         |
| ItemHorizontalPadding  | Dp       | Horizontal padding inside each item cell     | 2.dp          |
| IconSize               | Dp       | Icon size                                    | 24.dp         |
| IconTopPadding         | Dp       | Top padding for the icon                     | 9.dp          |
| LabelBottomPadding     | Dp       | Bottom padding for the label                 | 7.dp          |
| LabelFontSize          | Dp       | Label text size, in dp (does not follow the font scale) | 10.dp |
| TextFontSize           | Dp       | Text size (TextOnly mode), in dp             | 14.dp         |
| IconFadeDurationMillis | Int      | Icon cross-fade duration between states      | 180           |
| HideDurationMillis     | Int      | Hide translation duration                    | 200           |
| ShowDurationMillis     | Int      | Show translation duration                    | 250           |
| MenuExitDurationMillis | Int      | Fade-out of the items on show when the menu switches | 100   |
| MenuEnterFadeDurationMillis | Int | Fade-in of the switched-in items             | 100           |
| MenuEnterDurationMillis | Int     | Rise of the switched-in items from the bar's bottom edge | 350 |
| ItemMaskCornerRadius   | Dp       | Corner radius of the hover / focus mask behind an item | 8.dp |
| BadgeDotOffset         | Dp       | How far a red dot sticks out of the icon's top end corner | 1.dp |
| BadgeNumberHorizontalOffset | Dp  | How far a number badge's end lies past the icon (-2dp under 10, +1dp from 100 to 999) | 8.dp |
| BadgeNumberVerticalOffset | Dp    | How far a number badge rises above the icon  | 5.dp          |

#### Functions

| Function Name | Return Type | Description |
| ------------- | ----------- | ----------- |
| color()       | Color       | The bar background `?couiColorBar`: `#FAFAFA` light, `#1E1E1E` dark |

### NavigationBarDisplayMode Enum

| Value                 | Description                                  |
| --------------------- | -------------------------------------------- |
| IconAndText           | Show both icon and text                      |
| IconOnly              | Show icon only                               |
| TextOnly              | Show text only                               |
| IconWithSelectedLabel | Show icon always, show text only when selected|

The display mode set on `NavigationBar` is delivered to its items through the `LocalNavigationBarDisplayMode` composition local.

### NavigationItem Properties

`NavigationItem` is a convenience data class for holding a navigation item's label and icon.

| Property Name | Type        | Description       | Default Value | Required |
| ------------- | ----------- | ----------------- | ------------- | -------- |
| label         | String      | Label of the item | -             | Yes      |
| icon          | ImageVector | Icon of the item  | -             | Yes      |

## Advanced Usage

### NavigationBar

#### Custom Colors

```kotlin
NavigationBar(
    color = Color.Red.copy(alpha = 0.3f)
) {
    // ... items ...
}
```

#### Without Divider

```kotlin
NavigationBar(
    showDivider = false
) {
    // ... items ...
}
```

#### Handling Window Insets

```kotlin
NavigationBar(
    defaultWindowInsetsPadding = false // Handle window insets padding manually
) {
    // ... items ...
}
```

#### Display Modes

```kotlin
NavigationBar(
    // IconAndText (default) / IconOnly / TextOnly / IconWithSelectedLabel
    mode = NavigationBarDisplayMode.IconWithSelectedLabel
) {
    // ... items ...
}
```

#### Switching Menus

```kotlin
var editing by remember { mutableStateOf(false) }

NavigationBar(targetState = editing) { inEditMode ->
    if (inEditMode) {
        // ... the tools of the edit mode ...
    } else {
        // ... the tabs of the page ...
    }
}
```

### Using with Page Navigation (Using Scaffold)

#### Using NavigationBar

```kotlin
val pages = listOf("Home", "Profile", "Settings")
val icons = listOf(COUIIcons.VerticalSplit, COUIIcons.Contacts, COUIIcons.Settings)
var selectedIndex by remember { mutableStateOf(0) }

Scaffold(
    bottomBar = {
        NavigationBar {
            pages.forEachIndexed { index, label ->
                NavigationBarItem(
                    selected = selectedIndex == index,
                    onClick = { selectedIndex = index },
                    icon = icons[index],
                    label = label
                )
            }
        }
    }
) { paddingValues ->
    // Content area needs to consider padding
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Current Page: ${pages[selectedIndex]}",
            style = COUITheme.textStyles.displayS
        )
    }
}
```
