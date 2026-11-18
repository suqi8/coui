# ChipGroup

`ChipGroup` lays chips out in rows that wrap at the available width, mirroring COUIChipGroup: 8dp between chips and between rows, the COUI single-selection and selection-required rules, and a collapsable mode that shows only the first rows behind an expand / collapse button.

<div style="position: relative; height: 320px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=chipGroup" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.ChipGroup
import io.github.suqi8.coui.kmp.basic.ChipGroupDefaults
import io.github.suqi8.coui.kmp.basic.ChipGroupState
import io.github.suqi8.coui.kmp.basic.rememberChipGroupState
```

## Basic Usage

The selection lives in a `ChipGroupState`: call `toggle(index)` from each chip and read `isSelected(index)`.

```kotlin
val state = rememberChipGroupState()
val labels = listOf("Recommended", "Nearby", "Popular", "New")

ChipGroup(state = state) {
    labels.forEachIndexed { index, label ->
        Chip(
            selected = state.isSelected(index),
            onClick = { state.toggle(index) },
            label = label,
        )
    }
}
```

## Single Selection

`singleSelection` keeps at most one chip selected; `selectionRequired` refuses to unselect the last selected chip (COUI `setSingleSelection` / `setSelectionRequired`):

```kotlin
val state = rememberChipGroupState(
    singleSelection = true,
    selectionRequired = true,
    initialSelection = setOf(0),
)
```

## Collapsable

With `collapsable = true` and more rows than `collapsedMaxRows`, the group shows the 28dp round expand / collapse button (`IconChip`). Collapsed, only the first `collapsedMaxRows` rows are shown and the last of them keeps room for the expand button; expanded, the collapse button follows the last chip. Height changes run on the COUI spring (response 0.35, bounce 0).

```kotlin
val state = rememberChipGroupState(initialCollapsed = true)

ChipGroup(state = state, collapsable = true, collapsedMaxRows = 2) {
    // chips
}
```

## Single Line

`singleLine = true` keeps every chip on one row; put the group in a horizontally scrolling container:

```kotlin
ChipGroup(
    modifier = Modifier.horizontalScroll(rememberScrollState()),
    singleLine = true,
) {
    // chips
}
```

## Properties

### ChipGroup

| Property             | Type                   | Description                                        | Default Value                       | Required |
| -------------------- | ---------------------- | -------------------------------------------------- | ----------------------------------- | -------- |
| modifier             | Modifier               | Modifier applied to the group                      | Modifier                            | No       |
| state                | ChipGroupState         | Selection and collapsed state                      | rememberChipGroupState()            | No       |
| singleLine           | Boolean                | Whether the chips stay on one row                  | false                               | No       |
| collapsable          | Boolean                | Whether the group may collapse                     | false                               | No       |
| collapsedMaxRows     | Int                    | Rows shown while collapsed                         | ChipGroupDefaults.CollapsedMaxRows  | No       |
| horizontalSpacing    | Dp                     | Spacing between chips of a row                     | ChipGroupDefaults.HorizontalSpacing | No       |
| verticalSpacing      | Dp                     | Spacing between rows                               | ChipGroupDefaults.VerticalSpacing   | No       |
| collapseButtonColors | ChipColors             | Colors of the expand / collapse button             | ChipDefaults.chipColors()           | No       |
| content              | @Composable () -> Unit | The chips                                          | -                                   | Yes      |

### ChipGroupState

| Member              | Type        | Description                                                        |
| ------------------- | ----------- | ------------------------------------------------------------------ |
| singleSelection     | Boolean     | At most one chip selected                                          |
| selectionRequired   | Boolean     | The last selected chip cannot be unselected                        |
| selectedIndices     | Set\<Int>    | The selected chip indices                                          |
| collapsed           | Boolean     | Whether a collapsable group is collapsed (writable)                |
| isSelected(index)   | Boolean     | Whether the chip at `index` is selected                            |
| toggle(index)       | Unit        | Toggles the chip at `index` following the selection rules          |
| clearSelection()    | Unit        | Clears the selection                                               |

`rememberChipGroupState(singleSelection, selectionRequired, initialSelection, initialCollapsed)` creates and remembers one.

### ChipGroupDefaults

| Constant          | Type | Default Value | COUI Source                                 |
| ----------------- | ---- | ------------- | ------------------------------------------- |
| HorizontalSpacing | Dp   | 8.dp          | coui_chip_group_default_spacing_horizontal  |
| VerticalSpacing   | Dp   | 8.dp          | coui_chip_group_default_spacing_vertical    |
| CollapsedMaxRows  | Int  | 3             | COUIChipGroup `collapsedMaxRows` default    |
