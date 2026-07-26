# DatePicker

`DatePicker` 是 Miuix 中的基础交互组件，通过三列垂直滚轮（年 / 月 / 日）选择日期，对应 ColorOS 的 COUIDatePicker。日列范围会随所选年月自动联动（含闰年），切换到天数更少的月份时日值会自动收敛。所有列均支持无限循环滚动。

日期由纯数据类 `DateValue` 表示，不依赖任何日期时间库。

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.DatePicker
import io.github.suqi8.coui.kmp.basic.DatePickerDefaults
import io.github.suqi8.coui.kmp.basic.DateValue
```

## 基本用法

```kotlin
var date by remember { mutableStateOf(DateValue(year = 2026, month = 7, day = 26)) }

DatePicker(
    value = date,
    onValueChange = { date = it }
)
```

## 组件状态

### 禁用状态

```kotlin
DatePicker(
    value = date,
    onValueChange = { date = it },
    enabled = false
)
```

## 属性

### DatePicker 属性

| 属性名           | 类型                 | 说明                                   | 默认值                              | 是否必须 |
| ---------------- | -------------------- | -------------------------------------- | ----------------------------------- | -------- |
| value            | DateValue            | 当前选中的日期，超出范围的字段会被修正 | -                                   | 是       |
| onValueChange    | (DateValue) -> Unit  | 选中日期变化时的回调函数               | -                                   | 是       |
| modifier         | Modifier             | 应用于选择器的修饰符                   | Modifier                            | 否       |
| enabled          | Boolean              | 是否启用用户交互                       | true                                | 否       |
| yearRange        | IntRange             | 可选择的年份范围                       | DatePickerDefaults.YearRange        | 否       |
| yearLabel        | (Int) -> String      | 年份的显示文本转换函数                 | DatePickerDefaults.YearLabel        | 否       |
| monthLabel       | (Int) -> String      | 月份（1..12）的显示文本转换函数        | DatePickerDefaults.MonthLabel       | 否       |
| dayLabel         | (Int) -> String      | 日的显示文本转换函数                   | DatePickerDefaults.DayLabel         | 否       |
| yearColumnWidth  | Dp                   | 年份列宽度                             | DatePickerDefaults.YearColumnWidth  | 否       |
| monthColumnWidth | Dp                   | 月份列宽度                             | DatePickerDefaults.MonthColumnWidth | 否       |
| dayColumnWidth   | Dp                   | 日列宽度                               | DatePickerDefaults.DayColumnWidth   | 否       |
| colors           | NumberPickerColors   | 滚轮的颜色配置                         | NumberPickerDefaults.colors()       | 否       |
| textStyle        | TextStyle            | 滚轮项目的文本样式                     | COUITheme.textStyles.title3        | 否       |
| itemHeight       | Dp                   | 每个滚轮项目的高度                     | NumberPickerDefaults.ItemHeight     | 否       |
| insideMargin     | PaddingValues        | 滚轮列周围的内边距                     | DatePickerDefaults.InsideMargin     | 否       |

### DateValue 类

| 属性名 | 类型 | 说明                          |
| ------ | ---- | ----------------------------- |
| year   | Int  | 年份（如 2026）               |
| month  | Int  | 月份，从 1 开始（1 = 一月）   |
| day    | Int  | 日，从 1 开始                 |

### DatePickerDefaults 对象

| 属性名           | 类型            | 说明                       | 默认值                          |
| ---------------- | --------------- | -------------------------- | ------------------------------- |
| YearRange        | IntRange        | 默认可选年份范围           | 1900..2100                      |
| YearColumnWidth  | Dp              | 年份列默认宽度             | 76.dp                           |
| MonthColumnWidth | Dp              | 月份列默认宽度             | 96.dp                           |
| DayColumnWidth   | Dp              | 日列默认宽度               | 62.dp                           |
| InsideMargin     | PaddingValues   | 滚轮列周围的默认内边距     | PaddingValues(vertical = 12.dp) |
| YearLabel        | (Int) -> String | 纯年份数字                 | { it.toString() }               |
| MonthLabel       | (Int) -> String | 英文月份缩写               | "Jan".."Dec"                    |
| DayLabel         | (Int) -> String | 两位补零数字               | "01".."31"                      |

## 进阶用法

### 自定义年份范围

```kotlin
DatePicker(
    value = date,
    onValueChange = { date = it },
    yearRange = 2000..2030
)
```

### 本地化标签

传入自定义标签函数以本地化滚轮项目。例如中文 COUI 风格会把单位嵌入每个项目：

```kotlin
DatePicker(
    value = date,
    onValueChange = { date = it },
    yearLabel = { "${it}年" },
    monthLabel = { "${it}月" },
    dayLabel = { "${it}日" }
)
```

### 自定义颜色

```kotlin
DatePicker(
    value = date,
    onValueChange = { date = it },
    colors = NumberPickerDefaults.colors(
        selectedTextColor = Color.Red,
        unselectedTextColor = Color.Gray
    )
)
```
