# CheckboxWithDividerPreference

`CheckboxWithDividerPreference` is a preference row split in two, mirroring COUI's `COUICheckBoxWithDividerPreference` (layout `coui_check_box_divider_preference`): a clickable main area that usually opens a detail page, a vertical divider, and a checkbox toggled independently.

<div style="position: relative; height: 230px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=checkboxWithDividerPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.preference.CheckboxWithDividerPreference
```

## Basic Usage

```kotlin
var checked by remember { mutableStateOf(true) }

CheckboxWithDividerPreference(
    checked = checked,
    onCheckedChange = { checked = it },
    title = "Photos",
    summary = "Tap the row to choose albums",
    onClick = { /* open the detail page */ }
)
```

## Interaction

- The main area is a `BasicComponent`: only it shows the press feedback, receives the card-group padding, and runs `onClick`.
- The trailing area (divider + checkbox) is as tall as the main area. The whole area is the checkbox's touch target (the checkbox itself is not clickable). As in COUI, which builds this row on the androidx `CheckBoxPreference`, no haptic is played. It draws no press feedback.
- The divider is 1dp x 22dp in `?couiColorDivider`, 16dp before the checkbox.

## Component States

### Disabled State

```kotlin
CheckboxWithDividerPreference(
    checked = true,
    onCheckedChange = {},
    title = "Disabled",
    onClick = {},
    enabled = false
)
```

## Properties

### CheckboxWithDividerPreference Properties

| Property Name   | Type                               | Description                                | Default Value                         | Required |
| --------------- | ---------------------------------- | ------------------------------------------ | ------------------------------------- | -------- |
| checked         | Boolean                            | Checked state of the checkbox          | -                                     | Yes      |
| onCheckedChange | (Boolean) -> Unit                  | Callback when the checkbox is toggled  | -                                     | Yes      |
| title           | String                             | Preference title                           | -                                     | Yes      |
| modifier        | Modifier                           | Component modifier                         | Modifier                              | No       |
| onClick         | (() -> Unit)?                      | Main area click callback; null keeps it non-clickable | null                       | No       |
| titleColor      | BasicComponentColors               | Title text color configuration             | BasicComponentDefaults.titleColor()   | No       |
| summary         | String?                            | Preference summary                         | null                                  | No       |
| summaryColor    | BasicComponentColors               | Summary text color configuration           | BasicComponentDefaults.summaryColor() | No       |
| startAction     | @Composable (() -> Unit)?          | Custom start side content of the main area | null                                  | No       |
| endActions      | @Composable (RowScope.() -> Unit)? | Custom end side content of the main area (before the divider) | null               | No       |
| bottomAction    | @Composable (() -> Unit)?          | Custom bottom content of the main area     | null                                  | No       |
| checkboxColors  | CheckboxColors                      | checkbox color configuration           | CheckboxDefaults.checkboxColors()                      | No       |
| dividerColor    | Color                              | Vertical divider color                     | DividerDefaults.DividerColor          | No       |
| insideMargin    | PaddingValues                      | Main area internal content padding         | BasicComponentDefaults.InsideMargin   | No       |
| cardListPosition | CardListPosition | Row position inside its card group ([CardListGroup](./card.md#cardlistgroup)); the main area gains the extra padding | LocalCardListPosition.current (None outside a CardListGroup) | No |
| holdDownState   | Boolean                            | Whether the main area is held down         | false                                 | No       |
| enabled         | Boolean                            | Whether the main area and the checkbox are enabled | true                      | No       |
