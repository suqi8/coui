# NumberPicker

`NumberPicker` 是 COUI 中的基础交互组件，用于通过垂直滚动从一组数字中选择值。行为对齐 COUINumberPicker（ColorOS 17）：

- 每一行绘制两次，并在中心 48dp 选中带（中心 ± 24dp）处硬裁切：带内为 20dp 选中色文字，带外为 16dp 未选中色文字。跨越选中带边缘的行会被切成两半，没有渐变过渡。
- 字号单位是 dp，不跟随系统字体缩放。
- 惯性滑动使用 Android 样条曲线，速度为松手速度的 1.6 倍（先限制在 750..5000 px/s），停在整行上，再用 300ms 吸附对齐。
- 滚动过程中每次值变化都会触发 `onValueChange` 和一次振动反馈，而不只是在停止时。
- 点击中心上方或下方的行会前后步进一格；按住则每隔 `longPressStepInterval` 毫秒重复步进。

通过 `wrapAround` 参数支持无限循环滚动。

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=numberPicker" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.NumberPicker
```

## 基本用法

```kotlin
var value by remember { mutableIntStateOf(5) }

NumberPicker(
    value = value,
    onValueChange = { value = it },
    range = 0..10
)
```

## 组件状态

### 禁用状态

与 COUI 一致，禁用时颜色不变，只是不响应触摸。

```kotlin
var value by remember { mutableIntStateOf(5) }

NumberPicker(
    value = value,
    onValueChange = { value = it },
    range = 0..10,
    enabled = false
)
```

### 无限滚动

启用 `wrapAround` 后，选择器会从最后一项循环回到第一项，实现连续滚动。

```kotlin
var value by remember { mutableIntStateOf(0) }

NumberPicker(
    value = value,
    onValueChange = { value = it },
    range = 0..23,
    wrapAround = true
)
```

## 属性

### NumberPicker 属性

| 属性名           | 类型               | 说明                                                 | 默认值                           | 是否必须 |
| ---------------- | ------------------ | ---------------------------------------------------- | -------------------------------- | -------- |
| value            | Int                | 当前选中的值，超出 range 时会自动修正                | -                                | 是       |
| onValueChange    | (Int) -> Unit      | 选中值变化时的回调函数                               | -                                | 是       |
| modifier         | Modifier           | 应用于选择器的修饰符                                 | Modifier                         | 否       |
| enabled          | Boolean            | 是否启用用户交互                                     | true                             | 否       |
| range            | IntRange           | 可选择的值范围                                       | 0..10                            | 否       |
| label            | (Int) -> String    | 将值转换为显示字符串的函数                           | { it.toString() }                | 否       |
| unit             | String             | 绘制在选中值之后的单位标签，为空则隐藏               | ""                               | 否       |
| visibleItemCount | Int                | 可见项数。必须为奇数且不小于 3                       | 3                                | 否       |
| wrapAround       | Boolean            | 是否启用循环滚动（仅当项数多于 visibleItemCount 时生效） | false                        | 否       |
| longPressStepInterval | Long          | 按住上下行时自动步进的间隔（毫秒）                   | NumberPickerDefaults.LongPressStepInterval | 否 |
| colors           | NumberPickerColors | 选择器的颜色配置                                     | NumberPickerDefaults.colors()    | 否       |
| textStyle        | TextStyle          | 基础文本样式，字号会被下面两个尺寸替换               | COUITheme.textStyles.title3     | 否       |
| selectedTextSize | Dp                 | 选中带内的字号                                       | NumberPickerDefaults.SelectedTextSize | 否 |
| unselectedTextSize | Dp               | 选中带外的字号                                       | NumberPickerDefaults.UnselectedTextSize | 否 |
| itemHeight       | Dp                 | 每个项目的高度                                       | NumberPickerDefaults.ItemHeight   | 否       |

### NumberPickerDefaults 对象

NumberPickerDefaults 对象提供了 NumberPicker 组件的默认配置。

#### 属性

| 属性名     | 类型 | 说明                 | 默认值  |
| ---------- | ---- | -------------------- | ------- |
| ItemHeight | Dp   | 每个项目的默认高度   | 45.dp   |
| SelectedTextSize | Dp | 选中带内的字号 | 20.dp |
| UnselectedTextSize | Dp | 选中带外的字号 | 16.dp |
| UnitTextSize | Dp | 单位标签字号 | 14.dp |
| LongPressStepInterval | Long | 按住上下行时的自动步进间隔（毫秒） | 300L |

#### 方法

| 方法名   | 返回类型           | 说明                       |
| -------- | ------------------ | -------------------------- |
| colors() | NumberPickerColors | 创建默认的颜色配置         |

`colors()` 工厂方法接受以下参数：

| 参数名                      | 类型  | 默认值                                         |
| --------------------------- | ----- | ---------------------------------------------- |
| selectedTextColor           | Color | COUITheme.colorScheme.onSurface                |
| unselectedTextColor         | Color | COUITheme.colorScheme.onSurfaceVariantActions  |
| disabledSelectedTextColor   | Color | selectedTextColor                              |
| disabledUnselectedTextColor | Color | unselectedTextColor                            |

### NumberPickerColors 类

| 属性名                      | 类型  | 说明                       |
| --------------------------- | ----- | -------------------------- |
| selectedTextColor           | Color | 选中项（居中）的文字颜色   |
| unselectedTextColor         | Color | 未选中项的文字颜色         |
| disabledSelectedTextColor   | Color | 禁用时选中项的文字颜色     |
| disabledUnselectedTextColor | Color | 禁用时未选中项的文字颜色   |

## 进阶用法

### 自定义标签格式

```kotlin
var hour by remember { mutableIntStateOf(9) }

NumberPicker(
    value = hour,
    onValueChange = { hour = it },
    range = 0..23,
    label = { it.toString().padStart(2, '0') }  // "00", "01", ... "23"
)
```

### 单位标签

单位以 14dp 字号绘制在 18dp 数值槽位加 4dp 间距之后，基线为选中行基线上移 2dp。

```kotlin
var weight by remember { mutableIntStateOf(60) }

NumberPicker(
    value = weight,
    onValueChange = { weight = it },
    range = 30..150,
    unit = "kg"
)
```

### 时间选择器

组合两个 NumberPicker 创建时间选择器。

```kotlin
var hour by remember { mutableIntStateOf(16) }
var minute by remember { mutableIntStateOf(30) }

Row(
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically,
) {
    NumberPicker(
        value = hour,
        onValueChange = { hour = it },
        range = 0..23,
        label = { it.toString().padStart(2, '0') },
        wrapAround = true,
        modifier = Modifier.weight(1f),
    )
    Text(
        text = ":",
        fontWeight = FontWeight.Bold,
    )
    NumberPicker(
        value = minute,
        onValueChange = { minute = it },
        range = 0..59,
        label = { it.toString().padStart(2, '0') },
        wrapAround = true,
        modifier = Modifier.weight(1f),
    )
}
```

### 自定义可见项数

```kotlin
var value by remember { mutableIntStateOf(5) }

NumberPicker(
    value = value,
    onValueChange = { value = it },
    range = 1..100,
    visibleItemCount = 5 // Must be odd and at least 3
)
```

### 自定义颜色

```kotlin
var value by remember { mutableIntStateOf(5) }

NumberPicker(
    value = value,
    onValueChange = { value = it },
    range = 0..10,
    colors = NumberPickerDefaults.colors(
        selectedTextColor = Color.Red,
        unselectedTextColor = Color.Gray
    )
)
```
