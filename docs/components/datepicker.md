# DatePicker

`DatePicker` is a basic interactive component in COUI used for selecting a calendar date with three vertical scroll wheels (year / month / day), mirroring ColorOS's COUIDatePicker. The day range automatically follows the selected year and month (leap years included), and the day is clamped when a shorter month is selected. The date stays within `minDate` .. `maxDate`: as in COUIDatePicker.updateSpinners, while the selection is in the first or last year (or month) the month (or day) wheel only offers the reachable values and stops wrapping unless it still covers the whole year (or month). The default labels follow the locale like COUIDatePicker ("2026 / Jan / 01" in English, "2026年 / 1月 / 1日" in Chinese), and the picker is at most 640dp wide. Two hairlines in the `divider` color frame the selected row at the center ± 24dp, inset 16dp from both sides. Holding a row above or below the center steps the year and day every 100ms and the month every 200ms, as in COUIDatePicker. The wheels follow the locale's date field order (e.g. month / day / year in US English, year / month / day in Chinese, day / month / year in German), like COUIDatePicker.reorderSpinners. The wheels themselves follow [NumberPicker](./numberpicker).

Dates are represented by the plain `DateValue` data class, so no date/time library dependency is required.

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.DatePicker
import io.github.suqi8.coui.kmp.basic.DatePickerDefaults
import io.github.suqi8.coui.kmp.basic.DateValue
import io.github.suqi8.coui.kmp.basic.NumberPickerDefaults
```

## Basic Usage

```kotlin
var date by remember { mutableStateOf(DateValue(year = 2026, month = 7, day = 26)) }

DatePicker(
    value = date,
    onValueChange = { date = it }
)
```

## Component States

### Disabled State

```kotlin
DatePicker(
    value = date,
    onValueChange = { date = it },
    enabled = false
)
```

## Properties

### DatePicker Properties

| Property Name    | Type                 | Description                                          | Default Value                     | Required |
| ---------------- | -------------------- | ---------------------------------------------------- | --------------------------------- | -------- |
| value            | DateValue            | Currently selected date. Out-of-range fields coerced | -                                 | Yes      |
| onValueChange    | (DateValue) -> Unit  | Callback invoked when the selected date changes      | -                                 | Yes      |
| modifier         | Modifier             | Modifier applied to the picker                       | Modifier                          | No       |
| enabled          | Boolean              | Whether the picker is enabled for user interaction   | true                              | No       |
| yearIgnorable    | Boolean              | Whether the year can be left out (placeholder row)   | false                             | No       |
| minDate          | DateValue            | The earliest selectable date                         | DatePickerDefaults.MinDate        | No       |
| maxDate          | DateValue            | The latest selectable date                           | DatePickerDefaults.MaxDate        | No       |
| yearLabel        | (Int) -> String      | Converts a year to its display string                | DatePickerDefaults.yearLabel()    | No       |
| monthLabel       | (Int) -> String      | Converts a month (1..12) to its display string       | DatePickerDefaults.monthLabel()   | No       |
| dayLabel         | (Int) -> String      | Converts a day of month to its display string        | DatePickerDefaults.dayLabel()     | No       |
| fieldOrder       | DateFieldOrder       | Left-to-right order of the wheels (mirrored in RTL)  | DatePickerDefaults.localeFieldOrder() | No   |
| yearColumnWidth  | Dp                   | Width of the year column                             | DatePickerDefaults.YearColumnWidth | No      |
| monthColumnWidth | Dp                   | Width of the month column                            | DatePickerDefaults.MonthColumnWidth | No     |
| dayColumnWidth   | Dp                   | Width of the day column                              | DatePickerDefaults.DayColumnWidth | No       |
| colors           | NumberPickerColors   | Color configuration of the wheels                    | NumberPickerDefaults.colors()     | No       |
| dividerColor     | Color                | Color of the two hairlines around the selected row   | COUITheme.colorScheme.divider | No       |
| textStyle        | TextStyle            | Text style for the wheel items                       | COUITheme.textStyles.headlineM      | No       |
| itemHeight       | Dp                   | The height of each wheel item                        | NumberPickerDefaults.ItemHeight   | No       |
| insideMargin     | PaddingValues        | Padding around the wheel columns                     | DatePickerDefaults.InsideMargin   | No       |

### DateValue Class

| Property Name | Type | Description                                     |
| ------------- | ---- | ----------------------------------------------- |
| year          | Int  | The calendar year (e.g. 2026)                   |
| month         | Int  | The month of the year, 1-based (1 = January)    |
| day           | Int  | The day of the month, 1-based                   |

### DatePickerDefaults Object

| Property Name    | Type            | Description                                | Default Value             |
| ---------------- | --------------- | ------------------------------------------ | ------------------------- |
| MinDate          | DateValue       | Default earliest date                      | DateValue(1900, 1, 1)     |
| MaxDate          | DateValue       | Default latest date                        | DateValue(2100, 12, 31)   |
| IgnoredYear      | Int             | Year reported when the year is left out    | Int.MIN_VALUE             |
| YearColumnWidth  | Dp              | Default width of the year column           | 76.dp                     |
| MonthColumnWidth | Dp              | Default width of the month column          | 96.dp                     |
| DayColumnWidth   | Dp              | Default width of the day column            | 62.dp                     |
| InsideMargin     | PaddingValues   | Default padding around the wheel columns   | PaddingValues(vertical = 12.dp) |
| YearStepInterval | Long            | Year auto-repeat interval (ms) while held  | 100L                      |
| MonthStepInterval | Long           | Month auto-repeat interval (ms) while held | 200L                      |
| DayStepInterval  | Long            | Day auto-repeat interval (ms) while held   | 100L                      |

| Method Name        | Return Type     | Description                                                                 |
| ------------------ | --------------- | --------------------------------------------------------------------------- |
| yearLabel()        | (Int) -> String | "2026年" in Chinese, the plain number otherwise                             |
| monthLabel()       | (Int) -> String | The locale's abbreviated month name ("Jan", "1月")                          |
| dayLabel()         | (Int) -> String | "5日" in Chinese, two zero-padded digits otherwise                          |
| localeFieldOrder() | DateFieldOrder  | The locale's wheel order                                                    |

`DatePickerDefaults.localeFieldOrder()` reads the platform's best "yyyyMMMMdd" pattern: Android `DateFormat.getBestDateTimePattern`, the JDK long date pattern on desktop, `NSDateFormatter.dateFormatFromTemplate` on iOS / macOS and `Intl.DateTimeFormat` on the web. It falls back to `DateFieldOrder.YearMonthDay`.

### DateFieldOrder Enum

`YearMonthDay`, `YearDayMonth`, `MonthDayYear`, `MonthYearDay`, `DayMonthYear`, `DayYearMonth`. Each entry exposes `fields: List<DateField>`; `DateFieldOrder.fromPattern(pattern)` reads the order from a date pattern.

## Advanced Usage

### Fixed Column Order

```kotlin
DatePicker(
    value = date,
    onValueChange = { date = it },
    fieldOrder = DateFieldOrder.YearMonthDay
)
```

### Date Range

```kotlin
DatePicker(
    value = date,
    onValueChange = { date = it },
    minDate = DateValue(2026, 3, 15),
    maxDate = DateValue(2027, 8, 20)
)
```

### Optional Year

With `yearIgnorable`, the year wheel gets a placeholder row after the last year (COUIDatePicker `couiYearIgnorable`). Selecting it reports `DatePickerDefaults.IgnoredYear` as the year, and 29 February stays selectable:

```kotlin
var birthday by remember { mutableStateOf(DateValue(DatePickerDefaults.IgnoredYear, 2, 29)) }

DatePicker(
    value = birthday,
    onValueChange = { birthday = it },
    yearIgnorable = true
)
```

### Custom Labels

The default labels already follow the locale. Pass label functions to override them:

```kotlin
DatePicker(
    value = date,
    onValueChange = { date = it },
    monthLabel = { "M$it" }
)
```

### Custom Colors

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
