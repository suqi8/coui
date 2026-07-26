# Divider

`Divider` is a basic layout component in COUI used to separate content in lists and layouts. It provides both horizontal and vertical divider styles.

<div style="position: relative; height: 160px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=divider" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.HorizontalDivider // Horizontal divider
import io.github.suqi8.coui.kmp.basic.VerticalDivider   // Vertical divider
```

## Basic Usage

### Horizontal Divider

Horizontal divider is used to separate vertically arranged content:

```kotlin
Column {
    Text("Content Above")
    HorizontalDivider()
    Text("Content Below")
}
```

### Vertical Divider

Vertical divider is used to separate horizontally arranged content:

```kotlin
Row {
    Text("Left Content")
    VerticalDivider()
    Text("Right Content")
}
```

## Properties

### HorizontalDivider Properties

| Property Name | Type     | Description                     | Default Value                | Required |
| ------------- | -------- | ------------------------------- | ---------------------------- | -------- |
| modifier      | Modifier | Modifier applied to the divider | Modifier                     | No       |
| thickness     | Dp       | Thickness of the divider        | DividerDefaults.Thickness    | No       |
| color         | Color    | Color of the divider            | DividerDefaults.DividerColor | No       |

### VerticalDivider Properties

| Property Name | Type     | Description                     | Default Value                | Required |
| ------------- | -------- | ------------------------------- | ---------------------------- | -------- |
| modifier      | Modifier | Modifier applied to the divider | Modifier                     | No       |
| thickness     | Dp       | Thickness of the divider        | DividerDefaults.Thickness    | No       |
| color         | Color    | Color of the divider            | DividerDefaults.DividerColor | No       |

### DividerDefaults Object

The DividerDefaults object provides default values for the divider components.

#### Constants

| Constant Name | Type  | Description                                            | Default Value                      |
| ------------- | ----- | ------------------------------------------------------ | ---------------------------------- |
| Thickness     | Dp    | Default thickness of divider                           | 0.33.dp                            |
| DividerColor  | Color | Default color of divider                               | COUITheme.colorScheme.dividerLine |
| CardInset     | Dp    | Recommended horizontal inset inside a Card (COUI rule) | 16.dp                              |

## Advanced Usage

### Custom Thickness Divider

```kotlin
Column {
    Text("Content Above")
    HorizontalDivider(
        thickness = 2.dp
    )
    Text("Content Below")
}
```

### Custom Color Divider

```kotlin
Column {
    Text("Content Above")
    HorizontalDivider(
        color = Color.Red
    )
    Text("Content Below")
}
```

### Divider Between Card Rows

Following the COUI card list rule, place a divider only between adjacent rows inside a card
(never after the last row) and inset it by `DividerDefaults.CardInset` on both sides. For rows
with a leading icon, increase the start inset so the divider aligns with the title text start:

```kotlin
Card {
    BasicComponent(title = "First Row")
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = DividerDefaults.CardInset)
    )
    BasicComponent(title = "Second Row")
}
```

### Using Vertical Divider Between Buttons

```kotlin
Row(
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
) {
    Button(onClick = { /* Handle click event */ }) {
        Text("Cancel")
    }
    VerticalDivider(
        modifier = Modifier.height(24.dp)
    )
    Button(onClick = { /* Handle click event */ }) {
        Text("Confirm")
    }
}
```
