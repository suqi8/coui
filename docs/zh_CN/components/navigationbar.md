# NavigationBar

`NavigationBar` 是 COUI 中的底部导航栏组件，用于在应用底部创建导航菜单，支持 2 到 5 个导航项，提供不同的显示模式（仅图标、仅文本、图标和文本、仅选中项显示文本）。

COUI 17 悬浮胶囊导航栏见 [FloatingNavigationBar](./floatingnavigationbar)。

这些组件通常与 `Scaffold` 组件结合使用，以便在应用程序的不同页面中保持一致的布局和行为。

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=navigationBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.NavigationBar
import io.github.suqi8.coui.kmp.basic.NavigationBarItem
import io.github.suqi8.coui.kmp.basic.NavigationBarDisplayMode
import io.github.suqi8.coui.kmp.basic.NavigationItem
```

## 基本用法

### NavigationBar

NavigationBar 组件可用于创建固定在底部的导航菜单：

```kotlin
var selectedIndex by remember { mutableStateOf(0) }
val items = listOf("Home", "Profile", "Settings")
val icons = listOf(COUIIcons.VerticalSplit, COUIIcons.Contacts, COUIIcons.Settings)

Scaffold(
    bottomBar = {
        NavigationBar {
            items.forEachIndexed { index, label ->
                NavigationBarItem(
                    selected = selectedIndex == index,
                    onClick = { selectedIndex = index },
                    icon = icons[index],
                    label = label
                )
            }
        }
    }
)
```

## 组件状态

### 选中状态

`NavigationBarItem` 遵循 COUI tab 导航的状态模型：选中项的图标与标签以完整标签色显示，未选中项呈暗化色；图标在两种状态间以 180ms 交叉淡变过渡，标签颜色则瞬时切换。标签始终使用 Medium 字重；按压未选中项时图标会预览选中色。

### 宽窗口

导航栏与 COUI 的资源限定符一样跟随窗口变化。窗口宽度不小于 600dp 时（`values-w600dp`，`is_big_layout`），`IconAndText` 导航项改为图标在左、14dp 标签在右，间距 8dp，整体居中（`COUINavigationItemView.leftToRight`），两侧边距变为 44dp；横屏窗口同样使用 44dp 边距（`values-land`）。

## 属性

### NavigationBar 属性

| 属性名                     | 类型                    | 说明                       | 默认值                                | 是否必须 |
| -------------------------- | ----------------------- | -------------------------- | ------------------------------------- | -------- |
| modifier                   | Modifier                | 应用于导航栏的修饰符       | Modifier                              | 否       |
| visible                    | Boolean                 | 是否显示；隐藏时以 200ms（COUIOutEase）按自身高度下移并收起布局，显示时以 250ms（COUIInEase）反向 | true | 否 |
| color                      | Color                   | 导航栏背景颜色（`?couiColorBar`） | NavigationBarDefaults.color() | 否       |
| showDivider                | Boolean                 | 是否显示顶部分割线         | true                                  | 否       |
| defaultWindowInsetsPadding | Boolean                 | 是否应用默认窗口嵌入边距   | true                                  | 否       |
| mode                       | NavigationBarDisplayMode   | 导航项的显示模式           | NavigationBarDisplayMode.IconAndText     | 否       |
| content                    | @Composable RowScope.() | 导航栏的内容               | -                                     | 是       |

### NavigationBarItem 属性

| 属性名 | 类型        | 说明             | 默认值 | 是否必须 |
| ------ | ----------- | ---------------- | ------ | -------- |
| selected | Boolean     | 是否选中         | -      | 是       |
| onClick  | () -> Unit  | 点击时的回调     | -      | 是       |
| icon     | ImageVector | 图标             | -      | 是       |
| label    | String      | 文本标签         | -      | 是       |
| modifier | Modifier    | 应用于导航项的修饰符 | Modifier | 否       |
| enabled  | Boolean     | 是否启用         | true     | 否       |
| badge    | Int?        | 图标上的红点：`null` 无，`0` 为 6dp 圆点（超出图标右上角 1dp），正数为数字徽标（右缘超出图标 8dp、高出图标 5dp）。`TextOnly` 模式不显示 | null | 否 |

### NavigationBarDefaults 对象

NavigationBarDefaults 对象提供了 NavigationBar 和 NavigationBarItem 组件的默认值。

#### 常量

| 常量名                 | 类型     | 说明                       | 默认值 |
| ---------------------- | -------- | -------------------------- | ------ |
| ItemHeight             | Dp       | 项目单元高度               | 56.dp  |
| HorizontalPadding      | Dp       | 栏两侧的水平内边距         | 12.dp  |
| HorizontalPaddingWide  | Dp       | 宽窗口或横屏时的两侧边距   | 44.dp  |
| WideLayoutBreakpoint   | Dp       | 导航项改为左右排布的窗口宽度 | 600.dp |
| IconLabelSpacingWide   | Dp       | 宽布局下图标与标签的间距   | 8.dp   |
| LabelFontSizeWide      | Dp       | 宽布局下的标签字号，单位 dp | 14.dp |
| ItemHorizontalPadding  | Dp       | 项目单元内部水平内边距     | 2.dp   |
| IconSize               | Dp       | 图标尺寸                   | 24.dp  |
| IconTopPadding         | Dp       | 图标顶部内边距             | 9.dp   |
| LabelBottomPadding     | Dp       | 标签底部内边距             | 7.dp   |
| LabelFontSize          | Dp       | 标签字号，单位 dp（不随字体缩放） | 10.dp |
| TextFontSize           | Dp       | 字号（TextOnly 模式），单位 dp | 14.dp  |
| IconFadeDurationMillis | Int      | 图标状态间交叉淡变时长     | 180    |
| HideDurationMillis     | Int      | 隐藏平移时长               | 200    |
| ShowDurationMillis     | Int      | 显示平移时长               | 250    |
| BadgeDotOffset         | Dp       | 红点超出图标右上角的距离   | 1.dp   |
| BadgeNumberHorizontalOffset | Dp  | 数字徽标右缘超出图标的距离（小于 10 时 -2dp，100 到 999 时 +1dp） | 8.dp |
| BadgeNumberVerticalOffset | Dp    | 数字徽标高出图标的距离     | 5.dp   |

#### 函数

| 函数名 | 返回类型 | 说明 |
| ------ | -------- | ---- |
| color() | Color | 导航栏背景 `?couiColorBar`：浅色 `#FAFAFA`，深色 `#1E1E1E` |

### NavigationBarDisplayMode 枚举

| 值                    | 说明                   |
| --------------------- | ---------------------- |
| IconAndText           | 显示图标和文本         |
| IconOnly              | 仅显示图标             |
| TextOnly              | 仅显示文本             |
| IconWithSelectedLabel | 始终显示图标，仅选中时显示文本 |

设置在 `NavigationBar` 上的显示模式会通过 `LocalNavigationBarDisplayMode` 组合局部值传递给各导航项。

### NavigationItem 属性

`NavigationItem` 是一个用于承载导航项标签与图标的便捷数据类。

| 属性名 | 类型        | 说明       | 默认值 | 是否必须 |
| ------ | ----------- | ---------- | ------ | -------- |
| label  | String      | 该项的标签 | -      | 是       |
| icon   | ImageVector | 该项的图标 | -      | 是       |

## 进阶用法

### NavigationBar

#### 自定义颜色

```kotlin
NavigationBar(
    color = Color.Red.copy(alpha = 0.3f)
) {
    // ... items ...
}
```

#### 无分割线

```kotlin
NavigationBar(
    showDivider = false
) {
    // ... items ...
}
```

#### 处理窗口边距

```kotlin
NavigationBar(
    defaultWindowInsetsPadding = false // Handle window insets padding manually
) {
    // ... items ...
}
```

#### 显示模式

```kotlin
NavigationBar(
    // IconAndText (default) / IconOnly / TextOnly / IconWithSelectedLabel
    mode = NavigationBarDisplayMode.IconWithSelectedLabel
) {
    // ... items ...
}
```

### 结合页面切换使用（使用脚手架）

#### 使用 NavigationBar

```kotlin
val pages = listOf("Home", "Profile", "Settings")
val icons = listOf(COUIIcons.VerticalSplit, COUIIcons.Contacts, COUIIcons.Settings)
var selectedIndex by remember { mutableStateOf(0) }

Scaffold(
    bottomBar = {
        NavigationBar {
            pages.forEachIndexed { index, label ->
                NavigationBarItem(
                    selected = selectedIndex == index,
                    onClick = { selectedIndex = index },
                    icon = icons[index],
                    label = label
                )
            }
        }
    }
) { paddingValues ->
    // Content area needs to consider padding
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Current Page: ${pages[selectedIndex]}",
            style = COUITheme.textStyles.displayS
        )
    }
}
```
