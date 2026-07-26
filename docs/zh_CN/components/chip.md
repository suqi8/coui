# Chip

胶囊形可选中筛选标签,对应 ColorOS 的 COUIChip 选择样式(Widget.COUI.Chip.Choice)。未选中时是半透明灰色胶囊配主文本色标签;选中后胶囊填充强调色、标签变为白色。两种状态间用 COUI 选中弹簧交叉渐变,按压时缩小并叠加按压色(COUIPressFeedbackHelper)。

## 引入

```kotlin
import com.suqi8.coui.kmp.basic.Chip
import com.suqi8.coui.kmp.basic.ChipDefaults
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

## 单选组

```kotlin
var selectedIndex by remember { mutableIntStateOf(0) }

Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    listOf("全部", "照片", "视频").forEachIndexed { index, label ->
        Chip(
            selected = selectedIndex == index,
            onClick = { selectedIndex = index },
            label = label,
        )
    }
}
```

8dp 间距对应 COUIChipGroup 的默认横/纵间距。

## 属性

### Chip

| 属性              | 类型                       | 说明                             | 默认值                    | 必需 |
| ----------------- | -------------------------- | -------------------------------- | ------------------------- | ---- |
| selected          | Boolean                    | 是否选中                         | -                         | 是   |
| onClick           | () -> Unit                 | 点击回调                         | -                         | 是   |
| label             | String                     | 文本标签(单行,超长省略)      | -                         | 是   |
| modifier          | Modifier                   | 应用于 Chip 的修饰符             | Modifier                  | 否   |
| enabled           | Boolean                    | 是否启用                         | true                      | 否   |
| icon              | (@Composable () -> Unit)?  | 可选前置图标                     | null                      | 否   |
| cornerRadius      | Dp                         | 胶囊圆角                         | ChipDefaults.CornerRadius | 否   |
| minWidth          | Dp                         | 最小宽度                         | ChipDefaults.MinWidth     | 否   |
| minHeight         | Dp                         | 最小高度                         | ChipDefaults.MinHeight    | 否   |
| maxWidth          | Dp                         | 最大宽度                         | ChipDefaults.MaxWidth     | 否   |
| colors            | ChipColors                 | 颜色配置                         | ChipDefaults.chipColors() | 否   |
| insideMargin      | PaddingValues              | Chip 内部横向内边距              | ChipDefaults.InsideMargin | 否   |
| interactionSource | MutableInteractionSource?  | 交互源                           | null                      | 否   |
| indication        | Indication?                | 指示效果(按压反馈已内置)      | null                      | 否   |

### ChipDefaults

| 常量         | 类型          | 默认值                            | COUI 来源                                        |
| ------------ | ------------- | --------------------------------- | ------------------------------------------------ |
| MinWidth     | Dp            | 52.dp                             | coui_chip_default_min_width                      |
| MinHeight    | Dp            | 32.dp                             | coui_chip_selection_style_height                 |
| MaxWidth     | Dp            | 300.dp                            | coui_chip_default_max_width                      |
| TextMaxWidth | Dp            | 200.dp                            | coui_chip_default_max_text_width                 |
| CornerRadius | Dp            | 16.dp                             | chipCornerRadius -1 → 胶囊(高度 / 2)          |
| IconSize     | Dp            | 16.dp                             | coui_chip_selection_style_chip_icon_size         |
| IconSpacing  | Dp            | 4.dp                              | coui_chip_selection_style_chip_icon_end_padding  |
| InsideMargin | PaddingValues | PaddingValues(horizontal = 12.dp) | coui_chip_selection_style_chip_horizontal_padding |

### `chipColors()` 工厂

| 参数                           | 类型  | 默认值                                             | COUI 属性                        |
| ------------------------------ | ----- | -------------------------------------------------- | -------------------------------- |
| containerColor                 | Color | COUITheme.colorScheme.secondaryVariant            | uncheckedBackgroundColor         |
| selectedContainerColor         | Color | COUITheme.colorScheme.primary                     | checkedBackgroundColor           |
| disabledContainerColor         | Color | COUITheme.colorScheme.disabledSecondaryVariant    | uncheckedDisabledBackgroundColor |
| selectedDisabledContainerColor | Color | COUITheme.colorScheme.disabledPrimaryButton       | checkedDisabledBackgroundColor   |
| contentColor                   | Color | COUITheme.colorScheme.onSurface                   | uncheckedTextColor               |
| selectedContentColor           | Color | COUITheme.colorScheme.onPrimary                   | checkedTextColor                 |
| disabledContentColor           | Color | COUITheme.colorScheme.disabledOnSecondaryVariant  | uncheckedDisabledTextColor       |
| selectedDisabledContentColor   | Color | COUITheme.colorScheme.disabledOnPrimaryButton     | checkedDisabledTextColor         |

## 行为

- 选中/取消选中时,胶囊、标签与图标颜色在临界阻尼弹簧上交叉渐变(COUIChipDrawable.TintAnimation,response 0.3s / bounce 0)。
- 按压时 Chip 向 0.92 倍缩小并叠加按压色(COUIPressFeedbackHelper + COUIMaskEffectDrawable);快速点按也会明显闪现按压色。
- 标签单行显示,超过 `TextMaxWidth` 省略;整个 Chip 宽度被钳制在 `MinWidth` 与 `MaxWidth` 之间。
