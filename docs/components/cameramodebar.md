# CameraModeBar

`CameraModeBar` is the camera's mode strip, the port of the camera's headline (`ModePickerView`, laid out by `DimenConstants`): one line of mode names (Night, Portrait, Photo, Video, ...) that scrolls under a fixed capsule in the middle marking the selected mode. `CameraModeTitle` is the title the camera shows in its place while a mode opened from "More" is active: the mode name and a close cross in a capsule (`ModeTitleView`, `mode_title.xml`).

<div style="position: relative; height: 200px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=cameraModeBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.CameraModeBar
import io.github.suqi8.coui.kmp.basic.CameraModeBarDefaults
import io.github.suqi8.coui.kmp.basic.CameraModeTitle
import io.github.suqi8.coui.kmp.basic.CameraModeTitleDefaults
```

## Basic Usage

```kotlin
val modes = listOf("Night", "Portrait", "Photo", "Video", "Pro", "More")
var index by remember { mutableIntStateOf(2) } // Photo

CameraModeBar(
    modes = modes,
    selectedIndex = index,
    onSelectedIndexChange = { index = it },
)
```

The bar fills the width it is given and is 40dp high. The names are 13sp medium (following the font scale up to 1.35x), 32dp apart. The selected one sits in a 34dp capsule padded 16dp on both sides and at most 198dp wide; longer names are ellipsized. The strip fades out over the outer 72dp at both ends.

### Interaction

- Dragging moves the strip with 67% of the finger travel; past the first and last mode it stretches on a rubber band (an overshoot `o` shows as `(1 - 1 / (o / w + 1)) * w`, `w` a quarter of the bar width).
- The mode under the capsule flips once the middle passes the edge of the next name by 28px in the direction of travel, with a light haptic tick.
- On release the strip springs to that mode (`COUISpringForce` bounce 0.05, response 0.3s). A release faster than 5500px/s carries on three times as many modes as were dragged.
- Tapping a name (its width plus 8dp on both sides) springs to it. Taps are ignored while the strip slides.
- While the capsule travels between two modes its width blends from one name to the other. A name turns to the accent within 40dp of the middle, fully within 15dp, on (0, 0, 0.1, 1).
- `onSelectedIndexChange` is called once a gesture settles on a new mode. When `selectedIndex` changes from outside, the strip springs to it.
- An upward swipe (over 30px, under 150px sideways) is left to the parent, as the camera hands it to its bottom menu.

### Material Blur

Over the camera preview the capsule is a material blur (80dp radius, a glow overlay of a 15% grey over a 60% mix of 30% grey) with the camera's edge light. Pass a `MaterialBlurProvider` that records the content under the bar; without one the capsule keeps the camera's plain fill, white at 12%.

```kotlin
CameraModeBar(
    modes = modes,
    selectedIndex = index,
    onSelectedIndexChange = { index = it },
    materialBlur = provider, // records the viewfinder, not the bar
)
```

### Mode Title

While a mode picked from "More" is open, the camera replaces the strip with its title. Tapping anywhere on the title leaves the mode.

```kotlin
if (moreModeOpen) {
    CameraModeTitle(
        onClick = { moreModeOpen = false },
        title = "Slow-mo",
    )
} else {
    CameraModeBar(
        modes = modes,
        selectedIndex = index,
        onSelectedIndexChange = { index = it },
    )
}
```

The title is 36dp high around a 34dp capsule: the name in the accent (14sp medium) 16dp from the start, then a 16dp close cross 4dp after it and 12dp from the end. Names wider than 198dp scroll as a marquee with 16dp faded edges. The capsule is a 50% black fill, or the material blur with `materialBlur`. Pressed, the title dims to 50% in 200ms and recovers in 340ms; when the name changes the capsule resizes in 250ms on (0.3, 0, 0.1, 1).

## Properties

### CameraModeBar Properties

| Property Name         | Type                  | Description                                                      | Default Value                              | Required |
| --------------------- | --------------------- | ---------------------------------------------------------------- | ------------------------------------------ | -------- |
| modes                 | List&lt;String&gt;    | Mode names, start to end (not empty)                             | -                                          | Yes      |
| selectedIndex         | Int                   | Index of the selected mode                                       | -                                          | Yes      |
| onSelectedIndexChange | (Int) -> Unit         | Called when a drag, fling or tap settles on a new mode           | -                                          | Yes      |
| modifier              | Modifier              | Modifier applied to the bar                                      | Modifier                                   | No       |
| enabled               | Boolean               | Whether the strip accepts drags and taps                         | true                                       | No       |
| materialBlur          | MaterialBlurProvider? | Paints the material blur behind the capsule; null keeps the fill | null                                       | No       |
| colors                | CameraModeBarColors   | Colors of the bar                                                | CameraModeBarDefaults.cameraModeBarColors() | No       |

### CameraModeTitle Properties

| Property Name | Type                  | Description                                                      | Default Value                                  | Required |
| ------------- | --------------------- | ---------------------------------------------------------------- | ---------------------------------------------- | -------- |
| onClick       | () -> Unit            | Called when the title is tapped                                  | -                                              | Yes      |
| title         | String                | Name of the open mode                                            | -                                              | Yes      |
| modifier      | Modifier              | Modifier applied to the title                                    | Modifier                                       | No       |
| enabled       | Boolean               | Whether the title accepts taps                                   | true                                           | No       |
| materialBlur  | MaterialBlurProvider? | Paints the material blur behind the capsule; null keeps the fill | null                                           | No       |
| colors        | CameraModeTitleColors | Colors of the title                                              | CameraModeTitleDefaults.cameraModeTitleColors() | No       |

## Defaults Object

### CameraModeBarDefaults

| Property Name          | Type  | Description                                        | Value   |
| ---------------------- | ----- | -------------------------------------------------- | ------- |
| Height                 | Dp    | Bar height                                         | 40.dp   |
| CapsuleHeight          | Dp    | Capsule height                                     | 34.dp   |
| CapsulePadding         | Dp    | Capsule padding at both sides of the name          | 16.dp   |
| CapsuleMaxWidth        | Dp    | Widest capsule; longer names are ellipsized        | 198.dp  |
| ItemGap                | Dp    | Gap between two names                              | 32.dp   |
| TextSize               | Dp    | Name size (sp up to `MaxFontScale`)                | 13.dp   |
| MaxFontScale           | Float | Largest font scale the names follow                | 1.35f   |
| TextStrokeWidth        | Dp    | Dark outline around the names                      | 0.33.dp |
| EdgeFadeWidth          | Dp    | Fade at both ends of the strip                     | 72.dp   |
| HighlightStartDistance | Dp    | Distance from the middle where the accent starts   | 40.dp   |
| HighlightEndDistance   | Dp    | Distance from the middle within which it is full   | 15.dp   |
| TouchExtension         | Dp    | Extra touch width at both sides of a name          | 8.dp    |
| BlurRadius             | Dp    | Material blur radius of the capsule                | 80.dp   |

| Function              | Return Type         | Description                            |
| --------------------- | ------------------- | -------------------------------------- |
| cameraModeBarColors() | CameraModeBarColors | The fixed light-on-dark camera palette |

### CameraModeTitleDefaults

| Property Name      | Type  | Description                                   | Value  |
| ------------------ | ----- | --------------------------------------------- | ------ |
| Height             | Dp    | Title height                                  | 36.dp  |
| CapsuleHeight      | Dp    | Capsule height                                | 34.dp  |
| TextSize           | Dp    | Name size (sp up to 1.35x)                    | 14.dp  |
| TextStartPadding   | Dp    | Gap from the capsule start to the name        | 16.dp  |
| TextMaxWidth       | Dp    | Widest name before it scrolls                 | 198.dp |
| FadingEdgeLength   | Dp    | Fade at both edges of a scrolling name        | 16.dp  |
| MarqueeVelocity    | Dp    | Marquee speed per second                      | 30.dp  |
| MarqueeDelayMillis | Int   | Delay before the name starts scrolling        | 1200   |
| IconSpacing        | Dp    | Gap between the name and the close cross      | 4.dp   |
| IconEndPadding     | Dp    | Gap between the close cross and the capsule end | 12.dp |
| IconSize           | Dp    | Close cross size                              | 16.dp  |
| PressedAlpha       | Float | Alpha of the pressed title                    | 0.5f   |

| Function                | Return Type           | Description                |
| ----------------------- | --------------------- | -------------------------- |
| cameraModeTitleColors() | CameraModeTitleColors | The fixed camera palette   |

## Colors Classes

### CameraModeBarColors

| Property Name     | Type  | Description                          | Default             |
| ----------------- | ----- | ------------------------------------ | ------------------- |
| textColor         | Color | Mode names                           | White               |
| selectedTextColor | Color | Name under the capsule               | #FB822A             |
| textStrokeColor   | Color | Thin outline around every name       | #66000000           |
| capsuleColor      | Color | Capsule fill without material blur   | #1EFFFFFF           |
| blurMixColor      | Color | Material blur mix color              | 30% grey, 60% alpha |
| blurBlendColor    | Color | Material blur glow-overlay color     | 15% grey, 15% alpha |

### CameraModeTitleColors

| Property Name  | Type  | Description                        | Default             |
| -------------- | ----- | ---------------------------------- | ------------------- |
| textColor      | Color | Mode name                          | #FB822A             |
| iconColor      | Color | Close cross                        | #FB822A             |
| containerColor | Color | Capsule fill without material blur | #80000000           |
| blurMixColor   | Color | Material blur mix color            | 30% grey, 60% alpha |
| blurBlendColor | Color | Material blur glow-overlay color   | 15% grey, 15% alpha |
