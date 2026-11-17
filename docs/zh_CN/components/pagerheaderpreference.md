# PagerHeaderPreference

`PagerHeaderPreference` 是设置页顶部、第一张卡片上方的说明文字，对应 COUI 的 `COUIPagerHeaderPreference`（布局 `coui_pager_header_preference`）。在页面背景上显示可选标题和其下方 12sp 次要色的说明。

<div style="position: relative; height: 220px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=pagerHeaderPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.preference.PagerHeaderPreference
```

## 基本用法

```kotlin
LazyColumn {
    item {
        PagerHeaderPreference(
            summary = "Limits background activity and visual effects to extend battery life."
        )
    }
    item {
        Card(modifier = Modifier.padding(horizontal = 16.dp)) {
            SwitchPreference(checked = on, onCheckedChange = { on = it }, title = "Battery saver")
        }
    }
}
```

默认 32dp 水平内边距让文字与内缩 16dp 的卡片内文字对齐。页头保留 12dp 顶部外边距（`support_preference_header_preference_margin_top`）。

### 带标题

```kotlin
PagerHeaderPreference(
    title = "Battery saver",
    summary = "Limits background activity and visual effects to extend battery life."
)
```

## 属性

### PagerHeaderPreference 属性

| 属性名       | 类型          | 说明           | 默认值                                     | 必须 |
| ------------ | ------------- | -------------- | ------------------------------------------ | ---- |
| summary      | String        | 说明文字       | -                                          | 是   |
| modifier     | Modifier      | 组件修饰符     | Modifier                                   | 否   |
| title        | String?       | 可选标题       | null                                       | 否   |
| titleColor   | Color         | 标题颜色       | PagerHeaderPreferenceDefaults.TitleColor   | 否   |
| summaryColor | Color         | 说明文字颜色   | PagerHeaderPreferenceDefaults.SummaryColor | 否   |
| insideMargin | PaddingValues | 文字四周内边距 | PagerHeaderPreferenceDefaults.InsideMargin | 否   |

### PagerHeaderPreferenceDefaults 对象

| 属性名       | 类型          | 说明                                            | 值                                       |
| ------------ | ------------- | ----------------------------------------------- | ---------------------------------------- |
| MarginTop    | Dp            | 页头上方间距                                    | 12.dp                                    |
| InsideMargin | PaddingValues | 文字四周内边距                                  | horizontal = 32.dp, vertical = 8.dp      |
| TitleColor   | Color         | 标题颜色                                        | COUITheme.colorScheme.labelPrimary       |
| SummaryColor | Color         | 说明颜色（`coui_preference_secondary_text_color`） | COUITheme.colorScheme.labelSecondary |
