# Text Styles

This page documents all text styles provided by COUI, based on the actual implementation.

## Using Text Styles

- Use `COUITheme.textStyles.<name>` in composables.
- The color of all styles is set from `COUITheme.colorScheme.onBackground` at runtime.

## Styles

Every style is a COUI type step: its `textSize`, its weight and its `android:lineSpacingMultiplier`
(the `values-v35` bucket that ColorOS 16 and 17 resolve).

| Style Name | Size | Weight | Multiplier | Line height | COUI text appearance |
|------------|------|--------|------------|-------------|----------------------|
| `main`     | 16sp | Normal | 1.158  | 21.71sp | couiTextAppearanceBodyXL (TextBodyXL) |
| `paragraph`| 16sp | Normal | 1.1321 | 21.23sp | couiTextAppearanceBodyL |
| `body1`    | 16sp | Normal | 1.158  | 21.71sp | couiTextAppearanceBodyXL (TextBodyXL) |
| `body2`    | 14sp | Normal | 1.2245 | 20.09sp | couiTextAppearanceBody (TextBodyM) |
| `button`   | 16sp | Medium | 1.1579 | 21.71sp | couiTextAppearanceButtonL |
| `footnote1`| 13sp | Normal | 1.1739 | 17.88sp | couiTextAppearanceArticleBody (TextBodyS) |
| `footnote2`| 12sp | Normal | 1.143  | 16.07sp | couiTextAppearanceDescription (TextBodyXS) |
| `headline1`| 16sp | Medium | 1.158  | 21.71sp | couiTextAppearanceHeadline6 |
| `headline2`| 16sp | Medium | 1.158  | 21.71sp | couiTextAppearanceHeadline6 |
| `subtitle` | 14sp | Medium | 1.2245 | 20.09sp | couiTextAppearanceButton (TextButtonM) |
| `title1`   | 32sp | SemiBold | 1.2322 | 46.21sp | couiTextAppearanceHeadline1 |
| `title2`   | 24sp | SemiBold | 1.2  | 33.75sp | couiTextAppearanceHeadline3 |
| `title3`   | 20sp | Medium | 1.1831 | 27.73sp | couiTextAppearanceHeadline4 |
| `title4`   | 18sp | Medium | 1.1429 | 24.11sp | couiTextAppearanceHeadline5 |

::: tip Line height model
Android's `lineSpacingMultiplier` scales the font's metrics height (ascent + descent ≈ 1.1719em for
Roboto), not the font size, so a COUI line is `fontSize × multiplier × 1.1719` tall. The styles fold
that factor into `lineHeight`, which Compose treats as an absolute line box, so line heights match
ColorOS in dp on every platform whatever font is rendered. This was checked against the device: a
ColorOS 16 preference title node is 21.71dp tall at 560 dpi, exactly `headline1`.

Every style carries `LineHeightStyle(alignment = Proportional, trim = None)`: the extra leading is kept
on single-line text, as an Android TextView keeps it, and is split above and below the glyphs in
proportion to the font's ascent and descent.
:::

## Usage

```kotlin
Text(
    text = "Title",
    style = COUITheme.textStyles.title2
)

Text(
    text = "Body",
    style = COUITheme.textStyles.body1
)
```

## Customization

- Override styles via `defaultTextStyles(...)` and pass to `COUITheme(textStyles = ...)`.

```kotlin
val customTextStyles = defaultTextStyles(
    title1 = TextStyle(
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
