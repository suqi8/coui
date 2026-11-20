# ListPreference

`ListPreference` 是点击后弹出单选对话框的偏好行，对应 ColorOS 的 COUIListPreference（COUIListPreferenceDialogFragment：`COUIAlertDialog.BottomAssignment` 对话框 + `coui_select_dialog_singlechoice` 行）。偏好行是跳转行：行末显示当前选中项文字，其后是 COUI 跳转箭头（`coui_btn_next`）。对话框各行行末为单选按钮。点击条目立即提交选择并关闭对话框。

`MultiSelectListPreference` 是多选变体，对应 COUIMultiSelectListPreference：对话框行行末为复选框，仅在点击确定按钮时提交选择。

对话框是 `OverlayDialog`，因此两者都需要在 `Scaffold` 中使用。

<div style="position: relative; height: 360px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=listPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.preference.ListPreference
import io.github.suqi8.coui.kmp.preference.ListPreferenceDefaults
import io.github.suqi8.coui.kmp.preference.ListPreferenceEntry
import io.github.suqi8.coui.kmp.preference.MultiSelectListPreference
```

## 基本用法

```kotlin
val entries = remember {
    listOf(
        ListPreferenceEntry("浅色"),
        ListPreferenceEntry("深色"),
        ListPreferenceEntry("跟随系统", summary = "随系统深色模式切换"),
    )
}
var selectedIndex by remember { mutableIntStateOf(2) }

ListPreference(
    entries = entries,
    selectedIndex = selectedIndex,
    onSelectedIndexChange = { selectedIndex = it },
    title = "主题",
    cancelButtonText = "取消",
)
```

## 多选变体

```kotlin
var selectedIndices by remember { mutableStateOf(setOf(0, 1)) }

MultiSelectListPreference(
    entries = entries,
    selectedIndices = selectedIndices,
    onSelectedIndicesChange = { selectedIndices = it },
    title = "同步项目",
    confirmButtonText = "确定",
    cancelButtonText = "取消",
)
```

## 属性

### ListPreferenceEntry

| 属性    | 类型    | 说明                     | 默认值 | 必需 |
| ------- | ------- | ------------------------ | ------ | ---- |
| text    | String  | 条目文字(16sp,中等字重) | -    | 是   |
| summary | String? | 条目下方的可选摘要       | null   | 否   |
| enabled | Boolean | 条目是否可选             | true   | 否   |

### ListPreference

| 属性                  | 类型                      | 说明                                 | 默认值                                       | 必需 |
| --------------------- | ------------------------- | ------------------------------------ | -------------------------------------------- | ---- |
| entries               | List\<ListPreferenceEntry> | 可选条目列表                         | -                                            | 是   |
| selectedIndex         | Int                       | 选中条目索引(-1 表示未选)          | -                                            | 是   |
| onSelectedIndexChange | (Int) -> Unit             | 选中条目时的回调                     | -                                            | 是   |
| title                 | String                    | 偏好行标题                           | -                                            | 是   |
| cancelButtonText      | String                    | 对话框取消按钮文字                   | -                                            | 是   |
| modifier              | Modifier                  | 应用于偏好行的修饰符                 | Modifier                                     | 否   |
| titleColor            | BasicComponentColors      | 标题颜色配置                         | BasicComponentDefaults.titleColor()          | 否   |
| summary               | String?                   | 偏好行摘要                           | null                                         | 否   |
| summaryColor          | BasicComponentColors      | 摘要颜色配置                         | BasicComponentDefaults.summaryColor()        | 否   |
| dialogTitle           | String                    | 选择对话框标题                       | title                                        | 否   |
| colors                | ListPreferenceColors      | 对话框行颜色配置                     | ListPreferenceDefaults.listPreferenceColors() | 否  |
| startAction           | @Composable (() -> Unit)? | 自定义起始侧内容                     | null                                         | 否   |
| bottomAction          | @Composable (() -> Unit)? | 自定义底部内容                       | null                                         | 否   |
| insideMargin          | PaddingValues             | 行内边距                             | BasicComponentDefaults.InsideMargin          | 否   |
| cardListPosition | CardListPosition | 该行在卡片组（[CardListGroup](./card.md#cardlistgroup)）中的位置，圆角外边缘会获得额外内边距 | LocalCardListPosition.current（CardListGroup 之外为 None） | 否 |
| enabled               | Boolean                   | 行是否可点击                         | true                                         | 否   |
| showValue             | Boolean                   | 是否在行末显示当前选中项             | true                                         | 否   |
| renderInRootScaffold  | Boolean                   | 是否在根 Scaffold 渲染对话框         | true                                         | 否   |
| onExpandedChange      | ((Boolean) -> Unit)?      | 对话框显示 / 关闭时的回调            | null                                         | 否   |

### MultiSelectListPreference

相对 `ListPreference` 新增 / 不同的属性:

| 属性                    | 类型                | 说明                             | 默认值                            | 必需 |
| ----------------------- | ------------------- | -------------------------------- | --------------------------------- | ---- |
| selectedIndices         | Set\<Int>           | 选中条目的索引集合               | -                                 | 是   |
| onSelectedIndicesChange | (Set\<Int>) -> Unit | 点击确定时返回新选择的回调       | -                                 | 是   |
| confirmButtonText       | String              | 对话框确定按钮文字               | -                                 | 是   |
| checkboxColors          | CheckboxColors      | 对话框行复选框颜色               | CheckboxDefaults.checkboxColors() | 否   |

### ListPreferenceDefaults

| 常量                     | 类型 | 默认值 | COUI 来源                                      |
| ------------------------ | ---- | ------ | ---------------------------------------------- |
| PanelInsideMargin        | DpSize | DpSize(24.dp, 0.dp) | alert_dialog_single_list_padding_left / _right（对话框行左右内边距） |
| PanelItemMinHeight       | Dp   | 48.dp  | coui_delete_alert_dialog_button_height         |
| PanelItemVerticalPadding | Dp   | 10.dp  | alert_dialog_single_list_padding_vertical      |
| PanelItemIndicatorSpacing | Dp  | 16.dp  | coui_dialog_layout_margin_horizontal           |
| PanelItemSummarySpacing  | Dp   | 2.dp   | coui_alert_dialog_content_panel_padding_top    |
| CheckIconSize            | Dp   | 24.dp  | COUI 24dp 选择控件                             |
| ButtonBarTopPadding      | Dp   | 6.dp   | alert_dialog_single_list_last_item_padding_bottom（对话框不读取，其按钮栏沿用 COUI 17 间距） |
| ButtonBarBottomPadding   | Dp   | 12.dp  | 对话框不读取，其按钮栏沿用 COUI 17 间距        |

### `listPreferenceColors()` 工厂

| 参数                           | 类型  | 默认值                                            | COUI 角色                |
| ------------------------------ | ----- | ------------------------------------------------- | ------------------------ |
| itemTextColor                  | Color | COUITheme.colorScheme.labelPrimary                  | couiColorPrimaryNeutral  |
| disabledItemTextColor          | Color | COUITheme.colorScheme.disabledNeutral | couiColorDisabledNeutral |
| itemSummaryColor               | Color | COUITheme.colorScheme.labelSecondary         | couiColorSecondNeutral   |
| disabledItemSummaryColor       | Color | COUITheme.colorScheme.disabledNeutral | couiColorDisabledNeutral |
| selectedIndicatorColor         | Color | COUITheme.colorScheme.primary                    | couiColorPrimary         |
| disabledSelectedIndicatorColor | Color | COUITheme.colorScheme.disable            | -                        |

## 行为

- 点击偏好行会弹出以 `dialogTitle` 为标题的 `OverlayDialog`（COUI 17 底部对话框，大屏居中）。对话框显示期间偏好行不保持按压态（COUI 对话框不锁定锚点状态）；与 COUI 偏好行一致，不触发震动。
- 对话框每一行横跨对话框全宽、整行可点：起始侧为条目文字（16sp medium）与可选摘要（14sp），行末为单选按钮 / 复选框，左右内边距 24dp，最小高度 48dp。
- 单选：点击条目立即回调 `onSelectedIndexChange` 并关闭对话框，单选按钮不会先移动；取消按钮或点击外部关闭且不做修改（对应 COUIListPreferenceDialogFragment）。
- 多选:切换条目只更新暂存选择;点击确定按钮才通过 `onSelectedIndicesChange` 提交,取消 / 点击外部会丢弃修改(对应 COUIMultiSelectListPreferenceDialogFragment)。
- 相邻对话框行之间绘制发丝线分割线（0.33dp，couiColorDivider，左右缩进与行内边距相同），最后一行之后不绘制。
- `enabled = false` 的条目仍会显示但不可选中,使用禁用态文字颜色。
