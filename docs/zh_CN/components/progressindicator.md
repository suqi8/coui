# ProgressIndicator

`ProgressIndicator` 是 COUI 中的进度指示组件，用于展示操作的进度状态。提供了线性进度条、环形进度条、无限旋转指示器、旋转加载指示器以及带提示文字的组合加载指示器，适用于不同场景下的加载和进度展示需求。

<div style="position: relative; height: 250px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=progressIndicator" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.LinearProgressIndicator // 线性进度条
import io.github.suqi8.coui.kmp.basic.CircularProgressIndicator // 环形进度条
import io.github.suqi8.coui.kmp.basic.InfiniteProgressIndicator // 无限旋转指示器
import io.github.suqi8.coui.kmp.basic.RotatingProgressIndicator // 旋转加载指示器
import io.github.suqi8.coui.kmp.basic.CompProgressIndicator // 带提示文字的加载指示器
```

## 基本用法

### 线性进度条

线性进度条可用于展示操作的进度：

```kotlin
// 确定进度的线性进度条
var progress by remember { mutableStateOf(0.3f) }

LinearProgressIndicator(progress = progress)
```

```kotlin
// 不确定进度的线性进度条
LinearProgressIndicator()
```

::: tip
确定进度的线性进度条移植自 `COUIHorizontalProgressBar`（进度变化无动画；RTL 布局下从右侧开始填充）。不确定进度的三段循环动画是本库的扩展：ColorOS 的水平进度条没有不确定模式。
:::

```kotlin
// 禁用的线性进度条（使用禁用的进度色与轨道色）
LinearProgressIndicator(progress = 0.5f, enabled = false)
```

### 环形进度条

环形进度条适用于需要节省空间的场景：

```kotlin
// 确定进度的环形进度条
var progress by remember { mutableStateOf(0.7f) }

CircularProgressIndicator(progress = progress)
```

```kotlin
// 不确定进度的环形进度条
CircularProgressIndicator()
```

传入进度值时移植自 `COUICircularProgressBar`：中性色圆弧叠在分割线色轨道上，进度变化走临界阻尼弹簧（阻尼比 1、刚度 50）。与原版一样，布局尺寸为 `size + 2 × 4dp`，圆环外径为 `size − 2 × 4dp`：默认大号档（40dp，描边 5dp）是 48dp 的框、32dp 的圆环；中号档（30dp，描边 3dp）是 38dp 的框、22dp 的圆环。

不传进度时移植自 `COUILoadingView`：18dp 圆环（描边 2.67dp），主色圆弧（`#0080FF` / `#1A8CFF`）叠在主色淡染轨道（`#260080FF` / `#401A8CFF`）上，即 `ProgressIndicatorDefaults.loadingViewColors()`。圆弧每秒转一圈；与在 `onDraw` 里读取时钟的 `COUILoadingView` 一样，它的相位取自帧时钟，所以同屏的所有加载圈同步旋转。

#### 状态与类型

确定进度的环形进度条接受 `CircularProgressState`（对应原版 `pause()` / `resume()` / `error()` / `recover()`）与 `CircularProgressType`（`couiCircularProgressBarType`）：

```kotlin
// 错误：圆环在 350ms 内收缩成 19dp 的细圈，中间出现感叹号
CircularProgressIndicator(progress = 0.6f, state = CircularProgressState.Error)

// 暂停：只有图上类型可以暂停；圆环淡出，暂停图标淡入
CircularProgressIndicator(
    progress = 0.6f,
    state = CircularProgressState.Paused,
    type = CircularProgressType.OnImage
)
```

| 类型        | 外观                                                         |
| ----------- | ------------------------------------------------------------ |
| Default     | `TYPE_DEFAULT`                                               |
| OnImage     | `TYPE_ON_IMAGE`：轨道带 4dp、下移 2dp 的阴影；唯一可以暂停的类型 |
| FollowTheme | `TYPE_FOLLOW_THEME`：轨道透明度 89 / 255                      |

### 无限旋转指示器

无限旋转指示器适用于无法确定操作时长的场景，默认使用主题强调色着色，与 ColorOS「正在刷新…」的旋转指示器一致：

```kotlin
InfiniteProgressIndicator()
```

### 旋转加载指示器

`RotatingProgressIndicator` 是 ColorOS 系统默认的不确定态加载指示器：一段没有背景圆环的裸圆弧，端点为平头（butt cap），以 85% 不透明度的黑/白色绘制（`#D9000000` / `#D9FFFFFF`），在 76 帧（1266.7 ms）的一个周期内旋转两整圈，同时弧长在 273.6° 与 50.4° 之间脉动。它直接移植自 `Theme.COUI` 通过 `couiRotatingSpinnerJsonName` 属性绑定的 `coui_rotating_loading.json` 资源，用单个 `drawArc` 实现，因此不需要引入 Lottie 运行时。

```kotlin
RotatingProgressIndicator()
```

它提供默认（26dp）和小号（16dp）两档尺寸，分别对应 `coui_lottie_loading_view_large_*` 与 `coui_lottie_loading_view_small_*`。小号即 `Widget.COUI.COUILottieLoadingView` 加载圈，其资源（`coui_lottie_loading_small.json`）还把整个图层降到 30% 不透明度，所以圆弧实际为 64 / 255（`#40000000` / `#40FFFFFF`）：

```kotlin
// 小号旋转加载指示器
RotatingProgressIndicator(
    color = ProgressIndicatorDefaults.smallRotatingProgressIndicatorColor(),
    size = ProgressIndicatorDefaults.SmallRotatingProgressIndicatorSize,
    ringDiameter = ProgressIndicatorDefaults.SmallRotatingProgressIndicatorRingDiameter,
    strokeWidth = ProgressIndicatorDefaults.SmallRotatingProgressIndicatorStrokeWidth
)
```

### 带提示文字的加载指示器

`CompProgressIndicator` 移植自 `COUICompProgressIndicator`：26dp（大号）或 14dp（小号）的旋转加载动画，可附带提示文字（couiTextBodyS / couiColorLabelSecondary）——横排时单行、位于小号动画右侧 4dp；竖排时最多两行，位于动画下方 16dp（大号）/ 12dp（小号）。

```kotlin
CompProgressIndicator(
    type = CompProgressIndicatorType.LargeWithTextVertical,
    text = "正在加载…"
)
```

| CompProgressIndicatorType | 原版                                   |
| ------------------------- | -------------------------------------- |
| Large                     | `LARGE_ANIMATION`                      |
| Small                     | `SMALL_ANIMATION`                      |
| SmallWithTextHorizontal   | `SMALL_ANIMATION_WITH_TEXT_HORIZONTAL` |
| LargeWithTextVertical     | `LARGE_ANIMATION_WITH_TEXT_VERTICAL`   |
| SmallWithTextVertical     | `SMALL_ANIMATION_WITH_TEXT_VERTICAL`   |

### 尺寸规格

确定进度的环形进度条默认为大号档，另提供中号档；无限旋转指示器提供默认与大号两档，均定义在 `ProgressIndicatorDefaults` 中：

```kotlin
// 中号环形进度条
CircularProgressIndicator(
    progress = 0.5f,
    size = ProgressIndicatorDefaults.MediumCircularProgressIndicatorSize,
    strokeWidth = ProgressIndicatorDefaults.MediumCircularProgressIndicatorStrokeWidth
)

// Large infinite progress indicator
InfiniteProgressIndicator(
    size = ProgressIndicatorDefaults.LargeInfiniteProgressIndicatorSize,
    strokeWidth = ProgressIndicatorDefaults.LargeInfiniteProgressIndicatorStrokeWidth
)
```

## 组件状态

所有进度指示器组件都支持确定进度和不确定进度两种状态：

### 确定进度状态

当提供具体的进度值（0.0-1.0之间的浮点数）时，进度指示器会显示确切的进度：

```kotlin
var progress by remember { mutableStateOf(0.6f) }

LinearProgressIndicator(progress = progress)
CircularProgressIndicator(progress = progress)
```

### 不确定进度状态

当进度值为 null 时，进度指示器会显示动画效果，表示操作正在进行但无法确定具体进度：

```kotlin
LinearProgressIndicator(progress = null)
CircularProgressIndicator(progress = null)
```

## 属性

### LinearProgressIndicator 属性

| 属性名   | 类型                    | 说明                           | 默认值                                                         | 是否必须 |
| -------- | ----------------------- | ------------------------------ | -------------------------------------------------------------- | -------- |
| modifier | Modifier                | 应用于进度条的修饰符           | Modifier                                                       | 否       |
| progress | Float?                  | 当前进度值，null表示不确定状态 | null                                                           | 否       |
| enabled  | Boolean                 | 进度条是否启用                 | true                                                           | 否       |
| colors   | ProgressIndicatorColors | 进度条的颜色配置               | ProgressIndicatorDefaults.progressIndicatorColors()            | 否       |
| height   | Dp                      | 进度条的高度                   | ProgressIndicatorDefaults.DefaultLinearProgressIndicatorHeight | 否       |

### CircularProgressIndicator 属性

| 属性名      | 类型                    | 说明                           | 默认值                                                                | 是否必须 |
| ----------- | ----------------------- | ------------------------------ | --------------------------------------------------------------------- | -------- |
| modifier    | Modifier                | 应用于进度条的修饰符           | Modifier                                                              | 否       |
| progress    | Float?                  | 当前进度值，null表示不确定状态 | null                                                                  | 否       |
| state       | CircularProgressState   | Normal / Paused / Error（仅确定进度） | CircularProgressState.Normal                                   | 否       |
| type        | CircularProgressType    | Default / OnImage / FollowTheme（仅确定进度） | CircularProgressType.Default                           | 否       |
| colors      | ProgressIndicatorColors | 进度条的颜色配置               | 有进度时 circularProgressIndicatorColors()，无进度时 loadingViewColors() | 否       |
| strokeWidth | Dp                      | 环形进度条的描边宽度           | 有进度时 LargeCircularProgressIndicatorStrokeWidth，无进度时 DefaultLoadingViewStrokeWidth | 否 |
| size        | Dp                      | 有进度时为长度档（布局四周再加 4dp），无进度时为直径 | 有进度时 LargeCircularProgressIndicatorSize，无进度时 DefaultLoadingViewSize | 否 |

### CompProgressIndicator 属性

| 属性名    | 类型                      | 说明                               | 默认值                                                     | 是否必须 |
| --------- | ------------------------- | ---------------------------------- | ---------------------------------------------------------- | -------- |
| modifier  | Modifier                  | 应用于指示器的修饰符               | Modifier                                                   | 否       |
| paused    | Boolean                   | 让旋转动画停在当前帧               | false                                                      | 否       |
| type      | CompProgressIndicatorType | 布局类型                           | CompProgressIndicatorType.Large                            | 否       |
| text      | String?                   | 提示文字，仅 `...WithText...` 类型绘制 | null                                                   | 否       |
| color     | Color                     | 旋转圆弧的颜色                     | ProgressIndicatorDefaults.rotatingProgressIndicatorColor() | 否       |
| textColor | Color                     | 提示文字颜色                       | CompProgressIndicatorDefaults.textColor()                  | 否       |
| textStyle | TextStyle                 | 提示文字样式                       | COUITheme.textStyles.bodyS                                 | 否       |

### InfiniteProgressIndicator 属性

| 属性名          | 类型     | 说明                 | 默认值                                                                    | 是否必须 |
| --------------- | -------- | -------------------- | ------------------------------------------------------------------------- | -------- |
| modifier        | Modifier | 应用于进度条的修饰符 | Modifier                                                                  | 否       |
| color           | Color    | 圆弧的颜色           | COUITheme.colorScheme.primary                                             | 否       |
| size            | Dp       | 进度指示器的大小     | ProgressIndicatorDefaults.DefaultInfiniteProgressIndicatorSize            | 否       |
| strokeWidth     | Dp       | 圆弧的描边宽度       | ProgressIndicatorDefaults.DefaultInfiniteProgressIndicatorStrokeWidth     | 否       |

### RotatingProgressIndicator 属性

| 属性名       | 类型     | 说明                     | 默认值                                                                 | 是否必须 |
| ------------ | -------- | ------------------------ | ---------------------------------------------------------------------- | -------- |
| modifier     | Modifier | 应用于指示器的修饰符     | Modifier                                                               | 否       |
| paused       | Boolean  | 停在当前帧（`pauseAnimation`），取消暂停后从该帧继续 | false                                      | 否       |
| color        | Color    | 圆弧的颜色               | ProgressIndicatorDefaults.rotatingProgressIndicatorColor()             | 否       |
| size         | Dp       | 指示器方形视图框的大小   | ProgressIndicatorDefaults.DefaultRotatingProgressIndicatorSize         | 否       |
| ringDiameter | Dp       | 圆弧描边中心线的直径     | ProgressIndicatorDefaults.DefaultRotatingProgressIndicatorRingDiameter | 否       |
| strokeWidth  | Dp       | 圆弧的描边宽度           | ProgressIndicatorDefaults.DefaultRotatingProgressIndicatorStrokeWidth  | 否       |

### ProgressIndicatorDefaults 对象

ProgressIndicatorDefaults 对象提供了进度指示器组件的默认值和颜色配置。

#### 常量

| 常量名                                          | 类型 | 默认值 | 说明                     |
| ----------------------------------------------- | ---- | ------ | ------------------------ |
| DefaultLinearProgressIndicatorHeight            | Dp   | 4.dp   | 线性进度条的默认高度     |
| MediumCircularProgressIndicatorStrokeWidth      | Dp   | 3.dp   | 中号环形进度条的描边宽度 |
| MediumCircularProgressIndicatorSize             | Dp   | 30.dp  | 中号环形进度条的长度     |
| LargeCircularProgressIndicatorStrokeWidth       | Dp   | 5.dp   | 大号（默认）环形进度条的描边宽度 |
| LargeCircularProgressIndicatorSize              | Dp   | 40.dp  | 大号（默认）环形进度条的长度 |
| DefaultLoadingViewStrokeWidth                   | Dp   | 2.67.dp | 不确定进度环形指示器的描边宽度 |
| DefaultLoadingViewSize                          | Dp   | 18.dp  | 不确定进度环形指示器的大小 |
| LargeLoadingViewStrokeWidth                     | Dp   | 3.33.dp | 大号加载视图的描边宽度 |
| LargeLoadingViewSize                            | Dp   | 26.dp  | 大号加载视图的大小       |
| DefaultInfiniteProgressIndicatorStrokeWidth     | Dp   | 2.67.dp | 无限指示器的默认描边宽度 |
| DefaultInfiniteProgressIndicatorSize            | Dp   | 18.dp  | 无限指示器的默认大小     |
| LargeInfiniteProgressIndicatorStrokeWidth       | Dp   | 3.33.dp | 大号无限指示器的描边宽度 |
| LargeInfiniteProgressIndicatorSize              | Dp   | 26.dp  | 大号无限指示器的大小     |
| DefaultRotatingProgressIndicatorSize            | Dp   | 26.dp  | 旋转指示器默认视图框大小 |
| DefaultRotatingProgressIndicatorRingDiameter    | Dp   | 24.14.dp | 旋转指示器默认圆环直径 |
| DefaultRotatingProgressIndicatorStrokeWidth     | Dp   | 1.857.dp | 旋转指示器默认描边宽度 |
| SmallRotatingProgressIndicatorSize              | Dp   | 16.dp  | 小号旋转指示器视图框大小 |
| SmallRotatingProgressIndicatorRingDiameter      | Dp   | 12.68.dp | 小号旋转指示器圆环直径 |
| SmallRotatingProgressIndicatorStrokeWidth       | Dp   | 1.811.dp | 小号旋转指示器描边宽度 |
| MaxRotatingProgressIndicatorSize                | Dp   | 40.dp  | 旋转指示器支持的最大尺寸 |

#### 方法

| 方法名                    | 类型                    | 说明                         |
| ------------------------- | ----------------------- | ---------------------------- |
| progressIndicatorColors()         | ProgressIndicatorColors | 创建线性进度指示器的默认颜色配置 |
| circularProgressIndicatorColors() | ProgressIndicatorColors | 创建确定进度环形指示器的默认颜色配置 |
| loadingViewColors()               | ProgressIndicatorColors | 创建不确定进度环形指示器的主色配置 |
| rotatingProgressIndicatorColor()  | Color                   | 旋转加载指示器的默认圆弧色（85% 不透明度） |
| smallRotatingProgressIndicatorColor() | Color               | 小号旋转加载指示器的圆弧色（64 / 255）     |

### ProgressIndicatorColors 类

| 属性名                  | 类型  | 说明                         |
| ----------------------- | ----- | ---------------------------- |
| foregroundColor         | Color | 进度指示器的前景色           |
| disabledForegroundColor | Color | 禁用状态时进度指示器的前景色 |
| backgroundColor         | Color | 进度指示器的背景色           |
| disabledBackgroundColor | Color | 禁用状态时进度指示器的背景色（默认等于 backgroundColor） |
| errorColor              | Color | 错误状态环形指示器的圆环与图标颜色（默认等于 foregroundColor） |
| pauseIconColor          | Color | 暂停状态环形指示器的图标颜色（默认等于 foregroundColor） |
| shadowColor             | Color | 图上类型环形指示器的阴影（默认透明） |

## 进阶用法

### 自定义颜色的线性进度条

```kotlin
var progress by remember { mutableStateOf(0.4f) }

LinearProgressIndicator(
    progress = progress,
    colors = ProgressIndicatorDefaults.progressIndicatorColors(
        foregroundColor = Color.Red,
        backgroundColor = Color.LightGray
    )
)
```

### 调整大小的环形进度条

```kotlin
var progress by remember { mutableStateOf(0.75f) }

CircularProgressIndicator(
    progress = progress,
    size = 50.dp,
    strokeWidth = 6.dp
)
```

### 结合按钮使用的加载状态

```kotlin
var isLoading by remember { mutableStateOf(false) }
val scope = rememberCoroutineScope()

Button(
    onClick = {
        isLoading = true
        // 模拟操作
        scope.launch {
            delay(2000)
            isLoading = false
        }
    },
    enabled = !isLoading
) {
     AnimatedVisibility(
        visible = isLoading
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .padding(end = 8.dp),
            size = 20.dp,
            strokeWidth = 4.dp
        )
    }
    Text("提交")
}
```

### 自定义无限旋转指示器

```kotlin
InfiniteProgressIndicator(
    color = COUITheme.colorScheme.primary,
    size = 40.dp,
    strokeWidth = 3.dp
)
```

### 结合卡片使用的加载状态

```kotlin
var isLoading by remember { mutableStateOf(true) }

Card(
    modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .padding(16.dp)
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("加载中...")
            }
        } else {
            Text("内容已加载")
        }
    }
}
// 控制加载状态
LaunchedEffect(Unit) {
    delay(3000)
    isLoading = false
}
```
