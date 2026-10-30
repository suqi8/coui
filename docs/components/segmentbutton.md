# SegmentButton

`SegmentButton` is a segmented control in COUI: a capsule track holding a few mutually exclusive text segments, with a capsule indicator that springs onto the selected one. It is ported from COUI 17 `COUISegmentButtonLayout` and suits switching between a small number of views or filters.

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=segmentButton" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.SegmentButton
```

## Basic Usage

```kotlin
var selectedIndex by remember { mutableIntStateOf(0) }

SegmentButton(
    items = listOf("Day", "Week", "Month"),
    selectedIndex = selectedIndex,
    onSelectedIndexChange = { selectedIndex = it }
)
```

## Sizing

Each segment is its label plus 12dp on each side, at least 52dp wide. Without a fixed width the control wraps its labels. With a fixed width (for example `Modifier.fillMaxWidth()`), or when the labels do not fit, the width is distributed like `COUISegmentButtonLayout.calculateChildrenWidths`: segments narrower than an equal share keep their width and the others split the rest equally.

```kotlin
SegmentButton(
    items = listOf("All", "Allowed", "Not allowed"),
    selectedIndex = selectedIndex,
    onSelectedIndexChange = { selectedIndex = it },
    modifier = Modifier.fillMaxWidth()
)
```

The small variant (`SegmentButton.Tiny`, 32dp high with a 2dp inset):

```kotlin
SegmentButton(
    items = listOf("List", "Grid"),
    selectedIndex = selectedIndex,
    onSelectedIndexChange = { selectedIndex = it },
    height = SegmentButtonDefaults.HeightSmall,
    padding = SegmentButtonDefaults.PaddingSmall
)
```

## Interaction

`SegmentButton` follows `COUISegmentButtonLayout`:

- Tapping a segment selects it on release; the touched segment shrinks with the COUI press scale.
- The indicator moves on three springs: position (response 0.4, bounce 0.1), width (response 0.6 → 0.4 and bounce 0.5 → 0.1 by how different the two widths are, started at 1000 px/s; a growing indicator overshoots by at most 7dp, 5dp with the small inset) and height (dips towards 90% and springs back).
- Pressing the selected segment grabs the indicator: it presses in and can be dragged. Sliding onto another segment selects it with a haptic tick; dragging within the selected segment pulls the indicator on a rubber band.
- The whole control follows the COUI 17 touch motion (press scale, rubber band, drag deformation); `dragAnimationEnabled = false` turns it off.
- A disabled control looks the same, as in COUI; it only ignores touches.

## Properties

### SegmentButton Properties

| Property Name         | Type                | Description                                                          | Default Value                              | Required |
| --------------------- | ------------------- | -------------------------------------------------------------------- | ------------------------------------------ | -------- |
| items                 | List\<String\>      | Segment labels                                                       | -                                          | Yes      |
| selectedIndex         | Int                 | Index of the selected segment                                        | -                                          | Yes      |
| onSelectedIndexChange | (Int) -> Unit       | Called when the user selects another segment, by tap or drag         | -                                          | Yes      |
| modifier              | Modifier            | Modifier applied to the control                                      | Modifier                                   | No       |
| enabled               | Boolean             | Whether the control reacts to touches                                | true                                       | No       |
| materialEffect        | Boolean             | Whether the COUI 17 material is drawn (see below); off by default, as in COUI | false                             | No       |
| dragAnimationEnabled  | Boolean             | Whether the whole control follows the COUI 17 touch motion           | true                                       | No       |
| hapticFeedbackEnabled | Boolean             | Whether dragging onto another segment ticks                          | true                                       | No       |
| height                | Dp                  | Control height, at least `MinHeight`                                 | SegmentButtonDefaults.Height               | No       |
| padding               | Dp                  | Inset of the indicator from the track edge                           | SegmentButtonDefaults.Padding              | No       |
| colors                | SegmentButtonColors | Color configuration                                                  | SegmentButtonDefaults.segmentButtonColors() | No      |
| textStyle             | TextStyle           | Label style                                                          | SegmentButtonDefaults.textStyle()          | No       |

### SegmentButtonDefaults Object

#### Constants

| Constant Name            | Type | Description                                   | Value  |
| ------------------------ | ---- | --------------------------------------------- | ------ |
| Height                   | Dp   | Default height (`SegmentButton` style)        | 40.dp  |
| HeightSmall              | Dp   | Small height (`SegmentButton.Tiny` style)     | 32.dp  |
| MinHeight                | Dp   | Minimum height (`coui_segment_min_height`)    | 28.dp  |
| Padding                  | Dp   | Indicator inset (`coui_segment_padding`)      | 4.dp   |
| PaddingSmall             | Dp   | Small indicator inset (`coui_segment_padding_tiny`) | 2.dp |
| SegmentPaddingHorizontal | Dp   | Horizontal padding of each label              | 12.dp  |
| SegmentMinWidth          | Dp   | Minimum segment width                         | 52.dp  |

#### Methods

| Method Name           | Type                 | Description                                                        |
| --------------------- | -------------------- | ------------------------------------------------------------------ |
| segmentButtonColors() | SegmentButtonColors  | Creates the default color configuration                            |
| textStyle()           | TextStyle            | The label style, `couiTextButtonM` (14sp medium, `textStyles.subtitle`) |
| backdropSpec(isDark)  | MaterialBackdropSpec | The `TYPE_CONTENT_SEGMENT_BUTTON` backdrop blur: 25px, `#7F333333` color dodge + `#99EFEFEF` luminosity (light), `#7F7F7F7F` overlay + `#99262626` luminosity (dark) |

### SegmentButtonColors Class

| Property Name           | Type  | Description                                                          | Default Value              |
| ----------------------- | ----- | -------------------------------------------------------------------- | -------------------------- |
| backgroundColor         | Color | Track fill                                                           | #0F000000 / #1FFFFFFF (dark) |
| indicatorColor          | Color | Indicator fill                                                       | #B2FFFFFF / #33FFFFFF (dark) |
| selectedTextColor       | Color | Label color of the selected segment                                  | `onSurface`                |
| unselectedTextColor     | Color | Label color of the other segments                                    | `onSurface`                |
| indicatorShadowColor    | Color | Shadow under the indicator: gradient centre (light), solid fill (dark) | #0D000000 / #14000000 (dark) |
| indicatorShadowEndColor | Color | Light-scheme gradient edge of the indicator shadow                   | #03000000                  |

## Advanced Usage

### Material Effect

With `materialEffect = true` the control draws the COUI 17 material that `COUISegmentButtonLayout.setMaterialEffectEnabled(true)` turns on: an edge light and inner shadow on the track, an edge light on the indicator and a touch spotlight (these need runtime shaders), plus the `TYPE_CONTENT_SEGMENT_BUTTON` backdrop blur. coui-ui has no backdrop capture of its own: provide a `MaterialBackdropProvider` through `LocalMaterialBackdrop` and the blur replaces the track fill. Without a provider the track keeps `backgroundColor`, COUI's non-blur fallback. The backdrop must record the content behind the control, not the control itself.

```kotlin
CompositionLocalProvider(LocalMaterialBackdrop provides backdropProvider) {
    SegmentButton(
        items = listOf("Recent", "Favorites", "Shared"),
        selectedIndex = selectedIndex,
        onSelectedIndexChange = { selectedIndex = it },
        modifier = Modifier.fillMaxWidth(),
        materialEffect = true
    )
}
```

### Custom Colors

```kotlin
SegmentButton(
    items = listOf("On", "Off"),
    selectedIndex = selectedIndex,
    onSelectedIndexChange = { selectedIndex = it },
    colors = SegmentButtonDefaults.segmentButtonColors(
        selectedTextColor = COUITheme.colorScheme.primary
    )
)
```
