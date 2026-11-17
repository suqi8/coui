# Snackbar

`Snackbar` 是 COUI 中用于在屏幕底部展示简短提示信息的轻量反馈组件。它可以附带操作按钮（例如“撤销”），并支持多种显示时长。

<div style="position: relative; height: 360px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=snackbar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.Snackbar
import io.github.suqi8.coui.kmp.basic.SnackbarHost
import io.github.suqi8.coui.kmp.basic.SnackbarHostState
import io.github.suqi8.coui.kmp.basic.SnackbarDuration
import io.github.suqi8.coui.kmp.basic.SnackbarResult
```

## 基本用法

Snackbar 通常与 `Scaffold` 一起使用。你需要创建一个 `SnackbarHostState`，将其传给 `SnackbarHost`，再通过 `showSnackbar` 来显示消息：

```kotlin
val snackbarHostState = remember { SnackbarHostState() }
val scope = rememberCoroutineScope()

Scaffold(
    snackbarHost = {
        SnackbarHost(state = snackbarHostState)
    },
) { paddingValues ->
    Box(
        modifier = Modifier
            .padding(paddingValues),
    ) {
        TextButton(
            text = "显示消息",
            onClick = {
                scope.launch {
                    snackbarHostState.showSnackbar("这是一条短提示")
                }
            },
        )
    }
}
```

## SnackbarHostState 与 showSnackbar

`SnackbarHostState` 负责管理 Snackbar 消息队列。

### showSnackbar

```kotlin
suspend fun SnackbarHostState.showSnackbar(
    message: String,
    actionLabel: String? = null,
    withDismissAction: Boolean = false,
    duration: SnackbarDuration = SnackbarDuration.Short,
): SnackbarResult
```

| 参数名             | 类型             | 说明                             | 默认值                    | 是否必须 |
| ------------------ | ---------------- | -------------------------------- | ------------------------- | -------- |
| message            | String           | 要显示在 Snackbar 中的文本       | -                         | 是       |
| actionLabel        | String?          | 操作按钮的文本（可选）          | null                      | 否       |
| withDismissAction  | Boolean          | 是否显示关闭图标                 | false                     | 否       |
| duration           | SnackbarDuration | Snackbar 显示的时长              | SnackbarDuration.Short    | 否       |

返回值 `SnackbarResult` 用于区分 Snackbar 是被操作按钮触发，还是自然消失或关闭。

### 获取最新或最旧的 Snackbar

```kotlin
suspend fun SnackbarHostState.newestSnackbarData(): SnackbarData?
suspend fun SnackbarHostState.oldestSnackbarData(): SnackbarData?
```

你可以通过返回的 `SnackbarData` 调用 `dismiss()` 或 `performAction()`，以手动关闭最新或最旧的 Snackbar。

## SnackbarHost

`SnackbarHost` 负责根据给定的 `SnackbarHostState` 渲染屏幕上的 Snackbar。

```kotlin
@Composable
fun SnackbarHost(
    state: SnackbarHostState,
    modifier: Modifier = Modifier,
    canSwipeToDismiss: Boolean = true,
    content: @Composable (SnackbarData) -> Unit = { Snackbar(it) },
)
```

| 参数名            | 类型                               | 说明                              | 默认值             | 是否必须 |
| ----------------- | ---------------------------------- | --------------------------------- | ------------------ | -------- |
| state             | SnackbarHostState                  | 管理 Snackbar 队列的状态          | -                  | 是       |
| modifier          | Modifier                           | 应用于宿主容器的修饰符            | Modifier           | 否       |
| canSwipeToDismiss | Boolean                            | 是否允许通过滑动手势关闭 Snackbar | true               | 否       |
| content           | @Composable (SnackbarData) -> Unit | 自定义每一条 Snackbar 的内容      | `{ Snackbar(it) }` | 否       |

大多数情况下，可以直接使用默认的 `content`，即内置的 `Snackbar` 视觉样式。

手指按住 Snackbar 时自动消失计时暂停，松手后按完整时长重新计时（COUISnackBar `onTouchEvent`：DOWN / MOVE 取消，UP / CANCEL 重新计时）。滑动关闭、多条堆叠与关闭图标为本库扩展，COUI 无对应。

## Snackbar

`Snackbar` 定义了默认的消息样式。

```kotlin
@Composable
fun Snackbar(
    data: SnackbarData,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    cornerRadius: Dp = SnackbarDefaults.CornerRadius,
    singleLineCornerRadius: Dp = SnackbarDefaults.SingleLineCornerRadius,
    maxWidth: Dp = SnackbarDefaults.maxWidth(),
    colors: SnackbarColors = SnackbarDefaults.snackbarColors(),
    insideMargin: PaddingValues = SnackbarDefaults.InsideMargin,
)
```

| 参数名                 | 类型           | 说明                     | 默认值                            | 是否必须 |
| ---------------------- | -------------- | ------------------------ | --------------------------------- | -------- |
| data                   | SnackbarData   | 包含消息与操作信息的数据 | -                                 | 是       |
| modifier               | Modifier       | 应用于 Snackbar 容器的修饰符 | Modifier                      | 否       |
| icon                   | (@Composable () -> Unit)? | 可选前置图标，布局在 30.dp 容器内（通过 `SnackbarHost` 的 `content` 插槽传入） | null | 否       |
| cornerRadius           | Dp             | 垂直（多行）布局的圆角半径，按 `default_g2_weight` 2.45 绘制 | SnackbarDefaults.CornerRadius     | 否       |
| singleLineCornerRadius | Dp             | 单行布局的圆角半径；`Dp.Unspecified` 按高度一半取圆角（COUI 17 软胶囊） | SnackbarDefaults.SingleLineCornerRadius | 否 |
| maxWidth               | Dp             | Snackbar 的最大宽度（不含两侧 16.dp 外边距）；`Dp.Unspecified` 取消上限 | SnackbarDefaults.maxWidth() | 否       |
| colors                 | SnackbarColors | Snackbar 的颜色配置      | SnackbarDefaults.snackbarColors() | 否       |
| insideMargin           | PaddingValues  | Snackbar 内容区域的内边距（单行布局中操作按钮位于尾部时，末端内边距收窄为 4.dp） | SnackbarDefaults.InsideMargin | 否       |

### 布局

遵循 COUI 17 `COUISnackBar.adjustLayout`：

- **单行**：图标、消息、操作按钮与关闭图标排在一行，各自垂直居中，外形为胶囊。
- **垂直**：消息换行（或含换行符）或整行超出可用宽度时，消息距顶部 12.dp，操作按钮移到消息下方（间距 10.dp）并贴右，末端边距 4.dp、底部边距 9.dp，圆角降为 `cornerRadius`（16.dp）。
- **操作按钮**：操作文字带水平 12.dp、垂直 4.dp 的内边距作为胶囊按压区（COUI `text_ripple_bg_padding_*`），与消息间隔 16.dp，末端边距 4.dp。
- **宽度**：Snackbar 按内容自适应宽度，上限为 `maxWidth`。默认取当前窗口 COUI 响应式栅格（小边距）的 6 列宽度：紧凑窗口（宽度小于 600.dp）只有 4 列，因此可铺满窗口减去两侧 16.dp；中等窗口（8 列、边距 24.dp、间距 8.dp）与展开窗口（12 列、边距 40.dp、间距 12.dp）上居中显示为 6 列宽。

容器色为 `backgroundTop`（浅色 `#FFFFFF` / 深色 `#333333`），操作文字为 `primaryTextOnPopup`（`#0080FF` / `#5C9DFF`）。阴影以柔和投影近似 COUI `ShadowUtils` 3 级阴影，深色模式更浓（COUI 阴影 alpha 由 40/255 升至 120/255）。

### SnackbarColors 与 SnackbarDefaults

`SnackbarColors` 定义了 Snackbar 使用的颜色集合：

```kotlin
data class SnackbarColors(
    val containerColor: Color,
    val contentColor: Color,
    val actionContentColor: Color,
    val dismissActionContentColor: Color,
)
```

可以通过 `SnackbarDefaults.snackbarColors` 创建颜色配置：

```kotlin
val colors = SnackbarDefaults.snackbarColors(
    containerColor = COUITheme.colorScheme.backgroundTop,
    contentColor = COUITheme.colorScheme.labelPrimary,
    actionContentColor = COUITheme.colorScheme.primaryTextOnPopup,
    dismissActionContentColor = COUITheme.colorScheme.labelTertiary,
)
```

#### 常量

`SnackbarDefaults` 同时提供 `Snackbar` 使用的默认圆角、文字样式与内边距：

| 常量名                 | 类型          | 说明                      | 默认值                                               |
| ---------------------- | ------------- | ------------------------- | ---------------------------------------------------- |
| CornerRadius           | Dp            | 垂直（多行）布局的圆角半径（COUI `couiRoundCornerL`） | 16.dp                    |
| SingleLineCornerRadius | Dp            | 单行布局的圆角半径；未指定 = 高度一半的胶囊 | Dp.Unspecified                     |
| InsideMargin           | PaddingValues | Snackbar 内容的默认内边距 | PaddingValues(horizontal = 16.dp, vertical = 10.dp)  |
| IconSize               | Dp            | 可选前置图标容器的尺寸    | 30.dp                                                |

`SnackbarDefaults.textStyle()` 返回消息与操作按钮使用的默认文字样式（14sp、Medium 字重），`SnackbarDefaults.maxWidth()` 返回基于当前窗口栅格的最大宽度。

## SnackbarDuration 与 SnackbarResult

### SnackbarDuration

`SnackbarDuration` 用于控制 Snackbar 显示的时长：

```kotlin
sealed interface SnackbarDuration {
    data object Short : SnackbarDuration
    data object Long : SnackbarDuration
    data object Indefinite : SnackbarDuration
    data class Custom(val durationMillis: Long) : SnackbarDuration
}
```

| 选项        | 说明                         | 显示时长        |
| ----------- | ---------------------------- | --------------- |
| Short       | 短提示                       | 约 4 秒         |
| Long        | 较长提示                     | 约 10 秒        |
| Indefinite  | 一直显示，需手动关闭         | 直到手动关闭    |
| Custom      | 自定义毫秒级时长             | 用户自定义      |

### SnackbarResult

`SnackbarResult` 用于描述 Snackbar 的完成方式：

```kotlin
enum class SnackbarResult {
    Dismissed,
    ActionPerformed,
}
```

## 进阶用法

### 带前置图标的 Snackbar

通过 `SnackbarHost` 的自定义 `content` 插槽，为内置 `Snackbar` 传入图标：

```kotlin
SnackbarHost(state = snackbarHostState) { data ->
    Snackbar(
        data = data,
        icon = {
            Icon(
                imageVector = COUIIcons.Basic.Check,
                contentDescription = null,
                tint = COUITheme.colorScheme.primary
            )
        }
    )
}
```

### 带操作按钮的 Snackbar

```kotlin
val snackbarHostState = remember { SnackbarHostState() }
val scope = rememberCoroutineScope()

TextButton(
    text = "显示带操作的提示",
    onClick = {
        scope.launch {
            val result = snackbarHostState.showSnackbar(
                message = "这条消息包含操作按钮",
                actionLabel = "撤销",
                duration = SnackbarDuration.Short,
            )
            when (result) {
                SnackbarResult.ActionPerformed -> { /* 处理撤销 */ }
                SnackbarResult.Dismissed -> { /* 超时或被关闭 */ }
            }
        }
    },
)
```

### 可关闭且无限时长的 Snackbar

```kotlin
val snackbarHostState = remember { SnackbarHostState() }
val scope = rememberCoroutineScope()

TextButton(
    text = "显示无限时长提示",
    onClick = {
        scope.launch {
            snackbarHostState.showSnackbar(
                message = "无限时长提示，需要手动关闭",
                withDismissAction = true,
                duration = SnackbarDuration.Indefinite,
            )
        }
    },
)

TextButton(
    text = "关闭最早的一条",
    onClick = {
        scope.launch {
            snackbarHostState.oldestSnackbarData()?.dismiss()
        }
    },
)
```

## NotificationSnackbar

`NotificationSnackbar` 是 COUI 17 的通知样式 Snackbar（`COUINotificationSnackBar`）：浮起的卡片，
包含前置图片、标题、可选的副文本、小号操作按钮和右上角的关闭按钮。可以原地显示，也可以通过
`SnackbarHost` 显示以获得宿主的出现 / 消失动画与超时。

```kotlin
import io.github.suqi8.coui.kmp.basic.NotificationSnackbar
import io.github.suqi8.coui.kmp.basic.NotificationSnackbarIconType
```

```kotlin
NotificationSnackbar(
    title = "截图已保存",
    subtitle = "点击分享发送给联系人",
    actionLabel = "分享",
    onActionClick = { /* 分享 */ },
    onDismiss = { visible = false },
    icon = { Image(painter = thumbnail, contentDescription = null) },
)
```

通过宿主显示时，消息作为标题、操作标签作为按钮；按钮执行操作，关闭按钮或滑动将其关闭：

```kotlin
SnackbarHost(state = snackbarHostState, canSwipeToDismiss = false) { data ->
    NotificationSnackbar(data, subtitle = "点击分享发送给联系人", icon = { /* ... */ })
}
```

### NotificationSnackbar 属性

| 属性名            | 类型                           | 说明                                        | 默认值                                           | 是否必须 |
| -------------- | ---------------------------- | ----------------------------------------- | --------------------------------------------- | ---- |
| title          | String                       | 标题（COUI content text），最多两行                | -                                             | 是    |
| actionLabel    | String                       | 小号操作按钮的文字；COUI 要求必填（`COUINotificationSnackBar.make`）。`SnackbarData` 重载要求设置 `actionLabel` | - | 是 |
| onActionClick  | () -> Unit                   | 点击操作按钮时回调                                 | -                                             | 是    |
| onDismiss      | () -> Unit                   | 点击关闭按钮或滑走卡片时回调                            | -                                             | 是    |
| modifier       | Modifier                     | 应用于卡片的修饰符                                 | Modifier                                      | 否    |
| subtitle       | String?                      | 副文本，最多两行                                  | null                                          | 否    |
| icon           | (@Composable () -> Unit)?    | 前置图片，放在 `iconType` 给出的区域内                  | null                                          | 否    |
| iconType       | NotificationSnackbarIconType | `Square` 44dp、`Vertical` 36 x 48dp 或 `Horizontal` 58 x 44dp | NotificationSnackbarIconType.Square | 否    |
| swipeToDismiss | Boolean                      | 是否可滑动关闭                                   | true                                          | 否    |
| maxWidth       | Dp                           | 最大宽度，不含两侧 16dp 外边距                        | SnackbarDefaults.maxWidth()                   | 否    |
| cornerRadius   | Dp                           | 卡片圆角（couiRoundCornerL）                     | SnackbarDefaults.CornerRadius                 | 否    |
| colors         | NotificationSnackbarColors   | 卡片颜色                                      | SnackbarDefaults.notificationSnackbarColors() | 否    |

`SnackbarData` 重载以 `data` 取代 `title`、`actionLabel`、`onActionClick` 与 `onDismiss`。

### NotificationSnackbarColors

| 属性名            | 类型               | 说明                              | 默认值                                           |
| -------------- | ---------------- | ------------------------------- | --------------------------------------------- |
| containerColor | Color            | 卡片填充（couiColorBackgroundTop）     | COUITheme.colorScheme.backgroundTop           |
| titleColor     | Color            | 标题颜色（couiColorPrimaryNeutral）    | COUITheme.colorScheme.labelPrimary      |
| subtitleColor  | Color            | 副文本颜色（couiColorSecondNeutral）    | COUITheme.colorScheme.labelSecondaryVariant |
| closeIconColor | Color            | 关闭图标着色（couiColorLabelTertiary）   | COUITheme.colorScheme.labelTertiary |
| actionColors   | TextButtonColors | 操作按钮颜色                          | ButtonDefaults.textButtonColors()             |

### 布局与滑动

- **布局**：图片距起始、顶部与底部各 16dp，与文字间隔 8dp；标题 14sp 中粗，副文本 12sp、位于标题下方 2dp；小号按钮（内边距 12dp x 4dp，最宽 120dp、最多两行）垂直居中；20dp 关闭按钮距顶部与末端各 6dp。
- **滑动**：拖动超过触摸阈值（touch slop）后决定方向（COUI 在移动第一个像素时就决定，这里保留阈值，以免轻微移动的点按失效、抢走可滚动父布局的手势）。横向时卡片跟手，超过自身宽度的八分之一后以每毫秒 1.75px 的速度沿 COUI move-ease 曲线滑出屏幕并回调 `onDismiss`，否则弹回（response 0.35、bounce 0）。向下滑动超过宽度八分之一同样关闭；拖动开始后向上最多偏离原位宽度的八分之一。一开始就向上的拖动交给父布局处理。
