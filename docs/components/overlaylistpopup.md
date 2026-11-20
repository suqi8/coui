---
title: OverlayListPopup
requiresScaffoldHost: true
prerequisites:
  - Must be used within `Scaffold` to provide `COUIPopupHost`
  - Using outside `Scaffold` will cause popup content not to render
  - Multiple nested or side-by-side `Scaffold`s are supported without extra configuration
hostComponent: Scaffold
popupHost: COUIPopupHost
---

# OverlayListPopup

`OverlayListPopup` is a popup list component in COUI used to display a popup menu with multiple options. It provides a lightweight, floating temporary list suitable for various dropdown menus, context menus, and similar scenarios.

<div style="position: relative; height: 250px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=overlayListPopup" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

::: danger Prerequisite
This component depends on `Scaffold` providing `COUIPopupHost` to render popup content. It must be used within `Scaffold`, otherwise popup content will not render correctly.
:::

## Import

```kotlin
import io.github.suqi8.coui.kmp.overlay.OverlayListPopup
import io.github.suqi8.coui.kmp.basic.ListPopupColumn
import io.github.suqi8.coui.kmp.basic.DropdownGroupDivider
import io.github.suqi8.coui.kmp.basic.DropdownHeaderItem
import io.github.suqi8.coui.kmp.basic.ListPopupDefaults
import io.github.suqi8.coui.kmp.basic.DropdownImpl
import io.github.suqi8.coui.kmp.basic.PopupPositionProvider
```

## Basic Usage

The OverlayListPopup component can be used to create simple dropdown menus:

```kotlin
var showPopup by remember { mutableStateOf(false) }
var selectedIndex by remember { mutableStateOf(0) }
val items = listOf("Option 1", "Option 2", "Option 3")

Scaffold {
    Box {
        TextButton(
            text = "Click to show menu",
            onClick = { showPopup = true }
        )
        OverlayListPopup(
            show = showPopup,
            alignment = PopupPositionProvider.Align.Start,
            onDismissRequest = { showPopup = false } // Close the popup menu
        ) {
            ListPopupColumn {
                items.forEachIndexed { index, string ->
                    DropdownImpl(
                        text = string,
                        optionSize = items.size,
                        isSelected = selectedIndex == index,
                        index = index,
                        onSelectedIndexChange = {
                            selectedIndex = index
                            showPopup = false // Close the popup menu
                        }
                    )
                }
            }
        }
    }
}
```

## Component States

### Different Alignments

OverlayListPopup can be set with different alignment options:

```kotlin
var showPopup by remember { mutableStateOf(false) }

OverlayListPopup(
    show = showPopup,
    onDismissRequest = { showPopup = false }, // Close the popup menu
    alignment = PopupPositionProvider.Align.Start
) {
    ListPopupColumn {
        // Custom content
    }
}
```

### Window Dimming

```kotlin
var showPopup by remember { mutableStateOf(false) }

OverlayListPopup(
    show = showPopup,
    onDismissRequest = { showPopup = false }, // Close the popup menu
    enableWindowDim = true // COUI popups do not dim; opt in here
) {
    ListPopupColumn {
        // Custom content
    }
}
```

### Context Menu Positioning

Besides the default dropdown provider, `ListPopupDefaults.ContextMenuPositionProvider` anchors the popup to a corner of the anchor, combined with the corner alignments (`TopStart` / `TopEnd` / `BottomStart` / `BottomEnd`):

```kotlin
var showPopup by remember { mutableStateOf(false) }

OverlayListPopup(
    show = showPopup,
    popupPositionProvider = ListPopupDefaults.ContextMenuPositionProvider,
    alignment = PopupPositionProvider.Align.TopEnd,
    onDismissRequest = { showPopup = false }
) {
    ListPopupColumn {
        // Custom content
    }
}
```

You can also build a dropdown provider with custom margins via `ListPopupDefaults.dropdownPositionProvider(verticalMargin, horizontalMargin)`.

## COUI 17 Behavior

The popup follows COUI 17 `COUIPopupListWindow`:

- **Size**: rows are at least 40dp tall with 8dp vertical and 20dp side padding, and the list is inset by 8dp at the top and bottom. The popup is as wide as its widest row, clamped to 168–256dp (196dp minimum when a row opens a sub menu), and its height ends on a whole row.
- **Rows**: titles stay `couiColorLabelPrimary` when selected; a selected row shows a check and a sub-menu row the up/down indicator. There is no line between rows; groups are separated by a 12dp band with a 0.5dp divider, from three items on.
- **Horizontal items**: `DropdownHorizontalItems` (or `DropdownEntry(horizontal = true)`) puts a row of up to five icons, or three icons with labels, at the top of the list. Inside a popup list the cells share the sliding mask (17dp / 23dp corners) and the drag selection with the rows. When one label wraps, every label keeps two lines, so the labelled cells share one height.
- **Press feedback**: one rounded mask (capsule on a single-line row) slides between rows; dragging moves it with a haptic tick per row and selects the row under the finger on release. Holding a sub-menu row for 650ms opens it.
- **Disabled rows (deviation from COUI)**: while dragging, the pressed mask keeps sliding onto a disabled row and on to the next row on the same spring, at full press. COUI 17 fades the mask out over a disabled row and makes it reappear on the next enabled row instead. A disabled row still plays no haptic and releasing over it selects nothing.
- **Placement**: centred on the anchor (or the touched point with `preciseClickState`), 8dp below it, above it when there is no room, otherwise at the top of the available area; it keeps 16dp from the status bar, 32dp from the navigation bar or keyboard and the grid margin (16 / 24 / 40dp) from the sides.
- **Motion**: the popup grows from 0 about its centre while its centre travels a slightly curved path out of the anchor, and fades in; dismissal runs the same path back. The curve uses COUI's raw pixel constants on Android and scales them by density / 3.5 elsewhere.
- **Touch motion**: the whole menu follows a finger on it (`COUIPopupTouchMotionAnimator`): it stretches along the drag and narrows across it (a few percent, less for larger menus), anchored on the edge that faces the anchor, and moves with the finger by up to 28dp on a rubber band. This lasts for the whole gesture, while rows are drag-selected, while a long list scrolls and while the finger is outside the menu, and the menu springs back with a slight bounce when the finger lifts. A list that fits does not overscroll its rows; only a list that scrolls does.
- **Dismissal**: once the popup starts to close it stops taking touches outside its panel (`COUIPopupListWindow.dismiss` makes the window untouchable first), so taps and scrolls reach the page while the menu animates out.
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
out.

The shadow-edge stroke and the touch spotlight (`materialEffect`) are drawn on the opaque surface
as well, with the same parameters. COUI only draws them once the popup is blurred; this library keeps
them so a menu that cannot blur still lights up. Both are white light laid source-over onto the
surface, which is what a ColorOS 17 popup shows: its OVERLAY spotlight lands on the panel's own
transparent canvas above the window blur. They show on blurred and dark surfaces and disappear into
a pure white light-scheme fill. The spotlight glows under the finger, follows it across the rows
(drag-to-select included) and spreads out as it fades when the finger lifts.

## Properties

### OverlayListPopup

| Property Name         | Type                        | Description                                                       | Default Value                              |
| --------------------- | --------------------------- | ----------------------------------------------------------------- | ------------------------------------------ |
| show                  | Boolean                     | Whether to show the popup.                                        | -                                          |
| popupModifier         | Modifier                    | Modifier applied to the popup container.                          | Modifier                                   |
| popupPositionProvider | PopupPositionProvider       | Provides position calculation logic for the popup.                | ListPopupDefaults.DropdownPositionProvider |
| alignment             | PopupPositionProvider.Align | Specifies the alignment of the popup relative to the anchor.      | PopupPositionProvider.Align.Start          |
| enableWindowDim       | Boolean                     | Whether to dim the background when popup is shown.                | false                                       |
| onDismissRequest      | (() -> Unit)?               | Called when the user requests dismissal (e.g., clicking outside). | null                                       |
| onDismissFinished     | (() -> Unit)?               | Invoked after the hide animation completes; not invoked if the hide is cancelled mid-flight (e.g., `show` toggled back to true) | null         |
| maxHeight             | Dp?                         | Maximum height of the popup content.                              | null                                       |
| minWidth              | Dp                          | Minimum width of the popup content.                               | ListPopupDefaults.MinWidth                 |
| renderInRootScaffold  | Boolean                     | Whether to render the popup in the root (outermost) Scaffold. When true, the popup covers the full screen. When false, it renders within the current Scaffold's bounds with position compensation. | true |
| backgroundModifier    | Modifier                    | Applied inside the popup surface under the rows, on top of the default material blur. | Modifier |
| backgroundColor       | Color                       | Surface fill when the popup is not blurred (couiColorBackgroundTop, COUI's non-blur background). | ListPopupDefaults.backgroundColor() |
| materialBlur          | Boolean                     | Blurs what lies behind the popup with the COUI 17 popup material from `LocalPopupMaterialBlur`; false keeps the opaque `backgroundColor`. | `LocalCOUIMaterialEffectEnabled.current` |
| materialEffect        | Boolean                     | Draws the shadow-edge stroke and the touch spotlight, blurred or not. | `LocalCOUIMaterialEffectEnabled.current` |
| content               | @Composable () -> Unit      | The content to display inside the popup.                          | -                                          |

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

The text-based overload:

| Property Name         | Type           | Description                                 | Default Value                     |
| --------------------- | -------------- | ------------------------------------------- | --------------------------------- |
| text                  | String         | Text shown for the option                   | -                                 |
| optionSize            | Int            | Total number of options                     | -                                 |
| isSelected            | Boolean        | Whether this option is selected             | -                                 |
| index                 | Int            | Index of this option                        | -                                 |
| dropdownColors        | DropdownColors | Color configuration for the option          | DropdownDefaults.dropdownColors() |
| enabled               | Boolean        | Whether this option can be clicked          | true                              |
| dialogMode            | Boolean        | Whether the row is shown in dialog mode     | false                             |
| reserveIconSlot       | Boolean        | Keep the 20dp + 12dp leading icon slot so the title lines up with rows that have an icon | false |
| onSelectedIndexChange | (Int) -> Unit  | Callback when this option is clicked        | -                                 |

The item-based overload accepts a `DropdownItem` (with optional `icon`, `summary` and custom trailing `stateIcon`) and exposes extra layout flags:

| Property Name         | Type           | Description                                                                                                  | Default Value                     |
| --------------------- | -------------- | ------------------------------------------------------------------------------------------------------------ | --------------------------------- |
| item                  | DropdownItem   | The item of the current option                                                                               | -                                 |
| optionSize            | Int            | Total number of options                                                                                      | -                                 |
| isSelected            | Boolean        | Whether this option is selected                                                                              | -                                 |
| index                 | Int            | Index of this option                                                                                         | -                                 |
| dropdownColors        | DropdownColors | Color configuration for the option                                                                           | DropdownDefaults.dropdownColors() |
| enabled               | Boolean        | Whether this option can be clicked                                                                           | item.enabled                      |
| dialogMode            | Boolean        | Whether the row is shown in dialog mode                                                                      | false                             |
| hasSubmenu            | Boolean        | When true, the row acts as a submenu trigger: the up/down indicator is shown instead of the selection check     | false                             |
| submenuExpanded       | Boolean        | Whether the sub menu this row opens is showing (the indicator takes the selected colour)                     | false                             |
| reserveIconSlot       | Boolean        | Keep the leading icon slot when the item has no icon                                                         | false                             |
| onSelectedIndexChange | (Int) -> Unit  | Callback when this option is clicked                                                                         | -                                 |

COUI 17 `MixedPopupListAdapter` shows the icon view on every vertical row as soon as any item of the list has an icon, so the titles line up. When you build rows yourself, pass `reserveIconSlot = items.any { it.icon != null }` to every row of the list; the dropdown menus and spinners do this for you.

`DropdownItem.stateIcon` puts a custom icon in the 24dp trailing state slot (`PopupListItem.setStateIcon`). It is shown whether or not the item is selected, replaces the selection check (a submenu trigger keeps its indicator) and is tinted like the check: `selectedIndicatorColor` while selected, `submenuIndicatorColor` (couiColorLabelTertiary) otherwise, `alertContentColor` on an alert item.

Menus never dim the content behind them by default (`enableWindowDim = false`): `COUIPopupListWindow` clears its window background and adds no dim.

### DropdownGroupDivider and DropdownHeaderItem

When you lay out grouped rows yourself inside `ListPopupColumn`, use the same pieces the dropdown menus put between and above `DropdownEntry` groups. `DropdownGroupDivider` is the COUI 17 group separator (`MixedPopupListAdapter` divider item): a 12dp band with a centred 0.5dp line inset 20dp at both sides. `DropdownHeaderItem` is the non-clickable group title row (12sp medium, secondary label, at most two lines); the sliding press mask passes over it.

```kotlin
ListPopupColumn {
    DropdownHeaderItem(text = "Sort by")
    sortOptions.forEachIndexed { index, text ->
        DropdownImpl(text = text, optionSize = sortOptions.size, isSelected = sortIndex == index, index = index, onSelectedIndexChange = { sortIndex = it })
    }
    DropdownGroupDivider()
    DropdownImpl(text = "Reset", optionSize = 1, isSelected = false, index = 0, onSelectedIndexChange = { /* Reset */ })
}
```

| Composable           | Property Name | Type     | Description             | Default Value                              |
| -------------------- | ------------- | -------- | ----------------------- | ------------------------------------------ |
| DropdownGroupDivider | modifier      | Modifier | Modifier for the band   | Modifier                                   |
| DropdownGroupDivider | color         | Color    | Color of the line       | DropdownDefaults.PopupGroupDividerColor    |
| DropdownHeaderItem   | text          | String   | Group title (required)  | -                                          |
| DropdownHeaderItem   | modifier      | Modifier | Modifier for the row    | Modifier                                   |
| DropdownHeaderItem   | color         | Color    | Title color             | DropdownDefaults.dropdownColors().headerColor |

The dropdown menus built on `DropdownEntry` only draw the divider once the menu holds at least three items, like COUI 17.

### ListPopupContent

`ListPopupContent` is the low-level popup surface that `OverlayListPopup` and `WindowListPopup` draw their content in. It applies the scale, fade and gravity-path translation driven by the progress lambdas you pass, the COUI 17 touch deformation, the popup drop shadow, the 28dp squircle surface, `backgroundModifier`, and (with `materialEffect`) the shadow-edge stroke and touch spotlight. Use it only when building your own popup host with its own animation; for menus use the list popup components above.

| Property Name            | Type                   | Description                                                            | Default Value                       |
| ------------------------ | ---------------------- | ---------------------------------------------------------------------- | ----------------------------------- |
| popupContentSize         | IntSize                | Last reported content size, to skip redundant size callbacks           | -                                   |
| onPopupContentSizeChange | (IntSize) -> Unit      | Called when the measured content size changes                          | -                                   |
| fractionProgress         | () -> Float            | Current scale of the popup (0 → 1)                                     | -                                   |
| alphaProgress            | () -> Float            | Current alpha of the popup content (0 → 1)                             | -                                   |
| popupLayoutPosition      | PopupLayoutPosition    | Where the popup spawns relative to its anchor                          | -                                   |
| localTransformOrigin     | TransformOrigin        | Scaling origin local to the content (COUI 17 scales about the centre)  | -                                   |
| modifier                 | Modifier               | Modifier for the popup container                                       | Modifier                            |
| backgroundModifier       | Modifier               | Applied inside the surface clip, over `backgroundColor`, e.g. a material blur | Modifier                     |
| translation              | () -> Offset           | Current offset from the resting place (a point on the gravity path)    | { Offset.Zero }                     |
| backgroundColor          | Color                  | Surface fill                                                           | couiColorBackgroundTop              |
| materialEffect           | Boolean                | Draw the shadow-edge stroke and the touch spotlight                    | false                               |
| touchMotionOrigin        | () -> TransformOrigin  | Point the touch deformation stretches the popup about; COUI keeps the enter pivot there (the anchor's horizontal centre on the edge facing the anchor) | Centre of the edge facing the anchor |
| content                  | @Composable () -> Unit | Popup content                                                          | -                                   |

### PopupPositionProvider.Align

| Value       | Description                                         |
| ----------- | --------------------------------------------------- |
| Start       | Aligns the popup to the start of the anchor.        |
| End         | Aligns the popup to the end of the anchor.          |
| TopStart    | Aligns the popup to the top-start of the anchor.    |
| TopEnd      | Aligns the popup to the top-end of the anchor.      |
| BottomStart | Aligns the popup to the bottom-start of the anchor. |
| BottomEnd   | Aligns the popup to the bottom-end of the anchor.   |

### ListPopupDefaults Object

The ListPopupDefaults object provides default values and position providers for the popup.

#### Constants

| Constant Name  | Type | Description                                          | Value  |
| -------------- | ---- | ---------------------------------------------------- | ------ |
| MinWidth            | Dp   | Default minimum width of the popup (coui_popup_list_window_min_width)              | 168.dp |
| MinWidthWithSubmenu | Dp   | Minimum width when a row opens a sub menu (coui_popup_list_window_width_with_icon) | 196.dp |
| MaxWidth            | Dp   | Maximum width clamp used by `ListPopupColumn`                                      | 256.dp |
| AnchorGap           | Dp   | Gap between the anchor and the popup                                               | 8.dp   |
| TopBarrier          | Dp   | Clearance kept below the status bar                                                | 16.dp  |
| BottomBarrier       | Dp   | Clearance kept above the navigation bar or keyboard                                | 32.dp  |
| HorizontalBarrier   | Dp   | Side clearance of a compact window; see `horizontalBarrier(widthDp, heightDp)`     | 16.dp  |
| MinPopupHeight | Dp   | Minimum height the popup will occupy when measured   | 50.dp  |

#### Position Providers

| Name                                                | Type                  | Description                                                                                    |
| --------------------------------------------------- | --------------------- | ---------------------------------------------------------------------------------------------- |
| DropdownPositionProvider                            | PopupPositionProvider | Anchors the popup below (or above when there is no room) the anchor, for dropdown-style menus  |
| ContextMenuPositionProvider                         | PopupPositionProvider | Anchors the popup to a corner of the anchor, for context menus                                 |
| dropdownPositionProvider(verticalMargin, horizontalMargin) | PopupPositionProvider | Factory creating a dropdown provider with custom margins (defaults: vertical 8.dp, horizontal 0.dp) |
