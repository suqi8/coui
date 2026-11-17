# UserStatementDialog

`UserStatementDialog` is the bottom-panel user statement shown on first launch, mirroring ColorOS's `COUIUserStatementDialog` (`coui_component_full_page_statement_with_protocol.xml`): a panel laid out at the maximum height with no drag handle, showing the app logo, a title, the app name and either a message, an icon list or custom content, then the statement text (with the agreement text appended) in a short scroller, and a confirm button above an exit button.

The panel cannot be dragged and taps outside it are ignored (`setCanceledOnTouchOutside(false)`); back dismisses it only when `onDismissRequest` is set. On windows 600dp wide and wider it floats centered like every COUI panel.

Two variants are provided:

- `OverlayUserStatementDialog` — rendered inside `Scaffold`'s `COUIPopupHost` (must be used within `Scaffold`).
- `WindowUserStatementDialog` — rendered at window level, no `Scaffold` required.

<div style="position: relative; height: 320px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=userStatementDialog" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

::: danger Prerequisite
`OverlayUserStatementDialog` depends on `Scaffold` providing `COUIPopupHost` to render popup content. Use `WindowUserStatementDialog` when no `Scaffold` is available.
:::

## Import

```kotlin
import io.github.suqi8.coui.kmp.overlay.OverlayUserStatementDialog
// or
import io.github.suqi8.coui.kmp.window.WindowUserStatementDialog

import io.github.suqi8.coui.kmp.layout.UserStatementListItem
import io.github.suqi8.coui.kmp.layout.UserStatementDialogDefaults
```

## Basic Usage

```kotlin
var showStatement by remember { mutableStateOf(true) }
val linkStyles = FullPageStatementDefaults.linkStyles()
val statement = buildAnnotatedString {
    append("Continuing means you agree to the ")
    withLink(LinkAnnotation.Clickable("agreement", linkStyles) { openAgreement() }) {
        append("User Agreement")
    }
    append(".")
}

Scaffold {
    OverlayUserStatementDialog(
        show = showStatement,
        onConfirm = { showStatement = false },
        onExit = { finishApp() },
        logo = painterResource(Res.drawable.app_icon),
        title = "Welcome to",
        appName = "Notes",
        appMessage = "Write, organize and sync your notes everywhere.",
        statement = statement,
    )
}
```

## Properties

| Property             | Type                          | Description                                                                 | Default Value                          | Required |
| -------------------- | ----------------------------- | --------------------------------------------------------------------------- | -------------------------------------- | -------- |
| show                 | Boolean                       | Whether the panel is shown                                                  | -                                      | Yes      |
| onConfirm            | () -> Unit                    | Confirm button callback                                                     | -                                      | Yes      |
| onExit               | () -> Unit                    | Exit button callback                                                        | -                                      | Yes      |
| modifier             | Modifier                      | Modifier applied to the panel                                               | Modifier                               | No       |
| backgroundModifier   | Modifier                      | Applied inside the panel's corner clip, under the content (blurred material) | Modifier                              | No       |
| logo                 | Painter?                      | 80dp app logo (20dp corners); hidden with `customContent`                   | null                                   | No       |
| title                | String?                       | Line above the app name (16sp medium, single line)                          | null                                   | No       |
| appName              | String?                       | App name (28sp medium)                                                      | null                                   | No       |
| appMessage           | String?                       | Centered message (13sp), shown without `listItems` and `customContent`      | null                                   | No       |
| listItems            | List&lt;UserStatementListItem&gt; | Icon list below the app name                                            | emptyList()                            | No       |
| onListItemClick      | ((Int) -> Unit)?              | Called with the index of a tapped list row                                  | null                                   | No       |
| statement            | AnnotatedString?              | Statement text (10sp, line height 1.2em); may carry links                  | null                                   | No       |
| protocolText         | AnnotatedString?              | Agreement text appended to `statement`                                      | null                                   | No       |
| confirmText          | String                        | Confirm button label                                                        | UserStatementDialogDefaults.ConfirmText | No      |
| exitText             | String                        | Exit button label                                                           | UserStatementDialogDefaults.ExitText   | No       |
| backgroundColor      | Color                         | Panel background color                                                      | BottomSheetDefaults.backgroundColor()  | No       |
| enableWindowDim      | Boolean                       | Whether to dim the window behind the panel                                  | true                                   | No       |
| onDismissRequest     | (() -> Unit)?                 | Back gesture callback; back is ignored when null                            | null                                   | No       |
| onDismissFinished    | (() -> Unit)?                 | Called when the panel is completely dismissed                               | null                                   | No       |
| renderInRootScaffold | Boolean                       | (Overlay only) render in the root Scaffold                                  | true                                   | No       |
| materialStroke       | Boolean                       | Draw the COUI 17 material stroke of a blurred panel                         | false                                  | No       |
| customContent        | (@Composable () -> Unit)?     | Content replacing the logo, message and list (`setCustomView`)              | null                                   | No       |

### UserStatementListItem

| Property | Type     | Description                     |
| -------- | -------- | ------------------------------- |
| title    | String   | Row title (16sp medium)         |
| summary  | String?  | Text below the title (14sp)     |
| icon     | Painter? | 36dp icon at the start of the row |

### UserStatementDialogDefaults

| Constant    | Value                | COUI source         |
| ----------- | -------------------- | ------------------- |
| ConfirmText | "Agree and continue" | `coui_allow_text`   |
| ExitText    | "Exit"               | `coui_reject_text`  |

## Layout

- Content padding 24dp start / 24dp end (`coui_component_statement_panel_start_padding`, `_end_padding` + `_content_item_end_padding`).
- The logo block starts 40dp below the panel top; with only a message it starts 110dp down on compact windows and 80dp on wider ones.
- The statement scroller sits 16dp below the header and 12dp above the buttons, at most 80dp tall (150dp on compact windows with only a message), with 32dp fading edges.
- Both buttons span the width less 32dp on each side, 16dp apart; the exit button keeps 24dp from the panel bottom.
