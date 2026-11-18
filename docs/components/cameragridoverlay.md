# CameraGridOverlay

`CameraGridOverlay` draws the composition guides of the camera viewfinder ("Grid and guides"), the port of the camera's `GuideLineView`: the thirds grid, diagonal lines, the golden spiral and a centered cross. The lines are 0.66dp hairlines of 50% white edged on both sides by 0.33dp of 30% black, so they read on any preview.

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=cameraGridOverlay" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.CameraGridOverlay
import io.github.suqi8.coui.kmp.basic.CameraGridOverlayDefaults
import io.github.suqi8.coui.kmp.basic.CameraGridType
```

## Basic Usage

Give the overlay the viewfinder area:

```kotlin
Box(Modifier.fillMaxWidth().aspectRatio(3f / 4f)) {
    CameraPreview(Modifier.matchParentSize())
    CameraGridOverlay(
        type = CameraGridType.Grid,
        modifier = Modifier.matchParentSize(),
    )
}
```

### Guide Types

| Type         | Camera value | Lines                                                                                                   |
| ------------ | ------------ | ------------------------------------------------------------------------------------------------------- |
| Grid         | `grid`       | Thirds: two horizontal and two vertical lines                                                           |
| Diagonal     | `triangle`   | The diagonal from the top-right to the bottom-left corner, with a perpendicular from each side through its thirds |
| GoldenSpiral | `spiral`     | Nine quarter arcs with Fibonacci radii in a 34:55 golden rectangle centered in the frame, with the sides of the four inner squares |
| Symmetry     | `symmetry`   | A centered cross through the whole frame                                                                |

The camera's settings offer Grid, Diagonal lines and Golden spiral; Symmetry is the guide some of its modes use.

### Showing and Hiding

The overlay fades in when it first appears and whenever `visible` changes, over 310ms (an accelerate-decelerate curve). Switching `type` redraws at once.

```kotlin
CameraGridOverlay(
    type = type,
    visible = showGuides,
    modifier = Modifier.matchParentSize(),
)
```

### With the Level

Pass the state of a [CameraLevelIndicator](./cameralevelindicator) over the same area and the lines leave a 4.33dp gap where the level line crosses them: always for `Diagonal` and `Symmetry`, only once level for `Grid` (where the grown level line reaches the vertical lines), never for `GoldenSpiral`.

```kotlin
val levelState = rememberCameraLevelState()

CameraGridOverlay(
    type = CameraGridType.Diagonal,
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

## Properties

### CameraGridOverlay Properties

| Property Name | Type                    | Description                                                | Default Value                                   | Required |
| ------------- | ----------------------- | ---------------------------------------------------------- | ----------------------------------------------- | -------- |
| type          | CameraGridType          | Which guides to draw                                       | -                                               | Yes      |
| modifier      | Modifier                | Modifier applied to the overlay; must give it a size       | Modifier                                        | No       |
| visible       | Boolean                 | Whether the guides are shown; changes fade over 310ms      | true                                            | No       |
| levelState    | CameraLevelState?       | State of a `CameraLevelIndicator` over the same area       | null                                            | No       |
| colors        | CameraGridOverlayColors | Colors of the guides                                       | CameraGridOverlayDefaults.cameraGridOverlayColors() | No   |

## Defaults Object

### CameraGridOverlayDefaults

| Property Name      | Type | Description                              | Value   |
| ------------------ | ---- | ---------------------------------------- | ------- |
| LineWidth          | Dp   | Line width                               | 0.66.dp |
| OutlineWidth       | Dp   | Dark outline on each side of a line      | 0.33.dp |
| FadeDurationMillis | Int  | Fade when the guides are shown or hidden | 310     |

| Function                  | Return Type             | Description                            |
| ------------------------- | ----------------------- | -------------------------------------- |
| cameraGridOverlayColors() | CameraGridOverlayColors | The fixed light-on-dark camera palette |

## Colors Classes

### CameraGridOverlayColors

| Property Name | Type  | Description                           | Default   |
| ------------- | ----- | ------------------------------------- | --------- |
| lineColor     | Color | Guide lines                           | #80FFFFFF |
| outlineColor  | Color | Thin outline on both sides of a line  | #4D000000 |
