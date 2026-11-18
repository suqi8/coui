---
title: ContextMenu
requiresScaffoldHost: true
prerequisites:
  - "`OverlayContextMenu` must be used within `Scaffold` to provide `COUIPopupHost`"
  - "`WindowContextMenu` renders in a platform window and needs no `Scaffold`"
hostComponent: Scaffold
---

# ContextMenu

`OverlayContextMenu` and `WindowContextMenu` wrap content with a context menu that opens at the pointer, after ColorOS's `COUIContextMenu` / `COUIIsolatedPopupListWindow`: a long press or a secondary (right) mouse click opens the dropdown menu anchored to that single point instead of below the content.

<div style="position: relative; height: 360px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=contextMenu" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.menu.OverlayContextMenu
import io.github.suqi8.coui.kmp.menu.WindowContextMenu
```

## Basic Usage

```kotlin
val entries = listOf(
    DropdownEntry(
        items = listOf("Copy", "Share", "Rename").map { text ->
            DropdownItem(text = text, onClick = { /* handle */ })
        },
    ),
    DropdownEntry(items = listOf(DropdownItem(text = "Delete", alert = true))),
)

Scaffold {
    OverlayContextMenu(entries = entries) {
        BasicComponent(title = "Report.pdf", onClick = { /* open */ })
    }
}
```

`WindowContextMenu` takes the same parameters except `renderInRootScaffold` and works without a `Scaffold`.

## Properties

### OverlayContextMenu Properties

| Property Name         | Type                  | Description                                               | Default Value                    | Required |
| --------------------- | --------------------- | --------------------------------------------------------- | -------------------------------- | -------- |
| entries               | List\<DropdownEntry>   | Menu groups, separated by dividers                        | -                                | Yes      |
| modifier              | Modifier              | Modifier applied to the wrapper around the content        | Modifier                         | No       |
| enabled               | Boolean               | Whether a long press or right click opens the menu        | true                             | No       |
| dropdownColors        | DropdownColors        | Colors of the menu rows                                   | DropdownDefaults.dropdownColors() | No      |
| maxHeight             | Dp?                   | Maximum height of the menu                                | null                             | No       |
| renderInRootScaffold  | Boolean               | Whether the popup renders in the root scaffold overlay (Overlay only) | true                 | No       |
| collapseOnSelection   | Boolean               | Whether a row click dismisses the menu                    | true                             | No       |
| hapticFeedbackEnabled | Boolean               | Whether the long press plays the long-press haptic        | true                             | No       |
| onExpandedChange      | ((Boolean) -> Unit)?  | Called when the menu opens or closes                      | null                             | No       |
| content               | @Composable () -> Unit | Content the menu is registered on                        | -                                | Yes      |

## Behavior

- **Long press**: the menu opens at the DOWN point once the pointer has stayed within the touch slop for the long-press timeout (COUI `PreciseLongPressHelper`), with the long-press haptic. The rest of the gesture is consumed, so the content's own click does not also run.
- **Right click**: a secondary mouse button press opens the menu at once, without a haptic.
- **Placement**: the anchor collapses to the pointer (`COUIPopupListWindow.show(view, x, y)`, `COUIIsolatedPopupListWindow`'s 1px dummy anchor); the popup locator puts the menu below or above that point with no anchor gap and keeps it inside the window barriers. The menu itself is the regular dropdown popup, with its material, animation and row behavior.
- **Accessibility**: the wrapper adds no semantics of its own; expose the same actions elsewhere for users who cannot long press.
