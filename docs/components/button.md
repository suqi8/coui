# Button

`Button` is a basic interactive component in COUI, used to trigger actions or events. It provides multiple style options, including primary buttons, secondary buttons, and text buttons.

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=button" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.Button
```

## Basic Usage

The Button component can be used to trigger actions or events:

```kotlin
Button(
    onClick = { /* Handle click event */ }
) {
    Text("Button")
}
```

## Button Types

COUI provides various types of buttons suitable for different scenarios and levels of importance:

### Primary Button

```kotlin
Button(
    onClick = { /* Handle click event */ },
    colors = ButtonDefaults.buttonColorsPrimary()
) {
    Text("Primary Button")
}
```

### Secondary Button

```kotlin
Button(
    onClick = { /* Handle click event */ },
    colors = ButtonDefaults.buttonColors()
) {
    Text("Secondary Button")
}
```

### Text Button

```kotlin
TextButton(
    text = "Text Button",
    onClick = { /* Handle click event */ }
)
```

### Transparent Button

Corresponds to COUI `Widget.COUI.Button.Small.ButtonNew.Translate`: a translucent white fill (`#33FFFFFF`) with a white label in both schemes, meant for buttons laid over images or colored backdrops.

```kotlin
TextButton(
    text = "Transparent Button",
    onClick = { /* Handle click event */ },
    colors = ButtonDefaults.textButtonColorsTransparent()
)
```

### Borderless Text Button

No fill, primary-tinted label.

```kotlin
TextButton(
    text = "Borderless Button",
    onClick = { /* Handle click event */ },
    colors = ButtonDefaults.textButtonColorsBorderless()
)
```

### Small Size Tier

Pass the `Small` metrics from `ButtonDefaults` together with `ButtonDefaults.textStyleSmall()` (14sp medium) to get the COUI small size tier (`Widget.COUI.Button.Small`):

```kotlin
TextButton(
    text = "Small Button",
    onClick = { /* Handle click event */ },
    cornerRadius = ButtonDefaults.CornerRadiusSmall,
    minWidth = ButtonDefaults.MinWidthSmall,
    minHeight = ButtonDefaults.MinHeightSmall,
    insideMargin = ButtonDefaults.InsideMarginSmall,
    textStyle = ButtonDefaults.textStyleSmall()
)
```

## Press Feedback

The press feedback follows COUI 17 `COUIButton` (`COUITouchMotionAnimator` + `COUIMaskEffectDrawable`):

- **Scale target** is derived from the measured size in dp (`PressCalc`): an area channel `0.85 + B(t) * 0.12` with `t` the area mapped from 20² to 150², and for buttons wider than twice their height an aspect-ratio channel `0.85 + B((ar - 2) / 13) * 0.15` averaged in (`B = cubic-bezier(0, 0, 0, 1)`). A 296 x 44dp button presses to ~0.966, 132 x 44dp to ~0.920, 52 x 28dp to ~0.886.
- **Springs**: buttons wider than twice their height use the candy pair (press k 438.65 / ζ 1, release k 157.91 / ζ 0.5); compact and round buttons use the jelly pair (press k 631.65 / ζ 1, release k 157.91 / ζ 0.4). The under-damped release gives the slight rebound.
- **Press tint**: `couiColorPress` (`#1F000000` light / `#33FFFFFF` dark) on a k 438.65 / ζ 1 spring, held until at least 70% visible.

## Material Layer

ColorOS 17 draws an extra material layer on buttons only when the system material switches are on; they are off by default, so `Button` draws none unless asked. Pass `material` to opt in to the edge lighting + inner shadow (`COUIMaterialStrokeEffect` `CONTENT_CAPSULE_1/2/3`) and the touch spotlight (`COUISpotLightEffect` `OPAQUE_MEDIUM_1/2`, `TRANSPARENT_MEDIUM_1`) matching the button type. Both need runtime shaders (Android 13+ and every Skia target) and are skipped elsewhere. The background blur that COUI uses in place of the solid fill is not drawn.

```kotlin
TextButton(
    text = "Material Button",
    onClick = { /* Handle click event */ },
    colors = ButtonDefaults.textButtonColorsPrimary(),
    material = ButtonMaterial.Primary
)
```

## Component States

### Disabled State

```kotlin
Button(
    onClick = { /* Handle click event */ },
    enabled = false
) {
    Text("Disabled Button")
}
```

## Properties

### Button Properties

| Property Name     | Type                            | Description                            | Default Value                 | Required |
| ----------------- | ------------------------------- | -------------------------------------- | ----------------------------- | -------- |
| onClick           | () -> Unit                      | Callback triggered on click            | -                             | Yes      |
| modifier          | Modifier                        | Modifier applied to the button         | Modifier                      | No       |
| enabled           | Boolean                         | Whether the button is clickable        | true                          | No       |
| pressScaleEnabled | Boolean                         | Whether the button shrinks while pressed (COUIButton `scaleEnable`). Set to `false` for buttons that fill a container cell, such as a dialog button bar, so the only press feedback is the full-cell press tint | true | No       |
| cornerRadius      | Dp                              | Corner radius of the button; `Dp.Unspecified` rounds it at half the measured height (COUI capsule), an explicit value draws a squircle | ButtonDefaults.CornerRadius   | No       |
| minWidth          | Dp                              | Minimum width of the button            | ButtonDefaults.MinWidth       | No       |
| minHeight         | Dp                              | Minimum height of the button           | ButtonDefaults.MinHeight      | No       |
| colors            | ButtonColors                    | Button color configuration             | ButtonDefaults.buttonColors() | No       |
| insideMargin      | PaddingValues                   | Internal padding of the button         | ButtonDefaults.InsideMargin   | No       |
| material          | ButtonMaterial                  | COUI 17 material layer (edge lighting, inner shadow, touch spotlight) | ButtonMaterial.None | No       |
| interactionSource | MutableInteractionSource?       | Interaction source for the button      | null                          | No       |
| indication        | Indication?                     | Indication for click interactions; `null` because the COUI press feedback (scale + press tint) is built in | null | No       |
| content           | @Composable RowScope.() -> Unit | Composable function for button content | -                             | Yes      |

### TextButton Properties

| Property Name     | Type                      | Description                       | Default Value                     | Required |
| ----------------- | ------------------------- | --------------------------------- | --------------------------------- | -------- |
| text              | String                    | Text displayed on the button      | -                                 | Yes      |
| onClick           | () -> Unit                | Callback triggered on click       | -                                 | Yes      |
| modifier          | Modifier                  | Modifier applied to the button    | Modifier                          | No       |
| enabled           | Boolean                   | Whether the button is clickable   | true                              | No       |
| pressScaleEnabled | Boolean                   | Whether the button shrinks while pressed (COUIButton `scaleEnable`). Set to `false` for buttons that fill a container cell, such as a dialog button bar | true | No       |
| colors            | TextButtonColors          | Text button color configuration   | ButtonDefaults.textButtonColors() | No       |
| cornerRadius      | Dp                        | Corner radius of the button; `Dp.Unspecified` is the height-derived capsule | ButtonDefaults.CornerRadius       | No       |
| minWidth          | Dp                        | Minimum width of the button       | ButtonDefaults.MinWidth           | No       |
| minHeight         | Dp                        | Minimum height of the button      | ButtonDefaults.MinHeight          | No       |
| insideMargin      | PaddingValues             | Internal padding of the button    | ButtonDefaults.InsideMargin       | No       |
| textStyle         | TextStyle                 | Text style of the label (pass `ButtonDefaults.textStyleSmall()` with the `Small` metrics for the COUI small size tier) | COUITheme.textStyles.button | No       |
| maxLines          | Int                       | Maximum number of label lines; COUIButton is single-line with an end ellipsis | 1 | No       |
| material          | ButtonMaterial            | COUI 17 material layer, see `Button` | ButtonMaterial.None | No       |
| interactionSource | MutableInteractionSource? | Interaction source for the button | null                              | No       |
| indication        | Indication?               | Indication for click interactions; `null` because the COUI press feedback (scale + press tint) is built in | null | No       |

### ButtonDefaults Object

The ButtonDefaults object provides default values and color configurations for button components.

#### Constants

| Constant Name | Type          | Description                    | Default Value        |
| ------------- | ------------- | ------------------------------ | -------------------- |
| PressedScale  | Float         | Legacy COUI 16 press end scale; retained for source compatibility only, no longer used (the COUI 17 target is size-derived) | 0.92f |
| PressedBrightness | Float     | Legacy COUIButton `brightness` value; retained for source compatibility only, no longer used | 0.8f |
| MinWidth      | Dp            | Minimum width of the button (COUI `coui_btn_large_width_min`) | 132.dp |
| MinHeight     | Dp            | Minimum height of the button   | 44.dp                |
| CornerRadius  | Dp            | Corner radius of the button; unspecified = capsule at half the measured height | Dp.Unspecified |
| InsideMargin  | PaddingValues | Internal padding of the button (COUI `Widget.COUI.Button.Large`) | PaddingValues(horizontal = 12.dp, vertical = 8.dp) |
| MinWidthSmall | Dp            | Minimum width of the small size tier (COUI Widget.COUI.Button.Small) | 52.dp |
| MinHeightSmall | Dp           | Minimum height of the small size tier | 28.dp |
| CornerRadiusSmall | Dp        | Corner radius of the small size tier (height-derived capsule) | Dp.Unspecified |
| InsideMarginSmall | PaddingValues | Internal padding of the small size tier | PaddingValues(horizontal = 12.dp, vertical = 4.dp) |

#### Methods

| Method Name               | Type             | Description                                            |
| ------------------------- | ---------------- | ------------------------------------------------------ |
| buttonColors()            | ButtonColors     | Creates color configuration for secondary buttons      |
| buttonColorsPrimary()     | ButtonColors     | Creates color configuration for primary buttons        |
| textButtonColors()        | TextButtonColors | Creates color configuration for secondary text buttons |
| textButtonColorsPrimary() | TextButtonColors | Creates color configuration for primary text buttons   |
| textStyleSmall()          | TextStyle        | Label style of the small size tier (14sp medium)       |
| buttonColorsTransparent() | ButtonColors     | Creates color configuration for transparent buttons (translucent white fill, white label) |
| textButtonColorsTransparent() | TextButtonColors | Creates color configuration for transparent text buttons |
| textButtonColorsBorderless() | TextButtonColors | Creates color configuration for borderless / text buttons (transparent fill, themed label) |

The secondary colors use the `secondaryButton` (`#EBEBEB` light / `#353535` dark: COUI `couiColorContainer8` composited over `couiColorBackgroundElevated`), `disabledSecondaryButton` and `disabledOnSecondaryButton` scheme colors.

### ButtonMaterial

| Value       | Description |
| ----------- | ----------- |
| None        | No material layer (the ColorOS 17 default) |
| Primary     | `CONTENT_CAPSULE_1` stroke + `TYPE_OPAQUE_MEDIUM_1` spotlight |
| Secondary   | `CONTENT_CAPSULE_2` stroke + `TYPE_OPAQUE_MEDIUM_2` spotlight |
| Transparent | `CONTENT_CAPSULE_3` stroke + `TYPE_TRANSPARENT_MEDIUM_1` spotlight |

## Advanced Usage

### Button with Icon

```kotlin
Button(
    onClick = { /* Handle click event */ }
) {
    Icon(
        imageVector = COUIIcons.Favorites,
        contentDescription = "Favorites"
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text("Button with Icon")
}
```

### Custom Style Button

```kotlin
Button(
    onClick = { /* Handle click event */ },
    colors = ButtonDefaults.buttonColors(
        color = Color.Red.copy(alpha = 0.7f)
    ),
    cornerRadius = 8.dp
) {
    Text("Custom Button")
}
```

### Loading State Button

```kotlin
var isLoading by remember { mutableStateOf(false) }
val scope = rememberCoroutineScope()

Button(
    onClick = {
        isLoading = true
        // Simulate operation
        scope.launch {
            delay(2000)
            isLoading = false
        }
    },
    enabled = !isLoading
) {
     AnimatedVisibility(
        visible = isLoading
    ) {
        CircularProgressIndicator(
            modifier = Modifier.padding(end = 8.dp),
            size = 20.dp,
            strokeWidth = 4.dp
        )
    }
    Text("Submit")
}
```
