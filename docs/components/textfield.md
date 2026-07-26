# TextField

`TextField` is a basic input component in Miuix for receiving text input from users, styled after ColorOS COUIEditText. By default it renders the ColorOS Settings dialog form: bare 16sp text over a hairline underline that turns into an expanding accent line when focused, with the label acting as a plain placeholder. Stroke-only rectangle and fully undecorated (card) forms, an opt-in floating label, error shake, character counter, clear button and password toggle are also available.

<div style="position: relative; height: 340px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=textField" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import com.suqi8.coui.kmp.basic.TextField
import com.suqi8.coui.kmp.basic.TextFieldMode
```

## Basic Usage

The TextField component can be used to get user input:

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = "Username"
)
```

::: info
This TextField component now also supports the latest state-based version. Please refer to the [State-based](https://developer.android.com/develop/ui/compose/text/user-input?textfield=state-based) documentation for details.
:::

## Input Types

### TextField with Label (Placeholder)

By default (`useLabelAsPlaceholder = true`, matching ColorOS where every input uses the
HintDisable styles) the label is a plain placeholder: it is visible while the field is
empty and disappears once text is entered:

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = "Email Address"
)
```

### Floating Label

Set `useLabelAsPlaceholder = false` to enable the COUI HintAnim floating label: the label
shrinks to 10sp and floats up as soon as the field is focused or filled (200ms, COUI move
ease curve):

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = "Please enter content",
    useLabelAsPlaceholder = false
)
```

## Component States

### Disabled State

```kotlin
var text by remember { mutableStateOf("") }
TextField(
    value = text,
    onValueChange = { text = it },
    label = "Disabled Input Field",
    enabled = false
)
```

### Read-Only State

```kotlin
var text by remember { mutableStateOf("This is read-only content") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = "Read-Only Input Field",
    readOnly = true
)
```

## Background Modes

`TextField` supports the three COUIEditText background modes via `backgroundMode`:

- `TextFieldMode.Line` (default): no fill; a 0.33dp hairline underline plus a 1dp accent
  line that expands from the start edge when focused — the form ColorOS Settings uses for
  dialog and bottom-sheet inputs
- `TextFieldMode.Rectangle`: stroke-only rounded rectangle (10dp corners, no fill);
  0.33dp hairline stroke, 1dp accent stroke when focused; text is bold by default
- `TextFieldMode.None`: no background decoration at all — bare text, the form used inside
  white input cards (see `InputView`)

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = "Rectangle style",
    backgroundMode = TextFieldMode.Rectangle
)
```

### Focus Line Only

In `Line` mode, `justShowFocusLine = true` hides the resting underline and keeps only the
focused expanding line, mirroring the ColorOS Settings card-preference input
(COUIInputPreference `couiJustShowFocusLine`, default true on device). Place the field
inside a `Card` for the full Settings look:

```kotlin
var text by remember { mutableStateOf("") }

Card {
    TextField(
        value = text,
        onValueChange = { text = it },
        label = "Device name",
        justShowFocusLine = true,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}
```

## Error State

Setting `isError = true` tints the border / underline and label with the error color and
plays a one-shot horizontal shake animation:

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = "Digits only",
    isError = text.isNotEmpty() && !text.all { it.isDigit() }
)
```

## Character Counter

Setting `maxCount` shows a "count/max" counter at the end of the field and truncates input
beyond the limit. The counter turns red once the limit is reached:

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = "Max 10 characters",
    maxCount = 10
)
```

## Clear Button

Setting `showClearButton = true` shows a clear (fast delete) button while the field is
focused and not empty. Tapping it clears the whole text:

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = "Quick delete",
    showClearButton = true
)
```

## Password Toggle

Setting `showPasswordToggle = true` shows an eye button that switches the password
visibility. While hidden, the text is masked with bullets:

```kotlin
var password by remember { mutableStateOf("") }

TextField(
    value = password,
    onValueChange = { password = it },
    label = "Password",
    showPasswordToggle = true,
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
)
```

## Properties

### TextField Properties

| Property Name         | Type                                         | Description                         | Default Value                                    | Required |
| --------------------- | -------------------------------------------- | ----------------------------------- | ------------------------------------------------ | -------- |
| value                 | String or TextFieldValue                     | Text value of the input field       | -                                                | Yes      |
| onValueChange         | (String) -> Unit or (TextFieldValue) -> Unit | Callback when text changes          | -                                                | Yes      |
| modifier              | Modifier                                     | Modifier applied to the input field | Modifier                                         | No       |
| backgroundMode        | TextFieldMode                                | Background decoration mode          | TextFieldMode.Line                               | No       |
| insideMargin          | DpSize                                       | Internal padding of input field     | TextFieldDefaults.insideMargin(backgroundMode)   | No       |
| colors                | TextFieldColors                              | Colors used by the field            | TextFieldDefaults.textFieldColors()              | No       |
| cornerRadius          | Dp                                           | Corner radius (Rectangle mode)      | TextFieldDefaults.CornerRadius                    | No       |
| label                 | String                                       | Label / placeholder text            | ""                                               | No       |
| useLabelAsPlaceholder | Boolean                                      | Plain placeholder (true) or floating label (false) | true                              | No       |
| justShowFocusLine     | Boolean                                      | Line mode: hide the resting underline | false                                          | No       |
| enabled               | Boolean                                      | Whether input field is enabled      | true                                             | No       |
| readOnly              | Boolean                                      | Whether input field is read-only    | false                                            | No       |
| isError               | Boolean                                      | Error state (red tint + shake)      | false                                            | No       |
| maxCount              | Int?                                         | Max characters; shows a counter     | null                                             | No       |
| showClearButton       | Boolean                                      | Show clear button when focused      | false                                            | No       |
| showPasswordToggle    | Boolean                                      | Show password visibility toggle     | false                                            | No       |
| textStyle             | TextStyle                                    | Text style                          | TextFieldDefaults.textStyle(backgroundMode)      | No       |
| keyboardOptions       | KeyboardOptions                              | Keyboard options                    | KeyboardOptions.Default                          | No       |
| keyboardActions       | KeyboardActions                              | Keyboard actions                    | KeyboardActions.Default                          | No       |
| leadingIcon           | @Composable (() -> Unit)?                    | Leading icon                        | null                                             | No       |
| trailingIcon          | @Composable (() -> Unit)?                    | Trailing icon                       | null                                             | No       |
| singleLine            | Boolean                                      | Single line input                   | false                                            | No       |
| maxLines              | Int                                          | Maximum lines                       | If singleLine is true then 1, else Int.MAX_VALUE | No       |
| minLines              | Int                                          | Minimum lines                       | 1                                                | No       |
| visualTransformation  | VisualTransformation                         | Visual transformation               | VisualTransformation.None                        | No       |
| onTextLayout          | (TextLayoutResult) -> Unit                   | Text layout callback                | {}                                               | No       |
| interactionSource     | MutableInteractionSource?                    | Interaction source                  | null                                             | No       |
| cursorBrush           | Brush                                        | Cursor brush                        | SolidColor(colors.borderColor)                   | No       |

### TextField (state-based) Properties

| Property Name         | Type                                           | Description                                        | Default Value                              | Required |
| --------------------- | ---------------------------------------------- | -------------------------------------------------- | ------------------------------------------ | -------- |
| state                 | TextFieldState                                 | State object holding text and selection            | -                                          | Yes      |
| modifier              | Modifier                                       | Modifier applied to the input field                | Modifier                                   | No       |
| backgroundMode        | TextFieldMode                                  | Background decoration mode                         | TextFieldMode.Line                         | No       |
| insideMargin          | DpSize                                         | Internal padding of input field                    | TextFieldDefaults.insideMargin(backgroundMode) | No       |
| colors                | TextFieldColors                                | Colors used by the field                           | TextFieldDefaults.textFieldColors()        | No       |
| cornerRadius          | Dp                                             | Corner radius (Rectangle mode)                     | TextFieldDefaults.CornerRadius              | No       |
| label                 | String                                         | Label / placeholder text                           | ""                                         | No       |
| useLabelAsPlaceholder | Boolean                                        | Plain placeholder (true) or floating label (false) | true                                       | No       |
| justShowFocusLine     | Boolean                                        | Line mode: hide the resting underline              | false                                      | No       |
| enabled               | Boolean                                        | Whether input field is enabled                     | true                                       | No       |
| readOnly              | Boolean                                        | Whether input field is read-only                   | false                                      | No       |
| isError               | Boolean                                        | Error state (red tint + shake)                     | false                                      | No       |
| maxCount              | Int?                                           | Max characters; shows a counter                    | null                                       | No       |
| showClearButton       | Boolean                                        | Show clear button when focused                     | false                                      | No       |
| showPasswordToggle    | Boolean                                        | Show password visibility toggle                    | false                                      | No       |
| inputTransformation   | InputTransformation?                           | Input transformation                               | null                                       | No       |
| textStyle             | TextStyle                                      | Text style                                         | TextFieldDefaults.textStyle(backgroundMode) | No       |
| keyboardOptions       | KeyboardOptions                                | Keyboard options                                   | KeyboardOptions.Default                    | No       |
| onKeyboardAction      | KeyboardActionHandler?                         | Keyboard action handler                            | null                                       | No       |
| lineLimits            | TextFieldLineLimits                            | Line limits                                        | TextFieldLineLimits.Default                | No       |
| leadingIcon           | @Composable (() -> Unit)?                      | Leading icon                                       | null                                       | No       |
| trailingIcon          | @Composable (() -> Unit)?                      | Trailing icon                                      | null                                       | No       |
| onTextLayout          | Density.(getResult: () -> TextLayoutResult?) -> Unit | Text layout callback with density receiver    | null                                       | No       |
| interactionSource     | MutableInteractionSource?                      | Interaction source                                 | null                                       | No       |
| cursorBrush           | Brush                                          | Cursor brush                                       | SolidColor(colors.borderColor)             | No       |
| outputTransformation  | OutputTransformation?                          | Output transformation                              | null                                       | No       |
| scrollState           | ScrollState                                    | Scroll state                                       | rememberScrollState()                       | No       |

### TextFieldDefaults Object

The TextFieldDefaults object provides default values for TextField components.

#### Constants

| Constant Name    | Type     | Description                                        | Default Value         |
| ---------------- | -------- | -------------------------------------------------- | --------------------- |
| CornerRadius     | Dp       | Corner radius of the field                         | 10.dp                 |
| InsideMargin     | DpSize   | Internal padding in Rectangle mode                 | DpSize(16.dp, 12.dp)  |
| LineInsideMargin | DpSize   | Internal padding in Line mode                      | DpSize(0.dp, 15.dp)   |
| NoneInsideMargin | DpSize   | Internal padding in None mode                      | DpSize(0.dp, 9.dp)    |
| CounterFontSize  | TextUnit | Font size of the character counter                 | 10.sp                 |

#### `insideMargin()` function

`TextFieldDefaults.insideMargin(mode: TextFieldMode): DpSize` returns the default internal padding for the given background mode.

#### `textStyle()` function

`TextFieldDefaults.textStyle(mode: TextFieldMode): TextStyle` returns the default COUI input text style: 16sp regular, bold in `Rectangle` mode.

#### `textFieldColors()` factory

Builds a [TextFieldColors] instance. Override any subset; unspecified params fall back to the Miuix theme defaults.

| Parameter        | Type  | Default                                          |
| ---------------- | ----- | ------------------------------------------------ |
| backgroundColor  | Color | Color.Transparent (COUI rect mode is stroke-only) |
| labelColor       | Color | COUITheme.colorScheme.onSurfaceSecondary        |
| borderColor      | Color | COUITheme.colorScheme.primary                   |
| unfocusedBorderColor | Color | COUITheme.colorScheme.dividerLine           |
| errorColor       | Color | COUITheme.colorScheme.error                     |
| counterColor     | Color | COUITheme.colorScheme.onSurfaceContainerHigh    |
| iconColor        | Color | COUITheme.colorScheme.onSurfaceSecondary        |
| disabledTextColor | Color | COUITheme.colorScheme.disabledOnSurface        |

## Advanced Usage

### TextField with Icons

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = "Search",
    leadingIcon = {
        Icon(
            imageVector = COUIIcons.Search,
            contentDescription = "Search Icon",
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
)
```

### Password Input Field

```kotlin
var password by remember { mutableStateOf("") }
var passwordVisible by remember { mutableStateOf(false) }

TextField(
    value = password,
    onValueChange = { password = it },
    label = "Password",
    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
    trailingIcon = {
        IconButton(
            onClick = { passwordVisible = !passwordVisible },
            modifier = Modifier.padding(end = 12.dp)
        ) {
            Icon(
                imageVector = COUIIcons.Rename,
                tint = if (passwordVisible) COUITheme.colorScheme.primary else COUITheme.colorScheme.onSurfaceSecondary,
                contentDescription = if (passwordVisible) "Hide Password" else "Show Password"
            )
        }
    }
)
```

### Input Field with Validation

```kotlin
var email by remember { mutableStateOf("") }
var isError by remember { mutableStateOf(false) }
val errorColor = Color.Red.copy(0.3f)
val emailPattern = remember { Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+") }

Column {
    TextField(
        value = email,
        onValueChange = {
            email = it
            isError = email.isNotEmpty() && !emailPattern.matches(email)
        },
        label = "Email",
        colors = TextFieldDefaults.textFieldColors(
            labelColor = if (isError) errorColor else COUITheme.colorScheme.onSurfaceSecondary,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
    )
    if (isError) {
        Text(
            text = "Please enter a valid email address",
            color = errorColor,
            style = COUITheme.textStyles.body2,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }
}
```

### Custom Styles

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = "Custom Input Field",
    cornerRadius = 8.dp,
    colors = TextFieldDefaults.textFieldColors(
        backgroundColor = COUITheme.colorScheme.primary.copy(alpha = 0.1f),
    ),
    textStyle = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        color = COUITheme.colorScheme.primary
    )
)
```

### Using TextFieldValue

When you need more fine-grained control over text selection and cursor position:

```kotlin
var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }

TextField(
    value = textFieldValue,
    onValueChange = { textFieldValue = it },
    label = "Advanced Input Control",
    // TextFieldValue provides control over text, selection range, and cursor position
)
```
