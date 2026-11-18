# CameraMenuButton

`CameraMenuButton` is the round button of the camera's bottom effect menu, the port of the camera's `BottomMenuIcon`: a 54dp disc of 10% white with a white icon that turns into an orange accent disc with a black icon when selected, lit by the camera's edge light. `CameraEffectButton` is the small 32dp entry button the camera floats above its shutter row to open the beauty, filter or bokeh panel (`MenuLeftButton` / `MenuRightButton`).

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=cameraMenuButton" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.CameraEffectButton
import io.github.suqi8.coui.kmp.basic.CameraMenuButton
import io.github.suqi8.coui.kmp.basic.CameraMenuButtonDefaults
```

## Basic Usage

```kotlin
var selected by remember { mutableStateOf(false) }

CameraMenuButton(
    onClick = { selected = !selected },
    icon = bokehIcon,
    contentDescription = "Bokeh",
    selected = selected,
)
```

The disc is 54dp and holds a 24dp icon (the camera scales its 20dp vectors 1.2x). Unselected it is a `#1AFFFFFF` fill with a white icon; selected it is the Hasselblad gradient `#FF6B00` → `#FF932D` from top to bottom with a black icon. The state change is immediate, as in the camera.

### Edge Light

Like the camera, the disc carries an inner light rim: three white additive layers when unselected and a warm `#FFD39E` overlay when selected. Turn it off with `edgeLight = false`. It needs render effects (Android 12+ and every Skia target) and is skipped elsewhere.

### Bottom Menu Item

With a `label` the button becomes a bottom menu item: the label sits 4dp below the disc in white 10dp medium text and scrolls when it does not fit.

```kotlin
CameraMenuButton(
    onClick = { mode = Mode.Bokeh },
    icon = bokehIcon,
    contentDescription = "Bokeh",
    selected = mode == Mode.Bokeh,
    label = "Bokeh",
)
```

### Solid Accent and Colorful Border

Without the Hasselblad style the camera fills a selected disc with its solid accent. Pass unspecified gradient colors to get it:

```kotlin
CameraMenuButton(
    onClick = {},
    icon = icon,
    contentDescription = null,
    selected = true,
    colors = CameraMenuButtonDefaults.cameraMenuButtonColors(
        selectedGradientTopColor = Color.Unspecified,
        selectedGradientBottomColor = Color.Unspecified,
    ),
)
```

`colorfulBorder = true` adds the 1dp sweep-gradient ring (blue, violet, red, yellow, green) the camera draws on its AI preset entry.

### Effect Entry Button

```kotlin
CameraEffectButton(
    onClick = { filterOn = !filterOn },
    icon = filterIcon,
    contentDescription = "Filter",
    visible = showEntries,
    selected = filterOn,
    materialBlur = provider, // records the viewfinder, not the button
)
```

A 32dp disc with a 24dp icon. Its background is the camera material blur (80dp) when `materialBlur` is given and a 50% black fill otherwise. When `selected`, the icon fades to the accent `#FB822A` in 320ms on (0.3, 0, 0.1, 1). Changing `visible` fades the button in over 200ms while it grows from 80% on (0.3, 0, 0.1, 1), and fades it out over 200ms on (0.33, 0, 0.67, 1) while it shrinks back to 80%; it keeps its place in the layout while hidden.

## Properties

### CameraMenuButton Properties

| Property Name      | Type                   | Description                                         | Default Value                                  | Required |
| ------------------ | ---------------------- | --------------------------------------------------- | ---------------------------------------------- | -------- |
| onClick            | () -> Unit             | Called when the button is tapped                    | -                                              | Yes      |
| icon               | ImageVector            | The icon, white (black when selected)               | -                                              | Yes      |
| contentDescription | String?                | Accessibility description                           | -                                              | Yes      |
| modifier           | Modifier               | Modifier applied to the button                      | Modifier                                       | No       |
| selected           | Boolean                | Whether the button is selected                      | false                                          | No       |
| enabled            | Boolean                | Whether the button accepts taps                     | true                                           | No       |
| label              | String?                | Optional label below the disc                       | null                                           | No       |
| colorfulBorder     | Boolean                | Whether to draw the colorful sweep-gradient ring    | false                                          | No       |
| edgeLight          | Boolean                | Whether to draw the camera edge light               | true                                           | No       |
| size               | Dp                     | Disc size                                           | CameraMenuButtonDefaults.Size                  | No       |
| colors             | CameraMenuButtonColors | Colors of the button                                | CameraMenuButtonDefaults.cameraMenuButtonColors() | No    |

### CameraEffectButton Properties

| Property Name      | Type                     | Description                                         | Default Value                                      | Required |
| ------------------ | ------------------------ | --------------------------------------------------- | -------------------------------------------------- | -------- |
| onClick            | () -> Unit               | Called when the button is tapped                    | -                                                  | Yes      |
| icon               | ImageVector              | The icon                                            | -                                                  | Yes      |
| contentDescription | String?                  | Accessibility description                           | -                                                  | Yes      |
| modifier           | Modifier                 | Modifier applied to the button                      | Modifier                                           | No       |
| visible            | Boolean                  | Whether the button is shown; changes animate        | true                                               | No       |
| selected           | Boolean                  | Whether the effect is on (accent icon)              | false                                              | No       |
| enabled            | Boolean                  | Whether the button accepts taps                     | true                                               | No       |
| materialBlur       | MaterialBlurProvider?    | Paints the material blur behind the disc; null keeps the fill | null                                     | No       |
| colors             | CameraEffectButtonColors | Colors of the button                                | CameraEffectButtonDefaults.cameraEffectButtonColors() | No    |

## Defaults Object

### CameraMenuButtonDefaults

| Property Name       | Type  | Description                                  | Value   |
| ------------------- | ----- | -------------------------------------------- | ------- |
| Size                | Dp    | Disc size                                    | 54.dp   |
| CornerRadius        | Dp    | Outline corner radius (a circle at 54dp)     | 30.dp   |
| IconSize            | Dp    | Icon size                                    | 24.dp   |
| GradientHeight      | Dp    | Height the selected gradient spans           | 54.dp   |
| ColorfulBorderWidth | Dp    | Colorful ring width                          | 1.dp    |
| LabelTextSize       | Dp    | Label size (dp, not scaled with font scale)  | 10.dp   |
| LabelSpacing        | Dp    | Gap between the disc and the label           | 4.dp    |
| DisabledAlpha       | Float | Alpha of a disabled button                   | 0.3f    |

| Function                 | Return Type            | Description                            |
| ------------------------ | ---------------------- | -------------------------------------- |
| cameraMenuButtonColors() | CameraMenuButtonColors | The fixed light-on-dark camera palette |

### CameraEffectButtonDefaults

| Property Name | Type  | Description                                 | Value  |
| ------------- | ----- | ------------------------------------------- | ------ |
| Size          | Dp    | Button size                                 | 32.dp  |
| IconSize      | Dp    | Icon size                                   | 24.dp  |
| BlurRadius    | Dp    | Material blur radius                        | 80.dp  |
| HiddenScale   | Float | Scale the button grows from and shrinks to  | 0.8f   |

| Function                   | Return Type              | Description                            |
| -------------------------- | ------------------------ | -------------------------------------- |
| cameraEffectButtonColors() | CameraEffectButtonColors | The fixed light-on-dark camera palette |

## Colors Classes

### CameraMenuButtonColors

| Property Name               | Type  | Description                                      | Default   |
| --------------------------- | ----- | ------------------------------------------------ | --------- |
| containerColor              | Color | Disc when not selected                           | #1AFFFFFF |
| selectedContainerColor      | Color | Selected disc when no gradient is given          | #FB822A   |
| selectedGradientTopColor    | Color | Top of the selected gradient                     | #FF6B00   |
| selectedGradientBottomColor | Color | Bottom of the selected gradient                  | #FF932D   |
| iconColor                   | Color | Icon when not selected                           | White     |
| selectedIconColor           | Color | Icon when selected                               | Black     |
| labelColor                  | Color | Label                                            | White     |

### CameraEffectButtonColors

| Property Name     | Type  | Description                      | Default             |
| ----------------- | ----- | -------------------------------- | ------------------- |
| containerColor    | Color | Disc without material blur       | #7F000000           |
| blurMixColor      | Color | Material blur mix color          | 15% grey, 60% alpha |
| blurBlendColor    | Color | Material blur glow-overlay color | 65% grey, 40% alpha |
| iconColor         | Color | Icon when not selected           | White               |
| selectedIconColor | Color | Icon when selected               | #FB822A             |
