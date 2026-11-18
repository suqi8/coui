# FloatingActionButton

`FloatingActionButton` is a floating button component in COUI, typically used to display the most important or frequently used actions on a page. It usually floats above the interface with a prominent visual effect for quick user access.

This component is typically used in conjunction with the `Scaffold` component to maintain consistent layout and behavior across different pages in the application.

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=floatingActionButton" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.FloatingActionButton
```

## Basic Usage

The FloatingActionButton component can be used to trigger important actions:

```kotlin
FloatingActionButton(
    onClick = { /* Handle click event */ }
) {
    Icon(
        imageVector = COUIIcons.Add,
        contentDescription = "Add"
    )
}
```

## Component States

### Disabled State

```kotlin
FloatingActionButton(
    onClick = { /* Handle click event */ },
    enabled = false
) {
    Icon(
        imageVector = COUIIcons.Add,
        contentDescription = "Add"
    )
}
```

## Properties

### FloatingActionButton Properties

| Property Name          | Type                      | Description                                                          | Default Value                                              | Required |
| ---------------------- | ------------------------- | -------------------------------------------------------------------- | ---------------------------------------------------------- | -------- |
| onClick                | () -> Unit                | Callback triggered when clicked                                      | -                                                          | Yes      |
| modifier               | Modifier                  | Modifier applied to the button                                       | Modifier                                                   | No       |
| enabled                | Boolean                   | Whether the button is enabled                                        | true                                                       | No       |
| visible                | Boolean                   | Whether the button is shown; changes play the enlarge / shrink animation | true                                                   | No       |
| material               | Boolean                   | Whether the COUI 17 material (edge lighting, backdrop blur) is drawn | `LocalCOUIMaterialEffectEnabled.current` | No       |
| hapticFeedbackEnabled  | Boolean                   | Whether a touch down plays the press haptic (OPLUS 302)              | true                                                       | No       |
| shape                  | Shape                     | Shape of the button                                                  | CircleShape                                                | No       |
| containerColor         | Color                     | Background color of the button                                       | COUITheme.colorScheme.primary                              | No       |
| disabledContainerColor | Color                     | Background color of the disabled button                              | FloatingActionButtonDefaults.disabledContainerColor()      | No       |
| contentColor           | Color                     | `LocalContentColor` provided to the content                          | COUITheme.colorScheme.onPrimary                            | No       |
| shadowElevation        | Dp                        | OPlus shadow elevation; 0.dp removes the shadow                      | FloatingActionButtonDefaults.ShadowElevation               | No       |
| shadowColor            | Color                     | Color of the shadow glow                                             | containerColor at FloatingActionButtonDefaults.ShadowAlpha | No       |
| minWidth               | Dp                        | Minimum width of the button                                          | FloatingActionButtonDefaults.MinWidth                      | No       |
| minHeight              | Dp                        | Minimum height of the button                                         | FloatingActionButtonDefaults.MinHeight                     | No       |
| interactionSource      | MutableInteractionSource? | Interaction source of the button                                     | null                                                       | No       |
| content                | @Composable () -> Unit    | Composable content of the button                                     | -                                                          | Yes      |

### FloatingActionButtonDefaults Object

The FloatingActionButtonDefaults object provides default values for FloatingActionButton components.

#### Constants

| Constant Name     | Type  | Description                                               | Default Value |
| ----------------- | ----- | --------------------------------------------------------- | ------------- |
| MinWidth          | Dp    | Minimum width of the button                               | 56.dp         |
| MinHeight         | Dp    | Minimum height of the button                              | 56.dp         |
| ShadowElevation   | Dp    | OPlus shadow elevation (coui_float_btn_shadow_elevation)  | 60.dp         |
| ShadowAlpha       | Float | Alpha of the shadow color (coui_shadow_color_float_btn)   | 86f / 255f    |
| ShadowLightY      | Dp    | Y of the shadow light, above the window top               | -800.dp       |
| ShadowLightZ      | Dp    | Height of the shadow light                                | 10000.dp      |
| ShadowLightRadius | Dp    | Radius of the shadow light                                | 2000.dp       |
| ExpandedRotation  | Float | Main button content rotation of the expanded ExpandableFloatingActionButton | 45f |
| TinySize          | Dp    | Size of the tiny tier (`FloatingButton.Icon.Tiny`)        | 48.dp         |
| AutoShowDelayMillis | Long | Delay before a scroll-hidden button shows again (`DELAY_TIME_NO_ACTION_SLIDE_OUT`) | 5000L |

#### Functions

| Function Name             | Description                                                                  |
| ------------------------- | ---------------------------------------------------------------------------- |
| disabledContainerColor()  | `couiColorDisable` over the disabled base color: #B2D9FF light, #3E6083 dark |
| shadowPenumbra(elevation) | Blur of the spot shadow for an elevation (about 12dp at 60dp)                |
| secondaryContainerColor() | Fill of the secondary preset, `COUITheme.colorScheme.neutralButton` (`coui_button_neutral_color`) |
| secondaryDisabledContainerColor() | Disabled fill of the secondary preset, `COUITheme.colorScheme.pressBackground` (`couiColorPressBackground`) |
| translateContainerColor() | Fill of the translucent preset laid over images (#33FFFFFF)                  |

## Behavior

- **Press**: the COUI 17 touch motion moves the whole button: a size-derived press scale (about 0.91 for 56dp) with an under-damped release, rubber-band drag-follow and drag deformation. A touch down plays the OPLUS 302 haptic.
- **Shadow**: the OPlus light-source shadow COUI requests. It is a soft glow in the button's own color at alpha 86/255. The blur equals the penumbra of a 2000dp light 10000dp up (about 12dp). The shadow is offset downwards by `elevation × (centerY + 800dp) / (10000dp − elevation)`, so a button lower in the window casts its shadow further down.
- **Material**: with `material = true` (the ColorOS 17 default), the `TYPE_FRAMEWORK_CIRCLE_2` edge lighting and inner shadow are drawn over the fill, and the press mask is off. With a `LocalMaterialBackdrop` provider, the fill is replaced by the `TYPE_FRAMEWORK_FAB` backdrop blur: 10px, the fill at alpha 229 (light) / 230 (dark), then a white OVERLAY. With `material = false`, the opaque fill shows the `couiColorPress` mask while pressed. The edge lighting needs runtime shaders (Android 13+ and every Skia target).
- **Disabled**: the fill turns to `disabledContainerColor`; touches, haptics and the touch motion are ignored, and the material switches to its disabled presets.
- **Show / hide**: toggling `visible` plays `animationFloatingButtonEnlarge` / `animationFloatingButtonShrink` (alpha 0 ↔ 1, scale 0.6 ↔ 1 over 350ms on COUIMoveEaseInterpolator). A hidden button ignores touches and leaves the layout.

## Presets and Sizes

COUI 17 ships three color presets, each at 56dp or the 48dp tiny size (`FloatingButton.Icon.{Medium,Small,Tiny}.{Main,Secondary,Translate}`). Those styles also turn the shadow off (`fabNeedElevation = false`):

```kotlin
FloatingActionButton(
    onClick = { /* Handle click event */ },
    containerColor = FloatingActionButtonDefaults.secondaryContainerColor(),
    disabledContainerColor = FloatingActionButtonDefaults.secondaryDisabledContainerColor(),
    contentColor = COUITheme.colorScheme.labelPrimary,
    shadowElevation = 0.dp,
    minWidth = FloatingActionButtonDefaults.TinySize,
    minHeight = FloatingActionButtonDefaults.TinySize,
) {
    Icon(imageVector = COUIIcons.Add, contentDescription = "Add")
}
```

## Hide on Scroll

`rememberFloatingActionButtonScrollState()` mirrors `COUIFloatingButton.ScrollViewBehavior`: a scroll step of more than 10px towards the end hides the button, a step back towards the start shows it, and a hidden button shows itself again after 5 seconds (pass `autoShowDelayMillis = 0` to keep it hidden, `setAutoSlideInDisable`).

```kotlin
val fabScroll = rememberFloatingActionButtonScrollState()
Scaffold(
    floatingActionButton = {
        FloatingActionButton(onClick = {}, visible = fabScroll.visible) {
            Icon(imageVector = COUIIcons.Add, contentDescription = "Add")
        }
    },
) { padding ->
    LazyColumn(
        modifier = Modifier.nestedScroll(fabScroll.nestedScrollConnection),
        contentPadding = padding,
    ) { /* ... */ }
}
```

## Advanced Usage

### Custom Color

```kotlin
FloatingActionButton(
    onClick = { /* Handle click event */ },
    containerColor = Color.Red
) {
    Icon(
        imageVector = COUIIcons.Favorites,
        contentDescription = "Favorites",
        tint = Color.White
    )
}
```

### Extended Floating Action Button

```kotlin
FloatingActionButton(
    onClick = { /* Handle click event */ },
    shape = RoundedCornerShape(16.dp),
    minWidth = 120.dp
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Icon(
            imageVector = COUIIcons.Add,
            contentDescription = "Add",
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Add", color = Color.White)
    }
}
```

### Floating Action Button with Scaffold

```kotlin
Scaffold(
    floatingActionButton = {
        FloatingActionButton(
            onClick = { /* Handle click event */ }
        ) {
            Icon(
                imageVector = COUIIcons.Add,
                contentDescription = "Add"
            )
        }
    },
    floatingActionButtonPosition = FabPosition.End
) { paddingValues ->
    // Content area needs to consider padding
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // ...
    }
}
```

### Animated Floating Action Button

```kotlin
var expanded by remember { mutableStateOf(false) }
val animatedSize by animateDpAsState(
    targetValue = if (expanded) 65.dp else 60.dp,
    label = "FAB size animation",
)

FloatingActionButton(
    onClick = { expanded = !expanded },
    minWidth = animatedSize,
    minHeight = animatedSize
) {
    Icon(
        imageVector = if (expanded) COUIIcons.Remove else COUIIcons.Add,
        contentDescription = if (expanded) "Remove" else "Add",
        tint = Color.White
    )
}
```

## ExpandableFloatingActionButton

`ExpandableFloatingActionButton` is the COUI 17 speed dial (`COUIFloatingButton` with
`COUIFloatingButtonItem` children): clicking the main button expands it into a column of child
buttons, each with an optional label capsule before it.

```kotlin
import io.github.suqi8.coui.kmp.basic.ExpandableFloatingActionButton
import io.github.suqi8.coui.kmp.basic.FloatingActionButtonItem
```

```kotlin
var expanded by remember { mutableStateOf(false) }
val items = listOf(
    FloatingActionButtonItem(
        icon = { Icon(imageVector = COUIIcons.Edit, contentDescription = "Edit") },
        onClick = { expanded = false },
        label = "Edit",
    ),
    FloatingActionButtonItem(
        icon = { Icon(imageVector = COUIIcons.Share, contentDescription = "Share") },
        onClick = { expanded = false },
        label = "Share",
    ),
)

ExpandableFloatingActionButton(
    expanded = expanded,
    onExpandedChange = { expanded = it },
    items = items,
) {
    Icon(imageVector = COUIIcons.Add, contentDescription = "Add")
}
```

The component measures as the main button alone and places the items above it, outside its own
bounds: as the `Scaffold` floating action button the main button keeps its place with any
`FabPosition` and the snack bar does not move when the menu opens. Leave room above the button and
do not clip its parent there. As in COUI there is no scrim and an outside touch does not close the
menu; clicks on items that are sliding back into the main button are ignored.

### ExpandableFloatingActionButton Properties

| Property Name          | Type                           | Description                                                  | Default Value                                         | Required |
| ---------------------- | ------------------------------ | ------------------------------------------------------------ | ----------------------------------------------------- | -------- |
| expanded               | Boolean                        | Whether the items are shown                                  | -                                                     | Yes      |
| onExpandedChange       | (Boolean) -> Unit              | Called with the new state when the main button is clicked    | -                                                     | Yes      |
| items                  | List\<FloatingActionButtonItem> | Child actions, index 0 nearest to the main button            | -                                                     | Yes      |
| modifier               | Modifier                       | Modifier applied to the main button slot                     | Modifier                                              | No       |
| enabled                | Boolean                        | Whether the main button is enabled                           | true                                                  | No       |
| visible                | Boolean                        | Whether the main button is shown; hiding also collapses the menu | true                                              | No       |
| material               | Boolean                        | Whether the buttons and labels draw the COUI 17 material     | `LocalCOUIMaterialEffectEnabled.current` | No       |
| hapticFeedbackEnabled  | Boolean                        | Whether a touch down plays the press haptic                  | true                                                  | No       |
| containerColor         | Color                          | Fill of the main button                                      | COUITheme.colorScheme.primary                         | No       |
| disabledContainerColor | Color                          | Fill of the disabled main button                             | FloatingActionButtonDefaults.disabledContainerColor() | No       |
| contentColor           | Color                          | Content color of the main button                             | COUITheme.colorScheme.onPrimary                       | No       |
| rotation               | Float                          | Rotation of the main button content while expanded (degrees) | FloatingActionButtonDefaults.ExpandedRotation (45f)   | No       |
| content                | @Composable () -> Unit         | Content of the main button                                   | -                                                     | Yes      |

### FloatingActionButtonItem

| Property Name       | Type                   | Description                                                               | Default Value     |
| ------------------- | ---------------------- | ------------------------------------------------------------------------- | ----------------- |
| icon                | @Composable () -> Unit | Icon of the child button                                                  | -                 |
| onClick             | () -> Unit             | Called when the child button or its label is clicked; the menu stays open | -                 |
| label               | String?                | Text of the label capsule; null shows a bare child button                 | null              |
| enabled             | Boolean                | Whether the child button and label are enabled                            | true              |
| containerColor      | Color                  | Fill of the child button; Unspecified takes the theme primary             | Color.Unspecified |
| labelContainerColor | Color                  | Fill of the label capsule; Unspecified takes the theme primary            | Color.Unspecified |
| labelColor          | Color                  | Label text color; Unspecified takes white                                 | Color.Unspecified |

### Expand and Collapse Behavior

- **Layout**: index 0 sits 32dp above the main button, the next ones 16dp apart; every child button is 56dp with its label 12dp before it. Labels are 12sp, at most two lines and 220dp wide, padded 8dp x 4dp.
- **Expand**: the farthest item starts first, each next one 50ms later. An item springs up from the main button (stiffness 500, damping ratio 0.8) while the child button and label scale from 0.6; the child fades in over 300ms and the label over 350ms on `PathInterpolator(0.25, 0.1, 0.25, 1)`, the label growing from its top end corner.
- **Collapse**: the nearest item starts first, each next one 50ms later; it slides back into the main button over 300ms on the COUI move-ease curve while scaling to 0.6 and fading out (the label over 200ms, from its bottom end corner).
- **Main button**: its content rotates by 45 degrees over 300ms.
- **Fading shadows**: the fading child buttons and labels keep their shadows (their layers are grown past the shadow reach).
- **Material**: the main and child buttons draw the `FloatingActionButton` material; the labels draw the primary button material (`TYPE_CONTENT_PRIMARY_BUTTON` blur, `TYPE_CONTENT_CAPSULE_1` stroke).
