# FloatingAccessoryNavigationBar

`FloatingAccessoryNavigationBar` reproduces the ColorOS 17 floating navigation bar with accessories (`COUIBottomFloatingAccessoryNavigationBar`): the [FloatingNavigationBar](./navigationbar) capsule with an optional 56dp circular accessory at its start and at its end, such as a create or search button. Hidden accessories tuck behind the capsule and slide out with the COUI 17 melt-ball springs.

<div style="position: relative; height: 220px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=floatingAccessoryNavigationBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.FloatingAccessoryNavigationBar
import io.github.suqi8.coui.kmp.basic.FloatingNavigationBarAccessory
import io.github.suqi8.coui.kmp.basic.FloatingNavigationBarItem
```

## Basic Usage

```kotlin
var selected by remember { mutableIntStateOf(0) }
var searchShown by remember { mutableStateOf(true) }

FloatingAccessoryNavigationBar(
    endAccessoryVisible = searchShown,
    startAccessory = {
        FloatingNavigationBarAccessory(
            onClick = { /* Create */ },
            icon = COUIIcons.Add,
            contentDescription = "Create",
        )
    },
    endAccessory = {
        FloatingNavigationBarAccessory(
            onClick = { /* Search */ },
            icon = COUIIcons.Search,
            contentDescription = "Search",
        )
    },
) {
    items.forEachIndexed { index, item ->
        FloatingNavigationBarItem(
            selected = selected == index,
            onClick = { selected = index },
            icon = item.icon,
            label = item.label,
        )
    }
}
```

The items behave exactly as in `FloatingNavigationBar`: slide across the capsule to select, large icons, red dots and the item count springs.

## Changing the Accessory Icon

Pass a different `icon` to `FloatingNavigationBarAccessory` and the old icon scales and fades out while the new one scales and fades in (`setIconDrawable(_, true)`):

```kotlin
FloatingNavigationBarAccessory(
    onClick = { searching = !searching },
    icon = if (searching) COUIIcons.Close else COUIIcons.Search,
    contentDescription = if (searching) "Close" else "Search",
)
```

## Material Backdrop

Like the capsule, the accessory draws the COUI 17 `TYPE_FRAMEWORK_BOTTOM_BAR` backdrop blur when a `LocalMaterialBackdrop` provider is present (see [Button](./button)); otherwise it keeps the opaque `?couiColorBar` fill.

## Properties

### FloatingAccessoryNavigationBar

| Property                   | Type                      | Description                                                    | Default Value                                       | Required |
| -------------------------- | ------------------------- | -------------------------------------------------------------- | --------------------------------------------------- | -------- |
| modifier                   | Modifier                  | Modifier applied to the bar's container                        | Modifier                                            | No       |
| backgroundModifier         | Modifier                  | Applied inside the capsule, above the fill                     | Modifier                                            | No       |
| startAccessoryVisible      | Boolean                   | Whether the start accessory is shown                           | true                                                | No       |
| endAccessoryVisible        | Boolean                   | Whether the end accessory is shown                             | true                                                | No       |
| materialEffect             | Boolean                   | Whether the COUI 17 material and metaball merge are drawn      | `LocalCOUIMaterialEffectEnabled.current` | No       |
| showDivider                | Boolean                   | Whether to draw a divider ring around the capsule              | false                                               | No       |
| defaultWindowInsetsPadding | Boolean                   | Whether to keep the bar above the navigation and caption bars  | true                                                | No       |
| color                      | Color                     | Capsule fill without a backdrop                                | FloatingNavigationBarDefaults.color()               | No       |
| cornerRadius               | Dp                        | Capsule corner radius                                          | FloatingNavigationBarDefaults.CornerRadius          | No       |
| horizontalOutSidePadding   | Dp                        | Side padding on compact screens                                | FloatingNavigationBarDefaults.HorizontalOutSidePadding | No    |
| shadowElevation            | Dp                        | Blur radius of the capsule shadow                              | FloatingNavigationBarDefaults.ShadowElevation       | No       |
| startAccessory             | (@Composable () -> Unit)? | View at the capsule's start; null reserves no slot             | null                                                | No       |
| endAccessory               | (@Composable () -> Unit)? | View at the capsule's end; null reserves no slot               | null                                                | No       |
| content                    | @Composable () -> Unit    | The FloatingNavigationBarItems                                 | -                                                   | Yes      |

### FloatingNavigationBarAccessory

| Property           | Type         | Description                                           | Default Value                                   | Required |
| ------------------ | ------------ | ----------------------------------------------------- | ----------------------------------------------- | -------- |
| onClick            | () -> Unit   | Called when the accessory is clicked                  | -                                               | Yes      |
| icon               | ImageVector  | Icon drawn at its own size in the centre              | -                                               | Yes      |
| contentDescription | String?      | Accessibility label                                   | -                                               | Yes      |
| modifier           | Modifier     | Modifier applied to the accessory                     | Modifier                                        | No       |
| enabled            | Boolean      | Whether the accessory reacts to touches               | true                                            | No       |
| materialEffect     | Boolean      | Whether the material, spotlight and full icon change are drawn | `LocalCOUIMaterialEffectEnabled.current` | No       |
| color              | Color        | Fill without a backdrop                               | FloatingNavigationBarDefaults.color()           | No       |
| iconTint           | Color        | Tint of the icon; Unspecified leaves it untinted      | FloatingAccessoryNavigationBarDefaults.iconTint() | No     |
| shadowElevation    | Dp           | Blur radius of the drop shadow                        | FloatingNavigationBarDefaults.ShadowElevation   | No       |

### FloatingAccessoryNavigationBarDefaults

| Constant      | Type | Default Value | COUI Source                                                   |
| ------------- | ---- | ------------- | ------------------------------------------------------------- |
| AccessorySize | Dp   | 56.dp         | `mAccessorySize` = coui_bottom_floating_navigation_bar_height |

| Method     | Description                                                                             |
| ---------- | --------------------------------------------------------------------------------------- |
| iconTint() | `labelPrimary` (`?couiColorLabelPrimary`); COUI draws the drawable as supplied     |

## Behavior

- Every accessory that is set reserves its 56dp slot and the 12dp gap whether it is shown or not, so showing or hiding one never moves the capsule.
- On screens narrower than 600dp the bar keeps a 16dp padding on both sides, the start accessory hugs the start edge, the end accessory the end edge, and the capsule is centred between them. On wider screens there is no side padding and the accessories and capsule are centred as a group.
- A hidden accessory sits inside the capsule's start or end edge. Showing slides it out and scales it 0.5 -> 1 on springs of response 0.55 and bounce 0.35; its alpha follows how much of it the capsule still covers, and the two shapes merge like metaballs while they touch. A settled hidden accessory takes no touches.
- The accessory has the capsule's touch motion (press scale, rubber-band drag, deformation), its edge light and caustic shadow, and the `TYPE_BOTTOM_NAVIGATION` touch spotlight; there is no press mask.
