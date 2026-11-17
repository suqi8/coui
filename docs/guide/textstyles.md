# Text Styles

This page documents all text styles provided by COUI, based on the actual implementation.

## Using Text Styles

- Use `COUITheme.textStyles.<name>` in composables.
- The color of all styles is set from `COUITheme.colorScheme.labelPrimary` at runtime.

## Styles

Every style is a COUI type step: its `textSize`, its weight and its `android:lineSpacingMultiplier`
(the `values-v35` bucket that ColorOS 16 and 17 resolve).

The full COUI 17 scale under its official names (`couiTextXxx` → `xxx`):

| Style Name | Size | Weight | Multiplier | Line height | COUI text appearance |
|------------|------|--------|------------|-------------|----------------------|
| `displayL` | 72sp | Medium | 1.1384 | 96.05sp | couiTextDisplayL |
| `display` | 48sp | Medium | 1.0 | 56.25sp | couiTextAppearanceDisplay |
| `displayM` | 36sp | SemiBold (600) | 1.191 | 50.25sp | couiTextDisplayM |
| `displayS` | 32sp | SemiBold (600) | 1.2322 | 46.21sp | couiTextDisplayS |
| `displayXS` | 24sp | SemiBold (600) | 1.2 | 33.75sp | couiTextDisplayXS |
| `headlineL` | 28sp | Medium | 1.2245 | 40.18sp | couiTextHeadlineL |
| `headlineM` | 20sp | Medium | 1.1831 | 27.73sp | couiTextHeadlineM |
| `headlineS` | 18sp | 550 | 1.1429 | 24.11sp | couiTextHeadlineS (`textFontWeight` 550) |
| `headlineXS` | 16sp | Medium | 1.158 | 21.71sp | couiTextHeadlineXS |
| `bodyXL` | 16sp | Normal | 1.158 | 21.71sp | couiTextBodyXL |
| `bodyL` | 15sp | Normal | 1.1321 | 19.90sp | couiTextBodyL |
| `bodyM` | 14sp | Normal | 1.2245 | 20.09sp | couiTextBodyM |
| `bodyS` | 13sp | Normal | 1.1739 | 17.88sp | couiTextBodyS |
| `bodyXS` | 12sp | Normal | 1.143 | 16.07sp | couiTextBodyXS |
| `buttonL` | 16sp | Medium | 1.1579 | 21.71sp | couiTextButtonL |
| `buttonM` | 14sp | Medium | 1.2245 | 20.09sp | couiTextButtonM |
| `buttonS` | 12sp | Medium | 1.143 | 16.07sp | couiTextButtonS |
| `description` | 12sp | Medium | 1.143 | 16.07sp | couiTextDescription |
| `caption` | 10sp | Normal | 1.2 | 14.06sp | couiTextCaption |
| `tag` | 10sp | Medium | 1.2 | 14.06sp | couiTextTag |

## Font Scale Caps

`COUIFontScale` mirrors `COUIChangeTextUtil`: `COUIFontScale.cappedFontSize(fontSize, level)` returns the `sp` size
that renders at the capped system font scale of `G1` (no cap) .. `G5` (caps 0.9 / 1.0 / 1.15 / 1.35 / 1.6;
`G2` and `G3` snap to steps). `COUIFontScale.suitableFontScale(fontScale, level)` gives the scale itself.

```kotlin
val style = COUITheme.textStyles.caption
Text("Tab", style = style.copy(fontSize = COUIFontScale.cappedFontSize(style.fontSize, COUIFontScale.G2)))
```

::: tip Line height model
Android's `lineSpacingMultiplier` scales the font's metrics height (ascent + descent ≈ 1.1719em for
Roboto), not the font size, so a COUI line is `fontSize × multiplier × 1.1719` tall. The styles fold
that factor into `lineHeight`, which Compose treats as an absolute line box, so line heights match
ColorOS in dp on every platform whatever font is rendered. This was checked against the device: a
ColorOS 16 preference title node is 21.71dp tall at 560 dpi, exactly `headlineXS`.

Every style carries `LineHeightStyle(alignment = Proportional, trim = None)`: the extra leading is kept
on single-line text, as an Android TextView keeps it, and is split above and below the glyphs in
proportion to the font's ascent and descent.
:::

## Usage

```kotlin
Text(
    text = "Title",
    style = COUITheme.textStyles.displayXS
)

Text(
    text = "Body",
    style = COUITheme.textStyles.bodyXL
)
```

## Customization

- Override styles via `defaultTextStyles(...)` and pass to `COUITheme(textStyles = ...)`.

```kotlin
val customTextStyles = defaultTextStyles(
    displayS = TextStyle(
        fontSize = 36.sp,
        fontWeight = FontWeight.Bold
    )
)

val controller = remember { ThemeController(ColorSchemeMode.System) }
COUITheme(
    controller = controller,
    textStyles = customTextStyles
) { /* Content */ }
```
