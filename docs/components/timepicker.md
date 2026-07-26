# TimePicker

`TimePicker` is a basic interactive component in COUI used for selecting a time of day with hour and minute scroll wheels, mirroring ColorOS's COUITimeLimitPicker. It supports both 24-hour and 12-hour formats; in 12-hour mode an AM/PM wheel is added, placed before the hour wheel or after the minute wheel depending on locale convention. Optional unit labels (e.g. "h" / "min") can be drawn beside the selected row like the COUI unit text.

Times are represented by the plain `TimeValue` data class; the hour is always stored in 24-hour form (0..23) regardless of the display mode.

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.TimePicker
import io.github.suqi8.coui.kmp.basic.TimePickerDefaults
import io.github.suqi8.coui.kmp.basic.TimeValue
```

## Basic Usage

```kotlin
var time by remember { mutableStateOf(TimeValue(hour = 16, minute = 30)) }

TimePicker(
    value = time,
    onValueChange = { time = it }
)
```

## Component States

### 12-Hour Format

When `is24Hour` is false, hours display as 1..12 and an AM/PM wheel is shown after the minute wheel.

```kotlin
TimePicker(
    value = time,
    onValueChange = { time = it },
    is24Hour = false
)
```

### Disabled State

```kotlin
TimePicker(
    value = time,
    onValueChange = { time = it },
    enabled = false
)
```

## Properties

### TimePicker Properties

| Property Name     | Type                | Description                                                | Default Value                       | Required |
| ----------------- | ------------------- | ---------------------------------------------------------- | ----------------------------------- | -------- |
| value             | TimeValue           | Currently selected time (hour always in 24-hour form)      | -                                   | Yes      |
| onValueChange     | (TimeValue) -> Unit | Callback invoked when the selected time changes            | -                                   | Yes      |
| modifier          | Modifier            | Modifier applied to the picker                             | Modifier                            | No       |
| enabled           | Boolean             | Whether the picker is enabled for user interaction         | true                                | No       |
| is24Hour          | Boolean             | Whether the hour wheel uses the 24-hour format             | true                                | No       |
| amPmFirst         | Boolean             | Whether the AM/PM wheel is placed before the hour wheel    | false                               | No       |
| amLabel           | String              | Display string for AM on the AM/PM wheel                   | TimePickerDefaults.AmLabel          | No       |
| pmLabel           | String              | Display string for PM on the AM/PM wheel                   | TimePickerDefaults.PmLabel          | No       |
| hourUnit          | String              | Unit label beside the selected hour; empty to hide         | TimePickerDefaults.HourUnit         | No       |
| minuteUnit        | String              | Unit label beside the selected minute; empty to hide       | TimePickerDefaults.MinuteUnit       | No       |
| hourColumnWidth   | Dp                  | Width of the hour column                                   | TimePickerDefaults.HourColumnWidth  | No       |
| minuteColumnWidth | Dp                  | Width of the minute column                                 | TimePickerDefaults.MinuteColumnWidth | No      |
| amPmColumnWidth   | Dp                  | Width of the AM/PM column                                  | TimePickerDefaults.AmPmColumnWidth  | No       |
| colors            | NumberPickerColors  | Color configuration of the wheels                          | NumberPickerDefaults.colors()       | No       |
| textStyle         | TextStyle           | Text style for the wheel items                             | COUITheme.textStyles.title3        | No       |
| itemHeight        | Dp                  | The height of each wheel item                              | NumberPickerDefaults.ItemHeight     | No       |
| insideMargin      | PaddingValues       | Padding around the wheel columns                           | TimePickerDefaults.InsideMargin     | No       |

### TimeValue Class

| Property Name | Type | Description                              |
| ------------- | ---- | ---------------------------------------- |
| hour          | Int  | The hour of the day in 24-hour form (0..23) |
| minute        | Int  | The minute of the hour (0..59)           |

### TimePickerDefaults Object

| Property Name     | Type          | Description                                       | Default Value                   |
| ----------------- | ------------- | ------------------------------------------------- | ------------------------------- |
| HourColumnWidth   | Dp            | Default width of the hour column                  | 76.dp                           |
| MinuteColumnWidth | Dp            | Default width of the minute column                | 76.dp                           |
| AmPmColumnWidth   | Dp            | Default width of the AM/PM column                 | 62.dp                           |
| InsideMargin      | PaddingValues | Default padding around the wheel columns          | PaddingValues(vertical = 12.dp) |
| UnitTextOffset    | Dp            | Offset of the unit label from the wheel center    | 13.dp                           |
| UnitFontSize      | TextUnit      | Font size of the unit label                       | 14.sp                           |
| AmLabel           | String        | Default AM label                                  | "AM"                            |
| PmLabel           | String        | Default PM label                                  | "PM"                            |
| HourUnit          | String        | Default hour unit label (hidden)                  | ""                              |
| MinuteUnit        | String        | Default minute unit label (hidden)                | ""                              |

## Advanced Usage

### Unit Labels

Draw unit labels beside the selected hour and minute, like the COUI picker:

```kotlin
TimePicker(
    value = time,
    onValueChange = { time = it },
    hourUnit = "h",
    minuteUnit = "min"
)
```

### Localized 12-Hour Picker

For locales whose time pattern starts with the day period (e.g. Chinese), place the AM/PM wheel first and localize its labels:

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

### Custom Colors

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
