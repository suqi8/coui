# Button

`Button` is a basic interactive component in COUI, used to trigger actions or events. It provides multiple style options, including primary buttons, secondary buttons, and text buttons.

<div style="position: relative; height: 200px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
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

### Borderless Text Button

Corresponds to COUI `Widget.COUI.Button.Large.Borderless` / `Translate`: no fill, primary-tinted label.

```kotlin
TextButton(
    text = "Borderless Button",
    onClick = { /* Handle click event */ },
    colors = ButtonDefaults.textButtonColorsBorderless()
)
```

### Small Size Tier

Pass the `Small` metrics from `ButtonDefaults` together with a 14sp text style to get the COUI small size tier (`Widget.COUI.Button.Small`):

```kotlin
TextButton(
    text = "Small Button",
    onClick = { /* Handle click event */ },
    cornerRadius = ButtonDefaults.CornerRadiusSmall,
    minWidth = ButtonDefaults.MinWidthSmall,
    minHeight = ButtonDefaults.MinHeightSmall,
    insideMargin = ButtonDefaults.InsideMarginSmall,
    textStyle = COUITheme.textStyles.button.copy(fontSize = 14.sp)
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
| cornerRadius      | Dp                              | Corner radius of the button            | ButtonDefaults.CornerRadius   | No       |
| minWidth          | Dp                              | Minimum width of the button            | ButtonDefaults.MinWidth       | No       |
| minHeight         | Dp                              | Minimum height of the button           | ButtonDefaults.MinHeight      | No       |
| colors            | ButtonColors                    | Button color configuration             | ButtonDefaults.buttonColors() | No       |
| insideMargin      | PaddingValues                   | Internal padding of the button         | ButtonDefaults.InsideMargin   | No       |
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
| colors            | TextButtonColors          | Text button color configuration   | ButtonDefaults.textButtonColors() | No       |
| cornerRadius      | Dp                        | Corner radius of the button       | ButtonDefaults.CornerRadius       | No       |
| minWidth          | Dp                        | Minimum width of the button       | ButtonDefaults.MinWidth           | No       |
| minHeight         | Dp                        | Minimum height of the button      | ButtonDefaults.MinHeight          | No       |
| insideMargin      | PaddingValues             | Internal padding of the button    | ButtonDefaults.InsideMargin       | No       |
| textStyle         | TextStyle                 | Text style of the label (pass a 14sp style with the `Small` metrics for the COUI small size tier) | COUITheme.textStyles.button | No       |
| interactionSource | MutableInteractionSource? | Interaction source for the button | null                              | No       |
| indication        | Indication?               | Indication for click interactions; `null` because the COUI press feedback (scale + press tint) is built in | null | No       |

### ButtonDefaults Object

The ButtonDefaults object provides default values and color configurations for button components.

#### Constants

| Constant Name | Type          | Description                    | Default Value        |
| ------------- | ------------- | ------------------------------ | -------------------- |
| PressedScale  | Float         | Smallest scale a button shrinks to while pressed (surfaces up to 48 x 48dp) | 0.92f |
| PressedBrightness | Float     | Legacy COUIButton `brightness` value; retained for source compatibility only, no longer used | 0.8f |
| MinWidth      | Dp            | Minimum width of the button    | 58.dp                |
| MinHeight     | Dp            | Minimum height of the button   | 44.dp                |
| CornerRadius  | Dp            | Corner radius of the button    | 22.dp                |
| InsideMargin  | PaddingValues | Internal padding of the button | PaddingValues(horizontal = 12.dp, vertical = 0.dp) |
| MinWidthSmall | Dp            | Minimum width of the small size tier (COUI Widget.COUI.Button.Small) | 52.dp |
| MinHeightSmall | Dp           | Minimum height of the small size tier | 28.dp |
| CornerRadiusSmall | Dp        | Corner radius of the small size tier (height-derived capsule) | 14.dp |
| InsideMarginSmall | PaddingValues | Internal padding of the small size tier | PaddingValues(horizontal = 12.dp, vertical = 4.dp) |

#### Methods

| Method Name               | Type             | Description                                            |
| ------------------------- | ---------------- | ------------------------------------------------------ |
| buttonColors()            | ButtonColors     | Creates color configuration for secondary buttons      |
| buttonColorsPrimary()     | ButtonColors     | Creates color configuration for primary buttons        |
| textButtonColors()        | TextButtonColors | Creates color configuration for secondary text buttons |
| textButtonColorsPrimary() | TextButtonColors | Creates color configuration for primary text buttons   |
| textButtonColorsBorderless() | TextButtonColors | Creates color configuration for borderless / text buttons (transparent fill, themed label) |

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
