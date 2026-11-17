# CardInstructionPreference

`CardInstructionPreference` is a page-top description card with an illustration pager, mirroring ColorOS's `COUICardInstructionPreference` in its default description form. Each page shows one or more illustrations side by side (24dp apart, each with an optional caption), then a centered title (16sp medium, at most two lines) and a description. The text block scrolls with faded edges once it exceeds 102dp (64dp without a title). With more than one page a [PageIndicator](./pageindicator) appears 24dp below the pager; tapping a dot jumps to that page.

<div style="position: relative; height: 380px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=cardInstructionPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.preference.CardInstructionIllustration
import io.github.suqi8.coui.kmp.preference.CardInstructionPage
import io.github.suqi8.coui.kmp.preference.CardInstructionPreference
import io.github.suqi8.coui.kmp.preference.CardInstructionPreferenceDefaults
```

## Basic Usage

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

Pass your own `pagerState` (`rememberPagerState { pages.size }`) to read or drive the current page.

## Properties

### CardInstructionPreference Properties

| Property Name | Type                             | Description                       | Default Value                                                  | Required |
| ------------- | -------------------------------- | --------------------------------- | -------------------------------------------------------------- | -------- |
| pages         | List&lt;CardInstructionPage&gt;  | Pages of the card                 | -                                                              | Yes      |
| modifier      | Modifier                         | Component modifier                | Modifier                                                       | No       |
| pagerState    | PagerState                       | State of the illustration pager   | rememberPagerState { pages.size }                              | No       |
| cornerRadius  | Dp                               | Card corner radius                | CardInstructionPreferenceDefaults.CornerRadius                 | No       |
| colors        | CardInstructionPreferenceColors  | Card colors                       | CardInstructionPreferenceDefaults.cardInstructionPreferenceColors() | No  |
| insideMargin  | PaddingValues                    | Padding inside the card           | CardInstructionPreferenceDefaults.InsideMargin                 | No       |

### CardInstructionPage Properties

| Property Name | Type                                     | Description                      | Default Value |
| ------------- | ---------------------------------------- | -------------------------------- | ------------- |
| illustrations | List&lt;CardInstructionIllustration&gt;  | Illustrations shown side by side | emptyList()   |
| title         | String?                                  | Page title                       | null          |
| summary       | String?                                  | Page description                 | null          |

### CardInstructionIllustration Properties

| Property Name | Type                   | Description                     | Default Value |
| ------------- | ---------------------- | ------------------------------- | ------------- |
| content       | @Composable () -> Unit | The illustration (image / animation) | -        |
| title         | String?                | Caption below the illustration  | null          |

### CardInstructionPreferenceDefaults Object

| Constant                     | Type          | Default Value                                     | Description                                             |
| ---------------------------- | ------------- | ------------------------------------------------- | ------------------------------------------------------- |
| CornerRadius                 | Dp            | 16.dp                                             | coui_component_card_instruction_bg                      |
| InsideMargin                 | PaddingValues | PaddingValues(horizontal = 16.dp, vertical = 24.dp) | Card padding                                          |
| IllustrationSpacing          | Dp            | 24.dp                                             | coui_component_card_instruction_divider_width           |
| IllustrationTitleTopMargin   | Dp            | 24.dp                                             | Caption top margin                                      |
| ContentTopMargin             | Dp            | 24.dp                                             | Gap above the text block                                |
| ContentMaxHeightWithTitle    | Dp            | 102.dp                                            | coui_component_card_instruction_content_height_complete |
| ContentMaxHeightWithoutTitle | Dp            | 64.dp                                             | coui_component_card_instruction_content_height_part     |
| ContentFadingEdgeLength      | Dp            | 40.dp                                             | coui_component_card_instruction_content_fading_edge_length |
| SummaryTopMarginWithTitle    | Dp            | 12.dp                                             | …_summary_margin_top_small                              |
| SummaryTopMarginWithoutTitle | Dp            | 24.dp                                             | …_summary_margin_top_large                              |
| IndicatorTopMargin           | Dp            | 24.dp                                             | Gap above the page indicator                            |

#### `cardInstructionPreferenceColors()` factory

| Parameter        | Type  | Default                                          |
| ---------------- | ----- | ------------------------------------------------ |
| containerColor   | Color | COUITheme.colorScheme.cardBackground           |
| titleColor       | Color | COUITheme.colorScheme.labelPrimary                  |
| summaryColor     | Color | COUITheme.colorScheme.labelSecondary         |
| dotColor         | Color | COUITheme.colorScheme.labelTertiary     |
| selectedDotColor | Color | COUITheme.colorScheme.labelPrimary                  |
