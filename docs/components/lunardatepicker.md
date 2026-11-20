# LunarDatePicker

`LunarDatePicker` is a three-wheel Chinese lunar calendar date picker mirroring ColorOS's COUILunarDatePicker. Like COUI, it holds a Gregorian date (`value` / `onValueChange`) and shows it as lunar wheels: the lunar year, the lunar month (13 entries in a year with a leap month, the leap month labelled `闰` + its regular month) and the lunar day (初一 .. 三十). Moving the year keeps the lunar month and day (the leap flag only survives when the new year has the same leap month); moving the month or year clamps the day to the new month's length. While the date sits exactly on `minDate` or `maxDate`, the month and day wheels stop wrapping and only offer values on the reachable side, as in COUILunarDatePicker.updateSpinners. The wheels follow [NumberPicker](./numberpicker) and the hairlines / locale field order of [DatePicker](./datepicker); as there, the first and last wheel also cover the space left at either side (COUILunarDatePicker.onMeasure).

Conversions use `LunarCalendar`, a pure Kotlin port of COUILunarUtil (tables cover lunar years 1900..2100).

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=lunarDatePicker" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.DateValue
import io.github.suqi8.coui.kmp.basic.LunarCalendar
import io.github.suqi8.coui.kmp.basic.LunarDate
import io.github.suqi8.coui.kmp.basic.LunarDatePicker
import io.github.suqi8.coui.kmp.basic.LunarDatePickerDefaults
```

## Basic Usage

```kotlin
var date by remember { mutableStateOf(DateValue(year = 2025, month = 7, day = 25)) }

LunarDatePicker(
    value = date,
    onValueChange = { date = it }
)
Text(LunarCalendar.format(date)) // 2025年闰六月初一
```

## Component States

### Disabled State

```kotlin
LunarDatePicker(
    value = date,
    onValueChange = { date = it },
    enabled = false
)
```

## Properties

### LunarDatePicker Properties

| Property Name | Type                       | Description                                              | Default Value                       | Required |
| ------------- | -------------------------- | -------------------------------------------------------- | ----------------------------------- | -------- |
| value         | DateValue                  | The selected Gregorian date; clamped to minDate..maxDate | -                                   | Yes      |
| onValueChange | (DateValue) -> Unit        | Called with the new Gregorian date when a wheel moves    | -                                   | Yes      |
| modifier      | Modifier                   | Modifier applied to the picker                           | Modifier                            | No       |
| enabled       | Boolean                    | Whether the picker is enabled for user interaction       | true                                | No       |
| minDate       | DateValue                  | The earliest selectable Gregorian date                   | LunarDatePickerDefaults.MinDate     | No       |
| maxDate       | DateValue                  | The latest selectable Gregorian date                     | LunarDatePickerDefaults.MaxDate     | No       |
| yearLabel     | (Int) -> String            | Converts a lunar year to its display string              | LunarDatePickerDefaults.YearLabel   | No       |
| monthLabel    | (Int, Boolean) -> String   | Converts a lunar month and its leap flag to a string     | LunarDatePickerDefaults.MonthLabel  | No       |
| dayLabel      | (Int) -> String            | Converts a lunar day (1..30) to its display string       | LunarDatePickerDefaults.DayLabel    | No       |
| fieldOrder    | DateFieldOrder             | Left-to-right order of the wheels (mirrored in RTL)      | DatePickerDefaults.localeFieldOrder() | No     |
| columnWidth   | Dp                         | Width of each wheel                                      | LunarDatePickerDefaults.ColumnWidth | No       |
| colors        | NumberPickerColors         | Color configuration of the wheels                        | NumberPickerDefaults.colors()       | No       |
| dividerColor  | Color                      | Color of the two hairlines around the selected row       | COUITheme.colorScheme.divider   | No       |
| textStyle     | TextStyle                  | Text style for the wheel items                           | COUITheme.textStyles.headlineM         | No       |
| itemHeight    | Dp                         | The height of each wheel item                            | NumberPickerDefaults.ItemHeight     | No       |
| insideMargin  | PaddingValues              | Padding around the wheel columns                         | LunarDatePickerDefaults.InsideMargin | No      |

### LunarDatePickerDefaults Object

| Property Name     | Type                     | Description                                   | Default Value                   |
| ----------------- | ------------------------ | --------------------------------------------- | ------------------------------- |
| MinDate           | DateValue                | Earliest selectable date (COUI sMinDate)      | 1910-03-10                      |
| MaxDate           | DateValue                | Latest selectable date                        | 2099-12-31                      |
| ColumnWidth       | Dp                       | Width of every wheel                          | 76.dp                           |
| InsideMargin      | PaddingValues            | Padding around the wheel columns              | PaddingValues(vertical = 12.dp) |
| YearStepInterval  | Long                     | Year auto-repeat interval (ms) while held     | 100L                            |
| MonthStepInterval | Long                     | Month auto-repeat interval (ms) while held    | 200L                            |
| DayStepInterval   | Long                     | Day auto-repeat interval (ms) while held      | 100L                            |
| YearLabel         | (Int) -> String          | Plain lunar year number                       | { it.toString() }               |
| MonthLabel        | (Int, Boolean) -> String | 正月 .. 冬月, 腊月, prefixed with 闰 if leap  | "正月", "闰六月"                |
| DayLabel          | (Int) -> String          | 初一 .. 三十                                  | LunarCalendar.dayName(it)       |

### LunarCalendar Object

| Function / Property                        | Description                                                                 |
| ------------------------------------------ | --------------------------------------------------------------------------- |
| SupportedYears                             | Lunar years covered by the tables (1900..2100)                              |
| MinSolarDate / MaxSolarDate                | Gregorian range `fromSolar` accepts: 1900-01-31 .. 2100-12-31               |
| fromSolar(date: DateValue): LunarDate      | Gregorian -> lunar; throws outside MinSolarDate .. MaxSolarDate             |
| toSolar(lunar: LunarDate): DateValue?      | Lunar -> Gregorian, null when the lunar date does not exist                 |
| changeYear(lunar, newYear): DateValue?     | Moves a lunar date to another lunar year, clamping month leap flag and day  |
| leapMonth(year): Int                       | The leap month of a lunar year, 0 when none                                 |
| daysInMonth(year, month[, isLeapMonth])    | Length (29 / 30) of a lunar month                                           |
| daysInLeapMonth(year) / daysInYear(year)   | Length of the leap month / the whole lunar year                             |
| dayName(day): String                       | 初一 .. 三十                                                                |
| format(date) / format(lunar): String       | e.g. `2026年九月廿一`                                                       |
| solarTerm(date): String?                   | Solar term on a Gregorian date (1970..2036, traditional-Chinese names)      |
| cellLabel(date): String                    | Calendar cell label: festival, solar term, month name or day name           |

### LunarDate Class

| Property Name | Type    | Description                                 |
| ------------- | ------- | ------------------------------------------- |
| year          | Int     | The lunar year                              |
| month         | Int     | The lunar month, 1-based                    |
| day           | Int     | The lunar day, 1-based                      |
| isLeapMonth   | Boolean | Whether the month is the leap (闰) month    |

## Advanced Usage

### Reading the Lunar Date

```kotlin
val lunar = LunarCalendar.fromSolar(date)
val solar = LunarCalendar.toSolar(LunarDate(year = 2026, month = 8, day = 15)) // Mid-Autumn
```
