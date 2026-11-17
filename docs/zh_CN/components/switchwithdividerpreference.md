# SwitchWithDividerPreference

`SwitchWithDividerPreference` 是一分为二的设置项，对应 COUI 的 `COUISwitchWithDividerPreference`（布局 `coui_switch_with_divider_preference`）：左侧可点击的主区域（通常进入详情页）、一条竖分割线，以及可独立切换的开关。

<div style="position: relative; height: 230px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=switchWithDividerPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.preference.SwitchWithDividerPreference
```

## 基本用法

```kotlin
var checked by remember { mutableStateOf(true) }

SwitchWithDividerPreference(
    checked = checked,
    onCheckedChange = { checked = it },
    title = "WLAN",
    summary = "Connected",
    onClick = { /* 打开详情页 */ }
)
```

## 交互

- 主区域是一个 `BasicComponent`：只有它显示按压反馈、获得卡片分组内边距，并触发 `onClick`。
- 尾部区域（分割线 + 开关）与主区域等高。点击该区域任意位置都会切换开关并播放开关触感；开关本身也处理点击和拖动滑块。与 `SwitchPreference` 不同，按下开关不会让主区域进入按下态。该区域不绘制按压反馈。
- 分割线为 1dp x 22dp，颜色 `?couiColorDivider`，与开关间隔 16dp。

## 组件状态

### 禁用状态

```kotlin
SwitchWithDividerPreference(
    checked = true,
    onCheckedChange = {},
    title = "Disabled",
    onClick = {},
    enabled = false
)
```

## 属性

### SwitchWithDividerPreference 属性

| 属性名          | 类型                               | 说明                               | 默认值                                | 必须 |
| --------------- | ---------------------------------- | ---------------------------------- | ------------------------------------- | ---- |
| checked         | Boolean                            | 开关选中状态               | -                                     | 是   |
| onCheckedChange | (Boolean) -> Unit                  | 开关切换回调               | -                                     | 是   |
| title           | String                             | 设置项标题                         | -                                     | 是   |
| modifier        | Modifier                           | 组件修饰符                         | Modifier                              | 否   |
| onClick         | (() -> Unit)?                      | 主区域点击回调；为 null 时不可点击 | null                                  | 否   |
| titleColor      | BasicComponentColors               | 标题文本颜色配置                   | BasicComponentDefaults.titleColor()   | 否   |
| summary         | String?                            | 设置项摘要                         | null                                  | 否   |
| summaryColor    | BasicComponentColors               | 摘要文本颜色配置                   | BasicComponentDefaults.summaryColor() | 否   |
| startAction     | @Composable (() -> Unit)?          | 主区域左侧自定义内容               | null                                  | 否   |
| endActions      | @Composable (RowScope.() -> Unit)? | 主区域右侧自定义内容（分割线之前） | null                                  | 否   |
| bottomAction    | @Composable (() -> Unit)?          | 主区域底部自定义内容               | null                                  | 否   |
| switchColors  | SwitchColors                      | 开关颜色配置               | SwitchDefaults.switchColors()                      | 否   |
| dividerColor    | Color                              | 竖分割线颜色                       | DividerDefaults.DividerColor          | 否   |
| insideMargin    | PaddingValues                      | 主区域内部内容边距                 | BasicComponentDefaults.InsideMargin   | 否   |
| cardListPosition | CardListPosition | 行在卡片分组中的位置（[CardListGroup](./card.md#cardlistgroup)），额外内边距加在主区域 | LocalCardListPosition.current（在 CardListGroup 外为 None） | 否 |
| holdDownState   | Boolean                            | 主区域是否处于按下状态             | false                                 | 否   |
| enabled         | Boolean                            | 主区域与开关是否可用       | true                                  | 否   |
