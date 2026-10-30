# SidePaneLayout

`SidePaneLayout` places a side pane next to the main content for tablets, foldables and desktop windows. It is ported from COUI `COUISidePaneLayout`: the pane slides in and out with the COUI move-ease animation, the content resizes with it on wide layouts, and a toggle icon in the top-start corner opens and closes the pane.

<div style="position: relative; height: 420px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=sidePaneLayout" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.SidePaneLayout
import io.github.suqi8.coui.kmp.basic.rememberSidePaneState
```

## Basic Usage

```kotlin
val state = rememberSidePaneState()

SidePaneLayout(
    state = state,
    pane = { FolderList() },
) {
    NoteList()
}
```

## Behavior

- **Split mode** (`COUISidePaneUtils.isSplitMode`): when the layout is at least 600dp wide in a window at least 480dp tall, the pane (224dp, `coui_sliding_pane_width`) and the content sit side by side and the content width follows the pane: `width - paneWidth * progress`.
- **Narrow layouts**: the content keeps the full width and is pushed aside by the pane. With `coverStyle = true` the pane covers the content instead; a `coui_color_mask` mask fades in with the pane over the content, and touching the content closes the pane (`touchContentEnabled`).
- **Animation**: 483ms on `COUIMoveEaseInterpolator` (cubic-bezier 0.3, 0, 0.1, 1). An interrupted move only takes the remaining share of the duration.
- **Toggle icon**: 48dp, 45dp from the top and 12dp from the start edge (`coui_sliding_icon_layout`). Leave 72dp at the start of the content's title row (`coui_side_pane_layout_toolbar_margin_start`) so the icon does not overlap it.
- `alwaysShowMask = true` draws the mask on the content in every mode and swallows touches on it while the pane is open, without closing the pane.

A common pattern is to cover on narrow layouts only:

```kotlin
val windowHeight = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.height.toDp() }
BoxWithConstraints {
    val split = SidePaneLayoutDefaults.isSplitMode(maxWidth, windowHeight)
    SidePaneLayout(
        state = rememberSidePaneState(initialOpen = split),
        coverStyle = !split,
        pane = { FolderList() },
    ) {
        NoteList()
    }
}
```

## Properties

### SidePaneLayout Properties

| Property Name       | Type                   | Description                                                        | Default Value                    | Required |
| ------------------- | ---------------------- | ------------------------------------------------------------------ | -------------------------------- | -------- |
| pane                | @Composable () -> Unit | The side pane                                                      | -                                | Yes      |
| modifier            | Modifier               | Modifier applied to the layout                                     | Modifier                         | No       |
| state               | SidePaneState          | Open state                                                         | rememberSidePaneState()          | No       |
| coverStyle          | Boolean                | The pane covers the content instead of resizing it                 | false                            | No       |
| alwaysShowMask      | Boolean                | Mask the content (and swallow its touches) while the pane is open  | false                            | No       |
| touchContentEnabled | Boolean                | In cover style, touching the content closes the pane               | true                             | No       |
| showToggleIcon      | Boolean                | Show the toggle icon                                               | true                             | No       |
| paneWidth           | Dp                     | Pane width                                                         | SidePaneLayoutDefaults.PaneWidth | No       |
| maskColor           | Color                  | Mask color at full opening                                         | SidePaneLayoutDefaults.maskColor | No       |
| toggleIconColor     | Color                  | Toggle icon tint                                                   | colorScheme.onBackground         | No       |
| content             | @Composable () -> Unit | The main content                                                   | -                                | Yes      |

### SidePaneState

| Member         | Type             | Description                                         |
| -------------- | ---------------- | --------------------------------------------------- |
| isOpen         | Boolean          | Whether the pane is open or opening                 |
| progress       | Float            | Slide offset, 0 (closed) to 1 (open)                |
| isAnimating    | Boolean          | Whether the pane is moving                          |
| open()         | suspend          | Opens the pane with the animation                   |
| close()        | suspend          | Closes the pane with the animation                  |
| toggle()       | suspend          | Toggles the pane, as the icon does                  |
| snapTo(open)   | suspend          | Opens or closes without animating                   |

`rememberSidePaneState(initialOpen = true)` creates a saveable state; the pane starts open as in COUI (`setDefaultShowPane(true)`).

### SidePaneLayoutDefaults Object

| Name                      | Type          | Description                                          | Value                |
| ------------------------- | ------------- | ---------------------------------------------------- | -------------------- |
| ANIMATION_DURATION_MILLIS | Int           | Pane animation duration                              | 483                  |
| AnimationEasing           | Easing        | `COUIMoveEaseInterpolator`                           | (0.3, 0, 0.1, 1)     |
| AnimationSpec             | AnimationSpec | The full-length pane animation                       | tween(483)           |
| PaneWidth                 | Dp            | `coui_sliding_pane_width`                            | 224.dp               |
| ToggleIconSize            | Dp            | `coui_side_pane_layout_icon_size`                    | 48.dp                |
| ToggleIconGlyphSize       | Dp            | Icon inside the toggle button                        | 24.dp                |
| ToggleIconMarginTop       | Dp            | `coui_side_pane_layout_icon_margin_top`              | 45.dp                |
| ToggleIconMarginStart     | Dp            | `coui_side_pane_layout_icon_margin_start`            | 12.dp                |
| maskColor                 | Color         | `coui_color_mask` (`colorScheme.windowDimming`)      | #33000000 / #99000000 |
| isSplitMode(width, windowHeight) | Boolean | Whether pane and content sit side by side        | width ≥ 600dp and windowHeight ≥ 480dp |

## Differences from COUI

- The pane cannot be dragged: `COUISidePaneLayout` only drags it with a `ViewDragHelper` edge drag gated by its slideable flag, which Compose apps can do with their own gestures.
- The split-mode check uses the layout width (COUI reads the activity's `screenWidthDp`) and ignores the multi-window exception, so the layout also adapts inside a desktop window or a docs iframe.
