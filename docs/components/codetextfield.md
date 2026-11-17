# CodeTextField

A verification-code input that splits the code into individual cells, mirroring ColorOS's COUICodeInputView: 42x46dp card cells with an 8dp corner radius, a 1.6dp primary-colored stroke on the active cell, digits popping in with a 0.6 -> 1.0 scale + fade, and an optional security mode that shows dots instead of digits. Tapping anywhere focuses the hidden field; typing fills the cells from left to right, pasting distributes the pasted code into the cells, and backspace clears the last cell. A blinking cursor is shown in the active empty cell while focused.

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=codeTextField" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.CodeTextField
import io.github.suqi8.coui.kmp.basic.CodeTextFieldDefaults
```

## Basic Usage

```kotlin
var code by remember { mutableStateOf("") }

CodeTextField(
    value = code,
    onValueChange = { code = it.filter(Char::isDigit) },
    onComplete = { submit(it) },
    modifier = Modifier.fillMaxWidth(),
)
```

## Security Mode

```kotlin
var pin by remember { mutableStateOf("") }

CodeTextField(
    value = pin,
    onValueChange = { pin = it.filter(Char::isDigit) },
    cellCount = 4,
    security = true,
    modifier = Modifier.fillMaxWidth(),
)
```

## Component States

### Disabled State

A disabled field keeps its current code visible but cannot be focused or edited, and the cursor and active-cell stroke are hidden:

```kotlin
CodeTextField(
    value = code,
    onValueChange = { code = it },
    enabled = false,
    modifier = Modifier.fillMaxWidth(),
)
```

## Properties

### CodeTextField

| Property          | Type                      | Description                                        | Default Value                              | Required |
| ----------------- | ------------------------- | -------------------------------------------------- | ------------------------------------------ | -------- |
| value             | String                    | The code shown in the cells                        | -                                          | Yes      |
| onValueChange     | (String) -> Unit          | Callback with the sanitized code on change         | -                                          | Yes      |
| modifier          | Modifier                  | Modifier applied to the component                  | Modifier                                   | No       |
| cellCount         | Int                       | Number of code cells                               | CodeTextFieldDefaults.CellCount (6)        | No       |
| enabled           | Boolean                   | Whether the component is enabled                   | true                                       | No       |
| security          | Boolean                   | Show dots instead of digits                        | false                                      | No       |
| cellSize          | DpSize                    | Size of one cell before adaptive scaling           | CodeTextFieldDefaults.CellSize             | No       |
| cellCornerRadius  | Dp                        | Corner radius of one cell                          | CodeTextFieldDefaults.CellCornerRadius     | No       |
| colors            | CodeTextFieldColors       | Color configuration                                | CodeTextFieldDefaults.codeTextFieldColors() | No      |
| textStyle         | TextStyle                 | Text style of the cell digits                      | CodeTextFieldDefaults.textStyle()          | No       |
| keyboardOptions   | KeyboardOptions           | Keyboard options of the hidden field               | KeyboardOptions(keyboardType = KeyboardType.Number) | No |
| onComplete        | ((String) -> Unit)?       | Called with the full code once all cells are filled | null                                      | No       |
| interactionSource | MutableInteractionSource? | Interaction source                                 | null                                       | No       |

### CodeTextFieldDefaults

| Constant             | Type   | Description                                          | Default Value        |
| -------------------- | ------ | ---------------------------------------------------- | -------------------- |
| CellCount            | Int    | Default number of cells                              | 6                    |
| CellSize             | DpSize | Size of one cell                                     | DpSize(42.dp, 46.dp) |
| CellCornerRadius     | Dp     | Corner radius of one cell, drawn with the COUIShapePath Java path (`LegacySquircleShape`) like COUICodeInputView | 8.dp                 |
| MinCellSpacing       | Dp     | Minimum gap between adjacent cells                   | 4.dp                 |
| MaxCellSpacing       | Dp     | Maximum gap between adjacent cells                   | 16.dp                |
| DefaultCellSpacing   | Dp     | Gap between adjacent cells when the width is unbounded | 8.dp               |
| ContainerMarginHorizontal | Dp | Side margin subtracted on each side when the gap adapts | 16.dp          |
| CellStrokeWidth      | Dp     | Stroke width of the active-cell highlight            | 1.6.dp               |
| SecurityCircleRadius | Dp     | Radius of the dot shown in security mode             | 5.dp                 |
| CursorWidth          | Dp     | Width of the blinking cursor                         | 2.dp                 |
| ReferenceWidth       | Dp     | Window width below which cells scale down            | 360.dp               |

#### Methods

| Method Name           | Type                | Description                                                                       |
| --------------------- | ------------------- | --------------------------------------------------------------------------------- |
| textStyle()           | TextStyle           | Default digit style: 30dp regular (coui_code_input_cell_text_size is a dp size, so digits ignore the font scale) |
| codeTextFieldColors() | CodeTextFieldColors | Creates the color configuration for the code text field                           |

### `codeTextFieldColors()` factory

| Parameter           | Type  | Default                                          |
| ------------------- | ----- | ------------------------------------------------ |
| cellBackgroundColor | Color | COUITheme.colorScheme.cardBackground          |
| textColor           | Color | COUITheme.colorScheme.labelPrimary                 |
| focusedStrokeColor  | Color | COUITheme.colorScheme.primary                   |
| securityCircleColor | Color | COUITheme.colorScheme.labelPrimary (alpha 0.847)   |
| cursorColor         | Color | COUITheme.colorScheme.primary                   |

## Behavior

- `onValueChange` always receives the sanitized code: whitespace stripped and truncated to `cellCount`.
- A multi-character insertion (paste) replaces the whole code and is distributed into the cells, like COUICodeInputView's TextWatcher.
- The active cell (first empty one, or the last cell when full) shows a 1.6dp stroke while focused; the stroke fades in over 100ms with a 33ms delay and fades out over 100ms (COUI move ease 0.3, 0, 0.1, 1).
- Digits appear with a 0.6 -> 1.0 scale + fade over 100ms and fade out with a 33ms delay; security mode skips digit animations, mirroring COUI.
- On windows narrower than 360dp the cells shrink by `windowWidth / 360dp` (COUICodeInputView scales by the screen width, not the component width). Each cell then gets a per-side margin of `round((width - n * cellWidth - 2 * 16dp) / (2n - 2))` clamped to 2..8dp, so the gap between cells adapts within 4dp and 16dp.

## Advanced Usage

### Custom Cell Style

```kotlin
CodeTextField(
    value = code,
    onValueChange = { code = it },
    cellCount = 4,
    cellSize = DpSize(48.dp, 52.dp),
    cellCornerRadius = 12.dp,
    colors = CodeTextFieldDefaults.codeTextFieldColors(
        focusedStrokeColor = COUITheme.colorScheme.secondary,
    ),
    modifier = Modifier.fillMaxWidth(),
)
```
