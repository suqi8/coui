# RecommendedPreference

`RecommendedPreference` 是独立的相关设置推荐卡片，对应 ColorOS 的 `COUIRecommendedPreference`（设置页底部的「你可能想找：」卡片）。卡片为 12dp 圆角容器（COUI `couiRoundCornerM`），填充 `couiColorContainer4`，内含一个次要色小标题和逐条可点击的文字行。行按下时显示标准 COUI 按压遮罩。

当 `items` 为空时不渲染任何内容，与 `COUIRecommendedPreference.setData()` 在列表为空时隐藏该偏好的行为一致。

## 引入

```kotlin
import io.github.suqi8.coui.kmp.preference.RecommendedPreference
import io.github.suqi8.coui.kmp.preference.RecommendedItem
import io.github.suqi8.coui.kmp.preference.RecommendedPreferenceDefaults
```

## 基本用法

在 `remember` 块中构建条目列表以保持其在重组间稳定。像其他卡片块一样放置该卡片，例如加 16dp 水平外边距：

```kotlin
val items = remember {
    listOf(
        RecommendedItem(text = "显示与亮度", onClick = { /* 跳转 */ }),
        RecommendedItem(text = "壁纸与个性化", onClick = { /* 跳转 */ }),
        RecommendedItem(text = "电池", onClick = { /* 跳转 */ })
    )
}

RecommendedPreference(
    title = "你可能想找：",
    items = items,
    modifier = Modifier.padding(horizontal = 16.dp)
)
```

## 属性

### RecommendedPreference 属性

| 属性名       | 类型                        | 说明                             | 默认值                                                      | 是否必须 |
| ------------ | --------------------------- | -------------------------------- | ----------------------------------------------------------- | -------- |
| title        | String                      | 卡片标题文本                     | -                                                           | 是       |
| items        | List\<RecommendedItem\>     | 推荐条目；为空时不渲染           | -                                                           | 是       |
| modifier     | Modifier                    | 应用于组件的修饰符               | Modifier                                                    | 否       |
| cornerRadius | Dp                          | 卡片圆角半径                     | RecommendedPreferenceDefaults.CornerRadius                  | 否       |
| colors       | RecommendedPreferenceColors | 卡片颜色配置                     | RecommendedPreferenceDefaults.recommendedPreferenceColors() | 否       |
| insideMargin | PaddingValues               | 卡片边缘与文字的水平间距         | RecommendedPreferenceDefaults.InsideMargin                  | 否       |

### RecommendedItem 属性

| 属性名  | 类型          | 说明                                     | 默认值 | 是否必须 |
| ------- | ------------- | ---------------------------------------- | ------ | -------- |
| text    | String        | 行文本                                   | -      | 是       |
| onClick | (() -> Unit)? | 点击该行时的回调；null 时该行不可点击    | null   | 否       |

### RecommendedPreferenceDefaults 对象

| 常量                | 类型          | 默认值                            | 说明                                                         |
| ------------------- | ------------- | --------------------------------- | ------------------------------------------------------------ |
| CornerRadius        | Dp            | 12.dp                             | COUI couiRoundCornerM                                        |
| InsideMargin        | PaddingValues | PaddingValues(horizontal = 16.dp) | 卡内文字缩进（COUI 32dp 减去 16dp 卡片外边距）               |
| HeaderTopPadding    | Dp            | 16.dp                             | COUI recommended_recyclerView_padding_top                    |
| HeaderBottomSpacing | Dp            | 8.dp                              | COUI recommended_preference_list_item_head_bottom_margin     |
| HeaderMinHeight     | Dp            | 20.dp                             | COUI recommended_preference_list_item_height_head            |
| ItemVerticalPadding | Dp            | 10.dp                             | COUI recommended_preference_list_item_common_margin_vertical |
| BottomPadding       | Dp            | 8.dp                              | COUI recommended_recyclerView_padding_bottom                 |

#### `recommendedPreferenceColors()` 工厂方法

| 参数           | 类型  | 默认值                                                              |
| -------------- | ----- | ------------------------------------------------------------------- |
| containerColor | Color | couiColorContainer4（浅色 #0A000000 / 深色 #14FFFFFF，随主题自动切换） |
| titleColor     | Color | COUITheme.colorScheme.onSurfaceSecondary                           |
| itemColor      | Color | COUITheme.colorScheme.onSurface                                    |

## 进阶用法

### 自定义颜色

```kotlin
RecommendedPreference(
    title = "你可能想找：",
    items = items,
    colors = RecommendedPreferenceDefaults.recommendedPreferenceColors(
        containerColor = COUITheme.colorScheme.surfaceContainer,
        itemColor = COUITheme.colorScheme.primary
    )
)
```

### 置于设置页底部

```kotlin
LazyColumn {
    // ... 其他偏好设置卡片 ...
    item {
        RecommendedPreference(
            title = "你可能想找：",
            items = items,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        )
    }
}
```
