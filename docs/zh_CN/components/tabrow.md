# TabRow

`TabRow` 是 COUI 中的导航组件，适用于内容分类和导航场景。提供两种变体：标准样式复刻 ColorOS 的滑动标签栏（`COUITabLayout`：文字标签 + 细轨道 + 滑动下划线指示器），带轮廓（Contour）样式复刻 ColorOS 的分段按钮（`COUISegmentButtonLayout`：胶囊容器 + 滑动胶囊指示器）。

<div style="position: relative; height: 180px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=tabRow" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.TabRow // 标准样式（滑动标签栏）
import io.github.suqi8.coui.kmp.basic.TabRowWithContour // 带轮廓样式（分段按钮）
```

## 基本用法

### 标准样式

标准样式与 `COUITabLayout` 一致：48dp 高，标签文字 14sp（选中时为 Medium 字重），底部是左右各内缩 24dp 的 2dp 轨道，以及与选中标签文字等宽（至少 32dp）的 2dp 中性色指示器。切换选中项时，指示器以 `CubicBezierEasing(0.33f, 0f, 0.67f, 1f)` 在 `min(300, 50 × Δ标签数 + 150)` ms 内滑动，标签颜色同步渐变。每个标签带 8dp 圆角的按压遮罩，按下未选中的标签时触发一次触感反馈。

标签总宽能放进 360dp 内容宽度时等间距铺开；能放进整行时以 `itemSpacing` 为间距居中；否则整行可滚动，两端带 58dp 渐隐边缘，并让选中标签保持居中。

```kotlin
val tabs = listOf("推荐", "关注", "热门", "精选")
var selectedTabIndex by remember { mutableStateOf(0) }

TabRow(
    tabs = tabs,
    selectedTabIndex = selectedTabIndex,
    onTabSelected = { selectedTabIndex = it }
)
```

### 带轮廓样式

带轮廓样式与 ColorOS 分段按钮一致：40dp 高的胶囊容器，四边 4dp 内缩的滑动指示器。分段宽度由文字自然宽度（至少 52dp）计算后铺满整行。

```kotlin
val tabs = listOf("全部", "照片", "视频", "文档")
var selectedTabIndex by remember { mutableStateOf(0) }

TabRowWithContour(
    tabs = tabs,
    selectedTabIndex = selectedTabIndex,
    onTabSelected = { selectedTabIndex = it }
)
```

### 记住滚动位置

两种样式在标签放不下时都会变为可滚动。传入 `LazyListState` 可保留滚动位置。

```kotlin
val tabs = listOf("标签1", "标签2", "标签3", "标签4", "标签5")
var selectedTabIndex by remember { mutableStateOf(3) }
val tabListState = rememberLazyListState()

TabRowWithContour(
    tabs = tabs,
    selectedTabIndex = selectedTabIndex,
    onTabSelected = { selectedTabIndex = it },
    listState = tabListState,
)
```

## 属性

### TabRow 属性

| 属性名            | 类型                      | 说明                   | 默认值                                                           | 是否必须 |
| ----------------- | ------------------------- | ---------------------- | ---------------------------------------------------------------- | -------- |
| tabs              | List\<String>             | 标签文本列表           | -                                                                | 是       |
| selectedTabIndex  | Int                       | 当前选中的标签索引     | -                                                                | 是       |
| onTabSelected     | (Int) -> Unit             | 标签选中时的回调函数   | -                                                                | 是       |
| modifier          | Modifier                  | 应用于标签行的修饰符   | Modifier                                                         | 否       |
| colors            | TabRowColors              | 标签行的颜色配置                                                       | TabRowDefaults.underlineColors()  | 否       |
| minWidth          | Dp                        | 每个标签文字区的最小宽度                                               | TabRowDefaults.TabRowMinWidth     | 否       |
| maxWidth          | Dp                        | 每个标签文字区的最大宽度，超长文字省略                                 | TabRowDefaults.TabRowMaxWidth     | 否       |
| height            | Dp                        | 标签行的高度                                                           | TabRowDefaults.TabRowHeight       | 否       |
| cornerRadius      | Dp                        | 标签按压遮罩的圆角半径                                                 | TabRowDefaults.TabRowCornerRadius | 否       |
| itemSpacing       | Dp                        | 标签文字之间的最小间距；`Dp.Unspecified` 时为 20dp，行宽 ≥600dp 时 24dp | Dp.Unspecified                    | 否       |
| horizontalMargin  | Dp                        | 两端最小边距；`Dp.Unspecified` 时为 16dp，≥600dp 时 24dp，≥840dp 时 40dp | Dp.Unspecified                  | 否       |
| contentAlignment  | Alignment                 | 标签文字在标签内的对齐方式                                             | Alignment.Center                  | 否       |
| listState         | LazyListState?            | 标签列表的外部滚动状态                                                 | null                              | 否       |
| interactionSource | MutableInteractionSource? | 标签项的交互源                                                         | null                              | 否       |
| indication        | Indication?               | 标签项的按压遮罩，按 `cornerRadius` 裁切                               | LocalIndication.current           | 否       |

### TabRowWithContour 属性

| 属性名            | 类型                      | 说明                       | 默认值                                       | 是否必须 |
| ----------------- | ------------------------- | -------------------------- | -------------------------------------------- | -------- |
| tabs              | List\<String>             | 标签文本列表               | -                                            | 是       |
| selectedTabIndex  | Int                       | 当前选中的标签索引         | -                                            | 是       |
| onTabSelected     | (Int) -> Unit             | 标签选中时的回调函数       | -                                            | 是       |
| modifier          | Modifier                  | 应用于标签行的修饰符       | Modifier                                     | 否       |
| colors            | TabRowColors              | 标签行的颜色配置           | TabRowDefaults.tabRowColors()                | 否       |
| minWidth          | Dp                        | 每个标签的最小宽度         | TabRowDefaults.TabRowWithContourMinWidth     | 否       |
| maxWidth          | Dp                        | 每个标签的最大宽度         | TabRowDefaults.TabRowWithContourMaxWidth     | 否       |
| height            | Dp                        | 标签行的高度               | TabRowDefaults.TabRowWithContourHeight       | 否       |
| cornerRadius      | Dp                        | 滑动指示器的圆角半径       | TabRowDefaults.TabRowWithContourCornerRadius | 否       |
| contourPadding    | Dp                        | 容器与滑动指示器间的内缩   | TabRowDefaults.TabRowWithContourPadding      | 否       |
| itemSpacing       | Dp                        | 标签之间的间距             | 0.dp                                         | 否       |
| contentAlignment  | Alignment                 | 标签内容的对齐方式         | Alignment.Center                             | 否       |
| listState         | LazyListState?            | 标签列表的外部滚动状态     | null                                         | 否       |
| interactionSource | MutableInteractionSource? | 标签项的交互源             | null                                         | 否       |
| indication        | Indication?               | 标签项的点击反馈效果       | null                                         | 否       |

### TabRowDefaults 对象

TabRowDefaults 对象提供了 TabRow 组件的默认配置。

#### 常量

| 常量名                            | 类型 | 值          | 说明                                                     |
| --------------------------------- | ---- | ----------- | -------------------------------------------------------- |
| TabRowHeight                      | Dp   | 48.dp       | 标准样式的默认高度                                       |
| TabRowWithContourHeight           | Dp   | 40.dp       | 带轮廓样式的默认高度                                     |
| TabRowWithContourTinyHeight       | Dp   | 32.dp       | COUI `SegmentButton.Tiny` 样式的高度                     |
| TabRowCornerRadius                | Dp   | 8.dp        | 标准样式标签按压遮罩的圆角半径                           |
| TabRowWithContourCornerRadius     | Dp   | 16.dp       | 带轮廓样式滑块的默认圆角半径（胶囊形）                   |
| TabRowWithContourTinyCornerRadius | Dp   | 14.dp       | `SegmentButton.Tiny` 样式滑块的圆角半径                  |
| TabRowWithContourPadding          | Dp   | 4.dp        | 容器与滑块之间的默认内缩                                 |
| TabRowWithContourTinyPadding      | Dp   | 2.dp        | `SegmentButton.Tiny` 样式的容器内缩                      |
| TabRowMinWidth                    | Dp   | 0.dp        | 标准样式标签文字区的最小宽度                             |
| TabRowWithContourMinWidth         | Dp   | 52.dp       | 带轮廓样式的每个标签最小宽度                             |
| TabRowMaxWidth                    | Dp   | Dp.Unspecified | 标准样式标签文字区的最大宽度（行宽的 70%；`Dp.Infinity` 表示不限） |
| TabRowWithContourMaxWidth         | Dp   | Dp.Infinity | 带轮廓样式的每个标签最大宽度（无上限，标签铺满整行）     |

#### 方法

| 方法名            | 类型         | 说明                                   |
| ----------------- | ------------ | -------------------------------------- |
| underlineColors() | TabRowColors | 创建 `TabRow` 的默认颜色配置           |
| tabRowColors()    | TabRowColors | 创建 `TabRowWithContour` 的默认颜色配置 |

`underlineColors()` 参数：

| 参数名               | 类型  | 说明                     | 默认值                                   |
| -------------------- | ----- | ------------------------ | ---------------------------------------- |
| trackColor           | Color | 2dp 轨道的颜色           | `#0F000000`（浅色）/ `#19FFFFFF`（深色） |
| indicatorColor       | Color | 2dp 指示器的颜色         | COUITheme.colorScheme.onSurface          |
| contentColor         | Color | 未选中标签的文字颜色     | COUITheme.colorScheme.onSurfaceSecondary |
| selectedContentColor | Color | 选中及按下标签的文字颜色 | COUITheme.colorScheme.onSurface          |

### TabRowColors 类

| 属性名                  | 类型  | 说明               |
| ----------------------- | ----- | ------------------ |
| backgroundColor         | Color | 容器背景色（`TabRowWithContour`）或轨道颜色（`TabRow`） |
| contentColor            | Color | 标签的默认内容色   |
| selectedBackgroundColor | Color | 滑动指示器的颜色   |
| selectedContentColor    | Color | 选中标签的内容色   |

## 进阶用法

### 自定义颜色

```kotlin
val tabs = listOf("最新", "热门", "关注")
var selectedTabIndex by remember { mutableStateOf(0) }

TabRow(
    tabs = tabs,
    selectedTabIndex = selectedTabIndex,
    onTabSelected = { selectedTabIndex = it },
    colors = TabRowDefaults.underlineColors(
        indicatorColor = COUITheme.colorScheme.primary,
        selectedContentColor = COUITheme.colorScheme.primary
    )
)
```

### Tiny 变体

```kotlin
val tabs = listOf("照片", "视频", "人像")
var selectedTabIndex by remember { mutableStateOf(0) }

TabRowWithContour(
    tabs = tabs,
    selectedTabIndex = selectedTabIndex,
    onTabSelected = { selectedTabIndex = it },
    height = TabRowDefaults.TabRowWithContourTinyHeight,
    cornerRadius = TabRowDefaults.TabRowWithContourTinyCornerRadius,
    contourPadding = TabRowDefaults.TabRowWithContourTinyPadding
)
```

### 与 Pager 结合使用

```kotlin
val tabs = listOf("页面1", "页面2", "页面3")
val pagerState = rememberPagerState { tabs.size }
val scope = rememberCoroutineScope()

Surface {
    Column {
        TabRow(
            tabs = tabs,
            selectedTabIndex = pagerState.targetPage,
            onTabSelected = { scope.launch { pagerState.animateScrollToPage(it) } }
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("页面内容 ${page + 1}")
            }
        }
    }
}
```
