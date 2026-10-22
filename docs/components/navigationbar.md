# NavigationBar

`NavigationBar` is a bottom navigation bar component in COUI, used to create navigation menus fixed at the bottom of applications. It supports 2 to 5 navigation items, offering different display modes (icon only, text only, icon and text, icon with selected label).

`FloatingNavigationBar` is the COUI 17 floating bottom navigation bar: a 56dp capsule of 2 to 5 items, each a 24dp icon over a 10dp label, with the bottom-bar material, whole-capsule touch motion and slide-to-select.

These components are typically used in conjunction with the `Scaffold` component to maintain consistent layout and behavior across different pages in the application.

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=navigationBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.NavigationBar
import io.github.suqi8.coui.kmp.basic.NavigationBarItem
import io.github.suqi8.coui.kmp.basic.FloatingNavigationBar
import io.github.suqi8.coui.kmp.basic.FloatingNavigationBarItem
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

### FloatingNavigationBar

The FloatingNavigationBar component can be used to create floating navigation menus at the bottom:

```kotlin
var selectedIndex by remember { mutableStateOf(0) }
val items = listOf("Home", "Profile", "Settings")
val icons = listOf(COUIIcons.VerticalSplit, COUIIcons.Contacts, COUIIcons.Settings)

Scaffold(
    bottomBar = {
        FloatingNavigationBar {
            items.forEachIndexed { index, label ->
                FloatingNavigationBarItem(
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

`NavigationBarItem` follows the COUI tab navigation state model: the selected item shows the icon and label at full label color, unselected items are dimmed, and the icon cross-fades between the two states over 180ms while the label color switches instantly. Labels always use medium font weight; pressing an unselected item previews the selected icon color. `FloatingNavigationBarItem` switches its icon and label instantly between the selected color (`#E6000000`) and the unselected color (`#B8000000`); there is no indicator, press mask or haptic feedback.

### Slide to Select

In a `FloatingNavigationBar`, sliding a finger across the capsule selects the item under it as it moves, so the selection follows the finger. The item the slide ends on is committed through its `onClick` when the finger lifts, wherever it lifts; disabled items are skipped. A plain tap calls the tapped item's `onClick`. While the finger is down, the whole capsule takes the COUI 17 touch motion (press scale, rubber-band follow and drag deformation) and, with `materialEffect`, a touch spotlight.

## Properties

### NavigationBar Properties

| Property Name              | Type                     | Description                         | Default Value                         | Required |
| -------------------------- | ------------------------ | ----------------------------------- | ------------------------------------- | -------- |
| modifier                   | Modifier                 | Modifier applied to the nav bar     | Modifier                              | No       |
| color                      | Color                    | Background color of the nav bar (`?couiColorBar`) | NavigationBarDefaults.color() | No       |
| showDivider                | Boolean                  | Show top divider line or not        | true                                  | No       |
| defaultWindowInsetsPadding | Boolean                  | Apply default window insets padding | true                                  | No       |
| mode                       | NavigationBarDisplayMode    | Display mode for items              | NavigationBarDisplayMode.IconAndText     | No       |
| content                    | @Composable RowScope.()  | The content of the nav bar          | -                                     | Yes      |

### NavigationBarItem Properties

| Property Name | Type          | Description                      | Default Value | Required |
| ------------- | ------------- | -------------------------------- | ------------- | -------- |
| selected      | Boolean       | Whether the item is selected     | -             | Yes      |
| onClick       | () -> Unit    | Callback when the item is clicked| -             | Yes      |
| icon          | ImageVector   | Icon of the item                 | -             | Yes      |
| label         | String        | Label of the item                | -             | Yes      |
| modifier      | Modifier      | Modifier applied to the item     | Modifier      | No       |
| enabled       | Boolean       | Whether the item is enabled      | true          | No       |

### FloatingNavigationBar Properties

| Property Name              | Type                     | Description                             | Default Value                           | Required |
| -------------------------- | ------------------------ | --------------------------------------- | --------------------------------------- | -------- |
| modifier                   | Modifier                 | Modifier applied to the bar's full-width container | Modifier                     | No       |
| backgroundModifier         | Modifier                 | Applied inside the capsule clip, above `color` and below the items: the slot for the bottom-bar backdrop blur | Modifier | No |
| visible                    | Boolean                  | Whether the capsule is shown; changes run the COUI 17 show / hide springs, a hidden bar keeps its space but takes no touches | true | No |
| materialEffect             | Boolean                  | Draw the COUI 17 edge lighting, inner shadow, touch spotlight and hide blur | true   | No       |
| showDivider                | Boolean                  | Show a thin divider ring around the capsule (not part of COUI 17) | false        | No       |
| defaultWindowInsetsPadding | Boolean                  | Keep the capsule above the navigation and caption bars | true                    | No       |
| color                      | Color                    | Capsule fill; pass `Color.Transparent` when `backgroundModifier` paints a blur | FloatingNavigationBarDefaults.color() | No |
| cornerRadius               | Dp                       | Corner radius of the capsule; the default rounds it fully into the COUI 17 soft capsule, a smaller radius is drawn at `default_g2_weight` 2.45 | FloatingNavigationBarDefaults.CornerRadius | No       |
| horizontalAlignment        | Alignment.Horizontal     | Horizontal alignment of the capsule     | CenterHorizontally                      | No       |
| horizontalOutSidePadding   | Dp                       | Minimum distance between the capsule and the bar's sides | FloatingNavigationBarDefaults.HorizontalOutSidePadding | No       |
| shadowElevation            | Dp                       | Blur radius of the capsule's drop shadow; 0 disables it | FloatingNavigationBarDefaults.ShadowElevation          | No       |
| content                    | @Composable () -> Unit   | The content of the nav bar, `FloatingNavigationBarItem`s placed directly inside | -  | Yes      |

### FloatingNavigationBarItem Properties

| Property Name | Type          | Description                      | Default Value | Required |
| ------------- | ------------- | -------------------------------- | ------------- | -------- |
| selected      | Boolean       | Whether the item is selected     | -             | Yes      |
| onClick       | () -> Unit    | Callback when the item is clicked| -             | Yes      |
| icon          | ImageVector   | Icon of the item                 | -             | Yes      |
| label         | String        | Label of the item                | -             | Yes      |
| modifier      | Modifier      | Modifier applied to the item     | Modifier      | No       |
| enabled       | Boolean       | Whether the item is enabled      | true          | No       |
| colors        | FloatingNavigationBarColors | Content colors of the item | FloatingNavigationBarDefaults.itemColors() | No |

### NavigationBarDefaults Object

The NavigationBarDefaults object provides default values for NavigationBar and NavigationBarItem components.

#### Constants

| Constant Name          | Type     | Description                                  | Default Value |
| ---------------------- | -------- | -------------------------------------------- | ------------- |
| ItemHeight             | Dp       | Item cell height                             | 56.dp         |
| HorizontalPadding      | Dp       | Horizontal padding at both edges of the bar  | 12.dp         |
| ItemHorizontalPadding  | Dp       | Horizontal padding inside each item cell     | 2.dp          |
| IconSize               | Dp       | Icon size                                    | 24.dp         |
| IconTopPadding         | Dp       | Top padding for the icon                     | 9.dp          |
| LabelBottomPadding     | Dp       | Bottom padding for the label                 | 7.dp          |
| LabelFontSize          | Dp       | Label text size, in dp (does not follow the font scale) | 10.dp |
| TextFontSize           | Dp       | Text size (TextOnly mode), in dp             | 14.dp         |
| IconFadeDurationMillis | Int      | Icon cross-fade duration between states      | 180           |

#### Functions

| Function Name | Return Type | Description |
| ------------- | ----------- | ----------- |
| color()       | Color       | The bar background `?couiColorBar`: `#FAFAFA` light, `#1E1E1E` dark |

### FloatingNavigationBarDefaults Object

The FloatingNavigationBarDefaults object provides default values for FloatingNavigationBar and FloatingNavigationBarItem components.

#### Constants

| Constant Name            | Type  | Description                         | Default Value |
| ------------------------ | ----- | ----------------------------------- | ------------- |
| CornerRadius             | Dp    | Corner radius: `COUIRoundCorner.Full`, a capsule at any height (COUI 17 half-height soft capsule) | 72.dp         |
| HorizontalOutSidePadding | Dp    | Minimum side distance of the capsule (`coui_bottom_floating_navigation_bar_padding_horizontal`) | 16.dp |
| VerticalOutSidePadding   | Dp    | Space above and below the capsule (56dp centred in an 88dp container) | 16.dp |
| ShadowElevation          | Dp    | Drop shadow blur radius, after the COUI 17 caustic shadow (elevation 24dp, alpha 45) | 24.dp |
| Height                   | Dp    | Capsule height                      | 56.dp         |
| HorizontalPadding        | Dp    | Horizontal padding inside the capsule | 12.dp       |
| ItemSpacing              | Dp    | Spacing between items               | 8.dp          |
| ItemMaxWidth             | Dp    | Item width; items shrink equally when they do not fit | 70.dp |
| ItemHeight               | Dp    | Item height, centred in the capsule | 48.dp         |
| IconSize                 | Dp    | Icon size                           | 24.dp         |
| IconTopPadding           | Dp    | Distance from the item top to the icon | 4.dp       |
| LabelTopSpacing          | Dp    | Gap between icon and label          | 2.dp          |
| LabelTextSize            | Dp    | Label text size, in dp (does not follow the font scale) | 10.dp |
| HiddenScale              | Float | Capsule scale when hidden           | 0.8f          |
| HiddenBlurRadiusPx       | Float | Capsule blur radius when hidden, raw pixels | 30f   |

#### Functions

| Function Name | Return Type | Description |
| ------------- | ----------- | ----------- |
| color()       | Color       | The opaque fill, COUI's non-blur fallback `?couiColorBar`: `#FAFAFA` light, `#1E1E1E` dark |
| backdropSpec(isDark) | MaterialBackdropSpec | The COUI 17 bottom-bar material `TYPE_FRAMEWORK_BOTTOM_BAR`: 25px blur, then `#B3666666` COLOR_DODGE + `#99F5F5F5` LUMINOSITY (light) or `#80666666` OVERLAY + `#B3262626` LUMINOSITY (dark) |
| itemColors()  | FloatingNavigationBarColors | Item content colors: selected `#E6000000` / `#E6FFFFFF`, unselected `#B8000000` / `#B8FFFFFF`, disabled `#42000000` / `#4DFFFFFF` |

### FloatingNavigationBarColors Class

| Property Name   | Type  | Description                               |
| --------------- | ----- | ----------------------------------------- |
| selectedColor   | Color | Icon and label color of the selected item |
| unselectedColor | Color | Icon and label color of unselected items  |
| disabledColor   | Color | Icon and label color of disabled items    |

### NavigationBarDisplayMode Enum

| Value                 | Description                                  |
| --------------------- | -------------------------------------------- |
| IconAndText           | Show both icon and text                      |
| IconOnly              | Show icon only                               |
| TextOnly              | Show text only                               |
| IconWithSelectedLabel | Show icon always, show text only when selected|

The display mode set on `NavigationBar` is delivered to its items through the `LocalNavigationBarDisplayMode` composition local. `FloatingNavigationBar` has no `mode` parameter — its items always show an icon over a label.

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

### FloatingNavigationBar

#### Custom Color and Corner Radius

```kotlin
FloatingNavigationBar(
    color = COUITheme.colorScheme.primaryContainer,
    cornerRadius = 28.dp
) {
    // ... items ...
}
```

#### Custom Alignment and Padding

```kotlin
FloatingNavigationBar(
    horizontalAlignment = Alignment.Start, // Align to start
    horizontalOutSidePadding = 16.dp // Set outside padding
) {
    // ... items ...
}
```

#### Divider and Shadow

```kotlin
FloatingNavigationBar(
    showDivider = true, // Draw a thin divider ring around the bar
    shadowElevation = 0.dp // Disable the drop shadow
) {
    // ... items ...
}
```

#### Bottom-Bar Material Blur

COUI 17 fills the capsule with the framework bottom-bar material, a 25px backdrop blur with two blended color layers. coui-ui has no backdrop capture of its own, so hand the blur in through `backgroundModifier` (for example from a `MaterialBackdropProvider` built on coui-blur `couiViewBlur`) and make the fill transparent:

```kotlin
val isDark = COUITheme.colorScheme.background.luminance() < 0.5f
FloatingNavigationBar(
    color = Color.Transparent,
    backgroundModifier = backdropProvider.materialBackdrop(
        spec = FloatingNavigationBarDefaults.backdropSpec(isDark),
        shape = RoundedCornerShape(FloatingNavigationBarDefaults.CornerRadius),
    ),
) {
    // ... items ...
}
```

The backdrop must record the page content, not the bar: keep the bar outside the recorded layer (for example in the `Scaffold` `bottomBar`).

#### Show and Hide

```kotlin
FloatingNavigationBar(
    visible = !isScrollingDown, // Alpha, scale 0.8 and blur springs
) {
    // ... items ...
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
            style = COUITheme.textStyles.title1
        )
    }
}
```

#### Using FloatingNavigationBar

```kotlin
val pages = listOf("Home", "Profile", "Settings")
val icons = listOf(COUIIcons.VerticalSplit, COUIIcons.Contacts, COUIIcons.Settings)
var selectedIndex by remember { mutableStateOf(0) }

Scaffold(
    bottomBar = {
        FloatingNavigationBar {
            pages.forEachIndexed { index, label ->
                FloatingNavigationBarItem(
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
            style = COUITheme.textStyles.title1
        )
    }
}
```
