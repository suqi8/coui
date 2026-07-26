---
title: WindowBottomSheet
requiresScaffoldHost: false
prerequisites:
  - 可以在任何地方使用，不需要 `Scaffold` 或 `COUIPopupHost`
  - 在窗口层级渲染
hostComponent: None
popupHost: None
---

# WindowBottomSheet

`WindowBottomSheet` 是窗口级的底部抽屉组件。它使用平台 `Dialog` 渲染，不依赖 `Scaffold` 或 `COUIPopupHost`。支持大屏优化的动效、系统返回手势关闭，以及在内容内部通过组合局部请求关闭。

<div style="position: relative; height: 240px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=windowBottomSheet" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

::: tip 提示
该组件不依赖 `Scaffold`，可在任意 Composable 作用域中使用。
:::

## 引入

```kotlin
import io.github.suqi8.coui.kmp.window.WindowBottomSheet
import io.github.suqi8.coui.kmp.theme.LocalDismissState
```

## 基本用法

`WindowBottomSheet` 组件提供了基础的底部抽屉功能：

```kotlin
var showBottomSheet by remember { mutableStateOf(false) }

// Can be used anywhere
TextButton(
    text = "Show Window Bottom Sheet",
    onClick = { showBottomSheet = true }
)

WindowBottomSheet(
    show = showBottomSheet,
    title = "Window Bottom Sheet Title",
    onDismissRequest = { showBottomSheet = false }
) {
    val dismiss = LocalDismissState.current
    Text(text = "This is the content of the window bottom sheet")
    TextButton(
        text = "Close",
        onClick = { dismiss?.invoke() }
    )
}
```

## 属性

### WindowBottomSheet 属性

| 属性名                     | 类型                      | 说明                                         | 默认值                                      | 是否必须 |
| -------------------------- | ------------------------- | -------------------------------------------- | ------------------------------------------- | -------- |
| show                       | Boolean                   | 是否显示底部抽屉                             | -                                           | 是       |
| modifier                   | Modifier                  | 应用于底部抽屉的修饰符                       | Modifier                                    | 否       |
| title                      | String?                   | 底部抽屉的标题                               | null                                        | 否       |
| startAction                | @Composable (() -> Unit)? | 可选的左侧操作按钮(例如关闭按钮)             | null                                        | 否       |
| endAction                  | @Composable (() -> Unit)? | 可选的右侧操作按钮(例如提交按钮)             | null                                        | 否       |
| backgroundColor            | Color                     | 底部抽屉背景色                               | BottomSheetDefaults.backgroundColor() | 否       |
| enableWindowDim            | Boolean                   | 是否启用遮罩层                               | true                                        | 否       |
| cornerRadius               | Dp                        | 顶部圆角半径                                 | BottomSheetDefaults.cornerRadius      | 否       |
| sheetMaxWidth              | Dp                        | 底部抽屉的最大宽度                           | BottomSheetDefaults.maxWidth          | 否       |
| onDismissRequest           | (() -> Unit)?             | 当用户请求关闭（点击遮罩层或返回手势）时触发 | null                                        | 否       |
| onDismissFinished          | (() -> Unit)?             | 关闭动画完成后调用；若关闭过程被中途取消（例如 `show` 被设回 true），则不会触发 | null      | 否       |
| outsideMargin              | DpSize                    | 底部抽屉外部边距                             | BottomSheetDefaults.outsideMargin     | 否       |
| insideMargin               | DpSize                    | 底部抽屉内部内容的边距                       | BottomSheetDefaults.insideMargin      | 否       |
| defaultWindowInsetsPadding | Boolean                   | 是否应用默认窗口插入内边距                   | true                                        | 否       |
| dragHandleColor            | Color                     | 拖拽指示器的颜色                             | BottomSheetDefaults.dragHandleColor() | 否       |
| allowDismiss               | Boolean                   | 是否允许通过拖拽或返回手势关闭抽屉           | true                                        | 否       |
| enableNestedScroll         | Boolean                   | 是否允许内容嵌套滚动                         | true                                        | 否       |
| content                    | @Composable () -> Unit    | 底部抽屉的内容                               | -                                           | 是       |

### BottomSheetDefaults 对象

BottomSheetDefaults 对象为底部抽屉组件提供默认设置。

#### BottomSheetDefaults 属性

| 属性名        | 类型   | 说明                  |
| ------------- | ------ | --------------------- |
| cornerRadius  | Dp     | 默认圆角半径 (20.dp，COUI couiRoundCornerXL) |
| maxWidth      | Dp     | 默认宽度上限 (Dp.Infinity，实际宽度由 COUI 响应式栅格决定) |
| outsideMargin | DpSize | 底部抽屉外部默认边距  |
| insideMargin  | DpSize | 底部抽屉内部默认边距  |

#### BottomSheetDefaults 函数

| 函数名            | 返回类型 | 说明                   |
| ----------------- | -------- | ---------------------- |
| backgroundColor() | Color    | 获取默认背景颜色       |
| dragHandleColor() | Color    | 获取默认拖拽指示器颜色 |

## 进阶用法

### 头部操作按钮

使用 `startAction` 与 `endAction` 在头部区域放置操作按钮。`LocalDismissState` 会同时提供给两个操作插槽和内容插槽：

```kotlin
var showBottomSheet by remember { mutableStateOf(false) }

WindowBottomSheet(
    show = showBottomSheet,
    title = "Action Sheet",
    startAction = {
        val dismiss = LocalDismissState.current
        TextButton(
            text = "Cancel",
            onClick = { dismiss?.invoke() }
        )
    },
    endAction = {
        val dismiss = LocalDismissState.current
        TextButton(
            text = "Confirm",
            onClick = { dismiss?.invoke() }
        )
    },
    onDismissRequest = { showBottomSheet = false }
) {
    Text("Content with header action buttons")
}
```

### 从内容中关闭

您可以使用 `LocalDismissState` 从其内容中关闭底部抽屉：

```kotlin
WindowBottomSheet(
    show = showBottomSheet,
    title = "Dismiss Example",
    onDismissRequest = { showBottomSheet = false }
) {
    val dismiss = LocalDismissState.current

    TextButton(
        text = "Close Bottom Sheet",
        onClick = { dismiss?.invoke() }
    )
}
```

### 禁止手势关闭

设置 `allowDismiss = false` 可忽略下拉拖拽与返回手势关闭，此时抽屉只能通过代码关闭：

```kotlin
var showBottomSheet by remember { mutableStateOf(false) }

WindowBottomSheet(
    show = showBottomSheet,
    title = "Processing",
    allowDismiss = false, // drag and back gesture will not dismiss the sheet
    onDismissRequest = { showBottomSheet = false }
) {
    Text("This sheet can only be closed programmatically")
    TextButton(
        text = "Done",
        onClick = { showBottomSheet = false }
    )
}
```
