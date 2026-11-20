# IntentSlider

`IntentSlider` is the slim media progress bar of COUI, the port of COUI 17 `COUIIntentSeekBar`. It shows a playback position with an optional buffered (secondary) position, grows while touched, and can show a thumb while touched or paused. Use it for video, music and other playback progress; for picking a value use [Slider](./slider).

<div style="position: relative; height: 320px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=intentSlider" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.IntentSlider
import io.github.suqi8.coui.kmp.basic.IntentSliderPlaybackState
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

`playbackState` (`couiIntentSeekBarPlaybackState`) drives the playback visuals. With `showThumb` the thumb pops out while the bar is touched; while `Paused` it stays out at its rest radius, and when playback resumes it disappears.

```kotlin
IntentSlider(
    value = position,
    onValueChange = { position = it },
    showThumb = true,
    playbackState = if (isPlaying) IntentSliderPlaybackState.Playing else IntentSliderPlaybackState.Paused,
)
```

| State            | Visual (`resolveVisualState`)                                                                             |
| ---------------- | --------------------------------------------------------------------------------------------------------- |
| Idle             | Plain bar                                                                                                 |
| LoadingInitial   | A white light (60% alpha at its middle, 0.6 of the track long) sweeps across the whole bar; no progress  |
| Playing          | Plain bar; with the conic light style the light trails the progress end                                   |
| Paused           | Plain bar; the thumb (with `showThumb`) stays out                                                         |
| LoadingBuffering | The progress, with the light sweeping from the progress end to the end of the bar; no secondary progress  |

The sweeps repeat on a response 0.7 / bounce 0 spring (`LoadingEffectRenderer`); leaving `LoadingInitial` lets the running sweep finish its pass.

### Dragging While Loading

With `loadingDragReturn` (`setLoadingDragReturnEnabled`), a drag that starts while `LoadingInitial` shows the progress and lets the user scrub; on release the progress springs back to the start of `valueRange` (response 0.3, `LoadingDragReturnHandler`) without a fling, `onValueChange` receives the start value, and `onLoadingDragReturnComplete` runs once the spring settles.

```kotlin
IntentSlider(
    value = position,
    onValueChange = { position = it },
    playbackState = IntentSliderPlaybackState.LoadingInitial,
    loadingDragReturn = true,
    onLoadingDragReturnComplete = { /* the bar shows the loading sweep again */ },
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

### Conic Light

Over media, `conicLight = true` (`couiIntentSeekBarConicLight`) switches to the COUI 17 conic light progress style. Like `isConicLightProgressStyle` it only applies with `followSystemTheme = false`.

- The progress takes `IntentSliderColors.conicLightColor` (#F87796), with a white gradient over its end (`IntentConicProgressRenderer`): a fifth of the track while `Playing`, a tenth while dragging, the whole progress near the start; it fades in and out on a response 0.5 spring.
- While `Playing`, and while dragging during playback, two cones of light trail the progress end, spreading vertically as they go back (`ConicLightEffectRenderer`, AGSL `ConicLightShaderParams`, drawn with `BlendMode.Screen` and clipped to the bar). Entering playback springs the shape in with response 0.9 and the alpha with 0.3; dragging and fading out use 0.4.
- The light needs runtime shaders (Android 13+ and every Skia target) and is skipped elsewhere; the progress style is drawn everywhere.
- While a hold-to-speed-up gesture of the player is active, pass `holdToSpeedUp = true` (`setHoldToSpeedUpConeLightActive`): during `Playing` the cone light then takes its dragging shape, as during a drag, and returns to the playing shape when it is set back.

```kotlin
IntentSlider(
    value = position,
    onValueChange = { position = it },
    followSystemTheme = false,
    conicLight = true,
    holdToSpeedUp = isSpeedingUp,
    playbackState = IntentSliderPlaybackState.Playing,
)
```

## Interaction

`IntentSlider` follows COUI 17 `COUIIntentSeekBar`:

- On touch down the track grows from its rest height to its pressed height (`IntentSliderSize`) on a response 0.5 / bounce 0 spring, and shrinks back on release. A release waits until the track has grown by 2dp (`getMinEnlargeHeight`), so a quick tap still shows the feedback.
- Dragging moves the progress by the finger's travel from wherever it was (`couiSeekBarIsFollowThumb = false`); with `followThumb = true` the progress chases the finger instead. Either way the drawn progress trails the dragged value on the response 0.15 follow-hand spring (`FlexibleFollowHandHandler`).
- A tap springs the progress to the tapped position (response 0.3); a fast release (at least 100px/s) keeps it gliding.
- Only drags vibrate: taps and flings are silent, as `startClickTransitionAnim` and `onPhysicsFlingUpdate` run no feedback.
- While dragging, the whole bar follows the finger horizontally on the touch-motion rubber band (`TouchMotionEffectRenderer`) and springs back on release. `onDragHorizontalOffsetChange` receives that offset every frame (`OnDragHorizontalOffsetChangeListener`), to move labels along with the bar.
- The thumb grows to its pressed radius with a bounce (0.3), then either settles on its rest radius (paused) or disappears (response 0.4) once the track starts shrinking.
- Programmatic `value` updates are drawn at once (playback updates every frame); only the gestures animate.
- `IntentSlider` has no touch spotlight and no glitter: `COUIIntentSeekBar.initAdvancedFeatures` overrides the base setup and leaves both out.

## Properties

### IntentSlider Properties

| Property Name         | Type                              | Description                                                                                              | Default Value                          | Required |
| --------------------- | --------------------------------- | -------------------------------------------------------------------------------------------------------- | -------------------------------------- | -------- |
| value                 | Float                             | Current value, coerced into valueRange                                                                   | -                                      | Yes      |
| onValueChange         | (Float) -> Unit                   | Called with the new value while the user drags, taps or flings                                           | -                                      | Yes      |
| modifier              | Modifier                          | Modifier applied to the bar                                                                              | Modifier                               | No       |
| enabled               | Boolean                           | Whether the bar accepts input                                                                            | true                                   | No       |
| showThumb             | Boolean                           | Whether to draw the thumb while touched or paused                                                        | false                                  | No       |
| followThumb           | Boolean                           | Whether a drag moves the progress to the finger instead of by the finger's travel                        | false                                  | No       |
| followSystemTheme     | Boolean                           | Whether the colors follow the theme; off selects the fixed media palette and allows the backdrop blur    | true                                   | No       |
| materialBackdrop      | Boolean                           | Whether the blur-blend background may replace the fill (only with followSystemTheme off and a provider)  | true                                   | No       |
| conicLight            | Boolean                           | Whether to use the conic light progress style (only with followSystemTheme off)                          | false                                  | No       |
| loadingDragReturn     | Boolean                           | Whether a drag while LoadingInitial shows the progress and springs it back to the start on release       | false                                  | No       |
| holdToSpeedUp         | Boolean                           | Whether a hold-to-speed-up gesture is active; while Playing with conicLight the cone light takes its dragging shape | false                       | No       |
| playbackState         | IntentSliderPlaybackState         | Playback state: Idle, LoadingInitial, Playing, Paused or LoadingBuffering                                | IntentSliderPlaybackState.Idle         | No       |
| valueRange            | ClosedFloatingPointRange\<Float\> | Range of values                                                                                          | 0f..1f                                 | No       |
| secondaryValue        | Float?                            | Secondary (buffered) value drawn under the progress; null hides it                                       | null                                   | No       |
| onValueChangeFinished | (() -> Unit)?                     | Called at the end of a drag, a fling or a tap                                                            | null                                   | No       |
| onLoadingDragReturnComplete | (() -> Unit)?               | Called when a loading drag return has brought the progress back to the start                            | null                                   | No       |
| onDragHorizontalOffsetChange | ((Float) -> Unit)?       | Called with the touch-motion rubber-band offset (px) every frame it moves the bar                         | null                                   | No       |
| size                  | IntentSliderSize                  | Track and thumb sizes                                                                                    | IntentSliderSize.Default               | No       |
| horizontalPadding     | Dp                                | Inset of the track from both ends                                                                        | IntentSliderDefaults.HorizontalPadding | No       |
| colors                | IntentSliderColors                | Colors of the bar                                                                                        | intentSliderColors() / fixedColors()   | No       |
| hapticEffect          | SliderHapticEffect                | Haptic feedback type: ends, and every 1% a finger drags (`VibrationFeedbackHandler`). See [Slider](./slider#haptic-feedback) | SliderHapticEffect.Step     | No       |

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
| ConicLightColor   | Color                | Progress and light color of the conic light style   | #F87796                                     |
| BackdropSpec      | MaterialBackdropSpec | The blur-blend background                           | 30px blur, #66333333 Lighten, #4DFFFFFF Softlight |

| Function Name        | Return Type        | Description                                                                                 |
| -------------------- | ------------------ | ------------------------------------------------------------------------------------------- |
| intentSliderColors() | IntentSliderColors | Theme colors: background/secondary quarternaryNeutral, progress labelPrimary, opaque thumb |
| fixedColors()        | IntentSliderColors | Media colors: #4DFFFFFF background, #8AFFFFFF progress, #29FFFFFF secondary, white thumb    |

### IntentSliderColors Class

| Property Name          | Type  | Description                               |
| ---------------------- | ----- | ----------------------------------------- |
| backgroundColor        | Color | Background capsule (the unplayed part)    |
| progressColor          | Color | Progress                                  |
| secondaryProgressColor | Color | Secondary (buffered) progress             |
| thumbColor             | Color | Thumb                                     |
| thumbShadowColor       | Color | Thumb shadow                              |
| conicLightColor        | Color | Progress and light of the conic light style (defaults to ConicLightColor) |

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
    playbackState = if (player.isPlaying) IntentSliderPlaybackState.Playing else IntentSliderPlaybackState.Paused,
)
```
