# ProgressIndicator

`ProgressIndicator` is a progress indication component in COUI used to display the progress status of operations. It provides four styles: linear progress bar, circular progress indicator, infinite spinning indicator, and the rotating spinner, suitable for different loading and progress display scenarios.

<div style="position: relative; height: 250px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=progressIndicator" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.LinearProgressIndicator // Linear progress bar
import io.github.suqi8.coui.kmp.basic.CircularProgressIndicator // Circular progress indicator
import io.github.suqi8.coui.kmp.basic.InfiniteProgressIndicator // Infinite spinning indicator
import io.github.suqi8.coui.kmp.basic.RotatingProgressIndicator // Rotating spinner
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

### Infinite Progress Indicator

Infinite progress indicator is suitable for scenarios where operation duration is uncertain. It is tinted with the theme accent color by default, matching the ColorOS "Refreshing…" spinner:

```kotlin
InfiniteProgressIndicator()
```

### Rotating Spinner

`RotatingProgressIndicator` is the ColorOS system-default indeterminate spinner: a bare stroked arc with flat caps and no background ring, whose sweep pulses between 273.6° and 50.4° while the arc spins twice per 1250 ms cycle. It is a direct port of the `coui_rotating_loading.json` asset that `Theme.COUI` binds to the `couiRotatingSpinnerJsonName` attribute, reproduced with a single `drawArc` so it needs no Lottie runtime.

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

### Size Tiers

Circular and infinite indicators ship with a medium (default) and a large tier defined in `ProgressIndicatorDefaults`:

```kotlin
// Large circular progress indicator
CircularProgressIndicator(
    size = ProgressIndicatorDefaults.LargeCircularProgressIndicatorSize,
    strokeWidth = ProgressIndicatorDefaults.LargeCircularProgressIndicatorStrokeWidth
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
| colors        | ProgressIndicatorColors | Color configuration for the progress bar       | ProgressIndicatorDefaults.progressIndicatorColors()            | No       |
| height        | Dp                      | Height of the progress bar                     | ProgressIndicatorDefaults.DefaultLinearProgressIndicatorHeight | No       |

### CircularProgressIndicator Properties

| Property Name | Type                    | Description                                    | Default Value                                                         | Required |
| ------------- | ----------------------- | ---------------------------------------------- | --------------------------------------------------------------------- | -------- |
| modifier      | Modifier                | Modifier applied to the progress indicator     | Modifier                                                              | No       |
| progress      | Float?                  | Current progress value, null for indeterminate | null                                                                  | No       |
| colors        | ProgressIndicatorColors | Color configuration for the progress indicator | ProgressIndicatorDefaults.circularProgressIndicatorColors()           | No       |
| strokeWidth   | Dp                      | Stroke width of the circular track             | ProgressIndicatorDefaults.DefaultCircularProgressIndicatorStrokeWidth | No       |
| size          | Dp                      | Size of the circular indicator                 | ProgressIndicatorDefaults.DefaultCircularProgressIndicatorSize        | No       |

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
| color         | Color    | Color of the arc                                       | COUITheme.colorScheme.onSurfaceContainer                                 | No       |
| size          | Dp       | Size of the indicator's square view box                | ProgressIndicatorDefaults.DefaultRotatingProgressIndicatorSize           | No       |
| ringDiameter  | Dp       | Diameter of the arc's stroke centerline                | ProgressIndicatorDefaults.DefaultRotatingProgressIndicatorRingDiameter   | No       |
| strokeWidth   | Dp       | Stroke width of the arc                                | ProgressIndicatorDefaults.DefaultRotatingProgressIndicatorStrokeWidth    | No       |

### ProgressIndicatorDefaults Object

The ProgressIndicatorDefaults object provides default values and color configurations for progress indicator components.

#### Constants

| Constant Name                                   | Type | Default Value | Description                                     |
| ----------------------------------------------- | ---- | ------------- | ----------------------------------------------- |
| DefaultLinearProgressIndicatorHeight            | Dp   | 4.dp          | Default height of linear progress bar           |
| DefaultCircularProgressIndicatorStrokeWidth     | Dp   | 3.dp          | Default stroke width of circular indicator      |
| DefaultCircularProgressIndicatorSize            | Dp   | 30.dp         | Default size of circular indicator              |
| DefaultInfiniteProgressIndicatorStrokeWidth     | Dp   | 2.67.dp       | Default stroke width of infinite indicator      |
| DefaultInfiniteProgressIndicatorSize            | Dp   | 18.dp         | Default size of infinite indicator              |
| LargeCircularProgressIndicatorStrokeWidth       | Dp   | 5.dp          | Stroke width of the large circular tier         |
| LargeCircularProgressIndicatorSize              | Dp   | 40.dp         | Size of the large circular tier                 |
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
| circularProgressIndicatorColors() | ProgressIndicatorColors | Creates default color configuration for the circular indicator    |

### ProgressIndicatorColors Class

| Property Name           | Type  | Description                                 |
| ----------------------- | ----- | ------------------------------------------- |
| foregroundColor         | Color | Foreground color of the progress indicator  |
| disabledForegroundColor | Color | Foreground color when indicator is disabled |
| backgroundColor         | Color | Background color of the progress indicator  |

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
