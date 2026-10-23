---
title: OverlayCascadingListPopup
requiresScaffoldHost: true
prerequisites:
  - Must be used within `Scaffold` to provide `COUIPopupHost`
  - Using outside `Scaffold` will cause popup content not to render
  - Multiple nested or side-by-side `Scaffold`s are supported without extra configuration
hostComponent: Scaffold
popupHost: COUIPopupHost
---

# OverlayCascadingListPopup

`OverlayCascadingListPopup` is a popup list that supports a two-level cascading menu. Items whose `DropdownItem.children` is non-empty become submenu triggers; tapping such an item morphs the popup into a secondary list anchored at the trigger row, while the primary list scales down behind a translucent mask. Cascading depth is limited to **2**.

<div style="position: relative; height: 410px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=overlayCascadingListPopup" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

::: danger Prerequisite
This component depends on `Scaffold` providing `COUIPopupHost` to render popup content. It must be used within `Scaffold`, otherwise popup content will not render correctly.
:::

## Import

```kotlin
import io.github.suqi8.coui.kmp.overlay.OverlayCascadingListPopup
import io.github.suqi8.coui.kmp.basic.DropdownEntry
import io.github.suqi8.coui.kmp.basic.DropdownItem
import io.github.suqi8.coui.kmp.basic.PopupPositionProvider
```

## Basic Usage

Build `entries` so that some `DropdownItem`s carry a non-empty `children` list. The component renders a chevron next to such trigger rows; tapping the trigger expands the second level inline.

```kotlin
var showPopup by remember { mutableStateOf(false) }
var sortIndex by remember { mutableStateOf(0) }
var viewIndex by remember { mutableStateOf(0) }

val sortLabels = listOf("Sort by capture date", "Sort by date added")
val viewLabels = listOf("Group by date", "Compact")
val entries = listOf(
    DropdownEntry(
        items = sortLabels.mapIndexed { idx, label ->
            DropdownItem(
                text = label,
                selected = sortIndex == idx,
                onClick = { sortIndex = idx },
            )
        },
    ),
    DropdownEntry(
        items = listOf(
            DropdownItem(
                text = "View mode",
                children = viewLabels.mapIndexed { idx, label ->
                    DropdownItem(
                        text = label,
                        selected = viewIndex == idx,
                        onClick = { viewIndex = idx },
                    )
                },
            ),
        ),
    ),
)

Scaffold {
    Box {
        TextButton(
            text = "Click to show menu",
            onClick = { showPopup = true },
        )
        OverlayCascadingListPopup(
            show = showPopup,
            entries = entries,
            onDismissRequest = { showPopup = false },
        )
    }
}
```

::: tip Cascading Depth
Cascading depth is capped at 2. Items at the secondary level cannot have their own `children`; deeper trees are silently ignored.
:::

## Component States

### Different Alignments

```kotlin
OverlayCascadingListPopup(
    show = showPopup,
    entries = entries,
    onDismissRequest = { showPopup = false },
    alignment = PopupPositionProvider.Align.Start,
)
```

### Window Dimming

```kotlin
OverlayCascadingListPopup(
    show = showPopup,
    entries = entries,
    onDismissRequest = { showPopup = false },
    enableWindowDim = true, // COUI popups do not dim; opt in here
)
```

### Keep Popup Open After Selection

By default the popup closes when the user picks any leaf item. Set `collapseOnSelection = false` to keep it open (e.g., for multi-select within a submenu).

```kotlin
OverlayCascadingListPopup(
    show = showPopup,
    entries = entries,
    onDismissRequest = { showPopup = false },
    collapseOnSelection = false,
)
```

### Selected Item Marking

Leaf items with `selected = true` show a trailing check icon tinted with `DropdownColors.selectedIndicatorColor`; submenu trigger rows always show a trailing chevron instead.

```kotlin
DropdownItem(
    text = "Sort by date added",
    selected = true, // Marks this leaf with a trailing check icon
    onClick = { /* ... */ },
)
```

### Sub Menu Behavior

The sub menu follows COUI 17. On a compact window (narrower than 600dp) it covers the main menu: it is as wide as the main menu and starts with a copy of the parent row (medium title, tinted indicator) sitting on that row, then slides up to 14dp above it while its outline grows from the row; the main menu shrinks to 0.9, moves up to keep a 12dp strip visible and dims its rows to 30%. On wider windows the main menu shrinks to 0.9 with its rows at 70% while the sub menu scales in beside it, overlapping 14dp. Tapping the main menu, the copied header, outside the popup, or going back collapses the sub menu; dragging across the main menu collapses it after 500ms; at the top level, back and outside taps dismiss the popup via `onDismissRequest`. Predictive back previews the collapse or dismissal and restores the popup when cancelled.

## Properties

### OverlayCascadingListPopup

| Property Name         | Type                        | Description                                                                                                                                                                  | Default Value                              |
| --------------------- | --------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------ |
| show                  | Boolean                     | Whether to show the popup.                                                                                                                                                   | -                                          |
| entries               | List\<DropdownEntry>        | Grouped dropdown entries; top-level items with non-empty `children` become submenu triggers.                                                                                 | -                                          |
| onDismissRequest      | () -> Unit                  | Called when the user requests dismissal (e.g., clicking outside, tapping the back button).                                                                                   | -                                          |
| popupModifier         | Modifier                    | Modifier applied to the popup body.                                                                                                                                          | Modifier                                   |
| onDismissFinished     | (() -> Unit)?               | Called after the exit animation finishes.                                                                                                                                    | null                                       |
| popupPositionProvider | PopupPositionProvider       | Position strategy for the primary popup relative to its anchor.                                                                                                              | ListPopupDefaults.DropdownPositionProvider |
| alignment             | PopupPositionProvider.Align | Alignment of the primary popup.                                                                                                                                              | PopupPositionProvider.Align.End            |
| enableWindowDim       | Boolean                     | Whether to dim the rest of the window while the popup is shown.                                                                                                              | false                                       |
| maxHeight             | Dp?                         | Maximum height of either side. Null bounds it by the safe area.                                                                                                              | null                                       |
| minWidth              | Dp                          | Minimum width of the popup.                                                                                                                                                  | ListPopupDefaults.MinWidth                 |
| renderInRootScaffold  | Boolean                     | Whether to render the popup in the root (outermost) Scaffold. When true, the popup covers the full screen. When false, it renders within the current Scaffold's bounds with position compensation. | true |
| dropdownColors        | DropdownColors              | Colors used by every row.                                                                                                                                                    | DropdownDefaults.dropdownColors()          |
| collapseOnSelection   | Boolean                     | When true, selecting any leaf dismisses the popup.                                                                                                                           | true                                       |
| backgroundModifier    | Modifier                    | Applied inside the popup surface under the rows; the slot for the COUI 17 blurred background, e.g. coui-blur `Modifier.couiMaterialBlur(backdrop, MaterialBlurDefaults.popup(isDark))`. | Modifier |
| backgroundColor       | Color                       | Popup surface fill (couiColorBackgroundTop). Pass a transparent color with a blurring `backgroundModifier`. | ListPopupDefaults.backgroundColor() |
| materialEffect        | Boolean                     | Draws the material effects of a blurred COUI 17 popup: the shadow-edge stroke and the touch spotlight. | false |

### DropdownEntry

| Property Name | Type                | Description                                                                                            | Default Value | Required |
| ------------- | ------------------- | ------------------------------------------------------------------------------------------------------ | ------------- | -------- |
| items         | List\<DropdownItem> | Items shown in this dropdown group                                                                     | -             | Yes      |
| enabled       | Boolean             | Whether this group is enabled. False disables all items; true still respects each item's enabled state | true          | No       |
| title         | String?             | Optional non-clickable group header rendered above the items (12sp medium, secondary label, max 2 lines) | null          | No       |

### DropdownItem

| Property Name | Type                              | Description                                                                                                                       | Default Value | Required |
| ------------- | --------------------------------- | --------------------------------------------------------------------------------------------------------------------------------- | ------------- | -------- |
| text          | String                            | Text shown for the item                                                                                                           | -             | Yes      |
| enabled       | Boolean                           | Whether the item can be clicked. Disabled items are gray                                                                          | true          | No       |
| selected      | Boolean                           | Whether the item is selected                                                                                                      | false         | No       |
| onClick       | (() -> Unit)?                     | Callback invoked when the item is clicked. Ignored when `children` is non-empty (the click expands the submenu instead)            | null          | No       |
| icon          | @Composable ((Modifier) -> Unit)? | Icon shown before the item text                                                                                                   | null          | No       |
| summary       | String?                           | Summary text shown below the item text                                                                                            | null          | No       |
| children      | List\<DropdownItem>?              | Optional submenu items; only the cascading variants render these as a submenu (depth limited to 2)                                | null          | No       |
| hint          | @Composable (() -> Unit)?         | Optional trailing hint slot (badge, red dot, short count) shown before the selection indicator, width-capped at 40dp. Hidden entirely while the row is disabled | null          | No       |
| alert         | Boolean                           | Whether this is an alert (destructive) item; its title uses the error color | false         | No       |

### DropdownColors

| Property Name          | Type  | Description                             |
| ---------------------- | ----- | --------------------------------------- |
| contentColor           | Color | Color of the option title               |
| summaryColor           | Color | Color of the option summary             |
| containerColor         | Color | Background color of the option          |
| selectedContentColor   | Color | Title color of the selected option      |
| selectedSummaryColor   | Color | Summary color of the selected option    |
| selectedContainerColor | Color | Background color of the selected option |
| selectedIndicatorColor | Color | Color of the selected indicator icon    |
| disabledContentColor   | Color | Title color of a disabled option        |
| alertContentColor      | Color | Title color of an alert option          |
| headerColor            | Color | Title color of a group header row       |
| submenuIndicatorColor  | Color | Indicator color while the sub menu is closed |

### PopupPositionProvider.Align

| Value       | Description                                         |
| ----------- | --------------------------------------------------- |
| Start       | Aligns the popup to the start of the anchor.        |
| End         | Aligns the popup to the end of the anchor.          |
| TopStart    | Aligns the popup to the top-start of the anchor.    |
| TopEnd      | Aligns the popup to the top-end of the anchor.      |
| BottomStart | Aligns the popup to the bottom-start of the anchor. |
| BottomEnd   | Aligns the popup to the bottom-end of the anchor.   |
