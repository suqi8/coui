# TagView

`TagView` is a small text tag such as "NEW" or "BETA", after ColorOS's `COUITagView` on its `COUITagBackgroundView`: a single-line label on a filled, clipped background with optional leading and trailing icons.

<div style="position: relative; height: 180px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=tagView" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.TagView
import io.github.suqi8.coui.kmp.basic.TagViewDefaults
```

## Basic Usage

```kotlin
Row(verticalAlignment = Alignment.CenterVertically) {
    Text(text = "Dark mode")
    Spacer(Modifier.width(8.dp))
    TagView(text = "NEW")
}
```

## Properties

### TagView Properties

| Property Name | Type                      | Description                                                                  | Default Value                   | Required |
| ------------- | ------------------------- | ---------------------------------------------------------------------------- | ------------------------------- | -------- |
| text          | String                    | Text of the tag, single line with an end ellipsis                            | -                               | Yes      |
| modifier      | Modifier                  | Modifier applied to the tag                                                  | Modifier                        | No       |
| shape         | Shape                     | Background and clip shape; per-corner radii via `RoundedCornerShape`         | TagViewDefaults.Shape           | No       |
| colors        | TagViewColors             | Colors of the tag                                                            | TagViewDefaults.tagViewColors() | No       |
| strokeWidth   | Dp                        | Width of the outline                                                         | TagViewDefaults.StrokeWidth     | No       |
| textSize      | Dp                        | Size of the label, in dp so it ignores the font scale                        | TagViewDefaults.TextSize        | No       |
| insideMargin  | PaddingValues             | Margin inside the tag                                                        | TagViewDefaults.InsideMargin    | No       |
| leadingIcon   | (@Composable () -> Unit)? | Icon before the label, in a 5dp x 10dp box (COUI `couiTagViewLeftDrawable`)  | null                            | No       |
| trailingIcon  | (@Composable () -> Unit)? | Icon after the label at its own size (COUI `couiDrawableTagViewImage`)       | null                            | No       |

### TagViewDefaults Object

| Constant Name     | Type          | Description                                                      | Default Value                    |
| ----------------- | ------------- | ---------------------------------------------------------------- | -------------------------------- |
| CornerRadius      | Dp            | Default corner radius (COUI leaves `couiTagCornerRadius` at 0)   | 4.dp                             |
| Shape             | Shape         | Default shape                                                    | RoundedCornerShape(CornerRadius) |
| StrokeWidth       | Dp            | Default outline width (`couiTagStrokeWidth`)                     | 0.dp                             |
| TextSize          | Dp            | Default label size (`coui_default_tag_textsize`)                 | 10.dp                            |
| InsideMargin      | PaddingValues | Default inside margin (`coui_tag_view_layout`)                   | PaddingValues(horizontal = 3.dp) |
| LeadingIconWidth  | Dp            | Width of the leading icon box                                    | 5.dp                             |
| LeadingIconHeight | Dp            | Height of the leading icon box                                   | 10.dp                            |

| Function Name   | Description                                                                         |
| --------------- | ----------------------------------------------------------------------------------- |
| tagViewColors() | Creates `TagViewColors`: theme primary fill, white label, transparent outline       |

### TagViewColors Class

| Property Name  | Type  | Description                                       |
| -------------- | ----- | ------------------------------------------------- |
| containerColor | Color | Background fill (`couiTagBackgroundColor`)        |
| contentColor   | Color | Label color (`couiTagViewTextColor`)              |
| strokeColor    | Color | Outline color, drawn when the stroke width is > 0 |

## Behavior

- The label is sans-serif-medium (`couiTextTag`) with the ColorOS 15+ line spacing multiplier of 1.2, so a plain tag is about 14dp high.
- The background clips its content to the same shape (`COUITagBackgroundView.dispatchDraw`).
- COUI reads the default fill from `couiColorBackgroundWithTag`, which the shipped theme leaves unset, so the library uses the theme primary color and a 4dp radius; pass your own `colors` and `shape` to match a host.

## Advanced Usage

### Outlined Tag

```kotlin
TagView(
    text = "Outlined",
    colors = TagViewDefaults.tagViewColors(
        containerColor = Color.Transparent,
        contentColor = COUITheme.colorScheme.primary,
        strokeColor = COUITheme.colorScheme.primary,
    ),
    strokeWidth = 1.dp,
)
```

### Per-corner Radii

```kotlin
TagView(
    text = "Corner",
    shape = RoundedCornerShape(topStart = 6.dp, bottomEnd = 6.dp),
)
```
