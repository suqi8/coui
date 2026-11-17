# ScrollBar

`VerticalScrollBar` is the COUI 17 scrollbar (`COUIScrollBar`): a thin thumb that appears while the content scrolls, stays for 2 seconds after the last scroll and then fades out, and can be dragged to scroll. `HorizontalScrollBar` is the same thumb laid out horizontally.

::: warning Experimental
The scrollbar API is marked `@ExperimentalScrollBarApi`; opt in with `@OptIn(ExperimentalScrollBarApi::class)`.
:::

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.VerticalScrollBar
import io.github.suqi8.coui.kmp.basic.HorizontalScrollBar
import io.github.suqi8.coui.kmp.basic.rememberScrollBarAdapter
import io.github.suqi8.coui.kmp.basic.ScrollBarDefaults
import io.github.suqi8.coui.kmp.interfaces.ExperimentalScrollBarApi
```

## Basic Usage

Place the scrollbar over the scrollable content and connect them with a `ScrollBarAdapter`. Adapters exist for `ScrollState`, `LazyListState` and `LazyGridState`:

```kotlin
@OptIn(ExperimentalScrollBarApi::class)
@Composable
fun ListWithScrollBar() {
    val listState = rememberLazyListState()
    Box(Modifier.fillMaxSize()) {
        LazyColumn(state = listState) {
            items(100) { Text("Item $it", Modifier.padding(16.dp)) }
        }
        VerticalScrollBar(
            adapter = rememberScrollBarAdapter(listState),
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight()
        )
    }
}
```

## Behavior

- The thumb rect is 14dp wide (`coui_scrollbar_wight`) and at least 48dp long (`coui_scrollbar_min_height`). The visible thumb is drawn inside it, 8dp from its start and 16dp from its end (`coui_scrollbar_margin_top` / `_bottom`) and 5.5dp from the edge, so it is 3dp wide and at least 24dp long.
- It appears at once when the content scrolls, stays for 2000ms after the last scroll or drag (`SCROLLER_FADE_TIMEOUT`, four times the platform delay when it first appears) and then fades out linearly over 250ms.
- While the thumb is visible it can be grabbed: it widens to 6dp at once (pressed inset 4dp) and scrolls the content as it is dragged; releasing it restarts the fade timer.
- The thumb color is `#29000000` / `#40FFFFFF` (`coui_scrollbar_color`), the same while dragged.

::: info Library extensions
`COUIScrollBar` handles the vertical direction only and draws no track. `HorizontalScrollBar`, `ScrollBarColors.trackColor` (hidden by default) and the thumb widening while a mouse hovers it are library extensions.
:::

## Properties

### VerticalScrollBar / HorizontalScrollBar Properties

| Property Name  | Type             | Description                                                                 | Default Value                       | Required |
| -------------- | ---------------- | --------------------------------------------------------------------------- | ----------------------------------- | -------- |
| adapter        | ScrollBarAdapter | Connects the scrollbar to the scrollable component                          | -                                   | Yes      |
| modifier       | Modifier         | Modifier applied to the scrollbar                                           | Modifier                            | No       |
| reverseLayout  | Boolean          | Reverse the direction of scrolling and layout                               | false                               | No       |
| trackPadding   | PaddingValues    | Padding applied to the track to skip content padding areas                  | PaddingValues(0.dp)                 | No       |
| colors         | ScrollBarColors  | Colors of the scrollbar                                                     | ScrollBarDefaults.scrollBarColors() | No       |
| thumbWidth     | Dp               | Width of the visible thumb                                                  | ScrollBarDefaults.ThumbWidth        | No       |
| cornerRadius   | Dp               | Corner radius; `Dp.Unspecified` uses half of `thumbWidth`                   | ScrollBarDefaults.CornerRadius      | No       |
| thumbMinLength | Dp               | Minimum length of the thumb rect, margins included                          | ScrollBarDefaults.ThumbMinLength    | No       |
| endPadding     | Dp               | Padding from the end edge                                                   | ScrollBarDefaults.EndPadding        | No       |

### rememberScrollBarAdapter

| Overload                                       | Description                       |
| ---------------------------------------------- | --------------------------------- |
| rememberScrollBarAdapter(scrollState: ScrollState)   | For `verticalScroll` / `horizontalScroll` |
| rememberScrollBarAdapter(scrollState: LazyListState) | For `LazyColumn` / `LazyRow`      |
| rememberScrollBarAdapter(scrollState: LazyGridState) | For lazy grids                    |

### ScrollBarDefaults Object

| Constant Name          | Type | Default Value  | Description                                                     |
| ---------------------- | ---- | -------------- | --------------------------------------------------------------- |
| ThumbWidth             | Dp   | 3.dp           | Visible thumb width                                             |
| EndPadding             | Dp   | 5.5.dp         | Padding from the end edge (`coui_scrollbar_drawable_default_inset`) |
| ThumbMinLength         | Dp   | 48.dp          | Minimum thumb rect length (`coui_scrollbar_min_height`)         |
| ThumbMarginStart       | Dp   | 8.dp           | Visible thumb inset from the start of its rect                  |
| ThumbMarginEnd         | Dp   | 16.dp          | Visible thumb inset from the end of its rect                    |
| CornerRadius           | Dp   | Dp.Unspecified | Corner radius; unspecified uses half the thumb width            |
| FadeDelayMillis        | Int  | 2000           | Time the thumb stays after the last scroll or drag              |
| InitialFadeDelayMillis | Int  | 1200           | Time the thumb stays when it first appears                      |
| FadeDurationMillis     | Int  | 250            | Linear fade-out duration                                        |
| TouchTargetWidth       | Dp   | 14.dp          | Width of the strip that can start a thumb drag                  |
| DragThumbWidth         | Dp   | 6.dp           | Visible thumb width while dragged                               |
| DragEndPadding         | Dp   | 4.dp           | Padding from the end edge while dragged                         |

| Method Name       | Type            | Description                                                         |
| ----------------- | --------------- | ------------------------------------------------------------------- |
| scrollBarColors() | ScrollBarColors | Creates the colors; an unspecified thumb uses the theme drag-handle color |

### ScrollBarColors Class

| Property Name | Type  | Description                                              |
| ------------- | ----- | -------------------------------------------------------- |
| thumbColor    | Color | Thumb color; `Color.Unspecified` uses the theme default  |
| trackColor    | Color | Track color; `Color.Unspecified` hides the track         |
