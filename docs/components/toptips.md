# TopTips

A tip banner shown at the top of a page, mirroring COUI 17 `COUIDefaultTopTips` (coui_default_toptips.xml): a rounded card holding an optional start icon, an optional 16sp medium title, a 13sp message, an optional solid primary action button and an optional close button — the action and close buttons can be shown together. The message can also scroll as a single-line marquee. Toggling visibility plays the COUI 17 expand / collapse animation (a 250ms height clip with a content fade).

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.TopTips
import io.github.suqi8.coui.kmp.basic.TopTipsDefaults
```

## Basic Usage

```kotlin
TopTips(
    text = "New security update available",
    actionText = "Update",
    onAction = { /* handle action */ },
    modifier = Modifier.fillMaxWidth(),
)
```

## Closable Tips with a Start Icon

```kotlin
var visible by remember { mutableStateOf(true) }

TopTips(
    text = "Closable tips with a start icon",
    visible = visible,
    startIcon = {
        Icon(
            imageVector = COUIIcons.Basic.Search,
            contentDescription = null,
        )
    },
    onClose = { visible = false },
    modifier = Modifier.fillMaxWidth(),
)
```

## Title, Action and Close Together

```kotlin
TopTips(
    title = "Backup paused",
    text = "Connect to Wi-Fi to continue backing up your photos",
    startIcon = { Icon(imageVector = COUIIcons.Basic.Search, contentDescription = null) },
    actionText = "Retry",
    onAction = { /* handle action */ },
    onClose = { /* hide the tips */ },
    modifier = Modifier.fillMaxWidth(),
)
```

## Marquee

```kotlin
TopTips(
    text = "A message that is too long to fit on a single line keeps scrolling",
    marquee = true,
    modifier = Modifier.fillMaxWidth(),
)
```

## Properties

### TopTips

| Property     | Type                       | Description                                            | Default Value                   | Required |
| ------------ | -------------------------- | ------------------------------------------------------ | ------------------------------- | -------- |
| text         | String                     | The message of the banner                              | -                               | Yes      |
| modifier     | Modifier                   | Modifier applied to the banner                         | Modifier                        | No       |
| visible      | Boolean                    | Whether the banner is visible (animated in/out)        | true                            | No       |
| title        | String?                    | Optional title above the message (16sp medium, at most 2 lines) | null                   | No       |
| startIcon    | (@Composable () -> Unit)?  | Optional leading icon; its box (24dp, 36dp with a title) imposes its size on the icon | null | No |
| actionText   | String?                    | Optional label of the action button                    | null                            | No       |
| onAction     | (() -> Unit)?              | Callback when the action button is clicked             | null                            | No       |
| onClose      | (() -> Unit)?              | Callback of the close button; the button shows when not null | null                      | No       |
| marquee      | Boolean                    | Scroll the message as a single line (`COUIMarqueeTopTips.startRoll`) | false             | No       |
| cornerRadius | Dp                         | Corner radius of the banner; unspecified = COUI 17 automatic (a title-less single-line banner is a capsule at weight 1.0, otherwise 28dp at weight 3.0, animated over 300ms) | Dp.Unspecified | No |
| colors       | TopTipsColors              | Color configuration                                    | TopTipsDefaults.topTipsColors() | No       |

### TopTipsDefaults

| Constant          | Type  | Default Value | COUI Source                          |
| ----------------- | ----- | ------------- | ------------------------------------ |
| CornerRadius      | Dp    | 28.dp         | coui_toptips_view_radius (multi-line) |
| Weight            | Float | 3.0           | `getMultiLineWeight` (weight_16_1_large) |
| SingleLineCornerRadius | Dp | 72.dp      | couiRoundCornerFULL (single-line capsule) |
| SingleLineWeight  | Float | 1.0           | `getSingleLineWeight` (soft capsule) |
| HorizontalPadding | Dp    | 16.dp         | coui_toptips_view_icon_start_margin / content_end_margin / btn_margin |
| VerticalPadding   | Dp    | 12.dp         | coui_toptips_view_content_top_margin |
| IconSize          | Dp    | 24.dp         | coui_toptips_view_icon_btn_size (icon without title, close button) |
| TitleIconSize     | Dp    | 36.dp         | coui_toptips_view_title_icon_size    |
| IconSpacing       | Dp    | 8.dp          | coui_toptips_view_content_start_margin |
| TitleIconSpacing  | Dp    | 12.dp         | coui_toptips_view_title_icon_text_gap |
| TextMinHeight     | Dp    | 20.dp         | coui_toptips_view_content_min_height |
| TitleMinHeight    | Dp    | 22.dp         | coui_toptips_view_title_min_height   |
| MarqueeVelocity   | Dp    | 25.dp         | coui_top_tips_scroll_speed (per second) |

#### Methods

| Method Name       | Type          | Description                                                                    |
| ----------------- | ------------- | ------------------------------------------------------------------------------ |
| titleTextStyle()  | TextStyle     | Text style of the title (couiTextHeadlineXS, 16sp medium)                      |
| textStyle()       | TextStyle     | Text style of the message (couiTextBodyS, 13sp)                                |
| actionTextStyle() | TextStyle     | Label style of the action button (Widget.COUI.Button.Small, 14sp medium)       |
| topTipsColors()   | TopTipsColors | Creates the default color configuration (see below)                            |

### `topTipsColors()` factory

| Parameter          | Type  | Default                                        | COUI Attribute          |
| ------------------ | ----- | ---------------------------------------------- | ----------------------- |
| containerColor     | Color | #0A000000 light / #1AFFFFFF dark               | couiColorContainer4     |
| titleColor         | Color | COUITheme.colorScheme.labelPrimary                | couiColorLabelPrimary   |
| contentColor       | Color | COUITheme.colorScheme.labelSecondary       | couiColorLabelSecondary |
| actionColor        | Color | COUITheme.colorScheme.primary                  | couiColorContainerTheme (button fill) |
| actionContentColor | Color | COUITheme.colorScheme.onPrimary                | couiColorLabelOnColor (button label) |
| closeButtonColor   | Color | COUITheme.colorScheme.labelTertiary            | couiColorLabelTertiary  |
| closeIconColor     | Color | Color.White                                    | ic_embedded_top_tips_close |

## Behavior

- The action button is a `Widget.COUI.Button.Small.ButtonNew` primary capsule (14sp medium, 12/4dp padding, 52x28dp minimum) with the material effect forced off. The action and the close button can be shown together (12dp apart, the close button 12dp from the end).
- Without a title the start icon is 24dp and the buttons sit after the message, centred on it. Only when a start icon is shown and the message needs more than two lines next to the buttons does the action drop below the message, aligned with its start edge (8dp above, 12dp below), while the icon and the close button centre on the first two lines (`isNeedDefaultMultiTextWithoutTitle`).
- With a title the start icon is 36dp (12dp gap) and the buttons are centred on the title + message block. When a start icon is shown and either text no longer fits on one line next to the buttons, the action drops below the message and the close button aligns with the title (`isNeedDefaultMultiTextWithTitle`).
- With `marquee` the message is a single line that scrolls endlessly at 25dp/s with copies 80dp apart and 26dp fading edges when it does not fit (`COUIMarqueeTextView`); it never moves the action below it.
- Toggling `visible` plays `COUIDefaultTopTips.animateExpand` / `animateCollapse`: the card is revealed from its top edge by a 250ms height clip (COUIEaseInterpolator) while the content fades with the same progress. The banner keeps its full height in the layout during the animation and leaves the composition once collapsed.
