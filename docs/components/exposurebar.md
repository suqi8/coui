# ExposureBar

`ExposureBar` is the exposure compensation (EV) panel of the camera, the port of the camera's global EV bar (`FocusExposureUIManager` building a `BottomEffectContainer` around an `HSScaleBar` and a `ResetImageView`). A dark capsule holds a horizontal ruler with one tick per EV step and a reset button at its start. `ScaleBar`, the ruler inside it, can also be used on its own for other camera parameters such as ISO or shutter speed.

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=exposureBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.ExposureBar
import io.github.suqi8.coui.kmp.basic.ExposureBarDefaults
import io.github.suqi8.coui.kmp.basic.ScaleBar
```

## Basic Usage

```kotlin
var index by remember { mutableIntStateOf(ExposureBarDefaults.Values.size / 2) } // 0.0 EV

ExposureBar(
    selectedIndex = index,
    onSelectedIndexChange = { index = it },
)
```

The default entries are the camera's -2.0..+2.0 EV in 1/3 steps (13 ticks, a major tick every whole EV). The panel is 336dp x 60dp with 32dp corners; the ruler is inset 64dp on both sides and the 32dp reset button sits 14dp from the start and top.

### Interaction

- Dragging moves the ticks under the fixed orange indicator; the value above it always shows the nearest entry.
- Past either end the ruler stretches on a rubber band (5% of the finger travel, the ticks spreading by half of it) and springs back in 350ms on (0.3, 0, 0.1, 1) when released.
- A release faster than 500px/s keeps the ruler gliding for 400ms on (0, 0, 0.58, 1) over 30% of the speed (600ms on (0.3, 0.1, 0, 1) over 15% for rulers of more than 30 entries), then it settles on the nearest entry.
- Ticks fade out over the outer 49dp at both ends.
- Each change of the selected entry during a gesture plays a light tick haptic.
- The reset button dims to 30% while the value is already at `defaultIndex`. Tapping it spins the arrow once (300ms) and glides the ruler back in 300ms.

### Material Blur

Over the camera preview the capsule is a material blur (80dp radius, a glow overlay of a 15% grey over a 60% mix of 30% grey). Pass a `MaterialBlurProvider` that records the content under the panel; without one the panel keeps the camera's non-blur fill, an opaque `#141414` capsule with a 0.67dp 10% white border.

```kotlin
ExposureBar(
    selectedIndex = index,
    onSelectedIndexChange = { index = it },
    materialBlur = provider, // records the viewfinder, not the panel
)
```

### Professional Range

The camera's professional modes use -3.0..+3.0 EV:

```kotlin
ExposureBar(
    selectedIndex = index,
    onSelectedIndexChange = { index = it },
    values = ExposureBarDefaults.WideValues,
)
```

### ScaleBar on its Own

```kotlin
val isos = listOf("50", "64", "80", "100", "125", "160", "200", "250", "320", "400")
var iso by remember { mutableIntStateOf(3) }

ScaleBar(
    labels = isos,
    selectedIndex = iso,
    onSelectedIndexChange = { iso = it },
    valueSuffix = " ISO",
)
```

## Properties

### ExposureBar Properties

| Property Name         | Type                     | Description                                                        | Default Value                             | Required |
| --------------------- | ------------------------ | ------------------------------------------------------------------ | ----------------------------------------- | -------- |
| selectedIndex         | Int                      | Index of the selected entry of `values`                            | -                                         | Yes      |
| onSelectedIndexChange | (Int) -> Unit            | Called while the ruler is dragged, flung, settles or is reset      | -                                         | Yes      |
| modifier              | Modifier                 | Modifier applied to the panel                                      | Modifier                                  | No       |
| enabled               | Boolean                  | Whether the ruler and the reset button accept input                | true                                      | No       |
| values                | List&lt;String&gt;       | Labels of the entries, lowest to highest                           | ExposureBarDefaults.Values                | No       |
| defaultIndex          | Int                      | Entry the reset button returns to                                  | values.size / 2                           | No       |
| showResetButton       | Boolean                  | Whether to show the reset button                                   | true                                      | No       |
| materialBlur          | MaterialBlurProvider?    | Paints the material blur behind the capsule; null keeps the fill   | null                                      | No       |
| colors                | ExposureBarColors        | Colors of the panel                                                | ExposureBarDefaults.exposureBarColors()   | No       |

### ScaleBar Properties

| Property Name         | Type                     | Description                                                        | Default Value                             | Required |
| --------------------- | ------------------------ | ------------------------------------------------------------------ | ----------------------------------------- | -------- |
| labels                | List&lt;String&gt;       | Labels of the entries, start to end (not empty)                    | -                                         | Yes      |
| selectedIndex         | Int                      | Index of the selected entry                                        | -                                         | Yes      |
| onSelectedIndexChange | (Int) -> Unit            | Called while the ruler is dragged, flung or settles                | -                                         | Yes      |
| modifier              | Modifier                 | Modifier applied to the ruler                                      | Modifier                                  | No       |
| enabled               | Boolean                  | Whether the ruler accepts drags                                    | true                                      | No       |
| showIndicator         | Boolean                  | Whether to draw the center indicator                               | true                                      | No       |
| majorTickInterval     | Int                      | Entries per major tick; the first entry is always major            | ScaleBarDefaults.MajorTickInterval        | No       |
| valueSuffix           | String                   | Suffix appended to the value over the indicator                    | ""                                        | No       |
| valueTextSize         | Dp                       | Size of the value (dp, not scaled with the font scale)             | ScaleBarDefaults.ValueTextSize            | No       |
| colors                | ScaleBarColors           | Colors of the ruler                                                | ScaleBarDefaults.scaleBarColors()         | No       |

## Defaults Object

### ExposureBarDefaults

| Property Name            | Type          | Description                                    | Value                  |
| ------------------------ | ------------- | ---------------------------------------------- | ---------------------- |
| Values                   | List&lt;String&gt; | -2.0..+2.0 EV in 1/3 steps                | 13 entries             |
| WideValues               | List&lt;String&gt; | -3.0..+3.0 EV in 1/3 steps                | 19 entries             |
| Width                    | Dp            | Panel width                                    | 336.dp                 |
| Height                   | Dp            | Panel height                                   | 60.dp                  |
| CornerRadius             | Dp            | Capsule corner radius                          | 32.dp                  |
| BlurRadius               | Dp            | Material blur radius                           | 80.dp                  |
| BorderWidth              | Dp            | Border of the non-blur fill                    | 0.67.dp                |
| ScaleBarHorizontalMargin | Dp            | Ruler inset at both sides                      | 64.dp                  |
| MajorTickInterval        | Int           | Entries per major tick                         | 3                      |
| ValueTextSize            | Dp            | Size of the value over the indicator           | 14.dp                  |
| ResetButtonSize          | Dp            | Reset button size                              | 32.dp                  |
| ResetIconSize            | Dp            | Reset icon size                                | 24.dp                  |
| ResetButtonMarginStart   | Dp            | Reset button start margin                      | 14.dp                  |
| ResetButtonMarginTop     | Dp            | Reset button top margin                        | 14.dp                  |
| ResetDisabledAlpha       | Float         | Reset button alpha with nothing to reset       | 0.3f                   |

| Function            | Return Type       | Description                         |
| ------------------- | ----------------- | ----------------------------------- |
| exposureBarColors() | ExposureBarColors | The fixed light-on-dark camera palette |

### ScaleBarDefaults

| Property Name     | Type | Description                                         | Value   |
| ----------------- | ---- | --------------------------------------------------- | ------- |
| Height            | Dp   | Ruler height                                        | 60.dp   |
| TickWidth         | Dp   | Tick width                                          | 1.33.dp |
| MediumTickGap     | Dp   | Tick gap for up to `DenseCount` entries             | 14.dp   |
| SmallTickGap      | Dp   | Tick gap above `DenseCount` entries                 | 6.dp    |
| DenseCount        | Int  | Entry count above which ticks pack and glide longer | 30      |
| FadeWidth         | Dp   | Width over which ticks fade at both ends            | 49.dp   |
| TickTop           | Dp   | Tick top, from the vertical center                  | 6.dp    |
| TickBottom        | Dp   | Tick bottom, from the vertical center               | 18.dp   |
| IndicatorTop      | Dp   | Indicator top, from the vertical center             | 0.dp    |
| IndicatorBottom   | Dp   | Indicator bottom, from the vertical center          | 24.dp   |
| ValueBandTop      | Dp   | Top of the value band, from the vertical center     | -25.dp  |
| ValueBandHeight   | Dp   | Height of the value band                            | 20.dp   |
| ValueTextSize     | Dp   | Size of the value                                   | 18.dp   |
| MajorTickInterval | Int  | Entries per major tick                              | 2       |

| Function         | Return Type    | Description                         |
| ---------------- | -------------- | ----------------------------------- |
| scaleBarColors() | ScaleBarColors | The fixed light-on-dark ruler palette |

## Colors Classes

### ExposureBarColors

| Property Name    | Type           | Description                          | Default     |
| ---------------- | -------------- | ------------------------------------ | ----------- |
| containerColor   | Color          | Capsule fill without material blur   | #141414     |
| borderColor      | Color          | Capsule border without material blur | #1AFFFFFF   |
| blurMixColor     | Color          | Material blur mix color              | 30% grey, 60% alpha |
| blurBlendColor   | Color          | Material blur glow-overlay color     | 15% grey, 15% alpha |
| resetButtonColor | Color          | Reset button disc                    | #1AFFFFFF   |
| resetIconColor   | Color          | Reset arrow                          | White       |
| scaleBarColors   | ScaleBarColors | Ruler colors                         | ScaleBarDefaults.scaleBarColors() |

### ScaleBarColors

| Property Name  | Type  | Description               | Default   |
| -------------- | ----- | ------------------------- | --------- |
| majorTickColor | Color | Major ticks               | White     |
| minorTickColor | Color | Minor ticks               | #4DFFFFFF |
| indicatorColor | Color | Center indicator          | #FB822A   |
| valueColor     | Color | Value over the indicator  | White     |
