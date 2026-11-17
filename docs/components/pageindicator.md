# PageIndicator

`PageIndicator` reproduces the ColorOS page indicator (`COUIPageIndicator` / `COUIPageIndicator2`): a row of 6dp dots for carousels and onboarding pages. While the pages scroll, the current dot stretches towards the next one and snaps over to it (the trace "worm"), and the two dots cross-fade between the dot and trace colors. With more than five pages only six dots are visible: four full dots flanked by smaller ones that grow and shrink as the window follows the current page.

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=pageIndicator" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.PageIndicator
import io.github.suqi8.coui.kmp.basic.PageIndicatorDefaults
```

## Basic Usage

Follow a Compose `PagerState`; tapping the indicator scrolls the pager to the nearest dot:

```kotlin
val pagerState = rememberPagerState { 4 }

HorizontalPager(state = pagerState) { page ->
    // Page content
}
PageIndicator(pagerState = pagerState)
```

## Plain Position

Without a pager, pass the current page (and, while dragging, the offset towards the next page). A page change animates the trace on COUI's position spring (damping ratio 1, stiffness 1500):

```kotlin
var current by remember { mutableIntStateOf(0) }

PageIndicator(
    pageCount = 12,
    currentPage = current,
    onDotClick = { current = it },
)
```

## Properties

### PageIndicator (PagerState)

| Property    | Type                 | Description                                              | Default Value                    | Required |
| ----------- | -------------------- | -------------------------------------------------------- | -------------------------------- | -------- |
| pagerState  | PagerState           | The pager the indicator follows                          | -                                | Yes      |
| modifier    | Modifier             | Modifier applied to the indicator                        | Modifier                         | No       |
| pageCount   | Int                  | Number of dots                                           | pagerState.pageCount             | No       |
| dotClickable| Boolean              | Whether a tap selects the nearest dot                    | true                             | No       |
| colors      | PageIndicatorColors  | Dot and trace colors                                     | PageIndicatorDefaults.colors()   | No       |
| onDotClick  | ((Int) -> Unit)?     | Called with the tapped dot; null scrolls the pager to it | null                             | No       |

### PageIndicator (position)

| Property                  | Type                | Description                                                | Default Value                  | Required |
| ------------------------- | ------------------- | ---------------------------------------------------------- | ------------------------------ | -------- |
| pageCount                 | Int                 | Number of dots                                             | -                              | Yes      |
| currentPage               | Int                 | The current page                                           | -                              | Yes      |
| modifier                  | Modifier            | Modifier applied to the indicator                          | Modifier                       | No       |
| currentPageOffsetFraction | Float               | Offset towards the next page, 0 to 1                       | 0f                             | No       |
| animated                  | Boolean             | Whether page changes run the trace on the position spring  | true                           | No       |
| colors                    | PageIndicatorColors | Dot and trace colors                                       | PageIndicatorDefaults.colors() | No       |
| onDotClick                | ((Int) -> Unit)?    | Called with the dot nearest to a tap; null disables taps   | null                           | No       |

### PageIndicatorDefaults

| Constant      | Type | Default Value | COUI Source                                                |
| ------------- | ---- | ------------- | ---------------------------------------------------------- |
| DotSize       | Dp   | 6.dp          | coui_page_indicator_dot_size (also the indicator height)   |
| MediumDotSize | Dp   | 4.5.dp        | coui_page_indicator_dot_size_medium                        |
| SmallDotSize  | Dp   | 2.5.dp        | coui_page_indicator_dot_size_small                         |
| DotSpacing    | Dp   | 3.dp          | coui_page_indicator_dot_spacing (doubled: dots 6dp apart)  |

| Method   | Description                                                                                       |
| -------- | ------------------------------------------------------------------------------------------------- |
| colors() | Dots `?couiColorHintNeutral` (`labelTertiary`), trace `?couiColorLabelPrimary` (`labelPrimary`) |

### PageIndicatorColors

| Property   | Type  | Description                          |
| ---------- | ----- | ------------------------------------ |
| dotColor   | Color | Color of the dots                    |
| traceColor | Color | Color of the current dot and trace   |

## Behavior

- Each dot takes a 12dp step: the indicator is `min(6, pageCount) x 12dp` wide and 6dp high.
- The trace starts stretching after 5% of a page and is fully over at 95%; between those points the neck follows `COUIEaseInterpolator` (`PathInterpolator(0.33, 0, 0.67, 1)`), and in the first and last 5% the two dots cross-fade.
- In right-to-left layouts the indicator is mirrored.
- Positions beyond the last page (an over-scroll, or a looping carousel wrapping around) keep the last valid position.
