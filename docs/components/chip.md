# Chip

A capsule shaped, checkable filter tag mirroring ColorOS's COUIChip in its selection style (Widget.COUI.Chip.Choice). The unselected chip is a translucent gray capsule with a primary label; selecting it fills the capsule with the accent color and flips the label to white. The two states cross-fade on the COUI check spring, and pressing shrinks the chip while tinting the fill like COUIPressFeedbackHelper.

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
| CornerRadius | Dp            | 16.dp                            | chipCornerRadius -1 → capsule (height / 2)     |
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
| containerColor                 | Color | COUITheme.colorScheme.secondaryVariant            | uncheckedBackgroundColor          |
| selectedContainerColor         | Color | COUITheme.colorScheme.primary                     | checkedBackgroundColor            |
| disabledContainerColor         | Color | COUITheme.colorScheme.disabledSecondaryVariant    | uncheckedDisabledBackgroundColor  |
| selectedDisabledContainerColor | Color | COUITheme.colorScheme.disabledPrimaryButton       | checkedDisabledBackgroundColor    |
| contentColor                   | Color | COUITheme.colorScheme.onSurface                   | uncheckedTextColor                |
| selectedContentColor           | Color | COUITheme.colorScheme.onPrimary                   | checkedTextColor                  |
| disabledContentColor           | Color | COUITheme.colorScheme.disabledOnSecondaryVariant  | uncheckedDisabledTextColor        |
| selectedDisabledContentColor   | Color | COUITheme.colorScheme.disabledOnPrimaryButton     | checkedDisabledTextColor          |

## Behavior

- Selecting/deselecting cross-fades the capsule, label and icon colors on a critically damped spring (COUIChipDrawable.TintAnimation, response 0.3s / bounce 0).
- Pressing shrinks the chip towards 0.92 of its size and overlays the press tint (COUIPressFeedbackHelper + COUIMaskEffectDrawable); a quick tap still flashes the tint visibly.
- The label is single line and ellipsized beyond `TextMaxWidth`; the whole chip is clamped between `MinWidth` and `MaxWidth`.
