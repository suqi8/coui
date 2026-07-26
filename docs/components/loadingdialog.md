# LoadingDialog

`LoadingDialog` is a small always-centered dialog card with a rotating spinner and an optional message, mirroring ColorOS's rotating progress dialog (COUIRotatingDialogBuilder / `coui_progress_dialog_rotating.xml`): a 152dp-wide card with a 9dp corner radius, a 26dp spinner and 14sp label text.

Two variants are provided:

- `OverlayLoadingDialog` — rendered inside `Scaffold`'s `COUIPopupHost` (must be used within `Scaffold`).
- `WindowLoadingDialog` — rendered at window level, no `Scaffold` required.

::: danger Prerequisite
`OverlayLoadingDialog` depends on `Scaffold` providing `COUIPopupHost` to render popup content. Use `WindowLoadingDialog` when no `Scaffold` is available.
:::

## Import

```kotlin
import io.github.suqi8.coui.kmp.overlay.OverlayLoadingDialog
// or
import io.github.suqi8.coui.kmp.window.WindowLoadingDialog

import io.github.suqi8.coui.kmp.layout.LoadingDialogDefaults
```

## Basic Usage

```kotlin
var showLoading by remember { mutableStateOf(false) }

Scaffold {
    TextButton(
        text = "Start Loading",
        onClick = { showLoading = true }
    )

    OverlayLoadingDialog(
        show = showLoading,
        text = "Loading..."
    )

    // Hide the dialog when the task completes
    LaunchedEffect(showLoading) {
        if (showLoading) {
            doWork()
            showLoading = false
        }
    }
}
```

Without a message the spinner simply centers inside the card:

```kotlin
OverlayLoadingDialog(show = showLoading)
```

`WindowLoadingDialog` is used the same way, but renders in a platform window and needs no `Scaffold`:

```kotlin
var showLoading by remember { mutableStateOf(false) }

WindowLoadingDialog(
    show = showLoading,
    text = "Loading..."
)
```

## User Dismissal

By default the dialog cannot be dismissed by the user, matching a non-cancelable COUI progress dialog. Pass `onDismissRequest` to allow dismissal by tapping outside or pressing back:

```kotlin
OverlayLoadingDialog(
    show = showLoading,
    text = "Loading...",
    onDismissRequest = { showLoading = false }
)
```

## Properties

### OverlayLoadingDialog Properties

| Property Name        | Type          | Description                                                                                   | Default Value                          | Required |
| -------------------- | ------------- | --------------------------------------------------------------------------------------------- | -------------------------------------- | -------- |
| show                 | Boolean       | Whether to show the dialog                                                                    | -                                      | Yes      |
| modifier             | Modifier      | Modifier applied to the dialog                                                                | Modifier                               | No       |
| text                 | String?       | Message shown below the spinner                                                               | null                                   | No       |
| textColor            | Color         | Message text color                                                                            | LoadingDialogDefaults.textColor()      | No       |
| spinnerColor         | Color         | Spinner color                                                                                 | LoadingDialogDefaults.spinnerColor()   | No       |
| backgroundColor      | Color         | Card background color                                                                         | DialogDefaults.backgroundColor()       | No       |
| enableWindowDim      | Boolean       | Whether to enable dimming layer                                                               | true                                   | No       |
| onDismissRequest     | (() -> Unit)? | Called when the user taps outside or presses back; when null the dialog cannot be dismissed   | null                                   | No       |
| onDismissFinished    | (() -> Unit)? | Invoked after the hide animation completes                                                    | null                                   | No       |
| renderInRootScaffold | Boolean       | Whether to render the dialog in the root (outermost) Scaffold. When true, the dialog covers the full screen. When false, it renders within the current Scaffold's bounds | true | No       |

### WindowLoadingDialog Properties

Same as `OverlayLoadingDialog`, without `renderInRootScaffold` (the dialog always renders at window level):

| Property Name        | Type          | Description                                                                                   | Default Value                          | Required |
| -------------------- | ------------- | --------------------------------------------------------------------------------------------- | -------------------------------------- | -------- |
| show                 | Boolean       | Whether to show the dialog                                                                    | -                                      | Yes      |
| modifier             | Modifier      | Modifier applied to the dialog                                                                | Modifier                               | No       |
| text                 | String?       | Message shown below the spinner                                                               | null                                   | No       |
| textColor            | Color         | Message text color                                                                            | LoadingDialogDefaults.textColor()      | No       |
| spinnerColor         | Color         | Spinner color                                                                                 | LoadingDialogDefaults.spinnerColor()   | No       |
| backgroundColor      | Color         | Card background color                                                                         | DialogDefaults.backgroundColor()       | No       |
| enableWindowDim      | Boolean       | Whether to enable dimming layer                                                               | true                                   | No       |
| onDismissRequest     | (() -> Unit)? | Called when the user taps outside or presses back; when null the dialog cannot be dismissed   | null                                   | No       |
| onDismissFinished    | (() -> Unit)? | Invoked after the hide animation completes                                                    | null                                   | No       |

### LoadingDialogDefaults Object

#### Properties

| Property Name | Type | Description                                                       |
| ------------- | ---- | ----------------------------------------------------------------- |
| CardWidth     | Dp   | Card width, COUI `coui_spinner_layout_width` (152.dp)             |
| CardMinHeight | Dp   | Minimum card height, COUI `coui_spinner_layout_min_height` (120.dp) |
| CornerRadius  | Dp   | Card corner radius, COUI couiRoundCornerMRadius (9.dp)            |
| SpinnerSize   | Dp   | Spinner diameter, COUI `coui_spinner_loading_anim_width` (26.dp)  |

#### Functions

| Function Name  | Return Type | Description                                    |
| -------------- | ----------- | ---------------------------------------------- |
| textColor()    | Color       | Default message color (primary label color)    |
| spinnerColor() | Color       | Default spinner color (primary label color)    |
