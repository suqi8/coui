# LoadingDialog

`LoadingDialog` 是一个始终居中的小卡片加载对话框, 包含旋转加载指示器与可选文字, 对应 ColorOS 的旋转进度对话框 (COUIRotatingDialogBuilder / `coui_progress_dialog_rotating.xml`): 152dp 宽、与普通对话框相同的 45dp 圆角卡片, 36dp 加载指示器 (上 28dp、下 12dp) 与 14sp 次级标签色文字。带取消按钮时切换为可取消形态 (`COUIAlertDialog.Rotating.Cancelable`)。

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

## 可取消形态

传入 `cancelAction` 添加取消按钮 (`COUIRotatingDialogBuilder.setCancelButton`), 并可传入 `title` (`setDialogTitle`)。卡片随之切换为 `COUIAlertDialog.Rotating.Cancelable` 形态: 宽 180dp、无最小高度, 文字下方 16dp 接单按钮栏。

```kotlin
OverlayLoadingDialog(
    show = showLoading,
    title = "正在下载",
    text = "请稍候...",
    cancelAction = DialogButtonBarAction(text = "取消", onClick = { showLoading = false }),
)
```

## 属性

### OverlayLoadingDialog 属性

| 属性名               | 类型          | 说明                                                             | 默认值                                | 必需 |
| -------------------- | ------------- | ---------------------------------------------------------------- | ------------------------------------- | ---- |
| show                 | Boolean       | 是否显示对话框                                                   | -                                     | 是   |
| modifier             | Modifier      | 应用于对话框的修饰符                                             | Modifier                              | 否   |
| backgroundModifier   | Modifier      | 应用在卡片圆角裁剪内、内容之下; 用于 COUI 17 模糊背景 (同时传入透明的 `backgroundColor`) | Modifier | 否   |
| text                 | String?       | 加载指示器下方的文字                                             | null                                  | 否   |
| textColor            | Color         | 文字颜色                                                         | LoadingDialogDefaults.textColor()     | 否   |
| spinnerColor         | Color         | 加载指示器颜色                                                   | LoadingDialogDefaults.spinnerColor()  | 否   |
| backgroundColor      | Color         | 卡片背景色                                                       | DialogDefaults.backgroundColor()      | 否   |
| enableWindowDim      | Boolean       | 是否启用背景遮罩                                                 | true                                  | 否   |
| onDismissRequest     | (() -> Unit)? | 用户点击外部或按返回键时回调; 为 null 时用户无法关闭             | null                                  | 否   |
| onDismissFinished    | (() -> Unit)? | 隐藏动画完成后回调                                               | null                                  | 否   |
| renderInRootScaffold | Boolean       | 是否渲染在根 (最外层) Scaffold 中。为 true 时对话框覆盖全屏, 为 false 时在当前 Scaffold 的范围内渲染 | true | 否   |
| materialStroke       | Boolean       | 是否绘制模糊卡片的 COUI 17 材质描边 (边缘光 + 内阴影); 需要 runtime shader | false | 否   |
| title                | String?       | 加载指示器上方的标题 (`setDialogTitle`)                          | null                                  | 否   |
| titleColor           | Color         | 标题颜色                                                         | DialogDefaults.titleColor()           | 否   |
| cancelAction         | DialogButtonBarAction? | 取消按钮 (`setCancelButton`); 传入后切换为可取消形态    | null                                  | 否   |

### WindowLoadingDialog 属性

与 `OverlayLoadingDialog` 相同, 但没有 `renderInRootScaffold` (对话框始终渲染在窗口层级):

| 属性名               | 类型          | 说明                                                             | 默认值                                | 必需 |
| -------------------- | ------------- | ---------------------------------------------------------------- | ------------------------------------- | ---- |
| show                 | Boolean       | 是否显示对话框                                                   | -                                     | 是   |
| modifier             | Modifier      | 应用于对话框的修饰符                                             | Modifier                              | 否   |
| backgroundModifier   | Modifier      | 应用在卡片圆角裁剪内、内容之下; 用于 COUI 17 模糊背景 (同时传入透明的 `backgroundColor`) | Modifier | 否   |
| text                 | String?       | 加载指示器下方的文字                                             | null                                  | 否   |
| textColor            | Color         | 文字颜色                                                         | LoadingDialogDefaults.textColor()     | 否   |
| spinnerColor         | Color         | 加载指示器颜色                                                   | LoadingDialogDefaults.spinnerColor()  | 否   |
| backgroundColor      | Color         | 卡片背景色                                                       | DialogDefaults.backgroundColor()      | 否   |
| enableWindowDim      | Boolean       | 是否启用背景遮罩                                                 | true                                  | 否   |
| onDismissRequest     | (() -> Unit)? | 用户点击外部或按返回键时回调; 为 null 时用户无法关闭             | null                                  | 否   |
| onDismissFinished    | (() -> Unit)? | 隐藏动画完成后回调                                               | null                                  | 否   |
| materialStroke       | Boolean       | 是否绘制模糊卡片的 COUI 17 材质描边 (边缘光 + 内阴影); 需要 runtime shader | false | 否   |
| title                | String?       | 加载指示器上方的标题 (`setDialogTitle`)                          | null                                  | 否   |
| titleColor           | Color         | 标题颜色                                                         | DialogDefaults.titleColor()           | 否   |
| cancelAction         | DialogButtonBarAction? | 取消按钮 (`setCancelButton`); 传入后切换为可取消形态    | null                                  | 否   |

### LoadingDialogDefaults 对象

#### 可取消形态

传入 `cancelAction` 添加取消按钮 (`COUIRotatingDialogBuilder.setCancelButton`), 并可传入 `title` (`setDialogTitle`)。卡片随之切换为 `COUIAlertDialog.Rotating.Cancelable` 形态: 宽 180dp、无最小高度, 文字下方 16dp 接单按钮栏。

```kotlin
OverlayLoadingDialog(
    show = showLoading,
    title = "正在下载",
    text = "请稍候...",
    cancelAction = DialogButtonBarAction(text = "取消", onClick = { showLoading = false }),
)
```

## 属性

| 属性名        | 类型 | 说明                                                            |
| ------------- | ---- | --------------------------------------------------------------- |
| CardWidth     | Dp   | 卡片宽度, COUI `coui_spinner_layout_width` (152.dp)             |
| CancelableCardWidth | Dp | 可取消形态卡片宽度, COUI `coui_loading_alert_dialog_max_width_cancelable` (180.dp) |
| CardMinHeight | Dp   | 卡片最小高度, COUI `coui_spinner_layout_min_height` (120.dp)    |
| CornerRadius  | Dp   | 卡片圆角, 与普通对话框相同的 `coui_round_corner_xxxxl_radius_large_16_1` (45.dp) |
| SpinnerSize   | Dp   | 加载指示器尺寸, COUI `coui_spinner_loading_anim_width` (36.dp)  |
| SpinnerRingDiameter | Dp | 加载指示器圆环直径 (33.43.dp)                              |
| SpinnerStrokeWidth | Dp | 加载指示器线宽 (2.57.dp)                                    |

#### 函数

| 函数名         | 返回类型 | 说明                             |
| -------------- | -------- | -------------------------------- |
| textColor()    | Color    | 默认文字颜色 (`?couiColorLabelSecondary`) |
| spinnerColor() | Color    | 默认加载指示器颜色 (`coui_rotating_loading.json` 固定描边色) |
