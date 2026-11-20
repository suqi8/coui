# Slider

`Slider` is a basic interactive component in COUI used for selecting values within a continuous range. Users can adjust values by dragging the slider, making it suitable for scenarios such as volume adjustment, brightness control, and progress display.

COUI also provides `VerticalSlider` for vertical orientation and `RangeSlider` for selecting a range of values.

<div style="position: relative; height: 200px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=slider" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.Slider
import io.github.suqi8.coui.kmp.basic.VerticalSlider
import io.github.suqi8.coui.kmp.basic.RangeSlider
```

## Basic Usage

### Slider

```kotlin
var sliderValue by remember { mutableFloatStateOf(0.5f) }

Slider(
    value = sliderValue,
    onValueChange = { sliderValue = it }
)
```

### VerticalSlider

```kotlin
var sliderValue by remember { mutableFloatStateOf(0.5f) }

VerticalSlider(
    value = sliderValue,
    onValueChange = { sliderValue = it },
    modifier = Modifier.height(200.dp),
    text = "50%" // optional label at the top
)
```

### RangeSlider

```kotlin
var rangeValue by remember { mutableStateOf(0.2f..0.8f) }

RangeSlider(
    value = rangeValue,
    onValueChange = { rangeValue = it }
)
```

## Component States

### Disabled State

```kotlin
var value by remember { mutableFloatStateOf(0.5f) }

Slider(
    value = value,
    onValueChange = { value = it },
    enabled = false
)
```

## Haptic Feedback

`Slider`, `VerticalSlider` and `IntentSlider` vibrate like COUI 17 `COUISeekBar` (`VibrationFeedbackHandler`, on by default) whenever the integer progress changes: at either end (`EDGE_SHORT_VIBRATE`, 306), for every 1% a finger drags a continuous slider (`STEPPING_SHORT_VIBRATE`, 305) and for every section of a stepped one (`GRANULAR_SHORT_VIBRATE_SYNC`, 308). This is `SliderHapticEffect.Step`, their default. Pass `Edge` for the ends only or `None` to turn it off. See [SliderHapticEffect](../components/slider#sliderhapticeffect) for details.

Like COUI, only a finger dragging the slider and a tap on a stepped slider vibrate: a tap or a fling of a continuous slider and the snap of a stepped slider after a release are silent, even when they land on an end (`startClickTransitionAnim`, `onPhysicsFlingUpdate` and `snapToNearestSection` run no feedback).

```kotlin
var value by remember { mutableFloatStateOf(0.5f) }

Slider(
    value = value,
    onValueChange = { value = it },
    hapticEffect = SliderHapticEffect.Step
)
```

## Interaction

`Slider` follows COUI 17 `COUISeekBar`:

- On touch down the track grows from 20dp to 28dp and the thumb from 6dp to 8dp with a slight bounce (springs of response 0.5; the thumb grows with bounce 0.3). A release before the track has grown halfway waits until it has, so a quick tap still shows the feedback.
- A tap springs the progress to the tapped position (response 0.3); while dragging the progress follows the finger (response 0.15).
- A fast release (at least 100px/s) keeps the progress gliding, slowed by a linear damping of 15/s. Sliders with `steps` do not fling, as `COUISectionSeekBar`.
- Running into the maximum, by dragging or by a fling, flashes a white gradient over the progress (`GlitterEffectRenderer`). The gradient stays laid over the progress as it was at the end, so backing off right away uncovers its dimmer part. A fling stops at either end without overshooting, as the `FlingBehavior` frame spring is clamped to the track (`setFlingScale`).
- With `steps` the slider is a `COUISectionSeekBar`: the section marks are always drawn (except a mark the thumb covers whole, so none shows through the pressed glass thumb), a drag anchors on the section it started in and the thumb follows 60% of the finger within a section, each new section is reached with a 100ms snap on (0, 0, 0.25, 1), a release settles on a section in 150ms and a tap moves there in 200ms (`SectionAnimationHandler`).
- With `startFromMiddle` (`couiSeekBarStartMiddle`, continuous sliders only) the progress grows from the middle of the track to the thumb, the middle value holds the thumb until the finger has moved 20px, and running into either end flashes the glitter.
- While dragging, the whole slider follows the finger horizontally on the COUI 17 touch-motion rubber band (`TouchMotionEffectRenderer`: `(1 - 1 / (0.05·|dx| / 28dp + 1)) · 28dp`) and springs back on release (response 0.45, bounce 0.65).
- The layout is 1.4x of the track height (28dp by default, `COUISeekBarCore.onMeasure`), so the enlarged track stays inside the bounds; touches register within Compose's minimum touch target (48dp on Android) around it.

`RangeSlider` shares the press enlarge, the springs and the colors; the fling, the glitter and the material effects are `Slider` only.

`VerticalSlider` follows COUI 17 `COUIVerticalSeekBar`, a separate thin design: a 4dp track and progress with a 16dp thumb in the progress color. Once a drag passes the touch slop the track widens 6 times to 24dp and the progress 4 times to 16dp, in 183ms on the COUI ease curve, and the end padding goes from 10dp to 16dp. A tap moves the progress in `483ms × distance` (at least 150ms) on the COUI move-ease curve, a fast release keeps it gliding, and an optional `text` is drawn centered 6dp from the top. It is at least 36dp wide; give it a height.

### Material Effects

By default (`material = true`) `Slider` draws the effects ColorOS 17 adds with its system material switches on, as they are on real devices (`COUISeekBar` follows the spotlight switch through `onMaterialStateChange`): the pressed thumb turns to glass (`TexturedThumbRenderer`) and the `TYPE_OPAQUE_MEDIUM_1` touch spotlight (OVERLAY, intensity 0.6 light / 0.8 dark) follows the finger inside the track. Pass `material = false` to opt out. The spotlight needs runtime shaders (Android 13+ and every Skia target).

```kotlin
var value by remember { mutableFloatStateOf(0.5f) }

Slider(
    value = value,
    onValueChange = { value = it },
    material = false // opt out of the glass thumb and spotlight
)
```

## Properties

### Slider Properties

| Property Name         | Type                              | Description                                                                                                                                                                    | Default Value                      | Required |
| --------------------- | --------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ---------------------------------- | -------- |
| value                 | Float                             | Current value of the slider. If outside of valueRange provided, value will be coerced to this range                                                                            | -                                  | Yes      |
| onValueChange         | (Float) -> Unit                   | Callback when value changes                                                                                                                                                    | -                                  | Yes      |
| modifier              | Modifier                          | Modifier applied to the slider                                                                                                                                                 | Modifier                           | No       |
| enabled               | Boolean                           | Whether the slider is enabled                                                                                                                                                  | true                               | No       |
| valueRange            | ClosedFloatingPointRange\<Float\> | Range of values that this slider can take. The passed value will be coerced to this range                                                                                      | 0f..1f                             | No       |
| steps                 | Int                               | Amount of discrete allowable values. If 0, the slider will behave continuously. Must not be negative                                                                           | 0                                  | No       |
| onValueChangeFinished | (() -> Unit)?                     | Called when value change has ended                                                                                                                                             | null                               | No       |
| reverseDirection      | Boolean                           | Controls the direction of slider. When false (default), follows LayoutDirection (L-R in LTR, R-L in RTL). When true, reverses the direction.                                   | false                              | No       |
| startFromMiddle       | Boolean                           | Whether the progress of a continuous slider grows from the middle of the track (`couiSeekBarStartMiddle`)                                                                     | false                              | No       |
| height                | Dp                                | Rest height of the track; it grows to 1.4x while pressed, and the layout reserves that 1.4x height                                                                            | SliderDefaults.MinHeight           | No       |
| colors                | SliderColors                      | Color configuration of the slider                                                                                                                                              | SliderDefaults.sliderColors()      | No       |
| hapticEffect          | SliderDefaults.SliderHapticEffect | Type of haptic feedback. See [Haptic Feedback](#haptic-feedback)                                                                                                               | SliderHapticEffect.Step            | No       |
| showKeyPoints         | Boolean                           | Whether to draw the key points: the section marks of a stepped slider (always drawn by `COUISectionSeekBar`) or `keyPoints`                                                    | true                               | No       |
| keyPoints             | List\<Float\>?                    | Custom key point values to display on the slider. If null, uses step positions from steps parameter. Values should be within valueRange                                        | null                               | No       |
| magnetThreshold       | Float                             | Magnetic snap threshold as a fraction (0.0 to 1.0). When slider value is within this distance from a key point, it will snap to that point. Only applies when keyPoints is set | 0.02f                              | No       |
| material              | Boolean                           | Whether to draw the COUI 17 material effects (glass thumb while pressed, touch spotlight). See [Material Effects](#material-effects)                                         | true                               | No       |

### VerticalSlider Properties

| Property Name         | Type                              | Description                                                                                                                                                                    | Default Value                      | Required |
| --------------------- | --------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ---------------------------------- | -------- |
| value                 | Float                             | Current value of the slider                                                                                                                                                    | -                                  | Yes      |
| onValueChange         | (Float) -> Unit                   | Callback when value changes                                                                                                                                                    | -                                  | Yes      |
| modifier              | Modifier                          | Modifier applied to the slider                                                                                                                                                 | Modifier                           | No       |
| enabled               | Boolean                           | Whether the slider is enabled                                                                                                                                                  | true                               | No       |
| valueRange            | ClosedFloatingPointRange\<Float\> | Range of values that this slider can take                                                                                                                                      | 0f..1f                             | No       |
| steps                 | Int                               | Amount of discrete allowable values                                                                                                                                            | 0                                  | No       |
| onValueChangeFinished | (() -> Unit)?                     | Called when value change has ended                                                                                                                                             | null                               | No       |
| reverseDirection      | Boolean                           | Controls the direction of slider. When false, increases bottom to top. When true, increases top to bottom                                                                      | false                              | No       |
| text                  | String?                           | Optional label drawn centered 6dp from the top (`couiVerticalSeekBarText`)                                                                                                     | null                               | No       |
| textColor             | Color                             | Color of `text`                                                                                                                                                                | COUITheme.colorScheme.labelSecondary | No |
| colors                | SliderColors                      | Color configuration of the slider; the thumb uses the progress color                                                                                                           | SliderDefaults.sliderColors()      | No       |
| hapticEffect          | SliderDefaults.SliderHapticEffect | Type of haptic feedback. See [Haptic Feedback](#haptic-feedback)                                                                                                               | SliderHapticEffect.Step            | No       |
| showKeyPoints         | Boolean                           | Whether to show key point indicators on the slider (`COUIVerticalSeekBar` draws none)                                                                                          | false                              | No       |
| keyPoints             | List\<Float\>?                    | Custom key point values to display on the slider. If null, uses step positions from steps parameter. Values should be within valueRange                                        | null                               | No       |
| magnetThreshold       | Float                             | Magnetic snap threshold as a fraction (0.0 to 1.0). When slider value is within this distance from a key point, it will snap to that point. Only applies when keyPoints is set | 0.02f                              | No       |

### RangeSlider Properties

| Property Name         | Type                                        | Description                                                                                                                                                                    | Default Value                      | Required |
| --------------------- | ------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ---------------------------------- | -------- |
| value                 | ClosedFloatingPointRange\<Float\>           | Current values of the RangeSlider. If either value is outside of valueRange, it will be coerced                                                                                | -                                  | Yes      |
| onValueChange         | (ClosedFloatingPointRange\<Float\>) -> Unit | Lambda in which values should be updated                                                                                                                                       | -                                  | Yes      |
| modifier              | Modifier                                    | Modifier applied to the slider                                                                                                                                                 | Modifier                           | No       |
| enabled               | Boolean                                     | Whether the slider is enabled                                                                                                                                                  | true                               | No       |
| valueRange            | ClosedFloatingPointRange\<Float\>           | Range of values that Range Slider values can take                                                                                                                              | 0f..1f                             | No       |
| steps                 | Int                                         | Amount of discrete allowable values                                                                                                                                            | 0                                  | No       |
| onValueChangeFinished | (() -> Unit)?                               | Called when value change has ended                                                                                                                                             | null                               | No       |
| height                | Dp                                          | Rest height of the track; it grows to 1.4x while pressed, and the layout reserves that 1.4x height                                                                            | SliderDefaults.MinHeight           | No       |
| colors                | SliderColors                                | Color configuration of the slider                                                                                                                                              | SliderDefaults.sliderColors()      | No       |
| hapticEffect          | SliderDefaults.SliderHapticEffect           | Type of haptic feedback                                                                                                                                                        | SliderDefaults.DefaultHapticEffect | No       |
| showKeyPoints         | Boolean                                     | Whether to show key point indicators on the slider. Only works when keyPoints is not null                                                                                      | false                              | No       |
| keyPoints             | List\<Float\>?                              | Custom key point values to display on the slider. If null, uses step positions from steps parameter. Values should be within valueRange                                        | null                               | No       |
| magnetThreshold       | Float                                       | Magnetic snap threshold as a fraction (0.0 to 1.0). When slider value is within this distance from a key point, it will snap to that point. Only applies when keyPoints is set | 0.02f                              | No       |

### SliderDefaults Object

The SliderDefaults object provides default configurations for the Slider component.

#### Constants

| Constant Name       | Type               | Default Value           | Description                  |
| ------------------- | ------------------ | ----------------------- | ---------------------------- |
| MinHeight           | Dp                 | 20.dp                   | Default height of the slider |
| KeyPointRadius      | Dp                 | 3.0.dp                  | Default radius of key points |
| DefaultHapticEffect | SliderHapticEffect | SliderHapticEffect.Edge | Default haptic feedback type of `RangeSlider`; `Slider`, `VerticalSlider` and `IntentSlider` default to `Step` |

### VerticalSliderDefaults Object

| Constant Name          | Type  | Default Value | Description                                                                   |
| ---------------------- | ----- | ------------- | ----------------------------------------------------------------------------- |
| TrackWidth             | Dp    | 4.dp          | Rest width of the track and the progress                                      |
| TrackEnlargeScale      | Float | 6f            | Pressed track width scale (24dp)                                              |
| ProgressEnlargeScale   | Float | 4f            | Pressed progress width scale (16dp), also the thumb size                      |
| PaddingVertical        | Dp    | 10.dp         | Rest travel inset at each end                                                 |
| PressedPaddingVertical | Dp    | 4.dp          | Pressed track edge inset; the pressed travel inset adds the track radius     |
| MinWidth               | Dp    | 36.dp         | Minimum width of the slider                                                   |
| TextMarginTop          | Dp    | 6.dp          | Inset of `text` from the top edge                                             |

### SliderHapticEffect

| Value | Description                                                                                                       |
| ----- | ----------------------------------------------------------------------------------------------------------------- |
| None  | No haptic feedback                                                                                                |
| Edge  | Haptic feedback at the ends only                                                                                  |
| Step  | Haptic feedback at the ends and at steps: every 1% of a dragged continuous slider and every section of a stepped one |

#### Methods

| Method Name    | Return Type  | Description                         |
| -------------- | ------------ | ----------------------------------- |
| sliderColors() | SliderColors | Creates default color configuration |

### SliderColors Class

| Property Name           | Type  | Description                                   |
| ----------------------- | ----- | --------------------------------------------- |
| foregroundColor         | Color | Foreground color of the slider                |
| disabledForegroundColor | Color | Foreground color when disabled                |
| backgroundColor         | Color | Background color of the slider                |
| disabledBackgroundColor | Color | Background color when disabled                |
| thumbColor              | Color | Thumb color                                   |
| disabledThumbColor      | Color | Thumb color when disabled                     |
| keyPointColor           | Color | Color of key point indicators in background   |
| keyPointForegroundColor | Color | Color of key point indicators in foreground   |
| disabledKeyPointColor   | Color | Background key points when disabled           |
| disabledKeyPointForegroundColor | Color | Foreground key points when disabled   |
| thumbShadowColor        | Color | Thumb drop shadow, drawn only when enabled    |

`sliderColors()` follows COUI 17 `COUISeekBar`:

| Parameter                       | Default (light / dark)                                  | COUI source                                  |
| ------------------------------- | ------------------------------------------------------- | -------------------------------------------- |
| foregroundColor                 | `primary` (`#0080FF` / `#1A8CFF`)                       | `?couiColorContainerTheme`                   |
| disabledForegroundColor         | `disable` (`#4D0080FF` / `#4D1A8CFF`)     | `?couiColorDisable`                          |
| backgroundColor                 | `sliderBackground` (`#1F000000` / `#33FFFFFF`)          | `?couiColorContainer12`                      |
| disabledBackgroundColor         | `sliderBackground` (the track does not change)          | `coui_seekbar_background_selector`           |
| thumbColor                      | `onPrimary` (white)                                     | `coui_seekbar_thumb_selector`                |
| disabledThumbColor              | `disabledSliderThumb` (`#8AFFFFFF` / `#29FFFFFF`)       | `coui_seekbar_thumb_disable_color`           |
| keyPointColor                   | `labelTertiary` (`#42000000` / `#4DFFFFFF`)            | `?couiColorLabelTertiary`                    |
| keyPointForegroundColor         | `labelOnColor` (white)                      | `?couiColorLabelOnColor`                     |
| disabledKeyPointColor           | `disabledSliderKeyPoint` (`#42000000` / `#29FFFFFF`)    | `coui_seekbar_inactive_mark_disable_color`   |
| disabledKeyPointForegroundColor | `disabledSliderThumb` (`#8AFFFFFF` / `#29FFFFFF`)       | `coui_seekbar_active_mark_disable_color`     |
| thumbShadowColor                | `sliderThumbShadow` (`#1A000000` / `#40000000`)         | `coui_seekbar_thumb_shadow_color`            |

## Advanced Usage

### Custom Value Range

```kotlin
var temperature by remember { mutableFloatStateOf(25f) }

Column {
    Text("Temperature: ${temperature.roundToInt()}°C")
    Slider(
        value = temperature,
        onValueChange = { temperature = it },
        valueRange = 16f..32f
    )
}
```

### Discrete Steps

```kotlin
var rating by remember { mutableFloatStateOf(3f) }

Column {
    Text("Rating: ${rating.roundToInt()}/5")
    Slider(
        value = rating,
        onValueChange = { rating = it },
        valueRange = 1f..5f,
        steps = 3  // Creates 5 discrete values: 1, 2, 3, 4, 5
    )
}
```

### Custom Colors

```kotlin
var volume by remember { mutableFloatStateOf(0.7f) }

Slider(
    value = volume,
    onValueChange = { volume = it },
    colors = SliderDefaults.sliderColors(
        foregroundColor = Color.Red,
        backgroundColor = Color.LightGray
    )
)
```

### Custom Height

```kotlin
var brightness by remember { mutableFloatStateOf(0.8f) }

Slider(
    value = brightness,
    onValueChange = { brightness = it },
    height = 40.dp
)
```

### Slider with Haptic Feedback

```kotlin
var value by remember { mutableFloatStateOf(0.5f) }

Slider(
    value = value,
    onValueChange = { value = it },
    hapticEffect = SliderDefaults.SliderHapticEffect.Step
)
```

### VerticalSlider with Reverse Direction

```kotlin
var volume by remember { mutableFloatStateOf(0.5f) }

VerticalSlider(
    value = volume,
    onValueChange = { volume = it },
    modifier = Modifier.height(200.dp),
    reverseDirection = true  // Top to bottom
)
```

### RangeSlider for Price Filter

```kotlin
var priceRange by remember { mutableStateOf(100f..500f) }

Column {
    Text("Price: $${priceRange.start.roundToInt()} - $${priceRange.endInclusive.roundToInt()}")
    RangeSlider(
        value = priceRange,
        onValueChange = { priceRange = it },
        valueRange = 0f..1000f,
        steps = 99  // 101 discrete values from 0 to 1000
    )
}
```

### Complete Example with Value Change Callback

```kotlin
var value by remember { mutableFloatStateOf(0.5f) }
var finalValue by remember { mutableFloatStateOf(0.5f) }

Column {
    Text("Current: $value")
    Text("Final: $finalValue")
    Slider(
        value = value,
        onValueChange = { value = it },
        onValueChangeFinished = { finalValue = value },
        valueRange = 0f..100f,
        steps = 99
    )
}
```

### Slider with Custom Key Points

```kotlin
var value by remember { mutableFloatStateOf(50f) }

Column {
    Text("Value: ${value.roundToInt()}")
    Slider(
        value = value,
        onValueChange = { value = it },
        valueRange = 0f..100f,
        showKeyPoints = true,
        keyPoints = listOf(0f, 25f, 50f, 75f, 100f),
        magnetThreshold = 0.05f,  // 5% snap threshold
        hapticEffect = SliderDefaults.SliderHapticEffect.Step
    )
}
```

### RangeSlider with Key Points

```kotlin
var range by remember { mutableStateOf(20f..80f) }

Column {
    Text("Range: ${range.start.roundToInt()} - ${range.endInclusive.roundToInt()}")
    RangeSlider(
        value = range,
        onValueChange = { range = it },
        valueRange = 0f..100f,
        showKeyPoints = true,
        keyPoints = listOf(0f, 20f, 40f, 60f, 80f, 100f),
        magnetThreshold = 0.03f
    )
}
```
