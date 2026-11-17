# ListDialog

`ListDialog` covers the three list forms of ColorOS's alert dialog (`COUIAlertDialogBuilder`):

- **List dialog** (`setItems`, `COUIAlertDialog.List.Bottom`, `SummaryAdapter`): every item is a full-width capsule button with an optional 12sp summary below it.
- **Single-choice dialog** (`setSingleChoiceItems`, `ChoiceListAdapter`): 48dp rows with a label, an optional summary and icon, and an end radio button.
- **Multi-choice dialog** (`setMultiChoiceItems`, `ChoiceListAdapter`): the same rows ending in a checkbox, with an optional max checked count.

Each form follows the regular dialog layout: title, an optional message (capped at 60dp once a list follows), the scrolling list, and an optional `DialogButtonBar`.

Each form comes in two variants:

- `OverlayListDialog` / `OverlaySingleChoiceDialog` / `OverlayMultiChoiceDialog` — rendered inside `Scaffold`'s `COUIPopupHost` (must be used within `Scaffold`).
- `WindowListDialog` / `WindowSingleChoiceDialog` / `WindowMultiChoiceDialog` — rendered at window level, no `Scaffold` required.

::: danger Prerequisite
The overlay variants depend on `Scaffold` providing `COUIPopupHost`. Use the window variants when no `Scaffold` is available.
:::

<div style="position: relative; height: 500px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=listDialog" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.overlay.OverlayListDialog
import io.github.suqi8.coui.kmp.overlay.OverlaySingleChoiceDialog
import io.github.suqi8.coui.kmp.overlay.OverlayMultiChoiceDialog
// or
import io.github.suqi8.coui.kmp.window.WindowListDialog
import io.github.suqi8.coui.kmp.window.WindowSingleChoiceDialog
import io.github.suqi8.coui.kmp.window.WindowMultiChoiceDialog

import io.github.suqi8.coui.kmp.layout.ListDialogItem
import io.github.suqi8.coui.kmp.layout.ListDialogDefaults
import io.github.suqi8.coui.kmp.layout.ListDialogColors
```

## Basic Usage

### List Dialog

```kotlin
var showDialog by remember { mutableStateOf(false) }

OverlayListDialog(
    show = showDialog,
    title = "Share to",
    items = listOf(
        ListDialogItem(text = "Copy link"),
        ListDialogItem(text = "Save image", summary = "Saved to the gallery"),
        ListDialogItem(text = "Delete", textColor = COUITheme.colorScheme.error),
    ),
    onItemClick = { index -> showDialog = false },
    negative = DialogButtonBarAction(text = "Cancel", onClick = { showDialog = false }),
    onDismissRequest = { showDialog = false },
)
```

### Single-Choice Dialog

```kotlin
var selected by remember { mutableIntStateOf(0) }

OverlaySingleChoiceDialog(
    show = showDialog,
    title = "Screen timeout",
    items = listOf(ListDialogItem("30 seconds"), ListDialogItem("1 minute"), ListDialogItem("Never")),
    selectedIndex = selected,
    onSelectedIndexChange = { selected = it },
    negative = DialogButtonBarAction(text = "Cancel", onClick = { showDialog = false }),
    positive = DialogButtonBarAction(text = "OK", onClick = { showDialog = false }),
    onDismissRequest = { showDialog = false },
)
```

### Multi-Choice Dialog

```kotlin
var checked by remember { mutableStateOf(listOf(true, false, false)) }

OverlayMultiChoiceDialog(
    show = showDialog,
    title = "Sync items",
    items = listOf(ListDialogItem("Contacts"), ListDialogItem("Photos"), ListDialogItem("Notes", enabled = false)),
    checked = checked,
    onCheckedChange = { index, value -> checked = checked.toMutableList().also { it[index] = value } },
    maxCheckedCount = 2,
    onMaxCheckedReached = { /* show a hint */ },
    positive = DialogButtonBarAction(text = "OK", onClick = { showDialog = false }),
    onDismissRequest = { showDialog = false },
)
```

None of the three hides by itself on a click; set `show` to false to close it.

## Properties

### ListDialogItem

| Property Name | Type         | Description                                                        | Default Value     |
| ------------- | ------------ | ------------------------------------------------------------------ | ----------------- |
| text          | String       | Item label                                                         | -                 |
| summary       | String?      | Optional secondary line                                            | null              |
| textColor     | Color        | Label color of a list dialog item (`setItems` text colors); ignored by the choice dialogs | Color.Unspecified |
| icon          | ImageVector? | Start icon of a choice row; ignored by the list dialog             | null              |
| enabled       | Boolean      | Whether the item can be clicked                                    | true              |

### OverlayListDialog Properties

| Property Name        | Type                   | Description                                                   | Default Value                        | Required |
| -------------------- | ---------------------- | ------------------------------------------------------------- | ------------------------------------ | -------- |
| show                 | Boolean                | Whether to show the dialog                                    | -                                    | Yes      |
| items                | List\<ListDialogItem\> | The items                                                     | -                                    | Yes      |
| onItemClick          | (Int) -> Unit          | Called with the index of the clicked item                     | -                                    | Yes      |
| modifier             | Modifier               | Modifier applied to the dialog                                | Modifier                             | No       |
| backgroundModifier   | Modifier               | Applied inside the panel's corner clip, under the content     | Modifier                             | No       |
| title                | String?                | Dialog title                                                  | null                                 | No       |
| titleColor           | Color                  | Title color                                                   | DialogDefaults.titleColor()          | No       |
| summary              | String?                | Dialog message, capped at 60dp                                | null                                 | No       |
| summaryColor         | Color                  | Message color                                                 | DialogDefaults.summaryColor()        | No       |
| negative             | DialogButtonBarAction? | Negative button of the bar below the list                     | null                                 | No       |
| positive             | DialogButtonBarAction? | Positive button of the bar below the list                     | null                                 | No       |
| neutral              | DialogButtonBarAction? | Neutral button of the bar below the list                      | null                                 | No       |
| colors               | ListDialogColors       | Item colors                                                   | ListDialogDefaults.listDialogColors() | No      |
| backgroundColor      | Color                  | Panel background color                                        | DialogDefaults.backgroundColor()     | No       |
| enableWindowDim      | Boolean                | Whether to enable the dimming layer                           | true                                 | No       |
| onDismissRequest     | (() -> Unit)?          | Called when the user taps outside or presses back             | null                                 | No       |
| onDismissFinished    | (() -> Unit)?          | Called after the hide animation completes                     | null                                 | No       |
| renderInRootScaffold | Boolean                | Whether to render the dialog in the root Scaffold             | true                                 | No       |
| materialStroke       | Boolean                | Whether to draw the COUI 17 material stroke of a blurred panel; the buttons turn material too | false | No |

### OverlaySingleChoiceDialog Properties

Same as `OverlayListDialog`, with `selectedIndex: Int` (the selected row, -1 for none) and `onSelectedIndexChange: (Int) -> Unit` in place of `onItemClick`.

### OverlayMultiChoiceDialog Properties

Same as `OverlayListDialog`, with these in place of `onItemClick`:

| Property Name       | Type                   | Description                                                      | Default Value | Required |
| ------------------- | ---------------------- | ---------------------------------------------------------------- | ------------- | -------- |
| checked             | List\<Boolean\>        | Whether each row is checked                                      | -             | Yes      |
| onCheckedChange     | (Int, Boolean) -> Unit | Called with the index and the new state of a toggled row         | -             | Yes      |
| maxCheckedCount     | Int                    | Max number of checked rows, 0 for no limit                       | 0             | No       |
| onMaxCheckedReached | (() -> Unit)?          | Called instead of `onCheckedChange` when a row past the limit is clicked | null  | No       |

The window variants take the same properties without `renderInRootScaffold`.

### ListDialogDefaults Object

| Property Name               | Type | Description                                                      |
| --------------------------- | ---- | ---------------------------------------------------------------- |
| ListPanelMarginTop          | Dp   | Top margin of the list dialog list (16.dp, `coui_list_dialog_list_panel_margin_top`) |
| SummaryMaxHeight            | Dp   | Max message height above a list (60.dp)                          |
| ItemSummaryFontSize         | TextUnit | Summary text size below a list item (12.sp)                  |
| ItemSummaryLineHeight       | TextUnit | Summary line height below a list item (1.35.em)              |
| ChoiceItemMinHeight         | Dp   | Min height of a choice row (48.dp)                               |
| ChoiceItemPaddingHorizontal | Dp   | Horizontal padding of a choice row (24.dp)                       |
| ChoiceItemPaddingVertical   | Dp   | Vertical margin of the row text and control (10.dp)              |
| ChoiceItemTextMarginEnd     | Dp   | Gap between the text and the control (16.dp)                     |
| ChoiceItemIconMarginEnd     | Dp   | Gap between the icon and the text (16.dp)                        |
| ChoiceItemIconPaddingVertical | Dp | Vertical margin of the icon (20.dp)                              |
| ChoiceItemSummarySpacing    | Dp   | Gap between a row label and its summary (2.dp)                   |
| ChoiceListPaddingBottom     | Dp   | Bottom padding of a choice list (16.dp)                          |

| Function Name      | Return Type      | Description          |
| ------------------ | ---------------- | -------------------- |
| listDialogColors() | ListDialogColors | Default item colors  |

### ListDialogColors Class

| Property Name        | Type  | Description                                         |
| -------------------- | ----- | --------------------------------------------------- |
| textColor            | Color | Choice row label (`?couiColorPrimaryNeutral`)       |
| disabledTextColor    | Color | Disabled choice row label (`?couiColorDisabledNeutral`) |
| summaryColor         | Color | Item summary (`?couiColorSecondNeutral`)            |
| disabledSummaryColor | Color | Disabled item summary                               |
| dividerColor         | Color | Divider between choice rows (`?couiColorDivider`)   |
