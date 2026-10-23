# Chip

A capsule shaped, checkable filter tag mirroring ColorOS's COUIChip in its selection style (Widget.COUI.Chip.Choice). The unselected chip is a translucent gray capsule with a primary label; selecting it fills the capsule with the accent color and flips the label to white. The two states cross-fade on the COUI check spring, and pressing scales the chip with the COUI 17 press feedback while tinting the fill.

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.Chip
import io.github.suqi8.coui.kmp.basic.ChipDefaults
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

## Single Selection Group

```kotlin
var selectedIndex by remember { mutableIntStateOf(0) }

Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    listOf("All", "Photos", "Videos").forEachIndexed { index, label ->
        Chip(
            selected = selectedIndex == index,
            onClick = { selectedIndex = index },
            label = label,
        )
    }
}
```

The 8dp spacing matches COUIChipGroup's default horizontal/vertical spacing.

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
| icon              | (@Composable () -> Unit)?  | Optional leading icon                      | null                      | No       |
| cornerRadius      | Dp                         | Corner radius of the capsule               | ChipDefaults.CornerRadius | No       |
| minWidth          | Dp                         | Minimum width                              | ChipDefaults.MinWidth     | No       |
| minHeight         | Dp                         | Minimum height                             | ChipDefaults.MinHeight    | No       |
| maxWidth          | Dp                         | Maximum width                              | ChipDefaults.MaxWidth     | No       |
| colors            | ChipColors                 | Color configuration                        | ChipDefaults.chipColors() | No       |
| insideMargin      | PaddingValues              | Horizontal padding inside the chip         | ChipDefaults.InsideMargin | No       |
| interactionSource | MutableInteractionSource?  | Interaction source of the chip             | null                      | No       |
| indication        | Indication?                | Indication (press feedback is built in)    | null                      | No       |

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

#### Methods

| Method Name  | Type       | Description                                                                  |
| ------------ | ---------- | ---------------------------------------------------------------------------- |
| textStyle()  | TextStyle  | Default label style (COUITheme.textStyles.body2, COUI couiTextBodyM 14sp)    |
| chipColors() | ChipColors | Creates the color configuration for the chip                                 |

### `chipColors()` factory

| Parameter                      | Type  | Default                                            | COUI Attribute                    |
| ------------------------------ | ----- | -------------------------------------------------- | --------------------------------- |
| containerColor                 | Color | COUITheme.colorScheme.container8                  | uncheckedBackgroundColor          |
| selectedContainerColor         | Color | COUITheme.colorScheme.primary                     | checkedBackgroundColor            |
| disabledContainerColor         | Color | COUITheme.colorScheme.container8                  | uncheckedDisabledBackgroundColor  |
| selectedDisabledContainerColor | Color | COUITheme.colorScheme.disabledPrimaryButton       | checkedDisabledBackgroundColor    |
| contentColor                   | Color | COUITheme.colorScheme.onSurface                   | uncheckedTextColor                |
| selectedContentColor           | Color | COUITheme.colorScheme.onPrimary                   | checkedTextColor                  |
| disabledContentColor           | Color | COUITheme.colorScheme.disabledChipText            | uncheckedDisabledTextColor        |
| selectedDisabledContentColor   | Color | COUITheme.colorScheme.disabledOnPrimaryButton     | checkedDisabledTextColor          |

## Behavior

- Selecting/deselecting cross-fades the capsule, label and icon colors on a critically damped spring (COUIChipDrawable.TintAnimation, response 0.3s / bounce 0).
- Enabling or disabling the chip switches its colors at once, without the spring (COUIChipDrawable cancels the tint animation).
- Pressing scales the chip like COUI 17's COUIPressFeedbackHelper: the size-dependent COUIPressAnimatorUnit press (about 0.89 for a 52 x 32dp chip, 0.91 for a 100 x 32dp one) released on an under-damped spring, with no rubber or deformation. The fill takes the `press` tint (COUIMaskEffectDrawable); a quick tap still flashes the tint visibly. Hovering overlays the `hover` tint.
- Keyboard focus draws a 3dp `focusOutline` ring just outside the capsule (COUIStrokeDrawable, `default_focus_stroke_radius`), fading on the COUI response 0.3 spring.
- Default colors follow COUI 17: the unselected capsule is couiColorContainer8 (#14000000 / #1AFFFFFF) whether enabled or not; the disabled labels are #42000000 / #29FFFFFF (unselected) and #8AFFFFFF / #29FFFFFF (selected).
- The label is single line and ellipsized beyond `TextMaxWidth`; the whole chip is clamped between `MinWidth` and `MaxWidth`.
