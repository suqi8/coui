# ListPreference

`ListPreference` is a preference row that opens a single-choice dialog, mirroring ColorOS's COUIListPreference (COUIListPreferenceDialogFragment: a `COUIAlertDialog.BottomAssignment` dialog with `coui_select_dialog_singlechoice` rows). The row is a jump row: it shows the selected entry as trailing assignment text followed by the COUI jump arrow (`coui_btn_next`). Dialog rows end with a radio button. Tapping an entry commits the selection and dismisses the dialog.

`MultiSelectListPreference` is the multi-choice variant, mirroring COUIMultiSelectListPreference: dialog rows end with a checkbox, and the selection is only committed when the confirm button is clicked.

The dialog is an `OverlayDialog`, so both preferences need a `Scaffold`.

<div style="position: relative; height: 360px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=listPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.preference.ListPreference
import io.github.suqi8.coui.kmp.preference.ListPreferenceDefaults
import io.github.suqi8.coui.kmp.preference.ListPreferenceEntry
import io.github.suqi8.coui.kmp.preference.MultiSelectListPreference
```

## Basic Usage

```kotlin
val entries = remember {
    listOf(
        ListPreferenceEntry("Light"),
        ListPreferenceEntry("Dark"),
        ListPreferenceEntry("Follow system", summary = "Switch with the system dark mode"),
    )
}
var selectedIndex by remember { mutableIntStateOf(2) }

ListPreference(
    entries = entries,
    selectedIndex = selectedIndex,
    onSelectedIndexChange = { selectedIndex = it },
    title = "Theme",
    cancelButtonText = "Cancel",
)
```

## Multi-choice Variant

```kotlin
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

## Properties

### ListPreferenceEntry

| Property | Type    | Description                          | Default Value | Required |
| -------- | ------- | ------------------------------------ | ------------- | -------- |
| text     | String  | Entry text (16sp, medium)            | -             | Yes      |
| summary  | String? | Optional summary below the text      | null          | No       |
| enabled  | Boolean | Whether the entry can be selected    | true          | No       |

### ListPreference

| Property              | Type                      | Description                                        | Default Value                                | Required |
| --------------------- | ------------------------- | -------------------------------------------------- | -------------------------------------------- | -------- |
| entries               | List\<ListPreferenceEntry> | Entries to choose from                             | -                                            | Yes      |
| selectedIndex         | Int                       | Index of the selected entry (-1 = none)            | -                                            | Yes      |
| onSelectedIndexChange | (Int) -> Unit             | Callback when an entry is selected                 | -                                            | Yes      |
| title                 | String                    | Title of the preference row                        | -                                            | Yes      |
| cancelButtonText      | String                    | Label of the dialog cancel button                  | -                                            | Yes      |
| modifier              | Modifier                  | Modifier applied to the row                        | Modifier                                     | No       |
| titleColor            | BasicComponentColors      | Title color configuration                          | BasicComponentDefaults.titleColor()          | No       |
| summary               | String?                   | Summary of the preference row                      | null                                         | No       |
| summaryColor          | BasicComponentColors      | Summary color configuration                        | BasicComponentDefaults.summaryColor()        | No       |
| dialogTitle           | String                    | Title of the selection dialog                      | title                                        | No       |
| colors                | ListPreferenceColors      | Colors of the dialog rows                          | ListPreferenceDefaults.listPreferenceColors() | No      |
| startAction           | @Composable (() -> Unit)? | Custom start side content                          | null                                         | No       |
| bottomAction          | @Composable (() -> Unit)? | Custom bottom content                              | null                                         | No       |
| insideMargin          | PaddingValues             | Internal content padding                           | BasicComponentDefaults.InsideMargin          | No       |
| cardListPosition | CardListPosition | Row position inside its card group ([CardListGroup](./card.md#cardlistgroup)); rounded outer edges gain extra padding | LocalCardListPosition.current (None outside a CardListGroup) | No |
| enabled               | Boolean                   | Whether the row is clickable                       | true                                         | No       |
| showValue             | Boolean                   | Show the selected entry as trailing text           | true                                         | No       |
| renderInRootScaffold  | Boolean                   | Render the dialog in the root Scaffold             | true                                         | No       |
| onExpandedChange      | ((Boolean) -> Unit)?      | Callback when the dialog is shown / dismissed      | null                                         | No       |

### MultiSelectListPreference

Additional / differing properties compared to `ListPreference`:

| Property                | Type                 | Description                                          | Default Value                     | Required |
| ----------------------- | -------------------- | ---------------------------------------------------- | --------------------------------- | -------- |
| selectedIndices         | Set\<Int>            | Indices of the selected entries                      | -                                 | Yes      |
| onSelectedIndicesChange | (Set\<Int>) -> Unit  | Callback with the new selection on confirm           | -                                 | Yes      |
| confirmButtonText       | String               | Label of the dialog confirm button                   | -                                 | Yes      |
| checkboxColors          | CheckboxColors       | Colors of the dialog row checkboxes                  | CheckboxDefaults.checkboxColors() | No       |

### ListPreferenceDefaults

| Constant                 | Type | Default Value | COUI source                                    |
| ------------------------ | ---- | ------------- | ---------------------------------------------- |
| PanelInsideMargin        | DpSize | DpSize(24.dp, 0.dp) | alert_dialog_single_list_padding_left / _right (horizontal padding of a dialog row) |
| PanelItemMinHeight       | Dp   | 48.dp         | coui_delete_alert_dialog_button_height         |
| PanelItemVerticalPadding | Dp   | 10.dp         | alert_dialog_single_list_padding_vertical      |
| PanelItemIndicatorSpacing | Dp  | 16.dp         | coui_dialog_layout_margin_horizontal           |
| PanelItemSummarySpacing  | Dp   | 2.dp          | coui_alert_dialog_content_panel_padding_top    |
| CheckIconSize            | Dp   | 24.dp         | COUI 24dp selection widgets                    |
| ButtonBarTopPadding      | Dp   | 6.dp          | alert_dialog_single_list_last_item_padding_bottom (not read by the dialog, whose button bar keeps the COUI 17 spacing) |
| ButtonBarBottomPadding   | Dp   | 12.dp         | not read by the dialog, whose button bar keeps the COUI 17 spacing |

### `listPreferenceColors()` factory

| Parameter                      | Type  | Default                                       | COUI role               |
| ------------------------------ | ----- | --------------------------------------------- | ----------------------- |
| itemTextColor                  | Color | COUITheme.colorScheme.labelPrimary              | couiColorPrimaryNeutral |
| disabledItemTextColor          | Color | COUITheme.colorScheme.disabledNeutral | couiColorDisabledNeutral |
| itemSummaryColor               | Color | COUITheme.colorScheme.labelSecondary     | couiColorSecondNeutral  |
| disabledItemSummaryColor       | Color | COUITheme.colorScheme.disabledNeutral | couiColorDisabledNeutral |
| selectedIndicatorColor         | Color | COUITheme.colorScheme.primary                | couiColorPrimary (checked radio button) |
| disabledSelectedIndicatorColor | Color | COUITheme.colorScheme.disable        | checked radio button of a disabled row |

## Behavior

- Clicking the row opens an `OverlayDialog` titled `dialogTitle` (the COUI 17 bottom dialog, centered on large screens). The row does not stay pressed while the dialog shows, since the COUI dialog locks no anchor state, and like the COUI rows it plays no haptic.
- Each dialog row spans the dialog width and is the touch target: label (16sp medium) and optional summary (14sp) on the start side, the radio button / checkbox at the end, 24dp side padding, a 48dp minimum height.
- Single choice: tapping an entry invokes `onSelectedIndexChange` and dismisses the dialog immediately, without moving the radio button first; the cancel button or an outside tap dismisses without changes (COUIListPreferenceDialogFragment).
- Multi choice: toggling rows only updates a pending selection; the confirm button commits it via `onSelectedIndicesChange`, while cancel / outside tap discards it (COUIMultiSelectListPreferenceDialogFragment).
- A hairline divider (0.33dp, couiColorDivider) inset by the row padding is drawn between adjacent dialog rows only, never after the last row.
- Entries with `enabled = false` are shown but cannot be selected, using disabled text colors.
