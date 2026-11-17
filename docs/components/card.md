# Card

`Card` is a basic container component in COUI, used to hold related content and actions. It provides a card container with COUI style, suitable for scenarios such as information display and content grouping. Supports both static display and interactive modes.

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=card" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.Card
import io.github.suqi8.coui.kmp.utils.PressFeedbackType // If using interactive card
```

## Basic Usage

The Card component can be used to wrap and organize content (static card):

```kotlin
Card {
    Text("This is card content")
}
```

## Properties

### Card Properties

| Property Name     | Type                               | Description                               | Default Value                | Required | Applies To  |
| ----------------- | ---------------------------------- | ----------------------------------------- | ---------------------------- | -------- | ----------- |
| modifier          | Modifier                           | Modifier applied to the card              | Modifier                     | No       | All         |
| cornerRadius      | Dp                                 | Card corner radius                        | CardDefaults.CornerRadius    | No       | All         |
| insideMargin      | PaddingValues                      | Card inner padding                        | CardDefaults.InsideMargin    | No       | All         |
| colors            | CardColors                         | Card color configuration                  | CardDefaults.defaultColors() | No       | All         |
| shadow            | CardShadow?                        | Shadow around the card (COUI `COUIShadowCardView`); null for a flat card | null | No | All |
| pressFeedbackType | PressFeedbackType                  | Feedback type when pressed                | PressFeedbackType.Tint       | No       | Interactive |
| showIndication    | Boolean                            | Show indication on interaction            | false                        | No       | Interactive |
| holdDownState     | Boolean                            | Whether the card is in the pressed state  | false                        | No       | Interactive |
| onClick           | (() -> Unit)?                      | Callback when clicked                     | null                         | No       | Interactive |
| onLongPress       | (() -> Unit)?                      | Callback when long pressed                | null                         | No       | Interactive |
| content           | @Composable ColumnScope.() -> Unit | Composable function for card content area | -                            | Yes      | All         |

::: warning
Some properties are only available when creating an interactive card!
:::

### CardDefaults Object

The CardDefaults object provides default values and color configurations for the card component.

#### Constants

| Constant Name | Type          | Description        | Default Value       |
| ------------- | ------------- | ------------------ | ------------------- |
| CornerRadius  | Dp            | Card corner radius: the ColorOS 17 card list radius (`COUIRoundCorner.Normal.XL`, COUI `coui_round_corner_xl_radius_normal_16_1`) | 24.dp |
| InsideMargin  | PaddingValues | Card inner padding | PaddingValues(0.dp) |
| ShadowSize    | Dp            | Default shadow size (`support_shadow_size_level_for_shadow_card`) | 12.dp |
| ShadowColor   | Color         | Default shadow color; only its RGB is used | Color.Black |
| ShadowOffset  | Dp            | Default shadow offset along the angle (`couiShadowOffset` falls back to 7px) | 2.dp |

#### Methods

| Method Name     | Type       | Description                |
| --------------- | ---------- | -------------------------- |
| defaultColors() | CardColors | The default color for card |
| shadow()        | CardShadow | Creates a `CardShadow`; every argument defaults to the constants above, angle 0 and no hidden side |

### CardColors Class

| Property Name | Type  | Description                                                          |
| ------------- | ----- | -------------------------------------------------------------------- |
| color         | Color | Default background color of card                                     |
| contentColor  | Color | Default content color of card                                        |
| pressedColor  | Color | Fill color the card animates towards while pressed (Tint feedback)   |

### CardShadow Class

The shadow of `COUIShadowCardView`: a `MaterialShapeDrawable` whose compat shadow spreads `0.75 x size` past the outline as a 68/255 -> 20/255 -> 0 alpha ramp of the shadow RGB. It is drawn as a Gaussian drop shadow fitted to that ramp (blur radius `0.62 x size`, alpha 136/255) on the card's own squircle outline, and follows the press scale of an interactive card.

| Property Name | Type    | Description                                                                 |
| ------------- | ------- | --------------------------------------------------------------------------- |
| size          | Dp      | Shadow size (`couiShadowSize`); 0 draws nothing                             |
| color         | Color   | Shadow color; its alpha is ignored (`couiShadowColor`)                      |
| offset        | Dp      | How far the shadow is shifted along `angle` (`couiShadowOffset`)            |
| angle         | Int     | Direction of the offset in degrees, 0 pointing down, 90 right (`couiShadowAngle`) |
| hideLeft      | Boolean | No shadow past the left edge (`couiHideLeftShadow`)                         |
| hideTop       | Boolean | No shadow past the top edge (`couiHideTopShadow`)                           |
| hideRight     | Boolean | No shadow past the right edge (`couiHideRightShadow`)                       |
| hideBottom    | Boolean | No shadow past the bottom edge (`couiHideBottomShadow`)                     |

### PressFeedbackType Options

| Option | Description                                                                                        |
| ------ | -------------------------------------------------------------------------------------------------- |
| None   | No press feedback                                                                                  |
| Tint   | Animates the fill towards `pressedColor` while pressed or held down (COUI card feedback, default)  |
| Sink   | Scales down while pressed by the COUI 17 touch motion scale: the target (0.85–0.97) follows the card's area and aspect ratio |
| Tilt   | Tilts based on the touch position when pressed                                                     |


## Advanced Usage

### Custom Style Card

```kotlin
Card(
    cornerRadius = 8.dp,
    insideMargin = PaddingValues(16.dp),
    colors = CardDefaults.defaultColors(
        color = COUITheme.colorScheme.primary
    ),
) {
    Text("Custom Style Card")
}
```

### Content-Rich Card

```kotlin
Card(
    modifier = Modifier.padding(16.dp),
    insideMargin = PaddingValues(16.dp)
) {
    Text(
        text = "Card Title",
        style = COUITheme.textStyles.displayXS
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "This is a detailed description of the card, which can contain multiple lines of text."
    )
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(
            text = "Cancel",
            onClick = { /* Handle cancel event */ }
        )
        Spacer(modifier = Modifier.width(8.dp))
        TextButton(
            text = "Confirm",
            colors = ButtonDefaults.textButtonColorsPrimary(), // Use theme colors
            onClick = { /* Handle confirm event */ }
        )
    }
}
```

### Shadow Card

```kotlin
Card(
    insideMargin = PaddingValues(16.dp),
    shadow = CardDefaults.shadow(), // 12dp shadow, shifted 2dp down
) {
    Text("Shadow card")
}

// A card attached to something above it: no shadow past its top edge
Card(shadow = CardDefaults.shadow(hideTop = true)) { /* ... */ }
```

The shadow is drawn outside the card bounds, so leave room for it (padding) and keep clipping parents out of its way. `COUIShadowCardView`'s per-corner radii and stroke are not ported; the corners adjacent to a hidden side keep their round shape.

### Cards in a List

```kotlin
LazyColumn {
    items(5) { index ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            insideMargin = PaddingValues(16.dp)
        ) {
            Text("List Item ${index + 1}")
        }
    }
}
```

### Interactive Card

```kotlin
Card(
    modifier = Modifier.padding(16.dp),
    // pressFeedbackType defaults to PressFeedbackType.Tint: the fill animates
    // towards CardColors.pressedColor while pressed (COUI card press feedback)
    onClick = {/* Handle click event */ },
    onLongPress = {/* Handle long press event */ }
) {
    Text("Interactive Card")
}
```

```kotlin
Card(
    modifier = Modifier.padding(16.dp),
    pressFeedbackType = PressFeedbackType.Sink, // Set press feedback to sink effect
    showIndication = true, // Show indication on click
    onClick = {/* Handle click event */ },
) {
    Text("Interactive Card (Sink)")
}
```

### Card Group Row Positions

COUI grows the rounded outer edges of a card group by `coui_list_card_head_or_tail_padding` (2dp).
Pass a `CardListPosition` to the preference rows stacked inside a `Card` so each row gets the padding
on the edges that are actually rounded, or let a [`CardListGroup`](#cardlistgroup) assign them.
Outside a group a row defaults to `CardListPosition.None`, which adds nothing.

| Position | Extra top | Extra bottom | Total |
| :------- | :-------- | :----------- | :---- |
| `None` (default) | 0dp | 0dp | +0dp |
| `Head` | 2dp | 0dp | +2dp |
| `Middle` | 0dp | 0dp | +0dp |
| `Tail` | 0dp | 2dp | +2dp |
| `Full` (only row) | 2dp | 2dp | +4dp |

Use `cardListPositionOf(index, count)` to derive the position while iterating a list. It mirrors
COUI's `COUICardListHelper.getPositionInGroup(int, int)`, returning `Full` when `count == 1`.

```kotlin
val options = listOf("Every day", "Workdays only", "Never")

Card {
    options.forEachIndexed { index, option ->
        if (index > 0) HorizontalDivider(Modifier.padding(horizontal = 16.dp))
        MarkPreference(
            title = option,
            checked = selected == index,
            onClick = { selected = index },
            cardListPosition = cardListPositionOf(index, options.size),
        )
    }
}
```

### CardListGroup

`CardListGroup` numbers the rows for you: the first row is `Head`, the last `Tail`, the rows
between `Middle`, and a single row `Full`. Declare each row with `item { }`; each row is wrapped in a plain
`Column` emitted into the `Card`'s column. Each row composable (`BasicComponent` and the preference
rows) reads its position from `LocalCardListPosition` as the default of `cardListPosition`.

The group also draws the COUI row dividers (`COUIPreferenceItemDecoration`): below each `Head` or
`Middle` row built on `BasicComponent`, starting at the row's title and ending 16dp before the card
edge. The divider hides when that row or the row below is `selected`; pressing does not hide it. Do
not add `HorizontalDivider`s between grouped rows. `CardListGroup(showDividers = false)` turns the
dividers off, and `item(showDivider = false) { }` hides the one below a single row. See
[CardListGroup](./cardlistgroup.md).

```kotlin
import io.github.suqi8.coui.kmp.basic.CardListGroup

Card {
    CardListGroup {
        item {
            SwitchPreference(title = "Wi-Fi", checked = wifi, onCheckedChange = { wifi = it })
        }
        if (wifi) {
            item(key = "networks") {
                ArrowPreference(title = "Networks", onClick = {})
            }
        }
        options.forEach { option ->
            item(key = option) {
                MarkPreference(title = option, checked = selected == option, onClick = { selected = option })
            }
        }
    }
}
```

- Keep one row per `item`; a dialog that belongs to the row can share its `item`.
- Positions follow the rows present, so conditional rows (`if (...) item { }`) are numbered correctly.
  Give such rows and looped rows a `key` so they keep their state when rows before them change.
- Rows outside a group keep `CardListPosition.None`, and dialogs, sheets and popups reset it to `None`
  for their content.
- A row that passes `cardListPosition` explicitly keeps it; the group only supplies the default.
