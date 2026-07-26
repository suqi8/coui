# FullPageStatement

`FullPageStatement` is a full-page user agreement / privacy statement component in COUI, typically shown on first launch, mirroring ColorOS's COUIFullPageStatement (coui_full_page_statement.xml): a centered title, a scrollable statement body with vertical fading edges, and a bottom area holding a filled primary button plus an optional borderless exit text button.

The statement body takes all the space left between the title and the button area, so the buttons stay pinned to the bottom of the page.

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.FullPageStatement
import io.github.suqi8.coui.kmp.basic.FullPageStatementDefaults
```

## Basic Usage

```kotlin
FullPageStatement(
    title = "User Agreement & Privacy Policy",
    content = "Welcome! Before you continue, please read the statement carefully…",
    primaryButtonText = "Agree",
    onPrimaryButtonClick = { /* Continue */ },
    secondaryButtonText = "Disagree and exit",
    onSecondaryButtonClick = { /* Exit */ },
)
```

The component fills its parent (`fillMaxSize`), so it is normally hosted as a whole page, e.g. directly inside a `Scaffold`.

## Properties

### FullPageStatement Properties

| Property               | Type                     | Description                                          | Default Value                                    | Required |
| ---------------------- | ------------------------ | ---------------------------------------------------- | ------------------------------------------------ | -------- |
| title                  | String                   | Centered title (16sp, medium)                        | -                                                | Yes      |
| content                | String                   | Scrollable statement text (14sp, secondary color)    | -                                                | Yes      |
| primaryButtonText      | String                   | Label of the filled primary button                   | -                                                | Yes      |
| onPrimaryButtonClick   | () -> Unit               | Callback of the primary button                       | -                                                | Yes      |
| modifier               | Modifier                 | Modifier applied to the page                         | Modifier                                         | No       |
| secondaryButtonText    | String?                  | Label of the exit text button; hidden when null      | null                                             | No       |
| onSecondaryButtonClick | (() -> Unit)?            | Callback of the exit text button                     | null                                             | No       |
| primaryButtonWidth     | Dp?                      | Primary button width; null = COUI adaptive buckets   | null                                             | No       |
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
| ScrollFadeLength            | Dp            | 46.dp                                            | coui_full_page_statement_scroll_fade_length          |
| PrimaryButtonMarginTop      | Dp            | 20.dp                                            | coui_full_page_statement_button_margin_top           |
| PrimaryButtonMarginBottom   | Dp            | 16.dp                                            | coui_full_page_statement_button_margin               |
| SecondaryButtonMarginBottom | Dp            | 24.dp                                            | coui_full_page_statement_exit_button_margin_bottom   |
| PrimaryButtonWidthCompact   | Dp            | 174.dp                                           | coui_full_page_statement_button_width (base)         |
| PrimaryButtonWidthMedium    | Dp            | 220.dp                                           | coui_full_page_statement_button_width (values-w300dp) |
| PrimaryButtonWidthExpanded  | Dp            | 280.dp                                           | coui_full_page_statement_button_width (values-w600dp) |
| MediumWidthThreshold        | Dp            | 300.dp                                           | values-w300dp bucket                                 |
| ExpandedWidthThreshold      | Dp            | 600.dp                                           | values-w600dp bucket                                 |

`FullPageStatementDefaults.primaryButtonWidth(availableWidth)` resolves the COUI adaptive button width for a given available width.

### `fullPageStatementColors()` factory

| Parameter            | Type  | Default                                   | COUI role                  |
| -------------------- | ----- | ----------------------------------------- | -------------------------- |
| titleColor           | Color | COUITheme.colorScheme.onBackground       | couiColorPrimaryNeutral    |
| contentColor         | Color | COUITheme.colorScheme.onSurfaceSecondary | couiColorSecondNeutral     |
| secondaryButtonColor | Color | COUITheme.colorScheme.primary            | couiColorPrimaryTextOnPopup |

### FullPageStatementColors Class

| Property Name        | Type  | Description                       |
| -------------------- | ----- | --------------------------------- |
| titleColor           | Color | Color of the title                |
| contentColor         | Color | Color of the statement text       |
| secondaryButtonColor | Color | Color of the exit text button     |

## Behavior

- The statement body scrolls and shows 46dp vertical fading edges that ramp in with the scroll offset, like the COUIMaxHeightScrollView host.
- The primary button label is capped at two lines (COUIFullPageStatement `setMaxLines(2)`), with a fixed COUI bucket width: 174dp, 220dp from 300dp-wide windows and 280dp from 600dp-wide windows.
- The exit button is a bare accent-colored text (16sp, medium) with no fill and no press overlay, matching the COUI `txt_exit` TextView.

## Advanced Usage

### Confirm-Only Statement

Omit `secondaryButtonText` (it defaults to `null`) to hide the exit text button:

```kotlin
FullPageStatement(
    title = "User Agreement",
    content = "Welcome! Before you continue, please read the statement carefully…",
    primaryButtonText = "Got it",
    onPrimaryButtonClick = { /* Continue */ }
)
```

### Fixed Primary Button Width

Pass `primaryButtonWidth` to override the COUI adaptive width buckets:

```kotlin
FullPageStatement(
    title = "User Agreement",
    content = "Welcome! Before you continue, please read the statement carefully…",
    primaryButtonText = "Agree",
    onPrimaryButtonClick = { /* Continue */ },
    primaryButtonWidth = 240.dp
)
```

### Custom Colors

```kotlin
FullPageStatement(
    title = "User Agreement",
    content = "Welcome! Before you continue, please read the statement carefully…",
    primaryButtonText = "Agree",
    onPrimaryButtonClick = { /* Continue */ },
    secondaryButtonText = "Disagree and exit",
    onSecondaryButtonClick = { /* Exit */ },
    colors = FullPageStatementDefaults.fullPageStatementColors(
        contentColor = COUITheme.colorScheme.onBackground
    ),
    primaryButtonColors = ButtonDefaults.buttonColors()
)
```
