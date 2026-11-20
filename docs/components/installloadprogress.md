# InstallLoadProgress

`InstallLoadProgress` is the COUI download / install capsule button used by app stores. It shows a label on an accent-tinted capsule and, while downloading, an accent fill that grows with the progress; the part of the label covered by the fill turns white.

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=installLoadProgress" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.InstallLoadProgress
import io.github.suqi8.coui.kmp.basic.InstallLoadState
import io.github.suqi8.coui.kmp.basic.InstallLoadStyle
```

## Basic Usage

```kotlin
var state by remember { mutableStateOf(InstallLoadState.Idle) }
var progress by remember { mutableFloatStateOf(0f) }

InstallLoadProgress(
    text = if (state == InstallLoadState.Idle) "Download" else "${(progress * 100).toInt()}%",
    onClick = {
        state = if (state == InstallLoadState.Loading) InstallLoadState.Paused else InstallLoadState.Loading
    },
    state = state,
    progress = progress,
)
```

## States

| State     | Look                                                                                 |
| --------- | ------------------------------------------------------------------------------------ |
| `Idle`    | Small style: tinted capsule with an accent label. Large style: accent capsule with a white label |
| `Loading` | Tinted capsule with an accent fill of `progress`; the covered label turns white      |
| `Paused`  | Drawn exactly like `Loading`                                                         |
| `Failed`  | Accent capsule with a white label                                                    |

Progress changes follow a critically damped spring (COUI `COUISpringForce` bounce 0, response 1.0). Pressing dims the fills (HSL lightness × 0.8), insets the capsule by 5% of its size on every side and shrinks the label to 92%, over 200ms on press and 340ms on release with the move ease curve. The inset is at least 0.5% from the moment the finger lands (`DEFAULT_MIN_PRESS_FEEDBACK`), so the press shows at once.

With `hapticFeedbackEnabled = true` (COUI `loadingButtonNeedVibrate`, off by default) the button plays the COUI tap haptic when pressed and again when released.

## Styles

```kotlin
// Small (default): 52 × 28dp, 30dp wider outside Chinese locales
InstallLoadProgress(text = "Get", onClick = {})

// Large: 220 × 44dp
InstallLoadProgress(text = "Download", onClick = {}, style = InstallLoadStyle.Large)

// Circle (LOAD_STYLE_CIRCLE): a 33dp icon button; text becomes the content description
InstallLoadProgress(text = "Download", onClick = {}, style = InstallLoadStyle.Circle)
```

The circle style draws no label and no progress fill: a 30dp rounded square (7dp corners) holds a download arrow in the accent color on the tinted fill when idle, a white pause icon on the accent fill while loading, and a white arrow on the accent fill when paused or failed. The new icon cross-fades in over 340ms; a press shrinks the square to 90% in 200ms and dims it.

## Properties

### InstallLoadProgress Properties

| Property Name     | Type                       | Description                                              | Default Value                                           | Required |
| ----------------- | -------------------------- | -------------------------------------------------------- | ------------------------------------------------------- | -------- |
| text              | String                     | Label text                                               | -                                                       | Yes      |
| onClick           | () -> Unit                 | Click callback                                           | -                                                       | Yes      |
| modifier          | Modifier                   | Modifier applied to the button; a size here overrides the style size | Modifier                                    | No       |
| state             | InstallLoadState           | Current state                                            | InstallLoadState.Idle                                   | No       |
| progress          | Float                      | Progress between 0 and 1, shown while loading or paused  | 0f                                                      | No       |
| enabled           | Boolean                    | Whether the button is enabled                            | true                                                    | No       |
| hapticFeedbackEnabled | Boolean                | Whether press and release play the COUI tap haptic       | false                                                   | No       |
| style             | InstallLoadStyle           | Size style                                               | InstallLoadStyle.Small                                  | No       |
| colors            | InstallLoadProgressColors  | Color configuration                                      | InstallLoadProgressDefaults.installLoadProgressColors() | No       |
| textStyle         | TextStyle                  | Label text style                                         | COUITheme.textStyles.buttonM                           | No       |
| interactionSource | MutableInteractionSource?  | Interaction source                                       | null                                                    | No       |

### InstallLoadProgressDefaults Object

#### Constants

| Constant Name                  | Type  | Default Value | Description                                     |
| ------------------------------ | ----- | ------------- | ----------------------------------------------- |
| SmallWidth                     | Dp    | 52.dp         | Width of the small style                        |
| SmallForeignLanguageExtraWidth | Dp    | 30.dp         | Extra small-style width outside Chinese locales |
| SmallHeight                    | Dp    | 28.dp         | Height of the small style                       |
| LargeWidth                     | Dp    | 220.dp        | Width of the large style                        |
| LargeHeight                    | Dp    | 44.dp         | Height of the large style                       |
| TextPadding                    | Dp    | 6.dp          | Horizontal label padding                        |
| PressedBrightness              | Float | 0.8f          | Fill lightness factor while pressed             |
| CircleSize                     | Dp    | 33.dp         | Size of the circle style                        |
| CircleRadius                   | Dp    | 15.dp         | Half side of the circle-style square            |
| CircleCornerRadius             | Dp    | 7.dp          | Corner radius of the circle-style square        |

#### Methods

| Method Name                          | Type                      | Description                          |
| ------------------------------------ | ------------------------- | ------------------------------------ |
| installLoadProgressColors()          | InstallLoadProgressColors | Creates the default colors           |
| width(style, isChineseLocale)        | Dp                        | Width of a style                     |
| height(style)                        | Dp                        | Height of a style                    |

### InstallLoadProgressColors Class

| Property Name          | Type  | Description                                                   | Default (light / dark)      |
| ---------------------- | ----- | ------------------------------------------------------------- | --------------------------- |
| progressColor          | Color | Progress fill, and the whole capsule when filled              | `#0080FF` / `#1A8CFF`       |
| trackColor             | Color | Tinted capsule behind the progress                            | `#260080FF` / `#401A8CFF`   |
| disabledColor          | Color | Fill of every capsule layer while disabled                    | `#14000000` / `#26FFFFFF`   |
| contentColor           | Color | Label color over the track                                    | `#0080FF` / `#1A8CFF`       |
| onProgressContentColor | Color | Label color over the progress fill                            | `#FFFFFF`                   |

## Notes

- A disabled button only swaps its fills to `disabledColor`; the label keeps its colors, as in COUI.
- The gift state (`INSTALL_HAVE_GIFT`) is not ported: COUI 17 reads `couiInstallGiftBg` but never draws it.
