# BasicComponent

`BasicComponent` 是 COUI 中的基础标准组件。提供了标题、摘要以及左右两侧的可自定义内容区域，常用于构建列表项、设置项等界面元素。

本项目以此为基础提供了一些扩展组件，方便开发者快速构建符合设计规范的 UI 组件，详见[扩展组件](../components/#扩展组件)的使用。

<div style="position: relative; height: 350px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=basicComponent" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.BasicComponent
```

## 基本用法

BasicComponent 组件可以用于展示标题和摘要信息：

```kotlin
BasicComponent(
    title = "设置项标题",
    summary = "这里是设置项的描述文本"
)
```

## 组件变体

### 可点击组件

```kotlin
BasicComponent(
    title = "Wi-Fi",
    summary = "已连接到 HOME-WiFi",
    onClick = { /* 处理点击事件 */ }
)
```

### 带左侧图标的组件

```kotlin
BasicComponent(
    title = "昵称",
    summary = "一段简介",
    startAction = {
        Icon(
            imageVector = COUIIcons.Contacts,
            contentDescription = "头像图标",
            tint = COUITheme.colorScheme.labelPrimary
        )
    },
    onClick = { /* 处理点击事件 */ }
)
```

### 带右侧操作的组件

```kotlin
var isFlightMode by remember { mutableStateOf(false) }

BasicComponent(
    title = "飞行模式",
    endActions = {
        Switch(
            checked = isFlightMode,
            onCheckedChange = { isFlightMode = it }
        )
    }
)
```

### 自定义内容变体

除了带标题和摘要的形式外，BasicComponent 还提供了一个接收自定义内容的重载。当你希望完全掌控组件内部布局，但仍复用容器与交互效果时，可以使用该形式：

```kotlin
BasicComponent(
    startAction = {
        Icon(
            imageVector = COUIIcons.Contacts,
            contentDescription = "头像图标",
            tint = COUITheme.colorScheme.labelPrimary
        )
    },
    endActions = {
        Text("详情")
    }
) {
    Text(
        text = "自定义标题",
        style = COUITheme.textStyles.headlineXS
    )
    Text(
        text = "自定义内容说明",
        style = COUITheme.textStyles.bodyS
    )
}
```

## 组件状态

### 禁用状态

```kotlin
BasicComponent(
    title = "移动网络",
    summary = "SIM卡未插入",
    enabled = false
)
```

## 属性

### BasicComponent 属性（标题和摘要变体）

| 属性名            | 类型                               | 说明                 | 默认值                                | 是否必须 |
| ----------------- | ---------------------------------- | -------------------- | ------------------------------------- | -------- |
| modifier          | Modifier                           | 应用于组件的修饰符   | Modifier                              | 否       |
| title             | String?                            | 组件标题             | null                                  | 否       |
| titleColor        | BasicComponentColors               | 标题颜色配置         | BasicComponentDefaults.titleColor()   | 否       |
| summary           | String?                            | 组件摘要             | null                                  | 否       |
| summaryColor      | BasicComponentColors               | 摘要颜色配置         | BasicComponentDefaults.summaryColor() | 否       |
| startAction       | @Composable (() -> Unit)?          | 组件左侧的可组合内容 | null                                  | 否       |
| endActions        | @Composable (RowScope.() -> Unit)? | 组件右侧的可组合内容 | null                                  | 否       |
| bottomAction      | @Composable (() -> Unit)?          | 组件底部的可组合内容 | null                                  | 否       |
| insideMargin      | PaddingValues                      | 组件内部边距         | BasicComponentDefaults.InsideMargin   | 否       |
| cardListPosition  | CardListPosition                   | 该行在卡片组（[CardListGroup](./card.md#cardlistgroup)）中的位置，圆角外边缘会获得额外内边距 | LocalCardListPosition.current（CardListGroup 之外为 None） | 否       |
| onClick           | (() -> Unit)?                      | 点击组件时触发的回调 | null                                  | 否       |
| onClickLabel      | String?                            | 无障碍点击操作描述   | null                                  | 否       |
| role              | Role?                              | 无障碍语义角色       | null                                  | 否       |
| holdDownState     | Boolean                            | 组件是否处于按下状态 | false                                 | 否       |
| enabled           | Boolean                            | 组件是否可用         | true                                  | 否       |
| selected          | Boolean                            | 该行是否选中：按压层保持显示，在 CardListGroup 内时该行上下的分隔线隐藏 | false | 否       |
| startBadge        | Boolean                            | 是否在 startAction 右上角显示红点 | false | 否       |
| endBadgeCount     | Int?                               | 尾部内容前的红点：null 不显示，0 为纯点，大于 0 显示数字 | null | 否       |
| endIcon           | @Composable (() -> Unit)?          | endActions 前的 16dp 图标 | null                                  | 否       |
| interactionSource | MutableInteractionSource?          | 组件的交互源         | null                                  | 否       |

### BasicComponent 属性（自定义内容变体）

| 属性名            | 类型                               | 说明                 | 默认值                              | 是否必须 |
| ----------------- | ---------------------------------- | -------------------- | ----------------------------------- | -------- |
| modifier          | Modifier                           | 应用于组件的修饰符   | Modifier                            | 否       |
| startAction       | @Composable (() -> Unit)?          | 组件左侧的可组合内容 | null                                | 否       |
| endActions        | @Composable (RowScope.() -> Unit)? | 组件右侧的可组合内容 | null                                | 否       |
| bottomAction      | @Composable (() -> Unit)?          | 组件底部的可组合内容 | null                                | 否       |
| insideMargin      | PaddingValues                      | 组件内部边距         | BasicComponentDefaults.InsideMargin | 否       |
| cardListPosition  | CardListPosition                   | 该行在卡片组（[CardListGroup](./card.md#cardlistgroup)）中的位置，圆角外边缘会获得额外内边距 | LocalCardListPosition.current（CardListGroup 之外为 None） | 否       |
| onClick           | (() -> Unit)?                      | 点击组件时触发的回调 | null                                | 否       |
| onClickLabel      | String?                            | 无障碍点击操作描述   | null                                | 否       |
| role              | Role?                              | 无障碍语义角色       | null                                | 否       |
| holdDownState     | Boolean                            | 组件是否处于按下状态 | false                               | 否       |
| enabled           | Boolean                            | 组件是否可用         | true                                | 否       |
| selected          | Boolean                            | 该行是否选中：按压层保持显示，在 CardListGroup 内时该行上下的分隔线隐藏 | false | 否       |
| startBadge        | Boolean                            | 是否在 startAction 右上角显示红点 | false | 否       |
| endBadgeCount     | Int?                               | 尾部内容前的红点：null 不显示，0 为纯点，大于 0 显示数字 | null | 否       |
| endIcon           | @Composable (() -> Unit)?          | endActions 前的 16dp 图标 | null                                  | 否       |
| interactionSource | MutableInteractionSource?          | 组件的交互源         | null                                | 否       |
| content           | @Composable ColumnScope.() -> Unit | 组件内部的可组合内容 | -                                   | 是       |

### BasicComponentDefaults 对象

BasicComponentDefaults 对象提供了 BasicComponent 组件的默认值和颜色配置。

#### BasicComponentDefaults 常量

| 常量名       | 类型          | 说明           | 默认值               |
| ------------ | ------------- | -------------- | -------------------- |
| MinHeight    | Dp            | 行的最小高度（未计入卡片组位置内边距） | 48.dp |
| InsideMargin | PaddingValues | 组件的内部边距 | PaddingValues(horizontal = 16.dp, vertical = 10.dp) |
| StartActionSpacing | Dp | startAction 与标题的间距（coui_preference_icon_margin_right） | 16.dp |
| EndActionsSpacing | Dp | 标题与尾部内容的最小间距（support_preference_title_margin_end_in_right） | 16.dp |
| EndLowPriorityMinWidth | Dp | 标题过长时尾部内容的保底宽度；尾部内容不超过它时总能拿到完整宽度 | 80.dp |
| EndDecorationSpacing | Dp | 尾部红点 / 尾部图标之后的间距 | 4.dp |
| EndIconSize | Dp | endIcon 的尺寸 | 16.dp |
| StartBadgeInset | Dp | 起始红点相对 startAction 角的内缩 | 2.dp |
| StartActionMinTopMargin | Dp | 顶对齐时起始内容距行顶的最小距离 | 8.dp |

`startActionVerticalMargin(heightDp, lineCount)` 返回 COUI 起始内容的上下边距：24dp 内容 12dp（文字两行时 20dp），32dp 为 10dp / 16dp，36dp 为 12dp / 14dp，50dp 为 8dp，其余尺寸 14dp。行高会撑到容纳起始内容加这组边距；标题与摘要超过两行时，起始内容改为对齐前两行文字的中线。起始内容与标题已有 16dp 间距，图标不要再自带 padding。

#### BasicComponentDefaults 方法

| 方法名         | 类型                 | 说明             |
| -------------- | -------------------- | ---------------- |
| titleColor()   | BasicComponentColors | 创建标题颜色配置（`labelPrimary`，禁用 `disabledNeutral`） |
| summaryColor() | BasicComponentColors | 创建摘要颜色配置（`labelSecondary`，即 COUI `couiColorSecondNeutral`；禁用 `disabledNeutral`） |

标题使用 `textStyles.headlineXS`（16sp Medium，COUI `TextAppearance.COUI.List.Title`）。摘要使用 `textStyles.bodyS`（13sp，COUI `TextAppearance.COUI.Preference.Summary`），紧贴标题下方：COUI 17 删除了 COUI 16 摘要上的 2dp `layout_marginTop`。

### BasicComponentColors 类

用于配置组件的颜色状态。

| 属性名        | 类型  | 说明           |
| ------------- | ----- | -------------- |
| color         | Color | 正常状态的颜色 |
| disabledColor | Color | 禁用状态的颜色 |

## 进阶用法

### 复杂布局组件

```kotlin
BasicComponent(
    title = "音量",
    summary = "媒体音量：70%",
    startAction = {
        Icon(
            imageVector = COUIIcons.Play,
            contentDescription = "音量图标",
            tint = COUITheme.colorScheme.labelPrimary
        )
    },
    endActions = {
        IconButton(onClick = { /* 减小音量 */ }) {
            Icon(
                imageVector = COUIIcons.Remove,
                contentDescription = "减小音量",
                tint = COUITheme.colorScheme.labelPrimary
            )
        }
        Text("70%")
        IconButton(onClick = { /* 增大音量 */ }) {
            Icon(
                imageVector = COUIIcons.Add,
                contentDescription = "增大音量",
                tint = COUITheme.colorScheme.labelPrimary
            )
        }
    }
)
```

### 自定义样式组件

```kotlin
BasicComponent(
    title = "自定义组件",
    summary = "使用自定义颜色",
    titleColor = BasicComponentColors(
        color = Color.Blue,
        disabledColor = Color.Gray
    ),
    summaryColor = BasicComponentColors(
        color = Color.DarkGray,
        disabledColor = Color.LightGray
    ),
    insideMargin = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    onClick = { /* 处理选项点击 */ }
)
```

### 列表中使用

```kotlin
val options = listOf("选项1", "选项2", "选项3", "选项4")

Column {
    options.forEach { option ->
        BasicComponent(
            title = option,
            onClick = { /* 处理选项点击 */ }
        )
    }
}
```

### 在卡片组中使用

在 `Card` 中用 `CardListGroup` 包裹各行，每一行即可获得其卡片列表位置（圆角边缘上 2dp 的首 / 尾内边距）。
不在分组内的行保持 `CardListPosition.None`，显式传入的 `cardListPosition` 始终优先。分组还会绘制对齐各行标题的行间分隔线，并在 `selected` 行上下隐藏，因此行之间不要再插入 `HorizontalDivider`。详见 [CardListGroup](./cardlistgroup.md)。

```kotlin
Card {
    CardListGroup {
        options.forEach { option ->
            item(key = option) {
                BasicComponent(
                    title = option,
                    onClick = { /* 处理选项点击 */ }
                )
            }
        }
    }
}
```
