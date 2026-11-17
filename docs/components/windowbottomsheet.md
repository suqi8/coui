---
title: WindowBottomSheet
requiresScaffoldHost: false
prerequisites:
  - Can be used anywhere, does not require `Scaffold` or `COUIPopupHost`
  - Renders at window level
hostComponent: None
popupHost: None
---

# WindowBottomSheet

`WindowBottomSheet` is a window-level bottom sheet component. It renders using platform `Dialog` and does not require `Scaffold` or `COUIPopupHost`. It supports system back gesture dismissal and a composition local to request dismiss from inside content.

It follows the COUI panel (`COUIBottomSheetDialog`): on windows narrower than 600dp it slides up from the bottom edge; from 600dp on it floats centered, 64dp above the window bottom, with all four corners rounded and a scale (0.8 → 1) + fade entrance. Its width never exceeds 540dp. The whole panel can be dragged; scrollable content drags it once scrolled to the top. Releasing it with a downward fling faster than 500px/s or past half of its height dismisses it; pulling up stretches it by at most 28dp.

<div style="position: relative; height: 240px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=windowBottomSheet" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

::: tip
This component is independent of `Scaffold` and can be used in any composable scope.
:::

## Import

```kotlin
import io.github.suqi8.coui.kmp.window.WindowBottomSheet
import io.github.suqi8.coui.kmp.theme.LocalDismissState
```

## Basic Usage

`WindowBottomSheet` component provides basic bottom sheet functionality:

```kotlin
var showBottomSheet by remember { mutableStateOf(false) }

// Can be used anywhere
TextButton(
    text = "Show Window Bottom Sheet",
    onClick = { showBottomSheet = true }
)

WindowBottomSheet(
    show = showBottomSheet,
    title = "Window Bottom Sheet Title",
    onDismissRequest = { showBottomSheet = false }
) {
    val dismiss = LocalDismissState.current
    Text(text = "This is the content of the window bottom sheet")
    TextButton(
        text = "Close",
        onClick = { dismiss?.invoke() }
    )
}
```

## Properties

### WindowBottomSheet Properties

| Property Name              | Type                      | Description                                                    | Default Value                                | Required |
| -------------------------- | ------------------------- |----------------------------------------------------------------|----------------------------------------------| -------- |
| show                       | Boolean                   | Whether to show the bottom sheet                               | -                                            | Yes      |
| modifier                   | Modifier                  | Modifier applied to the bottom sheet                           | Modifier                                     | No       |
| title                      | String?                   | Bottom sheet title                                             | null                                         | No       |
| startAction                | @Composable (() -> Unit)? | Optional composable for start action (e.g., close button)      | null                                         | No       |
| endAction                  | @Composable (() -> Unit)? | Optional composable for end action (e.g., submit button)       | null                                         | No       |
| backgroundColor            | Color                     | Bottom sheet background color                                  | BottomSheetDefaults.backgroundColor()  | No       |
| enableWindowDim            | Boolean                   | Whether to enable dimming layer                                | true                                         | No       |
| cornerRadius               | Dp                        | Corner radius (top corners; all corners while floating)        | BottomSheetDefaults.cornerRadius       | No       |
| sheetMaxWidth              | Dp                        | Extra width cap on top of the COUI grid and the 540dp cap      | BottomSheetDefaults.maxWidth           | No       |
| onDismissRequest           | (() -> Unit)?             | Called when the user requests dismissal (outside tap or back)  | null                                         | No       |
| onDismissFinished          | (() -> Unit)?             | Invoked after the hide animation completes; not invoked if the hide is cancelled mid-flight (e.g., `show` toggled back to true) | null        | No       |
| outsideMargin              | DpSize                    | Bottom sheet external margin                                   | BottomSheetDefaults.outsideMargin      | No       |
| insideMargin               | DpSize                    | Bottom sheet internal content margin                           | BottomSheetDefaults.insideMargin       | No       |
| defaultWindowInsetsPadding | Boolean                   | Whether to apply default window insets padding                 | true                                         | No       |
| dragHandleColor            | Color                     | Drag indicator color                                           | BottomSheetDefaults.dragHandleColor()  | No       |
| showDragHandle             | Boolean                   | Whether to show the drag handle (COUI hides it on regular panels and keeps a 10dp gap) | false                    | No       |
| handlePanel                | Boolean                   | COUI handle panel: shows the handle, fills the maximum height, rests half-expanded (portrait) or collapsed to 82dp (landscape) and settles between collapsed / half-expanded / expanded | false | No |
| showInMaxHeight            | Boolean                   | Take the maximum height instead of wrapping the content        | false                                        | No       |
| allowDismiss               | Boolean                   | Whether to allow dismissing the sheet via drag, back gesture or outside taps | true                           | No       |
| dismissOnClickOutside      | Boolean                   | Whether tapping outside dismisses the sheet (with `allowDismiss`) | true                                      | No       |
| draggable                  | Boolean                   | Whether the panel follows drag gestures                        | true                                         | No       |
| enablePullUp               | Boolean                   | Whether the panel can be pulled above its resting position     | true                                         | No       |
| enableNestedScroll         | Boolean                   | Whether to enable nested scrolling for the content             | true                                         | No       |
| backgroundModifier         | Modifier                  | Applied inside the sheet's corner clip, under the content; use it for the COUI 17 blurred background (pair it with a transparent `backgroundColor`) | Modifier | No       |
| materialStroke             | Boolean                   | Whether to draw the COUI 17 material stroke (edge lighting + inner shadow) a blurred sheet carries; needs runtime shaders | false | No       |
| content                    | @Composable () -> Unit    | Bottom sheet content                                           | -                                            | Yes      |

### BottomSheetDefaults Object

The BottomSheetDefaults object provides default settings for the bottom sheet component.

#### BottomSheetDefaults Properties

| Property Name | Type   | Description                          |
| ------------- | ------ | ------------------------------------ |
| cornerRadius  | Dp     | Default corner radius (45.dp, COUI 17 `coui_round_corner_xxxxl_radius_large_16_1`) |
| maxWidth      | Dp     | Default width cap (Dp.Infinity; width follows the COUI responsive grid and `PanelMaxWidth`) |
| PanelMaxWidth | Dp     | Hard panel width cap (540.dp, `coui_panel_max_width`) |
| LargeScreenWidthThreshold | Dp | Window width from which the panel floats centered (600.dp) |
| LargeScreenMarginBottom | Dp | Bottom margin of a floating panel (64.dp) |
| DragHandleShownHeight | Dp | Height of the handle area while shown (16.dp) |
| DragHandleHiddenHeight | Dp | Gap kept above the content while the handle is hidden (10.dp) |
| outsideMargin | DpSize | Default bottom sheet external margin (DpSize(0.dp, 0.dp)) |
| insideMargin  | DpSize | Default bottom sheet internal margin (DpSize(0.dp, 0.dp): COUI panels add no padding, the content sets its own margins) |

#### BottomSheetDefaults Functions

| Function Name     | Return Type | Description                      |
| ----------------- | ----------- | -------------------------------- |
| backgroundColor() | Color       | Get default background color     |
| dragHandleColor() | Color       | Get default drag indicator color (`colorScheme.dragHandle`, COUI `?couiColorControls`) |

## Advanced Usage

### Blurred Material (COUI 17)

`backgroundModifier` and `materialStroke` work as on [OverlayBottomSheet](./overlaybottomsheet#blurred-material-coui-17).
A `WindowBottomSheet` lives in its own platform window, though, so a `LayerBackdrop` recorded in the
main window is not guaranteed to line up with it; prefer `OverlayBottomSheet` for the blurred material.

### Action Buttons in Header

Use `startAction` and `endAction` to place buttons in the header area. `LocalDismissState` is provided in both action slots and the content slot:

```kotlin
var showBottomSheet by remember { mutableStateOf(false) }

WindowBottomSheet(
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
            onClick = { dismiss?.invoke() }
        )
    },
    onDismissRequest = { showBottomSheet = false }
) {
    Text("Content with header action buttons")
}
```

### Dismissing from Content

You can use `LocalDismissState` to dismiss the bottom sheet from within its content:

```kotlin
WindowBottomSheet(
    show = showBottomSheet,
    title = "Dismiss Example",
    onDismissRequest = { showBottomSheet = false }
) {
    val dismiss = LocalDismissState.current

    TextButton(
        text = "Close Bottom Sheet",
        onClick = { dismiss?.invoke() }
    )
}
```

### Preventing Dismissal

Set `allowDismiss = false` to ignore drag-down and back-gesture dismissal, so the sheet can only be closed programmatically:

```kotlin
var showBottomSheet by remember { mutableStateOf(false) }

WindowBottomSheet(
    show = showBottomSheet,
    title = "Processing",
    allowDismiss = false, // drag and back gesture will not dismiss the sheet
    onDismissRequest = { showBottomSheet = false }
) {
    Text("This sheet can only be closed programmatically")
    TextButton(
        text = "Done",
        onClick = { showBottomSheet = false }
    )
}
```
