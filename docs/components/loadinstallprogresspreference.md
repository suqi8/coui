# LoadInstallProgressPreference

`LoadInstallProgressPreference` is a preference row with a download / install progress button at the end, mirroring ColorOS's `COUILoadInstallProgressPreference`. The widget is the small [InstallLoadProgress](./installloadprogress) capsule (`Widget.COUI.COUILoadProgress.InstallDownloadSmall`); the preference only forwards its label, state, progress and colors to it. Clicking the capsule calls `onProgressClick`, independently from the row click.

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=loadInstallProgressPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.InstallLoadState
import io.github.suqi8.coui.kmp.preference.LoadInstallProgressPreference
```

## Basic Usage

```kotlin
var state by remember { mutableStateOf(InstallLoadState.Idle) }
var progress by remember { mutableFloatStateOf(0f) }

LoadInstallProgressPreference(
    title = "Weather",
    summary = "18.6 MB",
    progressText = if (state == InstallLoadState.Loading) "${(progress * 100).toInt()}%" else "Install",
    state = state,
    progress = progress,
    onProgressClick = {
        state = if (state == InstallLoadState.Loading) InstallLoadState.Paused else InstallLoadState.Loading
    }
)
```

The caller owns the state machine: COUI reports state changes through `OnStateChangeListener`, which maps to updating `state` inside `onProgressClick`.

## Properties

### LoadInstallProgressPreference Properties

| Property Name     | Type                            | Description                                         | Default Value                                         | Required |
| ----------------- | ------------------------------- | --------------------------------------------------- | ----------------------------------------------------- | -------- |
| title             | String                          | Preference title                                    | -                                                     | Yes      |
| progressText      | String                          | Label of the progress button                        | -                                                     | Yes      |
| onProgressClick   | () -> Unit                      | Callback when the progress button is clicked        | -                                                     | Yes      |
| modifier          | Modifier                        | Component modifier                                  | Modifier                                              | No       |
| state             | InstallLoadState                | State of the progress button                        | InstallLoadState.Idle                                 | No       |
| progress          | Float                           | Progress between 0 and 1 (Loading / Paused)         | 0f                                                    | No       |
| titleColor        | BasicComponentColors            | Title text color configuration                      | BasicComponentDefaults.titleColor()                   | No       |
| summary           | String?                         | Preference summary                                  | null                                                  | No       |
| summaryColor      | BasicComponentColors            | Summary text color configuration                    | BasicComponentDefaults.summaryColor()                 | No       |
| progressColors    | InstallLoadProgressColors       | Colors of the progress button                       | InstallLoadProgressDefaults.installLoadProgressColors() | No     |
| progressTextStyle | TextStyle                       | Text style of the progress label (14sp)             | COUITheme.textStyles.buttonM                         | No       |
| startAction       | @Composable (() -> Unit)?       | Custom start side content                           | null                                                  | No       |
| endActions        | @Composable RowScope.() -> Unit | Custom end side content (before the button)         | {}                                                    | No       |
| bottomAction      | @Composable (() -> Unit)?       | Custom bottom side content                          | null                                                  | No       |
| insideMargin      | PaddingValues                   | Component internal content padding                  | BasicComponentDefaults.InsideMargin                   | No       |
| cardListPosition  | CardListPosition                | Row position inside its card group                  | LocalCardListPosition.current                         | No       |
| onClick           | (() -> Unit)?                   | Callback when the row (not the button) is clicked   | null                                                  | No       |
| holdDownState     | Boolean                         | Whether the component is held down                  | false                                                 | No       |
| enabled           | Boolean                         | Whether the row and the button are enabled          | true                                                  | No       |

`LoadInstallProgressPreference` has no Defaults object of its own; sizes and colors come from [InstallLoadProgressDefaults](./installloadprogress).

## Advanced Usage

### Failed State

```kotlin
LoadInstallProgressPreference(
    title = "Calculator",
    summary = "Download failed",
    progressText = "Retry",
    state = InstallLoadState.Failed,
    onProgressClick = { /* retry */ }
)
```

### Custom Colors

```kotlin
LoadInstallProgressPreference(
    title = "Game",
    progressText = "Update",
    onProgressClick = {},
    progressColors = InstallLoadProgressDefaults.installLoadProgressColors(
        progressColor = COUITheme.colorScheme.error,
        contentColor = COUITheme.colorScheme.error
    )
)
```
