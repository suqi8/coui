# CardListGroup

`CardListGroup` groups the rows stacked in a [Card](./card.md) the way a COUI preference group does:
it gives each row its card list position (the 2dp head / tail padding on the rounded edges) and draws
the row dividers.

<div style="position: relative; height: 420px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=cardListGroup" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.CardListGroup
```

## Basic Usage

```kotlin
Card(modifier = Modifier.padding(horizontal = 16.dp)) {
    CardListGroup {
        item { SwitchPreference(title = "WLAN", checked = wifi, onCheckedChange = { wifi = it }) }
        item { ArrowPreference(title = "Bluetooth", onClick = {}) }
        item { ArrowPreference(title = "Mobile network", onClick = {}) }
    }
}
```

## Positions

The first row is `CardListPosition.Head`, the last `Tail`, the rows between `Middle`, and a single row
`Full` (`COUICardListHelper.getPositionInGroup`). Each row reads it from `LocalCardListPosition` as the
default of its `cardListPosition`; a row that passes `cardListPosition` explicitly keeps its own.
Positions follow the rows present, so conditional rows (`if (...) item { }`) are numbered correctly.

## Dividers

The group draws the COUI row dividers (`COUIPreferenceItemDecoration`):

- Below every `Head` or `Middle` row built on `BasicComponent` (all preference rows are). Rows of other
  composables draw none, as COUI only draws dividers for its preferences.
- The divider starts at the row's title, so it lines up past a start icon, and ends 16dp
  (`DividerDefaults.CardInset`) before the card edge. Color `DividerDefaults.DividerColor`, thickness
  `DividerDefaults.Thickness` rounded to whole pixels with a one-pixel floor.
- It is hidden while that row or the row below it is `selected`; pressing a row does not hide it.

Do not add `HorizontalDivider`s between grouped rows.

```kotlin
Card {
    CardListGroup(showDividers = true) {
        options.forEachIndexed { index, option ->
            item(key = option, showDivider = true) {
                BasicComponent(
                    title = option,
                    selected = selectedIndex == index,
                    onClick = { selectedIndex = index },
                )
            }
        }
    }
}
```

## Properties

### CardListGroup Properties

| Property Name | Type | Description | Default Value | Required |
| ------------- | ---- | ----------- | ------------- | -------- |
| showDividers | Boolean | Whether the group draws row dividers at all (COUIPreferenceFragment `mEnableInternalDivider`) | true | No |
| content | CardListGroupScope.() -> Unit | Declares the rows with `item` | - | Yes |

### CardListGroupScope.item

| Parameter | Type | Description | Default Value |
| --------- | ---- | ----------- | ------------- |
| key | Any? | Stable key, so the row keeps its state when rows before it change | null |
| showDivider | Boolean | Whether the divider below this row is drawn (COUI `couiShowDivider`) | true |
| content | @Composable () -> Unit | The row | - |

## Notes

- Keep one row per `item`; a dialog that belongs to the row can share its `item`.
- Each row is wrapped in a plain `Column`.
- Dialogs, sheets and popups reset `LocalCardListPosition` to `None` for their content, so rows inside
  them neither get the group padding nor report to the group.
