# Chip

A capsule shaped, checkable filter tag mirroring ColorOS's COUIChip in its selection style (Widget.COUI.Chip.Choice). The unselected chip is a translucent gray capsule with a primary label; selecting it fills the capsule with the accent color and flips the label to white. The two states cross-fade on the COUI check spring, and pressing scales the chip with the COUI 17 press feedback while tinting the fill.

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=chip" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.Chip
import io.github.suqi8.coui.kmp.basic.ChipDefaults
import io.github.suqi8.coui.kmp.basic.IconChip
import io.github.suqi8.coui.kmp.basic.SuggestionChip
```

## Basic Usage

```kotlin
var selected by remember { mutableStateOf(false) }

Chip(
    selected = selected,
    onClick = { selected = !selected },
    label = "Recommended",
)
```

## With a Leading Icon

The icon is laid out in a 16dp box and tinted with the current label color through `LocalContentColor` (COUIChip `chipIconApplyTint`).

```kotlin
Chip(
    selected = selected,
    onClick = { selected = !selected },
    label = "Recommended",
    icon = {
        Icon(
            imageVector = COUIIcons.Basic.Check,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
        )
    },
)
```

## Close Icon, Count and Red Dot

`onClose` shows the COUI close icon (`ic_chip_close`) at the end of the chip; tapping it calls `onClose` instead of `onClick` (`setCloseIconVisible` + `setOnCloseIconClickListener`). `count` draws a secondary-colored number 2dp after the label (`setCountText`), and `showRedDot` draws the 6dp hint red dot after the label (`setShowRedDot`).

```kotlin
Chip(
    selected = selected,
    onClick = { selected = !selected },
    label = "Downloads",
    count = "12",
    showRedDot = true,
    onClose = { /* Remove the chip */ },
)
```

## Suggestion Chip

`SuggestionChip` is the smaller, non-checkable chip of `Widget.COUI.Chip.Suggestion` (also `Widget.COUI.Chip.Record` for search history): 28dp high, a 12sp label, a 14dp leading icon and a 12dp close icon. A tap only calls `onClick`.

```kotlin
SuggestionChip(
    onClick = { /* Search again */ },
    label = "Wallpaper",
    onClose = { /* Delete the record */ },
)
```

## Icon Chip

`IconChip` is the 28dp round, icon-only chip of `Widget.COUI.Chip.Suggestion.Collapsable`, which `ChipGroup` uses for its expand / collapse button.

```kotlin
IconChip(onClick = { /* ... */ }) {
    Icon(imageVector = COUIIcons.Basic.Search, contentDescription = "Search")
}
```

## Chip Groups

Use [ChipGroup](./chipgroup) to wrap chips into rows 8dp apart, with COUI single / required selection and the collapsable mode.

## Component States

### Disabled State

A disabled chip keeps its selected/unselected shape but switches to the disabled container and content colors, and no longer reacts to presses:

```kotlin
Chip(
    selected = true,
    onClick = { /* Ignored while disabled */ },
    label = "Disabled",
    enabled = false,
)
```

## Properties

### Chip

| Property          | Type                       | Description                                | Default Value             | Required |
| ----------------- | -------------------------- | ------------------------------------------ | ------------------------- | -------- |
| selected          | Boolean                    | Whether the chip is selected               | -                         | Yes      |
| onClick           | () -> Unit                 | Callback when the chip is clicked          | -                         | Yes      |
| label             | String                     | Text label (single line, ellipsized)       | -                         | Yes      |
| modifier          | Modifier                   | Modifier applied to the chip               | Modifier                  | No       |
| enabled           | Boolean                    | Whether the chip is enabled                | true                      | No       |
| showRedDot        | Boolean                    | Whether to draw the hint red dot           | false                     | No       |
| icon              | (@Composable () -> Unit)?  | Optional leading icon                      | null                      | No       |
| onClose           | (() -> Unit)?              | Shows the close icon and handles its tap   | null                      | No       |
| count             | String?                    | Count drawn after the label                | null                      | No       |
| cornerRadius      | Dp                         | Corner radius of the capsule               | ChipDefaults.CornerRadius | No       |
| minWidth          | Dp                         | Minimum width                              | ChipDefaults.MinWidth     | No       |
| minHeight         | Dp                         | Minimum height                             | ChipDefaults.MinHeight    | No       |
| maxWidth          | Dp                         | Maximum width                              | ChipDefaults.MaxWidth     | No       |
| colors            | ChipColors                 | Color configuration                        | ChipDefaults.chipColors() | No       |
| insideMargin      | PaddingValues              | Horizontal padding inside the chip         | ChipDefaults.InsideMargin | No       |
| interactionSource | MutableInteractionSource?  | Interaction source of the chip             | null                      | No       |
| indication        | Indication?                | Indication (press feedback is built in)    | null                      | No       |

### SuggestionChip

Same parameters as `Chip` without `selected`; the defaults are `cornerRadius = ChipDefaults.SuggestionCornerRadius` and `minHeight = ChipDefaults.SuggestionMinHeight`, with the `suggestionTextStyle()` label.

### IconChip

| Property          | Type                      | Description                    | Default Value             | Required |
| ----------------- | ------------------------- | ------------------------------ | ------------------------- | -------- |
| onClick           | () -> Unit                | Callback when clicked          | -                         | Yes      |
| modifier          | Modifier                  | Modifier applied to the chip   | Modifier                  | No       |
| enabled           | Boolean                   | Whether the chip is enabled    | true                      | No       |
| colors            | ChipColors                | Color configuration            | ChipDefaults.chipColors() | No       |
| interactionSource | MutableInteractionSource? | Interaction source of the chip | null                      | No       |
| icon              | @Composable () -> Unit    | The 16dp icon                  | -                         | Yes      |

### ChipDefaults

| Constant     | Type          | Default Value                    | COUI Source                                    |
| ------------ | ------------- | -------------------------------- | ---------------------------------------------- |
| MinWidth     | Dp            | 52.dp                            | coui_chip_default_min_width                    |
| MinHeight    | Dp            | 32.dp                            | coui_chip_selection_style_height               |
| MaxWidth     | Dp            | 300.dp                           | coui_chip_default_max_width                    |
| TextMaxWidth | Dp            | 200.dp                           | coui_chip_default_max_text_width               |
| CornerRadius | Dp            | 16.dp                            | chipCornerRadius -1 → capsule (height / 2), drawn as the weight-1.0 soft capsule; a smaller radius takes `default_g2_weight` 2.45 |
| IconSize     | Dp            | 16.dp                            | coui_chip_selection_style_chip_icon_size       |
| IconSpacing  | Dp            | 4.dp                             | coui_chip_selection_style_chip_icon_end_padding |
| InsideMargin | PaddingValues | PaddingValues(horizontal = 12.dp) | coui_chip_selection_style_chip_horizontal_padding |
| SuggestionMinHeight | Dp     | 28.dp                            | coui_chip_suggest_style_height                 |
| SuggestionCornerRadius | Dp  | 14.dp                            | capsule of the 28dp height                     |
| SuggestionIconSize | Dp      | 14.dp                            | coui_chip_suggest_style_chip_icon_size         |
| CloseIconSize | Dp           | 12.dp                            | coui_chip_suggest_style_close_icon_size        |
| CloseIconStartPadding | Dp   | 8.dp                             | coui_chip_suggest_style_close_icon_start_padding |
| CloseIconEndPadding | Dp     | 8.dp                             | coui_chip_suggest_style_close_icon_end_padding |
| CountSpacing | Dp            | 2.dp                             | COUIChipDrawable mCountMarginStart             |
| CollapsableSize | Dp         | 28.dp                            | coui_chip_group_collapsable_button_size        |
| CollapsableIconSize | Dp     | 16.dp                            | Widget.COUI.Chip.Suggestion.Collapsable chipIconSize |

#### Methods

| Method Name  | Type       | Description                                                                  |
| ------------ | ---------- | ---------------------------------------------------------------------------- |
| textStyle()  | TextStyle  | Default label style (COUITheme.textStyles.bodyM, COUI couiTextBodyM 14sp)    |
| suggestionTextStyle() | TextStyle | Suggestion chip label (COUITheme.textStyles.bodyXS, couiTextBodyXS 12sp) |
| chipColors() | ChipColors | Creates the color configuration for the chip                                 |

### `chipColors()` factory

| Parameter                      | Type  | Default                                            | COUI Attribute                    |
| ------------------------------ | ----- | -------------------------------------------------- | --------------------------------- |
| containerColor                 | Color | COUITheme.colorScheme.container8                  | uncheckedBackgroundColor          |
| selectedContainerColor         | Color | COUITheme.colorScheme.primary                     | checkedBackgroundColor            |
| disabledContainerColor         | Color | COUITheme.colorScheme.container8                  | uncheckedDisabledBackgroundColor  |
| selectedDisabledContainerColor | Color | COUITheme.colorScheme.disable       | checkedDisabledBackgroundColor    |
| contentColor                   | Color | COUITheme.colorScheme.labelPrimary                   | uncheckedTextColor                |
| selectedContentColor           | Color | COUITheme.colorScheme.onPrimary                   | checkedTextColor                  |
| disabledContentColor           | Color | COUITheme.colorScheme.disabledChipText            | uncheckedDisabledTextColor        |
| selectedDisabledContentColor   | Color | COUITheme.colorScheme.disabledOnPrimaryButton     | checkedDisabledTextColor          |
| countColor                     | Color | COUITheme.colorScheme.labelSecondary          | couiColorLabelSecondary (count)   |
| closeIconColor                 | Color | COUITheme.colorScheme.labelSecondary          | `ic_chip_close` couiColorSecondNeutral |

## Behavior

- Selecting/deselecting cross-fades the capsule, label and icon colors on a critically damped spring (COUIChipDrawable.TintAnimation, response 0.3s / bounce 0).
- Enabling or disabling the chip switches its colors at once, without the spring (COUIChipDrawable cancels the tint animation).
- Pressing scales the chip like COUI 17's COUIPressFeedbackHelper: the size-dependent COUIPressAnimatorUnit press (about 0.89 for a 52 x 32dp chip, 0.91 for a 100 x 32dp one) released on an under-damped spring, with no rubber or deformation. The fill takes the `press` tint (COUIMaskEffectDrawable); a quick tap still flashes the tint visibly. Hovering overlays the `hover` tint.
- Keyboard focus draws a 3dp `focusOutline` ring just outside the capsule (COUIStrokeDrawable, `default_focus_stroke_radius`), fading on the COUI response 0.3 spring.
- Default colors follow COUI 17: the unselected capsule is couiColorContainer8 (#14000000 / #1AFFFFFF) whether enabled or not; the disabled labels are #42000000 / #29FFFFFF (unselected) and #8AFFFFFF / #29FFFFFF (selected).
- The label is single line and ellipsized beyond `TextMaxWidth`; the whole chip is clamped between `MinWidth` and `MaxWidth`.
