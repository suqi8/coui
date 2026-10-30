# StepperPreference

`StepperPreference` is a preference row with a [Stepper](./stepper.md) at the end, mirroring ColorOS's `COUIStepperPreference`. COUI keeps the plain preference layout and only swaps in the stepper widget, forwarding the minimum (default -999), maximum (default 9999) and step (couiUnit, default 1). Like COUI's unpadded widget frame, the 32dp stepper may reach into the row's vertical padding, so the row stays as tall as a plain preference (48dp).

<div style="position: relative; height: 160px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=stepperPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.preference.StepperPreference
```

## Basic Usage

```kotlin
var copies by remember { mutableIntStateOf(1) }

StepperPreference(
    value = copies,
    onValueChange = { copies = it },
    title = "Copies",
    minValue = 1,
    maxValue = 10
)
```

## Component States

### Custom Step

```kotlin
StepperPreference(
    value = fontScale,
    onValueChange = { fontScale = it },
    title = "Font scale",
    summary = "Steps of 10, from 50 to 200",
    minValue = 50,
    maxValue = 200,
    step = 10
)
```

### Disabled State

```kotlin
StepperPreference(
    value = 3,
    onValueChange = {},
    title = "Disabled",
    enabled = false
)
```

## Properties

### StepperPreference Properties

| Property Name    | Type                      | Description                              | Default Value                          | Required |
| ---------------- | ------------------------- | ---------------------------------------- | -------------------------------------- | -------- |
| value            | Int                       | Current value                            | -                                      | Yes      |
| onValueChange    | (Int) -> Unit             | Called with the new value on each step   | -                                      | Yes      |
| title            | String                    | Row title                                | -                                      | Yes      |
| modifier         | Modifier                  | Component modifier                       | Modifier                               | No       |
| titleColor       | BasicComponentColors      | Title color                              | BasicComponentDefaults.titleColor()    | No       |
| summary          | String?                   | Row summary                              | null                                   | No       |
| summaryColor     | BasicComponentColors      | Summary color                            | BasicComponentDefaults.summaryColor()  | No       |
| minValue         | Int                       | Minimum value (couiMinimum)              | StepperDefaults.MinValue (-999)        | No       |
| maxValue         | Int                       | Maximum value (couiMaximum)              | StepperDefaults.MaxValue (9999)        | No       |
| step             | Int                       | Step size (couiUnit)                     | 1                                      | No       |
| stepperColors    | StepperColors             | Stepper colors                           | StepperDefaults.stepperColors()        | No       |
| startAction      | @Composable (() -> Unit)? | Start side content                       | null                                   | No       |
| bottomAction     | @Composable (() -> Unit)? | Bottom content                           | null                                   | No       |
| insideMargin     | PaddingValues             | Row inside margin                        | BasicComponentDefaults.InsideMargin    | No       |
| cardListPosition | CardListPosition          | Row position inside its card group       | LocalCardListPosition.current          | No       |
| enabled          | Boolean                   | Whether the row and stepper are enabled  | true                                   | No       |
