# FloatingToolbar

`FloatingToolbar` is the COUI 17 floating toolbar (`COUIBottomFloatingToolbar`). It comes in two forms:

- **Item toolbar** (`FloatingToolbar(items = ...)`): the COUI toolbar itself, a full-width bar at least 76dp high holding up to three capsules, one per item group, with icon or title items, red dots and an overflow menu.
- **Content capsule** (`FloatingToolbar { ... }`): a single capsule around free content, arranged horizontally or vertically (COUI's custom-view items). The actual placement on the screen is handled by the parent component, typically `Scaffold`.

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=floatingToolbar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.FloatingToolbar
import io.github.suqi8.coui.kmp.basic.FloatingToolbarDefaults
import io.github.suqi8.coui.kmp.basic.FloatingToolbarGroupArrangement
import io.github.suqi8.coui.kmp.basic.FloatingToolbarItem
import io.github.suqi8.coui.kmp.basic.ToolbarPosition // Used for Scaffold
```

## Item Toolbar

```kotlin
Box(Modifier.fillMaxSize()) {
    FloatingToolbar(
        items = listOf(
            FloatingToolbarItem(onClick = { /* Edit */ }, icon = COUIIcons.Edit, title = "Edit"),
            FloatingToolbarItem(onClick = { /* Delete */ }, icon = COUIIcons.Delete, title = "Delete", showRedDot = true),
            FloatingToolbarItem(onClick = { /* Done */ }, title = "Done", group = 1),
        ),
        modifier = Modifier.align(Alignment.BottomCenter),
    )
}
```

- **Layout**: 16dp from the sides below 600dp, 24dp below 840dp, 40dp from there; the capsules are centred in at least 76dp. Two groups sit at the edges (`Spread`) or side by side 16dp apart (`Packed`); a third sits in the middle.
- **Capsules**: 44dp high, items 4dp apart with 6dp of capsule beside the first and last (none around a single item). Icon items are 44 x 40dp with a 24dp icon; an item without an icon shows its title (16sp medium, 52-200dp wide, 16dp side padding).
- **Overflow**: a group shows at most six items. When more follow, or the items do not fit, the last place becomes a "more" item that opens a popup listing the rest, 8dp above the capsule.
- **Red dot**: a 6dp dot whose centre sits 12dp in from the item's top end corner; disabled items hide it.
- **Touch**: each capsule takes the COUI press motion and touch spotlight as a whole, whatever its items' states; items have no press mask of their own. A lone item is drawn on a circle by the material.
- **Item changes** (`ItemViewAnimatorImpl`): when the items a capsule shows change (added, removed, or moved into or out of the overflow item), the gaps first breathe for 200ms (closing up by 75% before the capsule grows, opening up by 75% before it shrinks), then the arriving items scale and fade in about their centres while the leaving ones scale and fade out (response 0.5 / bounce 0.4) and the capsule's width follows them. A change undone while it runs plays backwards. Give items a `key` when their icon, title and description do not tell them apart.
- **Groups**: a group that appears scales in (response 0.5 / bounce 0.4), one that goes away scales out where it stood (response 0.25 / bounce 0), and the capsules that have to move spring to their new places (response 0.5 / bounce 0.4). A change of the toolbar's width applies at once.

```kotlin
var editing by remember { mutableStateOf(false) }
FloatingToolbar(
    items = buildList {
        add(FloatingToolbarItem(onClick = { /* Share */ }, icon = COUIIcons.Share, title = "Share", key = "share"))
        if (editing) add(FloatingToolbarItem(onClick = { /* Delete */ }, icon = COUIIcons.Delete, title = "Delete", key = "delete"))
        add(FloatingToolbarItem(onClick = { editing = !editing }, title = if (editing) "Done" else "Edit", group = 1, key = "mode"))
    },
)
```

## Content Capsule

```kotlin
Scaffold(
    floatingToolbar = {
        FloatingToolbar {
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) { // or Column
                IconButton(onClick = { /* Action 1 */ }) {
                    Icon(COUIIcons.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = { /* Action 2 */ }) {
                    Icon(COUIIcons.Delete, contentDescription = "Delete")
                }
            }
        }
    },
    floatingToolbarPosition = ToolbarPosition.BottomCenter // Set the position
)
```

## Properties

### FloatingToolbar (Item Toolbar) Properties

| Property Name              | Type                            | Description                                              | Default Value                                      | Required |
| -------------------------- | ------------------------------- | -------------------------------------------------------- | -------------------------------------------------- | -------- |
| items                      | List\<FloatingToolbarItem>       | The items; the same `group` forms one capsule (at most three groups) | -                                      | Yes      |
| modifier                   | Modifier                        | Modifier applied to the full-width toolbar               | Modifier                                           | No       |
| visible                    | Boolean                         | Whether the capsules are shown (scale springs)           | true                                               | No       |
| materialEffect             | Boolean                         | Draw the COUI 17 bottom-bar material on the capsules     | `LocalCOUIMaterialEffectEnabled.current` | No       |
| defaultWindowInsetsPadding | Boolean                         | Keep the capsules above the navigation and caption bars  | true                                               | No       |
| groupArrangement           | FloatingToolbarGroupArrangement | `Spread` (edges) or `Packed` (centred together)          | FloatingToolbarGroupArrangement.Spread             | No       |
| color                      | Color                           | Capsule fill without the material blur                   | FloatingToolbarDefaults.defaultColor()             | No       |
| shadowElevation            | Dp                              | Blur radius of the capsules' shadow; 0 disables it       | FloatingToolbarDefaults.ShadowElevation            | No       |
| colors                     | FloatingToolbarItemColors       | Item content colors                                      | FloatingToolbarDefaults.itemColors()               | No       |
| overflowContentDescription | String                          | Accessibility label of the overflow item                 | FloatingToolbarDefaults.OverflowContentDescription | No       |

### FloatingToolbarItem

| Property Name      | Type         | Description                                                                 | Default Value |
| ------------------ | ------------ | --------------------------------------------------------------------------- | ------------- |
| onClick            | () -> Unit   | Called when the item is tapped, in the toolbar or in the overflow menu      | -             |
| icon               | ImageVector? | The icon; without one the title is shown                                    | null          |
| title              | String       | The title text, the overflow-menu row and the fallback accessibility label | ""            |
| contentDescription | String?      | The accessibility label                                                     | null          |
| group              | Int          | The group (capsule) of the item, ordered by value                           | 0             |
| enabled            | Boolean      | Whether the item is enabled                                                 | true          |
| showRedDot         | Boolean      | Whether a red dot is shown                                                  | false         |
| key                | Any?         | The identity the toolbar follows to animate items that come and go (`setId`), unique within the group; null uses the icon, title and description | null |

### FloatingToolbar (Content Capsule) Properties

| Property Name              | Type                   | Description                            | Default Value                          | Required |
| -------------------------- | ---------------------- | -------------------------------------- | -------------------------------------- | -------- |
| modifier                   | Modifier               | Modifier applied to the toolbar        | Modifier                               | No       |
| visible                    | Boolean                | Whether the toolbar is shown; changes run the COUI 17 scale-in / scale-out springs | true | No       |
| materialEffect             | Boolean                | Use the COUI 17 bottom-bar material: with a `LocalMaterialBackdrop` provider the backdrop blur replaces `color` | `LocalCOUIMaterialEffectEnabled.current` | No       |
| color                      | Color                  | Background color of the toolbar        | FloatingToolbarDefaults.defaultColor() | No       |
| cornerRadius               | Dp                     | Corner radius of the toolbar           | FloatingToolbarDefaults.CornerRadius   | No       |
| outSidePadding             | PaddingValues          | Padding outside the toolbar            | FloatingToolbarDefaults.OutSidePadding | No       |
| shadowElevation            | Dp                     | Blur radius of the caustic drop shadow; 0 disables it | FloatingToolbarDefaults.ShadowElevation | No |
| showDivider                | Boolean                | Show divider line around the toolbar   | false                                  | No       |
| content                    | @Composable () -> Unit | Composable content area of the toolbar | -                                      | Yes      |

### FloatingToolbarDefaults Object

| Property Name  | Type                    | Description              | Value                                   |
| -------------- | ----------------------- | ------------------------ | --------------------------------------- |
| CornerRadius   | Dp                      | Default corner radius (half the 44dp height, drawn as the weight-1.0 soft capsule; a radius below half the height takes `default_g2_weight` 2.45) | 22.dp                                   |
| defaultColor() | @Composable () -> Color | Default background color: COUI's non-blur fallback `?couiColorBar` | FloatingNavigationBarDefaults.color() |
| ShadowElevation | Dp                     | Caustic shadow blur radius (`SHADOW_CAUSTIC_DOWN`, alpha 45) | 24.dp            |
| backdropSpec(isDark) | MaterialBackdropSpec | The COUI 17 bottom-bar material `TYPE_FRAMEWORK_BOTTOM_BAR`, the same as `FloatingNavigationBarDefaults.backdropSpec` | 25px blur + two blended layers |
| OutSidePadding | PaddingValues           | Default outside padding of the content capsule (16dp side margin; 76dp in all around a 44dp capsule) | PaddingValues(horizontal = 16.dp, vertical = 16.dp) |
| MinHeight      | Dp                      | Least height of the item toolbar | 76.dp                           |
| HorizontalOutSidePaddingCompact / Medium / Expanded | Dp | Side margin below 600dp / below 840dp / from 840dp | 16.dp / 24.dp / 40.dp |
| horizontalOutSidePadding(width) | Dp     | The side margin for a toolbar width | -                            |
| MinGroupSpacing | Dp                     | Least gap between groups | 16.dp                                   |
| CapsuleHeight  | Dp                      | Capsule height           | 44.dp                                   |
| CapsuleInnerPadding | Dp                 | Capsule beside the first and last item (more than one item) | 6.dp   |
| ItemSpacing    | Dp                      | Gap between items        | 4.dp                                    |
| ItemMinWidth / ItemHeight | Dp           | Icon item size           | 44.dp / 40.dp                           |
| TitleMinWidth / TitleMaxWidth | Dp       | Title item width range   | 52.dp / 200.dp                          |
| TitleFontSize  | TextUnit                | Title text size          | 16.sp                                   |
| RedDotDiameter / RedDotOffset | Dp       | Red dot size; inset of its centre from the top end corner | 6.dp / 12.dp |
| MaxVisibleItems | Int                    | Items a group shows before the overflow item | 6                   |
| itemColors()   | FloatingToolbarItemColors | `?couiColorLabelPrimary` / `?couiColorLabelTertiary` | -              |

### ToolbarPosition (for Scaffold)

Please refer to the `ToolbarPosition` options in the [Scaffold](../components/scaffold#toolbarposition-options) documentation.

## Advanced Usage

### Custom Styles

```kotlin
FloatingToolbar(
    color = COUITheme.colorScheme.containerTheme,
    cornerRadius = 16.dp,
    outSidePadding = PaddingValues(24.dp),
    showDivider = false
) {
    Row(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) { // or Column
        IconButton(onClick = { /* Action 1 */ }) {
            Icon(COUIIcons.Edit, contentDescription = "Edit", tint = COUITheme.colorScheme.labelOnColor)
        }
        IconButton(onClick = { /* Action 2 */ }) {
            Icon(COUIIcons.Delete, contentDescription = "Delete", tint = COUITheme.colorScheme.labelOnColor)
        }
    }
}
```

### Toolbar with Divider

Set `showDivider = true` to draw a hairline divider ring around the toolbar; set `shadowElevation = 0.dp` to disable the drop shadow:

```kotlin
FloatingToolbar(
    showDivider = true,
    shadowElevation = 0.dp
) {
    Row(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = { /* Action 1 */ }) {
            Icon(COUIIcons.Edit, contentDescription = "Edit")
        }
        IconButton(onClick = { /* Action 2 */ }) {
            Icon(COUIIcons.Delete, contentDescription = "Delete")
        }
    }
}
```

### Vertically Arranged Content

```kotlin
FloatingToolbar {
    Column(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = { /* Action 1 */ }) {
            Icon(COUIIcons.Edit, contentDescription = "Edit")
        }
        IconButton(onClick = { /* Action 2 */ }) {
            Icon(COUIIcons.Delete, contentDescription = "Delete")
        }
    }
}
```

### Changing Position with Scaffold

```kotlin
Scaffold(
    floatingToolbar = {
        FloatingToolbar {
            // toolbar content
        }
    },
    floatingToolbarPosition = ToolbarPosition.CenterEnd // Place centered on the right edge
)
```

### Showing and Hiding

Toggle `visible` instead of wrapping the toolbar in `AnimatedVisibility`. The toolbar scales in about its centre (COUI 17 `COUIBottomFloatingToolbar` spring, response 0.5 / bounce 0.4) and scales out to nothing (response 0.25 / bounce 0) with its shadow intact; a fade from `AnimatedVisibility` would cut the shadow off while it runs. A hidden toolbar keeps its place in the layout but takes no touches.

```kotlin
var showToolbar by remember { mutableStateOf(true) }

FloatingToolbar(visible = showToolbar) {
    // toolbar content
}
```

### Bottom-Bar Material Blur

COUI 17 draws the toolbar with the framework bottom-bar material, a 25px backdrop blur with two blended color layers, over a transparent background. coui-ui has no backdrop capture of its own: provide a `MaterialBackdropProvider` through `LocalMaterialBackdrop`, and with `materialEffect` on the toolbar draws `FloatingToolbarDefaults.backdropSpec` in place of `color`. Without a provider it keeps `color`, COUI's non-blur fallback. The backdrop must record the page content, not the toolbar: keep the toolbar outside the recorded layer (for example in the `Scaffold` `floatingToolbar`).

```kotlin
CompositionLocalProvider(LocalMaterialBackdrop provides backdropProvider) {
    FloatingToolbar {
        // toolbar content
    }
}
```
