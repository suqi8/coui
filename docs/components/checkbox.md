# Checkbox

`Checkbox` is a basic selection component in COUI, supporting three states: checked, unchecked, and indeterminate. It provides an interactive selection control with animation effects, suitable for multiple selection scenarios and enabling/disabling configuration items.

<div style="position: relative; height: 100px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=checkbox" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.Checkbox
import io.github.suqi8.coui.kmp.basic.CheckboxStyle
import androidx.compose.ui.state.ToggleableState
```

## Basic Usage

The Checkbox component can be used to create selectable options:

```kotlin
var state by remember { mutableStateOf(ToggleableState.Off) }

Checkbox(
    state = state,
    onClick = { state = if (state == ToggleableState.On) ToggleableState.Off else ToggleableState.On }
)
```

## Component States

### Disabled State

```kotlin
Checkbox(
    state = ToggleableState.Off,
    onClick = null,
    enabled = false
)
```

### Indeterminate State

The indeterminate state is typically used to represent a parent checkbox where only some child checkboxes are selected. When indeterminate, a horizontal dash is shown instead of a checkmark.

```kotlin
var parentState by remember { mutableStateOf(ToggleableState.Indeterminate) }

// Cycle: Off → Indeterminate → On → Off
Checkbox(
    state = parentState,
    onClick = {
        parentState = when (parentState) {
            ToggleableState.Off -> ToggleableState.Indeterminate
            ToggleableState.Indeterminate -> ToggleableState.On
            ToggleableState.On -> ToggleableState.Off
        }
    }
)
```

## Properties

### Checkbox Properties

| Property Name | Type                | Description                                                    | Default Value                     | Required |
| ------------- | ------------------- | -------------------------------------------------------------- | --------------------------------- | -------- |
| state         | ToggleableState     | The current state of the Checkbox (On, Off, or Indeterminate)  | -                                 | Yes      |
| onClick       | (() -> Unit)?       | Callback when the Checkbox is clicked; `null` = non-interactive | -                                | Yes      |
| modifier      | Modifier            | Modifier applied to the Checkbox                               | Modifier                          | No       |
| colors        | CheckboxColors      | Color configuration for the Checkbox                           | CheckboxDefaults.checkboxColors() | No       |
| enabled       | Boolean             | Whether the Checkbox is interactive                            | true                              | No       |
| style         | CheckboxStyle       | The COUI drawable set: `Default`, or `Shape` over images       | CheckboxStyle.Default             | No       |

### CheckboxStyle

| Value   | Description                                                                                                                         |
| ------- | ----------------------------------------------------------------------------------------------------------------------------------- |
| Default | The regular COUI checkbox (`couiCheckBoxStyle`, `coui_checkbox_state`)                                                              |
| Shape   | The COUI shape checkbox laid over images (`couiShapeCheckBoxStyle`, `coui_shape_checkbox_state`); use it with `shapeCheckboxColors()` |

### CheckboxDefaults Object

The CheckboxDefaults object provides default color configurations for the Checkbox component.

#### Methods

| Method Name           | Type           | Description                                                                   |
| --------------------- | -------------- | ----------------------------------------------------------------------------- |
| checkboxColors()      | CheckboxColors | Creates default color config for checkbox                                     |
| shapeCheckboxColors() | CheckboxColors | Colors of the COUI shape checkbox laid over images (`couiShapeCheckBoxStyle`) |

### CheckboxColors Class

| Property Name                    | Type  | Description                                  |
| -------------------------------- | ----- | -------------------------------------------- |
| checkedForegroundColor           | Color | Foreground color when checked (checkmark)         |
| disabledCheckedForegroundColor   | Color | Foreground color when disabled and checked        |
| checkedBackgroundColor           | Color | Background color when checked                     |
| uncheckedBackgroundColor         | Color | Ring color when unchecked                         |
| disabledCheckedBackgroundColor   | Color | Background color when disabled and checked        |
| disabledUncheckedBackgroundColor | Color | Ring color when disabled and unchecked            |
| uncheckedFillColor               | Color | Fill inside the ring when unchecked (transparent) |
| disabledUncheckedFillColor       | Color | Fill inside the ring when disabled and unchecked  |

> The indeterminate state shares the same colors as the checked state.

Default colors follow COUI 17 (`coui_btn_check_*.xml`): the unchecked ring is `labelTertiary` (#42000000 / #4DFFFFFF) and `quarternaryNeutral` (#14000000 / #26FFFFFF) when disabled; the checked fill is `primary`, `disable` when disabled; the checkmark is white, `disabledOnPrimaryButton` (#8AFFFFFF / #29FFFFFF) when disabled.

### Press Feedback

Like `COUICheckBox`, the box never shrinks on press. An interactive checkbox (`onClick != null`) shows a 40dp circular highlight in `press` (#1F000000 / #33FFFFFF) behind the 24dp box, following the platform ripple timing; the same circle takes the `hover` and keyboard-focus (`focus`) layers of COUI's `COUIMaskRippleDrawable` on response 0.3 springs. Pass `onClick = null` when a surrounding row handles the click (as `CheckboxPreference` does) so only the row shows its press state. Like `COUICheckBox`, the standalone checkbox plays no haptic feedback.

State changes replay the COUI AVDs: the fill fades, the checkmark draws or erases itself, the indeterminate dash grows from or retracts to its left end (a growing dash is the overlap of an eased clip and a linear path morph, so it grows almost linearly), and the box, the checkmark and the dash each pop 1 -> 1.08 -> 1 on their own easings (the checkmark stays unscaled between indeterminate and checked). They replay only while enabled; a disabled checkbox jumps to its new state, except between indeterminate and checked, which COUI also animates when disabled. At rest the indeterminate box follows COUI's static drawable: it sits 0.666dp lower than the other states, with a 1.6dp dash (1.5dp when disabled).

## Advanced Usage

### Shape Checkbox

Over images or colored backdrops, use the COUI shape checkbox (`Widget.COUI.CompoundButton.COUICheckBox.Shape`): pass `style = CheckboxStyle.Shape` together with `CheckboxDefaults.shapeCheckboxColors()`. The unchecked box is a black 40% fill inside a white 40% ring (black 8% inside a white 30% ring when disabled), the same in light and dark themes; the checked and indeterminate states look like the default ones.

The shape style also plays COUI's own, quicker shape AVDs (`coui_shape_checkbox_*`) from and to unchecked: the box pops to 1.2 within 20-33ms and settles over the next 160-167ms, the fill fades in or out within a few frames, the 1.5dp checkmark draws or erases itself after 33ms, and the indeterminate state grows a round-capped dash from its left end (and retracts it the same way). Indeterminate <-> checked reuse the default AVDs, and a disabled shape checkbox never animates.

```kotlin
Checkbox(
    state = state,
    onClick = { state = if (state == ToggleableState.On) ToggleableState.Off else ToggleableState.On },
    colors = CheckboxDefaults.shapeCheckboxColors(),
    style = CheckboxStyle.Shape
)
```

### Custom Colors

```kotlin
var state by remember { mutableStateOf(ToggleableState.Off) }

Checkbox(
    state = state,
    onClick = { state = if (state == ToggleableState.On) ToggleableState.Off else ToggleableState.On },
    colors = CheckboxDefaults.checkboxColors(
        checkedBackgroundColor = Color.Red,
        checkedForegroundColor = Color.White
    )
)
```

### Parent-Child Checkboxes (Indeterminate Pattern)

```kotlin
val childStates = remember { mutableStateListOf(false, true, false) }
val parentState by remember {
    derivedStateOf {
        when {
            childStates.all { it } -> ToggleableState.On
            childStates.none { it } -> ToggleableState.Off
            else -> ToggleableState.Indeterminate
        }
    }
}

Column {
    Checkbox(
        state = parentState,
        onClick = {
            val newValue = parentState != ToggleableState.On
            childStates.indices.forEach { childStates[it] = newValue }
        }
    )
    childStates.forEachIndexed { index, checked ->
        Checkbox(
            state = ToggleableState(checked),
            onClick = { childStates[index] = !childStates[index] }
        )
    }
}
```

### Using with Text

```kotlin
var state by remember { mutableStateOf(ToggleableState.Off) }

Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(16.dp)
) {
    Checkbox(
        state = state,
        onClick = { state = if (state == ToggleableState.On) ToggleableState.Off else ToggleableState.On }
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(text = "Accept Terms and Privacy Policy")
}
```

### Using in Lists

```kotlin
val options = listOf("Option A", "Option B", "Option C")
val checkedStates = remember { mutableStateListOf(false, true, false) }

LazyColumn {
    items(options.size) { index ->
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                state = ToggleableState(checkedStates[index]),
                onClick = { checkedStates[index] = !checkedStates[index] }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = options[index])
        }
    }
}
```
