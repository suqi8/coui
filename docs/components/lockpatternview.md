# LockPatternView

`LockPatternView` reproduces the ColorOS 3 x 3 pattern lock (`COUILockPatternView`). Dragging over the dots draws a path; each added dot plays a ripple that grows to 7x and fades, its glowing inner circle fades in and the path segment snaps to its center. A wrong pattern turns red and flashes out over one second.

<div style="position: relative; height: 420px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=lockPatternView" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.LockPatternView
import io.github.suqi8.coui.kmp.basic.LockPatternViewDefaults
import io.github.suqi8.coui.kmp.basic.LockPatternDisplayMode
import io.github.suqi8.coui.kmp.basic.rememberLockPatternState
```

## Basic Usage

Check the pattern when the finger is lifted; mark a wrong one and clear it (the clear waits for the error flash to end):

```kotlin
val state = rememberLockPatternState()

LockPatternView(
    state = state,
    onPatternDetected = { pattern ->
        if (patternToString(pattern) != "14789") {
            state.setDisplayMode(LockPatternDisplayMode.Wrong)
        }
        state.clearPattern()
    },
)
```

## Replay and Entrance

```kotlin
// Replay a pattern, one dot every 700ms
state.setPattern(LockPatternDisplayMode.Animate, stringToPattern("14789"))

// The COUI entrance: dots rise 258dp and fade in, staggered by 16ms
scope.launch { state.animateEnter() }
```

## Properties

### LockPatternView

| Property              | Type                              | Description                                      | Default Value                       | Required |
| --------------------- | --------------------------------- | ------------------------------------------------ | ----------------------------------- | -------- |
| onPatternDetected     | (List\<LockPatternCell\>) -> Unit | Called with the pattern when the finger lifts    | -                                   | Yes      |
| modifier              | Modifier                          | Modifier applied to the view (default 312x280dp) | Modifier                            | No       |
| state                 | LockPatternState                  | Pattern, display mode and dot animations         | rememberLockPatternState()          | No       |
| enabled               | Boolean                           | Whether touches are accepted                     | true                                | No       |
| inStealthMode         | Boolean                           | Hide the path and selected dots (wrong still shows) | false                               | No       |
| hapticFeedbackEnabled | Boolean                           | Tick per added dot, reject on a wrong pattern    | true                                | No       |
| colors                | LockPatternViewColors             | Dot, success and error colors                    | LockPatternViewDefaults.colors()    | No       |
| onPatternStart        | () -> Unit                        | A pattern starts                                 | {}                                  | No       |
| onPatternCellAdded    | (List\<LockPatternCell\>) -> Unit | A dot was added                                  | {}                                  | No       |
| onPatternCleared      | () -> Unit                        | A pattern in progress was cancelled              | {}                                  | No       |

### LockPatternState

| Member                            | Description                                                                 |
| --------------------------------- | --------------------------------------------------------------------------- |
| pattern                           | The cells of the current pattern                                            |
| displayMode                       | `Correct`, `Animate` or `Wrong`                                             |
| setDisplayMode(mode, haptic)      | `Wrong` plays the 1s error flash (and a reject haptic for 2+ dots)          |
| setPattern(mode, pattern)         | Replaces the pattern                                                        |
| clearPattern()                    | Clears the pattern, after the error flash if it is running                  |
| animateEnter()                    | Suspends while the entrance plays                                           |

`patternToString` / `stringToPattern` convert a pattern to and from its 1-based dot numbers (`COUILockPatternUtils`).

### LockPatternViewDefaults

| Constant          | Type | Default Value | COUI Source                                  |
| ----------------- | ---- | ------------- | -------------------------------------------- |
| Width             | Dp   | 312.dp        | coui_lock_pattern_view_width                 |
| Height            | Dp   | 280.dp        | coui_lock_pattern_view_height                |
| HorizontalPadding | Dp   | 21.dp         | coui_lock_pattern_view_horizontal_padding    |
| VerticalPadding   | Dp   | 5.dp          | coui_lock_pattern_view_vertical_padding      |
| DotSize           | Dp   | 10.dp         | lock_pattern_dot_size                        |
| PathWidth         | Dp   | 2.dp          | lock_pattern_dot_line_width                  |
| EnterTranslateY   | Dp   | 258.dp        | color_lock_pattern_view_max_translate_y      |

| Method   | Description                                                                                      |
| -------- | ------------------------------------------------------------------------------------------------ |
| colors() | Dots #4D000000 / #66FFFFFF, success #000000 / #FFFFFF, error #EA3447, ripple peak 0.1 / 0.2       |

### LockPatternViewColors

| Property            | Type  | Description                                              |
| ------------------- | ----- | -------------------------------------------------------- |
| dotColor            | Color | The resting dots                                         |
| successColor        | Color | Path and selected dots while drawing or correct          |
| errorColor          | Color | Path and selected dots of a wrong pattern                |
| outerCircleMaxAlpha | Float | Peak alpha of the ripple of an added dot                 |

## Behavior

- A dot is hit within the middle 60% of its cell (`mHitFactor`). Jumping over a dot on a straight line adds it too.
- Ripple: scale 1 -> 7 over 460ms (`COUIInEaseInterpolator`), alpha 0 -> peak -> 0. Inner circle: 230ms fade-in (`COUIEaseInterpolator`). Path end: 100ms snap (fast-out-slow-in).
- Error flash: path and selected dots follow 1, 0.35, 1, 0.15, 0.5, 0 over 1s; the plain dot reappears once the flash drops below 0.1.
- As in COUI, the path is colored with the success / error color, so there is no separate path color.
