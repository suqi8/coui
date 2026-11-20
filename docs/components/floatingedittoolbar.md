# FloatingEditToolbar

`FloatingEditToolbar` is the COUI 17 floating edit bar (`COUIBottomFloatingEditToolbar`): the capsule of edit actions a list shows at the bottom while items are selected. Each `FloatingEditToolbarItem` is an icon with a label; the toolbar lays the items out, delivers their taps and slides in and out from below.

The toolbar follows the width it is laid out at. Narrower than 600dp it draws a 56dp capsule whose items stack a 24dp icon over a 10dp label; from 600dp it draws a 44dp capsule whose items put the icon beside a 14dp label of up to two lines. Items that do not fit shrink equally; there is no overflow menu.

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=floatingEditToolbar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.FloatingEditToolbar
import io.github.suqi8.coui.kmp.basic.FloatingEditToolbarItem
import io.github.suqi8.coui.kmp.basic.FloatingEditToolbarDefaults
import io.github.suqi8.coui.kmp.basic.FloatingEditToolbarColors
```

## Basic Usage

```kotlin
var selecting by remember { mutableStateOf(false) }

Scaffold(
    bottomBar = {
        FloatingEditToolbar(visible = selecting) {
            FloatingEditToolbarItem(onClick = { /* Share */ }, icon = COUIIcons.Share, label = "Share")
            FloatingEditToolbarItem(onClick = { /* Copy */ }, icon = COUIIcons.Copy, label = "Copy")
            FloatingEditToolbarItem(onClick = { /* Delete */ }, icon = COUIIcons.Delete, label = "Delete")
        }
    }
) { paddingValues ->
    // Content
}
```

- A tap clicks the item it started and ended on, unless the finger slid off that item by more than the touch slop on the way (`View.onTouchEvent`). The capsule takes the COUI press scale, rubber band and deformation and the touch spotlight; items have no press mask of their own. With every item disabled the capsule gives no touch feedback.
- `visible = false` slides the capsule out below the toolbar's bottom edge on a spring (response 0.38, bounce 0.25), with no fade. The hidden toolbar keeps its space but takes no touches.

## Properties

### FloatingEditToolbar Properties

| Property Name              | Type                   | Description                                                                 | Default Value                               | Required |
| -------------------------- | ---------------------- | --------------------------------------------------------------------------- | ------------------------------------------- | -------- |
| modifier                   | Modifier               | Modifier applied to the full-width container                                | Modifier                                    | No       |
| backgroundModifier         | Modifier               | Applied inside the capsule clip, above `color` and below the items          | Modifier                                    | No       |
| visible                    | Boolean                | Whether the capsule is shown; changes slide it in / out                     | true                                        | No       |
| materialEffect             | Boolean                | Draw the COUI 17 edge lighting, spotlight and (with a `LocalMaterialBackdrop` provider) the backdrop blur | `LocalCOUIMaterialEffectEnabled.current` | No |
| defaultWindowInsetsPadding | Boolean                | Keep the capsule above the navigation and caption bars                      | true                                        | No       |
| color                      | Color                  | Capsule fill; replaced by the backdrop blur when a provider is present      | FloatingEditToolbarDefaults.color()         | No       |
| shadowElevation            | Dp                     | Blur radius of the drop shadow; 0 disables it                               | FloatingEditToolbarDefaults.ShadowElevation | No       |
| content                    | @Composable () -> Unit | The `FloatingEditToolbarItem`s                                              | -                                           | Yes      |

### FloatingEditToolbarItem Properties

| Property Name | Type                      | Description                                                                    | Default Value                             | Required |
| ------------- | ------------------------- | ------------------------------------------------------------------------------ | ----------------------------------------- | -------- |
| onClick       | () -> Unit                | Called when the item is tapped                                                 | -                                         | Yes      |
| icon          | ImageVector               | The item icon, tinted with the content color                                   | -                                         | Yes      |
| label         | String                    | The item label, also its accessibility label                                   | -                                         | Yes      |
| modifier      | Modifier                  | Modifier applied to the item                                                   | Modifier                                  | No       |
| enabled       | Boolean                   | Whether the item is enabled; a disabled item ignores taps and hides its badge  | true                                      | No       |
| badge         | Int?                      | `null` for none, 0 for a plain red dot, a positive number for a number badge   | null                                      | No       |
| colors        | FloatingEditToolbarColors | The content colors                                                             | FloatingEditToolbarDefaults.itemColors()  | No       |

### FloatingEditToolbarDefaults Object

| Property Name                     | Type  | Description                                                     | Value  |
| --------------------------------- | ----- | --------------------------------------------------------------- | ------ |
| CornerRadius                      | Dp    | Fully rounded soft capsule                                      | COUIRoundCorner.Full |
| MinHeight                         | Dp    | Height the capsule is centred in                                | 88.dp  |
| CompactWidthBreakpoint            | Dp    | Below it the compact (icon over label) style applies            | 600.dp |
| ExpandedWidthBreakpoint           | Dp    | From it the expanded side padding applies                       | 840.dp |
| HorizontalOutSidePaddingCompact   | Dp    | Side padding on compact widths                                  | 16.dp  |
| HorizontalOutSidePaddingMedium    | Dp    | Side padding on medium widths                                   | 24.dp  |
| HorizontalOutSidePaddingExpanded  | Dp    | Side padding on expanded widths                                 | 40.dp  |
| HeightCompact                     | Dp    | Capsule height, compact style                                   | 56.dp  |
| HeightMediumLarge                 | Dp    | Capsule height, wide style                                      | 44.dp  |
| InnerPaddingVertical              | Dp    | Capsule inner padding, compact style                            | 12.dp  |
| InnerPaddingHorizontal            | Dp    | Capsule inner padding, wide style                               | 10.dp  |
| ItemSpacing                       | Dp    | Spacing between items                                           | 8.dp   |
| ItemMaxWidthVertical              | Dp    | Item width, compact style                                       | 70.dp  |
| ItemMaxWidthHorizontal            | Dp    | Item width, wide style                                          | 98.dp  |
| SingleItemWidthVertical           | Dp    | Width of a single item, compact style                           | 148.dp |
| SingleItemWidthHorizontal         | Dp    | Width of a single item, wide style                              | 152.dp |
| ItemHeightVertical                | Dp    | Item height, compact style                                      | 48.dp  |
| ItemHeightHorizontal              | Dp    | Item height, wide style                                         | 40.dp  |
| IconSize                          | Dp    | Icon size                                                       | 24.dp  |
| IconTopPadding                    | Dp    | Icon distance from the item top, compact style                  | 4.dp   |
| LabelTopSpacing                   | Dp    | Gap between icon and label, compact style                       | 2.dp   |
| IconLabelSpacingHorizontal        | Dp    | Gap between icon and label, wide style                          | 4.dp   |
| LabelTextSizeVertical             | Dp    | Label size, compact style (does not follow the font scale)      | 10.dp  |
| LabelTextSizeHorizontal           | Dp    | Label size, wide style (does not follow the font scale)         | 14.dp  |
| ShadowElevation                   | Dp    | Blur radius of the caustic drop shadow                          | 24.dp  |

| Function Name        | Return Type               | Description                                                          |
| -------------------- | ------------------------- | -------------------------------------------------------------------- |
| color()              | Color                     | The opaque `?couiColorBar` fill                                      |
| backdropSpec(isDark) | MaterialBackdropSpec      | The COUI 17 bottom-bar material `TYPE_FRAMEWORK_BOTTOM_BAR`          |
| itemColors()         | FloatingEditToolbarColors | `?couiColorLabelPrimary` enabled, `?couiColorLabelTertiary` disabled |

### FloatingEditToolbarColors Properties

| Property Name        | Type  | Description                           |
| -------------------- | ----- | ------------------------------------- |
| contentColor         | Color | Icon and label color of enabled items |
| disabledContentColor | Color | Icon and label color of disabled items |

## Advanced Usage

### Disabled Items and Badges

```kotlin
FloatingEditToolbar {
    FloatingEditToolbarItem(onClick = {}, icon = COUIIcons.Share, label = "Share", badge = 0)
    FloatingEditToolbarItem(onClick = {}, icon = COUIIcons.Copy, label = "Copy", enabled = false)
    FloatingEditToolbarItem(onClick = {}, icon = COUIIcons.Delete, label = "Delete", badge = 3)
}
```

### Material Blur

Provide a `LocalMaterialBackdrop` (for example from coui-blur) around the toolbar: the capsule then draws the 25px bottom-bar backdrop blur instead of `color`. The blur is drawn inside the capsule's own layer, below the edge lighting, so the edge light stays on the blur in both schemes. Pass `materialEffect = false` for a plain capsule.
