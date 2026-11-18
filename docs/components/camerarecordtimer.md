# CameraRecordTimer

`CameraRecordTimer` is the video recording timer of the camera, the port of `CameraTimeView` (`record_time.xml`). It shows the recorded time as `mm:ss`, or `hh:mm:ss` from one hour on, in white digits on the camera's red recording plate. Every digit sits in its own fixed cell, so the timer never changes width while it ticks.

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=cameraRecordTimer" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.CameraRecordTimer
import io.github.suqi8.coui.kmp.basic.CameraRecordTimerDefaults
```

## Basic Usage

```kotlin
CameraRecordTimer(timeMillis = recordedMillis)
```

The digits are 16dp, each centered in an 11 x 24dp cell, with the camera's two-dot colon glyph between minutes and seconds. The red plate (`#EB3B2F`) has 4dp corners and 6dp of padding at both sides. The hours and their colon only appear once the time reaches an hour, after a 4dp gap. Each digit carries a 0.33dp outline of 40% black so the white digits stay legible over a bright preview. The timer always lays its digits out left to right.

### Showing and Hiding

```kotlin
CameraRecordTimer(
    timeMillis = recordedMillis,
    visible = isRecording,
)
```

Showing fades the timer in over 400ms after a 150ms delay; hiding fades it out in 100ms, both on the COUI standard curve (0.33, 0, 0.67, 1). A hidden timer takes no space.

### Recording Dot

The camera's black-screen and time-lapse layouts drop the plate and lead with a blinking red dot (8dp, 4dp gap) that toggles every 486ms, the video mode's recording tick:

```kotlin
CameraRecordTimer(
    timeMillis = recordedMillis,
    showBackground = false,
    showRecordingDot = true,
)
```

### Count-down

For a video count-down, pass the remaining time and set `countDown`; partial seconds then round up, so the timer reads 00:10 until the last second has fully passed:

```kotlin
CameraRecordTimer(timeMillis = remainingMillis, countDown = true)
```

### Light Weight

The camera uses its regular `SysSans-En` weight 400 by default and weight 266 in some modes:

```kotlin
CameraRecordTimer(
    timeMillis = recordedMillis,
    fontWeight = CameraRecordTimerDefaults.LightFontWeight,
)
```

## Properties

### CameraRecordTimer Properties

| Property Name    | Type                    | Description                                                   | Default Value                                       | Required |
| ---------------- | ----------------------- | ------------------------------------------------------------- | --------------------------------------------------- | -------- |
| timeMillis       | Long                    | Recorded time, or remaining time when `countDown`             | -                                                   | Yes      |
| modifier         | Modifier                | Modifier applied to the timer                                 | Modifier                                            | No       |
| visible          | Boolean                 | Whether the timer is shown; changes fade it                   | true                                                | No       |
| countDown        | Boolean                 | Whether `timeMillis` is a remaining time (seconds round up)   | false                                               | No       |
| showBackground   | Boolean                 | Whether to draw the red recording plate                       | true                                                | No       |
| showRecordingDot | Boolean                 | Whether to lead with the blinking recording dot               | false                                               | No       |
| fontWeight       | FontWeight              | Weight of the digits                                          | CameraRecordTimerDefaults.RegularFontWeight         | No       |
| colors           | CameraRecordTimerColors | Colors of the timer                                           | CameraRecordTimerDefaults.cameraRecordTimerColors() | No       |

## Defaults Object

### CameraRecordTimerDefaults

| Property Name       | Type       | Description                         | Value           |
| ------------------- | ---------- | ----------------------------------- | --------------- |
| DigitWidth          | Dp         | Digit cell width                    | 11.dp           |
| DigitHeight         | Dp         | Digit cell height                   | 24.dp           |
| TextSize            | Dp         | Digit size (not font-scaled)        | 16.dp           |
| CornerRadius        | Dp         | Plate corner radius                 | 4.dp            |
| HorizontalPadding   | Dp         | Plate padding at both sides         | 6.dp            |
| HourMarginStart     | Dp         | Gap before the hours                | 4.dp            |
| StrokeWidth         | Dp         | Digit outline width                 | 0.333.dp        |
| RecordingDotSize    | Dp         | Recording dot size                  | 8.dp            |
| RecordingDotGap     | Dp         | Gap after the recording dot         | 4.dp            |
| BlinkIntervalMillis | Int        | Recording dot toggle interval       | 486             |
| RegularFontWeight   | FontWeight | Regular digit weight                | FontWeight.Normal |
| LightFontWeight     | FontWeight | Light digit weight                  | FontWeight(266) |

| Function                  | Return Type             | Description                          |
| ------------------------- | ----------------------- | ------------------------------------ |
| cameraRecordTimerColors() | CameraRecordTimerColors | The fixed light-on-dark camera palette |

## Colors Classes

### CameraRecordTimerColors

| Property Name   | Type  | Description                 | Default   |
| --------------- | ----- | --------------------------- | --------- |
| backgroundColor | Color | Recording plate             | #EB3B2F   |
| contentColor    | Color | Digits and colons           | White     |
| strokeColor     | Color | Outline around the digits   | #66000000 |
| dotColor        | Color | Blinking recording dot      | #E32E27   |
