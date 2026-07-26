---
title: WindowDialog
requiresScaffoldHost: false
prerequisites:
  - 可以在任何地方使用，不需要 `Scaffold` 或 `COUIPopupHost`
  - 在窗口层级渲染
hostComponent: None
popupHost: None
---

# WindowDialog

`WindowDialog` 是一个窗口级对话框组件。它基于平台 `Dialog` 渲染，无需依赖 `Scaffold` 或 `COUIPopupHost`。支持大屏优化动效、系统返回手势关闭，以及在内容内部通过组合局部触发关闭。

<div style="position: relative; height: 240px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=windowDialog" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

::: tip 提示
该组件不依赖 `Scaffold`，可在任意 Composable 作用域中使用。
:::

## 引入

```kotlin
import io.github.suqi8.coui.kmp.window.WindowDialog
import io.github.suqi8.coui.kmp.theme.LocalDismissState
```

## 基本用法

```kotlin
var showDialog by remember { mutableStateOf(false) }

TextButton(
    text = "Open",
    onClick = { showDialog = true }
)

WindowDialog(
    title = "WindowDialog",
    summary = "A basic window-level dialog",
    show = showDialog,
    onDismissRequest = { showDialog = false }
) {
    val dismiss = LocalDismissState.current
    TextButton(
        text = "Confirm",
        onClick = { dismiss?.invoke() },
        modifier = Modifier.fillMaxWidth()
    )
}
```

## 属性

### WindowDialog 属性

| 属性名                     | 类型                   | 说明                                           | 默认值                                 | 是否必须 |
| -------------------------- | ---------------------- | ---------------------------------------------- | -------------------------------------- | -------- |
| show                       | Boolean                | 是否显示对话框                                 | -                                      | 是       |
| modifier                   | Modifier               | 根内容修饰符                                   | Modifier                               | 否       |
| title                      | String?                | 对话框标题                                     | null                                   | 否       |
| titleColor                 | Color                  | 标题颜色                                       | DialogDefaults.titleColor()      | 否       |
| summary                    | String?                | 对话框摘要                                     | null                                   | 否       |
| summaryColor               | Color                  | 摘要颜色                                       | DialogDefaults.summaryColor()    | 否       |
| backgroundColor            | Color                  | 对话框背景色                                   | DialogDefaults.backgroundColor() | 否       |
| enableWindowDim            | Boolean                | 是否启用遮罩层                                 | true                                   | 否       |
| onDismissRequest           | (() -> Unit)?          | 当用户请求关闭（点击遮罩层或返回手势）时触发   | null                                   | 否       |
| onDismissFinished          | (() -> Unit)?          | 关闭动画完成后调用；若关闭过程被中途取消（例如 `show` 被设回 true），则不会触发 | null      | 否       |
| outsideMargin              | DpSize                 | 相对窗口边缘的外部边距                         | DialogDefaults.outsideMargin     | 否       |
| insideMargin               | DpSize                 | 内置标题/摘要文本的边距（宽 = 水平内边距，高 = 标题上方内边距）；content 插槽不加内边距 | DialogDefaults.insideMargin      | 否       |
| defaultWindowInsetsPadding | Boolean                | 是否应用默认窗口插入内边距（输入法/导航/标题） | true                                   | 否       |
| maxWidth                   | Dp                     | 对话框内容最大宽度                             | DialogDefaults.MaxWidth          | 否       |
| largeScreen                | Boolean?               | 大屏呈现方式覆写（居中缩放/淡入替代底部滑入）；为 null 时根据窗口尺寸自动判断 | null | 否       |
| cornerRadius               | Dp?                    | 圆角半径覆写；为 null 时，居中呈现使用 DialogDefaults.CornerRadius，底部吸附时由屏幕圆角推导（限制在 32dp..48dp） | null | 否       |
| content                    | @Composable () -> Unit | 对话框内容                                     | -                                      | 是       |

### DialogDefaults

#### 属性

| 属性名        | 类型   | 说明                         |
| ------------- | ------ | ---------------------------- |
| CornerRadius  | Dp     | 对话框面板圆角（19.dp）      |
| MaxWidth      | Dp     | 对话框内容最大宽度（392.dp） |
| outsideMargin | DpSize | 对话框外部默认边距（16, 24） |
| insideMargin  | DpSize | 内置标题/摘要文本的默认边距（24, 24）；content 插槽不加内边距 |

#### 函数

| 函数名            | 返回类型 | 说明                   |
| ----------------- | -------- | ---------------------- |
| titleColor()      | Color    | 获取默认标题颜色       |
| summaryColor()    | Color    | 获取默认摘要颜色       |
| backgroundColor() | Color    | 获取默认对话框背景颜色 |

### LocalDismissState

提供一个 `(() -> Unit)?` 函数，用于从内容内部关闭当前弹窗。这是所有弹出组件提供的统一关闭状态。

```kotlin
val dismiss = LocalDismissState.current
TextButton(
    text = "Close",
    onClick = { dismiss?.invoke() }
)
```

## 进阶用法

### 呈现方式覆写

默认情况下，对话框在紧凑窗口中吸附于底部，在大窗口（>= 840dp x 480dp）中居中显示。可通过 `largeScreen`、`cornerRadius` 和 `maxWidth` 覆写呈现方式：

```kotlin
var showDialog by remember { mutableStateOf(false) }

WindowDialog(
    show = showDialog,
    title = "Custom Presentation",
    summary = "Forced centered presentation with custom shape",
    largeScreen = true,   // always use the centered scale/fade presentation
    cornerRadius = 24.dp, // override the panel corner radius
    maxWidth = 320.dp,    // narrower content width cap
    onDismissRequest = { showDialog = false }
) {
    val dismiss = LocalDismissState.current
    TextButton(
        text = "OK",
        onClick = { dismiss?.invoke() },
        modifier = Modifier.fillMaxWidth()
    )
}
```
