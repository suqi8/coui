# ListPreference

`ListPreference` is a preference row that opens a bottom sheet single-choice panel, mirroring ColorOS's COUIListPreference (COUIAlertDialog_BottomAssignment with coui_select_dialog_singlechoice rows). The row shows the selected entry as trailing assignment text plus the COUI popup indicator; the selected panel row ends with a primary-tinted check mark. Tapping an entry commits the selection and dismisses the panel.

`MultiSelectListPreference` is the multi-choice variant, mirroring COUIMultiSelectListPreference: panel rows end with a checkbox, and the selection is only committed when the confirm button is clicked.

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
| cancelButtonText      | String                    | Label of the panel cancel button                   | -                                            | Yes      |
| modifier              | Modifier                  | Modifier applied to the row                        | Modifier                                     | No       |
| titleColor            | BasicComponentColors      | Title color configuration                          | BasicComponentDefaults.titleColor()          | No       |
| summary               | String?                   | Summary of the preference row                      | null                                         | No       |
| summaryColor          | BasicComponentColors      | Summary color configuration                        | BasicComponentDefaults.summaryColor()        | No       |
| dialogTitle           | String                    | Title of the selection panel                       | title                                        | No       |
| colors                | ListPreferenceColors      | Colors of the panel rows                           | ListPreferenceDefaults.listPreferenceColors() | No      |
| startAction           | @Composable (() -> Unit)? | Custom start side content                          | null                                         | No       |
| bottomAction          | @Composable (() -> Unit)? | Custom bottom content                              | null                                         | No       |
| insideMargin          | PaddingValues             | Internal content padding                           | BasicComponentDefaults.InsideMargin          | No       |
| enabled               | Boolean                   | Whether the row is clickable                       | true                                         | No       |
| showValue             | Boolean                   | Show the selected entry as trailing text           | true                                         | No       |
| renderInRootScaffold  | Boolean                   | Render the panel in the root Scaffold              | true                                         | No       |
| onExpandedChange      | ((Boolean) -> Unit)?      | Callback when the panel is shown / dismissed       | null                                         | No       |

### MultiSelectListPreference

Additional / differing properties compared to `ListPreference`:

| Property                | Type                 | Description                                          | Default Value                     | Required |
| ----------------------- | -------------------- | ---------------------------------------------------- | --------------------------------- | -------- |
| selectedIndices         | Set\<Int>            | Indices of the selected entries                      | -                                 | Yes      |
| onSelectedIndicesChange | (Set\<Int>) -> Unit  | Callback with the new selection on confirm           | -                                 | Yes      |
| confirmButtonText       | String               | Label of the panel confirm button                    | -                                 | Yes      |
| checkboxColors          | CheckboxColors       | Colors of the panel row checkboxes                   | CheckboxDefaults.checkboxColors() | No       |

### ListPreferenceDefaults

| Constant                 | Type | Default Value | COUI source                                    |
| ------------------------ | ---- | ------------- | ---------------------------------------------- |
| PanelItemMinHeight       | Dp   | 48.dp         | coui_delete_alert_dialog_button_height         |
| PanelItemVerticalPadding | Dp   | 10.dp         | alert_dialog_single_list_padding_vertical      |
| PanelItemIndicatorSpacing | Dp  | 16.dp         | coui_dialog_layout_margin_horizontal           |
| PanelItemSummarySpacing  | Dp   | 2.dp          | coui_alert_dialog_content_panel_padding_top    |
| CheckIconSize            | Dp   | 24.dp         | COUI 24dp selection widgets                    |
| ButtonBarTopPadding      | Dp   | 6.dp          | alert_dialog_single_list_last_item_padding_bottom |
| ButtonBarBottomPadding   | Dp   | 12.dp         | library convention                             |

### `listPreferenceColors()` factory

| Parameter                      | Type  | Default                                       | COUI role               |
| ------------------------------ | ----- | --------------------------------------------- | ----------------------- |
| itemTextColor                  | Color | COUITheme.colorScheme.onSurface              | couiColorPrimaryNeutral |
| disabledItemTextColor          | Color | COUITheme.colorScheme.disabledOnSecondaryVariant | couiColorDisabledNeutral |
| itemSummaryColor               | Color | COUITheme.colorScheme.onSurfaceSecondary     | couiColorSecondNeutral  |
| disabledItemSummaryColor       | Color | COUITheme.colorScheme.disabledOnSecondaryVariant | couiColorDisabledNeutral |
| selectedIndicatorColor         | Color | COUITheme.colorScheme.primary                | couiColorPrimary        |
| disabledSelectedIndicatorColor | Color | COUITheme.colorScheme.disabledPrimary        | -                       |

## Behavior

- Clicking the row opens an `OverlayBottomSheet` panel titled `dialogTitle` and keeps the row in the hold-down state until the panel is dismissed, with a context-click haptic on open.
- Single choice: tapping an entry invokes `onSelectedIndexChange` and dismisses the panel immediately; the cancel button or an outside tap dismisses without changes (COUIListPreferenceDialogFragment).
- Multi choice: toggling rows only updates a pending selection; the confirm button commits it via `onSelectedIndicesChange`, while cancel / outside tap discards it (COUIMultiSelectListPreferenceDialogFragment).
- A hairline divider (0.33dp, couiColorDivider) is drawn between adjacent panel rows only, never after the last row.
- Entries with `enabled = false` are shown but cannot be selected, using disabled text colors.
