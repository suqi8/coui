# ExposureBar

`ExposureBar` 是相机的曝光补偿（EV）面板，移植自相机的全局 EV 条（`FocusExposureUIManager` 用 `BottomEffectContainer` 包裹 `HSScaleBar` 与 `ResetImageView`）。深色胶囊里是一条横向刻度尺，每个 EV 档位一根刻度，起始端有一个重置按钮。面板内部的刻度尺 `ScaleBar` 也可以单独使用，用于 ISO、快门速度等其他相机参数。

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=exposureBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.ExposureBar
import io.github.suqi8.coui.kmp.basic.ExposureBarDefaults
import io.github.suqi8.coui.kmp.basic.ScaleBar
```

## 基本用法

```kotlin
var index by remember { mutableIntStateOf(ExposureBarDefaults.Values.size / 2) } // 0.0 EV

ExposureBar(
    selectedIndex = index,
    onSelectedIndexChange = { index = it },
)
```

默认档位是相机的 -2.0..+2.0 EV、1/3 步进（13 根刻度，每个整数 EV 一根大刻度）。面板尺寸 336dp x 60dp、圆角 32dp；刻度尺左右各内缩 64dp，32dp 的重置按钮距起始端和顶部各 14dp。

### 交互

- 拖动时刻度在固定的橙色指示线下移动；指示线上方的数值始终显示最近的档位。
- 拖过两端时刻度尺按橡皮筋拉伸（手指位移的 5%，刻度间距再展开其一半），松手后以 (0.3, 0, 0.1, 1) 曲线在 350ms 内回弹。
- 松手速度超过 500px/s 时继续惯性滑动：400ms、(0, 0, 0.58, 1) 曲线、距离为速度的 30%（超过 30 个档位时为 600ms、(0.3, 0.1, 0, 1)、15%），停下后吸附到最近档位。
- 两端最外 49dp 内的刻度逐渐淡出。
- 手势中每次切换选中档位都会触发一次轻刻度震动。
- 当前值已是 `defaultIndex` 时重置按钮变为 30% 透明度。点击后箭头旋转一圈（300ms），刻度尺在 300ms 内滑回默认档位。

### 材质模糊

在相机取景画面上，胶囊是材质模糊（半径 80dp，30% 灰 60% 不透明度混合后，再以 15% 灰做 glow overlay）。传入一个记录面板下方内容的 `MaterialBlurProvider` 即可；不传时面板使用相机的无模糊兜底：不透明 `#141414` 胶囊加 0.67dp 的 10% 白色描边。

```kotlin
ExposureBar(
    selectedIndex = index,
    onSelectedIndexChange = { index = it },
    materialBlur = provider, // 记录取景画面，不能包含面板本身
)
```

### 专业模式范围

相机专业模式使用 -3.0..+3.0 EV：

```kotlin
ExposureBar(
    selectedIndex = index,
    onSelectedIndexChange = { index = it },
    values = ExposureBarDefaults.WideValues,
)
```

### 单独使用 ScaleBar

```kotlin
val isos = listOf("50", "64", "80", "100", "125", "160", "200", "250", "320", "400")
var iso by remember { mutableIntStateOf(3) }

ScaleBar(
    labels = isos,
    selectedIndex = iso,
    onSelectedIndexChange = { iso = it },
    valueSuffix = " ISO",
)
```

## 属性

### ExposureBar 属性

| 属性名                | 类型                     | 说明                                         | 默认值                                    | 是否必须 |
| --------------------- | ------------------------ | -------------------------------------------- | ----------------------------------------- | -------- |
| selectedIndex         | Int                      | `values` 中选中档位的索引                    | -                                         | 是       |
| onSelectedIndexChange | (Int) -> Unit            | 拖动、惯性滑动、吸附或重置时回调             | -                                         | 是       |
| modifier              | Modifier                 | 应用于面板的修饰符                           | Modifier                                  | 否       |
| enabled               | Boolean                  | 刻度尺和重置按钮是否响应输入                 | true                                      | 否       |
| values                | List&lt;String&gt;       | 各档位标签，从低到高                         | ExposureBarDefaults.Values                | 否       |
| defaultIndex          | Int                      | 重置按钮回到的档位                           | values.size / 2                           | 否       |
| showResetButton       | Boolean                  | 是否显示重置按钮                             | true                                      | 否       |
| materialBlur          | MaterialBlurProvider?    | 在胶囊后绘制材质模糊；null 时使用不透明底色  | null                                      | 否       |
| colors                | ExposureBarColors        | 面板颜色                                     | ExposureBarDefaults.exposureBarColors()   | 否       |

### ScaleBar 属性

| 属性名                | 类型                     | 说明                                         | 默认值                                    | 是否必须 |
| --------------------- | ------------------------ | -------------------------------------------- | ----------------------------------------- | -------- |
| labels                | List&lt;String&gt;       | 各档位标签，从起点到终点（不可为空）         | -                                         | 是       |
| selectedIndex         | Int                      | 选中档位的索引                               | -                                         | 是       |
| onSelectedIndexChange | (Int) -> Unit            | 拖动、惯性滑动或吸附时回调                   | -                                         | 是       |
| modifier              | Modifier                 | 应用于刻度尺的修饰符                         | Modifier                                  | 否       |
| enabled               | Boolean                  | 是否响应拖动                                 | true                                      | 否       |
| showIndicator         | Boolean                  | 是否绘制中心指示线                           | true                                      | 否       |
| majorTickInterval     | Int                      | 每根大刻度跨越的档位数；第一个档位总是大刻度 | ScaleBarDefaults.MajorTickInterval        | 否       |
| valueSuffix           | String                   | 指示线上方数值的后缀                         | ""                                        | 否       |
| valueTextSize         | Dp                       | 数值字号（dp，不随字体缩放）                 | ScaleBarDefaults.ValueTextSize            | 否       |
| colors                | ScaleBarColors           | 刻度尺颜色                                   | ScaleBarDefaults.scaleBarColors()         | 否       |

## 默认值对象

### ExposureBarDefaults

| 属性名                   | 类型          | 说明                         | 值                     |
| ------------------------ | ------------- | ---------------------------- | ---------------------- |
| Values                   | List&lt;String&gt; | -2.0..+2.0 EV，1/3 步进 | 13 个档位              |
| WideValues               | List&lt;String&gt; | -3.0..+3.0 EV，1/3 步进 | 19 个档位              |
| Width                    | Dp            | 面板宽度                     | 336.dp                 |
| Height                   | Dp            | 面板高度                     | 60.dp                  |
| CornerRadius             | Dp            | 胶囊圆角                     | 32.dp                  |
| BlurRadius               | Dp            | 材质模糊半径                 | 80.dp                  |
| BorderWidth              | Dp            | 无模糊底色的描边宽度         | 0.67.dp                |
| ScaleBarHorizontalMargin | Dp            | 刻度尺左右内缩               | 64.dp                  |
| MajorTickInterval        | Int           | 每根大刻度跨越的档位数       | 3                      |
| ValueTextSize            | Dp            | 指示线上方数值字号           | 14.dp                  |
| ResetButtonSize          | Dp            | 重置按钮尺寸                 | 32.dp                  |
| ResetIconSize            | Dp            | 重置图标尺寸                 | 24.dp                  |
| ResetButtonMarginStart   | Dp            | 重置按钮起始边距             | 14.dp                  |
| ResetButtonMarginTop     | Dp            | 重置按钮顶部边距             | 14.dp                  |
| ResetDisabledAlpha       | Float         | 无需重置时的按钮透明度       | 0.3f                   |

| 函数                | 返回类型          | 说明                     |
| ------------------- | ----------------- | ------------------------ |
| exposureBarColors() | ExposureBarColors | 相机固定的浅色前景深色底配色 |

### ScaleBarDefaults

| 属性名            | 类型 | 说明                                     | 值      |
| ----------------- | ---- | ---------------------------------------- | ------- |
| Height            | Dp   | 刻度尺高度                               | 60.dp   |
| TickWidth         | Dp   | 刻度宽度                                 | 1.33.dp |
| MediumTickGap     | Dp   | 不超过 `DenseCount` 个档位时的刻度间距   | 14.dp   |
| SmallTickGap      | Dp   | 超过 `DenseCount` 个档位时的刻度间距     | 6.dp    |
| DenseCount        | Int  | 超过该档位数时刻度变密、惯性更长         | 30      |
| FadeWidth         | Dp   | 两端刻度淡出的宽度                       | 49.dp   |
| TickTop           | Dp   | 刻度顶部（相对垂直中心）                 | 6.dp    |
| TickBottom        | Dp   | 刻度底部（相对垂直中心）                 | 18.dp   |
| IndicatorTop      | Dp   | 指示线顶部（相对垂直中心）               | 0.dp    |
| IndicatorBottom   | Dp   | 指示线底部（相对垂直中心）               | 24.dp   |
| ValueBandTop      | Dp   | 数值区顶部（相对垂直中心）               | -25.dp  |
| ValueBandHeight   | Dp   | 数值区高度                               | 20.dp   |
| ValueTextSize     | Dp   | 数值字号                                 | 18.dp   |
| MajorTickInterval | Int  | 每根大刻度跨越的档位数                   | 2       |

| 函数             | 返回类型       | 说明                     |
| ---------------- | -------------- | ------------------------ |
| scaleBarColors() | ScaleBarColors | 固定的浅色前景深色底刻度配色 |

## 颜色类

### ExposureBarColors

| 属性名           | 类型           | 说明                     | 默认值      |
| ---------------- | -------------- | ------------------------ | ----------- |
| containerColor   | Color          | 无模糊时的胶囊底色       | #141414     |
| borderColor      | Color          | 无模糊时的胶囊描边       | #1AFFFFFF   |
| blurMixColor     | Color          | 材质模糊混合色           | 30% 灰，60% 不透明度 |
| blurBlendColor   | Color          | 材质模糊 glow overlay 色 | 15% 灰，15% 不透明度 |
| resetButtonColor | Color          | 重置按钮圆底             | #1AFFFFFF   |
| resetIconColor   | Color          | 重置箭头                 | 白色        |
| scaleBarColors   | ScaleBarColors | 刻度尺颜色               | ScaleBarDefaults.scaleBarColors() |

### ScaleBarColors

| 属性名         | 类型  | 说明             | 默认值    |
| -------------- | ----- | ---------------- | --------- |
| majorTickColor | Color | 大刻度           | 白色      |
| minorTickColor | Color | 小刻度           | #4DFFFFFF |
| indicatorColor | Color | 中心指示线       | #FB822A   |
| valueColor     | Color | 指示线上方的数值 | 白色      |
