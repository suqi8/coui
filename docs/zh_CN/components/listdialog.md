# ListDialog

`ListDialog` 覆盖 ColorOS 对话框 (`COUIAlertDialogBuilder`) 的三种列表形态:

- **列表对话框** (`setItems`、`COUIAlertDialog.List.Bottom`、`SummaryAdapter`): 每一项是一个整宽胶囊按钮, 下方可带 12sp 摘要。
- **单选对话框** (`setSingleChoiceItems`、`ChoiceListAdapter`): 48dp 行, 含标题、可选摘要与图标, 末端为单选按钮。
- **多选对话框** (`setMultiChoiceItems`、`ChoiceListAdapter`): 同样的行, 末端为复选框, 可限制最多勾选数。

三种形态都沿用普通对话框布局: 标题、可选消息 (后接列表时最高 60dp)、可滚动列表, 以及可选的 `DialogButtonBar`。

每种形态都提供两种变体:

- `OverlayListDialog` / `OverlaySingleChoiceDialog` / `OverlayMultiChoiceDialog` — 渲染在 `Scaffold` 的 `COUIPopupHost` 中 (必须在 `Scaffold` 内使用)。
- `WindowListDialog` / `WindowSingleChoiceDialog` / `WindowMultiChoiceDialog` — 渲染在窗口层级, 无需 `Scaffold`。

::: danger 前提条件
Overlay 变体依赖 `Scaffold` 提供的 `COUIPopupHost`。没有 `Scaffold` 时请使用 Window 变体。
:::

<div style="position: relative; height: 500px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=listDialog" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.overlay.OverlayListDialog
import io.github.suqi8.coui.kmp.overlay.OverlaySingleChoiceDialog
import io.github.suqi8.coui.kmp.overlay.OverlayMultiChoiceDialog
// 或
import io.github.suqi8.coui.kmp.window.WindowListDialog
import io.github.suqi8.coui.kmp.window.WindowSingleChoiceDialog
import io.github.suqi8.coui.kmp.window.WindowMultiChoiceDialog

import io.github.suqi8.coui.kmp.layout.ListDialogItem
import io.github.suqi8.coui.kmp.layout.ListDialogDefaults
import io.github.suqi8.coui.kmp.layout.ListDialogColors
```

## 基本用法

### 列表对话框

```kotlin
var showDialog by remember { mutableStateOf(false) }

OverlayListDialog(
    show = showDialog,
    title = "分享到",
    items = listOf(
        ListDialogItem(text = "复制链接"),
        ListDialogItem(text = "保存图片", summary = "保存到相册"),
        ListDialogItem(text = "删除", textColor = COUITheme.colorScheme.error),
    ),
    onItemClick = { index -> showDialog = false },
    negative = DialogButtonBarAction(text = "取消", onClick = { showDialog = false }),
    onDismissRequest = { showDialog = false },
)
```

### 单选对话框

```kotlin
var selected by remember { mutableIntStateOf(0) }

OverlaySingleChoiceDialog(
    show = showDialog,
    title = "自动锁屏",
    items = listOf(ListDialogItem("30 秒"), ListDialogItem("1 分钟"), ListDialogItem("永不")),
    selectedIndex = selected,
    onSelectedIndexChange = { selected = it },
    negative = DialogButtonBarAction(text = "取消", onClick = { showDialog = false }),
    positive = DialogButtonBarAction(text = "确定", onClick = { showDialog = false }),
    onDismissRequest = { showDialog = false },
)
```

### 多选对话框

```kotlin
var checked by remember { mutableStateOf(listOf(true, false, false)) }

OverlayMultiChoiceDialog(
    show = showDialog,
    title = "同步项目",
    items = listOf(ListDialogItem("联系人"), ListDialogItem("照片"), ListDialogItem("便签", enabled = false)),
    checked = checked,
    onCheckedChange = { index, value -> checked = checked.toMutableList().also { it[index] = value } },
    maxCheckedCount = 2,
    onMaxCheckedReached = { /* 提示已达上限 */ },
    positive = DialogButtonBarAction(text = "确定", onClick = { showDialog = false }),
    onDismissRequest = { showDialog = false },
)
```

三种对话框点击后都不会自行隐藏, 需要把 `show` 设为 false 来关闭。

## 属性

### ListDialogItem

| 属性名    | 类型         | 说明                                                       | 默认值            |
| --------- | ------------ | ---------------------------------------------------------- | ----------------- |
| text      | String       | 项目文字                                                   | -                 |
| summary   | String?      | 可选的第二行文字                                           | null              |
| textColor | Color        | 列表对话框项目文字色 (`setItems` 的文字色数组); 选择对话框忽略 | Color.Unspecified |
| icon      | ImageVector? | 选择行起始图标; 列表对话框忽略                             | null              |
| enabled   | Boolean      | 是否可点击                                                 | true              |

### OverlayListDialog 属性

| 属性名               | 类型                   | 说明                                          | 默认值                               | 必需 |
| -------------------- | ---------------------- | --------------------------------------------- | ------------------------------------ | ---- |
| show                 | Boolean                | 是否显示对话框                                | -                                    | 是   |
| items                | List\<ListDialogItem\> | 项目列表                                      | -                                    | 是   |
| onItemClick          | (Int) -> Unit          | 点击项目时以下标回调                          | -                                    | 是   |
| modifier             | Modifier               | 应用于对话框的修饰符                          | Modifier                             | 否   |
| backgroundModifier   | Modifier               | 应用在面板圆角裁剪内、内容之下                | Modifier                             | 否   |
| title                | String?                | 标题                                          | null                                 | 否   |
| titleColor           | Color                  | 标题颜色                                      | DialogDefaults.titleColor()          | 否   |
| summary              | String?                | 消息, 最高 60dp                               | null                                 | 否   |
| summaryColor         | Color                  | 消息颜色                                      | DialogDefaults.summaryColor()        | 否   |
| negative             | DialogButtonBarAction? | 列表下方按钮栏的取消按钮                      | null                                 | 否   |
| positive             | DialogButtonBarAction? | 列表下方按钮栏的确认按钮                      | null                                 | 否   |
| neutral              | DialogButtonBarAction? | 列表下方按钮栏的中性按钮                      | null                                 | 否   |
| colors               | ListDialogColors       | 项目颜色                                      | ListDialogDefaults.listDialogColors() | 否  |
| backgroundColor      | Color                  | 面板背景色                                    | DialogDefaults.backgroundColor()     | 否   |
| enableWindowDim      | Boolean                | 是否启用背景遮罩                              | true                                 | 否   |
| onDismissRequest     | (() -> Unit)?          | 点击外部或按返回键时回调                      | null                                 | 否   |
| onDismissFinished    | (() -> Unit)?          | 隐藏动画完成后回调                            | null                                 | 否   |
| renderInRootScaffold | Boolean                | 是否渲染在根 Scaffold 中                      | true                                 | 否   |
| materialStroke       | Boolean                | 是否绘制模糊面板的 COUI 17 材质描边; 按钮同时切换为材质 | false                   | 否   |

### OverlaySingleChoiceDialog 属性

与 `OverlayListDialog` 相同, 以 `selectedIndex: Int` (选中行, -1 表示无) 和 `onSelectedIndexChange: (Int) -> Unit` 取代 `onItemClick`。

### OverlayMultiChoiceDialog 属性

与 `OverlayListDialog` 相同, 以下列属性取代 `onItemClick`:

| 属性名              | 类型                   | 说明                                              | 默认值 | 必需 |
| ------------------- | ---------------------- | ------------------------------------------------- | ------ | ---- |
| checked             | List\<Boolean\>        | 每行是否勾选                                      | -      | 是   |
| onCheckedChange     | (Int, Boolean) -> Unit | 切换某行时以下标与新状态回调                      | -      | 是   |
| maxCheckedCount     | Int                    | 最多勾选数, 0 表示不限                            | 0      | 否   |
| onMaxCheckedReached | (() -> Unit)?          | 超过上限时代替 `onCheckedChange` 回调             | null   | 否   |

Window 变体的属性相同, 但没有 `renderInRootScaffold`。

### ListDialogDefaults 对象

| 属性名                      | 类型 | 说明                                                   |
| --------------------------- | ---- | ------------------------------------------------------ |
| ListPanelMarginTop          | Dp   | 列表对话框列表上边距 (16.dp, `coui_list_dialog_list_panel_margin_top`) |
| SummaryMaxHeight            | Dp   | 列表上方消息最大高度 (60.dp)                           |
| ItemSummaryFontSize         | TextUnit | 列表项摘要字号 (12.sp)                             |
| ItemSummaryLineHeight       | TextUnit | 列表项摘要行高 (1.35.em)                           |
| ChoiceItemMinHeight         | Dp   | 选择行最小高度 (48.dp)                                 |
| ChoiceItemPaddingHorizontal | Dp   | 选择行水平内边距 (24.dp)                               |
| ChoiceItemPaddingVertical   | Dp   | 行内文字与控件的纵向边距 (10.dp)                       |
| ChoiceItemTextMarginEnd     | Dp   | 文字与控件间距 (16.dp)                                 |
| ChoiceItemIconMarginEnd     | Dp   | 图标与文字间距 (16.dp)                                 |
| ChoiceItemIconPaddingVertical | Dp | 图标纵向边距 (20.dp)                                   |
| ChoiceItemSummarySpacing    | Dp   | 行标题与摘要间距 (2.dp)                                |
| ChoiceListPaddingBottom     | Dp   | 选择列表下内边距 (16.dp)                               |

| 函数名             | 返回类型         | 说明         |
| ------------------ | ---------------- | ------------ |
| listDialogColors() | ListDialogColors | 默认项目颜色 |

### ListDialogColors 类

| 属性名               | 类型  | 说明                                          |
| -------------------- | ----- | --------------------------------------------- |
| textColor            | Color | 选择行文字 (`?couiColorPrimaryNeutral`)       |
| disabledTextColor    | Color | 禁用选择行文字 (`?couiColorDisabledNeutral`)  |
| summaryColor         | Color | 项目摘要 (`?couiColorSecondNeutral`)          |
| disabledSummaryColor | Color | 禁用项目摘要                                  |
| dividerColor         | Color | 选择行之间的分隔线 (`?couiColorDivider`)      |
