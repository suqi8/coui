# CameraThumbnail

`CameraThumbnail` is the gallery thumbnail button of the camera, the port of the camera's `ThumbImageView`. A small rounded square shows the latest photo, center-cropped, with a hairline frame; a new photo grows out of its center over the previous one. Without a photo it shows the camera's dark placeholder.

<div style="position: relative; height: 200px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=cameraThumbnail" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.CameraThumbnail
import io.github.suqi8.coui.kmp.basic.CameraThumbnailDefaults
import io.github.suqi8.coui.kmp.basic.CameraThumbnailTransition
```

## Basic Usage

```kotlin
val latest: Painter? = remember(lastPhoto) { lastPhoto?.let { BitmapPainter(it) } }

CameraThumbnail(
    image = latest,
    onClick = { openGallery() },
)
```

The thumbnail is a 40dp square with 6dp corners and a 0.67dp 10% white frame drawn inside its edge. Photos are center-cropped to the square. A null `image` shows the camera's placeholder, a `#262626` fill.

The thumbnail tells photos apart by identity: pass a new `Painter` for each new photo and keep it stable between recompositions (`remember` it). The `ImageBitmap` overload does this for you:

```kotlin
CameraThumbnail(
    bitmap = lastBitmap, // ImageBitmap?
    onClick = { openGallery() },
)
```

### New Photo Animation

`transition` decides how a new photo comes in:

| Transition | Behavior                                                                                                   |
| ---------- | ---------------------------------------------------------------------------------------------------------- |
| ScaleIn    | Default. The new photo grows out of the center from 6% to full size over the old one, 350ms on (0.3, 0, 0.1, 1) |
| ScaleOut   | The new photo shows at once and the old one shrinks from 94% into the center over it, for example after a delete |
| None       | The new photo replaces the old one at once                                                                  |

During a burst, a new photo that arrives before the growing one has reached 80% replaces it in place instead of restarting the animation, so the thumbnail keeps up with the shots.

```kotlin
CameraThumbnail(
    image = latest,
    onClick = { openGallery() },
    transition = CameraThumbnailTransition.ScaleOut,
)
```

### Interaction

- Pressing dims the thumbnail to 50% in 200ms; releasing restores it in 340ms, both on (0.33, 0, 0.67, 1).
- A disabled thumbnail stays at 40% and ignores taps.
- Toggling `visible` fades and scales the thumbnail between 80% and full size in 200ms on (0.3, 0, 0.1, 1); showing starts after a 200ms delay. A hidden thumbnail keeps its space.
- `rotation` turns the photo with the device orientation along the shorter way, 1000ms per 270 degrees on (0.3, 0, 0.1, 1). Pass the negated device orientation to keep the photo upright.

```kotlin
CameraThumbnail(
    image = latest,
    onClick = { openGallery() },
    visible = !recording,
    rotation = -deviceOrientation, // 0, 90, 180 or 270
)
```

## Properties

| Property Name      | Type                      | Description                                                | Default Value                                 | Required |
| ------------------ | ------------------------- | ---------------------------------------------------------- | --------------------------------------------- | -------- |
| image              | Painter?                  | Latest photo; null shows the placeholder                   | -                                             | Yes      |
| onClick            | () -> Unit                | Called when the thumbnail is tapped                        | -                                             | Yes      |
| modifier           | Modifier                  | Modifier applied to the thumbnail                          | Modifier                                      | No       |
| enabled            | Boolean                   | Whether the thumbnail accepts taps                         | true                                          | No       |
| visible            | Boolean                   | Whether the thumbnail is shown                             | true                                          | No       |
| transition         | CameraThumbnailTransition | How a new image comes in                                   | CameraThumbnailTransition.ScaleIn             | No       |
| rotation           | Int                       | Angle the photo is turned to, clockwise degrees            | 0                                             | No       |
| contentDescription | String?                   | Accessibility description                                  | null                                          | No       |
| size               | Dp                        | Side of the square                                         | CameraThumbnailDefaults.Size                  | No       |
| cornerRadius       | Dp                        | Corner radius                                              | CameraThumbnailDefaults.CornerRadius          | No       |
| strokeWidth        | Dp                        | Width of the frame; 0dp draws none                         | CameraThumbnailDefaults.StrokeWidth           | No       |
| colors             | CameraThumbnailColors     | Colors of the thumbnail                                    | CameraThumbnailDefaults.cameraThumbnailColors() | No     |

The `ImageBitmap` overload takes `bitmap: ImageBitmap?` in place of `image` and the same other parameters.

## Defaults Object

### CameraThumbnailDefaults

| Property Name        | Type  | Description                                  | Value   |
| -------------------- | ----- | -------------------------------------------- | ------- |
| Size                 | Dp    | Thumbnail side                               | 40.dp   |
| CornerRadius         | Dp    | Corner radius                                | 6.dp    |
| RetroCornerRadius    | Dp    | Corner radius of the retro film style        | 4.dp    |
| StrokeWidth          | Dp    | Frame width                                  | 0.67.dp |
| TransitionDuration   | Int   | New photo animation, ms                      | 350     |
| VisibilityDuration   | Int   | Show and hide animation, ms                  | 200     |
| RotationMillisPer270 | Int   | Rotation speed, ms per 270 degrees           | 1000    |
| HiddenScale          | Float | Scale of a hidden thumbnail                  | 0.8f    |
| PressedAlpha         | Float | Alpha while pressed                          | 0.5f    |
| DisabledAlpha        | Float | Alpha when disabled                          | 0.4f    |

| Function                | Return Type           | Description                    |
| ----------------------- | --------------------- | ------------------------------ |
| cameraThumbnailColors() | CameraThumbnailColors | The fixed camera palette       |

## Colors Class

### CameraThumbnailColors

| Property Name    | Type  | Description                  | Default   |
| ---------------- | ----- | ---------------------------- | --------- |
| placeholderColor | Color | Fill shown without a photo   | #262626   |
| strokeColor      | Color | Frame                        | #1AFFFFFF |
