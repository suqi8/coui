# ProgressDialog

`ProgressDialog` 是 ColorOS 的横向进度条对话框 (`COUIAlertDialog.Progress`、`coui_progress_dialog_horizontal.xml`): 普通底部对话框, 自定义视图为居中的 200dp x 4dp 进度条 (轨道色 `?couiColorDivider`), 上下各 16dp。带取消按钮时为 `COUIAlertDialog.Progress.Cancelable`。

提供两种形态:

- `OverlayProgressDialog` — 渲染在 `Scaffold` 的 `COUIPopupHost` 中 (必须在 `Scaffold` 内使用)。
- `WindowProgressDialog` — 渲染在窗口层级, 无需 `Scaffold`。

不确定时长的等待请使用 [LoadingDialog](./loadingdialog)。

<div style="position: relative; height: 400px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=progressDialog" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.overlay.OverlayProgressDialog
// 或
import io.github.suqi8.coui.kmp.window.WindowProgressDialog

import io.github.suqi8.coui.kmp.layout.ProgressDialogDefaults
```

## 基本用法

```kotlin
var showDialog by remember { mutableStateOf(false) }
var progress by remember { mutableFloatStateOf(0f) }

OverlayProgressDialog(
    show = showDialog,
    progress = progress,
    title = "正在下载",
    cancelAction = DialogButtonBarAction(text = "取消", onClick = { showDialog = false }),
)
```

## 属性

### OverlayProgressDialog 属性

| 属性名               | 类型                    | 说明                                   | 默认值                                | 必需 |
| -------------------- | ----------------------- | -------------------------------------- | ------------------------------------- | ---- |
| show                 | Boolean                 | 是否显示对话框                         | -                                     | 是   |
| progress             | Float                   | 进度, 0 到 1                           | -                                     | 是   |
| modifier             | Modifier                | 应用于对话框的修饰符                   | Modifier                              | 否   |
| backgroundModifier   | Modifier                | 应用在面板圆角裁剪内、内容之下         | Modifier                              | 否   |
| title                | String?                 | 标题                                   | null                                  | 否   |
| titleColor           | Color                   | 标题颜色                               | DialogDefaults.titleColor()           | 否   |
| summary              | String?                 | 消息                                   | null                                  | 否   |
| summaryColor         | Color                   | 消息颜色                               | DialogDefaults.summaryColor()         | 否   |
| cancelAction         | DialogButtonBarAction?  | 取消按钮; 传入后为可取消形态           | null                                  | 否   |
| progressColors       | ProgressIndicatorColors | 进度条颜色                             | ProgressDialogDefaults.progressColors() | 否 |
| backgroundColor      | Color                   | 面板背景色                             | DialogDefaults.backgroundColor()      | 否   |
| enableWindowDim      | Boolean                 | 是否启用背景遮罩                       | true                                  | 否   |
| onDismissRequest     | (() -> Unit)?           | 点击外部或按返回键时回调               | null                                  | 否   |
| onDismissFinished    | (() -> Unit)?           | 隐藏动画完成后回调                     | null                                  | 否   |
| renderInRootScaffold | Boolean                 | 是否渲染在根 Scaffold 中               | true                                  | 否   |
| materialStroke       | Boolean                 | 是否绘制模糊面板的 COUI 17 材质描边    | false                                 | 否   |

`WindowProgressDialog` 的属性相同, 但没有 `renderInRootScaffold`。

### ProgressDialogDefaults 对象

| 属性名         | 类型 | 说明                                                        |
| -------------- | ---- | ----------------------------------------------------------- |
| ProgressWidth  | Dp   | 进度条宽度 (200.dp, `coui_loading_dialog_progress_width`)   |
| ProgressHeight | Dp   | 进度条高度 (4.dp, `coui_loading_dialog_progress_height`)    |
| PaddingTop     | Dp   | 进度条上方间距 (16.dp, `coui_progress_dialog_padding_top`)  |
| PaddingBottom  | Dp   | 进度条下方间距 (16.dp, `coui_progress_cancelable_dialog_padding_bottom`) |

| 函数名           | 返回类型                | 说明                               |
| ---------------- | ----------------------- | ---------------------------------- |
| progressColors() | ProgressIndicatorColors | 默认进度颜色 (分隔线色轨道)        |
