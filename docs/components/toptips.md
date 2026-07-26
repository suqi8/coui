# TopTips

A tip banner shown at the top of a page, mirroring ColorOS's COUIDefaultTopTips: a rounded card holding an optional 24dp start icon, a 14sp message and one trailing element — either an accent colored action label or a round close button. Toggling visibility plays the COUI 250ms vertical scale animation.

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
            modifier = Modifier.size(24.dp),
        )
    },
    onClose = { visible = false },
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
| startIcon    | (@Composable () -> Unit)?  | Optional leading icon                                  | null                            | No       |
| actionText   | String?                    | Optional label of the trailing action button           | null                            | No       |
| onAction     | (() -> Unit)?              | Callback when the action button is clicked             | null                            | No       |
| onClose      | (() -> Unit)?              | Callback of the close button (shown without action)    | null                            | No       |
| cornerRadius | Dp                         | Corner radius of the banner                            | TopTipsDefaults.CornerRadius    | No       |
| colors       | TopTipsColors              | Color configuration                                    | TopTipsDefaults.topTipsColors() | No       |

### TopTipsDefaults

| Constant        | Type | Default Value | COUI Source                          |
| --------------- | ---- | ------------- | ------------------------------------ |
| CornerRadius    | Dp   | 12.dp         | couiRoundCornerM                     |
| ContentPadding  | Dp   | 12.dp         | coui_toptips_view_btn_margin         |
| VerticalPadding | Dp   | 12.dp         | coui_toptips_view_title_top_margin   |
| IconSize        | Dp   | 24.dp         | coui_toptips_view_icon_btn_size      |
| IconSpacing     | Dp   | 8.dp          | coui_toptips_view_title_start_margin |
| TextMinHeight   | Dp   | 20.dp         | coui_toptips_view_title_min_height   |

#### Methods

| Method Name       | Type          | Description                                                                    |
| ----------------- | ------------- | ------------------------------------------------------------------------------ |
| textStyle()       | TextStyle     | Default text style of the message (14sp, `coui_toptips_view_default_text_size`) |
| actionTextStyle() | TextStyle     | Default text style of the action button (14sp medium)                          |
| topTipsColors()   | TopTipsColors | Creates the default color configuration (see below)                            |

### `topTipsColors()` factory

| Parameter        | Type  | Default                                        | COUI Attribute        |
| ---------------- | ----- | ---------------------------------------------- | --------------------- |
| containerColor   | Color | #0A000000 light / #14FFFFFF dark               | couiColorContainer4   |
| contentColor     | Color | COUITheme.colorScheme.onSurfaceSecondary      | couiColorSecondNeutral |
| actionColor      | Color | COUITheme.colorScheme.primary                 | couiColorPrimaryText  |
| closeButtonColor | Color | #29000000 light / #40FFFFFF dark               | couiColorControls     |
| closeIconColor   | Color | Color.White                                    | coui_ic_toptips_close |

## Behavior

- The trailing element is exclusive, matching COUIDefaultTopTipsView's button types: `actionText` shows the accent text button; otherwise `onClose` shows the round close button.
- When the single-line message would collide with the action label, the label drops to its own end-aligned row below the text (COUIDefaultTopTipsView.isNeedMultiText); in that layout the start icon aligns with the first text line.
- Toggling `visible` plays the COUICustomTopTips show/dismiss animation: a 250ms vertical scale pivoting at the center; the banner leaves the composition once the dismiss animation finishes.
- The message uses 14sp (`coui_toptips_view_default_text_size`); the action label is 14sp medium.
