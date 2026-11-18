# CameraLevelIndicator

`CameraLevelIndicator` 是相机取景框的水平仪，移植自相机的 `GradienterAssistView`（打开“水平仪”设置后由 `AssistViewPresenter` 叠加在预览画面上）。画面中央的水平线随设备横滚转动，两侧是两段固定的刻度；水平时它摆正、伸长盖过刻度并变为强调色。平放俯拍时，水平线换成两个十字。它还可以显示相机的中心小圆环。

<div style="position: relative; height: 520px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=cameraLevelIndicator" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.CameraLevelIndicator
import io.github.suqi8.coui.kmp.basic.CameraLevelIndicatorDefaults
import io.github.suqi8.coui.kmp.basic.rememberCameraLevelState
```

## 基本用法

把设备姿态（例如来自重力传感器）传给指示器，并让它覆盖取景区域：

```kotlin
Box(Modifier.fillMaxWidth().aspectRatio(3f / 4f)) {
    CameraPreview(Modifier.matchParentSize())
    CameraLevelIndicator(
        rollDegrees = roll,   // 右侧偏低为正
        pitchDegrees = pitch, // 0 为镜头朝向地平线，90 为屏幕朝上平放
        modifier = Modifier.matchParentSize(),
    )
}
```

- `rollDegrees` 是设备水平轴相对地平线的倾角。竖持时即绕镜头轴的旋转；平放时即左右倾斜。读数接近 ±90 表示横持。
- `pitchDegrees` 是绕设备水平轴的前倾角：镜头朝向地平线为 0，屏幕朝上平放为 90；超过 90 表示越过平放、顶边更低。

### 竖持

- 水平线长度为画面短边的三分之一、粗 1.32dp，两侧各有一段 16dp 的固定刻度，与线端相距 4dp。它与横滚反向转动，始终与真实地平线平行。
- 横滚 5° 以内完全显示，到 11° 淡出；镜头偏离地平线 30° 到 40° 之间也会淡出。横滚超过 45° 时切换为横屏方向。
- 进入水平 1.2° 以内时，水平线摆正、伸长盖过刻度（刻度消失）、变为强调色并触发一次震动（每秒最多一次）。保持水平 725ms 后指示器在 375ms 内淡出，设备再次倾斜时立即重新出现。

### 俯拍

两个轴都在平放 15° 以内时（超过 20° 才退出），两个 32dp 十字代替水平线：中央固定的白色目标十字，以及向较低一侧移动的强调色十字，倾斜 20° 时偏移 40dp。两个轴都在 0.4° 以内、且移动十字已回到中心 1dp 以内时，只保留居中的强调色十字。十字在平放 5° 以内完全显示，到 11° 淡出。

### 时序

每次出现前等待 400ms（刚从水平状态离开时不等待），再以 (0.33, 0, 0.67, 1) 曲线淡入 200ms；超出范围或在竖持与俯拍之间切换时淡出 250ms。每个新读数在 60ms 内过渡到位。线条和十字带 0.33dp 的 30% 黑色描边，在任何预览画面上都清晰可见。

### 中心标记

相机的中心指示：6dp 的白色圆环包着 20% 白色的圆点，开启后始终可见。可以与水平仪同时显示，也可以单独显示。

```kotlin
CameraLevelIndicator(
    rollDegrees = roll,
    pitchDegrees = pitch,
    showLevel = false,
    showCenterMark = true,
    modifier = Modifier.matchParentSize(),
)
```

### 与参考线配合

让同一区域上的 [CameraGridOverlay](./cameragridoverlay) 共享一个 `CameraLevelState`，水平线就会像相机一样在它穿过的参考线上切出 4.33dp 的缺口：

```kotlin
val levelState = rememberCameraLevelState()

CameraGridOverlay(
    type = CameraGridType.Grid,
    levelState = levelState,
    modifier = Modifier.matchParentSize(),
)
CameraLevelIndicator(
    rollDegrees = roll,
    pitchDegrees = pitch,
    state = levelState,
    modifier = Modifier.matchParentSize(),
)
```

`levelState.isLevel` 与 `levelState.isTopDown` 也可以用来驱动你自己的界面。

## 属性

### CameraLevelIndicator 属性

| 属性名                | 类型                       | 说明                                           | 默认值                                                 | 是否必须 |
| --------------------- | -------------------------- | ---------------------------------------------- | ------------------------------------------------------ | -------- |
| rollDegrees           | Float                      | 设备水平轴的倾角，顺时针为正                   | -                                                      | 是       |
| pitchDegrees          | Float                      | 前倾角：0 朝向地平线，90 屏幕朝上平放          | -                                                      | 是       |
| modifier              | Modifier                   | 应用于指示器的修饰符，须为其提供尺寸           | Modifier                                               | 否       |
| state                 | CameraLevelState           | 与 `CameraGridOverlay` 共享的状态              | rememberCameraLevelState()                             | 否       |
| showLevel             | Boolean                    | 是否显示水平线和俯拍十字                       | true                                                   | 否       |
| showCenterMark        | Boolean                    | 是否显示中心圆环                               | false                                                  | 否       |
| hapticFeedbackEnabled | Boolean                    | 进入水平时是否震动                             | true                                                   | 否       |
| colors                | CameraLevelIndicatorColors | 指示器颜色                                     | CameraLevelIndicatorDefaults.cameraLevelIndicatorColors() | 否    |

### CameraLevelState

| 成员      | 类型    | 说明                                                   |
| --------- | ------- | ------------------------------------------------------ |
| isLevel   | Boolean | 设备是否水平（竖持 1.2°，俯拍两个轴均 0.4°）          |
| isTopDown | Boolean | 指示器是否处于俯拍模式                                 |

## 默认值对象

### CameraLevelIndicatorDefaults

| 属性名                  | 类型  | 说明                                 | 值      |
| ----------------------- | ----- | ------------------------------------ | ------- |
| LineThickness           | Dp    | 水平线和刻度的粗细                   | 1.32.dp |
| TickLength              | Dp    | 每段固定刻度的长度                   | 16.dp   |
| TickMargin              | Dp    | 水平线与刻度的间距                   | 4.dp    |
| OutlineWidth            | Dp    | 线条内侧的深色描边                   | 0.33.dp |
| GridGapHeight           | Dp    | 在参考线上切出的缺口高度             | 4.33.dp |
| CrossSize               | Dp    | 俯拍十字尺寸                         | 32.dp   |
| CrossMaxOffset          | Dp    | 倾斜 20° 时移动十字的偏移            | 40.dp   |
| CrossSettleDistance     | Dp    | 移动十字距中心多近才算水平           | 1.dp    |
| CenterMarkDiameter      | Dp    | 中心圆环外径                         | 6.dp    |
| CenterMarkInnerDiameter | Dp    | 中心圆点直径                         | 4.66.dp |
| CenterMarkRingWidth     | Dp    | 中心圆环宽度                         | 0.67.dp |
| CenterMarkFillAlpha     | Float | 中心圆点透明度                       | 0.2f    |

| 函数                         | 返回类型                   | 说明                         |
| ---------------------------- | -------------------------- | ---------------------------- |
| cameraLevelIndicatorColors() | CameraLevelIndicatorColors | 相机固定的浅色前景深色底配色 |

## 颜色类

### CameraLevelIndicatorColors

| 属性名           | 类型  | 说明                                   | 默认值    |
| ---------------- | ----- | -------------------------------------- | --------- |
| lineColor        | Color | 倾斜时的水平线和刻度                   | White     |
| levelColor       | Color | 水平时的水平线，以及俯拍的移动十字     | #FB822A   |
| targetCrossColor | Color | 俯拍的固定目标十字                     | White     |
| outlineColor     | Color | 线条和十字的细描边                     | #4D000000 |
| centerMarkColor  | Color | 中心圆环和圆点                         | White     |
