# DatePicker

`DatePicker` 是 COUI 中的基础交互组件，通过三列垂直滚轮（年 / 月 / 日）选择日期，对应 ColorOS 的 COUIDatePicker。日列范围会随所选年月自动联动（含闰年），切换到天数更少的月份时日值会自动收敛。日期限定在 `minDate` .. `maxDate` 之内：与 COUIDatePicker.updateSpinners 一致，选中值处于首 / 末年（或月）时，月（或日）滚轮只提供可达的值，且除非仍覆盖全年（或整月）否则不再循环。默认文案随语言变化（英文 "2026 / Jan / 01"，中文 "2026年 / 1月 / 1日"），选择器最大宽度 640dp。选中行上下各有一条 `divider` 颜色的细线，位于中心 ± 24dp，左右各内缩 16dp。与 COUIDatePicker 一致，按住中心上方或下方的行时，年和日每 100ms、月每 200ms 自动步进。各列按当前语言的日期字段顺序排列（如美式英语为月/日/年、中文为年/月/日、德语为日/月/年），对应 COUIDatePicker.reorderSpinners。滚轮本身的行为见 [NumberPicker](./numberpicker)。

日期由纯数据类 `DateValue` 表示，不依赖任何日期时间库。

<div style="position: relative; height: 320px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=datePicker" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.DateFieldOrder
import io.github.suqi8.coui.kmp.basic.DatePicker
import io.github.suqi8.coui.kmp.basic.DatePickerDefaults
import io.github.suqi8.coui.kmp.basic.DateValue
import io.github.suqi8.coui.kmp.basic.NumberPickerDefaults
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
| yearIgnorable    | Boolean              | 年份是否可留空（多一行占位）           | false                               | 否       |
| minDate          | DateValue            | 最早可选日期                           | DatePickerDefaults.MinDate          | 否       |
| maxDate          | DateValue            | 最晚可选日期                           | DatePickerDefaults.MaxDate          | 否       |
| yearLabel        | (Int) -> String      | 年份的显示文本转换函数                 | DatePickerDefaults.yearLabel()      | 否       |
| monthLabel       | (Int) -> String      | 月份（1..12）的显示文本转换函数        | DatePickerDefaults.monthLabel()     | 否       |
| dayLabel         | (Int) -> String      | 日的显示文本转换函数                   | DatePickerDefaults.dayLabel()       | 否       |
| fieldOrder       | DateFieldOrder       | 滚轮从左到右的顺序（RTL 下镜像）       | DatePickerDefaults.localeFieldOrder() | 否     |
| yearColumnWidth  | Dp                   | 年份列宽度                             | DatePickerDefaults.YearColumnWidth  | 否       |
| monthColumnWidth | Dp                   | 月份列宽度                             | DatePickerDefaults.MonthColumnWidth | 否       |
| dayColumnWidth   | Dp                   | 日列宽度                               | DatePickerDefaults.DayColumnWidth   | 否       |
| colors           | NumberPickerColors   | 滚轮的颜色配置                         | NumberPickerDefaults.colors()       | 否       |
| dividerColor     | Color                | 选中行上下两条细线的颜色               | COUITheme.colorScheme.divider   | 否       |
| textStyle        | TextStyle            | 滚轮项目的文本样式                     | COUITheme.textStyles.headlineM        | 否       |
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
| MinDate          | DateValue       | 默认最早日期               | DateValue(1900, 1, 1)           |
| MaxDate          | DateValue       | 默认最晚日期               | DateValue(2100, 12, 31)         |
| IgnoredYear      | Int             | 年份留空时回调的年份       | Int.MIN_VALUE                   |
| YearColumnWidth  | Dp              | 年份列默认宽度             | 76.dp                           |
| MonthColumnWidth | Dp              | 月份列默认宽度             | 96.dp                           |
| DayColumnWidth   | Dp              | 日列默认宽度               | 62.dp                           |
| InsideMargin     | PaddingValues   | 滚轮列周围的默认内边距     | PaddingValues(vertical = 12.dp) |
| YearStepInterval | Long            | 按住时年份自动步进间隔（毫秒） | 100L                        |
| MonthStepInterval | Long           | 按住时月份自动步进间隔（毫秒） | 200L                        |
| DayStepInterval  | Long            | 按住时日自动步进间隔（毫秒）   | 100L                        |

| 方法名             | 返回类型        | 说明                                         |
| ------------------ | --------------- | -------------------------------------------- |
| yearLabel()        | (Int) -> String | 中文为 "2026年"，其他语言为纯数字            |
| monthLabel()       | (Int) -> String | 当前语言的月份缩写（"Jan"、"1月"）           |
| dayLabel()         | (Int) -> String | 中文为 "5日"，其他语言为两位补零数字         |
| localeFieldOrder() | DateFieldOrder  | 当前语言的滚轮顺序                           |

`DatePickerDefaults.localeFieldOrder()` 读取平台的最佳 "yyyyMMMMdd" 日期模式：Android 用 `DateFormat.getBestDateTimePattern`，桌面用 JDK 长日期模式，iOS / macOS 用 `NSDateFormatter.dateFormatFromTemplate`，Web 用 `Intl.DateTimeFormat`；无法判断时回退为 `DateFieldOrder.YearMonthDay`。

### DateFieldOrder 枚举

`YearMonthDay`、`YearDayMonth`、`MonthDayYear`、`MonthYearDay`、`DayMonthYear`、`DayYearMonth`。每项提供 `fields: List<DateField>`；`DateFieldOrder.fromPattern(pattern)` 可从日期模式解析顺序。

## 进阶用法

### 固定列顺序

```kotlin
DatePicker(
    value = date,
    onValueChange = { date = it },
    fieldOrder = DateFieldOrder.YearMonthDay
)
```

### 日期范围

```kotlin
DatePicker(
    value = date,
    onValueChange = { date = it },
    minDate = DateValue(2026, 3, 15),
    maxDate = DateValue(2027, 8, 20)
)
```

### 可忽略年份

`yearIgnorable` 会在年份滚轮的最后一年之后加一行占位（COUIDatePicker `couiYearIgnorable`）。选中它时年份回调为 `DatePickerDefaults.IgnoredYear`，并且 2 月 29 日仍可选：

```kotlin
var birthday by remember { mutableStateOf(DateValue(DatePickerDefaults.IgnoredYear, 2, 29)) }

DatePicker(
    value = birthday,
    onValueChange = { birthday = it },
    yearIgnorable = true
)
```

### 自定义标签

默认标签已随语言变化，需要时可传入标签函数覆盖：

```kotlin
DatePicker(
    value = date,
    onValueChange = { date = it },
    monthLabel = { "M$it" }
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
