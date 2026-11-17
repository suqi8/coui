---
title: WindowDialog
requiresScaffoldHost: false
prerequisites:
  - Can be used anywhere, does not require `Scaffold` or `COUIPopupHost`
  - Renders at window level
hostComponent: None
popupHost: None
---

# WindowDialog

`WindowDialog` is a window-level dialog component. It renders using platform `Dialog` and does not require `Scaffold` or `COUIPopupHost`. It supports large-screen optimized animations, system back gesture dismissal, and a composition local to request dismiss from inside content.

<div style="position: relative; height: 240px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=windowDialog" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

::: tip
This component is independent of `Scaffold` and can be used in any composable scope.
:::

## Import

```kotlin
import io.github.suqi8.coui.kmp.window.WindowDialog
import io.github.suqi8.coui.kmp.theme.LocalDismissState
```

## Basic Usage

```kotlin
var showDialog by remember { mutableStateOf(false) }

TextButton(
    text = "Open",
    onClick = { showDialog = true }
)

WindowDialog(
    title = "WindowDialog",
    summary = "A basic window-level dialog",
    show = showDialog,
    onDismissRequest = { showDialog = false }
) {
    val dismiss = LocalDismissState.current
    TextButton(
        text = "Confirm",
        onClick = { dismiss?.invoke() },
        modifier = Modifier.fillMaxWidth()
    )
}
```

## Properties

### WindowDialog Properties

| Property Name              | Type                   | Description                                                   | Default Value                          | Required |
| -------------------------- | ---------------------- | ------------------------------------------------------------- | -------------------------------------- | -------- |
| show                       | Boolean                | Whether to show the dialog                                    | -                                      | Yes      |
| modifier                   | Modifier               | Root content modifier                                         | Modifier                               | No       |
| title                      | String?                | Dialog title                                                  | null                                   | No       |
| titleColor                 | Color                  | Title color                                                   | DialogDefaults.titleColor()      | No       |
| summary                    | String?                | Dialog summary                                                | null                                   | No       |
| summaryColor               | Color                  | Summary color                                                 | DialogDefaults.summaryColor()    | No       |
| backgroundColor            | Color                  | Dialog background color                                       | DialogDefaults.backgroundColor() | No       |
| enableWindowDim            | Boolean                | Whether to enable dimming layer                               | true                                   | No       |
| onDismissRequest           | (() -> Unit)?          | Called when the user requests dismissal (outside tap or back) | null                                   | No       |
| onDismissFinished          | (() -> Unit)?          | Invoked after the hide animation completes; not invoked if the hide is cancelled mid-flight (e.g., `show` toggled back to true) | null              | No       |
| outsideMargin              | DpSize                 | Outer margin (window edges)                                   | DialogDefaults.outsideMargin     | No       |
| insideMargin               | DpSize                 | Margin for the built-in title/summary texts (width = horizontal padding, height = padding above the title); the content slot is unpadded | DialogDefaults.insideMargin      | No       |
| defaultWindowInsetsPadding | Boolean                | Apply default insets padding (IME, nav, caption)              | true                                   | No       |
| maxWidth                   | Dp                     | Maximum dialog content width                                  | DialogDefaults.MaxWidth          | No       |
| largeScreen                | Boolean?               | Override for the large-screen presentation (centered scale/fade instead of bottom slide-in); if null, detected from the window size | null | No       |
| cornerRadius               | Dp?                    | Corner radius override; if null, DialogDefaults.CornerRadius for the centered presentation, or derived from the screen corner radius (clamped to 32dp..48dp) when bottom-attached | null | No       |
| backgroundModifier         | Modifier               | Applied inside the panel's corner clip, under the content; use it for the COUI 17 blurred background (pair it with a transparent `backgroundColor`) | Modifier | No       |
| materialStroke             | Boolean                | Whether to draw the COUI 17 material stroke (edge lighting + inner shadow) a blurred dialog carries; needs runtime shaders. Also turns a `DialogButtonBar` inside material | false | No       |
| customContent              | (@Composable () -> Unit)? | Custom view slot between the summary and `content` (COUI `customPanel`), padded 8dp below; with it the summary keeps only 4dp below and a title-only dialog drops the extra 16dp under the title | null | No       |
| content                    | @Composable () -> Unit | Dialog content                                                | -                                      | Yes      |

### DialogDefaults

#### Properties

| Name          | Type   | Description                               |
| ------------- | ------ | ----------------------------------------- |
| CornerRadius  | Dp     | Dialog panel corner radius (45.dp, `coui_round_corner_xxxxl_radius_large_16_1`) |
| MaxWidth      | Dp     | Maximum visible panel width (360.dp: `coui_dialog_max_width` 392dp minus the two 16dp side insets) |
| CustomContentPaddingBottom | Dp | Bottom padding of `customContent` (8.dp, `coui_alert_dialog_customer_layout_padding_bottom`) |
| MaxHeight     | Dp     | Maximum panel height (600.dp: `coui_dialog_max_height` 632dp minus the 32dp bottom margin) |
| outsideMargin | DpSize | Default dialog external margin (16, 32); the height is the bottom gap without a navigation bar |
| MarginBottomWithNavigationBar | Dp | Gap between a bottom-aligned dialog and the screen edge under three-button navigation (60.dp) |
| insideMargin  | DpSize | Default margin for the built-in title/summary texts (24, 24); the content slot is unpadded |
| ButtonBarMinHeight | Dp | Min height of a horizontal dialog button bar and its buttons (44.dp) |
| ButtonBarInsideMargin | PaddingValues | Paddings of a bar button (12dp horizontal, 4dp vertical) |

#### Functions

| Name              | Return Type | Description                         |
| ----------------- | ----------- | ----------------------------------- |
| titleColor()      | Color       | Get default title color             |
| summaryColor()    | Color       | Get default summary color           |
| backgroundColor() | Color       | Get default dialog background color |

### LocalDismissState

Provides a `(() -> Unit)?` function to close the current popup from within the content. This is a unified dismiss state provided by all overlay components.

```kotlin
val dismiss = LocalDismissState.current
TextButton(
    text = "Close",
    onClick = { dismiss?.invoke() }
)
```

## Advanced Usage

### Blurred Material (COUI 17)

`backgroundModifier` and `materialStroke` work as on [OverlayDialog](./overlaydialog#blurred-material-coui-17).
A `WindowDialog` lives in its own platform window, though, so a `LayerBackdrop` recorded in the
main window is not guaranteed to line up with it; prefer `OverlayDialog` for the blurred material.

### Presentation Overrides

By default the dialog is bottom-attached on compact windows and centered on large windows (>= 840dp x 480dp in either orientation, except tall portrait windows narrower than 960dp). Use `largeScreen`, `cornerRadius` and `maxWidth` to override the presentation:

```kotlin
var showDialog by remember { mutableStateOf(false) }

WindowDialog(
    show = showDialog,
    title = "Custom Presentation",
    summary = "Forced centered presentation with custom shape",
    largeScreen = true,   // always use the centered scale/fade presentation
    cornerRadius = 24.dp, // override the panel corner radius
    maxWidth = 320.dp,    // narrower content width cap
    onDismissRequest = { showDialog = false }
) {
    val dismiss = LocalDismissState.current
    TextButton(
        text = "OK",
        onClick = { dismiss?.invoke() },
        modifier = Modifier.fillMaxWidth()
    )
}
```
