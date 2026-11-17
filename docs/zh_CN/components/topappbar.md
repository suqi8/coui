# TopAppBar

`TopAppBar` 是 COUI 中的顶部应用栏组件，用于在界面顶部提供导航、标题和操作按钮。支持大标题模式和普通模式，以及滚动时的动态效果。

此组件通常与 `Scaffold` 组件结合使用，以便在应用程序的不同页面中保持一致的布局和行为。

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=topAppBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.TopAppBar
import io.github.suqi8.coui.kmp.basic.SmallTopAppBar
import io.github.suqi8.coui.kmp.basic.TopAppBarSearchSlot
import io.github.suqi8.coui.kmp.basic.COUIScrollBehavior
import io.github.suqi8.coui.kmp.basic.rememberTopAppBarState
```

## 基本用法

### 小标题顶部栏

```kotlin
Scaffold(
    topBar = {
        SmallTopAppBar(
            title = "标题",
            navigationIcon = {
                IconButton(onClick = { /* 处理点击事件 */ }) {
                    Icon(COUIIcons.Back, contentDescription = "返回")
                }
            },
            actions = {
                IconButton(onClick = { /* 处理点击事件 */ }) {
                    Icon(COUIIcons.More, contentDescription = "更多")
                }
            }
        )
    }
)
```

### 大标题顶部栏

```kotlin
Scaffold(
    topBar = {
        TopAppBar(
            title = "标题",
            largeTitle = "大标题", // 如果不指定，将使用 title 的值
            navigationIcon = {
                IconButton(onClick = { /* 处理点击事件 */ }) {
                    Icon(COUIIcons.Back, contentDescription = "返回")
                }
            },
            actions = {
                IconButton(onClick = { /* 处理点击事件 */ }) {
                    Icon(COUIIcons.More, contentDescription = "更多")
                }
            }
        )
    }
)
```

`SmallTopAppBar` 是独立的 COUI 工具栏，标题使用 `TextAppearance.COUI.Toolbar.SecondTitle`（18dp、中等字重）；可折叠的 `TopAppBar` 折叠后标题为 24dp。

### 居中标题

`centerTitle = true` 让 `SmallTopAppBar` 的标题和副标题居中（COUI `setIsTitleCenterStyle`）。能在导航图标与操作按钮之间对称放下时以整条栏居中，否则在两者之间剩余的空间内居中：

```kotlin
SmallTopAppBar(
    title = "标题",
    centerTitle = true,
    navigationIcon = { /* ... */ },
    actions = {
        ToolbarTextButton(text = "完成", onClick = { /* 处理点击事件 */ })
    }
)
```

### 文字按钮与红点

在 `actions` 中放 `ToolbarTextButton` 即为文字菜单项（宽 60–104dp，14dp 主题色文字）。`IconButton` 通过 `redDot` 显示红点（`0` 为纯红点，正数为数字）；参见 [IconButton](./iconbutton)。

### 带副标题

两种顶部栏都可以在标题下方显示副标题。可折叠的 `TopAppBar` 中副标题默认随折叠渐隐；设置 `hideSubtitleOnCollapse = false` 可让其保持可见并滑入折叠栏标题下方，此时折叠标题由 24dp 缩为 18dp：

```kotlin
TopAppBar(
    title = "标题",
    largeTitle = "大标题",
    subtitle = "副标题",
    hideSubtitleOnCollapse = false
)
```

### 标题下方的搜索胶囊

`TopAppBarSearchSlot` 是 COUI 17 设置首页的搜索行。把它放进 `bottomContent`：它排出一行 52dp（`coui_search_view_height`）、带 16dp 页边距的区域，并随展开态标题区域一起折叠收起。通过具名参数 `inputField` 直接放一个 `InputField`；不要在里面再包一整个 `SearchBar`，因为槽位已经为胶囊留了 16dp 边距，搜索栏会再叠加一层自己的边距：

```kotlin
var query by remember { mutableStateOf("") }

TopAppBar(
    title = "设置",
    bottomContent = {
        TopAppBarSearchSlot(
            inputField = {
                InputField(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = { /* 处理搜索 */ },
                    expanded = false,
                    onExpandedChange = { /* 打开搜索页 */ },
                    label = "搜索"
                )
            }
        )
    }
)
```

## 大标题顶部栏滚动行为（使用脚手架）

TopAppBar 支持随内容滚动时改变其显示状态：

```kotlin
val scrollBehavior = COUIScrollBehavior()

Scaffold(
    topBar = {
        TopAppBar(
            title = "标题",
            largeTitle = "大标题", // 如果不指定，将使用 title 的值
            scrollBehavior = scrollBehavior
        )
    }
) { paddingValues ->
    // 内容区域需要考虑 padding
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            // 如需添加越界回弹效果，则应在绑定滚动行为之前添加
            .overScrollVertical()
            // 绑定 TopAppBar 滚动事件
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        contentPadding = PaddingValues(top = paddingValues.calculateTopPadding())
    ) {
        // 列表内容
    }
}
```

## 自定义样式

### 自定义颜色

```kotlin
TopAppBar(
    title = "标题",
    color = COUITheme.colorScheme.primary,
    titleColor = COUITheme.colorScheme.onPrimary,
    largeTitleColor = COUITheme.colorScheme.onPrimary
)
```

### 自定义内容边距

默认情况下标题、导航图标与操作按钮的边距像 COUI 一样随栏宽分档：紧凑窗口 16dp、中等窗口 24dp、展开窗口 40dp（`TopAppBarDefaults.horizontalPadding`）。传入具体值即可固定：

```kotlin
TopAppBar(
    title = "标题",
    titlePadding = 32.dp
)
```

### 自定义图标边距

```kotlin
TopAppBar(
    title = "标题",
    navigationIconPadding = 12.dp,
    actionIconPadding = 12.dp
)
```

## 属性

### TopAppBar 属性

| 属性名                     | 类型                            | 说明                           | 默认值                            | 是否必须 |
| -------------------------- | ------------------------------- | ------------------------------ | --------------------------------- | -------- |
| title                      | String                          | 顶部栏标题                     | -                                 | 是       |
| modifier                   | Modifier                        | 应用于顶部栏的修饰符           | Modifier                          | 否       |
| color                      | Color                           | 顶部栏背景颜色                 | COUITheme.colorScheme.backgroundWithCard    | 否       |
| titleColor                 | Color                           | 折叠时小标题文字颜色           | COUITheme.colorScheme.labelPrimary  | 否       |
| largeTitle                 | String                          | 大标题文本                     | title                             | 否       |
| largeTitleColor            | Color                           | 展开时大标题文字颜色           | COUITheme.colorScheme.labelPrimary  | 否       |
| subtitle                   | String                          | 显示在标题栏下方的副标题文本   | ""                                | 否       |
| subtitleColor              | Color                           | 副标题文字颜色                 | COUITheme.colorScheme.labelSecondaryVariant  | 否       |
| dividerColor               | Color                           | 折叠时显现的底部细分割线颜色   | COUITheme.colorScheme.divider | 否       |
| navigationIcon             | @Composable () -> Unit          | 导航图标区域的可组合函数       | {}                                | 否       |
| actions                    | @Composable RowScope.() -> Unit | 操作按钮区域的可组合函数（建议使用 24dp 图标） | {}                                | 否       |
| scrollBehavior             | ScrollBehavior?                 | 控制顶部栏滚动行为             | null                              | 否       |
| defaultWindowInsetsPadding | Boolean                         | 是否应用默认窗口边距           | true                              | 否       |
| showDivider                | Boolean                         | 是否绘制随折叠渐显的底部细分割线；设置了 `glow` 时始终隐藏 | true                              | 否       |
| hideSubtitleOnCollapse     | Boolean                         | 副标题是否随折叠渐隐；为 false 时保持不透明并滑入折叠栏标题下方，折叠标题为 18dp | true                              | 否       |
| titlePadding          | Dp                              | 水平内容边距；未指定时随栏宽分档（16 / 24 / 40dp） | Dp.Unspecified | 否       |
| navigationIconPadding      | Dp                              | 导航图标的起始边距；未指定时随栏宽分档 | Dp.Unspecified | 否       |
| actionIconPadding          | Dp                              | 操作图标的末尾边距；未指定时随栏宽分档 | Dp.Unspecified | 否       |
| expandedHeight             | Dp                              | 无副标题时的展开高度           | TopAppBarDefaults.ExpandedHeight | 否       |
| largeTitleFontSize         | TextUnit                        | 展开态大标题字号               | TopAppBarDefaults.largeTitleFontSize() (32dp) | 否       |
| largeTitleBottomMargin     | Dp                              | 指定后大标题按此边距贴展开态底部，而不是用顶部边距 | Dp.Unspecified | 否       |
| fadeLargeTitleOnCollapse   | Boolean                         | 折叠时让大标题淡出缩小，而不是变形为折叠标题（COUI 17 设置首页） | false | 否       |
| glow                       | (@Composable BoxScope.() -> Unit)? | 顶栏背后的衬底层，例如配合透明 `color` 使用的 `TopAppBarGlow`；`null` 为实色顶栏 | null | 否       |
| bottomContent              | @Composable () -> Unit          | 显示在标题栏下方的可组合内容 | {}                                | 否       |

### SmallTopAppBar 属性

| 属性名                     | 类型                            | 说明                     | 默认值                            | 是否必须 |
| -------------------------- | ------------------------------- | ------------------------ | --------------------------------- | -------- |
| title                      | String                          | 顶部栏标题               | -                                 | 是       |
| modifier                   | Modifier                        | 应用于顶部栏的修饰符     | Modifier                          | 否       |
| color                      | Color                           | 顶部栏背景颜色           | COUITheme.colorScheme.backgroundWithCard    | 否       |
| titleColor                 | Color                           | 标题文字颜色             | COUITheme.colorScheme.labelPrimary  | 否       |
| subtitle                   | String                          | 显示在标题栏下方的副标题文本 | ""                                | 否       |
| subtitleColor              | Color                           | 副标题文字颜色           | COUITheme.colorScheme.labelSecondaryVariant  | 否       |
| dividerColor               | Color                           | 滚动时显现的底部细分割线颜色 | COUITheme.colorScheme.divider | 否       |
| navigationIcon             | @Composable () -> Unit          | 导航图标区域的可组合函数 | {}                                | 否       |
| actions                    | @Composable RowScope.() -> Unit | 操作按钮区域的可组合函数（建议使用 24dp 图标） | {}                                | 否       |
| scrollBehavior             | ScrollBehavior?                 | 控制顶部栏滚动行为       | null                              | 否       |
| defaultWindowInsetsPadding | Boolean                         | 是否应用默认窗口边距     | true                              | 否       |
| showDivider                | Boolean                         | 内容在栏下滚动时是否绘制底部细分割线（需要 scrollBehavior）；设置了 `glow` 时始终隐藏 | true    | 否       |
| centerTitle                | Boolean                         | 标题与副标题居中（COUI 标题居中样式） | false | 否       |
| titlePadding          | Dp                              | 水平内容边距；未指定时随栏宽分档（16 / 24 / 40dp） | Dp.Unspecified | 否       |
| navigationIconPadding      | Dp                              | 导航图标的起始边距；未指定时随栏宽分档 | Dp.Unspecified | 否       |
| actionIconPadding          | Dp                              | 操作图标的末尾边距；未指定时随栏宽分档 | Dp.Unspecified | 否       |
| glow                       | (@Composable BoxScope.() -> Unit)? | 顶栏背后的衬底层，例如配合透明 `color` 使用的 `TopAppBarGlow`；`null` 为实色顶栏 | null | 否       |
| bottomContent              | @Composable () -> Unit          | 显示在标题栏下方的可组合内容 | {}                                | 否       |

### TopAppBarSearchSlot 属性

| 属性名       | 类型                   | 说明                                         | 默认值                                   | 是否必须 |
| ------------ | ---------------------- | -------------------------------------------- | ---------------------------------------- | -------- |
| inputField   | @Composable () -> Unit | 搜索输入框，例如 SearchBar 中的 `InputField` | -                                        | 是       |
| modifier     | Modifier               | 应用于槽位行的修饰符                         | Modifier                                 | 否       |
| height       | Dp                     | 槽位行高度（`coui_search_view_height`）      | TopAppBarDefaults.SearchSlotHeight       | 否       |
| insideMargin | PaddingValues          | 胶囊的水平页边距                             | TopAppBarDefaults.SearchSlotInsideMargin | 否       |

### TopAppBarDefaults 对象

TopAppBarDefaults 对象提供了 TopAppBar 和 SmallTopAppBar 组件的默认值。

#### 常量

| 常量名                    | 类型 | 说明                              | 默认值 |
| ------------------------- | ---- | --------------------------------- | ------ |
| HorizontalPaddingCompact   | Dp   | 紧凑窗口下标题与图标的边距        | 16.dp  |
| HorizontalPaddingMedium    | Dp   | 中等窗口下标题与图标的边距        | 24.dp  |
| HorizontalPaddingExpanded  | Dp   | 展开窗口下标题与图标的边距        | 40.dp  |
| SmallTitleTextSize         | Dp   | SmallTopAppBar 标题字号（SecondTitle，中等字重） | 18.dp  |
| LargeTitleTopPadding | Dp | 展开态大标题的顶部内边距          | 54.dp  |
| CollapsedHeight            | Dp   | TopAppBar 折叠时的高度           | 52.dp  |
| ExpandedHeight             | Dp   | 无副标题时 TopAppBar 展开态的最小高度 | 110.dp |
| SmallTopAppBarCenterHeight | Dp   | SmallTopAppBar 布局的垂直中心高度 | 52.dp  |
| LargeTitleBottomPadding    | Dp   | 展开态大标题区块下方的底部边距（折叠时渐变至 0.dp） | 12.dp   |
| SubtitleBottomPadding      | Dp   | 副标题溢出折叠栏时其下方的底部边距 | 8.dp   |
| SubtitleMarginTop          | Dp   | 标题与副标题之间的垂直间距        | 3.5.dp |
| NavigationIconGap          | Dp   | 导航图标与折叠态标题之间的水平间距 | 10.dp  |
| ActionIconGap              | Dp   | 标题与操作图标之间的水平间距      | 4.dp   |
| ActionItemSpacing          | Dp   | 相邻操作图标之间的水平间距        | 10.dp  |
| SearchSlotHeight           | Dp   | TopAppBarSearchSlot 行高度        | 52.dp  |
| SearchSlotInsideMargin     | PaddingValues | 搜索胶囊的水平页边距     | PaddingValues(horizontal = 16.dp) |

#### 函数

| 函数名            | 说明 |
| ----------------- | ---- |
| horizontalPadding(barWidth, windowHeight) | `titlePadding` / `navigationIconPadding` / `actionIconPadding` 未指定时使用的边距：栏宽不足 600dp 为 16dp，否则按栏宽与窗口高度对应的 COUI 屏幕档位取 24dp 或 40dp |

### ScrollBehavior

COUIScrollBehavior 是用于控制顶部栏滚动行为的配置对象。

#### rememberTopAppBarState

用于创建和记住 TopAppBarState：

```kotlin
val scrollBehavior = COUIScrollBehavior(
    state = rememberTopAppBarState(),
    canScroll = { true }
)
```

| 参数名             | 类型                        | 默认值                     | 说明                       |
| ------------------ | --------------------------- | -------------------------- | -------------------------- |
| state              | TopAppBarState              | rememberTopAppBarState()   | 控制滚动状态的状态对象     |
| canScroll          | () -> Boolean               | { true }                   | 控制是否允许滚动的回调     |
| snapAnimationSpec  | AnimationSpec\<Float>?      | 180ms 减速 tween           | 定义滚动停在中间态时吸附到全展开/全折叠的动画 |
| flingAnimationSpec | DecayAnimationSpec\<Float>? | rememberSplineBasedDecay() | 定义顶部栏滑动的衰减动画   |

## 进阶用法

### 处理窗口边距

```kotlin
TopAppBar(
    title = "标题",
    largeTitle = "大标题",
    defaultWindowInsetsPadding = false // 自行处理窗口嵌入边距
)
```

### 自定义滚动行为动画

```kotlin
var isScrollingEnabled by remember { mutableStateOf(true) }
val scrollBehavior = COUIScrollBehavior(
    snapAnimationSpec = tween(durationMillis = 100),
    flingAnimationSpec = rememberSplineBasedDecay(),
    canScroll = { isScrollingEnabled } // 可以动态控制是否允许滚动
)

TopAppBar(
    title = "标题",
    largeTitle = "大标题",
    scrollBehavior = scrollBehavior
)
```

### 大标题和小标题结合使用

```kotlin
var useSmallTopBar by remember { mutableStateOf(false) }

Box(modifier = Modifier.fillMaxSize()) {
    if (useSmallTopBar) {
        SmallTopAppBar(
            title = "精简模式",
            navigationIcon = {
                IconButton(onClick = { useSmallTopBar = false }) {
                    Icon(
                        imageVector = COUIIcons.Back,
                        contentDescription = "切换到大标题",
                        tint = COUITheme.colorScheme.labelPrimary
                    )
                }
            }
        )
    } else {
        TopAppBar(
            title = "标题",
            largeTitle = "展开模式",
            navigationIcon = {
                IconButton(onClick = { useSmallTopBar = true }) {
                    Icon(
                        imageVector = COUIIcons.Back,
                        contentDescription = "切换到小标题",
                        tint = COUITheme.colorScheme.labelPrimary
                    )
                }
            }
        )
    }
}
```
