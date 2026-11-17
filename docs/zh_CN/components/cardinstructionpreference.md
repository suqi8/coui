# CardInstructionPreference

`CardInstructionPreference` 是页面顶部带插图轮播的说明卡片，对应 ColorOS 的 `COUICardInstructionPreference`（默认的描述类型）。每页并排展示一张或多张插图（间距 24dp，每张可带说明文字），下方是居中的标题（16sp 中粗，最多两行）和描述。文字区超过 102dp（无标题时 64dp）后可滚动，上下边缘渐隐。多于一页时，轮播下方 24dp 处显示 [PageIndicator](./pageindicator) 圆点指示器，点击圆点可跳转到对应页。

<div style="position: relative; height: 380px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=cardInstructionPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.suqi8.coui.kmp.preference.CardInstructionIllustration
import io.github.suqi8.coui.kmp.preference.CardInstructionPage
import io.github.suqi8.coui.kmp.preference.CardInstructionPreference
import io.github.suqi8.coui.kmp.preference.CardInstructionPreferenceDefaults
```

## 基本用法

```kotlin
val pages = remember {
    listOf(
        CardInstructionPage(
            illustrations = listOf(
                CardInstructionIllustration(content = { Image(painterResource(Res.drawable.gesture_home), null) })
            ),
            title = "Swipe up from the bottom",
            summary = "Swipe up from the bottom edge of the screen to go home."
        ),
        CardInstructionPage(
            illustrations = listOf(
                CardInstructionIllustration(content = { /* left edge art */ }, title = "Left edge"),
                CardInstructionIllustration(content = { /* right edge art */ }, title = "Right edge")
            ),
            title = "Swipe in from either side",
            summary = "Swipe in from the left or right edge to go back."
        )
    )
}

CardInstructionPreference(
    pages = pages,
    modifier = Modifier.padding(horizontal = 16.dp)
)
```

传入自己的 `pagerState`（`rememberPagerState { pages.size }`）即可读取或控制当前页。

## 属性

### CardInstructionPreference 属性

| 属性名       | 类型                             | 说明               | 默认值                                                         | 是否必须 |
| ------------ | -------------------------------- | ------------------ | -------------------------------------------------------------- | -------- |
| pages        | List&lt;CardInstructionPage&gt;  | 卡片的各页         | -                                                              | 是       |
| modifier     | Modifier                         | 组件修饰符         | Modifier                                                       | 否       |
| pagerState   | PagerState                       | 插图轮播状态       | rememberPagerState { pages.size }                              | 否       |
| cornerRadius | Dp                               | 卡片圆角           | CardInstructionPreferenceDefaults.CornerRadius                 | 否       |
| colors       | CardInstructionPreferenceColors  | 卡片颜色           | CardInstructionPreferenceDefaults.cardInstructionPreferenceColors() | 否  |
| insideMargin | PaddingValues                    | 卡片内边距         | CardInstructionPreferenceDefaults.InsideMargin                 | 否       |

### CardInstructionPage 属性

| 属性名        | 类型                                     | 说明             | 默认值      |
| ------------- | ---------------------------------------- | ---------------- | ----------- |
| illustrations | List&lt;CardInstructionIllustration&gt;  | 并排展示的插图   | emptyList() |
| title         | String?                                  | 页标题           | null        |
| summary       | String?                                  | 页描述           | null        |

### CardInstructionIllustration 属性

| 属性名  | 类型                   | 说明                       | 默认值 |
| ------- | ---------------------- | -------------------------- | ------ |
| content | @Composable () -> Unit | 插图本身（图片 / 动画）    | -      |
| title   | String?                | 插图下方的说明文字         | null   |

### CardInstructionPreferenceDefaults 对象

| 常量                         | 类型          | 默认值                                              | 说明                                                    |
| ---------------------------- | ------------- | --------------------------------------------------- | ------------------------------------------------------- |
| CornerRadius                 | Dp            | 16.dp                                               | coui_component_card_instruction_bg                      |
| InsideMargin                 | PaddingValues | PaddingValues(horizontal = 16.dp, vertical = 24.dp) | 卡片内边距                                              |
| IllustrationSpacing          | Dp            | 24.dp                                               | coui_component_card_instruction_divider_width           |
| IllustrationTitleTopMargin   | Dp            | 24.dp                                               | 插图说明上边距                                          |
| ContentTopMargin             | Dp            | 24.dp                                               | 文字区上边距                                            |
| ContentMaxHeightWithTitle    | Dp            | 102.dp                                              | coui_component_card_instruction_content_height_complete |
| ContentMaxHeightWithoutTitle | Dp            | 64.dp                                               | coui_component_card_instruction_content_height_part     |
| ContentFadingEdgeLength      | Dp            | 40.dp                                               | coui_component_card_instruction_content_fading_edge_length |
| SummaryTopMarginWithTitle    | Dp            | 12.dp                                               | …_summary_margin_top_small                              |
| SummaryTopMarginWithoutTitle | Dp            | 24.dp                                               | …_summary_margin_top_large                              |
| IndicatorTopMargin           | Dp            | 24.dp                                               | 指示器上边距                                            |

#### `cardInstructionPreferenceColors()` 工厂方法

| 参数             | 类型  | 默认值                                           |
| ---------------- | ----- | ------------------------------------------------ |
| containerColor   | Color | COUITheme.colorScheme.cardBackground           |
| titleColor       | Color | COUITheme.colorScheme.labelPrimary                  |
| summaryColor     | Color | COUITheme.colorScheme.labelSecondary         |
| dotColor         | Color | COUITheme.colorScheme.labelTertiary     |
| selectedDotColor | Color | COUITheme.colorScheme.labelPrimary                  |
