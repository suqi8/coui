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
| CornerRadius  | Dp            | Card corner radius | 12.dp               |
| InsideMargin  | PaddingValues | Card inner padding | PaddingValues(0.dp) |

#### Methods

| Method Name     | Type       | Description                |
| --------------- | ---------- | -------------------------- |
| defaultColors() | CardColors | The default color for card |

### CardColors Class

| Property Name | Type  | Description                                                          |
| ------------- | ----- | -------------------------------------------------------------------- |
| color         | Color | Default background color of card                                     |
| contentColor  | Color | Default content color of card                                        |
| pressedColor  | Color | Fill color the card animates towards while pressed (Tint feedback)   |

### PressFeedbackType Options

| Option | Description                                                                                        |
| ------ | -------------------------------------------------------------------------------------------------- |
| None   | No press feedback                                                                                  |
| Tint   | Animates the fill towards `pressedColor` while pressed or held down (COUI card feedback, default)  |
| Sink   | Sinks (scales down) slightly when pressed                                                          |
| Tilt   | Tilts based on the touch position when pressed                                                     |


## Advanced Usage

### Custom Style Card

```kotlin
Card(
    cornerRadius = 8.dp,
    insideMargin = PaddingValues(16.dp),
    colors = CardDefaults.defaultColors(
        color = COUITheme.colorScheme.primaryVariant
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
        style = COUITheme.textStyles.title2
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
on the edges that are actually rounded. `CardListPosition.None` (the default) adds nothing, so
existing call sites are unaffected.

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
