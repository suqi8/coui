# CameraZoomBar

`CameraZoomBar` is the inertial zoom control of the camera, the port of `camera_inertial_zoom_layout.xml`: an `InertiaZoomBar` joystick between a minus and a plus `InertialZoomButton`, with the current, minimum and maximum zoom ratios (`InertiaZoomRotableTextView`) shown above it while it is used.

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=cameraZoomBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.CameraZoomBar
import io.github.suqi8.coui.kmp.basic.CameraZoomBarDefaults
```

## Basic Usage

```kotlin
var zoom by remember { mutableFloatStateOf(1f) }

CameraZoomBar(
    zoom = zoom,
    onZoomChange = { zoom = it },
    minZoom = 0.6f,
    maxZoom = 20f,
)
```

The bar is 244dp x 46dp with a 34.5dp thumb ring, flanked by two 44dp buttons 3.33dp away; the labels row above is 90dp high and tucks 7.67dp under the bar. The whole control is about 339dp wide, so give it the full width of a phone.

### Interaction

The bar does not map positions to ratios: its thumb rests in the middle and works like a joystick.

- Dragging the thumb sideways (past the touch slop) zooms continuously, faster the farther the thumb is from the middle. Every 25ms the zoom moves by `(k / 5) * ((zoom - minZoom) / (20 - minZoom) * 0.4795 + 0.0152)`, where `k` runs from -5 at the start to +5 at the end of the bar.
- While dragging, the thumb grows to 35dp and the lines from 2dp to 4dp (300ms on (0.3, 0, 0.1, 1)), a white line joins the middle to the thumb, the thumb hides its ratio, and the labels fade in (250ms on (0.5, 0, 0.3, 1)), the current ratio scaling up from 50%.
- On release the thumb glides back to the middle in 333ms to 667ms depending on how far it went, its ratio fades back in (183ms), and the labels fade out.
- Hitting `minZoom` or `maxZoom` plays a haptic and dims the bar to 50% (180ms) until the drag turns back.
- A quick tap jumps the thumb to the finger, shows the labels for one second and glides back without zooming.
- Tapping a button steps the zoom by 0.1 in 100ms on (0, 0, 0.1, 1); holding it after a long press zooms continuously at the speed of `k` = ±2. The buttons dim to 50% while pressed.

`onZoomChange` receives every new ratio, coerced into `minZoom..maxZoom`; the control keeps no zoom of its own.

### Bar Only

```kotlin
CameraZoomBar(
    zoom = zoom,
    onZoomChange = { zoom = it },
    showStepButtons = false,
    showValueLabels = false,
)
```

## Properties

### CameraZoomBar Properties

| Property Name   | Type                | Description                                                  | Default Value                              | Required |
| --------------- | ------------------- | ------------------------------------------------------------ | ------------------------------------------ | -------- |
| zoom            | Float               | Current zoom ratio                                           | -                                          | Yes      |
| onZoomChange    | (Float) -> Unit     | Called with each new ratio while dragging, holding or stepping | -                                        | Yes      |
| modifier        | Modifier            | Modifier applied to the control                              | Modifier                                   | No       |
| enabled         | Boolean             | Whether the bar and the buttons accept input                 | true                                       | No       |
| minZoom         | Float               | Lowest zoom ratio                                            | CameraZoomBarDefaults.MinZoom              | No       |
| maxZoom         | Float               | Highest zoom ratio (greater than `minZoom`)                  | CameraZoomBarDefaults.MaxZoom              | No       |
| showStepButtons | Boolean             | Whether to show the minus and plus buttons                   | true                                       | No       |
| showValueLabels | Boolean             | Whether to show the ratios above the bar while it is used    | true                                       | No       |
| colors          | CameraZoomBarColors | Colors of the control                                        | CameraZoomBarDefaults.cameraZoomBarColors() | No      |

## Defaults Object

### CameraZoomBarDefaults

| Property Name        | Type  | Description                                     | Value   |
| -------------------- | ----- | ----------------------------------------------- | ------- |
| MinZoom              | Float | Default lowest ratio (the ultra wide lens)      | 0.6f    |
| MaxZoom              | Float | Default highest ratio                           | 20f     |
| StepRatio            | Float | Zoom step of a button tap                       | 0.1f    |
| HoldVelocity         | Float | Zoom speed `k` while a button is held           | 2f      |
| BarWidth             | Dp    | Bar width                                       | 244.dp  |
| BarHeight            | Dp    | Bar height                                      | 46.dp   |
| BarPadding           | Dp    | Bar inset at both ends                          | 2.dp    |
| BarHorizontalMargin  | Dp    | Gap between the bar and the buttons             | 3.33.dp |
| LineWidth            | Dp    | Track and highlight line width                  | 2.dp    |
| DraggingLineWidth    | Dp    | Line width while dragging                       | 4.dp    |
| ThumbStrokeWidth     | Dp    | Thumb ring width at rest                        | 1.dp    |
| ThumbSize            | Dp    | Thumb diameter at rest                          | 34.5.dp |
| DraggingThumbSize    | Dp    | Thumb diameter while dragging                   | 35.dp   |
| ThumbTextSize        | Dp    | Size of the ratio in the thumb                  | 12.dp   |
| ThumbSuffixSpacing   | Dp    | Gap before the small "×"                        | 1.dp    |
| SmallSuffixWidth     | Dp    | Small "×" width                                 | 8.dp    |
| SmallSuffixHeight    | Dp    | Small "×" height                                | 16.dp   |
| LargeSuffixWidth     | Dp    | Large "×" width                                 | 12.dp   |
| LargeSuffixHeight    | Dp    | Large "×" height                                | 20.dp   |
| LargeSuffixSpacing   | Dp    | Gap before the large "×"                        | 1.5.dp  |
| ButtonSize           | Dp    | Button size                                     | 44.dp   |
| ButtonCrossLength    | Dp    | Length of the button cross arms                 | 16.dp   |
| ButtonCrossWidth     | Dp    | Width of the button cross arms                  | 1.33.dp |
| ButtonShadowStep     | Dp    | Width of one ring of the button halo            | 0.33.dp |
| LabelsHeight         | Dp    | Height of the labels row                        | 90.dp   |
| LabelsOverlap        | Dp    | How far the labels row tucks under the bar      | 7.67.dp |
| ValueTextSize        | Dp    | Size of the current ratio                       | 18.dp   |
| ValueWidth           | Dp    | Width of the current ratio box                  | 60.dp   |
| MinMaxTextSize       | Dp    | Size of the minimum and maximum ratios          | 12.dp   |
| MinMaxMargin         | Dp    | Inset of the minimum and maximum ratios         | 22.dp   |
| TextOutlineWidth     | Dp    | Width of the text outline                       | 0.67.dp |

| Function              | Return Type         | Description                   |
| --------------------- | ------------------- | ----------------------------- |
| cameraZoomBarColors() | CameraZoomBarColors | The fixed light-on-dark palette |

## Colors Classes

### CameraZoomBarColors

| Property Name       | Type  | Description                                | Default   |
| ------------------- | ----- | ------------------------------------------ | --------- |
| trackColor          | Color | Track on both sides of the thumb           | #4DFFFFFF |
| trackShadowColor    | Color | Dark layer under the track                 | #33000000 |
| highlightColor      | Color | Line from the middle to the dragged thumb  | White     |
| thumbColor          | Color | Thumb ring                                 | White     |
| contentColor        | Color | Zoom ratios                                | White     |
| contentOutlineColor | Color | Outline of the zoom ratios                 | #66000000 |
| buttonIconColor     | Color | Minus and plus crosses                     | White     |
| buttonShadowColor   | Color | Halo of the crosses                        | Black     |
