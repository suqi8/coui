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
import io.github.suqi8.coui.kmp.basic.TopAppBarSearchSlot
import io.github.suqi8.coui.kmp.basic.TopAppBarSearchBar
import io.github.suqi8.coui.kmp.basic.TopAppBarSearchState
import io.github.suqi8.coui.kmp.basic.topAppBarSearchContent
import io.github.suqi8.coui.kmp.basic.topAppBarSearchResults
import io.github.suqi8.coui.kmp.basic.rememberTopAppBarSearchState
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

### Search Capsule Below the Title

`TopAppBarSearchSlot` is a static search capsule row for a capsule that only opens something else, such as a separate search page; a search the user types into belongs in a `TopAppBarSearchBar` (see Searching In Place). Put it in `bottomContent`: it lays out a 52dp row (`coui_search_view_height`) with the 16dp page-side margin, and folds away together with the expanded title area. Place an `InputField` directly in the slot through the named `inputField` argument; do not wrap a whole `SearchBar` in it, since the slot already pads the capsule by 16dp and the search bar would add its own margin on top:

```kotlin
var query by remember { mutableStateOf("") }

TopAppBar(
    title = "Settings",
    bottomContent = {
        TopAppBarSearchSlot(
            inputField = {
                InputField(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = { /* Handle search */ },
                    expanded = false,
                    onExpandedChange = { /* Open the search page */ },
                    label = "Search"
                )
            }
        )
    }
)
```

### Searching In Place

A search the user types into under the title searches in place, linked with the bar like the COUI 17 settings home: tapping the capsule turns it into the edit state with a cancel button, the large title shrinks to 80%, fades and moves up, the toolbar scrolls away and the search bar rises to just below the status bar (`HomepageImpl.animToEdit`, a response 0.35 spring without bounce). Put a `TopAppBarSearchBar` in `bottomContent` (it brings its own 52dp row and page padding, so leave `TopAppBarSearchSlot` out) and pass the same `TopAppBarSearchState` to the bar. Fade the page out with `Modifier.topAppBarSearchContent` and bring the results in with `Modifier.topAppBarSearchResults`, in a list of their own: they fade in while moving up 108dp. Cancel and system back leave the search and clear the query. Keep `TopAppBarSearchSlot` for a capsule that only opens something else.

```kotlin
var searching by rememberSaveable { mutableStateOf(false) }
var query by rememberSaveable { mutableStateOf("") }
val searchState = rememberTopAppBarSearchState(active = searching)
val pageListState = rememberLazyListState()

Scaffold(
    topBar = {
        TopAppBar(
            title = "Settings",
            scrollBehavior = scrollBehavior,
            searchState = searchState,
            bottomContent = {
                TopAppBarSearchBar(
                    query = query,
                    onQueryChange = { query = it },
                    expanded = searching,
                    onExpandedChange = {
                        searching = it
                        if (!it) query = ""
                    },
                    cancelText = "Cancel",
                    label = "Search"
                )
            }
        )
    }
) { padding ->
    Box(Modifier.fillMaxSize()) {
        if (searchState.isContentVisible) {
            LazyColumn(
                modifier = Modifier.topAppBarSearchContent(searchState),
                state = pageListState,
                contentPadding = padding
            ) { /* The page */ }
        }
        if (searchState.isResultsVisible) {
            LazyColumn(
                modifier = Modifier.topAppBarSearchResults(searchState),
                contentPadding = padding
            ) { /* The results for query */ }
        }
    }
}
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
| searchState                | TopAppBarSearchState?           | Links the bar to a search bar in `bottomContent`: as the search enters, the title fades away and the bottom content rises to the top (COUI 17 settings home) | null | No |
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

### TopAppBarSearchSlot Properties

| Property Name | Type                   | Description                                             | Default Value                            | Required |
| ------------- | ---------------------- | ------------------------------------------------------- | ---------------------------------------- | -------- |
| inputField    | @Composable () -> Unit | The search input field, e.g. `InputField` from SearchBar | -                                       | Yes      |
| modifier      | Modifier               | Modifier applied to the slot row                        | Modifier                                 | No       |
| height        | Dp                     | Height of the slot row (`coui_search_view_height`)      | TopAppBarDefaults.SearchSlotHeight       | No       |
| insideMargin  | PaddingValues          | Horizontal page-side padding of the capsule             | TopAppBarDefaults.SearchSlotInsideMargin | No       |

### TopAppBarSearchBar Properties

| Property Name      | Type                          | Description                                                  | Default Value | Required |
| ------------------ | ----------------------------- | ------------------------------------------------------------ | ------------- | -------- |
| query              | String                        | The search text                                              | -             | Yes      |
| onQueryChange      | (String) -> Unit              | Called with the new search text                              | -             | Yes      |
| expanded           | Boolean                       | Whether the search is active                                 | -             | Yes      |
| onExpandedChange   | (Boolean) -> Unit             | Called to enter or leave the search                          | -             | Yes      |
| cancelText         | String                        | Localized text of the cancel button                          | -             | Yes      |
| modifier           | Modifier                      | Modifier applied to the search bar                           | Modifier      | No       |
| onSearch           | (String) -> Unit              | Called when the keyboard's search action is pressed          | {}            | No       |
| label              | String                        | Hint shown while the query is empty                          | ""            | No       |
| hints              | List<String>                  | Rotating hints shown in place of the label                   | emptyList()   | No       |
| innerPrimaryButton | (@Composable () -> Unit)?     | Button inside the capsule while the query is empty, e.g. voice input | null  | No       |

### TopAppBarSearchState

Created with `rememberTopAppBarSearchState(active)`, which runs the transition each time `active` changes.

| Property Name   | Type    | Description                                                                 |
| --------------- | ------- | --------------------------------------------------------------------------- |
| isActive        | Boolean | Whether the search is active, or entering                                   |
| progress        | Float   | How far the bar has moved into the search: 0 title area, 1 search bar at the top |
| resultsProgress | Float   | How far the search results have come in (response 0.35 entering, 0.1 leaving) |
| contentAlpha    | Float   | Alpha of the title and the page behind the search: `1 - progress`, cubed while entering |
| isContentVisible | Boolean | Whether the page behind the search still shows |
| isResultsVisible | Boolean | Whether the results show: while active and until they have left |

`Modifier.topAppBarSearchContent(state)` fades the page behind the search; `Modifier.topAppBarSearchResults(state)` fades the results in while moving them up from `ResultsOffset`. `isContentVisible` and `isResultsVisible` tell when each list needs to be composed.

### TopAppBarSearchDefaults Object

| Property Name    | Type                 | Description                                        | Value  |
| ---------------- | -------------------- | -------------------------------------------------- | ------ |
| TransitionSpec   | AnimationSpec<Float> | Title, toolbar and search bar spring               | response 0.35, bounce 0 |
| ResultsEnterSpec | AnimationSpec<Float> | Results spring on entering                         | response 0.35, bounce 0 |
| ResultsExitSpec  | AnimationSpec<Float> | Results spring on leaving                          | response 0.1, bounce 0 |
| ResultsOffset    | Dp                   | How far below their place the results start (`homepage_init_searchbar_top`) | 108.dp |
| TitleEndScale    | Float                | Scale the title shrinks to                         | 0.8    |
| TitleTravel      | Dp                   | How far the title moves up (200px at 3.5x)         | 57.dp  |

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
| SearchSlotHeight           | Dp   | Height of the TopAppBarSearchSlot row             | 52.dp         |
| SearchSlotInsideMargin     | PaddingValues | Horizontal page-side padding of the search capsule | PaddingValues(horizontal = 16.dp) |

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
