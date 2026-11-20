# TimePicker

`TimePicker` 是 COUI 中的基础交互组件，通过时 / 分垂直滚轮选择时间，对应 ColorOS 的 COUITimeLimitPicker。支持 24 小时制与 12 小时制；12 小时制下会增加上午/下午（AM/PM）滚轮，可按语言习惯放在小时列之前或分钟列之后。还可以在选中值之后绘制单位标签（如“时”/“分”），字号 14dp，基线为选中行基线上移 2dp，与 COUI 的单位文本一致。选中行上下各有一条 `divider` 颜色的细线，位于中心 ± 24dp，左右各内缩 16dp；AM/PM 滚轮未选中项字号为 18dp；按住分钟滚轮的上下行时每 100ms 步进一次。与 COUITimeLimitPicker 一致，RTL 布局下各滚轮仍保持从左到右的顺序（只有单位标签镜像），首末两列的触摸区域会延伸到两侧留白，在选择器任意位置拖动都能滚动它们。滚轮本身的行为见 [NumberPicker](./numberpicker)。

时间由纯数据类 `TimeValue` 表示；无论显示模式如何，小时始终以 24 小时制（0..23）存储。

<div style="position: relative; height: 320px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=timePicker" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.TimePicker
import io.github.suqi8.coui.kmp.basic.TimePickerDefaults
import io.github.suqi8.coui.kmp.basic.NumberPickerDefaults
import io.github.suqi8.coui.kmp.basic.TimeValue
```

## 基本用法

```kotlin
var time by remember { mutableStateOf(TimeValue(hour = 16, minute = 30)) }

TimePicker(
    value = time,
    onValueChange = { time = it }
)
```

## 组件状态

### 12 小时制

`is24Hour` 为 false 时，小时显示为 1..12，并在分钟列之后显示 AM/PM 滚轮。

```kotlin
TimePicker(
    value = time,
    onValueChange = { time = it },
    is24Hour = false
)
```

### 禁用状态

```kotlin
TimePicker(
    value = time,
    onValueChange = { time = it },
    enabled = false
)
```

## 属性

### TimePicker 属性

| 属性名            | 类型                | 说明                                         | 默认值                               | 是否必须 |
| ----------------- | ------------------- | -------------------------------------------- | ------------------------------------ | -------- |
| value             | TimeValue           | 当前选中的时间（小时始终为 24 小时制）       | -                                    | 是       |
| onValueChange     | (TimeValue) -> Unit | 选中时间变化时的回调函数                     | -                                    | 是       |
| modifier          | Modifier            | 应用于选择器的修饰符                         | Modifier                             | 否       |
| enabled           | Boolean             | 是否启用用户交互                             | true                                 | 否       |
| is24Hour          | Boolean             | 小时滚轮是否使用 24 小时制                   | true                                 | 否       |
| amPmFirst         | Boolean             | AM/PM 滚轮是否放在小时滚轮之前               | TimePickerDefaults.localeAmPmFirst() | 否       |
| amLabel           | String              | AM/PM 滚轮上「上午」的显示文本               | TimePickerDefaults.amLabel()         | 否       |
| pmLabel           | String              | AM/PM 滚轮上「下午」的显示文本               | TimePickerDefaults.pmLabel()         | 否       |
| hourUnit          | String              | 选中小时旁的单位标签，为空则隐藏             | TimePickerDefaults.HourUnit          | 否       |
| minuteUnit        | String              | 选中分钟旁的单位标签，为空则隐藏             | TimePickerDefaults.MinuteUnit        | 否       |
| visibleItemCount  | Int                 | 每个滚轮的可见行数（COUI setRowNumber），奇数且 ≥ 3 | 3                             | 否       |
| hourColumnWidth   | Dp                  | 小时列宽度                                   | TimePickerDefaults.HourColumnWidth   | 否       |
| minuteColumnWidth | Dp                  | 分钟列宽度                                   | TimePickerDefaults.MinuteColumnWidth | 否       |
| amPmColumnWidth   | Dp                  | AM/PM 列宽度                                 | TimePickerDefaults.AmPmColumnWidth   | 否       |
| colors            | NumberPickerColors  | 滚轮的颜色配置                               | NumberPickerDefaults.colors()        | 否       |
| dividerColor      | Color               | 选中行上下两条细线的颜色                     | COUITheme.colorScheme.divider    | 否       |
| textStyle         | TextStyle           | 滚轮项目的文本样式                           | COUITheme.textStyles.headlineM         | 否       |
| itemHeight        | Dp                  | 每个滚轮项目的高度                           | NumberPickerDefaults.ItemHeight      | 否       |
| insideMargin      | PaddingValues       | 滚轮列周围的内边距                           | TimePickerDefaults.InsideMargin      | 否       |

### TimeValue 类

| 属性名 | 类型 | 说明                              |
| ------ | ---- | --------------------------------- |
| hour   | Int  | 小时，24 小时制（0..23）          |
| minute | Int  | 分钟（0..59）                     |

### TimePickerDefaults 对象

| 属性名            | 类型          | 说明                             | 默认值                          |
| ----------------- | ------------- | -------------------------------- | ------------------------------- |
| HourColumnWidth   | Dp            | 小时列默认宽度                   | 76.dp                           |
| MinuteColumnWidth | Dp            | 分钟列默认宽度                   | 76.dp                           |
| AmPmColumnWidth   | Dp            | AM/PM 列默认宽度                 | 62.dp                           |
| InsideMargin      | PaddingValues | 滚轮列周围的默认内边距           | PaddingValues(vertical = 12.dp) |
| AmPmUnselectedTextSize | Dp       | AM/PM 滚轮未选中项字号           | 18.dp                           |
| MinuteStepInterval | Long         | 按住时分钟自动步进间隔（毫秒）   | 100L                            |
| HourUnit          | String        | 默认小时单位标签（隐藏）         | ""                              |
| MinuteUnit        | String        | 默认分钟单位标签（隐藏）         | ""                              |

| 方法名            | 返回类型 | 说明                                                           |
| ----------------- | -------- | -------------------------------------------------------------- |
| amLabel()         | String   | 当前语言的上午文本（"AM"、"上午"）                             |
| pmLabel()         | String   | 当前语言的下午文本（"PM"、"下午"）                             |
| localeAmPmFirst() | Boolean  | 当前语言的最佳 "hm" 模式是否把上午/下午放在前面（中文为 true） |

选择器最大宽度 640dp（`NumberPickerDefaults.PickersMaxWidth`）。

## 进阶用法

### 单位标签

在选中的时、分旁绘制单位标签，与 COUI 选择器一致：

```kotlin
TimePicker(
    value = time,
    onValueChange = { time = it },
    hourUnit = "时",
    minuteUnit = "分"
)
```

### 本地化 12 小时制

上午/下午文本以及该滚轮的位置默认随语言变化（中文在小时之前，英文在分钟之后），需要时可覆盖：

```kotlin
TimePicker(
    value = time,
    onValueChange = { time = it },
    is24Hour = false,
    amPmFirst = true,
    hourUnit = "时",
    minuteUnit = "分"
)
```

### 自定义颜色

```kotlin
TimePicker(
    value = time,
    onValueChange = { time = it },
    colors = NumberPickerDefaults.colors(
        selectedTextColor = Color.Red,
        unselectedTextColor = Color.Gray
    )
)
```
