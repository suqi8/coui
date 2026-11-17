# NavigationPopupMenu

`NavigationPopupMenu` is a popup menu of navigation destinations anchored to a bottom navigation bar (`COUINavigationPopupMenu`), for example the destinations behind a "More" item. The menu lines up its end edge 16dp in from the anchor's end edge and opens 8dp above the bar (below it when there is room), at least 208dp wide and at most 324dp tall. Each row follows `coui_navigation_popup_item.xml`: 12dp above and below, the icon 16dp from the start, then the title 16dp from the icon and the end in 16sp bold. Like `COUINavigationPopupMenu.MenuAdapter`, no selection check is drawn (`selected` only reaches accessibility), and disabled items are greyed out. A click on an enabled row dismisses the menu and then runs the item's action.

The menu is drawn with the COUI 17 list popup surface, shared press mask and motion.

Two variants are provided:

- `OverlayNavigationPopupMenu` — rendered inside `Scaffold`'s `COUIPopupHost` (must be used within `Scaffold`).
- `WindowNavigationPopupMenu` — rendered at window level, no `Scaffold` required.

::: danger Prerequisite
`OverlayNavigationPopupMenu` depends on `Scaffold` providing `COUIPopupHost` to render popup content. Use `WindowNavigationPopupMenu` when no `Scaffold` is available.
:::

<div style="position: relative; height: 360px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=navigationPopupMenu" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.overlay.OverlayNavigationPopupMenu
// or
import io.github.suqi8.coui.kmp.window.WindowNavigationPopupMenu

import io.github.suqi8.coui.kmp.layout.NavigationPopupMenuDefaults
import io.github.suqi8.coui.kmp.basic.DropdownItem
```

## Basic Usage

Call the menu inside the layout that wraps the navigation bar: that layout is the anchor.

```kotlin
var selected by remember { mutableIntStateOf(0) }
var showMenu by remember { mutableStateOf(false) }

Scaffold(
    bottomBar = {
        Box {
            NavigationBar {
                NavigationBarItem(selected = selected == 0, onClick = { selected = 0 }, icon = COUIIcons.Home, label = "Home")
                NavigationBarItem(selected = selected >= 1, onClick = { showMenu = true }, icon = COUIIcons.More, label = "More")
            }
            OverlayNavigationPopupMenu(
                show = showMenu,
                items = listOf(
                    DropdownItem(text = "Music", selected = selected == 1, onClick = { selected = 1 }),
                    DropdownItem(text = "Settings", selected = selected == 2, onClick = { selected = 2 }),
                ),
                onDismissRequest = { showMenu = false },
            )
        }
    },
) { paddingValues ->
    // Content
}
```

## Properties

### OverlayNavigationPopupMenu / WindowNavigationPopupMenu Properties

| Property Name        | Type                | Description                                                      | Default Value                          | Required |
| -------------------- | ------------------- | ---------------------------------------------------------------- | -------------------------------------- | -------- |
| show                 | Boolean             | Whether the menu is shown                                        | -                                      | Yes      |
| items                | List\<DropdownItem> | The destinations (`text`, `icon`, `enabled`, `selected`, `onClick`) | -                                   | Yes      |
| onDismissRequest     | () -> Unit          | Called on a row click, an outside tap or back                    | -                                      | Yes      |
| popupModifier        | Modifier            | Modifier applied to the popup content area                       | Modifier                               | No       |
| onDismissFinished    | (() -> Unit)?       | Called once the exit animation has finished                      | null                                   | No       |
| dropdownColors       | DropdownColors      | Row colors                                                       | DropdownDefaults.dropdownColors()      | No       |
| minWidth             | Dp                  | Minimum menu width                                               | NavigationPopupMenuDefaults.MinWidth   | No       |
| maxHeight            | Dp                  | Maximum menu height                                              | NavigationPopupMenuDefaults.MaxHeight  | No       |
| renderInRootScaffold | Boolean             | (Overlay only) Render in the root Scaffold                       | true                                   | No       |
| materialBlur         | Boolean             | Blur what lies behind with the COUI 17 popup material when a blur provider is in scope | true             | No       |

### NavigationPopupMenuDefaults Object

| Property Name    | Type                  | Description                                                    | Value  |
| ---------------- | --------------------- | -------------------------------------------------------------- | ------ |
| ItemVerticalPadding | Dp                 | Row top / bottom padding (`coui_navigation_popup_item.xml`)    | 12.dp  |
| ItemHorizontalPadding | Dp               | Icon start margin and title margins (`coui_navigation_popup_horizontal_margin`) | 16.dp |
| MinWidth         | Dp                    | `coui_navigation_popup_item_min_width`                         | 208.dp |
| MaxHeight        | Dp                    | `coui_navigation_popup_max_height`                             | 324.dp |
| HorizontalMargin | Dp                    | Inset of the menu's end edge from the anchor's end edge        | 16.dp  |
| VerticalMargin   | Dp                    | Gap between the anchor and the menu                            | 8.dp   |
| PositionProvider | PopupPositionProvider | End-aligned, below the anchor when it fits, otherwise above    | -      |
