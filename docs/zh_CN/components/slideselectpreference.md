# SlideSelectPreference

`SlideSelectPreference` 是在行末显示当前选中项状态文字并跟随上下选择指示符的设置项，对应 COUI 的 `COUISlideSelectPreference`（样式 `Preference.COUI.COUISelectPreference`，widget `coui_preference_widget_select`）。与官方类一致，它不内置弹层：点击后做什么由调用方决定（通常弹出列表弹窗或选择器），选定后再更新 `selectionText`。

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=slideSelectPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.preference.SlideSelectPreference
import io.github.suqi8.coui.kmp.preference.SlideSelectPreferenceDefaults
```

## 基本用法

```kotlin
val options = listOf("标准", "大", "超大")
var selected by remember { mutableIntStateOf(0) }

SlideSelectPreference(
    title = "字体大小",
    selectionText = options[selected],
    onClick = { /* 弹出弹窗或选择器，再更新 selected */ }
)
```

## 布局

- 状态文字（14sp 正文，`?couiColorSecondNeutral`）右对齐，最宽 162dp（`coui_preference_status_text_max_width`）；`selectionText` 为 null 或空串时隐藏。
- 指示符是 24dp 的 `coui_btn_select` 图形（上下两个实心三角），位于状态文字之后 16dp（`support_preference_image_padding_start`）。它仅作装饰，对无障碍服务隐藏。
- 状态文字与标题至少间隔 24dp（`support_preference_widget_focus_jump_margin_start`）。

## 组件状态

### 禁用状态

```kotlin
SlideSelectPreference(
    title = "Disabled",
    selectionText = "Standard",
    enabled = false
)
```

## 属性

### SlideSelectPreference 属性

| 属性名           | 类型                               | 说明                                          | 默认值                                                      | 必须 |
| ---------------- | ---------------------------------- | --------------------------------------------- | ----------------------------------------------------------- | ---- |
| title            | String                             | 设置项标题                                    | -                                                           | 是   |
| modifier         | Modifier                           | 组件修饰符                                    | Modifier                                                    | 否   |
| selectionText    | String?                            | 指示符前显示的当前选中项；null / 空串时隐藏   | null                                                        | 否   |
| onClick          | (() -> Unit)?                      | 行点击回调                                    | null                                                        | 否   |
| titleColor       | BasicComponentColors               | 标题文本颜色配置                              | BasicComponentDefaults.titleColor()                         | 否   |
| summary          | String?                            | 设置项摘要                                    | null                                                        | 否   |
| summaryColor     | BasicComponentColors               | 摘要文本颜色配置                              | BasicComponentDefaults.summaryColor()                       | 否   |
| colors           | SlideSelectPreferenceColors        | 状态文字与指示符颜色                          | SlideSelectPreferenceDefaults.slideSelectPreferenceColors() | 否   |
| startAction      | @Composable (() -> Unit)?          | 左侧自定义内容                                | null                                                        | 否   |
| endActions       | @Composable (RowScope.() -> Unit)? | 右侧自定义内容，位于状态文字之前              | null                                                        | 否   |
| bottomAction     | @Composable (() -> Unit)?          | 底部自定义内容                                | null                                                        | 否   |
| insideMargin     | PaddingValues                      | 内部内容边距                                  | BasicComponentDefaults.InsideMargin                         | 否   |
| cardListPosition | CardListPosition | 行在卡片分组中的位置（[CardListGroup](./card.md#cardlistgroup)），圆角外边缘会获得额外内边距 | LocalCardListPosition.current（在 CardListGroup 外为 None） | 否 |
| holdDownState    | Boolean                            | 行是否处于按下状态，例如弹层显示期间          | false                                                       | 否   |
| enabled          | Boolean                            | 行是否可点击                                  | true                                                        | 否   |

### SlideSelectPreferenceDefaults 对象

| 常量                      | 类型 | 默认值 | COUI 来源                                                                 |
| ------------------------- | ---- | ------ | ------------------------------------------------------------------------- |
| SelectionTextPaddingStart | Dp   | 8.dp   | support_preference_widget_focus_jump_margin_start（24dp）减去 BasicComponent 标题后的 16dp |
| SelectionTextMaxWidth     | Dp   | 162.dp | coui_preference_status_text_max_width                                     |
| IndicatorMarginStart      | Dp   | 16.dp  | support_preference_image_padding_start                                    |
| IndicatorSize             | Dp   | 24.dp  | coui_btn_select_*.png（xxhdpi 72px）                                      |

### `slideSelectPreferenceColors()` 工厂

| 参数                       | 类型  | 默认值                                        | COUI 角色               |
| -------------------------- | ----- | --------------------------------------------- | ----------------------- |
| selectionTextColor         | Color | COUITheme.colorScheme.labelSecondary      | couiColorSecondNeutral  |
| disabledSelectionTextColor | Color | COUITheme.colorScheme.labelTertiary | couiColorLabelTertiary  |
| indicatorColor             | Color | COUITheme.colorScheme.labelTertiary | coui_btn_select_normal  |
| disabledIndicatorColor     | Color | indicatorColor 的一半不透明度                 | coui_btn_select_disable |
