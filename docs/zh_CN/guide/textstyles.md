# 文本样式

本页基于实际实现，完整列出 COUI 提供的文本样式。

## 使用方式

- 在组合函数中通过 `COUITheme.textStyles.<名称>` 访问。
- 所有样式的颜色会在运行时由 `COUITheme.colorScheme.onBackground` 统一设置。

## 样式列表

每个样式都是一档 COUI 字阶：字号、字重和 `android:lineSpacingMultiplier`（取 ColorOS 16 / 17 生效的 `values-v35` 资源桶）。

| 样式名       | 字号  | 字重   | 行距倍数 | 行高 | 对应 COUI 样式 |
|--------------|-------|--------|----------|------|----------------|
| `main`       | 16sp  | Normal | 1.158  | 21.71sp | couiTextAppearanceBodyXL（TextBodyXL） |
| `paragraph`  | 16sp  | Normal | 1.1321 | 21.23sp | couiTextAppearanceBodyL |
| `body1`      | 16sp  | Normal | 1.158  | 21.71sp | couiTextAppearanceBodyXL（TextBodyXL） |
| `body2`      | 14sp  | Normal | 1.2245 | 20.09sp | couiTextAppearanceBody（TextBodyM） |
| `button`     | 16sp  | Medium | 1.1579 | 21.71sp | couiTextAppearanceButtonL |
| `footnote1`  | 13sp  | Normal | 1.1739 | 17.88sp | couiTextAppearanceArticleBody（TextBodyS） |
| `footnote2`  | 12sp  | Normal | 1.143  | 16.07sp | couiTextAppearanceDescription（TextBodyXS） |
| `headline1`  | 16sp  | Medium | 1.158  | 21.71sp | couiTextAppearanceHeadline6 |
| `headline2`  | 16sp  | Medium | 1.158  | 21.71sp | couiTextAppearanceHeadline6 |
| `subtitle`   | 14sp  | Medium | 1.2245 | 20.09sp | couiTextAppearanceButton（TextButtonM） |
| `title1`     | 32sp  | SemiBold | 1.2322 | 46.21sp | couiTextAppearanceHeadline1 |
| `title2`     | 24sp  | SemiBold | 1.2  | 33.75sp | couiTextAppearanceHeadline3 |
| `title3`     | 20sp  | Medium | 1.1831 | 27.73sp | couiTextAppearanceHeadline4 |
| `title4`     | 18sp  | Medium | 1.1429 | 24.11sp | couiTextAppearanceHeadline5 |

::: tip 行高口径
Android 的 `lineSpacingMultiplier` 乘的是字体度量高（ascent + descent，Roboto 约 1.1719em），不是字号，所以 COUI 的一行高 `字号 × 倍数 × 1.1719`。这些样式把该系数折进了 `lineHeight`；Compose 的行高是绝对行盒，因此不论平台实际渲染什么字体，行高都与 ColorOS 的 dp 值一致。真机核对：ColorOS 16 在 560dpi 下的列表项标题节点高 21.71dp，正好等于 `headline1`。

所有样式都带 `LineHeightStyle(alignment = Proportional, trim = None)`：单行文本也保留额外行距（与 Android TextView 一致），并按字体 ascent / descent 的比例分到字形上下。
:::

## 使用示例

```kotlin
Text(
    text = "标题",
    style = COUITheme.textStyles.title2
)

Text(
    text = "正文",
    style = COUITheme.textStyles.body1
)
```

## 自定义

- 通过 `defaultTextStyles(...)` 覆盖样式，并传入 `COUITheme(textStyles = ...)`。

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
) { /* 内容 */ }
```
