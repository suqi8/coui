---
title: OverlaySpinnerPreference
requiresScaffoldHost: true
prerequisites:
  - 必须在 `Scaffold` 中使用以提供 `COUIPopupHost`
  - 未在 `Scaffold` 中使用将导致弹出内容无法渲染
  - 支持多个嵌套或并列的 `Scaffold`，无需额外配置
hostComponent: Scaffold
popupHost: COUIPopupHost
---

# OverlaySpinnerPreference

`OverlaySpinnerPreference` 是 COUI 中的下拉选择器组件，提供了标题、摘要和带有图标、文本的选项列表，支持点击交互和多种显示模式，常用于具有视觉辅助的选项设置中。该组件与 `OverlayDropdownPreference` 组件类似，但提供更丰富的功能和交互体验。

<div style="position: relative; height: 420px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=overlaySpinnerPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

::: danger 使用前提
此组件依赖 `Scaffold` 提供的 `COUIPopupHost` 以显示弹出内容。必须在 `Scaffold` 中使用，否则弹出内容无法正常渲染。
:::

## 引入

```kotlin
import io.github.suqi8.coui.kmp.preference.OverlaySpinnerPreference
import io.github.suqi8.coui.kmp.popup.OverlayDropdownDialog
import io.github.suqi8.coui.kmp.basic.DropdownEntry
import io.github.suqi8.coui.kmp.basic.DropdownItem
```

## 基本用法

OverlaySpinnerPreference 组件提供了基础的下拉选择器功能：

```kotlin
var selectedIndex by remember { mutableStateOf(0) }
val options = listOf(
    DropdownItem(text = "选项 1"),
    DropdownItem(text = "选项 2"),
    DropdownItem(text = "选项 3"),
)

Scaffold {
    OverlaySpinnerPreference(
        title = "下拉选择器",
        items = options,
        selectedIndex = selectedIndex,
        onSelectedIndexChange = { selectedIndex = it }
    )
}
```

## 带图标和摘要的选项

```kotlin
// Create a rounded rectangle Painter
class RoundedRectanglePainter(
    private val cornerRadius: Dp = 6.dp
) : Painter() {
    override val intrinsicSize = Size.Unspecified

    override fun DrawScope.onDraw() {
        drawRoundRect(
            color = Color.White,
            size = Size(size.width, size.height),
            cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
        )
    }
}

var selectedIndex by remember { mutableStateOf(0) }
val options = listOf(
    DropdownItem(
        icon = { Icon(RoundedRectanglePainter(), "Icon", Modifier.padding(end = 12.dp), Color(0xFFFF5B29)) },
        text = "红色主题",
        summary = "活力四射的红色"
    ),
    DropdownItem(
        icon = { Icon(RoundedRectanglePainter(), "Icon", Modifier.padding(end = 12.dp), Color(0xFF3482FF)) },
        text = "蓝色主题",
        summary = "沉稳冷静的蓝色"
    ),
    DropdownItem(
        icon = { Icon(RoundedRectanglePainter(), "Icon", Modifier.padding(end = 12.dp), Color(0xFF36D167)) },
        text = "绿色主题",
        summary = "清新自然的绿色"
    ),
    DropdownItem(
        icon = { Icon(RoundedRectanglePainter(), "Icon", Modifier.padding(end = 12.dp), Color(0xFFFFB21D)) },
        text = "黄色主题",
        summary = "明亮活泼的黄色"
    )
)

Scaffold {
    OverlaySpinnerPreference(
        title = "功能选择",
        summary = "选择您要执行的操作",
        items = options,
        selectedIndex = selectedIndex,
        onSelectedIndexChange = { selectedIndex = it }
    )
}
```

## 组件状态

### 禁用状态

```kotlin
OverlaySpinnerPreference(
    title = "禁用选择器",
    summary = "此选择器当前不可用",
    items = listOf(DropdownItem(text = "选项 1")),
    selectedIndex = 0,
    onSelectedIndexChange = {},
    enabled = false
)
```

## 对话框模式

OverlaySpinnerPreference 还支持对话框模式，适用于显示较多选项或需要更醒目的选择界面时。通过提供 `dialogButtonString` 参数即可激活此模式。

```kotlin
var selectedIndex by remember { mutableStateOf(0) }
val options = listOf(
    DropdownItem(text = "选项 A"),
    DropdownItem(text = "选项 B"),
    DropdownItem(text = "选项 C")
)

Scaffold {
    OverlaySpinnerPreference(
        title = "对话框模式",
        dialogButtonString = "取消",
        items = options,
        selectedIndex = selectedIndex,
        onSelectedIndexChange = { selectedIndex = it }
    )
}
```

对话框模式下该行是 COUI `COUIListPreference` 行：选中值之后是 `coui_btn_next` 跳转箭头，而不是上下箭头。点击会打开 COUI 17 单选对话框，与 `ListPreference` 打开的对话框相同：`coui_select_dialog_singlechoice` 行（可选图标、标题与可选摘要、末端单选按钮、行间细分割线），`dialogButtonString` 是 COUI 17 胶囊按钮栏里唯一的按钮。选择选项会调用其 `onClick`，不触发振动；`collapseOnSelection` 时对话框随即关闭，退出过程中单选按钮停留在打开时的位置。分组 `title` 显示为标题行；`hint`、`alert`、`stateIcon` 与 `children` 不生效（COUI 列表对话框没有这些）。

### 独立使用下拉对话框

对话框模式背后的对话框以 `OverlayDropdownDialog`（`io.github.suqi8.coui.kmp.popup`）公开。需要由自己的触发控件（而不是偏好设置行）打开同样的选项对话框时直接使用它。它是一个由 Scaffold 承载的 `OverlayDialog`，列表同样是 COUI 单选列表，COUI 17 按钮栏（`DialogButtonBar`）里只有一个用于关闭对话框的按钮。点击选项会调用 `DropdownItem.onClick`，并在 `collapseOnSelection` 为 true 时关闭对话框；与 COUI 列表对话框一致，不触发振动。

```kotlin
var show by remember { mutableStateOf(false) }
var selectedIndex by remember { mutableStateOf(0) }
val entry = DropdownEntry(
    items = listOf("选项 1", "选项 2", "选项 3").mapIndexed { index, text ->
        DropdownItem(text = text, selected = selectedIndex == index, onClick = { selectedIndex = index })
    }
)

Scaffold {
    TextButton(text = "选择", onClick = { show = true })
    OverlayDropdownDialog(
        entry = entry,
        title = "选择一个选项",
        dialogButtonString = "取消",
        show = show,
        onDismiss = { show = false },
        onDismissFinished = {},
        dropdownColors = DropdownDefaults.dialogDropdownColors()
    )
}
```

## 分组选项

当选项需要选中状态、禁用状态、点击回调、图标或摘要时，可以使用 `DropdownEntry`。使用 `entries` 可以显示多个分组，分组之间会用分割线隔开。

```kotlin
var firstSelectedIndex by remember { mutableStateOf(0) }
var secondSelectedIndex by remember { mutableStateOf(0) }
val entries = listOf(
    DropdownEntry(
        items = listOf("小", "中").mapIndexed { index, text ->
            DropdownItem(text = text, selected = firstSelectedIndex == index, onClick = { firstSelectedIndex = index })
        }
    ),
    DropdownEntry(
        items = listOf("红色", "绿色", "蓝色").mapIndexed { index, text ->
            DropdownItem(text = text, selected = secondSelectedIndex == index, onClick = { secondSelectedIndex = index })
        }
    )
)

Scaffold {
    OverlaySpinnerPreference(
        title = "分组选项",
        entries = entries,
        collapseOnSelection = false
    )
}
```

对于 `entries` 重载，`collapseOnSelection` 控制点击选项后是否关闭弹出框。默认值为 `entries.size <= 1`，单个分组会在选中后关闭，多个分组会保持打开以便连续选择。提供 `dialogButtonString` 后，对话框模式也支持相同的 `entry` 和 `entries` 重载。

## 多选

因为选中状态放在 `DropdownItem` 上，可以用集合保存多个选中值，并在每个条目的 `onClick` 中切换选中状态。

```kotlin
var selectedItems by remember { mutableStateOf(setOf("A1", "B2")) }
val entries = listOf(
    DropdownEntry(
        items = listOf("A1", "A2").map { text ->
            DropdownItem(
                text = text,
                selected = text in selectedItems,
                onClick = {
                    selectedItems = if (text in selectedItems) selectedItems - text else selectedItems + text
                }
            )
        }
    ),
    DropdownEntry(
        items = listOf("B1", "B2", "B3").map { text ->
            DropdownItem(
                text = text,
                selected = text in selectedItems,
                onClick = {
                    selectedItems = if (text in selectedItems) selectedItems - text else selectedItems + text
                }
            )
        }
    )
)

Scaffold {
    OverlaySpinnerPreference(
        title = "多选选择器",
        entries = entries,
        collapseOnSelection = false
    )
}
```

## 监听展开状态

```kotlin
var selectedIndex by remember { mutableStateOf(0) }
var expanded by remember { mutableStateOf(false) }
val options = listOf(
    DropdownItem(text = "选项 1"),
    DropdownItem(text = "选项 2"),
    DropdownItem(text = "选项 3"),
)

Scaffold {
    OverlaySpinnerPreference(
        title = "下拉选择器",
        summary = if (expanded) "展开" else "收起",
        items = options,
        selectedIndex = selectedIndex,
        onExpandedChange = { expanded = it },
        onSelectedIndexChange = { selectedIndex = it }
    )
}
```

## 交互

该行遵循 COUI 17 `COUIMenuPreference` / `COUIClickSelectMenu`：

- **在手指处弹出**：记录按下点（`PreciseClickHelper`），菜单在该点处弹出，而不是贴着行的边缘。无障碍点击没有触点，退回以整行定位。
- **软键盘**：键盘显示时，点击会先收起键盘，100ms 后（窗口高度不小于 840dp 时为 240ms）再弹出菜单，使菜单按最终窗口定位。
- **锚点行**：菜单显示期间该行的悬停层保持锁定（`COUIPopupListWindow.setAnchorHoveredState`），菜单开始退出时即释放。
- **箭头**：上下箭头为 `couiColorLabelTertiary`，行禁用时为 `couiColorLabelQuaternary`。
- **选中值**：按 COUI 赋值文字（`TextAppearance.COUI.List.Assignment.End`）显示：13sp、body_s 行距、末端对齐、最多两行，与箭头间隔 4dp。
- **振动与对话框模式**：与 COUI 一致，弹出菜单不触发振动。对话框模式下该行末尾是 `coui_btn_next` 跳转箭头（颜色同样为 tertiary / 禁用时 quaternary），只播放正常的按下与抬起（COUI 列表对话框不锁定锚点状态），选择选项同样不振动。

## 属性

### OverlaySpinnerPreference 属性（下拉列表模式）

| 属性名                | 类型                      | 说明                 | 默认值                                | 是否必须 |
| --------------------- | ------------------------- | -------------------- | ------------------------------------- | -------- |
| items                 | List\<DropdownItem>       | 选项列表             | -                                     | 是       |
| selectedIndex         | Int                       | 当前选中项的索引     | -                                     | 是       |
| title                 | String                    | 选择器的标题         | -                                     | 是       |
| modifier              | Modifier                  | 应用于组件的修饰符   | Modifier                              | 否       |
| titleColor            | BasicComponentColors      | 标题文本的颜色配置   | BasicComponentDefaults.titleColor()   | 否       |
| summary               | String?                   | 选择器的摘要说明     | null                                  | 否       |
| summaryColor          | BasicComponentColors      | 摘要文本的颜色配置   | BasicComponentDefaults.summaryColor() | 否       |
| spinnerColors         | DropdownColors            | 选择器的颜色配置     | DropdownDefaults.dropdownColors()     | 否       |
| startAction           | @Composable (() -> Unit)? | 左侧显示的自定义内容 | null                                  | 否       |
| bottomAction          | @Composable (() -> Unit)? | 底部显示的自定义内容 | null                                  | 否       |
| insideMargin          | PaddingValues             | 组件内部内容的边距   | BasicComponentDefaults.InsideMargin   | 否       |
| cardListPosition | CardListPosition | 该行在卡片组（[CardListGroup](./card.md#cardlistgroup)）中的位置，圆角外边缘会获得额外内边距 | LocalCardListPosition.current（CardListGroup 之外为 None） | 否 |
| maxHeight             | Dp?                       | 下拉菜单的最大高度   | null                                  | 否       |
| enabled               | Boolean                   | 组件是否可交互       | true                                  | 否       |
| showValue             | Boolean                   | 是否显示当前选中的值 | true                                  | 否       |
| renderInRootScaffold  | Boolean                   | 是否在根（最外层）Scaffold 中渲染弹窗。为 true 时，弹窗覆盖全屏。为 false 时，在当前 Scaffold 的范围内渲染并进行位置补偿 | true | 否 |
| onExpandedChange      | ((Boolean) -> Unit)?      | 展开状态变化时的回调 | null                                  | 否       |
| onSelectedIndexChange | ((Int) -> Unit)?          | 选中项变化时的回调   | null                                  | 否       |

### Entry 重载属性

| 属性名                | 类型          | 说明                   | 默认值 | 是否必须 |
| --------------------- | ------------- | ---------------------- | ------ | -------- |
| entry                 | DropdownEntry | 单个下拉选项分组       | -      | 是       |
| collapseOnSelection   | Boolean       | 选中后是否关闭弹出框   | true   | 否       |

### Entries 分组重载属性

| 属性名                | 类型                 | 说明                           | 默认值            | 是否必须 |
| --------------------- | -------------------- | ------------------------------ | ----------------- | -------- |
| entries               | List\<DropdownEntry> | 由分割线隔开的下拉选项分组     | -                 | 是       |
| collapseOnSelection   | Boolean              | 每次选中后是否关闭弹出框       | entries.size <= 1 | 否       |
| renderInRootScaffold  | Boolean              | 是否在根 Scaffold 中渲染弹出框 | true              | 否       |

### OverlaySpinnerPreference 属性（对话框模式）

| 属性名                | 类型                      | 说明                     | 默认值                                  | 是否必须 |
| --------------------- | ------------------------- | ------------------------ | --------------------------------------- | -------- |
| items                 | List\<DropdownItem>       | 选项列表                 | -                                       | 是       |
| selectedIndex         | Int                       | 当前选中项的索引         | -                                       | 是       |
| title                 | String                    | 选择器的标题             | -                                       | 是       |
| dialogButtonString    | String                    | 对话框底部按钮的文本     | -                                       | 是       |
| modifier              | Modifier                  | 应用于组件的修饰符       | Modifier                                | 否       |
| popupModifier         | Modifier                  | 应用于弹出对话框的修饰符 | Modifier                                | 否       |
| titleColor            | BasicComponentColors      | 标题文本的颜色配置       | BasicComponentDefaults.titleColor()     | 否       |
| summary               | String?                   | 选择器的摘要说明         | null                                    | 否       |
| summaryColor          | BasicComponentColors      | 摘要文本的颜色配置       | BasicComponentDefaults.summaryColor()   | 否       |
| spinnerColors         | DropdownColors            | 对话框行的颜色：文字、摘要、禁用行、选中的单选按钮、分组标题         | DropdownDefaults.dialogDropdownColors() | 否       |
| startAction           | @Composable (() -> Unit)? | 左侧显示的自定义内容     | null                                    | 否       |
| bottomAction          | @Composable (() -> Unit)? | 底部显示的自定义内容     | null                                    | 否       |
| insideMargin          | PaddingValues             | 组件内部内容的边距       | BasicComponentDefaults.InsideMargin     | 否       |
| cardListPosition | CardListPosition | 该行在卡片组（[CardListGroup](./card.md#cardlistgroup)）中的位置，圆角外边缘会获得额外内边距 | LocalCardListPosition.current（CardListGroup 之外为 None） | 否 |
| enabled               | Boolean                   | 组件是否可交互           | true                                    | 否       |
| showValue             | Boolean                   | 是否显示当前选中的值     | true                                    | 否       |
| renderInRootScaffold  | Boolean                   | 是否在根（最外层）Scaffold 中渲染对话框。为 true 时，对话框覆盖全屏。为 false 时，在当前 Scaffold 的范围内渲染 | true | 否 |
| onExpandedChange      | ((Boolean) -> Unit)?      | 展开状态变化时的回调     | null                                    | 否       |
| onSelectedIndexChange | ((Int) -> Unit)?          | 选中项变化时的回调       | null                                    | 否       |

### 对话框 Entry 重载属性

| 属性名              | 类型          | 说明                 | 默认值 | 是否必须 |
| ------------------- | ------------- | -------------------- | ------ | -------- |
| entry               | DropdownEntry | 单个下拉选项分组     | -      | 是       |
| dialogButtonString  | String        | 对话框底部按钮的文本 | -      | 是       |
| collapseOnSelection | Boolean       | 选中后是否关闭对话框 | true   | 否       |

### 对话框 Entries 分组重载属性

| 属性名                | 类型                 | 说明                           | 默认值            | 是否必须 |
| --------------------- | -------------------- | ------------------------------ | ----------------- | -------- |
| entries               | List\<DropdownEntry> | 由分割线隔开的下拉选项分组     | -                 | 是       |
| dialogButtonString    | String               | 对话框底部按钮的文本           | -                 | 是       |
| collapseOnSelection   | Boolean              | 每次选中后是否关闭对话框       | entries.size <= 1 | 否       |
| renderInRootScaffold  | Boolean              | 是否在根 Scaffold 中渲染对话框 | true              | 否       |

### OverlayDropdownDialog 属性

`OverlayDropdownDialog` 提供 `entry: DropdownEntry` 重载（此时 `collapseOnSelection` 默认为 `true`）和 `entries: List<DropdownEntry>` 重载：

| 属性名               | 类型                 | 说明                                         | 默认值            | 是否必须 |
| -------------------- | -------------------- | -------------------------------------------- | ----------------- | -------- |
| entries              | List\<DropdownEntry> | 以单选行显示的选项分组；分组标题显示为标题行 | -                 | 是       |
| title                | String               | 对话框标题                                   | -                 | 是       |
| dialogButtonString   | String               | 底部按钮的文本，点击即关闭对话框             | -                 | 是       |
| show                 | Boolean              | 是否显示对话框                               | -                 | 是       |
| onDismiss            | () -> Unit           | 点击按钮、点击外部、返回或选中后收起时调用   | -                 | 是       |
| onDismissFinished    | () -> Unit           | 退出动画结束后调用                           | -                 | 是       |
| dropdownColors       | DropdownColors       | 单选行的颜色：文字、摘要、禁用行、选中的单选按钮、分组标题                             | -                 | 是       |
| popupModifier        | Modifier             | 应用于对话框的修饰符                         | Modifier          | 否       |
| renderInRootScaffold | Boolean              | 是否在根 Scaffold 中渲染对话框               | true              | 否       |
| collapseOnSelection  | Boolean              | 点击选项后是否关闭对话框                     | entries.size <= 1 | 否       |

### DropdownEntry 属性

| 属性名  | 类型                | 说明                       | 默认值 | 是否必须 |
| ------- | ------------------- | -------------------------- | ------ | -------- |
| items   | List\<DropdownItem> | 此分组中显示的条目         | -      | 是       |
| enabled | Boolean             | 此分组是否启用。为 false 时禁用整组条目；为 true 时仍会遵循每个条目的 enabled 状态 | true   | 否       |
| title | String?             | 可选的不可点击分组标题，渲染在各项之上（12sp 中等字重、次级标签色、最多 2 行） | null   | 否       |
| horizontal    | Boolean             | 是否作为 COUI 17 横向条目行（最多 5 个纯图标或 3 个图标+文字），仅弹窗模式，后接分组分割线 | false | 否 |


### DropdownItem 属性

| 属性名   | 类型                              | 说明                         | 默认值 | 是否必须 |
| -------- | --------------------------------- | ---------------------------- | ------ | -------- |
| text     | String                            | 选项显示的文本               | -      | 是       |
| enabled  | Boolean                           | 选项是否可点击，禁用时置灰   | true   | 否       |
| selected | Boolean                           | 选项是否处于选中状态         | false  | 否       |
| onClick  | (() -> Unit)?                     | 点击选项时触发的回调         | null   | 否       |
| icon     | @Composable ((Modifier) -> Unit)? | 显示在选项文本前的图标       | null   | 否       |
| summary  | String?                           | 显示在选项文本下方的摘要文本 | null   | 否       |
| children | List\<DropdownItem>?              | 可选的子菜单项；仅级联变体   | null   | 否       |
| hint | @Composable (() -> Unit)?         | 可选的尾部提示槽（徽标、红点、短计数），显示在选中指示图标之前，最大宽度 40dp。行被禁用时整体隐藏 | null   | 否       |
| alert | Boolean                           | 是否为警示（危险）项；其标题使用错误色 | false  | 否       |

### DropdownColors 属性

| 属性名                 | 类型  | 说明             |
| ---------------------- | ----- | ---------------- |
| contentColor           | Color | 选项标题颜色     |
| summaryColor           | Color | 选项摘要颜色     |
| containerColor         | Color | 选项背景颜色     |
| selectedContentColor   | Color | 选中项标题颜色   |
| selectedSummaryColor   | Color | 选中项摘要颜色   |
| selectedContainerColor | Color | 选中项背景颜色   |
| selectedIndicatorColor | Color | 选中指示图标颜色 |
| disabledContentColor | Color | 禁用项标题颜色 |
| alertContentColor | Color | 警示项标题颜色 |
| headerColor | Color | 分组标题行的标题颜色 |
| submenuIndicatorColor  | Color | 子菜单收起时指示器的颜色                |
