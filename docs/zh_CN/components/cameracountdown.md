# CameraCountdown

`CameraCountdown` 是相机的定时拍摄倒计时，即定时快门倒数时 `TimerSnapShotManager` 显示在取景画面上的数字。本页同时介绍相机的其他倒计时：`CameraExposureCountdown` 是长曝光时 "12.3s" 样式的读数（`NightTimeView`）；`CameraCountdownRing` 是相机的进度环（`RoundProgressBar`）以及录像停止倒计时的消退环（`ProgressRingView`）。

<div style="position: relative; height: 360px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=cameraCountdown" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.CameraCountdown
import io.github.suqi8.coui.kmp.basic.CameraCountdownDefaults
import io.github.suqi8.coui.kmp.basic.CameraCountdownRing
import io.github.suqi8.coui.kmp.basic.CameraExposureCountdown
```

## 基本用法

```kotlin
CameraCountdown(remainingMillis = remainingMillis)
```

显示的数字是剩余时间向上取整后的秒数：100dp 的白色数字，居中于 175dp 的方框内。剩余时间归零后不再绘制任何内容。

### 闪烁节奏

每个新数字在 200ms 内淡入并保持，自出现起 800ms 时开始在 200ms 内淡出，因此每一秒闪烁一次（先加速后减速，即相机 `ValueAnimator` 的默认曲线）。不透明度超过 80% 时数字带上淡淡的 2px 黑色光晕，最高为 20% 黑。

### 紧凑样式

相机会把倒计时移到取景画面顶部，显示为 46dp、50% 黑色圆底中的 26dp 数字。切换 `compact` 时，数字在 450ms 内按 (0.3, 0, 0.1, 1) 曲线从旧尺寸缩放到新尺寸：

```kotlin
CameraCountdown(remainingMillis = remainingMillis, compact = true)
```

### 长曝光倒计时

```kotlin
CameraExposureCountdown(remainingMillis = exposureLeftMillis)
```

剩余曝光时间精确到十分之一秒并带单位，例如 "12.3s"：18dp、字重 750 的白色数字，带 30% 黑色阴影，居中于 94 x 22dp 的方框内。百位与十位各占固定的 11dp 格子，百位在 100s 起出现，十位在 10s 起出现。显示时延迟 150ms 后在 250ms 内淡入；隐藏（`visible = false`）时在 250ms 内淡出。

### 进度环

```kotlin
CameraCountdownRing(
    progress = progress,
    modifier = Modifier.size(CameraCountdownDefaults.RingSize),
)
```

默认的环为 `RoundProgressBar`：1.6dp 的 20% 白色轨道，强调色弧线从 12 点钟方向顺时针增长，两端为平头，每次进度变化在 320ms 内按 (0.3, 0, 0.1, 1) 曲线过渡。

### 消退环

设置 `draining` 后，环为 `ProgressRingView` 的录像停止倒计时：2dp 的 55% 白色轨道，85% 白色弧线表示剩余部分，随 `progress` 从 0 到 1 自 12 点钟方向顺时针缩短。相机在 500ms 延迟后用先加速后减速的曲线在 10 秒内驱动它；传入环中的内容居中显示：

```kotlin
CameraCountdownRing(
    progress = CameraCountdownDefaults.DrainingRingEasing.transform(elapsedFraction),
    modifier = Modifier.size(CameraCountdownDefaults.DrainingRingSize),
    draining = true,
) {
    CameraRecordTimer(timeMillis = remainingMillis, countDown = true, showBackground = false)
}
```

## 属性

### CameraCountdown 属性

| 属性名          | 类型                  | 说明                         | 默认值                                          | 是否必须 |
| --------------- | --------------------- | ---------------------------- | ----------------------------------------------- | -------- |
| remainingMillis | Long                  | 距拍摄的剩余时间             | -                                               | 是       |
| modifier        | Modifier              | 应用于倒计时的修饰符         | Modifier                                        | 否       |
| compact         | Boolean               | 是否显示 46dp 的小圆底样式   | false                                           | 否       |
| colors          | CameraCountdownColors | 倒计时颜色                   | CameraCountdownDefaults.cameraCountdownColors() | 否       |

### CameraExposureCountdown 属性

| 属性名          | 类型                          | 说明                         | 默认值                                                  | 是否必须 |
| --------------- | ----------------------------- | ---------------------------- | ------------------------------------------------------- | -------- |
| remainingMillis | Long                          | 剩余曝光时间                 | -                                                       | 是       |
| modifier        | Modifier                      | 应用于倒计时的修饰符         | Modifier                                                | 否       |
| visible         | Boolean                       | 是否显示；变化时淡入淡出     | true                                                    | 否       |
| suffix          | String                        | 数字后的单位                 | CameraCountdownDefaults.ExposureSuffix                  | 否       |
| colors          | CameraExposureCountdownColors | 倒计时颜色                   | CameraCountdownDefaults.cameraExposureCountdownColors() | 否       |

### CameraCountdownRing 属性

| 属性名      | 类型                            | 说明                                   | 默认值                                      | 是否必须 |
| ----------- | ------------------------------- | -------------------------------------- | ------------------------------------------- | -------- |
| progress    | Float                           | 进度 0..1；`draining` 时为已过去的比例 | -                                           | 是       |
| modifier    | Modifier                        | 应用于环的修饰符；需指定尺寸           | Modifier                                    | 否       |
| draining    | Boolean                         | 是否绘制剩余部分而非已完成部分         | false                                       | 否       |
| animated    | Boolean                         | 进度变化是否过渡（320ms）              | !draining                                   | 否       |
| strokeWidth | Dp                              | 轨道与弧线的宽度                       | RingStrokeWidth 或 DrainingRingStrokeWidth  | 否       |
| colors      | CameraCountdownRingColors       | 环的颜色                               | ringColors() 或 drainingRingColors()        | 否       |
| content     | @Composable BoxScope.() -> Unit | 居中于环内的内容                       | {}                                          | 否       |

## 默认值对象

### CameraCountdownDefaults

| 属性名                            | 类型       | 说明                         | 值              |
| --------------------------------- | ---------- | ---------------------------- | --------------- |
| LargeSize                         | Dp         | 大号定时倒计时方框           | 175.dp          |
| LargeTextSize                     | Dp         | 大号定时倒计时数字字号       | 100.dp          |
| CompactSize                       | Dp         | 紧凑定时倒计时圆底           | 46.dp           |
| CompactTextSize                   | Dp         | 紧凑定时倒计时数字字号       | 26.dp           |
| ExposureWidth                     | Dp         | 长曝光倒计时方框宽度         | 94.dp           |
| ExposureHeight                    | Dp         | 长曝光倒计时方框高度         | 22.dp           |
| ExposureDigitWidth                | Dp         | 百位与十位格子宽度           | 11.dp           |
| ExposurePointWidth                | Dp         | 小数点格子宽度               | 4.dp            |
| ExposureSuffixMargin              | Dp         | 单位两侧边距                 | 3.dp            |
| ExposureTextSize                  | Dp         | 长曝光数字字号               | 18.dp           |
| ExposureFontWeight                | FontWeight | 长曝光数字字重               | FontWeight(750) |
| ExposureShadowRadius              | Dp         | 长曝光阴影半径               | 1.33.dp         |
| ExposureSuffix                    | String     | 长曝光单位                   | "s"             |
| RingStrokeWidth                   | Dp         | 进度环线宽                   | 1.6.dp          |
| RingSize                          | Dp         | 美颜菜单中的进度环尺寸       | 46.dp           |
| DrainingRingStrokeWidth           | Dp         | 录像停止环线宽               | 2.dp            |
| DrainingRingSize                  | Dp         | 录像停止环尺寸               | 270.dp          |
| TinyDrainingRingStrokeWidth       | Dp         | 小屏录像停止环线宽           | 1.5.dp          |
| TinyDrainingRingSize              | Dp         | 小屏录像停止环尺寸           | 156.dp          |
| RecordingStopCountdownMillis      | Int        | 录像停止倒计时时长           | 10000           |
| RecordingStopCountdownDelayMillis | Int        | 录像停止环开始消退前的延迟   | 500             |
| DrainingRingEasing                | Easing     | 录像停止环的曲线             | 先加速后减速    |

| 函数                            | 返回类型                      | 说明                         |
| ------------------------------- | ----------------------------- | ---------------------------- |
| cameraCountdownColors()         | CameraCountdownColors         | 固定的定时倒计时配色         |
| cameraExposureCountdownColors() | CameraExposureCountdownColors | 固定的长曝光倒计时配色       |
| ringColors()                    | CameraCountdownRingColors     | 进度环配色（强调色弧线）     |
| drainingRingColors()            | CameraCountdownRingColors     | 录像停止环配色               |

## 颜色类

### CameraCountdownColors

| 属性名                | 类型  | 说明                               | 默认值    |
| --------------------- | ----- | ---------------------------------- | --------- |
| contentColor          | Color | 数字                               | 白色      |
| compactContainerColor | Color | 紧凑数字后的圆底                   | #7F000000 |
| shadowColor           | Color | 光晕；其透明度跟随数字的不透明度   | 黑色      |

### CameraExposureCountdownColors

| 属性名        | 类型  | 说明                 | 默认值    |
| ------------- | ----- | -------------------- | --------- |
| contentColor  | Color | 数字、小数点与单位   | 白色      |
| shadowColor   | Color | 其后的阴影           | #4D000000 |

### CameraCountdownRingColors

| 属性名        | 类型  | 说明             | ringColors() | drainingRingColors() |
| ------------- | ----- | ---------------- | ------------ | -------------------- |
| trackColor    | Color | 弧线后的完整轨道 | #33FFFFFF    | #8CFFFFFF            |
| progressColor | Color | 弧线             | #FB822A      | #D9FFFFFF            |
