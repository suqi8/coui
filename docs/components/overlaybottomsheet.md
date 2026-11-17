---
title: OverlayBottomSheet
requiresScaffoldHost: true
prerequisites:
  - Must be used within `Scaffold` to provide `COUIPopupHost`
  - Using outside `Scaffold` will cause popup content not to render
  - Multiple nested or side-by-side `Scaffold`s are supported without extra configuration
hostComponent: Scaffold
popupHost: COUIPopupHost
---

# OverlayBottomSheet

`OverlayBottomSheet` is the COUI panel (`COUIBottomSheetDialog`). On windows narrower than 600dp it slides up from the bottom edge; from 600dp on it floats centered, 64dp above the window bottom, with all four corners rounded and a scale (0.8 → 1) + fade entrance. Its width follows the COUI grid and never exceeds 540dp. The whole panel can be dragged; scrollable content scrolls first and drags the panel once it reaches the top.

Dragging down dismisses the panel when released with a downward fling faster than 500px/s or past half of its height (content drags dismiss whenever the last move was downward). Pulling up stretches the panel by 0.2 of the finger travel, at most 28dp, and springs back.

<div style="position: relative; height: 240px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=overlayBottomSheet" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

::: danger Prerequisite
This component depends on `Scaffold` providing `COUIPopupHost` to render popup content. It must be used within `Scaffold`, otherwise popup content will not render correctly.
:::

## Import

```kotlin
import io.github.suqi8.coui.kmp.overlay.OverlayBottomSheet
import io.github.suqi8.coui.kmp.theme.LocalDismissState
```

## Basic Usage

OverlayBottomSheet component provides basic bottom sheet functionality:

```kotlin
var showBottomSheet by remember { mutableStateOf(false) }

Scaffold {
    TextButton(
        text = "Show Bottom Sheet",
        onClick = { showBottomSheet = true }
    )

    OverlayBottomSheet(
        show = showBottomSheet,
        title = "Bottom Sheet Title",
        onDismissRequest = { showBottomSheet = false }
    ) {
        Text(text = "This is the content of the bottom sheet")
    }
}
```

## Properties

### OverlayBottomSheet Properties

| Property Name              | Type                      | Description                                                    | Default Value                              | Required |
| -------------------------- | ------------------------- |----------------------------------------------------------------| ------------------------------------------ |----------|
| show                       | Boolean                   | Whether to show the bottom sheet                               | -                                          | Yes      |
| modifier                   | Modifier                  | Modifier applied to the bottom sheet                           | Modifier                                   | No       |
| title                      | String?                   | Bottom sheet title                                             | null                                       | No       |
| startAction                | @Composable (() -> Unit)? | Optional composable for start action (e.g., close button)      | null                                       | No       |
| endAction                  | @Composable (() -> Unit)? | Optional composable for end action (e.g., submit button)       | null                                       | No       |
| backgroundColor            | Color                     | Bottom sheet background color                                  | BottomSheetDefaults.backgroundColor() | No       |
| enableWindowDim            | Boolean                   | Whether to enable dimming layer                                | true                                       | No       |
| cornerRadius               | Dp                        | Corner radius (top corners; all corners while floating)        | BottomSheetDefaults.cornerRadius      | No       |
| sheetMaxWidth              | Dp                        | Extra width cap on top of the COUI grid and the 540dp cap      | BottomSheetDefaults.maxWidth          | No       |
| onDismissRequest           | (() -> Unit)?             | Called when the user requests dismissal (outside tap or back)  | null                                       | No       |
| onDismissFinished          | (() -> Unit)?             | Invoked after the hide animation completes; not invoked if the hide is cancelled mid-flight (e.g., `show` toggled back to true) | null      | No       |
| outsideMargin              | DpSize                    | Bottom sheet external margin                                   | BottomSheetDefaults.outsideMargin     | No       |
| insideMargin               | DpSize                    | Bottom sheet internal content margin                           | BottomSheetDefaults.insideMargin      | No       |
| defaultWindowInsetsPadding | Boolean                   | Whether to apply default window insets padding                 | true                                       | No       |
| dragHandleColor            | Color                     | Drag indicator color                                           | BottomSheetDefaults.dragHandleColor() | No       |
| showDragHandle             | Boolean                   | Whether to show the drag handle (COUI hides it on regular panels and keeps a 10dp gap) | false                    | No       |
| handlePanel                | Boolean                   | COUI handle panel: shows the handle, fills the maximum height, rests half-expanded (portrait) or collapsed to 82dp (landscape) and settles between collapsed / half-expanded / expanded | false | No |
| showInMaxHeight            | Boolean                   | Take the maximum height instead of wrapping the content        | false                                      | No       |
| allowDismiss               | Boolean                   | Whether to allow dismissing the sheet via drag, back gesture or outside taps | true                         | No       |
| dismissOnClickOutside      | Boolean                   | Whether tapping outside dismisses the sheet (with `allowDismiss`) | true                                    | No       |
| draggable                  | Boolean                   | Whether the panel follows drag gestures                        | true                                       | No       |
| enablePullUp               | Boolean                   | Whether the panel can be pulled above its resting position     | true                                       | No       |
| enableNestedScroll         | Boolean                   | Whether to enable nested scrolling for the content             | true                                       | No       |
| renderInRootScaffold       | Boolean                   | Whether to render the bottom sheet in the root (outermost) Scaffold. When true, it covers the full screen. When false, it renders within the current Scaffold's bounds | true | No |
| backgroundModifier         | Modifier                  | Applied inside the sheet's corner clip, under the content; use it for the COUI 17 blurred background (pair it with a transparent `backgroundColor`) | Modifier | No       |
| materialStroke             | Boolean                   | Whether to draw the COUI 17 material stroke (edge lighting + inner shadow) a blurred sheet carries; needs runtime shaders | false | No       |
| content                    | @Composable () -> Unit    | Bottom sheet content                                           | -                                          | Yes      |

### BottomSheetDefaults Object

The BottomSheetDefaults object provides default settings for the OverlayBottomSheet component.

#### BottomSheetDefaults Properties

| Property Name | Type   | Description                          |
| ------------- | ------ | ------------------------------------ |
| cornerRadius  | Dp     | Default corner radius (45.dp, COUI 17 `coui_round_corner_xxxxl_radius_large_16_1`) |
| maxWidth      | Dp     | Default width cap (Dp.Infinity; width follows the COUI responsive grid and `PanelMaxWidth`) |
| PanelMaxWidth | Dp     | Hard panel width cap (540.dp, `coui_panel_max_width`) |
| LargeScreenWidthThreshold | Dp | Window width from which the panel floats centered (600.dp) |
| LargeScreenMarginBottom | Dp | Bottom margin of a floating panel (64.dp, `coui_bottom_sheet_margin_bottom_default`) |
| DragHandleShownHeight | Dp | Height of the handle area while shown (16.dp) |
| DragHandleHiddenHeight | Dp | Gap kept above the content while the handle is hidden (10.dp, `coui_panel_drag_view_hide_height`) |
| outsideMargin | DpSize | Default bottom sheet external margin (DpSize(0.dp, 0.dp)) |
| insideMargin  | DpSize | Default bottom sheet internal margin (DpSize(0.dp, 0.dp): COUI panels add no padding, the content sets its own margins; width = horizontal padding, height = bottom padding) |

#### BottomSheetDefaults Functions

| Function Name     | Return Type | Description                      |
| ----------------- | ----------- | -------------------------------- |
| backgroundColor() | Color       | Get default background color     |
| dragHandleColor() | Color       | Get default drag indicator color (`colorScheme.dragHandle`, COUI `?couiColorControls`) |

## Advanced Usage

### Handle Panel

```kotlin
OverlayBottomSheet(
    show = showSheet,
    title = "Handle Panel",
    handlePanel = true,
    onDismissRequest = { showSheet = false },
) {
    LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp)) {
        items(50) { Text("Item $it", modifier = Modifier.padding(vertical = 12.dp)) }
    }
}
```

The panel opens half-expanded; scrolling the list up first expands it, dragging down collapses it to the 82dp peek height or dismisses it.

### Blurred Material (COUI 17)

With the system material blur on, COUI 17 blurs the window behind the panel
(`COUIBottomSheetDialog.initBlurParams`: radius `coui_bottomdialog_blur_radius` 400px, light/dark mix
and blend colors, overlay/color-dodge blend) and draws an edge-lighting stroke along the panel.
coui-ui stays free of a blur dependency: pass the blur through `backgroundModifier`, e.g. with
coui-blur's `couiMaterialBlur`, and turn on `materialStroke`. Without runtime shaders keep the opaque
`BottomSheetDefaults.backgroundColor()`.

```kotlin
val backdrop = rememberLayerBackdrop()
val isDark = isSystemInDarkTheme()

Box(Modifier.fillMaxSize().layerBackdrop(backdrop)) { /* page content */ }

OverlayBottomSheet(
    show = showSheet,
    title = "Blurred Sheet",
    backgroundColor = Color.Transparent,
    backgroundModifier = Modifier.couiMaterialBlur(backdrop, MaterialBlurDefaults.bottomSheet(isDark)),
    materialStroke = true,
    onDismissRequest = { showSheet = false },
) {
    Text("The page behind the sheet is blurred.")
}
```

### Custom Styled Bottom Sheet

```kotlin
var showBottomSheet by remember { mutableStateOf(false) }

Scaffold {
    TextButton(
        text = "Show Custom Styled Bottom Sheet",
        onClick = { showBottomSheet = true }
    )

    OverlayBottomSheet(
        show = showBottomSheet,
        title = "Custom Style",
        backgroundColor = COUITheme.colorScheme.card,
        dragHandleColor = COUITheme.colorScheme.primary,
        outsideMargin = DpSize(16.dp, 0.dp),
        insideMargin = DpSize(32.dp, 16.dp),
        onDismissRequest = { showBottomSheet = false }
    ) {
        Column {
            Text("Custom styled bottom sheet")
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                text = "Close",
                onClick = { showBottomSheet = false },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
```

### Bottom Sheet with List Content

```kotlin
var showBottomSheet by remember { mutableStateOf(false) }
var selectedItem by remember { mutableStateOf("") }

Scaffold {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TextButton(
            text = "Show Selection List",
            onClick = { showBottomSheet = true }
        )
        
        Text("Selected: $selectedItem")
    }

    OverlayBottomSheet(
        show = showBottomSheet,
        title = "Select Item",
        onDismissRequest = { showBottomSheet = false }
    ) {
        LazyColumn {
            items(20) { index ->
                Text(
                    text = "Item ${index + 1}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedItem = "Item ${index + 1}"
                            showBottomSheet = false
                        }
                        .padding(vertical = 12.dp)
                )
            }
        }
    }
}
```

### Without Dimming Layer

```kotlin
var showBottomSheet by remember { mutableStateOf(false) }

Scaffold {
    TextButton(
        text = "Show Bottom Sheet Without Dim",
        onClick = { showBottomSheet = true }
    )

    OverlayBottomSheet(
        show = showBottomSheet,
        title = "No Dimming",
        enableWindowDim = false,
        onDismissRequest = { showBottomSheet = false }
    ) {
        Text("This bottom sheet has no background dimming layer")
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(
            text = "Close",
            onClick = { showBottomSheet = false },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
```

### Bottom Sheet with Action Buttons

```kotlin
var showBottomSheet by remember { mutableStateOf(false) }

Scaffold {
    TextButton(
        text = "Show Bottom Sheet with Actions",
        onClick = { showBottomSheet = true }
    )

    OverlayBottomSheet(
        show = showBottomSheet,
        title = "Action Sheet",
        startAction = {
            val dismiss = LocalDismissState.current
            TextButton(
                text = "Cancel",
                onClick = { dismiss?.invoke() }
            )
        },
        endAction = {
            val dismiss = LocalDismissState.current
            TextButton(
                text = "Confirm",
                onClick = { dismiss?.invoke() },
                colors = ButtonDefaults.textButtonColorsPrimary()
            )
        },
        onDismissRequest = { showBottomSheet = false }
    ) {
        Text("Content with custom header actions")
        Spacer(modifier = Modifier.height(16.dp))
        Text("Start and end action buttons are displayed in the header")
    }
}
```

### Bottom Sheet with Form

```kotlin
var showBottomSheet by remember { mutableStateOf(false) }
var textFieldValue by remember { mutableStateOf("") }
var switchState by remember { mutableStateOf(false) }

Scaffold {
    TextButton(
        text = "Show Form Bottom Sheet",
        onClick = { showBottomSheet = true }
    )

    OverlayBottomSheet(
        show = showBottomSheet,
        title = "Settings Form",
        onDismissRequest = { showBottomSheet = false }
    ) {
        Card(
            colors = CardDefaults.defaultColors(
                color = COUITheme.colorScheme.secondaryContainer,
            ),
        ) {
            TextField(
                modifier = Modifier.padding(vertical = 12.dp),
                value = textFieldValue,
                label = "Enter content",
                maxLines = 1,
                onValueChange = { textFieldValue = it }
            )
            
            SwitchPreference(
                title = "Switch Option",
                checked = switchState,
                onCheckedChange = { switchState = it }
            )
        }
        
        Spacer(Modifier.height(12.dp))
        
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                text = "Cancel",
                onClick = { showBottomSheet = false },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(20.dp))
            TextButton(
                text = "Confirm",
                onClick = { showBottomSheet = false },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColorsPrimary()
            )
        }
    }
}
```

### Adaptive Content Height

```kotlin
var showBottomSheet by remember { mutableStateOf(false) }

Scaffold {
    TextButton(
        text = "Show Adaptive Height Bottom Sheet",
        onClick = { showBottomSheet = true }
    )

    OverlayBottomSheet(
        show = showBottomSheet,
        title = "Adaptive Height",
        onDismissRequest = { showBottomSheet = false }
    ) {
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            Text("The height adapts to content")
            Spacer(modifier = Modifier.height(16.dp))
            Text("Add as much content as needed")
            Spacer(modifier = Modifier.height(16.dp))
            Text("But will not cover the status bar area")
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                text = "Close",
                onClick = { showBottomSheet = false },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
```

### Non-Dismissible Bottom Sheet

```kotlin
var showBottomSheet by remember { mutableStateOf(false) }

Scaffold {
    TextButton(
        text = "Show Non-Dismissible Bottom Sheet",
        onClick = { showBottomSheet = true }
    )

    OverlayBottomSheet(
        show = showBottomSheet,
        title = "Non-Dismissible",
        allowDismiss = false,
        onDismissRequest = { showBottomSheet = false }
    ) {
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            Text("This bottom sheet cannot be dismissed by dragging or back gesture")
            Spacer(modifier = Modifier.height(16.dp))
            Text("You must explicitly close it using the button below")
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                text = "Close",
                onClick = { showBottomSheet = false },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
```
