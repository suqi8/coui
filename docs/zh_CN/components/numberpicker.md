# NumberPicker

`NumberPicker` 是 COUI 中的基础交互组件，用于通过垂直滚动从一组数字中选择值。行为对齐 COUINumberPicker（ColorOS 17）：

- 每一行绘制两次，并在中心 48dp 选中带（中心 ± 24dp）处硬裁切：带内为 20dp 选中色文字，带外为 16dp 未选中色文字。跨越选中带边缘的行会被切成两半，没有渐变过渡。
- 字号单位是 dp，不跟随系统字体缩放。
- 惯性滑动使用 Android 样条曲线，速度为松手速度的 1.6 倍（先限制在 750..5000 px/s）。与 COUINumberPicker.fling 一致，滑动距离按整行计算，并从松手时显示的值（而不是手指位置）起算，时长为样条时长 x 1.5；不足一行的轻甩保留小数部分，随后的 300ms 吸附决定落在哪一行。不循环的滚轮会在首行或末行直接停住，而不是缓动靠过去。Android 与 COUI 一样直接使用这些 px/s 原始值；其他平台把它们视为 3.5x 参考手机上的像素，再按实际密度缩放（`值 * density / 3.5`），使甩动距离与手机一致。
- 比滚轮还宽的文字从起始边绘制而不再居中；与 COUI 一致，按未选中字号判断一次，两遍绘制共用结果。
- 滚动过程中每次值变化都会触发 `onValueChange`，而不只是在停止时。
- 每次值变化都会触发一次振动，强度随滚动速度变化（静止 50%，5000 px/s 时 100%，即 COUI「crisp」RichTap 曲线），最短间隔 25ms。Android 11+ 且应用声明了 `VIBRATE` 权限时使用按强度缩放的 `VibrationEffect` 组合原语；否则使用固定的 OPLUS 308 振动（非 Android 平台为标准触感）。
- Android 上每次值变化还会在系统「触摸提示音」开启时播放 COUI 的 `coui_numberpicker_click.ogg`；其他平台静音。
- 各行按默认字体的 top/bottom 度量居中，与 COUI 一致（比按 ascent/descent 居中高约 0.8dp）。
- 点击中心上方或下方的行会前后步进一格；按住则每隔 `longPressStepInterval` 毫秒重复步进。

与 COUI 一致默认循环滚动（`wrapAround`）；传 `wrapAround = false` 可在两端停住。

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=numberPicker" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.NumberPicker
import io.github.suqi8.coui.kmp.basic.NumberPickerDefaults
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

`wrapAround`（默认开启，对应 COUI `mWrapSelectorWheelPreferred = true`）时，选择器会从最后一项循环回到第一项，实现连续滚动。与 COUI 一致，仅当项数多于 `visibleItemCount` 时才循环。

### 可选值

`ignorable = true` 会在最后一个值之后加一行占位（绘制为两条短横，位置略低于行中心，COUI 为中心下方 33.75px），选中时回调 `NumberPickerDefaults.IgnoredValue`（COUI `setIgnorable`，`DatePicker` 的可忽略年份即用它）。

```kotlin
var year by remember { mutableIntStateOf(NumberPickerDefaults.IgnoredValue) }

NumberPicker(
    value = year,
    onValueChange = { year = it },
    range = 1950..2030,
    ignorable = true
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
| wrapAround       | Boolean            | 是否启用循环滚动（仅当项数多于 visibleItemCount 时生效） | true                         | 否       |
| ignorable        | Boolean            | 在最后一个值之后加一行占位，选中时回调 `NumberPickerDefaults.IgnoredValue` | false      | 否       |
| longPressStepInterval | Long          | 按住上下行时自动步进的间隔（毫秒）                   | NumberPickerDefaults.LongPressStepInterval | 否 |
| colors           | NumberPickerColors | 选择器的颜色配置                                     | NumberPickerDefaults.colors()    | 否       |
| textStyle        | TextStyle          | 基础文本样式，字号会被下面两个尺寸替换               | COUITheme.textStyles.headlineM     | 否       |
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
| IgnoredValue | Int | 可忽略滚轮的占位行回调的值 | Int.MIN_VALUE |
| MinWidth | Dp | modifier 未给宽度时滚轮的宽度 | 80.dp |
| PickersMaxWidth | Dp | DatePicker、TimePicker、DateTimePicker、LunarDatePicker 的最大宽度 | 640.dp |

`modifier` 未指定宽度时滚轮宽 80dp（COUI `internalMinWidth`）。无障碍服务可以向前 / 向后步进滚轮，RTL 布局下单位标签会镜像到数值左侧。

#### 方法

| 方法名   | 返回类型           | 说明                       |
| -------- | ------------------ | -------------------------- |
| colors() | NumberPickerColors | 创建默认的颜色配置         |

`colors()` 工厂方法接受以下参数：

| 参数名                      | 类型  | 默认值                                         |
| --------------------------- | ----- | ---------------------------------------------- |
| selectedTextColor           | Color | COUITheme.colorScheme.labelPrimary                |
| unselectedTextColor         | Color | COUITheme.colorScheme.labelTertiary  |

### NumberPickerColors 类

| 属性名                      | 类型  | 说明                                 |
| --------------------------- | ----- | ------------------------------------ |
| selectedTextColor           | Color | 选中项（居中）与单位标签的文字颜色   |
| unselectedTextColor         | Color | 未选中项与占位短横的颜色             |

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
