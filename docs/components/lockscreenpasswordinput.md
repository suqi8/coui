# LockScreenPasswordInput

`LockScreenPasswordInput` reproduces the ColorOS lock-screen password entry (`COUILockScreenPwdInputLayout` + `COUILockScreenPwdInputView`): a 48dp capsule password field with an eye toggle and a round 48dp next button. In the desktop (lock screen) mode both are glass: transparent with an inner shadow and a hairline border, and pressing the button springs it down and lights its rim and the end of the field.

<div style="position: relative; height: 320px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=lockScreenPasswordInput" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.LockScreenPasswordInput
import io.github.suqi8.coui.kmp.basic.LockScreenPasswordInputDefaults
import io.github.suqi8.coui.kmp.basic.LockScreenPasswordInputMode
```

## Basic Usage

With `enableInputCount` the input is capped at `maxCount` and the next button activates at `minCount`:

```kotlin
var password by remember { mutableStateOf("") }
var error by remember { mutableStateOf("") }

LockScreenPasswordInput(
    value = password,
    onValueChange = { password = it; error = "" },
    onNextClick = { if (!check(it)) error = "Incorrect password" },
    errorMessage = error,
    enableInputCount = true,
)
```

## Lock Screen

```kotlin
LockScreenPasswordInput(
    value = password,
    onValueChange = { password = it },
    onNextClick = { unlock(it) },
    mode = LockScreenPasswordInputMode.Desktop,
)
```

Without a blurred wallpaper behind it, give the desktop field a fill: `colors = LockScreenPasswordInputDefaults.colors(LockScreenPasswordInputMode.Desktop).copy(fieldColor = LockScreenPasswordInputDefaults.DesktopFallbackColor, buttonColor = LockScreenPasswordInputDefaults.DesktopFallbackColor)`.

## Properties

### LockScreenPasswordInput

| Property               | Type                          | Description                                          | Default Value                                     | Required |
| ---------------------- | ----------------------------- | ---------------------------------------------------- | ------------------------------------------------- | -------- |
| value                  | String                        | The password                                         | -                                                 | Yes      |
| onValueChange          | (String) -> Unit              | Password changed                                     | -                                                 | Yes      |
| onNextClick            | (String) -> Unit              | Active next button clicked                           | -                                                 | Yes      |
| modifier               | Modifier                      | Modifier applied to the component                    | Modifier                                          | No       |
| mode                   | LockScreenPasswordInputMode   | `Setting` or `Desktop`                               | Setting                                           | No       |
| enabled                | Boolean                       | Whether the field accepts input                      | true                                              | No       |
| label                  | String                        | Hint while empty                                     | ""                                                | No       |
| errorMessage           | String                        | Error below the field; empty hides it                | ""                                                | No       |
| showPasswordToggle     | Boolean                       | Show the eye toggle                                  | true                                              | No       |
| enableInputCount       | Boolean                       | Apply `minCount` / `maxCount`                        | false                                             | No       |
| minCount               | Int                           | Length that activates the next button                | 6                                                 | No       |
| maxCount               | Int                           | Maximum length                                       | 16                                                | No       |
| colors                 | LockScreenPasswordInputColors | Colors                                               | LockScreenPasswordInputDefaults.colors(mode)      | No       |
| keyboardOptions        | KeyboardOptions               | Keyboard options                                     | NumberPassword                                    | No       |
| keyboardActions        | KeyboardActions               | Keyboard actions                                     | KeyboardActions.Default                           | No       |
| nextContentDescription | String                        | Accessibility description of the button              | "Next"                                            | No       |
| nextButtonInteractionSource | MutableInteractionSource? | Interactions of the next button; emitting a press plays the Desktop press feedback | null | No |

### LockScreenPasswordInputDefaults

| Constant               | Type     | Default Value | COUI Source                                        |
| ---------------------- | -------- | ------------- | -------------------------------------------------- |
| Height                 | Dp       | 48.dp         | coui_input_lock_screen_pwd_height                  |
| ButtonMargin           | Dp       | 12.dp         | coui_input_lock_screen_pwd_icon_margin             |
| FieldWidth             | Dp       | 268.dp        | coui_input_lock_screen_pwd_setting_width           |
| FieldPadding           | Dp       | 16.dp         | coui_input_lock_screen_pwd_card_padding            |
| FieldPaddingVertical   | Dp       | 10.5.dp       | coui_input_lock_screen_pwd_title_padding_top       |
| IconSize               | Dp       | 24.dp         | coui_input_lock_screen_pwd_next_src                |
| ErrorFontSize          | TextUnit | 10.sp         | couiTextAppearanceCaption                          |
| ErrorPaddingTop        | Dp       | 8.dp          | text_input_error                                   |
| ErrorPaddingHorizontal | Dp       | 32.dp         | text_input_error                                   |
| MinCount               | Int      | 6             | DEFAULT_MIN_INPUT_COUNT                            |
| MaxCount               | Int      | 16            | DEFAULT_MAX_INPUT_COUNT                            |
| DesktopFallbackColor   | Color    | #33FFFFFF     | coui_input_lock_screen_pwd_view_bg_color_desktop   |

| Method       | Description                                                                                                         |
| ------------ | ------------------------------------------------------------------------------------------------------------------- |
| colors(mode) | Setting: `cardBackground` field and button, tertiary / primary arrow. Desktop: transparent, white text, #4DFFFFFF / white arrow |

### LockScreenPasswordInputColors

| Property        | Type  | Description                                  |
| --------------- | ----- | -------------------------------------------- |
| fieldColor      | Color | Field fill                                   |
| buttonColor     | Color | Next button fill                             |
| textColor       | Color | Password text                                |
| hintColor       | Color | Hint                                         |
| iconColor       | Color | Inactive arrow                               |
| activeIconColor | Color | Active arrow                                 |
| eyeColor        | Color | Eye toggle: label secondary in setting mode, white in desktop mode |
| errorColor      | Color | Error line                                   |

## Behavior

- The error line fades in over 217ms and out over 283ms (`COUIEaseInterpolator`) and the field shows its error state.
- Desktop press: the button springs to the COUI press scale; its rim glows and a radial light reaches into the field, on response 0.3 springs (0.6 for the outer light on release). The press effect only runs while the button is active.
- The eye toggle keeps the 34dp size of COUI's eye drawables and reaches 12dp into the field's end padding (`button_layout` marginEnd -12dp); it animates its slash like the `TextField` toggle.
- An invalid `minCount` / `maxCount` pair falls back to 6 / 16.
- The next button sits at the end of the row: on the left in right-to-left layouts.
