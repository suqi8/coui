# EditTextPreference

`EditTextPreference` is a jump row that edits its text value in an input dialog, mirroring ColorOS's `COUIEditTextPreference` and `COUIEditTextPreferenceDialogFragment`. The row shows the current value as trailing text before the arrow. A click opens an `OverlayDialog` with a single-line underlined field (24dp side margins, quick delete on) that takes the focus at once, and a cancel / confirm button bar. The confirm button is disabled while the field is empty unless `supportEmptyInput` is set.

::: warning
The dialog is an `OverlayDialog`, so the component must be placed inside a `Scaffold`.
:::

<div style="position: relative; height: 180px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=editTextPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.preference.EditTextPreference
import io.github.suqi8.coui.kmp.preference.EditTextPreferenceDefaults
```

## Basic Usage

```kotlin
var nickname by remember { mutableStateOf("Suqi") }

Scaffold {
    EditTextPreference(
        value = nickname,
        onValueChange = { nickname = it },
        title = "Nickname",
        positiveButtonText = "OK",
        negativeButtonText = "Cancel",
        hint = "Enter a nickname"
    )
}
```

`onValueChange` is called only when the dialog is confirmed; cancelling or tapping outside keeps the old value.

## Component States

### Empty Input

```kotlin
EditTextPreference(
    value = signature,
    onValueChange = { signature = it },
    title = "Signature",
    positiveButtonText = "OK",
    negativeButtonText = "Cancel",
    dialogMessage = "Shown under your name.",
    supportEmptyInput = true
)
```

### Disabled State

```kotlin
EditTextPreference(
    value = "Locked",
    onValueChange = {},
    title = "Disabled",
    positiveButtonText = "OK",
    negativeButtonText = "Cancel",
    enabled = false
)
```

## Properties

### EditTextPreference Properties

| Property Name        | Type                        | Description                                           | Default Value                          | Required |
| -------------------- | --------------------------- | ----------------------------------------------------- | -------------------------------------- | -------- |
| value                | String                      | Current text value                                    | -                                      | Yes      |
| onValueChange        | (String) -> Unit            | Called with the edited text when confirmed            | -                                      | Yes      |
| title                | String                      | Row title                                             | -                                      | Yes      |
| positiveButtonText   | String                      | Confirm button label                                  | -                                      | Yes      |
| negativeButtonText   | String                      | Cancel button label                                   | -                                      | Yes      |
| modifier             | Modifier                    | Component modifier                                    | Modifier                               | No       |
| titleColor           | BasicComponentColors        | Title color                                           | BasicComponentDefaults.titleColor()    | No       |
| summary              | String?                     | Row summary                                           | null                                   | No       |
| summaryColor         | BasicComponentColors        | Summary color                                         | BasicComponentDefaults.summaryColor()  | No       |
| dialogTitle          | String?                     | Dialog title                                          | title                                  | No       |
| dialogMessage        | String?                     | Dialog message                                        | null                                   | No       |
| hint                 | String                      | Field hint                                            | ""                                     | No       |
| showValue            | Boolean                     | Show the value as trailing text                       | true                                   | No       |
| supportEmptyInput    | Boolean                     | Keep confirm enabled for an empty field               | false                                  | No       |
| textFieldColors      | TextFieldColors             | Field colors                                          | TextFieldDefaults.textFieldColors()    | No       |
| keyboardOptions      | KeyboardOptions             | Field keyboard options                                | KeyboardOptions.Default                | No       |
| startAction          | @Composable (() -> Unit)?   | Start side content                                    | null                                   | No       |
| bottomAction         | @Composable (() -> Unit)?   | Bottom content                                        | null                                   | No       |
| insideMargin         | PaddingValues               | Row inside margin                                     | BasicComponentDefaults.InsideMargin    | No       |
| cardListPosition     | CardListPosition            | Row position inside its card group                    | LocalCardListPosition.current          | No       |
| enabled              | Boolean                     | Whether the row is clickable                          | true                                   | No       |
| renderInRootScaffold | Boolean                     | Render the dialog in the root Scaffold                | true                                   | No       |
| onExpandedChange     | ((Boolean) -> Unit)?        | Called when the dialog is shown or dismissed          | null                                   | No       |

### EditTextPreferenceDefaults Object

| Constant              | Type | Default Value | Description                                              |
| --------------------- | ---- | ------------- | -------------------------------------------------------- |
| FieldHorizontalMargin | Dp   | 24.dp         | support_preference_dialog_edittext_margin_horizontal     |
| FieldBottomMargin     | Dp   | 14.dp         | coui_edittextdialog_marginbottom                         |
| FieldMinHeight        | Dp   | 34.dp         | support_preference_dialog_edittext_min_height            |
