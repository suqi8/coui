# CameraZoomBar

`CameraZoomBar` 是相机的惯性变焦控件，移植自 `camera_inertial_zoom_layout.xml`：减号与加号 `InertialZoomButton` 之间夹着一根 `InertiaZoomBar` 摇杆，使用时上方显示当前、最小与最大倍率（`InertiaZoomRotableTextView`）。

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=cameraZoomBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.CameraZoomBar
import io.github.suqi8.coui.kmp.basic.CameraZoomBarDefaults
```

## 基本用法

```kotlin
var zoom by remember { mutableFloatStateOf(1f) }

CameraZoomBar(
    zoom = zoom,
    onZoomChange = { zoom = it },
    minZoom = 0.6f,
    maxZoom = 20f,
)
```

变焦条 244dp x 46dp，滑块圆环直径 34.5dp，两侧各有一个 44dp 按钮，间距 3.33dp；上方的标签行高 90dp，并向下压入变焦条 7.67dp。整个控件约 339dp 宽，请给它整个手机屏幕的宽度。

### 交互

变焦条不把位置映射为倍率：滑块停在中间，像摇杆一样工作。

- 横向拖动滑块（超过触摸阈值后）会持续变焦，滑块离中间越远变焦越快。每 25ms 倍率变化 `(k / 5) * ((zoom - minZoom) / (20 - minZoom) * 0.4795 + 0.0152)`，其中 `k` 从变焦条起点的 -5 变化到终点的 +5。
- 拖动时，滑块增大到 35dp，线条由 2dp 加粗到 4dp（300ms，曲线 (0.3, 0, 0.1, 1)），一条白线连接中点与滑块，滑块隐藏其倍率，标签淡入（250ms，曲线 (0.5, 0, 0.3, 1)），当前倍率从 50% 放大。
- 松手后滑块在 333ms 到 667ms 内滑回中间（距离越远越久），倍率淡入（183ms），标签淡出。
- 到达 `minZoom` 或 `maxZoom` 时播放振动，并将变焦条变暗到 50%（180ms），直到拖动方向反转。
- 快速轻点会让滑块跳到手指处、显示标签一秒，然后滑回且不变焦。
- 轻点按钮使倍率以 100ms、曲线 (0, 0, 0.1, 1) 变化 0.1；长按后按住则以 `k` = ±2 的速度持续变焦。按钮按下时变暗到 50%。

`onZoomChange` 收到每个新的倍率，已限制在 `minZoom..maxZoom` 内；控件自身不保存倍率。

### 仅变焦条

```kotlin
CameraZoomBar(
    zoom = zoom,
    onZoomChange = { zoom = it },
    showStepButtons = false,
    showValueLabels = false,
)
```

## 属性

### CameraZoomBar 属性

| 属性名          | 类型                | 说明                                       | 默认值                                      | 必填 |
| --------------- | ------------------- | ------------------------------------------ | ------------------------------------------- | ---- |
| zoom            | Float               | 当前倍率                                   | -                                           | 是   |
| onZoomChange    | (Float) -> Unit     | 拖动、按住或步进时以新倍率回调             | -                                           | 是   |
| modifier        | Modifier            | 应用于控件的修饰符                         | Modifier                                    | 否   |
| enabled         | Boolean             | 变焦条与按钮是否响应输入                   | true                                        | 否   |
| minZoom         | Float               | 最低倍率                                   | CameraZoomBarDefaults.MinZoom               | 否   |
| maxZoom         | Float               | 最高倍率（须大于 `minZoom`）               | CameraZoomBarDefaults.MaxZoom               | 否   |
| showStepButtons | Boolean             | 是否显示减号与加号按钮                     | true                                        | 否   |
| showValueLabels | Boolean             | 使用时是否在上方显示倍率                   | true                                        | 否   |
| colors          | CameraZoomBarColors | 控件的颜色                                 | CameraZoomBarDefaults.cameraZoomBarColors() | 否   |

## 默认值对象

### CameraZoomBarDefaults

| 属性名               | 类型  | 说明                                 | 值      |
| -------------------- | ----- | ------------------------------------ | ------- |
| MinZoom              | Float | 默认最低倍率（超广角镜头）           | 0.6f    |
| MaxZoom              | Float | 默认最高倍率                         | 20f     |
| StepRatio            | Float | 轻点按钮的变焦步长                   | 0.1f    |
| HoldVelocity         | Float | 按住按钮时的变焦速度 `k`             | 2f      |
| BarWidth             | Dp    | 变焦条宽度                           | 244.dp  |
| BarHeight            | Dp    | 变焦条高度                           | 46.dp   |
| BarPadding           | Dp    | 变焦条两端内边距                     | 2.dp    |
| BarHorizontalMargin  | Dp    | 变焦条与按钮之间的间距               | 3.33.dp |
| LineWidth            | Dp    | 轨道与高亮线宽度                     | 2.dp    |
| DraggingLineWidth    | Dp    | 拖动时的线宽                         | 4.dp    |
| ThumbStrokeWidth     | Dp    | 静止时滑块圆环宽度                   | 1.dp    |
| ThumbSize            | Dp    | 静止时滑块直径                       | 34.5.dp |
| DraggingThumbSize    | Dp    | 拖动时滑块直径                       | 35.dp   |
| ThumbTextSize        | Dp    | 滑块内倍率文字大小                   | 12.dp   |
| ThumbSuffixSpacing   | Dp    | 小 "×" 前的间距                      | 1.dp    |
| SmallSuffixWidth     | Dp    | 小 "×" 宽度                          | 8.dp    |
| SmallSuffixHeight    | Dp    | 小 "×" 高度                          | 16.dp   |
| LargeSuffixWidth     | Dp    | 大 "×" 宽度                          | 12.dp   |
| LargeSuffixHeight    | Dp    | 大 "×" 高度                          | 20.dp   |
| LargeSuffixSpacing   | Dp    | 大 "×" 前的间距                      | 1.5.dp  |
| ButtonSize           | Dp    | 按钮尺寸                             | 44.dp   |
| ButtonCrossLength    | Dp    | 按钮十字臂长                         | 16.dp   |
| ButtonCrossWidth     | Dp    | 按钮十字臂宽                         | 1.33.dp |
| ButtonShadowStep     | Dp    | 按钮光晕每一圈的宽度                 | 0.33.dp |
| LabelsHeight         | Dp    | 标签行高度                           | 90.dp   |
| LabelsOverlap        | Dp    | 标签行压入变焦条的距离               | 7.67.dp |
| ValueTextSize        | Dp    | 当前倍率文字大小                     | 18.dp   |
| ValueWidth           | Dp    | 当前倍率文字框宽度                   | 60.dp   |
| MinMaxTextSize       | Dp    | 最小与最大倍率文字大小               | 12.dp   |
| MinMaxMargin         | Dp    | 最小与最大倍率的边距                 | 22.dp   |
| TextOutlineWidth     | Dp    | 文字描边宽度                         | 0.67.dp |

| 函数                  | 返回类型            | 说明                       |
| --------------------- | ------------------- | -------------------------- |
| cameraZoomBarColors() | CameraZoomBarColors | 固定的深底浅字相机配色     |

## 颜色类

### CameraZoomBarColors

| 属性名              | 类型  | 说明                         | 默认值    |
| ------------------- | ----- | ---------------------------- | --------- |
| trackColor          | Color | 滑块两侧的轨道               | #4DFFFFFF |
| trackShadowColor    | Color | 轨道下方的深色层             | #33000000 |
| highlightColor      | Color | 中点到被拖动滑块的连线       | White     |
| thumbColor          | Color | 滑块圆环                     | White     |
| contentColor        | Color | 倍率文字                     | White     |
| contentOutlineColor | Color | 倍率文字描边                 | #66000000 |
| buttonIconColor     | Color | 减号与加号十字               | White     |
| buttonShadowColor   | Color | 十字的光晕                   | Black     |
