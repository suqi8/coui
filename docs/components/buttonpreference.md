# ButtonPreference

`ButtonPreference` is a preference row with a small inline button at the end, mirroring ColorOS's `COUIButtonPreference`. The button follows the COUI small button style (`couiSmallButtonColorStyle` → `Widget.COUI.Button.Small`): a 52×28dp minimum capsule filled with the theme accent and a 14sp medium label. The button click is independent from the row click.

## Import

```kotlin
import com.suqi8.coui.kmp.preference.ButtonPreference
import com.suqi8.coui.kmp.preference.ButtonPreferenceDefaults
```

## Basic Usage

```kotlin
ButtonPreference(
    title = "Account",
    summary = "Sign in to sync your data",
    buttonText = "Sign in",
    onButtonClick = { /* handle button click */ }
)
```

## With a Clickable Row

The row click (`onClick`) is separate from the button click (`onButtonClick`):

```kotlin
ButtonPreference(
    title = "Storage",
    summary = "12.3 GB used",
    buttonText = "Clean",
    onButtonClick = { /* clean */ },
    onClick = { /* open storage details */ }
)
```

## Component States

### Disabled State

Disabling the preference disables both the row and the inline button:

```kotlin
ButtonPreference(
    title = "Disabled Row",
    summary = "Button and row are disabled",
    buttonText = "Action",
    onButtonClick = {},
    enabled = false
)
```

## Properties

### ButtonPreference Properties

| Property Name      | Type                            | Description                                  | Default Value                             | Required |
| ------------------ | ------------------------------- | -------------------------------------------- | ----------------------------------------- | -------- |
| title              | String                          | Preference title                             | -                                         | Yes      |
| buttonText         | String                          | Label of the inline button                   | -                                         | Yes      |
| onButtonClick      | () -> Unit                      | Callback when the inline button is clicked   | -                                         | Yes      |
| modifier           | Modifier                        | Component modifier                           | Modifier                                  | No       |
| titleColor         | BasicComponentColors            | Title text color configuration               | BasicComponentDefaults.titleColor()       | No       |
| summary            | String?                         | Preference summary                           | null                                      | No       |
| summaryColor       | BasicComponentColors            | Summary text color configuration             | BasicComponentDefaults.summaryColor()     | No       |
| startAction        | @Composable (() -> Unit)?       | Custom start side content                    | null                                      | No       |
| endActions         | @Composable RowScope.() -> Unit | Custom end side content (before the button)  | {}                                        | No       |
| bottomAction       | @Composable (() -> Unit)?       | Custom bottom side content                   | null                                      | No       |
| buttonColors       | ButtonColors                    | Inline button color configuration            | ButtonPreferenceDefaults.buttonColors()   | No       |
| buttonMinWidth     | Dp                              | Minimum width of the inline button           | ButtonPreferenceDefaults.ButtonMinWidth   | No       |
| buttonMinHeight    | Dp                              | Minimum height of the inline button          | ButtonPreferenceDefaults.ButtonMinHeight  | No       |
| buttonCornerRadius | Dp                              | Corner radius of the inline button           | ButtonPreferenceDefaults.ButtonCornerRadius | No     |
| buttonInsideMargin | PaddingValues                   | Padding inside the inline button             | ButtonPreferenceDefaults.ButtonInsideMargin | No     |
| insideMargin       | PaddingValues                   | Component internal content padding           | BasicComponentDefaults.InsideMargin       | No       |
| onClick            | (() -> Unit)?                   | Callback when the row (not the button) is clicked | null                                 | No       |
| holdDownState      | Boolean                         | Whether the component is held down           | false                                     | No       |
| enabled            | Boolean                         | Component interactive state                  | true                                      | No       |

### ButtonPreferenceDefaults Object

| Constant           | Type          | Default Value                                  | Description                                                     |
| ------------------ | ------------- | ---------------------------------------------- | --------------------------------------------------------------- |
| ButtonMinWidth     | Dp            | 52.dp                                          | COUI coui_btn_small_width_min                                   |
| ButtonMinHeight    | Dp            | 28.dp                                          | COUI coui_btn_small_height_min                                  |
| ButtonCornerRadius | Dp            | 14.dp                                          | Capsule radius (COUI drawableRadius -1 = height / 2)            |
| ButtonInsideMargin | PaddingValues | PaddingValues(horizontal = 12.dp, vertical = 4.dp) | Widget.COUI.Button.Small padding                            |

#### `buttonColors()` factory

| Parameter            | Type  | Default                                        |
| -------------------- | ----- | ---------------------------------------------- |
| color                | Color | COUITheme.colorScheme.primary                 |
| disabledColor        | Color | COUITheme.colorScheme.disabledPrimaryButton   |
| contentColor         | Color | COUITheme.colorScheme.onPrimary               |
| disabledContentColor | Color | COUITheme.colorScheme.disabledOnPrimaryButton |

## Advanced Usage

### Custom Button Colors

```kotlin
ButtonPreference(
    title = "Remove Device",
    buttonText = "Remove",
    onButtonClick = { /* remove */ },
    buttonColors = ButtonPreferenceDefaults.buttonColors(
        color = COUITheme.colorScheme.error,
        contentColor = COUITheme.colorScheme.onError
    )
)
```

### With Start Icon

```kotlin
ButtonPreference(
    title = "Bluetooth Device",
    summary = "Paired",
    buttonText = "Connect",
    onButtonClick = { /* connect */ },
    startAction = {
        Icon(
            imageVector = COUIIcons.Sort,
            contentDescription = null,
            tint = COUITheme.colorScheme.onBackground,
            modifier = Modifier.padding(end = 12.dp)
        )
    }
)
```
