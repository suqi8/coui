---
title: WindowIconDropdownMenu
requiresScaffoldHost: false
prerequisites:
  - Can be used anywhere, does not require `Scaffold` or `COUIPopupHost`
  - Renders at window level
hostComponent: None
popupHost: None
---

# WindowIconDropdownMenu

`WindowIconDropdownMenu` is an `IconButton` wrapper that opens a `WindowDropdownPopup` (rendered at window level via `Dialog`) when clicked. It is intended for toolbar action slots, such as the actions area of `TopAppBar`, where a single icon needs to expand into a list of actions, sort options, or filter toggles. Unlike `OverlayIconDropdownMenu`, it does not require a `Scaffold`.

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=windowIconDropdownMenu" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.menu.WindowIconDropdownMenu
import io.github.suqi8.coui.kmp.basic.DropdownEntry
import io.github.suqi8.coui.kmp.basic.DropdownItem
```

## Basic Usage

Place `WindowIconDropdownMenu` in the `actions` slot of a `TopAppBar` (or `SmallTopAppBar`). Clicking the icon opens the popup. This is the typical use case — a toolbar action button that expands into a list of menu items. Unlike `OverlayIconDropdownMenu`, no `Scaffold` is required around the menu itself, but a `Scaffold` is still the natural way to host a `TopAppBar`.

```kotlin
val entry = DropdownEntry(
    items = listOf("Edit", "Duplicate", "Share", "Delete").map { text ->
        DropdownItem(text = text, onClick = { /* handle action */ })
    }
)

Scaffold(
    topBar = {
        SmallTopAppBar(
            title = "Inbox",
            actions = {
                WindowIconDropdownMenu(entry = entry) {
                    Icon(imageVector = COUIIcons.Edit, contentDescription = "Action menu")
                }
            }
        )
    }
) { padding ->
    // page content
}
```

## Sort / Single Select

For sort menus or radio-style choices, set `selected` on each `DropdownItem` and let `collapseOnSelection = true` (default for the entry overload) close the popup after each pick.

```kotlin
var sortIndex by remember { mutableStateOf(0) }
val entry = DropdownEntry(
    items = listOf("Name", "Date", "Size").mapIndexed { index, text ->
        DropdownItem(text = text, selected = sortIndex == index, onClick = { sortIndex = index })
    }
)

WindowIconDropdownMenu(entry = entry) {
    Icon(imageVector = COUIIcons.Sort, contentDescription = "Sort")
}
```

## Multi Select

Track a `Set` of selected values, toggle each item from `onClick`, and pass `collapseOnSelection = false` so the popup stays open between picks.

```kotlin
var selected by remember { mutableStateOf(setOf("Photos")) }
val entry = DropdownEntry(
    items = listOf("Photos", "Videos", "Files").map { text ->
        DropdownItem(
            text = text,
            selected = text in selected,
            onClick = {
                selected = if (text in selected) selected - text else selected + text
            }
        )
    }
)

WindowIconDropdownMenu(entry = entry, collapseOnSelection = false) {
    Icon(imageVector = COUIIcons.SelectAll, contentDescription = "Multiple selection")
}
```

## Grouped Menu

Pass `entries: List<DropdownEntry>` to render multiple groups separated by dividers.

```kotlin
val entries = listOf(
    DropdownEntry(items = listOf("Item A-1", "Item A-2").map { DropdownItem(text = it) }),
    DropdownEntry(items = listOf("Item B-1", "Item B-2", "Item B-3").map { DropdownItem(text = it) })
)

WindowIconDropdownMenu(entries = entries) {
    Icon(imageVector = COUIIcons.MoreCircle, contentDescription = "More")
}
```

## Items with a Hint Slot

The `hint` slot renders between the title block and the selection indicator, capped at 40dp wide.
It suits a red dot, a count badge, or a very short label. Matching ColorOS, the hint is hidden
entirely while the row is disabled.

```kotlin
val entry = DropdownEntry(
    items = listOf(
        DropdownItem(text = "Inbox", hint = { Badge(count = 12) }),
        DropdownItem(text = "Updates", hint = { Badge() }),
        // The badge is suppressed because the row is disabled.
        DropdownItem(text = "Archive", hint = { Badge(count = 3) }, enabled = false),
    )
)

Scaffold {
    WindowIconDropdownMenu(entry = entry) {
        Icon(imageVector = COUIIcons.More, contentDescription = "More")
    }
}
```

## Group Headers

A `DropdownEntry` can declare a `title`, rendered above its items as a non-clickable header row
(12sp medium, secondary label color, at most 2 lines). Headers coexist with the group divider that
already separates adjacent entries.

```kotlin
val entries = listOf(
    DropdownEntry(
        title = "Sort by",
        items = listOf("Name", "Date modified").map { DropdownItem(text = it) }
    ),
    DropdownEntry(
        title = "Order",
        items = listOf("Ascending", "Descending").map { DropdownItem(text = it) }
    )
)

Scaffold {
    WindowIconDropdownMenu(entries = entries) {
        Icon(imageVector = COUIIcons.Sort, contentDescription = "Sort")
    }
}
```

## Alert Items

Set `alert = true` to mark a destructive action. Its title uses the error color instead of the
normal label color; disabled alert rows still fall back to the disabled color.

```kotlin
val entry = DropdownEntry(
    items = listOf(
        DropdownItem(text = "Rename"),
        DropdownItem(text = "Delete", alert = true),
    )
)

Scaffold {
    WindowIconDropdownMenu(entry = entry) {
        Icon(imageVector = COUIIcons.More, contentDescription = "More")
    }
}
```

## Component States

### Disabled State

```kotlin
WindowIconDropdownMenu(
    entry = DropdownEntry(items = listOf(DropdownItem(text = "Option 1"))),
    enabled = false
) {
    Icon(imageVector = COUIIcons.MoreCircle, contentDescription = "More")
}
```

The menu is also implicitly disabled when no `DropdownEntry` contains any items.

## Properties

### WindowIconDropdownMenu Properties (Entries Overload)

| Property Name       | Type                      | Description                                             | Default Value                       | Required |
| ------------------- | ------------------------- | ------------------------------------------------------- | ----------------------------------- | -------- |
| entries             | List\<DropdownEntry>      | Dropdown entry groups separated by dividers             | -                                   | Yes      |
| modifier            | Modifier                  | Modifier applied to the wrapping `Box`                  | Modifier                            | No       |
| enabled             | Boolean                   | Whether the icon button is interactive                  | true                                | No       |
| maxHeight           | Dp?                       | Maximum height of the dropdown popup                    | null                                | No       |
| dropdownColors      | DropdownColors            | Color configuration for dropdown items                  | DropdownDefaults.dropdownColors()   | No       |
| collapseOnSelection | Boolean                   | Whether to close the popup after each selection         | entries.size <= 1                   | No       |
| onExpandedChange    | ((Boolean) -> Unit)?      | Callback when the expanded state changes                | null                                | No       |
| backgroundColor     | Color                     | Background color of the underlying `IconButton`         | Color.Unspecified                   | No       |
| cornerRadius        | Dp                        | Corner radius of the underlying `IconButton`            | IconButtonDefaults.CornerRadius     | No       |
| minHeight           | Dp                        | Minimum height of the underlying `IconButton`           | IconButtonDefaults.MinHeight        | No       |
| minWidth            | Dp                        | Minimum width of the underlying `IconButton`            | IconButtonDefaults.MinWidth         | No       |
| content             | @Composable () -> Unit    | The icon (or other composable) shown inside the button  | -                                   | Yes      |

### Entry Overload Properties

| Property Name       | Type          | Description                                | Default Value | Required |
| ------------------- | ------------- | ------------------------------------------ | ------------- | -------- |
| entry               | DropdownEntry | Single dropdown entry group                | -             | Yes      |
| collapseOnSelection | Boolean       | Whether to close the popup after selection | true          | No       |

All other parameters are identical to the entries overload above.

### DropdownEntry Properties

| Property Name | Type                | Description                                                                                            | Default Value | Required |
| ------------- | ------------------- | ------------------------------------------------------------------------------------------------------ | ------------- | -------- |
| items         | List\<DropdownItem> | Items shown in this dropdown group                                                                     | -             | Yes      |
| enabled       | Boolean             | Whether this group is enabled. False disables all items; true still respects each item's enabled state | true          | No       |
| title         | String?             | Optional non-clickable group header rendered above the items (12sp medium, secondary label, max 2 lines) | null          | No       |

### DropdownItem Properties

| Property Name | Type                              | Description                                              | Default Value | Required |
| ------------- | --------------------------------- | -------------------------------------------------------- | ------------- | -------- |
| text          | String                            | Text shown for the item                                  | -             | Yes      |
| enabled       | Boolean                           | Whether the item can be clicked. Disabled items are gray | true          | No       |
| selected      | Boolean                           | Whether the item is selected                             | false         | No       |
| onClick       | (() -> Unit)?                     | Callback invoked when the item is clicked                | null          | No       |
| icon          | @Composable ((Modifier) -> Unit)? | Icon shown before the item text                          | null          | No       |
| summary       | String?                           | Summary text shown below the item text                   | null          | No       |
| children      | List\<DropdownItem>?              | Optional submenu items; cascading variants only          | null          | No       |
| hint          | @Composable (() -> Unit)?         | Optional trailing hint slot (badge, red dot, short count) shown before the selection indicator, width-capped at 40dp. Hidden entirely while the row is disabled | null          | No       |
| alert         | Boolean                           | Whether this is an alert (destructive) item; its title uses the error color | false         | No       |

### DropdownColors Properties

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
