# PageIndicator

`PageIndicator` 复刻 ColorOS 的页面指示器（`COUIPageIndicator` / `COUIPageIndicator2`）：用于轮播与引导页的一排 6dp 圆点。页面滚动时，当前圆点向下一个圆点拉伸并跳过去（轨迹"蠕虫"动画），两个圆点在圆点色与轨迹色之间渐变。页数超过五页时只显示六个圆点：四个完整圆点，两侧是更小的圆点，可见窗口跟随当前页移动时它们会放大或缩小。

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=pageIndicator" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.PageIndicator
import io.github.suqi8.coui.kmp.basic.PageIndicatorDefaults
```

## 基本用法

跟随 Compose 的 `PagerState`；点击指示器会把 pager 滚动到最近的圆点：

```kotlin
val pagerState = rememberPagerState { 4 }

HorizontalPager(state = pagerState) { page ->
    // 页面内容
}
PageIndicator(pagerState = pagerState)
```

## 直接传入位置

不使用 pager 时，传入当前页（拖动过程中再传入朝下一页的偏移）。页码变化时，轨迹按 COUI 的位置弹簧（阻尼比 1，刚度 1500）播放：

```kotlin
var current by remember { mutableIntStateOf(0) }

PageIndicator(
    pageCount = 12,
    currentPage = current,
    onDotClick = { current = it },
)
```

## 属性

### PageIndicator（PagerState）

| 属性名       | 类型                 | 说明                                         | 默认值                           | 是否必须 |
| ------------ | -------------------- | -------------------------------------------- | -------------------------------- | -------- |
| pagerState   | PagerState           | 指示器跟随的 pager                           | -                                | 是       |
| modifier     | Modifier             | 应用于指示器的修饰符                         | Modifier                         | 否       |
| pageCount    | Int                  | 圆点数量                                     | pagerState.pageCount             | 否       |
| dotClickable | Boolean              | 点击是否选中最近的圆点                       | true                             | 否       |
| colors       | PageIndicatorColors  | 圆点与轨迹颜色                               | PageIndicatorDefaults.colors()   | 否       |
| onDotClick   | ((Int) -> Unit)?     | 点击圆点时回调；为 null 时把 pager 滚动过去  | null                             | 否       |

### PageIndicator（位置）

| 属性名                    | 类型                | 说明                                   | 默认值                         | 是否必须 |
| ------------------------- | ------------------- | -------------------------------------- | ------------------------------ | -------- |
| pageCount                 | Int                 | 圆点数量                               | -                              | 是       |
| currentPage               | Int                 | 当前页                                 | -                              | 是       |
| modifier                  | Modifier            | 应用于指示器的修饰符                   | Modifier                       | 否       |
| currentPageOffsetFraction | Float               | 朝下一页的偏移，0 到 1                 | 0f                             | 否       |
| animated                  | Boolean             | 页码变化时是否按位置弹簧播放轨迹       | true                           | 否       |
| colors                    | PageIndicatorColors | 圆点与轨迹颜色                         | PageIndicatorDefaults.colors() | 否       |
| onDotClick                | ((Int) -> Unit)?    | 以离点击处最近的圆点回调；null 不可点击 | null                           | 否       |

### PageIndicatorDefaults

| 常量          | 类型 | 默认值 | COUI 来源                                        |
| ------------- | ---- | ------ | ------------------------------------------------ |
| DotSize       | Dp   | 6.dp   | coui_page_indicator_dot_size（也是指示器高度）   |
| MediumDotSize | Dp   | 4.5.dp | coui_page_indicator_dot_size_medium              |
| SmallDotSize  | Dp   | 2.5.dp | coui_page_indicator_dot_size_small               |
| DotSpacing    | Dp   | 3.dp   | coui_page_indicator_dot_spacing（加倍：圆点间距 6dp） |

| 方法     | 说明                                                                                           |
| -------- | ---------------------------------------------------------------------------------------------- |
| colors() | 圆点 `?couiColorHintNeutral`（`onSurfaceContainerHigh`），轨迹 `?couiColorLabelPrimary`（`onSurfaceContainer`） |

### PageIndicatorColors

| 属性名     | 类型  | 说明                 |
| ---------- | ----- | -------------------- |
| dotColor   | Color | 圆点颜色             |
| traceColor | Color | 当前圆点与轨迹颜色   |

## 行为

- 每个圆点占 12dp：指示器宽 `min(6, pageCount) x 12dp`，高 6dp。
- 轨迹在一页的 5% 之后开始拉伸，到 95% 完全过去；中间的颈部按 `COUIEaseInterpolator`（`PathInterpolator(0.33, 0, 0.67, 1)`）变化，首尾 5% 内两个圆点颜色渐变。
- 从右到左布局时指示器镜像。
- 超出最后一页的位置（过度滚动，或循环轮播回绕时）保持最后一个有效位置。
