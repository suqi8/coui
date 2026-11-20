# SwipeActionItem

`SwipeActionItem` reproduces the ColorOS slide list item (`COUISlideView` + `COUISlideMenuItem` + `COUISlideDeleteAnimation`): a row that slides towards the start to reveal action buttons behind it, with an optional built-in delete action that plays the COUI delete animation.

<div style="position: relative; height: 320px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=swipeActionItem" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.SwipeAction
import io.github.suqi8.coui.kmp.basic.SwipeActionItem
import io.github.suqi8.coui.kmp.basic.SwipeActionItemDefaults
import io.github.suqi8.coui.kmp.basic.SwipeActionMenuStyle
import io.github.suqi8.coui.kmp.basic.rememberSwipeActionItemState
```

## Basic Usage

The content slides over the actions, so give it an opaque background:

```kotlin
val state = rememberSwipeActionItemState()
val scope = rememberCoroutineScope()

SwipeActionItem(
    actions = listOf(SwipeAction(label = "Pin", color = COUITheme.colorScheme.primary)),
    onActionClick = { index -> scope.launch { state.close() } },
    state = state,
    onDelete = { rows.remove(row) },
) {
    BasicComponent(
        modifier = Modifier.background(COUITheme.colorScheme.cardBackground),
        title = row,
    )
}
```

## Round Rect Style

`menuStyle = SwipeActionMenuStyle.RoundRect` switches to `COUISlideView.setMenuItemStyle(MENU_ITEM_ROUND_RECT_STYLE)`: every action is its own full-height rounded rectangle (radius `couiRoundCornerM` 12dp, at most half of the row height), 4dp apart, 8dp after the content edge and 12dp before the end of the row. The menu stays packed while over-sliding instead of spreading.

```kotlin
SwipeActionItem(
    actions = listOf(SwipeAction(label = "Pin", color = COUITheme.colorScheme.primary)),
    onActionClick = { scope.launch { state.close() } },
    state = state,
    onDelete = { rows.remove(row) },
    menuStyle = SwipeActionMenuStyle.RoundRect,
) {
    BasicComponent(
        modifier = Modifier.background(COUITheme.colorScheme.cardBackground),
        title = row,
    )
}
```

## Delete

With `onDelete`, a trash action on the error color is added at the end of the menu (`COUISlideView.setDeleteEnable`). Clicking it plays `COUISlideDeleteAnimation`: the red fill grows across the row while the content slides out (330ms) and the row height collapses (400ms, 10ms later). `onDelete` runs when the collapse ends; remove the row from your data there. Key each row by its identity (`key(id) { ... }` or the lazy list item key) so the state of a deleted row does not move to its neighbour.

`fastDeleteEnabled = true` also deletes the row when it is over-slid past the menu by 48dp, or by 30dp with a fast fling (`COUISlideView.enableFastDelete`).

## Closing From Outside

Hoist the state to close the menu, for example when another row opens:

```kotlin
scope.launch { state.close() }
```

## Properties

### SwipeActionItem

| Property                 | Type                    | Description                                              | Default Value                                    | Required |
| ------------------------ | ----------------------- | -------------------------------------------------------- | ------------------------------------------------ | -------- |
| actions                  | List\<SwipeAction\>     | Actions in visual order from start to end                | -                                                | Yes      |
| onActionClick            | (Int) -> Unit           | Called with the index of the clicked action              | -                                                | Yes      |
| modifier                 | Modifier                | Modifier applied to the item                             | Modifier                                         | No       |
| state                    | SwipeActionItemState    | Reveal and delete state                                  | rememberSwipeActionItemState()                   | No       |
| enabled                  | Boolean                 | Whether the item can be swiped                           | true                                             | No       |
| onDelete                 | (() -> Unit)?           | Adds the delete action; called after the delete animation | null                                            | No       |
| fastDeleteEnabled        | Boolean                 | Whether over-sliding deletes (needs `onDelete`)          | false                                            | No       |
| deleteContentDescription | String?                 | Accessibility label of the delete action                 | null                                             | No       |
| menuStyle                | SwipeActionMenuStyle    | How the actions are drawn                                | SwipeActionMenuStyle.Rect                        | No       |
| actionMinWidth           | Dp                      | Minimum width of each action                             | SwipeActionItemDefaults.ActionMinWidth           | No       |
| colors                   | SwipeActionItemColors   | Menu colors                                              | SwipeActionItemDefaults.swipeActionItemColors()  | No       |
| textStyle                | TextStyle               | Action label style                                       | SwipeActionItemDefaults.textStyle()              | No       |
| content                  | @Composable () -> Unit  | Row content sliding over the actions                     | -                                                | Yes      |

### SwipeAction

| Property           | Type          | Description                                                   | Default Value     |
| ------------------ | ------------- | ------------------------------------------------------------- | ----------------- |
| label              | String?       | Label drawn in the middle of the action                       | null              |
| icon               | ImageVector?  | Icon drawn in the middle of the action                        | null              |
| color              | Color         | Own fill; `Unspecified` keeps `colors.containerColor`         | Color.Unspecified |
| contentDescription | String?       | Accessibility label                                           | label             |

### SwipeActionMenuStyle

| Value     | COUI Source                  | Description                                                                 |
| --------- | ---------------------------- | --------------------------------------------------------------------------- |
| Rect      | MENU_ITEM_NORMAL_RECT_STYLE  | One fill from the content edge to the end; actions spread when over-sliding |
| RoundRect | MENU_ITEM_ROUND_RECT_STYLE   | One rounded rectangle per action, with gaps and margins; no spreading       |

### SwipeActionItemState

| Member         | Type             | Description                                                      |
| -------------- | ---------------- | ---------------------------------------------------------------- |
| revealOffset   | Float            | How far the content is slid away, in pixels                      |
| isOpen         | Boolean          | Whether the menu is (partly) revealed                            |
| isDeleting     | Boolean          | Whether the delete animation is running                          |
| close()        | suspend          | Closes like `COUISlideView.shrink` (3ms per px, at most 200ms)   |
| open()         | suspend          | Opens on the release spring                                      |
| snapClose()    | suspend          | Jumps to the closed rest state                                   |

### SwipeActionItemDefaults

| Constant              | Type | Default Value | COUI Source                            |
| --------------------- | ---- | ------------- | -------------------------------------- |
| ActionMinWidth        | Dp   | 80.dp         | coui_slideview_menuitem_width          |
| ActionTextPadding     | Dp   | 12.dp         | coui_slide_view_text_padding           |
| FastDeleteOverSlide   | Dp   | 48.dp         | coui_slideview_over_slide_delete       |
| FastDeleteQuickSlop   | Dp   | 30.dp         | coui_slideview_quick_delete            |
| RoundRectCornerRadius | Dp   | 12.dp         | couiRoundCornerM (coui_round_corner_m) |
| RoundRectActionGap    | Dp   | 4.dp          | coui_slide_view_menuitem_gap_size      |
| RoundRectStartMargin  | Dp   | 8.dp          | coui_slide_view_menuitem_start_margin  |
| RoundRectEndMargin    | Dp   | 12.dp         | coui_slide_view_menuitem_end_margin    |

| Method                  | Type                  | Description                                        |
| ----------------------- | --------------------- | -------------------------------------------------- |
| textStyle()             | TextStyle             | bodyXL at 15sp(coui_slide_view_text_size)          |
| swipeActionItemColors() | SwipeActionItemColors | Creates the menu colors                            |

### `swipeActionItemColors()` factory

| Parameter      | Type  | Default                         | COUI Source                                   |
| -------------- | ----- | ------------------------------- | --------------------------------------------- |
| containerColor | Color | COUITheme.colorScheme.error     | itemBackgroundColor default couiColorError    |
| contentColor   | Color | COUITheme.colorScheme.onError   | coui_slideview_textcolor #F9F9F9              |

## Behavior

- The row takes a drag once it moves past the touch slop horizontally while moving more than twice as far horizontally as vertically (`COUISlideView.onInterceptTouchEvent`); a mostly vertical drag keeps scrolling the parent list. A closed row leaves drags towards the end edge to its parent, so it works inside a horizontal pager.
- When the row takes the drag it steps by 4/7 of the travel so far, or by 3/7 when the menu is already fully out or holds a single action, without passing the full menu; from then on the content follows the finger.
- Within the menu the content follows the finger 1:1. At or past the full menu every move, outwards and back, is damped by `delta x (1 - min(over / (width - menu), 1)) x 2 / 5` and, in the rect style, the actions spread apart, action `k` (counted from the start) moving by `k x over / (count + 1)`.
- On release, a fling faster than 1000 px/s opens or closes the menu; otherwise it opens when more than half of the menu shows. It settles on a damping 1 / stiffness 200 spring started at rest (`COUISlideView.animationScrollTo` sets no start velocity), so it never overshoots. With fast delete only the half-menu rule applies. When the parent takes the gesture over, the row returns to the nearer end on the `close()` curve instead.
- Inside a [CardListGroup](./cardlistgroup.md) the dividers above and below the row are hidden while its menu is out or it is being deleted (`COUISlideView.drawDivider`).
- In the round rect style the menu width also counts the gaps and both margins, so the half-menu, damping and fast delete thresholds all use it. Over-sliding leaves the actions packed against the content. When the delete runs and no action has its own color, a single rounded fill grows from the content edge to the end margin; with own colors each action keeps its own rounded rectangle.
- A label wider than 80dp widens its action to `text + 2 x 12dp`.
- Tapping the content while the menu is open closes it. Clicking a non-delete action keeps the menu open, like COUI.
- In RTL layouts the menu is at the left edge and the row slides to the right.
