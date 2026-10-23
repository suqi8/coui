# Stepper

A stepper lets the user adjust an integer value in fixed increments using a minus and a plus button, mirroring ColorOS 17's COUIStepperView: flat 32dp circular buttons with a press mask and press scale, long-press auto-repeat and the OPLUS step haptic.

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.Stepper
import io.github.suqi8.coui.kmp.basic.StepperDefaults
```

## Basic Usage

```kotlin
var value by remember { mutableIntStateOf(2) }

Stepper(
    value = value,
    onValueChange = { value = it },
    minValue = 0,
    maxValue = 10,
)
```

## Properties

### Stepper

| Property      | Type            | Description                                   | Default Value                | Required |
| ------------- | --------------- | --------------------------------------------- | ---------------------------- | -------- |
| value         | Int             | Current value                                 | -                            | Yes      |
| onValueChange | (Int) -> Unit   | Callback with the new value on step           | -                            | Yes      |
| modifier      | Modifier        | Modifier applied to the stepper               | Modifier                     | No       |
| minValue      | Int             | Minimum reachable value                       | StepperDefaults.MinValue (-999) | No    |
| maxValue      | Int             | Maximum reachable value                       | StepperDefaults.MaxValue (9999) | No    |
| step          | Int             | Increment/decrement per step                  | 1                            | No       |
| enabled       | Boolean         | Whether the stepper is enabled                | true                         | No       |
| colors        | StepperColors   | Color configuration                           | StepperDefaults.stepperColors() | No    |

### StepperDefaults

| Constant          | Type | Default Value |
| ----------------- | ---- | ------------- |
| ButtonSize        | Dp   | 32.dp         |
| GlyphSize         | Dp   | 15.2.dp       |
| GlyphStrokeWidth  | Dp   | 1.6.dp        |
| Spacing           | Dp   | 12.dp         |
| IndicatorMinWidth | Dp   | 44.dp         |
| MinValue          | Int  | -999          |
| MaxValue          | Int  | 9999          |
| RepeatIntervalMillis | Long | 100L       |

### `stepperColors()` factory

| Parameter            | Type  | Default                                   |
| -------------------- | ----- | ----------------------------------------- |
| buttonColor          | Color | COUITheme.colorScheme.dialogButton (couiColorContainer8) |
| contentColor         | Color | COUITheme.colorScheme.onSurface          |
| disabledContentColor | Color | COUITheme.colorScheme.dividerLine (couiColorDivider) |

## Behavior

- A tap on − or + changes the value by `step` when the finger lifts, clamped to `[minValue, maxValue]`.
- Holding a button steps once after the long-press timeout, then every 100ms until the finger lifts or the bound is reached.
- A button is automatically disabled when the value reaches its bound; only its glyph turns to the disabled color. The value text never dims, also when `enabled = false`.
- The buttons are flat `couiColorContainer8` circles with the `couiColorPress` press mask and a size-derived press scale (about 0.88 for 32dp). They have no material, rubber band or deformation.
- The value area has a fixed width: the widest digit repeated once per character of `maxValue`, and at least 44dp. The row does not change width as the value changes.
- Each step fires the COUI step haptic (OPLUS constant 308 on ColorOS, the closest standard haptic elsewhere), including every repeated step.
