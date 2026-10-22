# DateTimePicker

`DateTimePicker` selects a date and a time of day with one date wheel and hour / minute wheels, mirroring ColorOS's COUITimePicker. The date wheel shows the weekday, month and day ("Today" for the current date) and does not wrap; the hour and minute wheels wrap. In 12-hour mode an AM/PM wheel is added, and scrolling the hour wheel onto 12 flips AM/PM as COUI does.

The wheels follow [NumberPicker](./numberpicker). The date column is 150dp wide (174dp outside Chinese and English), the time columns 62dp, and all columns shrink proportionally when the picker is narrower than their sum. The two hairlines around the selected row are inset 16dp in 24-hour mode and run edge to edge in 12-hour mode.

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.DateTimePicker
import io.github.suqi8.coui.kmp.basic.DateTimePickerDefaults
import io.github.suqi8.coui.kmp.basic.DateTimeValue
import io.github.suqi8.coui.kmp.basic.DateValue
import io.github.suqi8.coui.kmp.basic.TimeValue
```

## Basic Usage

```kotlin
var value by remember {
    mutableStateOf(DateTimeValue(DateValue(2026, 10, 23), TimeValue(hour = 16, minute = 30)))
}

DateTimePicker(
    value = value,
    onValueChange = { value = it }
)
```

## Component States

### 12-Hour Format

```kotlin
DateTimePicker(
    value = value,
    onValueChange = { value = it },
    is24Hour = false
)
```

### Five-Minute Steps

```kotlin
DateTimePicker(
    value = value,
    onValueChange = { value = it },
    minuteFiveStep = true
)
```

## Properties

### DateTimePicker Properties

| Property Name   | Type                     | Description                                                        | Default Value                              | Required |
| --------------- | ------------------------ | ------------------------------------------------------------------ | ------------------------------------------ | -------- |
| value           | DateTimeValue            | Currently selected date and time                                   | -                                          | Yes      |
| onValueChange   | (DateTimeValue) -> Unit  | Callback invoked when the date or time changes                     | -                                          | Yes      |
| modifier        | Modifier                 | Modifier applied to the picker                                     | Modifier                                   | No       |
| enabled         | Boolean                  | Whether the picker is enabled for user interaction                 | true                                       | No       |
| is24Hour        | Boolean                  | Whether the hour wheel uses the 24-hour format                     | true                                       | No       |
| amPmFirst       | Boolean                  | Whether the AM/PM wheel sits before the hour wheel                 | true                                       | No       |
| minuteFiveStep  | Boolean                  | Whether the minute wheel steps by five minutes                     | false                                      | No       |
| yearRange       | IntRange?                | Years the date wheel covers; null = 50 years before to 150 after the first value | null                         | No       |
| dateLabel       | (DateValue) -> String    | Converts a date to its display string                              | DateTimePickerDefaults.DateLabel           | No       |
| todayLabel      | String                   | Display string of today's row                                      | DateTimePickerDefaults.TodayLabel          | No       |
| amLabel         | String                   | Display string for AM                                              | TimePickerDefaults.AmLabel                 | No       |
| pmLabel         | String                   | Display string for PM                                              | TimePickerDefaults.PmLabel                 | No       |
| dateColumnWidth | Dp                       | Width of the date column                                           | DateTimePickerDefaults.dateColumnWidth()   | No       |
| timeColumnWidth | Dp                       | Width of the hour, minute and AM/PM columns                        | DateTimePickerDefaults.TimeColumnWidth     | No       |
| colors          | NumberPickerColors       | Color configuration of the wheels                                  | NumberPickerDefaults.colors()              | No       |
| dividerColor    | Color                    | Color of the two hairlines around the selected row                 | COUITheme.colorScheme.dividerLine          | No       |
| textStyle       | TextStyle                | Text style for the wheel items                                     | COUITheme.textStyles.title3                | No       |
| itemHeight      | Dp                       | The height of each wheel item                                      | NumberPickerDefaults.ItemHeight            | No       |
| insideMargin    | PaddingValues            | Padding around the wheel columns                                   | DateTimePickerDefaults.InsideMargin        | No       |

### DateTimeValue Class

| Property Name | Type      | Description                                   |
| ------------- | --------- | --------------------------------------------- |
| date          | DateValue | The calendar date                             |
| time          | TimeValue | The time of day (hour always in 24-hour form) |

`DateValue.dayOfWeek` returns the ISO day of the week (1 = Monday .. 7 = Sunday), handy for custom date labels.

### DateTimePickerDefaults Object

| Property Name       | Type                  | Description                                          | Default Value                   |
| ------------------- | --------------------- | ---------------------------------------------------- | ------------------------------- |
| DateColumnWidth     | Dp                    | Date column width in Chinese and English             | 150.dp                          |
| WideDateColumnWidth | Dp                    | Date column width in other languages                 | 174.dp                          |
| TimeColumnWidth     | Dp                    | Width of the hour, minute and AM/PM columns          | 62.dp                           |
| InsideMargin        | PaddingValues         | Padding around the wheel columns                     | PaddingValues(vertical = 12.dp) |
| TodayLabel          | String                | Label of today's row                                 | "Today"                         |
| DateLabel           | (DateValue) -> String | English weekday, month and day                       | "Thu, Oct 23"                   |
| dateColumnWidth()   | Dp                    | DateColumnWidth for zh / en, WideDateColumnWidth otherwise | -                         |

## Advanced Usage

### Chinese Labels

COUI shows "10月23日 周五" and "今天" in Chinese:

```kotlin
val weekdays = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")

DateTimePicker(
    value = value,
    onValueChange = { value = it },
    is24Hour = false,
    dateLabel = { "${it.month}月${it.day.toString().padStart(2, '0')}日 ${weekdays[it.dayOfWeek - 1]}" },
    todayLabel = "今天",
    amLabel = "上午",
    pmLabel = "下午"
)
```
