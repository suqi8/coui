# AlphabetIndexBar

`AlphabetIndexBar` reproduces ColorOS's side letter index (`COUITouchSearchView`), used by contact and app lists: touching or dragging along the bar selects a key, highlights it and shows a bubble with the key beside the bar.

<div style="position: relative; height: 520px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=alphabetIndexBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.AlphabetIndexBar
import io.github.suqi8.coui.kmp.basic.AlphabetIndexBarDefaults
```

## Basic Usage

Place the bar above the list it indexes (the bubble is drawn outside its bounds) and scroll the list in `onKeySelected`:

```kotlin
Box(Modifier.fillMaxSize()) {
    LazyColumn(state = listState) { /* ... */ }
    AlphabetIndexBar(
        onKeySelected = { key ->
            scope.launch { listState.scrollToItem(indexOfSection(key)) }
        },
        availableKeys = sectionKeys,
        modifier = Modifier
            .align(Alignment.CenterEnd)
            .fillMaxHeight(),
    )
}
```

## Available Keys and Dots

Keys outside `availableKeys` stay visible but cannot be selected: touching one selects the closest available key above it (COUI `hasValue`). When the bar is too short for every key, runs of keys collapse into `·` dots following COUITouchSearchView's dot levels; dragging over a dot walks through the keys it hides. Below the smallest level the bar draws nothing.

## Properties

### AlphabetIndexBar

| Property              | Type                   | Description                                         | Default Value                                     | Required |
| --------------------- | ---------------------- | --------------------------------------------------- | ------------------------------------------------- | -------- |
| onKeySelected         | (String) -> Unit       | Called with each newly selected key                 | -                                                 | Yes      |
| modifier              | Modifier               | Modifier applied to the bar                         | Modifier                                          | No       |
| keys                  | List\<String\>         | Index keys, top to bottom                           | AlphabetIndexBarDefaults.Keys                     | No       |
| availableKeys         | Set\<String\>?         | Selectable keys, or `null` for all                  | null                                              | No       |
| showCollectKey        | Boolean                | Whether the favorites star leads the keys           | true                                              | No       |
| hapticFeedbackEnabled | Boolean                | Whether moving to another key ticks a haptic        | true                                              | No       |
| colors                | AlphabetIndexBarColors | Bar and bubble colors                               | AlphabetIndexBarDefaults.alphabetIndexBarColors() | No       |

### AlphabetIndexBarDefaults

| Constant        | Type           | Default Value  | COUI Source                                                   |
| --------------- | -------------- | -------------- | ------------------------------------------------------------- |
| CollectKey      | String         | "*"            | star_character                                                |
| Keys            | List\<String\> | A to Z, "#"    | normal_touchsearch_keys                                       |
| Width           | Dp             | 39.dp          | coui_touchsearch_background_width                             |
| RowHeight       | Dp             | 16.dp          | coui_touchsearch_each_item_height 14dp + item_spacing 2dp     |
| VerticalPadding | Dp             | 16.dp          | coui_touchsearch_padding_top / _bottom                        |
| CollectIconSize | Dp             | 12.dp          | coui_touchsearch_collect_normal                               |
| KeyTextSize     | Dp             | 12.dp          | coui_touchsearch_key_textsize (dp, ignores font scale)        |
| DotTextSize     | Dp             | 20.dp          | coui_touchsearch_key_dot_textsize                             |
| BubbleSize      | Dp             | 48.dp          | coui_touchsearch_popup_first_default_width / _height          |
| BubbleGap       | Dp             | 8.5.dp         | popupwin_right_margin 0.5dp + touch_end_gap 8dp               |

### `alphabetIndexBarColors()` factory

| Parameter          | Type  | Default                                   | COUI Source                                        |
| ------------------ | ----- | ----------------------------------------- | -------------------------------------------------- |
| keyColor           | Color | COUITheme.colorScheme.onSurfaceSecondary  | coui_touchsearchview_text_color (couiColorSecondNeutral) |
| selectedKeyColor   | Color | COUITheme.colorScheme.primaryTextOnPopup  | pressed state, couiColorPrimaryTextOnPopup         |
| dotColor           | Color | COUITheme.colorScheme.dragHandle          | coui_touchsearchview_dot_color (couiColorControls) |
| bubbleColor        | Color | COUITheme.colorScheme.backgroundTop       | coui_touch_search_popup_bg (couiColorBackgroundTop) |
| bubbleContentColor | Color | COUITheme.colorScheme.onBackground        | couiColorPrimaryNeutral                            |

## Behavior

- Keys are 12dp medium text centered in 16dp rows below a 16dp top padding; the star is a 12dp outline icon. The bar wraps to the height all keys need and compresses into dots when given less.
- The selected key, star or dot turns `selectedKeyColor` and keeps it after the finger lifts, like COUI.
- The 48dp bubble shows the key in 20sp medium (or a 24dp star), centered on the key row and 8.5dp beside the bar. It grows from 0.8 and fades in over 350ms on COUIMoveEaseInterpolator, and one second after the finger lifts it shrinks back to 0.8 and fades out over 300ms.
- Moving to another key ticks a `TextHandleMove` haptic. Every selectable key is also exposed as an accessibility custom action.
