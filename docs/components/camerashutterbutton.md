# CameraShutterButton

`CameraShutterButton` is the main shutter of the camera, the port of the camera's `MainShutterButton`: a 66dp glass circle with a white (or accent) disc for photos and a red disc for videos, which turns into a rounded square while recording. It also shows a capture progress arc, a loading spinner and a self-timer label. `CameraSwitchButton`, the round control button next to it (switch camera, pause / resume, capture while recording), is documented on this page too.

<div style="position: relative; height: 320px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=cameraShutterButton" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.CameraShutterButton
import io.github.suqi8.coui.kmp.basic.CameraShutterMode
import io.github.suqi8.coui.kmp.basic.CameraShutterStyle
import io.github.suqi8.coui.kmp.basic.CameraSwitchButton
import io.github.suqi8.coui.kmp.basic.CameraSwitchButtonDefaults
```

## Basic Usage

```kotlin
CameraShutterButton(
    onClick = { takePhoto() },
)
```

The shutter is 66dp: a 33dp ring around a 27dp-radius disc. It always uses its fixed light-on-dark palette, since it sits over the camera preview.

### Video and Recording

```kotlin
var recording by remember { mutableStateOf(false) }

CameraShutterButton(
    onClick = { recording = !recording },
    mode = CameraShutterMode.Video,
    recording = recording,
)
```

- Switching `mode` cross-fades the disc between white and red in 200ms on (0.3, 0, 0.1, 1).
- Starting a recording morphs the red disc into a 30dp square with 8dp corners in 300ms on (0.3, 0, 0.1, 1); stopping morphs it back.

### Interaction

- Pressing shrinks the disc (or square, or label) to 80% in 350ms and dims the ring to 50%; releasing grows it back in 300ms, both on (0, 0, 0.1, 1).
- In video mode the press feedback waits 120ms and the release 100ms, so a quick tap only starts or stops the recording.
- Touches within 3dp of the edge are ignored.
- `onLongClick` is called once the shutter has been held for the long-press timeout (the camera starts a burst or a quick recording); the tap then no longer calls `onClick`.
- A disabled shutter is drawn at 40%.

### Capture Progress

A capture that takes time, such as a long exposure, passes its progress:

```kotlin
CameraShutterButton(
    onClick = {},
    captureProgress = progress, // 0f..1f, null when no such capture runs
)
```

The disc morphs into the square, the ring dims to 30% in 180ms and a 4dp white arc fills from the top. When `captureProgress` returns to null the disc is shown again at once.

### Loading and Timer

```kotlin
CameraShutterButton(onClick = {}, loading = true)    // saving / processing
CameraShutterButton(onClick = {}, timerLabel = "3")  // the self-timer
```

- `loading` fades the disc out and spins a 4dp arc fading from transparent to 40% white around its edge, one turn every 700ms.
- `timerLabel` shrinks the disc away and grows the label (24dp, not scaled with the font scale) from 80% while fading it in, all in 300ms.

### Styles

| Style  | Look                                                                                         |
| ------ | -------------------------------------------------------------------------------------------- |
| Glass  | White disc on a blurred glass circle, edged by a 0.9dp ring fading in towards the bottom      |
| Accent | Glass with an orange gradient disc (#F99018 to #FF6B00), a soft orange halo and the edge light |
| Ring   | The camera without render effects: a 4dp white ring with a faint dark outline, no glass       |

```kotlin
CameraShutterButton(
    onClick = {},
    style = CameraShutterStyle.Accent,
)
```

### Material Blur

Over the camera preview the glass circle is a material blur (30dp radius, a glow overlay of a 15% grey over a 60% mix of 30% grey). Pass a `MaterialBlurProvider` that records the content under the shutter; without one the circle is a 50% black fill.

```kotlin
CameraShutterButton(
    onClick = {},
    materialBlur = provider, // records the viewfinder, not the shutter
)
```

## CameraSwitchButton

The round control button: a glass circle with a 24dp white icon. With its default icon it is the 40dp camera switch; with `CameraSwitchButtonDefaults.PauseIcon` / `ResumeIcon` it is the 48dp pause / resume button of a recording, and with `CaptureIcon` the button that takes a photo while recording.

```kotlin
var paused by remember { mutableStateOf(false) }

CameraSwitchButton(onClick = { switchCamera() }, iconRotation = orientation)

CameraSwitchButton(
    onClick = { paused = !paused },
    icon = if (paused) CameraSwitchButtonDefaults.ResumeIcon else CameraSwitchButtonDefaults.PauseIcon,
    size = CameraSwitchButtonDefaults.ControlSize,
)
```

- Pressing fades the button to 50% in 200ms and back in 340ms, both on (0.33, 0, 0.67, 1); a disabled button is drawn at 40%.
- Changing `icon` shrinks the old icon to 80% while fading it out in 200ms, then grows and fades in the new one in 200ms.
- Changing `iconRotation` (the device orientation) turns the icon the short way round at 270 degrees per second.
- With a `materialBlur` the circle is the camera material blur (80dp radius, a glow overlay of a 40% light grey over a 60% mix of 15% grey); otherwise a 50% black fill.

## Properties

### CameraShutterButton Properties

| Property Name   | Type                  | Description                                                          | Default Value                              | Required |
| --------------- | --------------------- | -------------------------------------------------------------------- | ------------------------------------------ | -------- |
| onClick         | () -> Unit            | Called when the shutter is tapped                                    | -                                          | Yes      |
| modifier        | Modifier              | Modifier applied to the shutter                                      | Modifier                                   | No       |
| onLongClick     | (() -> Unit)?         | Called once the shutter has been held for the long-press timeout     | null                                       | No       |
| enabled         | Boolean               | Whether the shutter accepts input                                    | true                                       | No       |
| mode            | CameraShutterMode     | Photo or Video, picks the disc color                                 | CameraShutterMode.Photo                    | No       |
| recording       | Boolean               | Whether a video is being recorded (the red square)                   | false                                      | No       |
| captureProgress | Float?                | Progress of a running capture, 0..1, drawn as an arc; null for none  | null                                       | No       |
| loading         | Boolean               | Whether the camera is busy: a spinner replaces the disc              | false                                      | No       |
| timerLabel      | String?               | A short label shown in place of the disc, such as "3"                | null                                       | No       |
| style           | CameraShutterStyle    | Glass, Accent or Ring                                                | CameraShutterStyle.Glass                   | No       |
| materialBlur    | MaterialBlurProvider? | Paints the material blur behind the glass; null keeps the fill       | null                                       | No       |
| colors          | CameraShutterColors   | Colors of the shutter                                                | CameraShutterDefaults.cameraShutterColors(style) | No |

### CameraSwitchButton Properties

| Property Name      | Type                     | Description                                              | Default Value                                      | Required |
| ------------------ | ------------------------ | -------------------------------------------------------- | -------------------------------------------------- | -------- |
| onClick            | () -> Unit               | Called when the button is tapped                         | -                                                  | Yes      |
| modifier           | Modifier                 | Modifier applied to the button                           | Modifier                                           | No       |
| enabled            | Boolean                  | Whether the button accepts input                         | true                                               | No       |
| icon               | ImageVector              | The icon                                                 | CameraSwitchButtonDefaults.SwitchIcon              | No       |
| contentDescription | String?                  | Accessibility label                                      | null                                               | No       |
| iconRotation       | Float                    | Rotation of the icon in degrees (device orientation)     | 0f                                                 | No       |
| size               | Dp                       | Diameter of the button                                   | CameraSwitchButtonDefaults.Size                    | No       |
| iconSize           | Dp                       | Size the icon is drawn at                                | CameraSwitchButtonDefaults.IconSize                | No       |
| materialBlur       | MaterialBlurProvider?    | Paints the material blur behind the circle               | null                                               | No       |
| colors             | CameraSwitchButtonColors | Colors of the button                                     | CameraSwitchButtonDefaults.cameraSwitchButtonColors() | No    |

## Defaults Object

### CameraShutterDefaults

| Property Name           | Type  | Description                                       | Value   |
| ----------------------- | ----- | ------------------------------------------------- | ------- |
| Size                    | Dp    | Shutter size                                      | 66.dp   |
| OuterRadius             | Dp    | Ring radius                                       | 33.dp   |
| InnerRadius             | Dp    | Disc radius                                       | 27.dp   |
| InnerSquareSize         | Dp    | Side of the recording square                      | 30.dp   |
| InnerSquareCornerRadius | Dp    | Corner radius of the recording square             | 8.dp    |
| RingWidth               | Dp    | Hairline ring of the glass styles                 | 0.9.dp  |
| DialWidth               | Dp    | Ring of the Ring style, progress arc, spinner     | 4.dp    |
| RingOutlineExtraWidth   | Dp    | Extra width of the Ring style's dark outline      | 0.33.dp |
| TouchInset              | Dp    | Edge band in which touches are ignored            | 3.dp    |
| BlurRadius              | Dp    | Material blur radius                              | 30.dp   |
| HaloRadius              | Dp    | Radius of the accent halo                         | 19.dp   |
| HaloOffsetY             | Dp    | Offset of the accent halo below the center        | 10.dp   |
| HaloBlurRadius          | Dp    | Blur of the accent halo                           | 28.dp   |
| TimerTextSize           | Dp    | Size of the timer label                           | 24.dp   |
| TimerShadowRadius       | Dp    | Shadow blur of the timer label                    | 2.dp    |
| PressedScale            | Float | Disc scale while pressed                          | 0.8f    |
| DisabledAlpha           | Float | Alpha of a disabled shutter                       | 0.4f    |

| Function                     | Return Type         | Description                                  |
| ---------------------------- | ------------------- | -------------------------------------------- |
| cameraShutterColors(style)   | CameraShutterColors | The fixed light-on-dark palette of a style   |

### CameraSwitchButtonDefaults

| Property Name   | Type        | Description                                         | Value   |
| --------------- | ----------- | --------------------------------------------------- | ------- |
| Size            | Dp          | Camera switch button size                           | 40.dp   |
| ControlSize     | Dp          | Pause / resume and capture button size              | 48.dp   |
| IconSize        | Dp          | Icon size                                           | 24.dp   |
| CaptureIconSize | Dp          | Size of the capture-while-recording glyph           | 56.dp   |
| BlurRadius      | Dp          | Material blur radius                                | 80.dp   |
| PressedAlpha    | Float       | Alpha while pressed                                 | 0.5f    |
| DisabledAlpha   | Float       | Alpha of a disabled button                          | 0.4f    |
| SwitchIcon      | ImageVector | Camera switch arrows                                | -       |
| PauseIcon       | ImageVector | Pause bars                                          | -       |
| ResumeIcon      | ImageVector | Resume triangle                                     | -       |
| CaptureIcon     | ImageVector | Small shutter of the capture-while-recording button | -       |

| Function                   | Return Type              | Description                         |
| -------------------------- | ------------------------ | ----------------------------------- |
| cameraSwitchButtonColors() | CameraSwitchButtonColors | The fixed light-on-dark palette     |

## Colors Classes

### CameraShutterColors

| Property Name         | Type  | Description                            | Default                          |
| --------------------- | ----- | -------------------------------------- | -------------------------------- |
| containerColor        | Color | Glass circle without material blur     | #7F000000                        |
| blurMixColor          | Color | Material blur mix color                | 30% grey, 60% alpha              |
| blurBlendColor        | Color | Material blur glow-overlay color       | 15% grey, 15% alpha              |
| ringTopColor          | Color | Ring at the top                        | #00D9D9D9 (Ring: White)          |
| ringBottomColor       | Color | Ring at 80% of the height and below    | #33D9D9D9 (Ring: White)          |
| ringOutlineColor      | Color | Dark outline of the Ring style         | #4D000000                        |
| photoTopColor         | Color | Photo disc down to 25% of its height   | White (Accent: #F99018)          |
| photoBottomColor      | Color | Photo disc at its bottom               | White (Accent: #FF6B00)          |
| videoColor            | Color | Video disc and recording square        | #E32127                          |
| haloColor             | Color | Halo below the accent disc             | #B3FF5900                        |
| progressColor         | Color | Progress arc                           | White                            |
| loadingColor          | Color | Bright end of the spinner              | #66FFFFFF                        |
| timerLabelColor       | Color | Timer label                            | White                            |
| timerLabelShadowColor | Color | Shadow of the timer label              | #33000000                        |

### CameraSwitchButtonColors

| Property Name  | Type  | Description                        | Default              |
| -------------- | ----- | ---------------------------------- | -------------------- |
| containerColor | Color | Circle without material blur       | #7F000000            |
| blurMixColor   | Color | Material blur mix color            | 15% grey, 60% alpha  |
| blurBlendColor | Color | Material blur glow-overlay color   | 65% grey, 40% alpha  |
| iconColor      | Color | Icon tint                          | White                |
