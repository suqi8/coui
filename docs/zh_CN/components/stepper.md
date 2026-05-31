# Stepper

步进器让用户通过减号和加号按钮以固定步长调整整数值,对应 ColorOS 的 COUIStepperView(32dp 圆形按钮,带 OPLUS 步进马达震动)。

## 引入

```kotlin
import top.yukonga.miuix.kmp.basic.Stepper
import top.yukonga.miuix.kmp.basic.StepperDefaults
```

## 基本用法

```kotlin
var value by remember { mutableIntStateOf(2) }

Stepper(
    value = value,
    onValueChange = { value = it },
    minValue = 0,
    maxValue = 10,
)
```

## 属性

### Stepper

| 属性          | 类型            | 说明                       | 默认值                       | 必需 |
| ------------- | --------------- | -------------------------- | ---------------------------- | ---- |
| value         | Int             | 当前值                     | -                            | 是   |
| onValueChange | (Int) -> Unit   | 步进时返回新值的回调       | -                            | 是   |
| modifier      | Modifier        | 应用于步进器的修饰符       | Modifier                     | 否   |
| minValue      | Int             | 可达到的最小值             | Int.MIN_VALUE                | 否   |
| maxValue      | Int             | 可达到的最大值             | Int.MAX_VALUE                | 否   |
| step          | Int             | 每次按压的增减量           | 1                            | 否   |
| enabled       | Boolean         | 步进器是否启用             | true                         | 否   |
| colors        | StepperColors   | 颜色配置                   | StepperDefaults.stepperColors() | 否 |

### StepperDefaults

| 常量              | 类型 | 默认值 |
| ----------------- | ---- | ------ |
| ButtonSize        | Dp   | 32.dp  |
| GlyphSize         | Dp   | 18.dp  |
| Spacing           | Dp   | 4.dp   |
| IndicatorMinWidth | Dp   | 40.dp  |

### `stepperColors()` 工厂

| 参数                 | 类型  | 默认值                                    |
| -------------------- | ----- | ----------------------------------------- |
| buttonColor          | Color | MiuixTheme.colorScheme.secondaryContainer |
| contentColor         | Color | MiuixTheme.colorScheme.onSurface          |
| disabledContentColor | Color | MiuixTheme.colorScheme.disabledOnSurface  |

## 行为

- 按 − 或 + 会按 `step` 改变数值,并钳制到 `[minValue, maxValue]`。
- 当数值到达边界时,对应按钮会自动禁用。
- 每次成功步进会触发 COUI 步进震动(ColorOS 上为 OPLUS 常量 308,其它平台回退到最接近的标准震动)。
