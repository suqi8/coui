# InputView

ColorOS「卡片输入」：白卡（24dp 圆角）内包含可选的 16sp 中等字重标题与一个完全无装饰的 15sp 输入框，计数 / 清除 / 密码按钮位于输入框尾部，10sp 错误提示位于卡片下方。对应 COUICardSingleInputView（单行）与 COUICardMultiInputView（多行，计数位于卡片右下角）。错误提示以 COUI ease 曲线 217ms 淡入 / 283ms 淡出，输入框处于聚焦状态时播放 COUI 错误抖动。

<div style="position: relative; height: 480px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=inputView" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

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

设置 `singleLine = false` 使用 COUICardMultiInputView 形态：输入区固定 152dp 高，与内容多少无关（点按空白处会聚焦输入框）；计数位于卡片右下角（12sp，距卡片底 12dp），紧贴在文字区下方。达到上限时计数以动画切换为错误色，1 秒后恢复：

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
| textFieldColors      | TextFieldColors           | 内部输入框的颜色                           | InputViewDefaults.textFieldColors()      | 否       |
| textStyle            | TextStyle                 | 输入文本样式                               | InputViewDefaults.textStyle()            | 否       |
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
| CornerRadius                | Dp       | 卡片圆角半径（COUIRoundCorner.Normal.XL）    | 24.dp  |
| ContentPadding              | Dp       | 卡片内容水平内边距                           | 16.dp  |
| TitlePaddingTop             | Dp       | 标题顶部内边距                               | 12.dp  |
| TitlePaddingBottom          | Dp       | 标题底部内边距                               | 4.dp   |
| TitleMinHeight              | Dp       | 标题最小高度                                 | 22.dp  |
| FieldPaddingVertical        | Dp       | 无标题时输入区垂直内边距                     | 15.dp  |
| FieldPaddingBottomWithTitle | Dp       | 有标题时输入区底部内边距（顶部为 0）         | 12.dp  |
| MultiFieldPadding           | Dp       | 多行卡片输入区垂直内边距                     | 13.dp  |
| MultiInputHeight            | Dp       | 多行输入区固定高度                           | 152.dp |
| MultiCountMarginBottom      | Dp       | 多行计数底部外边距                           | 12.dp  |
| MultiCountFontSize          | TextUnit | 多行计数字号                                 | 12.sp  |
| CountFontSize               | TextUnit | 单行行内计数字号                             | 10.sp  |
| ErrorFontSize               | TextUnit | 错误提示字号                                 | 10.sp  |
| ErrorPaddingTop             | Dp       | 错误提示顶部内边距                           | 4.dp   |
| MaxLines                    | Int      | 默认最大行数                                 | 5      |

`textStyle()` 返回默认输入文本样式（15sp 常规字重，COUI couiTextBodyL，行距同 `bodyL`）。

`titleTextStyle()` 返回默认标题样式（16sp、中等字重，COUI couiTextAppearanceHeadline6）。

`textFieldColors(labelColor)` 返回内部输入框的默认颜色：即 `TextFieldDefaults.textFieldColors()`，但提示文字使用 `labelTertiary`（couiColorHintNeutral，`#42000000` / `#4DFFFFFF`）——COUI 的 Input 系样式不设提示色，回落到主题提示色，而非 EditText 系样式的 54% 标签色。

### `inputViewColors()` 工厂

| 参数               | 类型  | 默认值                                          |
| ------------------ | ----- | ----------------------------------------------- |
| cardColor          | Color | COUITheme.colorScheme.cardBackground         |
| titleColor         | Color | COUITheme.colorScheme.labelPrimary                |
| disabledTitleColor | Color | COUITheme.colorScheme.disabledNeutral        |
| countColor         | Color | COUITheme.colorScheme.labelTertiary  |
| errorColor         | Color | COUITheme.colorScheme.error                    |

## 行为

- 内部输入框完全无装饰（`TextFieldMode.None`，COUIEditText MODE_BACKGROUND_NO_LINE）——无下划线也无描边，与 ColorOS 设置的卡片输入一致。
- `maxCount > 0` 时输入被硬性限制在上限内；计数（如显示）为 `当前/上限`。单行计数达到上限时立即变为 `couiColorError`（COUIInputView）；多行计数以 250ms 动画切换为错误色并在 1 秒后恢复（COUICardMultiInputView）。
- 设置非空 `errorMessage` 时在卡片下方显示提示（217ms 淡入 / 283ms 淡出，COUI ease 0.33, 0, 0.67, 1），输入框处于聚焦状态时同时播放 COUI 错误抖动。淡出过程中保留最后一条非空消息。
- ColorOS 17 上两种卡片输入都由 COUIInputListSelectedItemLayout 绘制卡片，平滑圆角路径下使用 24dp 卡片列表圆角（coui_round_corner_xl_radius_normal_16_1）。
- 输入区内边距对齐 COUICardSingleInputView：无标题上下各 15dp；有标题时顶部 0dp、底部 12dp。多行卡片在固定 152dp 的输入区内顶部留 13dp，有计数时文字区直接止于计数上沿（无计数时为 13dp + 12dp）。

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
