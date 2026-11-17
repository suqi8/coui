# TimePicker

`TimePicker` is a basic interactive component in COUI used for selecting a time of day with hour and minute scroll wheels, mirroring ColorOS's COUITimeLimitPicker. It supports both 24-hour and 12-hour formats; in 12-hour mode an AM/PM wheel is added, placed before the hour wheel or after the minute wheel depending on locale convention. Optional unit labels (e.g. "h" / "min") are drawn after the selected value in 14dp text, on its baseline lifted by 2dp, like the COUI unit text. Two hairlines in the `divider` color frame the selected row at the center ± 24dp, inset 16dp from both sides; the AM/PM wheel uses 18dp unselected text, and holding a side row of the minute wheel steps every 100ms. The wheels themselves follow [NumberPicker](./numberpicker).

Times are represented by the plain `TimeValue` data class; the hour is always stored in 24-hour form (0..23) regardless of the display mode.

<div style="position: relative; height: 320px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=timePicker" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.TimePicker
import io.github.suqi8.coui.kmp.basic.TimePickerDefaults
import io.github.suqi8.coui.kmp.basic.NumberPickerDefaults
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
| amPmFirst         | Boolean             | Whether the AM/PM wheel is placed before the hour wheel    | TimePickerDefaults.localeAmPmFirst() | No      |
| amLabel           | String              | Display string for AM on the AM/PM wheel                   | TimePickerDefaults.amLabel()        | No       |
| pmLabel           | String              | Display string for PM on the AM/PM wheel                   | TimePickerDefaults.pmLabel()        | No       |
| hourUnit          | String              | Unit label beside the selected hour; empty to hide         | TimePickerDefaults.HourUnit         | No       |
| minuteUnit        | String              | Unit label beside the selected minute; empty to hide       | TimePickerDefaults.MinuteUnit       | No       |
| visibleItemCount  | Int                 | Visible rows of every wheel (COUI setRowNumber); odd, ≥ 3  | 3                                   | No       |
| hourColumnWidth   | Dp                  | Width of the hour column                                   | TimePickerDefaults.HourColumnWidth  | No       |
| minuteColumnWidth | Dp                  | Width of the minute column                                 | TimePickerDefaults.MinuteColumnWidth | No      |
| amPmColumnWidth   | Dp                  | Width of the AM/PM column                                  | TimePickerDefaults.AmPmColumnWidth  | No       |
| colors            | NumberPickerColors  | Color configuration of the wheels                          | NumberPickerDefaults.colors()       | No       |
| dividerColor      | Color               | Color of the two hairlines around the selected row         | COUITheme.colorScheme.divider   | No       |
| textStyle         | TextStyle           | Text style for the wheel items                             | COUITheme.textStyles.headlineM        | No       |
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
| AmPmUnselectedTextSize | Dp       | Unselected text size of the AM/PM wheel           | 18.dp                           |
| MinuteStepInterval | Long         | Minute auto-repeat interval (ms) while held       | 100L                            |
| HourUnit          | String        | Default hour unit label (hidden)                  | ""                              |
| MinuteUnit        | String        | Default minute unit label (hidden)                | ""                              |

| Method Name       | Return Type | Description                                                                       |
| ----------------- | ----------- | --------------------------------------------------------------------------------- |
| amLabel()         | String      | The locale's AM label ("AM", "上午")                                              |
| pmLabel()         | String      | The locale's PM label ("PM", "下午")                                              |
| localeAmPmFirst() | Boolean     | Whether the locale's best "hm" pattern puts AM/PM first (true for Chinese)        |

The picker is at most 640dp wide (`NumberPickerDefaults.PickersMaxWidth`).

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

The AM/PM labels and the position of the AM/PM wheel follow the locale by default (before the hour in Chinese, after the minute in English). Override them when needed:

```kotlin
TimePicker(
    value = time,
    onValueChange = { time = it },
    is24Hour = false,
    amPmFirst = true,
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
