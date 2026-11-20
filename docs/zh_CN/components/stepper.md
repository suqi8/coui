# Stepper

步进器让用户通过减号和加号按钮以固定步长调整整数值,对应 ColorOS 17 的 COUIStepperView:扁平的 32dp 圆形按钮,带按压蒙层和按压缩放,支持长按连续步进,并带 OPLUS 步进马达震动。

<div style="position: relative; height: 240px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=stepper" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.Stepper
import io.github.suqi8.coui.kmp.basic.StepperDefaults
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
| minValue      | Int             | 可达到的最小值             | StepperDefaults.MinValue(-999) | 否 |
| maxValue      | Int             | 可达到的最大值             | StepperDefaults.MaxValue(9999) | 否 |
| step          | Int             | 每次步进的增减量           | 1                            | 否   |
| enabled       | Boolean         | 步进器是否启用             | true                         | 否   |
| colors        | StepperColors   | 颜色配置                   | StepperDefaults.stepperColors() | 否 |

### StepperDefaults

| 常量              | 类型 | 默认值  |
| ----------------- | ---- | ------- |
| ButtonSize        | Dp   | 32.dp   |
| GlyphSize         | Dp   | 15.2.dp |
| GlyphStrokeWidth  | Dp   | 1.6.dp  |
| Spacing           | Dp   | 12.dp   |
| IndicatorMinWidth | Dp   | 44.dp   |
| MinValue          | Int  | -999    |
| MaxValue          | Int  | 9999    |
| RepeatIntervalMillis | Long | 100L |

### `stepperColors()` 工厂

| 参数                 | 类型  | 默认值                                    |
| -------------------- | ----- | ----------------------------------------- |
| buttonColor          | Color | COUITheme.colorScheme.dialogButton(couiColorContainer8) |
| contentColor         | Color | COUITheme.colorScheme.labelPrimary          |
| disabledContentColor | Color | COUITheme.colorScheme.divider(couiColorDivider) |

## 行为

- 点按 − 或 + 会在抬起手指时按 `step` 改变数值,并钳制到 `[minValue, maxValue]`。
- 按住按钮时,超过长按时长后先步进一次,之后每 100ms 步进一次,直到抬起手指或到达边界,不会加速(`LongPressProxy`)。按住的按钮到达边界变为禁用时,即使手指还没抬起,按压缩放也会以弹簧回弹,按压蒙层则立即消失,与 `COUIStateEffectDrawable` 一致。
- 当数值到达边界时,对应按钮会自动禁用,只有它的符号变为禁用色。数值文字不会变暗,`enabled = false` 时也不会。
- 按钮是扁平的 `couiColorContainer8` 圆形,带 `couiColorPress` 按压蒙层和按尺寸计算的按压缩放(32dp 约为 0.88),没有材质、橡皮筋或形变效果。
- 数值区域宽度固定:按 `maxValue` 的字符数重复最宽的数字来测量,且不小于 44dp。数值变化时整行宽度不变。
- 每次步进都会触发 COUI 步进震动(ColorOS 上为 OPLUS 常量 308,其它平台回退到最接近的标准震动),长按连续步进的每一步也会触发。
