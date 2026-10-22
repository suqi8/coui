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
    colors: SnackbarColors = SnackbarDefaults.snackbarColors(),
    insideMargin: PaddingValues = SnackbarDefaults.InsideMargin,
)
```

| 参数名                 | 类型           | 说明                     | 默认值                            | 是否必须 |
| ---------------------- | -------------- | ------------------------ | --------------------------------- | -------- |
| data                   | SnackbarData   | 包含消息与操作信息的数据 | -                                 | 是       |
| modifier               | Modifier       | 应用于 Snackbar 容器的修饰符 | Modifier                      | 否       |
| icon                   | (@Composable () -> Unit)? | 可选前置图标，布局在 30.dp 容器内（通过 `SnackbarHost` 的 `content` 插槽传入） | null | 否       |
| cornerRadius           | Dp             | 垂直（多行）布局的圆角半径 | SnackbarDefaults.CornerRadius     | 否       |
| singleLineCornerRadius | Dp             | 单行布局的圆角半径；`Dp.Unspecified` 按高度一半取圆角（胶囊） | SnackbarDefaults.SingleLineCornerRadius | 否 |
| colors                 | SnackbarColors | Snackbar 的颜色配置      | SnackbarDefaults.snackbarColors() | 否       |
| insideMargin           | PaddingValues  | Snackbar 内容区域的内边距（单行布局中操作按钮位于尾部时，末端内边距收窄为 4.dp） | SnackbarDefaults.InsideMargin | 否       |

### 布局

遵循 COUI 17 `COUISnackBar.adjustLayout`：

- **单行**：图标、消息、操作按钮与关闭图标排在一行，各自垂直居中，外形为胶囊。
- **垂直**：消息换行（或含换行符）或整行超出可用宽度时，消息距顶部 12.dp，操作按钮移到消息下方（间距 10.dp）并贴右，末端边距 4.dp、底部边距 9.dp，圆角降为 `cornerRadius`（16.dp）。

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
    contentColor = COUITheme.colorScheme.onSurfaceContainer,
    actionContentColor = COUITheme.colorScheme.primaryTextOnPopup,
    dismissActionContentColor = COUITheme.colorScheme.onSurfaceVariantActions,
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

`SnackbarDefaults.textStyle()` 返回消息与操作按钮使用的默认文字样式（14sp、Medium 字重）。

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

