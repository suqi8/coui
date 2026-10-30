# CompleteState

`CompleteState` is the "operation complete" result block mirroring ColorOS's COUICompleteStateView: a 100dp result animation centered above a title and an optional subtitle. The three animations are redrawn from the Lottie files COUI ships (300x300 compositions at 60fps), keeping their key frames, easings and geometry:

- `Success`: a green ring fades in while a check mark sweeps in (1.48s).
- `Failed`: a red ring and exclamation mark fade in while the mark shakes (1.02s).
- `Waiting`: a clock whose hour hand grows out, the minute hand turns twice while the ring unwinds and winds back (3s).

The animation plays once when the component enters the composition and again whenever `type` or `replayKey` changes.

<div style="position: relative; height: 320px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=completeState" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.CompleteState
import io.github.suqi8.coui.kmp.basic.CompleteStateDefaults
import io.github.suqi8.coui.kmp.basic.CompleteStateType
```

## Basic Usage

```kotlin
CompleteState(
    title = "Payment successful",
    subtitle = "Order No. 2026103000001",
    type = CompleteStateType.Success
)
```

## Properties

### CompleteState Properties

| Property Name | Type                | Description                                             | Default Value                               | Required |
| ------------- | ------------------- | ------------------------------------------------------- | ------------------------------------------- | -------- |
| title         | String              | The title below the animation; hidden when empty        | -                                           | Yes      |
| modifier      | Modifier            | Modifier applied to the component                       | Modifier                                    | No       |
| subtitle      | String?             | The optional subtitle                                   | null                                        | No       |
| type          | CompleteStateType   | Selects the animation                                   | CompleteStateType.Success                   | No       |
| animate       | Boolean             | Whether to play the animation; false shows the last frame | true                                      | No       |
| replayKey     | Any?                | Changing this value replays the animation               | null                                        | No       |
| animationSize | Dp                  | Size of the animation                                   | CompleteStateDefaults.AnimationSize         | No       |
| colors        | CompleteStateColors | Color configuration                                     | CompleteStateDefaults.completeStateColors() | No       |
| titleStyle    | TextStyle           | Text style of the title                                 | COUITheme.textStyles.title3                 | No       |
| subtitleStyle | TextStyle           | Text style of the subtitle                              | COUITheme.textStyles.footnote2              | No       |

### CompleteStateType Enum

`Success`, `Failed`, `Waiting`.

### CompleteStateDefaults Object

| Property Name         | Type  | Description                                          | Default Value |
| --------------------- | ----- | ---------------------------------------------------- | ------------- |
| AnimationSize         | Dp    | Size of the result animation                         | 100.dp        |
| TextPaddingHorizontal | Dp    | Horizontal margin of the animation and the texts     | 8.dp          |
| TitleMarginTop        | Dp    | Top margin of the title                              | 8.dp          |
| SubtitleMarginTop     | Dp    | Top margin of the subtitle                           | 8.dp          |
| SuccessColor          | Color | Green baked into the success / wait animations       | #33B054       |
| FailedColor           | Color | Red baked into the fail animation                    | #ED4040       |

The result colors are fixed in COUI's Lottie files (force-dark is disabled on the view), so they are the same in light and dark themes.

### CompleteStateColors Class

| Property Name | Type  | Description                               |
| ------------- | ----- | ----------------------------------------- |
| successColor  | Color | Success ring and check mark               |
| failedColor   | Color | Failure ring and exclamation mark         |
| waitingColor  | Color | Waiting clock                             |
| titleColor    | Color | Title                                     |
| subtitleColor | Color | Subtitle                                  |
