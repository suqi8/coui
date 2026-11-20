# MultiSelectListPreference

`MultiSelectListPreference` 是点击后弹出多选对话框的偏好行，对应 ColorOS 的 COUIMultiSelectListPreference（COUIMultiSelectListPreferenceDialogFragment：`COUIAlertDialog.BottomAssignment` 对话框 + `coui_select_dialog_multichoice` 行）。偏好行是跳转行：行末按条目顺序显示已选条目文字，其后是 COUI 跳转箭头（`coui_btn_next`）。对话框行行末为复选框；切换条目只更新暂存选择，点击确定按钮时才提交。单选变体见 [ListPreference](./listpreference.md)。

<div style="position: relative; height: 200px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=multiSelectListPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

::: warning 注意
对话框是 `OverlayDialog`，因此 `MultiSelectListPreference` 需要在 `Scaffold` 中使用。
:::

## 引入

```kotlin
import io.github.suqi8.coui.kmp.preference.ListPreferenceEntry
import io.github.suqi8.coui.kmp.preference.MultiSelectListPreference
```

## 基本用法

```kotlin
val entries = remember {
    listOf(
        ListPreferenceEntry("照片"),
        ListPreferenceEntry("文档"),
        ListPreferenceEntry("联系人", summary = "联系人与通话记录"),
    )
}
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

## 组件状态

### 禁用状态

```kotlin
MultiSelectListPreference(
    entries = entries,
    selectedIndices = setOf(0),
    onSelectedIndicesChange = {},
    title = "已禁用",
    confirmButtonText = "确定",
    cancelButtonText = "取消",
    enabled = false,
)
```

`entries` 为空时偏好行同样禁用。单个条目可用 `ListPreferenceEntry(enabled = false)` 禁用：仍会显示但使用禁用态文字颜色，且不可切换。

## 属性

### MultiSelectListPreference 属性

| 属性                    | 类型                       | 说明                               | 默认值                                        | 必需 |
| ----------------------- | -------------------------- | ---------------------------------- | --------------------------------------------- | ---- |
| entries                 | List\<ListPreferenceEntry> | 可选条目列表                       | -                                             | 是   |
| selectedIndices         | Set\<Int>                  | 选中条目的索引集合                 | -                                             | 是   |
| onSelectedIndicesChange | (Set\<Int>) -> Unit        | 点击确定时返回新选择的回调         | -                                             | 是   |
| title                   | String                     | 偏好行标题                         | -                                             | 是   |
| confirmButtonText       | String                     | 对话框确定按钮文字                 | -                                             | 是   |
| cancelButtonText        | String                     | 对话框取消按钮文字                 | -                                             | 是   |
| modifier                | Modifier                   | 应用于偏好行的修饰符               | Modifier                                      | 否   |
| titleColor              | BasicComponentColors       | 标题颜色配置                       | BasicComponentDefaults.titleColor()           | 否   |
| summary                 | String?                    | 偏好行摘要                         | null                                          | 否   |
| summaryColor            | BasicComponentColors       | 摘要颜色配置                       | BasicComponentDefaults.summaryColor()         | 否   |
| dialogTitle             | String                     | 选择对话框标题                     | title                                         | 否   |
| colors                  | ListPreferenceColors       | 对话框行颜色配置                   | ListPreferenceDefaults.listPreferenceColors() | 否   |
| checkboxColors          | CheckboxColors             | 对话框行复选框颜色                 | CheckboxDefaults.checkboxColors()             | 否   |
| startAction             | @Composable (() -> Unit)?  | 自定义起始侧内容                   | null                                          | 否   |
| bottomAction            | @Composable (() -> Unit)?  | 自定义底部内容                     | null                                          | 否   |
| insideMargin            | PaddingValues              | 行内边距                           | BasicComponentDefaults.InsideMargin           | 否   |
| cardListPosition | CardListPosition | 该行在卡片组（[CardListGroup](./card.md#cardlistgroup)）中的位置，圆角外边缘会获得额外内边距 | LocalCardListPosition.current（CardListGroup 之外为 None） | 否 |
| enabled                 | Boolean                    | 行是否可点击                       | true                                          | 否   |
| showValue               | Boolean                    | 是否在行末显示已选条目             | true                                          | 否   |
| renderInRootScaffold    | Boolean                    | 是否在根 Scaffold 渲染对话框       | true                                          | 否   |
| onExpandedChange        | ((Boolean) -> Unit)?       | 对话框显示 / 关闭时的回调          | null                                          | 否   |

`ListPreferenceEntry`、`ListPreferenceDefaults` 与 `ListPreferenceColors` 与 `ListPreference` 共用，见 [ListPreference](./listpreference.md#属性)。

## 行为

- 点击偏好行会弹出以 `dialogTitle` 为标题、带取消与确定按钮的 `OverlayDialog`（COUI 17 底部对话框，大屏居中）。对话框显示期间偏好行不保持按压态（COUI 对话框不锁定锚点状态）；与 COUI 偏好行一致，不触发震动。
- 每次打开对话框，暂存选择都从 `selectedIndices` 重新开始；切换条目只更新暂存选择。
- 确定按钮通过 `onSelectedIndicesChange` 提交暂存选择并关闭对话框；取消按钮或点击外部关闭且不做修改。
- 行末文字按条目顺序列出已选条目，以 `", "` 分隔；超出 `entries` 范围的索引会被忽略。
