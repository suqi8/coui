# CompleteState

`CompleteState` 是「操作完成」结果区块，对应 ColorOS 的 COUICompleteStateView：100dp 的结果动画居中，下方为标题与可选副标题。三种动画依据 COUI 内置的 Lottie 文件（300x300 合成、60fps）重绘，保留其关键帧、缓动与几何：

- `Success`：绿色圆环淡入，同时对勾扫入（1.48s）。
- `Failed`：红色圆环与感叹号淡入，同时感叹号左右抖动（1.02s）。
- `Waiting`：时钟，时针从中心伸出，分针转两圈，圆环先退去再画回（3s）。

组件进入组合时播放一次，`type` 或 `replayKey` 变化时重新播放。

<div style="position: relative; height: 320px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=completeState" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.CompleteState
import io.github.suqi8.coui.kmp.basic.CompleteStateDefaults
import io.github.suqi8.coui.kmp.basic.CompleteStateType
```

## 基本用法

```kotlin
CompleteState(
    title = "支付成功",
    subtitle = "订单号 2026103000001",
    type = CompleteStateType.Success
)
```

## 属性

### CompleteState 属性

| 属性名        | 类型                | 说明                                     | 默认值                                      | 必须 |
| ------------- | ------------------- | ---------------------------------------- | ------------------------------------------- | ---- |
| title         | String              | 动画下方的标题，为空时隐藏               | -                                           | 是   |
| modifier      | Modifier            | 应用于组件的修饰符                       | Modifier                                    | 否   |
| subtitle      | String?             | 可选副标题                               | null                                        | 否   |
| type          | CompleteStateType   | 选择动画                                 | CompleteStateType.None                      | 否   |
| animate       | Boolean             | 是否播放动画，false 时直接显示最后一帧   | true                                        | 否   |
| replayKey     | Any?                | 该值变化时重新播放动画                   | null                                        | 否   |
| animationSize | Dp                  | 动画尺寸                                 | CompleteStateDefaults.AnimationSize         | 否   |
| colors        | CompleteStateColors | 颜色配置                                 | CompleteStateDefaults.completeStateColors() | 否   |
| titleStyle    | TextStyle           | 标题文字样式                             | COUITheme.textStyles.headlineM                 | 否   |
| subtitleStyle | TextStyle           | 副标题文字样式                           | COUITheme.textStyles.bodyXS              | 否   |

### CompleteStateType 枚举

`None`（无动画，100dp 区域留空；对应 COUI 默认的 `completeType` 0）、`Success`、`Failed`、`Waiting`。

### CompleteStateDefaults 对象

| 属性名                | 类型  | 说明                             | 默认值  |
| --------------------- | ----- | -------------------------------- | ------- |
| AnimationSize         | Dp    | 结果动画尺寸                     | 100.dp  |
| TextPaddingHorizontal | Dp    | 动画与文字的水平外边距           | 8.dp    |
| TitleMarginTop        | Dp    | 标题上边距                       | 8.dp    |
| SubtitleMarginTop     | Dp    | 副标题上边距                     | 8.dp    |
| SuccessColor          | Color | 成功 / 等待动画内置的绿色        | #33B054 |
| FailedColor           | Color | 失败动画内置的红色               | #ED4040 |

结果颜色写死在 COUI 的 Lottie 文件中（该视图关闭了强制深色），因此浅色与深色主题下相同。

### CompleteStateColors 类

| 属性名        | 类型  | 说明                 |
| ------------- | ----- | -------------------- |
| successColor  | Color | 成功圆环与对勾       |
| failedColor   | Color | 失败圆环与感叹号     |
| waitingColor  | Color | 等待时钟             |
| titleColor    | Color | 标题                 |
| subtitleColor | Color | 副标题               |
