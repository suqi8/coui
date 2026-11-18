# CameraFocusIndicator

`CameraFocusIndicator` 是相机的点按对焦框及其竖向曝光滑杆，移植自相机的 `FocusIndicatorRotateLayout` 与 `ExposureSeekbarContainer`。它叠在相机预览上：点按时出现四个角标和旁边的太阳，在预览任意位置上下拖动可移动太阳以调节曝光，滑杆旁的锁形按钮用于锁定 AE/AF。

<div style="position: relative; height: 420px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=cameraFocusIndicator" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.CameraFocusIndicator
import io.github.suqi8.coui.kmp.basic.CameraFocusIndicatorDefaults
import io.github.suqi8.coui.kmp.basic.rememberCameraFocusIndicatorState
```

## 基本用法

把对焦框叠在预览之上并给它与预览相同的尺寸。点按即可放置；曝光与锁定状态可从状态对象或回调中读取。

```kotlin
val state = rememberCameraFocusIndicatorState()

Box(Modifier.fillMaxSize()) {
    CameraPreview(Modifier.fillMaxSize())
    CameraFocusIndicator(
        state = state,
        modifier = Modifier.fillMaxSize(),
        onFocus = { position -> camera.focusAt(position) },
        onExposureChange = { exposure -> camera.setExposure(exposure) }, // 0..1，0.5 为不补偿
        onLockedChange = { locked -> camera.lockAeAf(locked) },
    )
}
```

曝光值是滑杆行程的比例，0（最暗）到 1（最亮）；每次重新对焦都会重置为 0.5 并解除锁定。

### 几何尺寸

- 对焦框 70dp：四个 1.5dp 粗、臂长 20dp 的角标，各带一圈 30% 黑色细描边。框中心距边缘至少 45dp（半个框加 10dp 边距）。
- 滑杆位于框中心末端侧 55dp 处，框距末端边缘不足 82dp 时移到起始侧。太阳（5dp 圆盘加八条 1.57dp 光线，外端 9.35dp）行程 119.6dp；在整个范围内，其镂空内核像月相一样逐渐填满，光线转过一整圈。
- 首次拖动后出现 1.76dp 的轨道线（在太阳上下各断开 11.7dp），同时在滑杆中心上方 88dp 处出现 24dp 的锁形按钮（触摸区 30dp）；框距顶部边缘不足 103dp 时按钮移到下方。

### 动画

- 出现：从 1.8 倍缩放到 1 倍，165ms，曲线 (0.3, 0, 1, 1)；同时 310ms 淡入，曲线 (0.33, 0, 0.67, 1)；随后角标闪烁一次（透明度 1、0.6、1，300ms）。
- 锁定：角标脉动（缩放 1、1.3、0.9、1，500ms），角标、太阳、轨道线和锁形图标在 300ms 内变为橙色，两者曲线均为 (0.33, 0, 0.67, 1)；锁形图标 500ms 内扣上锁梁。解锁时立即恢复白色。
- 自动隐藏（`autoHide`）：对焦后 3 秒淡出（透明度 55% 到 0，200ms，曲线 (0.3, 0, 0.1, 1)）。拖动曝光或切换锁定后 1.5 秒变暗到 55%（300ms，曲线 (0.58, 0, 0.69, 1)），再过 4 秒淡出。锁定时保持显示（变暗状态）；再次拖动会立即恢复。

### 代码控制

```kotlin
state.focusAt(Offset(x, y))     // 带出现动画放置
state.adjustExposure(0.1f)      // 等同拖动：显示轨道线和锁定按钮
state.updateExposure(0.5f)      // 只设置数值，不触发拖动的附带效果
state.lock(true)           // 等同点按锁定按钮
state.hide()                    // 立即淡出
```

### 标签

相机可以在框下方显示一行标签，例如对焦或 AE/AF 锁定提示：

```kotlin
CameraFocusIndicator(
    state = state,
    modifier = Modifier.fillMaxSize(),
    label = if (state.isLocked) "对焦/曝光锁定" else null,
)
```

## 属性

### CameraFocusIndicator 属性

| 属性名           | 类型                       | 说明                                         | 默认值                                | 是否必须 |
| ---------------- | -------------------------- | -------------------------------------------- | ------------------------------------- | -------- |
| state            | CameraFocusIndicatorState  | 位置、曝光、锁定与动画                       | -                                     | 是       |
| modifier         | Modifier                   | 应用于对焦区域的修饰符，必须给出尺寸         | Modifier                              | 否       |
| onFocus          | (Offset) -> Unit           | 点按放置对焦框时以点按位置回调               | {}                                    | 否       |
| onExposureChange | (Float) -> Unit            | 曝光变化时回调，包括重置为 0.5               | {}                                    | 否       |
| onLockedChange   | (Boolean) -> Unit          | AE/AF 锁定变化时回调                         | {}                                    | 否       |
| enabled          | Boolean                    | 是否响应点按和拖动                           | true                                  | 否       |
| tapToFocus       | Boolean                    | 点按是否放置对焦框                           | true                                  | 否       |
| autoHide         | Boolean                    | 是否自动淡出                                 | true                                  | 否       |
| showLockButton   | Boolean                    | 拖动曝光后是否显示 AE/AF 锁定按钮            | true                                  | 否       |
| label            | String?                    | 对焦框下方的标签，最多两行                   | null                                  | 否       |
| colors           | CameraFocusIndicatorColors | 对焦框的颜色                                 | CameraFocusIndicatorDefaults.colors() | 否       |

### CameraFocusIndicatorState

| 成员                         | 类型             | 说明                                         |
| ---------------------------- | ---------------- | -------------------------------------------- |
| focusPosition                | Offset?          | 框中心的请求位置（px），首次对焦前为 null    |
| exposure                     | Float            | 曝光值，0..1                                 |
| isLocked                     | Boolean          | AE/AF 是否锁定                               |
| isExposureActive             | Boolean          | 本次对焦后是否拖动过曝光                     |
| isVisible                    | Boolean          | 对焦框是否显示                               |
| focusAt(position)            | Unit             | 放置对焦框，重置曝光与锁定                   |
| adjustExposure(delta)        | Unit             | 像拖动一样调整曝光                           |
| updateExposure(value)        | Unit             | 设置曝光值，不触发拖动的附带效果             |
| lock(locked)            | Unit             | 锁定或解除 AE/AF                             |
| hide()                       | Unit             | 淡出对焦框                                   |

## 默认值对象

### CameraFocusIndicatorDefaults

| 属性名                 | 类型  | 说明                                   | 值       |
| ---------------------- | ----- | -------------------------------------- | -------- |
| IndicatorSize          | Dp    | 对焦框尺寸                             | 70.dp    |
| EdgeMargin             | Dp    | 框与边缘的最小距离                     | 10.dp    |
| SliderOffset           | Dp    | 框中心到滑杆的距离                     | 55.dp    |
| SliderFlipDistance     | Dp    | 距末端边缘小于此值时滑杆移到起始侧     | 82.dp    |
| SliderTravel           | Dp    | 太阳的行程                             | 119.6.dp |
| SliderLineWidth        | Dp    | 轨道线宽度                             | 1.76.dp  |
| SliderLineGap          | Dp    | 太阳中心与轨道线的间隙                 | 11.7.dp  |
| SunRadius              | Dp    | 太阳圆盘半径                           | 5.03.dp  |
| SunCoreRadius          | Dp    | 最暗时镂空内核的半径                   | 3.48.dp  |
| SunRayInnerRadius      | Dp    | 光线内端                               | 6.81.dp  |
| SunRayOuterRadius      | Dp    | 光线外端                               | 9.35.dp  |
| SunRayWidth            | Dp    | 光线宽度                               | 1.57.dp  |
| OutlineWidth           | Dp    | 图形外的细描边                         | 0.33.dp  |
| LockButtonTouchSize    | Dp    | 锁定按钮触摸区                         | 30.dp    |
| LockIconSize           | Dp    | 锁形图标尺寸                           | 24.dp    |
| LockButtonOffset       | Dp    | 框中心到锁定按钮的竖直距离             | 88.dp    |
| LockButtonFlipDistance | Dp    | 距顶部边缘小于此值时锁定按钮移到下方   | 103.dp   |
| LabelTextSize          | Dp    | 标签字号（不随字体缩放）               | 12.dp    |
| LabelShadowRadius      | Dp    | 标签阴影半径                           | 2.dp     |
| LabelMarginTop         | Dp    | 框与标签的间距                         | 0.dp     |
| LabelMaxWidth          | Dp    | 标签换行宽度                           | 120.dp   |
| AppearScale            | Float | 出现时的起始缩放                       | 1.8f     |
| DimmedAlpha            | Float | 变暗后的透明度                         | 0.55f    |
| DefaultExposure        | Float | 新对焦的曝光值                         | 0.5f     |
| DragSensitivity        | Float | 拖过整个高度对应的曝光范围比例         | 0.7f     |
| AutoHideDelayMillis    | Long  | 对焦后到淡出的时间                     | 3000L    |
| DimDelayMillis         | Long  | 调整后到变暗的时间                     | 1500L    |
| DimmedHideDelayMillis  | Long  | 变暗后到淡出的时间                     | 4000L    |

| 函数     | 返回类型                   | 说明                       |
| -------- | -------------------------- | -------------------------- |
| colors() | CameraFocusIndicatorColors | 固定的相机深底浅色配色     |

## 颜色类

### CameraFocusIndicatorColors

| 属性名           | 类型  | 说明                                 | 默认值    |
| ---------------- | ----- | ------------------------------------ | --------- |
| indicatorColor   | Color | 角标、太阳、轨道线和锁形图标         | White     |
| lockedColor      | Color | AE/AF 锁定时上述图形的颜色           | #FB822A   |
| outlineColor     | Color | 图形外的细描边                       | #4D000000 |
| labelColor       | Color | 框下方的标签                         | White     |
| labelShadowColor | Color | 标签阴影                             | #33000000 |
