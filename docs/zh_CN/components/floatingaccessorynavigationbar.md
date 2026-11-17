# FloatingAccessoryNavigationBar

`FloatingAccessoryNavigationBar` 复刻 ColorOS 17 带附件的悬浮导航栏（`COUIBottomFloatingAccessoryNavigationBar`）：在 [FloatingNavigationBar](./navigationbar) 胶囊的起始侧和末尾侧各可放一个 56dp 圆形附件，例如新建或搜索按钮。隐藏的附件收在胶囊后面，显示时按 COUI 17 融球弹簧滑出。

<div style="position: relative; height: 220px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=floatingAccessoryNavigationBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.FloatingAccessoryNavigationBar
import io.github.suqi8.coui.kmp.basic.FloatingNavigationBarAccessory
import io.github.suqi8.coui.kmp.basic.FloatingNavigationBarItem
```

## 基本用法

```kotlin
var selected by remember { mutableIntStateOf(0) }
var searchShown by remember { mutableStateOf(true) }

FloatingAccessoryNavigationBar(
    endAccessoryVisible = searchShown,
    startAccessory = {
        FloatingNavigationBarAccessory(
            onClick = { /* 新建 */ },
            icon = COUIIcons.Add,
            contentDescription = "Create",
        )
    },
    endAccessory = {
        FloatingNavigationBarAccessory(
            onClick = { /* 搜索 */ },
            icon = COUIIcons.Search,
            contentDescription = "Search",
        )
    },
) {
    items.forEachIndexed { index, item ->
        FloatingNavigationBarItem(
            selected = selected == index,
            onClick = { selected = index },
            icon = item.icon,
            label = item.label,
        )
    }
}
```

导航项的行为与 `FloatingNavigationBar` 完全一致：滑过胶囊即可选择、大图标、红点以及项数变化时的弹簧动画。

## 切换附件图标

给 `FloatingNavigationBarAccessory` 传入不同的 `icon`，旧图标缩小淡出、新图标放大淡入（`setIconDrawable(_, true)`）：

```kotlin
FloatingNavigationBarAccessory(
    onClick = { searching = !searching },
    icon = if (searching) COUIIcons.Close else COUIIcons.Search,
    contentDescription = if (searching) "Close" else "Search",
)
```

## 材质背景

与胶囊一样，存在 `LocalMaterialBackdrop` 提供者时附件绘制 COUI 17 `TYPE_FRAMEWORK_BOTTOM_BAR` 背景模糊（见 [Button](./button)）；否则保留不透明的 `?couiColorBar` 填充。

## 属性

### FloatingAccessoryNavigationBar

| 属性名                     | 类型                      | 说明                                     | 默认值                                              | 是否必须 |
| -------------------------- | ------------------------- | ---------------------------------------- | --------------------------------------------------- | -------- |
| modifier                   | Modifier                  | 应用于导航栏容器的修饰符                 | Modifier                                            | 否       |
| backgroundModifier         | Modifier                  | 应用于胶囊内部、填充之上                 | Modifier                                            | 否       |
| startAccessoryVisible      | Boolean                   | 是否显示起始侧附件                       | true                                                | 否       |
| endAccessoryVisible        | Boolean                   | 是否显示末尾侧附件                       | true                                                | 否       |
| materialEffect             | Boolean                   | 是否绘制 COUI 17 材质与融球连接          | `LocalCOUIMaterialEffectEnabled.current` | 否       |
| showDivider                | Boolean                   | 是否在胶囊外绘制分割线环                 | false                                               | 否       |
| defaultWindowInsetsPadding | Boolean                   | 是否让导航栏避开导航栏与标题栏 insets    | true                                                | 否       |
| color                      | Color                     | 无背景模糊时的胶囊填充色                 | FloatingNavigationBarDefaults.color()               | 否       |
| cornerRadius               | Dp                        | 胶囊圆角                                 | FloatingNavigationBarDefaults.CornerRadius          | 否       |
| horizontalOutSidePadding   | Dp                        | 小屏上的两侧内边距                       | FloatingNavigationBarDefaults.HorizontalOutSidePadding | 否    |
| shadowElevation            | Dp                        | 胶囊阴影的模糊半径                       | FloatingNavigationBarDefaults.ShadowElevation       | 否       |
| startAccessory             | (@Composable () -> Unit)? | 胶囊起始侧的视图；null 不预留位置        | null                                                | 否       |
| endAccessory               | (@Composable () -> Unit)? | 胶囊末尾侧的视图；null 不预留位置        | null                                                | 否       |
| content                    | @Composable () -> Unit    | FloatingNavigationBarItem 列表           | -                                                   | 是       |

### FloatingNavigationBarAccessory

| 属性名             | 类型        | 说明                                       | 默认值                                            | 是否必须 |
| ------------------ | ----------- | ------------------------------------------ | ------------------------------------------------- | -------- |
| onClick            | () -> Unit  | 点击附件时回调                             | -                                                 | 是       |
| icon               | ImageVector | 以自身尺寸绘制在中心的图标                 | -                                                 | 是       |
| contentDescription | String?     | 无障碍标签                                 | -                                                 | 是       |
| modifier           | Modifier    | 应用于附件的修饰符                         | Modifier                                          | 否       |
| enabled            | Boolean     | 附件是否响应触摸                           | true                                              | 否       |
| materialEffect     | Boolean     | 是否绘制材质、聚光与完整的图标切换效果     | `LocalCOUIMaterialEffectEnabled.current` | 否       |
| color              | Color       | 无背景模糊时的填充色                       | FloatingNavigationBarDefaults.color()             | 否       |
| iconTint           | Color       | 图标着色；Unspecified 不着色               | FloatingAccessoryNavigationBarDefaults.iconTint() | 否       |
| shadowElevation    | Dp          | 投影的模糊半径                             | FloatingNavigationBarDefaults.ShadowElevation     | 否       |

### FloatingAccessoryNavigationBarDefaults

| 常量          | 类型 | 默认值 | COUI 来源                                                     |
| ------------- | ---- | ------ | ------------------------------------------------------------- |
| AccessorySize | Dp   | 56.dp  | `mAccessorySize` = coui_bottom_floating_navigation_bar_height |

| 方法       | 说明                                                                |
| ---------- | ------------------------------------------------------------------- |
| iconTint() | `labelPrimary`（`?couiColorLabelPrimary`）；COUI 按原样绘制 drawable |

## 行为

- 设置了的附件无论是否显示都会预留 56dp 位置和 12dp 间距，因此显示或隐藏附件都不会移动胶囊。
- 宽度小于 600dp 时导航栏两侧保留 16dp 内边距，起始附件贴起始边、末尾附件贴末尾边，胶囊在二者之间居中；更宽时没有两侧内边距，附件与胶囊作为一组居中。
- 隐藏的附件位于胶囊起始或末尾边缘内侧。显示时它滑出并从 0.5 缩放到 1，弹簧 response 0.55、bounce 0.35；透明度随胶囊遮住它的比例变化，两者相接时像融球一样连在一起。已停稳的隐藏附件不接收触摸。
- 附件具有与胶囊相同的触摸动效（按压缩放、橡皮筋拖动、形变）、边缘光与焦散阴影，以及 `TYPE_BOTTOM_NAVIGATION` 触摸聚光；没有按压遮罩。
