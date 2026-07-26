# Snackbar

`Snackbar` is a lightweight feedback component in COUI used to display brief messages at the bottom of the screen. It can optionally provide actions such as “Undo” and supports different display durations.

<div style="position: relative; height: 360px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=snackbar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.Snackbar
import io.github.suqi8.coui.kmp.basic.SnackbarHost
import io.github.suqi8.coui.kmp.basic.SnackbarHostState
import io.github.suqi8.coui.kmp.basic.SnackbarDuration
import io.github.suqi8.coui.kmp.basic.SnackbarResult
```

## Basic Usage

The Snackbar is usually used together with `Scaffold`. You create a `SnackbarHostState`, pass it to `SnackbarHost`, and then call `showSnackbar` to display messages:

```kotlin
val snackbarHostState = remember { SnackbarHostState() }
val scope = rememberCoroutineScope()

Scaffold(
    snackbarHost = {
        SnackbarHost(state = snackbarHostState)
    },
) { paddingValues ->
    Box(
        modifier = Modifier
            .padding(paddingValues),
    ) {
        TextButton(
            text = "Show message",
            onClick = {
                scope.launch {
                    snackbarHostState.showSnackbar("This is a short message")
                }
            },
        )
    }
}
```

## SnackbarHostState and showSnackbar

`SnackbarHostState` manages a queue of Snackbar messages.

### showSnackbar

```kotlin
suspend fun SnackbarHostState.showSnackbar(
    message: String,
    actionLabel: String? = null,
    withDismissAction: Boolean = false,
    duration: SnackbarDuration = SnackbarDuration.Short,
): SnackbarResult
```

| Parameter Name    | Type             | Description                              | Default Value          | Required |
| ----------------- | ---------------- | ---------------------------------------- | ---------------------- | -------- |
| message           | String           | Text shown in the Snackbar               | -                      | Yes      |
| actionLabel       | String?          | Optional label for the action button     | null                   | No       |
| withDismissAction | Boolean          | Whether to show a dismiss icon           | false                  | No       |
| duration          | SnackbarDuration | Duration that the Snackbar stays visible | SnackbarDuration.Short | No       |

The return value `SnackbarResult` indicates whether the Snackbar was dismissed or the action was performed.

### Getting the oldest or newest Snackbar

```kotlin
suspend fun SnackbarHostState.newestSnackbarData(): SnackbarData?
suspend fun SnackbarHostState.oldestSnackbarData(): SnackbarData?
```

These helpers allow you to manually dismiss the newest or oldest visible Snackbar via `dismiss()` or `performAction()`.

## SnackbarHost

`SnackbarHost` is responsible for rendering the Snackbars based on the given `SnackbarHostState`.

```kotlin
@Composable
fun SnackbarHost(
    state: SnackbarHostState,
    modifier: Modifier = Modifier,
    canSwipeToDismiss: Boolean = true,
    content: @Composable (SnackbarData) -> Unit = { Snackbar(it) },
)
```

| Parameter Name    | Type                               | Description                                | Default Value        | Required |
| ----------------- | ---------------------------------- | ------------------------------------------ | -------------------- | -------- |
| state             | SnackbarHostState                  | State that holds the Snackbar queue        | -                    | Yes      |
| modifier          | Modifier                           | Modifier applied to the host container     | Modifier             | No       |
| canSwipeToDismiss | Boolean                            | Whether Snackbars can be dismissed by swipe | true                | No       |
| content           | @Composable (SnackbarData) -> Unit | Custom content for each Snackbar item      | `{ Snackbar(it) }` | No       |

In most cases you can keep the default `content` which uses the built‑in `Snackbar` composable.

## Snackbar

`Snackbar` defines the default visual style for messages.

```kotlin
@Composable
fun Snackbar(
    data: SnackbarData,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    cornerRadius: Dp = SnackbarDefaults.CornerRadius,
    singleLineCornerRadius: Dp = SnackbarDefaults.SingleLineCornerRadius,
    colors: SnackbarColors = SnackbarDefaults.snackbarColors(),
    insideMargin: PaddingValues = SnackbarDefaults.InsideMargin,
)
```

| Parameter Name         | Type           | Description                                                | Default Value                           | Required |
| ---------------------- | -------------- | ---------------------------------------------------------- | --------------------------------------- | -------- |
| data                   | SnackbarData   | Data describing message and actions                        | -                                       | Yes      |
| modifier               | Modifier       | Modifier applied to the Snackbar container                 | Modifier                                | No       |
| icon                   | (@Composable () -> Unit)? | Optional leading icon shown in a 30.dp box (pass it through the `content` lambda of `SnackbarHost`) | null | No       |
| cornerRadius           | Dp             | Corner radius when the message spans multiple lines        | SnackbarDefaults.CornerRadius           | No       |
| singleLineCornerRadius | Dp             | Corner radius when the message fits on a single line       | SnackbarDefaults.SingleLineCornerRadius | No       |
| colors                 | SnackbarColors | Color configuration for the Snackbar                       | SnackbarDefaults.snackbarColors()       | No       |
| insideMargin           | PaddingValues  | Inner padding of the Snackbar content (the end padding collapses to 4.dp when the action button is the trailing element) | SnackbarDefaults.InsideMargin | No       |

### SnackbarColors and SnackbarDefaults

`SnackbarColors` defines the color set used by the Snackbar:

```kotlin
data class SnackbarColors(
    val containerColor: Color,
    val contentColor: Color,
    val actionContentColor: Color,
    val dismissActionContentColor: Color,
)
```

You can create a color configuration via `SnackbarDefaults.snackbarColors`:

```kotlin
val colors = SnackbarDefaults.snackbarColors(
    containerColor = COUITheme.colorScheme.surfaceContainerHighest,
    contentColor = COUITheme.colorScheme.onSurfaceContainer,
    actionContentColor = COUITheme.colorScheme.primary,
    dismissActionContentColor = COUITheme.colorScheme.onSurfaceVariantActions,
)
```

#### Constants

`SnackbarDefaults` also exposes the default corner radii, text style and inside margin used by `Snackbar`:

| Constant Name          | Type          | Description                                              | Default Value                                       |
| ---------------------- | ------------- | -------------------------------------------------------- | --------------------------------------------------- |
| CornerRadius           | Dp            | Corner radius when the message spans multiple lines      | 16.dp                                               |
| SingleLineCornerRadius | Dp            | Corner radius when the message fits on a single line     | 24.dp                                               |
| InsideMargin           | PaddingValues | Default inner padding of the Snackbar                    | PaddingValues(horizontal = 16.dp, vertical = 10.dp) |
| IconSize               | Dp            | Size of the optional leading icon box                    | 30.dp                                               |

`SnackbarDefaults.textStyle()` returns the default text style of the message and action label (14sp, medium weight).

## SnackbarDuration and SnackbarResult

### SnackbarDuration

`SnackbarDuration` controls how long the Snackbar stays visible:

```kotlin
sealed interface SnackbarDuration {
    data object Short : SnackbarDuration
    data object Long : SnackbarDuration
    data object Indefinite : SnackbarDuration
    data class Custom(val durationMillis: Long) : SnackbarDuration
}
```

| Option     | Description                           | Duration         |
| ---------- | ------------------------------------- | ---------------- |
| Short      | Short message                         | About 4 seconds  |
| Long       | Longer message                        | About 10 seconds |
| Indefinite | Stays until dismissed or action fired | Until dismissed  |
| Custom     | Custom duration in milliseconds       | User‑specified  |

### SnackbarResult

`SnackbarResult` describes how the Snackbar was completed:

```kotlin
enum class SnackbarResult {
    Dismissed,
    ActionPerformed,
}
```

## Advanced Usage

### Snackbar with leading icon

Pass a custom `content` lambda to `SnackbarHost` to render the built-in `Snackbar` with an icon:

```kotlin
SnackbarHost(state = snackbarHostState) { data ->
    Snackbar(
        data = data,
        icon = {
            Icon(
                imageVector = COUIIcons.Basic.Check,
                contentDescription = null,
                tint = COUITheme.colorScheme.primary
            )
        }
    )
}
```

### Snackbar with action

```kotlin
val snackbarHostState = remember { SnackbarHostState() }
val scope = rememberCoroutineScope()

TextButton(
    text = "Show action",
    onClick = {
        scope.launch {
            val result = snackbarHostState.showSnackbar(
                message = "This message has an action",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short,
            )
            when (result) {
                SnackbarResult.ActionPerformed -> { /* handle undo */ }
                SnackbarResult.Dismissed -> { /* handle timeout */ }
            }
        }
    },
)
```

### Dismissible and indefinite Snackbar

```kotlin
val snackbarHostState = remember { SnackbarHostState() }
val scope = rememberCoroutineScope()

TextButton(
    text = "Show indefinite",
    onClick = {
        scope.launch {
            snackbarHostState.showSnackbar(
                message = "Indefinite message, dismiss manually",
                withDismissAction = true,
                duration = SnackbarDuration.Indefinite,
            )
        }
    },
)

TextButton(
    text = "Dismiss oldest",
    onClick = {
        scope.launch {
            snackbarHostState.oldestSnackbarData()?.dismiss()
        }
    },
)
```
