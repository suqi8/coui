# 文本样式

本页基于实际实现，完整列出 COUI 提供的文本样式。

## 使用方式

- 在组合函数中通过 `COUITheme.textStyles.<名称>` 访问。
- 所有样式的颜色会在运行时由 `COUITheme.colorScheme.labelPrimary` 统一设置。

## 样式列表

每个样式都是一档 COUI 字阶：字号、字重和 `android:lineSpacingMultiplier`（取 ColorOS 16 / 17 生效的 `values-v35` 资源桶）。

COUI 17 全部字阶，使用官方名称（`couiTextXxx` → `xxx`）：

| 样式名 | 字号 | 字重 | 行距倍数 | 行高 | 对应 COUI 样式 |
|--------|------|------|----------|------|----------------|
| `displayL` | 72sp | Medium | 1.1384 | 96.05sp | couiTextDisplayL |
| `display` | 48sp | Medium | 1.0 | 56.25sp | couiTextAppearanceDisplay |
| `displayM` | 36sp | SemiBold（600） | 1.191 | 50.25sp | couiTextDisplayM |
| `displayS` | 32sp | SemiBold（600） | 1.2322 | 46.21sp | couiTextDisplayS |
| `displayXS` | 24sp | SemiBold（600） | 1.2 | 33.75sp | couiTextDisplayXS |
| `headlineL` | 28sp | Medium | 1.2245 | 40.18sp | couiTextHeadlineL |
| `headlineM` | 20sp | Medium | 1.1831 | 27.73sp | couiTextHeadlineM |
| `headlineS` | 18sp | 550 | 1.1429 | 24.11sp | couiTextHeadlineS（`textFontWeight` 550） |
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

## 字号缩放封顶

`COUIFontScale` 对应 `COUIChangeTextUtil`：`COUIFontScale.cappedFontSize(fontSize, level)` 返回按 `G1`（不封顶）..
`G5`（上限 0.9 / 1.0 / 1.15 / 1.35 / 1.6；`G2`、`G3` 取台阶）封顶后的 `sp` 字号；
`COUIFontScale.suitableFontScale(fontScale, level)` 直接给出缩放倍数。

```kotlin
val style = COUITheme.textStyles.caption
Text("Tab", style = style.copy(fontSize = COUIFontScale.cappedFontSize(style.fontSize, COUIFontScale.G2)))
```

::: tip 行高口径
Android 的 `lineSpacingMultiplier` 乘的是字体度量高（ascent + descent，Roboto 约 1.1719em），不是字号，所以 COUI 的一行高 `字号 × 倍数 × 1.1719`。这些样式把该系数折进了 `lineHeight`；Compose 的行高是绝对行盒，因此不论平台实际渲染什么字体，行高都与 ColorOS 的 dp 值一致。真机核对：ColorOS 16 在 560dpi 下的列表项标题节点高 21.71dp，正好等于 `headlineXS`。

所有样式都带 `LineHeightStyle(alignment = Proportional, trim = None)`：单行文本也保留额外行距（与 Android TextView 一致），并按字体 ascent / descent 的比例分到字形上下。
:::

## 使用示例

```kotlin
Text(
    text = "标题",
    style = COUITheme.textStyles.displayXS
)

Text(
    text = "正文",
    style = COUITheme.textStyles.bodyXL
)
```

## 自定义

- 通过 `defaultTextStyles(...)` 覆盖样式，并传入 `COUITheme(textStyles = ...)`。

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
) { /* 内容 */ }
```
