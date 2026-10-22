# IconButton

`IconButton` is a button component in COUI used for providing auxiliary interaction points. They are typically used in scenarios that require compact buttons, such as toolbars or image lists.

<div style="position: relative; height: 160px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=iconButton" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.IconButton
```

## Basic Usage

The IconButton component can be used to trigger actions or events:

```kotlin
IconButton(
    onClick = { /* Handle click event */ }
) {
    Icon(
        imageVector = COUIIcons.Favorites,
        contentDescription = "Favorites"
    )
}
```

## Component States

### Disabled State

```kotlin
IconButton(
    onClick = { /* Handle click event */ },
    enabled = false
) {
    Icon(
        imageVector = COUIIcons.Favorites,
        contentDescription = "Favorites"
    )
}
```

### Hold Down State

IconButton supports controlling the hold down state through the `holdDownState` parameter, typically used for visual feedback when displaying pop-up dialogs:

```kotlin
var showDialog by remember { mutableStateOf(false) }

Scaffold {
    IconButton(
        onClick = { showDialog = true },
        holdDownState = showDialog
    ) {
        Icon(
            imageVector = COUIIcons.Favorites,
            contentDescription = "Favorites"
        )
    }
    // Define dialog elsewhere
    OverlayDialog(
        title = "Dialog",
        show = showDialog,
        onDismissRequest = { showDialog = false }
    ) {
        // Dialog content
    }
}
```

## Touch Motion

With `material = true` the button runs the `COUIViewTouchAnimator` that `ToolbarMaterialEffectDelegate` attaches (deformation, rubber band and press scale all on), the same motion as [Button](./button#press-feedback):

- **Press scale**: the 40dp circle springs to ~0.889 (jelly springs: press k 631.65 / ζ 1, release k 157.91 / ζ 0.4) and stays down while the finger is held, even outside the button.
- **Drag follow**: past a 5px dead zone the button follows the finger on the rubber band `(1 - 1 / (0.05·|d| / 28dp + 1)) · 28dp`, stretching up to 1.2x along the drag and squeezing across it (area-conserving), on a response 0.15 / bounce 0.15 spring.
- **Release**: everything springs home on response 0.42 / bounce 0.65, seeded with the finger velocity.
- **Masks**: the press mask (`couiColorPress`) fades as soon as the finger leaves the button; the hover mask (`couiColorHover`) follows the pointer on desktop and web.
- A disabled button ignores touches.

## Properties

### IconButton Properties

| Property Name   | Type                   | Description                        | Default Value                   | Required |
| --------------- | ---------------------- | ---------------------------------- | ------------------------------- | -------- |
| onClick         | () -> Unit             | Callback triggered on button click | -                               | Yes      |
| modifier        | Modifier               | Modifier applied to the button     | Modifier                        | No       |
| enabled         | Boolean                | Whether button is clickable        | true                            | No       |
| holdDownState   | Boolean                | Whether button is held down        | false                           | No       |
| material        | Boolean                | Draw the COUI 17 top-bar button material (filtered backdrop, shadow, edge lighting, touch spotlight, press and hover masks) and its touch motion (press scale, drag-follow rubber band, drag deformation) | true | No       |
| backgroundColor | Color                  | Button background color            | Color.Unspecified               | No       |
| cornerRadius    | Dp                     | Button corner radius               | IconButtonDefaults.CornerRadius | No       |
| minHeight       | Dp                     | Button minimum height              | IconButtonDefaults.MinHeight    | No       |
| minWidth        | Dp                     | Button minimum width               | IconButtonDefaults.MinWidth     | No       |
| content         | @Composable () -> Unit | Button content, typically an Icon  | -                               | Yes      |

### IconButtonDefaults Object

The IconButtonDefaults object provides default values for the icon button component.

#### Constants

| Constant Name | Type | Description           | Default Value |
| ------------- | ---- | --------------------- | ------------- |
| MinWidth      | Dp   | Minimum button width  | 40.dp         |
| MinHeight     | Dp   | Minimum button height | 40.dp         |
| CornerRadius  | Dp   | Button corner radius  | 20.dp         |

## Advanced Usage

### Custom Background Color

```kotlin
IconButton(
    onClick = { /* Handle click event */ },
    backgroundColor = Color.LightGray.copy(alpha = 0.3f)
) {
    Icon(
        imageVector = COUIIcons.Favorites,
        contentDescription = "Favorites"
    )
}
```

### Custom Size and Corner Radius

```kotlin
IconButton(
    onClick = { /* Handle click event */ },
    minWidth = 48.dp,
    minHeight = 48.dp,
    cornerRadius = 12.dp
) {
    Icon(
        imageVector = COUIIcons.Favorites,
        contentDescription = "Favorites"
    )
}
```

### Using with Other Components

```kotlin
Surface {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { /* Handle click event */ }
        ) {
            Icon(
                imageVector = COUIIcons.Add,
                tint = COUITheme.colorScheme.onBackground,
                contentDescription = "Add"
            )
        }
        Text("Add New Item")
    }
}
```

### Dynamic Icon Button

```kotlin
var isLiked by remember { mutableStateOf(false) }

IconButton(
    onClick = { isLiked = !isLiked }
) {
    Icon(
        imageVector = if (isLiked) COUIIcons.FavoritesFill else COUIIcons.Favorites,
        contentDescription = if (isLiked) "Like" else "Unlike",
        tint = if (isLiked) Color.Red else COUITheme.colorScheme.onBackground
    )
}
```
