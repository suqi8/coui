# InputPreference

`InputPreference` is a card list row that holds an inline single-line input field, mirroring ColorOS's `COUIInputPreference`. An optional title sits above the field; the field draws no resting underline, only the focused line (`couiJustShowFocusLine`), and the last row of a card group draws no line at all. The row has no press state.

<div style="position: relative; height: 200px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=inputPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.preference.InputPreference
import io.github.suqi8.coui.kmp.preference.InputPreferenceDefaults
```

## Basic Usage

```kotlin
var name by remember { mutableStateOf("My Phone") }

Card {
    CardListGroup {
        item {
            InputPreference(
                value = name,
                onValueChange = { name = it },
                title = "Device name",
                hint = "Enter a name"
            )
        }
    }
}
```

Without a title the field is padded 13dp above and below; below a title it is padded 0dp / 11dp.
The field has no minimum height (Widget.COUI.Input), its hint uses couiColorHintNeutral, and it
shows a clear button while focused and not empty, as COUIInputPreference does by default
(`quickDelete` inherited from Widget.COUI.Input); pass `showClearButton = false` to hide it.

## Component States

### Counter and Error

```kotlin
var hotspot by remember { mutableStateOf("") }

InputPreference(
    value = hotspot,
    onValueChange = { hotspot = it },
    hint = "Hotspot name",
    maxCount = 20,
    errorMessage = if (hotspot.contains(' ')) "Spaces are not allowed" else ""
)
```

COUI draws no list divider below a row in error state (`COUIInputPreference.drawDivider`); when you place `HorizontalDivider`s between rows, skip the one below an erroring row.

### Disabled State

```kotlin
InputPreference(
    value = "Read only",
    onValueChange = {},
    title = "Serial number",
    enabled = false
)
```

## Properties

### InputPreference Properties

| Property Name        | Type                  | Description                                                        | Default Value                                    | Required |
| -------------------- | --------------------- | ------------------------------------------------------------------ | ------------------------------------------------ | -------- |
| value                | String                | Text of the field                                                  | -                                                | Yes      |
| onValueChange        | (String) -> Unit      | Callback when the text changes                                     | -                                                | Yes      |
| modifier             | Modifier              | Component modifier                                                 | Modifier                                         | No       |
| title                | String?               | Title above the field                                              | null                                             | No       |
| hint                 | String                | Hint shown while the field is empty                                | ""                                               | No       |
| enabled              | Boolean               | Whether the row is enabled                                         | true                                             | No       |
| readOnly             | Boolean               | Whether the field is read-only                                     | false                                            | No       |
| justShowFocusLine    | Boolean               | Show only the focused underline (no resting line)                  | true                                             | No       |
| maxCount             | Int?                  | Maximum characters; shows a `count/max` counter                    | null                                             | No       |
| showClearButton      | Boolean               | Show the clear button while focused and not empty                  | true                                             | No       |
| showPasswordToggle   | Boolean               | Show the password eye toggle                                       | false                                            | No       |
| errorMessage         | String                | Error text below the field; empty hides it                         | ""                                               | No       |
| colors               | InputPreferenceColors | Title / error colors                                               | InputPreferenceDefaults.inputPreferenceColors()  | No       |
| textFieldColors      | TextFieldColors       | Field colors                                                       | InputViewDefaults.textFieldColors()              | No       |
| textStyle            | TextStyle             | Input text style                                                   | InputViewDefaults.textStyle()                    | No       |
| keyboardOptions      | KeyboardOptions       | Keyboard options                                                   | KeyboardOptions.Default                          | No       |
| keyboardActions      | KeyboardActions       | Keyboard actions                                                   | KeyboardActions.Default                          | No       |
| visualTransformation | VisualTransformation  | Visual transformation of the field                                 | VisualTransformation.None                        | No       |
| insideMargin         | PaddingValues         | Margin between the card edge and the content                       | InputPreferenceDefaults.InsideMargin             | No       |
| cardListPosition     | CardListPosition      | Row position inside its card group; the last row draws no line    | LocalCardListPosition.current                    | No       |
| interactionSource    | MutableInteractionSource? | Interaction source of the field                                | null                                             | No       |

### InputPreferenceDefaults Object

| Constant                    | Type          | Default Value                       | Description                                                    |
| --------------------------- | ------------- | ----------------------------------- | -------------------------------------------------------------- |
| InsideMargin                | PaddingValues | PaddingValues(horizontal = 16.dp)   | 32dp row padding minus the 16dp card inset                     |
| TitlePaddingTop             | Dp            | 10.dp                               | coui_input_preference_title_padding_top                        |
| TitlePaddingBottom          | Dp            | 4.dp                                | coui_input_preference_title_padding_bottom                     |
| TitleMinHeight              | Dp            | 22.dp                               | coui_inputView_title_minheight                                 |
| FieldPaddingVertical        | Dp            | 13.dp                               | coui_input_edit_text_no_title_padding_*_inPreference           |
| FieldPaddingBottomWithTitle | Dp            | 11.dp                               | coui_input_edit_error_text_has_title_padding_bottom            |
| ErrorFontSize               | TextUnit      | 10.sp                               | couiTextAppearanceCaption                                      |
| ErrorPaddingVertical        | Dp            | 4.dp                                | Error line padding                                             |

#### `inputPreferenceColors()` factory

| Parameter          | Type  | Default                                   |
| ------------------ | ----- | ----------------------------------------- |
| titleColor         | Color | COUITheme.colorScheme.labelPrimary           |
| disabledTitleColor | Color | COUITheme.colorScheme.disabledNeutral   |
| errorColor         | Color | COUITheme.colorScheme.error               |
