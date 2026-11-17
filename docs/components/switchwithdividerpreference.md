# SwitchWithDividerPreference

`SwitchWithDividerPreference` is a preference row split in two, mirroring COUI's `COUISwitchWithDividerPreference` (layout `coui_switch_with_divider_preference`): a clickable main area that usually opens a detail page, a vertical divider, and a switch toggled independently.

<div style="position: relative; height: 230px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=switchWithDividerPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.preference.SwitchWithDividerPreference
```

## Basic Usage

```kotlin
var checked by remember { mutableStateOf(true) }

SwitchWithDividerPreference(
    checked = checked,
    onCheckedChange = { checked = it },
    title = "WLAN",
    summary = "Connected",
    onClick = { /* open the detail page */ }
)
```

## Interaction

- The main area is a `BasicComponent`: only it shows the press feedback, receives the card-group padding, and runs `onClick`.
- The trailing area (divider + switch) is as tall as the main area. A tap anywhere in it toggles the switch with the switch haptic; the switch itself also handles taps and thumb drags. Unlike `SwitchPreference`, pressing the switch does not press the main area. It draws no press feedback.
- The divider is 1dp x 22dp in `?couiColorDivider`, 16dp before the switch.

## Component States

### Disabled State

```kotlin
SwitchWithDividerPreference(
    checked = true,
    onCheckedChange = {},
    title = "Disabled",
    onClick = {},
    enabled = false
)
```

## Properties

### SwitchWithDividerPreference Properties

| Property Name   | Type                               | Description                                | Default Value                         | Required |
| --------------- | ---------------------------------- | ------------------------------------------ | ------------------------------------- | -------- |
| checked         | Boolean                            | Checked state of the switch          | -                                     | Yes      |
| onCheckedChange | (Boolean) -> Unit                  | Callback when the switch is toggled  | -                                     | Yes      |
| title           | String                             | Preference title                           | -                                     | Yes      |
| modifier        | Modifier                           | Component modifier                         | Modifier                              | No       |
| onClick         | (() -> Unit)?                      | Main area click callback; null keeps it non-clickable | null                       | No       |
| titleColor      | BasicComponentColors               | Title text color configuration             | BasicComponentDefaults.titleColor()   | No       |
| summary         | String?                            | Preference summary                         | null                                  | No       |
| summaryColor    | BasicComponentColors               | Summary text color configuration           | BasicComponentDefaults.summaryColor() | No       |
| startAction     | @Composable (() -> Unit)?          | Custom start side content of the main area | null                                  | No       |
| endActions      | @Composable (RowScope.() -> Unit)? | Custom end side content of the main area (before the divider) | null               | No       |
| bottomAction    | @Composable (() -> Unit)?          | Custom bottom content of the main area     | null                                  | No       |
| switchColors  | SwitchColors                      | switch color configuration           | SwitchDefaults.switchColors()                      | No       |
| dividerColor    | Color                              | Vertical divider color                     | DividerDefaults.DividerColor          | No       |
| insideMargin    | PaddingValues                      | Main area internal content padding         | BasicComponentDefaults.InsideMargin   | No       |
| cardListPosition | CardListPosition | Row position inside its card group ([CardListGroup](./card.md#cardlistgroup)); the main area gains the extra padding | LocalCardListPosition.current (None outside a CardListGroup) | No |
| holdDownState   | Boolean                            | Whether the main area is held down         | false                                 | No       |
| enabled         | Boolean                            | Whether the main area and the switch are enabled | true                      | No       |
