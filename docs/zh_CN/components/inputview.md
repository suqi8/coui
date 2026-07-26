# InputView

ColorOS「卡片输入」：白卡（12dp 圆角）内包含可选的 16sp 中等字重标题与一个完全无装饰的输入框，计数 / 清除 / 密码按钮位于输入框尾部，10sp 错误提示位于卡片下方。对应 COUICardSingleInputView（单行）与 COUICardMultiInputView（多行，计数位于卡片右下角）。错误提示以 COUI ease 曲线 217ms 淡入 / 283ms 淡出，显示期间输入框播放 COUI 错误抖动。

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.InputView
import io.github.suqi8.coui.kmp.basic.InputViewDefaults
```

## 基本用法

```kotlin
var nickname by remember { mutableStateOf("") }

InputView(
    value = nickname,
    onValueChange = { nickname = it },
    title = "Nickname",
    label = "Enter your nickname",
    showCount = true,
    maxCount = 10,
    showClearButton = true,
    errorMessage = if (nickname.contains(' ')) "Nickname cannot contain spaces" else "",
)
```

## 多行卡片

设置 `singleLine = false` 使用 COUICardMultiInputView 形态：计数移动到卡片右下角（12sp），达到上限时以动画切换为错误色：

```kotlin
var signature by remember { mutableStateOf("") }

InputView(
    value = signature,
    onValueChange = { signature = it },
    label = "Signature",
    showCount = true,
    maxCount = 100,
    singleLine = false,
)
```

## 组件状态

### 禁用状态

```kotlin
InputView(
    value = "Read only value",
    onValueChange = {},
    title = "Nickname",
    enabled = false,
)
```

## 属性

### InputView

| 属性                 | 类型                      | 说明                                       | 默认值                                   | 是否必须 |
| -------------------- | ------------------------- | ------------------------------------------ | ---------------------------------------- | -------- |
| value                | String                    | 输入框的文本值                             | -                                        | 是       |
| onValueChange        | (String) -> Unit          | 文本变化时的回调函数                       | -                                        | 是       |
| modifier             | Modifier                  | 应用于组件的修饰符                         | Modifier                                 | 否       |
| title                | String                    | 卡片内标题，为空时隐藏                     | ""                                       | 否       |
| label                | String                    | 内部输入框的标签（占位符）                 | ""                                       | 否       |
| enabled              | Boolean                   | 组件是否可用                               | true                                     | 否       |
| readOnly             | Boolean                   | 输入框是否只读                             | false                                    | 否       |
| showCount            | Boolean                   | 显示「当前/上限」计数（需 maxCount > 0）   | false                                    | 否       |
| maxCount             | Int                       | 最大字符数，0 表示不限                     | 0                                        | 否       |
| showClearButton      | Boolean                   | 聚焦且非空时显示清除按钮                   | false                                    | 否       |
| showPasswordToggle   | Boolean                   | 显示密码可见切换按钮（隐藏时掩码显示）     | false                                    | 否       |
| errorMessage         | String                    | 卡片下方的错误提示，为空时隐藏             | ""                                       | 否       |
| cornerRadius         | Dp                        | 卡片圆角半径                               | InputViewDefaults.CornerRadius           | 否       |
| colors               | InputViewColors           | 卡片、标题、计数与错误提示的颜色           | InputViewDefaults.inputViewColors()      | 否       |
| textFieldColors      | TextFieldColors           | 内部输入框的颜色                           | TextFieldDefaults.textFieldColors()      | 否       |
| textStyle            | TextStyle                 | 输入文本样式                               | TextFieldDefaults.textStyle(TextFieldMode.None) | 否 |
| keyboardOptions      | KeyboardOptions           | 键盘选项配置                               | KeyboardOptions.Default                  | 否       |
| keyboardActions      | KeyboardActions           | 键盘操作配置                               | KeyboardActions.Default                  | 否       |
| singleLine           | Boolean                   | 单行卡片（true）或多行卡片（false）        | true                                     | 否       |
| maxLines             | Int                       | 最大行数                                   | singleLine 为 true 时 1，否则 5          | 否       |
| visualTransformation | VisualTransformation      | 视觉转换器                                 | VisualTransformation.None                | 否       |
| leadingIcon          | @Composable (() -> Unit)? | 前置图标                                   | null                                     | 否       |
| trailingIcon         | @Composable (() -> Unit)? | 后置图标，位于内置按钮之后                 | null                                     | 否       |
| interactionSource    | MutableInteractionSource? | 交互源                                     | null                                     | 否       |

### InputViewDefaults

| 常量                        | 类型     | 说明                                         | 默认值 |
| --------------------------- | -------- | -------------------------------------------- | ------ |
| CornerRadius                | Dp       | 卡片圆角半径（couiRoundCornerM）             | 12.dp  |
| ContentPadding              | Dp       | 卡片内容水平内边距                           | 16.dp  |
| TitlePaddingTop             | Dp       | 标题顶部内边距                               | 12.dp  |
| TitlePaddingBottom          | Dp       | 标题底部内边距                               | 4.dp   |
| TitleMinHeight              | Dp       | 标题最小高度                                 | 22.dp  |
| FieldPaddingVertical        | Dp       | 无标题时输入区垂直内边距                     | 15.dp  |
| FieldPaddingBottomWithTitle | Dp       | 有标题时输入区底部内边距（顶部为 0）         | 12.dp  |
| MultiFieldPadding           | Dp       | 多行卡片输入区垂直内边距                     | 13.dp  |
| MultiCountSpacing           | Dp       | 多行输入区与计数之间的间距                   | 4.dp   |
| MultiCountMarginBottom      | Dp       | 多行计数底部外边距                           | 12.dp  |
| MultiCountFontSize          | TextUnit | 多行计数字号                                 | 12.sp  |
| CountFontSize               | TextUnit | 单行行内计数字号                             | 10.sp  |
| ErrorFontSize               | TextUnit | 错误提示字号                                 | 10.sp  |
| ErrorPaddingTop             | Dp       | 错误提示顶部内边距                           | 4.dp   |
| MaxLines                    | Int      | 默认最大行数                                 | 5      |

`titleTextStyle()` 返回默认标题样式（16sp、中等字重，COUI couiTextAppearanceHeadline6）。

### `inputViewColors()` 工厂

| 参数               | 类型  | 默认值                                          |
| ------------------ | ----- | ----------------------------------------------- |
| cardColor          | Color | COUITheme.colorScheme.surfaceContainer         |
| titleColor         | Color | COUITheme.colorScheme.onSurface                |
| disabledTitleColor | Color | COUITheme.colorScheme.disabledOnSurface        |
| countColor         | Color | COUITheme.colorScheme.onSurfaceVariantActions  |
| errorColor         | Color | COUITheme.colorScheme.error                    |

## 行为

- 内部输入框完全无装饰（`TextFieldMode.None`，COUIEditText MODE_BACKGROUND_NO_LINE）——无下划线也无描边，与 ColorOS 设置的卡片输入一致。
- `maxCount > 0` 时输入被硬性限制在上限内；计数（如显示）为 `当前/上限`，达到上限时以 250ms 动画切换为错误色（同 COUICardMultiInputView）。
- 设置非空 `errorMessage` 时在卡片下方显示提示（217ms 淡入 / 283ms 淡出，COUI ease 0.33, 0, 0.67, 1），同时输入框播放 COUI 错误抖动。淡出过程中保留最后一条非空消息。
- 输入区内边距对齐 COUICardSingleInputView：无标题上下各 15dp；有标题时顶部 0dp、底部 12dp；多行卡片为 13dp。

## 进阶用法

### 带验证的密码输入

```kotlin
var password by remember { mutableStateOf("") }

InputView(
    value = password,
    onValueChange = { password = it },
    title = "Password",
    label = "At least 8 characters",
    showPasswordToggle = true,
    errorMessage = if (password.isNotEmpty() && password.length < 8) "Password is too short" else "",
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
)
```
