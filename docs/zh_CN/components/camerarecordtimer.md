# CameraRecordTimer

`CameraRecordTimer` 是相机的录像计时器，移植自 `CameraTimeView`（`record_time.xml`）。它以 `mm:ss` 显示已录制时长，满一小时后显示为 `hh:mm:ss`，白色数字置于相机的红色录制底板上。每个数字各占一个固定宽度的格子，因此计时器走秒时宽度始终不变。

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=cameraRecordTimer" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.CameraRecordTimer
import io.github.suqi8.coui.kmp.basic.CameraRecordTimerDefaults
```

## 基本用法

```kotlin
CameraRecordTimer(timeMillis = recordedMillis)
```

数字为 16dp，各自居中于 11 x 24dp 的格子中，分与秒之间是相机的两点冒号图形。红色底板（`#EB3B2F`）圆角 4dp，两侧内边距 6dp。时数及其冒号仅在满一小时后出现，前方留 4dp 间距。每个数字带有 0.33dp、40% 黑色的描边，使白色数字在明亮的取景画面上依然清晰。计时器始终按从左到右排列数字。

### 显示与隐藏

```kotlin
CameraRecordTimer(
    timeMillis = recordedMillis,
    visible = isRecording,
)
```

显示时延迟 150ms 后在 400ms 内淡入；隐藏时在 100ms 内淡出，均使用 COUI 标准曲线 (0.33, 0, 0.67, 1)。隐藏后的计时器不占空间。

### 录制圆点

相机的黑屏与延时摄影布局去掉底板，改为在前方显示一个闪烁的红色圆点（8dp，间距 4dp），每 486ms 切换一次，与录像模式的录制节拍一致：

```kotlin
CameraRecordTimer(
    timeMillis = recordedMillis,
    showBackground = false,
    showRecordingDot = true,
)
```

### 倒计时

用于录像倒计时时，传入剩余时间并设置 `countDown`；不足一秒的部分向上取整，因此在最后一秒完全过去之前计时器都显示 00:10：

```kotlin
CameraRecordTimer(timeMillis = remainingMillis, countDown = true)
```

### 细字重

相机默认使用 `SysSans-En` 常规字重 400，部分模式使用字重 266：

```kotlin
CameraRecordTimer(
    timeMillis = recordedMillis,
    fontWeight = CameraRecordTimerDefaults.LightFontWeight,
)
```

## 属性

### CameraRecordTimer 属性

| 属性名           | 类型                    | 说明                                         | 默认值                                              | 是否必须 |
| ---------------- | ----------------------- | -------------------------------------------- | --------------------------------------------------- | -------- |
| timeMillis       | Long                    | 已录制时长；`countDown` 时为剩余时间         | -                                                   | 是       |
| modifier         | Modifier                | 应用于计时器的修饰符                         | Modifier                                            | 否       |
| visible          | Boolean                 | 是否显示；变化时淡入淡出                     | true                                                | 否       |
| countDown        | Boolean                 | `timeMillis` 是否为剩余时间（秒数向上取整）  | false                                               | 否       |
| showBackground   | Boolean                 | 是否绘制红色录制底板                         | true                                                | 否       |
| showRecordingDot | Boolean                 | 是否在前方显示闪烁的录制圆点                 | false                                               | 否       |
| fontWeight       | FontWeight              | 数字字重                                     | CameraRecordTimerDefaults.RegularFontWeight         | 否       |
| colors           | CameraRecordTimerColors | 计时器颜色                                   | CameraRecordTimerDefaults.cameraRecordTimerColors() | 否       |

## 默认值对象

### CameraRecordTimerDefaults

| 属性名              | 类型       | 说明                         | 值                |
| ------------------- | ---------- | ---------------------------- | ----------------- |
| DigitWidth          | Dp         | 数字格宽度                   | 11.dp             |
| DigitHeight         | Dp         | 数字格高度                   | 24.dp             |
| TextSize            | Dp         | 数字字号（不随字体缩放）     | 16.dp             |
| CornerRadius        | Dp         | 底板圆角                     | 4.dp              |
| HorizontalPadding   | Dp         | 底板两侧内边距               | 6.dp              |
| HourMarginStart     | Dp         | 时数前的间距                 | 4.dp              |
| StrokeWidth         | Dp         | 数字描边宽度                 | 0.333.dp          |
| RecordingDotSize    | Dp         | 录制圆点尺寸                 | 8.dp              |
| RecordingDotGap     | Dp         | 录制圆点后的间距             | 4.dp              |
| BlinkIntervalMillis | Int        | 录制圆点切换间隔             | 486               |
| RegularFontWeight   | FontWeight | 常规数字字重                 | FontWeight.Normal |
| LightFontWeight     | FontWeight | 细数字字重                   | FontWeight(266)   |

| 函数                      | 返回类型                | 说明                       |
| ------------------------- | ----------------------- | -------------------------- |
| cameraRecordTimerColors() | CameraRecordTimerColors | 相机固定的浅色前景深色底配色 |

## 颜色类

### CameraRecordTimerColors

| 属性名          | 类型  | 说明             | 默认值    |
| --------------- | ----- | ---------------- | --------- |
| backgroundColor | Color | 录制底板         | #EB3B2F   |
| contentColor    | Color | 数字与冒号       | White     |
| strokeColor     | Color | 数字描边         | #66000000 |
| dotColor        | Color | 闪烁的录制圆点   | #E32E27   |
