# 模糊效果

`coui-blur` 是一个独立的 Compose Multiplatform 模糊效果库。它通过 Modifier 扩展提供背景模糊、颜色混合和纹理效果。支持 Android、Desktop (JVM)、iOS、macOS 和 Web (WasmJs/Js) 平台。

::: warning 注意
Android 上 `coui-blur` 要求 `minSdk` 33（Android 13）或更高。模糊、混合、
噪点、高光等所有效果均依赖 `RuntimeShader`，该 API 自 33 起才提供。若你的
应用 `minSdk` 低于 33 但仍想引入该库，请用 [下方介绍的能力检查](#运行时能力检查)
门控所有 blur 相关代码路径。
:::

## 配置

在项目中添加 `coui-blur` 依赖：

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.suqi8.coui.kmp:coui-blur:<version>")
        }
    }
}
```

Android 项目：

```kotlin
dependencies {
    implementation("io.github.suqi8.coui.kmp:coui-blur-android:<version>")
}
```

## 平台支持

| 平台          | 最低要求            |
| ------------- | ------------------- |
| Android       | API 33（Android 13）|
| Desktop (JVM) | 支持                |
| iOS / macOS   | 支持                |
| WasmJs / Js   | 支持                |

模糊由一对可分离高斯 `RuntimeShader` 再 wrap 进 `RenderEffect` 实现。Android 上
`RuntimeShader` 自 API 33 起才提供，所以这是整个库（含混合、噪点、高光）的硬下限。

### 运行时能力检查

如果你的应用 `minSdk` 低于 `coui-blur` 但仍想保持单 APK 发布，可以用下面的
能力检查门控 blur 调用。Android 上它们对应 `Build.VERSION.SDK_INT` 判断；
Skiko 系（Desktop / iOS / macOS / Web）始终返回 `true`。

```kotlin
import io.github.suqi8.coui.kmp.shader.isRenderEffectSupported
import io.github.suqi8.coui.kmp.shader.isRuntimeShaderSupported

// API 32+（以及所有非 Android 目标）为 true。仅需要背景记录支架时（例如
// 通过 `colorFilter(...)` 链一个 ColorFilter）适用。
val backdropScaffoldSupported = isRenderEffectSupported()

// API 33+（以及所有非 Android 目标）为 true。任何调用 `blur(...)`、
// `blendColors(...)`、`noiseDither(...)`、`Modifier.textureBlur(...)`、
// 高光样式、或自行构造 `RuntimeShader` 的路径都应被它门控。
val blurAndBlendSupported = isRuntimeShaderSupported()
```

## 基本用法

应用背景模糊分为三步：

1. 创建 `LayerBackdrop` 以捕获背景内容
2. 在内容容器上应用 `Modifier.layerBackdrop()`
3. 在模糊表面上应用 `Modifier.textureBlur()`

```kotlin
import io.github.suqi8.coui.kmp.blur.BlurColors
import io.github.suqi8.coui.kmp.blur.BlurDefaults
import io.github.suqi8.coui.kmp.blur.layerBackdrop
import io.github.suqi8.coui.kmp.blur.rememberLayerBackdrop
import io.github.suqi8.coui.kmp.blur.textureBlur

// 第 1 步：创建 LayerBackdrop
val backdrop = rememberLayerBackdrop()

// 第 2 步：捕获背景内容
Box(
    modifier = Modifier
        .fillMaxSize()
        .layerBackdrop(backdrop) // 捕获此 Box 的内容
) {
    // 背景内容（如图片、渐变或页面内容）
    Image(
        painter = painterResource(Res.drawable.background),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}
```

::: tip 背景色
`layerBackdrop` 仅捕获其所在 composable 绘制的内容，**不包含**父级 composable 的背景（例如 Scaffold 的 Surface）。如果捕获的内容存在透明区域（如无背景的文字），模糊时颜色会扩散到透明像素中，产生明显的色块。

为避免此问题，请在 `onDraw` lambda 中绘制不透明背景：

```kotlin
val backgroundColor = MaterialTheme.colorScheme.backgroundWithCard
val backdrop = rememberLayerBackdrop {
    drawRect(backgroundColor) // 确保捕获到不透明背景
    drawContent()
}
```

:::

```kotlin

// 第 3 步：在叠加层上应用模糊
Box(
    modifier = Modifier
        .size(200.dp)
        .textureBlur(
            backdrop = backdrop,
            shape = RoundedCornerShape(16.dp)
        )
) {
    Text(
        text = "模糊卡片",
        modifier = Modifier.padding(16.dp)
    )
}
```

## 颜色配置

使用 `BlurColors` 在模糊效果之上应用颜色调整和混合图层：

```kotlin
import io.github.suqi8.coui.kmp.blur.BlendColorEntry
import io.github.suqi8.coui.kmp.blur.BlurBlendMode
import io.github.suqi8.coui.kmp.blur.BlurColors

val colors = BlurColors(
    blendColors = listOf(
        BlendColorEntry(
            color = Color.White.copy(alpha = 0.3f),
            mode = BlurBlendMode.SrcOver
        )
    ),
    brightness = 0.05f,  // 范围：[-1, 1]，0 = 无变化
    contrast = 1.1f,     // 倍数，1 = 无变化
    saturation = 1.2f    // 倍数，1 = 无变化
)

Box(
    modifier = Modifier
        .textureBlur(
            backdrop = backdrop,
            shape = RoundedCornerShape(16.dp),
            colors = colors
        )
) {
    // 内容
}
```

也可以使用可组合辅助函数 `BlurDefaults.blurColors()`，它会自动 remember 配置：

```kotlin
val colors = BlurDefaults.blurColors(
    blendColors = listOf(
        BlendColorEntry(Color.White.copy(alpha = 0.2f), BlurBlendMode.Screen)
    ),
    brightness = 0f,
    contrast = 1f,
    saturation = 1.2f
)
```

## 混合模式

`BlurBlendMode` 提供了 40 多种混合模式，用于模糊背景上的颜色混合。

### 标准模式

标准 SkBlendMode 值（0-28），使用与 Skia 兼容的预乘 Alpha 公式：

| 模式 | 说明 |
| --- | --- |
| `SrcOver` | 普通 Alpha 合成（默认） |
| `Screen` | 提亮混合 |
| `Multiply` | 变暗混合 |
| `Overlay` | 增强对比度混合 |
| `SoftLight` | 柔和对比度调整 |
| `ColorDodge` | 通过降低对比度提亮 |
| `ColorBurn` | 通过增加对比度变暗 |
| `Darken` | 保留较暗像素 |
| `Lighten` | 保留较亮像素 |
| `Difference` | 绝对差值 |
| `Exclusion` | 类似 Difference 但对比度较低 |
| `Hue` | 应用源色相 |
| `Saturation` | 应用源饱和度 |
| `Color` | 应用源色相和饱和度 |
| `Luminosity` | 应用源亮度 |

### 自定义模式

自定义模式（100+），实现了 Lab 色彩空间运算、线性光混合等（需要 `isRuntimeShaderSupported()`）：

| 模式 | 说明 |
| --- | --- |
| `LinearLight` | 线性光混合 |
| `LinearLightWithGreyscale` | 带灰度调制的线性光 |
| `LinearLightLab` | Lab 色彩空间中的线性光 |
| `LabLightenWithGreyscale` | 带灰度调制的 Lab 提亮 |
| `LabDarkenWithGreyscale` | 带灰度调制的 Lab 变暗 |
| `Lab` | Lab 颜色映射 |
| `MiColorDodge` | 增强版颜色减淡 |
| `MiColorBurn` | 增强版颜色加深 |
| `PlusDarker` | 带 Alpha 合成的加暗 |
| `PlusLighter` | 带 Alpha 合成的加亮 |
| `AlphaBlend` | 带子级调制的 Alpha 混合 |
| `MiSaturation` | 饱和度调整 |
| `MiBrightness` | 亮度调整 |
| `MiLuminance` | 明度曲线调整 |

### 示例：多层混合

```kotlin
val colors = BlurColors(
    blendColors = listOf(
        BlendColorEntry(Color(0x40FFFFFF), BlurBlendMode.Screen),
        BlendColorEntry(Color(0x20000000), BlurBlendMode.Overlay)
    ),
    saturation = 1.5f
)
```

## 渐进模糊

`Modifier.progressiveTextureBlur` 应用渐变背景模糊：模糊强度沿指定方向从满强度连续过渡到零，过渡带中段是真实的中等模糊，清晰端保持全分辨率像素级锐利。适合导航栏、边缘淡出等场景。

```kotlin
import io.github.suqi8.coui.kmp.blur.ProgressiveBlur
import io.github.suqi8.coui.kmp.blur.progressiveTextureBlur

Box(
    modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .progressiveTextureBlur(
            backdrop = backdrop,
            shape = RectangleShape,
            blurRadius = 20f,
            gradient = ProgressiveBlur.Top // 顶部最强，底部锐利
        )
) {
    // 内容
}
```

### 渐变方向与区间

`ProgressiveBlur` 用两点线性渐变描述过渡：沿 `angle`（角度，从 +X 轴顺时针）投影，模糊在 `startFraction` 处为满强度，在 `endFraction` 处降为零。常见的边缘淡出直接用预设，也可通过 `copy` 调整区间：

```kotlin
// 预设覆盖四个方向的边缘淡出
ProgressiveBlur.Top      // angle = 90°，顶部最强
ProgressiveBlur.Bottom   // angle = 270°，底部最强
ProgressiveBlur.Left     // angle = 0°，左侧最强
ProgressiveBlur.Right    // angle = 180°，右侧最强

// 自定义区间：前 30% 保持满强度，90% 处完全清晰
val gradient = ProgressiveBlur.Top.copy(startFraction = 0.3f, endFraction = 0.9f)

// 自定义衰减曲线：前半段半径变化明显，后半段留长而柔和的尾部
val frontLoaded = ProgressiveBlur.Top.copy(curve = 0.5f)
```

`colors` 与 `noiseCoefficient` 的用法与 [`textureBlur`](#颜色配置) 一致，但只作用于模糊区域并随模糊一起淡出——清晰端与周围内容无缝衔接，不会出现染色硬边。注意渐进模糊的 `noiseCoefficient` 默认为 `0f`（禁用）：噪点叠在模糊渐变上比叠在均匀模糊上更显眼。

::: warning 性能
多级模糊层已在降采样分辨率上求值，但像素级锐利的清晰端仍需每帧额外一个全分辨率覆盖 pass——GPU 带宽开销高于同面积的 `textureBlur`。渐进模糊适合用在栏、边缘过渡带上，不建议覆盖大面积区域。
:::

::: tip
在自定义 `drawBackdrop` 管线中，`progressiveTextureBlurEffect(...)` 在 `effects` 块内运行同样的预设链；需搭配 `drawBackdrop` 的 `progressiveGradient` 参数（传入同一 gradient）以保证清晰端真正锐利——`Modifier.progressiveTextureBlur` 已将两者接好。
:::

## 进阶用法

### 独立 X/Y 模糊半径

可对水平和垂直方向应用不同的模糊强度：

```kotlin
Box(
    modifier = Modifier
        .textureBlur(
            backdrop = backdrop,
            shape = RoundedCornerShape(16.dp),
            blurRadiusX = 100f,
            blurRadiusY = 20f
        )
) {
    // 具有方向性模糊的内容
}
```

### 前景模糊（内容遮罩）

使用 `contentBlendMode` 创建前景模糊效果，内容的 Alpha 通道将作为模糊区域的遮罩：

```kotlin
import androidx.compose.ui.graphics.BlendMode as ComposeBlendMode

Text(
    text = "磨砂文字",
    style = COUITheme.textStyles.displayS,
    modifier = Modifier
        .textureBlur(
            backdrop = backdrop,
            shape = RectangleShape,
            blurRadius = 150f,
            contentBlendMode = ComposeBlendMode.DstIn // 内容 Alpha 遮罩模糊
        )
)
```

### 噪声抖动

`noiseCoefficient` 参数控制应用于模糊结果的抗条纹噪声。默认值为 `0.0045f`，设为 `0f` 可禁用：

```kotlin
Box(
    modifier = Modifier
        .textureBlur(
            backdrop = backdrop,
            shape = RoundedCornerShape(16.dp),
            noiseCoefficient = 0f // 禁用噪声抖动
        )
)
```

### 低级 API：drawBackdrop

如需完全控制效果管线，可使用 `Modifier.drawBackdrop()` 配合 `BackdropEffectScope`：

```kotlin
import io.github.suqi8.coui.kmp.blur.drawBackdrop

Box(
    modifier = Modifier
        .drawBackdrop(
            backdrop = backdrop,
            shape = { RoundedCornerShape(16.dp) },
            effects = {
                // 应用高斯模糊
                blur(radius = 60f)
                // 调整颜色
                colorControls(
                    brightness = 0.05f,
                    contrast = 1.1f,
                    saturation = 1.3f
                )
            }
        )
) {
    // 内容
}
```

#### BackdropEffectScope 扩展函数

| 扩展函数 | 说明 |
| --- | --- |
| `blur(radius, edgeTreatment)` | 应用高斯模糊 |
| `colorFilter(colorFilter)` | 应用 ColorFilter |
| `colorControls(brightness, contrast, saturation)` | 调整亮度、对比度和饱和度 |
| `effect(effect)` | 链接任意 RenderEffect |
| `runtimeShaderEffect(key, shaderString, uniformShaderName, block)` | 应用自定义 AGSL/SkSL 运行时着色器 |

::: warning 像素空间 uniform 需按 `downscaleFactor` 缩放
当 `runtimeShaderEffect` 链接在 `blur`（或其他抬高 `downscaleFactor` 的效果）之后，backdrop layer 以 `1 / downscaleFactor` 的分辨率录制，shader 接收的 `coord` 也是该降采样后 layer 的像素坐标。任何描述像素空间距离的 uniform —— 例如 size、padding/offset、圆角半径、折射带宽等 —— 都必须在 `block` 内部除以 `downscaleFactor`，否则形状几何会落在 layer 范围之外，所有采样都会拿到透明黑色。
:::

#### BackdropEffectScope 属性

| 属性 | 类型 | 说明 |
| --- | --- | --- |
| `size` | Size | 当前渲染尺寸 |
| `layoutDirection` | LayoutDirection | 当前布局方向 |
| `shape` | Shape | 当前裁剪形状 |
| `padding` | Float | 模糊溢出的额外内边距 |
| `renderEffect` | RenderEffect? | 累积的效果链 |
| `downscaleFactor` | Int | 降采样系数（1、2、4、8、16） |
| `noiseCoefficient` | Float | 噪声抖动系数 |

## 属性

### textureBlur / textureEffect 参数

| 参数名 | 类型 | 说明 | 默认值 | 是否必须 |
| --- | --- | --- | --- | --- |
| backdrop | Backdrop | 提供模糊背景内容的 Backdrop | - | 是 |
| shape | Shape | 模糊区域裁剪的形状 | - | 是 |
| blurRadius | Float | 模糊半径（dp），库内部自动转换为像素。限制在 [0, 150] 范围内 | 20f | 否 |
| blurRadiusX | Float | 水平模糊半径（dp，独立半径重载） | - | 是* |
| blurRadiusY | Float | 垂直模糊半径（dp，独立半径重载） | - | 是* |
| noiseCoefficient | Float | 抗条纹噪声抖动系数，0 表示禁用 | 0.0045f | 否 |
| colors | BlurColors | 模糊后应用的颜色调整和混合图层 | BlurColors() | 否 |
| highlight | Highlight? | 可选的边缘高光，绘制在内容之上。`null` 时跳过 | null | 否 |
| contentBlendMode | BlendMode? | 内容在模糊上方合成的混合模式 | null | 否 |
| enabled | Boolean | 是否启用模糊，为 false 时跳过效果并正常绘制内容 | true | 否 |

\* 仅在独立半径重载中必须。

### progressiveTextureBlur 参数

| 参数名 | 类型 | 说明 | 默认值 | 是否必须 |
| --- | --- | --- | --- | --- |
| backdrop | Backdrop | 提供模糊背景内容的 Backdrop | - | 是 |
| shape | Shape | 模糊区域裁剪的形状 | - | 是 |
| blurRadius | Float | 满强度处的模糊半径（dp）。限制在 [0, 150] 范围内 | 20f | 否 |
| blurRadiusX | Float | 满强度处的水平模糊半径（dp，独立半径重载） | - | 是* |
| blurRadiusY | Float | 满强度处的垂直模糊半径（dp，独立半径重载） | - | 是* |
| gradient | ProgressiveBlur | 控制模糊满强度与清晰位置的方向和区间 | ProgressiveBlur.Top | 否 |
| noiseCoefficient | Float | 抗条纹噪声抖动系数，0 表示禁用 | 0f | 否 |
| colors | BlurColors | 颜色调整和混合图层，随模糊一起淡出 | BlurColors() | 否 |
| highlight | Highlight? | 可选的边缘高光，绘制在内容之上。`null` 时跳过 | null | 否 |
| contentBlendMode | BlendMode | 内容在模糊上方合成的混合模式 | SrcOver | 否 |
| enabled | Boolean | 是否启用模糊，为 false 时跳过效果并正常绘制内容 | true | 否 |

\* 仅在独立半径重载中必须。

### ProgressiveBlur 属性

| 属性名 | 类型 | 说明 | 默认值 |
| --- | --- | --- | --- |
| angle | Float | 渐变方向（角度，从 +X 轴顺时针）：0 从左向右淡出，90 从上向下，180 从右向左，270 从下向上 | 90f |
| startFraction | Float | 沿 angle 投影范围内模糊为满强度的位置，范围 [0, 1] | 0f |
| endFraction | Float | 模糊降为零的位置，范围 [0, 1]；可小于 startFraction 以反转过渡，但不能与其相等 | 1f |
| curve | Float | 幂次衰减曲线，端点固定不变：< 1 时半径变化集中在强端（前段变化明显、尾部柔长），> 1 时集中在清晰端。必须为正数 | 1f |

预设：`ProgressiveBlur.Top` / `Bottom` / `Left` / `Right` 覆盖四个方向的边缘淡出。

### BlurColors 属性

| 属性名 | 类型 | 说明 | 默认值 |
| --- | --- | --- | --- |
| blendColors | List\<BlendColorEntry\> | 在模糊背景上按顺序混合的颜色列表 | emptyList() |
| brightness | Float | 亮度调整，范围 [-1, 1] | 0f |
| contrast | Float | 对比度倍数 | 1f |
| saturation | Float | 饱和度倍数 | 1f |

### BlurDefaults

| 常量 | 类型 | 说明 | 值 |
| --- | --- | --- | --- |
| BlurRadius | Float | 默认模糊半径（dp） | 20f |
| NoiseCoefficient | Float | 默认噪声抖动系数 | 0.0045f |
| MaxBlurRadius | Float | 最大允许模糊半径（dp） | 150f |

| 方法 | 返回类型 | 说明 |
| --- | --- | --- |
| blurColors() | BlurColors | 创建被 remember 的 BlurColors 实例 |

## COUI 17 材质模糊

`Modifier.couiMaterialBlur` 复刻 COUI 17 对话框与底部抽屉使用的窗口背景模糊
（`COUIBackgroundBlurBuilder` + `OplusBlurParam.setMaterialParams`）：先模糊背景，再按 alpha 混入 mix 颜色，
最后以 overlay（深色）或 color dodge（浅色）混合 blend 颜色，算法取自 ColorOS 17 渲染器中的材质混合 shader。
`MaterialBlurDefaults` 提供预设；把结果传给 coui-ui 对话框或底部抽屉的 `backgroundModifier` 插槽即可。

原生窗口模糊用的是 Kawase 滤波（沿用 AOSP `KawaseBlurFilter` 的分趟方式：`r / 2` 拆成最多 4 趟 5 点采样），
而不是高斯。`couiMaterialBlurRadius(r)` 把 COUI 半径换算成扩散程度相同的高斯半径（约 `0.433 * r / 0.45`，
例如 200px 约为 192），`couiMaterialBlur` 内部已自动换算。与原生的 `KawaseBlur_Dither` 一样，模糊结果上会叠加
±0.005 的抖动（`noiseCoefficient`，设为 0 关闭）。

```kotlin
OverlayDialog(
    show = showDialog,
    title = "模糊对话框",
    backgroundColor = Color.Transparent,
    backgroundModifier = Modifier.couiMaterialBlur(backdrop, MaterialBlurDefaults.dialog(isDark)),
    materialStroke = true,
    onDismissRequest = { showDialog = false },
) { /* ... */ }
```

| 预设 | 半径（px） | mix 颜色（浅 / 深） | blend 颜色（浅 / 深） | 混合模式（浅 / 深） |
| --- | --- | --- | --- | --- |
| `MaterialBlurDefaults.dialog(isDark)` | 200 | `#DC909090` / `#8C777777` | `#509F9F9F` / `#B2292929` | ColorDodge / Overlay |
| `MaterialBlurDefaults.bottomSheet(isDark)` | 400 | `#DCC8C8C8` / `#A4515151` | `#376E6E6E` / `#B30F0F0F` | ColorDodge / Overlay |
| `MaterialBlurDefaults.popup(isDark)` | 160 | `#99CDCDCD` / `#99262626` | `#BFEEEEEE` / `#99262626` | GlowOverlay（深浅相同） |
| `MaterialBlurDefaults.popup(isDark, hdr = true)` | 160 | `#DC7A7A7A` / `#50262626` | `#C8868686` / `#990F0F0F` | ColorDodge / Overlay |

| 参数 | 类型 | 说明 | 默认值 |
| --- | --- | --- | --- |
| backdrop | Backdrop | 提供表面背后内容的背景源 | - |
| style | MaterialBlurStyle | 半径（原始 px）、mix 颜色、blend 颜色与混合模式 | - |
| shape | Shape | 模糊的裁剪形状；在已裁剪的插槽内保持 `RectangleShape` | `RectangleShape` |
| fraction | Float | 同比缩放半径（取整到像素）与两种颜色的 alpha（COUI 抽屉随遮罩淡入淡出模糊） | 1f |
| enabled | Boolean | 是否绘制模糊 | true |
| noiseCoefficient | Float | 叠加在模糊结果上的抖动，0 为关闭 | `MaterialBlurDefaults.NoiseCoefficient`（0.005f） |

## COUI 17 视图模糊

`Modifier.couiViewBlur` 移植自 `COUIMaterialBlurEffect.setViewBlurEffect`：COUI 17 的按钮和顶栏按钮不画实色填充，而是对背后内容做这层背景滤镜。处理顺序是先模糊背景，再依次混合两层颜色。混合遵循 Android `BlendModeColorFilter` 的语义：常量色是 source，模糊后的背景是 destination。

半径方面，COUI 传入的是原始像素，HWUI 再把它换算成 `sigma = 0.57735 * r + 0.5`。`couiViewBlurRadius(r)` 把它转成 `blur` 所用的半径，即 `(0.57735 * r + 0.5) / 0.45`，两边的高斯核因此一致。节点自身的内容画在模糊之上，模糊按 `shape` 裁剪。

```kotlin
Box(Modifier.couiViewBlur(backdrop, ViewBlurDefaults.contentSecondaryButton(isDark), CircleShape)) {
    /* ... */
}
```

注意：背景源不能录制到绘制它的节点本身。被模糊的元素要放在录制层之上，作为它的兄弟节点，而不是放在录制层内部。

| 预设 | 半径（px） | 第 1 层 | 第 2 层 |
| --- | --- | --- | --- |
| `contentPrimaryButton(themeColor, isDark, enabled)` | 10 | 主题色 alpha 229（浅）/ 230（深）SrcOver；禁用时 alpha 77 | `#00FFFFFF`（浅）/ `#19FFFFFF`（深）Overlay；禁用时 `#19FFFFFF` Overlay |
| `contentSecondaryButton(isDark)` | 25 | `#CCFFFFFF` / `#33FFFFFF` SrcOver | `#00FFFFFF` SrcOver（无效果） |
| `contentTransparentButton()` | 25 | `#3F333333` Overlay | `#3FFFFFFF` Luminosity |
| `topBar(TopBarMaterialTier.Default, isDark)` | 0 | 浅 `#66333333` ColorDodge；深 `#CC262626` SrcOver | 浅 `#99F5F5F5` Luminosity；深 `#B37F7F7F` Overlay |
| `topBar(TopBarMaterialTier.Immersive, isDark)` | 25 | `#1A6E6E6E` ColorDodge | `#24F1F1F1` Luminosity（深浅相同） |
| `topBar(TopBarMaterialTier.DefaultBlur, isDark)` | 20 | 同 `Default` | 同 `Default` |
| `frameworkFab(themeColor, isDark, enabled)` | 10 | 同 `contentPrimaryButton` | `#1AFFFFFF`（浅）/ `#19FFFFFF`（深）Overlay；禁用时 `#19FFFFFF` Overlay |
| `frameworkBottomBar(isDark)` | 25 | 浅 `#B3666666` ColorDodge；深 `#80666666` Overlay | 浅 `#99F5F5F5`；深 `#B3262626` Luminosity |
| `searchBarNoMaterial(isDark)` | 0 | 浅 `#D6333333` ColorDodge；深 `#CC262626` SrcOver | 浅 `#66F5F5F5` Luminosity；深 `#B37F7F7F` Overlay |
| `contentSegmentButton(isDark)` | 25 | 浅 `#7F333333` ColorDodge；深 `#7F7F7F7F` Overlay | 浅 `#99EFEFEF`；深 `#99262626` Luminosity |
| `contentChipTab(isDark, selected, blur)` | 40（`blur = false` 时为 0） | 浅 ColorDodge；深 Luminosity | 浅 Luminosity；深 Overlay |
| `NoEffect` | 0 | 透明 | 透明（不绘制） |

`ViewBlurDefaults.interpolate(start, end, progress, blendModes = end)` 对应 `setViewBlurEffect` 的动画形式（搜索栏材质渐入、chip tab 选中切换）：半径线性插值，颜色按 `ArgbEvaluator` 插值（alpha 线性，RGB 在 gamma 2.2 线性空间），混合模式取自 `blendModes`。

| 参数 | 类型 | 说明 | 默认值 |
| --- | --- | --- | --- |
| backdrop | Backdrop | 提供节点背后内容的背景源 | - |
| style | ViewBlurStyle | 半径（原始 px）与两层颜色及其 Compose `BlendMode`。无效果的样式（半径 0、两层均为 `#00000000`）不绘制，与 COUI 清除效果一致 | - |
| shape | Shape | 模糊的裁剪形状，即组件轮廓 | - |
| enabled | Boolean | 是否绘制模糊 | true |

要让 coui-ui 按钮使用这层模糊，需要通过 coui-ui 的 `LocalMaterialBackdrop` 提供一个 `MaterialBackdropProvider`，让它返回 `Modifier.couiViewBlur(backdrop, ViewBlurStyle(spec.blurRadiusPx, spec.layer1, spec.mode1, spec.layer2, spec.mode2), shape)`。详见 [Button](../components/button#背景模糊)。

## 边缘高光

`Highlight` 沿圆角形状绘制一条带两路定向光的玻璃质感边缘。它通过 `Modifier.textureBlur` / `Modifier.textureEffect` 的 `highlight` 参数（恒定场景）或 `Modifier.drawBackdrop` 的 `highlight` 参数（响应式场景，与 `BackdropEffectScope` 共享）绘制。配合模糊背景可营造"模糊背景上的发光边缘"效果。

### 配合 textureBlur（恒定）

```kotlin
import androidx.compose.foundation.shape.RoundedCornerShape
import io.github.suqi8.coui.kmp.blur.highlight.Highlight

Box(
    modifier = Modifier
        .size(200.dp, 100.dp)
        .textureBlur(
            backdrop = backdrop,
            shape = RoundedCornerShape(24.dp),
            highlight = Highlight.GlassStrokeMiddleLight,
        ),
)
```

### 配合 drawBackdrop（响应式）

`highlight` lambda 与 `effects` 共用同一个 `BackdropEffectScope`，其返回值可随状态变化（如按压进度），并自动复用当前的 `size` / `shape`。

```kotlin
import io.github.suqi8.coui.kmp.blur.drawBackdrop
import io.github.suqi8.coui.kmp.blur.blur
import io.github.suqi8.coui.kmp.blur.highlight.Highlight

Box(
    modifier = Modifier
        .size(200.dp, 100.dp)
        .drawBackdrop(
            backdrop = backdrop,
            shape = { RoundedCornerShape(24.dp) },
            effects = { blur(20.dp.toPx()) },
            highlight = { Highlight.GlassStrokeMiddleLight.copy(alpha = pressProgress) },
        ),
)
```

传 `null`（或 `width = 0.dp` 的 `Highlight`）即可禁用。

::: warning 注意
边缘高光需要 `isRuntimeShaderSupported()` 支持。在不支持的平台或 API 级别上，将静默跳过绘制。
:::

### 内置预设

提供六个预设，按卡片尺寸和主题选用：

| 预设 | innerBlurRadius | 视觉特征 |
| --- | --- | --- |
| `Highlight.GlassStrokeBigLight` | 3.5 dp | halo 最厚最柔 — 亮模式大卡片 |
| `Highlight.GlassStrokeMiddleLight` | 2.8 dp | 标准亮模式卡片（默认） |
| `Highlight.GlassStrokeSmallLight` | 2.6 dp | 紧凑、更锐利的亮模式卡片 |
| `Highlight.GlassStrokeBigDark` | 1.7 dp | halo 最薄 — 暗模式大卡片 |
| `Highlight.GlassStrokeMiddleDark` | 2.0 dp | 标准暗模式卡片 |
| `Highlight.GlassStrokeSmallDark` | 2.3 dp | 紧凑、最锐利的暗模式卡片 |

`Highlight.Default` 等价于 `GlassStrokeMiddleLight`。

### 自定义 BloomStroke

可基于 token 覆盖单个字段，或从零构建 `BloomStroke`：

```kotlin
import io.github.suqi8.coui.kmp.blur.highlight.BloomStroke
import io.github.suqi8.coui.kmp.blur.highlight.LightPosition
import io.github.suqi8.coui.kmp.blur.highlight.LightSource

val custom = Highlight(
    width = 1.dp,
    alpha = 0.8f,
    style = BloomStroke(
        color = Color.White.copy(alpha = 0.05f),
        innerBlurRadius = 3.dp,
        primaryLight = LightSource(
            position = LightPosition(0.5f, 0.4f, -0.5f),
            intensity = 0.4f,
        ),
        secondaryLight = LightSource(
            position = LightPosition(0.5f, 0.85f, -0.5f),
            intensity = 0.3f,
        ),
    ),
)
```

`LightPosition` 使用归一化 UV 坐标，参考原点为 `(0.5, 0.7, 0)`。Shader 将 `(x − 0.5, y − 0.7, z)` 归一化后作为 3D 方向向量。负 `z` 把光放在表面平面之后（从内向外照亮边缘）— 所有内置预设均使用 `z < 0`。

两路光在 shader 中被限制到相反的半球：`primaryLight` 照亮圆角矩形上半部，`secondaryLight` 照亮下半部。`y < 0.7` 让光偏上；`y > 0.7` 让光偏下。

### 传感器驱动的视差

`rememberTiltLight` 通过设备旋转传感器实时偏移基准位置。Android 上会产生跟随设备倾斜的视差边缘；Desktop / iOS / macOS / Web 上 tilt 始终为零，光斑保持静止。

```kotlin
import io.github.suqi8.coui.kmp.blur.highlight.rememberTiltLight

val baseStyle = Highlight.GlassStrokeMiddleLight.style as BloomStroke

val tiltPrimary = rememberTiltLight(
    basePosition = baseStyle.primaryLight.position,
    intensity = baseStyle.primaryLight.intensity,
    sensitivity = 0.15f,
)
val tiltSecondary = rememberTiltLight(
    basePosition = baseStyle.secondaryLight.position,
    intensity = baseStyle.secondaryLight.intensity,
    sensitivity = 0.12f,
)

val highlight = Highlight.GlassStrokeMiddleLight.copy(
    style = baseStyle.copy(
        primaryLight = tiltPrimary,
        secondaryLight = tiltSecondary,
    ),
)
```

`sensitivity` 控制每弧度倾斜对应的 UV 偏移量 — `0.1f` 表示在 1 rad 倾斜下光位置移动 10%。提高数值会放大视差幅度。

### 边缘高光属性

#### Highlight

| 属性 | 类型 | 说明 | 默认值 |
| --- | --- | --- | --- |
| width | Dp | 用 `style.color` 绘制的描边带宽度 | 0.8.dp |
| alpha | Float | 整体不透明度倍数，范围 [0, 1] | 1f |
| style | HighlightStyle | 着色模型（通常是 `BloomStroke`） | `HighlightStyle.Default` |

#### BloomStroke

| 属性 | 类型 | 说明 | 默认值 |
| --- | --- | --- | --- |
| color | Color | 描边带的基础颜色；alpha 驱动描边亮度 | `White.copy(alpha = 0.05f)` |
| blendMode | BlendMode | 高光叠加层的合成模式 | `BlendMode.Plus` |
| innerBlurRadius | Dp | 光晕从圆角向内延伸的深度 — 决定 halo 的厚度与柔度 | 2.8.dp |
| primaryLight | LightSource | 上半球定向光 | `LightPosition(0.5f, 0.5f, -0.5f)`，intensity 0.4 |
| secondaryLight | LightSource | 下半球定向光 | `LightPosition(0.5f, 0.8f, -0.5f)`，intensity 0.25 |

#### LightSource

| 属性 | 类型 | 说明 | 默认值 |
| --- | --- | --- | --- |
| position | LightPosition | 光的 UV 位置 | - |
| color | Color | 光颜色（颜色 alpha 折入贡献缩放） | `White` |
| intensity | Float | 亮度缩放，≥ 0 | 1f |

#### LightPosition

| 属性 | 类型 | 说明 |
| --- | --- | --- |
| x | Float | 归一化 UV x（参考原点：0.5） |
| y | Float | 归一化 UV y（参考原点：0.7） |
| z | Float | 有符号深度；负值把光放在表面之后 |

#### rememberTiltLight 参数

| 参数 | 类型 | 说明 | 默认值 |
| --- | --- | --- | --- |
| basePosition | LightPosition | 零倾斜时的位置 | - |
| color | Color | 光颜色 | `White` |
| intensity | Float | 亮度缩放 | 1f |
| sensitivity | Float | 每弧度倾斜对应的 UV 偏移量 | 0.1f |
