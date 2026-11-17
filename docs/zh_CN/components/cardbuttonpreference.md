# CardButtonPreference

`CardButtonPreference` 是图标在上、单行标题在下的卡片式按钮，对应 ColorOS 的 `COUICardButtonPreference`。每个按钮块是 16dp 圆角卡片（`couiColorCardBackground`），水平内边距 8dp、垂直 12dp，标题位于图标下方 4dp。标题为 12sp，单行放不下时缩为 10sp，仍放不下再省略。按下时按钮块缩小而不是叠加颜色。`CardButtonPreferenceGroup` 按 COUI `CardButtonDecoration` 网格排布：列间距 8dp，行间距 16dp，同一行的按钮块等高。

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=cardButtonPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.preference.CardButtonItem
import io.github.suqi8.coui.kmp.preference.CardButtonPreference
import io.github.suqi8.coui.kmp.preference.CardButtonPreferenceDefaults
import io.github.suqi8.coui.kmp.preference.CardButtonPreferenceGroup
```

## 基本用法

```kotlin
val items = remember {
    listOf(
        CardButtonItem("扫一扫", icon = { Icon(COUIIcons.Scan, null) }, onClick = { /* 扫描 */ }),
        CardButtonItem("相册", icon = { Icon(COUIIcons.Photos, null) }, onClick = { /* 相册 */ }),
        CardButtonItem("音乐", icon = { Icon(COUIIcons.Music, null) }, onClick = { /* 音乐 */ }),
        CardButtonItem("备份", icon = { Icon(COUIIcons.Backup, null) }, onClick = { /* 备份 */ })
    )
}

CardButtonPreferenceGroup(
    items = items,
    modifier = Modifier.padding(horizontal = 16.dp)
)
```

也可以单独放置一个按钮块：

```kotlin
CardButtonPreference(
    title = "扫一扫",
    onClick = { /* 扫描 */ },
    icon = { Icon(COUIIcons.Scan, null) }
)
```

## 属性

### CardButtonPreference 属性

| 属性名       | 类型                       | 说明                             | 默认值                                                   | 必须 |
| ------------ | -------------------------- | -------------------------------- | -------------------------------------------------------- | ---- |
| title        | String                     | 标题                             | -                                                        | 是   |
| onClick      | (() -> Unit)?              | 点击回调；为 null 时不可点击     | -                                                        | 是   |
| modifier     | Modifier                   | 修饰符                           | Modifier                                                 | 否   |
| enabled      | Boolean                    | 是否可点击                       | true                                                     | 否   |
| cornerRadius | Dp                         | 圆角半径                         | CardButtonPreferenceDefaults.CornerRadius                | 否   |
| insideMargin | PaddingValues              | 内边距                           | CardButtonPreferenceDefaults.InsideMargin                | 否   |
| colors       | CardButtonPreferenceColors | 颜色                             | CardButtonPreferenceDefaults.cardButtonPreferenceColors() | 否  |
| icon         | @Composable (() -> Unit)?  | 标题上方的图标                   | null                                                     | 否   |

### CardButtonPreferenceGroup 属性

| 属性名   | 类型                       | 说明               | 默认值                                                   | 必须 |
| -------- | -------------------------- | ------------------ | -------------------------------------------------------- | ---- |
| items    | List\<CardButtonItem>      | 按显示顺序的按钮块 | -                                                        | 是   |
| modifier | Modifier                   | 修饰符             | Modifier                                                 | 否   |
| columns  | Int                        | 列数               | CardButtonPreferenceDefaults.Columns                     | 否   |
| colors   | CardButtonPreferenceColors | 颜色               | CardButtonPreferenceDefaults.cardButtonPreferenceColors() | 否  |

### CardButtonItem

| 字段    | 类型                      | 说明                         | 默认值 |
| ------- | ------------------------- | ---------------------------- | ------ |
| title   | String                    | 标题                         | -      |
| icon    | @Composable (() -> Unit)? | 标题上方的图标               | null   |
| enabled | Boolean                   | 是否可点击                   | true   |
| onClick | (() -> Unit)?             | 点击回调；为 null 时不可点击 | null   |

### CardButtonPreferenceDefaults 对象

| 名称               | 类型          | 默认值                                             | 说明                                                   |
| ------------------ | ------------- | -------------------------------------------------- | ------------------------------------------------------ |
| CornerRadius       | Dp            | 16.dp                                              | coui_component_card_button_bg 圆角                     |
| InsideMargin       | PaddingValues | PaddingValues(horizontal = 8.dp, vertical = 12.dp) | 卡片按钮布局内边距                                     |
| TitleTopMargin     | Dp            | 4.dp                                               | 图标与标题的间距                                       |
| TitleFontSize      | TextUnit      | 12.sp                                              | CARD_BUTTON_TEXT_SIZE_NORMAL                           |
| SmallTitleFontSize | TextUnit      | 10.sp                                              | CARD_BUTTON_TEXT_SIZE_SMALL（12sp 放不下时使用）       |
| ColumnSpacing      | Dp            | 8.dp                                               | 2 x coui_component_card_button_horizontal_margin_inner |
| RowSpacing         | Dp            | 16.dp                                              | coui_component_card_button_first_top_margin            |
| Columns            | Int           | 4                                                  | 默认列数（COUI 交由宿主网格决定）                      |

#### `cardButtonPreferenceColors()` 工厂方法

| 参数               | 类型  | 默认值                                           |
| ------------------ | ----- | ------------------------------------------------ |
| containerColor     | Color | COUITheme.colorScheme.cardBackground           |
| titleColor         | Color | COUITheme.colorScheme.labelPrimary                  |
| disabledTitleColor | Color | COUITheme.colorScheme.disabledNeutral |

### CardButtonPreferenceColors 类

| 属性               | 类型  | 说明                 |
| ------------------ | ----- | -------------------- |
| containerColor     | Color | 按钮块填充色         |
| titleColor         | Color | 标题颜色             |
| disabledTitleColor | Color | 禁用状态的标题颜色   |
