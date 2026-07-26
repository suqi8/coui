# TopAppBar

`TopAppBar` is a top application bar component in Miuix, used to provide navigation, title, and action buttons at the top of the interface. It supports both large title and regular modes, as well as dynamic effects during scrolling.

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
| color                      | Color                           | Top bar background color                       | COUITheme.colorScheme.surface  | No       |
| titleColor                 | Color                           | Color of the collapsed small title text        | COUITheme.colorScheme.onSurface | No       |
| largeTitle                 | String                          | Large title text                               | title                           | No       |
| largeTitleColor            | Color                           | Color of the expanded large title text         | COUITheme.colorScheme.onSurface | No       |
| subtitle                   | String                          | Subtitle text displayed below the title bar    | ""                              | No       |
| subtitleColor              | Color                           | Color of the subtitle text                     | COUITheme.colorScheme.onSurfaceVariantSummary | No       |
| dividerColor               | Color                           | Color of the bottom hairline divider revealed while collapsing | COUITheme.colorScheme.dividerLine | No       |
| navigationIcon             | @Composable () -> Unit          | Composable function for navigation icon area   | {}                              | No       |
| actions                    | @Composable RowScope.() -> Unit | Composable function for action buttons area (24dp icons recommended) | {}                              | No       |
| scrollBehavior             | ScrollBehavior?                 | Controls top bar scroll behavior               | null                            | No       |
| defaultWindowInsetsPadding | Boolean                         | Whether to apply default window insets padding | true                            | No       |
| showDivider                | Boolean                         | Whether to draw the bottom hairline divider that fades in as the bar collapses | true | No       |
| hideSubtitleOnCollapse     | Boolean                         | Whether the subtitle fades out while the bar collapses; when false it stays opaque and slides into the collapsed bar below the title | true | No       |
| titlePadding          | Dp                              | Horizontal content padding                     | TopAppBarDefaults.TitlePadding | No       |
| navigationIconPadding      | Dp                              | Start padding of the navigation icon           | TopAppBarDefaults.NavigationIconPadding | No       |
| actionIconPadding          | Dp                              | End padding of the action icons                | TopAppBarDefaults.ActionIconPadding | No       |
| bottomContent              | @Composable () -> Unit          | Composable content displayed below the title bar area | {} | No |

### SmallTopAppBar Properties

| Property Name              | Type                            | Description                                    | Default Value                   | Required |
| -------------------------- | ------------------------------- | ---------------------------------------------- | ------------------------------- | -------- |
| title                      | String                          | Top bar title                                  | -                               | Yes      |
| modifier                   | Modifier                        | Modifier applied to the top bar                | Modifier                        | No       |
| color                      | Color                           | Top bar background color                       | COUITheme.colorScheme.surface  | No       |
| titleColor                 | Color                           | Color of the title text                        | COUITheme.colorScheme.onSurface | No       |
| subtitle                   | String                          | Subtitle text displayed below the title bar    | ""                              | No       |
| subtitleColor              | Color                           | Color of the subtitle text                     | COUITheme.colorScheme.onSurfaceVariantSummary | No       |
| dividerColor               | Color                           | Color of the bottom hairline divider revealed on scroll | COUITheme.colorScheme.dividerLine | No       |
| navigationIcon             | @Composable () -> Unit          | Composable function for navigation icon area   | {}                              | No       |
| actions                    | @Composable RowScope.() -> Unit | Composable function for action buttons area (24dp icons recommended) | {}                              | No       |
| scrollBehavior             | ScrollBehavior?                 | Controls top bar scroll behavior               | null                            | No       |
| defaultWindowInsetsPadding | Boolean                         | Whether to apply default window insets padding | true                            | No       |
| showDivider                | Boolean                         | Whether to draw the bottom hairline divider as content scrolls beneath the bar (requires scrollBehavior) | true | No       |
| titlePadding          | Dp                              | Horizontal content padding                     | TopAppBarDefaults.TitlePadding | No       |
| navigationIconPadding      | Dp                              | Start padding of the navigation icon           | TopAppBarDefaults.NavigationIconPadding | No       |
| actionIconPadding          | Dp                              | End padding of the action icons                | TopAppBarDefaults.ActionIconPadding | No       |
| bottomContent              | @Composable () -> Unit          | Composable content displayed below the title bar area | {} | No |

### TopAppBarDefaults Object

The TopAppBarDefaults object provides default values for TopAppBar and SmallTopAppBar components.

#### Constants

| Constant Name             | Type | Description                                        | Default Value |
| ------------------------- | ---- | -------------------------------------------------- | ------------- |
| TitlePadding    | Dp   | Horizontal padding of the title and large title    | 16.dp         |
| LargeTitleTopPadding | Dp | Top padding of the expanded large title           | 54.dp         |
| NavigationIconPadding      | Dp   | Start padding of the navigation icon              | 16.dp         |
| ActionIconPadding          | Dp   | End padding of the action icons                   | 16.dp         |
| CollapsedHeight            | Dp   | Collapsed height of the TopAppBar                 | 52.dp         |
| ExpandedHeight             | Dp   | Minimum expanded height of the TopAppBar without a subtitle | 107.dp |
| SmallTopAppBarCenterHeight | Dp   | Vertical center height for SmallTopAppBar layout  | 52.dp         |
| LargeTitleBottomPadding    | Dp   | Bottom padding below the large title block when expanded (interpolates to 0.dp as the bar collapses) | 12.dp |
| SubtitleBottomPadding      | Dp   | Bottom padding below the subtitle when it overflows the collapsed bar | 8.dp  |
| SubtitleMarginTop          | Dp   | Vertical gap between the title and the subtitle   | 3.5.dp        |
| NavigationIconGap          | Dp   | Horizontal gap between the navigation icon and the collapsed title | 4.dp |
| ActionIconGap              | Dp   | Horizontal gap between the title and the action icons | 8.dp      |

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
                        tint = COUITheme.colorScheme.onBackground
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
                        tint = COUITheme.colorScheme.onBackground
                    )
                }
            }
        )
    }
}
```
