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

### Single-Button Width Tiers

COUI fixes the width of a standalone button by tier (`SingleButtonWrap`): the medium tier is 152dp (`ButtonDefaults.MediumWidth`); the large tier is 174dp (`ButtonDefaults.LargeWidth`) on medium and large screens, 296dp (`ButtonDefaults.SingleLargeWidth`) on small screens at least 360dp wide and the full width below. `ButtonDefaults.largeWidth()` resolves the large tier for the current window (`Dp.Unspecified` means fill the width). These tiers allow two label lines:

```kotlin
val largeWidth = ButtonDefaults.largeWidth()
TextButton(
    text = "Continue",
    onClick = { /* Handle click event */ },
    modifier = if (largeWidth == Dp.Unspecified) Modifier.fillMaxWidth() else Modifier.width(largeWidth),
    colors = ButtonDefaults.textButtonColorsPrimary(),
    maxLines = 2
)
```

### Description Button

`DescTextButton` is the COUI desc-type button (`COUIButton.setDescType`): a 16sp label over a 10sp description, each centred and ellipsized on its own line, with 12dp / 4dp padding, the 44dp min height and no min width:

```kotlin
DescTextButton(
    text = "Upgrade",
    descText = "Free for the first month",
    onClick = { /* Handle click event */ },
    modifier = Modifier.width(ButtonDefaults.MediumWidth),
    colors = ButtonDefaults.textButtonColorsPrimary()
)
```

## Press Feedback

The press feedback follows COUI 17 `COUIButton` (`COUITouchMotionAnimator` + `COUIMaskEffectDrawable`):

- **Scale target** is derived from the measured size in dp (`PressCalc`): an area channel `0.85 + B(t) * 0.12` with `t` the area mapped from 20² to 150², and for buttons wider than twice their height an aspect-ratio channel `0.85 + B((ar - 2) / 13) * 0.15` averaged in (`B = cubic-bezier(0, 0, 0, 1)`). A 296 x 44dp button presses to ~0.966, 132 x 44dp to ~0.920, 52 x 28dp to ~0.886.
- **Springs**: buttons wider than twice their height use the candy pair (press k 438.65 / ζ 1, release k 157.91 / ζ 0.5); compact and round buttons use the jelly pair (press k 631.65 / ζ 1, release k 157.91 / ζ 0.4). The under-damped release gives the slight rebound. Every press starts from rest, while the release takes over the momentum of a press-in still under way (`COUIPressAnimatorUnit`), so a quick tap sinks a little further before it bounces back. Both springs settle at the official minimum visible changes (0.001 pressing, 0.0001 releasing) instead of jumping the last 1%.
- **Press tint**: `couiColorPress` (`#1F000000` light / `#33FFFFFF` dark) on a k 438.65 / ζ 1 spring, held until at least 70% visible and turned around with its momentum. Like `COUIButton.onTouchEvent`, it enters on the touch down and exits on the lift or cancellation, so it shows at once inside scrolling lists and stays while the finger slides off the button; keyboard and accessibility presses show it too. It is off while the material layer is drawn (`COUIButton.applyBlurEffect` disables the mask's pressed state).
- **Hover tint**: `couiColorHover` (`#14000000` light / `#26FFFFFF` dark) on the same spring; it clears at once when the button is disabled.
- **Drag motion** (`COUIRubberAnimatorUnit` + `COUIDeformAnimatorUnit`): past a 5px dead zone the button follows the finger on the rubber band `(1 - 1 / (0.05·|d| / 28dp + 1)) · 28dp`, and stretches along the drag while squeezing across it (area-conserving, `1 + band(|d|, 0.55, 150dp) / 150dp · (max - 1)`, `max` = 1.2 for small views, less for large and long ones). Both follow a response 0.15 / bounce 0.15 spring. The press scale stays in while the finger is outside the button.
- **Release**: translation and stretch spring home on response 0.42 (0.45 for views over 100dp²) / bounce 0.65, seeded with the finger velocity. A gesture taken over by a scrolling parent releases with zero velocity. Touching the button again while it springs back catches it where it is; the next drag continues from there. Disabling the button springs everything back.
- **Label size**: like every `COUIButton` (`COUIChangeTextUtil.adaptFontSize(this, G4)`), the label of `TextButton`, `LoadingButton` and `TwoTextButton` stops growing past a 1.35 font scale; the two lines of `DescTextButton` follow the `G2` step (1.0 below a 1.15 font scale, 1.15 above), and so does its min height.

## Material Layer

Every COUI 17 `ButtonNew` style follows the system material switches (`useMaterialEffect = followSystem`), which are on on ColorOS 17 devices, so `Button` draws the material layer by default: the edge lighting + inner shadow (`COUIMaterialStrokeEffect` `CONTENT_CAPSULE_1/2/3`) and the touch spotlight (`COUISpotLightEffect` `OPAQUE_MEDIUM_1/2`, `TRANSPARENT_MEDIUM_1`). `ButtonMaterial.Auto` picks the preset from the fill (primary, transparent-button, or secondary for anything else; borderless buttons get none). Pass `ButtonMaterial.None` to opt out. Both effects need runtime shaders (Android 13+ and every Skia target) and are skipped elsewhere, in which case the press tint stays. The framework background blur that COUI draws in place of the solid fill needs a backdrop. See [Backdrop Blur](#backdrop-blur). The caustic shadow is not drawn: it is rendered natively by the Oplus `RenderNode`, and only when the app opts in.

```kotlin
TextButton(
    text = "Material Button",
    onClick = { /* Handle click event */ },
    colors = ButtonDefaults.textButtonColorsPrimary(),
    material = ButtonMaterial.Primary
)
```

### Backdrop Blur

With the system material blur on, COUI 17 does not draw a button's solid fill at all: `COUIButton.onDraw` skips it and `setViewBlurEffect` filters what lies behind the button instead (primary 10px with the fill re-alpha'd to 229/230, secondary 25px with `#CCFFFFFF` / `#33FFFFFF`, transparent 25px with `#3F333333` overlay + `#3FFFFFFF` luminosity). coui-ui cannot capture a backdrop on its own, so `Button` draws this only when a `MaterialBackdropProvider` is provided through `LocalMaterialBackdrop` and runtime shaders are available. The blur then replaces the fill inside the touch-motion layer, so it scales and deforms with the press, and the hover tint is drawn over it. Without a provider the button keeps its opaque fill, which is exactly COUI's non-blur fallback (`#EBEBEB` / `#353535` secondary). Pass `materialBackdrop = false` to keep the fill.

```kotlin
val backdrop = rememberLayerBackdrop()
Box {
    Image(painter, null, Modifier.matchParentSize().layerBackdrop(backdrop))
    val provider = remember(backdrop) {
        MaterialBackdropProvider { spec, shape ->
            Modifier.couiViewBlur(
                backdrop,
                ViewBlurStyle(spec.blurRadiusPx, spec.layer1, spec.mode1, spec.layer2, spec.mode2),
                shape,
            )
        }
    }
    CompositionLocalProvider(LocalMaterialBackdrop provides provider) {
        TextButton(text = "Blurred", onClick = {})
    }
}
```

- The provider's backdrop must not record the button: provide it around content that sits on top of the recorded layer. A page-wide `layerBackdrop` that contains the button cannot be used.
- Dialogs, bottom sheets and popups provide `null` for their content, so buttons on a panel keep their fill instead of sampling the page behind it. COUI's own dialog buttons use the `drawableMaterialEffect` path and never blur either.
- The press scale is applied to the sampled backdrop as well, so the backdrop shrinks by up to ~8% with the button. Under a 10-25px blur this is hardly visible.
- The caustic shadow is not drawn. COUI only turns it on through `setCausticShadow(6|7)`, and its default is off.

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
| cornerRadius      | Dp                              | Corner radius of the button; `Dp.Unspecified` rounds it at half the measured height (COUI capsule, the weight-1.0 soft capsule), an explicit value draws a weight-3.0 squircle | ButtonDefaults.CornerRadius   | No       |
| minWidth          | Dp                              | Minimum width of the button            | ButtonDefaults.MinWidth       | No       |
| minHeight         | Dp                              | Minimum height of the button           | ButtonDefaults.MinHeight      | No       |
| colors            | ButtonColors                    | Button color configuration             | ButtonDefaults.buttonColors() | No       |
| insideMargin      | PaddingValues                   | Internal padding of the button         | ButtonDefaults.InsideMargin   | No       |
| material          | ButtonMaterial                  | COUI 17 material layer (edge lighting, inner shadow, touch spotlight); the press tint is off while it is drawn. `Auto` draws none while `LocalCOUIMaterialEffectEnabled` is `false` | ButtonMaterial.Auto | No       |
| materialBackdrop  | Boolean                         | Whether the material may replace the fill with the backdrop blur; only drawn with a `LocalMaterialBackdrop` provider | `LocalCOUIMaterialEffectEnabled.current` | No       |
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
| textStyle         | TextStyle                 | Text style of the label (pass `ButtonDefaults.textStyleSmall()` with the `Small` metrics for the COUI small size tier) | COUITheme.textStyles.buttonL | No       |
| maxLines          | Int                       | Maximum number of label lines; COUIButton is single-line with an end ellipsis | 1 | No       |
| material          | ButtonMaterial            | COUI 17 material layer, see `Button` | ButtonMaterial.Auto | No       |
| materialBackdrop  | Boolean                   | Whether the material may replace the fill with the backdrop blur, see `Button` | `LocalCOUIMaterialEffectEnabled.current` | No       |
| interactionSource | MutableInteractionSource? | Interaction source for the button | null                              | No       |
| indication        | Indication?               | Indication for click interactions; `null` because the COUI press feedback (scale + press tint) is built in | null | No       |

### DescTextButton Properties

| Property Name     | Type                      | Description                                  | Default Value                     | Required |
| ----------------- | ------------------------- | -------------------------------------------- | --------------------------------- | -------- |
| text              | String                    | Label (16sp)                                 | -                                 | Yes      |
| descText          | String                    | Description under the label (10sp)           | -                                 | Yes      |
| onClick           | () -> Unit                | Callback triggered on click                  | -                                 | Yes      |
| modifier          | Modifier                  | Modifier applied to the button               | Modifier                          | No       |
| enabled           | Boolean                   | Whether the button is clickable              | true                              | No       |
| pressScaleEnabled | Boolean                   | Whether the button shrinks while pressed     | true                              | No       |
| cornerRadius      | Dp                        | Corner radius; unspecified = capsule         | ButtonDefaults.CornerRadius       | No       |
| minWidth          | Dp                        | Minimum width                                | ButtonDefaults.MinWidthDesc       | No       |
| minHeight         | Dp                        | Minimum height                               | ButtonDefaults.MinHeight          | No       |
| colors            | TextButtonColors          | Color configuration                          | ButtonDefaults.textButtonColors() | No       |
| insideMargin      | PaddingValues             | Internal padding                             | ButtonDefaults.InsideMarginDesc   | No       |
| material          | ButtonMaterial            | COUI 17 material layer, see `Button`         | ButtonMaterial.Auto               | No       |
| materialBackdrop  | Boolean                   | Whether the material may use the backdrop    | `LocalCOUIMaterialEffectEnabled.current` | No       |
| interactionSource | MutableInteractionSource? | Interaction source for the button            | null                              | No       |

### ButtonDefaults Object

The ButtonDefaults object provides default values and color configurations for button components.

#### Constants

| Constant Name | Type          | Description                    | Default Value        |
| ------------- | ------------- | ------------------------------ | -------------------- |
| MinWidth      | Dp            | Minimum width of the button (COUI `coui_btn_large_width_min`) | 132.dp |
| MinHeight     | Dp            | Minimum height of the button   | 44.dp                |
| CornerRadius  | Dp            | Corner radius of the button; unspecified = capsule at half the measured height | Dp.Unspecified |
| InsideMargin  | PaddingValues | Internal padding of the button (COUI `Widget.COUI.Button.Large`) | PaddingValues(horizontal = 12.dp, vertical = 8.dp) |
| MinWidthSmall | Dp            | Minimum width of the small size tier (COUI Widget.COUI.Button.Small) | 52.dp |
| MinHeightSmall | Dp           | Minimum height of the small size tier | 28.dp |
| CornerRadiusSmall | Dp        | Corner radius of the small size tier (height-derived capsule) | Dp.Unspecified |
| InsideMarginSmall | PaddingValues | Internal padding of the small size tier | PaddingValues(horizontal = 12.dp, vertical = 4.dp) |
| MediumWidth   | Dp            | Width of the medium single-button tier (COUI `coui_medium_btn_width`) | 152.dp |
| LargeWidth    | Dp            | Width of the large tier on medium and large screens (COUI `coui_larger_btn_width`) | 174.dp |
| SingleLargeWidth | Dp         | Width of the large tier on small screens at least 360dp wide (COUI `coui_single_larger_btn_width`) | 296.dp |
| SingleLargeWindowWidth | Dp   | Narrowest small-screen window that gets `SingleLargeWidth` (COUI `coui_single_larger_window_screen`) | 360.dp |
| MinWidthDesc  | Dp            | Minimum width of the desc-type button | 0.dp |
| InsideMarginDesc | PaddingValues | Internal padding of the desc-type button (COUI `coui_btn_desc_padding_*`) | PaddingValues(horizontal = 12.dp, vertical = 4.dp) |

#### Methods

| Method Name               | Type             | Description                                            |
| ------------------------- | ---------------- | ------------------------------------------------------ |
| buttonColors()            | ButtonColors     | Creates color configuration for secondary buttons      |
| buttonColorsPrimary()     | ButtonColors     | Creates color configuration for primary buttons        |
| textButtonColors()        | TextButtonColors | Creates color configuration for secondary text buttons |
| textButtonColorsPrimary() | TextButtonColors | Creates color configuration for primary text buttons   |
| textStyleSmall()          | TextStyle        | Label style of the small size tier (14sp medium)       |
| largeWidth()              | Dp               | Width of the large single-button tier for the current window; `Dp.Unspecified` = fill the width |
| textStyleDesc()           | TextStyle        | Label style of the desc-type button (16sp medium)      |
| textStyleDescSub()        | TextStyle        | Description style of the desc-type button (10sp medium) |
| buttonColorsTransparent() | ButtonColors     | Creates color configuration for transparent buttons (translucent white fill, white label) |
| textButtonColorsTransparent() | TextButtonColors | Creates color configuration for transparent text buttons |
| textButtonColorsBorderless() | TextButtonColors | Creates color configuration for borderless / text buttons (transparent fill, themed label) |

The secondary colors use the `secondaryButton` (`#EBEBEB` light / `#353535` dark: COUI `couiColorContainer8` composited over `couiColorBackgroundElevated`), `disabledSecondaryButton` and `disabledOnSecondaryButton` scheme colors.

### ButtonMaterial

| Value       | Description |
| ----------- | ----------- |
| Auto        | The preset matching the fill: `Primary` for the theme primary, `Transparent` for the transparent-button fill, none for a fully transparent fill, `Secondary` otherwise (the default) |
| None        | No material layer (`useMaterialEffect = forceDisable`) |
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
