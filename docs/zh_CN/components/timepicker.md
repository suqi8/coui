# TimePicker

`TimePicker` 是 Miuix 中的基础交互组件，通过时 / 分垂直滚轮选择时间，对应 ColorOS 的 COUITimeLimitPicker。支持 24 小时制与 12 小时制；12 小时制下会增加上午/下午（AM/PM）滚轮，可按语言习惯放在小时列之前或分钟列之后。还可以在选中行旁绘制单位标签（如“时”/“分”），与 COUI 的单位文本一致。

时间由纯数据类 `TimeValue` 表示；无论显示模式如何，小时始终以 24 小时制（0..23）存储。

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.TimePicker
import io.github.suqi8.coui.kmp.basic.TimePickerDefaults
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
| amPmFirst         | Boolean             | AM/PM 滚轮是否放在小时滚轮之前               | false                                | 否       |
| amLabel           | String              | AM/PM 滚轮上「上午」的显示文本               | TimePickerDefaults.AmLabel           | 否       |
| pmLabel           | String              | AM/PM 滚轮上「下午」的显示文本               | TimePickerDefaults.PmLabel           | 否       |
| hourUnit          | String              | 选中小时旁的单位标签，为空则隐藏             | TimePickerDefaults.HourUnit          | 否       |
| minuteUnit        | String              | 选中分钟旁的单位标签，为空则隐藏             | TimePickerDefaults.MinuteUnit        | 否       |
| hourColumnWidth   | Dp                  | 小时列宽度                                   | TimePickerDefaults.HourColumnWidth   | 否       |
| minuteColumnWidth | Dp                  | 分钟列宽度                                   | TimePickerDefaults.MinuteColumnWidth | 否       |
| amPmColumnWidth   | Dp                  | AM/PM 列宽度                                 | TimePickerDefaults.AmPmColumnWidth   | 否       |
| colors            | NumberPickerColors  | 滚轮的颜色配置                               | NumberPickerDefaults.colors()        | 否       |
| textStyle         | TextStyle           | 滚轮项目的文本样式                           | COUITheme.textStyles.title3         | 否       |
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
| UnitTextOffset    | Dp            | 单位标签相对滚轮中心的偏移       | 13.dp                           |
| UnitFontSize      | TextUnit      | 单位标签的字号                   | 14.sp                           |
| AmLabel           | String        | 默认 AM 文本                     | "AM"                            |
| PmLabel           | String        | 默认 PM 文本                     | "PM"                            |
| HourUnit          | String        | 默认小时单位标签（隐藏）         | ""                              |
| MinuteUnit        | String        | 默认分钟单位标签（隐藏）         | ""                              |

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

对于时间格式以上午/下午开头的语言（如中文），可将 AM/PM 滚轮放在最前并本地化其文本：

```kotlin
TimePicker(
    value = time,
    onValueChange = { time = it },
    is24Hour = false,
    amPmFirst = true,
    amLabel = "上午",
    pmLabel = "下午",
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
