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

标准样式与 `COUITabLayout` 一致：48dp 高，标签文字 14sp（选中时为 Medium 字重），底部是左右各内缩 24dp 的 2dp 轨道，以及与选中标签文字等宽（至少 32dp）的 2dp 中性色指示器。切换选中项时，指示器以 `CubicBezierEasing(0.33f, 0f, 0.67f, 1f)` 在 `min(300, 50 × Δ标签数 + 150)` ms 内滑动，标签颜色同步渐变。每个标签带 8dp 圆角的按压遮罩，按下未选中的标签时触发一次触感反馈；两者都与 `COUITabView` 一样在手指按下时立即开始，标签行位于可滚动页面中时也不例外。

标签总宽能放进 360dp 内容宽度时等间距铺开；能放进整行时以 `itemSpacing` 为间距居中；否则整行可滚动，两端带 58dp 渐隐边缘，并让选中标签保持居中。可滚动时沿用 `COUIHorizontalScrollView` 的手感：拖过两端时只跟随 40% 并随越界距离（以屏宽为上限）逐渐变硬；惯性滑到端点会越界最多屏宽的 37% 再回弹。标签行本身不振动：`COUITabLayoutBaseStyle` 关闭了标签布局自身的触感反馈，因此滚动到端点和滑动 Pager 翻页都不会振动。

```kotlin
val tabs = listOf("推荐", "关注", "热门", "精选")
var selectedTabIndex by remember { mutableStateOf(0) }

TabRow(
    tabs = tabs,
    selectedTabIndex = selectedTabIndex,
    onTabSelected = { selectedTabIndex = it }
)
```

### 图标、红点与禁用标签

传入 `TabRowItem` 可为标签加上图标、红点或禁用状态。有标签同时带图标和文字时行高为 72dp；图标位于文字之前，红点位于文字之后并贴行顶对齐，指示器始终在文字下方。

```kotlin
val items = listOf(
    TabRowItem(text = "照片", icon = COUIIcons.Photos),
    TabRowItem(text = "音乐", icon = COUIIcons.Music, badge = 0),
    TabRowItem(text = "图片", icon = COUIIcons.Image, badge = 12),
    TabRowItem(text = "设置", icon = COUIIcons.Settings, enabled = false),
)
var selectedTabIndex by remember { mutableStateOf(0) }

TabRow(
    items = items,
    selectedTabIndex = selectedTabIndex,
    onTabSelected = { selectedTabIndex = it }
)
```

### 带轮廓样式

带轮廓样式与 ColorOS 分段按钮一致：40dp 高的胶囊容器，四边 4dp 内缩的滑动指示器。分段宽度由文字自然宽度（至少 52dp，所有标签都用 Medium 字重的 `couiTextButtonM`）计算后铺满整行。

指示器沿用与 [SegmentButton](./segmentbutton) 相同的 COUI 17 分段按钮弹簧：位置（response 0.4，bounce 0.1）、宽度（按新旧宽度差异在 response 0.6 → 0.4、bounce 0.5 → 0.1 之间取值），以及先压到 90% 再弹回的高度。按下的分段从手指按下起以 COUI 17 按压缩放收缩，按住选中分段时指示器也一起压下。拖动指示器切换分段和 COUI 17 材质效果见 `SegmentButton`。

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

| 属性名            | 类型                      | 说明                                                                 | 默认值                           | 是否必须 |
| ----------------- | ------------------------- | -------------------------------------------------------------------- | -------------------------------- | -------- |
| tabs / items      | List\<String> / List\<TabRowItem> | 标签文本，或带图标、红点、禁用状态的标签                     | -                                | 是       |
| selectedTabIndex  | Int                       | 当前选中的标签索引                                                   | -                                | 是       |
| onTabSelected     | (Int) -> Unit             | 标签选中时的回调函数                                                 | -                                | 是       |
| modifier          | Modifier                  | 应用于标签行的修饰符                                                 | Modifier                         | 否       |
| enabled           | Boolean                   | 标签是否可选；禁用时文字与指示器置灰                                 | true                             | 否       |
| colors            | TabRowColors              | 标签行的颜色配置                                                     | TabRowDefaults.underlineColors() | 否       |
| minWidth          | Dp                        | 每个标签内容的最小宽度                                               | TabRowDefaults.TabRowMinWidth    | 否       |
| maxWidth          | Dp                        | 每个标签内容的最大宽度，超长文字省略                                 | TabRowDefaults.TabRowMaxWidth    | 否       |
| height            | Dp                        | 标签行高度；`Dp.Unspecified` 时为 48dp，有标签同时带图标和文字时 72dp | Dp.Unspecified                   | 否       |
| cornerRadius      | Dp                        | 标签按压遮罩的圆角半径                                               | TabRowDefaults.TabRowCornerRadius | 否      |
| itemSpacing       | Dp                        | 标签之间的最小间距；`Dp.Unspecified` 时小屏 20dp，其余 24dp          | Dp.Unspecified                   | 否       |
| horizontalMargin  | Dp                        | 两端最小边距；`Dp.Unspecified` 时按屏幕尺寸取 16dp、24dp 或 40dp     | Dp.Unspecified                   | 否       |
| contentAlignment  | Alignment                 | 标签内容在标签内的对齐方式                                           | Alignment.Center                 | 否       |
| listState         | LazyListState?            | 标签列表的外部滚动状态                                               | null                             | 否       |
| interactionSource | MutableInteractionSource? | 标签项的交互源                                                       | null                             | 否       |
| indication        | Indication?               | 标签项的按压遮罩，按 `cornerRadius` 裁切                             | LocalIndication.current          | 否       |
| pagerState        | PagerState?               | 用户拖动时标签行跟随的 Pager                                         | null                             | 否       |

屏幕尺寸沿用 COUI 的窗口分级：宽度小于 600dp 为小屏；宽度 ≥840dp 且窗口高度在 480dp 到 900dp 之间（或高度 ≥900dp 且不是宽度小于 960dp 的竖屏）为大屏；其余为中屏。与 `COUISlidingTabStrip` 一致，边距按行宽和窗口高度判断，间距按行宽和行高判断。

### TabRowItem

| 属性名  | 类型         | 说明                                                   | 默认值 |
| ------- | ------------ | ------------------------------------------------------ | ------ |
| text    | String       | 标签文字；纯图标标签可为空                             | -      |
| icon    | ImageVector? | 文字前的 24dp 图标，着色与文字相同                     | null   |
| badge   | Int?         | 文字后的红点：`null` 不显示，`0` 为圆点，正数显示数字  | null   |
| enabled | Boolean      | 该标签是否可选                                         | true   |

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
| TabRowHeight                      | Dp   | 48.dp       | 纯文字标签时标准样式的高度                               |
| TabRowIconHeight                  | Dp   | 72.dp       | 有标签同时带图标和文字时标准样式的高度                   |
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
| indicatorColor       | Color | 2dp 指示器的颜色         | COUITheme.colorScheme.labelPrimary          |
| contentColor         | Color | 未选中标签的文字颜色     | COUITheme.colorScheme.labelSecondary |
| selectedContentColor | Color | 选中及按下标签的文字颜色 | COUITheme.colorScheme.labelPrimary          |
| disabledContentColor | Color | 禁用标签的文字颜色       | COUITheme.colorScheme.disabledNeutral  |
| disabledIndicatorColor | Color | 禁用标签行的指示器颜色 | `#FFD9D9D9`                              |

### TabRowColors 类

| 属性名                  | 类型  | 说明               |
| ----------------------- | ----- | ------------------ |
| backgroundColor         | Color | 容器背景色（`TabRowWithContour`）或轨道颜色（`TabRow`） |
| contentColor            | Color | 标签的默认内容色   |
| selectedBackgroundColor | Color | 滑动指示器的颜色   |
| selectedContentColor    | Color | 选中标签的内容色   |
| disabledContentColor    | Color | 禁用标签的文字颜色（`TabRow`），默认同 `contentColor` |
| disabledSelectedBackgroundColor | Color | 禁用标签行的指示器颜色（`TabRow`），默认同 `selectedBackgroundColor` |

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

把 `PagerState` 传给 `pagerState`，用户拖动 Pager 时标签行会像 `COUITabLayout.setScrollPosition` 一样跟随：指示器在两个标签之间滑动，标签颜色在驶向下一页的后半程渐变（一次拖过多页时逐页渐变），标签行同步滚动。滑动翻页不触发触感反馈；点击标签仍使用自身的指示器动画。

```kotlin
val tabs = listOf("页面1", "页面2", "页面3")
val pagerState = rememberPagerState { tabs.size }
val scope = rememberCoroutineScope()

Surface {
    Column {
        TabRow(
            tabs = tabs,
            selectedTabIndex = pagerState.targetPage,
            onTabSelected = { scope.launch { pagerState.animateScrollToPage(it) } },
            pagerState = pagerState
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
