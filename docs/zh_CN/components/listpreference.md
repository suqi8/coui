# ListPreference

`ListPreference` 是点击后弹出底部单选面板的偏好行,对应 ColorOS 的 COUIListPreference(COUIAlertDialog_BottomAssignment + coui_select_dialog_singlechoice 行)。偏好行行末显示当前选中项文字与 COUI 弹出指示箭头;面板中选中行行末显示主题色对勾。点击面板条目立即提交选择并收起面板。

`MultiSelectListPreference` 是多选变体,对应 COUIMultiSelectListPreference:面板行行末为复选框,仅在点击确定按钮时提交选择。

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
| cancelButtonText      | String                    | 面板取消按钮文字                     | -                                            | 是   |
| modifier              | Modifier                  | 应用于偏好行的修饰符                 | Modifier                                     | 否   |
| titleColor            | BasicComponentColors      | 标题颜色配置                         | BasicComponentDefaults.titleColor()          | 否   |
| summary               | String?                   | 偏好行摘要                           | null                                         | 否   |
| summaryColor          | BasicComponentColors      | 摘要颜色配置                         | BasicComponentDefaults.summaryColor()        | 否   |
| dialogTitle           | String                    | 选择面板标题                         | title                                        | 否   |
| colors                | ListPreferenceColors      | 面板行颜色配置                       | ListPreferenceDefaults.listPreferenceColors() | 否  |
| startAction           | @Composable (() -> Unit)? | 自定义起始侧内容                     | null                                         | 否   |
| bottomAction          | @Composable (() -> Unit)? | 自定义底部内容                       | null                                         | 否   |
| insideMargin          | PaddingValues             | 行内边距                             | BasicComponentDefaults.InsideMargin          | 否   |
| cardListPosition | CardListPosition | 该行在卡片组中的位置，圆角外边缘会获得额外内边距 | CardListPosition.None | 否 |
| enabled               | Boolean                   | 行是否可点击                         | true                                         | 否   |
| showValue             | Boolean                   | 是否在行末显示当前选中项             | true                                         | 否   |
| renderInRootScaffold  | Boolean                   | 是否在根 Scaffold 渲染面板           | true                                         | 否   |
| onExpandedChange      | ((Boolean) -> Unit)?      | 面板展开 / 收起时的回调              | null                                         | 否   |

### MultiSelectListPreference

相对 `ListPreference` 新增 / 不同的属性:

| 属性                    | 类型                | 说明                             | 默认值                            | 必需 |
| ----------------------- | ------------------- | -------------------------------- | --------------------------------- | ---- |
| selectedIndices         | Set\<Int>           | 选中条目的索引集合               | -                                 | 是   |
| onSelectedIndicesChange | (Set\<Int>) -> Unit | 点击确定时返回新选择的回调       | -                                 | 是   |
| confirmButtonText       | String              | 面板确定按钮文字                 | -                                 | 是   |
| checkboxColors          | CheckboxColors      | 面板行复选框颜色                 | CheckboxDefaults.checkboxColors() | 否   |

### ListPreferenceDefaults

| 常量                     | 类型 | 默认值 | COUI 来源                                      |
| ------------------------ | ---- | ------ | ---------------------------------------------- |
| PanelItemMinHeight       | Dp   | 48.dp  | coui_delete_alert_dialog_button_height         |
| PanelItemVerticalPadding | Dp   | 10.dp  | alert_dialog_single_list_padding_vertical      |
| PanelItemIndicatorSpacing | Dp  | 16.dp  | coui_dialog_layout_margin_horizontal           |
| PanelItemSummarySpacing  | Dp   | 2.dp   | coui_alert_dialog_content_panel_padding_top    |
| CheckIconSize            | Dp   | 24.dp  | COUI 24dp 选择控件                             |
| ButtonBarTopPadding      | Dp   | 6.dp   | alert_dialog_single_list_last_item_padding_bottom |
| ButtonBarBottomPadding   | Dp   | 12.dp  | 库内约定                                       |

### `listPreferenceColors()` 工厂

| 参数                           | 类型  | 默认值                                            | COUI 角色                |
| ------------------------------ | ----- | ------------------------------------------------- | ------------------------ |
| itemTextColor                  | Color | COUITheme.colorScheme.onSurface                  | couiColorPrimaryNeutral  |
| disabledItemTextColor          | Color | COUITheme.colorScheme.disabledOnSecondaryVariant | couiColorDisabledNeutral |
| itemSummaryColor               | Color | COUITheme.colorScheme.onSurfaceSecondary         | couiColorSecondNeutral   |
| disabledItemSummaryColor       | Color | COUITheme.colorScheme.disabledOnSecondaryVariant | couiColorDisabledNeutral |
| selectedIndicatorColor         | Color | COUITheme.colorScheme.primary                    | couiColorPrimary         |
| disabledSelectedIndicatorColor | Color | COUITheme.colorScheme.disabledPrimary            | -                        |

## 行为

- 点击偏好行会弹出以 `dialogTitle` 为标题的 `OverlayBottomSheet` 面板,面板存续期间偏好行保持按压态,弹出时触发 context-click 震动。
- 单选:点击条目立即回调 `onSelectedIndexChange` 并收起面板;取消按钮或点击外部收起且不做修改(对应 COUIListPreferenceDialogFragment)。
- 多选:切换条目只更新暂存选择;点击确定按钮才通过 `onSelectedIndicesChange` 提交,取消 / 点击外部会丢弃修改(对应 COUIMultiSelectListPreferenceDialogFragment)。
- 相邻面板行之间绘制发丝线分割线(0.33dp,couiColorDivider),最后一行之后不绘制。
- `enabled = false` 的条目仍会显示但不可选中,使用禁用态文字颜色。
