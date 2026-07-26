# TextField

`TextField` 是 Miuix 中的基础输入组件，用于接收用户的文本输入，视觉对齐 ColorOS COUIEditText。默认呈现 ColorOS 设置对话框中的输入形态：16sp 裸文本 + 细下划线，聚焦时主题色 1dp 线条从起始边展开，标签作为普通占位符使用。同时提供纯描边圆角矩形、完全无装饰（卡片内）两种形态，以及可选的浮动标签、错误抖动、字数统计、清除按钮与密码切换。

<div style="position: relative; height: 340px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=textField" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.TextField
import io.github.suqi8.coui.kmp.basic.TextFieldMode
```

## 基本用法

TextField 组件可以用于获取用户输入：

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = "用户名"
)
```

::: info 信息
此 TextFiled 组件现在也支持最新基于状态的版本，具体请参考 [State-based](https://developer.android.com/develop/ui/compose/text/user-input?textfield=state-based&hl=zh-cn) 文档。
:::

## 输入框类型

### 带标签输入框（占位符）

默认情况下（`useLabelAsPlaceholder = true`，对应 ColorOS 全部输入框使用的 HintDisable 样式），标签是普通占位符：输入框为空时显示，输入文本后消失：

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = "邮箱地址"
)
```

### 浮动标签

设置 `useLabelAsPlaceholder = false` 可启用 COUI HintAnim 浮动标签：输入框获得焦点或有内容时，标签缩小到 10sp 并上浮（200ms，COUI move ease 曲线）：

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = "请输入内容",
    useLabelAsPlaceholder = false
)
```

## 组件状态

### 禁用状态

```kotlin
var text by remember { mutableStateOf("") }
TextField(
    value = text,
    onValueChange = { text = it },
    label = "禁用输入框",
    enabled = false
)
```

### 只读状态

```kotlin
var text by remember { mutableStateOf("这是只读内容") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = "只读输入框",
    readOnly = true
)
```

## 背景模式

`TextField` 通过 `backgroundMode` 支持 COUIEditText 的三种背景模式：

- `TextFieldMode.Line`（默认）：无填充；0.33dp 细下划线，聚焦时 1dp 主题色线条从起始边展开——即 ColorOS 设置的对话框 / 底部面板输入形态
- `TextFieldMode.Rectangle`：纯描边圆角矩形（10dp 圆角，无填充）；未聚焦 0.33dp 细描边，聚焦 1dp 主题色描边；默认文本加粗
- `TextFieldMode.None`：无任何背景装饰——裸文本，即白卡输入使用的形态（见 `InputView`）

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = "矩形样式",
    backgroundMode = TextFieldMode.Rectangle
)
```

### 仅显示聚焦线

`Line` 模式下设置 `justShowFocusLine = true` 可隐藏静息下划线，只保留聚焦时展开的线条，对应 ColorOS 设置卡片内偏好输入（COUIInputPreference `couiJustShowFocusLine`，真机默认开启）。把输入框放进 `Card` 即为设置里的完整观感：

```kotlin
var text by remember { mutableStateOf("") }

Card {
    TextField(
        value = text,
        onValueChange = { text = it },
        label = "设备名称",
        justShowFocusLine = true,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}
```

## 错误状态

设置 `isError = true` 会将描边 / 下划线与标签染成错误色，并播放一次水平抖动动画：

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = "仅限数字",
    isError = text.isNotEmpty() && !text.all { it.isDigit() }
)
```

## 字数统计

设置 `maxCount` 会在输入框尾部显示「当前/上限」计数，并在超出上限时截断输入。达到上限后计数变红：

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = "最多 10 个字符",
    maxCount = 10
)
```

## 清除按钮

设置 `showClearButton = true` 会在输入框聚焦且非空时显示清除（快速删除）按钮，点按后清空全部文本：

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = "快速删除",
    showClearButton = true
)
```

## 密码可见切换

设置 `showPasswordToggle = true` 会显示一个眼睛按钮用于切换密码可见性。隐藏时文本以圆点掩码显示：

```kotlin
var password by remember { mutableStateOf("") }

TextField(
    value = password,
    onValueChange = { password = it },
    label = "密码",
    showPasswordToggle = true,
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
)
```

## 属性

### TextField 属性

| 属性名                | 类型                                         | 说明                   | 默认值                                               | 是否必须 |
| --------------------- | -------------------------------------------- | ---------------------- | ---------------------------------------------------- | -------- |
| value                 | String 或 TextFieldValue                     | 输入框的文本值         | -                                                    | 是       |
| onValueChange         | (String) -> Unit 或 (TextFieldValue) -> Unit | 文本变化时的回调函数   | -                                                    | 是       |
| modifier              | Modifier                                     | 应用于输入框的修饰符   | Modifier                                             | 否       |
| backgroundMode        | TextFieldMode                                | 背景装饰模式           | TextFieldMode.Line                                   | 否       |
| insideMargin          | DpSize                                       | 输入框内部边距         | TextFieldDefaults.insideMargin(backgroundMode)       | 否       |
| colors                | TextFieldColors                              | 输入框使用的颜色       | TextFieldDefaults.textFieldColors()                  | 否       |
| cornerRadius          | Dp                                           | 圆角半径（Rectangle 模式） | TextFieldDefaults.CornerRadius                    | 否       |
| label                 | String                                       | 标签 / 占位符文本      | ""                                                   | 否       |
| useLabelAsPlaceholder | Boolean                                      | 普通占位符（true）或浮动标签（false） | true                                  | 否       |
| justShowFocusLine     | Boolean                                      | Line 模式：隐藏静息下划线 | false                                             | 否       |
| enabled               | Boolean                                      | 输入框是否可用         | true                                                 | 否       |
| readOnly              | Boolean                                      | 输入框是否只读         | false                                                | 否       |
| isError               | Boolean                                      | 错误状态（红色染色+抖动） | false                                             | 否       |
| maxCount              | Int?                                         | 最大字符数；显示计数   | null                                                 | 否       |
| showClearButton       | Boolean                                      | 聚焦时显示清除按钮     | false                                                | 否       |
| showPasswordToggle    | Boolean                                      | 显示密码可见切换按钮   | false                                                | 否       |
| textStyle             | TextStyle                                    | 输入文本样式           | TextFieldDefaults.textStyle(backgroundMode)          | 否       |
| keyboardOptions       | KeyboardOptions                              | 键盘选项配置           | KeyboardOptions.Default                              | 否       |
| keyboardActions       | KeyboardActions                              | 键盘操作配置           | KeyboardActions.Default                              | 否       |
| leadingIcon           | @Composable (() -> Unit)?                    | 前置图标               | null                                                 | 否       |
| trailingIcon          | @Composable (() -> Unit)?                    | 后置图标               | null                                                 | 否       |
| singleLine            | Boolean                                      | 是否为单行输入         | false                                                | 否       |
| maxLines              | Int                                          | 最大行数               | 如果 singleLine 为 true 则为 1，否则为 Int.MAX_VALUE | 否       |
| minLines              | Int                                          | 最小行数               | 1                                                    | 否       |
| visualTransformation  | VisualTransformation                         | 视觉转换器             | VisualTransformation.None                            | 否       |
| onTextLayout          | (TextLayoutResult) -> Unit                   | 文本布局变化回调       | {}                                                   | 否       |
| interactionSource     | MutableInteractionSource?                    | 交互源                 | null                                                 | 否       |
| cursorBrush           | Brush                                        | 光标画刷               | SolidColor(colors.borderColor)                       | 否       |

### TextField（state-based）属性

| 属性名                | 类型                                                | 说明                           | 默认值                                        | 是否必须 |
| --------------------- | --------------------------------------------------- | ------------------------------ | --------------------------------------------- | -------- |
| state                 | TextFieldState                                      | 保存文本与选择的状态对象       | -                                             | 是       |
| modifier              | Modifier                                            | 应用于输入框的修饰符           | Modifier                                      | 否       |
| backgroundMode        | TextFieldMode                                       | 背景装饰模式                   | TextFieldMode.Line                            | 否       |
| insideMargin          | DpSize                                              | 输入框内部边距                 | TextFieldDefaults.insideMargin(backgroundMode) | 否       |
| colors                | TextFieldColors                                     | 输入框使用的颜色               | TextFieldDefaults.textFieldColors()           | 否       |
| cornerRadius          | Dp                                                  | 圆角半径（Rectangle 模式）     | TextFieldDefaults.CornerRadius                | 否       |
| label                 | String                                              | 标签 / 占位符文本              | ""                                            | 否       |
| useLabelAsPlaceholder | Boolean                                             | 普通占位符（true）或浮动标签（false） | true                                   | 否       |
| justShowFocusLine     | Boolean                                             | Line 模式：隐藏静息下划线      | false                                         | 否       |
| enabled               | Boolean                                             | 输入框是否可用                 | true                                          | 否       |
| readOnly              | Boolean                                             | 输入框是否只读                 | false                                         | 否       |
| isError               | Boolean                                             | 错误状态（红色染色+抖动）      | false                                         | 否       |
| maxCount              | Int?                                                | 最大字符数；显示计数           | null                                          | 否       |
| showClearButton       | Boolean                                             | 聚焦时显示清除按钮             | false                                         | 否       |
| showPasswordToggle    | Boolean                                             | 显示密码可见切换按钮           | false                                         | 否       |
| inputTransformation   | InputTransformation?                                | 输入变换器                     | null                                          | 否       |
| textStyle             | TextStyle                                           | 输入文本样式                   | TextFieldDefaults.textStyle(backgroundMode)   | 否       |
| keyboardOptions       | KeyboardOptions                                     | 键盘选项配置                   | KeyboardOptions.Default                       | 否       |
| onKeyboardAction      | KeyboardActionHandler?                              | 键盘动作处理器                 | null                                          | 否       |
| lineLimits            | TextFieldLineLimits                                 | 行数限制                       | TextFieldLineLimits.Default                   | 否       |
| leadingIcon           | @Composable (() -> Unit)?                           | 前置图标                       | null                                          | 否       |
| trailingIcon          | @Composable (() -> Unit)?                           | 后置图标                       | null                                          | 否       |
| onTextLayout          | Density.(getResult: () -> TextLayoutResult?) -> Unit | 文本布局回调（带 Density 接收） | null                                          | 否       |
| interactionSource     | MutableInteractionSource?                           | 交互源                         | null                                          | 否       |
| cursorBrush           | Brush                                               | 光标画刷                       | SolidColor(colors.borderColor)                | 否       |
| outputTransformation  | OutputTransformation?                               | 输出变换器                     | null                                          | 否       |
| scrollState           | ScrollState                                         | 滚动状态                       | rememberScrollState()                         | 否       |

### TextFieldDefaults 对象

TextFieldDefaults 对象提供了 TextField 组件的默认值。

#### 常量

| 常量名           | 类型     | 说明                       | 默认值                |
| ---------------- | -------- | -------------------------- | --------------------- |
| CornerRadius     | Dp       | 输入框圆角半径             | 10.dp                 |
| InsideMargin     | DpSize   | Rectangle 模式内部边距     | DpSize(16.dp, 12.dp)  |
| LineInsideMargin | DpSize   | Line 模式内部边距          | DpSize(0.dp, 15.dp)   |
| NoneInsideMargin | DpSize   | None 模式内部边距          | DpSize(0.dp, 9.dp)    |
| CounterFontSize  | TextUnit | 字数统计文字字号           | 10.sp                 |

#### `insideMargin()` 函数

`TextFieldDefaults.insideMargin(mode: TextFieldMode): DpSize` 返回指定背景模式的默认内部边距。

#### `textStyle()` 函数

`TextFieldDefaults.textStyle(mode: TextFieldMode): TextStyle` 返回 COUI 默认输入文本样式：16sp 常规字重，`Rectangle` 模式下加粗。

#### `textFieldColors()` 工厂

构造 [TextFieldColors] 实例。按需覆盖任意子集，未指定的参数回退到 Miuix 主题默认值。

| 参数             | 类型  | 默认值                                            |
| ---------------- | ----- | ------------------------------------------------- |
| backgroundColor  | Color | Color.Transparent（COUI 矩形模式仅描边无填充）    |
| labelColor       | Color | COUITheme.colorScheme.onSurfaceSecondary         |
| borderColor      | Color | COUITheme.colorScheme.primary                    |
| unfocusedBorderColor | Color | COUITheme.colorScheme.dividerLine            |
| errorColor       | Color | COUITheme.colorScheme.error                      |
| counterColor     | Color | COUITheme.colorScheme.onSurfaceContainerHigh     |
| iconColor        | Color | COUITheme.colorScheme.onSurfaceSecondary         |
| disabledTextColor | Color | COUITheme.colorScheme.disabledOnSurface         |

## 进阶用法

### 带图标输入框

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = "搜索",
    leadingIcon = {
        Icon(
            imageVector = COUIIcons.Search,
            contentDescription = "搜索图标",
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
)
```

### 密码输入框

```kotlin
var password by remember { mutableStateOf("") }
var passwordVisible by remember { mutableStateOf(false) }

TextField(
    value = password,
    onValueChange = { password = it },
    label = "密码",
    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
    trailingIcon = {
        IconButton(
            onClick = { passwordVisible = !passwordVisible },
            modifier = Modifier.padding(end = 12.dp)
        ) {
            Icon(
                imageVector = COUIIcons.Rename,
                tint = if (passwordVisible) COUITheme.colorScheme.primary else COUITheme.colorScheme.onSurfaceSecondary,
                contentDescription = if (passwordVisible) "隐藏密码" else "显示密码"
            )
        }
    }
)
```

### 带验证的输入框

```kotlin
var email by remember { mutableStateOf("") }
var isError by remember { mutableStateOf(false) }
val errorColor = Color.Red.copy(0.3f)
val emailPattern = remember { Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+") }

Column {
    TextField(
        value = email,
        onValueChange = {
            email = it
            isError = email.isNotEmpty() && !emailPattern.matches(email)
        },
        label = "电子邮箱",
        colors = TextFieldDefaults.textFieldColors(
            labelColor = if (isError) errorColor else COUITheme.colorScheme.onSurfaceSecondary,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
    )
    if (isError) {
        Text(
            text = "请输入有效的邮箱地址",
            color = errorColor,
            style = COUITheme.textStyles.body2,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }
}
```

### 自定义样式

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = "自定义输入框",
    cornerRadius = 8.dp,
    colors = TextFieldDefaults.textFieldColors(
        backgroundColor = COUITheme.colorScheme.primary.copy(alpha = 0.1f),
    ),
    textStyle = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        color = COUITheme.colorScheme.primary
    )
)
```

### 使用 TextFieldValue

当需要更细致地控制文本选择和光标位置时：

```kotlin
var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }

TextField(
    value = textFieldValue,
    onValueChange = { textFieldValue = it },
    label = "高级输入控制",
    // TextFieldValue 提供了对文本、选择范围和光标位置的控制
)
```
