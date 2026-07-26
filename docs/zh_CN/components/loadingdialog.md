# LoadingDialog

`LoadingDialog` 是一个始终居中的小卡片加载对话框, 包含旋转加载指示器与可选文字, 对应 ColorOS 的旋转进度对话框 (COUIRotatingDialogBuilder / `coui_progress_dialog_rotating.xml`): 152dp 宽、9dp 圆角的卡片, 26dp 加载指示器与 14sp 文字。

提供两种形态:

- `OverlayLoadingDialog` — 渲染在 `Scaffold` 的 `COUIPopupHost` 中 (必须在 `Scaffold` 内使用)。
- `WindowLoadingDialog` — 渲染在窗口层级, 无需 `Scaffold`。

::: danger 前提条件
`OverlayLoadingDialog` 依赖 `Scaffold` 提供的 `COUIPopupHost` 渲染弹窗内容。没有 `Scaffold` 时请使用 `WindowLoadingDialog`。
:::

## 引入

```kotlin
import io.github.suqi8.coui.kmp.overlay.OverlayLoadingDialog
// or
import io.github.suqi8.coui.kmp.window.WindowLoadingDialog

import io.github.suqi8.coui.kmp.layout.LoadingDialogDefaults
```

## 基本用法

```kotlin
var showLoading by remember { mutableStateOf(false) }

Scaffold {
    TextButton(
        text = "开始加载",
        onClick = { showLoading = true }
    )

    OverlayLoadingDialog(
        show = showLoading,
        text = "加载中..."
    )

    // Hide the dialog when the task completes
    LaunchedEffect(showLoading) {
        if (showLoading) {
            doWork()
            showLoading = false
        }
    }
}
```

不传文字时, 加载指示器直接居中于卡片:

```kotlin
OverlayLoadingDialog(show = showLoading)
```

`WindowLoadingDialog` 用法相同, 但渲染在平台窗口层级, 无需 `Scaffold`:

```kotlin
var showLoading by remember { mutableStateOf(false) }

WindowLoadingDialog(
    show = showLoading,
    text = "加载中..."
)
```

## 用户关闭

默认情况下用户无法关闭对话框, 对应不可取消的 COUI 进度对话框。传入 `onDismissRequest` 后, 点击外部或按返回键可关闭:

```kotlin
OverlayLoadingDialog(
    show = showLoading,
    text = "加载中...",
    onDismissRequest = { showLoading = false }
)
```

## 属性

### OverlayLoadingDialog 属性

| 属性名               | 类型          | 说明                                                             | 默认值                                | 必需 |
| -------------------- | ------------- | ---------------------------------------------------------------- | ------------------------------------- | ---- |
| show                 | Boolean       | 是否显示对话框                                                   | -                                     | 是   |
| modifier             | Modifier      | 应用于对话框的修饰符                                             | Modifier                              | 否   |
| text                 | String?       | 加载指示器下方的文字                                             | null                                  | 否   |
| textColor            | Color         | 文字颜色                                                         | LoadingDialogDefaults.textColor()     | 否   |
| spinnerColor         | Color         | 加载指示器颜色                                                   | LoadingDialogDefaults.spinnerColor()  | 否   |
| backgroundColor      | Color         | 卡片背景色                                                       | DialogDefaults.backgroundColor()      | 否   |
| enableWindowDim      | Boolean       | 是否启用背景遮罩                                                 | true                                  | 否   |
| onDismissRequest     | (() -> Unit)? | 用户点击外部或按返回键时回调; 为 null 时用户无法关闭             | null                                  | 否   |
| onDismissFinished    | (() -> Unit)? | 隐藏动画完成后回调                                               | null                                  | 否   |
| renderInRootScaffold | Boolean       | 是否渲染在根 (最外层) Scaffold 中。为 true 时对话框覆盖全屏, 为 false 时在当前 Scaffold 的范围内渲染 | true | 否   |

### WindowLoadingDialog 属性

与 `OverlayLoadingDialog` 相同, 但没有 `renderInRootScaffold` (对话框始终渲染在窗口层级):

| 属性名               | 类型          | 说明                                                             | 默认值                                | 必需 |
| -------------------- | ------------- | ---------------------------------------------------------------- | ------------------------------------- | ---- |
| show                 | Boolean       | 是否显示对话框                                                   | -                                     | 是   |
| modifier             | Modifier      | 应用于对话框的修饰符                                             | Modifier                              | 否   |
| text                 | String?       | 加载指示器下方的文字                                             | null                                  | 否   |
| textColor            | Color         | 文字颜色                                                         | LoadingDialogDefaults.textColor()     | 否   |
| spinnerColor         | Color         | 加载指示器颜色                                                   | LoadingDialogDefaults.spinnerColor()  | 否   |
| backgroundColor      | Color         | 卡片背景色                                                       | DialogDefaults.backgroundColor()      | 否   |
| enableWindowDim      | Boolean       | 是否启用背景遮罩                                                 | true                                  | 否   |
| onDismissRequest     | (() -> Unit)? | 用户点击外部或按返回键时回调; 为 null 时用户无法关闭             | null                                  | 否   |
| onDismissFinished    | (() -> Unit)? | 隐藏动画完成后回调                                               | null                                  | 否   |

### LoadingDialogDefaults 对象

#### 属性

| 属性名        | 类型 | 说明                                                            |
| ------------- | ---- | --------------------------------------------------------------- |
| CardWidth     | Dp   | 卡片宽度, COUI `coui_spinner_layout_width` (152.dp)             |
| CardMinHeight | Dp   | 卡片最小高度, COUI `coui_spinner_layout_min_height` (120.dp)    |
| CornerRadius  | Dp   | 卡片圆角, COUI couiRoundCornerMRadius (9.dp)                    |
| SpinnerSize   | Dp   | 加载指示器直径, COUI `coui_spinner_loading_anim_width` (26.dp)  |

#### 函数

| 函数名         | 返回类型 | 说明                             |
| -------------- | -------- | -------------------------------- |
| textColor()    | Color    | 默认文字颜色 (主标签色)          |
| spinnerColor() | Color    | 默认加载指示器颜色 (主标签色)    |
