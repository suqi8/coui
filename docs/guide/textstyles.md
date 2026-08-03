# Text Styles

This page documents all text styles provided by COUI, based on the actual implementation.

## Using Text Styles

- Use `COUITheme.textStyles.<name>` in composables.
- The color of all styles is set from `COUITheme.colorScheme.onBackground` at runtime.

## Styles

Line heights come from each `couiTextAppearance*`'s `lineSpacingMultiplier`, taken from the
`values-v35` bucket that ColorOS 16 resolves.

| Style Name | Size | Weight | Line Height | COUI text appearance |
|------------|------|--------|-------------|----------------------|
| `main`     | 17sp | Normal | 1.158em     | couiTextAppearanceHeadline6 |
| `paragraph`| 17sp | Normal | 1.158em     | couiTextAppearanceBodyL |
| `body1`    | 16sp | Normal | 1.158em     | couiTextAppearanceBodyL |
| `body2`    | 14sp | Normal | 1.2245em    | couiTextAppearanceBody |
| `button`   | 16sp | Medium | 1.263em     | couiTextAppearanceButtonL |
| `footnote1`| 13sp | Normal | 1.2245em    | couiTextAppearanceBody |
| `footnote2`| 11sp | Normal | 1.143em     | couiTextAppearanceDescription |
| `headline1`| 16sp | Normal | 1.158em     | couiTextAppearanceHeadline6 |
| `headline2`| 16sp | Normal | 1.158em     | couiTextAppearanceHeadline6 |
| `subtitle` | 14sp | Bold   | 1.2245em    | couiTextAppearanceBody |
| `title1`   | 32sp | Normal | 1.2322em    | couiTextAppearanceHeadline1 |
| `title2`   | 24sp | Normal | 1.2em       | couiTextAppearanceHeadline3 |
| `title3`   | 20sp | Normal | 1.1831em    | couiTextAppearanceHeadline4 |
| `title4`   | 18sp | Normal | 1.2381em    | couiTextAppearanceHeadline5 |

::: tip
COUI has no 11sp / 13sp / 17sp text appearance, so those styles borrow the multiplier of the
closest tier by font size. `headline1` is the exception that was measured rather than inferred: a
ColorOS 16 preference title node is 21.71dp tall at 560 dpi, which is 16sp — not the 17sp inherited
from Miuix.

Every style carries `LineHeightStyle(alignment = Top, trim = None)` to match Android's
`lineSpacingMultiplier`: the extra leading sits below the line and is never trimmed. Compose's
default centres the leading and trims it on the first and last line, which cancels the line height
out entirely on single-line text.
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
