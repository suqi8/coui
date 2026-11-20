# LunarDatePicker

`LunarDatePicker` 是三列滚轮的农历日期选择器，对应 ColorOS 的 COUILunarDatePicker。与 COUI 一致，它持有公历日期（`value` / `onValueChange`），并以农历滚轮展示：农历年、农历月（有闰月的年份共 13 项，闰月显示为 `闰` + 对应月份）和农历日（初一 .. 三十）。滚动年份时保留农历月和日（仅当新年份的闰月相同时才保留闰月标记）；滚动月或年时日会收敛到新月份的天数。与 COUILunarDatePicker.updateSpinners 一致，当日期恰好位于 `minDate` 或 `maxDate` 时，月与日滚轮停止循环，只提供可到达一侧的值。滚轮行为见 [NumberPicker](./numberpicker)，分隔细线与按语言排列字段的方式同 [DatePicker](./datepicker)；同样，首末两列的触摸区域会延伸到两侧留白（COUILunarDatePicker.onMeasure）。

换算使用 `LunarCalendar`，它是 COUILunarUtil 的纯 Kotlin 移植（数据表覆盖农历 1900..2100 年）。

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=lunarDatePicker" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.DateValue
import io.github.suqi8.coui.kmp.basic.LunarCalendar
import io.github.suqi8.coui.kmp.basic.LunarDate
import io.github.suqi8.coui.kmp.basic.LunarDatePicker
import io.github.suqi8.coui.kmp.basic.LunarDatePickerDefaults
```

## 基本用法

```kotlin
var date by remember { mutableStateOf(DateValue(year = 2025, month = 7, day = 25)) }

LunarDatePicker(
    value = date,
    onValueChange = { date = it }
)
Text(LunarCalendar.format(date)) // 2025年闰六月初一
```

## 组件状态

### 禁用状态

```kotlin
LunarDatePicker(
    value = date,
    onValueChange = { date = it },
    enabled = false
)
```

## 属性

### LunarDatePicker 属性

| 属性名        | 类型                     | 说明                                         | 默认值                                | 必须 |
| ------------- | ------------------------ | -------------------------------------------- | ------------------------------------- | ---- |
| value         | DateValue                | 选中的公历日期，会收敛到 minDate..maxDate    | -                                     | 是   |
| onValueChange | (DateValue) -> Unit      | 滚轮变化时以新的公历日期回调                 | -                                     | 是   |
| modifier      | Modifier                 | 应用于选择器的修饰符                         | Modifier                              | 否   |
| enabled       | Boolean                  | 是否可交互                                   | true                                  | 否   |
| minDate       | DateValue                | 最早可选公历日期                             | LunarDatePickerDefaults.MinDate       | 否   |
| maxDate       | DateValue                | 最晚可选公历日期                             | LunarDatePickerDefaults.MaxDate       | 否   |
| yearLabel     | (Int) -> String          | 将农历年转换为显示文字                       | LunarDatePickerDefaults.YearLabel     | 否   |
| monthLabel    | (Int, Boolean) -> String | 将农历月及其闰月标记转换为显示文字           | LunarDatePickerDefaults.MonthLabel    | 否   |
| dayLabel      | (Int) -> String          | 将农历日（1..30）转换为显示文字              | LunarDatePickerDefaults.DayLabel      | 否   |
| fieldOrder    | DateFieldOrder           | 滚轮从左到右的顺序（RTL 下镜像）             | DatePickerDefaults.localeFieldOrder() | 否   |
| columnWidth   | Dp                       | 每列滚轮宽度                                 | LunarDatePickerDefaults.ColumnWidth   | 否   |
| colors        | NumberPickerColors       | 滚轮颜色配置                                 | NumberPickerDefaults.colors()         | 否   |
| dividerColor  | Color                    | 选中行上下两条细线的颜色                     | COUITheme.colorScheme.divider     | 否   |
| textStyle     | TextStyle                | 滚轮项文字样式                               | COUITheme.textStyles.headlineM           | 否   |
| itemHeight    | Dp                       | 每个滚轮项的高度                             | NumberPickerDefaults.ItemHeight       | 否   |
| insideMargin  | PaddingValues            | 滚轮列外围内边距                             | LunarDatePickerDefaults.InsideMargin  | 否   |

### LunarDatePickerDefaults 对象

| 属性名            | 类型                     | 说明                              | 默认值                          |
| ----------------- | ------------------------ | --------------------------------- | ------------------------------- |
| MinDate           | DateValue                | 最早可选日期（COUI sMinDate）     | 1910-03-10                      |
| MaxDate           | DateValue                | 最晚可选日期                      | 2099-12-31                      |
| ColumnWidth       | Dp                       | 每列宽度                          | 76.dp                           |
| InsideMargin      | PaddingValues            | 滚轮列外围内边距                  | PaddingValues(vertical = 12.dp) |
| YearStepInterval  | Long                     | 按住时年份自动步进间隔（ms）      | 100L                            |
| MonthStepInterval | Long                     | 按住时月份自动步进间隔（ms）      | 200L                            |
| DayStepInterval   | Long                     | 按住时日自动步进间隔（ms）        | 100L                            |
| YearLabel         | (Int) -> String          | 纯数字农历年                      | { it.toString() }               |
| MonthLabel        | (Int, Boolean) -> String | 正月 .. 冬月、腊月，闰月加「闰」  | "正月"、"闰六月"                |
| DayLabel          | (Int) -> String          | 初一 .. 三十                      | LunarCalendar.dayName(it)       |

### LunarCalendar 对象

| 函数 / 属性                                | 说明                                                   |
| ------------------------------------------ | ------------------------------------------------------ |
| SupportedYears                             | 数据表覆盖的农历年份（1900..2100）                     |
| MinSolarDate / MaxSolarDate                | `fromSolar` 接受的公历范围：1900-01-31 .. 2100-12-31   |
| fromSolar(date: DateValue): LunarDate      | 公历转农历；超出 MinSolarDate .. MaxSolarDate 时抛异常 |
| toSolar(lunar: LunarDate): DateValue?      | 农历转公历，农历日期不存在时返回 null                  |
| changeYear(lunar, newYear): DateValue?     | 把农历日期移到另一农历年，同时修正闰月标记与日         |
| leapMonth(year): Int                       | 某农历年的闰月，无闰月为 0                             |
| daysInMonth(year, month[, isLeapMonth])    | 农历月天数（29 / 30）                                  |
| daysInLeapMonth(year) / daysInYear(year)   | 闰月天数 / 全年天数                                    |
| dayName(day): String                       | 初一 .. 三十                                           |
| format(date) / format(lunar): String       | 如 `2026年九月廿一`                                    |
| solarTerm(date): String?                   | 公历日期上的节气（1970..2036，繁体名称）               |
| cellLabel(date): String                    | 日历格文字：节日、节气、月名或日名                     |

### LunarDate 类

| 属性名      | 类型    | 说明                     |
| ----------- | ------- | ------------------------ |
| year        | Int     | 农历年                   |
| month       | Int     | 农历月，从 1 开始        |
| day         | Int     | 农历日，从 1 开始        |
| isLeapMonth | Boolean | 是否为闰月               |

## 进阶用法

### 读取农历日期

```kotlin
val lunar = LunarCalendar.fromSolar(date)
val solar = LunarCalendar.toSolar(LunarDate(year = 2026, month = 8, day = 15)) // 中秋
```
