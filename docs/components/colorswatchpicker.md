# ColorSwatchPicker

A horizontal row of circular color swatches with an animated selection ring, mirroring the ColorOS 16 icon mono-color selector (UxColorSelectableView): a 34dp color dot inside a 44dp cell, with a 2dp theme-colored ring that fades in on selection.

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.ColorSwatchPicker
import io.github.suqi8.coui.kmp.basic.ColorSwatchPickerDefaults
```

## Basic Usage

```kotlin
val colors = remember {
    listOf(
        Color(0xFF0066FF),
        Color(0xFF24B232),
        Color(0xFFF08222),
        Color(0xFFDB382C),
    )
}
var selectedIndex by remember { mutableIntStateOf(0) }

ColorSwatchPicker(
    colors = colors,
    selectedIndex = selectedIndex,
    onSwatchSelected = { selectedIndex = it },
)
```

> Build the `colors` list inside a `remember { }` block (or keep the same instance across compositions): standard `List` is unstable to Compose and a fresh instance would defeat recomposition skipping.

## Properties

### ColorSwatchPicker

| Property         | Type          | Description                                              | Default Value                             | Required |
| ---------------- | ------------- | -------------------------------------------------------- | ----------------------------------------- | -------- |
| colors           | List\<Color\> | Colors displayed as swatches                             | -                                         | Yes      |
| selectedIndex    | Int           | Index of the selected swatch (negative for none)         | -                                         | Yes      |
| onSwatchSelected | (Int) -> Unit | Callback with the index of the swatch the user selects   | -                                         | Yes      |
| modifier         | Modifier      | Modifier applied to the picker                           | Modifier                                  | No       |
| enabled          | Boolean       | Whether the picker is enabled                            | true                                      | No       |
| swatchSize       | Dp            | Diameter of each color dot                               | ColorSwatchPickerDefaults.SwatchSize      | No       |
| cellSize         | Dp            | Touch target / selection ring outer diameter             | ColorSwatchPickerDefaults.CellSize        | No       |
| ringStrokeWidth  | Dp            | Stroke width of the selection ring                       | ColorSwatchPickerDefaults.RingStrokeWidth | No       |
| ringColor        | Color         | Color of the selection ring                              | COUITheme.colorScheme.primary            | No       |
| spacing          | Dp            | Spacing between adjacent cells                           | ColorSwatchPickerDefaults.Spacing         | No       |

### ColorSwatchPickerDefaults

| Constant            | Type              | Default Value                       | COUI Source                 |
| ------------------- | ----------------- | ----------------------------------- | --------------------------- |
| SwatchSize          | Dp                | 34.dp                               | ux_icon_color_select_inside |
| CellSize            | Dp                | 44.dp                               | ux_icon_color_select_out    |
| RingStrokeWidth     | Dp                | 2.dp                                | ux_theme_shadow_stroke      |
| Spacing             | Dp                | 0.dp                                | cells laid edge to edge     |
| RingFadeInDuration  | Int               | 280                                 | selection animator duration |
| RingFadeOutDuration | Int               | 150                                 | deselection animator duration |
| RingEasing          | CubicBezierEasing | CubicBezierEasing(0.33f, 0f, 0.67f, 1f) | PathInterpolator(0.33, 0, 0.67, 1) |

## Behavior

- Tapping a cell invokes `onSwatchSelected` with the cell index; the component is stateless and the caller owns the selection.
- The selection ring fades in over 280ms and fades out over 150ms with the COUI easing curve.
- Each cell exposes `Role.RadioButton` semantics inside a selectable group for accessibility.
