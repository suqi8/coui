# MarkWithDividerPreference

`MarkWithDividerPreference` 是一分为二的设置项，对应 COUI 的 `COUIMarkWithDividerPreference`（布局 `coui_mark_with_divider_preference`，样式 `Preference.COUI.COUIRadioWithDividerPreference`）：左侧可点击的主区域（通常进入详情页）、一条竖分割线，以及可独立选中的选中标记。通常多行组成单选组，例如已保存网络列表中用标记选定当前网络。

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=markWithDividerPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.preference.MarkWithDividerPreference
```

## 基本用法

```kotlin
val networks = listOf("Home-5G", "Office")
var selected by remember { mutableIntStateOf(0) }

Card {
    CardListGroup {
        networks.forEachIndexed { index, name ->
            item {
                MarkWithDividerPreference(
                    checked = selected == index,
                    onCheckedChange = { if (it) selected = index },
                    title = name,
                    onClick = { /* 打开详情页 */ }
                )
            }
        }
    }
}
```

## 交互

- 主区域是一个 `BasicComponent`：只有它显示按压反馈、获得卡片分组内边距，并触发 `onClick`。
- 尾部区域（分割线 + 标记）与主区域等高，整个区域都是标记的触控目标（标记本身不可点击）。点击时以取反后的状态回调 `onCheckedChange`，与 COUI 的 `TwoStatePreference.onClick` 一致；单选组中忽略 `false` 即可。不播放触感，也不绘制按压反馈。无障碍服务将该区域识别为单选按钮。
- 分割线为 1dp x 22dp，颜色 `?couiColorDivider`，与标记间隔 16dp；标记距卡片内行尾 16dp。

## 组件状态

### 禁用状态

```kotlin
MarkWithDividerPreference(
    checked = true,
    onCheckedChange = {},
    title = "Disabled",
    onClick = {},
    enabled = false
)
```

## 属性

### MarkWithDividerPreference 属性

| 属性名           | 类型                               | 说明                                   | 默认值                                  | 必须 |
| ---------------- | ---------------------------------- | -------------------------------------- | --------------------------------------- | ---- |
| checked          | Boolean                            | 该行是否带有标记                       | -                                       | 是   |
| onCheckedChange  | (Boolean) -> Unit                  | 点击标记区域时的回调，参数为取反后的状态 | -                                     | 是   |
| title            | String                             | 设置项标题                             | -                                       | 是   |
| modifier         | Modifier                           | 组件修饰符                             | Modifier                                | 否   |
| onClick          | (() -> Unit)?                      | 主区域点击回调；为 null 时不可点击     | null                                    | 否   |
| titleColor       | BasicComponentColors               | 标题文本颜色配置                       | BasicComponentDefaults.titleColor()     | 否   |
| summary          | String?                            | 设置项摘要                             | null                                    | 否   |
| summaryColor     | BasicComponentColors               | 摘要文本颜色配置                       | BasicComponentDefaults.summaryColor()   | 否   |
| startAction      | @Composable (() -> Unit)?          | 主区域左侧自定义内容                   | null                                    | 否   |
| endActions       | @Composable (RowScope.() -> Unit)? | 主区域右侧自定义内容（分割线之前）     | null                                    | 否   |
| bottomAction     | @Composable (() -> Unit)?          | 主区域底部自定义内容                   | null                                    | 否   |
| markColors       | RadioButtonColors                  | 标记颜色配置                           | RadioButtonDefaults.radioButtonColors() | 否   |
| dividerColor     | Color                              | 竖分割线颜色                           | DividerDefaults.DividerColor            | 否   |
| insideMargin     | PaddingValues                      | 主区域内部内容边距                     | BasicComponentDefaults.InsideMargin     | 否   |
| cardListPosition | CardListPosition | 行在卡片分组中的位置（[CardListGroup](./card.md#cardlistgroup)），额外内边距加在主区域 | LocalCardListPosition.current（在 CardListGroup 外为 None） | 否 |
| holdDownState    | Boolean                            | 主区域是否处于按下状态                 | false                                   | 否   |
| enabled          | Boolean                            | 主区域与标记是否可用                   | true                                    | 否   |
