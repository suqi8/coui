# ProgressDialog

`ProgressDialog` is ColorOS's horizontal progress dialog (`COUIAlertDialog.Progress`, `coui_progress_dialog_horizontal.xml`): a regular bottom dialog whose custom view is a centered 200dp x 4dp progress bar on a `?couiColorDivider` track, 16dp above and below. With a cancel button it is `COUIAlertDialog.Progress.Cancelable`.

Two variants are provided:

- `OverlayProgressDialog` — rendered inside `Scaffold`'s `COUIPopupHost` (must be used within `Scaffold`).
- `WindowProgressDialog` — rendered at window level, no `Scaffold` required.

For an indeterminate wait use [LoadingDialog](./loadingdialog) instead.

<div style="position: relative; height: 400px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=progressDialog" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.overlay.OverlayProgressDialog
// or
import io.github.suqi8.coui.kmp.window.WindowProgressDialog

import io.github.suqi8.coui.kmp.layout.ProgressDialogDefaults
```

## Basic Usage

```kotlin
var showDialog by remember { mutableStateOf(false) }
var progress by remember { mutableFloatStateOf(0f) }

OverlayProgressDialog(
    show = showDialog,
    progress = progress,
    title = "Downloading",
    cancelAction = DialogButtonBarAction(text = "Cancel", onClick = { showDialog = false }),
)
```

## Properties

### OverlayProgressDialog Properties

| Property Name        | Type                    | Description                                               | Default Value                         | Required |
| -------------------- | ----------------------- | --------------------------------------------------------- | ------------------------------------- | -------- |
| show                 | Boolean                 | Whether to show the dialog                                | -                                     | Yes      |
| progress             | Float                   | Progress between 0 and 1                                  | -                                     | Yes      |
| modifier             | Modifier                | Modifier applied to the dialog                            | Modifier                              | No       |
| backgroundModifier   | Modifier                | Applied inside the panel's corner clip, under the content | Modifier                              | No       |
| title                | String?                 | Dialog title                                              | null                                  | No       |
| titleColor           | Color                   | Title color                                               | DialogDefaults.titleColor()           | No       |
| summary              | String?                 | Dialog message                                            | null                                  | No       |
| summaryColor         | Color                   | Message color                                             | DialogDefaults.summaryColor()         | No       |
| cancelAction         | DialogButtonBarAction?  | Cancel button; switches to the cancelable form            | null                                  | No       |
| progressColors       | ProgressIndicatorColors | Progress bar colors                                       | ProgressDialogDefaults.progressColors() | No     |
| backgroundColor      | Color                   | Panel background color                                    | DialogDefaults.backgroundColor()      | No       |
| enableWindowDim      | Boolean                 | Whether to enable the dimming layer                       | true                                  | No       |
| onDismissRequest     | (() -> Unit)?           | Called when the user taps outside or presses back         | null                                  | No       |
| onDismissFinished    | (() -> Unit)?           | Called after the hide animation completes                 | null                                  | No       |
| renderInRootScaffold | Boolean                 | Whether to render the dialog in the root Scaffold         | true                                  | No       |
| materialStroke       | Boolean                 | Whether to draw the COUI 17 material stroke of a blurred panel | false                            | No       |

`WindowProgressDialog` takes the same properties without `renderInRootScaffold`.

### ProgressDialogDefaults Object

| Property Name  | Type | Description                                                     |
| -------------- | ---- | --------------------------------------------------------------- |
| ProgressWidth  | Dp   | Progress bar width (200.dp, `coui_loading_dialog_progress_width`) |
| ProgressHeight | Dp   | Progress bar height (4.dp, `coui_loading_dialog_progress_height`) |
| PaddingTop     | Dp   | Padding above the bar (16.dp, `coui_progress_dialog_padding_top`) |
| PaddingBottom  | Dp   | Padding below the bar (16.dp, `coui_progress_cancelable_dialog_padding_bottom`) |

| Function Name    | Return Type             | Description                                       |
| ---------------- | ----------------------- | ------------------------------------------------- |
| progressColors() | ProgressIndicatorColors | Default progress colors over the divider track   |
