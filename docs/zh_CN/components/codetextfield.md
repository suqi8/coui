# CodeTextField

验证码分格输入组件,对应 ColorOS 的 COUICodeInputView:42x46dp 卡片格、8dp 圆角、活动格 1.6dp 主色描边、数字以 0.6 -> 1.0 缩放 + 淡入弹出,并支持以圆点代替数字的安全模式。点击任意位置即可聚焦隐藏输入框;输入从左到右填格,粘贴会把验证码分发到各格,退格清除最后一格。聚焦时活动空格内显示闪烁光标。

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.CodeTextField
import io.github.suqi8.coui.kmp.basic.CodeTextFieldDefaults
```

## 基本用法

```kotlin
var code by remember { mutableStateOf("") }

CodeTextField(
    value = code,
    onValueChange = { code = it.filter(Char::isDigit) },
    onComplete = { submit(it) },
    modifier = Modifier.fillMaxWidth(),
)
```

## 安全模式

```kotlin
var pin by remember { mutableStateOf("") }

CodeTextField(
    value = pin,
    onValueChange = { pin = it.filter(Char::isDigit) },
    cellCount = 4,
    security = true,
    modifier = Modifier.fillMaxWidth(),
)
```

## 组件状态

### 禁用状态

禁用后组件仍显示当前验证码,但无法聚焦或编辑,光标与活动格描边也会隐藏:

```kotlin
CodeTextField(
    value = code,
    onValueChange = { code = it },
    enabled = false,
    modifier = Modifier.fillMaxWidth(),
)
```

## 属性

### CodeTextField

| 属性              | 类型                      | 说明                                   | 默认值                                              | 必需 |
| ----------------- | ------------------------- | -------------------------------------- | --------------------------------------------------- | ---- |
| value             | String                    | 各格显示的验证码                       | -                                                   | 是   |
| onValueChange     | (String) -> Unit          | 验证码变化时返回净化后文本的回调       | -                                                   | 是   |
| modifier          | Modifier                  | 应用于组件的修饰符                     | Modifier                                            | 否   |
| cellCount         | Int                       | 验证码格数                             | CodeTextFieldDefaults.CellCount (6)                 | 否   |
| enabled           | Boolean                   | 组件是否启用                           | true                                                | 否   |
| security          | Boolean                   | 是否以圆点代替数字显示                 | false                                               | 否   |
| cellSize          | DpSize                    | 自适应缩放前单格的尺寸                 | CodeTextFieldDefaults.CellSize                      | 否   |
| cellCornerRadius  | Dp                        | 单格的圆角                             | CodeTextFieldDefaults.CellCornerRadius              | 否   |
| colors            | CodeTextFieldColors       | 颜色配置                               | CodeTextFieldDefaults.codeTextFieldColors()         | 否   |
| textStyle         | TextStyle                 | 格内数字的文本样式                     | CodeTextFieldDefaults.textStyle()                   | 否   |
| keyboardOptions   | KeyboardOptions           | 隐藏输入框的键盘选项                   | KeyboardOptions(keyboardType = KeyboardType.Number) | 否   |
| onComplete        | ((String) -> Unit)?       | 所有格填满时返回完整验证码的回调       | null                                                | 否   |
| interactionSource | MutableInteractionSource? | 交互源                                 | null                                                | 否   |

### CodeTextFieldDefaults

| 常量                 | 类型   | 说明                             | 默认值               |
| -------------------- | ------ | -------------------------------- | -------------------- |
| CellCount            | Int    | 默认格数                         | 6                    |
| CellSize             | DpSize | 单格尺寸                         | DpSize(42.dp, 46.dp) |
| CellCornerRadius     | Dp     | 单格圆角                         | 8.dp                 |
| MinCellSpacing       | Dp     | 相邻格之间的最小间距             | 4.dp                 |
| MaxCellSpacing       | Dp     | 相邻格之间的最大间距             | 16.dp                |
| CellStrokeWidth      | Dp     | 活动格描边宽度                   | 1.6.dp               |
| SecurityCircleRadius | Dp     | 安全模式圆点半径                 | 5.dp                 |
| CursorWidth          | Dp     | 闪烁光标宽度                     | 2.dp                 |
| ReferenceWidth       | Dp     | 触发缩放的参考宽度               | 360.dp               |

#### 方法

| 方法名                | 类型                | 说明                                                                              |
| --------------------- | ------------------- | --------------------------------------------------------------------------------- |
| textStyle()           | TextStyle           | 默认数字样式(COUITheme.textStyles.title2 调整为 30sp,coui_code_input_cell_text_size) |
| codeTextFieldColors() | CodeTextFieldColors | 创建验证码输入框的颜色配置                                                        |

### `codeTextFieldColors()` 工厂

| 参数                | 类型  | 默认值                                          |
| ------------------- | ----- | ----------------------------------------------- |
| cellBackgroundColor | Color | COUITheme.colorScheme.surfaceContainer         |
| textColor           | Color | COUITheme.colorScheme.onSurface                |
| focusedStrokeColor  | Color | COUITheme.colorScheme.primary                  |
| securityCircleColor | Color | COUITheme.colorScheme.onSurface(alpha 0.847)   |
| cursorColor         | Color | COUITheme.colorScheme.primary                  |

## 行为

- `onValueChange` 始终返回净化后的验证码:去除空白字符并截断到 `cellCount`。
- 一次插入多个字符(粘贴)会替换整个验证码并分发到各格,对应 COUICodeInputView 的 TextWatcher 行为。
- 活动格(第一个空格,填满时为最后一格)在聚焦时显示 1.6dp 描边;描边以 100ms(延迟 33ms)淡入、100ms 淡出(COUI move 缓动 0.3, 0, 0.1, 1)。
- 数字以 0.6 -> 1.0 缩放 + 100ms 淡入出现,删除时延迟 33ms 淡出;安全模式跳过数字动画,与 COUI 一致。
- 可用宽度低于 360dp 参考宽度时,各格按比例缩小,格间距在 4dp 与 16dp 之间自适应。

## 进阶用法

### 自定义单格样式

```kotlin
CodeTextField(
    value = code,
    onValueChange = { code = it },
    cellCount = 4,
    cellSize = DpSize(48.dp, 52.dp),
    cellCornerRadius = 12.dp,
    colors = CodeTextFieldDefaults.codeTextFieldColors(
        focusedStrokeColor = COUITheme.colorScheme.secondary,
    ),
    modifier = Modifier.fillMaxWidth(),
)
```
