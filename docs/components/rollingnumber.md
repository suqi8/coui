# RollingNumber

`RollingNumber` shows an integer whose digits roll like a drum when it changes, the port of the camera's `RollingNumView` (the burst-shot counter) and `PaletteRollingNumView` (the palette and filter strength values). Every digit sits in a cell as wide as the style's "0", so the number keeps its width while it counts.

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=rollingNumber" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.RollingNumber
import io.github.suqi8.coui.kmp.basic.RollingNumberDefaults
```

## Basic Usage

```kotlin
var count by remember { mutableIntStateOf(8) }

RollingNumber(value = count)
```

### The Roll

- Each digit place whose digit changed rolls in 200ms on (0.6, 0, 0.3, 1): the old digit slides away by `rollDistance`, tilting back 45° and fading out, while the new digit slides in from the other side, tilting from 45° to flat and fading in.
- Rising values roll upwards, falling values downwards.
- A place that changes again mid-roll restarts from the digit it was rolling to.
- When the number of digits or the sign changes (9 → 10, 1 → -1), the whole number rolls at once.

### The Ones Digit

Like the camera, the ones digit does not roll by default: it changes on every step, so it switches at once and only the carries (tens, hundreds, ...) roll. Roll every place with `rollOnesDigit`:

```kotlin
RollingNumber(value = count, rollOnesDigit = true)
```

### Burst Counter

The camera's burst-shot counter: two digits (`minDigits = 2`), 14dp white text rolling 13dp, in a 32dp disc of 50% black:

```kotlin
Box(
    modifier = Modifier
        .size(RollingNumberDefaults.BurstBadgeSize)
        .background(Color(0x7F000000), CircleShape),
    contentAlignment = Alignment.Center,
) {
    RollingNumber(
        value = shots,
        minDigits = 2,
        color = Color.White,
        style = COUITheme.textStyles.bodyXL.copy(fontSize = 14.sp, fontWeight = FontWeight.Medium),
        rollDistance = RollingNumberDefaults.BurstRollDistance,
    )
}
```

### Negative Values

Negative values lead with a minus sign; crossing zero rolls the whole number:

```kotlin
RollingNumber(value = tone) // -100..100
```

## Properties

### RollingNumber Properties

| Property Name | Type      | Description                                                         | Default Value                    | Required |
| ------------- | --------- | ------------------------------------------------------------------- | -------------------------------- | -------- |
| value         | Int       | The number to show                                                  | -                                | Yes      |
| modifier      | Modifier  | Modifier applied to the number                                      | Modifier                         | No       |
| rollOnesDigit | Boolean   | Whether the ones digit rolls too                                    | false                            | No       |
| minDigits     | Int       | Minimum digits, padded with leading zeros (1..10)                   | 1                                | No       |
| color         | Color     | Digit color; falls back to the style color, then `LocalContentColor` | Color.Unspecified               | No       |
| style         | TextStyle | Text style of the digits                                            | COUITheme.textStyles.bodyXL      | No       |
| rollDistance  | Dp        | How far a digit travels; unspecified uses the font size             | Dp.Unspecified                   | No       |

## Defaults Object

### RollingNumberDefaults

| Property Name       | Type  | Description                                 | Value  |
| ------------------- | ----- | ------------------------------------------- | ------ |
| RollDurationMillis  | Int   | Roll duration of one digit place            | 200    |
| RollAngle           | Float | Tilt of a digit at either end of its roll   | 45f    |
| BurstTextSize       | Dp    | Burst-shot counter digit size               | 14.dp  |
| BurstRollDistance   | Dp    | Burst-shot counter roll distance            | 13.dp  |
| BurstBadgeSize      | Dp    | Burst-shot counter disc                     | 32.dp  |
| PaletteTextSize     | Dp    | Palette value digit size                    | 10.dp  |
| PaletteRollDistance | Dp    | Palette value roll distance                 | 12.dp  |
