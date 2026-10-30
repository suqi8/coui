# CardEntrancePreference

`CardEntrancePreference` 是宫格式的入口卡片组，对应 ColorOS 中按两列网格排布的一组 `COUICardEntrancePreference`。小卡片（`CardEntranceType.Small`）占半行，图标在标题和摘要上方；大卡片（`CardEntranceType.Large`）占满一行，图标位于文字左侧。每张卡片都是独立的圆角块（小卡片 12dp / 大卡片 16dp），按下时缩小。同一行的小卡片等高；两列间距 8dp，小卡片行与上一行间距 8dp，大卡片为 16dp。

<div style="position: relative; height: 330px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=cardEntrancePreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.preference.CardEntranceItem
import io.github.suqi8.coui.kmp.preference.CardEntrancePreference
import io.github.suqi8.coui.kmp.preference.CardEntranceType
```

## 基本用法

```kotlin
val items = remember {
    listOf(
        CardEntranceItem(title = "Privacy", summary = "Permissions", icon = { PrivacyIcon() }, onClick = { /* open */ }),
        CardEntranceItem(title = "Location", summary = "On", statusOn = true, icon = { LocationIcon() }, onClick = { /* open */ }),
        CardEntranceItem(
            title = "Backup & restore",
            summary = "Last backup: yesterday",
            type = CardEntranceType.Large,
            icon = { BackupIcon() },
            onClick = { /* open */ }
        )
    )
}

CardEntrancePreference(
    items = items,
    modifier = Modifier.padding(horizontal = 16.dp)
)
```

`statusOn` 会用强调色绘制摘要，表示“已开启”之类的状态。大卡片前单独剩下的小卡片会占半行，另一半留空。

## 属性

### CardEntrancePreference 属性

| 属性名   | 类型                          | 说明             | 默认值                                                      | 是否必须 |
| -------- | ----------------------------- | ---------------- | ----------------------------------------------------------- | -------- |
| items    | List&lt;CardEntranceItem&gt;  | 按顺序排列的卡片 | -                                                           | 是       |
| modifier | Modifier                      | 组件修饰符       | Modifier                                                    | 否       |
| colors   | CardEntrancePreferenceColors  | 卡片颜色         | CardEntrancePreferenceDefaults.cardEntrancePreferenceColors() | 否     |

### CardEntranceItem 属性

| 属性名   | 类型                       | 说明                               | 默认值                 |
| -------- | -------------------------- | ---------------------------------- | ---------------------- |
| title    | String                     | 卡片标题                           | -                      |
| summary  | String?                    | 标题下方的摘要                     | null                   |
| type     | CardEntranceType           | Small（半行）或 Large（整行）      | CardEntranceType.Small |
| icon     | @Composable (() -> Unit)?  | 卡片图标                           | null                   |
| statusOn | Boolean                    | 用强调色绘制摘要                   | false                  |
| enabled  | Boolean                    | 卡片是否可点击                     | true                   |
| onClick  | (() -> Unit)?              | 点击回调；为 null 时卡片不可交互   | null                   |

### CardEntrancePreferenceDefaults 对象

| 常量                  | 类型          | 默认值                                                         | 说明                                   |
| --------------------- | ------------- | -------------------------------------------------------------- | -------------------------------------- |
| SmallCornerRadius     | Dp            | 12.dp                                                          | coui_component_card_entrance_small_bg  |
| LargeCornerRadius     | Dp            | 16.dp                                                          | coui_component_card_entrance_large_bg  |
| SmallInsideMargin     | PaddingValues | PaddingValues(16.dp)                                           | 小卡片内边距                           |
| LargeInsideMargin     | PaddingValues | PaddingValues(start = 16.dp, end = 12.dp, top = 20.dp, bottom = 20.dp) | 大卡片内边距                   |
| SmallTitleTopMargin   | Dp            | 12.dp                                                          | 图标到标题（小卡片）                   |
| SmallSummaryTopMargin | Dp            | 4.dp                                                           | 标题到摘要（小卡片）                   |
| LargeIconSpacing      | Dp            | 16.dp                                                          | 图标到文字（大卡片）                   |
| LargeSummaryTopMargin | Dp            | 3.dp                                                           | 标题到摘要（大卡片）                   |
| ColumnSpacing         | Dp            | 8.dp                                                           | 2 x …_small_horizontal_margin_inner    |
| SmallRowSpacing       | Dp            | 8.dp                                                           | …_small_top_margin_other               |
| LargeRowSpacing       | Dp            | 16.dp                                                          | …_large_top_margin                     |

#### `cardEntrancePreferenceColors()` 工厂方法

| 参数                 | 类型  | 默认值                                             |
| -------------------- | ----- | -------------------------------------------------- |
| containerColor       | Color | COUITheme.colorScheme.surfaceContainer             |
| titleColor           | Color | COUITheme.colorScheme.onSurface                    |
| summaryColor         | Color | COUITheme.colorScheme.onSurfaceSecondary           |
| statusOnSummaryColor | Color | COUITheme.colorScheme.primary                      |
| disabledColor        | Color | COUITheme.colorScheme.disabledOnSecondaryVariant   |
