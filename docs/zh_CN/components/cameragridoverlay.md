# CameraGridOverlay

`CameraGridOverlay` 绘制相机取景框的构图参考线（“参考线”），移植自相机的 `GuideLineView`：三分网格线、斜对角线、黄金螺线和居中十字。线条是 0.66dp 的 50% 白色细线，两侧各有 0.33dp 的 30% 黑色描边，在任何预览画面上都清晰可见。

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=cameraGridOverlay" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.CameraGridOverlay
import io.github.suqi8.coui.kmp.basic.CameraGridOverlayDefaults
import io.github.suqi8.coui.kmp.basic.CameraGridType
```

## 基本用法

让参考线覆盖取景区域：

```kotlin
Box(Modifier.fillMaxWidth().aspectRatio(3f / 4f)) {
    CameraPreview(Modifier.matchParentSize())
    CameraGridOverlay(
        type = CameraGridType.Grid,
        modifier = Modifier.matchParentSize(),
    )
}
```

### 参考线类型

| 类型         | 相机取值     | 线条                                                                     |
| ------------ | ------------ | ------------------------------------------------------------------------ |
| Grid         | `grid`       | 三分线：两条横线和两条竖线                                               |
| Diagonal     | `triangle`   | 从右上角到左下角的对角线，以及从两侧经过其三等分点的垂线                 |
| GoldenSpiral | `spiral`     | 画面居中的 34:55 黄金矩形内，九段斐波那契半径的四分之一圆弧，以及内侧四个正方形的边 |
| Symmetry     | `symmetry`   | 贯穿整个画面的居中十字                                                   |

相机设置中提供网格线、斜对角线和黄金螺线；居中十字是部分模式使用的参考线。

### 显示与隐藏

参考线首次出现以及 `visible` 变化时，以先加速后减速的曲线在 310ms 内淡入淡出。切换 `type` 会立即重绘。

```kotlin
CameraGridOverlay(
    type = type,
    visible = showGuides,
    modifier = Modifier.matchParentSize(),
)
```

### 与水平仪配合

传入同一区域上 [CameraLevelIndicator](./cameralevelindicator) 的状态，参考线会在水平线穿过的位置留出 4.33dp 的缺口：`Diagonal` 和 `Symmetry` 始终留缺口，`Grid` 只在水平时留（伸长后的水平线会碰到竖线），`GoldenSpiral` 从不留。

```kotlin
val levelState = rememberCameraLevelState()

CameraGridOverlay(
    type = CameraGridType.Diagonal,
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

## 属性

### CameraGridOverlay 属性

| 属性名     | 类型                    | 说明                                         | 默认值                                          | 是否必须 |
| ---------- | ----------------------- | -------------------------------------------- | ----------------------------------------------- | -------- |
| type       | CameraGridType          | 绘制哪种参考线                               | -                                               | 是       |
| modifier   | Modifier                | 应用于参考线的修饰符，须为其提供尺寸         | Modifier                                        | 否       |
| visible    | Boolean                 | 是否显示参考线；变化时淡入淡出 310ms         | true                                            | 否       |
| levelState | CameraLevelState?       | 同一区域上 `CameraLevelIndicator` 的状态     | null                                            | 否       |
| colors     | CameraGridOverlayColors | 参考线颜色                                   | CameraGridOverlayDefaults.cameraGridOverlayColors() | 否   |

## 默认值对象

### CameraGridOverlayDefaults

| 属性名             | 类型 | 说明                       | 值      |
| ------------------ | ---- | -------------------------- | ------- |
| LineWidth          | Dp   | 线宽                       | 0.66.dp |
| OutlineWidth       | Dp   | 线条两侧的深色描边         | 0.33.dp |
| FadeDurationMillis | Int  | 显示或隐藏时的淡入淡出时长 | 310     |

| 函数                      | 返回类型                | 说明                         |
| ------------------------- | ----------------------- | ---------------------------- |
| cameraGridOverlayColors() | CameraGridOverlayColors | 相机固定的浅色前景深色底配色 |

## 颜色类

### CameraGridOverlayColors

| 属性名       | 类型  | 说明               | 默认值    |
| ------------ | ----- | ------------------ | --------- |
| lineColor    | Color | 参考线             | #80FFFFFF |
| outlineColor | Color | 线条两侧的细描边   | #4D000000 |
