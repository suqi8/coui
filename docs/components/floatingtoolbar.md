# FloatingToolbar

`FloatingToolbar` is a floating toolbar component in COUI that renders its content within a container with a rounded background, arranged either horizontally or vertically. The actual placement on the screen is handled by the parent component, typically `Scaffold`.

This component is usually used in conjunction with `Scaffold`, placed in a specific position on the page to provide quick actions or display information.

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=floatingToolbar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.FloatingToolbar
import io.github.suqi8.coui.kmp.basic.FloatingToolbarDefaults
import io.github.suqi8.coui.kmp.basic.ToolbarPosition // Used for Scaffold
```

## Basic Usage

```kotlin
Scaffold(
    floatingToolbar = {
        FloatingToolbar {
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) { // or Column
                IconButton(onClick = { /* Action 1 */ }) {
                    Icon(COUIIcons.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = { /* Action 2 */ }) {
                    Icon(COUIIcons.Delete, contentDescription = "Delete")
                }
            }
        }
    },
    floatingToolbarPosition = ToolbarPosition.BottomCenter // Set the position
)
```

## Properties

### FloatingToolbar Properties

| Property Name              | Type                   | Description                            | Default Value                          | Required |
| -------------------------- | ---------------------- | -------------------------------------- | -------------------------------------- | -------- |
| modifier                   | Modifier               | Modifier applied to the toolbar        | Modifier                               | No       |
| visible                    | Boolean                | Whether the toolbar is shown; changes run the COUI 17 scale-in / scale-out springs | true | No       |
| materialEffect             | Boolean                | Use the COUI 17 bottom-bar material: with a `LocalMaterialBackdrop` provider the backdrop blur replaces `color` | true | No       |
| color                      | Color                  | Background color of the toolbar        | FloatingToolbarDefaults.defaultColor() | No       |
| cornerRadius               | Dp                     | Corner radius of the toolbar           | FloatingToolbarDefaults.CornerRadius   | No       |
| outSidePadding             | PaddingValues          | Padding outside the toolbar            | FloatingToolbarDefaults.OutSidePadding | No       |
| shadowElevation            | Dp                     | The shadow elevation of the toolbar    | 4.dp                                   | No       |
| showDivider                | Boolean                | Show divider line around the toolbar   | false                                  | No       |
| content                    | @Composable () -> Unit | Composable content area of the toolbar | -                                      | Yes      |

### FloatingToolbarDefaults Object

| Property Name  | Type                    | Description              | Value                                   |
| -------------- | ----------------------- | ------------------------ | --------------------------------------- |
| CornerRadius   | Dp                      | Default corner radius (half the 44dp height, drawn as the weight-1.0 soft capsule; a radius below half the height takes `default_g2_weight` 2.45) | 22.dp                                   |
| defaultColor() | @Composable () -> Color | Default background color | COUITheme.colorScheme.surfaceContainer |
| backdropSpec(isDark) | MaterialBackdropSpec | The COUI 17 bottom-bar material `TYPE_FRAMEWORK_BOTTOM_BAR`, the same as `FloatingNavigationBarDefaults.backdropSpec` | 25px blur + two blended layers |
| OutSidePadding | PaddingValues           | Default outside padding  | PaddingValues(12.dp, 8.dp)              |

### ToolbarPosition (for Scaffold)

Please refer to the `ToolbarPosition` options in the [Scaffold](../components/scaffold#toolbarposition-options) documentation.

## Advanced Usage

### Custom Styles

```kotlin
FloatingToolbar(
    color = COUITheme.colorScheme.primaryContainer,
    cornerRadius = 16.dp,
    outSidePadding = PaddingValues(24.dp),
    showDivider = false
) {
    Row(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) { // or Column
        IconButton(onClick = { /* Action 1 */ }) {
            Icon(COUIIcons.Edit, contentDescription = "Edit", tint = COUITheme.colorScheme.onPrimaryContainer)
        }
        IconButton(onClick = { /* Action 2 */ }) {
            Icon(COUIIcons.Delete, contentDescription = "Delete", tint = COUITheme.colorScheme.onPrimaryContainer)
        }
    }
}
```

### Toolbar with Divider

Set `showDivider = true` to draw a hairline divider ring around the toolbar; set `shadowElevation = 0.dp` to disable the drop shadow:

```kotlin
FloatingToolbar(
    showDivider = true,
    shadowElevation = 0.dp
) {
    Row(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = { /* Action 1 */ }) {
            Icon(COUIIcons.Edit, contentDescription = "Edit")
        }
        IconButton(onClick = { /* Action 2 */ }) {
            Icon(COUIIcons.Delete, contentDescription = "Delete")
        }
    }
}
```

### Vertically Arranged Content

```kotlin
FloatingToolbar {
    Column(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = { /* Action 1 */ }) {
            Icon(COUIIcons.Edit, contentDescription = "Edit")
        }
        IconButton(onClick = { /* Action 2 */ }) {
            Icon(COUIIcons.Delete, contentDescription = "Delete")
        }
    }
}
```

### Changing Position with Scaffold

```kotlin
Scaffold(
    floatingToolbar = {
        FloatingToolbar {
            // toolbar content
        }
    },
    floatingToolbarPosition = ToolbarPosition.CenterEnd // Place centered on the right edge
)
```

### Showing and Hiding

Toggle `visible` instead of wrapping the toolbar in `AnimatedVisibility`. The toolbar scales in about its centre (COUI 17 `COUIBottomFloatingToolbar` spring, response 0.5 / bounce 0.4) and scales out to nothing (response 0.25 / bounce 0) with its shadow intact; a fade from `AnimatedVisibility` would cut the shadow off while it runs. A hidden toolbar keeps its place in the layout but takes no touches.

```kotlin
var showToolbar by remember { mutableStateOf(true) }

FloatingToolbar(visible = showToolbar) {
    // toolbar content
}
```

### Bottom-Bar Material Blur

COUI 17 draws the toolbar with the framework bottom-bar material, a 25px backdrop blur with two blended color layers, over a transparent background. coui-ui has no backdrop capture of its own: provide a `MaterialBackdropProvider` through `LocalMaterialBackdrop`, and with `materialEffect` on the toolbar draws `FloatingToolbarDefaults.backdropSpec` in place of `color`. Without a provider it keeps `color`, COUI's non-blur fallback. The backdrop must record the page content, not the toolbar: keep the toolbar outside the recorded layer (for example in the `Scaffold` `floatingToolbar`).

```kotlin
CompositionLocalProvider(LocalMaterialBackdrop provides backdropProvider) {
    FloatingToolbar {
        // toolbar content
    }
}
```
