# InputDialog

`InputDialog` 是 ColorOS 的单输入框对话框, 有两种形态:

- **SingleEdit** (`COUIAlertDialog.SingleEdit`、`coui_single_edit_bottom_alert_dialog_layout.xml`): 线条样式输入框, 带快速删除, 最多 5 行, 左右 24dp、上下 8dp; 可选密码可见切换。
- **SingleInput** (`COUIAlertDialog.SingleInput`、`coui_single_inputview_bottom_alert_dialog_layout.xml`): 带字数统计的 [InputView](./inputview), 左右 24dp、下 18dp。传入 `maxCount` 时采用此形态。

两者都是底部 assignment 对话框 (大屏居中), 打开时输入框自动获取焦点。

提供两种变体:

- `OverlayInputDialog` — 渲染在 `Scaffold` 的 `COUIPopupHost` 中 (必须在 `Scaffold` 内使用)。
- `WindowInputDialog` — 渲染在窗口层级, 无需 `Scaffold`。

<div style="position: relative; height: 420px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=inputDialog" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.overlay.OverlayInputDialog
// 或
import io.github.suqi8.coui.kmp.window.WindowInputDialog

import io.github.suqi8.coui.kmp.layout.InputDialogDefaults
```

## 基本用法

```kotlin
var showDialog by remember { mutableStateOf(false) }
var name by remember { mutableStateOf("") }

OverlayInputDialog(
    show = showDialog,
    value = name,
    onValueChange = { name = it },
    title = "重命名",
    label = "输入名称",
    negative = DialogButtonBarAction(text = "取消", onClick = { showDialog = false }),
    positive = DialogButtonBarAction(text = "保存", enabled = name.isNotEmpty(), onClick = { showDialog = false }),
    onDismissRequest = { showDialog = false },
)
```

带字数统计 (SingleInput):

```kotlin
OverlayInputDialog(
    show = showDialog,
    value = note,
    onValueChange = { note = it },
    title = "添加备注",
    maxCount = 30,
    positive = DialogButtonBarAction(text = "保存", onClick = { showDialog = false }),
)
```

## 属性

### OverlayInputDialog 属性

| 属性名               | 类型                   | 说明                                      | 默认值                               | 必需 |
| -------------------- | ---------------------- | ----------------------------------------- | ------------------------------------ | ---- |
| show                 | Boolean                | 是否显示对话框                            | -                                    | 是   |
| value                | String                 | 输入文字                                  | -                                    | 是   |
| onValueChange        | (String) -> Unit       | 输入变化时回调                            | -                                    | 是   |
| modifier             | Modifier               | 应用于对话框的修饰符                      | Modifier                             | 否   |
| backgroundModifier   | Modifier               | 应用在面板圆角裁剪内、内容之下            | Modifier                             | 否   |
| title                | String?                | 标题                                      | null                                 | 否   |
| titleColor           | Color                  | 标题颜色                                  | DialogDefaults.titleColor()          | 否   |
| summary              | String?                | 消息                                      | null                                 | 否   |
| summaryColor         | Color                  | 消息颜色                                  | DialogDefaults.summaryColor()        | 否   |
| label                | String                 | 输入为空时的提示                          | ""                                   | 否   |
| maxCount             | Int?                   | 最多字数; 传入后为 SingleInput 形态       | null                                 | 否   |
| showPasswordToggle   | Boolean                | 是否以可见性切换遮盖文字                  | false                                | 否   |
| keyboardOptions      | KeyboardOptions        | 键盘选项                                  | KeyboardOptions.Default              | 否   |
| textFieldColors      | TextFieldColors        | 输入框颜色                                | TextFieldDefaults.textFieldColors()  | 否   |
| negative             | DialogButtonBarAction? | 取消按钮                                  | null                                 | 否   |
| positive             | DialogButtonBarAction? | 确认按钮                                  | null                                 | 否   |
| neutral              | DialogButtonBarAction? | 中性按钮                                  | null                                 | 否   |
| backgroundColor      | Color                  | 面板背景色                                | DialogDefaults.backgroundColor()     | 否   |
| enableWindowDim      | Boolean                | 是否启用背景遮罩                          | true                                 | 否   |
| onDismissRequest     | (() -> Unit)?          | 点击外部或按返回键时回调                  | null                                 | 否   |
| onDismissFinished    | (() -> Unit)?          | 隐藏动画完成后回调                        | null                                 | 否   |
| renderInRootScaffold | Boolean                | 是否渲染在根 Scaffold 中                  | true                                 | 否   |
| materialStroke       | Boolean                | 是否绘制模糊面板的 COUI 17 材质描边       | false                                | 否   |

`WindowInputDialog` 的属性相同, 但没有 `renderInRootScaffold`。

### InputDialogDefaults 对象

| 属性名                | 类型 | 说明                                        |
| --------------------- | ---- | ------------------------------------------- |
| FieldMarginHorizontal | Dp   | 输入框左右边距 (24.dp, `common_margin`)     |
| EditMarginVertical    | Dp   | SingleEdit 输入框上下边距 (8.dp)            |
| InputMarginBottom     | Dp   | SingleInput 输入框下边距 (18.dp)            |
| MaxLines              | Int  | 输入最多行数 (5)                            |
