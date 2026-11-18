# CameraHint

`CameraHint` 是相机的浮动提示，移植自相机的 `CameraScreenHintView` 及其 `HintTextView`：取景画面上方的一行简短白字，例如“长按快门连拍”，衬在一块会淡入淡出的深色小面板上。`CameraOutlinedText` 移植自相机的 `StrokeTextView`，直接把文字画在取景画面上，并带一圈细的深色描边，在明亮场景中依然清晰可读。

<div style="position: relative; height: 320px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=cameraHint" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.CameraHint
import io.github.suqi8.coui.kmp.basic.CameraHintBackground
import io.github.suqi8.coui.kmp.basic.CameraHintDefaults
import io.github.suqi8.coui.kmp.basic.CameraOutlinedText
```

## 基本用法

```kotlin
var visible by remember { mutableStateOf(true) }

// 相机在提示出现 3 秒后将其隐藏。
LaunchedEffect(visible) {
    if (visible) {
        delay(CameraHintDefaults.AutoHideMillis)
        visible = false
    }
}

CameraHint(
    text = "长按快门连拍",
    visible = visible,
)
```

单行提示衬在高 24dp、圆角 4dp、左右内边距 6dp 的面板上，文字为 12dp 白色。宽于 `maxWidth`（322dp）或包含换行符的文字会换行并撑高面板，上下内边距 4dp，行间额外增加 4dp，居中排列。

### 显示与隐藏

- 显示时提示在 `showDelayMillis` 之后用 250ms 淡入，曲线 (0.33, 0, 0.67, 1)。
- 显示期间更换 `text`，新文字会从透明重新淡入，与相机一致。
- 隐藏时用 100ms 淡出，曲线相同；完全隐藏的提示不占空间。
- 相机会在大多数提示出现 `CameraHintDefaults.AutoHideMillis`（3000ms）后将其隐藏。用计时器驱动 `visible` 即可实现同样效果。

### 背景

| 背景  | 外观                                         |
| ----- | -------------------------------------------- |
| Dark  | 默认。50% 黑色面板                           |
| Light | 20% 白色面板                                 |
| None  | 无面板；文字改为投下柔和的 70% 黑色阴影      |

```kotlin
CameraHint(
    text = "请对准拍摄主体",
    background = CameraHintBackground.None,
)
```

### 材质模糊

在会模糊提示背景的设备上，面板是相机的材质模糊（80dp 半径，15% 灰的发光叠加覆盖在 30% 灰、60% 混合之上），其上再叠一层 20% 浅灰。传入一个记录提示下方内容的 `MaterialBlurProvider`；`Dark` 与 `Light` 都会变为模糊，`None` 则忽略它。

```kotlin
CameraHint(
    text = "长按快门连拍",
    materialBlur = provider, // 记录取景画面，而不是提示本身
)
```

### 描边文字

`CameraOutlinedText` 把文字画两遍：先画 0.333dp、40% 黑色、圆头的描边，再在上方填充。

```kotlin
CameraOutlinedText(text = "滑动切换滤镜")
```

`brush` 用渐变代替 `color` 填充文字，与相机的渐变引导标题相同：

```kotlin
CameraOutlinedText(
    text = "重塑光影",
    brush = Brush.horizontalGradient(CameraHintDefaults.GuideGradientColors),
    textSize = 20.dp,
    fontWeight = FontWeight.Bold,
    strokeWidth = 0.dp,
)
```

## 属性

### CameraHint 属性

| 属性名          | 类型                  | 说明                                     | 默认值                              | 是否必须 |
| --------------- | --------------------- | ---------------------------------------- | ----------------------------------- | -------- |
| text            | String                | 提示文字                                 | -                                   | 是       |
| modifier        | Modifier              | 应用于提示的修饰符                       | Modifier                            | 否       |
| visible         | Boolean               | 是否显示                                 | true                                | 否       |
| background      | CameraHintBackground  | 文字后方的面板                           | CameraHintBackground.Dark           | 否       |
| showDelayMillis | Int                   | 淡入前的延迟                             | 0                                   | 否       |
| materialBlur    | MaterialBlurProvider? | 在面板后方绘制材质模糊；null 保持底色    | null                                | 否       |
| maxWidth        | Dp                    | 超过此宽度时文字换行                     | CameraHintDefaults.MaxWidth         | 否       |
| textSize        | Dp                    | 文字大小（dp，不随字体缩放）             | CameraHintDefaults.TextSize         | 否       |
| colors          | CameraHintColors      | 提示颜色                                 | CameraHintDefaults.cameraHintColors() | 否     |

### CameraOutlinedText 属性

| 属性名      | 类型       | 说明                                   | 默认值                              | 是否必须 |
| ----------- | ---------- | -------------------------------------- | ----------------------------------- | -------- |
| text        | String     | 文字                                   | -                                   | 是       |
| modifier    | Modifier   | 应用于文字的修饰符                     | Modifier                            | 否       |
| color       | Color      | 填充颜色                               | Color.White                         | 否       |
| brush       | Brush?     | 覆盖 `color` 的填充画刷                | null                                | 否       |
| textSize    | Dp         | 文字大小（dp，不随字体缩放）           | CameraHintDefaults.OutlinedTextSize | 否       |
| fontWeight  | FontWeight | 字重                                   | FontWeight.Normal                   | 否       |
| textAlign   | TextAlign  | 行对齐方式                             | TextAlign.Center                    | 否       |
| maxLines    | Int        | 最大行数，末行以省略号结尾             | Int.MAX_VALUE                       | 否       |
| strokeWidth | Dp         | 描边宽度；0dp 不画描边                 | CameraHintDefaults.TextStrokeWidth  | 否       |
| strokeColor | Color      | 描边颜色                               | CameraHintDefaults.TextStrokeColor  | 否       |

## 默认值对象

### CameraHintDefaults

| 属性名              | 类型              | 说明                               | 值                        |
| ------------------- | ----------------- | ---------------------------------- | ------------------------- |
| TextSize            | Dp                | 提示文字大小                       | 12.dp                     |
| MinHeight           | Dp                | 单行面板高度                       | 24.dp                     |
| CornerRadius        | Dp                | 面板圆角半径                       | 4.dp                      |
| HorizontalPadding   | Dp                | 水平内边距                         | 6.dp                      |
| VerticalPadding     | Dp                | 垂直内边距                         | 4.dp                      |
| LineSpacingExtra    | Dp                | 换行后的额外行间距                 | 4.dp                      |
| MaxWidth            | Dp                | 超过此宽度时文字换行               | 322.dp                    |
| BlurRadius          | Dp                | 材质模糊半径                       | 80.dp                     |
| ShowDuration        | Int               | 淡入时长，毫秒                     | 250                       |
| HideDuration        | Int               | 淡出时长，毫秒                     | 100                       |
| AutoHideMillis      | Long              | 相机显示提示的时长，毫秒           | 3000                      |
| TextStrokeWidth     | Dp                | `CameraOutlinedText` 的描边宽度    | 0.333.dp                  |
| TextStrokeColor     | Color             | `CameraOutlinedText` 的描边颜色    | #66000000                 |
| OutlinedTextSize    | Dp                | `CameraOutlinedText` 的文字大小    | 12.dp                     |
| GuideGradientColors | List&lt;Color&gt; | 引导标题的渐变色                   | #ECECEC, #E987AB, #6687F9 |

| 函数               | 返回类型         | 说明           |
| ------------------ | ---------------- | -------------- |
| cameraHintColors() | CameraHintColors | 固定的相机配色 |

## 颜色类

### CameraHintColors

| 属性名              | 类型  | 说明                     | 默认值          |
| ------------------- | ----- | ------------------------ | --------------- |
| textColor           | Color | 文字                     | White           |
| darkContainerColor  | Color | `Dark` 面板              | #7F000000       |
| lightContainerColor | Color | `Light` 面板             | #33FFFFFF       |
| textShadowColor     | Color | `None` 时的文字阴影      | #B3000000       |
| blurFillColor       | Color | 材质模糊上方的填充       | #33A6A6A6       |
| blurMixColor        | Color | 材质模糊混合色           | 30% 灰，60% 透明度 |
| blurBlendColor      | Color | 材质模糊发光叠加色       | 15% 灰，15% 透明度 |
