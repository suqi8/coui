# FloatingToolbar

`FloatingToolbar` 是 COUI 17 悬浮工具栏（`COUIBottomFloatingToolbar`），有两种形态：

- **条目工具栏**（`FloatingToolbar(items = ...)`）：COUI 工具栏本体，一条至少 76dp 高的满宽栏，按条目分组最多放三个胶囊，支持图标项、文字项、红点与溢出菜单。
- **内容胶囊**（`FloatingToolbar { ... }`）：包住自定义内容的单个胶囊，可水平或垂直排列（对应 COUI 的自定义视图条目）。实际的屏幕放置由父组件（通常是 `Scaffold`）处理。

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=floatingToolbar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.FloatingToolbar
import io.github.suqi8.coui.kmp.basic.FloatingToolbarDefaults
import io.github.suqi8.coui.kmp.basic.FloatingToolbarGroupArrangement
import io.github.suqi8.coui.kmp.basic.FloatingToolbarItem
import io.github.suqi8.coui.kmp.basic.ToolbarPosition // 用于 Scaffold
```

## 条目工具栏

```kotlin
Box(Modifier.fillMaxSize()) {
    FloatingToolbar(
        items = listOf(
            FloatingToolbarItem(onClick = { /* 编辑 */ }, icon = COUIIcons.Edit, title = "编辑"),
            FloatingToolbarItem(onClick = { /* 删除 */ }, icon = COUIIcons.Delete, title = "删除", showRedDot = true),
            FloatingToolbarItem(onClick = { /* 完成 */ }, title = "完成", group = 1),
        ),
        modifier = Modifier.align(Alignment.BottomCenter),
    )
}
```

- **布局**：宽度小于 600dp 时两侧留 16dp，小于 840dp 时 24dp，此后 40dp；胶囊在至少 76dp 的高度内垂直居中。两个分组贴两侧（`Spread`）或以 16dp 间距并排居中（`Packed`）；第三个分组居中。
- **胶囊**：高 44dp，条目间距 4dp，首尾条目外侧各留 6dp 胶囊（单个条目时不留）。图标项 44 x 40dp、图标 24dp；没有图标的条目显示标题（16sp 中黑，宽 52-200dp，两侧内边距 16dp）。
- **溢出**：每组最多显示六个条目；后面还有条目或放不下时，最后一位换成「更多」项，点击在胶囊上方 8dp 处弹出其余条目的菜单。
- **红点**：直径 6dp，圆心距条目右上角各 12dp；禁用的条目不显示红点。
- **触摸**：每个胶囊整体响应 COUI 按压形变与触摸聚光，与条目是否启用无关；条目本身没有按压蒙层。只有一个条目的胶囊由材质画成正圆。
- **条目变化**（`ItemViewAnimatorImpl`）：胶囊显示的条目变化时（增删条目、条目移入或移出溢出项），间距先「呼吸」200ms（胶囊将变宽时收拢 75%，将变窄时张开 75%），随后新条目绕中心缩放淡入、离开的条目缩放淡出（response 0.5 / bounce 0.4），胶囊宽度随之变化；动画中途撤销会倒放回去。图标、标题、描述无法区分条目时请提供 `key`。
- **分组**：新出现的分组缩放进入（response 0.5 / bounce 0.4），移除的分组在原位缩放消失（response 0.25 / bounce 0），需要挪位的胶囊以弹簧移到新位置（response 0.5 / bounce 0.4）。工具栏宽度变化时直接到位。

```kotlin
var editing by remember { mutableStateOf(false) }
FloatingToolbar(
    items = buildList {
        add(FloatingToolbarItem(onClick = { /* 分享 */ }, icon = COUIIcons.Share, title = "分享", key = "share"))
        if (editing) add(FloatingToolbarItem(onClick = { /* 删除 */ }, icon = COUIIcons.Delete, title = "删除", key = "delete"))
        add(FloatingToolbarItem(onClick = { editing = !editing }, title = if (editing) "完成" else "编辑", group = 1, key = "mode"))
    },
)
```

## 内容胶囊

```kotlin
Scaffold(
    floatingToolbar = {
        FloatingToolbar {
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) { // 或 Column
                IconButton(onClick = { /* 操作 1 */ }) {
                    Icon(COUIIcons.Edit, contentDescription = "编辑")
                }
                IconButton(onClick = { /* 操作 2 */ }) {
                    Icon(COUIIcons.Delete, contentDescription = "删除")
                }
            }
        }
    },
    floatingToolbarPosition = ToolbarPosition.BottomCenter // 设置位置
)
```

## 属性

### FloatingToolbar（条目工具栏）属性

| 属性名                     | 类型                            | 说明                                               | 默认值                                             | 是否必须 |
| -------------------------- | ------------------------------- | -------------------------------------------------- | -------------------------------------------------- | -------- |
| items                      | List\<FloatingToolbarItem>       | 条目；同一 `group` 组成一个胶囊（最多三组）        | -                                                  | 是       |
| modifier                   | Modifier                        | 应用于满宽工具栏的修饰符                           | Modifier                                           | 否       |
| visible                    | Boolean                         | 是否显示胶囊（缩放弹簧）                           | true                                               | 否       |
| materialEffect             | Boolean                         | 是否在胶囊上绘制 COUI 17 底栏材质                  | `LocalCOUIMaterialEffectEnabled.current` | 否       |
| defaultWindowInsetsPadding | Boolean                         | 是否让胶囊避开导航栏与标题栏                       | true                                               | 否       |
| groupArrangement           | FloatingToolbarGroupArrangement | `Spread`（贴两侧）或 `Packed`（并排居中）          | FloatingToolbarGroupArrangement.Spread             | 否       |
| color                      | Color                           | 无材质模糊时的胶囊底色                             | FloatingToolbarDefaults.defaultColor()             | 否       |
| shadowElevation            | Dp                              | 胶囊阴影的模糊半径，0 表示关闭                     | FloatingToolbarDefaults.ShadowElevation            | 否       |
| colors                     | FloatingToolbarItemColors       | 条目内容颜色                                       | FloatingToolbarDefaults.itemColors()               | 否       |
| overflowContentDescription | String                          | 溢出项的无障碍标签                                 | FloatingToolbarDefaults.OverflowContentDescription | 否       |

### FloatingToolbarItem

| 属性名             | 类型         | 说明                                           | 默认值 |
| ------------------ | ------------ | ---------------------------------------------- | ------ |
| onClick            | () -> Unit   | 在工具栏或溢出菜单中点击条目时调用             | -      |
| icon               | ImageVector? | 图标；为空时显示标题                           | null   |
| title              | String       | 标题文字、溢出菜单中的行文字及备用无障碍标签   | ""     |
| contentDescription | String?      | 无障碍标签                                     | null   |
| group              | Int          | 条目所属分组（胶囊），按数值排序               | 0      |
| enabled            | Boolean      | 是否启用                                       | true   |
| showRedDot         | Boolean      | 是否显示红点                                   | false  |
| key                | Any?         | 条目身份（`setId`），工具栏据此为增删的条目播放动画，组内唯一；为空时按图标、标题与描述区分 | null |

### FloatingToolbar（内容胶囊）属性

| 属性名                     | 类型                   | 说明                       | 默认值                                 | 是否必须 |
| -------------------------- | ---------------------- | -------------------------- | -------------------------------------- | -------- |
| modifier                   | Modifier               | 应用于工具栏的修饰符       | Modifier                               | 否       |
| visible                    | Boolean                | 是否显示工具栏；切换时运行 COUI 17 的缩放显示 / 隐藏弹簧 | true | 否       |
| materialEffect             | Boolean                | 是否使用 COUI 17 底栏材质：有 `LocalMaterialBackdrop` 时由背景模糊取代 `color` | `LocalCOUIMaterialEffectEnabled.current` | 否       |
| color                      | Color                  | 工具栏背景颜色             | FloatingToolbarDefaults.defaultColor() | 否       |
| cornerRadius               | Dp                     | 工具栏圆角半径             | FloatingToolbarDefaults.CornerRadius   | 否       |
| outSidePadding             | PaddingValues          | 工具栏外部的内边距         | FloatingToolbarDefaults.OutSidePadding | 否       |
| shadowElevation            | Dp                     | 焦散投影的模糊半径，0 表示关闭 | FloatingToolbarDefaults.ShadowElevation | 否   |
| showDivider                | Boolean                | 是否显示工具栏周围的分割线 | false                                  | 否       |
| content                    | @Composable () -> Unit | 工具栏内容区域的可组合函数 | -                                      | 是       |

### FloatingToolbarDefaults 对象

| 属性名         | 类型                    | 说明           | 值                                      |
| -------------- | ----------------------- | -------------- | --------------------------------------- |
| CornerRadius   | Dp                      | 默认圆角半径（44dp 高度的一半，按权重 1.0 软胶囊绘制；小于高度一半时按 `default_g2_weight` 2.45 绘制） | 22.dp                                   |
| defaultColor() | @Composable () -> Color | 默认背景颜色：COUI 无模糊回退色 `?couiColorBar` | FloatingNavigationBarDefaults.color() |
| ShadowElevation | Dp                     | 焦散阴影模糊半径（`SHADOW_CAUSTIC_DOWN`，alpha 45） | 24.dp           |
| backdropSpec(isDark) | MaterialBackdropSpec | COUI 17 底栏材质 `TYPE_FRAMEWORK_BOTTOM_BAR`，与 `FloatingNavigationBarDefaults.backdropSpec` 相同 | 25px 模糊 + 两层混合色 |
| OutSidePadding | PaddingValues           | 内容胶囊的默认外部内边距（两侧 16dp；44dp 胶囊连同上下共 76dp） | PaddingValues(horizontal = 16.dp, vertical = 16.dp) |
| MinHeight      | Dp                      | 条目工具栏的最小高度 | 76.dp                             |
| HorizontalOutSidePaddingCompact / Medium / Expanded | Dp | 宽度小于 600dp / 小于 840dp / 不小于 840dp 时的两侧边距 | 16.dp / 24.dp / 40.dp |
| horizontalOutSidePadding(width) | Dp     | 按工具栏宽度取两侧边距 | -                               |
| MinGroupSpacing | Dp                     | 分组间最小间距 | 16.dp                                   |
| CapsuleHeight  | Dp                      | 胶囊高度       | 44.dp                                   |
| CapsuleInnerPadding | Dp                 | 多个条目时首尾条目外侧的胶囊宽度 | 6.dp                  |
| ItemSpacing    | Dp                      | 条目间距       | 4.dp                                    |
| ItemMinWidth / ItemHeight | Dp           | 图标项尺寸     | 44.dp / 40.dp                           |
| TitleMinWidth / TitleMaxWidth | Dp       | 文字项宽度范围 | 52.dp / 200.dp                          |
| TitleFontSize  | TextUnit                | 标题字号       | 16.sp                                   |
| RedDotDiameter / RedDotOffset | Dp       | 红点直径；圆心距右上角的距离 | 6.dp / 12.dp              |
| MaxVisibleItems | Int                    | 出现溢出项前每组最多显示的条目数 | 6                     |
| itemColors()   | FloatingToolbarItemColors | `?couiColorLabelPrimary` / `?couiColorLabelTertiary` | -              |

### ToolbarPosition (用于 Scaffold)

请参考 [Scaffold](../components/scaffold#toolbarposition-选项) 文档中的 `ToolbarPosition` 选项。

## 进阶用法

### 自定义样式

```kotlin
FloatingToolbar(
    color = COUITheme.colorScheme.containerTheme,
    cornerRadius = 16.dp,
    outSidePadding = PaddingValues(24.dp),
    showDivider = false
) {
    Row(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) { // 或 Column
        IconButton(onClick = { /* 操作 1 */ }) {
            Icon(COUIIcons.Edit, contentDescription = "编辑", tint = COUITheme.colorScheme.labelOnColor)
        }
        IconButton(onClick = { /* 操作 2 */ }) {
            Icon(COUIIcons.Delete, contentDescription = "删除", tint = COUITheme.colorScheme.labelOnColor)
        }
    }
}
```

### 带分割线的工具栏

设置 `showDivider = true` 可在工具栏四周绘制一圈细分割线；设置 `shadowElevation = 0.dp` 可关闭投影：

```kotlin
FloatingToolbar(
    showDivider = true,
    shadowElevation = 0.dp
) {
    Row(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = { /* 操作 1 */ }) {
            Icon(COUIIcons.Edit, contentDescription = "编辑")
        }
        IconButton(onClick = { /* 操作 2 */ }) {
            Icon(COUIIcons.Delete, contentDescription = "删除")
        }
    }
}
```

### 垂直排列内容

```kotlin
FloatingToolbar {
    Column(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = { /* 操作 1 */ }) {
            Icon(COUIIcons.Edit, contentDescription = "编辑")
        }
        IconButton(onClick = { /* 操作 2 */ }) {
            Icon(COUIIcons.Delete, contentDescription = "删除")
        }
    }
}
```

### 结合 Scaffold 改变位置

```kotlin
Scaffold(
    floatingToolbar = {
        FloatingToolbar {
            // 工具栏内容
        }
    },
    floatingToolbarPosition = ToolbarPosition.CenterEnd // 放置在右侧居中
)
```

### 显示与隐藏

请切换 `visible`，不要用 `AnimatedVisibility` 包裹工具栏。工具栏会以中心为轴缩放出现（COUI 17 `COUIBottomFloatingToolbar` 弹簧，response 0.5 / bounce 0.4），并缩放到消失（response 0.25 / bounce 0），过程中阴影保持完整；`AnimatedVisibility` 的淡入淡出会在动画期间裁掉阴影。隐藏的工具栏仍占据布局位置，但不再响应触摸。

```kotlin
var showToolbar by remember { mutableStateOf(true) }

FloatingToolbar(visible = showToolbar) {
    // 工具栏内容
}
```

### 底栏材质模糊

COUI 17 用框架底栏材质绘制工具栏：透明背景上叠 25px 背景模糊和两层混合色。coui-ui 自身不采集背景：通过 `LocalMaterialBackdrop` 提供 `MaterialBackdropProvider`，开启 `materialEffect` 时工具栏用 `FloatingToolbarDefaults.backdropSpec` 取代 `color`；没有提供时保留 `color`，即 COUI 的无模糊回退。背景采集层只能记录页面内容，不能包含工具栏本身：把工具栏放在采集层之外（例如 `Scaffold` 的 `floatingToolbar`）。

```kotlin
CompositionLocalProvider(LocalMaterialBackdrop provides backdropProvider) {
    FloatingToolbar {
        // 工具栏内容
    }
}
```
