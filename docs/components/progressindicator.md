# ProgressIndicator

`ProgressIndicator` is a progress indication component in COUI used to display the progress status of operations. It provides linear progress bar, circular progress indicator, infinite spinning indicator, the rotating spinner and the composite loading indicator with a tip, suitable for different loading and progress display scenarios.

<div style="position: relative; height: 250px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=progressIndicator" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.LinearProgressIndicator // Linear progress bar
import io.github.suqi8.coui.kmp.basic.CircularProgressIndicator // Circular progress indicator
import io.github.suqi8.coui.kmp.basic.InfiniteProgressIndicator // Infinite spinning indicator
import io.github.suqi8.coui.kmp.basic.RotatingProgressIndicator // Rotating spinner
import io.github.suqi8.coui.kmp.basic.CompProgressIndicator // Loading indicator with a tip
```

## Basic Usage

### Linear Progress Bar

Linear progress bar can be used to show operation progress:

```kotlin
// Linear progress bar with determinate progress
var progress by remember { mutableStateOf(0.3f) }

LinearProgressIndicator(progress = progress)
```

```kotlin
// Linear progress bar with indeterminate progress
LinearProgressIndicator()
```

::: tip
The determinate bar ports `COUIHorizontalProgressBar` (no progress animation; it fills from the right in RTL layouts). The indeterminate three-segment loop is a library extension: ColorOS's horizontal progress bar has no indeterminate mode.
:::

```kotlin
// Disabled linear progress bar (disabled fill and track colors)
LinearProgressIndicator(progress = 0.5f, enabled = false)
```

### Circular Progress Indicator

Circular progress indicator is suitable for space-saving scenarios:

```kotlin
// Circular progress indicator with determinate progress
var progress by remember { mutableStateOf(0.7f) }

CircularProgressIndicator(progress = progress)
```

```kotlin
// Circular progress indicator with indeterminate progress
CircularProgressIndicator()
```

With a progress value the indicator ports `COUICircularProgressBar`: a neutral arc on a divider track that follows progress changes on a critically damped spring (damping ratio 1, stiffness 50). Like the original it lays out at `size + 2 × 4dp` and draws a ring of outer diameter `size − 2 × 4dp`: the default large tier (40dp, stroke 5dp) is a 48dp box around a 32dp ring, the medium tier (30dp, stroke 3dp) a 38dp box around a 22dp ring.

Without a progress it ports `COUILoadingView`: an 18dp ring (stroke 2.67dp) with an accent arc (`#0080FF` / `#1A8CFF`) on an accent-tinted track (`#260080FF` / `#401A8CFF`), `ProgressIndicatorDefaults.loadingViewColors()`.

#### State and Type

A determinate indicator takes a `CircularProgressState` (`pause()` / `resume()` / `error()` / `recover()` of the original) and a `CircularProgressType` (`couiCircularProgressBarType`):

```kotlin
// Error: the ring shrinks to a 19dp outline around an exclamation mark (350ms)
CircularProgressIndicator(progress = 0.6f, state = CircularProgressState.Error)

// Paused: only the on-image type pauses; the ring fades out and a pause icon fades in
CircularProgressIndicator(
    progress = 0.6f,
    state = CircularProgressState.Paused,
    type = CircularProgressType.OnImage
)
```

| Type        | Look                                                        |
| ----------- | ----------------------------------------------------------- |
| Default     | `TYPE_DEFAULT`                                              |
| OnImage     | `TYPE_ON_IMAGE`: the track casts a 4dp shadow, 2dp down; the only type that pauses |
| FollowTheme | `TYPE_FOLLOW_THEME`: the track at alpha 89 / 255            |

### Infinite Progress Indicator

Infinite progress indicator is suitable for scenarios where operation duration is uncertain. It is tinted with the theme accent color by default, matching the ColorOS "Refreshing…" spinner:

```kotlin
InfiniteProgressIndicator()
```

### Rotating Spinner

`RotatingProgressIndicator` is the ColorOS system-default indeterminate spinner: a bare stroked arc with flat caps and no background ring, drawn in black / white at 85% opacity (`#D9000000` / `#D9FFFFFF`), whose sweep pulses between 273.6° and 50.4° while the arc spins twice per 76-frame (1266.7 ms) cycle. It is a direct port of the `coui_rotating_loading.json` asset that `Theme.COUI` binds to the `couiRotatingSpinnerJsonName` attribute, reproduced with a single `drawArc` so it needs no Lottie runtime.

```kotlin
RotatingProgressIndicator()
```

It ships a default (26dp) and a small (16dp) tier, matching `coui_lottie_loading_view_large_*` and `coui_lottie_loading_view_small_*`:

```kotlin
// Small rotating spinner
RotatingProgressIndicator(
    size = ProgressIndicatorDefaults.SmallRotatingProgressIndicatorSize,
    ringDiameter = ProgressIndicatorDefaults.SmallRotatingProgressIndicatorRingDiameter,
    strokeWidth = ProgressIndicatorDefaults.SmallRotatingProgressIndicatorStrokeWidth
)
```

### Loading Indicator with a Tip

`CompProgressIndicator` ports `COUICompProgressIndicator`: the rotating spinner at 26dp (large) or 14dp (small), optionally with a tip in couiTextBodyS / couiColorLabelSecondary — one line 4dp after the small spinner, or up to two lines 16dp (large) / 12dp (small) below it.

```kotlin
CompProgressIndicator(
    type = CompProgressIndicatorType.LargeWithTextVertical,
    text = "Loading…"
)
```

| CompProgressIndicatorType | Original                              |
| ------------------------- | ------------------------------------- |
| Large                     | `LARGE_ANIMATION`                     |
| Small                     | `SMALL_ANIMATION`                     |
| SmallWithTextHorizontal   | `SMALL_ANIMATION_WITH_TEXT_HORIZONTAL` |
| LargeWithTextVertical     | `LARGE_ANIMATION_WITH_TEXT_VERTICAL`  |
| SmallWithTextVertical     | `SMALL_ANIMATION_WITH_TEXT_VERTICAL`  |

### Size Tiers

The determinate circular indicator defaults to the large tier and also ships a medium one; the infinite indicator ships a default and a large tier, all in `ProgressIndicatorDefaults`:

```kotlin
// Medium circular progress indicator
CircularProgressIndicator(
    progress = 0.5f,
    size = ProgressIndicatorDefaults.MediumCircularProgressIndicatorSize,
    strokeWidth = ProgressIndicatorDefaults.MediumCircularProgressIndicatorStrokeWidth
)

// Large infinite progress indicator
InfiniteProgressIndicator(
    size = ProgressIndicatorDefaults.LargeInfiniteProgressIndicatorSize,
    strokeWidth = ProgressIndicatorDefaults.LargeInfiniteProgressIndicatorStrokeWidth
)
```

## Component States

All progress indicator components support both determinate and indeterminate progress states:

### Determinate Progress State

When a specific progress value (float between 0.0-1.0) is provided, the progress indicator shows exact progress:

```kotlin
var progress by remember { mutableStateOf(0.6f) }

LinearProgressIndicator(progress = progress)
CircularProgressIndicator(progress = progress)
```

### Indeterminate Progress State

When the progress value is null, the progress indicator shows an animation indicating an ongoing operation with unknown progress:

```kotlin
LinearProgressIndicator(progress = null)
CircularProgressIndicator(progress = null)
```

## Properties

### LinearProgressIndicator Properties

| Property Name | Type                    | Description                                    | Default Value                                                  | Required |
| ------------- | ----------------------- | ---------------------------------------------- | -------------------------------------------------------------- | -------- |
| modifier      | Modifier                | Modifier applied to the progress bar           | Modifier                                                       | No       |
| progress      | Float?                  | Current progress value, null for indeterminate | null                                                           | No       |
| enabled       | Boolean                 | Whether the progress bar is enabled            | true                                                           | No       |
| colors        | ProgressIndicatorColors | Color configuration for the progress bar       | ProgressIndicatorDefaults.progressIndicatorColors()            | No       |
| height        | Dp                      | Height of the progress bar                     | ProgressIndicatorDefaults.DefaultLinearProgressIndicatorHeight | No       |

### CircularProgressIndicator Properties

| Property Name | Type                    | Description                                    | Default Value                                                         | Required |
| ------------- | ----------------------- | ---------------------------------------------- | --------------------------------------------------------------------- | -------- |
| modifier      | Modifier                | Modifier applied to the progress indicator     | Modifier                                                              | No       |
| progress      | Float?                  | Current progress value, null for indeterminate | null                                                                  | No       |
| state         | CircularProgressState   | Normal, Paused or Error (determinate only)     | CircularProgressState.Normal                                          | No       |
| type          | CircularProgressType    | Default, OnImage or FollowTheme (determinate only) | CircularProgressType.Default                                      | No       |
| colors        | ProgressIndicatorColors | Color configuration for the progress indicator | circularProgressIndicatorColors() with progress, loadingViewColors() without | No       |
| strokeWidth   | Dp                      | Stroke width of the circular track             | LargeCircularProgressIndicatorStrokeWidth with progress, DefaultLoadingViewStrokeWidth without | No |
| size          | Dp                      | The length tier with progress (the box adds 4dp each side), the diameter without | LargeCircularProgressIndicatorSize with progress, DefaultLoadingViewSize without | No |

### CompProgressIndicator Properties

| Property Name | Type                      | Description                                  | Default Value                                              | Required |
| ------------- | ------------------------- | -------------------------------------------- | ---------------------------------------------------------- | -------- |
| modifier      | Modifier                  | Modifier applied to the indicator            | Modifier                                                   | No       |
| type          | CompProgressIndicatorType | The layout                                   | CompProgressIndicatorType.Large                            | No       |
| text          | String?                   | The tip, drawn by the `...WithText...` types | null                                                       | No       |
| color         | Color                     | Color of the spinner arc                     | ProgressIndicatorDefaults.rotatingProgressIndicatorColor() | No       |
| textColor     | Color                     | Color of the tip                             | CompProgressIndicatorDefaults.textColor()                  | No       |
| textStyle     | TextStyle                 | Style of the tip                             | COUITheme.textStyles.bodyS                                 | No       |

### InfiniteProgressIndicator Properties

| Property Name   | Type     | Description                        | Default Value                                                             | Required |
| --------------- | -------- | ---------------------------------- | ------------------------------------------------------------------------- | -------- |
| modifier        | Modifier | Modifier applied to the indicator  | Modifier                                                                  | No       |
| color           | Color    | Color of the arc                   | COUITheme.colorScheme.primary                                             | No       |
| size            | Dp       | Size of the indicator              | ProgressIndicatorDefaults.DefaultInfiniteProgressIndicatorSize            | No       |
| strokeWidth     | Dp       | Stroke width of the arc            | ProgressIndicatorDefaults.DefaultInfiniteProgressIndicatorStrokeWidth     | No       |

### RotatingProgressIndicator Properties

| Property Name | Type     | Description                                            | Default Value                                                            | Required |
| ------------- | -------- | ------------------------------------------------------ | ------------------------------------------------------------------------ | -------- |
| modifier      | Modifier | Modifier applied to the indicator                      | Modifier                                                                 | No       |
| color         | Color    | Color of the arc                                       | ProgressIndicatorDefaults.rotatingProgressIndicatorColor()               | No       |
| size          | Dp       | Size of the indicator's square view box                | ProgressIndicatorDefaults.DefaultRotatingProgressIndicatorSize           | No       |
| ringDiameter  | Dp       | Diameter of the arc's stroke centerline                | ProgressIndicatorDefaults.DefaultRotatingProgressIndicatorRingDiameter   | No       |
| strokeWidth   | Dp       | Stroke width of the arc                                | ProgressIndicatorDefaults.DefaultRotatingProgressIndicatorStrokeWidth    | No       |

### ProgressIndicatorDefaults Object

The ProgressIndicatorDefaults object provides default values and color configurations for progress indicator components.

#### Constants

| Constant Name                                   | Type | Default Value | Description                                     |
| ----------------------------------------------- | ---- | ------------- | ----------------------------------------------- |
| DefaultLinearProgressIndicatorHeight            | Dp   | 4.dp          | Default height of linear progress bar           |
| MediumCircularProgressIndicatorStrokeWidth      | Dp   | 3.dp          | Stroke width of the medium circular tier        |
| MediumCircularProgressIndicatorSize             | Dp   | 30.dp         | Length of the medium circular tier              |
| LargeCircularProgressIndicatorStrokeWidth       | Dp   | 5.dp          | Stroke width of the large circular tier (default) |
| LargeCircularProgressIndicatorSize              | Dp   | 40.dp         | Length of the large circular tier (default)     |
| DefaultLoadingViewStrokeWidth                   | Dp   | 2.67.dp       | Stroke width of the indeterminate circular indicator |
| DefaultLoadingViewSize                          | Dp   | 18.dp         | Size of the indeterminate circular indicator    |
| LargeLoadingViewStrokeWidth                     | Dp   | 3.33.dp       | Stroke width of the large loading view          |
| LargeLoadingViewSize                            | Dp   | 26.dp         | Size of the large loading view                  |
| DefaultInfiniteProgressIndicatorStrokeWidth     | Dp   | 2.67.dp       | Default stroke width of infinite indicator      |
| DefaultInfiniteProgressIndicatorSize            | Dp   | 18.dp         | Default size of infinite indicator              |
| LargeInfiniteProgressIndicatorStrokeWidth       | Dp   | 3.33.dp       | Stroke width of the large infinite tier         |
| LargeInfiniteProgressIndicatorSize              | Dp   | 26.dp         | Size of the large infinite tier                 |
| DefaultRotatingProgressIndicatorSize            | Dp   | 26.dp         | Default view box size of the rotating spinner   |
| DefaultRotatingProgressIndicatorRingDiameter    | Dp   | 24.14.dp      | Default ring diameter of the rotating spinner   |
| DefaultRotatingProgressIndicatorStrokeWidth     | Dp   | 1.857.dp      | Default stroke width of the rotating spinner    |
| SmallRotatingProgressIndicatorSize              | Dp   | 16.dp         | View box size of the small rotating tier        |
| SmallRotatingProgressIndicatorRingDiameter      | Dp   | 12.68.dp      | Ring diameter of the small rotating tier        |
| SmallRotatingProgressIndicatorStrokeWidth       | Dp   | 1.811.dp      | Stroke width of the small rotating tier         |
| MaxRotatingProgressIndicatorSize                | Dp   | 40.dp         | Largest supported rotating spinner size         |

#### Methods

| Method Name                       | Type                    | Description                                                       |
| --------------------------------- | ----------------------- | ----------------------------------------------------------------- |
| progressIndicatorColors()         | ProgressIndicatorColors | Creates default color configuration for linear indicators         |
| circularProgressIndicatorColors() | ProgressIndicatorColors | Creates default color configuration for the determinate circular indicator |
| loadingViewColors()               | ProgressIndicatorColors | Creates the accent color configuration for the indeterminate circular indicator |
| rotatingProgressIndicatorColor()  | Color                   | Default arc color of the rotating spinner (85% opacity)          |

### ProgressIndicatorColors Class

| Property Name           | Type  | Description                                 |
| ----------------------- | ----- | ------------------------------------------- |
| foregroundColor         | Color | Foreground color of the progress indicator  |
| disabledForegroundColor | Color | Foreground color when indicator is disabled |
| backgroundColor         | Color | Background color of the progress indicator  |
| disabledBackgroundColor | Color | Background color when indicator is disabled (defaults to backgroundColor) |
| errorColor              | Color | Ring and icon color of an errored circular indicator (defaults to foregroundColor) |
| pauseIconColor          | Color | Icon color of a paused circular indicator (defaults to foregroundColor) |
| shadowColor             | Color | Shadow of an on-image circular indicator (defaults to transparent) |

## Advanced Usage

### Custom Colored Linear Progress Bar

```kotlin
var progress by remember { mutableStateOf(0.4f) }

LinearProgressIndicator(
    progress = progress,
    colors = ProgressIndicatorDefaults.progressIndicatorColors(
        foregroundColor = Color.Red,
        backgroundColor = Color.LightGray
    )
)
```

### Resized Circular Progress Indicator

```kotlin
var progress by remember { mutableStateOf(0.75f) }

CircularProgressIndicator(
    progress = progress,
    size = 50.dp,
    strokeWidth = 6.dp
)
```

### Loading State with Button

```kotlin
var isLoading by remember { mutableStateOf(false) }
val scope = rememberCoroutineScope()

Button(
    onClick = {
        isLoading = true
        // Simulate operation
        scope.launch {
            delay(2000)
            isLoading = false
        }
    },
    enabled = !isLoading
) {
     AnimatedVisibility(
        visible = isLoading
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .padding(end = 8.dp),
            size = 20.dp,
            strokeWidth = 4.dp
        )
    }
    Text("Submit")
}
```

### Custom Infinite Progress Indicator

```kotlin
InfiniteProgressIndicator(
    color = COUITheme.colorScheme.primary,
    size = 40.dp,
    strokeWidth = 3.dp
)
```

### Loading State with Card

```kotlin
var isLoading by remember { mutableStateOf(true) }

Card(
    modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .padding(16.dp)
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Loading...")
            }
        } else {
            Text("Content Loaded")
        }
    }
}
// Control loading state
LaunchedEffect(Unit) {
    delay(3000)
    isLoading = false
}
```
