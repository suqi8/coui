# Tooltip

`Tooltip` 移植自 COUI 17 的 `COUIToolTips`：一个带箭头、指向锚点元素的气泡。`TooltipBox` 把它锚定到内容上并负责定位；它的 `tooltip` 槽填入 COUI 的三种气泡样式之一 —— `Tooltip`（Default）、`IconTooltip`（Icon）或 `ImageTooltip`（Image）。与 COUI 一致，tooltip 通过 `TooltipState` 以编程方式显示，且没有超时。

<div style="position: relative; height: 360px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=tooltip" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.TooltipBox
import io.github.suqi8.coui.kmp.basic.Tooltip
import io.github.suqi8.coui.kmp.basic.IconTooltip
import io.github.suqi8.coui.kmp.basic.ImageTooltip
import io.github.suqi8.coui.kmp.basic.TooltipBubble
import io.github.suqi8.coui.kmp.basic.TooltipStyle
import io.github.suqi8.coui.kmp.basic.TooltipMode
import io.github.suqi8.coui.kmp.basic.TooltipAnchorPosition
import io.github.suqi8.coui.kmp.basic.TooltipState
import io.github.suqi8.coui.kmp.basic.rememberTooltipState
import io.github.suqi8.coui.kmp.basic.TooltipDefaults
```

## 基本用法

```kotlin
val state = rememberTooltipState()
val scope = rememberCoroutineScope()

TooltipBox(
    state = state,
    tooltip = { Tooltip(text = "点击关闭图标即可关闭此提示。") },
) {
    TextButton(text = "显示提示", onClick = { scope.launch { state.show() } })
}
```

## 样式

| 样式 | 组件 | 外观 |
| ---- | ---- | ---- |
| `TooltipStyle.Default`，`TooltipMode.Tooltips` | `Tooltip(text)` | 强调色气泡（`couiColorTips`，此处取 `primary`），13sp 白字，末端 18dp 关闭图标 |
| `TooltipStyle.Default`，`TooltipMode.Info` | `Tooltip(text, mode = TooltipMode.Info)` | 白色「detail floating」气泡（`backgroundTop`），13sp 次要文字，无关闭图标，最窄 48dp |
| `TooltipStyle.Icon` | `IconTooltip(text, icon)` | 白色卡片：8dp 圆角的 40dp 图标、可选 16sp medium 标题、13sp 正文、可选关闭文字按钮 |
| `TooltipStyle.Image` | `ImageTooltip(text, image)` | 260dp 白色卡片：裁剪的图片（内缩 228 × 116dp、8dp 圆角，或通栏 132dp）、可选标题、正文、可选关闭按钮 |

所有气泡都是 17dp、权重 3.0 的平滑圆角矩形（`coui_tooltips_os_16_1_radius_17_dp`、`OS_16_1_WEIGHT`），最宽 328dp（Image 为 260dp），带 COUI SHADOW_LV4 阴影。箭头（20 × 10.1dp，2dp 圆角尖端、底部外扩圆弧；靠近圆角时换成 16.27 × 10.29dp 的贴边箭头）与气泡合并为同一轮廓。

## TooltipBox

```kotlin
@Composable
fun TooltipBox(
    state: TooltipState,
    tooltip: @Composable TooltipScope.() -> Unit,
    modifier: Modifier = Modifier,
    placement: TooltipAnchorPosition = TooltipAnchorPosition.Above,
    showArrow: Boolean = true,
    dismissOnTouchOutside: Boolean = false,
    enableUserInput: Boolean = false,
    offset: DpOffset = DpOffset.Zero,
    arrowFollowsOffset: Boolean = false,
    content: @Composable () -> Unit,
)
```

| 参数名 | 类型 | 说明 | 默认值 | 是否必须 |
| ------ | ---- | ---- | ------ | -------- |
| state | TooltipState | 控制显隐的状态 | - | 是 |
| tooltip | @Composable TooltipScope.() -> Unit | 气泡：`Tooltip`、`IconTooltip`、`ImageTooltip` 或 `TooltipBubble` | - | 是 |
| modifier | Modifier | 应用于锚点外层的修饰符 | Modifier | 否 |
| placement | TooltipAnchorPosition | 首选方向：`Above`、`Below`、`Left`、`Right`、`Start`、`End` | Above | 否 |
| showArrow | Boolean | 是否绘制箭头 | true | 否 |
| dismissOnTouchOutside | Boolean | 点击外部或返回键是否关闭（同时使窗口可获取焦点） | false | 否 |
| enableUserInput | Boolean | 悬停 / 长按锚点时是否显示 | false | 否 |
| offset | DpOffset | tooltip 窗口的额外偏移（RTL 下 x 取反） | DpOffset.Zero | 否 |
| arrowFollowsOffset | Boolean | 箭头是否随 `offset` 移动，而非继续指向锚点 | false | 否 |
| content | @Composable () -> Unit | 锚点内容 | - | 是 |

### 定位

- **Above / Below**：以锚点为中心，离窗口边缘保持 16dp。首选方向放不下时换到另一侧；两侧都放不下时取空间更大的一侧，内容可滚动。气泡距锚点约 5.4dp 再加 13dp 的窗口内边距，箭头尖端距锚点约 8.6dp。
- **Left / Right / Start / End**：与锚点垂直居中，间距 5.7dp。不会左右翻转：气泡收窄到该侧的可用宽度；若会触及窗口底部，则改为显示在锚点上方。
- 箭头沿气泡边缘跟随锚点中心；距圆角不足 9dp + 2dp 时换成贴边箭头，其直边延续气泡侧边。

### 动画与关闭

tooltip 从箭头尖端长出：缩放与透明度从 0 弹到 1（COUISpringForce bounce 0.2，缩放 response 0.4、透明度 0.3），关闭时弹回 0，此时去掉阴影并忽略触摸。通过 `state.dismiss()`、关闭图标 / 关闭按钮，或在 `dismissOnTouchOutside` 时点击外部 / 返回键关闭。

## Tooltip

```kotlin
@Composable
fun TooltipScope.Tooltip(
    text: String,
    modifier: Modifier = Modifier,
    mode: TooltipMode = TooltipMode.Tooltips,
    colors: TooltipColors = TooltipDefaults.tooltipColors(mode = mode),
    maxWidth: Dp = TooltipDefaults.MaxWidth,
    onCloseClick: (() -> Unit)? = null,
)
```

关闭图标（仅 Tooltips 模式，内容描述 "Close this tip"，触摸区 58dp）先关闭 tooltip 再调用 `onCloseClick`。另有一个以 `content` lambda 代替 `text` 的重载用于自定义内容，四周同样是 12dp 边距（Tooltips 模式末端 42dp）。

## IconTooltip

```kotlin
@Composable
fun TooltipScope.IconTooltip(
    text: String,
    icon: Painter,
    modifier: Modifier = Modifier,
    title: String? = null,
    dismissText: String? = null,
    colors: TooltipColors = TooltipDefaults.tooltipColors(style = TooltipStyle.Icon),
    maxWidth: Dp = TooltipDefaults.MaxWidth,
    onDismissClick: (() -> Unit)? = null,
)
```

## ImageTooltip

```kotlin
@Composable
fun TooltipScope.ImageTooltip(
    text: String,
    image: Painter,
    modifier: Modifier = Modifier,
    title: String? = null,
    dismissText: String? = null,
    imageWithEdges: Boolean = true,
    colors: TooltipColors = TooltipDefaults.tooltipColors(style = TooltipStyle.Image),
    maxWidth: Dp = TooltipDefaults.ImageMaxWidth,
    onDismissClick: (() -> Unit)? = null,
)
```

另有一个以 `media: @Composable BoxScope.() -> Unit` 槽代替 `image` 的重载，例如放循环动画。Icon 与 Image 的滚动区在可滚动时上下各有 24dp 渐隐边。

```kotlin
TooltipBox(
    state = state,
    placement = TooltipAnchorPosition.Below,
    tooltip = {
        ImageTooltip(
            title = "新功能",
            text = "三指下滑即可截屏。",
            image = painterResource(Res.drawable.screenshot_gesture),
            dismissText = "知道了",
        )
    },
) { /* 锚点 */ }
```

## TooltipBubble

`TooltipBubble(style, mode, containerColor, maxWidth, cornerRadius, content)` 是三种样式共用的气泡：窗口内边距、箭头、阴影、尺寸与裁剪。需要完全自定义内容时使用。

## TooltipState

```kotlin
@Composable
fun rememberTooltipState(
    initialIsVisible: Boolean = false,
    isPersistent: Boolean = true,
    mutatorMutex: MutatorMutex = BasicTooltipDefaults.GlobalMutatorMutex,
): TooltipState
```

`isPersistent` 默认为 true：COUI tooltip 会一直显示到被关闭。传 false 则在平台 tooltip 超时后自动关闭。`show()` 是挂起函数；全局 `MutatorMutex` 保证同时只显示一个 tooltip。

## TooltipDefaults

| 成员 | 说明 | 值 |
| ---- | ---- | -- |
| CornerRadius | 气泡圆角 | 17.dp |
| CornerWeight | 气泡圆角权重 | 3.0f |
| MaxWidth | Default / Icon 最大宽度 | 328.dp |
| ImageMaxWidth | Image 气泡宽度 | 260.dp |
| InfoMinWidth | Info 窗口最小宽度（含内边距） | 80.dp |
| windowInset(style, mode) | 气泡外容纳箭头与阴影的透明内边距 | 13 × 13dp；Info 为 16 × 13dp |
| maxWidth(style) / minWidth(style, mode) | 各样式的宽度限制 | - |
| tooltipColors(style, mode, …) | `TooltipColors(containerColor, contentColor, titleColor, actionColor)` | Tooltips：`primary` / `onPrimary`；其他：`backgroundTop` / `onSurfaceSecondary`；标题 `onSurface`；操作 `primary` |

## 已弃用

Miuix 版 tooltip API —— `TooltipBox(positionProvider, …)`、`TooltipBox(text, …)`、`RichTooltipBox`、`PlainTooltip`、`RichTooltip`、`RichTooltipColors`、`TooltipDefaults.rememberTooltipPositionProvider`、`caretShape` 以及 `PlainTooltip*` / `RichTooltip*` 常量 —— 仍可使用但已弃用。参见[升级指南](../guide/upgrading.md)。
