# PagerFooterPreference

`PagerFooterPreference` 是设置页底部、最后一张卡片下方的说明文字，对应 COUI 的 `COUIPagerFooterPreference`（布局 `coui_pager_footer_preference`）。说明中可含 COUI 链接样式的可点击链接，即 COUI `couiEnalbeClickSpan` 开启的能力。

<div style="position: relative; height: 200px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=pagerFooterPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.appendLink
import io.github.suqi8.coui.kmp.preference.PagerFooterPreference
```

## 基本用法

```kotlin
PagerFooterPreference(
    summary = "Updates download over WLAN only."
)
```

### 带链接

```kotlin
PagerFooterPreference(
    summary = buildAnnotatedString {
        append("Updates download over WLAN only. ")
        appendLink("Data usage", onClick = { /* 打开流量使用页 */ })
    }
)
```

### 去掉额外底部外边距

页脚默认保留 24dp 底部外边距（`support_preference_footer_preference_margin_bottom`）。页脚后面还有内容时可关闭：

```kotlin
PagerFooterPreference(
    summary = "Updates download over WLAN only.",
    withExtraMarginBottom = false
)
```

## 属性

### PagerFooterPreference 属性

| 属性名                | 类型                     | 说明                                        | 默认值                                     | 必须 |
| --------------------- | ------------------------ | ------------------------------------------- | ------------------------------------------ | ---- |
| summary               | AnnotatedString / String | 说明文字（AnnotatedString 重载可含链接）    | -                                          | 是   |
| modifier              | Modifier                 | 组件修饰符                                  | Modifier                                   | 否   |
| title                 | String?                  | 可选标题                                    | null                                       | 否   |
| withExtraMarginBottom | Boolean                  | 是否保留 24dp 底部外边距                    | true                                       | 否   |
| titleColor            | Color                    | 标题颜色                                    | PagerFooterPreferenceDefaults.TitleColor   | 否   |
| summaryColor          | Color                    | 非链接说明文字颜色                          | PagerFooterPreferenceDefaults.SummaryColor | 否   |
| linkColors            | LinkTextColors           | 链接颜色（仅 AnnotatedString 重载）         | LinkTextDefaults.linkTextColors()          | 否   |
| insideMargin          | PaddingValues            | 文字四周内边距                              | PagerFooterPreferenceDefaults.InsideMargin | 否   |

### PagerFooterPreferenceDefaults 对象

| 属性名       | 类型          | 说明                                         | 值                                       |
| ------------ | ------------- | -------------------------------------------- | ---------------------------------------- |
| MarginBottom | Dp            | `withExtraMarginBottom` 时页脚下方间距       | 24.dp                                    |
| InsideMargin | PaddingValues | 文字四周内边距                               | horizontal = 32.dp, vertical = 8.dp      |
| TitleColor   | Color         | 标题颜色                                     | COUITheme.colorScheme.labelPrimary       |
| SummaryColor | Color         | 说明颜色                                     | COUITheme.colorScheme.labelSecondary |
