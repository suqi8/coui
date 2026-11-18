# CameraZoomDial

`CameraZoomDial` 是相机的变焦刻度环，移植自相机的弧形变焦条（`ZoomArcSeekBar`，由 `CameraSeekBar` 绘制的 `ZoomSeekBar`）。长按或拖动镜头倍率按钮时展开一个深色穹顶，顶部沿弧线排布对数变焦刻度；转动它即可变焦，最后一次触摸 1.5 秒后自动收起。

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=cameraZoomDial" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.CameraZoomDial
import io.github.suqi8.coui.kmp.basic.CameraZoomDialDefaults
```

## 基本用法

```kotlin
var zoom by remember { mutableFloatStateOf(1f) }
var expanded by remember { mutableStateOf(false) }

CameraZoomDial(
    value = zoom,
    onValueChange = { zoom = it },
    expanded = expanded,
    onExpandedChange = { expanded = it },
) {
    LensButtons() // 收起态控件，例如 0.6x / 1x / 2x / 3x
}
```

把刻度环放在取景区域的底边。它占满给定宽度；高度为 199dp 穹顶落在该宽度内的部分（至少 98dp，且能容纳 `content`），`content` 的中心位于顶部下方 86.7dp。

### 绘制

- 穹顶是半径 199dp 圆的顶部弧帽：50% 黑色，带 0.7dp、10% 白色的描边。
- 从 `stops` 第一项到最后一项，每 0.1x 一根 0.66dp x 10dp 的径向刻度，位于描边内侧 8dp。每个倍频程占相同角度：15x 以内的范围占 90°（刻度环止于 6x 及以下时为 60°）。
- `stops` 中的每一项是纯白大刻度，其数值写在刻度下方 18dp 处（12dp，沿弧线旋转）。整数倍率也是大刻度；刻度环超过 30x 时只有 10 的倍数、以及 10x 以上 5 的倍数才是。其余刻度为 15% 白色。
- 从 15x 起，大刻度降到 70% 并朝刻度环末端逐渐再减半，小刻度合并为一条半透明色带。
- 刻度与标签在穹顶与左右边缘相交处往上 5° 起、8° 范围内淡出。
- 当前倍率位于顶部固定的 1.5dp 强调色刻度下方，以加粗强调色显示并带 "×" 号，向下取整到 0.1（与显示值相差 0.1 才更新）。滑到它下方的档位标签在相交处淡出，过渡宽度 4dp。

### 交互

- 拖动时刻度绕圆心随手指转动：手指下的刻度跟随手指，所以向右拖会降低倍率。
- 移动速度低于 200px/s 时，刻度环会停在经过的每个 `stops` 档位上，直到手指再移动几个 dp（10x 以下距离更大）才继续。
- 拖过两端时刻度按橡皮筋拉伸（手指位移的 4%，最多范围的 2.1%，刻度间距随之展开），松手后以 (0.3, 0, 0.1, 1) 曲线在 500ms 内回弹。
- 经过两端之间的 `stops` 档位、或 20x 以上 5 的倍数时触发一次刻度震动；拉到橡皮筋尽头时触发一次边缘震动。
- 触摸某个档位的标签，刻度环在 300ms 内滑到该档位。
- 收起时，在 `content` 上（及其左右 17dp、上下 12dp 范围内）长按、横向拖动或向上滑动会调用 `onExpandedChange(true)`；横向拖动会在同一手势中继续转动刻度环。
- 陡峭的向下滑动，或 1.5 秒无触摸，会调用 `onExpandedChange(false)`。

### 动画

展开时穹顶与刻度淡入（250ms，延迟 70ms），穹顶以弹簧曲线放大 33dp（350ms），同时 `content` 淡出（180ms）。收起时它们分别在 280ms 与 180ms 内淡出，`content` 淡入（250ms）。

### 自定义档位

`stops` 的第一项与最后一项是刻度环的两端；位于 0.1x 网格上的值会与刻度对齐。

```kotlin
CameraZoomDial(
    value = zoom,
    onValueChange = { zoom = it },
    expanded = expanded,
    onExpandedChange = { expanded = it },
    stops = listOf(0.6f, 1f, 2f, 5f), // 止于 5x：刻度环占 60°
)
```

### 始终展开

```kotlin
CameraZoomDial(
    value = zoom,
    onValueChange = { zoom = it },
    expanded = true,
    onExpandedChange = {}, // 忽略收起请求
)
```

## 属性

### CameraZoomDial 属性

| 属性名           | 类型                    | 说明                                         | 默认值                                       | 是否必须 |
| ---------------- | ----------------------- | -------------------------------------------- | -------------------------------------------- | -------- |
| value            | Float                   | 当前变焦倍率，位于第一个与最后一个档位之间   | -                                            | 是       |
| onValueChange    | (Float) -> Unit         | 转动刻度环或滑向档位时回调                   | -                                            | 是       |
| expanded         | Boolean                 | 刻度环是否展开                               | -                                            | 是       |
| onExpandedChange | (Boolean) -> Unit       | 手势请求展开或刻度环应收起时回调             | -                                            | 是       |
| modifier         | Modifier                | 应用于刻度环的修饰符                         | Modifier                                     | 否       |
| enabled          | Boolean                 | 刻度环与展开手势是否响应输入                 | true                                         | 否       |
| stops            | List&lt;Float&gt;       | 标记的变焦倍率，升序；首尾两项为两端         | CameraZoomDialDefaults.Stops                 | 否       |
| radius           | Dp                      | 穹顶半径                                     | CameraZoomDialDefaults.Radius                | 否       |
| colors           | CameraZoomDialColors    | 刻度环的颜色                                 | CameraZoomDialDefaults.cameraZoomDialColors() | 否       |
| content          | @Composable () -> Unit  | 穹顶下的收起态控件，例如镜头倍率按钮         | {}                                           | 否       |

## 默认值对象

### CameraZoomDialDefaults

| 属性名              | 类型               | 说明                                   | 值                         |
| ------------------- | ------------------ | -------------------------------------- | -------------------------- |
| Stops               | List&lt;Float&gt;  | 0.6x-120x、3x 长焦相机的档位           | 0.6, 1, 2, 3, 10, 20, 120  |
| Radius              | Dp                 | 穹顶半径                               | 199.dp                     |
| UnfoldDistance      | Dp                 | 开始展开时穹顶缩小的量                 | 33.dp                      |
| BorderWidth         | Dp                 | 穹顶描边                               | 0.7.dp                     |
| TickLength          | Dp                 | 刻度长度                               | 10.dp                      |
| TickPaddingTop      | Dp                 | 描边与刻度之间的间距                   | 8.dp                       |
| TickWidth           | Dp                 | 刻度宽度                               | 0.66.dp                    |
| IndicatorWidth      | Dp                 | 强调色指示刻度的宽度                   | 1.5.dp                     |
| LabelMarginTop      | Dp                 | 刻度与标签之间的间距                   | 18.dp                      |
| LabelBaselineOffset | Dp                 | 标签基线位于标签点下方的距离           | 4.dp                       |
| LabelTextSize       | Dp                 | 档位标签字号                           | 12.dp                      |
| ValueTextSize       | Dp                 | 当前倍率字号                           | 12.dp                      |
| LabelFadeMargin     | Dp                 | 标签滑到当前倍率下方时的淡出宽度       | 4.dp                       |
| MultiplySignSpacing | Dp                 | "×" 号前的间距                         | 1.dp                       |
| LabelTouchSize      | Dp                 | 触摸点与档位标签的最大距离             | 15.dp                      |
| HotAreaHorizontal   | Dp                 | 收起态控件左右的热区                   | 17.dp                      |
| HotAreaVertical     | Dp                 | 收起态控件上下的热区                   | 12.dp                      |
| ContentCenterY      | Dp                 | 收起态控件中心距顶部的距离             | 86.7.dp                    |
| MinHeight           | Dp                 | 刻度环的最小高度                       | 98.dp                      |
| CollapseDelayMillis | Long               | 最后一次触摸后请求收起的延迟           | 1500                       |

| 函数                   | 返回类型             | 说明                       |
| ---------------------- | -------------------- | -------------------------- |
| cameraZoomDialColors() | CameraZoomDialColors | 固定的相机深底浅色配色     |

## 颜色类

### CameraZoomDialColors

| 属性名         | 类型  | 说明                                   | 默认值    |
| -------------- | ----- | -------------------------------------- | --------- |
| containerColor | Color | 穹顶                                   | #7F000000 |
| borderColor    | Color | 穹顶描边                               | #1AFFFFFF |
| tickColor      | Color | 刻度；小刻度与色带使用其 15%           | White     |
| labelColor     | Color | 档位标签                               | White     |
| indicatorColor | Color | 当前倍率上方的固定刻度                 | #FB822A   |
| valueColor     | Color | 当前倍率及其 "×" 号                    | #FB822A   |
