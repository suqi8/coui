# TabRow

`TabRow` is a navigation component in COUI for content categorization and navigation scenarios. It provides two variants: the standard style reproduces the ColorOS sliding tab layout (`COUITabLayout`: text tabs over a thin track with a sliding underline indicator), and the contour style reproduces the ColorOS segment button (`COUISegmentButtonLayout`: a capsule container with a sliding capsule indicator).

<div style="position: relative; height: 180px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=tabRow" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.suqi8.coui.kmp.basic.TabRow // Standard style (sliding tab layout)
import io.github.suqi8.coui.kmp.basic.TabRowWithContour // Contour style (segment button)
```

## Basic Usage

### Standard Style

The standard style matches `COUITabLayout`: a 48dp row of 14sp labels (medium weight when selected), a 2dp track inset 24dp from both edges, and a 2dp neutral indicator as wide as the selected label (at least 32dp). Changing the selection slides the indicator over `min(300, 50 × Δtabs + 150)` ms with `CubicBezierEasing(0.33f, 0f, 0.67f, 1f)` while the label colors crossfade. Each tab has an 8dp press mask, and touching an unselected tab plays a haptic tick.

Labels that fit into the 360dp content width are spread with equal gaps, labels that fit into the row are centered with `itemSpacing` between them, and otherwise the row scrolls with 58dp fading edges and keeps the selected tab centered.

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

When the tabs cannot fit, both styles become scrollable. Pass a `LazyListState` to keep the scroll position.

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
| colors            | TabRowColors              | Color configuration                                                                      | TabRowDefaults.underlineColors()  | No       |
| minWidth          | Dp                        | Minimum width of the label slot of each tab                                              | TabRowDefaults.TabRowMinWidth     | No       |
| maxWidth          | Dp                        | Maximum width of the label slot; longer labels are ellipsized                            | TabRowDefaults.TabRowMaxWidth     | No       |
| height            | Dp                        | Height of the tab row                                                                    | TabRowDefaults.TabRowHeight       | No       |
| cornerRadius      | Dp                        | Corner radius of the press mask of a tab                                                 | TabRowDefaults.TabRowCornerRadius | No       |
| itemSpacing       | Dp                        | Minimum spacing between labels; `Dp.Unspecified` = 20dp, or 24dp from a 600dp wide row   | Dp.Unspecified                    | No       |
| horizontalMargin  | Dp                        | Minimum margin at both ends; `Dp.Unspecified` = 16dp, 24dp from 600dp, 40dp from 840dp   | Dp.Unspecified                    | No       |
| contentAlignment  | Alignment                 | Alignment of the label in its tab                                                        | Alignment.Center                  | No       |
| listState         | LazyListState?            | External scroll state for tabs                                                           | null                              | No       |
| interactionSource | MutableInteractionSource? | Interaction source for tab items                                                         | null                              | No       |
| indication        | Indication?               | Press mask of tab items, clipped to `cornerRadius`                                       | LocalIndication.current           | No       |

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
| TabRowHeight                      | Dp   | 48.dp       | Default height of tab row for standard style                      |
| TabRowWithContourHeight           | Dp   | 40.dp       | Default height of tab row for contour style                       |
| TabRowWithContourTinyHeight       | Dp   | 32.dp       | Height of the COUI `SegmentButton.Tiny` style                     |
| TabRowCornerRadius                | Dp   | 8.dp        | Press mask corner radius of a tab for standard style              |
| TabRowWithContourCornerRadius     | Dp   | 16.dp       | Default indicator corner radius (capsule) for contour style       |
| TabRowWithContourTinyCornerRadius | Dp   | 14.dp       | Indicator corner radius of the `SegmentButton.Tiny` style         |
| TabRowWithContourPadding          | Dp   | 4.dp        | Default inset between container and indicator                     |
| TabRowWithContourTinyPadding      | Dp   | 2.dp        | Contour inset of the `SegmentButton.Tiny` style                   |
| TabRowMinWidth                    | Dp   | 0.dp        | Min width of the label slot for standard style                    |
| TabRowWithContourMinWidth         | Dp   | 52.dp       | Min width of tabs for contour style                               |
| TabRowMaxWidth                    | Dp   | Dp.Unspecified | Max width of the label slot for standard style (70% of the row; `Dp.Infinity` removes the limit) |
| TabRowWithContourMaxWidth         | Dp   | Dp.Infinity | Max width of tabs for contour style (unbounded, tabs fill row)    |

#### Methods

| Method Name       | Type         | Description                                                    |
| ----------------- | ------------ | -------------------------------------------------------------- |
| underlineColors() | TabRowColors | Create the default color configuration of `TabRow`             |
| tabRowColors()    | TabRowColors | Create the default color configuration of `TabRowWithContour`  |

`underlineColors()` parameters:

| Parameter Name       | Type  | Description                              | Default Value                            |
| -------------------- | ----- | ---------------------------------------- | ---------------------------------------- |
| trackColor           | Color | Color of the 2dp track                   | `#0F000000` (light) / `#19FFFFFF` (dark) |
| indicatorColor       | Color | Color of the 2dp indicator               | COUITheme.colorScheme.onSurface          |
| contentColor         | Color | Label color of unselected tabs           | COUITheme.colorScheme.onSurfaceSecondary |
| selectedContentColor | Color | Label color of selected and pressed tabs | COUITheme.colorScheme.onSurface          |

### TabRowColors Class

| Property Name           | Type  | Description                        |
| ----------------------- | ----- | ---------------------------------- |
| backgroundColor         | Color | Container color (`TabRowWithContour`) or track color (`TabRow`) |
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
    colors = TabRowDefaults.underlineColors(
        indicatorColor = COUITheme.colorScheme.primary,
        selectedContentColor = COUITheme.colorScheme.primary
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
val scope = rememberCoroutineScope()

Surface {
    Column {
        TabRow(
            tabs = tabs,
            selectedTabIndex = pagerState.targetPage,
            onTabSelected = { scope.launch { pagerState.animateScrollToPage(it) } }
        )
        HorizontalPager(
            state = pagerState,
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
