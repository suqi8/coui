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

`WindowBottomSheet` 是窗口级的底部抽屉组件。它使用平台 `Dialog` 渲染，不依赖 `Scaffold` 或 `COUIPopupHost`。支持系统返回手势关闭，以及在内容内部通过组合局部请求关闭。

它遵循 COUI 面板（`COUIBottomSheetDialog`）：窗口宽度小于 600dp 时从底部滑入并贴底；从 600dp 起改为居中浮窗，距窗口底部 64dp、四角圆角，以缩放（0.8 → 1）+ 淡入入场。宽度不超过 540dp。整个面板都可拖动，可滚动内容滚到顶后继续下拉才带动面板。松手时向下速度超过 500px/s 或拖过面板一半高度即关闭（遮罩立即淡出，已弹出的键盘随之收起）；其余情况松手后以 0.4s 弹簧、带着松手速度回到静止位置。上拉最多拉伸 28dp。面板仍在运动时切换显示 / 隐藏，动画会从面板当前位置掉头。

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
| cornerRadius               | Dp                        | 圆角半径（贴底时为顶部两角，浮窗时为四角）   | BottomSheetDefaults.cornerRadius      | 否       |
| sheetMaxWidth              | Dp                        | 在 COUI 栅格宽度与 540dp 上限之外的额外宽度上限 | BottomSheetDefaults.maxWidth          | 否       |
| onDismissRequest           | (() -> Unit)?             | 当用户请求关闭（点击遮罩层或返回手势）时触发 | null                                        | 否       |
| onDismissFinished          | (() -> Unit)?             | 关闭动画完成后调用；若关闭过程被中途取消（例如 `show` 被设回 true），则不会触发 | null      | 否       |
| outsideMargin              | DpSize                    | 底部抽屉外部边距                             | BottomSheetDefaults.outsideMargin     | 否       |
| insideMargin               | DpSize                    | 底部抽屉内部内容的边距                       | BottomSheetDefaults.insideMargin      | 否       |
| defaultWindowInsetsPadding | Boolean                   | 是否应用默认窗口插入内边距                   | true                                        | 否       |
| dragHandleColor            | Color                     | 拖拽指示器的颜色                             | BottomSheetDefaults.dragHandleColor() | 否       |
| showDragHandle             | Boolean                   | 是否显示拖拽把手（COUI 普通面板隐藏把手，顶部保留 10dp 间距） | false                       | 否       |
| handlePanel                | Boolean                   | COUI 把手面板：显示把手、占满最大高度，竖屏以半展开、横屏以 82dp 收起态打开，拖动后停在收起 / 半展开 / 展开三态之一 | false | 否 |
| showInMaxHeight            | Boolean                   | 以最大高度显示，而不是自适应内容高度         | false                                       | 否       |
| allowDismiss               | Boolean                   | 是否允许通过拖拽、返回手势或点击外部关闭抽屉；为 false 时下拉止于 82dp 露出高度（COUI 不可取消面板） | true | 否       |
| dismissOnClickOutside      | Boolean                   | 点击面板外部是否关闭（需 `allowDismiss` 为 true） | true                                   | 否       |
| draggable                  | Boolean                   | 面板是否跟随拖拽手势                         | true                                        | 否       |
| enablePullUp               | Boolean                   | 面板能否被上拉超过静止位置                   | true                                        | 否       |
| enableNestedScroll         | Boolean                   | 是否允许内容嵌套滚动                         | true                                        | 否       |
| backgroundModifier         | Modifier                  | 作用在抽屉圆角裁剪内、内容之下；用于 COUI 17 模糊背景（同时把 `backgroundColor` 设为透明） | Modifier | 否       |
| materialStroke             | Boolean                   | 是否绘制 COUI 17 模糊抽屉的材质描边（边缘光 + 内阴影）；需要 RuntimeShader | false | 否       |
| content                    | @Composable () -> Unit    | 底部抽屉的内容                               | -                                           | 是       |

### BottomSheetDefaults 对象

BottomSheetDefaults 对象为底部抽屉组件提供默认设置。

#### BottomSheetDefaults 属性

| 属性名        | 类型   | 说明                  |
| ------------- | ------ | --------------------- |
| cornerRadius  | Dp     | 默认圆角半径（45.dp，COUI 17 `coui_round_corner_xxxxl_radius_large_16_1`） |
| maxWidth      | Dp     | 默认宽度上限 (Dp.Infinity，实际宽度由 COUI 响应式栅格与 `PanelMaxWidth` 决定) |
| PanelMaxWidth | Dp     | 面板宽度硬上限（540.dp，`coui_panel_max_width`） |
| LargeScreenWidthThreshold | Dp | 面板改为居中浮窗的窗口宽度（600.dp） |
| LargeScreenMarginBottom | Dp | 浮窗距窗口底部的距离（64.dp） |
| DragHandleShownHeight | Dp | 显示把手时把手区域高度（16.dp） |
| DragHandleHiddenHeight | Dp | 隐藏把手时内容上方保留的间距（10.dp） |
| outsideMargin | DpSize | 底部抽屉外部默认边距 (DpSize(0.dp, 0.dp)) |
| insideMargin  | DpSize | 底部抽屉内部默认边距 (DpSize(0.dp, 0.dp)：COUI 面板自身没有内边距，由内容自行设置边距) |

#### BottomSheetDefaults 函数

| 函数名            | 返回类型 | 说明                   |
| ----------------- | -------- | ---------------------- |
| backgroundColor() | Color    | 获取默认背景颜色       |
| dragHandleColor() | Color    | 获取默认拖拽指示器颜色（`colorScheme.dragHandle`，COUI `?couiColorControls`） |

## 进阶用法

### 模糊材质（COUI 17）

`backgroundModifier` 与 `materialStroke` 的用法同 [OverlayBottomSheet](./overlaybottomsheet#模糊材质-coui-17)。
但 `WindowBottomSheet` 位于独立的平台窗口中，主窗口录制的 `LayerBackdrop` 不保证能与之对齐；需要模糊材质时优先使用 `OverlayBottomSheet`。

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

设置 `allowDismiss = false` 可忽略下拉拖拽与返回手势关闭，此时抽屉只能通过代码关闭。下拉只会把面板收起到 82dp 露出高度，与 COUI 不可取消面板一致：

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
