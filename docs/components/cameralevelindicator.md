# CameraLevelIndicator

`CameraLevelIndicator` is the level (horizon) indicator of the camera viewfinder, the port of the camera's `GradienterAssistView` that `AssistViewPresenter` lays over the preview when the "Level" setting is on. A line in the middle of the frame turns with the device roll against two fixed ticks; once level it snaps straight, grows over the ticks and turns the accent color. Lying flat for top-down shots, two crosses replace the line. It can also show the camera's small center ring.

<div style="position: relative; height: 520px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=cameraLevelIndicator" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.CameraLevelIndicator
import io.github.suqi8.coui.kmp.basic.CameraLevelIndicatorDefaults
import io.github.suqi8.coui.kmp.basic.rememberCameraLevelState
```

## Basic Usage

Feed the indicator the device attitude, for example from the gravity sensor, and give it the viewfinder area:

```kotlin
Box(Modifier.fillMaxWidth().aspectRatio(3f / 4f)) {
    CameraPreview(Modifier.matchParentSize())
    CameraLevelIndicator(
        rollDegrees = roll,   // right edge lower: positive
        pitchDegrees = pitch, // 0 facing the horizon, 90 lying flat screen-up
        modifier = Modifier.matchParentSize(),
    )
}
```

- `rollDegrees` is the tilt of the device's horizontal axis against the horizon. Upright it is the rotation around the lens axis; lying flat it is the left/right tilt. Readings near ±90 hold the device in landscape.
- `pitchDegrees` is the forward tilt around the device's horizontal axis: 0 with the lens facing the horizon, 90 lying flat screen-up; past 90 the device is tipped beyond flat with its top edge lower.

### Upright

- The line is a third of the frame's narrow side long and 1.32dp thick, flanked by two fixed 16dp ticks 4dp away from its ends. It turns against the roll so it stays parallel to the real horizon.
- It is fully shown up to 5° of roll and fades out by 11°; it also fades out while the lens turns 30° to 40° away from the horizon. Past 45° of roll it switches to the landscape orientation.
- Within 1.2° of level the line snaps straight, grows over the ticks (which disappear), turns the accent color and plays a haptic tick (at most once a second). After 725ms of staying level the indicator fades out in 375ms and comes back at once when the device tilts again.

### Top-Down

Within 15° of lying flat on both axes (left again past 20°) two 32dp crosses replace the line: a white target cross fixed in the middle and an accent cross that moves toward the lower edge, 40dp away at 20° of tilt. Within 0.4° on both axes, once the moving cross has settled within 1dp of the middle, only the accent cross remains. The crosses are shown up to 5° from flat and fade out by 11°.

### Timing

Every appearance waits 400ms (none right after the device was level) and fades in over 200ms on (0.33, 0, 0.67, 1); leaving the range, or switching between upright and top-down, fades out in 250ms. Each new reading glides there over 60ms. The bars and crosses carry a 0.33dp outline of 30% black so they read on any preview.

### Center Mark

The camera's center indicator: a 6dp white ring around a 20% white disc, always visible while on. It can be shown with or without the level.

```kotlin
CameraLevelIndicator(
    rollDegrees = roll,
    pitchDegrees = pitch,
    showLevel = false,
    showCenterMark = true,
    modifier = Modifier.matchParentSize(),
)
```

### With the Grid

Share a `CameraLevelState` with a [CameraGridOverlay](./cameragridoverlay) over the same area and the level line cuts a 4.33dp gap into the grid lines it crosses, as the camera does:

```kotlin
val levelState = rememberCameraLevelState()

CameraGridOverlay(
    type = CameraGridType.Grid,
    levelState = levelState,
    modifier = Modifier.matchParentSize(),
)
CameraLevelIndicator(
    rollDegrees = roll,
    pitchDegrees = pitch,
    state = levelState,
    modifier = Modifier.matchParentSize(),
)
```

`levelState.isLevel` and `levelState.isTopDown` can also drive your own UI.

## Properties

### CameraLevelIndicator Properties

| Property Name         | Type                       | Description                                                   | Default Value                                          | Required |
| --------------------- | -------------------------- | ------------------------------------------------------------- | ------------------------------------------------------ | -------- |
| rollDegrees           | Float                      | Tilt of the device's horizontal axis, clockwise positive      | -                                                      | Yes      |
| pitchDegrees          | Float                      | Forward tilt: 0 facing the horizon, 90 lying flat screen-up   | -                                                      | Yes      |
| modifier              | Modifier                   | Modifier applied to the indicator; must give it a size        | Modifier                                               | No       |
| state                 | CameraLevelState           | State shared with a `CameraGridOverlay`                       | rememberCameraLevelState()                             | No       |
| showLevel             | Boolean                    | Whether to show the level line and the top-down crosses       | true                                                   | No       |
| showCenterMark        | Boolean                    | Whether to show the center ring                               | false                                                  | No       |
| hapticFeedbackEnabled | Boolean                    | Whether reaching level plays a haptic tick                    | true                                                   | No       |
| colors                | CameraLevelIndicatorColors | Colors of the indicator                                       | CameraLevelIndicatorDefaults.cameraLevelIndicatorColors() | No    |

### CameraLevelState

| Member    | Type    | Description                                                                      |
| --------- | ------- | -------------------------------------------------------------------------------- |
| isLevel   | Boolean | Whether the device is level (1.2° upright, 0.4° on both axes top-down)          |
| isTopDown | Boolean | Whether the indicator is in its top-down mode                                    |

## Defaults Object

### CameraLevelIndicatorDefaults

| Property Name           | Type  | Description                                          | Value   |
| ----------------------- | ----- | ---------------------------------------------------- | ------- |
| LineThickness           | Dp    | Thickness of the level line and the ticks            | 1.32.dp |
| TickLength              | Dp    | Length of each fixed tick                            | 16.dp   |
| TickMargin              | Dp    | Gap between the level line and each tick             | 4.dp    |
| OutlineWidth            | Dp    | Dark outline inside the bars                         | 0.33.dp |
| GridGapHeight           | Dp    | Height of the gap cut into the grid lines            | 4.33.dp |
| CrossSize               | Dp    | Size of the top-down crosses                         | 32.dp   |
| CrossMaxOffset          | Dp    | Travel of the moving cross at 20° of tilt            | 40.dp   |
| CrossSettleDistance     | Dp    | How close the moving cross must be to count as level | 1.dp    |
| CenterMarkDiameter      | Dp    | Outer diameter of the center ring                    | 6.dp    |
| CenterMarkInnerDiameter | Dp    | Diameter of the center disc                          | 4.66.dp |
| CenterMarkRingWidth     | Dp    | Width of the center ring                             | 0.67.dp |
| CenterMarkFillAlpha     | Float | Alpha of the center disc                             | 0.2f    |

| Function                     | Return Type                | Description                            |
| ---------------------------- | -------------------------- | -------------------------------------- |
| cameraLevelIndicatorColors() | CameraLevelIndicatorColors | The fixed light-on-dark camera palette |

## Colors Classes

### CameraLevelIndicatorColors

| Property Name    | Type  | Description                                             | Default   |
| ---------------- | ----- | ------------------------------------------------------- | --------- |
| lineColor        | Color | Level line and ticks while tilted                       | White     |
| levelColor       | Color | Level line once level, and the moving top-down cross    | #FB822A   |
| targetCrossColor | Color | Fixed top-down target cross                             | White     |
| outlineColor     | Color | Thin outline around the bars and the crosses            | #4D000000 |
| centerMarkColor  | Color | Center ring and disc                                    | White     |
