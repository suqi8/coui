---
title: WindowSpinnerPreference
requiresScaffoldHost: false
prerequisites:
  - Can be used anywhere, does not require `Scaffold` or `COUIPopupHost`
  - Renders at the window layer
hostComponent: None
popupHost: None
---

# WindowSpinnerPreference

`WindowSpinnerPreference` is a dropdown selector component in COUI that provides titles, summaries, and a list of options with icons and text. It renders at the window level without needing a `Scaffold` host, making it suitable for use cases where `Scaffold` is not available or desired.

<div style="position: relative; height: 420px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=windowSpinnerPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

::: tip Note
This component does not rely on `Scaffold` and can be used in any Composable scope.
:::

## Import

```kotlin
import io.github.suqi8.coui.kmp.preference.WindowSpinnerPreference
import io.github.suqi8.coui.kmp.popup.WindowDropdownDialog
import io.github.suqi8.coui.kmp.basic.DropdownEntry
import io.github.suqi8.coui.kmp.basic.DropdownItem
```

## Basic Usage

The WindowSpinnerPreference component provides basic dropdown selector functionality:

```kotlin
var selectedIndex by remember { mutableStateOf(0) }
val options = listOf(
    DropdownItem(text = "Option 1"),
    DropdownItem(text = "Option 2"),
    DropdownItem(text = "Option 3"),
)

WindowSpinnerPreference(
    title = "Dropdown Selector",
    items = options,
    selectedIndex = selectedIndex,
    onSelectedIndexChange = { selectedIndex = it }
)
```

## Options with Icons and Summaries

```kotlin
// Create a rounded rectangle Painter
private class RoundedRectanglePainter(
    private val cornerRadius: Dp = 6.dp
) : Painter() {
    override val intrinsicSize = Size.Unspecified
    override fun DrawScope.onDraw() {
        drawRoundRect(
            color = Color.White,
            size = Size(size.width, size.height),
            cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
        )
    }
}

var selectedIndex by remember { mutableStateOf(0) }
val options = listOf(
    DropdownItem(
        icon = { modifier -> Icon(RoundedRectanglePainter(), "Icon", modifier, Color(0xFFFF5B29)) },
        text = "Red Theme",
        summary = "Energetic Red"
    ),
    DropdownItem(
        icon = { modifier -> Icon(RoundedRectanglePainter(), "Icon", modifier, Color(0xFF3482FF)) },
        text = "Blue Theme",
        summary = "Calm Blue"
    ),
)

WindowSpinnerPreference(
    title = "Menu",
    items = options,
    selectedIndex = selectedIndex,
    onSelectedIndexChange = { selectedIndex = it }
)
```

## Component State

### Disabled State

```kotlin
WindowSpinnerPreference(
    title = "Disabled Spinner",
    summary = "This spinner is currently unavailable",
    items = listOf(DropdownItem(text = "Option 1")),
    selectedIndex = 0,
    onSelectedIndexChange = {},
    enabled = false
)
```

## Dialog Mode

WindowSpinnerPreference also supports a dialog mode, which is useful for displaying a larger list of options or when a more prominent selection interface is needed. This mode is activated by providing a `dialogButtonString`.

```kotlin
var selectedIndex by remember { mutableStateOf(0) }
val options = listOf(
    DropdownItem(text = "Option A"),
    DropdownItem(text = "Option B"),
    DropdownItem(text = "Option C")
)

WindowSpinnerPreference(
    title = "Dialog Selector",
    dialogButtonString = "Cancel",
    items = options,
    selectedIndex = selectedIndex,
    onSelectedIndexChange = { selectedIndex = it },
)
```

### Standalone Dropdown Dialog

The dialog behind dialog mode is public as `WindowDropdownDialog` (`io.github.suqi8.coui.kmp.popup`). Use it to open the same option dialog from your own trigger instead of a preference row. It is a window-layer `WindowDialog` (no `Scaffold` needed) with the `DropdownEntry` list as its list panel and one full-width button at the bottom that dismisses it. Item clicks call `DropdownItem.onClick` and close the dialog when `collapseOnSelection` is true; like a COUI list dialog, they play no haptic.

```kotlin
var show by remember { mutableStateOf(false) }
var selectedIndex by remember { mutableStateOf(0) }
val entry = DropdownEntry(
    items = listOf("Option A", "Option B", "Option C").mapIndexed { index, text ->
        DropdownItem(text = text, selected = selectedIndex == index, onClick = { selectedIndex = index })
    }
)

TextButton(text = "Choose", onClick = { show = true })
WindowDropdownDialog(
    entry = entry,
    title = "Choose an option",
    dialogButtonString = "Cancel",
    show = show,
    onDismiss = { show = false },
    onDismissFinished = {},
    dropdownColors = DropdownDefaults.dialogDropdownColors()
)
```

## Grouped Options

Use `DropdownEntry` when you need a custom item list with selected states, disabled items, item callbacks, icons, or summaries. Use `entries` to show multiple option groups separated by dividers.

```kotlin
var firstSelectedIndex by remember { mutableStateOf(0) }
var secondSelectedIndex by remember { mutableStateOf(0) }
val entries = listOf(
    DropdownEntry(
        items = listOf("Small", "Medium").mapIndexed { index, text ->
            DropdownItem(text = text, selected = firstSelectedIndex == index, onClick = { firstSelectedIndex = index })
        }
    ),
    DropdownEntry(
        items = listOf("Red", "Green", "Blue").mapIndexed { index, text ->
            DropdownItem(text = text, selected = secondSelectedIndex == index, onClick = { secondSelectedIndex = index })
        }
    )
)

WindowSpinnerPreference(
    title = "Grouped Selector",
    entries = entries,
    collapseOnSelection = false
)
```

For the `entries` overload, `collapseOnSelection` controls whether the popup closes after an item is selected. It defaults to `entries.size <= 1`, so a single group closes after selection while multiple groups stay open for consecutive changes. The same `entry` and `entries` overloads are also available in dialog mode by providing `dialogButtonString`.

## Multi Select

Because selection state lives on `DropdownItem`, multiple items can be selected by keeping a set of selected values and toggling each item from `onClick`.

```kotlin
var selectedItems by remember { mutableStateOf(setOf("A1", "B2")) }
val entries = listOf(
    DropdownEntry(
        items = listOf("A1", "A2").map { text ->
            DropdownItem(
                text = text,
                selected = text in selectedItems,
                onClick = {
                    selectedItems = if (text in selectedItems) selectedItems - text else selectedItems + text
                }
            )
        }
    ),
    DropdownEntry(
        items = listOf("B1", "B2", "B3").map { text ->
            DropdownItem(
                text = text,
                selected = text in selectedItems,
                onClick = {
                    selectedItems = if (text in selectedItems) selectedItems - text else selectedItems + text
                }
            )
        }
    )
)

WindowSpinnerPreference(
    title = "Multi Select Selector",
    entries = entries,
    collapseOnSelection = false
)
```

## Observe Expanded State

```kotlin
var selectedIndex by remember { mutableStateOf(0) }
var expanded by remember { mutableStateOf(false) }
val options = listOf(
    DropdownItem(text = "Option A"),
    DropdownItem(text = "Option B"),
    DropdownItem(text = "Option C")
)

WindowSpinnerPreference(
    title = "Dropdown Selector",
    summary = if (expanded) "Expanded" else "Collapsed",
    items = options,
    selectedIndex = selectedIndex,
    onExpandedChange = { expanded = it },
    onSelectedIndexChange = { selectedIndex = it }
)
```

## Interaction

The row follows COUI 17 `COUIMenuPreference` / `COUIClickSelectMenu`:

- **Opens at the finger**: the touch-down point is recorded (`PreciseClickHelper`) and the menu is placed at that point rather than at the edge of the row. An accessibility click, which has no touch point, falls back to the row.
- **Soft keyboard**: if the keyboard is showing, a tap hides it first and the menu opens 100ms later (240ms on windows at least 840dp tall), so it is placed against the final window.
- **Anchor row**: while the menu is showing, the row keeps its hover layer locked (`COUIPopupListWindow.setAnchorHoveredState`); the layer is released as soon as the menu starts to leave.
- **Arrow**: the up-down arrow is `couiColorLabelTertiary`, and `couiColorLabelQuaternary` while the row is disabled.
- **Selected value**: shown as the COUI assignment text (`TextAppearance.COUI.List.Assignment.End`): 13sp with the body_s line spacing, end aligned, at most two lines, 4dp before the arrow.
- **Haptics and dialog mode**: opening the menu plays no haptic, as in COUI. In dialog mode the row plays its normal press and release (a COUI list dialog locks no anchor state), and picking an option plays no haptic either.

## Properties

### WindowSpinnerPreference Properties (Popup Mode)

| Property Name         | Type                      | Description                          | Default Value                         | Required |
| --------------------- | ------------------------- | ------------------------------------ | ------------------------------------- | -------- |
| items                 | List\<DropdownItem>       | List of dropdown items               | -                                     | Yes      |
| selectedIndex         | Int                       | Index of currently selected item     | -                                     | Yes      |
| title                 | String                    | Title of the spinner                 | -                                     | Yes      |
| modifier              | Modifier                  | Modifier applied to the component    | Modifier                              | No       |
| titleColor            | BasicComponentColors      | Title text color configuration       | BasicComponentDefaults.titleColor()   | No       |
| summary               | String?                   | Summary description                  | null                                  | No       |
| summaryColor          | BasicComponentColors      | Summary text color configuration     | BasicComponentDefaults.summaryColor() | No       |
| spinnerColors         | DropdownColors            | Color configuration for spinner      | DropdownDefaults.dropdownColors()     | No       |
| startAction           | @Composable (() -> Unit)? | Custom start side content            | null                                  | No       |
| bottomAction          | @Composable (() -> Unit)? | Custom bottom side content           | null                                  | No       |
| insideMargin          | PaddingValues             | Internal content padding             | BasicComponentDefaults.InsideMargin   | No       |
| cardListPosition | CardListPosition | Row position inside its card group ([CardListGroup](./card.md#cardlistgroup)); rounded outer edges gain extra padding | LocalCardListPosition.current (None outside a CardListGroup) | No |
| maxHeight             | Dp?                       | Maximum height of popup              | null                                  | No       |
| enabled               | Boolean                   | Whether component is interactive     | true                                  | No       |
| showValue             | Boolean                   | Whether to show the selected value   | true                                  | No       |
| onExpandedChange      | ((Boolean) -> Unit)?      | Callback when expanded state changes | null                                  | No       |
| onSelectedIndexChange | ((Int) -> Unit)?          | Selection change callback            | null                                  | No       |

### Entry Overload Properties

| Property Name       | Type          | Description                                | Default Value | Required |
| ------------------- | ------------- | ------------------------------------------ | ------------- | -------- |
| entry               | DropdownEntry | Single dropdown entry group                | -             | Yes      |
| collapseOnSelection | Boolean       | Whether to close the popup after selection | true          | No       |

### Grouped Entries Overload Properties

| Property Name       | Type                 | Description                                     | Default Value     | Required |
| ------------------- | -------------------- | ----------------------------------------------- | ----------------- | -------- |
| entries             | List\<DropdownEntry> | Dropdown entry groups separated by dividers     | -                 | Yes      |
| collapseOnSelection | Boolean              | Whether to close the popup after each selection | entries.size <= 1 | No       |

All other parameters are identical to the items overload above. The `entry` and `entries` overloads do not take `onSelectedIndexChange` — use each `DropdownItem.onClick` instead.

### WindowSpinnerPreference Properties (Dialog Mode)

| Property Name         | Type                      | Description                          | Default Value                           | Required |
| --------------------- | ------------------------- | ------------------------------------ | --------------------------------------- | -------- |
| items                 | List\<DropdownItem>       | List of dropdown items               | -                                       | Yes      |
| selectedIndex         | Int                       | Index of currently selected item     | -                                       | Yes      |
| title                 | String                    | Title of the spinner                 | -                                       | Yes      |
| dialogButtonString    | String                    | Text for the dialog button           | -                                       | Yes      |
| modifier              | Modifier                  | Modifier applied to the component    | Modifier                                | No       |
| popupModifier         | Modifier                  | Modifier for the popup dialog        | Modifier                                | No       |
| titleColor            | BasicComponentColors      | Title text color configuration       | BasicComponentDefaults.titleColor()     | No       |
| summary               | String?                   | Summary description                  | null                                    | No       |
| summaryColor          | BasicComponentColors      | Summary text color configuration     | BasicComponentDefaults.summaryColor()   | No       |
| spinnerColors         | DropdownColors            | Color configuration for spinner      | DropdownDefaults.dialogDropdownColors() | No       |
| startAction           | @Composable (() -> Unit)? | Custom start side content            | null                                    | No       |
| bottomAction          | @Composable (() -> Unit)? | Custom bottom side content           | null                                    | No       |
| insideMargin          | PaddingValues             | Internal content padding             | BasicComponentDefaults.InsideMargin     | No       |
| cardListPosition | CardListPosition | Row position inside its card group ([CardListGroup](./card.md#cardlistgroup)); rounded outer edges gain extra padding | LocalCardListPosition.current (None outside a CardListGroup) | No |
| enabled               | Boolean                   | Whether component is interactive     | true                                    | No       |
| showValue             | Boolean                   | Whether to show the selected value   | true                                    | No       |
| collapseOnSelection   | Boolean                   | Whether to close the dialog after selection | true                             | No       |
| onExpandedChange      | ((Boolean) -> Unit)?      | Callback when expanded state changes | null                                    | No       |
| onSelectedIndexChange | ((Int) -> Unit)?          | Selection change callback            | null                                    | No       |

### Dialog Entry Overload Properties

| Property Name      | Type          | Description                                 | Default Value | Required |
| ------------------ | ------------- | ------------------------------------------- | ------------- | -------- |
| entry              | DropdownEntry | Single dropdown entry group                 | -             | Yes      |
| dialogButtonString | String        | Text for the dialog button                  | -             | Yes      |
| collapseOnSelection | Boolean      | Whether to close the dialog after selection | true          | No       |

### Dialog Grouped Entries Overload Properties

| Property Name      | Type                 | Description                                      | Default Value     | Required |
| ------------------ | -------------------- | ------------------------------------------------ | ----------------- | -------- |
| entries            | List\<DropdownEntry> | Dropdown entry groups separated by dividers      | -                 | Yes      |
| dialogButtonString | String               | Text for the dialog button                       | -                 | Yes      |
| collapseOnSelection | Boolean             | Whether to close the dialog after each selection | entries.size <= 1 | No       |

All other parameters are identical to the dialog-mode items overload above. The `entry` and `entries` overloads do not take `onSelectedIndexChange` — use each `DropdownItem.onClick` instead.

### WindowDropdownDialog Properties

`WindowDropdownDialog` has an `entry: DropdownEntry` overload (where `collapseOnSelection` defaults to `true`) and an `entries: List<DropdownEntry>` overload:

| Property Name       | Type                 | Description                                                    | Default Value     | Required |
| ------------------- | -------------------- | -------------------------------------------------------------- | ----------------- | -------- |
| entries             | List\<DropdownEntry> | Option groups shown in the dialog list, separated by dividers  | -                 | Yes      |
| title               | String               | Dialog title                                                   | -                 | Yes      |
| dialogButtonString  | String               | Text of the bottom button, which dismisses the dialog          | -                 | Yes      |
| show                | Boolean              | Whether the dialog is shown                                    | -                 | Yes      |
| onDismiss           | () -> Unit           | Called on the button, an outside tap, back, or a collapsing selection | -          | Yes      |
| onDismissFinished   | () -> Unit           | Called after the exit animation finishes                       | -                 | Yes      |
| dropdownColors      | DropdownColors       | Colors of the option rows                                      | -                 | Yes      |
| popupModifier       | Modifier             | Modifier applied to the dialog                                 | Modifier          | No       |
| collapseOnSelection | Boolean              | Whether to close the dialog after an item is clicked           | entries.size <= 1 | No       |

### DropdownEntry Properties

| Property Name | Type                | Description                        | Default Value | Required |
| ------------- | ------------------- | ---------------------------------- | ------------- | -------- |
| items         | List\<DropdownItem> | Items shown in this dropdown group | -             | Yes      |
| enabled       | Boolean             | Whether this group is enabled. False disables all items; true still respects each item's enabled state | true | No |
| title         | String?             | Optional non-clickable group header rendered above the items (12sp medium, secondary label, max 2 lines) | null          | No       |
| horizontal    | Boolean             | Render the group as the COUI 17 row of horizontal items (up to 5 icons, or 3 icons with labels); popup mode only, always followed by a divider | false | No |


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
| submenuIndicatorColor  | Color | Indicator color while the sub menu is closed |
