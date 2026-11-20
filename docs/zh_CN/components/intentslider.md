# IntentSlider

`IntentSlider` 是 COUI 的细长媒体进度条，移植自 COUI 17 `COUIIntentSeekBar`。它显示播放位置和可选的缓冲（次级）位置，按住时变粗，并可在按住或暂停时显示滑块。适用于视频、音乐等播放进度；用于选择数值时请使用 [Slider](./slider)。

<div style="position: relative; height: 320px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=intentSlider" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.IntentSlider
import io.github.suqi8.coui.kmp.basic.IntentSliderPlaybackState
import io.github.suqi8.coui.kmp.basic.IntentSliderSize
```

## 基本用法

```kotlin
var position by remember { mutableFloatStateOf(0.3f) }

IntentSlider(
    value = position,
    onValueChange = { position = it },
    secondaryValue = 0.6f, // 缓冲位置
)
```

与 `match_parent` 的 `COUIIntentSeekBar` 一致，进度条占满可用宽度，高度至少为 `IntentSliderDefaults.MinHeight`。

### 滑块与播放状态

`playbackState`（`couiIntentSeekBarPlaybackState`）决定播放相关的视觉。开启 `showThumb` 后，按住进度条时滑块会弹出；`Paused` 时滑块以静止半径常驻，恢复播放后滑块消失。

```kotlin
IntentSlider(
    value = position,
    onValueChange = { position = it },
    showThumb = true,
    playbackState = if (isPlaying) IntentSliderPlaybackState.Playing else IntentSliderPlaybackState.Paused,
)
```

| 状态             | 视觉（`resolveVisualState`）                                                       |
| ---------------- | ---------------------------------------------------------------------------------- |
| Idle             | 普通进度条                                                                         |
| LoadingInitial   | 一道白光（中间 60% 不透明度，长度为轨道的 0.6）扫过整条进度条；不绘制进度          |
| Playing          | 普通进度条；开启锥形光样式时光束拖尾于进度末端                                     |
| Paused           | 普通进度条；滑块（开启 `showThumb` 时）常驻                                        |
| LoadingBuffering | 绘制进度，白光从进度末端扫到轨道末端；不绘制次级进度                               |

扫光以 response 0.7 / bounce 0 的弹簧循环（`LoadingEffectRenderer`）；离开 `LoadingInitial` 时正在进行的扫光会走完这一程。

### 加载中拖动

开启 `loadingDragReturn`（`setLoadingDragReturnEnabled`）后，在 `LoadingInitial` 时开始的拖动会显示进度并允许拖动定位；松手后进度以 response 0.3 的弹簧回到 `valueRange` 起点（`LoadingDragReturnHandler`），不会惯性滑行，`onValueChange` 收到起点值，弹簧停稳后回调 `onLoadingDragReturnComplete`。

```kotlin
IntentSlider(
    value = position,
    onValueChange = { position = it },
    playbackState = IntentSliderPlaybackState.LoadingInitial,
    loadingDragReturn = true,
    onLoadingDragReturnComplete = { /* 进度条重新显示加载扫光 */ },
)
```

### 尺寸

```kotlin
IntentSlider(value = position, onValueChange = { position = it }, size = IntentSliderSize.Small)
IntentSlider(value = position, onValueChange = { position = it }, size = IntentSliderSize.Large)
```

### 媒体内容之上

关闭 `followSystemTheme` 即使用 COUI 在视频、封面上使用的固定白色配色。存在 `LocalMaterialBackdrop` 提供者时，COUI 17 的模糊混合背景（`BlurBlendEffectRenderer`：30px 模糊，再叠加 `#66333333` 变亮与 `#4DFFFFFF` 柔光）会替代半透明背景填充；没有提供者时保留填充，即 COUI 的非模糊外观。

```kotlin
CompositionLocalProvider(LocalMaterialBackdrop provides provider) {
    IntentSlider(
        value = position,
        onValueChange = { position = it },
        followSystemTheme = false,
        showThumb = true,
    )
}
```

### 锥形光

在媒体内容之上，`conicLight = true`（`couiIntentSeekBarConicLight`）切换为 COUI 17 的锥形光进度样式。与 `isConicLightProgressStyle` 一致，仅在 `followSystemTheme = false` 时生效。

- 进度使用 `IntentSliderColors.conicLightColor`（#F87796），末端叠加一段白色渐变（`IntentConicProgressRenderer`）：`Playing` 时为轨道的五分之一，拖动时为十分之一，靠近起点时覆盖整段进度；渐变以 response 0.5 的弹簧淡入淡出。
- `Playing` 时以及播放中拖动时，两道光锥拖尾于进度末端，越往后纵向越散（`ConicLightEffectRenderer`，AGSL `ConicLightShaderParams`，以 `BlendMode.Screen` 绘制并裁剪在进度条内）。进入播放时形状以 response 0.9、透明度以 0.3 的弹簧进入；拖动与淡出使用 0.4。
- 光效需要运行时着色器（Android 13+ 及所有 Skia 平台），其他情况下跳过；进度样式在所有平台绘制。
- 播放器的长按倍速手势进行中时传 `holdToSpeedUp = true`（`setHoldToSpeedUpConeLightActive`）：`Playing` 时光锥改用拖动时的形态，取消后回到播放形态。

```kotlin
IntentSlider(
    value = position,
    onValueChange = { position = it },
    followSystemTheme = false,
    conicLight = true,
    holdToSpeedUp = isSpeedingUp,
    playbackState = IntentSliderPlaybackState.Playing,
)
```

## 交互

`IntentSlider` 遵循 COUI 17 `COUIIntentSeekBar`：

- 按下时轨道以 response 0.5 / bounce 0 的弹簧从静止高度变为按压高度（见 `IntentSliderSize`），松手后收回。松手会等到轨道已增高 2dp（`getMinEnlargeHeight`）后再收回，因此快速点击也有反馈。
- 拖动时进度按手指移动的距离从当前位置开始移动（`couiSeekBarIsFollowThumb = false`）；`followThumb = true` 时进度改为追随手指。两种方式下绘制的进度都以 response 0.15 的跟手弹簧（`FlexibleFollowHandHandler`）追随拖动值。
- 点击会让进度以弹簧动画移到点击位置（response 0.3）；快速松手（至少 100px/s）会让进度继续滑行。
- 只有拖动会振动：点击和滑行不振动，`startClickTransitionAnim` 与 `onPhysicsFlingUpdate` 都不触发反馈。
- 拖动时整个进度条沿触摸跟手橡皮筋（`TouchMotionEffectRenderer`）水平跟随手指，松手后弹回。`onDragHorizontalOffsetChange` 每帧收到这个偏移（`OnDragHorizontalOffsetChangeListener`），可让时间标签等随进度条一起移动。
- 滑块带回弹（0.3）放大到按压半径，轨道开始收回后，暂停时回到静止半径，否则消失（response 0.4）。
- 代码设置的 `value` 立即绘制（播放进度每帧更新），只有手势会产生动画。
- `IntentSlider` 没有触摸聚光与闪光效果：`COUIIntentSeekBar.initAdvancedFeatures` 覆写了基类初始化，不启用这两项。

## 属性

### IntentSlider 属性

| 属性名                | 类型                              | 说明                                                             | 默认值                                 | 是否必须 |
| --------------------- | --------------------------------- | ---------------------------------------------------------------- | -------------------------------------- | -------- |
| value                 | Float                             | 当前值，会被限制在 valueRange 内                                 | -                                      | 是       |
| onValueChange         | (Float) -> Unit                   | 用户拖动、点击或滑行时回调新值；加载中拖动回弹时回调起点值       | -                                      | 是       |
| modifier              | Modifier                          | 应用于进度条的修饰符                                             | Modifier                               | 否       |
| enabled               | Boolean                           | 是否响应输入                                                     | true                                   | 否       |
| showThumb             | Boolean                           | 按住或暂停时是否绘制滑块                                         | false                                  | 否       |
| followThumb           | Boolean                           | 拖动时进度是否移到手指位置，而非按手指移动距离移动               | false                                  | 否       |
| followSystemTheme     | Boolean                           | 颜色是否跟随主题；关闭时使用固定媒体配色并允许背景模糊           | true                                   | 否       |
| materialBackdrop      | Boolean                           | 模糊混合背景能否替代填充（仅在关闭 followSystemTheme 且有提供者时） | true                                | 否       |
| conicLight            | Boolean                           | 是否使用锥形光进度样式（仅在关闭 followSystemTheme 时）          | false                                  | 否       |
| loadingDragReturn     | Boolean                           | LoadingInitial 时拖动是否显示进度并在松手后回弹到起点            | false                                  | 否       |
| holdToSpeedUp         | Boolean                           | 长按倍速手势是否进行中；Playing 且开启 conicLight 时光锥改用拖动形态 | false                               | 否       |
| playbackState         | IntentSliderPlaybackState         | 播放状态：Idle、LoadingInitial、Playing、Paused 或 LoadingBuffering | IntentSliderPlaybackState.Idle      | 否       |
| valueRange            | ClosedFloatingPointRange\<Float\> | 取值范围                                                         | 0f..1f                                 | 否       |
| secondaryValue        | Float?                            | 绘制在进度下方的次级（缓冲）值；null 时隐藏                      | null                                   | 否       |
| onValueChangeFinished | (() -> Unit)?                     | 拖动、滑行或点击结束时回调                                       | null                                   | 否       |
| onLoadingDragReturnComplete | (() -> Unit)?               | 加载中拖动回弹到起点后回调                                       | null                                   | 否       |
| onDragHorizontalOffsetChange | ((Float) -> Unit)?       | 触摸跟手橡皮筋每帧移动进度条时回调其水平偏移（px）               | null                                   | 否       |
| size                  | IntentSliderSize                  | 轨道与滑块尺寸                                                   | IntentSliderSize.Default               | 否       |
| horizontalPadding     | Dp                                | 轨道距两端的内边距                                               | IntentSliderDefaults.HorizontalPadding | 否       |
| colors                | IntentSliderColors                | 进度条颜色                                                       | intentSliderColors() / fixedColors()   | 否       |
| hapticEffect          | SliderHapticEffect                | 触觉反馈类型：两端，以及手指拖动每 1%（`VibrationFeedbackHandler`），见 [Slider](./slider#触觉反馈) | SliderHapticEffect.Step     | 否       |

### IntentSliderSize

| 值      | 静止高度 | 按压高度 | 滑块半径 | 按压滑块半径 |
| ------- | -------- | -------- | -------- | ------------ |
| Default | 4.dp     | 6.dp     | 4.dp     | 5.dp         |
| Small   | 2.dp     | 10.dp    | 3.dp     | 8.dp         |
| Large   | 6.dp     | 10.dp    | 5.dp     | 8.dp         |

### IntentSliderDefaults 对象

| 属性名            | 类型                 | 说明                 | 值                                                |
| ----------------- | -------------------- | -------------------- | ------------------------------------------------- |
| HorizontalPadding | Dp                   | 轨道距两端的内边距   | 14.dp                                             |
| MinHeight         | Dp                   | 进度条最小高度       | 24.dp                                             |
| ConicLightColor   | Color                | 锥形光样式的进度与光色 | #F87796                                         |
| BackdropSpec      | MaterialBackdropSpec | 模糊混合背景         | 30px 模糊，#66333333 Lighten，#4DFFFFFF Softlight |

| 函数名               | 返回类型           | 说明                                                                              |
| -------------------- | ------------------ | --------------------------------------------------------------------------------- |
| intentSliderColors() | IntentSliderColors | 主题配色：背景/次级 quarternaryNeutral，进度 labelPrimary，不透明滑块             |
| fixedColors()        | IntentSliderColors | 媒体配色：背景 #4DFFFFFF，进度 #8AFFFFFF，次级 #29FFFFFF，白色滑块                |

### IntentSliderColors 类

| 属性名                 | 类型  | 说明                     |
| ---------------------- | ----- | ------------------------ |
| backgroundColor        | Color | 背景胶囊（未播放部分）   |
| progressColor          | Color | 进度                     |
| secondaryProgressColor | Color | 次级（缓冲）进度         |
| thumbColor             | Color | 滑块                     |
| thumbShadowColor       | Color | 滑块阴影                 |
| conicLightColor        | Color | 锥形光样式的进度与光（默认 ConicLightColor） |

## 进阶用法

### 自定义颜色

在 COUI 中自定义颜色会关闭模糊混合背景（`hasCustomUserColors`），因此使用自定义颜色时请传入 `materialBackdrop = false`。

```kotlin
IntentSlider(
    value = position,
    onValueChange = { position = it },
    followSystemTheme = false,
    materialBackdrop = false,
    colors = IntentSliderDefaults.fixedColors(progressColor = Color(0xFFFF8A00)),
)
```

### 结合播放位置进行拖动定位

```kotlin
var position by remember { mutableFloatStateOf(0f) }
var seeking by remember { mutableStateOf(false) }

IntentSlider(
    value = position,
    onValueChange = {
        seeking = true
        position = it
    },
    onValueChangeFinished = {
        player.seekTo(position)
        seeking = false
    },
    showThumb = true,
    playbackState = if (player.isPlaying) IntentSliderPlaybackState.Playing else IntentSliderPlaybackState.Paused,
)
```
