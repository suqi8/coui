# NumberPicker

`NumberPicker` is a basic interactive component in COUI used for selecting a value from a range of numbers by vertical scrolling. It follows COUINumberPicker (ColorOS 17):

- Each row is drawn twice and hard-clipped at a 48dp band around the center (center ± 24dp): inside the band the text is 20dp in the selected color, outside it is 16dp in the unselected color. A row crossing the band edge is split; there is no gradual blend.
- Text sizes are in dp, so they do not follow the system font scale.
- A fling uses the Android spline at 1.6x the release velocity (clamped to 750..5000 px/s), lands on a whole row and finishes with a 300ms snap. Android uses these raw px/s values like COUI. Other platforms treat them as px on a 3.5x reference phone and scale them by the actual density (`value * density / 3.5`), so a flick travels as far as it does on the phone.
- `onValueChange` fires on every value change while the wheel moves, not only when it stops.
- Every value change plays a haptic tick whose intensity follows the wheel speed (50% at rest to 100% at 5000 px/s, COUI's "crisp" RichTap curve), at most one every 25ms. On Android 11+ apps with the `VIBRATE` permission it is a scaled `VibrationEffect` composition primitive; otherwise the fixed OPLUS 308 tick (or the standard haptic off Android).
- On Android every value change also plays COUI's `coui_numberpicker_click.ogg` while the system "touch sounds" setting is on; other platforms are silent.
- Rows are centered on the default typeface's top/bottom font metrics, like COUI (about 0.8dp higher than an ascent/descent center).
- Tapping the row above or below the center steps by one; holding it repeats the step every `longPressStepInterval` milliseconds.

Supports infinite scrolling with the `wrapAround` parameter.

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=numberPicker" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.NumberPicker
```

## Basic Usage

```kotlin
var value by remember { mutableIntStateOf(5) }

NumberPicker(
    value = value,
    onValueChange = { value = it },
    range = 0..10
)
```

## Component States

### Disabled State

As in COUI, a disabled picker keeps its colors and only ignores touch.

```kotlin
var value by remember { mutableIntStateOf(5) }

NumberPicker(
    value = value,
    onValueChange = { value = it },
    range = 0..10,
    enabled = false
)
```

### Infinite Scrolling

When `wrapAround` is enabled, the picker wraps from the last item back to the first, allowing continuous scrolling. As in COUI, wrapping only applies when the range has more items than `visibleItemCount`.

```kotlin
var value by remember { mutableIntStateOf(0) }

NumberPicker(
    value = value,
    onValueChange = { value = it },
    range = 0..23,
    wrapAround = true
)
```

## Properties

### NumberPicker Properties

| Property Name    | Type               | Description                                                              | Default Value              | Required |
| ---------------- | ------------------ | ------------------------------------------------------------------------ | -------------------------- | -------- |
| value            | Int                | Current selected value. If outside range, it will be coerced             | -                          | Yes      |
| onValueChange    | (Int) -> Unit      | Callback invoked when the selected value changes                         | -                          | Yes      |
| modifier         | Modifier           | Modifier applied to the picker                                           | Modifier                   | No       |
| enabled          | Boolean            | Whether the picker is enabled for user interaction                       | true                       | No       |
| range            | IntRange           | The range of selectable values                                           | 0..10                      | No       |
| label            | (Int) -> String    | Converts a value to its display string                                   | { it.toString() }          | No       |
| unit             | String             | Unit label drawn after the selected value; empty to hide                 | ""                         | No       |
| visibleItemCount | Int                | Number of visible items. Must be odd and at least 3                      | 3                          | No       |
| wrapAround       | Boolean            | Whether the picker wraps around (infinite scrolling)                     | false                      | No       |
| longPressStepInterval | Long          | Interval (ms) between auto-repeated steps while a side row is held       | NumberPickerDefaults.LongPressStepInterval | No |
| colors           | NumberPickerColors | Color configuration of the picker                                        | NumberPickerDefaults.colors() | No       |
| textStyle        | TextStyle          | Base text style; its font size is replaced by the two sizes below        | COUITheme.textStyles.title3 | No       |
| selectedTextSize | Dp                 | Text size inside the center band                                         | NumberPickerDefaults.SelectedTextSize | No |
| unselectedTextSize | Dp               | Text size outside the center band                                        | NumberPickerDefaults.UnselectedTextSize | No |
| itemHeight       | Dp                 | The height of each item in the picker                                    | NumberPickerDefaults.ItemHeight | No       |

### NumberPickerDefaults Object

The NumberPickerDefaults object provides default configurations for the NumberPicker component.

#### Properties

| Property Name | Type | Description                     | Default Value |
| ------------- | ---- | ------------------------------- | ------------- |
| ItemHeight    | Dp   | Default height of each item     | 45.dp         |
| SelectedTextSize | Dp | Text size inside the center band | 20.dp       |
| UnselectedTextSize | Dp | Text size outside the center band | 16.dp    |
| UnitTextSize  | Dp   | Text size of the unit label     | 14.dp         |
| LongPressStepInterval | Long | Auto-repeat interval (ms) while a side row is held | 300L |

#### Methods

| Method Name | Return Type        | Description                         |
| ----------- | ------------------ | ----------------------------------- |
| colors()    | NumberPickerColors | Creates default color configuration |

The `colors()` factory accepts the following parameters:

| Parameter Name              | Type  | Default Value                                  |
| --------------------------- | ----- | ---------------------------------------------- |
| selectedTextColor           | Color | COUITheme.colorScheme.onSurface                |
| unselectedTextColor         | Color | COUITheme.colorScheme.onSurfaceVariantActions  |
| disabledSelectedTextColor   | Color | selectedTextColor                              |
| disabledUnselectedTextColor | Color | unselectedTextColor                            |

### NumberPickerColors Class

| Property Name              | Type  | Description                                  |
| -------------------------- | ----- | -------------------------------------------- |
| selectedTextColor          | Color | Text color for the selected (center) item    |
| unselectedTextColor        | Color | Text color for unselected items              |
| disabledSelectedTextColor  | Color | Selected text color when disabled            |
| disabledUnselectedTextColor| Color | Unselected text color when disabled          |

## Advanced Usage

### Custom Label Format

```kotlin
var hour by remember { mutableIntStateOf(9) }

NumberPicker(
    value = hour,
    onValueChange = { hour = it },
    range = 0..23,
    label = { it.toString().padStart(2, '0') }  // "00", "01", ... "23"
)
```

### Unit Label

The unit is drawn in 14dp text after an 18dp value slot plus a 4dp margin, on the selected row's baseline lifted by 2dp.

```kotlin
var weight by remember { mutableIntStateOf(60) }

NumberPicker(
    value = weight,
    onValueChange = { weight = it },
    range = 30..150,
    unit = "kg"
)
```

### Time Picker

Combine two NumberPickers to create a time picker.

```kotlin
var hour by remember { mutableIntStateOf(16) }
var minute by remember { mutableIntStateOf(30) }

Row(
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically,
) {
    NumberPicker(
        value = hour,
        onValueChange = { hour = it },
        range = 0..23,
        label = { it.toString().padStart(2, '0') },
        wrapAround = true,
        modifier = Modifier.weight(1f),
    )
    Text(
        text = ":",
        fontWeight = FontWeight.Bold,
    )
    NumberPicker(
        value = minute,
        onValueChange = { minute = it },
        range = 0..59,
        label = { it.toString().padStart(2, '0') },
        wrapAround = true,
        modifier = Modifier.weight(1f),
    )
}
```

### Custom Visible Item Count

```kotlin
var value by remember { mutableIntStateOf(5) }

NumberPicker(
    value = value,
    onValueChange = { value = it },
    range = 1..100,
    visibleItemCount = 5 // Must be odd and at least 3
)
```

### Custom Colors

```kotlin
var value by remember { mutableIntStateOf(5) }

NumberPicker(
    value = value,
    onValueChange = { value = it },
    range = 0..10,
    colors = NumberPickerDefaults.colors(
        selectedTextColor = Color.Red,
        unselectedTextColor = Color.Gray
    )
)
```
