# FullPageStatement

`FullPageStatement` is a full-page user agreement / privacy statement component in COUI, typically shown on first launch, mirroring ColorOS's COUIFullPageStatement (coui_full_page_statement.xml): a centered title, a scrollable statement body with vertical fading edges, and a bottom area holding a filled primary button plus an optional borderless exit text button.

Like the COUI layout, the statement body grows with its text up to `statementMaxHeight` (248dp) and the buttons follow right below it; when the page is taller, the remaining space stays empty at the bottom. On small landscape windows (smallest width < 480dp) the exit and confirm actions sit side by side as two 44dp buttons.

<div style="position: relative; height: 540px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=fullPageStatement" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.FullPageStatement
import io.github.suqi8.coui.kmp.basic.FullPageStatementDefaults
```

## Basic Usage

```kotlin
FullPageStatement(
    title = "User Agreement & Privacy Policy",
    content = AnnotatedString("Welcome! Before you continue, please read the statement carefully…"),
    primaryButtonText = "Agree",
    onPrimaryButtonClick = { /* Continue */ },
    modifier = Modifier.fillMaxSize(),
    secondaryButtonText = "Disagree and exit",
    onSecondaryButtonClick = { /* Exit */ },
)
```

The component takes the size its `modifier` gives it; pass `Modifier.fillMaxSize()` to host it as a whole page.

## Agreement Links

The statement text is an `AnnotatedString`, so agreement links are `LinkAnnotation`s. `FullPageStatementDefaults.linkStyles()` gives them the COUI look (`couiColorLink`, alpha 77/255 while pressed, no underline):

```kotlin
val linkStyles = FullPageStatementDefaults.linkStyles()
val content = buildAnnotatedString {
    append("Please read the ")
    withLink(LinkAnnotation.Clickable("agreement", linkStyles) { openAgreement() }) {
        append("User Agreement")
    }
    append(" before you continue.")
}
```

## Properties

### FullPageStatement Properties

| Property               | Type                     | Description                                          | Default Value                                    | Required |
| ---------------------- | ------------------------ | ---------------------------------------------------- | ------------------------------------------------ | -------- |
| title                  | String                   | Centered title (16sp, medium); scrolls past 80 / 120 / 160dp | -                                        | Yes      |
| content                | AnnotatedString          | Scrollable statement text (13sp, secondary color), may carry links | -                                  | Yes      |
| primaryButtonText      | String                   | Label of the filled primary button                   | -                                                | Yes      |
| onPrimaryButtonClick   | () -> Unit               | Callback of the primary button                       | -                                                | Yes      |
| modifier               | Modifier                 | Modifier applied to the page                         | Modifier                                         | No       |
| secondaryButtonText    | String?                  | Label of the exit text button; hidden when null      | null                                             | No       |
| onSecondaryButtonClick | (() -> Unit)?            | Callback of the exit text button                     | null                                             | No       |
| primaryButtonWidth     | Dp?                      | Primary button width; null = COUI adaptive buckets   | null                                             | No       |
| statementMaxHeight     | Dp                       | Maximum height of the statement body                 | FullPageStatementDefaults.StatementMaxHeight     | No       |
| colors                 | FullPageStatementColors  | Text color configuration                             | FullPageStatementDefaults.fullPageStatementColors() | No    |
| primaryButtonColors    | ButtonColors             | Colors of the filled primary button                  | ButtonDefaults.buttonColorsPrimary()             | No       |
| contentPadding         | PaddingValues            | Padding around the statement text                    | FullPageStatementDefaults.ContentPadding         | No       |
| scrollState            | ScrollState              | Scroll state of the statement area                   | rememberScrollState()                            | No       |

### FullPageStatementDefaults Object

| Constant                    | Type          | Default Value                                    | COUI source                                          |
| --------------------------- | ------------- | ------------------------------------------------ | ---------------------------------------------------- |
| TitleMarginTop              | Dp            | 12.dp                                            | coui_full_page_statement_text_button_padding         |
| TitleMarginBottom           | Dp            | 12.dp                                            | coui_full_page_statement_content_margin              |
| TitleMarginHorizontal       | Dp            | 24.dp                                            | coui_full_page_statement_text_button_padding_horizontal |
| ContentPadding              | PaddingValues | start 24.dp, top 12.dp, end 24.dp, bottom 14.dp  | coui_full_page_statement_padding_*                   |
| ScrollFadeLength            | Dp            | 32.dp                                            | coui_full_page_statement_scroll_fade_length          |
| StatementMaxHeight          | Dp            | 248.dp                                           | coui_full_page_statement_scroll_max_height           |
| TitleMarginShort            | Dp            | 3.dp                                             | values-h200dp title margins                          |
| ShortWindowHeight           | Dp            | 300.dp                                           | values-h300dp bucket                                 |
| PrimaryButtonMarginTop      | Dp            | 20.dp                                            | coui_full_page_statement_button_margin_top           |
| PrimaryButtonMarginBottom   | Dp            | 16.dp                                            | coui_full_page_statement_button_margin               |
| PrimaryButtonMarginTopShort | Dp            | 10.dp                                            | values-h200dp button_margin_top                      |
| PrimaryButtonMarginBottomShort | Dp         | 8.dp                                             | values-h200dp button_margin                          |
| SmallLandSmallestWidth      | Dp            | 480.dp                                           | COUIFullPageStatement.isSmallScreen                  |
| LandButtonHeight            | Dp            | 44.dp                                            | coui_full_page_statement_button_button_width_landlong |
| LandButtonMarginHorizontal  | Dp            | 37.dp                                            | coui_full_page_statement_button_margin_horizontal_landlong |
| LandButtonSpacing           | Dp            | 8.dp                                             | 2 × coui_full_page_statement_button_margin_center_landlong |
| LandButtonMarginTop         | Dp            | 16.dp                                            | coui_full_page_statement_button_margin_top_landlong  |
| LandButtonMarginBottom      | Dp            | 24.dp                                            | coui_full_page_statement_button_margin_bottom_landlong |
| SecondaryButtonMarginBottom | Dp            | 24.dp                                            | coui_full_page_statement_exit_button_margin_bottom   |
| PrimaryButtonWidthCompact   | Dp            | 174.dp                                           | coui_full_page_statement_button_width (base)         |
| PrimaryButtonWidthMedium    | Dp            | 220.dp                                           | coui_full_page_statement_button_width (values-w300dp) |
| PrimaryButtonWidthExpanded  | Dp            | 280.dp                                           | coui_full_page_statement_button_width (values-w600dp) |
| MediumWidthThreshold        | Dp            | 300.dp                                           | values-w300dp bucket                                 |
| ExpandedWidthThreshold      | Dp            | 600.dp                                           | values-w600dp bucket                                 |

`FullPageStatementDefaults.primaryButtonWidth(availableWidth)` resolves the COUI adaptive button width for a given available width, `titleMaxHeight(windowHeight)` the title scroller cap (80dp, 120dp from 300dp-tall windows, 160dp from 560dp-tall windows), and `linkStyles(color)` the `TextLinkStyles` of statement links.

### `fullPageStatementColors()` factory

| Parameter            | Type  | Default                                   | COUI role                  |
| -------------------- | ----- | ----------------------------------------- | -------------------------- |
| titleColor           | Color | COUITheme.colorScheme.labelPrimary       | couiColorPrimaryNeutral    |
| contentColor         | Color | COUITheme.colorScheme.labelSecondary | couiColorSecondNeutral     |
| secondaryButtonColor | Color | COUITheme.colorScheme.primary            | couiColorPrimaryTextOnPopup |

### FullPageStatementColors Class

| Property Name        | Type  | Description                       |
| -------------------- | ----- | --------------------------------- |
| titleColor           | Color | Color of the title                |
| contentColor         | Color | Color of the statement text       |
| secondaryButtonColor | Color | Color of the exit text button     |

## Behavior

- The statement body scrolls and shows 32dp vertical fading edges that ramp in with the scroll offset, like the COUIMaxHeightScrollView host.
- Windows shorter than 300dp use the tighter values-h200dp margins (title 3dp, button 10dp above / 8dp below).
- The primary button label is capped at two lines (COUIFullPageStatement `setMaxLines(2)`), with a fixed COUI bucket width: 174dp, 220dp from 300dp-wide windows and 280dp from 600dp-wide windows.
- The exit button is a bare accent-colored text (16sp, medium) with no fill and no press overlay, matching the COUI `txt_exit` TextView.

## Advanced Usage

### Confirm-Only Statement

Omit `secondaryButtonText` (it defaults to `null`) to hide the exit text button:

```kotlin
FullPageStatement(
    title = "User Agreement",
    content = AnnotatedString("Welcome! Before you continue, please read the statement carefully…"),
    primaryButtonText = "Got it",
    onPrimaryButtonClick = { /* Continue */ }
)
```

### Fixed Primary Button Width

Pass `primaryButtonWidth` to override the COUI adaptive width buckets:

```kotlin
FullPageStatement(
    title = "User Agreement",
    content = AnnotatedString("Welcome! Before you continue, please read the statement carefully…"),
    primaryButtonText = "Agree",
    onPrimaryButtonClick = { /* Continue */ },
    primaryButtonWidth = 240.dp
)
```

### Custom Colors

```kotlin
FullPageStatement(
    title = "User Agreement",
    content = AnnotatedString("Welcome! Before you continue, please read the statement carefully…"),
    primaryButtonText = "Agree",
    onPrimaryButtonClick = { /* Continue */ },
    secondaryButtonText = "Disagree and exit",
    onSecondaryButtonClick = { /* Exit */ },
    colors = FullPageStatementDefaults.fullPageStatementColors(
        contentColor = COUITheme.colorScheme.labelPrimary
    ),
    primaryButtonColors = ButtonDefaults.buttonColors()
)
```
