# Switch

`Switch` is a basic toggle component in COUI used to switch between two states. It provides an interactive switch control with animation effects, suitable for enabling and disabling settings.

<div style="position: relative; height: 100px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=switch" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.Switch
```

## Basic Usage

The Switch component can be used to toggle between two states:

```kotlin
var checked by remember { mutableStateOf(false) }

Switch(
    checked = checked,
    onCheckedChange = { checked = it }
)
```

## Interaction

`Switch` follows the COUI 17 `COUISwitch`:

- The 44 x 24 dp track holds an 18 dp thumb that travels 20 dp.
- Toggling moves the thumb on a spring (response 0.4, bounce 0.3). The thumb stretches with its position, up to 1.3x about a third of the way, and the track colour blends with it. Toggling again mid-flight redirects the thumb instead of restarting it.
- The thumb can be dragged: past the touch slop it follows the finger, and on release it settles on the nearer end, or on the end it was flung towards. A drag that ends on the other end calls `onCheckedChange`; if the owner keeps the old state, the thumb springs back.
- Press and hover lay `COUITheme.colorScheme.press` / `hover` over the track.

## Component States

### Disabled State

```kotlin
var checked by remember { mutableStateOf(false) }

Switch(
    checked = checked,
    onCheckedChange = { checked = it },
    enabled = false
)
```

### Loading State

While `isLoading` is true, the thumb shrinks away and is replaced by a spinning gradient arc; touch input is swallowed until loading ends:

```kotlin
var checked by remember { mutableStateOf(true) }
var isLoading by remember { mutableStateOf(true) }

Switch(
    checked = checked,
    onCheckedChange = { checked = it },
    isLoading = isLoading
)
```

## Properties

### Switch Properties

| Property Name   | Type                 | Description                        | Default Value                 | Required |
| --------------- | -------------------- | ---------------------------------- | ----------------------------- | -------- |
| checked         | Boolean              | Whether the switch is checked      | -                             | Yes      |
| onCheckedChange | ((Boolean) -> Unit)? | Callback when switch state changes; `null` = non-interactive | -   | Yes      |
| modifier        | Modifier             | Modifier applied to the switch     | Modifier                      | No       |
| colors          | SwitchColors         | Color configuration for the switch | SwitchDefaults.switchColors() | No       |
| enabled         | Boolean              | Whether the switch is interactive  | true                          | No       |
| isLoading       | Boolean              | Whether the switch is in the COUI loading state: the thumb shrinks away and is replaced by a spinning gradient arc; touch is swallowed | false | No       |

### SwitchDefaults Object

The SwitchDefaults object provides the dimensions and default color configurations for the Switch component.

#### Constants

| Constant Name    | Type   | Description                                   | Value   |
| ---------------- | ------ | --------------------------------------------- | ------- |
| TrackWidth       | Dp     | Track width                                   | 44.dp   |
| TrackHeight      | Dp     | Track height                                  | 24.dp   |
| ThumbSize        | Dp     | Thumb diameter                                | 18.dp   |
| ThumbStartOffset | Dp     | Thumb offset in the unchecked position        | 3.dp    |
| ThumbEndOffset   | Dp     | Thumb offset in the checked position          | 23.dp   |
| Travel           | Dp     | Distance the thumb travels                    | 20.dp   |
| ThumbShadow      | Shadow | Drop shadow under the thumb while enabled     | -       |

#### Methods

| Method Name    | Type         | Description                                 |
| -------------- | ------------ | ------------------------------------------- |
| switchColors() | SwitchColors | Creates default color config for the switch |

### SwitchColors Class

| Property Name               | Type  | Description                             | Default Value | Required |
| --------------------------- | ----- | --------------------------------------- | ------------- | -------- |
| checkedThumbColor           | Color | Thumb color when checked                | `onPrimary`           | Yes      |
| uncheckedThumbColor         | Color | Thumb color when unchecked              | `onSecondary`         | Yes      |
| disabledCheckedThumbColor   | Color | Thumb color when disabled and checked   | `disabledSliderThumb` | Yes      |
| disabledUncheckedThumbColor | Color | Thumb color when disabled and unchecked | `disabledSliderThumb` | Yes      |
| checkedTrackColor           | Color | Track color when checked                | `primary`             | Yes      |
| uncheckedTrackColor         | Color | Track color when unchecked              | `dragHandle`          | Yes      |
| disabledCheckedTrackColor   | Color | Track color when disabled and checked   | `disabledPrimary`     | Yes      |
| disabledUncheckedTrackColor | Color | Track color when disabled and unchecked | `secondaryContainer`  | Yes      |

The default values in the table are the `COUITheme.colorScheme` tokens `switchColors()` uses. With dynamic colors, both enabled thumbs use `onPrimary`.

## Advanced Usage

### Custom Colors

```kotlin
var checked by remember { mutableStateOf(false) }

Switch(
    checked = checked,
    onCheckedChange = { checked = it },
    colors = SwitchDefaults.switchColors(
        checkedTrackColor = Color.Red,
        checkedThumbColor = Color.White
    )
)
```

### Using with Text

```kotlin
var checked by remember { mutableStateOf(false) }

Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(16.dp)
) {
    Switch(
        checked = checked,
        onCheckedChange = { checked = it }
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(text = if (checked) "Enabled" else "Disabled")
}
```

### Using in Lists

```kotlin
val options = listOf("Airplane Mode", "Bluetooth", "Location Services")
val checkedStates = remember { mutableStateListOf(false, true, false) }

LazyColumn {
    items(options.size) { index ->
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = options[index])
            Switch(
                checked = checkedStates[index],
                onCheckedChange = { checkedStates[index] = it }
            )
        }
    }
}
```

### Clickable List Row

```kotlin
data class Option(val text: String, var isSelected: Boolean)
val options = remember {
    mutableStateListOf(
        Option("Airplane Mode", false),
        Option("Bluetooth", true),
        Option("Location Services", false)
    )
}

LazyColumn {
    items(options.size) { index ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    options[index] = options[index].copy(
                        isSelected = !options[index].isSelected
                    )
                }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = options[index].text)
            Switch(
                checked = options[index].isSelected,
                onCheckedChange = { 
                    options[index] = options[index].copy(
                        isSelected = !options[index].isSelected
                    )
                }
            )
        }
    }
}
```