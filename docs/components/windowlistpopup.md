---
title: WindowListPopup
requiresScaffoldHost: false
prerequisites:
  - Can be used anywhere, does not require `Scaffold` or `COUIPopupHost`
  - Renders at window level
hostComponent: None
popupHost: None
---

# WindowListPopup

`WindowListPopup` is a popup list component that renders at the window level using `Dialog`. Unlike `OverlayListPopup`, it does not require a `Scaffold` or `COUIPopupHost`.

<div style="position: relative; height: 250px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=windowListPopup" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

::: tip
This component does not depend on `Scaffold` and can be used in any composable scope.
:::

## Import

```kotlin
import io.github.suqi8.coui.kmp.window.WindowListPopup
import io.github.suqi8.coui.kmp.basic.ListPopupColumn
```

## Basic Usage

The WindowListPopup component can be used to create dropdown menus without `Scaffold`:

```kotlin
var showPopup by remember { mutableStateOf(false) }
var selectedIndex by remember { mutableStateOf(0) }
val items = listOf("Option 1", "Option 2", "Option 3")

Box {
    TextButton(
        text = "Click to show menu",
        onClick = { showPopup = true }
    )
    WindowListPopup(
        show = showPopup,
        alignment = PopupPositionProvider.Align.Start,
        onDismissRequest = { showPopup = false }
    ) {
        val dismiss = LocalDismissState.current
        ListPopupColumn {
            items.forEachIndexed { index, string ->
                DropdownImpl(
                    text = string,
                    optionSize = items.size,
                    isSelected = selectedIndex == index,
                    index = index,
                    onSelectedIndexChange = {
                        selectedIndex = index
                        dismiss?.invoke()
                    }
                )
            }
        }
    }
}
```

## COUI 17 Behavior

The popup follows COUI 17 `COUIPopupListWindow`:

- **Size**: rows are at least 40dp tall with 8dp vertical and 20dp side padding, and the list is inset by 8dp at the top and bottom. The popup is as wide as its widest row, clamped to 168–256dp (196dp minimum when a row opens a sub menu), and its height ends on a whole row.
- **Rows**: titles stay `couiColorLabelPrimary` when selected; a selected row shows a check and a sub-menu row the up/down indicator. There is no line between rows; groups are separated by a 12dp band with a 0.5dp divider, from three items on.
- **Press feedback**: one rounded mask (capsule on a single-line row) slides between rows; dragging moves it with a haptic tick per row and selects the row under the finger on release. Holding a sub-menu row for 650ms opens it.
- **Placement**: centred on the anchor (or the touched point with `preciseClickState`), 8dp below it, above it when there is no room, otherwise at the top of the available area; it keeps 16dp from the status bar, 32dp from the navigation bar or keyboard and the grid margin (16 / 24 / 40dp) from the sides.
- **Motion**: the popup grows from 0 about its centre while its centre travels a slightly curved path out of the anchor, and fades in; dismissal runs the same path back.
- **No dimming** by default: COUI popups leave the content behind them untouched.

## Blurred Background

COUI leaves popup blur off unless an app asks for it. Pass the blur through `backgroundModifier` together with a transparent `backgroundColor`, and turn on `materialEffect` for the stroke and spotlight:

```kotlin
val isDark = COUITheme.colorScheme.background.luminance() < 0.5f
OverlayListPopup(
    show = showPopup,
    onDismissRequest = { showPopup = false },
    backgroundModifier = Modifier.couiMaterialBlur(backdrop, MaterialBlurDefaults.popup(isDark)),
    backgroundColor = Color.Transparent,
    materialEffect = true,
) {
    ListPopupColumn { /* rows */ }
}
```

## Properties

### WindowListPopup

| Property Name         | Type                        | Description                                                      | Default Value                              |
| --------------------- | --------------------------- | ---------------------------------------------------------------- | ------------------------------------------ |
| show                  | Boolean                     | Whether to show the popup.                                       | -                                          |
| popupModifier         | Modifier                    | Modifier applied to the popup container.                         | Modifier                                   |
| popupPositionProvider | PopupPositionProvider       | Provides position calculation logic for the popup.               | ListPopupDefaults.DropdownPositionProvider |
| alignment             | PopupPositionProvider.Align | Specifies the alignment of the popup relative to the anchor.     | PopupPositionProvider.Align.Start          |
| enableWindowDim       | Boolean                     | Whether to dim the background when popup is shown.               | false                                       |
| onDismissRequest      | (() -> Unit)?               | Called when the user requests dismissal (e.g., clicking outside) | null                                       |
| onDismissFinished     | (() -> Unit)?               | Invoked after the hide animation completes; not invoked if the hide is cancelled mid-flight (e.g., `show` toggled back to true) | null         |
| maxHeight             | Dp?                         | Maximum height of the popup content.                             | null                                       |
| minWidth              | Dp                          | Minimum width of the popup content.                              | ListPopupDefaults.MinWidth                 |
| backgroundModifier    | Modifier                    | Applied inside the popup surface under the rows; the slot for the COUI 17 blurred background, e.g. coui-blur `Modifier.couiMaterialBlur(backdrop, MaterialBlurDefaults.popup(isDark))`. | Modifier |
| backgroundColor       | Color                       | Popup surface fill (couiColorBackgroundTop). Pass a transparent color with a blurring `backgroundModifier`. | ListPopupDefaults.backgroundColor() |
| materialEffect        | Boolean                     | Draws the material effects of a blurred COUI 17 popup: the shadow-edge stroke and the touch spotlight. | false |
| content               | @Composable () -> Unit      | The content to display inside the popup.                         | -                                          |

### ListPopupColumn

| Property Name | Type                   | Description                                    | Default Value |
| ------------- | ---------------------- | ---------------------------------------------- | ------------- |
| content       | @Composable () -> Unit | The list content to display inside the column. | -             |

### DropdownImpl

`DropdownImpl` can be used as a standard row inside `ListPopupColumn`. Set `enabled = false` to disable a row; disabled rows are not clickable and use the disabled text color.

```kotlin
DropdownImpl(
    text = "Disabled option",
    optionSize = items.size,
    isSelected = false,
    index = 1,
    enabled = false,
    onSelectedIndexChange = {}
)
```

| Property Name         | Type           | Description                          | Default Value                     |
| --------------------- | -------------- | ------------------------------------ | --------------------------------- |
| text                  | String         | Text shown for the option            | -                                 |
| optionSize            | Int            | Total number of options              | -                                 |
| isSelected            | Boolean        | Whether this option is selected      | -                                 |
| index                 | Int            | Index of this option                 | -                                 |
| dropdownColors        | DropdownColors | Color configuration for the option   | DropdownDefaults.dropdownColors() |
| enabled               | Boolean        | Whether this option can be clicked   | true                              |
| dialogMode            | Boolean        | Use dialog-style row metrics instead of popup metrics | false            |
| onSelectedIndexChange | (Int) -> Unit  | Callback when this option is clicked | -                                 |

`DropdownImpl` also provides an overload that takes a full `DropdownItem` instead of `text`, enabling rows with an icon or summary. It additionally accepts `hasSubmenu` (renders the up/down indicator), `submenuExpanded` (tints the indicator while its sub menu is open), and `isFirst` / `isLast`, kept for compatibility and ignored in popup mode by COUI 17; `enabled` defaults to `item.enabled`.

```kotlin
DropdownImpl(
    item = DropdownItem(
        text = "Copy",
        summary = "Copy to clipboard",
    ),
    optionSize = items.size,
    isSelected = false,
    index = 0,
    onSelectedIndexChange = { /* handle click */ }
)
```

### PopupPositionProvider.Align

| Value       | Description                                         |
| ----------- | --------------------------------------------------- |
| Start       | Aligns the popup to the start of the anchor.        |
| End         | Aligns the popup to the end of the anchor.          |
| TopStart    | Aligns the popup to the top-start of the anchor.    |
| TopEnd      | Aligns the popup to the top-end of the anchor.      |
| BottomStart | Aligns the popup to the bottom-start of the anchor. |
| BottomEnd   | Aligns the popup to the bottom-end of the anchor.   |

### LocalDismissState

Provides a `(() -> Unit)?` function to dismiss the current popup from within its content. This is a unified dismiss state provided by all overlay components.

```kotlin
val dismiss = LocalDismissState.current
TextButton(
    text = "Close",
    onClick = { dismiss?.invoke() }
)
```
