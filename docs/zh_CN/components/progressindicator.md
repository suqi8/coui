# ProgressIndicator

`ProgressIndicator` 是 COUI 中的进度指示组件，用于展示操作的进度状态。提供了线性进度条、环形进度条、无限旋转指示器和旋转加载指示器四种样式，适用于不同场景下的加载和进度展示需求。

<div style="position: relative; height: 250px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=progressIndicator" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.LinearProgressIndicator // 线性进度条
import io.github.suqi8.coui.kmp.basic.CircularProgressIndicator // 环形进度条
import io.github.suqi8.coui.kmp.basic.InfiniteProgressIndicator // 无限旋转指示器
import io.github.suqi8.coui.kmp.basic.RotatingProgressIndicator // 旋转加载指示器
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

### 无限旋转指示器

无限旋转指示器适用于无法确定操作时长的场景，默认使用主题强调色着色，与 ColorOS「正在刷新…」的旋转指示器一致：

```kotlin
InfiniteProgressIndicator()
```

### 旋转加载指示器

`RotatingProgressIndicator` 是 ColorOS 系统默认的不确定态加载指示器：一段没有背景圆环的裸圆弧，端点为平头（butt cap），在 1250 ms 的一个周期内旋转两整圈，同时弧长在 273.6° 与 50.4° 之间脉动。它直接移植自 `Theme.COUI` 通过 `couiRotatingSpinnerJsonName` 属性绑定的 `coui_rotating_loading.json` 资源，用单个 `drawArc` 实现，因此不需要引入 Lottie 运行时。

```kotlin
RotatingProgressIndicator()
```

它提供默认（26dp）和小号（16dp）两档尺寸，分别对应 `coui_lottie_loading_view_large_*` 与 `coui_lottie_loading_view_small_*`：

```kotlin
// 小号旋转加载指示器
RotatingProgressIndicator(
    size = ProgressIndicatorDefaults.SmallRotatingProgressIndicatorSize,
    ringDiameter = ProgressIndicatorDefaults.SmallRotatingProgressIndicatorRingDiameter,
    strokeWidth = ProgressIndicatorDefaults.SmallRotatingProgressIndicatorStrokeWidth
)
```

### 尺寸规格

环形与无限旋转指示器提供中号（默认）和大号两种规格，定义在 `ProgressIndicatorDefaults` 中：

```kotlin
// Large circular progress indicator
CircularProgressIndicator(
    size = ProgressIndicatorDefaults.LargeCircularProgressIndicatorSize,
    strokeWidth = ProgressIndicatorDefaults.LargeCircularProgressIndicatorStrokeWidth
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
| colors   | ProgressIndicatorColors | 进度条的颜色配置               | ProgressIndicatorDefaults.progressIndicatorColors()            | 否       |
| height   | Dp                      | 进度条的高度                   | ProgressIndicatorDefaults.DefaultLinearProgressIndicatorHeight | 否       |

### CircularProgressIndicator 属性

| 属性名      | 类型                    | 说明                           | 默认值                                                                | 是否必须 |
| ----------- | ----------------------- | ------------------------------ | --------------------------------------------------------------------- | -------- |
| modifier    | Modifier                | 应用于进度条的修饰符           | Modifier                                                              | 否       |
| progress    | Float?                  | 当前进度值，null表示不确定状态 | null                                                                  | 否       |
| colors      | ProgressIndicatorColors | 进度条的颜色配置               | ProgressIndicatorDefaults.circularProgressIndicatorColors()           | 否       |
| strokeWidth | Dp                      | 环形进度条的描边宽度           | ProgressIndicatorDefaults.DefaultCircularProgressIndicatorStrokeWidth | 否       |
| size        | Dp                      | 环形进度条的大小               | ProgressIndicatorDefaults.DefaultCircularProgressIndicatorSize        | 否       |

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
| color        | Color    | 圆弧的颜色               | COUITheme.colorScheme.onSurfaceContainer                               | 否       |
| size         | Dp       | 指示器方形视图框的大小   | ProgressIndicatorDefaults.DefaultRotatingProgressIndicatorSize         | 否       |
| ringDiameter | Dp       | 圆弧描边中心线的直径     | ProgressIndicatorDefaults.DefaultRotatingProgressIndicatorRingDiameter | 否       |
| strokeWidth  | Dp       | 圆弧的描边宽度           | ProgressIndicatorDefaults.DefaultRotatingProgressIndicatorStrokeWidth  | 否       |

### ProgressIndicatorDefaults 对象

ProgressIndicatorDefaults 对象提供了进度指示器组件的默认值和颜色配置。

#### 常量

| 常量名                                          | 类型 | 默认值 | 说明                     |
| ----------------------------------------------- | ---- | ------ | ------------------------ |
| DefaultLinearProgressIndicatorHeight            | Dp   | 4.dp   | 线性进度条的默认高度     |
| DefaultCircularProgressIndicatorStrokeWidth     | Dp   | 3.dp   | 环形进度条的默认描边宽度 |
| DefaultCircularProgressIndicatorSize            | Dp   | 30.dp  | 环形进度条的默认大小     |
| DefaultInfiniteProgressIndicatorStrokeWidth     | Dp   | 2.67.dp | 无限指示器的默认描边宽度 |
| DefaultInfiniteProgressIndicatorSize            | Dp   | 18.dp  | 无限指示器的默认大小     |
| LargeCircularProgressIndicatorStrokeWidth       | Dp   | 5.dp   | 大号环形进度条的描边宽度 |
| LargeCircularProgressIndicatorSize              | Dp   | 40.dp  | 大号环形进度条的大小     |
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
| circularProgressIndicatorColors() | ProgressIndicatorColors | 创建环形进度指示器的默认颜色配置 |

### ProgressIndicatorColors 类

| 属性名                  | 类型  | 说明                         |
| ----------------------- | ----- | ---------------------------- |
| foregroundColor         | Color | 进度指示器的前景色           |
| disabledForegroundColor | Color | 禁用状态时进度指示器的前景色 |
| backgroundColor         | Color | 进度指示器的背景色           |

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
