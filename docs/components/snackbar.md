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

While a Snackbar is touched, its auto-dismiss timer is paused; on release the timer restarts with the full duration (COUISnackBar `onTouchEvent`: DOWN / MOVE cancel, UP / CANCEL re-post). Swipe-to-dismiss, stacking several Snackbars and the dismiss icon are library extensions with no COUI counterpart.

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
    maxWidth: Dp = SnackbarDefaults.maxWidth(),
    colors: SnackbarColors = SnackbarDefaults.snackbarColors(),
    insideMargin: PaddingValues = SnackbarDefaults.InsideMargin,
)
```

| Parameter Name         | Type           | Description                                                | Default Value                           | Required |
| ---------------------- | -------------- | ---------------------------------------------------------- | --------------------------------------- | -------- |
| data                   | SnackbarData   | Data describing message and actions                        | -                                       | Yes      |
| modifier               | Modifier       | Modifier applied to the Snackbar container                 | Modifier                                | No       |
| icon                   | (@Composable () -> Unit)? | Optional leading icon shown in a 30.dp box (pass it through the `content` lambda of `SnackbarHost`) | null | No       |
| cornerRadius           | Dp             | Corner radius of the vertical (multi-line) layout, drawn at `default_g2_weight` 2.45 | SnackbarDefaults.CornerRadius           | No       |
| singleLineCornerRadius | Dp             | Corner radius of the single-line layout; `Dp.Unspecified` rounds it at half its height (the COUI 17 soft capsule) | SnackbarDefaults.SingleLineCornerRadius | No       |
| maxWidth               | Dp             | Maximum width of the Snackbar, excluding its 16.dp outer margins; `Dp.Unspecified` removes the cap | SnackbarDefaults.maxWidth() | No       |
| colors                 | SnackbarColors | Color configuration for the Snackbar                       | SnackbarDefaults.snackbarColors()       | No       |
| insideMargin           | PaddingValues  | Inner padding of the Snackbar content (the end padding collapses to 4.dp when the action button is the trailing element of the single-line layout) | SnackbarDefaults.InsideMargin | No       |

### Layout

Following COUI 17 `COUISnackBar.adjustLayout`:

- **Single line**: the icon, message, action and dismiss icon sit on one line, each centered vertically, inside a capsule.
- **Vertical**: once the message wraps (or contains a line break) or the line is wider than the available width, the message starts 12.dp from the top, the action moves below it (10.dp gap), aligned to the end with a 4.dp end margin and a 9.dp bottom margin, and the corners drop to `cornerRadius` (16.dp).
- **Action**: the action label is padded 12.dp horizontally and 4.dp vertically for its capsule press area (COUI `text_ripple_bg_padding_*`), keeps a 16.dp gap after the message and a 4.dp end margin.
- **Width**: the Snackbar wraps its content, up to `maxWidth`. By default this is six columns of the COUI responsive layout grid (small margin) of the current window: on compact windows (narrower than 600.dp) the grid only has four columns, so the Snackbar may span the window minus its 16.dp margins; on medium (8 columns, 24.dp margin, 8.dp gutter) and expanded (12 columns, 40.dp margin, 12.dp gutter) windows it stays centered at six columns.

The container is `backgroundTop` (`#FFFFFF` light / `#333333` dark) and the action label is `primaryTextOnPopup` (`#0080FF` / `#5C9DFF`). The shadow approximates COUI `ShadowUtils` level 3 with a soft drop shadow, denser in the dark scheme (the COUI shadow alpha rises from 40/255 to 120/255).

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
    containerColor = COUITheme.colorScheme.backgroundTop,
    contentColor = COUITheme.colorScheme.onSurfaceContainer,
    actionContentColor = COUITheme.colorScheme.primaryTextOnPopup,
    dismissActionContentColor = COUITheme.colorScheme.onSurfaceVariantActions,
)
```

#### Constants

`SnackbarDefaults` also exposes the default corner radii, text style and inside margin used by `Snackbar`:

| Constant Name          | Type          | Description                                              | Default Value                                       |
| ---------------------- | ------------- | -------------------------------------------------------- | --------------------------------------------------- |
| CornerRadius           | Dp            | Corner radius of the vertical (multi-line) layout (COUI `couiRoundCornerL`) | 16.dp                            |
| SingleLineCornerRadius | Dp            | Corner radius of the single-line layout; unspecified = capsule at half the height | Dp.Unspecified             |
| InsideMargin           | PaddingValues | Default inner padding of the Snackbar                    | PaddingValues(horizontal = 16.dp, vertical = 10.dp) |
| IconSize               | Dp            | Size of the optional leading icon box                    | 30.dp                                               |

`SnackbarDefaults.textStyle()` returns the default text style of the message and action label (14sp, medium weight), and `SnackbarDefaults.maxWidth()` returns the grid-based maximum width for the current window.

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

## NotificationSnackbar

`NotificationSnackbar` is the COUI 17 notification snack bar (`COUINotificationSnackBar`): a
floating card with a leading image, a title, an optional subtitle, a small action button and a close
button in the top end corner. It can be shown in place, or through `SnackbarHost` to get the host's
show / dismiss animation and timeout.

```kotlin
import io.github.suqi8.coui.kmp.basic.NotificationSnackbar
import io.github.suqi8.coui.kmp.basic.NotificationSnackbarIconType
```

```kotlin
NotificationSnackbar(
    title = "Screenshot saved",
    subtitle = "Tap Share to send it to a contact",
    actionLabel = "Share",
    onActionClick = { /* share */ },
    onDismiss = { visible = false },
    icon = { Image(painter = thumbnail, contentDescription = null) },
)
```

Through a host, the message is the title, the action label the button; the button performs the
action and the close button or a swipe dismisses it:

```kotlin
SnackbarHost(state = snackbarHostState, canSwipeToDismiss = false) { data ->
    NotificationSnackbar(data, subtitle = "Tap Share to send it to a contact", icon = { /* ... */ })
}
```

### NotificationSnackbar Properties

| Property Name  | Type                         | Description                                                    | Default Value                                    | Required |
| -------------- | ---------------------------- | -------------------------------------------------------------- | ------------------------------------------------ | -------- |
| title          | String                       | Title (COUI content text), at most two lines                   | -                                                | Yes      |
| actionLabel    | String?                      | Label of the small action button; null hides it                | -                                                | Yes      |
| onActionClick  | () -> Unit                   | Called when the action button is clicked                       | -                                                | Yes      |
| onDismiss      | () -> Unit                   | Called when the close button is clicked or the card is swiped away | -                                            | Yes      |
| modifier       | Modifier                     | Modifier applied to the card                                   | Modifier                                         | No       |
| subtitle       | String?                      | Subtitle, at most two lines                                    | null                                             | No       |
| icon           | (@Composable () -> Unit)?    | Leading image, laid out in the box `iconType` gives            | null                                             | No       |
| iconType       | NotificationSnackbarIconType | `Square` 44dp, `Vertical` 36 x 48dp or `Horizontal` 58 x 44dp  | NotificationSnackbarIconType.Square              | No       |
| swipeToDismiss | Boolean                      | Whether the card can be swiped away                            | true                                             | No       |
| maxWidth       | Dp                           | Maximum width, excluding the 16dp outer margins                | SnackbarDefaults.maxWidth()                      | No       |
| cornerRadius   | Dp                           | Corner radius of the card (couiRoundCornerL)                   | SnackbarDefaults.CornerRadius                    | No       |
| colors         | NotificationSnackbarColors   | Colors of the card                                             | SnackbarDefaults.notificationSnackbarColors()    | No       |

The `SnackbarData` overload takes `data` instead of `title`, `actionLabel`, `onActionClick` and `onDismiss`.

### NotificationSnackbarColors

| Property Name  | Type             | Description                                | Default Value                                 |
| -------------- | ---------------- | ------------------------------------------ | --------------------------------------------- |
| containerColor | Color            | Card fill (couiColorBackgroundTop)         | COUITheme.colorScheme.backgroundTop           |
| titleColor     | Color            | Title color (couiColorPrimaryNeutral)      | COUITheme.colorScheme.onSurfaceContainer      |
| subtitleColor  | Color            | Subtitle color (couiColorSecondNeutral)    | COUITheme.colorScheme.onSurfaceVariantSummary |
| closeIconColor | Color            | Close glyph tint (couiColorLabelTertiary)  | COUITheme.colorScheme.onSurfaceVariantActions |
| actionColors   | TextButtonColors | Colors of the action button                | ButtonDefaults.textButtonColors()             |

### Layout and Swipe

- **Layout**: the image sits 16dp from the start, top and bottom edges and 8dp before the text; the title is 14sp medium and the subtitle 12sp, 2dp below it; the small button (12dp x 4dp padding, at most 120dp wide and two lines) is vertically centred; the 20dp close button sits 6dp from the top and end edges.
- **Swipe**: a drag past the touch slop picks an axis (COUI picks it on the first pixel; the slop keeps taps on the buttons and a scrolling parent's gestures working). Sideways the card follows the finger; past an eighth of its width it slides off screen at 1.75px per ms on the COUI move-ease curve and `onDismiss` runs, otherwise it springs back (response 0.35, bounce 0). Downwards it dismisses the same way past an eighth of its width; once dragged it is held to an eighth of its width above its place. A drag that starts upwards is left to the parent.
