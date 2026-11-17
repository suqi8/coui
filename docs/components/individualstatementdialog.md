# IndividualStatementDialog

`IndividualStatementDialog` is the bottom-panel function privacy statement, mirroring ColorOS's `COUIIndividualStatementDialog` (`coui_component_full_page_function_privacy.xml`): a panel with no drag handle holding a centered title, the statement text and optional opt-in rows in a scroller of at most 384dp, a confirm button and an exit text button.

- Each opt-in row (`COUICheckBoxItemView`) shows a title, a summary and a checkbox; tapping anywhere on the row toggles it. Rows are separated by dividers inset 24dp / 20dp.
- With opt-in rows the confirm button stays disabled until at least one row is checked, and the statement text shrinks from 14sp to 12sp. `onConfirm` receives the checked items. Checks are cleared each time the panel is shown.
- The confirm button is 220dp wide on small portrait windows (smallest width < 480dp) and 280dp otherwise; small landscape windows show exit and confirm side by side.
- The panel cannot be dragged and taps outside it are ignored; back dismisses it only when `onDismissRequest` is set.

Two variants are provided: `OverlayIndividualStatementDialog` (inside `Scaffold`) and `WindowIndividualStatementDialog` (window level).

<div style="position: relative; height: 320px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=individualStatementDialog" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

::: danger Prerequisite
`OverlayIndividualStatementDialog` depends on `Scaffold` providing `COUIPopupHost` to render popup content. Use `WindowIndividualStatementDialog` when no `Scaffold` is available.
:::

## Import

```kotlin
import io.github.suqi8.coui.kmp.overlay.OverlayIndividualStatementDialog
// or
import io.github.suqi8.coui.kmp.window.WindowIndividualStatementDialog

import io.github.suqi8.coui.kmp.layout.StatementPrivacyItem
import io.github.suqi8.coui.kmp.layout.IndividualStatementDialogDefaults
```

## Basic Usage

```kotlin
var showDialog by remember { mutableStateOf(false) }

Scaffold {
    OverlayIndividualStatementDialog(
        show = showDialog,
        onConfirm = { checked ->
            enable(checked)
            showDialog = false
        },
        onExit = { showDialog = false },
        title = "Optional features",
        statement = AnnotatedString("Choose the optional features to turn on."),
        privacyItems = listOf(
            StatementPrivacyItem("Personalized suggestions", "Use your usage data to tailor suggestions"),
            StatementPrivacyItem("Crash reports", "Send anonymous crash logs"),
        ),
        onDismissRequest = { showDialog = false },
    )
}
```

## Properties

| Property             | Type                              | Description                                              | Default Value                                 | Required |
| -------------------- | --------------------------------- | -------------------------------------------------------- | --------------------------------------------- | -------- |
| show                 | Boolean                           | Whether the panel is shown                               | -                                             | Yes      |
| onConfirm            | (List&lt;StatementPrivacyItem&gt;) -> Unit | Confirm callback with the checked items          | -                                             | Yes      |
| onExit               | () -> Unit                        | Exit button callback                                     | -                                             | Yes      |
| modifier             | Modifier                          | Modifier applied to the panel                            | Modifier                                      | No       |
| backgroundModifier   | Modifier                          | Applied inside the panel's corner clip, under the content | Modifier                                     | No       |
| title                | String?                           | Centered title (18sp medium)                             | null                                          | No       |
| statement            | AnnotatedString?                  | Statement text (14sp, 12sp with opt-in rows); may carry links | null                                     | No       |
| privacyItems         | List&lt;StatementPrivacyItem&gt;  | Opt-in rows                                              | emptyList()                                   | No       |
| confirmText          | String                            | Confirm button label                                     | IndividualStatementDialogDefaults.ConfirmText | No       |
| exitText             | String                            | Exit button label                                        | IndividualStatementDialogDefaults.ExitText    | No       |
| backgroundColor      | Color                             | Panel background color                                   | BottomSheetDefaults.backgroundColor()         | No       |
| enableWindowDim      | Boolean                           | Whether to dim the window behind the panel               | true                                          | No       |
| onDismissRequest     | (() -> Unit)?                     | Back gesture callback; back is ignored when null         | null                                          | No       |
| onDismissFinished    | (() -> Unit)?                     | Called when the panel is completely dismissed            | null                                          | No       |
| renderInRootScaffold | Boolean                           | (Overlay only) render in the root Scaffold               | true                                          | No       |
| materialStroke       | Boolean                           | Draw the COUI 17 material stroke of a blurred panel      | false                                         | No       |

### StatementPrivacyItem

| Property | Type    | Description          |
| -------- | ------- | -------------------- |
| title    | String  | Row title            |
| summary  | String? | Text below the title |

### IndividualStatementDialogDefaults

| Constant    | Value                | COUI source         |
| ----------- | -------------------- | ------------------- |
| ConfirmText | "Agree and continue" | `coui_allow_text`   |
| ExitText    | "Exit"               | `coui_reject_text`  |
