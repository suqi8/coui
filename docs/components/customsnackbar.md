# CustomSnackbar

`CustomSnackbar` is a snack bar whose whole look comes from your content, after ColorOS's `COUICustomSnackBar`: a bare frame that adds the snack bar behaviour (auto dismiss, slide down to dismiss, press feedback, fade out) around a custom view and draws no surface of its own.

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=customSnackbar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.CustomSnackbar
import io.github.suqi8.coui.kmp.basic.CustomSnackbarDefaults
```

## Basic Usage

```kotlin
var visible by remember { mutableStateOf(true) }

Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
    if (visible) {
        CustomSnackbar(
            onDismiss = { visible = false },
            touchSlidable = true,
            autoDismissMillis = 4000L,
        ) {
            Card(insideMargin = PaddingValues(16.dp), shadow = CardDefaults.shadow()) {
                Text("Saved to album")
            }
        }
    }
}
```

## Properties

### CustomSnackbar Properties

| Property Name        | Type                   | Description                                                                        | Default Value | Required |
| -------------------- | ---------------------- | ---------------------------------------------------------------------------------- | ------------- | -------- |
| onDismiss            | () -> Unit             | Called once the snack bar is dismissed (after the fade, if any); remove it here    | -             | Yes      |
| modifier             | Modifier               | Modifier applied to the snack bar                                                  | Modifier      | No       |
| touchSlidable        | Boolean                | Whether a downward drag dismisses it (`setTouchSlidable`)                          | false         | No       |
| pressFeedback        | Boolean                | Whether it shrinks while pressed (`setPressFeedBack`)                              | false         | No       |
| dismissWithAnimation | Boolean                | Whether dismissing fades it out first (`setDismissWithAnim`)                       | true          | No       |
| autoDismissMillis    | Long?                  | Idle time after the last touch before it dismisses itself; null keeps it           | null          | No       |
| content              | @Composable () -> Unit | The custom content, including its surface                                          | -             | Yes      |

A second overload takes a `SnackbarData` instead of `onDismiss` and `autoDismissMillis`, for use inside a `SnackbarHost`: sliding it away dismisses the data, and the host keeps its own timeout and animations.

### CustomSnackbarDefaults Object

| Name                  | Type   | Description                                                 | Value                              |
| --------------------- | ------ | ----------------------------------------------------------- | ---------------------------------- |
| DismissDurationMillis | Int    | Fade-out duration (`COUICustomSnackAnimUtil`)               | 180                                |
| DismissEasing         | Easing | Fade-out easing (`PathInterpolator(0.1, 0, 0.1, 1)`)        | CubicBezierEasing(0.1f, 0f, 0.1f, 1f) |

## Behavior

- **Auto dismiss**: every touch event restarts the countdown, so the snack bar stays while it is being touched and dismisses `autoDismissMillis` after the last touch.
- **Slide**: the snack bar follows a downward drag and never moves above its place; releasing it while it is dragged down dismisses it. COUI starts on the first moved pixel; here the drag starts past the touch slop, so a tap on a button inside still clicks, and a shorter drag springs back.
- **Press feedback**: drawn with the COUI 17 touch-motion press scale.
- No show animation is built in (COUI leaves it to `setShowAnimSet`); wrap it in `AnimatedVisibility` if you need one.
- The ColorOS builder of this class is stripped from the shipped apps, so the defaults above are the library's: COUI's fields all start off.

## Advanced Usage

### Inside a SnackbarHost

```kotlin
SnackbarHost(snackbarHostState, canSwipeToDismiss = false) { data ->
    CustomSnackbar(data, touchSlidable = true, pressFeedback = true) {
        Card(
            modifier = Modifier.padding(horizontal = 16.dp),
            insideMargin = PaddingValues(16.dp),
            shadow = CardDefaults.shadow(),
        ) {
            Text(data.visuals.message)
        }
    }
}
```
