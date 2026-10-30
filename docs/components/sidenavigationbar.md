# SideNavigationBar

`SideNavigationBar` is a responsive navigation drawer for large screens, ported from COUI `COUISideNavigationBar` (a `DrawerLayout`). By the layout size it either docks the drawer beside the content, which makes room for it, or slides it over the content under a mask like a modal drawer.

Unlike [NavigationRail](./navigationrail) (a fixed 72dp column of icon items), `SideNavigationBar` is a drawer container: its drawer hosts any content, usually a list of pages.

<div style="position: relative; height: 420px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=sideNavigationBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.SideNavigationBar
import io.github.suqi8.coui.kmp.basic.rememberSideNavigationBarState
```

## Basic Usage

```kotlin
val state = rememberSideNavigationBarState(initialOpen = true)
val scope = rememberCoroutineScope()

SideNavigationBar(
    state = state,
    drawerContent = { PageList() },
) {
    Column {
        IconButton(onClick = { scope.launch { state.toggle() } }) {
            Icon(COUIIcons.Basic.Sidebar, contentDescription = "Navigation")
        }
        PageContent()
    }
}
```

## Drawer Modes

The mode compares the screen tier of the bar's size (`COUIResponsiveUtils`: small = 1, medium = 2, large = 3) with the content weight (1, or 2 with `parentChildHierarchy`):

| Mode    | When                                    | Drawer width                    | Content                              |
| ------- | --------------------------------------- | ------------------------------- | ------------------------------------ |
| Docked  | window weight > content weight          | `min(280dp, width × 0.382)`     | Shrinks beside the drawer, no mask   |
| Overlay | otherwise (e.g. narrower than 600dp)    | 300dp                           | Covered by the drawer and a `coui_color_mask` scrim; tapping it closes the drawer |

A 0.33dp `couiColorDivider` line follows the drawer's end edge in both modes. `state.mode` exposes the current mode, so the content can, for example, close the drawer after a selection only in overlay mode.

When the size class changes, an overlay drawer the user did not open while docked closes, and a docked drawer restores the open state the user last chose while docked (`mImplicitDrawerState`).

The drawer moves like a `DrawerLayout` settle: 512ms for a full move (`ViewDragHelper`: `(distance / range + 1) × 256ms`, at most 600ms) on the quintic ease-out interpolator. As in COUI the swipe gesture is locked; open and close it through the state.

## Edit Mode

`editMode = true` (`setIsInEditState`) masks the content beside a docked drawer with a 300ms fade and stops it from taking touches; in overlay mode it keeps the scrim from closing the drawer. `editModeMask = false` (`setHandlerEditModeMask`) skips the mask.

## Properties

### SideNavigationBar Properties

| Property Name        | Type                   | Description                                                   | Default Value                          | Required |
| -------------------- | ---------------------- | ------------------------------------------------------------- | -------------------------------------- | -------- |
| drawerContent        | @Composable () -> Unit | Drawer content                                                | -                                      | Yes      |
| modifier             | Modifier               | Modifier applied to the bar                                   | Modifier                               | No       |
| state                | SideNavigationBarState | Drawer state                                                  | rememberSideNavigationBarState()       | No       |
| parentChildHierarchy | Boolean                | The content is itself a list-detail split (docks only on large screens) | false                        | No       |
| editMode             | Boolean                | Edit state: mask the docked content                           | false                                  | No       |
| editModeMask         | Boolean                | Whether edit mode draws the mask                              | true                                   | No       |
| drawerColor          | Color                  | Drawer background                                             | colorScheme.surface                    | No       |
| maskColor            | Color                  | Scrim / edit mask color                                       | SideNavigationBarDefaults.maskColor    | No       |
| dividerColor         | Color                  | Drawer divider color                                          | DividerDefaults.DividerColor           | No       |
| content              | @Composable () -> Unit | Main content                                                  | -                                      | Yes      |

### SideNavigationBarState

| Member       | Type                  | Description                                        |
| ------------ | --------------------- | -------------------------------------------------- |
| isOpen       | Boolean               | Whether the drawer is open or opening              |
| mode         | SideNavigationBarMode | Current mode (`Overlay` / `Docked`)                |
| progress     | Float                 | Drawer offset, 0 to 1                              |
| open()       | suspend               | Opens the drawer                                   |
| close()      | suspend               | Closes the drawer                                  |
| toggle()     | suspend               | Toggles the drawer                                 |
| snapTo(open) | suspend               | Opens or closes without animating                  |

### SideNavigationBarDefaults Object

| Name                         | Type    | Description                                      | Value                    |
| ---------------------------- | ------- | ------------------------------------------------ | ------------------------ |
| OverlayDrawerWidth           | Dp      | `coui_side_navigation_bar_min_width`             | 300.dp                   |
| DockedDrawerMaxWidth         | Dp      | `coui_side_navigation_bar_max_width`             | 280.dp                   |
| DockedDrawerWidthFraction    | Float   | Docked share of the width                        | 0.382                    |
| DividerThickness             | Dp      | `coui_side_navigation_drawer_divider_width`      | 0.33.dp                  |
| EDIT_MASK_DURATION_MILLIS    | Int     | Edit mask fade                                   | 300                      |
| SettleEasing                 | Easing  | `ViewDragHelper` interpolator `(t - 1)^5 + 1`    | -                        |
| maskColor                    | Color   | `coui_color_mask` (`colorScheme.windowDimming`)  | #33000000 / #99000000    |
| drawerMode(width, height, parentChildHierarchy) | SideNavigationBarMode | Mode of a bar size | -          |
| drawerWidth(width, height, mode) | Dp  | Drawer width of a bar size                       | -                        |

## Differences from COUI

- In docked mode COUI leaves resizing the content to the app (through `onDrawerSlide`); `SideNavigationBar` makes room for the drawer itself.
- Hover / mouse event routing and the accessibility hierarchy tweaks of `COUISideNavigationBar` are not ported.
