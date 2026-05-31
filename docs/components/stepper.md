# Stepper

A stepper lets the user adjust an integer value in fixed increments using a minus and a plus button, mirroring ColorOS's COUIStepperView (32dp circular buttons with the OPLUS step haptic).

## Import

```kotlin
import top.yukonga.miuix.kmp.basic.Stepper
import top.yukonga.miuix.kmp.basic.StepperDefaults
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
| minValue      | Int             | Minimum reachable value                       | Int.MIN_VALUE                | No       |
| maxValue      | Int             | Maximum reachable value                       | Int.MAX_VALUE                | No       |
| step          | Int             | Increment/decrement per press                 | 1                            | No       |
| enabled       | Boolean         | Whether the stepper is enabled                | true                         | No       |
| colors        | StepperColors   | Color configuration                           | StepperDefaults.stepperColors() | No    |

### StepperDefaults

| Constant          | Type | Default Value |
| ----------------- | ---- | ------------- |
| ButtonSize        | Dp   | 32.dp         |
| GlyphSize         | Dp   | 18.dp         |
| Spacing           | Dp   | 4.dp          |
| IndicatorMinWidth | Dp   | 40.dp         |

### `stepperColors()` factory

| Parameter            | Type  | Default                                   |
| -------------------- | ----- | ----------------------------------------- |
| buttonColor          | Color | MiuixTheme.colorScheme.secondaryContainer |
| contentColor         | Color | MiuixTheme.colorScheme.onSurface          |
| disabledContentColor | Color | MiuixTheme.colorScheme.disabledOnSurface  |

## Behavior

- Pressing − or + changes the value by `step`, clamped to `[minValue, maxValue]`.
- A button is automatically disabled when the value reaches its bound.
- Each successful step fires the COUI step haptic (OPLUS constant 308 on ColorOS, the closest standard haptic elsewhere).
