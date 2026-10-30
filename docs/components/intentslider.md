# IntentSlider

`IntentSlider` is the slim media progress bar of COUI, the port of COUI 17 `COUIIntentSeekBar`. It shows a playback position with an optional buffered (secondary) position, grows while touched, and can show a thumb while touched or paused. Use it for video, music and other playback progress; for picking a value use [Slider](./slider).

<div style="position: relative; height: 320px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=intentSlider" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.IntentSlider
import io.github.suqi8.coui.kmp.basic.IntentSliderSize
```

## Basic Usage

```kotlin
var position by remember { mutableFloatStateOf(0.3f) }

IntentSlider(
    value = position,
    onValueChange = { position = it },
    secondaryValue = 0.6f, // buffered position
)
```

Like a `match_parent` `COUIIntentSeekBar`, the bar fills the available width; it is at least `IntentSliderDefaults.MinHeight` high.

### Thumb and Playback State

With `showThumb` the thumb pops out while the bar is touched. While `paused` it stays out at its rest radius; when playback resumes it disappears.

```kotlin
IntentSlider(
    value = position,
    onValueChange = { position = it },
    showThumb = true,
    paused = !isPlaying,
)
```

### Sizes

```kotlin
IntentSlider(value = position, onValueChange = { position = it }, size = IntentSliderSize.Small)
IntentSlider(value = position, onValueChange = { position = it }, size = IntentSliderSize.Large)
```

### Over Media Content

Turn `followSystemTheme` off for the fixed white palette COUI uses over video and artwork. When a `LocalMaterialBackdrop` provider is present, the COUI 17 blur-blend background (`BlurBlendEffectRenderer`: a 30px blur, then `#66333333` lighten and `#4DFFFFFF` soft light) replaces the translucent background fill; without one the fill stays, COUI's non-blur look.

```kotlin
CompositionLocalProvider(LocalMaterialBackdrop provides provider) {
    IntentSlider(
        value = position,
        onValueChange = { position = it },
        followSystemTheme = false,
        showThumb = true,
    )
}
```

## Interaction

`IntentSlider` follows COUI 17 `COUIIntentSeekBar`:

- On touch down the track grows from its rest height to its pressed height (`IntentSliderSize`) on a response 0.5 / bounce 0 spring, and shrinks back on release. A release waits until the track has grown by 2dp (`getMinEnlargeHeight`), so a quick tap still shows the feedback.
- Dragging moves the progress by the finger's travel from wherever it was (`couiSeekBarIsFollowThumb = false`); with `followThumb = true` the progress chases the finger instead (response 0.15).
- A tap springs the progress to the tapped position (response 0.3); a fast release (at least 100px/s) keeps it gliding.
- While dragging, the whole bar follows the finger horizontally on the touch-motion rubber band (`TouchMotionEffectRenderer`) and springs back on release.
- The thumb grows to its pressed radius with a bounce (0.3), then either settles on its rest radius (paused) or disappears (response 0.4) once the track starts shrinking.
- Programmatic `value` updates are drawn at once (playback updates every frame); only the gestures animate.
- `IntentSlider` has no touch spotlight and no glitter: `COUIIntentSeekBar.initAdvancedFeatures` leaves both out.

## Properties

### IntentSlider Properties

| Property Name         | Type                              | Description                                                                                              | Default Value                          | Required |
| --------------------- | --------------------------------- | -------------------------------------------------------------------------------------------------------- | -------------------------------------- | -------- |
| value                 | Float                             | Current value, coerced into valueRange                                                                   | -                                      | Yes      |
| onValueChange         | (Float) -> Unit                   | Called with the new value while the user drags, taps or flings                                           | -                                      | Yes      |
| modifier              | Modifier                          | Modifier applied to the bar                                                                              | Modifier                               | No       |
| enabled               | Boolean                           | Whether the bar accepts input                                                                            | true                                   | No       |
| showThumb             | Boolean                           | Whether to draw the thumb while touched or paused                                                        | false                                  | No       |
| paused                | Boolean                           | Whether the media is paused: the thumb stays out at its rest radius                                      | false                                  | No       |
| followThumb           | Boolean                           | Whether a drag moves the progress to the finger instead of by the finger's travel                        | false                                  | No       |
| followSystemTheme     | Boolean                           | Whether the colors follow the theme; off selects the fixed media palette and allows the backdrop blur    | true                                   | No       |
| materialBackdrop      | Boolean                           | Whether the blur-blend background may replace the fill (only with followSystemTheme off and a provider)  | true                                   | No       |
| valueRange            | ClosedFloatingPointRange\<Float\> | Range of values                                                                                          | 0f..1f                                 | No       |
| secondaryValue        | Float?                            | Secondary (buffered) value drawn under the progress; null hides it                                       | null                                   | No       |
| onValueChangeFinished | (() -> Unit)?                     | Called at the end of a drag, a fling or a tap                                                            | null                                   | No       |
| size                  | IntentSliderSize                  | Track and thumb sizes                                                                                    | IntentSliderSize.Default               | No       |
| horizontalPadding     | Dp                                | Inset of the track from both ends                                                                        | IntentSliderDefaults.HorizontalPadding | No       |
| colors                | IntentSliderColors                | Colors of the bar                                                                                        | intentSliderColors() / fixedColors()   | No       |
| hapticEffect          | SliderHapticEffect                | Haptic feedback type                                                                                     | SliderDefaults.DefaultHapticEffect     | No       |

### IntentSliderSize

| Value   | Rest Height | Pressed Height | Thumb Radius | Pressed Thumb Radius |
| ------- | ----------- | -------------- | ------------ | -------------------- |
| Default | 4.dp        | 6.dp           | 4.dp         | 5.dp                 |
| Small   | 2.dp        | 10.dp          | 3.dp         | 8.dp                 |
| Large   | 6.dp        | 10.dp          | 5.dp         | 8.dp                 |

### IntentSliderDefaults Object

| Property Name     | Type                 | Description                                         | Value                                       |
| ----------------- | -------------------- | --------------------------------------------------- | ------------------------------------------- |
| HorizontalPadding | Dp                   | Inset of the track from both ends                   | 14.dp                                       |
| MinHeight         | Dp                   | Minimum height of the bar                           | 24.dp                                       |
| BackdropSpec      | MaterialBackdropSpec | The blur-blend background                           | 30px blur, #66333333 Lighten, #4DFFFFFF Softlight |

| Function Name        | Return Type        | Description                                                                                 |
| -------------------- | ------------------ | ------------------------------------------------------------------------------------------- |
| intentSliderColors() | IntentSliderColors | Theme colors: background/secondary quarternaryNeutral, progress onBackground, opaque thumb |
| fixedColors()        | IntentSliderColors | Media colors: #4DFFFFFF background, #8AFFFFFF progress, #29FFFFFF secondary, white thumb    |

### IntentSliderColors Class

| Property Name          | Type  | Description                               |
| ---------------------- | ----- | ----------------------------------------- |
| backgroundColor        | Color | Background capsule (the unplayed part)    |
| progressColor          | Color | Progress                                  |
| secondaryProgressColor | Color | Secondary (buffered) progress             |
| thumbColor             | Color | Thumb                                     |
| thumbShadowColor       | Color | Thumb shadow                              |

## Advanced Usage

### Custom Colors

Custom colors turn the blur-blend background off in COUI (`hasCustomUserColors`), so pass `materialBackdrop = false` with them.

```kotlin
IntentSlider(
    value = position,
    onValueChange = { position = it },
    followSystemTheme = false,
    materialBackdrop = false,
    colors = IntentSliderDefaults.fixedColors(progressColor = Color(0xFFFF8A00)),
)
```

### Seeking with a Playback Position

```kotlin
var position by remember { mutableFloatStateOf(0f) }
var seeking by remember { mutableStateOf(false) }

IntentSlider(
    value = position,
    onValueChange = {
        seeking = true
        position = it
    },
    onValueChangeFinished = {
        player.seekTo(position)
        seeking = false
    },
    showThumb = true,
    paused = !player.isPlaying,
)
```
