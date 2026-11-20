# CalendarPicker

`CalendarPicker` is a month-grid date picker mirroring ColorOS's calendar picker family (COUICalendarDayPickerView / COUIDateMonthView / COUICalendarYearView). A header row holds the previous / next month buttons and the month-year label with an expand arrow; the body is a horizontally paged month grid. The selected day is a filled primary circle that grows from 80% while fading in (280ms), the previously selected day of the same month fades out (150ms), and today is ringed in the primary color. Days of the adjacent months fill the first and last rows and can be tapped as well. Every page change, the settle after a swipe included, is a 300ms COUIMoveEaseInterpolator scroll (COUICalendarViewPagerScroller), and the header switches to the new month as soon as the page is picked: at once for the month buttons, on release for a swipe. Tapping the month-year label flips the arrow upside down and turns it and the label primary at once (COUIRotateView swaps two drawables without a transition), fades the month buttons out (150ms) and, 120ms later, swaps the grid for a [DatePicker](./datepicker) wheel bounded by `minDate` .. `maxDate`, cross-fading 280ms in / 150ms out; collapsing fades the buttons back in over 280ms. The month buttons and the arrow press with a 24dp ripple-like circle in `pressBackground` (coui_canlendar_menu_bg); the label itself shows no press feedback. Like COUIChangeTextUtil.getG3FontSize, the header, weekday and day texts grow to at most 1.15x with the system font scale. The header and weekday labels follow the locale ("October 2026" / S M T W T F S in English, "2026年10月" / 日 一 二 三 四 五 六 in Chinese), the picker is at most 724dp wide, and every day cell is exposed to accessibility services as a clickable node.

Dates are represented by the plain `DateValue` data class, so no date/time library dependency is required.

<div style="position: relative; height: 420px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=calendarPicker" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.CalendarPicker
import io.github.suqi8.coui.kmp.basic.CalendarPickerDefaults
import io.github.suqi8.coui.kmp.basic.DateValue
```

## Basic Usage

```kotlin
var date by remember { mutableStateOf(DateValue(year = 2026, month = 10, day = 30)) }

CalendarPicker(
    value = date,
    onValueChange = { date = it }
)
```

## Properties

### CalendarPicker Properties

| Property Name    | Type                         | Description                                                  | Default Value                              | Required |
| ---------------- | ---------------------------- | ------------------------------------------------------------ | ------------------------------------------ | -------- |
| value            | DateValue                    | The selected date; clamped to minDate .. maxDate             | -                                          | Yes      |
| onValueChange    | (DateValue) -> Unit          | Called when a day is tapped or the wheel moves               | -                                          | Yes      |
| modifier         | Modifier                     | Modifier applied to the picker                               | Modifier                                   | No       |
| minDate          | DateValue                    | The earliest selectable date                                 | CalendarPickerDefaults.MinDate             | No       |
| maxDate          | DateValue                    | The latest selectable date                                   | CalendarPickerDefaults.MaxDate             | No       |
| today            | DateValue?                   | The date ringed as today; null for none                      | CalendarPickerDefaults.today()             | No       |
| firstDayOfWeek   | Int                          | First grid column, 1 = Sunday .. 7 = Saturday                | CalendarPickerDefaults.FirstDayOfWeek      | No       |
| monthYearLabel   | (Int, Int) -> String         | Converts a year and month (1..12) to the header label        | CalendarPickerDefaults.monthYearLabel()    | No       |
| weekdayLabel     | (Int) -> String              | Converts a day of week (1 = Sunday) to its column label      | CalendarPickerDefaults.weekdayLabel()      | No       |
| dayLabel         | (Int) -> String              | Converts a day of month to its cell label                    | CalendarPickerDefaults.DayLabel            | No       |
| colors           | CalendarPickerColors         | Color configuration                                          | CalendarPickerDefaults.calendarPickerColors() | No    |
| headerTextStyle  | TextStyle                    | Text style of the month-year label                           | COUITheme.textStyles.headlineXS             | No       |
| weekdayTextStyle | TextStyle                    | Text style of the weekday labels                             | COUITheme.textStyles.buttonM              | No       |
| dayTextStyle     | TextStyle                    | Text style of the day labels                                 | COUITheme.textStyles.headlineXS             | No       |
| insideMargin     | PaddingValues                | Padding around the whole picker                              | CalendarPickerDefaults.InsideMargin        | No       |

### CalendarPickerDefaults Object

| Property Name           | Type            | Description                                                 | Default Value                     |
| ----------------------- | --------------- | ----------------------------------------------------------- | --------------------------------- |
| MinDate                 | DateValue       | Earliest selectable date                                    | 1900-01-01                        |
| MaxDate                 | DateValue       | Latest selectable date                                      | 2100-12-31                        |
| FirstDayOfWeek          | Int             | First grid column (Sunday)                                  | 1                                 |
| InsideMargin            | PaddingValues   | Padding around the picker                                   | PaddingValues(vertical = 16.dp)   |
| HeaderHeight            | Dp              | Height of the header row                                    | 32.dp                             |
| HeaderPaddingHorizontal | Dp              | Horizontal padding of the header row                        | 24.dp                             |
| ChevronSize             | Dp              | Size of the chevron icons                                   | 24.dp                             |
| ChevronPadding          | Dp              | Padding of the chevron buttons                              | 4.dp                              |
| ArrowSize               | Dp              | Size of the expand arrow                                    | 16.dp                             |
| MonthPaddingHorizontal  | Dp              | Horizontal padding of the month grid                        | 16.dp                             |
| WeekdayHeight           | Dp              | Height of the weekday row                                   | 40.dp                             |
| DayHeight               | Dp              | Height of a week row                                        | 40.dp                             |
| FiveRowExtraHeight      | Dp              | Extra height of every row in a five-week month              | 8.dp                              |
| MonthHeight             | Dp              | Height of a month page                                      | 280.dp                            |
| DayCircleDiameter       | Dp              | Diameter of the selection / press / today circles           | 32.dp                             |
| TodayStrokeWidth        | Dp              | Stroke width of the today ring                              | 1.8.dp                            |
| YearViewOffset          | Dp              | Vertical offset of the expanded wheel                       | (-18).dp                          |
| MaxWidth                | Dp              | Maximum width of the picker (calendar_picker_max_width)     | 724.dp                            |
| DayLabel                | (Int) -> String | Plain day number                                            | { it.toString() }                 |

| Method Name      | Return Type          | Description                                                          |
| ---------------- | -------------------- | -------------------------------------------------------------------- |
| today()          | DateValue            | The system date, read once                                           |
| monthYearLabel() | (Int, Int) -> String | The locale's best "MMMMy" label ("October 2026", "2026年10月")       |
| weekdayLabel()   | (Int) -> String      | The locale's shortest weekday names (S M T W T F S, 日 一 二 …)      |

### CalendarPickerColors Class

| Property Name      | Type  | Description                                     |
| ------------------ | ----- | ----------------------------------------------- |
| headerColor        | Color | Month-year label and chevrons                   |
| expandedHeaderColor | Color | Month-year label while the wheel is open (primary) |
| arrowColor         | Color | Collapsed expand arrow                          |
| expandedArrowColor | Color | Expanded arrow                                  |
| weekdayColor       | Color | Weekday labels                                  |
| dayColor           | Color | Days of the shown month                         |
| disabledDayColor   | Color | Adjacent-month and out-of-range days            |
| selectedColor      | Color | Fill of the selected day                        |
| onSelectedColor    | Color | Label of the selected day                       |
| todayColor         | Color | Ring and label of today                         |
| pressedColor       | Color | Highlight of a pressed day                      |

## Advanced Usage

### Limited Range, Monday First

```kotlin
CalendarPicker(
    value = date,
    onValueChange = { date = it },
    minDate = DateValue(year = 2026, month = 9, day = 10),
    maxDate = DateValue(year = 2026, month = 12, day = 20),
    firstDayOfWeek = 2
)
```

### Custom Labels

The default labels follow the locale. Pass your own to override them:

```kotlin
CalendarPicker(
    value = date,
    onValueChange = { date = it },
    monthYearLabel = { year, month -> "$year.$month" },
    weekdayLabel = { listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")[it - 1] }
)
```
