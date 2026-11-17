# DateTimePicker

`DateTimePicker` selects a date and a time of day with one date wheel and hour / minute wheels, mirroring ColorOS's COUITimePicker. The date wheel shows the weekday, month and day ("Today" for the current date) and does not wrap; the hour and minute wheels wrap. In 12-hour mode an AM/PM wheel is added, and scrolling the hour wheel onto 12 flips AM/PM as COUI does.

The wheels follow [NumberPicker](./numberpicker). The date column is 150dp wide (174dp outside Chinese and English), the time columns 62dp, and all columns shrink proportionally when the picker is narrower than their sum. The two hairlines around the selected row are inset 16dp in 24-hour mode and run edge to edge in 12-hour mode.

<div style="position: relative; height: 320px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=dateTimePicker" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

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
| amPmFirst       | Boolean                  | Whether the AM/PM wheel sits before the hour wheel                 | TimePickerDefaults.localeAmPmFirst()       | No       |
| minuteFiveStep  | Boolean                  | Whether the minute wheel steps by five minutes                     | false                                      | No       |
| yearRange       | IntRange?                | Years the date wheel covers; null = 50 years before to 150 after the first value | null                         | No       |
| dateLabel       | (DateValue) -> String    | Converts a date to its display string                              | DateTimePickerDefaults.dateLabel()         | No       |
| todayLabel      | String                   | Display string of today's row                                      | DateTimePickerDefaults.todayLabel()        | No       |
| amLabel         | String                   | Display string for AM                                              | TimePickerDefaults.amLabel()               | No       |
| pmLabel         | String                   | Display string for PM                                              | TimePickerDefaults.pmLabel()               | No       |
| hourUnit        | String                   | Unit label beside the selected hour (COUI setUnitVisible); empty to hide | TimePickerDefaults.HourUnit          | No       |
| minuteUnit      | String                   | Unit label beside the selected minute; empty to hide               | TimePickerDefaults.MinuteUnit              | No       |
| dateColumnWidth | Dp                       | Width of the date column                                           | DateTimePickerDefaults.dateColumnWidth()   | No       |
| timeColumnWidth | Dp                       | Width of the hour, minute and AM/PM columns                        | DateTimePickerDefaults.TimeColumnWidth     | No       |
| colors          | NumberPickerColors       | Color configuration of the wheels                                  | NumberPickerDefaults.colors()              | No       |
| dividerColor    | Color                    | Color of the two hairlines around the selected row                 | COUITheme.colorScheme.divider          | No       |
| textStyle       | TextStyle                | Text style for the wheel items                                     | COUITheme.textStyles.headlineM                | No       |
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
| dateColumnWidth()   | Dp                    | DateColumnWidth for zh / en, WideDateColumnWidth otherwise | -                         |
| dateLabel()         | (DateValue) -> String | "10月23日 周四" in Chinese, the locale's abbreviated weekday, month and day otherwise ("Thu, Oct 23") | - |
| todayLabel()        | String                | The locale's "Today" ("今天" in Chinese)             | -                               |

The columns shrink by `width / (date + 3 × time column)`, counting the AM/PM column even in 24-hour mode as COUITimePicker does, and the picker is at most 640dp wide. The AM/PM wheel follows the locale's order by default (before the hour in Chinese, after the minute in English).

## Advanced Usage

### Custom Labels

The default labels follow the locale. Pass your own to override them:

```kotlin
DateTimePicker(
    value = value,
    onValueChange = { value = it },
    is24Hour = false,
    dateLabel = { "${it.month}/${it.day}" },
    todayLabel = "Now",
    hourUnit = "h",
    minuteUnit = "min"
)
```
