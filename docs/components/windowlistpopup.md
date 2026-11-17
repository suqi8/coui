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
import io.github.suqi8.coui.kmp.basic.DropdownGroupDivider
import io.github.suqi8.coui.kmp.basic.DropdownHeaderItem
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
- **Horizontal items**: `DropdownHorizontalItems` (or `DropdownEntry(horizontal = true)`) puts a row of up to five icons, or three icons with labels, at the top of the list. Inside a popup list the cells share the sliding mask (17dp / 23dp corners) and the drag selection with the rows.
- **Press feedback**: one rounded mask (capsule on a single-line row) slides between rows; dragging moves it with a haptic tick per row and selects the row under the finger on release. Holding a sub-menu row for 650ms opens it.
- **Disabled rows (deviation from COUI)**: while dragging, the pressed mask keeps sliding onto a disabled row and on to the next row on the same spring, at full press. COUI 17 fades the mask out over a disabled row and makes it reappear on the next enabled row instead. A disabled row still plays no haptic and releasing over it selects nothing.
- **Placement**: centred on the anchor (or the touched point with `preciseClickState`), 8dp below it, above it when there is no room, otherwise at the top of the available area; it keeps 16dp from the status bar, 32dp from the navigation bar or keyboard and the grid margin (16 / 24 / 40dp) from the sides.
- **Motion**: the popup grows from 0 about its centre while its centre travels a slightly curved path out of the anchor, and fades in; dismissal runs the same path back. The curve uses COUI's raw pixel constants on Android and scales them by density / 3.5 elsewhere.
- **Shadow**: the OPlus `SHADOW_LV4` light-source shadow, drawn as a spot layer that shifts with the popup's distance below the light plus a faint ambient layer.
- **No dimming** by default: COUI popups leave the content behind them untouched.

## Blurred Background

Like ColorOS 17, the popup is blurred by default: it draws the COUI 17 popup material
(`ListPopupDefaults.materialBlurSpec`: 160px blur, glow-overlay blend, mix `#99CDCDCD` /
`#99262626`, blend `#BFEEEEEE` / `#99262626`) with the shadow-edge stroke and the touch spotlight.
coui-ui has no backdrop capture of its own, so the blur comes from the `LocalPopupMaterialBlur`
provider in scope where the popup is called. Provide it once around your scaffold, recording the
page the menus open over:

```kotlin
val backdrop = rememberLayerBackdrop()
val popupBlur = remember(backdrop) {
    MaterialBlurProvider { spec, shape ->
        Modifier.couiMaterialBlur(backdrop, spec.toMaterialBlurStyle(), shape) // coui-blur
    }
}
Box(Modifier.layerBackdrop(backdrop)) {
    CompositionLocalProvider(LocalPopupMaterialBlur provides popupBlur) {
        Page() // every menu opened from here is blurred
    }
}
```

`toMaterialBlurStyle()` maps `MaterialBlurSpec` field by field onto coui-blur's `MaterialBlurStyle`
(see `utils/PanelBlur.kt` in the example app). Without a provider, or where runtime shaders are
missing, the popup keeps the opaque `backgroundColor`, COUI's non-blur background. Dialogs and
bottom sheets turn the provider off for their content. Pass `materialBlur = false` to opt one popup
out. The window variant draws the same page recording into its own window; on Android it does not use the platform blur-behind, which blurs the whole screen behind a window rather than the menu's rounded rect.

The shadow-edge stroke and the touch spotlight (`materialEffect`) are drawn on the opaque surface
as well, with the same parameters. COUI only draws them once the popup is blurred; this library keeps
them so a menu that cannot blur still lights up. Both are white light laid source-over onto the
surface, which is what a ColorOS 17 popup shows: its OVERLAY spotlight lands on the panel's own
transparent canvas above the window blur. They show on blurred and dark surfaces and disappear into
a pure white light-scheme fill. The spotlight glows under the finger, follows it across the rows
(drag-to-select included) and spreads out as it fades when the finger lifts.

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
| backgroundModifier    | Modifier                    | Applied inside the popup surface under the rows, on top of the default material blur. | Modifier |
| backgroundColor       | Color                       | Surface fill when the popup is not blurred (couiColorBackgroundTop, COUI's non-blur background). | ListPopupDefaults.backgroundColor() |
| materialBlur          | Boolean                     | Blurs what lies behind the popup with the COUI 17 popup material from `LocalPopupMaterialBlur`; false keeps the opaque `backgroundColor`. | `LocalCOUIMaterialEffectEnabled.current` |
| materialEffect        | Boolean                     | Draws the shadow-edge stroke and the touch spotlight, blurred or not. | `LocalCOUIMaterialEffectEnabled.current` |
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

`DropdownImpl` also provides an overload that takes a full `DropdownItem` instead of `text`, enabling rows with an icon or summary. It additionally accepts `hasSubmenu` (renders the up/down indicator) and `submenuExpanded` (tints the indicator while its sub menu is open); `enabled` defaults to `item.enabled`. Both overloads take `reserveIconSlot`: COUI 17 keeps the 20dp + 12dp icon slot on every row as soon as any item of the list has an icon, so pass `items.any { it.icon != null }` to every row you build yourself. `DropdownItem.stateIcon` draws a custom icon in the 24dp trailing state slot (`PopupListItem.setStateIcon`), tinted like the selection check.

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

### DropdownGroupDivider and DropdownHeaderItem

When you lay out grouped rows yourself inside `ListPopupColumn`, `DropdownGroupDivider()` draws the COUI 17 group separator (a 12dp band with a centred 0.5dp line inset 20dp at both sides) and `DropdownHeaderItem(text = …)` the non-clickable group title row, exactly as the dropdown menus do between `DropdownEntry` groups:

```kotlin
ListPopupColumn {
    DropdownHeaderItem(text = "Sort by")
    // DropdownImpl rows of the first group
    DropdownGroupDivider()
    // DropdownImpl rows of the second group
}
```

Both take an optional `modifier` and `color`; see [OverlayListPopup](./overlaylistpopup#dropdowngroupdivider-and-dropdownheaderitem) for the full table. The low-level surface both list popups draw in, `ListPopupContent`, is described there as well.

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
