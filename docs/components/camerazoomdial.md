# CameraZoomDial

`CameraZoomDial` is the zoom dial of the camera, the port of the camera's arc zoom bar (`ZoomArcSeekBar`, a `ZoomSeekBar` drawn by `CameraSeekBar`). Long-pressing or dragging the lens buttons unfolds a dark dome with a logarithmic zoom ruler along its top; turning it changes the zoom, and it folds away again 1.5s after the last touch.

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=cameraZoomDial" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.CameraZoomDial
import io.github.suqi8.coui.kmp.basic.CameraZoomDialDefaults
```

## Basic Usage

```kotlin
var zoom by remember { mutableFloatStateOf(1f) }
var expanded by remember { mutableStateOf(false) }

CameraZoomDial(
    value = zoom,
    onValueChange = { zoom = it },
    expanded = expanded,
    onExpandedChange = { expanded = it },
) {
    LensButtons() // your collapsed control, e.g. 0.6x / 1x / 2x / 3x
}
```

Place the dial at the bottom edge of the viewfinder. It fills the width it is given; its height is the part of the 199dp dome inside that width (at least 98dp, and tall enough for `content`), and `content` is centered 86.7dp below its top.

### Drawing

- The dome is the top cap of a 199dp circle: 50% black with a 0.7dp 10% white rim.
- One radial 0.66dp x 10dp tick every 0.1x from the first to the last entry of `stops`, 8dp inside the rim. Every octave of zoom takes the same angle: the range up to 15x spans 90° (60° when the dial ends at 6x or below).
- Each entry of `stops` is a full-white major tick with its value written 18dp under it (12dp, rotated along the arc). Whole values are major as well; once the dial reaches past 30x only multiples of 10, and of 5 above 10x, are. Other ticks are 15% white.
- From 15x on, major ticks dim to 70% and on towards half of that at the end of the dial, and the minor ticks merge into one translucent band.
- Ticks and labels fade out over 8°, starting 5° above where the dome meets the side edges.
- The current zoom sits under a fixed 1.5dp accent tick at the top, written in bold accent with a "×" sign, rounded down to 0.1 (it only moves once the zoom is 0.1 away). Stop labels sliding under it fade out where they meet it, over 4dp.

### Interaction

- Dragging turns the ruler with the finger around the circle center: the ticks under the finger follow it, so dragging to the right lowers the zoom.
- Moving slower than 200px/s, the dial stops on each entry of `stops` it crosses and holds there until the finger has moved on by a few dp (more below 10x).
- Past either end the ruler stretches on a rubber band (4% of the finger travel, up to 2.1% of the range, the ticks spreading out) and springs back in 500ms on (0.3, 0, 0.1, 1).
- Crossing an entry of `stops` between both ends, or a multiple of 5 above 20x, plays a tick haptic; reaching the end of the rubber band plays an edge haptic.
- Touching the label of a stop glides the dial there in 300ms.
- While collapsed, long-pressing `content`, dragging it sideways or swiping it up (within 17dp / 12dp around it) calls `onExpandedChange(true)`; a sideways drag keeps turning the dial in the same gesture.
- A steep downward swipe, or 1.5s without a touch, calls `onExpandedChange(false)`.

### Animation

Expanding fades the dome and the ruler in (250ms, 70ms delay) and grows the dome by 33dp on a spring (350ms), while `content` fades out (180ms). Collapsing fades them out in 280ms and 180ms and `content` back in (250ms).

### Custom Stops

The first and the last entry of `stops` are the ends of the dial; values on the 0.1x grid line up with its ticks.

```kotlin
CameraZoomDial(
    value = zoom,
    onValueChange = { zoom = it },
    expanded = expanded,
    onExpandedChange = { expanded = it },
    stops = listOf(0.6f, 1f, 2f, 5f), // ends at 5x: the dial spans 60°
)
```

### Always Expanded

```kotlin
CameraZoomDial(
    value = zoom,
    onValueChange = { zoom = it },
    expanded = true,
    onExpandedChange = {}, // ignore the collapse requests
)
```

## Properties

### CameraZoomDial Properties

| Property Name    | Type                    | Description                                                         | Default Value                                | Required |
| ---------------- | ----------------------- | ------------------------------------------------------------------- | -------------------------------------------- | -------- |
| value            | Float                   | Current zoom ratio, between the first and the last stop             | -                                            | Yes      |
| onValueChange    | (Float) -> Unit         | Called while the dial is turned or glides to a stop                 | -                                            | Yes      |
| expanded         | Boolean                 | Whether the dial is unfolded                                        | -                                            | Yes      |
| onExpandedChange | (Boolean) -> Unit       | Called when a gesture asks to expand or the dial should collapse    | -                                            | Yes      |
| modifier         | Modifier                | Modifier applied to the dial                                        | Modifier                                     | No       |
| enabled          | Boolean                 | Whether the dial and the expand gestures accept input               | true                                         | No       |
| stops            | List&lt;Float&gt;       | Marked zoom ratios, ascending; the first and last are the ends      | CameraZoomDialDefaults.Stops                 | No       |
| radius           | Dp                      | Radius of the dome                                                  | CameraZoomDialDefaults.Radius                | No       |
| colors           | CameraZoomDialColors    | Colors of the dial                                                  | CameraZoomDialDefaults.cameraZoomDialColors() | No       |
| content          | @Composable () -> Unit  | Collapsed control under the dome, such as the lens buttons          | {}                                           | No       |

## Defaults Object

### CameraZoomDialDefaults

| Property Name       | Type               | Description                                              | Value                                  |
| ------------------- | ------------------ | -------------------------------------------------------- | -------------------------------------- |
| Stops               | List&lt;Float&gt;  | Stops of a 0.6x-120x camera with a 3x telephoto          | 0.6, 1, 2, 3, 10, 20, 120              |
| Radius              | Dp                 | Dome radius                                              | 199.dp                                 |
| UnfoldDistance      | Dp                 | How much smaller the dome starts unfolding               | 33.dp                                  |
| BorderWidth         | Dp                 | Rim of the dome                                          | 0.7.dp                                 |
| TickLength          | Dp                 | Tick length                                              | 10.dp                                  |
| TickPaddingTop      | Dp                 | Gap between the rim and the ticks                        | 8.dp                                   |
| TickWidth           | Dp                 | Tick width                                               | 0.66.dp                                |
| IndicatorWidth      | Dp                 | Width of the accent indicator                            | 1.5.dp                                 |
| LabelMarginTop      | Dp                 | Gap between the ticks and the labels                     | 18.dp                                  |
| LabelBaselineOffset | Dp                 | Label baseline below the label point                     | 4.dp                                   |
| LabelTextSize       | Dp                 | Size of the stop labels                                  | 12.dp                                  |
| ValueTextSize       | Dp                 | Size of the current value                                | 12.dp                                  |
| LabelFadeMargin     | Dp                 | Fade of a label sliding under the current value          | 4.dp                                   |
| MultiplySignSpacing | Dp                 | Gap before the "×" sign                                  | 1.dp                                   |
| LabelTouchSize      | Dp                 | How near a touch must be to a stop label                 | 15.dp                                  |
| HotAreaHorizontal   | Dp                 | Hot area beside the collapsed control                    | 17.dp                                  |
| HotAreaVertical     | Dp                 | Hot area above and below the collapsed control           | 12.dp                                  |
| ContentCenterY      | Dp                 | Center of the collapsed control below the top            | 86.7.dp                                |
| MinHeight           | Dp                 | Least height of the dial                                 | 98.dp                                  |
| CollapseDelayMillis | Long               | Delay after the last touch before asking to collapse     | 1500                                   |

| Function               | Return Type          | Description                              |
| ---------------------- | -------------------- | ---------------------------------------- |
| cameraZoomDialColors() | CameraZoomDialColors | The fixed light-on-dark camera palette   |

## Colors Classes

### CameraZoomDialColors

| Property Name  | Type  | Description                                  | Default   |
| -------------- | ----- | -------------------------------------------- | --------- |
| containerColor | Color | Dome                                         | #7F000000 |
| borderColor    | Color | Rim of the dome                              | #1AFFFFFF |
| tickColor      | Color | Ticks; minor ticks and the band use 15% of it | White     |
| labelColor     | Color | Stop labels                                  | White     |
| indicatorColor | Color | Fixed tick over the current value            | #FB822A   |
| valueColor     | Color | Current value and its "×" sign               | #FB822A   |
