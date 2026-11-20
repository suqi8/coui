# DateTimePicker

`DateTimePicker` 用一个日期滚轮加时 / 分滚轮选择日期和时间，对应 ColorOS 的 COUITimePicker。日期滚轮显示星期、月和日（当天显示「今天」），不循环；时、分滚轮循环滚动。12 小时制下会增加上午/下午滚轮，并与 COUI 一样在小时滚到 12 时切换上午/下午。

滚轮本身的行为见 [NumberPicker](./numberpicker)。日期列宽 150dp（中文、英文以外的语言为 174dp），时间列宽 62dp；选择器比各列总宽窄时，所有列按比例缩小。选中行上下的两条细线在 24 小时制下左右内缩 16dp，12 小时制下通栏绘制。与 coui_time_picker.xml 一致，RTL 布局下各滚轮仍保持从左到右的顺序（只有单位标签镜像），首末两列的触摸区域会延伸到两侧留白，在选择器任意位置拖动都能滚动它们。

<div style="position: relative; height: 320px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=dateTimePicker" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.DateTimePicker
import io.github.suqi8.coui.kmp.basic.DateTimePickerDefaults
import io.github.suqi8.coui.kmp.basic.DateTimeValue
import io.github.suqi8.coui.kmp.basic.DateValue
import io.github.suqi8.coui.kmp.basic.TimeValue
```

## 基本用法

```kotlin
var value by remember {
    mutableStateOf(DateTimeValue(DateValue(2026, 10, 23), TimeValue(hour = 16, minute = 30)))
}

DateTimePicker(
    value = value,
    onValueChange = { value = it }
)
```

## 组件状态

### 12 小时制

```kotlin
DateTimePicker(
    value = value,
    onValueChange = { value = it },
    is24Hour = false
)
```

### 五分钟步进

```kotlin
DateTimePicker(
    value = value,
    onValueChange = { value = it },
    minuteFiveStep = true
)
```

## 属性

### DateTimePicker 属性

| 属性名          | 类型                     | 说明                                                   | 默认值                                     | 是否必须 |
| --------------- | ------------------------ | ------------------------------------------------------ | ------------------------------------------ | -------- |
| value           | DateTimeValue            | 当前选中的日期和时间                                   | -                                          | 是       |
| onValueChange   | (DateTimeValue) -> Unit  | 日期或时间变化时的回调                                 | -                                          | 是       |
| modifier        | Modifier                 | 应用于选择器的修饰符                                   | Modifier                                   | 否       |
| enabled         | Boolean                  | 是否启用用户交互                                       | true                                       | 否       |
| is24Hour        | Boolean                  | 小时滚轮是否使用 24 小时制                             | true                                       | 否       |
| amPmFirst       | Boolean                  | 上午/下午滚轮是否位于小时滚轮之前                      | TimePickerDefaults.localeAmPmFirst()       | 否       |
| minuteFiveStep  | Boolean                  | 分钟滚轮是否以 5 分钟步进                              | false                                      | 否       |
| yearRange       | IntRange?                | 日期滚轮覆盖的年份；null 表示首个值之前 50 年到之后 150 年 | null                                   | 否       |
| dateLabel       | (DateValue) -> String    | 日期的显示文本转换函数                                 | DateTimePickerDefaults.dateLabel()         | 否       |
| todayLabel      | String                   | 当天那一行的显示文本                                   | DateTimePickerDefaults.todayLabel()        | 否       |
| amLabel         | String                   | 上午的显示文本                                         | TimePickerDefaults.amLabel()               | 否       |
| pmLabel         | String                   | 下午的显示文本                                         | TimePickerDefaults.pmLabel()               | 否       |
| hourUnit        | String                   | 选中小时旁的单位标签（COUI setUnitVisible），为空则隐藏 | TimePickerDefaults.HourUnit               | 否       |
| minuteUnit      | String                   | 选中分钟旁的单位标签，为空则隐藏                       | TimePickerDefaults.MinuteUnit              | 否       |
| dateColumnWidth | Dp                       | 日期列宽度                                             | DateTimePickerDefaults.dateColumnWidth()   | 否       |
| timeColumnWidth | Dp                       | 时、分、上午/下午列宽度                                | DateTimePickerDefaults.TimeColumnWidth     | 否       |
| colors          | NumberPickerColors       | 滚轮的颜色配置                                         | NumberPickerDefaults.colors()              | 否       |
| dividerColor    | Color                    | 选中行上下两条细线的颜色                               | COUITheme.colorScheme.divider          | 否       |
| textStyle       | TextStyle                | 滚轮项目的文本样式                                     | COUITheme.textStyles.headlineM                | 否       |
| itemHeight      | Dp                       | 每个滚轮项目的高度                                     | NumberPickerDefaults.ItemHeight            | 否       |
| insideMargin    | PaddingValues            | 滚轮列周围的内边距                                     | DateTimePickerDefaults.InsideMargin        | 否       |

### DateTimeValue 类

| 属性名 | 类型      | 说明                              |
| ------ | --------- | --------------------------------- |
| date   | DateValue | 日期                              |
| time   | TimeValue | 时间（小时始终为 24 小时制）      |

`DateValue.dayOfWeek` 返回 ISO 星期（1 = 周一 .. 7 = 周日），便于自定义日期文本。

### DateTimePickerDefaults 对象

| 属性名              | 类型                  | 说明                                       | 默认值                          |
| ------------------- | --------------------- | ------------------------------------------ | ------------------------------- |
| DateColumnWidth     | Dp                    | 中文、英文下的日期列宽度                   | 150.dp                          |
| WideDateColumnWidth | Dp                    | 其他语言下的日期列宽度                     | 174.dp                          |
| TimeColumnWidth     | Dp                    | 时、分、上午/下午列宽度                    | 62.dp                           |
| InsideMargin        | PaddingValues         | 滚轮列周围的内边距                         | PaddingValues(vertical = 12.dp) |
| dateColumnWidth()   | Dp                    | zh / en 返回 DateColumnWidth，否则 WideDateColumnWidth | -                   |
| dateLabel()         | (DateValue) -> String | 中文为「10月23日 周四」，其他语言为本地化的星期、月、日缩写（"Thu, Oct 23"） | - |
| todayLabel()        | String                | 当前语言的「今天」                         | -                               |

各列按 `宽度 / (日期列 + 3 × 时间列)` 收缩，与 COUITimePicker 一致在 24 小时制下也把隐藏的上午/下午列算进去；选择器最大宽度 640dp。上午/下午滚轮默认按语言习惯放置（中文在小时之前，英文在分钟之后）。

## 进阶用法

### 自定义文本

默认文本已随语言变化，需要时可传入自己的文本：

```kotlin
DateTimePicker(
    value = value,
    onValueChange = { value = it },
    is24Hour = false,
    dateLabel = { "${it.month}/${it.day}" },
    todayLabel = "现在",
    hourUnit = "时",
    minuteUnit = "分"
)
```
