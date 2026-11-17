# NumericKeyboard

`NumericKeyboard` reproduces the ColorOS secure numeric keyboard (`COUINumericKeyboard`): digits on round 68dp keys in a 3 x 4 grid, with a side key in each bottom corner. It has two press effects: the ripple of the Settings keyboard and the glass keys of the lock screen, where the pressed key springs down and lights its rim and its neighbours.

<div style="position: relative; height: 460px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=numericKeyboard" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.NumericKeyboard
import io.github.suqi8.coui.kmp.basic.NumericKeyboardDefaults
import io.github.suqi8.coui.kmp.basic.NumericKeyboardPressEffect
```

## Basic Usage

```kotlin
var input by remember { mutableStateOf("") }

NumericKeyboard(
    onNumberClick = { input += it },
    rightKey = NumericKeyboardDefaults.deleteKey(), // both corners are empty by default
    onRightKeyClick = { input = input.dropLast(1) },
)
```

## Side Keys

A side key shows an icon or a bold label. Changing a side key after the first composition fades it in or out:

```kotlin
NumericKeyboard(
    onNumberClick = { input += it },
    leftKey = if (input.isNotEmpty()) NumericKeyboardDefaults.finishKey() else null,
    rightKey = NumericKeyboardDefaults.deleteKey(),
    onLeftKeyClick = { submit(input) },
    onRightKeyClick = { input = input.dropLast(1) },
)
```

## Lock-Screen Keys

```kotlin
NumericKeyboard(
    onNumberClick = { pin += it },
    pressEffect = NumericKeyboardPressEffect.Light,
    colors = NumericKeyboardDefaults.lockScreenColors(),
)
```

## Properties

### NumericKeyboard

| Property              | Type                       | Description                                     | Default Value                        | Required |
| --------------------- | -------------------------- | ----------------------------------------------- | ------------------------------------ | -------- |
| onNumberClick         | (Int) -> Unit              | Called with the digit of a released key         | -                                    | Yes      |
| modifier              | Modifier                   | Modifier (default 248 x 326dp)                  | Modifier                             | No       |
| state                 | NumericKeyboardState       | Key animations, `animateEnter()`                | rememberNumericKeyboardState()       | No       |
| enabled               | Boolean                    | Whether keys accept touches                     | true                                 | No       |
| hapticFeedbackEnabled | Boolean                    | Keyboard haptic on touch down                   | true                                 | No       |
| leftKey               | NumericKeyboardSideKey?    | Bottom-left key, null for an empty cell         | null                                 | No       |
| rightKey              | NumericKeyboardSideKey?    | Bottom-right key, null for an empty cell        | null                                 | No       |
| pressEffect           | NumericKeyboardPressEffect | `Ripple` or `Light`                             | Ripple                               | No       |
| keySize               | Dp                         | Key diameter                                    | 68.dp                                | No       |
| colors                | NumericKeyboardColors      | Key colors                                      | NumericKeyboardDefaults.colors()     | No       |
| onLeftKeyClick        | () -> Unit                 | Left key released                               | {}                                   | No       |
| onRightKeyClick       | () -> Unit                 | Right key released                              | {}                                   | No       |

### NumericKeyboardSideKey

| Property           | Type          | Description                                          | Default Value |
| ------------------ | ------------- | ---------------------------------------------------- | ------------- |
| icon               | ImageVector?  | Icon, drawn with its own colors unless `tintIcon`   | null          |
| text               | String?       | Bold label, used when there is no icon               | null          |
| contentDescription | String?       | Accessibility description                            | text          |
| textColor          | Color         | Label color; unspecified uses `wordTextColor`        | Unspecified   |
| textSize           | Dp            | Label size                                           | 16.dp         |
| tintIcon           | Boolean       | Tint the icon with the number color (`deleteKey()` sets it) | false  |

### NumericKeyboardDefaults

| Constant                | Type        | Default Value | COUI Source                              |
| ----------------------- | ----------- | ------------- | ---------------------------------------- |
| Width                   | Dp          | 248.dp        | coui_numeric_keyboard_view_width         |
| Height                  | Dp          | 326.dp        | coui_numeric_keyboard_view_height        |
| KeySize                 | Dp          | 68.dp         | coui_numeric_keyboard_view_size          |
| NumberTextSize          | Dp          | 32.dp         | number_keyboard_number_size              |
| NumberOffsetY           | Dp          | 1.dp          | coui_numeric_keyboard_number_offset_y    |
| SideTextSize            | Dp          | 16.dp         | coui_number_keyboard_finish_text_size    |
| AdditionalPressableArea | Dp          | 32.dp         | coui_additional_pressable_area           |
| LightShaderRadius       | Dp          | 124.dp        | coui_numeric_light_shader_radius         |
| EnterTranslateY         | Dp          | 258.dp        | coui_numeric_keyboard_max_translate_y    |
| DeleteIcon              | ImageVector | 25 x 14dp     | ic_coui_number_keyboard_setting_delete   |

| Method             | Description                                                                                                  |
| ------------------ | ------------------------------------------------------------------------------------------------------------ |
| colors()           | Settings keyboard: black / white digits on `cardBackground` / #8C3D3D3D keys (place it on the page `backgroundWithCard`, not on a card), side keys unfilled, ripple 6% / 12%, label #2AD181 |
| lockScreenColors() | Lock screen: white digits, no key fill, white ripple 12%                                                      |
| deleteKey()        | The delete side key                                                                                           |
| finishKey()        | The "Done" side key                                                                                           |

### NumericKeyboardColors

| Property              | Type  | Description                                     |
| --------------------- | ----- | ----------------------------------------------- |
| numberColor           | Color | Digits and side icons                           |
| numberBackgroundColor | Color | Digit key fill (ripple effect)                  |
| sideBackgroundColor   | Color | Side key fill                                   |
| pressedColor          | Color | Ripple color                                    |
| circleMaxAlpha        | Float | Peak ripple alpha                               |
| wordTextColor         | Color | Default side label color                        |

## Behavior

- A key fires on release when the finger is still on it; sliding off cancels it, and so does a scrolling parent (a list or a pager) taking the drag over, so the keyboard can sit in a scrolling page. Several fingers can press keys at once. The hit area extends up to 32dp around each key, capped at half the spacing.
- Ripple: on press a disc grows from 47% to 100% of the key over 100ms; on release it grows to 117% and fades over 160ms while a soft halo spreads to twice the key and fades over 400ms.
- Light: the pressed key springs to the COUI press scale; its rim glows (inner light) and the keys within 124dp catch a radial light, both on response 0.3 springs (0.6 for the outer light on release). Every digit has a two-layer inner shadow and a 0.6dp border; on Android before 12, where layer blur is unavailable, the inner shadow is drawn with a blur mask instead (same radius and offsets).
- The digits are 32dp medium, not scaled by the font size setting.
