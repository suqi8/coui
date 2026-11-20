---
title: OverlaySpinnerPreference
requiresScaffoldHost: true
prerequisites:
  - Must be used within `Scaffold` to provide `COUIPopupHost`
  - Using outside `Scaffold` will cause popup content not to render
  - Multiple nested or side-by-side `Scaffold`s are supported without extra configuration
hostComponent: Scaffold
popupHost: COUIPopupHost
---

# OverlaySpinnerPreference

`OverlaySpinnerPreference` is a dropdown selector component in COUI that provides titles, summaries, and a list of options with icons and text. It supports click interaction and various display modes, commonly used in option settings with visual aids. This component is similar to `OverlayDropdownPreference` but offers richer functionality and interaction experience.

<div style="position: relative; height: 420px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=overlaySpinnerPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

::: danger Prerequisite
This component depends on `Scaffold` providing `COUIPopupHost` to render popup content. It must be used within `Scaffold`, otherwise popup content will not render correctly.
:::

## Import

```kotlin
import io.github.suqi8.coui.kmp.preference.OverlaySpinnerPreference
import io.github.suqi8.coui.kmp.popup.OverlayDropdownDialog
import io.github.suqi8.coui.kmp.basic.DropdownEntry
import io.github.suqi8.coui.kmp.basic.DropdownItem
```

## Basic Usage

The OverlaySpinnerPreference component provides basic dropdown selector functionality:

```kotlin
var selectedIndex by remember { mutableStateOf(0) }
val options = listOf(
    DropdownItem(text = "Option 1"),
    DropdownItem(text = "Option 2"),
    DropdownItem(text = "Option 3"),
)

Scaffold {
    OverlaySpinnerPreference(
        title = "Dropdown Selector",
        items = options,
        selectedIndex = selectedIndex,
        onSelectedIndexChange = { selectedIndex = it }
    )
}
```

## Options with Icons and Summaries

```kotlin
// Create a rounded rectangle Painter
class RoundedRectanglePainter(
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
        icon = { Icon(RoundedRectanglePainter(), "Icon", Modifier.padding(end = 12.dp), Color(0xFFFF5B29)) },
        text = "Red Theme",
        summary = "Vibrant red"
    ),
    DropdownItem(
        icon = { Icon(RoundedRectanglePainter(), "Icon", Modifier.padding(end = 12.dp), Color(0xFF3482FF)) },
        text = "Blue Theme",
        summary = "Calm blue"
    ),
    DropdownItem(
        icon = { Icon(RoundedRectanglePainter(), "Icon", Modifier.padding(end = 12.dp), Color(0xFF36D167)) },
        text = "Green Theme",
        summary = "Fresh green"
    ),
    DropdownItem(
        icon = { Icon(RoundedRectanglePainter(), "Icon", Modifier.padding(end = 12.dp), Color(0xFFFFB21D)) },
        text = "Yellow Theme",
        summary = "Bright yellow"
    )
)

Scaffold {
    OverlaySpinnerPreference(
        title = "Function Selection",
        summary = "Choose the action you want to perform",
        items = options,
        selectedIndex = selectedIndex,
        onSelectedIndexChange = { selectedIndex = it }
    )
}
```

## Component States

### Disabled State

```kotlin
OverlaySpinnerPreference(
    title = "Disabled Selector",
    summary = "This selector is currently unavailable",
    items = listOf(DropdownItem(text = "Option 1")),
    selectedIndex = 0,
    onSelectedIndexChange = {},
    enabled = false
)
```

## Dialog Mode

OverlaySpinnerPreference also supports dialog mode, suitable for displaying a larger number of options or when a more prominent selection interface is needed. This mode can be activated by providing the `dialogButtonString` parameter.

```kotlin
var selectedIndex by remember { mutableStateOf(0) }
val options = listOf(
    DropdownItem(text = "Option 1"),
    DropdownItem(text = "Option 2"),
    DropdownItem(text = "Option 3")
)

Scaffold {
    OverlaySpinnerPreference(
        title = "Dialog Mode",
        dialogButtonString = "Cancel",
        items = options,
        selectedIndex = selectedIndex,
        onSelectedIndexChange = { selectedIndex = it }
    )
}
```

In dialog mode the row is a COUI `COUIListPreference` row: the selected value, then the `coui_btn_next` jump arrow instead of the up-down arrow. A click opens the COUI 17 single-choice dialog, the same dialog `ListPreference` opens: `coui_select_dialog_singlechoice` rows (an optional icon, the label over an optional summary, an end radio button, a hairline between rows) and `dialogButtonString` as the only button in the COUI 17 capsule button bar. Picking an option calls its `onClick` and plays no haptic; with `collapseOnSelection` the dialog dismisses and the radio stays where it opened while the dialog leaves. A group `title` adds a header row; `hint`, `alert`, `stateIcon` and `children` do not apply, as a COUI list dialog has none of them.

### Standalone Dropdown Dialog

The dialog behind dialog mode is public as `OverlayDropdownDialog` (`io.github.suqi8.coui.kmp.popup`). Use it to open the same option dialog from your own trigger instead of a preference row. It is a scaffold-hosted `OverlayDialog` with the same COUI single-choice list and one button in the COUI 17 button bar (`DialogButtonBar`) that dismisses it. Item clicks call `DropdownItem.onClick` and close the dialog when `collapseOnSelection` is true; like a COUI list dialog, they play no haptic.

```kotlin
var show by remember { mutableStateOf(false) }
var selectedIndex by remember { mutableStateOf(0) }
val entry = DropdownEntry(
    items = listOf("Option 1", "Option 2", "Option 3").mapIndexed { index, text ->
        DropdownItem(text = text, selected = selectedIndex == index, onClick = { selectedIndex = index })
    }
)

Scaffold {
    TextButton(text = "Choose", onClick = { show = true })
    OverlayDropdownDialog(
        entry = entry,
        title = "Choose an option",
        dialogButtonString = "Cancel",
        show = show,
        onDismiss = { show = false },
        onDismissFinished = {},
        dropdownColors = DropdownDefaults.dialogDropdownColors()
    )
}
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

Scaffold {
    OverlaySpinnerPreference(
        title = "Grouped Selector",
        entries = entries,
        collapseOnSelection = false
    )
}
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

Scaffold {
    OverlaySpinnerPreference(
        title = "Multi Select Selector",
        entries = entries,
        collapseOnSelection = false
    )
}
```

## Observe Expanded State

```kotlin
var selectedIndex by remember { mutableStateOf(0) }
var expanded by remember { mutableStateOf(false) }
val options = listOf(
    DropdownItem(text = "Option 1"),
    DropdownItem(text = "Option 2"),
    DropdownItem(text = "Option 3"),
)

Scaffold {
    OverlaySpinnerPreference(
        title = "Dropdown Selector",
        summary = if (expanded) "Expanded" else "Collapsed",
        items = options,
        selectedIndex = selectedIndex,
        onExpandedChange = { expanded = it },
        onSelectedIndexChange = { selectedIndex = it }
    )
}
```

## Interaction

The row follows COUI 17 `COUIMenuPreference` / `COUIClickSelectMenu`:

- **Opens at the finger**: the touch-down point is recorded (`PreciseClickHelper`) and the menu is placed at that point rather than at the edge of the row. An accessibility click, which has no touch point, falls back to the row.
- **Soft keyboard**: if the keyboard is showing, a tap hides it first and the menu opens 100ms later (240ms on windows at least 840dp tall), so it is placed against the final window.
- **Anchor row**: while the menu is showing, the row keeps its hover layer locked (`COUIPopupListWindow.setAnchorHoveredState`); the layer is released as soon as the menu starts to leave.
- **Arrow**: the up-down arrow is `couiColorLabelTertiary`, and `couiColorLabelQuaternary` while the row is disabled.
- **Selected value**: shown as the COUI assignment text (`TextAppearance.COUI.List.Assignment.End`): 13sp with the body_s line spacing, end aligned, at most two lines, 4dp before the arrow.
- **Haptics and dialog mode**: opening the menu plays no haptic, as in COUI. In dialog mode the row ends in the `coui_btn_next` jump arrow (the same tertiary / quaternary colors), plays its normal press and release (a COUI list dialog locks no anchor state), and picking an option plays no haptic either.

## Properties

### OverlaySpinnerPreference Properties (Dropdown Mode)

| Property Name         | Type                      | Description                     | Default Value                         | Required |
| --------------------- | ------------------------- | ------------------------------- | ------------------------------------- | -------- |
| items                 | List\<DropdownItem>       | Options list                    | -                                     | Yes      |
| selectedIndex         | Int                       | Current selected item index     | -                                     | Yes      |
| title                 | String                    | Selector title                  | -                                     | Yes      |
| modifier              | Modifier                  | Component modifier              | Modifier                              | No       |
| titleColor            | BasicComponentColors      | Title text color config         | BasicComponentDefaults.titleColor()   | No       |
| summary               | String?                   | Selector description            | null                                  | No       |
| summaryColor          | BasicComponentColors      | Summary text color config       | BasicComponentDefaults.summaryColor() | No       |
| spinnerColors         | DropdownColors            | Color configuration for spinner | DropdownDefaults.dropdownColors()     | No       |
| startAction           | @Composable (() -> Unit)? | Custom start side content       | null                                  | No       |
| bottomAction          | @Composable (() -> Unit)? | Custom bottom side content      | null                                  | No       |
| insideMargin          | PaddingValues             | Internal content padding        | BasicComponentDefaults.InsideMargin   | No       |
| cardListPosition | CardListPosition | Row position inside its card group ([CardListGroup](./card.md#cardlistgroup)); rounded outer edges gain extra padding | LocalCardListPosition.current (None outside a CardListGroup) | No |
| maxHeight             | Dp?                       | Maximum dropdown height         | null                                  | No       |
| enabled               | Boolean                   | Interactive state               | true                                  | No       |
| showValue             | Boolean                   | Show current selected value     | true                                  | No       |
| renderInRootScaffold  | Boolean                   | Whether to render the popup in the root (outermost) Scaffold. When true, the popup covers the full screen. When false, it renders within the current Scaffold's bounds with position compensation | true | No |
| onExpandedChange      | ((Boolean) -> Unit)?      | Callback when expanded state changes | null                               | No       |
| onSelectedIndexChange | ((Int) -> Unit)?          | Selection change callback       | null                                  | No       |

### Entry Overload Properties

| Property Name       | Type          | Description                                | Default Value | Required |
| ------------------- | ------------- | ------------------------------------------ | ------------- | -------- |
| entry               | DropdownEntry | Single dropdown entry group                | -             | Yes      |
| collapseOnSelection | Boolean       | Whether to close the popup after selection | true          | No       |

### Grouped Entries Overload Properties

| Property Name        | Type                 | Description                                      | Default Value     | Required |
| -------------------- | -------------------- | ------------------------------------------------ | ----------------- | -------- |
| entries              | List\<DropdownEntry> | Dropdown entry groups separated by dividers      | -                 | Yes      |
| collapseOnSelection  | Boolean              | Whether to close the popup after each selection  | entries.size <= 1 | No       |
| renderInRootScaffold | Boolean              | Whether to render the popup in the root Scaffold | true              | No       |

### OverlaySpinnerPreference Properties (Dialog Mode)

| Property Name         | Type                      | Description                          | Default Value                           | Required |
| --------------------- | ------------------------- | ------------------------------------ | --------------------------------------- | -------- |
| items                 | List\<DropdownItem>       | Options list                         | -                                       | Yes      |
| selectedIndex         | Int                       | Current selected item index          | -                                       | Yes      |
| title                 | String                    | Selector title                       | -                                       | Yes      |
| dialogButtonString    | String                    | Dialog bottom button text            | -                                       | Yes      |
| modifier              | Modifier                  | Component modifier                   | Modifier                                | No       |
| popupModifier         | Modifier                  | Dialog popup modifier                | Modifier                                | No       |
| titleColor            | BasicComponentColors      | Title text color config              | BasicComponentDefaults.titleColor()     | No       |
| summary               | String?                   | Selector description                 | null                                    | No       |
| summaryColor          | BasicComponentColors      | Summary text color config            | BasicComponentDefaults.summaryColor()   | No       |
| spinnerColors         | DropdownColors            | Colors of the dialog rows: label, summary, disabled row, checked radio, group header      | DropdownDefaults.dialogDropdownColors() | No       |
| startAction           | @Composable (() -> Unit)? | Custom start side content            | null                                    | No       |
| bottomAction          | @Composable (() -> Unit)? | Custom bottom side content           | null                                    | No       |
| insideMargin          | PaddingValues             | Internal content padding             | BasicComponentDefaults.InsideMargin     | No       |
| cardListPosition | CardListPosition | Row position inside its card group ([CardListGroup](./card.md#cardlistgroup)); rounded outer edges gain extra padding | LocalCardListPosition.current (None outside a CardListGroup) | No |
| enabled               | Boolean                   | Interactive state                    | true                                    | No       |
| showValue             | Boolean                   | Show current selected value          | true                                    | No       |
| renderInRootScaffold  | Boolean                   | Whether to render the dialog in the root (outermost) Scaffold. When true, the dialog covers the full screen. When false, it renders within the current Scaffold's bounds | true | No |
| onExpandedChange      | ((Boolean) -> Unit)?      | Callback when expanded state changes | null                               | No       |
| onSelectedIndexChange | ((Int) -> Unit)?          | Selection change callback            | null                                    | No       |

### Dialog Entry Overload Properties

| Property Name      | Type          | Description                 | Default Value | Required |
| ------------------ | ------------- | --------------------------- | ------------- | -------- |
| entry              | DropdownEntry | Single dropdown entry group | -             | Yes      |
| dialogButtonString | String        | Dialog bottom button text   | -             | Yes      |
| collapseOnSelection | Boolean      | Whether to close the dialog after selection | true | No       |

### Dialog Grouped Entries Overload Properties

| Property Name        | Type                 | Description                                       | Default Value     | Required |
| -------------------- | -------------------- | ------------------------------------------------- | ----------------- | -------- |
| entries              | List\<DropdownEntry> | Dropdown entry groups separated by dividers       | -                 | Yes      |
| dialogButtonString   | String               | Dialog bottom button text                         | -                 | Yes      |
| collapseOnSelection  | Boolean              | Whether to close the dialog after each selection  | entries.size <= 1 | No       |
| renderInRootScaffold | Boolean              | Whether to render the dialog in the root Scaffold | true              | No       |

### OverlayDropdownDialog Properties

`OverlayDropdownDialog` has an `entry: DropdownEntry` overload (where `collapseOnSelection` defaults to `true`) and an `entries: List<DropdownEntry>` overload:

| Property Name        | Type                 | Description                                                    | Default Value     | Required |
| -------------------- | -------------------- | -------------------------------------------------------------- | ----------------- | -------- |
| entries              | List\<DropdownEntry> | Option groups shown as radio rows; a group title adds a header row  | -                 | Yes      |
| title                | String               | Dialog title                                                   | -                 | Yes      |
| dialogButtonString   | String               | Text of the bottom button, which dismisses the dialog          | -                 | Yes      |
| show                 | Boolean              | Whether the dialog is shown                                    | -                 | Yes      |
| onDismiss            | () -> Unit           | Called on the button, an outside tap, back, or a collapsing selection | -          | Yes      |
| onDismissFinished    | () -> Unit           | Called after the exit animation finishes                       | -                 | Yes      |
| dropdownColors       | DropdownColors       | Colors of the radio rows: label, summary, disabled row, checked radio, group header                                      | -                 | Yes      |
| popupModifier        | Modifier             | Modifier applied to the dialog                                 | Modifier          | No       |
| renderInRootScaffold | Boolean              | Whether to render the dialog in the root Scaffold              | true              | No       |
| collapseOnSelection  | Boolean              | Whether to close the dialog after an item is clicked           | entries.size <= 1 | No       |

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
