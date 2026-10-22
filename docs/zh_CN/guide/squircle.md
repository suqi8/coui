# 平滑圆角

`coui-squircle` 是一个独立的 Compose Multiplatform 平滑圆角（squircle，连续
曲率圆角）矩形库。它通过 `Modifier` 扩展提供填充 / 裁剪 / 描边，并暴露底层的
`Path` 构造器。支持 Android、Desktop (JVM)、iOS、macOS 和 Web (WasmJs/Js) 平台。

::: tip 与 RoundedCornerShape 的区别
`RoundedCornerShape` 的圆角是一段纯粹的四分之一圆弧。本库的 squircle 圆角即
ColorOS 的平滑圆角（Oplus `Path.addSmoothRoundRect`）：一段 Lamé 超椭圆
`|x|^n + |y|^n = r^n` 的四分之一，指数 `n` 即圆角 `weight`，在距顶点 `r`
（圆角半径）处与两条直边相切。权重 `2` 即圆弧；权重越大，曲线在直边上停留越久，
角越方。视觉差异在中到大半径时最明显。
:::

::: warning 平台下限
基于 shader 的 modifier（`squircleBackground`、`squircleSurface`、
`squircleClip`、`squircleBorder`）需要 Android API 33+。更低 API 上会自动
回退到等价的 `RoundedCornerShape` 实现 —— 不会崩溃，无需手动门控。运行时
开关见下方[全局开关](#全局开关)。
:::

## 配置

在项目中添加 `coui-squircle` 依赖：

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.suqi8.coui.kmp:coui-squircle:<version>")
        }
    }
}
```

Android 项目：

```kotlin
dependencies {
    implementation("io.github.suqi8.coui.kmp:coui-squircle-android:<version>")
}
```

## 平台支持

| 平台          | 基于 shader 的 modifier             | 基于 path 的 API |
| ------------- | ----------------------------------- | ---------------- |
| Android       | API 33+（更低版本自动回退圆弧圆角） | 所有 API 级别    |
| Desktop (JVM) | 支持                                | 支持             |
| iOS / macOS   | 支持                                | 支持             |
| WasmJs / Js   | 支持                                | 支持             |

回退是自动的，调用方无需分支。`Path.addSquircleRect` 在所有平台都能跑，但
提供了显式的 `squircleEnabled` 参数 —— 跟 modifier 混用时透传它即可保持
视觉一致。

## 基本用法

```kotlin
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.squircle.squircleBackground

Box(
    modifier = Modifier
        .size(120.dp, 80.dp)
        .squircleBackground(
            color = COUITheme.colorScheme.primaryVariant,
            cornerRadius = 20.dp,
        ),
)
```

## 变体

### squircleBackground — 仅填充

在 squircle 轮廓内填充纯色。子内容**不会**被裁剪到圆角 —— 适合只想画一个
背景、但仍允许子内容溢出或自行处理边角的场景。

```kotlin
Modifier.squircleBackground(
    color = COUITheme.colorScheme.primaryVariant,
    cornerRadius = 16.dp,
)
```

### squircleSurface — 填充 + 裁剪

可直接替代 `Modifier.clip(RoundedCornerShape(r)).background(color)`，但用的
是 squircle 轮廓。子内容在同一个离屏 layer 中被裁剪到 squircle 轮廓 ——
没有重复绘制成本。

```kotlin
Modifier.squircleSurface(
    color = COUITheme.colorScheme.primaryVariant,
    cornerRadius = 16.dp,
)
```

### squircleClip — 仅裁剪

把子内容裁剪到 squircle 轮廓，但不绘制填充。适合填充由其它途径提供（如
`Image`、其它 modifier 链等）的场景。

```kotlin
Modifier.squircleClip(cornerRadius = 16.dp)
```

::: warning 离屏成本
`squircleSurface` 和 `squircleClip` 各自会开一个离屏 GPU layer。size 和参数
稳定时 shader 输出会被缓存；能传常量就传常量，避免让 `cornerRadius` 受持续
动画状态驱动。
:::

### squircleBorder — 仅描边

围绕布局绘制 squircle 轮廓的描边，自动内缩半个描边宽度，使其与同半径
的 `squircleBackground` / `squircleSurface` 视觉对齐。基于 path 实现（不需要
shader），比 shader 变体更便宜。

```kotlin
import io.github.suqi8.coui.kmp.squircle.squircleBorder

Modifier.squircleBorder(
    width = 1.dp,
    color = COUITheme.colorScheme.outline,
    cornerRadius = 16.dp,
)
```

## 每角独立半径

所有基于 shader 的 modifier 都提供按角独立半径的重载，参数顺序与
`RoundedCornerShape` 一致（`topStart`、`topEnd`、`bottomEnd`、`bottomStart`）：

```kotlin
Modifier.squircleSurface(
    color = COUITheme.colorScheme.secondaryVariant,
    topStart = 24.dp,
    topEnd = 24.dp,
    bottomEnd = 0.dp,
    bottomStart = 0.dp,
)
```

混搭即可得到非对称形状（"贴在顶部的卡片"、"一侧切直的内嵌徽章"等）。

## 绝对圆角（与布局方向无关）

`absoluteSquircleBackground`、`absoluteSquircleClip`、`absoluteSquircleSurface`
与每角独立半径的重载相同，但接受物理方位的角参数（`topLeft`、`topRight`、
`bottomRight`、`bottomLeft`，从左上顺时针），且**不会**随 `LocalLayoutDirection`
翻转——与 `AbsoluteRoundedCornerShape` 之于 `RoundedCornerShape` 的关系一致。
当圆角需要固定在物理方位、不随 RTL/LTR 改变时使用（例如绑定到滑动边缘的过渡揭示）。

```kotlin
Modifier.absoluteSquircleSurface(
    color = COUITheme.colorScheme.surface,
    topLeft = 24.dp,
    topRight = 24.dp,
    bottomRight = 0.dp,
    bottomLeft = 0.dp,
)
```

## 调整曲线参数

`weight` 是每个角的超椭圆指数，与 ColorOS 传给 `addSmoothRoundRect` 的值相同。
圆角总在 `cornerRadius` 处与直边相切，权重只改变两切点之间曲线的方正程度。

| 参数     | 默认值 | 范围     | 作用                                                   |
| -------- | :----: | -------- | ------------------------------------------------------ |
| `weight` | `2.5`  | `[1, 8]` | 超椭圆指数。`2` 为圆弧，`3` 更方；低于 `2` 为软胶囊。 |

ColorOS 17 用到以下值，`coui-ui` 在 `COUIRoundCorner` 上提供：

| 权重  | COUI 来源              | 使用者                                                     |
| :---: | ---------------------- | ---------------------------------------------------------- |
| `1.0` | `FULL_RRECT_G2_WEIGHT` | 全圆胶囊：胶囊按钮、搜索框、单行顶部提示                   |
| 未指定 | 不传权重              | Chip、悬浮工具栏 / 导航栏、开关与滑条轨道、分段控件、Snackbar：全圆时 `1.0`，否则 `2.45`（`default_g2_weight`） |
| `2.0` | 圆弧                   | 基础 `coui_round_corner_*` 半径、圆形                      |
| `2.5` | `weight_16_1_normal`   | 卡片列表、输入卡片、弹出菜单（`*_radius_normal_16_1`，如 24dp） |
| `3.0` | `weight_16_1_large`    | 按钮、卡片视图、气泡提示、警告对话框、底部面板（45dp）     |

```kotlin
Modifier.squircleSurface(
    color = COUITheme.colorScheme.surface,
    cornerRadius = 45.dp,
    weight = 3f,
)
```

引用默认值用 `SquircleDefaults.Weight`。胶囊（`cornerRadius` 等于高度一半）上
更大的权重会把圆头变方。

### 软胶囊（权重低于 2）

低于 `2` 的权重复现 Oplus G2 shader 的 `superN < 2` 分支：轮廓从圆弧胶囊
（`weight = 2`）向 Oplus 软胶囊过渡，`weight <= 1.005` 时即为软胶囊
（`SquircleDefaults.CapsuleWeight` = 1.0，ColorOS 给所有全圆 `addSmoothRoundRect`
的权重）。软胶囊的半径取短边一半，直边与端帽之间用平滑最大值衔接而非相切圆弧，
衔接处最多比圆弧胶囊内收约 0.02 r。与 `OplusPathSmooth` 一致，它只作用于**全圆**
矩形（四角半径相等且不小于短边一半）；其他矩形会把低于 2 的权重按 2 绘制。
请传一个总能覆盖半高的半径（ColorOS 用 `couiRoundCornerFULL`，72dp）：

```kotlin
Modifier.squircleSurface(
    color = COUITheme.colorScheme.primary,
    cornerRadius = 72.dp,
    weight = SquircleDefaults.CapsuleWeight,
)
```

shader 直接计算原生距离函数；`addSquircleRect` 追踪其零等值线（误差 0.001 px 以内）。

### 未指定权重

ColorOS 很多调用点根本不传权重（`addSmoothRoundRect(rect, rx, ry, dir)`、
`setSmoothRoundRect(rect, radius)`）。此时原生侧对全圆矩形用 `1.0`，其余用
`persist.sys.oplus.default_g2_weight`（未设置时为 2.45）。`SquircleDefaults.UnspecifiedWeight`
复现这一规则：把它作为 `weight` 传入，各 modifier、`addSquircleRect` 与 `SquircleShape`
都会按矩形逐个解析，胶囊仍是软胶囊，而自定义的较小半径按 `SquircleDefaults.DefaultG2Weight` 绘制。

```kotlin
Modifier.squircleBackground(
    color = COUITheme.colorScheme.surfaceContainer,
    cornerRadius = cornerRadius, // 72.dp：软胶囊；16.dp：权重 2.45
    weight = SquircleDefaults.UnspecifiedWeight,
)
```

::: info 从 `extension` 迁移
旧版本使用 `extension`（角部区域倍数，默认 1.2819），三次曲线角会延伸到
`cornerRadius` 之外。该参数现已改为 `weight`；`SquircleDefaults.Extension*`
仅作为已弃用常量保留。
:::

## Path API

当你需要在 modifier 流水线之外构造 squircle 轮廓（例如每帧重建的 `clipPath`
揭示动画、自定义 `Canvas` 绘制、非 shader Shape 的 `Outline` 等），可直接
构造 path：

```kotlin
import androidx.compose.ui.graphics.Path
import io.github.suqi8.coui.kmp.squircle.addSquircleRect

val path = Path().apply {
    addSquircleRect(
        width = sizePx.width,
        height = sizePx.height,
        cornerRadius = 20.dp.toPx(),
    )
}
```

每个角由若干段拟合超椭圆的三次贝塞尔曲线构成（误差在半径的 0.03% 以内）。
静态填充 / 裁剪场景请优先用 shader-backed modifier —— 它们逐像素计算精确的
超椭圆距离。

另有两个接受 `Rect`（而非尺寸）的重载，用于大画布内的形状：
`addSquircleRect(rect, cornerRadius, weight)`，以及按物理方位逐角指定的
`addSquircleRect(rect, topLeft, topRight, bottomRight, bottomLeft, weight)`。与 Oplus G2
shader 一致，每个角的半径各自钳位到短边一半，所有角共用同一权重；半径为 0 的角保持直角。

```kotlin
// 弹出框形变：上方两角收缩，下方两角保持圆角。
path.addSquircleRect(rect, topLeft = rTop, topRight = rTop, bottomRight = rBottom, bottomLeft = rBottom, weight = 2.5f)
```

`Canvas` 代码可用 `DrawScope.drawSquircleRect(color 或 brush, cornerRadius, topLeft, size, weight)`
一次填充（每次调用都会构造 path）；`SquircleShape(cornerRadius, weight)` 把
`addSquircleRect` 包装成 `Shape`，供接受 Shape 的组件（Surface、阴影）使用：

```kotlin
Surface(shape = SquircleShape(72.dp, SquircleDefaults.UnspecifiedWeight)) { /* ... */ }
```

`SquircleShape` 基于 path，所有平台可用；需要与 modifier 的 `RoundedCornerShape`
回退保持一致时，按 `isSquircleEnabled()` 分支。

## COUI 旧版 Path

ColorOS 17 仍有少数组件用 COUI 的 Java 回退 path `COUIShapePath.getRoundRectPath`
而非超椭圆：提示红点（`Badge`）、`COUICodeInputView`（`CodeTextField`）和
`COUIRecommendedDrawable`（`RecommendedPreference`）。`addLegacySquircleRect`
原样移植它 —— 每角三段三次曲线，控制点为半径的固定百分比，在半径的 128.19% 处
离开直边（半径达到短半边的 90% 时收缩到 110.4%）。`LegacySquircleShape` 把它包装成
`Shape`，可用于 `background` / `clip` / `border`：

```kotlin
import io.github.suqi8.coui.kmp.squircle.LegacySquircleShape

Modifier.background(COUITheme.colorScheme.surface, LegacySquircleShape(8.dp))
```

它基于 path（无需 shader），不受 `LocalSquircleEnabled` 影响。

## 全局开关

`LocalSquircleEnabled`（默认 `true`）门控本模块全部 shader-backed squircle
modifier。置为 `false` 时所有 modifier 在运行时切换到 `RoundedCornerShape`
回退，适合用户偏好或 A/B 对比：

```kotlin
import io.github.suqi8.coui.kmp.squircle.LocalSquircleEnabled

CompositionLocalProvider(LocalSquircleEnabled provides userPrefersRoundedRects) {
    AppContent()
}
```

`isSquircleEnabled()` 把 `LocalSquircleEnabled.current` 与运行时 shader 能力
检查合并为单次读取。当你在同一组件里混用 shader-backed modifier 与
`addSquircleRect` 时，在 @Composable 处读它一次，捕获布尔后透传给 path 构造器：

```kotlin
@Composable
fun OutlinedSurface(cornerRadius: Dp) {
    val squircleEnabled = isSquircleEnabled()
    val path = remember { Path() }
    Box(
        Modifier
            .squircleBackground(COUITheme.colorScheme.surface, cornerRadius)
            .drawWithContent {
                drawContent()
                path.rewind()
                path.addSquircleRect(
                    width = size.width,
                    height = size.height,
                    cornerRadius = cornerRadius.toPx(),
                    squircleEnabled = squircleEnabled,
                )
                drawPath(path, COUITheme.colorScheme.outline, style = Stroke(1.dp.toPx()))
            },
    )
}
```

## 属性

### squircleBackground / squircleSurface / squircleClip

| 参数           | 类型    | 说明                                              | 默认值                              |
| -------------- | ------- | ------------------------------------------------- | ----------------------------------- |
| `color`        | `Color` | 填充颜色（`squircleClip` 无此参数）               | -                                   |
| `cornerRadius` | `Dp`    | 统一圆角半径                                      | -                                   |
| `topStart`     | `Dp`    | 每角半径（按角重载）                              | -                                   |
| `topEnd`       | `Dp`    | 每角半径（按角重载）                              | -                                   |
| `bottomEnd`    | `Dp`    | 每角半径（按角重载）                              | -                                   |
| `bottomStart`  | `Dp`    | 每角半径（按角重载）                              | -                                   |
| `weight`       | `Float` | 超椭圆指数，钳位到 `[1, 8]`；低于 2 为软胶囊（仅全圆矩形）；`UnspecifiedWeight` 按矩形解析 | `SquircleDefaults.Weight` = 2.5     |

### squircleBorder

| 参数           | 类型    | 说明              | 默认值                          |
| -------------- | ------- | ----------------- | ------------------------------- |
| `width`        | `Dp`    | 描边宽度          | -                               |
| `color`        | `Color` | 描边颜色          | -                               |
| `cornerRadius` | `Dp`    | 统一圆角半径      | -                               |
| `weight`       | `Float` | 超椭圆指数        | `SquircleDefaults.Weight`       |

### Path.addSquircleRect

| 参数              | 类型      | 说明                                                 | 默认值                          |
| ----------------- | --------- | ---------------------------------------------------- | ------------------------------- |
| `width`           | `Float`   | path 宽度（像素）                                    | -                               |
| `height`          | `Float`   | path 高度（像素）                                    | -                               |
| `rect`            | `Rect`    | 边界（像素；`Rect` 重载，代替宽高）                  | -                               |
| `cornerRadius`    | `Float`   | 圆角半径（像素）                                     | -                               |
| `topLeft` ... `bottomLeft` | `Float` | 逐角半径（像素，物理方位；逐角重载）       | -                               |
| `weight`          | `Float`   | 超椭圆指数                                           | `SquircleDefaults.Weight`       |
| `squircleEnabled` | `Boolean` | 为 `false` 时改为追加同尺寸的圆角矩形                 | `true`                          |

### Path.addLegacySquircleRect / LegacySquircleShape

| 参数           | 类型      | 说明                                                  | 默认值 |
| -------------- | --------- | ----------------------------------------------------- | ------ |
| `width`        | `Float`   | path 宽度（像素）                                     | -      |
| `height`       | `Float`   | path 高度（像素）                                     | -      |
| `cornerRadius` | `Float`   | 圆角半径（像素；`LegacySquircleShape` 上为 `Dp`），与 COUI 一样不钳位 | -      |
| `left` / `top` | `Float`   | 矩形偏移                                              | `0f`   |
| `topLeft` ... `bottomLeft` | `Boolean` | 是否圆化对应角                          | `true` |

### SquircleDefaults

| 常量           | 类型    | 说明                       |  值    |
| -------------- | ------- | -------------------------- | -----: |
| `Weight`       | `Float` | 默认权重（`weight_16_1_normal`） | `2.5`  |
| `WeightMin`    | `Float` | 权重下限（软胶囊）         | `1.0`  |
| `CapsuleWeight`| `Float` | 全圆胶囊（`FULL_RRECT_G2_WEIGHT`） | `1.0`  |
| `DefaultG2Weight` | `Float` | 未传权重且非全圆（`default_g2_weight`） | `2.45` |
| `UnspecifiedWeight` | `Float` | 标记：按矩形解析为 `CapsuleWeight` 或 `DefaultG2Weight` | `0.0` |
| `WeightMax`    | `Float` | 权重上限                   | `8.0`  |
