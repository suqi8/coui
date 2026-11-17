# TopAppBar

`TopAppBar` is a top application bar component in COUI, used to provide navigation, title, and action buttons at the top of the interface. It supports both large title and regular modes, as well as dynamic effects during scrolling.

This component is typically used in conjunction with the `Scaffold` component to maintain consistent layout and behavior across different pages in the application.

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=topAppBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.TopAppBar
import io.github.suqi8.coui.kmp.basic.SmallTopAppBar
import io.github.suqi8.coui.kmp.basic.COUIScrollBehavior
import io.github.suqi8.coui.kmp.basic.rememberTopAppBarState
```

## Basic Usage

### Small TopAppBar

```kotlin
Scaffold(
    topBar = {
        SmallTopAppBar(
            title = "Title",
            navigationIcon = {
                IconButton(onClick = { /* Handle click event */ }) {
                    Icon(COUIIcons.Back, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { /* Handle click event */ }) {
                    Icon(COUIIcons.More, contentDescription = "More")
                }
            }
        )
    }
)
```

### Large TopAppBar

```kotlin
Scaffold(
    topBar = {
        TopAppBar(
            title = "Title",
            largeTitle = "Large Title", // If not specified, title value will be used
            navigationIcon = {
                IconButton(onClick = { /* Handle click event */ }) {
                    Icon(COUIIcons.Back, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { /* Handle click event */ }) {
                    Icon(COUIIcons.More, contentDescription = "More")
                }
            }
        )
    }
)
```

`SmallTopAppBar` is the standalone COUI toolbar: its title uses `TextAppearance.COUI.Toolbar.SecondTitle` (18dp, medium weight). The collapsible `TopAppBar` collapses its large title to a 24dp title.

### Centered Title

`centerTitle = true` centers the title and subtitle of a `SmallTopAppBar` (COUI `setIsTitleCenterStyle`). They are centered on the whole bar when they fit between the navigation icon and the actions symmetrically, otherwise centered in the space left between them:

```kotlin
SmallTopAppBar(
    title = "Title",
    centerTitle = true,
    navigationIcon = { /* ... */ },
    actions = {
        ToolbarTextButton(text = "Done", onClick = { /* Handle click event */ })
    }
)
```

### Text Actions and Red Dots

Put a `ToolbarTextButton` in `actions` for a text menu item (60–104dp wide, 14dp label in the theme color). An `IconButton` takes a `redDot` (`0` for a plain dot, a positive number for a count); see [IconButton](./iconbutton).

### With Subtitle

Both bars can show a subtitle below the title. On the collapsible `TopAppBar` the subtitle
fades out while the bar collapses by default; set `hideSubtitleOnCollapse = false` to keep
it visible and slide it into the collapsed bar below the title, where the collapsed title shrinks
from 24dp to 18dp:

```kotlin
TopAppBar(
    title = "Title",
    largeTitle = "Large Title",
    subtitle = "Subtitle",
    hideSubtitleOnCollapse = false
)
```

## Large TopAppBar Scroll Behavior (Using Scaffold)

TopAppBar supports changing its display state when content scrolls:

```kotlin
val scrollBehavior = COUIScrollBehavior()

Scaffold(
    topBar = {
        TopAppBar(
            title = "Title",
            largeTitle = "Large Title", // If not specified, title value will be used
            scrollBehavior = scrollBehavior
        )
    }
) { paddingValues ->
    // Content area needs to consider padding
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            // If you want to add the overscroll effect, please add it before the scroll behavior
            .overScrollVertical()
            // Bind TopAppBar scroll behavior
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        contentPadding = PaddingValues(top = paddingValues.calculateTopPadding())
    ) {
        // List content
    }
}
```

## Custom Styles

### Custom Colors

```kotlin
TopAppBar(
    title = "Title",
    color = COUITheme.colorScheme.primary,
    titleColor = COUITheme.colorScheme.onPrimary,
    largeTitleColor = COUITheme.colorScheme.onPrimary
)
```

### Custom Content Padding

By default the title, navigation icon and action paddings follow the bar width like COUI: 16dp on a compact window, 24dp on a medium one and 40dp on an expanded one (`TopAppBarDefaults.horizontalPadding`). Pass a value to fix it:

```kotlin
TopAppBar(
    title = "Title",
    titlePadding = 32.dp
)
```

### Custom Icon Padding

```kotlin
TopAppBar(
    title = "Title",
    navigationIconPadding = 12.dp,
    actionIconPadding = 12.dp
)
```

## Properties

### TopAppBar Properties

| Property Name              | Type                            | Description                                    | Default Value                   | Required |
| -------------------------- | ------------------------------- | ---------------------------------------------- | ------------------------------- | -------- |
| title                      | String                          | Top bar title                                  | -                               | Yes      |
| modifier                   | Modifier                        | Modifier applied to the top bar                | Modifier                        | No       |
| color                      | Color                           | Top bar background color                       | COUITheme.colorScheme.backgroundWithCard  | No       |
| titleColor                 | Color                           | Color of the collapsed small title text        | COUITheme.colorScheme.labelPrimary | No       |
| largeTitle                 | String                          | Large title text                               | title                           | No       |
| largeTitleColor            | Color                           | Color of the expanded large title text         | COUITheme.colorScheme.labelPrimary | No       |
| subtitle                   | String                          | Subtitle text displayed below the title bar    | ""                              | No       |
| subtitleColor              | Color                           | Color of the subtitle text                     | COUITheme.colorScheme.labelSecondaryVariant | No       |
| dividerColor               | Color                           | Color of the bottom hairline divider revealed while collapsing | COUITheme.colorScheme.divider | No       |
| navigationIcon             | @Composable () -> Unit          | Composable function for navigation icon area   | {}                              | No       |
| actions                    | @Composable RowScope.() -> Unit | Composable function for action buttons area (24dp icons recommended) | {}                              | No       |
| scrollBehavior             | ScrollBehavior?                 | Controls top bar scroll behavior               | null                            | No       |
| defaultWindowInsetsPadding | Boolean                         | Whether to apply default window insets padding | true                            | No       |
| showDivider                | Boolean                         | Whether to draw the bottom hairline divider that fades in as the bar collapses; always suppressed while `glow` is set | true | No       |
| hideSubtitleOnCollapse     | Boolean                         | Whether the subtitle fades out while the bar collapses; when false it stays opaque and slides into the collapsed bar below the title, and the collapsed title is 18dp | true | No       |
| titlePadding          | Dp                              | Horizontal content padding; unspecified follows the bar width (16 / 24 / 40dp) | Dp.Unspecified | No       |
| navigationIconPadding      | Dp                              | Start padding of the navigation icon; unspecified follows the bar width | Dp.Unspecified | No       |
| actionIconPadding          | Dp                              | End padding of the action icons; unspecified follows the bar width | Dp.Unspecified | No       |
| expandedHeight             | Dp                              | Expanded bar height without a subtitle         | TopAppBarDefaults.ExpandedHeight | No       |
| largeTitleFontSize         | TextUnit                        | Font size of the expanded large title          | TopAppBarDefaults.largeTitleFontSize() (32dp) | No       |
| largeTitleBottomMargin     | Dp                              | Anchor the large title to the bar bottom by this margin instead of the top padding | Dp.Unspecified | No       |
| fadeLargeTitleOnCollapse   | Boolean                         | Fade and shrink the large title away instead of morphing it into the collapsed title (COUI 17 settings home) | false | No       |
| glow                       | (@Composable BoxScope.() -> Unit)? | Backing layer drawn behind the bar, e.g. `TopAppBarGlow` with a transparent `color`; `null` for a solid bar | null | No       |
| bottomContent              | @Composable () -> Unit          | Composable content displayed below the title bar area | {} | No |

### SmallTopAppBar Properties

| Property Name              | Type                            | Description                                    | Default Value                   | Required |
| -------------------------- | ------------------------------- | ---------------------------------------------- | ------------------------------- | -------- |
| title                      | String                          | Top bar title                                  | -                               | Yes      |
| modifier                   | Modifier                        | Modifier applied to the top bar                | Modifier                        | No       |
| color                      | Color                           | Top bar background color                       | COUITheme.colorScheme.backgroundWithCard  | No       |
| titleColor                 | Color                           | Color of the title text                        | COUITheme.colorScheme.labelPrimary | No       |
| subtitle                   | String                          | Subtitle text displayed below the title bar    | ""                              | No       |
| subtitleColor              | Color                           | Color of the subtitle text                     | COUITheme.colorScheme.labelSecondaryVariant | No       |
| dividerColor               | Color                           | Color of the bottom hairline divider revealed on scroll | COUITheme.colorScheme.divider | No       |
| navigationIcon             | @Composable () -> Unit          | Composable function for navigation icon area   | {}                              | No       |
| actions                    | @Composable RowScope.() -> Unit | Composable function for action buttons area (24dp icons recommended) | {}                              | No       |
| scrollBehavior             | ScrollBehavior?                 | Controls top bar scroll behavior               | null                            | No       |
| defaultWindowInsetsPadding | Boolean                         | Whether to apply default window insets padding | true                            | No       |
| showDivider                | Boolean                         | Whether to draw the bottom hairline divider as content scrolls beneath the bar (requires scrollBehavior); always suppressed while `glow` is set | true | No       |
| centerTitle                | Boolean                         | Center the title and subtitle (COUI title center style) | false | No       |
| titlePadding          | Dp                              | Horizontal content padding; unspecified follows the bar width (16 / 24 / 40dp) | Dp.Unspecified | No       |
| navigationIconPadding      | Dp                              | Start padding of the navigation icon; unspecified follows the bar width | Dp.Unspecified | No       |
| actionIconPadding          | Dp                              | End padding of the action icons; unspecified follows the bar width | Dp.Unspecified | No       |
| glow                       | (@Composable BoxScope.() -> Unit)? | Backing layer drawn behind the bar, e.g. `TopAppBarGlow` with a transparent `color`; `null` for a solid bar | null | No       |
| bottomContent              | @Composable () -> Unit          | Composable content displayed below the title bar area | {} | No |

### TopAppBarDefaults Object

The TopAppBarDefaults object provides default values for TopAppBar and SmallTopAppBar components.

#### Constants

| Constant Name             | Type | Description                                        | Default Value |
| ------------------------- | ---- | -------------------------------------------------- | ------------- |
| HorizontalPaddingCompact   | Dp   | Title / icon padding on a compact window           | 16.dp         |
| HorizontalPaddingMedium    | Dp   | Title / icon padding on a medium window            | 24.dp         |
| HorizontalPaddingExpanded  | Dp   | Title / icon padding on an expanded window         | 40.dp         |
| SmallTitleTextSize         | Dp   | Title size of SmallTopAppBar (SecondTitle, medium weight) | 18.dp  |
| LargeTitleTopPadding | Dp | Top padding of the expanded large title           | 54.dp         |
| CollapsedHeight            | Dp   | Collapsed height of the TopAppBar                 | 52.dp         |
| ExpandedHeight             | Dp   | Minimum expanded height of the TopAppBar without a subtitle | 110.dp |
| SmallTopAppBarCenterHeight | Dp   | Vertical center height for SmallTopAppBar layout  | 52.dp         |
| LargeTitleBottomPadding    | Dp   | Bottom padding below the large title block when expanded (interpolates to 0.dp as the bar collapses) | 12.dp |
| SubtitleBottomPadding      | Dp   | Bottom padding below the subtitle when it overflows the collapsed bar | 8.dp  |
| SubtitleMarginTop          | Dp   | Vertical gap between the title and the subtitle   | 3.5.dp        |
| NavigationIconGap          | Dp   | Horizontal gap between the navigation icon and the collapsed title | 10.dp|
| ActionIconGap              | Dp   | Horizontal gap between the title and the action icons | 4.dp      |
| ActionItemSpacing          | Dp   | Horizontal spacing between adjacent action icons | 10.dp     |

#### Functions

| Function Name     | Description |
| ----------------- | ----------- |
| horizontalPadding(barWidth, windowHeight) | The padding used when `titlePadding` / `navigationIconPadding` / `actionIconPadding` are unspecified: 16dp below a 600dp bar width, otherwise 24dp or 40dp by the COUI screen tier of the bar width and window height |

### ScrollBehavior

COUIScrollBehavior is a configuration object used to control the scroll behavior of the top bar.

#### rememberTopAppBarState

Used to create and remember TopAppBarState:

```kotlin
val scrollBehavior = COUIScrollBehavior(
    state = rememberTopAppBarState(),
    canScroll = { true }
)
```

| Parameter Name     | Type                        | Default Value              | Description                                      |
| ------------------ | --------------------------- | -------------------------- | ------------------------------------------------ |
| state              | TopAppBarState              | rememberTopAppBarState()   | State object controlling scroll state            |
| canScroll          | () -> Boolean               | { true }                   | Callback to control whether scrolling is allowed |
| snapAnimationSpec  | AnimationSpec\<Float>?      | 180ms decelerate tween     | Defines the snap to fully expanded/collapsed when scrolling stops midway |
| flingAnimationSpec | DecayAnimationSpec\<Float>? | rememberSplineBasedDecay() | Defines decay animation for fling                |

## Advanced Usage

### Handling Window Insets

```kotlin
TopAppBar(
    title = "Title",
    largeTitle = "Large Title",
    defaultWindowInsetsPadding = false // Handle window insets manually
)
```

### Custom Scroll Behavior Animation

```kotlin
var isScrollingEnabled by remember { mutableStateOf(true) }
val scrollBehavior = COUIScrollBehavior(
    snapAnimationSpec = tween(durationMillis = 100),
    flingAnimationSpec = rememberSplineBasedDecay(),
    canScroll = { isScrollingEnabled } // Can dynamically control whether scrolling is allowed
)

TopAppBar(
    title = "Title",
    largeTitle = "Large Title",
    scrollBehavior = scrollBehavior
)
```

### Combining Large and Small Titles

```kotlin
var useSmallTopBar by remember { mutableStateOf(false) }

Box(modifier = Modifier.fillMaxSize()) {
    if (useSmallTopBar) {
        SmallTopAppBar(
            title = "Compact Mode",
            navigationIcon = {
                IconButton(onClick = { useSmallTopBar = false }) {
                    Icon(
                        imageVector = COUIIcons.Back,
                        contentDescription = "Switch to Large Title",
                        tint = COUITheme.colorScheme.labelPrimary
                    )
                }
            }
        )
    } else {
        TopAppBar(
            title = "Title",
            largeTitle = "Expanded Mode",
            navigationIcon = {
                IconButton(onClick = { useSmallTopBar = true }) {
                    Icon(
                        imageVector = COUIIcons.Back,
                        contentDescription = "Switch to Small Title",
                        tint = COUITheme.colorScheme.labelPrimary
                    )
                }
            }
        )
    }
}
```
