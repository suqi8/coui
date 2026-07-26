# InputView

The ColorOS "card input": a white card (12dp corners) holding an optional 16sp medium title and a bare, undecorated text field, with the counter / clear / password buttons at the end of the field and a 10sp error caption below the card. Mirrors COUICardSingleInputView (single line) and COUICardMultiInputView (multi line, counter at the bottom-end corner of the card). The error caption fades in over 217ms / out over 283ms with the COUI ease curve, and the field plays the COUI error shake while it is shown.

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.InputView
import io.github.suqi8.coui.kmp.basic.InputViewDefaults
```

## Basic Usage

```kotlin
var nickname by remember { mutableStateOf("") }

InputView(
    value = nickname,
    onValueChange = { nickname = it },
    title = "Nickname",
    label = "Enter your nickname",
    showCount = true,
    maxCount = 10,
    showClearButton = true,
    errorMessage = if (nickname.contains(' ')) "Nickname cannot contain spaces" else "",
)
```

## Multi-line Card

Set `singleLine = false` for the COUICardMultiInputView form: the counter moves to the
bottom-end corner of the card (12sp) and animates to the error color when the limit is hit:

```kotlin
var signature by remember { mutableStateOf("") }

InputView(
    value = signature,
    onValueChange = { signature = it },
    label = "Signature",
    showCount = true,
    maxCount = 100,
    singleLine = false,
)
```

## Properties

### InputView

| Property             | Type                      | Description                                             | Default Value                            | Required |
| -------------------- | ------------------------- | ------------------------------------------------------- | ---------------------------------------- | -------- |
| value                | String                    | Text value of the input field                           | -                                        | Yes      |
| onValueChange        | (String) -> Unit          | Callback when text changes                              | -                                        | Yes      |
| modifier             | Modifier                  | Modifier applied to the component                       | Modifier                                 | No       |
| title                | String                    | Title inside the card, hidden when empty                | ""                                       | No       |
| label                | String                    | Label (hint) of the inner field                         | ""                                       | No       |
| enabled              | Boolean                   | Whether the component is enabled                        | true                                     | No       |
| readOnly             | Boolean                   | Whether the field is read-only                          | false                                    | No       |
| showCount            | Boolean                   | Show the "length/max" counter (needs maxCount > 0)      | false                                    | No       |
| maxCount             | Int                       | Maximum characters, 0 means unlimited                   | 0                                        | No       |
| showClearButton      | Boolean                   | Show a clear button while focused and not empty         | false                                    | No       |
| showPasswordToggle   | Boolean                   | Show the password eye toggle (masks the text)           | false                                    | No       |
| errorMessage         | String                    | Error caption below the card, empty hides it            | ""                                       | No       |
| cornerRadius         | Dp                        | Corner radius of the card                               | InputViewDefaults.CornerRadius           | No       |
| colors               | InputViewColors           | Colors of card, title, counter and error line           | InputViewDefaults.inputViewColors()      | No       |
| textFieldColors      | TextFieldColors           | Colors of the inner field                               | TextFieldDefaults.textFieldColors()      | No       |
| textStyle            | TextStyle                 | Text style of the input text                            | TextFieldDefaults.textStyle(TextFieldMode.None) | No |
| keyboardOptions      | KeyboardOptions           | Keyboard options                                        | KeyboardOptions.Default                  | No       |
| keyboardActions      | KeyboardActions           | Keyboard actions                                        | KeyboardActions.Default                  | No       |
| singleLine           | Boolean                   | Single-line card (true) or multi-line card (false)      | true                                     | No       |
| maxLines             | Int                       | Maximum lines                                           | If singleLine then 1, else 5             | No       |
| visualTransformation | VisualTransformation      | Visual transformation                                   | VisualTransformation.None                | No       |
| leadingIcon          | @Composable (() -> Unit)? | Leading icon                                            | null                                     | No       |
| trailingIcon         | @Composable (() -> Unit)? | Trailing icon, placed after the built-in buttons        | null                                     | No       |
| interactionSource    | MutableInteractionSource? | Interaction source                                      | null                                     | No       |

### InputViewDefaults

| Constant                   | Type     | Description                                             | Default Value |
| -------------------------- | -------- | ------------------------------------------------------- | ------------- |
| CornerRadius               | Dp       | Corner radius of the card (couiRoundCornerM)            | 12.dp         |
| ContentPadding             | Dp       | Horizontal content padding inside the card              | 16.dp         |
| TitlePaddingTop            | Dp       | Top padding of the title                                | 12.dp         |
| TitlePaddingBottom         | Dp       | Bottom padding of the title                             | 4.dp          |
| TitleMinHeight             | Dp       | Minimum height of the title                             | 22.dp         |
| FieldPaddingVertical       | Dp       | Vertical field padding without a title                  | 15.dp         |
| FieldPaddingBottomWithTitle | Dp      | Bottom field padding when a title is shown (top is 0)   | 12.dp         |
| MultiFieldPadding          | Dp       | Vertical field padding in the multi-line card           | 13.dp         |
| MultiCountMarginBottom     | Dp       | Bottom margin of the multi-line counter                 | 12.dp         |
| MultiCountFontSize         | TextUnit | Font size of the multi-line counter                     | 12.sp         |
| CountFontSize              | TextUnit | Font size of the single-line inline counter             | 10.sp         |
| ErrorFontSize              | TextUnit | Font size of the error caption                          | 10.sp         |
| ErrorPaddingTop            | Dp       | Top padding of the error caption                        | 4.dp          |
| MaxLines                   | Int      | Default maximum lines                                   | 5             |

`titleTextStyle()` returns the default title style (16sp, medium weight, COUI couiTextAppearanceHeadline6).

### `inputViewColors()` factory

| Parameter          | Type  | Default                                          |
| ------------------ | ----- | ------------------------------------------------ |
| cardColor          | Color | COUITheme.colorScheme.surfaceContainer          |
| titleColor         | Color | COUITheme.colorScheme.onSurface                 |
| disabledTitleColor | Color | COUITheme.colorScheme.disabledOnSurface         |
| countColor         | Color | COUITheme.colorScheme.onSurfaceVariantActions   |
| errorColor         | Color | COUITheme.colorScheme.error                     |

## Behavior

- The inner field is fully undecorated (`TextFieldMode.None`, COUIEditText MODE_BACKGROUND_NO_LINE) — no underline and no border, exactly like the card inputs in ColorOS Settings.
- When `maxCount > 0` the input is hard-limited to that many characters; the counter (if shown) reads `length/max` and animates to the error color at the limit (250ms, like COUICardMultiInputView).
- Setting a non-empty `errorMessage` shows the caption below the card (fade in 217ms / out 283ms, COUI ease 0.33, 0, 0.67, 1) and plays the COUI error shake on the field. The last non-empty message keeps showing during the fade-out.
- Field paddings mirror COUICardSingleInputView: 15dp top/bottom without a title; 0dp top / 12dp bottom when a title is shown; 13dp in the multi-line card.

## Advanced Usage

### Password input with validation

```kotlin
var password by remember { mutableStateOf("") }

InputView(
    value = password,
    onValueChange = { password = it },
    title = "Password",
    label = "At least 8 characters",
    showPasswordToggle = true,
    errorMessage = if (password.isNotEmpty() && password.length < 8) "Password is too short" else "",
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
)
```
