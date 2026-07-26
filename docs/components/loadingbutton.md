# LoadingButton

A text button with a built-in loading state, mirroring ColorOS's COUILoadingButton: while loading the label is hidden, three tiny dots pulse in a staggered wave, and clicks are swallowed so the action cannot be triggered again.

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.LoadingButton
import io.github.suqi8.coui.kmp.basic.LoadingButtonDefaults
```

## Basic Usage

```kotlin
var isLoading by remember { mutableStateOf(false) }

LoadingButton(
    text = "Download",
    onClick = { isLoading = true },
    isLoading = isLoading,
    colors = ButtonDefaults.textButtonColorsPrimary(),
)
```

### With a loading label

```kotlin
LoadingButton(
    text = "Sign in",
    onClick = { isLoading = true },
    isLoading = isLoading,
    loadingText = "Signing in",
)
```

## Properties

### LoadingButton

| Property          | Type                      | Description                                             | Default Value                    | Required |
| ----------------- | ------------------------- | ------------------------------------------------------- | -------------------------------- | -------- |
| text              | String                    | Label shown when not loading                            | -                                | Yes      |
| onClick           | () -> Unit                | Callback on click; not invoked while loading            | -                                | Yes      |
| modifier          | Modifier                  | Modifier applied to the button                          | Modifier                         | No       |
| isLoading         | Boolean                   | Whether the button is in the loading state              | false                            | No       |
| loadingText       | String?                   | Label shown next to the dots while loading              | null                             | No       |
| enabled           | Boolean                   | Whether the button is enabled                           | true                             | No       |
| cornerRadius      | Dp                        | Corner radius of the button                             | ButtonDefaults.CornerRadius      | No       |
| minWidth          | Dp                        | Minimum width of the button                             | ButtonDefaults.MinWidth          | No       |
| minHeight         | Dp                        | Minimum height of the button                            | ButtonDefaults.MinHeight         | No       |
| colors            | TextButtonColors          | Color configuration                                     | ButtonDefaults.textButtonColors() | No      |
| insideMargin      | PaddingValues             | Margin inside the button                                | ButtonDefaults.InsideMargin      | No       |
| interactionSource | MutableInteractionSource? | Interaction source of the button                        | null                             | No       |
| indication        | Indication?               | Indication of the button (COUI press feedback is built in) | null                          | No       |

### LoadingButtonDefaults

| Constant   | Type | Default Value |
| ---------- | ---- | ------------- |
| DotRadius  | Dp   | 1.dp          |
| DotSpacing | Dp   | 2.dp          |

## Behavior

- While `isLoading` is `true` the label is hidden but stays measured, so the button keeps its width; clicks are swallowed while the COUI press feedback (scale + tint) still plays.
- The three dots pulse 20% -> 50% -> 100% -> 50% -> 20% alpha on a linear curve; each dot is delayed by 333ms and the whole 1332ms wave loops, matching the COUILoadingButton animator set.
- With `loadingText` set, the loading state shows that label followed by three animated dot glyphs instead of the plain dot indicator (COUIButton `isShowLoadingText`).
- The dots are drawn with the button's content color, like COUILoadingButton draws them with the label paint.
