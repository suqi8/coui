---
title: WindowCascadingListPopup
requiresScaffoldHost: false
prerequisites:
  - Can be used anywhere, does not require `Scaffold` or `COUIPopupHost`
  - Renders at window level
hostComponent: None
popupHost: None
---

# WindowCascadingListPopup

`WindowCascadingListPopup` is a popup list with two-level cascading menu support, rendered at the window level via `Dialog`. Unlike `OverlayCascadingListPopup`, it does not require a `Scaffold` or `COUIPopupHost`. Items whose `DropdownItem.children` is non-empty become submenu triggers; cascading depth is limited to **2**.

<div style="position: relative; height: 410px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=windowCascadingListPopup" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

::: tip
This component does not depend on `Scaffold` and can be used in any composable scope.
:::

## Import

```kotlin
import io.github.suqi8.coui.kmp.window.WindowCascadingListPopup
import io.github.suqi8.coui.kmp.basic.DropdownEntry
import io.github.suqi8.coui.kmp.basic.DropdownItem
```

## Basic Usage

`WindowCascadingListPopup` is used the same way as `OverlayCascadingListPopup` but does not need a `Scaffold` host. The example below shows two top-level items: a flat sort group, and a "View mode" trigger that expands a secondary list.

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

Box {
    TextButton(
        text = "Click to show menu",
        onClick = { showPopup = true },
    )
    WindowCascadingListPopup(
        show = showPopup,
        entries = entries,
        onDismissRequest = { showPopup = false },
    )
}
```

::: tip Cascading Depth
Cascading depth is capped at 2. Items at the secondary level cannot have their own `children`; deeper trees are silently ignored.
:::

## Sub Menu Behavior

The sub menu follows COUI 17. On a compact window (narrower than 600dp) it covers the main menu: it is as wide as the main menu and starts with a copy of the parent row (medium title, tinted indicator) sitting on that row, then slides up to 14dp above it while its outline grows from the row; the main menu shrinks to 0.9, moves up to keep a 12dp strip visible and dims its rows to 30%. On wider windows the main menu shrinks to 0.9 with its rows at 70% while the sub menu scales in beside it, overlapping 14dp. Tapping the main menu, the copied header, outside the popup, or going back collapses the sub menu; dragging across the main menu collapses it after 500ms; holding a sub-menu row for 650ms opens it and the same finger can then slide into the sub menu and lift over a row to pick it; if the finger stays on the main menu instead, the drag keeps selecting there (a deliberate deviation: COUI ignores the main menu once a sub menu opens), and lifting on the long-pressed row itself picks nothing; on a compact window the sub menu's shadow fades in with it; at the top level, back and outside taps dismiss the popup via `onDismissRequest`. Predictive back previews the collapse or dismissal and restores the popup when cancelled.

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

## Properties

### WindowCascadingListPopup

| Property Name         | Type                        | Description                                                                                  | Default Value                              |
| --------------------- | --------------------------- | -------------------------------------------------------------------------------------------- | ------------------------------------------ |
| show                  | Boolean                     | Whether to show the popup.                                                                   | -                                          |
| entries               | List\<DropdownEntry>        | Grouped dropdown entries; top-level items with non-empty `children` become submenu triggers. | -                                          |
| onDismissRequest      | () -> Unit                  | Called when the user requests dismissal (e.g., clicking outside, tapping the back button).   | -                                          |
| popupModifier         | Modifier                    | Modifier applied to the popup body.                                                          | Modifier                                   |
| onDismissFinished     | (() -> Unit)?               | Called after the exit animation finishes.                                                    | null                                       |
| popupPositionProvider | PopupPositionProvider       | Position strategy for the primary popup relative to its anchor.                              | ListPopupDefaults.DropdownPositionProvider |
| alignment             | PopupPositionProvider.Align | Alignment of the primary popup.                                                              | PopupPositionProvider.Align.End            |
| enableWindowDim       | Boolean                     | Whether to dim the rest of the window while the popup is shown.                              | false                                       |
| maxHeight             | Dp?                         | Maximum height of either side. Null bounds it by the safe area.                              | null                                       |
| minWidth              | Dp                          | Minimum width of the popup.                                                                  | ListPopupDefaults.MinWidth                 |
| dropdownColors        | DropdownColors              | Colors used by every row.                                                                    | DropdownDefaults.dropdownColors()          |
| collapseOnSelection   | Boolean                     | When true, selecting any leaf dismisses the popup.                                           | true                                       |
| backgroundModifier    | Modifier                    | Applied inside the popup surface under the rows, on top of the default material blur. | Modifier |
| backgroundColor       | Color                       | Surface fill when the popup is not blurred (couiColorBackgroundTop, COUI's non-blur background). | ListPopupDefaults.backgroundColor() |
| materialBlur          | Boolean                     | Blurs what lies behind the popup with the COUI 17 popup material from `LocalPopupMaterialBlur`; false keeps the opaque `backgroundColor`. | true |
| materialEffect        | Boolean                     | On a blurred popup, draws the shadow-edge stroke and the touch spotlight. | true |

### DropdownEntry

| Property Name | Type                | Description                                                                                            | Default Value | Required |
| ------------- | ------------------- | ------------------------------------------------------------------------------------------------------ | ------------- | -------- |
| items         | List\<DropdownItem> | Items shown in this dropdown group                                                                     | -             | Yes      |
| enabled       | Boolean             | Whether this group is enabled. False disables all items; true still respects each item's enabled state | true          | No       |
| title         | String?             | Optional non-clickable group header rendered above the items (12sp medium, secondary label, max 2 lines) | null          | No       |
| horizontal    | Boolean             | Render the group as the COUI 17 row of horizontal items (up to 5 icons, or 3 icons with labels); popup mode only, always followed by a divider | false | No |

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
