# InputDialog

`InputDialog` is ColorOS's single-input dialog in its two forms:

- **SingleEdit** (`COUIAlertDialog.SingleEdit`, `coui_single_edit_bottom_alert_dialog_layout.xml`): a line-style edit text with quick delete, up to five lines, 24dp side margins and 8dp above and below; optionally a password toggle.
- **SingleInput** (`COUIAlertDialog.SingleInput`, `coui_single_inputview_bottom_alert_dialog_layout.xml`): an [InputView](./inputview) with an input counter, 24dp side margins and 18dp below. Taken when `maxCount` is set.

Both are bottom assignment dialogs (centered on large screens) and focus the field when they open.

Two variants are provided:

- `OverlayInputDialog` — rendered inside `Scaffold`'s `COUIPopupHost` (must be used within `Scaffold`).
- `WindowInputDialog` — rendered at window level, no `Scaffold` required.

<div style="position: relative; height: 420px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=inputDialog" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.overlay.OverlayInputDialog
// or
import io.github.suqi8.coui.kmp.window.WindowInputDialog

import io.github.suqi8.coui.kmp.layout.InputDialogDefaults
```

## Basic Usage

```kotlin
var showDialog by remember { mutableStateOf(false) }
var name by remember { mutableStateOf("") }

OverlayInputDialog(
    show = showDialog,
    value = name,
    onValueChange = { name = it },
    title = "Rename",
    label = "Enter a name",
    negative = DialogButtonBarAction(text = "Cancel", onClick = { showDialog = false }),
    positive = DialogButtonBarAction(text = "Save", enabled = name.isNotEmpty(), onClick = { showDialog = false }),
    onDismissRequest = { showDialog = false },
)
```

With an input counter (SingleInput):

```kotlin
OverlayInputDialog(
    show = showDialog,
    value = note,
    onValueChange = { note = it },
    title = "Add a note",
    maxCount = 30,
    positive = DialogButtonBarAction(text = "Save", onClick = { showDialog = false }),
)
```

## Properties

### OverlayInputDialog Properties

| Property Name        | Type                   | Description                                               | Default Value                        | Required |
| -------------------- | ---------------------- | --------------------------------------------------------- | ------------------------------------ | -------- |
| show                 | Boolean                | Whether to show the dialog                                | -                                    | Yes      |
| value                | String                 | Input text                                                | -                                    | Yes      |
| onValueChange        | (String) -> Unit       | Called when the input text changes                        | -                                    | Yes      |
| modifier             | Modifier               | Modifier applied to the dialog                            | Modifier                             | No       |
| backgroundModifier   | Modifier               | Applied inside the panel's corner clip, under the content | Modifier                             | No       |
| title                | String?                | Dialog title                                              | null                                 | No       |
| titleColor           | Color                  | Title color                                               | DialogDefaults.titleColor()          | No       |
| summary              | String?                | Dialog message                                            | null                                 | No       |
| summaryColor         | Color                  | Message color                                             | DialogDefaults.summaryColor()        | No       |
| label                | String                 | Hint shown while the input is empty                       | ""                                   | No       |
| maxCount             | Int?                   | Max characters; when set the dialog takes the SingleInput form | null                            | No       |
| showPasswordToggle   | Boolean                | Whether the field masks its text behind a visibility toggle | false                              | No       |
| keyboardOptions      | KeyboardOptions        | Keyboard options of the field                             | KeyboardOptions.Default              | No       |
| textFieldColors      | TextFieldColors        | Field colors                                              | TextFieldDefaults.textFieldColors()  | No       |
| negative             | DialogButtonBarAction? | Negative button                                           | null                                 | No       |
| positive             | DialogButtonBarAction? | Positive button                                           | null                                 | No       |
| neutral              | DialogButtonBarAction? | Neutral button                                            | null                                 | No       |
| backgroundColor      | Color                  | Panel background color                                    | DialogDefaults.backgroundColor()     | No       |
| enableWindowDim      | Boolean                | Whether to enable the dimming layer                       | true                                 | No       |
| onDismissRequest     | (() -> Unit)?          | Called when the user taps outside or presses back         | null                                 | No       |
| onDismissFinished    | (() -> Unit)?          | Called after the hide animation completes                 | null                                 | No       |
| renderInRootScaffold | Boolean                | Whether to render the dialog in the root Scaffold         | true                                 | No       |
| materialStroke       | Boolean                | Whether to draw the COUI 17 material stroke of a blurred panel | false                           | No       |

`WindowInputDialog` takes the same properties without `renderInRootScaffold`.

### InputDialogDefaults Object

| Property Name         | Type | Description                                         |
| --------------------- | ---- | --------------------------------------------------- |
| FieldMarginHorizontal | Dp   | Side margin of the field (24.dp, `common_margin`)   |
| EditMarginVertical    | Dp   | Margin above and below the SingleEdit field (8.dp)  |
| InputMarginBottom     | Dp   | Margin below the SingleInput field (18.dp)          |
| MaxLines              | Int  | Max lines of the input (5)                          |
