# CameraHint

`CameraHint` is the floating hint of the camera, the port of the camera's `CameraScreenHintView` and its `HintTextView`: a short white line such as "Hold the shutter to shoot a burst" over the viewfinder, on a small dark panel that fades in and out. `CameraOutlinedText`, the port of the camera's `StrokeTextView`, draws text straight over the viewfinder with a thin dark outline so it stays readable over bright scenes.

<div style="position: relative; height: 320px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=cameraHint" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.CameraHint
import io.github.suqi8.coui.kmp.basic.CameraHintBackground
import io.github.suqi8.coui.kmp.basic.CameraHintDefaults
import io.github.suqi8.coui.kmp.basic.CameraOutlinedText
```

## Basic Usage

```kotlin
var visible by remember { mutableStateOf(true) }

// The camera hides a hint 3 seconds after showing it.
LaunchedEffect(visible) {
    if (visible) {
        delay(CameraHintDefaults.AutoHideMillis)
        visible = false
    }
}

CameraHint(
    text = "Hold the shutter to shoot a burst",
    visible = visible,
)
```

A single line sits on a panel 24dp high with 4dp corners and 6dp of horizontal padding, in 12dp white text. Text wider than `maxWidth` (322dp) or holding a line break wraps and grows the panel, with 4dp of vertical padding and 4dp of extra spacing between lines, centered.

### Showing and Hiding

- Showing fades the hint in over 250ms on (0.33, 0, 0.67, 1), after `showDelayMillis`.
- Changing `text` while shown fades the new text in from transparent, as the camera does.
- Hiding fades the hint out in 100ms on the same curve; a fully hidden hint takes no space.
- The camera hides most hints `CameraHintDefaults.AutoHideMillis` (3000ms) after they appear. Drive `visible` from a timer to do the same.

### Backgrounds

| Background | Look                                                                     |
| ---------- | ------------------------------------------------------------------------ |
| Dark       | Default. A 50% black panel                                               |
| Light      | A 20% white panel                                                        |
| None       | No panel; the text casts a soft 70% black shadow instead                 |

```kotlin
CameraHint(
    text = "Point at the subject",
    background = CameraHintBackground.None,
)
```

### Material Blur

On devices that blur their hints, the panel is the camera's material blur (80dp radius, a glow overlay of a 15% grey over a 60% mix of 30% grey) under a 20% light grey fill. Pass a `MaterialBlurProvider` that records the content under the hint; `Dark` and `Light` both become the blur, and `None` ignores it.

```kotlin
CameraHint(
    text = "Hold the shutter to shoot a burst",
    materialBlur = provider, // records the viewfinder, not the hint
)
```

### Outlined Text

`CameraOutlinedText` draws the text twice: first as a 0.333dp outline of 40% black with round caps, then filled on top.

```kotlin
CameraOutlinedText(text = "Slide to switch filters")
```

`brush` fills the text with a gradient instead of `color`, like the camera's gradient guide titles:

```kotlin
CameraOutlinedText(
    text = "Light and shadow, retold",
    brush = Brush.horizontalGradient(CameraHintDefaults.GuideGradientColors),
    textSize = 20.dp,
    fontWeight = FontWeight.Bold,
    strokeWidth = 0.dp,
)
```

## Properties

### CameraHint Properties

| Property Name   | Type                  | Description                                                 | Default Value                       | Required |
| --------------- | --------------------- | ----------------------------------------------------------- | ----------------------------------- | -------- |
| text            | String                | The hint                                                    | -                                   | Yes      |
| modifier        | Modifier              | Modifier applied to the hint                                | Modifier                            | No       |
| visible         | Boolean               | Whether the hint is shown                                   | true                                | No       |
| background      | CameraHintBackground  | Panel behind the text                                       | CameraHintBackground.Dark           | No       |
| showDelayMillis | Int                   | Delay before the hint fades in                              | 0                                   | No       |
| materialBlur    | MaterialBlurProvider? | Paints the material blur behind the panel; null keeps the fill | null                             | No       |
| maxWidth        | Dp                    | Width past which the text wraps                             | CameraHintDefaults.MaxWidth         | No       |
| textSize        | Dp                    | Text size (dp, not scaled with the font scale)              | CameraHintDefaults.TextSize         | No       |
| colors          | CameraHintColors      | Colors of the hint                                          | CameraHintDefaults.cameraHintColors() | No     |

### CameraOutlinedText Properties

| Property Name | Type       | Description                                           | Default Value                       | Required |
| ------------- | ---------- | ----------------------------------------------------- | ----------------------------------- | -------- |
| text          | String     | The text                                              | -                                   | Yes      |
| modifier      | Modifier   | Modifier applied to the text                          | Modifier                            | No       |
| color         | Color      | Fill color                                            | Color.White                         | No       |
| brush         | Brush?     | Fill brush overriding `color`                         | null                                | No       |
| textSize      | Dp         | Text size (dp, not scaled with the font scale)        | CameraHintDefaults.OutlinedTextSize | No       |
| fontWeight    | FontWeight | Weight of the text                                    | FontWeight.Normal                   | No       |
| textAlign     | TextAlign  | Alignment of the lines                                | TextAlign.Center                    | No       |
| maxLines      | Int        | Maximum number of lines, the last one ellipsized      | Int.MAX_VALUE                       | No       |
| strokeWidth   | Dp         | Outline width; 0dp draws none                         | CameraHintDefaults.TextStrokeWidth  | No       |
| strokeColor   | Color      | Outline color                                         | CameraHintDefaults.TextStrokeColor  | No       |

## Defaults Object

### CameraHintDefaults

| Property Name       | Type               | Description                                    | Value                   |
| ------------------- | ------------------ | ---------------------------------------------- | ----------------------- |
| TextSize            | Dp                 | Hint text size                                 | 12.dp                   |
| MinHeight           | Dp                 | Panel height of a single line                  | 24.dp                   |
| CornerRadius        | Dp                 | Panel corner radius                            | 4.dp                    |
| HorizontalPadding   | Dp                 | Horizontal padding                             | 6.dp                    |
| VerticalPadding     | Dp                 | Vertical padding                               | 4.dp                    |
| LineSpacingExtra    | Dp                 | Extra spacing between wrapped lines            | 4.dp                    |
| MaxWidth            | Dp                 | Width past which the text wraps                | 322.dp                  |
| BlurRadius          | Dp                 | Material blur radius                           | 80.dp                   |
| ShowDuration        | Int                | Fade-in duration, ms                           | 250                     |
| HideDuration        | Int                | Fade-out duration, ms                          | 100                     |
| AutoHideMillis      | Long               | How long the camera shows a hint, ms           | 3000                    |
| TextStrokeWidth     | Dp                 | Outline width of `CameraOutlinedText`          | 0.333.dp                |
| TextStrokeColor     | Color              | Outline color of `CameraOutlinedText`          | #66000000               |
| OutlinedTextSize    | Dp                 | Text size of `CameraOutlinedText`              | 12.dp                   |
| GuideGradientColors | List&lt;Color&gt;  | Gradient of the guide titles                   | #ECECEC, #E987AB, #6687F9 |

| Function           | Return Type      | Description              |
| ------------------ | ---------------- | ------------------------ |
| cameraHintColors() | CameraHintColors | The fixed camera palette |

## Colors Class

### CameraHintColors

| Property Name       | Type  | Description                          | Default             |
| ------------------- | ----- | ------------------------------------ | ------------------- |
| textColor           | Color | Text                                 | White               |
| darkContainerColor  | Color | `Dark` panel                         | #7F000000           |
| lightContainerColor | Color | `Light` panel                        | #33FFFFFF           |
| textShadowColor     | Color | Text shadow of `None`                | #B3000000           |
| blurFillColor       | Color | Fill over the material blur          | #33A6A6A6           |
| blurMixColor        | Color | Material blur mix color              | 30% grey, 60% alpha |
| blurBlendColor      | Color | Material blur glow-overlay color     | 15% grey, 15% alpha |
