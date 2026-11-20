# 工具函数

COUI 提供了一系列工具函数，帮助您更高效地开发应用程序。以下是主要工具函数的详细介绍和使用示例。

## 弹出窗口工具 (COUIPopupUtils)

`COUIPopupUtils` 是一个用于显示对话框布局和弹出窗口布局的工具类。该类已经集成到 `Scaffold` 组件中，可以直接使用。

每个 `Scaffold` 会自动管理独立的弹出层状态，多个嵌套或并列的 `Scaffold` 无需额外配置即可正常工作。

### 对话框布局 (DialogLayout)

```kotlin
// 需要一个 MutableState<Boolean> 来控制显示状态
val showDialogState = remember { mutableStateOf(false) }

DialogLayout(
    visible = showDialogState,          // MutableState<Boolean> 用于控制对话框的可见性
    enterTransition = fadeIn(),         // 可选，对话框内容的自定义进入动画
    exitTransition = fadeOut(),         // 可选，对话框内容的自定义退出动画
    enableWindowDim = true,             // 可选，是否启用遮罩层, 默认为 true
    enableAutoLargeScreen = true,       // 可选，是否自动检测大屏幕并调整动画
    dimEnterTransition = fadeIn(),      // 可选，遮罩层的自定义进入动画
    dimExitTransition = fadeOut(),      // 可选，遮罩层的自定义退出动画
    dimAlpha = null,                    // 可选，MutableState<Float> 用于动态控制遮罩层透明度 (0f-1f)
    onDismissFinished = { /* 动画结束后回调 */ }, // 可选，对话框完全关闭（动画结束）时的回调
    renderInRootScaffold = true,       // 可选，是否在根（最外层）Scaffold 中渲染，默认为 true
) {
    // 对话框内容
}
```

正常情况下无需主动使用。详见 [OverlayDialog](../components/overlaydialog.md) 或 [OverlayBottomSheet](../components/overlaybottomsheet.md) 文档。

### 弹出窗口布局 (PopupLayout)

```kotlin
// 需要一个 MutableState<Boolean> 来控制显示状态
val showPopupState = remember { mutableStateOf(false) }

PopupLayout(
    visible = showPopupState,                         // MutableState<Boolean> 用于控制弹出窗口的可见性
    enterTransition = fadeIn(),                       // 可选，弹出窗口内容的自定义进入动画
    exitTransition = fadeOut(),                       // 可选，弹出窗口内容的自定义退出动画
    enableWindowDim = true,                           // 可选，是否启用遮罩层, 默认为 true
    dimEnterTransition = fadeIn(),                    // 可选，遮罩层的自定义进入动画
    dimExitTransition = fadeOut(),                   // 可选，遮罩层的自定义退出动画
    renderInRootScaffold = true,                     // 可选，是否在根（最外层）Scaffold 中渲染，默认为 true
) {
    // 弹出窗口内容
}
```

正常情况下无需主动使用。详见 [OverlayListPopup](../components/overlaylistpopup) 文档。

## 越界回弹效果 (Overscroll)

COUI 以修饰符和主题默认 `OverscrollEffect` 两种形式提供 COUI 17 滚动容器（`COUIRecyclerView`、`COUINestedScrollView`）的越界回弹：

- 拖过边缘时，内容只跟随手指的 `0.4 × (1 − |越界量| / 窗口尺寸)`（`COUIPhysicalAnimationUtil.calcRealOverScrollDist`），起始 40%，越拉越紧。
- 松手后按 `SpringOverScroller` 的 RK4 弹簧回弹（摩擦 12.19，张力竖向 16 × 2.15、横向 16 × 3.2，origami 单位，略过阻尼）。
- 快速滑动撞到边缘时，剩余速度带着内容冲过边缘再由同一弹簧拉回，冲出距离最多为窗口的 37.31%（`calcOverFlingDecelerateDist`）。越界状态下松手只回弹、不让内容惯性滚动（`COUIRecyclerView.performSpringBack`）。只有速度超过 6000px/s 的快速滑动例外：朝内容方向时，越界部分先沿 COUI 惯性曲线冲回边缘，再把剩余速度交给内容继续惯性滚动（`performOverScrollingReverseFling`）；朝外时，该速度带入回弹弹簧（`setCurrVelocityY`）。

### 垂直越界回弹

```kotlin
LazyColumn(
    modifier = Modifier
        // 添加越界回弹效果
        .overScrollVertical()
        // 如需绑定 TopAppBar 滚动事件，则应在越界回弹效果之后添加
        .nestedScroll(scrollBehavior.nestedScrollConnection)
) {
    // 列表内容
}
```

### 水平越界回弹

```kotlin
LazyRow(
    modifier = Modifier.overScrollHorizontal()
) {
    // 列表内容
}
```

### 越界回弹参数

```kotlin
LazyColumn(
    modifier = Modifier.overScrollVertical(
        nestedScrollToParent = true, // 是否分发嵌套滚动事件到父级，默认 true
        isEnabled = { platform() == Platform.Android || platform() == Platform.IOS } // 默认仅在 Android/iOS 启用
    ),
    overscrollEffect = null // 建议将此参数设置为 null，禁用默认效果
) {
    // 列表内容
}
```

**参数说明:**

* `nestedScrollToParent`：布尔值，是否分发嵌套滚动事件到父级。默认：`true`。
* `isEnabled`：Lambda，是否启用越界回弹。默认仅 Android/iOS。

### 通过 OverscrollFactory（主题级集成）

`COUIOverscrollFactory` 实现了 Compose 的 `OverscrollFactory` 接口，与 `overScrollVertical()` 提供完全相同的物理效果，但以主题级别集成。与 `COUIRecyclerView` / `COUINestedScrollView`（`couiRecyclerViewEnableVibrator`，默认开启）一样，快速滑动撞到边缘、或在越界状态下松手时（移动不足 10px 的轻点除外，`isClickEvent`），它会播放滚动边缘振动（`performHapticFeedback(307)`）；如需关闭可创建 `COUIOverscrollEffect(edgeHapticEnabled = false)`。两个 `COUITheme` 重载均已将其设置为默认工厂，因此所有标准 Compose 可滚动组件（`LazyColumn`、`LazyRow`、`verticalScroll`、`horizontalScroll` 等）无需任何额外修饰符即可自动使用 COUI 越界回弹效果。

```kotlin
// 无需额外修饰符 —— COUITheme 已自动提供越界回弹效果
LazyColumn {
    items(list) { item -> ItemRow(item) }
}
```

禁用特定组件的越界效果：

```kotlin
LazyColumn(overscrollEffect = null) {
    items(list) { item -> ItemRow(item) }
}
```

禁用整个子树的越界效果：

```kotlin
CompositionLocalProvider(LocalOverscrollFactory provides null) {
    // 此处的组件不会有 COUI 越界回弹效果
}
```

显式使用 `COUIOverscrollEffect`（例如与自定义 `Modifier.overscroll()` 配合）：

```kotlin
val overscrollEffect = rememberOverscrollEffect()

LazyColumn(
    modifier = Modifier.overscroll(overscrollEffect),
    overscrollEffect = overscrollEffect,
) {
    items(list) { item -> ItemRow(item) }
}
```

**与 `overScrollVertical()` 的对比：**

| 特性 | `overScrollVertical()` | `COUIOverscrollFactory` |
| :--- | :---: | :---: |
| 使用方式 | 每个组件手动添加 `Modifier` | 主题级别，自动生效 |
| 物理效果 | 相同弹簧物理 | 相同弹簧物理 |
| 边缘振动（307） | ❌（需加 `scrollEndHaptic()`） | ✅ |
| 默认启用平台 | Android、iOS | 全平台（通过主题） |
| 内容未超出容器时仍可触发 | ✅ | ❌ |
| 需要逐个组件手动添加修饰符 | ✅ | ❌ |

> **限制说明：** `COUIOverscrollFactory` 通过接收可滚动组件到达滚动边界后无法消耗的剩余 delta 来工作。如果内容未超出容器高度（例如 `LazyColumn` 的所有条目均能在屏幕内完整显示），可滚动组件不会到达真正的边界，`applyToScroll` 不会收到非零剩余量，越界回弹效果将**无法触发**。此类场景请改用 `overScrollVertical()`。

### COUI 惯性滚动（COUIFlingBehavior）

越界回弹只接管惯性滚动结束之后的部分；惯性滚动本身由可滚动组件的 `FlingBehavior` 决定，而 Compose 没有主题级的默认值。传入 `rememberCOUIFlingBehavior()` 即可获得 `COUIRecyclerView` / `COUINestedScrollView`（`SpringOverScroller`）的惯性滚动：距离与时长由松手速度的摩擦模拟得出，随速度从 2000 升到 20000px/s，距离按 1 → 0.8 → 0.4、时长按 1.3 → 0.8 → 0.5 缩放，内容在这段时间内沿 `COUISpringInterpolator`（刚度 40、阻尼比 1.15）运动——比 Android 默认惯性滑得更远、减速更柔和。滑到尽头时剩余速度交给 COUI 越界回弹。

```kotlin
LazyColumn(flingBehavior = rememberCOUIFlingBehavior()) { /* ... */ }
```

## 滚动到边界触觉反馈 (Modifier.scrollEndHaptic())

COUI 提供了用于在可滚动容器快速滑动到其开始或结束边界时触发触觉反馈的修饰符。默认播放 `COUINestedScrollView` 使用的 COUI 滚动边缘振动（`performHapticFeedback(307)`，`EDGE_LIST_VIBRATE`），非 ColorOS 设备退回最接近的标准触感。使用主题默认 `COUIOverscrollEffect` 的可滚动组件已经自带边缘振动；本修饰符用于 `overScrollVertical()` / `overScrollHorizontal()` 或未使用 COUI 越界回弹的容器。

```kotlin
LazyColumn(
    modifier = Modifier
        .fillMaxSize()
        // 添加滚动到边界触觉反馈
        .scrollEndHaptic()
        .overScrollVertical(),
    overscrollEffect = null
) {
    // 列表内容
}
```

**参数说明:**

* `hapticFeedbackType`：改用的标准 `HapticFeedbackType`；为 `null`（默认）时播放 COUI 滚动边缘振动。

## 按压反馈效果 (Modifier.pressable())

COUI 提供了视觉反馈效果来增强用户交互体验，通过类似触觉的响应提升操作感。

支持与 `Modifier.clickable()` 等响应式修饰符一起使用，`SinkFeedback` 为默认效果。

```kotlin
Box(
    modifier = Modifier
        .pressable()
        .background(Color.Blue)
        .size(100.dp)
)
```

### 下沉效果

`SinkFeedback` 指示会在组件被按下时应用一种“下沉”视觉效果，通过平滑缩放组件实现。

`interactionSource` 为 `null` 时，仅在需要时才会懒惰地创建内部 `MutableInteractionSource`，这会降低组合过程中可点击的性能成本。

```kotlin
Box(
    modifier = Modifier
        .pressable(interactionSource = null, indication = SinkFeedback())
        .background(Color.Blue)
        .size(100.dp)
)
```

### 倾斜效果

`TiltFeedback` 指示会根据用户按压组件的位置应用一种“倾斜”效果。倾斜方向由触摸偏移决定，使一角“下沉”而另一角保持“静止”。

```kotlin
val interactionSource = remember { MutableInteractionSource() }

Box(
    modifier = Modifier
        .pressable(interactionSource = interactionSource, indication = TiltFeedback())
        .combinedClickable(
            interactionSource = interactionSource,
            indication = LocalIndication.current,
            onClick = {},
            onLongClick = {}
        )
        .background(Color.Green)
        .size(100.dp)
)
```

### 按压反馈类型 (PressFeedbackType)

`PressFeedbackType` 枚举定义了组件被按下时可以应用的不同类型的视觉反馈。

| 类型 | 说明                                   |
| ---- | -------------------------------------- |
| None | 无视觉反馈                             |
| Sink | 应用下沉效果，组件在按下时轻微缩小     |
| Tilt | 应用倾斜效果，组件根据触摸位置轻微倾斜 |

## 选中态蒙层 (SelectedInteractionEffect)

`COUITheme` 安装的 `COUIIndication`（见 [状态蒙层](./theme#状态蒙层)）在条目报告为选中时也会绘制焦点层（`COUITheme.colorScheme.focus`），与 COUI `COUIMaskEffectDrawable` 对选中容器条目的处理一致。`SelectedInteractionEffect`（`io.github.suqi8.coui.kmp.utils`）负责提供这一状态：`selected` 变为 true 时向交互源发出 `COUISelectInteraction.Select`，变为 false 时发出对应的 `Unselect`，离开组合时撤销选中。把同一个交互源传给绘制该指示效果的 clickable 即可。

```kotlin
var selected by remember { mutableStateOf(false) }
val interactionSource = remember { MutableInteractionSource() }
SelectedInteractionEffect(selected = selected, interactionSource = interactionSource)

Box(
    modifier = Modifier
        .fillMaxWidth()
        .clickable(
            interactionSource = interactionSource,
            indication = LocalIndication.current, // COUITheme 下即 COUIIndication
            onClick = { selected = !selected }
        )
        .padding(16.dp)
) {
    Text("可选中的行")
}
```

| 参数              | 类型                     | 说明                       |
| ----------------- | ------------------------ | -------------------------- |
| selected          | Boolean                  | 条目是否选中               |
| interactionSource | MutableInteractionSource | 条目的指示效果所读取的交互源 |

如需手动控制，也可以自行向交互源发出 `COUISelectInteraction.Select()`，之后再发出 `COUISelectInteraction.Unselect(select)`。
