# IconButton

`IconButton` is a button component in COUI used for providing auxiliary interaction points. They are typically used in scenarios that require compact buttons, such as toolbars or image lists.

<div style="position: relative; height: 260px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=iconButton" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.IconButton
import io.github.suqi8.coui.kmp.basic.ToolbarTextButton
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

### Red Dot

`redDot` adds the COUI toolbar menu red dot (`COUIToolbar.setRedDot`): `0` shows a plain dot, a positive number a count badge. It hangs off the top end corner of the centered 24dp icon area and does not change the button size:

```kotlin
IconButton(
    onClick = { /* Handle click event */ },
    redDot = 3
) {
    Icon(
        imageVector = COUIIcons.More,
        contentDescription = "More"
    )
}
```

## ToolbarTextButton

`ToolbarTextButton` is the COUI 17 text menu item for a top app bar's `actions`: a capsule on the same top-bar material, 60–104dp wide and 40dp tall, with a single 14dp medium label in the theme color that ellipsizes past the maximum width.

```kotlin
SmallTopAppBar(
    title = "Title",
    actions = {
        ToolbarTextButton(
            text = "Done",
            onClick = { /* Handle click event */ }
        )
    }
)
```

| Property Name   | Type                    | Description                          | Default Value                                  | Required |
| --------------- | ----------------------- | ------------------------------------ | ---------------------------------------------- | -------- |
| text            | String                  | Label of the button                  | -                                              | Yes      |
| onClick         | () -> Unit              | Callback triggered on button click   | -                                              | Yes      |
| modifier        | Modifier                | Modifier applied to the button       | Modifier                                       | No       |
| enabled         | Boolean                 | Whether button is clickable          | true                                           | No       |
| holdDownState   | Boolean                 | Whether button is held down          | false                                          | No       |
| material        | Boolean                 | Draw the COUI 17 top-bar button material | `LocalCOUIMaterialEffectEnabled.current` | No       |
| backgroundColor | Color                   | Button background color              | Color.Unspecified                              | No       |
| minWidth        | Dp                      | Minimum width                        | IconButtonDefaults.TextButtonMinWidth          | No       |
| maxWidth        | Dp                      | Maximum width                        | IconButtonDefaults.TextButtonMaxWidth          | No       |
| minHeight       | Dp                      | Minimum height                       | IconButtonDefaults.MinHeight                   | No       |
| insideMargin    | PaddingValues           | Padding around the label             | IconButtonDefaults.TextButtonInsideMargin      | No       |
| colors          | ToolbarTextButtonColors | Label colors                         | IconButtonDefaults.toolbarTextButtonColors()   | No       |

## Touch Motion

With `material = true` the button runs the `COUIViewTouchAnimator` that `ToolbarMaterialEffectDelegate` attaches (deformation, rubber band and press scale all on), the same motion as [Button](./button#press-feedback):

- **Press scale**: the 40dp circle springs to ~0.889 (jelly springs: press k 631.65 / ζ 1, release k 157.91 / ζ 0.4) and stays down while the finger is held, even outside the button.
- **Drag follow**: past a 5px dead zone the button follows the finger on the rubber band `(1 - 1 / (0.05·|d| / 28dp + 1)) · 28dp`, stretching up to 1.2x along the drag and squeezing across it (area-conserving), on a response 0.15 / bounce 0.15 spring.
- **Release**: everything springs home on response 0.42 / bounce 0.65, seeded with the finger velocity.
- **Masks**: there is no press mask, as on COUI 17 top-bar buttons (`ToolbarMaterialEffectDelegate.handleTouch` never touches the mask drawable): a press shows the touch motion and the spotlight. The hover mask (`couiColorHover`) follows the pointer on desktop and web.
- A disabled button ignores touches.

## Properties

### IconButton Properties

| Property Name   | Type                   | Description                        | Default Value                   | Required |
| --------------- | ---------------------- | ---------------------------------- | ------------------------------- | -------- |
| onClick         | () -> Unit             | Callback triggered on button click | -                               | Yes      |
| modifier        | Modifier               | Modifier applied to the button     | Modifier                        | No       |
| enabled         | Boolean                | Whether button is clickable        | true                            | No       |
| holdDownState   | Boolean                | Whether button is held down        | false                           | No       |
| material        | Boolean                | Draw the COUI 17 top-bar button material (filtered backdrop, shadow, edge lighting, touch spotlight, press and hover masks) and its touch motion (press scale, drag-follow rubber band, drag deformation) | `LocalCOUIMaterialEffectEnabled.current` | No       |
| backgroundColor | Color                  | Button background color            | Color.Unspecified               | No       |
| cornerRadius    | Dp                     | Button corner radius               | IconButtonDefaults.CornerRadius | No       |
| minHeight       | Dp                     | Button minimum height              | IconButtonDefaults.MinHeight    | No       |
| minWidth        | Dp                     | Button minimum width               | IconButtonDefaults.MinWidth     | No       |
| redDot          | Int?                   | Toolbar red dot: null none, 0 plain dot, positive count | null       | No       |
| content         | @Composable () -> Unit | Button content, typically an Icon  | -                               | Yes      |

### IconButtonDefaults Object

The IconButtonDefaults object provides default values for the icon button component.

#### Constants

| Constant Name | Type | Description           | Default Value |
| ------------- | ---- | --------------------- | ------------- |
| MinWidth      | Dp   | Minimum button width  | 40.dp         |
| MinHeight     | Dp   | Minimum button height | 40.dp         |
| CornerRadius  | Dp   | Button corner radius  | 20.dp         |
| TextButtonMinWidth | Dp | Minimum width of ToolbarTextButton | 60.dp |
| TextButtonMaxWidth | Dp | Maximum width of ToolbarTextButton | 104.dp |
| TextButtonInsideMargin | PaddingValues | Padding around the ToolbarTextButton label | PaddingValues(horizontal = 12.dp) |
| TextButtonTextSize | Dp | Label size of ToolbarTextButton (ignores font scale) | 14.dp |
| RedDotAnchorSize | Dp | Icon area the red dot hangs off | 24.dp |
| RedDotHorizontalOffset / RedDotVerticalOffset | Dp | Plain dot overhang past the icon area's end / top | 1.dp / 1.dp |
| RedDotWithSmallNumberHorizontalOffset | Dp | Count badge end overhang, counts under 10 | 6.dp |
| RedDotWithNumberHorizontalOffset | Dp | Count badge end overhang, counts under 100 | 8.dp |
| RedDotWithBigNumberHorizontalOffset | Dp | Count badge end overhang, counts of 100 and more | 9.dp |
| RedDotWithNumberVerticalOffset | Dp | Count badge top overhang | 5.dp |

#### Functions

| Function Name | Description |
| ------------- | ----------- |
| toolbarTextButtonColors(contentColor, disabledContentColor) | ToolbarTextButton label colors; defaults `colorScheme.primary` / `colorScheme.disabledNeutral` |

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
                tint = COUITheme.colorScheme.labelPrimary,
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
        tint = if (isLiked) Color.Red else COUITheme.colorScheme.labelPrimary
    )
}
```
