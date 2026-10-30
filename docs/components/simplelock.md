# SimpleLock

`SimpleLock` reproduces the ColorOS PIN dot row (`COUISimpleLock`): four or six 10dp dots that fill as digits are entered. Deleting fades the last dot out, clearing fades them all, and a failure shakes the row while the filled dots drop away.

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=simpleLock" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.SimpleLock
import io.github.suqi8.coui.kmp.basic.SimpleLockDefaults
import io.github.suqi8.coui.kmp.basic.SimpleLockType
import io.github.suqi8.coui.kmp.basic.rememberSimpleLockState
```

## Basic Usage

```kotlin
var pin by remember { mutableStateOf("") }

SimpleLock(filledCount = pin.length)
```

## Failure

```kotlin
val state = rememberSimpleLockState()

SimpleLock(filledCount = pin.length, type = SimpleLockType.Six, state = state)

scope.launch {
    state.playFailure()
    pin = ""
}
```

## Over a Wallpaper

```kotlin
SimpleLock(
    filledCount = pin.length,
    colors = SimpleLockDefaults.transparentColors(),
    glowEffect = true,
)
```

## Properties

### SimpleLock

| Property              | Type             | Description                                          | Default Value                  | Required |
| --------------------- | ---------------- | ---------------------------------------------------- | ------------------------------ | -------- |
| filledCount           | Int              | Number of entered digits                             | -                              | Yes      |
| modifier              | Modifier         | Modifier applied to the row                          | Modifier                       | No       |
| type                  | SimpleLockType   | `Four` or `Six` dots                                 | Four                           | No       |
| state                 | SimpleLockState  | Animations, `playFailure()`                          | rememberSimpleLockState()      | No       |
| glowEffect            | Boolean          | New dots spring in with a glow                       | false                          | No       |
| hapticFeedbackEnabled | Boolean          | Reject haptic on failure                             | true                           | No       |
| colors                | SimpleLockColors | Filled and empty dot colors                          | SimpleLockDefaults.colors()    | No       |
| contentDescription    | String?          | Accessibility description                            | null                           | No       |

### SimpleLockDefaults

| Constant   | Type | Default Value | COUI Source                          |
| ---------- | ---- | ------------- | ------------------------------------ |
| DotSize    | Dp   | 10.dp         | coui_simple_lock_*_rectangle_icon    |
| DotSpacing | Dp   | 10.dp         | couiRectanglePadding                 |
| GlowOffset | Dp   | 25.dp         | coui_glow_ball_offset                |

| Method              | Description                                                   |
| ------------------- | ------------------------------------------------------------- |
| colors()            | Filled #000000 / #E6FFFFFF, empty #19000000 / #66FFFFFF        |
| transparentColors() | Filled #FFFFFF, empty #26FFFFFF                               |

### SimpleLockColors

| Property      | Type  | Description        |
| ------------- | ----- | ------------------ |
| filledColor   | Color | Entered dots       |
| outlinedColor | Color | Empty dots         |

## Behavior

- A new digit fills its dot at once; with `glowEffect` it springs in (response 0.35, bounce 0.5, up to 1.2x) while a glow flares and fades.
- Deleting one digit fades the dot over 230ms (`COUIEaseInterpolator`); dropping several at once fades all of them.
- `playFailure()` runs 800ms: the row shakes through 30, -28, 14, -8, 4, -3, 0px and the filled dots fall 250px one after another, fading over their first 150px. The dots are then empty; reset `filledCount` to 0.
- The row is `10dp + 150px` tall, leaving room for the fall.
