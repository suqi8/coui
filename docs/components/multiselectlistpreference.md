# MultiSelectListPreference

`MultiSelectListPreference` is a preference row that opens a multi-choice dialog, mirroring ColorOS's COUIMultiSelectListPreference (COUIMultiSelectListPreferenceDialogFragment: a `COUIAlertDialog.BottomAssignment` dialog with `coui_select_dialog_multichoice` rows). The row is a jump row: it shows the selected entries, joined in entry order, as trailing assignment text followed by the COUI jump arrow (`coui_btn_next`). Dialog rows end with a checkbox; toggling them only stages the selection, which is committed when the confirm button is clicked. For the single-choice variant see [ListPreference](./listpreference.md).

<div style="position: relative; height: 200px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=multiSelectListPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

::: warning
The dialog is an `OverlayDialog`, so `MultiSelectListPreference` must be used inside a `Scaffold`.
:::

## Import

```kotlin
import io.github.suqi8.coui.kmp.preference.ListPreferenceEntry
import io.github.suqi8.coui.kmp.preference.MultiSelectListPreference
```

## Basic Usage

```kotlin
val entries = remember {
    listOf(
        ListPreferenceEntry("Photos"),
        ListPreferenceEntry("Documents"),
        ListPreferenceEntry("Contacts", summary = "Contacts and call logs"),
    )
}
var selectedIndices by remember { mutableStateOf(setOf(0, 1)) }

MultiSelectListPreference(
    entries = entries,
    selectedIndices = selectedIndices,
    onSelectedIndicesChange = { selectedIndices = it },
    title = "Sync items",
    confirmButtonText = "Confirm",
    cancelButtonText = "Cancel",
)
```

## Component States

### Disabled State

```kotlin
MultiSelectListPreference(
    entries = entries,
    selectedIndices = setOf(0),
    onSelectedIndicesChange = {},
    title = "Disabled",
    confirmButtonText = "Confirm",
    cancelButtonText = "Cancel",
    enabled = false,
)
```

A row with an empty `entries` list is disabled as well. Single entries can be disabled with `ListPreferenceEntry(enabled = false)`: they are shown with disabled text colors and cannot be toggled.

## Properties

### MultiSelectListPreference Properties

| Property                | Type                       | Description                                          | Default Value                                 | Required |
| ----------------------- | -------------------------- | ---------------------------------------------------- | --------------------------------------------- | -------- |
| entries                 | List\<ListPreferenceEntry> | Entries to choose from                               | -                                             | Yes      |
| selectedIndices         | Set\<Int>                  | Indices of the selected entries                      | -                                             | Yes      |
| onSelectedIndicesChange | (Set\<Int>) -> Unit        | Callback with the new selection on confirm           | -                                             | Yes      |
| title                   | String                     | Title of the preference row                          | -                                             | Yes      |
| confirmButtonText       | String                     | Label of the dialog confirm button                   | -                                             | Yes      |
| cancelButtonText        | String                     | Label of the dialog cancel button                    | -                                             | Yes      |
| modifier                | Modifier                   | Modifier applied to the row                          | Modifier                                      | No       |
| titleColor              | BasicComponentColors       | Title color configuration                            | BasicComponentDefaults.titleColor()           | No       |
| summary                 | String?                    | Summary of the preference row                        | null                                          | No       |
| summaryColor            | BasicComponentColors       | Summary color configuration                          | BasicComponentDefaults.summaryColor()         | No       |
| dialogTitle             | String                     | Title of the selection dialog                        | title                                         | No       |
| colors                  | ListPreferenceColors       | Colors of the dialog rows                            | ListPreferenceDefaults.listPreferenceColors() | No       |
| checkboxColors          | CheckboxColors             | Colors of the dialog row checkboxes                  | CheckboxDefaults.checkboxColors()             | No       |
| startAction             | @Composable (() -> Unit)?  | Custom start side content                            | null                                          | No       |
| bottomAction            | @Composable (() -> Unit)?  | Custom bottom content                                | null                                          | No       |
| insideMargin            | PaddingValues              | Internal content padding                             | BasicComponentDefaults.InsideMargin           | No       |
| cardListPosition | CardListPosition | Row position inside its card group ([CardListGroup](./card.md#cardlistgroup)); rounded outer edges gain extra padding | LocalCardListPosition.current (None outside a CardListGroup) | No |
| enabled                 | Boolean                    | Whether the row is clickable                         | true                                          | No       |
| showValue               | Boolean                    | Show the selected entries as trailing text           | true                                          | No       |
| renderInRootScaffold    | Boolean                    | Render the dialog in the root Scaffold               | true                                          | No       |
| onExpandedChange        | ((Boolean) -> Unit)?       | Callback when the dialog is shown / dismissed        | null                                          | No       |

`ListPreferenceEntry`, `ListPreferenceDefaults` and `ListPreferenceColors` are shared with `ListPreference`; see [ListPreference](./listpreference.md#properties).

## Behavior

- Clicking the row opens an `OverlayDialog` titled `dialogTitle` (the COUI 17 bottom dialog, centered on large screens) with a cancel and a confirm button. The row does not stay pressed while the dialog shows, since the COUI dialog locks no anchor state, and like the COUI rows it plays no haptic.
- Each time the dialog opens, the staged selection restarts from `selectedIndices`. Toggling a row only updates the staged selection.
- The confirm button commits the staged selection via `onSelectedIndicesChange` and dismisses the dialog; the cancel button or an outside tap dismisses it without changes.
- The trailing text lists the selected entries in entry order, separated by `", "`; indices outside `entries` are ignored.
