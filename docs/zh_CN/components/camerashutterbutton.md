# CameraShutterButton

`CameraShutterButton` 是相机的主快门，移植自相机的 `MainShutterButton`：一个 66dp 的玻璃圆，拍照时中间是白色（或强调色）圆盘，录像时是红色圆盘，录制中会变为圆角方块。它还可以显示拍摄进度弧、加载转圈和定时拍摄标签。旁边的圆形控制按钮 `CameraSwitchButton`（切换摄像头、暂停 / 继续、录像中拍照）也在本页介绍。

<div style="position: relative; height: 320px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=cameraShutterButton" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.CameraShutterButton
import io.github.suqi8.coui.kmp.basic.CameraShutterMode
import io.github.suqi8.coui.kmp.basic.CameraShutterStyle
import io.github.suqi8.coui.kmp.basic.CameraSwitchButton
import io.github.suqi8.coui.kmp.basic.CameraSwitchButtonDefaults
```

## 基本用法

```kotlin
CameraShutterButton(
    onClick = { takePhoto() },
)
```

快门尺寸为 66dp：半径 33dp 的圆环包围半径 27dp 的圆盘。由于快门位于相机预览之上，它始终使用固定的深底浅色配色。

### 录像与录制

```kotlin
var recording by remember { mutableStateOf(false) }

CameraShutterButton(
    onClick = { recording = !recording },
    mode = CameraShutterMode.Video,
    recording = recording,
)
```

- 切换 `mode` 时，圆盘在白色与红色之间以 200ms、(0.3, 0, 0.1, 1) 交叉渐变。
- 开始录制时，红色圆盘以 300ms、(0.3, 0, 0.1, 1) 变形为边长 30dp、圆角 8dp 的方块；停止录制时变回圆盘。

### 交互

- 按下时圆盘（或方块、标签）在 350ms 内缩小到 80%，圆环降为 50% 透明度；松开后在 300ms 内恢复，曲线均为 (0, 0, 0.1, 1)。
- 录像模式下按下反馈延迟 120ms、松开反馈延迟 100ms，因此快速轻点只会开始或停止录制。
- 距边缘 3dp 以内的触摸会被忽略。
- 按住达到长按超时后调用 `onLongClick`（相机用于连拍或快速录像），此后松手不再调用 `onClick`。
- 禁用的快门以 40% 透明度绘制。

### 拍摄进度

需要一段时间的拍摄（例如长曝光）可传入进度：

```kotlin
CameraShutterButton(
    onClick = {},
    captureProgress = progress, // 0f..1f，没有此类拍摄时为 null
)
```

圆盘变形为方块，圆环在 180ms 内降到 30%，一条 4dp 的白色弧线从顶部开始走满。`captureProgress` 恢复为 null 时圆盘立即重新显示。

### 加载与定时

```kotlin
CameraShutterButton(onClick = {}, loading = true)    // 保存 / 处理中
CameraShutterButton(onClick = {}, timerLabel = "3")  // 定时拍摄
```

- `loading` 让圆盘淡出，并在其边缘转动一条从透明渐变到 40% 白色的 4dp 弧线，每 700ms 转一圈。
- `timerLabel` 让圆盘缩小消失，标签（24dp，不随字体缩放）从 80% 放大并淡入，均为 300ms。

### 样式

| 样式   | 外观                                                                   |
| ------ | ---------------------------------------------------------------------- |
| Glass  | 模糊玻璃圆上的白色圆盘，外圈是一条向下渐显的 0.9dp 圆环                |
| Accent | 在 Glass 基础上使用橙色渐变圆盘（#F99018 到 #FF6B00）、柔和的橙色光晕和边缘光 |
| Ring   | 相机在不支持渲染效果时的外观：4dp 白色圆环加淡淡的深色描边，没有玻璃   |

```kotlin
CameraShutterButton(
    onClick = {},
    style = CameraShutterStyle.Accent,
)
```

### 材质模糊

在相机预览之上，玻璃圆是材质模糊（半径 30dp，在 30% 灰色、60% 混合之上叠加 15% 灰色的辉光叠加）。传入一个记录快门下方内容的 `MaterialBlurProvider`；不传时玻璃圆为 50% 黑色填充。

```kotlin
CameraShutterButton(
    onClick = {},
    materialBlur = provider, // 记录取景画面，而不是快门本身
)
```

## CameraSwitchButton

圆形控制按钮：玻璃圆中间一个 24dp 的白色图标。使用默认图标时是 40dp 的切换摄像头按钮；使用 `CameraSwitchButtonDefaults.PauseIcon` / `ResumeIcon` 时是录制中 48dp 的暂停 / 继续按钮；使用 `CaptureIcon` 时是录像中拍照按钮。

```kotlin
var paused by remember { mutableStateOf(false) }

CameraSwitchButton(onClick = { switchCamera() }, iconRotation = orientation)

CameraSwitchButton(
    onClick = { paused = !paused },
    icon = if (paused) CameraSwitchButtonDefaults.ResumeIcon else CameraSwitchButtonDefaults.PauseIcon,
    size = CameraSwitchButtonDefaults.ControlSize,
)
```

- 按下时按钮在 200ms 内淡到 50%，松开后 340ms 恢复，曲线均为 (0.33, 0, 0.67, 1)；禁用的按钮以 40% 透明度绘制。
- 更换 `icon` 时，旧图标在 200ms 内缩小到 80% 并淡出，随后新图标在 200ms 内放大并淡入。
- 更改 `iconRotation`（设备方向）时，图标以每秒 270 度沿较短方向转动。
- 传入 `materialBlur` 时圆为相机材质模糊（半径 80dp，在 15% 灰色、60% 混合之上叠加 40% 浅灰的辉光叠加）；否则为 50% 黑色填充。

## 属性

### CameraShutterButton 属性

| 属性名          | 类型                  | 说明                                             | 默认值                                     | 必须 |
| --------------- | --------------------- | ------------------------------------------------ | ------------------------------------------ | ---- |
| onClick         | () -> Unit            | 点击快门时调用                                   | -                                          | 是   |
| modifier        | Modifier              | 应用于快门的修饰符                               | Modifier                                   | 否   |
| onLongClick     | (() -> Unit)?         | 按住达到长按超时后调用                           | null                                       | 否   |
| enabled         | Boolean               | 快门是否响应输入                                 | true                                       | 否   |
| mode            | CameraShutterMode     | 拍照或录像，决定圆盘颜色                         | CameraShutterMode.Photo                    | 否   |
| recording       | Boolean               | 是否正在录制（红色方块）                         | false                                      | 否   |
| captureProgress | Float?                | 进行中拍摄的进度 0..1，绘制为弧线；null 表示没有 | null                                       | 否   |
| loading         | Boolean               | 相机是否忙碌：用转圈代替圆盘                     | false                                      | 否   |
| timerLabel      | String?               | 代替圆盘显示的短标签，例如 "3"                   | null                                       | 否   |
| style           | CameraShutterStyle    | Glass、Accent 或 Ring                            | CameraShutterStyle.Glass                   | 否   |
| materialBlur    | MaterialBlurProvider? | 在玻璃圆后绘制材质模糊；null 时保留填充          | null                                       | 否   |
| colors          | CameraShutterColors   | 快门的颜色                                       | CameraShutterDefaults.cameraShutterColors(style) | 否 |

### CameraSwitchButton 属性

| 属性名             | 类型                     | 说明                               | 默认值                                             | 必须 |
| ------------------ | ------------------------ | ---------------------------------- | -------------------------------------------------- | ---- |
| onClick            | () -> Unit               | 点击按钮时调用                     | -                                                  | 是   |
| modifier           | Modifier                 | 应用于按钮的修饰符                 | Modifier                                           | 否   |
| enabled            | Boolean                  | 按钮是否响应输入                   | true                                               | 否   |
| icon               | ImageVector              | 图标                               | CameraSwitchButtonDefaults.SwitchIcon              | 否   |
| contentDescription | String?                  | 无障碍标签                         | null                                               | 否   |
| iconRotation       | Float                    | 图标旋转角度（设备方向）           | 0f                                                 | 否   |
| size               | Dp                       | 按钮直径                           | CameraSwitchButtonDefaults.Size                    | 否   |
| iconSize           | Dp                       | 图标绘制尺寸                       | CameraSwitchButtonDefaults.IconSize                | 否   |
| materialBlur       | MaterialBlurProvider?    | 在圆后绘制材质模糊                 | null                                               | 否   |
| colors             | CameraSwitchButtonColors | 按钮的颜色                         | CameraSwitchButtonDefaults.cameraSwitchButtonColors() | 否 |

## 默认值对象

### CameraShutterDefaults

| 属性名                  | 类型  | 说明                                   | 值      |
| ----------------------- | ----- | -------------------------------------- | ------- |
| Size                    | Dp    | 快门尺寸                               | 66.dp   |
| OuterRadius             | Dp    | 圆环半径                               | 33.dp   |
| InnerRadius             | Dp    | 圆盘半径                               | 27.dp   |
| InnerSquareSize         | Dp    | 录制方块边长                           | 30.dp   |
| InnerSquareCornerRadius | Dp    | 录制方块圆角                           | 8.dp    |
| RingWidth               | Dp    | 玻璃样式的细圆环宽度                   | 0.9.dp  |
| DialWidth               | Dp    | Ring 样式圆环、进度弧和转圈的宽度      | 4.dp    |
| RingOutlineExtraWidth   | Dp    | Ring 样式深色描边多出的宽度             | 0.33.dp |
| TouchInset              | Dp    | 忽略触摸的边缘宽度                     | 3.dp    |
| BlurRadius              | Dp    | 材质模糊半径                           | 30.dp   |
| HaloRadius              | Dp    | 强调色光晕半径                         | 19.dp   |
| HaloOffsetY             | Dp    | 强调色光晕向下偏移                     | 10.dp   |
| HaloBlurRadius          | Dp    | 强调色光晕模糊半径                     | 28.dp   |
| TimerTextSize           | Dp    | 定时标签字号                           | 24.dp   |
| TimerShadowRadius       | Dp    | 定时标签阴影模糊                       | 2.dp    |
| PressedScale            | Float | 按下时的圆盘缩放                       | 0.8f    |
| DisabledAlpha           | Float | 禁用时的透明度                         | 0.4f    |

| 函数                       | 返回类型            | 说明                         |
| -------------------------- | ------------------- | ---------------------------- |
| cameraShutterColors(style) | CameraShutterColors | 指定样式的固定深底浅色配色   |

### CameraSwitchButtonDefaults

| 属性名          | 类型        | 说明                         | 值      |
| --------------- | ----------- | ---------------------------- | ------- |
| Size            | Dp          | 切换摄像头按钮尺寸           | 40.dp   |
| ControlSize     | Dp          | 暂停 / 继续及拍照按钮尺寸    | 48.dp   |
| IconSize        | Dp          | 图标尺寸                     | 24.dp   |
| CaptureIconSize | Dp          | 录像中拍照图形的尺寸         | 56.dp   |
| BlurRadius      | Dp          | 材质模糊半径                 | 80.dp   |
| PressedAlpha    | Float       | 按下时的透明度               | 0.5f    |
| DisabledAlpha   | Float       | 禁用时的透明度               | 0.4f    |
| SwitchIcon      | ImageVector | 切换摄像头箭头               | -       |
| PauseIcon       | ImageVector | 暂停竖条                     | -       |
| ResumeIcon      | ImageVector | 继续三角                     | -       |
| CaptureIcon     | ImageVector | 录像中拍照按钮的小快门       | -       |

| 函数                       | 返回类型                 | 说明                   |
| -------------------------- | ------------------------ | ---------------------- |
| cameraSwitchButtonColors() | CameraSwitchButtonColors | 固定的深底浅色配色     |

## 颜色类

### CameraShutterColors

| 属性名                | 类型  | 说明                         | 默认值                         |
| --------------------- | ----- | ---------------------------- | ------------------------------ |
| containerColor        | Color | 无材质模糊时的玻璃圆         | #7F000000                      |
| blurMixColor          | Color | 材质模糊混合色               | 30% 灰色，60% 透明度           |
| blurBlendColor        | Color | 材质模糊辉光叠加色           | 15% 灰色，15% 透明度           |
| ringTopColor          | Color | 圆环顶部颜色                 | #00D9D9D9（Ring：白色）        |
| ringBottomColor       | Color | 圆环在 80% 高度及以下的颜色  | #33D9D9D9（Ring：白色）        |
| ringOutlineColor      | Color | Ring 样式的深色描边          | #4D000000                      |
| photoTopColor         | Color | 拍照圆盘上部 25% 的颜色      | 白色（Accent：#F99018）        |
| photoBottomColor      | Color | 拍照圆盘底部颜色             | 白色（Accent：#FF6B00）        |
| videoColor            | Color | 录像圆盘和录制方块           | #E32127                        |
| haloColor             | Color | 强调色圆盘下方的光晕         | #B3FF5900                      |
| progressColor         | Color | 进度弧                       | 白色                           |
| loadingColor          | Color | 转圈的亮端                   | #66FFFFFF                      |
| timerLabelColor       | Color | 定时标签                     | 白色                           |
| timerLabelShadowColor | Color | 定时标签阴影                 | #33000000                      |

### CameraSwitchButtonColors

| 属性名         | 类型  | 说明                     | 默认值               |
| -------------- | ----- | ------------------------ | -------------------- |
| containerColor | Color | 无材质模糊时的圆         | #7F000000            |
| blurMixColor   | Color | 材质模糊混合色           | 15% 灰色，60% 透明度 |
| blurBlendColor | Color | 材质模糊辉光叠加色       | 65% 灰色，40% 透明度 |
| iconColor      | Color | 图标颜色                 | 白色                 |
