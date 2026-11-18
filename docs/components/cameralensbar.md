# CameraLensBar

`CameraLensBar` is the lens switcher of the camera (0.6× · 1× · 3× …), the port of the camera's `SwitchCameraBar` in its divided style. Each lens is a dark disc with its zoom ratio; the selected disc is larger and shows its ratio in the accent color followed by a small "×".

<div style="position: relative; height: 220px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=cameraLensBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.CameraLensBar
import io.github.suqi8.coui.kmp.basic.CameraLensBarDefaults
```

## Basic Usage

```kotlin
val lenses = listOf(0.6f, 1f, 3f).map(CameraLensBarDefaults::formatZoomRatio) // "0.6", "1", "3"
var index by remember { mutableIntStateOf(1) }

CameraLensBar(
    labels = lenses,
    selectedIndex = index,
    onSelectedIndexChange = { index = it },
)
```

The discs have a 14dp radius and sit 17dp apart; the selected disc grows to 16dp. The bar is 36dp high and exactly as wide as its discs (`CameraLensBarDefaults.width(count)`), so place it with the parent's alignment. Ratios are 10dp text; the selected one is bold and followed by a 6dp x 14dp "×" glyph 1.5dp after it.

`CameraLensBarDefaults.formatZoomRatio` formats ratios like the camera: one decimal at most, rounded down, without a trailing ".0" (1.0 gives "1", 0.6 gives "0.6").

### Interaction

- Tapping a lens selects it at once and plays a tap haptic; taps are ignored while a switch is still running.
- During a switch the new disc grows and the old one shrinks in 400ms on (0.3, 0, 0.1, 1), while the ratio slides aside to make room for the "×".
- The colors cross over in 400ms on (0.33, 0, 0.67, 1) after a 100ms delay: the new ratio turns from white to the accent and its "×" fades in, the old one does the reverse.
- Pressing the selected disc shrinks it to 85% and darkens it from 50% to 65% black in 500ms (a critically damped spring); releasing springs it back in 280ms with a slight bounce.
- When `selectedIndex` changes from outside, the bar plays the same switch.

### Light Background

Over bright scenes the camera switches to translucent white discs:

```kotlin
CameraLensBar(
    labels = lenses,
    selectedIndex = index,
    onSelectedIndexChange = { index = it },
    colors = CameraLensBarDefaults.lightBackgroundColors(),
)
```

### Large Screens

On large screens the camera uses bigger discs:

```kotlin
CameraLensBar(
    labels = lenses,
    selectedIndex = index,
    onSelectedIndexChange = { index = it },
    radius = CameraLensBarDefaults.EnlargedRadius,
    selectedRadius = CameraLensBarDefaults.EnlargedSelectedRadius,
)
```

## Properties

### CameraLensBar Properties

| Property Name         | Type                | Description                                                  | Default Value                              | Required |
| --------------------- | ------------------- | ------------------------------------------------------------ | ------------------------------------------ | -------- |
| labels                | List&lt;String&gt;  | Zoom ratios of the lenses, widest first (not empty)          | -                                          | Yes      |
| selectedIndex         | Int                 | Index of the selected lens                                   | -                                          | Yes      |
| onSelectedIndexChange | (Int) -> Unit       | Called with the index of the tapped lens                     | -                                          | Yes      |
| modifier              | Modifier            | Modifier applied to the bar                                  | Modifier                                   | No       |
| enabled               | Boolean             | Whether the lenses accept taps                               | true                                       | No       |
| radius                | Dp                  | Radius of an unselected disc                                 | CameraLensBarDefaults.Radius               | No       |
| selectedRadius        | Dp                  | Radius of the selected disc                                  | CameraLensBarDefaults.SelectedRadius       | No       |
| itemSpacing           | Dp                  | Gap between two discs                                        | CameraLensBarDefaults.ItemSpacing          | No       |
| textSize              | Dp                  | Size of the ratios (dp, not scaled with the font scale)      | CameraLensBarDefaults.TextSize             | No       |
| colors                | CameraLensBarColors | Colors of the bar                                            | CameraLensBarDefaults.cameraLensBarColors() | No      |

## Defaults Object

### CameraLensBarDefaults

| Property Name          | Type               | Description                                  | Value               |
| ---------------------- | ------------------ | -------------------------------------------- | ------------------- |
| Labels                 | List&lt;String&gt; | The camera's default lenses                  | "0.6", "1", "2"     |
| Height                 | Dp                 | Bar height                                   | 36.dp               |
| Radius                 | Dp                 | Radius of an unselected disc                 | 14.dp               |
| SelectedRadius         | Dp                 | Radius of the selected disc                  | 16.dp               |
| ItemSpacing            | Dp                 | Gap between two discs                        | 17.dp               |
| EnlargedRadius         | Dp                 | Unselected radius on large screens           | 15.dp               |
| EnlargedSelectedRadius | Dp                 | Selected radius on large screens             | 18.dp               |
| TextSize               | Dp                 | Size of the ratios                           | 10.dp               |
| SuffixWidth            | Dp                 | Width of the "×" glyph                       | 6.dp                |
| SuffixHeight           | Dp                 | Height of the "×" glyph                      | 14.dp               |
| SuffixSpacing          | Dp                 | Gap between the ratio and the "×"            | 1.5.dp              |
| SuffixOffsetY          | Dp                 | How far the "×" sits below the text center   | 0.5.dp              |

| Function                | Return Type         | Description                                       |
| ----------------------- | ------------------- | ------------------------------------------------- |
| width(count, …)         | Dp                  | Width of a bar of `count` lenses                  |
| formatZoomRatio(ratio)  | String              | Formats a ratio like the camera ("1", "0.6")      |
| cameraLensBarColors()   | CameraLensBarColors | The fixed palette over a dark preview             |
| lightBackgroundColors() | CameraLensBarColors | The palette over a light preview                  |

## Colors Classes

### CameraLensBarColors

| Property Name                 | Type  | Description                        | Default   |
| ----------------------------- | ----- | ---------------------------------- | --------- |
| containerColor                | Color | Unselected discs                   | #7F000000 |
| selectedContainerColor        | Color | Selected disc                      | #80000000 |
| pressedSelectedContainerColor | Color | Selected disc while pressed        | #A6000000 |
| contentColor                  | Color | Unselected ratios                  | White     |
| selectedContentColor          | Color | Selected ratio                     | #FB822A   |
| suffixColor                   | Color | "×" after the selected ratio       | #FB822A   |
