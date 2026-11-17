# ScrollBar

`VerticalScrollBar` 是 COUI 17 的滚动条（`COUIScrollBar`）：内容滚动时出现的细滑块，最后一次滚动后停留 2 秒再淡出，可以拖动滑块来滚动内容。`HorizontalScrollBar` 是同一滑块的横向布局。

::: warning 实验性
滚动条 API 标注了 `@ExperimentalScrollBarApi`，使用时需 `@OptIn(ExperimentalScrollBarApi::class)`。
:::

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.VerticalScrollBar
import io.github.suqi8.coui.kmp.basic.HorizontalScrollBar
import io.github.suqi8.coui.kmp.basic.rememberScrollBarAdapter
import io.github.suqi8.coui.kmp.basic.ScrollBarDefaults
import io.github.suqi8.coui.kmp.interfaces.ExperimentalScrollBarApi
```

## 基本用法

把滚动条叠放在可滚动内容上方，用 `ScrollBarAdapter` 连接二者。已提供 `ScrollState`、`LazyListState`、`LazyGridState` 三种适配器：

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

## 行为

- 滑块矩形宽 14dp（`coui_scrollbar_wight`），长度至少 48dp（`coui_scrollbar_min_height`）。可见滑块绘制在矩形内部，距起点 8dp、距终点 16dp（`coui_scrollbar_margin_top` / `_bottom`），距边缘 5.5dp，因此宽 3dp、至少长 24dp。
- 内容一滚动就立即出现，最后一次滚动或拖动后停留 2000ms（`SCROLLER_FADE_TIMEOUT`，首次出现时为平台延迟的 4 倍），然后在 250ms 内线性淡出。
- 滑块可见时可以抓住拖动：按下立即变宽到 6dp（按下内边距 4dp），拖动时滚动内容，松手后重新计时淡出。
- 滑块颜色 `#29000000` / `#40FFFFFF`（`coui_scrollbar_color`），按下时不变。

::: info 库扩展
`COUIScrollBar` 只处理竖向、也不绘制轨道。`HorizontalScrollBar`、`ScrollBarColors.trackColor`（默认隐藏）以及鼠标悬停时滑块加宽是本库的扩展。
:::

## 属性

### VerticalScrollBar / HorizontalScrollBar 属性

| 属性名         | 类型             | 说明                                               | 默认值                              | 是否必须 |
| -------------- | ---------------- | -------------------------------------------------- | ----------------------------------- | -------- |
| adapter        | ScrollBarAdapter | 连接滚动条与可滚动组件                             | -                                   | 是       |
| modifier       | Modifier         | 应用于滚动条的修饰符                               | Modifier                            | 否       |
| reverseLayout  | Boolean          | 反转滚动与布局方向                                 | false                               | 否       |
| trackPadding   | PaddingValues    | 轨道内边距，用于跳过内容的 padding 区域            | PaddingValues(0.dp)                 | 否       |
| colors         | ScrollBarColors  | 滚动条颜色                                         | ScrollBarDefaults.scrollBarColors() | 否       |
| thumbWidth     | Dp               | 可见滑块宽度                                       | ScrollBarDefaults.ThumbWidth        | 否       |
| cornerRadius   | Dp               | 圆角半径；`Dp.Unspecified` 时取 `thumbWidth` 的一半 | ScrollBarDefaults.CornerRadius      | 否       |
| thumbMinLength | Dp               | 滑块矩形的最小长度（含边距）                       | ScrollBarDefaults.ThumbMinLength    | 否       |
| endPadding     | Dp               | 距末端边缘的距离                                   | ScrollBarDefaults.EndPadding        | 否       |

### rememberScrollBarAdapter

| 重载                                                 | 说明                                  |
| ---------------------------------------------------- | ------------------------------------- |
| rememberScrollBarAdapter(scrollState: ScrollState)   | 用于 `verticalScroll` / `horizontalScroll` |
| rememberScrollBarAdapter(scrollState: LazyListState) | 用于 `LazyColumn` / `LazyRow`         |
| rememberScrollBarAdapter(scrollState: LazyGridState) | 用于懒加载网格                        |

### ScrollBarDefaults 对象

| 常量名                 | 类型 | 默认值         | 说明                                                  |
| ---------------------- | ---- | -------------- | ----------------------------------------------------- |
| ThumbWidth             | Dp   | 3.dp           | 可见滑块宽度                                          |
| EndPadding             | Dp   | 5.5.dp         | 距末端边缘的距离（`coui_scrollbar_drawable_default_inset`） |
| ThumbMinLength         | Dp   | 48.dp          | 滑块矩形最小长度（`coui_scrollbar_min_height`）       |
| ThumbMarginStart       | Dp   | 8.dp           | 可见滑块距矩形起点的距离                              |
| ThumbMarginEnd         | Dp   | 16.dp          | 可见滑块距矩形终点的距离                              |
| CornerRadius           | Dp   | Dp.Unspecified | 圆角半径；未指定时取滑块宽度的一半                    |
| FadeDelayMillis        | Int  | 2000           | 最后一次滚动或拖动后的停留时间                        |
| InitialFadeDelayMillis | Int  | 1200           | 首次出现时的停留时间                                  |
| FadeDurationMillis     | Int  | 250            | 线性淡出时长                                          |
| TouchTargetWidth       | Dp   | 14.dp          | 可开始拖动滑块的条带宽度                              |
| DragThumbWidth         | Dp   | 6.dp           | 拖动时的可见滑块宽度                                  |
| DragEndPadding         | Dp   | 4.dp           | 拖动时距末端边缘的距离                                |

| 方法名            | 类型            | 说明                                              |
| ----------------- | --------------- | ------------------------------------------------- |
| scrollBarColors() | ScrollBarColors | 创建颜色配置；未指定滑块色时使用主题的拖动手柄色 |

### ScrollBarColors 类

| 属性名     | 类型  | 说明                                         |
| ---------- | ----- | -------------------------------------------- |
| thumbColor | Color | 滑块颜色；`Color.Unspecified` 使用主题默认值 |
| trackColor | Color | 轨道颜色；`Color.Unspecified` 隐藏轨道       |
