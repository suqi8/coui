# FloatingNavigationBar

`FloatingNavigationBar` is the COUI 17 floating bottom navigation bar: a 56dp capsule of 2 to 5 items, each a 24dp icon over a 10dp label, with the bottom-bar material, whole-capsule touch motion and slide-to-select.

See [NavigationBar](./navigationbar) for the classic bottom bar and [FloatingAccessoryNavigationBar](./floatingaccessorynavigationbar) for the variant with accessory buttons.

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=navigationBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.FloatingNavigationBar
import io.github.suqi8.coui.kmp.basic.FloatingNavigationBarDefaults
import io.github.suqi8.coui.kmp.basic.FloatingNavigationBarItem
```

## Basic Usage

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

`FloatingNavigationBarItem` switches its icon and label instantly between the selected color (`#E6000000`) and the unselected color (`#B8000000`); there is no indicator, press mask or haptic feedback. A pointer hovering a normal item, or keyboard focus on it, fades in COUI's hover / focus mask behind the icon and the label: a pill reaching 8dp past the item's sides (`COUIMaskRippleDrawable`, `?couiColorHover` / `?couiColorFocus`, each on a response 0.3 spring); the large icon item has none. The selected item offers no click action to accessibility services, as in `setupItemAccessibility`.

### Item Changes and Labels

When the items stop fitting or their count changes, the capsule width and position follow on springs (response 0.55, bounce 0.35) and the items spread with the capsule. On a count change every existing item starts from where it was drawn and eases onto the new spread on the same spring (COUI rebuilds its item views, which makes them jump), and a new item appears in its slot. Each label cross-fades between its layout at the old and the new item width (shrinking: old out at response 0.2, new in at 0.3; growing the other way round). Labels sit where an Android `TextView` with `includeFontPadding` puts them: 30dp below the item top plus Roboto's font padding (0.128em).

### Expansion View

With `expansion` and `expansionVisible`, the expansion view slides out from behind the capsule's end on a spring (response 0.55, bounce 0.35) while it scales 0.5 -> 1 and the capsule shrinks to make room. Its alpha follows how much of it the capsule still covers (opaque up to half covered, then fading linearly), and with `materialEffect` the two shapes merge like metaballs while they touch. Hiding reverses it, and the slide stops at its target as soon as the capsule covers the view entirely, so the spring's bounce cannot show it again (`finishCollapseTranslationIfFullyOverlapped`); a settled hidden expansion view is not placed and takes no touches. A `FloatingNavigationBarAccessory` used as the expansion view takes its touch motion only once it has slid out (`setExpansionViewTouchMotionAnimEnabled`).

### Slide to Select

In a `FloatingNavigationBar`, sliding a finger across the capsule selects the item under it as it moves, so the selection follows the finger. The item the slide ends on is committed through its `onClick` when the finger lifts, wherever it lifts; disabled items are skipped. A plain tap calls the tapped item's `onClick`. When a scrolling parent takes the gesture over, the tap is dropped but an item the slide has already selected is still committed (`dispatchPendingSlideItemClickIfNeeded`). While the finger is down, the whole capsule takes the COUI 17 touch motion (press scale, rubber-band follow and drag deformation) and, with `materialEffect`, a touch spotlight; the touch motion pauses while the capsule slides sideways (`doHorizontalMoveAnim`).

## Properties

### FloatingNavigationBar Properties

| Property Name              | Type                     | Description                             | Default Value                           | Required |
| -------------------------- | ------------------------ | --------------------------------------- | --------------------------------------- | -------- |
| modifier                   | Modifier                 | Modifier applied to the bar's full-width container | Modifier                     | No       |
| backgroundModifier         | Modifier                 | Applied inside the capsule clip, above `color` and below the items: the slot for a custom background | Modifier | No |
| visible                    | Boolean                  | Whether the capsule is shown; changes run the COUI 17 show / hide springs, a hidden bar keeps its space but takes no touches | true | No |
| expansionVisible           | Boolean                  | Whether `expansion` is shown beside the capsule | true                           | No       |
| materialEffect             | Boolean                  | Draw the COUI 17 edge lighting, inner shadow, touch spotlight, hide blur, the metaball merge with `expansion` and, with a `LocalMaterialBackdrop` provider, the backdrop blur | `LocalCOUIMaterialEffectEnabled.current` | No       |
| showDivider                | Boolean                  | Show a thin divider ring around the capsule (not part of COUI 17) | false        | No       |
| defaultWindowInsetsPadding | Boolean                  | Keep the capsule above the navigation and caption bars | true                    | No       |
| color                      | Color                    | Capsule fill; replaced by the backdrop blur when a `LocalMaterialBackdrop` provider is present | FloatingNavigationBarDefaults.color() | No |
| cornerRadius               | Dp                       | Corner radius of the capsule; the default rounds it fully into the COUI 17 soft capsule, a smaller radius is drawn at `default_g2_weight` 2.45 | FloatingNavigationBarDefaults.CornerRadius | No       |
| horizontalAlignment        | Alignment.Horizontal     | Horizontal alignment of the capsule while no `expansion` is shown; with it, COUI's placement applies (compact: capsule at the start, expansion at the end; wide: the pair centred) | CenterHorizontally | No |
| horizontalOutSidePadding   | Dp                       | Minimum distance between the capsule and the bar's sides | FloatingNavigationBarDefaults.HorizontalOutSidePadding | No       |
| shadowElevation            | Dp                       | Blur radius of the capsule's drop shadow; 0 disables it | FloatingNavigationBarDefaults.ShadowElevation          | No       |
| expansion                  | (@Composable () -> Unit)? | Optional view beside the capsule (for example a 56dp search button), centred on a 56dp slot 12dp from the capsule | null | No |
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
| largeIcon     | Boolean       | Use the 40dp large icon that rises 14dp above the item (10dp above the capsule); taps on it select the item | false | No |
| badge         | Int?          | Red dot on the icon: `null` none, `0` a 6dp dot at the icon's top end corner, a positive number a number badge 4dp above the icon whose end edge lies 6 / 9 / 10dp past the icon for 1 / 2 / 3 digits. Hidden on disabled items | null | No |
| colors        | FloatingNavigationBarColors | Content colors of the item | FloatingNavigationBarDefaults.itemColors() | No |

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
| ItemMaskHorizontalOutset | Dp    | How far an item's hover / focus mask reaches past its sides | 8.dp |
| IconSize                 | Dp    | Icon size                           | 24.dp         |
| IconTopPadding           | Dp    | Distance from the item top to the icon | 4.dp       |
| LabelTopSpacing          | Dp    | Gap between icon and label          | 2.dp          |
| LabelTextSize            | Dp    | Label text size, in dp (does not follow the font scale) | 10.dp |
| HiddenScale              | Float | Capsule scale when hidden           | 0.8f          |
| HiddenBlurRadiusPx       | Float | Capsule blur radius when hidden, raw pixels | 30f   |
| LargeIconSize            | Dp    | Large icon size                     | 40.dp         |
| LargeIconOverflow        | Dp    | How far the large icon rises above its item | 14.dp |
| BadgeTopOverflow         | Dp    | How far a number badge rises above the icon | 4.dp  |
| BadgeEndOverflowOneDigit / TwoDigits / ThreeDigits | Dp | How far a number badge's end lies past the icon | 6.dp / 9.dp / 10.dp |
| ExpansionSlotSize        | Dp    | Slot the expansion view is centred on | 56.dp       |
| ExpansionSpacing         | Dp    | Gap between the capsule and the expansion view | 12.dp |
| ExpansionHiddenScale     | Float | Expansion view scale when hidden    | 0.5f          |
| CompactWidthBreakpoint   | Dp    | Below this container width the compact expansion placement applies | 600.dp |

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

`FloatingNavigationBar` has no `mode` parameter — its items always show an icon over a label.

## Advanced Usage

### Custom Color and Corner Radius

```kotlin
FloatingNavigationBar(
    color = COUITheme.colorScheme.containerTheme,
    cornerRadius = 28.dp
) {
    // ... items ...
}
```

### Custom Alignment and Padding

```kotlin
FloatingNavigationBar(
    horizontalAlignment = Alignment.Start, // Align to start
    horizontalOutSidePadding = 16.dp // Set outside padding
) {
    // ... items ...
}
```

### Divider and Shadow

```kotlin
FloatingNavigationBar(
    showDivider = true, // Draw a thin divider ring around the bar
    shadowElevation = 0.dp // Disable the drop shadow
) {
    // ... items ...
}
```

### Bottom-Bar Material Blur

COUI 17 fills the capsule with the framework bottom-bar material, a 25px backdrop blur with two blended color layers. coui-ui has no backdrop capture of its own: provide a `MaterialBackdropProvider` (for example one built on coui-blur `couiViewBlur`) through `LocalMaterialBackdrop`, and with `materialEffect` on the capsule draws `FloatingNavigationBarDefaults.backdropSpec` in place of `color`:

```kotlin
CompositionLocalProvider(LocalMaterialBackdrop provides backdropProvider) {
    FloatingNavigationBar {
        // ... items ...
    }
}
```

The backdrop must record the page content, not the bar: keep the bar outside the recorded layer (for example in the `Scaffold` `bottomBar`).

### Expansion View, Large Icon and Badges

```kotlin
FloatingNavigationBar(
    expansionVisible = showSearch,
    expansion = { SearchButton(onClick = { showSearch = false }) },
) {
    FloatingNavigationBarItem(selected = page == 0, onClick = { page = 0 }, icon = COUIIcons.Photos, label = "Photos", badge = 0)
    FloatingNavigationBarItem(selected = page == 1, onClick = { page = 1 }, icon = COUIIcons.Add, label = "Create", largeIcon = true)
    FloatingNavigationBarItem(selected = page == 2, onClick = { page = 2 }, icon = COUIIcons.Contacts, label = "Me", badge = 8)
}
```

### Show and Hide

```kotlin
FloatingNavigationBar(
    visible = !isScrollingDown, // Alpha, scale 0.8 and blur springs
) {
    // ... items ...
}
```

### Using with Scaffold

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
            style = COUITheme.textStyles.displayS
        )
    }
}
```
