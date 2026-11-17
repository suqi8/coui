# Theme System

COUI provides a complete theme system that allows you to easily maintain a consistent design style
throughout your application. The theme system consists of color schemes and text styles.

## Using COUITheme

Use `ThemeController` to control the color scheme mode, then wrap your content with `COUITheme`:

```kotlin
@Composable
fun App() {
    // Available modes: System, Light, Dark, MonetSystem, MonetLight, MonetDark
    val controller = remember { ThemeController(ColorSchemeMode.System) }
    COUITheme(controller = controller) { /* Content */ }
}
```

`ColorSchemeMode.System` / `ColorSchemeMode.MonetSystem` automatically follows the system's dark mode.

### Specific Modes

- **Dynamic Colors (Monet)**

  Use `ThemeController` with `Monet` modes to enable dynamic colors.
  - If `keyColor` is provided, colors are generated from this seed color.
  - If `keyColor` is `null` (default), it attempts to use the system's wallpaper colors (Android "Material You").

  ```kotlin
  @Composable
  fun AppWithMonet() {
      val controller = remember {
          ThemeController(
              ColorSchemeMode.MonetSystem, // or MonetLight, MonetDark
              // keyColor = Color(0xFF3482FF) // Optional: Custom seed color
          )
      }
      COUITheme(controller = controller) { /* Content */ }
  }
  ```

- **Palette Style and Color Spec**

  You can customize the palette style and color specification used by Monet color generation via `paletteStyle` and `colorSpec`:

  ```kotlin
  val controller = remember {
      ThemeController(
          ColorSchemeMode.MonetSystem,
          keyColor = Color(0xFF3482FF),
          paletteStyle = ThemePaletteStyle.Vibrant,
          colorSpec = ThemeColorSpec.Spec2025
      )
  }
  COUITheme(controller = controller) { /* Content */ }
  ```

  ::: tip
  `ThemeColorSpec.Spec2025` is only supported by `TonalSpot`, `Neutral`, `Vibrant`, and `Expressive` palette styles. For other styles, it gracefully falls back to `Spec2021`.
  :::

- **Manual Dark Mode Control**

  Use the `isDark` parameter to explicitly control the dark state, overriding the system setting.

  ```kotlin
  val controller = remember {
      ThemeController(
          ColorSchemeMode.System,
          isDark = true // Force dark mode
      )
  }
  ```

- **Custom Color Schemes**

  You can provide custom `lightColors` and `darkColors` directly to the `ThemeController`.

  ```kotlin
  val controller = remember {
      ThemeController(
          ColorSchemeMode.System,
          lightColors = myLightColors,
          darkColors = myDarkColors
      )
  }
  ```

- **Theme Color Families**

  COUI themes change color by swapping ten theme attributes (primary, theme containers and labels, disable, focus,
  focus outline, the translucent theme tint) and nothing else. `COUIThemeColors` holds the official families: the
  static `Blue` (default), `Green`, `Red`, `Orange` and `Yellow` (`Theme.COUI.<Color>`), every
  `COUIOverlay.Theme.*` preset (`GreenFirst`, `SkyBlueThird`, `SingleFirst` .. `SingleTenth`, ...; also
  `COUIThemeColors.overlay(identity, set)` and `COUIThemeColors.Singles`), and
  `COUIThemeColor.fromColor(color)` derives a family from any color with the COUI single-color rule.

  ```kotlin
  val controller = remember { ThemeController(ColorSchemeMode.System, themeColor = COUIThemeColors.Green) }
  // or: lightColorScheme(COUIThemeColors.Green), darkColorScheme(COUIThemeColors.Green),
  //     myColors.withThemeColor(COUIThemeColor.fromColor(Color(0xFFFF6436)))
  ```

- **Direct Usage**

  Provide a color scheme directly to `COUITheme(colors = ...)` for full customization without a controller:

  ```kotlin
  @Composable
  fun AppWithColors() {
      val colors = lightColorScheme() // or darkColorScheme()
      COUITheme(colors = colors) { /* Content */ }
  }
  ```

## ThemeController

The `ThemeController` manages the current color scheme of the COUI theme.

### ThemeController Properties

| Property Name | Type | Description | Default Value |
| --- | --- | --- | --- |
| colorSchemeMode | ColorSchemeMode | The color scheme mode | ColorSchemeMode.System |
| lightColors | Colors | Light color scheme | lightColorScheme() |
| darkColors | Colors | Dark color scheme | darkColorScheme() |
| keyColor | Color? | Seed color for dynamic color generation. `null` uses system wallpaper colors | null |
| colorSpec | ThemeColorSpec | Material color specification version | ThemeColorSpec.Spec2021 |
| paletteStyle | ThemePaletteStyle | Palette style for dynamic color generation | ThemePaletteStyle.TonalSpot |
| isDark | Boolean? | Override system dark mode. `null` follows system | null |
| themeColor | COUIThemeColor? | Theme color family applied over `lightColors` / `darkColors` (not in Monet modes) | null |

::: tip Monet
COUI itself has no tonal mapping: a system theme color only replaces the ten theme attributes. In Monet modes the
theme slots therefore follow the single-color rule (`couiColorSecondary` = 15% / 30%, `couiColorDisable` = 30%, ...)
and the labels keep their COUI alphas (`couiColorLabelSecondary` 54%, `couiColorLabelTertiary` /
`couiColorDisabledNeutral` 26% / 30%) over the tonal `onSurface`.
:::

### ColorSchemeMode

| Value | Description |
| --- | --- |
| System | Follows system light/dark setting |
| Light | Forces light mode |
| Dark | Forces dark mode |
| MonetSystem | Dynamic colors, follows system light/dark |
| MonetLight | Dynamic colors, forces light mode |
| MonetDark | Dynamic colors, forces dark mode |

### ThemePaletteStyle

| Value | Spec2025 Support | Description |
| --- | --- | --- |
| TonalSpot | Yes | Default Material You style with balanced tonal variations |
| Neutral | Yes | Muted, low-chroma palette |
| Vibrant | Yes | High-chroma, vivid palette |
| Expressive | Yes | Bold and artistic palette with creative color shifts |
| Rainbow | No | Broad spectrum of hues |
| FruitSalad | No | Playful, multi-hue palette |
| Monochrome | No | Single-hue grayscale palette |
| Fidelity | No | Closely matches the seed color |
| Content | No | Derived from content colors for maximum accuracy |

### ThemeColorSpec

| Value | Description |
| --- | --- |
| Spec2021 | Original Material Design 3 color specification |
| Spec2025 | Updated 2025 color specification (only supported by TonalSpot, Neutral, Vibrant, Expressive) |

## COUITheme Object

Access the current theme values through the `COUITheme` object:

```kotlin
val colors = COUITheme.colorScheme
val textStyles = COUITheme.textStyles
val mode = COUITheme.colorSchemeMode
val isDynamic = COUITheme.isDynamicColor
val isDark = COUITheme.isDark
```

| Property | Type | Description |
| --- | --- | --- |
| colorScheme | Colors | Current color scheme |
| textStyles | TextStyles | Current text styles |
| colorSchemeMode | ColorSchemeMode? | Current color scheme mode (null if using direct `colors` overload) |
| isDynamicColor | Boolean | Whether the current mode is a Monet dynamic color mode |
| isDark | Boolean | Whether the current scheme is dark (COUI `isCOUIDarkTheme`, `Colors.isDark`) |

`COUITheme` also provides `LocalTextSelectionColors`: handles in `primary` (`coui_text_select_*_tint`) and the
highlight `textColorHighlight` (`android:textColorHighlight` = `?couiColorSecondary`).

## Customizing the Theme

You can customize the theme in the following ways:

- Select a color scheme mode via `ThemeController(ColorSchemeMode.*)`.
- Opt into dynamic colors via `MonetSystem` / `MonetLight` / `MonetDark`.
- Choose a palette style via `paletteStyle` and a color spec via `colorSpec`.
- Override text styles by passing `textStyles`:

```kotlin
val customTextStyles = defaultTextStyles(
    displayS = TextStyle(
        fontSize = 36.sp,
        fontWeight = FontWeight.Bold
    ),
    // Other text styles...
)

val controller = remember { ThemeController(ColorSchemeMode.Light) }
COUITheme(
    controller = controller,
    textStyles = customTextStyles
) {
    // Your application content
}
```

## Follow System Dark Mode

Following the system's dark mode is built-in. Use `ColorSchemeMode.System`:

```kotlin
@Composable
fun MyApp() {
    val controller = remember { ThemeController(ColorSchemeMode.System) }
    COUITheme(controller = controller) {
        // Application content
    }
}
```

## Corner Radius Steps

`COUIRoundCorner` holds the COUI corner radius steps (`coui_round_corner_*`): `XS` 4, `S` 8, `M` 12, `L` 16,
`XL` 20, `XXL` 24, `XXXL` 28, `XXXXL` 32 dp. ColorOS 17 swaps a base step for a larger radius on its smooth-corner
paths, exposed as `COUIRoundCorner.Normal` (card lists, `*_radius_normal_16_1`: `Normal.XL` = 24dp, the default
`Card` radius) and `COUIRoundCorner.Large` (buttons and card views, `*_radius_large_16_1`). Each family carries its
smoothing weight, the superellipse exponent the squircle modifiers take as `weight` (see [Squircle](./squircle.md)):
`COUIRoundCorner.Normal.Weight` = 2.5 (`weight_16_1_normal`), `COUIRoundCorner.Large.Weight` = 3.0
(`weight_16_1_large`, also dialogs, bottom sheets and tooltips), `COUIRoundCorner.CircularWeight` = 2.0 for the base
steps and circles, which ColorOS 17 draws without smoothing, and `COUIRoundCorner.CapsuleWeight` = 1.0, the Oplus soft
capsule of full-round controls (pair it with `COUIRoundCorner.Full`, couiRoundCornerFULL 72dp).
`COUIRoundCorner.UnspecifiedWeight` stands for the COUI call sites that pass no weight: it resolves to the soft capsule
when the rect is fully rounded and to `COUIRoundCorner.DefaultG2Weight` = 2.45 (`default_g2_weight`) otherwise.
`COUIRoundCorner.largeRadiusFor(radius)` maps a base step to its `Large` radius (`RoundCornerUtil.getRoundCornerForOS17`).

## Layout Grid and Spacing

`COUILayoutGrid` is the COUI responsive grid (`columnCount`, `margin`, `gutter`, `columnWidth`, `spanWidth`,
`gridWidth`, `screenTier` for a window size) plus the shared page spacings: `CardMarginHorizontal` 16dp
(`coui_preference_card_margin_horizontal`), `DefaultMarginHorizontal` 24dp (`grid_guide_column_default_margin_*`),
`ColumnGap` 8dp, `CardSpecialPadding` 16dp, `ListSpecialPadding` 24dp, `MarginSmall` 16dp, `MarginLarge` 24dp,
`Gutter` 8dp and `GutterExpanded` 12dp.

## State Layers

`COUITheme` installs `COUIIndication` as `LocalIndication`. It draws the `hover`, `focus` and `press` colors of the
scheme as three independent layers (COUI `COUIMaskEffectDrawable`), each on a response 0.3 / bounce 0 spring. A
release that comes before the press layer reaches 70% first carries it on to 70%, so quick taps stay visible.
