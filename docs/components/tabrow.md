# TabRow

`TabRow` is a navigation component in COUI that reproduces the ColorOS 16 segment button (`COUISegmentButtonLayout`). It provides two variants: the contour style (a capsule container with a sliding indicator) and the standard style (the same segment button without the container fill), suitable for content categorization and navigation scenarios.

<div style="position: relative; height: 180px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=tabRow" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.TabRow // Standard style (frameless)
import io.github.suqi8.coui.kmp.basic.TabRowWithContour // Contour style (segment button)
```

## Basic Usage

### Standard Style

The standard style draws only the sliding capsule indicator and its drop shadow, on a transparent background by default.

```kotlin
val tabs = listOf("Recommended", "Following", "Popular", "Featured")
var selectedTabIndex by remember { mutableStateOf(0) }

TabRow(
    tabs = tabs,
    selectedTabIndex = selectedTabIndex,
    onTabSelected = { selectedTabIndex = it }
)
```

### Contour Style

The contour style matches the ColorOS segment button: a 40dp capsule container with a 4dp inset around the sliding indicator. Segments are measured from their label width (at least 52dp) and distributed to fill the row.

```kotlin
val tabs = listOf("All", "Photos", "Videos", "Documents")
var selectedTabIndex by remember { mutableStateOf(0) }

TabRowWithContour(
    tabs = tabs,
    selectedTabIndex = selectedTabIndex,
    onTabSelected = { selectedTabIndex = it }
)
```

### Preserve Scroll Position

When the tabs cannot fit at their minimum width, the row becomes scrollable.

```kotlin
val tabs = listOf("Tab 1", "Tab 2", "Tab 3", "Tab 4", "Tab 5")
var selectedTabIndex by remember { mutableStateOf(3) }
val tabListState = rememberLazyListState()

TabRowWithContour(
    tabs = tabs,
    selectedTabIndex = selectedTabIndex,
    onTabSelected = { selectedTabIndex = it },
    listState = tabListState,
)
```

## Properties

### TabRow Properties

| Property Name     | Type                      | Description                      | Default Value                                       | Required |
| ----------------- | ------------------------- | -------------------------------- | --------------------------------------------------- | -------- |
| tabs              | List\<String>             | List of tab texts                | -                                                   | Yes      |
| selectedTabIndex  | Int                       | Current selected tab index       | -                                                   | Yes      |
| onTabSelected     | (Int) -> Unit             | Callback when tab is selected    | -                                                   | Yes      |
| modifier          | Modifier                  | Modifier for the tab row         | Modifier                                            | No       |
| colors            | TabRowColors              | Color configuration              | TabRowDefaults.tabRowColors(backgroundColor = Color.Transparent) | No       |
| minWidth          | Dp                        | Minimum width of each tab        | TabRowDefaults.TabRowMinWidth                       | No       |
| maxWidth          | Dp                        | Maximum width of each tab        | TabRowDefaults.TabRowMaxWidth                       | No       |
| height            | Dp                        | Height of the tab row            | TabRowDefaults.TabRowHeight                         | No       |
| cornerRadius      | Dp                        | Corner radius of the indicator   | TabRowDefaults.TabRowCornerRadius                   | No       |
| itemSpacing       | Dp                        | Spacing between tabs             | 0.dp                                                | No       |
| contentAlignment  | Alignment                 | Alignment of tab content         | Alignment.Center                                    | No       |
| listState         | LazyListState?            | External scroll state for tabs   | null                                                | No       |
| interactionSource | MutableInteractionSource? | Interaction source for tab items | null                                                | No       |
| indication        | Indication?               | Indication for tab items         | null                                                | No       |

### TabRowWithContour Properties

| Property Name     | Type                      | Description                              | Default Value                                | Required |
| ----------------- | ------------------------- | ---------------------------------------- | -------------------------------------------- | -------- |
| tabs              | List\<String>             | List of tab texts                        | -                                            | Yes      |
| selectedTabIndex  | Int                       | Current selected tab index               | -                                            | Yes      |
| onTabSelected     | (Int) -> Unit             | Callback when tab is selected            | -                                            | Yes      |
| modifier          | Modifier                  | Modifier for the tab row                 | Modifier                                     | No       |
| colors            | TabRowColors              | Color configuration                      | TabRowDefaults.tabRowColors()                | No       |
| minWidth          | Dp                        | Minimum width of each tab                | TabRowDefaults.TabRowWithContourMinWidth     | No       |
| maxWidth          | Dp                        | Maximum width of each tab                | TabRowDefaults.TabRowWithContourMaxWidth     | No       |
| height            | Dp                        | Height of the tab row                    | TabRowDefaults.TabRowWithContourHeight       | No       |
| cornerRadius      | Dp                        | Corner radius of the indicator           | TabRowDefaults.TabRowWithContourCornerRadius | No       |
| contourPadding    | Dp                        | Inset between container and indicator    | TabRowDefaults.TabRowWithContourPadding      | No       |
| itemSpacing       | Dp                        | Spacing between tabs                     | 0.dp                                         | No       |
| contentAlignment  | Alignment                 | Alignment of tab content                 | Alignment.Center                             | No       |
| listState         | LazyListState?            | External scroll state for tabs           | null                                         | No       |
| interactionSource | MutableInteractionSource? | Interaction source for tab items         | null                                         | No       |
| indication        | Indication?               | Indication for tab items                 | null                                         | No       |

### TabRowDefaults Object

The TabRowDefaults object provides default configurations for the TabRow component.

#### Constants

| Constant Name                     | Type | Value       | Description                                                       |
| --------------------------------- | ---- | ----------- | ----------------------------------------------------------------- |
| TabRowHeight                      | Dp   | 40.dp       | Default height of tab row for standard style                      |
| TabRowWithContourHeight           | Dp   | 40.dp       | Default height of tab row for contour style                       |
| TabRowWithContourTinyHeight       | Dp   | 32.dp       | Height of the COUI `SegmentButton.Tiny` style                     |
| TabRowCornerRadius                | Dp   | 20.dp       | Default indicator corner radius (capsule) for standard style      |
| TabRowWithContourCornerRadius     | Dp   | 16.dp       | Default indicator corner radius (capsule) for contour style       |
| TabRowWithContourTinyCornerRadius | Dp   | 14.dp       | Indicator corner radius of the `SegmentButton.Tiny` style         |
| TabRowWithContourPadding          | Dp   | 4.dp        | Default inset between container and indicator                     |
| TabRowWithContourTinyPadding      | Dp   | 2.dp        | Contour inset of the `SegmentButton.Tiny` style                   |
| TabRowMinWidth                    | Dp   | 52.dp       | Min width of tabs for standard style                              |
| TabRowWithContourMinWidth         | Dp   | 52.dp       | Min width of tabs for contour style                               |
| TabRowMaxWidth                    | Dp   | Dp.Infinity | Max width of tabs for standard style (unbounded, tabs fill row)   |
| TabRowWithContourMaxWidth         | Dp   | Dp.Infinity | Max width of tabs for contour style (unbounded, tabs fill row)    |

#### Methods

| Method Name    | Type         | Description                        |
| -------------- | ------------ | ---------------------------------- |
| tabRowColors() | TabRowColors | Create default color configuration |

### TabRowColors Class

| Property Name           | Type  | Description                        |
| ----------------------- | ----- | ---------------------------------- |
| backgroundColor         | Color | Background color of the container  |
| contentColor            | Color | Default content color of tabs      |
| selectedBackgroundColor | Color | Color of the sliding indicator     |
| selectedContentColor    | Color | Content color of the selected tab  |

## Advanced Usage

### Custom Colors

```kotlin
val tabs = listOf("Latest", "Popular", "Following")
var selectedTabIndex by remember { mutableStateOf(0) }

TabRow(
    tabs = tabs,
    selectedTabIndex = selectedTabIndex,
    onTabSelected = { selectedTabIndex = it },
    colors = TabRowDefaults.tabRowColors(
        backgroundColor = Color.LightGray.copy(alpha = 0.5f),
        contentColor = Color.Gray,
        selectedBackgroundColor = COUITheme.colorScheme.primary,
        selectedContentColor = Color.White
    )
)
```

### Tiny Variant

```kotlin
val tabs = listOf("Photo", "Video", "Portrait")
var selectedTabIndex by remember { mutableStateOf(0) }

TabRowWithContour(
    tabs = tabs,
    selectedTabIndex = selectedTabIndex,
    onTabSelected = { selectedTabIndex = it },
    height = TabRowDefaults.TabRowWithContourTinyHeight,
    cornerRadius = TabRowDefaults.TabRowWithContourTinyCornerRadius,
    contourPadding = TabRowDefaults.TabRowWithContourTinyPadding
)
```

### Using with Pager

```kotlin
val tabs = listOf("Page 1", "Page 2", "Page 3")
val pagerState = rememberPagerState { tabs.size }
var selectedTabIndex by remember { mutableStateOf(0) }

LaunchedEffect(pagerState.currentPage) {
    selectedTabIndex = pagerState.currentPage
}

LaunchedEffect(selectedTabIndex) {
    pagerState.animateScrollToPage(selectedTabIndex)
}

Surface {
    Column {
        TabRow(
            tabs = tabs,
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { selectedTabIndex = it }
        )
        HorizontalPager(
            pagerState = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Page Content ${page + 1}")
            }
        }
    }
}
```
