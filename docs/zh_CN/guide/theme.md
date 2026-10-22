# 主题系统

COUI 提供了一套完整的主题系统，使您能够轻松地在整个应用中保持一致的设计风格。整个主题系统由颜色方案和文本样式组成。

## 使用 COUITheme

使用 `ThemeController` 控制配色模式，然后用 `COUITheme` 包裹内容：

```kotlin
@Composable
fun App() {
    // 可用模式：System、Light、Dark、MonetSystem、MonetLight、MonetDark
    val controller = remember { ThemeController(ColorSchemeMode.System) }
    COUITheme(controller = controller) { /* 内容 */ }
}
```

使用 `ColorSchemeMode.System` / `ColorSchemeMode.MonetSystem` 时会自动跟随系统深色模式。

### 具体方式

- **动态取色 (Monet)**

  使用 `ThemeController` 的 `Monet` 模式启用动态取色。
  - 如果提供了 `keyColor`，将从该种子颜色生成配色方案。
  - 如果 `keyColor` 为 `null`（默认），则尝试使用系统的壁纸颜色（Android "Material You"）。

  ```kotlin
  @Composable
  fun AppWithMonet() {
      val controller = remember {
          ThemeController(
              ColorSchemeMode.MonetSystem, // 或 MonetLight、MonetDark
              // keyColor = Color(0xFF3482FF) // 可选：自定义种子颜色
          )
      }
      COUITheme(controller = controller) { /* 内容 */ }
  }
  ```

- **调色板风格与颜色规范**

  通过 `paletteStyle` 和 `colorSpec` 自定义 Monet 动态取色使用的调色板风格和颜色规范：

  ```kotlin
  val controller = remember {
      ThemeController(
          ColorSchemeMode.MonetSystem,
          keyColor = Color(0xFF3482FF),
          paletteStyle = ThemePaletteStyle.Vibrant,
          colorSpec = ThemeColorSpec.Spec2025
      )
  }
  COUITheme(controller = controller) { /* 内容 */ }
  ```

  ::: tip 提示
  `ThemeColorSpec.Spec2025` 仅由 `TonalSpot`、`Neutral`、`Vibrant` 和 `Expressive` 调色板风格支持。其他风格会自动降级为 `Spec2021`。
  :::

- **手动控制深色模式**

  使用 `isDark` 参数显式控制深色状态，覆盖系统设置。

  ```kotlin
  val controller = remember {
      ThemeController(
          ColorSchemeMode.System,
          isDark = true // 强制深色模式
      )
  }
  ```

- **Controller 中的自定义配色**

  您也可以直接将自定义的 `lightColors` 和 `darkColors` 传递给 `ThemeController`。

  ```kotlin
  val controller = remember {
      ThemeController(
          ColorSchemeMode.System,
          lightColors = myLightColors,
          darkColors = myDarkColors
      )
  }
  ```

- **直接使用**

  直接传入颜色方案到 `COUITheme(colors = ...)`，用于在不使用 Controller 的情况下完全自定义：

  ```kotlin
  @Composable
  fun AppWithColors() {
      val colors = lightColorScheme() // 或 darkColorScheme()
      COUITheme(colors = colors) { /* 内容 */ }
  }
  ```

## ThemeController

`ThemeController` 管理当前 COUI 主题的配色方案。

### ThemeController 属性

| 属性名 | 类型 | 说明 | 默认值 |
| --- | --- | --- | --- |
| colorSchemeMode | ColorSchemeMode | 配色模式 | ColorSchemeMode.System |
| lightColors | Colors | 浅色配色方案 | lightColorScheme() |
| darkColors | Colors | 深色配色方案 | darkColorScheme() |
| keyColor | Color? | 动态取色的种子颜色。`null` 时使用系统壁纸颜色 | null |
| colorSpec | ThemeColorSpec | Material 颜色规范版本 | ThemeColorSpec.Spec2021 |
| paletteStyle | ThemePaletteStyle | 动态取色的调色板风格 | ThemePaletteStyle.TonalSpot |
| isDark | Boolean? | 覆盖系统深色模式。`null` 时跟随系统 | null |

### ColorSchemeMode

| 值 | 说明 |
| --- | --- |
| System | 跟随系统浅色/深色设置 |
| Light | 强制浅色模式 |
| Dark | 强制深色模式 |
| MonetSystem | 动态取色，跟随系统浅色/深色 |
| MonetLight | 动态取色，强制浅色模式 |
| MonetDark | 动态取色，强制深色模式 |

### ThemePaletteStyle

| 值 | 支持 Spec2025 | 说明 |
| --- | --- | --- |
| TonalSpot | 是 | 默认 Material You 风格，平衡的色调变化 |
| Neutral | 是 | 柔和、低彩度调色板 |
| Vibrant | 是 | 高彩度、鲜艳调色板 |
| Expressive | 是 | 大胆且富有艺术感的创意配色 |
| Rainbow | 否 | 广谱多色相调色板 |
| FruitSalad | 否 | 活泼的多色相调色板 |
| Monochrome | 否 | 单色灰度调色板 |
| Fidelity | 否 | 紧密匹配种子颜色 |
| Content | 否 | 从内容颜色派生，最大程度保持准确性 |

### ThemeColorSpec

| 值 | 说明 |
| --- | --- |
| Spec2021 | 原始 Material Design 3 颜色规范 |
| Spec2025 | 2025 更新版颜色规范（仅 TonalSpot、Neutral、Vibrant、Expressive 支持） |

## COUITheme 对象

通过 `COUITheme` 对象访问当前主题值：

```kotlin
val colors = COUITheme.colorScheme
val textStyles = COUITheme.textStyles
val mode = COUITheme.colorSchemeMode
val isDynamic = COUITheme.isDynamicColor
```

| 属性 | 类型 | 说明 |
| --- | --- | --- |
| colorScheme | Colors | 当前颜色方案 |
| textStyles | TextStyles | 当前文本样式 |
| colorSchemeMode | ColorSchemeMode? | 当前配色模式（使用直接 `colors` 重载时为 null） |
| isDynamicColor | Boolean | 当前模式是否为 Monet 动态取色模式 |

## 自定义主题

可以通过以下方式进行主题自定义：

- 通过 `ThemeController(ColorSchemeMode.*)` 选择配色模式。
- 选择动态配色：`MonetSystem` / `MonetLight` / `MonetDark`。
- 通过 `paletteStyle` 选择调色板风格，通过 `colorSpec` 选择颜色规范。
- 传入 `textStyles` 覆盖文本样式：

```kotlin
val customTextStyles = defaultTextStyles(
    title1 = TextStyle(
        fontSize = 36.sp,
        fontWeight = FontWeight.Bold
    ),
    // 其他文本样式...
)

val controller = remember { ThemeController(ColorSchemeMode.Light) }
COUITheme(
    controller = controller,
    textStyles = customTextStyles
) {
    // 您的应用内容
}
```

## 跟随系统深色模式

跟随系统深色模式已内置，使用 `ColorSchemeMode.System` 即可：

```kotlin
@Composable
fun MyApp() {
    val controller = remember { ThemeController(ColorSchemeMode.System) }
    COUITheme(controller = controller) {
        // 应用内容
    }
}
```

## 圆角档位

`COUIRoundCorner` 收录 COUI 圆角档位（`coui_round_corner_*`）：`XS` 4、`S` 8、`M` 12、`L` 16、`XL` 20、`XXL` 24、`XXXL` 28、`XXXXL` 32 dp。ColorOS 17 在平滑圆角路径上会把基础档换成更大的半径，对应 `COUIRoundCorner.Normal`（卡片列表，`*_radius_normal_16_1`；`Normal.XL` = 24dp，即 `Card` 默认圆角）和 `COUIRoundCorner.Large`（按钮与卡片视图，`*_radius_large_16_1`）。每组档位附带平滑权重，即 squircle modifier 的 `weight` 参数（超椭圆指数，见 [平滑圆角](./squircle.md)）：`COUIRoundCorner.Normal.Weight` = 2.5（`weight_16_1_normal`），`COUIRoundCorner.Large.Weight` = 3.0（`weight_16_1_large`，对话框、底部面板与气泡提示也用它），基础档与圆形用 `COUIRoundCorner.CircularWeight` = 2.0（ColorOS 17 不做平滑），全圆控件用 `COUIRoundCorner.CapsuleWeight` = 1.0（Oplus 软胶囊，配合 `COUIRoundCorner.Full`，即 couiRoundCornerFULL 72dp）。`COUIRoundCorner.UnspecifiedWeight` 对应 COUI 不传权重的调用点：全圆时解析为软胶囊，否则为 `COUIRoundCorner.DefaultG2Weight` = 2.45（`default_g2_weight`）。

## 状态蒙层

`COUITheme` 把 `COUIIndication` 设为 `LocalIndication`。它把配色方案中的 `hover`、`focus`、`press` 三色作为三个独立图层绘制（COUI `COUIMaskEffectDrawable`），各自使用 response 0.3 / bounce 0 弹簧。按压层进度未到 70% 就抬手时，会先继续走到 70% 再回落，保证快速点按也可见。
