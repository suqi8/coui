# NavigationBar

`NavigationBar` 是 COUI 中的底部导航栏组件，用于在应用底部创建导航菜单，支持 2 到 5 个导航项，提供不同的显示模式（仅图标、仅文本、图标和文本、仅选中项显示文本）。

`FloatingNavigationBar` 是 COUI 17 悬浮底部导航栏：56dp 胶囊，容纳 2 到 5 个导航项，每项为 24dp 图标加其下 10dp 标签，带底栏材质、整体触摸形变与滑动选择。

这些组件通常与 `Scaffold` 组件结合使用，以便在应用程序的不同页面中保持一致的布局和行为。

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=navigationBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.NavigationBar
import io.github.suqi8.coui.kmp.basic.NavigationBarItem
import io.github.suqi8.coui.kmp.basic.FloatingNavigationBar
import io.github.suqi8.coui.kmp.basic.FloatingNavigationBarItem
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

### FloatingNavigationBar

FloatingNavigationBar 组件可用于创建悬浮在底部的导航菜单：

```kotlin
var selectedIndex by remember { mutableStateOf(0) }
val items = listOf("Home", "Profile", "Settings")
val icons = listOf(COUIIcons.VerticalSplit, COUIIcons.Contacts, COUIIcons.Settings)

Scaffold(
    bottomBar = {
        FloatingNavigationBar {
            items.forEachIndexed { index, label ->
                FloatingNavigationBarItem(
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

`NavigationBarItem` 遵循 COUI tab 导航的状态模型：选中项的图标与标签以完整标签色显示，未选中项呈暗化色；图标在两种状态间以 180ms 交叉淡变过渡，标签颜色则瞬时切换。标签始终使用 Medium 字重；按压未选中项时图标会预览选中色。`FloatingNavigationBarItem` 的图标与标签在选中色（`#E6000000`）和未选中色（`#B8000000`）之间瞬时切换，没有指示器、按压遮罩或振动反馈。

### 导航项变化与标签

导航项放不下或数量变化时，胶囊宽度与位置以弹簧（response 0.55，bounce 0.35）过渡，导航项随胶囊展开。数量变化时，已有导航项从原绘制位置出发，以同一弹簧过渡到新的分布（COUI 会重建导航项视图，导致跳变），新增导航项直接出现在其槽位。每个标签在旧宽度与新宽度两种排版之间交叉淡变（变窄：旧的以 response 0.2 淡出、新的以 0.3 淡入；变宽反之）。标签位置按 Android `TextView` 的 `includeFontPadding` 校准：距导航项顶部 30dp，再加 Roboto 字体上方留白（0.128em）。

### 扩展视图

提供 `expansion` 且 `expansionVisible` 时，扩展视图以弹簧（response 0.55，bounce 0.35）从胶囊末端后方滑出，同时由 0.5 缩放到 1，胶囊收窄让位。其透明度跟随被胶囊遮挡的比例（遮挡一半以内不透明，之后线性淡出）；开启 `materialEffect` 时两者接触处以融球效果相连。隐藏时反向；隐藏并静止后不再放置，也不响应触摸。

### 滑动选择

在 `FloatingNavigationBar` 中，手指在胶囊上滑动时会随手指选中其下方的导航项，选中态跟随手指移动。手指抬起时（无论在何处抬起）提交滑动结束时所在项的 `onClick`；禁用项会被跳过。普通点按调用被点项的 `onClick`。按下期间整个胶囊带有 COUI 17 触摸形变（按压缩放、橡皮筋跟随与拖拽形变），开启 `materialEffect` 时还有触摸聚光。

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

### FloatingNavigationBar 属性

| 属性名                     | 类型                      | 说明                               | 默认值                                  | 是否必须 |
| -------------------------- | ------------------------- | ---------------------------------- | --------------------------------------- | -------- |
| modifier                   | Modifier                  | 应用于导航栏整宽容器的修饰符       | Modifier                                | 否       |
| backgroundModifier         | Modifier                  | 作用于胶囊裁剪内、`color` 之上、导航项之下：底栏背景模糊的位置 | Modifier | 否 |
| visible                    | Boolean                   | 是否显示胶囊；切换时运行 COUI 17 显示 / 隐藏弹簧，隐藏后保留占位但不响应触摸 | true | 否 |
| expansionVisible           | Boolean                   | 是否在胶囊旁显示 `expansion`       | true                                    | 否       |
| materialEffect             | Boolean                   | 是否绘制 COUI 17 描边光、内阴影、触摸聚光、隐藏模糊以及与 `expansion` 的融球效果 | true | 否       |
| showDivider                | Boolean                   | 是否在胶囊周围绘制细分割线（非 COUI 17 样式） | false                   | 否       |
| defaultWindowInsetsPadding | Boolean                   | 是否让胶囊避开导航栏与标题栏嵌入   | true                                    | 否       |
| color                      | Color                     | 胶囊填充色；`backgroundModifier` 绘制模糊时传 `Color.Transparent` | FloatingNavigationBarDefaults.color() | 否 |
| cornerRadius               | Dp                        | 胶囊圆角半径；默认值使其全圆，按 COUI 17 软胶囊绘制，更小的半径按 `default_g2_weight` 2.45 绘制 | FloatingNavigationBarDefaults.CornerRadius | 否       |
| horizontalAlignment        | Alignment.Horizontal      | 未显示 `expansion` 时胶囊的水平对齐方式；显示时按 COUI 布局（窄屏胶囊靠起始边、扩展视图靠末端，宽屏两者整体居中） | CenterHorizontally | 否 |
| horizontalOutSidePadding   | Dp                        | 胶囊与导航栏两侧的最小距离         | FloatingNavigationBarDefaults.HorizontalOutSidePadding | 否       |
| shadowElevation            | Dp                        | 胶囊投影的模糊半径；0 关闭投影     | FloatingNavigationBarDefaults.ShadowElevation          | 否       |
| expansion                  | (@Composable () -> Unit)? | 胶囊旁的可选扩展视图（例如 56dp 搜索按钮），居中于距胶囊 12dp 的 56dp 槽位 | null | 否 |
| content                    | @Composable () -> Unit    | 导航栏的内容，直接放置 `FloatingNavigationBarItem` | -                       | 是       |

### FloatingNavigationBarItem 属性

| 属性名 | 类型        | 说明             | 默认值 | 是否必须 |
| ------ | ----------- | ---------------- | ------ | -------- |
| selected | Boolean     | 是否选中         | -      | 是       |
| onClick  | () -> Unit  | 点击时的回调     | -      | 是       |
| icon     | ImageVector | 图标             | -      | 是       |
| label    | String      | 文本标签         | -      | 是       |
| modifier | Modifier    | 应用于导航项的修饰符 | Modifier | 否       |
| enabled  | Boolean     | 是否启用         | true     | 否       |
| largeIcon | Boolean    | 使用 40dp 大图标，高出导航项 14dp（高出胶囊 10dp）；点按大图标即选中该项 | false | 否 |
| badge    | Int?        | 图标上的红点：`null` 无，`0` 为图标右上角的 6dp 圆点，正数为数字徽标（高出图标 4dp，1 / 2 / 3 位数时右缘超出图标 6 / 9 / 10dp）。禁用项不显示 | null | 否 |
| colors   | FloatingNavigationBarColors | 导航项内容色 | FloatingNavigationBarDefaults.itemColors() | 否 |

### NavigationBarDefaults 对象

NavigationBarDefaults 对象提供了 NavigationBar 和 NavigationBarItem 组件的默认值。

#### 常量

| 常量名                 | 类型     | 说明                       | 默认值 |
| ---------------------- | -------- | -------------------------- | ------ |
| ItemHeight             | Dp       | 项目单元高度               | 56.dp  |
| HorizontalPadding      | Dp       | 栏两侧的水平内边距         | 12.dp  |
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

### FloatingNavigationBarDefaults 对象

FloatingNavigationBarDefaults 对象提供了 FloatingNavigationBar 和 FloatingNavigationBarItem 组件的默认值。

#### 常量

| 常量名                   | 类型  | 说明                       | 默认值 |
| ------------------------ | ----- | -------------------------- | ------ |
| CornerRadius             | Dp    | 圆角半径：`COUIRoundCorner.Full`，任意高度都是胶囊（COUI 17 高度一半的软胶囊） | 72.dp  |
| HorizontalOutSidePadding | Dp    | 胶囊两侧最小距离（`coui_bottom_floating_navigation_bar_padding_horizontal`） | 16.dp |
| VerticalOutSidePadding   | Dp    | 胶囊上下留白（56dp 胶囊居中于 88dp 容器） | 16.dp |
| ShadowElevation          | Dp    | 投影模糊半径，近似 COUI 17 焦散阴影（高度 24dp，alpha 45） | 24.dp |
| Height                   | Dp    | 胶囊高度                   | 56.dp  |
| HorizontalPadding        | Dp    | 胶囊内部水平内边距         | 12.dp  |
| ItemSpacing              | Dp    | 项目间距                   | 8.dp   |
| ItemMaxWidth             | Dp    | 项目宽度；放不下时等比缩小 | 70.dp  |
| ItemHeight               | Dp    | 项目高度，在胶囊内垂直居中 | 48.dp  |
| IconSize                 | Dp    | 图标尺寸                   | 24.dp  |
| IconTopPadding           | Dp    | 图标距项目顶部的距离       | 4.dp   |
| LabelTopSpacing          | Dp    | 图标与标签的间距           | 2.dp   |
| LabelTextSize            | Dp    | 标签字号，单位 dp（不随字体缩放） | 10.dp |
| HiddenScale              | Float | 隐藏时胶囊的缩放           | 0.8f   |
| HiddenBlurRadiusPx       | Float | 隐藏时胶囊自身的模糊半径（原始像素） | 30f |
| LargeIconSize            | Dp    | 大图标尺寸                 | 40.dp  |
| LargeIconOverflow        | Dp    | 大图标高出导航项的距离     | 14.dp  |
| BadgeTopOverflow         | Dp    | 数字徽标高出图标的距离     | 4.dp   |
| BadgeEndOverflowOneDigit / TwoDigits / ThreeDigits | Dp | 数字徽标右缘超出图标的距离 | 6.dp / 9.dp / 10.dp |
| ExpansionSlotSize        | Dp    | 扩展视图所居中的槽位       | 56.dp  |
| ExpansionSpacing         | Dp    | 胶囊与扩展视图的间距       | 12.dp  |
| ExpansionHiddenScale     | Float | 扩展视图隐藏时的缩放       | 0.5f   |
| CompactWidthBreakpoint   | Dp    | 容器窄于此宽度时使用窄屏扩展布局 | 600.dp |

#### 函数

| 函数名 | 返回类型 | 说明 |
| ------ | -------- | ---- |
| color() | Color | 不透明填充，COUI 无模糊时的回退色 `?couiColorBar`：浅色 `#FAFAFA`，深色 `#1E1E1E` |
| backdropSpec(isDark) | MaterialBackdropSpec | COUI 17 底栏材质 `TYPE_FRAMEWORK_BOTTOM_BAR`：25px 模糊，然后浅色 `#B3666666` COLOR_DODGE + `#99F5F5F5` LUMINOSITY，深色 `#80666666` OVERLAY + `#B3262626` LUMINOSITY |
| itemColors() | FloatingNavigationBarColors | 导航项内容色：选中 `#E6000000` / `#E6FFFFFF`，未选中 `#B8000000` / `#B8FFFFFF`，禁用 `#42000000` / `#4DFFFFFF` |

### FloatingNavigationBarColors 类

| 属性名          | 类型  | 说明                   |
| --------------- | ----- | ---------------------- |
| selectedColor   | Color | 选中项的图标与标签颜色 |
| unselectedColor | Color | 未选中项的图标与标签颜色 |
| disabledColor   | Color | 禁用项的图标与标签颜色 |

### NavigationBarDisplayMode 枚举

| 值                    | 说明                   |
| --------------------- | ---------------------- |
| IconAndText           | 显示图标和文本         |
| IconOnly              | 仅显示图标             |
| TextOnly              | 仅显示文本             |
| IconWithSelectedLabel | 始终显示图标，仅选中时显示文本 |

设置在 `NavigationBar` 上的显示模式会通过 `LocalNavigationBarDisplayMode` 组合局部值传递给各导航项。`FloatingNavigationBar` 没有 `mode` 参数——其导航项始终显示图标加其下标签。

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

### FloatingNavigationBar

#### 自定义颜色和圆角

```kotlin
FloatingNavigationBar(
    color = COUITheme.colorScheme.primaryContainer,
    cornerRadius = 28.dp
) {
    // ... items ...
}
```

#### 自定义对齐和边距

```kotlin
FloatingNavigationBar(
    horizontalAlignment = Alignment.Start, // Align to start
    horizontalOutSidePadding = 16.dp // Set outside padding
) {
    // ... items ...
}
```

#### 分割线与阴影

```kotlin
FloatingNavigationBar(
    showDivider = true, // Draw a thin divider ring around the bar
    shadowElevation = 0.dp // Disable the drop shadow
) {
    // ... items ...
}
```

#### 底栏材质模糊

COUI 17 用框架底栏材质填充胶囊：25px 背景模糊叠加两层混合色。coui-ui 自身不采集背景，因此通过 `backgroundModifier` 传入模糊（例如基于 coui-blur `couiViewBlur` 的 `MaterialBackdropProvider`），并把填充色设为透明：

```kotlin
val isDark = COUITheme.colorScheme.background.luminance() < 0.5f
FloatingNavigationBar(
    color = Color.Transparent,
    backgroundModifier = backdropProvider.materialBackdrop(
        spec = FloatingNavigationBarDefaults.backdropSpec(isDark),
        shape = RoundedCornerShape(FloatingNavigationBarDefaults.CornerRadius),
    ),
) {
    // ... items ...
}
```

背景采集层只能记录页面内容，不能包含导航栏本身：把导航栏放在采集层之外（例如 `Scaffold` 的 `bottomBar`）。

#### 扩展视图、大图标与徽标

```kotlin
FloatingNavigationBar(
    expansionVisible = showSearch,
    expansion = { SearchButton(onClick = { showSearch = false }) },
) {
    FloatingNavigationBarItem(selected = page == 0, onClick = { page = 0 }, icon = COUIIcons.Photos, label = "Photos", badge = 0)
    FloatingNavigationBarItem(selected = page == 1, onClick = { page = 1 }, icon = COUIIcons.Add, label = "Create", largeIcon = true)
    FloatingNavigationBarItem(selected = page == 2, onClick = { page = 2 }, icon = COUIIcons.Contacts, label = "Me", badge = 8)
}
```

#### 显示与隐藏

```kotlin
FloatingNavigationBar(
    visible = !isScrollingDown, // Alpha, scale 0.8 and blur springs
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
            style = COUITheme.textStyles.title1
        )
    }
}
```

#### 使用 FloatingNavigationBar

```kotlin
val pages = listOf("Home", "Profile", "Settings")
val icons = listOf(COUIIcons.VerticalSplit, COUIIcons.Contacts, COUIIcons.Settings)
var selectedIndex by remember { mutableStateOf(0) }

Scaffold(
    bottomBar = {
        FloatingNavigationBar {
            pages.forEachIndexed { index, label ->
                FloatingNavigationBarItem(
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
            style = COUITheme.textStyles.title1
        )
    }
}
```
