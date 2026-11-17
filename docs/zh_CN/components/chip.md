# Chip

胶囊形可选中筛选标签,对应 ColorOS 的 COUIChip 选择样式(Widget.COUI.Chip.Choice)。未选中时是半透明灰色胶囊配主文本色标签;选中后胶囊填充强调色、标签变为白色。两种状态间用 COUI 选中弹簧交叉渐变,按压时按 COUI 17 按压反馈缩放并叠加按压色。

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=chip" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.basic.Chip
import io.github.suqi8.coui.kmp.basic.ChipDefaults
import io.github.suqi8.coui.kmp.basic.IconChip
import io.github.suqi8.coui.kmp.basic.SuggestionChip
```

## 基本用法

```kotlin
var selected by remember { mutableStateOf(false) }

Chip(
    selected = selected,
    onClick = { selected = !selected },
    label = "推荐",
)
```

## 带前置图标

图标放置在 16dp 盒子里,并通过 `LocalContentColor` 染成当前标签色(COUIChip `chipIconApplyTint`)。

```kotlin
Chip(
    selected = selected,
    onClick = { selected = !selected },
    label = "推荐",
    icon = {
        Icon(
            imageVector = COUIIcons.Basic.Check,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
        )
    },
)
```

## 关闭图标、计数与红点

`onClose` 会在 Chip 末尾显示 COUI 关闭图标(`ic_chip_close`),点击它调用 `onClose` 而不是 `onClick`(`setCloseIconVisible` + `setOnCloseIconClickListener`)。`count` 在标签后 2dp 处绘制次要色计数(`setCountText`),`showRedDot` 在标签后绘制 6dp 提示红点(`setShowRedDot`)。

```kotlin
Chip(
    selected = selected,
    onClick = { selected = !selected },
    label = "下载",
    count = "12",
    showRedDot = true,
    onClose = { /* 移除该 Chip */ },
)
```

## 建议 Chip

`SuggestionChip` 对应 `Widget.COUI.Chip.Suggestion`(搜索历史的 `Widget.COUI.Chip.Record` 同样式):不可选中,高 28dp,标签 12sp,前置图标 14dp,关闭图标 12dp;点击只调用 `onClick`。

```kotlin
SuggestionChip(
    onClick = { /* 再次搜索 */ },
    label = "壁纸",
    onClose = { /* 删除记录 */ },
)
```

## 图标 Chip

`IconChip` 对应 `Widget.COUI.Chip.Suggestion.Collapsable`:28dp 圆形、仅含图标,`ChipGroup` 的展开 / 收起按钮即用它。

```kotlin
IconChip(onClick = { /* ... */ }) {
    Icon(imageVector = COUIIcons.Basic.Search, contentDescription = "搜索")
}
```

## Chip 组

多个 Chip 请放进 [ChipGroup](./chipgroup):自动换行、8dp 间距,支持 COUI 的单选 / 必选与折叠。

## 组件状态

### 禁用状态

禁用的 Chip 保持其选中/未选中形态,但切换为禁用容器色与内容色,且不再响应按压:

```kotlin
Chip(
    selected = true,
    onClick = { /* Ignored while disabled */ },
    label = "Disabled",
    enabled = false,
)
```

## 属性

### Chip

| 属性              | 类型                       | 说明                             | 默认值                    | 必需 |
| ----------------- | -------------------------- | -------------------------------- | ------------------------- | ---- |
| selected          | Boolean                    | 是否选中                         | -                         | 是   |
| onClick           | () -> Unit                 | 点击回调                         | -                         | 是   |
| label             | String                     | 文本标签(单行,超长省略)      | -                         | 是   |
| modifier          | Modifier                   | 应用于 Chip 的修饰符             | Modifier                  | 否   |
| enabled           | Boolean                    | 是否启用                         | true                      | 否   |
| showRedDot        | Boolean                    | 是否绘制提示红点                 | false                     | 否   |
| icon              | (@Composable () -> Unit)?  | 可选前置图标                     | null                      | 否   |
| onClose           | (() -> Unit)?              | 显示关闭图标并处理其点击         | null                      | 否   |
| count             | String?                    | 标签后的计数文本                 | null                      | 否   |
| cornerRadius      | Dp                         | 胶囊圆角                         | ChipDefaults.CornerRadius | 否   |
| minWidth          | Dp                         | 最小宽度                         | ChipDefaults.MinWidth     | 否   |
| minHeight         | Dp                         | 最小高度                         | ChipDefaults.MinHeight    | 否   |
| maxWidth          | Dp                         | 最大宽度                         | ChipDefaults.MaxWidth     | 否   |
| colors            | ChipColors                 | 颜色配置                         | ChipDefaults.chipColors() | 否   |
| insideMargin      | PaddingValues              | Chip 内部横向内边距              | ChipDefaults.InsideMargin | 否   |
| interactionSource | MutableInteractionSource?  | 交互源                           | null                      | 否   |
| indication        | Indication?                | 指示效果(按压反馈已内置)      | null                      | 否   |

### SuggestionChip

参数与 `Chip` 相同但没有 `selected`;默认 `cornerRadius = ChipDefaults.SuggestionCornerRadius`、`minHeight = ChipDefaults.SuggestionMinHeight`,标签样式为 `suggestionTextStyle()`。

### IconChip

| 属性              | 类型                      | 说明          | 默认值                    | 必需 |
| ----------------- | ------------------------- | ------------- | ------------------------- | ---- |
| onClick           | () -> Unit                | 点击回调      | -                         | 是   |
| modifier          | Modifier                  | 修饰符        | Modifier                  | 否   |
| enabled           | Boolean                   | 是否启用      | true                      | 否   |
| colors            | ChipColors                | 颜色配置      | ChipDefaults.chipColors() | 否   |
| interactionSource | MutableInteractionSource? | 交互源        | null                      | 否   |
| icon              | @Composable () -> Unit    | 16dp 图标     | -                         | 是   |

### ChipDefaults

| 常量         | 类型          | 默认值                            | COUI 来源                                        |
| ------------ | ------------- | --------------------------------- | ------------------------------------------------ |
| MinWidth     | Dp            | 52.dp                             | coui_chip_default_min_width                      |
| MinHeight    | Dp            | 32.dp                             | coui_chip_selection_style_height                 |
| MaxWidth     | Dp            | 300.dp                            | coui_chip_default_max_width                      |
| TextMaxWidth | Dp            | 200.dp                            | coui_chip_default_max_text_width                 |
| CornerRadius | Dp            | 16.dp                             | chipCornerRadius -1 → 胶囊(高度 / 2)，按权重 1.0 软胶囊绘制；更小的半径按 `default_g2_weight` 2.45 绘制 |
| IconSize     | Dp            | 16.dp                             | coui_chip_selection_style_chip_icon_size         |
| IconSpacing  | Dp            | 4.dp                              | coui_chip_selection_style_chip_icon_end_padding  |
| InsideMargin | PaddingValues | PaddingValues(horizontal = 12.dp) | coui_chip_selection_style_chip_horizontal_padding |
| SuggestionMinHeight | Dp     | 28.dp                             | coui_chip_suggest_style_height                   |
| SuggestionCornerRadius | Dp  | 14.dp                             | 28dp 高度的胶囊                                  |
| SuggestionIconSize | Dp      | 14.dp                             | coui_chip_suggest_style_chip_icon_size           |
| CloseIconSize | Dp           | 12.dp                             | coui_chip_suggest_style_close_icon_size          |
| CloseIconStartPadding | Dp   | 8.dp                              | coui_chip_suggest_style_close_icon_start_padding |
| CloseIconEndPadding | Dp     | 8.dp                              | coui_chip_suggest_style_close_icon_end_padding   |
| CountSpacing | Dp            | 2.dp                              | COUIChipDrawable mCountMarginStart               |
| CollapsableSize | Dp         | 28.dp                             | coui_chip_group_collapsable_button_size          |
| CollapsableIconSize | Dp     | 16.dp                             | Widget.COUI.Chip.Suggestion.Collapsable chipIconSize |

#### 方法

| 方法名       | 类型       | 说明                                                                    |
| ------------ | ---------- | ----------------------------------------------------------------------- |
| textStyle()  | TextStyle  | 默认标签样式(COUITheme.textStyles.bodyM,COUI couiTextBodyM 14sp)     |
| suggestionTextStyle() | TextStyle | 建议 Chip 标签样式(COUITheme.textStyles.bodyXS,couiTextBodyXS 12sp) |
| chipColors() | ChipColors | 创建 Chip 的颜色配置                                                    |

### `chipColors()` 工厂

| 参数                           | 类型  | 默认值                                             | COUI 属性                        |
| ------------------------------ | ----- | -------------------------------------------------- | -------------------------------- |
| containerColor                 | Color | COUITheme.colorScheme.container8                  | uncheckedBackgroundColor         |
| selectedContainerColor         | Color | COUITheme.colorScheme.primary                     | checkedBackgroundColor           |
| disabledContainerColor         | Color | COUITheme.colorScheme.container8                  | uncheckedDisabledBackgroundColor |
| selectedDisabledContainerColor | Color | COUITheme.colorScheme.disable       | checkedDisabledBackgroundColor   |
| contentColor                   | Color | COUITheme.colorScheme.labelPrimary                   | uncheckedTextColor               |
| selectedContentColor           | Color | COUITheme.colorScheme.onPrimary                   | checkedTextColor                 |
| disabledContentColor           | Color | COUITheme.colorScheme.disabledChipText            | uncheckedDisabledTextColor       |
| selectedDisabledContentColor   | Color | COUITheme.colorScheme.disabledOnPrimaryButton     | checkedDisabledTextColor         |
| countColor                     | Color | COUITheme.colorScheme.labelSecondary          | couiColorLabelSecondary(计数)  |
| closeIconColor                 | Color | COUITheme.colorScheme.labelSecondary          | `ic_chip_close` couiColorSecondNeutral |

## 行为

- 选中/取消选中时,胶囊、标签与图标颜色在临界阻尼弹簧上交叉渐变(COUIChipDrawable.TintAnimation,response 0.3s / bounce 0)。
- 启用/禁用切换时颜色立即变化,不走弹簧(COUIChipDrawable 会取消着色动画)。
- 按压缩放对齐 COUI 17 的 COUIPressFeedbackHelper:按尺寸计算的 COUIPressAnimatorUnit 按压(52 x 32dp 约 0.89,100 x 32dp 约 0.91),松手时走欠阻尼弹簧回弹,无橡皮筋与形变。填充叠加 `press` 按压色(COUIMaskEffectDrawable);快速点按也会明显闪现按压色。悬停时叠加 `hover` 色。
- 键盘焦点时在胶囊外侧绘制 3dp 的 `focusOutline` 描边(COUIStrokeDrawable,`default_focus_stroke_radius`),以 COUI response 0.3 弹簧淡入淡出。
- 默认颜色对齐 COUI 17:未选中胶囊无论是否启用均为 couiColorContainer8(#14000000 / #1AFFFFFF);禁用标签色为 #42000000 / #29FFFFFF(未选中)与 #8AFFFFFF / #29FFFFFF(选中)。
- 标签单行显示,超过 `TextMaxWidth` 省略;整个 Chip 宽度被钳制在 `MinWidth` 与 `MaxWidth` 之间。
