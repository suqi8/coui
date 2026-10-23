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
| material               | Boolean                   | Whether the COUI 17 material (edge lighting, backdrop blur) is drawn | true                                                       | No       |
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
| PressedScale      | Float | Deprecated, no longer read                                | 0.92f         |

#### Functions

| Function Name             | Description                                                                  |
| ------------------------- | ---------------------------------------------------------------------------- |
| disabledContainerColor()  | `couiColorDisable` over the disabled base color: #B2D9FF light, #3E6083 dark |
| shadowPenumbra(elevation) | Blur of the spot shadow for an elevation (about 12dp at 60dp)                |

## Behavior

- **Press**: the COUI 17 touch motion moves the whole button: a size-derived press scale (about 0.91 for 56dp) with an under-damped release, rubber-band drag-follow and drag deformation. A touch down plays the OPLUS 302 haptic.
- **Shadow**: the OPlus light-source shadow COUI requests. It is a soft glow in the button's own color at alpha 86/255. The blur equals the penumbra of a 2000dp light 10000dp up (about 12dp). The shadow is offset downwards by `elevation × (centerY + 800dp) / (10000dp − elevation)`, so a button lower in the window casts its shadow further down.
- **Material**: with `material = true` (the ColorOS 17 default), the `TYPE_FRAMEWORK_CIRCLE_2` edge lighting and inner shadow are drawn over the fill, and the press mask is off. With a `LocalMaterialBackdrop` provider, the fill is replaced by the `TYPE_FRAMEWORK_FAB` backdrop blur: 10px, the fill at alpha 229 (light) / 230 (dark), then a white OVERLAY. With `material = false`, the opaque fill shows the `couiColorPress` mask while pressed. The edge lighting needs runtime shaders (Android 13+ and every Skia target).
- **Disabled**: the fill turns to `disabledContainerColor`; touches, haptics and the touch motion are ignored, and the material switches to its disabled presets.

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
