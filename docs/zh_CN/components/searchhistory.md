# SearchHistory

`SearchHistory` 复刻 ColorOS 的搜索历史面板（`COUISearchHistoryView` + `COUIFlowLayout`）：带清空按钮的标题栏，以及按行流式排布的胶囊历史记录，可折叠到展开按钮之后。

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=searchHistory" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.SearchHistory
import io.github.suqi8.coui.kmp.basic.SearchHistoryDefaults
```

## 基本用法

```kotlin
val history = remember { mutableStateListOf("Wallpaper", "Dark mode", "Bluetooth") }

SearchHistory(
    title = "Search history",
    items = history,
    onItemClick = { query -> search(query) },
    onClear = { history.clear() },
    maxLinesFolded = 2,
    clearContentDescription = "Clear search history",
)
```

`items` 为空时面板不绘制任何内容。

## 折叠

设置 `maxLinesFolded` 后，超出该行数的记录会被折叠：圆形展开按钮占据最后一个折叠行的末尾（放不下时替换该行最后一条记录）。展开后最多显示 `maxLinesExpanded` 行，并在最后一行末尾显示收起按钮。`expandable = false` 时面板固定显示 `maxLinesFolded` 行，不显示按钮。

记录条目是 COUI 建议型标签（`SuggestionChip`）：按下时缩放并叠加按压色，快速轻点也会等按压色足够明显后再褪去；文字只随系统字体缩放放大到 1.35（`COUIChangeTextUtil` G4）。展开与收起按钮按下时在 200ms 内缩到 0.9、松手后 340ms 恢复；快速轻点时会先让缩小动画走完 200ms 的 80% 再恢复（`COUIPressFeedbackImageView`）。

## 属性

### SearchHistory

| 属性名                  | 类型                | 说明                         | 默认值                                      | 是否必须 |
| ----------------------- | ------------------- | ---------------------------- | ------------------------------------------- | -------- |
| title                   | String              | 面板标题                     | -                                           | 是       |
| items                   | List\<String\>      | 按显示顺序排列的历史记录     | -                                           | 是       |
| onItemClick             | (String) -> Unit    | 以被点击的记录回调           | -                                           | 是       |
| onClear                 | () -> Unit          | 清空动画开始时回调           | -                                           | 是       |
| modifier                | Modifier            | 应用于面板的修饰符           | Modifier                                    | 否       |
| expandable              | Boolean             | 折叠的记录是否可展开         | true                                        | 否       |
| maxLinesFolded          | Int                 | 折叠时显示的行数             | SearchHistoryDefaults.MaxLinesFolded        | 否       |
| maxLinesExpanded        | Int                 | 展开时显示的行数             | SearchHistoryDefaults.MaxLinesExpanded      | 否       |
| clearContentDescription | String?             | 清空按钮的无障碍描述         | null                                        | 否       |
| colors                  | SearchHistoryColors | 面板颜色                     | SearchHistoryDefaults.searchHistoryColors() | 否       |
| insideMargin            | PaddingValues       | 面板内容四周的内边距         | SearchHistoryDefaults.insideMargin()        | 否       |

### SearchHistoryDefaults

| 常量                  | 类型          | 默认值                                    | COUI 来源                                            |
| --------------------- | ------------- | ----------------------------------------- | ---------------------------------------------------- |
| MaxLinesFolded        | Int           | Int.MAX_VALUE                             | maxRowFolded 默认值                                  |
| MaxLinesExpanded      | Int           | 8                                         | DEFAULT_MAX_ROW_UNFOLDED                             |
| HorizontalPadding     | Dp            | 16.dp                                     | coui_component_search_history_padding_start / _end   |
| HorizontalPaddingWide | Dp            | 20.dp                                     | values-w600dp padding_start / _end                   |
| WideWindowWidth       | Dp            | 600.dp                                    | values-w600dp 限定符                                 |
| TopPadding            | Dp            | 16.dp                                     | coui_component_search_history_padding_top            |
| BottomPadding         | Dp            | 32.dp                                     | coui_component_search_history_padding_bottom         |
| TitleVerticalPadding  | Dp            | 7.dp                                      | coui_component_search_history_title_margin_vertical  |
| ClearIconSize         | Dp            | 18.dp                                     | coui_component_search_history_delete_icon_size       |
| ClearPressRadius      | Dp            | 24.dp                                     | coui_component_icon_bg 水波纹半径                    |
| FlowTopMargin         | Dp            | 8.dp                                      | coui_component_search_history_flow_margin_top        |
| ItemSpacing           | Dp            | 8.dp                                      | coui_component_search_history_flow_item_spacing      |
| LineSpacing           | Dp            | 8.dp                                      | coui_component_search_history_flow_line_spacing      |
| ChipHeight            | Dp            | 28.dp                                     | coui_chip_suggest_style_height                       |
| ChipMinWidth          | Dp            | 52.dp                                     | coui_chip_default_min_width                          |
| ChipMaxWidth          | Dp            | 300.dp                                    | coui_chip_default_max_width                          |
| ChipTextMaxWidth      | Dp            | 200.dp                                    | coui_chip_default_max_text_width                     |
| ChipHorizontalPadding | Dp            | 12.dp                                     | coui_chip_suggest_style_chip_horizontal_padding      |
| ToggleIconSize        | Dp            | 16.dp                                     | coui_component_expand_arrow_drop_down                |

| 方法                  | 类型                | 说明                                        |
| --------------------- | ------------------- | ------------------------------------------- |
| titleTextStyle()      | TextStyle           | subtitle，couiTextAppearanceButton 14sp 中粗 |
| chipTextStyle()       | TextStyle           | bodyXS，couiTextBodyXS 12sp                 |
| searchHistoryColors() | SearchHistoryColors | 创建面板颜色                                |

### `searchHistoryColors()` 工厂

| 参数              | 类型  | 默认值                                   | COUI 来源               |
| ----------------- | ----- | ---------------------------------------- | ----------------------- |
| titleColor        | Color | COUITheme.colorScheme.labelPrimary       | couiColorPrimaryNeutral |
| clearIconColor    | Color | COUITheme.colorScheme.labelSecondary | couiColorSecondNeutral  |
| clearPressColor   | Color | COUITheme.colorScheme.press              | couiColorPress          |
| chipColor         | Color | COUITheme.colorScheme.container8         | couiColorContainer8     |
| chipContentColor  | Color | COUITheme.colorScheme.labelPrimary          | couiColorLabelPrimary   |
| toggleButtonColor | Color | COUITheme.colorScheme.container8         | couiColorContainer8     |
| toggleIconColor   | Color | COUITheme.colorScheme.labelSecondary | couiColorSecondNeutral  |

## 行为

- 记录为 28dp 胶囊（12sp 文字，12dp 水平内边距），按 `COUIFlowLayout` 排布：放得下时以 8dp 间距加入当前行；比面板还宽的记录独占一行。
- 展开时立即切换到展开高度，额外记录和收起按钮在 100ms 延迟后用 400ms 淡入，展开按钮用 250ms 淡出。收起时它们在 300ms 内淡出，然后面板收回。所有淡入淡出都使用 COUIMoveEaseInterpolator；不可见的记录无法点击。
- 记录按 COUI 胶囊的方式按压（按压缩放 + couiColorPress 着色）；展开与收起按钮在 200ms 内缩放到 0.9，并在 340ms 内恢复。
- 点击清空按钮会立即调用 `onClear`，同时整个面板在 300ms 内淡出；淡出期间仍显示被清空的记录，因此 `onClear` 里可以直接清空 `items`（COUI `setOnDeleteClickListener`）。
- `SearchHistoryDefaults.insideMargin()` 左右内边距为 16dp，窗口宽度不小于 600dp 时为 20dp（COUI `values-w600dp`）。
