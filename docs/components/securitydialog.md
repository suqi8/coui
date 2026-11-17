# SecurityDialog

`SecurityDialog` is a security statement dialog mirroring ColorOS's COUISecurityAlertDialogBuilder (`coui_security_alert_dialog_statement_or_checkbox.xml`): a regular alert dialog extended with a statement paragraph (with an optional tappable link), a "Don't ask me again" checkbox row and a cancel / confirm [DialogButtonBar](./dialogbuttonbar) of COUI 17 filled capsule buttons.

<div style="position: relative; height: 420px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=securityDialog" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

Two variants are provided:

- `OverlaySecurityDialog` — rendered inside `Scaffold`'s `COUIPopupHost` (must be used within `Scaffold`).
- `WindowSecurityDialog` — rendered at window level, no `Scaffold` required.

::: danger Prerequisite
`OverlaySecurityDialog` depends on `Scaffold` providing `COUIPopupHost` to render popup content. Use `WindowSecurityDialog` when no `Scaffold` is available.
:::

## Import

```kotlin
import io.github.suqi8.coui.kmp.overlay.OverlaySecurityDialog
// or
import io.github.suqi8.coui.kmp.window.WindowSecurityDialog

import io.github.suqi8.coui.kmp.layout.SecurityDialogDefaults
import io.github.suqi8.coui.kmp.layout.SecurityDialogColors
```

## Basic Usage

```kotlin
var showDialog by remember { mutableStateOf(false) }

Scaffold {
    TextButton(
        text = "Show Security Dialog",
        onClick = { showDialog = true }
    )

    OverlaySecurityDialog(
        show = showDialog,
        title = "Security Notice",
        summary = "This feature needs to connect to the network.",
        statement = "Tap and view Privacy Policy for more information.",
        statementLinkText = "Privacy Policy",
        onLinkClick = { /* Open the privacy policy */ },
        onConfirm = { dontRemind ->
            showDialog = false
            if (dontRemind) { /* Persist the choice */ }
        },
        onCancel = { showDialog = false }
    )
}
```

- The substring of `statement` equal to `statementLinkText` is rendered in the accent color and invokes `onLinkClick` when tapped.
- Pressing back invokes `onCancel`, matching COUI reporting the back key as the negative selection. Tapping outside does nothing: COUI makes the dialog non-cancelable once a selection listener is set.
- `onCheckedChange` reports every checkbox toggle, like COUI's `onSelected(0, isChecked)`.
- Passing `checkboxText = null` hides the checkbox row (equivalent to `setHasCheckBox(false)`).

## Properties

### OverlaySecurityDialog / WindowSecurityDialog Properties

| Property Name        | Type              | Description                                                                                | Default Value                                | Required |
| -------------------- | ----------------- | ------------------------------------------------------------------------------------------ | -------------------------------------------- | -------- |
| show                 | Boolean           | Whether to show the dialog                                                                 | -                                            | Yes      |
| onConfirm            | (Boolean) -> Unit | Called on confirm with the current checkbox state                                          | -                                            | Yes      |
| onCancel             | () -> Unit        | Called on cancel button or back press (an outside tap does nothing)                        | -                                            | Yes      |
| modifier             | Modifier          | Modifier applied to the dialog                                                             | Modifier                                     | No       |
| backgroundModifier   | Modifier          | Applied inside the panel's corner clip, under the content; use it for the COUI 17 blurred background (pair it with a transparent `backgroundColor`) | Modifier | No       |
| title                | String?           | Dialog title                                                                               | null                                         | No       |
| summary              | String?           | Dialog summary (message)                                                                   | null                                         | No       |
| statement            | String?           | Statement paragraph; hidden when null                                                      | null                                         | No       |
| statementLinkText    | String?           | Substring of `statement` rendered as a tappable link                                       | null                                         | No       |
| onLinkClick          | (() -> Unit)?     | Called when the statement link is tapped                                                   | null                                         | No       |
| checkboxText         | String?           | Checkbox row label; hidden when null                                                       | SecurityDialogDefaults.CheckboxText          | No       |
| initialChecked       | Boolean           | Initial checkbox state, re-applied each time the dialog is shown                           | false                                        | No       |
| onCheckedChange      | ((Boolean) -> Unit)? | Called on every checkbox toggle                                                         | null                                         | No       |
| confirmText          | String            | Confirm (positive) button label                                                            | SecurityDialogDefaults.ConfirmText           | No       |
| cancelText           | String            | Cancel (negative) button label                                                             | SecurityDialogDefaults.CancelText            | No       |
| titleColor           | Color             | Title text color                                                                           | DialogDefaults.titleColor()                  | No       |
| summaryColor         | Color             | Summary text color                                                                         | DialogDefaults.summaryColor()                | No       |
| backgroundColor      | Color             | Dialog background color                                                                    | DialogDefaults.backgroundColor()             | No       |
| colors               | SecurityDialogColors | Colors of the statement, link and checkbox texts                                        | SecurityDialogDefaults.securityDialogColors() | No      |
| enableWindowDim      | Boolean           | Whether to enable dimming layer                                                            | true                                         | No       |
| onDismissFinished    | (() -> Unit)?     | Invoked after the hide animation completes                                                 | null                                         | No       |
| renderInRootScaffold | Boolean           | Whether to render the dialog in the root (outermost) Scaffold (`OverlaySecurityDialog` only) | true                                       | No       |
| materialStroke       | Boolean           | Whether to draw the COUI 17 material stroke (edge lighting + inner shadow) a blurred dialog carries; needs runtime shaders. The cancel / confirm buttons turn material too | false | No       |

### SecurityDialogDefaults Object

#### Properties

| Property Name | Type   | Description                                                          |
| ------------- | ------ | -------------------------------------------------------------------- |
| CheckboxText  | String | Default checkbox label ("Don't ask me again")                        |
| ConfirmText   | String | Default confirm button label ("OK")                                  |
| CancelText    | String | Default cancel button label ("Cancel")                               |

#### Functions

| Function Name          | Return Type          | Description                                        |
| ---------------------- | -------------------- | -------------------------------------------------- |
| securityDialogColors() | SecurityDialogColors | Create the default statement / link / checkbox colors |

### SecurityDialogColors Properties

| Property Name     | Type  | Description                                              |
| ----------------- | ----- | -------------------------------------------------------- |
| statementColor    | Color | Statement paragraph color (secondary label color)        |
| linkColor         | Color | Inline link color (accent, 30% alpha while pressed)      |
| checkboxTextColor | Color | Checkbox label color (secondary label color)             |

## Advanced Usage

### Without Statement or Checkbox

```kotlin
OverlaySecurityDialog(
    show = showDialog,
    title = "Enable Feature",
    summary = "Do you want to enable this feature?",
    checkboxText = null, // Hide the checkbox row
    onConfirm = { _ -> showDialog = false },
    onCancel = { showDialog = false }
)
```

### Custom Labels and Initial State

```kotlin
OverlaySecurityDialog(
    show = showDialog,
    title = "Data Usage Reminder",
    statement = "See the User Agreement for details.",
    statementLinkText = "User Agreement",
    checkboxText = "Do not ask again",
    initialChecked = true,
    confirmText = "Agree",
    cancelText = "Disagree",
    onConfirm = { dontRemind -> showDialog = false },
    onCancel = { showDialog = false }
)
```
