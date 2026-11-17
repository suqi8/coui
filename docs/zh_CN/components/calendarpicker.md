# CalendarPicker

`CalendarPicker` 是月历网格日期选择器，对应 ColorOS 的日历选择器家族（COUICalendarDayPickerView / COUIDateMonthView / COUICalendarYearView）。顶部一行包含上一月 / 下一月按钮，以及带展开箭头的年月标题；主体是可横向翻页的月份网格。选中日为主色实心圆，从 80% 放大并淡入（280ms），同月中之前选中的日期淡出（150ms），今天以主色圆环标记。首末行用相邻月份的日期补齐，同样可以点击。点击年月标题会旋转箭头、标题变为主色、翻页按钮 150ms 淡出，并在 120ms 后以 280ms 淡入 / 150ms 淡出切换为受 `minDate` .. `maxDate` 约束的 [DatePicker](./datepicker) 滚轮；收起时按钮 280ms 淡入。标题与星期文字随语言变化（英文 "October 2026" / S M T W T F S，中文 "2026年10月" / 日 一 二 三 四 五 六），选择器最大宽度 724dp，每个日期格都作为可点击节点暴露给无障碍服务。

日期由纯数据类 `DateValue` 表示，不依赖任何日期时间库。

<div style="position: relative; height: 420px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=calendarPicker" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.CalendarPicker
import io.github.suqi8.coui.kmp.basic.CalendarPickerDefaults
import io.github.suqi8.coui.kmp.basic.DateValue
```

## 基本用法

```kotlin
var date by remember { mutableStateOf(DateValue(year = 2026, month = 10, day = 30)) }

CalendarPicker(
    value = date,
    onValueChange = { date = it }
)
```

## 属性

### CalendarPicker 属性

| 属性名           | 类型                 | 说明                                         | 默认值                                     | 必须 |
| ---------------- | -------------------- | -------------------------------------------- | ------------------------------------------ | ---- |
| value            | DateValue            | 选中日期，会收敛到 minDate .. maxDate        | -                                          | 是   |
| onValueChange    | (DateValue) -> Unit  | 点击日期或滚动滚轮时回调                     | -                                          | 是   |
| modifier         | Modifier             | 应用于选择器的修饰符                         | Modifier                                   | 否   |
| minDate          | DateValue            | 最早可选日期                                 | CalendarPickerDefaults.MinDate             | 否   |
| maxDate          | DateValue            | 最晚可选日期                                 | CalendarPickerDefaults.MaxDate             | 否   |
| today            | DateValue?           | 标记为今天的日期，null 表示不标记            | CalendarPickerDefaults.today()             | 否   |
| firstDayOfWeek   | Int                  | 网格首列，1 = 周日 .. 7 = 周六               | CalendarPickerDefaults.FirstDayOfWeek      | 否   |
| monthYearLabel   | (Int, Int) -> String | 将年与月（1..12）转换为标题文字              | CalendarPickerDefaults.monthYearLabel()    | 否   |
| weekdayLabel     | (Int) -> String      | 将星期（1 = 周日）转换为列标题               | CalendarPickerDefaults.weekdayLabel()      | 否   |
| dayLabel         | (Int) -> String      | 将日转换为单元格文字                         | CalendarPickerDefaults.DayLabel            | 否   |
| colors           | CalendarPickerColors | 颜色配置                                     | CalendarPickerDefaults.calendarPickerColors() | 否 |
| headerTextStyle  | TextStyle            | 年月标题的文字样式                           | COUITheme.textStyles.headlineXS             | 否   |
| weekdayTextStyle | TextStyle            | 星期标题的文字样式                           | COUITheme.textStyles.buttonM              | 否   |
| dayTextStyle     | TextStyle            | 日期文字样式                                 | COUITheme.textStyles.headlineXS             | 否   |
| insideMargin     | PaddingValues        | 整个选择器的内边距                           | CalendarPickerDefaults.InsideMargin        | 否   |

### CalendarPickerDefaults 对象

| 属性名                  | 类型                 | 说明                               | 默认值                          |
| ----------------------- | -------------------- | ---------------------------------- | ------------------------------- |
| MinDate                 | DateValue            | 最早可选日期                       | 1900-01-01                      |
| MaxDate                 | DateValue            | 最晚可选日期                       | 2100-12-31                      |
| FirstDayOfWeek          | Int                  | 网格首列（周日）                   | 1                               |
| InsideMargin            | PaddingValues        | 选择器内边距                       | PaddingValues(vertical = 16.dp) |
| HeaderHeight            | Dp                   | 标题行高度                         | 32.dp                           |
| HeaderPaddingHorizontal | Dp                   | 标题行水平内边距                   | 24.dp                           |
| ChevronSize             | Dp                   | 翻页箭头图标尺寸                   | 24.dp                           |
| ChevronPadding          | Dp                   | 翻页按钮内边距                     | 4.dp                            |
| ArrowSize               | Dp                   | 展开箭头尺寸                       | 16.dp                           |
| MonthPaddingHorizontal  | Dp                   | 月份网格水平内边距                 | 16.dp                           |
| WeekdayHeight           | Dp                   | 星期行高度                         | 40.dp                           |
| DayHeight               | Dp                   | 周行高度                           | 40.dp                           |
| FiveRowExtraHeight      | Dp                   | 五周月份每行的额外高度             | 8.dp                            |
| MonthHeight             | Dp                   | 月份页高度                         | 280.dp                          |
| DayCircleDiameter       | Dp                   | 选中 / 按压 / 今天圆的直径         | 32.dp                           |
| TodayStrokeWidth        | Dp                   | 今天圆环的描边宽度                 | 1.8.dp                          |
| YearViewOffset          | Dp                   | 展开滚轮的垂直偏移                 | (-18).dp                        |
| MaxWidth                | Dp                   | 选择器最大宽度（calendar_picker_max_width） | 724.dp                   |
| DayLabel                | (Int) -> String      | 纯数字日期                         | { it.toString() }               |

| 方法名           | 返回类型             | 说明                                                   |
| ---------------- | -------------------- | ------------------------------------------------------ |
| today()          | DateValue            | 读取一次系统日期                                       |
| monthYearLabel() | (Int, Int) -> String | 当前语言的最佳 "MMMMy" 标题（"October 2026"、"2026年10月"） |
| weekdayLabel()   | (Int) -> String      | 当前语言最短的星期名（S M T W T F S、日 一 二 …）      |

### CalendarPickerColors 类

| 属性名             | 类型  | 说明                         |
| ------------------ | ----- | ---------------------------- |
| headerColor        | Color | 年月标题与翻页箭头           |
| expandedHeaderColor | Color | 展开滚轮时的年月标题（主色） |
| arrowColor         | Color | 收起状态的展开箭头           |
| expandedArrowColor | Color | 展开状态的箭头               |
| weekdayColor       | Color | 星期标题                     |
| dayColor           | Color | 当月日期                     |
| disabledDayColor   | Color | 相邻月份与超出范围的日期     |
| selectedColor      | Color | 选中日的填充                 |
| onSelectedColor    | Color | 选中日的文字                 |
| todayColor         | Color | 今天的圆环与文字             |
| pressedColor       | Color | 按压日期的高亮               |

## 进阶用法

### 限定范围、周一为首列

```kotlin
CalendarPicker(
    value = date,
    onValueChange = { date = it },
    minDate = DateValue(year = 2026, month = 9, day = 10),
    maxDate = DateValue(year = 2026, month = 12, day = 20),
    firstDayOfWeek = 2
)
```

### 自定义文字

默认文字已随语言变化，需要时可传入自己的文字：

```kotlin
CalendarPicker(
    value = date,
    onValueChange = { date = it },
    monthYearLabel = { year, month -> "$year.$month" },
    weekdayLabel = { listOf("周日", "周一", "周二", "周三", "周四", "周五", "周六")[it - 1] }
)
```
