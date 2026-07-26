# SwitchLoadingPreference

`SwitchLoadingPreference` is a switch preference row with a loading state, mirroring ColorOS's `COUISwitchLoadingPreference`. It looks like a `SwitchPreference`, but while `isLoading` is `true` the switch thumb shows the COUI loading spinner and neither the row nor the switch can be toggled. It is meant for settings that take effect asynchronously (e.g. toggling a network feature).

## Import

```kotlin
import com.suqi8.coui.kmp.preference.SwitchLoadingPreference
```

## Basic Usage

The typical flow: the user taps the row, `onCheckedChange` fires, the caller turns the loading state on, performs the asynchronous work, then updates `checked` and turns the loading state off.

```kotlin
var checked by remember { mutableStateOf(false) }
var target by remember { mutableStateOf(false) }
var isLoading by remember { mutableStateOf(false) }

LaunchedEffect(isLoading) {
    if (isLoading) {
        delay(1500) // Simulate asynchronous work
        checked = target
        isLoading = false
    }
}

SwitchLoadingPreference(
    checked = checked,
    onCheckedChange = {
        target = it
        isLoading = true
    },
    title = "Async Switch",
    summary = "Applies the change after a short delay",
    isLoading = isLoading
)
```

## Component States

### Loading State

While loading, clicks on the row and the switch are ignored (COUISwitch swallows touch while its loading style is active):

```kotlin
SwitchLoadingPreference(
    checked = true,
    onCheckedChange = {},
    title = "Loading Switch",
    isLoading = true
)
```

### Disabled State

```kotlin
SwitchLoadingPreference(
    checked = true,
    onCheckedChange = {},
    title = "Disabled Switch",
    enabled = false
)
```

## Properties

### SwitchLoadingPreference Properties

| Property Name   | Type                            | Description                                    | Default Value                         | Required |
| --------------- | ------------------------------- | ---------------------------------------------- | ------------------------------------- | -------- |
| checked         | Boolean                         | Switch checked state                           | -                                     | Yes      |
| onCheckedChange | (Boolean) -> Unit               | Switch state change callback (not invoked while loading) | -                            | Yes      |
| title           | String                          | Preference title                               | -                                     | Yes      |
| modifier        | Modifier                        | Component modifier                             | Modifier                              | No       |
| isLoading       | Boolean                         | Whether the switch shows the loading spinner; the row cannot be toggled while loading | false | No |
| titleColor      | BasicComponentColors            | Title text color configuration                 | BasicComponentDefaults.titleColor()   | No       |
| summary         | String?                         | Preference summary                             | null                                  | No       |
| summaryColor    | BasicComponentColors            | Summary text color configuration               | BasicComponentDefaults.summaryColor() | No       |
| startAction     | @Composable (() -> Unit)?       | Custom start side content                      | null                                  | No       |
| endActions      | @Composable RowScope.() -> Unit | Custom end side content (before the switch)    | {}                                    | No       |
| bottomAction    | @Composable (() -> Unit)?       | Custom bottom side content                     | null                                  | No       |
| switchColors    | SwitchColors                    | Switch control color configuration             | SwitchDefaults.switchColors()         | No       |
| insideMargin    | PaddingValues                   | Component internal content padding             | BasicComponentDefaults.InsideMargin   | No       |
| holdDownState   | Boolean                         | Whether the component is held down             | false                                 | No       |
| enabled         | Boolean                         | Component interactive state                    | true                                  | No       |

## Advanced Usage

### With ViewModel-Driven Async Toggle

```kotlin
SwitchLoadingPreference(
    checked = uiState.wifiEnabled,
    onCheckedChange = { viewModel.setWifiEnabled(it) }, // Flips uiState.isApplying while working
    title = "WiFi",
    summary = "Turn on to connect to wireless networks",
    isLoading = uiState.isApplying
)
```

### With End Actions

```kotlin
SwitchLoadingPreference(
    checked = checked,
    onCheckedChange = { /* start async work */ },
    title = "Sync",
    isLoading = isLoading,
    endActions = {
        Text(
            text = if (isLoading) "Applying…" else "",
            color = COUITheme.colorScheme.onSurfaceVariantActions,
            modifier = Modifier.padding(end = 6.dp)
        )
    }
)
```
