# Banner

`Banner` reproduces the ColorOS banner (`COUIBanner`): a horizontally paging carousel of rounded cards that scrolls itself and shows a [PageIndicator](./pageindicator) over the bottom of the cards. It loops infinitely, pauses while touched, and can let the neighbouring cards peek in. On windows at least 600dp wide it turns into a plain horizontal list of cards, as COUI does.

<div style="position: relative; height: 360px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=banner" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.Banner
import io.github.suqi8.coui.kmp.basic.BannerDefaults
import io.github.suqi8.coui.kmp.basic.BannerType
```

## Basic Usage

Give the banner a height; each page is clipped to a rounded card:

```kotlin
Banner(
    pageCount = images.size,
    modifier = Modifier
        .fillMaxWidth()
        .height(160.dp),
) { page ->
    Image(
        painter = images[page],
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
    )
}
```

## Peeking Neighbours

`startItemWidth` and `endItemWidth` let the previous and next cards show at the sides; `pageMargin` is the gap between cards:

```kotlin
Banner(
    pageCount = images.size,
    modifier = Modifier.fillMaxWidth().height(140.dp),
    autoLoop = false,
    pageMargin = 8.dp,
    startItemWidth = 16.dp,
    endItemWidth = 16.dp,
) { page ->
    // Page content
}
```

## Scaled Neighbours

`scalePages` shrinks the cards beside the current one to 0.85 and grows them back as they scroll in (`COUIScalePageTransformer`):

```kotlin
Banner(
    pageCount = images.size,
    modifier = Modifier.fillMaxWidth().height(140.dp),
    startItemWidth = 16.dp,
    endItemWidth = 16.dp,
    scalePages = true,
) { page ->
    // Page content
}
```

## List Form

`type` defaults to `BannerDefaults.type()`: `BannerType.List` on windows at least 600dp wide, `BannerType.Pager` otherwise (`COUIBanner` reads `screenWidthDp >= 600`). The list form (`COUIBannerRecyclerView`) has no indicator, no loop and no auto scroll: the cards sit `listItemSpacing` (8dp) apart behind a `pageMargin` side padding, and a fling settles with a card at the start edge. COUI leaves the card width to the app; `listItemWidth` sets it, and when unspecified two cards fill the viewport.

```kotlin
Banner(
    pageCount = images.size,
    modifier = Modifier.fillMaxWidth().height(120.dp),
    type = BannerType.List,
    listItemWidth = 280.dp,
) { page ->
    // Page content
}
```

## Properties

### Banner

| Property             | Type                         | Description                                                 | Default Value                       | Required |
| -------------------- | ---------------------------- | ----------------------------------------------------------- | ----------------------------------- | -------- |
| pageCount            | Int                          | Number of pages                                             | -                                   | Yes      |
| modifier             | Modifier                     | Modifier applied to the banner                              | Modifier                            | No       |
| type                 | BannerType                   | `Pager` (carousel) or `List` (horizontal list)              | BannerDefaults.type()               | No       |
| initialPage          | Int                          | Page shown first                                            | 0                                   | No       |
| autoLoop             | Boolean                      | Whether the banner scrolls by itself (needs `infiniteLoop`) | true                                | No       |
| infiniteLoop         | Boolean                      | Whether the last page is followed by the first              | true                                | No       |
| showIndicator        | Boolean                      | Whether the page indicator is shown                         | true                                | No       |
| loopDurationMillis   | Long                         | How long a page stays before the next auto scroll           | BannerDefaults.LoopDurationMillis   | No       |
| scrollDurationMillis | Int                          | Duration of an auto scroll                                  | BannerDefaults.ScrollDurationMillis | No       |
| cornerRadius         | Dp                           | Corner radius of the cards                                  | BannerDefaults.CornerRadius         | No       |
| pageMargin           | Dp                           | Gap between cards and their side inset                      | BannerDefaults.PageMargin           | No       |
| startItemWidth       | Dp                           | How far the previous card peeks in                          | 0.dp                                | No       |
| endItemWidth         | Dp                           | How far the next card peeks in                              | 0.dp                                | No       |
| listItemWidth        | Dp                           | Card width in the list form; unspecified fits two cards     | Dp.Unspecified                      | No       |
| listItemSpacing      | Dp                           | Gap between cards in the list form                          | BannerDefaults.ListItemSpacing      | No       |
| scalePages           | Boolean                      | Scale the neighbouring cards to 0.85 in the pager form      | false                               | No       |
| indicatorColors      | PageIndicatorColors          | Page indicator colors                                       | BannerDefaults.indicatorColors()    | No       |
| onPageChange         | ((Int) -> Unit)?             | Called with the real page when the current page changes; an auto scroll reports it when it lands | null                                | No       |
| pageContent          | @Composable (page: Int) -> Unit | Content of a page, given the real page index             | -                                   | Yes      |

### BannerDefaults

| Constant              | Type | Default Value                | COUI Source                                        |
| --------------------- | ---- | ---------------------------- | -------------------------------------------------- |
| LoopDurationMillis    | Long | 5000                         | `couiLoopDuration` default 5 (seconds)             |
| ScrollDurationMillis  | Int  | 950                          | `COUIBanner` scroll duration                       |
| CornerRadius          | Dp   | CardDefaults.CornerRadius    | Card list radius (COUI leaves the card shape to the app) |
| PageMargin            | Dp   | 24.dp                        | `COUIBannerUtil` default page margin               |
| IndicatorBottomMargin | Dp   | 10.dp                        | coui_banner_content_layout `layout_marginBottom`   |
| ListTypeBreakpoint    | Dp   | 600.dp                       | `COUIBanner` `screenWidthDp >= 600`                |
| ListItemSpacing       | Dp   | 8.dp                         | `COUIBannerRecyclerView` `coui_item_decoration_8dp` |
| MinPageScale          | Float | 0.85                        | `COUIScalePageTransformer`                         |

| Method            | Description                                                                                         |
| ----------------- | --------------------------------------------------------------------------------------------------- |
| indicatorColors() | Fixed colors for the image behind: dots `#4DFFFFFF` (`coui_page_indicator_dot_color_in_dark_bg`), trace `#E6FFFFFF` |
| type()            | `BannerType.List` when the window is at least `ListTypeBreakpoint` wide, else `BannerType.Pager`     |

## Behavior

- With `infiniteLoop` and two or more pages, a copy of the last page sits before the first and a copy of the first after the last; when the pager settles on a copy it jumps to the real page without animation.
- The auto scroll waits `loopDurationMillis + scrollDurationMillis`, then scrolls to the next page in `scrollDurationMillis` on `PathInterpolator(0.2, 0, 0.1, 1)`. Like COUI's loop task, which re-posts itself as it starts a scroll, scrolls start `loopDurationMillis + scrollDurationMillis` apart, so each page rests for `loopDurationMillis` once it has settled. A touch cancels the pending scroll (one already running finishes unless the finger drags the pager); lifting the finger restarts the wait. Like COUI's fake drag, an auto scroll reports its page through `onPageChange` only when it lands on it.
- The cards are inset by `startItemWidth + pageMargin` and `endItemWidth + pageMargin` and placed `pageMargin` apart.
- The indicator is centred 10dp above the bottom; its dots are not clickable. While the pager wraps around through a copy, the indicator holds the last dot and then jumps.
