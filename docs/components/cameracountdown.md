# CameraCountdown

`CameraCountdown` is the self-timer countdown of the camera, the number `TimerSnapShotManager` puts over the preview while the timer shutter counts down. The same page covers the camera's other countdowns: `CameraExposureCountdown`, the "12.3s" readout of long exposures (`NightTimeView`), and `CameraCountdownRing`, the camera's progress ring (`RoundProgressBar`) and the draining ring of its recording-stop countdown (`ProgressRingView`).

<div style="position: relative; height: 360px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=cameraCountdown" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.CameraCountdown
import io.github.suqi8.coui.kmp.basic.CameraCountdownDefaults
import io.github.suqi8.coui.kmp.basic.CameraCountdownRing
import io.github.suqi8.coui.kmp.basic.CameraExposureCountdown
```

## Basic Usage

```kotlin
CameraCountdown(remainingMillis = remainingMillis)
```

The number is the remaining time rounded up to whole seconds: a 100dp white number centered in a 175dp box. Nothing is drawn once the remaining time reaches zero.

### The Pulse

Every new number fades in over 200ms, holds, and from 800ms after it appeared fades out over 200ms, so each second blinks once (accelerate-decelerate, the camera's `ValueAnimator` default). Above 80% opacity the number grows a faint 2px black glow, up to 20% black.

### Compact

The camera moves the countdown to the top of the preview as a 26dp number in a 46dp disc of 50% black. Switching `compact` scales the number from its old size to the new one in 450ms on (0.3, 0, 0.1, 1):

```kotlin
CameraCountdown(remainingMillis = remainingMillis, compact = true)
```

### Long-exposure Countdown

```kotlin
CameraExposureCountdown(remainingMillis = exposureLeftMillis)
```

The remaining exposure reads in tenths of a second with a unit, for example "12.3s": 18dp white digits of weight 750 with a 30% black shadow, centered in a 94 x 22dp box. The hundreds and tens sit in fixed 11dp cells, the hundreds appearing from 100s and the tens from 10s. Showing fades it in over 250ms after a 150ms delay; hiding (`visible = false`) fades it out over 250ms.

### Progress Ring

```kotlin
CameraCountdownRing(
    progress = progress,
    modifier = Modifier.size(CameraCountdownDefaults.RingSize),
)
```

The default ring is `RoundProgressBar`: a 1.6dp 20% white track with an accent arc growing clockwise from 12 o'clock, squared off at its ends, gliding to each new progress in 320ms on (0.3, 0, 0.1, 1).

### Draining Ring

With `draining`, the ring is the recording-stop countdown of `ProgressRingView`: a 2dp 55% white track and an 85% white arc for the part still left, which shrinks clockwise from 12 o'clock as `progress` runs from 0 to 1. The camera drives it over 10 seconds after a 500ms delay on an accelerate-decelerate curve; content passed to the ring is centered inside it:

```kotlin
CameraCountdownRing(
    progress = CameraCountdownDefaults.DrainingRingEasing.transform(elapsedFraction),
    modifier = Modifier.size(CameraCountdownDefaults.DrainingRingSize),
    draining = true,
) {
    CameraRecordTimer(timeMillis = remainingMillis, countDown = true, showBackground = false)
}
```

## Properties

### CameraCountdown Properties

| Property Name   | Type                  | Description                              | Default Value                                    | Required |
| --------------- | --------------------- | ---------------------------------------- | ------------------------------------------------ | -------- |
| remainingMillis | Long                  | Time left before the shot                | -                                                | Yes      |
| modifier        | Modifier              | Modifier applied to the countdown        | Modifier                                         | No       |
| compact         | Boolean               | Whether to show the small 46dp disc      | false                                            | No       |
| colors          | CameraCountdownColors | Colors of the countdown                  | CameraCountdownDefaults.cameraCountdownColors()  | No       |

### CameraExposureCountdown Properties

| Property Name   | Type                          | Description                                  | Default Value                                           | Required |
| --------------- | ----------------------------- | -------------------------------------------- | ------------------------------------------------------- | -------- |
| remainingMillis | Long                          | Exposure time left                           | -                                                       | Yes      |
| modifier        | Modifier                      | Modifier applied to the countdown            | Modifier                                                | No       |
| visible         | Boolean                       | Whether the countdown is shown; changes fade | true                                                    | No       |
| suffix          | String                        | Unit after the number                        | CameraCountdownDefaults.ExposureSuffix                  | No       |
| colors          | CameraExposureCountdownColors | Colors of the countdown                      | CameraCountdownDefaults.cameraExposureCountdownColors() | No       |

### CameraCountdownRing Properties

| Property Name | Type                       | Description                                                   | Default Value                                         | Required |
| ------------- | -------------------------- | ------------------------------------------------------------- | ----------------------------------------------------- | -------- |
| progress      | Float                      | Progress in 0..1; for `draining`, the elapsed fraction        | -                                                     | Yes      |
| modifier      | Modifier                   | Modifier applied to the ring; give it a size                  | Modifier                                              | No       |
| draining      | Boolean                    | Whether to draw the part left instead of the part elapsed     | false                                                 | No       |
| animated      | Boolean                    | Whether progress changes glide (320ms)                        | !draining                                             | No       |
| strokeWidth   | Dp                         | Width of the track and the arc                                | RingStrokeWidth, or DrainingRingStrokeWidth           | No       |
| colors        | CameraCountdownRingColors  | Colors of the ring                                            | ringColors(), or drainingRingColors()                 | No       |
| content       | @Composable BoxScope.() -> Unit | Content centered in the ring                             | {}                                                    | No       |

## Defaults Object

### CameraCountdownDefaults

| Property Name                     | Type       | Description                                    | Value           |
| --------------------------------- | ---------- | ---------------------------------------------- | --------------- |
| LargeSize                         | Dp         | Large self-timer box                           | 175.dp          |
| LargeTextSize                     | Dp         | Large self-timer number size                   | 100.dp          |
| CompactSize                       | Dp         | Compact self-timer disc                        | 46.dp           |
| CompactTextSize                   | Dp         | Compact self-timer number size                 | 26.dp           |
| ExposureWidth                     | Dp         | Long-exposure countdown box width              | 94.dp           |
| ExposureHeight                    | Dp         | Long-exposure countdown box height             | 22.dp           |
| ExposureDigitWidth                | Dp         | Hundreds and tens cell width                   | 11.dp           |
| ExposurePointWidth                | Dp         | Decimal point cell width                       | 4.dp            |
| ExposureSuffixMargin              | Dp         | Margins around the unit                        | 3.dp            |
| ExposureTextSize                  | Dp         | Long-exposure digit size                       | 18.dp           |
| ExposureFontWeight                | FontWeight | Long-exposure digit weight                     | FontWeight(750) |
| ExposureShadowRadius              | Dp         | Long-exposure shadow radius                    | 1.33.dp         |
| ExposureSuffix                    | String     | Long-exposure unit                             | "s"             |
| RingStrokeWidth                   | Dp         | Progress ring stroke                           | 1.6.dp          |
| RingSize                          | Dp         | Progress ring size in the beauty menu          | 46.dp           |
| DrainingRingStrokeWidth           | Dp         | Recording-stop ring stroke                     | 2.dp            |
| DrainingRingSize                  | Dp         | Recording-stop ring size                       | 270.dp          |
| TinyDrainingRingStrokeWidth       | Dp         | Recording-stop ring stroke on small screens    | 1.5.dp          |
| TinyDrainingRingSize              | Dp         | Recording-stop ring size on small screens      | 156.dp          |
| RecordingStopCountdownMillis      | Int        | Recording-stop countdown length                | 10000           |
| RecordingStopCountdownDelayMillis | Int        | Delay before the recording-stop ring drains    | 500             |
| DrainingRingEasing                | Easing     | Curve of the recording-stop ring               | accelerate-decelerate |

| Function                        | Return Type                   | Description                                   |
| ------------------------------- | ----------------------------- | --------------------------------------------- |
| cameraCountdownColors()         | CameraCountdownColors         | The fixed self-timer palette                  |
| cameraExposureCountdownColors() | CameraExposureCountdownColors | The fixed long-exposure palette               |
| ringColors()                    | CameraCountdownRingColors     | The progress ring palette (accent arc)        |
| drainingRingColors()            | CameraCountdownRingColors     | The recording-stop ring palette               |

## Colors Classes

### CameraCountdownColors

| Property Name         | Type  | Description                                  | Default   |
| --------------------- | ----- | -------------------------------------------- | --------- |
| contentColor          | Color | Number                                       | White     |
| compactContainerColor | Color | Disc behind the compact number               | #7F000000 |
| shadowColor           | Color | Glow; its alpha follows the number's opacity | Black     |

### CameraExposureCountdownColors

| Property Name | Type  | Description                   | Default   |
| ------------- | ----- | ----------------------------- | --------- |
| contentColor  | Color | Digits, point and unit        | White     |
| shadowColor   | Color | Shadow behind them            | #4D000000 |

### CameraCountdownRingColors

| Property Name | Type  | Description            | ringColors() | drainingRingColors() |
| ------------- | ----- | ---------------------- | ------------ | -------------------- |
| trackColor    | Color | Full ring behind the arc | #33FFFFFF  | #8CFFFFFF            |
| progressColor | Color | Arc                    | #FB822A      | #D9FFFFFF            |
