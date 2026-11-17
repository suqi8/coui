# Banner

`Banner` 复刻 ColorOS 的轮播横幅（`COUIBanner`）：可横向翻页的圆角卡片轮播，会自动滚动，并在卡片底部显示 [PageIndicator](./pageindicator)。它无限循环、触摸时暂停，还可以让相邻卡片在两侧露出一部分。与 COUI 一致，窗口宽度不小于 600dp 时它会变为普通的横向卡片列表。

<div style="position: relative; height: 360px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=banner" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.Banner
import io.github.suqi8.coui.kmp.basic.BannerDefaults
import io.github.suqi8.coui.kmp.basic.BannerType
```

## 基本用法

给横幅指定高度；每一页都会被裁剪成圆角卡片：

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

## 露出相邻卡片

`startItemWidth` 与 `endItemWidth` 让上一张和下一张卡片在两侧露出；`pageMargin` 是卡片间距：

```kotlin
Banner(
    pageCount = images.size,
    modifier = Modifier.fillMaxWidth().height(140.dp),
    autoLoop = false,
    pageMargin = 8.dp,
    startItemWidth = 16.dp,
    endItemWidth = 16.dp,
) { page ->
    // 页面内容
}
```

## 缩放相邻卡片

`scalePages` 会把当前页两侧的卡片缩小到 0.85，并在滑入时逐渐恢复（`COUIScalePageTransformer`）：

```kotlin
Banner(
    pageCount = images.size,
    modifier = Modifier.fillMaxWidth().height(140.dp),
    startItemWidth = 16.dp,
    endItemWidth = 16.dp,
    scalePages = true,
) { page ->
    // 页面内容
}
```

## 列表形态

`type` 默认取 `BannerDefaults.type()`：窗口宽度不小于 600dp 时为 `BannerType.List`，否则为 `BannerType.Pager`（`COUIBanner` 判断 `screenWidthDp >= 600`）。列表形态（`COUIBannerRecyclerView`）没有指示器、不循环也不自动滚动：卡片间距 `listItemSpacing`（8dp），两侧留 `pageMargin`，惯性滑动停在某张卡片贴齐起始边的位置。COUI 把卡片宽度交给应用决定；`listItemWidth` 用于设置宽度，未指定时一屏放两张卡片。

```kotlin
Banner(
    pageCount = images.size,
    modifier = Modifier.fillMaxWidth().height(120.dp),
    type = BannerType.List,
    listItemWidth = 280.dp,
) { page ->
    // 页面内容
}
```

## 属性

### Banner

| 属性名               | 类型                            | 说明                                         | 默认值                              | 是否必须 |
| -------------------- | ------------------------------- | -------------------------------------------- | ----------------------------------- | -------- |
| pageCount            | Int                             | 页数                                         | -                                   | 是       |
| modifier             | Modifier                        | 应用于横幅的修饰符                           | Modifier                            | 否       |
| type                 | BannerType                      | `Pager`（轮播）或 `List`（横向列表）         | BannerDefaults.type()               | 否       |
| initialPage          | Int                             | 首先显示的页                                 | 0                                   | 否       |
| autoLoop             | Boolean                         | 是否自动滚动（需要 `infiniteLoop`）          | true                                | 否       |
| infiniteLoop         | Boolean                         | 最后一页之后是否接第一页                     | true                                | 否       |
| showIndicator        | Boolean                         | 是否显示页面指示器                           | true                                | 否       |
| loopDurationMillis   | Long                            | 每页停留多久后自动滚动                       | BannerDefaults.LoopDurationMillis   | 否       |
| scrollDurationMillis | Int                             | 一次自动滚动的时长                           | BannerDefaults.ScrollDurationMillis | 否       |
| cornerRadius         | Dp                              | 卡片圆角                                     | BannerDefaults.CornerRadius         | 否       |
| pageMargin           | Dp                              | 卡片间距及卡片距两侧的内缩                   | BannerDefaults.PageMargin           | 否       |
| startItemWidth       | Dp                              | 上一张卡片露出的宽度                         | 0.dp                                | 否       |
| endItemWidth         | Dp                              | 下一张卡片露出的宽度                         | 0.dp                                | 否       |
| listItemWidth        | Dp                              | 列表形态下的卡片宽度；未指定时一屏两张       | Dp.Unspecified                      | 否       |
| listItemSpacing      | Dp                              | 列表形态下的卡片间距                         | BannerDefaults.ListItemSpacing      | 否       |
| scalePages           | Boolean                         | 轮播形态下把相邻卡片缩放到 0.85              | false                               | 否       |
| indicatorColors      | PageIndicatorColors             | 页面指示器颜色                               | BannerDefaults.indicatorColors()    | 否       |
| onPageChange         | ((Int) -> Unit)?                | 当前页变化时以真实页码回调；自动滚动在落定到下一页时才回调 | null                                | 否       |
| pageContent          | @Composable (page: Int) -> Unit | 页面内容，参数为真实页码                     | -                                   | 是       |

### BannerDefaults

| 常量                  | 类型 | 默认值                    | COUI 来源                                      |
| --------------------- | ---- | ------------------------- | ---------------------------------------------- |
| LoopDurationMillis    | Long | 5000                      | `couiLoopDuration` 默认 5（秒）                |
| ScrollDurationMillis  | Int  | 950                       | `COUIBanner` 滚动时长                          |
| CornerRadius          | Dp   | CardDefaults.CornerRadius | 卡片列表圆角（COUI 把卡片形状交给应用）        |
| PageMargin            | Dp   | 24.dp                     | `COUIBannerUtil` 默认页间距                    |
| IndicatorBottomMargin | Dp   | 10.dp                     | coui_banner_content_layout `layout_marginBottom` |
| ListTypeBreakpoint    | Dp   | 600.dp                    | `COUIBanner` `screenWidthDp >= 600`              |
| ListItemSpacing       | Dp   | 8.dp                      | `COUIBannerRecyclerView` `coui_item_decoration_8dp` |
| MinPageScale          | Float | 0.85                     | `COUIScalePageTransformer`                       |

| 方法              | 说明                                                                                     |
| ----------------- | ---------------------------------------------------------------------------------------- |
| indicatorColors() | 针对背后图片的固定颜色：圆点 `#4DFFFFFF`（`coui_page_indicator_dot_color_in_dark_bg`），轨迹 `#E6FFFFFF` |
| type()            | 窗口宽度不小于 `ListTypeBreakpoint` 时返回 `BannerType.List`，否则返回 `BannerType.Pager` |

## 行为

- 开启 `infiniteLoop` 且页数不少于两页时，第一页之前放一份最后一页的副本、最后一页之后放一份第一页的副本；pager 停在副本上时无动画跳到对应的真实页。
- 自动滚动先等待 `loopDurationMillis + scrollDurationMillis`，再用 `scrollDurationMillis` 按 `PathInterpolator(0.2, 0, 0.1, 1)` 滚到下一页。触摸会取消待执行的滚动（已在进行的滚动会完成，除非手指拖动了页面），抬起手指后重新计时。与 COUI 的 fake drag 一致，自动滚动只在落定到新页时才通过 `onPageChange` 回调。
- 卡片两侧分别内缩 `startItemWidth + pageMargin` 与 `endItemWidth + pageMargin`，彼此相距 `pageMargin`。
- 指示器居中，距底部 10dp；其圆点不可点击。pager 经由副本回绕时，指示器先停在最后一个圆点再跳转。
