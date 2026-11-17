# StatusSwitchingPreference

`StatusSwitchingPreference` is a preference row whose end widget switches between a status text, a status icon and a loading spinner, mirroring ColorOS's `COUIStatusSwitchingPreference`. A typical use is a row that shows "On" / "Off" and shows the spinner while the change is being applied. The status text uses the 14sp body style in the secondary label color, and the widget keeps 16dp to the title (`coui_status_switching_preference_widget_margin_start`).

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=statusSwitchingPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.preference.StatusSwitchingPreference
import io.github.suqi8.coui.kmp.preference.StatusSwitchingPreferenceDefaults
```

## Basic Usage

```kotlin
var enabled by remember { mutableStateOf(false) }
var switching by remember { mutableStateOf(false) }

LaunchedEffect(switching) {
    if (switching) {
        delay(1500) // apply the change
        enabled = !enabled
        switching = false
    }
}

StatusSwitchingPreference(
    title = "Hotspot",
    status = if (enabled) "On" else "Off",
    switching = switching,
    onClick = { if (!switching) switching = true }
)
```

## Widget Selection

COUI shows whichever widget its last `showText` / `showIcon` / `showAnim` call selected. Here the widget is derived from the parameters, in this order:

| Condition              | Widget                             | COUI call    |
| ---------------------- | ---------------------------------- | ------------ |
| `switching = true`     | Rotating loading spinner           | `showAnim`   |
| `statusIcon != null`   | The icon                           | `showIcon`   |
| `status != null`       | The status text                    | `showText`   |
| otherwise              | No widget                          | (default)    |

The original plays a caller-supplied Lottie resource in a loop for `showAnim`; this port uses the COUI rotating spinner instead.

## Properties

### StatusSwitchingPreference Properties

| Property Name    | Type                      | Description                                        | Default Value                                       | Required |
| ---------------- | ------------------------- | -------------------------------------------------- | --------------------------------------------------- | -------- |
| title            | String                    | Preference title                                   | -                                                   | Yes      |
| modifier         | Modifier                  | Component modifier                                 | Modifier                                            | No       |
| status           | String?                   | Status text                                        | null                                                | No       |
| switching        | Boolean                   | Whether the status is being switched (spinner)     | false                                               | No       |
| statusIcon       | @Composable (() -> Unit)? | Status icon, shown instead of `status`             | null                                                | No       |
| titleColor       | BasicComponentColors      | Title text color configuration                     | BasicComponentDefaults.titleColor()                 | No       |
| summary          | String?                   | Preference summary                                 | null                                                | No       |
| summaryColor     | BasicComponentColors      | Summary text color configuration                   | BasicComponentDefaults.summaryColor()               | No       |
| statusColor      | Color                     | Color of the status text                           | StatusSwitchingPreferenceDefaults.statusColor()     | No       |
| indicatorColor   | Color                     | Color of the spinner                               | ProgressIndicatorDefaults.rotatingProgressIndicatorColor() | No |
| indicatorSize    | Dp                        | Size of the spinner                                | ProgressIndicatorDefaults.DefaultRotatingProgressIndicatorSize | No |
| startAction      | @Composable (() -> Unit)? | Custom start side content                          | null                                                | No       |
| bottomAction     | @Composable (() -> Unit)? | Custom bottom side content                         | null                                                | No       |
| insideMargin     | PaddingValues             | Component internal content padding                 | BasicComponentDefaults.InsideMargin                 | No       |
| cardListPosition | CardListPosition          | Row position inside its card group                 | LocalCardListPosition.current                       | No       |
| onClick          | (() -> Unit)?             | Callback when the row is clicked                   | null                                                | No       |
| holdDownState    | Boolean                   | Whether the component is held down                 | false                                               | No       |
| enabled          | Boolean                   | Component interactive state                        | true                                                | No       |

### StatusSwitchingPreferenceDefaults Object

| Name              | Type  | Default Value                         | Description                                            |
| ----------------- | ----- | ------------------------------------- | ------------------------------------------------------ |
| WidgetMarginStart | Dp    | 16.dp                                 | COUI coui_status_switching_preference_widget_margin_start |
| statusColor()     | Color | COUITheme.colorScheme.labelSecondary | COUI ?couiColorLabelSecondary                       |

## Advanced Usage

### Status Icon

```kotlin
StatusSwitchingPreference(
    title = "Backup",
    statusIcon = {
        Icon(
            imageVector = COUIIcons.Ok,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = COUITheme.colorScheme.primary
        )
    }
)
```
