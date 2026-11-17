# MarkWithDividerPreference

`MarkWithDividerPreference` is a preference row split in two, mirroring COUI's `COUIMarkWithDividerPreference` (layout `coui_mark_with_divider_preference`, style `Preference.COUI.COUIRadioWithDividerPreference`): a clickable main area that usually opens a detail page, a vertical divider, and a selection mark selected independently. Rows are usually used as a single-select group, e.g. a list of saved networks where the mark picks the active one.

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=markWithDividerPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.preference.MarkWithDividerPreference
```

## Basic Usage

```kotlin
val networks = listOf("Home-5G", "Office")
var selected by remember { mutableIntStateOf(0) }

Card {
    CardListGroup {
        networks.forEachIndexed { index, name ->
            item {
                MarkWithDividerPreference(
                    checked = selected == index,
                    onCheckedChange = { if (it) selected = index },
                    title = name,
                    onClick = { /* open the detail page */ }
                )
            }
        }
    }
}
```

## Interaction

- The main area is a `BasicComponent`: only it shows the press feedback, receives the card-group padding, and runs `onClick`.
- The trailing area (divider + mark) is as tall as the main area and is the mark's touch target (the mark itself is not clickable). A tap calls `onCheckedChange` with the inverted state, as COUI's `TwoStatePreference.onClick` does; for a single-select group, ignore `false`. No haptic is played and no press feedback is drawn. Accessibility services see the area as a radio button.
- The divider is 1dp x 22dp in `?couiColorDivider`, 16dp before the mark; the mark keeps 16dp to the row end inside the card.

## Component States

### Disabled State

```kotlin
MarkWithDividerPreference(
    checked = true,
    onCheckedChange = {},
    title = "Disabled",
    onClick = {},
    enabled = false
)
```

## Properties

### MarkWithDividerPreference Properties

| Property Name    | Type                               | Description                                                     | Default Value                         | Required |
| ---------------- | ---------------------------------- | --------------------------------------------------------------- | ------------------------------------- | -------- |
| checked          | Boolean                            | Whether this row carries the mark                               | -                                     | Yes      |
| onCheckedChange  | (Boolean) -> Unit                  | Callback when the mark area is tapped, with the inverted state  | -                                     | Yes      |
| title            | String                             | Preference title                                                | -                                     | Yes      |
| modifier         | Modifier                           | Component modifier                                              | Modifier                              | No       |
| onClick          | (() -> Unit)?                      | Main area click callback; null keeps it non-clickable           | null                                  | No       |
| titleColor       | BasicComponentColors               | Title text color configuration                                  | BasicComponentDefaults.titleColor()   | No       |
| summary          | String?                            | Preference summary                                              | null                                  | No       |
| summaryColor     | BasicComponentColors               | Summary text color configuration                                | BasicComponentDefaults.summaryColor() | No       |
| startAction      | @Composable (() -> Unit)?          | Custom start side content of the main area                      | null                                  | No       |
| endActions       | @Composable (RowScope.() -> Unit)? | Custom end side content of the main area (before the divider)   | null                                  | No       |
| bottomAction     | @Composable (() -> Unit)?          | Custom bottom content of the main area                          | null                                  | No       |
| markColors       | RadioButtonColors                  | Mark color configuration                                        | RadioButtonDefaults.radioButtonColors() | No     |
| dividerColor     | Color                              | Vertical divider color                                          | DividerDefaults.DividerColor          | No       |
| insideMargin     | PaddingValues                      | Main area internal content padding                              | BasicComponentDefaults.InsideMargin   | No       |
| cardListPosition | CardListPosition | Row position inside its card group ([CardListGroup](./card.md#cardlistgroup)); the main area gains the extra padding | LocalCardListPosition.current (None outside a CardListGroup) | No |
| holdDownState    | Boolean                            | Whether the main area is held down                              | false                                 | No       |
| enabled          | Boolean                            | Whether the main area and the mark are enabled                  | true                                  | No       |
