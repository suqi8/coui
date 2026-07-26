# DatePicker

`DatePicker` is a basic interactive component in COUI used for selecting a calendar date with three vertical scroll wheels (year / month / day), mirroring ColorOS's COUIDatePicker. The day range automatically follows the selected year and month (leap years included), and the day is clamped when a shorter month is selected. All columns support infinite scrolling.

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
| yearRange        | IntRange             | The selectable year range                            | DatePickerDefaults.YearRange      | No       |
| yearLabel        | (Int) -> String      | Converts a year to its display string                | DatePickerDefaults.YearLabel      | No       |
| monthLabel       | (Int) -> String      | Converts a month (1..12) to its display string       | DatePickerDefaults.MonthLabel     | No       |
| dayLabel         | (Int) -> String      | Converts a day of month to its display string        | DatePickerDefaults.DayLabel       | No       |
| yearColumnWidth  | Dp                   | Width of the year column                             | DatePickerDefaults.YearColumnWidth | No      |
| monthColumnWidth | Dp                   | Width of the month column                            | DatePickerDefaults.MonthColumnWidth | No     |
| dayColumnWidth   | Dp                   | Width of the day column                              | DatePickerDefaults.DayColumnWidth | No       |
| colors           | NumberPickerColors   | Color configuration of the wheels                    | NumberPickerDefaults.colors()     | No       |
| textStyle        | TextStyle            | Text style for the wheel items                       | COUITheme.textStyles.title3      | No       |
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
| YearRange        | IntRange        | Default selectable year range              | 1900..2100                |
| YearColumnWidth  | Dp              | Default width of the year column           | 76.dp                     |
| MonthColumnWidth | Dp              | Default width of the month column          | 96.dp                     |
| DayColumnWidth   | Dp              | Default width of the day column            | 62.dp                     |
| InsideMargin     | PaddingValues   | Default padding around the wheel columns   | PaddingValues(vertical = 12.dp) |
| YearLabel        | (Int) -> String | Plain year number                          | { it.toString() }         |
| MonthLabel       | (Int) -> String | Abbreviated English month names            | "Jan".."Dec"              |
| DayLabel         | (Int) -> String | Zero-padded two digits                     | "01".."31"                |

## Advanced Usage

### Custom Year Range

```kotlin
DatePicker(
    value = date,
    onValueChange = { date = it },
    yearRange = 2000..2030
)
```

### Localized Labels

Pass custom label functions to localize the wheel items. For example, the Chinese COUI style embeds the unit into each item:

```kotlin
DatePicker(
    value = date,
    onValueChange = { date = it },
    yearLabel = { "${it}年" },
    monthLabel = { "${it}月" },
    dayLabel = { "${it}日" }
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
