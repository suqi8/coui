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
| content                    | @Composable () -> Unit | Dialog content                                                | -                                      | Yes      |

### DialogDefaults

#### Properties

| Name          | Type   | Description                               |
| ------------- | ------ | ----------------------------------------- |
| CornerRadius  | Dp     | Dialog panel corner radius (19.dp)        |
| MaxWidth      | Dp     | Maximum dialog content width (392.dp)     |
| outsideMargin | DpSize | Default outer margin for dialog (16, 24)  |
| insideMargin  | DpSize | Default margin for the built-in title/summary texts (24, 24); the content slot is unpadded |
| ButtonBarMinHeight | Dp | Min height of a horizontal dialog button bar (58.dp) |
| ButtonBarInsideMargin | PaddingValues | Paddings of a button in a horizontal bar (24dp horizontal, 12dp top, 22dp bottom); the panel bottom inset is carried by the buttons |
| ButtonBarDividerThickness | Dp | Thickness of the divider between horizontal bar buttons (1.dp) |
| ButtonBarDividerInsetTop | Dp | Top inset of the divider between horizontal bar buttons (17.dp) |
| ButtonBarDividerInsetBottom | Dp | Bottom inset of the divider between horizontal bar buttons (21.dp) |

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

### Presentation Overrides

By default the dialog is bottom-attached on compact windows and centered on large windows (>= 840dp x 480dp). Use `largeScreen`, `cornerRadius` and `maxWidth` to override the presentation:

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
