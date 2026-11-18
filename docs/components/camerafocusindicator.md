# CameraFocusIndicator

`CameraFocusIndicator` is the tap-to-focus indicator of the camera with its vertical exposure slider, the port of the camera's `FocusIndicatorRotateLayout` and `ExposureSeekbarContainer`. It overlays the camera preview: a tap places four corner brackets with a sun beside them, a vertical drag anywhere over the preview moves the sun to adjust the exposure, and a padlock button next to the slider locks AE/AF.

<div style="position: relative; height: 420px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=cameraFocusIndicator" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.CameraFocusIndicator
import io.github.suqi8.coui.kmp.basic.CameraFocusIndicatorDefaults
import io.github.suqi8.coui.kmp.basic.rememberCameraFocusIndicatorState
```

## Basic Usage

Lay the indicator over the preview and give it the preview's size. A tap places it; read the exposure and the lock from the state or the callbacks.

```kotlin
val state = rememberCameraFocusIndicatorState()

Box(Modifier.fillMaxSize()) {
    CameraPreview(Modifier.fillMaxSize())
    CameraFocusIndicator(
        state = state,
        modifier = Modifier.fillMaxSize(),
        onFocus = { position -> camera.focusAt(position) },
        onExposureChange = { exposure -> camera.setExposure(exposure) }, // 0..1, 0.5 = no compensation
        onLockedChange = { locked -> camera.lockAeAf(locked) },
    )
}
```

The exposure is a fraction of the slider range, 0 (darkest) to 1 (brightest); every new focus resets it to 0.5 and releases the lock.

### Geometry

- The focus box is 70dp: four 1.5dp brackets with 20dp arms, each with a 30% black hairline outline. Its center is kept 45dp inside the edges (half the box plus a 10dp margin).
- The slider sits 55dp to the end of the box center, or to the start when the box is within 82dp of the end edge. The sun (a 5dp disc with eight 1.57dp rays reaching 9.35dp) travels 119.6dp; its hollow core fills up like a moon phase and its rays turn a full circle across the range.
- After the first drag a 1.76dp track line appears, cut 11.7dp around the sun, together with the 24dp padlock (30dp touch target) 88dp above the slider center, or below it when the box is within 103dp of the top edge.

### Animation

- Appearing: the indicator scales from 1.8x to 1x in 165ms on (0.3, 0, 1, 1) and fades in over 310ms on (0.33, 0, 0.67, 1); the brackets then blink once (alpha 1, 0.6, 1 in 300ms).
- Locking: the brackets pulse (scale 1, 1.3, 0.9, 1 in 500ms) and the brackets, the sun, the line and the padlock turn orange in 300ms, both on (0.33, 0, 0.67, 1); the padlock closes its shackle in 500ms. Unlocking returns to white at once.
- Auto hide (`autoHide`): 3s after a focus the indicator fades out (from 55% to 0 in 200ms on (0.3, 0, 0.1, 1)). After an exposure drag or a lock toggle it dims to 55% after 1.5s (300ms on (0.58, 0, 0.69, 1)) and fades out 4s later. A locked indicator stays, dimmed; a new drag restores it at once.

### Driving it from Code

```kotlin
state.focusAt(Offset(x, y))     // place it with the appear animation
state.adjustExposure(0.1f)      // like a drag: shows the line and the lock button
state.updateExposure(0.5f)      // set the value without the drag side effects
state.lock(true)           // like the lock button
state.hide()                    // fade it out now
```

### Label

The camera can show a label under the box, such as the focus or AE/AF lock hint:

```kotlin
CameraFocusIndicator(
    state = state,
    modifier = Modifier.fillMaxSize(),
    label = if (state.isLocked) "AF/AE lock" else null,
)
```

## Properties

### CameraFocusIndicator Properties

| Property Name    | Type                       | Description                                                        | Default Value                         | Required |
| ---------------- | -------------------------- | ------------------------------------------------------------------ | ------------------------------------- | -------- |
| state            | CameraFocusIndicatorState  | Position, exposure, lock and animations                            | -                                     | Yes      |
| modifier         | Modifier                   | Modifier applied to the indicator area; it must be given a size    | Modifier                              | No       |
| onFocus          | (Offset) -> Unit           | Called with the tap position when a tap places the indicator       | {}                                    | No       |
| onExposureChange | (Float) -> Unit            | Called whenever the exposure changes, including the reset to 0.5   | {}                                    | No       |
| onLockedChange   | (Boolean) -> Unit          | Called whenever the AE/AF lock changes                             | {}                                    | No       |
| enabled          | Boolean                    | Whether the indicator reacts to taps and drags                     | true                                  | No       |
| tapToFocus       | Boolean                    | Whether a tap places the indicator                                 | true                                  | No       |
| autoHide         | Boolean                    | Whether the indicator fades out on its own                         | true                                  | No       |
| showLockButton   | Boolean                    | Whether the AE/AF lock button appears after an exposure drag       | true                                  | No       |
| label            | String?                    | Label under the focus box, at most two lines                       | null                                  | No       |
| colors           | CameraFocusIndicatorColors | Colors of the indicator                                            | CameraFocusIndicatorDefaults.colors() | No       |

### CameraFocusIndicatorState

| Member                       | Type             | Description                                                        |
| ---------------------------- | ---------------- | ------------------------------------------------------------------ |
| focusPosition                | Offset?          | Requested center of the box (px), null before the first focus      |
| exposure                     | Float            | Exposure, 0..1                                                     |
| isLocked                     | Boolean          | Whether AE/AF is locked                                            |
| isExposureActive             | Boolean          | Whether the exposure was dragged since the last focus              |
| isVisible                    | Boolean          | Whether the indicator is shown                                     |
| focusAt(position)            | Unit             | Places the indicator, resets the exposure and the lock             |
| adjustExposure(delta)        | Unit             | Moves the exposure like a drag                                     |
| updateExposure(value)        | Unit             | Sets the exposure without the drag side effects                    |
| lock(locked)            | Unit             | Locks or releases AE/AF                                            |
| hide()                       | Unit             | Fades the indicator out                                            |

## Defaults Object

### CameraFocusIndicatorDefaults

| Property Name          | Type  | Description                                                  | Value    |
| ---------------------- | ----- | ------------------------------------------------------------ | -------- |
| IndicatorSize          | Dp    | Focus box size                                               | 70.dp    |
| EdgeMargin             | Dp    | Minimum distance between the box and the edges               | 10.dp    |
| SliderOffset           | Dp    | Distance from the box center to the slider                   | 55.dp    |
| SliderFlipDistance     | Dp    | End-edge distance below which the slider moves to the start  | 82.dp    |
| SliderTravel           | Dp    | Travel of the sun                                            | 119.6.dp |
| SliderLineWidth        | Dp    | Track line width                                             | 1.76.dp  |
| SliderLineGap          | Dp    | Gap between the sun center and the line                      | 11.7.dp  |
| SunRadius              | Dp    | Sun disc radius                                              | 5.03.dp  |
| SunCoreRadius          | Dp    | Radius of the hollow core at the darkest exposure            | 3.48.dp  |
| SunRayInnerRadius      | Dp    | Inner end of the rays                                        | 6.81.dp  |
| SunRayOuterRadius      | Dp    | Outer end of the rays                                        | 9.35.dp  |
| SunRayWidth            | Dp    | Ray width                                                    | 1.57.dp  |
| OutlineWidth           | Dp    | Hairline outline around the shapes                           | 0.33.dp  |
| LockButtonTouchSize    | Dp    | Touch size of the lock button                                | 30.dp    |
| LockIconSize           | Dp    | Lock icon size                                               | 24.dp    |
| LockButtonOffset       | Dp    | Vertical distance from the box center to the lock button     | 88.dp    |
| LockButtonFlipDistance | Dp    | Top-edge distance below which the lock button moves down     | 103.dp   |
| LabelTextSize          | Dp    | Label size (not scaled with the font scale)                  | 12.dp    |
| LabelShadowRadius      | Dp    | Label shadow radius                                          | 2.dp     |
| LabelMarginTop         | Dp    | Gap between the box and the label                            | 0.dp     |
| LabelMaxWidth          | Dp    | Width the label wraps within                                 | 120.dp   |
| AppearScale            | Float | Scale the indicator appears from                             | 1.8f     |
| DimmedAlpha            | Float | Alpha of a dimmed indicator                                  | 0.55f    |
| DefaultExposure        | Float | Exposure of a new focus                                      | 0.5f     |
| DragSensitivity        | Float | Part of the range one drag over the whole height covers      | 0.7f     |
| AutoHideDelayMillis    | Long  | Time after a focus before fading out                         | 3000L    |
| DimDelayMillis         | Long  | Time after an adjustment before dimming                      | 1500L    |
| DimmedHideDelayMillis  | Long  | Time a dimmed indicator stays before fading out              | 4000L    |

| Function | Return Type                | Description                         |
| -------- | -------------------------- | ----------------------------------- |
| colors() | CameraFocusIndicatorColors | The fixed light-on-dark camera palette |

## Colors Classes

### CameraFocusIndicatorColors

| Property Name    | Type  | Description                                   | Default   |
| ---------------- | ----- | --------------------------------------------- | --------- |
| indicatorColor   | Color | Brackets, sun, track line and padlock         | White     |
| lockedColor      | Color | The same shapes while AE/AF is locked         | #FB822A   |
| outlineColor     | Color | Hairline outline around the shapes            | #4D000000 |
| labelColor       | Color | Label under the box                           | White     |
| labelShadowColor | Color | Label shadow                                  | #33000000 |
