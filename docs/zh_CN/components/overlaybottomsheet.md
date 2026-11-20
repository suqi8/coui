---
title: OverlayBottomSheet
requiresScaffoldHost: true
prerequisites:
  - 必须在 `Scaffold` 中使用以提供 `COUIPopupHost`
  - 未在 `Scaffold` 中使用将导致弹出内容无法渲染
  - 支持多个嵌套或并列的 `Scaffold`，无需额外配置
hostComponent: Scaffold
popupHost: COUIPopupHost
---

# OverlayBottomSheet

`OverlayBottomSheet` 对应 COUI 面板（`COUIBottomSheetDialog`）。窗口宽度小于 600dp 时从底部滑入并贴底显示；从 600dp 起改为居中浮窗，距窗口底部 64dp、四角圆角，以缩放（0.8 → 1）+ 淡入入场。宽度跟随 COUI 栅格，且不超过 540dp。整个面板都可拖动；可滚动内容先滚动，滚到顶后继续下拉才带动面板。

松手时向下速度超过 500px/s 或拖过面板一半高度即关闭（经由内容拖动时，只要最后一次移动方向向下就关闭）；此时遮罩立即按自身弹簧淡出、面板滑出屏幕，已弹出的键盘随之收起。其余情况松手后以 0.4s 弹簧、带着松手速度回到静止位置，冲过静止位置的部分只显示 40%。上拉时面板按手指位移的 0.2 拉伸，最多 28dp，松手回弹。`allowDismiss = false` 时面板只跟手下拉到仅剩 82dp 露出高度为止，随后停在收起态或弹回。面板仍在运动时切换显示 / 隐藏，动画会从面板当前位置掉头。

<div style="position: relative; height: 240px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=overlayBottomSheet" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

::: danger 使用前提
此组件依赖 `Scaffold` 提供的 `COUIPopupHost` 以显示弹出内容。必须在 `Scaffold` 中使用，否则弹出内容无法正常渲染。
:::

## 引入

```kotlin
import io.github.suqi8.coui.kmp.overlay.OverlayBottomSheet
import io.github.suqi8.coui.kmp.theme.LocalDismissState
```

## 基本用法

OverlayBottomSheet 组件提供了基础的底部抽屉功能:

```kotlin
var showBottomSheet by remember { mutableStateOf(false) }

Scaffold {
    TextButton(
        text = "显示底部抽屉",
        onClick = { showBottomSheet = true }
    )

    OverlayBottomSheet(
        show = showBottomSheet,
        title = "底部抽屉标题",
        onDismissRequest = { showBottomSheet = false }
    ) {
        Text(text = "这是底部抽屉的内容")
    }
}
```

## 属性

### OverlayBottomSheet 属性

| 属性名                     | 类型                      | 说明                                         | 默认值                                     | 是否必须 |
| -------------------------- | ------------------------- | -------------------------------------------- | ------------------------------------------ | -------- |
| show                       | Boolean                   | 是否显示底部抽屉                             | -                                          | 是       |
| modifier                   | Modifier                  | 应用于底部抽屉的修饰符                       | Modifier                                   | 否       |
| title                      | String?                   | 底部抽屉的标题                               | null                                       | 否       |
| startAction                | @Composable (() -> Unit)? | 可选的左侧操作按钮(例如关闭按钮)             | null                                       | 否       |
| endAction                  | @Composable (() -> Unit)? | 可选的右侧操作按钮(例如提交按钮)             | null                                       | 否       |
| backgroundColor            | Color                     | 底部抽屉背景色                               | BottomSheetDefaults.backgroundColor() | 否       |
| enableWindowDim            | Boolean                   | 是否启用遮罩层                               | true                                       | 否       |
| cornerRadius               | Dp                        | 圆角半径（贴底时为顶部两角，浮窗时为四角）   | BottomSheetDefaults.cornerRadius      | 否       |
| sheetMaxWidth              | Dp                        | 在 COUI 栅格宽度与 540dp 上限之外的额外宽度上限 | BottomSheetDefaults.maxWidth          | 否       |
| onDismissRequest           | (() -> Unit)?             | 当用户请求关闭（点击遮罩层或返回手势）时触发 | null                                       | 否       |
| onDismissFinished          | (() -> Unit)?             | 关闭动画完成后调用；若关闭过程被中途取消（例如 `show` 被设回 true），则不会触发 | null     | 否       |
| outsideMargin              | DpSize                    | 底部抽屉外部边距                             | BottomSheetDefaults.outsideMargin     | 否       |
| insideMargin               | DpSize                    | 底部抽屉内部内容的边距                       | BottomSheetDefaults.insideMargin      | 否       |
| defaultWindowInsetsPadding | Boolean                   | 是否应用默认窗口插入内边距                   | true                                       | 否       |
| dragHandleColor            | Color                     | 拖拽指示器的颜色                             | BottomSheetDefaults.dragHandleColor() | 否       |
| showDragHandle             | Boolean                   | 是否显示拖拽把手（COUI 普通面板隐藏把手，顶部保留 10dp 间距） | false                      | 否       |
| handlePanel                | Boolean                   | COUI 把手面板：显示把手、占满最大高度，竖屏以半展开、横屏以 82dp 收起态打开，拖动后停在收起 / 半展开 / 展开三态之一 | false | 否 |
| showInMaxHeight            | Boolean                   | 以最大高度显示，而不是自适应内容高度         | false                                      | 否       |
| allowDismiss               | Boolean                   | 是否允许通过拖拽、返回手势或点击外部关闭抽屉；为 false 时下拉止于 82dp 露出高度（COUI 不可取消面板） | true | 否       |
| dismissOnClickOutside      | Boolean                   | 点击面板外部是否关闭（需 `allowDismiss` 为 true） | true                                  | 否       |
| draggable                  | Boolean                   | 面板是否跟随拖拽手势                         | true                                       | 否       |
| enablePullUp               | Boolean                   | 面板能否被上拉超过静止位置                   | true                                       | 否       |
| enableNestedScroll         | Boolean                   | 是否允许内容嵌套滚动                         | true                                       | 否       |
| renderInRootScaffold       | Boolean                   | 是否在根（最外层）Scaffold 中渲染底部抽屉。为 true 时，覆盖全屏。为 false 时，在当前 Scaffold 的范围内渲染 | true | 否 |
| backgroundModifier         | Modifier                  | 作用在抽屉圆角裁剪内、内容之下；用于 COUI 17 模糊背景（同时把 `backgroundColor` 设为透明） | Modifier | 否       |
| materialStroke             | Boolean                   | 是否绘制 COUI 17 模糊抽屉的材质描边（边缘光 + 内阴影）；需要 RuntimeShader | false | 否       |
| content                    | @Composable () -> Unit    | 底部抽屉的内容                               | -                                          | 是       |

### BottomSheetDefaults

#### BottomSheetDefaults 属性

| 属性名        | 类型   | 说明                  |
| ------------- | ------ | --------------------- |
| cornerRadius  | Dp     | 默认圆角半径（45.dp，COUI 17 `coui_round_corner_xxxxl_radius_large_16_1`） |
| maxWidth      | Dp     | 默认宽度上限 (Dp.Infinity，实际宽度由 COUI 响应式栅格与 `PanelMaxWidth` 决定) |
| PanelMaxWidth | Dp     | 面板宽度硬上限（540.dp，`coui_panel_max_width`） |
| LargeScreenWidthThreshold | Dp | 面板改为居中浮窗的窗口宽度（600.dp） |
| LargeScreenMarginBottom | Dp | 浮窗距窗口底部的距离（64.dp，`coui_bottom_sheet_margin_bottom_default`） |
| DragHandleShownHeight | Dp | 显示把手时把手区域高度（16.dp） |
| DragHandleHiddenHeight | Dp | 隐藏把手时内容上方保留的间距（10.dp，`coui_panel_drag_view_hide_height`） |
| outsideMargin | DpSize | 底部抽屉外部默认边距 (DpSize(0.dp, 0.dp)) |
| insideMargin  | DpSize | 底部抽屉内部默认边距 (DpSize(0.dp, 0.dp)：COUI 面板自身没有内边距，由内容自行设置边距；宽 = 水平内边距，高 = 底部内边距) |

#### BottomSheetDefaults 函数

| 函数名            | 返回类型 | 说明                   |
| ----------------- | -------- | ---------------------- |
| backgroundColor() | Color    | 获取默认背景颜色       |
| dragHandleColor() | Color    | 获取默认拖拽指示器颜色（`colorScheme.dragHandle`，COUI `?couiColorControls`） |

## 进阶用法

### 把手面板

```kotlin
OverlayBottomSheet(
    show = showSheet,
    title = "Handle Panel",
    handlePanel = true,
    onDismissRequest = { showSheet = false },
) {
    LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp)) {
        items(50) { Text("Item $it", modifier = Modifier.padding(vertical = 12.dp)) }
    }
}
```

面板以半展开态打开；向上滚动列表会先把面板展开，向下拖动则收起到 82dp 的露出高度或关闭。

### 模糊材质（COUI 17）

系统材质模糊开启时，COUI 17 会模糊面板背后的窗口（`COUIBottomSheetDialog.initBlurParams`：半径
`coui_bottomdialog_blur_radius` 400px，浅/深色各一组 mix 与 blend 颜色，混合模式为 overlay / color dodge），
并沿面板绘制边缘光描边。coui-ui 不依赖模糊模块：通过 `backgroundModifier` 传入模糊（例如 coui-blur 的
`couiMaterialBlur`），并打开 `materialStroke`。不支持 RuntimeShader 时保留不透明的
`BottomSheetDefaults.backgroundColor()`。

```kotlin
val backdrop = rememberLayerBackdrop()
val isDark = isSystemInDarkTheme()

Box(Modifier.fillMaxSize().layerBackdrop(backdrop)) { /* 页面内容 */ }

OverlayBottomSheet(
    show = showSheet,
    title = "模糊抽屉",
    backgroundColor = Color.Transparent,
    backgroundModifier = Modifier.couiMaterialBlur(backdrop, MaterialBlurDefaults.bottomSheet(isDark)),
    materialStroke = true,
    onDismissRequest = { showSheet = false },
) {
    Text("抽屉背后的页面会被模糊。")
}
```

### 自定义样式底部抽屉

```kotlin
var showBottomSheet by remember { mutableStateOf(false) }

Scaffold {
    TextButton(
        text = "显示自定义样式底部抽屉",
        onClick = { showBottomSheet = true }
    )

    OverlayBottomSheet(
        show = showBottomSheet,
        title = "自定义样式",
        backgroundColor = COUITheme.colorScheme.card,
        dragHandleColor = COUITheme.colorScheme.primary,
        outsideMargin = DpSize(16.dp, 0.dp),
        insideMargin = DpSize(32.dp, 16.dp),
        onDismissRequest = { showBottomSheet = false }
    ) {
        Column {
            Text("自定义样式的底部抽屉")
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                text = "关闭",
                onClick = { showBottomSheet = false },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
```

### 带列表内容的底部抽屉

```kotlin
var showBottomSheet by remember { mutableStateOf(false) }
var selectedItem by remember { mutableStateOf("") }

Scaffold {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TextButton(
            text = "显示选择列表",
            onClick = { showBottomSheet = true }
        )
        
        Text("已选择: $selectedItem")
    }

    OverlayBottomSheet(
        show = showBottomSheet,
        title = "选择项目",
        onDismissRequest = { showBottomSheet = false }
    ) {
        LazyColumn {
            items(20) { index ->
                Text(
                    text = "项目 ${index + 1}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedItem = "项目 ${index + 1}"
                            showBottomSheet = false
                        }
                        .padding(vertical = 12.dp)
                )
            }
        }
    }
}
```

### 不使用遮罩层

```kotlin
var showBottomSheet by remember { mutableStateOf(false) }

Scaffold {
    TextButton(
        text = "显示无遮罩底部抽屉",
        onClick = { showBottomSheet = true }
    )

    OverlayBottomSheet(
        show = showBottomSheet,
        title = "无遮罩层",
        enableWindowDim = false,
        onDismissRequest = { showBottomSheet = false }
    ) {
        Text("这个底部抽屉没有背景遮罩层")
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(
            text = "关闭",
            onClick = { showBottomSheet = false },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
```

### 带操作按钮的底部抽屉

```kotlin
var showBottomSheet by remember { mutableStateOf(false) }

Scaffold {
    TextButton(
        text = "显示带操作按钮的底部抽屉",
        onClick = { showBottomSheet = true }
    )

    OverlayBottomSheet(
        show = showBottomSheet,
        title = "操作面板",
        startAction = {
            val dismiss = LocalDismissState.current
            TextButton(
                text = "取消",
                onClick = { dismiss?.invoke() }
            )
        },
        endAction = {
            val dismiss = LocalDismissState.current
            TextButton(
                text = "确认",
                onClick = { dismiss?.invoke() },
                colors = ButtonDefaults.textButtonColorsPrimary()
            )
        },
        onDismissRequest = { showBottomSheet = false }
    ) {
        Text("带有自定义标题栏操作按钮的内容")
        Spacer(modifier = Modifier.height(16.dp))
        Text("左右两侧的操作按钮显示在标题栏中")
    }
}
```

### 带表单的底部抽屉

```kotlin
var showBottomSheet by remember { mutableStateOf(false) }
var textFieldValue by remember { mutableStateOf("") }
var switchState by remember { mutableStateOf(false) }

Scaffold {
    TextButton(
        text = "显示表单底部抽屉",
        onClick = { showBottomSheet = true }
    )

    OverlayBottomSheet(
        show = showBottomSheet,
        title = "设置表单",
        onDismissRequest = { showBottomSheet = false }
    ) {
        Card(
            colors = CardDefaults.defaultColors(
                color = COUITheme.colorScheme.secondaryContainer,
            ),
        ) {
            TextField(
                modifier = Modifier.padding(vertical = 12.dp),
                value = textFieldValue,
                label = "输入内容",
                maxLines = 1,
                onValueChange = { textFieldValue = it }
            )
            
            SwitchPreference(
                title = "开关选项",
                checked = switchState,
                onCheckedChange = { switchState = it }
            )
        }
        
        Spacer(Modifier.height(12.dp))
        
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                text = "取消",
                onClick = { showBottomSheet = false },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(20.dp))
            TextButton(
                text = "确认",
                onClick = { showBottomSheet = false },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColorsPrimary()
            )
        }
    }
}
```

### 自适应内容高度

```kotlin
var showBottomSheet by remember { mutableStateOf(false) }

Scaffold {
    TextButton(
        text = "显示自适应高度底部抽屉",
        onClick = { showBottomSheet = true }
    )

    OverlayBottomSheet(
        show = showBottomSheet,
        title = "自适应高度",
        onDismissRequest = { showBottomSheet = false }
    ) {
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            Text("高度会自动适应内容")
            Spacer(modifier = Modifier.height(16.dp))
            Text("可以添加任意多的内容")
            Spacer(modifier = Modifier.height(16.dp))
            Text("但不会覆盖到状态栏区域")
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                text = "关闭",
                onClick = { showBottomSheet = false },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
```

### 禁止关闭的底部抽屉

```kotlin
var showBottomSheet by remember { mutableStateOf(false) }

Scaffold {
    TextButton(
        text = "显示禁止关闭的底部抽屉",
        onClick = { showBottomSheet = true }
    )

    OverlayBottomSheet(
        show = showBottomSheet,
        title = "禁止关闭",
        allowDismiss = false,
        onDismissRequest = { showBottomSheet = false }
    ) {
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            Text("这个底部抽屉无法通过拖拽或返回手势关闭")
            Spacer(modifier = Modifier.height(16.dp))
            Text("您必须通过下面的按钮显式关闭它")
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                text = "关闭",
                onClick = { showBottomSheet = false },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
```
